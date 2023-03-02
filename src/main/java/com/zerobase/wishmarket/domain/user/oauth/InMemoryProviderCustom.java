package com.zerobase.wishmarket.domain.user.oauth;

import java.util.HashMap;
import java.util.Map;

public class InMemoryProviderCustom {
    private final Map<String, OauthProvider> providers;

    public InMemoryProviderCustom(Map<String, OauthProvider> providers) {
        this.providers = new HashMap<>(providers);
    }

    public OauthProvider findByProviderName(String name) {
        return providers.get(name);
    }
}
