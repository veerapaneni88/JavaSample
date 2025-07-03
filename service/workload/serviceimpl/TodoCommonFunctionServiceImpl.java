package us.tx.state.dfps.service.workload.serviceimpl;

import java.util.Date;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionInputDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionOutputDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.UnitDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.SupervisorDto;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.dto.TodoInfoCommonDto;
import us.tx.state.dfps.service.workload.service.TodoCommonFunctionService;

@Service
@Transactional
public class TodoCommonFunctionServiceImpl implements TodoCommonFunctionService {

	@Autowired
	TodoDao todoDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	WorkLoadDao workLoadDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	UnitDao unitDao;

	/**
	 * This service performs the business logic neccessary to execute the TODO
	 * COMMON FUNCTION. This service will help standardize/formalize how Todo's
	 * are created. It will prevent hard-coding, of Todo descriptions, due
	 * dates, and other data within the functional program.
	 * 
	 * Service Name: CSUB40U
	 * 
	 * @param todoCommonFunctionReq
	 * @return @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TodoCommonFunctionOutputDto TodoCommonFunction(TodoCommonFunctionInputDto todoCommonFunctionInputDto) {
		TodoCommonFunctionOutputDto todoCommonFunctionOutputDto = new TodoCommonFunctionOutputDto();
		TodoCommonFunctionDto todoCommonFunctionDto = null;
		Date date = new Date();
		Date nullDate = null;
		TodoInfoCommonDto todoInfoCommonDto = null;
		StageDto stageDto = null;
		TodoDto todoDto = new TodoDto();
		TodoDto newTodoDto = null;
		SupervisorDto supervisorDto = null;
		String cdStagePersRole = ServiceConstants.EMPTY_STRING;
		Long idPerson = ServiceConstants.ZERO_VAL;
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		Date dtTempDate = null;
		Date dtTempDateReview = null;
		String shiftDays = ServiceConstants.EMPTY_STRING;
		String shiftMonths = ServiceConstants.EMPTY_STRING;
		String shiftYears = ServiceConstants.EMPTY_STRING;
		String txtTodoInfoDesc = ServiceConstants.EMPTY_STRING;
		UnitEmpLinkDto unitEmpLinkDto = null;
		try {
			if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionInputDto)) {
				if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionInputDto.getTodoCommonFunctionDto())) {
					todoCommonFunctionDto = todoCommonFunctionInputDto.getTodoCommonFunctionDto();
					if (TypeConvUtil.isNullOrEmpty(
							todoCommonFunctionInputDto.getTodoCommonFunctionDto().getDtSysDtTodoCfDueFrom())) {
						todoCommonFunctionDto.setDtSysDtTodoCfDueFrom(date);
					}
					if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysCdTodoCf())) {
						// CSES08D
						todoInfoCommonDto = todoDao.getTodoInfoByTodoInfo(todoCommonFunctionDto.getSysCdTodoCf());
					}
					if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto)) {
						if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getIndTodoInfoEnabled())) {
							if (todoInfoCommonDto.getIndTodoInfoEnabled().equals(ServiceConstants.STRING_IND_Y)) {
								if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysIdTodoCfStage())) {
									// CINT40D
									stageDto = stageDao.getStageById(todoCommonFunctionDto.getSysIdTodoCfStage());
									if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
										if (!TypeConvUtil.isNullOrEmpty(stageDto.getIdCase())) {
											todoDto.setIdTodoCase(stageDto.getIdCase());
										}
										if (!TypeConvUtil.isNullOrEmpty(stageDto.getNmStage())) {
											todoDto.setNmStage(stageDto.getNmStage());
										}
									}
									if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysIdTodoCfPersWkr())) {
										todoDto.setIdTodoPersWorker(todoCommonFunctionDto.getSysIdTodoCfPersWkr());
									} else {
										if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
											if (!TypeConvUtil.isNullOrEmpty(stageDto.getIndStageClose())) {
												if (stageDto.getIndStageClose().equals(ServiceConstants.CLOSE_STAGE)) {
													if (!TypeConvUtil.isNullOrEmpty(stageDto.getDtStageClose())) {
														cdStagePersRole = ServiceConstants.HIST_PRIM_ROLE_STAGE_CLOSE;
													}
												} else {
													cdStagePersRole = ServiceConstants.PRIMARY_ROLE_STAGE_OPEN;
												}
											}
										}
										// CINV51D
										if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysIdTodoCfStage())
												&& !TypeConvUtil.isNullOrEmpty(cdStagePersRole)) {
											idPerson = workLoadDao.getPersonIdByRole(
													todoCommonFunctionDto.getSysIdTodoCfStage(), cdStagePersRole);
										}
										if (!TypeConvUtil.isNullOrEmpty(idPerson)) {
											todoDto.setIdTodoPersWorker(idPerson);
										}
									}
								}
								archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
								if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getIdTodoInfo())) {
									todoDto.setIdTodoInfo(todoInfoCommonDto.getIdTodoInfo());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getCdTodoInfoTask())) {
									todoDto.setCdTodoTask(todoInfoCommonDto.getCdTodoInfoTask());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getCdTodoInfoType())) {
									todoDto.setCdTodoType(todoInfoCommonDto.getCdTodoInfoType());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysIdTodoCfEvent())) {
									todoDto.setIdTodoEvent(todoCommonFunctionDto.getSysIdTodoCfEvent());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysIdTodoCfStage())) {
									todoDto.setIdTodoStage(todoCommonFunctionDto.getSysIdTodoCfStage());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysTxtTodoCfDesc())) {
									todoDto.setTodoDesc(todoCommonFunctionDto.getSysTxtTodoCfDesc());
									if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysCdTodoCf())) {
										if (todoCommonFunctionDto.getSysCdTodoCf()
												.equals(ServiceConstants.TODO_INFO_KIN001)) {
											if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDesc())) {
												todoDto.setTodoDesc(todoInfoCommonDto.getTodoInfoDesc().replaceAll(
														ServiceConstants.TODO_INFO_TOKEN_NM_RESOURCE,
														todoCommonFunctionDto.getSysTxtTodoCfDesc()));
											}
										}
										if (todoCommonFunctionDto.getSysCdTodoCf()
												.equals(ServiceConstants.TODO_INFO_FAD046)) {
											if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDesc())) {
												todoDto.setTodoDesc(todoInfoCommonDto.getTodoInfoDesc().replaceAll(
														ServiceConstants.TODO_INFO_TOKEN_NM_RESOURCE,
														todoCommonFunctionDto.getSysTxtTodoCfDesc()));
											}
										}
									}
								} else {
									if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDesc())) {
										todoDto.setTodoDesc(todoInfoCommonDto.getTodoInfoDesc());
									}
								}
								if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysTxtTodoCfLongDesc())) {
									todoDto.setTodoLongDesc(todoCommonFunctionDto.getSysTxtTodoCfLongDesc());
								} else {
									if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoLongDesc())) {
										todoDto.setTodoLongDesc(todoInfoCommonDto.getTodoInfoLongDesc());
									}
								}
								if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getDtSysDtTodoCfDueFrom())) {
									dtTempDate = todoCommonFunctionDto.getDtSysDtTodoCfDueFrom();
								}
								if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysCdTodoCf())) {
									if (todoCommonFunctionDto.getSysCdTodoCf().equals(ServiceConstants.TODO_INFO_SUB015)
											|| todoCommonFunctionDto.getSysCdTodoCf()
													.equals(ServiceConstants.TODO_INFO_SUB016)
											|| todoCommonFunctionDto.getSysCdTodoCf()
													.equals(ServiceConstants.TODO_INFO_ADO015)) {
										if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueDD())
												&& !TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueYY())
												&& !TypeConvUtil.isNullOrEmpty(dtTempDate)) {
											dtTempDate = DateUtils.addToDate(dtTempDate,
													todoInfoCommonDto.getTodoInfoDueDD(), ServiceConstants.NEGATIVE_TWO,
													todoInfoCommonDto.getTodoInfoDueYY());
											shiftDays = todoInfoCommonDto.getTodoInfoDueDD();
											shiftMonths = ServiceConstants.POSITIVE_ONE;
											shiftYears = todoInfoCommonDto.getTodoInfoDueYY();
										}
									} else {
										if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueDD())
												&& !TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueYY())
												&& !TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueMM())) {
											shiftDays = todoInfoCommonDto.getTodoInfoDueDD();
											shiftMonths = todoInfoCommonDto.getTodoInfoDueMM();
											shiftYears = todoInfoCommonDto.getTodoInfoDueYY();
										}
									}
								}
								if (!TypeConvUtil.isNullOrEmpty(dtTempDate) && !TypeConvUtil.isNullOrEmpty(shiftDays)
										&& !TypeConvUtil.isNullOrEmpty(shiftMonths)
										&& !TypeConvUtil.isNullOrEmpty(shiftYears)) {
									dtTempDate = DateUtils.addToDate(dtTempDate, shiftDays, shiftMonths, shiftYears);
								}
								if (!TypeConvUtil.isNullOrEmpty(dtTempDate)) {
									todoDto.setDtTodoDue(dtTempDate);
								}
								if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysCdTodoCf())) {
									if (todoCommonFunctionDto.getSysCdTodoCf()
											.equals(ServiceConstants.TODO_INFO_APS005)) {
										if (!TypeConvUtil.isNullOrEmpty(dtTempDate)
												&& !TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDesc())) {
											todoDto.setTodoDesc(todoInfoCommonDto.getTodoInfoDesc().replaceAll(
													ServiceConstants.TODO_INFO_TOKEN_DATE_DUE,
													DateUtils.stringDate(dtTempDate)));
										}
									}
									if (todoCommonFunctionDto.getSysCdTodoCf()
											.equals(ServiceConstants.TODO_INFO_APS006)) {
										if (!TypeConvUtil.isNullOrEmpty(dtTempDate)
												&& !TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDesc())) {
											todoDto.setTodoDesc(todoInfoCommonDto.getTodoInfoDesc().replaceAll(
													ServiceConstants.TODO_INFO_TOKEN_DATE_DUE,
													DateUtils.stringDate(dtTempDate)));
										}
									}
									if (todoCommonFunctionDto.getSysCdTodoCf().equals(ServiceConstants.TODO_INFO_SUB015)
											|| todoCommonFunctionDto.getSysCdTodoCf()
													.equals(ServiceConstants.TODO_INFO_SUB016)
											|| todoCommonFunctionDto.getSysCdTodoCf()
													.equals(ServiceConstants.TODO_INFO_SUB022)) {
										if (!TypeConvUtil
												.isNullOrEmpty(todoCommonFunctionDto.getDtSysDtTodoCfDueFrom())) {
											dtTempDateReview = todoCommonFunctionDto.getDtSysDtTodoCfDueFrom();
											todoDto.setDtTodoTaskDue(todoCommonFunctionDto.getDtSysDtTodoCfDueFrom());
										}
										if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDesc())) {
											txtTodoInfoDesc = todoInfoCommonDto.getTodoInfoDesc();
											if (!TypeConvUtil.isNullOrEmpty(dtTempDateReview)) {
												txtTodoInfoDesc = todoInfoCommonDto.getTodoInfoDesc().replace(
														ServiceConstants.DATE_PLACE_HOLDER,
														DateUtils.stringDt(dtTempDateReview));
											}
										}
										if (!TypeConvUtil.isNullOrEmpty(txtTodoInfoDesc)) {
											if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
												if (!TypeConvUtil.isNullOrEmpty(stageDto.getNmStage())) {
													txtTodoInfoDesc = txtTodoInfoDesc.replace(
															ServiceConstants.NAME_PLACE_HOLDER, stageDto.getNmStage());
												}
											}
										}
										if (!TypeConvUtil.isNullOrEmpty(txtTodoInfoDesc)) {
											todoDto.setTodoDesc(txtTodoInfoDesc);
										}
									}
									if (todoCommonFunctionDto.getSysCdTodoCf()
											.equals(ServiceConstants.TODO_INFO_05_CODE)
											|| todoCommonFunctionDto.getSysCdTodoCf().equals(ServiceConstants.TODO_CODE)
											|| todoCommonFunctionDto.getSysCdTodoCf()
													.equals(ServiceConstants.TODO_INFO_37_CODE)) {
										if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDesc())) {
											if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
												if (!TypeConvUtil.isNullOrEmpty(stageDto.getNmStage())) {
													todoDto.setTodoDesc(todoInfoCommonDto.getTodoInfoDesc().replace(
															ServiceConstants.NAME_PLACE_HOLDER, stageDto.getNmStage()));
												}
											}
										}
									}
								}
								todoDto.setDtTodoCreated(date);
								todoDto.setDtTodoCompleted(nullDate);
								if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getCdTodoInfoType())) {
									if (todoInfoCommonDto.getCdTodoInfoType().equals(ServiceConstants.CD_TODO_TASK)) {
										todoDto.setIdTodoPersCreator(ServiceConstants.ZERO_VAL);
										if (!TypeConvUtil
												.isNullOrEmpty(todoCommonFunctionDto.getDtSysDtTodoCfDueFrom())) {
											dtTempDate = todoCommonFunctionDto.getDtSysDtTodoCfDueFrom();
										}
										if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueDD())
												&& !TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueYY())
												&& !TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueMM())
												&& !TypeConvUtil.isNullOrEmpty(dtTempDate)) {
											dtTempDate = DateUtils.addToDate(dtTempDate,
													todoInfoCommonDto.getTodoInfoDueDD(),
													todoInfoCommonDto.getTodoInfoDueMM(),
													todoInfoCommonDto.getTodoInfoDueYY());
										}
										if (!TypeConvUtil.isNullOrEmpty(dtTempDate)) {
											todoDto.setDtTodoDue(dtTempDate);
										}
									} else {
										if (!TypeConvUtil
												.isNullOrEmpty(todoCommonFunctionDto.getSysIdTodoCfPersCrea())) {
											todoDto.setIdTodoPersCreator(
													todoCommonFunctionDto.getSysIdTodoCfPersCrea());
											if (!todoCommonFunctionDto.getDayCareAbsInd()) {
												todoDto.setDtTodoDue(nullDate);
											}
										}
									}
								}
								if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysCdTodoCf())) {
									if (todoCommonFunctionDto.getSysCdTodoCf().equals(ServiceConstants.TODO_INFO_LEG001)
											|| todoCommonFunctionDto.getSysCdTodoCf()
													.equals(ServiceConstants.TODO_INFO_MED002)
											|| todoCommonFunctionDto.getSysCdTodoCf()
													.equals(ServiceConstants.TODO_INFO_MED003)) {
										if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getCdTask())) {
											todoDto.setCdTodoTask(todoCommonFunctionDto.getCdTask());
										}
										if (!TypeConvUtil
												.isNullOrEmpty(todoCommonFunctionDto.getDtSysDtTodoCfDueFrom())) {
											dtTempDate = todoCommonFunctionDto.getDtSysDtTodoCfDueFrom();
											todoDto.setDtTodoTaskDue(todoCommonFunctionDto.getDtSysDtTodoCfDueFrom());
										}
										if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueDD())
												&& !TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueYY())
												&& !TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueMM())
												&& !TypeConvUtil.isNullOrEmpty(dtTempDate)) {
											dtTempDate = DateUtils.addToDate(dtTempDate,
													todoInfoCommonDto.getTodoInfoDueDD(),
													todoInfoCommonDto.getTodoInfoDueMM(),
													todoInfoCommonDto.getTodoInfoDueYY());
										}
										if (!TypeConvUtil.isNullOrEmpty(dtTempDate)) {
											todoDto.setDtTodoDue(dtTempDate);
										}
										if (!TypeConvUtil.isNullOrEmpty(todoDto.getDtTodoDue())) {
											if (todoDto.getDtTodoDue().before(date)) {
												todoDto.setDtTodoDue(date);
											}
										}
										//Defect-13713- Required function is checked as ADD or A.
										if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionInputDto.getReqFuncCd())) {
											if (!(todoCommonFunctionInputDto.getReqFuncCd()
													.equals(ServiceConstants.REQ_IND_AUD_ADD) || todoCommonFunctionInputDto.getReqFuncCd()
													.equals(ServiceConstants.REQ_FUNC_CD_ADD))) {
												
												archInputDto.setReqFuncCd(todoCommonFunctionInputDto.getReqFuncCd());
												if (!TypeConvUtil
														.isNullOrEmpty(todoCommonFunctionDto.getTsLastUpdate())) {
													todoDto.setDtLastUpdate(todoCommonFunctionDto.getTsLastUpdate());
												}
												if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getIdTodo())) {
													todoDto.setIdTodo(todoCommonFunctionDto.getIdTodo());
												}
												if (!TypeConvUtil
														.isNullOrEmpty(todoCommonFunctionDto.getIdTodoInfo())) {
													todoDto.setIdTodoInfo(todoCommonFunctionDto.getIdTodoInfo());
												}
												if (!TypeConvUtil
														.isNullOrEmpty(todoCommonFunctionDto.getDtTodoCreated())) {
													todoDto.setDtTodoCreated(todoCommonFunctionDto.getDtTodoCreated());
												}
												if (!TypeConvUtil
														.isNullOrEmpty(todoCommonFunctionDto.getDtTodoCompleted())) {
													todoDto.setDtTodoCompleted(
															todoCommonFunctionDto.getDtTodoCompleted());
												}
											}
										}
									}
									if (todoCommonFunctionDto.getSysCdTodoCf()
											.equals(ServiceConstants.TODO_INFO_ADP_002)
											|| todoCommonFunctionDto.getSysCdTodoCf()
													.equals(ServiceConstants.TODO_INFO_ADP_003)) {
										if (!TypeConvUtil
												.isNullOrEmpty(todoCommonFunctionDto.getDtSysDtTodoCfDueFrom())) {
											dtTempDate = todoCommonFunctionDto.getDtSysDtTodoCfDueFrom();
										}
										if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueDD())
												&& !TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueYY())
												&& !TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getTodoInfoDueMM())
												&& !TypeConvUtil.isNullOrEmpty(dtTempDate)) {
											dtTempDate = DateUtils.addToDate(dtTempDate,
													todoInfoCommonDto.getTodoInfoDueDD(),
													todoInfoCommonDto.getTodoInfoDueMM(),
													todoInfoCommonDto.getTodoInfoDueYY());
										}
										if (!TypeConvUtil.isNullOrEmpty(dtTempDate)) {
											todoDto.setDtTodoDue(dtTempDate);
										}
									}
								}
								if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysIdTodoCfPersAssgn())) {
									todoDto.setIdTodoPersAssigned(todoCommonFunctionDto.getSysIdTodoCfPersAssgn());
									// CCMN43D
									newTodoDto = todoDao.todoAUD(todoDto, archInputDto);
									if (!TypeConvUtil.isNullOrEmpty(newTodoDto)) {
										if (!TypeConvUtil.isNullOrEmpty(todoCommonFunctionDto.getSysCdTodoCf())
												&& !TypeConvUtil
														.isNullOrEmpty(todoCommonFunctionInputDto.getReqFuncCd())) {
											//Defect-13713- Required function is checked as ADD or A.
											if (todoCommonFunctionDto.getSysCdTodoCf()
													.equals(ServiceConstants.TODO_INFO_LEG001)
													&& (todoCommonFunctionInputDto.getReqFuncCd()
															.equals(ServiceConstants.REQ_IND_AUD_ADD) || todoCommonFunctionInputDto.getReqFuncCd()
															.equals(ServiceConstants.REQ_FUNC_CD_ADD))) {  
												if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersWorker())) {
													// CCMN60D
													supervisorDto = personDao
															.getSupervisorByPersonId(todoDto.getIdTodoPersWorker());
												}
												if (!TypeConvUtil.isNullOrEmpty(supervisorDto)) {
													if (!TypeConvUtil.isNullOrEmpty(supervisorDto.getIdPerson())) {
														todoDto.setIdTodoPersAssigned(supervisorDto.getIdPerson());
														if (!TypeConvUtil.isNullOrEmpty(
																todoCommonFunctionDto.getSysIdTodoCfPersAssgn())) {
															if (!supervisorDto.getIdPerson().equals(
																	todoCommonFunctionDto.getSysIdTodoCfPersAssgn())) {
																// CCMN43D
																todoDao.todoAUD(todoDto, archInputDto);
															}
														}
													}
												}
											}
										}
									}
								} else if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getCdTodoInfoPersAssigned())
										&& todoInfoCommonDto.getCdTodoInfoPersAssigned()
												.equals(ServiceConstants.CD_MEMBER)) {
									if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersWorker())) {
										todoDto.setIdTodoPersAssigned(todoDto.getIdTodoPersWorker());
									}
									// CCMN43D
									todoDao.todoAUD(todoDto, archInputDto);
								} else if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getCdTodoInfoPersAssigned())
										&& todoInfoCommonDto.getCdTodoInfoPersAssigned()
												.equals(ServiceConstants.CD_LEAD)) {
									if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersWorker())) {
										// CCMN60D
										supervisorDto = personDao
												.getSupervisorByPersonId(todoDto.getIdTodoPersWorker());
									}
									if (!TypeConvUtil.isNullOrEmpty(supervisorDto)) {
										if (!TypeConvUtil.isNullOrEmpty(supervisorDto.getIdPerson())) {
											todoDto.setIdTodoPersAssigned(supervisorDto.getIdPerson());
										}
										// CCMN43D
										newTodoDto = todoDao.todoAUD(todoDto, archInputDto);
										if (!TypeConvUtil.isNullOrEmpty(newTodoDto)) {
											if (!TypeConvUtil.isNullOrEmpty(todoInfoCommonDto.getCdTodoInfoTask())
													&& todoInfoCommonDto.getCdTodoInfoTask()
															.equals(ServiceConstants.SVC_CD_TASK_CONTACT_APS)) {
												if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersWorker())) {
													todoDto.setIdTodoPersAssigned(todoDto.getIdTodoPersWorker());
												}
												// CCMN43D
												todoDao.todoAUD(todoDto, archInputDto);
											}
										}
									} else {
										if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersWorker())) {
											todoDto.setIdTodoPersAssigned(todoDto.getIdTodoPersWorker());
										}
										// CCMN43D
										todoDao.todoAUD(todoDto, archInputDto);
									}
								} else {
									if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersWorker()) && !TypeConvUtil
											.isNullOrEmpty(todoInfoCommonDto.getCdTodoInfoPersAssigned())) {
										// CSEC04D
										unitEmpLinkDto = unitDao.getUnitEmpLinkByIdPersonRole(
												todoInfoCommonDto.getCdTodoInfoPersAssigned(),
												todoDto.getIdTodoPersWorker());
									}
									if (!TypeConvUtil.isNullOrEmpty(unitEmpLinkDto)) {
										if (!TypeConvUtil.isNullOrEmpty(unitEmpLinkDto.getIdPerson())) {
											todoDto.setIdTodoPersAssigned(unitEmpLinkDto.getIdPerson());
										}
										// CCMN43D
										todoDao.todoAUD(todoDto, archInputDto);
									} else {
										if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersWorker())) {
											todoDto.setIdTodoPersAssigned(todoDto.getIdTodoPersWorker());
										}
										// CCMN43D
										todoDao.todoAUD(todoDto, archInputDto);
									}
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getCdTodoTask())) {
									todoCommonFunctionOutputDto.setCdTodoTask(todoDto.getCdTodoTask());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getCdTodoType())) {
									todoCommonFunctionOutputDto.setCdTodoType(todoDto.getCdTodoType());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getDtTodoCompleted())) {
									todoCommonFunctionOutputDto.setDtTaskDue(todoDto.getDtTodoCompleted());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getDtTodoCreated())) {
									todoCommonFunctionOutputDto.setDtTodoCompleted(todoDto.getDtTodoCreated());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getDtTodoDue())) {
									todoCommonFunctionOutputDto.setDtTodoCreated(todoDto.getDtTodoDue());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getDtTodoTaskDue())) {
									todoCommonFunctionOutputDto.setDtTodoDue(todoDto.getDtTodoTaskDue());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoCase())) {
									todoCommonFunctionOutputDto.setIdCase(todoDto.getIdTodoCase());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoEvent())) {
									todoCommonFunctionOutputDto.setIdEvent(todoDto.getIdTodoEvent());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoStage())) {
									todoCommonFunctionOutputDto.setIdStage(todoDto.getIdTodoStage());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodo())) {
									todoCommonFunctionOutputDto.setIdTodo(todoDto.getIdTodo());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersAssigned())) {
									todoCommonFunctionOutputDto.setIdTodoPersAssigned(todoDto.getIdTodoPersAssigned());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersCreator())) {
									todoCommonFunctionOutputDto.setIdTodoPersCreator(todoDto.getIdTodoPersCreator());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersWorker())) {
									todoCommonFunctionOutputDto.setIdTodoPersWorker(todoDto.getIdTodoPersWorker());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getTodoDesc())) {
									todoCommonFunctionOutputDto.setTodoDesc(todoDto.getTodoDesc());
								}
								if (!TypeConvUtil.isNullOrEmpty(todoDto.getTodoLongDesc())) {
									todoCommonFunctionOutputDto.setTodoLongDesc(todoDto.getTodoLongDesc());
								}
							}
						}
					}
				}
			}
		} catch (HibernateException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.toString());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}
		return todoCommonFunctionOutputDto;
	}
}
