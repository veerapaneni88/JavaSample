/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This interface interacts with controller and service impl
 *Feb 08, 2018- 10:59:00 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.placement.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.PostEventPersonDto;
import us.tx.state.dfps.service.admin.dao.EventProcessDao;
import us.tx.state.dfps.service.admin.dao.ServiceDeliveryRtrvDtlsDao;
import us.tx.state.dfps.service.admin.dao.TodoUpdDtTodoCompletedDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.EventInputDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.TodoUpdDtTodoCompletedInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.TaskDao;
import us.tx.state.dfps.service.common.request.OutputLaunchRtrvReq;
import us.tx.state.dfps.service.common.request.OutputLaunchSaveReq;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.OutputLaunchRtrvoRes;
import us.tx.state.dfps.service.common.response.PostEventRes;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.forms.dao.FormsDao;
import us.tx.state.dfps.service.placement.dto.OutputLaunchRtrvoDto;
import us.tx.state.dfps.service.placement.service.OutputLaunchRtrvService;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsInDto;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsOutDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TaskDto;

@Service
@Transactional
public class OutputLaunchRtrvServiceImpl implements OutputLaunchRtrvService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	StageDao stageDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	TaskDao taskDao;

	@Autowired
	ServiceDeliveryRtrvDtlsDao serviceDeliveryRtrvDtlsDao;

	@Autowired
	StageEventStatusCommonService stageEventStatusCommonService;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	ApprovalCommonService approvalService;

	@Autowired
	EventService eventService;

	@Autowired
	TodoUpdDtTodoCompletedDao todoUpdtCompleteDao;

	@Autowired
	EventProcessDao eventProcessDao;
	
	@Autowired
	FormsDao formsDao;
	
	private static final Logger log = Logger.getLogger(OutputLaunchRtrvServiceImpl.class);

	private static final String SUB_OL_EVNT_NARR_CTB = "CEVNTTBL";
	private static final String FCE_NARRATIVE = "FCE_NARRATIVE";
	private static final String PRIMARY_CHILD = "PC";

	/**
	 * This service will receive Id Event and invoke perform a full-row
	 * retrieval of the Event Table.Using CdEventType retrieved from the query,
	 * service will query one of the Document/Narrative tables to retrieve a
	 * time stamp.
	 * 
	 * @param outputLaunchRtrvReq
	 * @return OutputLaunchRtrvoRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public OutputLaunchRtrvoRes callOutputLaunchRtrvService(OutputLaunchRtrvReq outputLaunchRtrvReq) {
		log.debug("Entering method callOutputLaunchRtrvService in OutputLaunchRtrvServiceImpl");
		OutputLaunchRtrvoRes outputLaunchRtrvoRes = new OutputLaunchRtrvoRes();
		OutputLaunchRtrvoDto outputLaunchRtrvoDto = new OutputLaunchRtrvoDto();
		StageDto stageDto = new StageDto();
		EventDto eventDto = new EventDto();
		TaskDto taskDto = new TaskDto();
		// calling CINT40D service to Retrieve Stage Row
		stageDto = stageDao.getStageById(outputLaunchRtrvReq.getIdStage());
		if (!ObjectUtils.isEmpty(stageDto)) {
			outputLaunchRtrvoDto.setIdCase(stageDto.getIdCase());
			outputLaunchRtrvoDto.setIdStage(stageDto.getIdStage());
			outputLaunchRtrvoDto.setCdStage(stageDto.getCdStage());
		}
		if (outputLaunchRtrvReq.getIdEvent() > ServiceConstants.ZERO_VAL) {
			// ccmn45d service for event retrieve
			eventDto = eventDao.getEventByid(outputLaunchRtrvReq.getIdEvent());
			if (!ObjectUtils.isEmpty(eventDto)) {
				// setting the values retrieved from event table to
				// outputLaunchRtrvoDto
				outputLaunchRtrvoDto.setIdEvent(eventDto.getIdEvent());
				outputLaunchRtrvoDto.setIdStage(eventDto.getIdStage());
				outputLaunchRtrvoDto.setEventDescr(eventDto.getEventDescr());
				outputLaunchRtrvoDto.setCdEventStatus(eventDto.getCdEventStatus());
				outputLaunchRtrvoDto.setIdPerson(eventDto.getIdPerson());
				outputLaunchRtrvoDto.setDtEventOccurred(eventDto.getDtEventOccurred());
				outputLaunchRtrvoDto.setCdEventType(eventDto.getCdEventType());
				outputLaunchRtrvoDto.setDtEventLastUpdate(eventDto.getDtLastUpdate());
			}
			ServiceDeliveryRtrvDtlsInDto serviceDeliveryRtrvDtlsInDto = new ServiceDeliveryRtrvDtlsInDto();
			serviceDeliveryRtrvDtlsInDto.setIdEvent(outputLaunchRtrvReq.getIdEvent());
			// Get the narrative table name from Codes_Code table with EventType
			String tableName = serviceDeliveryRtrvDtlsDao.getDecodeTableName(eventDto.getCdEventType(),
					SUB_OL_EVNT_NARR_CTB);

			if (outputLaunchRtrvReq.getCdTask().equals(ServiceConstants.FCE_APPLICATION_TASK_CODE)) {
				tableName = FCE_NARRATIVE;
			}

			serviceDeliveryRtrvDtlsInDto.setSysTxtTablename(tableName);
			// call csys06d service to retrieve BLOB Narr to get lastupdate
			List<ServiceDeliveryRtrvDtlsOutDto> serviceDeliveryRtrvDtlsList = serviceDeliveryRtrvDtlsDao
					.getNarrExists(serviceDeliveryRtrvDtlsInDto);
			ServiceDeliveryRtrvDtlsOutDto serviceDeliveryRtrvDtlsOutDto = null;
			if (!ObjectUtils.isEmpty(serviceDeliveryRtrvDtlsList)) {
				serviceDeliveryRtrvDtlsOutDto = serviceDeliveryRtrvDtlsList.get(0);
				outputLaunchRtrvoDto.setDtLastUpdate(serviceDeliveryRtrvDtlsOutDto.getDtLastUpdate());
			}
		} else {
			outputLaunchRtrvoDto.setIdEvent(ServiceConstants.Zero_Value);
		}
		// call CCMN82D service to retrieve event type from Task table
		taskDto = taskDao.getTaskDetails(outputLaunchRtrvReq.getCdTask());
		if (!ObjectUtils.isEmpty(taskDto)) {
			outputLaunchRtrvoDto.setCdEventType(taskDto.getCdTaskEventType());
			outputLaunchRtrvoDto.setCdTask(taskDto.getCdTask());
		}
		outputLaunchRtrvoRes.setOutputLaunchRtrvoDto(outputLaunchRtrvoDto);
		log.debug("Exiting method callOutputLaunchRtrvService in OutputLaunchRtrvServiceImpl");
		return outputLaunchRtrvoRes;
	}

	/**
	 * This service will receive OutputLaunchSaveReq and update / save the data
	 * in event table. Approval table if event is in pending and todo table time
	 * stamp.
	 * 
	 * @param outPutLaunchSaveReqa
	 * @return OutputLaunchRtrvoRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public OutputLaunchRtrvoRes saveOutputLaunch(OutputLaunchSaveReq outPutLaunchSaveReq) {
		log.debug("Entering method saveOutputLaunch in OutputLaunchRtrvServiceImpl");
		StageTaskInDto stageTaskDto = new StageTaskInDto();
		PostEventReq postEventReq = new PostEventReq();
		OutputLaunchRtrvoRes outputLaunchRes = new OutputLaunchRtrvoRes();
		TodoUpdDtTodoCompletedInDto todoCompleteDto = new TodoUpdDtTodoCompletedInDto();
		stageTaskDto.setReqFuncCd(outPutLaunchSaveReq.getReqFuncCd());
		stageTaskDto.setIdStage(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdStage());
		stageTaskDto.setCdTask(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getCdTask());
		// Calling ccmn06u this service is to Check Stage/Event common function
		String stageEventStatus = stageEventStatusCommonService.checkStageEventStatus(stageTaskDto);
		// Event Status New was added as part of the if statement
		// in order to make sure that the primary child information
		// Stage must be subcare in order to call CINV51D
		if (StringUtils.isNotEmpty(stageEventStatus) && ServiceConstants.ARC_SUCCESS.equalsIgnoreCase(stageEventStatus)
				&& ((StringUtils.isNotEmpty(outPutLaunchSaveReq.getCdEventStatus())
						&& CodesConstant.CEVTSTAT_NEW.equalsIgnoreCase(outPutLaunchSaveReq.getCdEventStatus()))
						|| (ServiceConstants.Zero_Value == outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdEvent()))
				&& outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getCdStage()
						.equalsIgnoreCase(CodesConstant.CSTAGES_SUB)) {
			// Calling cinv51d this service is to get Personid
			// Set CINV51D IdStage and CdStagePersRole to "PC" for primary child
			// and
			// personId
			Long idPerson = stagePersonLinkDao
					.getPersonIdByRole(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdStage(), PRIMARY_CHILD);
			//outPutLaunchSaveReq.getOutputLaunchRtrvoDto().setIdPerson(idPerson);
			if(!ObjectUtils.isEmpty(idPerson)){
				List<PostEventPersonDto> postEventPersonList = new ArrayList<PostEventPersonDto>();
				PostEventPersonDto postEventPersonDto = new PostEventPersonDto();
				postEventPersonDto.setIdPerson(idPerson);
				postEventPersonList.add(postEventPersonDto);
				postEventReq.setPostEventPersonList(postEventPersonList);
			}			
		}	
		if (!outPutLaunchSaveReq.getSysNbrReserved1()
				&& !ObjectUtils.isEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdEvent())
				&& !outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdEvent().equals(ServiceConstants.Zero_Value)) {
			// This event status is the 'current' event status is pending
			// Fetch the saved event from db. if it is in pend state Invalidate
			// the pending approval.
			EventDto savedEvent = eventDao.getEventByid(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdEvent());
			if (CodesConstant.CEVTSTAT_PEND.equalsIgnoreCase(savedEvent.getCdEventStatus())) {
				ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
				approvalCommonInDto.setIdEvent(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdEvent());
				// Calling ccmn05u this service is to Invalidate approval common
				// function
				approvalService.callCcmn05uService(approvalCommonInDto);
			}

		}

		postEventReq.setUlIdStage(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdStage());
		// Unless cinv51d was called,Person should be null
		if(!ObjectUtils.isEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdPerson())){
			postEventReq.setUlIdPerson(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdPerson());
		}		
		// set id event
		postEventReq.setUlIdEvent(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdEvent());
		// set date of event occurred
		if (!StringUtils.isEmpty(outPutLaunchSaveReq.getReqFuncCd())
				&& ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(outPutLaunchSaveReq.getReqFuncCd())) {
			outPutLaunchSaveReq.getOutputLaunchRtrvoDto().setDtEventLastUpdate(new Date());
			outPutLaunchSaveReq.getOutputLaunchRtrvoDto().setDtEventOccurred(new Date());
			postEventReq.setDtDtEventOccurred(new Date());
		} else if (!ObjectUtils.isEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getDtEventOccurred())) {
			postEventReq.setDtDtEventOccurred(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getDtEventOccurred());
		}
		// set event Lastupdate
		if (!ObjectUtils.isEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getDtEventLastUpdate())) {
			postEventReq.setTsLastUpdate(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getDtEventLastUpdate());
		}
		// set taskcode
		postEventReq.setSzCdTask(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getCdTask());
		if (StringUtils.isNotEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getSpanishOrEnglish())) {
			String taskCode = taskDao.getTaskCode(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getSpanishOrEnglish(),
					outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getCdStage());
			switch (outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getSpanishOrEnglish()) {

			case CodesConstant.CEVNTTYP_VIS:
				postEventReq.setSzCdTask(taskCode);
				outPutLaunchSaveReq.getOutputLaunchRtrvoDto().setCdTask(taskCode);
				break;
			case CodesConstant.CEVNTTYP_VIP:
				postEventReq.setSzCdTask(taskCode);
				outPutLaunchSaveReq.getOutputLaunchRtrvoDto().setCdTask(taskCode);
				break;
			default:
				break;
			}
		}
		// set EventType
		if (!ObjectUtils.isEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getCdEventType())) {
			postEventReq.setSzCdEventType(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getCdEventType());
		}
		// set EventStatus
		if (!ObjectUtils.isEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getCdEventStatus())) {
			postEventReq.setSzCdEventStatus(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getCdEventStatus());
		}
		// set EventDescr
		if (!ObjectUtils.isEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getEventDescr())) {
			postEventReq.setSzTxtEventDescr(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getEventDescr());
		}
		if (!ObjectUtils.isEmpty(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdCase())) {
			postEventReq.setUlIdCase(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdCase());
		}
		// set Function action
		if (!StringUtils.isEmpty(outPutLaunchSaveReq.getReqFuncCd())) {
			postEventReq.setReqFuncCd(outPutLaunchSaveReq.getReqFuncCd());
		}
		PostEventRes postEventRes = eventService.postEvent(postEventReq);
		// Calling ccmn01u this service for Event common function
		outputLaunchRes.setOutputLaunchRtrvoDto(outPutLaunchSaveReq.getOutputLaunchRtrvoDto());
		outputLaunchRes.getOutputLaunchRtrvoDto().setIdEvent(postEventRes.getUlIdEvent());
		if (ServiceConstants.Zero_Value < outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdEvent()) {
			todoCompleteDto.setIdEvent(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdEvent());
			// Calling cinv43d service for To Do Completed Processing
			todoUpdtCompleteDao.updateTODOEvent(todoCompleteDto);
		}
		return outputLaunchRes;
	}

	/**
	 * Method Name:deleteDocument Method Description: Service Impl method to
	 * delete the 'PROC' Document and and the associated events when the Delete
	 * button clicked in Common Application Detail page.
	 * 
	 * @param outPutLaunchSaveReq
	 * @return CommonStringRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonStringRes deleteDocument(OutputLaunchSaveReq outPutLaunchSaveReq) {

		CommonStringRes commonStringRes = new CommonStringRes();
		EventInputDto eventInputDto = new EventInputDto();
		eventInputDto.setIdEvent(outPutLaunchSaveReq.getOutputLaunchRtrvoDto().getIdEvent());
		eventProcessDao.deleteEvent(eventInputDto);
		commonStringRes.setCommonRes(ServiceConstants.FORM_SUCCESS);

		return commonStringRes;
	}

	public void copyNarrativeDocForNewUsing(OutputLaunchRtrvoDto outputLaunchRtrvoDto) {
		if (ServiceConstants.WINDOW_MODE_NEW_USING
				.equalsIgnoreCase(outputLaunchRtrvoDto.getPageMode())) {
			String eventType = outputLaunchRtrvoDto.getCdEventType();
			if (StringUtils.isNotEmpty(outputLaunchRtrvoDto.getSpanishOrEnglish())) {
				switch (outputLaunchRtrvoDto.getSpanishOrEnglish()) {
				case CodesConstant.CEVNTTYP_VIS:
					eventType = CodesConstant.CEVNTTYP_VIS;
					break;
				case CodesConstant.CEVNTTYP_VIP:
					eventType = CodesConstant.CEVNTTYP_VIP;
					break;
				default:
					break;
				}
			}
			String tableName = serviceDeliveryRtrvDtlsDao.getDecodeTableName(eventType, SUB_OL_EVNT_NARR_CTB);
			formsDao.copyNarrativeDocForNewUsing(outputLaunchRtrvoDto.getIdEvent(),
					outputLaunchRtrvoDto.getIdNewEvent(), tableName);
		}
	}
}
