package com.github.af6140.jboss.vault;

import com.github.af6140.jboss.vault.command.StoreSecret;
import com.github.af6140.jboss.vault.command.Vault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@SuppressWarnings("unused")
public class VaultManagerRunner implements CommandLineRunner, ExitCodeGenerator {

    private final CommandLine.IFactory factory; // auto-configured to inject PicocliSpringFactory
    private final Vault vault;
    private int exitCode;

    public VaultManagerRunner(Vault vault, CommandLine.IFactory factory) {
        this.factory = factory;
        this.vault= vault;
    }

    @Override
    public void run(String... args) {
        exitCode = new CommandLine(vault, factory)
                .execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
