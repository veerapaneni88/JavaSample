package us.tx.state.dfps.service.approval.dao;

import java.util.List;

import us.tx.state.dfps.approval.dto.ApprovalFormDataDto;
import us.tx.state.dfps.approval.dto.ApprovalSecondaryCommentsDto;
import us.tx.state.dfps.approval.dto.ApproverJobHistoryDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.ApprovalRejectionPersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * methods for DB access for Approval Form Mar 14, 2018- 10:56:02 AM © 2017
 * Texas Department of Family and Protective Services
 */
public interface ApprovalFormDao {

	/**
	 * Method Name: getApprovalData Method Description: Gets approval data
	 * 
	 * @param idEvent
	 * @return ApprovalFormDataDto
	 * @throws DataNotFoundException
	 */
	public ApprovalFormDataDto getApprovalData(Long idEvent);

	/**
	 * Method Name: getApprovalEventLink Method Description: Gets approval and
	 * event data
	 * 
	 * @param idEvent
	 * @return List<EventDto>
	 * @throws DataNotFoundException
	 */
	public List<EventDto> getApprovalEventLink(Long idEvent);

	/**
	 * Method Name: getApprover Method Description: Gets approver data
	 * 
	 * @param idEvent
	 * @return List<ApproverJobHistoryDto>
	 * @throws DataNotFoundException
	 */
	public List<ApproverJobHistoryDto> getApprover(Long idEvent);

	/**
	 * Method Name: getApprovalRejection Method Description: Retrieves rejection
	 * data
	 * 
	 * @param idStage
	 * @return List<ApprovalRejectionPersonDto>
	 * @throws DataNotFoundException
	 */
	public List<ApprovalRejectionPersonDto> getApprovalRejection(Long idStage);

	/**
	 * Method Name: getSecondaryApprovalComments Method Description: To get
	 * secondary approval comments
	 * 
	 * @param idEvent
	 * @return String
	 */
	ApprovalSecondaryCommentsDto getSecondaryApprovalComments(Long idEvent);

}
