package us.tx.state.dfps.service.investigation.controller;

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
import us.tx.state.dfps.service.investigation.service.FacilityInvRepService;
import us.tx.state.dfps.service.investigation.service.FacilityInvSumService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: controller
 * for populating form CFIV1300 Mar 20, 2018- 1:32:45 PM © 2017 Texas Department
 * of Family and Protective Services
 */

@RestController
@RequestMapping("/finvsum")
public class FacilityInvSumController {

	@Autowired
	FacilityInvSumService facilityInvSumService;

	@Autowired
	FacilityInvRepService facilityInvRepService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("ServiceBusiness-FacilityInvSumControllerLog");

	/**
	 * 
	 * Method Description: Populates PROVIDER INVESTIGATION SUMMARY REPORT form
	 * Name: cfiv1300
	 * 
	 * @param facilityInvSumReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getfacilityinvsum", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getfacilityInvSum(@RequestBody FacilityInvSumReq facilityInvSumReq) {
		if (ObjectUtils.isEmpty(facilityInvSumReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("facilityInvSumReq.idStage.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(facilityInvSumService.getFacilityInvSumReport(facilityInvSumReq)));
		log.info("TransactionId :" + facilityInvSumReq.getTransactionId());

		return commonFormRes;
	}

	/**
	 * 
	 * Method Description:Populates form cfiv1500, which Populates the APS
	 * ICF-MR FACILITY INVESTIGATIVE REPORT aka 5-DAY STATUS REPORT.
	 * 
	 * @param facilityInvSumReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getfacilityinvrep", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getfacilityInvRep(@RequestBody FacilityInvSumReq facilityInvSumReq) {
		if (ObjectUtils.isEmpty(facilityInvSumReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("facilityInvSumReq.idStage.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(facilityInvRepService.getFacilityInvReport(facilityInvSumReq)));
		log.info("TransactionId :" + facilityInvSumReq.getTransactionId());

		return commonFormRes;
	}

}
