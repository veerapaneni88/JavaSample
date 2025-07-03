package us.tx.state.dfps.service.medicalconsenter.controller;

import java.util.Locale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.MedicalConsenterFormLogReq;
import us.tx.state.dfps.service.common.request.MedicalConsenterReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.MedicalConsenterRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.medicalconsenter.service.MedicalConsenterService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:MedicalConsenterController Oct 31, 2017- 4:05:58 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@Api(tags = { "placements", "medicalconsenter" })
@RequestMapping("/MedicalConsenter")
public class MedicalConsenterController {
	@Autowired
	private MedicalConsenterService medicalConsenterService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(MedicalConsenterController.class);

	/**
	 * Method Name: saveMedicalConsenterDetail Method Description: Saves the
	 * Medical Consenter data to the database
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/saveMedicalConsenterDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes saveMedicalConsenterDetail(
			@RequestBody MedicalConsenterReq medicalConsenterReq){
		log.debug("Entering method saveMedicalConsenterDetail in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getMedicalConsenterDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("medicalConsenterDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getEventInputDto())) {
			throw new InvalidRequestException(messageSource.getMessage("eventInputDto.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setMedicalConsenterDto(medicalConsenterService.saveMedicalConsenterDetail(
				medicalConsenterReq.getMedicalConsenterDto(), medicalConsenterReq.getEventInputDto()));
		log.debug("Exiting method saveMedicalConsenterDetail in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: selectPersonId Method Description:Select personId from case
	 * and stage ids in database
	 * 
	 * @param medicalConsenter
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/selectPersonId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes selectPersonId(@RequestBody MedicalConsenterReq medicalConsenter) {
		log.debug("Entering method selectPersonId in MedicalConsenterController");
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		if (TypeConvUtil.isNullOrEmpty(medicalConsenter.getMedicalConsenterDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("medicalConsenterDto.mandatory", null, Locale.US));
		}
		medicalConsenterRes.setMedicalConsenterDto(
				medicalConsenterService.selectPersonId(medicalConsenter.getMedicalConsenterDto()));
		log.debug("Exiting method selectPersonId in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenter.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: queryMedicalConsenterList Method Description:Select list of
	 * medical consenter detail from case and stage ids in database.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@ApiOperation(value = "Get medical consenter list", tags = { "placements", "medicalconsenter" })
	@RequestMapping(value = "/queryMedicalConsenterList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes queryMedicalConsenterList(
			@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method queryMedicalConsenterList in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getMedicalConsenterDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("medicalConsenterDto.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setMedicalConsenterList(
				medicalConsenterService.queryMedicalConsenterList(medicalConsenterReq.getMedicalConsenterDto()));
		log.debug("Exiting method queryMedicalConsenterList in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: queryMedicalConsenterRecord Method Description:Select a
	 * medical consenter detail for the given primary id in database.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/queryMedicalConsenterRecord", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes queryMedicalConsenterRecord(
			@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method queryMedicalConsenterRecord in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdMedCons())) {
			throw new InvalidRequestException(messageSource.getMessage("idMedCons.Mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setMedicalConsenterDto(
				medicalConsenterService.queryMedicalConsenterRecord(medicalConsenterReq.getIdMedCons()));
		log.debug("Exiting method queryMedicalConsenterRecord in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: endDateRecordType Method Description:End Date Type of
	 * another record before saving new Medical Consenter
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/endDateRecordType", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes endDateRecordType(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method endDateRecordType in MedicalConsenterController");
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getMedicalConsenterDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("medicalConsenterDto.mandatory", null, Locale.US));
		}
		medicalConsenterRes.setMedicalConsenterDto(
				medicalConsenterService.endDateRecordType(medicalConsenterReq.getMedicalConsenterDto()));
		log.debug("Exiting method endDateRecordType in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: updateMedicalConsenterEndDate Method Description:Update the
	 * Medical Consenter end date to the database.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/updateMedicalConsenterEndDate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes updateMedicalConsenterEndDate(
			@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method updateMedicalConsenterEndDate in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getMedicalConsenterDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("medicalConsenterDto.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setMedicalConsenterDto(
				medicalConsenterService.updateMedicalConsenterEndDate(medicalConsenterReq.getMedicalConsenterDto()));
		log.debug("Exiting method updateMedicalConsenterEndDate in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;

	}

	/**
	 * Method Name: checkDfpsStaff Method Description: Check if a Medical
	 * Consenter is a DFPS Staff
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/checkDfpsStaff", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes checkDfpsStaff(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method checkDfpsStaff in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdStaff())) {
			throw new InvalidRequestException(messageSource.getMessage("idStaff.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setIsStaff(medicalConsenterService.checkDfpsStaff(medicalConsenterReq.getIdStaff()));
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		log.debug("Exiting method checkDfpsStaff in MedicalConsenterController");
		return medicalConsenterRes;

	}

	/**
	 * Method Name: checkMedicalConsenterStatus Method Description:check Medical
	 * Consenter Status from database.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/checkMedicalConsenterStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes checkMedicalConsenterStatus(
			@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method checkMedicalConsenterStatus in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getMedicalConsenterDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("medicalConsenterDto.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setStatus(
				medicalConsenterService.checkMedicalConsenterStatus(medicalConsenterReq.getMedicalConsenterDto()));
		log.debug("Exiting method checkMedicalConsenterStatus in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: isPersonMedicalConsenter Method Description:check if the
	 * person is in Medical Consenter table.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/isPersonMedicalConsenter", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes isPersonMedicalConsenter(
			@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method isPersonMedicalConsenter in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdPersonString())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setIsMedCons(medicalConsenterService.isPersonMedicalConsenter(
				medicalConsenterReq.getIdPersonString(), medicalConsenterReq.getIdStageString()));
		log.debug("Exiting method isPersonMedicalConsenter in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;

	}

	/**
	 * Method Name: getMedicalConsenterIdForEvent Method Description:Get the
	 * medical consenter id based on the medical consenter creation event id.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/getMedicalConsenterIdForEvent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes getMedicalConsenterIdForEvent(
			@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method getMedicalConsenterIdForEvent in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes
				.setIdMedCons(medicalConsenterService.getMedicalConsenterIdForEvent(medicalConsenterReq.getIdEvent()));
		log.debug("Exiting method getMedicalConsenterIdForEvent in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: isPersonMedicalConsenterType Method Description:check if the
	 * person is already Medical Consenter type.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/isPersonMedicalConsenterType", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes isPersonMedicalConsenterType(
			@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method isPersonMedicalConsenterType in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdChild())) {
			throw new InvalidRequestException(messageSource.getMessage("idChild.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setIdEvent(medicalConsenterService.isPersonMedicalConsenterType(
				medicalConsenterReq.getIdPerson(), medicalConsenterReq.getIdCase(), medicalConsenterReq.getIdChild()));
		log.debug("Exiting method isPersonMedicalConsenterType in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: updateMedicalConsenterRecord Method Description:Update the
	 * Medical Consenter Record.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/updateMedicalConsenterRecord", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes updateMedicalConsenterRecord(
			@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method updateMedicalConsenterRecord in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty((medicalConsenterReq.getMedicalConsenterDto()))) {
			throw new InvalidRequestException(
					messageSource.getMessage("medicalConsenterDto.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();

		medicalConsenterRes.setConsenterId(
				medicalConsenterService.updateMedicalConsenterRecord(medicalConsenterReq.getMedicalConsenterDto()));

		log.debug("Exiting method updateMedicalConsenterRecord in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: getPrimaryChild Method Description:Get Primary Child given
	 * stage id and Case id
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/getPrimaryChild", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes getPrimaryChild(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method getPrimaryChild in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("idChild.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setIdPerson(medicalConsenterService.getPrimaryChild(medicalConsenterReq.getIdCase(),
				medicalConsenterReq.getIdStage()));
		log.debug("Exiting method getPrimaryChild in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: getStageType Method Description:Method retrieves the type of
	 * input stage.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/getStageType", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes getStageType(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method getStageType in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("idChild.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setStageType(medicalConsenterService.getStageType(medicalConsenterReq.getIdStage()));
		log.debug("Exiting method getStageType in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: getCorrespStage Method Description:If Input stage is SUB/ADO
	 * the method retrieves corresponding ADO/SUB stage id from stage_link
	 * table.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/getCorrespStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes getCorrespStage(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method getCorrespStage in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setIdStage(medicalConsenterService.getCorrespStage(medicalConsenterReq.getIdStage()));
		log.debug("Exiting method getCorrespStage in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: personAddrExists Method Description:This implements two
	 * logic 1. Checks if the person in the Medical Censenter have atleast one
	 * Zip code in the associated addresses and 2.if the peron have atleast one
	 * assoicated address.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/personAddrExists", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes personAddrExists(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method personAddrExists in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes
				.setAddrOrZipCodeNull(medicalConsenterService.personAddrExists(medicalConsenterReq.getIdPerson()));
		log.debug("Exiting method personAddrExists in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: checkAlertTodoExists Method Description:Method to check if
	 * Medical Consenter Alert Exists for stage
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/checkAlertTodoExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes checkAlertTodoExists(
			@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method checkAlertTodoExists in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getCd_todo_type())) {
			throw new InvalidRequestException(messageSource.getMessage("cd_todo_type.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getTxt_Todo_Desc())) {
			throw new InvalidRequestException(messageSource.getMessage("txt_Todo_Desc.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setIndAlertExists(medicalConsenterService.checkAlertTodoExists(
				medicalConsenterReq.getIdStage(), medicalConsenterReq.getIdCase(),
				medicalConsenterReq.getCd_todo_type(), medicalConsenterReq.getTxt_Todo_Desc()));
		log.debug("Exiting method checkAlertTodoExists in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: getPrimaryWorker Method Description:Fetch Primary Worker for
	 * given stage only when stage is active.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/getPrimaryWorker", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes getPrimaryWorker(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method getPrimaryWorker in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		log.debug("Exiting method getPrimaryWorker in MedicalConsenterController");
		medicalConsenterRes
				.setIdPrimaryWorker(medicalConsenterService.getPrimaryWorker(medicalConsenterReq.getIdStage()));
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: checkPrimBackExists Method Description:Method returns true
	 * if Primary child for the given stage has atleast one Primary and one
	 * Backup MC's that are court authorized.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/checkPrimBackExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes checkPrimBackExists(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method checkPrimBackExists in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("idChild.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setIndPrimBackExists(medicalConsenterService
				.checkPrimBackExists(medicalConsenterReq.getIdStage(), medicalConsenterReq.getIdChild()));
		log.debug("Exiting method checkPrimBackExists in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());

		return medicalConsenterRes;
	}

	/**
	 * Method Name: checkMCCourtAuth Method Description:Method returns true if
	 * the Primary Child has atleast one Medical Consenter either in
	 * SUBCARE/ADOPTION stage or in related ADOPTION/SUBCARE stage that is
	 * marked as court authorized(IND_CRT_AUTH is 'Y')
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/checkMCCourtAuth", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes checkMCCourtAuth(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method checkMCCourtAuth in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdChild())) {
			throw new InvalidRequestException(messageSource.getMessage("idChild.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setCheckMCCourtAuth(medicalConsenterService
				.checkMCCourtAuth(medicalConsenterReq.getIdStage(), medicalConsenterReq.getIdChild()));
		log.debug("Exiting method checkMCCourtAuth in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: isActiveMedCons Method Description:isActiveMedCons gets
	 * count of active medical consenters for the person.
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/isActiveMedCons", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes isActiveMedCons(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method isActiveMedCons in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("idPerson.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes
				.setHasActiveMedCons(medicalConsenterService.isActiveMedCons(medicalConsenterReq.getIdPerson()));
		log.debug("Exiting method isActiveMedCons in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * Method Name: updatePersonNameSfx Method Description:Method to get the
	 * Person Name Suffix
	 * 
	 * @param medicalConsenterReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/updatePersonNameSfx", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  MedicalConsenterRes updatePersonNameSfx(@RequestBody MedicalConsenterReq medicalConsenterReq) {
		log.debug("Entering method updatePersonNameSfx in MedicalConsenterController");
		if (TypeConvUtil.isNullOrEmpty(medicalConsenterReq.getMedicalConsenterDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("medicalConsenterDto.mandatory", null, Locale.US));
		}
		MedicalConsenterRes medicalConsenterRes = new MedicalConsenterRes();
		medicalConsenterRes.setMedicalConsenterDto(
				medicalConsenterService.updatePersonNameSfx(medicalConsenterReq.getMedicalConsenterDto()));
		log.debug("Exiting method updatePersonNameSfx in MedicalConsenterController");
		medicalConsenterRes.setTransactionId(medicalConsenterReq.getTransactionId());
		return medicalConsenterRes;
	}

	/**
	 * 
	 * Method Name: audMdclConsenterFormLog Method Description: This method is
	 * to save Medical Consenter forms(Designation of Medical Consenter Form
	 * 2085-B English & Spanish) status and doc_type in the Log
	 * table(MED_CNSNTR_FORM_LOG) when the Form launched using launch button in
	 * detail page, If Save and Complete button is clicked in Detail page update
	 * the status as 'COMP', If Delete button clicked delete the record.
	 *
	 * @param medicalConsenterFormLogReq
	 * @return CommonStringRes
	 */
	@RequestMapping(value = "/audMdclConsenterFormLog", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonStringRes audMdclConsenterFormLog(@RequestBody MedicalConsenterFormLogReq medicalConsenterFormLogReq) {

		return medicalConsenterService.audMdclConsenterFormLog(medicalConsenterFormLogReq);
	}

	/**
	 * 
	 * Method Name: getMdclConsenterFormLogList Method Description: This method
	 * is to get the list to display forms(Designation of Medical Consenter Form
	 * 2085-B English & Spanish) associated with the Medical Consenters in the
	 * Medical Consenter List page
	 *
	 * @param medicalConsenterFormLogReq
	 * @return MedicalConsenterRes
	 */
	@RequestMapping(value = "/getMdclConsenterFormLogList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public MedicalConsenterRes getMdclConsenterFormLogList(
			@RequestBody MedicalConsenterFormLogReq medicalConsenterFormLogReq) {

		return medicalConsenterService.getMdclConsenterFormLogList(medicalConsenterFormLogReq);
	}

	/**
	 * 
	 * Method Name: updateMdclConsenterFormLog Method Description: This method
	 * is to update Medical Consenter forms(Designation of Medical Consenter
	 * Form 2085-B English & Spanish) status and doc_type in the Log
	 * table(MED_CNSNTR_FORM_LOG) when the Form is launched and saved
	 * 
	 * @param medicalConsenterFormLogReq
	 * @return CommonStringRes
	 */
	@RequestMapping(value = "/updateMdclConsenterFormLog", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonStringRes updateMdclConsenterFormLog(
			@RequestBody MedicalConsenterFormLogReq medicalConsenterFormLogReq) {

		return medicalConsenterService.updateMdclConsenterFormLog(medicalConsenterFormLogReq);
	}

}
