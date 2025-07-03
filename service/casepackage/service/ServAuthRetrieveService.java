package us.tx.state.dfps.service.casepackage.service;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.ServAuthRetrieveDto;
import us.tx.state.dfps.service.common.request.ServAuthRetrieveReq;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: ServAuthRetrieve
 * Service Class Description: This class is used for retrieving ServAuthRetrieve
 * List Apr 03, 2017 - 5:19:51 PM
 */

public interface ServAuthRetrieveService {

	/**
	 * 
	 * Method Description: This Method will retrieval the Service Authorization
	 * APS Detail window based on the input request. Service Name : CCON24S
	 * 
	 * @param servAuthRetrieveReq
	 * @return List<ServAuthRetrieveDto>
	 * @,DataNotFoundException
	 */

	public List<ServAuthRetrieveDto> getAuthDetails(ServAuthRetrieveReq servAuthRetrieveReq);

}