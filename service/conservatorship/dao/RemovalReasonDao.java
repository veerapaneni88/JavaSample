package us.tx.state.dfps.service.conservatorship.dao;

import java.util.List;

import us.tx.state.dfps.service.conservatorship.dto.RemovalReasonDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Get the
 * Removal reason Mar 1, 2018- 5:56:54 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface RemovalReasonDao {

	/**
	 * Method Description: This Method will be used to Retrieves a full row from
	 * the Removal Reason table Service Name:CSUB14S DAM:CLSS21D
	 * 
	 * @param idEvent
	 * @return List<RemovalReasonDto> @
	 */

	public List<RemovalReasonDto> getRemReasonDtl(List<Long> idEventList);

}
