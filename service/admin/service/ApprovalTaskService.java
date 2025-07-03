package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.service.admin.dto.ApprovalOutputDto;
import us.tx.state.dfps.service.admin.dto.ApprovalTaskDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * interface for ApprovalTaskServiceImpl Aug 8, 2017- 11:16:02 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ApprovalTaskService {
	/**
	 * Method Name: callCcmn05uService Method Description:Gets approval record
	 * 
	 * @param approvalTaskDto
	 * @return ApprovalOutputDto @
	 */
	public ApprovalOutputDto callCcmn05uService(ApprovalTaskDto approvalTaskDto);

}
