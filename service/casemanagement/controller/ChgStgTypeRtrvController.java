package us.tx.state.dfps.service.casemanagement.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casemanagement.dto.ChgStgTypeRtrvoDto;
import us.tx.state.dfps.service.casemanagement.service.ChgStgTypeRtrvService;
import us.tx.state.dfps.service.common.request.ChgStgTypeRtrvReq;
import us.tx.state.dfps.service.common.response.ChgStgTypeRtrvRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB63S Class
 * Description: ChgStgTypeRtrvController will have all operation which are
 * mapped to ChgStgType retrieve. Aug 4, 2017 - 3:18:29 PM
 */
@RestController
@RequestMapping("/chgstgtype")
public class ChgStgTypeRtrvController {

	@Autowired
	private ChgStgTypeRtrvService chgStgTypeRtrvService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * This service will receive IdStage and IdEvent and retrieve CdEventType,
	 * IdPerson, CdEventStatus, CdStageType, CdTask, dtEventOccurred from the
	 * Stage and Event table.
	 *
	 * @param objChgStgTypeRtrviDto
	 * @return responseDto
	 */
	@RequestMapping(headers = { "Accept=application/json" }, method = RequestMethod.POST, value = "/chgstgtypertrv")
	
	public ChgStgTypeRtrvRes chgStgTypeRtrv(@RequestBody ChgStgTypeRtrvReq objChgStgTypeRtrviDto) {
		ChgStgTypeRtrvRes responseDto = new ChgStgTypeRtrvRes();
		if (TypeConvUtil.isNullOrEmpty(objChgStgTypeRtrviDto.getIdEvent())
				&& TypeConvUtil.isNullOrEmpty(objChgStgTypeRtrviDto.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		List<ChgStgTypeRtrvoDto> response = chgStgTypeRtrvService.callChgStgTypeRtrvService(objChgStgTypeRtrviDto);
		if (response != null) {
			responseDto.setResponse(response);
		}
		return responseDto;
	}
}
