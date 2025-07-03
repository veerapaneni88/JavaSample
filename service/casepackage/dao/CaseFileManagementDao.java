package us.tx.state.dfps.service.casepackage.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.CaseFileManagement;
import us.tx.state.dfps.common.domain.RecordsRetention;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCFC21S
 * Class Description: Incoming Details Dao Mar 26, 2017 - 8:58:10 PM
 */

public interface CaseFileManagementDao {
	/**
	 * 
	 * Method Description: This Method will retrieve a full row from the Case
	 * File Management DAM Name:CSES57D Service: CCFC21S
	 * 
	 * @param ulIdCase
	 * @return CaseFileManagementDto @
	 */
	public CaseFileManagementDto getCaseFileDetails(Long ulIdCase);

	/**
	 * Method Description: This method will retrieve idCaseFileCase and
	 * txtCaseFileLocateInfo from Case File Management table. Service
	 * Name:CFMgmntList
	 * 
	 * @param idCase
	 * @return List<CaseFileManagementDto> @
	 */
	public List<CaseFileManagementDto> getCFMgmntList(Long idCase);

	/**
	 * Method Description: Query case file management and case merge table and
	 * get the case id and skp trn info for From cases merged to current case.
	 * Service Name:CFMgmntList
	 * 
	 * @param idCase
	 * @return @
	 */
	public List<CaseFileManagementDto> getSkpTrnInfo(Long idCase);

	/**
	 * This service will add/update all columns for an Id Case from the RECORDS
	 * RETENTION table. It will call DAM: CAUD75D - REC RETN AUD.
	 * 
	 * @return @
	 */

	public long insertRecordsRetention(RecordsRetention retention);

	public void updateRecordsRetention(RecordsRetention retention);

	public void deleteRecordsRetention(RecordsRetention retention);

	/**
	 * This service will save all columns for an IdCase to the CASE FILE
	 * MANAGEMENT table. There will be one row for a specified IdCase.
	 * Furthermore, it will check to see if the MailCode/Region/Program
	 * specified exists as well as the Unit/Region/Program exists. Additionally,
	 * it will retrieve a full row from the CAPS CASE table to get the closure
	 * date for the Case. CAUD76D - REC CASE FILE MANAGEMENT
	 */
	public long insertCaseFileManagement(CaseFileManagement caseFileManagement);

	public void updateCaseFileManagement(CaseFileManagement caseFileManagement);

	public void deleteCaseFileManagement(CaseFileManagement caseFileManagement);

	public CaseFileManagement findCaseFileManagementById(Long idCaseFileCase);
}
