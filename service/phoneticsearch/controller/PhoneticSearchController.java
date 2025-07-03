package us.tx.state.dfps.service.phoneticsearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.phoneticsearch.IIRService.PhoneticSearchService;
import us.tx.state.dfps.service.common.request.PhoneticSearchReq;
import us.tx.state.dfps.service.common.response.PhoneticSearchRes;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Phonetic Search
 * Class Description: PhoneticSearchController will have all operation which are
 * mapped to phonetic search module. May 11, 2017 - 2:18:29 PM
 */
@Api(tags = { "personsearch" })
@RestController
@RequestMapping("/phonetic")
public class PhoneticSearchController {

	@Autowired
	PhoneticSearchService phoneticSearchService;

	/**
	 * Method Description: This method is used to performs search operations for
	 * staff and person and retrieval of results based on the request Service
	 * Name: Phonetic Search
	 * 
	 * @param phoneticSearchReq
	 * @param searchFilter
	 * @return PhoneticSearchRes
	 */
	@ApiOperation(value = "Person list for phonetic search", tags = { "personsearch" })
	@RequestMapping(value = "/phoneticsearch", headers = {
			"Accept=application/json;charset=windows-1252" }, method = RequestMethod.POST)
	public  PhoneticSearchRes getPhoneticSearchDtls(@RequestBody PhoneticSearchReq phoneticSearchReq) {

		// log.info("TransactionId :" + searchUnitListReq.getTransactionId());
		return phoneticSearchService.phoneticSearch(phoneticSearchReq);
	}

}
