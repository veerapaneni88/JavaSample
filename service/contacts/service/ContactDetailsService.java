package us.tx.state.dfps.service.contacts.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.tx.state.dfps.common.dto.ContactDetailChildFatalityReportDto;
import us.tx.state.dfps.common.dto.ContactPersonNarrValueDto;
import us.tx.state.dfps.common.web.bean.StageSearchBean;
import us.tx.state.dfps.service.admin.dto.TodoCreateOutDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.request.CommonContactNarrativeReq;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ContactDetailReq;
import us.tx.state.dfps.service.common.request.ContactReq;
import us.tx.state.dfps.service.common.request.ContactSearchReq;
import us.tx.state.dfps.service.common.request.DisplayFbssClosingLetterReq;
import us.tx.state.dfps.service.common.request.FbssClosingLetterReq;
import us.tx.state.dfps.service.common.request.InrFollowupPendingReq;
import us.tx.state.dfps.service.common.request.InsertAllegReq;
import us.tx.state.dfps.service.common.request.SaveContactReq;
import us.tx.state.dfps.service.common.request.SaveCpaReq;
import us.tx.state.dfps.service.common.request.SaveGuideContactReq;
import us.tx.state.dfps.service.common.request.StageSearchReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.ContactAUDRes;
import us.tx.state.dfps.service.common.response.ContactDetailNarrativeRes;
import us.tx.state.dfps.service.common.response.ContactSearchRes;
import us.tx.state.dfps.service.common.response.InrFollowupPendingRes;
import us.tx.state.dfps.service.common.response.SaveContactRes;
import us.tx.state.dfps.service.common.response.StageSearchRes;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPSValueModBean;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptValueDto;
import us.tx.state.dfps.service.contact.dto.ChildFatalityContactDto;
import us.tx.state.dfps.service.contact.dto.ContactDetailCFReportBean;
import us.tx.state.dfps.service.contact.dto.ContactGuideDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.web.contact.bean.ContactDetailDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactAUDDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactDetailsDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactInformationDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ChildContactDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ConGuideFetchOutDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactInformationDoDto;

/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactDetailsService Jul 13, 2018- 2:45:22 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ContactDetailsService {

	/**
	 * Method Name:saveContact Method Description: Method to add/update/delete
	 * Contact person information. The operation type (A U D) is already
	 * prepared in ContactSeachListDetailConversation
	 *
	 * @param contactDetailReq
	 * @return Long
	 */
	public Long saveContact(ContactDetailReq contactDetailReq);

	/**
	 * Method Name: reviewComplete Method Description:This method allows to
	 * complete review when type CSS Review is selected in contact details page.
	 *
	 * @param saveContactReq
	 * @return SaveContactRes
	 */
	public SaveContactRes reviewComplete(SaveContactReq saveContactReq);

	/**
	 * Method Name: searchContacts Method Description:This method search
	 * contacts
	 *
	 * @param contactSearchReq
	 * @return ContactSearchRes
	 */
	public ContactSearchRes searchContacts(ContactSearchReq contactSearchReq);

	/**
	 * Method Name: indStructNarrExists Method Description: get narrative
	 * indication
	 *
	 * @param contactSearchReq
	 * @return boolean
	 */
	public boolean indStructNarrExists(ContactSearchReq contactSearchReq);

	/**
	 * Method Name: completeCFInfoTasks Method Description:completeCFInfoTasks
	 *
	 * @param commonEventIdReq
	 * @return SaveContactRes
	 */
	public SaveContactRes completeCFInfoTasks(CommonEventIdReq commonEventIdReq);

	/**
	 * Method Name: getCFTChildrenForStage Method Description:This method checks
	 * returns all the deceased Chidlrenin the given Stage. Checks for following
	 * Conditions.
	 *
	 * 1. Allegation Record present for the Child with IND_FATALITY = 'Y'. 2.
	 * Same Child should have Date of Death in Person Table.
	 *
	 * @param idStage
	 * @return List<Long>
	 */
	public List<Long> getCFTChildrenForStage(Long idStage);

	/**
	 * This method deletes the contact and related records
	 *
	 * @param deleteContactDto
	 * @param contactGuideList
	 * @param contactPersonNarrList
	 * @return Long
	 */
	public ContactAUDRes contactDetailAud(ContactAUDDto deleteContactDto, List<ContactGuideDto> contactGuideList,
                                        List<ContactPersonNarrValueDto> contactPersonNarrList) ;

	/**
	 * Method Name: selectContact1050BReport Method Description:This method
	 * retrieves the information for Child Fatality 1050B Report from the
	 * database using Event Id.
	 *
	 * @param idEvent
	 * @param program
	 * @return CFT1050BReportFetchDto
	 */
	public ContactDetailChildFatalityReportDto selectContact1050BReport(long idEvent, String program);

	/**
	 * Method Name: saveContactFor1050BReport Method Description: This method
	 * creates 1050B Report Event (and Contact) for each Child Fatality in the
	 * given Stage.
	 *
	 * @param childFatalityContactDto
	 * @return Long
	 */
	public Long saveContactFor1050BReport(ChildFatalityContactDto childFatalityContactDto);

	/**
	 * Method Name: fetchCurrAbuseNglctInfo Method Description:Fetches the
	 * current Abuse and neglect bean info info for the IdStage
	 *
	 * @param idStage
	 * @param idCase
	 * @return CFTRlsInfoRptCPSValueBean
	 */
	public CFTRlsInfoRptCPSValueModBean fetchCurrAbuseNglctInfo(Long idStage, Long idCase);

	/**
	 * Method Name: fetchPriorHistoryInfo Method Description:Fetches the
	 * priorHistory Information for the stage
	 *
	 * @param idStage
	 * @param idPerson
	 * @return List<CFTRlsInfoRptCPSValueModBean>
	 */
	public List<CFTRlsInfoRptCPSValueModBean> fetchPriorHistoryInfo(Long idStage, Long idPerson);

	/**
	 * Method Name: fetchFacilityType Method Description:Get the operation type
	 * for the current stage.
	 *
	 * @param stageId
	 * @return String
	 */
	public String fetchFacilityType(Long idStage);

	/**
	 * Method Name: fetchChildFatalityInfo Method Description:Fetches Child
	 * Fatality Information
	 *
	 * @param personId
	 * @param stageId
	 * @param userId
	 * @return CFT1050BReportDto
	 */
	public ContactDetailCFReportBean fetchChildFatalityInfo(long personId, long stageId, long userId);

	/**
	 * Method Name: fetchContactPeronNarrList Method Description:This method
	 * fetches the contact person narrative information for contact of type
	 * KinshipNotification. The contact_person_narr table stores the information
	 * when a person is not notified explicitly. This method builds the list
	 * based on stage_person_link, and if record exist in contact_person_narr
	 * table, information is included.
	 *
	 * @param conGuideFetchOutDto
	 * @param idStage
	 * @param idEvent
	 * @return List<ContactPersonNarrValueDto>
	 */
	public List<ContactPersonNarrValueDto> fetchContactPeronNarrList(ArrayList<ContactDetailDto> contactedList,
																	 long idStage, Long idEvent);

	/**
	 * Saves the priorHistory/current /other Information for the stage
	 *
	 * @param cftRlsInfoRptCPSValueDto
	 * @return CFTRlsInfoRptCPAValueDto
	 */
	public CFTRlsInfoRptCPSValueModBean saveCFTRlsInfoRptCPS(CFTRlsInfoRptCPSValueModBean cftRlsInfoRptCPSValueDto);

	/**
	 * This method creates 1050B Report Event.
	 *
	 * @param eventValueDto
	 * @param idUser
	 * @return long
	 */
	public long create1050BEvent(EventValueDto eventValueDto, long idUser);

	/**
	 * This function creates Rejection Alert for 1050B Author.
	 *
	 * @param idEvent
	 * @param idUser
	 * @param program
	 * @return TodoCreateOutDto @
	 */
	public TodoCreateOutDto create1050BRejAlert(long idEvent, long idUser, String program);

	/**
	 * This function creates Rejection Task for 1050B Author.
	 *
	 * @param idEvent
	 * @param idUser
	 * @param program
	 * @return TodoCreateOutDto @
	 */
	public TodoCreateOutDto create1050BRejectionTask(long idEvent, long idUser, String program);

	/**
	 * This method updates 1050B Report Event.
	 *
	 * @param cft1050bReportDto
	 * @return long @
	 */
	public long update1050BEvent(ChildFatalityContactDto cft1050bReportDto);

	/**
	 * This method will delete row(s) from A/N or Agency section from the CF
	 * Info for Public Release data entry page.
	 *
	 * @param idFtRlsInfoRptCPS
	 * @return long @
	 */
	public long deleteCFTRlsInfoRptCPS(long idFtRlsInfoRptCPS);

	/**
	 * Deletes the priorHistory /other Information for the stage
	 *
	 * @param cft1050bReportDto
	 * @return CFT1050BReportDto @
	 */
	public ChildFatalityContactDto deleteLICHistory(ChildFatalityContactDto cft1050bReportDto);

	/**
	 * This method is used to Alert of Extension Request Approval/Rejection
	 *
	 * @param idEvent
	 * @param approvalLen
	 * @param approvalStatus
	 * @return long @
	 */
	public long createExtensionAprRejAlert(Long idEvent, String approvalLen, String approvalStatus);

	/**
	 * This method creates 1050B Report Event (and Contact) for each Child
	 * Fatality in the given Stage.
	 *
	 * @param ContactDetailsDto
	 * @param program
	 * @param idUser
	 * @return List<Integer>
	 */
	public List<Integer> createEventFor1050BReports(ContactDetailsDto contactDetailsDto, String program, Long idUser);

	/**
	 * This method gets called to insert a agency history entry when Save button
	 * clicked on the add screen (shown via click of Add button under agency
	 * history <Licensing - Prior Verification Dates: section>).
	 *
	 * @param InsertAllegReq
	 * @return Long.
	 */
	public Long insertAllegation(InsertAllegReq insertAllegReq);

	/**
	 * This method gets called to insert a agency history entry when Save button
	 * clicked on the add screen (shown via click of Add button under agency
	 * history <Licensing - Prior Verification Dates: section>).
	 *
	 * @param InsertAllegReq
	 *
	 * @return Long .
	 */
	public Long saveCpa(SaveCpaReq insertCpaReq);

	/**
	 * Method to add/update/delete Contact person information. The operation
	 * type (A U D) is already prepared in ContactSeachListDetailConversation
	 *
	 * @param contactPersonNarrList
	 * @param idEvent
	 * @param idCase
	 * @param connection
	 * @return Long
	 */
	public Long saveContactPersonNarrInfo(List<ContactPersonNarrValueDto> contactPersonNarrList, Long idEvent,
										  Long idCase);

	/**
	 * Method Name: isRmRsAddressExist Method Description:Fetch if Residence
	 * mailing or Residence address exists for a person
	 *
	 * @param personId
	 * @return boolean
	 */
	public boolean isRmRsAddressExist(long personId);

	/**
	 * Method Name: getContactType Method Description:This function returns
	 * Contac Type for the given Contact Event Id.
	 *
	 * @param idEvent
	 * @return String
	 */
	public String getContactType(long idEvent);

	/**
	 * Method Name: hasCF1050BRecord Method Description:This method checks if
	 * Non-End Dated 1050B Report exists for the given Stage and the Child.
	 *
	 * @param idStage
	 * @param idPerson
	 * @return Boolean
	 */
	public Boolean hasCF1050BRecord(Long idStage, Long idPerson);

	/**
	 * Method Name: fetchFacilityType Method Description:Get the operation type
	 * for the current stage.
	 *
	 * @param stageId
	 * @return String
	 */
	public String fetchFacilityType(long stageId);

	/**
	 * Method Name: fetchPersonInfo Method Description:Fetches the current Abuse
	 * and neglect bean info info for the IdStage
	 *
	 * @param stageId
	 * @return CFTRlsInfoRptValueDto @
	 */
	public CFTRlsInfoRptValueDto fetchPersonInfo(long stageId);

	/**
	 * Method Name: selectContact1050BReport Method Description:This method
	 * retrieves the information for Child Fatality 1050B Report from the
	 * database using Event Id.
	 *
	 * @param idEvent
	 * @param program
	 * @param operationType
	 * @return CFT1050BReportGetDto @
	 */
	public ContactDetailChildFatalityReportDto selectContact1050BReport(long idEvent, String program,
																		String operationType);

	/**
	 *
	 * Method Name: selectCodeTableRows Method Description: Method that returns
	 * the key/value pair info. used for dropdown list of - Reason Agency Home
	 * Verification Was Relinquished - Add prior history verification screen
	 * (agency history).
	 *
	 * @return Map<String,String> @
	 */
	public Map<String, String> selectCodeTableRows();

	/**
	 * Method Name: fetch1050BRlsInfoRpt Method Description:This function retuns
	 * 1050B RlsInfoRpt (Main Table) using idEvent
	 *
	 * @param idEvent
	 * @return CFTRlsInfoRptValueModBean @
	 */
	public CFTRlsInfoRptValueDto fetch1050BRlsInfoRpt(long idEvent);

	/**
	 * Method Name: getChildFatalityTaskCode Method Description: This method
	 * returns Child Fatality Task Code based on Program
	 *
	 * @param program
	 * @return CFTRlsInfoRptValueDto @
	 */
	public String getChildFatalityTaskCode(String program);

	/**
	 * Method Name: getContactCF06Doc Method Description: Retrieves Narrative
	 * blob and dtLastUpdate from database when Narrative is present. Csys06s
	 *
	 * @param CommonContactNarrativeReq
	 * @return GetContactCF06DocRes @
	 */
	public ContactDetailNarrativeRes getNarrative(CommonContactNarrativeReq getContactCF06DocReq);

	/**
	 * Method Name: isCrimHistCheckPending Method Description: Check if any DPS
	 * Criminal History check is pending
	 *
	 * @param idStage
	 * @return boolean @
	 */
	public boolean isCrimHistCheckPending(long idStage);

	/**
	 * Method Name: checkCrimHistAction Method Description: This method to get
	 * the idPerson if the Criminal History Action is null for the given
	 * Id_Stage.
	 *
	 * @param idStage
	 * @return HashMap @
	 */
	public HashMap checkCrimHistAction(long idStage);

	/**
	 *
	 * Method Name: deleteContactDtl Method Description: This method deletes the
	 * contact and related records
	 *
	 * @param csys07siDto
	 * @param cFT1050BReportDto
	 *
	 */
	public Long deleteContactDtl(ContactAUDDto csys07siDto, ChildFatalityContactDto cFT1050BReportDto);

	/**
	 * Method Name: contactAUD Method Description: Method to add/update/delete
	 * Contact guide information.
	 *
	 * @param saveContactReq
	 * @return ContactDetailsRes
	 */
	public List<ContactGuideDto> contactAUD(SaveGuideContactReq saveGuideContactReq);

	/**
	 * Method Name: saveContactDtls Method Description:This method saves the
	 * contact and related objects (event_person_link, guide topics, contact
	 * person narrative) etc.
	 *
	 * @param saveContactReq
	 * @return ContactDetailsRes
	 */
	public ChildContactDto saveContactDtls(SaveGuideContactReq saveGuideContactReq);

	/**
	 * Method Description: This method is used to retrieve the information for
	 * FBSS Closing Letter form by passing idPerson,idStage and Form Name as
	 * input request
	 *
	 *
	 * @param FbssClosingLetterReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getFbssClosingLetter(FbssClosingLetterReq fbssClosingLetterReq);

	/**
	 * Method Description: This method is used to return Flag which enables FBSS
	 * Closing Letter and FBSS Closing Spanish Letter dropdown in PersonDetails
	 * Page
	 *
	 * @param displayFbssClosingLetterReq
	 * @return boolean @
	 */
	public boolean displayFbssClosingLetter(DisplayFbssClosingLetterReq displayFbssClosingLetterReq);

	/**
	 * Method Name: getReasonRlngshmntCodes Method Description: Service method
	 * to get reasonRlngshmntCodes
	 *
	 * @return Map<String, String>
	 */
	Map<String, String> getReasonRlngshmntCodes();

	/**
	 * Gets the contact other name.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the contact other name
	 */
	public SaveContactRes getContactOtherName(Long idEvent);

	/**
	 * Update contact other name.
	 *
	 * @param idEvent
	 *            the id event
	 * @param otherContactName
	 *            the other contact name
	 * @return the save contact res
	 */
	public SaveContactRes updateContactOtherName(Long idEvent, String otherContactName);

	/**
	 * ADS Service Name: sendAlerToPrimary . Method Description: This method is
	 * used to send an Alert to the Primary assigned staff when the LPS Worker
	 * initially saves a Contact with a purpose of “CVS Monthly Required (FTF)”
	 *
	 * @param ContactReq
	 * @return SaveContactRes
	 */
	public SaveContactRes sendAlerToPrimary(ContactReq contactReq);

	/**
	 * Method Name: getErrorMessage Method Description:This method Generates
	 * error messages for GOLD Gua Ref on contact launch.Before cbgr and ccgr
	 * redirect to the Save routine, stop with error messages from ccon31s if
	 * required fields are null.
	 *
	 * @param contactReq
	 * @return List<Integer>
	 */
	public List<Integer> getErrorMessage(ContactReq contactReq);

	/**
	 *Method Name:	searchExtOrCommencementContact
	 *Method Description:
	 *@param contactSearchReq
	 *@return
	 */
	public ContactSearchRes searchExtOrCommencementContact(ContactSearchReq contactSearchReq);

	/**
	 * Method Description: This method gets the Narrative
	 * Count from RISK_ASSMT_IRA_NARR and RISK_ASSMT_NARR which helps to set the DocType
	 * for Structured Narrative
	 *
	 * @param ContactSearchReq
	 * @return String
	 */
	//Warranty Defect - 11243 - To display both Versions of Structured Narrative RISKSF and CIV33O00
	public String getRiskNarrExists(ContactSearchReq contactSearchReq);

	/**
	 * artf258107
	 * Method to get the last updated timestamp of a contact based on the event id
	 * @param commonHelperReq
	 * @return
	 */
	CommonHelperRes getContactDtLastUpdateByEventId(CommonHelperReq commonHelperReq);

	CommonHelperRes getEarliestContactDate(CommonHelperReq commonHelperReq);

	public ContactDto getContactById(Long eventId);

	StageValueBeanDto getStageByStageId(Long idStage);

	public Integer getCountOFContactsInStage(Long idStage);
	public ContactSearchRes searchContactsForAPIPagination(ContactSearchReq contactSearchReq,int offset,int pageSize );

	StageSearchRes stageSearch(StageSearchReq stageSearchBean);

	ConGuideFetchOutDto getContactFollowupDetails(Long idEvent);

	public InrFollowupPendingRes getFollowupPending(InrFollowupPendingReq stageSearchReq);
}
