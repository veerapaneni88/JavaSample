package us.tx.state.dfps.service.commontodofunction.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.TodoCreateOutDto;
import us.tx.state.dfps.service.common.request.CommonToDoFunctionReq;
import us.tx.state.dfps.service.common.response.CommonToDoFunctionRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.commontodofunction.service.CommonToDoFunctionService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * controller implements the methods declared in Csub40uBean Oct 25, 2017-
 * 5:30:20 PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/CommonToDoFunctionController")
public class CommonToDoFunctionController {

	@Autowired
	private CommonToDoFunctionService commonToDoFunctionService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(CommonToDoFunctionController.class);

	/**
	 * 
	 * Method Name: TodoCommonFunction Method Description:
	 * 
	 * @param commonToDoFunctionReq
	 * @return CommonToDoFunctionRes
	 */
	@RequestMapping(value = "/TodoCommonFunction", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonToDoFunctionRes TodoCommonFunction(
			@RequestBody CommonToDoFunctionReq commonToDoFunctionReq) {
		log.debug("Entering method callCSUB40U in Csub40uController");
		if (TypeConvUtil.isNullOrEmpty(commonToDoFunctionReq.getTodoCreateInDto())) {
			throw new InvalidRequestException(messageSource.getMessage("TodoCreateOutDto.not.found", null, Locale.US));
		}
		TodoCreateOutDto todoCreateOutDto = commonToDoFunctionService
				.callCSUB40U(commonToDoFunctionReq.getTodoCreateInDto());
		CommonToDoFunctionRes commonToDoFunctionRes = new CommonToDoFunctionRes();
		commonToDoFunctionRes.setTodoCreateOutDto(todoCreateOutDto);
		log.debug("Exiting method callCSUB40U in Csub40uController");
		return commonToDoFunctionRes;
	}

	/**
	 * 
	 * Method Name: doCSUB40UO Method Description:
	 * 
	 * @param commonToDoFunctionReq
	 * @return CommonToDoFunctionRes
	 */
	@RequestMapping(value = "/doCSUB40UO", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonToDoFunctionRes doCSUB40UO(@RequestBody CommonToDoFunctionReq commonToDoFunctionReq) {
		log.debug("Entering method doCSUB40UO in Csub40uController");
		if (TypeConvUtil.isNullOrEmpty(commonToDoFunctionReq.getTodoCreateInDto())) {
			throw new InvalidRequestException(messageSource.getMessage("TodoCreateOutDto.not.found", null, Locale.US));
		}
		TodoCreateOutDto todoCreateOutDto = commonToDoFunctionService
				.doCSUB40UO(commonToDoFunctionReq.getTodoCreateInDto());
		log.debug("Exiting method doCSUB40UO in Csub40uController");
		CommonToDoFunctionRes commonToDoFunctionRes = new CommonToDoFunctionRes();
		commonToDoFunctionRes.setTodoCreateOutDto(todoCreateOutDto);
		return commonToDoFunctionRes;
	}
}
