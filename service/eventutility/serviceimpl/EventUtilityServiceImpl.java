package us.tx.state.dfps.service.eventutility.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.admin.dao.FetchEventDao;
import us.tx.state.dfps.service.admin.dto.ApprovalTaskDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDto;
import us.tx.state.dfps.service.admin.dto.FetchEventResultDto;
import us.tx.state.dfps.service.admin.service.ApprovalTaskService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.eventutility.dao.EventUtilityDao;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.xmlstructs.inputstructs.EventIdArrayDto;
import us.tx.state.dfps.xmlstructs.inputstructs.EventIdStructDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Used to
 * update the status of events. Sep 6, 2017- 7:09:31 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Service
@Transactional
public class EventUtilityServiceImpl implements EventUtilityService {

	@Autowired
	EventUtilityDao eventUtilityDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	FetchEventDao fetchEventDao;

	@Autowired
	ApprovalTaskService approvalTaskService;

	@Autowired
	CaseUtils caseUtils;

	/**
	 * MethoDName : updateEventStatus Method Description: This method is used to
	 * update the cdEventStatus in Event table/Entity provided input as List
	 * events
	 * 
	 * @param events
	 * @return long
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long updateEventStatus(List<Event> events) {

		return eventUtilityDao.updateEventStatus(events);
	}

	/**
	 * Method Description: This method is used to update the cdEventStatus in
	 * Event table/Entity provided input as List events, String status
	 * 
	 * @param events
	 * @param status
	 * @return long @
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long updateEventStatus(List<Event> events, String status) {

		return eventUtilityDao.updateEventStatus(events, status);
	}

	/**
	 * Method Description: This method is used to update the cdEventStatus in
	 * Event table/Entity provided input as int idEvent & String cdEventstatus
	 * 
	 * @param idEvent
	 * @param cdEventStatus
	 * @return long @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long updateEventStatus(Long idEvent, String cdEventStatus) {

		return eventUtilityDao.updateEventStatus(idEvent, cdEventStatus);
	}

	/**
	 * Method Description: This method is used to update the status to COMP
	 * provided input as stageClosureEventId
	 * 
	 * @param stageClosureEventId
	 * @return long @
	 */

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long invalidatePendingStageClosure(Long stageClosureEventId) {

		EventIdArrayDto eventIdArrayDto = new EventIdArrayDto();
		EventIdStructDto eventIdStructDto = new EventIdStructDto();
		eventIdStructDto.setIdEvent(stageClosureEventId);

		addEventIdStruct(eventIdStructDto, eventIdArrayDto);

		ServiceInputDto serviceInputDto = new ServiceInputDto();

		ApprovalTaskDto approvalTaskDto = new ApprovalTaskDto();

		serviceInputDto.setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

		serviceInputDto.setUlSysNbrReserved1("N");

		approvalTaskDto.setUlIdEvent(stageClosureEventId);

		approvalTaskDto.setArchInputStructDto(serviceInputDto);

		approvalTaskService.callCcmn05uService(approvalTaskDto);

		return eventUtilityDao.updateEventStatus(caseUtils.getEvents(eventIdArrayDto), ServiceConstants.CEVTSTAT_COMP);
	}

	/**
	 * Method Name: addEventIdStruct Method Description:
	 * 
	 * @param eventIdStructDto
	 * @param arrayList
	 */
	@SuppressWarnings("unchecked")
	private void addEventIdStruct(EventIdStructDto eventIdStructDto, EventIdArrayDto eventIdArrayDto) {
		if (eventIdArrayDto.getEventIdStructList().size() >= 1500) {
			throw new IndexOutOfBoundsException();
		}
		eventIdArrayDto.getEventIdStructList().add(eventIdStructDto);
	}

	/**
	 * MethodName:fetchEventInfo MethodDescription:Fetches the event info for
	 * the IdEvent.
	 * 
	 * @param idEvent
	 * @return EventValueDto
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public EventValueDto fetchEventInfo(long idEvent) {
		EventValueDto eventValueDto = new EventValueDto();
		FetchEventDto fetchEventDto = new FetchEventDto();
		fetchEventDto.setIdEvent(idEvent);
		List<FetchEventResultDto> fetchEventResultDtos = fetchEventDao.fetchEventDao(fetchEventDto);
		FetchEventResultDto fetchEventResultDto = fetchEventResultDtos.get(0);
		eventValueDto.setCdEventType(fetchEventResultDto.getCdEventType());
		eventValueDto.setEventDescr(fetchEventResultDto.getTxtEventDescr());
		eventValueDto.setCdEventTask(fetchEventResultDto.getCdTask());
		eventValueDto.setCdEventStatus(fetchEventResultDto.getCdEventStatus());
		eventValueDto.setIdEvent(fetchEventResultDto.getIdEvent());
		eventValueDto.setIdStage(fetchEventResultDto.getIdStage());
		eventValueDto.setIdPerson(fetchEventResultDto.getIdPerson());
		eventValueDto.setDtEventOccurred(fetchEventResultDto.getDtDtEventOccurred());
		eventValueDto.setDtLastUpdate(fetchEventResultDto.getDtLastUpdate());
		return eventValueDto;
	}

	/**
	 * Checks if the event exists or not
	 * 
	 * @param eventBean
	 * @return boolean @
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public boolean eventExists(EventValueDto eventBean) {

		return eventUtilityDao.eventExists(eventBean);
	}
}
