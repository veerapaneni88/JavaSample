package us.tx.state.dfps.service.workload.service;

import us.tx.state.dfps.common.dto.TodoCommonFunctionInputDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionOutputDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB40U Class
 * Description: Todo Common Function Apr 10, 2017 - 10:51:33 AM
 */

public interface TodoCommonFunctionService {

	/**
	 * This service performs the business logic neccessary to execute the TODO
	 * COMMON FUNCTION. This service will help standardize/formalize how Todo's
	 * are created. It will prevent hard-coding, of Todo descriptions, due
	 * dates, and other data within the functional program.
	 * 
	 * Service Name: CSUB40U
	 * 
	 * @param todoCommonFunctionInputDto
	 * @return @
	 */
	public TodoCommonFunctionOutputDto TodoCommonFunction(TodoCommonFunctionInputDto todoCommonFunctionInputDto);

}
