/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This call is the service implementation for family plan detail.
 *Mar 8, 2018- 3:31:47 PM
 *
 */
package us.tx.state.dfps.service.familyplan.serviceimpl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.FamilyPlan;
import us.tx.state.dfps.common.domain.FamilyPlanEval;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.FamAssmtDto;
import us.tx.state.dfps.common.dto.FamAssmtFactorDto;
import us.tx.state.dfps.common.dto.FamilyPlanAssmtDto;
import us.tx.state.dfps.common.dto.FamilyPlanEvalServiceDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.ServPlanEvalRecDto;
import us.tx.state.dfps.common.dto.ServicePlanDto;
import us.tx.state.dfps.common.dto.ServicePlanEvalDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.WorkerDetailDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanDtlEvalDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanNarrDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanPartcpntDto;
import us.tx.state.dfps.familyplan.request.FamilyPlanDtlEvalReq;
import us.tx.state.dfps.familyplan.response.FamilyPlanDtlEvalRes;
import us.tx.state.dfps.familyplan.response.FamilyPlanSaveResp;
import us.tx.state.dfps.fsna.dto.CpsFsnaDomainLookupDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.dto.CheckStageInpDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.EventTaskStageService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.FamilyPlanReq;
import us.tx.state.dfps.service.common.request.ServiceAuthorizationHeaderReq;
import us.tx.state.dfps.service.common.request.SrvreferralsReq;
import us.tx.state.dfps.service.common.response.FamilyPlanRes;
import us.tx.state.dfps.service.common.response.SrvreferralsRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.conservatorship.dao.NameStagePersonLinkPersonDao;
import us.tx.state.dfps.service.contacts.dao.ContactDetailsDao;
import us.tx.state.dfps.service.cvs.dto.NameStagePersonLinkPersonInDto;
import us.tx.state.dfps.service.cvs.dto.NameStagePersonLinkPersonOutDto;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.eventutility.dao.EventUtilityDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.familyTree.bean.FamilyPlanGoalValueDto;
import us.tx.state.dfps.service.familyTree.bean.TaskGoalValueDto;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanDao;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanDtlEvalDao;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanGoalDao;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanItemDtlDao;
import us.tx.state.dfps.service.familyplan.dto.PrincipalParticipantDto;
import us.tx.state.dfps.service.familyplan.service.FamilyPlanService;
import us.tx.state.dfps.service.forms.dao.FamilyPlanEvalDao;
import us.tx.state.dfps.service.forms.dto.FamilyAssmtFactDto;
import us.tx.state.dfps.service.forms.dto.FamilyChildNameGaolDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvaItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanTaskDto;
import us.tx.state.dfps.service.forms.dto.FdgmFamilyDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.dto.RiskAreaLookUpDto;
import us.tx.state.dfps.service.forms.util.FamilyAssmtFormPrefillData;
import us.tx.state.dfps.service.forms.util.FamilyPlanEvalFormPrefillData;
import us.tx.state.dfps.service.forms.util.FamilyPlanEvalFreFormPrefillData;
import us.tx.state.dfps.service.forms.util.FamilyPlanFormPrefillData;
import us.tx.state.dfps.service.forms.util.FamilyServicePrefillData;
import us.tx.state.dfps.service.fsna.dao.FSNADao;
import us.tx.state.dfps.service.handwriting.dao.HandWritingDao;
import us.tx.state.dfps.service.handwriting.dto.HandWritingValueDto;
import us.tx.state.dfps.service.investigation.service.SrvreferralsService;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.CriminalHistoryDao;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.stageutility.service.StageUtilityService;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventPersonLinkDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

@Service
@Transactional
public class FamilyPlanServiceImpl implements FamilyPlanService {

	/** The disaster plan dao. */
	@Autowired
	private DisasterPlanDao disasterPlanDao;

	/** The stage dao. */
	@Autowired
	private StageDao stageDao;

	/** The look up dao. */
	@Autowired
	private LookupDao lookUpDao;

	/** The family plan dao. */
	@Autowired
	private FamilyPlanDao familyPlanDao;

	/** The family plan goal dao. */
	@Autowired
	private FamilyPlanGoalDao familyPlanGoalDao;

	/** The case util. */
	@Autowired
	CaseUtils caseUtil;

	/** The person util. */
	@Autowired
	PersonUtil personUtil;

	/** The name stage person link person dao. */
	@Autowired
	private NameStagePersonLinkPersonDao nameStagePersonLinkPersonDao;

	/** The family assmt form prefill data. */
	@Autowired
	private FamilyAssmtFormPrefillData familyAssmtFormPrefillData;

	/** The family plan eval fre form prefill data. */
	@Autowired
	private FamilyPlanEvalFreFormPrefillData familyPlanEvalFreFormPrefillData;

	/** The family plan eval form prefill data. */
	@Autowired
	private FamilyPlanEvalFormPrefillData familyPlanEvalFormPrefillData;

	/** The family service prefill data. */
	@Autowired
	private FamilyServicePrefillData familyServicePrefillData;

	/** The family plan eval dao. */
	@Autowired
	private FamilyPlanEvalDao familyPlanEvalDao;

	/** The family plan dtl eval dao. */
	@Autowired
	private FamilyPlanDtlEvalDao familyPlanDtlEvalDao;

	/** The event dao. */
	@Autowired
	EventDao eventDao;

	/** The handwriting dao. */
	@Autowired
	private HandWritingDao handwritingDao;

	/** The approval common service. */
	@Autowired
	ApprovalCommonService approvalCommonService;

	/** The SNA dao. */
	@Autowired
	private FSNADao fSNADao;

	/** The family plan form prefill data. */
	@Autowired
	FamilyPlanFormPrefillData familyPlanFormPrefillData;

	/** The stage utility service. */
	@Autowired
	StageUtilityService stageUtilityService;

	/** The event person link dao. */
	@Autowired
	EventPersonLinkDao eventPersonLinkDao;

	/** The event utility dao. */
	@Autowired
	EventUtilityDao eventUtilityDao;

	/** The criminal history dao. */
	@Autowired
	CriminalHistoryDao criminalHistoryDao;

	/** The contact details dao. */
	@Autowired
	ContactDetailsDao contactDetailsDao;

	/** The srvreferrals service. */
	@Autowired
	SrvreferralsService srvreferralsService;

	/** The family plan item dtl dao. */
	@Autowired
	FamilyPlanItemDtlDao familyPlanItemDtlDao;

	/** The StageEventStatus Service **/
	@Autowired
	StageEventStatusCommonService stageEventStatusService;

	/** The event Task Stage Service. */
	@Autowired
	EventTaskStageService eventTaskStageService;

	/** The Post Event Service. */
	@Autowired
	PostEventService postEventService;


	/** The Constant YES_COMMA. */
	private static final String YES_COMMA = ",";

	/** The Constant PARENT. */
	private static final String PARENT = "PRNT";

	/** The Constant CHILD. */
	private static final String CHILD = "CHLD";

	/** The Constant FPR_CHECKLIST_TASK_CODE. */
	public static final String FPR_CHECKLIST_TASK_CODE = "2306";

	/** The Constant FRE_CHECKLIST_TASK_CODE. */
	public static final String FRE_CHECKLIST_TASK_CODE = "2307";

	/** The Constant FSU_CHECKLIST_TASK_CODE. */
	public static final String FSU_CHECKLIST_TASK_CODE = "2308";

	/** The Constant INV_CHECKLIST_TASK_CODE. */
	public static final String INV_CHECKLIST_TASK_CODE = "2309";
	
	private static final String CLOSURE_TASK_CODE_FSU = "4110";
	private static final String CLOSURE_TASK_CODE_FRE = "5560";
	private static final String CLOSURE_TASK_CODE_FPR = "7010";

	public static final String C = "C";

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(FamilyPlanServiceImpl.class);

	/**
	 * This method is used to get the family plan assessment details.
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public PreFillDataServiceDto getFamilyPlanAssmt(FamilyPlanReq familyPlanReq) {

		int sMonthDiff = ServiceConstants.Zero_INT;
		int sDayDiff = ServiceConstants.Zero_INT;
		int sYear = ServiceConstants.Zero_INT;
		int indicator = ServiceConstants.Zero_INT;

		List<FamAssmtFactorDto> famAssmtFactorList[] = new ArrayList[ServiceConstants.NUMBER_OF_SUBJECTS];
		FamilyPlanAssmtDto familyPlanAssmtDto = new FamilyPlanAssmtDto();
		LinkedList<String> familyAssmtSubject = new LinkedList<String>();
		Long idPerson = ServiceConstants.ZERO_VAL;
		// Get the case name, case id and the system date
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(familyPlanReq.getIdStage());

		familyPlanAssmtDto.setGenericCaseInfoDto(genericCaseInfoDto);
		// Get the idPerson of the historically primary worker for the
		// investigation
		// stage

		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(familyPlanReq.getIdStage(),
				ServiceConstants.PRIMARY_WORKER_ROLE);
		idPerson = stagePersonDto.getIdTodoPersWorker();

		familyPlanAssmtDto.setStagePersonDto(stagePersonDto);

		// Call method to get worker full name

		WorkerDetailDto workerDetailDto = disasterPlanDao.getWorkerInfoById(idPerson);
		familyPlanAssmtDto.setWorkerDetailDto(workerDetailDto);

		// call method to get date family assessment was completed

		FamAssmtDto famAssmtDto = familyPlanDao.getFamilyAssmt(familyPlanReq.getIdEvent());
		familyPlanAssmtDto.setFamAssmtDto(famAssmtDto);

		familyAssmtSubject.add(ServiceConstants.INCIDENT_FAMILY_ASSMT_SUBJECT);
		familyAssmtSubject.add(ServiceConstants.CHILD_FAMILY_ASSMT_SUBJECT);
		familyAssmtSubject.add(ServiceConstants.ADULT_FAMILY_ASSMT_SUBJECT);
		familyAssmtSubject.add(ServiceConstants.FAMILY_FAMILY_ASSMT_SUBJECT);

		for (int i = 0; i < ServiceConstants.NUMBER_OF_SUBJECTS; i++) {
			famAssmtFactorList[i] = familyPlanDao.getFamAssmtFactors(familyPlanReq.getIdEvent(),
					familyAssmtSubject.get(i));
		}

		NameStagePersonLinkPersonInDto pInputDataRec = new NameStagePersonLinkPersonInDto();
		pInputDataRec.setIdStage(familyPlanReq.getIdStage());
		pInputDataRec.setCdStagePersType(ServiceConstants.PRINCIPAL);
		List<NameStagePersonLinkPersonOutDto> stagePersonLinkList = nameStagePersonLinkPersonDao
				.getStagePersonLinkDetails(pInputDataRec);

		/*
		 ** The following logic replaces the person age with a calcualted age
		 * from the date of birth and the date of the family assessment
		 * completion. The logic first checks to see if there is a date for the
		 * family assessment completion. Next it loops through all of the people
		 * returned and if they have a date of birth, the age is recalcualted.
		 * If neither of the above conditions are true then the age is left
		 * alone so the age found on the database will be popluated if there is
		 * one.
		 */

		Calendar cal = Calendar.getInstance();
		cal.setTime(famAssmtDto.getDtFamAssmtComplt());

		if (null != famAssmtDto.getDtFamAssmtComplt()) {
			for (int i = 0; i < stagePersonLinkList.size(); i++) {
				if (stagePersonLinkList.get(i).getDtPersonBirth() != null) {
					Calendar tempCal = Calendar.getInstance();
					tempCal.setTime(stagePersonLinkList.get(i).getDtPersonBirth());
					sMonthDiff = cal.get(Calendar.MONTH) - tempCal.get(Calendar.MONTH);
					sDayDiff = cal.get(Calendar.DAY_OF_MONTH) - tempCal.get(Calendar.DAY_OF_MONTH);

					if (sMonthDiff > 0) {
						sYear = cal.get(Calendar.YEAR) - tempCal.get(Calendar.YEAR);
					} else if (sMonthDiff < 0) {
						sYear = cal.get(Calendar.YEAR) - tempCal.get(Calendar.YEAR) - 1;

					} else if (sDayDiff < 0) {
						sYear = cal.get(Calendar.YEAR) - tempCal.get(Calendar.YEAR) - 1;

					} else {
						sYear = cal.get(Calendar.YEAR) - tempCal.get(Calendar.YEAR);
					}

					stagePersonLinkList.get(i).setLNbrPersonAge((short) sYear);
					sYear = ServiceConstants.Zero_INT;
				}
			}
		}

		/*
		 ** The following logic populates the indicator of whether the person
		 * brought back from the clsc10 dam is a parent or a child. The family
		 * assment factor code value is checked for either an 'A' or a 'C' to
		 * classify each id_person found in the family assessment factors table.
		 * Those id_persons that cannot be tied to an id_person on the family
		 * assessment factors table will be untouched so our service black box
		 * will ignore them when building the text string due to attributes
		 * entered in the Access front end.
		 */

		for (int i = 0; i < stagePersonLinkList.size(); i++) {
			for (int j = 0; j < famAssmtFactorList[ServiceConstants.CHILD_VALUE].size()
					&& indicator == ServiceConstants.Zero_INT; j++) {
				if (famAssmtFactorList[ServiceConstants.CHILD_VALUE].get(j).getIdFamAssmtPrincipal()
						.equals(stagePersonLinkList.get(i).getIdPerson())) {
					stagePersonLinkList.get(i).setBCdPersonChar(ServiceConstants.CHILD_PERSON_CHAR);
					indicator = 1;
				}

			}

			for (int j = 0; j < famAssmtFactorList[ServiceConstants.ADULT_VALUE].size()
					&& indicator == ServiceConstants.Zero_INT; j++) {
				if (famAssmtFactorList[ServiceConstants.ADULT_VALUE].get(j).getIdFamAssmtPrincipal()
						.equals(stagePersonLinkList.get(i).getIdPerson())) {
					stagePersonLinkList.get(i).setBCdPersonChar(ServiceConstants.PARENT_PERSON_CHAR);
					indicator = 1;
				}

			}

			indicator = 0;
		}

		familyPlanAssmtDto.setFamAssmtFactorList(famAssmtFactorList);
		familyPlanAssmtDto.setStagePersonLinkList(stagePersonLinkList);

		return familyAssmtFormPrefillData.returnPrefillData(familyPlanAssmtDto);

	}

	/**
	 * This method gets the information of family plan evaluation.
	 */
	@SuppressWarnings("static-access")
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public PreFillDataServiceDto getFamilyPlanEval(FamilyPlanReq familyPlanReq) {
		FamilyPlanEvalServiceDto familyPlanEvalDto = new FamilyPlanEvalServiceDto();
		Long idEvent = ServiceConstants.LONG_ZERO_VAL;
		// Get the case name, case id and the system date
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(familyPlanReq.getIdStage());

		/* Get Family Task and Goal Evaluation info */
		List<ServPlanEvalRecDto> servicePlanEvalRecList = familyPlanDao.getServPlanEvalRec(familyPlanReq.getIdEvent());
		/* Get Service plan evaluation info */
		ServicePlanEvalDto servicePlanEvalDto = familyPlanDao.getServicePlanEvalDtl(familyPlanReq.getIdEvent());
		if (servicePlanEvalDto != null) {
			idEvent = servicePlanEvalDto.getIdSvcPlanEvent();
		}

		// get service Plan info
		ServicePlanDto servicePlanDto = familyPlanDao.getServicePlanByIdEvent(idEvent);
		if (!ObjectUtils.isEmpty(servicePlanDto) && !ObjectUtils.isEmpty(servicePlanEvalDto)) {
			// If service plan type is FPP
			if (servicePlanDto.getCdSvcPlanType().equals(ServiceConstants.FPP)) {
				if (servicePlanEvalDto.getCdSvcPlanEvalDtlType().equals(ServiceConstants.REV)
						|| servicePlanEvalDto.getCdSvcPlanEvalDtlType().equals(ServiceConstants.BLANK)) {
					servicePlanDto.setDtSvcPlanNextRevw(null);
				} else if (null != servicePlanEvalDto.getDtSvcPlanEvalDtlCmpl()) {
					/*
					 ** SIR 5151 - Needed dtnextrevv to be 6 months from date the
					 * eval was completed. Copy date completed into NextRevw
					 * date and add six months to it.
					 */
					Calendar cal = Calendar.getInstance();
					cal.setTime(servicePlanEvalDto.getDtSvcPlanEvalDtlCmpl());
					cal.add(cal.MONTH, 6);

					servicePlanDto.setDtSvcPlanNextRevw(cal.getTime());

				} else if (null == servicePlanEvalDto.getDtSvcPlanEvalDtlCmpl()) {
					servicePlanDto.setDtSvcPlanNextRevw(null);
				}
			}
		}

		familyPlanEvalDto.setGenericCaseInfoDto(genericCaseInfoDto);
		familyPlanEvalDto.setServicePlanEvalRecList(servicePlanEvalRecList);
		familyPlanEvalDto.setServicePlanDto(servicePlanDto);
		familyPlanEvalDto.setServicePlanEvalDto(servicePlanEvalDto);

		return familyPlanEvalFormPrefillData.returnPrefillData(familyPlanEvalDto);
	}

	/**
	 * This method gets the family plan information.
	 */
	@SuppressWarnings("static-access")
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getFamilyPlan(FamilyPlanReq familyPlanReq) {
		Long idPerson = ServiceConstants.ZERO_VAL;
		Long idEvent = ServiceConstants.ZERO_VAL;
		boolean bFirstAdult = false;
		boolean bFirstChild = false;
		Date effectiveDate = null;
		FdgmFamilyDto fdgmFamilyDto = new FdgmFamilyDto();
		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(familyPlanReq.getIdStage());
		// CSVC42D
		List<RiskAreaLookUpDto> riskAreaLookUpDtoList = familyPlanDao.getRiskAreaLookUp(familyPlanReq.getIdEvent());

		// Call CCMN19D is used to retrieves primary worker for stage
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(familyPlanReq.getIdStage(),
				ServiceConstants.PRIMARY_WORKER_ROLE);
		if (!ObjectUtils.isEmpty(stagePersonDto.getIdTodoPersWorker())) {
			idPerson = stagePersonDto.getIdTodoPersWorker();
		}
		// CSEC01D
		WorkerDetailDto workerDetailDto = disasterPlanDao.getWorkerInfoById(idPerson);

		// CSVC43D
		FamilyPlanEvalDto familyPlanEvalDto = familyPlanEvalDao.getFamilyPlanEvalTable(familyPlanReq.getIdEvent());
		if (null != familyPlanEvalDto && null != familyPlanEvalDto.getDtNextDue()) {
			StringBuilder sb = new StringBuilder();
			Calendar cal = Calendar.getInstance();
			cal.setTime(familyPlanEvalDto.getDtNextDue());
			sb.append(cal.get(cal.MONTH));
			sb.append('/');
			sb.append(cal.get(cal.YEAR));
			familyPlanEvalDto.setTsLastUpdate(sb.toString());
		}
		if (null != familyPlanEvalDto) {
			idEvent = familyPlanEvalDto.getIdEvent();
		}

		// Get the FAMILY_PLAN data and a bit of FAMILY_PLAN_EVAL data.
		// CSVC41D
		FamilyPlanDto familyPlanDto = familyPlanEvalDao.getFamilyPlanTable(familyPlanReq.getIdEvent());

		if (null != familyPlanDto) {
			if (null != familyPlanDto.getDtNextReview()) {
				StringBuilder sb = new StringBuilder();
				Calendar cal = Calendar.getInstance();
				cal.setTime(familyPlanDto.getDtNextReview());
				sb.append(cal.get(cal.MONTH));
				sb.append('/');
				sb.append(cal.get(cal.YEAR));
				familyPlanDto.setTsLastUpdate(sb.toString());
			}
		}
		// CSVC39D
		List<FamilyPlanItemDto> familyPlanItemGoalList = familyPlanDao
				.getFamilyPlanItemGoal(familyPlanReq.getIdEvent());

		// CSVC40D
		List<FamilyPlanEvaItemDto> familyPlanEvalItemList = familyPlanEvalDao
				.getFamilyPlanEvalItems(familyPlanReq.getIdEvent());

		// CSVC45D
		List<FamilyPlanItemDto> taskForSpecRiskAreaList = familyPlanDao
				.getTaskForSpecRiskArea(familyPlanReq.getIdEvent());

		// CSVC44D
		List<FamilyPlanItemDto> familyPlanItemLists = familyPlanDao.getFamilyPlanItems(familyPlanReq.getIdEvent());

		// CCMN45D
		EventDto eventDto = eventDao.getEventByid(familyPlanReq.getIdEvent());
		if (!ObjectUtils.isEmpty(eventDto)) {
			idEvent = eventDto.getIdEvent();
		} else {
			effectiveDate = lookUpDao.getCurrentDate();
		}
		if (!ObjectUtils.isEmpty(eventDto)) {
			if (eventDto.getCdEventStatus().equals(ServiceConstants.APPROVAL)) {
				effectiveDate = eventDto.getDtLastUpdate();
			} else {
				effectiveDate = lookUpDao.getCurrentDate();
			}
		}

		// CLSC23D
		List<FamilyAssmtFactDto> familyAssmtFactDtoList = familyPlanEvalDao
				.getDistinctPricipleNames(idEvent != 0l ? idEvent : familyPlanReq.getIdEvent(), effectiveDate);

		for (int i = 0; i < familyAssmtFactDtoList.size(); i++) {
			familyAssmtFactDtoList.get(i).setComma(ServiceConstants.NO_COMMA);
			if (!familyAssmtFactDtoList.get(i).getCdPersonMaritalStatus().equals(ServiceConstants.MARITAL_STATUS_CHILD)
					|| familyAssmtFactDtoList.get(i).getCdStagePersRelInt().equals(ServiceConstants.ROLE_PARENT)
					|| familyAssmtFactDtoList.get(i).getCdStagePersRelInt().equals(ServiceConstants.ROLE_B_PARENT)) {
				familyAssmtFactDtoList.get(i).setCdStagePersType(ServiceConstants.ADULT_TYPE);

				if (!bFirstAdult) {
					bFirstAdult = true;
				} else {
					familyAssmtFactDtoList.get(i).setComma(YES_COMMA);

				}
			} else {
				familyAssmtFactDtoList.get(i).setCdStagePersType(ServiceConstants.CHILD_TYPE);

				if (!bFirstChild) {
					bFirstChild = true;
				} else {
					familyAssmtFactDtoList.get(i).setComma(YES_COMMA);

				}
			}

		}

		// CLSCB5D
		List<FamilyChildNameGaolDto> familyChildNameGaolDtoList = familyPlanEvalDao.getFamilyChildNameGaol(idEvent);
		fdgmFamilyDto.setEventDto(eventDto);
		fdgmFamilyDto.setFamilyAssmtFactDtoList(familyAssmtFactDtoList);
		fdgmFamilyDto.setFamilyChildNameGaolDtoList(familyChildNameGaolDtoList);
		fdgmFamilyDto.setFamilyPlanDto(familyPlanDto);
		fdgmFamilyDto.setFamilyPlanEvalDto(familyPlanEvalDto);
		fdgmFamilyDto.setFamilyPlanItemGoalList(familyPlanItemGoalList);

		fdgmFamilyDto.setFamilyPlanItemLists(familyPlanItemLists);
		fdgmFamilyDto.setGenericCaseInfoDto(genericCaseInfoDto);
		fdgmFamilyDto.setStagePersonDto(stagePersonDto);
		fdgmFamilyDto.setTaskForSpecRiskAreaList(taskForSpecRiskAreaList);
		fdgmFamilyDto.setWorkerDetailDto(workerDetailDto);
		fdgmFamilyDto.setFamilyPlanEvalItemList(familyPlanEvalItemList);
		fdgmFamilyDto.setRiskAreaLookUpDtoList(riskAreaLookUpDtoList);
		return familyServicePrefillData.returnPrefillData(fdgmFamilyDto);
	}

	/**
	 * Method Name : approveFamilyPlan Method Description : This method update
	 * the FAMILY_PLAN_GOAL & FAMILY_PLAN_TASK DT_APPROVAL column with the
	 * current time stamp.
	 *
	 * @param approvalId
	 *            the approval id
	 * @param caseId
	 *            the case id
	 * @return Map
	 */

	public Map<String, List<?>> approveFamilyPlan(Long approvalId, Long caseId) {
		Map<String, List<?>> output = new HashMap<>();
		List<Long> eventIdList = familyPlanDao.getEventIdFromAppEventLink(approvalId);
		Long eventIdTobeApproved = 0L;
		for (Long eventId : eventIdList) {
			if (eventId > 0) {
				eventIdTobeApproved = eventId;
			}
		}
		Long familyPlanEventId;
		boolean isFamilyPlanEvent = familyPlanDao.isEventIdFamilyPlan(eventIdTobeApproved);

		if (isFamilyPlanEvent == ServiceConstants.TRUEVAL) {
			familyPlanEventId = eventIdTobeApproved;
		} else {
			familyPlanEventId = familyPlanDao.getFamilyPlanEventId(eventIdTobeApproved);
		}

		List<FamilyPlanItemDto> familyPlanItemList = familyPlanDao.queryFamilyPlanItems(familyPlanEventId, caseId);
		List<FamilyPlanItemDto> newFamilyPlanItemList = new ArrayList<>();
		for (FamilyPlanItemDto familyPlanItemDto : familyPlanItemList) {
			if (familyPlanItemDto.getCdInitialLevelConcern() == null
					&& familyPlanItemDto.getCdCurrentLevelConcern() != null) {
				familyPlanItemDto.setCdInitialLevelConcern(familyPlanItemDto.getCdCurrentLevelConcern());
				newFamilyPlanItemList.add(familyPlanItemDto);
			}
		}

		if (!newFamilyPlanItemList.isEmpty()) {
			familyPlanDao.updateFamilyPlanItems(newFamilyPlanItemList, ServiceConstants.APPROVE);
		}

		output.put("ITEMSLIST", newFamilyPlanItemList);

		List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList = familyPlanDao.queryAllGoals(familyPlanEventId,
				caseId);
		if (!ObjectUtils.isEmpty(familyPlanGoalValueDtoList)) {
			familyPlanDao.updateGoals(familyPlanGoalValueDtoList, ServiceConstants.APPROVE);
			output.put("GOALSLIST", familyPlanGoalValueDtoList);
		} else {
			output.put("GOALSLIST", new ArrayList());
		}

		if (!ObjectUtils.isEmpty(familyPlanGoalValueDtoList)) {
			List<TaskGoalValueDto> taskGoalValueDtoList = familyPlanDao
					.queryTasksNotApproved(familyPlanGoalValueDtoList, caseId);
			if (!ObjectUtils.isEmpty(taskGoalValueDtoList)) {
				familyPlanDao.updateTasks(taskGoalValueDtoList, ServiceConstants.APPROVE);
			}
			output.put("TASKLIST", taskGoalValueDtoList);
		} else {
			output.put("TASKLIST", new ArrayList());
		}

		/*
		 * The following code inserts alerts for the secondary workers in the
		 * To-Do table only if there are secondary workers associated with the
		 * family plan and family plan or eval are approved.
		 */

		// Get the ids of secondary workers associated with the family plan or
		// eval
		// stage.
		List<Long> secondaryWorkers = familyPlanDao.queryWorkloadForSecondaryWorker(familyPlanEventId);

		// If there are values for the secondary workers, only then insert into
		// the
		// To-Do table.
		if (!TypeConvUtil.isNullOrEmpty(secondaryWorkers)) {

			//artf262865 : need this todoBean only if secondary workers exists
			// Get values from the To-Do table which were saved when family plan or
			// eval was
			// submitted before approval.
			Todo todoBean = familyPlanDao.queryToDo(approvalId);

			// Loop through the secondary workers list and get the secondary
			// worker ids and
			// then pass them for insertion
			// into To-Do table.

			for (Long workerId : secondaryWorkers) {
				Todo addTodoBean = new Todo();
				BeanUtils.copyProperties(todoBean, addTodoBean);
				String workName = familyPlanDao.getWorkersName(workerId);

				// Building the name Initials from FullName
				String[] nameToken = StringUtils.splitPreserveAllTokens(workName);
				StringBuilder nameInitialBuilder = new StringBuilder();
				String firstName = nameToken[0];
				String lastName = nameToken[2];
				if (!TypeConvUtil.isNullOrEmpty(firstName))
					nameInitialBuilder.append(firstName.charAt(0));
				if (!TypeConvUtil.isNullOrEmpty(lastName))
					nameInitialBuilder.append(lastName.charAt(0));

				// Get the worker's initial from full name and set it into
				// toCreatorNm variable.
				addTodoBean.setNmTodoCreatorInit(nameInitialBuilder.toString());

				//artf262865 : create NEW alert not a task for secondary worker
				addTodoBean.setIdTodo(null);
				addTodoBean.setCdTodoType("A");

				// Insert alerts in the To-Do table for the secondary workers
				familyPlanDao.addToDoAlertForSecondaryWorker(addTodoBean, workerId, familyPlanEventId);
			}
		}
		return output;
	}

	/**
	 * Method Name: disApproveFamilyPlan Method Description: This method will
	 * disApprove the FamilyPlan ( DT_APPROVAL = NULL) in Task and Goal Table.
	 *
	 * @param input
	 *            the input
	 * @return Long
	 * 
	 *         the service exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public Long disApproveFamilyPlan(Map input) {
		Long updatedResult = ServiceConstants.ZERO;
		List<FamilyPlanItemDto> familyPlanItemDtoList = (List<FamilyPlanItemDto>) input.get("ITEMSLIST");
		if (!ObjectUtils.isEmpty(familyPlanItemDtoList)) {
			familyPlanDao.updateFamilyPlanItems(familyPlanItemDtoList, ServiceConstants.REJECT);
		}

		List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList = (List<FamilyPlanGoalValueDto>) input.get("GOALSLIST");
		if (!ObjectUtils.isEmpty(familyPlanGoalValueDtoList)) {
			familyPlanDao.updateGoals(familyPlanGoalValueDtoList, ServiceConstants.REJECT);
		}

		List<TaskGoalValueDto> taskGoalValueDtoList = (List<TaskGoalValueDto>) input.get("TASKLIST");
		if (!ObjectUtils.isEmpty(taskGoalValueDtoList)) {
			updatedResult = familyPlanDao.updateTasks(taskGoalValueDtoList, ServiceConstants.REJECT);
		}

		return updatedResult;
	}

	/**
	 * Method Name: updateFPTasksGoals Method Description: This method fetches
	 * all the approved tasks and goals for an event id and update the
	 * FAMILY_PLAN_GOAL & FAMILY_PLAN_TASK DT_APPROVAL column with the current
	 * time stamp.
	 *
	 * @param idEvent
	 *            the id event
	 * @return Long
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public Long updateFPTasksGoals(Long idEvent) {

		Long updateResult = ServiceConstants.LongZero;
		if (!familyPlanDao.isEventIdFamilyPlanEval(idEvent)) {
			List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList = familyPlanDao.queryApprovedGoals(idEvent);
			if (!TypeConvUtil.isNullOrEmpty(familyPlanGoalValueDtoList)) {
				familyPlanDao.updateGoals(familyPlanGoalValueDtoList, ServiceConstants.REJECT);
			}
			List<TaskGoalValueDto> taskGoalValueDtoList = familyPlanDao.queryApprovedTasks(idEvent);
			if (!TypeConvUtil.isNullOrEmpty(taskGoalValueDtoList)) {
				updateResult = familyPlanDao.updateTasks(taskGoalValueDtoList, ServiceConstants.REJECT);
			}
		}
		return updateResult;
	}

	/**
	 * Method Name: getEventBean Method Description: This method will get
	 * eventStatus from Event table.
	 *
	 * @param eventId
	 *            the event id
	 * @return EventValueBeanDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public String getEventStatus(Long eventId) {
		return eventDao.getEventStatus(eventId);
	}

	/**
	 * Method Name: getChildCandidacy Method Description: This method will get
	 * Child Candidacy details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public FamilyPlanRes getChildCandidacy(FamilyPlanReq familyPlanReq) {
		final List<FamilyPlanDto> listPersonDtl = new ArrayList<>();
		FamilyPlanRes res = new FamilyPlanRes();
		FamilyPlanDto familyPlanDto = familyPlanReq.getFamilyPlanDto();
		String eventStatus = getEventStatus(familyPlanDto.getIdEvent());
		String version = familyPlanDao.getFamilyPlanVersion(familyPlanDto.getIdEvent());
		if ((ServiceConstants.EVENT_STATUS_NEW.equals(eventStatus)
				|| ServiceConstants.EVENT_STATUS_PROC.equals(eventStatus)) /*&& !"3".equals(version)*/) {
			ArrayList<Long> principalsList = familyPlanDao.fetchPrincipalsForPlan(familyPlanDto.getIdEvent());
			principalsList.forEach(personId -> {
				FamilyPlanDto fmlyPlanBean = familyPlanDao.fetchChildNotInSubcare(personId, familyPlanDto.getIdCase(),
						familyPlanDto.getIdStage());
				if (!ObjectUtils.isEmpty(fmlyPlanBean.getIdPerson())) {
					// Fetch the child risk of removal based on personId and
					// eventId
					FamilyPlanDto fmlyValueBean = familyPlanDao.fetchChildCandidacyRS(fmlyPlanBean.getIdPerson(),
							familyPlanDto.getIdEvent());
					// Get row from child candidacy table
					// if no rows are present then add the person Bean to array
					// else get values from Family Child Candidacy table and
					// then add the FamilyValueBean to array.
					if (!ObjectUtils.isEmpty(fmlyValueBean)) {
						fmlyValueBean.setNmPersonFull(fmlyPlanBean.getNmPersonFull());
						fmlyValueBean.setIdPerson(fmlyPlanBean.getIdPerson());
						fmlyValueBean.setCdCitizenShipCode(fmlyPlanBean.getCdCitizenShipCode());
						listPersonDtl.add(fmlyValueBean);
					} else {
						listPersonDtl.add(fmlyPlanBean);
					}

				}
			});
			res.setListPersonDtl(listPersonDtl);
			// Set the page mode based on the previous pagemode
			// coming from the Event list page.

			if (!familyPlanReq.getPageMode().equals(ServiceConstants.PAGE_MODE_VIEW)) {
				res.setPageMode(ServiceConstants.PAGE_MODE_MODIFY);
			}

		} else {
			// If the page is not in PROC status, then just get the candidacy
			// rows from FAMILY_CHILD_CANDIDACY
			// table and display the page in VIEW mode.
			res.setListPersonDtl(familyPlanDao.fetchAllChildCandidacy(familyPlanDto.getIdEvent()));
			res.setPageMode(ServiceConstants.PAGE_MODE_VIEW);
		}

		return res;
	}

	/**
	 * Method Name: saveChildCandidacy Method Description: This method will Save
	 * the Child Candidacy details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FamilyPlanRes saveChildCandidacy(FamilyPlanReq familyPlanReq) {

		return familyPlanDao.saveChildCandidacy(familyPlanReq);
	}

	/**
	 * Method Name: queryFamilyPlan Method Description:Query the family plan
	 * details from the database.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return FamilyPlanDto
	 * @throws HibernateException
	 *             the hibernate exception
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public FamilyPlanDto queryFamilyPlan(FamilyPlanDto familyPlanDto) {
		EventValueDto familyPlanEvent = familyPlanDao.queryEvent(familyPlanDto.getIdEvent());
		familyPlanDto.setFamilyPlanEvent(familyPlanEvent);
		return familyPlanDao.queryFamilyPlan(familyPlanDto);
	}

	/**
	 * Method Name: queryLegacyEvents Method Description: Retrieves all family
	 * plan, family plan evaluation and family assessment events given the event
	 * id of a legacy family plan, family plan evalution or family assessment.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return List<EventValueBeanDto>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public FamilyPlanDto fetchFamilyPlanDtl(FamilyPlanDto familyPlanDto) {

		Boolean isLegacyEvent = ServiceConstants.TRUEVAL;
		EventValueDto riskAssmtEvent = new EventValueDto();

		// Fetching the TaskCode
		String taskCode = familyPlanDto.getCdTask();

		// User is accessing a LEGACY FAMILY PLAN EVAL or LEGACY FAMILY
		// ASSESSMENT.
		if (taskCode.equals(ServiceConstants.LEGACY_CD_TASK_FPR_3_MONTH_EVAL)
				|| taskCode.equals(ServiceConstants.LEGACY_CD_TASK_FPR_6_MONTH_EVAL)
				|| taskCode.equals(ServiceConstants.LEGACY_CD_TASK_FPR_FAMILY_ASSMT)
				|| taskCode.equals(ServiceConstants.LEGACY_CD_TASK_FPR_SPEC_MONTH_EVAL)
				|| taskCode.equals(ServiceConstants.LEGACY_CD_TASK_FRE_3_MONTH_EVAL)
				|| taskCode.equals(ServiceConstants.LEGACY_CD_TASK_FRE_6_MONTH_EVAL)
				|| taskCode.equals(ServiceConstants.LEGACY_CD_TASK_FRE_FAMILY_ASSMT)
				|| taskCode.equals(ServiceConstants.LEGACY_CD_TASK_FRE_SPEC_MONTH_EVAL)
				|| taskCode.equals(ServiceConstants.LEGACY_CD_TASK_FSU_6_MONTH_EVAL)
				|| taskCode.equals(ServiceConstants.LEGACY_CD_TASK_FSU_FAMILY_ASSMT)
				|| taskCode.equals(ServiceConstants.LEGACY_CD_TASK_FSU_SPEC_MONTH_EVAL)) {
			// Setting the message to display as "You have accessed a legacy
			// family plan
			// (one created before the initial launch of IMPACT).
			// You can view the details of this plan by launching the
			// appropriate form."
			familyPlanDto.setLegacyFamilyPlan(ServiceConstants.MSG_FP_LEGACY_DATA);

			// Query the family plan & family plan event details.
			EventValueDto familyPlanEvent = familyPlanDao.queryEvent(familyPlanDto.getIdEvent());
			familyPlanDto.setFamilyPlanEvent(familyPlanEvent);

		} else if (taskCode.equals(ServiceConstants.CD_TASK_FPR_FAM_PLAN)
				|| taskCode.equals(ServiceConstants.CD_TASK_FRE_FAM_PLAN)
				|| taskCode.equals(ServiceConstants.CD_TASK_FSU_FAM_PLAN)) {
			// To check whether the Family Plan is a legacy Family Plan
			isLegacyEvent = familyPlanDao.checkIfEventIsLegacy(familyPlanDto.getIdEvent());
			if (isLegacyEvent.booleanValue()) {

				// Setting the message to display as "You have accessed a legacy
				// family plan
				// (one created before the initial launch of IMPACT).
				// You can view the details of this plan by launching the
				// appropriate form."
				familyPlanDto.setLegacyFamilyPlan(ServiceConstants.MSG_FP_LEGACY_DATA);

				// Query the family plan & family plan event details.
				EventValueDto familyPlanEvent = familyPlanDao.queryEvent(familyPlanDto.getIdEvent());
				familyPlanDto.setFamilyPlanEvent(familyPlanEvent);
			} else {
				// User is accessing a family plan created in IMPACT. Query the
				// family plan and
				// family plan event details.
				EventValueDto familyPlanEvent = familyPlanDao.queryEvent(familyPlanDto.getIdEvent());
				familyPlanDto.setFamilyPlanEvent(familyPlanEvent);
				familyPlanDto = familyPlanDao.queryFamilyPlan(familyPlanDto);
				// To set the value for FamilyPlanRoles
				if (familyPlanDto.getIdEvent() != ServiceConstants.ZERO) {
					List fgdmRolesList = familyPlanDao.queryFamilyPlanRole(familyPlanDto.getIdEvent());
					if (fgdmRolesList.size() > ServiceConstants.ZERO) {
						familyPlanDto.setFgdmRolesList(fgdmRolesList);
					}
					// Fetch Event Details
					EventDto eventDtl = eventDao.getEventByid(familyPlanDto.getIdEvent());
					// Fetch the Principals and Participant Details
					List<PrincipalParticipantDto> principalsParticipantsList = familyPlanDao
							.queryPrincipalsForStage(eventDtl);
					familyPlanDto.setPrincipalParticipantList(principalsParticipantsList);

					// Fetch the Principles for event
					List<PersonValueDto> personValueList = familyPlanDao.queryPrincipalsForEvent(eventDtl);
					familyPlanDto.setPrincipalsForEventList(personValueList);
					if (!ObjectUtils.isEmpty(personValueList) && personValueList.size() > 0)
						familyPlanDao.checkIfIntepreterTranslatorIsNeeded(familyPlanDto);
					// Fetch Selected Principles for the Event
					List<PersonValueDto> personValueSelectedList = familyPlanDao
							.queryPrincipalsSelectedForEvent(eventDtl);
					familyPlanDto.setPrincipalsSelectedForEventList(personValueSelectedList);
				}
				// To set the RiskAssessment Details
				riskAssmtEvent = getRiskAssmtEvent(familyPlanDto.getIdStage());
				if (!ObjectUtils.isEmpty(riskAssmtEvent)) {
					familyPlanDto.setRiskEvent(riskAssmtEvent);
				}
				// Add
				// Setting the message to display as "An Evaluation exists for
				// this Family Plan,
				// so the data has changed.
				// To view the details of the Family Plan that you selected,
				// please refer to
				// printed copies of the Family Plan."
				if (null != familyPlanDto.getFamilyPlanEvaluationList()) {
					if (familyPlanDto.getFamilyPlanEvaluationList().size() > ServiceConstants.ZERO) {
						// Setting the ErrorMessage
						familyPlanDto.setMessageByEval(ServiceConstants.MSG_FP_EVAL_EXISTS);
					}
				}
			}
			boolean isFamilyPlan = true;
			familyPlanDto.setIndFamilyPlan(isFamilyPlan);
		}
		// ------------------------------------------------------------------------
		// User is accessing an IMPACT FAMILY PLAN EVALUATION.
		else if (taskCode.equals(ServiceConstants.CD_TASK_FPR_FAM_PLAN_EVAL)
				|| taskCode.equals(ServiceConstants.CD_TASK_FRE_FAM_PLAN_EVAL)
				|| taskCode.equals(ServiceConstants.CD_TASK_FSU_FAM_PLAN_EVAL)) {
			// Get the event id of the family plan to which the evalutions
			// belongs.
			Long familyPlanEvalEventId = familyPlanDto.getIdEvent();
			Long familyPlanEventId = familyPlanDao.queryFamilyPlanEventId(familyPlanEvalEventId);
			// Query the family plan & family plan event details.
			EventValueDto familyPlanEvent = familyPlanDao.queryEvent(familyPlanEventId);
			familyPlanDto.setFamilyPlanEvent(familyPlanEvent);
			familyPlanDto.setIdEvalEvent(familyPlanEvalEventId);
			// Queries the Family Plan Details
			familyPlanDto = familyPlanDao.queryFamilyPlan(familyPlanDto);
			// To set the value for FamilyPlanRoles
			if (familyPlanDto.getIdEvent() != ServiceConstants.ZERO) {
				List fgdmRolesList = familyPlanDao.queryFamilyPlanRole(familyPlanDto.getIdEvent());
				if (fgdmRolesList.size() > ServiceConstants.ZERO) {
					familyPlanDto.setFgdmRolesList(fgdmRolesList);
				}
				// Fetch Event Details
				EventDto eventDtl = eventDao.getEventByid(familyPlanDto.getIdEvent());
				// Fetch the Principals and Participant Details
				List<PrincipalParticipantDto> principalsParticipantsList = familyPlanDao
						.queryPrincipalsForStage(eventDtl);
				familyPlanDto.setPrincipalParticipantList(principalsParticipantsList);
				// Fetch the Principles for event
				List<PersonValueDto> personValueList = familyPlanDao.queryPrincipalsForEvent(eventDtl);
				familyPlanDto.setPrincipalsForEventList(personValueList);

				if (!ObjectUtils.isEmpty(personValueList) && personValueList.size() > 0)
					familyPlanDao.checkIfIntepreterTranslatorIsNeeded(familyPlanDto);
				// Fetch Selected Principles for the Event
				List<PersonValueDto> personValueSelectedList = familyPlanDao.queryPrincipalsSelectedForEvent(eventDtl);
				familyPlanDto.setPrincipalsSelectedForEventList(personValueSelectedList);
			}
			// To set the RiskAssessment Details
			riskAssmtEvent = getRiskAssmtEvent(familyPlanDto.getIdStage());
			if (!ObjectUtils.isEmpty(riskAssmtEvent)) {
				familyPlanDto.setRiskEvent(riskAssmtEvent);
			}

			// Setting the message to display as "A more recent Evaluation
			// exists for this
			// Family Plan.
			// To view the details of the Evaluation that you selected, please
			// refer to
			// printed copies of the Family Plan.
			if (null != familyPlanDto.getFamilyPlanEvaluationList()) {
				if (familyPlanDto.getFamilyPlanEvaluationList().size() > ServiceConstants.ZERO) {
					Long mostRecentEvalEvent = familyPlanDto.getFamilyPlanEvaluationList().get(0).getIdEvent();
					if (mostRecentEvalEvent > ServiceConstants.ZERO) {
						if (!mostRecentEvalEvent.equals(familyPlanDto.getIdEvent())) {// Setting
																						// the
																						// Error
																						// Message
							familyPlanDto.setMessageByNumber(ServiceConstants.MSG_FP_NEWER_EVAL_EXISTS);
						}
					}
				}
			}
			boolean isFamilyPlanEval = true;
			familyPlanDto.setIndFamilyPlanEval(isFamilyPlanEval);
		}

		// Create a hashmap of the person id's of the children in the case that
		// are in subcare. The hashmap will be used to identify the principals
		// for the event that need a permanency goal.

		HashMap<Long, Boolean> childrenInCaseInSubcareHashMap = new HashMap();
		List<Long> childrenInCaseInSubcareList = new ArrayList<Long>();
		childrenInCaseInSubcareList = familyPlanDao.queryChildrenInCaseInSubcare(familyPlanDto.getIdCase());
		if (!ObjectUtils.isEmpty(childrenInCaseInSubcareList)) {
			for (Long childrenInCaseInSubcare : childrenInCaseInSubcareList) {
				childrenInCaseInSubcareHashMap.put(childrenInCaseInSubcare, ServiceConstants.TRUEVAL);
			}
		}
		List<EventValueDto> EventValueDtoList = familyPlanDao.queryLegacyEvents(familyPlanDto.getIdEvent());
		familyPlanDto.setFamilyPlanEventValueList(EventValueDtoList);
		if (!ObjectUtils.isEmpty(familyPlanDto.getIdEvent()) && familyPlanDto.getIdEvent() > ServiceConstants.ZERO) {
			EventDto eventDto = eventDao.getEventByid(familyPlanDto.getIdEvent());
			familyPlanDto.setDtLastUpdate(eventDto.getDtLastUpdate());
		}
		familyPlanDto.setChildrenInCaseInSubcareHashmap(childrenInCaseInSubcareHashMap);
		return familyPlanDto;

	}

	/**
	 * Method Name: deleteFamilyPlan Method Description: Delete family plan
	 * based on Event ID.
	 *
	 * @param idEvent
	 *            the id event
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteFamilyPlan(Long idEvent) {
		familyPlanDao.deleteFamilyPlan(idEvent);
	}

	/**
	 * Method Name: queryFamilyPlanTask Method Description: This method will
	 * query the Family Plan task table to retrieve the task.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return List<TaskGoalValueDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<TaskGoalValueDto> queryFamilyPlanTask(Long eventId, Long caseId) {
		log.debug("Entering method queryFamilyPlanTask in FamilyPlanServiceImpl");
		List<TaskGoalValueDto> taskGoalValueDtoList = new ArrayList<TaskGoalValueDto>();
		taskGoalValueDtoList = familyPlanDao.queryFamilyPlanTask(eventId, caseId);
		log.debug("Exiting method queryFamilyPlanTask in FamilyPlanServiceImpl");
		return taskGoalValueDtoList;
	}

	/**
	 * Method Name: queryFamilyPlanGoal Method Description: Query the Goals
	 * table to retrieve all the Goals associated with a Family Plan.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return the list
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<FamilyPlanGoalValueDto> queryFamilyPlanGoal(Long eventId, Long caseId) {
		log.debug("Entering method queryFamilyPlanGoal in FamilyPlanServiceImpl");
		List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList = new ArrayList<>();
		List<HandWritingValueDto> handWritingValueDtoList = new ArrayList<>();
		familyPlanGoalValueDtoList = familyPlanDao.queryFamilyPlanGoal(eventId, caseId);
		if (!TypeConvUtil.isNullOrEmpty(familyPlanGoalValueDtoList)) {
			for (FamilyPlanGoalValueDto familyPlanGoalValueDto : familyPlanGoalValueDtoList) {
				handWritingValueDtoList = handwritingDao.fetchHandwrittenDataForKey(String.valueOf(eventId)
						+ "_txtFamilyPlanGoal_" + String.valueOf(familyPlanGoalValueDto.getFamilyPlanGoalId()),
						Boolean.FALSE);
				if (!TypeConvUtil.isNullOrEmpty(handWritingValueDtoList)) {
					for (HandWritingValueDto handWritingValueDto : handWritingValueDtoList) {
						familyPlanGoalValueDto.setHandwritingKeyName(handWritingValueDto.getTxtKeyValue());
					}
				}
			}
		}
		log.debug("Exiting method queryFamilyPlanGoal in FamilyPlanServiceImpl");
		return familyPlanGoalValueDtoList;
	}

	/**
	 * Method Name: queryApprovedActiveTasks Method Description: This method
	 * will query the Family Plan task table to retrieve the approved active
	 * task list.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @param currentIdEvent
	 *            the current id event
	 * @return List<TaskGoalValueDto>
	 * 
	 *         the service exception
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<TaskGoalValueDto> queryApprovedActiveTasks(Long eventId, Long caseId, Long currentIdEvent) {
		log.debug("Entering method queryApprovedActiveTasks in FamilyPlanServiceImpl");
		List<TaskGoalValueDto> activeTasksValueDtoList = new ArrayList<TaskGoalValueDto>();
		activeTasksValueDtoList = familyPlanDao.queryApprovedActiveTasks(eventId, caseId, currentIdEvent);
		log.debug("Exiting method queryApprovedActiveTasks in FamilyPlanServiceImpl");
		return activeTasksValueDtoList;
	}

	/**
	 * Method Name: queryApprovedInActiveTasks Method Description: This method
	 * will query the Family Plan task table to retrieve the approved list of
	 * inactive tasks.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @param currentIdEvent
	 *            the current id event
	 * @return List<TaskGoalValueDto>
	 * 
	 *         the service exception
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<TaskGoalValueDto> queryApprovedInActiveTasks(Long eventId, Long caseId, Long currentIdEvent) {
		log.debug("Entering method queryApprovedInActiveTasks in FamilyPlanServiceImpl");
		List<TaskGoalValueDto> inactiveTaskValueDtoList = new ArrayList<TaskGoalValueDto>();
		inactiveTaskValueDtoList = familyPlanDao.queryApprovedInActiveTasks(eventId, caseId, currentIdEvent);
		log.debug("Exiting method queryApprovedInActiveTasks in FamilyPlanServiceImpl");
		return inactiveTaskValueDtoList;
	}

	/**
	 * Method Name: queryFGDMFamilyGoal Method Description: This method is used
	 * to query the Family Plan Goal.
	 *
	 * @param idCase
	 *            the id case
	 * @param idEvent
	 *            the id event
	 * @return List<FamilyPlanGoalValueDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<FamilyPlanGoalValueDto> queryFGDMFamilyGoal(Long idCase, Long idEvent) {
		return familyPlanGoalDao.queryFGDMGoal(idCase, idEvent);
	}

	/**
	 * Method Name: getAreaOfConcernList Method Description: This method returns
	 * a list of Area of Concern for a family plan goal.
	 *
	 * @param idFamilyPlanGoal
	 *            the id family plan goal
	 * @return List<String>
	 * 
	 *         the service exception
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<String> getAreaOfConcernList(Long idFamilyPlanGoal) {
		return familyPlanGoalDao.getAreaOfConcernList(idFamilyPlanGoal);
	}

	/**
	 * Gets the risk assmt event.
	 *
	 * @param idStage
	 *            the id stage
	 * @return the risk assmt event
	 */
	private EventValueDto getRiskAssmtEvent(Long idStage) {
		EventValueDto riskAssmtEvent = new EventValueDto();
		Long investigationStageId = queryInvestigationStageId(idStage);
		EventDto mostRecentRiskAssmtEvent = familyPlanDao.getEventAssessment(investigationStageId,
				ServiceConstants.IRA_ASSMT_TASK);
		if (!ObjectUtils.isEmpty(mostRecentRiskAssmtEvent)) {

			riskAssmtEvent = familyPlanDao.queryEvent(mostRecentRiskAssmtEvent.getIdEvent());
		}
		return riskAssmtEvent;
	}

	/**
	 * Query investigation stage id.
	 *
	 * @param idStage
	 *            the id stage
	 * @return the long
	 * 
	 *         the service exception
	 */
	private Long queryInvestigationStageId(Long idStage) {
		return familyPlanDao.queryInvestigationStageId(idStage);
	}

	/**
	 * Method Name:getFamilyPlanDetails Method Description: This method is used
	 * to get family plan/family plan evaluation information when adding or
	 * modifying the family plan.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the family plan details
	 */
	@Override
	public FamilyPlanDtlEvalRes getFamilyPlanDetails(FamilyPlanDtlEvalReq familyPlanReq) {
		FamilyPlanDtlEvalRes familyPlanDtlEvalRes = familyPlanDtlEvalDao.getFamilyPlanDetails(familyPlanReq);
		// to get all domains for both parent and child
		List<CpsFsnaDomainLookupDto> allDomains = fSNADao
				.queryFsna(familyPlanReq.getCdStage().equals(CodesConstant.CSTAGES_FPR) ? CodesConstant.CSTAGES_FPR
						: CodesConstant.CSTAGES_FSU);
		// filter the domains and get parent domains
		List<CpsFsnaDomainLookupDto> parentDomains = allDomains.stream()
				.filter(o -> PARENT.equals(o.getCdFSNADomainType())).collect(Collectors.toList());
		// filter the domains and get child domains
		List<CpsFsnaDomainLookupDto> childDomains = allDomains.stream()
				.filter(o -> CHILD.equals(o.getCdFSNADomainType())).collect(Collectors.toList());
		familyPlanDtlEvalRes.setParentDomainCodes(parentDomains);
		familyPlanDtlEvalRes.setChildDomainCodes(childDomains);
		familyPlanDtlEvalRes.getFamilyPlanPartcpntDto().stream().forEach(familyPlanPartcpntDto -> {
			if (C.equals(familyPlanPartcpntDto.getIndPartcpntType())
					&& !ObjectUtils.isEmpty(familyPlanPartcpntDto.getIdPerson())) {
				if (stageUtilityService.isChildInSubStage(familyPlanPartcpntDto.getIdPerson())) {
					familyPlanPartcpntDto.setChildInSubStage(Boolean.TRUE);
				}
			}
		});
		
		return familyPlanDtlEvalRes;
	}

	/**
	 * Method Name: saveFamilyPlan
	 * 
	 * Method Description: Based on the taskcodes, this method delegates the
	 * save control to FP detail or evaluation accordingly.
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @return the family plan save resp
	 * 
	 *         the service exception
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FamilyPlanSaveResp saveFamilyPlan(FamilyPlanDtlEvalReq familyPlanDtlEvalReq) {

		FamilyPlanSaveResp familyPlanSaveResp = new FamilyPlanSaveResp();

		String taskCode = familyPlanDtlEvalReq.getCdTask();
		// check if the Request is to save the Family Plan Detail or Family Plan
		// Evaluation Detail
		if (StringUtils.equals(taskCode, ServiceConstants.FAMILY_PLAN_TASK_FSU)
				|| StringUtils.equals(taskCode, ServiceConstants.FAMILY_PLAN_TASK_FRE)
				|| StringUtils.equals(taskCode, ServiceConstants.FAMILY_PLAN_TASK_FPR)) {
			familyPlanDtlEvalReq.setFamilyPlanDtlInd(Boolean.TRUE);
			familyPlanSaveResp = saveFamilyPlanDetail(familyPlanDtlEvalReq);
		} else if (StringUtils.equals(taskCode, ServiceConstants.FAMILY_PLAN_EVALUATION_TASK_FSU)
				|| StringUtils.equals(taskCode, ServiceConstants.FAMILY_PLAN_EVALUATION_TASK_FRE)
				|| StringUtils.equals(taskCode, ServiceConstants.FAMILY_PLAN_EVALUATION_TASK_FPR)) {
			familyPlanDtlEvalReq.setFamilyPlanDtlInd(Boolean.FALSE);
			familyPlanSaveResp = saveFamilyPlanEvaluationDetail(familyPlanDtlEvalReq);
		} else {
			throw new ServiceLayerException(ServiceConstants.INVALID_TASKCODE);
		}
		//After saving, invalid pending closure stage task
		String cdStage = familyPlanDtlEvalReq.getCdStage();
		Long idStage = familyPlanDtlEvalReq.getIdStage();
		String cdTask = null;
		if (cdStage.equals(CodesConstant.CSTAGES_FSU)) {
			cdTask = CLOSURE_TASK_CODE_FSU;
		} else if (cdStage.equals(CodesConstant.CSTAGES_FRE)) {
			cdTask = CLOSURE_TASK_CODE_FRE;
		} else if (cdStage.equals(CodesConstant.CSTAGES_FPR)) {
			cdTask = CLOSURE_TASK_CODE_FPR;
		}
		List<EventDto> eventDtoList = eventDao.getEventByStageIDAndTaskCode(idStage, cdTask);
		if (!CollectionUtils.isEmpty(eventDtoList)) {
			EventDto eventDto = eventDtoList.get(0);
			if (CodesConstant.CEVTSTAT_PEND.equals(eventDto.getCdEventStatus())) {
				ApprovalCommonInDto pInputMsg = new ApprovalCommonInDto();
				ApprovalCommonOutDto pOutputMsg = new ApprovalCommonOutDto();
				pInputMsg.setIdEvent(eventDto.getIdEvent());
				// Call Service to invalidate approvals
				approvalCommonService.InvalidateAprvl(pInputMsg, pOutputMsg);
			}
		}
		

		return familyPlanSaveResp;
	}

	/**
	 * Method Name: saveFamilyPlanEvaluationDetail
	 * 
	 * Method Description: The below method created or updates Family Plan
	 * Evaluation.
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @return the family plan save resp
	 */
	private FamilyPlanSaveResp saveFamilyPlanEvaluationDetail(FamilyPlanDtlEvalReq familyPlanDtlEvalReq) {
		FamilyPlanSaveResp familyPlanSaveResp = new FamilyPlanSaveResp();
		FamilyPlanDtlEvalDto incomingfamilyPlanEvalDto = familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto();
		ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq = new ServiceAuthorizationHeaderReq();
		PostEventOPDto eventDto = new PostEventOPDto();
		Long idFamilyPlanEval = null;
		// check if id family plan is null
		if (TypeConvUtil.isNullOrEmpty(incomingfamilyPlanEvalDto.getIdFamilyPlanEvaluation())) {
			// create event
			serviceAuthorizationHeaderReq.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
			eventDto = createOrUpdateEvent(serviceAuthorizationHeaderReq, familyPlanDtlEvalReq, Boolean.TRUE);
			familyPlanDtlEvalReq.setIdFamilyPlanEvalEvent(eventDto.getIdEvent());
			// dao call
			idFamilyPlanEval = familyPlanDao.saveFamilyPlan(familyPlanDtlEvalReq);

		} else {
			// modify flow
			// update event
			serviceAuthorizationHeaderReq.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
			eventDto = createOrUpdateEvent(serviceAuthorizationHeaderReq, familyPlanDtlEvalReq, Boolean.TRUE);
			familyPlanDtlEvalReq.setIdFamilyPlanEvalEvent(eventDto.getIdEvent());
			// dao call
			idFamilyPlanEval = familyPlanDao.updateFamilyPlan(familyPlanDtlEvalReq);
			updateDtDeterminationChildCandidacy(familyPlanDtlEvalReq, eventDto);

		}
		familyPlanSaveResp.setIdEvent(eventDto.getIdEvent());
		familyPlanSaveResp.setIdFamilyPlanEval(idFamilyPlanEval);
		return familyPlanSaveResp;
	}

	/**
	 *Method Name:	updateDtDeterminationChildCandidacy
	 *Method Description:
	 *@param familyPlanDtlEvalReq
	 *@param eventDto
	 */
	private void updateDtDeterminationChildCandidacy(FamilyPlanDtlEvalReq familyPlanDtlEvalReq,
			PostEventOPDto eventDto) {
		// this block will be executed if the code stage type does not start
		// with "C-"
		if (!familyPlanDtlEvalReq.getCdStageType().startsWith(ServiceConstants.SVC_CD_STAGE_TYPE_CRSR)) {
			EventDto eventDtoChildPlan = eventDao.getEventByid(eventDto.getIdEvent());
			//Add complete status for defect 14364
			if (ServiceConstants.EVENTSTATUS_NEW.equals(eventDtoChildPlan.getCdEventStatus())
					|| ServiceConstants.EVENTSTATUS_PROCESS.equals(eventDtoChildPlan.getCdEventStatus())
					|| ServiceConstants.EVENTSTATUS_COMPLETE.equals(eventDtoChildPlan.getCdEventStatus())) {
				familyPlanDao.deleteChildCandidacy(eventDto.getIdEvent());
				if (!ObjectUtils.isEmpty(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getDtCompleted())) {
					// Update the Determination and Redetermination dates in the
					// FAMILY_CHILD_CANDIDACY table
					familyPlanDao.updateChildCandidacyDeterminations(eventDto.getIdEvent());
					// If there are any rows present in the PERSON_ELIGIBILITY
					// table , then that row is end dated and a new row is
					// inserted.
					familyPlanDao.fetchAndInsertRecsChildCandidacy(eventDto.getIdEvent());
				}
			}
		}
	}

	/**
	 * Method Name: saveFamilyPlanDetail Method
	 * 
	 * Description: The below method creates or updates Family Plan.
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @return the family plan save resp
	 */
	private FamilyPlanSaveResp saveFamilyPlanDetail(FamilyPlanDtlEvalReq familyPlanDtlEvalReq) {
		FamilyPlanDtlEvalDto incomingfamilyPlanDto = familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		FamilyPlanSaveResp familyPlanSaveResp = new FamilyPlanSaveResp();
		PostEventOPDto eventDto = new PostEventOPDto();
		Long idFamilyPlan = null;
		// check if id family plan is null
		if (TypeConvUtil.isNullOrEmpty(incomingfamilyPlanDto.getIdFamilyPlan())) {

			// create event
			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
			eventDto = createOrUpdateEvent(serviceReqHeaderDto, familyPlanDtlEvalReq, Boolean.FALSE);
			familyPlanDtlEvalReq.setIdFamilyPlanEvent(eventDto.getIdEvent());
			// dao call
			idFamilyPlan = familyPlanDao.saveFamilyPlan(familyPlanDtlEvalReq);

		} else {

			// modify flow
			// update event
			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
			eventDto = createOrUpdateEvent(serviceReqHeaderDto, familyPlanDtlEvalReq, Boolean.FALSE);
			familyPlanDtlEvalReq.setIdFamilyPlanEvent(eventDto.getIdEvent());
			
			// dao call
			
			String indFormchanged = familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getIndFormChanged();
			
			if(ServiceConstants.Y.equals(indFormchanged))
				idFamilyPlan = familyPlanDao.updateFamilyPlan(familyPlanDtlEvalReq);
			
			updateDtDeterminationChildCandidacy(familyPlanDtlEvalReq, eventDto);

		}

		// artf128746 - To Do Alerts creation for Family Plan on 'Save and Submit' when clicked by worker or 'Save'
		// when clicked by supervisor while in approval mode
		if ((familyPlanDtlEvalReq.getIndSubmit() || familyPlanDtlEvalReq.isIndApprovalMode())
				&& !ObjectUtils.isEmpty(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getDtNextReview())) {

			FamilyPlanDto familyPlanDto = new FamilyPlanDto();
			familyPlanDto.setIdCase(familyPlanDtlEvalReq.getIdCase());
			familyPlanDto.setIdStage(familyPlanDtlEvalReq.getIdStage());
			familyPlanDto.setCdStage(familyPlanDtlEvalReq.getCdStage());
			familyPlanDto.setNmStage(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getNmCase());
			familyPlanDto.setFamilyPlanEvent(new EventValueDto());
			familyPlanDto.getFamilyPlanEvent().setIdEvent(eventDto.getIdEvent());
			familyPlanDto.setIdUser(familyPlanDtlEvalReq.getIdUser());
			familyPlanDto.setDtNextDue(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getDtNextReview());
			familyPlanDto.setIdPrimaryWorker(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getIdWrkr());
			familyPlanDao.updateToDoforNextReview(familyPlanDto);
		}

		familyPlanSaveResp.setIdEvent(eventDto.getIdEvent());
		familyPlanSaveResp.setIdFamilyPlan(idFamilyPlan);

		return familyPlanSaveResp;
	}

	/**
	 * Method Name: callPostEvent
	 * 
	 * Method Description: This method calls the common post Event method to
	 * create/update an Event.
	 *
	 * @param serviceReqHeaderDto
	 *            the service req header dto
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @param indFamilyPlanEval
	 *            the ind family plan eval
	 * @return the post event OP dto
	 */
	private PostEventOPDto createOrUpdateEvent(ServiceReqHeaderDto serviceReqHeaderDto,
			FamilyPlanDtlEvalReq familyPlanDtlEvalReq, Boolean indFamilyPlanEval) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		Long idEvent = indFamilyPlanEval ? familyPlanDtlEvalReq.getIdFamilyPlanEvalEvent()
				: familyPlanDtlEvalReq.getIdFamilyPlanEvent();
		// if event status is pending then invalidate the associated to do
		if (!TypeConvUtil.isNullOrEmpty(idEvent)) {
			Event event = eventDao.getEventById(idEvent);
			if (!familyPlanDtlEvalReq.isIndApprovalMode() && event.getCdEventStatus().equals(ServiceConstants.EVENT_STATUS_PEND)) {
				ApprovalCommonInDto pInputMsg = new ApprovalCommonInDto();
				ApprovalCommonOutDto pOutputMsg = new ApprovalCommonOutDto();
				pInputMsg.setIdEvent(idEvent);
				// Call Service to invalidate approvals
				approvalCommonService.InvalidateAprvl(pInputMsg, pOutputMsg);
			}
		}

		String eventStatus =familyPlanDtlEvalReq.getIndSubmit() ? ServiceConstants.EVENT_STATUS_COMP
				: ServiceConstants.EVENTSTATUS_PROCESS;
		if (familyPlanDtlEvalReq.isIndApprovalMode()) {
			eventStatus = CodesConstant.CEVTSTAT_PEND;
		}
		postEventIPDto.setCdEventStatus(eventStatus);
		postEventIPDto.setCdTask(familyPlanDtlEvalReq.getCdTask());
		postEventIPDto.setCdEventType(ServiceConstants.EVENT_TYPE_PLN);
		// set Event Description
		postEventIPDto.setEventDescr(getFamilyPlanEventDescription(familyPlanDtlEvalReq, indFamilyPlanEval));
		postEventIPDto.setIdEvent(idEvent);
		postEventIPDto.setIdStage(familyPlanDtlEvalReq.getIdStage());
		postEventIPDto.setDtEventOccurred(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getDtEventOccurred());
		if (ObjectUtils.isEmpty(postEventIPDto.getDtEventOccurred()))
			postEventIPDto.setDtEventOccurred(new Date());
		postEventIPDto.setIdCase(familyPlanDtlEvalReq.getIdCase());
		postEventIPDto.setIdPerson(familyPlanDtlEvalReq.getIdUser());
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
		return postEventOPDto;
	}

	/**
	 * Method Name: setFamilyPlanEventDescription
	 * 
	 * Method Description: This helper method sets the event description based
	 * on status name, status type, family plan type and completion or
	 * evaluation dates.
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @param indFamilyPlanEval
	 *            the ind family plan eval
	 * @return eventDesc
	 */
	private String getFamilyPlanEventDescription(FamilyPlanDtlEvalReq familyPlanDtlEvalReq, Boolean indFamilyPlanEval) {

		StringBuilder eventDesc = new StringBuilder();
		eventDesc.append(TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getCdStage()) ? ServiceConstants.EMPTY_STRING
				: familyPlanDtlEvalReq.getCdStage()).append(ServiceConstants.CHAR_CONSTANT_SPACE);
		eventDesc.append(TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getCdStageType())
				? ServiceConstants.EMPTY_STRING : familyPlanDtlEvalReq.getCdStageType());
		// if it is Family Plan Detail
		if (!indFamilyPlanEval) {
			eventDesc.append(ServiceConstants.CHAR_CONSTANT_SPACE).append(ServiceConstants.FAMILY_PLAN);

			// If save and submit, the status of the
			// family plan event should be COMP (or possibly PEND).
			if (familyPlanDtlEvalReq.getIndSubmit()) {
				eventDesc.append(ServiceConstants.CHAR_CONSTANT_SPACE).append(ServiceConstants.COMPLETED_LOWERCASE)
						.append(ServiceConstants.CHAR_CONSTANT_SPACE);
				eventDesc.append(
						TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getDtCompleted())
								? ServiceConstants.EMPTY_STRING
								: DateUtils.formatDatetoString(
										familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getDtCompleted()));
			}

		} else {
			// for Family Plan Evaluations
			eventDesc.append(ServiceConstants.CHAR_CONSTANT_SPACE).append(ServiceConstants.FAMILY_PLAN_EVENT);
			eventDesc.append(ServiceConstants.CHAR_CONSTANT_SPACE)
					.append(TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getIdFamilyPlanEvent())
							? ServiceConstants.EMPTY_STRING : familyPlanDtlEvalReq.getIdFamilyPlanEvent());
			eventDesc.append(ServiceConstants.CHAR_CONSTANT_SPACE).append(ServiceConstants.COMPLETED_LOWERCASE);
			eventDesc.append(ServiceConstants.CHAR_CONSTANT_SPACE)
					.append(TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getDtCompleted())
							? ServiceConstants.EMPTY_STRING
							: DateUtils.formatDatetoString(
									familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getDtCompleted()));
			eventDesc.append(ServiceConstants.CHAR_CONSTANT_SPACE).append(ServiceConstants.EVALUATED);
			eventDesc.append(ServiceConstants.CHAR_CONSTANT_SPACE).append(
					TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getDtCurrentReview())
							? ServiceConstants.EMPTY_STRING
							: DateUtils.formatDatetoString(
									familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getDtCurrentReview()));
		}

		return eventDesc.toString();

	}

	/**
	 * gets the family plan information required to launch the form.
	 */
	@Override
	public PreFillDataServiceDto getFamilyPlanFormDetails(FamilyPlanDtlEvalReq familyPlanReq) {

		List<CpsFsnaDto> cpsFsnaList = familyPlanDao.getFamilyPlanRequest(familyPlanReq);
		for (CpsFsnaDto cpsFsna : cpsFsnaList) {
			familyPlanReq.setIdFsnaEvent(cpsFsna.getIdCpsFsna());
			if ((!ObjectUtils.isEmpty(cpsFsna.getIdPrmryCrgvrPrnt()))
					&& (cpsFsna.getIdCreatedPerson().equals(cpsFsna.getIdPrmryCrgvrPrnt()))) {
				familyPlanReq.setIdPrmryPrntCrgvr(cpsFsna.getIdCreatedPerson());
			} else if ((!ObjectUtils.isEmpty(cpsFsna.getIdSecndryCrgvrPrnt()))
					&& (cpsFsna.getIdCreatedPerson().equals(cpsFsna.getIdSecndryCrgvrPrnt()))) {
				familyPlanReq.setIdScndryPrntCrgvr(cpsFsna.getIdCreatedPerson());
			}
		}
		FamilyPlanDtlEvalRes familyPlanDtlEvalRes = getFamilyPlanDetails(familyPlanReq);
		familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto()
				.setNmCase(caseUtil.getNmCase(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getIdCase()));
		List<String> partipLi = new ArrayList<String>();
		List<String> partipLi2 = new ArrayList<String>();
		for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanDtlEvalRes.getFamilyPlanPartcpntDto()) {
			if (familyPlanPartcpntDto.getIndPartcpntType().equalsIgnoreCase("P")) {
				partipLi.add(familyPlanPartcpntDto.getNmParticipant());
			}
		}
		for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanDtlEvalRes.getFamilyPlanPartcpntDto()) {
			if (familyPlanPartcpntDto.getIndPartcpntType().equalsIgnoreCase("C")) {
				partipLi2.add(familyPlanPartcpntDto.getNmParticipant());
			}
		}
		//artf258293 : space is needed to wrap content when we download the form
		familyPlanDtlEvalRes.setTxtPrimParticp(String.join("; ", partipLi.toArray(new String[0])));
		familyPlanDtlEvalRes.setChildAssessed(String.join("; ", partipLi2.toArray(new String[0])));
		familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().setNmPersonFull(
				personUtil.getPersonFullName(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getIdWrkr()));
		familyPlanDtlEvalRes.setCdStage(familyPlanReq.getCdStage());
		return familyPlanFormPrefillData.returnPrefillData(familyPlanDtlEvalRes);

	}

	/**
	 * gets the family plan evaluation information required to launch the form.
	 */
	@Override
	public PreFillDataServiceDto getFamilyPlanEvalFormDetails(FamilyPlanDtlEvalReq familyPlanReq) {

		List<CpsFsnaDto> cpsFsnaList = familyPlanDao.getFamilyPlanEvalRequest(familyPlanReq);

		for (CpsFsnaDto cpsFsna : cpsFsnaList) {
			familyPlanReq.setIdFsnaEvent(cpsFsna.getIdCpsFsna());
			if ((!ObjectUtils.isEmpty(cpsFsna.getIdPrmryCrgvrPrnt()))
					&& (cpsFsna.getIdCreatedPerson().equals(cpsFsna.getIdPrmryCrgvrPrnt()))) {
				familyPlanReq.setIdPrmryPrntCrgvr(cpsFsna.getIdCreatedPerson());
			} else if ((!ObjectUtils.isEmpty(cpsFsna.getIdSecndryCrgvrPrnt()))
					&& (cpsFsna.getIdCreatedPerson().equals(cpsFsna.getIdSecndryCrgvrPrnt()))) {
				familyPlanReq.setIdScndryPrntCrgvr(cpsFsna.getIdCreatedPerson());
			}
		}

		FamilyPlanDtlEvalRes familyPlanDtlEvalRes = getFamilyPlanDetails(familyPlanReq);
		familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto()
				.setNmCase(caseUtil.getNmCase(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getIdCase()));
		List<String> partipLi = new ArrayList<String>();
		List<String> partipLi2 = new ArrayList<String>();
		for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanDtlEvalRes.getFamilyPlanPartcpntDto()) {
			if (familyPlanPartcpntDto.getIndPartcpntType().equalsIgnoreCase("P")) {
				partipLi.add(familyPlanPartcpntDto.getNmParticipant());
			}
		}
		for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanDtlEvalRes.getFamilyPlanPartcpntDto()) {
			if (familyPlanPartcpntDto.getIndPartcpntType().equalsIgnoreCase("C")) {
				partipLi2.add(familyPlanPartcpntDto.getNmParticipant());
			}
		}
		familyPlanDtlEvalRes.setChildAssessed(String.join(";", partipLi2.toArray(new String[0])));
		familyPlanDtlEvalRes.setTxtPrimParticp(String.join(";", partipLi.toArray(new String[0])));
		familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().setNmPersonFull(
				personUtil.getPersonFullName(familyPlanDtlEvalRes.getFamilyPlanDtlEvalDto().getIdWrkr()));
		familyPlanDtlEvalRes.setCdStage(familyPlanReq.getCdStage());
		return familyPlanEvalFreFormPrefillData.returnPrefillData(familyPlanDtlEvalRes);

	}

	/**
	 * This method gets the family plan narrative information.
	 */
	@Override
	public FamilyPlanDtlEvalRes getFamilyPlanNarravtive(Long idStage, Long idEvent, boolean isFamilyPlanEval) {
		FamilyPlanDtlEvalRes familyRes = new FamilyPlanDtlEvalRes();
		List<FamilyPlanNarrDto> familyPlanNarrList = familyPlanDao.getFamilyPlanNarrList(idStage, idEvent);
		familyRes.setFamilyPlanNarrList(familyPlanNarrList);

		Date dtLastUpdate = null;

		if (!ObjectUtils.isEmpty(idEvent) && idEvent > 0) {
			if (isFamilyPlanEval) {
				FamilyPlanEval familyPlanEval = familyPlanDao.getFamilyPlanEval(idEvent);
				dtLastUpdate = familyPlanEval.getDtLastUpdate();
			} else {
				FamilyPlan familyPlan = familyPlanDao.getFamilyPlan(idEvent);
				dtLastUpdate = familyPlan.getDtLastUpdate();
			}
		}
		familyRes.setDtLastUpdate(dtLastUpdate);
		return familyRes;
	}
	
	@Override
	public FamilyPlanDtlEvalRes getFamilyPlanVersions(Long idEvent) {
		FamilyPlanDtlEvalRes familyRes = new FamilyPlanDtlEvalRes();
		List<FamilyPlanNarrDto> familyPlanNarrList = new ArrayList<FamilyPlanNarrDto>();
		
		if(!ObjectUtils.isEmpty(idEvent) && idEvent > 0)
			familyPlanNarrList = familyPlanDao.getFamilyPlanVersions(idEvent);
		familyRes.setFamilyPlanNarrList(familyPlanNarrList);

		return familyRes;
	}

	
	/**
	 * Method Name: saveFamilyPlanLegacy Method Description:Saves the family
	 * plan details to the database. Creates a new family plan event or a new
	 * family plan evaluation event, if needed. Otherwise, updates the existing
	 * family plan event or family plan evaluation event. Returns the event id
	 * of the event being added or updated.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return the family plan res
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FamilyPlanRes saveFamilyPlanLegacy(FamilyPlanDto familyPlanDto) {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		Long idEvent = null;
		// saving family plan data, and the family plan event does exist
		if (familyPlanDto.isIndFamilyPlan() && !ObjectUtils.isEmpty(familyPlanDto.getIdEvent())) {
			// If the event is pending approval and the user did not access the
			// family plan via an approval todo, or if the user accessed the
			// family plan after navigating a stage closure approval todo, call
			// InvalidateApproval common function to invalidate the approval and
			// demote all related events. If the user accessed the family plan
			// after navigating a stage closure approval todo, we want to
			// invalidate the pending family plan so the user can close the
			// stage without leaving a pending family plan.
			EventDto eventDto = eventDao.getEventByid(familyPlanDto.getIdEvent());
			//Date latestFamilyPlanDate = familyPlanDao.getFamilyplanDtLastUpdate(familyPlanDto.getIdEvent());
			if(!eventDto.getDtLastUpdate().equals(DateUtils.getDateyyyyMMddHHmmss(familyPlanDto.getDtLastUpdate()))){
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(2046);
				familyPlanRes.setErrorDto(errorDto);
			}else{
				if (ServiceConstants.PEND.equals(eventDto.getCdEventStatus())
						&& (!familyPlanDto.isApprovalMode() || familyPlanDto.isApprovalModeForStageClosure())) {
					ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
					approvalCommonInDto.setIdEvent(eventDto.getIdEvent());
					approvalCommonService.callCcmn05uService(approvalCommonInDto);
				}
				// updates the family plan
				familyPlanDao.updateFamilyPlanEntity(familyPlanDto, false);
				// updates the family plan cost
				familyPlanDao.updateFamilyPlanCost(familyPlanDto);
				// Updates the event person link based the participants checked in
				// the pricipals and participants section.
				updateFamilyParticipants(familyPlanDto.getPrincipalParticipantList(), familyPlanDto.getIdEvent(),
						familyPlanDto.getIdCase());
				// Addition/Update/Deletion of roles checkbox values.
				familyPlanDao.updateFamilyPlanRole(familyPlanDto.getIdEvent(), familyPlanDto.getFgdmRolesList());
				// Call PostEvent to update the family plan event
				updateEvent(familyPlanDto.getFamilyPlanEvent(), familyPlanDto);
			}
			idEvent = familyPlanDto.getFamilyPlanEvent().getIdEvent();
		}
		// saving family plan evaluation data, and the family plan evaluation
		// event does not yet exist
		else if (familyPlanDto.isIndFamilyPlanEval() && ObjectUtils.isEmpty(familyPlanDto.getIdEvalEvent())) {
			// Workers can now create an evaluation in one stage for a family
			// plan
			// that was created in another stage. Since the plan type is
			// determined
			// based upon the stage of service in which the plan or eval is
			// created,
			// we need to determine the new plan type so that it can be saved to
			// the
			// database. Set the plan type of the new family plan. If the stage
			// is
			// FPR or FRE, the plan type is FPP. If the stage is FSU, the plan
			// type
			// is PSC.
			if (CodesConstant.CSTAGES_FPR.equals(familyPlanDto.getCdStage())
					|| CodesConstant.CSTAGES_FRE.equals(familyPlanDto.getCdStage())) {
				familyPlanDto.setCdPlanType(CodesConstant.CPLNTYPE_FPP);
			} else if (CodesConstant.CSTAGES_FSU.equals(familyPlanDto.getCdStage())) {
				familyPlanDto.setCdPlanType(CodesConstant.CPLNTYPE_PSC);
			}
			// Call PostEvent to update the family plan event and the principals
			// for the event.
			idEvent = updateEvent(familyPlanDto.getFamilyPlanEvent(), familyPlanDto);
			// updates family plan evaluation
			familyPlanDao.updateFamilyPlanEval(familyPlanDto, idEvent);
			// Updates the event person link based the participants checked in
			// the pricipals and participants section.
			updateFamilyParticipants(familyPlanDto.getPrincipalParticipantList(), idEvent, familyPlanDto.getIdCase());
			// Addition/Update/Deletion of roles checkbox values.
			familyPlanDao.updateFamilyPlanRole(idEvent, familyPlanDto.getFgdmRolesList());
			// Update FamilyPlanItem table to set Current Level of Concern to
			// Previous Level of Concern
			familyPlanDao.updateFamilyplanitem(familyPlanDto.getIdEvent(), familyPlanDto.getIdCase());
		} else if (familyPlanDto.isIndFamilyPlanEval() && !ObjectUtils.isEmpty(familyPlanDto.getIdEvalEvent())) {
			EventDto eventDto = eventDao.getEventByid(familyPlanDto.getIdEvalEvent());
			//Date latestFamilyPlanDate = familyPlanDao.getFamilyplanEvalDtLastUpdate(familyPlanDto.getIdEvent());
			if(!eventDto.getDtLastUpdate().equals(DateUtils.getDateyyyyMMddHHmmss(familyPlanDto.getDtLastUpdate()))){
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(2046);
				familyPlanRes.setErrorDto(errorDto);
				idEvent = familyPlanDto.getIdEvent();
			}else{
				if (ServiceConstants.PEND.equals(eventDto.getCdEventStatus())
						&& (!familyPlanDto.isApprovalMode() || familyPlanDto.isApprovalModeForStageClosure())) {
					ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
					approvalCommonInDto.setIdEvent(eventDto.getIdEvent());
					approvalCommonService.callCcmn05uService(approvalCommonInDto);
				}
				// Call PostEvent to update the family plan event
				familyPlanDto.getFamilyPlanEvent().setDtEventOccurred(eventDto.getDtEventOccurred());
				idEvent = updateEvent(familyPlanDto.getFamilyPlanEvent(), familyPlanDto);
				// updates family plan evaluation
				familyPlanDao.updateFamilyPlanEval(familyPlanDto, idEvent);
				// Updates the event person link based the participants checked in
				// the pricipals and participants section.
				updateFamilyParticipants(familyPlanDto.getPrincipalParticipantList(), idEvent, familyPlanDto.getIdCase());
				// Addition/Update/Deletion of roles checkbox values.
				familyPlanDao.updateFamilyPlanRole(idEvent, familyPlanDto.getFgdmRolesList());
			}
		}
		// If the stage closure event is pending approval, and the user did not
		// access the stage via an approval todo, invalidate it. Then set the
		// stage closure event status to COMP.
		String cdStage = familyPlanDto.getCdStage();
		Long idStage = familyPlanDto.getIdStage();
		String cdTask = null;
		if (cdStage.equals(CodesConstant.CSTAGES_FSU)) {
			cdTask = CLOSURE_TASK_CODE_FSU;
		} else if (cdStage.equals(CodesConstant.CSTAGES_FRE)) {
			cdTask = CLOSURE_TASK_CODE_FRE;
		} else if (cdStage.equals(CodesConstant.CSTAGES_FPR)) {
			cdTask = CLOSURE_TASK_CODE_FPR;
		}
		List<EventDto> eventDtoList = eventDao.getEventByStageIDAndTaskCode(idStage, cdTask);
		if (ObjectUtils.isEmpty(familyPlanRes.getErrorDto()) && !CollectionUtils.isEmpty(eventDtoList) && !familyPlanDto.isApprovalMode()) {
			EventDto eventDto = eventDtoList.get(0);
			if (CodesConstant.CEVTSTAT_PEND.equals(eventDto.getCdEventStatus())) {
				ApprovalCommonInDto pInputMsg = new ApprovalCommonInDto();
				ApprovalCommonOutDto pOutputMsg = new ApprovalCommonOutDto();
				pInputMsg.setIdEvent(eventDto.getIdEvent());
				// Call Service to invalidate approvals
				approvalCommonService.InvalidateAprvl(pInputMsg, pOutputMsg);
			}
		}
		/*if (ObjectUtils.isEmpty(familyPlanRes.getErrorDto()) && !ObjectUtils.isEmpty(familyPlanDto.getStageClosureEvent())
				&& ServiceConstants.PEND.equals(familyPlanDto.getStageClosureEvent().getCdEventStatus())
				&& !familyPlanDto.isApprovalMode()) {
			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			approvalCommonInDto.setIdEvent(familyPlanDto.getStageClosureEvent().getIdEvent());
			approvalCommonService.callCcmn05uService(approvalCommonInDto);
			eventUtilityDao.updateEventStatus(familyPlanDto.getStageClosureEvent().getIdEvent(),
					CodesConstant.CEVTSTAT_COMP);
		}*/
		// If the worker clicked 'Save and Submit', or if the supervisor is
		// saving changes while in approval mode, create an Alert To-Do for the
		// Next Review.
		if (ObjectUtils.isEmpty(familyPlanRes.getErrorDto()) && (familyPlanDto.isIndSaveAndSubmit() || familyPlanDto.isApprovalMode())
				&& !ObjectUtils.isEmpty(familyPlanDto.getDtNextDue())) {
			// deletes the existing to and creates new todo
			familyPlanDao.updateToDoforNextReview(familyPlanDto);
		}
		// this block will be executed if the code stage type does not start
		// with "C-"
		
		if (ObjectUtils.isEmpty(familyPlanRes.getErrorDto()) && !familyPlanDto.getCdStageType().startsWith(ServiceConstants.SVC_CD_STAGE_TYPE_CRSR)) {
			EventDto eventDto = eventDao.getEventByid(idEvent);
			if (ServiceConstants.EVENTSTATUS_NEW.equals(eventDto.getCdEventStatus())
					|| ServiceConstants.EVENTSTATUS_PROCESS.equals(eventDto.getCdEventStatus())) {
				familyPlanDao.deleteChildCandidacy(idEvent);
				if (!ObjectUtils.isEmpty(familyPlanDto.getDtCompleted())) {
					// Update the Determination and Redetermination dates in the
					// FAMILY_CHILD_CANDIDACY table
					familyPlanDao.updateChildCandidacyDeterminations(idEvent);
					// If there are any rows present in the PERSON_ELIGIBILITY
					// table , then that row is end dated and a new row is
					// inserted.
					familyPlanDao.fetchAndInsertRecsChildCandidacy(idEvent);
				}
			}
		}
		familyPlanRes.setFamilyPlanDto(familyPlanDto);
		familyPlanRes.setResult(idEvent);
		return familyPlanRes;
	}

	/**
	 * Method Name: saveSubmitFamilyPlanLegacy Method Description: This method
	 * saves the information and do further validations to continue save and
	 * submit.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @param familyPlanRes
	 *            the family plan res
	 * @return the family plan res
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FamilyPlanRes saveSubmitFamilyPlanLegacy(FamilyPlanDto familyPlanDto, FamilyPlanRes familyPlanRes) {
		familyPlanDto = fetchFamilyPlanDtl(familyPlanDto);
		// Error message if the user tries to Save and submit without completing
		// the Foster Care Candidacy page.
		if (ServiceConstants.FPR_PROGRAM.equals(familyPlanDto.getCdStage())
				|| ServiceConstants.FRE_PROGRAM.equals(familyPlanDto.getCdStage())
				|| ServiceConstants.FSU_PROGRAM.equals(familyPlanDto.getCdStage())) {
			HashMap personDetail = criminalHistoryDao.checkCrimHistAction(familyPlanDto.getIdStage());
			familyPlanRes.setPersonDetailForValidation(personDetail);
		}
		// Check if any DPS Criminal History check is pending, if yes display an
		// Error message
		if (contactDetailsDao.isCrimHistCheckPending(familyPlanDto.getIdStage())) {
			familyPlanRes.setCrimHistPendingForValidation(true);
		}
		familyPlanRes.setValidPermGoals(checkPermanencyGoals(familyPlanDto));
		familyPlanRes.setValidSvcsReferrals(checkServicesAndReferralsChecklist(familyPlanDto));
		familyPlanRes.setFamilyPlanDto(familyPlanDto);
		return familyPlanRes;
	}

	/**
	 * Check permanency goals.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return true, if successful
	 */
	private boolean checkPermanencyGoals(FamilyPlanDto familyPlanDto) {
		boolean isValid = true;
		if (ServiceConstants.FSU_PROGRAM.equals(familyPlanDto.getCdStage())
				&& !ObjectUtils.isEmpty(familyPlanDto.getChildrenInCaseInSubcareHashmap())
				&& familyPlanDto.getChildrenInCaseInSubcareHashmap().size() > 0) {
			HashMap<Long, Boolean> childrenInCaseInSubcareHashmap = familyPlanDto.getChildrenInCaseInSubcareHashmap();
			for (PersonValueDto o : familyPlanDto.getPrincipalsSelectedForEventList()) {
				if (!ObjectUtils.isEmpty(familyPlanDto.getChildrenInPlanInSubcareList())) {
					for (PersonValueDto childrenInPlanInSubcareList : familyPlanDto.getChildrenInPlanInSubcareList()) {
						if (!ObjectUtils.isEmpty(childrenInPlanInSubcareList.getPersonId())
								&& !ObjectUtils.isEmpty(o.getPersonId())
								&& childrenInPlanInSubcareList.getPersonId().equals(o.getPersonId())) {
							if (!ObjectUtils.isEmpty(childrenInPlanInSubcareList.getPermanencyGoalTargetDate())) {
								o.setPermanencyGoalTargetDate(
										childrenInPlanInSubcareList.getPermanencyGoalTargetDate());
							}
							if (!ObjectUtils.isEmpty(childrenInPlanInSubcareList.getPermanencyGoalCode())) {
								o.setPermanencyGoalCode(childrenInPlanInSubcareList.getPermanencyGoalCode());
							}
						}
					}
				}
				Boolean principalIsChildInSubcare = childrenInCaseInSubcareHashmap.get(o.getPersonId());
				if (!ObjectUtils.isEmpty(principalIsChildInSubcare) && principalIsChildInSubcare
						&& (ObjectUtils.isEmpty(o.getPermanencyGoalCode())
								|| ObjectUtils.isEmpty(o.getPermanencyGoalTargetDate()))) {
					isValid = false;
					break;
				}
			}
		}
		return isValid;
	}

	/**
	 * Check services and referrals checklist.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return true, if successful
	 */
	private boolean checkServicesAndReferralsChecklist(FamilyPlanDto familyPlanDto) {
		boolean isValid = false;
		SrvreferralsRes srvreferralsRes = null;
		if (!ObjectUtils.isEmpty(familyPlanDto.getInterpreterTranslatorIsNeeded())
				&& familyPlanDto.getInterpreterTranslatorIsNeeded()) {
			SrvreferralsReq srvrflReq = new SrvreferralsReq();
			String taskCode = INV_CHECKLIST_TASK_CODE;
			if (ServiceConstants.FPR_PROGRAM.equals(familyPlanDto.getCdStage())) {
				taskCode = FPR_CHECKLIST_TASK_CODE;
			} else if (ServiceConstants.FRE_PROGRAM.equals(familyPlanDto.getCdStage())) {
				taskCode = FRE_CHECKLIST_TASK_CODE;
			} else if (ServiceConstants.FSU_PROGRAM.equals(familyPlanDto.getCdStage())) {
				taskCode = FSU_CHECKLIST_TASK_CODE;
			}
			List<EventDto> eventDtoList = eventDao.getEventByStageIDAndTaskCode(familyPlanDto.getIdStage(), taskCode);
			if(!ObjectUtils.isEmpty(eventDtoList)){
				Long idEvent = eventDtoList.get(0).getIdEvent();
				srvrflReq.setUlIdEvent(idEvent);
				srvrflReq.setUlIdStage(familyPlanDto.getIdStage());
				srvrflReq.setSzCdStage(familyPlanDto.getCdStage());
				srvreferralsRes = srvreferralsService.getSrvrflInfo(srvrflReq);
			}
			if (!ObjectUtils.isEmpty(srvreferralsRes) && !ObjectUtils.isEmpty(srvreferralsRes.getcpsChecklist())
					&& !ObjectUtils.isEmpty(srvreferralsRes.getcpsChecklist().getChklstComments())) {
				isValid = true;
			}
			if (!ObjectUtils.isEmpty(srvreferralsRes) && !ObjectUtils.isEmpty(srvreferralsRes.getCpsChecklistItemsDto())
					&& !ObjectUtils.isEmpty(srvreferralsRes.getCpsChecklistItemsDto().getCpsChecklistItemDto())) {
				isValid = srvreferralsRes.getCpsChecklistItemsDto().getCpsChecklistItemDto().stream()
						.anyMatch(o -> CodesConstant.CSRCKLST_245.equals(o.getCdSvcReferred()));
			}
		} else {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * Method Name: updateFamilyParticipants Method Description: if the values
	 * are new and not in DataBase so add them otherwise delete/uncheck them.
	 *
	 * @param principalParticipantDtoList
	 *            the principal participant dto list
	 * @param idEvent
	 *            the id event
	 * @param idCase
	 *            the id case
	 */
	private void updateFamilyParticipants(List<PrincipalParticipantDto> principalParticipantDtoList, Long idEvent,
			Long idCase) {
		// get the list of person whose indParticipant or indPrincipal has
		// checked.
		List<Long> personIds = principalParticipantDtoList.stream()
				.filter(p -> (ServiceConstants.Y.equals(p.getIndParticipant())
						|| ServiceConstants.Y.equals(p.getIndPrincipal())))
				.map(o -> o.getIdPerson()).collect(Collectors.toList());
		// get the list of event person links stored in DataBase.
		List<EventPersonLinkDto> eventPersonLinkList = eventPersonLinkDao.getEventPersonLinkForIdEvent(idEvent);
		if (!ObjectUtils.isEmpty(eventPersonLinkList) && !eventPersonLinkList.isEmpty() && !personIds.isEmpty()) {
			// get the list of person id's from event person link.
			List<Long> dbPersonIds = eventPersonLinkList.stream().map(e -> e.getIdPerson())
					.collect(Collectors.toList());
			eventPersonLinkList.forEach(e -> {
				// if the persons in UI were unchecked that has to be deleted
				// from the event person link. So compare the participants from
				// db and UI and pass the delete function.
				// If a checkbox(s) was/were deselected from both principal and
				// participants, delete them.
				if (!personIds.contains(e.getIdPerson())) {
					e.setReqFuncCd(ServiceConstants.REQ_IND_AUD_DELETE);
				}
				// if the participants are common in UI and DB variables then
				// pass the function as Update
				else if (personIds.contains(e.getIdPerson())) {
					e.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
					PrincipalParticipantDto principalParticipantDto = principalParticipantDtoList.stream()
							.filter(p -> p.getIdPerson().equals(e.getIdPerson())).findFirst().get();
					e.setIndFamPlanPart(principalParticipantDto.getIndParticipant());
					e.setIndFamPlanPrincipal(principalParticipantDto.getIndPrincipal());
				}
			});

			for (PrincipalParticipantDto p : principalParticipantDtoList) {
				// if the participants coming from UI are not in Db pass the
				// function as ADD.
				if (!dbPersonIds.contains(p.getIdPerson()) && personIds.contains(p.getIdPerson())) {
					EventPersonLinkDto eventPersonLinkDto = new EventPersonLinkDto();
					eventPersonLinkDto.setIdPerson(p.getIdPerson());
					eventPersonLinkDto.setIdEvent(idEvent);
					eventPersonLinkDto.setIdCase(idCase);
					eventPersonLinkDto.setIndFamPlanPrincipal(p.getIndPrincipal());
					eventPersonLinkDto.setIndFamPlanPart(p.getIndParticipant());
					eventPersonLinkDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
					eventPersonLinkList.add(eventPersonLinkDto);
				}

			}

		} else if (ObjectUtils.isEmpty(eventPersonLinkList) && !ObjectUtils.isEmpty(personIds)
				&& personIds.size() > 0) {
			for (PrincipalParticipantDto p : principalParticipantDtoList) {
				if (personIds.contains(p.getIdPerson())) {
					// if the participants coming from UI are not in Db pass the
					// function as ADD.
					EventPersonLinkDto eventPersonLinkDto = new EventPersonLinkDto();
					eventPersonLinkDto.setIdPerson(p.getIdPerson());
					eventPersonLinkDto.setIdEvent(idEvent);
					eventPersonLinkDto.setIdCase(idCase);
					eventPersonLinkDto.setIndFamPlanPrincipal(p.getIndPrincipal());
					eventPersonLinkDto.setIndFamPlanPart(p.getIndParticipant());
					eventPersonLinkDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
					eventPersonLinkList.add(eventPersonLinkDto);
				}

			}
		}
		// Does the operations based on the function variable
		eventPersonLinkList.forEach(e -> eventPersonLinkDao.getEventPersonLinkAUD(e));
	}

	/**
	 * Method Name: updateEvent Method Description: This populates the request
	 * to update the event and adds/ updates the event.
	 *
	 * @param eventValueDto
	 *            the event value dto
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return the long
	 */
	private Long updateEvent(EventValueDto eventValueDto, FamilyPlanDto familyPlanDto) {
		String eventDesc = null;
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		postEventIPDto.setCdEventType(CodesConstant.CEVNTTYP_PLN);
		postEventIPDto.setCdTask(familyPlanDto.getCdTask());
		postEventIPDto.setIdPerson(familyPlanDto.getIdUser());
		postEventIPDto.setIdCase(familyPlanDto.getIdCase());
		postEventIPDto.setIdStage(familyPlanDto.getIdStage());
		postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_PROC);
		postEventIPDto.setDtEventOccurred(eventValueDto.getDtEventOccurred());
		// Family Plan
		if (ServiceConstants.CD_TASK_FPR_FAM_PLAN.equals(familyPlanDto.getCdTask())
				|| ServiceConstants.CD_TASK_FRE_FAM_PLAN.equals(familyPlanDto.getCdTask())
				|| ServiceConstants.CD_TASK_FSU_FAM_PLAN.equals(familyPlanDto.getCdTask())) {
			// creates the event description for family plan
			eventDesc = familyPlanDto.getCdStage() + ServiceConstants.SPACE_SYMBOL + familyPlanDto.getCdStageType()
					+ ServiceConstants.SPACE_SYMBOL + ServiceConstants.FAMILY_PLAN;
			// If the 'Date Plan Completed' field is not empty, the status of
			// the family plan event should be COMP (or possibly PEND).
			if (!ObjectUtils.isEmpty(familyPlanDto.getDtCompleted())) {
				eventDesc = eventDesc + ServiceConstants.SPACE_SYMBOL + ServiceConstants.COMPLETED_LOWERCASE
						+ ServiceConstants.SPACE_SYMBOL + DateUtils.stringDt(familyPlanDto.getDtCompleted());
				postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_COMP);
				if (familyPlanDto.isApprovalMode() && !familyPlanDto.isApprovalModeForStageClosure()) {
					postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_PEND);
				}
			}
		}

		// Family Plan Evaluation
		else if (ServiceConstants.CD_TASK_FPR_FAM_PLAN_EVAL.equals(familyPlanDto.getCdTask())
				|| ServiceConstants.CD_TASK_FRE_FAM_PLAN_EVAL.equals(familyPlanDto.getCdTask())
				|| ServiceConstants.CD_TASK_FSU_FAM_PLAN_EVAL.equals(familyPlanDto.getCdTask())) {
			if (ServiceConstants.CD_TASK_FPR_FAM_PLAN.equals(eventValueDto.getCdEventTask())) {
				eventDesc = CodesConstant.CSTAGES_FPR;
			} else if (ServiceConstants.CD_TASK_FRE_FAM_PLAN.equals(eventValueDto.getCdEventTask())) {
				eventDesc = CodesConstant.CSTAGES_FRE;
			} else if (ServiceConstants.CD_TASK_FSU_FAM_PLAN.equals(eventValueDto.getCdEventTask())) {
				eventDesc = CodesConstant.CSTAGES_FSU;
			}
			// creates the event description for family plan evaluation.
			eventDesc = eventDesc + ServiceConstants.SPACE_SYMBOL + familyPlanDto.getCdStageType()
					+ ServiceConstants.SPACE_SYMBOL + ServiceConstants.FAMILY_PLAN_EVENT + ServiceConstants.SPACE_SYMBOL
					+ eventValueDto.getIdEvent() + ServiceConstants.SPACE_SYMBOL + ServiceConstants.COMPLETED_LOWERCASE
					+ ServiceConstants.SPACE_SYMBOL + DateUtils.stringDt(familyPlanDto.getDtCompleted())
					+ ServiceConstants.SPACE_SYMBOL + ServiceConstants.EVALUATED;
			// If the 'Current Review Completed' field is not empty, the status
			// of the family plan eval event should be COMP (or possibly PEND).
			if (!ObjectUtils.isEmpty(familyPlanDto.getDtNextReview())) {
				eventDesc = eventDesc + ServiceConstants.SPACE_SYMBOL + DateUtils.stringDt(familyPlanDto.getDtNextReview());
				postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_COMP);
				if (familyPlanDto.isApprovalMode() && !familyPlanDto.isApprovalModeForStageClosure()) {
					postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_PEND);
				}
			}
		}
		postEventIPDto.setEventDescr(eventDesc);
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.ADD);
		if (familyPlanDto.isIndFamilyPlan() && !ObjectUtils.isEmpty(eventValueDto.getIdEvent())
				&& eventValueDto.getIdEvent() > 0L) {
			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.UPDATE);
			postEventIPDto.setIdEvent(eventValueDto.getIdEvent());
			postEventIPDto.setTsLastUpdate(new Date());
		} else if (familyPlanDto.isIndFamilyPlanEval()) {
			if (!ObjectUtils.isEmpty(familyPlanDto.getIdEvalEvent())) {
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.UPDATE);
				postEventIPDto.setIdEvent(familyPlanDto.getIdEvalEvent());
				postEventIPDto.setTsLastUpdate(new Date());
			} else {
				postEventIPDto.setDtEventOccurred(new Date());
			}
		}
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
		return postEventOPDto.getIdEvent();
	}

	/**
	 * This method is implements to retrieve the family plan information when
	 * adding family plan evaluation.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public FamilyPlanDto getFamilyPlanEvalAddMode(FamilyPlanDto familyPlanDto) {
		Long selectedFamilyPlanEventId = familyPlanDto.getSelectedFamilyPlanEventId();

		EventValueDto familyPlanEvent = familyPlanDao.queryEvent(selectedFamilyPlanEventId);
		familyPlanDto.setFamilyPlanEvent(familyPlanEvent);

		familyPlanDto = familyPlanDao.queryFamilyPlan(familyPlanDto);

		EventDto newFamilyPlanEvalEvent = new EventDto();

		newFamilyPlanEvalEvent.setIdCase(familyPlanDto.getIdCase());
		newFamilyPlanEvalEvent.setIdStage(familyPlanDto.getIdStage());
		newFamilyPlanEvalEvent.setIdPerson(familyPlanDto.getIdUser());
		newFamilyPlanEvalEvent.setCdTask(familyPlanDto.getCdTask());

		FamilyPlanEvalDto newFamilyPlanEvaluation = new FamilyPlanEvalDto();

		newFamilyPlanEvaluation.setEvalEvent(newFamilyPlanEvalEvent);

		if (ObjectUtils.isEmpty(familyPlanDto.getFamilyPlanEvaluationList())) {
			List<FamilyPlanEvalDto> familyPlanEvaluationList = new ArrayList<FamilyPlanEvalDto>();
			familyPlanEvaluationList.add(newFamilyPlanEvaluation);
		}
		familyPlanDto.setIndReadyForNextEvaluation(ServiceConstants.Y);
		familyPlanDto.setTravelCost(ServiceConstants.ZERO);
		familyPlanDto.setFacilityCost(ServiceConstants.ZERO);
		familyPlanDto.setChildCareCost(ServiceConstants.ZERO);
		familyPlanDto.setFoodCost(ServiceConstants.ZERO);
		familyPlanDto.setFgdmTotalCost(ServiceConstants.ZERO);
		familyPlanDto.setOtherParticipants(ServiceConstants.EMPTY_STRING);
		familyPlanDto.setSelFGDMConference(ServiceConstants.STRING_TEN);
		familyPlanDto.setCelebrationOfSuccess(ServiceConstants.EMPTY_STRING);
		familyPlanDto.setPurposeOfReconference(ServiceConstants.EMPTY_STRING);
		familyPlanDto.setFgdmRolesList(new ArrayList<String>());

		EventDto eventDtl = eventDao.getEventByid(selectedFamilyPlanEventId);
		// Fetch the Principals and Participant Details
		List<PrincipalParticipantDto> principalsParticipantsList = familyPlanDao.queryPrincipalsForStage(eventDtl);
		familyPlanDto.setPrincipalParticipantList(principalsParticipantsList);
		return familyPlanDto;
	}

	/**
	 * This helps to fetch the principals for risk of removal.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
	public List<Long> fetchPrincipalsRiskOfRemoval(Long idEvent) {
		return familyPlanDao.fetchPrincipalsRiskOfRemoval(idEvent);
	}

	/**
	 * This method gets the count of completed FCC.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
	public Long getCountFCCComplete(Long idEvent) {
		return familyPlanDao.getCountFCCComplete(idEvent);
	}

	/**
	 * This method fetches all the children in foster care candidacy.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
	public List<FamilyPlanDto> fetchAllChildCandidacy(Long idEvent) {
		return familyPlanDao.fetchAllChildCandidacy(idEvent);
	}

	/**
	 * This method gets the list of children in foster care candidacy.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public FamilyPlanRes getListChildInFCC(FamilyPlanReq familyPlanReq) {
		final List<FamilyPlanDto> listPersonDtl = new ArrayList<>();
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		FamilyPlanDto familyPlanDto = familyPlanReq.getFamilyPlanDto();
		String eventStatus = getEventStatus(familyPlanDto.getIdEvent());
		if (ServiceConstants.EVENT_STATUS_PROC.equals(eventStatus)) {
			ArrayList<Long> principalsList = familyPlanDao.fetchPrincipalsForPlan(familyPlanDto.getIdEvent());
			for (Long personId : principalsList) {
				FamilyPlanDto fmlyPlanBean = familyPlanDao.fetchChildNotInSubcare(personId, familyPlanDto.getIdCase(),
						familyPlanDto.getIdStage());
				if (!ObjectUtils.isEmpty(fmlyPlanBean.getIdPerson())) {
					// Fetch the child risk of removal based on personId and
					// eventId
					FamilyPlanDto fmlyValueBean = familyPlanDao.fetchChildCandidacyRS(fmlyPlanBean.getIdPerson(),
							familyPlanDto.getIdEvent());
					if (!ObjectUtils.isEmpty(fmlyValueBean)) {
						fmlyValueBean.setNmPersonFull(fmlyPlanBean.getNmPersonFull());
						fmlyValueBean.setIdPerson(fmlyPlanBean.getIdPerson());
						fmlyValueBean.setCdCitizenShipCode(fmlyPlanBean.getCdCitizenShipCode());
						listPersonDtl.add(fmlyValueBean);
					} else {
						listPersonDtl.add(fmlyPlanBean);
					}

				}
			}
			familyPlanRes.setListPersonDtl(listPersonDtl);

		} else if (!(ServiceConstants.EVENT_STATUS_NEW.equals(eventStatus)
				|| ServiceConstants.EMPTY_STRING.equals(eventStatus))) {
			familyPlanRes.setListPersonDtl(familyPlanDao.fetchAllChildCandidacy(familyPlanDto.getIdEvent()));
		}

		return familyPlanRes;
	}

	/**
	 * This fetches the goals and active tasks for item.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public FamilyPlanItemDto queryGoalsAndActiveTasksForItem(FamilyPlanItemDto familyPlanItemDto) {
		familyPlanItemDto = familyPlanItemDtlDao.populateGoalsForItem(familyPlanItemDto);
		familyPlanDao.getActiveTaskGoals(familyPlanItemDto);
		return familyPlanItemDto;
	}

	/**
	 * This method looks for the tasks which are not linked to goals.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public void setAllTasksNotLinkedToGoals(FamilyPlanItemDto familyPlanItemDto) {
		familyPlanDao.setAllTasksNotLinkedToGoals(familyPlanItemDto);
	}

	/**
	 * Method Name: savePermanencyGoals Method Description: save service for
	 * Permanency Goal in Family Plan Detail Page.
	 * 
	 * @param familyPlanDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FamilyPlanRes savePermanencyGoals(FamilyPlanDto familyPlanDto) {
		FamilyPlanRes response = new FamilyPlanRes();
		// check if FamilyPlanEvaluation exist for the given family Plan.
		if(!ObjectUtils.isEmpty(familyPlanDto.getChildrenInPlanInSubcareList())){
			List<PersonValueDto> childrenInPlanInSubcareList = familyPlanDto.getChildrenInPlanInSubcareList();
			List<EventPersonLinkDto> eventPersonLinkList = eventPersonLinkDao.getEventPersonLinkForIdEvent(familyPlanDto.getIdEvent());
			childrenInPlanInSubcareList.forEach(o ->{
				EventPersonLinkDto eventPersonLink = eventPersonLinkList.stream().filter(x -> x.getIdPerson().equals(o.getPersonId())).findFirst().get();
				if (ObjectUtils.isEmpty(response.getErrorDto()) && !ObjectUtils.isEmpty(eventPersonLink) && !ObjectUtils.isEmpty(eventPersonLink.getTsLastUpdate())
						&& !ObjectUtils.isEmpty(o.getEventPersonLinkDateLastUpdate())
						&& DateUtils
						.getDateyyyyMMddHHmmss(o.getEventPersonLinkDateLastUpdate()).compareTo(eventPersonLink.getTsLastUpdate()) != 0) {
					ErrorDto error = new ErrorDto();
					error.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
					response.setErrorDto(error);
				}
			});
		
		}
	  if (ObjectUtils.isEmpty(response.getErrorDto())) {			 
		EventDto mostRecentEvent = new EventDto();
		List<FamilyPlanEvalDto> familyPlanEvalList = familyPlanDao.queryFamilyPlanEvaluations(familyPlanDto)
				.getFamilyPlanEvaluationList();
		if (!ObjectUtils.isEmpty(familyPlanEvalList)) {
			// If FamilyPlan Evaluation exists, take the most recent event.
			mostRecentEvent = familyPlanEvalList.get(0).getEvalEvent();
		} else {
			// Else fetch the family Plan Event
			EventValueDto familyPlanEvent = familyPlanDao.queryEvent(familyPlanDto.getIdEvent());
			BeanUtils.copyProperties(familyPlanEvent, mostRecentEvent);
			mostRecentEvent.setCdTask(familyPlanEvent.getCdEventTask());
		}
		// Call checkstageeventStatus before saving Permanency Goal. It checks
		// if stage is open and modifiable
		StageTaskInDto stageTaskInDto = new StageTaskInDto();
		stageTaskInDto.setIdStage(mostRecentEvent.getIdStage());
		stageTaskInDto.setCdTask(mostRecentEvent.getCdTask());
		stageTaskInDto.setCdReqFunction(TypeConvUtil.isNullOrEmpty(mostRecentEvent.getIdEvent()) ? ServiceConstants.ADD
				: ServiceConstants.UPDATE);
		String retVal = stageEventStatusService.checkStageEventStatus(stageTaskInDto);
		if (ServiceConstants.ARC_SUCCESS.equalsIgnoreCase(retVal)) {
			final Long idEvent = mostRecentEvent.getIdEvent();
			// Fetch the saved Prinicpal for the event.
			List<PersonValueDto> childrenInPlanFromDB = familyPlanDao.queryPrincipalsForEvent(mostRecentEvent);
			List<PersonValueDto> childrenInPlanFromReq = familyPlanDto.getChildrenInPlanInSubcareList();
			if (!ObjectUtils.isEmpty(childrenInPlanFromDB) && !ObjectUtils.isEmpty(childrenInPlanFromReq)) {
				// final HashMap<Long, PersonValueDto> childrenInPlanFromDbMap =
				// childrenInPlanFromDB.stream().collect(Collectors.toMap(PersonValueDto::getPersonId,
				// person -> person));
				childrenInPlanFromReq.stream().forEach(person -> {
					PersonValueDto personInDb = childrenInPlanFromDB.stream()
							.filter(p -> person.getPersonId().equals(p.getPersonId())).findAny()
							.orElse(new PersonValueDto());
					boolean hasPermGoalChanged = (!StringUtils.isEmpty(person.getPermanencyGoalCode())
							&& StringUtils.isEmpty(personInDb.getPermanencyGoalCode()))
							|| (StringUtils.isEmpty(person.getPermanencyGoalCode())
									&& !StringUtils.isEmpty(personInDb.getPermanencyGoalCode()))
							|| (!StringUtils.isEmpty(person.getPermanencyGoalCode())
									&& !StringUtils.isEmpty(personInDb.getPermanencyGoalCode())
									&& !person.getPermanencyGoalCode().equals(personInDb.getPermanencyGoalCode()));
					boolean hasTargetDateChngd = (!TypeConvUtil.isNullOrEmpty(person.getPermanencyGoalTargetDate())
							&& TypeConvUtil.isNullOrEmpty(personInDb.getPermanencyGoalTargetDate()))
							|| (TypeConvUtil.isNullOrEmpty(person.getPermanencyGoalTargetDate())
									&& !TypeConvUtil.isNullOrEmpty(personInDb.getPermanencyGoalTargetDate()))
							|| (!TypeConvUtil.isNullOrEmpty(person.getPermanencyGoalTargetDate())
									&& !TypeConvUtil.isNullOrEmpty(personInDb.getPermanencyGoalTargetDate())
									&& !person.getPermanencyGoalTargetDate()
											.equals(personInDb.getPermanencyGoalTargetDate()));
					if (hasPermGoalChanged || hasTargetDateChngd) {
						// if any of Permanency Goal or Target Date is changed
						// update the value in Event Person Link table.
						familyPlanDao.updateChildPermanencyInfo(idEvent, person);
					}
				});
			}
			// Save the Permanency Goal Comments.
			familyPlanDao.updatePermanencyGoalComment(familyPlanDto);
		} else {
			ErrorDto error = new ErrorDto();
			error.setErrorMsg(retVal);
			response.setErrorDto(error);
		}
	  }
		return response;
	}

	/**
	 * Method Name: saveFamilyPlanTask Method Description: Saves the Task
	 * related information into the Task Table.
	 *
	 * @param familyPlanGoalValueDtos
	 *            - list of tasks
	 * @param idEvent
	 *            -event id
	 * @param idCase
	 *            - case id
	 * @param currentEventId
	 * @param familyPlanDto
	 *            - family plan task detail
	 * @param handwritingFieldsToBeDeleted
	 *            - handwriting Fields ToBe Deleted
	 * @return FamilyPlanRes - family plan task response
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
	public FamilyPlanRes saveFamilyPlanTask(List<TaskGoalValueDto> taskGoalValueDtoList, Long idEvent, Long idCase,
			Long currentEventId, FamilyPlanDto familyPlanDto) {

		log.debug("Entering method saveFamilyPlanTask in FamilyPlanServiceImpl");
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		List<TaskGoalValueDto> insertGoalValueDtos = new ArrayList<>();
		List<TaskGoalValueDto> updateValueDtos = new ArrayList<>();
		List<TaskGoalValueDto> activeUpdateTaskGoalDtos = new ArrayList<>();
		List<TaskGoalValueDto> existingTaskGoalValueDtoList = new ArrayList<TaskGoalValueDto>();

		try {
			existingTaskGoalValueDtoList = familyPlanDao.queryFamilyPlanTask(idEvent, idCase);
			existingTaskGoalValueDtoList
					.addAll(familyPlanDao.queryApprovedActiveTasks(idEvent, idCase, currentEventId));
			existingTaskGoalValueDtoList
					.addAll(familyPlanDao.queryApprovedInActiveTasks(idEvent, idCase, currentEventId));
			if (!CollectionUtils.isEmpty(existingTaskGoalValueDtoList)) {
				for (TaskGoalValueDto oldtaskGoalValueDto : existingTaskGoalValueDtoList) {
					for (TaskGoalValueDto taskGoalValueDto : taskGoalValueDtoList) {
						if (!ObjectUtils.isEmpty(oldtaskGoalValueDto.getDateLastUpdate())
								&& !ObjectUtils.isEmpty(taskGoalValueDto.getDateLastUpdate())) {
							if (oldtaskGoalValueDto.getFamilyPlanTaskId()
									.equals(taskGoalValueDto.getFamilyPlanTaskId())) {
								Timestamp newLastUpdatedTime = DateUtils
										.getDateyyyyMMddHHmmss(taskGoalValueDto.getDateLastUpdate());
								if (oldtaskGoalValueDto.getDateLastUpdate().compareTo(newLastUpdatedTime) !=0) {
									ErrorDto errorDto = new ErrorDto();
									errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
									familyPlanRes.setErrorDto(errorDto);
									return familyPlanRes;
								}
							}
						}
					}
				}
			}
			for (TaskGoalValueDto taskGoalValueDto : taskGoalValueDtoList) {
				if ((!TypeConvUtil.isNullOrEmpty(taskGoalValueDto.getOperation()))
						&& (taskGoalValueDto.getOperation().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_ADD))) {
					insertGoalValueDtos.add(taskGoalValueDto);
				} else if ((!TypeConvUtil.isNullOrEmpty(taskGoalValueDto.getOperation())
						&& (taskGoalValueDto.getOperation().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_UPDATE)))) {
					if (!TypeConvUtil.isNullOrEmpty(taskGoalValueDto.getDateApproval())) {
						activeUpdateTaskGoalDtos.add(taskGoalValueDto);
					} else {
						updateValueDtos.add(taskGoalValueDto);
					}
				}
			}

			if (!insertGoalValueDtos.isEmpty()) {
				familyPlanDao.saveFamilyPlanTask(insertGoalValueDtos, currentEventId);
			}
			if (!updateValueDtos.isEmpty()) {
				familyPlanDao.updateFamilyPlanTask(updateValueDtos, currentEventId);
				familyPlanDao.updateTaskGoalLink(updateValueDtos);
			}
			if (!activeUpdateTaskGoalDtos.isEmpty()) {
				familyPlanDao.updateFamilPlanActiveTasks(activeUpdateTaskGoalDtos, currentEventId);
			}

			EventValueDto mostRecentEvent = new EventValueDto();
			if (!TypeConvUtil.isNullOrEmpty(familyPlanDto.getFamilyPlanEvaluationList())) {
				FamilyPlanEvalDto mostRecentFamilyPlanEval = familyPlanDto.getFamilyPlanEvaluationList().get(0);
				EventDto eventDto = mostRecentFamilyPlanEval.getEvalEvent();
				BeanUtils.copyProperties(eventDto, mostRecentEvent);
				if (!TypeConvUtil.isNullOrEmpty(mostRecentFamilyPlanEval.getDtCompleted())) {
					familyPlanDto.setDtCompletedEval(mostRecentFamilyPlanEval.getDtCompleted());
				}
			} else {
				mostRecentEvent = familyPlanDto.getFamilyPlanEvent();
			}

			if (ServiceConstants.CEVTSTAT_PEND.equals(mostRecentEvent.getCdEventStatus())
					&& (!familyPlanDto.isApprovalMode() || familyPlanDto.isApprovalModeForStageClosure())) {
				EventDto eventDto = eventDao.getEventByid(familyPlanDto.getIdEvent());
				ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
				approvalCommonInDto.setIdEvent(eventDto.getIdEvent());
				approvalCommonService.callCcmn05uService(approvalCommonInDto);
			}

		} catch (DataNotFoundException e) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorMsg(e.getMessage());
			familyPlanRes.setErrorDto(errorDto);
			log.error(e.getMessage());
		}

		log.debug("Exiting method saveFamilyPlanTask in FamilyPlanServiceImpl");

		return familyPlanRes;
	}

	/**
	 * 
	 * Method Name: populateCCMN06UICheckStageEventStatus Method Description:
	 * populate the CheckStageEventStatus request dto
	 * 
	 * @param eventBean
	 *            - event dto
	 * @param actionCode
	 *            - function code string
	 * @return CheckStageInpDto - input dto
	 */
	private CheckStageInpDto populateCCMN06UICheckStageEventStatus(EventValueDto eventBean, String actionCode) {
		CheckStageInpDto checkStageEventStatusDto = new CheckStageInpDto();
		ServiceInputDto input = new ServiceInputDto();
		checkStageEventStatusDto.setIdStage(eventBean.getIdStage());
		checkStageEventStatusDto.setCdTask(eventBean.getCdEventTask());
		input.setSzUserId(String.valueOf(eventBean.getIdPerson()));
		input.setCreqFuncCd(actionCode);
		checkStageEventStatusDto.setServiceInput(input);
		return checkStageEventStatusDto;
	}

	/**
	 * 
	 * Method Name: formatDate Method Description:format the given
	 * date(MM/dd/yyyy)
	 * 
	 * @param cdate
	 *            - input date
	 * @return String- date string
	 */
	private String formatDate(Date cdate) {
		if (cdate == null) {
			return ServiceConstants.EMPTY_STRING;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		String strDate = dateFormat.format(cdate);
		return strDate;
	}

	/**
	 * Method Name: deleteFamilyPlanTask Method Description: Deletes the
	 * selected Family Plan Task.
	 * 
	 * @param familyPlanValueBeanDto,familyPlanItemValueBeanDto,
	 *            indexOfTaskToDelete
	 * @return FamilyPlanRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
	public FamilyPlanRes deleteFamilyPlanTask(FamilyPlanDto familyPlanDto, FamilyPlanItemDto familyPlanItemDto,
			Integer indexOfTaskToDelete) {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		try {
			List<FamilyPlanTaskDto> tasksList = (List<FamilyPlanTaskDto>) familyPlanItemDto.getFamilyPlanTaskDtoList();
			FamilyPlanTaskDto taskToDelete = (FamilyPlanTaskDto) tasksList.get(indexOfTaskToDelete);
			if (taskToDelete.getIdFamilyPlanTask() > 0) {
				EventValueDto mostRecentEvent = new EventValueDto();
				if (!ServiceConstants.CEVTSTAT_APRV.equals(familyPlanDto.getFamilyPlanEvent().getEventStatusCode())) {
					mostRecentEvent = familyPlanDto.getFamilyPlanEvent();
				} else {
					List<FamilyPlanEvalDto> familyPlanEvalsList = (List<FamilyPlanEvalDto>) familyPlanDto
							.getFamilyPlanEvaluationList();
					FamilyPlanEvalDto mostRecentFamilyPlanEval = (FamilyPlanEvalDto) familyPlanEvalsList.get(0);
					EventDto eventDto = mostRecentFamilyPlanEval.getEvalEvent();
					BeanUtils.copyProperties(eventDto, mostRecentEvent);
				}
				if (ServiceConstants.CEVTSTAT_PEND.equals(mostRecentEvent.getEventStatusCode())
						&& !familyPlanDto.isApprovalMode()) {
					EventDto eventDto = eventDao.getEventByid(familyPlanDto.getIdEvent());
					ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
					approvalCommonInDto.setIdEvent(eventDto.getIdEvent());
					approvalCommonService.callCcmn05uService(approvalCommonInDto);
				}
				familyPlanDao.deleteFamilyPlanTask(taskToDelete);
			}
			tasksList.remove(indexOfTaskToDelete);
			if (tasksList.size() == 0) {
				tasksList = null;
			}
			familyPlanItemDto.setFamilyPlanTaskDtoList(tasksList);
			familyPlanRes.setFamilyPlanItemDto(familyPlanItemDto);
		} catch (DataNotFoundException e) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorMsg(e.getMessage());
			familyPlanRes.setErrorDto(errorDto);
			log.error(e.getMessage());
		}
		return familyPlanRes;
	}

	/**
	 * Method Name: deleteNewFamilyPlanTask Method Description: Delete a row
	 * from the family_plan_task,FAM_PLN_TASK_GOAL_LINK table
	 *
	 * @param idFamilyPlanTask
	 *            - id key for the family_plan_task
	 * @param familyPlanDto
	 *            - family plan task detail
	 * @return FamilyPlanRes - family plan task response
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
	public FamilyPlanRes deleteNewFamilyPlanTask(Long idFamilyPlanTask, FamilyPlanDto familyPlanDto) {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		try {
			familyPlanDao.deleteTaskGoalLink(idFamilyPlanTask);
			familyPlanDao.deleteNewFamilyPlanTask(idFamilyPlanTask);

			EventValueDto mostRecentEvent = new EventValueDto();
			if (familyPlanDto.getFamilyPlanEvaluationList() != null) {
				FamilyPlanEvalDto mostRecentFamilyPlanEval = familyPlanDto.getFamilyPlanEvaluationList().get(0);
				EventDto eventDto = mostRecentFamilyPlanEval.getEvalEvent();
				BeanUtils.copyProperties(eventDto, mostRecentEvent);
				if (mostRecentFamilyPlanEval.getDtCompleted() != null) {
					familyPlanDto.setDtCompletedEval(mostRecentFamilyPlanEval.getDtCompleted());
				}
			} else {
				mostRecentEvent = familyPlanDto.getFamilyPlanEvent();
			}

			if (ServiceConstants.CEVTSTAT_PEND.equals(mostRecentEvent.getEventStatusCode())
					&& (!familyPlanDto.isApprovalMode() || familyPlanDto.isApprovalModeForStageClosure())) {
				EventDto eventDto = eventDao.getEventByid(familyPlanDto.getIdEvent());
				ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
				approvalCommonInDto.setIdEvent(eventDto.getIdEvent());
				approvalCommonService.callCcmn05uService(approvalCommonInDto);
			}
		} catch (DataNotFoundException e) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorMsg(e.getMessage());
			familyPlanRes.setErrorDto(errorDto);
		}
		return familyPlanRes;

	}

	/**
	 * Method Name: saveOrUpdateFamPlanGoal Method Description: to save or
	 * update family goal
	 * 
	 * @param familyPlanDto
	 * @param idCase
	 * @param idEvent
	 * @return String
	 */
	@Override
	public FamilyPlanRes saveOrUpdateFamPlanGoal(FamilyPlanDto familyPlanDto, Long idEvent, Long idCase) {
		List<FamilyPlanGoalValueDto> familyPlanGoalValueDtos = familyPlanDto.getFamilyPlanGoalValueDtos();
		log.debug("Entering method saveOrUpdateFamPlanGoal in FamilyPlanServiceImpl");
		FamilyPlanRes familyPlanRes = familyPlanGoalDao.saveOrUpdateFamilyPlanGoal(familyPlanGoalValueDtos, idEvent,
				idCase);
		familyPlanDto.setIdCase(idCase);
		List<FamilyPlanEvalDto> familyPlanEvalList = familyPlanDao.queryFamilyPlanEvaluations(familyPlanDto)
				.getFamilyPlanEvaluationList();
		EventValueDto mostRecentEvent = new EventValueDto();
		if (!TypeConvUtil.isNullOrEmpty(familyPlanEvalList)) {
			FamilyPlanEvalDto mostRecentFamilyPlanEval = familyPlanDto.getFamilyPlanEvaluationList().get(0);
			EventDto eventDto = mostRecentFamilyPlanEval.getEvalEvent();
			BeanUtils.copyProperties(eventDto, mostRecentEvent);
			if (!TypeConvUtil.isNullOrEmpty(mostRecentFamilyPlanEval.getDtCompleted())) {
				familyPlanDto.setDtCompletedEval(mostRecentFamilyPlanEval.getDtCompleted());
			}
		} else {
			mostRecentEvent = familyPlanDto.getFamilyPlanEvent();
		}
		if (ServiceConstants.CEVTSTAT_PEND.equals(mostRecentEvent.getCdEventStatus())
				&& (!familyPlanDto.isApprovalMode() || familyPlanDto.isApprovalModeForStageClosure())) {
			EventDto eventDto = eventDao.getEventByid(familyPlanDto.getIdEvent());
			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			approvalCommonInDto.setIdEvent(eventDto.getIdEvent());
			approvalCommonService.callCcmn05uService(approvalCommonInDto);
		}
		log.debug("Exiting method saveOrUpdateFamPlanGoal in FamilyPlanServiceImpl");
		return familyPlanRes;
	}

	/**
	 * Method Name: deleteFamPlanGoal Method Description: to delete family goal
	 * 
	 * @param idGoal
	 * @return String
	 */
	@Override
	public String deleteFamPlanGoal(Long idGoal, FamilyPlanDto familyPlanDto) {
		log.debug("Entering method deleteFamPlanGoal in FamilyPlanServiceImpl");
		String returnMsg = familyPlanGoalDao.deleteFamPlanGoal(idGoal);
		List<FamilyPlanEvalDto> familyPlanEvalList = familyPlanDao.queryFamilyPlanEvaluations(familyPlanDto)
				.getFamilyPlanEvaluationList();
		EventValueDto mostRecentEvent = new EventValueDto();
		if (!TypeConvUtil.isNullOrEmpty(familyPlanEvalList)) {
			FamilyPlanEvalDto mostRecentFamilyPlanEval = familyPlanDto.getFamilyPlanEvaluationList().get(0);
			EventDto eventDto = mostRecentFamilyPlanEval.getEvalEvent();
			BeanUtils.copyProperties(eventDto, mostRecentEvent);
			if (!TypeConvUtil.isNullOrEmpty(mostRecentFamilyPlanEval.getDtCompleted())) {
				familyPlanDto.setDtCompletedEval(mostRecentFamilyPlanEval.getDtCompleted());
			}
		} else {
			mostRecentEvent = familyPlanDto.getFamilyPlanEvent();
		}
		if (ServiceConstants.CEVTSTAT_PEND.equals(mostRecentEvent.getCdEventStatus())
				&& (!familyPlanDto.isApprovalMode() || familyPlanDto.isApprovalModeForStageClosure())) {
			EventDto eventDto = eventDao.getEventByid(familyPlanDto.getIdEvent());
			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			approvalCommonInDto.setIdEvent(eventDto.getIdEvent());
			approvalCommonService.callCcmn05uService(approvalCommonInDto);
		}

		log.debug("Exiting method deleteFamPlanGoal in FamilyPlanServiceImpl");
		return returnMsg;
	}

}
