package us.tx.state.dfps.service.arsafetyassmt.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.arsafetyassmt.service.ARSafetyAssmtService;
import us.tx.state.dfps.service.common.request.ARClosureSafetyAssmtReq;
import us.tx.state.dfps.service.common.response.ARClosureSafetyAssmtRes;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service
 * Name:ARSafetyAssmtValueBean List, ARSafetyAssmt Details Class
 * Description:ARSafetyAssmtController class will have all operation which are
 * related to case and relevant page to case.
 * 
 */

@RestController
@RequestMapping("/arSafetyAssmt")
public class ARSafetyAssmtController {

	@Autowired
	ARSafetyAssmtService arSafetyAssmtService;

	@Autowired
	MessageSource messageSource;

	/**
	 * Fetches the ARSafety Assessment data from the database.
	 * 
	 * @param ARClosureSafetyAssmtReq
	 * @return ARClosureSafetyAssmtRes
	 */
	@RequestMapping(value = "/getARSafetyAssmt", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ARClosureSafetyAssmtRes getARSafetyAssmt(
			@RequestBody ARClosureSafetyAssmtReq arSafetyAssmtReq) {

		if (TypeConvUtil.isNullOrEmpty(arSafetyAssmtReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("arsafetyassmt.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(arSafetyAssmtReq.getIndAssmtType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("arsafetyassmt.assmttype.mandatory", null, Locale.US));
		}

		return arSafetyAssmtService.getARSafetyAssmt(arSafetyAssmtReq);

	}

	/**
	 * Fetches the ARSafetyAssmtFactor data from the database.
	 * 
	 * @param arSafetyAssmtReq
	 * @return
	 */
	@RequestMapping(value = "/getARSafetyAssmtFactor", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ARClosureSafetyAssmtRes getARSafetyAssmtFactor(
			@RequestBody ARClosureSafetyAssmtReq arSafetyAssmtReq) {

		if (TypeConvUtil.isNullOrEmpty(arSafetyAssmtReq.getIdFactorInitial())) {
			throw new InvalidRequestException(
					messageSource.getMessage("arsafetyassmt.factorid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(arSafetyAssmtReq.getIdArSafetyAssmt())) {
			throw new InvalidRequestException(
					messageSource.getMessage("arsafetyassmt.arsafetyassmtid.mandatory", null, Locale.US));
		}

		return arSafetyAssmtService.getARSafetyAssmtFactor(arSafetyAssmtReq.getIdFactorInitial(),
				arSafetyAssmtReq.getIdArSafetyAssmt());

	}

}
