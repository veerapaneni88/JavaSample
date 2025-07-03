package us.tx.state.dfps.service.sslccommchklst.controller;

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
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SslcCommChklstReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.sslccommchklst.service.SslcCommChklstService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Sends input
 * data to service and gets back prefill data Mar 15, 2018- 5:09:19 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/sslccommencement")
public class SslcCommChklstController {

	@Autowired
	private SslcCommChklstService sslcCommChklstService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(SslcCommChklstController.class);

	/**
	 * 
	 * Method Name: getChecklistData CINV89S Method Description: cfiv4400 SSLC
	 * 24-Hour Commencement Checklist form
	 * 
	 * @param sslcCommChklstReq
	 * @return
	 */
	@RequestMapping(value = "/getchecklist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getChecklistData(@RequestBody SslcCommChklstReq sslcCommChklstReq) {
		log.debug("Entering method CINV89S getChecklistData in SslcCommChklstController");
		if (TypeConvUtil.isNullOrEmpty(sslcCommChklstReq)) {
			throw new InvalidRequestException(messageSource.getMessage("ppm.stageId.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil
				.getXMLFormat(sslcCommChklstService.getChecklistData(sslcCommChklstReq, ServiceConstants.FALSEVAL)));

		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: getReferralData CINV89S Method Description: cfiv2300 Adult
	 * Protective Services Referral Form
	 * 
	 * @param sslcCommChklstReq
	 * @return
	 */
	@RequestMapping(value = "/getreferral", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getReferralData(@RequestBody SslcCommChklstReq sslcCommChklstReq) {
		log.debug("Entering method CINV89S getReferralData in SslcCommChklstController");
		if (TypeConvUtil.isNullOrEmpty(sslcCommChklstReq)) {
			throw new InvalidRequestException(messageSource.getMessage("ppm.stageId.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil
				.getXMLFormat(sslcCommChklstService.getChecklistData(sslcCommChklstReq, ServiceConstants.TRUEVAL)));

		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: getApsReferralData CINV89S Method Description: cfiv4300
	 * Adult Protective Services Referral Form
	 * 
	 * @param sslcCommChklstReq
	 * @return
	 */
	@RequestMapping(value = "/getapsreferral", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getApsReferralData(@RequestBody SslcCommChklstReq sslcCommChklstReq) {
		log.debug("Entering method CINV89S getReferralData in SslcCommChklstController");
		if (TypeConvUtil.isNullOrEmpty(sslcCommChklstReq)) {
			throw new InvalidRequestException(messageSource.getMessage("ppm.stageId.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(sslcCommChklstService.getApsReferralData(sslcCommChklstReq)));

		return commonFormRes;
	}
}
