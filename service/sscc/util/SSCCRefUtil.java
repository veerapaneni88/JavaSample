/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This class is the utility class for SSCCReferralBean
 *Sep 25, 2017- 12:20:40 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.sscc.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.AssignmentGroupDto;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.service.casepackage.dao.SSCCTimelineDao;
import us.tx.state.dfps.service.casepackage.dto.SSCCParameterDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefPlcmtDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCResourceDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCTimelineDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.sscc.dao.SSCCPlacementNetworkDao;
import us.tx.state.dfps.service.sscc.dao.SSCCRefDao;
import us.tx.state.dfps.service.workload.dao.ApprovalDao;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Utility
 * class for SSCCRefService Sep 25, 2017- 12:20:40 PM © 2017 Texas Department of
 * Family and Protective Services.
 */
@Service
@Transactional
public class SSCCRefUtil {

	/** The sscc plcmnt ntwrk dao. */
	@Autowired
	SSCCPlacementNetworkDao ssccPlcmntNtwrkDao;

	/** The sscc ref dao. */
	@Autowired
	SSCCRefDao ssccRefDao;

	/** The sscc timeline dao. */
	@Autowired
	SSCCTimelineDao ssccTimelineDao;

	/** The stage dao. */
	@Autowired
	StageDao stageDao;

	/** The person dao. */
	@Autowired
	PersonDao personDao;

	/** The case utility rel. */
	@Autowired
	CaseUtils caseUtilityRel;

	/** The formatting utils. */
	@Autowired
	FormattingUtils formattingUtils;

	/** The approval dao. */
	@Autowired
	ApprovalDao approvalDao;

	/** The lookup dao. */
	@Autowired
	LookupDao lookupDao;

	/** The Constant TIMELINE_DESC_REF_CREATED. */
	public static final String TIMELINE_DESC_REF_CREATED = " Referral Created";

	/** The Constant TIMELINE_DESC_REF_RESCIND. */
	public static final String TIMELINE_DESC_REF_RESCIND = " Referral Rescinded";

	/** The Constant TIMELINE_DESC_REF_ACKNWLD. */
	public static final String TIMELINE_DESC_REF_ACKNWLD = " Referral Acknowledged";

	/** The Constant TIMELINE_DESC_PERSON_ADD. */
	public static final String TIMELINE_DESC_PERSON_ADD = " added to Family Service Referral";

	/** The Constant TIMELINE_DESC_PERSON_REMOVE. */
	public static final String TIMELINE_DESC_PERSON_REMOVE = " removed from Family Service Referral";

	/** The Constant TIMELINE_DESC_NOTIF_DISCHARGE. */
	public static final String TIMELINE_DESC_NOTIF_DISCHARGE = " End Referral Notification Initiated";

	/** The Constant TIMELINE_DESC_REQ_DISCHARGE. */
	public static final String TIMELINE_DESC_REQ_DISCHARGE = " End Referral Request Initiated";

	/** The Constant TIMELINE_DESC_DISCHARGE_NOTIF_ACKN. */
	public static final String TIMELINE_DESC_DISCHARGE_NOTIF_ACKN = " End Referral Notification Acknowledged";

	/** The Constant TIMELINE_DESC_DISCHARGE_REQ_ACKN. */
	public static final String TIMELINE_DESC_DISCHARGE_REQ_ACKN = " End Referral Request Acknowledged";

	/** The Constant TIMELINE_DESC_DISCHARGE_NOTIF_RESCIND. */
	public static final String TIMELINE_DESC_DISCHARGE_NOTIF_RESCIND = " End Referral Notification Rescinded";

	/** The Constant TIMELINE_DESC_DISCHARGE_REQ_RESCIND. */
	public static final String TIMELINE_DESC_DISCHARGE_REQ_RESCIND = " End Referral Request Rescinded";

	/** The Constant TIMELINE_DESC_DISCHARGE_REQ_REJECT. */
	public static final String TIMELINE_DESC_DISCHARGE_REQ_REJECT = " Referral Discharge Request Rejected";

	/** The Constant TIMELINE_DESC_DISCHARGE_FINAL. */
	public static final String TIMELINE_DESC_DISCHARGE_FINAL = " End Referral Finalized";

	/** The Constant TIMELINE_DESC_DISCHARGE_UNDO. */
	public static final String TIMELINE_DESC_DISCHARGE_UNDO = " SSCC Referral Re-opened by DFPS ";

	/**
	 * Method Name: fetchActiveSSCCRefForStage Method Description :Returns a
	 * list of SSCCRefValueBean objects of active referrals.
	 *
	 * @param idStage
	 *            the id stage
	 * @param cdRefType
	 *            the cd ref type
	 * @return List
	 */
	public List<SSCCRefDto> fetchActiveSSCCRefForStage(Long idStage, String cdRefType) {
		List<SSCCRefDto> res = new ArrayList<SSCCRefDto>();
		res = ssccRefDao.fetchActiveSSCCRefForStage(idStage, ServiceConstants.PLACEMENT_REFERRAL);
		return res;
	}

	/**
	 * Method Name: fetchSSCCRefHeaderDataForNewReferral Method Description:This
	 * method will fetch the cdContractRegion for the case that is within the
	 * SSCC catchment Also, the list of drop down values for Reference Stage
	 * drop down.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param userId
	 *            the user id
	 * @return SSCCRefDto
	 */
	public SSCCRefDto fetchSSCCRefHeaderDataForNewReferral(SSCCRefDto ssccRefDto, String userId) {

		Map<Long, String> mapOfValidSUB = new HashMap<>();
		Long idSSCCCatchment = 0L;
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setIdUser(Long.valueOf(userId));
		// Fetch all open stages for case
		List<StageDto> activeSUBStages = caseUtilityRel.getOpenSUBStages(ssccRefDto.getIdCase());

		for (StageDto stage : activeSUBStages) {
			Long idSSCCCatchmentTemp = 0L;
			// Fetch the Primary Child for stage
			Long idPrimaryChild = stageDao.findPrimaryChildForStage(stage.getIdStage());
			Person person = personDao.getPersonDetails(idPrimaryChild);
			boolean primaryChildHasLegal = ssccRefDao.primaryChildHasLegalStatusForCase(stage.getIdCase(),
					idPrimaryChild);
			boolean hasStageAccess = caseUtilityRel.hasStageAccess(stage.getIdStage(), userProfileDto);
			// If SUB stage then check for valid stage type
			if (!ObjectUtils.isEmpty(stage.getCdStageType())) {
				// If Primary Child Age > 18 OR PC has no legal status for
				// case then check if stage county is within catchment
				// region of SSCC
				// Check if Child Has No Legal Status. If yes, use legal
				// county to identify SSCC Contract Region
				if ((ServiceConstants.STAGE_TYPE_REG.equals(stage.getCdStageType())
						&& (DateUtils.getAge(person.getDtPersonBirth(),
								person.getDtPersonDeath()) >= ServiceConstants.MAX_CLD_AGE || !primaryChildHasLegal))
						|| (CodesConstant.CSTGTYPE_CPB.equals(stage.getCdStageType()) && !primaryChildHasLegal)
						|| (CodesConstant.CSTGTYPE_CRC.equals(stage.getCdStageType()))) {
					SSCCParameterDto ssccParameterDto1 = ssccRefDao
							.fetchSSCCCntrctRegionforStageCounty(stage.getIdStage());
					idSSCCCatchmentTemp = getSSCCCachement(ssccParameterDto1, ssccRefDto, stage, hasStageAccess,
							mapOfValidSUB);
				}
				// Check if PC's current Legal Status within case has a
				// Legal County within catchment area of SSCC with an active
				// contract
				else {
					SSCCParameterDto ssccParameterDto2 = ssccRefDao.fetchSSCCCntrctRegionSUB(stage.getIdCase(),
							idPrimaryChild, stage.getCdStageType());
					idSSCCCatchmentTemp = getSSCCCachement(ssccParameterDto2, ssccRefDto, stage, hasStageAccess,
							mapOfValidSUB);
				}
				if (idSSCCCatchmentTemp != 0L) {
					idSSCCCatchment = idSSCCCatchmentTemp;
				}
			}
		}
		// Set map of valid SUB stages into the value bean
		ssccRefDto.setValidSubStageDropDownList(mapOfValidSUB);

		SSCCResourceDto ssccResourceDto = null;
		if (!ObjectUtils.isEmpty(ssccRefDto.getCdCntrctRegion())) {
			ssccResourceDto = ssccRefDao.fetchSSCCResourceInfo(ssccRefDto.getCdCntrctRegion(), idSSCCCatchment);
		}

		// if there is an active SSCC contract record for catchment region
		if (!ObjectUtils.isEmpty(ssccResourceDto)) {
			ssccRefDto.setIdSSCCResource(ssccResourceDto.getIdSSCCResource());
			ssccRefDto.setNmSSCCResource(ssccResourceDto.getNmSSCCResource());
			ssccRefDto.setCdSSCCCatchment(ssccResourceDto.getCdSSCCCatchment());
			ssccRefDto.setIdRsrcSSCC(ssccResourceDto.getIdSSCCResource());
			ssccRefDto.setSsccResourceDto(ssccResourceDto);
			//Set Stage II indicator to false
			ssccRefDto.setIndSSCCStageII(false);
			// add map of referraltype to sscc resource id
			setReferralTypeToSSCCResourceIdMap( ssccRefDto,  ssccResourceDto  );
			
			if (!ObjectUtils.isEmpty(ssccResourceDto.getDtFamilyServiceReferral())) {
				if (DateUtils.isToday(ssccResourceDto.getDtFamilyServiceReferral())
						|| DateUtils.isBeforeToday(ssccResourceDto.getDtFamilyServiceReferral())) {
					//Set Stage II indicator to true as SSCC is in Stage II
					ssccRefDto.setIndSSCCStageII(true);


										// Fetch all open FSU stages for case
					List<StageDto> activeFSUStages = caseUtilityRel.getOpenFSUStages(ssccRefDto.getIdCase());
					List<StageDto> activeFREStages = caseUtilityRel.getOpenFREStages(ssccRefDto.getIdCase());
					List<StageDto> activeFamilyStages = new ArrayList<StageDto>();
					activeFamilyStages.addAll(activeFSUStages);
					activeFamilyStages.addAll(activeFREStages);
					activeFamilyStages.addAll(activeSUBStages);
					List<StageDto> allSUBStages = caseUtilityRel.getAllSUBStages(ssccRefDto.getIdCase());
					allSUBStages.forEach(stageSUB -> {
						// If Sub stage has at least one Placement Referral then
						// check PC age
						if (ObjectUtils.isEmpty(ssccRefDto.getValidFamStageDropDownList())) {
							Long idPrimaryChild = stageDao.findPrimaryChildForStage(stageSUB.getIdStage());
							Person person = personDao.getPersonDetails(idPrimaryChild);
							// Condition to see if the person age is less than
							// or equal to 18
							if (DateUtils.getAge(person.getDtPersonBirth(),
									person.getDtPersonDeath()) <= ServiceConstants.MAX_CLD_AGE) {
								// stream over the active family stages and get
								// the map of stage id and stage name - stage
								// code if there is one active SSCC Family
								// Service and stage access.
								Map<Long, String> mapOfValidFamilyStages = activeFamilyStages.stream()
										.filter(familyStage -> (!ssccRefDao
												.hasActiveSSCCFamilySvcRefInCase(familyStage.getIdCase())
												&& caseUtilityRel.hasStageAccess(familyStage.getIdStage(),
												userProfileDto)))
										.collect(Collectors.toMap(familyStage -> familyStage.getIdStage(),
												familyStage -> familyStage.getNmStage().concat(" - ")
														.concat(familyStage.getCdStage())));

								if(!mapOfValidFamilyStages.isEmpty() ){
									ssccRefDto.setIndDisplayFamilyRefRequiredDiv(true);
									if(hasNotRescindSSCCRefForCase(ssccRefDto.getIdCase())){
										ssccRefDto.setValidFamStageDropDownList(mapOfValidFamilyStages);
									}
								}
							}
						}
					});

				}
			}
		}

		return ssccRefDto;

	}

	/**
	 * Method Name: getSSCCCachement Method Description: This method sets the
	 * idSSCCCatchment and cdSSCCCatchment.
	 *
	 * @param ssccParameterDto
	 *            the sscc parameter dto
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param stage
	 *            the stage
	 * @param hasStageAccess
	 *            the has stage access
	 * @param mapOfValidSUB
	 *            the map of valid SUB
	 * @return the SSCC cachement
	 */
	private Long getSSCCCachement(SSCCParameterDto ssccParameterDto, SSCCRefDto ssccRefDto, StageDto stage,
			boolean hasStageAccess, Map<Long, String> mapOfValidSUB) {
		Long idSSCCCatchment = 0L;
		if (!ObjectUtils.isEmpty(ssccParameterDto)) {
			if (hasStageAccess && !hasActiveSSCCPlcmtReferralExistsForStage(stage.getIdStage())) {
				mapOfValidSUB.put(stage.getIdStage(), stage.getNmStage());
			}

			if (ObjectUtils.isEmpty(ssccRefDto.getCdCntrctRegion())) {
				ssccRefDto.setCdCntrctRegion(ssccParameterDto.getCdCntrctRegion());
				idSSCCCatchment = ssccParameterDto.getIdSSCCCatchment();
			}

		}
		return idSSCCCatchment;
	}

	/**
	 * Method Name: hasActiveSSCCPlcmtReferralExistsForStage Method
	 * Description:Fetch all the active SSCC Referrals for stage for a the input
	 * Referral type.
	 *
	 * @param idStage
	 *            the id stage
	 * @return boolean
	 */
	public boolean hasActiveSSCCPlcmtReferralExistsForStage(Long idStage) {
		List<SSCCRefDto> referralListforStage = ssccRefDao.fetchActiveSSCCReferralsForStage(idStage,
				ServiceConstants.PLACEMENT_REFERRAL);
		return (!ObjectUtils.isEmpty(referralListforStage) && referralListforStage.size() > 0) ? true : false;
	}

	/**
	 * Method Name: hasNotRescindSSCCRefForCase Method Description: Method
	 * returns true if there is at least one SSCC Referral in the case that has
	 * not been rescinded.
	 *
	 * @param idCase
	 *            the id case
	 * @return boolean
	 */
	public boolean hasNotRescindSSCCRefForCase(Long idCase) {
		List<SSCCRefDto> referralListforStage = ssccRefDao.fetchSSCCNotRescindRefForCase(idCase,
				ServiceConstants.EMPTY_STRING, ServiceConstants.PLACEMENT_REFERRAL);
		return (referralListforStage.size() > 0) ? true : false;
	}

	/**
	 * Method Name: readSSCCRefByPK Method Description:Method fetches the SSCC
	 * Referral information for a Referral Id and returns an SSCCRefDto object.
	 *
	 * @param idSsccReferral
	 *            the id sscc referral
	 * @return SSCCRefDto
	 */
	public SSCCRefDto readSSCCRefByPK(Long idSsccReferral) {
		Map<Long, String> mapOfValidSUB = new HashMap<>();
		Map<Long, String> mapOfStageList = new HashMap<>();
		SSCCRefDto ssccRefDto = new SSCCRefDto();
		HashMap<Long, AssignmentGroupDto> dfpsStaffAssigned = new HashMap<>();
		if (!ObjectUtils.isEmpty(idSsccReferral)) {
			ssccRefDto = ssccRefDao.fetchReferralByPK(idSsccReferral);
			if (!ObjectUtils.isEmpty(ssccRefDto) && !ObjectUtils.isEmpty(ssccRefDto.getIdSSCCResource())) {
				SSCCResourceDto ssccResourceDto = new SSCCResourceDto();
				ssccResourceDto.setIdSSCCResource(ssccRefDto.getIdSSCCResource());
				ssccResourceDto.setCdSSCCCatchment(ssccRefDto.getCdSSCCCatchment());
				ssccResourceDto.setNmSSCCResource(ssccRefDto.getNmSSCCResource());
				ssccRefDto.setSsccResourceDto(ssccResourceDto);
			}
			if (!ObjectUtils.isEmpty(ssccRefDto.getCdRescindReason()))
				ssccRefDto.setIndRefRescind(ServiceConstants.YES);
			// Fetch the DFPS Assigned staff for page display
			HashMap<Long, AssignmentGroupDto> staffAssigned = ssccRefDao
					.fetchDFPSStaffAssignedtoStage(ssccRefDto.getIdStage());
			// Fetch assigned SSCC Secondary staff
			List<Long> ssccSecondaryList = ssccRefDao.fetchSSCCSecondaryForStage(ssccRefDto.getIdStage(),
					ssccRefDto.getCdSSCCCatchment());
			ssccRefDto.setSsccSecondaryStaff(ssccSecondaryList);
			// if there are existing SSCC Secondary staff
			if (!ObjectUtils.isEmpty(ssccSecondaryList)) {
				String strAdditionalSSCCStaff = ServiceConstants.EMPTY_STRING;
				ssccRefDto.setIdSSCCStaff1(ssccSecondaryList.get(0));
				if (ssccSecondaryList.size() > 1) {
					ssccRefDto.setIdSSCCStaff2(ssccSecondaryList.get(1));
				}
				/*
				 * When there are more than 2 SSCC Secondary Staff assigned to a
				 * stage, all additional SSCC Secondary staff other than the two
				 * display on the Referral page will be unassigned on Update
				 * action. This string will be used to display an informational
				 * message to the user that the following additional SSCC
				 * secondary staff are being un-assigned.
				 */
				if (ssccSecondaryList.size() > 1) {
					List<String> names = new ArrayList<>();
					ssccSecondaryList.subList(2, ssccSecondaryList.size()).forEach(o -> {
						names.add(formattingUtils.formatName(o));
					});
					strAdditionalSSCCStaff = String.join(",", names);
				}
				ssccRefDto.setStrAdditionalSSCCStaff(strAdditionalSSCCStaff);
			}
			/*
			 * If there are any SSCC staff assigned to the stage these must be
			 * excluded from the DFPS Staff assigned array list which will be
			 * used to display DFPS staff on Referral Detail page.
			 */
			if (!ObjectUtils.isEmpty(staffAssigned) && !staffAssigned.isEmpty()) {
				staffAssigned.entrySet().forEach(o -> {
					if (CodesConstant.CSTFROLS_SE.equals(o.getValue().getCdStagePersRole())
							&& ssccSecondaryList.size() > 0) {
						// Identify SSCC Secondary Staff and remove them from
						// map
						if (!ssccSecondaryList.contains(o.getKey())) {
							dfpsStaffAssigned.put(o.getKey(), o.getValue());
						}
					} else if ((CodesConstant.CSTFROLS_SE.equals(o.getValue().getCdStagePersRole())
							&& ssccSecondaryList.size() == 0)
							|| CodesConstant.CSTFROLS_PR.equals(o.getValue().getCdStagePersRole())) {
						dfpsStaffAssigned.put(o.getKey(), o.getValue());
					}
				});
			}
		}
		ssccRefDto.setDfpsStaffAssign(dfpsStaffAssigned);
		// Fetch list of SSCC Staff person ids for current referral catchment
		List<Long> ssccStaffList = ssccRefDao.getSSCCStaffInCatchment(ssccRefDto.getCdSSCCCatchment());

		ssccStaffList.forEach(o -> {
			if (!dfpsStaffAssigned.containsKey(o)) {
				mapOfValidSUB.put(o, formattingUtils.formatName(o));
			}
		});
		Map<Long, String> sortedMap = mapOfValidSUB.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors
				.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		ssccRefDto.setSsccStaffInCatchment(sortedMap);

		if (CodesConstant.CSSCCREF_10.equals(ssccRefDto.getCdSSCCRefType())
				|| CodesConstant.CSSCCREF_20.equals(ssccRefDto.getCdSSCCRefType())) {
			mapOfStageList.put(ssccRefDto.getIdStage(), ssccRefDto.getNmStage());
			ssccRefDto.setValidStageList(mapOfStageList);
		} else if (CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
			String cdStage = caseUtilityRel.getStage(ssccRefDto.getIdStage()).getCdStage();
			mapOfStageList.put(ssccRefDto.getIdStage(), ssccRefDto.getNmStage().concat(" - ").concat(cdStage));
			ssccRefDto.setValidStageList(mapOfStageList);
		}
		return ssccRefDto;
	}

	/**
	 * Method Name: ssccReferralListSummaryStatus Method Description:Method
	 * calculates the status that needs to be displayed on the SSCC Referral
	 * List expandable section.
	 *
	 * @param ssccRefListDto
	 *            the sscc ref list dto
	 * @return SSCCRefListDto
	 */
	public SSCCRefListDto ssccReferralListSummaryStatus(SSCCRefListDto ssccRefListDto) {
		List<SSCCRefDto> ssccRefDtos = ssccRefListDto.getSsccReferralForCaseList();
		boolean indCaseHasOnlyDischargeRef = true;
		if (!ssccRefDtos.isEmpty()) {
			ssccRefListDto.setCdListPageStatus(ServiceConstants.NONE);
		} else {
			for (SSCCRefDto ssccRefDto : ssccRefDtos) {
				if (CodesConstant.CSSCCSTA_40.equals(ssccRefDto.getCdRefStatus())
						|| CodesConstant.CSSCCSTA_50.equals(ssccRefDto.getCdRefStatus())) {
					ssccRefListDto.setCdListPageStatus(ServiceConstants.ACTIVE);
				}

				if (!CodesConstant.CSSCCSTA_130.equals(ssccRefDto.getCdRefStatus())) {
					indCaseHasOnlyDischargeRef = false;
				}
			}

			if (indCaseHasOnlyDischargeRef) {
				ssccRefListDto.setCdListPageStatus(ServiceConstants.DISCHARGE);
			}
		}

		return ssccRefListDto;
	}

	/**
	 * Method Name: fetchSSCCReferralPlcmtInfo Method Description: Fetches the
	 * Legal Status data for the Primary Child in a case at the time of SSCC
	 * Referral.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @return SSCCRefDto
	 */
	public SSCCRefDto fetchSSCCReferralPlcmtInfo(SSCCRefDto ssccRefDto) {
		// Fetch Legal Status information
		ssccRefDto = ssccRefDao.fetchLegalStatusForCase(ssccRefDto);
		SSCCRefPlcmtDto ssccRefPlcmtDto = ssccRefDto.getSsccRefPlcmtDto();
		String cdLegalStatus = ssccRefPlcmtDto.getCdLegalStatusType();
		// Check if the current legal status of the child in this case is an
		// Eligible Legal Status for SSCC Referral
		Stage stage = caseUtilityRel.getStage(ssccRefDto.getIdStage());
		if (ServiceConstants.STAGE_TYPE_REG.equals(stage.getCdStageType())
				&& (CodesConstant.CCOR_010.equals(cdLegalStatus) || CodesConstant.CCOR_020.equals(cdLegalStatus)
						|| CodesConstant.CCOR_030.equals(cdLegalStatus) || CodesConstant.CCOR_040.equals(cdLegalStatus)
						|| CodesConstant.CCOR_050.equals(cdLegalStatus) || CodesConstant.CCOR_070.equals(cdLegalStatus)
						|| CodesConstant.CCOR_130.equals(cdLegalStatus))) {
			ssccRefPlcmtDto.setEligibleLegalStatus(cdLegalStatus);
		} else if (CodesConstant.CSTGTYPE_CPB.equals(stage.getCdStageType())
				&& CodesConstant.CCOR_080.equals(cdLegalStatus)) {
			ssccRefPlcmtDto.setEligibleLegalStatus(cdLegalStatus);
		} else {
			ssccRefPlcmtDto.setCdLegalCounty(null);
			ssccRefPlcmtDto.setDtLegalStatusEffective(null);
			ssccRefPlcmtDto.setIdLegalStatusEvent(null);
			ssccRefPlcmtDto.setEligibleLegalStatus(null);
		}
		// fetches the conservatorship removal data.
		ssccRefDto = ssccRefDao.fetchCnsrvtrshpRemovalData(ssccRefDto);
		ssccRefDto.getSsccRefPlcmtDto().setListPriorCaseHistory(ssccRefDao.fetchCaseInformationForPC(ssccRefDto));
		return ssccRefDto;
	}

	/**
	 * Method name: fetchSSCCRefPlcmtInfoforActiveRef Method Description:
	 * Fetches Child Placement Information for Placement Referrals that are in
	 * Active status.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @return the SSCC ref dto
	 */
	public SSCCRefDto fetchSSCCRefPlcmtInfoforActiveRef(SSCCRefDto ssccRefDto) {
		// Fetch Legal Status information
		ssccRefDto = ssccRefDao.fetchLegalStatusForRef(ssccRefDto);
		SSCCRefPlcmtDto ssccRefPlcmtDto = ssccRefDto.getSsccRefPlcmtDto();
		if (!ObjectUtils.isEmpty(ssccRefPlcmtDto)) {
			if (!ObjectUtils.isEmpty(ssccRefPlcmtDto.getCdLegalStatusType())) {
				ssccRefPlcmtDto.setEligibleLegalStatus(ssccRefPlcmtDto.getCdLegalStatusType());
			} else {
				ssccRefPlcmtDto.setCdLegalCounty(null);
				ssccRefPlcmtDto.setDtLegalStatusEffective(null);
				ssccRefPlcmtDto.setEligibleLegalStatus(null);
			}
		}
		// Fetch the Conservatorship Removal Date
		ssccRefDto = ssccRefDao.fetchRefPlcmtCnsrvtrshpRemovalDt(ssccRefDto);
		if (ObjectUtils.isEmpty(ssccRefPlcmtDto)) {
			ssccRefPlcmtDto = new SSCCRefPlcmtDto();
		}
		if (!ObjectUtils.isEmpty(ssccRefDto.getSsccRefPlcmtDto())) {
			ssccRefPlcmtDto.setDtCnsrvtrshpRmvl(ssccRefDto.getSsccRefPlcmtDto().getDtCnsrvtrshpRmvl());
			ssccRefPlcmtDto.setIdCnsrvtrshpRmvlEvent(ssccRefDto.getSsccRefPlcmtDto().getIdCnsrvtrshpRmvlEvent());
		}
		// Fetch the Prior Case Information for Child
		ssccRefPlcmtDto.setListPriorCaseHistory(ssccRefDao.fetchCaseInformationForPC(ssccRefDto));
		ssccRefDto.setSsccRefPlcmtDto(ssccRefPlcmtDto);
		return ssccRefDto;
	}

	/**
	 * Method Name: processSecondaryStaffAssign Method Description: Method saves
	 * new secondary staff and generates required to do's. construct a set of
	 * the SSCC secondary assignments that need to be added or deleted If
	 * SSCCRefDto object has array of SSCC Secondary PID's that are currently
	 * assigned to stage and an array of SSCC Secondary PID's that have now been
	 * selected by user for save If new PID is in existing staff array - no
	 * action If new PID is not an existing staff then 2 actions - Unassign old
	 * staff and assign new staff
	 *
	 * @param ssccReferralDto
	 *            the sscc referral dto
	 * @param userId
	 *            the user id
	 */
	public void processSecondaryStaffAssign(SSCCRefDto ssccReferralDto, String userId) {
		if (!ssccReferralDto.getIndUserSSCC()) {
			List<Long> existingSSCCSecStaffArray = ssccReferralDto.getSsccSecondaryStaff();
			List<Long> newSSCCSecStaffArray = new ArrayList<>();

			if (!ObjectUtils.isEmpty(ssccReferralDto.getIdSSCCStaff1()) && ssccReferralDto.getIdSSCCStaff1() > 0) {
				newSSCCSecStaffArray.add(ssccReferralDto.getIdSSCCStaff1());
			}

			if (!ObjectUtils.isEmpty(ssccReferralDto.getIdSSCCStaff2()) && ssccReferralDto.getIdSSCCStaff2() > 0) {
				newSSCCSecStaffArray.add(ssccReferralDto.getIdSSCCStaff2());
			}
			if (!ObjectUtils.isEmpty(existingSSCCSecStaffArray)) {
				existingSSCCSecStaffArray.forEach(o -> {
					if (!newSSCCSecStaffArray.contains(o)) {
						// deletes the stage person link
						ssccRefDao.deleteStagePersonLink(o, ssccReferralDto.getIdStage());
					}
				});
			}
			if (!ObjectUtils.isEmpty(existingSSCCSecStaffArray) && existingSSCCSecStaffArray.size() > 0
					&& !ObjectUtils.isEmpty(newSSCCSecStaffArray) && newSSCCSecStaffArray.size() > 0) {
				newSSCCSecStaffArray.forEach(o -> {
					if (!existingSSCCSecStaffArray.contains(o)) {
						// inserts into stage person link
						ssccRefDao.insertIntoStagePersonLink(ssccReferralDto, o);
						// create to do for the selected secondary staff
						ssccRefDao.createSecondaryAssignTodo(ssccReferralDto, userId, o,
								approvalDao.getPrimaryWorkerIdForStage(ssccReferralDto.getIdStage()));
					}
				});
			} else if ((ObjectUtils.isEmpty(existingSSCCSecStaffArray) || existingSSCCSecStaffArray.size() == 0)
					&& !ObjectUtils.isEmpty(newSSCCSecStaffArray) && newSSCCSecStaffArray.size() > 0) {
				newSSCCSecStaffArray.forEach(o -> {
					// inserts into stage person link
					ssccRefDao.insertIntoStagePersonLink(ssccReferralDto, o);
					// create to do for the selected secondary staff
					ssccRefDao.createSecondaryAssignTodo(ssccReferralDto, userId, o,
							approvalDao.getPrimaryWorkerIdForStage(ssccReferralDto.getIdStage()));
				});
			}
		}
	}

	/**
	 * Method Name: populateSSCCTimelineDtoForReferral Method Description: this
	 * method is used to populate the timeline dto.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param timelineRefDesc
	 *            the timeline ref desc
	 * @param idUser
	 *            the id user
	 * @return the SSCC timeline dto
	 */
	public SSCCTimelineDto populateSSCCTimelineDtoForReferral(SSCCRefDto ssccRefDto, String timelineRefDesc,
			String idUser) {
		SSCCTimelineDto ssccTimelineDto = new SSCCTimelineDto();
		ssccTimelineDto.setDtLastUpdate(new Date());
		ssccTimelineDto.setIdLastUpdatePerson(Long.valueOf(idUser));
		ssccTimelineDto.setIdCreatedPerson(Long.valueOf(idUser));
		ssccTimelineDto.setDtCreated(new Date());
		ssccTimelineDto.setCdTimelineTableName(CodesConstant.CSSCCTBL_10);
		ssccTimelineDto.setTxtTimelineDesc(timelineRefDesc);
		ssccTimelineDto.setDtRecorded(new Date());
		ssccTimelineDto.setIdSsccReferral(ssccRefDto.getIdSSCCReferral());
		if (ssccRefDto.getIndUserSSCC()) {
			ssccTimelineDto.setIdSSCCResource(ssccRefDto.getIdSSCCResource());
		}
		ssccTimelineDto.setIdReference(ssccRefDto.getIdSSCCReferral());
		return ssccTimelineDto;
	}

	/**
	 * Populate DTO for update notify.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param idUser
	 *            the id user
	 * @return the SSCC ref dto
	 */
	public SSCCRefDto populateDTOForUpdateNotify(SSCCRefDto ssccRefDto, String idUser) {
		SSCCRefDto ssccRefDtoUpdate = new SSCCRefDto();
		BeanUtils.copyProperties(ssccRefDto, ssccRefDtoUpdate);
		String timelineRefDesc = ServiceConstants.EMPTY_STR;
		ssccRefDtoUpdate.setIdLastUpdatePerson(Long.valueOf(idUser));
		ssccRefDtoUpdate.setSsccTimelineDtoList(new ArrayList<>());
		if (!ObjectUtils.isEmpty(ssccRefDtoUpdate.getCdRescindReason())) {
			// Populate ssccTimelineValueBean with the values saved into the
			// SSCC_REFERRAL table
			timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
					.concat(TIMELINE_DESC_REF_RESCIND);
			ssccRefDtoUpdate.getSsccTimelineDtoList()
					.add(populateSSCCTimelineDtoForReferral(ssccRefDto, timelineRefDesc, idUser));
			ssccRefDtoUpdate.setCdRefStatus(CodesConstant.CSSCCSTA_70);
		} else if (!ObjectUtils.isEmpty(ssccRefDtoUpdate.getCdDischargeReason())
				&& ObjectUtils.isEmpty(ssccRefDtoUpdate.getCdRefSubStatus())) {
			if (!ssccRefDto.getIndUserSSCC() && !ObjectUtils.isEmpty(ssccRefDtoUpdate.getCdDischargeReason())) {
				// If user is DFPS and the Discharge is ready for review then
				// set referral sub status to 'Discharge Notification Initiated'
				timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
						.concat(TIMELINE_DESC_NOTIF_DISCHARGE);
				ssccRefDtoUpdate.getSsccTimelineDtoList()
						.add(populateSSCCTimelineDtoForReferral(ssccRefDto, timelineRefDesc, idUser));
				ssccRefDtoUpdate.setCdRefSubStatus(CodesConstant.CSSCCSS_10);
			} else if (ssccRefDto.getIndUserSSCC() && !ObjectUtils.isEmpty(ssccRefDtoUpdate.getCdDischargeReason())) {
				// If user is SSCC and the Discharge is ready for review then
				// set referral sub status to 'Discharge Request Initiated'
				timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
						.concat(TIMELINE_DESC_REQ_DISCHARGE);
				ssccRefDtoUpdate.getSsccTimelineDtoList()
						.add(populateSSCCTimelineDtoForReferral(ssccRefDto, timelineRefDesc, idUser));
				ssccRefDtoUpdate.setCdRefSubStatus(CodesConstant.CSSCCSS_20);
			}
		}
		// If Discharge Reason is not null then Indicator Discharge Ready Review
		// must be set to true
		if (!ObjectUtils.isEmpty(ssccRefDtoUpdate.getCdDischargeReason())) {
			ssccRefDtoUpdate.setIndDischargeReadyReview(ServiceConstants.Y);
		}
		/*
		 * When the Referral Sub Status is NOT NULL but the Discharge Reason is
		 * an EMPTY string one of the following actions have occured: 1. The
		 * initiating party has clicked on the 'Discharge Rescind' button on the
		 * page 2. DFPS user has clicked on the 'Discharge Reject' button (this
		 * is possible only for a Discharge Request initiated by SSCC) 3. When
		 * one of the above actions occurs, the updateNotify method is invoked
		 * and the following fields are updated with a null value: a.
		 * cdRefSubStatus b. dtDischargePlanned c. cdRefDischargeReason 4. The
		 * timeline record in inserted into the Timeline table with a
		 * appropriate description
		 */
		if (!ObjectUtils.isEmpty(ssccRefDtoUpdate.getCdRefSubStatus())
				&& ObjectUtils.isEmpty(ssccRefDtoUpdate.getCdDischargeReason())) {
			ssccRefDtoUpdate.setCdRefSubStatus(null);
			ssccRefDtoUpdate.setDtDischargePlanned(null);
			ssccRefDtoUpdate.setDtDischargeActual(null);
			ssccRefDtoUpdate.setIndDischargeReadyReview(ServiceConstants.N);
			if ((CodesConstant.CSSCCSS_10.equals(ssccRefDto.getCdRefSubStatus())
					|| CodesConstant.CSSCCSS_30.equals(ssccRefDto.getCdRefSubStatus()))) {
				timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
						.concat(TIMELINE_DESC_DISCHARGE_NOTIF_RESCIND);
			} else if ((CodesConstant.CSSCCSS_20.equals(ssccRefDto.getCdRefSubStatus())
					|| CodesConstant.CSSCCSS_40.equals(ssccRefDto.getCdRefSubStatus()))) {
				timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
						.concat(TIMELINE_DESC_DISCHARGE_REQ_RESCIND);
			} 
			ssccRefDtoUpdate.getSsccTimelineDtoList()
					.add(populateSSCCTimelineDtoForReferral(ssccRefDto, timelineRefDesc, idUser));
		}
		return ssccRefDtoUpdate;
	}
	
	
	/**
	 * Set the mapping from referral type to  ssc resource Id. This info used to drive
	 * the dropdown in SSCCReferralDetails.jsp
	 * @param ssccResourceDto
	 * @return map
	 */
	
	private void setReferralTypeToSSCCResourceIdMap(SSCCRefDto ssccRefDto, SSCCResourceDto ssccResourceDto) {
		HashMap<String, Long> hMap = new HashMap <String, Long>();
		hMap.put(CodesConstant.CSSCCREF_10, ssccResourceDto.getIdSSCCResource() );
		hMap.put(CodesConstant.CSSCCREF_20, ssccResourceDto.getIdSSCCResource() );
		hMap.put(CodesConstant.CSSCCREF_30, ssccResourceDto.getIdResourceFamilySA());
		
		ssccRefDto.setReferralTypeToResourceIdMap(hMap);
	}

	public Date getActiveSubStageStartDate(Long idCase){
		Date subStageStartDate = null;

		List<StageDto> activeSUBStages = caseUtilityRel.getOpenSUBStages(idCase);
		if(!activeSUBStages.isEmpty()){
			subStageStartDate  = activeSUBStages.get(0).getDtStageStart();
			for (StageDto stage : activeSUBStages) {
				if(subStageStartDate.after(stage.getDtStageStart())){
					subStageStartDate = stage.getDtStageStart();
				}
			}
		}
		return subStageStartDate;
	}

}
