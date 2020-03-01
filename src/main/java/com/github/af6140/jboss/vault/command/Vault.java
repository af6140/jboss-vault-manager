package com.github.af6140.jboss.vault.command;

import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@Component
@CommandLine.Command(name = "vault",mixinStandardHelpOptions = true,
subcommands = {
        StoreSecret.class,
        RetrieveSecret.class,
        RemoveSecret.class,
        CheckSecret.class
})
public class Vault implements Callable {

    @CommandLine.Parameters(index = "0", description = "main command")
    private String mainCommand;
    @Override
    public Object call() throws Exception {
        return null;
    }
}