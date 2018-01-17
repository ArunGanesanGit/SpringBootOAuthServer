package arun.auth.server.custom;

import lombok.Getter;

/**
 * Enum to maintain different Auth Providers
 */
public enum AuthProviderEnum {

    CUSTOM("Custom"),
    FACEBOOK("Facebook"),
    GITHUB("Github"),
    GOOGLE("Google");

    @Getter
    private final String name;

    AuthProviderEnum(String name) {
        this.name = name;
    }

}
