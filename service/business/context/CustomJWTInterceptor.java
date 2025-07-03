package us.tx.state.dfps.service.business.context;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import us.tx.state.dfps.service.common.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomJWTInterceptor implements HandlerInterceptor {
    Logger log = Logger.getLogger(CustomJWTInterceptor.class);

    private static final String USER_ID_PERSON = "userIdPerson";

    /**
     * This method intercepts all the requests and if it has a bearer_token it will decode and store it in security context.
     * Since spring security is not enabled for service-business, this method is manually intercepting for the JWT token.
     * This can be deleted once the spring security is enabled.
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("[preHandle][" + request + "]" + "[" + request.getMethod()
                + "]" + request.getRequestURI());
        String userIdPerson = request.getHeader(USER_ID_PERSON);
        if (StringUtil.isValid(userIdPerson)) {
            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(userIdPerson, null);
            SecurityContextHolder.getContext().setAuthentication(userToken);
        }
        return true;
    }

}
