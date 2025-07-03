package us.tx.state.dfps.service.admin.controller;

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
import us.tx.state.dfps.service.admin.service.ListProfAssmtService;
import us.tx.state.dfps.service.common.request.ListProfAssmtReq;
import us.tx.state.dfps.service.common.response.ListProfAssmtRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.workload.dto.TodoDto;

@RestController
@RequestMapping("/professionalassesment")
public class ListProfAssmtController {

	@Autowired
	ListProfAssmtService listProfAssmtService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ListProfAssmtController.class);

	/**
	 * call Prof Assmt table, Narr table, Person table.
	 * 
	 * @param objListProfAssmtiDto
	 * @return
	 */
	@RequestMapping(value = "/getprofessionalassesment", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ListProfAssmtRes getProfessionalAssesment(@RequestBody ListProfAssmtReq listProfAssmtReq) {
		log.debug("Entering method  getProfessionalAssesment in ListProfAssmtController");
		ListProfAssmtRes listProfAssmtRes = new ListProfAssmtRes();
		if (TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ListProfAssmtReq.IdStage.mandatory", null, Locale.US));

		}
		if (!TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getEventIdInDto())
				&& TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getEventIdInDto().getIdEvent())) {

			throw new InvalidRequestException(
					messageSource.getMessage("ListProfAssmtReq.EventIdInDto.IdEvent.mandatory", null, Locale.US));
		}

		listProfAssmtRes = listProfAssmtService.getListProfAssmtService(listProfAssmtReq);
		log.debug("Exiting method getProfessionalAssesment in ListProfAssmtController");
		return listProfAssmtRes;
	}

	@RequestMapping(value = "/getTodoDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ListProfAssmtRes getTodoDtl(@RequestBody ListProfAssmtReq listProfAssmtReq) {
		log.debug("Entering method  getProfessionalAssesment in ListProfAssmtController");
		ListProfAssmtRes listProfAssmtRes = new ListProfAssmtRes();
		TodoDto todoDto = new TodoDto();
		if (TypeConvUtil.isNullOrEmpty(listProfAssmtReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ListProfAssmtReq.IdEvent.mandatory", null, Locale.US));

		}

		todoDto = listProfAssmtService.getTodoDtl(listProfAssmtReq.getIdEvent());
		listProfAssmtRes.setTodoDto(todoDto);
		log.debug("Exiting method getProfessionalAssesment in ListProfAssmtController");
		return listProfAssmtRes;
	}

}
