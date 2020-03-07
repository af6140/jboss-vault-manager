package com.github.af6140.jboss.vault;

import org.springframework.stereotype.Component;

@Component
public class VaultManagerFactory {
    public  VaultManager  getVaultManager(
            String keyStoreURL,
            int iterationCount,
            String encryptionDirectory,
            String salt,
            String keyStorePassword,
            boolean createKeystore

    ) {
        return VaultManager.builder()
                .keystoreURL(keyStoreURL)
                .iterationCount(iterationCount)
                .encryptionDirectory(encryptionDirectory)
                .salt(salt)
                .createKeystore(createKeystore)
                .keystorePassword(keyStorePassword)
                .build();
    }

}
