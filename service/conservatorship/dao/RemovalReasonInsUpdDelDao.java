package us.tx.state.dfps.service.conservatorship.dao;

import us.tx.state.dfps.service.cvs.dto.RemovalReasonInsUpdDelInDto;
import us.tx.state.dfps.service.cvs.dto.RemovalReasonInsUpdDelOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Caud30dDao
 * Aug 13, 2017- 11:49:32 AM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface RemovalReasonInsUpdDelDao {

	/**
	 * 
	 * Method Name: removalReasonInsUpdDel Method Description:DAM: caud30dAUDdam
	 * 
	 * @param pInputDataRec
	 * @return @
	 */
	public RemovalReasonInsUpdDelOutDto removalReasonInsUpdDel(RemovalReasonInsUpdDelInDto removalReasonInsUpdDelInDto);
}
