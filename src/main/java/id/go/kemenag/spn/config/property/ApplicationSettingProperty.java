package id.go.kemenag.spn.config.property;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "setting")
public class ApplicationSettingProperty {

    private Security security;

    @Data
    public static class Security {

        private BaseSecurity application;

        private BaseSecurity auth;

        private Long ttl;

        private Long refreshTtl;

        @Data
        public static class BaseSecurity {

            @NotBlank
            private String key;

            @NotBlank
            private String value;
        }
    }

    private Feature feature;

    @Data
    public static class Feature {

        private Config config;

        @Data
        public static class Config {

            private boolean featSkipApproach;
            private boolean featReprocessApproach;
        }
    }

    public String getAppKey() {
        return security.getApplication().getKey();
    }

    public String getAppSecret() {
        return security.getApplication().getValue();
    }

    public String getJwtSecret() {
        return security.getAuth().getValue();
    }

    public Long getTtl() {
        return security.getTtl();
    }

    public Long getRefreshTtl() {
        return security.getRefreshTtl();
    }
}
