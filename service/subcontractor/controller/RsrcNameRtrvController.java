package us.tx.state.dfps.service.subcontractor.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.RsrcNameReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.subcontractor.service.RsrcNameRtrvService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CRES17S
 * service This service is used to get the resource name based on the resource
 * id Jan 19, 2018- 11:45:24 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@RestController
@RequestMapping("/resource")
public class RsrcNameRtrvController {

	@Autowired
	RsrcNameRtrvService rsrcNameRtrvService;

	@Autowired
	MessageSource messageSource;

	public static final String uIdRsrcLinkChild_STRING = "uIdRsrcLinkChild";

	public static final int uIdRsrcLinkChild_IND = 13;

	/**
	 * Method Name: returnResourceNameFromResourceID
	 * 
	 * Method Description: This method returns Resource Name if the Resource ID
	 * that it is passed exists in Resource Directory. Service name:CCON17S
	 * 
	 * @param objRsrcNameRtrviDto
	 * @return Cres04doDto
	 * 
	 */
	@RequestMapping(value = "/name", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonStringRes getRsrcName(@RequestBody RsrcNameReq rsrcNameReq) {
		if (TypeConvUtil.isNullOrEmpty(rsrcNameReq.getIdRsrcLinkChild())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		return rsrcNameRtrvService.getRsrcName(rsrcNameReq);
	}
}
