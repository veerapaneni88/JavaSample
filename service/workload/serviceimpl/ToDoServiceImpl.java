package us.tx.state.dfps.service.workload.serviceimpl;

import java.text.ParseException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dao.AdminWorkerDao;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateOutDto;
import us.tx.state.dfps.service.admin.service.TodoCreateService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.service.ToDoService;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Implements
 * the methods in TodoBean Sep 6, 2017- 9:13:08 PM © 2017 Texas Department of
 * Family and Protective Services
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 */
@Service
@Transactional
public class ToDoServiceImpl implements ToDoService {

	@Autowired
	TodoDao toDoDao;

	@Autowired
	StageDao stageDao;
	
	@Autowired
	AdminWorkerDao adminWorkerDao;

	@Autowired
	TodoCreateService todoCreateService;

	/**
	 * Method Name: getSupervisorForStage Method Description: This function
	 * returns the Supervisor for the given Stage.
	 * 
	 * @param idStage
	 * @return long @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public long getSupervisorForStage(long idStage) {
		return toDoDao.getSupervisorForStage(idStage);
	}

	/**
	 * Method Name: updateToDo Method Description: This method update TODO
	 * Table.
	 * 
	 * @param todoDto
	 * @return TodoDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TodoDto updateToDo(TodoDto todoDto) {

		toDoDao.updateTodo(todoDto);
		return todoDto;
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long deleteToDoByEvent(Long idEvent) {
		toDoDao.deleteTodoListByEventId(idEvent);
		return idEvent;
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long deleteTodoListByEventIdAndTaskCode(Long idEvent) {
		toDoDao.deleteTodoListByEventIdAndTaskCode(idEvent);
		return idEvent;
	}

  @Override
  public void deleteTodoByIdTodo(Long idTodo) {
    toDoDao.deleteTodo(idTodo);
  }

	/**
	 * Method Name: selectToDosForStage Method Description: This function
	 * retrieves Open Tasks / Alerts of the given Type for the given Stage. If
	 * cdTodoInfoList is null, returns all Todos/Alerts for the Stage.
	 *
	 * @param idStage
	 * @param cdTodoInfoList
	 * @return List<TodoDto>
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<TodoDto> selectToDosForStage(long idStage, List<String> cdTodoInfoList) {
		return toDoDao.selectToDosForStage(idStage, cdTodoInfoList);
	}

	/**
	 * Method Name: updateToDos Method Description: Manual Stage Progression INT
	 * to A-R This function updates Todo Table.
	 * 
	 * @param toDosDto
	 * @return long[]
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public long[] updateToDos(List<TodoDto> toDosDto) {
		return toDoDao.updateToDosDao(toDosDto);
	}

	/**
	 * Method Name: deleteToDosForStage Method Description: This function
	 * deletes Tasks / Alerts of the given Type for the given Stage.
	 * 
	 * @param idStage
	 * @param cdTodoInfoList
	 * @return long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public long deleteToDosForStage(long idStage, List<String> todoInfoList) {

		return toDoDao.deleteTodosForStage(idStage, todoInfoList);

	}

	/**
	 * Method Name: endDateInvAlerts Method Description: This function will be
	 * called by the Approval of Investigation conclusion. It will end date all
	 * the Alerts/Tasks for the workers and supervisors reminding them to
	 * complete Investigation conclusion.
	 * 
	 * @param idInvStage
	 * @return long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public long endDateInvAlerts(long idInvStage) {
		// Retrieve Investigation tasks and Alerts associated with the stage.
		ArrayList<String> cdTodoInfoList = new ArrayList<>();
		cdTodoInfoList.add(ServiceConstants.TODO_INFO_INV_CWALERT);
		cdTodoInfoList.add(ServiceConstants.TODO_INFO_INV30DAY_SUPERVISOR);
		cdTodoInfoList.add(ServiceConstants.TODO_INFO_INV37DAY_SUPERVISOR);
		cdTodoInfoList.add(ServiceConstants.TODO_INFO_INV37DAY_WORKER_TODO);
		cdTodoInfoList.add(ServiceConstants.TODO_INFO_SDM_SA_PRIORITY_1_WORKER);
		cdTodoInfoList.add(ServiceConstants.TODO_INFO_SDM_SA_PRIORITY_2_WORKER);
		cdTodoInfoList.add(ServiceConstants.TODO_INFO_SDM_SA_SUPERVISOR);

		List<TodoDto> todoList = selectToDosForStage(idInvStage, cdTodoInfoList);
		for (TodoDto todoLists : todoList) {
			todoLists.setDtTodoCompleted(new java.util.Date());
			toDoDao.updateTodo(todoLists);
		}
		return todoList.size();
	}

	/**
	 * Method Name: createInvestigationAlerts Method Description: This function
	 * creates 30-Day / 37-Day INV Caseworker Alerts.
	 * 
	 * @param idCase
	 * @param idIntakeStage
	 * @param idLoggedInWorker
	 * @return long
	 * @throws ParseException
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public long createInvestigationAlerts(long idCase, long idIntakeStage, long idLoggedInWorker){

		Event invStgOpenEvt = adminWorkerDao.getInvStageOpenEvent(idCase);
		Stage intakeStageInfo = adminWorkerDao.retrieveStageInfo(idIntakeStage);

		if (invStgOpenEvt != null && invStgOpenEvt.getIdEvent() != 0 && intakeStageInfo != null
				&& intakeStageInfo.getIdStage() != 0) {
			Date intakeStartDate = intakeStageInfo.getDtStageStart();

			// Create 30-Day CaseWorker Alert.
			Date dtDue30DayAlert = DateUtils.addToDate(intakeStartDate, 0, 0, 45);
			String shortDesc = ServiceConstants.SHORT_DESC + DateUtils.stringDt(dtDue30DayAlert);
			createInvAlert(shortDesc, shortDesc, ServiceConstants.TODO_INFO_INV_CWALERT,
					invStgOpenEvt.getPerson().getIdPerson(), 0, invStgOpenEvt.getStage().getIdStage(),
					invStgOpenEvt.getIdEvent(), intakeStartDate);

			String caseworkerInitials = formatUserInitials(
					toDoDao.getPersonFullName(invStgOpenEvt.getPerson().getIdPerson()));

			// Create 30-Day Alert for CPS INV Supervisor.
			long idSupervisor = toDoDao.getSupervisorForStage(invStgOpenEvt.getStage().getIdStage());
			shortDesc = caseworkerInitials + " 30 Day Alert";
			createInvAlert(shortDesc, shortDesc, ServiceConstants.TODO_INFO_INV30DAY_SUPERVISOR, idSupervisor, 0,
					invStgOpenEvt.getStage().getIdStage(), invStgOpenEvt.getIdEvent(), intakeStartDate);

			// Create 37-Day Alert for CPS INV Supervisor.
			shortDesc = caseworkerInitials + " 37 Day Alert";
			createInvAlert(shortDesc, shortDesc, ServiceConstants.TODO_INFO_INV37DAY_SUPERVISOR, idSupervisor, 0,
					invStgOpenEvt.getStage().getIdStage(), invStgOpenEvt.getIdEvent(), intakeStartDate);

			// Create 37-Day TASK for "Caseworker".
			shortDesc = ServiceConstants.SHORT_DESC + DateUtils.stringDt(dtDue30DayAlert);
			createInvAlert(shortDesc, shortDesc, ServiceConstants.TODO_INFO_INV37DAY_WORKER_TODO,
					invStgOpenEvt.getPerson().getIdPerson(), 0, invStgOpenEvt.getStage().getIdStage(),
					invStgOpenEvt.getIdEvent(), intakeStartDate);

			Stage invStage = toDoDao.getStage(invStgOpenEvt.getStage().getIdStage());
			Date invStageStartDate = invStage.getDtStageStart();
			if (toDoDao.isCPSSDMInvSafetyAssmt(invStageStartDate)) {
				shortDesc = "Complete documentation of Initial Safety Assessment.";

				Date dateDueFrom = ServiceConstants.BOOLEAN_TRUE.equals(intakeStageInfo.getIndFormallyScreened())
						? invStageStartDate : toDoDao.getIncomingCallDateForIntake(idIntakeStage);
				String todoInfoType = null;

				if (ServiceConstants.PRIORITY_1.equals(intakeStageInfo.getCdStageInitialPriority())) {
					todoInfoType = ServiceConstants.TODO_INFO_SDM_SA_PRIORITY_1_WORKER;
				} else if (ServiceConstants.PRIORITY_2.equals(intakeStageInfo.getCdStageInitialPriority())) {
					todoInfoType = ServiceConstants.TODO_INFO_SDM_SA_PRIORITY_2_WORKER;
				}

				if (todoInfoType != null) {
					createInvAlert(shortDesc, null, todoInfoType, invStgOpenEvt.getPerson().getIdPerson(), 0,
							invStgOpenEvt.getStage().getIdStage(), invStgOpenEvt.getIdEvent(), dateDueFrom);
				}
			}
		}

		return ServiceConstants.ONE_VAL;
	}

	/**
	 * Method Name: createRCLAlerts Method Description: This function creates
	 * RCL Alert.
	 * artf129782: Licensing Investigation Conclusion
	 * 
	 * @param assignedTo
	 * @param idStage
	 * @param idEvent
	 * @param description
	 * @return long
	 * 
	 */
	public long createRCLAlerts(long idStage, long idEvent, long assignedTo, String description, long idCreatedPerson){
		
	    StageDto stage = stageDao.getStageById(idStage);
		TodoDto alertTodoDto = new TodoDto();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		alertTodoDto.setDtTodoCreated(new Date());
		alertTodoDto.setDtTodoDue(new Date());
		alertTodoDto.setCdTodoTask(ServiceConstants.RCL_ALERT_TASK_CODE);
		alertTodoDto.setCdTodoType(ServiceConstants.TODO_ACTIONS_ALERT);
		alertTodoDto.setIdTodoCase(stage.getIdCase());
		alertTodoDto.setIdTodoEvent(idEvent);
		alertTodoDto.setIdTodoStage(idStage);
		alertTodoDto.setTodoDesc(description);
		alertTodoDto.setIdTodoPersAssigned(assignedTo);
		alertTodoDto.setIdTodoPersWorker(idCreatedPerson);
		//alertTodoDto.setIdTodoPersCreator(idCreatedPerson);
		return toDoDao.todoAUD(alertTodoDto, serviceReqHeaderDto).getIdTodo();
	
	}
	/**
	 * Method Name: createInvAlert Method Description: This function creates
	 * Investigation Alert.
	 * 
	 * @param toDoDesc
	 * @param toDoLongDesc
	 * @param cdTodoInfoType
	 * @param idPrsnAssgn
	 * @param idUser
	 * @param idStage
	 * @param idEvent
	 * @param dtDueFrom
	 * @return TodoCreateOutDto
	 * 
	 */
	private TodoCreateOutDto createInvAlert(String toDoDesc, String toDoLongDesc, String cdTodoInfoType,
			Long idPrsnAssgn, int idUser, Long idStage, Long idEvent, Date dtDueFrom) {
		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
		todoCreateInDto.setSysCdTodoCf(cdTodoInfoType);
		todoCreateInDto.setDtSysDtTodoCfDueFrom(dtDueFrom);
		todoCreateInDto.setSysIdTodoCfPersCrea((long) idUser);
		todoCreateInDto.setSysIdTodoCfStage(idStage);
		todoCreateInDto.setSysIdTodoCfPersAssgn(idPrsnAssgn);
		todoCreateInDto.setSysIdTodoCfPersWkr((long) idUser);

		MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
		todoCreateInDto.setServiceInputDto(new ServiceInputDto());
		mergeSplitToDoDto.setCdTodoCf(cdTodoInfoType);
		mergeSplitToDoDto.setDtTodoCfDueFrom(dtDueFrom);
		mergeSplitToDoDto.setIdTodoCfPersCrea((long) idUser);
		mergeSplitToDoDto.setIdTodoCfStage(idStage);
		mergeSplitToDoDto.setIdTodoCfPersAssgn(idPrsnAssgn);
		mergeSplitToDoDto.setIdTodoCfPersWkr((long) idUser);

		if (!ObjectUtils.isEmpty(toDoDesc))
			mergeSplitToDoDto.setTodoCfDesc(toDoDesc);
		if (!ObjectUtils.isEmpty(toDoLongDesc))
			mergeSplitToDoDto.setTodoCfLongDesc(toDoLongDesc);
		if (idEvent > 0) {
			mergeSplitToDoDto.setIdTodoCfEvent(idEvent);
		}

		todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);

		/*
		 * if (toDoDesc != null) todoCreateInDto.setSysTxtTodoCfDesc(toDoDesc);
		 * if (toDoLongDesc != null)
		 * todoCreateInDto.setSysTxtTodoCfLongDesc(toDoLongDesc); if (idEvent >
		 * 0) { todoCreateInDto.setSysIdTodoCfEvent(idEvent); }
		 */

		return todoCreateService.TodoCommonFunction(todoCreateInDto);

	}

	/**
	 * Method Name: formatUserInitials Method Description: Formatting user
	 * initials
	 * 
	 * @param personFullName
	 * @return String
	 * 
	 */
	@Override
	public String formatUserInitials(String personFullName) {
		String firstInitial = null;
		String middleInitial = null;
		String lastInitial = null;
		String userName = personFullName.trim();
		int userNameLength = userName.length();
		int index = userName.indexOf(',');
		if (personFullName != null && userNameLength > 0 && index >= 0) {
			lastInitial = String.valueOf(userName.charAt(0)).toUpperCase();
			while (++index < userNameLength && userName.charAt(index) == ' ')
				;
			firstInitial = String.valueOf(userName.charAt(index)).toUpperCase();
			int lastIndex = userName.lastIndexOf(' ');
			if (index < userNameLength && userName.charAt(userNameLength - 1) == '.'
					&& userName.charAt(userNameLength - 2) != ' ') {
				middleInitial = String.valueOf(userName.charAt(userNameLength - 2)).toUpperCase();
			} else if (index < userNameLength && lastIndex > index && lastIndex < userNameLength) {
				middleInitial = String.valueOf(userName.charAt(lastIndex + 1));
			}
		}
		String initials = firstInitial != null ? firstInitial : "";
		initials += middleInitial != null ? middleInitial : "";
		initials += lastInitial != null ? lastInitial : "";
		return initials;
	}

	/**
	 * Method Name: changeInvAlertDueDates Method Description: This function
	 * changes the Due date for Caseworker and Supervisor Alerts if the
	 * Extension Request is Approved
	 * 
	 * @param idInvStage
	 * @param approvalLen
	 * @return long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public Long changeInvAlertDueDates(Long idInvStage, String approvalLen) {
		int aprvLen = 0;
		// Modified the code to compare the approval lenght value with
		// corresponding code value for warranty defect 12294
		if (CodesConstant.CAPRVLEN_10.equals(approvalLen)) {
			aprvLen = 7;
		} else if (CodesConstant.CAPRVLEN_20.equals(approvalLen)) {
			aprvLen = 14;
		} else if (CodesConstant.CAPRVLEN_30.equals(approvalLen)) {
			aprvLen = 21;
		}else if(CodesConstant.CAPRVLEN_40.equals(approvalLen)){
			aprvLen = 30;
		}

		// Retrieve Investigation tasks and Alerts associated with the stage.
		ArrayList<String> cdTodoInfoList = new ArrayList<>();
		cdTodoInfoList.add(ServiceConstants.TODO_INFO_INV_CWALERT);
		cdTodoInfoList.add(ServiceConstants.TODO_INFO_INV30DAY_SUPERVISOR);
		cdTodoInfoList.add(ServiceConstants.TODO_INFO_INV37DAY_SUPERVISOR);
		cdTodoInfoList.add(ServiceConstants.TODO_INFO_INV37DAY_WORKER_TODO);

		List<TodoDto> todoList = toDoDao.selectToDosForStage(idInvStage, cdTodoInfoList);
		for (TodoDto todoDto : todoList) {
			String cdToDoInfo = todoDto.getCdTodoInfo();
			Date dtToDoDue = todoDto.getDtTodoDue();
			Date newDtToDoDue = DateUtils.addToDate(dtToDoDue, 0, 0, aprvLen);

			String shortDesc = todoDto.getTodoDesc();

			// 30-Day CaseWorker Alert.
			if (ServiceConstants.TODO_INFO_INV_CWALERT.equals(cdToDoInfo)) {
				Date newDueDate = DateUtils.addToDate(dtToDoDue, 0, 0,(aprvLen + 15));
				shortDesc = ServiceConstants.SHORT_DESC + DateUtils.stringDt(newDueDate);
			}
			// 37-Day TASK for "CaseWorker".
			else if (ServiceConstants.TODO_INFO_INV37DAY_WORKER_TODO.equals(cdToDoInfo)) {
				Date newDueDate = DateUtils.addToDate(dtToDoDue, 0, 0,(aprvLen + 15 - 7));
				shortDesc = ServiceConstants.SHORT_DESC + DateUtils.stringDt(newDueDate);
				todoDto.setDtTodoTaskDue(newDueDate);
			}

			todoDto.setDtTodoDue(newDtToDoDue);
			todoDto.setTodoDesc(shortDesc);
			todoDto.setTodoLongDesc(shortDesc);

			toDoDao.updateTodo(todoDto);
		}
		return ServiceConstants.ONE_VAL;
	}


	/**
	 * Method Name: updateToDos
	 * Method Description: Alerts link click updates Todo Table.
	 *
	 * @param toDosDto
	 * @return long[]
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public long[] updateAlertToDos(List<TodoDto> toDosDto) {
		long[] retVal = null;
		retVal = toDoDao.updateAlertToDos(toDosDto);

		for (TodoDto currTodo : toDosDto) {
			// find supervisor alert
			Long idSupervisorTodo = toDoDao.findSupervisorAlert(currTodo.getIdTodo());
			if (idSupervisorTodo != null && !idSupervisorTodo.equals(currTodo.getIdTodo())) {
				// update supervisor Todo
				List<TodoDto> supervisorDtoList = new ArrayList<>();
				TodoDto supervisorTodoDto = new TodoDto();
				supervisorTodoDto.setIdTodo(idSupervisorTodo);
				supervisorTodoDto.setIndAlertViewed(currTodo.getIndAlertViewed());
				supervisorDtoList.add(supervisorTodoDto);
				toDoDao.updateAlertToDos(supervisorDtoList);
			}
			// we could add a 1 to the return value array, but it's unused so the effort is not needed.
		}

		return retVal;
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public void getTodoCaseDueDtList(List<TodoDto> toDosDto) {

		toDoDao.enhanceTodoListWithExtensions(toDosDto);

		return;
	}

	/**
	 * Method Name: createRCLAlerts
	 * Method Description: This function creates Alert.
	 *
	 * @param todoDto
	 * @return long
	 *
	 */
	public long createAlert(TodoDto todoDto){
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		return toDoDao.todoAUD(todoDto, serviceReqHeaderDto).getIdTodo();
	}

	/**
	 * method helps to call the todoDao dao for Guardianship referral permissions
	 *
	 * @param idPerson selected person id
	 * @return if person has the permissions to approve the Guardianship Referral return true else false
	 */
	@Override
	public boolean checkGudApprovels(Long idPerson) {
		return toDoDao.checkForGdnRefAprvByPersonId(idPerson);
	}

	@Override
	public boolean checkCaseClosureApprover(Long idPerson){
		return toDoDao.isCaseClosureApprover(idPerson);
	}
}
