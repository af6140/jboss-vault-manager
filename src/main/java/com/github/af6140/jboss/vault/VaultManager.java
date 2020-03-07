package com.github.af6140.jboss.vault;

import lombok.Builder;
import org.jboss.as.security.vault.VaultSession;

@Builder
public class VaultManager {

  private VaultMgmtSession vaultSession;
  private String keystoreURL;
  private String keystorePassword;
  private String encryptionDirectory;
  private String salt;
  @Builder.Default private int iterationCount = 10;
  @Builder.Default private boolean createKeystore = false;

  protected VaultMgmtSession getVaultSession() throws VaultException {
    if (this.vaultSession == null) {
      try {
        vaultSession =
            new VaultMgmtSession(
                keystoreURL,
                keystorePassword,
                encryptionDirectory,
                salt,
                iterationCount,
                createKeystore);

        vaultSession.startVaultSession("vault");
      } catch (Exception e) {
        throw new VaultException("Failed to initialize vault session: " + e.getMessage(), e);
      }
    }
    return vaultSession;
  }

  public void storeSecret(String vaultBlock, String attributeName, String value)
      throws VaultException {
    VaultMgmtSession vaultSession = this.getVaultSession();
    try {
      vaultSession.addSecuredAttributeWithDisplay(vaultBlock, attributeName, value.toCharArray());
    } catch (Exception e) {
      throw new VaultException("Failed to add secret :" + e.getMessage(), e);
    }
  }

  public String getVaultConfiguration() throws VaultException {
    return this.getVaultSession().vaultConfiguration();
  }

  public String retrieveSecret(String vaultBlock, String attribute) throws VaultException {
    try {
      char[] secret = this.getVaultSession().retrieveSecuredAttribute(vaultBlock, attribute);
      return String.valueOf(secret);
    } catch (Exception e) {
      throw new VaultException("Failed to retrieve secret: " + e.getMessage(), e);
    }
  }

  public boolean secretExists(String vaultBlock, String attribute) throws VaultException {
      try {
          return this.getVaultSession().checkSecuredAttribute(vaultBlock, attribute);
      } catch (Exception e) {
          throw new VaultException("Failed to check secret: " + e.getMessage(), e);
      }
  }

  public boolean removeSecret(String vaultBlock, String attribute) throws VaultException {
      try {
          return this.getVaultSession().removeSecuredAttribute(vaultBlock, attribute);
      } catch (Exception e) {
          throw new VaultException("Failed to retrieve secret: " + e.getMessage(), e);
      }
  }
}
