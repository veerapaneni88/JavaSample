package us.tx.state.dfps.service.casemanagement.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dao.StageRegionDao;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.StageRegionInDto;
import us.tx.state.dfps.service.admin.dto.StageRegionOutDto;
import us.tx.state.dfps.service.casemanagement.dto.ChgStgTypeRtrvoDto;
import us.tx.state.dfps.service.casemanagement.service.ChgStgTypeRtrvService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ChgStgTypeRtrvReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * The Class ChgStgTypeRtrvServiceImpl.
 */
@Service
@Transactional
public class ChgStgTypeRtrvServiceImpl implements ChgStgTypeRtrvService {

	/** The obj cint 40 d dao. */
	@Autowired
	private StageRegionDao objCint40dDao;

	/** The obj ccmn 45 d dao. */
	@Autowired
	private EventIdDao objCcmn45dDao;

	/** The Constant log. */
	private static final Logger LOGGER = Logger.getLogger(ChgStgTypeRtrvServiceImpl.class);

	/**
	 * Method Description: This Method will retrieve the Stage and Event Details
	 * for particular stage and event id by calling the DAO layer. Service Name
	 * : CSUB63S
	 *
	 * @param pInputMsg
	 *            the input msg
	 * @return objChgStgTypeRtrvoDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<ChgStgTypeRtrvoDto> callChgStgTypeRtrvService(ChgStgTypeRtrvReq pInputMsg) {
		LOGGER.debug("Entering method callChgStgTypeRtrvService in ChgStgTypeRtrvServiceImpl");
		List<ChgStgTypeRtrvoDto> objChgStgTypeRtrvoDto = null;
		StageRegionInDto pCINT40DInputRec = new StageRegionInDto();
		EventIdInDto pCCMN45DInputRec = new EventIdInDto();
		if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getIdStage())) {
			pCINT40DInputRec.setIdStage(pInputMsg.getIdStage());
		}
		List<StageRegionOutDto> cint40doDtos = objCint40dDao.getStageDtls(pCINT40DInputRec);
		if (!ObjectUtils.isEmpty(cint40doDtos)) {
			objChgStgTypeRtrvoDto = new ArrayList<>();
			for (StageRegionOutDto cint40doDto : cint40doDtos) {
				ChgStgTypeRtrvoDto pOutputMsg = new ChgStgTypeRtrvoDto();
				pOutputMsg.setCdEventStatus(ServiceConstants.STATUS_NEW);
				if (!TypeConvUtil.isNullOrEmpty(cint40doDto.getCdStageType())) {
					pOutputMsg.setCdStageType(cint40doDto.getCdStageType());
				}
				if (!TypeConvUtil.isNullOrEmpty(cint40doDto.getCdStage())) {
					pOutputMsg.setCdStage(cint40doDto.getCdStage());
				}
				if (!TypeConvUtil.isNullOrEmpty(cint40doDto.getTsLastUpdate())) {
					pOutputMsg.setDtLastUpdate(cint40doDto.getTsLastUpdate());
				}
				processForEventDetails(pInputMsg, pCCMN45DInputRec, pOutputMsg);
				objChgStgTypeRtrvoDto.add(pOutputMsg);
			}
		}
		LOGGER.debug("Exiting method callChgStgTypeRtrvService in ChgStgTypeRtrvServiceImpl");
		return objChgStgTypeRtrvoDto;
	}

	/**
	 * Method Name: getEventDetails Method Description:
	 * 
	 * @param pInputMsg
	 * @param pCCMN45DInputRec
	 * @param pOutputMsg
	 */
	private void processForEventDetails(ChgStgTypeRtrvReq pInputMsg, EventIdInDto pCCMN45DInputRec,
			ChgStgTypeRtrvoDto pOutputMsg) {
		if (pInputMsg.getIdEvent() != 0) {
			pCCMN45DInputRec.setIdEvent(pInputMsg.getIdEvent());
			List<EventIdOutDto> ccmn45doDtos = objCcmn45dDao.getEventDetailList(pCCMN45DInputRec);
			processEventDetails(pOutputMsg, ccmn45doDtos);

		}
	}

	/**
	 * Method Name: processEventDetails
	 * 
	 * @param pOutputMsg
	 * @param ccmn45doDtos
	 */
	private void processEventDetails(ChgStgTypeRtrvoDto pOutputMsg, List<EventIdOutDto> ccmn45doDtos) {
		for (EventIdOutDto ccmn45doDto : ccmn45doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto.getIdEvent())) {
				pOutputMsg.setIdEvent(ccmn45doDto.getIdEvent());
			}
			if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto.getIdStage())) {
				pOutputMsg.setIdStage(ccmn45doDto.getIdStage());
			}
			if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto.getIdPerson())) {
				pOutputMsg.setIdPerson(ccmn45doDto.getIdPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto.getCdTask())) {
				pOutputMsg.setCdTask(ccmn45doDto.getCdTask());
			}
			if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto.getCdEventType())) {
				pOutputMsg.setCdEventType(ccmn45doDto.getCdEventType());
			}
			if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto.getCdEventStatus())) {
				pOutputMsg.setCdEventStatus(ccmn45doDto.getCdEventStatus());
			}
			if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto.getEventDescr())) {
				pOutputMsg.setTxtEventDescr(ccmn45doDto.getEventDescr());
			}
			setDates(pOutputMsg, ccmn45doDto);
		}
	}

	/**
	 * Method Name: setDates.
	 *
	 * @param pOutputMsg
	 *            the output msg
	 * @param ccmn45doDto
	 *            the ccmn 45 do dto
	 */
	private void setDates(ChgStgTypeRtrvoDto pOutputMsg, EventIdOutDto ccmn45doDto) {
		if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto.getDtEventOccurred())) {
			pOutputMsg.setDtEventOccurred(ccmn45doDto.getDtEventOccurred());
		}

		if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto.getTsLastUpdate())) {
			pOutputMsg.setDtLastUpdate(ccmn45doDto.getTsLastUpdate());
		}
	}
}
