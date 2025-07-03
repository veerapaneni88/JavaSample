package us.tx.state.dfps.service.commontodofunction.serviceimpl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.AdminWorkerDao;
import us.tx.state.dfps.service.admin.dao.FetchToDoDao;
import us.tx.state.dfps.service.admin.dao.PersonInfoDao;
import us.tx.state.dfps.service.admin.dao.StageDetailsDao;
import us.tx.state.dfps.service.admin.dao.TodoCreateDao;
import us.tx.state.dfps.service.admin.dao.UnitEmpLinkDao;
import us.tx.state.dfps.service.admin.dto.AdminWorkerInpDto;
import us.tx.state.dfps.service.admin.dto.AdminWorkerOutpDto;
import us.tx.state.dfps.service.admin.dto.EmpLinkInDto;
import us.tx.state.dfps.service.admin.dto.EmpLinkOutDto;
import us.tx.state.dfps.service.admin.dto.EventStageDiDto;
import us.tx.state.dfps.service.admin.dto.FetchToDoDto;
import us.tx.state.dfps.service.admin.dto.FetchToDodiDto;
import us.tx.state.dfps.service.admin.dto.StageDetailsDiDto;
import us.tx.state.dfps.service.admin.dto.StageDetailsDoDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.commontodofunction.service.CommonToDoFunctionService;
import us.tx.state.dfps.xmlstructs.inputstructs.PersonEmployeeInDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;
import us.tx.state.dfps.xmlstructs.outputstructs.PersonEmployeeOutDto;

@Service
@Transactional
public class CommonToDoFunctionServiceImpl implements CommonToDoFunctionService {

	@Autowired
	private StageDetailsDao stageDetailsDao;

	@Autowired
	private FetchToDoDao fetchToDoDao;

	@Autowired
	private AdminWorkerDao adminWorkerDao;

	@Autowired
	private TodoCreateDao todoCreateDao;

	@Autowired
	private UnitEmpLinkDao unitEmpLinkDao;

	@Autowired
	private PersonInfoDao personInfoDao;
	private static final Logger log = Logger.getLogger(CommonToDoFunctionServiceImpl.class);

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public TodoCreateOutDto callCSUB40U(TodoCreateInDto todoCreateInDto) {

		return doCSUB40UO(todoCreateInDto);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public TodoCreateOutDto TodoCommonFunction(TodoCreateInDto todoCreateInDto) {

		return doCSUB40UO(todoCreateInDto);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public TodoCreateOutDto doCSUB40UO(TodoCreateInDto todoCreateInDto) {

		Date dtDtSystemDate = new Date();
		TodoCreateOutDto todoCreateOutDto = new TodoCreateOutDto();
		if (todoCreateInDto.getMergeSplitToDoDto() != null && TypeConvUtil.isNullOrEmpty(todoCreateInDto.getMergeSplitToDoDto().getDtLastUpdate())) {
			todoCreateInDto.getMergeSplitToDoDto().setDtLastUpdate(dtDtSystemDate);
		}
		if (todoCreateInDto.getMergeSplitToDoDto()!= null && TypeConvUtil.isNullOrEmpty(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom())) {
			todoCreateInDto.getMergeSplitToDoDto().setDtTodoCfDueFrom(dtDtSystemDate);
		}
		FetchToDoDto fetchToDoDto = callCSES08D(todoCreateInDto);
		EventStageDiDto eventStageDiDto = new EventStageDiDto();
		ServiceInputDto serviceInputDto = todoCreateInDto.getServiceInputDto() != null
				? todoCreateInDto.getServiceInputDto() : new ServiceInputDto();
		eventStageDiDto.setServiceInputDto(serviceInputDto);
		if (todoCreateInDto.getMergeSplitToDoDto()!= null && ServiceConstants.Y.equals(fetchToDoDto.getIndTodoInfoEnabled())) {
			StageDetailsDoDto stageDetailsDoDto;
			if (!ObjectUtils.isEmpty(todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfStage())) {
				stageDetailsDoDto = callCINT40D(todoCreateInDto);
				eventStageDiDto.setUlIdCase(stageDetailsDoDto.getIdCase());
			} else {
				stageDetailsDoDto = new StageDetailsDoDto();
			}
			if (!ObjectUtils.isEmpty(todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfPersWkr())) {
				eventStageDiDto.setUlIdTodoPersWorker((todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfPersWkr()));
			} else {
				AdminWorkerOutpDto adminWorkerOutpDto = callCINV51D(todoCreateInDto, stageDetailsDoDto);
				eventStageDiDto.setUlIdTodoPersWorker(adminWorkerOutpDto.getIdTodoPersAssigned());
			}
			if (ServiceConstants.TODO_INFO_MED002.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())
					|| ServiceConstants.TODO_INFO_MED003.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())
					|| ServiceConstants.TODO_INFO_LEG001.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())) {
				if (TypeConvUtil.isNullOrEmpty(eventStageDiDto.getServiceInputDto().getCreqFuncCd())) {
					eventStageDiDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				} else if (ServiceConstants.REQ_FUNC_CD_UPDATE
						.equals(eventStageDiDto.getServiceInputDto().getCreqFuncCd())) {
					eventStageDiDto.setLdIdTodo(Long.valueOf(todoCreateInDto.getMergeSplitToDoDto().getIdTodo()));
					eventStageDiDto.setTsLastUpdate(todoCreateInDto.getMergeSplitToDoDto().getDtLastUpdate());
					if (null != todoCreateInDto.getMergeSplitToDoDto().getDtLastUpdate()) {
						eventStageDiDto.setTsLastUpdate(todoCreateInDto.getMergeSplitToDoDto().getDtLastUpdate());
					} else {
						eventStageDiDto.setTsLastUpdate(dtDtSystemDate);
					}
				}
			} else {
				eventStageDiDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			}
			if (TypeConvUtil.isNullOrEmpty(eventStageDiDto.getServiceInputDto().getCreqFuncCd())) {
				eventStageDiDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			}
			if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(eventStageDiDto.getServiceInputDto().getCreqFuncCd())) {
				eventStageDiDto.setLdIdTodo(Long.valueOf(todoCreateInDto.getMergeSplitToDoDto().getIdTodo()));
				eventStageDiDto.setTsLastUpdate(todoCreateInDto.getMergeSplitToDoDto().getDtLastUpdate());
			}
			if (!ObjectUtils.isEmpty(fetchToDoDto.getCdTodoInfoTask())) {
				eventStageDiDto.setSzCdTodoTask(fetchToDoDto.getCdTodoInfoTask());
			}
			if (!ObjectUtils.isEmpty(fetchToDoDto.getCdTodoInfoType())) {
				eventStageDiDto.setSzCdTodoType(fetchToDoDto.getCdTodoInfoType());
			}
			if (!ObjectUtils.isEmpty(todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfEvent())) {
				eventStageDiDto.setUlIdEvent(todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfEvent());
			}
			if (!ObjectUtils.isEmpty(todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfStage())) {
				eventStageDiDto.setUlIdStage(todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfStage());
			}
			if (!ObjectUtils.isEmpty(todoCreateInDto.getMergeSplitToDoDto().getTodoCfLongDesc())) {
				eventStageDiDto.setTxtTodoLongDesc(todoCreateInDto.getMergeSplitToDoDto().getTodoCfLongDesc());
			} else {
				eventStageDiDto.setTxtTodoLongDesc(fetchToDoDto.getTxtTodoInfoLongDesc());
			}
			if (!ObjectUtils.isEmpty(todoCreateInDto.getMergeSplitToDoDto().getTodoCfDesc())) {
				eventStageDiDto.setSzTxtTodoDesc(todoCreateInDto.getMergeSplitToDoDto().getTodoCfDesc());
			} else {
				eventStageDiDto.setSzTxtTodoDesc(fetchToDoDto.getTxtTodoInfoDesc());
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
			if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoDueDd()))
				cal.add(Calendar.DAY_OF_MONTH, fetchToDoDto.getNbrTodoInfoDueDd());
			if (ServiceConstants.TODO_INFO_SUB015.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())
					|| ServiceConstants.TODO_INFO_SUB016.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())
					|| ServiceConstants.TODO_INFO_ADO015.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())) {
				cal.add(Calendar.MONTH, ServiceConstants.MINUS_ONE);
			} else {
				if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoDueMm()))
					cal.add(Calendar.MONTH, fetchToDoDto.getNbrTodoInfoDueMm());
			}
			if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoDueYy()))
				cal.add(Calendar.YEAR, fetchToDoDto.getNbrTodoInfoDueYy());
			eventStageDiDto.setDtDtTodoDue(cal.getTime());
			if (ServiceConstants.TODO_INFO_APS005.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())) {
				String date = eventStageDiDto.getDtDtTodoDue().toString();
				String buffer = replaceStr(fetchToDoDto.getTxtTodoInfoDesc(), ServiceConstants.DATE_DUE, date);
				eventStageDiDto.setSzTxtTodoDesc(buffer);
			}
			if (ServiceConstants.TODO_INFO_APS006.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())) {
				String date = eventStageDiDto.getDtDtTodoDue().toString();
				String buffer = replaceStr(fetchToDoDto.getTxtTodoInfoDesc(), ServiceConstants.DATE_DUE, date);
				eventStageDiDto.setSzTxtTodoDesc(buffer);
			}
			if (ServiceConstants.CD_TODO_TASK.equals(fetchToDoDto.getCdTodoInfoType())) {
				eventStageDiDto.setUlIdTodoPersCreator(ServiceConstants.ZERO_VAL);
				cal.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoTaskDueDd())) {
					cal.add(Calendar.DAY_OF_MONTH, fetchToDoDto.getNbrTodoInfoTaskDueDd());
				}
				if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoTaskDueMm())) {
					cal.add(Calendar.MONTH, fetchToDoDto.getNbrTodoInfoTaskDueMm());
				}
				if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoTaskDueYy())) {
					cal.add(Calendar.YEAR, fetchToDoDto.getNbrTodoInfoTaskDueYy());
				}
				eventStageDiDto.setDtDtTaskDue(cal.getTime());
			} else {

				if (!ObjectUtils.isEmpty(todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfPersCrea())) {
					eventStageDiDto.setUlIdTodoPersCreator(
							Long.valueOf(todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfPersCrea()));
				}
				eventStageDiDto.setDtDtTaskDue(null);
			}
			eventStageDiDto.setDtDtTodoCreated(dtDtSystemDate);
			eventStageDiDto.setDtDtTodoCompleted(null);
			if (ServiceConstants.CD_TASK_SUB_PLCMNT_REQ.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTask())
					|| ServiceConstants.CD_TASK_ADO_PLCMNT_REQ
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTask())
					|| ServiceConstants.CD_TASK_SUB_PLCMNT_STTS
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTask())
					|| ServiceConstants.CD_TASK_ADO_PLCMNT_STTS
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTask())) {
				eventStageDiDto.setSzCdTodoTask(todoCreateInDto.getMergeSplitToDoDto().getCdTask());
			}
			if (ServiceConstants.TODO_INFO_MED002.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())
					|| ServiceConstants.TODO_INFO_MED003.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())
					|| ServiceConstants.TODO_INFO_LEG001.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())) {
				eventStageDiDto.setDtDtTaskDue(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				eventStageDiDto.setSzCdTodoTask(todoCreateInDto.getMergeSplitToDoDto().getCdTask());
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoTaskDueDd())) {
					cal1.add(Calendar.DAY_OF_MONTH, -fetchToDoDto.getNbrTodoInfoDueDd());
				}
				if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoDueMm())) {
					cal1.add(Calendar.MONTH, -fetchToDoDto.getNbrTodoInfoDueMm());
				}
				if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoDueYy())) {
					cal1.add(Calendar.YEAR, -fetchToDoDto.getNbrTodoInfoDueYy());
				}
				eventStageDiDto.setDtDtTodoDue(cal1.getTime());
				if (DateUtils.isBeforeToday(eventStageDiDto.getDtDtTodoDue())) {
					eventStageDiDto.setDtDtTodoDue(new Date());
				}
				if (ServiceConstants.CD_TODO_TASK.equals(fetchToDoDto.getCdTodoInfoType())
						&& !DateUtils.isNull(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCompleted())) {
					eventStageDiDto.setDtDtTodoCompleted(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCompleted());
				}
			}
			if (ServiceConstants.TODO_INFO_ADP002.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())
					|| ServiceConstants.TODO_INFO_ADP003.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())
					|| ServiceConstants.TODO_INFO_PCA001.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())) {
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoDueDd())) {
					cal1.add(Calendar.DAY_OF_MONTH, -fetchToDoDto.getNbrTodoInfoDueDd());
				}
				if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoDueMm())) {
					cal1.add(Calendar.MONTH, -fetchToDoDto.getNbrTodoInfoDueMm());
				}
				if (!ObjectUtils.isEmpty(fetchToDoDto.getNbrTodoInfoDueYy())) {
					cal1.add(Calendar.YEAR, -fetchToDoDto.getNbrTodoInfoDueYy());
				}
				eventStageDiDto.setDtDtTodoDue(cal1.getTime());
			}
			if (ServiceConstants.TODO_INFO_INV_CWALERT.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())
					|| ServiceConstants.TODO_INFO_INV30DAY_SUPERVISOR
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())) {
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				cal1.add(Calendar.DAY_OF_MONTH, ServiceConstants.THIRTY_VALUE);
				eventStageDiDto.setDtDtTodoDue(cal1.getTime());
			}
			if (ServiceConstants.TODO_INFO_INV37DAY_SUPERVISOR
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())
					|| ServiceConstants.TODO_INFO_INV37DAY_WORKER_TODO
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())) {
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				cal1.add(Calendar.DAY_OF_MONTH, ServiceConstants.THIRTY_SEVEN_VALUE);
				eventStageDiDto.setDtDtTodoDue(cal1.getTime());
			}
			if (ServiceConstants.TODO_INFO_INV37DAY_WORKER_TODO
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf())) {
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				cal2.add(Calendar.DAY_OF_MONTH, ServiceConstants.FORTY_FIVE_VALUE);
				eventStageDiDto.setDtDtTaskDue(cal2.getTime());
			}
			if ((ServiceConstants.TODO_AR_SAFETY_ASSESSMENT_7DAY_ALERT_PR
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))
					&& (ServiceConstants.TODO_AR_SAFETY_ASSESSMENT_7DAY_ALERT_SUP
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))) {
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				cal1.add(Calendar.DAY_OF_MONTH, ServiceConstants.SEVEN_VALUE);
				eventStageDiDto.setDtDtTodoDue(cal1.getTime());
			}
			if ((ServiceConstants.TODO_AR_SAFETY_ASSESSMENT_10DAY_TASK_PR
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))
					&& (ServiceConstants.TODO_AR_SAFETY_ASSESSMENT_10DAY_ALERT_SUP
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))) {
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				cal1.add(Calendar.DAY_OF_MONTH, ServiceConstants.TEN_VALUE);
				eventStageDiDto.setDtDtTodoDue(cal1.getTime());
			}
			if ((ServiceConstants.TODO_AR_SUBMIT_AR_COMPLETE_OR_EXREQ_45DAY_ALERT_PR
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))
					&& (ServiceConstants.TODO_AR_SUBMIT_AR_COMPLETE_OR_EXREQ_45DAY_ALERT_SUP
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))) {
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				cal1.add(Calendar.DAY_OF_MONTH, ServiceConstants.FORTY_FIVE_VALUE);
				eventStageDiDto.setDtDtTodoDue(cal1.getTime());
			}
			if ((ServiceConstants.TODO_AR_SUBMIT_AR_COMPLETE_OR_EXREQ_52DAY_TASK_PR
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))
					&& (ServiceConstants.TODO_AR_SUBMIT_AR_COMPLETE_OR_EXREQ_52DAY_ALERT_SUP
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))) {
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				cal1.add(Calendar.DAY_OF_MONTH, ServiceConstants.FIFTY_TWO_VALUE);
				eventStageDiDto.setDtDtTodoDue(cal1.getTime());
			}
			if ((ServiceConstants.TODO_AR_SUBMIT_AR_COMPLETE_72DAY_ALERT_PR
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))
					&& (ServiceConstants.TODO_AR_SUBMIT_AR_COMPLETE_72DAY_ALERT_SUP
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))) {
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				cal1.add(Calendar.DAY_OF_MONTH, ServiceConstants.SEVENTY_TWO_VALUE);
				eventStageDiDto.setDtDtTodoDue(cal1.getTime());
			}
			if ((ServiceConstants.TODO_AR_SUBMIT_AR_CLOSURE_TODAY_80DAY_TASK_PR
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))
					&& (ServiceConstants.TODO_AR_SUBMIT_AR_CLOSURE_TODAY_80DAY_ALERT_SUP
					.equals(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf()))) {
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(todoCreateInDto.getMergeSplitToDoDto().getDtTodoCfDueFrom());
				cal1.add(Calendar.DAY_OF_MONTH, ServiceConstants.EIGHTY_VALUE);
				eventStageDiDto.setDtDtTodoDue(cal1.getTime());
			}
			eventStageDiDto.setUlIdTodoInfo(fetchToDoDto.getIdTodoInfo());
			if (ServiceConstants.Zero != todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfPersAssgn()) {
				eventStageDiDto.setUlIdTodoPersAssigned(
						new Long(todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfPersAssgn()));
				todoCreateDao.audToDo(eventStageDiDto);
			} else if (ServiceConstants.CD_MEMBER.equals(fetchToDoDto.getCdTodoInfoPersAssignd())) {
				eventStageDiDto.setUlIdTodoPersAssigned(eventStageDiDto.getUlIdTodoPersWorker());
				todoCreateDao.audToDo(eventStageDiDto);
			} else if (ServiceConstants.CD_LEAD.equals(fetchToDoDto.getCdTodoInfoPersAssignd())) {
				PersonEmployeeInDto personEmployeeInDto = new PersonEmployeeInDto();
				personEmployeeInDto.setArchInputStruct(todoCreateInDto.getServiceInputDto());
				personEmployeeInDto.setIdPerson(eventStageDiDto.getUlIdTodoPersWorker());
				PersonEmployeeOutDto personEmployeeOutDto;
				List<PersonEmployeeOutDto> personEmployeeOutDtoList = personInfoDao.getSupervisor(personEmployeeInDto);
				personEmployeeOutDto = personEmployeeOutDtoList.get(ServiceConstants.Zero);
				eventStageDiDto.setUlIdTodoPersAssigned(personEmployeeOutDto.getIdPerson());
				todoCreateDao.audToDo(eventStageDiDto);
			} else {
				EmpLinkInDto empLinkInDto = new EmpLinkInDto();
				empLinkInDto.setServiceInputDto(todoCreateInDto.getServiceInputDto());
				empLinkInDto.setIdPerson(eventStageDiDto.getUlIdTodoPersWorker());
				empLinkInDto.setCdUnitMemberRole(fetchToDoDto.getCdTodoInfoPersAssignd());
				List<EmpLinkOutDto> empLinkOutDtoList = unitEmpLinkDao.getUnitEmpLinkRecord(empLinkInDto);
				if(!ObjectUtils.isEmpty(empLinkOutDtoList)){
					EmpLinkOutDto empLinkOutDto = empLinkOutDtoList.get(ServiceConstants.Zero);
					eventStageDiDto.setUlIdTodoPersAssigned(empLinkOutDto.getIdPerson());
					todoCreateDao.audToDo(eventStageDiDto);
				}

			}
			todoCreateOutDto.setCdTodoTask(eventStageDiDto.getSzCdTodoTask());
			todoCreateOutDto.setCdTodoType(eventStageDiDto.getSzCdTodoType());
			todoCreateOutDto.setDtDtTaskDue(eventStageDiDto.getDtDtTodoCompleted());
			todoCreateOutDto.setDtDtTodoCompleted(eventStageDiDto.getDtDtTodoCreated());
			todoCreateOutDto.setDtDtTodoCreated(eventStageDiDto.getDtDtTodoDue());
			todoCreateOutDto.setDtDtTodoDue(eventStageDiDto.getDtDtTaskDue());
			if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getUlIdCase())) {
				todoCreateOutDto.setIdCase(eventStageDiDto.getUlIdCase());
			}
			if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getUlIdCase())) {
				todoCreateOutDto.setIdCase(eventStageDiDto.getUlIdCase());
			}
			if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getUlIdEvent())) {
				todoCreateOutDto.setIdEvent(eventStageDiDto.getUlIdEvent());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getUlIdStage())) {
				todoCreateOutDto.setIdStage(eventStageDiDto.getUlIdStage());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getLdIdTodo())) {
				todoCreateOutDto.setIdTodo(eventStageDiDto.getLdIdTodo());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getUlIdTodoPersAssigned())) {
				todoCreateOutDto.setIdTodoPersAssigned(eventStageDiDto.getUlIdTodoPersAssigned());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getUlIdTodoPersCreator())) {
				todoCreateOutDto.setIdTodoPersCreator(eventStageDiDto.getUlIdTodoPersCreator());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getUlIdTodoPersWorker())) {
				todoCreateOutDto.setIdTodoPersWorker(eventStageDiDto.getUlIdTodoPersWorker());
			}

			todoCreateOutDto.setTxtTodoDesc(eventStageDiDto.getSzTxtTodoDesc());
			todoCreateOutDto.setTxtTodoLongDesc(eventStageDiDto.getTxtTodoLongDesc());
		}
		return todoCreateOutDto;
	}

	private AdminWorkerOutpDto callCINV51D(TodoCreateInDto todoCreateInDto, StageDetailsDoDto stageDetailsDoDto) {
		AdminWorkerInpDto adminWorkerInpDto = new AdminWorkerInpDto();
		adminWorkerInpDto.setServiceInputDto(todoCreateInDto.getServiceInputDto());
		adminWorkerInpDto.setIdStage(todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfStage());
		if (ServiceConstants.CLOSE_STAGE.equals(stageDetailsDoDto.getIndStageClose())) {
			if (stageDetailsDoDto.getDtStageClose().getYear() != ServiceConstants.ZERO_SHORT) {
				adminWorkerInpDto.setCdStagePersRole(ServiceConstants.HIST_PRIM_ROLE_STAGE_CLOSE);
			}
		} else {
			adminWorkerInpDto.setCdStagePersRole(ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
		}
		return adminWorkerDao.getPersonInRole(adminWorkerInpDto);
	}

	private StageDetailsDoDto callCINT40D(TodoCreateInDto todoCreateInDto) {
		StageDetailsDiDto stageDetailsDiDto = new StageDetailsDiDto();
		stageDetailsDiDto.setArchInputStruct(todoCreateInDto.getServiceInputDto());
		stageDetailsDiDto.setIdStage(todoCreateInDto.getMergeSplitToDoDto().getIdTodoCfStage());
		List<StageDetailsDoDto> stageDetailsDoDtoList = stageDetailsDao.getStageDtls(stageDetailsDiDto);
		StageDetailsDoDto stageDetailsDoDto = stageDetailsDoDtoList.get(ServiceConstants.Zero);
		return stageDetailsDoDto;
	}

	private FetchToDoDto callCSES08D(TodoCreateInDto todoCreateInDto) {
		FetchToDodiDto fetchToDodiDto = new FetchToDodiDto();
		FetchToDoDto fetchToDoDto = new FetchToDoDto();
		fetchToDodiDto.setServiceInputDto(todoCreateInDto.getServiceInputDto());
		if(todoCreateInDto.getMergeSplitToDoDto() != null ) {
			fetchToDodiDto.setCdTodoInfo(todoCreateInDto.getMergeSplitToDoDto().getCdTodoCf());
		}
		List<FetchToDoDto> fetchToDoDtoList = fetchToDoDao.getTodoInfo(fetchToDodiDto);
		if (!CollectionUtils.isEmpty(fetchToDoDtoList)) {
			fetchToDoDto = fetchToDoDtoList.get(ServiceConstants.Zero);
		}
		return fetchToDoDto;

	}

	public String replaceStr(String sOrig, String sSubstr, String sRep) {
		String buffer = sOrig.replaceAll(sSubstr, sRep);
		return buffer;
	}

}
