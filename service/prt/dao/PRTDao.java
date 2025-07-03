package us.tx.state.dfps.service.prt.dao;

import java.sql.SQLException;
import java.util.List;

import us.tx.state.dfps.common.domain.PrtConnection;
import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTEventLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTPermGoalDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskPersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * BasePRTSessionDao for BasePRTSession Oct 6, 2017- 3:03:42 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PRTDao {

	/**
	 * @param idActionPlan
	 * @param actionPlan
	 * @return List<PRTEventLinkValueDto>
	 */
	public List<PRTEventLinkDto> selectPrtEventLink(long idActionPlan, ActionPlanType actionPlan);

	/**
	 * Method Name: insertPrtEventLink Method Description:This method is used to
	 * insert a record in the PRT_EVENT_LINK table
	 * 
	 * @param prtEventLinkDto
	 */
	public void insertPrtEventLink(PRTEventLinkDto prtEventLinkDto);

	/**
	 * Method Name: insertPRTPersonLink Method Description:This method is used
	 * to insert a record in the PRT_PERSON_LINK table
	 * 
	 * @param prtPersonLinkDto
	 * @return Long - The unique id generated after creating the record.
	 */
	public Long insertPRTPersonLink(PRTPersonLinkDto prtPersonLinkDto);

	/**
	 * Method Name: deletePrtGoalsForChild Method Description:This method is
	 * used to delete the goals for a child from the PRT_PERM_GOAL table.
	 * 
	 * @param idPrtPersonLink
	 */
	public void deletePrtGoalsForChild(Long idPrtPersonLink);

	/**
	 * Method Name: deletePrtConnections Method Description:
	 * 
	 * @param deleteIdConnList
	 */
	public void deletePrtConnections(List<Long> deleteIdConnList);

	/**
	 * This method inserts record into PRT_CONNECTION table.
	 *
	 * @param prtConnection
	 *            the prt connection
	 * @return the prt connection
	 * @returns idPrtConnection - newly created Id.
	 */
	public PrtConnection insertPrtConnection(PRTConnectionDto prtConn);

	/**
	 * Method Name: insertPRTPermGoals Method Description:This method is used to
	 * insert record into PRT_PERM_GOAL table.
	 * 
	 * @param goal
	 */
	public Long insertPRTPermGoals(PRTPermGoalDto goal);

	/**
	 * Method Name: insertPrtStrategy Method Description:This method is used to
	 * insert record into the PRT_STRATEGY table.
	 * 
	 * @param prtStrategyDto
	 */
	public Long insertPrtStrategy(PRTStrategyDto prtStrategyDto);

	/**
	 * Method Name: insertPrtTask Method Description:This method is used to
	 * create a record in the PRT_TASK table.
	 * 
	 * @param task
	 * @return Long - The unique identifier generated after creating the record.
	 */
	public Long insertPrtTask(PRTTaskDto task);

	/**
	 * Method Name: getNewIDPRTPersonLink Method Description: This method
	 * returns new PRT person link for a child.
	 * 
	 * @param idPrtActionPlan
	 * @param idSrcPrtActplnFollowup
	 * @param idPrtActplnFollowup
	 * @param idPrtPersonLink
	 * @return
	 */
	public Long getNewIDPRTPersonLink(Long idPrtActionPlan, Long idSrcPrtActplnFollowup, Long idPrtActplnFollowup,
			Long idPrtPersonLink);

	/**
	 * Method Name: insertTaskPersonLink Method Description:This method is used
	 * to insert a record in the PRT_PERSON_LINK table.
	 * 
	 * @param taskPersonLink
	 */
	public void insertTaskPersonLink(PRTTaskPersonLinkDto taskPersonLink);

	/**
	 * Method Name: updatePRTPersonLink Method Description:This method is used
	 * to update the children for PRT Follow-Up Details.
	 * 
	 * @param prtChild
	 */
	public void updatePRTPersonLink(PRTPersonLinkDto prtChild);

	/**
	 * Method Name: deletePRTStrategy Method Description:This method is used to
	 * delete the strategy and tasks from the following tables
	 * -PRT_STRATEGY,PRT_TASK_PERSON_LINK,PRT_TASK
	 * 
	 * @param idStrategy
	 */
	public void deletePRTStrategy(Long idStrategy);

	/**
	 * This method fetches records from PRT_PERM_GOAL table for the Child from
	 * PRT Tables.
	 * 
	 * @param idPrtPersonLink
	 * 
	 * @return List <PRTPermGoalValueBean> or null if record not found.
	 * 
	 * @
	 */

	public List<PRTPermGoalDto> selectPRTPermGoals(Long idPrtPersonLink);

	/**
	 * This method gets latest Legal Status for the Child.
	 * 
	 * @param idPerson
	 * 
	 * @return Legal Status.
	 * 
	 * @.
	 */

	public LegalStatusDto fetchLatestLegalStatus(Long idPerson);

	/**
	 * This method fetches single row from PRT_STRATEGY table.
	 * 
	 * @param idPrtActPlnOrFollowup
	 * @param ActionPlanType
	 * 
	 * @return List <PRTEventLinkValueDto> or null if record not found.
	 * 
	 * @
	 */

	public List<PRTStrategyDto> selectPrtStrategy(Long idPrtActPlnOrFollowup, ActionPlanType planType);

	/**
	 * This method fetches records from PRT_TASK table for the given Strategy.
	 * 
	 * @param idPrtStrategy
	 * @param current
	 * 
	 * @return List <PRTTaskValueDto> or null if record not found.
	 */

	public List<PRTTaskDto> selectPrtTasks(Long idPrtStrategy, boolean current);

	/**
	 * This method fetches records from PRT_TASK_PERSON_LINK table for the given
	 * Strategy.
	 * 
	 * @param idPrtTask
	 * 
	 * @return List <PRTTaskPersonLinkValueBean> or null if record not found.
	 * 
	 * @throws SQLException,
	 *             DaoException
	 */

	public List<PRTTaskPersonLinkDto> selectChildrenForTask(Long idPrtTask);

	/**
	 * This method fetches PRT_PERSON_LINK rows for the given Action Plan.
	 * 
	 * @param idPrtPersonLink
	 * 
	 * @return List <PRTPersonLinkValueBean> or null if record not found.
	 * 
	 * @
	 */

	public List<PRTEventLinkDto> selectPrtEventLink(Long idPrtActPlnOrFollowup, ActionPlanType planType);
}
