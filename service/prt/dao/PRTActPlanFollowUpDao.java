package us.tx.state.dfps.service.prt.dao;

import java.util.List;

import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.common.request.PRTActplanFollowUpReq;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.subcare.dto.PRTActPlanFollowUpDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the Interface which provides the declaration of the methods which will be
 * used to retrieve/save PRT Follow-Up. Mar 28, 2018- 3:33:06 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PRTActPlanFollowUpDao {

	/**
	 * This method updates PRT idContactEvent of the PRTActionPlanFollowup to
	 * 
	 * PRT_PERSON_LINK table using idPrtActionPlanFollowup.
	 * 
	 * @param prtPersonLink
	 * 
	 * @
	 */

	public Long updatePRTFollowupContactToPersLink(PRTPersonLinkDto prtPersonLink);

	/**
	 * Method Name: getPRTConnInCompActPlnFollowup Method Description: This
	 * method retrieves connections from COMP action plan followup.
	 * 
	 * @param prtActplanFollowUpReq
	 *            - The dto will hold the input paramters such as
	 *            idPerson,idEvent,idFollowUp.
	 * @return Long - The value of number of connections for each child in PRT
	 *         Follow-Up in COMP status.
	 */
	public Long getPRTConnInCompActPlnFollowup(PRTActplanFollowUpReq prtActplanFollowUpReq);

	/**
	 * Method Name: getPRTConnInCompActPln Method Description:This method
	 * retrieves connections from existing COMP action plan.
	 * 
	 * @param prtActplanFollowUpReq
	 *            - The dto will hold the input paramters such as
	 *            idPerson,idEvent,idFollowUp.
	 * @return Long - The value of number of connections for each child in PRT
	 *         Action Plan in COMP status.
	 */
	public Long getPRTConnInCompActPln(PRTActplanFollowUpReq prtActplanFollowUpReq);

	/**
	 * Method Name: selectLatestFollowUp Method Description:This method gets
	 * latest FollowUp for the given Action Plan.
	 * 
	 * @param idFollowUp
	 *            - The id of the action plan.
	 * @param eventStatus
	 *            - The event status.
	 * @return Long - The id of the follow-up.
	 */
	public Long selectLatestFollowUp(Long idFollowUp, String eventStatus);

	/**
	 * Method Name: fetchActionPlanInCOMP Method Description:This method gets
	 * Action Plan in COMP status for the given Stage.
	 * 
	 * @param idStage
	 *            - The current id stage.
	 * @return - The id of the latest PRT action plan .
	 */
	public Long fetchActionPlanInCOMP(Long idStage);

	/**
	 * Method Name: fetchOpenActionPlan Method Description:This method gets Open
	 * Action Plan Id for the given Person.
	 * 
	 * @param idPerson
	 *            - The id person of child who is in a open Action Plan who was
	 *            not part of the closing PRT.
	 * @return - The id of the latest PRT Action Plan.
	 */
	public Long fetchOpenActionPlan(Long idPerson);

	/**
	 * Method Name: selectLatestFollowUpForStage Method Description:This method
	 * gets latest FollowUp for the given Action Plan and stage.
	 * 
	 * @param idActionPlan
	 *            - The id of the PRT Action Plan.
	 * @param idStage
	 *            - The id stage.
	 * @param eventStatus
	 *            - The event status.
	 * @return Long - The id of the PRT Follow-Up.
	 */
	public Long selectLatestFollowUpForStage(Long idActionPlan, Long idStage, String eventStatus);

	/**
	 * Method Name: selectActionPlanFollowUp Method Description:This method
	 * fetches single row from PRT_ACTPLN_FOLLOWUP table.
	 * 
	 * @param idPrtActplnFollowup
	 *            - The id of the PRT Follow-Up.
	 * @return PRTActPlanFollowUpDto - The dto holds the PRT Follow-Up details.
	 */
	public PRTActPlanFollowUpDto selectActionPlanFollowUp(Long idPrtActplnFollowup);

	/**
	 * Method Name: selectPrtChildren Method Description:This method fetches
	 * PRT_PERSON_LINK rows for the given Action Plan.
	 * 
	 * @param idPrtActplnFollowup
	 *            - The id of the PRT Action Plan or the PRT Follow-Up.
	 * @return List - The list of children .
	 */
	public List<PRTPersonLinkDto> selectPrtChildren(Long idPrtActplnFollowup);

	/**
	 * Method Name: selectPRTConnections Method Description:This method
	 * overrides base class function. In addition to selecting connections, this
	 * function also sets selected by user flag, for the selected connections.
	 * 
	 * @param idPrtPersonLink
	 *            - The id Person of the child in the PRT Follow-Up.
	 * @param idStage
	 *            - The id stage.
	 * @return List - The list of connections for a particular child.
	 */
	public List<PRTConnectionDto> selectPRTConnections(Long idPrtPersonLink, Long idStage);

	/**
	 * Method Name: getPRTParentTaskInfo Method Description:This method get
	 * parent task information for a given task.
	 * 
	 * @param parentIds
	 *            - The id tasks.
	 * @return List - PRT Task details.
	 */
	@SuppressWarnings("rawtypes")
	public List getPRTParentTaskInfo(List<Long> parentIds);

	/**
	 * Method Name: insertActPlanFollowUp Method Description:This method inserts
	 * record into PRT_ACTPLN_FOLLOWUP table.
	 * 
	 * @param followUp
	 *            - The dto with the input values to be saved for the PRT
	 *            Follow-Up .
	 * @return Long - The identifier generated after saving/updating the PRT
	 *         Follow-Up.
	 */
	public Long insertActPlanFollowUp(PRTActPlanFollowUpDto followUp);

	/**
	 * Method Name: updateActPlanFollowUp Method Description:This method updates
	 * PRT_ACTPLN_FOLLOWUP table using PRTActPlanFollowUpValueBean.
	 * 
	 * @param followUp
	 *            -The dto with the input values to be updated for the PRT
	 *            Follow-Up
	 */
	public void updateActPlanFollowUp(PRTActPlanFollowUpDto followUp);

	/**
	 * Method Name: selectFollowUpUsingEventId Method Description:This method
	 * fetches single row from PRT_ACTPLN_FOLLOWUP table using Event Id.
	 * 
	 * @param idPrtActionPlanFollowUpEvent
	 *            - The id event of the PRT Follow-Up.
	 * @return PRTActPlanFollowUpDto - The dto with the PRT Follow-Up details.
	 */
	public PRTActPlanFollowUpDto selectFollowUpUsingEventId(Long idPrtActionPlanFollowUpEvent);

	/**
	 * Method Name: populateEventIdForChild Method Description:This method
	 * populates Event Id associated with the Child.
	 * 
	 * @param children
	 *            - The list of children in the PRT Action Plan.
	 * @param idActPlanOrFollowUp
	 *            - The id of Action Plan or Follow-Up.
	 * @param planType
	 *            - The plan either Action Plan or Follow-Up.
	 */
	public void populateEventIdForChild(List<PRTPersonLinkDto> children, Long idActPlanOrFollowUp,
			ActionPlanType planType);

	/**
	 * Method Name: selectLatestFollowUpWithPermStatus Method Description:This
	 * method gets Latest FollowUp for the Action Plan which has Perm Status
	 * populated.
	 * 
	 * @param idActionPlan
	 * @return
	 */
	public Long selectLatestFollowUpWithPermStatus(Long idActionPlan);

	/**
	 * Method Name: getActPlanEventId Method Description:This method retrieves
	 * the event id of the Follow-Up using the id Action Plan.
	 * 
	 * @param idActionPlan
	 *            - The id of action plan.
	 * @return Long - The id of the follow-up.
	 */
	Long getActPlanEventId(Long idActionPlan);

}
