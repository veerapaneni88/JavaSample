package us.tx.state.dfps.service.conservatorship.service;

import java.text.ParseException;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CnsrvtrshpRemovalAlertReq;
import us.tx.state.dfps.service.common.request.CnsrvtrshpRemovalReq;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.CnsrvtrshpRemovalAlertRes;
import us.tx.state.dfps.service.common.response.CnsrvtrshpRemovalRes;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CSUB14S Class
 * Description:ConservatorshipService class May 1, 2017 - 4:40:18 PM
 */

public interface ConservatorshipService {

	/**
	 * Method Description: This is the retrieve service used by Conservatorship
	 * Removal to retrieve information regarding the Removal Event. It retrieves
	 * the detail for the event, as well as the removal reason, child removal
	 * characteristics, and adult removal characteristics. Service Name: CSUB14S
	 * 
	 * @param cnsrvtrshpRemovalReq
	 * @return CnsrvtrshpRemovalRes
	 * @ @throws
	 *       ParseException
	 */
	public CnsrvtrshpRemovalRes getRmvlEventDtls(CnsrvtrshpRemovalReq cnsrvtrshpRemovalReq);

	/**
	 * Method Name: updateIdRmvlGroup Method Description: This method will
	 * update group Id for the removed children
	 * 
	 * @param CommonEventIdReq
	 * @return CommonHelperRes @
	 */
	public CommonHelperRes updateIdRmvlGroup(CommonEventIdReq eventIdList);

	/**
	 * Method Name: CVSRemovalAlert Method Description: This method will trigger
	 * alert in “CVS Removal” page
	 * 
	 * @param cnsrvtrshpRemovalAlertReq
	 * @return @
	 */
	public CnsrvtrshpRemovalAlertRes getAlertForCVSRemoval(CnsrvtrshpRemovalAlertReq cnsrvtrshpRemovalAlertReq);

	/**
	 * 
	 * Method Name: babyMosesRemovalReasonExists Method Description:
	 * 
	 * @param commonHelperReq
	 * @return @
	 */
	public CommonBooleanRes babyMosesRemovalReasonExists(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: fetchEmployeeEmail Method Description: This Method is used
	 * for fetching the primary and secondary case-workers employee email
	 * addresses based on the event id
	 * 
	 * @param commonHelperReq
	 * @return String
	 * @throws InvalidRequestException
	 * @
	 */
	public String fetchEmployeeEmail(CommonHelperReq commonHelperReq) throws InvalidRequestException;

}
