package us.tx.state.dfps.service.financial.service;

import us.tx.state.dfps.service.common.request.ServiceAuthorizationHeaderReq;
import us.tx.state.dfps.service.common.response.ServiceAuthorizationHeaderRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Service
 * interface which will be implemented by serviceAuthorizationHeaserServiceImpl>
 * June 27, 2018- 3:05:39 PM © 2017 Texas Department of Family and Protective
 * Services.
 */
public interface ServiceAuthorizationHeaderService {

	/**
	 * Method name: retrieveServiceAuthHeaderInfo Method Description: This
	 * method is used to get Service Authorization Header information.
	 * 
	 * @param serviceAuthorizationHeaderReq
	 * @return
	 */
	public ServiceAuthorizationHeaderRes retrieveServiceAuthHeaderInfo(
			ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq);

	/**
	 * Method Name: saveServiceAuthHeader Method Description: This service
	 * performs Service Authorization header save functionality as well as the
	 * creation and modification of events, Approval Invalidation and ToDo
	 * creation.
	 * 
	 * @param serviceAuthorizationHeaderReq
	 * @return
	 */
	public ServiceAuthorizationHeaderRes saveServiceAuthHeader(
			ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq);

	/**
	 * Method Name: validateContractForResource Method Description: This service
	 * serves to determine whether a contract for a particular resource is
	 * valid, and if so, to retrieve certain information for that contract.
	 * 
	 * @param serviceAuthorizationHeaderReq
	 * @return
	 */
	public ServiceAuthorizationHeaderRes validateContractForResource(
			ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq);

}
