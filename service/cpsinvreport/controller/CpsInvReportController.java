package us.tx.state.dfps.service.cpsinvreport.controller;

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
import us.tx.state.dfps.service.common.request.FacilityInvSumReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinvreport.service.CpsInvReportService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: controller
 * class for CpsInvReport Apr 4, 2018- 4:55:46 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/cpsinvreport")
public class CpsInvReportController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	CpsInvReportService cpsInvReportService;

	private static final Logger log = Logger.getLogger(CpsInvReportController.class);

	/**
	 * 
	 * Method Description: CPSInvReport form Name: civ36o00
	 * 
	 * @param facilityInvSumReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getcpsinvreport", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getCpsInvReport(@RequestBody FacilityInvSumReq facilityInvSumReq){
		if (ObjectUtils.isEmpty(facilityInvSumReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("facilityInvSumReq.idStage.mandatory", null, Locale.US));

		;
		log.info("TransactionId :" + facilityInvSumReq.getTransactionId());

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(cpsInvReportService.getCPSInvReport(facilityInvSumReq)));
		return commonFormRes;
	}

}
