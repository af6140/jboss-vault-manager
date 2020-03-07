package com.github.af6140.jboss.vault.command;

import com.github.af6140.jboss.vault.VaultManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;

@Component
@CommandLine.Command(name = "set", mixinStandardHelpOptions = true, description = "Store secret")
public class StoreSecret extends AbstractCommand {

  @Getter
  @Setter
  static class IndividualDependent {
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

    @SuppressWarnings("unused")
    @CommandLine.Option(
        names = {"-e", "--secret"},
        required = true,
        description = "Secret value")
    private String secretValue;
  }

  @Getter
  @Setter
  static class BatchDependent {
    @CommandLine.Option(
        names = {"-i", "--input"},
        required = true)
    protected File inputCsv;
  }

  @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
  Composite composite;

  static class Composite {
    @CommandLine.ArgGroup(exclusive = false, multiplicity = "0..1")
    IndividualDependent dependent;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "0..1")
    BatchDependent batchDependent;
  }

  @Override
  public Object call() throws Exception {
    VaultManager manager = this.buildVoltManager();
    if (this.composite.dependent!=null) {
      System.out.println("Processing record ... " + this.composite.dependent.vaultBlock + "::" + this.composite.dependent.secretName);
      manager.storeSecret(this.composite.dependent.vaultBlock, this.composite.dependent.secretName,
              this.composite.dependent.secretValue);
    }else {
      File f = this.composite.batchDependent.inputCsv;
      Reader reader = Files.newBufferedReader(f.toPath());
      Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(reader);
      for (CSVRecord record : records) {
        String vaultBlock = record.get(0);
        String secretName = record.get(1);
        String secretValue = record.get(2);
        System.out.println("Processing record ... " + vaultBlock + "::" + secretName);
        manager.storeSecret(vaultBlock, secretName,secretValue);
      }
    }
    return null;
  }
}
