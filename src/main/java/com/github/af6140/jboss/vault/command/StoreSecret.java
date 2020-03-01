package com.github.af6140.jboss.vault.command;

import com.github.af6140.jboss.vault.VaultManager;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "set", mixinStandardHelpOptions = true)
public class StoreSecret extends AbstractCommand {
  @CommandLine.Option(
      names = {"-b", "--block"},
      required = true,
      description = "Vault block")
  private String vaultBlock;

  @CommandLine.Option(
      names = {"-n", "--name"},
      required = true,
      description = "Secret name")
  private String secretName;

  @CommandLine.Option(
      names = {"-e", "--secret"},
      required = true,
      description = "Secret value")
  private String secretVaule;

  @Override
  public Object call() throws Exception {
    VaultManager manager = this.buildVoltManager();
    manager.storeSecret(this.vaultBlock, this.secretName, this.secretVaule);
    return null;
  }
}
