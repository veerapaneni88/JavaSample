package us.tx.state.dfps.service.pcaeligibilitysummary.controller;

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
import us.tx.state.dfps.service.common.request.PcaEligibilityAlconReq;
import us.tx.state.dfps.service.common.response.PcaEligibilityFetchRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.pcaeligibilitysummary.service.PcaEligibilitySummaryService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Session
 * Bean has methods to create new Eligibility Summary, display existing
 * Eligibility Summary and display Summary Event List. Oct 17, 2017- 3:40:46 PM
 * © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/PcaEligibilitySummary")
public class PcaEligibilitySummaryController {

	@Autowired
	private PcaEligibilitySummaryService pcaEligibilitySummaryService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-PcaEligibilitySummaryControllerLog");

	/**
	 * Method Name: fetchActivePcaList Method Description: Get Person Active PCA
	 * Eligibilities for input person id
	 * 
	 * @param pcaEligibilityAlconReq
	 * @return PcaEligibilityFetchRes
	 * 
	 */
	@RequestMapping(value = "/fetchActivePcaList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PcaEligibilityFetchRes fetchActivePcaList(
			@RequestBody PcaEligibilityAlconReq pcaEligibilityAlconReq){
		LOG.debug("Entering method fetchActivePcaList in PcaEligibilitySummaryController");
		if (TypeConvUtil.isNullOrEmpty(pcaEligibilityAlconReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("pca.personId.mandatory", null, Locale.US));
		}
		PcaEligibilityFetchRes eligibilityFetchRes = new PcaEligibilityFetchRes();
		eligibilityFetchRes.setTransactionId(pcaEligibilityAlconReq.getTransactionId());
		LOG.info("TransactionId :" + pcaEligibilityAlconReq.getTransactionId());
		eligibilityFetchRes.setPcaEligSummaryValueBeanDtoList(
				pcaEligibilitySummaryService.fetchActivePcaList(pcaEligibilityAlconReq.getPersonId()));
		LOG.debug("Exiting method fetchActivePcaList in PcaEligibilitySummaryController");
		return eligibilityFetchRes;
	}
}
