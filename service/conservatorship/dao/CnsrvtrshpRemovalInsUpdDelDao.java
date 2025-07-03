package us.tx.state.dfps.service.conservatorship.dao;

import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalInsUpdDelInDto;
import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalInsUpdDelOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Caud29dDao
 * Aug 12, 2017- 12:36:05 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface CnsrvtrshpRemovalInsUpdDelDao {

	public CnsrvtrshpRemovalInsUpdDelOutDto cnsrvtrshpRemovalInsUpdDel(
			CnsrvtrshpRemovalInsUpdDelInDto cnsrvtrshpRemovalInsUpdDelInDto);
}
