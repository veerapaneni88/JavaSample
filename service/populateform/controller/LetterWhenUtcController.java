package us.tx.state.dfps.service.populateform.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.populateform.service.LetterWhenUtcService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes call
 * to service and sends form data to forms server Jul 5, 2018- 11:36:44 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/utc")
public class LetterWhenUtcController {

	@Autowired
	private LetterWhenUtcService letterWhenUtcService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(LetterWhenUtcController.class);

	/**
	 * Method Name: getUtcLetter Method Description: Makes DAO calls and sends
	 * data to prefill
	 * 
	 * @param request
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getletter", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getUtcLetter(@RequestBody PopulateFormReq request) {
		log.debug("Entering method cinv57s getUtcLetter in LetterWhenUtcController");
		if (TypeConvUtil.isNullOrEmpty(request.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(request.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(request.getFormName())) {
			throw new InvalidRequestException(messageSource.getMessage("common.formname.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(letterWhenUtcService.getUtcLetter(request)));
		//Added for defect 14423 fix. The UserLogonId is stored in the UserId as defined in DocumentsMetaData.xml.
		//Set the UserLogonId with UserId so that it will be used when populating the Snoop_log table.
		request.setUserLogonId(request.getUserId());
		return commonFormRes;
	}

	/**
	 * Method Name: getUtcLetterSpanish Method Description: Makes DAO calls and
	 * sends data to prefill
	 * 
	 * @param request
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getletterspanish", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getUtcLetterSpanish(@RequestBody PopulateFormReq request) {
		log.debug("Entering method cinv57s getUtcLetter in LetterWhenUtcController");
		if (TypeConvUtil.isNullOrEmpty(request.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(request.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(request.getFormName())) {
			throw new InvalidRequestException(messageSource.getMessage("common.formname.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(letterWhenUtcService.getUtcLetter(request)));
		//Added for defect 14423 fix
		request.setUserLogonId(request.getUserId());
		return commonFormRes;
	}

}
