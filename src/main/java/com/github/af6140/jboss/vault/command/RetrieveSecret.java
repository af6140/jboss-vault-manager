package com.github.af6140.jboss.vault.command;

import com.github.af6140.jboss.vault.VaultManager;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "get", mixinStandardHelpOptions = true)
public class RetrieveSecret extends AbstractCommand {
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
  public String call() throws Exception {
    VaultManager manager = this.buildVoltManager();
    String secret = manager.retrieveSecret(this.vaultBlock, this.secretName);
    System.out.println("The secret retrieved is :" + secret);
    return secret;
  }
}
