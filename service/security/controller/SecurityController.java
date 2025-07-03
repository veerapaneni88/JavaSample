package us.tx.state.dfps.service.security.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.security.dto.AppReqDto;
import us.tx.state.dfps.service.security.request.SSOTokenReq;
import us.tx.state.dfps.service.security.response.SSOTokenRes;
import us.tx.state.dfps.service.security.service.SecurityService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Controller
 * class for SSO token generation Dec 21, 2017- 4:18:56 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/security")
public class SecurityController {

	@Autowired
	SecurityService securityService;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: createSsoToken Method Description: This method creates a
	 * Token for SSO.
	 * 
	 * @param ssoTokenReq
	 * @return ssoTokenRes
	 */
	@RequestMapping(value = "/createSsoToken", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SSOTokenRes createSsoToken(@RequestBody SSOTokenReq ssoTokenReq){
		if (TypeConvUtil.isNullOrEmpty(ssoTokenReq.getEmpUserName())
				|| TypeConvUtil.isNullOrEmpty(ssoTokenReq.getSourceApplication())
				|| TypeConvUtil.isNullOrEmpty(ssoTokenReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("token.request.invalid", null, Locale.US));
		}
		AppReqDto appReqDto = securityService.insertToken(ssoTokenReq);
		SSOTokenRes ssoTokenRes = new SSOTokenRes();
		if (appReqDto != null) {
			ssoTokenRes.setTokenValue(appReqDto.getTokenValue());
			ssoTokenRes.setStatus(appReqDto.getStatus());
			ssoTokenRes.setAppRequestId(appReqDto.getIdAppRequest());
		}
		return ssoTokenRes;
	}
}
