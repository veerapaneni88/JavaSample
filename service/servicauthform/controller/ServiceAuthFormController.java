package us.tx.state.dfps.service.servicauthform.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.servicauthform.service.ServiceAuthFormService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Authorization form used by CPS to refer clients for paid services under PRS
 * contracts. Mar 1, 2018- 1:31:27 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("serviceauthform")
public class ServiceAuthFormController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ServiceAuthFormService serviceAuthFormService;

	private static final Logger log = Logger.getLogger(ServiceAuthFormController.class);

	/**
	 * 
	 * Method Name: getServiceAuthFormData Method Description: Request to get
	 * the Service Auth Form Data in prefill data format.
	 * 
	 * @param commonHelperReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getserviceauthdata", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getServiceAuthFormData(@RequestBody CommonHelperReq commonHelperReq) {

		CommonFormRes commonFormRes = new CommonFormRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(serviceAuthFormService.getServiceAuthFormData(commonHelperReq)));

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return commonFormRes;
	}

}
