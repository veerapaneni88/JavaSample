package us.tx.state.dfps.service.common.phoneticsearch.IIRService;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.PhoneticSearchReq;
import us.tx.state.dfps.service.common.response.PhoneticSearchRes;

public interface PhoneticSearchService {

	/**
	 * Method Description: This method is used to performs search operations for
	 * staff and person and retrieval of results based on the request Service
	 * Name: Phonetic Search
	 * 
	 * @param phoneticSearchReq
	 * @param searchFilter
	 * @return PhoneticSearchRes
	 * @throws InvalidRequestException
	 * @
	 */
	public PhoneticSearchRes phoneticSearch(PhoneticSearchReq phoneticSearchReq);
}
