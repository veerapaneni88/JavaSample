package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.TletsCheckInDto;
import us.tx.state.dfps.service.admin.dto.TletsCheckOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Clsch6dDao Aug 10, 2017- 12:32:25 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface TletsCheckDao {

	/**
	 * 
	 * Method Name: verifyTLETSPerson Method Description:
	 * 
	 * @param pInputDataRec
	 * @return List<Clsch6doDto> @
	 */
	public List<TletsCheckOutDto> verifyTLETSPerson(TletsCheckInDto pInputDataRec);
}
