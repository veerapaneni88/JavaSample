package us.tx.state.dfps.service.fce.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.FceReview;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionInputDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionOutputDto;
import us.tx.state.dfps.service.admin.dto.ResourceServiceInDto;
import us.tx.state.dfps.service.adoptionasstnc.dao.AdoptionAsstncDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.EligibilitySummarySaveReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.EligibilitySummaryRes;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.fce.EligibilityDto;
import us.tx.state.dfps.service.fce.EligibilitySummaryDto;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.EligibilityEventDto;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceContextDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceReasonNotEligibleDto;
import us.tx.state.dfps.service.fce.service.EligibilitySummaryService;
import us.tx.state.dfps.service.fce.service.FceService;
import us.tx.state.dfps.service.fostercarereview.dao.FcePersonDao;
import us.tx.state.dfps.service.fostercarereview.dao.FceReasonNotEligibleDao;
import us.tx.state.dfps.service.fostercarereview.dao.FceReviewDao;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.MedicaidUpdateDao;
import us.tx.state.dfps.service.person.dto.MedicaidUpdateDto;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.service.TodoCommonFunctionService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * EligibilitySummaryServiceImpl Mar 12, 2018- 11:29:26 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class EligibilitySummaryServiceImpl implements EligibilitySummaryService {

	@Autowired
	private StageDao stagedao;

	@Autowired
	private EventDao eventDao;

	@Autowired
	private FcePersonDao fcePersonDao;

	@Autowired
	private LookupDao lookudao;

	@Autowired
	private FceReasonNotEligibleDao fceReasonNotEligibleDao;

	@Autowired
	private PlacementDao placementDao;

	@Autowired
	private FceReviewDao fceReviewDao;

	@Autowired
	private FceDao fceDao;

	@Autowired
	private FceService fceService;

	@Autowired
	private CheckStageEventStatusService checkStageEventStatusService;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EventService eventService;

	@Autowired
	private AdoptionAsstncDao adoptionAsstncDao;

	@Autowired
	private WorkLoadDao workLoadDao;

	@Autowired
	private TodoCommonFunctionService todoCommonFunctionService;

	@Autowired
	private MedicaidUpdateDao medicaidUpdateDao;

	@Autowired
	private TodoDao todoDao;

	// Constants
	public static final String MSG_BLOC_EQUAL1_AND_SSI = "25490";
	public static final String MSG_SYS_STAGE_CLOSED = "8164";
	public static final Integer MSG_SUB_GAP_EXISTS_1 = 8109;
	public static final Integer MSG_SUB_GAP_EXISTS_2 = 8140;
	public static final Integer MSG_SUB_GAP_EXISTS_3 = 8322;
	public static final String FCE_ELIGIBILITY_TASK_CODE = "3120";
	public static final String FCE_ELIGIBILITY_EVENT_TYPE = "FCD";
	public static final int MSG_OPEN_SUMMARY_EVENT = 25175;
	public static final String TODO_CODE_SUB022 = "SUB022";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.fce.service.EligibilitySummaryService#read(java.
	 * lang.Long, java.lang.Long, java.lang.Long)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public EligibilitySummaryRes read(Long idStage, Long idEvent, Long idLastUpdatePerson) {
		EligibilitySummaryRes eligibilitySummaryRes = new EligibilitySummaryRes();
		eligibilitySummaryRes.setEligibilitySummaryDto(executeRead(idStage, idEvent, idLastUpdatePerson));
		return eligibilitySummaryRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.fce.service.EligibilitySummaryService#save(us.tx
	 * .state.dfps.service.fce.EligibilitySummaryDto, java.util.Set)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void save(EligibilitySummaryDto eligibilitySummary) {
		executeSave(eligibilitySummary);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.fce.service.EligibilitySummaryService#delete(us.
	 * tx.state.dfps.service.fce.EligibilitySummaryDto)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void delete(EligibilitySummaryDto eligibilitySummaryDto) {

		Long idFceEligibility = eligibilitySummaryDto.getFceEligibilityDto().getIdFceEligibility();
		Long idEligibilityEvent = eligibilitySummaryDto.getFceEligibilityDto().getIdEligibilityEvent();

		fceDao.deleteFceDepCareDeductDependentPerson(idFceEligibility);
		fceDao.deleteFceDepCareDeductAdultPerson(idFceEligibility);
		fceDao.deleteFcePerson(idFceEligibility);
		fceDao.deleteFceApplicationForEligibility(idEligibilityEvent);
		fceDao.deleteFceReasonNotEligible(idFceEligibility);
		fceDao.deleteFceEligibility(idFceEligibility);
		eventDao.deleteEventById(idEligibilityEvent);

	}

	/**
	 * Method Name: executeRead Method Description: This method is used to
	 * executeRead
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idLastUpdatePerson
	 * @ @return EligibilitySummaryDto
	 */
	private EligibilitySummaryDto executeRead(Long idStage, Long idEvent, Long idLastUpdatePerson) {
		EligibilitySummaryDto eligibilitySummaryDto = new EligibilitySummaryDto();
		ResourceServiceInDto resourceServiceInDto = new ResourceServiceInDto();
		Long idPerson = null;
		FceContextDto fceContext = fceService.initializeFceEligibility(idStage, idEvent, idLastUpdatePerson);

		FceEligibilityDto fceEligibilityDto = fceContext.getFceEligibilityDto();
		EligibilityDto eligibilityDto = fceContext.getEligibilityDto();
		FceApplicationDto fceApplicationDto = fceContext.getFceApplicationDto();

		Date dtRsnblEffortPreventRem = null;
		
		if (!ObjectUtils.isEmpty(fceEligibilityDto)) {
			eligibilitySummaryDto.setFceEligibilityDto(fceEligibilityDto);
			idPerson = fceEligibilityDto.getIdPerson();
			
			if(!ObjectUtils.isEmpty(fceEligibilityDto.getIdFceApplication())) {
				dtRsnblEffortPreventRem = fceDao.getFceApplication(fceEligibilityDto.getIdFceApplication()).getFceEligibility().getDtRsnblEffortPreventRem();
			}
		}

		if (!ObjectUtils.isEmpty(eligibilityDto)) {
			eligibilitySummaryDto.setEligibilityDto(eligibilityDto);
			if (TypeConvUtil.isNullOrEmpty(idPerson)) {
				idPerson = eligibilityDto.getIdPerson();
			}
		}

		if (!ObjectUtils.isEmpty(fceContext.getCdEventStatus()))
			eligibilitySummaryDto.setCdEventStatus(fceContext.getCdEventStatus());

		if (!ObjectUtils.isEmpty(fceContext.getIdFcePerson())) {
			eligibilitySummaryDto
					.setDtChildBirth(fcePersonDao.getFcepersonById(fceContext.getIdFcePerson()).getDtBirth());
		}

		Long idFceEligibility = fceEligibilityDto.getIdFceEligibility();

		List<FceReasonNotEligibleDto> reasonsNotEligible = fceReasonNotEligibleDao
				.findReasonsNotEligible(idFceEligibility);
		//Fix for defect 13695 - Invld error msg ending sys gen'd elig
		eligibilitySummaryDto.setReasonsNotEligible(reasonsNotEligible);

		fceApplicationDto = fceDao.getFceApplicationById(fceContext.getIdFceApplication());

		if (!ObjectUtils.isEmpty(fceApplicationDto))
			eligibilitySummaryDto.setCdApplication(fceApplicationDto.getCdApplication());

		PlacementDto placementDto = placementDao.findActivePlacementsForEligibilty(fceContext.getIdPerson());

		eligibilitySummaryDto.setIndNoActivePlacement(ServiceConstants.Y);

		if (!ObjectUtils.isEmpty(placementDto)) {
			eligibilitySummaryDto.setIndNoActivePlacement(ServiceConstants.N);

			String cdPlcmtType = placementDto.getCdPlcmtType();
			String cdPlcmtLivArr = placementDto.getCdPlcmtLivArr();

			eligibilitySummaryDto.setIndNonPrsPaidPlacement(isPlacementPrsPaid(cdPlcmtType, cdPlcmtLivArr));
		}

		// Fetch the FCE review bean for Date voluntary foster care agreement
		// was signed.
		Long idFceReview = fceEligibilityDto.getIdFceReview();

		if (!ObjectUtils.isEmpty(idFceReview) && idFceReview != 0) {
			FceReview fceReviewLocal = fceReviewDao.getById(idFceReview);
			if (!ObjectUtils.isEmpty(fceReviewLocal)) {
				if (ObjectUtils.isEmpty(eligibilitySummaryDto.getEligibilityDto()))
					eligibilitySummaryDto.setEligibilityDto(new EligibilityDto());
				eligibilitySummaryDto.getEligibilityDto().setDtVolFcAgrmntSign(fceReviewLocal.getDtVolFcAgrmntSign());
			}
		}

		// fetch first placement start date by child person id
		if (!TypeConvUtil.isNullOrEmpty(idPerson)) {
			resourceServiceInDto.setIdPlcmtChild(idPerson);
			List<PlacementDto> placementsForChild = placementDao.getPlacementsByChildId(resourceServiceInDto);
			if (!CollectionUtils.isEmpty(placementsForChild)){
				Date firstPlcmntStartDate = placementsForChild.stream()
						.filter(p -> p != null && p.getDtPlcmtStart() != null).map(PlacementDto::getDtPlcmtStart)
						.min(Date::compareTo).get();
				eligibilitySummaryDto.setDtTitleIVEstart(firstPlcmntStartDate);
				
				if(!ObjectUtils.isEmpty(dtRsnblEffortPreventRem)) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dtRsnblEffortPreventRem);
					int monthRsnblEffortPreventRem = cal.get(Calendar.MONTH);
					
					cal.setTime(firstPlcmntStartDate);
					int monthFirstPlcmntStart = cal.get(Calendar.MONTH);
					
					if(dtRsnblEffortPreventRem.after(firstPlcmntStartDate) && monthRsnblEffortPreventRem != monthFirstPlcmntStart) {
						Date firstDayOfRsnblEffortPreventRem = DateUtils.getFirstDayOfTheMonth(dtRsnblEffortPreventRem);
						eligibilitySummaryDto.setDtTitleIVEstart(firstDayOfRsnblEffortPreventRem);
					}
				}
			}
		}
		
		
		return eligibilitySummaryDto;
	}

	/**
	 * Method Name: executeSave Method Description: This method is used to
	 * executeSave
	 * 
	 * @param eligibilitySummaryDto
	 * @param ignoreMessages
	 * @ @return void
	 */
	private void executeSave(EligibilitySummaryDto eligibilitySummaryDto) {

		EligibilityDto eligibilityDto = eligibilitySummaryDto.getEligibilityDto();
		FceEligibilityDto fceEligibilityDto = eligibilitySummaryDto.getFceEligibilityDto();

		// Have to know the age to override the Review Date as necessary
		Date startDate = eligibilityDto.getDtEligStart();

		// Summary Start Date
		Integer age = DateUtils.getAge(eligibilitySummaryDto.getDtChildBirth(), startDate);
		Date startDatePlus1Year = DateUtils.addToDate(startDate, 1, 0, 0);
		Date reviewDate = startDatePlus1Year;

		if (ObjectUtils.isEmpty(eligibilityDto.getDtEligReview())) {
			eligibilityDto.setDtEligReview(startDatePlus1Year);
		}

		if (!ObjectUtils.isEmpty(fceEligibilityDto.getIdStage()))
			fceDao.verifyOpenStage(fceEligibilityDto.getIdStage());

		CommonHelperRes commonHelperRes = fceDao.saveEligibility(fceEligibilityDto, ServiceConstants.FALSEVAL);
		if (!ObjectUtils.isEmpty(commonHelperRes.getErrorDto())) {
			throw new ServiceLayerException("", (long) ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH, null);
		}

		long idEligibilityEvent = fceEligibilityDto.getIdEligibilityEvent();
		EventDto eventDto = eventDao.getEventByid(idEligibilityEvent);

		fceService.syncFceEligibilityStatus(fceEligibilityDto);

		String selectedEligibility = eligibilityDto.getCdEligSelected();
		Double amtSsiDouble = fceEligibilityDto.getAmtSsi();

		// This check should only be done when saving a new summary.
		String eventStatus = eventDto.getCdEventStatus();

		if (age >= 17 && CodesConstant.CEVTSTAT_NEW.equals(eventStatus)) {
			Date dtChildAge = DateUtils.addToDate(eligibilitySummaryDto.getDtChildBirth(), age, 0, 0);

			Date nextMth1stDayAfterBday = DateUtils.addToDate(DateUtils.getLastDayOfTheMonth(dtChildAge), 0, 0, 1);
			reviewDate = nextMth1stDayAfterBday;

			// Check if the review Date is before the Start date, and if so add
			// 1 year.
			if (reviewDate.before(startDate)) {
				reviewDate = DateUtils.addToDate(reviewDate, 1, 0, 0);
			}

			int reviewAge = DateUtils.getAge(eligibilitySummaryDto.getDtChildBirth(), reviewDate);

			if (reviewAge < 18 || DateUtils.isBefore(startDatePlus1Year, reviewDate) && age < 18) {
				eligibilityDto.setDtEligReview(startDatePlus1Year);
			} else {
				eligibilityDto.setDtEligReview(reviewDate);
			}
		}

		if (CodesConstant.CEVTSTAT_NEW.equals(eventStatus) && (!ObjectUtils.isEmpty(eligibilityDto.getDtEligEnd()))
				&& (!ObjectUtils.isEmpty(eligibilityDto.getDtEligStart()))) {
			eligibilitySummaryDto.getEligibilityDto().setDtEligReview(ServiceConstants.NULL_VALDAT);
		}

		if ((CodesConstant.CEVTSTAT_NEW.equals(eventStatus)) && (CodesConstant.CELIGIBI_010.equals(selectedEligibility))
				&& (CodesConstant.CBILPLOC_010.equals(fceEligibilityDto.getCdBlocChild()))
				&& (!ObjectUtils.isEmpty(amtSsiDouble)) && (amtSsiDouble.doubleValue() > 0)) {
			throw new ServiceLayerException(lookudao.getMessageByNumber("25490"));
		}

		String eventDescription = lookudao.simpleDecodeSafe(CodesConstant.CELIGIBI, eligibilityDto.getCdEligSelected())
				+ " Start " + DateUtils.stringDt(eligibilityDto.getDtEligStart());

		String newEventStatus = CodesConstant.CEVTSTAT_PROC;

		if (!ObjectUtils.isEmpty(eligibilityDto.getDtEligEnd())) {
			fceService.syncFceEligibilityStatus(fceEligibilityDto);

			eventDescription += " End " + DateUtils.stringDt(eligibilityDto.getDtEligEnd());
			newEventStatus = CodesConstant.CEVTSTAT_COMP;
		}

		EligibilityDto oldEligibility = fceDao.findLegacyEligibility(idEligibilityEvent);

		String createOrUpdate = ServiceConstants.REQ_FUNC_CD_UPDATE;
		if (ObjectUtils.isEmpty(oldEligibility))
			createOrUpdate = ServiceConstants.REQ_FUNC_CD_ADD;

		EligibilitySummarySaveReq eligibilitySummarySaveReq = new EligibilitySummarySaveReq();

		eligibilitySummarySaveReq.setReqFuncCd(createOrUpdate);

		setCsup(oldEligibility, eligibilityDto, fceEligibilityDto.getIndChildSupportOrdered());

		if (!ObjectUtils.isEmpty(oldEligibility)) {
			eligibilityDto.setDtLastUpdate(oldEligibility.getDtLastUpdate());
		}

		if (ObjectUtils.isEmpty(eligibilityDto.getDtEligEnd()))
			eligibilityDto.setDtEligEnd(ServiceConstants.NULL_JAVA_DATE_DATE);

		eligibilitySummarySaveReq.setEligibilityDto(eligibilityDto);
		eligibilitySummarySaveReq.setFceEligibilityDto(fceEligibilityDto);

		EligibilityEventDto eligibilityEventDto = new EligibilityEventDto();

		eligibilityEventDto.setOldEventStatus(eventDto.getCdEventStatus());
		eligibilityEventDto.setNewEventStatus(newEventStatus);

		eligibilityEventDto.setDtEventOccurred(eventDto.getDtEventOccurred());
		eligibilityEventDto.setCdTask(FCE_ELIGIBILITY_TASK_CODE);

		String stageName = ServiceConstants.EMPTY_STRING;
		if (!ObjectUtils.isEmpty(eligibilityDto.getIdStage()))
			stageName = stagedao.getStageById(eligibilityDto.getIdStage()).getNmStage();

		eligibilityEventDto.setNmStage(stageName);

		eligibilityEventDto.setCdEventType(FCE_ELIGIBILITY_EVENT_TYPE);
		eligibilityEventDto.setIdEvent(idEligibilityEvent);
		eligibilityEventDto.setIdStage(eligibilityDto.getIdStage());

		// If the UlIdPerson is 0 use the "Last person updated" so that
		// System will no longer appear on generated Eligibility Summaries
		Long localIdPerson = eligibilityDto.getIdPerson();

		if (ObjectUtils.isEmpty(localIdPerson) || localIdPerson.equals(0L)) {
			localIdPerson = fceEligibilityDto.getIdLastUpdatePerson();
		}

		eligibilityEventDto.setIdPerson(localIdPerson);
		eligibilityEventDto.setEventDescr(eventDescription);

		eligibilityEventDto.setTsLastUpdate(eventDto.getDtLastUpdate());

		eligibilitySummarySaveReq.setElgiblityEventDto(eligibilityEventDto);

		List<String> cdMedUpdTransType = initializeCdMedUpdTransType(oldEligibility, eligibilityDto);

		eligibilitySummarySaveReq.setCdMedUpdTransType(cdMedUpdTransType);

		eligibilitySummarySaveReq.setIndPrfrmValidation(ServiceConstants.Y);

		String disableServiceValidations = eligibilitySummaryDto.getDisableServiceValidation();

		if (!ObjectUtils.isEmpty(disableServiceValidations) && ServiceConstants.N.equals(disableServiceValidations)) {
			eligibilitySummarySaveReq.setIndPrfrmValidation(ServiceConstants.N);
		}

		eligibilitySummarySaveReq.setIndNewExtndFCRevw(ServiceConstants.Y);

		eligibilitySummarySaveReq.setCdWinMode(ServiceConstants.PAGEMODE_MODIFY);
		if (ObjectUtils.isEmpty(oldEligibility)) {
			eligibilitySummarySaveReq.setCdWinMode(ServiceConstants.PAGEMODE_NEW);
		}

		eligibilitySummarySaveReq.setIndGeneric(ServiceConstants.N);
		//ALM 13665 adding idtodo to eligibilitySummarySaveReq
		//ALM 17659 idTodo will be null when there is no task associated with the Eligibility
		if (eligibilitySummaryDto.getIdTodo() != null && eligibilitySummaryDto.getIdTodo() > 0)
			eligibilitySummarySaveReq.setIdTodo(eligibilitySummaryDto.getIdTodo());

		// CSUB19S
		handlingSaveEligibility(eligibilitySummarySaveReq);
	}

	/**
	 * Method Name: handlingSaveEligibility Method Description: This method is
	 * used to handlingSaveEligibility
	 * 
	 * @param eligibilitySummarySaveReq
	 * @return
	 */
	private void handlingSaveEligibility(EligibilitySummarySaveReq eligibilitySummarySaveReq) {
		InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
		String eventReqFuncCd = ServiceConstants.ADD;
		Long idEligibilityEvent = eligibilitySummarySaveReq.getElgiblityEventDto().getIdEvent();
		if (!ObjectUtils.isEmpty(idEligibilityEvent) && idEligibilityEvent > 0) {
			eventReqFuncCd = ServiceConstants.UPDATE;
		}
		inCheckStageEventStatusDto.setCdReqFunction(eventReqFuncCd);
		inCheckStageEventStatusDto.setIdStage(eligibilitySummarySaveReq.getFceEligibilityDto().getIdStage());
		inCheckStageEventStatusDto.setCdTask(ServiceConstants.FCE_ELIGIBILITY_TASK_CODE);
		Boolean checkStageStatus = checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
		if (!Boolean.TRUE.equals(checkStageStatus)) {
			throw new ServiceLayerException(ServiceConstants.FND_FAIL);
		}
		Long idChild = stageDao.findPrimaryChildForStage(eligibilitySummarySaveReq.getFceEligibilityDto().getIdStage());
		if (ServiceConstants.ADD.equals(eventReqFuncCd)) {
			String taskCode = ServiceConstants.FCE_ELIGIBILITY_TASK_CODE;
			String description = fceService.getDescription(taskCode, ServiceConstants.NEW_EVENT);
			idEligibilityEvent = fceService.createPostEvent(Long.valueOf(eligibilitySummarySaveReq.getUserId()),
					eligibilitySummarySaveReq.getFceEligibilityDto().getIdStage(), idChild, taskCode,
					ServiceConstants.FCE_ELIGIBILITY_EVENT_TYPE, description);
			eligibilitySummarySaveReq.getEligibilityDto().setIdEligEvent(idEligibilityEvent);
		} else {
			eventService.updateEventStatus(idEligibilityEvent,
					eligibilitySummarySaveReq.getElgiblityEventDto().getNewEventStatus(),
					eligibilitySummarySaveReq.getElgiblityEventDto().getEventDescr());
		}
		saveEligibility(eligibilitySummarySaveReq, idChild);
	}

	/**
	 * Method Name: saveEligibility Method Description: This method is used to
	 * saveEligibility
	 * 
	 * @param eligibilitySummarySaveReq
	 * @param idPerson
	 * @return void
	 */
	private void saveEligibility(EligibilitySummarySaveReq eligibilitySummarySaveReq, Long idPerson) {

		/**
		 * LOGIC: Two operations: INSERT and UPDATE ('U') are allowed. DELETE is
		 * not
		 * 
		 * If EndDate or StartDate is NULL its will be set to MAXDATE
		 * 
		 * 1) INSERT: Input criteria: a) SysIndPrfrmValidation For a given
		 * ID_PERSON (hI_ulIdPerson) do the following:
		 * 
		 * 
		 * Always checks OVERLAP_1 and OVERLAP_2 If SysIndPrfrmValidation ="Y"
		 * then checks EXISTS_1 and EXISTS_2 Both checks "hI_dtDtEligStart" and
		 * "hI_dtDtEligEnd" against other existing records If both are passed:
		 * insert new record
		 * 
		 * 
		 * If SysIndPrfrmValidation <>"Y" no need to check EXISTS_1 and
		 * EXISTS_2. Thus a record can be inserted with its PlocStart Date and
		 * PlocEnd Date overlaps with other existing records if its PlocType is
		 * not BLOC and you do not want to check (set SysIndPrfrmValidation='N')
		 * 
		 * 
		 * 2) UPDATE: Input criteria: for a given ID_ELIG_EVENT, DT_LAST_UPDATE
		 * These 2 fields must match in order for that record to be updated
		 * (different input for INSERT)
		 * 
		 * a) CdPlocType and SysIndPrfrmValidation If new CdPlocType="BLOC" then
		 * checks OVERLAP_1 and OVERLAP_2
		 * 
		 * If SysIndPrfrmValidation ="Y" then If new START date is diff from old
		 * START date ==> check GAP_EXIST_1 (set GAP_EXIST_1 if the gap is >=
		 * 1.0 day) If new END date is diff from old END date ==> check
		 * GAP_EXIST_2 (set GAP_EXIST_2 if the gap is >= 1.0 day)
		 * 
		 * 
		 * If both are passed: update record. If SysIndPrfrmValidation <>"Y" no
		 * need to check EXISTS_1 and EXISTS_2. Thus a record can be updated
		 * with its EligStart Date and EligEnd Date overlaps with other existing
		 * records if SysIndPrfrmValidation='N'
		 **/

		EligibilityDto eligibilityDto = null;
		List<Long> idEligEventList = null;
		Long idEligEvent = null;
		boolean bLeftGapExists = false;
		boolean bRightGapExists = false;
		String indPrfrmValidation = eligibilitySummarySaveReq.getIndPrfrmValidation();
		Date startDate = eligibilitySummarySaveReq.getEligibilityDto().getDtEligStart();
		Date endDate = eligibilitySummarySaveReq.getEligibilityDto().getDtEligEnd();

		if (ServiceConstants.ADD.equals(eligibilitySummarySaveReq.getReqFuncCd())) {

			idEligEventList = fceDao.findEligEventIdByPersonId(idPerson);
			/*****************************************************************/
			/* Check if there's any record of this ID_PERSON. If none, then */
			/* everything passed. No need to go through all these validation. */
			/*****************************************************************/
			if (!ObjectUtils.isEmpty(idEligEventList) && idEligEventList.size() > 0) {
				idEligEventList = fceDao.saveEligFirstValidation(idPerson, startDate, endDate);
				if (!ObjectUtils.isEmpty(idEligEventList) && idEligEventList.size() > 0)
					throw new ServiceLayerException("", ServiceConstants.MSG_SUB_PERIOD_OVERLAP_1, null);

				idEligEventList = fceDao.saveEligSecondValidation(idPerson, startDate, endDate);
				if (!ObjectUtils.isEmpty(idEligEventList) && idEligEventList.size() > 0)
					throw new ServiceLayerException("", ServiceConstants.MSG_SUB_PERIOD_OVERLAP_2, null);

				idEligEventList = fceDao.saveEligThirdValidation(idPerson, startDate, endDate);
				if (!ObjectUtils.isEmpty(idEligEventList) && idEligEventList.size() > 0)
					throw new ServiceLayerException("", ServiceConstants.MSG_SUB_PERIOD_OVERLAP_1, null);

				if (ServiceConstants.Y.equals(indPrfrmValidation)) {
					eligibilityDto = fceDao.saveEligFourthValidation(idPerson, startDate, endDate);

					if (!ObjectUtils.isEmpty(eligibilityDto) && !ObjectUtils.isEmpty(eligibilityDto.getDtEligEnd())) {
						/*
						 * java.sql.Date startSqlDate = new
						 * java.sql.Date(startDate.getTime()); java.sql.Date
						 * endSqlDate = new
						 * java.sql.Date(eligibilityDto.getDtEligEnd().getTime()
						 * );
						 */
						Date startSqlDate = DateUtils.getDateWithoutTime(startDate);
						Date endSqlDate = DateUtils.getDateWithoutTime(eligibilityDto.getDtEligEnd());
						if (DateUtils.daysDifference(startSqlDate, endSqlDate) >= 1.0)
							bLeftGapExists = true;
					}
					eligibilityDto = fceDao.saveEligFifthValidation(idPerson, startDate, endDate);
					if (!ObjectUtils.isEmpty(eligibilityDto) && !ObjectUtils.isEmpty(eligibilityDto.getDtEligStart())
							&& DateUtils.daysDifference(eligibilityDto.getDtEligStart(), endDate) >= 1.0) {
						/*
						 * java.sql.Date startSqlDate = new
						 * java.sql.Date(eligibilityDto.getDtEligStart().getTime
						 * ()); java.sql.Date endSqlDate = new
						 * java.sql.Date(endDate.getTime());
						 */
						Date startSqlDate = DateUtils.getDateWithoutTime(eligibilityDto.getDtEligStart());
						Date endSqlDate = DateUtils.getDateWithoutTime(endDate);
						if (DateUtils.daysDifference(startSqlDate, endSqlDate) >= 1.0)
							bRightGapExists = true;
					}
				}

				isGapExists(bLeftGapExists, bRightGapExists);

				if (ServiceConstants.Y.equals(eligibilitySummarySaveReq.getIndGeneric())) {
					eligibilityDto = fceDao.determineEligWasCourtOrd(idPerson,
							eligibilitySummarySaveReq.getEligibilityDto().getIdEligEvent());

					if (!ObjectUtils.isEmpty(eligibilityDto)) {
						if ((!ObjectUtils.isEmpty(eligibilityDto.getCdEligCsupQuest1())
								&& eligibilityDto.getCdEligCsupQuest1()
										.equals(eligibilitySummarySaveReq.getEligibilityDto().getCdEligCsupQuest1()))
								|| (!ObjectUtils.isEmpty(eligibilityDto.getCdEligCsupQuest2())
										&& eligibilityDto.getCdEligCsupQuest2().equals(
												eligibilitySummarySaveReq.getEligibilityDto().getCdEligCsupQuest2()))
								|| (!ObjectUtils.isEmpty(eligibilityDto.getCdEligCsupQuest3())
										&& eligibilityDto.getCdEligCsupQuest3().equals(
												eligibilitySummarySaveReq.getEligibilityDto().getCdEligCsupQuest3()))
								|| (!ObjectUtils.isEmpty(eligibilityDto.getCdEligCsupQuest4())
										&& eligibilityDto.getCdEligCsupQuest4().equals(
												eligibilitySummarySaveReq.getEligibilityDto().getCdEligCsupQuest4()))
								|| (!ObjectUtils.isEmpty(eligibilityDto.getCdEligCsupQuest5())
										&& eligibilityDto.getCdEligCsupQuest5().equals(
												eligibilitySummarySaveReq.getEligibilityDto().getCdEligCsupQuest5()))
								|| (!ObjectUtils.isEmpty(eligibilityDto.getCdEligCsupQuest6())
										&& eligibilityDto.getCdEligCsupQuest6().equals(
												eligibilitySummarySaveReq.getEligibilityDto().getCdEligCsupQuest6()))
								|| (!ObjectUtils.isEmpty(eligibilityDto.getCdEligCsupQuest7())
										&& eligibilityDto.getCdEligCsupQuest7().equals(
												eligibilitySummarySaveReq.getEligibilityDto().getCdEligCsupQuest7()))) {

							throw new ServiceLayerException("", ServiceConstants.MSG_SUB_COURT_ORDERED, null);
						}
					}

					idEligEventList = fceDao.saveEligSeventhValidation(idPerson, startDate, endDate);
					if (!ObjectUtils.isEmpty(idEligEventList) && idEligEventList.size() > 0)
						throw new ServiceLayerException("", ServiceConstants.MSG_SUB_PERIOD_OVERLAP_2, null);
				}
			}
			eligibilityDto = eligibilitySummarySaveReq.getEligibilityDto();
			eligibilityDto.setIdPerson(idPerson);
			eligibilityDto.setIdPersonUpdate(eligibilitySummarySaveReq.getFceEligibilityDto().getIdLastUpdatePerson());
			eligibilityDto.setIdCase(eligibilitySummarySaveReq.getFceEligibilityDto().getIdCase());
			eligibilityDto.setIdStage(eligibilitySummarySaveReq.getFceEligibilityDto().getIdStage());

			eligibilitySummarySaveReq.getEligibilityDto()
					.setIdEligEvent(eligibilitySummarySaveReq.getFceEligibilityDto().getIdEligibilityEvent());
			/**************************************************************************/
			/* Here record is ready to be inserted. New record could be: */
			/*
			 * 1. NON-BLOC type: Just insert it in directly regardless if any
			 * record
			 */
			/* exists with the same ID_PERSON. */
			/*
			 * 2. BLOC type: a. No record exists of BLOC type. Then this is a
			 * brand
			 */
			/* new record. Just insert it into the chain. */
			/* b. BLOC records exists, but this new record passes all */
			/* validation. Just insert it into the chain */
			/**************************************************************************/
			idEligEvent = fceDao.saveElig(eligibilitySummarySaveReq.getEligibilityDto());

		} else if (ServiceConstants.UPDATE.equals(eligibilitySummarySaveReq.getReqFuncCd())) {
			idEligEvent = eligibilitySummarySaveReq.getFceEligibilityDto().getIdEligibilityEvent();
			Date dtLastUpdate = eligibilitySummarySaveReq.getEligibilityDto().getDtLastUpdate();

			eligibilityDto = fceDao.findEligEventIdByIdEligEvent(idEligEvent, idPerson, startDate, endDate,
					eligibilitySummarySaveReq.getEligibilityDto().getDtLastUpdate());
			if (ObjectUtils.isEmpty(dtLastUpdate) || ObjectUtils.isEmpty(eligibilityDto.getDtLastUpdate())
					|| dtLastUpdate.compareTo(eligibilityDto.getDtLastUpdate()) != 0) {
				throw new ServiceLayerException("MSG_CMN_TMSTAMP_MISMATCH",
						(long) ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH, null);
			}
			if ((ObjectUtils.isEmpty(eligibilityDto.getDtCurrentPlocStart())
					|| ObjectUtils.isEmpty(eligibilityDto.getDtCurrentPlocEnd()))) {
				throw new ServiceLayerException("eligibility Start or End data is null");
			}
			Date dtCurrentPlocStart = eligibilityDto.getDtCurrentPlocStart();
			Date dtCurrentPlocEnd = eligibilityDto.getDtCurrentPlocEnd();

			/*
			 * SimpleDateFormat sdf = new
			 * SimpleDateFormat(ServiceConstants.DATE_FORMAT_YY_MM_DD);
			 * 
			 * String strStartDate = sdf.format(startDate); startDate =
			 * DateUtils.stringDate(strStartDate); String strEndDate =
			 * sdf.format(endDate); endDate = DateUtils.stringDate(strEndDate);
			 * java.sql.Date startSqlDate = new
			 * java.sql.Date(DateUtils.stringDate(strStartDate).getTime());
			 * java.sql.Date endSqlDate = new
			 * java.sql.Date(DateUtils.stringDate(strEndDate).getTime());
			 * endDate = endSqlDate; startDate = startSqlDate;
			 */

			/*
			 * java.sql.Date startSqlDate = new
			 * java.sql.Date(startDate.getTime()); java.sql.Date endSqlDate =
			 * new java.sql.Date(endDate.getTime()); endDate = endSqlDate;
			 * startDate = startSqlDate;
			 */

			startDate = DateUtils.getDateWithoutTime(startDate);
			endDate = DateUtils.getDateWithoutTime(endDate);

			idEligEventList = fceDao.updateEligFirstValidation(idEligEvent, idPerson, startDate, dtCurrentPlocStart);
			if (!ObjectUtils.isEmpty(idEligEventList) && idEligEventList.size() > 0)
				throw new ServiceLayerException("", ServiceConstants.MSG_SUB_PERIOD_OVERLAP_1, null);

			idEligEventList = fceDao.updateEligSecondValidation(idEligEvent, idPerson, endDate, dtCurrentPlocEnd);
			if (!ObjectUtils.isEmpty(idEligEventList) && idEligEventList.size() > 0)
				throw new ServiceLayerException("", ServiceConstants.MSG_SUB_PERIOD_OVERLAP_2, null);

			if (ServiceConstants.Y.equals(indPrfrmValidation)) {
				eligibilityDto = fceDao.updateEligThirdValidation(idPerson, startDate, dtCurrentPlocStart);
				if (!ObjectUtils.isEmpty(eligibilityDto) && !ObjectUtils.isEmpty(eligibilityDto.getDtEligEnd())) {
					Date endSqlDate = DateUtils.getDateWithoutTime(eligibilityDto.getDtEligEnd());
					if (DateUtils.daysDifference(startDate, endSqlDate) >= 1.0)
						bLeftGapExists = true;
				}
				eligibilityDto = fceDao.updateEligFourthValidation(idPerson, endDate, dtCurrentPlocEnd);
				if (!ObjectUtils.isEmpty(eligibilityDto) && !ObjectUtils.isEmpty(eligibilityDto.getDtEligStart())) {
					Date startSqlDate = DateUtils.getDateWithoutTime(eligibilityDto.getDtEligStart());
					if (DateUtils.daysDifference(startSqlDate, endDate) >= 1.0)
						bRightGapExists = true;
				}
			}
			isGapExists(bLeftGapExists, bRightGapExists);

			eligibilitySummarySaveReq.getEligibilityDto().setIdPersonUpdate(eligibilitySummarySaveReq.getFceEligibilityDto().getIdLastUpdatePerson());
			fceDao.updateElig(eligibilitySummarySaveReq.getEligibilityDto());
		}

		String cdWinMode = eligibilitySummarySaveReq.getCdWinMode();
		//Fix for defect 13012 - FC Elig Review Date Cannot Be Updated
		if (ServiceConstants.PAGEMODE_NEW.equals(cdWinMode)
				&& !ObjectUtils.isEmpty(eligibilitySummarySaveReq.getEligibilityDto().getDtEligReview())) {
			toDoCommonFunction(eligibilitySummarySaveReq);
		}

		if (!ObjectUtils.isEmpty(eligibilitySummarySaveReq.getCdMedUpdTransType())) {
			medicaidUpdate(eligibilitySummarySaveReq,idPerson);
		}

		workLoadDao.getPersonIdByRole(eligibilitySummarySaveReq.getFceEligibilityDto().getIdStage(),
				ServiceConstants.SECONDARY_WORKER);
		//ALM 13665: retrieving todo record and completing the todo task.
		//ALM 17659 idTodo will be null when there is no task associated with the Eligibility
		if (eligibilitySummarySaveReq.getIdTodo() != null && eligibilitySummarySaveReq.getIdTodo() > 0) {
			TodoDto	todoDto = todoDao.getTodoDtlsById(eligibilitySummarySaveReq.getIdTodo());
			if (todoDto != null && todoDto.getIdTodo() > 0 && todoDto.getDtTodoCompleted() == null) {
				todoDto.setDtTodoCompleted(new Date());
				todoDao.updateTodo(todoDto);
			}
		}
	}

	/**
	 * Method Name: medicaidUpdate 
	 * Method Description: This method is used to save/update the Medicaid
	 * @param eligibilitySummarySaveReq
	 * @param idPerson
	 */
	private void medicaidUpdate(EligibilitySummarySaveReq eligibilitySummarySaveReq,Long idPerson) {

		List<String> cdMedUpdTransType = eligibilitySummarySaveReq.getCdMedUpdTransType();

		cdMedUpdTransType.stream().filter(Objects::nonNull).forEach(transType -> {
			MedicaidUpdateDto medicaidUpdateDto = new MedicaidUpdateDto();

			medicaidUpdateDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			medicaidUpdateDto.setIdMedUpdStage(eligibilitySummarySaveReq.getEligibilityDto().getIdStage());
			//Warranty Defect#11157 fix
			medicaidUpdateDto.setIdMedUpdPerson((!ObjectUtils.isEmpty(idPerson) && idPerson.equals(eligibilitySummarySaveReq.getEligibilityDto().getIdPerson())) ? 
					eligibilitySummarySaveReq.getEligibilityDto().getIdPerson() : idPerson);
			medicaidUpdateDto.setIdMedUpdRecord(eligibilitySummarySaveReq.getElgiblityEventDto().getIdEvent());
			medicaidUpdateDto.setCdMedUpdType(eligibilitySummarySaveReq.getElgiblityEventDto().getCdEventType());
			medicaidUpdateDto.setCdMedUpdTransType(transType);

			medicaidUpdateDao.addMedicaidUpdate(medicaidUpdateDto);

		});

	}

	private TodoCommonFunctionOutputDto toDoCommonFunction(EligibilitySummarySaveReq eligibilitySummarySaveReq) {
		TodoCommonFunctionInputDto todoCommonFunctionInputDto = new TodoCommonFunctionInputDto();
		TodoCommonFunctionDto todoCommonFunctionDto = new TodoCommonFunctionDto();

		String indNewExtndFCRevw = eligibilitySummarySaveReq.getIndNewExtndFCRevw();
		Date dtReview = eligibilitySummarySaveReq.getEligibilityDto().getDtEligReview();

		if (!ObjectUtils.isEmpty(dtReview)) {
			if (ServiceConstants.Y.equals(indNewExtndFCRevw)) {
				todoCommonFunctionDto.setDtSysDtTodoCfDueFrom(DateUtils.subtractDaysFromDate(dtReview, 30));
			} else
				todoCommonFunctionDto.setDtSysDtTodoCfDueFrom(DateUtils.subtractDaysFromDate(dtReview, 23));
		}

		todoCommonFunctionDto.setSysCdTodoCf(TODO_CODE_SUB022);
		todoCommonFunctionDto
				.setSysIdTodoCfPersCrea(eligibilitySummarySaveReq.getFceEligibilityDto().getIdLastUpdatePerson());
		todoCommonFunctionDto.setSysIdTodoCfEvent(0L);
		todoCommonFunctionDto.setSysIdTodoCfStage(eligibilitySummarySaveReq.getEligibilityDto().getIdStage());

		todoCommonFunctionInputDto.setTodoCommonFunctionDto(todoCommonFunctionDto);

		TodoCommonFunctionOutputDto commonFunctionOutputDto = todoCommonFunctionService
				.TodoCommonFunction(todoCommonFunctionInputDto);

		return commonFunctionOutputDto;

	}

	private void isGapExists(boolean bLeftGapExists, boolean bRightGapExists) {
		if (bLeftGapExists && bRightGapExists)
			throw new ServiceLayerException("", ServiceConstants.MSG_SUB_GAP_EXISTS_3, null);
		else if (bLeftGapExists)
			throw new ServiceLayerException("", ServiceConstants.MSG_SUB_GAP_EXISTS_1, null);
		else if (bRightGapExists)
			throw new ServiceLayerException("", ServiceConstants.MSG_SUB_GAP_EXISTS_2, null);
	}

	/**
	 * Method Name: setCsup Method Description: This method is used to setCsup
	 * 
	 * @param oldEligibility
	 * @param eligibilityDto
	 * @param indChildSupportOrdered
	 * @return void
	 */
	private void setCsup(EligibilityDto oldEligibility, EligibilityDto eligibilityDto, String indChildSupportOrdered) {

		String oldActualEligibility = ServiceConstants.EMPTY_STRING;
		String oldSelectedEligibility = ServiceConstants.EMPTY_STRING;

		if (!ObjectUtils.isEmpty(oldEligibility)) {
			oldActualEligibility = oldEligibility.getCdEligActual();
			oldSelectedEligibility = oldEligibility.getCdEligSelected();
		}

		String newActualEligibility = eligibilityDto.getCdEligActual();
		String newSelectedEligibility = eligibilityDto.getCdEligSelected();

		boolean actualEligibilityChanged = (newActualEligibility.equals(oldActualEligibility) == false);
		boolean selectedEligibilityChanged = (newSelectedEligibility.equals(oldSelectedEligibility) == false);

		if (!ObjectUtils.isEmpty(oldEligibility)) {
			eligibilityDto.setIndEligCsupSend(String.valueOf(oldEligibility.getIndEligCsupSend()));
			eligibilityDto.setDtEligCsupReferral(oldEligibility.getDtEligCsupReferral());
		}

		boolean clearCheckboxes = false;
		if ((actualEligibilityChanged) || (selectedEligibilityChanged) || (ObjectUtils.isEmpty(oldEligibility))) {
			if ((CodesConstant.CELIGIBI_010.equals(newSelectedEligibility))
					|| (CodesConstant.CELIGIBI_020.equals(newSelectedEligibility))
					|| (CodesConstant.CELIGIBI_030.equals(newSelectedEligibility))
					|| (CodesConstant.CELIGIBI_010.equals(oldSelectedEligibility))
					|| (CodesConstant.CELIGIBI_020.equals(oldSelectedEligibility))
					|| (CodesConstant.CELIGIBI_030.equals(oldSelectedEligibility))) {
				eligibilityDto.setIndEligCsupSend(ServiceConstants.Y);
				eligibilityDto.setDtEligCsupReferral(new Date());
			} else {
				eligibilityDto.setIndEligCsupSend(ServiceConstants.N);
				eligibilityDto.setDtEligCsupReferral(ServiceConstants.NULL_VALDAT);
				clearCheckboxes = true;
			}
		}

		List<String> cdEligCsupQuest1List = initializeCdEligCsupQuest1List(oldEligibility, indChildSupportOrdered);

		if (clearCheckboxes) {
			cdEligCsupQuest1List = new ArrayList<String>();
		}
		//Fix for defect 12676
		if(ServiceConstants.YES.equals(indChildSupportOrdered)) {
			eligibilityDto.setCdEligCsupQuest1(ServiceConstants.CELIGIBI_030);
		}
		
		eligibilityDto.setCdEligCsupQuest1List(cdEligCsupQuest1List);

	}

	/**
	 * Method Name: initializeCdEligCsupQuest1List Method Description: This
	 * method is used to initializeCdEligCsupQuest1List
	 * 
	 * @param oldEligibility
	 * @param indChildSupportOrdered
	 * @return
	 * @return List<String>
	 */
	private List<String> initializeCdEligCsupQuest1List(EligibilityDto oldEligibility, String indChildSupportOrdered) {

		String[] questions = new String[7];
		// preserve old values of 1, 2, 4
		if (!ObjectUtils.isEmpty(oldEligibility)) {
			if (isQuestionAnswered(CodesConstant.CELIGCSU_010, oldEligibility)) {
				questions[0] = CodesConstant.CELIGCSU_010;
			}
			if (isQuestionAnswered(CodesConstant.CELIGCSU_020, oldEligibility)) {
				questions[1] = CodesConstant.CELIGCSU_020;
			}
			if (isQuestionAnswered(CodesConstant.CELIGCSU_040, oldEligibility)) {
				questions[3] = CodesConstant.CELIGCSU_040;
			}
		}
		// set question3 according to checkbox on EligibilitySummary
		if (ServiceConstants.Y.equals(indChildSupportOrdered)) {
			questions[2] = CodesConstant.CELIGCSU_030;
		}

		return Arrays.asList(questions);

	}

	/**
	 * Method Name: initializeCdMedUpdTransType Method Description: This method
	 * is used to initializeCdMedUpdTransType
	 * 
	 * @param wasEligibilityDto
	 * @param newEligibilityDto
	 * @return List<String>
	 */
	private List<String> initializeCdMedUpdTransType(EligibilityDto wasEligibilityDto,
			EligibilityDto newEligibilityDto) {

		String[] cdMedUpdTransTypeArray = new String[5];

		Date wasStartDate = ServiceConstants.NULL_VALDAT;
		Date wasEndDate = ServiceConstants.NULL_VALDAT;
		String wasCdEligSelected = null;
		boolean wasTitleIVE = false;
		boolean wasStatePaid = false;
		boolean wasMAO = false;
		String wasCdMedicaidEligibility = ServiceConstants.EMPTY_STRING;
		boolean wasMedicaidEligible = false;

		if (!ObjectUtils.isEmpty(wasEligibilityDto)) {
			wasStartDate = wasEligibilityDto.getDtEligStart();
			wasEndDate = normalizeEndDate(wasEligibilityDto.getDtEligEnd());
			wasCdEligSelected = wasEligibilityDto.getCdEligSelected();
			wasTitleIVE = CodesConstant.CELIGIBI_010.equals(wasCdEligSelected);
			wasStatePaid = CodesConstant.CELIGIBI_020.equals(wasCdEligSelected);
			wasMAO = CodesConstant.CELIGIBI_030.equals(wasCdEligSelected);
			wasCdMedicaidEligibility = wasEligibilityDto.getCdEligMedEligGroup();

			wasMedicaidEligible = ((wasTitleIVE) || (wasStatePaid) || (wasMAO));
		}

		Date isStartDate = newEligibilityDto.getDtEligStart();
		Date isEndDate = normalizeEndDate(newEligibilityDto.getDtEligEnd());
		String isCdEligSelected = newEligibilityDto.getCdEligSelected();
		boolean isTitleIVE = CodesConstant.CELIGIBI_010.equals(isCdEligSelected);
		boolean isStatePaid = CodesConstant.CELIGIBI_020.equals(isCdEligSelected);
		boolean isMAO = CodesConstant.CELIGIBI_030.equals(isCdEligSelected);
		String isCdMedicaidEligibility = newEligibilityDto.getCdEligMedEligGroup();

		boolean isMedicaidEligible = ((isTitleIVE) || (isStatePaid) || (isMAO));

		if ((isMedicaidEligible) && (ObjectUtils.isEmpty(wasStartDate)) && (!ObjectUtils.isEmpty(isStartDate))
				&& (!ObjectUtils.isEmpty(isEndDate)) && (!CodesConstant.CELIGMED_X.equals(isCdMedicaidEligibility))) {
			cdMedUpdTransTypeArray[4] = CodesConstant.CMEDUPTR_OPC;
		} else if ((isMedicaidEligible) && (ObjectUtils.isEmpty(wasStartDate)) && (!ObjectUtils.isEmpty(isStartDate))
				&& (ObjectUtils.isEmpty(isEndDate)) && (!CodesConstant.CELIGMED_X.equals(isCdMedicaidEligibility))) {
			cdMedUpdTransTypeArray[0] = CodesConstant.CMEDUPTR_ADD;
		} else if ((isMedicaidEligible) && (!ObjectUtils.isEmpty(wasStartDate)) && (ObjectUtils.isEmpty(wasEndDate))
				&& (!ObjectUtils.isEmpty(isEndDate)) && (!CodesConstant.CELIGMED_X.equals(wasCdMedicaidEligibility))) {
			cdMedUpdTransTypeArray[1] = CodesConstant.CMEDUPTR_DEN;
		}

		if ((!ObjectUtils.isEmpty(wasStartDate)) && (ObjectUtils.isEmpty(wasEndDate))
				&& (ObjectUtils.isEmpty(isEndDate)) && (!wasCdMedicaidEligibility.equals(isCdMedicaidEligibility))) {
			if (CodesConstant.CELIGMED_X.equals(isCdMedicaidEligibility)) {
				cdMedUpdTransTypeArray[1] = CodesConstant.CMEDUPTR_DEN;
			} else {
				cdMedUpdTransTypeArray[2] = CodesConstant.CMEDUPTR_SUS;
			}
		}

		if (((isMedicaidEligible) || (wasMedicaidEligible)) && (!ObjectUtils.isEmpty(wasStartDate))
				&& (ObjectUtils.isEmpty(wasEndDate)) && (ObjectUtils.isEmpty(isEndDate))
				&& (!wasCdEligSelected.equals(isCdEligSelected))) {
			if (CodesConstant.CELIGMED_X.equals(isCdMedicaidEligibility)) {
				cdMedUpdTransTypeArray[1] = CodesConstant.CMEDUPTR_DEN;
			} else {
				cdMedUpdTransTypeArray[3] = CodesConstant.CMEDUPTR_TRA;
			}
		}

		return Arrays.asList(cdMedUpdTransTypeArray);
	}

	private static Date normalizeEndDate(Date date) {
		if ((ObjectUtils.isEmpty(date)) || (date.getTime() == ServiceConstants.MAX_DATE.getTime())) {
			return null;
		}
		return date;
	}

	private static boolean isQuestionAnswered(String question, EligibilityDto eligibilityDto) {
		return ((question.equals(eligibilityDto.getCdEligCsupQuest1()))
				|| (question.equals(eligibilityDto.getCdEligCsupQuest2()))
				|| (question.equals(eligibilityDto.getCdEligCsupQuest3()))
				|| (question.equals(eligibilityDto.getCdEligCsupQuest4())));
	}

	private static String isPlacementPrsPaid(String cdPlcmtType, String cdPlcmtLivArr) {

		if (ObjectUtils.isEmpty(cdPlcmtType) || CodesConstant.CPLMNTYP_010.equals(cdPlcmtType)
				|| CodesConstant.CPLMNTYP_090.equals(cdPlcmtType) || CodesConstant.CPLMNTYP_040.equals(cdPlcmtType)) {
			return ServiceConstants.Y;
		}
		// CD_PLCMT_LIV_ARR not in ('GT', '71') which is in prebill code
		if (CodesConstant.CPLLAFRM_GT.equals(cdPlcmtLivArr) || CodesConstant.CPLLAFRM_71.equals(cdPlcmtLivArr)) {
			return ServiceConstants.Y;
		}

		return ServiceConstants.N;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.fce.service.EligibilitySummaryService#
	 * isAutoEligibility(java.lang.Long)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isAutoEligibility(Long idEvent) {
		return fceDao.isAutoEligibility(idEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.fce.service.EligibilitySummaryService#
	 * getAdoProcessStatus(java.lang.Long)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean getAdoProcessStatus(Long idStage) {
		return adoptionAsstncDao.getAdoProcessStatus(idStage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.fce.service.EligibilitySummaryService#
	 * getFceEligibility(java.lang.Long)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FceEligibilityDto getFceEligibility(Long idFceEligibility) {
		return fceDao.getFceEligibility(idFceEligibility);
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public EligibilitySummaryRes getEligibilityByStage(Long idStage) {

		List<EligibilityDto> eligibilityDtoList = fceDao.findEligByidStage(idStage);
		EligibilitySummaryRes response  = new EligibilitySummaryRes();
		response.setEligibilityListDto(eligibilityDtoList);
		return response;
	}

}
