package us.tx.state.dfps.service.admin.serviceimpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dao.EventInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.EventPersonLinkInsUpdDelDao;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.EventInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.EventInsUpdDelOutDto;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusOutDto;
import us.tx.state.dfps.service.admin.service.PostEventStageStatusService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PostEventStageStatusServiceImpl Aug 7, 2017- 6:15:29 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PostEventStageStatusServiceImpl implements PostEventStageStatusService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	EventIdDao objCcmn45dDao;

	@Autowired
	EventInsUpdDelDao objCcmn46dDao;

	@Autowired
	EventPersonLinkInsUpdDelDao objCcmn68dDao;

	private static final Logger log = Logger.getLogger(PostEventStageStatusServiceImpl.class);

	/**
	 * 
	 * Method Name: callPostEventStageStatusService Method Description: This
	 * service will perform common-function Post Event.
	 * 
	 * @param pInputMsg
	 * @return PostEventStageStatusOutDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostEventStageStatusOutDto callPostEventStageStatusService(PostEventStageStatusInDto pInputMsg) {
		log.debug("Entering method callCcmn01uService in PostEventStageStatusServiceImpl");
		PostEventStageStatusOutDto pOutputMsg = new PostEventStageStatusOutDto();
		pOutputMsg = PostEvent(pInputMsg);
		log.debug("Exiting method callCcmn01uService in PostEventStageStatusServiceImpl");
		return pOutputMsg;
	}

	/**
	 * 
	 * Method Name: PostEvent Method Description:This service will perform
	 * common-function Post Event.
	 * 
	 * @param pInputMsg
	 * @return PostEventStageStatusOutDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostEventStageStatusOutDto PostEvent(PostEventStageStatusInDto pInputMsg) {
		log.debug("Entering method PostEvent in PostEventStageStatusServiceImpl");
		PostEventStageStatusOutDto pOutputMsg = new PostEventStageStatusOutDto();
		pOutputMsg = CallCCMN46D(pInputMsg);
		if (!ObjectUtils.isEmpty(pOutputMsg) && !ObjectUtils.isEmpty(pOutputMsg.getIdEvent())) {
			CallCCMN68D(pInputMsg);
			if (null != pOutputMsg) {
				if (ServiceConstants.REQ_FUNC_CD_DELETE != pInputMsg.getCdReqFunction()) {
					CallCCMN45D(pInputMsg);
				}
			}
		}
		log.debug("Exiting method PostEvent in PostEventStageStatusServiceImpl");
		return pOutputMsg;
	}

	/**
	 * 
	 * Method Name: PostEvent Method Description:This service will perform
	 * common-function Post Event table only.
	 * 
	 * @param postEventStageStatusInDto
	 * @return PostEventStageStatusOutDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostEventStageStatusOutDto postEventOnly(PostEventStageStatusInDto postEventStageStatusInDto) {
		log.debug("Entering method PostEvent in PostEventStageStatusServiceImpl");
		PostEventStageStatusOutDto postEventStageStatusOutDto = new PostEventStageStatusOutDto();
		postEventStageStatusOutDto = CallCCMN46D(postEventStageStatusInDto);
		log.debug("Exiting method PostEvent in PostEventStageStatusServiceImpl");
		return postEventStageStatusOutDto;
	}

	/**
	 * 
	 * Method Name: CallCCMN46D Method Description:This service will perform
	 * common-function Post Event.
	 * 
	 * @param pInputMsg
	 * @return PostEventStageStatusOutDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostEventStageStatusOutDto CallCCMN46D(PostEventStageStatusInDto pInputMsg) {
		PostEventStageStatusOutDto oCcmn01uoDto = new PostEventStageStatusOutDto();
		EventInsUpdDelInDto iCcmn46diDto = mappContDTOtoCcmn46DTO(pInputMsg);
		EventInsUpdDelOutDto oCcmn46doDto = objCcmn46dDao.ccmn46dAUDdam(iCcmn46diDto);
		if (!TypeConvUtil.isNullOrEmpty(oCcmn46doDto)) {
			if (pInputMsg.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_ADD)) {
				oCcmn01uoDto.setIdEvent(oCcmn46doDto.getIdEvent());
				pInputMsg.setIdEvent(oCcmn46doDto.getIdEvent());
			} else if (pInputMsg.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
				oCcmn01uoDto.setIdEvent(pInputMsg.getIdEvent());
			}
		}
		return oCcmn01uoDto;
	}

	/**
	 * 
	 * Method Name: CallCCMN68D Method Description:This service will perform
	 * common-function Post Event.
	 * 
	 * @param pInputMsg
	 * @return PostEventStageStatusOutDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostEventStageStatusOutDto CallCCMN68D(PostEventStageStatusInDto pInputMsg) {
		log.debug("Entering method CallCCMN68D in PostEventStageStatusServiceImpl");
		EventPersonLinkInsUpdDelInDto pCCMN68DInputRec = new EventPersonLinkInsUpdDelInDto();		
		if (!ObjectUtils.isEmpty(pInputMsg.getIdPerson()) && 0 != pInputMsg.getIdPerson()) {
			if (!ObjectUtils.isEmpty(pInputMsg.getIdEventPerson()) && 0 != pInputMsg.getIdEventPerson())
			{
				pInputMsg.setIdPerson(pInputMsg.getIdEventPerson());
			}
			pCCMN68DInputRec = mappContDTOtoCcmn68DTO(pInputMsg);
			objCcmn68dDao.updateEventPersonLink(pCCMN68DInputRec);
		}
		PostEventStageStatusOutDto oCcmn01uoDto = new PostEventStageStatusOutDto();
		log.debug("Exiting method CallCCMN68D in PostEventStageStatusServiceImpl");
		return oCcmn01uoDto;
	}

	/**
	 * 
	 * Method Name: CallCCMN45D Method Description:This service will perform
	 * common-function Post Event.
	 * 
	 * @param pInputMsg
	 * @return PostEventStageStatusOutDto @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostEventStageStatusOutDto CallCCMN45D(PostEventStageStatusInDto pInputMsg) {
		log.debug("Entering method CallCCMN45D in PostEventStageStatusServiceImpl");
		EventIdInDto pCCMN45DInputRec = new EventIdInDto();
		List<EventIdOutDto> pCCMN45DOutputRec = new ArrayList<EventIdOutDto>();
		PostEventStageStatusOutDto pOutputMsg = new PostEventStageStatusOutDto();
		if (0 != pInputMsg.getIdEvent()) {
			pCCMN45DInputRec.setIdEvent(pInputMsg.getIdEvent());
			pCCMN45DOutputRec = objCcmn45dDao.getEventDetailList(pCCMN45DInputRec);
		}
		pOutputMsg.setRowCount(pCCMN45DOutputRec.size());
		log.debug("Exiting method CallCCMN45D in PostEventStageStatusServiceImpl");
		return pOutputMsg;
	}

	/**
	 * 
	 * Method Name: mappContDTOtoCcmn68DTO Method Description:This service will
	 * perform common-function Post Event.
	 * 
	 * @param pInputDto
	 * @return EventPersonLinkInsUpdDelInDto
	 */
	public EventPersonLinkInsUpdDelInDto mappContDTOtoCcmn68DTO(PostEventStageStatusInDto pInputDto) {
		EventPersonLinkInsUpdDelInDto pCCMN68DInputRec = new EventPersonLinkInsUpdDelInDto();
		pCCMN68DInputRec.setIdEvent(pInputDto.getIdEvent());
		pCCMN68DInputRec.setIdPerson(pInputDto.getIdPerson());
		pCCMN68DInputRec.setTsLastUpdate(pInputDto.getDtEventLastUpdate());
		pCCMN68DInputRec.setCdNotice(pInputDto.getCdNotice());
		pCCMN68DInputRec.setDistMethod(pInputDto.getDistMethod());
		if (null != pInputDto.getCdScrDataAction())
			pCCMN68DInputRec.setCdReqFunction(pInputDto.getCdScrDataAction());
		else
			pCCMN68DInputRec.setCdReqFunction(pInputDto.getReqFuncCd());
		if (!ObjectUtils.isEmpty(pInputDto.getLirowCcmn01UiG00()) && pInputDto.getLirowCcmn01UiG00().size() > 0) {
			if (!ObjectUtils.isEmpty(pInputDto.getLirowCcmn01UiG00().get(0).getIdPerson())) {
				pCCMN68DInputRec.setIdPerson(pInputDto.getLirowCcmn01UiG00().get(0).getIdPerson());
			}
			pCCMN68DInputRec.setCdScrDataAction(pInputDto.getLirowCcmn01UiG00().get(0).getCdScrDataAction());
		}
		return pCCMN68DInputRec;
	}

	/**
	 * 
	 * Method Name: mappContDTOtoCcmn46DTO Method Description:This service will
	 * perform common-function Post Event.
	 * 
	 * @param pInputDto
	 * @return EventInsUpdDelInDto
	 */
	public EventInsUpdDelInDto mappContDTOtoCcmn46DTO(PostEventStageStatusInDto pInputDto) {
		EventInsUpdDelInDto Ccmn46diDto = new EventInsUpdDelInDto();
		Ccmn46diDto.setDtEventLastUpdate(pInputDto.getDtEventLastUpdate());
		Ccmn46diDto.setCdEventStatus(pInputDto.getCdEventStatus());
		Ccmn46diDto.setCdTask(pInputDto.getCdTask());
		Ccmn46diDto.setCdEventType(pInputDto.getCdEventType());
		Ccmn46diDto.setDtEventOccurred(pInputDto.getDtEventOccurred());
		Ccmn46diDto.setIdEvent(pInputDto.getIdEvent());
		Ccmn46diDto.setIdPerson(pInputDto.getIdPerson());
		Ccmn46diDto.setIdStage(pInputDto.getIdStage());
		Ccmn46diDto.setEventDescr(pInputDto.getEventDescr());
		Ccmn46diDto.setCdReqFunction(pInputDto.getReqFuncCd());
		return Ccmn46diDto;
	}

	/**
	 * 
	 * Method Name: convertDateformat Method Description:This method convert the
	 * date in required format.
	 * 
	 * @param oldDateString
	 * @return Date
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
