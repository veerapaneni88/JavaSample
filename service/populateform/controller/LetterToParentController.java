package us.tx.state.dfps.service.populateform.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.populateform.service.LetterToParentService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Receives
 * input data from forms and calls service to obtain data to be sent back to
 * form May 29, 2018- 1:32:21 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("/parentletter")
public class LetterToParentController {

	@Autowired
	private LetterToParentService letterToParentService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(LetterToParentController.class);

	/**
	 * Method Name: getNotification Method Description: Gets the prefill string
	 * for the CFIV3000/CFIV3100 forms
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 * 
	 */
	@RequestMapping(value = "/getletter", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getParentLetter(@RequestBody PopulateFormReq request) {
		log.debug("Entering method cinv32s getParentLetter in LetterToParentController");
		if (TypeConvUtil.isNullOrEmpty(request.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(request.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(letterToParentService.getParentLetter(request)));

		return commonFormRes;
	}

}
