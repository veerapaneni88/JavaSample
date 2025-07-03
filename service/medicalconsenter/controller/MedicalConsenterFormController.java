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
import us.tx.state.dfps.service.common.request.MedicalConsenterReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.medicalconsenter.service.MedicalConsenterFormService;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;

/**
 * IMPACT PHASE 2 MODERNIZATION Class Description: This service is used to
 * launch the Medical Consenter forms. Oct 30, 2017- 5:11:02 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/medicalcontroller")
public class MedicalConsenterFormController {

	@Autowired
	MedicalConsenterFormService medicalConsenterService;

	@Autowired
	MessageSource messageSource;
	private static final Logger log = Logger.getLogger(MedicalConsenterFormController.class);

	/**
	 * 
	 * Method Description:Method to launch Medical Consenter forms
	 * 
	 * @param medicalConsenterDto
	 *            Service name :CMED01S
	 * @return medicalDto
	 */
	@RequestMapping(value = "/getMedicalConsent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getMedicalConsenterForm(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method CMED01S in MedicalConsenterFormController");
		MedicalConsenterDto medicalConsenterDto = new MedicalConsenterDto();

		// Setting the value in MedicalConsenterDto
		medicalConsenterDto.setIdCase(medicalConsenterReq.getIdCase());
		medicalConsenterDto.setIdPerson(medicalConsenterReq.getIdPerson());
		medicalConsenterDto.setIdStage(medicalConsenterReq.getIdStage());
		medicalConsenterDto.setIdEvent(medicalConsenterReq.getIdEvent());

		if (TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterDto.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(medicalConsenterService.getMedicalConsentForms(medicalConsenterDto)));

		log.debug("Exiting method CMED01S in Cmed01sController");
		return commonFormRes;
	}
}
