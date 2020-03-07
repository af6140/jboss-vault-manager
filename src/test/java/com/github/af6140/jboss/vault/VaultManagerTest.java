package com.github.af6140.jboss.vault;

import org.apache.commons.io.FileUtils;
import org.jboss.as.security.vault.VaultSession;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class VaultManagerTest {
    private VaultManager vaultManager;
    private String defaultVaultBlock;

    @Before
    public void setUp() {
        defaultVaultBlock="com.github.af6140.jboss";
        String testKeyStore = "./target/test-vault.keystore";
        String testVaultDir = "./target/test-vault";
        FileUtils.deleteQuietly(new File(testKeyStore));
        try {
            FileUtils.deleteDirectory(new File(testVaultDir));
        }catch (IOException e) {
            System.out.println(e.getCause());
        }
        vaultManager = VaultManager.builder()
                .keystoreURL(testKeyStore)
                .iterationCount(10)
                .encryptionDirectory(testVaultDir)
                .salt("12345678")
                .createKeystore(true)
                .keystorePassword("12345678")
                .build();

    }

    @Test
    public void testGetVaultSession() throws Exception {
        VaultMgmtSession session = vaultManager.getVaultSession();
        assert session!=null;
    }
    @Test
    public void testStoreSecret() throws Exception{
        this.vaultManager.storeSecret(defaultVaultBlock, "password", "test2");
    }

    @Test
    public void testGetVaultConfiguration() throws Exception {
        System.out.println(this.vaultManager.getVaultConfiguration());
    }

    @Test
    public void testRetrieveSecret() throws  Exception {
        String expected = "haveagoodday";
        this.vaultManager.storeSecret(defaultVaultBlock, "testretrieve", expected);
        String output = this.vaultManager.retrieveSecret(defaultVaultBlock, "testretrieve");
        System.out.println(output);
    }

    @Test
    public void testSecretExits() throws  Exception {
        String expectedAttribute = "haveagoodday";
        this.vaultManager.storeSecret(defaultVaultBlock, expectedAttribute, "dummy");
        assert this.vaultManager.secretExists(defaultVaultBlock,expectedAttribute);
        assert !this.vaultManager.secretExists(defaultVaultBlock,expectedAttribute+"noway");

    }

    @Test
    public void testRemoveSecret() throws Exception{
        String expectedAttribute = "toberemoved";
        this.vaultManager.storeSecret(defaultVaultBlock, expectedAttribute, "dummy");
        assert this.vaultManager.removeSecret(defaultVaultBlock,expectedAttribute);
        assert !this.vaultManager.secretExists(defaultVaultBlock,expectedAttribute);
    }
}
