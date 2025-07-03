/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 10, 2018- 4:07:00 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.workload.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.RCCPWorkloadReq;
import us.tx.state.dfps.service.common.response.RCCPWorkloadRes;
import us.tx.state.dfps.service.workload.service.ExternalWorkloadService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 10, 2018- 4:07:00 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/extWorkload")
public class ExternalWorkloadController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ExternalWorkloadService externalWorkloadService;

	/**
	 * 
	 * Method Name: getAssignWorkloadDtls Method Description:
	 * 
	 * @param rccpWorkloadReq
	 * @return RCCPWorkloadRes
	 */
	@RequestMapping(value = "/getExtWorkloadDtls", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RCCPWorkloadRes getExtWorkloadDtls(@RequestBody RCCPWorkloadReq rccpWorkloadReq) {
		if (ObjectUtils.isEmpty(rccpWorkloadReq.getRccpWorkLoadDto().getIdUser())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		rccpWorkloadReq.setRccpWorkLoadDto(rccpWorkloadReq.getRccpWorkLoadDto());

		return externalWorkloadService.getExternalWorkloadDetails(rccpWorkloadReq);
	}

	/**
	 * 
	 * Method Name: getStageDetails Method Description:
	 * 
	 * @param rccpWorkloadReq
	 * @return RCCPWorkloadRes
	 */
	@RequestMapping(value = "/getStageDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RCCPWorkloadRes getStageDetails(@RequestBody RCCPWorkloadReq rccpWorkloadReq) {
		if (ObjectUtils.isEmpty(rccpWorkloadReq.getRccpWorkLoadDto().getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return externalWorkloadService.getStageDetails(rccpWorkloadReq);
	}

	/**
	 * 
	 * Method Name: searchWorkload Method Description:
	 * 
	 * @param rccpWorkloadReq
	 * @return RCCPWorkloadRes
	 */
	@RequestMapping(value = "/searchWorkload", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RCCPWorkloadRes searchWorkload(@RequestBody RCCPWorkloadReq rccpWorkloadReq) {
		if (ObjectUtils.isEmpty(rccpWorkloadReq.getRccpWorkLoadDto().getIdUser())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return externalWorkloadService.searchWorkload(rccpWorkloadReq);
	}

}
