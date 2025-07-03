package us.tx.state.dfps.service.cpsintakereport.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CpsIntakeReportReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsintakereport.service.CpsIntakeReportService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CpsIntakeReportController will have all operation which are
 * mapped to CpsIntakeReport module Feb 9, 2018- 1:58:51 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/cpsintakereport")
public class CpsIntakeReportController {

	@Autowired
	CpsIntakeReportService cpsIntakeReportService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(CpsIntakeReportController.class);

	/**
	 * 
	 *Method Name:	getCpsIntakeReport
	 *Method Description: This method is used to get the detail of intake report.
	 *@param cpsIntakeReportReq
	 *@return
	 */
	@RequestMapping(value = "/getcpsintreport", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getCpsIntakeReport(@RequestBody CpsIntakeReportReq cpsIntakeReportReq) {

		CommonFormRes commonFormRes = new CommonFormRes();
		if (TypeConvUtil.isNullOrEmpty(cpsIntakeReportReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(cpsIntakeReportService.getCpsIntakeReport(cpsIntakeReportReq)));

		log.info("TransactionId :" + cpsIntakeReportReq.getTransactionId());
		return commonFormRes;
	}

}
