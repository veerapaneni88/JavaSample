package us.tx.state.dfps.service.arreport.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.arinvconclusion.dto.ArEaEligibilityDto;
import us.tx.state.dfps.arinvconclusion.dto.ArInvCnclsnDto;
import us.tx.state.dfps.arinvconclusion.dto.PCSPDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.SDM.dao.SDMSafetyAssessmentDao;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtAreaValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtFactorValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.arreport.dao.ArReportDao;
import us.tx.state.dfps.service.arreport.dto.ArPrincipalsHistoryDto;
import us.tx.state.dfps.service.arreport.dto.ArRelationshipsDto;
import us.tx.state.dfps.service.arreport.dto.ArReportDto;
import us.tx.state.dfps.service.arreport.dto.ArServiceReferralsDto;
import us.tx.state.dfps.service.arreport.service.ArReportService;
import us.tx.state.dfps.service.casemanagement.dao.ArHelperDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSelectStageDao;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.WorkingNarrativeDao;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvComDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvContactSdmSafetyAssessDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvCrimHistDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvIntakePersonPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportMergedDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvSdmSafetyRiskDto;
import us.tx.state.dfps.service.cpsinvreport.dao.CpsInvReportDao;
import us.tx.state.dfps.service.cpsinvreport.service.CpsInvReportService;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ArReportPrefillData;
import us.tx.state.dfps.service.investigation.dao.CpsInvstDetailDao;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;
import us.tx.state.dfps.service.person.dto.SupervisorDto;
import us.tx.state.dfps.service.riskassesment.dto.SdmSafetyRiskAssessmentsDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Gathers
 * data to send to prefill to create prefill string Apr 4, 2018- 5:00:21 PM ©
 * 2017 Texas Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 06/10/2020 thompswa artf152702 : CPI June 2020 Project -null check emergency assist
 * 11/20/2020 nagarr artf169312, hp alm 16271, PD 61897 : R2 Contacts duplicated in INV AR report
 * 01/03/2022 thompswa artf211437 : AR Report Inconsistency - p2 report is NOT showing the merged intake.
 */
@Service
@Transactional
public class ArReportServiceImpl implements ArReportService {

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	ArReportDao dao;

	@Autowired
	WorkingNarrativeDao workingNarrDao;

	@Autowired
	CpsInvstDetailDao cpsDao;

	@Autowired
	CaseMaintenanceSelectStageDao caseDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	PcaDao pcaDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	CpsInvReportDao cpsInvReportDao;

	@Autowired
	ChildServicePlanFormDao childServicePlanFormDao;

	@Autowired
	ArReportPrefillData prefillData;

	@Autowired
	CpsInvReportService cpsInvReportService;

	@Autowired
	CaseMaintenanceSelectStageDao caseMaintenanceSelectStageDao;

	@Autowired
	SDMSafetyAssessmentDao sdmSafetyAssessmentDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	ArHelperDao arHelperDao;


	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ArReportDto getGenericCaseInfo(Long idStage){
		ArReportDto prefillDto = new ArReportDto();
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(idStage);
		if (!ObjectUtils.isEmpty(genericCaseInfoDto)
				&& ServiceConstants.CSTAGES_FPR.equalsIgnoreCase(genericCaseInfoDto.getCdStage())) {
			StageDto priorStageDto = dao.getPriorStage(idStage);
			if (!ObjectUtils.isEmpty(priorStageDto) && 0 < priorStageDto.getIdStage()) {
				genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(priorStageDto.getIdStage());
			}
		}
		if (!ObjectUtils.isEmpty(genericCaseInfoDto) && 0 < genericCaseInfoDto.getIdStage()) {
			idStage = genericCaseInfoDto.getIdStage();
		}
		prefillDto.setGenericCaseInfoDto(genericCaseInfoDto);
		prefillDto.setIdStage(idStage);
		// Get merged stage list
		List<Long> mergedStageList = workingNarrDao.spIdStage(genericCaseInfoDto.getIdCase(),
				genericCaseInfoDto.getIdStage(), genericCaseInfoDto.getCdStage());
		// Convert to string for query
		prefillDto.setMergedStages(mergedStagesAsString(mergedStageList));

		//
		String mergeString = null;
		Long idPriorStage = ServiceConstants.ZERO;
		List<CpsInvIntakePersonPrincipalDto> getIntakesList = new ArrayList<CpsInvIntakePersonPrincipalDto>();
		StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
		// CINT21D - Investigation details
		StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
		StageRtrvOutDto invStageRtrvOutDto = new StageRtrvOutDto();
		stageRtrvInDto.setUlIdStage(idStage);
		caseDao.selectStage(stageRtrvInDto, invStageRtrvOutDto);
		prefillDto.setInvStageRtrvOutDto(invStageRtrvOutDto);

		// Get prior INT stage ID
		idPriorStage = dao.getStageMergeInfo(idStage);

		// CINT21D - Intake details
		StageRtrvOutDto intStageRtrvOutDto = new StageRtrvOutDto();
		stageRtrvInDto.setUlIdStage(idPriorStage);
		caseDao.selectStage(stageRtrvInDto, intStageRtrvOutDto);
		prefillDto.setIntStageRtrvOutDto(intStageRtrvOutDto);
		// csecd2d no java version, gets the prior INT stage ID
		Long intakeStageId = ServiceConstants.ZERO;
		if (!ObjectUtils.isEmpty(cpsInvReportDao.getPriorIntStage(idStage))) {
			intakeStageId = cpsInvReportDao.getPriorIntStage(idStage);
		}

		if (!ObjectUtils.isEmpty(intakeStageId)) {
			// cint21d
			stageRtrvInDto.setUlIdStage(intakeStageId);
			caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
		}

		if (!ObjectUtils.isEmpty(genericCaseInfoDto.getIdStage())) {

			if (!ObjectUtils.isEmpty(cpsInvReportDao.getMergeHistory(genericCaseInfoDto.getIdCase()))) {
				List<Long> mergeStageList = workingNarrDao.spIdStage(genericCaseInfoDto.getIdCase(),
						genericCaseInfoDto.getIdStage(), genericCaseInfoDto.getCdStage());
				StringBuilder sb = new StringBuilder();
				if (!ObjectUtils.isEmpty(mergeStageList)) {
					for (Long stage : mergeStageList) {
						sb.append(stage);
						sb.append(",");
					}

					if (ServiceConstants.Zero < sb.length()) {
						mergeString = sb.toString().substring(0, sb.length() - 1);
					}
					/*
					 * closed-to-merged stage list including earliest merged
					 * stage start date
					 */
				}

				if (!ObjectUtils.isEmpty(mergeString)) {
					getIntakesList = cpsInvReportDao.getIntakes(mergeString);
					prefillDto.setGetIntakesList(getIntakesList); // artf211437
				}
			}
		}

		List<CpsInvReportMergedDto> cpsInvReportMergedDtoList = new ArrayList<CpsInvReportMergedDto>();
		Map<String, String> allStages = getAllStages(mergeString, genericCaseInfoDto, getIntakesList, intakeStageId,
				cpsInvReportMergedDtoList);
		prefillDto.setAllStages(allStages);
		prefillDto.setCpsInvReportMergedDtoList(cpsInvReportMergedDtoList);

		return prefillDto;
	}

	/**
	 * Method Name: getArReport Method Description: Gets information about AR
	 * report from database and returns prefill string
	 *
	 * @param req
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ArReportDto getArReport(CommonApplicationReq req, ArReportDto prefillDto) {
		// Declare global variables and prefill DTO
		Long idStage = req.getIdStage();
		Long idPriorStage = ServiceConstants.ZERO;
		List<CpsInvIntakePersonPrincipalDto> getIntakesList = new ArrayList<CpsInvIntakePersonPrincipalDto>();
		List<CpsInvComDto> getContactNamesList = new ArrayList<CpsInvComDto>();
		List<CpsInvSdmSafetyRiskDto> getSdmQaList = new ArrayList<CpsInvSdmSafetyRiskDto>();

		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = prefillDto.getGenericCaseInfoDto();

		if (!ObjectUtils.isEmpty(genericCaseInfoDto) && 0 < genericCaseInfoDto.getIdStage()) {
			idStage = genericCaseInfoDto.getIdStage();
		}
		prefillDto.setGenericCaseInfoDto(genericCaseInfoDto);
		prefillDto.setIdStage(idStage);


		Long invStageId = prefillDto.getInvStageRtrvOutDto().getIdStage();

		// Get mref status
		String mref = dao.getMrefStatus(invStageId);
		prefillDto.setMref(mref);

		// Get reporter info for intake section
		List<CpsInvIntakePersonPrincipalDto> reporterList = cpsInvReportDao.getPersonSplInfo(ServiceConstants.CONST_R,
				invStageId);
		if (!ObjectUtils.isEmpty(reporterList)) {
			prefillDto.setReporterDto(reporterList.get(0));
		}
		// CCMN19D - Worker information
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(invStageId,
				ServiceConstants.PRIMARY_WORKER);
		prefillDto.setStagePersonDto(stagePersonDto);

		// CCMN60D - Supervisor information
		SupervisorDto supervisorDto = pcaDao.getSupervisorPersonId(stagePersonDto.getIdTodoPersWorker());
		prefillDto.setSupervisorDto(supervisorDto);

		// Get office address
		EmployeePersPhNameDto employeePersPhNameDto = employeeDao
				.searchPersonPhoneName(stagePersonDto.getIdTodoPersWorker());
		prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);


		// Get intake allegations
		List<AllegationDetailDto> allegationDetailList = dao.getIntakeAllegations(idPriorStage);
		prefillDto.setAllegationDetailList(allegationDetailList);

		// Get principals list
		List<CpsInvPrincipalDto> principalsList = cpsInvReportDao.getPrincipals(idStage);
		prefillDto.setPrincipalsList(principalsList);

		// Get principals characteristics
		List<CharacteristicsDto> characteristicsList = dao.getPrnCharacteristicsByStage(idStage);
		prefillDto.setCharacteristicsList(characteristicsList);

		// Get collaterals list
		List<CpsInvIntakePersonPrincipalDto> reportcollateralList = cpsInvReportDao.getPersonSplInfo(ServiceConstants.CAPS,
				idStage);
		prefillDto.setCollateralList(reportcollateralList);
		List<CpsInvIntakePersonPrincipalDto> collateralList=new ArrayList<CpsInvIntakePersonPrincipalDto>();
		if(!ObjectUtils.isEmpty(reportcollateralList)&& !ObjectUtils.isEmpty(prefillDto.getReporterDto()))
		{
			for(CpsInvIntakePersonPrincipalDto cpsInvIntakePersonPrincipalDto:reportcollateralList)
			{
				if(!cpsInvIntakePersonPrincipalDto.getIdPerson().equals(prefillDto.getReporterDto().getIdPerson()))
				{
					collateralList.add(cpsInvIntakePersonPrincipalDto);
				}
			}
			prefillDto.setCollateralList(collateralList);
		}


		// Get historical allegations
		List<FacilityAllegationInfoDto> histAllegList = dao.getArAllegationsByStage(idStage);
		prefillDto.setHistAllegList(histAllegList);

		// Get historical removals
		List<CpsInvComDto> histRemovalsList = cpsInvReportDao.getRemovals(idStage);
		prefillDto.setHistRemovalsList(histRemovalsList);

		// Get criminal history
		List<CpsInvCrimHistDto> criminalHistList = cpsInvReportDao.getCriminalHistory(idStage);
		prefillDto.setCriminalHistList(criminalHistList);

		// Get safety assessments
		List<ARSafetyAssmtValueDto> safetyAssmtList = dao.getArSafetyAssmtsByStage(idStage);
		List<ARSafetyAssmtAreaValueDto> safetyAreaList = dao.getArSafetyAssmtAreasAll(idStage);
		List<ARSafetyAssmtFactorValueDto> safetyFactorList = dao.getArSafetyFactorsByStage(idStage);
		if (!ObjectUtils.isEmpty(safetyAssmtList)) {
			String assmtType = ServiceConstants.CD_INACTIVE;
			for (ARSafetyAssmtValueDto safetyAssmtDto : safetyAssmtList) {
				// add area list to assmt dto
				List<ARSafetyAssmtAreaValueDto> areaList = new ArrayList<ARSafetyAssmtAreaValueDto>();
				if (!ObjectUtils.isEmpty(safetyAreaList)) {
					for (ARSafetyAssmtAreaValueDto safetyAreaDto : safetyAreaList) {
						if (assmtType.equals(safetyAssmtDto.getIndAssmtType())
								&& assmtType.equals(safetyAreaDto.getIndAssmtType())) {
							// add factor list to safety area dto
							List<ARSafetyAssmtFactorValueDto> factorList = new ArrayList<ARSafetyAssmtFactorValueDto>();
							if (!ObjectUtils.isEmpty(safetyFactorList)) {
								for (ARSafetyAssmtFactorValueDto safetyFactorDto : safetyFactorList) {
									if (assmtType.equals(safetyAssmtDto.getIndAssmtType())
											&& assmtType.equals(safetyFactorDto.getIndAssmtType())
											&& !ObjectUtils.isEmpty(safetyAreaDto.getIdArea())
											&& safetyAreaDto.getIdArea().equals(safetyFactorDto.getIdArea())) {
										factorList.add(safetyFactorDto);
									}
								}
								safetyAreaDto.setaRSafetyAssmtFactors(factorList);
								areaList.add(safetyAreaDto);
							}
						}
					}
				}
				safetyAssmtDto.setaRSafetyAssmtAreas(areaList);
				// add factor list to assmt dto
				List<ARSafetyAssmtFactorValueDto> factorList = new ArrayList<ARSafetyAssmtFactorValueDto>();
				if (!ObjectUtils.isEmpty(safetyFactorList)) {
					for (ARSafetyAssmtFactorValueDto safetyFactorDto : safetyFactorList) {
						if (assmtType.equals(safetyAssmtDto.getIndAssmtType())
								&& assmtType.equals(safetyFactorDto.getIndAssmtType())) {
							factorList.add(safetyFactorDto);
						}
					}
				}
				safetyAssmtDto.setaRSafetyAssmtFactors(factorList);
				assmtType = ServiceConstants.CAPS;
			}
			int ctr = 0;
			for (ARSafetyAssmtValueDto safetyAssmtDto : safetyAssmtList) {
				if (0 == ctr++) {
					prefillDto.setInitialSafetyAssmt(safetyAssmtDto);
					prefillDto.setIntialEventDto(childServicePlanFormDao
							.fetchEventDetails(prefillDto.getInitialSafetyAssmt().getIdEvent().longValue()).get(0));
				} else {
					prefillDto.setClosureSafetyAssmt(safetyAssmtDto);
					prefillDto.setClosureEventDto(childServicePlanFormDao
							.fetchEventDetails(prefillDto.getClosureSafetyAssmt().getIdEvent().longValue()).get(0));
				}
			}
		}

		// Get services and referrals
		List<ArServiceReferralsDto> servRefList = dao.selectServiceReferrals(idStage, ServiceConstants.ZERO);
		prefillDto.setServRefList(servRefList);
		prefillDto.setDtFamPlanComp(dao.getPlanCompletionDate(idStage, ServiceConstants.INI_FAM_PLAN));
		prefillDto.setDtSafPlanComp(dao.getPlanCompletionDate(idStage, ServiceConstants.INI_SAF_PLAN));

		// Get PCSP
		List<PCSPDto> pcspList = dao.getSafetyPlacements(idStage);
		prefillDto.setPcspList(pcspList);

		// Get A-R information
		ArInvCnclsnDto arInvConcDto = dao.selectARConclusion(idStage);

		// Get AR EA Eligibility Information

		//List<ArEaEligibilityDto> arEaEligibilityDtoList=cpsInvReportDao.getEmergencyAssistance(idStage, ServiceConstants.A_R_STAGE);
		//artf163982 modified the code to pass case id to retrieve new risk assesment for the new ar stage created. Replicated the same logic as UI for the form
		//[artf163982] ALM#15522 Some A-R cases still not displaying questions and correct determination in Alternative Response Report
		List<ArEaEligibilityDto> arEaEligibilityDtoList  = arHelperDao.getArEaEligibilityDetails(arInvConcDto);

		if (!ObjectUtils.isEmpty(arEaEligibilityDtoList)) {
			for (ArEaEligibilityDto arEaEligibilityDto : arEaEligibilityDtoList) {
				if ( null == arEaEligibilityDto.getIndEaResponse()){
					arEaEligibilityDto.setIndEaResponse(ServiceConstants.EMPTY_STRING);
				}
				if ( null == arEaEligibilityDto.getCdEaQuestion()){
					arEaEligibilityDto.setCdEaQuestion(ServiceConstants.EMPTY_STRING);
				}
			} // artf152702 TODO: fix code or data to prevent null question/response
		}
		arInvConcDto.setArEaEligibilityDtoList(arEaEligibilityDtoList);
		prefillDto.setArInvConcDto(arInvConcDto);

		// Get A-R contacts
		String mergedStages = StringUtils.isNotBlank(prefillDto.getMergedStages()) ? prefillDto.getMergedStages() : idStage.toString();
		List<CpsInvComDto> contactNamesList = cpsInvReportDao.getContactNames(mergedStages);
		prefillDto.setContactNamesList(contactNamesList);

		// Get Relationship info
		Date dtStageClosureApproval = null;
		if (!ObjectUtils.isEmpty(arInvConcDto) && ServiceConstants.ZERO < arInvConcDto.getIdEvent()) {
			dtStageClosureApproval = dao.getClosureApproval(arInvConcDto.getIdEvent());
		}
		if (ObjectUtils.isEmpty(dtStageClosureApproval)) {
			dtStageClosureApproval = ServiceConstants.MAX_DATE;
		}
		List<ArRelationshipsDto> relationshipList = dao.getRelationships(idStage, dtStageClosureApproval);
		prefillDto.setRelationshipList(relationshipList);

		//To retrieve the Safety Assessments form CPS SA and CPS RA
		List<SdmSafetyRiskAssessmentsDto> sdmSafetyRiskAssessmentsDtoList = dao.getSdmSafetyRiskAssessments(idStage);
		prefillDto.setSdmSafetyRiskAssessments(sdmSafetyRiskAssessmentsDtoList);


		// To retrieve the SDM CareGiver and Children Assessed
		List<String> careGiverChildrenAssessedList=new ArrayList();
		List<Long> careGiverAssessedPersonIds=sdmSafetyAssessmentDao.getChildrenCareGiverAssessedInSDMSafety(idStage);
		if(!ObjectUtils.isEmpty(careGiverAssessedPersonIds))
		{
			for(Long personid: careGiverAssessedPersonIds)
			{
				PersonDto person=personDao.getPersonById(personid);
				String caregivername=person.getNmPersonFirst()+ServiceConstants.SPACE+person.getNmPersonLast();
				careGiverChildrenAssessedList.add(caregivername);
			}
		}
		String mergeString = prefillDto.getAllStages().get(ServiceConstants.ALL);
		if (ServiceConstants.STR_ZERO_VAL.equals(mergeString)) {
			mergeString = idStage.toString();
		}
		getContactNamesList=cpsInvReportDao.getContactNames(mergeString);
		prefillDto.setContactNamesList1(getContactNamesList);
		prefillDto.setCareGiverChildrenAssessedList(careGiverChildrenAssessedList);

		Map<String, String> allStages = prefillDto.getAllStages();


		if (StringUtils.isNotBlank(allStages.get(ServiceConstants.CSTAGES_INV))
				&& StringUtils.isNotBlank(allStages.get(ServiceConstants.CSTAGES_AR))) {
			getSdmQaList = cpsInvReportDao.getSdmQa(allStages.get(ServiceConstants.CSTAGES_INV) + ServiceConstants.COMMA
					+ allStages.get(ServiceConstants.CSTAGES_AR));

		} else if (StringUtils.isNotBlank(allStages.get(ServiceConstants.CSTAGES_INV))) {
			getSdmQaList = cpsInvReportDao.getSdmQa(allStages.get(ServiceConstants.CSTAGES_INV));
		} else if (StringUtils.isNotBlank(allStages.get(ServiceConstants.CSTAGES_AR))) {
			getSdmQaList = cpsInvReportDao.getSdmQa(allStages.get(ServiceConstants.CSTAGES_AR));
		}


		prefillDto.setGetSdmQaList(getSdmQaList);


		return prefillDto;
	}

	/* (non-Javadoc)
	 * @see us.tx.state.dfps.service.arreport.service.ArReportService#returnPrefillData(us.tx.state.dfps.service.arreport.dto.ArReportDto)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto returnPrefillData(ArReportDto arReportDto){
		return prefillData.returnPrefillData(arReportDto);
	}

	/* (non-Javadoc)
	 * @see us.tx.state.dfps.service.arreport.service.ArReportService#getSDMCareGiverList(us.tx.state.dfps.service.arreport.dto.ArReportDto)
	 */
	@Override
	public ArReportDto getSDMCareGiverList(ArReportDto arReportDto){
		List<CpsInvContactSdmSafetyAssessDto> contactSp1List = arReportDto.getGetContactsp1List();
		List<CpsInvContactSdmSafetyAssessDto> finalContactsp1List = new ArrayList<CpsInvContactSdmSafetyAssessDto>();
		if (!ObjectUtils.isEmpty(contactSp1List)) {
			Long previousEventId = 0L;
			for (CpsInvContactSdmSafetyAssessDto cpsInvContactSdmSafetyAssessDto : contactSp1List) {
				if (!previousEventId.equals(cpsInvContactSdmSafetyAssessDto.getIdEvent())) {
					cpsInvContactSdmSafetyAssessDto.setContactNamesList(arReportDto.getCareGiverChildrenAssessedList());
					finalContactsp1List.add(cpsInvContactSdmSafetyAssessDto);
				}
				previousEventId = cpsInvContactSdmSafetyAssessDto.getIdEvent();
			}
		}
		arReportDto.setGetContactsp1List(finalContactsp1List);
		return arReportDto;
	}

	/* (non-Javadoc)
	 * @see us.tx.state.dfps.service.arreport.service.ArReportService#getHistoricalPrincipals(java.lang.Long)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<ArPrincipalsHistoryDto> getHistoricalPrincipals(Long idStage){
		// Get historical principals
		return dao.getHistoricalPrincipals(idStage);
	}

	/**
	 * Method Name: mergedStagesAsString Method Description: Convert list of
	 * merged stages into comma delimited string
	 *
	 * @param mergedStageList
	 * @return String
	 */
	private String mergedStagesAsString(List<Long> mergedStageList) {
		StringBuilder sb = new StringBuilder();
		for (Long stage : mergedStageList) {
			sb.append(stage);
			sb.append(ServiceConstants.COMMA);
		}
		return (ServiceConstants.Zero < sb.length()) ? sb.toString().substring(0, sb.toString().length() - 1)
				: ServiceConstants.EMPTY_STR;
	}

	/* (non-Javadoc)
	 * @see us.tx.state.dfps.service.arreport.service.ArReportService#getContactList(java.lang.Long, java.lang.String)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<ContactDto> getContactList(Long idStage, String mergedStages) {
		return dao.getContactList(idStage, mergedStages);
	}

	/* (non-Javadoc)
	 * @see us.tx.state.dfps.service.arreport.service.ArReportService#getContacts(java.util.List)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<CpsInvContactSdmSafetyAssessDto> getContacts(List<CpsInvReportMergedDto> cpsInvReportMergedDtoList) {
		return cpsInvReportDao.getContacts(cpsInvReportMergedDtoList);
	}


	/**
	 * Method Name: getAllStages Method Description: Gathers all id stages
	 * associated with case and organizes by stage type
	 *
	 * @param mergedStages
	 * @param genericCaseInfoDto
	 * @param getIntakesList
	 * @param intakeStageId
	 * @return Map<String,String>
	 */
	private Map<String, String> getAllStages(String mergedStages, GenericCaseInfoDto genericCaseInfoDto,
											 List<CpsInvIntakePersonPrincipalDto> getIntakesList, Long intakeStageId,List<CpsInvReportMergedDto> cpsInvReportMergedDtoList) {
		// get inv stages
		String invMergeString = ServiceConstants.EMPTY_STR;
		if (StringUtils.isBlank(mergedStages) || ServiceConstants.STR_ZERO_VAL.equals(mergedStages)) {
			invMergeString = String.valueOf(genericCaseInfoDto.getIdStage());
		} else {
			invMergeString = mergedStages + ServiceConstants.COMMA + genericCaseInfoDto.getIdStage();
		}

		// get ar stages
		List<Long> arStageList = cpsInvReportDao.getPriorArStages(invMergeString);
		String arMergeString = ServiceConstants.EMPTY_STRING;
		int i = 0;
		if (!ObjectUtils.isEmpty(arStageList)) {
			for (Long stage : arStageList) {
				if (0 == i++) {
					arMergeString = String.valueOf(stage);
				} else {
					arMergeString += ServiceConstants.COMMA + stage;
				}
				List<Long> mergedStageList = workingNarrDao.spIdStage(genericCaseInfoDto.getIdCase(), stage,
						ServiceConstants.CSTAGES_AR);
				for (Long priorStage : mergedStageList) {
					arMergeString += ServiceConstants.COMMA + priorStage;
				}
			}
		}
		// get int stages
		String intMergeString = ServiceConstants.EMPTY_STRING;
		intMergeString = String.valueOf(intakeStageId);
		if (!ObjectUtils.isEmpty(arMergeString)) {
			List<CpsInvIntakePersonPrincipalDto> arIntakes = cpsInvReportDao.getIntakes(arMergeString);

			if (!ObjectUtils.isEmpty(arIntakes)) {
				for (CpsInvIntakePersonPrincipalDto intake : arIntakes) {
					intMergeString += ServiceConstants.COMMA + intake.getIdStage();
				}
			}
		}

		for (CpsInvIntakePersonPrincipalDto intake : getIntakesList) {
			intMergeString += ServiceConstants.COMMA + intake.getIdStage();
		}
		String allStages = intMergeString;
		allStages += StringUtils.isNotBlank(arMergeString) ? (ServiceConstants.COMMA + arMergeString)
				: ServiceConstants.EMPTY_STR;
		allStages += StringUtils.isNotBlank(invMergeString) ? (ServiceConstants.COMMA + invMergeString)
				: ServiceConstants.EMPTY_STR;
		Map<String, String> stages = new HashMap<>();
		CpsInvReportMergedDto intReportMergedDto = new CpsInvReportMergedDto();
		CpsInvReportMergedDto invReportMergedDto = new CpsInvReportMergedDto();
		CpsInvReportMergedDto arReportMergedDto = new CpsInvReportMergedDto();
		CpsInvReportMergedDto allReportMergedDto = new CpsInvReportMergedDto();

		if(!ObjectUtils.isEmpty(intMergeString))
		{
			stages.put(ServiceConstants.CSTAGES_INT, intMergeString);
			intReportMergedDto.setCdStage(ServiceConstants.CSTAGES_INT);
			intReportMergedDto.setStrMergedStages(intMergeString);
			cpsInvReportMergedDtoList.add(intReportMergedDto);
		}
		if(!ObjectUtils.isEmpty(invMergeString))
		{
			stages.put(ServiceConstants.CSTAGES_INV, invMergeString);
			invReportMergedDto.setCdStage(ServiceConstants.CSTAGES_INV);
			invReportMergedDto.setStrMergedStages(invMergeString);
			cpsInvReportMergedDtoList.add(invReportMergedDto);
		}
		if(!ObjectUtils.isEmpty(arMergeString))
		{
			stages.put(ServiceConstants.CSTAGES_AR, arMergeString);
			arReportMergedDto.setCdStage(ServiceConstants.CSTAGES_AR);
			arReportMergedDto.setStrMergedStages(arMergeString);
			cpsInvReportMergedDtoList.add(arReportMergedDto);
		}
		if(!ObjectUtils.isEmpty(allStages))
		{
			stages.put(ServiceConstants.ALL, allStages);
		}

		return stages;
	}
}
