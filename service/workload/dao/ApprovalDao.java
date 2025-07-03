package us.tx.state.dfps.service.workload.dao;

import us.tx.state.dfps.common.domain.Approval;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.workload.dto.ApprovalDto;
import us.tx.state.dfps.service.workload.dto.ApprovalPersonSearchDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN19S Class
 * Description: Approval DAO Interface Apr 3, 2017 - 3:45:39 PM
 */

public interface ApprovalDao {

	/**
	 * 
	 * Method Description: Method is implemented in ApprovalDaoImpl to perform
	 * AUD operations Service Name: CCMN19S
	 * 
	 * @param archInputDto
	 * @param approvalDto
	 * @return ServiceResHeaderDto @
	 */
	public String getApprovalAUD(ServiceReqHeaderDto serviceReqHeaderDto, ApprovalDto approvalDto, Long idEvent);

	/**
	 * 
	 * Method Description:call ApprovalPersonSearchbyId in Impl class to
	 * retrieve person details
	 * 
	 * @param idApproval
	 * @return ApprovalPersonSearchDto @
	 */
	public ApprovalPersonSearchDto approvalPersonSearchbyId(Long idApproval);

	/**
	 * Method Description: Get Stage Id based on the Case ID. Service Name: Kin
	 * Approval Ejb
	 * 
	 * @param idCase
	 * @return Long @
	 */
	public Boolean checkStageCaseID(Long idCase);

	/**
	 * Returns the last (using the fact that ID columns increment) approvers
	 * status record for the event. This will always be one of "APRV," "REJT,"
	 * "PEND," OR "INVD." If the event has not been submitted for approval, it
	 * will be null.
	 *
	 * @param ulIdEvent
	 *            The ID_EVENT of the event for which the Approvers status will
	 *            be returned.
	 * @return The approvers status for the particular event.
	 */
	String getApproversStatus(Long idEvent);

	/**
	 * Method Name: getPrimaryWorkerIdForStage Method Description:
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	public CommonHelperRes getPrimaryWorkerIdForStage(CommonHelperReq commonHelperReq);

	/**
	 * AR - Stage progression from AR - FPR - message not displayed This method
	 * will fetch the overall disposition
	 * 
	 * @param idCase
	 *            the stage identifier
	 * @return String disposition code @
	 */
	String getARStageOverallDisposition(Long idCase, Long idStage);

	/**
	 * Returns idEvent for the given Approval Event.
	 * 
	 * @param idAprvlEvent
	 * 
	 * @return idEvent
	 * 
	 * @
	 */
	Long fetchIdEventForIdAprEvent(Long IdApproval);

	/**
	 * Returns the ulIdPerson of the Primary Worker of the stage. i;e the Person
	 * with role as PRIMARY ("PR").
	 * 
	 * @param ulIdStage
	 * @return
	 */

	public long getPrimaryWorkerIdForStage(Long ulIdStage);

	public void saveorUpdate(Approval approval);
}
