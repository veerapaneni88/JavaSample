package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dao.EventEventValsDao;
import us.tx.state.dfps.service.admin.dao.StageAssignDao;
import us.tx.state.dfps.service.admin.dao.StageCntyDao;
import us.tx.state.dfps.service.admin.dao.TaskTaskDetailsDao;
import us.tx.state.dfps.service.admin.dto.EventInDto;
import us.tx.state.dfps.service.admin.dto.EventOutDto;
import us.tx.state.dfps.service.admin.dto.StageAssignInDto;
import us.tx.state.dfps.service.admin.dto.StageAssignOutDto;
import us.tx.state.dfps.service.admin.dto.StageCntyInDto;
import us.tx.state.dfps.service.admin.dto.StageCntyOutDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.StageTaskOutDto;
import us.tx.state.dfps.service.admin.dto.TaskInDto;
import us.tx.state.dfps.service.admin.dto.TaskOutDto;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV50S Aug
 * 7, 2017- 9:05:29 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class StageEventStatusCommonServiceImpl implements StageEventStatusCommonService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	StageAssignDao objCint21dDao;

	@Autowired
	TaskTaskDetailsDao objCcmn82dDao;

	@Autowired
	EventEventValsDao objClsc71dDao;

	@Autowired
	StageCntyDao objCses71dDao;

	private static final Logger log = Logger.getLogger(StageEventStatusCommonServiceImpl.class);

	/**
	 * 
	 * Method Name: CheckStageEventStatus Method Description:This is a Common
	 * Function (not a service) which is called by services which update
	 * functional tables. It is packaged in the libappd.a archive library. This
	 * function receives the following inputs: cReqFuncCd, idStage, and cdTask.
	 * 
	 * @param pInputMsg
	 * @return String @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String checkStageEventStatus(StageTaskInDto pInputMsg) {
		log.debug("Entering method CheckStageEventStatus in StageEventStatusCommonServiceImpl");
		Integer sLowestStatus = ServiceConstants.APRV_NBR_EVENT_STAT;
		Integer sTempStatus = ServiceConstants.APRV_NBR_EVENT_STAT;
		boolean bStageIsClosed = false;
		String returnValue = ServiceConstants.ARC_SUCCESS;
		StageAssignInDto pCINT21DInputRec = new StageAssignInDto();
		TaskInDto pCCMN82DInputRec = new TaskInDto();
		EventInDto pCLSC71DInputRec = new EventInDto();
		pCINT21DInputRec.setIdStage(pInputMsg.getIdStage());
		List<StageAssignOutDto> cint21doDtos = objCint21dDao.getStageDetails(pCINT21DInputRec);
		if (!TypeConvUtil.isNullOrEmpty(cint21doDtos)) {
			for (StageAssignOutDto cint21doDto : cint21doDtos) {
				if (!TypeConvUtil.isNullOrEmpty(cint21doDto.getDtStageClose())) {
					boolean isExists = false;
					for (String string : ServiceConstants.TASKSTATUS) {
						if (pInputMsg.getCdTask().equals(string)) {
							isExists = true;
							break;
						}
					}
					if (isExists) {
						returnValue = ServiceConstants.ARC_SUCCESS;
						break;
					} else {
						if ((pInputMsg.getCdTask().equalsIgnoreCase(ServiceConstants.TASKUPADATEA))
								|| (pInputMsg.getCdTask().equalsIgnoreCase(ServiceConstants.TASKUPADATEB))) {
							List<StageTaskOutDto> ccmn06uoDtos = CallCSES71D(pInputMsg, bStageIsClosed);
							for (StageTaskOutDto ccmn06uoDto : ccmn06uoDtos) {
								if (ccmn06uoDto.isStageIsClosed() != false) {
									returnValue = ServiceConstants.ARC_SUCCESS;
									break;
								}
							}
						} else {
							returnValue = ServiceConstants.MSG_SYS_STAGE_CLOSED;
							break;
						}
					}
				}
				if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getCdTask())) {
					pCCMN82DInputRec.setCdTask(pInputMsg.getCdTask());
					List<TaskOutDto> ccmn82doDtos = objCcmn82dDao.getTaskDtls(pCCMN82DInputRec);
					if (null != ccmn82doDtos && ccmn82doDtos.size() > 0) {
						returnValue = ServiceConstants.ARC_SUCCESS;
						if (ServiceConstants.ADD.equalsIgnoreCase(pInputMsg.getCdReqFunction())) {
							for (TaskOutDto ccmn82doDto : ccmn82doDtos) {
								if (ServiceConstants.IND_MULTIPLE_INST_TRUE
										.equalsIgnoreCase(ccmn82doDto.getIndTaskMultInstance())) {
									pCLSC71DInputRec.setIdStage(pInputMsg.getIdStage());
									pCLSC71DInputRec.setCdTask(pInputMsg.getCdTask());
									List<EventOutDto> clsc71doDtos = objClsc71dDao.getEventValues(pCLSC71DInputRec);
									if (null != clsc71doDtos && clsc71doDtos.size() > 0) {
										if (!TypeConvUtil.isNullOrEmpty(ccmn82doDto.getCdEventStatus())) {
											for (EventOutDto clsc71doDto : clsc71doDtos) {
												if (!clsc71doDto.getCdEventStatus()
														.equalsIgnoreCase(ServiceConstants.NEW_EVENT_STATUS)) {
													sTempStatus = ServiceConstants.NEW_NBR_EVENT_STAT;
												} else if (!clsc71doDto.getCdEventStatus()
														.equalsIgnoreCase(ServiceConstants.PROCESS_EVENT_STATUS)) {
													sTempStatus = ServiceConstants.PROC_NBR_EVENT_STAT;
												} else if (!clsc71doDto.getCdEventStatus()
														.equalsIgnoreCase(ServiceConstants.COMPLETE_EVENT_STATUS)) {
													sTempStatus = ServiceConstants.COMP_NBR_EVENT_STAT;
												} else if (!clsc71doDto.getCdEventStatus()
														.equalsIgnoreCase(ServiceConstants.PENDING_EVENT_STATUS)) {
													sTempStatus = ServiceConstants.PEND_NBR_EVENT_STAT;
												} else if (!clsc71doDto.getCdEventStatus()
														.equalsIgnoreCase(ServiceConstants.APPROVE_EVENT_STATUS)) {
													sTempStatus = ServiceConstants.APRV_NBR_EVENT_STAT;
												}
											}
											if (sTempStatus < sLowestStatus) {
												sLowestStatus = sTempStatus;
											}
											sTempStatus = ServiceConstants.NEW_NBR_EVENT_STAT;
											if (!ccmn82doDto.getCdEventStatus()
													.equalsIgnoreCase(ServiceConstants.NEW_EVENT_STATUS)) {
												sTempStatus = ServiceConstants.NEW_NBR_EVENT_STAT;
											} else if (!ccmn82doDto.getCdEventStatus()
													.equalsIgnoreCase(ServiceConstants.PROCESS_EVENT_STATUS)) {
												sTempStatus = ServiceConstants.PROC_NBR_EVENT_STAT;
											} else if (!ccmn82doDto.getCdEventStatus()
													.equalsIgnoreCase(ServiceConstants.COMPLETE_EVENT_STATUS)) {
												sTempStatus = ServiceConstants.COMP_NBR_EVENT_STAT;
											} else if (!ccmn82doDto.getCdEventStatus()
													.equalsIgnoreCase(ServiceConstants.PENDING_EVENT_STATUS)) {
												sTempStatus = ServiceConstants.PEND_NBR_EVENT_STAT;
											} else if (!(ccmn82doDto.getCdEventStatus()
													.equalsIgnoreCase(ServiceConstants.APPROVE_EVENT_STATUS))) {
												sTempStatus = ServiceConstants.APRV_NBR_EVENT_STAT;
											}
											if (sLowestStatus != sTempStatus) {
												returnValue = ServiceConstants.MSG_SYS_EVENT_STS_MSMTCH;
												break;
											} else {
												returnValue = ServiceConstants.ARC_SUCCESS;
												break;
											}
										}
									}
								} else {
									returnValue = ServiceConstants.MSG_SYS_MULT_INST;
									break;
								}
							}
						}
					}
				}
			}
		}
		log.debug("Exiting method CheckStageEventStatus in StageEventStatusCommonServiceImpl");
		return returnValue;
	}

	/**
	 * Method Description: This Method will retrieve the Stage Details for
	 * particular stage id by calling the DAO layer. Service Name : CINV50S
	 * 
	 * @param pInputMsg
	 * @param pbStageIsClosed
	 * @return ccmn06uoDtos @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StageTaskOutDto> CallCSES71D(StageTaskInDto pInputMsg, boolean pbStageIsClosed) {
		log.debug("Entering method CallCSES71D in StageEventStatusCommonServiceImpl");
		StageCntyInDto pCSES71DInputRec = new StageCntyInDto();
		List<StageTaskOutDto> ccmn06uoDtos = new ArrayList<StageTaskOutDto>();
		pCSES71DInputRec.setIdStage(pInputMsg.getIdStage());
		List<StageCntyOutDto> cses71doDtos = objCses71dDao.getStageValues(pCSES71DInputRec);
		for (StageCntyOutDto cses71doDto : cses71doDtos) {
			if (cses71doDto != null) {
				StageTaskOutDto ccmn06uoDto = new StageTaskOutDto();
				if ((TypeConvUtil.isNullOrEmpty(cses71doDto.getDtStageClose()))
						|| (ServiceConstants.YEAR_CLOSED == cses71doDto.getDtStageClose().getYear())) {
					pbStageIsClosed = false;
				} else {
					pbStageIsClosed = true;
				}
				ccmn06uoDto.setbStageIsClosed(pbStageIsClosed);
				ccmn06uoDto.setIdStage(cses71doDto.getIdStage());
				ccmn06uoDtos.add(ccmn06uoDto);
			}
		}
		log.debug("Exiting method CallCSES71D in StageEventStatusCommonServiceImpl");
		return ccmn06uoDtos;
	}

	/**
	 * Method Description: This Method will retrieve the Stage Details for
	 * particular stage id by calling the DAO layer. Service Name : CINV50S
	 * 
	 * @param pInputMsg
	 * @return StageTaskOutDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StageTaskOutDto callCcmn06uService(StageTaskInDto pInputMsg) {
		log.debug("Entering method callCcmn06uService in StageEventStatusCommonServiceImpl");
		StageTaskOutDto pOutputMsg = new StageTaskOutDto();
		checkStageEventStatus(pInputMsg);
		if (pOutputMsg != null) {
		}
		log.debug("Exiting method callCcmn06uService in StageEventStatusCommonServiceImpl");
		return pOutputMsg;
	}
}
