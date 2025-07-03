package us.tx.state.dfps.service.investigation.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.CloseStageCaseInputDto;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.PriorStageDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dao.ServiceDeliveryRtrvDtlsDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkPersonStgTypeDao;
import us.tx.state.dfps.service.admin.dao.UpdateToDoDao;
import us.tx.state.dfps.service.admin.dto.AllegationStageInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkPersonStgTypeInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkPersonStgTypeOutDto;
import us.tx.state.dfps.service.admin.dto.UpdateToDoDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casemanagement.dao.ArHelperDao;
import us.tx.state.dfps.service.casemanagement.dao.FetchIncomingFacilityDao;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.SituationDao;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityOutputDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.FacilityInvCnclsnReq;
import us.tx.state.dfps.service.common.request.RetrvPersonIdentifiersReq;
import us.tx.state.dfps.service.common.response.FacilityInvCnclsnRes;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.JSONUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.ContactSearchDto;
import us.tx.state.dfps.service.contact.dto.ContactSearchListDto;
import us.tx.state.dfps.service.contacts.dao.AllegFacilDao;
import us.tx.state.dfps.service.contacts.dao.ContactSearchDao;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvCnclsnDetailDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvDetailFetchDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvEventDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvProviderDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvStageDetailsDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvstRsrcLinkRsrcAddDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.ProgramAdminDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.ResourceAddressDto;
import us.tx.state.dfps.service.investigation.dao.FacilAllgDtlDao;
import us.tx.state.dfps.service.investigation.dao.FacilityAbuseInvReportDao;
import us.tx.state.dfps.service.investigation.dao.FacilityInvCnclsnDao;
import us.tx.state.dfps.service.investigation.dao.FacilityInvSumDao;
import us.tx.state.dfps.service.investigation.dto.FacilAllegInjuryDto;
import us.tx.state.dfps.service.investigation.service.FacilityInvCnclsnService;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.person.dao.CriminalHistoryDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonIdDao;
import us.tx.state.dfps.service.person.dto.PersonIdentifiersDto;
import us.tx.state.dfps.service.personutility.dao.PersonUtilityDao;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsInDto;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsOutDto;
import us.tx.state.dfps.service.stageutility.dao.StageUtilityDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.service.CloseStageCaseService;
import us.tx.state.dfps.xmlstructs.inputstructs.AllegationFacilAllegPersonDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilAllegPersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to provide the service implementation for Facility Investigation
 * conclusion. This class will provide implementation to fetch/save/validate/
 * information related to Facility Investigation conclusion screen. May 24,
 * 2018- 3:37:25 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class FacilityInvCnclsnServiceImpl implements FacilityInvCnclsnService {

	@Autowired
	FacilityInvCnclsnDao facilityInvCnclsnDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	CaseSummaryDao caseSummaryDao;

	@Autowired
	NotifToLawEnforcementDao notifToLawEnforcementDao;

	@Autowired
	FetchIncomingFacilityDao fetchIncomingFacilityDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	FacilityInvSumDao facilityInvSumDao;

	@Autowired
	ServiceDeliveryRtrvDtlsDao serviceDeliveryRtrvDtlsDao;

	@Autowired
	ContactSearchDao contactSearchDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	PersonIdDao personIdDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	AllegFacilDao allegFacilDao;

	@Autowired
	CheckStageEventStatusService checkStageEventStatusService;

	@Autowired
	ApprovalCommonService approvalCommonService;

	@Autowired
	PostEventService postEventService;

	@Autowired
	CapsCaseDao capsCaseDao;

	@Autowired
	SituationDao situationDao;

	@Autowired
	ArHelperDao arHelperDao;

	@Autowired
	StagePersonLinkPersonStgTypeDao stagePersonLinkPersonStgTypeDao;

	@Autowired
	UpdateToDoDao updateToDoDao;

	@Autowired
	FacilAllgDtlDao facilAllgDtlDao;

	@Autowired
	EventStagePersonLinkInsUpdDao eventStagePersonLinkInsUpDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	PersonUtilityDao personUtilityDao;

	@Autowired
	StageUtilityDao stageUtilityDao;

	@Autowired
	CloseStageCaseService closeStageCaseService;

	@Autowired
	JSONUtil jsonUtil;
	
	@Autowired
	FacilityAbuseInvReportDao facilityAbuseInvReportDao;

	@Autowired
	CriminalHistoryDao criminalHistoryDao;

	private static final Logger log = Logger.getLogger(FacilityInvCnclsnServiceImpl.class);

	public static final Date ENHANCED_APS_FAC_INV_DATE = Date
			.from(LocalDate.of(2010, 05, 07).atStartOfDay(ZoneId.systemDefault()).toInstant());

	public static final Date EMR_CHANGES_DATE = Date
			.from(LocalDate.of(2013, 07, 25).atStartOfDay(ZoneId.systemDefault()).toInstant());

	public final static String APS_FACILITY_NARRATIVE_VIEW = "aps_facil_narr_view";

	public final static String FACIL_CONCL_NARR_VIEW = "FACIL_CONCL_NARR_VIEW";

	public final static String APS_FACIL_REVIEW_NARR_VIEW = "APS_FACIL_REV_NARR_VIEW";

	public final static String FACIL_CONCL_REVIEW_NARR_VIEW = "FAC_CONCL_REV_NARR_VIEW";

	public final static String FAC_REFERRAL_NARR = "REFERRAL_NARR";

	public final static String AFC_INV_NARR = "AFC_INV_NARR";

	public final static String AFC_INV_NARR2 = "AFC_INV_NARR2";

	public final static String FACILITIES_ABUSE_NEGLECT_KEY = "FACILITIES_ABUSE_NEGLECT";

	public final static String NOTICE_LE_KEY = "NOTICE_LE";

	public final static String REVIEW_FACILITIES_ABUSE_NEGLECT_KEY = "REVIEW_FACILITIES_ABUSE_NEGLECT";

	public final static String FACILITIES_INVST_CONCL_KEY = "FACILITIES_INVST_CONCL";

	public final static String REVIEW_FACILITIES_INVST_CONCL_KEY = "REVIEW_FACILITIES_INVST_CONCL";

	public final static String FAC_REFERRAL_KEY = "FAC_REFERRAL";

	public final static String AFC_INV_KEY = "AFC_INV";

	public final static String AFC_INV_OIG_KEY = "AFC_INV_OIG";

	public final static String APS_NOTIF_LE_NARR = "APS_NOTIF_NARR_VIEW";

	public final static String FC_ALL_COND_MET = "C";

	public final static String FC_NO_ALLEG_SER = "A";

	public final static String FC_NO_DATE = "D";

	public final static String FC_NO_INJ_LST_DETAIL = "I";

	public final static String RELATED = "R";

	public final static String VIEWED = "V";

	public final static int SUCCESS = 0;

	public final static String SAVE_AND_COMPLETE = "saveAndComplete";

	public final static String SAVE_AND_CLOSE = "saveAndClose";

	public static final String FACILITY_INV_CONCLUSION_TASK_CODE = "2450";

	public static final String DECODE_NO_EDITS = "NNNNNNNN";

	public static final String ACTION_CODE_EDITS = "E";

	public static final String ACTION_CODE_CLOSE = "C";

	public static final String SET_K = "K";

	public static final String ACTION_CODE_SAVE_AND_SUBMIT = "S";

	public static final String TWELVE_PM = "12:00 PM";

	public static final String TWELVE_AM = "12:00 AM";

	/**
	 * Method Description: This method will retrieve the facility Investigation
	 * Resource Details for the given InvstRsrcLink ID.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes getFacilityInvstRsrcLink(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		List<FacilityInvstRsrcLinkRsrcAddDto> facilityInvstRsrcLinkRsrcAddDtoList = facilityInvCnclsnDao
				.getInvstRcrcLinkDetails(facilityInvCnclsnReq.getIdInvstRsrcLink());
		facilityInvCnclsnRes = populateFacilityAndResourceAddress(facilityInvCnclsnRes,
				facilityInvstRsrcLinkRsrcAddDtoList);
		log.info("TransactionId :" + facilityInvCnclsnReq.getTransactionId());
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Description: This method will retrieve the facility associated
	 * with the allegation for the given Stage ID.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes getAllegedFacilitiesDtls(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		facilityInvCnclsnRes
				.setAllegedFacilityList(facilityInvCnclsnDao.getAllegedFacilityIDs(facilityInvCnclsnReq.getIdStage()));
		log.info("TransactionId :" + facilityInvCnclsnReq.getTransactionId());
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Description: This method will save the facility to the link table
	 * as part of investigation.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes getsaveFacility(FacilityInvCnclsnReq facilityInvCnclsnReq) {

		FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto = facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto();
		ResourceAddressDto invstMailAddressDto = facilityInvCnclsnReq.getResourceAddressDto();

		String crudOprStatus = ServiceConstants.NOAUDDOP;
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();

		// set idCase and idStage values
		facilityInvCnclsnDetailDto.setIdCase(facilityInvCnclsnReq.getIdCase());
		facilityInvCnclsnDetailDto.setIdStage(facilityInvCnclsnReq.getIdStage());

		if (null != facilityInvCnclsnReq.getResourceAddressDto()) {
			invstMailAddressDto.setAddType(CodesConstant.CRSCADDR_08);
		}
		List<ResourceAddressDto> resourceAddressDtoList = new ArrayList<ResourceAddressDto>();

		if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(facilityInvCnclsnReq.getDataAction())) {

			FacilityInvCnclsnRes facilityInvCnclsnResProcess = new FacilityInvCnclsnRes();

			resourceAddressDtoList = facilityInvCnclsnDao.getRsrcAddressByResourceID(
					facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto().getIdFacilResource(), null);

			if (!TypeConvUtil.isNullOrEmpty(resourceAddressDtoList)) {
				for (ResourceAddressDto resourceAddressDtoResult : resourceAddressDtoList) {
					if (CodesConstant.CRSCADDR_01.equalsIgnoreCase(resourceAddressDtoResult.getAddType())) {
						facilityInvCnclsnResProcess.setResourceAddressDto(resourceAddressDtoResult);
						break;
					}
				}
			}
			// capture the primary address and create type 9 address for the
			// facility
			if (null != facilityInvCnclsnResProcess.getResourceAddressDto()) {
				facilityInvCnclsnResProcess.getResourceAddressDto().setAddType(CodesConstant.CRSCADDR_09);
				facilityInvCnclsnResProcess.getResourceAddressDto().setIdRsrcAddress(ServiceConstants.ZERO_VAL);

				ResourceAddressDto resourceAddressDtoInsert = facilityInvCnclsnDao
						.saveResourceAddressDtl(facilityInvCnclsnResProcess.getResourceAddressDto());

				facilityInvCnclsnDetailDto.setIdRsrcSiteAddress(resourceAddressDtoInsert.getIdRsrcAddress());
			}
			// create the type 8 address with the user entered detail from page
			if (facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto().getIdFacilResource() > ServiceConstants.ZERO_VAL) {
				if (null == invstMailAddressDto.getIdResource()) {
					invstMailAddressDto
							.setIdResource(facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto().getIdFacilResource());
				}

				invstMailAddressDto = facilityInvCnclsnDao.saveResourceAddressDtl(invstMailAddressDto);
				facilityInvCnclsnDetailDto.setIdRsrcMailAddress(invstMailAddressDto.getIdRsrcAddress());
			}

			facilityInvCnclsnDao.saveFacilityRsrcLinkDtl(facilityInvCnclsnDetailDto);

			crudOprStatus = ServiceConstants.INSERT_MSG;

		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(facilityInvCnclsnReq.getDataAction())) {

			// Set id Resource
			if (null != facilityInvCnclsnDetailDto) {
				invstMailAddressDto.setIdResource(facilityInvCnclsnDetailDto.getIdFacilResource());
			}

			facilityInvCnclsnDao.updateResourceAddressDtl(invstMailAddressDto);
			facilityInvCnclsnDao.updateFacilityRsrcLinkDtl(facilityInvCnclsnDetailDto);
			crudOprStatus = ServiceConstants.UPDATE_MSG;

		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equalsIgnoreCase(facilityInvCnclsnReq.getDataAction())) {
			facilityInvCnclsnDao.deleteFacilityRsrcLinkDtl(facilityInvCnclsnDetailDto);
			crudOprStatus = ServiceConstants.DELETE_MSG;
		}
		facilityInvCnclsnRes.setCrudOpreationStatus(crudOprStatus);
		log.info("TransactionId :" + facilityInvCnclsnReq.getTransactionId());
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Description: This method will assign and update the facility
	 * Overall Dispo as part of investigation.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes assignAndUpdateFacilOverallDispo(FacilityInvCnclsnReq facilityInvCnclsnReq) {

		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		String crudOprStatus = ServiceConstants.NOAUDDOP;
		Long idResourceLinkPrev = ServiceConstants.ZERO_VAL;
		Long idResourceLinkCurrent = ServiceConstants.ZERO_VAL;
		Long idResourceTmp = ServiceConstants.ZERO_VAL;
		List<String> dispoList = new ArrayList<String>();
		Long count = ServiceConstants.ZERO_VAL;
		facilityInvCnclsnDao.initializeFacilitiesOverallDispo(facilityInvCnclsnReq.getIdStage());

		List<FacilityAllegationDto> facilityAllegationDtoList = facilityInvCnclsnDao
				.getFacilityAllegationListDetails(facilityInvCnclsnReq.getIdStage());
		FacilityInvCnclsnRes facilityInvCnclsnResProcess = new FacilityInvCnclsnRes();
		for (FacilityAllegationDto facilityAllegationDto : facilityAllegationDtoList) {

			idResourceLinkCurrent = facilityAllegationDto.getIdFacilRsrcLink();
			if (idResourceTmp.equals(facilityAllegationDto.getIdFacilResource()) && count > ServiceConstants.ZERO_VAL) {
				idResourceTmp = facilityAllegationDto.getIdFacilResource();
				dispoList.add(facilityAllegationDto.getCdAllegDispo());
			} else if (count.equals(ServiceConstants.ZERO_VAL)) {
				idResourceTmp = facilityAllegationDto.getIdFacilResource();
				dispoList.add(facilityAllegationDto.getCdAllegDispo());
			} else {
				String overallDispo = calculateResourceLevelOverallDispo(dispoList);
				List<FacilityInvstRsrcLinkRsrcAddDto> facilityInvstRsrcLinkRsrcAddDtoList = facilityInvCnclsnDao
						.getInvstRcrcLinkDetails(idResourceLinkPrev);
				facilityInvCnclsnResProcess = populateFacilityAndResourceAddress(facilityInvCnclsnResProcess,
						facilityInvstRsrcLinkRsrcAddDtoList);
				if (facilityInvCnclsnResProcess.getFacilityInvCnclsnDetailDtoList().size() > 0) {
					for (FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto : facilityInvCnclsnResProcess
							.getFacilityInvCnclsnDetailDtoList()) {
						facilityInvCnclsnDetailDto.setCdFacilInvstOvrallDis(overallDispo);
						facilityInvCnclsnDao.updateFacilityRsrcLinkDtl(facilityInvCnclsnDetailDto);
						facilityInvCnclsnResProcess.setFacilityInvCnclsnDetailDto(facilityInvCnclsnDetailDto);
					}

				}
				dispoList = new ArrayList<String>();
				if (facilityAllegationDto.getIdFacilResource() != ServiceConstants.ZERO_VAL) {
					idResourceTmp = facilityAllegationDto.getIdFacilResource();
					dispoList.add(facilityAllegationDto.getCdAllegDispo());
				}
				crudOprStatus = ServiceConstants.UPDATE_MSG;
			}
			idResourceLinkPrev = idResourceLinkCurrent;
			count++;
		}
		if (!dispoList.isEmpty() && idResourceLinkPrev != 0) {
			String overallDispo = calculateResourceLevelOverallDispo(dispoList);
			List<FacilityInvstRsrcLinkRsrcAddDto> facilityInvstRsrcLinkRsrcAddDtoList = facilityInvCnclsnDao
					.getInvstRcrcLinkDetails(idResourceLinkPrev);
			facilityInvCnclsnResProcess = populateFacilityAndResourceAddress(facilityInvCnclsnResProcess,
					facilityInvstRsrcLinkRsrcAddDtoList);
			if (facilityInvCnclsnResProcess.getFacilityInvCnclsnDetailDtoList().size() > 0) {
				for (FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto : facilityInvCnclsnResProcess
						.getFacilityInvCnclsnDetailDtoList()) {
					facilityInvCnclsnDetailDto.setCdFacilInvstOvrallDis(overallDispo);
					facilityInvCnclsnDao.updateFacilityRsrcLinkDtl(facilityInvCnclsnDetailDto);
					facilityInvCnclsnResProcess.setFacilityInvCnclsnDetailDto(facilityInvCnclsnDetailDto);
				}

			}
			crudOprStatus = ServiceConstants.UPDATE_MSG;
		}
		facilityInvCnclsnRes.setCrudOpreationStatus(crudOprStatus);
		log.info("TransactionId :" + facilityInvCnclsnReq.getTransactionId());
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Description: This method will save the facility to the link table
	 * as part of investigation.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes deleteFacility(FacilityInvCnclsnReq facilityInvCnclsnReq) {

		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		String crudOprStatus = ServiceConstants.NOAUDDOP;
		if (!TypeConvUtil.isNullOrEmpty(
				TypeConvUtil.toLong(facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto().getIdFacilRsrcLink()))) {
			facilityInvCnclsnDao.deleteFacilityRsrcLinkDtl(facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto());
			crudOprStatus = ServiceConstants.DELETE_MSG;
		}
		if (facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto().getIdRsrcMailAddress() > ServiceConstants.ZERO_VAL) {
			facilityInvCnclsnDao.deleteResourceAddressDtl(
					facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto().getIdRsrcMailAddress());
			crudOprStatus = ServiceConstants.DELETE_MSG;
		}
		if (facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto().getIdRsrcSiteAddress() > ServiceConstants.ZERO_VAL) {
			facilityInvCnclsnDao.deleteResourceAddressDtl(
					facilityInvCnclsnReq.getFacilityInvCnclsnDetailDto().getIdRsrcSiteAddress());
			crudOprStatus = ServiceConstants.DELETE_MSG;
		}
		facilityInvCnclsnRes.setCrudOpreationStatus(crudOprStatus);
		log.info("TransactionId :" + facilityInvCnclsnReq.getTransactionId());
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Description: This method will verify any one of allegation is tied
	 * to one facility
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes facilityTiedToAllegation(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		Boolean allegationTied = ServiceConstants.TRUEVAL;
		List<FacilityInvProviderDto> facilityInvCnclsnDetailDtoList = facilityInvCnclsnDao
				.getInvestigatedFacilityList(facilityInvCnclsnReq.getIdStage());
		List<Long> idResourceList = facilityInvCnclsnDao.getAllegedFacilityIDs(facilityInvCnclsnReq.getIdStage());
		for (FacilityInvProviderDto facilityInvCnclsnDetailDto : facilityInvCnclsnDetailDtoList) {
			idResourceList.contains(facilityInvCnclsnDetailDto.getIdFacilResource());
			allegationTied = ServiceConstants.FALSEVAL;
		}
		facilityInvCnclsnRes.setAllegationTied(allegationTied);
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Description: This method will verify any one of allegation is tied
	 * to one facility
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes nameTiedToFacWithAllegOfCRC(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		Boolean allegationTied = ServiceConstants.TRUEVAL;
		List<FacilityAllegationDto> facilityAllegationDtoList = facilityInvCnclsnDao
				.getFacilityAllegationListDetails(facilityInvCnclsnReq.getIdStage());
		facilityInvCnclsnRes.setFacilityTiedWithAllegation(ServiceConstants.FALSEVAL);

		for (FacilityAllegationDto facilityAllegationDto : facilityAllegationDtoList) {
			if (TypeConvUtil.isNullOrEmpty(facilityAllegationDto.getCdFacAdminFName())
					|| facilityAllegationDto.getCdFacAdminFName().length() <= ServiceConstants.ZERO_SHORT) {
				allegationTied = ServiceConstants.FALSEVAL;
			}
			if (TypeConvUtil.isNullOrEmpty(facilityAllegationDto.getCdFacAdminLName())
					|| facilityAllegationDto.getCdFacAdminLName().length() <= ServiceConstants.ZERO_SHORT) {
				allegationTied = ServiceConstants.FALSEVAL;
			}

			// Check if facility resource id linked with allegation
			if (facilityAllegationDto.getIdFacilResource() == facilityInvCnclsnReq.getIdFacilityResource()) {
				facilityInvCnclsnRes.setFacilityTiedWithAllegation(ServiceConstants.TRUEVAL);
			}
		}
		facilityInvCnclsnRes.setAllegationTied(allegationTied);
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Name: comparePersonAndFacilityAddress Method Description:This
	 * method will compare the Person and Facility Address and return the
	 * boolean value
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters to fetch the
	 *            facility address and the victim addresses.
	 * @return FacilityInvCnclsnRes - This dto will hold the boolean indicator
	 *         to indicate if the victim address and facility address match.
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes comparePersonAndFacilityAddress(FacilityInvCnclsnReq facilityInvCnclsnReq) {

		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		Boolean addressMatched = false;
		// retrieve the facility address
		List<ResourceAddressDto> rsrcInvFacilPrimaryAddressList = facilityInvCnclsnDao
				.getInvFacilPrimaryAddressList(facilityInvCnclsnReq.getIdStage());
		// retrieve the victims addresses
		List<ResourceAddressDto> rsrcAllegedVictimsPrimaryAddressList = facilityInvCnclsnDao
				.getAllegedVictimsPrimaryAddressList(facilityInvCnclsnReq.getIdStage());
		int numPersonMatched = 0;
		int numPersons = rsrcAllegedVictimsPrimaryAddressList.size();
		if (!CollectionUtils.isEmpty(rsrcInvFacilPrimaryAddressList)
				&& !CollectionUtils.isEmpty(rsrcAllegedVictimsPrimaryAddressList)) {
			/*
			 * Compare the victim address and the facility address and increment
			 * the counter if the values match.
			 */
			for (ResourceAddressDto rsrcAllegedVictimsPrimaryAddress : rsrcAllegedVictimsPrimaryAddressList) {

				for (ResourceAddressDto rsrcInvFacilPrimaryAddress : rsrcInvFacilPrimaryAddressList) {
					if (getStringSafe(rsrcAllegedVictimsPrimaryAddress.getAddStreetLine1())
							.equalsIgnoreCase(getStringSafe(rsrcInvFacilPrimaryAddress.getAddStreetLine1()))
							&& getStringSafe(rsrcAllegedVictimsPrimaryAddress.getAddStreetLine2())
									.equalsIgnoreCase(getStringSafe(rsrcInvFacilPrimaryAddress.getAddStreetLine2()))
							&& getStringSafe(rsrcAllegedVictimsPrimaryAddress.getAddState())
									.equalsIgnoreCase(getStringSafe(rsrcInvFacilPrimaryAddress.getAddState()))
							&& getStringSafe(rsrcAllegedVictimsPrimaryAddress.getAddZip())
									.equalsIgnoreCase(getStringSafe(rsrcInvFacilPrimaryAddress.getAddZip()))
							&& getStringSafe(rsrcAllegedVictimsPrimaryAddress.getAddCounty())
									.equalsIgnoreCase(getStringSafe(rsrcInvFacilPrimaryAddress.getAddCounty()))) {
						numPersonMatched++;
						break;
					}

				}
			}
		}
		/*
		 * If the number of victims and the number of address matched are same
		 * then return true.
		 */
		addressMatched = (numPersons == numPersonMatched) ? true : false;
		facilityInvCnclsnRes.setAddressMatched(addressMatched);
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Name: getCurrentStagePriority Method Description:This method is
	 * used to retrieve the current priority of the stage.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameter for retrieving the
	 *            current priority of the stage.
	 * @return FacilityInvCnclsnRes -This dto will hold the list of providers in
	 *         the Facility Investigation conclusion .
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes getCurrentStagePriority(FacilityInvCnclsnReq facilityInvCnclsnReq) {

		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		/*
		 * Call the dao implementation to get the current priority for the
		 * stage.
		 */
		String currentStagePriority = facilityInvCnclsnDao.getCurrentPriority(facilityInvCnclsnReq.getIdStage());
		facilityInvCnclsnRes.setCurrentPriority(currentStagePriority);
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Name: getApprovalStatusInfo Method Description:This method is used
	 * to get the approval status of the Facility Investigation Conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters for retrieving the
	 *            approval status info.
	 * @return FacilityInvCnclsnRes - This dto will hold the approval status
	 *         details for the Facility Investigation Conclusion.
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes getApprovalStatusInfo(FacilityInvCnclsnReq facilityInvCnclsnReq) {

		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		/*
		 * Call the dao implementation to get the approval status for the
		 * facility conclusion.
		 */
		FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto = facilityInvCnclsnDao
				.getApprovalStatusInfo(facilityInvCnclsnReq.getIdEvent());
		facilityInvCnclsnRes.setFacilityInvCnclsnDetailDto(facilityInvCnclsnDetailDto);
		return facilityInvCnclsnRes;

	}

	/**
	 * Method Name: getProgramAdmins Method Description:This method is used to
	 * get the Program Admins for the New EMR Investigation conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters for retrieving the
	 *            Program Admin map .
	 * @return FacilityInvCnclsnRes - This dto will hold the Program Admins map
	 *         for the New EMR Investigation conclusion.
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes getProgramAdminsDtls(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		Map<Long, String> programAdminMap = new LinkedHashMap<Long, String>();
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		/*
		 * If the idProgramAdmin is null , then passing 0 to fetch the list of
		 * program admins.
		 */
		List<ProgramAdminDto> programAdminList = facilityInvCnclsnDao.getProgramAdmins(
				!ObjectUtils.isEmpty(facilityInvCnclsnReq.getIdPerson()) ? facilityInvCnclsnReq.getIdPerson() : 0l);
		/*
		 * If the list of program admin is not empty , then iterating the list
		 * and forming the map with the idPerson as key and full name as value.
		 */
		if (!CollectionUtils.isEmpty(programAdminList)) {
			for (ProgramAdminDto programAdminDto : programAdminList) {
				programAdminMap.put(programAdminDto.getIdPerson(),
						programAdminDto.getLastName() + " , " + programAdminDto.getFirstName());
			}
		}

		facilityInvCnclsnRes.setProgramAdminMap(programAdminMap);

		return facilityInvCnclsnRes;

	}

	/**
	 * Method Description: This method will will return the boolean as true if
	 * the linked facility type is Community Provider.
	 *
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes getLinkedFacilityCommunityProvider(FacilityInvCnclsnReq facilityInvCnclsnReq) {

		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		boolean linkedFacilityCommunnity = false;
		List<FacilityInvProviderDto> facilityInvCnclsnDetailDtoList = facilityInvCnclsnDao
				.getInvestigatedFacilityList(facilityInvCnclsnReq.getIdStage());
		if (!CollectionUtils.isEmpty(facilityInvCnclsnDetailDtoList)) {
			linkedFacilityCommunnity = facilityInvCnclsnDetailDtoList.stream()
					.anyMatch(facilityInvProviderDto -> (CodesConstant.CFACTYP2_CP
							.equalsIgnoreCase(facilityInvProviderDto.getCdRsrcFacilType())
							|| CodesConstant.CFACTYP2_BH.equalsIgnoreCase(facilityInvProviderDto.getCdRsrcFacilType())
							|| CodesConstant.CFACTYP2_HC.equalsIgnoreCase(facilityInvProviderDto.getCdRsrcFacilType())
							|| CodesConstant.CFACTYP2_MC.equalsIgnoreCase(facilityInvProviderDto.getCdRsrcFacilType())
							|| CodesConstant.CFACTYP2_CD.equalsIgnoreCase(facilityInvProviderDto.getCdRsrcFacilType())
							|| CodesConstant.CFACTYP2_LS
									.equalsIgnoreCase(facilityInvProviderDto.getCdRsrcFacilType())));
		}

		facilityInvCnclsnRes.setLinkedFacilityCommunnity(linkedFacilityCommunnity);
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Description: This method will invokes the dao method
	 * getPersMedicaidMissingInd which returns a map of person id keys and an
	 * indicator (Y or N) for each person indicating if Medicaid identifier is
	 * missing. This methods return true if there is one value in the map that
	 * equals to 'Y' indicating that there is at least one person without an
	 * active Medicaid identifier
	 *
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FacilityInvCnclsnRes getMedicaidIdentifierMissing(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		boolean medicaidIdentifier = false;
		Map<Long, String> victimMedMissingMap = facilityInvCnclsnDao
				.getPersMedicaidMissingInd(facilityInvCnclsnReq.getIdStage());
		if (!CollectionUtils.isEmpty(victimMedMissingMap) && victimMedMissingMap.containsValue(ServiceConstants.YES)) {

			medicaidIdentifier = true;
		}
		facilityInvCnclsnRes.setMedicaidIdentifier(medicaidIdentifier);
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Description: This method is will calculate the facility level
	 * overall disposition for the AFC facilities
	 * 
	 * @param dispoList
	 * @return String
	 */
	private String calculateResourceLevelOverallDispo(List<String> dispoList) {
		String disp = "";
		for (String dispo : dispoList) {
			if (CodesConstant.CCONFIRM_CON.equalsIgnoreCase(dispo))
				return dispo;
			if (CodesConstant.CCONFIRM_CRC.equalsIgnoreCase(dispo))
				return CodesConstant.CCONFIRM_CON;
			if (CodesConstant.CCONFIRM_COU.equalsIgnoreCase(dispo))
				return dispo;
			if (CodesConstant.CCONFIRM_INC.equalsIgnoreCase(dispo))
				return dispo;
			if (CodesConstant.CCONFIRM_UNF.equalsIgnoreCase(dispo))
				return dispo;
			if (ServiceConstants.CCONFIRM_XXX.equalsIgnoreCase(dispo))
				return dispo;
			if (CodesConstant.CCONFIRM_ZZZ.equalsIgnoreCase(dispo))
				return dispo;
		}
		return disp;
	}

	/**
	 * Method Description: This method is used to populate and set the data to
	 * Facility and Resource Address Dto.
	 * 
	 * @param facilityInvCnclsnRes
	 * @param facilityInvstRsrcLinkRsrcAddDtoList
	 * @return FacilityInvCnclsnRes
	 */
	private FacilityInvCnclsnRes populateFacilityAndResourceAddress(FacilityInvCnclsnRes facilityInvCnclsnRes,
			List<FacilityInvstRsrcLinkRsrcAddDto> facilityInvstRsrcLinkRsrcAddDtoList) {

		List<FacilityInvCnclsnDetailDto> facilityInvCnclsnDetailDtoList = new ArrayList<FacilityInvCnclsnDetailDto>();
		List<ResourceAddressDto> resourceAddressDtoList = new ArrayList<ResourceAddressDto>();

		for (FacilityInvstRsrcLinkRsrcAddDto facilityInvstRsrcLinkRsrcAddDto : facilityInvstRsrcLinkRsrcAddDtoList) {

			FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto = new FacilityInvCnclsnDetailDto();

			facilityInvCnclsnDetailDto.setCdMhmrCompCode(facilityInvstRsrcLinkRsrcAddDto.getCdMhmrCompCode());
			facilityInvCnclsnDetailDto.setDtLastUpdated(facilityInvstRsrcLinkRsrcAddDto.getDtLastUpdated());
			facilityInvCnclsnDetailDto
					.setCdFacilInvstOvrallDis(facilityInvstRsrcLinkRsrcAddDto.getCdFacilInvstOvrallDis());
			facilityInvCnclsnDetailDto
					.setNmFacilInvstFacility(facilityInvstRsrcLinkRsrcAddDto.getNmFacilInvstFacility());
			facilityInvCnclsnDetailDto.setIdCase(facilityInvstRsrcLinkRsrcAddDto.getIdCase());
			facilityInvCnclsnDetailDto.setIdEvent(facilityInvstRsrcLinkRsrcAddDto.getIdEvent());
			facilityInvCnclsnDetailDto.setIdStage(facilityInvstRsrcLinkRsrcAddDto.getIdStage());
			facilityInvCnclsnDetailDto.setIdFacilRsrcLink(facilityInvstRsrcLinkRsrcAddDto.getIdFacilRsrcLink());
			facilityInvCnclsnDetailDto.setIdFacilResource(facilityInvstRsrcLinkRsrcAddDto.getIdFacilResource());
			facilityInvCnclsnDetailDto.setIdRsrcMailAddress(facilityInvstRsrcLinkRsrcAddDto.getIdRsrcMailAddress());
			facilityInvCnclsnDetailDto.setIdRsrcSiteAddress(facilityInvstRsrcLinkRsrcAddDto.getIdRsrcSiteAddress());
			facilityInvCnclsnDetailDto.setPhoneNumber(facilityInvstRsrcLinkRsrcAddDto.getPhoneNumber());
			facilityInvCnclsnDetailDto.setNameFirst(facilityInvstRsrcLinkRsrcAddDto.getNameFirst());
			facilityInvCnclsnDetailDto.setNameMiddle(facilityInvstRsrcLinkRsrcAddDto.getNameMiddle());
			facilityInvCnclsnDetailDto.setNameLast(facilityInvstRsrcLinkRsrcAddDto.getNameLast());

			facilityInvCnclsnDetailDtoList.add(facilityInvCnclsnDetailDto);

			ResourceAddressDto resourceAddressDto = new ResourceAddressDto();
			resourceAddressDto.setIdResource(facilityInvstRsrcLinkRsrcAddDto.getIdResource());
			resourceAddressDto.setIdRsrcAddress(facilityInvstRsrcLinkRsrcAddDto.getIdRsrcAddress());
			resourceAddressDto.setAddStreetLine1(facilityInvstRsrcLinkRsrcAddDto.getAddStreetLine1());
			resourceAddressDto.setAddStreetLine2(facilityInvstRsrcLinkRsrcAddDto.getAddStreetLine2());
			resourceAddressDto.setAddCity(facilityInvstRsrcLinkRsrcAddDto.getAddCity());
			resourceAddressDto.setAddState(facilityInvstRsrcLinkRsrcAddDto.getAddState());
			resourceAddressDto.setAddZip(facilityInvstRsrcLinkRsrcAddDto.getAddZip());
			resourceAddressDto.setAddCounty(facilityInvstRsrcLinkRsrcAddDto.getAddCounty());
			resourceAddressDto.setAddType(facilityInvstRsrcLinkRsrcAddDto.getAddType());
			resourceAddressDto.setAddComments(facilityInvstRsrcLinkRsrcAddDto.getAddComments());
			resourceAddressDto.setNmCnty(facilityInvstRsrcLinkRsrcAddDto.getNmCnty());
			resourceAddressDto.setNmCntry(facilityInvstRsrcLinkRsrcAddDto.getNmCntry());
			resourceAddressDto.setCdGcdRtrn(facilityInvstRsrcLinkRsrcAddDto.getCdGcdRtrn());
			resourceAddressDto.setCdAddrRtrn(facilityInvstRsrcLinkRsrcAddDto.getCdAddrRtrn());
			resourceAddressDto.setNbrGcdLat(facilityInvstRsrcLinkRsrcAddDto.getNbrGcdLat());
			resourceAddressDto.setNbrGcdLong(facilityInvstRsrcLinkRsrcAddDto.getNbrGcdLong());
			resourceAddressDto.setTxtMailbltyScore(facilityInvstRsrcLinkRsrcAddDto.getTxtMailbltyScore());
			resourceAddressDto.setIndValdtd(facilityInvstRsrcLinkRsrcAddDto.getIndValdtd());
			resourceAddressDto.setDtValdtd(facilityInvstRsrcLinkRsrcAddDto.getDtValdtd());

			resourceAddressDtoList.add(resourceAddressDto);
		}
		facilityInvCnclsnRes.setFacilityInvCnclsnDetailDtoList(facilityInvCnclsnDetailDtoList);
		facilityInvCnclsnRes.setResourceAddressDtoList(resourceAddressDtoList);
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Name: getReportableConductExists Method Description:This method is
	 * used to check if Reportable Conduct exists for the current stage in the
	 * list of Facilities in the Facility Investigation Conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters for checking if a
	 *            reportable conduct exists.
	 * @return FacilityInvCnclsnRes - This dto will hold the boolean indicator
	 *         for reportable conduct exists.
	 */
	@Override
	public FacilityInvCnclsnRes getReportableConductExists(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		/*
		 * Call the dao implementation to check if reportable conduct exists and
		 * setting the boolean indicator in the response.
		 */
		boolean indReportableConductExists = facilityInvCnclsnDao
				.getReportableConductExists(facilityInvCnclsnReq.getIdStage());
		facilityInvCnclsnRes.setIndReportableConductExists(indReportableConductExists);
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Name: getFacilityInvCnclsn Method Description:This method is used
	 * to get the Facility Investigation conclusion details for the stage.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameter values for retrieving
	 *            the Facility Conclusion details.
	 * @return FacilityInvCnclsnRes - This dto will hold the values of Facility
	 *         Conclusion details.
	 */
	@Override
	public FacilityInvCnclsnRes getFacilityInvCnclsn(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		String tableName = null;
		FacilityInvDetailFetchDto facilityInvDetailFetchDto = new FacilityInvDetailFetchDto();
		Map<String, Date> blobLastUpdateList = new LinkedHashMap<String, Date>();
		Map<String, String> blobExistsList = new LinkedHashMap<String, String>();
		PriorStageDto priorStageDto = new PriorStageDto();
		boolean indLeNotif = false;
		// If the event is already then get the last update date of the event
		if (!ObjectUtils.isEmpty(facilityInvCnclsnReq.getIdEvent()) && 0 != facilityInvCnclsnReq.getIdEvent()) {

			getFacilityInvCnclsnEventDetails(facilityInvDetailFetchDto, facilityInvCnclsnReq.getIdEvent());
		}

		/*
		 * Check if the stage was created after the Enhanced APS facility
		 * Investigation Conclusion
		 */
		SelectStageDto selectStageDto = new SelectStageDto();
		selectStageDto = caseSummaryDao.getStage(facilityInvCnclsnReq.getIdStage(), ServiceConstants.STAGE_CURRENT);

		if (selectStageDto.getDtStartDate().compareTo(ENHANCED_APS_FAC_INV_DATE) >= 1) {
			facilityInvDetailFetchDto.setProviderList(getInvestigatedFacilitiesList(facilityInvCnclsnReq.getIdStage()));

		}

		// get the facility investigation details
		getFacilityInvestigationDetails(facilityInvDetailFetchDto, facilityInvCnclsnReq.getIdStage());

		if (((!ObjectUtils.isEmpty(facilityInvDetailFetchDto.getFacilInvDtlDto().getIdFacilResource())
				&& facilityInvDetailFetchDto.getFacilInvDtlDto().getIdFacilResource().equals(0l))
				|| ObjectUtils.isEmpty(facilityInvDetailFetchDto.getFacilInvDtlDto().getIdFacilResource()))
				|| (ObjectUtils.isEmpty(facilityInvDetailFetchDto.getFacilInvDtlDto().getNmFacilinvstFacility()))) {
			priorStageDto = notifToLawEnforcementDao.getPriorStagebyId(facilityInvCnclsnReq.getIdStage());
			// get the Facility details from the INCOMING_FACILITY table
			getIncomingFacilityDetails(facilityInvDetailFetchDto, priorStageDto);

		}
		/*
		 * If the Facility type and Mmhr code is null from the retrieve of
		 * facility investigation details then populating the values from
		 * RESOURCE table.
		 */
		if (!ObjectUtils.isEmpty(facilityInvDetailFetchDto.getFacilInvDtlDto().getIdFacilResource())
				&& 0 != facilityInvDetailFetchDto.getFacilInvDtlDto().getIdFacilResource()
				&& (ObjectUtils.isEmpty(facilityInvDetailFetchDto.getFacilInvDtlDto().getCdMhmrCompCode())
						|| ObjectUtils.isEmpty(facilityInvDetailFetchDto.getFacilInvDtlDto().getCdRsrcFacilType()))) {

			getMmhrCodeAndResrscType(facilityInvDetailFetchDto);

		}
		/*
		 * Check if a Request for Review ** contact was recorded.
		 */
		if (!ObjectUtils.isEmpty(facilityInvSumDao.getContactDate(facilityInvCnclsnReq.getIdStage()))) {
			facilityInvDetailFetchDto.getFacilInvDtlDto().setIdReviewContact(1l);
		}

		// Check if narrative blobs exist in database for forms
		tableName = APS_FACILITY_NARRATIVE_VIEW;
		indLeNotif = checkNarrativeExists(facilityInvDetailFetchDto, facilityInvCnclsnReq, blobLastUpdateList,
				blobExistsList, tableName, indLeNotif);
		tableName = FACIL_CONCL_NARR_VIEW;
		indLeNotif = checkNarrativeExists(facilityInvDetailFetchDto, facilityInvCnclsnReq, blobLastUpdateList,
				blobExistsList, tableName, indLeNotif);

		tableName = APS_FACIL_REVIEW_NARR_VIEW;
		indLeNotif = checkNarrativeExists(facilityInvDetailFetchDto, facilityInvCnclsnReq, blobLastUpdateList,
				blobExistsList, tableName, indLeNotif);
		tableName = FACIL_CONCL_REVIEW_NARR_VIEW;
		indLeNotif = checkNarrativeExists(facilityInvDetailFetchDto, facilityInvCnclsnReq, blobLastUpdateList,
				blobExistsList, tableName, indLeNotif);
		tableName = FAC_REFERRAL_NARR;
		indLeNotif = checkNarrativeExists(facilityInvDetailFetchDto, facilityInvCnclsnReq, blobLastUpdateList,
				blobExistsList, tableName, indLeNotif);

		// get the referral contact if exists
		Long idReferralContact = getReferralContactEvent(CodesConstant.CCNTCTYP_EFER, null,
				facilityInvCnclsnReq.getIdStage());
		facilityInvDetailFetchDto.setIdReferralContact(idReferralContact);

		// get the ECOM contact if exists
		Long idECOMContact = getReferralContactEvent(CodesConstant.CCNTCTYP_ECOM, null,
				facilityInvCnclsnReq.getIdStage());
		facilityInvDetailFetchDto.setIdEcomContact(idECOMContact);
		tableName = AFC_INV_NARR;
		indLeNotif = checkNarrativeExists(facilityInvDetailFetchDto, facilityInvCnclsnReq, blobLastUpdateList,
				blobExistsList, tableName, indLeNotif);

		// get the oig contact if exists
		Long idOigContact = getReferralContactEvent(CodesConstant.CCNTCTYP_ENOT, CodesConstant.COTHCNCT_COIG,
				facilityInvCnclsnReq.getIdStage());
		facilityInvDetailFetchDto.setIdContactEvent(idOigContact);
		tableName = AFC_INV_NARR2;
		indLeNotif = checkNarrativeExists(facilityInvDetailFetchDto, facilityInvCnclsnReq, blobLastUpdateList,
				blobExistsList, tableName, indLeNotif);

		// get the stage details
		getFacilityInvCnclsnStageDetails(facilityInvDetailFetchDto, facilityInvCnclsnReq.getIdStage());

		//artf159190 : ALM ID : 15242-- as long as the stage is open, save the latest Face To Face Contact date to DT_FACIL_INVST_BEGUN
		if (ObjectUtils.isEmpty(facilityInvDetailFetchDto.getFacilityInvStageDetailsDto().getDtStageClose())) {
			getFaceToFaceContactDetails(facilityInvDetailFetchDto, facilityInvCnclsnReq.getIdStage());
		}

		// get the facility allegation incident occurred date
		getFacilityAllegationIncidentOccuredDate(facilityInvDetailFetchDto, facilityInvCnclsnReq.getIdStage());

		// get facility_injury details
		getFacilityInjuryDetails(facilityInvDetailFetchDto, facilityInvCnclsnReq.getIdStage());

		facilityInvDetailFetchDto.setIdPriorStage(getIntakeStage(facilityInvCnclsnReq.getIdStage()));

		facilityInvDetailFetchDto.getFacilityInvEventDto().setIndMhmrClientNum(ServiceConstants.N);
		facilityInvDetailFetchDto.getFacilityInvEventDto().setIndVerMhmrClientNum(ServiceConstants.N);

		/*
		 * get all person ids and search indicators to determine if any
		 * principals are involved.
		 */
		getVictimsForStage(facilityInvDetailFetchDto, facilityInvCnclsnReq.getIdStage());
		if (!CollectionUtils.isEmpty(facilityInvDetailFetchDto.getBlobLastUpdateList())) {
			facilityInvDetailFetchDto.setBlobLastUpdateListJson(
					jsonUtil.objectToJsonString(facilityInvDetailFetchDto.getBlobLastUpdateList()));
		}

		facilityInvCnclsnRes.setFacilityInvDetailFetchDto(facilityInvDetailFetchDto);
		return facilityInvCnclsnRes;
	}

	/**
	 * Method Name: getVictimsForStage Method Description:This method is used to
	 * get all the victims in the stage
	 * 
	 * @param facilityInvDetailFetchDto
	 * @param idStage
	 */
	private void getVictimsForStage(FacilityInvDetailFetchDto facilityInvDetailFetchDto, Long idStage) {
		List<StagePersonLinkDto> principalsList = facilityInvCnclsnDao.getVictimsForStage(idStage);
		for (StagePersonLinkDto stagePersonLinkDto : principalsList) {
			if (!ServiceConstants.Y.equals(facilityInvDetailFetchDto.getFacilityInvEventDto().getIndMhmrClientNum())) {

				/*
				 ** Call DAO to determine if the victims retrieved by
				 * 'getVictimsForStage' method have an MHMR Client Number. The
				 * no MHMR Client Number flag will be set to TRUE in the CallDAO
				 * function if a victim is found that has no MHMR Client Number.
				 */
				checkMHMRNumberExists(facilityInvDetailFetchDto, stagePersonLinkDto.getIdPerson());
				/*
				 * ** If the Injury Allegation is of Type 'Serious' and ** the
				 * Date of Determination is not NULL then set the ** flag to
				 * indicate that the stage can be closed.
				 */
				if (RELATED.equals(stagePersonLinkDto.getCdStagePersSearchInd())) {
					facilityInvDetailFetchDto.getFacilityInvEventDto().setIndVerMhmrClientNum(ServiceConstants.Y);
				} else if (!ServiceConstants.Y
						.equals(facilityInvDetailFetchDto.getFacilityInvEventDto().getIndVerMhmrClientNum())) {
					/*
					 ** Call DAO to check if the victim has been merged. The
					 * person merged/related flag will be set to TRUE in the
					 * CallDAM function if the person has been merged.
					 */
					if (facilityInvCnclsnDao.getMergeCount(stagePersonLinkDto.getIdPerson()) > 0l) {
						facilityInvDetailFetchDto.getFacilityInvEventDto().setIndVerMhmrClientNum(ServiceConstants.Y);
					}

				}
			}
		}

	}

	/**
	 * Method Name: checkMHMRNumberExists Method Description:
	 * 
	 * @param facilityInvDetailFetchDto
	 * @param idPerson
	 */
	private void checkMHMRNumberExists(FacilityInvDetailFetchDto facilityInvDetailFetchDto, Long idPerson) {
		RetrvPersonIdentifiersReq retrvPersonIdentifiersReq = new RetrvPersonIdentifiersReq();
		retrvPersonIdentifiersReq.setIdPerson(idPerson);
		retrvPersonIdentifiersReq.setIdType(CodesConstant.CNUMTYPE_MHMR_CLIENT_NUMBER);
		List<PersonIdentifiersDto> personIdentifiersDtosList = personIdDao
				.getPersonIdentifierByIdType(retrvPersonIdentifiersReq);

		if (CollectionUtils.isEmpty(personIdentifiersDtosList)) {
			/*
			 ** 
			 * No MHMR Client Number was found, so check to see if the person's
			 * name is unknown by calling the following dam.
			 */
			Person person = personDao.getPerson(idPerson);
			if (!ObjectUtils.isEmpty(person) && !ObjectUtils.isEmpty(person.getNmPersonFirst())
					&& !ObjectUtils.isEmpty(person.getNmPersonLast())) {
				facilityInvDetailFetchDto.getFacilityInvEventDto().setIndMhmrClientNum(ServiceConstants.Y);
			}
		}
	}

	/**
	 * Method Name: getIntakeStage Method Description:This method is used to get
	 * the id of the Intake stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return Long - The id of the prior stage.
	 */
	private Long getIntakeStage(Long idStage) {

		return stageDao.getEarliestIntakeDates(idStage).getIdPriorStage();
	}

	/**
	 * Method Name: getFacilityInjuryDetails Method Description:This method is
	 * used to get the facility injury details
	 * 
	 * @param facilityInvDetailFetchDto
	 *            - This dto will hold the Facility Conclusion details.
	 * @param idStage
	 *            - The id of the stage.
	 */
	private void getFacilityInjuryDetails(FacilityInvDetailFetchDto facilityInvDetailFetchDto, Long idStage) {
		boolean indValid = false;
		boolean indNoDateDetermination = false;
		boolean indNoInjuryDetail = false;
		List<FacilAllegInjuryDto> injuryAllegationList = facilityInvCnclsnDao.getFacilityInjuryDetails(idStage);
		if (!CollectionUtils.isEmpty(injuryAllegationList)) {
			for (FacilAllegInjuryDto facilAllegInjuryDto : injuryAllegationList) {

				if (CodesConstant.CDGROINJ_CC1.equals(facilAllegInjuryDto.getCdFacilInjuryType())
						&& !ObjectUtils.isEmpty(facilAllegInjuryDto.getDtFacilInjuryDtrmntn())) {
					indValid = true;
					break;
				}
				/*
				 * ** If the Injury Allegation is of Type 'Serious' and the **
				 * Date of Determination is NULL, set flag to indicate that ** a
				 * Date of Determination is required for this closure reason.
				 */
				else if (CodesConstant.CDGROINJ_CC1.equals(facilAllegInjuryDto.getCdFacilInjuryType())
						&& !ObjectUtils.isEmpty(facilAllegInjuryDto.getIdFacilityInjury())
						&& facilAllegInjuryDto.getIdFacilityInjury() != 0l
						&& ObjectUtils.isEmpty(facilAllegInjuryDto.getDtFacilInjuryDtrmntn())) {
					indNoDateDetermination = true;
				}
				/*
				 * ** If there is a Serious Allegation without an Injury Detail
				 * record ** set the flag to indicate that an Injury Detail
				 * Record is ** required for this closure reason
				 */
				else if (CodesConstant.CDGROINJ_CC1.equals(facilAllegInjuryDto.getCdFacilInjuryType())
						&& ObjectUtils.isEmpty(facilAllegInjuryDto.getIdFacilityInjury())) {
					indNoInjuryDetail = true;
				}

			}
		}

		if (indValid) {
			facilityInvDetailFetchDto.getFacilityInvEventDto().setCdSerInjAlleg(FC_ALL_COND_MET);
		} else if (indNoDateDetermination) {
			facilityInvDetailFetchDto.getFacilityInvEventDto().setCdSerInjAlleg(FC_NO_DATE);
		} else if (indNoInjuryDetail) {
			facilityInvDetailFetchDto.getFacilityInvEventDto().setCdSerInjAlleg(FC_NO_INJ_LST_DETAIL);
		} else {
			facilityInvDetailFetchDto.getFacilityInvEventDto().setCdSerInjAlleg(FC_NO_ALLEG_SER);
		}
	}

	/**
	 * Method Name: getFacilityAllegationIncidentOccuredDate Method
	 * Description:This method is used to get the facility allegation incident
	 * occurrence date
	 * 
	 * @param facilityInvDetailFetchDto
	 *            - This dto will hold the Facility Conclusion details.
	 * @param idStage
	 *            - The id of the stage.
	 */
	private void getFacilityAllegationIncidentOccuredDate(FacilityInvDetailFetchDto facilityInvDetailFetchDto,
			Long idStage) {
		/*
		 * Getting the minimum date of of facility allegation from the list of
		 * facility allegations for the stage.
		 */
		Date minFacilAllegationDate = facilityInvCnclsnDao.getMinDateFacilityAllegation(idStage);
		// If the date is not empty , setting the value in the dto to be shown
		// on
		// screen.
		if (!ObjectUtils.isEmpty(minFacilAllegationDate)) {
			facilityInvDetailFetchDto.getFacilInvDtlDto().setDtFacilInvstIncident(minFacilAllegationDate);
			String timeFiled = DateUtils
					.getTime(facilityInvDetailFetchDto.getFacilInvDtlDto().getDtFacilInvstIncident());
			if (!(timeFiled.contains(TWELVE_PM) || timeFiled.contains(TWELVE_AM))) {
				facilityInvDetailFetchDto.getFacilInvDtlDto().setTmFacilInvstInc(timeFiled);
			}
		}

	}

	/**
	 * Method Name: getFaceToFaceContactDetails Method Description:This method
	 * is used to get the face to face contact detail start date
	 * 
	 * @param facilityInvDetailFetchDto
	 *            - This dto will hold the Facility Conclusion details.
	 * @param idStage
	 *            - The id of the stage.
	 */
	private void getFaceToFaceContactDetails(FacilityInvDetailFetchDto facilityInvDetailFetchDto, Long idStage) {
		ContactDto contactDto = new ContactDto();
		//Defect#12755 - removed code to set the contact type
		contactDto.setCdContactPurpose(CodesConstant.CCNTPURP_AAMT);
		contactDto.setIdContactStage(idStage);
		ContactDto outpuContactDto = facilityInvCnclsnDao.getFaceToFaceContact(contactDto);
		if (!ObjectUtils.isEmpty(outpuContactDto) && !ObjectUtils.isEmpty(outpuContactDto.getDtContactOccurred())) {
			facilityInvDetailFetchDto.getFacilInvDtlDto().setDtFacilInvstBegun(outpuContactDto.getDtContactOccurred());
			facilityInvDetailFetchDto.getFacilInvDtlDto().setTmFacilInvstBeg(
					DateUtils.getTime(facilityInvDetailFetchDto.getFacilInvDtlDto().getDtFacilInvstBegun()));
		}
	}

	/**
	 * Method Name: getFacilityInvCnclsnStageDetails Method Description:This
	 * method is used to populate the stage details for the facility inv
	 * conclusion details.
	 * 
	 * @param facilityInvDetailFetchDto
	 *            - This dto will hold the Facility Conclusion details.
	 * @param idStage
	 *            - The id of the stage.
	 */
	private void getFacilityInvCnclsnStageDetails(FacilityInvDetailFetchDto facilityInvDetailFetchDto, Long idStage) {
		FacilityInvStageDetailsDto facilityInvStageDetailsDto = new FacilityInvStageDetailsDto();
		// Calling the dao implementation to get the stage details.
		StageDto stageDto = stageDao.getStageById(idStage);
		BeanUtils.copyProperties(stageDto, facilityInvStageDetailsDto);
		facilityInvDetailFetchDto.setFacilityInvStageDetailsDto(facilityInvStageDetailsDto);
	}

	/**
	 * Method Name: getReferralContactEvent Method Description:This method is
	 * used to get the id of the type of contact which is passed as a method
	 * paramter
	 * 
	 * @param cdContactType
	 *            - The value for the type of contact.
	 * @param otherContactType
	 *            - The value for other type of contact.
	 * @param idStage
	 *            - The id of the stage.
	 * @return idContactEvent - The id for the contact retrieved based on the
	 *         input parameters passed.
	 */
	private Long getReferralContactEvent(String cdContactType, String otherContactType, Long idStage) {
		Long idContactEvent = null;
		ContactSearchDto contactSearchDto = new ContactSearchDto();
		contactSearchDto.setSzCdContactType(cdContactType);
		contactSearchDto.setUlIdStage(idStage);
		if (!ObjectUtils.isEmpty(otherContactType)) {
			contactSearchDto.setSzCdContactOthers(otherContactType);
		}
		try {
			// Call the dao implementation to retrieve the contact based on the
			// contact type
			// and stage
			idContactEvent = contactSearchDao.searchContacts(contactSearchDto).getContactDetailSearchDto()
					.getContactPurposeDtos().get(0).getIdEvent();

		} catch (Exception exception) {
			// If no contacts are found , then setting the id to 0.
			idContactEvent = 0l;
		}
		return idContactEvent;
	}

	/**
	 * Method Name: checkNarrativeExists Method Description:This method checks
	 * if a particular type of form blob object exists in the database
	 * 
	 * @param blobExistsList
	 * @param blobLastUpdateList
	 * @param facilityInvDetailFetchDto
	 * @param indLeNotif
	 * @param tableName
	 * @param blobExistsList
	 * @param blobLastUpdateList
	 * @param facilityInvCnclsnReq
	 */
	private boolean checkNarrativeExists(FacilityInvDetailFetchDto facilityInvDetailFetchDto,
			FacilityInvCnclsnReq facilityInvCnclsnReq, Map<String, Date> blobLastUpdateList,
			Map<String, String> blobExistsList, String tableName, boolean indLeNotif) {
		String keyValue = null;
		Long idEvent = facilityInvCnclsnReq.getIdStage();
		if (APS_FACILITY_NARRATIVE_VIEW.equals(tableName)) {
			keyValue = FACILITIES_ABUSE_NEGLECT_KEY;
		}

		else if (APS_FACIL_REVIEW_NARR_VIEW.equals(tableName)) {
			keyValue = REVIEW_FACILITIES_ABUSE_NEGLECT_KEY;
		}

		else if (FACIL_CONCL_NARR_VIEW.equals(tableName)) {
			keyValue = FACILITIES_INVST_CONCL_KEY;
		}

		else if (FACIL_CONCL_REVIEW_NARR_VIEW.equals(tableName)) {
			keyValue = REVIEW_FACILITIES_INVST_CONCL_KEY;
		} else if (FAC_REFERRAL_NARR.equals(tableName)) {
			keyValue = FAC_REFERRAL_KEY;
		} else if (AFC_INV_NARR.equals(tableName)) {
			keyValue = AFC_INV_KEY;

			idEvent = facilityInvCnclsnReq.getIdEvent();

		} else if (AFC_INV_NARR2.equals(tableName)) {

			keyValue = AFC_INV_OIG_KEY;
			tableName = new String(AFC_INV_NARR);
			idEvent = facilityInvDetailFetchDto.getIdContactEvent();

		}
		ServiceDeliveryRtrvDtlsInDto serviceDeliveryRtrvDtlsInDto = new ServiceDeliveryRtrvDtlsInDto();
		serviceDeliveryRtrvDtlsInDto.setIdEvent(idEvent);
		serviceDeliveryRtrvDtlsInDto.setSysTxtTablename(tableName);
		/*
		 * Calling the dao to get the list of narratives for the particular
		 * conclusion event. If any results are returned from the dao call ,
		 * then setting Y or else setting N.
		 */
		List<ServiceDeliveryRtrvDtlsOutDto> narrativeList = serviceDeliveryRtrvDtlsDao
				.getNarrExists(serviceDeliveryRtrvDtlsInDto);
		if (!CollectionUtils.isEmpty(narrativeList)) {
			blobExistsList.put(keyValue, ServiceConstants.Y);
			int size = narrativeList.size();
			blobLastUpdateList.put(keyValue, narrativeList.get((size == 0) ? size : size - 1).getDtLastUpdate());
		} else {
			blobExistsList.put(keyValue, ServiceConstants.N);
		}

		facilityInvDetailFetchDto.setBlobExistsList(blobExistsList);
		facilityInvDetailFetchDto.setBlobLastUpdateList(blobLastUpdateList);
		if (!indLeNotif) {
			tableName = APS_NOTIF_LE_NARR;
			serviceDeliveryRtrvDtlsInDto.setSysTxtTablename(tableName);
			narrativeList = serviceDeliveryRtrvDtlsDao.getNarrExists(serviceDeliveryRtrvDtlsInDto);
			if (!CollectionUtils.isEmpty(narrativeList)) {
				blobExistsList.put(NOTICE_LE_KEY, ServiceConstants.Y);
				int size = narrativeList.size();
				blobLastUpdateList.put(NOTICE_LE_KEY,
						narrativeList.get((size == 0) ? size : size - 1).getDtLastUpdate());
			} else {
				blobExistsList.put(NOTICE_LE_KEY, ServiceConstants.N);
			}
		}
		facilityInvDetailFetchDto.setBlobExistsList(blobExistsList);
		facilityInvDetailFetchDto.setBlobLastUpdateList(blobLastUpdateList);
		return indLeNotif;

	}

	/**
	 * Method Name: getMmhrCodeAndResrscType Method Description:This method is
	 * used to populate the resource type and Mmhr code from CAPS_RESOURCE
	 * table.
	 * 
	 * @param facilityInvDetailFetchDto
	 */
	private void getMmhrCodeAndResrscType(FacilityInvDetailFetchDto facilityInvDetailFetchDto) {
		CapsResource resourceDetail = capsResourceDao
				.getCapsResourceById(facilityInvDetailFetchDto.getFacilInvDtlDto().getIdFacilResource());
		if (!ObjectUtils.isEmpty(resourceDetail.getCdRsrcMhmrCompCode())) {
			facilityInvDetailFetchDto.getFacilInvDtlDto().setCdMhmrCompCode(resourceDetail.getCdRsrcMhmrCompCode());
		}

		if (!ObjectUtils.isEmpty(resourceDetail.getCdRsrcFacilType())) {
			facilityInvDetailFetchDto.getFacilInvDtlDto().setCdRsrcFacilType(resourceDetail.getCdRsrcFacilType());
		}

	}

	/**
	 * Method Name: getIncomingFacilityDetails Method Description:This method is
	 * used to get the facility details from the Incoming Facility.
	 * 
	 * @param facilityInvDetailFetchDto
	 *            - This dto will hold the Facility Conclusion details.
	 * @param priorStageDto
	 *            - This dto will have the attribute having the id of the stage.
	 */
	private void getIncomingFacilityDetails(FacilityInvDetailFetchDto facilityInvDetailFetchDto,
			PriorStageDto priorStageDto) {
		RetreiveIncomingFacilityInputDto retreiveIncomingFacilityInputDto = new RetreiveIncomingFacilityInputDto();
		RetreiveIncomingFacilityOutputDto retreiveIncomingFacilityOutputDto = new RetreiveIncomingFacilityOutputDto();
		retreiveIncomingFacilityInputDto.setIdStage(priorStageDto.getIdPriorStage());
		fetchIncomingFacilityDao.fetchIncomingFacility(retreiveIncomingFacilityInputDto,
				retreiveIncomingFacilityOutputDto);
		facilityInvDetailFetchDto.getFacilInvDtlDto()
				.setIdFacilResource(retreiveIncomingFacilityOutputDto.getIdResource());
		facilityInvDetailFetchDto.getFacilInvDtlDto()
				.setAddrFacilInvstCity(retreiveIncomingFacilityOutputDto.getAddrIncmgFacilCity());
		facilityInvDetailFetchDto.getFacilInvDtlDto()
				.setAddrFacilInvstStr1(retreiveIncomingFacilityOutputDto.getAddrIncmgFacilStLn1());
		facilityInvDetailFetchDto.getFacilInvDtlDto()
				.setNmFacilinvstFacility(retreiveIncomingFacilityOutputDto.getNmIncmgFacilName());
		facilityInvDetailFetchDto.getFacilInvDtlDto()
				.setAddrFacilInvstStr2(retreiveIncomingFacilityOutputDto.getAddrIncmgFacilStLn2());
		facilityInvDetailFetchDto.getFacilInvDtlDto()
				.setAddrFacilInvstCnty(retreiveIncomingFacilityOutputDto.getCdIncmgFacilCnty());
		facilityInvDetailFetchDto.getFacilInvDtlDto()
				.setAddrFacilInvstState(retreiveIncomingFacilityOutputDto.getCdIncmgFacilState());
		facilityInvDetailFetchDto.getFacilInvDtlDto()
				.setAddrFacilInvstZip(retreiveIncomingFacilityOutputDto.getAddrIncmgFacilZip());
		facilityInvDetailFetchDto.getFacilInvDtlDto()
				.setNbrFacilinvstPhone(retreiveIncomingFacilityOutputDto.getNbrIncmgFacilPhone());
		facilityInvDetailFetchDto.getFacilInvDtlDto()
				.setNbrFacilinvstExtension(retreiveIncomingFacilityOutputDto.getNbrIncmgFacilPhoneExt());
		facilityInvDetailFetchDto.getFacilInvDtlDto()
				.setNmFacilinvstAff(retreiveIncomingFacilityOutputDto.getNmIncmgFacilAffiliated());
	}

	/**
	 * Method Name: getFacilityInvestigationDetails Method Description:This
	 * method is used to get the facility investigation details
	 * 
	 * @param facilityInvDetailFetchDto
	 *            - This dto will hold the Facility Conclusion details.
	 * @param idStage
	 *            - The id of the stage.
	 */
	private void getFacilityInvestigationDetails(FacilityInvDetailFetchDto facilityInvDetailFetchDto, Long idStage) {
		FacilInvDtlDto facilInvDtlDto = new FacilInvDtlDto();
		// Calling the dao implementation to get the facility investigation
		// details.
		facilInvDtlDto = facilityInvCnclsnDao.getFacilityInvestigationDetail(idStage);
		/*
		 * get the time portion from the date value and set in the dto for
		 * displaying on the screen.
		 */
		if (!ObjectUtils.isEmpty(facilInvDtlDto.getDtFacilInvstBegun())) {
			String timeFiled = DateUtils.getTime(facilInvDtlDto.getDtFacilInvstBegun());
			if (!(timeFiled.contains(TWELVE_PM) || timeFiled.contains(TWELVE_AM))) {
				facilInvDtlDto.setTmFacilInvstBeg(timeFiled);
			}

		}
		if (!ObjectUtils.isEmpty(facilInvDtlDto.getDtFacilInvstIncident())) {
			String timeFiled = DateUtils.getTime(facilInvDtlDto.getDtFacilInvstIncident());
			if (!(timeFiled.contains(TWELVE_PM) || timeFiled.contains(TWELVE_AM))) {
				facilInvDtlDto.setTmFacilInvstInc(timeFiled);
			}

		}
		if (!ObjectUtils.isEmpty(facilInvDtlDto.getDtFacilInvstIntake())) {
			String timeFiled = DateUtils.getTime(facilInvDtlDto.getDtFacilInvstIntake());
			//Defect 11397 - Commenting below condition to show the intake time
			//if (!(timeFiled.contains(TWELVE_PM) || timeFiled.contains(TWELVE_AM))) {
				facilInvDtlDto.setTmFacilInvstInt(timeFiled);
			//}

		}
		facilInvDtlDto.setCdTask(FACILITY_INV_CONCLUSION_TASK_CODE);
		facilityInvDetailFetchDto.setFacilInvDtlDto(facilInvDtlDto);
	}

	/**
	 * Method Name: getInvestigatedFacilityList Method Description:This method
	 * is invoked to retrieve the list of Providers in the Facility
	 * Investigation conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameter for retrieving the
	 *            list of providers.
	 * @return facilityInvCnclsnRes - This dto will hold the list of providers
	 *         in the Facility Investigation conclusion .
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public List<FacilityInvProviderDto> getInvestigatedFacilitiesList(Long idStage) {
		List<FacilityInvProviderDto> facilityInvProviderDtosList = new ArrayList<FacilityInvProviderDto>();
		// Calling the dao impl to get the list of providers in the facil inv
		// conclusion.
		facilityInvProviderDtosList = facilityInvCnclsnDao.getInvestigatedFacilityList(idStage);
		return facilityInvProviderDtosList;
	}

	/**
	 * Method Name: getFacilityInvCnclsnEventDetails Method Description:This
	 * method is used to get the Facility Investigation Conclusion Event
	 * information.
	 * 
	 * @param facilityInvDetailFetchDto
	 *            -This dto will hold the Facility Conclusion details.
	 * @param idEvent
	 *            - The id of the conclusion event.
	 */
	private void getFacilityInvCnclsnEventDetails(FacilityInvDetailFetchDto facilityInvDetailFetchDto, Long idEvent) {
		// Call the dao impl to get the event details.
		EventDto eventDto = eventDao.getEventByid(idEvent);
		FacilityInvEventDto facilityInvEventDto = new FacilityInvEventDto();
		// Setting the event details in the dto
		facilityInvEventDto.setCdEventStatus(eventDto.getCdEventStatus());
		facilityInvEventDto.setCdEventType(eventDto.getCdEventType());
		facilityInvEventDto.setDtEventOccurred(eventDto.getDtEventOccurred());
		facilityInvEventDto.setIdEvent(eventDto.getIdEvent());
		facilityInvEventDto.setIdStage(eventDto.getIdStage());
		facilityInvEventDto.setIdPerson(eventDto.getIdPerson());
		facilityInvEventDto.setTxtEventDescr(eventDto.getEventDescr());
		facilityInvEventDto.setCdTask(eventDto.getCdTask());
		facilityInvEventDto.setDtLastUpdate(eventDto.getDtLastUpdate());
		facilityInvDetailFetchDto.setFacilityInvEventDto(facilityInvEventDto);
	}

	/**
	 * Method Name: getCommunityProviderExists Method Description:This method is
	 * used to check if a community provider exists in the list of Providers in
	 * the Facility Investigation conclusion.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the input parameters to check if a
	 *            community provider exists as part of the Facility
	 *            Investigation conclusion.
	 * @return facilityInvCnclsnRes - This dto will hold the boolean indicator
	 *         whether a community provider exists or not.
	 */
	@Override
	public boolean checkCommunityProviderExists(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		boolean isValid = false;
		boolean communityProviderExists = getLinkedFacilityCommunityProvider(facilityInvCnclsnReq)
				.getLinkedFacilityCommunnity();
		boolean isMedicaidIdentifierMissing = getMedicaidIdentifierMissing(facilityInvCnclsnReq)
				.getMedicaidIdentifier();
		if (communityProviderExists && isMedicaidIdentifierMissing) {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * Method Name: saveFacilityInvCnclsn Method Description:This method is used
	 * to save the Facility Investigation conclusion details.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the Facility Investigation conclusion
	 *            details to be saved.
	 * @return FacilityInvCnclsnRes - This dto will hold the response of the
	 *         save of Facility Conclusion , whether the data was saved or some
	 *         error occurred.
	 * @throws Exception
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FacilityInvCnclsnRes saveFacilityInvCnclsn(FacilityInvCnclsnReq facilityInvCnclsnReq) throws Exception {
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		boolean indValidateFurther = true;
		List<Integer> errorList = new ArrayList<Integer>();
		FacilityInvDetailFetchDto facilityInvDetailFetchDto = facilityInvCnclsnReq.getFacilityInvDetailFetchDto();
		FacilityInvStageDetailsDto facilityInvStageDetailsDto = facilityInvDetailFetchDto
				.getFacilityInvStageDetailsDto();
		FacilityInvEventDto facilityInvEventDto = facilityInvDetailFetchDto.getFacilityInvEventDto();
		FacilInvDtlDto facilInvDtlDto = facilityInvDetailFetchDto.getFacilInvDtlDto();
		// Converting the last update json to collection object
		if (!ObjectUtils.isEmpty(facilityInvDetailFetchDto.getBlobLastUpdateListJson())) {
			facilityInvDetailFetchDto.setBlobLastUpdateList(
					(Map<String, Date>) JSONUtil.jsonToMap(facilityInvDetailFetchDto.getBlobLastUpdateListJson()));
		}
		// Check if the stage classification is 'AFC' and the stage is 'INV'
		if (CodesConstant.CPGRMSFM_AFC.equals(facilityInvStageDetailsDto.getCdStageClassification())
				&& CodesConstant.CSTAGES_INV.equals(facilityInvStageDetailsDto.getCdStage())
				&& !ObjectUtils.isEmpty(facilInvDtlDto.getDtFacilInvstComplt())) {
			/*
			 * Check if in facility allegation the seriousness of injury is
			 * fatal , then check if the date of death is entered and reason for
			 * death . If not entered then return error code to be displayed in
			 * the screen.
			 */
			int code = getVictimsInFacilityAllegation(facilityInvStageDetailsDto.getIdStage());
			if (code != 0) {
				errorList.add(code);
				indValidateFurther = false;
			}

		}

		if (indValidateFurther
				&& CodesConstant.CPGRMSFM_AFC.equals(facilityInvStageDetailsDto.getCdStageClassification())
				&& CodesConstant.CSTAGES_INV.equals(facilityInvStageDetailsDto.getCdStage())
				&& (SAVE_AND_COMPLETE.equals(facilityInvCnclsnReq.getButtonClicked())
						|| SAVE_AND_CLOSE.equals(facilityInvCnclsnReq.getButtonClicked()))) {
			/*
			 * If Save And Complete or Save And Close is clicked , then check if
			 * the alleged victims and the facility address match. If not, then
			 * return error code to be displayed in the screen.
			 */
			int code = checkFacilityResourceAddress(facilityInvStageDetailsDto.getIdStage(),
					facilInvDtlDto.getIdFacilResource());
			if (code != 0) {
				errorList.add(code);
				indValidateFurther = false;
			}
		}
		AdminReviewDto adminReviewDto = null;
		// If the stage is ARI or ARF , the retrieve the ARI details.
		if (indValidateFurther && (CodesConstant.CSTAGES_ARI.equals(facilityInvStageDetailsDto.getCdStage())
				|| CodesConstant.CSTAGES_ARF.equals(facilityInvStageDetailsDto.getCdStage()))) {
			adminReviewDto = facilityInvCnclsnDao.getAdminReviewDetails(facilityInvStageDetailsDto.getIdStage());
			if (ObjectUtils.isEmpty(adminReviewDto)) {
				indValidateFurther = false;
			} else {
				indValidateFurther = true;
			}
		}
		Long idStageForCheckEventStatus = null;
		if (indValidateFurther) {
			if (CodesConstant.CSTAGES_ARI.equals(facilityInvStageDetailsDto.getCdStage())
					|| CodesConstant.CSTAGES_ARF.equals(facilityInvStageDetailsDto.getCdStage())) {
				idStageForCheckEventStatus = adminReviewDto.getIdStage();
			} else {
				idStageForCheckEventStatus = facilityInvStageDetailsDto.getIdStage();
			}

			// Call the check stage event status service impl
			InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
			inCheckStageEventStatusDto.setIdStage(idStageForCheckEventStatus);
			inCheckStageEventStatusDto.setCdTask(facilInvDtlDto.getCdTask());
			if (checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto)) {
				indValidateFurther = true;
			} else {
				indValidateFurther = false;
			}
		}
		String cdEventStatus = CodesConstant.CEVTSTAT_PEND;
		if (indValidateFurther && !CodesConstant.CSTAGES_ARI.equals(facilityInvStageDetailsDto.getCdStage())
				&& !CodesConstant.CSTAGES_ARF.equals(facilityInvStageDetailsDto.getCdStage())) {
			if (!facilityInvCnclsnReq.getSysNbrReserved1()) {
				if (CodesConstant.CEVTSTAT_PEND.equals(facilityInvEventDto.getCdEventStatus())) {

					ApprovalCommonInDto pInputMsg = new ApprovalCommonInDto();
					ApprovalCommonOutDto pOutputMsg = new ApprovalCommonOutDto();
					pInputMsg.setIdEvent(facilInvDtlDto.getIdEvent());
					// Call Service to invalidate approvals
					approvalCommonService.InvalidateAprvl(pInputMsg, pOutputMsg);
				}
				if (!ObjectUtils.isEmpty(facilInvDtlDto.getNmFacilinvstFacility())
						&& !ObjectUtils.isEmpty(facilInvDtlDto.getDtFacilInvstIntake())
						&& !ObjectUtils.isEmpty(facilInvDtlDto.getDtFacilInvstBegun())
						&& !ObjectUtils.isEmpty(facilInvDtlDto.getDtFacilInvstComplt())
						&& !ObjectUtils.isEmpty(facilityInvStageDetailsDto.getCdStageReasonClosed())) {
					cdEventStatus = CodesConstant.CEVTSTAT_COMP;
				} else {
					cdEventStatus = CodesConstant.CEVTSTAT_PROC;
				}
				// Call the Post Event service to update the EVENT and
				// EVENT_PERSON_LINK tables.
				callPostEvent(cdEventStatus, facilityInvEventDto);
			}

			// Call method to update the Facility Investigation Conclusion
			// details
			updateFacilityInvestigation(facilInvDtlDto);

		}
		// Call method to update STAGE table
		if (indValidateFurther || facilityInvDetailFetchDto.isIndARI()) {
			updateStageDetails(facilityInvStageDetailsDto);
		}

		facilityInvCnclsnRes.setErrorCodesList(errorList);
		facilityInvCnclsnRes.setFacilityInvDetailFetchDto(facilityInvDetailFetchDto);

		/*
		 * Check if the facility in the Provider section is from the
		 * INCOMING_FACILITY table , then create a link in the FACIL_RSRC_LINK
		 * table.
		 */
		if (indValidateFurther && facilityInvDetailFetchDto.isIndNewInvestigation()
				&& CodesConstant.CEVTSTAT_NEW.equals(facilityInvEventDto.getCdEventStatus())
				|| (CollectionUtils.isEmpty(facilityInvDetailFetchDto.getProviderList()))
				|| (!CollectionUtils.isEmpty(facilityInvDetailFetchDto.getProviderList()) && (ObjectUtils
						.isEmpty(facilityInvDetailFetchDto.getProviderList().get(0)
								.getIdFacilRsrcLink())
						|| (!ObjectUtils
								.isEmpty(facilityInvDetailFetchDto.getProviderList().get(0).getIdFacilRsrcLink())
								&& facilityInvDetailFetchDto.getProviderList().get(0).getIdFacilRsrcLink()
										.equals(0l))))) {
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getIdFacilResource())
					&& 0l != facilInvDtlDto.getIdFacilResource()) {
				createInvestigationFacility(facilityInvDetailFetchDto);
			}

		}
		return facilityInvCnclsnRes;

	}

	/**
	 * Method Name: createInvestigationFacility Method Description:
	 * 
	 * @param facilityInvDetailFetchDto
	 */
	/**
	 * Method Name: createInvestigationFacility Method Description:This method
	 * is used to call the method which creates a new facility . In this method
	 * the request to that method is populated.
	 * 
	 * @param facilityInvDetailFetchDto
	 *            -This dto will hold the Facility Conclusion details.
	 */
	private void createInvestigationFacility(FacilityInvDetailFetchDto facilityInvDetailFetchDto) {
		FacilityInvCnclsnReq facilityInvCnclsnReq = new FacilityInvCnclsnReq();
		facilityInvCnclsnReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		facilityInvCnclsnReq.setDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
		facilityInvCnclsnReq.setIdCase(facilityInvDetailFetchDto.getFacilityInvStageDetailsDto().getIdCase());
		facilityInvCnclsnReq.setIdStage(facilityInvDetailFetchDto.getFacilityInvStageDetailsDto().getIdStage());
		ResourceAddressDto resourceAddressDto = new ResourceAddressDto();
		FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto = new FacilityInvCnclsnDetailDto();
		BeanUtils.copyProperties(facilityInvDetailFetchDto.getFacilInvDtlDto(), facilityInvCnclsnDetailDto);
		facilityInvCnclsnDetailDto
				.setPhoneNumber(facilityInvDetailFetchDto.getFacilInvDtlDto().getNbrFacilinvstPhone());
		facilityInvCnclsnDetailDto
				.setNmFacilInvstFacility(facilityInvDetailFetchDto.getFacilInvDtlDto().getNmFacilinvstFacility());

		resourceAddressDto.setIdResource(facilityInvDetailFetchDto.getFacilInvDtlDto().getIdFacilResource());
		resourceAddressDto.setAddStreetLine1(facilityInvDetailFetchDto.getFacilInvDtlDto().getAddrFacilInvstStr1());
		resourceAddressDto.setAddStreetLine2(facilityInvDetailFetchDto.getFacilInvDtlDto().getAddrFacilInvstStr2());
		resourceAddressDto.setAddCity(facilityInvDetailFetchDto.getFacilInvDtlDto().getAddrFacilInvstCity());
		resourceAddressDto.setAddState(facilityInvDetailFetchDto.getFacilInvDtlDto().getAddrFacilInvstState());
		resourceAddressDto.setAddCounty(facilityInvDetailFetchDto.getFacilInvDtlDto().getAddrFacilInvstCnty());
		resourceAddressDto.setAddZip(facilityInvDetailFetchDto.getFacilInvDtlDto().getAddrFacilInvstZip());
		resourceAddressDto.setAddComments(facilityInvDetailFetchDto.getFacilInvDtlDto().getTxtFacilInvstComments());
		facilityInvCnclsnReq.setResourceAddressDto(resourceAddressDto);
		facilityInvCnclsnReq.setFacilityInvCnclsnDetailDto(facilityInvCnclsnDetailDto);
		getsaveFacility(facilityInvCnclsnReq);

	}

	/**
	 * Method Name: updateStageDetails Method Description:This method is used to
	 * call the dao implementation which updates the stage details.
	 * 
	 * @param facilityInvStageDetailsDto
	 *            - This dto will the values of the stage details.
	 */
	private void updateStageDetails(FacilityInvStageDetailsDto facilityInvStageDetailsDto) {
		Stage stage = stageDao.getStageEntityById(facilityInvStageDetailsDto.getIdStage());
		// ALM 16643 fix - Ignore dtStageCreated to avoid update happening to the audit column of existing stage record
		String [] ignoreProp = new String[]{"dtStageCreated"};
		BeanUtils.copyProperties(facilityInvStageDetailsDto, stage, ignoreProp);
		if (!ObjectUtils.isEmpty(facilityInvStageDetailsDto.getStagePriorityCmnts())) {
			stage.setTxtStagePriorityCmnts(facilityInvStageDetailsDto.getStagePriorityCmnts());
		}
		if (!ObjectUtils.isEmpty(facilityInvStageDetailsDto.getStageClosureCmnts())) {
			stage.setTxtStageClosureCmnts(facilityInvStageDetailsDto.getStageClosureCmnts());
		}
		stage.setCapsCase(capsCaseDao.getCapsCaseEntityById(facilityInvStageDetailsDto.getIdCase()));
		stage.setSituation(situationDao.getSituationEntityById(facilityInvStageDetailsDto.getIdSituation()));
		stageDao.updateStage(stage);
	}

	/**
	 * Method Name: updateFacilityInvestigation Method Description:
	 * 
	 * @param facilInvDtlDto
	 */
	/**
	 * Method Name: updateFacilityInvestigation Method Description:This method
	 * is used to call the dao implementation to update the facility
	 * investigation conclusion details.
	 * 
	 * @param facilInvDtlDto
	 *            - This dto will have the facility investigation details.
	 * @throws Exception
	 */
	private void updateFacilityInvestigation(FacilInvDtlDto facilInvDtlDto) throws Exception {
		LocalDateTime dtInvComplete = null;
		// If the Documentation Complete date is entered on the screen , then
		// converting
		// the date to LocalDateTime for date comparison logic.
		if (!ObjectUtils.isEmpty(facilInvDtlDto.getDtFacilInvstComplt())) {
			dtInvComplete = facilInvDtlDto.getDtFacilInvstComplt().toInstant().atZone(ZoneId.systemDefault())
					.toLocalDateTime();
			facilInvDtlDto.setDtFacilInvstComplt(Date.from(dtInvComplete.atZone(ZoneId.systemDefault()).toInstant()));
		}
		/*
		 * If the intake date and intake time is present , combine the date and
		 * time fields to form the timestamp as a LocalDateTime instance.
		 */
		LocalDateTime dtIntakeReceived = null;
		if (!ObjectUtils.isEmpty(facilInvDtlDto.getDtFacilInvstIntake())) {
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getTmFacilInvstInt())) {
				dtIntakeReceived = DateUtils.getDateTime(facilInvDtlDto.getDtFacilInvstIntake(),
						facilInvDtlDto.getTmFacilInvstInt());
			} else {
				// Defect 11397 - Assignment was done to wrong field causing exception
				dtIntakeReceived = facilInvDtlDto.getDtFacilInvstIntake().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDateTime();
			}
			facilInvDtlDto
					.setDtFacilInvstIntake(Date.from(dtIntakeReceived.atZone(ZoneId.systemDefault()).toInstant()));
		}
		/*
		 * If the incident occurred date and incident occurred time is present ,
		 * combine the date and time fields to form the timestamp as a
		 * LocalDateTime instance.
		 */
		LocalDateTime dtIncidentOccured = null;
		if (!ObjectUtils.isEmpty(facilInvDtlDto.getDtFacilInvstIncident())) {
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getTmFacilInvstInc())) {
				dtIncidentOccured = DateUtils.getDateTime(facilInvDtlDto.getDtFacilInvstIncident(),
						facilInvDtlDto.getTmFacilInvstInc());
			} else {
				dtIncidentOccured = facilInvDtlDto.getDtFacilInvstIncident().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDateTime();
			}
			facilInvDtlDto
					.setDtFacilInvstIncident(Date.from(dtIncidentOccured.atZone(ZoneId.systemDefault()).toInstant()));
		}

		/*
		 * If the investigation begun date and investigation begun time is
		 * present , combine the date and time fields to form the timestamp as a
		 * LocalDateTime instance.
		 */
		LocalDateTime dtInvBegun = null;
		if (!ObjectUtils.isEmpty(facilInvDtlDto.getDtFacilInvstBegun())) {
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getTmFacilInvstBeg())) {
				dtInvBegun = DateUtils.getDateTime(facilInvDtlDto.getDtFacilInvstBegun(),
						facilInvDtlDto.getTmFacilInvstBeg());
			} else {
				dtInvBegun = facilInvDtlDto.getDtFacilInvstBegun().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDateTime();
			}
			facilInvDtlDto.setDtFacilInvstBegun(Date.from(dtInvBegun.atZone(ZoneId.systemDefault()).toInstant()));
		}

		// Calling the dao implementation to update the facility investigation
		// details.
		FacilInvDtlDto facilInvDtlDto2 = facilityInvCnclsnDao.updateFacilityInvCnclsnDetails(facilInvDtlDto);
		facilInvDtlDto.setDtLastUpdate(facilInvDtlDto2.getDtLastUpdate());

	}

	/**
	 * Method Name: callPostEvent Method Description:This method is used to call
	 * the PostEvent method . The request to the Post Event is populated in this
	 * method and then the Post Event is called.
	 * 
	 * @param cdEventStatus
	 *            - The event status.
	 * @param facilityInvEventDto
	 *            - The dto will have the values of the facility investigation
	 *            event.
	 */
	private void callPostEvent(String cdEventStatus, FacilityInvEventDto facilityInvEventDto) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		postEventIPDto.setIdPerson(facilityInvEventDto.getIdPerson());
		postEventIPDto.setIdStage(facilityInvEventDto.getIdStage());
		postEventIPDto.setCdEventType(CodesConstant.CEVNTTYP_CCL);
		postEventIPDto.setCdTask(facilityInvEventDto.getCdTask());
		postEventIPDto.setIdEvent(facilityInvEventDto.getIdEvent());
		postEventIPDto.setDtEventOccurred(facilityInvEventDto.getDtEventOccurred());
		postEventIPDto.setCdEventStatus(cdEventStatus);
		postEventIPDto.setEventDescr(facilityInvEventDto.getTxtEventDescr());
		postEventIPDto.setTsLastUpdate(facilityInvEventDto.getDtLastUpdate());

		// Calling the Post Event service implementation.
		postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
	}

	/**
	 * Method Name: checkFacilityResourceAddress Method Description:This method
	 * is used to validate whether the facility address and the victim address
	 * match.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @param idResource
	 *            - The id of the resource.
	 * @return returnCode - The value indicating if the addresses match or not.
	 */
	private int checkFacilityResourceAddress(Long idStage, Long idResource) {
		int returnCode = SUCCESS;
		// Calling the dao impl to get the addresses for the alleged victims in
		// the
		// facil inv conclusion.
		List<ResourceAddressDto> list = facilityInvCnclsnDao.getAllegedVictimsPrimaryAddressList(idStage);
		if (!CollectionUtils.isEmpty(list)) {
			// If the list is not empty , checking the facility address with the
			// alleged
			// victims address.
			returnCode = getFacilityResourceAddress(idResource, list);

		}

		return returnCode;
	}

	/**
	 * Method Name: getFacilityResourceAddress Method Description:This method is
	 * used to validate whether the facility address and the victim address
	 * match.
	 * 
	 * @param idResource
	 *            - The id of the facility address
	 * @param list
	 *            - The list of address of the alleged victims.
	 * @return - The code indicating of the addresses match or not.
	 */
	private int getFacilityResourceAddress(Long idResource, List<ResourceAddressDto> list) {
		int returnCode = SUCCESS;
		List<String> facilityCodes = new ArrayList<String>();
		facilityCodes.add(CodesConstant.CFACTYP4_16);
		facilityCodes.add(CodesConstant.CFACTYP4_17);
		// Calling the dao impl to retrieve the list of facility address
		List<ResourceAddressDto> resourceAddressList = facilityInvCnclsnDao.getRsrcAddressFromCapsRsrc(idResource,
				facilityCodes);
		if (!CollectionUtils.isEmpty(resourceAddressList)) {

			ResourceAddressDto resourceAddressDto = resourceAddressList.get(0);
			/*
			 * Iterating over the addresses of the alleged victims and checking
			 * if it matches with the facility address. If yes, then breaking
			 * out of the for loop.
			 */
			for (ResourceAddressDto primaryAddress : list) {

				if ((!getStringSafe(primaryAddress.getAddStreetLine1())
						.equals(getStringSafe(resourceAddressDto.getAddStreetLine1()))
						|| !getStringSafe(primaryAddress.getAddStreetLine2())
								.equals(getStringSafe(resourceAddressDto.getAddStreetLine2()))
						|| !getStringSafe(primaryAddress.getAddState())
								.equals(getStringSafe(resourceAddressDto.getAddState()))
						|| !getStringSafe(primaryAddress.getAddZip())
								.equals(getStringSafe(resourceAddressDto.getAddZip()))
						|| !getStringSafe(primaryAddress.getAddCounty())
								.equals(getStringSafe(resourceAddressDto.getAddCounty())))) {
					returnCode = Messages.MSG_MISMATCH_ADDR;
				} else {
					returnCode = SUCCESS;
					break;

				}
			}
		}
		return returnCode;

	}

	/**
	 * Method Name: getVictimsInFacilityAllegation Method Description: This
	 * method is used to check if the victims information in the facility
	 * allegation are proper according to the business logic.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @return returnCode -The code indicating if an error exists with the
	 *         victim information in the Facility allegation.
	 */
	private int getVictimsInFacilityAllegation(Long idStage) {
		int returnCode = SUCCESS;
		AllegationFacilAllegPersonDto allegationFacilAllegPersonDto = new AllegationFacilAllegPersonDto();
		allegationFacilAllegPersonDto.setUlIdAllegationStage(idStage.intValue());
		FacilAllegPersonDto facilAllegPersonDto = allegFacilDao
				.getAllegationFacilAllegPerson(allegationFacilAllegPersonDto);
		if (!ObjectUtils.isEmpty(facilAllegPersonDto)
				&& !ObjectUtils.isEmpty(facilAllegPersonDto.getAllegFacilPersonDto()) && !CollectionUtils
						.isEmpty(facilAllegPersonDto.getAllegFacilPersonDto().getAllegationStageVictimDtoList())) {
			// If the seriousness of injury is Fatal and there is dod in person
			// detail for
			// that person , then show error on screen.
			boolean indNoDateOfDeath = facilAllegPersonDto.getAllegFacilPersonDto().getAllegationStageVictimDtoList()
					.stream().anyMatch(victim -> CodesConstant.CDGROINJ_DD1.equals(victim.getCdFacilAllegInjSer())
							&& ObjectUtils.isEmpty(victim.getDtPersonDeath()));
			// If the seriousness of injury is Fatal and reason of death is
			// selected as Not
			// Related to A/N , then show error on screen.
			boolean indValidReason = facilAllegPersonDto.getAllegFacilPersonDto().getAllegationStageVictimDtoList()
					.stream()
					.anyMatch(victim -> CodesConstant.CDGROINJ_DD1.equals(victim.getCdFacilAllegInjSer())
							&& CodesConstant.CRSNFDTH_NAB.equals(victim.getCdPersonDeath())
							&& !ObjectUtils.isEmpty(victim.getDtPersonDeath()));

			if (indNoDateOfDeath) {
				returnCode = Messages.MSG_RSN_DATE_DEATH_REQ;
			} else if (indValidReason) {
				returnCode = Messages.MSG_INVALID_RSN_DEATH;
			}

		}
		return returnCode;
	}

	/**
	 * Method Name: validateSubmitAndCloseInvestigation Method Description:This
	 * method is used to perform validations before conclusion can be completed
	 * or the stage can be closed.
	 * 
	 * @param facilityInvDetailFetchDto
	 * @param indEditProcess
	 * @param cReqFuncCd
	 * @return
	 */
	private Map<String, Object> validateSubmitAndCloseInvestigation(FacilityInvDetailFetchDto facilityInvDetailFetchDto,
			String indEditProcess, String cReqFuncCd) {
		List<Long> notApprovedEvent = new ArrayList<Long>();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Integer> errorCodesList = new ArrayList<>();
		FacilityInvStageDetailsDto facilityInvStageDetailsDto = facilityInvDetailFetchDto
				.getFacilityInvStageDetailsDto();
		FacilInvDtlDto facilInvDtlDto = facilityInvDetailFetchDto.getFacilInvDtlDto();
		boolean indValidateFurther = true;
		// Call the checkStageEventStatus method
		// Call the check stage event status service impl
		InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
		inCheckStageEventStatusDto.setIdStage(facilInvDtlDto.getIdStage());
		inCheckStageEventStatusDto.setCdTask(facilInvDtlDto.getCdTask());
		if (!checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto)) {
			indValidateFurther = true;
		}

		if (indValidateFurther && !DECODE_NO_EDITS.equals(indEditProcess)) {
			checkForSerOfInjAndLocOfIncidentFields(facilInvDtlDto.getIdStage(), errorCodesList);
		}
		/*
		 ** Call the Dao to retrieve from the PERSON and STAGE PERSON LINK tables
		 * and run the following edit processes: Person Characteristics Victim
		 * Date of Birth Person Search
		 */
		checkPersonInformationDetails(facilInvDtlDto.getIdStage(), errorCodesList, indEditProcess);

		/*
		 ** This DAM retrieves all contacts for the stage, only run if edit
		 * process position 1 is "Y"
		 */
		if (ServiceConstants.CHAR_Y == indEditProcess.charAt(1)) {
			List<Long> idStagesList = new ArrayList<>();
			idStagesList.add(facilInvDtlDto.getIdStage());
			List<ContactSearchListDto> list = contactSearchDao.searchContacts(null, null, null, null, null, null, null,
					null, null, null, null, null, null, idStagesList);
			if (CollectionUtils.isEmpty(list)) {
				errorCodesList.add(Messages.MSG_INV_CONTACT_REQ);
			}
		}

		/*
		 ** This Dao gets the ID CASE for the contact type evidence list from the
		 * contact table, only run if edit process position 2 is "Y".
		 */
		if (ServiceConstants.CHAR_Y == indEditProcess.charAt(2)) {
			if (CollectionUtils.isEmpty(facilityInvCnclsnDao.getEvidenceList(facilityInvStageDetailsDto.getIdCase()))) {
				errorCodesList.add(Messages.MSG_INV_EXT_DOC_REQ);
			}
		}

		// Retrieve all the principals in the stage
		checkPrincipalsInStage(facilInvDtlDto.getIdStage(), errorCodesList, indEditProcess);

		/*
		 * If the error list cout is 0 and is not being called for the edits ,
		 * then set all TODOs to COMPLETED.
		 */
		if (CollectionUtils.isEmpty(errorCodesList) && !cReqFuncCd.equals(ACTION_CODE_EDITS)) {
			UpdateToDoDto updateToDoDto = new UpdateToDoDto();
			updateToDoDto.setIdEvent(facilInvDtlDto.getIdEvent());
			updateToDoDao.completeTodo(updateToDoDto);

			EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
			eventStagePersonLinkInsUpdInDto.setIdStage(facilInvDtlDto.getIdStage());
			eventStagePersonLinkInsUpdInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
			List<EventStagePersonLinkInsUpdOutDto> eventList = eventStagePersonLinkInsUpDao
					.getEventAndStatusDtls(eventStagePersonLinkInsUpdInDto);

			if (!CollectionUtils.isEmpty(eventList)) {
				for (EventStagePersonLinkInsUpdOutDto eventStagePersonLinkInsUpdOutDto : eventList) {
					boolean indErrorAlreadyExists = false;
					if (!DECODE_NO_EDITS.equals(indEditProcess)) {
						if (!CodesConstant.CEVTSTAT_COMP.equals(eventStagePersonLinkInsUpdOutDto.getCdEventStatus())
								&& !CodesConstant.CEVTSTAT_APRV
										.equals(eventStagePersonLinkInsUpdOutDto.getCdEventStatus())) {
							boolean indContactEventNew = !(CodesConstant.CEVTSTAT_NEW
									.equals(eventStagePersonLinkInsUpdOutDto.getCdEventStatus())
									&& CodesConstant.CEVNTTYP_CON
											.equals(eventStagePersonLinkInsUpdOutDto.getCdEventType()));
							boolean indPriorityChangeEventNew = !(CodesConstant.CEVTSTAT_NEW
									.equals(eventStagePersonLinkInsUpdOutDto.getCdEventStatus())
									&& CodesConstant.CEVNTTYP_PRT
											.equals(eventStagePersonLinkInsUpdOutDto.getCdEventType()));
							boolean indMedEventNew = !(CodesConstant.CEVTSTAT_NEW
									.equals(eventStagePersonLinkInsUpdOutDto.getCdEventStatus())
									&& CodesConstant.CEVNTTYP_MED
											.equals(eventStagePersonLinkInsUpdOutDto.getCdEventType()));
							boolean indCaseGeneralEventNew = !(CodesConstant.CEVTSTAT_NEW
									.equals(eventStagePersonLinkInsUpdOutDto.getCdEventStatus())
									&& CodesConstant.CEVNTTYP_CAS
											.equals(eventStagePersonLinkInsUpdOutDto.getCdEventType()));
							if (!indErrorAlreadyExists && indContactEventNew && indPriorityChangeEventNew
									&& indMedEventNew && indCaseGeneralEventNew) {
								errorCodesList.add(Messages.MSG_INV_EVENT_NOT_COMP);
								indErrorAlreadyExists = true;
							}
						}
					}
				}
			}

			if (!CollectionUtils.isEmpty(eventList)) {
				notApprovedEvent = eventList.stream()
						.filter(event -> !(CodesConstant.CEVTSTAT_APRV.equals(event.getCdEventStatus())))
						.map(EventStagePersonLinkInsUpdOutDto::getIdEvent).collect(Collectors.toList());
				returnMap.put("eventList", notApprovedEvent);
				if (ACTION_CODE_CLOSE.equals(cReqFuncCd)) {
					/*
					 ** Close all events by changing all event status's to APRVD
					 */

					for (Long idEvent : notApprovedEvent) {
						facilAllgDtlDao.getEventDetailsUpdate(idEvent, CodesConstant.CEVTSTAT_APRV);
					}
					// Call service to close the stage
					CloseStageCaseInputDto closeStageCaseInputDto = new CloseStageCaseInputDto();
					closeStageCaseInputDto.setCdStageProgram(facilityInvStageDetailsDto.getCdStageProgram());
					closeStageCaseInputDto.setCdStageReasonClosed(facilityInvStageDetailsDto.getCdStageReasonClosed());
					closeStageCaseInputDto.setIdStage(facilityInvStageDetailsDto.getIdStage());
					closeStageCaseInputDto.setCdStage(facilityInvStageDetailsDto.getCdStage());
					closeStageCaseService.closeStageCase(closeStageCaseInputDto);
				}

			}
		}
		returnMap.put("errorCodesList", errorCodesList);

		return returnMap;
	}

	/**
	 * Method Name: checkPrincipalsInStage Method Description:This method is
	 * used to check the person detail informations of all the principals in the
	 * stage. If there is any error in the person information, then set error to
	 * show on the screen.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @param errorCodesList
	 *            - The list of the errors
	 * @param indEditProcess
	 *            - The value of edit process.
	 */
	private void checkPrincipalsInStage(Long idStage, List<Integer> errorCodesList, String indEditProcess) {
		boolean bRsnDthEdit = false;
		boolean indDateReasonDeath = false;
		StagePersonLinkPersonStgTypeInDto stagePersonLinkPersonStgTypeInDto = new StagePersonLinkPersonStgTypeInDto();
		stagePersonLinkPersonStgTypeInDto.setIdStage(idStage);
		stagePersonLinkPersonStgTypeInDto.setCdStagePersType(CodesConstant.CPRSNTYP_PRN);
		List<StagePersonLinkPersonStgTypeOutDto> principalsList = stagePersonLinkPersonStgTypeDao
				.getPersonDtls(stagePersonLinkPersonStgTypeInDto);

		if (!CollectionUtils.isEmpty(principalsList)) {
			if (ServiceConstants.CHAR_Y == indEditProcess.charAt(7)) {
				for (StagePersonLinkPersonStgTypeOutDto stagePersonLinkPersonStgTypeOutDto : principalsList) {
					// If the person's dod is present and not the cdPersonDeath
					// , then show error on
					// the screen.
					if (!ObjectUtils.isEmpty(stagePersonLinkPersonStgTypeOutDto.getDtPersonDeath())
							&& ObjectUtils.isEmpty(stagePersonLinkPersonStgTypeOutDto.getCdPersonDeath())) {
						indDateReasonDeath = true;
						break;
					}
				}

				for (int principal = 0; (principal < principalsList.size() || bRsnDthEdit); ++principal) {
					if (CodesConstant.CRSNFDTH_ABN.equals(principalsList.get(principal).getCdPersonDeath())
							|| CodesConstant.CRSNFDTH_ABO.equals(principalsList.get(principal).getCdPersonDeath())
							|| CodesConstant.CRSNFDTH_OAN.equals(principalsList.get(principal).getCdPersonDeath())
							|| CodesConstant.CRSNFDTH_ABP.equals(principalsList.get(principal).getCdPersonDeath())) {
						checkAllegationDisposition(principalsList.get(principal).getIdPerson(), idStage, bRsnDthEdit,
								errorCodesList);
					}
				}
			}
			if (indDateReasonDeath) {
				errorCodesList.add(Messages.MSG_INV_DATE_RSN_DTH_EDIT);
			}
		}

	}

	/**
	 * Method Name: checkAllegationDisposition Method Description:This method is
	 * used to check the allegation disposition in the facility allegation . It
	 * checks if the seriousness of injury is Fatal and the disposition is Valid
	 * or Confirmed
	 * 
	 * @param idPerson
	 *            - The id of person in person list.
	 * @param idStage
	 *            - The id of the stage/
	 * @param bRsnDthEdit
	 *            - The indicator to indicate if the reason for death is
	 *            necessary or not.
	 * @param errorCodesList
	 *            - The list of errors .
	 */
	private void checkAllegationDisposition(Long idPerson, Long idStage, boolean bRsnDthEdit,
			List<Integer> errorCodesList) {
		List<String> dispositionList = new ArrayList<>();
		AllegationStageInDto allegationStageInDto = new AllegationStageInDto();
		allegationStageInDto.setIdPerson(idPerson);
		allegationStageInDto.setIdStage(idStage);
		allegationStageInDto.setCdFacilAllegInjSer(CodesConstant.CDGROINJ_DD1);
		dispositionList.add(CodesConstant.CDISPSTN_CON);
		dispositionList.add(CodesConstant.CDISPSTN_CRC);
		dispositionList.add(CodesConstant.CDISPSTN_VAL);
		allegationStageInDto.setDispositionList(dispositionList);
		// Check if for a person, if the seriousness of injury is fatal and the
		// disposition is Confirmed or Valid or Reportable Conduct in any
		// Facility
		// Allegation , then show error
		// message
		if (CollectionUtils.isEmpty(facilityInvCnclsnDao.getFacilityAllegationListForVictim(allegationStageInDto))) {
			bRsnDthEdit = true;
			errorCodesList.add(Messages.MSG_INV_RSN_DTH_EDIT);
		}

	}

	/**
	 * Method Name: checkPersonInformationDetails Method Description:This method
	 * is used to validate the person details for principals in the stage.If
	 * there is any error with the person's information , then show the error on
	 * the screen.
	 * 
	 * @param idStage
	 * @param errorCodesList
	 * @param indEditProcess
	 */
	private void checkPersonInformationDetails(Long idStage, List<Integer> errorCodesList, String indEditProcess) {
		boolean indVictimDob = false;
		boolean indPersCharacter = false;
		boolean indPersSearch = false;
		boolean indUnknownName = false;
		List<PersonDto> personDetailsList = arHelperDao.getPersonCharacteristics(idStage);
		if (!CollectionUtils.isEmpty(personDetailsList)) {
			for (PersonDto personDto : personDetailsList) {
				indUnknownName = false;
				if (ObjectUtils.isEmpty(personDto.getNmPersonFirst())
						&& ObjectUtils.isEmpty(personDto.getNmPersonLast())) {
					indUnknownName = true;
				}
				if (ServiceConstants.CHAR_Y == indEditProcess.charAt(5)
						&& !RELATED.equals(personDto.getCdStagePersSearchInd())
						&& !CodesConstant.CPRSNTYP_COL.equals(personDto.getCdStagePersType())
						&& !VIEWED.equals(personDto.getCdStagePersSearchInd()) && !indUnknownName) {
					indPersSearch = true;
					errorCodesList.add(Messages.MSG_INV_VICTIM_DOB_REQ);
				}
				if (ServiceConstants.CHAR_Y == indEditProcess.charAt(3)
						&& (ObjectUtils.isEmpty(personDto.getCdPersonChar()) || ServiceConstants.STR_ZERO_VAL.equals(personDto.getCdPersonChar()))
						&& CodesConstant.CPRSNTYP_PRN.equals(personDto.getCdStagePersType()) && !indUnknownName) {
					indPersCharacter = true;
				}

				if (ServiceConstants.CHAR_Y == indEditProcess.charAt(4)
						&& CodesConstant.CPRSNTYP_PRN.equals(personDto.getCdStagePersType())
						&& ObjectUtils.isEmpty(personDto.getDtPersonBirth()) && !indUnknownName) {
					indVictimDob = true;
				}
				/*
				 ** Break out of loop if all warning flags are set
				 */
				if (indPersCharacter && indVictimDob && indPersSearch) {
					break;
				}
			}
		}
		if (indPersCharacter) {
			errorCodesList.add(Messages.MSG_INV_PERS_CHAR_REQ);
		}
		if (indVictimDob) {
			errorCodesList.add(Messages.MSG_INV_VICTIM_DOB_REQ);
		}

		if (indPersSearch) {
			errorCodesList.add(Messages.MSG_INV_PERS_SEARCH_REQ);
		}
	}

	/**
	 * Method Name: checkForSerOfInjAndLocOfIncidentFields Method
	 * Description:This method is used to check if the seriousness of injury
	 * field in Facility allegation is null.If yes, then show error on the
	 * screen.
	 * 
	 * @param idStage
	 * @param errorCodesList
	 */
	private void checkForSerOfInjAndLocOfIncidentFields(Long idStage, List<Integer> errorCodesList) {
		AllegationFacilAllegPersonDto allegationFacilAllegPersonDto = new AllegationFacilAllegPersonDto();
		allegationFacilAllegPersonDto.setUlIdAllegationStage(idStage.intValue());
		FacilAllegPersonDto facilAllegPersonDto = allegFacilDao
				.getAllegationFacilAllegPerson(allegationFacilAllegPersonDto);
		if (!ObjectUtils.isEmpty(facilAllegPersonDto)
				&& !ObjectUtils.isEmpty(facilAllegPersonDto.getAllegFacilPersonDto()) && !CollectionUtils
						.isEmpty(facilAllegPersonDto.getAllegFacilPersonDto().getAllegationStageVictimDtoList())) {
			boolean indSerOfInjuryNull = facilAllegPersonDto.getAllegFacilPersonDto().getAllegationStageVictimDtoList()
					.stream().anyMatch(allegationStageVictimDto -> ObjectUtils
							.isEmpty(allegationStageVictimDto.getCdFacilAllegInjSer()));
			if (indSerOfInjuryNull) {

				errorCodesList.add(Messages.MSG_INV_FIELD_NULL);

			}

		}
	}

	/**
	 * Method Name: validateForSaveAndSubmit Method Description:This method is
	 * used to validate before save and submit can be proceeded .
	 * 
	 * @param facilityInvCnclsnReq
	 * @return errorCodesList
	 */
	public List<Integer> validateForSaveAndSubmit(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		List<Integer> errorCodesList = new ArrayList<Integer>();
		SelectStageDto selectStageDto = caseSummaryDao.getStage(facilityInvCnclsnReq.getIdStage(),
				ServiceConstants.STAGE_CURRENT);
		boolean showEmrChnages = isNewEMRInvestigation(selectStageDto);
		if (showEmrChnages) {
			boolean facilityAdminNmNotExists = false;
			List<FacilityAllegationDto> list = facilityInvCnclsnDao
					.getNameTiedToFacilAllegOfCRC(facilityInvCnclsnReq.getIdStage());
			if (!CollectionUtils.isEmpty(list)) {
				facilityAdminNmNotExists = list.stream()
						.anyMatch(allegation -> (ObjectUtils.isEmpty(allegation.getCdFacAdminFName())
								|| ObjectUtils.isEmpty(allegation.getCdFacAdminLName())));
			}
			if (facilityAdminNmNotExists) {
				errorCodesList.add(Messages.MSG_FACIl_ADMIN_NAMES);
			}
			checkForHCSFacilities(facilityInvCnclsnReq, errorCodesList);
			boolean isCrimHistPending = criminalHistoryDao.isCrimHistPending(facilityInvCnclsnReq.getIdStage());
			if(isCrimHistPending) {
				errorCodesList.add(ServiceConstants.CRML_HIST_CHECK);
			}
		}

		return errorCodesList;
	}

	/**
	 * Method Name: checkForHCSFacilities Method Description:This method is used
	 * to check the validation logic if the facility type id HCS.
	 * 
	 * @param facilityInvCnclsnReq
	 * @param errorCodesList
	 */
	private void checkForHCSFacilities(FacilityInvCnclsnReq facilityInvCnclsnReq, List<Integer> errorCodesList) {
		if (facilityInvCnclsnReq.getFacilityInvDetailFetchDto().isIndNewInvestigation()
				&& facilityInvCnclsnReq.getFacilityInvDetailFetchDto().isIndHCSInvestigation()) {
			
			checkProviderAllegationErrors(facilityInvCnclsnReq, errorCodesList);
			if (!comparePersonAndFacilityAddress(facilityInvCnclsnReq).getAddressMatched().booleanValue()) {
				errorCodesList.add(Messages.MSG_MISMATCH_ADDR);
			}

		}

	}

	/**
	 * Method Name: isNewEMRInvestigation Method Description:This method is used
	 * to check if the investigation is after the rollout of EMR changes
	 * 
	 * @param selectStageDto
	 *            - The dto will the stage information.
	 * @return isNewAPSInvestigation - The boolean indicator to indicate if the
	 *         investigation is a new EMR.
	 */
	private boolean isNewEMRInvestigation(SelectStageDto selectStageDto) {
		boolean isNewAPSInvestigation = false;
		Date cutOffDate = EMR_CHANGES_DATE;
		if ((selectStageDto.getDtStartDate().compareTo(cutOffDate) >= 1)
				|| (ServiceConstants.N.equals(selectStageDto.getIndStageClose()))
				|| (selectStageDto.getDtStageClose().compareTo(cutOffDate) >= 1)) {
			isNewAPSInvestigation = true;
		}
		return isNewAPSInvestigation;
	}

	/**
	 * Method Name: saveAndSubmitFacilityInvCnclsn Method Description:This
	 * method is used to Save and Submit the Facility Investigation Conclusion
	 * details. It first checks if there are validation errors , and if any
	 * return the list of error codes to the business delegate to be displayed
	 * on the screen. If no validation errors , then the method saves the
	 * Facility Investigation Conclusion details and calls the Validation
	 * service(CINV59s) to check if there is any errors before completing the
	 * Conclusion. If any the list of error codes are returned to the business
	 * delegate.Else the conclusion ic completed.
	 * 
	 * @param facilityInvCnclsnReq
	 * @return FacilityInvCnclsnRes
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FacilityInvCnclsnRes saveAndSubmitFacilityInvCnclsn(FacilityInvCnclsnReq facilityInvCnclsnReq)
			throws Exception {
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		/*
		 * Step 1 -First call the method which checks the business rules for any
		 * validation errors.
		 */
		List<Integer> errorCodesList = validateForSaveAndSubmit(facilityInvCnclsnReq);

		if (!CollectionUtils.isEmpty(errorCodesList)) {

			facilityInvCnclsnRes.setErrorCodesList(errorCodesList);
			if(errorCodesList.stream().anyMatch(err -> err.equals(ServiceConstants.CRML_HIST_CHECK))){
				HashMap personDetail = criminalHistoryDao.checkCrimHistAction(facilityInvCnclsnReq.getIdStage());
				if (!ObjectUtils.isEmpty(personDetail)) {
					facilityInvCnclsnRes.setPersonDetail(personDetail);
				}
			}

		}
		// Step 2 - If there no errors then save the Facility Investigation
		// Conclusion
		// details
		else {

			FacilityInvCnclsnRes facilityInvCnclsnRes1 = saveFacilityInvCnclsn(facilityInvCnclsnReq);
			if (CollectionUtils.isEmpty(facilityInvCnclsnRes1.getErrorCodesList())) {

				/*
				 * Step 3-Call the validation service to check for further
				 * business rules before completing the conclusion.
				 */
				populateReqForSubmitAndCloseInvService(facilityInvCnclsnReq);
				Map<String, Object> returnMap = validateSubmitAndCloseInvestigation(
						facilityInvCnclsnReq.getFacilityInvDetailFetchDto(), facilityInvCnclsnReq.getIndEditProcess(),
						facilityInvCnclsnReq.getReqFuncCd());
				List<Integer> validationErrorCodesList = (List<Integer>) returnMap.get("errorCodesList");
				if (returnMap.containsKey("eventList")
						&& !CollectionUtils.isEmpty((List<Long>) returnMap.get("eventList"))) {
					facilityInvCnclsnRes.setEventsList((List<Long>) returnMap.get("eventList"));
				}
				if (!CollectionUtils.isEmpty(validationErrorCodesList)) {
					facilityInvCnclsnRes.setErrorCodesList(validationErrorCodesList);
				}

				// Step 4 - Check for other business rules for person related
				// details
				if (personUtilityDao.getPRNRaceEthnicityStat(facilityInvCnclsnReq.getIdStage())) {
					validationErrorCodesList.add(Messages.MSG_INV_NO_RAC_STAT);
				}
				if (stageUtilityDao.getDeathReasonMissing(facilityInvCnclsnReq.getIdStage()) > 0) {
					validationErrorCodesList.add(Messages.MSG_INV_DATE_RSN_DTH_EDIT);
				}
				facilityInvCnclsnRes.setErrorCodesList(validationErrorCodesList);

			} else {
				facilityInvCnclsnRes.setErrorCodesList(facilityInvCnclsnRes1.getErrorCodesList());
			}
		}

		return facilityInvCnclsnRes;
	}

	/**
	 * Method Name: populateReqForSubmitAndCloseInvService Method
	 * Description:This method is used to populate the indEditProcess based on
	 * the closure reason entered on the screen.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - The dto will hold the values of facility conclusion .
	 */
	private void populateReqForSubmitAndCloseInvService(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		// If the closure reason is any OTHER than Normal closure, do not check
		// for
		// edits
		// If generating the FacInvCnclsn report, do not check for edits
		if (ServiceConstants.N.equals(
				facilityInvCnclsnReq.getFacilityInvDetailFetchDto().getBlobExistsList().get(FACILITIES_INVST_CONCL_KEY))
				&& !("05").equals(facilityInvCnclsnReq.getFacilityInvDetailFetchDto().getFacilityInvStageDetailsDto()
						.getCdStageReasonClosed())) {
			facilityInvCnclsnReq.setIndEditProcess(lookupDao.decode(CodesConstant.CFACCLED, CodesConstant.CFACCLED_97));
		} else {

			facilityInvCnclsnReq.setIndEditProcess(lookupDao.decode(CodesConstant.CFACCLED, facilityInvCnclsnReq
					.getFacilityInvDetailFetchDto().getFacilityInvStageDetailsDto().getCdStageReasonClosed()));
		}

	}

	/**
	 * Method Name: saveAndCloseFacilityInvCnclsn Method Description:This method
	 * is used to save the facility conclusion and close the stage.
	 * 
	 * @param facilityInvCnclsnReq
	 *            - This dto will hold the details to saved .
	 * @return FacilityInvCnclsnRes - This dto will hold the response of the
	 *         save and submit of Facility Conclusion , whether the data was
	 *         saved or some error occurred.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public FacilityInvCnclsnRes saveAndCloseFacilityInvCnclsn(FacilityInvCnclsnReq facilityInvCnclsnReq)
			throws Exception {
		boolean indValidateFurther = true;
		FacilityInvCnclsnRes facilityInvCnclsnRes = new FacilityInvCnclsnRes();
		FacilityInvDetailFetchDto facilityInvDetailFetchDto = facilityInvCnclsnReq.getFacilityInvDetailFetchDto();
		FacilityInvStageDetailsDto facilityInvStageDetailsDto = facilityInvDetailFetchDto
				.getFacilityInvStageDetailsDto();
		FacilInvDtlDto facilInvDtlDto = facilityInvDetailFetchDto.getFacilInvDtlDto();
		// Set the indicators in the dto for whether the forms exists in the db
		determineFormExists(facilityInvDetailFetchDto);
		/*
		 * Step 1 -First call the method which checks the business rules for any
		 * validation errors.
		 */
		List<Integer> errorCodesList = new ArrayList<Integer>();
		checkForHCSFacilities(facilityInvCnclsnReq, errorCodesList);
		if (!CollectionUtils.isEmpty(errorCodesList)) {

			indValidateFurther = false;

		}

		// Check if the Investigation conclusion was submitted before closing
		// the
		// Investigation conclusion.
		FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto = facilityInvCnclsnDao
				.getApprovalStatusInfo(facilInvDtlDto.getIdEvent());
		if (indValidateFurther && ObjectUtils.isEmpty(facilityInvCnclsnDetailDto)
				&& CodesConstant.CFCCLCDS_05.equals(facilityInvStageDetailsDto.getCdStageReasonClosed())) {
			errorCodesList.add(Messages.MSG_SUBMITTED_BEFORE_CLOSED);
			facilityInvCnclsnRes.setErrorCodesList(errorCodesList);
			indValidateFurther = false;
		}
		if (indValidateFurther && !ObjectUtils.isEmpty(facilityInvCnclsnDetailDto)) {
			String approvalStatus = facilityInvCnclsnDetailDto.getCdApproversStatus();
			if (!ObjectUtils.isEmpty(approvalStatus) && !CodesConstant.CAPPDESG_APRV.equals(approvalStatus)
					&& CodesConstant.CFCCLCDS_05.equals(facilityInvStageDetailsDto.getCdStageReasonClosed())) {
				errorCodesList.add(Messages.MSG_SUBMITTED_BEFORE_CLOSED);
				indValidateFurther = false;
			}
			boolean docExistsAbuseNeglect = facilityInvDetailFetchDto.isIndDocExistsAbuseNeglect();
			if (!CollectionUtils.isEmpty(facilityInvDetailFetchDto.getPageSet())
					&& facilityInvDetailFetchDto.getPageSet().contains(SET_K)) {
				docExistsAbuseNeglect = facilityInvDetailFetchDto.isIndDocExistsRevFacAbuseNeglect();
			}
			if (indValidateFurther && !ObjectUtils.isEmpty(approvalStatus)
					&& !CodesConstant.CAPPDESG_APRV.equals(approvalStatus)
					&& CodesConstant.CFCCLCDS_05.equals(facilityInvStageDetailsDto.getCdStageReasonClosed())
					&& !docExistsAbuseNeglect) {
				errorCodesList.add(Messages.MSG_INV_NO_AB_NEG);
			}
		}
		facilityInvCnclsnRes.setErrorCodesList(errorCodesList);

		/*
		 * Step 2 - If there no errors then save the Facility Investigation
		 * Conclusion details.
		 */
		if (CollectionUtils.isEmpty(errorCodesList)) {
			FacilityInvCnclsnRes facilityInvCnclsnRes1 = saveFacilityInvCnclsn(facilityInvCnclsnReq);
			if (CollectionUtils.isEmpty(facilityInvCnclsnRes1.getErrorCodesList())) {
				boolean indNoRacePresent = false;
				indNoRacePresent = personUtilityDao.getPRNRaceEthnicityStat(facilityInvCnclsnReq.getIdStage());
				// Step 4 - Check for other business rules for person related
				// details
				if (indNoRacePresent) {
					facilityInvCnclsnReq.setReqFuncCd(ACTION_CODE_SAVE_AND_SUBMIT);
				} else {
					facilityInvCnclsnReq.setReqFuncCd(ACTION_CODE_CLOSE);
				}
				/*
				 * Step 3-Call the validation service to check for further
				 * business rules before completing the conclusion.
				 */
				populateReqForSubmitAndCloseInvService(facilityInvCnclsnReq);
				Map<String, Object> returnMap = validateSubmitAndCloseInvestigation(
						facilityInvCnclsnReq.getFacilityInvDetailFetchDto(), facilityInvCnclsnReq.getIndEditProcess(),
						facilityInvCnclsnReq.getReqFuncCd());
				List<Integer> validationErrorCodesList = (List<Integer>) returnMap.get("errorCodesList");
				if (returnMap.containsKey("eventList")
						&& !CollectionUtils.isEmpty((List<Long>) returnMap.get("eventList"))) {
					facilityInvCnclsnRes.setEventsList((List<Long>) returnMap.get("eventList"));
				}
				if (!CollectionUtils.isEmpty(validationErrorCodesList)) {
					facilityInvCnclsnRes.setErrorCodesList(validationErrorCodesList);
				}
				if (indNoRacePresent) {
					facilityInvCnclsnRes.getErrorCodesList().add(Messages.MSG_INV_NO_RAC_STAT);
				}

			} else {
				facilityInvCnclsnRes.setErrorCodesList(facilityInvCnclsnRes1.getErrorCodesList());
			}
		}

		return facilityInvCnclsnRes;
	}

	/**
	 * Method Name: determineFormExists Method Description:This method is used
	 * to set the indicators whether forms exists for the facility conclusion
	 * event or not.
	 * 
	 * @param facilityInvDetailFetchDto
	 *            -This dto will hold the Facility Conclusion details.
	 */
	private void determineFormExists(FacilityInvDetailFetchDto facilityInvDetailFetchDto) {
		Map<String, String> blobExistsMap = facilityInvDetailFetchDto.getBlobExistsList();
		if (!ObjectUtils.isEmpty(facilityInvDetailFetchDto.getIdReferralContact())
				&& 0l != facilityInvDetailFetchDto.getIdReferralContact()) {
			facilityInvDetailFetchDto.setIndDocExistsReferralContact(true);
		}

		if (!CollectionUtils.isEmpty(blobExistsMap)) {
			if (ServiceConstants.Y.equals(blobExistsMap.get(FACILITIES_ABUSE_NEGLECT_KEY))) {
				facilityInvDetailFetchDto.setIndDocExistsFacAbuseNeglect(true);
			} else {
				facilityInvDetailFetchDto.setIndDocExistsFacAbuseNeglect(false);
			}

			if (ServiceConstants.Y.equals(blobExistsMap.get(NOTICE_LE_KEY))) {
				facilityInvDetailFetchDto.setIndDocExistsNoticeLe(true);
			} else {
				facilityInvDetailFetchDto.setIndDocExistsNoticeLe(false);
			}

			if (ServiceConstants.Y.equals(blobExistsMap.get(REVIEW_FACILITIES_ABUSE_NEGLECT_KEY))) {
				facilityInvDetailFetchDto.setIndDocExistsRevFacAbuseNeglect(true);
			} else {
				facilityInvDetailFetchDto.setIndDocExistsRevFacAbuseNeglect(false);
			}

			if (ServiceConstants.Y.equals(blobExistsMap.get(FACILITIES_INVST_CONCL_KEY))) {
				facilityInvDetailFetchDto.setIndDocExistsFacInvCnclsnForm(true);
			} else {
				facilityInvDetailFetchDto.setIndDocExistsFacInvCnclsnForm(false);
			}

			if (ServiceConstants.Y.equals(blobExistsMap.get(REVIEW_FACILITIES_INVST_CONCL_KEY))) {
				facilityInvDetailFetchDto.setIndDocExistsRevFacInvCnclsnForm(true);
			} else {
				facilityInvDetailFetchDto.setIndDocExistsRevFacInvCnclsnForm(false);
			}

			if (ServiceConstants.Y.equals(blobExistsMap.get(FAC_REFERRAL_KEY))) {
				facilityInvDetailFetchDto.setIndDocExistsReferralForm(true);
			} else {
				facilityInvDetailFetchDto.setIndDocExistsReferralForm(false);
			}

			if (ServiceConstants.Y.equals(blobExistsMap.get(AFC_INV_KEY))) {
				facilityInvDetailFetchDto.setIndDocExistsFiveDayReport(true);
			} else {
				facilityInvDetailFetchDto.setIndDocExistsFiveDayReport(false);
			}

			if (ServiceConstants.Y.equals(blobExistsMap.get(AFC_INV_OIG_KEY))) {
				facilityInvDetailFetchDto.setIndDocExistsNoticeOIG(true);
			} else {
				facilityInvDetailFetchDto.setIndDocExistsNoticeOIG(false);
			}
		}
	}

	/**
	 * Method Name: getStringSafe Method Description:This method is used to
	 * check if the string input is null and if so , return an empty string to
	 * avoid null pointers while comparing.
	 * 
	 * @param parmName
	 *            - The input string value.
	 * @return string - The string safe value.
	 */
	private String getStringSafe(String parmName) {

		String string = parmName;

		if (string == null) {
			return ServiceConstants.EMPTY_STR;
		}
		string = string.trim();
		if (string.equals(ServiceConstants.NULL_VALUE)) {
			return ServiceConstants.EMPTY_STR;
		}
		return string;
	}

	
	@Override
	public FacilityInvCnclsnRes checkErrorDisplayAbuseForm(FacilityInvCnclsnReq facilityInvCnclsnReq) {
		FacilityInvCnclsnRes facilityInvCnclsnRes=new FacilityInvCnclsnRes();
		List<Integer> errorList=new ArrayList<Integer>();
		FacilityInvCnclsnRes facilityInvCnclsnRes2=getFacilityInvCnclsn(facilityInvCnclsnReq);
		FacilityInvDetailFetchDto facilityInvDetailFetchDto=facilityInvCnclsnRes2.getFacilityInvDetailFetchDto();
		if(facilityInvDetailFetchDto.getFacilityInvStageDetailsDto().getDtStageStart().compareTo(ENHANCED_APS_FAC_INV_DATE) >= 1 && ("CFIV1600".equalsIgnoreCase(facilityInvCnclsnReq.getDocType()) || "CFIV1600B".equalsIgnoreCase(facilityInvCnclsnReq.getDocType()))) {
			checkProviderAllegationErrors(facilityInvCnclsnReq,errorList);
		}
		if(CollectionUtils.isEmpty(errorList) && ObjectUtils.isEmpty(facilityInvDetailFetchDto.getFacilityInvStageDetailsDto().getDtStageClose()) ) {
		facilityInvCnclsnReq.setFacilityInvDetailFetchDto(facilityInvDetailFetchDto);
		populateReqForSubmitAndCloseInvService(facilityInvCnclsnReq);
		Map<String, Object> returnMap = validateSubmitAndCloseInvestigation(
				facilityInvCnclsnReq.getFacilityInvDetailFetchDto(), facilityInvCnclsnReq.getIndEditProcess(),
				"E");
		errorList=(List<Integer>)returnMap.get("errorCodesList");
		}
		facilityInvCnclsnRes.setErrorCodesList(errorList);
		return facilityInvCnclsnRes;
	}
	
	private void checkProviderAllegationErrors(FacilityInvCnclsnReq facilityInvCnclsnReq,List<Integer> errorCodesList) {
		boolean facilityNotTiedWithAllegation = false;
		boolean allegationNotTied = false;
		List<FacilityInvProviderDto> providerList = getInvestigatedFacilitiesList(
				facilityInvCnclsnReq.getIdStage());
		List<Long> idResourcesList = facilityInvCnclsnDao.getAllegedFacilityIDs(facilityInvCnclsnReq.getIdStage());
		if (!CollectionUtils.isEmpty(idResourcesList) && !CollectionUtils.isEmpty(providerList)) {
			for (FacilityInvProviderDto facilityInvProviderDto : providerList) {
				if (!idResourcesList.contains(facilityInvProviderDto.getIdFacilResource())) {
					allegationNotTied = true;
				}
			}

		}
		if (!CollectionUtils.isEmpty(idResourcesList)) {
			facilityNotTiedWithAllegation = idResourcesList.stream()
					.anyMatch(idResource -> ObjectUtils.isEmpty(idResource));
		}

		if (allegationNotTied) {
			errorCodesList.add(Messages.MSG_FACILITY_ALLEGATION_TIE);
		}
		if (facilityNotTiedWithAllegation) {
			errorCodesList.add(Messages.MSG_RESOURCE_EACH_ALLEGATION);
		}
	}
	
	
}
