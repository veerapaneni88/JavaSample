package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.common.domain.Name;
import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN04S Class
 * Description:Operations for Name Apr 14, 2017 - 11:16:39 AM
 */
public interface NameService {
	/**
	 * 
	 * Method Description:getEmpNameDto
	 * 
	 * @param name
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */

	// CCMN04S
	public EmpNameDto getEmpNameDto(Name name) throws DataNotFoundException;

}
