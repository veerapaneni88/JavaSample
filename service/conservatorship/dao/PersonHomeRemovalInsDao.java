package us.tx.state.dfps.service.conservatorship.dao;

import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalInsInDto;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalInsOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Caud41dDao
 * Aug 15, 2017- 4:40:56 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface PersonHomeRemovalInsDao {

	/**
	 * 
	 * Method Name: personHomeRemovalAUD Method Description:caud41dAUDdam
	 * 
	 * @param personHomeRemovalInsInDto
	 * @return @
	 */
	public PersonHomeRemovalInsOutDto personHomeRemovalAUD(PersonHomeRemovalInsInDto personHomeRemovalInsInDto);
}
