package us.tx.state.dfps.service.admin.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.CommonTodoInDto;
import us.tx.state.dfps.service.admin.dto.CommonTodoOutDto;
import us.tx.state.dfps.service.admin.service.CommonTodoService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * performs the business logic neccessary to execute the TODO COMMON FUNCTION.
 * This service will help standardize/formalize how Todo's are created.It will
 * prevent hard-coding, of Todo descriptions, due dates, and other data within
 * the functional program. Aug 11, 2017- 9:30:20 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/commontodo")
public class CommonTodoController {

	@Autowired
	CommonTodoService objCsub40uService;

	private static final Logger log = Logger.getLogger(CommonTodoController.class);

	/**
	 * This service performs the business logic neccessary to execute the TODO
	 * COMMON FUNCTION. This service will help standardize/formalize how Todo's
	 * are created. It will prevent hard-coding, of Todo descriptions, due
	 * dates, and other data within the functional program.
	 *
	 * @param objCsub40uiDto
	 * @return
	 */
	@RequestMapping(value = "/commontodoouto", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonTodoOutDto CommonTodoOutDto(@RequestBody CommonTodoInDto objCsub40uiDto) {
		log.debug("Entering method CommonTodoOutDto in CommonTodoController");
		CommonTodoOutDto objCsub40uoDto = objCsub40uService.callCsub40uService(objCsub40uiDto);
		log.debug("Exiting method CommonTodoOutDto in CommonTodoController");
		return objCsub40uoDto;
	}
}
