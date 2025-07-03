/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 28, 2018- 3:24:01 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.prt.service;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PRTActionPlanReq;
import us.tx.state.dfps.service.common.request.PRTParticipantReq;
import us.tx.state.dfps.service.common.response.PRTActionPlanRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.prt.dto.PRTParticipantDto;
import us.tx.state.dfps.service.prt.dto.PRTPermStatusLookupDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.subcare.dto.PRTActionPlanDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 28, 2018- 3:24:01 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PRTActionPlanService {

	/**
	 * Method Name: fetchActionPlan Method Description:This method retrieves all
	 * PRT Action Plan Details.
	 *
	 * @param idActionPlanEvent
	 *            the id action plan event
	 * @param idStage
	 *            the id stage
	 * @param idCase
	 *            the id case
	 * @return PRTActionPlanDto @ the service exception
	 */
	public PreFillDataServiceDto displayActionPlanForm(Long idActionPlanEvent, Long idStage, Long idCase);

	/**
	 * Gets the stage ids for act plan.
	 *
	 * @param idPrtActionPlan
	 *            the id prt action plan
	 * @return the stage ids for act plan @ the service exception
	 */
	public List<Long> getStageIdsForActPlan(Long idPrtActionPlan);

	/**
	 * Method Name: fetchActionPlan Method Description:This method retrieves all
	 * PRT Action Plan Details.
	 *
	 * @param idActionPlanEvent
	 *            the id action plan event
	 * @param idStage
	 *            the id stage
	 * @param idCase
	 *            the id case
	 * @return PRTActionPlanDto @ the service exception
	 */
	public PRTActionPlanDto fetchActionPlan(Long idActionPlanEvent, Long idStage, Long idCase);

	/**
	 * Method Name: validateAddActionPlan Method Description:This method
	 * validates before creating new Action Plan.
	 *
	 * @param idStage
	 *            the id stage
	 * @param idCase
	 *            the id case
	 * @return List<Long> @ the service exception
	 */
	public List<Long> validateAddActionPlan(Long idStage, Long idCase);

	/**
	 * Fetch prt perm status lookup.
	 *
	 * @return the list @ the service exception
	 */
	public List<PRTPermStatusLookupDto> fetchPrtPermStatusLookup();

	/**
	 * Delete action plan.
	 *
	 * @param eventId
	 *            the event id
	 * @param stageId
	 *            the stage id
	 * @param caseId
	 *            the case id @ the service exception
	 */
	public Long deleteActionPlan(Long eventId, Long stageId, Long caseId);

	/**
	 * Save action plan.
	 *
	 * @param actionPlan
	 *            the action plan
	 * @return the int @ the service exception
	 */
	public Long saveActionPlan(PRTActionPlanDto actionPlan);

	/**
	 * Method Name: updatePRTContactToPersLink Method Description: This method
	 * update PRTContactToPersLink.
	 *
	 * @param pRTActionPlanReq
	 *            the RT action plan req
	 * @return the long @ the service exception
	 */
	public Long updatePRTContactToPersLink(PRTActionPlanReq pRTActionPlanReq);

	/**
	 * This method retrieves all PRT Action Plan Details.
	 * 
	 * @param idActionPlanEvent
	 * @param idStage
	 * @param idCase
	 * 
	 * @return PRTActionPlanValueBean
	 */
	public PRTActionPlanDto fetchActionPlan(Long idPrtActionPlan);

	/**
	 * Insert PRT participant.
	 *
	 * @param participant
	 *            the participant
	 * @return the long @ the service exception
	 */
	public Long insertPRTParticipant(PRTParticipantReq participant);

	/**
	 * Delete PRT participant.
	 *
	 * @param idPrtParticipant
	 *            the id prt participant @ the service exception
	 */
	public Long deletePRTParticipant(Long idPrtParticipant);

	/**
	 * Fetch and populate latest child plans.
	 *
	 * @param stageIdList
	 *            the stage id list
	 * @param children
	 *            the children
	 */
	public List<PRTPersonLinkDto> fetchAndPopulateLatestChildPlans(List<Long> stageIdList,
			List<PRTPersonLinkDto> children);

	/**
	 * Fetch latest legal status.
	 *
	 * @param idPerson
	 *            the id person
	 * @return the legal status dto @ the service exception
	 */
	public LegalStatusDto fetchLatestLegalStatus(Long idPerson);

	/**
	 * Fetch open action plan.
	 *
	 * @param idPerson
	 *            the id person
	 * @return the PRT action plan res @ the service exception
	 */
	public PRTActionPlanRes fetchOpenActionPlan(Long idPerson);

	/**
	 * Method Name: deletePRTStrategy Method Description:This method is used to
	 * delete the PRT Strategy and Tasks related to the Strategy.This method
	 * calls the dao implementation to delete from
	 * PRT_STRATEGY,PRT_TASK_PERSON_LINK,PRT_TASK tables.
	 * 
	 * @param prtActplanFollowUpReq
	 */
	public void deletePRTStrategy(PRTActionPlanReq prtActionPlanReq);

	/**
	 * 
	 * Method Name: selectPRTParticipants Method Description: This method is
	 * used to fetch the List of Participants for that PRT Action Plan
	 * 
	 * @param idPrtActionPlan
	 * @return
	 */
	public List<PRTParticipantDto> selectPRTParticipants(Long idPrtActionPlan);

	/**
	 * 
	 * Method Name: getTimeStamp Method Description:
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	public Date getTimeStamp(CommonHelperReq commonHelperReq);
}
