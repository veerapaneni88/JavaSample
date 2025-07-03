package us.tx.state.dfps.service.pcgoal.service;

import us.tx.state.dfps.service.common.request.PCGoalReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CCMN56S Mar
 * 5, 2018- 1:58:28 PM © 2017 Texas Department of Family and Protective Services
 */
public interface PCGoalService {

	/**
	 * Method Name: getPCGoal Method Description: Populates form ccmn0100, which
	 * outputs Permanency Goal and Concurrent Goal definitions. Definitions are
	 * to be included on the CPOS and FPOS forms.
	 * 
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getPCGoal(PCGoalReq pCGoalReq);

}
