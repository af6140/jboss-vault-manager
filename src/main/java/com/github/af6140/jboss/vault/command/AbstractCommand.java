package com.github.af6140.jboss.vault.command;

import com.github.af6140.jboss.vault.VaultManager;
import java.util.concurrent.Callable;

import com.github.af6140.jboss.vault.VaultManagerFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine;

@Getter
public abstract class AbstractCommand<V> implements Callable<V> {

  @SuppressWarnings("unused")
  @CommandLine.Option(
      names = {"--keystore", "-k"},
      required = true,
      description = "Vault keystore")
  private String keyStoreURL;

  @SuppressWarnings("unused")
  @CommandLine.Option(
      names = {"--keystorePassword", "-p"},
      required = true,
      description = "Vault keystore password")
  private String keyStorePassword;

  @SuppressWarnings("unused")
  @CommandLine.Option(
      names = {"--vaultDirectory", "-d"},
      required = true,
      description = "Vault directory")
  private String encryptionDirectory;

  @SuppressWarnings("unused")
  @CommandLine.Option(
      names = {"--salt", "-s"},
      required = true,
      defaultValue = "12345678",
      description = "Salt")
  private String salt;

  @SuppressWarnings("unused")
  @CommandLine.Option(
      names = {"--iteration", "-i"},
      required = true,
      defaultValue = "10",
      description = "iteration")
  private int iterationCount;

  @SuppressWarnings("unused")
  @CommandLine.Option(
      names = {"--createKeystore", "-c"},
      required = true,
      defaultValue = "false",
      description = "create keystore if not existing")
  private boolean createKeystore;

  @Autowired private VaultManagerFactory vaultManagerFactory;

  public void setVaultManagerFactory(VaultManagerFactory vaultManagerFactory) {
    this.vaultManagerFactory = vaultManagerFactory;
  }

  protected VaultManager buildVoltManager() {
    return this.vaultManagerFactory.getVaultManager(
        this.keyStoreURL,
        this.iterationCount,
        this.encryptionDirectory,
        this.salt,
        this.keyStorePassword,
        this.createKeystore);
  }

  public void setKeyStoreURL(String keyStoreURL) {
    this.keyStoreURL = keyStoreURL;
  }

  public void setKeyStorePassword(String keyStorePassword) {
    this.keyStorePassword = keyStorePassword;
  }

  public void setEncryptionDirectory(String encryptionDirectory) {
    this.encryptionDirectory = encryptionDirectory;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public void setIterationCount(int iterationCount) {
    this.iterationCount = iterationCount;
  }

  public void setCreateKeystore(boolean createKeystore) {
    this.createKeystore = createKeystore;
  }

  public abstract V call() throws Exception;
}
