package us.tx.state.dfps.service.populateform.controller;

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
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.populateform.service.PopulateFormService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this
 * controller will send request to populateFormService class request object to
 * populateForm service> Jan 17, 2018- 3:58:09 PM © 2017 Texas Department of
 * Family and Protective Services
 *
 */

@RestController
@RequestMapping("/populateForm")
public class PopulateFormController {

	@Autowired
	PopulateFormService populateFormService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(PopulateFormController.class);

	/**
	 * TUX service: CINV38S / CFIV3300 Gets the populate form.
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return the populate form
	 */
	@RequestMapping(value = "/getPopulateForm", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getPopulateForm(@RequestBody PopulateFormReq populateFormReq) {
		if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("populateForm.idStage.mandatory", null, Locale.US));
		}
		;
		if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("populateForm.IdPerson.mandatory", null, Locale.US));
		}
		;
		if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("populateForm.IdCase.mandatory", null, Locale.US));
		}
		;
		// CommonFormRes
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(populateFormService.getFormsPopulated(populateFormReq)));
		log.debug(TypeConvUtil.getXMLFormat(populateFormService.getFormsPopulated(populateFormReq)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());
		return commonFormRes;
	}
}
