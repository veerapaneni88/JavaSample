package us.tx.state.dfps.service.business.context;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import us.tx.state.dfps.common.dto.CryptData;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 19, 2017- 12:20:56 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public class SSORequestHeaderAuthenticationFilter extends RequestHeaderAuthenticationFilter {

	private boolean allowPreAuthenticatedPrincipals = true;

	public SSORequestHeaderAuthenticationFilter() {
		super();
		this.setPrincipalRequestHeader(ServiceConstants.SERVICE_HEADER);
		this.setExceptionIfHeaderMissing(false);
	}

	/**
	 * This is called when a request is made, the returned object identifies the
	 * user and will either be {@literal null} or a String. This method will
	 * throw an exception if exceptionIfHeaderMissing is set to true (default)
	 * and the required header is missing.
	 *
	 * @param request
	 *            {@link HttpServletRequest}
	 */
	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		String userName = (String) (super.getPreAuthenticatedPrincipal(request));
		if (userName == null || "".trim().equals(userName)) {
			return userName;
		}
		userName = new CryptData().decrypt(ServiceConstants.SECURITY_KEY, userName);
		return userName;
	}

	public boolean isAllowPreAuthenticatedPrincipals() {
		return allowPreAuthenticatedPrincipals;
	}

}
