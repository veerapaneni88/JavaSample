/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jan 18, 2018- 5:19:28 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.controller;

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
import us.tx.state.dfps.service.common.request.SubcareLOCFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.service.SubcareLOCFormService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 18, 2018- 5:19:28 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/SubcareLOCForm")
public class SubcareLOCFormController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	SubcareLOCFormService subcareLOCFormService;

	private static final Logger log = Logger.getLogger(SubcareLOCFormController.class);

	/**
	 * 
	 * Method Name: getSubLOCAuthReqForm Method Description: The controller for
	 * CSUB44S that call the service
	 * 
	 * @param subcareLOCFromReq
	 * @return
	 */
	@RequestMapping(value = "/getSubLOCAuthReqForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getSubLOCAuthReqForm(@RequestBody SubcareLOCFormReq subcareLOCFromReq) {

		if (TypeConvUtil.isNullOrEmpty(subcareLOCFromReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(subcareLOCFromReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(subcareLOCFormService.getSubcareLOCAuthReqReport(subcareLOCFromReq)));
		log.info("TransactionId :" + subcareLOCFromReq.getTransactionId());
		return commonFormRes;
	}
}
