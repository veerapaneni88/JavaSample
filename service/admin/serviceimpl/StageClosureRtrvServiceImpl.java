package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.CloseOpenStageInputDto;
import us.tx.state.dfps.common.dto.CloseOpenStageOutputDto;
import us.tx.state.dfps.common.dto.CloseStageCaseInputDto;
import us.tx.state.dfps.common.dto.CommonDto;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.PlacementValueDto;
import us.tx.state.dfps.common.web.MessagesConstants;
import us.tx.state.dfps.service.admin.dao.CriminalHistoryRecordsCheckDao;
import us.tx.state.dfps.service.admin.dao.EligibilityDao;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dao.EventStageTypeTaskDao;
import us.tx.state.dfps.service.admin.dao.LegalActionEventDao;
import us.tx.state.dfps.service.admin.dao.LegalStatusPersonMaxStatusDtDao;
import us.tx.state.dfps.service.admin.dao.PcaSubsidyDao;
import us.tx.state.dfps.service.admin.dao.PersonPortfolioDao;
import us.tx.state.dfps.service.admin.dao.PlacementActPlannedDao;
import us.tx.state.dfps.service.admin.dao.StageCdDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkRecordDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkStTypeRoleDao;
import us.tx.state.dfps.service.admin.dao.StageSituationDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthDetailNameDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthEventLinkDao;
import us.tx.state.dfps.service.admin.dao.TodoUpdDtTodoCompletedDao;
import us.tx.state.dfps.service.admin.dao.WorkloadStgPerLinkSelDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecordsCheckInDto;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecordsCheckOutDto;
import us.tx.state.dfps.service.admin.dto.EligibilityInDto;
import us.tx.state.dfps.service.admin.dto.EligibilityOutDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeTaskInDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeTaskOutDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventInDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventOutDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusDetailDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.admin.dto.PcaSubsidyOutDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioInDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedInDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusOutDto;
import us.tx.state.dfps.service.admin.dto.StageCdInDto;
import us.tx.state.dfps.service.admin.dto.StageCdOutDto;
import us.tx.state.dfps.service.admin.dto.StageClosureRtrvDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkStTypeRoleInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkStTypeRoleOutDto;
import us.tx.state.dfps.service.admin.dto.StageRegionInDto;
import us.tx.state.dfps.service.admin.dto.StageSituationInDto;
import us.tx.state.dfps.service.admin.dto.StageSituationOutDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthDetailNameInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthDetailNameOutDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkOutDto;
import us.tx.state.dfps.service.admin.dto.TodoUpdDtTodoCompletedInDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelInDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelOutDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventStageStatusService;
import us.tx.state.dfps.service.admin.service.StageClosureRtrvService;
import us.tx.state.dfps.service.adoptionasstnc.service.AdoptionAsstncService;
import us.tx.state.dfps.service.applicationbackground.service.ApplicationBackgroundService;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.service.CaseSummaryService;
import us.tx.state.dfps.service.casepackage.service.PcspService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.StageClosureRtrvReq;
import us.tx.state.dfps.service.common.response.StageClosureRtrvRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.ContactPrincipalsCollateralsDto;
import us.tx.state.dfps.service.contacts.dao.PersonMPSDao;
import us.tx.state.dfps.service.contacts.dao.StagePersonDao;
import us.tx.state.dfps.service.contacts.service.ContactDetailsService;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.financial.dto.LegalStatusValueDto;
import us.tx.state.dfps.service.financial.service.ServiceAuthorizationService;
import us.tx.state.dfps.service.guardianshipdtl.service.GuardianshipDtlService;
import us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao;
import us.tx.state.dfps.service.legalstatus.service.LegalStatusService;
import us.tx.state.dfps.service.pca.dto.PcaAppAndBackgroundDto;
import us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao;
import us.tx.state.dfps.service.person.dao.CriminalHistoryDao;
import us.tx.state.dfps.service.personlistbystage.dao.PersonListByStageDao;
import us.tx.state.dfps.service.personutility.service.PersonUtilityService;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dao.TemporaryAbsenceDao;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.service.PlacementService;
import us.tx.state.dfps.service.stageClosure.service.StageClosureService;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dto.AdoptionSubsidyDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.ServiceAuthorizationDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;
import us.tx.state.dfps.service.workload.service.CloseStageCaseService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * retrieves the Event row and Stage row for a closure event given IdEvent and
 * IdStage Aug 21, 2017- 5:25:28 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Service
@Transactional
public class StageClosureRtrvServiceImpl implements StageClosureRtrvService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	StageDao stageDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	PlacementActPlannedDao placementActPlannedDao;

	@Autowired
	StagePersonLinkStTypeRoleDao stagePersonLinkStTypeRoleDao;

	@Autowired
	EventService eventService;

	@Autowired
	PcspListPlacmtDao pcspListPlacmtDao;

	@Autowired
	CaseSummaryService caseSummaryService;
	// Cinv51dDao
	@Autowired
	WorkloadStgPerLinkSelDao workloadStgPerLinkSelDao;

	// CSES38D
	@Autowired
	EligibilityDao eligibilityDao;
	// CCMN87D
	@Autowired
	EventStagePersonLinkInsUpdDao eventStagePersonLinkInsUpdDao;
	// CSESD6D
	@Autowired
	LegalActionEventDao legalActionEventDao;
	// CLSS84D
	@Autowired
	PlacementDao placementDao;

	// Cses32d
	@Autowired
	LegalStatusPersonMaxStatusDtDao legalStatusPersonMaxStatusDtDao;
	// CCMN44D
	@Autowired
	PersonPortfolioDao personPortfolioDao;

	// CSESA3D
	@Autowired
	EventStageTypeTaskDao eventStageTypeTaskDao;

	// CCMNF6D
	@Autowired
	StageCdDao stageCdDao;

	// CCMNB9D ,CCMNH9D
	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	// CSES78D
	@Autowired
	LegalStatusDao legalStatusDao;

	// CINV39D
	@Autowired
	StagePersonLinkRecordDao stagePersonLinkRecordDao;

	// CSES24D
	@Autowired
	SvcAuthEventLinkDao svcAuthEventLinkDao;

	// CSES23D
	@Autowired
	ServiceAuthorizationDao serviceAuthorizationDao;

	// CLSS24D
	@Autowired
	SvcAuthDetailNameDao svcAuthDetailNameDao;

	// CLSS30D
	@Autowired
	StageSituationDao stageSituationDao;

	// CCMNB8D
	@Autowired
	private StageProgDao stageProgDao;

	// CSESC2D
	@Autowired
	CriminalHistoryRecordsCheckDao criminalHistoryRecordsCheckDao;
	// CSES35D
	@Autowired
	CommonApplicationDao commonApplicationDao;

	// CCMNI8D
	@Autowired
	PcaSubsidyDao pcaSubsidyDao;
	// CAUD47D
	@Autowired
	CloseOpenStageService closeOpenStageService;
	// CINV43D
	@Autowired
	TodoUpdDtTodoCompletedDao todoUpdDtTodoCompletedDao;

	// invalidate Approval
	@Autowired
	ApprovalCommonService approvalCommonService;

	@Autowired
	PostEventStageStatusService postEventStageStatusService;

	@Autowired
	CloseStageCaseService closeStageCaseService;

	@Autowired
	StagePersonDao stagePersonDao;

	@Autowired
	PersonUtilityService personUtilityService;
	@Autowired
	StageClosureService stageClosureService;

	@Autowired
	ServiceAuthorizationService serviceAuthorizationService;

	@Autowired
	PersonMPSDao personMPSDao;

	@Autowired
	PcaAppAndBackgroundDao pcaAppAndBackgroundDao;

	@Autowired
	LegalStatusService legalStatusService;

	@Autowired
	PlacementService placementService;

	@Autowired
	AdoptionAsstncService adoptionAsstncService;

	@Autowired
	ApplicationBackgroundService applicationBackgroundService;

	@Autowired
	GuardianshipDtlService guardianshipDtlService;

	@Autowired
	ContactDetailsService contactDetailsService;

	@Autowired
	PcspService pcspService;
	
	@Autowired
	CriminalHistoryDao criminalHistoryDao;
	
	@Autowired
	private PersonListByStageDao personListByStageDao;
	
	@Autowired
	TemporaryAbsenceDao temporaryAbsenceDao;

	private static final Logger log = Logger.getLogger("ServiceBusiness-StageClosureRtrvService");

	/**
	 * Description:This method retrieves the Event row and Stage row for a
	 * closure event given IdEvent and IdStage
	 * 
	 * @param idStage
	 * @param idEvent
	 * @return StageClosureRtrvDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StageClosureRtrvDto getStageClosureDetails(Long idStage, Long idEvent) {
		log.debug("Entering method callStageClosureRtrvService in StageClosureRtrvServiceImpl");
		StageClosureRtrvDto stageClosureRtrvDto = new StageClosureRtrvDto();
		StageRegionInDto stageRegionInDto = new StageRegionInDto();
		stageClosureRtrvDto.setDtWCDDtSystemDate(new Date());

		stageRegionInDto.setIdStage(idStage);

		StageDto stageDto = stageDao.getStageById(idStage);
		if (!ObjectUtils.isEmpty(stageDto)) {
			stageClosureRtrvDto.setStageDto(stageDto);
		}

		if (!ObjectUtils.isEmpty(idEvent)) {

			EventDto eventDto = eventDao.getEventByid(idEvent);
			if (!ObjectUtils.isEmpty(eventDto)) {
				stageClosureRtrvDto.setEventDto(eventDto);
			}

		}
		Long idPlacementChild = getPlacementChildId(idStage);
		if (!ObjectUtils.isEmpty(idPlacementChild)) {
			String setBindOpenDADDPlcmt = getPlacementActPlannedInDto(idPlacementChild);
			if (!TypeConvUtil.isNullOrEmpty(setBindOpenDADDPlcmt)) {
				stageClosureRtrvDto.setIndOpenDADDPlcmt(setBindOpenDADDPlcmt);
			}
		}
		log.debug("Exiting method callStageClosureRtrvService in StageClosureRtrvServiceImpl");
		return stageClosureRtrvDto;
	}

	/**
	 * 
	 * Method Name: getPlacementActPlannedInDto Method Description:This method
	 * will retrieve the most recent row from placement(LEGACY WAS CallCSES34D)
	 * table
	 * 
	 * @param idPerson
	 * @return
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getPlacementActPlannedInDto(Long idPerson) {
		log.debug("Entering method getPlacementActPlannedInDto in StageClosureRtrvServiceImpl");
		PlacementActPlannedInDto placementActPlannedInDto = new PlacementActPlannedInDto();
		placementActPlannedInDto.setIdPlcmtChild(idPerson);
		List<PlacementActPlannedOutDto> placementActPlannedOutDtos = placementActPlannedDao
				.getPlacementRecord(placementActPlannedInDto);
		String indOpenDADDPlcmt = null;
		if (CollectionUtils.isNotEmpty(placementActPlannedOutDtos)) {
			PlacementActPlannedOutDto placementActPlannedOutDto = placementActPlannedOutDtos.get(0);
			if ((TypeConvUtil.isNullOrEmpty(placementActPlannedOutDto.getDtPlcmtEnd()) || 
					ServiceConstants.MAX_DATE.compareTo(placementActPlannedOutDto.getDtPlcmtEnd())==0)
					&& ServiceConstants.OPEN_NON_FPS_PAID_PLCMT
							.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtType())
					&& !ServiceConstants.PRIV_AGCY_ADPT_HM
							.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())) {
				indOpenDADDPlcmt = ServiceConstants.STRING_IND_Y;
			} else if ((TypeConvUtil.isNullOrEmpty(placementActPlannedOutDto.getDtPlcmtEnd()) || 
					ServiceConstants.MAX_DATE.compareTo(placementActPlannedOutDto.getDtPlcmtEnd())==0)
					&& !ServiceConstants.ADOPTIVE_PLACEMENT
							.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
					&& !ServiceConstants.PRIV_AGCY_ADPT_HM.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
					&& (ServiceConstants.OPEN_PLCMT_DA.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
					|| ServiceConstants.OPEN_PLCMT_DD.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
					|| ServiceConstants.OPEN_PLCMT_DG.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
					|| ServiceConstants.OPEN_PLCMT_DF.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
					|| ServiceConstants.OPEN_PLCMT_DQ.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
					|| ServiceConstants.OPEN_PLCMT_DR.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
					|| ServiceConstants.OPEN_PLCMT_DE.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))) {
				indOpenDADDPlcmt = ServiceConstants.STRING_IND_Y;
			} else {
				indOpenDADDPlcmt = ServiceConstants.STRING_IND_N;
			}
		}
		log.debug("Exiting method CallCSES34D in StageClosureRtrvServiceImpl");
		return indOpenDADDPlcmt;
	}

	/**
	 * 
	 * Method Name: getPlacementActPlannedOutDto Method Description:This method
	 * will retrieve the most recent row from placement(LEGACY WAS CallCSES34D)
	 * table
	 * 
	 * @param idPerson
	 * @return PlacementActPlannedOutDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PlacementActPlannedOutDto getPlacementActPlannedOutDto(Long idPerson) {
		log.debug("Entering method getPlacementActPlannedInDto in StageClosureRtrvServiceImpl");

		PlacementActPlannedOutDto placementActPlannedOutDto = new PlacementActPlannedOutDto();
		if (!ObjectUtils.isEmpty(idPerson)) {
			PlacementActPlannedInDto placementActPlannedInDto = new PlacementActPlannedInDto();
			placementActPlannedInDto.setIdPlcmtChild(idPerson);

			List<PlacementActPlannedOutDto> placementActPlannedOutDtos = placementActPlannedDao
					.getPlacementRecord(placementActPlannedInDto);

			if (CollectionUtils.isNotEmpty(placementActPlannedOutDtos)) {
				placementActPlannedOutDto = placementActPlannedOutDtos.get(0);

			}
		}
		log.debug("Exiting method getPlacementActPlannedInDto in StageClosureRtrvServiceImpl");
		return placementActPlannedOutDto;
	}

	/**
	 * 
	 * Method Name: getPlacementChildId Method Description:This method gets the
	 * person ID.(LEGACY WAS CallCINT20D)
	 * 
	 * @param idStage
	 * @return
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getPlacementChildId(Long idStage) {
		log.debug("Entering method getPlacementChildId in StageClosureRtrvServiceImpl");
		StagePersonLinkStTypeRoleInDto stagePersonLinkStTypeRoleInDto = new StagePersonLinkStTypeRoleInDto();

		stagePersonLinkStTypeRoleInDto.setIdStage(idStage);

		stagePersonLinkStTypeRoleInDto.setCdStagePersRole(ServiceConstants.PERSON_ROLE_PRIM_CHILD);

		stagePersonLinkStTypeRoleInDto.setCdStagePersType(ServiceConstants.PRINCIPAL);

		List<StagePersonLinkStTypeRoleOutDto> stagePersonLinkStTypeRoleOutDtos = stagePersonLinkStTypeRoleDao
				.stagePersonDtls(stagePersonLinkStTypeRoleInDto);
		Long idPlacementChild = null;
		if (CollectionUtils.isNotEmpty(stagePersonLinkStTypeRoleOutDtos)) {
			idPlacementChild = stagePersonLinkStTypeRoleOutDtos.get(0).getIdPerson();
		}
		log.debug("Exiting method getPlacementChildId in StageClosureRtrvServiceImpl");
		return idPlacementChild;
	}

	/**
	 * 
	 * Method Name: getPlacementChildId Method Description:This method gets the
	 * StageClosure page Details ( consolidated Service)
	 * 
	 * @param idStage
	 * @param cdTask
	 * @return StageClosureRtrvRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StageClosureRtrvRes getStageClosurePageDetails(Long idStage, String cdTask) {
		StageClosureRtrvRes stageClosureRtrvRes = new StageClosureRtrvRes();
		// getting event using satgeId and taskCode

		EventDto eventDto = new EventDto();
		List<EventDto> eventDtos = eventDao.getEventByStageIDAndTaskCode(idStage, cdTask);
		if (CollectionUtils.isNotEmpty(eventDtos)) {
			eventDto = eventDtos.get(0);

		}
		// fetch Stage Closure details and set it in the response
		StageClosureRtrvDto stageclosureRtrvDto = getStageClosureDetails(idStage, eventDto.getIdEvent());
		stageClosureRtrvRes.setStageclosureRtrvDto(stageclosureRtrvDto);
		// get the Check out Status and set it in response
		stageClosureRtrvRes.setCaseCheckoutStatus(stageDao.getCaseStageCheckoutStatus(idStage));
		// if event status is complete then check event is submitted for
		// approval , and set the same value in response

		if (!ObjectUtils.isEmpty(stageclosureRtrvDto.getEventDto())
				&& ServiceConstants.STATUS_COMP.equals(stageclosureRtrvDto.getEventDto().getCdEventStatus())) {
			stageClosureRtrvRes.setSubmittedForApproval(pcspListPlacmtDao
					.setHasBeenSubmittedForApprovalCps(stageclosureRtrvDto.getEventDto().getIdEvent()));
		}

		return stageClosureRtrvRes;
	}

	/*
	artf188936: check for eligibility 1. If stage type does not start with C- (existing condition) 
	2. or if the stage is 'SUB' and stage type is one of the 9. (new condition)	 */
	private boolean checkSubStageTypes(StageClosureRtrvDto stageClosureRtrvDto){
		boolean subStageFlag = false;
		if (   !(CASE_REL_SPEC_REQ.equals(stageClosureRtrvDto.getStageDto().getCdStageType().substring(0, 2)))
			 || ( 	 STAGE_SUBCARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())
				  && (    CASE_REL_SPEC_JPC_REQ.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageType())
				       || CASE_REL_SPEC_IC_REQ.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageType())
				       || CASE_REL_SPEC_TYC_REQ.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageType())
				       || CASE_REL_SPEC_PB_REQ.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageType())
				       || CASE_REL_SPEC_RC_REQ.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageType())
				       || CASE_REL_SPEC_OS_REQ.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageType())
				       || CASE_REL_SPEC_CO_REQ.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageType())
				       || CASE_REL_SPEC_AS_REQ.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageType())
				       || CASE_REL_SPEC_ICA_REQ.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageType()))))
		{
			subStageFlag = true;
		}
		return subStageFlag;
	}

	/**
	 * Method Name: saveStageClosure Method Description: This Method Runs all
	 * the validations , if no error found then it will save the info , this
	 * method gets called for save , save and Sumbit , and Approval Status
	 * 
	 * @param stageClosureRtrvReq
	 * @return StageClosureRtrvRes
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StageClosureRtrvRes saveStageClosure(StageClosureRtrvReq stageClosureRtrvReq) {

		String tempCdStageOpen = ServiceConstants.EMPTY_STRING;
		Date dtDtSystemDate = new Date();
		long lRC = 0;
		boolean indSvcOpen = false;
		boolean svcAuthsToProgress = false;
		boolean eligibleStageFound = false;
		boolean fceEligEventExists = false;
		boolean alocEventExists = false;
		boolean blocEventExists = false;
		boolean indFPSPaidPlcmtExists = false;
		boolean caseLegalStsFound = false;
		boolean otherFamOpen = false;
		boolean subStageOpen = false;
		boolean indOpenSubAdo = false;
		long fYears = 0;
		StageClosureRtrvRes stageClosureRtrvRes = new StageClosureRtrvRes();
		List<StageCdOutDto> stageCdOutDtos = new ArrayList<>();
		// pInputMsg
		StageClosureRtrvDto stageClosureRtrvDto = stageClosureRtrvReq.getStageClosureDto();
		// pOutputMsg
		StageClosureRtrvDto stageClosureRtrvOutDto = new StageClosureRtrvDto();
		List<LegalStatusPersonMaxStatusDtOutDto> legalStatusPersonMaxStatusDtOutDtos = null;

		String tempCdStageClosureReason = stageClosureRtrvDto.getStageDto().getCdStageReasonClosed();
		List<ErrorDto> errorDtos = new ArrayList<>();
		
		boolean validationResult = validationForSexualVctmizationQues(stageClosureRtrvDto);
		if(!validationResult) {
			addError(MSG_COMPLETE_SEX_VIC_QN, errorDtos);
		}
		
		Long idPerson = null;
		if (!ServiceConstants.STRING_IND_Y.equals(stageClosureRtrvDto.getSysIndCase())) {
			// getROWCSUB68SIG01 is StageDTo
			if (!STAGE_FAM_REUN.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())
					&& !STAGE_FAM_SUBCARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())
					&& (checkSubStageTypes(stageClosureRtrvDto)
							|| STAGE_POST_ADOPT.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())
							|| STAGE_PERM_CARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))) {
				// CallCINV51D
				idPerson = getPersonId(stageClosureRtrvDto.getStageDto().getIdStage(),
						stageClosureRtrvDto.getStageDto().getCdStageProgram());

			}
			switch (stageClosureRtrvDto.getStageDto().getCdStage().substring(0, 1)) {
			case SUBCARE:
				String eventStatus = getEventStatus(stageClosureRtrvDto.getStageDto().getIdStage());// CallCSVC34D

				if (!TypeConvUtil.isNullOrEmpty(eventStatus)) {
					addError(MSG_DAY_CARE_REQ_PEND, errorDtos);
				}
				// CallCSES38D
				EligibilityOutDto eligibilityOutDto = getEligibility(idPerson);
				/*
				 * The message MSG_STG_CLOS_SUB_A ("Eligibility must be ended.")
				 * should only be put in the error list window if the end date
				 * for eligibility is MAX_DATE or NULL
				 * 
				 */
				if (!ObjectUtils.isEmpty(eligibilityOutDto)) {

					fceEligEventExists = true;
					/*
					 * Foster Care Eligibility event exists. Set indicator
					 * accordingly so that it can be used later for edit checks.
					 */

					if (dateIsEmpty(eligibilityOutDto.getDtEligEnd())) {
						addError(ServiceConstants.MSG_STG_CLOS_SUB_A, errorDtos);
					}
				}
				
				//added for PPM 65209
				if(openTAExists(stageClosureRtrvDto.getStageDto().getIdStage())){
					addError(ServiceConstants.MSG_TA_SUB_STAGE_CLOSE, errorDtos);
				}

				if (!fceEligEventExists) {
					EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
					eventStagePersonLinkInsUpdInDto.setCdTask(FCE_ELIG_TASK);
					eventStagePersonLinkInsUpdInDto.setCdEventType(FCE_ELIG_TYPE);
					// CallCCMN87D
					List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpdOutDtoList = eventStagePersonLink(
							stageClosureRtrvDto.getStageDto().getIdStage(), eventStagePersonLinkInsUpdInDto);
					if (CollectionUtils.isNotEmpty(eventStagePersonLinkInsUpdOutDtoList)) {
						/*
						 * If the event status is not COMP, then there's an open
						 * eligibility. Display error message.
						 *
						 * If COMP Foster Care Eligibility event exists, set
						 * indicator accordingly so that it can be used later
						 * for other edit checks.
						 */

						Optional<EventStagePersonLinkInsUpdOutDto> dtoOption = eventStagePersonLinkInsUpdOutDtoList
								.stream().filter(dto -> STATUS_COMPLETE.equalsIgnoreCase(dto.getCdEventStatus()))
								.findFirst();
						if (dtoOption.isPresent()) {
							fceEligEventExists = true;
						} else {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_A, errorDtos);
						}

					}

				}

				if (!CASE_REL_SPEC_REQ
						.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageType().substring(0, 2))) {
					// CSES34DOutRec
					/*
					 * Check PLACEMENT
					 */
					PlacementActPlannedOutDto placementActPlannedOutDto = getPlacementActPlannedOutDto(idPerson);
					/*
					 * An edit check for any subcare closure reason except
					 * "Adoptive Placement - 060" has been added. The edit
					 * (MSG_STG_CLOS_SUB_C) will appear if there is an open
					 * placement (no removal date) of type "PRS Contracted Care"
					 * or "PRS Foster/Adoptive Home". The subcare closure reason
					 * of "Adoptive Placement" is excluded from this edit check.
					 *
					 * SIR #21038 - Added an edit check for the SUB closure
					 * reason "Adoptive Placement." If the SUB closure reason is
					 * "Adoptive Placement", the current living arrangement for
					 * the PC should be Adoptive Placement.
					 */

					if (!ObjectUtils.isEmpty(placementActPlannedOutDto)) {
						/*
						 * If the childs current Subcare Closure Reason is
						 * "Adoptive Placement" then do not perform the edit
						 * check. Else begin edit check for MSG_STG_CLOS_SUB_C.
						 *
						 * 
						 * check if the SUB closure reason is "Adoptive
						 * Placement." The most recent placement must be an
						 * Adoptive Placement (Living Arragement of Adoptive
						 * Placement) if the SUB closure reason is "Adoptive
						 * Placement."
						 */
						if (SEC_POS_CLOSE_REAS_SIX.equals(
								String.valueOf(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed().charAt(1)))
								&& TypeConvUtil.isNullOrEmpty(placementActPlannedOutDto.getDtPlcmtEnd())
								&& !ADOPTIVE_PLACEMENT.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
								&& !PRIV_AGCY_ADPT_HM.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())) {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_110, errorDtos);
						} else if (dateIsEmpty(placementActPlannedOutDto.getDtPlcmtEnd())
								&& !ADOPTIVE_PLACEMENT.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
								&& !PRIV_AGCY_ADPT_HM.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
								&& (PLCMT_TYPE_PRS_CON.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtType())
										|| PLCMT_TYPE_PRS_FAD
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtType())
										|| PLCMT_TYPE_UNAUTH
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtType())
										|| PLCMT_TYPE_TYC.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtType())
										|| PLCMT_TYPE_JPC
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtType()))) {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_C, errorDtos);
						} else if ((KIN_SHIP_NON_LIC.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtType()))
								&& (COU_ORD_REL_KIN.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| REL_HOME.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| COU_ORD_FIC_KIN
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| FIC_KIN_HOME
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))) {

							// pCSESD6DOutputRec
							//Modified the logic below from subtype to legal outcome for Defect#10648
							LegalActionEventOutDto legalActionEventOutDto = null;
							if (!ObjectUtils.isEmpty(idPerson)) {
								legalActionEventOutDto = legalActionEventDao.getCCORCCVSLegalAction(idPerson,
										stageClosureRtrvDto.getStageDto().getIdCase(),
										stageClosureRtrvDto.getStageDto().getIdStage());
							}
							if (!ObjectUtils.isEmpty(legalActionEventOutDto)) {
								if ((COU_ORD_REL_KIN.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| REL_HOME.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))
										&& !ObjectUtils.isEmpty(placementActPlannedOutDto.getDtPlcmtEnd())) {
									if (!ObjectUtils.isEmpty(legalActionEventOutDto.getCdLegalActOutcome())
											&& !LGL_OTCM_PMC_REL
													.equalsIgnoreCase(legalActionEventOutDto.getCdLegalActOutcome())
											&& !LGL_OTCM_NON_SUIT.equalsIgnoreCase(
													legalActionEventOutDto.getCdLegalActOutcome())) {
										addError(ServiceConstants.MSG_SUB_NO_PMC_REL_LEGAL_ACTION, errorDtos);
									}
								}

								if ((COU_ORD_FIC_KIN.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| FIC_KIN_HOME.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))
										&& !ObjectUtils.isEmpty(placementActPlannedOutDto.getDtPlcmtEnd())) {
									if (!ObjectUtils.isEmpty(legalActionEventOutDto.getCdLegalActOutcome())
											&& !LGL_OTCM_PMC_FCTV_KIN
													.equalsIgnoreCase(legalActionEventOutDto.getCdLegalActOutcome())
											&& !LGL_OTCM_NON_SUIT.equalsIgnoreCase(
													legalActionEventOutDto.getCdLegalActOutcome())) {
										addError(ServiceConstants.MSG_SUB_NO_PMC_OTH_LEGAL_ACTION, errorDtos);
									}
								}
							} else {

								// case SQL_NOT_FOUND:
								// Fix for defect 11601 - SUB Stage requiring Legal Action regardless of closure
								// reason
								if ((COU_ORD_REL_KIN.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| REL_HOME.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))
										&& ObjectUtils.isEmpty(placementActPlannedOutDto.getDtPlcmtEnd())) {
									addError(ServiceConstants.MSG_SUB_NO_PMC_REL_LEGAL_ACTION, errorDtos);
								}

								if ((COU_ORD_FIC_KIN.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| FIC_KIN_HOME.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))
										&& ObjectUtils.isEmpty(placementActPlannedOutDto.getDtPlcmtEnd())) {
									addError(ServiceConstants.MSG_SUB_NO_PMC_OTH_LEGAL_ACTION, errorDtos);
								}

							}
						}

					}

					// call CSES35D
					/*
					 * Check for ALOC event. ALOC event must exist if FPS paid
					 * placement (anything but 'Non-Certified Person' (010) or
					 * 'Non-FPS Paid' (040)) exists for the stage.
					 */
					PersonLocDto personLocDto = null;
					if (!ObjectUtils.isEmpty(idPerson)) {
						if (!ObjectUtils.isEmpty(
								commonApplicationDao.getPersonLocDtls(idPerson, ServiceConstants.CD_PLOC_TYPE))) {
							personLocDto = commonApplicationDao
									.getPersonLocDtls(idPerson, ServiceConstants.CD_PLOC_TYPE).get(0);
						}
					}

					if (!ObjectUtils.isEmpty(personLocDto) && !ObjectUtils.isEmpty(personLocDto.getCdPlocType())) {
						alocEventExists = true;
					} else {
						alocEventExists = false;
					}

					if (!ObjectUtils.isEmpty(idPerson)) {
						if (!ObjectUtils.isEmpty(
								commonApplicationDao.getPersonLocDtls(idPerson, ServiceConstants.CPLOCELG_BLOC))) {
							personLocDto = commonApplicationDao
									.getPersonLocDtls(idPerson, ServiceConstants.CPLOCELG_BLOC).get(0);
						}
					}
					if (!ObjectUtils.isEmpty(personLocDto) && !ObjectUtils.isEmpty(personLocDto.getCdPlocType())) {
						/*
						 * BLOC event exists. Set indicator accordingly so it
						 * can be used later for edit checks.
						 */
						blocEventExists = true;
						/*
						 * If PlocEnd Date is null, then a billing record still
						 * exits, post the message to the errorDtos
						 */
						if (dateIsEmpty(personLocDto.getDtPlocEnd())) {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_B, errorDtos);

						}
					} else {

						blocEventExists = false;
					}
					/*
					 * Check all placements in this stage for an FPS paid
					 * placement (anything but 'Non-Certified Person' (010) or
					 * 'Non-FPS Paid' (040) or 'Unauthorized Placement' (080)
					 * (SIR 23067). REEDLG SIR 23067 - check also for
					 * 'Unauthorized Placement' (080)
					 */
					// CallCLSS84D
					List<PlacementDto> placementDtoList = placementDao
							.getActualPlacement(stageClosureRtrvDto.getStageDto().getIdStage());
					if (CollectionUtils.isNotEmpty(placementDtoList)) {
						indFPSPaidPlcmtExists = false;

						Optional<PlacementDto> placementDtoOption = placementDtoList.stream()
								.filter(dto -> !PLCMT_TYPE_NON_FPS_PAID.equalsIgnoreCase(dto.getCdPlcmtType())
										&& !PLCMT_TYPE_NON_CERT_PERSON.equalsIgnoreCase(dto.getCdPlcmtType())
										&& !KIN_SHIP_NON_LIC.equalsIgnoreCase(dto.getCdPlcmtType())
										&& !PLCMT_TYPE_UNAUTH.equalsIgnoreCase(dto.getCdPlcmtType()))
								.findFirst();

						if (placementDtoOption.isPresent()) {
							indFPSPaidPlcmtExists = true;

						}
					} else {

						// case SQL_NOT_FOUND:
						indFPSPaidPlcmtExists = false;
					}
					/*
					 * Before closing the SUB stage, if an FPS paid placement
					 * (anything but 'Non-Certified Person' (010) or 'Non-FPS
					 * Paid' (040)) exists in the stage, stage must have ALOC,
					 * BLOC and Foster Care Eligibility events so the resource
					 * can be paid. If these don't exist, post the edit.
					 * 'Unauthorized Placement' (080) to edit exceptions.
					 */
					if (indFPSPaidPlcmtExists) {
						if (!alocEventExists) {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_ALOC_REQ, errorDtos);
						}
						if (!blocEventExists) {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_BLOC_REQ, errorDtos);
						}
						if (!fceEligEventExists) {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_FCE_REQ, errorDtos);
						}
					}
					/*
					 * Switch on the 1st number of the Closure Reason to see
					 * what other edit checks need to be performed
					 */
					switch (stageClosureRtrvDto.getStageDto().getCdStageReasonClosed().substring(1, 2)) {
					case (SEC_POS_CLOSE_REAS_ONE):
					case (SEC_POS_CLOSE_REAS_TWO):
					case (SEC_POS_CLOSE_REAS_THREE):
					case (SEC_POS_CLOSE_REAS_EIGHT):
						/*
						 ** Perform Edit Check for Legal Status
						 */
						legalStatusPersonMaxStatusDtOutDtos = getLegalStatusPersonMax(idPerson); // CallCSES32D
						if (CollectionUtils.isNotEmpty(legalStatusPersonMaxStatusDtOutDtos)) {
							// CSES32DOutRec
							/*
							 * If LegalStaus is not PRS Responsibility
							 * Terminated and Closure Reason is Non Suit or PMC
							 * To Other, then add MSG_STG_CLOS_SUB_020 to the
							 * error array
							 */
							//artf227805
							LegalStatusPersonMaxStatusDtOutDto legalStatusPersonMaxStatusDtOutDto = legalStatusPersonMaxStatusDtOutDtos
									.get(0);
							if ((!PRS_RESP_TERM
									.equalsIgnoreCase(legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus()))
									&& ((NON_SUIT.equalsIgnoreCase(
									stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))
									|| (PMC_TO_OTHER.equalsIgnoreCase(
									stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))
									|| (EXIT_TO_TRIBE.equalsIgnoreCase(
									stageClosureRtrvDto.getStageDto().getCdStageReasonClosed())))) {
								addError(ServiceConstants.MSG_STG_CLOS_SUB_020, errorDtos);

							}
							/*
							 ** If Legal Staus is not CVS Not Obtained and
							 * Closure Reason is CVS Not Obtained, place
							 ** MSG_STG_CLOS_SUB_030 in the Error Array
							 */

							if ((!CVS_NOT_OBTAINED
									.equalsIgnoreCase(legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus()))
									&& (REAS_CVS_NOT_OBT.equalsIgnoreCase(
											stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))) {
								addError(ServiceConstants.MSG_STG_CLOS_SUB_030, errorDtos);

							}
							/*
							 * If Legal Status is not Child Emancipated and
							 * Closure Reason is Child Emacipated, place
							 * MSG_STG_CLOS_SUB_080 in the Error Array
							 */

							if ((!CHILD_EMANCIPATED
									.equalsIgnoreCase(legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus()))
									&& (REAS_EMANCIPATED.equalsIgnoreCase(
											stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))) {
								addError(ServiceConstants.MSG_STG_CLOS_SUB_080, errorDtos);

							}
						} else {

							addError(ServiceConstants.MSG_LEGAL_STAT_NOT_FOUND, errorDtos);
						}
						/*
						 * checking the RunAways completed for closed reason CVS
						 * not obtained
						 */
						if ((REAS_CVS_NOT_OBT
								.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))
								&& stageClosureService.getRunAwayStatus(idPerson)) {

							addError(ServiceConstants.MSG_RUNAWAY_COMPLETED, errorDtos);

						}

						break;
					case (SEC_POS_CLOSE_REAS_FOUR):
						/*
						 * SUB stage closure to prevent user from selecting
						 * closure reason of 'Child Returned Home' if legal
						 * status is 'Adoption Consummated'
						 */

						if (ObjectUtils.isEmpty(legalStatusPersonMaxStatusDtOutDtos)) {
							legalStatusPersonMaxStatusDtOutDtos = getLegalStatusPersonMax(idPerson);// CallCSES32D
						}
						if (CollectionUtils.isNotEmpty(legalStatusPersonMaxStatusDtOutDtos)) {
							// CSES32DOutRec
							LegalStatusPersonMaxStatusDtOutDto legalStatusPersonMaxStatusDtOutDto = legalStatusPersonMaxStatusDtOutDtos
									.get(0);
							if (ADOPTION_CONSUM
									.equalsIgnoreCase(legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus())) {
								addError(ServiceConstants.MSG_STG_CLOS_SUB_120, errorDtos);
							}

						}

					case (SEC_POS_CLOSE_REAS_FIVE):
					case (SEC_POS_CLOSE_REAS_SEVEN):

						/*
						 * If the childs current Living Arrangement is not Own
						 * Home and the closure reason is Placed in Own Home
						 * then add MSG_STG_CLOS_SUB_040 to ErrorArray
						 */
						/*
						 * If Living Arr is 'Non-Custodial Parent's home' then
						 * do not display the error message. Allow submission
						 * for approval.
						 */
						if ((!OWN_HOME.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())
								&& !NON_CUST_PARENT_HM.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))
								&& (REAS_OWN_HOME.equalsIgnoreCase(
										stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))) {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_040, errorDtos);
						}
						/*
						 * If the child's current Living Arrangement is not
						 * Relatives Home and none of the Placement Info is
						 * OWN_HOME and the Closure Reason is Placed in
						 * Relatives Home, add MSG_STG_CLOS_SUB_050 to the Error
						 * Array
						 */

						if (((!RELATIVES_HOME.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))
								&& (!COU_ORD_REL_KIN.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())))
								&& ((!REAS_OWN_HOME.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtInfo1()))
										|| (!REAS_OWN_HOME
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtInfo2()))
										|| (!REAS_OWN_HOME
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtInfo3()))
										|| (!REAS_OWN_HOME
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtInfo4()))
										|| (!REAS_OWN_HOME
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtInfo5()))
										|| (!REAS_OWN_HOME
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtInfo6()))
										|| (!REAS_OWN_HOME
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtInfo7())))
								&& (REAS_REL_HOME.equalsIgnoreCase(
										stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))) {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_050, errorDtos);
						}
						/*
						 * If the Placement Removal Reason is not Child Ran Away
						 * and the Closure Reason is Child Ran Away, place
						 * MSG_STG_CLOS_SUB_070 in the ErrorAway
						 */

						if ((!CHILD_RAN_AWAY.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtRemovalRsn()))
								&& (REAS_RAN_AWAY.equalsIgnoreCase(
										stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))) {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_070, errorDtos);
						}
						/*
						 * if stage closure reason either
						 * SEC_POS_CLOSE_REAS_FOUR (040) or
						 * SEC_POS_CLOSE_REAS_FIVE (050), and if no open ADO
						 * placement exists, make sure a terminating legal
						 * status (090,100,120,150) exists, If no terminating
						 * legal status exists, add error message to message
						 * array.
						 */

						if ((!REAS_RAN_AWAY
								.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))
								&& (!ObjectUtils.isEmpty(placementActPlannedOutDto.getDtPlcmtEnd())
										&& (!ADOPTIVE_PLACEMENT
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))
										&& (!PRIV_AGCY_ADPT_HM
												.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr())))) {

							if (ObjectUtils.isEmpty(legalStatusPersonMaxStatusDtOutDtos)) {
								legalStatusPersonMaxStatusDtOutDtos = getLegalStatusPersonMax(idPerson);// CallCSES32D
							}
							if (CollectionUtils.isNotEmpty(legalStatusPersonMaxStatusDtOutDtos)) {
								// CSES32DOutRec
								LegalStatusPersonMaxStatusDtOutDto legalStatusPersonMaxStatusDtOutDto = legalStatusPersonMaxStatusDtOutDtos
										.get(0);
								if ((!ADOPTION_CONSUM
										.equalsIgnoreCase(legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus()))
										&& (!CHILD_EMANCIPATED.equalsIgnoreCase(
												legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus()))
										&& (!PRS_RESP_TERM.equalsIgnoreCase(
												legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus()))
										&& (!CVS_NOT_OBTAINED.equalsIgnoreCase(
												legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus()))) {
									addError(ServiceConstants.MSG_STG_CLOS_SUB_LEGSTAT, errorDtos);
								}
							} else {

								addError(ServiceConstants.MSG_STG_CLOS_SUB_LEGSTAT, errorDtos);
							}
						}

						break;
					case (SEC_POS_CLOSE_REAS_SIX):
						CommonDto commonDto = new CommonDto();
						commonDto.setIdCase(stageClosureRtrvDto.getStageDto().getIdCase());
						commonDto.setIdStage(stageClosureRtrvDto.getStageDto().getIdStage());
						commonDto.setCdStage(STAGE_ADOPTION);
						// CallCMSC09D
						Long subCareCount = placementDao.getCountSubcareStage(commonDto);
						/*
						 * If no rows were found, add MSG_STG_CLOS_SUB_060 to
						 * the ErrorDtos
						 */
						if (ObjectUtils.isEmpty(subCareCount) || subCareCount == 0L) {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_060, errorDtos);
						}
						/*
						 * If the childs current Subcare Closure Reason is
						 * "Adoptive Placement" then do not perform the edit
						 * check. Else begin edit check for MSG_STG_CLOS_SUB_C.
						 *
						 * added some logic to if statement, in order to account
						 * for PRIV_AGCY_ADPT_HM living arrangement.
						 */

						if (!ObjectUtils.isEmpty(placementActPlannedOutDto)
								&& dateIsNotEmpty(placementActPlannedOutDto.getDtPlcmtEnd())
								&& (!ADOPTIVE_PLACEMENT.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))
								&& (!PRIV_AGCY_ADPT_HM
										.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))) {
							/*
							 * Display edit check message: "Most recent
							 * placement must be Adoptive Placement."
							 */
							addError(ServiceConstants.MSG_STG_CLOS_SUB_110, errorDtos);
						}

						break;
					case (SEC_POS_CLOSE_REAS_NINE):
						List<PersonPortfolioOutDto> personPortfolioOutDtos = getPersonPortfolio(idPerson);// CallCCMN44D
						if (CollectionUtils.isNotEmpty(personPortfolioOutDtos)) {
							// CCMN44DOutRec
							/*
							 * if a child is aged out, you don't need a permancy
							 * goal of Long Term Care or independent living.
							 */

							/*
							 * Perform Edit Check for Person Age
							 */
							PersonPortfolioOutDto personPortfolioOutDto = personPortfolioOutDtos.get(0);
							/*
							 * We need to calculate the age of the person based
							 * on their birth date and today's date Compare
							 * today's date to child's date of birth
							 */

							// lRC =
							// dtDtSystemDate.compareTo(personPortfolioOutDto.getDtPersonBirth());//
							// TODO

							fYears = DateUtils.calculatePersonsAgeInYears(personPortfolioOutDto.getDtPersonBirth(),
									dtDtSystemDate);
							if (fYears < 18) {
								addError(ServiceConstants.MSG_STG_CLOS_ADO_040, errorDtos);
							}

						}

						break;
					case (SEC_POS_CLOSE_REAS_ZERO):
						personPortfolioOutDtos = getPersonPortfolio(idPerson);// CallCCMN44D
						if (CollectionUtils.isNotEmpty(personPortfolioOutDtos)) {
							// CCMN44DOutRec
							/*
							 ** We need to make sure that the date of death has
							 ** been entered in the Person Detail. If it has not,
							 * we place MSG_STG_CLOS_SUB_100 in the errorDtos.
							 */
							PersonPortfolioOutDto personPortfolioOutDto = personPortfolioOutDtos.get(0);
							if (dateIsEmpty(personPortfolioOutDto.getDtPersonDeath())) {
								addError(ServiceConstants.MSG_STG_CLOS_SUB_100, errorDtos);
							}

						}
						break;
					default:

						break;

					}

					if ((REAS_RAN_AWAY.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))
							|| SUB_CHILD_DEATH
									.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed())
							|| SUB_AGED_OUT
									.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed())) {
						if (ObjectUtils.isEmpty(legalStatusPersonMaxStatusDtOutDtos)) {
							legalStatusPersonMaxStatusDtOutDtos = getLegalStatusPersonMax(idPerson);// CallCSES32D
						}
						/*
						 ** If LegalStaus is not PRS Responsibility Terminated
						 * and the user selects Child Ran Away, Child Death, or
						 * Aged Out the stage should not be closed
						 */
						checkFPSTerminated(legalStatusPersonMaxStatusDtOutDtos, errorDtos);
					}

				}

				break;/* end case(SUBCARE) */
			case (ADO_AOC):
				if (!CASE_REL_SPEC_REQ
						.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageType().substring(0, 2))) {
					switch (stageClosureRtrvDto.getStageDto().getCdStage().substring(1, 2)) {
					case (SEC_POS_CD_STAGE_O):

					case (SEC_POS_CD_STAGE_D):
						if (REAS_AGED_OUT.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed())
								|| REAS_CHILD_DEATH
										.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed())) {
							if (ObjectUtils.isEmpty(legalStatusPersonMaxStatusDtOutDtos)) {
								legalStatusPersonMaxStatusDtOutDtos = getLegalStatusPersonMax(idPerson);// CallCSES32D
							}
							// check About Legal Status's
							checkFPSTerminated(legalStatusPersonMaxStatusDtOutDtos, errorDtos);
						}

						switch (stageClosureRtrvDto.getStageDto().getCdStageReasonClosed().substring(1, 2)) {
						case (SEC_POS_CLOSE_REAS_ONE):
						case (SEC_POS_CLOSE_REAS_THREE):

							if (ObjectUtils.isEmpty(legalStatusPersonMaxStatusDtOutDtos)) {
								legalStatusPersonMaxStatusDtOutDtos = getLegalStatusPersonMax(idPerson);// CallCSES32D
							}
							if (CollectionUtils.isNotEmpty(legalStatusPersonMaxStatusDtOutDtos)) {
								// CSES32DOutRec
								LegalStatusPersonMaxStatusDtOutDto legalStatusPersonMaxStatusDtOutDto = legalStatusPersonMaxStatusDtOutDtos
										.get(0);
								if ((!PRS_RESP_TERM
										.equalsIgnoreCase(legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus()))
										&& (ADO_PMC_TO_OTHER.equalsIgnoreCase(
												stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))) {
									addError(ServiceConstants.MSG_STG_CLOS_ADO_030, errorDtos);
								}

								if ((!ADOPTION_CONSUM
										.equalsIgnoreCase(legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus()))
										&& (REAS_ADOPT_CONSUM.equalsIgnoreCase(
												stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))) {
									addError(ServiceConstants.MSG_STG_CLOS_ADO_010, errorDtos);
								}
							} else {

								addError(ServiceConstants.MSG_LEGAL_STAT_NOT_FOUND, errorDtos);
							}

							if ((REAS_ADOPT_CONSUM
									.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))) {
								// CSES34DOutRec
								PlacementActPlannedOutDto placementActPlannedOutDto = getPlacementActPlannedOutDto(
										idPerson); // CSES34D
								if (!ObjectUtils.isEmpty(placementActPlannedOutDto)) {
									if ((!ADOPTIVE_PLACEMENT
											.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))
											&& (!PRIV_AGCY_ADPT_HM
													.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))
											|| ((ARC_UTL_MAX_YEAR != placementActPlannedOutDto.getDtPlcmtEnd()
													.getYear())
													&& dateIsNotEmpty(placementActPlannedOutDto.getDtPlcmtEnd()))) {
										addError(ServiceConstants.MSG_STG_CLOS_ADO_060, errorDtos);
									}
								} else {

									addError(ServiceConstants.MSG_PLCMT_NOT_FOUND, errorDtos);
								}
							}

							break;
						case (SEC_POS_CLOSE_REAS_TWO):
							List<PlacementDto> placementDtos = null;
							if (!ObjectUtils.isEmpty(idPerson)) {
								placementDtos = placementDao.getMostRecentPlacement(idPerson);// CallCSES44D
							}
							if (CollectionUtils.isNotEmpty(placementDtos)) {
								// CSES44DOutRec
								PlacementDto placementDto = placementDtos.get(0);
								if (CHILD_TURNED_18.equalsIgnoreCase(placementDto.getCdPlcmtRemovalRsn())
										|| CHILD_DEATH.equalsIgnoreCase(placementDto.getCdPlcmtRemovalRsn())
										|| ACUTE_CARE_RTN_HOME.equalsIgnoreCase(placementDto.getCdPlcmtRemovalRsn())
										|| CAREGIVER_REQUESTED.equalsIgnoreCase(placementDto.getCdPlcmtRemovalRsn())
										|| CHILD_NM_CHANGED.equalsIgnoreCase(placementDto.getCdPlcmtRemovalRsn())
										|| CHILD_REQUESTED.equalsIgnoreCase(placementDto.getCdPlcmtRemovalRsn())
										|| CPS_INITIATED.equalsIgnoreCase(placementDto.getCdPlcmtRemovalRsn())
										|| RISK_ABUSE_NEGLECT.equalsIgnoreCase(placementDto.getCdPlcmtRemovalRsn())
										|| SUPERVISOR_REQUEST.equalsIgnoreCase(placementDto.getCdPlcmtRemovalRsn())) {
									// this purpose fully kept not sure why we
									// need this

								} else {
									addError(ServiceConstants.MSG_STG_CLOS_ADO_020, errorDtos);
								}
							} else {

								addError(ServiceConstants.MSG_PLCMT_ADOPT_PLCMT_REQ, errorDtos);
							}

							Long openSubStage = null;
							if (!ObjectUtils.isEmpty(idPerson)) {
								openSubStage = stageDao.getSUBOpenStagesCount(idPerson, ServiceConstants.PRIMARY_CHILD);// CallCSES21D
							}
							if (!ObjectUtils.isEmpty(openSubStage)) {
								addError(ServiceConstants.MSG_SUB_NOT_FOUND, errorDtos);
							}

							break;
						case (SEC_POS_CLOSE_REAS_FOUR):
						case (SEC_POS_CLOSE_REAS_FIVE):
							List<PersonPortfolioOutDto> personPortfolioOutDtos = getPersonPortfolio(idPerson);// CallCCMN44D
							if (CollectionUtils.isNotEmpty(personPortfolioOutDtos)) {
								// CCMN44DOutRec
								PersonPortfolioOutDto personPortfolioOutDto = personPortfolioOutDtos.get(0);
								if (dateIsNotEmpty(personPortfolioOutDto.getDtPersonBirth())) {
									fYears = DateUtils.calculatePersonsAgeInYears(
											personPortfolioOutDto.getDtPersonBirth(), dtDtSystemDate);
								}

								if ((fYears < 18)
										&& (REAS_AGED_OUT.equalsIgnoreCase(
												stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))
										&& dateIsNotEmpty(personPortfolioOutDto.getDtPersonBirth())) {
									addError(ServiceConstants.MSG_STG_CLOS_ADO_040, errorDtos);
								}

								if (dateIsEmpty(personPortfolioOutDto.getDtPersonDeath())
										&& (REAS_CHILD_DEATH.equalsIgnoreCase(
												stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))) {
									addError(ServiceConstants.MSG_STG_CLOS_SUB_100, errorDtos);
								}

							}

							break;
						case (SEC_POS_CLOSE_REAS_SIX):
							// CSES34DOutRec
							PlacementActPlannedOutDto placementActPlannedOutDto = getPlacementActPlannedOutDto(
									idPerson); // CSES34D

							if (!ObjectUtils.isEmpty(placementActPlannedOutDto)
									&& (ADOPTIVE_PLACEMENT
											.equalsIgnoreCase(placementActPlannedOutDto.getCdPlcmtLivArr()))
									&& (0 != (placementActPlannedOutDto.getDtPlcmtStart()
											.compareTo(placementActPlannedOutDto.getDtPlcmtEnd())))) {

								addError(ServiceConstants.MSG_STG_CLOS_ADO_070, errorDtos);
							}

							break;
						default:
							break;

						}
						break;
					default:
						break;

					}
				}

				break;
			case (POST_ADOPTION):
				if ((PAD_CHILD_DEATH.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed())
						&& STAGE_POST_ADOPT.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))
						|| (PCA_CHILD_DEATH.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed())
								&& STAGE_PERM_CARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))) {
					List<PersonPortfolioOutDto> personPortfolioOutDtos = getPersonPortfolio(idPerson);// CallCCMN44D
					if (CollectionUtils.isNotEmpty(personPortfolioOutDtos)) {
						// CCMN44DOutRec
						PersonPortfolioOutDto personPortfolioOutDto = personPortfolioOutDtos.get(0);
						if (dateIsEmpty(personPortfolioOutDto.getDtPersonDeath())) {
							addError(ServiceConstants.MSG_STG_CLOS_SUB_100, errorDtos);
						}

					}
				}
				// CCMN87DInRec
				EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
				if (STAGE_POST_ADOPT.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
					eventStagePersonLinkInsUpdInDto.setCdTask(PAD_ADOPT_SUB);
					eventStagePersonLinkInsUpdInDto.setCdEventType(ADOPT_SUBSIDY_TYPE);
				} else if (STAGE_PERM_CARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
					eventStagePersonLinkInsUpdInDto.setCdTask(PCA_ADOPT_SUB);
					eventStagePersonLinkInsUpdInDto.setCdEventType(PCA_SUBSIDY_TYPE);
				} else {
					eventStagePersonLinkInsUpdInDto.setCdTask(null);
					eventStagePersonLinkInsUpdInDto.setCdEventType(ADOPT_SUBSIDY_TYPE);
				}
				// CallCCMN87D
				List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpdOutDtoList = eventStagePersonLink(
						stageClosureRtrvDto.getStageDto().getIdStage(), eventStagePersonLinkInsUpdInDto);
				// CallCCMN87D,CCMN87DInRec,CCMN87DOutRec
				if (STAGE_PERM_CARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
					if (CollectionUtils.isNotEmpty(eventStagePersonLinkInsUpdOutDtoList)) {
						// CCMNI8DInRec
						for (EventStagePersonLinkInsUpdOutDto eventStagePersonLinkInsUpdOutDto : eventStagePersonLinkInsUpdOutDtoList) {
							List<PcaSubsidyOutDto> pcaSubsidyOutDtos = pcaSubsidyDao
									.getPCASubsidyRecord(eventStagePersonLinkInsUpdOutDto.getIdEvent());// CallCCMNI8D,CCMNI8DInRec,CCMNI8DOutRec
							if (CollectionUtils.isNotEmpty(pcaSubsidyOutDtos)) {
								// CCMNI8DOutRec
								PcaSubsidyOutDto pcaSubsidyOutDto = pcaSubsidyOutDtos.get(0);
								if (ObjectUtils.isEmpty(pcaSubsidyOutDto.getCdCloseRsn())) {
									addError(ServiceConstants.MSG_STG_CLOS_PCA_SUBSIDY, errorDtos);
								}

							}

						}

					}
				} else {
					if (!ObjectUtils.isEmpty(stageClosureRtrvOutDto)
							&& CollectionUtils.isNotEmpty(eventStagePersonLinkInsUpdOutDtoList))// TODO

					{
						for (EventStagePersonLinkInsUpdOutDto eventStagePersonLinkInsUpdOutDto : eventStagePersonLinkInsUpdOutDtoList) {
							// CallCSES64D
							// CSES64DOutRec
							AdoptionSubsidyDto adoptionSubsidyDto = eventDao
									.getAdoptionSubsidyByIdEvent(eventStagePersonLinkInsUpdOutDto.getIdEvent());

							if (!ObjectUtils.isEmpty(adoptionSubsidyDto)) {
								if (ObjectUtils.isEmpty(adoptionSubsidyDto.getCdAdptSubCloseRsn())) {
									addError(ServiceConstants.MSG_STG_CLOS_PAD_A, errorDtos);
								}

							}

						}

					}

				}

				if ((STAGE_PERM_CARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))) {
					if (!ObjectUtils.isEmpty(idPerson)) {
						List<PlacementDto> placementDtos = placementDao.getMostRecentLinvingArrangement(idPerson,
								PCA_PLCMNT);
						// CallCSESF0D
						if (CollectionUtils.isNotEmpty(placementDtos)) {
							// CSESF0DOutRec
							PlacementDto placementDto = placementDtos.get(0);
							if ((ARC_UTL_MAX_YEAR == placementDto.getDtPlcmtEnd().getYear())
									|| dateIsEmpty(placementDto.getDtPlcmtEnd())) {
								addError(ServiceConstants.MSG_STG_CLOS_PCA_PLCMT, errorDtos);
							}

						}
					}
				}

				break;
			case (FAMILY_STAGE):
				/*
				 * Check if any pending Day Care events for stage
				 */
				eventStatus = getEventStatus(stageClosureRtrvDto.getStageDto().getIdStage());// CallCSVC34D
				if (StringUtils.isNotBlank(eventStatus)) {
					addError(MSG_DAY_CARE_REQ_PEND, errorDtos);

				}
				// CallCSESA3D
				List<EventStageTypeTaskOutDto> evnetDtls = getEventStageTypeTask(
						stageClosureRtrvDto.getStageDto().getIdStage(), stageClosureRtrvDto.getStageDto().getCdStage());
				/*
				 * Verify that the worker has completed the Services and
				 * Referrals Checklist before allowing them to close the FSU/FRE
				 * stage.
				 */
				if (CollectionUtils.isNotEmpty(evnetDtls)) {
					// CSESA3DOutRec
					EventStageTypeTaskOutDto eventStageTypeTaskOutDto = evnetDtls.get(0);

					if (!STATUS_COMPLETE.equalsIgnoreCase(eventStageTypeTaskOutDto.getCdEventStatus())
							&& (Boolean.FALSE.equals(stageClosureRtrvDto.getSysNbrReserved1())
									|| STATUS_PENDING.equalsIgnoreCase(eventStageTypeTaskOutDto.getCdEventStatus()))) {
						addError(ServiceConstants.MSG_INV_SVC_RFRL_CHKLST_WARNING, errorDtos);
					}
				} else {
					/*
					 * The event does not exist, so post an edit message
					 * informing the user they must complete the Services and
					 * Referrals Checklist before closing.
					 */

					addError(ServiceConstants.MSG_INV_SVC_RFRL_CHKLST_WARNING, errorDtos);
				}
				// callCSVC28D
				/*
				 * Ensure that there are no pending events before allowing the
				 * user to submit the stage for closure.
				 */
				checkEventPending(stageClosureRtrvDto.getStageDto().getIdStage(), ServiceConstants.PEND, errorDtos);

				/*****************************************************************
				 ** check for the last FSU/FRE stage in the case. No principal in
				 * the stage may have a legal status of 'Care, Custody, and
				 * Control', 'TMC', 'Other Legal Basis for Resp', or 'Possible
				 * Conservatorship', unless the Closure reason is "Parent Died."
				 **
				 ** check added for last FSU/FRE stage in the case. No child can
				 * have legal status of 'PMC/Rts Not Term (All, Bio Fa, Leg Fa,
				 * Mother) AND the child is not the PC in an open SUB or ADO
				 * stage.
				 ******************************************************************/
				// CallCCMNF6D
				stageCdOutDtos = getStageCode(stageClosureRtrvDto.getStageDto().getIdCase());
				if (CollectionUtils.isNotEmpty(stageCdOutDtos)) {
					// CCMNF6DOutRec
					/*
					 * For each row returned and bOtherFamOpen, set otherFamOpen
					 * to TRUE if the stage is FRE/FSU. Also determine if there
					 * is an open SUB stage for the case
					 */
					for (StageCdOutDto stageCdOutDto : stageCdOutDtos) {
						/*
						 * If the idstage retrieved does not match the idstage
						 * passed into the service, then another stage exists.
						 * Check to see if it is either FRE or FSU. If so, set
						 * otherFamOpen to TRUE. This flag indicates that
						 * another FRE/FSU stage exists.
						 */
						if ((!otherFamOpen) || (!subStageOpen)) {
							if (!stageClosureRtrvDto.getStageDto().getIdStage().equals(stageCdOutDto.getIdStage())) {
								if ((STAGE_FAM_REUN.equalsIgnoreCase(stageCdOutDto.getCdStage()))
										|| (STAGE_FAM_SUBCARE.equalsIgnoreCase(stageCdOutDto.getCdStage()))) {
									otherFamOpen = true;
								}
								if (STAGE_SUBCARE.equalsIgnoreCase(stageCdOutDto.getCdStage())) {
									subStageOpen = true;
								}
							}
						}
						if (STAGE_SUBCARE.equalsIgnoreCase(stageCdOutDto.getCdStage())) {
							if(Objects.nonNull(stageClosureRtrvDto.getStageDto())
									&& Objects.nonNull(stageClosureRtrvDto.getStageDto().getCdStage())
									&& stageClosureRtrvDto.getStageDto().getCdStage().equalsIgnoreCase(FRE_PROGRAM)
									&& Objects.nonNull(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed())
									&& stageClosureRtrvDto.getStageDto().getCdStageReasonClosed().equalsIgnoreCase(STAGE_PROG_FRE_FPR_REASON_CD)) {
								addError(MessagesConstants.MSG_FRE_SUB_STAGE_OPEN_ERROR, errorDtos);
							}
						}

					}

				}
				/*
				 * If otherFamOpen is FALSE and the StageReasonClose is not
				 * 'Parent Death,' then call Stage Person Link Retrieve Modified
				 * the if test, we always want to make this edit if this is the
				 * last Fam stage in the case Removed conditional for
				 * CdStageReasonClosed = Parent's Death
				 */

				if (!otherFamOpen) {
					// CCMNB9D
					// Retrieves all the records in the STAGE_PERSON_LINK
					// table for the ID
					// STAGE given as input
					List<StagePersonLinkDto> stagePersonLinkDtolist = stagePersonLinkDao
							.getStagePersonLinkByIdStage(stageClosureRtrvDto.getStageDto().getIdStage());
					if (CollectionUtils.isNotEmpty(stagePersonLinkDtolist)) {
						// CCMNB9DOutRec
						for (StagePersonLinkDto stagePersonLinkDto : stagePersonLinkDtolist) {
							if (PRINCIPAL.equalsIgnoreCase(stagePersonLinkDto.getCdStagePersType())) {

								// CSES78DOutRec
								LegalStatusDetailDto legalStatusDetailDto = legalStatusDao.getLatestLegalStatus(
										stagePersonLinkDto.getIdPerson(),
										stageClosureRtrvDto.getStageDto().getIdCase());// CallCSES78D
								/*
								 ** If there Legal Status is one of the following
								 ** types, then add MSG_STG_CLOS_FAM_010 to the
								 ** errorDtos. Added another level to the test If
								 * one of these legal status is found then we
								 ** must have 1) an open subcare stage in the
								 * case and/or 2) a closure reason of parents
								 * died if we find this then we can skip the
								 * edit and go to the next person
								 */
								if (!ObjectUtils.isEmpty(legalStatusDetailDto)
										&& !ObjectUtils.isEmpty(legalStatusDetailDto.getCdLegalStatStatus())) {
									if (((LEGAL_STS_TYPE_ONE
											.equalsIgnoreCase(legalStatusDetailDto.getCdLegalStatStatus()))
											|| (LEGAL_STS_TYPE_TWO
													.equalsIgnoreCase(legalStatusDetailDto.getCdLegalStatStatus()))
											|| (LEGAL_STS_TYPE_EIGHT
													.equalsIgnoreCase(legalStatusDetailDto.getCdLegalStatStatus()))
											|| (LEGAL_STS_TYPE_ONE_THIRTY
													.equalsIgnoreCase(legalStatusDetailDto.getCdLegalStatStatus())))
											&& ((!REAS_PARENT_DEATH.equalsIgnoreCase(
													stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))
													|| (!subStageOpen))) {
										addError(ServiceConstants.MSG_STG_CLOS_FAM_010, errorDtos);
									}

								}
								// StageCdOutDtos
								// CCMNF6DOutRec
								for (StageCdOutDto stageCdOutDto : stageCdOutDtos) {
									if (!indOpenSubAdo && (STAGE_ADOPTION.equalsIgnoreCase(stageCdOutDto.getCdStage()))
											|| (STAGE_SUBCARE.equalsIgnoreCase(stageCdOutDto.getCdStage()))) {
										// CINV39DOutRec CallCINV39D
										StagePersonLinkRecordOutDto stagePersonLinkRecordOutDto = getStagePersonLinkRecord(
												stageCdOutDto.getIdStage(), stagePersonLinkDto.getIdPerson());
										if (!ObjectUtils.isEmpty(stagePersonLinkRecordOutDto) && PRIMARY_CHILD
												.equalsIgnoreCase(stagePersonLinkRecordOutDto.getCdStagePersRole())) {
											indOpenSubAdo = true;
										}
									}

								}
								if ((LEGAL_STS_TYPE_THREE.equalsIgnoreCase(legalStatusDetailDto.getCdLegalStatStatus()))
										|| (LEGAL_STS_TYPE_FOUR
												.equalsIgnoreCase(legalStatusDetailDto.getCdLegalStatStatus()))
										|| (LEGAL_STS_TYPE_FIVE
												.equalsIgnoreCase(legalStatusDetailDto.getCdLegalStatStatus()))
										|| (LEGAL_STS_TYPE_SIX
												.equalsIgnoreCase(legalStatusDetailDto.getCdLegalStatStatus()))
										|| (LEGAL_STS_TYPE_SEVEN
												.equalsIgnoreCase(legalStatusDetailDto.getCdLegalStatStatus()))) {
									if (!indOpenSubAdo) {
										addError(ServiceConstants.MSG_STG_CLOS_FAM_020, errorDtos);
									}
								}
							}

						}

					}
				}

				break;
			default:
				break;
			}
			// till here the Stage Switch
			if ((!otherFamOpen)) {
				// CallCCMNF6D
				stageCdOutDtos = getStageCode(stageClosureRtrvDto.getStageDto().getIdCase());
				if (CollectionUtils.isNotEmpty(stageCdOutDtos)) {
					// CCMNF6DOutRec
					for (StageCdOutDto stageCdOutDto : stageCdOutDtos) {
						if (!otherFamOpen
								&& (!stageClosureRtrvDto.getStageDto().getIdStage().equals(stageCdOutDto.getIdStage()))
								&& (((STAGE_FAM_REUN.equalsIgnoreCase(stageCdOutDto.getCdStage())))
										|| (STAGE_FAM_SUBCARE.equalsIgnoreCase(stageCdOutDto.getCdStage()))
										|| (STAGE_FAM_PRES.equalsIgnoreCase(stageCdOutDto.getCdStage())))) {
							otherFamOpen = true;
						}

					}

				}
			}
			// CCMN87DInRec
			EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
			if (STAGE_AOC.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
				eventStagePersonLinkInsUpdInDto.setCdTask(AOC_SVC_AUTH);
			} else if (STAGE_SUBCARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
				eventStagePersonLinkInsUpdInDto.setCdTask(SUB_SVC_AUTH);
			} else if (STAGE_ADOPTION.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
				eventStagePersonLinkInsUpdInDto.setCdTask(ADO_SVC_AUTH);
			} else if (STAGE_POST_ADOPT.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
				eventStagePersonLinkInsUpdInDto.setCdTask(PAD_SVC_AUTH);
			} else if (STAGE_FAM_SUBCARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
				eventStagePersonLinkInsUpdInDto.setCdTask(FSU_SVC_AUTH);
			} else if (STAGE_FAM_REUN.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
				eventStagePersonLinkInsUpdInDto.setCdTask(FRE_SVC_AUTH);
			} else {
				eventStagePersonLinkInsUpdInDto.setCdTask(null);
			}
			eventStagePersonLinkInsUpdInDto.setCdEventType(SERVICE_AUTH_TYPE);
			// CallCCMN87D
			List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpdOutDtoList = eventStagePersonLink(
					stageClosureRtrvDto.getStageDto().getIdStage(), eventStagePersonLinkInsUpdInDto);
			if (CollectionUtils.isNotEmpty(eventStagePersonLinkInsUpdOutDtoList)) {
				// CCMN87DOutRec
				for (EventStagePersonLinkInsUpdOutDto eventStagePersonLink : eventStagePersonLinkInsUpdOutDtoList) {
					if (!indSvcOpen && (SERVICE_AUTH_TYPE.equalsIgnoreCase(eventStagePersonLink.getCdEventType()))
							&& (((!STATUS_NEW.equalsIgnoreCase(eventStagePersonLink.getCdEventStatus()))
									&& (!STATUS_PROCESS.equalsIgnoreCase(eventStagePersonLink.getCdEventStatus())))
									|| (CAPS_PROG_APS.equalsIgnoreCase(
											stageClosureRtrvDto.getStageDto().getCdStageProgram())))) {
						// CSES24DInRec
						// CallCSES24D
						List<SvcAuthEventLinkOutDto> svcAuthEventLinkOutDtos = getSvcAuthEventLink(
								eventStagePersonLink.getIdEvent());
						if (CollectionUtils.isNotEmpty(svcAuthEventLinkOutDtos)) {
							// CSES24DOutRec
							SvcAuthEventLinkOutDto svcAuthEventLinkOutDto = svcAuthEventLinkOutDtos.get(0);
							// CallCSES23D
							// CSES23DOutRec
							ServiceAuthorizationDto serviceAuthorizationDto = serviceAuthorizationDao
									.getServiceAuthorizationById(svcAuthEventLinkOutDto.getIdSvcAuth());
							if (!ObjectUtils.isEmpty(serviceAuthorizationDto)) {
								if (ServiceConstants.Y
										.equalsIgnoreCase(serviceAuthorizationDto.getIndSvcAuthComplete())) {
									// CallCLSS24D
									List<SvcAuthDetailNameOutDto> svcAuthDetailNameOutDtos = getSvcAuthDetailName(
											svcAuthEventLinkOutDto.getIdSvcAuth());
									if (!ObjectUtils.isEmpty(svcAuthDetailNameOutDtos)) {
									}
									// CLSS24DOutRec
									for (SvcAuthDetailNameOutDto svcAuthDetailNameOutDto : svcAuthDetailNameOutDtos) {
										//Added the null condition check for DtSvcAuthDtlTerm - warranty defect 12568
										if (!indSvcOpen
												&& !ObjectUtils.isEmpty(svcAuthDetailNameOutDto.getDtSvcAuthDtlTerm())
												&& (0 > (dtDtSystemDate
														.compareTo(svcAuthDetailNameOutDto.getDtSvcAuthDtlTerm())))
												&& ((STATUS_PENDING
														.equalsIgnoreCase(eventStagePersonLink.getCdEventStatus()))
														|| (STATUS_COMPLETE.equalsIgnoreCase(
																eventStagePersonLink.getCdEventStatus()))
														|| (STATUS_APPROVED.equalsIgnoreCase(
																eventStagePersonLink.getCdEventStatus())))) {
											if (STATUS_APPROVED
													.equalsIgnoreCase(eventStagePersonLink.getCdEventStatus())
													&& !FORMER_DAY_CARE.equalsIgnoreCase(
															svcAuthDetailNameOutDto.getCdSvcAuthDtlSvc())) {
												svcAuthsToProgress = true;
											}
											if (KIN_SERVICE_AUTH_68B
													.equalsIgnoreCase(svcAuthDetailNameOutDto.getCdSvcAuthDtlSvc())
													|| KIN_SERVICE_AUTH_68C.equalsIgnoreCase(
															svcAuthDetailNameOutDto.getCdSvcAuthDtlSvc())
													|| KIN_SERVICE_AUTH_68D.equalsIgnoreCase(
															svcAuthDetailNameOutDto.getCdSvcAuthDtlSvc())
													|| KIN_SERVICE_AUTH_68E.equalsIgnoreCase(
															svcAuthDetailNameOutDto.getCdSvcAuthDtlSvc())
													|| KIN_SERVICE_AUTH_68F.equalsIgnoreCase(
															svcAuthDetailNameOutDto.getCdSvcAuthDtlSvc())
													|| KIN_SERVICE_AUTH_68G.equalsIgnoreCase(
															svcAuthDetailNameOutDto.getCdSvcAuthDtlSvc())
													|| KIN_SERVICE_AUTH_68H.equalsIgnoreCase(
															svcAuthDetailNameOutDto.getCdSvcAuthDtlSvc())
													|| KIN_SERVICE_AUTH_68I.equalsIgnoreCase(
															svcAuthDetailNameOutDto.getCdSvcAuthDtlSvc())) {
												svcAuthsToProgress = false;
												indSvcOpen = true;
											}
											switch (stageClosureRtrvDto.getStageDto().getCdStage().substring(0, 1)) {
											case SUBCARE:
												if (!STATUS_APPROVED
														.equalsIgnoreCase(eventStagePersonLink.getCdEventStatus())
														|| (!otherFamOpen && !FORMER_DAY_CARE.equalsIgnoreCase(
																svcAuthDetailNameOutDto.getCdSvcAuthDtlSvc()))) {
													indSvcOpen = true;
												}

												break;
											case ADO_AOC:
												if (STAGE_AOC.equalsIgnoreCase(
														stageClosureRtrvDto.getStageDto().getCdStage())) {
													if (!STATUS_APPROVED
															.equalsIgnoreCase(eventStagePersonLink.getCdEventStatus())
															&& !STATUS_COMPLETE.equalsIgnoreCase(
																	eventStagePersonLink.getCdEventStatus())) {
														indSvcOpen = true;
													}
												} else if (STAGE_ADOPTION.equalsIgnoreCase(
														stageClosureRtrvDto.getStageDto().getCdStage())) {
													if (!ADOPT_DISRUPTION.equalsIgnoreCase(
															stageClosureRtrvDto.getStageDto().getCdStageReasonClosed())
															|| !STATUS_APPROVED.equalsIgnoreCase(
																	eventStagePersonLink.getCdEventStatus())) {
														indSvcOpen = true;
													}
												}

												break;
											case FAMILY_STAGE:
												if (!STATUS_APPROVED
														.equalsIgnoreCase(eventStagePersonLink.getCdEventStatus())
														|| (!otherFamOpen && !FORMER_DAY_CARE.equalsIgnoreCase(
																svcAuthDetailNameOutDto.getCdSvcAuthDtlSvc()))) {
													indSvcOpen = true;
												}

												break;
											default:
												break;
											}
										}

									}
								}

							}

						}
					}

				}

			}

			if (!indSvcOpen && svcAuthsToProgress) {
				// CallCLSS30D
				List<StageSituationOutDto> stageSituationOutDtos = getStageSituation(
						stageClosureRtrvDto.getStageDto().getIdCase());
				// Clss30do
				for (StageSituationOutDto stageSituationOutDto : stageSituationOutDtos) {
					if (!eligibleStageFound
							&& (dateIsEmpty(stageSituationOutDto.getDtStageClose())
									|| ARC_MAX_YEAR == stageSituationOutDto.getDtStageClose().getYear())
							&& stageSituationOutDto.getIdStage() != stageClosureRtrvDto.getStageDto().getIdStage()) {
						// lRC =
						// dtTempStageStart.compareTo(stageSituationOutDto.getDtStageStart());//as
						// legacy passed NULL for the date comparison
						lRC = -1;
						if ((lRC < 0) && ((!PREP_ADULT.equalsIgnoreCase(stageSituationOutDto.getCdStage()))
								&& (!ARI_STAGE.equalsIgnoreCase(stageSituationOutDto.getCdStage()))
								&& (!STAGE_SUBCARE.equalsIgnoreCase(stageSituationOutDto.getCdStage()))
								&& (!INVESTIGATION.equalsIgnoreCase(stageSituationOutDto.getCdStage()))
								&& (!ADOPTION.equalsIgnoreCase(stageSituationOutDto.getCdStage())))) {
							eligibleStageFound = true;
						} else if ((lRC == 0) && ((!PREP_ADULT.equalsIgnoreCase(stageSituationOutDto.getCdStage()))
								&& (!ARI_STAGE.equalsIgnoreCase(stageSituationOutDto.getCdStage()))
								&& (!STAGE_SUBCARE.equalsIgnoreCase(stageSituationOutDto.getCdStage()))
								&& (!ADOPTION.equalsIgnoreCase(stageSituationOutDto.getCdStage())))) {
							// Clss30doi
							for (StageSituationOutDto stageSituationOutDtoi : stageSituationOutDtos) {
								if ((dateIsEmpty(stageSituationOutDto.getDtStageClose()))
										|| (ARC_MAX_YEAR == stageSituationOutDto.getDtStageClose().getYear())
										|| FPR_PROGRAM.equalsIgnoreCase(stageSituationOutDtoi.getCdStage())
										|| FSU_PROGRAM.equalsIgnoreCase(stageSituationOutDtoi.getCdStage())
										|| FRE_PROGRAM.equalsIgnoreCase(stageSituationOutDtoi.getCdStage())) {

									eligibleStageFound = true;

								}

							}
						}
					}

				}
				if (!eligibleStageFound) {
					indSvcOpen = true;
				}
			}
			if (indSvcOpen) {
				addError(ServiceConstants.MSG_SVA_OPN_AUTHS, errorDtos);
			}
			if (CollectionUtils.isEmpty(errorDtos)) {
				if ((STAGE_ADOPTION.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))
						&& (REAS_ADOPT_CONSUM
								.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed()))) {
					// CSES64DOutRec
					AdoptionSubsidyDto adoptionSubsidyDto = eventDao
							.getAdoptionSubsidyByIdEvent(stageClosureRtrvDto.getEventDto().getIdEvent());//
					// CallCSES64D
					if (!ObjectUtils.isEmpty(adoptionSubsidyDto)) {
						stageClosureRtrvDto.getStageDto().setCdStageReasonClosed(STAGE_POST_ADOPT);
					}
				}

				List<StageProgDto> stageProgDtoList = stageProgDao.getStgProgroession(
						stageClosureRtrvDto.getStageDto().getCdStage(),
						stageClosureRtrvDto.getStageDto().getCdStageProgram(),
						stageClosureRtrvDto.getStageDto().getCdStageReasonClosed());
				// CallCCMNB8D
				// CCMNB8DOutRec
				StageProgDto stageProgDto = stageProgDtoList.get(0);
				if (!ObjectUtils.isEmpty(stageProgDto)) {

					stageClosureRtrvDto.setIndStageClose(stageProgDto.getIndStageProgClose());
					tempCdStageClosureReason = stageProgDto.getCdStageProgRsnClose();
					tempCdStageOpen = stageProgDto.getCdStageProgOpen();
				}
				if (!CLOSE_OPEN_STAGE.equalsIgnoreCase(stageProgDto.getIndStageProgClose())) {

					if ((CollectionUtils.isNotEmpty(stageCdOutDtos))) {
						if ((1 == stageCdOutDtos.size()) && (stageCdOutDtos.get(0).getIdStage() == stageClosureRtrvDto
								.getStageDto().getIdStage())) {
							if (CAPS_PROG_CPS.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageProgram())) {
								List<StagePersDto> stagePersDtos = null;
								if (!CASE_REL_SPEC_REQ.equalsIgnoreCase(
										stageClosureRtrvDto.getStageDto().getCdStageType().substring(0, 2))) {
									// CallCCMNH9D
									stagePersDtos = stagePersonLinkDao.getPersonLegalStatus(
											stageClosureRtrvDto.getStageDto().getIdCase(), LEG_STATUS_EVENT);

								}
								if (CollectionUtils.isNotEmpty(stagePersDtos)) {
									// CCMNH9DOutRec
									for (StagePersDto stagePersDto : stagePersDtos) {
										if (!caseLegalStsFound
												&& !STAFF.equalsIgnoreCase(stagePersDto.getCdStagePersType())) {
											// CSES78DOutRec
											LegalStatusDetailDto legalStatusDetailDto = legalStatusDao
													.getLatestLegalStatus(stagePersDto.getIdPerson(),
															stageClosureRtrvDto.getStageDto().getIdCase());// CallCSES78D
											if (!ObjectUtils.isEmpty(legalStatusDetailDto)) {
												if ((LEGAL_STS_TYPE_ONE
														.equalsIgnoreCase(legalStatusDetailDto.getCdLegalStatStatus()))
														|| (LEGAL_STS_TYPE_TWO.equalsIgnoreCase(
																legalStatusDetailDto.getCdLegalStatStatus()))
														|| (LEGAL_STS_TYPE_THREE.equalsIgnoreCase(
																legalStatusDetailDto.getCdLegalStatStatus()))
														|| (LEGAL_STS_TYPE_FOUR.equalsIgnoreCase(
																legalStatusDetailDto.getCdLegalStatStatus()))
														|| (LEGAL_STS_TYPE_FIVE.equalsIgnoreCase(
																legalStatusDetailDto.getCdLegalStatStatus()))
														|| (LEGAL_STS_TYPE_SIX.equalsIgnoreCase(
																legalStatusDetailDto.getCdLegalStatStatus()))
														|| (LEGAL_STS_TYPE_SEVEN.equalsIgnoreCase(
																legalStatusDetailDto.getCdLegalStatStatus()))
														|| (LEGAL_STS_TYPE_EIGHT.equalsIgnoreCase(
																legalStatusDetailDto.getCdLegalStatStatus()))
														|| (LEGAL_STS_TYPE_ONE_THIRTY.equalsIgnoreCase(
																legalStatusDetailDto.getCdLegalStatStatus()))
														|| (LEGAL_STS_TYPE_ONE_FOURTY.equalsIgnoreCase(
																legalStatusDetailDto.getCdLegalStatStatus()))) {
													if (STAGE_POST_ADOPT.equalsIgnoreCase(
															stageClosureRtrvDto.getStageDto().getCdStage())) {
														addError(ServiceConstants.MSG_TERM_LEGAL_STAT_REQ, errorDtos);
													} else {
														addError(ServiceConstants.MSG_STG_CLOS_SUB_010, errorDtos);
													}
													caseLegalStsFound = true;
												}

											}
										}

									}
								} else {

									if ((!STAGE_POST_ADOPT
											.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))
											&& (!STAGE_PERM_CARE.equalsIgnoreCase(
													stageClosureRtrvDto.getStageDto().getCdStage()))) {
										addError(ServiceConstants.MSG_LEGAL_STAT_NOT_FOUND, errorDtos);
									}

								}
							}
							eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
							if (STAGE_AOC.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
								eventStagePersonLinkInsUpdInDto.setCdTask(AOC_SVC_AUTH);
							} else if (STAGE_SUBCARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
								eventStagePersonLinkInsUpdInDto.setCdTask(SUB_SVC_AUTH);
							} else if (STAGE_ADOPTION
									.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
								eventStagePersonLinkInsUpdInDto.setCdTask(ADO_SVC_AUTH);
							} else if (STAGE_POST_ADOPT
									.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
								eventStagePersonLinkInsUpdInDto.setCdTask(PAD_SVC_AUTH);
							} else if (STAGE_FAM_SUBCARE
									.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
								eventStagePersonLinkInsUpdInDto.setCdTask(FSU_SVC_AUTH);
							} else if (STAGE_FAM_REUN
									.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())) {
								eventStagePersonLinkInsUpdInDto.setCdTask(FRE_SVC_AUTH);
							} else {
								eventStagePersonLinkInsUpdInDto.setCdTask(null);
							}
							eventStagePersonLinkInsUpdInDto.setCdEventType(SERVICE_AUTH_TYPE);
							// CallCCMN87D
							List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpdOutDtos = eventStagePersonLink(
									stageClosureRtrvDto.getStageDto().getIdStage(), eventStagePersonLinkInsUpdInDto);
							if (CollectionUtils.isNotEmpty(eventStagePersonLinkInsUpdOutDtos)) {
								// CCMN87DOutRec
								for (EventStagePersonLinkInsUpdOutDto eventStagePersonLink : eventStagePersonLinkInsUpdOutDtos) {
									if (!indSvcOpen
											&& (!STATUS_NEW.equalsIgnoreCase(eventStagePersonLink.getCdEventStatus()))
											&& (!STATUS_PROCESS
													.equalsIgnoreCase(eventStagePersonLink.getCdEventStatus()))
											&& (SERVICE_AUTH_TYPE
													.equalsIgnoreCase(eventStagePersonLink.getCdEventType()))) {
										// CallCSES24D
										List<SvcAuthEventLinkOutDto> svcAuthEventLinkOutDtos = getSvcAuthEventLink(
												eventStagePersonLink.getIdEvent());
										if (CollectionUtils.isNotEmpty(svcAuthEventLinkOutDtos)) {
											// CSES24DOutRec
											SvcAuthEventLinkOutDto svcAuthEventLinkOut = svcAuthEventLinkOutDtos.get(0);
											// CSES23DOutRec
											ServiceAuthorizationDto serviceAuthorizationDto = serviceAuthorizationDao
													.getServiceAuthorizationById(svcAuthEventLinkOut.getIdSvcAuth());
											// CallCSES23D
											if (!ObjectUtils.isEmpty(serviceAuthorizationDto)) {
												if (ServiceConstants.YES
														.equals(serviceAuthorizationDto.getIndSvcAuthComplete())) {
													// CallCLSS24D
													List<SvcAuthDetailNameOutDto> svcAuthDetailNameOutDtos = getSvcAuthDetailName(
															svcAuthEventLinkOut.getIdSvcAuth());

													if (CollectionUtils.isNotEmpty(svcAuthDetailNameOutDtos)) {
														// CLSS24DOutRec
														for (SvcAuthDetailNameOutDto svcAuthDetailName : svcAuthDetailNameOutDtos) {
															if ((0 > dtDtSystemDate
																	.compareTo(svcAuthDetailName.getDtSvcAuthDtlTerm()))
																	&& (!DAY_CARE.equalsIgnoreCase(
																			svcAuthDetailName.getCdSvcAuthDtlSvc()))
																	&& (!FORMER_DAY_CARE.equalsIgnoreCase(
																			svcAuthDetailName.getCdSvcAuthDtlSvc()))
																	&& (!GENERAL_PROT_CARE.equalsIgnoreCase(
																			svcAuthDetailName.getCdSvcAuthDtlSvc()))
																	&& (!IVE_FC_DAY_CARE.equalsIgnoreCase(
																			svcAuthDetailName.getCdSvcAuthDtlSvc()))
																	&& (!REGISTERED_FAMILY_HM.equalsIgnoreCase(
																			svcAuthDetailName.getCdSvcAuthDtlSvc()))
																	&& (!STATE_PAID_FC_DAY_CARE.equalsIgnoreCase(
																			svcAuthDetailName.getCdSvcAuthDtlSvc()))) {
																indSvcOpen = true;
															}

														}

													}
												}

											}

										}
									}

								}

							}

							if (indSvcOpen) {
								addError(ServiceConstants.MSG_SVA_OPN_AUTHS, errorDtos);
							}
							/**
							 * deleted the following if(bCaseLegalStsFound)(0 ==
							 * errorDtos.size()) && (FND_SUCCESS == RetVal)
							 **/

						}

					}
				}

			}
			if ((FPR_PROGRAM.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))
					|| (FSU_PROGRAM.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))
					|| (FRE_PROGRAM.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))
					|| (STAGE_SUBCARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))
					|| (KIN_PROGRAM.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))
					|| (STAGE_ADOPTION.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))) {
				long rc1 = 0;
				rc1 = getCriminalCheckRecords(stageClosureRtrvDto.getStageDto().getIdStage());
				// CallCSESC2D

				if (rc1 > 0) {
					stageClosureRtrvDto.setIdPerson(rc1);// setting the personId
					addError(ServiceConstants.CRML_HIST_CHECK, errorDtos);
				}

			}
			if (CollectionUtils.isNotEmpty(errorDtos)) {
				stageClosureRtrvRes.setStageclosureRtrvDto(stageClosureRtrvDto);
				stageClosureRtrvRes.setErrorDtos(errorDtos);
				return stageClosureRtrvRes;
			}

		}

		if (!caseLegalStsFound && errorDtos.isEmpty()) {

			StageDto stageDto = stageClosureRtrvDto.getStageDto();
			stageDto.setCdStageReasonClosed(tempCdStageClosureReason);
			// CallCAUD47D
			/*
			 ** Close Stage
			 */
			closeOpenStageService.stageAUD(stageClosureRtrvDto.getStageDto(), ServiceConstants.REQ_FUNC_CD_UPDATE);

			if ((0 < stageClosureRtrvDto.getEventDto().getIdEvent())) {
				// CallCINV43D
				/*
				 ** Only try to STATUS_COMPLETE the todo's if there is an event
				 * for the closure
				 */
				updateTODOEvent(stageClosureRtrvDto.getEventDto().getIdEvent());

			}
			if (Boolean.FALSE.equals(stageClosureRtrvDto.getSysNbrReserved1())) {
				// since we are altering the event status in dto , we are
				// fetching the actual status using the parent dto.
				if ((STATUS_PENDING.equalsIgnoreCase(stageClosureRtrvDto.getCurrentEventStatus()))) {
					ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
					approvalCommonInDto.setIdEvent(stageClosureRtrvDto.getEventDto().getIdEvent());
					/*
					 ** Invalidate Approval
					 */
					approvalCommonService.callCcmn05uService(approvalCommonInDto);

				}
				// we need to set this event id in out going Dto
				PostEventStageStatusOutDto postEventStageStatusOutDto = callPostEvent(stageClosureRtrvDto.getEventDto(),
						dtDtSystemDate);
				// we need to set this
				if (ObjectUtils.isEmpty(stageClosureRtrvDto.getEventDto())) {
					EventDto eventDto = new EventDto();
					stageClosureRtrvDto.setEventDto(eventDto);
				}
				stageClosureRtrvDto.getEventDto().setIdEvent(postEventStageStatusOutDto.getIdEvent());

			}
			/*
			 * Added PCA condition also in the if loop if APS or PAD or PCA,
			 * call either of the Stage Progression common functions
			 * (CloseOpenStage or CloseStageCase) Don't progress if NULL_DATE
			 * was passed into service
			 */
			if (((CAPS_PROG_APS.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStageProgram()))
					|| (STAGE_POST_ADOPT.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage()))
					|| (STAGE_PERM_CARE.equalsIgnoreCase(stageClosureRtrvDto.getStageDto().getCdStage())))
					&& (!TypeConvUtil.isNullOrEmpty(stageClosureRtrvDto.getStageDto().getDtStageClose()))) {
				if ((CLOSE_OPEN_STAGE == stageClosureRtrvDto.getIndStageClose())
						|| (OPEN_STAGE == stageClosureRtrvDto.getIndStageClose())) {
					// CloseOpenStageOutputDto closeOpenStageOutputDto
					callCloseOpenStage(stageClosureRtrvDto, dtDtSystemDate, tempCdStageClosureReason, tempCdStageOpen);

				} else {
					callCloseStageCase(stageClosureRtrvDto);

				}
			}
		}
		stageClosureRtrvRes.setStageclosureRtrvDto(stageClosureRtrvDto);
		stageClosureRtrvRes.setErrorDtos(errorDtos);

		return stageClosureRtrvRes;
	}

	private boolean openTAExists(Long idStage) {
		return temporaryAbsenceDao.isOpenTAPlacementPresent(idStage);
	}

	/**
	 * Method Name: checkFPSTerminated Method Description: this method check
	 * Child's Legal Status must be 'FPS Resp Terminated' if no legalStatus then
	 * adds Legal status messages
	 * 
	 * @param legalStatusPersonMaxStatusDtOutDtos
	 * @param errorDtos
	 */
	private void checkFPSTerminated(List<LegalStatusPersonMaxStatusDtOutDto> legalStatusPersonMaxStatusDtOutDtos,
			List<ErrorDto> errorDtos) {
		if (CollectionUtils.isNotEmpty(legalStatusPersonMaxStatusDtOutDtos)) {
			// CSES32DOutRec
			LegalStatusPersonMaxStatusDtOutDto legalStatusPersonMaxStatusDtOutDto = legalStatusPersonMaxStatusDtOutDtos
					.get(0);
			if (!PRS_RESP_TERM.equalsIgnoreCase(legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus())) {
				addError(ServiceConstants.MSG_STG_CLOS_SUB_020, errorDtos);
			}
		} else {

			addError(ServiceConstants.MSG_LEGAL_STAT_NOT_FOUND, errorDtos);
		}
	}

	/**
	 * 
	 * Method Name: getPersonId Method Description: this method to get Primary
	 * Child for given stage based Stage program
	 * 
	 * @param idStage
	 * @param cdStageProgram
	 * @return personId
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getPersonId(Long idStage, String cdStageProgram) {
		log.debug("Entering method getPersonId in StageClosureRtrvServiceImpl");
		Long idPersonChild = null;
		WorkloadStgPerLinkSelInDto workloadStgPerLinkSelInDto = new WorkloadStgPerLinkSelInDto();

		workloadStgPerLinkSelInDto.setIdStage(idStage);
		if (CAPS_PROG_CPS.equalsIgnoreCase(cdStageProgram)) {
			workloadStgPerLinkSelInDto.setCdStagePersRole(PRIMARY_CHILD);
		} else {
			workloadStgPerLinkSelInDto.setCdStagePersRole(CLIENT);
		}

		List<WorkloadStgPerLinkSelOutDto> workLoadList = workloadStgPerLinkSelDao
				.getWorkLoad(workloadStgPerLinkSelInDto);
		if (!CollectionUtils.isEmpty(workLoadList)) {
			idPersonChild = workLoadList.get(0).getIdTodoPersAssigned();

		}

		log.debug("Exiting method getPersonId in StageClosureRtrvServiceImpl");
		return idPersonChild;
	}

	/**
	 * 
	 * Method Name: getEventStatus(CallCSVC34D) Method Description: this Service
	 * gets EventStatus using StageId Of dayCare event which are pending
	 * 
	 * @param idStage
	 * @return eventStatus
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getEventStatus(Long idStage) {
		log.debug("Entering method getEventStatus in StageClosureRtrvServiceImpl");

		String eventStatus = eventDao.getEventStatus(idStage, DAY_CARE_TYPE, STATUS_PENDING);

		log.debug("Exiting method getEventStatus in StageClosureRtrvServiceImpl");
		return eventStatus;
	}

	/**
	 * 
	 * Method Name: getEligibility Method Description: This Method to Check
	 * Eligibility summary exists for perso
	 * 
	 * @param idPerson
	 * @return EligibilityOutDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public EligibilityOutDto getEligibility(Long idPerson) {
		// Entering method CallCSES38D in StageClosureRtrvServiceImpl
		log.debug("Entering method getEligibility in StageClosureRtrvServiceImpl");
		EligibilityInDto eligibilityInputDto = new EligibilityInDto();

		eligibilityInputDto.setIdPerson(idPerson);
		eligibilityInputDto.setDtScrDtCurrentDate(new Date());
		EligibilityOutDto eligibilityOutDto = null;
		if (!ObjectUtils.isEmpty(idPerson)) {
			List<EligibilityOutDto> eligiList = eligibilityDao.getEligibilityRecord(eligibilityInputDto);
			if (CollectionUtils.isNotEmpty(eligiList)) {
				eligibilityOutDto = eligibilityDao.getEligibilityRecord(eligibilityInputDto).get(0);
			}
		}

		log.debug("Exiting method getEligibility in StageClosureRtrvServiceImpl");
		return eligibilityOutDto;
	}

	/**
	 * 
	 * Method Name: eventStagePersonLink Method Description: this method to get
	 * the EventStagePersonLink
	 * 
	 * @param idStage
	 * @param eventStagePersonLinkDto
	 * @return List<EventStagePersonLinkInsUpdOutDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLink(Long idStage,
			EventStagePersonLinkInsUpdInDto eventStagePersonLinkDto) {
		// Entering method CallCCMN87D in StageClosureRtrvServiceImpl
		log.debug("Entering method eventStagePersonLink in StageClosureRtrvServiceImpl");

		EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();

		eventStagePersonLinkInsUpdInDto.setPageNbr(INITIAL_PAGE);
		eventStagePersonLinkInsUpdInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		eventStagePersonLinkInsUpdInDto.setIdStage(idStage);// pInputMsg.getStageDto().getIdStage()
		if (!TypeConvUtil.isNullOrEmpty(eventStagePersonLinkDto.getCdTask())) {
			eventStagePersonLinkInsUpdInDto.setCdTask(eventStagePersonLinkDto.getCdTask());
		}

		eventStagePersonLinkInsUpdInDto.setCdEventType(eventStagePersonLinkDto.getCdEventType());
		List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpdOutDtos = eventStagePersonLinkInsUpdDao
				.getEventAndStatusDtls(eventStagePersonLinkInsUpdInDto);
		log.debug("Exiting method eventStagePersonLink in StageClosureRtrvServiceImpl");
		return eventStagePersonLinkInsUpdOutDtos;

	}

	/**
	 * 
	 * Method Name: getLegalStatusPersonMax Method Description:
	 * 
	 * @param idPerson
	 * @return List<LegalStatusPersonMaxStatusDtOutDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<LegalStatusPersonMaxStatusDtOutDto> getLegalStatusPersonMax(Long idPerson) {
		// Entering method CallCSES32D in StageClosureRtrvServiceImpl
		log.debug("Entering method getLegalStatusPersonMax in StageClosureRtrvServiceImpl");
		if (!ObjectUtils.isEmpty(idPerson)) {
			LegalStatusPersonMaxStatusDtInDto legalStatusPersonMaxStatusDtInDto = new LegalStatusPersonMaxStatusDtInDto();
			legalStatusPersonMaxStatusDtInDto.setIdPerson(idPerson);

			return legalStatusPersonMaxStatusDtDao.getRecentLegelStatusRecord(legalStatusPersonMaxStatusDtInDto);
		}
		log.debug("Exiting method getLegalStatusPersonMax in StageClosureRtrvServiceImpl");
		return null;

	}

	/**
	 * 
	 * Method Name: getPersonPortfolio Method Description:
	 * 
	 * @param idPerson
	 * @return List<PersonPortfolioOutDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PersonPortfolioOutDto> getPersonPortfolio(Long idPerson) {
		// Entering method CallCCMN44D in StageClosureRtrvServiceImpl
		log.debug("Entering method getPersonPortfolio in StageClosureRtrvServiceImpl");
		if (!ObjectUtils.isEmpty(idPerson)) {
			PersonPortfolioInDto personPortfolioInDto = new PersonPortfolioInDto();
			personPortfolioInDto.setIdPerson(idPerson);

			return personPortfolioDao.getPersonRecord(personPortfolioInDto);
		}
		log.debug("Exiting method getPersonPortfolio in StageClosureRtrvServiceImpl");
		return null;

	}

	/**
	 * 
	 * Method Name: getEventStageTypeTask Method Description:
	 * 
	 * @param idStage
	 * @param cdStage
	 * @return List<EventStageTypeTaskOutDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EventStageTypeTaskOutDto> getEventStageTypeTask(Long idStage, String cdStage) {
		// Entering method CallCSESA3D in StageClosureRtrvServiceImpl
		log.debug("Entering method getEventStageTypeTask in StageClosureRtrvServiceImpl");
		EventStageTypeTaskInDto eventStageTypeTaskInDto = new EventStageTypeTaskInDto();

		eventStageTypeTaskInDto.setIdStage(idStage);
		eventStageTypeTaskInDto.setCdEventType(SVC_REF_CHKLST_EVENT_TYPE);
		if (STAGE_FAM_REUN.equalsIgnoreCase(cdStage)) {
			eventStageTypeTaskInDto.setCdTask(SVC_REF_CHKLST_FRE_TASK);
		} else if (STAGE_FAM_SUBCARE.equalsIgnoreCase(cdStage)) {
			eventStageTypeTaskInDto.setCdTask(SVC_REF_CHKLST_FSU_TASK);
		}
		log.debug("Exiting method getEventStageTypeTask in StageClosureRtrvServiceImpl");
		return eventStageTypeTaskDao.getEventDtls(eventStageTypeTaskInDto);

	}

	/**
	 * 
	 * Method Name: checkEventPending Method Description:
	 * 
	 * @param idStage
	 * @param cdEventStatus
	 * @param errorDtos
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void checkEventPending(Long idStage, String cdEventStatus, List<ErrorDto> errorDtos) {
		// Entering method CallCSVC28D in StageClosureRtrvServiceImp
		log.debug("Entering method checkEventPending in StageClosureRtrvServiceImpl");

		List<EventDto> eventDtos = eventDao.getEventInfo(idStage, cdEventStatus);
		if (CollectionUtils.isNotEmpty(eventDtos)) {
			EventDto eventDto = eventDtos.get(0);
			if ((!STR_CD_TASK_FRE.equalsIgnoreCase(eventDto.getCdTask()))
					&& (!STR_CD_TASK_FSU.equalsIgnoreCase(eventDto.getCdTask()))) {
				addError(ServiceConstants.MSG_SVC_EVENT_PENDING, errorDtos);
			}

		}

		log.debug("Exiting method checkEventPending in StageClosureRtrvServiceImpl");
	}

	/**
	 * 
	 * Method Name: getStageCode Method Description:
	 * 
	 * @param idCase
	 * @return List<StageCdOutDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StageCdOutDto> getStageCode(Long idCase) {
		// Entering method CallCCMNF6D in CpsInvConclValServiceImpl
		log.debug("Entering method getStageCode in StageClosureRtrvServiceImpl");
		StageCdInDto stageCdInDto = new StageCdInDto();
		stageCdInDto.setIdCase(idCase);
		List<StageCdOutDto> stageDtlsByDate = stageCdDao.getStageDtlsByDate(stageCdInDto);
		log.debug("Exiting method getStageCode in StageClosureRtrvServiceImpl");
		return stageDtlsByDate;

	}

	/**
	 * 
	 * Method Name: getStagePersonLinkRecord Method Description:
	 * 
	 * @param idStage
	 * @param idPerson
	 * @return StagePersonLinkRecordOutDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StagePersonLinkRecordOutDto getStagePersonLinkRecord(Long idStage, Long idPerson) {
		// Entering method CallCINV39D in StageClosureRtrvServiceImpl
		log.debug("Entering method getStagePersonLinkRecord in StageClosureRtrvServiceImpl");
		StagePersonLinkRecordInDto stagePersonLinkRecordInDto = new StagePersonLinkRecordInDto();
		StagePersonLinkRecordOutDto stagePersonLinkRecordOutDto = new StagePersonLinkRecordOutDto();

		stagePersonLinkRecordInDto.setIdPerson(idPerson);
		stagePersonLinkRecordInDto.setIdStage(idStage);
		List<StagePersonLinkRecordOutDto> stagePersonLinkRecordOutDtos = stagePersonLinkRecordDao
				.getStagePersonLinkRecord(stagePersonLinkRecordInDto);
		if (CollectionUtils.isNotEmpty(stagePersonLinkRecordOutDtos)) {
			stagePersonLinkRecordOutDto = stagePersonLinkRecordOutDtos.get(0);

		}

		log.debug("Exiting method getStagePersonLinkRecord in StageClosureRtrvServiceImpl");
		return stagePersonLinkRecordOutDto;
	}

	/**
	 * 
	 * Method Name: getSvcAuthEventLink Method Description:
	 * 
	 * @param idEvent
	 * @return List<SvcAuthEventLinkOutDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SvcAuthEventLinkOutDto> getSvcAuthEventLink(Long idEvent) {
		log.debug("Entering method getSvcAuthEventLink in StageClosureRtrvServiceImpl");

		// call CallCSES24D Retrieves ID SVC AUTH from the SVC AUTH EVENT LINK
		// table using input ID EVENT.
		SvcAuthEventLinkInDto svcAuthEventLink = new SvcAuthEventLinkInDto();
		svcAuthEventLink.setIdSvcAuthEvent(idEvent);
		log.debug("Exiting method getSvcAuthEventLink in StageClosureRtrvServiceImpl");
		return svcAuthEventLinkDao.getAuthEventLink(svcAuthEventLink);
	}

	/**
	 * 
	 * Method Name: getSvcAuthDetailName Method Description:This method will
	 * give list of data from SVC_AUTH_DTL table.
	 * 
	 * @param idSvcAuth
	 * @return List<SvcAuthDetailNameOutDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SvcAuthDetailNameOutDto> getSvcAuthDetailName(Long idSvcAuth) {
		// Entering method CallCLSS24D
		log.debug("Entering method getSvcAuthDetailName in StageClosureRtrvServiceImpl");
		SvcAuthDetailNameInDto svcAuthDetailNameInDto = new SvcAuthDetailNameInDto();
		svcAuthDetailNameInDto.setIdSvcAuth(idSvcAuth);
		List<SvcAuthDetailNameOutDto> serviceAuthentication = svcAuthDetailNameDao
				.getServiceAuthentication(svcAuthDetailNameInDto);
		log.debug("Exiting method getSvcAuthDetailName in StageClosureRtrvServiceImpl");
		return serviceAuthentication;
	}

	/**
	 * 
	 * /**
	 * 
	 * Method Name: getStageSituation Method Description:
	 * 
	 * @param idCase
	 * @return List<StageSituationOutDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StageSituationOutDto> getStageSituation(Long idCase) {
		// Method for CallCLSS30D
		log.debug("Entering method getStageSituation in StageClosureRtrvServiceImpl");
		StageSituationInDto stageSituationInDto = new StageSituationInDto();
		stageSituationInDto.setIdCase(idCase);
		log.debug("Exiting method getStageSituation in StageClosureRtrvServiceImpl");
		return stageSituationDao.getStageDetails(stageSituationInDto);

	}

	/**
	 * 
	 * Method Name: getCriminalCheckRecords Method Description: This method will
	 * give list of data from CRIMINAL_HISTORY table.
	 * 
	 * @param idStage
	 * @return Long ( criminal record PersonId)
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getCriminalCheckRecords(Long idStage) {
		// Entering method CallCSESC2D
		log.debug("Entering method getCriminalCheckRecords in StageClosureRtrvServiceImpl");
		CriminalHistoryRecordsCheckInDto criminalHistoryRecordsCheckInDto = new CriminalHistoryRecordsCheckInDto();
		criminalHistoryRecordsCheckInDto.setIdStage(idStage);
		List<CriminalHistoryRecordsCheckOutDto> criminalCheckRecords = criminalHistoryRecordsCheckDao
				.getCriminalCheckRecords(criminalHistoryRecordsCheckInDto);
		if (CollectionUtils.isNotEmpty(criminalCheckRecords)) {
			return criminalCheckRecords.get(0).getIdPerson();
		}
		log.debug("Exiting method getCriminalCheckRecords in StageClosureRtrvServiceImpl");
		return 0L;
	}

	/**
	 * 
	 * Method Name: updateTODOEvent Method Description: This method used for
	 * Updating the Todo Event
	 * 
	 * @param idEvent
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateTODOEvent(Long idEvent) {
		// Entering method CallCINV43D in StageClosureRtrvServiceImpl
		log.debug("Entering method updateTODOEvent in StageClosureRtrvServiceImpl");
		TodoUpdDtTodoCompletedInDto updateToDoDto = new TodoUpdDtTodoCompletedInDto();
		updateToDoDto.setIdEvent(idEvent);
		todoUpdDtTodoCompletedDao.updateTODOEvent(updateToDoDto);

		log.debug("Exiting method updateTODOEvent in StageClosureRtrvServiceImpl");
	}

	/**
	 * 
	 * Method Name: callPostEvent Method Description:
	 * 
	 * @param eventDto
	 * @param dtSystemDate
	 * @return PostEventStageStatusOutDto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PostEventStageStatusOutDto callPostEvent(EventDto eventDto, Date dtSystemDate) {
		log.debug("Entering method callPostEvent in StageClosureRtrvServiceImpl");
		PostEventStageStatusInDto postEventStageStatusInDto = new PostEventStageStatusInDto();

		if (0 == eventDto.getIdEvent()) {
			postEventStageStatusInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		} else {
			postEventStageStatusInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		}

		postEventStageStatusInDto.setCdTask(eventDto.getCdTask());
		postEventStageStatusInDto.setCdEventType(eventDto.getCdEventType());
		if (eventDto.getIdEvent() > 0) {
			postEventStageStatusInDto.setDtEventOccurred(eventDto.getDtEventOccurred());
		} else {
			postEventStageStatusInDto.setDtEventOccurred(dtSystemDate);
		}

		postEventStageStatusInDto.setIdEvent(eventDto.getIdEvent());
		postEventStageStatusInDto.setIdStage(eventDto.getIdStage());
		postEventStageStatusInDto.setIdPerson(eventDto.getIdPerson());
		postEventStageStatusInDto.setEventDescr(eventDto.getEventDescr());
		postEventStageStatusInDto.setCdEventStatus(eventDto.getCdEventStatus());
		log.debug("Exiting method CallPostEvent in StageClosureRtrvServiceImpl");
		return postEventStageStatusService.callPostEventStageStatusService(postEventStageStatusInDto);

	}

	/**
	 * 
	 * Method Name: callCloseOpenStage Method Description:
	 * 
	 * @param pInputMsg
	 * @param dtSystemDate
	 * @param szCdStageClosureReason
	 * @param szCdStageOpen
	 * @return CloseOpenStageOutputDto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CloseOpenStageOutputDto callCloseOpenStage(StageClosureRtrvDto pInputMsg, Date dtSystemDate,
			String szCdStageClosureReason, String szCdStageOpen) {
		log.debug("Entering method CallCloseOpenStage in StageClosureRtrvServiceImpl");
		CloseOpenStageInputDto closeOpenStageInputDto = new CloseOpenStageInputDto();

		closeOpenStageInputDto.setIdPerson(pInputMsg.getEventDto().getIdPerson());
		closeOpenStageInputDto.setDtStageStart(dtSystemDate);
		closeOpenStageInputDto.setIdStage(pInputMsg.getStageDto().getIdStage());
		closeOpenStageInputDto.setCdStage(pInputMsg.getStageDto().getCdStage());
		closeOpenStageInputDto.setCdStageReasonClosed(szCdStageClosureReason);
		closeOpenStageInputDto.setCdStageProgram(pInputMsg.getStageDto().getCdStageProgram());
		closeOpenStageInputDto.setCdStageOpen(szCdStageOpen);
		closeOpenStageInputDto.setSysIndSStgOpenOnly(ServiceConstants.N);
		log.debug("Exiting method CallCloseOpenStage in StageClosureRtrvServiceImpl");

		return closeOpenStageService.closeOpenStage(closeOpenStageInputDto);

	}

	/**
	 * 
	 * Method Name: callCloseStageCase Method Description:
	 * 
	 * @param stageClosureRtrvDto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void callCloseStageCase(StageClosureRtrvDto stageClosureRtrvDto) {
		log.debug("Entering method CallCloseStageCase in StageClosureRtrvServiceImpl");
		CloseStageCaseInputDto closeStageCaseInput = new CloseStageCaseInputDto();
		closeStageCaseInput.setCdStage(stageClosureRtrvDto.getStageDto().getCdStage());
		closeStageCaseInput.setCdStageProgram(stageClosureRtrvDto.getStageDto().getCdStageProgram());
		closeStageCaseInput.setCdStageReasonClosed(stageClosureRtrvDto.getStageDto().getCdStageReasonClosed());
		closeStageCaseInput.setIdStage(stageClosureRtrvDto.getStageDto().getIdStage());
		closeStageCaseInput.setEventDescr(stageClosureRtrvDto.getEventDto().getEventDescr());
		closeStageCaseService.closeStageCase(closeStageCaseInput);

		log.debug("Exiting method CallCloseStageCase in StageClosureRtrvServiceImpl");
	}

	/**
	 * Method Name: isActiveReferral Method Description: Returns true/false if
	 * there is an active child referral for stage id
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean isActiveReferral(Long idStage) {
		return stagePersonDao.isActiveReferral(idStage);
	}

	/**
	 * Method Name: getPrtActiveActionPlan Method Description: This method
	 * returns a boolean value based on whether or not a sub stage is currently
	 * in open status.
	 * 
	 * @param idPerson
	 * @return boolean
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean getPrtActiveActionPlan(Long idPerson) {
		return stagePersonDao.getPrtActiveActionPlan(idPerson);
	}

	/**
	 * Method Name: getPrtActionPlanInProcStatus Method Description: This method
	 * returns a boolean value based on whether or not a sub stage is currently
	 * in Proc status.
	 * 
	 * @param idPerson
	 * @return boolean
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean getPrtActionPlanInProcStatus(Long idPerson) {
		return stagePersonDao.getPrtActionPlanInProcStatus(idPerson);
	}

	/**
	 * Method Name: getPlacementsByStageId Method Description:CLSS84D this
	 * service will fetchPlacments
	 * 
	 * @param idPerson
	 * @return List<PlacementDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementDto> getPlacementsByStageId(Long idSatge) {
		log.debug("Entering method CallCloseStageCase in StageClosureRtrvServiceImpl");

		return placementDao.getActualPlacement(idSatge);
	}

	/**
	 * 
	 * Method Name: getLegalActionType Method Description: this Service Will
	 * fetch LegalActionEvent( CSESD6D )
	 * 
	 * @param idPerson
	 * @param idCase
	 * @param idStage
	 * @return LegalActionEventOutDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public LegalActionEventOutDto getLegalActionEvent(Long idPerson, Long idCase, Long idSatge) {
		log.debug("Entering method CallCloseStageCase in StageClosureRtrvServiceImpl");

		return legalActionEventDao.getLegalActionType(idPerson, idCase, idSatge);
	}

	/**
	 * Method Name: getPlacementDtoByPersonId Method Description: CSES44D--This
	 * Service performs a complete row retrieval from the most recent actual
	 * adoption placement from the PLACEMENT table, given the ID_PLCMT_CHILD.
	 * This Service is a sister Service of CSES34D.
	 * 
	 * @param idPlcmtChild
	 * @return List<PlacementDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementDto> getPlacementDtoByPersonId(Long idPerson) {
		log.debug("Entering method CallCloseStageCase in StageClosureRtrvServiceImpl");

		return placementDao.getMostRecentPlacement(idPerson);
	}

	/**
	 * 
	 * Method Name: getPlacementDtoByPersonIdandPlacement Method Description:
	 * CSES44D This service retrieves the most recent living arrangement,
	 * placement start date from the Placement table using ID_PLCMT_CHILD.
	 * 
	 * @param idPlcmtChild
	 * @return List<PlacementDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PlacementDto> getPlacementDtoByPersonIdandPlacement(Long idPerson) {
		log.debug("Entering method CallCloseStageCase in StageClosureRtrvServiceImpl");

		return placementDao.getMostRecentLinvingArrangement(idPerson, PCA_PLCMNT);
	}

	// CSES78D
	/**
	 * Method Name: getLatestLegalStatus (CSES78D) Method Description:This
	 * Service retrieves a full row from LEGAL_STATUS.
	 * 
	 * @param idPerson
	 * @param idCase
	 * @return LegalStatusDetailDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public LegalStatusDetailDto getLatestLegalStatus(Long idPerson, Long idCase) {
		log.debug("Entering method CallCloseStageCase in StageClosureRtrvServiceImpl");

		return legalStatusDao.getLatestLegalStatus(idPerson, idCase);
	}

	// CCMNH9D
	/**
	 * Method Name: getPersonLegalStatus(CCMNH9D) Method Description:Retrieves
	 * all the person with Legal Statuses in a given Case.
	 * 
	 * @param idCase
	 * @param cdEventType
	 * @return List<StagePersDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StagePersDto> getPersonLegalStatus(Long idCase) {
		log.debug("Entering method CallCloseStageCase in StageClosureRtrvServiceImpl");

		return stagePersonLinkDao.getPersonLegalStatus(idCase, LEG_STATUS_EVENT);
	}

	// CCMNI8D
	/**
	 * 
	 * Method Name: getPCASubsidyRecord Method Description:CCMNI8D - Retrieves
	 * the PCA subsidy record for an event id.
	 * 
	 * @param idEvent
	 * @return List<PcaSubsidyOutDto>
	 */

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PcaSubsidyOutDto> getPCASubsidyRecord(Long idEvent) {
		log.debug("Entering method CallCloseStageCase in StageClosureRtrvServiceImpl");

		return pcaSubsidyDao.getPCASubsidyRecord(idEvent);

	}

	/**
	 * 
	 * Method Name: saveSubmit Method Description: this is the consolidated
	 * Service for save and Submit ,first it calls save method , then it preform
	 * extra validations that is handled through performRemainingValdations
	 * 
	 * @param idEvent
	 * @return List<PcaSubsidyOutDto>
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StageClosureRtrvRes saveSubmit(StageClosureRtrvReq stageClosureRtrvReq) {

		StageClosureRtrvRes stageClosureRtrvRes = saveStageClosure(stageClosureRtrvReq);
		// we need to perform extra validations after calling the save service.
		performRemainingValdations(stageClosureRtrvReq.getCommonDto(), stageClosureRtrvRes.getErrorDtos(),
				stageClosureRtrvRes);

		return stageClosureRtrvRes;

	}

	private void addError(int errorCode, List<ErrorDto> errorDtos) {
		ErrorDto error = new ErrorDto();
		error.setErrorCode(errorCode);
		errorDtos.add(error);
	}

	/**
	 * 
	 * Method Name: performRemainingValdations Method Description:this method
	 * will performs additional checks apart from the CSUB68
	 * 
	 * @param commonDto
	 * @param errorList
	 * @param stageClosureRtrvRes
	 */

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void performRemainingValdations(CommonDto commonDto, List<ErrorDto> errorList,
			StageClosureRtrvRes stageClosureRtrvRes) {
		HashMap<String, String> personInFDTCEdit = new HashMap<>();
		String mostRecentFDTCSubType = null;
		boolean startsFDTCFlag = false;
		HashMap<String, String> hashMapFDTC = new HashMap<>();
		int idFDTCStage = 0;
		HashMap<Long, String> personFDTCMap = new HashMap<>();
		String personFullName = ServiceConstants.EMPTY_STRING;

		StageClosureRtrvDto stageClosureRtrvDto = stageClosureRtrvRes.getStageclosureRtrvDto();

		String stage = commonDto.getCdStage();

		// Populate the error list and set the error branch if error messages
		// returned

		/* Race and ethnicity are required for Principals in Stage Closure. */
		// validateRaceStatForPRN
		validateRaceStatForPRN(commonDto.getIdStage(), errorList);

		// Check if any legal action subtype of Starts, for Legal Action of type
		// Family Drug Treatment Court,
		// this check is performed only for Stages FRE and FSU */

		if (ServiceConstants.CSTAGES_FRE.equalsIgnoreCase(commonDto.getCdStage())
				|| ServiceConstants.CSTAGES_FSU.equalsIgnoreCase(commonDto.getCdStage())) {
			personFDTCMap = stageClosureService.getPersonIdInFDTC(commonDto.getIdCase());

			for (Map.Entry<Long, String> entry : personFDTCMap.entrySet()) {
				if (!startsFDTCFlag) {
					Long personId = entry.getKey();
					hashMapFDTC = stageClosureService.getMostRecentFDTCSubtype(personId);
					personFullName = entry.getValue();

					if (!hashMapFDTC.isEmpty()) {
						mostRecentFDTCSubType = hashMapFDTC.get(ServiceConstants.CD_LEGAL_ACT_ACTN_SUBTYPE);
						idFDTCStage = Integer.parseInt(hashMapFDTC.get(ServiceConstants.ID_EVENT_STAGE));
					}

					// If most recent FDTC legal action is a Starts or
					// is a Continues but recorded in a different Stage then set
					// the edit flag to true

					if (ServiceConstants.CFDT_010.equalsIgnoreCase(mostRecentFDTCSubType)
							|| (ServiceConstants.CFDT_030.equalsIgnoreCase(mostRecentFDTCSubType)
									&& commonDto.getIdStage() != idFDTCStage)) {
						startsFDTCFlag = true;
						personInFDTCEdit.put(personId.toString(), personFullName);
					}
				}
			}

			// validate PCSP records on Save

			if (ServiceConstants.CPGRMS_CPS.equals(commonDto.getStageProgram())) {

				// check if placements not ended or verified
				int pcspPlcmtErrMsg = pcspService.valPCSPPlcmt(commonDto.getCdStage(), commonDto.getIdCase(),
						commonDto.getIdStage());

				if (pcspPlcmtErrMsg > 0) {

					addError(pcspPlcmtErrMsg, errorList);
				}

				// check if assessments not completed
				if (pcspListPlacmtDao.hasOpenPCSPAsmntForStage(commonDto.getIdStage())) {
					addError(ServiceConstants.MSG_PCSP_VAL_ASMNT_COMP_OR_DEL, errorList);

				}

			}
		}
		if (startsFDTCFlag) {
			addError(ServiceConstants.MSG_ERR_FDTC_CLOSURE, errorList);

		}

		// Call the method which determines if there are MPS persons associated
		// with
		// this stage. If there are MPS persons then a message is added to the
		// errorList array.
		boolean bpersMPSSearchReq = false;
		bpersMPSSearchReq = personMPSDao.getNbrMPSPersStage(commonDto.getIdStage(), ServiceConstants.YES);

		if (bpersMPSSearchReq) {
			addError(ServiceConstants.MSG_MPS_PERSON_NOT_SEARCHED, errorList);

		}
		if (ServiceConstants.CSTAGES_SUB.equalsIgnoreCase(commonDto.getCdStage())
				|| ServiceConstants.CSTAGES_FSU.equalsIgnoreCase(commonDto.getCdStage())
				|| ServiceConstants.CSTAGES_FRE.equalsIgnoreCase(commonDto.getCdStage())) {
			validateChildSsccReferral(commonDto.getIdStage(), errorList);
		}

		// If Closure reason of "Child Placed in PCA" is selected.
		// Validate that The Legal Action is PMC to Relative or PMC to Fictive
		// Kin.
		// and the Legal Status is FPS Resp Terminated.
		// and the PCA Application was submitted to the Eligibility worker.
		String closureReason = stageClosureRtrvDto.getStageDto().getCdStageReasonClosed();
		if (ServiceConstants.CSTAGES_SUB.equalsIgnoreCase(commonDto.getCdStage())
				&& ((ServiceConstants.CCLOSSUB_110.equals(closureReason))) ) {
			validateChildPlacedinPCA(commonDto.getIdCase(), commonDto.getIdStage(), errorList);
		}

		// Defect#13181 : As per the direction from program/requirement, PRT shouldn't effect the
		// stage closure as PRTs in CPOS is no more being done. Hence, allowing the stage to close without checking if
		// any open PRT is available. Commenting below condition will not validate stage closure for Open PRT.
		/*if (ServiceConstants.CSTAGES_SUB.equalsIgnoreCase(commonDto.getCdStage())) {
			checkChildOpenPrt(commonDto.getIdStage(), errorList);
		}*/

		// Validation that don't allow closure of ADO stage
		// if there is a latest existing Adoption Adoption Application
		// which is in PEND or COMP status (not withdrawn - un-approved status).
		if (ServiceConstants.CSTAGES_ADO.equalsIgnoreCase(commonDto.getCdStage())) {
			// Validate to see if there are no open adoption assistance
			// eligibility for that stage
//artf227805
			if (StringUtils.isNotBlank(closureReason) && !ServiceConstants.CCLOSADO_010.equals(closureReason) && !ServiceConstants.CCLOSADO_070.equals(closureReason) ) {
				validateAdoptionAsstClos(commonDto.getIdStage(), errorList);
			}

			validateAdptAssistAppl(commonDto.getIdStage(), errorList);
		}

		String pgm = commonDto.getStageProgram();

		if (CollectionUtils.isEmpty(errorList) && stage.equals(STAGE_AOC) && CAPS_PROG_APS.equals(pgm)) {

			boolean guardDetOutcomeNull = validateGuardianDetail(commonDto.getIdCase());
			if (guardDetOutcomeNull) {
				addError(ServiceConstants.MSG_GUARD_FINAL_OUTCOME_REQ, errorList);
			}

		}

		// Check if any DPS Criminal History check is pending, if yes display an
		// Error message
		if (contactDetailsService.isCrimHistCheckPending(commonDto.getIdStage())) {
			addError(ServiceConstants.MSG_CRML_HIST_CHECK_STAGE_PEND, errorList);
		}
		if (CollectionUtils.isNotEmpty(errorList)) {

			if (!ObjectUtils.isEmpty(stageClosureRtrvDto.getIdPerson()) && stageClosureRtrvDto.getIdPerson() != 0) {
				
				HashMap personDetail = criminalHistoryDao.checkCrimHistAction(commonDto.getIdStage());
				if (!personDetail.isEmpty()) {
					String namePerson = (String) personDetail.get(ServiceConstants.NAMEPERSON);
					// set this name into DTo , and set back to the userData
					stageClosureRtrvDto.setNmPerson(namePerson);

				}

			}

		} else {

			// Since the Open FDTC check is not done by saveService, check for
			// Open
			// FDTC Legal Action
			// is also done in the Success path.
			// sending the Error Message , if this message
			// present than show page with error

			if (startsFDTCFlag) {
				String personFDTCEdit = getFDTCPersonDetails(personInFDTCEdit);
				stageClosureRtrvRes.setFdtcErrorMessage(personFDTCEdit);

			}
		}

		stageClosureRtrvRes.setStageclosureRtrvDto(stageClosureRtrvDto);

	}

	/**
	 * Method Name: validateRaceStatForPRN Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	private void validateRaceStatForPRN(Long idStage, List<ErrorDto> errorList) {

		if (personUtilityService.isPRNRaceStatMissing(idStage)) {
			addError(ServiceConstants.MSG_INV_NO_RAC_STAT, errorList);
		}
	}

	/**
	 * 
	 * Method Name: validateChildSsccReferral Method Description:
	 * 
	 * @param idStage
	 * @param errorList
	 */
	private void validateChildSsccReferral(Long idStage, List<ErrorDto> errorList) {

		boolean isActiveChildReferral = isActiveReferral(idStage);
		if (isActiveChildReferral) {
			addError(ServiceConstants.MSG_SSCC_REFERRAL_AT_STAGE_CLOSURE, errorList);
		}

	}

	/**
	 * 
	 * This method performs following validations if Closure reason of "Child
	 * Placed in PCA" is selected while closing the SUB stage.
	 * 
	 * Validate that the Legal Action is PMC to Relative or PMC to Fictive Kin.
	 * and the Legal Status is FPS Resp Terminated. and the PCA Application was
	 * submitted to the Eligibility worker.
	 */
	private void validateChildPlacedinPCA(Long idCase, Long idStage, List<ErrorDto> errorList) {

		// Validate that the PCA Application was submitted to the Eligibility
		// worker.

		PcaAppAndBackgroundDto latestApp = pcaAppAndBackgroundDao.selectLatestAppForStage(idStage);
		if (ObjectUtils.isEmpty(latestApp.getIdPcaEligApplication()) || latestApp.getIdPcaEligApplication() == 0) {
			addError(ServiceConstants.MSG_PCA_CLOSE_SUB_APP_NOTSUBMITTED, errorList);
		} else {
			Long idPerson = latestApp.getIdPerson();
			// Validate that the Legal Action is PMC to Relative or PMC to
			// Fictive Kin.

			LegalActionEventInDto legalActionEventInDto = new LegalActionEventInDto();
			legalActionEventInDto.setIdStage(idStage);
			legalActionEventInDto.setIdCase(idCase);
			legalActionEventInDto.setIdPerson(idPerson);
			
			//Fix for defect 13083 - Legal Action Error Closing Stage-PCA
			LegalActionEventOutDto legalActionEventOutDto = legalActionEventDao
					.selectLatestLegalActionOutcome(legalActionEventInDto);
			String outcome = ServiceConstants.EMPTY_STRING;
			if (!ObjectUtils.isEmpty(legalActionEventOutDto)) {
				outcome = legalActionEventOutDto.getCdLegalActOutcome();
			}
			if (StringUtils.isEmpty(outcome)) {
				addError(ServiceConstants.MSG_PCA_CLOSE_SUB_LEGALACT_PMC, errorList);
			}
			
			// Validation - The Legal Status is FPS Resp Terminated
			LegalStatusValueDto legalStatusValueBean = serviceAuthorizationService.selectLatestLegalStatus(idPerson,
					ServiceConstants.CLEGSTAT_120);
			if (ObjectUtils.isEmpty(legalStatusValueBean.getIdLegalStatEvent())
					|| legalStatusValueBean.getIdLegalStatEvent() == 0) {

				addError(ServiceConstants.MSG_PCA_CLOSE_SUB_LEGALSTATUS_FPSRESPTRM, errorList);
			}
			// Validate that Legal Status Discharge Reason is Relative or
			// Fictive Kin.
			else if (!ServiceConstants.CLSDSCHG_030.equals(legalStatusValueBean.getCdDischargeRsn())
					&& !ServiceConstants.CLSDSCHG_040.equals(legalStatusValueBean.getCdDischargeRsn())) {
				addError(ServiceConstants.MSG_NO_LEGAL_STATUS_DISCHARGE_REASON, errorList);
			}
			// Validate that the Closure Reason for Latest Placement is "Child
			// Placed in PCA"

			boolean isPlcmtEndPlcaedinPCA = false;
			List<PlacementValueDto> tmpPlcmtList = placementService.findAllPlacementsForStage(idStage);
			if (CollectionUtils.isNotEmpty(tmpPlcmtList)) {
				PlacementValueDto latestPlcmt = tmpPlcmtList.get(tmpPlcmtList.size() - 1);
				if (!ObjectUtils.isEmpty(latestPlcmt)
						&& ServiceConstants.CPLREMRS_230.equals(latestPlcmt.getCdPlcmtRemovalRsn())) {
					isPlcmtEndPlcaedinPCA = true;
				}
			}
			if (!isPlcmtEndPlcaedinPCA) {
				addError(ServiceConstants.MSG_SUB_CLOSE_PLACE_END_PLACED_PCA, errorList);
			}
		}
	}

	/**
	 * This method is called when save and submit sub stage closure to check if
	 * there is an active child prt plan ochu
	 * 
	 */
	private void checkChildOpenPrt(Long idStage, List<ErrorDto> errorList) {

		Long idPerson = stagePersonDao.getPrimaryClientIdForStage(idStage);
		boolean isChildPrtOpen = getPrtActiveActionPlan(idPerson);
		boolean isChildPrtInProc = getPrtActionPlanInProcStatus(idPerson);
		if (isChildPrtOpen || isChildPrtInProc) {
			addError(ServiceConstants.MSG_SUB_CHILD_IN_OPEN_PRT, errorList);
		}
	}

	/**
	 * This method performs following validations to check if there are any open
	 * adoption subsidies for that stage
	 * 
	 * @param -
	 *            idStage
	 * @param -
	 *            errorList
	 */
	private void validateAdoptionAsstClos(Long idStage, List<ErrorDto> errorList) {

		boolean isAdptSubsidyEnded = adoptionAsstncService.isAdptSubsidyEnded(idStage);
		if (!isAdptSubsidyEnded) {
			addError(ServiceConstants.MSG_STG_CLOS_ADO_A, errorList);
		}

	}

	/**
	 * This method checks if there is a latest existing Adoption Adoption
	 * Application which is in PEND or COMP status (not withdrawn - unapproved
	 * status) before closing the ADO stage.
	 * 
	 * @param context
	 * @param errorList
	 */
	private void validateAdptAssistAppl(Long idStage, List<ErrorDto> errorList) {

		// Throw and error message that the latest Adopt Assist Application is
		// in
		// PEND or COMP status so cannot close the ADO stage.
		boolean isAdptAssistApplUnapprvd = applicationBackgroundService.isAdptAssistApplUnapproved(idStage);
		if (isAdptAssistApplUnapprvd) {
			addError(ServiceConstants.MSG_AA_UNAPRVD_ADO_STAGE_CLOS_NT_ALLWD, errorList);
		}

	}

	/**
	 * This method queries to find if there are guardianship records of type DAD
	 * with null final outcome.
	 *
	 * @param idCase
	 * @return isGuardianDtlNull
	 */
	private boolean validateGuardianDetail(Long idCase) {

		return guardianshipDtlService.isDADFinalOutcomeDocumented(idCase);
	}

	/**
	 * 
	 * Method Name: getFDTCPersonDetails Method Description:check For
	 * 
	 * @param personInFDTCEdit
	 * @return
	 */
	private String getFDTCPersonDetails(HashMap<String, String> personInFDTCEdit) {
		StringBuilder fdtcPersonDt = new StringBuilder();
		String personName;
		String fdtcPersonDetails = ServiceConstants.EMPTY_STRING;

		if (!personInFDTCEdit.isEmpty()) {

			for (Map.Entry<String, String> entry : personInFDTCEdit.entrySet()) {
				personName = entry.getValue();

				fdtcPersonDt.append("PID:");
				fdtcPersonDt.append(entry.getKey());
				fdtcPersonDt.append(" Name:");
				fdtcPersonDt.append(personName);
				fdtcPersonDt.append("; ");
			}
			fdtcPersonDetails = fdtcPersonDt.toString();
			fdtcPersonDetails = !ServiceConstants.EMPTY_STRING.equals(fdtcPersonDetails)
					? fdtcPersonDetails.substring(0, fdtcPersonDetails.length() - 2) : ServiceConstants.EMPTY_STRING;
		}

		return fdtcPersonDetails;
	}

	/**
	 * 
	 * Method Name: checkDateIsEmpty Method Description:this method check date
	 * empty or default Date
	 * 
	 * @param date
	 * @return
	 */
	private boolean dateIsEmpty(Date date) {

		return (ObjectUtils.isEmpty(date)) || (date.compareTo(DateUtils.getDefaultFutureDate()) == 0);

	}

	/**
	 * 
	 * Method Name: checkDateIsNotEmpty Method Description: this method check
	 * date not empty and not default Date
	 * 
	 * @param date
	 * @return
	 */
	private boolean dateIsNotEmpty(Date date) {

		// stageSituationOutDto.getDtStageClose()
		return (!ObjectUtils.isEmpty(date)) && (date.compareTo(DateUtils.getDefaultFutureDate()) != 0);

	}


	/**
	 * 
	 * Method Name: validationForSexualVctmizationQues Method Description: this method check
	 * if all the PRN less then 18 have answered Sexual Vctmization Question
	 * 
	 * @param StageClosureRtrvDto
	 * @return boolean
	 */
	
	private boolean validationForSexualVctmizationQues(StageClosureRtrvDto stageClosureRtrvDto) {
	
		String stage = stageClosureRtrvDto.getStageDto().getCdStage();
		Date dtDtSystemDate = new Date();
		
		if(stage.equals(STAGE_SUBCARE) || stage.equals(STAGE_FAM_REUN) || stage.equals(STAGE_FAM_PRES)) {
			long stageid = stageClosureRtrvDto.getStageDto().getIdStage(); //cdStagePersType
			List<ContactPrincipalsCollateralsDto> listPpl = personListByStageDao.getPRNPersonDetailsForStage(stageClosureRtrvDto.getStageDto().getIdStage(),ServiceConstants.PRINCIPAL );
			if(!ObjectUtils.isEmpty(listPpl)) {
				for(ContactPrincipalsCollateralsDto dto:listPpl) {
					ContactPrincipalsCollateralsDto person = dto;
					int personage = DateUtils.calculatePersonsAgeInYears(dto.getDtPersonBirth(),dtDtSystemDate);//person.getPersonAge();
					if( personage  < ServiceConstants.MIN_PG_AGE) {
						boolean answered = personListByStageDao.getIndChildSxVctmzinHistory(person.getIdPerson());
						if(!answered) {
							return false;
						}
					}	
				}

			}

		}
		return true;
	}

	
	private static final String PRIMARY_CHILD = "PC";
	private static final String CLIENT = "CL";
	public static final String AUTHORIZED_LOC = "ALOC";
	private static final String PRS_RESP_TERM = "120";
	private static final String NON_SUIT = "010";
	//artf227805
	private static final String EXIT_TO_TRIBE = "120";
	private static final String PMC_TO_OTHER = "020";
	private static final String CVS_NOT_OBTAINED = "150";
	private static final String REAS_CVS_NOT_OBT = "030";
	private static final String CHILD_EMANCIPATED = "100";
	private static final String REAS_EMANCIPATED = "080";
	private static final String OWN_HOME = "DA";
	private static final String RELATIVES_HOME = "DD";
	private static final String REAS_OWN_HOME = "040";
	private static final String REAS_REL_HOME = "050";
	private static final String CHILD_RAN_AWAY = "130";
	private static final String REAS_RAN_AWAY = "070";
	private static final String ADOPTION_CONSUM = "090";
	private static final String REAS_ADOPT_CONSUM = "010";
	private static final String ADO_PMC_TO_OTHER = "030";
	private static final String REAS_AGED_OUT = "040";
	private static final String REAS_CHILD_DEATH = "050";
	private static final String SUB_AGED_OUT = "090";
	private static final String SUB_CHILD_DEATH = "100";
	private static final String PAD_CHILD_DEATH = "080";
	private static final String REAS_PARENT_DEATH = "050";
	private static final String ADOPT_DISRUPTION = "020";
	private static final String PRIV_AGCY_ADPT_HM = "71";
	private static final String PCA_CHILD_DEATH = "010";
	private static final String COU_ORD_FIC_KIN = "DR";
	private static final String COU_ORD_REL_KIN = "DQ";
	private static final String REL_HOME = "DD";
	private static final String FIC_KIN_HOME = "DF";
	private static final String KIN_SHIP_NON_LIC = "090";
	private static final String PMC_TO_REL_KIN = "100";
	private static final String PMC_TO_OTHER_KIN = "109";
	private static final String PMC_NON_SUIT = "330";
	private static final String SEC_POS_CLOSE_REAS_ONE = "1";
	private static final String SEC_POS_CLOSE_REAS_TWO = "2";
	private static final String SEC_POS_CLOSE_REAS_THREE = "3";
	private static final String SEC_POS_CLOSE_REAS_FOUR = "4";
	private static final String SEC_POS_CLOSE_REAS_FIVE = "5";
	private static final String SEC_POS_CLOSE_REAS_SIX = "6";
	private static final String SEC_POS_CLOSE_REAS_SEVEN = "7";
	private static final String SEC_POS_CLOSE_REAS_EIGHT = "8";
	private static final String SEC_POS_CLOSE_REAS_NINE = "9";
	private static final String SEC_POS_CLOSE_REAS_ZERO = "0";
	private static final String SEC_POS_CD_STAGE_D = "D";
	private static final String SEC_POS_CD_STAGE_O = "O";
	private static final String SUBCARE = "S";
	private static final String ADO_AOC = "A";
	private static final String POST_ADOPTION = "P";
	private static final String FAMILY_STAGE = "F";
	private static final String STAGE_ADOPTION = "ADO";
	private static final String STAGE_POST_ADOPT = "PAD";
	private static final String STAGE_FAM_REUN = "FRE";
	private static final String STAGE_FAM_SUBCARE = "FSU";
	private static final String STAGE_PERM_CARE = "PCA";
	private static final String KIN_PROGRAM = "KIN";
	private static final String STAGE_FAM_PRES = "FPR";
	private static final String CASE_REL_SPEC_REQ = "C-";
	private static final String CASE_REL_SPEC_JPC_REQ = "C-JPC";
	private static final String CASE_REL_SPEC_IC_REQ = "C-IC";
	private static final String CASE_REL_SPEC_TYC_REQ = "C-TYC";
	private static final String CASE_REL_SPEC_PB_REQ = "C-PB";
	private static final String CASE_REL_SPEC_RC_REQ = "C-RC";
	private static final String CASE_REL_SPEC_OS_REQ = "C-OS";
	private static final String CASE_REL_SPEC_CO_REQ = "C-CO";
	private static final String CASE_REL_SPEC_AS_REQ = "C-AS";
	private static final String CASE_REL_SPEC_ICA_REQ = "C-ICA";
	private static final int INITIAL_PAGE = 1;
	private static final String CLOSE_OPEN_STAGE = "1";
	private static final String OPEN_STAGE = "2";
	private static final String STR_CD_TASK_FRE = "5560";
	private static final String STR_CD_TASK_FSU = "4110";
	private static final String STAGE_SUBCARE = "SUB";
	private static final String STAGE_AOC = "AOC";
	private static final String SUB_SVC_AUTH = "3020";
	private static final String AOC_SVC_AUTH = "5040";
	private static final String ADO_SVC_AUTH = "8530";
	private static final String PAD_SVC_AUTH = "9020";
	private static final String FSU_SVC_AUTH = "4190";
	private static final String FRE_SVC_AUTH = "5640";
	private static final String STATUS_APPROVED = "APRV";
	private static final String SERVICE_AUTH_TYPE = "AUT";
	private static final String DAY_CARE_TYPE = "DCR";
	private static final String ADOPT_SUBSIDY_TYPE = "ADP";
	private static final String PCA_SUBSIDY_TYPE = "PCA";
	private static final String PAD_ADOPT_SUB = "9100";
	private static final String PCA_ADOPT_SUB = "9580";
	private static final String PCA_PLCMNT = "9570";
	private static final String PLCMT_TYPE_PRS_CON = "030";
	private static final String PLCMT_TYPE_PRS_FAD = "020";
	private static final String PLCMT_TYPE_UNAUTH = "080";
	private static final String PLCMT_TYPE_TYC = "050";
	private static final String PLCMT_TYPE_JPC = "060";
	private static final String LEGAL_STS_TYPE_ONE = "010";
	private static final String LEGAL_STS_TYPE_TWO = "020";
	private static final String LEGAL_STS_TYPE_THREE = "030";
	private static final String LEGAL_STS_TYPE_FOUR = "040";
	private static final String LEGAL_STS_TYPE_FIVE = "050";
	private static final String LEGAL_STS_TYPE_SIX = "060";
	private static final String LEGAL_STS_TYPE_SEVEN = "070";
	private static final String LEGAL_STS_TYPE_EIGHT = "080";
	private static final String LEGAL_STS_TYPE_ONE_THIRTY = "130";
	private static final String LEGAL_STS_TYPE_ONE_FOURTY = "140";
	private static final String STAFF = "STF";
	private static final String PRINCIPAL = "PRN";
	private static final String ADOPTIVE_PLACEMENT = "GT";
	private static final String STATUS_NEW = "NEW";
	private static final String STATUS_PROCESS = "PROC";
	private static final String STATUS_COMPLETE = "COMP";
	private static final String STATUS_PENDING = "PEND";
	private static final String FCE_ELIG_TASK = "3120";
	private static final String FCE_ELIG_TYPE = "FCD";
	private static final String DAY_CARE = "99R";
	private static final String FORMER_DAY_CARE = "40M";
	private static final String GENERAL_PROT_CARE = "40W";
	private static final String IVE_FC_DAY_CARE = "40A";
	private static final String REGISTERED_FAMILY_HM = "99Q";
	private static final String STATE_PAID_FC_DAY_CARE = "40B";
	private static final String LEG_STATUS_EVENT = "LES";
	private static final String PLCMT_TYPE_NON_CERT_PERSON = "010";
	private static final String PLCMT_TYPE_NON_FPS_PAID = "040";
	private static final String SVC_REF_CHKLST_FRE_TASK = "2307";
	private static final String SVC_REF_CHKLST_FSU_TASK = "2308";
	private static final String SVC_REF_CHKLST_EVENT_TYPE = "CHK";
	private static final String KIN_SERVICE_AUTH_68B = "68B";
	private static final String KIN_SERVICE_AUTH_68C = "68C";
	private static final String KIN_SERVICE_AUTH_68D = "68D";
	private static final String KIN_SERVICE_AUTH_68E = "68E";
	private static final String KIN_SERVICE_AUTH_68F = "68F";
	private static final String KIN_SERVICE_AUTH_68G = "68G";
	private static final String KIN_SERVICE_AUTH_68H = "68H";
	private static final String KIN_SERVICE_AUTH_68I = "68I";
	private static final String NON_CUST_PARENT_HM = "DE";
	private static final String CHILD_TURNED_18 = "510";
	private static final String CHILD_DEATH = "520";
	private static final String ACUTE_CARE_RTN_HOME = "530";
	private static final String CAREGIVER_REQUESTED = "540";
	private static final String CHILD_NM_CHANGED = "550";
	private static final String CHILD_REQUESTED = "560";
	private static final String CPS_INITIATED = "570";
	private static final String RISK_ABUSE_NEGLECT = "580";
	private static final String SUPERVISOR_REQUEST = "590";
	private static final String PREP_ADULT = "PAL";
	private static final String ADOPTION = "ADO";
	private static final String INVESTIGATION = "INV";
	private static final String ARI_STAGE = "ARI";
	private static final String FPR_PROGRAM = "FPR";
	private static final String FRE_PROGRAM = "FRE";
	private static final String FSU_PROGRAM = "FSU";
	private static final String CAPS_PROG_APS = "APS";
	private static final int MSG_DAY_CARE_REQ_PEND = 56136;
	private static final int MSG_COMPLETE_SEX_VIC_QN = 57064;

	private static final String CAPS_PROG_CPS = "CPS";
	// TDOD need to figure this
	private static final int ARC_MAX_YEAR = 4712;
	private static final int ARC_UTL_MAX_YEAR = 300;

	private static final String LGL_OTCM_PMC_REL = "140";
	private static final String LGL_OTCM_PMC_FCTV_KIN = "170";
	private static final String LGL_OTCM_NON_SUIT = "230";
	private static final String STAGE_PROG_FRE_FPR_REASON_CD = "090";
}
