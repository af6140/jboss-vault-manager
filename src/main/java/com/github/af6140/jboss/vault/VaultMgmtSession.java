package com.github.af6140.jboss.vault;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.jboss.as.security.logging.SecurityLogger;
import org.jboss.security.Util;
import org.jboss.security.plugins.PBEUtils;
import org.jboss.security.vault.SecurityVault;
import org.jboss.security.vault.SecurityVaultException;
import org.jboss.security.vault.SecurityVaultFactory;

public class VaultMgmtSession {
  public static final String VAULT_ENC_ALGORITHM = "PBEwithMD5andDES";

  static final Charset CHARSET;
  private String keystoreURL;
  private String keystorePassword;
  private String keystoreMaskedPassword;
  private String encryptionDirectory;
  private String salt;
  private int iterationCount;
  private boolean createKeystore;
  private SecurityVault vault;
  private String vaultAlias;

  public VaultMgmtSession(
      String keystoreURL,
      String keystorePassword,
      String encryptionDirectory,
      String salt,
      int iterationCount)
      throws Exception {
    this(keystoreURL, keystorePassword, encryptionDirectory, salt, iterationCount, false);
  }

  public VaultMgmtSession(
      String keystoreURL,
      String keystorePassword,
      String encryptionDirectory,
      String salt,
      int iterationCount,
      boolean createKeystore)
      throws Exception {
    this.keystoreURL = keystoreURL;
    this.keystorePassword = keystorePassword;
    this.encryptionDirectory = encryptionDirectory;
    this.salt = salt;
    this.iterationCount = iterationCount;
    this.createKeystore = createKeystore;
    this.validate();
  }

  private void validate() throws Exception {
    this.validateKeystoreURL();
    this.validateEncryptionDirectory();
    this.validateSalt();
    this.validateIterationCount();
    this.validateKeystorePassword();
  }

  protected void validateKeystoreURL() throws Exception {
    File f = new File(this.keystoreURL);
    if (!f.exists()) {
      if (!this.createKeystore) {
        throw SecurityLogger.ROOT_LOGGER.keyStoreDoesnotExistWithExample(
            this.keystoreURL, this.keystoreURL);
      }
    } else if (!f.canWrite() || !f.isFile()) {
      throw SecurityLogger.ROOT_LOGGER.keyStoreNotWritable(this.keystoreURL);
    }
  }

  protected void validateKeystorePassword() throws Exception {
    if (this.keystorePassword == null) {
      throw SecurityLogger.ROOT_LOGGER.keyStorePasswordNotSpecified();
    }
  }

  protected void validateEncryptionDirectory() throws Exception {
    if (this.encryptionDirectory == null) {
      throw new Exception("Encryption directory has to be specified.");
    } else {
      if (!this.encryptionDirectory.endsWith("/") || this.encryptionDirectory.endsWith("\\")) {
        this.encryptionDirectory = this.encryptionDirectory + "/";
      }

      File d = new File(this.encryptionDirectory);
      if (!d.exists() && !d.mkdirs()) {
        throw SecurityLogger.ROOT_LOGGER.cannotCreateEncryptionDirectory(d.getAbsolutePath());
      } else if (!d.isDirectory()) {
        throw SecurityLogger.ROOT_LOGGER.encryptionDirectoryDoesNotExist(this.encryptionDirectory);
      }
    }
  }

  protected void validateIterationCount() throws Exception {
    if (this.iterationCount < 1 && this.iterationCount > 2147483647) {
      throw SecurityLogger.ROOT_LOGGER.iterationCountOutOfRange(
          String.valueOf(this.iterationCount));
    }
  }

  protected void validateSalt() throws Exception {
    if (this.salt == null || this.salt.length() != 8) {
      throw SecurityLogger.ROOT_LOGGER.saltWrongLength();
    }
  }

  private String computeMaskedPassword() throws Exception {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEwithMD5andDES");
    char[] password = "somearbitrarycrazystringthatdoesnotmatter".toCharArray();
    PBEParameterSpec cipherSpec = new PBEParameterSpec(this.salt.getBytes(), this.iterationCount);
    PBEKeySpec keySpec = new PBEKeySpec(password);
    SecretKey cipherKey = factory.generateSecret(keySpec);
    String maskedPass =
        PBEUtils.encode64(
            this.keystorePassword.getBytes(), "PBEwithMD5andDES", cipherKey, cipherSpec);
    return "MASK-" + maskedPass;
  }

  private void initSecurityVault() throws Exception {
    try {
      this.vault = SecurityVaultFactory.get();
      this.vault.init(this.getVaultOptionsMap());
      this.handshake();
    } catch (SecurityVaultException var2) {
      throw SecurityLogger.ROOT_LOGGER.securityVaultException(var2);
    }
  }

  public void startVaultSession(String vaultAlias) throws Exception {
    if (vaultAlias == null) {
      throw SecurityLogger.ROOT_LOGGER.vaultAliasNotSpecified();
    } else {
      this.keystoreMaskedPassword =
          this.keystorePassword.startsWith("MASK-") || Util.isPasswordCommand(this.keystorePassword)
              ? this.keystorePassword
              : this.computeMaskedPassword();
      this.vaultAlias = vaultAlias;
      this.initSecurityVault();
    }
  }

  private Map<String, Object> getVaultOptionsMap() {
    Map<String, Object> options = new HashMap();
    options.put("KEYSTORE_URL", this.keystoreURL);
    options.put("KEYSTORE_PASSWORD", this.keystoreMaskedPassword);
    options.put("KEYSTORE_ALIAS", this.vaultAlias);
    options.put("SALT", this.salt);
    options.put("ITERATION_COUNT", Integer.toString(this.iterationCount));
    options.put("ENC_FILE_DIR", this.encryptionDirectory);
    if (this.createKeystore && !(new File(this.keystoreURL)).exists()) {
      options.put("CREATE_KEYSTORE", Boolean.toString(this.createKeystore));
    }

    return options;
  }

  private void handshake() throws SecurityVaultException {
    Map<String, Object> handshakeOptions = new HashMap();
    handshakeOptions.put("PUBLIC_CERT", this.vaultAlias);
    this.vault.handshake(handshakeOptions);
  }

  public String addSecuredAttribute(String vaultBlock, String attributeName, char[] attributeValue)
      throws Exception {
    this.vault.store(vaultBlock, attributeName, attributeValue, (byte[]) null);
    return this.securedAttributeConfigurationString(vaultBlock, attributeName);
  }

  public void addSecuredAttributeWithDisplay(
      String vaultBlock, String attributeName, char[] attributeValue) throws Exception {
    this.vault.store(vaultBlock, attributeName, attributeValue, (byte[]) null);
    this.attributeCreatedDisplay(vaultBlock, attributeName);
  }

  public boolean checkSecuredAttribute(String vaultBlock, String attributeName) throws Exception {
    return this.vault.exists(vaultBlock, attributeName);
  }

  public boolean removeSecuredAttribute(String vaultBlock, String attributeName) throws Exception {
    return this.vault.remove(vaultBlock, attributeName, (byte[]) null);
  }

  public char[] retrieveSecuredAttribute(String vaultBlock, String attributeName) throws Exception {
    return this.vault.retrieve(vaultBlock, attributeName, (byte[]) null);
  }

  private void attributeCreatedDisplay(String vaultBlock, String attributeName) {
    System.out.println(
        SecurityLogger.ROOT_LOGGER.vaultAttributeCreateDisplay(
            vaultBlock,
            attributeName,
            this.securedAttributeConfigurationString(vaultBlock, attributeName)));
  }

  private String securedAttributeConfigurationString(String vaultBlock, String attributeName) {
    return "VAULT::" + vaultBlock + "::" + attributeName + "::1";
  }

  public void vaultConfigurationDisplay() {
    String configuration = this.vaultConfiguration();
    System.out.println(SecurityLogger.ROOT_LOGGER.vaultConfigurationTitle());
    System.out.println("********************************************");
    System.out.println("For standalone mode:");
    System.out.println(configuration);
    System.out.println("********************************************");
    System.out.println("For domain mode:");
    System.out.println("/host=the_host" + configuration);
    System.out.println("********************************************");
  }

  public String vaultConfiguration() {
    StringBuilder sb = new StringBuilder();
    sb.append("/core-service=vault:add(vault-options=[");
    sb.append("(\"KEYSTORE_URL\" => \"").append(this.keystoreURL).append("\")").append(",");
    sb.append("(\"KEYSTORE_PASSWORD\" => \"")
        .append(this.keystoreMaskedPassword)
        .append("\")")
        .append(",");
    sb.append("(\"KEYSTORE_ALIAS\" => \"").append(this.vaultAlias).append("\")").append(",");
    sb.append("(\"SALT\" => \"").append(this.salt).append("\")").append(",");
    sb.append("(\"ITERATION_COUNT\" => \"").append(this.iterationCount).append("\")").append(",");
    sb.append("(\"ENC_FILE_DIR\" => \"").append(this.encryptionDirectory).append("\")");
    sb.append("])");
    return sb.toString();
  }

  public String getKeystoreMaskedPassword() {
    return this.keystoreMaskedPassword;
  }

  static String blockAttributeDisplayFormat(String vaultBlock, String attributeName) {
    return "[" + vaultBlock + "::" + attributeName + "]";
  }

  static {
    CHARSET = StandardCharsets.UTF_8;
  }
}
