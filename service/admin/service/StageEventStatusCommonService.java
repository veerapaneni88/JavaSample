package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.StageTaskOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for StageEventStatusCommonServiceImpl Aug 14, 2017- 4:16:15 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface StageEventStatusCommonService {

	/**
	 * 
	 * Method Name: CheckStageEventStatus Method Description:This is a Common
	 * Function (not a service) which is called by services which update
	 * functional tables. It is packaged in the libappd.a archive library. This
	 * function receives the following inputs: ReqFuncCd, IdStage, and CdTask.
	 * 
	 * @param pCCMN06UInputRec
	 * @return String @
	 */
	public String checkStageEventStatus(StageTaskInDto pCCMN06UInputRec);

	/**
	 * 
	 * Method Name: CallCSES71D Method Description: This method will get data
	 * from STAGE table.
	 * 
	 * @param pInputMsg
	 * @param pbStageIsClosed
	 * @return List<StageTaskOutDto> @
	 */
	public List<StageTaskOutDto> CallCSES71D(StageTaskInDto pInputMsg, boolean pbStageIsClosed);

	/**
	 * 
	 * Method Name: callCcmn06uService Method Description: This is a Common
	 * Function (not a service) which is called by services which update
	 * functional tables. It is packaged in the libappd.a archive library. This
	 * function receives the following inputs: cReqFuncCd, ulIdStage, and
	 * szCdTask.
	 * 
	 * @param pInputMsg
	 * @return StageTaskOutDto @
	 */
	public StageTaskOutDto callCcmn06uService(StageTaskInDto pInputMsg);
}
