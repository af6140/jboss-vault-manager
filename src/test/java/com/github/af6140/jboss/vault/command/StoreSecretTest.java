package com.github.af6140.jboss.vault.command;

import com.github.af6140.jboss.vault.VaultManager;
import com.github.af6140.jboss.vault.VaultManagerFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StoreSecretTest {

  @Mock private VaultManagerFactory vaultManagerFactory;

  @Mock private VaultManager vaultManager;

  @InjectMocks private StoreSecret storeSecret;

  @Before
  public void setUp() {
    StoreSecret.Composite composite = new StoreSecret.Composite();
    composite.batchDependent = new StoreSecret.BatchDependent();
    composite.batchDependent.inputCsv = new File("./target/test-classes/secrets.csv");
    storeSecret = new StoreSecret();
    storeSecret.composite = composite;
    storeSecret.setSalt("salt");
    storeSecret.setCreateKeystore(true);
    storeSecret.setEncryptionDirectory("./vault");
    storeSecret.setIterationCount(10);
    storeSecret.setKeyStoreURL("./identity.jks");
    storeSecret.setKeyStorePassword("pass");
    storeSecret.setVaultManagerFactory(vaultManagerFactory);
  }

  @Test
  public void testCallWithInputFile() throws Exception {
    StoreSecret.Composite composite = new StoreSecret.Composite();
    composite.batchDependent = new StoreSecret.BatchDependent();
    composite.batchDependent.setInputCsv(new File("./target/test-classes/secrets.csv"));
    storeSecret.composite = composite;
    when(vaultManagerFactory.getVaultManager("./identity.jks", 10, "./vault", "salt", "pass", true))
        .thenReturn(vaultManager);
    storeSecret.call();
    verify(vaultManager, times(3))
        .storeSecret(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString());
  }

  @Test
  public void testCallWithCommandLineInput() throws Exception {
    StoreSecret.Composite composite = new StoreSecret.Composite();
    composite.dependent = new StoreSecret.IndividualDependent();
    composite.dependent.setSecretName("name1");
    composite.dependent.setSecretValue("value1");
    composite.dependent.setVaultBlock("block1");
    storeSecret.composite = composite;
    when(vaultManagerFactory.getVaultManager("./identity.jks", 10, "./vault", "salt", "pass", true))
        .thenReturn(vaultManager);
    storeSecret.call();
    verify(vaultManager, times(1)).storeSecret("block1", "name1", "value1");
  }
}
