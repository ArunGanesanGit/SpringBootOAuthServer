package arun.auth.server.custom;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * It is a partial Auth header matcher to match the request path and load the filter chain
 */
public class CustomAuthHeaderMatcher implements RequestMatcher {

    private final String expectedHeaderName;
    private final String expectedHeaderValue;

    public CustomAuthHeaderMatcher(String expectedHeaderName, String expectedHeaderValue) {
        this.expectedHeaderName = expectedHeaderName;
        this.expectedHeaderValue = expectedHeaderValue;
    }

    @Override
    public boolean matches(HttpServletRequest httpServletRequest) {
        if (StringUtils.isNotEmpty(this.expectedHeaderName) && StringUtils.isNotEmpty(this.expectedHeaderValue)) {
            String actualHeaderValue = httpServletRequest.getHeader(this.expectedHeaderName);
            if (StringUtils.isNotEmpty(actualHeaderValue)) {
                return actualHeaderValue.toLowerCase().startsWith(expectedHeaderValue.toLowerCase());
            }
        }
        return false;
    }
}
