package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
import us.tx.state.dfps.service.admin.dto.EventStageDoDto;
import us.tx.state.dfps.service.admin.dto.FetchToDoDto;
import us.tx.state.dfps.service.admin.dto.FetchToDodiDto;
import us.tx.state.dfps.service.admin.dto.PersonDiDto;
import us.tx.state.dfps.service.admin.dto.PersonDoDto;
import us.tx.state.dfps.service.admin.dto.StageDetailsDiDto;
import us.tx.state.dfps.service.admin.dto.StageDetailsDoDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateOutDto;
import us.tx.state.dfps.service.admin.service.TodoCreateService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * performs the business logic neccessary to execute the TODO COMMON FUNCTION
 * Aug 22, 2017- 8:50:49 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
@Transactional
public class TodoCreateServiceImpl implements TodoCreateService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FetchToDoDao objCses08dDao;

	@Autowired
	StageDetailsDao objCint40dDao;

	@Autowired
	AdminWorkerDao objCinv51dDao;

	@Autowired
	TodoCreateDao objCcmn43dDao;

	@Autowired
	PersonInfoDao objCcmn60dDao;

	@Autowired
	UnitEmpLinkDao objCsec04dDao;

	private static final Logger log = Logger.getLogger(TodoCreateServiceImpl.class);

	/**
	 * Method Name: ccmn16dQUERYdam Method Description: This Method does insert
	 * and delete operation
	 * 
	 * @param pInputMsg
	 * @return TodoCreateOutDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public TodoCreateOutDto TodoCommonFunction(TodoCreateInDto pInputMsg) {
		log.debug("Entering method TodoCommonFunction in TodoCreateServiceImpl");
		String szStageName = null;
		Date dtTempDateReview;
		Date dtDtSystemDate;
		Date dtNullDate;
		Date dtTempDate;
		Date dtShiftDate = null;
		TodoCreateOutDto todoCreateOutDto = new TodoCreateOutDto();
		FetchToDodiDto pCSES08DInputRec = new FetchToDodiDto();
		FetchToDoDto pCSES08DOutputRec = new FetchToDoDto();
		StageDetailsDiDto pCINT40DInputRec = new StageDetailsDiDto();
		StageDetailsDoDto pCINT40DOutputRec = new StageDetailsDoDto();
		AdminWorkerInpDto pCINV51DInputRec = new AdminWorkerInpDto();
		AdminWorkerOutpDto pCINV51DOutputRec = new AdminWorkerOutpDto();
		PersonDiDto pCCMN60DInputRec = new PersonDiDto();
		PersonDoDto pCCMN60DOutputRec = new PersonDoDto();
		EmpLinkInDto pCSEC04DInputRec = new EmpLinkInDto();
		EmpLinkOutDto pCSEC04DOutputRec = new EmpLinkOutDto();
		EventStageDiDto pCCMN43DInputRec = new EventStageDiDto();
		EventStageDoDto pCCMN43DOutputRec = new EventStageDoDto();
		dtNullDate = new Date();
		dtDtSystemDate = new Date();
		if (TypeConvUtil.isNullOrEmpty(pInputMsg.getMergeSplitToDoDto().getDtTodoCfDueFrom())) {
			pInputMsg.setDtSysDtTodoCfDueFrom(dtDtSystemDate);
		}

		pCSES08DInputRec.setCdTodoInfo(pInputMsg.getMergeSplitToDoDto().getCdTodoCf());
		List<FetchToDoDto> resource08 = objCses08dDao.getTodoInfo(pCSES08DInputRec);
		if (CollectionUtils.isNotEmpty(resource08)) {
			pCSES08DOutputRec = (FetchToDoDto) resource08.get(0);
			if (ServiceConstants.INDICATOR_YES1.equalsIgnoreCase(pCSES08DOutputRec.getIndTodoInfoEnabled())) {
				if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getMergeSplitToDoDto().getIdTodoCfStage())) {
					pCINT40DInputRec.setIdStage(pInputMsg.getMergeSplitToDoDto().getIdTodoCfStage());
					List<StageDetailsDoDto> resource40 = objCint40dDao.getStageDtls(pCINT40DInputRec);
					if (CollectionUtils.isNotEmpty(resource40)) {
						pCINT40DOutputRec = (StageDetailsDoDto) resource40.get(0);
						if (!TypeConvUtil.isNullOrEmpty(pCINT40DOutputRec.getIdCase())) {
							pCCMN43DInputRec.setUlIdCase(pCINT40DOutputRec.getIdCase());
						}
						szStageName = pCINT40DOutputRec.getNmStage();
					}
					if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getMergeSplitToDoDto().getIdTodoCfPersWkr())) {
						pCCMN43DInputRec.setUlIdTodoPersWorker(pInputMsg.getMergeSplitToDoDto().getIdTodoCfPersWkr());
					} else {

						pCINV51DInputRec.setIdStage(pInputMsg.getMergeSplitToDoDto().getIdTodoCfStage());
						if (ServiceConstants.CLOSE_STAGE.equalsIgnoreCase(pCINT40DOutputRec.getIndStageClose())) {
							if (!TypeConvUtil.isNullOrEmpty(pCINT40DOutputRec.getDtStageClose())) {
								pCINV51DInputRec.setCdStagePersRole(ServiceConstants.HIST_PRIM_ROLE_STAGE_CLOSE);
							}
						} else {
							pCINV51DInputRec.setCdStagePersRole(ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
						}

						List<AdminWorkerOutpDto> resource51 = objCinv51dDao.getWorkLoad(pCINV51DInputRec);
						if (CollectionUtils.isNotEmpty(resource51)) {
							pCINV51DOutputRec = (AdminWorkerOutpDto) resource51.get(0);
							if (!TypeConvUtil.isNullOrEmpty(pCINV51DOutputRec.getIdTodoPersAssigned())) {
								pCCMN43DInputRec.setUlIdTodoPersWorker(pCINV51DOutputRec.getIdTodoPersAssigned());
							}
						}

					}
				}
				pCCMN43DInputRec.setCReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				if (!TypeConvUtil.isNullOrEmpty(pCSES08DOutputRec.getIdTodoInfo())) {
					pCCMN43DInputRec.setUlIdTodoInfo(pCSES08DOutputRec.getIdTodoInfo());
				}
				if (!TypeConvUtil.isNullOrEmpty(pCSES08DOutputRec.getCdTodoInfoTask())) {
					pCCMN43DInputRec.setSzCdTodoTask(pCSES08DOutputRec.getCdTodoInfoTask());
				} else {
					pCCMN43DInputRec.setSzCdTodoTask(pInputMsg.getMergeSplitToDoDto().getCdTask());
				}
				if (!TypeConvUtil.isNullOrEmpty(pCSES08DOutputRec.getCdTodoInfoType())) {
					pCCMN43DInputRec.setSzCdTodoType(pCSES08DOutputRec.getCdTodoInfoType());
				}
				if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getMergeSplitToDoDto().getIdTodoCfEvent())) {
					pCCMN43DInputRec.setUlIdEvent(pInputMsg.getMergeSplitToDoDto().getIdTodoCfEvent());
				}
				if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getMergeSplitToDoDto().getIdTodoCfStage())) {
					pCCMN43DInputRec.setUlIdStage(pInputMsg.getMergeSplitToDoDto().getIdTodoCfStage());
				}
				if (!StringUtils.isEmpty(pInputMsg.getSysTxtTodoCfDesc())) {
					pCCMN43DInputRec.setSzTxtTodoDesc(pInputMsg.getSysTxtTodoCfDesc());
					if (ServiceConstants.TODO_INFO_KIN001.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())) {
						String buffer;
						buffer = replace_str(pCSES08DOutputRec.getTxtTodoInfoLongDesc(), "{NM RESOURCE}",
								pInputMsg.getSysTxtTodoCfDesc());
						pCCMN43DInputRec.setSzTxtTodoDesc(buffer);
					}
					if (ServiceConstants.TODO_INFO_FAD046.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())) {
						String buffer;
						buffer = replace_str(pCSES08DOutputRec.getTxtTodoInfoLongDesc(), "{NM RESOURCE}",
								pInputMsg.getSysTxtTodoCfDesc());
						pCCMN43DInputRec.setSzTxtTodoDesc(buffer);
					}
				} else {
					pCCMN43DInputRec.setSzTxtTodoDesc(pCSES08DOutputRec.getTxtTodoInfoDesc());
				}
				if (!StringUtils.isEmpty(pInputMsg.getMergeSplitToDoDto().getTodoCfDesc())) {
					pCCMN43DInputRec.setSzTxtTodoDesc(pInputMsg.getMergeSplitToDoDto().getTodoCfDesc());
					pCCMN43DInputRec.setTxtTodoLongDesc(pInputMsg.getMergeSplitToDoDto().getTodoCfDesc());
				} else {
					pCCMN43DInputRec.setTxtTodoLongDesc(pCSES08DOutputRec.getTxtTodoInfoLongDesc());
				}
				// dtTempDate = pInputMsg.getDtSysDtTodoCfDueFrom();
				/*
				 * if
				 * (ServiceConstants.TODO_INFO_SUB015.equalsIgnoreCase(pInputMsg
				 * .getSysCdTodoCf()) ||
				 * ServiceConstants.TODO_INFO_SUB016.equalsIgnoreCase(pInputMsg.
				 * getSysCdTodoCf()) ||
				 * ServiceConstants.TODO_INFO_ADO015.equalsIgnoreCase(pInputMsg.
				 * getSysCdTodoCf())) {
				 * 
				 * dtShiftDate=pCSES08DOutputRec.getSNbrTodoInfoDueDd(); (Date
				 * FAR )dtTempDate = (Date FAR )dtShiftDate;
				 * dtShiftDate=pCSES08DOutputRec.getSNbrTodoInfoDueDd();
				 * 
				 * } else { //
				 * dtShiftDate=pCSES08DOutputRec.getSNbrTodoInfoDueDd(); }
				 */
				if (!TypeConvUtil.isNullOrEmpty(pCSES08DOutputRec.getNbrTodoInfoDueDd())
						&& !TypeConvUtil.isNullOrEmpty(pCSES08DOutputRec.getNbrTodoInfoDueMm())
						&& !TypeConvUtil.isNullOrEmpty(pCSES08DOutputRec.getNbrTodoInfoDueYy())) {
					Calendar c = Calendar.getInstance();
					c.set(pCSES08DOutputRec.getNbrTodoInfoDueYy(), pCSES08DOutputRec.getNbrTodoInfoDueMm() - 2,
							pCSES08DOutputRec.getNbrTodoInfoDueDd(), 0, 0);
					dtShiftDate = c.getTime();
				} else {
					dtShiftDate = new Date();
				}
				dtTempDate = (Date) dtShiftDate;
				if (!TypeConvUtil.isNullOrEmpty(dtTempDate)) {
					pCCMN43DInputRec.setDtDtTodoDue(dtTempDate);
				}
				if (ServiceConstants.TODO_INFO_APS005.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())) {
					String buffer;
					int date;
					date = dtTempDate.getMonth() + dtTempDate.getMonth() + dtTempDate.getMonth();
					buffer = replace_str(pCSES08DOutputRec.getTxtTodoInfoDesc(), "{DATE DUE}", Integer.toString(date));
					pCCMN43DInputRec.setSzTxtTodoDesc(buffer);
				}
				if (ServiceConstants.TODO_INFO_APS006.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())) {
					String buffer;
					int date;
					date = dtTempDate.getMonth() + dtTempDate.getMonth() + dtTempDate.getMonth();
					buffer = replace_str(pCSES08DOutputRec.getTxtTodoInfoDesc(), "{DATE DUE}", Integer.toString(date));
					pCCMN43DInputRec.setSzTxtTodoDesc(buffer);
				}
				if ((ServiceConstants.TODO_INFO_SUB015.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf()))
						|| (ServiceConstants.TODO_INFO_SUB016
								.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf()))
						|| (ServiceConstants.TODO_INFO_SUB022
								.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf()))) {
					String buffer;
					int date;
					dtTempDateReview = pInputMsg.getMergeSplitToDoDto().getDtTodoCfDueFrom();
					date = dtTempDateReview.getMonth() + dtTempDateReview.getMonth() + dtTempDateReview.getMonth();
					buffer = replace_str(pCSES08DOutputRec.getTxtTodoInfoDesc(), ServiceConstants.DATE_PLACE_HOLDER,
							Integer.toString(date));
					buffer = replace_str(buffer, ServiceConstants.NAME_PLACE_HOLDER, szStageName);
					pCCMN43DInputRec.setSzTxtTodoDesc(buffer);
				}
				if ((ServiceConstants.TODO_INFO_05_CODE
						.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf()))
						|| (ServiceConstants.TODO_CODE.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf()))
						|| (ServiceConstants.TODO_INFO_37_CODE
								.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf()))) {
					String buffer;
					buffer = replace_str(pCSES08DOutputRec.getTxtTodoInfoDesc(), ServiceConstants.NAME_PLACE_HOLDER,
							szStageName);
					pCCMN43DInputRec.setSzTxtTodoDesc(buffer);
				}
				pCCMN43DInputRec.setDtDtTodoCreated(dtDtSystemDate);
				pCCMN43DInputRec.setDtDtTodoCompleted(pInputMsg.getMergeSplitToDoDto().getDtTodoCompleted());
				if (ServiceConstants.CD_TODO_TASK.equalsIgnoreCase(pCSES08DOutputRec.getCdTodoInfoType())) {
					pCCMN43DInputRec.setUlIdTodoPersCreator(ServiceConstants.ZERO_VAL);
					// dtShiftDate=pCSES08DOutputRec.getSNbrTodoInfoTaskDueDd();
					dtTempDate = pInputMsg.getMergeSplitToDoDto().getDtTodoCfDueFrom();
					// (Date)dtTempDate = (Date FAR )dtShiftDate;
					//Defect 11553: in case if the scheduled date already subtracted by 10 days
					if(pInputMsg.getSelectedScheduledCourtDate()!=null){
						pCCMN43DInputRec.setDtDtTaskDue(pInputMsg.getSelectedScheduledCourtDate());
					}
					else {
						pCCMN43DInputRec.setDtDtTaskDue(dtTempDate);
					}
				} else {
					pCCMN43DInputRec.setUlIdTodoPersCreator(pInputMsg.getMergeSplitToDoDto().getIdTodoCfPersCrea());
					pCCMN43DInputRec.setDtDtTaskDue(dtNullDate);
				}
				if (ServiceConstants.TODO_INFO_LEG001.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf())
						|| ServiceConstants.TODO_INFO_MED002
								.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf())
						|| ServiceConstants.TODO_INFO_MED003
								.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf())) {
					// pCCMN43DInputRec.setSzCdTodoTask(pInputMsg.getCSUB40UIG00().getSzCdTask());
					dtTempDate = pInputMsg.getMergeSplitToDoDto().getDtTodoCfDueFrom();
					// dtShiftDate=-pCSES08DOutputRec.getSNbrTodoInfoDueDd();
					// (Date FAR )dtTempDate = (Date FAR )dtShiftDate;
					//Defect 11553: in case if the scheduled date already subtracted by 10 days
					if(pInputMsg.getSelectedScheduledCourtDate()!=null){
						pCCMN43DInputRec.setDtDtTaskDue(pInputMsg.getSelectedScheduledCourtDate());
					}
					else {
						pCCMN43DInputRec.setDtDtTaskDue(pInputMsg.getMergeSplitToDoDto().getDtTodoCfDueFrom());
					}
					pCCMN43DInputRec.setDtDtTodoDue(dtTempDate);
					/*
					 * if(getDays(pCCMN43DInputRec.getDtDtTodoDue())<getDays(
					 * dtDtSystemDate)) {
					 * pCCMN43DInputRec.setDtDtTodoDue(dtDtSystemDate); }
					 */
				}
				if (ServiceConstants.TODO_INFO_ADP_002.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf())
						|| ServiceConstants.TODO_INFO_ADP_003
								.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf())) {
					dtTempDate = pInputMsg.getMergeSplitToDoDto().getDtTodoCfDueFrom();
					// dtShiftDate=-pCSES08DOutputRec.getSNbrTodoInfoDueDd();
					// (Date FAR )dtTempDate = (Date FAR )dtShiftDate;
					pCCMN43DInputRec.setDtDtTodoDue(dtTempDate);
				}
				if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getMergeSplitToDoDto().getIdTodoCfPersAssgn())) {
					pCCMN43DInputRec.setUlIdTodoPersAssigned(pInputMsg.getMergeSplitToDoDto().getIdTodoCfPersAssgn());
					pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
					/* Defect Fix# <No.13318> - Legal /Court hearing tasks assignment
					if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {
						if (ServiceConstants.TODO_INFO_LEG001
								.equalsIgnoreCase(pInputMsg.getMergeSplitToDoDto().getCdTodoCf())
								&& pInputMsg.getServiceInputDto().getCreqFuncCd()
										.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
							pCCMN60DInputRec.setIdPerson(pCCMN43DInputRec.getUlIdTodoPersWorker());
							List<PersonDoDto> resource60 = objCcmn60dDao.getPersonName(pCCMN60DInputRec);
							if (!TypeConvUtil.isNullOrEmpty(resource60)) {
								// pCCMN43DInputRec.setUlIdTodoPersAssigned(pCCMN60DOutputRec.getIdPerson());
								if ((!ObjectUtils.isEmpty(pCCMN60DOutputRec.getIdPerson()) && !pCCMN60DOutputRec
										.getIdPerson().equals(pInputMsg.getMergeSplitToDoDto().getIdTodoCfPersAssgn()))
										|| (ObjectUtils.isEmpty(pCCMN60DOutputRec.getIdPerson()) && !ObjectUtils
												.isEmpty(pInputMsg.getMergeSplitToDoDto().getIdTodoCfPersAssgn()))) {
									pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
								}
							}
						}
					} */
				} else if (ServiceConstants.CD_MEMBER.equalsIgnoreCase(pCSES08DOutputRec.getCdTodoInfoPersAssignd())) {
					pCCMN43DInputRec.setUlIdTodoPersAssigned(pCCMN43DInputRec.getUlIdTodoPersWorker());
					pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
					if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {

					}
				} else if (ServiceConstants.CD_LEAD.equalsIgnoreCase(pCSES08DOutputRec.getCdTodoInfoPersAssignd())) {
					pCCMN60DInputRec.setIdPerson(pCCMN43DInputRec.getUlIdTodoPersWorker());
					List<PersonDoDto> resource60 = objCcmn60dDao.getPersonName(pCCMN60DInputRec);
					if (CollectionUtils.isNotEmpty(resource60)) {
						pCCMN60DOutputRec = (PersonDoDto) resource60.get(0);
						pCCMN43DInputRec.setUlIdTodoPersAssigned(pCCMN60DOutputRec.getIdPerson());
						pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
						if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {
							if (ServiceConstants.SVC_CD_TASK_CONTACT_APS
									.equalsIgnoreCase(pCSES08DOutputRec.getCdTodoInfoTask())) {
								pCCMN43DInputRec.setUlIdTodoPersAssigned(pCCMN43DInputRec.getUlIdTodoPersWorker());
								pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
								if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {

								}
							}

						}
						/*[artf # artf130740] -  Defect Fix# <No.13318> - Legal /Court hearing tasks assignment
						pCCMN43DInputRec.setUlIdTodoPersAssigned(pCCMN43DInputRec.getUlIdTodoPersWorker());
						pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
						if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {

						}*/

					}
				} else {

					pCSEC04DInputRec.setIdPerson(!ObjectUtils.isEmpty(pCCMN43DInputRec.getUlIdTodoPersWorker())
							? pCCMN43DInputRec.getUlIdTodoPersWorker() : 0l);
					pCSEC04DInputRec.setCdUnitMemberRole(pCSES08DOutputRec.getCdTodoInfoPersAssignd());
					List<EmpLinkOutDto> resource04 = objCsec04dDao.getUnitEmpLinkRecord(pCSEC04DInputRec);
					if (CollectionUtils.isNotEmpty(resource04)) {
						pCSEC04DOutputRec = (EmpLinkOutDto) resource04.get(0);
						pCCMN43DInputRec.setUlIdTodoPersAssigned(pCSEC04DOutputRec.getIdPerson());
						pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
						if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {

						}

					}

				}
				todoCreateOutDto = mapCsub40DTO(todoCreateOutDto, pCCMN43DInputRec);
			}

		}

		log.debug("Exiting method TodoCommonFunction in TodoCreateServiceImpl");
		return todoCreateOutDto;
	}

	/**
	 *
	 * @param pCCMN43DInputRec
	 * @param pOutputMsg
	 * @return TodoCreateOutDto @
	 */
	private TodoCreateOutDto mapCsub40DTO(TodoCreateOutDto pOutputMsg, EventStageDiDto pCCMN43DInputRec) {
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getSzCdTodoTask())) {
			pOutputMsg.setCdTodoTask(pCCMN43DInputRec.getSzCdTodoTask());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getSzCdTodoType())) {
			pOutputMsg.setCdTodoType(pCCMN43DInputRec.getSzCdTodoType());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getDtDtTodoCompleted())) {
			pOutputMsg.setDtDtTaskDue(pCCMN43DInputRec.getDtDtTodoCompleted());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getDtDtTodoCreated())) {
			pOutputMsg.setDtDtTodoCompleted(pCCMN43DInputRec.getDtDtTodoCreated());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getDtDtTodoDue())) {
			pOutputMsg.setDtDtTodoCreated(pCCMN43DInputRec.getDtDtTodoDue());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getDtDtTaskDue())) {
			pOutputMsg.setDtDtTodoDue(pCCMN43DInputRec.getDtDtTaskDue());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getUlIdCase())) {
			pOutputMsg.setIdCase(pCCMN43DInputRec.getUlIdCase());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getUlIdEvent())) {
			pOutputMsg.setIdEvent(pCCMN43DInputRec.getUlIdEvent());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getUlIdStage())) {
			pOutputMsg.setIdStage(pCCMN43DInputRec.getUlIdStage());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getLdIdTodo())) {
			pOutputMsg.setIdTodo(pCCMN43DInputRec.getLdIdTodo());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getUlIdTodoPersAssigned())) {
			pOutputMsg.setIdTodoPersAssigned(pCCMN43DInputRec.getUlIdTodoPersAssigned());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getUlIdTodoPersCreator())) {
			pOutputMsg.setIdTodoPersCreator(pCCMN43DInputRec.getUlIdTodoPersCreator());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getUlIdTodoPersWorker())) {
			pOutputMsg.setIdTodoPersWorker(pCCMN43DInputRec.getUlIdTodoPersWorker());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getSzTxtTodoDesc())) {
			pOutputMsg.setTxtTodoDesc(pCCMN43DInputRec.getSzTxtTodoDesc());
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN43DInputRec.getTxtTodoLongDesc())) {
			pOutputMsg.setTxtTodoLongDesc(pCCMN43DInputRec.getTxtTodoLongDesc());
		}

		return pOutputMsg;
	}

	/**
	 *
	 * @param orig
	 * @param substr
	 * @param rep
	 * @return p @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String replace_str(String orig, String substr, String rep) {
		log.debug("Entering method replace_str in TodoCreateServiceImpl");
		String p = null;
		if (orig.contains(substr)) {
			p = orig.replace(substr, rep);
		}

		log.debug("Exiting method replace_str in TodoCreateServiceImpl");
		return p;
	}

	/**
	 *
	 * @param d
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void getDays(Date d) {
		log.debug("Entering method getDays in TodoCreateServiceImpl");
		long y, m;
		m = (d.getMonth() + 9) % 12;
		y = d.getYear() - m / 10;
		log.debug("Exiting method getDays in TodoCreateServiceImpl");
	}

	/**
	 *
	 * @param pInputMsg
	 * @return TodoCreateOutDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TodoCreateOutDto callCsub40uService(TodoCreateInDto pInputMsg) {
		log.debug("Entering method callCsub40uService in TodoCreateServiceImpl");
		TodoCreateOutDto pOutputMsg = new TodoCreateOutDto();
		pOutputMsg = TodoCommonFunction(pInputMsg);
		log.debug("Exiting method callCsub40uService in TodoCreateServiceImpl");
		return pOutputMsg;
	}

}
