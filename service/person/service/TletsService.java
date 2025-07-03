package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.common.request.TletsReq;
import us.tx.state.dfps.service.common.response.TletsRes;

public interface TletsService {

	/**
	 * Method Description: This Method will retrieve information for populating
	 * Texas Law Enforcement Telecommunications System (TLETS) List window.
	 * Service Name : TLETS List
	 * 
	 * @param tletsReq
	 * @return @
	 * 
	 */
	public TletsRes getTletsList(TletsReq tletsReq);

	/**
	 * Method Description: This Method will perform Retrieve, Add and Update on
	 * populating Texas Law Enforcement Telecommunications System (TLETS) Check
	 * window. Service Name : TLETS Check
	 * 
	 * @param tletsReq
	 * @return @
	 * 
	 */
	public TletsRes audTletsDetails(TletsReq tletsReq);

	/**
	 * Method Description: This Method will retrieve information for populating
	 * Texas Law Enforcement Telecommunications System (TLETS) check window.
	 * Service Name : TLETS check
	 * 
	 * @param tletsReq
	 * @return @
	 * 
	 */

	public TletsRes getTletsCheckDtl(TletsReq tletsReq);

}
