package us.tx.state.dfps.service.event.service;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvLetterDto;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.EventUtilRes;
import us.tx.state.dfps.service.common.response.ListObjectRes;
import us.tx.state.dfps.service.common.response.PostEventRes;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 5, 2017 - 1:21:18 PM
 */
public interface EventService {
	public PostEventRes postEvent(PostEventReq postEventReq);

	/**
	 * Returns a list of EventDto objects for the given EventIdS, the events are
	 * passed in this object because this method is used in ToDoConversation
	 * with data from ToDoDetailDB. Utility Service
	 * 
	 * @param idEvent
	 * @return @
	 */
	public EventDto getEvent(Long idEvent);
	
	/**
	 * Returns the most recent Event information for given case id and event description for Foster Care Candidacy
	 *
	 * @param caseId
	 * @param eventDesc
	 *            
	 * @return EventDto 
	 * 
	 */
	public EventDto getLatestEventForFCCByCaseIdAndEventDesc(Long caseId, String eventDesc);

	/**
	 * Returns the most recent event id, event status, task code and timestamp
	 * for the given stage and event type.<br>
	 * Utility Service
	 *
	 * @param ulIdStage
	 *            Stage id for the event
	 * @param szCdEventType
	 *            Type of event
	 * @return The most recent event id, event status, task code and timestamp
	 *         in the form of an Event class for the given stage and event type.
	 */
	public EventUtilRes getEventByStageAndEventType(Long ulIdStage, String szCdEventType);

	/**
	 * 
	 * Method Description:Returns a set of tasks associated with events not in
	 * COMP or APRV status for a particular stage. Utility Service
	 * 
	 * @param isStage
	 * @return List<String> @
	 */

	public List<EventDto> getPendingEventTasks(Long idStage);

	/**
	 * 
	 * Method Description: This method returns an boolean value based on whether
	 * or not a stage is in PEND status. Utility Service
	 * 
	 * @param idStage
	 *            This is Stage ID
	 * @param stageType
	 *            This is Stage Type
	 * @param StageProgType
	 *            This is Program Type
	 * @return Boolean Its tells if the stage is in PEND status or not.
	 */

	public CommonHelperRes isStageInPendStatus(Long idStage, String stageType, String StageProgType);

	/**
	 * Method Description: Looks at the case to determine if the given user has
	 * access to a given stage.<br>
	 * Use this version of the method if you want to test access for the current
	 * user.<br>
	 * The following items are checked: <br>
	 * <li>primary worker assigned to stage</li>
	 * <li>one of the four secondary workers assigned to the stage</li>
	 * <li>the supervisor of any of the above</li>
	 * <li>the designee of any of the above supervisors</li>
	 * 
	 * @param idStage
	 *            This is Stage ID
	 * @param idPerson
	 *            This is Stage Type
	 * @return Boolean whether or not the user has access
	 */
	public CommonHelperRes hasStageAccess(Long idStage, Long idPerson);

	public CommonHelperRes hasApproverTask(Long idStage, Long idPerson);

	/**
	 * 
	 * Method Description:Returns a list of open stages for a given case; they
	 * are sorted by stage code, then stage name.
	 * 
	 * @param idCase
	 * @return @
	 */
	public List<SelectStageDto> getOpenStages(Long idCase, String caseStatus);

	/**
	 * Returns the most recent event id and event status for the given stage and
	 * task code<br>
	 * Usage Example: see getEvent( int, String )
	 * 
	 * @param CommonHelperReq
	 * @return ListObjectRes @
	 */
	public ListObjectRes getEventByStageIDAndTaskCode(CommonHelperReq commonHelperReq);

	/*
	 * Method-Description:Returns the status of the given event.
	 * 
	 * @param IdEvent The ID_EVENT for the event of interest
	 * 
	 * @return The status of the event
	 * 
	 * @
	 */
	public CommonHelperRes getSzCdEventStatus(CommonHelperReq commonHelperReq);

	/*
	 * Method-Description:Returns true if the stage has a existing extension
	 * request.
	 * 
	 * @param stageID
	 * 
	 * @return Boolean true/False depending on id Stage has existing extension
	 * request.
	 * 
	 * @throws Service Exception
	 * 
	 */
	public CommonHelperRes hasExtnReq(CommonHelperReq commonHelperReq);

	/*
	 * Method-Description:Returns true if the stage has a child fatality
	 * present.
	 * 
	 * @param stageID
	 * 
	 * @return Boolean true/False depending on id Stage has child fatality
	 * present.
	 * 
	 * @throws Service Exception
	 * 
	 */
	public CommonHelperRes isChildFatalityPresent(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:Check to see if the given event has an SSCC placement.
	 * 
	 * @param eventID
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	public CommonHelperRes getIdResourceSSCC(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:Returns the Living Arrangement from an event id
	 * 
	 * @param eventID
	 * @return String (Living arrangement)
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	public CommonHelperRes getLivingArrangement(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:eturns the active child referral for idStage
	 * 
	 * @param stageID
	 * @return Long -- the child active referral
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	public CommonHelperRes getChildActiveReferral(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:Check and retrieve if there is an intake for the given
	 * case in the contact
	 * 
	 * @param caseID
	 * @return List of all StageIds and PriorStageIds.
	 * @throws Service
	 *             Exception
	 * 
	 **/
	public CommonHelperRes queryIntakeStage(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:Check if there is a contact for the given case id and
	 * stage id and Contact Type
	 * 
	 * @param caseID,
	 *            StageID, Contact Type
	 * @return Boolean True or false
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	public CommonHelperRes isContactInStage(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:This method returns an boolean value based on whether
	 * or not any of the ADO stage is currently in PROC status.
	 * 
	 * @param stageId
	 *            --stageID
	 * @return Boolean -- true or false
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	public CommonHelperRes getAdoProcessStatus(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description: Returns true if the stage has a pending extension
	 * request
	 * 
	 * @param stageId
	 *            --stageID , idCurrentContactEvent (event id)
	 * @return Boolean -- true or false
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	public CommonHelperRes hasPendingExtnReq(CommonHelperReq commonHelperReq);

	public CommonHelperRes updateEventStatus(CommonEventIdReq eventIdList);

	/**
	 * Method Name: updateEvent Method Description:
	 * 
	 * @param idEvent
	 * @return
	 */
	public CommonHelperRes updateEvent(Long idEvent, String taskCode);

	/**
	 * Returns the most recent CPS event id and event status for the given stage
	 * and task code<br>
	 * Usage Example: see getEvent( int, String )
	 * 
	 * @param CommonHelperReq
	 * @return ListObjectRes @
	 */
	public ListObjectRes getCpsEventByStageIdAndTaskCode(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: getPendingStageClosureEvent Method Description:Check to see
	 * if the given stage has a stage closure event that is in PEND status.
	 * 
	 * @param idStage
	 * @return
	 */
	public Long getPendingStageClosureEvent(Long idStage);

	public void updateEventStatus(Long idEvent, String eventStatus);

	public void updateEventStatus(Long idEvent, String eventStatus, String eventDescription);

	/**
	 * 
	 * @param idEvent
	 * @return
	 */

	/**
	 * Method Name: getChildPlanServicePlanList. Method Description: This method
	 * will fetch the all child service plan created for a child by passing
	 * idStage and cdTask values
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	public List<EventDto> getChildServicePlanList(String cdTask, Long idStage);

	/**
	 * Method Name: hasLegalStatus Method Description: Checks wheather the legal
	 * status is present in the DB or not.
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean hasLegalStatus(Long idStage);
	

	/**
	 * @param idEvent
	 * @return
	 */
	public Boolean getCdTasksByEventid(Long idEvent);

	public CciInvLetterDto getLetterDetails(Long idEvent);

	String getRoraEventStatus(Long stageId);

	/**
	 * @param idEvent
	 * @return
	 */
	public Boolean isSafetyAssessmentCreatedContact(Long idEvent);

	public List<EventDto> getEventsByStageIDAndTaskCodes(CommonHelperReq commonHelperReq);

}
