/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 28, 2018- 3:32:38 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.prt.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.PrtActionPlan;
import us.tx.state.dfps.common.domain.PrtParticipant;
import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTEventLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTParticipantDto;
import us.tx.state.dfps.service.prt.dto.PRTPermStatusLookupDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.subcare.dto.PRTActionPlanDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 28, 2018- 3:32:38 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PRTActionPlanDao {

	/**
	 * Method Name: selectActionPlanUsingEventId Method Description: This method
	 * fetches single row from PRT_ACTION_PLAN table using Event Id.
	 *
	 * @param idActionPlanEvent
	 *            the id action plan event
	 * @return PRTActionPlanDto
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public PRTActionPlanDto selectActionPlanUsingEventId(Long idActionPlanEvent);

	/**
	 * Delete participants for act plan.
	 *
	 * @param actionPlanId
	 *            the action plan id @ the service exception
	 */
	public void deleteParticipantsForActPlan(Long actionPlanId);

	/**
	 * Delete prt goals for child.
	 *
	 * @param prtPersonLinkId
	 *            the prt person link id @ the service exception
	 */
	public void deletePrtGoalsForChild(Long prtPersonLinkId);

	/**
	 * Delete prt connections.
	 *
	 * @param prtPersonLinkId
	 *            the prt person link id @ the service exception
	 */
	public void deletePrtConnections(Long prtPersonLinkId);

	/**
	 * Delete prt strategy.
	 *
	 * @param prtStrategyId
	 *            the prt strategy id @ the service exception
	 */
	public void deletePrtStrategy(Long prtStrategyId);

	/**
	 * Delete person link for act plan.
	 *
	 * @param actionPlanId
	 *            the action plan id @ the service exception
	 */
	public void deletePersonLinkForActPlan(long actionPlanId);

	/**
	 * Delete prt event link.
	 *
	 * @param actionPlanId
	 *            the action plan id @ the service exception
	 */
	public void deletePrtEventLink(Long actionPlanId);

	/**
	 * Delete prt event.
	 *
	 * @param eventId
	 *            the event id @ the service exception
	 */
	public void deletePrtEvent(Long eventId);

	/**
	 * Delete action plan.
	 *
	 * @param actionPlanId
	 *            the action plan id @ the service exception
	 */
	public Long deleteActionPlan(Long actionPlanId);

	/**
	 * Insert action plan.
	 *
	 * @param actionPlan
	 *            the action plan
	 * @return the prt action plan @ the service exception
	 */
	public long insertActionPlan(PRTActionPlanDto actionPlan);

	/**
	 * Update PRT participants.
	 *
	 * @param participant
	 *            the participant
	 * @return the prt participant @ the service exception
	 */
	public PrtParticipant updatePRTParticipants(PRTParticipantDto participant);

	/**
	 * Update action plan.
	 *
	 * @param actionPlan
	 *            the action plan
	 * @return the prt action plan @ the service exception
	 */
	public PrtActionPlan updateActionPlan(PRTActionPlanDto actionPlan);

	/**
	 * Gets the event by id.
	 *
	 * @param eventId
	 *            the event id
	 * @return the event by id @ the service exception
	 */
	public Event getEventById(Long eventId);

	/**
	 * Method Name: selectPrtChildren Method Description:.
	 *
	 * @param idPrtActionPlan
	 *            the id prt action plan
	 * @return the list
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public List<PRTPersonLinkDto> selectPrtChildren(Long idPrtActionPlan);

	/**
	 * Method Name: populateEventIdForChild Method Description:.
	 *
	 * @param prtPersonList
	 *            the prt person list
	 * @param idPrtActionPlan
	 *            the id prt action plan
	 * @param actionPlan
	 *            the action plan
	 * @return the long
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public Long populateEventIdForChild(List<PRTPersonLinkDto> prtPersonList, Long idPrtActionPlan,
			ActionPlanType actionPlan);

	/**
	 * Method Name: selectPRTConnections Method Description:.
	 *
	 * @param idPrtPersonLink
	 *            the id prt person link
	 * @param idChildSUBStage
	 *            the id child SUB stage
	 * @return the list
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public List<PRTConnectionDto> selectPRTConnections(Long idPrtPersonLink, Long idChildSUBStage);

	/**
	 * Method Name: selectPRTParticipants Method Description:.
	 *
	 * @param idPrtActionPlan
	 *            the id prt action plan
	 * @return the list
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public List<PRTParticipantDto> selectPRTParticipants(Long idPrtActionPlan);

	/**
	 * Method Name: selectActionPlan Method Description:.
	 *
	 * @param idPrtActionPlan
	 *            the id prt action plan
	 * @return PRTActionPlanDto
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public PRTActionPlanDto selectActionPlan(Long idPrtActionPlan);

	/**
	 * Select prt perm status lookup.
	 *
	 * @return the list
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public List<PRTPermStatusLookupDto> selectPrtPermStatusLookup();

	/**
	 * Select prt event link with id stage.
	 *
	 * @param idPrtActPlnOrFollowup
	 *            the id prt act pln or followup
	 * @param planType
	 *            the plan type
	 * @return the list
	 */
	public List<PRTEventLinkDto> selectPrtEventLinkWithIdStage(Long idPrtActPlnOrFollowup, ActionPlanType planType);

	/**
	 * Delete PRTParticipant by Id.
	 *
	 * @param idPrtParticipant
	 *            the id prt participant @ the service exception
	 */
	public Long deletePrtParticipant(Long idPrtParticipant);

	/**
	 * Update PRT contact to pers link.
	 *
	 * @param pRTPersonLinkValueDto
	 *            the RT person link value dto
	 * @return the long
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public Long updatePRTContactToPersLink(PRTPersonLinkDto pRTPersonLinkValueDto);

	/**
	 * Method Name: insertPRTParticipant Method Description: this method inserts
	 * record into PRT_PARTICIPANT table.
	 *
	 * @param participantDto
	 *            the participant dto
	 * @return idPrtParticipant
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public Long insertPRTParticipant(PRTParticipantDto participantDto);

	/**
	 * Method Name: fetchPRTParticipants Method Description:.
	 *
	 * @param prtActionPlanId
	 *            the prt action plan id
	 * @return the array list
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public ArrayList<PRTParticipantDto> fetchPRTParticipants(Long prtActionPlanId);

	/**
	 * Method Name: getStageIdsForActPlan Method Description:
	 * 
	 * @param idActionPlan
	 * @return List<Long>
	 */
	public List<Long> getStageIdsForActPlan(Long idActionPlan);

	/**
	 * 
	 * Method Name: getTimeStamp Method Description:
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	public Date getTimeStamp(CommonHelperReq commonHelperReq);

}
