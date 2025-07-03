package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.CaseListDto;
import us.tx.state.dfps.service.casepackage.dto.PrincipalListDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION EJB Name: PrincipalCaseHistoryBean
 * Class Description: PrincipalCaseHistoryDao Interface Aug 2, 2017 - 3:45:39 PM
 */

public interface PrincipalCaseHistoryDao {

	/**
	 * Case list.
	 *
	 * @param caseID
	 *            the case ID
	 * @return the list @ the service exception
	 */
	public List<CaseListDto> caseList(long caseID);

	/**
	 * Method Description: This method will get the unique Case IDs. Also get
	 * the relvant Case List information for each Case Id from the following
	 * table. CAPS_CASE, STAGE, STAGE_PERSON_LINK, CASE_LINK, CASE_,MERGE,
	 * CPS_INVST_DETAIL, PERSON table. caseList() will populate Case List
	 * Section on the PrincipalCaseHistory page.
	 *
	 * @param caseID
	 *            the case ID
	 * @param globalCaseID
	 *            the global case ID
	 * @return List<PrincipalListDto>
	 */
	public List<PrincipalListDto> selectPrincipalList(long caseID, long globalCaseID);

	/**
	 * 
	 * Method Description: Method is implemented in PrincipalCaseHistoryDaoImpl
	 * to perform insert operations. This method will insert Parent Child
	 * relationship Checked Linked case information into CASE_LINK Table. EJB
	 * Name: PrincipalCaseHistoryBean
	 * 
	 *
	 * @param idUser
	 * @param idCase
	 * @param idLinkCase
	 * @param indicator
	 * @return void
	 */
	public void insertCaseInfo(Long idUser, Long idCase, Long idLinkCase, String indicator);

	/**
	 * 
	 * Method Description: Method is implemented in PrincipalCaseHistoryDaoImpl
	 * to perform update operations. This method will update Parent to Child
	 * Information. This method updates the Unchecked Linked Case Information
	 * into the Case Link. Users with the Merger Case security attribute will be
	 * able to update linking even if the case is closed and not in their chain
	 * of command. All other users will only be able to use it if the case is
	 * open.EJB Name: PrincipalCaseHistoryBean
	 * 
	 * @param idUser
	 * @param idCase
	 * @param idLinkCase
	 * @param indicator
	 * @return void
	 */
	public void updateCaseInfo(Long idUser, Long idCase, Long idLinkCase, String indicator);

}
