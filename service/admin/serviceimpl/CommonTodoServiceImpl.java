package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.admin.dao.StageRegionDao;
import us.tx.state.dfps.service.admin.dao.TodoInfoDao;
import us.tx.state.dfps.service.admin.dao.TodoInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.UnitEmpLinkDao;
import us.tx.state.dfps.service.admin.dao.UnitEmpLinkPersonUnitSelDao;
import us.tx.state.dfps.service.admin.dao.WorkloadStgPerLinkSelDao;
import us.tx.state.dfps.service.admin.dto.CommonTodoInDto;
import us.tx.state.dfps.service.admin.dto.CommonTodoOutDto;
import us.tx.state.dfps.service.admin.dto.StageRegionInDto;
import us.tx.state.dfps.service.admin.dto.StageRegionOutDto;
import us.tx.state.dfps.service.admin.dto.TodoInfoInDto;
import us.tx.state.dfps.service.admin.dto.TodoInfoOutDto;
import us.tx.state.dfps.service.admin.dto.TodoInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.TodoInsUpdDelOutDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkInDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkOutDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkPersonUnitSelInDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkPersonUnitSelOutDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelInDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelOutDto;
import us.tx.state.dfps.service.admin.service.CommonTodoService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

@Service
@Transactional
public class CommonTodoServiceImpl implements CommonTodoService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	TodoInfoDao objCses08dDao;

	@Autowired
	StageRegionDao objCint40dDao;

	@Autowired
	WorkloadStgPerLinkSelDao objCinv51dDao;

	@Autowired
	TodoInsUpdDelDao objCcmn43dDao;

	@Autowired
	UnitEmpLinkPersonUnitSelDao objCcmn60dDao;

	@Autowired
	UnitEmpLinkDao objCsec04dDao;

	private static final Logger log = Logger.getLogger(CommonTodoServiceImpl.class);
	private static final String CD_TASK_SUB_PLCMNT_REQ = "9620";
	private static final String CD_TASK_ADO_PLCMNT_REQ = "9621";
	private static final String CD_TASK_SUB_PLCMNT_STTS = "9622";
	private static final String CD_TASK_ADO_PLCMNT_STTS = "9623";

	/**
	 *
	 * @param pInputMsg
	 * @return CommonTodoOutDto
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CommonTodoOutDto TodoCommonFunction(CommonTodoInDto pInputMsg) {
		log.debug("Entering method TodoCommonFunction in CommonTodoServiceImpl");
		String szStageName = null;
		Date dtTempDateReview;
		Date dtDtSystemDate;
		Date dtNullDate;
		Date dtTempDate = null;
		Date dtShiftDate = null;
		CommonTodoOutDto commonTodoOutDto = new CommonTodoOutDto();
		TodoInfoInDto pCSES08DInputRec = new TodoInfoInDto();
		TodoInfoOutDto pCSES08DOutputRec = new TodoInfoOutDto();
		StageRegionInDto pCINT40DInputRec = new StageRegionInDto();
		StageRegionOutDto pCINT40DOutputRec = new StageRegionOutDto();
		WorkloadStgPerLinkSelInDto pCINV51DInputRec = new WorkloadStgPerLinkSelInDto();
		WorkloadStgPerLinkSelOutDto pCINV51DOutputRec = new WorkloadStgPerLinkSelOutDto();
		UnitEmpLinkPersonUnitSelInDto pCCMN60DInputRec = new UnitEmpLinkPersonUnitSelInDto();
		UnitEmpLinkPersonUnitSelOutDto pCCMN60DOutputRec = new UnitEmpLinkPersonUnitSelOutDto();
		UnitEmpLinkInDto pCSEC04DInputRec = new UnitEmpLinkInDto();
		UnitEmpLinkOutDto pCSEC04DOutputRec = new UnitEmpLinkOutDto();
		TodoInsUpdDelInDto pCCMN43DInputRec = new TodoInsUpdDelInDto();
		TodoInsUpdDelOutDto pCCMN43DOutputRec = new TodoInsUpdDelOutDto();
		dtNullDate = new Date();
		dtDtSystemDate = new Date();
		if (TypeConvUtil.isNullOrEmpty(pInputMsg.getDtSysDtTodoCfDueFrom())) {
			pInputMsg.setDtSysDtTodoCfDueFrom(dtDtSystemDate);
		}
		pCSES08DInputRec.setCdTodoInfo(pInputMsg.getSysCdTodoCf());
		List<TodoInfoOutDto> resource08 = objCses08dDao.getTodoInfo(pCSES08DInputRec);
		if (!TypeConvUtil.isNullOrEmpty(resource08) && resource08.size() > 0) {
			pCSES08DOutputRec = (TodoInfoOutDto) resource08.get(0);
			if (ServiceConstants.INDICATOR_YES1.equalsIgnoreCase(pCSES08DOutputRec.getIndTodoInfoEnabled())) {
				if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getSysIdTodoCfStage())) {
					pCINT40DInputRec.setIdStage(pInputMsg.getSysIdTodoCfStage());
					List<StageRegionOutDto> resource40 = objCint40dDao.getStageDtls(pCINT40DInputRec);
					if (!TypeConvUtil.isNullOrEmpty(resource40) && resource40.size() > 0) {
						pCINT40DOutputRec = (StageRegionOutDto) resource40.get(0);
						pCCMN43DInputRec.setIdCase(pCINT40DOutputRec.getIdCase());
						szStageName = pCINT40DOutputRec.getNmStage();
					}
					if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getSysIdTodoCfPersWkr())) {
						pCCMN43DInputRec.setIdTodoPersWorker(pInputMsg.getSysIdTodoCfPersWkr());
					} else {
						pCINV51DInputRec.setIdStage(pInputMsg.getSysIdTodoCfStage());
						if (ServiceConstants.CLOSE_STAGE.equalsIgnoreCase(pCINT40DOutputRec.getIndStageClose())) {
							if (!TypeConvUtil.isNullOrEmpty(pCINT40DOutputRec.getDtStageClose())) {
								pCINV51DInputRec.setCdStagePersRole(ServiceConstants.HIST_PRIM_ROLE_STAGE_CLOSE);
							}
						} else {
							pCINV51DInputRec.setCdStagePersRole(ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
						}
						List<WorkloadStgPerLinkSelOutDto> resource51 = objCinv51dDao.getWorkLoad(pCINV51DInputRec);
						if (!TypeConvUtil.isNullOrEmpty(resource51)) {
							pCINV51DOutputRec = (WorkloadStgPerLinkSelOutDto) resource51.get(0);
							pCCMN43DInputRec.setIdTodoPersWorker(pCINV51DOutputRec.getIdTodoPersAssigned());
						}
					}
				}
				pCCMN43DInputRec.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_ADD);
				pCCMN43DInputRec.setIdTodoInfo(pCSES08DOutputRec.getIdTodoInfo());
				pCCMN43DInputRec.setCdTodoTask(pCSES08DOutputRec.getCdTodoInfoTask());
				pCCMN43DInputRec.setCdTodoType(pCSES08DOutputRec.getTodoInfoType());
				pCCMN43DInputRec.setIdEvent(pInputMsg.getSysIdTodoCfEvent());
				pCCMN43DInputRec.setIdStage(pInputMsg.getSysIdTodoCfStage());
				if (!ServiceConstants.NULL_STRING.equalsIgnoreCase(pInputMsg.getSysTxtTodoCfDesc())) {
					pCCMN43DInputRec.setTodoDesc(pInputMsg.getSysTxtTodoCfDesc());
					if (ServiceConstants.TODO_INFO_KIN001.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())) {
						String buffer;
						buffer = replace_str(pCSES08DOutputRec.getTodoInfoLongDesc(), "{NM RESOURCE}",
								pInputMsg.getSysTxtTodoCfDesc());
						pCCMN43DInputRec.setTodoDesc(buffer);
					}
					if (ServiceConstants.TODO_INFO_FAD046.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())) {
						String buffer;
						buffer = replace_str(pCSES08DOutputRec.getTodoInfoLongDesc(), "{NM RESOURCE}",
								pInputMsg.getSysTxtTodoCfDesc());
						pCCMN43DInputRec.setTodoDesc(buffer);
					}
				} else {
					pCCMN43DInputRec.setTodoDesc(pCSES08DOutputRec.getTodoInfoDesc());
				}
				if (!ServiceConstants.NULL_STRING.equalsIgnoreCase(pInputMsg.getSysTxtTodoCfLongDesc())) {
					pCCMN43DInputRec.setTodoLongDesc(pInputMsg.getSysTxtTodoCfLongDesc());
				} else {
					pCCMN43DInputRec.setTodoLongDesc(pCSES08DOutputRec.getTodoInfoLongDesc());
				}
				dtTempDate = pInputMsg.getDtSysDtTodoCfDueFrom();
				if (ServiceConstants.TODO_INFO_SUB015.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())
						|| ServiceConstants.TODO_INFO_SUB016.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())
						|| ServiceConstants.TODO_INFO_ADO015.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())) {
					/*
					 * dtShiftDate=pCSES08DOutputRec.getSNbrTodoInfoDueDd();
					 * (Date FAR )dtTempDate = (Date FAR )dtShiftDate;
					 * dtShiftDate=pCSES08DOutputRec.getSNbrTodoInfoDueDd();
					 */
				} else {
					// dtShiftDate=pCSES08DOutputRec.getSNbrTodoInfoDueDd();
				}
				dtTempDate = (Date) dtShiftDate;
				pCCMN43DInputRec.setDtTodoDue(dtTempDate);
				if (null != dtTempDate) {
					String buffer;
					int date;
					if (ServiceConstants.TODO_INFO_APS005.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())) {
						date = dtTempDate.getMonth() + dtTempDate.getMonth() + dtTempDate.getMonth();
						buffer = replace_str(pCSES08DOutputRec.getTodoInfoDesc(), "{DATE DUE}", Integer.toString(date));
						pCCMN43DInputRec.setTodoDesc(buffer);
					}
				}
				if (null != (dtTempDate)) {
					String buffer;
					int date;
					if (ServiceConstants.TODO_INFO_APS006.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())) {
						date = dtTempDate.getMonth() + dtTempDate.getMonth() + dtTempDate.getMonth();
						buffer = replace_str(pCSES08DOutputRec.getTodoInfoDesc(), "{DATE DUE}", Integer.toString(date));
						pCCMN43DInputRec.setTodoDesc(buffer);
					}
				}
				if ((ServiceConstants.TODO_INFO_SUB015.equalsIgnoreCase(pInputMsg.getSysCdTodoCf()))
						|| (ServiceConstants.TODO_INFO_SUB016.equalsIgnoreCase(pInputMsg.getSysCdTodoCf()))
						|| (ServiceConstants.TODO_INFO_SUB022.equalsIgnoreCase(pInputMsg.getSysCdTodoCf()))) {
					String buffer;
					int date;
					dtTempDateReview = pInputMsg.getDtSysDtTodoCfDueFrom();
					date = dtTempDateReview.getMonth() + dtTempDateReview.getMonth() + dtTempDateReview.getMonth();
					buffer = replace_str(pCSES08DOutputRec.getTodoInfoDesc(), ServiceConstants.DATE_PLACE_HOLDER,
							Integer.toString(date));
					buffer = replace_str(buffer, ServiceConstants.NAME_PLACE_HOLDER, szStageName);
					pCCMN43DInputRec.setTodoDesc(buffer);
				}
				if ((ServiceConstants.TODO_INFO_05_CODE.equalsIgnoreCase(pInputMsg.getSysCdTodoCf()))
						|| (ServiceConstants.TODO_CODE.equalsIgnoreCase(pInputMsg.getSysCdTodoCf()))
						|| (ServiceConstants.TODO_INFO_37_CODE.equalsIgnoreCase(pInputMsg.getSysCdTodoCf()))) {
					String buffer;
					buffer = replace_str(pCSES08DOutputRec.getTodoInfoDesc(), ServiceConstants.NAME_PLACE_HOLDER,
							szStageName);
					pCCMN43DInputRec.setTodoDesc(buffer);
				}
				pCCMN43DInputRec.setDtTodoCreated(dtDtSystemDate);
				pCCMN43DInputRec.setDtTodoCompleted(dtNullDate);
				if (ServiceConstants.CD_TODO_TASK.equalsIgnoreCase(pCSES08DOutputRec.getTodoInfoType())) {
					// The default the value is null. Don not set to zero else
					// it will break the foreign key reference with person
					// table.
					pCCMN43DInputRec.setIdTodoPersCreator(null);
					// dtShiftDate=pCSES08DOutputRec.getSNbrTodoInfoTaskDueDd();
					dtTempDate = pInputMsg.getDtSysDtTodoCfDueFrom();
					// (Date)dtTempDate = (Date FAR )dtShiftDate;
					pCCMN43DInputRec.setDtTaskDue(dtTempDate);
				} else {
					pCCMN43DInputRec.setIdTodoPersCreator(pInputMsg.getSysIdTodoCfPersCrea());
					pCCMN43DInputRec.setDtTaskDue(dtNullDate);
				}
				// Alert Type for ICPC
				if (CD_TASK_SUB_PLCMNT_REQ.equals(pInputMsg.getCdTask())
						|| CD_TASK_ADO_PLCMNT_REQ.equals(pInputMsg.getCdTask())
						|| CD_TASK_SUB_PLCMNT_STTS.equals(pInputMsg.getCdTask())
						|| CD_TASK_ADO_PLCMNT_STTS.equals(pInputMsg.getCdTask())) {
					pCCMN43DInputRec.setCdTodoTask(pInputMsg.getCdTask());
				}

				if (ServiceConstants.TODO_INFO_LEG001.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())
						|| ServiceConstants.TODO_INFO_MED002.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())
						|| ServiceConstants.TODO_INFO_MED003.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())) {
					// pCCMN43DInputRec.setSzCdTodoTask(pInputMsg.getCSUB40UIG00().getSzCdTask());
					dtTempDate = pInputMsg.getDtSysDtTodoCfDueFrom();
					// dtShiftDate=-pCSES08DOutputRec.getSNbrTodoInfoDueDd();
					// (Date FAR )dtTempDate = (Date FAR )dtShiftDate;
					pCCMN43DInputRec.setDtTaskDue(pInputMsg.getDtSysDtTodoCfDueFrom());
					pCCMN43DInputRec.setDtTodoDue(dtTempDate);
					/*
					 * if(getDays(pCCMN43DInputRec.getDtDtTodoDue())<getDays(
					 * dtDtSystemDate)) {
					 * pCCMN43DInputRec.setDtDtTodoDue(dtDtSystemDate); }
					 */
					if (pInputMsg.getReqFuncCd() != ServiceConstants.REQ_FUNC_CD_ADD) {
						pCCMN43DInputRec.setCdReqFunction(pInputMsg.getReqFuncCd());
						/*
						 * pCCMN43DInputRec.setLdIdTodo(pInputMsg.getCSUB40UIG00
						 * ().getLdIdTodo());
						 * pCCMN43DInputRec.setUlIdTodoInfo(pInputMsg.
						 * getCSUB40UIG00().getUlIdTodoInfo());
						 * pCCMN43DInputRec.setDtDtTodoCreated(pInputMsg.
						 * getCSUB40UIG00().getDtDtTodoCreated());
						 * pCCMN43DInputRec.setDtDtTodoCompleted(pInputMsg.
						 * getCSUB40UIG00().getDtDtTodoCompleted());
						 */
					}
				}
				if (ServiceConstants.TODO_INFO_ADP_002.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())
						|| ServiceConstants.TODO_INFO_ADP_003.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())) {
					dtTempDate = pInputMsg.getDtSysDtTodoCfDueFrom();
					// dtShiftDate=-pCSES08DOutputRec.getSNbrTodoInfoDueDd();
					// (Date FAR )dtTempDate = (Date FAR )dtShiftDate;
					pCCMN43DInputRec.setDtTodoDue(dtTempDate);
				}
				if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getSysIdTodoCfPersAssgn())) {
					pCCMN43DInputRec.setIdTodoPersAssigned(pInputMsg.getSysIdTodoCfPersAssgn());
					pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
					if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {

						if (ServiceConstants.TODO_INFO_LEG001.equalsIgnoreCase(pInputMsg.getSysCdTodoCf())
								&& pInputMsg.getReqFuncCd() == ServiceConstants.REQ_FUNC_CD_ADD) {
							pCCMN60DInputRec.setIdPerson(pCCMN43DInputRec.getIdTodoPersWorker());
							List<UnitEmpLinkPersonUnitSelOutDto> resource60 = objCcmn60dDao
									.getPersonName(pCCMN60DInputRec);
							if (!TypeConvUtil.isNullOrEmpty(resource60)) {
								pCCMN43DInputRec.setIdTodoPersAssigned(pCCMN60DOutputRec.getIdPerson());
								if (pCCMN60DOutputRec.getIdPerson() != pInputMsg.getSysIdTodoCfPersAssgn()) {
									pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
								}
							}
						}
					}
				} else if (ServiceConstants.CD_MEMBER.equalsIgnoreCase(pCSES08DOutputRec.getCdTodoInfoPersAssignd())) {
					pCCMN43DInputRec.setIdTodoPersAssigned(pCCMN43DInputRec.getIdTodoPersWorker());
					pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
					if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {
					}
				} else if (ServiceConstants.CD_LEAD.equalsIgnoreCase(pCSES08DOutputRec.getCdTodoInfoPersAssignd())) {
					pCCMN60DInputRec.setIdPerson(pCCMN43DInputRec.getIdTodoPersWorker());
					List<UnitEmpLinkPersonUnitSelOutDto> resource60 = objCcmn60dDao.getPersonName(pCCMN60DInputRec);
					if (!TypeConvUtil.isNullOrEmpty(resource60)) {
						pCCMN60DOutputRec = (UnitEmpLinkPersonUnitSelOutDto) resource60.get(0);
						pCCMN43DInputRec.setIdTodoPersAssigned(pCCMN60DOutputRec.getIdPerson());
						pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
						if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {
							if (ServiceConstants.SVC_CD_TASK_CONTACT_APS
									.equalsIgnoreCase(pCSES08DOutputRec.getCdTodoInfoTask())) {
								pCCMN43DInputRec.setIdTodoPersAssigned(pCCMN43DInputRec.getIdTodoPersWorker());
								pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
								if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {
								}
							}
						}
						pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
						if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {
						}
					}
				} else {
					pCSEC04DInputRec.setIdPerson(pCCMN43DInputRec.getIdTodoPersWorker());
					pCSEC04DInputRec.setCdUnitMemberRole(pCSES08DOutputRec.getCdTodoInfoPersAssignd());
					List<UnitEmpLinkOutDto> resource04 = objCsec04dDao.getUnitEmpLinkRecord(pCSEC04DInputRec);
					if (!CollectionUtils.isEmpty(resource04)) {
						pCSEC04DOutputRec = (UnitEmpLinkOutDto) resource04.get(0);
						pCCMN43DInputRec.setIdTodoPersAssigned(pCSEC04DOutputRec.getIdPerson());
						pCCMN43DOutputRec = objCcmn43dDao.cudTODO(pCCMN43DInputRec);
						if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {
							pCCMN43DInputRec.setIdTodo(pCCMN43DOutputRec.getIdTodo());
						}
					}
				}
				if (!TypeConvUtil.isNullOrEmpty(pCCMN43DOutputRec)) {
					pCCMN43DInputRec.setIdTodo(pCCMN43DOutputRec.getIdTodo());
				}
				commonTodoOutDto = mapCsub40DTO(commonTodoOutDto, pCCMN43DInputRec);
			}
		}
		log.debug("Exiting method TodoCommonFunction in CommonTodoServiceImpl");
		return commonTodoOutDto;
	}

	/**
	 *
	 * @param pCCMN43DInputRec
	 * @param pOutputMsg
	 * @return CommonTodoOutDto @
	 */
	private CommonTodoOutDto mapCsub40DTO(CommonTodoOutDto pOutputMsg, TodoInsUpdDelInDto pCCMN43DInputRec) {
		pOutputMsg.setCdTodoTask(pCCMN43DInputRec.getCdTodoTask());
		pOutputMsg.setCdTodoType(pCCMN43DInputRec.getCdTodoType());
		pOutputMsg.setDtTaskDue(pCCMN43DInputRec.getDtTodoCompleted());
		pOutputMsg.setDtTodoCompleted(pCCMN43DInputRec.getDtTodoCreated());
		pOutputMsg.setDtTodoCreated(pCCMN43DInputRec.getDtTodoDue());
		pOutputMsg.setDtTodoDue(pCCMN43DInputRec.getDtTaskDue());
		pOutputMsg.setIdCase(pCCMN43DInputRec.getIdCase());
		pOutputMsg.setIdEvent(pCCMN43DInputRec.getIdEvent());
		pOutputMsg.setIdStage(pCCMN43DInputRec.getIdStage());
		pOutputMsg.setIdTodo(pCCMN43DInputRec.getIdTodo());
		pOutputMsg.setIdTodoPersAssigned(pCCMN43DInputRec.getIdTodoPersAssigned());
		pOutputMsg.setIdTodoPersCreator(pCCMN43DInputRec.getIdTodoPersCreator());
		pOutputMsg.setIdTodoPersWorker(pCCMN43DInputRec.getIdTodoPersWorker());
		pOutputMsg.setTodoDesc(pCCMN43DInputRec.getTodoDesc());
		pOutputMsg.setTodoLongDesc(pCCMN43DInputRec.getTodoLongDesc());
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
		log.debug("Entering method replace_str in CommonTodoServiceImpl");
		String p = null;
		if (orig.contains(substr)) {
			p = orig.replace(substr, rep);
		}
		log.debug("Exiting method replace_str in CommonTodoServiceImpl");
		return p;
	}

	/**
	 *
	 * @param pInputMsg
	 * @return CommonTodoOutDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonTodoOutDto callCsub40uService(CommonTodoInDto pInputMsg) {
		log.debug("Entering method callCsub40uService in CommonTodoServiceImpl");
		CommonTodoOutDto pOutputMsg = new CommonTodoOutDto();
		pOutputMsg = TodoCommonFunction(pInputMsg);
		log.debug("Exiting method callCsub40uService in CommonTodoServiceImpl");
		return pOutputMsg;
	}
}
