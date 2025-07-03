package us.tx.state.dfps.service.contacts.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.*;
import us.tx.state.dfps.common.web.bean.StageSearchBean;
import us.tx.state.dfps.service.admin.dao.FetchEventDao;
import us.tx.state.dfps.service.admin.dao.ServiceDeliveryRtrvDtlsDao;
import us.tx.state.dfps.service.admin.dao.StageRetDao;
import us.tx.state.dfps.service.admin.dto.*;
import us.tx.state.dfps.service.admin.service.TodoCreateService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptAllegDispValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPAValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPSValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPSValueModBean;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptResourceValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptRsrcVoltnsValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptValueDto;
import us.tx.state.dfps.service.contact.dto.CFTSafetyAssessmentInfoDto;
import us.tx.state.dfps.service.contact.dto.ChildFatalityContactDto;
import us.tx.state.dfps.service.contact.dto.ContactActiveAddrDto;
import us.tx.state.dfps.service.contact.dto.ContactDetailCFReportBean;
import us.tx.state.dfps.service.contact.dto.ContactDetailDto;
import us.tx.state.dfps.service.contact.dto.ContactGuideDto;
import us.tx.state.dfps.service.contact.dto.ContactSearchDto;
import us.tx.state.dfps.service.contact.dto.ContactSearchListDto;
import us.tx.state.dfps.service.contact.dto.FbssClosingLetterDto;
import us.tx.state.dfps.service.contact.dto.InrContactFollowUpPendingDto;
import us.tx.state.dfps.service.contact.dto.MailDto;
import us.tx.state.dfps.service.contact.dto.NameDto;
import us.tx.state.dfps.service.contact.dto.*;
import us.tx.state.dfps.service.contacts.dao.*;
import us.tx.state.dfps.service.contacts.dao.ChildFatality1050BDao;
import us.tx.state.dfps.service.contacts.dao.ContactDetailsDao;
import us.tx.state.dfps.service.contacts.dao.ContactGuideDao;
import us.tx.state.dfps.service.contacts.dao.ContactNarrativeDao;
import us.tx.state.dfps.service.contacts.dao.ContactOccuredDao;
import us.tx.state.dfps.service.contacts.dao.ContactPersonDao;
import us.tx.state.dfps.service.contacts.dao.ContactProcessDao;
import us.tx.state.dfps.service.contacts.dao.ContactSearchDao;
import us.tx.state.dfps.service.contacts.dao.InrSafetyDao;
import us.tx.state.dfps.service.contacts.service.ContactDetailSaveService;
import us.tx.state.dfps.service.contacts.service.ContactDetailsService;
import us.tx.state.dfps.service.contacts.service.ContactGuideService;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.FbssClosingLetterPrefillData;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.CriminalHistoryDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.DisplayFbssClosingLetterDto;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.person.service.PersonListService;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsInDto;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsOutDto;
import us.tx.state.dfps.service.stageutility.service.StageUtilityService;
import us.tx.state.dfps.service.workload.dao.AddressDao;
import us.tx.state.dfps.service.workload.dao.ContactDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageLinkDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;
import us.tx.state.dfps.web.security.user.UserRolesEnum;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactAUDDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactDetailsDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactFieldDiDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactFoundDiArrayDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactFoundDiDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactInformationDiDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactNarrativeStageDiDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactPersonDiDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactStageDiDto;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ChildContactDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ConGuideFetchOutDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactDoArrayDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactDoDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactInformationDoDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactNarrativeStageDoDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactPersonDetailsDoDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactPurposeDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactStageDoDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FindContactDto;
import us.tx.state.dfps.xmlstructs.outputstructs.InrSafetyFieldDto;
import us.tx.state.dfps.xmlstructs.outputstructs.PersonDetailsDoArrayDto;
import us.tx.state.dfps.xmlstructs.outputstructs.PersonDetailsEventDoDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ServiceOutputDto;

import static us.tx.state.dfps.service.common.ServiceConstants.DATE_FORMAT;

import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * service-business - IMPACT PHASE 2 MODERNIZATION Class Description: this class
 * is used for implementing all the contact related DAO calls Jul 31, 2017-
 * 1:04:37 PM © 2017 Texas Department of Family and Protective Services
 */

@Service
public class ContactDetailsServiceImpl implements ContactDetailsService {
	private static final String CHISTTYP = "CHISTTYP";
	private static final String CRCLFACD = "CRCLFACD";
	private static final String CHILD_FATALITY_1050B_REPORT = "Child Fatality 1050B Report.";


	@Autowired
	ContactDetailsDao contactDetailsDao;

	@Autowired
	FetchEventDao fetchEventDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	TodoCreateService todoCreateService;

	@Autowired
	ChildFatality1050BDao childFatality1050BDao;

	@Autowired
	EventService eventService;

	@Autowired
	EventUtilityService eventUtilityService;

	@Autowired
	ContactDetailSaveService contactDetailSaveService;
	@Autowired
	ServiceDeliveryRtrvDtlsDao serviceDeliveryRtrvDtlsDao;

	@Autowired
	TodoDao todoDao;

	@Autowired
	AddressDao addressDao;

	@Autowired
	LookupDao lookupDao;
	@Autowired
	ContactDao contactDao;

	@Autowired
	ContactSearchDao contactSearchDao;

	@Autowired
	ContactGuideService contactGuideService;

	@Autowired
	ContactOccuredDao contactOccuredDao;

	@Autowired
	ContactNarrativeDao contactNarrativeDao;

	@Autowired
	StageRetDao stageRetDao;

	@Autowired
	ContactPersonDao contactPersonDao;

	@Autowired
	ContactGuideDao contactGuideDao;

	@Autowired
	FbssClosingLetterPrefillData fbssClosingLetterPrefillData;

	@Autowired
	CloseOpenStageService closeOpenStageService;

	@Autowired
	PersonListService personListService;

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	CharacteristicsDao characteristicsDao;

	@Autowired
	CriminalHistoryDao criminalHistoryDao;

	@Autowired
	private WorkLoadDao workLoadDao;

	@Autowired
	private StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	private MobileUtil mobileUtil;

	@Autowired
	private StageUtilityService stageUtilityService;

	@Autowired
	private InrSafetyDao inrSafetyDao;

	@Autowired
	private ContactProcessDao contactProcessDao;

	private static final Logger log = Logger.getLogger(ContactDetailsServiceImpl.class);

	/**
	 * Method Name: saveContact Method Description:This method for now will be
	 * used for Kinship Notification contact but subsequently other contacts
	 * will be transferred over to this service. This method saves the contact
	 * and related objects (event_person_link, guide topics, contact person
	 * narrative)
	 *
	 * @param saveContactReq
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public Long saveContact(ContactDetailReq contactDetailReq) {
		ContactAUDDto contactDto = contactDetailReq.getContactAUDDto();
		List<ContactPersonNarrValueDto> contactPersonNarrList = contactDetailReq.getContactPersonNarrList();
		ChildContactDto childContactDto = contactDetailSaveService.audContactDetailRecord(contactDto);
		if (ServiceConstants.CCNTCTYP_GKIN.equals(contactDto.getCdContactType())
				|| ServiceConstants.CCNTCTYP_GKNS.equals(contactDto.getCdContactType())) {
			saveContactPersonNarrInfo(contactPersonNarrList, childContactDto.getIdEvent(), contactDto.getIdCase());
		}
		return childContactDto.getIdEvent();
	}

	/**
	 * method name : create1050BEvent method description : this method is used
	 * to save the contact details for new/existing contact for CPS and RCL
	 * program
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long create1050BEvent(EventValueDto eventValueDto, long idUser) {

		eventValueDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
		eventValueDto.setCdEventType(ServiceConstants.CEVNTTYP_CON);
		eventValueDto.setEventDescr(CHILD_FATALITY_1050B_REPORT);
		eventValueDto.setCdEventTask(ServiceConstants.CPGRMS_CPS.equals(eventValueDto.getCdProgram())
				? ServiceConstants.TASK_CODE_CPS_INV_CON : ServiceConstants.TASK_CODE_RCL_INV_CON);
		long idContactEvt = 0;
		// creating the new object for post event request and populating to call
		// the post event service
		PostEventReq postEventReq = new PostEventReq();

		postEventReq.setUlIdEvent(eventValueDto.getIdEvent());
		postEventReq.setUlIdStage(eventValueDto.getIdStage());
		postEventReq.setUlIdPerson(eventValueDto.getIdPerson());

		postEventReq.setSzCdTask(eventValueDto.getCdEventTask());
		postEventReq.setSzCdEventType(eventValueDto.getCdEventType());
		postEventReq.setSzTxtEventDescr(eventValueDto.getEventDescr());

		Date date = new Date();
		postEventReq.setDtDtEventOccurred(date);

		postEventReq.setSzCdEventStatus(eventValueDto.getCdEventStatus());
		postEventReq.setTsLastUpdate(eventValueDto.getDtLastUpdate());

		PostEventPersonDto postEventPersonDto = new PostEventPersonDto();
		postEventPersonDto.setIdPerson(idUser);

		List<PostEventPersonDto> postEventPersonDtos = new ArrayList<PostEventPersonDto>();
		postEventPersonDtos.add(postEventPersonDto);

		postEventReq.setPostEventPersonList(postEventPersonDtos);

		postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventReq.setUlIdCase(eventValueDto.getIdCase());

		PostEventRes postEventRes = eventService.postEvent(postEventReq);
		idContactEvt = postEventRes.getUlIdEvent();

		return idContactEvt;
	}

	/**
	 * MethodName:create1050BRejAlert MethoDescription: This function creates
	 * Rejection Alert for 1050B Author.
	 *
	 * EJB Name : ContactBean.java
	 *
	 * @param contactEventReq
	 * @return ContactRejAlertRes
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TodoCreateOutDto create1050BRejAlert(long idEvent, long idUser, String program) {
		ContactDto contactDto = contactDetailsDao.selectContact(idEvent);
		long idContactPerson = contactDto.getIdContactWorker();
		if (idContactPerson != 0) {
			String shortDesc = "Child Fatality 2058b/2059b Report has been rejected.  Any prior approvals have been invalidated.";
			return createContactTodo(shortDesc, shortDesc, "CON008", idContactPerson, idUser,
					contactDto.getIdContactStage(), idEvent);
		}
		return null;
	}

	/**
	 * Method Name: createContactTodo Method Description: This function creates
	 * Alerts or Todos for Contacts.
	 *
	 * @param toDoDesc
	 * @param toDoLongDesc
	 * @param cdTodoInfoType
	 * @param idPrsnAssgn
	 * @param idUser
	 * @param idStage
	 * @param idEvent
	 * @return TodoCreateOutDto
	 *
	 */
	private TodoCreateOutDto createContactTodo(String toDoDesc, String toDoLongDesc, String cdTodoInfoType,
											   long idPrsnAssgn, long idUser, Long idStage, long idEvent) {
		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
		todoCreateInDto.setSysCdTodoCf(cdTodoInfoType);
		todoCreateInDto.setDtSysDtTodoCfDueFrom(null);
		todoCreateInDto.setSysIdTodoCfPersCrea(idUser);
		todoCreateInDto.setSysIdTodoCfStage(idStage);
		todoCreateInDto.setSysIdTodoCfPersAssgn(idPrsnAssgn);
		todoCreateInDto.setSysIdTodoCfPersWkr(idUser);

		MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
		todoCreateInDto.setServiceInputDto(new ServiceInputDto());
		mergeSplitToDoDto.setCdTodoCf(cdTodoInfoType);
		mergeSplitToDoDto.setDtTodoCfDueFrom(null);
		mergeSplitToDoDto.setIdTodoCfStage(idStage);
		mergeSplitToDoDto.setIdTodoCfPersAssgn(idPrsnAssgn);
		mergeSplitToDoDto.setIdTodoCfPersWkr(idUser);

		if (!ObjectUtils.isEmpty(toDoDesc))
			mergeSplitToDoDto.setTodoCfDesc(toDoDesc);
		if (!ObjectUtils.isEmpty(toDoLongDesc))
			mergeSplitToDoDto.setTodoCfLongDesc(toDoLongDesc);
		if (idEvent > 0) {
			mergeSplitToDoDto.setIdTodoCfEvent(idEvent);
		}

		todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);
		return todoCreateService.TodoCommonFunction(todoCreateInDto);

	}

	/**
	 * MethodName: create1050BRejectionTask MethodDescription: This function
	 * creates Rejection Task for 1050B Author.
	 *
	 * EJB Name : ContactBean.java
	 *
	 * @param idEvent
	 * @param idUser
	 * @param program
	 * @return TodoCreateOutDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TodoCreateOutDto create1050BRejectionTask(long idEvent, long idUser, String program) {
		String cdTodoInfo = "";
		ContactDto contactDto = contactDetailsDao.selectContact(idEvent);
		long idContactPerson = contactDto.getIdContactWorker();
		if (ServiceConstants.CPGRMS_CPS.equals(program)) {
			cdTodoInfo = ServiceConstants.CD_TODO_INFO_CPS;
		} else if (ServiceConstants.CPGRMS_RCL.equals(program)) {
			cdTodoInfo = ServiceConstants.CD_TODO_INFO_CCL;
		}
		if (idContactPerson != 0) {
			String shortDesc = "Child Fatality 2058b/2059b Report has been rejected.";
			return createContactTodo(shortDesc, shortDesc, cdTodoInfo, idContactPerson, idUser,
					contactDto.getIdContactStage(), idEvent);
		}
		return null;
	}

	/**
	 * MethodName: update1050BEvent MethodDescription: update1050BEvent
	 *
	 * @param cft1050bReportDto
	 * @return long
	 */

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long update1050BEvent(ChildFatalityContactDto cft1050bReportDto) {
		Long result = ServiceConstants.ZERO_VAL;
		EventValueDto contactEvt = cft1050bReportDto.getContactEvent();
		// First fetch the EventValueBean from database.
		EventValueDto oldEvent = eventUtilityService.fetchEventInfo(contactEvt.getIdEvent());
		String oldEventStatus = oldEvent.getCdEventStatus();

		// Invalidate Pending Approvals.
		if (ServiceConstants.CEVTSTAT_PEND.equals(oldEventStatus) && cft1050bReportDto.isInvalidateApproval()) {
			// Update Event to COMP, Approvers to Invalid
			result = contactDetailsDao.updateApproversStatus(contactEvt.getIdEvent());
			// Update Approval Event to COMP.
			result = contactDetailsDao.updateAppEventStatus(contactEvt.getIdEvent(), ServiceConstants.CEVTSTAT_COMP);
		} else if (cft1050bReportDto.isInvalidateApproval() == false) {
			// Update Current Event.
			Event event = new Event();
			event.setIdEvent(contactEvt.getIdEvent());
			event.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
			List<Event> events = new ArrayList<Event>();
			events.add(event);
			result = eventUtilityService.updateEventStatus(events);
			log.info("result: " + result);
		}
		return result;
	}

	/**
	 * MethodName: update1050BEvent MethodDescription: update1050BEvent
	 *
	 * @param cft1050bReportDto
	 * @return long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CFTRlsInfoRptCPSValueModBean saveCFTRlsInfoRptCPS(CFTRlsInfoRptCPSValueModBean cftRlsInfoRptCPSValueDto) {
		CFTRlsInfoRptCPSValueModBean cpsValueDto;
		if (ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdFtRlsInfoRptCps())
				|| ServiceConstants.ZERO_VAL == cftRlsInfoRptCPSValueDto.getIdFtRlsInfoRptCps()) {
			cpsValueDto = childFatality1050BDao.insertRlsInfoRptCPS(cftRlsInfoRptCPSValueDto);
		} else {
			cpsValueDto = childFatality1050BDao.updateRlsInfoRptCPS(cftRlsInfoRptCPSValueDto);
		}
		return cpsValueDto;
	}

	/**
	 * MethodName: deleteCFTRlsInfoRptCPS MethodDescription:
	 * deleteCFTRlsInfoRptCPS
	 *
	 * @param idFtRlsInfoRptCPS
	 * @return long
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long deleteCFTRlsInfoRptCPS(long idFtRlsInfoRptCPS) {
		return childFatality1050BDao.deleteRlsInfoRptCPS(idFtRlsInfoRptCPS);
	}

	/**
	 * MethodName: deleteLICHistory MethodDescription: deleteLICHistory
	 *
	 * @param cft1050bReportDto
	 * @return ChildFatalityContactDto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ChildFatalityContactDto deleteLICHistory(ChildFatalityContactDto cft1050bReportDto) {

		EventValueDto eventStatusVB = eventUtilityService.fetchEventInfo(cft1050bReportDto.getIdEvent());

		if (ServiceConstants.CEVTSTAT_PEND.equals(eventStatusVB.getCdEventStatus())
				&& cft1050bReportDto.isInvalidateApproval()) {

			// Deleting while in pending approval status will invalidate the
			// approval

			if (ServiceConstants.DEL_AGENCY.equals(cft1050bReportDto.getDeleteType())) {
				childFatality1050BDao.deleteRlsInfoCPA(cft1050bReportDto.getIdFtInfoCpaOrAlleg());
			} else if (ServiceConstants.DEL_ABUSE_NEGLECT.equals(cft1050bReportDto.getDeleteType())) {
				childFatality1050BDao.deleteFtInfoAllegDisp(cft1050bReportDto.getIdFtInfoCpaOrAlleg());
			}

			// Update Event to COMP, Approvers to Invalid after delete

			Integer idEvent = (int) cft1050bReportDto.getIdEvent();
			long result = contactDetailsDao.updateApproversStatus(idEvent);

			// Update Approval Event to COMP.

			result = contactDetailsDao.updateAppEventStatus(idEvent, ServiceConstants.CEVTSTAT_COMP);
			log.debug(result);

		} else {
			if (ServiceConstants.DEL_AGENCY.equals(cft1050bReportDto.getDeleteType())) {
				childFatality1050BDao.deleteRlsInfoCPA(cft1050bReportDto.getIdFtInfoCpaOrAlleg());
			} else if (ServiceConstants.DEL_ABUSE_NEGLECT.equals(cft1050bReportDto.getDeleteType())) {
				childFatality1050BDao.deleteFtInfoAllegDisp(cft1050bReportDto.getIdFtInfoCpaOrAlleg());
			}
		}
		return cft1050bReportDto;
	}

	/**
	 * MethodName:createExtensionAprRejAlert MethodDescription:This method is
	 * used to Alert of Extension Request Approval/Rejection
	 *
	 * @param idEvent
	 * @param approvalLen
	 * @param approvalStatus
	 * @return long
	 *
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public long createExtensionAprRejAlert(Long idEvent, String approvalLen, String approvalStatus) {
		// Get the Case Worker Id from Event table.
		FetchEventDto ccmn45diDto = new FetchEventDto();
		ccmn45diDto.setIdEvent(idEvent);

		List<FetchEventResultDto> ccmn45doDtos = new ArrayList<FetchEventResultDto>();

		ccmn45doDtos = fetchEventDao.fetchEventDao(ccmn45diDto);
		FetchEventRowDto rowccmn45doDto = new FetchEventRowDto();
		for (FetchEventResultDto ccmn45doDto : ccmn45doDtos) {
			rowccmn45doDto.setIdEvent(ccmn45doDto.getIdEvent());
			rowccmn45doDto.setCdTask(ccmn45doDto.getCdTask());

			Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String tsLastUpdate = formatter.format(ccmn45doDto.getDtLastUpdate());

			rowccmn45doDto.setTsLastUpdate(tsLastUpdate);
			rowccmn45doDto.setCdEventStatus(ccmn45doDto.getCdEventStatus());
			rowccmn45doDto.setCdEventType(ccmn45doDto.getCdEventType());
			rowccmn45doDto.setDtEventOccurred(ccmn45doDto.getDtDtEventOccurred());
			rowccmn45doDto.setDtDtEventCreated(ccmn45doDto.getDtDtEventCreated());
			rowccmn45doDto.setIdStage(ccmn45doDto.getIdStage());
			rowccmn45doDto.setIdPerson(ccmn45doDto.getIdPerson());
			rowccmn45doDto.setTxtEventDescr(ccmn45doDto.getTxtEventDescr());
		}

		if (rowccmn45doDto != null && rowccmn45doDto.getIdEvent() != 0) {
			String shortDesc = "Submitted Request for Extension has been ";
			shortDesc += (ServiceConstants.APRV_STATUS_REJECT.equals(approvalStatus)) ? "rejected." : "approved ";
			shortDesc += (ServiceConstants.APRV_STATUS_APPROVED.equals(approvalStatus)) ? " - " : "";
			// Create Todo.
			createContactTodo(shortDesc, shortDesc, ServiceConstants.TODO_INFO_CWALERT_EXTREQ_APRVORREJ,
					rowccmn45doDto.getIdPerson(), 0L, rowccmn45doDto.getIdStage(), idEvent);
		}

		return 0;
	}

	/**
	 * MethodName: createEventFor1050BReports MethodDescription:
	 * createEventFor1050BReports
	 *
	 * @param cft1050bReportDto
	 * @param program
	 * @param idUser
	 * @return List
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Integer> createEventFor1050BReports(ContactDetailsDto contactDetailsDto, String program, Long idUser) {
		List<Integer> cftNewEventList = new ArrayList<>();
		// Get the deceased Children List in the stage.
		long idStage = contactDetailsDto.getToDoAudStructDto().getIdStage();

		List<Long> cftChildren = contactDetailsDao.getCFTChildrenForStage(idStage);
		for (long idPerson : cftChildren) {
			if (contactDetailsDao.hasCF1050BRecord(idStage, idPerson) == false) {
				// Create New Contact Event.
				PostEventReq postEventReq = new PostEventReq();
				postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				postEventReq.setSzCdTask(ServiceConstants.CPGRMS_CPS.equals(program)
						? ServiceConstants.TASK_CODE_CPS_INV_CON : ServiceConstants.TASK_CODE_RCL_INV_CON);
				postEventReq.setTsLastUpdate(contactDetailsDto.getToDoAudStructDto().getTsLastUpdate());
				postEventReq.setSzCdEventStatus(ServiceConstants.CEVTSTAT_NEW);
				postEventReq.setSzCdEventType(ServiceConstants.CEVNTTYP_CON);
				postEventReq.setDtDtEventOccurred(contactDetailsDto.getEventStructDto().getDtEventOccurred());
				postEventReq.setUlIdEvent(contactDetailsDto.getToDoAudStructDto().getIdEvent());
				postEventReq.setUlIdStage(idStage);
				postEventReq.setUlIdPerson(contactDetailsDto.getToDoAudStructDto().getIdTodoPersAssigned());
				postEventReq.setSzTxtEventDescr(contactDetailsDto.getEventStructDto().getEventDescr());
				postEventReq.setUlIdCase(contactDetailsDto.getToDoAudStructDto().getIdCase());
				PostEventRes eventRes = eventService.postEvent(postEventReq);
				Integer idContactEvent = eventRes.getUlIdEvent().intValue();

				// Create New Contact Record.
				// There is no way to identity the 1050B Report Event just by
				// looking at the Event Record.
				// So we are creating Contact Record as well while creating NEW
				// Contact Event.

				ContactDto contactDto = new ContactDto();

				contactDto.setIdEvent(Long.valueOf(idContactEvent));
				contactDto.setIdCase(contactDetailsDto.getToDoAudStructDto().getIdCase());
				contactDto.setIdContactStage(contactDetailsDto.getToDoAudStructDto().getIdStage());

				contactDto.setIdContactWorker(contactDetailsDto.getToDoAudStructDto().getIdTodoPersAssigned());
				contactDto.setDtContactOccurred(new Date());
				contactDto.setCdContactType(ServiceConstants.CCNTCTYP_FCFT);

				closeOpenStageService.contactAUD(contactDto, ServiceConstants.REQ_FUNC_CD_ADD);

				// Create New Table Record and add idPersonChild into that
				// table.
				// idPerson in the loop will be used for this table.
				CFTRlsInfoRptValueDto cftRlsInfoRpt = new CFTRlsInfoRptValueDto();
				cftRlsInfoRpt.setIdCreatedPerson(idUser);
				cftRlsInfoRpt.setIdLastUpdatePerson(idUser);

				cftRlsInfoRpt.setIdEvent(idContactEvent);
				cftRlsInfoRpt.setIdPerson(idPerson);

				PersonDto personDto = personDao.getPersonById(idPerson);
				cftRlsInfoRpt.setNmPersonFull(personDto.getNmPersonFull());

				// Report Type
				if (ServiceConstants.CPGRMS_CPS.equals(program)) {
					cftRlsInfoRpt.setCdReport(ServiceConstants.CAPS_PROG_CPS);
				} else if (ServiceConstants.CPGRMS_RCL.equals(program)) {
					String operType = fetchFacilityType(idStage);
					if (ServiceConstants.CRCLFACD_GRO.equals(operType)
							|| (lookupDao.simpleDecodeSafe(CRCLFACD, ServiceConstants.CRCLFACD_IFFH).equals(operType))
							|| (lookupDao.simpleDecodeSafe(CRCLFACD, ServiceConstants.CRCLFACD_IFGH))
							.equals(operType)) {
						cftRlsInfoRpt.setCdReport("RCLG");
					} else if ((lookupDao.simpleDecodeSafe(CRCLFACD, ServiceConstants.CRCLFACD_AFH)).equals(operType)) {
						cftRlsInfoRpt.setCdReport("RCLA");
					}
				}

				childFatality1050BDao.insertCFTRlsInfoRpt(cftRlsInfoRpt);
				cftNewEventList.add(idContactEvent);

			}
		}
		return cftNewEventList;
	}

	/**
	 * Get the operation type for the current stage.
	 *
	 * @param idStage
	 * @return
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String fetchFacilityType(Long idStage) {
		return contactDetailsDao.fetchFacilityType(idStage);
	}

	/**
	 * Method contactDetailAud Method Description:This method is used to the
	 * Add/Update/Delete contact and related records
	 *
	 * @param ContactAUDDto
	 * @param contactGuideList
	 * @param contactPersonNarrList
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public ContactAUDRes contactDetailAud(ContactAUDDto contactAUDDto, List<ContactGuideDto> contactGuideList,
								 List<ContactPersonNarrValueDto> contactPersonNarrList) {
		ContactAUDRes contactAUDRes = new ContactAUDRes();
		if ((ServiceConstants.CCNTCTYP_GKIN.equals(contactAUDDto.getCdContactType())
				|| ServiceConstants.CCNTCTYP_GKNS.equals(contactAUDDto.getCdContactType()))) {
			// delete contact person narrative information
			contactAUDRes.setContactdeleted(deleteContactPersonNarrInfo(contactPersonNarrList, contactAUDDto.getIdEvent(),
					contactAUDDto.getIdCase()));
		} else {
			ContactDetailDto contactDetailDreq = new ContactDetailDto();
			contactDetailDreq.setContactGuideList(contactGuideList);
			contactDetailDreq.setIdEvent((long) contactAUDDto.getSynchronizationServiceDto().getIdEvent());
			contactDetailDreq.setIdCase((long) contactAUDDto.getIdCase());
			// delete the Contact guide information when Contact is deleted.
			contactGuideService.deleteGuidePlanInfo(contactDetailDreq);
		}
		// AUD the contact and event person link
		ChildContactDto childContactDto = contactDetailSaveService.audContactDetailRecord(contactAUDDto);
		if(childContactDto !=null){
			contactAUDRes.setContactdeleted(childContactDto.getIdEvent());
			contactAUDRes.setInrSelfIsDeleted(childContactDto.getInrSelfIsDeleted());
		}
		return contactAUDRes;
	}

	/**
	 * Method deleteContactPersonNarrInfo Method Description:This method is used
	 * to Delete contact narrative info
	 *
	 * @param contactPersonNarrList
	 * @param idEvent
	 * @param idCase
	 * @return long
	 */
	private long deleteContactPersonNarrInfo(List<ContactPersonNarrValueDto> contactPersonNarrList, Long idEvent,
											 Long idCase) {
		long deletedContact = 0;
		if (!CollectionUtils.isEmpty(contactPersonNarrList)) {
			for (ContactPersonNarrValueDto contactPersonNarrValueDto : contactPersonNarrList) {
				contactPersonNarrValueDto.setIdEvent(idEvent);
				contactPersonNarrValueDto.setIdCase(idCase);
				if (!ObjectUtils.isEmpty(contactPersonNarrValueDto.getIdContactPersonNarr())
						&& contactPersonNarrValueDto.getIdContactPersonNarr() > 0) {
					deletedContact = contactDetailsDao.deleteContactPersonNarr(contactPersonNarrValueDto);
				}
			}
		}
		return deletedContact;
	}

	/**
	 * Method Name: saveContact Method Description:Method to add/update/delete
	 * Contact person information.
	 *
	 * @param contactPersonNarrList
	 * @param idEvent
	 * @param idCase
	 * @return Long
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public Long saveContactPersonNarrInfo(List<ContactPersonNarrValueDto> contactPersonNarrList, Long idEvent,
										  Long idCase) {
		Integer result = 0;
		if (contactPersonNarrList != null && contactPersonNarrList.size() > 0) {
			for (int i = 0; i < contactPersonNarrList.size(); i++) {
				ContactPersonNarrValueDto persNarrDto = new ContactPersonNarrValueDto();
				persNarrDto = (ContactPersonNarrValueDto) contactPersonNarrList.get(i);
				persNarrDto.setIdEvent(idEvent);
				persNarrDto.setIdCase(idCase);
				if (!ObjectUtils.isEmpty(persNarrDto.getCdOperation())) {
					if (persNarrDto.getCdOperation().equals(ServiceConstants.ADD)) {
						result = contactDetailsDao.addContactPersonNarr(persNarrDto);
					} else if (persNarrDto.getCdOperation().equals(ServiceConstants.DELETE)) {
						result = (int) contactDetailsDao.deleteContactPersonNarr(persNarrDto);
					} else if (persNarrDto.getCdOperation().equals(ServiceConstants.UPDATE)) {
						result = contactDetailsDao.updateContactPersonNarr(persNarrDto);
					}
				}
			}
		}
		return result.longValue();
	}

	/**
	 * Method Name:insertAllegation Method Description: This method gets called
	 * to insert a agency history entry when Save button clicked on the add
	 * screen (shown via click of Add button under agency history <Licensing -
	 * Prior Verification Dates: section>).
	 *
	 * @param InsertAllegReq
	 * @return int.
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public Long insertAllegation(InsertAllegReq insertAllegReq) {
		CFTRlsInfoRptAllegDispValueDto cftRlsInfoRptAllegDispValueDto = insertAllegReq
				.getCftrlsInfoRptAllegDispValueDto();
		ChildFatalityContactDto cft1050BReportDB = insertAllegReq.getCft1050BReportDto();
		EventValueDto eventValueDto = eventUtilityService.fetchEventInfo(cft1050BReportDB.getIdEvent());
		// If case is in Pending Status, Approval status should be updated
		if (ServiceConstants.CEVTSTAT_PEND.equals(eventValueDto.getCdEventStatus())
				&& cft1050BReportDB.isInvalidateApproval()) {
			contactDetailsDao.updateApproversStatus(cft1050BReportDB.getIdEvent());
			contactDetailsDao.updateAppEventStatus(cft1050BReportDB.getIdEvent(), ServiceConstants.CEVTSTAT_COMP);
		}
		// Inserting values to Database
		Long retCode = childFatality1050BDao.insertRlsInfoAllegDisposition(cftRlsInfoRptAllegDispValueDto);
		return retCode;
	}

	/**
	 * Method Name: saveCpa Method Description: service class to save cpa
	 *
	 * @param saveCpaReq
	 * @return long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long saveCpa(SaveCpaReq saveCpaReq) {

		Long idEvent = saveCpaReq.getIdEvent();
		if (!TypeConvUtil.isNullOrEmpty(idEvent)) {
			EventValueDto eventValueDto = eventUtilityService.fetchEventInfo(idEvent);

			// event is in pend status and add agency history will invalidate
			// the apprroval
			if ((!ObjectUtils.isEmpty(saveCpaReq.getApprovalMode()) && !saveCpaReq.getApprovalMode())
					&& CodesConstant.CEVTSTAT_PEND.equals(eventValueDto.getCdEventStatus())) {
				contactDetailsDao.updateApproversStatus(idEvent);
				contactDetailsDao.updateAppEventStatus(idEvent, ServiceConstants.CEVTSTAT_COMP);

			}
		}
		long retCode = childFatality1050BDao.saveRlsInfoRptCPA(saveCpaReq.getCftRlsInfoRptCPAValueDto());

		return retCode;
	}

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
	 * @return List<ContactPersonNarrValueDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<ContactPersonNarrValueDto> fetchContactPeronNarrList(
			ArrayList<us.tx.state.dfps.web.contact.bean.ContactDetailDto> contactedList, long idStage, Long idEvent) {
		List<ContactPersonNarrValueDto> contactPersonNarrValueDtos = new ArrayList<>();
		if (!CollectionUtils.isEmpty(contactedList) && contactedList.size() > 0) {
			for (int i = 0; i < contactedList.size(); i++) {
				try {
					us.tx.state.dfps.web.contact.bean.ContactDetailDto conGuideFetchOutRowDto = contactedList.get(i);
					ContactPersonNarrValueDto contactPersonNarrValueDto = new ContactPersonNarrValueDto();
					contactPersonNarrValueDto.setNmPersonFull(conGuideFetchOutRowDto.getSzNmPersonFull());
					contactPersonNarrValueDto.setIdPerson(ObjectUtils.isEmpty(conGuideFetchOutRowDto.getUlIdPerson())
							? 0l : (long) conGuideFetchOutRowDto.getUlIdPerson());
					contactPersonNarrValueDto.setIdEvent(idEvent);
					contactPersonNarrValueDto.setCdStagePersRelInt(conGuideFetchOutRowDto.getSzCdStagePersRelInt());
					if (ServiceConstants.N.equals(conGuideFetchOutRowDto.getcIndPersRmvlNotified())) {
						ContactPersonNarrValueDto perNarValueDto = contactDetailsDao
								.fetchContactPersonNarr(contactPersonNarrValueDto);
						contactPersonNarrValueDto.setIdContactPersonNarr(perNarValueDto.getIdContactPersonNarr());
						contactPersonNarrValueDto.setCdRsnNotNofified(perNarValueDto.getCdRsnNotNofified());
						contactPersonNarrValueDto.setDtLastUpdate(perNarValueDto.getDtLastUpdate());
						contactPersonNarrValueDto.setNarrative(perNarValueDto.getNarrative());
					}
					contactPersonNarrValueDtos.add(contactPersonNarrValueDto);
				} catch (DataNotFoundException e) {
					log.error(e.getMessage());
				}
			}
		}
		return contactPersonNarrValueDtos;
	}

	/**
	 * Method Name: isRmRsAddressExist Method Description:Fetch if Residence
	 * mailing or Residence address exists for a person
	 *
	 * @param personId
	 * @return boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean isRmRsAddressExist(long personId) {
		Boolean isRmRsAddressExist = Boolean.FALSE;
		try {
			isRmRsAddressExist = addressDao.isRmRsAddressExist(personId);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return isRmRsAddressExist;
	}

	/**
	 * Method Name: getContactType Method Description:This function returns
	 * Contac Type for the given Contact Event Id.
	 *
	 * @param idEvent
	 * @return String
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getContactType(long idEvent) {
		String contactType = ServiceConstants.EMPTY_STRING;
		contactType = contactDetailsDao.getContactType(idEvent);
		return contactType;
	}

	/**
	 * Method Name: hasCF1050BRecord Method Description:This method checks if
	 * Non-End Dated 1050B Report exists for the given Stage and the Child.
	 *
	 * @param idStage
	 * @param idPerson
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean hasCF1050BRecord(Long idStage, Long idPerson) {
		Boolean indicator = Boolean.FALSE;
		try {
			indicator = contactDetailsDao.hasCF1050BRecord(idStage, idPerson);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return indicator;
	}

	/**
	 * Method Name: getCFTChildrenForStage Method Description:This method checks
	 * returns all the deceased Children in the given Stage. Checks for
	 * following Conditions.
	 *
	 * 1. Allegation Record present for the Child with IND_FATALITY = 'Y'. 2.
	 * Same Child should have Date of Death in Person Table.
	 *
	 * @param idStage
	 * @return List<Long>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Long> getCFTChildrenForStage(Long idStage) {
		List<Long> cftChildrenList = new ArrayList<>();
		cftChildrenList = contactDetailsDao.getCFTChildrenForStage(idStage);
		return cftChildrenList;
	}

	/**
	 * Method Name: fetchCurrAbuseNglctInfo Method Description:Fetches the
	 * current Abuse and neglect bean info info for the IdStage
	 *
	 * @param idStage
	 * @param idCase
	 * @return CFTRlsInfoRptCPSValueDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CFTRlsInfoRptCPSValueModBean fetchCurrAbuseNglctInfo(Long idStage, Long idCase) {
		CFTRlsInfoRptCPSValueModBean cftRlsInfoRptCPSValueDto = new CFTRlsInfoRptCPSValueModBean();
		cftRlsInfoRptCPSValueDto.setIdStage(idStage);
		cftRlsInfoRptCPSValueDto.setIdCase(idCase);
		cftRlsInfoRptCPSValueDto = contactDetailsDao.getCFTCurrHisInfo(cftRlsInfoRptCPSValueDto);
		return cftRlsInfoRptCPSValueDto;
	}

	/**
	 * Method Name: fetchPriorHistoryInfo Method Description:Fetches the
	 * priorHistory Information for the stage
	 *
	 * @param idStage
	 * @param idPerson
	 * @return List<CFTRlsInfoRptCPSValueDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<CFTRlsInfoRptCPSValueModBean> fetchPriorHistoryInfo(Long idStage, Long idPerson) {
		List<CFTRlsInfoRptCPSValueModBean> cftRlsInfoRptCPSValueDtos = new ArrayList<>();
		cftRlsInfoRptCPSValueDtos = contactDetailsDao.getCFTPriorHistory(idStage, idPerson);
		return cftRlsInfoRptCPSValueDtos;
	}

	/**
	 * Method Name: fetchChildFatalityInfo Method Description:Fetches Child
	 * Fatality Information
	 *
	 * @param personId
	 * @param stageId
	 * @param userId
	 * @return CFT1050BReportDto
	 *
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public ContactDetailCFReportBean fetchChildFatalityInfo(long personId, long stageId, long userId) {
		ContactDetailCFReportBean cft1050bReportDto = new ContactDetailCFReportBean();
		try {
			cft1050bReportDto = contactDetailsDao.fetchChildFatalityInfo(personId, stageId, userId);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return cft1050bReportDto;
	}

	/**
	 * Method Name: fetchFacilityType Method Description:Get the operation type
	 * for the current stage.
	 *
	 * @param stageId
	 * @return String
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public String fetchFacilityType(long stageId) {
		String facilityType = contactDetailsDao.fetchFacilityType(stageId);
		return facilityType;
	}

	/**
	 * Method Name: fetchPersonInfo Method Description:Fetches the current Abuse
	 * and neglect bean info info for the IdStage
	 *
	 * @param stageId
	 * @return CFTRlsInfoRptValueDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CFTRlsInfoRptValueDto fetchPersonInfo(long stageId) {
		CFTRlsInfoRptValueDto cftRlsInfoRptValueDto = new CFTRlsInfoRptValueDto();
		try {
			List<Long> childIds = contactDetailsDao.getCFTChildrenForStage(stageId);
			if (!childIds.isEmpty()) {
				cftRlsInfoRptValueDto.setIdPerson(childIds.get(0));
				cftRlsInfoRptValueDto.setNmPersonFull(todoDao.getPersonFullName(childIds.get(0)));
			}
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return cftRlsInfoRptValueDto;
	}

	/**
	 * Method Name: selectContact1050BReport Method Description:This method
	 * retrieves the information for Child Fatality 1050B Report from the
	 * database using Event Id.
	 *
	 * @param idEvent
	 * @param program
	 * @return CFT1050BReportFetchDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ContactDetailChildFatalityReportDto selectContact1050BReport(long idEvent, String program) {
		ContactDetailChildFatalityReportDto cft1050bReportFetchDto = new ContactDetailChildFatalityReportDto();
		try {
			List<EventValueDto> lieventValueDto = fetchEventInfo(idEvent);
			EventValueDto eventValueDto = lieventValueDto.get(ServiceConstants.Zero);
			eventValueDto.setCdProgram(program);
			cft1050bReportFetchDto.setEventValueDto(eventValueDto);

			ContactDto contactDto = contactDetailsDao.selectContact(idEvent);
			cft1050bReportFetchDto.setContactDto(contactDto);
			Long idFtRlsInfoRpt = ServiceConstants.ZERO_VAL;
			CFTRlsInfoRptValueDto ftRlsInfoRptDto = childFatality1050BDao.selectCFTRlsInfoRpt(idEvent);
			cft1050bReportFetchDto.setCftRlsInfoRpt(ftRlsInfoRptDto);
			if (!TypeConvUtil.isNullOrEmpty(ftRlsInfoRptDto))
				idFtRlsInfoRpt = (long) ftRlsInfoRptDto.getIdFtRlsInfoRpt();

			if (ServiceConstants.CPS_PROGRAM.equals(program)) {
				List<CFTRlsInfoRptCPSValueDto> ftRlsInfoRptCpsDtos = childFatality1050BDao
						.selectRlsInfoRptCPS(idFtRlsInfoRpt);
				cft1050bReportFetchDto.setListRlsInfoRptCpsDto(ftRlsInfoRptCpsDtos);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return cft1050bReportFetchDto;

	}

	/**
	 *
	 * Method Name: fetchEventInfo Method Description:Fetches the event info for
	 * the IdEvent.
	 *
	 * @param idEvent
	 * @return List<EventValueDto>
	 */
	private List<EventValueDto> fetchEventInfo(long idEvent) {

		List<EventValueDto> lieventValueDtos = new ArrayList<>();
		FetchEventDto fetchEventDto = new FetchEventDto();
		fetchEventDto.setIdEvent(idEvent);
		List<FetchEventResultDto> lifetchEventResultDtos = fetchEventDao.fetchEventDao(fetchEventDto);
		for (FetchEventResultDto fetchEventResultDto : lifetchEventResultDtos) {
			EventValueDto eventValueDto = new EventValueDto();
			eventValueDto.setCdEventStatus(fetchEventResultDto.getCdEventStatus());
			eventValueDto.setCdEventTask(fetchEventResultDto.getCdTask());
			eventValueDto.setCdEventType(fetchEventResultDto.getCdEventType());
			eventValueDto.setDtEventOccurred(fetchEventResultDto.getDtDtEventOccurred());
			eventValueDto.setDtLastUpdate(fetchEventResultDto.getDtLastUpdate());
			eventValueDto.setEventDescr(fetchEventResultDto.getTxtEventDescr());
			eventValueDto.setIdEvent(fetchEventResultDto.getIdEvent());
			eventValueDto.setIdPerson(fetchEventResultDto.getIdPerson());
			eventValueDto.setIdStage(fetchEventResultDto.getIdStage());

			lieventValueDtos.add(eventValueDto);
		}
		return lieventValueDtos;
	}

	/**
	 * Method Name: selectContact1050BReport Method Description:This method
	 * retrieves the information for Child Fatality 1050B Report from the
	 * database using Event Id.
	 *
	 * @param idEvent
	 * @param program
	 * @param operationType
	 * @return CFT1050BReportGetDto
	 *
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ContactDetailChildFatalityReportDto selectContact1050BReport(long idEvent, String program,
																		String operationType) {
		ContactDetailChildFatalityReportDto cft1050bReportGetDto = new ContactDetailChildFatalityReportDto();
		try {
			List<EventValueDto> lieventValueDto = fetchEventInfo(idEvent);
			EventValueDto eventValueDto = lieventValueDto.get(ServiceConstants.Zero);
			eventValueDto.setCdProgram(program);
			cft1050bReportGetDto.setEventValueDto(eventValueDto);

			ContactDto contactDto = contactDetailsDao.selectContact(idEvent);
			cft1050bReportGetDto.setContactDto(contactDto);
			Long idFtRlsInfoRpt = ServiceConstants.ZERO_VAL;
			// calling the dao class to fetch the Info report
			CFTRlsInfoRptValueDto cFTRlsInfoRptValueDto = childFatality1050BDao.selectCFTRlsInfoRpt(idEvent);
			cft1050bReportGetDto.setCftRlsInfoRpt(cFTRlsInfoRptValueDto);

			idFtRlsInfoRpt = cFTRlsInfoRptValueDto.getIdFtRlsInfoRpt();

			if (ServiceConstants.CPS_PROGRAM.equals(program)) {
				// calling the DAO class to fetch the Info report in case of CPS
				List<CFTRlsInfoRptCPSValueDto> ftRlsInfoRptCpsDtos = childFatality1050BDao
						.selectRlsInfoRptCPS(idFtRlsInfoRpt);
				cft1050bReportGetDto.setListRlsInfoRptCpsDto(ftRlsInfoRptCpsDtos);
			} else if (ServiceConstants.CPGRMS_RCL.equals(program)) {
				// calling the DAO class to fetch the allegation Report
				// dispositions
				List<CFTRlsInfoRptAllegDispValueDto> ftRlsInfoRptAllegDispDtos = childFatality1050BDao
						.selectRlsInfoAllegDispositions(idFtRlsInfoRpt);
				cft1050bReportGetDto.setListRlsInfoRptAllegDispDto(ftRlsInfoRptAllegDispDtos);
				// calling the DAO class to fetch the info Report Resource
				CFTRlsInfoRptResourceValueDto cftRlsInfoRptResourceValueDto = childFatality1050BDao
						.selectRlsInfoRptRsrc(idFtRlsInfoRpt);
				cft1050bReportGetDto.setCftRlsInfoRptResourceValueDto(cftRlsInfoRptResourceValueDto);
				// calling the DAO class to fetch the info Report Resource
				// voilations
				List<CFTRlsInfoRptRsrcVoltnsValueDto> listCFTRlsInfoRptRsrcVoltnsValueDto = childFatality1050BDao
						.selectRlsInfoRptRsrcVoilations(idFtRlsInfoRpt);
				cft1050bReportGetDto.setListRlsInfoRptRsrcVoltnsDto(listCFTRlsInfoRptRsrcVoltnsValueDto);
				if (operationType
						.equals(lookupDao.simpleDecodeSafe(ServiceConstants.CRCLFACD, ServiceConstants.CRCLFACD_AFH))) {
					// calling the DAO class to fetch the info Report for CPA if
					// the operation code is Agency foster home
					List<CFTRlsInfoRptCPAValueDto> ftRlsInfoRptCpaDtos = childFatality1050BDao
							.selectRlsInfoRptCPA(idFtRlsInfoRpt);
					cft1050bReportGetDto.setListRlsInfoRptCpaDto(ftRlsInfoRptCpaDtos);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return cft1050bReportGetDto;
	}

	/**
	 *
	 * Method Name: selectCodeTableRows Method Description: Method that returns
	 * the key/value pair info. used for dropdown list of - Reason Agency Home
	 * Verification Was Relinquished - Add prior history verification screen
	 * (agency history).
	 *
	 * @return Map<String,String>
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public Map<String, String> selectCodeTableRows() {

		Map<String, String> resultMap = new HashMap<>();
		try {
			resultMap = childFatality1050BDao.selectCodeTableRows();

		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return resultMap;

	}

	/**
	 * Method Name: fetch1050BRlsInfoRpt Method Description:This function
	 * returns 1050B RlsInfoRpt (Main Table) using idEvent
	 *
	 * @param idEvent
	 * @return CFTRlsInfoRptValueDto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public CFTRlsInfoRptValueDto fetch1050BRlsInfoRpt(long idEvent) {
		CFTRlsInfoRptValueDto cftRlsInfoRptValueDto = childFatality1050BDao.selectCFTRlsInfoRpt(idEvent);

		return cftRlsInfoRptValueDto;
	}

	/**
	 * Method Name: getChildFatalityTaskCode Method Description: This method
	 * returns Child Fatality Task Code based on Program
	 *
	 * @param program
	 * @return CFTRlsInfoRptValueDto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public String getChildFatalityTaskCode(String program) {
		String taskCode = ServiceConstants.EMPTY_STRING;

		try {
			if (ServiceConstants.CPGRMS_CPS.equals(program)) {
				taskCode = ServiceConstants.TASK_CODE_CPS_INV_CON;
			} else if (ServiceConstants.CPGRMS_RCL.equals(program))
				taskCode = ServiceConstants.TASK_CODE_RCL_INV_CON;

		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return taskCode;

	}

	/**
	 * Method Name: saveContactFor1050BReport Method Description: This method
	 * creates 1050B Report Event (and Contact) for each Child Fatality in the
	 * given Stage.
	 *
	 * @param childFatalityContactDto
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long saveContactFor1050BReport(ChildFatalityContactDto childFatalityContactDto) {
		Long idContactEvt = 0l;
		EventValueDto eventValueDto = childFatalityContactDto.getContactEvent();
		ContactDto contactDto = childFatalityContactDto.getContact();
		try {
			// if event id is coming as null or zero it means the contact is not
			// existing in this case service will insert new records to the
			// table
			if (TypeConvUtil.isNullOrEmpty(eventValueDto.getIdEvent()) || 0l == eventValueDto.getIdEvent()) {
				idContactEvt = create1050BEvent(eventValueDto, childFatalityContactDto.getIdUser());
				contactDto.setIdEvent(idContactEvt);
				contactDao.insertContact(contactDto);
				childFatalityContactDto.getCftRlsInfoRpt().setIdEvent(idContactEvt);
				Long idrlsInfoRpt = childFatality1050BDao
						.insertCFTRlsInfoRpt(childFatalityContactDto.getCftRlsInfoRpt());
				// calling the save method to insert other table details related
				// to this contact
				saveSub(childFatalityContactDto, eventValueDto, idrlsInfoRpt);
			} else if (0 != eventValueDto.getIdEvent()
					&& ServiceConstants.CEVTSTAT_NEW.equals(eventValueDto.getCdEventStatus())) {
				// if event id is coming as not null or not zero and event
				// status is new it means the contact is existing in this case
				// service will update the existing records to the table
				idContactEvt = eventValueDto.getIdEvent();
				eventUtilityService.updateEventStatus(eventValueDto.getIdEvent(), ServiceConstants.CEVTSTAT_COMP);
				contactDao.updateContact(contactDto);
				// calling the save method to update other table details related
				// to this contact
				saveSub(childFatalityContactDto, eventValueDto,
						childFatalityContactDto.getCftRlsInfoRpt().getIdFtRlsInfoRpt());
			} else {
				// if event id is coming as not null or not zero and event
				// status is not new
				// it means the contact is existing in this case service will
				// update the existing records to the table
				idContactEvt = eventValueDto.getIdEvent();

				update1050BEvent(childFatalityContactDto);

				contactDao.updateContact(contactDto);
				// if the program is of CPS then below tables will get update
				if (ServiceConstants.CPGRMS_CPS.equals(eventValueDto.getCdProgram())) {
					List<CFTRlsInfoRptCPSValueModBean> cftRlsInfoRptCPSList = childFatalityContactDto
							.getRlsInfoRptCPSList();
					for (CFTRlsInfoRptCPSValueModBean rlsInfoRptCPS : cftRlsInfoRptCPSList) {
						if (ObjectUtils.isEmpty(rlsInfoRptCPS.getIdFtRlsInfoRptCps())
								|| 0 == rlsInfoRptCPS.getIdFtRlsInfoRptCps()) {
							rlsInfoRptCPS
									.setIdFtRlsInfoRpt(childFatalityContactDto.getCftRlsInfoRpt().getIdFtRlsInfoRpt());
							rlsInfoRptCPS.setIdCreatedPerson(childFatalityContactDto.getIdUser());
							childFatality1050BDao.insertRlsInfoRptCPS(rlsInfoRptCPS);
						} else {
							childFatality1050BDao.updateRlsInfoRptCPS(rlsInfoRptCPS);
						}
					}
				}
				// if the program is RCL then below update calls is need to be
				// done
				if (ServiceConstants.CPGRMS_RCL.equals(eventValueDto.getCdProgram())) {
					childFatality1050BDao.updateRlsInfoRptRsrc(childFatalityContactDto.getRlsInfoRptRsrc());
				}
			}
			// if the request is for save and submit then the task is supposed
			// to be completed
			// we are using below method to complete the task
			if (childFatalityContactDto.getSaveType() == ChildFatalityContactDto.SaveOperationType.SAVE_AND_SUBMIT) {
				complete1050BTasks(idContactEvt, eventValueDto.getCdProgram());
			}
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}

		return idContactEvt;
	}

	/**
	 * Method Name: saveSub Method Description: this method is to update and
	 * insert the records into the cf info related tables
	 *
	 * @param cft1050BReportDB
	 * @param contactEvt
	 * @param idrlsInfoRpt
	 */
	private void saveSub(ChildFatalityContactDto cft1050BReportDB, EventValueDto contactEvt, Long idrlsInfoRpt) {
		// if the program code is CPS below condition will satify
		if (ServiceConstants.CPGRMS_CPS.equals(contactEvt.getCdProgram())) {
			List<CFTRlsInfoRptCPSValueModBean> cftRlsInfoRptCPSList = cft1050BReportDB.getRlsInfoRptCPSList();
			for (CFTRlsInfoRptCPSValueModBean rlsInfoRptCPS : cftRlsInfoRptCPSList) {
				rlsInfoRptCPS.setIdFtRlsInfoRpt(idrlsInfoRpt);
				childFatality1050BDao.insertRlsInfoRptCPS(rlsInfoRptCPS);
				// Defect#13525, Removed the id event check because the event is created and has an id at this point.
				int counter = ServiceConstants.Zero;
				List<CFTSafetyAssessmentInfoDto> safetyAssessmentInfoDBList = rlsInfoRptCPS
						.getSafetyAssessmentInfoDBList();

				if (!ObjectUtils.isEmpty(safetyAssessmentInfoDBList)) {
					// if the safety assessment if having record then
					// populating the below variables
					for (CFTSafetyAssessmentInfoDto cFTSafetyAssessmentInfoDB : safetyAssessmentInfoDBList) {

						String sdmInfo = String.valueOf(++counter) + ".) " + FormattingUtils.formatDate(
								((CFTSafetyAssessmentInfoDto) cFTSafetyAssessmentInfoDB).getDtSafetyAssessed())
								+ ":  "
								+ FormattingUtils
								.formatString(((CFTSafetyAssessmentInfoDto) cFTSafetyAssessmentInfoDB)
										.getEventDescription().replaceFirst(ServiceConstants.SDM_SHORT_NAME,
												ServiceConstants.EMPTY_STR)) // QCR 62702 - SDM Removal
								+ ":  "
								+ FormattingUtils
								.formatString(((CFTSafetyAssessmentInfoDto) cFTSafetyAssessmentInfoDB)
										.getSafetyDecision());
						// if the safety assessment if having record calling
						// the insert DAO method
						childFatality1050BDao.insertRlsInfoRptCPS(rlsInfoRptCPS, idrlsInfoRpt,
								ServiceConstants.CHISTTYP_SDMSA, sdmInfo, counter);
					}
				}

				if (rlsInfoRptCPS.getSdmRaFinalRiskLevel() != null
						&& !rlsInfoRptCPS.getSdmRaFinalRiskLevel().equals(ServiceConstants.EMPTY_STRING)) {
					String sdmRAFRLInfo = lookupDao.simpleDecodeSafe(CHISTTYP, ServiceConstants.CHISTTYP_SDMRAFRL)
							+ ": " + rlsInfoRptCPS.getSdmRaFinalRiskLevel();
					childFatality1050BDao.insertRlsInfoRptCPS(rlsInfoRptCPS, idrlsInfoRpt,
							ServiceConstants.CHISTTYP_SDMRAFRL, sdmRAFRLInfo, 0);
				}

			}
		}
		// In case if the program code is RCL below block of code will execute
		else if (ServiceConstants.CPGRMS_RCL.equals(contactEvt.getCdProgram())) {
			// this method is used to call the insert DAO method to update the
			// allegation disposition table
			childFatality1050BDao.insertRlsInfoAllegDispositionBatch(cft1050BReportDB.getRlsInfoAllegDispositions(),
					idrlsInfoRpt);

			cft1050BReportDB.getRlsInfoRptRsrc().setIdFtRlsInfoRpt(idrlsInfoRpt);
			// this method is used to call the insert DAO method to update the
			// info resource table
			childFatality1050BDao.insertRlsInfoRptRsrc(cft1050BReportDB.getRlsInfoRptRsrc());
			// this method is used to call the insert DAO method to update the
			// resource violation table
			childFatality1050BDao.insertRsrcViolationBatch(cft1050BReportDB.getRlsInfoRptRsrcVoltns(), idrlsInfoRpt);
			// below table will only get updated if the operation type is of
			// agency foster home
			if (cft1050BReportDB.getRlsInfoRptRsrc().getOperationType()
					.equals(lookupDao.simpleDecodeSafe(CRCLFACD, ServiceConstants.CRCLFACD_AFH))) {
				// this method is used to call the insert DAO method to update
				// the report CPS table table
				childFatality1050BDao.insertRlsInfoRptCPABatch(cft1050BReportDB.getRlsInfoRptCPAList(), idrlsInfoRpt);
			}
		}
	}

	/**
	 * Method Name: complete1050BTasks Method Description: This function sets
	 * 1050B todos Complete.
	 *
	 * @param idContactEvt
	 * @param connection
	 * @param program
	 */
	private void complete1050BTasks(Long idContactEvt, String program) {
		String cftTaskCode = getChildFatalityTaskCode(program);
		List<TodoDto> compTodos = new ArrayList<TodoDto>();
		List<TodoDto> todos = todoDao.selectToDosForEvent(idContactEvt);
		for (TodoDto todo : todos) {
			if (isValid(cftTaskCode) && cftTaskCode.equals(todo.getCdTodoTask()) && "T".equals(todo.getCdTodoType())) {
				todo.setDtTodoCompleted(new Date());
				compTodos.add(todo);
			}
		}

		if (compTodos.size() > ServiceConstants.Zero) {
			todoDao.updateToDosDao(compTodos);
		}
	}

	/**
	 * Method Name: isValid Method Description:Checks to see if a given string
	 * is valid. This includes checking that the string is not null or empty.
	 *
	 * @param value
	 * @return boolean
	 */
	public static boolean isValid(String value) {
		if (TypeConvUtil.isNullOrEmpty(value)) {
			return false;
		}
		String trimmedString = value.trim();
		return (trimmedString.length() > 0);
	}

	/**
	 * MethodName: completeCFInfoTasks MethodDescription: completeCFInfoTasks
	 *
	 * @param commonEventIdReq
	 * @return SaveContactRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SaveContactRes completeCFInfoTasks(CommonEventIdReq commonEventIdReq) {
		SaveContactRes res = new SaveContactRes();
		String completeStatus = contactDetailsDao.completeCFInfoTasks(commonEventIdReq);
		res.setStatusResponse(completeStatus);
		return res;
	}

	/**
	 * Method Name: reviewComplete Method Description:This method allows to
	 * complete review when type CSS Review is selected in contact details page.
	 *
	 * @param saveContactReq
	 * @return SaveContactRes
	 *
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SaveContactRes reviewComplete(SaveContactReq saveContactReq) {
		SaveContactRes res = new SaveContactRes();
		res.setStatusResponse(contactDetailsDao.reviewComplete(saveContactReq));
		return res;
	}

	/**
	 * Method Name: getContactOtherName
	 *
	 * @param idContact
	 * @return SaveContactRes
	 *
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SaveContactRes getContactOtherName(Long idContact) {
		SaveContactRes res = new SaveContactRes();
		res.setOtherContactName(contactDetailsDao.getContact(idContact).getNmContactOth());
		return res;
	}

	/**
	 * Method Name: updateContactOtherName
	 *
	 * @param idEvent
	 * @param otherContactName
	 * @return SaveContactRes
	 *
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SaveContactRes updateContactOtherName(Long idEvent, String otherContactName) {
		SaveContactRes res = new SaveContactRes();
		Contact contactFromDB = contactDetailsDao.getContact(idEvent);
		contactFromDB.setNmContactOth(otherContactName);
		contactDetailsDao.updateContact(contactFromDB);
		res.setStatusResponse(ServiceConstants.SUCCESS);
		return res;
	}

	/**
	 * Method Name: searchContacts
	 *
	 * @param ContactSearchReq
	 * @return ContactSearchRes
	 *
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ContactSearchRes searchContacts(ContactSearchReq contactSearchReq) {
		ContactSearchRes result = new ContactSearchRes();
		result.setContactList(contactSearchDao.searchContacts(contactSearchReq.getPersonIds(),
				contactSearchReq.getIndPersonPhonePrimary(), contactSearchReq.getCdPersonPhoneType(),
				contactSearchReq.getCdEventStatus(), contactSearchReq.getIdCase(),
				contactSearchReq.getDtScrSearchDateFrom(), contactSearchReq.getDtScrSearchDateTo(),
				contactSearchReq.getIdEvent(), contactSearchReq.getCdContactType(),
				contactSearchReq.getCdContactPurpose(), contactSearchReq.getCdContactMethod(),
				contactSearchReq.getCdContactLocation(), contactSearchReq.getCdContactOthers(),
				contactSearchReq.getIdStage()));
		return result;
	}

	/**
	 * Method Name: indStructNarrExists
	 *
	 * @param ContactSearchReq
	 * @return boolean
	 *
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean indStructNarrExists(ContactSearchReq contactSearchReq) {
		return contactSearchDao.indStructNarrExists(contactSearchReq.getIdEvent());
	}

	/**
	 * Method Name: getNarrative Method Description:Retrieves Narrative blob and
	 * dtLastUpdate from database when Narrative is present. Csys06s
	 *
	 * @param CommonContactNarrativeReq
	 * @return ContactDetailNarrativeRes
	 *
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ContactDetailNarrativeRes getNarrative(CommonContactNarrativeReq commonContactNarrativeReq) {
		ServiceDeliveryRtrvDtlsInDto serviceDeliveryRtrvDtlsInDto = new ServiceDeliveryRtrvDtlsInDto();
		serviceDeliveryRtrvDtlsInDto.setIdEvent(commonContactNarrativeReq.getIdEvent());
		serviceDeliveryRtrvDtlsInDto.setSysTxtTablename(commonContactNarrativeReq.getNmTable());
		List<ServiceDeliveryRtrvDtlsOutDto> serviceDeliveryRtrvDtlsList = serviceDeliveryRtrvDtlsDao
				.getNarrExists(serviceDeliveryRtrvDtlsInDto);
		ServiceDeliveryRtrvDtlsOutDto serviceDeliveryRtrvDtlsOutDto = null;
		ContactDetailNarrativeRes pCSYS06DOutputRec = new ContactDetailNarrativeRes();
		try {
			if (!ObjectUtils.isEmpty(serviceDeliveryRtrvDtlsList)) {
				serviceDeliveryRtrvDtlsOutDto = serviceDeliveryRtrvDtlsList.get(0);
				pCSYS06DOutputRec.setDtLastUpdate(serviceDeliveryRtrvDtlsOutDto.getDtLastUpdate());
				pCSYS06DOutputRec.setNbrBLOBLength(serviceDeliveryRtrvDtlsOutDto.getBlob().length());
				pCSYS06DOutputRec.setIdDocumentTemplate(serviceDeliveryRtrvDtlsOutDto.getIdDocumentTemplate());
				byte[] bdata = serviceDeliveryRtrvDtlsOutDto.getBlob().getBytes(1,
						(int) serviceDeliveryRtrvDtlsOutDto.getBlob().length());
				String dataStr = new String(bdata);
				pCSYS06DOutputRec.setBlbBLOBData(dataStr);
			} else {
				pCSYS06DOutputRec.setMessage("MSG_NO_ROWS_RETURNED");
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		return pCSYS06DOutputRec;
	}

	/**
	 * Method Name: isCrimHistCheckPending Method Description: Check if any DPS
	 * Criminal History check is pending
	 *
	 * @param idStage
	 * @return boolean
	 */
	@Override
	public boolean isCrimHistCheckPending(long idStage) {

		return contactDetailsDao.isCrimHistCheckPending(idStage);
	}

	/**
	 * Method Name: checkCrimHistAction Method Description: This method to get the
	 * idPerson if the Criminal History Action is null for the given Id_Stage.
	 *
	 * @param idStage
	 * @return HashMap
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public HashMap checkCrimHistAction(long idStage) {

		return criminalHistoryDao.checkCrimHistAction(idStage);
	}

	/**
	 *
	 * Method Name: callCSYS12D Method Description: Retrieves a list of names
	 * corresponding to each contact.
	 *
	 * @param contactPersonDiDto
	 * @param contactFoundDiArrayDto
	 * @return
	 */
	private ContactDoDto callCSYS12D(ContactPersonDiDto contactPersonDiDto,
									 ContactFoundDiArrayDto contactFoundDiArrayDto) {
		ContactPersonDetailsDoDto contactPersonDetailsDoDto = new ContactPersonDetailsDoDto();

		try {
			contactPersonDetailsDoDto = contactPersonDao.getPersonDetailsForEvent(contactPersonDiDto);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		PersonDetailsDoArrayDto personDetailsDoArrayDto = contactPersonDetailsDoDto.getPersonDetailsDoARRAYDto();
		if (!TypeConvUtil.isNullOrEmpty(personDetailsDoArrayDto)) {
			if ((personDetailsDoArrayDto.getPersonDetailsDoDtoList()
					.size() < (!TypeConvUtil.isNullOrEmpty(contactFoundDiArrayDto)
					? contactFoundDiArrayDto.getContactFoundDiDtoList().size()
					: 0))) {
				return null;
			}
		}

		Map<Integer, PersonDetailsEventDoDto> personIdRowMap = new LinkedHashMap<Integer, PersonDetailsEventDoDto>();
		for (PersonDetailsEventDoDto personDetailsDoDto : personDetailsDoArrayDto.getPersonDetailsDoDtoList()) {
			personIdRowMap.put(Integer.valueOf(personDetailsDoDto.getUlIdPerson().intValue()), personDetailsDoDto);
		}
		PersonDetailsDoArrayDto tempPersonRow = new PersonDetailsDoArrayDto();

		if (contactFoundDiArrayDto != null) {

			for (ContactFoundDiDto contactFoundDiDto : contactFoundDiArrayDto.getContactFoundDiDtoList()) {

				Integer ulIdPerson = Integer.valueOf(contactFoundDiDto.getUlIdPerson().intValue());
				PersonDetailsEventDoDto personDetailsDoDto = (PersonDetailsEventDoDto) personIdRowMap.get(ulIdPerson);
				if (personDetailsDoDto == null) {

					return null;
				} else {
					personIdRowMap.remove(ulIdPerson);
					if (tempPersonRow.getPersonDetailsDoDtoList().size() <= 50)
						tempPersonRow.getPersonDetailsDoDtoList().add(personDetailsDoDto);
				}
			}
		}
		ContactDoDto contactDoDto = new ContactDoDto();

		PersonDetailsEventDoDto[] personDetailsDoDtoArray = new PersonDetailsEventDoDto[ServiceConstants.MAX_NAMES];
		for (Map.Entry<Integer, PersonDetailsEventDoDto> entry : personIdRowMap.entrySet()) {
			if (tempPersonRow.getPersonDetailsDoDtoList().size() < ServiceConstants.MAX_NAMES
					&& personIdRowMap.size() > 0)
				if (tempPersonRow.getPersonDetailsDoDtoList().size() <= 50)
					tempPersonRow.getPersonDetailsDoDtoList().add(entry.getValue());
			personIdRowMap.remove(entry.getKey());

		}
		int i = tempPersonRow.getPersonDetailsDoDtoList().size();
		PersonDetailsEventDoDto[] personDetailsDoArray = new PersonDetailsEventDoDto[i];
		for (int j = 0; j < i; j++) {
			personDetailsDoArray[j] = ((PersonDetailsEventDoDto) tempPersonRow.getPersonDetailsDoDtoList().get(j));
		}

		System.arraycopy(personDetailsDoArray, 0, personDetailsDoDtoArray, 0,
				tempPersonRow.getPersonDetailsDoDtoList().size());
		contactDoDto.setNmContact1(
				personDetailsDoDtoArray[0] != null ? personDetailsDoDtoArray[0].getSzNmPersonFull() : null);
		contactDoDto.setNmContact2(
				personDetailsDoDtoArray[1] != null ? personDetailsDoDtoArray[1].getSzNmPersonFull() : null);
		contactDoDto.setNmContact3(
				personDetailsDoDtoArray[2] != null ? personDetailsDoDtoArray[2].getSzNmPersonFull() : null);
		contactDoDto.setNmContact4(
				personDetailsDoDtoArray[3] != null ? personDetailsDoDtoArray[3].getSzNmPersonFull() : null);
		contactDoDto.setNmContact5(
				personDetailsDoDtoArray[4] != null ? personDetailsDoDtoArray[4].getSzNmPersonFull() : null);
		return contactDoDto;
	}

	/**
	 *
	 * Method Name: deleteContactDtl Method Description: This method deletes the
	 * contact and related records
	 *
	 * @param csys07siDto
	 * @param cFT1050BReportDto
	 *
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public Long deleteContactDtl(ContactAUDDto csys07siDto, ChildFatalityContactDto cFT1050BReportDto) {
		long deletedContact = 0;

		if (ServiceConstants.CCNTCTYP_FCFT.equals(csys07siDto.getCdContactType())) {
			EventValueDto contactEventBean = cFT1050BReportDto.getContactEvent();

			contactDetailsDao.deleteCFTRlsInfoRpt(contactEventBean.getIdEvent());
			Long rptId = cFT1050BReportDto.getCftRlsInfoRpt().getIdFtRlsInfoRpt();

			if (ServiceConstants.CPGRMS_CPS.equals(contactEventBean.getCdProgram())) {
				contactDetailsDao.deleteRlsInfoRptCPSByRptId(rptId);
			} else if (ServiceConstants.CPGRMS_RCL.equals(contactEventBean.getCdProgram())) {
				contactDetailsDao.deleteRlsInfoAllegDispositionByRptId(rptId);
				contactDetailsDao.deleteRlsInfoRptRsrcByRptId(rptId);
				contactDetailsDao.deleteRsrcViolationByRptId(rptId);
				contactDetailsDao.deleteRlsInfoRptCPAByRptId(rptId);
			}

		}
		// delete the contact and event person link

		contactDetailSaveService.audContactDetailRecord(csys07siDto);
		return deletedContact;
	}

	/**
	 * Method Name: contactAUD Method Description: Method to add/update/delete
	 * Contact guide information.
	 *
	 * @param saveContactReq
	 * @return ContactDetailsRes
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<ContactGuideDto> contactAUD(SaveGuideContactReq saveGuideContactReq) {
		List<ContactGuideDto> contactGuideList = saveGuideContactReq.getContactGuideDto();
		if (saveGuideContactReq.getCntPurpose().equals(ServiceConstants.CCNTPURP_GFTF)
				|| ServiceConstants.CCNTPURP_GCMR.equalsIgnoreCase(saveGuideContactReq.getCntPurpose())) {
			if (saveGuideContactReq.getCntType().equals(ServiceConstants.CCNTCTYP_GREG)
					|| saveGuideContactReq.getCntType().equals(ServiceConstants.CCNTCTYP_BREG)) {

				if (!CollectionUtils.isEmpty(contactGuideList)) {
					for (ContactGuideDto guideBean : contactGuideList) {
						guideBean.setIdEvent(saveGuideContactReq.getIdEvent());
						guideBean.setIdCase(saveGuideContactReq.getIdCase());
						// if principal is Parent or Child
						if (guideBean.getIdEvent() > 0
								&& (null != guideBean.getGuidePlan() && !guideBean.getGuidePlan().equals(""))
								&& !guideBean.getGuideplanType().equals(ServiceConstants.CGTXTTYP_COL)
								&& !guideBean.getGuideplanType().equals(ServiceConstants.CGTXTTYP_CGVR)) {
							if (guideBean.getCdOperation().equals(ServiceConstants.ADD)) {
								guideBean = contactGuideDao.saveGuidePlanForPrincipal(guideBean);

								if (!ObjectUtils.isEmpty(guideBean.getSelectedGuideTopics())
										&& !guideBean.getSelectedGuideTopics().isEmpty()) {
									updateGuideTopics(guideBean);
								}
							} else if (guideBean.getCdOperation().equals(ServiceConstants.DELETE)) {
								contactGuideDao.deleteContactGuideTopic(guideBean);
								contactGuideDao.deleteContactGuidePlan(guideBean);
							} else if (guideBean.getCdOperation().equals(ServiceConstants.UPDATE)) {
								contactGuideDao.updateContactGuidePlan(guideBean);
								contactGuideDao.deleteContactGuideTopic(guideBean);
								if (guideBean.getSelectedGuideTopics() != null
										&& !guideBean.getSelectedGuideTopics().isEmpty()) {
									updateGuideTopics(guideBean);
								}
							}
						} else if (guideBean.getGuideplanType().equals(ServiceConstants.CGTXTTYP_COL)) {
							if (guideBean.getCdOperation().equals(ServiceConstants.ADD)
									&& !guideBean.getGuidePlan().equals("") && null != guideBean.getGuidePlan()) {
								contactGuideDao.saveNarrColCargvr(guideBean);

							} else if (guideBean.getCdOperation().equals(ServiceConstants.UPDATE)
									&& !ObjectUtils.isEmpty(guideBean.getIdContactGuideNarr())
									&& guideBean.getIdContactGuideNarr() > 0) {
								contactGuideDao.updateContactGuidePlan(guideBean);
							} else if (guideBean.getCdOperation().equals(ServiceConstants.DELETE)) {
								contactGuideDao.deleteContactGuidePlan(guideBean);
							}
						} // end of else if collateral
						else if (guideBean.getGuideplanType().equals(ServiceConstants.CGTXTTYP_CGVR)) {
							if (guideBean.getCdOperation().equals(ServiceConstants.ADD)) {
								/*
								 * A Caregiver record always needs to exist. This is because the Primary Key of
								 * the Caregiver record is needed to fetch the related Guide topics. There could
								 * be a scenario where there is no Guide Narrative for Caregiver but it may
								 * contain some Guide Topics.
								 */
								contactGuideDao.saveNarrColCargvr(guideBean);// save
								// caregiver
								// narrative
								if (guideBean.getSelectedGuideTopics() != null
										&& !guideBean.getSelectedGuideTopics().isEmpty()) {
									saveCaregvrGuideTopics(guideBean);
								}

							} else if (guideBean.getCdOperation().equals(ServiceConstants.UPDATE)) {

								if (null != guideBean.getGuidePlan()) {
									contactGuideDao.updateContactGuidePlan(guideBean);
								}
								contactGuideDao.deleteContactGuideTopic(guideBean);
								if (guideBean.getSelectedGuideTopics() != null
										&& !guideBean.getSelectedGuideTopics().isEmpty()) {
									saveCaregvrGuideTopics(guideBean);
								}
							}
						}
					}
				}
			}

		} else {
			if (contactGuideList != null && !contactGuideList.isEmpty()) {
				deleteGuidePlanInfo(contactGuideList, saveGuideContactReq.getIdEvent(),
						saveGuideContactReq.getIdCase());
			}

		}

		return contactGuideList;
	}

	// save caregiver guide topics
	private void saveCaregvrGuideTopics(ContactGuideDto guideBean) {
		List<String> selTopics = guideBean.getSelectedGuideTopics();
		for (String guideTopic : selTopics) {
			contactGuideDao.saveCaregvrGuideTopics(guideBean, guideTopic);
		}
	}

	// if selectedGuideTopics List is not null
	private void updateGuideTopics(ContactGuideDto guideBean) {
		List<String> selTopics = guideBean.getSelectedGuideTopics();
		for (String guideTopic : selTopics) {
			contactGuideDao.saveGuideTopics(guideBean, guideTopic);
		}
	}

	/**
	 * Method to delete the Contact guide information when Contact is deleted.
	 *
	 * @param contactGuideList
	 * @param idEvent
	 * @param idCase
	 * @return
	 */
	public List<ContactGuideDto> deleteGuidePlanInfo(List<ContactGuideDto> contactGuideList, Long idEvent,
													 Long idCase) {
		if (contactGuideList != null && !contactGuideList.isEmpty()) {
			for (int i = 0; i < contactGuideList.size(); i++) {
				ContactGuideDto guideBean = new ContactGuideDto();
				guideBean = contactGuideList.get(i);
				guideBean.setIdEvent(idEvent);
				guideBean.setIdCase(idCase);
				if (guideBean.getIdContactGuideNarr() > 0) {
					contactGuideDao.deleteContactGuideTopic(guideBean);
					contactGuideDao.deleteContactGuidePlan(guideBean);
				}

			}
		}
		return contactGuideList;
	}

	/**
	 * Method Name: saveContactDtls Method Description:This method saves the contact
	 * and related objects (event_person_link, guide topics, contact person
	 * narrative) etc.
	 *
	 * @param saveContactReq
	 * @return ContactDetailsRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public ChildContactDto saveContactDtls(SaveGuideContactReq saveGuideContactReq) {
		ContactAUDDto deleteContactDto = saveGuideContactReq.getDeleteContactDto();
		ChildContactDto childContactDto = new ChildContactDto();
		childContactDto = contactDetailSaveService.audContactDetailRecord(deleteContactDto);
		if ((ServiceConstants.CCNTCTYP_GKIN.equals(deleteContactDto.getCdContactType())
				|| ServiceConstants.CCNTCTYP_GKNS.equals(deleteContactDto.getCdContactType()))) {
			saveContactPersonNarrInfo(saveGuideContactReq.getContactPersonNarrValueDto(), childContactDto.getIdEvent(),
					saveGuideContactReq.getIdCase());
		} else {
			// save contact guide plan and topics
			saveGuideContactReq.setIdEvent(childContactDto.getIdEvent());
			contactAUD(saveGuideContactReq);

		}
		return childContactDto;
	}

	/**
	 * Method Description: This method is used to retrieve the information for FBSS
	 * Closing Letter form by passing idperson,idStage and FormName as input request
	 *
	 *
	 * @param fbssClosingLetterReq
	 * @return PreFillDataServiceDto @
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getFbssClosingLetter(FbssClosingLetterReq fbssClosingLetterReq) {

		// Initialising the Variables
		Person person = null;
		FbssClosingLetterDto fbssClosingLetterDto = new FbssClosingLetterDto();
		AddressValueDto addressValueDto = new AddressValueDto();
		PersonValueDto personValueDto = new PersonValueDto();
		AddressValueDto workerAddressValueDto = new AddressValueDto();
		MailDto mailDto;
		NameDto nameDto=new NameDto();

		// Warranty Defect Fix - 11375 - Modified the Logic to get the Primary Worker Id on the stage
		// Fetch DFPS Case Worker & Supervisor Details - Call CINV51D
		Long idPersonCaseWrkr = workLoadDao.getPersonIdByRole(fbssClosingLetterReq.getIdStage(),
				ServiceConstants.STAGE_PERS_ROLE_PR);

		// Warranty Defect Fix 11618 - Added Condition to Check for 0L
		//For Closed Stage fetch the Historic Primary Data
		if(ObjectUtils.isEmpty(idPersonCaseWrkr)||idPersonCaseWrkr==0L)
		{
			idPersonCaseWrkr=stagePersonLinkDao.getPersonIdByRole(fbssClosingLetterReq.getIdStage(), ServiceConstants.HIST_PRIM_WORKER);
		}

		// Fetchning and Setting Values for Person Name Details
		person = personDao.getPersonByPersonId(fbssClosingLetterReq.getIdPerson());
		fbssClosingLetterDto.setPerson(person);

		// Fetching and Setting Person Address Details
		addressValueDto = addressDao.fetchCurrentPrimaryAddress(fbssClosingLetterReq.getIdPerson());
		fbssClosingLetterDto.setPersonaddressValueDto(addressValueDto);

		// retrieves staff mail code and phone details
		mailDto = personDao.getStaffMailAndPhone(idPersonCaseWrkr);
		fbssClosingLetterDto.setMailDto(mailDto);

		// Fetching and Setting Worker Address Details
		if (!ObjectUtils.isEmpty(mailDto) && !ObjectUtils.isEmpty(mailDto.getMailCode())) {
			personValueDto = personDao.getStaffAddress(mailDto.getMailCode());
			// set address
			if (!ObjectUtils.isEmpty(personValueDto)) {
				workerAddressValueDto.setStreetLn1(personValueDto.getStreetLn1());
				workerAddressValueDto.setStreetLn2(personValueDto.getStreetLn2());
				workerAddressValueDto.setCity(personValueDto.getCity());
				workerAddressValueDto.setZip(personValueDto.getZip());
			}
			fbssClosingLetterDto.setWorkeraddressValueDto(workerAddressValueDto);
		}

		// Fetchning and Setting the Worker Name Details
		person = personDao.getPersonByPersonId(idPersonCaseWrkr);
		if(!ObjectUtils.isEmpty(person))
		{
			nameDto.setNmNameFirst(person.getNmPersonFirst());
			nameDto.setNmNameLast(person.getNmPersonLast());
			nameDto.setNmNameMiddle(person.getNmPersonMiddle());
			nameDto.setCdNameSuffix(person.getCdPersonSuffix());
		}
		fbssClosingLetterDto.setNameDto(nameDto);

		return fbssClosingLetterPrefillData.returnPrefillData(fbssClosingLetterDto);

	}

	/**
	 * Method Description: This method is used to return Flag which enables FBSS
	 * Closing Letter and FBSS Closing Spanish Letter dropdown in PersonDetails Page
	 *
	 * @param displayFbssClosingLetterReq
	 * @return boolean @
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean displayFbssClosingLetter(DisplayFbssClosingLetterReq displayFbssClosingLetterReq) {

		boolean displayFbssClosingLetter = false;
		Long idStage = 0L;
		Long idPerson = 0L;
		Long idCase = 0L;

		DisplayFbssClosingLetterDto displayFbssClosingLetterDto = new DisplayFbssClosingLetterDto();

		idStage = displayFbssClosingLetterReq.getIdStage();
		idPerson = displayFbssClosingLetterReq.getIdPerson();
		idCase = displayFbssClosingLetterReq.getIdCase();
		displayFbssClosingLetterDto = personDao.getDisplayStatus(idPerson, idStage, idCase);
		if (displayFbssClosingLetterDto.HasValue == 1) {
			displayFbssClosingLetter = true;
		} else {
			displayFbssClosingLetter = false;
		}
		return displayFbssClosingLetter;

	}

	/**
	 * Method Name: getReasonRlngshmntCodes Method Description: service class to get
	 * the reason Rlngshmnt codes
	 *
	 * @param
	 * @return Map<String, String>
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Map<String, String> getReasonRlngshmntCodes() {
		return contactDetailsDao.getReasonRlngshmntCodes();
	}

	/**
	 * ADS Service Name: sendAlerToPrimary . Method Description: This method is used
	 * to send an Alert to the Primary assigned staff when the LPS Worker initially
	 * saves a Contact with a purpose of “CVS Monthly Required (FTF)”
	 *
	 * @param SaveContactRes
	 * @return ContactDetailsRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SaveContactRes sendAlerToPrimary(ContactReq contactReq) {
		SaveContactRes saveContactRes = new SaveContactRes();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		TodoDto todoDto = new TodoDto();
		// Setting the values in the request for TO_DO table
		todoDto.setCdTodoType(ServiceConstants.TODO_ACTIONS_ALERT);
		todoDto.setDtTodoCreated(new Date());
		todoDto.setDtTodoCompleted(new Date());
		todoDto.setDtTodoDue(new Date());
		todoDto.setCdTodoTask(ServiceConstants.CPS_CONTACT_TASK);
		todoDto.setIdTodoStage(contactReq.getIdStage());
		todoDto.setIdTodoEvent(contactReq.getIdEvent());
		// get Child Name

		todoDto.setTodoDesc(
				ServiceConstants.TODO_DESCRIPTION.concat(contactReq.getStage()).concat(ServiceConstants.PERIOD));
		todoDto.setTodoLongDesc(ServiceConstants.EMPTY_STRING);
		// get Primary staff
		CommonHelperRes res = personListService.getPrimaryCaseworkerForStage(contactReq.getIdStage());
		todoDto.setIdTodoPersAssigned(res.getUlIdPerson());
		// Calling the dao implementation to insert a new record in the _TODO
		// table
		todoDao.todoAUD(todoDto, serviceReqHeaderDto);
		saveContactRes.setStatusResponse(ServiceConstants.SUCCESS);
		return saveContactRes;
	}

	/**
	 * Method Name: getErrorMessage Method Description:This method Generates error
	 * messages for GOLD Gua Ref on contact launch.Before cbgr and ccgr redirect to
	 * the Save routine, stop with error messages from ccon31s if required fields
	 * are null.
	 *
	 * @param contactReq
	 * @return List<String>
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Integer> getErrorMessage(ContactReq contactReq) {
		// Declaring local variables.
		Long idPerson = ServiceConstants.ZERO;
		Long idSecondaryWorker = ServiceConstants.ZERO;
		Long idPrimaryWorker = ServiceConstants.ZERO;
		int citizenFlag = ServiceConstants.Zero_INT;
		int allegationFlag = ServiceConstants.Zero_INT;
		int intSelf = ServiceConstants.NotExists;
		int j = ServiceConstants.Zero_INT;
		String cdEmployeeClass = ServiceConstants.EMPTY_STRING;
		String cdStage = ServiceConstants.EMPTY_STRING;
		Long idAllegationStage = contactReq.getIdStage();
		List<CharacteristicsDto> charDtlList = null;
		List<Integer> errorMsgList = new ArrayList<>();
		Date date = new Date();
		// DAM CLSC0DD - Retrieves all the records in the STAGE_PERSON_LINK
		// table for the ID STAGE given as input
		List<PersonDto> personList = contactDetailsDao.getAllPersonRecords(contactReq.getIdStage());
		for (PersonDto personDto : personList) {
			// The SELF id_person is passed to DAM CLSC0DD or the proposed
			// ward's role is CLIENT.
			if (ServiceConstants.SELF.equals(personDto.getCdStagePersRelInt())
					|| ServiceConstants.PERSON_ROLE_CLIENT.equals(personDto.getCdStagePersRelInt())) {
				idPerson = personDto.getIdPerson();
				intSelf = j;
			}
			if (ServiceConstants.SECONDARY_WORKER.equals(personDto.getCdStagePersRole())) {
				idSecondaryWorker = personDto.getIdPerson();
			}
			if (ServiceConstants.PRIMARY_WORKER.equals(personDto.getCdStagePersRole())) {
				idPrimaryWorker = personDto.getIdPerson();
			}
			j++;
		}
		PersonDto selfPersonDto = personList.get(intSelf);

		if (ObjectUtils.isEmpty(selfPersonDto.getPersonEthnicGroup())) {
			errorMsgList.add(ServiceConstants.MSG_GUA_REF_RACE_REQD);
		}
		/*
		 ** compare to null
		 */
		if (ObjectUtils.isEmpty(selfPersonDto.getCdPersonSex())) {
			errorMsgList.add(ServiceConstants.MSG_GUA_REF_SEX_REQD);
		}
		/*
		 ** compare to null
		 */
		if (ObjectUtils.isEmpty(selfPersonDto.getCdEthniCity())) {
			errorMsgList.add(ServiceConstants.MSG_GUA_REF_ETHN_REQD);
		}
		/*
		 ** compare to null
		 */
		if ((ObjectUtils.isEmpty(selfPersonDto.getCdPersonCitizenShip()))
				&& (ServiceConstants.CCNTCTYP_CCGR.equals(contactReq.getCdContactType()))) {
			errorMsgList.add(ServiceConstants.MSG_GUA_REF_CTZN_REQD);
		}

		if (ServiceConstants.CCNTCTYP_CBGR.equals(contactReq.getCdContactType())) {
			// DAM CLSC1BD This DAM receives ID PERSON and returns one or more
			// rows from the ADDRESS_PERSON_LINK APL, PERSON_ADDRESS tables.
			List<PersonDto> personAddressList = contactDetailsDao.getPersonsAddrDtls(idPerson);
			for (PersonDto personDto : personAddressList) {
				// The desired subset of address types.
				if ((ServiceConstants.RESIDENCE.equals(personDto.getAddrPersonLink()))
						|| (ServiceConstants.FACILITY.equals(personDto.getAddrPersonLink()))
						|| (ServiceConstants.BUSINESS.equals(personDto.getAddrPersonLink()))
						|| (ServiceConstants.FAMILY_TYPE.equals(personDto.getAddrPersonLink()))
						|| (ServiceConstants.FRIEND_NEIGHBOR.equals(personDto.getAddrPersonLink()))
						|| (ServiceConstants.SCHOOL.equals(personDto.getAddrPersonLink()))
						|| (ServiceConstants.OTHERXX.equals(personDto.getAddrPersonLink()))) {
					// get ward addresses, test for errors
					// MSG_GUA_REF_ADDR_REQD.
					if ((ObjectUtils.isEmpty(personDto.getAddrPersonStLn1()))
							|| (ObjectUtils.isEmpty(personDto.getAddrPersonCity()))
							|| (ObjectUtils.isEmpty(personDto.getAddrPersonZip()))
							|| (ObjectUtils.isEmpty(personDto.getCdPersonCounty()))
							|| (ObjectUtils.isEmpty(personDto.getCdPersonState()))) {
						errorMsgList.add(ServiceConstants.MSG_GUA_REF_ADDR_REQD);
					}
				}
			}
			if (CollectionUtils.isEmpty(personAddressList)) {
				errorMsgList.add(ServiceConstants.MSG_GUA_REF_ADDR_REQD);
			}
		}
		// DAM CSEC02D - This dam will return the generic case information
		// needed for all forms. Get stage info including case id
		if(!mobileUtil.isMPSEnvironment()){
			GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(contactReq.getIdStage());
			cdStage = genericCaseInfoDto.getCdStage();
			if (ServiceConstants.CSTAGES_SVC.equals(cdStage)) {
				// DAM CCMNB5D - This DAM returns the prior stage ID for a SVC stage
				// which will be the INV stage where are the allegations.Get prior
				// stage id from if cd_stage is SVC.
				StageLinkDto stageLinkDto = contactDetailsDao.getRecentClosedIdStage(contactReq.getIdStage());
				idAllegationStage = stageLinkDto.getIdPriorStage();
			}
		}
		// Get allegation message for CBGR MSG_GUA_REF_DISP_REQD.
		if (ServiceConstants.CCNTCTYP_CBGR.equals(contactReq.getCdContactType())) {
			// DAM CLSSABD - This dam will retrieve all of the unique allegation
			// types for a given idAllegationStage.get list of unique
			// allegations.
			List<AllegationDto> allegationList = contactDetailsDao.getDistinctAllgtnList(idAllegationStage);
			for (AllegationDto allegationDto : allegationList) {
				if (ServiceConstants.CAPSALDP_VAL.equals(allegationDto.getCdAllegDisposition())
						|| ServiceConstants.CAPSALDP_VRC.equals(allegationDto.getCdAllegDisposition())) {
					allegationFlag = ServiceConstants.NBR_VERSION;
				}
			}
			if (0 == allegationFlag) {
				errorMsgList.add(ServiceConstants.MSG_GUA_REF_DISP_REQD);
			}

		}
		// get citizenship message for CBGR - MSG_GUA_REF_CTZN_REQD.
		if (ServiceConstants.CCNTCTYP_CBGR.equals(contactReq.getCdContactType())) {
			// DAM CLSS60D - Retrieves all the records in the CHARACTERISTICS
			// table for the ID PERSON given as input
			charDtlList = characteristicsDao.getCharDtls(idPerson, date, date);
			for (CharacteristicsDto characteristicsDto : charDtlList)
				// Get citizenship from CHARACTERISTICS.
				if ((ServiceConstants.CITIZEN.equals(characteristicsDto.getCdCharacteristic()))
						|| (ServiceConstants.RESIDENT.equals(characteristicsDto.getCdCharacteristic()))
						|| (ServiceConstants.UNQUALIFIED.equals(characteristicsDto.getCdCharacteristic()))
						|| (ServiceConstants.UNDETERMINED.equals(characteristicsDto.getCdCharacteristic()))) {
					citizenFlag = ServiceConstants.NBR_VERSION;
				}
			if (citizenFlag == 0) {
				errorMsgList.add(ServiceConstants.MSG_GUA_REF_CTZN_REQD);
			}
		}

		// Get secondary job code for CCGR - MSG_GUA_REF_2ND_REQD.
		if (ServiceConstants.CCNTCTYP_CCGR.equals(contactReq.getCdContactType())) {
			// DAM CSEC01D - This dam will retrieve all worker info based upon
			// an Id Person.
			ContactActiveAddrDto contactActiveAddrDto = contactDetailsDao.getContactActiveAddr(idSecondaryWorker);
			cdEmployeeClass = contactActiveAddrDto.getCdEmployeeClass();
			if ((0 == idSecondaryWorker) || (idSecondaryWorker == idPrimaryWorker)
					|| (ServiceConstants.DD_SPECIALIST.equals(cdEmployeeClass))) {
				errorMsgList.add(ServiceConstants.MSG_GUA_REF_2ND_REQD);
			}
		}
		return errorMsgList;
	}


	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ContactSearchRes searchExtOrCommencementContact(ContactSearchReq contactSearchReq) {

		List<ContactSearchListDto> contactList = new ArrayList<>();
		ContactSearchDto contactSearchDto = new ContactSearchDto();
		contactSearchDto.setSzCdContactType(contactSearchReq.getCdContactType());
		contactSearchDto.setUlIdStage(contactSearchReq.getIdStage().get(0));
		contactSearchDto.setSzCdEventStatus(contactSearchReq.getCdEventStatus());
		// Call the dao implementation to retrieve the contact based on the
		// contact type
		// and stage
		List<ContactPurposeDto> list = contactSearchDao.searchContacts(contactSearchDto).getContactDetailSearchDto()
				.getContactPurposeDtos();
		for (ContactPurposeDto contactPurposeDto : list) {
			ContactSearchListDto contactSearchListDto = new ContactSearchListDto();
			BeanUtils.copyProperties(contactPurposeDto, contactSearchListDto);
			contactList.add(contactSearchListDto);
		}

		ContactSearchRes result = new ContactSearchRes();
		result.setContactList(contactList);
		return result;

	}




	/**
	 * Method Description: This method gets the Narrative
	 * Count from RISK_ASSMT_IRA_NARR and RISK_ASSMT_NARR which helps to set the DocType
	 * for Structured Narrative
	 *
	 * @param ContactSearchReq
	 * @return String
	 */
	//Warranty Defect - 11243 - To display both Versions of Structured Narrative RISKSF and CIV33O00
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getRiskNarrExists(ContactSearchReq contactSearchReq) {

		return contactDetailsDao.getRiskNarrExists(contactSearchReq.getIdCase(), contactSearchReq.getIdEvent());

	}

	/**
	 *  artf258107
	 *  Method to get the last updated timestamp of a contact based on the event id
	 * @param commonHelperReq
	 * @return
	 */




	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes getContactDtLastUpdateByEventId(CommonHelperReq commonHelperReq) {
		Contact contact = contactDetailsDao.getContact(commonHelperReq.getIdEvent());
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if(!ObjectUtils.isEmpty(contact)) {
			commonHelperRes.setDtLastUpdate(contact.getDtLastUpdate());
			commonHelperRes.setIdEvent(contact.getIdEvent());
		}

		return commonHelperRes;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes getEarliestContactDate(CommonHelperReq commonHelperReq) {
		ContactStageDiDto contactStageDiDto=new ContactStageDiDto();
		contactStageDiDto.setUlIdStage(commonHelperReq.getIdStage());
		ContactStageDoDto contactStageDoDto = contactOccuredDao.consys15dQueryDao(contactStageDiDto);
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if(!ObjectUtils.isEmpty(contactStageDoDto.getDtDTContactOccurred())) {
			commonHelperRes.setContactEarliestDate(new SimpleDateFormat(DATE_FORMAT).format(contactStageDoDto.getDtDTContactOccurred()));
		}
		return commonHelperRes;
	}

	public ContactDto getContactById(Long eventId){
		return contactDao.getContactById(eventId);
	}

	public StageValueBeanDto getStageByStageId(Long idStage){
		return stageUtilityService.retrieveStageInfo(idStage);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Integer getCountOFContactsInStage(Long idStage){
		return contactSearchDao.getCountOFContactsInStage(idStage);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ContactSearchRes searchContactsForAPIPagination(ContactSearchReq contactSearchReq,int offset,int pageSize ) {
		ContactSearchRes result = new ContactSearchRes();
		result.setContactList(contactSearchDao.searchContactsForAPIPagination(contactSearchReq.getPersonIds(),
				contactSearchReq.getIndPersonPhonePrimary(), contactSearchReq.getCdPersonPhoneType(),
				contactSearchReq.getCdEventStatus(), contactSearchReq.getIdCase(),
				contactSearchReq.getDtScrSearchDateFrom(), contactSearchReq.getDtScrSearchDateTo(),
				contactSearchReq.getIdEvent(), contactSearchReq.getCdContactType(),
				contactSearchReq.getCdContactPurpose(), contactSearchReq.getCdContactMethod(),
				contactSearchReq.getCdContactLocation(), contactSearchReq.getCdContactOthers(),
				contactSearchReq.getIdStage(),offset,pageSize));
		return result;
	}


	// CANIRSP-23 For I&R Staff Search
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public StageSearchRes stageSearch(StageSearchReq stageSearchBean) {
    StageSearchRes retVal = contactSearchDao.stageSearch(stageSearchBean);
    // CANIRSP-465 caseworker id is passed differently when a supervisor is viewing a caseworker's workload.
    Long userId;
    if (stageSearchBean.getIdStaff() != null && stageSearchBean.getIdStaff() != 0l) {
      userId = stageSearchBean.getIdStaff();
    } else {
      userId = Long.valueOf(stageSearchBean.getUserId());
    }

    // if user has the sensitive case permission, they are automatically not blocked. so reversing that they con only be
    // blocked if they don't have sensitive case permission.
    if (!ObjectUtils.isEmpty(retVal.getStageSearchResultList()) &&
        (stageSearchBean.getHasSensitiveAccess() == null || !stageSearchBean.getHasSensitiveAccess()) &&
        contactSearchDao.isCaseSensitive(retVal.getStageSearchResultList().get(0).getIdCase()) &&
        !contactSearchDao.hasSensitiveAccess(userId, retVal.getStageSearchResultList().get(0).getIdCase()))
    {
      retVal.setSensitive(true);
    }
    return retVal;
  }

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public InrFollowupPendingRes getFollowupPending(InrFollowupPendingReq stageSearchReq) {
		InrFollowupPendingRes retVal = new InrFollowupPendingRes();
		List<InrContactFollowUpPendingDto> dtoList = contactSearchDao.getFollowupPending(stageSearchReq);
		retVal.setFollowupPendingList(dtoList);
		return retVal;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ConGuideFetchOutDto getContactFollowupDetails(Long idEvent) {
		Long groupNum = contactProcessDao.getInrGroupNum(idEvent);

		ConGuideFetchOutDto conGuideFetchOutDto = new ConGuideFetchOutDto();
		ContactFieldDiDto contactFieldDiDto = new ContactFieldDiDto();
        if(groupNum !=null){
			contactFieldDiDto.setGroupNum(groupNum);
			List<InrSafetyFieldDto> inrSafetyFieldDtoList = inrSafetyDao.getFollowUpList(contactFieldDiDto);
			conGuideFetchOutDto.setInrSafetyFieldDtoList(inrSafetyFieldDtoList);
		}
		return conGuideFetchOutDto;
	}

}
