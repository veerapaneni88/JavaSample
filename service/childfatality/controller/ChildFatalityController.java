package us.tx.state.dfps.service.childfatality.controller;

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
import us.tx.state.dfps.service.childfatality.service.ChildFatalityService;
import us.tx.state.dfps.service.common.request.ChildFatalityReq;
import us.tx.state.dfps.service.common.response.ChildFatalityRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

@RestController
@RequestMapping("/childFatality")
public class ChildFatalityController {

	@Autowired
	ChildFatalityService childFatalityService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ChildFatalityController.class);

	/**
	 * Search child.
	 *
	 * @param childFatalityReq
	 *            the child fatality req
	 * @return the child fatality res
	 */
	@RequestMapping(value = "/searchChild", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ChildFatalityRes searchChild(@RequestBody ChildFatalityReq childFatalityReq) {
		log.info("TransactionId :" + childFatalityReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(childFatalityReq.getChildFatalityDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.inputcheck.data", null, Locale.US));
		}
		ChildFatalityRes childFatalityRes = childFatalityService.searchChild(childFatalityReq);

		if (childFatalityRes != null && childFatalityRes.isRecordsAvailable()) {
			log.info("childFatalityRes: " + childFatalityRes.getPersonIds().size());
			return childFatalityRes;
		} else {
			throw new DataNotFoundException(messageSource.getMessage("childfatality.search.data", null, Locale.US));
		}
	}
}
