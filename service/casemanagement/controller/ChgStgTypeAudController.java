package us.tx.state.dfps.service.casemanagement.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casemanagement.service.ChgStgTypeAudService;
import us.tx.state.dfps.service.common.request.ChgStgTypeAudReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CSUB64S to
 * save change stage type Aug 22, 2017- 11:06:44 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/chgstgtypeaud")
public class ChgStgTypeAudController {

	@Autowired
	private ChgStgTypeAudService objChgStgTypeAudService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * This service will add or update the Event Table using the post event
	 * function, add or update the Stage table, and (if necessary), call the
	 * invalidate event function.
	 *
	 * @param objChgStgTypeAudiDto
	 * @return CommonStringRes
	 */
	@RequestMapping(headers = { "Accept=application/json" }, method = RequestMethod.POST, value = "/chgstgtypeaud")
	
	public CommonStringRes chgStgTypeAud(@RequestBody ChgStgTypeAudReq objChgStgTypeAudiDto) {
		CommonStringRes response = null;
		if (ObjectUtils.isEmpty(objChgStgTypeAudiDto.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(objChgStgTypeAudiDto.getCdTask())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdTask.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(objChgStgTypeAudiDto.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(objChgStgTypeAudiDto.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(objChgStgTypeAudiDto.getReqFuncCd())) {
			throw new InvalidRequestException(messageSource.getMessage("common.ReqFuncCd.mandatory ", null, Locale.US));
		}
		response = objChgStgTypeAudService.callChgStgTypeAudService(objChgStgTypeAudiDto);
		return response;
	}
}
