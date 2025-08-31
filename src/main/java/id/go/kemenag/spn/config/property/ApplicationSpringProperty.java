package id.go.kemenag.spn.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring")
public class ApplicationSpringProperty {

    private Dataz data;

    @Data
    public static class Dataz {

        private Redisz redis;

        @Data
        public static class Redisz {

            private String host;

            private int port;

            private String password;
        }
    }

    public String getRedisHost() {
        return data.getRedis().getHost();
    }

    public int getRedisPort() {
        return data.getRedis().getPort();
    }

    public String getRedisPassword() {
        return data.getRedis().getPassword();
    }
}
