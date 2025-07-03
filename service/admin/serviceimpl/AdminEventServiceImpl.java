package us.tx.state.dfps.service.admin.serviceimpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dao.EventAdminDao;
import us.tx.state.dfps.service.admin.dao.EventPersonLinkAdminDao;
import us.tx.state.dfps.service.admin.dao.FetchEventDetailDao;
import us.tx.state.dfps.service.admin.dto.AdminEventInputDto;
import us.tx.state.dfps.service.admin.dto.AdminEventOutputDto;
import us.tx.state.dfps.service.admin.dto.EventDataInputDto;
import us.tx.state.dfps.service.admin.dto.EventDataOutputDto;
import us.tx.state.dfps.service.admin.dto.EventPersonAdminLinkDto;
import us.tx.state.dfps.service.admin.dto.FetchEventAdminDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDetailDto;
import us.tx.state.dfps.service.admin.service.AdminEventService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Ccmn01uServiceImpl Aug 7, 2017- 6:15:29 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class AdminEventServiceImpl implements AdminEventService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FetchEventDetailDao fetchEventDetailDao;

	@Autowired
	EventAdminDao eventAdminDao;

	@Autowired
	EventPersonLinkAdminDao objCcmn68dDao;

	private static final Logger log = Logger.getLogger(AdminEventServiceImpl.class);

	/**
	 * PostEvent
	 *
	 * @param pInputMsg
	 * @return pOutputMsg @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public AdminEventOutputDto postEvent(AdminEventInputDto pInputMsg) {
		log.debug("Entering method PostEvent in Ccmn01uServiceImpl");
		AdminEventOutputDto pOutputMsg = new AdminEventOutputDto();

		pOutputMsg = CallCCMN46D(pInputMsg);
		if (null != pOutputMsg) {
			pOutputMsg = CallCCMN68D(pInputMsg);
			if (null != pOutputMsg) {
				if (ServiceConstants.REQ_FUNC_CD_DELETE != pInputMsg.getReqFunctionCd()) {
					pOutputMsg = CallCCMN45D(pInputMsg);
				}
			}
		}

		log.debug("Exiting method PostEvent in Ccmn01uServiceImpl");
		return pOutputMsg;
	}

	/**
	 * CallCCMN46D
	 *
	 * @param pInputMsg
	 * @return oCcmn01uoDto @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public AdminEventOutputDto CallCCMN46D(AdminEventInputDto pInputMsg) {

		AdminEventOutputDto adminEventOutputDto = new AdminEventOutputDto();

		EventDataInputDto eventDataInput = mappContDTOtoCcmn46DTO(pInputMsg);

		EventDataOutputDto eventDataOutout = eventAdminDao.postEvent(eventDataInput);

		if (null != eventDataOutout) {

			if (pInputMsg.getReqFunctionCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_ADD)) {
				adminEventOutputDto.setIdEvent(eventDataOutout.getIdEvent());
				pInputMsg.setIdEvent(eventDataOutout.getIdEvent());
			} else if (pInputMsg.getReqFunctionCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
				adminEventOutputDto.setIdEvent(pInputMsg.getIdEvent());
			}

		}

		return adminEventOutputDto;
	}

	/**
	 * CallCCMN68D
	 *
	 * @param pInputMsg
	 * @return oCcmn01uoDto @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public AdminEventOutputDto CallCCMN68D(AdminEventInputDto pInputMsg) {
		log.debug("Entering method CallCCMN68D in Ccmn01uServiceImpl");

		EventPersonAdminLinkDto pCCMN68DInputRec = new EventPersonAdminLinkDto();
		if (pInputMsg.getLiRowccmn01uig01().size() > 0) {
			if (pInputMsg.getLiRowccmn01uig01().get(0).getIdPerson() != 0) {
				pCCMN68DInputRec = mappContDTOtoCcmn68DTO(pInputMsg);
				objCcmn68dDao.updateEventPersonLink(pCCMN68DInputRec);
			}
		}
		AdminEventOutputDto oCcmn01uoDto = new AdminEventOutputDto();
		if (pInputMsg.getIdEvent() > 0) {
			oCcmn01uoDto.setIdEvent(pInputMsg.getIdEvent());
		}
		log.debug("Exiting method CallCCMN68D in Ccmn01uServiceImpl");
		return oCcmn01uoDto;
	}

	/**
	 * CallCCMN45D
	 *
	 * @param pInputMsg
	 * @return pOutputMsg @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public AdminEventOutputDto CallCCMN45D(AdminEventInputDto pInputMsg) {
		log.debug("Entering method CallCCMN45D in Ccmn01uServiceImpl");
		FetchEventAdminDto pCCMN45DInputRec = new FetchEventAdminDto();
		FetchEventDetailDto pCCMN45DOutputRec = new FetchEventDetailDto();
		AdminEventOutputDto pOutputMsg = new AdminEventOutputDto();

		if (0 != pInputMsg.getIdEvent()) {
			if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(pInputMsg.getReqFunctionCd())) {
				pCCMN45DInputRec.setIdEvent(pInputMsg.getIdEvent());
			} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(pInputMsg.getReqFunctionCd())) {
				pCCMN45DInputRec.setIdEvent(pInputMsg.getIdEvent());
			} else {
				pCCMN45DInputRec.setIdEvent(pInputMsg.getIdEvent());
			}
			pCCMN45DOutputRec = fetchEventDetailDao.getEventDetail(pCCMN45DInputRec);
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN45DOutputRec) && !TypeConvUtil.isNullOrEmpty(pCCMN45DOutputRec)) {
			FetchEventDetailDto ccmn45doDto = pCCMN45DOutputRec;
			pOutputMsg.setIdEvent(ccmn45doDto.getIdEvent());
			if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto.getDtLastUpdate())) {
				pOutputMsg.setDtLastUpdate(ccmn45doDto.getDtLastUpdate().toString());
			}

		}

		// pOutputMsg.setRowCount(pCCMN45DOutputRec);

		log.debug("Exiting method CallCCMN45D in Ccmn01uServiceImpl");
		return pOutputMsg;
	}

	/**
	 * mappContDTOtoCcmn68DTO
	 *
	 * @param pInputDto
	 * @return pCCMN68DInputRec
	 */
	public EventPersonAdminLinkDto mappContDTOtoCcmn68DTO(AdminEventInputDto pInputDto) {

		EventPersonAdminLinkDto pCCMN68DInputRec = new EventPersonAdminLinkDto();
		pCCMN68DInputRec.setUlIdEvent(pInputDto.getIdEvent());
		pCCMN68DInputRec.setUlIdPerson(pInputDto.getLiRowccmn01uig01().get(0).getIdPerson());
		pCCMN68DInputRec.setSzCdScrDataAction(pInputDto.getLiRowccmn01uig01().get(0).getCdScrDataAction());
		if (null != pInputDto.getLiRowccmn01uig01().get(0).getCdScrDataAction())
			pCCMN68DInputRec.setReqFunctionCd(pInputDto.getLiRowccmn01uig01().get(0).getCdScrDataAction());
		else
			pCCMN68DInputRec.setReqFunctionCd(pInputDto.getReqFunctionCd());

		return pCCMN68DInputRec;
	}

	/**
	 * mappContDTOtoCcmn68DTO
	 *
	 * @param pInputDto
	 * @return Ccmn46diDto
	 */
	public EventDataInputDto mappContDTOtoCcmn46DTO(AdminEventInputDto pInputDto) {
		EventDataInputDto Ccmn46diDto = new EventDataInputDto();

		Ccmn46diDto.setEventLastUpdate(pInputDto.getEventLastUpdate());
		Ccmn46diDto.setCdEventStatus(pInputDto.getCdEventStatus());
		Ccmn46diDto.setCdTask(pInputDto.getCdTask());
		Ccmn46diDto.setCdEventType(pInputDto.getCdEventType());
		Ccmn46diDto.setDtDtEventOccurred(pInputDto.getDtDtEventOccurred());
		Ccmn46diDto.setIdEvent(pInputDto.getIdEvent());
		Ccmn46diDto.setIdPerson(pInputDto.getIdPerson());
		Ccmn46diDto.setIdStage(pInputDto.getIdStage());
		Ccmn46diDto.setTxtEventDescr(pInputDto.getTxtEventDescr());
		Ccmn46diDto.setReqFunctionCd(pInputDto.getReqFunctionCd());
		Ccmn46diDto.setDtDtEventCreated(pInputDto.getDtEventCreated());

		return Ccmn46diDto;
	}

	/**
	 * convertDateformat
	 *
	 * @param oldDateString
	 * @return convertedDate
	 */
	public Date convertDateformat(String oldDateString) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date convertedDate = new Date();
		try {
			convertedDate = df.parse(oldDateString);
		} catch (ParseException e) {
			log.debug("Date convertion issue." + e.getMessage());
		}

		return convertedDate;
	}
}
