package com.github.af6140.jboss.vault;

import lombok.Builder;
import org.jboss.as.security.vault.VaultSession;

@Builder
public class VaultManager {

    private VaultSession vaultSession;
    private String keystoreURL;
    private String keystorePassword;
    private String encryptionDirectory;
    private String salt;
    @Builder.Default private int iterationCount = 10;
    @Builder.Default private boolean createKeystore = false;

    protected VaultSession getVaultSession() throws Exception {
        if(this.vaultSession == null) {
            vaultSession = new VaultSession(
                    keystoreURL, keystorePassword,
                    encryptionDirectory, salt, iterationCount, createKeystore);
            vaultSession.startVaultSession("vault");
        }
        return vaultSession;
    }
    public void storeSecret(String vaultBlock, String attributeName, String value) throws Exception {
        VaultSession vaultSession = this.getVaultSession();
        vaultSession.addSecuredAttributeWithDisplay(vaultBlock, attributeName, value.toCharArray());
    }

    public String getVaultConfiguration() throws Exception{
        return this.getVaultSession().vaultConfiguration();
    }

    public String retrieveSecret(String vaultBlock, String attribute) throws Exception{
        char[] secret = this.getVaultSession().retrieveSecuredAttribute(vaultBlock, attribute);
        return String.valueOf(secret);
    }

    public boolean secretExists(String vaultBlock, String attribute) throws Exception {
        return this.getVaultSession().checkSecuredAttribute(vaultBlock, attribute);
    }

    public boolean removeSecret(String vaultBlock, String attribute) throws Exception {
        return this.getVaultSession().removeSecuredAttribute(vaultBlock, attribute);
    }

}
