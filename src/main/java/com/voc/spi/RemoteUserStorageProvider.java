package com.voc.spi;

import com.voc.dto.UserSPIResponse;
import com.voc.dto.VerifyPasswordResponse;
import com.voc.service.UserService;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

public class RemoteUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {
    private final KeycloakSession session;
    private final ComponentModel model;
    private final UserService usersService;

    public RemoteUserStorageProvider(KeycloakSession session, ComponentModel model, UserService usersService) {
        this.session = session;
        this.model = model;
        this.usersService = usersService;
    }

    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(username, realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        UserModel returnValue = null;
        UserSPIResponse user = usersService.getUserDetails(username);
        if (user != null) {
            returnValue = createUserModel(username, realm, user.getPassword());
        }
        return returnValue;
    }

    private UserModel createUserModel(String username, RealmModel realm, String password) {
        return new AbstractUserAdapter(session, realm, model) {
            @Override
            public String getUsername() {
                return username;
            }
        };
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType)) {
            return false;
        }
        return !getCredentialStore().getStoredCredentialsByType(realm, user, credentialType).isEmpty();
    }

    private UserCredentialStore getCredentialStore() {
        return session.userCredentialManager();
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        VerifyPasswordResponse verifyPasswordResponse = usersService.verifyUserPassword(user.getUsername(), credentialInput.getChallengeResponse());
        if (verifyPasswordResponse == null) return false;
        return verifyPasswordResponse.isResult();
    }

}
