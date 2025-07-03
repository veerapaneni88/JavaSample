package us.tx.state.dfps.service.prt.service;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PRTActplanFollowUpReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.subcare.dto.PRTActPlanFollowUpDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the interface which declares the methods to be used for retrieving/saving
 * data for PRT Follow-Up. Mar 28, 2018- 3:27:10 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PRTActPlanFollowUpService {

	/**
	 * This method retrieves all PRT Action Plan FollowUp Details.
	 * 
	 * @param idActplnFollowupEvent
	 * @param idStage
	 * @param idCase
	 * 
	 * @return PRTActPlanFollowUpValueDto
	 */

	public PreFillDataServiceDto displayActPlanFollowUpFrom(Long idEvent, Long idStage, Long idCase);

	/**
	 * Method Name: isChildExitingPRT Method Description:This method checks if
	 * the Child is exiting PRT. if the child's legal status changed to one of
	 * the following values : Adoption Consummated Child Emancipated FPS Resp.
	 * terminated
	 * 
	 * @param idChild
	 *            - The person id of the child.
	 * @return boolean - The boolean value of true or false if the child is
	 *         exiting PRT.
	 */
	public boolean isChildExitingPRT(Long idChild);

	/**
	 * Method Name: fetchPrevFollowUp Method Description:This method is used to
	 * fetch the details of the follow-up.
	 * 
	 * @param idFollowUp
	 *            - The id of the event of the follow-up.
	 * @param idStage
	 *            - The stage id in which the follow-up was created.
	 * @param idCase
	 *            - The case id in which the follow-up was created.
	 * @return PRTActPlanFollowUpDto - The dto with the follow-up details.
	 */

	public PRTActPlanFollowUpDto fetchActPlanFollowUp(Long idActplnFollowupEvent, Long idStage, Long idCase);

	/**
	 * Method Name: fetchPrevFollowUp Method Description:This method is used to
	 * fetch the previous follow-up.
	 * 
	 * @param idFollowUp
	 *            - The id of the previous follow-up.
	 * @param idStage
	 *            - The stage id in which the follow-up was created.
	 * @param idCase
	 *            - The case id in which the follow-up was created.
	 * @return PRTActPlanFollowUpDto - The dto with the follow-up details.
	 */
	public PRTActPlanFollowUpDto fetchPrevFollowUp(Long idFollowUp, Long idStage, Long idCase);

	/**
	 * Method Name: validateAddNewFollowUp Method Description:This method is
	 * used to validate if a new PRT Follow-Up can be created.
	 * 
	 * @param prtActPlanFollowUpReq
	 *            - The dto holds the input paramter value - idCase, idStage,
	 *            idEvent
	 * @return List<String> - The dto holds the list of errors if present.
	 */
	List<Integer> validateAddNewFollowUp(PRTActplanFollowUpReq prtActPlanFollowUpReq);

	/**
	 * Method Name: getFollowUpType Method Description:This method is used to
	 * create the follow-Up type list.
	 * 
	 * @param prtActPlanFollowUpDto
	 *            - This dto is used to hold the input paramter values for
	 *            creating the follow-up type list.
	 * @return List - The list of options to be excluded in the follow-up type
	 *         drop-down.
	 */
	public List<String> getFollowUpTypeExcludeOptions(PRTActPlanFollowUpDto prtActPlanFollowUpDto);

	/**
	 * Method Name: savePRTFollowUp Method Description:This method is used to
	 * save the PRT Follow-Up details.
	 * 
	 * @param PRTActPlanFollowUpDto
	 *            -The dto will hold the PRT Follow-Up details to be
	 *            saved/updated.
	 * @return Long - The id of the record saved/updated PRT Follow-Up details.
	 */
	public Long savePRTFollowUp(PRTActPlanFollowUpDto prtActPlanFollowUpDto);

	/**
	 * Method Name: getFollowUpDetails Method Description:
	 * 
	 * @param prtActPlanFollowUpDto
	 *            - This dto will hold the input paramter values for fetching
	 *            the PRT Follow-Up details for a particular PRT Follow-Up Type.
	 * @return PRTActPlanFollowUpDto - This dto will hold the PRT Follow-Up
	 *         details specific to Follow-Up Type.
	 */
	public PRTActPlanFollowUpDto getFollowUpDetails(PRTActPlanFollowUpDto prtActPlanFollowUpDto);

	/**
	 * Method Name: savePRTStrategiesAndTasks Method Description:This method is
	 * used to save the strategies and the tasks related to the strategies. The
	 * service implementations calls the dao implementation which will insert
	 * data into PRT_STRATEGY,PRT_TASK,PRT_TASK_PERSON_LINK tables.
	 * 
	 * @param prtActPlanFollowUpDto
	 *            - The dto will hold the follow-up ,strategy and task related
	 *            information.
	 */
	public void savePRTStrategiesAndTasks(PRTActPlanFollowUpDto prtActPlanFollowUpDto);

	/**
	 * 
	 * Method Name: getTimeStamp Method Description:
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	public Date getTimeStamp(CommonHelperReq commonHelperReq);

}
