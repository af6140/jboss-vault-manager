package com.github.af6140.jboss.vault.command;

import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(
    name = "vault",
    mixinStandardHelpOptions = true,
    description = "Manage jboss vault and secrets",
    subcommands = {StoreSecret.class, RetrieveSecret.class, RemoveSecret.class, CheckSecret.class})
public class Vault implements Callable {

  @SuppressWarnings("unused")
  @CommandLine.Parameters(index = "0", description = "command: set, get, remove, check")
  private String subCommand;

  @Override
  public Object call() throws Exception {
    return null;
  }
}
