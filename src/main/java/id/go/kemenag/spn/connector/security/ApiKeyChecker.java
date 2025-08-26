package id.go.kemenag.spn.connector.security;

import id.go.kemenag.spn.constant.ApplicationConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component("apiKeyChecker")
public class ApiKeyChecker {

    public boolean isValid() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }

        HttpServletRequest request = attributes.getRequest();

        Boolean apiKeyValid = (Boolean) request.getAttribute(ApplicationConstant.API_KEY_VALID_ATTRIBUTE);
        return Boolean.TRUE.equals(apiKeyValid);
    }
}
