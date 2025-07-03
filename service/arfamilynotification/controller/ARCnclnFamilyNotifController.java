package us.tx.state.dfps.service.arfamilynotification.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.arfamilynotification.service.ARCnclnFamilyNotifService;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Controller
 * class for Alternative Response Family Notification> Apr 5, 2018- 11:27:07 AM
 * © 2017 Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/aRCnclnFamilyNotif")
public class ARCnclnFamilyNotifController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ARCnclnFamilyNotifService aRCnclnFamilyNotifService;

	private static final Logger log = Logger.getLogger("ServiceBusiness-PcspHistoryFormControllerLog");

	/**
	 * 
	 * Method Description: Populates Arfanot form on person detail/contact
	 * detail page Name: Arfanot
	 * 
	 * @param populateFormReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getARCnclnFamilyNotif", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getARCnclnFamilyNotif(@RequestBody PopulateFormReq populateFormReq) {

		if (ObjectUtils.isEmpty(populateFormReq.getIdCase()))
			throw new InvalidRequestException(
					messageSource.getMessage("common.caseid.mandatory", null, Locale.US));

		if (ObjectUtils.isEmpty(populateFormReq.getIdWorker()))
			throw new InvalidRequestException(
					messageSource.getMessage("common.workerid.mandatory", null, Locale.US));

		if (ObjectUtils.isEmpty(populateFormReq.getArClosureReason()))
			throw new InvalidRequestException(
					messageSource.getMessage("common.arClosureReason.mandatory", null, Locale.US));

		if (ObjectUtils.isEmpty(populateFormReq.getIdPerson()))
			throw new InvalidRequestException(
					messageSource.getMessage("common.personid.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(aRCnclnFamilyNotifService.getARCnclnFamilyNotif(populateFormReq)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());

		return commonFormRes;
	}

	/**
	 * 
	 * Method Description: Populates arsafna form on ARInitialSafetyAssmt and
	 * ARClosureSafetyAssmt page Name: arsafna
	 * 
	 * @param populateFormReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getARSafetyFamilyAssmtForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getARSafetyFamilyAssmtForm(@RequestBody PopulateFormReq populateFormReq) {

		if (ObjectUtils.isEmpty(populateFormReq.getIdCase()))
			throw new InvalidRequestException(
					messageSource.getMessage("common.caseid.mandatory", null, Locale.US));

		if (ObjectUtils.isEmpty(populateFormReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("common.stageid.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(aRCnclnFamilyNotifService.getARSafetyFamilyAssmtForm(populateFormReq)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());

		return commonFormRes;
	}

}
