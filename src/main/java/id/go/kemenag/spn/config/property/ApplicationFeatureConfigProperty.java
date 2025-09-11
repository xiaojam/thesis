package id.go.kemenag.spn.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "setting.feature.config")
public class ApplicationFeatureConfigProperty {

    private boolean featAllowSubProcess;
}
