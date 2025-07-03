package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventApprovalEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.EventApprovalEventLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Ccmn57dao Aug 8, 2017- 10:26:07 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface EventApprovalEventLinkDao {

	/**
	 * 
	 * Method Name: getApprovalEventLink Method Description:
	 * 
	 * @param pInputDataRec
	 * @return List<Ccmn57doDto> @
	 */
	public List<EventApprovalEventLinkOutDto> getApprovalEventLink(EventApprovalEventLinkInDto pInputDataRec);
}
