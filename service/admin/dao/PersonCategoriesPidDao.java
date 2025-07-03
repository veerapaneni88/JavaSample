package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PersonCategoryInDto;
import us.tx.state.dfps.service.admin.dto.PersonCategoryOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cinv29 Aug 5, 2017- 11:54:44 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PersonCategoriesPidDao {

	/**
	 * 
	 * Method Name: getAllCaegoriesForPID Method Description: This method will
	 * get data from PERSON_CATEGORY table.
	 * 
	 * @param personCategoryInDto
	 * @return List<PersonCategoryOutDto>
	 */
	public List<PersonCategoryOutDto> getAllCaegoriesForPID(PersonCategoryInDto personCategoryInDto);
}
