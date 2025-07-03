package us.tx.state.dfps.service.sscc.serviceimpl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.AssignmentGroupDto;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.SSCCTimelineDao;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCParameterDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefFamilyDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefPlcmtDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCResourceDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCTimelineDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.HasSSCCReferralReq;
import us.tx.state.dfps.service.common.request.SSCCRefListKeyReq;
import us.tx.state.dfps.service.common.request.SSCCReferralReq;
import us.tx.state.dfps.service.common.response.HasSSCCReferralRes;
import us.tx.state.dfps.service.common.response.SSCCRefListKeyRes;
import us.tx.state.dfps.service.common.response.SSCCReferralRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.sscc.dao.SSCCListDao;
import us.tx.state.dfps.service.sscc.dao.SSCCRefDao;
import us.tx.state.dfps.service.sscc.service.SSCCRefService;
import us.tx.state.dfps.service.sscc.util.SSCCRefUtil;
import us.tx.state.dfps.service.stageutility.dao.StageUtilityDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: SSCCReferralBean
 * Description: This class will do the serive business logic required for sscc
 * Referral detail Mar 24, 2017 - 7:50:07 PM.
 */
@Service
@Transactional
public class SSCCRefServiceImpl implements SSCCRefService {

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	/** The stage dao. */
	@Autowired
	StageDao stageDao;

	/** The person dao. */
	@Autowired
	PersonDao personDao;

	/** The caps case dao. */
	@Autowired
	CapsCaseDao capsCaseDao;

	/** The sscc ref dao. */
	@Autowired
	SSCCRefDao ssccRefDao;

	/** The sscc timeline dao. */
	@Autowired
	SSCCTimelineDao ssccTimelineDao;

	/** The sscc ref util. */
	@Autowired
	SSCCRefUtil ssccRefUtil;

	/** The case utils. */
	@Autowired
	CaseUtils caseUtils;

	/** The stage utility dao. */
	@Autowired
	StageUtilityDao stageUtilityDao;

	/** The lookup dao. */
	@Autowired
	LookupDao lookupDao;

	@Autowired
	SSCCListDao ssccListDao;

	/** The case utility rel. */
	@Autowired
	CaseUtils caseUtilityRel;


	/** The Constant TIMELINE_DTDFPS_UPDATE_FIXER. */
	public static final String TIMELINE_DTDFPS_UPDATE_FIXER = " Date Recorded DFPS updated by SSCC Referral Fixer";

	/** The Constant TIMELINE_DTSSCC_UPDATE_FIXER. */
	public static final String TIMELINE_DTSSCC_UPDATE_FIXER = " Date Recorded SSCC updated by SSCC Referral Fixer";

	/** The Constant TIMELINE_REFTYPE_FIXER. */
	public static final String TIMELINE_REFTYPE_FIXER = " SSCC Referral Type updated by SSCC Referral Fixer";

	/** The Constant TIMELINE_REFSUBTYPE_UPDATE_FIXER. */
	public static final String TIMELINE_REFSUBTYPE_UPDATE_FIXER = " SSCC Referral Subtype updated by SSCC Referral Fixer";

	/** The Constant TIMELINE_IND_PRIOR_COMM_UPDATE_FIXER. */
	public static final String TIMELINE_IND_PRIOR_COMM_UPDATE_FIXER = " Prior Comm Indicator updated by SSCC Referral Fixer";

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
	public static final String TIMELINE_DESC_DISCHARGE_NOTIF_RESCIND = "End Referral Notification Rescinded";

	/** The Constant TIMELINE_DESC_DISCHARGE_REQ_RESCIND. */
	public static final String TIMELINE_DESC_DISCHARGE_REQ_RESCIND = " End Referral Request Rescinded";

	/** The Constant TIMELINE_DESC_DISCHARGE_REQ_REJECT. */
	public static final String TIMELINE_DESC_DISCHARGE_REQ_REJECT = " Referral Discharge Request Rejected";

	/** The Constant TIMELINE_DESC_DISCHARGE_FINAL. */
	public static final String TIMELINE_DESC_DISCHARGE_FINAL = " End Referral Finalized";

	/** The Constant TIMELINE_DESC_DISCHARGE_UNDO. */
	public static final String TIMELINE_DESC_DISCHARGE_UNDO = " SSCC Referral Re-opened by DFPS ";

	/** The Constant MSG_SSCC_REF_DIS_OUTSTANDING. */
	public static final int MSG_SSCC_REF_DIS_OUTSTANDING = 56231;

	/** The Constant MSG_SSCC_REF_SVC_AUTH_OUTSTANDING. */
	public static final int MSG_SSCC_REF_SVC_AUTH_OUTSTANDING = 56232;

	/** The Constant MSG_SSCC_REF_SVC_APRV_OUTSTANDING. */
	public static final int MSG_SSCC_REF_SVC_APRV_OUTSTANDING = 56233;

	/** The Constant MSG_SSCC_REF_PLCMT_OPT_OUTSTANDING. */
	public static final int MSG_SSCC_REF_PLCMT_OPT_OUTSTANDING = 56234;

	/** The Constant MSG_SSCC_REF_OPEN_PLCMT. */
	public static final int MSG_SSCC_REF_OPEN_PLCMT = 56235;

	/** The Constant MSG_SSCC_PLCMT_END_GT_ACT_DIS. */
	public static final int MSG_SSCC_PLCMT_END_GT_ACT_DIS = 56236;

	/** The Constant MSG_SSCC_REF_NO_UNDO_DIS. */
	public static final int MSG_SSCC_REF_NO_UNDO_DIS = 56405;

	/** The Constant MSG_SSCC_REF_DT_PRIOR_REQ. */
	public static final int MSG_SSCC_REF_DT_PRIOR_REQ = 56406;

	/** The Constant MSG_SSCC_DT_PRIOR_REQ_PLCMT_LINK. */
	public static final int MSG_SSCC_DT_PRIOR_REQ_PLCMT_LINK = 56399;

	/** The Constant MSG_SSCC_CP_LINKED. */
	public static final int MSG_SSCC_CP_LINKED = 56400;

	/** The Constant MSG_SSCC_ACT_PLCMT_LINKED. */
	public static final int MSG_SSCC_ACT_PLCMT_LINKED = 56401;

	/** The Constant MSG_SSCC_PLCMT_LINKED. */
	public static final int MSG_SSCC_PLCMT_LINKED = 56402;

	/** The Constant MSG_SSCC_SVC_AUTH_LINKED. */
	public static final int MSG_SSCC_SVC_AUTH_LINKED = 56403;

	/** The Constant MSG_SSCC_TERM_SVC_AUTH_LINKED. */
	public static final int MSG_SSCC_TERM_SVC_AUTH_LINKED = 56404;

	/** The Constant MSG_DT_NOT_PRIOR_SSCC_RES_START. */
	public static final int MSG_DT_NOT_PRIOR_SSCC_RES_START = 56365;

	//Defect 14093, artf146687 changes.
	private static final int MSG_FMLY_REF_UPDATED_CODE = 57127;

	private static final String MSG_FMLY_REF_UPDATED_MSG = "Family Referral already exists. Navigate to Case Summary to view.";

	/** The Constant TIMELINE_DESC_REF_CREATED_TRANSFER_PROCESS. */
	public static final String TIMELINE_DESC_REF_CREATED_TRANSFER_PROCESS = " Referral Created by Transfer Process";

	/** The Constant TIMELINE_DESC_DISCHARGE_FINAL_TRANSFER_PROCESS. */
	public static final String TIMELINE_DESC_DISCHARGE_FINAL_TRANSFER_PROCESS = " End Referral Finalized by Transfer Process";

	/**
	 * This method will return SSCCRefListValueBean with indicators to display
	 * the SSCC Referral Section on the Case Summary page and indicator to
	 * display Add button on the SSCC Referral Section and the SSCC Catchment
	 * region for this case.
	 *
	 * @param sSCCRefListDto
	 *            the s SCC ref list dto
	 * @param ulIdPerson
	 *            the ul id person
	 * @return the SSCC parameter dto
	 */
	@Override
	@Transactional
	public SSCCParameterDto fetchValidStageSUBForRefDisplay(SSCCRefListDto sSCCRefListDto, Long ulIdPerson) {
		SSCCParameterDto sSCCParameterDto = null;
		SSCCParameterDto newSSCCParameterDto = null;
		List<StageDto> stageDtoList = new ArrayList<>();
		Long idPrimaryChild = ServiceConstants.NULL_VAL;
		int personAge = ServiceConstants.Zero;
		boolean primaryChildHasLegalStatus = false;
		List<SSCCRefDto> sSCCRefDtoList = new ArrayList<>();
		stageDtoList = stageDao.getStagesByType(sSCCRefListDto.getIdCase(), ServiceConstants.OPEN_STAGES,
				CodesConstant.CSTAGES_SUB);
		if (!TypeConvUtil.isNullOrEmpty(stageDtoList)) {
			for (StageDto stageDto : stageDtoList) {
				if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
					if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdStage())) {
						idPrimaryChild = stageDao.findPrimaryChildForStage(stageDto.getIdStage());
					}
					if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageType())) {
						if (stageDto.getCdStageType().equals(ServiceConstants.STAGE_TYPE_REG)) {
							if (!TypeConvUtil.isNullOrEmpty(idPrimaryChild)) {
								PersonDto personDto = personDao.getPersonById(idPrimaryChild);
								if (!TypeConvUtil.isNullOrEmpty(personDto)) {
									personAge = DateUtils.calculatePersonAge(personDto);
									if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdCase())) {
										primaryChildHasLegalStatus = capsCaseDao.primaryChildHasLegalStatusForCase(
												stageDto.getIdCase(), idPrimaryChild);
									}
									if (!primaryChildHasLegalStatus || personAge >= ServiceConstants.MAX_CLD_AGE) {
										if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdStage())) {
											newSSCCParameterDto = ssccRefDao
													.fetchSSCCCntrctRegionforStageCounty(stageDto.getIdStage());
											if (!TypeConvUtil.isNullOrEmpty(newSSCCParameterDto)) {
												sSCCParameterDto = newSSCCParameterDto;
												if (!TypeConvUtil.isNullOrEmpty(ulIdPerson)) {
													if (stageDao.hasStageAccess(stageDto.getIdStage(), ulIdPerson)) {
														sSCCRefDtoList = ssccRefDao.fetchActiveSSCCReferralsForStage(
																stageDto.getIdStage(),
																ServiceConstants.PLACEMENT_REFERAAL);
														if (sSCCRefDtoList.size() <= ServiceConstants.Zero) {
															sSCCRefListDto.setIndDisplayReferralAddButton(true);
															return sSCCParameterDto;
														}
													}
												}
											}
										}
									} else {
										if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdCase())
												&& !TypeConvUtil.isNullOrEmpty(stageDto.getCdStageType())) {
											newSSCCParameterDto = ssccRefDao.fetchSSCCCntrctRegionSUB(
													stageDto.getIdCase(), idPrimaryChild, stageDto.getCdStageType());
											if (!TypeConvUtil.isNullOrEmpty(newSSCCParameterDto)) {
												sSCCParameterDto = newSSCCParameterDto;
												if (!TypeConvUtil.isNullOrEmpty(ulIdPerson)
														&& !TypeConvUtil.isNullOrEmpty(stageDto.getIdStage())) {
													if (stageDao.hasStageAccess(stageDto.getIdStage(), ulIdPerson)) {
														sSCCRefDtoList = ssccRefDao.fetchActiveSSCCReferralsForStage(
																stageDto.getIdStage(),
																ServiceConstants.PLACEMENT_REFERAAL);
														if (sSCCRefDtoList.size() <= ServiceConstants.Zero) {
															sSCCRefListDto.setIndDisplayReferralAddButton(true);
															return sSCCParameterDto;
														}
													}
												}
											}
										}
									}
								}
							}
						} else if (stageDto.getCdStageType().equals(CodesConstant.CSTGTYPE_CPB)) {
							if (!TypeConvUtil.isNullOrEmpty(idPrimaryChild)) {
								if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdCase())) {
									primaryChildHasLegalStatus = capsCaseDao
											.primaryChildHasLegalStatusForCase(stageDto.getIdCase(), idPrimaryChild);
								}
								if (!primaryChildHasLegalStatus) {
									if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdStage())) {
										newSSCCParameterDto = ssccRefDao
												.fetchSSCCCntrctRegionforStageCounty(stageDto.getIdStage());
										if (!TypeConvUtil.isNullOrEmpty(newSSCCParameterDto)) {
											sSCCParameterDto = newSSCCParameterDto;
											if (!TypeConvUtil.isNullOrEmpty(ulIdPerson)) {
												if (stageDao.hasStageAccess(stageDto.getIdStage(), ulIdPerson)) {
													sSCCRefDtoList = ssccRefDao.fetchActiveSSCCReferralsForStage(
															stageDto.getIdStage(), ServiceConstants.PLACEMENT_REFERAAL);
													if (sSCCRefDtoList.size() <= ServiceConstants.Zero) {
														sSCCRefListDto.setIndDisplayReferralAddButton(true);
														return sSCCParameterDto;
													}
												}
											}
										}
									}
								} else {
									if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdCase())
											&& !TypeConvUtil.isNullOrEmpty(stageDto.getCdStageType())) {
										newSSCCParameterDto = ssccRefDao.fetchSSCCCntrctRegionSUB(stageDto.getIdCase(),
												idPrimaryChild, stageDto.getCdStageType());
										if (!TypeConvUtil.isNullOrEmpty(newSSCCParameterDto)) {
											sSCCParameterDto = newSSCCParameterDto;
											if (!TypeConvUtil.isNullOrEmpty(ulIdPerson)
													&& !TypeConvUtil.isNullOrEmpty(stageDto.getIdStage())) {
												if (stageDao.hasStageAccess(stageDto.getIdStage(), ulIdPerson)) {
													sSCCRefDtoList = ssccRefDao.fetchActiveSSCCReferralsForStage(
															stageDto.getIdStage(), ServiceConstants.PLACEMENT_REFERAAL);
													if (sSCCRefDtoList.size() <= ServiceConstants.Zero) {
														sSCCRefListDto.setIndDisplayReferralAddButton(true);
														return sSCCParameterDto;
													}
												}
											}
										}
									}
								}
							}
						} else if (stageDto.getCdStageType().equals(CodesConstant.CSTGTYPE_CRC)) {
							if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdStage())) {
								newSSCCParameterDto = ssccRefDao
										.fetchSSCCCntrctRegionforStageCounty(stageDto.getIdStage());
								if (!TypeConvUtil.isNullOrEmpty(newSSCCParameterDto)) {
									sSCCParameterDto = newSSCCParameterDto;
									if (!TypeConvUtil.isNullOrEmpty(ulIdPerson)) {
										if (stageDao.hasStageAccess(stageDto.getIdStage(), ulIdPerson)) {
											sSCCRefDtoList = ssccRefDao.fetchActiveSSCCReferralsForStage(
													stageDto.getIdStage(), ServiceConstants.PLACEMENT_REFERAAL);
											if (sSCCRefDtoList.size() <= ServiceConstants.Zero) {
												sSCCRefListDto.setIndDisplayReferralAddButton(true);
												return sSCCParameterDto;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return sSCCParameterDto;
	}

	/**
	 * Method validates and identifies open FSU and FRE stages within the case
	 * that can be used as Reference stages for new SSCC Referrals.
	 *
	 * @param sSCCRefListDto
	 *            the s SCC ref list dto
	 * @param ulIdPerson
	 *            the ul id person
	 * @param sSCCResouceDto
	 *            the s SCC resouce dto
	 */
	@Override
	@Transactional
	public void fetchValidFamilyStageForRefDisplay(SSCCRefListDto sSCCRefListDto, Long ulIdPerson,
												   SSCCResourceDto sSCCResouceDto) {
		boolean indStageHasNoActiveFamServiceRef = false;
		Date today = new Date();
		int personAge = ServiceConstants.Zero;
		if (!TypeConvUtil.isNullOrEmpty(sSCCResouceDto)) {
			if (!TypeConvUtil.isNullOrEmpty(sSCCResouceDto.getDtFamilyServiceReferral())) {
				if (sSCCResouceDto.getDtFamilyServiceReferral().equals(today)
						|| sSCCResouceDto.getDtFamilyServiceReferral().before(today)) {
					if (!TypeConvUtil.isNullOrEmpty(sSCCRefListDto)) {
						if (!TypeConvUtil.isNullOrEmpty(sSCCRefListDto.getIdCase())) {
							List<StageDto> activeFamilyStageList = new ArrayList<>();
							List<StageDto> activeFSUStages = stageDao.getStagesByType(sSCCRefListDto.getIdCase(),
									ServiceConstants.OPEN_STAGES, CodesConstant.CSTAGES_FSU);
							List<StageDto> activeFREStages = stageDao.getStagesByType(sSCCRefListDto.getIdCase(),
									ServiceConstants.OPEN_STAGES, CodesConstant.CSTAGES_FRE);
							List<StageDto> activeSUBStages = stageDao.getStagesByType(sSCCRefListDto.getIdCase(),
									ServiceConstants.OPEN_STAGES, CodesConstant.CSTAGES_SUB);
							activeFamilyStageList.addAll(activeFSUStages);
							activeFamilyStageList.addAll(activeFREStages);
							activeFamilyStageList.addAll(activeSUBStages);
							for (StageDto stageDto : activeFamilyStageList) {
								if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdStage())) {
									List<SSCCRefDto> sSCCRefDtoList = ssccRefDao.fetchActiveSSCCReferralsForStage(
											stageDto.getIdStage(), ServiceConstants.FAMILY_REFERAAL);
									if (!TypeConvUtil.isNullOrEmpty(sSCCRefDtoList)) {
										if (sSCCRefDtoList.size() <= ServiceConstants.Zero) {
											if (!TypeConvUtil.isNullOrEmpty(ulIdPerson)) {
												if (stageDao.hasStageAccess(stageDto.getIdStage(), ulIdPerson)) {
													indStageHasNoActiveFamServiceRef = true;
												}
											}
										}else{
											indStageHasNoActiveFamServiceRef = false;
											break;
										}
									}
								}
							}
							if (indStageHasNoActiveFamServiceRef) {
								List<StageDto> allSUBStages = stageDao.getStagesByType(sSCCRefListDto.getIdCase(),
										ServiceConstants.ALL_STAGES, CodesConstant.CSTAGES_SUB);
								if (!TypeConvUtil.isNullOrEmpty(allSUBStages)) {
									for (StageDto stageDto : allSUBStages) {
										List<SSCCRefDto> sSCCRefDtoList = ssccRefDao.fetchSSCCNotRescindRefForCase(
												sSCCRefListDto.getIdCase(), ServiceConstants.EMPTY_STRING,
												ServiceConstants.PLACEMENT_REFERAAL);
										if (!TypeConvUtil.isNullOrEmpty(sSCCRefDtoList)) {
											if (sSCCRefDtoList.size() > ServiceConstants.Zero) {
												if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdStage())) {
													Long idPrimaryChild = stageDao
															.findPrimaryChildForStage(stageDto.getIdStage());
													if (!TypeConvUtil.isNullOrEmpty(idPrimaryChild)) {
														PersonDto personDto = personDao.getPersonById(idPrimaryChild);
														if (!TypeConvUtil.isNullOrEmpty(personDto)) {
															personAge = DateUtils.calculatePersonAge(personDto);
														}
													}
												}
												if (personAge <= ServiceConstants.MAX_CLD_AGE) {
													sSCCRefListDto.setIndDisplayReferralAddButton(true);
													return;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This method will be used by CaseSummary.displayCaseSummary() to check if
	 * the SSCC Referral Section and Add button need to be displayed on the Case
	 * Summary page. Note: Calling method must set the Case Id into the
	 * ssccRefListValBean
	 *
	 * @param sSCCRefListDto
	 *            the s SCC ref list dto
	 * @param ulIdPerson
	 *            the ul id person
	 * @return the SSCC ref list dto
	 * @returnSSCCRefListDto @
	 */
	@Override
	@Transactional
	public SSCCRefListDto processDisplayLogicForAddButton(SSCCRefListDto sSCCRefListDto, Long ulIdPerson) {
		SSCCParameterDto sSCCParameterDto = null;
		SSCCResourceDto sSCCResourceDto = null;
		if (!TypeConvUtil.isNullOrEmpty(sSCCRefListDto)) {
			if (!TypeConvUtil.isNullOrEmpty(sSCCRefListDto.getIdCase())) {
				String caseProgram = capsCaseDao.fetchCaseProgram(sSCCRefListDto.getIdCase());
				if (!TypeConvUtil.isNullOrEmpty(caseProgram)) {
					// Check if Case is CPS
					if (caseProgram.equals(CodesConstant.CPGRMS_CPS)) {
						sSCCParameterDto = this.fetchValidStageSUBForRefDisplay(sSCCRefListDto, ulIdPerson);
						// Catchment then ssccParametersValBean is null
						if (!TypeConvUtil.isNullOrEmpty(sSCCParameterDto)) {
							if (!TypeConvUtil.isNullOrEmpty(sSCCParameterDto.getCdCntrctRegion())
									&& !TypeConvUtil.isNullOrEmpty(sSCCParameterDto.getIdSSCCCatchment())) {
								sSCCResourceDto = ssccRefDao.fetchSSCCResourceInfo(sSCCParameterDto.getCdCntrctRegion(),
										sSCCParameterDto.getIdSSCCCatchment());
							}
							if (!TypeConvUtil.isNullOrEmpty(sSCCParameterDto.getCdCntrctRegion())
									&& !TypeConvUtil.isNullOrEmpty(ulIdPerson)) {
								// Set flag to indicate if user is SSCC
								sSCCRefListDto.setUserSSCC(ssccRefDao.isUserSSCCExternal(ulIdPerson,
										sSCCParameterDto.getCdCntrctRegion()));
							}
						}
						// button
						if (TypeConvUtil.isNullOrEmpty(sSCCResourceDto)) {
							sSCCRefListDto.setIndDisplayReferralAddButton(false);
						}

						//Setting indicator for SSCC is in Stage I or Stage II
						sSCCRefListDto.setSSCCStageII(getSSCCStageIndicator(sSCCResourceDto));

						// Business Rules to Display SSCC Referral Section
						// on the Case Summary Page
						// Rule 1: Case has at least one existing Referral
						// (active or inactive) OR
						SSCCRefListDto newSSCCRefListDto = ssccRefDao.fetchReferralsForCase(sSCCRefListDto);
						if (!TypeConvUtil.isNullOrEmpty(newSSCCRefListDto)) {
							if (!TypeConvUtil.isNullOrEmpty(newSSCCRefListDto.getSsccReferralForCaseList())) {
								if (newSSCCRefListDto.getSsccReferralForCaseList().size() > ServiceConstants.Zero) {
									sSCCRefListDto.setIndDisplayReferralSection(true);
								} // fetchValidSUBForRefDisplay())
								if (sSCCRefListDto.isIndDisplayReferralAddButton()) {
									sSCCRefListDto.setIndDisplayReferralSection(true);
								}
							}
						}
						// (active/inactive) and PC < 18 years
						if (!sSCCRefListDto.isIndDisplayReferralAddButton()) {
							this.fetchValidFamilyStageForRefDisplay(sSCCRefListDto, ulIdPerson, sSCCResourceDto);
						}
						// Display referral section only if 1) there is at
						// least on referral for case 2) OR if the Add
						// button is available
						SSCCRefListDto newSSCCRefListDto2 = ssccRefDao.fetchReferralsForCase(sSCCRefListDto);
						if (!TypeConvUtil.isNullOrEmpty(newSSCCRefListDto2)) {
							if (!TypeConvUtil.isNullOrEmpty(newSSCCRefListDto2.getSsccReferralForCaseList())) {
								if (newSSCCRefListDto2.getSsccReferralForCaseList().size() > ServiceConstants.Zero) {
									sSCCRefListDto.setIndDisplayReferralSection(true);
								} else {
									sSCCRefListDto.setIndDisplayReferralSection(
											sSCCRefListDto.isIndDisplayReferralAddButton());
								}
							} else {
								sSCCRefListDto.setIndDisplayReferralSection(false);
							}
						} else {
							sSCCRefListDto.setIndDisplayReferralSection(false);
						}
					}
				}
			}
		}
		return sSCCRefListDto;
	}



	/**
	 * getSSCCRefListKey.
	 *
	 * @param sSCCRefListKeyReq
	 *            the s SCC ref list key req
	 * @return the SSCC ref list key
	 */
	@Override
	@Transactional
	public SSCCRefListKeyRes getSSCCRefListKey(SSCCRefListKeyReq sSCCRefListKeyReq) {
		SSCCRefListKeyRes sSCCRefListKeyRes = new SSCCRefListKeyRes();
		SSCCRefListDto sSCCRefListDto = new SSCCRefListDto();
		if (!TypeConvUtil.isNullOrEmpty(sSCCRefListKeyReq)) {
			if (!TypeConvUtil.isNullOrEmpty(sSCCRefListKeyReq.getIdCase())) {
				sSCCRefListDto.setIdCase(sSCCRefListKeyReq.getIdCase());
			}
			if (!TypeConvUtil.isNullOrEmpty(sSCCRefListKeyReq.getIdPerson())) {
				sSCCRefListDto = this.processDisplayLogicForAddButton(sSCCRefListDto, sSCCRefListKeyReq.getIdPerson());
			}
			sSCCRefListDto = ssccRefDao.fetchReferralsForCase(sSCCRefListDto);
			sSCCRefListDto = this.ssccReferralListSummaryStatus(sSCCRefListDto);
			if (!TypeConvUtil.isNullOrEmpty(sSCCRefListDto.getIdCase())) {
				ssccRefDao.deleteNewSSCCReferral(sSCCRefListDto.getIdCase());
			}
		}
		sSCCRefListKeyRes.setsSCCRefListDto(sSCCRefListDto);
		return sSCCRefListKeyRes;
	}

	/**
	 * Returns true if there is at least one SSCC Referral for given stage.
	 *
	 * @param idStage
	 *            the id stage
	 * @param cdRefType
	 *            the cd ref type
	 * @return @
	 */
	@Override
	@Transactional
	public boolean hasSSCCReferralForStage(Long idStage, String cdRefType) {
		boolean indStagehasReferral = false;
		List<SSCCRefDto> sSCCReferralForStage = null;
		List<SSCCRefDto> sSCCPlcmtReferralExistsForStage = null;
		List<SSCCRefDto> sSCCFamilyServReferralExistsForStage = null;
		if (!TypeConvUtil.isNullOrEmpty(cdRefType)) {
			if (ServiceConstants.ALL.equals(cdRefType)) {
				if (!TypeConvUtil.isNullOrEmpty(idStage)) {
					sSCCReferralForStage = ssccRefDao.fetchSSCCReferralsForStage(idStage, ServiceConstants.EMPTY_STRING,
							ServiceConstants.EMPTY_STRING);
					if (!TypeConvUtil.isNullOrEmpty(sSCCReferralForStage)) {
						if (sSCCReferralForStage.size() > ServiceConstants.Zero) {
							indStagehasReferral = true;
						}
					}
				}
			} else if (ServiceConstants.PLACEMENT_REFERAAL.equals(cdRefType)) {
				sSCCPlcmtReferralExistsForStage = ssccRefDao.fetchSSCCReferralsForStage(idStage,
						ServiceConstants.EMPTY_STRING, ServiceConstants.PLACEMENT_REFERAAL);
				if (!TypeConvUtil.isNullOrEmpty(sSCCPlcmtReferralExistsForStage)) {
					if (sSCCPlcmtReferralExistsForStage.size() > ServiceConstants.Zero) {
						indStagehasReferral = true;
					}
				}
			} else if (ServiceConstants.FAMILY_REFERAAL.equals(cdRefType)) {
				sSCCFamilyServReferralExistsForStage = ssccRefDao.fetchSSCCReferralsForStage(idStage,
						ServiceConstants.EMPTY_STRING, ServiceConstants.FAMILY_REFERAAL);
				if (!TypeConvUtil.isNullOrEmpty(sSCCFamilyServReferralExistsForStage)) {
					if (sSCCFamilyServReferralExistsForStage.size() > ServiceConstants.Zero) {
						indStagehasReferral = true;
					}
				}
			}
		}
		return indStagehasReferral;
	}

	/**
	 * Method calculates the status that needs to be displayed on the SSCC
	 * Referral List expandable section.
	 *
	 * @param sSCCRefListDto
	 *            the s SCC ref list dto
	 * @return @
	 */
	@Override
	@Transactional
	public SSCCRefListDto ssccReferralListSummaryStatus(SSCCRefListDto sSCCRefListDto) {
		List<SSCCRefDto> sSCCRefDtoList = null;
		boolean indCaseHasOnlyDischargeRef = true;
		if (!TypeConvUtil.isNullOrEmpty(sSCCRefListDto)) {
			sSCCRefDtoList = sSCCRefListDto.getSsccReferralForCaseList();
			if (!CollectionUtils.isEmpty(sSCCRefDtoList)) {
				for (SSCCRefDto sSCCRefDto : sSCCRefDtoList) {
					if (!TypeConvUtil.isNullOrEmpty(sSCCRefDto)) {
						if (!TypeConvUtil.isNullOrEmpty(sSCCRefDto.getCdRefStatus())) {
							if (sSCCRefDto.getCdRefStatus().equals(CodesConstant.CSSCCSTA_40)
									|| sSCCRefDto.getCdRefStatus().equals(CodesConstant.CSSCCSTA_50)) {
								sSCCRefListDto.setCdListPageStatus(ServiceConstants.ACTIVE);
							}
							if (!sSCCRefDto.getCdRefStatus().equals(CodesConstant.CSSCCSTA_130)) {
								indCaseHasOnlyDischargeRef = false;
							}
						}
					}
				}
				if (indCaseHasOnlyDischargeRef) {
					sSCCRefListDto.setCdListPageStatus(ServiceConstants.DISCHARGE);
				}
			} else {
				sSCCRefListDto.setCdListPageStatus(ServiceConstants.NONE);
			}
		}
		return sSCCRefListDto;
	}

	/**
	 * Returns true if there is at least one SSCC Referral for given stage.
	 *
	 * @param hasSSCCReferralReq
	 *            the has SSCC referral req
	 * @return @
	 */
	@Override
	public HasSSCCReferralRes hasSSCCReferral(HasSSCCReferralReq hasSSCCReferralReq) {
		HasSSCCReferralRes hasSSCCReferralRes = new HasSSCCReferralRes();
		String indStageHasSSCCRef = ServiceConstants.FALSE;
		if (!TypeConvUtil.isNullOrEmpty(hasSSCCReferralReq)) {
			if (!TypeConvUtil.isNullOrEmpty(hasSSCCReferralReq.getIdStage())) {
				if (this.hasSSCCReferralForStage(hasSSCCReferralReq.getIdStage(), ServiceConstants.ALL)) {
					indStageHasSSCCRef = ServiceConstants.TRUE;
				}
			}
		}
		hasSSCCReferralRes.setIndStageHasSSCCRef(indStageHasSSCCRef);
		return hasSSCCReferralRes;
	}

	/**
	 * Returns true if there is at least one SSCC Referral for given stage.
	 *
	 * @param hasSSCCReferralReq
	 *            the has SSCC referral req
	 * @return @
	 */
	@Override
	public HasSSCCReferralRes hasActiveSSCCReferral(HasSSCCReferralReq hasSSCCReferralReq) {
		HasSSCCReferralRes hasSSCCReferralRes = new HasSSCCReferralRes();
		hasSSCCReferralRes.setIndCaseHasSSCCRef(ssccRefDao.hasActiveSSCCReferral(hasSSCCReferralReq.getCaseId()));
		return hasSSCCReferralRes;
	}

	/**
	 * Returns true if there is at least one SSCC Referral for given stage.
	 *
	 * @param idUser
	 *            the id user
	 * @return boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean isUserSSCC(Long idUser) {
		return ssccRefDao.isUserSSCCExternal(idUser, ServiceConstants.EMPTY_STR);
	}

	/**
	 * Gets all active SSCC Referral for given stage.
	 *
	 * @param idStage
	 *            the id stage
	 * @param strReferralType
	 *            the str referral type
	 * @return List<SSCCRefDto> all active SSCC Referrals
	 */
	@Override
	public List<SSCCRefDto> GetActiveSSCCReferral(Long idStage, String strReferralType) {
		List<SSCCRefDto> sSCCRefList = null;
		sSCCRefList = ssccRefDao.fetchActiveSSCCReferralsForStage(idStage, strReferralType);
		return sSCCRefList;
	}

	/**
	 * Method Name: fetchSSCCReferralListForCase Method Description:Fetches the
	 * list of SSCC Referrals for the case and sets it into the
	 * SSCCRefListValueBean.
	 *
	 * @param ssccRefListDto
	 *            the sscc ref list dto
	 * @return SSCCRefListDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCRefListDto fetchSSCCReferralListForCase(SSCCRefListDto ssccRefListDto) {
		SSCCRefListDto ssccRefListDtoRet = new SSCCRefListDto();
		ssccRefListDtoRet = ssccRefDao.fetchReferralsForCase(ssccRefListDto);
		ssccRefListDtoRet = ssccReferralListSummaryStatus(ssccRefListDto);
		ssccRefDao.deleteNewSSCCReferral(ssccRefListDto.getIdCase());
		return ssccRefListDtoRet;
	}

	/* Services used in SSCC Referral Detail page */
	/**
	 * Method Name: updateSSCCReferralDetail Method Description:Fetches the list
	 * of SSCC Referrals for the case and sets it into the SSCCRefListValueBean.
	 *
	 * @param ssccReferralDto
	 *            the sscc referral dto
	 * @param userId
	 *            the user id
	 * @return SSCCRefListDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateSSCCReferralDetail(SSCCRefDto ssccReferralDto, String userId) {
		ssccRefDao.updateSSCCRefHeader(ssccReferralDto);
		// If the Referral is being Rescinded
		if (CodesConstant.CSSCCSTA_70.equals(ssccReferralDto.getCdRefStatus())) {
			if (CodesConstant.CSSCCREF_10.equals(ssccReferralDto.getCdSSCCRefType())
					|| CodesConstant.CSSCCREF_20.equals(ssccReferralDto.getCdSSCCRefType())) {
				// when a placement referral is set to Rescind, also rescind the
				// corresponding Placement Option, Plcmt Circumstance, SSCC
				// Child Plan Topic and SSCC Service Auth Requests not Approved
				// or Rejected.
				ssccRefDao.updatePlcmtHeaderStatus(CodesConstant.CSSCCSTA_70, ssccReferralDto.getIdSSCCReferral());
				ssccRefDao.updateSSCCChildPlanTopicStatus(CodesConstant.CSSCCSTA_140,
						ssccReferralDto.getIdSSCCReferral());

				ssccRefDao.updateSSCCChildPlanStatus(CodesConstant.CSSCCSTA_140, ssccReferralDto.getIdSSCCReferral());
				ssccRefDao.updateSSCCServiceAuth(CodesConstant.CSSCCSTA_70, ssccReferralDto.getIdSSCCReferral());
			} else if (CodesConstant.CSSCCREF_30.equals(ssccReferralDto.getCdSSCCRefType())) {
				// when a Family service referral is set to Rescind, also
				// rescind the corresponding Service Auth Requests that have not
				// been Approved or Rejected
				ssccRefDao.updateSSCCServiceAuth(CodesConstant.CSSCCSTA_70, ssccReferralDto.getIdSSCCReferral());
			}
			// Un-assign all SSCC Secondary Staff when Referral is being
			// Rescinded
			ssccRefDao.unAssignAllSSCCSecondaryStaff(ssccReferralDto);
		} else if (CodesConstant.CSSCCSTA_130.equals(ssccReferralDto.getCdRefStatus())) {
			// When Referral is being Discharged un-assign all SSCC Secondary
			// Staff from stage
			ssccRefDao.unAssignAllSSCCSecondaryStaff(ssccReferralDto);
		}
		if (!ObjectUtils.isEmpty(ssccReferralDto.getSsccTimelineDtoList())
				&& !ssccReferralDto.getSsccTimelineDtoList().isEmpty()) {
			ssccReferralDto.getSsccTimelineDtoList()
					.forEach(ssccTimelineDto -> ssccRefDao.saveSSCCTimeline(ssccTimelineDto));
		}
		//Changes for UC12.8. For Family Referral, when the end referral is finalized, any active persons on the referral
		//should be end dated, so that the batch can pick them up and end Svc Auths.

		if (CodesConstant.CSSCCREF_30.equals(ssccReferralDto.getCdSSCCRefType())) {   //Family Referral
			if (CodesConstant.CSSCCSTA_130.equals(ssccReferralDto.getCdRefStatus())) {   //Discharged

				ssccReferralDto.getSsccRefFamilyDtoList()
						.forEach(ssccRefFamilyDto -> {
							if (ObjectUtils.isEmpty(ssccRefFamilyDto.getDtEnd())) {
								ssccRefFamilyDto.setDtEnd(ssccReferralDto.getDtDischargeActual());
								ssccRefDao.updateSSCCRefFamily(ssccRefFamilyDto, userId);
							}
						});


			}

		}

	}

	/**
	 * Method name: fetchSSCCTimelineListForStage Method description: fetches
	 * the list of Timeline records for a stage.
	 *
	 * @param idStage
	 *            the id stage
	 * @param idSSCCReferral
	 *            the id SSCC referral
	 * @return the list
	 */
	private List<SSCCTimelineDto> fetchSSCCTimelineListForStage(Long idStage, Long idSSCCReferral) {
		SSCCTimelineDto ssccTimelineDto = new SSCCTimelineDto();
		ssccTimelineDto.setIdStage(idStage);
		ssccTimelineDto.setIdSsccReferral(idSSCCReferral);
		ssccTimelineDto.setIdReference(idSSCCReferral);
		ssccTimelineDto.setCdTimelineTableName(CodesConstant.CSSCCTBL_10);
		// Calls the service to fetch timeline list
		List<SSCCTimelineDto> ssccTimelineDtolist = ssccTimelineDao.getSSCCTimelineList(ssccTimelineDto);
		return ssccTimelineDtolist;
	}

	/**
	 * Method Name: fetchSSCCRefHeaderDataForNewReferral Method Description:
	 * Method fetches the SSCC Header information for a new SSCC Referral 1. The
	 * SSCC catchment region for the case 2. Populates the reference stage
	 * options lists (for Reference Stage drop down)
	 *
	 * @param ssccReferralReq
	 *            the sscc referral req
	 * @return the SSCC referral res
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCReferralRes fetchSSCCRefHeaderDataForNewReferral(SSCCReferralReq ssccReferralReq) {
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();
		// Calls the service which comes up with drop down values for new
		// referral.
		SSCCRefDto ssccRefDto = ssccRefUtil.fetchSSCCRefHeaderDataForNewReferral(ssccReferralReq.getSsccRefDto(),
				ssccReferralReq.getUserId());
		ssccRefDto.setNmCase(capsCaseDao.getCapsCaseByid(ssccRefDto.getIdCase()).getNmCase());
		ssccRefDto.setDtPriorDischargeActual(ssccRefDao.fetchPriorDischargeActualDt(ssccRefDto.getIdStage()));
		ssccReferralRes.setSsccRefDto(ssccRefDto);
		return ssccReferralRes;
	}

	/**
	 * Method Name: fetchReferralById Method Description: Fetches the Referral
	 * information for idSSCCReferral legacy name: readReferralByPK.
	 *
	 * @param idSSCCReferral
	 *            the id SSCC referral
	 * @param userId
	 *            the user id
	 * @param isUserFixer
	 *            the is user fixer
	 * @return the SSCC referral res
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCReferralRes fetchReferralById(Long idSSCCReferral, String userId, boolean isUserFixer) {
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();
		// Invoke the util method to fetch the SSCC Referral using the Primary
		// key
		SSCCRefDto ssccRefDto = ssccRefUtil.readSSCCRefByPK(idSSCCReferral);
		SSCCResourceDto ssccResourceDto = null;
		if (!ObjectUtils.isEmpty(ssccRefDto.getCdCntrctRegion())) {
			ssccResourceDto = ssccRefDao.fetchSSCCResourceInfo(ssccRefDto.getCdCntrctRegion(), ssccRefDto.getIdSSCCCatchment());
		}

		//Setting indicator for SSCC is in Stage I or Stage II
		ssccRefDto.setIndSSCCStageII(getSSCCStageIndicator(ssccResourceDto));


		ssccRefDto.setNmCase(capsCaseDao.getCapsCaseByid(ssccRefDto.getIdCase()).getNmCase());
		boolean isSSCCUser = ssccRefDao.isUserSSCCExternal(Long.valueOf(userId), ssccRefDto.getCdCntrctRegion());
		ssccRefDto.setIndUserSSCC(isSSCCUser);
		ssccRefDto.setIndDisplayTimelineSection(Boolean.TRUE);
		// The 'Save' button should not be displayed
		ssccRefDto.setIndDisplaySaveButton(Boolean.FALSE);
		// When Referral is in 'NEW' mode
		if (CodesConstant.CSSCCSTA_10.equals(ssccRefDto.getCdRefStatus())) {
			// Call setFlagsForNewReferral()
			setFlagsForNewReferral(ssccRefDto, userId, isSSCCUser, isUserFixer);
		} else if (CodesConstant.CSSCCSTA_40.equals(ssccRefDto.getCdRefStatus())
				|| CodesConstant.CSSCCSTA_50.equals(ssccRefDto.getCdRefStatus())) {
			// Call setFlagsForRefActiveStatus()
			setFlagsForRefActiveStatus(ssccRefDto, userId, isSSCCUser, isUserFixer);
		} else if (CodesConstant.CSSCCSTA_130.equals(ssccRefDto.getCdRefStatus())) {
			// Call setFlagsForRefDischargedStatus()
			setFlagsForRefDischargedStatus(ssccRefDto);
		} else if (CodesConstant.CSSCCSTA_70.equals(ssccRefDto.getCdRefStatus())) {
			// Call setFlagsForRefRescindStatus()
			setFlagsForRefRescindStatus(ssccRefDto);
		}
		// Set the display flags for Rescind Section
		if (!ObjectUtils.isEmpty(ssccRefDto.getCdRescindReason())
				|| !ObjectUtils.isEmpty(ssccRefDto.getTxtRescindComment()))
			ssccRefDto.setIndDisplayRefRescindSectionDiv(Boolean.TRUE);

		// Set the display flag for the Discharge Section
		if (!ObjectUtils.isEmpty(ssccRefDto.getCdDischargeReason()))
			ssccRefDto.setIndDisplayDischargeSectionDiv(Boolean.TRUE);

		// Set the display flag for DtRecordedDFPS
		if (ServiceConstants.YES.equals(ssccRefDto.getIndPriorComm()))
			ssccRefDto.setIndDisplayDtRecordedDFPSDiv(Boolean.TRUE);

		if (CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
			// The Date Expected Placement must be protected when Referral Type
			// is 'Family Service Referral'
			ssccRefDto.setIndProtectDtExpectedPlcmt(Boolean.TRUE);
			if (!CodesConstant.CSSCCSTA_10.equals(ssccRefDto.getCdRefStatus())) {
				if (CodesConstant.CSSCCSTA_40.equals(ssccRefDto.getCdRefStatus()))
					ssccRefDto.setIndDisplayFamilySectionEditMode(Boolean.TRUE);

				ssccRefDto.setIndDisplaySSCCRefFamilySection(Boolean.TRUE);

				//UC12.7/12.8 - commented the check for SSCC User in order to show Add/Remove buttons for DFPS and SSCC users.
				//if (!isSSCCUser && ssccRefDto.getIndDisplayFamilySectionEditMode())
				if(ssccRefDto.getIndDisplayFamilySectionEditMode()
						&& !ssccRefDto.getIndDisplayDischargeFinalButton())
					ssccRefDto.setIndDisplayFamilyRefButtons(Boolean.TRUE);

				// Fetch the list of Person's for Family Service Referral
				// Section.
				ArrayList<SSCCRefFamilyDto> personList = ssccRefDao.fetchSSCCReferralFamilyPersonList(idSSCCReferral);
				// While Family Section is in Edit mode, if user is SSCC or if
				// Indicator SVC Auth is true disable Radio button
				// stream over the person list and set the indicators inside the
				// list and update the list.
				personList.stream().filter(o -> (o.getIndSvcAuth() || isSSCCUser))
						.peek(o -> o.setIndDisableRadioButton(Boolean.TRUE))
						.filter(o -> (CodesConstant.CSSCCSTA_130.equals(ssccRefDto.getCdRefStatus())
								|| CodesConstant.CSSCCSTA_70.equals(ssccRefDto.getCdRefStatus())))
						.peek(o -> o.setIndDisableCheckbox(Boolean.TRUE));
				ssccRefDto.setSsccRefFamilyDtoList(personList);
			}
		}

		if (CodesConstant.CSSCCREF_10.equals(ssccRefDto.getCdSSCCRefType())
				|| CodesConstant.CSSCCREF_20.equals(ssccRefDto.getCdSSCCRefType())) {
			if (CodesConstant.CSSCCSTA_10.equals(ssccRefDto.getCdRefStatus())) {
				// For 'NEW' Placement type of Referral, fetch the SSCC
				// Placement Information
				ssccRefUtil.fetchSSCCReferralPlcmtInfo(ssccRefDto);
			} else {
				// Fetch Placement Information data for active referrals (fetch
				// information from sscc_referral_event)
				ssccRefUtil.fetchSSCCRefPlcmtInfoforActiveRef(ssccRefDto);
			}
			ssccRefDto.setIndDisplaySSCCRefPlcmtSection(Boolean.TRUE);
		}

		// When stage is checked out to MPS then users cannot perform any
		// Discharge related actions in IMPACT
		if (caseUtils.getCaseCheckoutStatus(ssccRefDto.getIdStage())) {
			ssccRefDto.setIndDisplayDischargeRejectButton(Boolean.FALSE);
			ssccRefDto.setIndDisplayDischargeRescindButton(Boolean.FALSE);
			ssccRefDto.setIndDisplayDischargeAckdgeButton(Boolean.FALSE);
			ssccRefDto.setIndDisplayDischargeButton(Boolean.FALSE);
			ssccRefDto.setIndDisplayDischargeActualDiv(Boolean.FALSE);
			ssccRefDto.setIndDisplayDischargeFinalButton(Boolean.FALSE);
		}
		// Set flag to display the Identified section and set flags to protect
		// the header fields
		ssccRefDto.setIndDisplayIdentifierSection(Boolean.TRUE);
		ssccRefDto.setIndProtectRefType(Boolean.TRUE);
		ssccRefDto.setIndProtectRefSubType(Boolean.TRUE);
		ssccRefDto.setIndProtectStage(Boolean.TRUE);
		ssccRefDto.setIndProtectIndPrioComm(Boolean.TRUE);
		ssccRefDto.setIndProtectDtPriorCommDFPS(Boolean.TRUE);
		if (!ObjectUtils.isEmpty(ssccRefDto.getDtRecordedSscc()))
			ssccRefDto.setIndProtectDtPriorCommSSCC(Boolean.TRUE);
		// Check if user is SSCC Fixer and set the indicator in the value bean
		if (isUserFixer) {//removed !isSSCCUser as SSCC can also be a fixer for 3B transition
			ssccRefDto.setIndUserSSCCFixer(Boolean.TRUE);
			setFlagsForDFPSFixer(ssccRefDto, userId);
		}
		// If Stage Assignment Staff 1 is not protected then display the
		// UpdateNotify button
		if (!ssccRefDto.getIndProtectSSCCStaff1() && !ssccRefDto.getIndDisplaySaveTransmitButton()
				&& !ssccRefDto.getIndDisplayDischargeAckdgeButton()) {
			ssccRefDto.setIndDisplayUpdateAndNotifyButton(Boolean.TRUE);
		}
		if (CodesConstant.CSSCCSTA_10.equals(ssccRefDto.getCdRefStatus())
				&& CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
			List<SSCCRefFamilyDto> ssccRefFamilyDtoList = new ArrayList<>();
			List<PersonListDto> personListByStage = personDao.getPersonListByIdStage(ssccRefDto.getIdStage(),
					ServiceConstants.STAFF_TYPE);
			personListByStage.forEach(o -> {
				// PD 77127: added to not display COL type persons for Family Referral
				if (!ServiceConstants.COLLATERAL.equalsIgnoreCase(o.getStagePersType())){
				SSCCRefFamilyDto ssccRefFamilyDto = new SSCCRefFamilyDto();
				ssccRefFamilyDto.setNmPersonFull(o.getPersonFull());
				ssccRefFamilyDto.setCdRelInt(o.getStagePersRelInt());
				ssccRefFamilyDto.setIdPerson(o.getIdPerson());
				if (!ObjectUtils.isEmpty(o.getDtPersonBirth())) {
					ssccRefFamilyDto.setAge(DateUtils.getAge(o.getDtPersonBirth()));
				}
				ssccRefFamilyDtoList.add(ssccRefFamilyDto);
				}
			});
			ssccRefDto.setSsccRefFamilyDtoList(ssccRefFamilyDtoList);
		}
		ssccRefDto.setSsccTimelineDtoList(fetchSSCCTimelineListForStage(ssccRefDto.getIdStage(), idSSCCReferral));
		ssccRefDto.setDtPriorDischargeActual(ssccRefDao.fetchPriorDischargeActualDt(ssccRefDto.getIdStage()));
		ssccRefDto.setDtResourceStart(ssccRefDao.fetchSSCCResourceDtStart(ssccRefDto.getIdSSCCResource(),
				ssccRefDto.getIdSSCCCatchment(), ssccRefDto.getCdCntrctRegion(), ssccRefDto.getCdSSCCRefType()));

		if (CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType()) ){
			String cdStage = caseUtilityRel.getStage(ssccRefDto.getIdStage()).getCdStage();
			List<SelectStageDto> allOpenStageList=stageDao.getOpenStages(ssccRefDto.getIdCase(),"N");
			Map<Long, String> allOpenStageMap = allOpenStageList.stream().filter(p->!(p.getIdStage().equals(ssccRefDto.getIdStage())) && (p.getCdStage().equals(CodesConstant.CSTAGES_FSU)||p.getCdStage().equals(CodesConstant.CSTAGES_FRE)||p.getCdStage().equals(CodesConstant.CSTAGES_SUB))).collect(
					Collectors.toMap(q->q.getIdStage(),q->q.getNmStage().concat(" - ").concat(q.getCdStage())));
			allOpenStageMap.put(999999999l,"Case Closure, Do Not Transfer Referral");
			TreeMap<Long, String> allOpenStageTreeMap =new TreeMap<>(allOpenStageMap);
			ssccRefDto.setValidTransferToStageList(allOpenStageTreeMap);
		}
		ssccReferralRes.setSsccRefDto(ssccRefDto);
		return ssccReferralRes;
	}

	/**
	 * Method Name: setFlagsForNewReferral Method Description: Method sets the
	 * display flags in dto object when Referral status is NEW.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param userId
	 *            the user id
	 * @param isSSCCUser
	 *            the is SSCC user
	 * @param isUserFixer
	 *            the is user fixer
	 */
	private void setFlagsForNewReferral(SSCCRefDto ssccRefDto, String userId, boolean isSSCCUser, boolean isUserFixer) {
		// If user is DFPS and status is 'NEW' display the 'Save and Transmit'
		// button
		ssccRefDto.setIndDisplaySaveTransmitButton(Boolean.TRUE);
		// The Delete button should be displayed only when Referral is in 'NEW'
		// status
		ssccRefDto.setIndDisplayDeleteButton(Boolean.TRUE);
		// When Referral is in 'NEW' status the timeline section will not be
		// displayed
		ssccRefDto.setIndDisplayTimelineSection(Boolean.FALSE);
		// Staff Assignment is protected when Referral is Active unless the user
		// is assigned to stage or Supervisor of assigned staff or SSCC Fixer
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setIdUser(Long.valueOf(userId));
		if ((isUserFixer) || caseUtils.hasStageAccess(ssccRefDto.getIdStage(), userProfileDto)) {//removed !isSSCCUser as SSCC can also be a fixer for 3B transition
			ssccRefDto.setIndProtectSSCCStaff1(Boolean.FALSE);
			List<AssignmentGroupDto> dfpsSecondary = ssccRefDto.getDfpsStaffAssign().values().stream()
					.filter(o -> CodesConstant.CSTFROLS_SE.equals(o.getCdStagePersRole())).collect(Collectors.toList());
			ssccRefDto.setIndProtectSSCCStaff2(
					(dfpsSecondary.size() > 2 && ObjectUtils.isEmpty(ssccRefDto.getIdSSCCStaff2())) ? Boolean.TRUE
							: Boolean.FALSE);
		} else {
			ssccRefDto.setIndProtectSSCCStaff1(Boolean.TRUE);
			ssccRefDto.setIndProtectSSCCStaff2(Boolean.TRUE);
		}
		// For Family Service Type Referrals
		if (CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
			// Set display flag for Family Service Referral Section as true
			ssccRefDto.setIndDisplaySSCCRefFamilySection(Boolean.TRUE);
		}
	}

	/**
	 * Method Name: setFlagsForRefActiveStatus Method Description: Method sets
	 * the display flags in value bean object when Referral status is Active.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param userId
	 *            the user id
	 * @param isSSCCUser
	 *            the is SSCC user
	 * @param isUserFixer
	 *            the is user fixer
	 */
	private void setFlagsForRefActiveStatus(SSCCRefDto ssccRefDto, String userId, boolean isSSCCUser,
											boolean isUserFixer) {
		// Staff Assignment is protected when Referral is Active unless the user
		// is assigned to stage or Supervisor of assigned staff or SSCC Fixer
		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setIdUser(Long.valueOf(userId));
		if ((isUserFixer) || caseUtils.hasStageAccess(ssccRefDto.getIdStage(), userProfileDto)) {//removed !isSSCCUser as SSCC can also be a fixer for 3B transition
			ssccRefDto.setIndProtectSSCCStaff1(Boolean.FALSE);
			List<AssignmentGroupDto> dfpsSecondary = ssccRefDto.getDfpsStaffAssign().values().stream()
					.filter(o -> CodesConstant.CSTFROLS_SE.equals(o.getCdStagePersRole())).collect(Collectors.toList());
			ssccRefDto.setIndProtectSSCCStaff2(
					(dfpsSecondary.size() > 2 && ObjectUtils.isEmpty(ssccRefDto.getIdSSCCStaff2())) ? Boolean.TRUE
							: Boolean.FALSE);
		} else {
			ssccRefDto.setIndProtectSSCCStaff1(Boolean.TRUE);
			ssccRefDto.setIndProtectSSCCStaff2(Boolean.TRUE);
		}
		// Display logic for Update and Notify Button for DFPS
		if ( !ssccRefDto.getIndDisplayDischargeFinalButton()) {
			ssccRefDto.setIndDisplayUpdateAndNotifyButton(Boolean.TRUE);
		}
		// Logic to Display the Discharge Button for Placement Referrals
		// Rules: 1. When there is a Linked placement or Service auth for this
		// referral
		// Rules: 2 AND Referral sub status is NULL (which means not discharge
		// initiated yet )
		if ( ObjectUtils.isEmpty(ssccRefDto.getCdRefSubStatus())
				&& !CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {

			ssccRefDto.setIndDisplayDischargeButton(Boolean.TRUE);
			// Indicator Discharge Ready For Review should always be set to
			// true. In the event, user clicks on Discharge button
			// the Discharge fields get displayed and this indicator will be
			// always true and protected
			ssccRefDto.setIndDischargeReadyReview(ServiceConstants.YES);
		} else if (ServiceConstants.YES.equals(ssccRefDto.getIndLinkedSvcAuthData())
				&& ObjectUtils.isEmpty(ssccRefDto.getCdRefSubStatus())
				&& CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
			// Logic to Display the Discharge Button for Family Referrals
			// Rules: 1. When there is a Linked Service auth for this referral
			// Rules: 2 AND Referral sub status is NULL (which means not
			// discharge initiated yet )
			ssccRefDto.setIndDisplayDischargeButton(Boolean.TRUE);
			// Indicator Discharge Ready For Review should always be set to
			// true. In the event, user clicks on Discharge button
			// the Discharge fields get displayed and this indicator will be
			// always true and protected
			ssccRefDto.setIndDischargeReadyReview(ServiceConstants.YES);
		}

		// Logic to display the DtRecordedSscc
		if (isSSCCUser && !ObjectUtils.isEmpty(ssccRefDto.getDtRecordedDfps()))
			ssccRefDto.setIndDisplayDtRecordedSSCCDiv(Boolean.TRUE);
		else if (!isSSCCUser && !ObjectUtils.isEmpty(ssccRefDto.getDtRecordedSscc()))
			ssccRefDto.setIndDisplayDtRecordedSSCCDiv(Boolean.TRUE);

		// Logic to display the 'Rescind' button
		if (!CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
			// If Placement Referral
			if ( (!ServiceConstants.YES.equals(ssccRefDto.getIndLinkedSvcAuthData())  //Removed the check for !isSSCCUser.
					&& !ServiceConstants.YES.equals(ssccRefDto.getIndLinkedPlcmtData())))
				ssccRefDto.setIndDisplayRescindButton(Boolean.FALSE);
		} else if (!ServiceConstants.YES.equals(ssccRefDto.getIndLinkedSvcAuthData())) {//Removed the check for !isSSCCUser.
			// If Family Service Referral
			ssccRefDto.setIndDisplayRescindButton(Boolean.TRUE);
		}
		// When the logged in user is a SSCC user
		if (isSSCCUser) {
			// SIR 1016673 - Disable Staff assignment fields when user is SSCC
			ssccRefDto.setIndProtectSSCCStaff1(Boolean.TRUE);
			ssccRefDto.setIndProtectSSCCStaff2(Boolean.TRUE);

			// Display the Acknowledge Referral button to the SSCC user only
			// when 1) User has not yet Acknowledged the Referral
			// 2) The Referral Discharge has not been initiated OR Referral not
			// ready for discharge
			if (!ServiceConstants.YES.equals(ssccRefDto.getIndRefAcknowledge())
					&& !ssccRefDto.getIndDisplayDischargeButton()
					&& ObjectUtils.isEmpty(ssccRefDto.getCdRefSubStatus()))
				ssccRefDto.setIndDisplayAcknwldgeButton(Boolean.TRUE);

		}

		if (!ObjectUtils.isEmpty(ssccRefDto.getCdRefSubStatus())) {
			if (CodesConstant.CSSCCSS_10.equals(ssccRefDto.getCdRefSubStatus())) {
				// If DFPS has initiated a Discharge Notification
				if (isSSCCUser) {
					// When DFPS initiates a Discharge Notification the
					// Acknowledge Discharge button must be available to SSCC
					// Added Rescind Discharge Button for SSCC
					// Added Finalize End Button for SSCC
					ssccRefDto.setIndDisplayDischargeAckdgeButton(true);
					ssccRefDto.setIndProtectCdRefDischargeReason(Boolean.FALSE);
					ssccRefDto.setIndProtectDtDischargePlanned(Boolean.TRUE);
					ssccRefDto.setIndDisplayDischargeRescindButton(Boolean.TRUE);
					ssccRefDto.setIndDisplayDischargeFinalButton(Boolean.TRUE);
					ssccRefDto.setIndDisplayDischargeActualDiv(Boolean.TRUE);
				} else {
					// The 'Rescind Discharge' and 'Finalize Discharge' buttons
					// must be available to DFPS
					ssccRefDto.setIndDisplayDischargeRescindButton(Boolean.TRUE);
					// with the removal of Rescind End Referral button hiding End Referral button
					ssccRefDto.setIndDisplayDischargeButton(Boolean.FALSE);
				}

			} else if (CodesConstant.CSSCCSS_20.equals(ssccRefDto.getCdRefSubStatus())) {
				// If SSCC has initiated a Discharge Request
				if (!isSSCCUser) {
					// When SSCC Initiates a Discharge Request then display
					// Acknolwdge Discharge button if user is DFPS
					// Changed from Reject Discharge to Rescind Discharge
					ssccRefDto.setIndDisplayDischargeAckdgeButton(Boolean.TRUE);
					ssccRefDto.setIndDisplayDischargeRescindButton(Boolean.TRUE);
					ssccRefDto.setIndDisplayUpdateAndNotifyButton(Boolean.TRUE);
				} else {
					// If user is SSCC then display the Rescind Discharge button
					// Added Finalize End Referral Button
					ssccRefDto.setIndDisplayDischargeRescindButton(Boolean.TRUE);
					ssccRefDto.setIndDisplayDischargeFinalButton(Boolean.TRUE);
					ssccRefDto.setIndDisplayDischargeActualDiv(Boolean.TRUE);
					ssccRefDto.setIndDisplayUpdateAndNotifyButton(Boolean.TRUE);

				}
			} else if (CodesConstant.CSSCCSS_30.equals(ssccRefDto.getCdRefSubStatus())) {
				if (isSSCCUser) {
					// SSCC has already Acknowledged the Discharge Notification.
					// Protect Fields and display Rescind Discharge Button
					ssccRefDto.setIndProtectCdRefDischargeReason(Boolean.FALSE);
					ssccRefDto.setIndProtectDtDischargePlanned(Boolean.TRUE);
					ssccRefDto.setIndDisplayDischargeRescindButton(Boolean.TRUE);
					ssccRefDto.setIndDisplayDischargeFinalButton(Boolean.TRUE);
					ssccRefDto.setIndDisplayDischargeActualDiv(Boolean.TRUE);
				} else {
					// The 'Rescind Discharge' and 'Finalize Discharge' buttons
					// must be available to DFPS
					ssccRefDto.setIndDisplayDischargeRescindButton(Boolean.TRUE);
				}
			} else if (CodesConstant.CSSCCSS_40.equals(ssccRefDto.getCdRefSubStatus())) {
				if (isSSCCUser) {
					// DFPS has already Acknowledged the Discharge Request.
					// Protect Fields and display Rescind Discharge Button
					// ssccRefDto.setIndProtectCdRefDischargeReason(
					// ArchitectureConstants.TRUE );
					// ssccRefDto.setIndProtectDtDischargePlanned(
					// ArchitectureConstants.TRUE );
					ssccRefDto.setIndDisplayDischargeRescindButton(Boolean.TRUE);
					ssccRefDto.setIndDisplayUpdateAndNotifyButton(Boolean.TRUE);
					ssccRefDto.setIndDisplayDischargeFinalButton(Boolean.TRUE);
					ssccRefDto.setIndDisplayDischargeActualDiv(Boolean.TRUE);

				} else {
					// The 'Rescind Discharge' and 'Finalize Discharge' buttons
					// must be available to DFPS
					ssccRefDto.setIndDisplayDischargeRescindButton(Boolean.TRUE);
				}
			}

			if (!isSSCCUser) {
				// If Sub Status is not null (i.e. Either a Discharge
				// Request/Notification has been initiated and/or Acknowleged)
				// And user is DFPS then display the Finalize Discharge button
				ssccRefDto.setIndDisplayDischargeActualDiv(Boolean.TRUE);
				ssccRefDto.setIndDisplayDischargeFinalButton(Boolean.TRUE);
				ssccRefDto.setIndDisplayUpdateAndNotifyButton(Boolean.TRUE);
			}
			// Code changes added for UC 12 CBC P3 changes
			// If referral is Family Referral AND
			// User is NOT a Fixer
			// -> Disable 'Actual End Referral Date' field and default to current date
			if(CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())
					&& !isUserFixer) {
				ssccRefDto.setIndProtectDtDischargeActual(true);
				ssccRefDto.setDtDischargeActual(new Date());
			}
		}
	}

	/**
	 * Method Name: setFlagsForRefDischargedStatus Method Description: Method
	 * sets the display flags in value bean object when Referral status is
	 * Discharged.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 */
	private void setFlagsForRefDischargedStatus(SSCCRefDto ssccRefDto) {
		ssccRefDto.setIndDisplayDischargeActualDiv(Boolean.TRUE);
		ssccRefDto.setIndProtectCdRefDischargeReason(Boolean.FALSE);
		ssccRefDto.setIndProtectDtDischargeActual(Boolean.TRUE);
		ssccRefDto.setIndProtectDtDischargePlanned(Boolean.TRUE);
		ssccRefDto.setIndProtectDtExpectedPlcmt(Boolean.TRUE);
		ssccRefDto.setIndProtectSSCCStaff1(Boolean.TRUE);
		ssccRefDto.setIndProtectSSCCStaff2(Boolean.TRUE);
		ssccRefDto.setIndDisplayUpdateAndNotifyButton(Boolean.FALSE);
		ssccRefDto.setIndDisplayFamilyRefButtons(Boolean.FALSE);
		ssccRefDto.setIndDisplayFamilySectionEditMode(Boolean.FALSE);
	}

	/**
	 * Method Name: setFlagsForRefRescindStatus Method description: Method sets
	 * the display flags in value bean object when Referral status is Rescind.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 */
	private void setFlagsForRefRescindStatus(SSCCRefDto ssccRefDto) {
		ssccRefDto.setIndProtectRescindComment(Boolean.TRUE);
		ssccRefDto.setIndProtectRescindReason(Boolean.TRUE);
		ssccRefDto.setIndProtectDtExpectedPlcmt(Boolean.TRUE);
		ssccRefDto.setIndProtectSSCCStaff1(Boolean.TRUE);
		ssccRefDto.setIndProtectSSCCStaff2(Boolean.TRUE);
		ssccRefDto.setIndDisplayUpdateAndNotifyButton(Boolean.FALSE);
		ssccRefDto.setIndDisplayFamilyRefButtons(Boolean.FALSE);
		ssccRefDto.setIndDisplayFamilySectionEditMode(Boolean.FALSE);
	}

	/**
	 * Method Name: setFlagsForDFPSFixer Method Description: sets the display
	 * flags in value bean object when Referral status is Rescind.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param userId
	 *            the user id
	 */
	private void setFlagsForDFPSFixer(SSCCRefDto ssccRefDto, String userId) {
		if (CodesConstant.CSSCCSTA_40.equals(ssccRefDto.getCdRefStatus())) {
			if (!CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
				ssccRefDto.setIndProtectRefType(Boolean.FALSE);
				ssccRefDto.setIndProtectRefSubType(Boolean.FALSE);
			}
			ssccRefDto.setIndProtectDtPriorCommDFPS(Boolean.FALSE);
			ssccRefDto.setIndProtectDtPriorCommSSCC(Boolean.FALSE);
			if (ServiceConstants.YES.equals(ssccRefDto.getIndPriorComm()))
				ssccRefDto.setIndDisplayDtRecordedSSCCDiv(Boolean.TRUE);

			ssccRefDto.setIndProtectIndPrioComm(Boolean.FALSE);
			ssccRefDto.setIndProtectDtExpectedPlcmt(Boolean.FALSE);
			ssccRefDto.setIndDisplayUpdateAndNotifyButton(Boolean.TRUE);
			UserProfileDto userProfileDto = new UserProfileDto();
			userProfileDto.setIdUser(Long.valueOf(userId));
			if (!caseUtils.hasStageAccess(ssccRefDto.getIdStage(), userProfileDto)) {
				// When user is a Fixer but is not case assigned the following
				// buttons should
				// not be available
				ssccRefDto.setIndDisplayAcknwldgeButton(Boolean.FALSE);
				ssccRefDto.setIndDisplayDischargeAckdgeButton(Boolean.FALSE);
				ssccRefDto.setIndDisplayDischargeButton(Boolean.FALSE);
				ssccRefDto.setIndDisplayDischargeFinalButton(Boolean.FALSE);
				ssccRefDto.setIndDisplayDischargeRejectButton(Boolean.FALSE);
				ssccRefDto.setIndDisplayDischargeRescindButton(Boolean.FALSE);
				ssccRefDto.setIndDisplayRescindButton(Boolean.FALSE);
				ssccRefDto.setIndDisplayFamilyRefButtons(Boolean.FALSE);

				ssccRefDto.setIndProtectCdRefDischargeReason(Boolean.TRUE);
				ssccRefDto.setIndProtectDtDischargePlanned(Boolean.TRUE);
				ssccRefDto.setIndProtectDtDischargeActual(Boolean.TRUE);
				ssccRefDto.setIndProtectDtDischargeActual(Boolean.TRUE);

				ssccRefDto.setIndProtectRescindReason(Boolean.TRUE);
				ssccRefDto.setIndProtectRescindComment(Boolean.TRUE);

			}
			ssccRefDto.setIndDisplayDeleteFixerButton(Boolean.TRUE);
		} else if (CodesConstant.CSSCCSTA_130.equals(ssccRefDto.getCdRefStatus())) {
			// If Referral is discharged and user is Fixer display the Undo
			// Discharge Button
			ssccRefDto.setIndDisplayDischargeUndoButton(Boolean.TRUE);
			ssccRefDto.setIndDisplayDeleteFixerButton(Boolean.TRUE);
		}
		// SSCC Fixer can Delete any referral that is Active or Rescinded
		else if (CodesConstant.CSSCCSTA_70.equals(ssccRefDto.getCdRefStatus())) {
			ssccRefDto.setIndDisplayDeleteFixerButton(Boolean.TRUE);
		}
	}

	/**
	 * Method Name: saveSSCCReferralHeader Method Description: This method helps
	 * to add the SSCC referral Header information.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @return the SSCC referral res
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCReferralRes saveSSCCReferralHeader(SSCCRefDto ssccRefDto) {
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();
		Set<Long> idSSCCReferralList = new HashSet<>();
		if (!ObjectUtils.isEmpty(ssccRefDto.getSsccResourceDto().getIdSSCCCatchment())
				&& ObjectUtils.isEmpty(ssccRefDto.getIdSSCCCatchment())) {
			ssccRefDto.setIdSSCCCatchment(ssccRefDto.getSsccResourceDto().getIdSSCCCatchment());
		}
		for (Long idStage : ssccRefDto.getIdStages()) {
			Long ulIdPerson = ssccRefDto.getIdCreatedPerson();
			// if the referral type is not family referral.
			if (!CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
				ssccRefDto.setIdPerson(stageUtilityDao.findPrimaryChildForStage(idStage));
			}
			// gets the resource start date.
			Date resourceStartDate = ssccRefDao.fetchSSCCResourceDtStart(
					ssccRefDto.getSsccResourceDto().getIdSSCCResource(),
					ssccRefDto.getSsccResourceDto().getIdSSCCCatchment(),
					ssccRefDto.getSsccResourceDto().getCdSSCCCntrctRegion(),
					ssccRefDto.getCdSSCCRefType());
			if (!ObjectUtils.isEmpty(resourceStartDate) && !ObjectUtils.isEmpty(ssccRefDto.getDtRecordedDfps())
					&& ssccRefDto.getDtRecordedDfps().before(resourceStartDate)) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(MSG_DT_NOT_PRIOR_SSCC_RES_START);
				ssccReferralRes.setErrorDto(errorDto);
			}
			if (CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
				//Defect 14093, artf146687 changes. Code to check for concurrent inserts.
				SSCCRefListDto sSCCRefListDto = new SSCCRefListDto();
				sSCCRefListDto.setIdCase(ssccRefDto.getIdCase());


				sSCCRefListDto = processDisplayLogicForAddButton(sSCCRefListDto, ulIdPerson);
				if (!sSCCRefListDto.isIndDisplayReferralAddButton()) {
					ErrorDto errorDto = new ErrorDto();
					errorDto.setErrorCode(MSG_FMLY_REF_UPDATED_CODE);
					errorDto.setErrorMsg(MSG_FMLY_REF_UPDATED_MSG);
					ssccReferralRes.setErrorDto(errorDto);
				}
			}
			if (ObjectUtils.isEmpty(ssccReferralRes.getErrorDto())) {
				ssccRefDto.setDtCreated(new Date());
				ssccRefDto.setIdStage(idStage);
				Long idSSCCRefId = ssccRefDao.updateSSCCRefHeader(ssccRefDto);
				ssccRefDto.setIdSSCCReferral(idSSCCRefId);
				if (!CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
					boolean isSSCCUser = ssccRefDao.isUserSSCCExternal(ulIdPerson, ssccRefDto.getCdCntrctRegion());
					ssccRefDto.setIndUserSSCC(isSSCCUser);
					ssccRefUtil.fetchSSCCReferralPlcmtInfo(ssccRefDto);
					populateSsccRefDtoforSave(ssccRefDto, ulIdPerson);
					SSCCListDto ssccListDto = populateSSCCListDtoForSaveAndTransmit(ssccRefDto, ulIdPerson);
					ssccReferralRes = saveAndTransmitSSCCReferral(ssccRefDto, ssccListDto, ulIdPerson.toString());
					idSSCCReferralList.add(ssccRefDto.getIdSSCCReferral());
					ssccRefDto.setIdSSCCReferral(null);
				} else {
					ssccReferralRes.setIdSSCCReferral(idSSCCRefId);
				}
			}
		}
		if(!ObjectUtils.isEmpty(ssccRefDto.getIndFamilyRefRequired()) && ssccRefDto.getIndFamilyRefRequired().equalsIgnoreCase(ServiceConstants.STRING_IND_Y)){
			ssccReferralRes =createFamilyReferral(ssccRefDto, ssccRefDto.getIdCreatedPerson(),ssccReferralRes);
		}
		ssccReferralRes.setIdSSCCReferralLst(idSSCCReferralList);
		return ssccReferralRes;
	}

	/**
	 * @param ssccRefDto
	 * @param idUser
	 * @param ssccReferralRes
	 * @return
	 */
	private SSCCReferralRes createFamilyReferral(SSCCRefDto ssccRefDto, Long idUser, SSCCReferralRes ssccReferralRes) {
		Long fsuOrFreStageId = 0L;
		List<StageDto> activeFSUStages = stageDao.getStagesByType(ssccRefDto.getIdCase(),ServiceConstants.OPEN_STAGES, CodesConstant.CSTAGES_FSU);
	  // PPM 91198:added to differentiate manual referral creation vs transfer referral
	   if(ObjectUtils.isEmpty(ssccRefDto.getTransferToStage())){
		if(ObjectUtils.isEmpty(activeFSUStages)){
			List<StageDto> activeFREStages = stageDao.getStagesByType(ssccRefDto.getIdCase(),ServiceConstants.OPEN_STAGES, CodesConstant.CSTAGES_FRE);
			fsuOrFreStageId = activeFREStages.get(0).getIdStage();
		}else{
			fsuOrFreStageId = activeFSUStages.get(0).getIdStage();
		}
	   }else{
			fsuOrFreStageId=ssccRefDto.getTransferToStage();

	   }
	    Long sourceStageId= ssccRefDto.getIdStage();
		ssccRefDto.setDtCreated(new Date());
		ssccRefDto.setCdSSCCRefType(CodesConstant.CSSCCREF_30);
		ssccRefDto.setIdStage(fsuOrFreStageId);
		ssccRefDto.setIdPerson(null);
		ssccRefDto.setCdSSCCRefSubtype(null);
		ssccRefDto.setDtExpectedPlcmt(null);
		ssccRefDto.getIdStages().add(fsuOrFreStageId);
		if(!ObjectUtils.isEmpty(ssccRefDto.getReferralTypeToResourceIdMap())) {
			Long familyResourceId = ssccRefDto.getReferralTypeToResourceIdMap().get(CodesConstant.CSSCCREF_30);
			ssccRefDto.setIdRsrcSSCC(familyResourceId);
		}
		Long idSSCCRefId = ssccRefDao.updateSSCCRefHeader(ssccRefDto);
		ssccRefDto.setIdSSCCReferral(idSSCCRefId);
		SSCCListDto ssccListDto = populateSSCCListDtoForSaveAndTransmit(ssccRefDto,idUser);
		List<SSCCRefFamilyDto> ssccRefFamilyDtoList = new ArrayList<>();
		List<StagePersonValueDto> personListByStage = personDao.getPersonDtlList(fsuOrFreStageId);
		// PPM 91198: added allPersonsExist for Transfer referral and ObjectUtils.isEmpty(ssccRefDto.getTransferToStage()) for adding manuall referral
		List<SSCCRefFamilyDto> nonExistingPersonsReferralList=null;
		if (!ObjectUtils.isEmpty(ssccRefDto.getTransferToStage())) {
			nonExistingPersonsReferralList =allpersonsNotExistsinTransferringStage(ssccRefDto.getSsccRefFamilyDtoList() ,personListByStage);
			personListByStage=allpersonsListExistsinTransferringStage(personListByStage, ssccRefDto.getSsccRefFamilyDtoList());
			if (!ObjectUtils.isEmpty(nonExistingPersonsReferralList) && nonExistingPersonsReferralList.size()>0) {
				nonExistingPersonsReferralList.forEach(sSCCRefFamilyDto-> ssccRefDao.insertIntoStagePersonLinkForTransfer(ssccRefDto,sSCCRefFamilyDto, sourceStageId));
				List<SSCCRefFamilyDto> newStagePersonValueList = populateNewPersons(nonExistingPersonsReferralList,idSSCCRefId);
				ssccRefFamilyDtoList.addAll(newStagePersonValueList);
			}
		 }
			personListByStage.forEach(o -> {
				SSCCRefFamilyDto ssccRefFamilyDto = new SSCCRefFamilyDto();
				ssccRefFamilyDto.setNmPersonFull(o.getNmPersonFull());
				ssccRefFamilyDto.setCdRelInt(o.getCdStagePersRelInt());
				ssccRefFamilyDto.setIdPerson(o.getIdPerson());
				if (!ObjectUtils.isEmpty(o.getDtPersonBirth())) {
					ssccRefFamilyDto.setAge(DateUtils.getAge(o.getDtPersonBirth()));
				}
				ssccRefFamilyDto.setIdSsccReferral(idSSCCRefId);
				ssccRefFamilyDto.setIndPersonAdded(true);
				ssccRefFamilyDtoList.add(ssccRefFamilyDto);
			});

		ssccRefDto.setSsccRefFamilyDtoList(ssccRefFamilyDtoList);
		ssccReferralRes = saveAndTransmitSSCCReferral(ssccRefDto,ssccListDto,idUser.toString());
		ssccReferralRes.setSsccRefDto(ssccRefDto);
		return ssccReferralRes;
	}

	/**
	 * @param ssccRefDto
	 * @param idUser
	 * @return
	 */
	private SSCCListDto populateSSCCListDtoForSaveAndTransmit(SSCCRefDto ssccRefDto, Long idUser) {
		SSCCListDto ssccListDto = new SSCCListDto();
		ssccListDto.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());
		ssccListDto.setDtCreated(new Date());
		ssccListDto.setDtLastUpdate(new Date());
		ssccListDto.setIdCreatedPerson(idUser);
		ssccListDto.setIdLastUpdatePerson(idUser);
		ssccListDto.setIndNew(ServiceConstants.YES);
		// If there is no valid legal status then set indicator legal status
		// missing to true
		if (!ObjectUtils.isEmpty(ssccRefDto.getSsccRefPlcmtDto())
				&& (ObjectUtils.isEmpty(ssccRefDto.getSsccRefPlcmtDto().getIdLegalStatusEvent())
				|| ssccRefDto.getSsccRefPlcmtDto().getIdLegalStatusEvent() == 0)) {
			ssccListDto.setIndLegalStatusMissing(ServiceConstants.YES);
		}
    // PPM 91198:added to  address dirty read caused in session
    // SSCCSA-302 - PD 91198
    if (!ObjectUtils.isEmpty(ssccRefDto.getTransferToStage())
        && !(ssccRefDto.getTransferToStage() == 999999999l)) {
      ssccRefDto.setCdRefStatus(CodesConstant.CSSCCSTA_40);
    }
		// For Child Placement Referrals if Referral Subtype is 'SA' (New
		// Removal)
		// then set CD_CHILD_PLAN_DUE = 10 (New Removal & SSCC Referral)
		// DT_CHILD_PLAN_DUE = Conservatorship Removal date + 40 Days
		if (!CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())
				&& CodesConstant.CSSCCSUB_SA.equals(ssccRefDto.getCdSSCCRefSubtype())) {
			ssccListDto.setCdChildPlanDue(CodesConstant.CSSCCCPP_10);
			if (!ObjectUtils.isEmpty(ssccRefDto.getSsccRefPlcmtDto().getDtCnsrvtrshpRmvl())) {
				ssccListDto.setDtChildPlanDue(ssccRefDto.getSsccRefPlcmtDto().getDtCnsrvtrshpRmvl());
			}
		} else if (!CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())
				&& !CodesConstant.CSSCCSUB_SA.equals(ssccRefDto.getCdSSCCRefSubtype())) {
			ssccListDto.setCdChildPlanDue(CodesConstant.CSSCCCPP_20);
			if (ServiceConstants.YES.equals(ssccRefDto.getIndPriorComm())) {
				ssccListDto.setDtChildPlanDue(ssccRefDto.getDtRecordedDfps());
			} else {
				ssccListDto.setDtChildPlanDue(ssccRefDto.getDtRecorded());
			}
		}
		// Update the DtFamilyMemeberUpdate on SSCC_LIST table when
		if (CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
			ssccListDto.setDtFamilyMemberUpdate(new Date());
		}
		return ssccListDto;
	}

	/**
	 * @param ssccRefDto
	 * @param idUser
	 */
	private void populateSsccRefDtoforSave(SSCCRefDto ssccRefDto, Long idUser) {
		ssccRefDto.setIdLastUpdatePerson(idUser);
		ssccRefDto.setCdRefStatus(CodesConstant.CSSCCSTA_40);
		if (CodesConstant.CSSCCREF_10.equals(ssccRefDto.getCdSSCCRefType())
				|| CodesConstant.CSSCCREF_20.equals(ssccRefDto.getCdSSCCRefType())) {
			SSCCRefPlcmtDto ssccRefPlcmtDto = ssccRefDto.getSsccRefPlcmtDto();
			ssccRefPlcmtDto.setDtCreated(new Date());
			ssccRefPlcmtDto.setDtLastUpdate(new Date());
			ssccRefPlcmtDto.setIdCreatedPerson(idUser);
			ssccRefPlcmtDto.setIdLastUpdatePerson(idUser);
			ssccRefPlcmtDto.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());
			ssccRefDto.setSsccRefPlcmtDto(ssccRefPlcmtDto);
		}
	}

	/**
	 * Method Name: saveAndTransmitSSCCReferral Method Description: This method
	 * updates all the sscc referral, sscc list, sscc timeline and sscc event
	 * tables.
	 * Also adds notification event for Interoperability
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param ssccListDto
	 *            the sscc list dto
	 * @param idUser
	 *            the id user
	 * @return the SSCC referral res
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCReferralRes saveAndTransmitSSCCReferral(SSCCRefDto ssccRefDto, SSCCListDto ssccListDto, String idUser) {
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();

		if (CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
			//Defect 14093, artf146687 changes. Code to check for concurrent inserts.
			SSCCRefListDto sSCCRefListDto = new SSCCRefListDto();
			sSCCRefListDto.setIdCase(ssccRefDto.getIdCase());
			Long ulIdPerson = Long.parseLong(idUser);

			sSCCRefListDto= processDisplayLogicForAddButton(sSCCRefListDto, ulIdPerson);
			if(!sSCCRefListDto.isIndDisplayReferralAddButton()) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(MSG_FMLY_REF_UPDATED_CODE);
				errorDto.setErrorMsg(MSG_FMLY_REF_UPDATED_MSG);
				ssccReferralRes.setErrorDto(errorDto);
				ssccReferralRes.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());
				return ssccReferralRes;
			}
		}
		String timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
				.concat(TIMELINE_DESC_REF_CREATED);
		//PPM 91198 overriding the timeline message if referral is created through transfer process
		if(!ObjectUtils.isEmpty(ssccRefDto.getTransferToStage()) && !(ssccRefDto.getTransferToStage()==999999999l)){
			timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
					.concat(TIMELINE_DESC_REF_CREATED_TRANSFER_PROCESS);
		}
		// Populate ssccTimeLineDto with the values saved into the SSCC_REFERRAL
		// table
		List<SSCCTimelineDto> timelineList = new ArrayList<>();
		timelineList.add(ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto, timelineRefDesc, idUser));
		ssccRefDto.setSsccTimelineDtoList(timelineList);
		// Update the cdStatus and dtExpectedPlcmt in the SSCC_REFERRAL table
		ssccRefDto.setIdSSCCReferral(ssccRefDao.updateSSCCRefHeader(ssccRefDto));
		// For Placement Referrals
		if (!ObjectUtils.isEmpty(ssccRefDto.getSsccRefPlcmtDto())
				&& (CodesConstant.CSSCCREF_10.equals(ssccRefDto.getCdSSCCRefType())
				|| CodesConstant.CSSCCREF_20.equals(ssccRefDto.getCdSSCCRefType()))) {
			if (!ObjectUtils.isEmpty(ssccRefDto.getSsccRefPlcmtDto().getIdCnsrvtrshpRmvlEvent())
					&& ssccRefDto.getSsccRefPlcmtDto().getIdCnsrvtrshpRmvlEvent() > 0L) {
				// Set the Conservatorship Removal Event into Id Event for save
				ssccRefDto.getSsccRefPlcmtDto().setIdEvent(ssccRefDto.getSsccRefPlcmtDto().getIdCnsrvtrshpRmvlEvent());
				ssccRefDto.getSsccRefPlcmtDto().setCdEventType(CodesConstant.CEVNTTYP_REM);
				// update sscc referral event table
				ssccRefDao.updateSsccReferralEvent(ssccRefDto.getSsccRefPlcmtDto());
			}
			if (!ObjectUtils.isEmpty(ssccRefDto.getSsccRefPlcmtDto().getIdLegalStatusEvent())
					&& ssccRefDto.getSsccRefPlcmtDto().getIdLegalStatusEvent() > 0L) {
				// Set the Legal Status Event into Id Event for save
				ssccRefDto.getSsccRefPlcmtDto().setIdEvent(ssccRefDto.getSsccRefPlcmtDto().getIdLegalStatusEvent());
				ssccRefDto.getSsccRefPlcmtDto().setCdEventType(CodesConstant.CEVNTTYP_LES);
				// update sscc referral event table
				ssccRefDao.updateSsccReferralEvent(ssccRefDto.getSsccRefPlcmtDto());
			} else {
				// For Plcmt Referral if Legal Status event is null then update
				// the SSCC_LIST indicator
				ssccListDto.setIndLegalStatusMissing(ServiceConstants.Y);
			}

		}

		if (!ObjectUtils.isEmpty(ssccRefDto.getSsccRefFamilyDtoList())) {
			// saves the sscc family referrals
			ssccRefDto.getSsccRefFamilyDtoList().forEach(o -> {
				if (!ObjectUtils.isEmpty(o.getIndPersonAdded()) && o.getIndPersonAdded()) {
					//SSCC Enhancements - PPM 61082 - UC5
					Date famRefStartDate = getStartDateForFamilyReferral(ssccRefDto, o.getIdPerson() );
					o.setDtStart(famRefStartDate);
					ssccRefDao.saveSSCCRefFamily(o, idUser);
					String timeDesc = o.getNmPersonFull().concat(TIMELINE_DESC_PERSON_ADD);
					ssccRefDto.getSsccTimelineDtoList()
							.add(ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto, timeDesc, idUser));
				}
			});
		}

		// call to insert new row into SSCC_LIST
		ssccRefDao.updateSSCCList(ssccListDto);
		ssccRefDto.getSsccTimelineDtoList().forEach(o -> {
			ssccRefDao.saveSSCCTimeline(o);
		});
		ssccReferralRes.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());

		return ssccReferralRes;
	}

	/**
	 * Method Name: acknowledgeSSCCReferral Method Description: This method is
	 * used update the sscc referral when acknowledge button is clicked.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param idUser
	 *            the id user
	 * @return the SSCC referral res
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCReferralRes acknowledgeSSCCReferral(SSCCRefDto ssccRefDto, String idUser) {
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();
		ssccRefDto.setIdLastUpdatePerson(Long.valueOf(idUser));
		ssccRefDto.setIndRefAcknowledge(ServiceConstants.Y);
		ssccRefDto.setCdRefStatus(CodesConstant.CSSCCSTA_40);
		// form the event description as referral acknowledged.
		String timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
				.concat(TIMELINE_DESC_REF_ACKNWLD);
		// Populate ssccTimeLineDto with the values saved into the SSCC_REFERRAL
		// table
		List<SSCCTimelineDto> timelineList = new ArrayList<>();
		timelineList.add(ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto, timelineRefDesc, idUser));
		ssccRefDto.setSsccTimelineDtoList(timelineList);
		updateSSCCReferralDetail(ssccRefDto, idUser);
		ssccReferralRes.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());
		return ssccReferralRes;
	}

	/**
	 * This method will be triggered when user clicks on acknowledge discharge
	 * button.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCReferralRes acknowledgeDischargeSSCCReferral(SSCCRefDto ssccRefDto, String idUser) {
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();
		String cdRefStatusFromReq = ssccRefDto.getCdRefSubStatus();
		ssccRefDto.setIndDischargeReadyReview(ServiceConstants.Y);
		String timelineRefDesc = ServiceConstants.EMPTY_STR;
		List<SSCCTimelineDto> timelineList = new ArrayList<>();
		// when referral discharge notification is initiated.
		if (CodesConstant.CSSCCSS_10.equals(cdRefStatusFromReq) && ssccRefDto.getIndUserSSCC()) {
			ssccRefDto.setCdRefSubStatus(CodesConstant.CSSCCSS_30);
			timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
					.concat(TIMELINE_DESC_DISCHARGE_NOTIF_ACKN);
		}
		// when referral discharge request is initiated.
		else if (CodesConstant.CSSCCSS_20.equals(cdRefStatusFromReq) && !ssccRefDto.getIndUserSSCC()) {
			ssccRefDto.setCdRefSubStatus(CodesConstant.CSSCCSS_40);
			timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
					.concat(TIMELINE_DESC_DISCHARGE_REQ_ACKN);
		}
		timelineList.add(ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto, timelineRefDesc, idUser));
		// If Referral has not been acknowledged yet. Then set the
		// indRefAcknowledge to true and create a timeline milestone for the
		// same
		if (ServiceConstants.N.equals(ssccRefDto.getIndRefAcknowledge())) {
			ssccRefDto.setIndRefAcknowledge(ServiceConstants.Y);
			timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
					.concat(TIMELINE_DESC_REF_ACKNWLD);
			timelineList.add(ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto, timelineRefDesc, idUser));
		}
		ssccRefDto.setSsccTimelineDtoList(timelineList);
		// updates the sscc referral
		updateSSCCReferralDetail(ssccRefDto, idUser);
		ssccReferralRes.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());
		return ssccReferralRes;
	}

	/**
	 * Method Name: deleteSSCCReferralHeader Method Description: This method
	 * deletes the sscc referral header information.
	 *
	 * @param idSSCCReferral
	 *            the id SSCC referral
	 * @return the long
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long deleteSSCCReferralHeader(Long idSSCCReferral) {
		boolean indDeleted = ssccRefDao.deleteSSCCReferral(idSSCCReferral);
		if (indDeleted) {
			return idSSCCReferral;
		}
		return null;
	}

	/**
	 * Method Name: finalizeSSCCReferralDischarge Method Description: Method is
	 * invoked when user clicks on the Finalize Discharge button on the SSCC
	 * Referral Detail page.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param idUser
	 *            the id user
	 * @return the SSCC referral res
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCReferralRes finalizeSSCCReferralDischarge(SSCCRefDto ssccRefDto, String idUser) {
		ssccRefDto.setIndDischargeReadyReview(ServiceConstants.Y);
		ssccRefDto.setCdRefStatus(CodesConstant.CSSCCSTA_130);
		String timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
				.concat(TIMELINE_DESC_DISCHARGE_FINAL);
		//PPM 91198 overriding the timeline message if referral is ended by transfer process
		if(!ObjectUtils.isEmpty(ssccRefDto.getTransferToStage()) && !(ssccRefDto.getTransferToStage()==999999999l)){
			timelineRefDesc = lookupDao.decode(CodesConstant.CSSCCREF, ssccRefDto.getCdSSCCRefType())
					.concat(TIMELINE_DESC_DISCHARGE_FINAL_TRANSFER_PROCESS);
		}
		// Populate ssccTimeLineDto with the values saved into the SSCC_REFERRAL
		// table
		List<SSCCTimelineDto> timelineList = new ArrayList<>();
		timelineList.add(ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto, timelineRefDesc, idUser));
		ssccRefDto.setSsccTimelineDtoList(timelineList);
		boolean indReadyforDischarge = true;
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();
		List<ErrorDto> errorDtoList = new ArrayList<>();
		// Fetch Vendor Id and set it to DTO
		Long vendorId = ssccRefDao.fetchVendorIdforReferral(ssccRefDto);
		// Perform all validation checks before updating Referral status to
		// discharged
		if (CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
			if (ssccRefDao.hasActiveSSCCSvcAuthReq(ssccRefDto.getIdSSCCReferral())) {
				indReadyforDischarge = false;
				addError(MSG_SSCC_REF_DIS_OUTSTANDING, errorDtoList);
			}

			// Check if any SSCC service Authorizations are in PROC, PEND or
			// COMP status
			if (ssccRefDao.hasActiveSvcforRefPerson(ssccRefDto, vendorId)) {
				indReadyforDischarge = false;
				addError(MSG_SSCC_REF_SVC_AUTH_OUTSTANDING, errorDtoList);
			}

			// Check if any Approved SSCC SVC Auth's have a Term Dt > Actual
			// Discharge Date
			//Removing this for UC12 changes. FR 12.14.1.3 is descoped.
			/*
			 * List<SSCCRefDto> svcAuthValList = ssccRefDao.fetchAprvSvcAuthList(ssccRefDto,
			 * vendorId); if (!ObjectUtils.isEmpty(svcAuthValList)) { for (SSCCRefDto
			 * serviceAuthValue : svcAuthValList) {
			 *
			 * if (DateUtils.minutesDifference(serviceAuthValue.getDtSvcAuthDtlTerm(),
			 * ssccRefDto.getDtDischargeActual()) > 0) { indReadyforDischarge = false;
			 * addError(MSG_SSCC_REF_SVC_APRV_OUTSTANDING, errorDtoList); break; } } }
			 */
		}

		else {

			// For Placement Referrals perform the following validation checks

			// Check if any placement circumstances are in PROPOSE, ACKNOWLEDGE
			// status
			// Check if any SSCC Child Plan Content is in INITIATE, SAVED
			// CONTENT, RE-PROPOSE or PROPOSE status
			// Check if any Exceptional Care Designations are in PROPOSE status
			// Check if any SSCC Service Authorization requests are in INITIATE
			// or PROPOSE status

			if (ssccRefDao.hasActivePlcmtCircumforRef(ssccRefDto.getIdSSCCReferral())
					|| ssccRefDao.hasActiveSSCCChildPlanContent(ssccRefDto)
					|| ssccRefDao.hasSSCCActExcptCareDesig(ssccRefDto.getIdSSCCReferral())
					|| ssccRefDao.hasActiveSSCCSvcAuthReq(ssccRefDto.getIdSSCCReferral())) {
				indReadyforDischarge = false;
				addError(MSG_SSCC_REF_DIS_OUTSTANDING, errorDtoList);
			}
			// Check if any placement options are in PROPOSE, ACKNOWLEDGE, SAVED
			// CONTENT, APPROVED or PLACED status
			if (ssccRefDao.hasActivePlcmtOptionsforRef(ssccRefDto.getIdSSCCReferral())) {
				indReadyforDischarge = false;
				addError(MSG_SSCC_REF_PLCMT_OPT_OUTSTANDING, errorDtoList);
			}

			// Check if any Placements are without a removal date or removal dt
			// > Actual Discharge Date
			List<PlacementDto> plcmtValList = ssccRefDao.fetchSSCCPlcmtEndDtList(ssccRefDto.getIdStage(),
					ssccRefDto.getIdSSCCResource());
			if (!ObjectUtils.isEmpty(plcmtValList) && !plcmtValList.isEmpty()) {
				for (PlacementDto placementDto : plcmtValList) {

					if (ObjectUtils.isEmpty(placementDto.getDtPlcmtEnd())
							|| ServiceConstants.MAX_DATE.equals(placementDto.getDtPlcmtEnd())) {
						indReadyforDischarge = false;
						addError(MSG_SSCC_REF_OPEN_PLCMT, errorDtoList);
						break;
					} else if (DateUtils.minutesDifference(placementDto.getDtPlcmtEnd(),
							ssccRefDto.getDtDischargeActual()) > 0) {
						indReadyforDischarge = false;
						addError(MSG_SSCC_PLCMT_END_GT_ACT_DIS, errorDtoList);
						break;
					}

				}
			}

			// Check if any SSCC service Authorizations are in PROC, PEND or
			// COMP status
			if (ssccRefDao.hasActiveSvcforRefPerson(ssccRefDto, vendorId)) {
				indReadyforDischarge = false;
				addError(MSG_SSCC_REF_SVC_AUTH_OUTSTANDING, errorDtoList);
			}

			List<SSCCRefDto> svcAuthValList = ssccRefDao.fetchAprvSvcAuthList(ssccRefDto, vendorId);
			// Check if any Approved SSCC SVC Auth's have a Term Dt > Actual
			// Discharge Date
			if (!ObjectUtils.isEmpty(svcAuthValList)) {
				for (SSCCRefDto svcAuthList : svcAuthValList) {
					if (DateUtils.minutesDifference(svcAuthList.getDtSvcAuthDtlTerm(),
							ssccRefDto.getDtDischargeActual()) > 0) {
						indReadyforDischarge = false;
						addError(MSG_SSCC_REF_SVC_APRV_OUTSTANDING, errorDtoList);
						break;
					}
				}

			}
		}
		ssccReferralRes.setErrorvalue(errorDtoList);

		if (indReadyforDischarge) {
			ssccRefDto.setIdLastUpdatePerson(Long.valueOf(idUser));
			updateSSCCReferralDetail(ssccRefDto, idUser);
			ssccReferralRes.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());
		}

		return ssccReferralRes;
	}

	/**
	 * Adds the error.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorDtoList
	 *            the error dto list
	 */
	private void addError(int errorCode, List<ErrorDto> errorDtoList) {
		ErrorDto error = new ErrorDto();
		error.setErrorCode(errorCode);
		errorDtoList.add(error);
	}

	/**
	 * Method Name:undoDischargeSSCCReferral. Method Description:Method is
	 * invoked when SSCC Fixer clicks on Undo Discharge button
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param idUser
	 *            the id user
	 * @return the SSCC referral res
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public SSCCReferralRes undoDischargeSSCCReferral(SSCCRefDto ssccRefDto, String idUser) {
		SSCCReferralRes sSCCReferralRes = new SSCCReferralRes();
		// forms the timeline descrition for discharge undo.
		String timeLineRefDesc = TIMELINE_DESC_DISCHARGE_UNDO;
		SSCCTimelineDto sSCCTimelineDto = ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto, timeLineRefDesc,
				idUser);
		if (!dischargeSSCCReferral(ssccRefDto, sSCCTimelineDto)) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(MSG_SSCC_REF_NO_UNDO_DIS);
			sSCCReferralRes.setErrorDto(errorDto);
		}
		sSCCReferralRes.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());
		return sSCCReferralRes;
	}

	/**
	 * Method Name: updateAndNotifySSCCReferral Method Description: This method
	 * is does required actions when update and notify button is clicked.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param idUser
	 *            the id user
	 * @return the SSCC referral res
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCReferralRes updateAndNotifySSCCReferral(SSCCRefDto ssccRefDto, String idUser) {
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();
		// Read the SSCC Referral information from DB to check if Fixer has
		// updated any fields
		// SSCCRefDto sccRefDtoDB =
		// fetchReferralById(ssccRefDto.getIdSSCCReferral(),
		// idUser).getSsccRefDto();
		SSCCRefDto sccRefDtoDB = ssccRefDao.fetchReferralByPK(ssccRefDto.getIdSSCCReferral());
		ErrorDto errorDto = null;
		if (ssccRefDto.getIndUserSSCCFixer() && !CodesConstant.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
			// When SSCC Fixer is updating a Placement Referrals and user has
			// unchecked the Prior Comm checkbox OR Fixer is modified the
			// DtRecordedDFPS value
			if ((!compare(ssccRefDto.getIndPriorComm(), sccRefDtoDB.getIndPriorComm())
					&& ServiceConstants.Y.equals(sccRefDtoDB.getIndPriorComm()))
					|| !compare(ssccRefDto.getDtRecordedDfps(), sccRefDtoDB.getDtRecordedDfps())) {
				/*
				 * Fixer has unchecked the Prior Communication indicator.
				 * Perform following validation checks before updating
				 * information Fetch all active placements associated with this
				 * referral and ensure that Plcmt start date is after DtRecorded
				 * Fetch all end dated placements associated with this referral
				 * and ensure Plcmt start date is after DtRecorded and Plcmt end
				 * date is after dtRecorded if Referral is Active AND if
				 * referral is Discharged then Plcmt End date should be before
				 * Actual Discharge Date of referral
				 */
				List<PlacementDto> plcmtList = ssccRefDao.fetchSSCCPlcmtEndDtList(ssccRefDto.getIdStage(),
						ssccRefDto.getIdSSCCResource());
				if (!ObjectUtils.isEmpty(plcmtList) && !plcmtList.isEmpty()) {
					for (PlacementDto placementDto : plcmtList) {
						Date dtRecorded = !ObjectUtils.isEmpty(ssccRefDto.getDtRecordedDfps())
								? ssccRefDto.getDtRecordedDfps() : ssccRefDto.getDtRecorded();
						if (ObjectUtils.isEmpty(errorDto)) {
							if (ObjectUtils.isEmpty(placementDto.getDtPlcmtEnd())
									|| ServiceConstants.GENERIC_END_DATE.equals(placementDto.getDtPlcmtEnd())) {
								// If Referral Active and Plcmt Open ensure
								// Plcmt Start date is before DtRecorded of
								// Referral
								if (CodesConstant.CSSCCSTA_40.equals(ssccRefDto.getCdRefStatus())
										&& placementDto.getDtPlcmtStart().before(dtRecorded)) {
									errorDto = new ErrorDto();
									errorDto.setErrorCode(MSG_SSCC_REF_DT_PRIOR_REQ);
								}
							} else {
								// This means that the Placement is end dated.
								// Check
								// if the plcmt start and end dates fall
								// within the dt_recorded and
								// dt_discharge_actual of
								// any discharged referral for stage.
								// If there is no discharged ref linked to this
								// end
								// dated plcmt then check to
								// see if the Plcmt start date is before
								// dtRecorded
								// of the current Active SSCC Referral
								// and display error is condition is true.
								if (!ssccRefDao.isPlcmtDatesWithinRefRange(placementDto.getIdPlcmtEvent(),
										ssccRefDto.getIdStage()) && placementDto.getDtPlcmtStart().before(dtRecorded)) {
									errorDto = new ErrorDto();
									errorDto.setErrorCode(MSG_SSCC_DT_PRIOR_REQ_PLCMT_LINK);
								}
							}
						}

					}
				}
			}
		}
		ssccReferralRes.setErrorDto(errorDto);
		if (ObjectUtils.isEmpty(errorDto)) {
			// If the SSCC Fixer has updated any of the following fields a
			// timeline item needs to be saved.
			// Ref Type, Ref Sub Type, Ind Prior Comm, Dt Recorded DFPS, DT
			// Recorded SSCC
			List<SSCCTimelineDto> timelineFixerUpdates = new ArrayList<>();
			if (ssccRefDto.getIndUserSSCCFixer()) {
				if (!compare(ssccRefDto.getDtRecordedDfps(), sccRefDtoDB.getDtRecordedDfps())) {
					timelineFixerUpdates.add(ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto,
							TIMELINE_DTDFPS_UPDATE_FIXER, idUser));
				}
				if (!compare(ssccRefDto.getDtRecordedSscc(), sccRefDtoDB.getDtRecordedSscc())) {
					timelineFixerUpdates.add(ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto,
							TIMELINE_DTSSCC_UPDATE_FIXER, idUser));
				}
				if (!compare(ssccRefDto.getCdSSCCRefType(), sccRefDtoDB.getCdSSCCRefType())) {
					timelineFixerUpdates.add(
							ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto, TIMELINE_REFTYPE_FIXER, idUser));
				}
				if (!compare(ssccRefDto.getCdSSCCRefSubtype(), sccRefDtoDB.getCdSSCCRefSubtype())) {
					timelineFixerUpdates.add(ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto,
							TIMELINE_REFSUBTYPE_UPDATE_FIXER, idUser));
				}
				if (!compare(ssccRefDto.getIndPriorComm(), sccRefDtoDB.getIndPriorComm())) {
					timelineFixerUpdates.add(ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto,
							TIMELINE_IND_PRIOR_COMM_UPDATE_FIXER, idUser));
				}
			}
			// Populate the dto with the data before update service
			ssccRefDto = ssccRefUtil.populateDTOForUpdateNotify(ssccRefDto, idUser);
			if (!timelineFixerUpdates.isEmpty()) {
				ssccRefDto.getSsccTimelineDtoList().addAll(timelineFixerUpdates);
			}
			updateSSCCReferralDetail(ssccRefDto, idUser);
		}
		ssccReferralRes.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());
		ssccReferralRes.setSsccRefDto(ssccRefDto);
		return ssccReferralRes;

	}

	/**
	 * Method Name: compare Method Description: this method compares both the
	 * strings and returns flag.
	 *
	 * @param obj1
	 *            the obj 1
	 * @param obj2
	 *            the obj 2
	 * @return true, if successful
	 */
	private boolean compare(Object obj1, Object obj2) {
		return ObjectUtils.isEmpty(obj1) ? ObjectUtils.isEmpty(obj2) : obj1.equals(obj2);
	}

	/**
	 * Method Name: compare Method Description: this method compares both the
	 * strings and returns flag.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param ssccTimelineDto
	 *            the sscc timeline dto
	 * @return true, if successful
	 */
	private boolean dischargeSSCCReferral(SSCCRefDto ssccRefDto, SSCCTimelineDto ssccTimelineDto) {
		boolean indUndoDischarge = false;
		// If Family Service Type Referral
		// If Family Service Referrals: Check if there is an active Family
		// referral in case
		// If there is no active referral in case check if
		// Actual discharge date is max discharge date in case
		if (ServiceConstants.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())
				&& !ssccRefDao.hasActiveSSCCFamilySvcRefInCase(ssccRefDto.getIdCase())
				&& ssccRefDao.hasFamRefMaxDtDischargeInCase(ssccRefDto.getIdSSCCReferral(), ssccRefDto.getIdCase())) {
			indUndoDischarge = true;
		} else {
			// Check if stage has an active Placement Referral
			// If no active Placement referral in stage then check if
			// Actual discharge date is max discharge date in stage
			if ((!ssccRefDao.hasActiveSSCCPlcmtReferralExistsForStage(ssccRefDto.getIdStage(),
					ServiceConstants.PLACEMENT_REFERAAL))
					&& ssccRefDao.hasPlcmtRefMaxDtDischargeInStage(ssccRefDto.getIdSSCCReferral(),
					ssccRefDto.getIdStage())) {
				indUndoDischarge = true;
			}
		}
		if (indUndoDischarge) {
			ssccRefDao.updateSSCCRefStatus(ssccRefDto, ServiceConstants.CSSCCSTA_40);
			//Adding logic for UC12 to remove the end date for the persons. The end date should have been populated when the referral was finalized, and same as the actual referral end date.
			//Find all the persons who have the end date as the actual referral end date and update the end date to null for those persons.
			ssccRefDao.updateSSCCRefFamilyForUndoDischarge(ssccRefDto, ssccTimelineDto.getIdLastUpdatePerson());
			ssccRefDao.saveSSCCTimeline(ssccTimelineDto);
		}
		return indUndoDischarge;
	}

	/**
	 * Method deletes the SSCC Referral Record from the SSCC Referral Table
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCReferralRes deleteSSCCReferral(SSCCRefDto ssccRefDto) {
		// Get the SSCCRefValueBean from state

		SSCCReferralRes sSCCReferralRes = new SSCCReferralRes();

		if (ServiceConstants.CSSCCSTA_10.equals(ssccRefDto.getCdRefStatus())) {
			ssccRefDao.deleteSSCCReferral(ssccRefDto.getIdSSCCReferral());

		} else if (ServiceConstants.CSSCCSTA_40.equals(ssccRefDto.getCdRefStatus())
				|| ServiceConstants.CSSCCSTA_70.equals(ssccRefDto.getCdRefStatus()) || ServiceConstants.CSSCCSTA_130.equals(ssccRefDto.getCdRefStatus())) {
			List<ErrorDto> errorMessages = deleteSSCCRefFixer(ssccRefDto);
			// When SSCC Fixer is trying to delete an active or resented or ended
			// referral
			if (!ObjectUtils.isEmpty(errorMessages)) {
				sSCCReferralRes.setErrorvalue(errorMessages);
			} else {
				sSCCReferralRes.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());
			}
		}

		return sSCCReferralRes;
	}

	/**
	 * Method will be invoked when the SSCC Fixer is trying to delete an Active
	 * or Rescinded Referral
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @return the list
	 */
	private List<ErrorDto> deleteSSCCRefFixer(SSCCRefDto ssccRefDto) {
		List<ErrorDto> errorMsgList = new ArrayList<>();
		// Validation checks would need to be performed only for Active
		// Referrals
		// Check if there are any Active SSCC Placements for this Referral
		if (ServiceConstants.CSSCCSTA_40.equals(ssccRefDto.getCdRefStatus()) || ServiceConstants.CSSCCSTA_130.equals(ssccRefDto.getCdRefStatus()) ){ //added 130 status for 10.2 CBC R3 changes
			if (!ServiceConstants.CSSCCREF_30.equals(ssccRefDto.getCdSSCCRefType())) {
				// Check if there are any SSCC child plans in PROC status liked
				// to SSCC Referral
				if (ssccRefDao.hasActiveSSCCChildPlan(ssccRefDto.getIdSSCCReferral())) {
					addError(MSG_SSCC_CP_LINKED, errorMsgList);
				}
				List<PlacementDto> plcmtValList = ssccRefDao.fetchSSCCPlcmtEndDtList(ssccRefDto.getIdStage(),
						ssccRefDto.getIdSSCCResource());
				if (!ObjectUtils.isEmpty(plcmtValList)) {
					boolean indNoDischargedRef = false;
					for (PlacementDto placementDto : plcmtValList) {
						Date dtPlcmtEnd = placementDto.getDtPlcmtEnd();
						Date dtPlcmtStart = placementDto.getDtPlcmtStart();
						if (ObjectUtils.isEmpty(dtPlcmtEnd) || ServiceConstants.GENERIC_END_DATE.equals(dtPlcmtEnd)) {
							// The Referral cannot be deleted if there is an
							// Active Plcmt
							addError(MSG_SSCC_ACT_PLCMT_LINKED, errorMsgList);
							break;
						} else {
							// For SSCC Plcmts that are end dated 1) The Plcmt
							// Start must be after
							// DtRecorded/DtRecordedDFPS
							// 2) Plcmt End must be before Actual Discharge date
							// of atleast one Discharged
							// Referral for SSCC
							List<SSCCRefDto> dischargedRefList = ssccRefDao
									.fetchDischargedSSCCReferralsforStage(ssccRefDto);
							if (!ObjectUtils.isEmpty(dischargedRefList)) {
								indNoDischargedRef = true;
								String indPlcmtDatesWithinRange = ServiceConstants.EMPTY_STRING;
								for (SSCCRefDto sSCCRefDto : dischargedRefList) {
									indPlcmtDatesWithinRange = ServiceConstants.BOOLEAN_FALSE;
									Date dtRecorded = null;

									if (!ObjectUtils.isEmpty(sSCCRefDto.getDtRecordedDfps()))
										dtRecorded = sSCCRefDto.getDtRecordedDfps();
									else
										dtRecorded = sSCCRefDto.getDtRecorded();

									if ((dtPlcmtStart.after(dtRecorded) || dtPlcmtStart.equals(dtRecorded))
											&& (dtPlcmtEnd.before(sSCCRefDto.getDtDischargeActual())
											|| dtPlcmtEnd.equals(sSCCRefDto.getDtDischargeActual()))) {
										indPlcmtDatesWithinRange = ServiceConstants.BOOLEAN_TRUE;
										break;
									}
								} // for loop end
								if (ServiceConstants.BOOLEAN_FALSE.equals(indPlcmtDatesWithinRange)) {
									addError(MSG_SSCC_PLCMT_LINKED, errorMsgList);
									break;
								}

							} // if end
						} // else end
					} // end of for loop
					if (indNoDischargedRef && !errorMsgList.stream()
							.filter(o -> MSG_SSCC_ACT_PLCMT_LINKED == o.getErrorCode() || MSG_SSCC_PLCMT_LINKED == o.getErrorCode()).findAny().isPresent()) {
						// If there are no discharged Referrals and no active
						// placement but at least one end dated placement
						addError(MSG_SSCC_PLCMT_LINKED, errorMsgList);
					}
				} // end if FetchPlamcentList
			} // End if not Family Referral

			// Check if there is an active Service Auth for this Referral
			if (ssccRefDao.hasActiveSvcforRefPerson(ssccRefDto, ssccRefDao.fetchVendorIdforReferral(ssccRefDto)))
				addError(MSG_SSCC_SVC_AUTH_LINKED, errorMsgList);

			// If there are Service Authorizations for SSCC then begin and end
			// dates must
			// fall within start and end dates of
			// a Discharged Referral for that SSCC
			List<SSCCRefDto> aprvSvcAuthList = ssccRefDao.fetchAprvSvcAuthList(ssccRefDto,
					ssccRefDao.fetchVendorIdforReferral(ssccRefDto));
			if (!ObjectUtils.isEmpty(aprvSvcAuthList)) {
				String indAprvSvcDatesWithinRange;
				for (SSCCRefDto sSCCRefDto : aprvSvcAuthList) {
					indAprvSvcDatesWithinRange = ServiceConstants.BOOLEAN_FALSE;
					// Fetch all the discharged referrals for SSCC and ensure
					// SvcAuth begin and end
					// dates is within DtRecorded and DtDischargeActual of
					// referral
					List<SSCCRefDto> dischargedRefList3 = ssccRefDao.fetchDischargedSSCCReferralsforStage(ssccRefDto);
					if (!ObjectUtils.isEmpty(dischargedRefList3)) {
						for (SSCCRefDto sSCCDto : dischargedRefList3) {
							Date dtRecord = null;
							if (!ObjectUtils.isEmpty(sSCCDto.getDtRecordedDfps()))
								dtRecord = sSCCDto.getDtRecordedDfps();
							else
								dtRecord = sSCCDto.getDtRecorded();

							if ((sSCCRefDto.getDtSvcAuthDtlBegin().after(dtRecord)
									|| sSCCRefDto.getDtSvcAuthDtlBegin().equals(dtRecord))
									&& (sSCCRefDto.getDtSvcAuthDtlTerm().before(sSCCDto.getDtDischargeActual())
									|| sSCCRefDto.getDtSvcAuthDtlTerm()
									.equals(sSCCDto.getDtDischargeActual()))) {
								indAprvSvcDatesWithinRange = ServiceConstants.BOOLEAN_TRUE;
								break;
							}
						}
					}
					if (ServiceConstants.BOOLEAN_FALSE.equals(indAprvSvcDatesWithinRange)) {
						addError(MSG_SSCC_TERM_SVC_AUTH_LINKED, errorMsgList);
						break;
					}
				}
			}
			if (ObjectUtils.isEmpty(errorMsgList) || errorMsgList.isEmpty()) {
				// function to delete from all the tables and unassign the
				// secondary staff as well.
				deleteSSCCReferralByFixer(ssccRefDto);
				ssccRefDao.unAssignAllSSCCSecondaryStaff(ssccRefDto);
			}
		} else if (ServiceConstants.CSSCCSTA_70.equals(ssccRefDto.getCdRefStatus())) {
			// function to delete from all the tables and unassign the secondary
			// staff as well.
			deleteSSCCReferralByFixer(ssccRefDto);
			ssccRefDao.unAssignAllSSCCSecondaryStaff(ssccRefDto);
		}
		return errorMsgList;
	}

	/**
	 * This method will call all the deletes which needs to be deleted when sscc
	 * referral is deleted.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 */
	private void deleteSSCCReferralByFixer(SSCCRefDto ssccRefDto) {

		// Delete Child Plan Participant

		ssccRefDao.deleteSSCCChildPlanParticip(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC Child Plan Topic
		ssccRefDao.deleteSSCCChildPlanTopic(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC Child Plan records associated with Referral
		ssccRefDao.deleteSSCCChildPlan(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC DayCare Request records associated with Referral
		ssccRefDao.deleteSSCCDaycareRequest(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC Except Care Design records associated with Referral
		ssccRefDao.deleteSSCCExceptCareDesig(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC List records associated with Referral
		ssccRefDao.deleteSSCCList(ssccRefDto.getIdSSCCReferral());

		// SIR 1018834 - Delete SSCC Placement Narratives
		ssccRefDao.deleteSSCCPlcmtNarr(ssccRefDto.getIdSSCCReferral());

		// SIR 1018834 - Delete SSCC Placement Placed
		ssccRefDao.deleteSSCCPlcmtPlaced(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC Placement Info
		ssccRefDao.deleteSSCCPlcmtInfo(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC Placement MedCnstr
		ssccRefDao.deleteSSCCPlcmtMedCnsntr(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC Placement Name
		ssccRefDao.deleteSSCCPlcmtName(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC Placement Circumstance
		ssccRefDao.deleteSSCCPlcmtCircumstance(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC Placement Header records associated with Referral
		ssccRefDao.deleteSSCCPlcmtHeader(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC Referral Event records associated with Referral
		ssccRefDao.deleteSSCCRefEvent(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC Referral Family records associated with Referral
		ssccRefDao.deleteSSCCRefFamily(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC SVC Authorization records associated with Referral
		ssccRefDao.deleteSSCCSvcAuth(ssccRefDto.getIdSSCCReferral());

		// Delete SSCC Time line records associated with Referral
		ssccRefDao.deleteSSCCTimeline(ssccRefDto.getIdSSCCReferral());

		// Delete the Referral
		ssccRefDao.deleteSSCCReferral(ssccRefDto.getIdSSCCReferral());

	}

	/**
	 *
	 * Method Name: addOrRemoveSSCCRefFamilyPerson Method Description: This method used
	 * when user adds or removes the person to family referral.
	 *
	 * @param ssccRefDto
	 * @param idUser
	 * @return
	 */
	@Override
	public SSCCReferralRes addOrRemoveSSCCRefFamilyPerson(SSCCRefDto ssccRefDto, String idUser,
														  boolean indRemovePerson) {
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();
		SSCCListDto ssccList = null;
		String timelineRefDesc = null;
		//Adding this code to populate the indUserSSCC, so that when the Timeline is created, Agency is set correctly.
		boolean isSSCCUser = ssccRefDao.isUserSSCCExternal(Long.valueOf(idUser), ssccRefDto.getCdCntrctRegion());
		ssccRefDto.setIndUserSSCC(isSSCCUser);
		if (!indRemovePerson) {
			timelineRefDesc = ssccRefDto.getSsccRefFamilyDto().getNmPersonFull().concat(TIMELINE_DESC_PERSON_ADD);
		} else {
			timelineRefDesc = ssccRefDto.getSsccRefFamilyDto().getNmPersonFull().concat(TIMELINE_DESC_PERSON_REMOVE);
		}
		SSCCTimelineDto sSCCTimelineDto = ssccRefUtil.populateSSCCTimelineDtoForReferral(ssccRefDto, timelineRefDesc,
				idUser);
		List<SSCCListDto> ssccDtoList = ssccListDao.fetchSSCCList(ssccRefDto.getIdSSCCReferral());
		if (!ObjectUtils.isEmpty(ssccDtoList)) {
			ssccList = ssccDtoList.get(0);
		}
		if (!indRemovePerson) {
			Date startDate = getStartDateForFamilyReferral(ssccRefDto, ssccRefDto.getSsccRefFamilyDto().getIdPerson());
			ssccRefDto.getSsccRefFamilyDto().setDtStart(startDate);
			ssccRefDao.saveSSCCRefFamily(ssccRefDto.getSsccRefFamilyDto(), idUser);
		} else {
			setEndDateForFamilyPerson(ssccRefDto.getSsccRefFamilyDto(),ssccRefDto);
			ssccRefDao.updateSSCCRefFamily(ssccRefDto.getSsccRefFamilyDto(), idUser);
		}
		ssccRefDao.saveSSCCTimeline(sSCCTimelineDto);
		ssccList.setIdSSCCReferral(ssccRefDto.getSsccRefFamilyDto().getIdSsccReferral());
		ssccList.setIdLastUpdatePerson(Long.valueOf(idUser));
		ssccList.setDtFamilyMemberUpdate(new Date());
		ssccRefDao.updateSSCCList(ssccList);
		ssccReferralRes.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());
		return ssccReferralRes;
	}


	/**
	 *
	 * Method Name: setSSCCStageIndicator Method Description: This method used
	 * to set the indicator for SSCC Stage
	 *
	 * @param sSCCResourceDto
	 * @return
	 */
	private boolean getSSCCStageIndicator(SSCCResourceDto sSCCResourceDto) {
		Date today = new Date();
		boolean indSSCCStageII = false;
		if (!TypeConvUtil.isNullOrEmpty(sSCCResourceDto)) {
			if (!TypeConvUtil.isNullOrEmpty(sSCCResourceDto.getDtFamilyServiceReferral())) {
				if (sSCCResourceDto.getDtFamilyServiceReferral().equals(today)
						|| sSCCResourceDto.getDtFamilyServiceReferral().before(today)) {
					indSSCCStageII = true;
				}
			}
		}
		return indSSCCStageII;
	}

	@Override
	public SSCCReferralRes retrieveSSCCRefByStageId(long stageId) {
		SSCCReferralRes res = null;
		List<SSCCRefDto> ssccRefDtoList = ssccRefDao.fetchActiveSSCCReferralsForStage(stageId, ServiceConstants.PLACEMENT_REFERAAL);
		if(null != ssccRefDtoList && ssccRefDtoList.size() > 0){
			for(SSCCRefDto dto : ssccRefDtoList){
				res = new SSCCReferralRes();
				res.setIdSSCCReferral(dto.getIdSSCCReferral());
			}
		}

		return res;
	}



	private String getAssignedPersonsIdForReferral(SSCCRefDto ssccRefDto) {
		String allPersonsAssigned = ServiceConstants.EMPTY_STRING;
		List<Long> staffAssigned = new ArrayList<Long>();

		if(!ObjectUtils.isEmpty(ssccRefDto.getDfpsStaffAssign())){
			staffAssigned.addAll(ssccRefDto.getDfpsStaffAssign().values().stream().map(dfpsStaff -> dfpsStaff.getIdPerson()).collect(Collectors.toList()));
		}
		if (!ObjectUtils.isEmpty(ssccRefDto.getIdSSCCStaff1()) && ssccRefDto.getIdSSCCStaff1() > 0) {
			staffAssigned.add(ssccRefDto.getIdSSCCStaff1());
		}

		if (!ObjectUtils.isEmpty(ssccRefDto.getIdSSCCStaff2()) && ssccRefDto.getIdSSCCStaff2() > 0) {
			staffAssigned.add(ssccRefDto.getIdSSCCStaff2());
		}
		if(!ObjectUtils.isEmpty(staffAssigned)){
			allPersonsAssigned = StringUtils.join(staffAssigned,",");
		}


		return allPersonsAssigned;
	}

	/**
	 * @param ssccRefDto
	 */
	private Date getStartDateForFamilyReferral(SSCCRefDto ssccRefDto, Long idPerson) {
		Date startDate = getFamilyReferralStartDate(ssccRefDto);
		SSCCRefFamilyDto ssccRefFamilyDto = ssccRefDao.fetchEndDateForPersonInFamReferral(idPerson, ssccRefDto.getIdCase());

		if (!ObjectUtils.isEmpty(ssccRefFamilyDto) && !ObjectUtils.isEmpty(ssccRefFamilyDto.getDtEnd())){
			Date dtDischarge = ssccRefFamilyDto.getDtEnd();
			startDate = DateUtils.addToDate(dtDischarge, 0, 0, 1);
		}
		return startDate;
	}

	/**
	 * @param ssccRefDto
	 * @return
	 */
	private Date getFamilyReferralStartDate(SSCCRefDto ssccRefDto) {
		Date familyReferralStartDt = new Date();
		SSCCResourceDto ssccResourceDto = null;
		if (!ObjectUtils.isEmpty(ssccRefDto.getCdCntrctRegion())) {
			ssccResourceDto = ssccRefDao.fetchSSCCResourceInfo(ssccRefDto.getCdCntrctRegion(), ssccRefDto.getIdSSCCCatchment());
		}
		Date activeSubStageStartDate = ssccRefUtil.getActiveSubStageStartDate(ssccRefDto.getIdCase());
		if (!ObjectUtils.isEmpty(ssccResourceDto) && !ObjectUtils.isEmpty(ssccResourceDto.getDtFamilyServiceReferral())
				&& !ObjectUtils.isEmpty(activeSubStageStartDate)) {
			if(ssccResourceDto.getDtFamilyServiceReferral().after(activeSubStageStartDate)){
				familyReferralStartDt = ssccResourceDto.getDtFamilyServiceReferral();
			}else{
				familyReferralStartDt = activeSubStageStartDate;
			}
		}else if(ObjectUtils.isEmpty(activeSubStageStartDate)){
			familyReferralStartDt = ssccResourceDto.getDtFamilyServiceReferral();
		}
		return familyReferralStartDt;
	}



	/**
	 * @param ssccRefFamilyDto
	 * @param ssccRefDto
	 */
	private void setEndDateForFamilyPerson(SSCCRefFamilyDto ssccRefFamilyDto, SSCCRefDto ssccRefDto) {
		Long idPerson = ssccRefDto.getSsccRefFamilyDto().getIdPerson();
		List<SSCCRefFamilyDto> ssccRefFamilyList =  ssccRefDao.fetchSSCCReferralFamilyPersonList(ssccRefDto.getIdSSCCReferral());
		if (!ObjectUtils.isEmpty(ssccRefFamilyList)) {
			ssccRefFamilyList = ssccRefFamilyList.stream().filter(ref -> !ObjectUtils.isEmpty(ref.getDtStart()))
					.filter(famRef -> famRef.getIdPerson().equals(idPerson)).sorted(Comparator.comparing(SSCCRefFamilyDto::getDtStart).reversed())
					.collect(Collectors.toList());
		}
		if (ssccRefFamilyList.size() > 0) {
			Date dtStart = ssccRefFamilyList.get(0).getDtStart();
			if(!ObjectUtils.isEmpty(dtStart) && DateUtils.isAfterToday(dtStart)){
				ssccRefFamilyDto.setDtEnd(dtStart);
			}

		}
	}


	/**
	 * code added for artf231094
	 * @param ssccRefDto
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCReferralRes getSSCCRefCount(SSCCRefDto ssccRefDto) {
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();
		List<Long> ssccRefCount = ssccRefDao.fetchSSCCRefCount(ssccRefDto.getIdSSCCReferral(), ssccRefDto.getIdPerson());
		ssccRefDto.setSsccRefCountList(!ObjectUtils.isEmpty(ssccRefCount) ? ssccRefCount : new ArrayList<Long>());
		ssccReferralRes.setSsccRefDto(ssccRefDto);
		return ssccReferralRes;
	}

	/**
	 * code added for PPM 91198
	 * @param  personListByStage
	 * @param  ssccRefFamilyDtoList
	 * @return personListByStage
	 */
	private List<StagePersonValueDto> allpersonsListExistsinTransferringStage(List<StagePersonValueDto> personListByStage,List<SSCCRefFamilyDto> ssccRefFamilyDtoList) {
		 List<StagePersonValueDto> newPersonListByStage=personListByStage.stream().filter(stagePersonValueDto->ssccRefFamilyDtoList.stream().anyMatch(sSCCRefFamilyDto->sSCCRefFamilyDto.getIdPerson().equals(stagePersonValueDto.getIdPerson()))).collect(Collectors.toList());
         return newPersonListByStage;
	}
	/**
	 * code added for PPM 91198
	 * @param ssccRefFamilyDtoList
	 * @param personListByStage
	 * @return boolean
	 */
	private List<SSCCRefFamilyDto> allpersonsNotExistsinTransferringStage(List<SSCCRefFamilyDto> ssccRefFamilyDtoList, List<StagePersonValueDto> personListByStage) {

		List<SSCCRefFamilyDto> nonExistingPersonsReferralList = ssccRefFamilyDtoList.stream().filter(sSCCRefFamilyDto -> !personListByStage.stream()
						.anyMatch(stagePersonValueDto -> stagePersonValueDto.getIdPerson().equals(sSCCRefFamilyDto.getIdPerson())))
				.collect(Collectors.toList());
		return nonExistingPersonsReferralList;
	}

	private List<SSCCRefFamilyDto> populateNewPersons(List<SSCCRefFamilyDto> nonExistingPersonsReferralList, Long idSSCCRefId) {
		List<StagePersonValueDto> resultList = new ArrayList<>();
		nonExistingPersonsReferralList.forEach(ssccRefFamilyDto -> {
			ssccRefFamilyDto.setIdSsccReferral(idSSCCRefId);
			ssccRefFamilyDto.setIndPersonAdded(true);
		});
        return nonExistingPersonsReferralList;
	}

}
