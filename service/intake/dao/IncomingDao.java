package us.tx.state.dfps.service.intake.dao;

import us.tx.state.dfps.common.domain.IncomingDetail;
import us.tx.state.dfps.service.common.request.PriorityClosureSaveReq;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Mar 30, 2017 - 4:06:02 PM
 */
public interface IncomingDao {

	/**
	 * 
	 * Method Description: Method for Priority Closure Save/Close. This Updates
	 * IncomingDetail.
	 * 
	 * @param PriorityClosureSaveReq
	 * @return @ Tuxedo Service Name: CINT21S
	 */

	public void updateIncomingDetail(IncomingDetail incomingDetail, PriorityClosureSaveReq priorityClosureSaveReq);

}
