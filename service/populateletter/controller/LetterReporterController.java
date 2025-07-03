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
import us.tx.state.dfps.service.common.request.PopulateLetterReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.populateletter.service.LetterReporterService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Tuxedo
 * Service Name: CINV63S Class Jan 15, 2018- 12:58:15 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@RestController
@RequestMapping("/populateletter")
public class LetterReporterController {

	/**
	 * 
	 */

	@Autowired
	LetterReporterService letterReporterService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("ServiceBusiness-PopulateLetterControllerLog");

	/**
	 * 
	 * Method Description: Populates the Letter to Reporter forms
	 * (cfiv0500-English and cfiv1800-Spanish) Name: CINV63S
	 * 
	 * @param populateLetterReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getPopulateLetter", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getPopulateLetter(@RequestBody PopulateLetterReq populateLetterReq) {
		if (TypeConvUtil.isNullOrEmpty(populateLetterReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("populateLetter.idStage.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(populateLetterReq.getIdPerson()))
			throw new InvalidRequestException(
					messageSource.getMessage("populateLetter.IdPerson.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(letterReporterService.populateLetter(populateLetterReq, false)));
		log.info("TransactionId :" + populateLetterReq.getTransactionId());
		// return letterReporterService.populateLetter(populateLetterReq);

		return commonFormRes;
	}

	@RequestMapping(value = "/getPopulateLetterSpanish", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getPopulateLetterSpanish(@RequestBody PopulateLetterReq populateLetterReq) {
		if (TypeConvUtil.isNullOrEmpty(populateLetterReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("populateLetter.idStage.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(populateLetterReq.getIdPerson()))
			throw new InvalidRequestException(
					messageSource.getMessage("populateLetter.IdPerson.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(letterReporterService.populateLetter(populateLetterReq, true)));
		log.info("TransactionId :" + populateLetterReq.getTransactionId());
		// return letterReporterService.populateLetter(populateLetterReq);
		log.debug(commonFormRes.getPreFillData());
		return commonFormRes;
	}

}
