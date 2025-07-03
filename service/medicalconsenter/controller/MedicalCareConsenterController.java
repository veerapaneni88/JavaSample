package us.tx.state.dfps.service.medicalconsenter.controller;

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
import us.tx.state.dfps.service.common.request.MedicalCareConsenterReq;
import us.tx.state.dfps.service.common.request.MedicalConsenterFormLogReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.MedicalConsenterRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.medicalconsenter.service.MedicalCareConsenterService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * MedicalCareConsenterController used to launch the Notification Regarding
 * Consent for Medical Care. Feb 9, 2018- 1:43:38 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/MedicalCareConsenterController")
public class MedicalCareConsenterController {

	@Autowired
	MedicalCareConsenterService medCareConsenterService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(MedicalCareConsenterController.class);

	/**
	 * Method Description: The service is used to launch the Medical Consenter
	 * forms.CMED03S service is converted to this.
	 * 
	 * @param MedicalCareConsenterReq
	 * @return MedicalConsenterPersonRes
	 */
	@RequestMapping(value = "/medicalConsenterRtrv", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes medicalConsenterRtrv(@RequestBody MedicalCareConsenterReq medCareConsenterReq) {

		log.debug("Entering method medicalConsenterRtrv in MedicalCareConsenterController");
		CommonFormRes commonFormRes = new CommonFormRes();

		if (TypeConvUtil.isNullOrEmpty(medCareConsenterReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medCareConsenterReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idPerson.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medCareConsenterReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}

		commonFormRes.setPreFillData(TypeConvUtil
				.getXMLFormat(medCareConsenterService.callMedicalCareConsenterService(medCareConsenterReq)));

		log.info("TransactionId :" + medCareConsenterReq.getTransactionId());
		log.debug("Exiting method medicalConsenterRtrv in MedicalCareConsenterController");
		return commonFormRes;
	}

}
