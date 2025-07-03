package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dao.EventRetDao;
import us.tx.state.dfps.service.admin.dao.FetchStageDao;
import us.tx.state.dfps.service.admin.dao.StageRetDao;
import us.tx.state.dfps.service.admin.dao.TaskDetailsDao;
import us.tx.state.dfps.service.admin.dto.CheckStageInpDto;
import us.tx.state.dfps.service.admin.dto.CheckStageOutDto;
import us.tx.state.dfps.service.admin.dto.EventdiDto;
import us.tx.state.dfps.service.admin.dto.EventdoDto;
import us.tx.state.dfps.service.admin.dto.FetchStagediDto;
import us.tx.state.dfps.service.admin.dto.FetchStagedoDto;
import us.tx.state.dfps.service.admin.dto.StagediDto;
import us.tx.state.dfps.service.admin.dto.StagedoDto;
import us.tx.state.dfps.service.admin.dto.TaskdiDto;
import us.tx.state.dfps.service.admin.dto.TaskdoDto;
import us.tx.state.dfps.service.admin.service.EventTaskStageService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Ccmn06uServiceImpl Sep 7, 2017- 10:57:42 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class EventTaskStageServiceImpl implements EventTaskStageService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FetchStageDao fetchStageDao;

	@Autowired
	TaskDetailsDao taskDetailsDao;

	@Autowired
	EventRetDao eventRetDao;

	@Autowired
	StageRetDao stageRetDao;

	private static final Logger log = Logger.getLogger(EventTaskStageServiceImpl.class);

	/**
	 * Method Name: checkStageEventStatus Method Description:This Method will
	 * retrieve the StagePersonLink Details and Event details for particular
	 * stage id and event id by calling the DAO layer.
	 * 
	 * @param checkStageInpDto
	 * @return returnValue @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String checkStageEventStatus(CheckStageInpDto checkStageInpDto) {
		log.debug("Entering method CheckStageEventStatus in EventTaskStageServiceImpl");

		Integer sLowestStatus = ServiceConstants.APRV_NBR_EVENT_STAT;
		Integer sTempStatus = ServiceConstants.APRV_NBR_EVENT_STAT;
		boolean bStageIsClosed = ServiceConstants.FALSEVAL;
		String returnValue = ServiceConstants.FND_FAIL;
		FetchStagediDto fetchStagediDto = new FetchStagediDto();
		TaskdiDto taskdiDto = new TaskdiDto();
		EventdiDto eventdiDto = new EventdiDto();

		FetchStagedoDto fetchStagedoDto = getStagewise(checkStageInpDto, fetchStagediDto);
		if (!TypeConvUtil.isNullOrEmpty(fetchStagedoDto)) {

			returnValue = checkStageClose(checkStageInpDto, bStageIsClosed, returnValue, fetchStagedoDto);
			if (!TypeConvUtil.isNullOrEmpty(checkStageInpDto.getCdTask())) {

				taskdiDto.setSzCdTask(checkStageInpDto.getCdTask());
				TaskdoDto taskdoDto = taskDetailsDao.getTaskDtls(taskdiDto).get(0);
				if (!TypeConvUtil.isNullOrEmpty(taskdoDto)) {
					returnValue = ServiceConstants.ARC_SUCCESS;
					if (ServiceConstants.REQ_FUNC_CD_ADD.equals(checkStageInpDto.getReqFuncCd())) {
						returnValue = checkUlIdTask(checkStageInpDto, sLowestStatus, sTempStatus, eventdiDto,
								taskdoDto);
					}
				}

			}
		}

		log.debug("Exiting method CheckStageEventStatus in EventTaskStageServiceImpl");
		return returnValue;
	}

	/**
	 * Gets the stagewise.
	 *
	 * @param checkStageInpDto
	 *            the check stage inp dto
	 * @param fetchStagediDto
	 *            the fetch stagedi dto
	 * @return the stagewise
	 */
	private FetchStagedoDto getStagewise(CheckStageInpDto checkStageInpDto, FetchStagediDto fetchStagediDto) {
		if (!TypeConvUtil.isNullOrEmpty(checkStageInpDto.getIdStage())) {
			fetchStagediDto.setUlIdStage(checkStageInpDto.getIdStage());
		}
		FetchStagedoDto fetchStagedoDto = fetchStageDao.getStageDetails(fetchStagediDto).get(0);
		return fetchStagedoDto;
	}

	/**
	 * Method Name: checkUlIdTask Method Description:
	 * 
	 * @param ccmn06uiDto
	 * @param sLowestStatus
	 * @param sTempStatus
	 * @param pCLSC71DInputRec
	 * @param ccmn82doDto
	 * @return @
	 */
	private String checkUlIdTask(CheckStageInpDto ccmn06uiDto, Integer sLowestStatus, Integer sTempStatus,
			EventdiDto pCLSC71DInputRec, TaskdoDto ccmn82doDto) {
		String returnValue;
		if (!TypeConvUtil.isNullOrEmpty(ccmn06uiDto.getIdStage())) {
			pCLSC71DInputRec.setUlIdStage(ccmn06uiDto.getIdStage());
		}
		if (!TypeConvUtil.isNullOrEmpty(ccmn06uiDto.getCdTask())) {
			pCLSC71DInputRec.setSzCdTask(ccmn06uiDto.getCdTask());
		}
		List<EventdoDto> clsc71doDtos = eventRetDao.getEventValues(pCLSC71DInputRec);
		returnValue = getTaskMultInstance(sLowestStatus, sTempStatus, ccmn82doDto, clsc71doDtos);
		return returnValue;
	}

	/**
	 * Method Name: checkStageClose Method Description:
	 * 
	 * @param ccmn06uiDto
	 * @param bStageIsClosed
	 * @param returnValue
	 * @param cint21doDto
	 * @return @
	 */
	private String checkStageClose(CheckStageInpDto ccmn06uiDto, boolean bStageIsClosed, String returnValue,
			FetchStagedoDto cint21doDto) {
		if (!TypeConvUtil.isNullOrEmpty(cint21doDto.getDtDtStageClose())) {

			boolean isExists = false;
			boolean isCdTaskExists = !TypeConvUtil.isNullOrEmpty(ccmn06uiDto.getCdTask());
			returnValue = getCdTask(ccmn06uiDto, bStageIsClosed, isExists, isCdTaskExists);
		}
		return returnValue;
	}

	/**
	 * Method Name: getTaskMultInstance Method Description:
	 * 
	 * @param sLowestStatus
	 * @param sTempStatus
	 * @param ccmn82doDto
	 * @param clsc71doDtos
	 * @return
	 */
	private String getTaskMultInstance(Integer sLowestStatus, Integer sTempStatus, TaskdoDto ccmn82doDto,
			List<EventdoDto> clsc71doDtos) {
		String returnValue;
		if (!TypeConvUtil.isNullOrEmpty(clsc71doDtos)) {
			returnValue = ServiceConstants.ARC_SUCCESS;
			if (!ServiceConstants.IND_MULTIPLE_INST_TRUE.equals(ccmn82doDto.getbIndTaskMultInstance())) {
				returnValue = ServiceConstants.MSG_SYS_MULT_INST;
			}
			returnValue = getCdEventStatus(sLowestStatus, sTempStatus, returnValue, ccmn82doDto, clsc71doDtos);

		} else {
			returnValue = ServiceConstants.ARC_SUCCESS;
		}
		return returnValue;
	}

	/**
	 * Method Name: getCdEventStatus Method Description:
	 * 
	 * @param sLowestStatus
	 * @param sTempStatus
	 * @param returnValue
	 * @param ccmn82doDto
	 * @param clsc71doDtos
	 * @return
	 */
	private String getCdEventStatus(Integer sLowestStatus, Integer sTempStatus, String returnValue,
			TaskdoDto ccmn82doDto, List<EventdoDto> clsc71doDtos) {
		if (!TypeConvUtil.isNullOrEmpty(ccmn82doDto.getSzCdEventStatus())) {
			for (EventdoDto clsc71doDto : clsc71doDtos) {
				if (!TypeConvUtil.isNullOrEmpty(clsc71doDto.getSzCdEventStatus())) {
					sTempStatus = getEventStatus(sTempStatus, clsc71doDto);
				}
			}
			returnValue = compareStatus(sLowestStatus, sTempStatus, returnValue, ccmn82doDto);

		}
		return returnValue;
	}

	/**
	 * Method Name: compareStatus Method Description:
	 * 
	 * @param sLowestStatus
	 * @param sTempStatus
	 * @param returnValue
	 * @param ccmn82doDto
	 * @return
	 */
	private String compareStatus(Integer sLowestStatus, Integer sTempStatus, String returnValue,
			TaskdoDto ccmn82doDto) {
		if (sTempStatus < sLowestStatus) {
			sLowestStatus = sTempStatus;
		}
		sTempStatus = ServiceConstants.NEW_NBR_EVENT_STAT;

		sTempStatus = compareEventStatus(sTempStatus, ccmn82doDto);
		if (sLowestStatus < sTempStatus) {
			returnValue = ServiceConstants.MSG_SYS_EVENT_STS_MSMTCH;
		}
		return returnValue;
	}

	/**
	 * Method Name: getCdTask Method Description:
	 * 
	 * @param ccmn06uiDto
	 * @param bStageIsClosed
	 * @param isExists
	 * @param isCdTaskExists
	 * @return returnValue @
	 */
	private String getCdTask(CheckStageInpDto ccmn06uiDto, boolean bStageIsClosed, boolean isExists,
			boolean isCdTaskExists) {
		String returnValue;
		for (String string : ServiceConstants.TASKSTATUS) {
			if (isCdTaskExists && ccmn06uiDto.getCdTask().equals(string)) {
				isExists = true;
				break;
			}
		}
		if (isExists) {
			returnValue = ServiceConstants.ARC_SUCCESS;

		} else {
			returnValue = getTask(ccmn06uiDto, bStageIsClosed, isCdTaskExists);

		}
		return returnValue;
	}

	/**
	 * Method Name: getEventStatus Method Description:
	 * 
	 * @param sTempStatus
	 * @param clsc71doDto
	 * @return
	 */
	private Integer getEventStatus(Integer sTempStatus, EventdoDto clsc71doDto) {
		if (clsc71doDto.getSzCdEventStatus().equalsIgnoreCase(ServiceConstants.NEW_EVENT_STATUS)) {
			sTempStatus = ServiceConstants.NEW_NBR_EVENT_STAT;
		} else if (clsc71doDto.getSzCdEventStatus().equalsIgnoreCase(ServiceConstants.PROCESS_EVENT_STATUS)) {
			sTempStatus = ServiceConstants.PROC_NBR_EVENT_STAT;
		} else if (clsc71doDto.getSzCdEventStatus().equalsIgnoreCase(ServiceConstants.COMPLETE_EVENT_STATUS)) {
			sTempStatus = ServiceConstants.COMP_NBR_EVENT_STAT;
		} else if (clsc71doDto.getSzCdEventStatus().equalsIgnoreCase(ServiceConstants.PENDING_EVENT_STATUS)) {
			sTempStatus = ServiceConstants.PEND_NBR_EVENT_STAT;
		} else if (clsc71doDto.getSzCdEventStatus().equalsIgnoreCase(ServiceConstants.APPROVE_EVENT_STATUS)) {
			sTempStatus = ServiceConstants.APRV_NBR_EVENT_STAT;
		}
		return sTempStatus;
	}

	/**
	 * Method Name: getTask Method Description:
	 * 
	 * @param ccmn06uiDto
	 * @param bStageIsClosed
	 * @param isCdTaskExists
	 * @return @
	 */
	private String getTask(CheckStageInpDto ccmn06uiDto, boolean bStageIsClosed, boolean isCdTaskExists) {
		String returnValue;
		if ((isCdTaskExists && ccmn06uiDto.getCdTask().equalsIgnoreCase(ServiceConstants.TASKUPADATEA))
				|| (isCdTaskExists && ccmn06uiDto.getCdTask().equalsIgnoreCase(ServiceConstants.TASKUPADATEB))) {
			List<CheckStageOutDto> ccmn06uoDtos = callCSES71D(ccmn06uiDto, bStageIsClosed);
			for (CheckStageOutDto ccmn06uoDto : ccmn06uoDtos) {
				if (ccmn06uoDto.isbStageIsClosed()) {

					returnValue = ServiceConstants.ARC_SUCCESS;
					break;
				}
			}
		}

		returnValue = ServiceConstants.MSG_SYS_STAGE_CLOSED;
		return returnValue;
	}

	/**
	 * Method Name: compareEventStatus Method Description:
	 * 
	 * @param sTempStatus
	 * @param ccmn82doDto
	 * @return
	 */
	private Integer compareEventStatus(Integer sTempStatus, TaskdoDto ccmn82doDto) {
		if (ccmn82doDto.getSzCdEventStatus().equalsIgnoreCase(ServiceConstants.NEW_EVENT_STATUS)) {
			sTempStatus = ServiceConstants.NEW_NBR_EVENT_STAT;
		} else if (ccmn82doDto.getSzCdEventStatus().equalsIgnoreCase(ServiceConstants.PROCESS_EVENT_STATUS)) {
			sTempStatus = ServiceConstants.PROC_NBR_EVENT_STAT;
		} else if (ccmn82doDto.getSzCdEventStatus().equalsIgnoreCase(ServiceConstants.COMPLETE_EVENT_STATUS)) {
			sTempStatus = ServiceConstants.COMP_NBR_EVENT_STAT;
		} else if (ccmn82doDto.getSzCdEventStatus().equalsIgnoreCase(ServiceConstants.PENDING_EVENT_STATUS)) {
			sTempStatus = ServiceConstants.PEND_NBR_EVENT_STAT;
		} else if ((ccmn82doDto.getSzCdEventStatus().equalsIgnoreCase(ServiceConstants.APPROVE_EVENT_STATUS))) {
			sTempStatus = ServiceConstants.APRV_NBR_EVENT_STAT;
		}
		return sTempStatus;
	}

	/**
	 * Method Name: callCSES71D Method Description:This Method will retrieve the
	 * Stage Details for particular stage id by calling the DAO layer.
	 * 
	 * @param ccmn06uiDto
	 * @param pbStageIsClosed
	 * @return returnValue @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<CheckStageOutDto> callCSES71D(CheckStageInpDto ccmn06uiDto, boolean pbStageIsClosed) {
		log.debug("Entering method CallCSES71D in Ccmn06uServiceImpl");
		StagediDto pCSES71DInputRec = new StagediDto();
		List<CheckStageOutDto> ccmn06uoDtos = new ArrayList<CheckStageOutDto>();
		pCSES71DInputRec.setUlIdStage(ccmn06uiDto.getIdStage());
		List<StagedoDto> cses71doDtos = stageRetDao.getStageValues(pCSES71DInputRec);
		for (StagedoDto cses71doDto : cses71doDtos) {
			if (cses71doDto != null) {
				CheckStageOutDto ccmn06uoDto = new CheckStageOutDto();
				Calendar calendar = Calendar.getInstance();
				if (!TypeConvUtil.isNullOrEmpty(cses71doDto.getDtDtStageClose())) {
					calendar.setTime(cses71doDto.getDtDtStageClose());
				}
				if ((TypeConvUtil.isNullOrEmpty(cses71doDto.getDtDtStageClose()))
						|| (ServiceConstants.YEAR_CLOSED == calendar.get(Calendar.YEAR))) {
				} else {
					pbStageIsClosed = true;
				}
				ccmn06uoDto.setbStageIsClosed(pbStageIsClosed);
				ccmn06uoDtos.add(ccmn06uoDto);

			}

		}

		log.debug("Exiting method CallCSES71D in Ccmn06uServiceImpl");
		return ccmn06uoDtos;
	}

	/**
	 * Method Name: callCcmn06uService Method Description:This Method will
	 * retrieve the Stage Details for particular stage id by calling the DAO
	 * layer.
	 * 
	 * @param ccmn06uiDto
	 * @return ccmn06uoDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CheckStageOutDto callCcmn06uService(CheckStageInpDto ccmn06uiDto) {
		log.debug("Entering method callCcmn06uService in Ccmn06uServiceImpl");

		CheckStageOutDto ccmn06uoDto = new CheckStageOutDto();
		String pOutputMsg = checkStageEventStatus(ccmn06uiDto);
		ccmn06uoDto.setRespMsg(pOutputMsg);
		log.debug("Exiting method callCcmn06uService in Ccmn06uServiceImpl");
		return ccmn06uoDto;
	}

}
