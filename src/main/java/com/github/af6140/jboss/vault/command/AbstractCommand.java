package com.github.af6140.jboss.vault.command;

import com.github.af6140.jboss.vault.VaultManager;
import java.util.concurrent.Callable;
import lombok.Getter;
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

  protected VaultManager buildVoltManager() {
    return VaultManager.builder()
        .keystoreURL(this.keyStoreURL)
        .iterationCount(this.iterationCount)
        .encryptionDirectory(this.encryptionDirectory)
        .salt(this.salt)
        .createKeystore(this.createKeystore)
        .keystorePassword("MASK-" + this.keyStorePassword)
        .build();
  }

  public abstract V call() throws Exception;
}
