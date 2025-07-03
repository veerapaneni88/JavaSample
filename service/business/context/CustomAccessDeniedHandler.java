package us.tx.state.dfps.service.business.context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 14, 2017- 11:58:12 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc)
			throws IOException, ServletException {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("errorCode", HttpServletResponse.SC_FORBIDDEN);
		data.put("errorMessage", "Unauthorized");

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		new ObjectMapper().writeValue(response.getOutputStream(), data);

	}
}