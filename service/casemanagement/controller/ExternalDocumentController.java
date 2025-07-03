/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 11, 2017- 3:36:20 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casemanagement.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casemanagement.service.ExternalDocumentService;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ExternalDocumentationAUDReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.ExternalDocumentationRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ExternalDocumentController Aug 11, 2017- 3:36:20 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/externaldocumentation")
public class ExternalDocumentController {

	private static final Logger log = Logger.getLogger(ExternalDocumentController.class);

	@Autowired
	ExternalDocumentService externalDocumentService;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: externaldocumentationAUD Method Description:The method
	 * updates the event and calls the method, which performs adds, updates, and
	 * deletes on the EXT_DOCUMENTATION table.Cinv22s.callCINV22S
	 * 
	 * @param commonHelperReq
	 * @returnCommonHelperRes
	 */
	@RequestMapping(value = "/externaldocumentationAUD", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes externaldocumentationAUD(
			@RequestBody ExternalDocumentationAUDReq externalDocumentationAUDReq) {
		if (TypeConvUtil.isNullOrEmpty(externalDocumentationAUDReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		Long rowId = externalDocumentService.externaldocumentationAUD(externalDocumentationAUDReq);
		CommonHelperRes response = new CommonHelperRes();
		response.setRowId(rowId);
		log.info("TransactionId :" + response.getTransactionId());
		return response;
	}

	/**
	 * Method Name: fetchExternaldocumentation Method Description: The method
	 * fetches External document information
	 * 
	 * @param commonHelperReq
	 * @return ExternalDocumentationRes
	 */
	@RequestMapping(value = "/getExternaldocumentation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ExternalDocumentationRes fetchExternaldocumentation(
			@RequestBody CommonHelperReq commonHelperReq) {
		ExternalDocumentationRes response = externalDocumentService.fetchExternaldocumentation(commonHelperReq);
		return response;
	}
}
