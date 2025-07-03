package us.tx.state.dfps.service.investigation.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.LicensingInvCnclusnReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.service.CCIReporterLetterFormService;

import java.util.Locale;

/**
 * IMPACT PHASE 2 MODERNIZATION Class Description: This service is used to
 * launch the Medical Consenter forms. Oct 30, 2017- 5:11:02 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/cciReporterLetterForm")
public class CCIReporterLetterFormController {
	private static final Logger log = Logger.getLogger(CCIReporterLetterFormController.class);

	@Autowired
	private CCIReporterLetterFormService licensingInvFormService;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/getPrefillData", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getLicensingInvOperationInfo(
			@RequestBody LicensingInvCnclusnReq licensingInvCnclusnReq) {

		if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("licensingInvCnclusnReq.idStage.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getIdPerson()))
			throw new InvalidRequestException(
					messageSource.getMessage("licensingInvCnclusnReq.idReporter.mandatory", null, Locale.US));

		CommonFormRes returnValue = new CommonFormRes();
		returnValue.setPreFillData(TypeConvUtil.getXMLFormat(licensingInvFormService.retrieveOperationIdentity(licensingInvCnclusnReq)));
		return returnValue;
	}
}
