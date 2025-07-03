package us.tx.state.dfps.service.casepackage.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.service.PostEventStageStatusService;
import us.tx.state.dfps.service.casepackage.dao.SpecialHandlingAudDao;
import us.tx.state.dfps.service.casepackage.dao.SpecialHandlingCaseDetailFetchDao;
import us.tx.state.dfps.service.casepackage.dao.SpecialHandlingSensitiveUpdateDao;
import us.tx.state.dfps.service.casepackage.dao.SpecialHandlingStageDetailFetchDao;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingAudInDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingAudOutDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingCaseDetailInDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingCaseDetailOutDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingSensitiveUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingSensitiveUpdateOutDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingStageDetailInDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingStageDetailOutDto;
import us.tx.state.dfps.service.casepackage.service.SpecialHandlingAudService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.SpecialHandlingAudReq;
import us.tx.state.dfps.service.common.response.SpecialHandlingAudRes;
import us.tx.state.dfps.service.cvs.dto.EventRowUpdateTwoDto;
import us.tx.state.dfps.service.workload.dto.StageIdDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * while perform AUD operations for the special handling. It will update part of
 * one row in the CAPS CASE table. Apr 12, 2018- 3:59:43 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class SpecialHandlingAudServiceImpl implements SpecialHandlingAudService {

	@Autowired
	MessageSource messageSource;

	// Ccmng4d
	@Autowired
	SpecialHandlingAudDao specialHandlingAudDao;

	// Cinve7d
	@Autowired
	SpecialHandlingSensitiveUpdateDao specialHandlingSensitiveUpdateDao;

	// Ccmnb1d
	@Autowired
	SpecialHandlingCaseDetailFetchDao specialHandlingCaseDetailFetchDao;

	// Ccmne1d
	@Autowired
	SpecialHandlingStageDetailFetchDao specialHandlingStageDetailFetchDao;

	// Ccmn01u
	@Autowired
	PostEventStageStatusService postEventStageStatusService;
	
	@Autowired
	StageDao stageDao;

	public static final String STAGE_TO_BE_UPDATED_STRING = "INT";
	public static final String STAGE_FAD = "FAD";
	public static final String STAGE_PAD = "PAD";
	public static final String RECORD_SENSITIVE_EVENT_DESC = "Sensitive start";
	public static final String RECORD_UNSENSITIVE_EVENT_DESC = "Sensitive end";
	public static final String EVENT_TYPE_SENSITIVE = "SEN";
	public static final String EVENT_STATUS_COMPLETE = "COMP";
	public static final String INDICATOR_NO = "N";
	public static final String INDICATOR_YES = "Y";

	private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeServiceLog");

	/**
	 * Method Name: specialHandlingAud Method Description: Handles AUD for
	 * Special handling in case summary
	 * 
	 * @param specialHandlingAudReq
	 * @return SpecialHandlingAudRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SpecialHandlingAudRes specialHandlingAud(SpecialHandlingAudReq specialHandlingAudReq) {
		log.debug("Entering method specialHandlingAud in SpecialHandlingAudServiceImpl");
		String tempIndCaseSensitive = ServiceConstants.EMPTY_STRING;
		SpecialHandlingAudRes specialHandlingAudRes = new SpecialHandlingAudRes();
		tempIndCaseSensitive = retrieveCapsCaseDetail(specialHandlingAudReq, specialHandlingAudRes);
		updateSpecialHandling(specialHandlingAudReq, specialHandlingAudRes);
		retrieveStageDetail(specialHandlingAudReq, specialHandlingAudRes, tempIndCaseSensitive);
		log.debug("Exiting method specialHandlingAud in SpecialHandlingAudServiceImpl");
		return specialHandlingAudRes;
	}

	/**
	 * Method Name: updateSpecialHandling Method Description: This method will
	 * update the sensitivity, special handling and worker safety issues for a
	 * case. DAM CCMNG4D
	 * 
	 * @param specialHandlingAudReq
	 * @param specialHandlingAudRes
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateSpecialHandling(SpecialHandlingAudReq specialHandlingAudReq,
			SpecialHandlingAudRes specialHandlingAudRes) {
		log.debug("Entering method updateSpecialHandling in SpecialHandlingAudServiceImpl");
		SpecialHandlingAudInDto specialHandlingAudInDto = new SpecialHandlingAudInDto();
		SpecialHandlingAudOutDto specialHandlingAudOutDto = new SpecialHandlingAudOutDto();

		specialHandlingAudInDto.setIdCase(specialHandlingAudReq.getSpecHD().getIdCase());
		specialHandlingAudInDto.setCdCaseSpeclHndlg(specialHandlingAudReq.getSpecHD().getCdCaseSpeclHndlg());
		specialHandlingAudInDto.setIndCaseSensitive(specialHandlingAudReq.getSpecHD().getIndCaseSensitive());
		specialHandlingAudInDto.setIndCaseWorkerSafety(specialHandlingAudReq.getSpecHD().getIndCaseWorkerSafety());
		specialHandlingAudInDto.setTxtSpecHandling(specialHandlingAudReq.getSpecHD().getSpecHandling());
		specialHandlingAudInDto.setTxtCaseWorkerSafety(specialHandlingAudReq.getSpecHD().getCaseWorkerSafety());
		specialHandlingAudInDto.setTxtCaseSensitiveCmnts(specialHandlingAudReq.getSpecHD().getCaseSensitiveCmnts());
		specialHandlingAudInDto.setIndCaseAlert(specialHandlingAudReq.getIndCaseAlert());
		specialHandlingAudInDto.setIndSafetyCheckList(specialHandlingAudReq.getIndSafetyCheckList());
		specialHandlingAudInDto.setIndLitigationHold(specialHandlingAudReq.getSpecHD().getIndLitigationHold());
		specialHandlingAudInDto.setTxtLitigationHold(specialHandlingAudReq.getSpecHD().getLitigationHold());
		specialHandlingAudInDto.setIndMediaAttention(specialHandlingAudReq.getSpecHD().getIndMediaAttention());
		specialHandlingAudInDto.setTxtMediaAttention(specialHandlingAudReq.getSpecHD().getMediaAttentionComments());
		specialHandlingAudInDto.setIndSelfReport(specialHandlingAudReq.getSpecHD().getIndSelfReport());
		specialHandlingAudInDto.setTxtSelfReport(specialHandlingAudReq.getSpecHD().getSelfReportComments());
		specialHandlingAudInDto.setReqFuncCd(specialHandlingAudReq.getReqFuncCd());
		specialHandlingAudInDto.setSysTsLastUpdate2(specialHandlingAudReq.getSpecHD().getTsSysTsLastUpdate2());
		specialHandlingAudDao.specialHandlingAud(specialHandlingAudInDto, specialHandlingAudOutDto);
		if (specialHandlingAudOutDto != null && !ObjectUtils.isEmpty(specialHandlingAudReq.getCdStage())) { // 10994 - Do null check for cdStage

			if ((!specialHandlingAudReq.getCdStage().equalsIgnoreCase(STAGE_FAD))
					|| (!specialHandlingAudReq.getCdStage().equalsIgnoreCase(STAGE_PAD))) {
				updateSensitiveIndicator(specialHandlingAudReq, specialHandlingAudRes);
				if (specialHandlingAudRes != null) {

				}
			}

		}

		log.debug("Exiting method updateSpecialHandling in SpecialHandlingAudServiceImpl");
	}

	/**
	 * Method Name: updateSensitiveIndicator Method Description: Update the
	 * Sensitive Indicator in incoming detail table for the cases which are
	 * marked sensitive in either Intake or in Investigation. DAM CINVE7D
	 * 
	 * @param specialHandlingAudReq
	 * @param specialHandlingAudRes
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateSensitiveIndicator(SpecialHandlingAudReq specialHandlingAudReq,
			SpecialHandlingAudRes specialHandlingAudRes) {
		log.debug("Entering method updateSensitiveIndicator in SpecialHandlingAudServiceImpl");
		SpecialHandlingSensitiveUpdateInDto specialHandlingSensitiveUpdateInDto = new SpecialHandlingSensitiveUpdateInDto();
		SpecialHandlingSensitiveUpdateOutDto specialHandlingSensitiveUpdateOutDto = new SpecialHandlingSensitiveUpdateOutDto();

		specialHandlingSensitiveUpdateInDto.setIdCase(specialHandlingAudReq.getSpecHD().getIdCase());
		specialHandlingSensitiveUpdateInDto
				.setIndCaseSensitive(specialHandlingAudReq.getSpecHD().getIndCaseSensitive());
		specialHandlingSensitiveUpdateInDto.setCdStageType(STAGE_TO_BE_UPDATED_STRING);
		specialHandlingSensitiveUpdateDao.specialHandlingSensitiveUpdate(specialHandlingSensitiveUpdateInDto,
				specialHandlingSensitiveUpdateOutDto);

		log.debug("Exiting method updateSensitiveIndicator in SpecialHandlingAudServiceImpl");
	}

	/**
	 * Method Name: CallCCMNB1D Method Description: Retrieve one row from the
	 * CAPS_CASE table for a given ID_CASE. DAM CCMNB1D
	 * 
	 * @param specialHandlingAudReq
	 * @param specialHandlingAudRes
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String retrieveCapsCaseDetail(SpecialHandlingAudReq specialHandlingAudReq,
			SpecialHandlingAudRes specialHandlingAudRes) {
		log.debug("Entering method retrieveCapsCaseDetail in SpecialHandlingAudServiceImpl");
		String tempIndCaseSensitive = ServiceConstants.EMPTY_STRING;
		SpecialHandlingCaseDetailInDto specialHandlingCaseDetailInDto = new SpecialHandlingCaseDetailInDto();
		SpecialHandlingCaseDetailOutDto specialHandlingCaseDetailOutDto = new SpecialHandlingCaseDetailOutDto();
		specialHandlingCaseDetailInDto.setIdCase(specialHandlingAudReq.getSpecHD().getIdCase());
		specialHandlingCaseDetailOutDto = specialHandlingCaseDetailFetchDao
				.specialHandlingCaseDetailFetch(specialHandlingCaseDetailInDto);

		if (specialHandlingCaseDetailOutDto != null) {
			tempIndCaseSensitive = specialHandlingCaseDetailOutDto.getIndCaseSensitive();
		}

		log.debug("Exiting method retrieveCapsCaseDetail in SpecialHandlingAudServiceImpl");

		return tempIndCaseSensitive;
	}

	/**
	 * Method Name: CallCCMNE1D Method Description: This DAM retrieves ID STAGE,
	 * CD STAGE TYPE, and DT STAGE CLOSE for all stages linked to an ID CASE on
	 * the STAGE table. DAM CCMNE1D
	 * 
	 * @param specialHandlingAudReq
	 * @param specialHandlingAudRes
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void retrieveStageDetail(SpecialHandlingAudReq specialHandlingAudReq,
			SpecialHandlingAudRes specialHandlingAudRes, String tempIndCaseSensitive) {
		log.debug("Entering method retrieveStageDetail in SpecialHandlingAudServiceImpl");
		SpecialHandlingStageDetailInDto specialHandlingStageDetailInDto = new SpecialHandlingStageDetailInDto();
		SpecialHandlingStageDetailOutDto specialHandlingStageDetailOutDto = new SpecialHandlingStageDetailOutDto();

		specialHandlingStageDetailInDto.setIdCase(specialHandlingAudReq.getSpecHD().getIdCase());
		specialHandlingStageDetailFetchDao.specialHandlingStageDetailFetch(specialHandlingStageDetailInDto,
				specialHandlingStageDetailOutDto);
		if (specialHandlingStageDetailOutDto != null) {
			if (((INDICATOR_YES.equals(specialHandlingAudReq.getSpecHD().getIndCaseSensitive()))
					&& (!INDICATOR_YES.equals(tempIndCaseSensitive)))
					|| ((!INDICATOR_YES.equals(specialHandlingAudReq.getSpecHD().getIndCaseSensitive()))
							&& (INDICATOR_YES.equals(tempIndCaseSensitive)))) {
				postEvent(specialHandlingAudReq, specialHandlingAudRes, specialHandlingStageDetailOutDto,
						tempIndCaseSensitive);
			}

		}

		log.debug("Exiting method retrieveStageDetail in SpecialHandlingAudServiceImpl");
	}

	/**
	 * Method Name: postEvent Method Description: Handles EVENT Creation
	 * 
	 * @param specialHandlingAudReq
	 * @param specialHandlingAudRes
	 * @param specialHandlingStageDetailOutDto
	 * @param tempIndCaseSensitive
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void postEvent(SpecialHandlingAudReq specialHandlingAudReq, SpecialHandlingAudRes specialHandlingAudRes,
			SpecialHandlingStageDetailOutDto specialHandlingStageDetailOutDto, String tempIndCaseSensitive) {
		log.debug("Entering method postEvent in SpecialHandlingAudServiceImpl");
		List<Long> stageList = new ArrayList<Long>();
		PostEventStageStatusInDto postEventStageStatusInDto = new PostEventStageStatusInDto();
		EventRowUpdateTwoDto eventRowUpdateTwoDto = new EventRowUpdateTwoDto();
		postEventStageStatusInDto.setRowCcmn01UiG00(eventRowUpdateTwoDto);
		postEventStageStatusInDto.setDtEventOccurred(null);
		postEventStageStatusInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventStageStatusInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventStageStatusInDto.setCdEventType(EVENT_TYPE_SENSITIVE);
		// pCCMN01UInputRec.getRowCcmn01UiG00().setIdPerson((int)
		// pInputMsg.getSpecHD().getIdPerson());
		postEventStageStatusInDto.setIdPerson(specialHandlingAudReq.getSpecHD().getIdPerson());
		postEventStageStatusInDto.setDtEventOccurred(new Date());
		if ((INDICATOR_YES.equals(specialHandlingAudReq.getSpecHD().getIndCaseSensitive()))
				&& (!INDICATOR_YES.equals(tempIndCaseSensitive))) {
			postEventStageStatusInDto.setEventDescr(RECORD_SENSITIVE_EVENT_DESC);
		} else if ((!INDICATOR_YES.equals(specialHandlingAudReq.getSpecHD().getIndCaseSensitive()))
				&& (INDICATOR_YES.equals(tempIndCaseSensitive))) {
			postEventStageStatusInDto.setEventDescr(RECORD_UNSENSITIVE_EVENT_DESC);
		}

		postEventStageStatusInDto.setCdTask(null);
		postEventStageStatusInDto.setCdEventStatus(EVENT_STATUS_COMPLETE);
		if (!ObjectUtils.isEmpty(specialHandlingAudReq.getSpecHD().getIdStage()) 
				&& specialHandlingAudReq.getSpecHD().getIdStage() > 0){
			stageList.add(specialHandlingAudReq.getSpecHD().getIdStage());
		}else{
			List<StageIdDto> stageListDto = stageDao.searchStageByCaseId(specialHandlingAudReq.getSpecHD().getIdCase());
			if (!ObjectUtils.isEmpty(stageListDto)){
				stageListDto.forEach(dto->stageList.add(dto.getIdStage()));
			}
		}
		for(Long idStage:stageList){
			postEventStageStatusInDto.setIdStage(idStage);
			postEventStageStatusService.callPostEventStageStatusService(postEventStageStatusInDto);
		}

		log.debug("Exiting method postEvent in SpecialHandlingAudServiceImpl");
	}

}
