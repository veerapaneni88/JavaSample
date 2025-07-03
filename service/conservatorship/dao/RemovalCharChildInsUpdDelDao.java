package us.tx.state.dfps.service.conservatorship.dao;

import us.tx.state.dfps.service.cvs.dto.RemovalCharChildInsUpdDelInDto;
import us.tx.state.dfps.service.cvs.dto.RemovalCharChildInsUpdDelOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Caud31dDao
 * Aug 18, 2017- 10:58:47 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface RemovalCharChildInsUpdDelDao {

	public RemovalCharChildInsUpdDelOutDto removalCharChildInsUpdDel(
			RemovalCharChildInsUpdDelInDto removalCharChildInsUpdDelInDto);
}
