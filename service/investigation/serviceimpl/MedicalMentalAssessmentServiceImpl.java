/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 14, 2017- 3:27:27 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.investigation.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ProfessionalAssmt;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.common.dto.PostEventPersonDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionInputDto;
import us.tx.state.dfps.service.admin.dao.UpdateEventDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.UpdateEventiDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.casepackage.dto.MdclMentalAssmntDtlDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.request.MdclMentalAssmtReq;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.response.PostEventRes;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.investigation.dao.MedicalMentalAssessmentDao;
import us.tx.state.dfps.service.investigation.service.MedicalMentalAssessmentService;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.dto.TodoInfoCommonDto;
import us.tx.state.dfps.service.workload.service.TodoCommonFunctionService;
import us.tx.state.dfps.web.mdclMentalAssmnt.bean.MdclSaveDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 14, 2017- 3:27:27 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class MedicalMentalAssessmentServiceImpl implements MedicalMentalAssessmentService {

	@Autowired
	CheckStageEventStatusService checkStageEventStatusService;

	@Autowired
	private StageEventStatusCommonService stageEventStatusCommonService;

	@Autowired
	EventService eventService;

	@Autowired
	TodoCommonFunctionService todoCommonFunctionService;

	@Autowired
	TodoDao todoDao;

	@Autowired
	MedicalMentalAssessmentDao medicalMentalAssessmentDao;

	@Autowired
	CodesDao codesDao;

	@Autowired
	ApprovalCommonService approvalService;

	@Autowired
	UpdateEventDao updateEventDao;

	public static final int MSG_SYS_STAGE_CLOSED = 8164;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.investigation.service.
	 * MedicalMentalAssessmentService#medcialMentalAssessmentAUD(us.tx.state.
	 * dfps.service.common.request.MdclMentalAssmtReq) CINV31
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MdclSaveDto medcialMentalAssessmentAUD(MdclMentalAssmtReq mdclMentalAssmtReq) {
		InCheckStageEventStatusDto dtoObj = new InCheckStageEventStatusDto();
		MdclSaveDto mdclSaveDto = new MdclSaveDto();
		Long result = 0L;
		dtoObj.setCdReqFunction(mdclMentalAssmtReq.getReqFuncCd());
		dtoObj.setIdStage(mdclMentalAssmtReq.getIdStage());
		dtoObj.setCdTask(mdclMentalAssmtReq.getCdTask());
		StageTaskInDto stageTaskInDto = new StageTaskInDto();
		stageTaskInDto.setIdStage(mdclMentalAssmtReq.getIdStage());
		stageTaskInDto.setCdTask(mdclMentalAssmtReq.getCdTask());
		stageTaskInDto.setCdReqFunction(mdclMentalAssmtReq.getReqFuncCd());
		String retVal = stageEventStatusCommonService.checkStageEventStatus(stageTaskInDto);
		// Defect#13376 - Modified the code based on the tuxedo service cinv31s
		boolean isCVSStages;
		isCVSStages = isCVSStages(mdclMentalAssmtReq);
		if (ServiceConstants.ARC_SUCCESS.equals(retVal)) {
			if (!ObjectUtils.isEmpty(mdclMentalAssmtReq.getIdEvent())) {
				Long idEvent = callPostEvent(mdclMentalAssmtReq, isCVSStages);
				// validate the conclusion event if it is passed.
				if (!ObjectUtils.isEmpty(mdclMentalAssmtReq.getSysNbrReserved1())
						&& !mdclMentalAssmtReq.getSysNbrReserved1()
						&& !ObjectUtils.isEmpty(mdclMentalAssmtReq.getIdCCLEvent())) {
					UpdateEventiDto updateEventiDto = new UpdateEventiDto();
					updateEventiDto.setUlIdEvent(mdclMentalAssmtReq.getIdEvent());
					updateEventDao.updateEvent(updateEventiDto);

					ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
					approvalCommonInDto.setIdEvent(mdclMentalAssmtReq.getIdCCLEvent());
					approvalService.callCcmn05uService(approvalCommonInDto);
				}

				callToDoCommonFunction(mdclMentalAssmtReq, idEvent, isCVSStages);
				result = medicalAssessmentAUD(mdclMentalAssmtReq, idEvent);
				mdclSaveDto.setRowId(result);
			} else if (ServiceConstants.MSG_SYS_STAGE_CLOSED.equals(retVal)) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(MSG_SYS_STAGE_CLOSED);
				mdclSaveDto.setErrorDto(errorDto);
				mdclSaveDto.setRowId(result);
			}

		}

		return mdclSaveDto;
	}

	private Long callPostEvent(MdclMentalAssmtReq mdclMentalAssmtReq, boolean isCVSStages) {
		Long idEvent = 0L;
		PostEventReq postEventReq = new PostEventReq();

		if (mdclMentalAssmtReq.getMdclMentalAssmntDtlDto() != null) {
			if (mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getIdEvent() == 0L) {
				postEventReq.setDtDtEventOccurred(new Date());
				postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			} else {
				postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				postEventReq.setDtDtEventOccurred(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtEventOccurred());
			}
			/*
			 * Copy Event Person Link information only if the a record is to be
			 * inserted in the Event person Link
			 */
			if (mdclMentalAssmtReq.getReqFuncCd() != null
					&& ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(mdclMentalAssmtReq.getReqFuncCd())) {
				List<PostEventPersonDto> postEventPersonList = new ArrayList<PostEventPersonDto>();
				PostEventPersonDto personLinkDto = new PostEventPersonDto();
				personLinkDto.setIdPerson(mdclMentalAssmtReq.getIdPerson());
				personLinkDto.setCdScrDataAction(mdclMentalAssmtReq.getReqFuncCd());
				postEventPersonList.add(personLinkDto);
				postEventReq.setPostEventPersonList(postEventPersonList);
			}
			postEventReq.setUlIdEvent(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getIdEvent());
			postEventReq.setUlIdStage(mdclMentalAssmtReq.getIdStage());
			postEventReq.setUlIdPerson(mdclMentalAssmtReq.getIdPerson());
			postEventReq.setSzCdTask(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdTask());
			postEventReq.setSzCdEventType(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdEventType());
			postEventReq.setSzTxtEventDescr(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getEventDescr());
			postEventReq.setTsLastUpdate(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getTsLastUpdate());
			postEventReq.setCdStage(mdclMentalAssmtReq.getStage());
			postEventReq.setUserId(mdclMentalAssmtReq.getUserId());
			if (isCVSStages) {
				if (null != mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtProfAssmtAppt()) {
					postEventReq.setSzCdEventStatus(CodesConstant.CEVTSTAT_COMP);
				} else {
					postEventReq.setSzCdEventStatus(CodesConstant.CEVTSTAT_NEW);
				}
			} else {
				postEventReq.setSzCdEventStatus(CodesConstant.CEVTSTAT_COMP);
			}

			PostEventRes response = eventService.postEvent(postEventReq);
			if (null != response) {
				idEvent = response.getUlIdEvent();
			}
		}
		return idEvent;
	}

	private void callToDoCommonFunction(MdclMentalAssmtReq mdclMentalAssmtReq, Long idEvent, boolean isCVSStages) {
		TodoCommonFunctionInputDto todoDto = new TodoCommonFunctionInputDto();
		todoDto.setReqFuncCd(mdclMentalAssmtReq.getReqFuncCd());
		TodoCommonFunctionDto dto = new TodoCommonFunctionDto();
		dto.setCdTask(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdTask());
		// CSES08D
		TodoInfoCommonDto medTwo = todoDao.getTodoInfoByTodoInfo(ServiceConstants.TODO_INFO_MED002);
		TodoInfoCommonDto medThree = todoDao.getTodoInfoByTodoInfo(ServiceConstants.TODO_INFO_MED003);
		List<TodoDto> todoDtoList = todoDao
				.fetchToDoListForEvent(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getIdEvent());
		int nbrOfTodos = todoDtoList != null ? todoDtoList.size() : 0;
		if (mdclMentalAssmtReq.getReqFuncCd() != null
				&& ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(mdclMentalAssmtReq.getReqFuncCd())) {
			for (TodoDto todo : todoDtoList) {
				if (todo.getIdTodoInfo() == 0L) {
					// ServiceReqHeaderDto object is made as the TODODAO.todoAUD
					// is method parameter in this manner. Else only the
					// particular value could have been enough
					ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
					todo.setDtTodoCompleted(new Date());
					todoDao.todoAUD(todo, serviceReqHeaderDto);
				}
			}
			// a ToDo for the Next Appointment date.
			// Calendar calendar = Calendar.getInstance();
			// calendar.setTime(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtProfAssmtAppt());
			// int year = calendar.get(Calendar.YEAR);
			if (null != mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtProfAssmtAppt()) {
				if (CodesConstant.CARSAPPT_PYM
						.equals(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdProfAssmtApptRsn())
						|| CodesConstant.CARSAPPT_DAA
								.equals(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdProfAssmtApptRsn())) {
					dto.setDtSysDtTodoCfDueFrom(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue());

					dto.setSysCdTodoCf(ServiceConstants.TODO_INFO_MED002);
					setTodoDescMessage(dto, mdclMentalAssmtReq.getMdclMentalAssmntDtlDto());
					// Call the Common function to create ToDo
					// Insert new the Todo.
					todoDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
					setTodoDescMessage(dto, mdclMentalAssmtReq.getMdclMentalAssmntDtlDto());
					toDoCommonFunctionHelper(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto(), dto, idEvent,
							mdclMentalAssmtReq.getReqFuncCd(), todoDto);
				}
			} else // Else we will create a Todo for the Appt Scheduled date
			if (null != mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtApptScheduled()) {
				dto.setDtSysDtTodoCfDueFrom(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtApptScheduled());
				dto.setSysCdTodoCf(ServiceConstants.TODO_INFO_MED003);
				// Call the Common function to create ToDo
				// Insert new the Todo.
				todoDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				setTodoDescMessage(dto, mdclMentalAssmtReq.getMdclMentalAssmntDtlDto());
				toDoCommonFunctionHelper(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto(), dto, idEvent,
						mdclMentalAssmtReq.getReqFuncCd(), todoDto);
			}
		} else if (mdclMentalAssmtReq.getReqFuncCd() != null
				&& ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(mdclMentalAssmtReq.getReqFuncCd())) {
			boolean bMed002TodoTypeExists = false;
			boolean bMed003TodoTypeExists = false;
			for (TodoDto todo : todoDtoList) {
				if (todo.getIdTodoInfo().equals(medTwo.getIdTodoInfo())) {
					bMed002TodoTypeExists = true;
				} else if (todo.getIdTodoInfo().equals(medThree.getIdTodoInfo())) {
					bMed003TodoTypeExists = true;
				}
			}
			if (nbrOfTodos > 1) {
				// the date occurred, as the scheduled date todo will be
				// uneditable if it comes into this logic.
				if (bMed002TodoTypeExists && bMed003TodoTypeExists) {
					for (TodoDto todo : todoDtoList) {
						if (todo.getIdTodoInfo().equals(medTwo.getIdTodoInfo())) {
							// due.
							if (CodesConstant.CARSAPPT_PYM
									.equals(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdProfAssmtApptRsn())
									|| CodesConstant.CARSAPPT_DAA.equals(
											mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdProfAssmtApptRsn())) {
								if (null != mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtProfAssmtAppt()) {
									if (!ObjectUtils
											.isEmpty(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue())
											&& new Date().before(
													mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue())
											|| new Date().equals(DateUtils.stringDate(mdclMentalAssmtReq
													.getMdclMentalAssmntDtlDto().getDtNxtApptDue()))) {
										// Update the Todo
										todoDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

										dto.setDtSysDtTodoCfDueFrom(
												mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue());
										dto.setSysCdTodoCf(ServiceConstants.TODO_INFO_MED002);
										dto.setIdTodo(todo.getIdTodo());
										dto.setTsLastUpdate(todo.getDtLastUpdate());
										setTodoDescMessage(dto, mdclMentalAssmtReq.getMdclMentalAssmntDtlDto());
										toDoCommonFunctionHelper(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto(), dto,
												idEvent, mdclMentalAssmtReq.getReqFuncCd(), todoDto);
									} else {
										deleteTodoHelper(todo.getIdTodo());
									}
								}
							} else {
								deleteTodoHelper(todo.getIdTodo());
							}
						}
					}
					// end of for
				} else {
					updateMedTodoType(mdclMentalAssmtReq, idEvent, todoDto, dto, todoDtoList, medTwo, medThree);
				}
			} else if (nbrOfTodos == 1) {
				updateMedTodoType(mdclMentalAssmtReq, idEvent, todoDto, dto, todoDtoList, medTwo, medThree);
			} else // It will come in this condition only when we are updating
					// existng records which are already compeleted
			// But they dont have any Todo yet. So we will check if the date
			// next appmnt date is future date then we create
			// a new todo.
			{
				if ((CodesConstant.CARSAPPT_PYM
						.equals(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdProfAssmtApptRsn())
						|| CodesConstant.CARSAPPT_DAA
								.equals(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdProfAssmtApptRsn()))
								&& null != mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtProfAssmtAppt()) {
					if (!ObjectUtils.isEmpty(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue())
							&& new Date().before(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue())
							|| new Date().equals(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue())) {
						// Insert new the Todo.
						todoDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
						dto.setDtSysDtTodoCfDueFrom(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue());
						dto.setSysCdTodoCf(ServiceConstants.TODO_INFO_MED002);
						setTodoDescMessage(dto, mdclMentalAssmtReq.getMdclMentalAssmntDtlDto());
						// Fixed Warranty Defect#12298 - Need to create a new
						// Todo required function has to be passed as ADD
						toDoCommonFunctionHelper(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto(), dto, idEvent,
								todoDto.getReqFuncCd(), todoDto);
					}
				}
			}
		} else // Delete Function Defect#13376 - Added condition to delete only
				// for specific stages
		{
			if (isCVSStages && !ObjectUtils.isEmpty(mdclMentalAssmtReq.getReqFuncCd())
					&& ServiceConstants.REQ_FUNC_CD_DELETE.equalsIgnoreCase(mdclMentalAssmtReq.getReqFuncCd())) {
				callDeleteTodo(todoDtoList);
			}
		}
	}

	private void setTodoDescMessage(TodoCommonFunctionDto dto, MdclMentalAssmntDtlDto mdclMentalAssmntDtlDto) {
		TodoInfoCommonDto response = todoDao.getTodoInfoByTodoInfo(dto.getSysCdTodoCf());
		String todoDesc = replaceStr(response.getTodoInfoDesc(),
				ServiceConstants.NAME_OF_PERSON_TODO_DESC_SUBSTRING_TO_REPLACE,
				mdclMentalAssmntDtlDto.getNameProfAssmtPrincipal());
		List<CodeAttributes> retList = codesDao.getCodesTable(CodesConstant.CARSAPPT,
				mdclMentalAssmntDtlDto.getCdProfAssmtApptRsn());
		if (null != retList && retList.size() > 0) {
			todoDesc = replaceStr(todoDesc, ServiceConstants.MED_MENTAL_REASON_TODO_DESC_SUBSTRING_TO_REPLACE,
					retList.get(0).getDecode());
		}
		if (null != dto.getDtSysDtTodoCfDueFrom()) {
			// Modified the code for warranty defect 12511
			String appointmentDt = DateUtils.dateStringInSlashFormat(dto.getDtSysDtTodoCfDueFrom());
			todoDesc = replaceStr(todoDesc, ServiceConstants.DATE_DUE_TODO_DESC_SUBSTRING_TO_REPLACE, appointmentDt);
		} else {
			todoDesc = replaceStr(todoDesc, ServiceConstants.DATE_DUE_TODO_DESC_SUBSTRING_TO_REPLACE, "");
		}
		dto.setSysTxtTodoCfDesc(todoDesc);
	}

	private static String replaceStr(String sOrig, String sSubstr, String sRep) {
		String buffer = new String();
		buffer = sOrig.replaceAll(sSubstr, sRep);
		return buffer;
	}

	private void toDoCommonFunctionHelper(MdclMentalAssmntDtlDto mdclMentalAssmntDtlDto, TodoCommonFunctionDto dto,
			Long idEvent, String action, TodoCommonFunctionInputDto todoDto) {
		if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(action)) {
			dto.setSysIdTodoCfEvent(idEvent);
		} else {
			dto.setSysIdTodoCfEvent(mdclMentalAssmntDtlDto.getIdEvent());
		}
		dto.setSysIdTodoCfPersAssgn(mdclMentalAssmntDtlDto.getSysIdTodoCfPersAssgn());
		dto.setSysIdTodoCfPersCrea(mdclMentalAssmntDtlDto.getSysIdTodoCfPersCrea());
		dto.setSysIdTodoCfStage(mdclMentalAssmntDtlDto.getIdStage());
		if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(action)) {
			todoDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(action)) {
			todoDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equalsIgnoreCase(action)) {
			todoDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_DELETE);
		}
		todoDto.setTodoCommonFunctionDto(dto);
		todoCommonFunctionService.TodoCommonFunction(todoDto);
	}

	private void deleteTodoHelper(Long todoId) {
		TodoDto dto = new TodoDto();
		// ServiceReqHeaderDto object is made as the TODODAO.todoAUD is method
		// parameter in this manner. Else only the particular value could have
		// been enough
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_DELETE);
		dto.setIdTodo(todoId);
		todoDao.todoAUD(dto, serviceReqHeaderDto);
	}

	private void updateMedTodoType(MdclMentalAssmtReq mdclMentalAssmtReq, Long idEvent,
			TodoCommonFunctionInputDto todoDto, TodoCommonFunctionDto dto, List<TodoDto> todoDtoList,
			TodoInfoCommonDto medTwo, TodoInfoCommonDto medThree) {
		for (TodoDto todo : todoDtoList) {
			if (todo.getIdTodoInfo().equals(medTwo.getIdTodoInfo())) {
				// delete the Todo related to the next appointment due.
				if (CodesConstant.CARSAPPT_PYM
						.equals(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdProfAssmtApptRsn())
						|| CodesConstant.CARSAPPT_DAA
								.equals(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdProfAssmtApptRsn())) {
					if (null != mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtProfAssmtAppt()) {
						{
							// If the user back date occured and the NxtApptDue
							// falls before today then delete that todo.
							if (!ObjectUtils.isEmpty(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue())
									&& new Date()
											.before(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue())
									|| new Date()
											.equals(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue())) {
								// Update the Todo
								todoDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
								dto.setDtSysDtTodoCfDueFrom(
										mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue());
								dto.setSysCdTodoCf(ServiceConstants.TODO_INFO_MED002);
								dto.setIdTodo(todo.getIdTodo());
								dto.setTsLastUpdate(todo.getDtLastUpdate());
								setTodoDescMessage(dto, mdclMentalAssmtReq.getMdclMentalAssmntDtlDto());
								toDoCommonFunctionHelper(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto(), dto, idEvent,
										mdclMentalAssmtReq.getReqFuncCd(), todoDto);
							} else {
								deleteTodoHelper(todo.getIdTodo());
							}
						}
						break;
					} else {
						deleteTodoHelper(todo.getIdTodo());
						break;
					}
				}
			} else if (todo.getIdTodoInfo().equals(medThree.getIdTodoInfo())) {
				if (null != mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtApptScheduled()) {
					// Update the Todo
					todoDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

					dto.setDtSysDtTodoCfDueFrom(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtApptScheduled());
					dto.setSysCdTodoCf(ServiceConstants.TODO_INFO_MED003);
					dto.setIdTodo(todo.getIdTodo());
					dto.setTsLastUpdate(todo.getDtLastUpdate());
					setTodoDescMessage(dto, mdclMentalAssmtReq.getMdclMentalAssmntDtlDto());
					// and the todo is not completed, then we mark that todo
					// as completed by putting the date completed.
					if (null != mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtProfAssmtAppt()
							&& null == todo.getDtTodoCompleted()) {
						dto.setDtTodoCompleted(new Date());
					}
					toDoCommonFunctionHelper(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto(), dto, idEvent,
							mdclMentalAssmtReq.getReqFuncCd(), todoDto);
				}
				//Defect-13713 - this if block will get executed only when the assessment reason is (PYM or DAA) and date professional assessment appointment added.
				if ((CodesConstant.CARSAPPT_PYM
						.equals(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdProfAssmtApptRsn())
						|| CodesConstant.CARSAPPT_DAA
								.equals(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getCdProfAssmtApptRsn()))
								&& null != mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtProfAssmtAppt()) {
					// Insert new the Todo.
					todoDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
					dto.setDtSysDtTodoCfDueFrom(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtNxtApptDue());
					dto.setSysCdTodoCf(ServiceConstants.TODO_INFO_MED002);
					setTodoDescMessage(dto, mdclMentalAssmtReq.getMdclMentalAssmntDtlDto());
					toDoCommonFunctionHelper(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto(), dto, idEvent,
							todoDto.getReqFuncCd(), todoDto);

				}
				//[artf204877] Defect: 18305 -   added changes for  updating todo completed date column if dateEventOccurred is present
				if(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtEventOccurred() != null){
					todoDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
					dto.setDtSysDtTodoCfDueFrom(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtApptScheduled());
					dto.setSysCdTodoCf(ServiceConstants.TODO_INFO_MED003);
					dto.setIdTodo(todo.getIdTodo());
					dto.setTsLastUpdate(todo.getDtLastUpdate());
					setTodoDescMessage(dto, mdclMentalAssmtReq.getMdclMentalAssmntDtlDto());
					dto.setDtTodoCompleted(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getDtEventOccurred());
					toDoCommonFunctionHelper(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto(), dto, idEvent,
							mdclMentalAssmtReq.getReqFuncCd(), todoDto);
				}
			}

		}
	}

	private void callDeleteTodo(List<TodoDto> todoDtoList) {
		for (TodoDto todo : todoDtoList) {
			deleteTodoHelper(todo.getIdTodo());
		}
	}

	private Long medicalAssessmentAUD(MdclMentalAssmtReq mdclMentalAssmtReq, Long idNewEvent) {
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(mdclMentalAssmtReq.getReqFuncCd())
				|| ServiceConstants.REQ_FUNC_CD_DELETE.equals(mdclMentalAssmtReq.getReqFuncCd())) {
			mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().setIdEvent(idNewEvent);
		}

		ProfessionalAssmt profassessment = medicalMentalAssessmentDao
				.getProfessionalAssmt(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getIdEvent());

		if (!ObjectUtils.isEmpty(profassessment) && !ObjectUtils.isEmpty(profassessment.getDtLastUpdate())
				&& !ObjectUtils.isEmpty(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getTsLastUpdate())
				&& mdclMentalAssmtReq.getMdclMentalAssmntDtlDto().getTsLastUpdate()
						.compareTo(profassessment.getDtLastUpdate()) != 0)
			throw new ServiceLayerException("Time Mismatch error", new Long(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH),
					null);

		return medicalMentalAssessmentDao.medicalAssessmentAUD(mdclMentalAssmtReq.getMdclMentalAssmntDtlDto(),
				mdclMentalAssmtReq.getReqFuncCd());
	}

	@Override
	public Boolean isOpenStage(Long idStage) {
		return medicalMentalAssessmentDao.verifyOpenStage(idStage);
	}

	@Override
	public boolean isCVSStages(MdclMentalAssmtReq mdclMentalAssmtReq) {
		if (ServiceConstants.CPGRMS_CPS.equals(mdclMentalAssmtReq.getStageProgram())
				&& (ServiceConstants.CSTAGES_FRE.equals(mdclMentalAssmtReq.getStage())
						|| ServiceConstants.CSTAGES_FSU.equals(mdclMentalAssmtReq.getStage())
						|| ServiceConstants.CSTAGES_SUB.equals(mdclMentalAssmtReq.getStage()))) {
			return true;
		} else

		{
			return false;
		}
	}
}
