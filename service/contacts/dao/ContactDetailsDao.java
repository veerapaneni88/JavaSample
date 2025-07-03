package us.tx.state.dfps.service.contacts.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import oracle.jdbc.driver.DatabaseError;
import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.dto.ContactPersonNarrValueDto;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.SaveContactReq;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPSValueModBean;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewContactDto;
import us.tx.state.dfps.service.contact.dto.ContactActiveAddrDto;
import us.tx.state.dfps.service.contact.dto.ContactDetailCFReportBean;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactDetailsDao Jul 31, 2017- 1:04:17 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ContactDetailsDao {
	/**
	 * Method Name: reviewComplete Method Description:This method allows to
	 * complete review when type CSS Review is selected in contact details page.
	 * 
	 * @param saveContactReq
	 * @return String
	 */
	public String reviewComplete(SaveContactReq saveContactReq);

	/**
	 * Method Name: completeCFInfoTasks Method Description:This method allows to
	 * complete CFInfoTask
	 * 
	 * @param commonEventIdReq
	 * @return @
	 */
	public String completeCFInfoTasks(CommonEventIdReq commonEventIdReq);

	/**
	 * This method retrieves Contact Record for the given Contact Event Id.
	 * 
	 * @param idEvent
	 * @return ContactDto
	 */
	public ContactDto selectContact(long idEvent);

	/**
	 * MethodName: updateApproversStatus MethodDescription: Updates the
	 * Approvers Status Code to Invalid
	 * 
	 * @param idEvent
	 * @return long
	 */
	public long updateApproversStatus(Long idEvent);

	/**
	 * MethodName: updateAppEventStatus MethodDescription: Update Approval event
	 * status.
	 * 
	 * @param idEvent
	 * @param eventStatus
	 * @return long
	 */
	public long updateAppEventStatus(Long idEvent, String eventStatus);

	/**
	 * * This method checks returns all the deceased Children in the given
	 * Stage. Checks for following Conditions. 1. Allegation Record present for
	 * the Child with IND_FATALITY = 'Y'. 2. Same Child should have Date of
	 * Death in Person Table.
	 * 
	 * @param idStage
	 * @return List<Long> @
	 */
	public List<Long> getCFTChildrenForStage(Long idStage);

	/**
	 * This method checks if Non-End Dated 1050B Report exists for the given
	 * Stage and the Child.
	 * 
	 * @param idStage
	 * @param idPerson
	 * @return @
	 */
	public boolean hasCF1050BRecord(Long idStage, Long idPerson);

	/**
	 * Method Name: getCFTPriorHistory Method Description:This method return
	 * prior history for the given Stage.
	 * 
	 * @param idStage
	 * @param idPerson
	 * @return List<CFTRlsInfoRptCPSValueDto>
	 */
	public List<CFTRlsInfoRptCPSValueModBean> getCFTPriorHistory(Long idStage, Long idPerson);

	/**
	 * Method Name: getCFTCurrHisInfo Method Description: This method current
	 * info for the given Stage.
	 * 
	 * @param cftRlsInfoRptCPSValueDto
	 * @return CFTRlsInfoRptCPSValueDto
	 */
	public CFTRlsInfoRptCPSValueModBean getCFTCurrHisInfo(CFTRlsInfoRptCPSValueModBean cftRlsInfoRptCPSValueDto);

	/**
	 * Method Name: fetchChildFatalityInfo Method Description:One of two
	 * different retrieval methods is invoked to fetch data for child fatality
	 * (RCL screen) based on if the contact page for current person and stage is
	 * NEW or existing - This method retrieves child fatality info for the given
	 * Stage and is used by continueType_xa() method used when the contact type
	 * is new <no prior data> exist for current person ID & stage (data
	 * retrieved from existing tables IMPACT & CLASS tables, not from
	 * FT_RLS_INFO_* tables >> which is retrieved from for update of contact
	 * info via displayContact_xa()).
	 * 
	 * @param personId
	 * @param stageId
	 * @param userId
	 * @return ContactDetailCFReportBean
	 */
	public ContactDetailCFReportBean fetchChildFatalityInfo(long personId, long stageId, long userId);

	/**
	 * Method Name: fetchContactPersonNarr Method Description: Method to fetch
	 * Contact Guide Narrative for Caregiver/Collateral contacted.
	 * 
	 * @param contactPersonNarrValueDto
	 * @return ContactPersonNarrValueDto
	 */
	public ContactPersonNarrValueDto fetchContactPersonNarr(ContactPersonNarrValueDto contactPersonNarrValueDto);

	/**
	 * Function and related back end CALL to get operType based on stage.
	 * 
	 * @param idStage
	 * @return String
	 */
	public String fetchFacilityType(Long idStage);

	/**
	 * Method to delete the Contact Guide Narrative
	 * 
	 * @param personNarrBean
	 * @return @
	 */
	public long deleteContactPersonNarr(ContactPersonNarrValueDto personNarrBean);

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 * 
	 * Outputs: Service return code.
	 * 
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 * 
	 * 
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 * 
	 * @param contact
	 * @
	 */
	public void saveContact(Contact contact);

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 * 
	 * Outputs: Service return code.
	 * 
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 * 
	 * 
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 * 
	 * @param contact
	 * @
	 */
	public void updateContact(Contact contact);

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 * 
	 * Outputs: Service return code.
	 * 
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 * 
	 * 
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 * 
	 * @param contact
	 * @
	 */
	public void deleteContact(Contact contact);

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 * 
	 * Outputs: Service return code.
	 * 
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 * 
	 * 
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 * 
	 * @param idContact
	 * @return @
	 */
	public Contact getContactEntityById(Long idContact);

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 * 
	 * Outputs: Service return code.
	 * 
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 * 
	 * 
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 * 
	 * @param idContact
	 * @return @
	 */
	public ContactDto getContactById(Long idContact);

	/**
	 * Method to save the Contact person narrative into the Contact_person_narr
	 * table.
	 * 
	 * @param ContactPersonNarrValueDto
	 * @return
	 */
	public int addContactPersonNarr(ContactPersonNarrValueDto personNarrDto);

	/**
	 * Method to delete the Contact Guide Narrative
	 * 
	 * @param guideBean
	 */

	public int updateContactPersonNarr(ContactPersonNarrValueDto personNarrDto);

	/**
	 * @param idEvent
	 * @return Long @
	 */
	public Long updateApproversStatus(int idEvent);

	/**
	 * Update Approval event status.
	 * 
	 * @param idEvent
	 * @param eventStatus
	 * @return Long @
	 */
	public Long updateAppEventStatus(int idEvent, String eventStatus);

	/**
	 * Method Name: getContactType Method Description:This method retrieves
	 * Contact Type for the given Contact Event Id.
	 * 
	 * @param idEvent
	 * @return String
	 */
	public String getContactType(long idEvent);

	public boolean isCrimHistCheckPending(long idStage);

	/**
	 * Method Name: deleteCFTRlsInfoRpt Method Description: This method deletes
	 * record from FtRlsInfoRpt table.
	 * 
	 * @param idEvent
	 * @
	 */
	public void deleteCFTRlsInfoRpt(Long idEvent);

	/**
	 * Method Name: deleteRlsInfoRptCPSByRptId Method Description: This method
	 * deletes record from FtRlsInfoRptCps table.
	 * 
	 * @param idEvent
	 * @
	 */
	public void deleteRlsInfoRptCPSByRptId(Long rptId);

	/**
	 * Method Name: deleteRlsInfoAllegDispositionByRptId Method Description:
	 * This method deletes record from FtRlsInfoRptAllegDisp table.
	 * 
	 * @param rptId
	 * @
	 */
	public void deleteRlsInfoAllegDispositionByRptId(Long rptId);

	/**
	 * Method Name: deleteRlsInfoRptRsrcByRptId Method Description: This method
	 * deletes record from FtRlsInfoRptRsrc table.
	 * 
	 * @param rptId
	 * @
	 */
	public void deleteRlsInfoRptRsrcByRptId(Long rptId);

	/**
	 * Method Name: deleteRsrcViolationByRptId Method Description: This method
	 * deletes record from FtRlsInfoRptRsrcVoltns table.
	 * 
	 * @param rptId
	 * @
	 */
	public void deleteRsrcViolationByRptId(Long rptId);

	/**
	 * Method Name: deleteRlsInfoRptCPAByRptId Method Description: This method
	 * deletes record from FtRlsInfoRptCpa table.
	 * 
	 * @param rptId
	 * @
	 */
	public void deleteRlsInfoRptCPAByRptId(Long rptId);

	/**
	 * Method Name: getReasonRlngshmntCodes Method Description: This method to
	 * get the reasonRlngshmnt codes
	 * 
	 * @return Map<String, String>
	 */
	Map<String, String> getReasonRlngshmntCodes();

	/**
	 * Gets the contact.
	 *
	 * @param idContact
	 *            the id contact
	 * @return the contact
	 */
	Contact getContact(Long idContact);

	/**
	 * Method Name: getAllPersonRecords Method Description: This method receives
	 * ID STAGE from the service and returns one or more rows from the
	 * STAGE_PERSON_LINK, NAME, PERSON, ADDRESS_PERSON_LINK, PERSON_ADDRESS,
	 * PERSON_PHONE tables. (CLSC0DD)
	 * 
	 * @param idStage
	 * @return List<PersonDto>
	 */
	public List<PersonDto> getAllPersonRecords(Long idStage);

	/**
	 * Method Name: getPersonsAddrDtls Method Description:This method receives
	 * ID_PERSON and returns one or more rows from the ADDRESS_PERSON_LINK,
	 * PERSON_ADDRESS tables for non-invalid non-endated addresses. (CLSC1BD)
	 * 
	 * @param idPerson
	 * @return List<PersonDto>
	 */
	public List<PersonDto> getPersonsAddrDtls(Long idPerson);

	/**
	 * Method Name: getRecentClosedIdStage Method Description: This method Gets
	 * the most recently closed previous ID STAGE for a given ID STAGE.
	 * (ccmnb5d)
	 * 
	 * @param idStage
	 * @return StageLinkDto
	 */
	public StageLinkDto getRecentClosedIdStage(Long idStage);

	/**
	 * Method Name: getContactActiveAddr Method Description: This method
	 * Retrieves active primary address, phone number, and name for an employee
	 * (CSEC01D)
	 * 
	 * @param idPerson
	 * @return ContactActiveAddrDto
	 */
	public ContactActiveAddrDto getContactActiveAddr(Long idPerson);

	/**
	 * Method Name: getDistinctAllgtnList Method Description: Get distinct list
	 * of allegation records for a given ID_ALLEGATION_STAGE. If the perpetrator
	 * = victim, return SELF_NEGLECT in szCdDecodeAllegType. (CLSSABD)
	 * 
	 * @param idStage
	 * @return List<AllegationDto>
	 */
	public List<AllegationDto> getDistinctAllgtnList(Long idStage);

	/**
	 * Method Name: getRiskNarrExists Method Description: This method gets the Narrative
	 * Count from RISK_ASSMT_IRA_NARR and RISK_ASSMT_NARR which helps to set the DocType
	 * for Structured Narrative
	 * 
	 * @param idCase
	 * @param idEvent
	 * @return String
	 */
	//Warranty Defect - 11243 - To display both Versions of Structured Narrative RISKSF and CIV33O00
	public String getRiskNarrExists(Long idCase, Long idEvent);

}
