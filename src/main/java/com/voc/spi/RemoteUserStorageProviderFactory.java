package com.voc.spi;

import com.voc.service.UserService;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class RemoteUserStorageProviderFactory implements UserStorageProviderFactory<RemoteUserStorageProvider> {
    public static final String PROVIDER_NAME = "voc-user-storage-provider";

    @Override
    public RemoteUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new RemoteUserStorageProvider(session, model, new UserService());
    }

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }

}
