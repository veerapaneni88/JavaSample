package us.tx.state.dfps.service.cpsinvreport.serviceimpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.arinvconclusion.dto.ArEaEligibilityDto;
import us.tx.state.dfps.arinvconclusion.dto.PCSPDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.arreport.dao.ArReportDao;
import us.tx.state.dfps.service.arreport.dto.ArRelationshipsDto;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSelectStageDao;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.casepackage.dto.PcspExtnDtlDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.WorkingNarrativeDao;
import us.tx.state.dfps.service.common.request.FacilityInvSumReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvAllegDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvComDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvContactSdmSafetyAssessDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvCrimHistDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvIntakePersonPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportMergedDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvRiskDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvSdmSafetyRiskDto;
import us.tx.state.dfps.service.cpsinv.dto.ServRefDto;
import us.tx.state.dfps.service.cpsinvreport.dao.CpsInvReportDao;
import us.tx.state.dfps.service.cpsinvreport.service.CpsInvReportService;
import us.tx.state.dfps.service.cpsinvstsummary.dao.CpsInvstSummaryDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CpsInvReportPrefillData;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;
import us.tx.state.dfps.service.person.dto.SupervisorDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * class for CPSInvReport form Name: civ36o00 Apr 4, 2018- 4:50:23 PM © 2017
 * Texas Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 07/13/2020 thompswa artf159096 : add isEligible for emergency assist
 * 11/20/2020 nagarr artf169312, hp alm 16271, PD 61897 : R2 Contacts duplicated in INV AR report
 */
@Service
@Transactional
public class CpsInvReportServiceImpl implements CpsInvReportService {

	@Autowired
	CpsInvReportDao cpsInvReportDao;

	@Autowired
	ArReportDao aRReportDao;

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	WorkingNarrativeDao workingNarrDao;

	@Autowired
	CaseMaintenanceSelectStageDao caseMaintenanceSelectStageDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	PcspListPlacmtDao pcspListPlacmtDao;

	@Autowired
	private CpsInvstSummaryDao cpsInvstSummaryDao;

	@Autowired
	CpsInvReportPrefillData cpsInvReportPrefillData;

	public CpsInvReportServiceImpl() {
		super();
	}

	private static final Logger logger = Logger.getLogger(CpsInvReportServiceImpl.class);
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getCPSInvReport(FacilityInvSumReq facilityInvSumReq){

		GenericCaseInfoDto genericCaseInfoDto = new GenericCaseInfoDto();
		StagePersonDto stagePersonDto = new StagePersonDto();
		SupervisorDto supervisorDto = new SupervisorDto();
		EmployeePersPhNameDto employeePersPhNameDto = new EmployeePersPhNameDto();
		StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();// intake info
		List<CpsInvIntakePersonPrincipalDto> getPersonSplInfoListPrin = new ArrayList<CpsInvIntakePersonPrincipalDto>();
		List<CpsInvIntakePersonPrincipalDto> getPersonSplInfoListCol = new ArrayList<CpsInvIntakePersonPrincipalDto>();
		List<CpsInvIntakePersonPrincipalDto> getIntakesList = new ArrayList<CpsInvIntakePersonPrincipalDto>();
		List<CpsInvAllegDto> getAllegationsList = new ArrayList<CpsInvAllegDto>();
		List<CpsInvPrincipalDto> getPrincipalsList = new ArrayList<CpsInvPrincipalDto>();
		List<CpsInvComDto> getRemovalsList = new ArrayList<CpsInvComDto>();
		List<CpsInvCrimHistDto> getCriminalHistoryList = new ArrayList<CpsInvCrimHistDto>();
		List<CpsInvSdmSafetyRiskDto> getRiskAreaList = new ArrayList<CpsInvSdmSafetyRiskDto>();
		List<CpsInvSdmSafetyRiskDto> getRiskFactorsList = new ArrayList<CpsInvSdmSafetyRiskDto>();
		List<PCSPDto> getSafetyPlacementsList = new ArrayList<PCSPDto>();
		List<CpsInvContactSdmSafetyAssessDto> getContactsp1List = new ArrayList<CpsInvContactSdmSafetyAssessDto>();
		List<CpsInvComDto> getContactNamesList = new ArrayList<CpsInvComDto>();
		List<CpsInvSdmSafetyRiskDto> getSdmQaList = new ArrayList<CpsInvSdmSafetyRiskDto>();
		List<ArRelationshipsDto> getRelationshipsList = new ArrayList<ArRelationshipsDto>();
		List<CpsInvAllegDto> getPrnInvAllegationsList = new ArrayList<CpsInvAllegDto>();
		List<CpsInvIntakePersonPrincipalDto> getPrincipalsHistoryList = new ArrayList<CpsInvIntakePersonPrincipalDto>();
		List<CpsInvSdmSafetyRiskDto> getSafetyFactorsList = new ArrayList<CpsInvSdmSafetyRiskDto>();
		List<CharacteristicsDto> getCharacteristicsDtoList = new ArrayList<CharacteristicsDto>();
		Date sCApprovalDate = new Date();
		String sdmRiskFinal = ServiceConstants.EMPTY_STR;
		String mRef = ServiceConstants.EMPTY_STR;
		Long intakeStageId = ServiceConstants.ZERO;
		Boolean isLegacySafetyInterval = Boolean.FALSE;
		Boolean isLegacyRisk = Boolean.FALSE;

		CpsInvReportDto cpsInvReportDto = new CpsInvReportDto();
		Long stageId = facilityInvSumReq.getIdStage();
		StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
		String mergeString = null;
		Boolean isAfterLegacySafetyOnset = Boolean.FALSE;
		Boolean isBeforeSdmSafetyOnset = Boolean.FALSE;
		String methSuspId = null;

		// csecd2d no java version, gets the prior INT stage ID
		if (!ObjectUtils.isEmpty(cpsInvReportDao.getPriorIntStage(stageId))) {
			intakeStageId = cpsInvReportDao.getPriorIntStage(stageId);
		}

		if (!ObjectUtils.isEmpty(intakeStageId)) {
			// cint21d
			stageRtrvInDto.setUlIdStage(intakeStageId);
			caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
		}

		/** INVESTIGATION INFORMATION SECTION **/

		// cinv95d
		List<CpsInvstDetailDto> cpsInvstDetailList = cpsInvstSummaryDao.getCpsInvstDetail(stageId);

		methSuspId = pcspListPlacmtDao.getMethIndicator(intakeStageId);

		/** SAFETY ASSESSMENT **/

		// getSdmSafetyAssessments
		List<CpsInvContactSdmSafetyAssessDto> getSdmSafetyAssessmentsList = cpsInvReportDao
				.getSdmSafetyRiskAssessments(stageId);

		// getSafetyAssessment
		List<CpsInvSdmSafetyRiskDto> getSafetyAssessmentList = cpsInvReportDao.getSafetyAssessment(stageId);

		// getRiskAssessment
		List<CpsInvRiskDto> getRiskAssessmentList = cpsInvReportDao.getRiskAssessment(stageId);

		/**
		 * populate Intial Summary section.
		 */

		if (!ObjectUtils.isEmpty(cpsInvstDetailList)) {

			// csec02d, case information
			genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(cpsInvstDetailList.get(0).getIdStage());
			genericCaseInfoDto.setIndCaseSensitive(indTranslate(genericCaseInfoDto.getIndCaseSensitive()));

			/**
			 * getting boolean legacy safety interval for caseinfo
			 */
			try {
				if (!ObjectUtils.isEmpty(genericCaseInfoDto.getDtStageStart())) {

					String mar2015Dt = lookupDao.simpleDecode(ServiceConstants.CRELDATE,
							ServiceConstants.CRELDATE_MAR_2015_IMPACT1);
					Date date1 = DateUtils.toJavaDateFromInput(mar2015Dt);

					isBeforeSdmSafetyOnset = DateUtils.isBefore(genericCaseInfoDto.getDtStageStart(), date1);

					String aug2015Dt = lookupDao.simpleDecode(ServiceConstants.CRELDATE,
							ServiceConstants.CRELDATE_AUG_2015_IMPACT);
					date1 = DateUtils.toJavaDateFromInput(aug2015Dt);
					isLegacyRisk = DateUtils.isBefore(genericCaseInfoDto.getDtStageStart(), date1);
				}

				if (!ObjectUtils.isEmpty(genericCaseInfoDto.getDtStageClose())) {

					String may2007Dt = lookupDao.simpleDecode(ServiceConstants.CRELDATE,
							ServiceConstants.CRELDATE_MAY_2007_CPS_REFORM);

					Date date1 = DateUtils.toJavaDateFromInput(may2007Dt);
					isAfterLegacySafetyOnset = DateUtils.isAfter(genericCaseInfoDto.getDtStageClose(), date1);
				}
			} catch (ParseException e) {
				new ServiceLayerException(e.getMessage());
			}
			isLegacySafetyInterval = isBeforeSdmSafetyOnset && isAfterLegacySafetyOnset;

			// get the worker information
			// Call CCMN19D
			stagePersonDto = stageDao.getStagePersonLinkDetails(stageId, ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);

			if (!ObjectUtils.isEmpty(stagePersonDto)) {
				// supervisor information Call CCMN60D
				supervisorDto = personDao.getSupervisorByPersonId(stagePersonDto.getIdTodoPersWorker());
				/* retrieves worker info CallCSEC01D */

				employeePersPhNameDto = employeeDao.searchPersonPhoneName(stagePersonDto.getIdTodoPersWorker());
			}

			sdmRiskFinal = cpsInvReportDao.getValidSdmRa(stageId);
			mRef = cpsInvReportDao.getMref(stageId);
		}

		/**
		 * populate Intake Narrative section. Return the reporter for the
		 * Reporter section
		 */

		if (!ObjectUtils.isEmpty(stageRtrvOutDto.getIdStage())) {
			// cint66d - gets the reporter info for intake section from both
			// person and stage_person_link
			getPersonSplInfoListPrin = cpsInvReportDao.getPersonSplInfo(ServiceConstants.REPORTER,
					stageRtrvOutDto.getIdStage());

			if (!ObjectUtils.isEmpty(getPersonSplInfoListPrin)) {
				if (ObjectUtils.isEmpty(getPersonSplInfoListPrin.get(0).getIdPerson())
						|| ServiceConstants.ZERO >= getPersonSplInfoListPrin.get(0).getIdPerson()) {
					getPersonSplInfoListPrin.get(0).setIdPerson(ServiceConstants.ZERO);
				}
			}
		}

		/**
		 * Need to get all of the merged intakes using the merged stage string
		 * from clscdbd.
		 */
		if (!ObjectUtils.isEmpty(genericCaseInfoDto.getIdStage())) {

			if (!ObjectUtils.isEmpty(cpsInvReportDao.getMergeHistory(genericCaseInfoDto.getIdCase()))) {
				List<Long> mergedStageList = workingNarrDao.spIdStage(genericCaseInfoDto.getIdCase(),
						genericCaseInfoDto.getIdStage(), genericCaseInfoDto.getCdStage());
				StringBuilder sb = new StringBuilder();
				if (!ObjectUtils.isEmpty(mergedStageList)) {
					for (Long stage : mergedStageList) {
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
				}
			}
		}

		/**
		 * populate Allegation Detail section.
		 */

		getAllegationsList = cpsInvReportDao.getAllegations(stageId);

		/**
		 * populate Person List, Principals section.
		 */
		getPrincipalsList = cpsInvReportDao.getPrincipals(stageId);

		// Removal dates

		getRemovalsList = cpsInvReportDao.getRemovals(stageId);

		// getPrnCharacteristicsByStage

		getCharacteristicsDtoList = aRReportDao.getPrnCharacteristicsByStage(stageId);

		/**
		 * populate Person List, Collaterals section.
		 */

		// Person List - Collaterals
		getPersonSplInfoListCol = cpsInvReportDao.getPersonSplInfo(ServiceConstants.COLLATERALC, stageId);
		cpsInvReportDto.setGetPersonSplInfoListCol(getPersonSplInfoListCol);

		// Warranty Defect - 12267 - To remove the Duplicate Names getting Populated
		List<CpsInvIntakePersonPrincipalDto> collaterallist=getPersonSplInfoListCol;
		if(!ObjectUtils.isEmpty(getPersonSplInfoListCol)&& !ObjectUtils.isEmpty(getPersonSplInfoListPrin))
		{
			for(CpsInvIntakePersonPrincipalDto collateralinfo:getPersonSplInfoListPrin)
			{
				collaterallist = collaterallist.stream().filter(dto -> !dto.getIdPerson().equals(collateralinfo.getIdPerson())).collect(Collectors.toList());

			}
			cpsInvReportDto.setGetPersonSplInfoListCol(collaterallist);
		}



		/** CRIMINAL HISTORY FOR ALL PRINCIPALS **/

		getCriminalHistoryList = cpsInvReportDao.getCriminalHistory(stageId);

		/** SAFETY ASSESSMENT **/

		getSafetyFactorsList = cpsInvReportDao.getSafetyFactors(stageId);

		/** RISK ASSESSMENT **/

		if (!ObjectUtils.isEmpty(genericCaseInfoDto.getIdStage())) {
			// area concern
			getRiskAreaList = cpsInvReportDao.getRiskArea(genericCaseInfoDto.getIdStage());
			getRiskFactorsList = cpsInvReportDao.getRiskFactors(genericCaseInfoDto.getIdStage());
		}

		/** SERVICES AND REFERRALS **/

		ServRefDto servRefDto = cpsInvReportDao.getServicesReferrals(stageId,
				cpsInvReportDao.getEventIdByStageAndEventType(stageId, ServiceConstants.CEVNTTYP_CHK),
				ServiceConstants.CSTAGES_INV);

		/** PARENTAL CHILD SAFETY PLCMTs **/

		getSafetyPlacementsList = aRReportDao.getSafetyPlacements(genericCaseInfoDto.getIdStage());
		List<PcspDto> pcspList = pcspListPlacmtDao.getPcspPlacemnts(genericCaseInfoDto.getIdCase());
		if (!ObjectUtils.isEmpty(getSafetyPlacementsList)) {
			for (PCSPDto pcpsDto : getSafetyPlacementsList) {
				pcpsDto.setIndCargvrManual(indTranslate(pcpsDto.getIndCargvrManual()));
			}
		}
		if (!ObjectUtils.isEmpty(pcspList)) {
			List<String> pcspEndReasonList = new ArrayList<>();
			for (PcspDto pcspDto : pcspList) {
				pcspEndReasonList.add(ServiceConstants.CCOR_070.equals(pcspDto.getCdEndRsn())
						? pcspListPlacmtDao.getPcspPlacemetInfo(pcspDto.getIdPlacement()).getEndRsnOther()
						: ServiceConstants.EMPTY_STR);
				List<PcspExtnDtlDto> pcspExtlist = pcspListPlacmtDao.getPcspPlacementExtDtl(pcspDto.getIdPlacement());
				pcspExtlist.stream().filter(pcspExtDto -> pcspExtDto!= null).forEach(pcspExtDto -> {
					if( pcspExtDto.getIndAtrnyParentAgrmnt()!=null) {
						String atrnyAgrmnt = pcspExtDto.getIndAtrnyParentAgrmnt();
						if (atrnyAgrmnt.equalsIgnoreCase("Y")) {
							pcspExtDto.setIndAtrnyParentAgrmnt("Yes");
						} else if (atrnyAgrmnt.equalsIgnoreCase("N")) {
							pcspExtDto.setIndAtrnyParentAgrmnt("No");
						}
					} if (pcspExtDto.getIndToContPcsp() !=null) {
						String indCourt = pcspExtDto.getIndToContPcsp();
						if (indCourt.equalsIgnoreCase("Y")) {
							pcspExtDto.setIndToContPcsp("Yes");
						} else if (indCourt.equalsIgnoreCase("N")) {
							pcspExtDto.setIndToContPcsp("No");
						}
					}
				});
				pcspDto.setPcspExtnDtlDtoList(pcspExtlist);
			}
			cpsInvReportDto.setPcspEndReasonList(pcspEndReasonList);
		}

		/** EMERGENCY ASSISTANCE SECTION **/

		List<ArEaEligibilityDto> eaEligibilityList = cpsInvReportDao
				.getEmergencyAssistance(cpsInvstDetailList.get(0).getIdStage(), ServiceConstants.CSTAGES_INV);
		if (!ObjectUtils.isEmpty(eaEligibilityList)) {
			for (ArEaEligibilityDto arEaEligibilityDto : eaEligibilityList) {
				if ( null == arEaEligibilityDto.getIndEaResponse()){
					arEaEligibilityDto.setIndEaResponse(ServiceConstants.SPACE);
				}
			}
		}

		/** INVESTIGATION CONTACTS - INCLUDING MERGE TYPES AND SDMs **/

		List<CpsInvReportMergedDto> cpsInvReportMergedDtoList = new ArrayList<CpsInvReportMergedDto>();
		Map<String, String> allStages = getAllStages(mergeString, genericCaseInfoDto, getIntakesList, intakeStageId,cpsInvReportMergedDtoList);
		mergeString = allStages.get(ServiceConstants.ALL);

		getContactsp1List = cpsInvReportDao.getContacts(cpsInvReportMergedDtoList);
		List<CpsInvContactSdmSafetyAssessDto> finalContactsp1List = new ArrayList<CpsInvContactSdmSafetyAssessDto>();

		if (!ObjectUtils.isEmpty(getContactsp1List)) {
			Long previousEventId = 0L;
			for (CpsInvContactSdmSafetyAssessDto cpsInvContactSdmSafetyAssessDto : getContactsp1List) {
				if (!previousEventId.equals(cpsInvContactSdmSafetyAssessDto.getIdEvent())) {
					finalContactsp1List.add(cpsInvContactSdmSafetyAssessDto);
				}
				previousEventId = cpsInvContactSdmSafetyAssessDto.getIdEvent();
			}
		}

		if (ServiceConstants.STR_ZERO_VAL.equals(mergeString)) {
			mergeString = stageId.toString();
		}
		getContactNamesList = cpsInvReportDao.getContactNames(mergeString);

		if (StringUtils.isNotBlank(allStages.get(ServiceConstants.CSTAGES_INV))
				&& StringUtils.isNotBlank(allStages.get(ServiceConstants.CSTAGES_AR))) {
			getSdmQaList = cpsInvReportDao.getSdmQa(allStages.get(ServiceConstants.CSTAGES_INV) + ServiceConstants.COMMA
					+ allStages.get(ServiceConstants.CSTAGES_AR));

		} else if (StringUtils.isNotBlank(allStages.get(ServiceConstants.CSTAGES_INV))) {
			getSdmQaList = cpsInvReportDao.getSdmQa(allStages.get(ServiceConstants.CSTAGES_INV));
		} else if (StringUtils.isNotBlank(allStages.get(ServiceConstants.CSTAGES_AR))) {
			getSdmQaList = cpsInvReportDao.getSdmQa(allStages.get(ServiceConstants.CSTAGES_AR));
		}

		/**
		 * populate Relationship Information section.
		 */
		// csec23d - grab stage closure date from the approval. INV code notes
		// this is because approval will have a timestamp component.
		// This is needed for the relationship information lookup

		if (!ObjectUtils.isEmpty(cpsInvstDetailList)) {
			if (cpsInvstDetailList.get(0).getIdEvent() > 0)
				sCApprovalDate = aRReportDao.getClosureApproval(cpsInvstDetailList.get(0).getIdEvent());
		}
		if (ObjectUtils.isEmpty(sCApprovalDate)) {
			sCApprovalDate = ServiceConstants.MAX_DATE;
		}

		// Relationship Information.
		getRelationshipsList = aRReportDao.getRelationships(stageId, sCApprovalDate);

		cpsInvReportDto.setGenericCaseInfoDto(genericCaseInfoDto);
		cpsInvReportDto.setStagePersonDto(stagePersonDto);
		cpsInvReportDto.setSupervisorDto(supervisorDto);
		cpsInvReportDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		cpsInvReportDto.setStageRtrvOutDto(stageRtrvOutDto);
		cpsInvReportDto.setGetPersonSplInfoListPrin(getPersonSplInfoListPrin);
		cpsInvReportDto.setGetIntakesList(getIntakesList);
		cpsInvReportDto.setGetAllegationsList(getAllegationsList);
		cpsInvReportDto.setGetPrincipalsList(getPrincipalsList);
		cpsInvReportDto.setGetRemovalsList(getRemovalsList);
		cpsInvReportDto.setGetCriminalHistoryList(getCriminalHistoryList);
		cpsInvReportDto.setGetRiskAreaList(getRiskAreaList);
		cpsInvReportDto.setGetRiskFactorsList(getRiskFactorsList);
		cpsInvReportDto.setGetSafetyPlacementsList(getSafetyPlacementsList);
		cpsInvReportDto.setGetContactsp1List(finalContactsp1List);
		cpsInvReportDto.setGetContactNamesList(getContactNamesList);
		cpsInvReportDto.setGetSdmQaList(getSdmQaList);
		cpsInvReportDto.setGetRelationshipsList(getRelationshipsList);
		cpsInvReportDto.setGetPrnInvAllegationsList(getPrnInvAllegationsList);
		cpsInvReportDto.setGetPrincipalsHistoryList(getPrincipalsHistoryList);
		cpsInvReportDto.setsCApprovalDate(sCApprovalDate);
		cpsInvReportDto.setGetValidSdmRaString(sdmRiskFinal);
		cpsInvReportDto.setGetMrefString(mRef);
		cpsInvReportDto.setGetRiskAssessmentList(getRiskAssessmentList);
		cpsInvReportDto.setGetSafetyAssessmentList(getSafetyAssessmentList);
		cpsInvReportDto.setGetSdmSafetyAssessmentsList(getSdmSafetyAssessmentsList);
		cpsInvReportDto.setIsEligible(isEligible(getSdmSafetyAssessmentsList)); // artf152701
		cpsInvReportDto.setCpsInvstDetailList(cpsInvstDetailList);
		cpsInvReportDto.setGetSafetyFactorsList(getSafetyFactorsList);
		cpsInvReportDto.setIntakeStageId(intakeStageId);
		cpsInvReportDto.setIsLegacySafetyInterval(isLegacySafetyInterval);
		cpsInvReportDto.setMethSuspId(methSuspId);
		cpsInvReportDto.setGetCharacteristicsDtoList(getCharacteristicsDtoList);
		cpsInvReportDto.setIsLegacyRisk(isLegacyRisk);
		cpsInvReportDto.setServRefDto(servRefDto);
		cpsInvReportDto.setPcspList(pcspList);
		cpsInvReportDto.setEaEligibilityList(eaEligibilityList);
		cpsInvReportDto.setStageId(stageId);

    // Get Citizenship Status for stage id and Oldest Victim.
    String citizenStatus =
        personDao.getOldestVictimCitizenshipByStageIdAndRelType(
            stageId, ServiceConstants.CRPTRINT_OV);
		if(!StringUtils.isEmpty(citizenStatus)) {
			cpsInvReportDto.setCitizenshipStatus(citizenStatus);
		}
		return cpsInvReportPrefillData.returnPrefillData(cpsInvReportDto);
	}


	/**
	 * Translates indicator character
	 *
	 * @param p_ind_char
	 * @return
	 */
	private String indTranslate(String p_ind_char) {
		String retval = ServiceConstants.NULL_STRING;
		if (p_ind_char != null) {
			if (p_ind_char.equals(ServiceConstants.STRING_IND_Y)) {
				retval = ServiceConstants.YES_TEXT;
			} else if (p_ind_char.equals(ServiceConstants.STRING_IND_N)) {
				retval = ServiceConstants.NO_TEXT;
			} else if (p_ind_char.equals(ServiceConstants.INFO_NEEDED)) {
				retval = ServiceConstants.NEEDS_MORE_INFO;
			}
		}
		return retval;
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
					// Fix for Defect 13222 - Updated to use Prior Stage ID for INT (As Stage ID has INV)
					intMergeString += ServiceConstants.COMMA + intake.getIdPriorStage();
				}
			}
		}

		for (CpsInvIntakePersonPrincipalDto intake : getIntakesList) {
			// Fix for Defect 13222 - Updated to use Prior Stage ID for INT (As Stage ID has INV)
			intMergeString += ServiceConstants.COMMA + intake.getIdPriorStage();
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

	/**
	 * Method Name: isEligible Method Description: Checks if eligible for
	 * Emergency Assistance
	 *
	 * @param getSdmSafetyAssessmentsList
	 * @return Boolean
	 */
	private Boolean isEligible(List<CpsInvContactSdmSafetyAssessDto> safetyRiskAssessmentsList) {
		boolean eligible = false;
		boolean household = false;
		// artf152701 sdm assessments before and after household to be separate lists
		List<CpsInvContactSdmSafetyAssessDto> sdmList = new ArrayList<>();
		List<CpsInvContactSdmSafetyAssessDto> householdSdmList = new ArrayList<>();
		if (null != safetyRiskAssessmentsList && 0 < safetyRiskAssessmentsList.size()) {
			for (CpsInvContactSdmSafetyAssessDto assmt : safetyRiskAssessmentsList){
				if (TypeConvUtil.isNullOrEmpty(assmt.getIdHouseholdEvent())) {
					sdmList.add(assmt);
				} else {
					householdSdmList.add(assmt);
				}
			}
			// find the sdm for the one selected household
			if ( 0 < householdSdmList.size()) {
				for (CpsInvContactSdmSafetyAssessDto assmt : householdSdmList){
					if (assmt.getIdEvent().equals(assmt.getIdHouseholdEvent() ) // artf159096
							&& ServiceConstants.HOUSEHOLD_TYPERISK.equals(assmt.getCdAssmtType() ) ) {
						household = true;
						if (ServiceConstants.HIGH.equals(assmt.getCdFinalRiskLevel() )
								|| ServiceConstants.CSDMRLVL_VERYHIGH.equals(assmt.getCdFinalRiskLevel() ) ) {
							eligible = true;
							break;
						}
					}
				}
			}
			// before p2 r1.1, there was at most one risk type
			if ( ! household && 0 < sdmList.size()) {
				for(CpsInvContactSdmSafetyAssessDto assmt : sdmList){
					if(ServiceConstants.HOUSEHOLD_TYPERISK.equals(assmt.getCdAssmtType() )
							&& (ServiceConstants.HIGH.equals(assmt.getCdFinalRiskLevel() )
							|| ServiceConstants.CSDMRLVL_VERYHIGH.equals(assmt.getCdFinalRiskLevel() ) ) ) {
						eligible = true;
						break;
					}
				}
			}
		}
		return eligible;
	}


}
