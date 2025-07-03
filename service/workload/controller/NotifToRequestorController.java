package us.tx.state.dfps.service.workload.controller;

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
import us.tx.state.dfps.service.common.request.NotifToRequestorReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.workload.service.NotifToRequestorService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Controller
 * for CCFC46S Mar 7, 2018- 11:11:11 AM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("notifrequestor")
public class NotifToRequestorController {

	@Autowired
	private NotifToRequestorService notifToRequestorService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(NotifToRequestorController.class);

	/**
	 * Method Name: getReqestor Method Description: Calls service and sends
	 * prefill data to forms
	 * 
	 * @param notifToRequestorReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getReqestor(@RequestBody NotifToRequestorReq notifToRequestorReq) {

			if (TypeConvUtil.isNullOrEmpty(notifToRequestorReq.getIdStage()))  {
				throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
			}
			return notifToRequestorService.getRequestor(notifToRequestorReq);

	}
}
