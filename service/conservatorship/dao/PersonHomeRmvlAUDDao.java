package us.tx.state.dfps.service.conservatorship.dao;

import us.tx.state.dfps.service.cvs.dto.PersonHomeRemInputDto;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Simple
 * Standard AUD on table PERSON_HOME_REMOVAL Mar 1, 2018- 5:55:30 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface PersonHomeRmvlAUDDao {
	/**
	 * 
	 * Method Name: personHomeRemovalAUD Method Description: caud12dAUDdam
	 * 
	 * @param personHomeRemInputDto
	 * @param personHomeRemOutDto
	 * @
	 */
	public void personHomeRemovalAUD(PersonHomeRemInputDto personHomeRemInputDto,
			PersonHomeRemOutDto personHomeRemOutDto);

}
