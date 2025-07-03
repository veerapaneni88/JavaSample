package us.tx.state.dfps.service.workload.service;

import us.tx.state.dfps.service.common.request.PrincipalCaseHistoryReq;
import us.tx.state.dfps.service.common.response.PrincipalCaseHistoryRes;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION EJB Name: PrincipalCaseHistoryBean
 * Description: This class is use for CRUD Operations
 * PrincipalCaseHistoryService Aug 2, 2017 - 3:19:51 PM
 */

public interface PrincipalCaseHistoryService {

	/**
	 * Method Description: Method is implemented in PrincipalCaseHistoryDaoImpl
	 * to perform insert operations. This method will insert Parent Child
	 * relationship Checked Linked case information into CASE_LINK Table. EJB
	 * Name: PrincipalCaseHistoryBean
	 *
	 * @param caseID
	 *            the case ID
	 * @return PrincipalCaseHistoryRes @ the service exception
	 */
	public PrincipalCaseHistoryRes caseList(PrincipalCaseHistoryReq principalCaseHistoryReq);

	/**
	 * Method Name: selectPrincipalList Method Description: This method will be
	 * called when the radio button for a case is selected on the
	 * PrincipalCaseHistory page. Also the Principal List section will include
	 * the Stage Id, Stage Type and Overall Disposition for the INV stage and
	 * all of the principals in the stage. For each principal, the Name, Person
	 * ID, Age, DOB, Gender, Role, and Rel/Int will be displayed. The Principal
	 * List section will be sorted by Stage ID descending, and then by ID Person
	 * ascending order.
	 * 
	 * @param PrincipalCaseHistoryReq
	 * @return PrincipalCaseHistoryRes
	 */
	public PrincipalCaseHistoryRes selectPrincipalList(PrincipalCaseHistoryReq principalCaseHistoryReq);

	/**
	 * Method Description: Method is implemented in PrincipalCaseHistoryDaoImpl
	 * to perform insert operations. This method will insert Parent Child
	 * relationship Checked Linked case information into CASE_LINK Table. EJB
	 * Name: PrincipalCaseHistoryBean
	 * 
	 * @param principalCaseHistoryReq
	 * @return PrincipalCaseHistoryRes
	 */
	public PrincipalCaseHistoryRes insertCaseInfo(PrincipalCaseHistoryReq principalCaseHistoryReq);

	/**
	 * 
	 * Method Description: Method is implemented in PrincipalCaseHistoryDaoImpl
	 * to perform update operations. This method will update Parent to Child
	 * Information. This method updates the Unchecked Linked Case Information
	 * into the Case Link. Users with the Merger Case security attribute will be
	 * able to update linking even if the case is closed and not in their chain
	 * of command. All other users will only be able to use it if the case is
	 * open. EJB Name: PrincipalCaseHistoryBean
	 * 
	 * @param principalCaseHistoryReq
	 * @return PrincipalCaseHistoryRes @
	 */
	public PrincipalCaseHistoryRes updateCaseInfo(PrincipalCaseHistoryReq principalCaseHistoryReq);

}
