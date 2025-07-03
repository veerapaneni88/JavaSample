package us.tx.state.dfps.service.conservatorship.dao;

import java.util.List;

import us.tx.state.dfps.service.conservatorship.dto.RemovalCharChildDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB14S Class
 * Description: RemovalCharChildDao Interface May 1, 2017 - 10:45:39 AM
 */
public interface RemovalCharChildDao {

	/**
	 * Method Description: This Method will be used for full row retreival from
	 * the Removal Char Child using IdEvent to return the records. Service
	 * Name:CSUB14S DAM:CLSS22D
	 * 
	 * @param idEvent
	 * @return List<RemovalCharChildDto> @
	 */

	public List<RemovalCharChildDto> getRemCharChildDtl(List<Long> idEvents);

}
