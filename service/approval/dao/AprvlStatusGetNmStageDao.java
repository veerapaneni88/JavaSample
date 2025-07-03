package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.SaveApprovalStatusNmStageReq;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusNmStageRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 8,
 * 2018- 3:38:32 PM © 2018 Texas Department of Family and Protective Services
 */
public interface AprvlStatusGetNmStageDao {

	/**
	 * Method Name: getnmStage Method Description: This method retrieves a stage
	 * name from the Stage table based on an ID_stage as an input. DAM NAME:
	 * CCMNJ7D Service Name: CCMN35S
	 * 
	 * @param SaveApprovalStatusNmStageReq
	 * @return SaveApprovalStatusNmStageRes
	 */
	public SaveApprovalStatusNmStageRes getNmStage(SaveApprovalStatusNmStageReq saveApprovalStatusNmStageReq);
}
