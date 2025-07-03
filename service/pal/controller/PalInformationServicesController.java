package us.tx.state.dfps.service.pal.controller;

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

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PalInformationReq;
import us.tx.state.dfps.service.common.request.UpdtPalServiceTrainingReq;
import us.tx.state.dfps.service.common.response.PalInformationRes;
import us.tx.state.dfps.service.pal.dto.PalInformationDto;
import us.tx.state.dfps.service.pal.service.PalInformationService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is the
 * services controller for PAL information Apr 3, 2018- 5:48:43 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/palInformation")
public class PalInformationServicesController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private PalInformationService palInformationService;

	private static final Logger logger = Logger.getLogger(PalInformationServicesController.class);

	/**
	 * 
	 * Method Name: getPalInfo Method Description:Fetches the PalInformation
	 * 
	 * @param commonHelperReq
	 * @return
	 */

	@RequestMapping(value = "/getPalInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PalInformationRes getPalInfo(@RequestBody CommonHelperReq commonHelperReq) {
		logger.debug("Entering method getPalInfo in PalInformationServicesController");
		if (ObjectUtils.isEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		PalInformationRes palInformationRes = new PalInformationRes();
		PalInformationDto palInformationDto = palInformationService.getPalInfo(commonHelperReq.getIdCase(),
				commonHelperReq.getIdStage(), commonHelperReq.getUserID());
		palInformationRes.setPalInformationDto(palInformationDto);
		logger.debug("Exiting method getPalInfo in PalInformationServicesController with stage id: "
				+ commonHelperReq.getIdStage());
		return palInformationRes;
	}

	/**
	 * AUD service for PAL Service/Training record. Legacy service # CCFC11S.
	 * This service will update all columns for an Id Stage/ Id PAL Services
	 * from the PAL SERVICE table. It will also update all columns for an Id
	 * Event from the EVENT table. It can add or modify the EVENT row. It will
	 * also retrieve the primary child for the PAL stage in order to lik the
	 * event with the primary child whem adding the
	 * 
	 * @param updtPalServiceTrainingReq
	 * @return ServiceResHeaderDto
	 */
	@RequestMapping(value = "/updtpalservicetraining", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceResHeaderDto updtPalServiceTraining(
			@RequestBody UpdtPalServiceTrainingReq updtPalServiceTrainingReq) {
		logger.debug("Entering method getPalInfo in PalInformationServicesController");
		if (ObjectUtils.isEmpty(updtPalServiceTrainingReq.getPalServiceTrainingDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idCase.mandatory", null, Locale.US));
		}
		ServiceResHeaderDto serviceResHeaderDto = palInformationService
				.updtPalServiceTraining(updtPalServiceTrainingReq);
		logger.debug("Exiting method getPalInfo in PalInformationServicesController with stage id: "
				+ updtPalServiceTrainingReq.getPalServiceTrainingDto().getIdStage());
		return serviceResHeaderDto;
	}

	/**
	 * Method Name: reopenPALStage Method Description:This service will update
	 * the close date ofr the Stage, Situation and Case tables to null. It will
	 * also set the stage closure reason to null on the STAGE table.
	 * Additionally, the new primary worker will be added t the stage along with
	 * a link to the primary child. Finally, the ILS Assessment and PAL Services
	 * event statuses will be set back to "PROC". When the case is reopened, the
	 * records retention recored must be deleted and the case file management
	 * recored must be updated with the appropriate information.
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	@RequestMapping(value = "/reopenPALStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PalInformationRes reopenPALStage(@RequestBody CommonHelperReq commonHelperReq) {
		logger.debug("Entering method reopenPALStage in PalInformationServicesController");
		if (ObjectUtils.isEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(commonHelperReq.getUserId())) {
			throw new InvalidRequestException(messageSource.getMessage("contacts.idUser.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		PalInformationRes palInformationRes = new PalInformationRes();
		PalInformationDto palInformationDto = palInformationService.reopenPALStage(commonHelperReq.getIdCase(),
				commonHelperReq.getIdStage(), commonHelperReq.getUserId(), commonHelperReq.getIdPerson());
		palInformationRes.setPalInformationDto(palInformationDto);
		logger.debug("Exiting method reopenPALStage in PalInformationServicesController with stage id: "
				+ commonHelperReq.getIdStage());
		return palInformationRes;
	}

	/**
	 * 
	 * Method Name: saveIlsAssessment Method Description:Saves the
	 * Summary,Follow up and IlsAssessment
	 * 
	 * @param palInformationReq
	 * @return
	 */
	@RequestMapping(value = "/savePalInformation", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PalInformationRes saveIlsAssessment(@RequestBody PalInformationReq palInformationReq) {
		logger.debug("Entering method savePalInformation in PalInformationServicesController");
		PalInformationDto palInformationDto = palInformationReq.getPalInformationDto();
		PalInformationRes palInformationRes = palInformationService.savePalInformation(palInformationReq);
		palInformationRes.setPalInformationDto(palInformationDto);
		logger.debug("Exiting method savePalInformation in PalInformationServicesController with stage id: "
				+ palInformationReq.getReqFuncCd());
		return palInformationRes;
	}
}
