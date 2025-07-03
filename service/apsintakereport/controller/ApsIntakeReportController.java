package us.tx.state.dfps.service.apsintakereport.controller;

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
import us.tx.state.dfps.service.apsintakereport.service.ApsIntakeReportService;
import us.tx.state.dfps.service.common.request.ApsIntakeReportReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Sends input
 * data to service Mar 16, 2018- 3:46:55 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@RestController
@RequestMapping("apsintake")
public class ApsIntakeReportController {

	@Autowired
	private ApsIntakeReportService apsIntakeReportService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ApsIntakeReportController.class);

	@RequestMapping(value = "/getreportccl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getReportData(@RequestBody ApsIntakeReportReq apsIntakeReportReq) {
		log.debug("Entering method CINT38S getReportData in ApsIntakeReportController");
		if (TypeConvUtil.isNullOrEmpty(apsIntakeReportReq)) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(apsIntakeReportService.getIntakeReport(apsIntakeReportReq)));

		return commonFormRes;
	}

	@RequestMapping(value = "/getreportfac", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getReportDataFac(@RequestBody ApsIntakeReportReq apsIntakeReportReq) {
		log.debug("Entering method CINT41S getReportDataFac in ApsIntakeReportController");
		if (TypeConvUtil.isNullOrEmpty(apsIntakeReportReq)) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(apsIntakeReportService.getIntakeReportFac(apsIntakeReportReq)));

		return commonFormRes;
	}

	@RequestMapping(value = "/getreportnorep", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getReportDataNoRep(@RequestBody ApsIntakeReportReq apsIntakeReportReq) {
		log.debug("Entering method CINT45S getReportDataNoRep in ApsIntakeReportController");
		if (TypeConvUtil.isNullOrEmpty(apsIntakeReportReq)) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(apsIntakeReportService.getIntakeReportNoRep(apsIntakeReportReq)));

		return commonFormRes;
	}

}
