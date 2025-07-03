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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dao.EventPersonLinkProcessDao;
import us.tx.state.dfps.service.admin.dao.EventProcessDao;
import us.tx.state.dfps.service.admin.dao.FetchEventDao;
import us.tx.state.dfps.service.admin.dto.APSRoraRowDto;
import us.tx.state.dfps.service.admin.dto.EventInputDto;
import us.tx.state.dfps.service.admin.dto.EventLinkInDto;
import us.tx.state.dfps.service.admin.dto.EventOutputDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDto;
import us.tx.state.dfps.service.admin.dto.FetchEventResultDto;
import us.tx.state.dfps.service.admin.dto.PostDto;
import us.tx.state.dfps.service.admin.dto.PostOutputDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventPersonLinkDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.xmlstructs.inputstructs.DomicileDeprivationChildToEventDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;
import us.tx.state.dfps.xmlstructs.inputstructs.SynchronizationServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PostEventServiceImpl Aug 7, 2017- 6:15:29 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class PostEventServiceImpl implements PostEventService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FetchEventDao objCcmn45dDao;

	@Autowired
	EventProcessDao objCcmn46dDao;

	@Autowired
	EventPersonLinkProcessDao objCcmn68dDao;
	
	@Autowired
	private EventDao eventDao;

	@Autowired
	private EventPersonLinkDao eventPersonLinkDao;

	private static final Logger log = Logger.getLogger(PostEventServiceImpl.class);

	/**
	 * callCcmn01uService
	 *
	 * @param postDto
	 * @return pOutputMsg @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostOutputDto callCcmn01uService(PostDto postDto) {
		log.debug("Entering method callCcmn01uService in PostEventServiceImpl");

		PostOutputDto postOutputDto = new PostOutputDto();
		postOutputDto = PostEvent(postDto);

		log.debug("Exiting method callCcmn01uService in PostEventServiceImpl");
		return postOutputDto;
	}

	/**
	 * PostEvent
	 *
	 * @param postDto
	 * @return pOutputMsg
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostOutputDto PostEvent(PostDto postDto) {

		PostOutputDto postOutputDto = new PostOutputDto();

		postOutputDto = CallCCMN46D(postDto);
		if (null != postOutputDto) {
			postOutputDto = CallCCMN68D(postDto);
			if (null != postOutputDto) {
				if (ServiceConstants.REQ_FUNC_CD_DELETE != postDto.getArchInputStructDto().getCreqFuncCd()) {
					postOutputDto = CallCCMN45D(postDto);
				}
			}
		}

		return postOutputDto;
	}

	/**
	 * CallCCMN46D
	 *
	 * @param postDto
	 * @return oCcmn01uoDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostOutputDto CallCCMN46D(PostDto postDto) {

		PostOutputDto postOutputDto = new PostOutputDto();

		EventInputDto eventInputDto = mappContDTOtoCcmn46DTO(postDto);

		EventOutputDto eventOutputDto = objCcmn46dDao.ccmn46dAUDdam(eventInputDto);
		if (null != eventOutputDto) {
			if (postDto.getArchInputStructDto().getCreqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_ADD)) {
				postOutputDto.setIdEvent(eventOutputDto.getIdEvent());
				postDto.setIdEvent(eventOutputDto.getIdEvent());
			} else if (postDto.getArchInputStructDto().getCreqFuncCd()
					.equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
				postOutputDto.setIdEvent(postDto.getRowCcmn01UiG00().getIdEvent());
			}

		}

		return postOutputDto;
	}

	/**
	 * CallCCMN68D
	 *
	 * @param postDto
	 * @return oCcmn01uoDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostOutputDto CallCCMN68D(PostDto postDto) {
		log.debug("Entering method CallCCMN68D in PostEventServiceImpl");

		EventLinkInDto eventLinkInDto = new EventLinkInDto();
		if (postDto.getLiRowccmn01uig01().size() > 0) {
			if (!ObjectUtils.isEmpty(postDto.getLiRowccmn01uig01().get(0).getIdPerson())
					&& postDto.getLiRowccmn01uig01().get(0).getIdPerson() != 0) {
				eventLinkInDto = mappContDTOtoCcmn68DTO(postDto);
				objCcmn68dDao.updateEventPersonLink(eventLinkInDto);
			}
		}
		PostOutputDto oCcmn01uoDto = new PostOutputDto();
		if (!ObjectUtils.isEmpty(postDto.getRowCcmn01UiG00().getIdEvent())) {
			oCcmn01uoDto.setIdEvent(postDto.getRowCcmn01UiG00().getIdEvent());
		}
		log.debug("Exiting method CallCCMN68D in PostEventServiceImpl");
		return oCcmn01uoDto;
	}

	/**
	 * CallCCMN45D
	 *
	 * @param postDto
	 * @return pOutputMsg
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostOutputDto CallCCMN45D(PostDto postDto) {

		FetchEventDto fetchEventDto = new FetchEventDto();
		List<FetchEventResultDto> pCCMN45DOutputRec = new ArrayList<>();
		PostOutputDto postOutputDto = new PostOutputDto();

		if (0 != postDto.getIdEvent()) {
			if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(postDto.getReqFunctionCd())
					|| (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(postDto.getReqFunctionCd()))) {
				fetchEventDto.setIdEvent(postDto.getIdEvent());
			} else {
				fetchEventDto.setIdEvent(postDto.getIdEvent());
				log.debug("Entered into else");
			}

			pCCMN45DOutputRec = objCcmn45dDao.fetchEventDao(fetchEventDto);
		}
		if (!TypeConvUtil.isNullOrEmpty(pCCMN45DOutputRec) && !CollectionUtils.isEmpty(pCCMN45DOutputRec) 
				&& !TypeConvUtil.isNullOrEmpty(pCCMN45DOutputRec.get(0))
				&& !ObjectUtils.isEmpty(pCCMN45DOutputRec)) {
			FetchEventResultDto ccmn45doDto = pCCMN45DOutputRec.get(0);
			postOutputDto.setIdEvent(ccmn45doDto.getIdEvent());
			if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto.getDtLastUpdate())) {
				postOutputDto.setDtLastUpdate(ccmn45doDto.getDtLastUpdate().toString());
			}
		}

		postOutputDto.setRowCount(pCCMN45DOutputRec.size());

		return postOutputDto;
	}

	/**
	 * mappContDTOtoCcmn68DTO
	 *
	 * @param postDto
	 * @return pCCMN68DInputRec
	 */
	public EventLinkInDto mappContDTOtoCcmn68DTO(PostDto postDto) {

		EventLinkInDto eventLinkInDto = new EventLinkInDto();
		eventLinkInDto.setIdEvent(postDto.getIdEvent());
		eventLinkInDto.setIdPerson(postDto.getLiRowccmn01uig01().get(0).getIdPerson());
		eventLinkInDto.setCdScrDataAction(postDto.getLiRowccmn01uig01().get(0).getCdScrDataAction());
		if (null != postDto.getLiRowccmn01uig01().get(0).getCdScrDataAction())
			eventLinkInDto.setReqFunctionCd(postDto.getLiRowccmn01uig01().get(0).getCdScrDataAction());
		else
			eventLinkInDto.setReqFunctionCd(postDto.getReqFunctionCd());

		return eventLinkInDto;
	}

	/**
	 * mappContDTOtoCcmn68DTO
	 *
	 * @param postDto
	 * @return Ccmn46diDto
	 */
	public EventInputDto mappContDTOtoCcmn46DTO(PostDto postDto) {
		EventInputDto eventInputDto = new EventInputDto();

		eventInputDto.setEventLastUpdate(postDto.getRowCcmn01UiG00().getDtLastUpdate());
		eventInputDto.setCdEventStatus(postDto.getRowCcmn01UiG00().getCdEventStatus());
		eventInputDto.setCdTask(postDto.getRowCcmn01UiG00().getCdTask());
		eventInputDto.setCdEventType(postDto.getRowCcmn01UiG00().getCdEventType());
		eventInputDto.setDtDtEventOccurred(postDto.getRowCcmn01UiG00().getDtEventOccurred());
		if (!ObjectUtils.isEmpty(postDto.getRowCcmn01UiG00().getIdEvent())) {
			eventInputDto.setIdEvent(postDto.getRowCcmn01UiG00().getIdEvent());
		}
		eventInputDto.setIdPerson(postDto.getRowCcmn01UiG00().getIdPerson());
		eventInputDto.setIdStage(postDto.getRowCcmn01UiG00().getIdStage());
		eventInputDto.setTxtEventDescr(postDto.getRowCcmn01UiG00().getEventDescr());
		eventInputDto.setReqFunctionCd(postDto.getArchInputStructDto().getCreqFuncCd());
		eventInputDto.setDtDtEventCreated(postDto.getDtEventCreated());

		return eventInputDto;
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

	/**
	 * Method Name: postEvent Method Description:This method
	 * creates/updates/deletes Event (Event & EVENT_PERSON_LINK for Primary
	 * Child) using personId, stageId, eventType, task code and event
	 * description.
	 * 
	 * @param eventValueDto
	 * @param cReqFuncCd
	 * @param idStagePersonLinkPerson
	 * @return Long
	 */
	@Override
	public Long postEvent(EventValueDto eventValueDto, String cReqFuncCd, Long idStagePersonLinkPerson) {
		PostDto postDto = createEventTuxInput(eventValueDto, cReqFuncCd, idStagePersonLinkPerson);
		PostOutputDto postOutputDto = PostEvent(postDto);
		Long idEvent = postOutputDto.getIdEvent();
		return idEvent;
	}

	/**
	 * Method Name: createEventTuxInput Method Description:This method creates
	 * CCMN01UI (Input Object for creating Event) using personId, stageId,
	 * eventType, task code and event description.
	 * 
	 * @param eventValueDto
	 * @param cReqFuncCd
	 * @param idStagePersonLinkPerson
	 * @return PostDto
	 */
	private PostDto createEventTuxInput(EventValueDto eventValueDto, String cReqFuncCd, Long idStagePersonLinkPerson) {
		PostDto postDto = new PostDto();
		ServiceInputDto serviceInputDto = new ServiceInputDto();
		serviceInputDto.setCreqFuncCd(cReqFuncCd);
		postDto.setArchInputStructDto(serviceInputDto);
		SynchronizationServiceDto synchronizationServiceDto = new SynchronizationServiceDto();
		synchronizationServiceDto.setIdEvent(eventValueDto.getIdEvent());
		synchronizationServiceDto.setIdStage(eventValueDto.getIdStage());
		synchronizationServiceDto.setIdPerson(eventValueDto.getIdPerson());
		synchronizationServiceDto.setCdTask(eventValueDto.getCdEventTask());
		synchronizationServiceDto.setCdEventType(eventValueDto.getCdEventType());
		synchronizationServiceDto.setEventDescr(eventValueDto.getEventDescr());
		synchronizationServiceDto.setDtEventOccurred(new Date());
		synchronizationServiceDto.setCdEventStatus(eventValueDto.getCdEventStatus());
		synchronizationServiceDto.setDtLastUpdate(eventValueDto.getDtLastUpdate());
		// synchronizationServiceDto.setDtDtEventCreated(eventValueDto.getDtEventCreated());

		DomicileDeprivationChildToEventDto deprivationChildToEventDto = new DomicileDeprivationChildToEventDto();
		List<APSRoraRowDto> apsRoraRowDtoList = new ArrayList<>();
		APSRoraRowDto apsRoraRowDto = new APSRoraRowDto();
		if (idStagePersonLinkPerson != ServiceConstants.Zero_Value
				&& cReqFuncCd.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
			apsRoraRowDto.setIdPerson(idStagePersonLinkPerson);
			apsRoraRowDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
		}
		deprivationChildToEventDto.setaPSRoraList(apsRoraRowDtoList);
		synchronizationServiceDto.setDomicileDeprivationChildToEventDto(deprivationChildToEventDto);
		postDto.setRowCcmn01UiG00(synchronizationServiceDto);
		
		// Map the Array of LiRowccmn01uig01 - this will create Event person Link Entry 
		apsRoraRowDtoList.add(apsRoraRowDto);
		postDto.setLiRowccmn01uig01(apsRoraRowDtoList);
		
		// change made by Lubaba
		postDto.setDtEventCreated(eventValueDto.getDtEventCreated());
		return postDto;
	}
	
	
	/**
	 * Name: checkPostEventStatus Method Description: This common function is
	 * called to update the event table and the Event Person link table. Rows
	 * can be added, updated or deleted from the event table, while the Event
	 * Person Link table, you can only add and delete.
	 * 
	 * @param postEventIPDto
	 * @param ServiceReqHeaderDto
	 * @return postEventOPDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PostEventOPDto checkPostEventStatus(PostEventIPDto postEventIPDto, ServiceReqHeaderDto ServiceReqHeaderDto) {
		PostEventOPDto postEventOPDto = new PostEventOPDto();
		EventPersonLinkDto eventPersonLinkDto = new EventPersonLinkDto();
		Date date = new Date();
		String retEvnPerLink = "";
		EventDto eventDto = new EventDto();
		if (!ObjectUtils.isEmpty(postEventIPDto.getIdCase()) && postEventIPDto.getIdCase() > 0) {
			eventDto.setIdCase(postEventIPDto.getIdCase());
		}
		eventDto.setCdEventType(postEventIPDto.getCdEventType());
		eventDto.setIdStage(postEventIPDto.getIdStage());
		eventDto.setIdPerson(postEventIPDto.getIdPerson());
		eventDto.setDtEventCreated(new Date());
		eventDto.setDtEventOccurred(postEventIPDto.getDtEventOccurred());
		eventDto.setEventDescr(postEventIPDto.getEventDescr());
		eventDto.setCdEventStatus(postEventIPDto.getCdEventStatus());
		if (!ObjectUtils.isEmpty(postEventIPDto.getTsLastUpdate())) {
			eventDto.setDtLastUpdate(postEventIPDto.getTsLastUpdate());
		} else {
			eventDto.setDtLastUpdate(date);
		}
		eventDto.setCdTask(postEventIPDto.getCdTask());
		eventDto.setDtEventOccurred(postEventIPDto.getDtEventOccurred());
		eventDto.setPostEventDto(postEventIPDto.getPostEventDto());
		if (ServiceConstants.REQ_IND_AUD_UPDATE.equalsIgnoreCase(ServiceReqHeaderDto.getReqFuncCd())
				|| ServiceConstants.UPDATE.equalsIgnoreCase(ServiceReqHeaderDto.getReqFuncCd())
				|| ServiceConstants.REQ_IND_AUD_DELETE.equalsIgnoreCase(ServiceReqHeaderDto.getReqFuncCd())
				|| ServiceConstants.DELETE.equalsIgnoreCase(ServiceReqHeaderDto.getReqFuncCd())) {
			eventDto.setIdEvent(postEventIPDto.getIdEvent());
		}
		EventDto eventId = eventDao.eventAUDFunc(ServiceReqHeaderDto, eventDto);
		postEventOPDto.setIdEvent(eventId.getIdEvent());
		postEventIPDto.setIdEvent(eventId.getIdEvent());
		if (!ServiceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_DELETE)
				&& !ServiceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.DELETE)
				&& !TypeConvUtil.isNullOrEmpty(postEventIPDto.getIdEvent())
				&& !TypeConvUtil.isNullOrEmpty(postEventIPDto.getPostEventDto())) {
			for (int i = 0; i < postEventIPDto.getPostEventDto().size(); i++) {
				if (!ObjectUtils.isEmpty(postEventIPDto.getPostEventDto().get(i).getIdPerson())
						&& !(ServiceConstants.ZERO_VAL.equals(postEventIPDto.getPostEventDto().get(i).getIdPerson()))) {
					eventPersonLinkDto.setIdEvent(postEventIPDto.getIdEvent());
					eventPersonLinkDto
							.setIdEventPersonLink(postEventIPDto.getPostEventDto().get(i).getIdEventPersonLink());
					eventPersonLinkDto.setIdPerson(postEventIPDto.getPostEventDto().get(i).getIdPerson());
					eventPersonLinkDto.setCdScrDataAction(postEventIPDto.getPostEventDto().get(i).getCdScrDataAction());
					eventPersonLinkDto.setTsLastUpdate(date);
					eventPersonLinkDto.setIndHousehold(postEventIPDto.getPostEventDto().get(i).getIndHousehold());
					eventPersonLinkDto.setReqFuncCd(postEventIPDto.getPostEventDto().get(i).getCdScrDataAction());
					retEvnPerLink = eventPersonLinkDao.getEventPersonLinkAUD(eventPersonLinkDto);
					postEventOPDto.setRetrunMsg(retEvnPerLink);
				}
			}
			// Commenting the Below piece as it seems to be of no use for the
			// update/insert of Event.
			/*
			 * if
			 * (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(retEvnPerLink)
			 * )) { if (!ServiceReqHeaderDto.getcReqFuncCd().equalsIgnoreCase(
			 * ServiceConstants.REQ_IND_AUD_DELETE) &&
			 * !ServiceReqHeaderDto.getcReqFuncCd().equalsIgnoreCase(
			 * ServiceConstants.DELETE)) { Long eventID = 0l; if
			 * (ServiceReqHeaderDto.getcReqFuncCd().equalsIgnoreCase(
			 * ServiceConstants.REQ_IND_AUD_ADD) ||
			 * ServiceReqHeaderDto.getcReqFuncCd().equalsIgnoreCase(
			 * ServiceConstants.ADD)) { eventID = postEventOPDto.getIdEvent(); }
			 * else if (ServiceReqHeaderDto.getcReqFuncCd().equalsIgnoreCase(
			 * ServiceConstants.REQ_IND_AUD_UPDATE) ||
			 * ServiceReqHeaderDto.getcReqFuncCd().equalsIgnoreCase(
			 * ServiceConstants.UPDATE)) { eventID =
			 * postEventIPDto.getIdEvent(); } eventDto =
			 * eventDao.getEventByid(eventID); if
			 * (!ObjectUtils.isEmpty(eventDto) && (eventDto.getIdEvent() != 0))
			 * { postEventOPDto.setTsLastUpdate(eventDto.getDtLastUpdate()); } }
			 * }
			 */
		}
		return postEventOPDto;
	}

}
