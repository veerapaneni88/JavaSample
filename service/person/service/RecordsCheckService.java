/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jun 19, 2017- 7:58:54 PM
 *© 2017 Texas Department of Family and Protective Services
 *  *******Change History**********
 *  01/25/2021 nairl artf172936 : DEV BR 15.01 Indicator (IMPACT) for Person Who Has Access to CHRI P2
 *  02/12/2021 nairl artf172946 : DEV BR 21.01 Support Manual Entry of Results from DPS’ SecureSite into IMPACT P2
 */
package us.tx.state.dfps.service.person.service;

import java.sql.SQLException;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.EmailNotificationsReq;
import us.tx.state.dfps.service.common.request.RecordsCheckDetailReq;
import us.tx.state.dfps.service.common.request.RecordsCheckReq;
import us.tx.state.dfps.service.common.request.RecordsCheckStatusReq;
import us.tx.state.dfps.service.common.response.EmailNotificationsRes;
import us.tx.state.dfps.service.common.response.RecordsCheckListRes;
import us.tx.state.dfps.service.common.response.RecordsCheckRes;

public interface RecordsCheckService {

	/**
	 * Method Name: getDtCreatedOfPaperRecord Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes getDtCreatedOfPaperRecord(RecordsCheckReq request);

	/**
	 * Method Name: getMostRecentCASAFPSIdRecCheck Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes getMostRecentCASAFPSIdRecCheck(RecordsCheckReq request);

	/**
	 * Method Name: isPersonCasaProvisioned Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes isPersonCasaProvisioned(RecordsCheckReq request);

	/**
	 * Method Name: retrieveRecordsCheckDocument Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes retrieveRecordsCheckDocument(RecordsCheckReq request);

	/**
	 * Method Name: retrieveRecordsCheckNotifications Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes retrieveRecordsCheckNotifications(RecordsCheckReq request);

	/**
	 * Method Name: getRecordsCheckDetail Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes getRecordsCheckDetail(RecordsCheckReq request);

	/**
	 * Method Name: isABCSCheck Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes isABCSCheck(RecordsCheckReq request);

	/**
	 * Method Name: isCasaFpsCheck Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes isCasaFpsCheck(RecordsCheckReq request);

	/**
	 * Method Name: getAbcsContractID Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes getAbcsContractID(RecordsCheckReq request);

	/**
	 * Added to implement artf172936
	 * Method Name: getAbcsAccessData
	 * Method Description: This method is used to get the access data from ABCS.
	 *
	 * @param request
	 * @return RecordsCheckRes
	 */
	RecordsCheckRes getAbcsAccessData(RecordsCheckReq request);

	/**
	 * Method Name: callDPSWSNameSearchProcedure Method Description:
	 * 
	 * @param request
	 * @return
	 */
	void callDPSWSNameSearchProcedure(RecordsCheckReq request);

	/**
	 * Method Name: getPersonNameList Method Description:
	 * 
	 * @param request
	 */
	RecordsCheckRes getPersonNameList(RecordsCheckReq request);

	/**
	 * Method Name: isNameValid Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes isNameValid(RecordsCheckReq request);

	/**
	 * Method Name: hasPendingFingerprintCheck Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes hasPendingFingerprintCheck(RecordsCheckReq request);

	/**
	 * Method Name: getScorContractNbr Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes getScorContractNbr(RecordsCheckReq request);

	/**
	 * Method Name: generateFBIClearanceEmail Method Description:
	 * 
	 * @param request
	 * @return
	 */

	EmailNotificationsRes generateFBIEligibleExhireEmail(EmailNotificationsReq request);
	/**
	 * Method Name: generateFBIEligibleEmail Method Description:
	 *
	 * @param request
	 * @return
	 */

	EmailNotificationsRes generateEligibleEmail(EmailNotificationsReq request);

	/**
	 * Method Name: generateFBIEligibleEmail Method Description:
	 *
	 * @param request
	 * @return
	 */

	EmailNotificationsRes generatePCSEligibleEmail(EmailNotificationsReq request);

	/**
	 * Method Name: generatePCSInEligibleEmail Method Description:
	 *
	 * @param request
	 * @return
	 */
	EmailNotificationsRes generatePCSInEligibleEmail(EmailNotificationsReq request);

	/**
	 * Method Name: generateFBIIneligibleEmail Method Description:
	 *
	 * @param request
	 * @return
	 */
	EmailNotificationsRes generateIneligibleEmail(EmailNotificationsReq request);

	/**
	 * Method Name: generatePSClearanceEmail Method Description:
	 * 
	 * @param request
	 * @return
	 */
	EmailNotificationsRes generatePSClearanceEmail(EmailNotificationsReq request);

	/**
	 * Method Name: hasEmailSent Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes hasEmailSent(RecordsCheckReq request);

	/**
	 * Method Name: getRecordDocumentTsLastUpdate Method Description:
	 * 
	 * @param request
	 * @return
	 */
	RecordsCheckRes getRecordDocumentTsLastUpdate(RecordsCheckReq request);

	/**
	 * Method Name: deleteDocumentPdbRecord Method Description:
	 * 
	 * @param request
	 * @return
	 */
	void deleteDocumentPdbRecord(RecordsCheckDetailReq request);

	/**
	 * Method Name: getRecordsCheckList Method Description: This service is used
	 * to retrieve the list of Records Check
	 * 
	 * @param Long
	 * @return RecordsCheckListRes
	 */
	public RecordsCheckListRes getRecordsCheckList(RecordsCheckReq recordsCheckReq);

	/**
	 * Method Name: getMaxIdRecCheckNotif Method Description: This service is
	 * used to retrieve the maximum value of ID_RECORDS_CHECK_NOTIF
	 * 
	 * @return Integer
	 */
	public Integer getMaxIdRecCheckNotif();

	/**
	 * 
	 * Method Name: recordsCheckAUDService Method Description:This service will
	 * add, Update or Delete a Records Check a given IdRecCheck
	 * 
	 * @param RecordsCheckDetailReq
	 * @return RecordsCheckListRes
	 * @throws InvalidRequestException
	 * @
	 */
	public RecordsCheckListRes recordsCheckAUDService(RecordsCheckDetailReq recordsCheckDetailReq);

	/**
	 * Method Name: updateRecordsCheckStatus Method Description:This method is
	 * for updating status for corresponding idRecCheck
	 * 
	 * @param RecordsCheckStatusReq
	 * @return RecordsCheckRes @
	 */
	public RecordsCheckRes updateRecordsCheckStatus(RecordsCheckStatusReq recordsCheckStatusReq);

	/**
	 * Method Name: generateAlerts Method Description: This method is used to
	 * generate alerts for Person when the results are returned for the DPS
	 * Criminal History Record Check.
	 * 
	 * @param RecordsCheckRes
	 * @return RecordsCheckStatusReq
	 */
	public RecordsCheckRes generateAlerts(RecordsCheckStatusReq recordsCheckStatusReq);

	/**
	 * Method Name: getServiceCode Method Description:Retrives Service code from
	 * stored procedure call for a giving Record Check id
	 * 
	 * @param RecordsCheckStatusReq
	 * @return RecordsCheckRes
	 */
	public RecordsCheckRes getServiceCode(RecordsCheckStatusReq recordsCheckStatusReq);
	/* Added for artf172946 */
	/**
	 * Method Name: updateRecordsCheck
	 * Method Description:This method will update
	 * the determination,status,dtDetermFinal and accptRej fields for the corresponding
	 * idRecCheck
	 *
	 * @param recordsCheckDetailReq
	 * @return RecordsCheckRes
	 */
	public RecordsCheckRes updateRecordsCheck(RecordsCheckDetailReq recordsCheckDetailReq);
	/**
	 *
	 * Method Name: recordsCheckDetermHistoryAD
	 * Method Description:This service will add and delete Records Check Determination History for a given IdRecCheck
	 *
	 * @param recordsCheckDetailReq
	 * @return RecordsCheckListRes
	 */
	public RecordsCheckListRes recdsCheckDetermHistoryAD(RecordsCheckDetailReq recordsCheckDetailReq);
	/**
	 *
	 * Method Name: getRecCheckDetermHistory
	 * Method Description:This service will get Records Check Determination History for a given IdRecCheck
	 *
	 * @param idRecCheck
	 * @return RecordsCheckListRes
	 */
	public RecordsCheckListRes getRecCheckDetermHistory(Long idRecCheck);

	/* End of artf172946 */

	/**
	 * @param request
	 * @return
	 */
	public RecordsCheckRes hasOriginatingFPCheck(RecordsCheckReq request);

	/**
	 * @param request
	 * @return
	 */
	public RecordsCheckRes getSidOriginalFingerprint(RecordsCheckReq request);

	/**
	 * @param request
	 * @return
	 */
	RecordsCheckRes isABCSCheckRapBack(RecordsCheckReq request);

	/**
	 * Method Name: getNewHireCount Method Description: This method fetch
	 * The count from BACKGROUND_CHECK_PDB joining CONTRACT_PDB table giving a records_check count
	 *
	 * @param idRecCheck
	 * @return count
	 */
	public RecordsCheckRes getNewHireCount(Long idRecCheck);

	public RecordsCheckRes getCdDetermination(Long idRecCheck);


}
