package us.tx.state.dfps.service.notifications.serviceimpl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.service.admin.dao.ContactInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.EventPersonLinkInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.LeNotificationTodoDao;
import us.tx.state.dfps.service.admin.dao.TodoInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.TodoUpdDtTodoCompletedDao;
import us.tx.state.dfps.service.admin.dto.ContactInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.LeNotificationTodoDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusOutDto;
import us.tx.state.dfps.service.admin.dto.TodoInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.TodoUpdDtTodoCompletedInDto;
import us.tx.state.dfps.service.admin.service.PostEventStageStatusService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.NotificationsReq;
import us.tx.state.dfps.service.common.response.NotificationsRes;
import us.tx.state.dfps.service.notifications.service.NotificationsService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * will update the contact table and return a contact if one occurred before.
 * Aug 10, 2017- 10:05:45 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
@Transactional
public class NotificationsServiceImpl implements NotificationsService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ContactInsUpdDelDao contactInsUpdDelDao;

	@Autowired
	LeNotificationTodoDao leNotificationTodoDao;

	@Autowired
	TodoInsUpdDelDao todoInsUpdDelDao;

	@Autowired
	TodoUpdDtTodoCompletedDao todoUpdDtTodoCompletedDao;

	@Autowired
	PostEventStageStatusService postEventStageStatusService;

	@Autowired
	EventPersonLinkInsUpdDelDao eventPersonLinkInsUpdDelDao;

	private static final Logger log = Logger.getLogger(NotificationsServiceImpl.class);

	/**
	 * 
	 * Method Name: callNotificationsService Method Description:This service
	 * will update the contact table and return a contact if one occurred
	 * before.
	 * 
	 * @param notficationReq
	 * @return NotificationsOutDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public NotificationsRes callNotificationsService(NotificationsReq notficationReq) {
		log.debug("Entering method callNotificationsService in NotificationsServiceImpl");
		NotificationsRes notificationRes = new NotificationsRes();
		PostEventStageStatusOutDto postEventStageStatusOutDto = new PostEventStageStatusOutDto();
		notficationReq.setDtEventOccurred(new Date());
		postEventStageStatusOutDto = callPostEvent(notficationReq);
		/*
		 * if (!ObjectUtils.isEmpty(notficationReq.getIdEvent())) {
		 * callUpdateEventTable(notficationReq); }
		 */
		notficationReq.setIdEvent(postEventStageStatusOutDto.getIdEvent());
		notficationReq.setDtEventLastUpdate(new Date());
		callUpdateContactTable(notficationReq);
		if (!ObjectUtils.isEmpty(notficationReq.getIdTodo())) {
			if (ServiceConstants.TODO_LE_NOTIF_TASK.equalsIgnoreCase(notficationReq.getCdTask())
					|| !ObjectUtils.isEmpty(notficationReq.getCdTask())) {
				updateofToDo(notficationReq);
			}
		}
		log.debug("Exiting method callCint33sService in NotificationsServiceImpl");

		notificationRes.setIdEvent(postEventStageStatusOutDto.getIdEvent());
		return notificationRes;
	}

	/**
	 * 
	 * Method Name: callUpdateContactTable Method Description:This method is
	 * used to update the contact table.
	 * 
	 * @param notificationReq
	 * @
	 */
	private void callUpdateContactTable(NotificationsReq notificationReq) {
		log.debug("Entering method callUpdateContactTable in NotificationsServiceImpl");
		ContactInsUpdDelInDto contactInsUpdDelInDto = new ContactInsUpdDelInDto();
		contactInsUpdDelInDto.setIdEvent(notificationReq.getIdEvent());
		contactInsUpdDelInDto.setIdStage(notificationReq.getIdStage());
		contactInsUpdDelInDto.setIdPerson(notificationReq.getIdPerson());
		contactInsUpdDelInDto.setDtDTContactOccurred(new Date());
		contactInsUpdDelInDto.setIndContactAttempted(ServiceConstants.INDICATOR_NO);
		contactInsUpdDelInDto.setCdContactLocation(notificationReq.getCdContactLocation());
		contactInsUpdDelInDto.setCdContactMethod(notificationReq.getCdContactMethod());
		contactInsUpdDelInDto.setCdContactOthers(notificationReq.getCdContactOthers());
		contactInsUpdDelInDto.setCdContactPurpose(notificationReq.getCdContactPurpose());
		contactInsUpdDelInDto.setCdContactType(notificationReq.getCdContactType());
		contactInsUpdDelInDto.setTxtClosureDesc(notificationReq.getTxtClosureDesc());
		contactInsUpdDelInDto.setTmScrTmCntct(new Date());
		contactInsUpdDelInDto.setSysReqFuncCd(notificationReq.getReqFuncCd());
		contactInsUpdDelInDto.setDtMonthlySummBegin(new Date());
		contactInsUpdDelInDto.setDtMonthlySummEnd(new Date());
		contactInsUpdDelInDto.setIdPersonUpdate(notificationReq.getIdPerson());
		contactInsUpdDelDao.updateContactAndContactNarrative(contactInsUpdDelInDto);
		log.debug("Exiting method callUpdateContactTable in NotificationsServiceImpl");
	}

	/**
	 * 
	 * Method Name: updateofToDo Method Description:This method will Update the
	 * to-do to a status of complete.
	 * 
	 * @param notificationReq
	 * @
	 */
	private void updateofToDo(NotificationsReq notificationReq) {
		log.debug("Entering method UpdateofToDo in NotificationsServiceImpl");
		Date dtDTSystemDate = new Date();
		LeNotificationTodoDto leNotificationTodoDto = new LeNotificationTodoDto();
		TodoInsUpdDelInDto todoInsUpdDelInDto = new TodoInsUpdDelInDto();
		TodoUpdDtTodoCompletedInDto todoUpdDtTodoCompletedInDto = new TodoUpdDtTodoCompletedInDto();
		if ((!ObjectUtils.isEmpty(notificationReq.getIdStage()))
				&& (StringUtils.isEmpty(notificationReq.getCdTask()))) {
			leNotificationTodoDto.setIdStage(notificationReq.getIdStage());
			leNotificationTodoDto.setCdTodoTask(notificationReq.getCdTask());
			List<LeNotificationTodoDto> notifications = leNotificationTodoDao
					.getTodoByIdStageAndTask(leNotificationTodoDto);
			if (notifications != null) {
				for (LeNotificationTodoDto notificationsList : notifications) {
					todoInsUpdDelInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
					todoInsUpdDelInDto.setIdTodo(notificationsList.getIdTodo());
					todoInsUpdDelInDto.setIdCase(notificationsList.getIdCase());
					todoInsUpdDelInDto.setIdEvent(notificationReq.getIdEvent());
					todoInsUpdDelInDto.setIdStage(notificationReq.getIdStage());
					todoInsUpdDelInDto.setIdTodoPersAssigned(notificationsList.getIdTodoPersAssigned());
					todoInsUpdDelInDto.setIdTodoPersCreator(notificationsList.getIdTodoPersCreator());
					todoInsUpdDelInDto.setIdTodoPersWorker(notificationsList.getIdTodoPersWorker());
					todoInsUpdDelInDto.setDtTodoCreated(notificationsList.getDtTodoCreated());
					todoInsUpdDelInDto.setDtTodoDue(notificationsList.getDtTodoDue());
					todoInsUpdDelInDto.setDtTaskDue(notificationsList.getDtTaskDue());
					todoInsUpdDelInDto.setCdTodoTask(ServiceConstants.TODO_LE_NOTIF_TASK);
					todoInsUpdDelInDto.setCdTodoType(notificationsList.getCdTodoType());
					todoInsUpdDelInDto.setTodoDesc(notificationsList.getTodoDesc());
					todoInsUpdDelInDto.setTodoLongDesc(notificationsList.getTodoLongDesc());
					todoInsUpdDelInDto.setDtTodoCompleted(dtDTSystemDate);
					todoInsUpdDelDao.cudTODO(todoInsUpdDelInDto);
				}
			}
		} else if (!ObjectUtils.isEmpty(notificationReq.getIdEvent())) {
			todoUpdDtTodoCompletedInDto.setIdEvent(notificationReq.getIdEvent());
			todoUpdDtTodoCompletedDao.updateTODOEvent(todoUpdDtTodoCompletedInDto);
		}
		log.debug("Exiting method UpdateofToDo in NotificationsServiceImpl");
	}

	/**
	 * 
	 * Method Name: callPostEvent Method Description:This method is used to
	 * invoke service to update event
	 * 
	 * @param notificationReq
	 * @
	 */
	private PostEventStageStatusOutDto callPostEvent(NotificationsReq notificationReq) {
		log.debug("Entering method callPostEvent in NotificationsServiceImpl");
		PostEventStageStatusInDto postEventStageStatusInDto = new PostEventStageStatusInDto();
		PostEventStageStatusOutDto postEventStageStatusOutDto = new PostEventStageStatusOutDto();
		/*
		 * if (ObjectUtils.isEmpty(notificationReq.getIdEvent())) { return; }
		 */
		BeanUtils.copyProperties(notificationReq, postEventStageStatusInDto);
		postEventStageStatusInDto.setReqFuncCd(notificationReq.getReqFuncCd());
		postEventStageStatusInDto.setIdEventPerson(notificationReq.getIdEventPerson());
		postEventStageStatusInDto.setCdNotice(notificationReq.getCdNotice());
		postEventStageStatusInDto.setDistMethod(notificationReq.getDistMethod());
		postEventStageStatusOutDto = postEventStageStatusService
				.callPostEventStageStatusService(postEventStageStatusInDto);
		log.debug("Exiting method callPostEvent in NotificationsServiceImpl");
		return postEventStageStatusOutDto;
	}

	/**
	 * 
	 * Method Name: callUpdateEventTable Method Description:This method is used
	 * to invoke service to update event table.
	 * 
	 * @param notificationReq
	 * @
	 */
	/*
	 * private void callUpdateEventTable(NotificationsReq notificationReq) {
	 * log.
	 * debug("Entering method callUpdateEventTable in NotificationsServiceImpl"
	 * ); EventPersonLinkInsUpdDelInDto eventPersonLinkInsUpdDelInDto = new
	 * EventPersonLinkInsUpdDelInDto();
	 * eventPersonLinkInsUpdDelInDto.setCdReqFunction(notificationReq.
	 * getcReqFuncCd());
	 * eventPersonLinkInsUpdDelInDto.setIdEvent(notificationReq.getIdEvent());
	 * eventPersonLinkInsUpdDelInDto.setIdPerson(notificationReq.getIdPerson());
	 * eventPersonLinkInsUpdDelDao.updateEventPersonLink(
	 * eventPersonLinkInsUpdDelInDto); log.
	 * debug("Exiting method callUpdateEventTable in NotificationsServiceImpl");
	 * }
	 */
}
