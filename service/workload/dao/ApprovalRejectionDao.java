package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.ApprovalRejectionPersonDto;
import us.tx.state.dfps.service.workload.dto.RejectApprovalDto;

public interface ApprovalRejectionDao {

	/**
	 * 
	 * Method Description:CCMNI3D dam that retrieves all row from the
	 * APPROVAL_REJECTION table for any given stage Dam Name: CCMNI3D
	 * 
	 * @param idCase
	 * @param idStage
	 * @return List<ApprovalRejectionPersonSearchDto>
	 * @throws DataNotFoundException
	 * @
	 */
	public List<ApprovalRejectionPersonDto> approvalRejectionPersonSearch(Long idCase, Long idStage);

	/**
	 * Method Name: saveRejectionApproval Method Description:This method is used
	 * to save the incomplete CCL Rejection check box for CCL Program.
	 * 
	 * @param rejectApprovalDto
	 */
	public void saveRejectionApproval(RejectApprovalDto rejectApprovalDto);

}
