package us.tx.state.dfps.service.placement.dao;

import us.tx.state.dfps.service.placement.dto.ApprovalInfoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: The
 * interfaces to retrieve the child's plan placement Jan 27, 2018- 5:26:34 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface ChildPlanPlacementDao {

	/**
	 * 
	 * Method Name: getApprovalInfo Method Description: To retrieve the
	 * approval, approver and person data
	 * 
	 * @param idEvent
	 * @return @
	 */
	public ApprovalInfoDto getApprovalInfo(Long idEvent);

	/**
	 * 
	 * Method Name: getDecodeValue Method Description: To retrieve the
	 * decodedValue based on codeType, linktableCode and codestablesCode
	 * 
	 * @param codeType
	 * @param linktableCode
	 * @param codestablesCode
	 * @return @
	 */
	public String getDecodeValue(String codeType, String linktableCode, String codestablesCode);

}
