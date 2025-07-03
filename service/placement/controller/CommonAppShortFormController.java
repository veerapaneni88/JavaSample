package us.tx.state.dfps.service.placement.controller;

import java.util.Comparator;
import java.util.List;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonAppShortFormReq;
import us.tx.state.dfps.service.common.response.CommonAppShortFormRes;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.exception.ResourceNotFoundException;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.placement.dto.CommonApplicationShortFormDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.ShortFormCsaEpisodeIncdntsDto;
import us.tx.state.dfps.service.placement.dto.ShortFormEducationSrvDto;
import us.tx.state.dfps.service.placement.dto.ShortFormMedicationDto;
import us.tx.state.dfps.service.placement.dto.ShortFormPsychiatricDto;
import us.tx.state.dfps.service.placement.dto.ShortFormRtnRunawayDto;
import us.tx.state.dfps.service.placement.dto.ShortFormSiblingsDto;
import us.tx.state.dfps.service.placement.dto.ShortFormSpecialProgrammingDto;
import us.tx.state.dfps.service.placement.dto.ShortFormTherapyDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.placement.service.CommonAppShortFormService;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;
import us.tx.state.dfps.web.placement.bean.ShortFormSubstanceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CommonApplicationController will have all operation which are
 * mapped to Placement module. Feb 9, 2018- 2:13:13 PM © 2017 Texas Department
 * of Family and Protective Services
 *********************************  Change History *********************************
 * 06/18/2019  muddur    artf113241 : Interoperability Project initial. 
 * 01/08/2020  anantj    artf113794 : Changes for Common App Short Form Sub-tables for Webservice API. 
 * 01/15/2020  thompswa  artf113241 : add getCommonApplicationShortForm for print. 
 */
@Api(tags = { "common-app" })
@RestController
@RequestMapping("/commonAppShortForm")
public class CommonAppShortFormController {
	@Autowired
	private CommonAppShortFormService shortFormService;

	@Autowired
	MessageSource messageSource;
	
	private static final Logger logger = Logger.getLogger(CommonAppShortFormController.class);

	public static final String NEW_PAGE_MODE = "1";
	public static final String NEW_USING_PAGE_MODE = "2";
	public static final String VIEW_PAGE_MODE = "3";

	@RequestMapping(value = "/getShortForm", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonAppShortFormRes getCommonApplicationForm(@RequestBody CommonAppShortFormReq commonAppShortFormReq) {
		
		StagePersonLinkCaseDto stagePersonLinkCaseDto = null;
		EmployeePersPhNameDto employeePersPhNameDto = null;
		UnitDto unitDto = null;
		EmployeePersPhNameDto employeeSup = null;
		PersonDto personDto = null;
		String ethnicity = null;
		String race = null;
		FceEligibilityDto  fceEligibilityDto = null;
		LegalStatusPersonMaxStatusDtOutDto legalStatus = null;
		FceApplicationDto fceAppDt = null;
		EventIdOutDto eventDto = null;
		CommonApplicationShortFormDto shortFormDto = null;
		ShortFormSpecialProgrammingDto splProg = null;
		ShortFormRtnRunawayDto rtnRunaway = null;
		ShortFormEducationSrvDto eduSrv = null;
		List<PlacementDtlDto> placementLog = null;
		List<ShortFormSiblingsDto> siblingList = null;
		List<ShortFormMedicationDto> medList = null;
		List<ShortFormTherapyDto> therapyList = null;
		ShortFormSubstanceDto substanceDto = null;
		List<ShortFormPsychiatricDto> psychiatricList = null;
		List<SexualVictimIncidentDto> sexualVictimList = null;
		SexualVictimHistoryDto sexualVictim = null;
		List<TraffickingDto> traffickingList = null;
		List<TraffickingDto> sfTraffickingList = null;
		List<ShortFormCsaEpisodeIncdntsDto> episodeIncdntDtls = null;
		List<ShortFormCsaEpisodeIncdntsDto> sfEpisodeIncdntDtls = null;
		CnsrvtrshpRemovalDto removalDto = null;	
		List<LegalStatusPersonMaxStatusDtOutDto> legalStatusList = null;
				
		stagePersonLinkCaseDto = shortFormService
				.retrieveHeaderInfo(commonAppShortFormReq.getStageId());
		
		if(!ServiceConstants.EVENTSTATUS_COMPLETE.equals(commonAppShortFormReq.getEventStatus()) && !ServiceConstants.EVENTSTATUS_APPROVE.equals(commonAppShortFormReq.getEventStatus()) || "2".equals(commonAppShortFormReq.getPageMode())){
		
			employeePersPhNameDto = shortFormService
					.geCaseworkerInfo(commonAppShortFormReq.getStageId());
			unitDto = shortFormService.getUnitInfo(employeePersPhNameDto.getIdUnit());
			employeeSup = shortFormService
					.getSupervisorInfo(employeePersPhNameDto.getIdJobPersSupv());
			personDto = shortFormService.getChildInfo(stagePersonLinkCaseDto.getIdPerson());
			ethnicity = shortFormService.getEthnicityInfo(stagePersonLinkCaseDto.getIdPerson());
			race = shortFormService.getRaceInfo(stagePersonLinkCaseDto.getIdPerson());
			fceEligibilityDto= shortFormService.getCitizenInfo(stagePersonLinkCaseDto.getIdPerson());
			
			legalStatusList = shortFormService.getLegalCounty(stagePersonLinkCaseDto.getIdPerson());
			if(null != legalStatusList && legalStatusList.size() > 0){
				legalStatus = legalStatusList.stream()
						.max(Comparator.comparing(LegalStatusPersonMaxStatusDtOutDto::getTsLastUpdate)).get();
			}
			
			fceAppDt = shortFormService.getRemovalAddr(stagePersonLinkCaseDto.getIdPerson());
			placementLog = shortFormService.getPlacementLog(stagePersonLinkCaseDto.getIdPerson());
			sexualVictim = shortFormService.getSexualVictimization(stagePersonLinkCaseDto.getIdPerson());
			traffickingList = shortFormService.getTrafficking(stagePersonLinkCaseDto.getIdPerson());
			episodeIncdntDtls = shortFormService.getCSAEpisodeIncdntDtls(stagePersonLinkCaseDto.getIdPerson());
			removalDto = shortFormService.getRemovalDate(stagePersonLinkCaseDto.getIdPerson());
		}
		if(null != commonAppShortFormReq.getEventId() && commonAppShortFormReq.getEventId() > 0){
			shortFormDto = shortFormService.getCommonAppShortFormByEventId(commonAppShortFormReq.getEventId());
			if(null != shortFormDto){
				splProg = shortFormService.getSplProgByCaExId(shortFormDto.getIdCaExCommonApplication());
			}
			if(null != shortFormDto){
				rtnRunaway = shortFormService.getRtnRunawayByCaExId(shortFormDto.getIdCaExCommonApplication());
			}
			if(null != shortFormDto){
				eduSrv = shortFormService.getEduSrvByCaExId(shortFormDto.getIdCaExCommonApplication());
			}
			if(null != shortFormDto){
				medList = shortFormService.getMedicationByCaExId(shortFormDto.getIdCaExCommonApplication());
			}
			if(null != shortFormDto){
				therapyList = shortFormService.getTherapyDtlByCaExId(shortFormDto.getIdCaExCommonApplication());
			}
			if(null != shortFormDto){
				substanceDto = shortFormService.getSubstanceAbuseByExId(shortFormDto.getIdCaExCommonApplication());
			}
			if(null != shortFormDto){
				psychiatricList = shortFormService.getHospitalizationByCaExId(shortFormDto.getIdCaExCommonApplication());
			}
			//siblingList = shortFormService.getSiblingsByCaExId(shortFormDto.getIdCaExCommonApplication());
			if(!NEW_USING_PAGE_MODE.equals(commonAppShortFormReq.getPageMode())){
				if(null !=shortFormDto && null == sexualVictim || null == sexualVictim.getSvhIncidents() ||(ServiceConstants.EVENTSTATUS_COMPLETE.equals(commonAppShortFormReq.getEventStatus()) || ServiceConstants.EVENTSTATUS_APPROVE.equals(commonAppShortFormReq.getEventStatus()))){
					sexualVictimList = shortFormService.getSFSexualVictimization(shortFormDto.getIdCaExCommonApplication());
				}
				if(null !=shortFormDto && null == traffickingList || traffickingList.size() <=0  || (ServiceConstants.EVENTSTATUS_COMPLETE.equals(commonAppShortFormReq.getEventStatus()) || ServiceConstants.EVENTSTATUS_APPROVE.equals(commonAppShortFormReq.getEventStatus()))){
					sfTraffickingList = shortFormService.getSFTraffickingHistory(shortFormDto.getIdCaExCommonApplication());
				}
				if(null !=shortFormDto && null == episodeIncdntDtls || episodeIncdntDtls.size() <=0 || (ServiceConstants.EVENTSTATUS_COMPLETE.equals(commonAppShortFormReq.getEventStatus()) || ServiceConstants.EVENTSTATUS_APPROVE.equals(commonAppShortFormReq.getEventStatus()))){
					sfEpisodeIncdntDtls = shortFormService.getSFEpisodeIncidents(shortFormDto.getIdCaExCommonApplication());
				}
			}
		
		}
		
		if(null !=shortFormDto && ServiceConstants.EVENTSTATUS_COMPLETE.equals(commonAppShortFormReq.getEventStatus()) || ServiceConstants.EVENTSTATUS_APPROVE.equals(commonAppShortFormReq.getEventStatus())){
			placementLog = shortFormService.getPlacementLogByExId(shortFormDto.getIdCaExCommonApplication());
		}
		
		PersonLocDto personLocDto =	shortFormService.retrieveLevelOfCare(stagePersonLinkCaseDto.getIdPerson());
		if(!ServiceConstants.EVENTSTATUS_COMPLETE.equals(commonAppShortFormReq.getEventStatus()) && !ServiceConstants.EVENTSTATUS_APPROVE.equals(commonAppShortFormReq.getEventStatus()) ){
			siblingList = shortFormService.getSiblingsList(commonAppShortFormReq.getStageId(), shortFormDto);
		}else if(null != shortFormDto && null != shortFormDto.getIdCaExCommonApplication()){
			siblingList = shortFormService.getSiblingsByCaExId(shortFormDto.getIdCaExCommonApplication());
		}

		ServicePackageDtlDto recommendedServicePackageDto = shortFormService.retrieveRecommendedServicePackage(commonAppShortFormReq.getStageId());

		CommonAppShortFormRes res = new CommonAppShortFormRes();
		res.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);
		res.setEmployeePersPhNameDto(employeePersPhNameDto);
		res.setFceEligibilityDto(fceEligibilityDto);
		res.setUnitDto(unitDto);
		res.setEmployeeSup(employeeSup);
		res.setPersonDto(personDto);
		res.setEthnicity(ethnicity);
		res.setRace(race);
		res.setLegalStatus(legalStatus);
		res.setFceAppDt(fceAppDt);
		res.setEventDto(eventDto);
		res.setCommonApplicationShortFormDto(shortFormDto);
		res.setSplProgDto(splProg);
		res.setPersonLocDto(personLocDto);
		res.setRtnRunaway(rtnRunaway);
		res.setEduSrv(eduSrv);
		res.setPlcmtLog(placementLog);
		res.setSiblingList(siblingList);
		res.setMedList(medList);
		res.setTherapyList(therapyList);
		res.setSubstanceDto(substanceDto);
		res.setPsychiatricList(psychiatricList);
		res.setSexualVictimDto(sexualVictim);
		res.setSexualVictimList(sexualVictimList);
		res.setTraffickingLst(traffickingList);
		res.setSfTraffickingLst(sfTraffickingList);
		res.setEpisodeIncdntDtls(episodeIncdntDtls);
		res.setSfEpisodeIncdntDtls(sfEpisodeIncdntDtls);
		res.setRemovalDto(removalDto);
		res.setRecommendedServicePackageDtlDto(recommendedServicePackageDto);
		return res;

	}

	@RequestMapping(value = "/saveShortForm", headers = { "Accept=application/json" }, method = RequestMethod.POST )
	public @ResponseBody CommonAppShortFormRes saveCommonApplicationForm(@RequestBody CommonAppShortFormReq commonAppShortFormReq) {
		logger.debug("Entering method saveCommonApplicationForm in CommonAppShortFormController");

		CommonAppShortFormRes res =  shortFormService.saveShortForm(commonAppShortFormReq);
		if(ServiceConstants.EVENTSTATUS_COMPLETE.equals(commonAppShortFormReq.getEventStatus())){
			if(ObjectUtils.isEmpty(commonAppShortFormReq.getCaExCommonApplicationId())) {
				if(!ObjectUtils.isEmpty(res.getCommonApplicationShortFormDto())) {
					commonAppShortFormReq.setCaExCommonApplicationId(res.getCommonApplicationShortFormDto().getIdCaExCommonApplication());
				}
			}
			shortFormService.createEventForNotification(commonAppShortFormReq);
		}
		
		return res;
		
	}
	
	@ApiOperation(value = "Get common application short form", tags = { "common-app" })
	@RequestMapping(value = "/getShortFormWithId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonAppShortFormRes getCommonApplicationFormForI(@RequestBody CommonAppShortFormReq commonAppShortFormReq) {
		
		CommonAppShortFormRes res = new CommonAppShortFormRes();
		CommonApplicationShortFormDto commonApplicationShortFormDto = null;
		Long idCaExCommonApplication = null;

		if (commonAppShortFormReq.getCaExCommonApplicationId() != null) {
			// Get CA_EX ida
			commonApplicationShortFormDto = shortFormService.getCommonAppShortFormById(commonAppShortFormReq.getCaExCommonApplicationId());

		} else if (commonAppShortFormReq.getEventId() != null) {
			// Get CA_EX using the event id
			commonApplicationShortFormDto = shortFormService.getCommonAppShortFormByEventId(commonAppShortFormReq.getEventId());

		} else if (commonAppShortFormReq.getStageId() != null) {
			// Get CA_EX using stage id, we may get multiple CA obj, use the most recent.
			throw new InvalidRequestException("Not Yet implemented");

		} else {
			throw new InvalidRequestException(messageSource.getMessage("commonAppShortFormReq.getCaExCommonApplicationId.mandatory", null, Locale.US));
		}

		if(  commonApplicationShortFormDto == null ){
	  	    throw new ResourceNotFoundException( CommonApplicationShortFormDto.class.getSimpleName(), commonAppShortFormReq.getCaExCommonApplicationId()+""  );
  		}else {
  			idCaExCommonApplication = commonApplicationShortFormDto.getIdCaExCommonApplication();
  			ShortFormSpecialProgrammingDto splProg = shortFormService.getSplProgByCaExId(idCaExCommonApplication);
  			ShortFormRtnRunawayDto rtnRunaway = shortFormService.getRtnRunawayByCaExId(idCaExCommonApplication);
  			ShortFormEducationSrvDto eduSrv = shortFormService.getEduSrvByCaExId(idCaExCommonApplication);
  			List<PlacementDtlDto> placementLog = shortFormService.getPlacementLogByExId(idCaExCommonApplication);
  			List<ShortFormSiblingsDto> siblingList = shortFormService.getSiblingsList(commonApplicationShortFormDto.getIdStage(), commonApplicationShortFormDto);
  			List<ShortFormMedicationDto> medList = shortFormService.getMedicationByCaExId(idCaExCommonApplication);
  			List<ShortFormTherapyDto> therapyList = shortFormService.getTherapyDtlByCaExId(idCaExCommonApplication);
  			ShortFormSubstanceDto substanceDto = shortFormService.getSubstanceAbuseByExId(idCaExCommonApplication);
  			List<ShortFormPsychiatricDto> psychiatricList = shortFormService.getHospitalizationByCaExId(idCaExCommonApplication);
  			List<SexualVictimIncidentDto> sexualVictimList = shortFormService.getSFSexualVictimization(idCaExCommonApplication);
  			List<TraffickingDto> sfTraffickingList = shortFormService.getSFTraffickingHistory(idCaExCommonApplication);
  			List<ShortFormCsaEpisodeIncdntsDto> sfEpisodeIncdntDtls = shortFormService.getSFEpisodeIncidents(idCaExCommonApplication);
  			
  			res.setCommonApplicationShortFormDto(commonApplicationShortFormDto);
  			res.setSplProgDto(splProg);
  			res.setRtnRunaway(rtnRunaway);
  			res.setEduSrv(eduSrv);
  			res.setPlcmtLog(placementLog);
  			res.setSiblingList(siblingList);
  			res.setMedList(medList);
  			res.setTherapyList(therapyList);
  			res.setSubstanceDto(substanceDto);
  			res.setPsychiatricList(psychiatricList);
  			res.setSexualVictimList(sexualVictimList);
  			res.setSfTraffickingLst(sfTraffickingList);
  			res.setSfEpisodeIncdntDtls(sfEpisodeIncdntDtls);
			if (res.getSexualVictimDto() != null) { // impossible to reach. Remove if you have testing bandwith to validate.
				res.getSexualVictimDto().setIndUnconfirmedVictimHistory(commonApplicationShortFormDto.getUnconfirmedSexualVictim());
				res.getSexualVictimDto().setIndSexualBehaviorProblem(commonApplicationShortFormDto.getIndSexualBehaviorProblem());
				res.getSexualVictimDto().setTxtBehaviorProblems(commonApplicationShortFormDto.getTxtSexualBehaviorProblem());
			}
  		}
		return res;

	}
	
	@ApiOperation(value = "Approve Common App Short Form", tags = { "common-app" })
	@RequestMapping(value = "/approveCommonAppShortForm", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonAppShortFormRes approveCommonApplicationForm(@RequestBody CommonAppShortFormReq commonAppShortFormReq) {

		CommonApplicationShortFormDto commonApplicationShortFormDto = null;
		
		Long approverId = null;

		if (commonAppShortFormReq.getCaExCommonApplicationId() != null) {
			// Get CA_EX ida
			commonApplicationShortFormDto = shortFormService.getCommonAppShortFormById(commonAppShortFormReq.getCaExCommonApplicationId());

		} else {
			
			throw new InvalidRequestException(messageSource.getMessage("commonAppShortFormReq.mandatory.formId", null, Locale.US));
		}

		if(  commonApplicationShortFormDto == null )
		{
	  	    throw new ResourceNotFoundException( CommonApplicationShortFormDto.class.getSimpleName(), commonAppShortFormReq.getCaExCommonApplicationId()+""  );
  		}
		
		//code added for defect 15353 PROC status 2087ex should not be ack/rejected
		if(commonApplicationShortFormDto.getIdEvent()!=null){
			EventIdOutDto eventDto = shortFormService.getEventDetails(commonApplicationShortFormDto.getIdEvent());
			if(eventDto==null || ServiceConstants.EVENTSTATUS_PROCESS.equalsIgnoreCase(eventDto.getCdEventStatus())){
				throw new InvalidRequestException(ServiceConstants.EVENTSTATUS_PROCESS,new Long(30002));
			}
		}
		
		if(commonAppShortFormReq.getAppproverId() !=null){
			approverId = commonAppShortFormReq.getAppproverId();
		}else{
			throw new InvalidRequestException(messageSource.getMessage("commonAppShortFormReq.mandatory.approverId", null, Locale.US));
		}
		
		CommonAppShortFormRes res =  shortFormService.approveShortForm(commonApplicationShortFormDto, approverId);
		
		return res;

	}
	
	@ApiOperation(value = "Reject Common App Short Form", tags = { "common-app" })
	@RequestMapping(value = "/rejectCommonAppShortForm", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonAppShortFormRes rejectCommonApplicationForm(@RequestBody CommonAppShortFormReq commonAppShortFormReq) {

		CommonApplicationShortFormDto commonApplicationShortFormDto = null;
		
		Long approverId = null;
		
		String rejectReason = null;

		if (commonAppShortFormReq.getCaExCommonApplicationId() != null) {
			// Get CA_EX ida
			commonApplicationShortFormDto = shortFormService.getCommonAppShortFormById(commonAppShortFormReq.getCaExCommonApplicationId());

		} else {
			
			throw new InvalidRequestException(messageSource.getMessage("commonAppShortFormReq.mandatory.formId", null, Locale.US));
		}

		if(  commonApplicationShortFormDto == null )
		{
	  	    throw new ResourceNotFoundException( CommonApplicationShortFormDto.class.getSimpleName(), commonAppShortFormReq.getCaExCommonApplicationId()+""  );
  		}
		
		//code added for defect 15353 PROC status 2087ex should not be ack/rejected
		if(commonApplicationShortFormDto.getIdEvent()!=null){
			EventIdOutDto eventDto = shortFormService.getEventDetails(commonApplicationShortFormDto.getIdEvent());
			if(eventDto==null || ServiceConstants.EVENTSTATUS_PROCESS.equalsIgnoreCase(eventDto.getCdEventStatus())){
				throw new InvalidRequestException(ServiceConstants.EVENTSTATUS_PROCESS,new Long(30002));
			}
		}
		
		if(commonAppShortFormReq.getAppproverId() !=null){
			
			approverId = commonAppShortFormReq.getAppproverId();
		
		}else{
			
			throw new InvalidRequestException(messageSource.getMessage("commonAppShortFormReq.mandatory.approverId", null, Locale.US));
		}
		
		if(commonAppShortFormReq.getRejectReason() !=null){
			
			rejectReason = commonAppShortFormReq.getRejectReason();
		
		}else{
			
			throw new InvalidRequestException(messageSource.getMessage("commonAppShortFormReq.mandatory.rejectReason", null, Locale.US));
		
		}
		
		
		
		CommonAppShortFormRes res =  shortFormService.rejectShortForm(commonApplicationShortFormDto, approverId, rejectReason);
		
		return res;

	}
	
	/**
	 * Method Description: This Service is used to
	 * retrieve the printable common application shortform. This form documents the
	 * historical social, emotional, educational, medical, and family account of
	 * the child from the 2087EX application process by passing IdEvent as input request
	 * 
	 * @param commonAppShortFormReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getCommonApplicationShortForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getCommonApplicationShortForm(@RequestBody CommonAppShortFormReq commonAppShortFormReq) {
		CommonFormRes commonFormRes = new CommonFormRes();
		
		if (ObjectUtils.isEmpty(commonAppShortFormReq.getEventId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		// This service call will populate the prefill data for R2 Impact form architecture
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(shortFormService.getCommonApplShortForm(commonAppShortFormReq)));
		return commonFormRes;
	}

}
