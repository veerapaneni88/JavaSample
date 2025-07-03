package us.tx.state.dfps.service.placement.controller;

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
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.DocumentTemplateDto;
import us.tx.state.dfps.service.forms.dto.DocumentTmpltCheckDto;
import us.tx.state.dfps.service.forms.service.FormsService;
import us.tx.state.dfps.service.placement.service.CommonApplicationService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CommonApplicationController will have all operation which are
 * mapped to Placement module. Feb 9, 2018- 2:13:13 PM © 2017 Texas Department
 * of Family and Protective Services
 *  * **********  Change History *********************************
 * 10/21/2019 thompswa artf128758 : FCL Project - Add getPlacementApplication method boolean parameter
 * 11/25/2019 thompswa artf130779 : pass the R2 template ID to the CommonApplicationServiceImpl
 */
@RestController
@RequestMapping("/commonApplication")
public class CommonApplicationController {
	@Autowired
	CommonApplicationService commonApplicationService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	FormsService formsService;

	private static final Logger logger = Logger.getLogger(CommonApplicationController.class);

	/**
	 * Tuxedo Service Name: CSUB72S. Method Description: This Service is used to
	 * retrieve the common application form. This form fully documents the
	 * historical social, emotional, educational, medical, and family account of
	 * the child by passing IdStage and IdPerson as input request
	 * 
	 * @param commonApplicationReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getCommonApplicationForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getCommonApplicationForm(@RequestBody CommonApplicationReq commonApplicationReq) {
		if (ObjectUtils.isEmpty(commonApplicationReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		CommonFormRes commonFormRes = new CommonFormRes();
		// Launch the legacy or R2 Impact form by checking the template version  of the Form.
		if (!ObjectUtils.isEmpty(commonApplicationReq.getIdEvent())) {
			DocumentTmpltCheckDto documentTmpltCheckDto = new DocumentTmpltCheckDto();
			documentTmpltCheckDto.setTableName(ServiceConstants.COMMONAPP_TABLE_NAME);
			documentTmpltCheckDto.setNarrEventId(commonApplicationReq.getIdEvent());
			DocumentTemplateDto documentTemplateDto = formsService.documentTemplateCheck(documentTmpltCheckDto);
			if (!ObjectUtils.isEmpty(documentTemplateDto) && !ObjectUtils.isEmpty(documentTemplateDto.getIdTemplate())) {
				commonApplicationReq.setIdTemplate(documentTemplateDto.getIdTemplate());                 // artf130779
		    }
			if (!ObjectUtils.isEmpty(documentTemplateDto) && !ObjectUtils.isEmpty(documentTemplateDto.getIndLgcy())
					&& ServiceConstants.YES.equalsIgnoreCase(documentTemplateDto.getIndLgcy())) {
				// This service call will populate the prefill data for Legacy form
				commonFormRes.setPreFillData(TypeConvUtil
						.getXMLFormat(commonApplicationService.getCommonApplicationForm(commonApplicationReq)));
			} else {
				// This service call will populate the prefill data for R2 Impact form
				commonFormRes.setPreFillData(TypeConvUtil
						.getXMLFormat(commonApplicationService.getPlacementApplication(commonApplicationReq, true)));
			}
		} else {
			// This service call will populate the prefill data for R2 Impact form
			commonFormRes.setPreFillData(
					TypeConvUtil.getXMLFormat(commonApplicationService.getPlacementApplication(commonApplicationReq, false)));
		}
		logger.info("TransactionId :" + commonApplicationReq.getTransactionId());
		return commonFormRes;
	}
	
	/**
	 * Method Description: This Service is used to
	 * retrieve the common application form status and CSA latest details.
	 * 
	 * @param commonApplicationReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getCommonAppStatusAndCSADtls", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getCommonAppStatusAndCSADtls(@RequestBody CommonApplicationReq commonApplicationReq) {
		if (ObjectUtils.isEmpty(commonApplicationReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(commonApplicationReq.getIdEvent()))
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));		
		return commonApplicationService.getCommonAppStatusAndCSADtls(commonApplicationReq);
	}
}
