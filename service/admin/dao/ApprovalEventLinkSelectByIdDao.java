package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.ApprovalEventLinkSelectByIdInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalEventLinkSelectByIdOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Ccmn55d Aug 8, 2017- 9:09:10 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface ApprovalEventLinkSelectByIdDao {

	/**
	 * 
	 * Method Name: getIDApproval Method Description:
	 * 
	 * @param pInputDataRec
	 * @return List<Ccmn55doDto> @
	 */
	public List<ApprovalEventLinkSelectByIdOutDto> getIDApproval(ApprovalEventLinkSelectByIdInDto pInputDataRec);
}
