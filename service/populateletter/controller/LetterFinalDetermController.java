package us.tx.state.dfps.service.populateletter.controller;

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
import us.tx.state.dfps.service.populateletter.service.LetterFinalDetermService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes call
 * to service and sends form data to forms server Jul 6, 2018- 2:36:02 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("letterreporter")
public class LetterFinalDetermController {

	@Autowired
	private LetterFinalDetermService letterFinalDetermService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(LetterFinalDetermController.class);

	/**
	 * Method Name: getLetter Method Description: Makes DAO calls and sends data
	 * to prefill
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 */
	@RequestMapping(value = "/finaldeterm", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getUtcLetter(@RequestBody PopulateFormReq request) {
		log.debug("Entering method cinv78s getLetter in LetterFinalDetermController");
		if (TypeConvUtil.isNullOrEmpty(request.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(request.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(letterFinalDetermService.getLetter(request)));

		return commonFormRes;
	}

}
