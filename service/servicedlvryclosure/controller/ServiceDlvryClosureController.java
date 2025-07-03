package us.tx.state.dfps.service.servicedlvryclosure.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.ServiceDlvryClosureStageInDto;
import us.tx.state.dfps.service.admin.dto.ServiceDlvryClosureStageOutDto;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ServiceDlvryClosureReq;
import us.tx.state.dfps.service.common.request.ServiceDlvrySaveAndSubmitClosureReq;
import us.tx.state.dfps.service.common.request.StageClosureReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.ServiceDlvryClosureRes;
import us.tx.state.dfps.service.common.response.ServiceDlvryClosureSVCDetailsRes;
import us.tx.state.dfps.service.common.response.ServiceDlvryClosureSaveSubmitRes;
import us.tx.state.dfps.service.common.response.StageClosureRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.servicedlvryclosure.service.DlvryClosureSaveService;
import us.tx.state.dfps.service.servicedlvryclosure.service.DlvryClosureService;
import us.tx.state.dfps.service.servicedlvryclosure.service.DlvryClosureSubmitService;
import us.tx.state.dfps.service.servicedlvryclosure.service.ServiceDlvryClosureStageService;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureEventDto;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureSaveDto;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureValidationDto;
import us.tx.state.service.servicedlvryclosure.dto.ServiceDlvryClosureDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * Controller Retrieves the Ccmn45dDao,Cint40dDao,Csvc21dDao,Csesc2dDao Dam
 * Details Aug 23, 2017- 4:51:13 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("/serviceDlvryClosure")
public class ServiceDlvryClosureController {

	@Autowired
	DlvryClosureService dlvryClosureService;

	@Autowired
	DlvryClosureSaveService dlvryClosureSaveService;

	@Autowired
	DlvryClosureSubmitService dlvryClosureSubmitService;


	@Autowired
	ServiceDlvryClosureStageService serviceDlvryClosureStageService;


	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("ServiceBusiness-ServiceDlvryClosureController");

	/**
	 * Method Name: rtrvServiceDlvryClosure Method Description: This Retrieves
	 * the Ccmn45dDao,Cint40dDao,Csvc21dDao,Csesc2dDao Dam Details
	 * 
	 * @param servDelCloseRtrviDto
	 * @return ServiceDlvryClosureRes
	 */

	@RequestMapping(value = "/rtrvServiceDlvryClosure", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceDlvryClosureRes rtrvServiceDlvryClosureDtls(
			@RequestBody ServiceDlvryClosureReq serviceDlvryClosureReq) {
		ServiceDlvryClosureRes servDelCloseRtrvRes = new ServiceDlvryClosureRes();
		if (TypeConvUtil.isNullOrEmpty(serviceDlvryClosureReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ServiceDlvryClosureiDto.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(serviceDlvryClosureReq.getCdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ServiceDlvryClosureiDto.cdStage.mandatory", null, Locale.US));
		}
		log.info("Service call in Service Dlvry closure");
		ServiceDlvryClosureDto serviceDlvryClosure = dlvryClosureService.dlvryClosureService(serviceDlvryClosureReq);
		servDelCloseRtrvRes.setServiceDlvryClosureoDto(serviceDlvryClosure);
		return servDelCloseRtrvRes;
	}

	/**
	 * Method Name: saveServiceDlvryClosure Method Description: Service Delivery
	 * Closure fields to save
	 * 
	 * @param dlvryClosureSaveDto
	 * @return DlvryClosureEventDto
	 */
	@RequestMapping(value = "/saveServiceDlvryClosure", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DlvryClosureEventDto saveServiceDlvryClosure(
			@RequestBody DlvryClosureSaveDto dlvryClosureSaveDto) {
		log.debug("Entering method CSVC14S in ServDelCloseSaveController");
		DlvryClosureEventDto dlvryClosureEventDto = dlvryClosureSaveService
				.saveDlvryClosureService(dlvryClosureSaveDto);
		log.debug("Exiting method CSVC14S in ServDelCloseSaveController");
		return dlvryClosureEventDto;
	}

	/**
	 * Method Name: getPersonIdInFDTC Method Description:The method returns the
	 * list of person that have legal actions of type FDTC for a given case
	 * 
	 * @param serviceDlvryClosureReq
	 * @return ServiceDlvryClosureRes
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/getPersonIdInFDTC", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceDlvryClosureRes getPersonIdInFDTC(
			@RequestBody ServiceDlvryClosureReq serviceDlvryClosureReq) {
		log.info("Entering method getPersonIdInFDTC in ServiceDlvryClosureController");
		if (TypeConvUtil.isNullOrEmpty(serviceDlvryClosureReq.getIdCase())) {
			throw new InvalidRequestException(messageSource
					.getMessage("servicedlvryclosure.ServiceDlvryClosureValue.mandatory", null, Locale.US));
		}
		ServiceDlvryClosureRes serviceDlvryClosureRes = new ServiceDlvryClosureRes();
		HashMap hashMap = dlvryClosureSubmitService.getPersonIdInFDTC(serviceDlvryClosureReq.getIdCase());
		serviceDlvryClosureRes.getServiceDlvryClosureoDto().setHashMap(hashMap);
		log.info("Exiting method getPersonIdInFDTC in ServiceDlvryClosureController");
		return serviceDlvryClosureRes;
	}

	/**
	 * Method Name: getMostRecentFDTCSubtype Method Description: Returns the
	 * most recent FDTC Subtype and Outcome date for a Person in a given case id
	 * 
	 * @param stageClosureReq
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getMostRecentFDTCSubtype", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureRes getMostRecentFDTCSubtype(@RequestBody StageClosureReq stageClosureReq) {
		log.info("Entering method getMostRecentFDTCSubtype in ServiceDlvryClosureController");
		if (TypeConvUtil.isNullOrEmpty(stageClosureReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stageclosure.getMostRecentFDTCSubtype.mandatory", null, Locale.US));
		}
		StageClosureRes stageClosureRes = new StageClosureRes();
		stageClosureRes
				.setResultHashMap(dlvryClosureSubmitService.getMostRecentFDTCSubtype(stageClosureReq.getIdPerson()));
		log.info("Exiting method getMostRecentFDTCSubtype in ServiceDlvryClosureController");
		return stageClosureRes;
	}

	/**
	 * Method Name: saveAndSubmitServiceDlvryClosure Method Description: save
	 * and submit for service dlvry closure
	 * 
	 * @param serviceDlvrySaveAndSubmitClosureReq
	 * @return ServiceDlvryClosureSaveSubmitRes
	 */
	@RequestMapping(value = "/saveSubmitServiceDlvryClosure", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceDlvryClosureSaveSubmitRes saveAndSubmitServiceDlvryClosure(
			@RequestBody ServiceDlvrySaveAndSubmitClosureReq serviceDlvrySaveAndSubmitClosureReq) {
		log.debug("Entering method saveAndSubmitServiceDlvryClosure in ServiceDlvryClosure");

		DlvryClosureSaveDto dlvryClosureSaveDto = serviceDlvrySaveAndSubmitClosureReq.getDlvryClosureSaveDto();
		DlvryClosureValidationDto dlvryClosureValidationDto = serviceDlvrySaveAndSubmitClosureReq
				.getDlvryClosureValidationDto();
		ServiceDlvryClosureReq serviceDlvryClosureReq = serviceDlvrySaveAndSubmitClosureReq.getServiceDlvryClosureReq();
		ServiceDlvryClosureSaveSubmitRes serviceDlvryClosureSaveSubmitRes = dlvryClosureSaveService
				.saveAndSubmitDlvryClosure(dlvryClosureSaveDto, serviceDlvryClosureReq, dlvryClosureValidationDto);
		log.debug("Exiting method CSVC14S in ServDelCloseSaveController");
		return serviceDlvryClosureSaveSubmitRes;
	}

	@RequestMapping(value = "/printTaskExistsFPR", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes printTaskExists(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		return dlvryClosureService.getPrintTaskExists(commonHelperReq);
	}

	/**
	 * Method Name: retrvDecisionDate Method Description: retrieves the service delivery Decision details
	 *
	 * @param svcdelvdtlStageInDto
	 * @return ServiceDlvryClosureSVCDetailsRes
	 */
	@RequestMapping(value = "/retrvDecisionDate", headers = {"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceDlvryClosureSVCDetailsRes retrvDecisionDate(@RequestBody ServiceDlvryClosureStageInDto svcdelvdtlStageInDto) {
		ServiceDlvryClosureSVCDetailsRes serviceDlvryClosureSVCDetailsRes = new ServiceDlvryClosureSVCDetailsRes();
		serviceDlvryClosureSVCDetailsRes.setServiceDlvryClosureStageOutDtos(serviceDlvryClosureStageService.retrvDecisionDate(svcdelvdtlStageInDto));
		return serviceDlvryClosureSVCDetailsRes;
	}

	@PostMapping(value = "/getClosureForm", headers = {"Accept=application/json"})
	public CommonFormRes getApsCaseReviewLegacyVersionInformation(@RequestBody CommonApplicationReq request) {

		CommonFormRes commonFormRes = new CommonFormRes();
		if (TypeConvUtil.isNullOrEmpty(request.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(serviceDlvryClosureStageService.getClosureFormInformation(request)));

		return commonFormRes;
	}

	/**
	 * Method Name: retrvDecisionDate Method Description: retrieves the service delivery Decision details
	 *
	 * @param svcdelvdtlStageInDto
	 * @return ServiceDlvryClosureSVCDetailsRes
	 */
	@RequestMapping(value = "/retrvDecisionDateAps", headers = {"Accept=application/json" }, method = RequestMethod.POST)
	public ServiceDlvryClosureSVCDetailsRes retrvDecisionDateAps(@RequestBody ServiceDlvryClosureStageInDto svcdelvdtlStageInDto) {
		ServiceDlvryClosureSVCDetailsRes serviceDlvryClosureSVCDetailsRes = new ServiceDlvryClosureSVCDetailsRes();
		serviceDlvryClosureSVCDetailsRes.setServiceDlvryClosureStageOutDtos(serviceDlvryClosureStageService.retrvDecisionDateAps(svcdelvdtlStageInDto));
		return serviceDlvryClosureSVCDetailsRes;
	}

}
