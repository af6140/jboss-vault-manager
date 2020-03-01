package com.github.af6140.jboss.vault.command;

import com.github.af6140.jboss.vault.VaultManager;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "get-config", mixinStandardHelpOptions = true, description = "Get vault configuration")
public class GetVaultConfig extends AbstractCommand{
  @Override
  public Boolean call() throws Exception {
    VaultManager manager = this.buildVoltManager();
    String configuration = manager.getVaultConfiguration();
    System.out.println(configuration);
    return null;
  }
}
