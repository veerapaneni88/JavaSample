package us.tx.state.dfps.service.contacts.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.common.web.bean.StageSearchBean;
import us.tx.state.dfps.service.casepackage.dto.ContactNarrativeDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.ContactSearchListDto;
import us.tx.state.dfps.service.contactnarrlog.Service.ContactNarrLogAppendService;
import us.tx.state.dfps.service.contacts.service.*;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ConGuideFetchInDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;
import us.tx.state.dfps.web.contact.bean.ContactNocPersonDetailDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactFieldDiDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Implements
 * methods from ContactGuideBean and ContactBean. Sep 6, 2017- 9:27:34 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Api(tags = { "contacts" })
@RestController
@RequestMapping("/contacts")
public class ContactDetailsController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ContactDetailsService contactDetailsService;


	@Autowired
	ContactNocPersonDetailsService contactNocPersonDetailsService;

	@Autowired
	ContactGuideService contactGuideService;

	@Autowired
	StageClosureEventService stageClosureEventService;

	@Autowired
	ContactCVSService contactCVSService;

	@Autowired
	ContactNarrLogAppendService contactNarrLogAppendService;

	/**
	 *
	 * Method Name: getContactCVS Method Description: CVS Monthly Evaluation :
	 * CVSEVAL form Name: cvseval
	 *
	 * @param cpsInvConclValReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getContactCVS", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getContactCVS(@RequestBody CpsInvConclValReq cpsInvConclValReq) {
		if (ObjectUtils.isEmpty(cpsInvConclValReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("CpsInvConclValReq.idStage.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(cpsInvConclValReq.getIdCase()))
			throw new InvalidRequestException(
					messageSource.getMessage("CpsInvConclValReq.idCase.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(cpsInvConclValReq.getIdEvent()))
			throw new InvalidRequestException(
					messageSource.getMessage("CpsInvConclValReq.idEvent.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(cpsInvConclValReq.getHiddenField()))
			throw new InvalidRequestException(
					messageSource.getMessage("CpsInvConclValReq.hiddenField.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(contactCVSService.getContactCVS(cpsInvConclValReq)));

		return commonFormRes;

	}

	/**
	 *
	 * Method Name: completeCFInfoTasks Method Description: complete the CF info
	 * tasks
	 *
	 * @param commonEventIdReq
	 * @return SaveContactRes
	 */
	@RequestMapping(value = "/completeCFInfoTasks", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SaveContactRes completeCFInfoTasks(@RequestBody CommonEventIdReq commonEventIdReq) {

		if (TypeConvUtil.isNullOrEmpty(commonEventIdReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		SaveContactRes saveContactRes = new SaveContactRes();
		try {
			saveContactRes = contactDetailsService.completeCFInfoTasks(commonEventIdReq);
		} catch (DataNotFoundException e) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException(
					messageSource.getMessage("contacts.completeCFInfoTasks.noDataFound", null, Locale.US));
			dataNotFoundException.initCause(e);
			throw dataNotFoundException;
		}
		return saveContactRes;
	}

	/**
	 * Method Name: reviewComplete Method Description: This method allows to
	 * complete review when type CSS Review is selected in contact details page.
	 *
	 * @param saveContactReq
	 * @return SaveContactRes
	 */
	@RequestMapping(value = "/reviewComplete", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SaveContactRes reviewComplete(@RequestBody SaveContactReq saveContactReq) {
		if (TypeConvUtil.isNullOrEmpty(saveContactReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return contactDetailsService.reviewComplete(saveContactReq);
	}

	/**
	 * Method Name: updateContactOtherName Complete Method Description: This
	 * method update other contact name
	 *
	 * @param updateContactOtherName
	 * @return SaveContactRes
	 */
	@RequestMapping(value = "/updateContactOtherName", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SaveContactRes updateContactOtherName(@RequestBody SaveContactReq saveContactReq) {
		if (TypeConvUtil.isNullOrEmpty(saveContactReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(saveContactReq.getOtherContactName())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.othercontactname.mandatory", null, Locale.US));
		}
		return contactDetailsService.updateContactOtherName(saveContactReq.getIdEvent(),
				saveContactReq.getOtherContactName());
	}

	/**
	 * Method Name: getContactOtherName Complete Method Description: This method
	 * update other contact name
	 *
	 * @param updateContactOtherName
	 * @return SaveContactRes
	 */
	@RequestMapping(value = "/getContactOtherName", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SaveContactRes getContactOtherName(@RequestBody SaveContactReq saveContactReq) {
		if (TypeConvUtil.isNullOrEmpty(saveContactReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		return contactDetailsService.getContactOtherName(saveContactReq.getIdEvent());
	}

	/**
	 * Method Name: searchContact Method Description: method to retriev list of
	 * contacts
	 *
	 * @param saveContactReq
	 * @return ContactSearchRes
	 */

	@RequestMapping(value = "/searchcontact", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactSearchRes searchContacts(@RequestBody ContactSearchReq contactSearchReq) {
		if (TypeConvUtil.isNullOrEmpty(contactSearchReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return contactDetailsService.searchContacts(contactSearchReq);
	}

	/**
	 * Method Name: searchContact Method Description: method to retriev list of
	 * contacts
	 *
	 * @param saveContactReq
	 * @return boolean
	 */

	@RequestMapping(value = "/getnarrativeexists", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  boolean getNarrativeExists(@RequestBody ContactSearchReq contactSearchReq) {

		return contactDetailsService.indStructNarrExists(contactSearchReq);
	}

	/**
	 * Method Name: saveContact Method Description:This method for now will be
	 * used for Kinship Notification contact but subsequently other contacts
	 * will be transferred over to this service
	 *
	 * @param saveContactReq
	 * @return ContactDetailsRes
	 */

	@RequestMapping(value = "/saveContact", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactDetailsRes saveContact(@RequestBody ContactDetailReq contactDetailReq){
		ContactDetailsRes contactDetailsRes = new ContactDetailsRes();
		contactDetailsRes.setIdContactEvt(contactDetailsService.saveContact(contactDetailReq));
		return contactDetailsRes;
	}

	/**
	 * Method Name: updateContact Method Description: Method to
	 * add/update/delete Contact guide information.
	 *
	 * @param contactGuideReq
	 * @return ContactGuideRes
	 */
	@RequestMapping(value = "/updateContact", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactGuideRes updateContact(@RequestBody ContactGuideReq contactGuideReq){
		if (TypeConvUtil.isNullOrEmpty(contactGuideReq.getContactDetailDto().getContactGuideList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("contacts.contactGuideList.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(contactGuideReq.getContactDetailDto().getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.CONTACT_ID_EVENT_CHECK, null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(contactGuideReq.getContactDetailDto().getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("contacts.idCase.mandatory", null, Locale.US));
		}

		ContactGuideRes contactGuideRes = new ContactGuideRes();
		contactGuideRes.setContactDetailDto(contactGuideService.updatecontact(contactGuideReq.getContactDetailDto()));

		return contactGuideRes;

	}

	/**
	 * Method Name: deleteGuidePlanInfo Method Description: Method to delete the
	 * Contact guide information when Contact is deleted.
	 *
	 * @param contactGuideReq
	 * @return ContactGuideRes
	 */
	@RequestMapping(value = "/deleteGuidePlanInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ContactGuideRes deleteGuidePlanInfo(@RequestBody ContactGuideReq contactGuideReq) {
		if (TypeConvUtil.isNullOrEmpty(contactGuideReq.getContactDetailDto().getContactGuideList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("contacts.contactGuideList.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(contactGuideReq.getContactDetailDto().getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.CONTACT_ID_EVENT_CHECK, null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(contactGuideReq.getContactDetailDto().getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("contacts.idCase.mandatory", null, Locale.US));
		}

		ContactGuideRes contactGuideRes = new ContactGuideRes();
		contactGuideRes
				.setContactDetailDto(contactGuideService.deleteGuidePlanInfo(contactGuideReq.getContactDetailDto()));

		return contactGuideRes;

	}

	/**
	 * Method Name: create1050BEvent Method Description: This method creates
	 * 1050B Report Event.
	 *
	 * @param contactEventReq
	 * @return ContactEventRes
	 */
	@RequestMapping(value = "/create1050BEvent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactEventRes create1050BEvent(@RequestBody ContactEventReq contactEventReq) {
		ContactEventRes contactEventRes = new ContactEventRes();

		if (TypeConvUtil.isNullOrEmpty(contactEventReq.getIdUser())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.CONTACT_ID_USER_CHECK, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(contactEventReq.getEventValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("contacts.eventValueBean.mandatory", null, Locale.US));
		}

		long result = contactDetailsService.create1050BEvent(contactEventReq.getEventValueDto(),
				contactEventReq.getIdUser());
		contactEventRes.setIdUser(contactEventReq.getIdUser());
		contactEventRes.setEventValueDto(contactEventReq.getEventValueDto());
		contactEventRes.setIdContactEvt(result);
		return contactEventRes;
	}

	/**
	 * Method Name: create1050BRejAlert Method Description: This function
	 * creates Rejection Alert for 1050B Author.
	 *
	 *
	 * @param contactEventReq
	 * @return ContactRejAlertRes
	 */
	@RequestMapping(value = "/create1050BRejAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ContactRejAlertRes create1050BRejAlert(@RequestBody ContactRejAlertReq contactRejAlertReq) {
		ContactRejAlertRes contactRejAlertRes = new ContactRejAlertRes();

		if (TypeConvUtil.isNullOrEmpty(contactRejAlertReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.CONTACT_ID_EVENT_CHECK, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(contactRejAlertReq.getIdUser())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.CONTACT_ID_USER_CHECK, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(contactRejAlertReq.getProgram())) {
			throw new InvalidRequestException(messageSource.getMessage("contacts.program.mandatory", null, Locale.US));
		}

		contactDetailsService.create1050BRejAlert(contactRejAlertReq.getIdEvent(), contactRejAlertReq.getIdUser(),
				contactRejAlertReq.getProgram());
		contactRejAlertRes.setIdEvent(contactRejAlertReq.getIdEvent());
		contactRejAlertRes.setIdUser(contactRejAlertReq.getIdUser());
		contactRejAlertRes.setProgram(contactRejAlertReq.getProgram());

		return contactRejAlertRes;
	}

	/**
	 * Method Name: Method Description: This function creates Rejection Task for
	 * 1050B Author.
	 *
	 *
	 * @param contactRejAlertReq
	 * @return ContactRejAlertRes
	 */
	@RequestMapping(value = "/create1050BRejectionTask", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ContactRejAlertRes create1050BRejectionTask(
			@RequestBody ContactRejAlertReq contactRejAlertReq) {
		ContactRejAlertRes contactRejAlertRes = new ContactRejAlertRes();

		if (TypeConvUtil.isNullOrEmpty(contactRejAlertReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.CONTACT_ID_EVENT_CHECK, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(contactRejAlertReq.getIdUser())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.CONTACT_ID_USER_CHECK, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(contactRejAlertReq.getProgram())) {
			throw new InvalidRequestException(messageSource.getMessage("contacts.program.mandatory", null, Locale.US));
		}

		contactDetailsService.create1050BRejectionTask(contactRejAlertReq.getIdEvent(), contactRejAlertReq.getIdUser(),
				contactRejAlertReq.getProgram());
		contactRejAlertRes.setIdEvent(contactRejAlertReq.getIdEvent());
		contactRejAlertRes.setIdUser(contactRejAlertReq.getIdUser());
		contactRejAlertRes.setProgram(contactRejAlertReq.getProgram());

		return contactRejAlertRes;
	}

	/**
	 * Method Name:update1050BEvent Method Description: This method updates
	 * 1050B Report Event.
	 *
	 * EJB Name : ContactBean.java
	 *
	 * @param contactU1050BReq
	 * @return ContactU1050BRes
	 */
	@RequestMapping(value = "/update1050BEvent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactChildFatalityLicRes update1050BEvent(
			@RequestBody CommonChildFatalityReq contactU1050BReq) {

		ContactChildFatalityLicRes contactU1050BRes = new ContactChildFatalityLicRes();

		if (TypeConvUtil.isNullOrEmpty(contactU1050BReq.getCft1050bReportDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("contacts.CFT1050BReportDto.mandatory", null, Locale.US));
		}

		contactDetailsService.update1050BEvent(contactU1050BReq.getCft1050bReportDto());
		contactU1050BRes.setCft1050bReportDto(contactU1050BReq.getCft1050bReportDto());
		return contactU1050BRes;
	}

	/**
	 * Method Name:saveCFTRlsInfoRptCPS Method Description: Saves the
	 * priorHistory/current /other Information for the stage
	 *
	 * @param saveCFTRReq
	 * @return SaveCFTRRes
	 */
	@RequestMapping(value = "/saveCFTRlsInfoRptCPS", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SaveCFTRRes saveCFTRlsInfoRptCPS(@RequestBody SaveCFTRReq saveCFTRReq) {
		SaveCFTRRes saveCFTRRes = new SaveCFTRRes();

		if (TypeConvUtil.isNullOrEmpty(saveCFTRReq.getCftRlsInfoRptCPSValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("contacts.CftRlsInfoRptCPAValueDto.mandatory", null, Locale.US));
		}

		saveCFTRRes.setCftRlsInfoRptCPSValueDto(
				contactDetailsService.saveCFTRlsInfoRptCPS(saveCFTRReq.getCftRlsInfoRptCPSValueDto()));
		return saveCFTRRes;

	}

	/**
	 * Method Name: deleteCFTRlsInfoRptCPS Method Description: This method will
	 * delete row(s) from A/N or Agency section from the CF Info for Public
	 * Release data entry page.
	 *
	 * @param cFTRlsInfoRptCPSReq
	 * @return CFTRlsInfoRptCPSRes
	 */
	@RequestMapping(value = "/deleteCFTRlsInfoRptCPS", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CFTRlsInfoRptCPSRes deleteCFTRlsInfoRptCPS(
			@RequestBody CFTRlsInfoRptCPSReq cFTRlsInfoRptCPSReq) {
		CFTRlsInfoRptCPSRes cFTRlsInfoRptCPSRes = new CFTRlsInfoRptCPSRes();

		if (TypeConvUtil.isNullOrEmpty(cFTRlsInfoRptCPSReq.getIdFtRlsInfoRptCPS())) {
			throw new InvalidRequestException(
					messageSource.getMessage("contacts.idFtRlsInfoRptCPS.mandatory", null, Locale.US));
		}

		cFTRlsInfoRptCPSRes.setIdFtRlsInfoRptCPS(
				contactDetailsService.deleteCFTRlsInfoRptCPS(cFTRlsInfoRptCPSReq.getIdFtRlsInfoRptCPS()));
		return cFTRlsInfoRptCPSRes;

	}

	/**
	 * Method Name: deleteLICHistory Method Description: Deletes the
	 * priorHistory /other Information for the stage
	 *
	 * @param contactU1050BReq
	 * @return ContactU1050BRes
	 */
	@RequestMapping(value = "/deleteLICHistory", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactChildFatalityLicRes deleteLICHistory(
			@RequestBody CommonChildFatalityReq contactU1050BReq) {

		ContactChildFatalityLicRes contactU1050BRes = new ContactChildFatalityLicRes();

		if (TypeConvUtil.isNullOrEmpty(contactU1050BReq.getCft1050bReportDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("contacts.CFT1050BReportDto.mandatory", null, Locale.US));
		}

		contactU1050BRes
				.setCft1050bReportDto(contactDetailsService.deleteLICHistory(contactU1050BReq.getCft1050bReportDto()));
		return contactU1050BRes;
	}

	/**
	 * Method Name: Method Description: This method is used to
	 * createExtensionAprRejAlert method
	 *
	 * @param contactReq
	 * @return ContactRes
	 */
	@RequestMapping(value = "/createextension", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactRes createExtensionAprRejAlert(@RequestBody ContactReq contactReq) {

		ContactRes contactRes = new ContactRes();
		Long contact = contactDetailsService.createExtensionAprRejAlert(contactReq.getIdEvent(),
				contactReq.getApprovalLen(), contactReq.getApprovalStatus());
		contactRes.setContact(contact);
		return contactRes;

	}

	/**
	 * Method Name: Method Description: This method is used to
	 * createEventFor1050BReports method This method creates 1050B Report Event
	 * (and Contact) for each Child Fatality in the given Stage.
	 *
	 * @param contactCreateEventReq
	 * @return ContactCreateEventRes
	 */
	@RequestMapping(value = "/createEventFor1050BReports", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ContactCreateEventRes createEventFor1050BReports(
			@RequestBody ContactCreateEventReq contactCreateEventReq){
		ContactCreateEventRes contactCreateEventRes = new ContactCreateEventRes();
		List<Integer> event = contactDetailsService.createEventFor1050BReports(
				contactCreateEventReq.getContactDetailsDto(), contactCreateEventReq.getProgram(),
				contactCreateEventReq.getIdUser());
		contactCreateEventRes.setEventInt(event);
		return contactCreateEventRes;

	}

	/**
	 * Method Name:contactDetailAUD Method Description:This method is used to
	 * the Add/Update/Delete contact and related records
	 *
	 * @param contactDetailReq
	 * @return ContactAUDRes
	 */
	@RequestMapping(value = "/contactDetailAUD", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactAUDRes contactDetailAUD(@RequestBody ContactDetailReq contactDetailReq){
		ContactAUDRes contactAUDRes = contactDetailsService.contactDetailAud(contactDetailReq.getContactAUDDto(),
				contactDetailReq.getContactGuideList(), contactDetailReq.getContactPersonNarrList());


		return contactAUDRes;

	}

  //	fetchContactNocPersonDetail
   @RequestMapping(value = "/fetchContactNocPersonDetail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
   public ContactNocPersonResp getContactNocPersonDetails(@RequestBody ContactNocPersonDetailDto contactNocPersonDetailDto){
	ContactNocPersonResp contactNocPersonResp = new ContactNocPersonResp();
	List<ContactNocPersonDetailDto> contactNocPersonList =  contactNocPersonDetailsService.getContactNocPersonDetails(contactNocPersonDetailDto);
	contactNocPersonResp.setContactNocPersonDetailDtoList(contactNocPersonList);
	return contactNocPersonResp;

}

	@RequestMapping(value = "/contactNocPersonDetailAUD", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ContactNocPersonAUDRes contactNocPersonDetailAUD(@RequestBody ContactNocPersonReq contactNocPersonReq){
		ContactNocPersonAUDRes contactNocPersonAUDRes = new ContactNocPersonAUDRes();
		List<Long> savedIds =  contactNocPersonDetailsService.saveContactNocPersonDetailRecord(contactNocPersonReq);
		return contactNocPersonAUDRes;

	}



	/**
	 *
	 * Method Name: saveCpa Method Description: controller method to save cpa in
	 * to FT_RLS_INFO_RPT_CPA table
	 *
	 * @param SaveCpaReq
	 * @return ContactSearchRes
	 */
	@RequestMapping(value = "/saveCpa", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactDetailsRes saveCpa(@RequestBody SaveCpaReq saveCpaReq) {
		ContactDetailsRes contactDetailsRes = new ContactDetailsRes();
		contactDetailsRes.setUpdateResult(contactDetailsService.saveCpa(saveCpaReq));
		return contactDetailsRes;

	}

	/**
	 * Method Name: insertAllegation Method Description: This method gets called
	 * to insert a agency history entry when Save button clicked on the add
	 * screen (shown via click of Add button under agency history <Licensing -
	 * Prior Verification Dates: section>).
	 *
	 * @param insertAllegReq
	 * @return
	 */
	@RequestMapping(value = "/insertAllegation", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactDetailsRes insertAllegation(@RequestBody InsertAllegReq insertAllegReq){
		ContactDetailsRes contactDetailsRes = new ContactDetailsRes();
		// Updating response
		contactDetailsRes.setUpdateResult(contactDetailsService.insertAllegation(insertAllegReq));
		return contactDetailsRes;
	}

	/**
	 * Method Name: fetchGuideTopicDescr Method Description: Method retrieves
	 * Guide Topic Description
	 *
	 * @return ContactGuideRes
	 */
	@RequestMapping(value = "/fetchGuideTopicDescr", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ContactFetchRes fetchGuideTopicDescr(@RequestBody ContactFetchReq contactFetchReq) {
		ContactFetchRes contactFetchRes = new ContactFetchRes();
		contactFetchRes.setContactFetchDtos(contactGuideService.fetchGuideTopicDescr());

		return contactFetchRes;

	}

	/**
	 * Method Name: checkIfGuideNarrExists Method Description: Method to check
	 * if Contact Guide Narrative records exist for a given Contact.
	 *
	 * @param contactGuideReq
	 * @return ContactGuideRes
	 */
	@RequestMapping(value = "/checkIfGuideNarrExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ContactCheckRes checkIfGuideNarrExists(@RequestBody ContactRejAlertReq contactRejAlertReq) {
		if (TypeConvUtil.isNullOrEmpty(contactRejAlertReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.CONTACT_ID_EVENT_CHECK, null, Locale.US));
		}
		ContactCheckRes contactCheckRes = new ContactCheckRes();
		// contactCheckRes.setIndicator(contactGuideService.checkIfGuideNarrExists(contactRejAlertReq.getIdEvent()));
		return contactCheckRes;
	}

	/**
	 * Method Name: fetchContactGuideList Method Description:Method to retrieve
	 * all the Contact guide information for saved records to create a List of
	 * Contact Guide value beans for a new contact.
	 *
	 * @param fetchContactGuideReq
	 * @return FetchContactGuideRes
	 */
	@RequestMapping(value = "/fetchContactGuideList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FetchContactGuideRes fetchContactGuideList(
			@RequestBody FetchContactGuideReq fetchContactGuideReq){

		if (TypeConvUtil.isNullOrEmpty(fetchContactGuideReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(fetchContactGuideReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		FetchContactGuideRes fetchContactGuideRes = new FetchContactGuideRes();
		fetchContactGuideRes.setContactGuideDtos(contactGuideService.fetchContactGuideList(fetchContactGuideReq));
		return fetchContactGuideRes;

	}

	/**
	 * Method Name: fetchContactPeronNarrList Method Description:This method
	 * fetches the contact person narrative information for contact of type
	 * KinshipNotification. The contact_person_narr table stores the information
	 * when a person is not notified explicitly. This method builds the list
	 * based on stage_person_link, and if record exist in contact_person_narr
	 * table, information is included.
	 *
	 * @param fetchContactGuideReq
	 * @return FetchContactPersNarrRes
	 */
	@RequestMapping(value = "/fetchContactPeronNarrList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FetchContactPersNarrRes fetchContactPeronNarrList(
			@RequestBody FetchContactGuideReq fetchContactGuideReq) {
		FetchContactPersNarrRes fetchContactPersNarrRes = new FetchContactPersNarrRes();
		fetchContactPersNarrRes.setContactPersonNarrValueDtos(
				contactDetailsService.fetchContactPeronNarrList(fetchContactGuideReq.getContactedList(),
						fetchContactGuideReq.getIdStage(), fetchContactGuideReq.getIdEvent()));
		return fetchContactPersNarrRes;

	}

	/**
	 * Method Name: isRmRsAddressExist Method Description:Fetch if Residence
	 * mailing or Residence address exists for a person
	 *
	 * @param kinPlacementInfoReq
	 * @return ContactCheckRes
	 */
	@RequestMapping(value = "/isRmRsAddressExist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactCheckRes isRmRsAddressExist(@RequestBody KinPlacementInfoReq kinPlacementInfoReq) {

		if (TypeConvUtil.isNullOrEmpty(kinPlacementInfoReq.getIdperson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		ContactCheckRes contactCheckRes = new ContactCheckRes();
		contactCheckRes.setIndicator(contactDetailsService.isRmRsAddressExist(kinPlacementInfoReq.getIdperson()));
		return contactCheckRes;

	}

	/**
	 * Method Name: getContactType Method Description:This function returns
	 * Contac Type for the given Contact Event Id.
	 *
	 * @param contactRejAlertReq
	 * @return ContactTypeRes
	 */
	@RequestMapping(value = "/getContactType", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactTypeRes getContactType(@RequestBody ContactRejAlertReq contactRejAlertReq) {

		if (TypeConvUtil.isNullOrEmpty(contactRejAlertReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		ContactTypeRes contactTypeRes = new ContactTypeRes();
		contactTypeRes.setContactType(contactDetailsService.getContactType(contactRejAlertReq.getIdEvent()));
		return contactTypeRes;

	}

	/**
	 * Method Name: hasCF1050BRecord Method Description:This method checks if
	 * Non-End Dated 1050B Report exists for the given Stage and the Child.
	 *
	 * @param searchStageReq
	 * @return ContactCheckRes
	 */
	@RequestMapping(value = "/hasCF1050BRecord", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactCheckRes hasCF1050BRecord(@RequestBody SearchStageReq searchStageReq) {

		if (TypeConvUtil.isNullOrEmpty(searchStageReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		ContactCheckRes contactCheckRes = new ContactCheckRes();
		contactCheckRes.setIndicator(
				contactDetailsService.hasCF1050BRecord(searchStageReq.getIdStage(), searchStageReq.getIdPerson()));
		return contactCheckRes;

	}

	/**
	 * Method Name: getCFTChildrenForStage Method Description:This method checks
	 * returns all the deceased Chidlrenin the given Stage. Checks for following
	 * Conditions.
	 *
	 * 1. Allegation Record present for the Child with IND_FATALITY = 'Y'. 2.
	 * Same Child should have Date of Death in Person Table.
	 *
	 * @param searchStageReq
	 * @return IDListRes
	 */
	@RequestMapping(value = "/getCFTChildrenForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  IDListRes getCFTChildrenForStage(@RequestBody SearchStageReq searchStageReq) {

		if (TypeConvUtil.isNullOrEmpty(searchStageReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		IDListRes idListRes = new IDListRes();
		idListRes.setIdList(contactDetailsService.getCFTChildrenForStage(searchStageReq.getIdStage()));
		return idListRes;
	}

	/**
	 * Method Name: fetchCurrAbuseNglctInfo Method Description:Fetches the
	 * current Abuse and neglect bean info info for the IdStage
	 *
	 * @param cpsInvCnclsnReq
	 * @return SaveCFTRRes
	 */
	@RequestMapping(value = "/fetchCurrAbuseNglctInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SaveCFTRRes fetchCurrAbuseNglctInfo(@RequestBody CpsInvCnclsnReq cpsInvCnclsnReq){

		if (TypeConvUtil.isNullOrEmpty(cpsInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(cpsInvCnclsnReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}

		SaveCFTRRes saveCFTRRes = new SaveCFTRRes();
		saveCFTRRes.setCftRlsInfoRptCPSValueDto(contactDetailsService
				.fetchCurrAbuseNglctInfo(cpsInvCnclsnReq.getIdStage(), cpsInvCnclsnReq.getIdCase()));
		return saveCFTRRes;
	}

	/**
	 * Method Name: fetchPriorHistoryInfo Method Description:Fetches the
	 * priorHistory Information for the stage
	 *
	 * @param searchStageReq
	 * @return CFTRlsInfoRes
	 */
	@RequestMapping(value = "/fetchPriorHistoryInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CFTRlsInfoRes fetchPriorHistoryInfo(@RequestBody SearchStageReq searchStageReq){

		if (TypeConvUtil.isNullOrEmpty(searchStageReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(searchStageReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		CFTRlsInfoRes cftRlsInfoRes = new CFTRlsInfoRes();
		cftRlsInfoRes.setCftRlsInfoRptCPSValueDtos(
				contactDetailsService.fetchPriorHistoryInfo(searchStageReq.getIdStage(), searchStageReq.getIdPerson()));
		return cftRlsInfoRes;

	}

	/**
	 * Method Name: fetchChildFatalityInfo Method Description:Fetches Child
	 * Fatality Information
	 *
	 * @param contactFetchReq
	 * @return ContactU1050BRes
	 */
	@RequestMapping(value = "/fetchChildFatalityInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ContactChildFatalityLicRes fetchChildFatalityInfo(@RequestBody ContactFetchReq contactFetchReq){

		if (TypeConvUtil.isNullOrEmpty(contactFetchReq.getStageId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(contactFetchReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(contactFetchReq.getIdUser())) {
			throw new InvalidRequestException(messageSource.getMessage("contacts.idUser.mandatory", null, Locale.US));
		}

		ContactChildFatalityLicRes contactU1050BRes = new ContactChildFatalityLicRes();
		contactU1050BRes.setContactDetailCFT1050BReportDB(contactDetailsService.fetchChildFatalityInfo(
				contactFetchReq.getPersonId(), contactFetchReq.getStageId(), contactFetchReq.getIdUser()));
		return contactU1050BRes;

	}

	/**
	 * Method Name: fetchFacilityType Method Description:Get the operation type
	 * for the current stage.
	 *
	 * @param contactFetchReq
	 * @return FacilityTypeRes
	 */
	@RequestMapping(value = "/fetchFacilityType", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityTypeRes fetchFacilityType(@RequestBody ContactFetchReq contactFetchReq){

		if (TypeConvUtil.isNullOrEmpty(contactFetchReq.getStageId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		FacilityTypeRes facilityTypeRes = new FacilityTypeRes();
		facilityTypeRes.setFacilityType(contactDetailsService.fetchFacilityType(contactFetchReq.getStageId()));
		return facilityTypeRes;

	}

	/**
	 * Method Name: fetchPersonInfo Method Description: Fetches the current
	 * Abuse and neglect bean info info for the IdStage
	 *
	 * @param contactFetchReq
	 * @return CFTRlsInfoRptRes
	 */
	@RequestMapping(value = "/fetchPersonInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CFTRlsInfoRptRes fetchPersonInfo(@RequestBody ContactFetchReq contactFetchReq) {

		if (TypeConvUtil.isNullOrEmpty(contactFetchReq.getStageId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		CFTRlsInfoRptRes cftRlsInfoRptRes = new CFTRlsInfoRptRes();
		cftRlsInfoRptRes.setCftRlsInfoRptValueDto(contactDetailsService.fetchPersonInfo(contactFetchReq.getStageId()));
		return cftRlsInfoRptRes;

	}

	/**
	 *
	 * Method Name: selectContact1050BReport Method Description:This method
	 * retrieves the information for Child Fatality 1050B Report from the
	 * database using Event Id.
	 *
	 * @param contactRejAlertReq
	 * @return CFT1050BReportFetchRes
	 */
	/*
	 * @RequestMapping(value = "/selectContact1050BReport", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  ChildFatalityReportRes selectContact1050BReport(
	 *
	 * @RequestBody ContactRejAlertReq contactRejAlertReq) {
	 * log.info(ServiceConstants.TRANSACTION_ID +
	 * contactRejAlertReq.getTransactionId()); // throwing the error message if
	 * the input doesn't have id event if
	 * (TypeConvUtil.isNullOrEmpty(contactRejAlertReq.getIdEvent())) { throw new
	 * InvalidRequestException(messageSource.getMessage(
	 * "common.eventid.mandatory", null, Locale.US)); }// throwing the error
	 * message if the input doesn't have program if
	 * (TypeConvUtil.isNullOrEmpty(contactRejAlertReq.getProgram())) { throw new
	 * InvalidRequestException(
	 * messageSource.getMessage("common.stageProgramType.mandatory", null,
	 * Locale.US)); } ChildFatalityReportRes cft1050BReportFetchRes = new
	 * ChildFatalityReportRes();
	 * cft1050BReportFetchRes.setcFT1050BReportFetchDto(contactDetailsService
	 * .selectContact1050BReport(contactRejAlertReq.getIdEvent(),
	 * contactRejAlertReq.getProgram()));
	 *
	 * return cft1050BReportFetchRes; }
	 */
	/**
	 *
	 * Method Name: selectContact1050BReport Method Description:This method
	 * retrieves the information for Child Fatality 1050B Report from the
	 * database using Event Id.
	 *
	 * @param contactGetRejAlertReq
	 * @return CFT1050BReportGetRes
	 */
	@RequestMapping(value = "/selectContact1050BReport", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonContactCFReportRes selectContact1050BReport(
			@RequestBody ContactGetRejAlertReq contactGetRejAlertReq) {
		// throwing the error message if the input doesn't have id event
		if (TypeConvUtil.isNullOrEmpty(contactGetRejAlertReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		} // throwing the error message if the input doesn't have program
		if (TypeConvUtil.isNullOrEmpty(contactGetRejAlertReq.getProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.stageProgramType.mandatory", null, Locale.US));
		}
		CommonContactCFReportRes cft1050bReportGetRes = new CommonContactCFReportRes();
		// calling the service method to get the report for 1050B report
		cft1050bReportGetRes.setcFT1050BReportGetDto(
				contactDetailsService.selectContact1050BReport(contactGetRejAlertReq.getIdEvent(),
						contactGetRejAlertReq.getProgram(), contactGetRejAlertReq.getOperationType()));
		return cft1050bReportGetRes;
	}

	/**
	 *
	 * Method Name: selectCodeTableRows Method Description:Method that returns
	 * the key/value pair info. used for dropdown list of - Reason Agency Home
	 * Verification Was Relinquished - Add prior history verification screen
	 * (agency history).
	 *
	 * @param contactGetRejAlertReq
	 * @return FacilityTypeRes
	 */
	@RequestMapping(value = "/selectCodeTableRows", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityTypeRes selectCodeTableRows(@RequestBody ContactGetRejAlertReq contactGetRejAlertReq) {

		FacilityTypeRes facilityTypeRes = new FacilityTypeRes();
		facilityTypeRes.setCodeTableRows(contactDetailsService.selectCodeTableRows());
		return facilityTypeRes;
	}

	/**
	 *
	 * Method Name: fetch1050BRlsInfoRpt Method Description:This function retuns
	 * 1050B RlsInfoRpt (Main Table) using idEvent
	 *
	 * @param contactGetRejAlertReq
	 * @return CFTRlsInfoRptRes
	 */
	@RequestMapping(value = "/fetch1050BRlsInfoRpt", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CFTRlsInfoRptRes fetch1050BRlsInfoRpt(
			@RequestBody ContactGetRejAlertReq contactGetRejAlertReq) {
		if (TypeConvUtil.isNullOrEmpty(contactGetRejAlertReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		CFTRlsInfoRptRes cftRlsInfoRptRes = new CFTRlsInfoRptRes();

		cftRlsInfoRptRes.setCftRlsInfoRptValueDto(
				contactDetailsService.fetch1050BRlsInfoRpt(contactGetRejAlertReq.getIdEvent()));
		return cftRlsInfoRptRes;
	}

	/**
	 *
	 * Method Name: getChildFatalityTaskCode Method Description: This method
	 * returns Child Fatality Task Code based on Program
	 *
	 * @param contactGetRejAlertReq
	 * @return FacilityTypeRes
	 */
	@RequestMapping(value = "/getChildFatalityTaskCode", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FacilityTypeRes getChildFatalityTaskCode(
			@RequestBody ContactGetRejAlertReq contactGetRejAlertReq) {
		// throwing the error message if the input doesn't have program
		if (TypeConvUtil.isNullOrEmpty(contactGetRejAlertReq.getProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.programType.mandatory", null, Locale.US));
		}
		FacilityTypeRes facilityTypeRes = new FacilityTypeRes();

		facilityTypeRes.setTaskCode(contactDetailsService.getChildFatalityTaskCode(contactGetRejAlertReq.getProgram()));
		return facilityTypeRes;
	}

	/**
	 * Method Name: saveContactFor1050BReport Method Description: This method
	 * creates 1050B Report Event (and Contact) for each Child Fatality in the
	 * given Stage.
	 *
	 * @param saveContactFor1050Req
	 * @return ContactDetailsRes
	 */
	@RequestMapping(value = "/saveContactFor1050BReport", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ContactDetailsRes saveContactFor1050BReport(
			@RequestBody SaveChildFatalityContactReq saveContactFor1050Req) {
		// throwing the error message in case request doesn't have the CFT
		// Report
		if (TypeConvUtil.isNullOrEmpty(saveContactFor1050Req.getCft1050bReportDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("contacts.CFT1050BReportDto.mandatory", null, Locale.US));
		}
		ContactDetailsRes contactDetailsRes = new ContactDetailsRes();
		contactDetailsRes.setTransactionId(saveContactFor1050Req.getTransactionId());
		// calling the service to get the contact detail for a report
		contactDetailsRes.setIdContactEvt(
				contactDetailsService.saveContactFor1050BReport(saveContactFor1050Req.getCft1050bReportDto()));
		return contactDetailsRes;
	}

	/**
	 * Method Name: getContactDetailCFRes Method Description:This method get the
	 * contact details
	 *
	 * @param stageClosureEventReq
	 * @return StageClosureEventRes
	 */
	@RequestMapping(value = "/getContactDetailCFRes", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureEventRes getContactDetailCFRes(
			@RequestBody StageClosureEventReq stageClosureEventReq){
		// throwing the error message in case request doesn't have the ConGuide
		// Report
		if (TypeConvUtil.isNullOrEmpty(stageClosureEventReq.getConGuideFetchInDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("conGuideFetchInDto.mandatory", null, Locale.US));
		}
		if (!ObjectUtils.isEmpty(stageClosureEventReq.getUserId())) {
			stageClosureEventReq.getConGuideFetchInDto().setIdUser(Long.parseLong(stageClosureEventReq.getUserId()));
		}

		StageClosureEventRes stageClosureEventRes = new StageClosureEventRes();
		// calling the service to get the contact detail for a report
		stageClosureEventRes.setConGuideFetchOutDto(
				stageClosureEventService.getContactDetailCFRes(stageClosureEventReq.getConGuideFetchInDto()));
		stageClosureEventRes.setTransactionId(stageClosureEventReq.getTransactionId());
		return stageClosureEventRes;
	}

	// CANIRSP-11 For I&R/A/N Staffing Notifications
	@RequestMapping(value = "/getContactDetailAllegedVictim", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureEventRes getContactDetailAllegedVictim(
			@RequestBody StageClosureEventReq stageClosureEventReq){
		if (TypeConvUtil.isNullOrEmpty(stageClosureEventReq.getConGuideFetchInDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("conGuideFetchInDto.mandatory", null, Locale.US));
		}
		StageClosureEventRes stageClosureEventRes = new StageClosureEventRes();

		// CANIRSP-212 I&R Staffing : make login id available so we can filter to children assigned to this caseworker
		if (stageClosureEventReq.getUserId() != null) {
			stageClosureEventReq.getConGuideFetchInDto().setIdUser(Long.parseLong(stageClosureEventReq.getUserId()));
		}

		// calling the service to get the contact detail for a report
		stageClosureEventRes.setConGuideFetchOutDto(
				stageClosureEventService.getAllegedVictimsForStage(stageClosureEventReq.getConGuideFetchInDto()));
		stageClosureEventRes.setTransactionId(stageClosureEventReq.getTransactionId());
		return stageClosureEventRes;
	}

	// CANIRSP-23 For I&R/A/N Staffing Notifications
	@RequestMapping(value = "/getContactDetailIntakeReports", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ContactSearchRes getContactDetailIntakeReports(
			@RequestBody ContactSearchReq contactIntakeReportsReq){
		if (TypeConvUtil.isNullOrEmpty(contactIntakeReportsReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		ContactSearchRes contactSearchRes = new ContactSearchRes();
		ContactNarrativeDto reportAlt = null;
		// see if this is out of state
		if (contactIntakeReportsReq.getIdEvent() != null && !contactIntakeReportsReq.getIdEvent().equals(0l)) {
			reportAlt = stageClosureEventService.getIntakeReportAlternatives(contactIntakeReportsReq.getIdEvent());
		}
		if (reportAlt == null || reportAlt.getCdContactOthers() == null) {
			// calling the service to get the contact detail for a report
			contactSearchRes.setStageToIntakeNarrative(
					stageClosureEventService.getContactDetailIntakeReports(contactIntakeReportsReq.getIdStage()));
			contactSearchRes.setTransactionId(contactIntakeReportsReq.getTransactionId());
		} else {
			// return the out of state or provider investigation narrative that we already fetched.
			contactSearchRes.setStageToIntakeNarrative(new LinkedList<>());
			contactSearchRes.getStageToIntakeNarrative().add(reportAlt);
			contactSearchRes.setTransactionId(contactIntakeReportsReq.getTransactionId());
		}

		return contactSearchRes;
	}

	@RequestMapping(value = "/getContactFollowupDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureEventRes getContactFollowupDetails(
			@RequestBody ContactSearchReq contactSearchReq){
		if (TypeConvUtil.isNullOrEmpty(contactSearchReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		StageClosureEventRes stageClosureEventRes = new StageClosureEventRes();
		stageClosureEventRes.setConGuideFetchOutDto(
				contactDetailsService.getContactFollowupDetails(contactSearchReq.getIdEvent()));
		stageClosureEventRes.setTransactionId(contactSearchReq.getTransactionId());
		return stageClosureEventRes;
	}

	/**
	 * Method Name: getNarrative Method Description:Retrieves Narrative blob and
	 * dtLastUpdate from database when Narrative is present. Csys06s
	 *
	 * @param commonContactNarrativeReq
	 * @return ContactDetailNarrativeRes
	 */
	@ApiOperation(value = "Get contact narrative", tags = { "contacts" })
	@RequestMapping(value = "/getNarrative", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactDetailNarrativeRes getNarrative(
			@RequestBody CommonContactNarrativeReq commonContactNarrativeReq){

		if (ObjectUtils.isEmpty(commonContactNarrativeReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonContactNarrativeReq.getNmTable())) {
			throw new InvalidRequestException(messageSource.getMessage("narrative.table.name", null, Locale.US));
		}
		ContactDetailNarrativeRes contactDetailNarrativeRes = contactDetailsService
				.getNarrative(commonContactNarrativeReq);
		return contactDetailNarrativeRes;
	}

	/**
	 * Method Name: isCrimHistCheckPending Method Description: Check if any DPS
	 * Criminal History check is pending
	 *
	 * @param criminalHistoryReq
	 * @return criminalHistoryRes
	 */
	@RequestMapping(value = "/isCrimHistCheckPending", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CriminalHistoryRes isCrimHistCheckPending(@RequestBody CriminalHistoryReq criminalHistoryReq){
		if (TypeConvUtil.isNullOrEmpty(criminalHistoryReq.getIdStage())) {
			throw new InvalidRequestException(messageSource
					.getMessage("criminalHistoryReq.isCrimHistCheckPending.getIdStage.mandatory", null, Locale.US));
		}
		CriminalHistoryRes criminalHistoryRes = new CriminalHistoryRes();
		criminalHistoryRes
				.setCriminalCheckResult(contactDetailsService.isCrimHistCheckPending(criminalHistoryReq.getIdStage()));
		return criminalHistoryRes;
	}

	/**
	 * Method Name: checkCrimHistAction Method Description: This method to get
	 * the idPerson if the Criminal History Action is null for the given
	 * Id_Stage.
	 *
	 * @param criminalHistoryReq
	 * @return criminalHistoryRes
	 */
	@RequestMapping(value = "/checkCrimHistAction", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CriminalHistoryRes checkCrimHistAction(@RequestBody CriminalHistoryReq criminalHistoryReq){
		if (TypeConvUtil.isNullOrEmpty(criminalHistoryReq.getIdStage())) {
			throw new InvalidRequestException(messageSource
					.getMessage("criminalHistoryReq.checkCrimHistAction.getIdStage.mandatory", null, Locale.US));
		}
		CriminalHistoryRes criminalHistoryRes = new CriminalHistoryRes();
		criminalHistoryRes.setCriminalHistoryActionCheckResult(
				contactDetailsService.checkCrimHistAction(criminalHistoryReq.getIdStage()));
		return criminalHistoryRes;
	}

	/**
	 * Method Name : deleteContactDtl Method Description : This method deletes
	 * the contact and related records
	 *
	 * @param deleteContactReq
	 * @return
	 */
	@RequestMapping(value = "/deleteContactDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactAUDRes deleteContactDtl(@RequestBody DeleteContactDtlReq deleteContactDtlReq){
		ContactAUDRes deleteContactRes = new ContactAUDRes();
		Long del = contactDetailsService.deleteContactDtl(deleteContactDtlReq.getDeleteContactDto(),
				deleteContactDtlReq.getcFT1050BReportDto());
		deleteContactRes.setContactdeleted(del);
		return deleteContactRes;

	}

	/**
	 * Method Name: contactAUD Method Description: Method to add/update/delete
	 * Contact guide information.
	 *
	 * @param saveContactReq
	 * @return ContactDetailsRes
	 */

	@RequestMapping(value = "/contactAUD", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SaveGuideContactRes contactAUD(@RequestBody SaveGuideContactReq saveContactReq){
		SaveGuideContactRes saveGuideContactRes = new SaveGuideContactRes();
		saveGuideContactRes.setContactGuideDto(contactDetailsService.contactAUD(saveContactReq));
		return saveGuideContactRes;
	}

	/**
	 * Method Name: saveContactDtls Method Description:This method saves the
	 * contact and related objects (event_person_link, guide topics, contact
	 * person narrative) etc.
	 *
	 * @param saveContactReq
	 * @return ContactDetailsRes
	 */
	@RequestMapping(value = "/saveContactDtls", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SaveGuideContactRes saveContactDtls(@RequestBody SaveGuideContactReq saveContactReq){

		SaveGuideContactRes saveGuideContactRes = new SaveGuideContactRes();
		saveGuideContactRes.setChildContactDto(contactDetailsService.saveContactDtls(saveContactReq));

		return saveGuideContactRes;
	}

	/**
	 * Method Name: getFbssClosingLetter Method Description: Tuxedo Service
	 * Name: fbss1100. This method is used to retrieve Case Person List form by
	 * passing IdCase as input request
	 *
	 * @param FbssClosingLetterReq
	 * @return commonFormRes
	 */
	@RequestMapping(value = "/getFbssClosingLetter", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getFbssClosingLetter(@RequestBody FbssClosingLetterReq fbssClosingLetterReq) {

		if (TypeConvUtil.isNullOrEmpty(fbssClosingLetterReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(fbssClosingLetterReq.getIdPerson()))
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(fbssClosingLetterReq.getSzFormName()))
			throw new InvalidRequestException(
					messageSource.getMessage("fbssClosingLetter.formname.notFound", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(contactDetailsService.getFbssClosingLetter(fbssClosingLetterReq)));
		return commonFormRes;
	}

	/**
	 * Method Description: This method is used to return Flag which enables FBSS
	 * Closing Letter and FBSS Closing Spanish Letter dropdown in PersonDetails
	 * Page
	 *
	 * @param FbssClosingLetterReq
	 * @return DisplayFbssClosingLetterRes
	 */
	@RequestMapping(value = "/displayFbssClosingLetter", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public DisplayFbssClosingLetterRes displayFbssClosingLetter(
			@RequestBody DisplayFbssClosingLetterReq displayFbssClosingLetterReq) {

		if (TypeConvUtil.isNullOrEmpty(displayFbssClosingLetterReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(displayFbssClosingLetterReq.getIdPerson()))
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(displayFbssClosingLetterReq.getIdCase()))
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));

		boolean displayFbssClosingLetterFlag;

		DisplayFbssClosingLetterRes displayFbssClosingLetterRes = new DisplayFbssClosingLetterRes();
		displayFbssClosingLetterFlag = contactDetailsService.displayFbssClosingLetter(displayFbssClosingLetterReq);
		displayFbssClosingLetterRes.setDisplayStatus(displayFbssClosingLetterFlag);
		return displayFbssClosingLetterRes;
	}

	/**
	 * Method Name: getReasonRlngshmntCodes Method Description: get
	 * ReasonRlngshmnt Codes
	 *
	 * @return ContactSearchRes
	 */
	@RequestMapping(value = "/getReasonRlngshmntCodes", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ContactSearchRes getReasonRlngshmntCodes() {
		ContactSearchRes contactSearchRes = new ContactSearchRes();
		contactSearchRes.setReasonRlngshmntCodes(contactDetailsService.getReasonRlngshmntCodes());
		return contactSearchRes;
	}

	/**
	 * Method Name: getContactNarr Tuxedo Service Name: CINV69S. Method
	 * Description: This method is used to prefill Narrative Form as part of FDS
	 * - Narrative - Contact Information
	 *
	 * @param ContactNarrativeReq
	 * @return commonFormRes
	 */
	@RequestMapping(value = "/getContactNarr", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getContactNarr(@RequestBody ContactNarrativeReq contactNarrativeReq) {
		CommonFormRes commonFormRes = new CommonFormRes();
		if (ObjectUtils.isEmpty(contactNarrativeReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(contactNarrLogAppendService.getContactNarr(contactNarrativeReq.getIdEvent())));
		return commonFormRes;
	}

	/**
	 * Method Name: sendAlerToPrimary ADS Service Name: sendAlerToPrimary .
	 * Method Description: This method is used to send an Alert to the Primary
	 * assigned staff when the LPS Worker initially saves a Contact
	 *
	 * @param ContactReq
	 * @return SaveContactRes
	 */
	@RequestMapping(value = "/sendAlerToPrimary", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SaveContactRes sendAlerToPrimary(@RequestBody ContactReq contactReq) {
		if (TypeConvUtil.isNullOrEmpty(contactReq.getIdCase()))
			throw new InvalidRequestException(messageSource.getMessage("common.idCase.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(contactReq.getIdEvent()))
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(contactReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		SaveContactRes saveContactRes = contactDetailsService.sendAlerToPrimary(contactReq);
		return saveContactRes;
	}

	/**
	 * Method Name: getErrorMessage Method Description: This method Generates
	 * error messages for GOLD Gua Ref on contact launch.Before cbgr and ccgr
	 * redirect to the Save routine, stop with error messages from ccon31s if
	 * required fields are null.
	 *
	 * @param contactReq
	 * @return SaveContactRes
	 */
	@RequestMapping(value = "/getErrorMessage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SaveContactRes getErrorMessage(@RequestBody ContactReq contactReq) {
		if (TypeConvUtil.isNullOrEmpty(contactReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(contactReq.getCdContactType()))
			throw new InvalidRequestException(
					messageSource.getMessage("common.cdContactType.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(contactReq.getUserId()))
			throw new InvalidRequestException(messageSource.getMessage("common.userId.mandatory", null, Locale.US));
		List<Integer> errorList = contactDetailsService.getErrorMessage(contactReq);
		SaveContactRes saveContactRes = new SaveContactRes();
		saveContactRes.setErrorCodeList(errorList);
		return saveContactRes;
	}

	@RequestMapping(value = "/searchExtOrCommencementContact", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactSearchRes searchExtOrCommencementContact(@RequestBody ContactSearchReq contactSearchReq) {
		if (TypeConvUtil.isNullOrEmpty(contactSearchReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return contactDetailsService.searchExtOrCommencementContact(contactSearchReq);
	}



	/**
	 * Method Name: getRiskNarrExists
	 * Method Description: This method gets the Narrative
	 * Count from RISK_ASSMT_IRA_NARR and RISK_ASSMT_NARR which helps to set the DocType
	 * for Structured Narrative
	 *
	 * @param ContactSearchReq
	 * @return String
	 */
	//Warranty Defect - 11243 - To display both Versions of Structured Narrative RISKSF and CIV33O00
	@RequestMapping(value = "/getRiskNarrExists", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactSearchRes getRiskNarrExists(@RequestBody ContactSearchReq contactSearchReq) {
		ContactSearchRes contactSearchRes=new ContactSearchRes();
		contactSearchRes.setRowQty(contactDetailsService.getRiskNarrExists(contactSearchReq));
		return contactSearchRes;
	}

	/**
	 * artf258107
	 * Method to get the last updated timestamp of a contact based on the event id
	 * @param commonHelperReq
	 * @return
	 */
	@RequestMapping(value = "/getContactLastUpdateDate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getContactLastUpdateDate(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = contactDetailsService.getContactDtLastUpdateByEventId(commonHelperReq);
		return commonHelperRes;
	}

	@RequestMapping(value = "/getEarliestContact", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getEarliestContactDate(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = contactDetailsService.getEarliestContactDate(commonHelperReq);
		return commonHelperRes;
	}

	@RequestMapping(value = "/getByEventId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactSearchRes getByEventId(@RequestBody ContactSearchReq contactSearchReq) {
		ContactSearchRes contactSearchRes=new ContactSearchRes();
		contactSearchRes.setContactDto(contactDetailsService.getContactById(contactSearchReq.getIdEvent()));
		return contactSearchRes;
	}

	@RequestMapping(value = "/getByStageId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ContactSearchRes getByStageId(@RequestBody ContactSearchReq contactSearchReq) {
		ContactSearchRes contactSearchRes=new ContactSearchRes();
		contactSearchRes.setStageValueBeanDto(contactDetailsService.getStageByStageId(contactSearchReq.getIdStage().get(0)));
		return contactSearchRes;
	}

	@ApiOperation(value = "Get contact count by stage", tags = { "contacts" })
	@RequestMapping(value = "/getCountByStage", headers = { "Accept=application/json" }, method = RequestMethod.GET)
	public Integer getCountByStage(Long idStage) {
		return contactDetailsService.getCountOFContactsInStage(idStage);
	}

	@ApiOperation(value = "Get all Contact List", tags = { "contacts" })
	@RequestMapping(value = "/searchContactsForAPIPagination", headers = { "Accept=application/json" }, method = RequestMethod.GET)
	public  List<ContactSearchListDto> searchContactsForAPIPagination( long idStage, int offset, int pageSize ) {
		if (TypeConvUtil.isNullOrEmpty(idStage)) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		ContactSearchReq contactSearchReq = new ContactSearchReq();
		List<Long> idStages = new ArrayList<>();
		idStages.add(idStage);
		contactSearchReq.setIdStage(idStages);
		return contactDetailsService.searchContactsForAPIPagination(contactSearchReq,offset,pageSize ).getContactList();
	}


	// CANIRSP-23 For I&R Staff Search
	@RequestMapping(value = "/stageSearch", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public StageSearchRes stageSearch(@RequestBody StageSearchReq stageSearchBean) {
		if (TypeConvUtil.isNullOrEmpty(stageSearchBean.getIdStage()) && TypeConvUtil.isNullOrEmpty(stageSearchBean.getIdCase()))
			throw new InvalidRequestException(messageSource.getMessage("common.stageorcaseid.mandatory", null, Locale.US));
		return contactDetailsService.stageSearch(stageSearchBean);
	}

	@RequestMapping(value = "/getContactStaffingDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureEventRes getContactStaffingDetails(
			@RequestBody StageClosureEventReq stageClosureEventReq){
		StageClosureEventRes stageClosureEventRes = new StageClosureEventRes();
		stageClosureEventRes.setConGuideFetchOutDto(
				stageClosureEventService.getContactDetailStaffingDetail(stageClosureEventReq.getConGuideFetchInDto()));
		return stageClosureEventRes;
	}

	@RequestMapping(value = "/getFollowupPending", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public InrFollowupPendingRes getFollowupPending(@RequestBody InrFollowupPendingReq inrFollowupPendingReq) {
		return contactDetailsService.getFollowupPending(inrFollowupPendingReq);
	}
}
