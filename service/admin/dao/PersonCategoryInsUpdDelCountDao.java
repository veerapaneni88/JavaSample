package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.PersonCategoryInsUpdDelCountInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for CCMNC2d Aug 11, 2017- 2:42:59 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PersonCategoryInsUpdDelCountDao {

	/**
	 * 
	 * Method Name: updatePersonCategory Method Description: Update Person
	 * Category record
	 * 
	 * @param personCategoryInsUpdDelCountInDto
	 * @return int
	 */
	public int updatePersonCategory(PersonCategoryInsUpdDelCountInDto personCategoryInsUpdDelCountInDto);
}
