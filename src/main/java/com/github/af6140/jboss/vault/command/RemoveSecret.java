package com.github.af6140.jboss.vault.command;

import com.github.af6140.jboss.vault.VaultManager;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "remove", mixinStandardHelpOptions = true)
public class RemoveSecret extends AbstractCommand {
  @SuppressWarnings("unused")
  @CommandLine.Option(
      names = {"-b", "--block"},
      required = true,
      description = "Vault block")
  private String vaultBlock;

  @SuppressWarnings("unused")
  @CommandLine.Option(
      names = {"-n", "--name"},
      required = true,
      description = "Secret name")
  private String secretName;

  @Override
  public Boolean call() throws Exception {
    VaultManager manager = this.buildVoltManager();
    boolean removed = manager.removeSecret(this.vaultBlock, this.secretName);
    return removed;
  }
}
