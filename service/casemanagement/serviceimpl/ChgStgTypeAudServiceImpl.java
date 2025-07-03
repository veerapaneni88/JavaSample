package us.tx.state.dfps.service.casemanagement.serviceimpl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.admin.dao.TodoUpdDtTodoCompletedDao;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusOutDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.TodoUpdDtTodoCompletedInDto;
import us.tx.state.dfps.service.admin.service.PostEventStageStatusService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.casemanagement.dao.CpsCheckListDao;
import us.tx.state.dfps.service.casemanagement.dao.StageUpdIDByStageTypeDao;
import us.tx.state.dfps.service.casemanagement.dto.CpsCheckListInDto;
import us.tx.state.dfps.service.casemanagement.dto.CpsCheckListOutDto;
import us.tx.state.dfps.service.casemanagement.dto.StageUpdIDByStageTypeInDto;
import us.tx.state.dfps.service.casemanagement.service.ChgStgTypeAudService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ChgStgTypeAudReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CSUB64S Aug
 * 21, 2017- 9:05:43 PM © 2017 Texas Department of Family and Protective
 * Services.
 */
@Service
@Transactional
public class ChgStgTypeAudServiceImpl implements ChgStgTypeAudService {

	/** The Constant SQL_SUCCESS. */
	private static final int SQL_SUCCESS = 1;

	/** The Constant FND_FAIL. */
	private static final int FND_FAIL = 0;

	/** The obj ccmn 06 u service. */
	@Autowired
	private StageEventStatusCommonService stageEventStatusCommonService;

	/** The obj ccmn 01 u service. */
	@Autowired
	private PostEventStageStatusService postEventStageStatusService;

	/** The obj caud 42 d dao. */
	@Autowired
	private StageUpdIDByStageTypeDao stageUpdIDByStageTypeDao;

	/** The obj cinv 43 d dao. */
	@Autowired
	private TodoUpdDtTodoCompletedDao todoUpdDtTodoCompletedDao;

	/** The obj csesc 9 d dao. */
	@Autowired
	private CpsCheckListDao cpsCheckListDao;

	/** The Constant log. */
	private static final Logger LOGGER = Logger.getLogger(ChgStgTypeAudServiceImpl.class);

	/**
	 * This service will add or update the Event Table using the post event
	 * function, add or update the Stage table, and (if necessary), call the
	 * invalidate event function.
	 *
	 * @param pInputMsg
	 *            the input msg
	 * @return retVal @ the service exception
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public CommonStringRes callChgStgTypeAudService(ChgStgTypeAudReq pInputMsg) {
		CommonStringRes response = new CommonStringRes();
		PostEventStageStatusOutDto ccmn01uoDto = new PostEventStageStatusOutDto();
		String retVal = ServiceConstants.FND_FAIL;
		StageUpdIDByStageTypeInDto pCAUD42DInputRec = new StageUpdIDByStageTypeInDto();
		PostEventStageStatusInDto pCCMN01UInputRec = new PostEventStageStatusInDto();
		TodoUpdDtTodoCompletedInDto pCINV43DInputRec = new TodoUpdDtTodoCompletedInDto();
		StageTaskInDto pCCMN06UInputRec = getStageTaskInput(pInputMsg);

		String returnStatus = stageEventStatusCommonService.checkStageEventStatus(pCCMN06UInputRec);
		if (!ObjectUtils.isEmpty(returnStatus) && ServiceConstants.ARC_SUCCESS.equalsIgnoreCase(returnStatus)) {
			retVal = ServiceConstants.FND_SUCCESS;
		} else {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorMsg(returnStatus);
			response.setErrorDto(errorDto);
		}

		if (ServiceConstants.FND_SUCCESS.equalsIgnoreCase(retVal)
				&& (ServiceConstants.STAGE_TYPE_SFI.equalsIgnoreCase(pInputMsg.getCdStageType())
						|| (ServiceConstants.STAGE_TYPE_SFI.equalsIgnoreCase(pInputMsg.getCdOldStageType())))) {

			// Retrieve Service Ref Checklist Info from CPS_CHECKLIST
			int retStatus = callCSESC9D(pInputMsg);
			if (SQL_SUCCESS == retStatus) {
				retVal = ServiceConstants.FND_SUCCESS;
			} else {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(retStatus);
				response.setErrorDto(errorDto);
				retVal = ServiceConstants.FND_FAIL;
			}

		}
		if (ServiceConstants.FND_SUCCESS.equalsIgnoreCase(retVal)) {
			setPostEventReq(pInputMsg, pCCMN01UInputRec);

			try {
				ccmn01uoDto = postEventStageStatusService.postEventOnly(pCCMN01UInputRec);
			} catch (DataNotFoundException e) {
				LOGGER.error("Error occured in CCMN01 service");
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				response.setErrorDto(errorDto);
				retVal = ServiceConstants.FND_FAIL;
			}
			/*
			 ** Call CAUD42D
			 */
			retVal = call42D(pInputMsg, response, ccmn01uoDto, retVal, pCAUD42DInputRec);
		}
		updateTODO(ccmn01uoDto, retVal, pCINV43DInputRec);
		response.setCommonRes(retVal);
		return response;
	}

	/**
	 * Method Name: setPostEventReq Method Description:method to set the request
	 * for post event .
	 *
	 * @param pInputMsg
	 *            the input msg
	 * @param pCCMN01UInputRec
	 *            the CCMN 01 U input rec
	 */
	private void setPostEventReq(ChgStgTypeAudReq pInputMsg, PostEventStageStatusInDto pCCMN01UInputRec) {
		pCCMN01UInputRec.setIdEvent(pInputMsg.getIdEvent());
		if (0 != pCCMN01UInputRec.getIdEvent()) {
			pCCMN01UInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			pCCMN01UInputRec.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		} else {
			pCCMN01UInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			pCCMN01UInputRec.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_ADD);
		}
		pCCMN01UInputRec.setIdEvent(pInputMsg.getIdEvent());
		pCCMN01UInputRec.setIdStage(pInputMsg.getIdStage());
		pCCMN01UInputRec.setIdPerson(pInputMsg.getIdPerson());
		pCCMN01UInputRec.setCdTask(pInputMsg.getCdTask());
		if (!ObjectUtils.isEmpty(pInputMsg.getTxtEventDescr()) && pInputMsg.getTxtEventDescr().length() > 80) {
			pCCMN01UInputRec.setEventDescr(pInputMsg.getTxtEventDescr().substring(ServiceConstants.Zero_INT,
					ServiceConstants.EVENT_TXT_MAX_SIZE));
		} else {
			pCCMN01UInputRec.setEventDescr(pInputMsg.getTxtEventDescr());

		}
		pCCMN01UInputRec.setCdEventStatus(ServiceConstants.STATUS_COMPLETE);
		pCCMN01UInputRec.setDtEventOccurred(new Date());
		pCCMN01UInputRec.setDtEventLastUpdate(pInputMsg.getDtLastUpdate());
		pCCMN01UInputRec.setCdEventType(pInputMsg.getCdEventType());
	}

	/**
	 * Method Name: updateTODO Method Description:.
	 *
	 * @param ccmn01uoDto
	 *            the ccmn 01 uo dto
	 * @param retVal
	 *            the ret val
	 * @param pCINV43DInputRec
	 *            the CINV 43 D input rec
	 */
	private void updateTODO(PostEventStageStatusOutDto ccmn01uoDto, String retVal,
			TodoUpdDtTodoCompletedInDto pCINV43DInputRec) {
		if (ServiceConstants.FND_SUCCESS.equalsIgnoreCase(retVal)) {
			pCINV43DInputRec.setIdEvent(ccmn01uoDto.getIdEvent());
			try {
				todoUpdDtTodoCompletedDao.updateTODOEvent(pCINV43DInputRec);
			} catch (DataNotFoundException e) {
				LOGGER.error("Failed to update Todo Table");
			}
		}
	}

	/**
	 * Method Name: call42D.
	 *
	 * @param pInputMsg
	 *            the input msg
	 * @param response
	 *            the response
	 * @param ccmn01uoDto
	 *            the ccmn 01 uo dto
	 * @param retVal
	 *            the ret val
	 * @param pCAUD42DInputRec
	 *            the CAUD 42 D input rec
	 * @return the string
	 */
	private String call42D(ChgStgTypeAudReq pInputMsg, CommonStringRes response, PostEventStageStatusOutDto ccmn01uoDto,
			String retVal, StageUpdIDByStageTypeInDto pCAUD42DInputRec) {
		String retString = retVal;
		if (!ObjectUtils.isEmpty(ccmn01uoDto)) {
			pCAUD42DInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			pCAUD42DInputRec.setIdStage(pInputMsg.getIdStage());
			pCAUD42DInputRec.setCdStageType(pInputMsg.getCdStageType());
			pCAUD42DInputRec.setDtLastUpdate(pInputMsg.getDtLastUpdate());
			String caud42dDao = stageUpdIDByStageTypeDao.caud42dAUDdam(pCAUD42DInputRec);
			if (!ObjectUtils.isEmpty(caud42dDao) && ServiceConstants.SQL_SUCCESS.equalsIgnoreCase(caud42dDao)) {
				retString = ServiceConstants.FND_SUCCESS;
			} else {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				response.setErrorDto(errorDto);
				retString = ServiceConstants.FND_FAIL;
			}

		}
		return retString;
	}

	/**
	 * Method Name: getStageTaskInput Method Description:method is used to
	 * create request for getting the stage task .
	 *
	 * @param pInputMsg
	 *            the input msg
	 * @return the stage task input
	 */
	private StageTaskInDto getStageTaskInput(ChgStgTypeAudReq pInputMsg) {
		StageTaskInDto pCCMN06UInputRec = new StageTaskInDto();
		if (!ObjectUtils.isEmpty(pInputMsg.getReqFuncCd())) {
			pCCMN06UInputRec.setReqFuncCd(pInputMsg.getReqFuncCd());
		}
		if (!ObjectUtils.isEmpty(pInputMsg.getCdTask())) {
			pCCMN06UInputRec.setCdTask(pInputMsg.getCdTask());
		}
		if (!ObjectUtils.isEmpty(pInputMsg.getIdStage())) {
			pCCMN06UInputRec.setIdStage(pInputMsg.getIdStage());
		}
		return pCCMN06UInputRec;
	}

	/**
	 * This retrieves an entire row from the CPS_CHECKLIST table containing all
	 * the information for the Services and Referrals Checklist window and
	 * checks to see if the SFI questions have been answered to allow the stage
	 * type to be changed to/from SFI.
	 *
	 * @param pInputMsg
	 *            the input msg
	 * @return relVal
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int callCSESC9D(ChgStgTypeAudReq pInputMsg) {
		CpsCheckListInDto pCSESC9DInputRec = new CpsCheckListInDto();
		pCSESC9DInputRec.setIdStage(pInputMsg.getIdStage());
		List<CpsCheckListOutDto> csesc9doDtos = cpsCheckListDao.csesc9dQUERYdam(pCSESC9DInputRec);
		return processServiceReferral(pInputMsg, csesc9doDtos);

	}

	/**
	 * Method Name: processServiceReferral.
	 *
	 * @param pInputMsg
	 *            the input msg
	 * @param csesc9doDtos
	 *            the csesc 9 do dtos
	 * @return the int
	 */
	private int processServiceReferral(ChgStgTypeAudReq pInputMsg, List<CpsCheckListOutDto> csesc9doDtos) {
		String cQuestionsY = "";
		String cDatesValid = "";
		boolean isExit = false;
		int relVal = FND_FAIL;
		Date dtToday = new Date();
		for (CpsCheckListOutDto csesc9doDto : csesc9doDtos) {
			cQuestionsY = getQuestionInd(csesc9doDto);

			cDatesValid = isDateValid(dtToday, csesc9doDto);
			relVal = checkSFIChangeValid(pInputMsg, cQuestionsY, cDatesValid);

			if (FND_FAIL != relVal) {
				isExit = true;
			}

			if (ServiceConstants.STAGE_TYPE_SFI.equalsIgnoreCase(pInputMsg.getCdStageType())) {
				if (ServiceConstants.INDICATOR_YES.equalsIgnoreCase(cDatesValid)
						&& ServiceConstants.INDICATOR_YES.equalsIgnoreCase(cQuestionsY)) {
					relVal = SQL_SUCCESS;
					// isExit = true;
				} else {
					relVal = ServiceConstants.MSG_SFI_NOT_ELIG;
					// isExit = true;
				}
				isExit = true;
			}

			if (isExit) {
				break;
			}
		}
		return relVal;
	}

	/**
	 * Method Name: checkSFIChangeValid Method Description:
	 * 
	 * @param pInputMsg
	 * @param cQuestionsY
	 * @param cDatesValid
	 * @param relVal
	 * @return
	 */
	private int checkSFIChangeValid(ChgStgTypeAudReq pInputMsg, String cQuestionsY, String cDatesValid) {
		int relVal = FND_FAIL;
		if (ServiceConstants.STAGE_TYPE_SFI.equalsIgnoreCase(pInputMsg.getCdOldStageType())) {
			if (ServiceConstants.INDICATOR_YES.equalsIgnoreCase(cDatesValid)
					&& ServiceConstants.INDICATOR_YES.equalsIgnoreCase(cQuestionsY)) {
				relVal = ServiceConstants.MSG_CANT_CHANGE_FROM_SFI;

			} else {
				relVal = SQL_SUCCESS;

			}
		}
		return relVal;
	}

	/**
	 * Method Name: getQuestionInd.
	 *
	 * @param csesc9doDto
	 *            the csesc 9 do dto
	 * @return the question ind
	 */
	private String getQuestionInd(CpsCheckListOutDto csesc9doDto) {
		String cQuestionsY = ServiceConstants.EMPTY_STRING;
		if (ServiceConstants.INDICATOR_YES.equalsIgnoreCase(csesc9doDto.getIndCitizenshipVerify())
				&& ServiceConstants.INDICATOR_YES.equalsIgnoreCase(csesc9doDto.getIndProblemNeglect())
				&& ServiceConstants.INDICATOR_YES.equalsIgnoreCase(csesc9doDto.getIndEligVerifiedByStaff())
				&& ServiceConstants.INDICATOR_YES.equalsIgnoreCase(csesc9doDto.getIndIncomeQualification())
				&& ServiceConstants.INDICATOR_YES.equalsIgnoreCase(csesc9doDto.getIndChildRmvlReturn())) {
			cQuestionsY = ServiceConstants.INDICATOR_YES;
		}
		return cQuestionsY;
	}

	/**
	 * Method Name: isDateValid
	 * 
	 * @param cDatesValid
	 *            the c dates valid
	 * @param dtToday
	 *            the dt today
	 * @param csesc9doDto
	 *            the csesc 9 do dto
	 * @return the string
	 */
	private String isDateValid(Date dtToday, CpsCheckListOutDto csesc9doDto) {
		String cDatesValid = ServiceConstants.EMPTY_STRING;
		long lRCStart;
		long lRCEnd;
		if (!ObjectUtils.isEmpty(csesc9doDto.getDtEligStart()) && !ObjectUtils.isEmpty(csesc9doDto.getDtEligEnd())) {
			lRCStart = dtToday.compareTo(csesc9doDto.getDtEligStart());
			lRCEnd = dtToday.compareTo(csesc9doDto.getDtEligEnd());
			if (lRCStart >= 0 && lRCEnd < 0) {
				cDatesValid = ServiceConstants.INDICATOR_YES;
			}
		}
		return cDatesValid;
	}
}
