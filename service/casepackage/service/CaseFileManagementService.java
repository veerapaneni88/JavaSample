package us.tx.state.dfps.service.casepackage.service;

import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;
import us.tx.state.dfps.service.common.request.CaseFileManagementReq;
import us.tx.state.dfps.service.common.request.CaseFileMgtReq;
import us.tx.state.dfps.service.common.request.RecordsRetentionReq;
import us.tx.state.dfps.service.common.response.CaseFileMgtRes;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCFC21S Class
 * Description: This class is use for retrieving CaseFileManagementRtrv Mar 23,
 * 2017 - 7:40:51 PM
 */

public interface CaseFileManagementService {
	/**
	 * 
	 * Method Description: This Method will retrieve all columns for an Id Case
	 * from the CASE FILE MANAGEMENT table. There will be one row for a
	 * specified Id Case. It will retrieve a full row from both the OFFICE and
	 * UNIT tables with the ID OFFICE and ID UNIT respectively. The service will
	 * also retrieve a full row from the CAPS CASE table to get the closure date
	 * for the Case. Finally, it will check to see if the person who entered the
	 * window is the primary worker. Service Name: CCFC21S
	 * 
	 * @param caseFileMgtReq
	 * @return CaseFileManagementDto @
	 */
	public CaseFileManagementDto getCaseFileManagementRtrv(CaseFileMgtReq caseFileMgtReq);

	/**
	 * This service will add/update all columns for an Id Case from the RECORDS
	 * RETENTION table. It will call DAM: CAUD75D - REC RETN AUD.
	 * 
	 * @return @
	 */
	String manageRecordsRetention(RecordsRetentionReq recordsRetentionReq);

	/**
	 * This service will save all columns for an IdCase to the CASE FILE
	 * MANAGEMENT table. There will be one row for a specified IdCase.
	 * Furthermore, it will check to see if the MailCode/Region/Program
	 * specified exists as well as the Unit/Region/Program exists. Additionally,
	 * it will retrieve a full row from the CAPS CASE table to get the closure
	 * date for the Case.
	 * 
	 * @return @
	 */
	String manageCaseFileManagement(CaseFileManagementReq caseFileManagementReq);

	/**
	 * Method Description: This method will retrieve locating information.
	 * Service Name:CFMgmntList
	 * 
	 * @param caseFileMgtReq
	 * @return CaseFileMgtRes @
	 */
	public CaseFileMgtRes getCFMList(CaseFileMgtReq caseFileMgtReq);

}
