package us.tx.state.dfps.service.casepackage.dao;

import java.util.LinkedHashMap;

import us.tx.state.dfps.service.common.request.CFMgmntReq;
import us.tx.state.dfps.service.common.response.CFMgmntRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CFMgnmtListDao Sep 6, 2017- 2:54:36 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface CFMgnmtListDao {

	/**
	 * 
	 * Method Name: getCFMgmntInfo Method Description: Get CF Management
	 * information
	 * 
	 * @param searchReq
	 * @return CFMgmntDto @
	 */
	public CFMgmntRes getCFMgmntInfo(CFMgmntReq searchReq);

	/**
	 * 
	 * Method Name: getSkpTrnInfo Method Description: Get skp Transaction
	 * information
	 * 
	 * @param cfMgmntReq
	 * @return LinkedHashMap @
	 */
	public LinkedHashMap getSkpTrnInfo(CFMgmntReq cfMgmntReq);

}
