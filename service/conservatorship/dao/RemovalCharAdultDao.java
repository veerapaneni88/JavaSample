package us.tx.state.dfps.service.conservatorship.dao;

import java.util.List;

import us.tx.state.dfps.service.conservatorship.dto.RemovalCharAdultDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB14S Class
 * Description: RemovalCharAdultDao Interface May 1, 2017 - 11:15:39 AM
 */
public interface RemovalCharAdultDao {

	/**
	 * Method Description: This Method will be used for full row retreival from
	 * the Removal Char Adult using IdEvent to return the records. Service
	 * Name:CSUB14S DAM:CLSS23D
	 * 
	 * @param idEvent
	 * @return List<RemovalCharAdultDto> @
	 */
	public List<RemovalCharAdultDto> getRemCharAdultDtl(List<Long> idEventList);

}
