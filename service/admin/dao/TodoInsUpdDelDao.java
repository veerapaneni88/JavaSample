package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.TodoInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.TodoInsUpdDelOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description: Ccmn43dDao
 *
 * Aug 7, 2017- 9:38:37 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface TodoInsUpdDelDao {

	/**
	 * Legacy Name: ccmn43dAUDdam
	 * 
	 * @param todoInsUpdDelInDto
	 * @return TodoInsUpdDelOutDto
	 */
	public TodoInsUpdDelOutDto cudTODO(TodoInsUpdDelInDto todoInsUpdDelInDto);
}
