package us.tx.state.dfps.service.admin.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.admin.dto.SubContractorsDto;
import us.tx.state.dfps.service.admin.dto.SubContractorsReq;
import us.tx.state.dfps.service.admin.service.SubContractorsService;
import us.tx.state.dfps.service.common.response.SubContractorsRes;
import us.tx.state.dfps.service.exception.ServiceLayerException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * SubContractorsServiceController Feb 9, 2018- 4:56:12 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/subContractors")
public class SubContractorsServiceController {

	@Autowired
	SubContractorsService subContractorsService;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: getSubContractorsDetails Method Description: This method is
	 * used to retrieve a list of subcontractors of a resource, or homes into
	 * which an agency can place clients.
	 * 
	 * @param subContractorsReq
	 * @ @return SubContractorsRes
	 */
	@RequestMapping(value = "/getSubContractorDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SubContractorsRes getSubContractorsDetails(@RequestBody SubContractorsReq subContractorsReq) {

		if (ObjectUtils.isEmpty(subContractorsReq.getIdResource())) {
			throw new ServiceLayerException(
					messageSource.getMessage("SubContractorsReq.idResource.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(subContractorsReq.getCdRsrcLinkType())) {
			throw new ServiceLayerException(
					messageSource.getMessage("SubContractorsReq.cdRsrcLinkType.mandatory", null, Locale.US));
		}
		List<SubContractorsDto> subContractorsDtoList = subContractorsService
				.getSubContractorsDtoList(subContractorsReq);
		SubContractorsRes subContractorsRes = new SubContractorsRes();
		subContractorsRes.setSubContractorsList(subContractorsDtoList);

		return subContractorsRes;
	}
}
