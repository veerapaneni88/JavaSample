package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN19S Class
 * Description: Approval DAO Interface Apr 3, 2017 - 3:45:39 PM
 */

public interface ApprovalEventDelDao {

	/**
	 * 
	 * Method Description: Method is implemented in ApprovalEventDelDaoImpl to
	 * perform delete operations Service Name: CCMN19S
	 * 
	 * @param archInputDto
	 * @param approvalID
	 * @return ServiceResHeaderDto @
	 */
	public String getApprovalEventrecordDel(ServiceReqHeaderDto serviceReqHeaderDto, Long approvalID, Long toDoID,
			List<Long> eventList);
}
