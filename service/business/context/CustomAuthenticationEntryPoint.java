package us.tx.state.dfps.service.business.context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 14, 2017- 11:55:14 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/**
	 * 
	 */
	public CustomAuthenticationEntryPoint() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.web.AuthenticationEntryPoint#commence(javax.
	 * servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException paramAuthenticationException) throws IOException, ServletException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("errorCode", HttpServletResponse.SC_FORBIDDEN);
		data.put("errorMessage", "Unauthorized");

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		new ObjectMapper().writeValue(response.getOutputStream(), data);

	}

}
