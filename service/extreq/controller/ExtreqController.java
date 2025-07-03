package us.tx.state.dfps.service.extreq.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.ExtreqReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.extreq.service.ExtreqService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CSVC27s
 * Extension Request Mar 15, 2018- 12:13:35 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@RestController
@RequestMapping("/extreq")
public class ExtreqController {

	@Autowired
	ExtreqService extreqService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("ServiceBusiness-ExtreqControllerLog");

	/**
	 * 
	 * Method Description: Populates Extension Request form Name: extreq
	 * 
	 * @param extReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getextreq", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getAFIStatement(@RequestBody ExtreqReq extreqReq) {
		if (ObjectUtils.isEmpty(extreqReq.getIdEvent()))
			throw new InvalidRequestException(messageSource.getMessage("extreqReq.IdEvent.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(extreqService.getExtreq(extreqReq)));
		log.info("TransactionId :" + extreqReq.getTransactionId());

		return commonFormRes;
	}

}
