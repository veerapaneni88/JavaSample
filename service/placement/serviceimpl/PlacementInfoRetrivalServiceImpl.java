/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jan 5, 2018- 10:44:16 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.placement.serviceimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ResourceHistory;
import us.tx.state.dfps.service.admin.dao.FacilityLocDao;
import us.tx.state.dfps.service.admin.dao.FacilityServiceTypeDao;
import us.tx.state.dfps.service.admin.dao.FetchEventDetailDao;
import us.tx.state.dfps.service.admin.dao.PersonPortfolioDao;
import us.tx.state.dfps.service.admin.dao.ServiceDeliveryRtrvDtlsDao;
import us.tx.state.dfps.service.admin.dao.StageDetailsDao;
import us.tx.state.dfps.service.admin.dto.FacilityLocDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocInDto;
import us.tx.state.dfps.service.admin.dto.FetchEventAdminDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDetailDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioInDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioOutDto;
import us.tx.state.dfps.service.admin.dto.ResourceServiceInDto;
import us.tx.state.dfps.service.admin.dto.StageDetailsDiDto;
import us.tx.state.dfps.service.admin.dto.StageDetailsDoDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.PlcmtInfoRetrivalReq;
import us.tx.state.dfps.service.common.request.PlcmtInfoRsrcRetrivalReq;
import us.tx.state.dfps.service.common.response.CommonCountRes;
import us.tx.state.dfps.service.common.response.PlcmtInfoRetrivalResp;
import us.tx.state.dfps.service.common.response.PlcmtInfoRsrcRetrivalResp;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.facilityservicetype.dto.FacilityServiceTypeInDto;
import us.tx.state.dfps.service.facilityservicetype.dto.FacilityServiceTypeOutDto;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.placement.dao.ContractDao;
import us.tx.state.dfps.service.placement.dao.PersonLocPersonDao;
import us.tx.state.dfps.service.placement.dao.TemporaryAbsenceDao;
import us.tx.state.dfps.service.placement.dto.ContractContractPeriodInDto;
import us.tx.state.dfps.service.placement.dto.ContractContractPeriodOutDto;
import us.tx.state.dfps.service.placement.dto.ContractCountyOutDto;
import us.tx.state.dfps.service.placement.dto.ContractCountyPeriodInDto;
import us.tx.state.dfps.service.placement.dto.ContractCountyPeriodOutDto;
import us.tx.state.dfps.service.placement.dto.PersonLocInDto;
import us.tx.state.dfps.service.placement.dto.PersonLocOutDto;
import us.tx.state.dfps.service.placement.service.PlacementInfoRetrivalService;
import us.tx.state.dfps.service.placement.service.PlacementService;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.resource.dto.ResourceHistoryInDto;
import us.tx.state.dfps.service.resourcehistory.dao.ResourceHistoryDao;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsInDto;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsOutDto;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.CapsResourceLinkDto;
import us.tx.state.dfps.service.subcare.dto.ChildBillOfRightsDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dao.AssignDao;
import us.tx.state.dfps.service.workload.dto.AssignmentGroupDto;
import us.tx.state.dfps.service.workload.dto.EventListDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 5, 2018- 10:44:16 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class PlacementInfoRetrivalServiceImpl implements PlacementInfoRetrivalService {

	private static final String CHILD_PLACE_CATEGORY = "CPL";
	private static final String PRIMARY_CHILD = "PC";
	private static final String FND_SUCCESS = "FND_SUCCESS";
	private static final String FND_FAIL = "FND_FAIL";
	private static final String PLCMT_ISSUES_NARR = "PLCMT_ISSUES_NARR";
	private static final String PLCMT_DISCHG_NARR = "PLCMT_DISCHG_NARR";
	private static final String DISASTER_PLAN_NARR = "DISASTER_PLAN_NARR";
	private static final Date DT_FOSTERCARE_RATE_CHANGE = DateUtils.date(2001, 9, 01);
	private static final Date DT_SERVICE_CODE_CHANGE = DateUtils.date(2003, 9, 01);
	private static final String MSG_SUB_NON_PRS = "MSG_SUB_NON_PRS";
	private static final String CPA = "02";
	private static final String FOST_ADOPT = "020";
	private static final String CONTRACT = "030";
	private static final String NON_PAID = "040";
	private static final String TYC = "050";
	private static final String JUV_PROB = "060";
	private static final String PACE = "070";
	private static final String PCA = "100";
	private static final String PLOC_ONE = "010";
	private static final String PLOC_TWO = "020";
	private static final String PLOC_BASIC = "210";
	private static final String BILLING = "BLOC";
	private static final String REQUESTED = "RLOC";
	private static final String AUTHORIZED = "ALOC";
	private static final String AGE_REQ_CODES_TABLE = "CAGERQRD";
	private static final int AGE_LIMIT = 12;
	private static final char CHAR_ONE = '1';
	private static final char CHAR_ZERO = '0';
	private static final char CHAR_X = 'X';
	private static final String PLCMT_CODES_TABLE2 = "PLCMNTSV";
	private static final String PLCMT_CODES_TABLE = "CPLCMTSC";

	private static final String BASIC_LIV_ARR = "GA";
	private static final String RCVNG_LIV_ARR = "GP";
	private static final String LGL_RISK_LIV_ARR = "GW";
	private static final String HABILITATE_LIV_ARR = "GD";
	private static final String THERAPEUTIC_LIV_ARR = "GG";
	private static final String MED_NEEDS_LIV_ARR = "GK";

	private static final String BASIC_RCVNG_LGL_RISK_A = "63A";
	private static final String MOD_RCVNG_LGL_RISK_B = "63B";
	private static final String TREATMENT_FOSTER_CARE_SVC = "63U";

	private static final String TREATMENT_FOSTER_CARE_FACIL = "87";

	private static final String BASIC_60A = "60A";
	private static final String BASIC_60B = "60B";
	private static final String HABIL_THERA_60C = "60C";

	private static final String BASIC_RCVNG_LGL_RISK = "95L";
	private static final String HABIL_THERA_PRIMARY = "95M";
	private static final String ADOPTIVE_LIV_ARR = "GT";

	private static final String ADOPTIVE_HOME_SVC = "96D";
	private static final String PCA_RECURRING_SVC = "96M";
	private static final String ACTIVE = "A";

	private static final String SVH = "SVH"; // service hold
	private static final String PSH = "PSH"; // pay, service hold
	private static final String PNT = "PNT";

	private static final String JPC_VENDOR_ID = "JPC-VID";
	private static final String TYC_VENDOR_ID = "TYC-VID";
	private static final String PACE_VENDOR_ID = "PACE-VID";

	@Autowired
	EventDao eventDao;

	@Autowired
	FacilityServiceTypeDao facilityServiceTypeDao;

	@Autowired
	ServiceDeliveryRtrvDtlsDao serviceDeliveryRtrvDtlsDao;

	@Autowired
	StageDetailsDao stageDetailsDao;

	@Autowired
	PlacementDao placementDao;

	@Autowired
	FetchEventDetailDao fetchEventDetailDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	ResourceHistoryDao resourceHistoryDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	PersonLocPersonDao personLocPersonDao;

	@Autowired
	AssignDao assignDao;

	@Autowired
	PersonPortfolioDao personPortfolioDao;

	@Autowired
	FacilityLocDao facilityLocDao;

	@Autowired
	CharacteristicsDao characteristicsDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	CodesDao codesDao;

	@Autowired
	ContractDao contractDao;

	@Autowired
	PlacementService placementService;

	@Autowired
	TemporaryAbsenceDao temporaryAbsenceDao;

	/**
	 * 
	 * Method Name: getPlacementInformation Method Description:service to
	 * retrieve the display information Service Name : CSUB25S
	 * 
	 * @param plcmtInfoRetrivalReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public PlcmtInfoRetrivalResp getPlacementInformation(PlcmtInfoRetrivalReq plcmtInfoRetrivalReq) {

		String retVal = FND_FAIL;
		PlacementDto placementDto = new PlacementDto();
		String cdEventStatus = ServiceConstants.EMPTY_STR;

		PlcmtInfoRetrivalResp resp = new PlcmtInfoRetrivalResp();
		resp.setDtWCDDtSystemDate(new Date());
		// CallCCMN87D to retrive the id_event for the closure
		EventListDto eventListDto = getEventListDto(plcmtInfoRetrivalReq);
		if (!ObjectUtils.isEmpty(eventListDto)) {
			resp.setIdEvent(eventListDto.getIdEvent());
			resp.setCdEventStatus(eventListDto.getCdEventStatus());
		} else {
			resp.setCdEventStatus(ServiceConstants.EVENT_STATUS_NEW);
		}
		// CINT40D
		StageDetailsDiDto stageDetailsDiDto = new StageDetailsDiDto();
		stageDetailsDiDto.setIdStage(plcmtInfoRetrivalReq.getIdStage());
		List<StageDetailsDoDto> stageDetailsDoDtoList = stageDetailsDao.getStageDtls(stageDetailsDiDto);
		StageDetailsDoDto stageDetailsDoDto = null;
		if (!ObjectUtils.isEmpty(stageDetailsDoDtoList)) {
			stageDetailsDoDto = stageDetailsDoDtoList.get(0);
			resp.setIdCase(stageDetailsDoDto.getIdCase());
			retVal = FND_SUCCESS;
		}

		if (!ObjectUtils.isEmpty(plcmtInfoRetrivalReq.getIdEvent()) && 0 < plcmtInfoRetrivalReq.getIdEvent()
				&& retVal == FND_SUCCESS) {
			// callCCMN45D
			FetchEventAdminDto fetchEventAdminDto = new FetchEventAdminDto();
			fetchEventAdminDto.setIdEvent(plcmtInfoRetrivalReq.getIdEvent());
			FetchEventDetailDto fetchEventDetailDto = fetchEventDetailDao.getEventDetail(fetchEventAdminDto);

			if (!ObjectUtils.isEmpty(fetchEventDetailDto)) {
				resp.setFetchEventDetailDto(fetchEventDetailDto);
				cdEventStatus = fetchEventDetailDto.getCdEventStatus();
				resp.setCdEventStatus(cdEventStatus);
				/*
				 ** If the CdEventStatus is NOT NEW, then retrieve the Placement
				 * record
				 */
				if (!ServiceConstants.EVENTSTATUS_NEW.equalsIgnoreCase(cdEventStatus)) {
					// CallCSES37D retrieve placement by id
					placementDto = placementService
							.processPlacement(placementDao.selectPlacement(plcmtInfoRetrivalReq.getIdEvent()));

				}
			} // end of ccmn45

			// CallCSYS06D for PLCMT_ISSUES_NARR
			ServiceDeliveryRtrvDtlsInDto serviceDeliveryRtrvDtlsInDto = new ServiceDeliveryRtrvDtlsInDto();
			serviceDeliveryRtrvDtlsInDto.setIdEvent(plcmtInfoRetrivalReq.getIdEvent());
			serviceDeliveryRtrvDtlsInDto.setSysTxtTablename(PLCMT_ISSUES_NARR);
			List<ServiceDeliveryRtrvDtlsOutDto> serviceDeliveryRtrvDtlsList = serviceDeliveryRtrvDtlsDao
					.getNarrExists(serviceDeliveryRtrvDtlsInDto);
			ServiceDeliveryRtrvDtlsOutDto serviceDeliveryRtrvDtlsOutDto = null;
			if (!ObjectUtils.isEmpty(serviceDeliveryRtrvDtlsList) && !serviceDeliveryRtrvDtlsList.isEmpty()) {
				serviceDeliveryRtrvDtlsOutDto = serviceDeliveryRtrvDtlsList.get(0);
				resp.setIndBLOBExistsPlcmtIssues(Boolean.TRUE);
				resp.setDtBLOBLastUpdatePlcmtIssues(serviceDeliveryRtrvDtlsOutDto.getDtLastUpdate());
			}

			// CallCSYS06D for PLCMT_DISCHG_NARR
			serviceDeliveryRtrvDtlsInDto.setSysTxtTablename(PLCMT_DISCHG_NARR);
			serviceDeliveryRtrvDtlsList = serviceDeliveryRtrvDtlsDao.getNarrExists(serviceDeliveryRtrvDtlsInDto);
			if (!ObjectUtils.isEmpty(serviceDeliveryRtrvDtlsList) && !serviceDeliveryRtrvDtlsList.isEmpty()) {
				serviceDeliveryRtrvDtlsOutDto = serviceDeliveryRtrvDtlsList.get(0);
				resp.setIndBLOBExistsPlcmtDischg(Boolean.TRUE);
				resp.setDtBLOBLastUpdatePlcmtDischg(serviceDeliveryRtrvDtlsOutDto.getDtLastUpdate());
			}

			if (!ObjectUtils.isEmpty(placementDto) && ((!ObjectUtils.isEmpty(placementDto.getIdRsrcFacil())
					&& placementDto.getIdRsrcFacil() > 0)
					|| (!ObjectUtils.isEmpty(placementDto.getIdPlcmtAdult()) && placementDto.getIdPlcmtAdult() > 0))) {
				// CallCSYS09D for DISASTER_PLAN_NARR
				serviceDeliveryRtrvDtlsInDto.setSysTxtTablename(DISASTER_PLAN_NARR);
				serviceDeliveryRtrvDtlsInDto.setIdEvent(0L);
				if (!ObjectUtils.isEmpty(placementDto.getIdRsrcFacil())) {
					serviceDeliveryRtrvDtlsInDto.setIdResource(placementDto.getIdRsrcFacil());
				} else {
					serviceDeliveryRtrvDtlsInDto.setIdResource(plcmtInfoRetrivalReq.getIdRsrcFacil());
				}
				serviceDeliveryRtrvDtlsInDto.setIdPerson(placementDto.getIdPlcmtAdult());

				serviceDeliveryRtrvDtlsList = serviceDeliveryRtrvDtlsDao
						.getServiceDeliveryDtls(serviceDeliveryRtrvDtlsInDto);
				if (!ObjectUtils.isEmpty(serviceDeliveryRtrvDtlsList) && !serviceDeliveryRtrvDtlsList.isEmpty()) {
					serviceDeliveryRtrvDtlsOutDto = serviceDeliveryRtrvDtlsList.get(0);
					resp.setIndBLOBExistsDisasterPlan(Boolean.TRUE);
					resp.setDtBLOBLastUpdateDisasterPlan(serviceDeliveryRtrvDtlsOutDto.getDtLastUpdate());
					resp.setIdBlobEvent(serviceDeliveryRtrvDtlsOutDto.getIdEvent());
				}
			}

		} // end if

		/* SIR#3582: If a facility or agency is returned, get FaHomeType */

		if (FND_SUCCESS == retVal && !ServiceConstants.FOST_ADOPT.equalsIgnoreCase(placementDto.getCdPlcmtType())
				&& (!ObjectUtils.isEmpty(placementDto.getIdRsrcFacil())
						|| !ObjectUtils.isEmpty(placementDto.getIdRsrcAgency()))) {
			Long idCapsResource = 0L;

			// call cres04d
			// If a ChildPlacingAgency (CPA) exists validate its contract
			// Otherwise, use the Facility's IdResource
			if (!ObjectUtils.isEmpty(placementDto.getIdRsrcAgency())) {
				idCapsResource = placementDto.getIdRsrcAgency();
			} else {
				idCapsResource = placementDto.getIdRsrcFacil();
			}
			ResourceDto resourceDto = capsResourceDao.getResourceById(idCapsResource);
			if (!ObjectUtils.isEmpty(resourceDto)
					&& ServiceConstants.GRO_FACILITY_TYPE_80.equalsIgnoreCase(resourceDto.getCdRsrcFacilType())
					|| ServiceConstants.RTC_FACILITY_TYPE_64.equalsIgnoreCase(resourceDto.getCdRsrcFacilType())) {
				// CallCSESB1D
				List<FacilityServiceTypeOutDto> facilityServiceTypeOutDtoList = callFacilityServiceType(
						placementDto.getIdRsrcFacil(), placementDto.getDtPlcmtStart());
				resp.setFacilityServiceTypeDtoList(facilityServiceTypeOutDtoList);
				if (ObjectUtils.isEmpty(facilityServiceTypeOutDtoList)) {
					resp.setRowQtySize(0L);
				} else {
					resp.setRowQtySize(Long.valueOf(facilityServiceTypeOutDtoList.size()));
				}

			}

		} else if (FND_SUCCESS == retVal && ServiceConstants.FOST_ADOPT.equalsIgnoreCase(placementDto.getCdPlcmtType())
				&& (!ObjectUtils.isEmpty(placementDto.getIdRsrcFacil())
						|| !ObjectUtils.isEmpty(placementDto.getIdRsrcAgency()))) {

			// call cres54d
			ResourceHistoryInDto resourceHistoryInDto = new ResourceHistoryInDto();

			// If a ChildPlacingAgency (CPA) exists validate its contract
			// Otherwise, use the Facility's IdResource
			if (!ObjectUtils.isEmpty(placementDto.getIdRsrcAgency()) && placementDto.getIdRsrcAgency() > 0) {
				resourceHistoryInDto.setIdRsrc(placementDto.getIdRsrcAgency());
			} else {
				resourceHistoryInDto.setIdRsrc(placementDto.getIdRsrcFacil());
			}
			resourceHistoryInDto.setDtPlacementStart(placementDto.getDtPlcmtStart());
			ResourceHistory resourceHistoryDto = resourceHistoryDao.getRsrcHistoryByIdAndDate(resourceHistoryInDto);
			if (!ObjectUtils.isEmpty(resourceHistoryDto)) {
				resp.setCdRsrcFaHomeType1(resourceHistoryDto.getCdRshsFaHomeType1());
				resp.setCdRsrcFaHomeType2(resourceHistoryDto.getCdRshsFaHomeType2());
				resp.setCdRsrcFaHomeType3(resourceHistoryDto.getCdRshsFaHomeType3());
				resp.setCdRsrcFaHomeType4(resourceHistoryDto.getCdRshsFaHomeType4());
				resp.setCdRsrcFaHomeType5(resourceHistoryDto.getCdRshsFaHomeType5());
				resp.setCdRsrcFaHomeType6(resourceHistoryDto.getCdRshsFaHomeType6());
				resp.setCdRsrcFaHomeType7(resourceHistoryDto.getCdRshsFaHomeType7());
				resp.setCdRsrcCategory(resourceHistoryDto.getCdRshsCategory());
				resp.setIndRsrcEmergPlace(resourceHistoryDto.getIndRshsEmergPlace());
			}
		}

		if (!ServiceConstants.STATUS_NEW.equalsIgnoreCase(cdEventStatus)
				|| !ServiceConstants.NULL_STRING.equalsIgnoreCase(cdEventStatus)
				|| (!ObjectUtils.isEmpty(resp.getFetchEventDetailDto())
						&& 0 != resp.getFetchEventDetailDto().getIdStage()
						&& resp.getFetchEventDetailDto().getIdStage() != plcmtInfoRetrivalReq.getIdStage())) {
			// call cinv51d
			long idPrimaryWrkr = stagePersonLinkDao.getPersonIdByRole(plcmtInfoRetrivalReq.getIdStage(), PRIMARY_CHILD);
			placementDto.setIdPlcmtChild(idPrimaryWrkr);

			// call csec33d Person_loc simple retrieve
			if (idPrimaryWrkr > 0) {
				PersonLocInDto personLocInDto = new PersonLocInDto();
				personLocInDto.setIdPerson(placementDto.getIdPlcmtChild());
				personLocInDto.setCdPlocType(AUTHORIZED);
				personLocInDto.setDtPlocStart(new Date());

				PersonLocOutDto personLocOutDto = personLocPersonDao.getPersonLocById(personLocInDto);
				if (!ObjectUtils.isEmpty(personLocOutDto)) {
					resp.setCdPlocChild(personLocOutDto.getCdPlocChild());
				}
			}
			// call ccmn29d Retrieve STF from StagePersonLink
			if (!ObjectUtils.isEmpty(plcmtInfoRetrivalReq.getIdPerson()) && plcmtInfoRetrivalReq.getIdPerson() > 0) {

				List<AssignmentGroupDto> assignmentGroupList = assignDao
						.getAssignmentgroup(plcmtInfoRetrivalReq.getIdStage());
				resp.setSysIndGeneric(Boolean.FALSE);
				Optional<AssignmentGroupDto> matchingObject = assignmentGroupList.stream()
						.filter(p -> p.getIdPerson().equals(plcmtInfoRetrivalReq.getIdPerson())).findFirst();
				if (!ObjectUtils.isEmpty(matchingObject) && matchingObject.isPresent()) {
					resp.setSysIndGeneric(Boolean.TRUE);
				}

			}

		}

		// callCLSS01D
		if (!ObjectUtils.isEmpty(plcmtInfoRetrivalReq.getIdRsrcFacil()) && plcmtInfoRetrivalReq.getIdRsrcFacil() > 0
				&& !ObjectUtils.isEmpty(placementDto.getIdPlcmtChild()) && placementDto.getIdPlcmtChild() > 0) {
			CommonCountRes otherChildCountRes = callOtherChildInPlacementCnt(plcmtInfoRetrivalReq.getIdRsrcFacil(),
					placementDto.getIdPlcmtChild());
			if (!ObjectUtils.isEmpty(otherChildCountRes)) {
				resp.setOtherChildCount(otherChildCountRes.getCount());
			}
		}

		// CallCSUB88D
		if (!ObjectUtils.isEmpty(placementDto)) {
			List<PlacementDto> placementDtoList = callChildPlacementsByChildId(placementDto);
			placementDto.setIndCloseADOPlcmt(Boolean.FALSE);

			for (PlacementDto childPlacementsDto : placementDtoList) {
				if (((ServiceConstants.ADOPT.equalsIgnoreCase(childPlacementsDto.getCdStage()))
						|| (ServiceConstants.PAD.equalsIgnoreCase(childPlacementsDto.getCdStage())))
						&& (!ObjectUtils.isEmpty(childPlacementsDto.getDtStageClosed()))
						&& (ObjectUtils.isEmpty(childPlacementsDto.getDtPlcmtEnd()) || ServiceConstants.MAX_DATE.equals(childPlacementsDto.getDtPlcmtEnd()))
						&& ((ServiceConstants.ADOPTIVE_PLACEMENT
								.equalsIgnoreCase(childPlacementsDto.getCdPlcmtLivArr()))
								|| (ServiceConstants.NONFPS_ADOPT_HOME
										.equalsIgnoreCase(childPlacementsDto.getCdPlcmtLivArr())))) {

					placementDto.setIndCloseADOPlcmt(Boolean.TRUE);
					/*
					 * SIR 27463 - If existing ADO/PAD placement found, send
					 * placement start date back to be compared to the page's
					 * placement start date, to determine if auto closure should
					 * happen.
					 */
					placementDto.setDtPlcmtStartADO(childPlacementsDto.getDtPlcmtStart());
					break;
				}
			}
		}
		//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification
		setBillOfRightsDates(resp, placementDto.getIdPlcmtChild());
		if(!ObjectUtils.isEmpty(placementDto)){
			Long countOfPlcmts = placementDao.getCountOfAllPlacementsByChildId(placementDto.getIdPlcmtChild());
			if(countOfPlcmts >0L){
				if(ServiceConstants.EVENTSTATUS_NEW.equalsIgnoreCase(resp.getCdEventStatus()) || (ObjectUtils.isEmpty(resp.getDtBillOfRightsInit()))){
					resp.setDisableIntialBillOfRightsDt(true);
				}
			}
		}
		List<Date> dtBORReviewEarliestLst = getEarliestReviewBillOfRights(placementDto.getIdPlcmtChild());
		if(!ObjectUtils.isEmpty(dtBORReviewEarliestLst)){
			resp.setDtBOREarliest(dtBORReviewEarliestLst.get(0));
		}
		if(!ObjectUtils.isEmpty(plcmtInfoRetrivalReq.getIdEvent())) {
			placementDto.setTemporaryAbsenceDtoList(placementDao.getTemporaryAbsenceList(plcmtInfoRetrivalReq.getIdEvent()));
			placementDto.setActiveTAsCount(temporaryAbsenceDao.getActiveTemporaryAbsencesForActivePlacement(plcmtInfoRetrivalReq.getIdEvent()));
		}
		resp.setPlacementDto(placementDto);
		return resp;
	}

	//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification
	/**
	 * @param resp
	 * @param idPlcmtChild
	 */
	private void setBillOfRightsDates(PlcmtInfoRetrivalResp resp, Long idPlcmtChild) {
		List<ChildBillOfRightsDto> rightsDto;
		rightsDto = placementDao.getBillOfRightsDatesByChildId(idPlcmtChild);
		if(!ObjectUtils.isEmpty(rightsDto)){
			ChildBillOfRightsDto dto1 = rightsDto.stream().filter( o ->ServiceConstants.CHILD_BILL_OF_RIGHTS_TYPE_INITIAL.
																				equals(o.getCdBillOfRightsType())).findAny().orElse(null);
			if(!ObjectUtils.isEmpty(dto1)){
				resp.setDtBillOfRightsInit(dto1.getDtBillOfRights());
				if(ServiceConstants.EVENT_STATUS_APRV.equalsIgnoreCase(resp.getCdEventStatus()) ||
						(!ObjectUtils.isEmpty(dto1.getIndDisableInitialBor()) && ServiceConstants.STRING_IND_Y.equals(dto1.getIndDisableInitialBor()))){
					resp.setDisableIntialBillOfRightsDt(true);
				}
			}
			ChildBillOfRightsDto dto2 = rightsDto.stream().filter( o ->ServiceConstants.CHILD_BILL_OF_RIGHTS_TYPE_REVIEW.
					equals(o.getCdBillOfRightsType())).findAny().orElse(null);
			if(!ObjectUtils.isEmpty(dto2)){
				resp.setDtBillOfRightsReview(dto2.getDtBillOfRights());
			}
		}
	}

	/**
	 * Method Name: callChildPlacementsByChildId Method Description:
	 * 
	 * @param placementDto
	 * @return
	 */
	private List<PlacementDto> callChildPlacementsByChildId(PlacementDto placementDto) {
		ResourceServiceInDto resourceServiceInDto = new ResourceServiceInDto();
		resourceServiceInDto.setIdPlcmtChild(placementDto.getIdPlcmtChild());
		List<PlacementDto> placementDtoList = placementDao.getPlacementsByChildId(resourceServiceInDto);
		return placementDtoList;
	}

	/**
	 * Method Name: callOtherChildInPlacementCnt Method Description: search for
	 * placements by resource ID and placement child it returns
	 * ulOtherChildCount for child specific resources.
	 * 
	 * @param placementDto
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonCountRes callOtherChildInPlacementCnt(long idRsrcFacil, long idPlcmtChild) {
		ResourceServiceInDto resourceServiceInDto = new ResourceServiceInDto();
		resourceServiceInDto.setIdRsrcFacil(idRsrcFacil);
		resourceServiceInDto.setIdPlcmtChild(idPlcmtChild);
		return placementDao.findOtherChildInPlacementCount(resourceServiceInDto);
	}

	/**
	 * Method Name: callFacilityServiceType Method Description: get service
	 * codes from FACILITY_SERVICE_TYPE given resource id and placement start
	 * date.
	 *
	 * @param idRsrcFacil
	 *            the id rsrc facil
	 * @param plcmtStartDt
	 *            the plcmt start dt
	 * @return the list
	 */
	private List<FacilityServiceTypeOutDto> callFacilityServiceType(Long idRsrcFacil, Date plcmtStartDt) {
		FacilityServiceTypeInDto facilityServiceTypeInDto = new FacilityServiceTypeInDto();
		facilityServiceTypeInDto.setIdResource(idRsrcFacil);
		facilityServiceTypeInDto.setDtPlacementStart(plcmtStartDt);
		return facilityServiceTypeDao.getFacilityServiceType(facilityServiceTypeInDto);
	}

	/**
	 * Method Name: getEventListDto Method Description: Retrieve the id_event
	 * for the closure stage.
	 *
	 * @param plcmtInfoRetrivalReq
	 *            the plcmt info retrival req
	 * @return the event list dto
	 */
	private EventListDto getEventListDto(PlcmtInfoRetrivalReq plcmtInfoRetrivalReq) {

		List<EventListDto> events = eventDao.getEventDtls(plcmtInfoRetrivalReq.getIdStage(),
				ServiceConstants.CLOSURE_EVENT_TYPE);
		EventListDto eventListDto = null;
		if (!ObjectUtils.isEmpty(events)) {
			eventListDto = events.get(0);
		}
		return eventListDto;
	}

	/**
	 * 
	 * Method Name: getPlacementInformation Method Description:service to
	 * retrieve the placement Resource display information Service Name :
	 * CSUB31S
	 * 
	 * @param plcmtInfoRetrivalReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public PlcmtInfoRsrcRetrivalResp getPlacementRsrcInfo(PlcmtInfoRsrcRetrivalReq plcmtReq) {
		PlcmtInfoRsrcRetrivalResp resp = new PlcmtInfoRsrcRetrivalResp();
		resp.setIndActiveStatus(Boolean.FALSE);
		resp.setIndALoc(Boolean.FALSE);
		resp.setIndHomeHist(Boolean.FALSE);
		resp.setIndChkd(Boolean.FALSE);
		resp.setSysIndGeneric(Boolean.FALSE);
		resp.setIndLocChange(Boolean.FALSE);
		List<String> errorList = new ArrayList<>();
		String retVal = ServiceConstants.EMPTY_STR;
		Boolean validContractFound = Boolean.FALSE;
		int age = 0;
		boolean fosterCareRateChange = Boolean.FALSE;
		boolean serviceCodeChange = Boolean.FALSE;
		String capsRes = FND_FAIL;
		String resHistResp = FND_FAIL;

		if (!ObjectUtils.isEmpty(plcmtReq.getDtPlcmtStart())
				&& (plcmtReq.getDtPlcmtStart().equals(DT_FOSTERCARE_RATE_CHANGE)
						|| plcmtReq.getDtPlcmtStart().after(DT_FOSTERCARE_RATE_CHANGE))) {
			fosterCareRateChange = Boolean.TRUE;
		}

		if (!ObjectUtils.isEmpty(plcmtReq.getDtPlcmtStart())
				&& (plcmtReq.getDtPlcmtStart().equals(DT_SERVICE_CODE_CHANGE)
						|| plcmtReq.getDtPlcmtStart().after(DT_SERVICE_CODE_CHANGE))) {
			serviceCodeChange = Boolean.TRUE;
		}
		// callCCMN44D
		age = getPersonAge(plcmtReq);
		retVal = FND_SUCCESS;

		// callCRES04D
		ResourceDto resourceDto = capsResourceDao.getResourceById(plcmtReq.getIdResource());
		if (!ObjectUtils.isEmpty(resourceDto)) {
			if (!ObjectUtils.isEmpty(resourceDto) && (ServiceConstants.GRO_FACILITY_TYPE_80
					.equalsIgnoreCase(resourceDto.getCdRsrcFacilType())
					|| ServiceConstants.RTC_FACILITY_TYPE_64.equalsIgnoreCase(resourceDto.getCdRsrcFacilType()))) {
				// CallCSESB1D
				List<FacilityServiceTypeOutDto> facilityServiceTypeOutDtoList = callFacilityServiceType(
						plcmtReq.getIdResource(), plcmtReq.getDtPlcmtStart());
				retVal = FND_SUCCESS;
				resp.setFacilityServiceTypeDtoList(facilityServiceTypeOutDtoList);
				if (ObjectUtils.isEmpty(facilityServiceTypeOutDtoList)) {
					resp.setRowQty2(0L);
				} else {
					resp.setRowQty2(Long.valueOf(facilityServiceTypeOutDtoList.size()));
				}
			}
			/*
			 ** if the facility type is a non-PRS home and the placement type is
			 * prs f/a home, return an error otherwise continue
			 */
			if (ServiceConstants.NONPRS_ADOPT_HOME.equalsIgnoreCase(resourceDto.getCdRsrcFacilType())
					&& ServiceConstants.FOST_ADOPT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {
				errorList.add(MSG_SUB_NON_PRS);
				retVal = FND_FAIL;
			}
			resp.setCdRsrcCategory(resourceDto.getCdRsrcCategory());
			if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(resourceDto.getIndRsrcEmergPlace())) {
				resp.setIndRsrcEmergPlace(Boolean.TRUE);
			}

		}

		if (FND_SUCCESS == retVal) {
			// callCSEC24d - calling
			CapsResourceLinkDto capsResourceLinkDto = capsResourceDao.getCapsResourceLink(plcmtReq.getIdResource(),
					CPA);
			if (!ObjectUtils.isEmpty(capsResourceLinkDto) && capsResourceLinkDto != null) {
				resp.setIdRsrcAgency(capsResourceLinkDto.getIdRsrcLinkParent());
				if (CONTRACT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
						|| NON_PAID.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
						|| JUV_PROB.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
						|| TYC.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
						|| PACE.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {
					// callCRES04D
					ResourceDto rsrcDto = capsResourceDao.getResourceById(capsResourceLinkDto.getIdRsrcLinkParent());
					if (!ObjectUtils.isEmpty(rsrcDto)) {
						resp.setNmPlcmtAgency(rsrcDto.getNmResource());
					}

				}
			} // End callCSEC24d

			/* SIR#3582: PRS F/A Home was substituted for NON_PAID */
			/* SIR 14938: Added PACE to the if statement below. */
			if (CONTRACT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
					|| FOST_ADOPT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
					|| JUV_PROB.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
					|| TYC.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
					|| PACE.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {

				if (FND_SUCCESS == retVal) {
					/*
					 ** SIR 22462 - Call csec25d if the placement type is foster
					 * adoptive, otherwise call csecc2d
					 */
					if (FOST_ADOPT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {
						// call CSEC25D
						getFacilityLocation(plcmtReq.getIdResource(), plcmtReq.getDtPlcmtStart(), resp);
						retVal = FND_SUCCESS;

					} else {
						// call csecc2d
						getFacilityLocationByRsrc(plcmtReq.getIdResource(), plcmtReq.getDtPlcmtStart(), resp);
						retVal = FND_SUCCESS;
					}
				}

				if (FND_SUCCESS == retVal) {
					/*
					 * SIR 13172 - If we're making a Foster/Adoptive placement,
					 * retrieve from RESOURCE_HISTORY the information that was
					 * valid at the time of the placement. This will allow
					 * back-dated placements to be created.
					 */
					if (!FOST_ADOPT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {
						// call cres04d
						capsRes = callCapsResource(plcmtReq.getIdResource(), resp);

					} else {
						// call cres54d
						resHistResp = callResourceHistory(plcmtReq.getIdResource(), plcmtReq.getDtPlcmtStart(), resp);
					}

					/*
					 ** Analyze return code from both CRES04D and CRES54D
					 */
					if ((!ObjectUtils.isEmpty(capsRes) && FND_SUCCESS.equals(capsRes))
							|| (!ObjectUtils.isEmpty(resHistResp) && FND_SUCCESS.equals(resHistResp))) {
						resp.setIndALoc(Boolean.FALSE);
						// call CSEC33D AUTHORIZED
						PersonLocOutDto personLocOutDto = getPersonLoc(plcmtReq.getIdPlcmtChild(),
								plcmtReq.getDtPlcmtStart(), AUTHORIZED);
						if (!ObjectUtils.isEmpty(personLocOutDto)) {
							if (!PLOC_ONE.equalsIgnoreCase(personLocOutDto.getCdPlocChild())
									&& !PLOC_TWO.equalsIgnoreCase(personLocOutDto.getCdPlocChild())
									&& !PLOC_BASIC.equalsIgnoreCase(personLocOutDto.getCdPlocChild())) {
								// Call CLSS46D - Rtrv Char By Category
								List<CharacteristicsDto> characteristicsDtoList = characteristicsDao
										.getCharByPersonIdAndCategory(plcmtReq.getIdPlcmtChild(), CHILD_PLACE_CATEGORY);
								// changing for defect fix
								if (ObjectUtils.isEmpty(characteristicsDtoList)) {
									resp.setIndALoc(Boolean.TRUE);
								}
							}
						}

						// call CSEC33D BILLING

						PersonLocOutDto personLocOutDto1 = getPersonLoc(plcmtReq.getIdPlcmtChild(),
								plcmtReq.getDtPlcmtStart(), BILLING);
						if (!ObjectUtils.isEmpty(personLocOutDto1)) {
							/*
							 * Compare the DtPlcmtStart with the PLOC End Date
							 */
							if (!ObjectUtils.isEmpty(plcmtReq.getDtPlcmtStart())
									&& !plcmtReq.getDtPlcmtStart().equals(personLocOutDto1.getDtPlocEnd())) {
								processForPlcmtStartPlocEnd(plcmtReq, resp, age, fosterCareRateChange,
										serviceCodeChange, personLocOutDto1);

							}
							// BLOC DtEnd = DtPlcmtStart. Begin Contract
							// Validate
							// for REQUESTED
							if (ObjectUtils.isEmpty(plcmtReq.getDtPlcmtStart())
									|| ServiceConstants.NULL_JAVA_DATE_DATE.equals(plcmtReq.getDtPlcmtStart())
									|| ObjectUtils.isEmpty(personLocOutDto1.getDtPlocEnd())
									|| (!ObjectUtils.isEmpty(plcmtReq.getDtPlcmtStart())
											&& plcmtReq.getDtPlcmtStart().equals(personLocOutDto1.getDtPlocEnd()))) {
								// call CSEC33D
								PersonLocOutDto personLocOutDto2 = getPersonLoc(plcmtReq.getIdPlcmtChild(),
										plcmtReq.getDtPlcmtStart(), REQUESTED);
								if (!ObjectUtils.isEmpty(personLocOutDto2)) {
									processForPlcmtStartPlocEnd(plcmtReq, resp, age, fosterCareRateChange,
											serviceCodeChange, personLocOutDto2);
								} else {
									resp.setIndLocChange(Boolean.TRUE);
								}

							}
						} else {
							PersonLocOutDto personLocOutDto3 = getPersonLoc(plcmtReq.getIdPlcmtChild(),
									plcmtReq.getDtPlcmtStart(), REQUESTED);
							if (!ObjectUtils.isEmpty(personLocOutDto3)) {
								processForPlcmtStartPlocEnd(plcmtReq, resp, age, fosterCareRateChange,
										serviceCodeChange, personLocOutDto3);
							} else {
								resp.setIndLocChange(Boolean.TRUE);
							}
						}
					} else if (FOST_ADOPT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {
						resp.setIndHomeHist(Boolean.TRUE);
					}
				}

			}

			//

			if (CONTRACT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
					|| FOST_ADOPT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
					|| JUV_PROB.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
					|| TYC.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
					|| PACE.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
					|| PCA.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {

				if (FOST_ADOPT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
						&& ObjectUtils.isEmpty(resp.getCdPlcmtService())) {

					if (BASIC_LIV_ARR.equalsIgnoreCase(plcmtReq.getCdRsrcFacilType())
							|| RCVNG_LIV_ARR.equalsIgnoreCase(plcmtReq.getCdRsrcFacilType())
							|| LGL_RISK_LIV_ARR.equalsIgnoreCase(plcmtReq.getCdRsrcFacilType())) {

						fosterRateChangeBasicLiving(resp, age, fosterCareRateChange, serviceCodeChange);

					} else if (HABILITATE_LIV_ARR.equalsIgnoreCase(plcmtReq.getCdRsrcFacilType())
							|| THERAPEUTIC_LIV_ARR.equalsIgnoreCase(plcmtReq.getCdRsrcFacilType())
							|| MED_NEEDS_LIV_ARR.equalsIgnoreCase(plcmtReq.getCdRsrcFacilType())) {

						fosterRateChangeHabilitateLiv(resp, fosterCareRateChange, serviceCodeChange);
					} else if (ADOPTIVE_LIV_ARR.equalsIgnoreCase(plcmtReq.getCdRsrcFacilType())) {
						resp.setCdPlcmtService(ADOPTIVE_HOME_SVC);
					}

				} else if (PCA.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
						&& ObjectUtils.isEmpty(resp.getCdPlcmtService())) {
					resp.setCdPlcmtService(PCA_RECURRING_SVC);
				// When a user selects a Placement Type of FPS Contracted Foster Placement, and select Living Arrangement
				// of Treatment Foster Care, the system should populate PLACEMENT.ID_PLCMT_CONTRACT as the contract
				// ID associated with the selected resource's Child Placement Agency (CPA) Treatment Foster Care
				// contract, if the resource falls under a CPA.
				// When a user selects a Placement Type of FPS Contracted Foster Placement, and select Living Arrangement
				// of Treatment Foster Care, the system should populate PLACEMENT.ID_PLCMT_CONTRACT as the contract
				// ID associated with the selected resource's Treatment Foster Care contract.
				// artf129775 - If we need to look up a treatment foster care contract, mark the response so it happens
				// during the call to getContractCountyPeriod() later.
				} else if (CONTRACT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
						&& ObjectUtils.isEmpty(resp.getCdPlcmtService())
						&& TREATMENT_FOSTER_CARE_FACIL.equalsIgnoreCase(plcmtReq.getCdRsrcFacilType())) {
					resp.setCdPlcmtService(TREATMENT_FOSTER_CARE_SVC);
				}

				// CSEC26D
				ContractCountyPeriodOutDto contractCountyPeriodOutDto = getContractCountyPeriod(plcmtReq, resp);

				if (!ObjectUtils.isEmpty(contractCountyPeriodOutDto)) {
					validContractFound = Boolean.TRUE;

					/* SIR#3582: bIndChkd is intened to signal that Alert */
					/* is Not To Be Sent. Direct Match with LOC and FLOC */
					if (!resp.getIndChkd()) {
						resp.setIndChkd(resp.getIndChkd());
					}
					if (!JUV_PROB.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
							&& !TYC.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
							&& !PACE.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {
						resp.setIdContract(contractCountyPeriodOutDto.getIdContract());
					}
					resp.setCdCnperStatus(contractCountyPeriodOutDto.getCdCnperStatus());

				} else {
					if (FOST_ADOPT.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {
						validContractFound = Boolean.FALSE;
					} else {
						validContractFound = processForNoContractNotFosterAdopt(plcmtReq, resp, validContractFound);
					}
				}
			}

			if (!validContractFound) {
				resp.setSysIndGeneric(Boolean.TRUE);
			} else {
				resp.setSysIndGeneric(Boolean.FALSE);

				if ((JUV_PROB.equalsIgnoreCase(plcmtReq.getCdPlcmtType()))) {
					// call csec73d

					List<Long> contractIdList = contractDao.getContractByVendorandPlcmtDate(JPC_VENDOR_ID,
							plcmtReq.getDtPlcmtStart());
					if (!ObjectUtils.isEmpty(contractIdList)) {
						resp.setIdContract(contractIdList.get(0));
						resp.setIndNoDataFound(Boolean.FALSE);
					} else {
						resp.setIndNoDataFound(Boolean.TRUE);
					}

				} else if (TYC.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
						|| PACE.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {
					// csec87d
					String nmBatchParam = ServiceConstants.EMPTY_STR;
					if (TYC.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {
						nmBatchParam = TYC_VENDOR_ID;
					} else if (PACE.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {
						nmBatchParam = PACE_VENDOR_ID;
					}

					List<Long> idCntractList = contractDao.getContractByVendorandPlcmtDate(nmBatchParam,
							plcmtReq.getDtPlcmtStart());

					if (!ObjectUtils.isEmpty(idCntractList)) {
						resp.setIdContract(idCntractList.get(0));
						resp.setIndNoDataFound(Boolean.FALSE);
					} else {
						resp.setIndNoDataFound(Boolean.TRUE);
					}
				}

			}
		}

		return resp;
	}

	/**
	 * Method Name: getPersonAge Method Description:
	 * 
	 * @param plcmtReq
	 * @return
	 */
	private int getPersonAge(PlcmtInfoRsrcRetrivalReq plcmtReq) {
		int age;
		PersonPortfolioInDto personPortfolioInDto = new PersonPortfolioInDto();
		personPortfolioInDto.setIdPerson(plcmtReq.getIdPlcmtChild());
		List<PersonPortfolioOutDto> personPortfolioOutDtoList = personPortfolioDao
				.getPersonRecord(personPortfolioInDto);
		PersonPortfolioOutDto personPortfolioOutDto = null;
		if (!ObjectUtils.isEmpty(personPortfolioOutDtoList)) {
			personPortfolioOutDto = personPortfolioOutDtoList.get(0);
		}
		if (!ObjectUtils.isEmpty(personPortfolioOutDto)) {
			long timeInmillis = getTimeByDateComparision(personPortfolioOutDto.getDtPersonBirth(),
					plcmtReq.getDtPlcmtStart());
			age = (int) timeInmillis / 525600;
			return age;
		} else {
			return 0;
		}
	}

	/**
	 * Method Name: processForNoContractNotFosterAdopt Method Description:
	 * 
	 * @param plcmtReq
	 * @param resp
	 * @param validContractFound
	 */
	private boolean processForNoContractNotFosterAdopt(PlcmtInfoRsrcRetrivalReq plcmtReq,
			PlcmtInfoRsrcRetrivalResp resp, Boolean validContractFound) {

		setIndActiveStatus(resp);
		if (resp.getIndActiveStatus() == Boolean.TRUE) {
			// call CLSS70D
			List<ContractCountyOutDto> contractCountyOutDtoList = getContractCountyList(plcmtReq, resp);

			for (ContractCountyOutDto contractCountyOutDto : contractCountyOutDtoList) {

				ContractContractPeriodInDto contractContractPeriodInDto = new ContractContractPeriodInDto();
				contractContractPeriodInDto.setIdContract(contractCountyOutDto.getIdContract());
				contractContractPeriodInDto.setNbrCnperPeriod(contractCountyOutDto.getNbrCncntyPeriod());
				// callCSEC72D
				ContractContractPeriodOutDto contractContractPeriodOutDto = contractDao
						.getContarctContractPeriod(contractContractPeriodInDto);
				// artf129775 If we didn't select a foster care treatment contract earlier, don't do so now.
				if (!ObjectUtils.isEmpty(contractContractPeriodOutDto) &&
						!TREATMENT_FOSTER_CARE_SVC.equalsIgnoreCase(contractCountyOutDto.getCdCncntyService())) {
					if ((CONTRACT.equalsIgnoreCase(plcmtReq.getCdPlcmtType()))
							&& ((SVH.equalsIgnoreCase(contractContractPeriodOutDto.getCdCnperStatus()))
									|| (PNT.equalsIgnoreCase(contractContractPeriodOutDto.getCdCnperStatus()))
									|| (PSH.equalsIgnoreCase(contractContractPeriodOutDto.getCdCnperStatus())))) {
						resp.setIndChkd(Boolean.FALSE);
						validContractFound = Boolean.FALSE;
					} else {
						if (!JUV_PROB.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
								&& !TYC.equalsIgnoreCase(plcmtReq.getCdPlcmtType())
								&& !PACE.equalsIgnoreCase(plcmtReq.getCdPlcmtType())) {
							resp.setIdContract(contractContractPeriodOutDto.getIdContract());
						}

						resp.setIndChkd(Boolean.TRUE);
						validContractFound = Boolean.TRUE;
						break;

					}
				} else {

					resp.setIndChkd(Boolean.FALSE);
					validContractFound = Boolean.FALSE;
				}

			} /* end for loop for CONTRACT rows returned */

		}
		return validContractFound;

	}

	/**
	 * Method Name: callCLSS70D Method Description:
	 * 
	 * @param plcmtReq
	 * @param resp
	 * @return
	 */
	private List<ContractCountyOutDto> getContractCountyList(PlcmtInfoRsrcRetrivalReq plcmtReq,
			PlcmtInfoRsrcRetrivalResp resp) {
		ContractCountyPeriodInDto contractCntyPeriodInDto = new ContractCountyPeriodInDto();

		if (!ObjectUtils.isEmpty(resp.getIdRsrcAgency())) {
			contractCntyPeriodInDto.setIdResource(resp.getIdRsrcAgency());
		} else /* Otherwise, use the Facility's IdResource */
		{
			contractCntyPeriodInDto.setIdResource(plcmtReq.getIdResource());
		}
		contractCntyPeriodInDto.setCdCncntyCounty(plcmtReq.getAddrPlcmtCnty());
		contractCntyPeriodInDto.setDtplcmtStart(plcmtReq.getDtPlcmtStart());
		List<ContractCountyOutDto> contractCountyOutDtoList = contractDao.getContarctCountyWithoutService(contractCntyPeriodInDto);
		return contractCountyOutDtoList;
	}

	/**
	 * Method Name: getActiveStatus Method Description:
	 * 
	 * @param resp
	 */
	private void setIndActiveStatus(PlcmtInfoRsrcRetrivalResp resp) {
		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus1())) {
			resp.setIndActiveStatus(Boolean.TRUE);
		} else {
			resp.setIndActiveStatus(Boolean.FALSE);
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus2()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus3()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus4()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus5()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus6()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus7()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus8()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus9()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus10()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus11()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}
		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus12()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus13()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}

		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus14()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}
		if (ACTIVE.equalsIgnoreCase(resp.getCdFlocStatus15()) && (resp.getIndActiveStatus() == Boolean.FALSE)) {
			resp.setIndActiveStatus(Boolean.TRUE);
			return;
		}
	}

	/**
	 * Method Name: callCSEC26D Method Description:
	 * 
	 * @param plcmtReq
	 * @param resp
	 */
	private ContractCountyPeriodOutDto getContractCountyPeriod(PlcmtInfoRsrcRetrivalReq plcmtReq,
			PlcmtInfoRsrcRetrivalResp resp) {
		ContractCountyPeriodInDto contractCntyPeriodInDto = new ContractCountyPeriodInDto();
		if (!ObjectUtils.isEmpty(resp.getIdRsrcAgency())) {
			contractCntyPeriodInDto.setIdResource(resp.getIdRsrcAgency());
		}

		else /* Otherwise, use the Facility's IdResource */
		{
			contractCntyPeriodInDto.setIdResource(plcmtReq.getIdResource());
		}
		contractCntyPeriodInDto.setDtScrCurrentDate(plcmtReq.getDtPlcmtStart());
		contractCntyPeriodInDto.setCdCncntyCounty(plcmtReq.getAddrPlcmtCnty());
		contractCntyPeriodInDto.setCdCncntyService(resp.getCdPlcmtService());
		return contractDao.getContractCountyPeriod(contractCntyPeriodInDto);
	}

	/**
	 * Method Name: fosterRateChangeHabilitateLiv Method Description:
	 * 
	 * @param resp
	 * @param fosterCareRateChange
	 * @param serviceCodeChange
	 */
	private void fosterRateChangeHabilitateLiv(PlcmtInfoRsrcRetrivalResp resp, boolean fosterCareRateChange,
			boolean serviceCodeChange) {
		if (fosterCareRateChange) {
			if (serviceCodeChange) {
				resp.setCdPlcmtService(MOD_RCVNG_LGL_RISK_B);
			} else {
				resp.setCdPlcmtService(HABIL_THERA_60C);
			}
		} else {
			resp.setCdPlcmtService(HABIL_THERA_PRIMARY);
		}
	}

	/**
	 * Method Name: processForFosterRateChange Method Description:
	 * 
	 * @param resp
	 * @param age
	 * @param fosterCareRateChange
	 * @param serviceCodeChange
	 */
	private void fosterRateChangeBasicLiving(PlcmtInfoRsrcRetrivalResp resp, int age, boolean fosterCareRateChange,
			boolean serviceCodeChange) {
		if (fosterCareRateChange) {
			if (serviceCodeChange) {
				resp.setCdPlcmtService(BASIC_RCVNG_LGL_RISK_A);
			} else {
				if (age >= AGE_LIMIT) {
					resp.setCdPlcmtService(BASIC_60B);
				} else {
					resp.setCdPlcmtService(BASIC_60A);
				}
			}
		} else {
			resp.setCdPlcmtService(BASIC_RCVNG_LGL_RISK);
		}
	}

	/**
	 * Method Name: processForPlcmtStartPlocEnd Method Description:
	 * 
	 * @param plcmtInfoRsrcRetrivalReq
	 * @param resp
	 * @param age
	 * @param fosterCareRateChange
	 * @param serviceCodeChange
	 * @param locAndType
	 * @param emergString
	 * @param personLocOutDto1
	 */
	private void processForPlcmtStartPlocEnd(PlcmtInfoRsrcRetrivalReq plcmtInfoRsrcRetrivalReq,
			PlcmtInfoRsrcRetrivalResp resp, int age, boolean fosterCareRateChange, boolean serviceCodeChange,
			PersonLocOutDto personLocOutDto) {
		String locAgeRequired;
		StringBuilder locAndType = new StringBuilder();
		String emergString;
		locAndType.append(personLocOutDto.getCdPlocChild());
		locAndType = locAndType.append(plcmtInfoRsrcRetrivalReq.getCdRsrcFacilType());
		locAgeRequired = locAndType.toString();
		emergString = plcmtInfoRsrcRetrivalReq.getIndPlcmetEmerg();
		locAndType.append(emergString);
		locAndType.append(plcmtInfoRsrcRetrivalReq.getCdPlcmtType());

		if (fosterCareRateChange) {
			List<CodeAttributes> codeAttributesList = codesDao.getCodesTable(AGE_REQ_CODES_TABLE, locAgeRequired);
			processForDecode(age, locAndType, codeAttributesList);

			List<CodeAttributes> codeAttributesList1 = codesDao.getCodesTable(PLCMT_CODES_TABLE2,
					locAndType.toString());
			if (!ObjectUtils.isEmpty(codeAttributesList1)) {
				resp.setCdPlcmtService(codeAttributesList1.get(0).getDecode());
			}

		} else {
			List<CodeAttributes> codeAttributesList2 = codesDao.getCodesTable(PLCMT_CODES_TABLE, locAndType.toString());
			if (!ObjectUtils.isEmpty(codeAttributesList2)) {
				resp.setCdPlcmtService(codeAttributesList2.get(0).getDecode());
			}

		}

		/*
		 * SIR#3582: if fost_adopt, check Living Arrangement
		 */
		if (FOST_ADOPT.equalsIgnoreCase(plcmtInfoRsrcRetrivalReq.getCdPlcmtType())) {

			if (BASIC_LIV_ARR.equalsIgnoreCase(plcmtInfoRsrcRetrivalReq.getCdRsrcFacilType())
					|| RCVNG_LIV_ARR.equalsIgnoreCase(plcmtInfoRsrcRetrivalReq.getCdRsrcFacilType())
					|| LGL_RISK_LIV_ARR.equalsIgnoreCase(plcmtInfoRsrcRetrivalReq.getCdRsrcFacilType())) {

				fosterRateChangeBasicLiving(resp, age, fosterCareRateChange, serviceCodeChange);

				if (!PLOC_ONE.equalsIgnoreCase(personLocOutDto.getCdPlocChild())) {
					resp.setIndChkd(Boolean.TRUE);
				}

			} else if (HABILITATE_LIV_ARR.equalsIgnoreCase(plcmtInfoRsrcRetrivalReq.getCdRsrcFacilType())
					|| THERAPEUTIC_LIV_ARR.equalsIgnoreCase(plcmtInfoRsrcRetrivalReq.getCdRsrcFacilType())
					|| MED_NEEDS_LIV_ARR.equalsIgnoreCase(plcmtInfoRsrcRetrivalReq.getCdRsrcFacilType())) {

				fosterRateChangeHabilitateLiv(resp, fosterCareRateChange, serviceCodeChange);
			} else if (ADOPTIVE_LIV_ARR.equalsIgnoreCase(plcmtInfoRsrcRetrivalReq.getCdRsrcFacilType())) {
				resp.setCdPlcmtService(ADOPTIVE_HOME_SVC);
			}

		}
	}

	/**
	 * Method Name: processForDecode Method Description:
	 * 
	 * @param age
	 * @param locAndType
	 * @param codeAttributesList
	 */
	private void processForDecode(int age, StringBuilder locAndType, List<CodeAttributes> codeAttributesList) {
		if (!ObjectUtils.isEmpty(codeAttributesList)) {
			String decode = codeAttributesList.get(0).getDecode();
			if (!ServiceConstants.STRING_IND_Y.equalsIgnoreCase(decode)) {
				if (age >= AGE_LIMIT) {
					locAndType.append(CHAR_ONE);
				} else {
					locAndType.append(CHAR_ZERO);
				}
			} else {
				locAndType.append(CHAR_X);
			}
		}
	}

	/**
	 * Method Name: getPersonLoc Method Description:
	 * 
	 * @param plcmtInfoRsrcRetrivalReq
	 * @return
	 */
	private PersonLocOutDto getPersonLoc(Long idPlcmtChild, Date plcmtStartDt, String cdPlocType) {
		PersonLocInDto personLocInDto = new PersonLocInDto();
		personLocInDto.setIdPerson(idPlcmtChild);
		personLocInDto.setCdPlocType(cdPlocType);
		personLocInDto.setDtPlocStart(plcmtStartDt);
		PersonLocOutDto personLocOutDto = personLocPersonDao.getPersonLocById(personLocInDto);
		return personLocOutDto;
	}

	/**
	 * Method Name: callResourceHistory Method Description:
	 * 
	 * @param plcmtInfoRsrcRetrivalReq
	 * @param resp
	 */
	private String callResourceHistory(Long idResource, Date plcmtStartDt, PlcmtInfoRsrcRetrivalResp resp) {

		ResourceHistoryInDto resourceHistoryInDto = new ResourceHistoryInDto();
		if (!ObjectUtils.isEmpty(resp.getIdRsrcAgency()) && resp.getIdRsrcAgency() > 0) {
			resourceHistoryInDto.setIdRsrc(resp.getIdRsrcAgency());
		} else /* Otherwise, use the Facility's IdResource */
		{
			resourceHistoryInDto.setIdRsrc(idResource);
		}
		if (ServiceConstants.NULL_JAVA_DATE_DATE.equals(plcmtStartDt))
			resourceHistoryInDto.setDtPlacementStart(ServiceConstants.MIN_DATE);
		else
			resourceHistoryInDto.setDtPlacementStart(plcmtStartDt);
		ResourceHistory resourceHistoryDto = resourceHistoryDao.getRsrcHistoryByIdAndDate(resourceHistoryInDto);
		if (!ObjectUtils.isEmpty(resourceHistoryDto)) {
			resp.setCdRsrcFaHomeType1(resourceHistoryDto.getCdRshsFaHomeType1());
			resp.setCdRsrcFaHomeType2(resourceHistoryDto.getCdRshsFaHomeType2());
			resp.setCdRsrcFaHomeType3(resourceHistoryDto.getCdRshsFaHomeType3());
			resp.setCdRsrcFaHomeType4(resourceHistoryDto.getCdRshsFaHomeType4());
			resp.setCdRsrcFaHomeType5(resourceHistoryDto.getCdRshsFaHomeType5());
			resp.setCdRsrcFaHomeType6(resourceHistoryDto.getCdRshsFaHomeType6());
			resp.setCdRsrcFaHomeType7(resourceHistoryDto.getCdRshsFaHomeType7());
			resp.setCdRsrcOwnership(resourceHistoryDto.getCdRshsOwnership());
			return FND_SUCCESS;

		}
		return FND_FAIL;
	}

	/**
	 * Method Name: callCapsResource Method Description:
	 * 
	 * @param plcmtInfoRsrcRetrivalReq
	 * @param resp
	 */
	private String callCapsResource(Long idRsrc, PlcmtInfoRsrcRetrivalResp resp) {
		Long idResource = 0L;
		if (!ObjectUtils.isEmpty(resp.getIdRsrcAgency()) && resp.getIdRsrcAgency() > 0) {
			idResource = resp.getIdRsrcAgency();
		} else /* Otherwise, use the Facility's IdResource */
		{
			idResource = idRsrc;
		}
		ResourceDto resourceDto1 = capsResourceDao.getResourceById(idResource);
		if (!ObjectUtils.isEmpty(resourceDto1)) {
			resp.setCdRsrcFaHomeType1(resourceDto1.getCdRsrcFaHomeType1());
			resp.setCdRsrcFaHomeType2(resourceDto1.getCdRsrcFaHomeType2());
			resp.setCdRsrcFaHomeType3(resourceDto1.getCdRsrcFaHomeType3());
			resp.setCdRsrcFaHomeType4(resourceDto1.getCdRsrcFaHomeType4());
			resp.setCdRsrcFaHomeType5(resourceDto1.getCdRsrcFaHomeType5());
			resp.setCdRsrcFaHomeType6(resourceDto1.getCdRsrcFaHomeType6());
			resp.setCdRsrcFaHomeType7(resourceDto1.getCdRsrcFaHomeType7());
			resp.setCdRsrcOwnership(resourceDto1.getCdRsrcOwnership());
			return FND_SUCCESS;
		}
		return FND_FAIL;
	}

	/**
	 * Method Name: getFacilityLocationByRsrc Method Description:
	 * 
	 * @param idResource
	 * @param dtPlcmtStart
	 * @param resp
	 */
	private void getFacilityLocationByRsrc(Long idResource, Date dtPlcmtStart, PlcmtInfoRsrcRetrivalResp resp) {
		FacilityLocInDto facilityLocInDto = new FacilityLocInDto();
		if (!ObjectUtils.isEmpty(resp.getIdRsrcAgency()) && resp.getIdRsrcAgency() > 0) {
			facilityLocInDto.setIdResource(resp.getIdRsrcAgency());
		} else {
			facilityLocInDto.setIdResource(idResource);
		}
		facilityLocInDto.setPlcmtStartDate(dtPlcmtStart);

		List<FacilityLocDto> facilityLocDtoList = facilityLocDao.getFclityLocByRsrcId(facilityLocInDto);

		if (!ObjectUtils.isEmpty(facilityLocDtoList)) {
			FacilityLocDto facilityLocDto = facilityLocDtoList.get(0);
			resp.setCdFlocStatus1(facilityLocDto.getCdFlocStatus1());
			resp.setCdFlocStatus2(facilityLocDto.getCdFlocStatus2());
			resp.setCdFlocStatus3(facilityLocDto.getCdFlocStatus3());
			resp.setCdFlocStatus4(facilityLocDto.getCdFlocStatus4());
			resp.setCdFlocStatus5(facilityLocDto.getCdFlocStatus5());
			resp.setCdFlocStatus6(facilityLocDto.getCdFlocStatus6());
			resp.setCdFlocStatus7(facilityLocDto.getCdFlocStatus7());
			resp.setCdFlocStatus8(facilityLocDto.getCdFlocStatus8());
			resp.setCdFlocStatus9(facilityLocDto.getCdFlocStatus9());
			resp.setCdFlocStatus10(facilityLocDto.getCdFlocStatus10());
			resp.setCdFlocStatus11(facilityLocDto.getCdFlocStatus11());
			resp.setCdFlocStatus12(facilityLocDto.getCdFlocStatus12());
			resp.setCdFlocStatus13(facilityLocDto.getCdFlocStatus13());
			resp.setCdFlocStatus14(facilityLocDto.getCdFlocStatus14());
			resp.setCdFlocStatus15(facilityLocDto.getCdFlocStatus15());

		}

	}

	/**
	 * Method Name: getFacilityLocation Method Description:
	 * 
	 * @param plcmtInfoRsrcRetrivalReq
	 * @param resp
	 */
	private void getFacilityLocation(Long idResource, Date plcmtStartDt, PlcmtInfoRsrcRetrivalResp resp) {
		FacilityLocInDto facilityLocInDto = new FacilityLocInDto();
		if (!ObjectUtils.isEmpty(resp.getIdRsrcAgency()) && resp.getIdRsrcAgency() > 0) {
			facilityLocInDto.setIdResource(resp.getIdRsrcAgency());
		} else {
			facilityLocInDto.setIdResource(idResource);
		}
		facilityLocInDto.setPlcmtStartDate(plcmtStartDt);

		List<FacilityLocDto> facilityLocDtoList = facilityLocDao.getFclityLocByResourceId(facilityLocInDto);

		if (!ObjectUtils.isEmpty(facilityLocDtoList)) {
			FacilityLocDto facilityLocDto = facilityLocDtoList.get(0);
			resp.setCdFlocStatus1(facilityLocDto.getCdFlocStatus1());
			resp.setCdFlocStatus2(facilityLocDto.getCdFlocStatus2());
			resp.setCdFlocStatus3(facilityLocDto.getCdFlocStatus3());
			resp.setCdFlocStatus4(facilityLocDto.getCdFlocStatus4());
			resp.setCdFlocStatus5(facilityLocDto.getCdFlocStatus5());
			resp.setCdFlocStatus6(facilityLocDto.getCdFlocStatus6());
			resp.setCdFlocStatus7(facilityLocDto.getCdFlocStatus7());
			resp.setCdFlocStatus8(facilityLocDto.getCdFlocStatus8());
			resp.setCdFlocStatus9(facilityLocDto.getCdFlocStatus9());
			resp.setCdFlocStatus10(facilityLocDto.getCdFlocStatus10());
			resp.setCdFlocStatus11(facilityLocDto.getCdFlocStatus11());
			resp.setCdFlocStatus12(facilityLocDto.getCdFlocStatus12());
			resp.setCdFlocStatus13(facilityLocDto.getCdFlocStatus13());
			resp.setCdFlocStatus14(facilityLocDto.getCdFlocStatus14());
			resp.setCdFlocStatus15(facilityLocDto.getCdFlocStatus15());

		}
	}

	/**
	 * Method Name: getTimeByDateComparision Method Description:
	 * 
	 * @param plcmtInfoRsrcRetrivalReq
	 * @param personPortfolioOutDto
	 * @return
	 */
	private long getTimeByDateComparision(Date persnBirthDt, Date plcmtStartDt) {

		Calendar timePlcmtStart = Calendar.getInstance();
		Calendar timePersonBirth = Calendar.getInstance();
		if (!ObjectUtils.isEmpty(plcmtStartDt)) {
			timePlcmtStart.set(plcmtStartDt.getYear(), plcmtStartDt.getMonth(), plcmtStartDt.getDay());
		} else {
			timePlcmtStart.set(0, 0, 1);
		}
		if (!ObjectUtils.isEmpty(persnBirthDt)) {
			timePersonBirth.set(persnBirthDt.getYear(), persnBirthDt.getMonth(), persnBirthDt.getDay());
		} else {
			timePersonBirth.set(0, 0, 1);
		}
		return timePlcmtStart.getTimeInMillis() - timePersonBirth.getTimeInMillis();

	}

	/**
	 * 
	 * Method Name:Method to get placement details with eventid
	 *
	 * @param idPlacementEvent
	 * @return PlacementDto @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public PlacementDto getPlacementDetails(Long idPlacementEvent) {
		return placementService.processPlacement(placementDao.selectPlacement(idPlacementEvent));
	}


	private List<Date> getEarliestReviewBillOfRights(Long idPlcmtChild){
		return placementDao.getEarliestReviewBillOfRights(idPlcmtChild);
	}

}
