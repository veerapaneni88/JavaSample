package us.tx.state.dfps.service.prt.service;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.common.request.PRTActplanFollowUpReq;
import us.tx.state.dfps.service.prt.dto.PRTPermStatusLookupDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.subcare.dto.PRTActPlanFollowUpDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * is the interface which provides the declaration of common service
 * implementation for PRT Action Plan and PRT Follow-Up. April 6, 2018- 3:03:42
 * PM © 2017 Texas Department of Family and Protective Services
 */
public interface PRTService {

	/**
	 * Method Name: getActPlanEventId Method Description:This method is used to
	 * retrieve the id event for the particular action plan.
	 * 
	 * @param idActionPlan
	 *            - The id of the Action Plan.
	 * @return Long - The id event for the Action Plan.
	 */
	public Long getActPlanEventId(long idActionPlan);

	/**
	 * Method Name: deletePRTStrategy Method Description:This method is used to
	 * delete the PRT Strategy and Tasks related to the Strategy.This method
	 * calls the dao implementation to delete from
	 * PRT_STRATEGY,PRT_TASK_PERSON_LINK,PRT_TASK tables.
	 * 
	 * @param prtActplanFollowUpReq
	 *            - The dto holds the input parameter for the delete operation.
	 */
	public void deletePRTStrategy(PRTActplanFollowUpReq prtActplanFollowUpReq);

	/**
	 * Method Name: fetchStrategies Method Description:This method is used to
	 * fetch the strategies for a particular Action Plan/Follow-Up.
	 * 
	 * @param followUp
	 *            - The dto hold the input parameter values for retrieving
	 *            strategies as well as the PRT Follow-Up details.
	 * @param prevFollowup
	 *            - The boolean indicator to indicate if a previous follow-up
	 *            exits.
	 */
	public void fetchStrategies(PRTActPlanFollowUpDto followUp, boolean prevFollowup);

	/**
	 * Method Name: getExitLegalStatusDate Method Description:This method is
	 * used to get the legal status exit date for a particular child.
	 * 
	 * @param idChild
	 *            - The id Person of the child.
	 * @return Date -The exit date for the child in legal status.
	 */
	public Date getExitLegalStatusDate(Long idChild);

	/**
	 * Method Name: getPermStatus Method Description:This method is used to
	 * fetch the perm status lookup definitions.
	 * 
	 * @return List - The list of Perm Status Lookup definitions.
	 */
	public List<PRTPermStatusLookupDto> getPermStatus();

	/**
	 * Method Name: savePRTConnections Method Description:This method is used to
	 * save and delete the PRT connections.
	 * 
	 * @param prtPersonLinkDto
	 *            - The dto will hold the child details in PRT Action Plan.
	 */
	public void savePRTConnections(PRTPersonLinkDto prtPersonLinkDto);

	/**
	 * Method Name: saveChildPlanGoals Method Description:This method is used to
	 * save the child goals for the PRT Plan.
	 * 
	 * @param prtPersonLinkDto
	 *            - The dto will hold the child details in PRT Action Plan.
	 */
	public void saveChildPlanGoals(PRTPersonLinkDto prtPersonLinkDto);
}
