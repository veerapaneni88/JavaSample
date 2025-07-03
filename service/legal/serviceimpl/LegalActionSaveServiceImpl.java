package us.tx.state.dfps.service.legal.serviceimpl;

import static java.time.temporal.ChronoUnit.DAYS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.LegalAction;
import us.tx.state.dfps.common.dto.EmailDetailsDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dao.FetchToDoDao;
import us.tx.state.dfps.service.admin.dao.PersonInfoDao;
import us.tx.state.dfps.service.admin.dao.TodoCreateDao;
import us.tx.state.dfps.service.admin.dao.UpdateToDoDao;
import us.tx.state.dfps.service.admin.dto.APSRoraRowDto;
import us.tx.state.dfps.service.admin.dto.CheckStageInpDto;
import us.tx.state.dfps.service.admin.dto.EventStageDiDto;
import us.tx.state.dfps.service.admin.dto.FetchToDoDto;
import us.tx.state.dfps.service.admin.dto.FetchToDodiDto;
import us.tx.state.dfps.service.admin.dto.PostDto;
import us.tx.state.dfps.service.admin.dto.PostOutputDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.dto.UpdateToDoDto;
import us.tx.state.dfps.service.admin.service.EventTaskStageService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.admin.service.TodoCreateService;
import us.tx.state.dfps.service.baseriskassmt.dto.MPSStatsValueDto;
import us.tx.state.dfps.service.casepackage.dao.CaseDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.AssignSaveGroupReq;
import us.tx.state.dfps.service.common.request.LegalActionSaveReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.EmployeeMailRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.JNDIUtil;
import us.tx.state.dfps.service.common.util.JNDIUtil.EncryptionException;
import us.tx.state.dfps.service.common.util.OutlookUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.common.utils.MPSStatsUtils;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.legal.dao.LeglActnModificationDao;
import us.tx.state.dfps.service.legal.dao.PersonDetailsDao;
import us.tx.state.dfps.service.legal.dao.ToDoEventDao;
import us.tx.state.dfps.service.legal.dto.FetchToDoOutDto;
import us.tx.state.dfps.service.legal.dto.LegalActionRtrvOutDto;
import us.tx.state.dfps.service.legal.dto.LegalActionSaveInDto;
import us.tx.state.dfps.service.legal.dto.LegalActionSaveOutDto;
import us.tx.state.dfps.service.legal.dto.LegalActionsModificationDto;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdiDto;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdoDto;
import us.tx.state.dfps.service.legal.dto.SearchArrayToDoDto;
import us.tx.state.dfps.service.legal.dto.SearchTodoDto;
import us.tx.state.dfps.service.legal.service.LegalActionSaveService;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.outlook.AppointmentDto;
import us.tx.state.dfps.service.person.service.PersonListService;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.DomicileDeprivationChildToEventDto;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.PersonEmployeeInDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;
import us.tx.state.dfps.xmlstructs.inputstructs.SynchronizationServiceDto;
import us.tx.state.dfps.xmlstructs.outputstructs.PersonEmployeeOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * implements the method in LegalActionSaveService Oct 25, 2017- 10:36:53 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class LegalActionSaveServiceImpl implements LegalActionSaveService {

	private static final String NULL = "";

	@Autowired
	private TodoCreateService todoCreateService;

	@Autowired
	private EventTaskStageService eventTaskStageService;

	@Autowired
	private PostEventService postEventService;

	@Autowired
	private UpdateToDoDao updateToDoDao;

	@Autowired
	private LeglActnModificationDao leglActnModificationDao;

	@Autowired
	private FetchToDoDao fetchToDoDao;

	@Autowired
	private ToDoEventDao toDoEventDao;

	@Autowired
	private PersonInfoDao personInfoDao;

	@Autowired
	private TodoCreateDao todoCreateDao;

	@Autowired
	private PersonDetailsDao personDetailsDao;

	@Autowired
	private LookupDao lookupDao;

	@Autowired
	private OutlookUtil outlookUtil;
	
	@Autowired
	private StageDao stageDao;
	
	@Autowired
	private CaseDao caseDao;
	
	@Autowired
	TodoDao toDoDao;
	
	@Autowired
	PersonListService  personListService;
	
	@Autowired
	TodoDao todoDao;

	@Autowired
	CaseUtils caseUtils;

	private static final Logger log = Logger.getLogger(LegalActionSaveServiceImpl.class);

	public static final ResourceBundle EMAIL_CONFIG_BUNDLE = ResourceBundle.getBundle("EmailConfig");
	public static final String LEGAL_ACTION_EMAIL_CONFIG_BASE = "LegalActionOutcome.emailId.";
	private static final String PROD = "Prod";
	private static final String SUCCESS = "success";
	private static final String OUTLOOKSECURITY = "jndi/outlook_security";
	public static final String EXCEPTION_STRING_ONE = "Exception Occured while ";
	private static String NO_JNDI = "JNDI_NOT_PRESENT";
	public static final String IS_ON = "is on";
	public static final String FOR = "for";
	public static final String CLEGCPS_CCVS = "CCVS";
	public static final String PERM_REVIEW_HEARING_BEFORE_FINAL_ORDER = "Permanency Hearing Before Final Order";
	public static final String PERM_REVIEW_HEARING_AFTER_FINAL_ORDER = "Permanency Hearing After Final Order";
	public static final String STATUS_HEARING = "Status Hearing";
	public static final String CCVS_10 = "10";
	public static final String CCVS_20 = "20";
	public static final String CCVS_30 = "30";
	public static final String CCVS_40 = "40";
	public static final String CCVS_50 = "50";
	public static final String CCVS_60 = "60";
	public static final String CCVS_70 = "70";

	/**
	 * 
	 * Method Name: legalActionsOutcomeSaveMultiple Method Description:This is
	 * the save service for the Legal Action/Outcome window for multiple stages
	 * and person selected. Tuxedo: CSUB39S
	 * 
	 * @param legalActionSaveInDtos
	 * @return List<LegalActionSaveOutDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<LegalActionSaveOutDto> legalActionsOutcomeSaveMultiple(
			List<LegalActionSaveInDto> legalActionSaveInDtos) {
		log.debug("Entering method legalActionsOutcomeSaveMultiple in LegalActionSaveServiceImpl");
		List<LegalActionSaveOutDto> actionSaveOutDtos = new ArrayList<LegalActionSaveOutDto>();

		LegalActionSaveReq legalActionSaveReq = new LegalActionSaveReq();

		for (LegalActionSaveInDto legalActionSaveInDto : legalActionSaveInDtos) {
			legalActionSaveReq.setLegalActionSaveDto(legalActionSaveInDto);
			LegalActionSaveOutDto actionSaveOutDto = legalActionsOutcomeSave(legalActionSaveReq);
			actionSaveOutDtos.add(actionSaveOutDto);
		}

		log.debug("Exiting method legalActionsOutcomeSaveMultiple in LegalActionSaveServiceImpl");
		return actionSaveOutDtos;
	}

	/**
	 * 
	 * Method Name: legalActionsOutcomeSave Method Description:This is the save
	 * service for the Legal Action/Outcome window.
	 * 
	 * @param actionSaveInDto
	 * @return LegalActionSaveOutDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public LegalActionSaveOutDto legalActionsOutcomeSave(LegalActionSaveReq legalActionSaveReq) {

		LegalActionSaveOutDto legalActionSaveOutDto = new LegalActionSaveOutDto();

		String cdOutcomeSub = NULL;
		String time = NULL;

		// ADS -Use case 2.6.2.4 - Outcome subtype and time
		if (null != legalActionSaveReq.getScheduledTime())
			time = legalActionSaveReq.getScheduledTime();
		if (null != legalActionSaveReq.getCdLegalActOutSub())
			cdOutcomeSub = legalActionSaveReq.getCdLegalActOutSub();

		LegalActionSaveInDto actionSaveInDto = new LegalActionSaveInDto();
		actionSaveInDto = legalActionSaveReq.getLegalActionSaveDto();

		Long ulIdEvent = ServiceConstants.Zero_Value;
		Date dtScheduleDate = legalActionSaveReq.getLegalActionSaveDto().getLegalActionsDto()
				.getDtScheduledCourtDate();
		String actionType = legalActionSaveReq.getLegalActionSaveDto().getLegalActionsDto().getSzCdLegalActAction();
		String subType = legalActionSaveReq.getLegalActionSaveDto().getLegalActionsDto().getDecodeSubType();
		String subTypeCode = legalActionSaveReq.getLegalActionSaveDto().getLegalActionsDto().getSzCdLegalActActnSubtype();
		
		try {
			checkStageEventStatus(actionSaveInDto);
			if (ServiceConstants.REQ_FUNC_CD_ADD.equals(actionSaveInDto.getServiceInputDto().getCreqFuncCd())
					|| ServiceConstants.REQ_FUNC_CD_UPDATE
							.equals(actionSaveInDto.getServiceInputDto().getCreqFuncCd())) {
				try {
					ulIdEvent = postEvent(actionSaveInDto, legalActionSaveOutDto);
				} catch (ServiceLayerException e) {
					ServiceLayerException serviceLayerException = new ServiceLayerException(
							ServiceConstants.SQL_NOT_FOUND);
					serviceLayerException.initCause(e);
					throw serviceLayerException;
				}

				if (ServiceConstants.CEVTSTAT_COMP
						.equals(actionSaveInDto.getSynchronizationServiceDto().getCdEventStatus())
						&& ServiceConstants.REQ_FUNC_CD_UPDATE
								.equals(actionSaveInDto.getServiceInputDto().getCreqFuncCd())
						&& !isCVSOrderActnAndSubActn(actionSaveInDto)) {
					completeTodo(ulIdEvent);
				}

				LegalAction res = legalActionSave(actionSaveInDto, ulIdEvent, cdOutcomeSub, time);
				legalActionSaveOutDto.setTsLastUpdate(res.getDtLastUpdate());
				legalActionSaveOutDto.setIdLastUpdatedPerson(res.getIdLastUpdatePerson());

				FetchToDoDto fetchToDoDto = fetchTodoDefInfo(ServiceConstants.CD_TODO_INFO_FOR_LEG_ACTION_TASK);

				// Common Function to create/update related ToDo for: csub40u
				toDoCommonFunction(actionSaveInDto, legalActionSaveOutDto, ulIdEvent, fetchToDoDto, time);
				

				/*
				 * If CPS INV, Orders for Investigation, Child Safety Alert List is entered as a
				 * legal action, check the special handling indicator.
				 */
				if ("CINV".equals(actionSaveInDto.getLegalActionsDto().getSzCdLegalActAction())
						&& "245".equals(actionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype())
						&& "010".equals(actionSaveInDto.getLegalActionsDto().getSzCdLegalActOutcome())) {

					StageDto stageDto = stageDao
							.getStageById(actionSaveInDto.getSynchronizationServiceDto().getIdStage());

					if (!ObjectUtils.isEmpty(stageDto.getIdCase()) && stageDto.getIdCase() > 0l) {
						caseDao.updateChildSafetyList(stageDto.getIdCase(), ServiceConstants.Y);

					}
				}
				//Alert Creation functionality ADS
				if(!ObjectUtils.isEmpty(dtScheduleDate) && CLEGCPS_CCVS.equals(actionType) &&( CCVS_10.equals(subTypeCode)
						|| CCVS_20.equals(subTypeCode)
						|| CCVS_30.equals(subTypeCode)
						||CCVS_40.equals(subTypeCode)
						||CCVS_50.equals(subTypeCode))) {
					LocalDate currentDate=LocalDate.now() ;
					LocalDate dtScheduleMinus10=dtScheduleDate.toInstant()
						      .atZone(ZoneId.systemDefault())
						      .toLocalDate();
					boolean indCreateAlert=false;
					long diff= DAYS.between(currentDate , dtScheduleMinus10);
					switch(subTypeCode) {
					case "20":
						if(diff>=30) {
							indCreateAlert=true;
							break;
						}
					case "30":
						if(diff>=30) {
							indCreateAlert=true;
							break;
						}
					case "40":
						if(diff>=45) {
							indCreateAlert=true;
							break;
						}
					case "50":
						if(diff>=30) {
							indCreateAlert=true;
							break;
						}
					case "10":
						if(diff>=30) {
							indCreateAlert=true;
							break;
						}
					}
				if(indCreateAlert) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
					String formattedScheduleDateString = dtScheduleMinus10.format(formatter);
					dtScheduleMinus10=dtScheduleMinus10.minusDays(10);
					StringBuffer stringBuffer=new StringBuffer();
					stringBuffer.append("Deliver Notice for " );
					stringBuffer.append(subType);				
					String formattedScheduleMinus10String = dtScheduleMinus10.format(formatter);
									stringBuffer.append(" scheduled on "+formattedScheduleDateString+" before "+" "+formattedScheduleMinus10String);
					
					ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
					TodoDto todoDto=new TodoDto();
					todoDto.setCdTodoType(ServiceConstants.TODO_ACTIONS_ALERT);
					todoDto.setIdTodoPersCreator(0l);
					todoDto.setDtTodoCreated(new Date());
					todoDto.setDtTodoCompleted(new Date());
					todoDto.setDtTodoDue(new Date());
					todoDto.setDtTodoTaskDue(new Date());
					todoDto.setTodoDesc(ServiceConstants.EMPTY_STRING);
					todoDto.setTodoLongDesc(ServiceConstants.EMPTY_STRING);
					todoDto.setIdTodoPersAssigned(personListService.getPrimaryCaseworkerForStage(actionSaveInDto.getSynchronizationServiceDto().getIdStage()).getUlIdPerson());
					todoDto.setTodoDesc(stringBuffer.toString());
					todoDto.setIdTodoStage(actionSaveInDto.getSynchronizationServiceDto().getIdStage());
					todoDto.setIdTodoCase(legalActionSaveReq.getIdCase());
					todoDto.setCdTodoTask(actionSaveInDto.getSynchronizationServiceDto().getCdTask());
					todoDto.setIdTodoPersCreator(legalActionSaveReq.getSysIdTodoCfPersCrea());
					todoDto.setTodoLongDesc(stringBuffer.toString());
					TodoDto savedToDo = toDoDao.todoAUD(todoDto, serviceReqHeaderDto);
				}
				}
			} // End If - Add or Update
			else {
				// Call delete legal Action
				legalActionSave(actionSaveInDto, ulIdEvent, cdOutcomeSub, time);
				StageDto stageDto = stageDao.getStageById(actionSaveInDto.getSynchronizationServiceDto().getIdStage());

				if (!ObjectUtils.isEmpty(stageDto.getIdCase()) && stageDto.getIdCase() > 0l) {
					caseDao.updateChildSafetyList(stageDto.getIdCase(), ServiceConstants.NULL_STRING);
				}
			} // END Else it is Delete

			// If MPS then call the method to log statistical info
			// into the DB
			if (ServiceConstants.MOBILE_IMPACT) {
				callMPSStatsHelper(actionSaveInDto, ulIdEvent);
			}

			// To send the Calendar Event to the assigned Primary or Secondary
			// Case Worker
			
			if (!ObjectUtils.isEmpty(legalActionSaveOutDto) && !ObjectUtils.isEmpty(dtScheduleDate)
					&& CLEGCPS_CCVS.equals(actionType)
					&& (PERM_REVIEW_HEARING_BEFORE_FINAL_ORDER.equals(subType)
							|| (PERM_REVIEW_HEARING_AFTER_FINAL_ORDER.equals(subType))
							|| STATUS_HEARING.equals(subType))) {

				// fetch and send email
				fetchEmployeeEmail(null,legalActionSaveOutDto.getIdLegalActEvent(), dtScheduleDate,
						legalActionSaveReq.getLegalActionSaveDto().getHostName(), subType,
						legalActionSaveReq.getIdCase(), Boolean.FALSE);
			}

			//artf128727-PD 56260 : R2-Task type 3030 not deleting
			//Program has selected to still create the Task but alter the functionality to not make it display on the To Do list if an
			//Outcome Date was entered at the time the Legal Action was created.
			if(ServiceConstants.REQ_FUNC_CD_ADD
					.equals(actionSaveInDto.getServiceInputDto().getCreqFuncCd())
			&& ulIdEvent!=ServiceConstants.Zero_Value
			&& (!ObjectUtils.isEmpty(actionSaveInDto.getLegalActionsDto().getDtDtLegalActOutcomeDt())) ){
				completeTodo(ulIdEvent);
			}


		} catch (ServiceLayerException e) {
			throw e;
		}
		return legalActionSaveOutDto;
	}

	/**
	 * 
	 * Method Name: checkStageEventStatus Method Description:
	 * 
	 * @param actionSaveInDto
	 */
	private void checkStageEventStatus(LegalActionSaveInDto actionSaveInDto) {
		CheckStageInpDto checkStageInpDto = new CheckStageInpDto();
		ServiceInputDto serviceInputDto = (ServiceInputDto) actionSaveInDto.getServiceInputDto();
		if (actionSaveInDto.getSynchronizationServiceDto().getIdEvent() != ServiceConstants.ZERO_VAL) {
			serviceInputDto.setCreqFuncCd(actionSaveInDto.getServiceInputDto().getCreqFuncCd());
		} else {
			serviceInputDto.setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		}
		checkStageInpDto.setServiceInput(serviceInputDto);
		checkStageInpDto.getServiceInput().setCreqFuncCd(actionSaveInDto.getServiceInputDto().getCreqFuncCd());
		checkStageInpDto.setIdStage((long) actionSaveInDto.getSynchronizationServiceDto().getIdStage());
		checkStageInpDto.setCdTask(actionSaveInDto.getSynchronizationServiceDto().getCdTask());
		try {
			// This returns a boolean, but ServiceException is ALWAYS thrown if
			// it
			// would return false.
			// If we get a ServiceException here, we can't go on; just throw it.
			eventTaskStageService.checkStageEventStatus(checkStageInpDto);
		} catch (ServiceLayerException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}
	}

	/**
	 * 
	 * Method Name: callPostEvent Method Description:
	 * 
	 * @param legalActionSaveInDto
	 * @param legalActionSaveOutDto
	 * @return Long
	 */
	private Long postEvent(LegalActionSaveInDto legalActionSaveInDto, LegalActionSaveOutDto legalActionSaveOutDto) {
		PostDto postDto = new PostDto();
		Long ulIdEvent = ServiceConstants.Zero_Value;
		SynchronizationServiceDto postEventInput = new SynchronizationServiceDto();
		postDto.setArchInputStructDto((ServiceInputDto) legalActionSaveInDto.getServiceInputDto());
		postEventInput.setIdEvent(legalActionSaveInDto.getSynchronizationServiceDto().getIdEvent());
		postEventInput.setIdStage(legalActionSaveInDto.getSynchronizationServiceDto().getIdStage());
		postEventInput.setIdPerson(legalActionSaveInDto.getLegalActionsDto().getUlPersWkr());
		postEventInput.setCdTask(legalActionSaveInDto.getSynchronizationServiceDto().getCdTask());
		postEventInput.setCdEventType(legalActionSaveInDto.getSynchronizationServiceDto().getCdEventType());
		postEventInput.setDtEventOccurred(legalActionSaveInDto.getSynchronizationServiceDto().getDtEventOccurred());
		if(postEventInput.getDtEventOccurred() == null) {
			postEventInput.setDtEventOccurred(new Date());
		}
		postDto.setDtDtEventOccurred(legalActionSaveInDto.getSynchronizationServiceDto().getDtEventOccurred());
		postDto.setDtEventCreated(legalActionSaveInDto.getSynchronizationServiceDto().getDtEventOccurred());
		postEventInput.setEventDescr(legalActionSaveInDto.getSynchronizationServiceDto().getEventDescr());
		postEventInput.setCdEventStatus(legalActionSaveInDto.getSynchronizationServiceDto().getCdEventStatus());
		postEventInput.setDtLastUpdate(legalActionSaveInDto.getSynchronizationServiceDto().getDtLastUpdate());
		// Copy Event Person Link information
		DomicileDeprivationChildToEventDto domicileDeprivationChildToEventDto = new DomicileDeprivationChildToEventDto();
		List<APSRoraRowDto> apsRoraRowDtoList = new ArrayList<APSRoraRowDto>();
		domicileDeprivationChildToEventDto.setaPSRoraList(apsRoraRowDtoList);
		APSRoraRowDto aPSRoraRowDto = new APSRoraRowDto();
		aPSRoraRowDto.setIdPerson(legalActionSaveInDto.getSynchronizationServiceDto()
				.getDomicileDeprivationChildToEventDto().getApsRoraRowDto().getIdPerson());
		aPSRoraRowDto.setCdScrDataAction(legalActionSaveInDto.getSynchronizationServiceDto()
				.getDomicileDeprivationChildToEventDto().getApsRoraRowDto().getCdScrDataAction());
		addROWCCMN01UIG01(domicileDeprivationChildToEventDto, aPSRoraRowDto);
		postEventInput.setDomicileDeprivationChildToEventDto(domicileDeprivationChildToEventDto);
		if (legalActionSaveInDto.getSynchronizationServiceDto().getIdEvent() == ServiceConstants.ZERO_VAL) {
			postDto.getArchInputStructDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		} else {
			postDto.getArchInputStructDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		}
		postDto.setLiRowccmn01uig01(apsRoraRowDtoList);
		postDto.setRowCcmn01UiG00(postEventInput);
		postDto.setIdPerson(legalActionSaveInDto.getLegalActionsDto().getUlPersWkr());

		try {
			PostOutputDto postOutputDto = postEventService.PostEvent(postDto);
			ulIdEvent = postOutputDto.getIdEvent();
			legalActionSaveOutDto.setIdLegalActEvent(postOutputDto.getIdEvent());
			// legalActionSaveOutDto.setTsLastUpdate(DateUtils.stringDate(postOutputDto.getTsLastUpdate()));
		} catch (ServiceLayerException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}

		return ulIdEvent;
	}

	private void addROWCCMN01UIG01(DomicileDeprivationChildToEventDto domicileDeprivationChildToEventDto,
			APSRoraRowDto apsRoraRowDto) {
		if (domicileDeprivationChildToEventDto.getaPSRoraList().size() >= 30) {
			throw new IndexOutOfBoundsException();
		}
		domicileDeprivationChildToEventDto.getaPSRoraList().add(apsRoraRowDto);

	}

	/**
	 * 
	 * Method Name: completeTodo Method Description:DAM: CINV43D
	 * 
	 * @param idEvent
	 */
	private void completeTodo(Long idEvent) {
		UpdateToDoDto updateToDoDto = new UpdateToDoDto();

		updateToDoDto.setIdEvent(idEvent);
		updateToDoDao.completeTodo(updateToDoDto);

	}

	/**
	 * 
	 * Method Name: legalActionSave Method Description: DAM CAUD03D
	 * 
	 * @param legalActionSaveInDto
	 * @param idEvent
	 */
	private LegalAction legalActionSave(LegalActionSaveInDto legalActionSaveInDto, Long idEvent, String outcomeSub,
			String time) {
		LegalActionsModificationDto legalActionsModificationDto = new LegalActionsModificationDto();
		legalActionsModificationDto.setServiceInputDto((ServiceInputDto) legalActionSaveInDto.getServiceInputDto());
		if (ObjectUtils.isEmpty(legalActionSaveInDto.getLegalActionsDto().getUlIdLegalActEvent())) {
			legalActionsModificationDto.setIdLegalActEvent(idEvent);
			legalActionsModificationDto.setTsLastUpdate(
					(!TypeConvUtil.isNullOrEmpty(legalActionSaveInDto.getLegalActionsDto().getTsLastUpdate()))
							? legalActionSaveInDto.getLegalActionsDto().getTsLastUpdate() : new Date());
		} else {
			legalActionsModificationDto
					.setIdLegalActEvent(legalActionSaveInDto.getLegalActionsDto().getUlIdLegalActEvent());
			legalActionsModificationDto.setTsLastUpdate(legalActionSaveInDto.getLegalActionsDto().getTsLastUpdate());
		}
		// If not Delete function then populate rest.
		if (!ServiceConstants.REQ_FUNC_CD_DELETE.equals(legalActionSaveInDto.getServiceInputDto().getCreqFuncCd())) {
			legalActionsModificationDto.setIdPerson(legalActionSaveInDto.getLegalActionsDto().getUlIdPerson());
			legalActionsModificationDto
					.setCdLegalActAction(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActAction());
			legalActionsModificationDto
					.setCdLegalActActnSubtype(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype());
			legalActionsModificationDto
					.setCdLegalActOutcome(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActOutcome());
			if (!ObjectUtils.isEmpty(legalActionSaveInDto.getLegalActionsDto().getDtDtLegalActDateFiled())) {
				legalActionsModificationDto
						.setDtLegalActDateFiled(legalActionSaveInDto.getLegalActionsDto().getDtDtLegalActDateFiled());
			}
			if (!ObjectUtils.isEmpty(legalActionSaveInDto.getLegalActionsDto().getDtDtLegalActOutcomeDt())) {
				legalActionsModificationDto
						.setDtLegalActOutcomeDate(legalActionSaveInDto.getLegalActionsDto().getDtDtLegalActOutcomeDt());
			}
			legalActionsModificationDto
					.setCIndLegalActActionTkn(legalActionSaveInDto.getLegalActionsDto().getcIndLegalActActionTkn());
			legalActionsModificationDto
					.setCIndLegalActDocsNCase(legalActionSaveInDto.getLegalActionsDto().getcIndLegalActDocsNCase());
			legalActionsModificationDto
					.setTxtLegalActComment(legalActionSaveInDto.getLegalActionsDto().getSzTxtLegalActComment());
			legalActionsModificationDto
					.setCIndFDTCGraduated(legalActionSaveInDto.getLegalActionsDto().getcIndFDTCGraduated());
			legalActionsModificationDto
					.setCdFDTCEndReason(legalActionSaveInDto.getLegalActionsDto().getSzCdFDTCEndReason());
			legalActionsModificationDto.setIdLastUpdatePerson(legalActionSaveInDto.getLegalActionsDto().getUlPersWkr());
			legalActionsModificationDto.setCdQRTPCourtStatus(legalActionSaveInDto.getLegalActionsDto().getCdQRTPCourtStatus());
		}

		legalActionsModificationDto.setScheduledTime(time);
		legalActionsModificationDto.setCdLegalActOutSub(outcomeSub);
		return leglActnModificationDao.insertOrUpdateLegalAction(legalActionsModificationDto);

	}

	/**
	 * 
	 * Method Name: callToDoCommonFunction Method Description:
	 * 
	 * @param legalActionSaveInDto
	 * @param legalActionSaveOutDto
	 * @param idEvent
	 * @param fetchToDoDto
	 */
	@SuppressWarnings("unchecked")
	private void toDoCommonFunction(LegalActionSaveInDto legalActionSaveInDto,
			LegalActionSaveOutDto legalActionSaveOutDto, Long idEvent, FetchToDoDto fetchToDoDto, String time) {
		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
		todoCreateInDto.setServiceInputDto((ServiceInputDto) legalActionSaveInDto.getServiceInputDto());
		todoCreateInDto.setMergeSplitToDoDto(new MergeSplitToDoDto());

		Long nbrOfCVSOrderTodos;
		// If stage is Not AOC
		if (!ServiceConstants.CSTAGES_AOC.equals(legalActionSaveInDto.getCdStage())) {
			if (!isCVSOrderActnAndSubActn(legalActionSaveInDto)) {
				if (ServiceConstants.TODO_HEARING
						.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActAction())) {
					if (ServiceConstants.TODO_23_SUBTYPE
							.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype())) {
						todoCreateInDto.getMergeSplitToDoDto().setCdTodoCf(ServiceConstants.TODO_INFO_23_CODE);
					} else if (ServiceConstants.TODO_25_1_SUBTYPE
							.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype())
							|| ServiceConstants.TODO_25_2_SUBTYPE
									.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype())) {
						todoCreateInDto.getMergeSplitToDoDto().setCdTodoCf(ServiceConstants.TODO_INFO_25_CODE);
					}
				} else if (ServiceConstants.TODO_GUARDIAN
						.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActAction())) {
					if (ServiceConstants.TODO_12_SUBTYPE
							.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype())) {
						todoCreateInDto.getMergeSplitToDoDto().setCdTodoCf(ServiceConstants.TODO_INFO_12_CODE);
					} else if (ServiceConstants.TODO_13_SUBTYPE
							.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype())) {
						todoCreateInDto.getMergeSplitToDoDto().setCdTodoCf(ServiceConstants.TODO_INFO_13_CODE);
					}
				}
				if (ServiceConstants.TODO_GUARDIAN
						.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActAction())
						&& ServiceConstants.TODO_13_SUBTYPE
								.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype())) {
					// If (CurrentDate < WorkingDate)
					// set dtSysDtTodoCfDueFrom to WorkingDate
					// else (CurrentDate >= WorkingDate) set
					// set dtSysDtTodoCfDueFrom to WorkingDate + 1 year

					Calendar myCal = Calendar.getInstance();
					myCal.set(Calendar.YEAR, myCal.get(Calendar.YEAR));
					myCal.set(Calendar.MONTH, ServiceConstants.TWO_INT);
					myCal.set(Calendar.DAY_OF_MONTH, ServiceConstants.usPageNbr_IND);
					Date workingDate = myCal.getTime();

					if (DateUtils.isBefore(new Date(), workingDate)
							&& !ServiceConstants.TODO_12_SUBTYPE
									.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype())
							&& !ServiceConstants.TODO_13_SUBTYPE
									.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype())) {
						todoCreateInDto.getMergeSplitToDoDto().setDtTodoCfDueFrom(workingDate);
					} else {
						// Add One year to workingDate
						todoCreateInDto.getMergeSplitToDoDto()
								.setDtTodoCfDueFrom(DateUtils.addToDate(workingDate, 1, 0, 0));
					}
				} else {
					todoCreateInDto.getMergeSplitToDoDto()
							.setDtTodoCfDueFrom(legalActionSaveInDto.getLegalActionsDto().getDtDtLegalActOutcomeDt());
				}
			}
			// End if not isCVSOrderActnAndSubActn( csub39si )
		} else // End - If stage is Not AOC
		{
			// To Be Implemented if the Stage is AOC
		}
		todoCreateInDto.getMergeSplitToDoDto()
				.setIdTodoCfPersCrea((long) legalActionSaveInDto.getSysIdTodoCfPersCrea());
		todoCreateInDto.getMergeSplitToDoDto()
				.setIdTodoCfStage(legalActionSaveInDto.getSynchronizationServiceDto().getIdStage());
		todoCreateInDto.getMergeSplitToDoDto()
				.setCdTask(legalActionSaveInDto.getSynchronizationServiceDto().getCdTask());
		// Fetch the Todos for this event if it exists.
		//Added the code for warranty defect 11779
		Long todoIdEvent =null;
		if (!ObjectUtils.isEmpty(idEvent) && !idEvent.equals(ServiceConstants.ZERO)) {
			todoIdEvent = idEvent;
		} else {
			todoIdEvent = !ObjectUtils.isEmpty(legalActionSaveInDto.getLegalActionsDto().getUlIdLegalActEvent())
					? legalActionSaveInDto.getLegalActionsDto().getUlIdLegalActEvent() : null;
		}
		List<FetchToDoOutDto> liinv29soDto = toDoEventDao.fetchToDoListForEvent(todoIdEvent);
		SearchArrayToDoDto searchArrayToDoDto = new SearchArrayToDoDto();
		List<SearchTodoDto> searchTodoDtos = new ArrayList<SearchTodoDto>();// searchArrayToDoDto.getRowCCMN42DOList();
		SearchTodoDto todoDto = new SearchTodoDto();
		if (!CollectionUtils.isEmpty(liinv29soDto)) {
			todoDto.setIdTodo(liinv29soDto.get(0).getIdTodo());
			//Added the code for warranty defect 11779
			if (liinv29soDto.size() > ServiceConstants.One) {
				for (FetchToDoOutDto fetchToDoOutDto : liinv29soDto) {
					todoDto.setIdTodo(fetchToDoOutDto.getIdTodo());
					if (!DateUtils.isNull(legalActionSaveInDto.getLegalActionsDto().getDtScheduledCourtDate())) {
						try {
							todoDao.updateToDoTaskDue(fetchToDoOutDto.getIdTodo(), DateUtils.getTimestamp(
									legalActionSaveInDto.getLegalActionsDto().getDtScheduledCourtDate(), time));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		searchTodoDtos.add(todoDto);
		nbrOfCVSOrderTodos = searchArrayToDoDto.getRowQty();
		// For CVS Orders Action Type we need to create/update the corresponding
		// Todo.
		//Defect 13318,CVS Legal /Court hearing tasks assignment
		    if (isCVSOrderActnAndSubActn(legalActionSaveInDto) ||
		ServiceConstants.CCVS.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActAction())) {
			//if (isCVSOrderActnAndSubActn(legalActionSaveInDto)) {
			// Re-factored this as a local variable.
			Long idTodoCfPersAssgnTo = caseUtils.getPrimaryWorkerIdForStage(legalActionSaveInDto.getSynchronizationServiceDto().getIdStage());

			// If the Scheduled court date is not null then we have to update or
			// insert todos for it
			if (!DateUtils.isNull(legalActionSaveInDto.getLegalActionsDto().getDtScheduledCourtDate())) {
				// Fetch the Supervisor Info so that we can create todo for him
				// also
				PersonEmployeeOutDto personEmployeeOutDto = fetchSupervisorInfo(idTodoCfPersAssgnTo);
				todoCreateInDto.getMergeSplitToDoDto().setIdTodoCfPersAssgn(idTodoCfPersAssgnTo.intValue());
				// The UlSysIdTodoCfPersWkr is the primary worker
				// Id. So csub39si.getUlSysIdTodoCfPersAssgn()
				// will be the primary worker Id as it was set in Conversation.
				todoCreateInDto.getMergeSplitToDoDto().setIdTodoCfPersWkr(idTodoCfPersAssgnTo);
				todoCreateInDto.getMergeSplitToDoDto().setCdTodoCf(ServiceConstants.CD_TODO_INFO_FOR_LEG_ACTION_TASK);
				//[artf114519] -  Defect Fix# <Alm#11553> - Incorrect court date is displayed on staff to do list
				boolean isPastDate = false;
				try {
					Date selectedScheduledCourtDate = legalActionSaveInDto.getLegalActionsDto().getDtScheduledCourtDate();
					Date todaySystemDate = new Date();
					int diffInDays = (int) ((selectedScheduledCourtDate.getTime() - todaySystemDate.getTime()) / (1000 * 60 * 60 * 24));
					if (diffInDays >= 10) {
						Date newDateAfterSubtracting10Days = org.apache.commons.lang.time.DateUtils.addDays(selectedScheduledCourtDate, -10);
						todoCreateInDto.getMergeSplitToDoDto().setDtTodoCfDueFrom(DateUtils
								.getTimestamp(newDateAfterSubtracting10Days, time));
						//Defect 11553: storing the selected court schedule date in case of subtracting the selectedcourt date with 10 days, this will happen
						//only when we select court schedule date in 10 days advance from the current date
						//[artf176391] - Defect Fix# 16836- Legal Action Scheduled court time
						todoCreateInDto.setSelectedScheduledCourtDate(DateUtils
								.getTimestamp(selectedScheduledCourtDate, time));
						isPastDate=true;
					}
					else{
						todoCreateInDto.getMergeSplitToDoDto().setDtTodoCfDueFrom(DateUtils
								.getTimestamp(selectedScheduledCourtDate, time));
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				todoCreateInDto.getMergeSplitToDoDto().setIdTodoCfEvent(idEvent);
				todoCreateInDto.getMergeSplitToDoDto().setIdTodoCfPersCrea(ServiceConstants.ZERO_VAL);
				if (ServiceConstants.REQ_FUNC_CD_ADD
						.equals(legalActionSaveInDto.getServiceInputDto().getCreqFuncCd())) {
					for (SearchTodoDto searchTodoDto : searchTodoDtos) {
						if (searchTodoDto.getIdTodoInfo() == ServiceConstants.Zero_Value) {
							completeTodoByIdTodo(searchTodoDto.getIdTodo());
						}
					}
					// The todoCommon function will Create todo task for the
					// assigned Person.
					todoCreateInDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
					setTodoDescMessage(todoCreateInDto, legalActionSaveInDto,isPastDate);
					todoCommonFunctionHelper(todoCreateInDto);
					// the UlSysIdTodoCfPersAssgn to 0 to create Todo for the
					// supervisor.
					if (idTodoCfPersAssgnTo != ServiceConstants.Zero_Value
							&& personEmployeeOutDto.getIdPerson() != ServiceConstants.Zero_Value
							&& personEmployeeOutDto.getIdPerson() != idTodoCfPersAssgnTo) {
						todoCreateInDto.getMergeSplitToDoDto().setIdTodoCfPersAssgn(ServiceConstants.Zero);
						todoCommonFunctionHelper(todoCreateInDto);
					}
				} else if (ServiceConstants.REQ_FUNC_CD_UPDATE
						.equals(legalActionSaveInDto.getServiceInputDto().getCreqFuncCd())) {
					// In Update state, if there are any todos then update them.
					// if not, then insert.
					if (null != nbrOfCVSOrderTodos && nbrOfCVSOrderTodos == ServiceConstants.Zero_Value) {
						// Insert the todo for the assigned Person
						todoCreateInDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
						setTodoDescMessage(todoCreateInDto, legalActionSaveInDto,isPastDate);
						todoCommonFunctionHelper(todoCreateInDto);
						// if the person and supervisor is same then don't
						// create a todo again for the supervisor.
						if (idTodoCfPersAssgnTo != ServiceConstants.Zero_Value
								&& personEmployeeOutDto.getIdPerson() != ServiceConstants.Zero_Value
								&& personEmployeeOutDto.getIdPerson() != idTodoCfPersAssgnTo) {
							// Since the ToCommon Bean Module works this way so
							// have to set
							// the UlSysIdTodoCfPersAssgn to 0 to create Todo
							// for the supervisor.
							todoCreateInDto.getMergeSplitToDoDto().setIdTodoCfPersAssgn(0);
							todoCommonFunctionHelper(todoCreateInDto);
						}
					} else {
						todoCreateInDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
						setTodoDescMessage(todoCreateInDto, legalActionSaveInDto,isPastDate);
						// Update the todos.
						for (SearchTodoDto searchTodoDto : searchTodoDtos) {
							if (searchTodoDto.getIdTodoInfo() == fetchToDoDto.getIdTodoInfo()) {
								todoCreateInDto.getMergeSplitToDoDto()
										.setIdTodoCfPersAssgn(searchTodoDto.getIdTodoPersAssigned().intValue());
								todoCreateInDto.getMergeSplitToDoDto().setIdTodo(searchTodoDto.getIdTodo());
								todoCreateInDto.getMergeSplitToDoDto().setDtLastUpdate(searchTodoDto.getTsLastUpdate());
								// and the dtToDocompleted is null then set sys
								// date.
								if (DateUtils.isNull(searchTodoDto.getDtTodoCompleted())
										&& ServiceConstants.CEVTSTAT_COMP.equals(legalActionSaveInDto
												.getSynchronizationServiceDto().getCdEventStatus())) {
									todoCreateInDto.getMergeSplitToDoDto().setDtTodoCompleted(new Date());
								}
								todoCommonFunctionHelper(todoCreateInDto);
							}
						}
					}
				}
			} else {
				if (ServiceConstants.REQ_FUNC_CD_ADD
						.equals(legalActionSaveInDto.getServiceInputDto().getCreqFuncCd())) {
					for (SearchTodoDto searchTodoDto : searchTodoDtos) {
						if (searchTodoDto.getIdTodoInfo() == ServiceConstants.Zero_Value) {
							completeTodoByIdTodo(searchTodoDto.getIdTodo());
						}
					}
				} else // the todo exist them delete.
				if (ServiceConstants.REQ_FUNC_CD_UPDATE
						.equals(legalActionSaveInDto.getServiceInputDto().getCreqFuncCd()) && null != nbrOfCVSOrderTodos
						&& nbrOfCVSOrderTodos > ServiceConstants.Zero_Value) {
					for (SearchTodoDto searchTodoDto : searchTodoDtos) {
						if (searchTodoDto.getIdTodoInfo() == fetchToDoDto.getIdTodoInfo()) {
							deleteTodo(legalActionSaveInDto, searchTodoDto.getIdTodo());
						}
					}
				}
			}
		} else // END of isCVSOrderActnAndSubActn( csub39si )
		{
			// if yes, then put date todo completed as todays date for that todo
			if (ServiceConstants.REQ_FUNC_CD_ADD.equals(legalActionSaveInDto.getServiceInputDto().getCreqFuncCd())) {
				for (SearchTodoDto searchTodoDto : searchTodoDtos) {
					if (!ObjectUtils.isEmpty(searchTodoDto.getIdTodoInfo())) {
						completeTodoByIdTodo(searchTodoDto.getIdTodo());
					}
				}
			}
			// Fetch the Todos for this event if it exists.
			if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(legalActionSaveInDto.getServiceInputDto().getCreqFuncCd())
					&& null != nbrOfCVSOrderTodos && nbrOfCVSOrderTodos > ServiceConstants.Zero_Value) {
				for (SearchTodoDto searchTodoDto : searchTodoDtos) {
					if (searchTodoDto.getIdTodoInfo() == fetchToDoDto.getIdTodoInfo()) {
						deleteTodo(legalActionSaveInDto, searchTodoDto.getIdTodo());
					}
				}
			}
			// Call the todo helper to create new Todos.
			if (legalActionSaveInDto.getSysIdTodoCfPersCrea() != ServiceConstants.Zero_Value) {
				todoCommonFunctionHelper(todoCreateInDto);
			}
		}
		// End of isCVSOrderActnAndSubActn( csub39si ) is false.
	}

	/**
	 * 
	 * Method Name: todoCommonFunctionHelper Method Description:
	 * 
	 * @param todoCreateInDto
	 */
	private void todoCommonFunctionHelper(TodoCreateInDto todoCreateInDto) {
		try {
			// This returns a boolean, but ServiceException is ALWAYS thrown if
			// it would return false.
			// If we get a ServiceException here, we can't go on; just throw it.
			todoCreateService.TodoCommonFunction(todoCreateInDto);
		} catch (ServiceLayerException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}

	}

	/**
	 * 
	 * Method Name: completeTodoByIdTodo Method Description: complete todo by
	 * todo id
	 * 
	 * @param ulIdTodo
	 */
	private void completeTodoByIdTodo(Long ulIdTodo) {
		EventStageDiDto eventStageDiDto = new EventStageDiDto();
		eventStageDiDto.setLdIdTodo(ulIdTodo);
		eventStageDiDto.setDtDtTodoCompleted(new Date());
		todoCreateDao.completeTodo(eventStageDiDto);

	}

	/**
	 * 
	 * Method Name: fetchPersonFullName Method Description:
	 * 
	 * @param legalActionSaveInDto
	 * @return String
	 */
	private String fetchPersonFullName(LegalActionSaveInDto legalActionSaveInDto) {
		PersonDetailsdiDto personDetailsdiDto = new PersonDetailsdiDto();
		String nmPersonFull = null;
		personDetailsdiDto.setServiceInputDto(legalActionSaveInDto.getServiceInputDto());
		personDetailsdiDto.setIdPerson(legalActionSaveInDto.getLegalActionsDto().getUlIdPerson());
		PersonDetailsdoDto personDetailsdoDto = personDetailsDao.getPersonInformation(personDetailsdiDto);
		if (!TypeConvUtil.isNullOrEmpty(personDetailsdoDto.getNmPersonFull())) {
			nmPersonFull = personDetailsdoDto.getNmPersonFull();
		}
		return nmPersonFull;
	}

	/**
	 * 
	 * Method Name: isCVSOrderActnAndSubActn Method Description:
	 * 
	 * @param legalActionSaveInDto
	 * @return Boolean
	 */
	private Boolean isCVSOrderActnAndSubActn(LegalActionSaveInDto legalActionSaveInDto) {
		boolean result=false;
		if ((ServiceConstants.CLEGCPS_CCOR.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActAction()))
				&& ((ServiceConstants.CCOR_120
						.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype()))
						|| (ServiceConstants.CCOR_130
								.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype()))
						|| (ServiceConstants.CCOR_140
								.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype()))
						|| (ServiceConstants.CCOR_320
								.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype()))
						|| (ServiceConstants.CCOR_325
								.equals(legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype())))) {
			result=true;
		}
		return result;
	}

	/**
	 * 
	 * Method Name: deleteTodo Method Description:
	 * 
	 * @param legalActionSaveInDto
	 * @param idTodo
	 */
	private void deleteTodo(LegalActionSaveInDto legalActionSaveInDto, Long idTodo) {
		EventStageDiDto eventStageDiDto = new EventStageDiDto();
		eventStageDiDto.setServiceInputDto(legalActionSaveInDto.getServiceInputDto());
		eventStageDiDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
		eventStageDiDto.setLdIdTodo(idTodo);
		todoCreateDao.audToDo(eventStageDiDto);
	}

	/**
	 * 
	 * Method Name: fetchTodoDefInfo Method Description:fetch todo info
	 * 
	 * @param sysCdTodoCf
	 * @return FetchToDoDto
	 */
	private FetchToDoDto fetchTodoDefInfo(String sysCdTodoCf) {
		FetchToDodiDto fetchToDodiDto = new FetchToDodiDto();
		fetchToDodiDto.setCdTodoInfo(sysCdTodoCf);
		List<FetchToDoDto> fetchToDoDtos = fetchToDoDao.getTodoInfo(fetchToDodiDto);
		return fetchToDoDtos.get(ServiceConstants.Zero);

	}

	/**
	 * 
	 * Method Name: fetchSupervisorInfo Method Description:
	 * 
	 * @param idTodoPersWorker
	 * @return PersonEmployeeOutDto
	 */
	private PersonEmployeeOutDto fetchSupervisorInfo(Long idTodoPersWorker) {
		PersonEmployeeInDto personEmployeeInDto = new PersonEmployeeInDto();
		PersonEmployeeOutDto personEmployeeOutDto = new PersonEmployeeOutDto();
		personEmployeeInDto.setIdPerson(idTodoPersWorker);
		List<PersonEmployeeOutDto> personEmployeeOutListDto = personInfoDao.getSupervisor(personEmployeeInDto);
		if (!CollectionUtils.isEmpty(personEmployeeOutListDto)) {
			personEmployeeOutDto = personEmployeeOutListDto.get(ServiceConstants.Zero);
		}
		return personEmployeeOutDto;
	}

	/**
	 * 
	 * Method Name: setTodoDescMessage Method Description:
	 * 
	 * @param todoCreateInDto
	 * @param legalActionSaveInDto
	 */
	private void setTodoDescMessage(TodoCreateInDto todoCreateInDto, LegalActionSaveInDto legalActionSaveInDto, boolean isAlreadyDateMinus10) {
		FetchToDodiDto fetchToDodiDto = new FetchToDodiDto();
		fetchToDodiDto.setServiceInputDto(todoCreateInDto.getServiceInputDto());
		fetchToDodiDto.setCdTodoInfo(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf());
		List<FetchToDoDto> fetchToDoDtos = fetchToDoDao.getTodoInfo(fetchToDodiDto);
		// The date for appending the todo desc is 10 days before the due date
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.toJavaDate(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom()));
		if(!isAlreadyDateMinus10){
			cal.add(Calendar.DAY_OF_MONTH, ServiceConstants.MINUS_TEN);
		}
		Date todoDueFromForTodoDesc = cal.getTime();
		/*
		 * [LA Type] [LA Subtype] Court Report for [PName] is due to all parties
		 * on [XX]
		 */
		String todoDesc = new String();
		todoDesc = replaceStr(fetchToDoDtos.get(ServiceConstants.Zero).getTxtTodoInfoDesc(),
				ServiceConstants.LEGAL_ACTION_TYPE_TODO_DESC_SUBSTRING_TO_REPLACE, lookupDao.simpleDecodeSafe(
						ServiceConstants.CLEGCPS, legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActAction()));
		todoDesc = replaceStr(todoDesc, ServiceConstants.LEGAL_ACTION_SUBTYPE_TODO_DESC_SUBSTRING_TO_REPLACE,
				ObjectUtils.isEmpty(lookupDao.simpleDecodeSafe(ServiceConstants.CCVS,
						legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype()))
								? ServiceConstants.EMPTY_STRING
								: lookupDao.simpleDecodeSafe(ServiceConstants.CCVS,
										legalActionSaveInDto.getLegalActionsDto().getSzCdLegalActActnSubtype()));
		todoDesc = replaceStr(todoDesc, ServiceConstants.PRINCIPAL_NAME_TODO_DESC_SUBSTRING_TO_REPLACE,
				fetchPersonFullName(legalActionSaveInDto));
		todoDesc = replaceStr(todoDesc, ServiceConstants.DATE_TODO_DESC_SUBSTRING_TO_REPLACE,
				DateUtils.toString(todoDueFromForTodoDesc, new SimpleDateFormat(DateUtils.SLASH_DATE_MASK)));
		todoCreateInDto.getMergeSplitToDoDto().setTodoCfDesc(todoDesc);
	}

	/**
	 * 
	 * Method Name: replaceStr Method Description:
	 * 
	 * @param sOrig
	 * @param sSubstr
	 * @param sRep
	 * @return String
	 */
	private String replaceStr(String sOrig, String sSubstr, String sRep) {
		String buffer = sOrig.replaceAll(sSubstr, sRep);
		return buffer;
	}

	/**
	 * 
	 * Method Name: callMPSStatsHelper Method Description:
	 * 
	 * @param legalActionSaveInDto
	 * @param idEvent
	 */
	private void callMPSStatsHelper(LegalActionSaveInDto legalActionSaveInDto, Long idEvent) {
		MPSStatsValueDto mpsStatsValueDto = new MPSStatsValueDto();
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(legalActionSaveInDto.getServiceInputDto().getCreqFuncCd())) {
			mpsStatsValueDto.setIdEvent(idEvent);
			mpsStatsValueDto.setCdDmlType(ServiceConstants.REQ_FUNC_CD_ADD);
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE
				.equals(legalActionSaveInDto.getServiceInputDto().getCreqFuncCd())) {
			mpsStatsValueDto.setIdEvent(legalActionSaveInDto.getLegalActionsDto().getUlIdLegalActEvent());
			mpsStatsValueDto.setCdDmlType(ServiceConstants.REQ_FUNC_CD_UPDATE);
		} else // delete
		{
			mpsStatsValueDto.setIdEvent(legalActionSaveInDto.getLegalActionsDto().getUlIdLegalActEvent());
			mpsStatsValueDto.setCdDmlType(ServiceConstants.REQ_FUNC_CD_DELETE);
		}
		mpsStatsValueDto.setIdStage(legalActionSaveInDto.getSynchronizationServiceDto().getIdStage());
		mpsStatsValueDto.setCdReference(ServiceConstants.CMPSSTAT_009);
		MPSStatsUtils mpsStatsUtils = new MPSStatsUtils();
		mpsStatsUtils.logStatsToDB(mpsStatsValueDto);
	}
	/*
	 * private void createTodo(TodoCreateInDto todoCreateInDto){
	 * 
	 * TodoDto todoDto = new TodoDto(); ServiceReqHeaderDto serviceReqHeaderDto
	 * = new ServiceReqHeaderDto(); }
	 */

	/**
	 * 
	 * Method Name: fetchEmployeeEmail Method Description: This Method is used
	 * for fetching the primary and secondary case-worker's employee email
	 * addresses based on the event id
	 * 
	 * @param idEvent
	 * @param dtCourtSchedule
	 * @param hostName
	 * @param cdSubType
	 * @param idCase
	 * @param fromAssign
	 * @return String
	 */

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String fetchEmployeeEmail(AssignSaveGroupReq assignSaveGroupReq,Long idEvent, Date dtCourtSchedule, String hostName, String cdSubType, Long idCase,
			Boolean fromAssign) {
		String message = ServiceConstants.EMPTY_STR;
		Boolean emailSent = Boolean.FALSE;
		// if dtCourtSchedule is future date then send email
		if (!ObjectUtils.isEmpty(dtCourtSchedule) && DateUtils.isAfterToday(dtCourtSchedule)) {
			List<LegalActionRtrvOutDto> idEventList = leglActnModificationDao.fetchLegalActionEventIds(idCase);
			// if idEventList not empty and not from Assign page then do the
			// below logic to set emailSent flag
			if (!CollectionUtils.isEmpty(idEventList) && !fromAssign) {
				// filter out the currently created legal event and form the
				// list
				List<LegalActionRtrvOutDto> legalDtos = idEventList.stream()
						.filter(o -> !o.getIdLegalActEvent().equals(idEvent)).collect(Collectors.toList());
				// iterate and check dtCourtSchedule and cdSubType is present in
				// the new list and set the flag emailSent
				for (LegalActionRtrvOutDto legalActionRtrvOutDto : legalDtos) {
					if (!ObjectUtils.isEmpty(legalActionRtrvOutDto.getCdLegalActActnSubtype())
							&& legalActionRtrvOutDto.getCdLegalActActnSubtype().equals(cdSubType)
							&& !ObjectUtils.isEmpty(legalActionRtrvOutDto.getDtScheduledCourtDate())
							&& legalActionRtrvOutDto.getDtScheduledCourtDate().equals(dtCourtSchedule)) {
						emailSent = Boolean.TRUE;
						break;
					}
				}
			}

			EmployeeMailRes employeeMailRes = new EmployeeMailRes();
			Map<Long, List<EmailDetailsDto>> emailDetailMap = new HashMap<Long, List<EmailDetailsDto>>();
			if (!emailSent && !ObjectUtils.isEmpty(idEvent) && idEvent > 0l) {
				List<Long> idPersonList = new ArrayList<Long>();
				List<EmailDetailsDto> emailDetailsDtoList = null;
				//Warranty Defect#12114 - Issue fixed to avoid dupilcate outlook calender invite - start
				if (!ObjectUtils.isEmpty(assignSaveGroupReq)) {
					//Warranty Defect#12114 - Added a filter to send outlook invite to the selected assginee in assign page
					assignSaveGroupReq.getAssignSaveGroupDto().stream().filter(o -> !o.getCdScrDataAction().equals("DELETE"))
							.forEach(assignSaveGroupDto -> idPersonList.add(assignSaveGroupDto.getIdPerson()));
					emailDetailsDtoList = leglActnModificationDao.getEmailAddress(idPersonList,idEvent);
				} else {
					// Calling the dao method to get the email dto list
					emailDetailsDtoList = leglActnModificationDao.fetchEmployeeEmail(idEvent);
				}
				// Generating a map of the email dto list having key as id event
				emailDetailMap.put(idEvent, emailDetailsDtoList);				
			}
			//End			
			employeeMailRes.setReciepentDetail(emailDetailMap);
			//calling the Send appointment method to generate and send the appointment to the users
			message = sendAppointment(employeeMailRes, dtCourtSchedule, hostName, cdSubType);
		}
		return message;
	}

	/**
	 * 
	 * Method Name: sendAppointment Method Description: This Method is used for
	 * Send The Appointment Invite
	 * 
	 * @param employeeMailRes
	 * @param dtCVSRemoval
	 * @param hostName
	 * @return String
	 */
	public String sendAppointment(EmployeeMailRes employeeMailRes, Date dtScheduleCourt, String hostName,
			String cdSubType) {
		// generating a array of string to store the service account Name and
		// the password
		// which is provided from the JNDI configuration		
		String[] outLookSecurity = null;
		outLookSecurity = JNDIUtil.lookUp(OUTLOOKSECURITY).split(",");
		String outLookService = outLookSecurity[0];
		String outLookLogOnCode = ServiceConstants.EMPTY_STR;
		try {
			// encrypt password string coming from JNDI
			outLookLogOnCode = JNDIUtil.decrypt(outLookSecurity[1]);
		} catch (EncryptionException e) {
			log.fatal(EXCEPTION_STRING_ONE + e.getMessage());
			// catching the exception if the JNDI is not configured and sending the message to web 
			return NO_JNDI;
		}		 
		// below loop is used to generate the multiple appointment DTO to be
		// sent to outlook util
		// to generate and trigger the appointment for multiple users
		List<String> emailAddress = new ArrayList<>();
		//Warranty Defect#12114 - Modifed the logic to iterate the map and set the employee email address from the emaildetailDto list - start
		if (!ObjectUtils.isEmpty(employeeMailRes.getReciepentDetail())) {
			for (Map.Entry<Long, List<EmailDetailsDto>> recipientDetail : employeeMailRes.getReciepentDetail()
					.entrySet()) {
				AppointmentDto appointmentDto = new AppointmentDto();
				appointmentDto.setAppointmentBody("");
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtScheduleCourt);
				String dtScheduleCourtFmted = DateUtils.dateStringInSlashFormat(dtScheduleCourt);
				Date startDate = cal.getTime();
				// below logic is to generate the end time of the day
				cal.setTime(startDate);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MILLISECOND, 59);
				Date endDate = cal.getTime();
				appointmentDto.setDtStartDate(startDate);
				appointmentDto.setDtEndDate(endDate);
				appointmentDto.setIndAllDayEvent(Boolean.TRUE);
				appointmentDto.setRemainderMins(10080);				
				for (EmailDetailsDto emailDetailsDto : recipientDetail.getValue()) {
					StringBuilder subject = new StringBuilder();
					String cdLegalsubType = subject.append(cdSubType).append(ServiceConstants.CONSTANT_SPACE)
							.append(FOR).append(ServiceConstants.CONSTANT_SPACE).append(emailDetailsDto.getStageName())
							.append(ServiceConstants.CONSTANT_SPACE).append(IS_ON)
							.append(ServiceConstants.CONSTANT_SPACE).append(dtScheduleCourtFmted).toString();
					appointmentDto.setAppointmentSubject(cdLegalsubType);
					// below condition is added to prevent the mail
					// triggering to actual user during multiple testing
					// phase
					if (PROD.equals(hostName)) {
						emailAddress.add(emailDetailsDto.getEmailAddress());
					}
					// remove below conditional if test email address needs
					// to be removed
					else {
						String emailProperty = LEGAL_ACTION_EMAIL_CONFIG_BASE + hostName;
						emailAddress.add(EMAIL_CONFIG_BUNDLE.getString(emailProperty));
					}
					appointmentDto.setReceiverEmailAddress(emailAddress);
					
				}
				//End
				// calling the outlook utility method to send the appointment
				// meeting also we are calling the outlookExchange service
				// method to get
				// the the Exchange service passing the service account userName
				// and Password to get the service
				outlookUtil.sendAppointment(appointmentDto,
						outlookUtil.getOutlookExchangeService(outLookService,outLookLogOnCode));
			}
		}
		return SUCCESS;
	}

	/* (non-Javadoc)
	 * @see us.tx.state.dfps.service.legal.service.LegalActionSaveService#checkTMCExists(long, java.lang.String)
	 */
	@Override
	public CommonHelperRes checkTMCExists(Long idStage, String cdLegalStatStatus) {
		CommonHelperRes commonHelperRes=new CommonHelperRes();
		Long idEvent=leglActnModificationDao.checkTMCExists(idStage,cdLegalStatStatus);
		commonHelperRes.setIdEvent(idEvent);
		return commonHelperRes;
	}

}
