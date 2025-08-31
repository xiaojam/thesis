package id.go.kemenag.spn.config.filter;

import id.go.kemenag.spn.config.property.ApplicationSettingProperty;
import id.go.kemenag.spn.constant.ApplicationConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class InternalSecurityFilter extends OncePerRequestFilter {

    private final ApplicationSettingProperty applicationSettingProperty;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        var apiKeyHeaderName = applicationSettingProperty.getAppKey();
        var expectedApiSecret = applicationSettingProperty.getAppSecret();
        var providedKeySecret = request.getHeader(apiKeyHeaderName);

        if (providedKeySecret != null) {
            if (expectedApiSecret != null && expectedApiSecret.equals(providedKeySecret)) {
                request.setAttribute(ApplicationConstant.API_KEY_VALID_ATTRIBUTE, Boolean.TRUE);
            } else {response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid API Secret");
                return;
            }
        } else {request.setAttribute(ApplicationConstant.API_KEY_VALID_ATTRIBUTE, Boolean.FALSE);
            request.setAttribute(ApplicationConstant.API_KEY_VALID_ATTRIBUTE, Boolean.FALSE);
        }

        filterChain.doFilter(request, response);
    }
}
