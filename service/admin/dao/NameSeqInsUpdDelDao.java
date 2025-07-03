package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.NameSeqInsUpdDelInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cinv32 Aug 11, 2017- 4:34:13 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface NameSeqInsUpdDelDao {

	/**
	 * 
	 * Method Name: updateNameRecord Method Description: Inserts/Update/Delete a
	 * record in NAME table.
	 * 
	 * @param nameSeqInsUpdDelInDto
	 * @return int
	 */
	public int updateNameRecord(NameSeqInsUpdDelInDto nameSeqInsUpdDelInDto);
}
