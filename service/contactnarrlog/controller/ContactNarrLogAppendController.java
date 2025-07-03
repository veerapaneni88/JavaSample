package us.tx.state.dfps.service.contactnarrlog.controller;

import java.util.Date;
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
import us.tx.state.dfps.service.common.request.CpsIntakeReportReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contactnarrlog.Service.ContactNarrLogAppendService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactNarrLogAppendController for service CSVC06S Feb 14, 2018-
 * 3:04:08 PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/contactnarrlog")
public class ContactNarrLogAppendController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ContactNarrLogAppendService contactService;

	private static final Logger log = Logger.getLogger(ContactNarrLogAppendController.class);

	@RequestMapping(value = "/getcontactnarrlogdetails", headers = {
	"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getContactNarrLogDetails(@RequestBody CpsIntakeReportReq cpsIntakeReportReq){
		CommonFormRes commonFormRes = new CommonFormRes();
		if (TypeConvUtil.isNullOrEmpty(cpsIntakeReportReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		PreFillDataServiceDto prefillDto = contactService.getContactNarrLogDetails(cpsIntakeReportReq);

		if (ObjectUtils.isEmpty(prefillDto)) {
			commonFormRes.setPreFillData("");
		} else {
			commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(prefillDto));
		}

		log.info("TransactionId :" + cpsIntakeReportReq.getTransactionId());

		return commonFormRes;
	}
}
