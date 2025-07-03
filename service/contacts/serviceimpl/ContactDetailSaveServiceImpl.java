package us.tx.state.dfps.service.contacts.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.domain.InrDuplicateGrouping;
import us.tx.state.dfps.common.domain.InrDuplicateGroupingLink;
import us.tx.state.dfps.common.dto.PostEventPersonDto;
import us.tx.state.dfps.common.web.WebConstants;
import us.tx.state.dfps.service.SDM.dao.SDMSafetyAssessmentDao;
import us.tx.state.dfps.service.admin.dao.*;
import us.tx.state.dfps.service.admin.dto.*;
import us.tx.state.dfps.service.admin.service.ApprovalTaskService;
import us.tx.state.dfps.service.admin.service.EventTaskStageService;
import us.tx.state.dfps.service.admin.service.TodoCreateService;
import us.tx.state.dfps.service.baseriskassmt.dto.MPSStatsValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.response.PostEventRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.contact.dto.ContactSearchDto;
import us.tx.state.dfps.service.contacts.dao.AdApprovalEventDao;
import us.tx.state.dfps.service.contacts.dao.AllegFacilDao;
import us.tx.state.dfps.service.contacts.dao.ApsInvstDao;
import us.tx.state.dfps.service.contacts.dao.ContactDateDao;
import us.tx.state.dfps.service.contacts.dao.ContactEventPersonDao;
import us.tx.state.dfps.service.contacts.dao.ContactNarrativeDao;
import us.tx.state.dfps.service.contacts.dao.ContactProcessDao;
import us.tx.state.dfps.service.contacts.dao.ContactReviewDao;
import us.tx.state.dfps.service.contacts.dao.ContactSearchDao;
import us.tx.state.dfps.service.contacts.dao.CpsInvstDetailUpdateDao;
import us.tx.state.dfps.service.contacts.dao.CriminalRecordsDao;
import us.tx.state.dfps.service.contacts.dao.DeleteEventPersonLinkDao;
import us.tx.state.dfps.service.contacts.dao.EventStageDao;
import us.tx.state.dfps.service.contacts.dao.EventUpdateDao;
import us.tx.state.dfps.service.contacts.dao.FacilAllegPriorReviewDao;
import us.tx.state.dfps.service.contacts.dao.FacilityInvstDtlUpdateDao;
import us.tx.state.dfps.service.contacts.dao.IncomingDetailUpdateDao;
import us.tx.state.dfps.service.contacts.dao.InrSafetyDao;
import us.tx.state.dfps.service.contacts.dao.MPSStatsDao;
import us.tx.state.dfps.service.contacts.dao.StagePersonDao;
import us.tx.state.dfps.service.contacts.dao.StagePriorDao;
import us.tx.state.dfps.service.contacts.dao.StagesOverallDispDao;
import us.tx.state.dfps.service.contacts.dao.SysNbrValidationDao;
import us.tx.state.dfps.service.contacts.dao.TodoDeleteDao;
import us.tx.state.dfps.service.contacts.dao.VictimRoleDao;
import us.tx.state.dfps.service.contacts.daoimpl.ContactProcessDaoImpl;
import us.tx.state.dfps.service.contacts.service.ContactDetailSaveService;
import us.tx.state.dfps.service.cpsinv.dto.UsSysNbrMessageCodeArrayDto;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.person.dao.StagePersonRetrvDao;
import us.tx.state.dfps.service.workload.dao.ContactDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.EventPersonLinkDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.*;
import us.tx.state.dfps.xmlstructs.outputstructs.*;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Save/Save
 * And Submit Service for Contact Detail. Jul 27, 2018- 1:10:30 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
public class ContactDetailSaveServiceImpl implements ContactDetailSaveService {

	@Autowired
	private AllegFacilDao allegFacilDao;

	@Autowired
	private StagesOverallDispDao stagesOverallDispDao;

	@Autowired
	private CpsInvstDetailUpdateDao cpsInvstDetailUpdateDao;

	@Autowired
	private ContactProcessDao contactProcessDao;

	@Autowired
	private ContactEventPersonDao contactEventPersonDao;

	@Autowired
	private ContactSearchDao contactSearchDao;

	@Autowired
	private UpdateEventDao updateEventDao;

	@Autowired
	private UpdateToDoDao updateToDoDao;

	@Autowired
	private PersonInfoDao personInfoDao;

	@Autowired
	private TodoCreateService todoCreateService;

	@Autowired
	private SysNbrValidationDao sysNbrValidationDao;

	@Autowired
	private EventUpdateDao eventUpdateDao;

	@Autowired
	private StagePriorDao stagePriorDao;

	@Autowired
	private ContactReviewDao contactReviewDao;

	@Autowired
	private TodoDeleteDao todoDeleteDao;

	@Autowired
	private AdApprovalEventDao adApprovalEventDao;

	@Autowired
	private CriminalRecordsDao criminalRecordsDao;

	@Autowired
	private MPSStatsDao mPSStatsDao;

	@Autowired
	private DeleteEventPersonLinkDao deleteEventPersonLinkDao;

	@Autowired
	private ContactNarrativeDao contactNarrativeDao;

	@Autowired
	private EventProcessDao eventProcessDao;

	@Autowired
	private VictimRoleDao victimRoleDao;

	@Autowired
	private AdminWorkerDao adminWorkerDao;

	@Autowired
	private ApsInvstDao apsInvstDao;

	@Autowired
	private StagePersonDao stagePersonDao;

	@Autowired
	private ContactDateDao contactDateDao;

	@Autowired
	private FacilAllegPriorReviewDao facilAllegPriorReviewDao;

	@Autowired
	private FacilityInvstDtlUpdateDao facilityInvstDtlUpdateDao;

	@Autowired
	private EventStageDao eventStageDao;

	@Autowired
	private IncomingDetailUpdateDao incomingDetailUpdateDao;

	@Autowired
	private EventPersonLinkProcessDao eventPersonLinkProcessDao;

	@Autowired
	private StageUpdateDao stageUpdateDao;

	@Autowired
	private EventTaskStageService eventTaskStageService;

	@Autowired
	private ApprovalTaskService approvalTaskService;

	@Autowired
	private EventService eventService;
	
	@Autowired 
	private EventPersonLinkDao eventPersonLinkDao;

	@Autowired
	private MobileUtil mobileUtil;

	@Autowired
	TodoDao todoDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	private StagePersonRetrvDao stagePersonRetrvDaoImpl;

	@Autowired
	private ContactDao contactDao;

	@Autowired
	private EventPersonLinkAdminDao eventPersonLinkAdminDao;

	@Autowired
	private SDMSafetyAssessmentDao sdmSafetyAssessmentDao;

    @Autowired
    private InrSafetyDao inrSafetyDao;




	/**
	 * 
	 * Method Name: audContactDetailRecord Method Description:ADD, Save/Save And
	 * Submit Service for Contact Detail.
	 * 
	 * @param contactAUDDto
	 * @return ChildContactDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public ChildContactDto audContactDetailRecord(ContactAUDDto contactAUDDto) {

		return contactDetailAud(contactAUDDto);
	}

	/**
	 * 
	 * Method Name: contactDetailAud Method Description:This method
	 * Add/Update/Delete contact and related records
	 * 
	 * @param contactAUDDto
	 * @return ChildContactDto
	 */
	private ChildContactDto contactDetailAud(ContactAUDDto contactAUDDto) {
		ChildContactDto childContactDto = new ChildContactDto();
		boolean actualEventIdFound = false;
		ServiceOutputDto serviceOutputDto = new ServiceOutputDto();
		childContactDto.setServiceOutputDto(serviceOutputDto);
		String cdContactType = contactAUDDto.getCdContactType();
		// TODO we don't do much paramter overwriting anymore, so these orignal... method variable are probably not needed.
		String originalFuncCd = contactAUDDto.getServiceInputDto().getCreqFuncCd();
		// we call contactAUDDto.setIdEvent(contactAUDDto.getSynchronizationServiceDto().getIdEvent()) below, so no need to store existing value
		Long originalSyncEventId = contactAUDDto.getSynchronizationServiceDto().getIdEvent();
		Long originalStageId = contactAUDDto.getSynchronizationServiceDto().getIdStage();
		Date originalLastUpdate = contactAUDDto.getDtLastUpdate();
		List<EventPersonLinkInsertDto> originalOperationList = contactAUDDto.getEventPersonLinkInsertArrayDto() != null ?
				contactAUDDto.getEventPersonLinkInsertArrayDto().getEventPersonLinkInsertDtoList() : null;
		// checking for the contact type
		if (!(ServiceConstants.SVC_TYPE_CHAR_CLOSED.equals(cdContactType)
				|| ServiceConstants.CCNTCTYP_FCCA.equals(cdContactType)
				|| ServiceConstants.CPS_INV_LETTERS.contains(contactAUDDto.getCdContactType())
				|| ServiceConstants.CPS_AR_LETTERS.contains(contactAUDDto.getCdContactType())
				|| ServiceConstants.FBSS_LETTERS.contains(contactAUDDto.getCdContactType())
				|| ServiceConstants.CCNTCTYP_FCFT.equals(contactAUDDto.getCdContactType()))) {
			CheckStageInpDto checkStageInpDto = new CheckStageInpDto();
			checkStageInpDto.setServiceInput(contactAUDDto.getServiceInputDto());
			checkStageInpDto.getServiceInput().setCreqFuncCd(contactAUDDto.getServiceInputDto().getCreqFuncCd());
			checkStageInpDto.setIdStage(contactAUDDto.getSynchronizationServiceDto().getIdStage());
			checkStageInpDto.setCdTask(contactAUDDto.getSynchronizationServiceDto().getCdTask());
			eventTaskStageService.checkStageEventStatus(checkStageInpDto);
		}

		if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())
				&& ServiceConstants.CCNTCTYP_EREV.equals(cdContactType)) {
			serviceOutputDto.setRowQtySize(ServiceConstants.Zero);
			childContactDto.setNbrContact(getCountReqRevContactsForStage(contactAUDDto));
		}

		if (contactAUDDto.getSynchronizationServiceDto().getIdEvent() == ServiceConstants.ZERO_VAL
				&& ServiceConstants.TODO_CONTACT_TYPES.contains(cdContactType)) {
			FindContactDto findContactDto = findNewContacts(contactAUDDto);
			serviceOutputDto.setRowQtySize(findContactDto.getServiceOutputDto().getRowQtySize());
			serviceOutputDto.setBmoreDataInd(findContactDto.getServiceOutputDto().getBmoreDataInd());
			contactAUDDto.getSynchronizationServiceDto()
					.setIdEvent(Long.valueOf(
							!CollectionUtils.isEmpty(findContactDto.getContactDetailSearchDto().getContactPurposeDtos())
									? findContactDto.getContactDetailSearchDto().getContactPurposeDtos()
											.get(ServiceConstants.Zero).getIdEvent()
									: ServiceConstants.ZERO_VAL));
			if (!ObjectUtils.isEmpty(contactAUDDto.getSynchronizationServiceDto().getIdEvent())
					&& ServiceConstants.ZERO_VAL != contactAUDDto.getSynchronizationServiceDto().getIdEvent())
				contactAUDDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			synchronizationServiceDetail(contactAUDDto);
		}

		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())
				|| ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())) {
			if (!(ServiceConstants.CPS_INV_LETTERS.contains(contactAUDDto.getCdContactType())
					|| ServiceConstants.CPS_AR_LETTERS.contains(contactAUDDto.getCdContactType())
					|| ServiceConstants.FBSS_LETTERS.contains(contactAUDDto.getCdContactType())))

			{
				if (!isTrue(contactAUDDto.getServiceInputDto().getUlSysNbrReserved1()) && ServiceConstants.CEVTSTAT_PEND
						.equals(contactAUDDto.getSynchronizationServiceDto().getCdEventStatus())) {
					if (ServiceConstants.MOBILE_IMPACT) {
						throw new IllegalStateException("Tring to invalidate an approval on MPS!");
					} else {
						contactAUDDto.getSynchronizationServiceDto().setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
						invalidateApproval(contactAUDDto,  contactAUDDto.getSynchronizationServiceDto().getIdEvent());
					}
				}
			}

		}

		 if (!isTrue(contactAUDDto.getServiceInputDto().getUlSysNbrReserved1())
				&& !ServiceConstants.ZERO_VAL.equals(contactAUDDto.getIdEvent())
				&& (!ServiceConstants.ANOT.equals(contactAUDDto.getCdContactType())
						&& !ServiceConstants.LNOT.equals(contactAUDDto.getCdContactType()))) {
			if (ServiceConstants.MOBILE_IMPACT) {
				throw new IllegalStateException("Tring to invalidate an approval on MPS!");
			} else {
				updateEventForFatality(contactAUDDto);
				invalidateApproval(contactAUDDto, contactAUDDto.getIdEvent());
			}
		}

		if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())
				&& !(ServiceConstants.CPS_INV_LETTERS.contains(contactAUDDto.getCdContactType())
						|| ServiceConstants.CPS_AR_LETTERS.contains(contactAUDDto.getCdContactType())
						|| ServiceConstants.FBSS_LETTERS.contains(contactAUDDto.getCdContactType())))

		{
			deleteTodo(contactAUDDto);

			if (ServiceConstants.SERVER_IMPACT) {
				deleteApprovalEventLink(contactAUDDto);
			}
		}

		if (ServiceConstants.FACE_TO_FACE.equals(contactAUDDto.getCdContactMethod())) {
			if (ServiceConstants.CPGRMS_APS.equals(contactAUDDto.getCdStageClassification())
					&& !ServiceConstants.CSTAGES_AOC.equals(contactAUDDto.getCdStage())
					&& !ServiceConstants.CSTAGES_ARI.equals(contactAUDDto.getCdStage())) {
				searchContacts(contactAUDDto); // TODO why are we ignoring return value?
			}
		}

		if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd()) &&
				!WebConstants.INR_CONTACT_TYPES.contains(contactAUDDto.getCdContactType())) { // inr delete is handled differently
			deleteEventPersonLink(contactAUDDto);
		}

		contactAUDDto.setIdEvent(contactAUDDto.getSynchronizationServiceDto().getIdEvent());
		Long ulIdEvent = ServiceConstants.ZERO_VAL;

		if (!ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd()) &&
				!WebConstants.INR_CONTACT_TYPES.contains(contactAUDDto.getCdContactType())) {
			// create event id normally if not a delete or I&R Contact
			ulIdEvent = postEvent(contactAUDDto.getServiceInputDto(), contactAUDDto.getSynchronizationServiceDto());
			contactAUDDto.setIdEvent(ulIdEvent);
			deleteEventPersonLinkRecord(contactAUDDto);
		}

		String tmpReqFuncCd = new String();
		if (contactAUDDto.getServiceInputDto().getUlPageSizeNbr() != ServiceConstants.Zero
				|| contactAUDDto.getServiceInputDto().getUlSysNbrReserved2() == ServiceConstants.One) {
			tmpReqFuncCd = contactAUDDto.getServiceInputDto().getCreqFuncCd();
			if (!ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())) {
				contactAUDDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			}
		}
		// CANIRSP-211 the inner IF is complex enough that I can't touch it without breaking it. Just nest 2 ifs instead of trying to combine the logic.
		if (!WebConstants.INR_CONTACT_TYPES.contains(contactAUDDto.getCdContactType())) {
			if (!(!StringUtils.isEmpty(tmpReqFuncCd)
					&& ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd()))) {
				audContact(contactAUDDto);
			}
		}

		// This is the only block that should be handling I&R contacts. other logic need to be skipped.
		if (WebConstants.INR_CONTACT_TYPES.contains(contactAUDDto.getCdContactType())) {
			if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())) {
				// delete the contact and group entirely, and do it bottom up so we don't try to delete anything that still has references to it.
				Long groupId = contactProcessDao.getInrGroupNum(contactAUDDto.getIdEvent());
				Map<Long, SimpleEventStageDto> personIdToDataMap = contactProcessDao.getInrPersonIdToDataMap(groupId);

				// delete from INR_SAFETY_FOLLOWUP
				inrSafetyDao.deleteActionItemsByGroup(groupId);

				// delete from INR_GROUP_TO_STAGE
				contactProcessDao.deleteInstakeStageListForGroup(groupId);

				// delete the contacts/events and grouping
				for (SimpleEventStageDto currEventData : personIdToDataMap.values()) {
					deleteEventFromInrGroup(currEventData.getIdEvent(), currEventData.getIdPerson(), groupId, contactAUDDto.getIdPerson(), personIdToDataMap);
				}

				// delete the group from INR_DUPLICATE_GROUPING
				contactProcessDao.deleteInrGroup(groupId);
			} else {
				// manage multiple contacts
				Set<Long> eventsInGroup = null;

				Map<Long, Long> allegedVictimIdToPcStage = new HashMap<>();
				// note event id will uniquely map to a single person, but person could map to many events so be careful always search by event.
				Map<Long, SimpleEventStageDto> personIdToDataMap = null;

				StagePersonRetrvInDto stagePersonRetrvInDto = new StagePersonRetrvInDto();
				stagePersonRetrvInDto.setUlIdCase(contactAUDDto.getIdCase());
				stagePersonRetrvInDto.setUlIdPerson(contactAUDDto.getIdPerson());
				StagePersonRetrvOutDto allegedVictimDetails = stagePersonRetrvDaoImpl.getAllegedVictimsForStage(stagePersonRetrvInDto);
				if (!ObjectUtils.isEmpty(allegedVictimDetails.getStagePersonRetrvArrayOutDto()) &&
						!ObjectUtils.isEmpty(allegedVictimDetails.getStagePersonRetrvArrayOutDto().getStagePersonRetrvRowOutDtoList())) {
					for (StagePersonRetrvRowOutDto currRecord : allegedVictimDetails.getStagePersonRetrvArrayOutDto().getStagePersonRetrvRowOutDtoList()) {
						allegedVictimIdToPcStage.put(currRecord.getUlIdPerson(), currRecord.getUlIdStage());
					}
				}

				boolean inrGroupIsNew = false;
				Long groupNum = contactProcessDao.getInrGroupNum(contactAUDDto.getIdEvent());
				// get group number from dto or sequence
				if (groupNum == null) { // new group case
					groupNum = contactProcessDao.createInrGroup(contactAUDDto.getCdInrProviderRegType(), contactAUDDto.getTxtNarrativeRpt(), contactAUDDto.getIdPerson());
					contactAUDDto.setInrGroupId(groupNum);
					personIdToDataMap = new HashMap<>(0);
					inrGroupIsNew = true;
				} else { // existing group case
					contactAUDDto.setInrGroupId(groupNum);
					personIdToDataMap = contactProcessDao.getInrPersonIdToDataMap(groupNum);
				}

				// loop through Alleged Victim updates
				// WARNING in this loop we tamper with the request object, altering event id and function code, so it's
				// critical we put it back afterwards.
				if (!ObjectUtils.isEmpty(contactAUDDto.getEventPersonLinkInsertArrayDto())) {
					// deleteEventPersonLinkRecord tries to process the list of operations. Since we're handling that
					// here we need to diable the list when reusing logic. The implication is that this is the wrong place for this logic.
					contactAUDDto.getEventPersonLinkInsertArrayDto().setEventPersonLinkInsertDtoList(null);

					for (EventPersonLinkInsertDto currentAllegedVictimUpdateObj : originalOperationList) {
						switch (currentAllegedVictimUpdateObj.getCdScrDataAction()) {
							case ServiceConstants.REQ_FUNC_CD_ADD:
								// first update STAGE_PERSON_LINK for existing events, add link to new child
								for (SimpleEventStageDto currPersonData : personIdToDataMap.values()) {
									if (!currPersonData.getDeleted()) {
										EventLinkInDto eventPersonLinkInsertDto = new EventLinkInDto();
										eventPersonLinkInsertDto.setIdPerson(currentAllegedVictimUpdateObj.getIdPerson());
										eventPersonLinkInsertDto.setIdEvent(currPersonData.getIdEvent());
										eventPersonLinkInsertDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
										eventPersonLinkInsertDto.setArchInputStruct(new ServiceInputDto());
										eventPersonLinkInsertDto.getArchInputStruct().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
										eventPersonLinkProcessDao.ccmn68dAUDdam(eventPersonLinkInsertDto);
									}
								}

								// create new event
								contactAUDDto.getSynchronizationServiceDto().setIdStage(allegedVictimIdToPcStage.get(Integer.valueOf(currentAllegedVictimUpdateObj.getIdPerson()).longValue()));
								contactAUDDto.getSynchronizationServiceDto().setIdEvent(null);
								contactAUDDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
								ulIdEvent = postEvent(contactAUDDto.getServiceInputDto(), contactAUDDto.getSynchronizationServiceDto());

								// save every event id so we have at least something until the event for THIS stage gets
								// created. Note that it is possible to create a group of contacts that does not include
								// THIS stage. When that happens, we still want to show one of the contacts that just got
								// created, even though they will not be able to find it in the contact list.
								if (!actualEventIdFound) {
									if (ObjectUtils.nullSafeEquals(originalSyncEventId, Long.valueOf(0)) &&
											ObjectUtils.nullSafeEquals(originalStageId, contactAUDDto.getSynchronizationServiceDto().getIdStage())) {
										actualEventIdFound = true;
									}
									childContactDto.setIdEvent(ulIdEvent);
								}

								// this seems like setup for AUDContact below.
								contactAUDDto.setIdEvent(ulIdEvent);

								// Add links for exiting primary children new to event.
								for (SimpleEventStageDto currExistingPersonLink : personIdToDataMap.values()) {
									if (!currExistingPersonLink.getDeleted()) {
										EventLinkInDto eventPersonLinkInsertDto = new EventLinkInDto();
										eventPersonLinkInsertDto.setIdEvent(ulIdEvent);
										eventPersonLinkInsertDto.setIdPerson(currExistingPersonLink.getIdPerson().intValue());
										eventPersonLinkInsertDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
										eventPersonLinkInsertDto.setArchInputStruct(new ServiceInputDto());
										eventPersonLinkInsertDto.getArchInputStruct().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
										eventPersonLinkProcessDao.ccmn68dAUDdam(eventPersonLinkInsertDto);
									}
								}

								// add link for new primary child to new event
								EventLinkInDto eventPersonLinkInsertDto = new EventLinkInDto();
								eventPersonLinkInsertDto.setIdEvent(ulIdEvent);
								eventPersonLinkInsertDto.setIdPerson(currentAllegedVictimUpdateObj.getIdPerson());
								eventPersonLinkInsertDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
								eventPersonLinkInsertDto.setArchInputStruct(new ServiceInputDto());
								eventPersonLinkInsertDto.getArchInputStruct().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
								eventPersonLinkProcessDao.ccmn68dAUDdam(eventPersonLinkInsertDto);

								// update the map of all groups with the new entry, so the next loop can operate correctly and add itself to the new event
								SimpleEventStageDto addedEvent = new SimpleEventStageDto();
								addedEvent.setIdPerson(Integer.valueOf(currentAllegedVictimUpdateObj.getIdPerson()).longValue());
								addedEvent.setIdEvent(ulIdEvent);
								addedEvent.setIdStage(contactAUDDto.getSynchronizationServiceDto().getIdStage());
								addedEvent.setAdded(true);
								personIdToDataMap.put(addedEvent.getIdPerson(), addedEvent);

								// create contact record
								audContact(contactAUDDto);

								// add contact to inr group
								contactProcessDao.audInrGrouping(contactAUDDto);
								break;
							case ServiceConstants.REQ_FUNC_CD_DELETE:
								Map<Long, SimpleEventStageDto> finalPersonIdToDataMap = personIdToDataMap;
								Long idDeleteEvent = personIdToDataMap.get(Integer.valueOf(currentAllegedVictimUpdateObj.getIdPerson()).longValue()).getIdEvent();

								if (idDeleteEvent.equals(originalSyncEventId)) {
									childContactDto.setInrSelfIsDeleted(true);
								}

								deleteEventFromInrGroup(idDeleteEvent, Integer.valueOf(currentAllegedVictimUpdateObj.getIdPerson()).longValue(),
										groupNum, contactAUDDto.getIdPerson(), personIdToDataMap);

								break;
							// 	there is no data in the links to save, so update doesn't make sense
						}
					}

					contactAUDDto.setIdEvent(originalSyncEventId);
					contactAUDDto.getServiceInputDto().setCreqFuncCd(originalFuncCd);
					contactAUDDto.getEventPersonLinkInsertArrayDto().setEventPersonLinkInsertDtoList(originalOperationList);
					contactAUDDto.getSynchronizationServiceDto().setIdStage(originalStageId);
					contactAUDDto.getSynchronizationServiceDto().setIdEvent(originalSyncEventId);
				}

				// loop though existing contacts and see if they need updates. Any newly added above rows will be correct
				// and need not be re-considered. Any deleted contacts need to be actively excluded from updates.
				for (SimpleEventStageDto currExistingContactData : personIdToDataMap.values()) {
					if (!currExistingContactData.getDeleted() && !currExistingContactData.getAdded()) {
						Contact existingContact = contactDao.getContactEntityById(currExistingContactData.getIdEvent());

						if (isDirty(contactAUDDto, existingContact)) {
							contactAUDDto.setIdEvent(currExistingContactData.getIdEvent());
							contactAUDDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
							contactAUDDto.setDtLastUpdate(existingContact.getDtLastUpdate());
							audContact(contactAUDDto);
						}
					}
				}
				contactAUDDto.setIdEvent(originalSyncEventId);
				contactAUDDto.getServiceInputDto().setCreqFuncCd(originalFuncCd);
				contactAUDDto.getSynchronizationServiceDto().setIdStage(originalStageId);
				contactAUDDto.getSynchronizationServiceDto().setIdEvent(originalSyncEventId);
				contactAUDDto.setDtLastUpdate(originalLastUpdate);

				// Link group to intake stages or save out of state exemption to the group
				contactProcessDao.mergeGroupLinkToStages(contactAUDDto.getCdInrProviderRegType(),
						contactAUDDto.getTxtNarrativeRpt(), parseStringToList(contactAUDDto.getIntakeStageList()),
						contactAUDDto.getInrGroupId(), contactAUDDto.getIdPerson(), inrGroupIsNew);

				// process Saftey Actions/Followup Details
				updateFollowupDetails(contactAUDDto);
			}
		}

		if (ServiceConstants.CSTAGES_FPR.equals(contactAUDDto.getCdStage())
				|| ServiceConstants.CSTAGES_ADO.equals(contactAUDDto.getCdStage())
				|| ServiceConstants.CSTAGES_FSU.equals(contactAUDDto.getCdStage())
				|| ServiceConstants.CSTAGES_FRE.equals(contactAUDDto.getCdStage())
				|| ServiceConstants.CSTAGES_SUB.equals(contactAUDDto.getCdStage())
				|| ServiceConstants.CSTAGES_FAD.equals(contactAUDDto.getCdStage())) {
			if (ServiceConstants.SERVER_IMPACT) {
				CriminalRecordsDoDto csesc2do = CallCSESC2D(contactAUDDto);
				if (!ObjectUtils.isEmpty(csesc2do.getUlIdPerson())
						&& csesc2do.getUlIdPerson() > ServiceConstants.Zero) {
					UsSysNbrMessageCodeArrayDto usSysNbrMessageCodeArrayDto = new UsSysNbrMessageCodeArrayDto();

					if (TypeConvUtil.isNullOrEmpty(childContactDto.getMessageCodeDto())) {
						childContactDto.setMessageCodeDto(new MessageCodeDto());
					} else if (!TypeConvUtil
							.isNullOrEmpty(childContactDto.getMessageCodeDto().getUsSysNbrMessageCodeArray())) {
						usSysNbrMessageCodeArrayDto = childContactDto.getMessageCodeDto().getUsSysNbrMessageCodeArray();
					}

					childContactDto.getMessageCodeDto().setUsSysNbrMessageCodeArray(usSysNbrMessageCodeArrayDto);
				}
			}
		}

		if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())) {

			callDeleteContactNarrative(contactAUDDto.getSynchronizationServiceDto().getIdEvent());

		}

		if (!ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd()) &&
				!WebConstants.INR_CONTACT_TYPES.contains(contactAUDDto.getCdContactType())) {
			if (ServiceConstants.CEVTSTAT_COMP.equals(contactAUDDto.getSynchronizationServiceDto().getCdEventStatus())
					&& (!ServiceConstants.ANOT.equals(contactAUDDto.getCdContactType())
							&& !ServiceConstants.LNOT.equals(contactAUDDto.getCdContactType()))) {
				updateTODOEvent(contactAUDDto);
			}

			if (contactAUDDto.getSynchronizationServiceDto().getCdEventStatus().equals(ServiceConstants.CEVTSTAT_COMP)
					&& ServiceConstants.TODO_CONTACT_TYPES.contains(cdContactType)
					&& ServiceConstants.SVC_FUTURE_TODOS_CREATED != contactAUDDto.getServiceInputDto().getUsPageNbr()
					&& !(ServiceConstants.ANOT.equals(contactAUDDto.getCdContactType())
							|| ServiceConstants.LNOT.equals(contactAUDDto.getCdContactType()))) {
				createNEWContacts(contactAUDDto, ServiceConstants.FALSEVAL, ServiceConstants.Zero);
			}

			if (contactAUDDto.getSynchronizationServiceDto().getCdEventStatus().equals(ServiceConstants.CEVTSTAT_COMP)
					&& (cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_TYPE_PAL_MNTH)
							|| cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_PERS_HOME_STUDY_SUM)
							|| cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_QUARTER_VISIT_SUM)
							|| cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_REGULAR_SUM)
							|| cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_CORRECT_ACT_SUM)
							|| cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_DEVELOP_PLAN_SUM)
							|| cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_CLOSING_SUM_SUM)
							|| cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_MONTH_ASSESS_SUM)
							|| cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_REEVALUATION_SUM)
							|| cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_SERIOUS_INC_SUM)
							|| cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_VARIANCE_SUM)
							|| cdContactType.endsWith(ServiceConstants.SVC_CD_CONTACT_VIOLATION_SUM))) {

				searchContactWindow(contactAUDDto, childContactDto);
			}
		}

		if (ServiceConstants.SVC_CD_CONTACT_REQUEST_REVIEW.equals(cdContactType)
				&& ServiceConstants.N.equals(contactAUDDto.getIndReview())) {

			getAllegationFacilAllegPerson(contactAUDDto);

		}

		if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())
				&& ServiceConstants.REQUEST_FOR_REVIEW.equals(cdContactType)) {
			if (childContactDto.getNbrContact() == ServiceConstants.One) {

				deleteFacilityAllegationPriorReview(contactAUDDto);

				updateFacilityInvestigationDetail(contactAUDDto);
			}
		}

		if (ServiceConstants.CPGRMS_APS.equals(contactAUDDto.getCdStageClassification())
				&& ServiceConstants.CSTAGES_INV.equals(contactAUDDto.getCdStage())
				&& ServiceConstants.SVC_CD_CONTACT_TYPE_HOURC24.equals(cdContactType)) {
			if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())
					|| ServiceConstants.REQ_FUNC_CD_ADD.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())) {

				getEarliestContactDate(contactAUDDto, childContactDto);
			}

		}

		if (ServiceConstants.CPGRMS_CPS.equals(contactAUDDto.getCdStageClassification())
				&& ServiceConstants.CSTAGES_SUB.equals(contactAUDDto.getCdStage())
				&& ServiceConstants.SVC_CD_CONTACT_TYPE_SUB_CLOS_SUM.equals(cdContactType)) {
			getIdPersonForDifferentRole(contactAUDDto, childContactDto);

			// Calculate person Age
			if (!ObjectUtils.isEmpty(childContactDto.getIdPalStage())
					&& ServiceConstants.Zero < childContactDto.getIdPalStage()) {
				// Check # of PAL training elements
				getUlSysNbrValidationMsg(contactAUDDto, childContactDto);
				if (ServiceConstants.Y.equals(childContactDto.getBindSendPalFollowup())) {
					getPalWorkerAssigned(contactAUDDto, childContactDto);
					if (ServiceConstants.ZERO_VAL < childContactDto.getIdPalWorker()) {
						sendToDoToPalWrkr(contactAUDDto, childContactDto);
					}
				}
			}
		}

		if (ServiceConstants.CPGRMS_AFC.equals(contactAUDDto.getCdStageClassification())
				&& ServiceConstants.CSTAGES_INV.equals(contactAUDDto.getCdStage())
				&& ServiceConstants.SVC_CD_CON_AFC_IFF_E.equals(cdContactType)
				&& ServiceConstants.FALSE.equals(contactAUDDto.getIndVictimSelected())) {
			if (contactAUDDto.getServiceInputDto().getCreqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
					|| contactAUDDto.getServiceInputDto().getCreqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
				checkPersonVictim(contactAUDDto, childContactDto);
			}
		}

		if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd()) &&
			!WebConstants.INR_CONTACT_TYPES.contains(contactAUDDto.getCdContactType())) {
			audEvent(contactAUDDto);
		}

		if (ServiceConstants.CPGRMS_CPS.equals(contactAUDDto.getCdStageClassification())
				&& ServiceConstants.CSTAGES_INV.equals(contactAUDDto.getCdStage())
				&& ServiceConstants.CCNTPURP_BNTL.equals(contactAUDDto.getCdContactPurpose())) {
			if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())
					|| ServiceConstants.REQ_FUNC_CD_DELETE.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())) {
				updateInitialContactDate(contactAUDDto);
			}
		}

		// if this is an INR ADD, we created multiple events and have already saved the most relevant one, so keep it and don't overwrite it.
		if (!WebConstants.INR_CONTACT_TYPES.contains(contactAUDDto.getCdContactType()) ||
				!ServiceConstants.REQ_FUNC_CD_ADD.equals(contactAUDDto.getServiceInputDto().getCreqFuncCd())) {
			childContactDto.setIdEvent(contactAUDDto.getIdEvent());
		}

		if (mobileUtil.isMPSEnvironment()) {
			callMPSStatsHelper(contactAUDDto);
		}

		boolean taskComplete = true;
		if(ServiceConstants.SCIA.equalsIgnoreCase(contactAUDDto.getCdContactPurpose())){
			Date intStartDate = stageUpdateDao.getIntakeStageStartDt(contactAUDDto.getSynchronizationServiceDto().getIdStage());
			if(null!=intStartDate  ){
				Date intStartDatePlus10d = DateUtils.addToDate(intStartDate, 0, 0, 11);
				intStartDatePlus10d.setHours(0);
				intStartDatePlus10d.setMinutes(0);
				intStartDatePlus10d.setSeconds(0);
				if((DateUtils.getCurrentDate()).after(intStartDatePlus10d)){
					taskComplete = false;
				}
			}
		}

		// CANIRSP-68 I&R A/N contacts don't have tasks, so skip trying to delete them
		if (taskComplete && !WebConstants.INR_CONTACT_TYPES.contains(contactAUDDto.getCdContactType())) {
			if (contactAUDDto.getServiceInputDto().getCreqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
					|| contactAUDDto.getServiceInputDto().getCreqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_ADD)
					&& ( ServiceConstants.CSTAGES_INV.equals(contactAUDDto.getCdStage()) &&
						ServiceConstants.ACCI.equals(contactAUDDto.getCdContactType()))) {
				List<TodoDto> toDos = new ArrayList<>();
				if(ServiceConstants.SCIA.equalsIgnoreCase(contactAUDDto.getCdContactPurpose())){
					 toDos = todoDao.getTasksByStageIdTask(contactAUDDto.getSynchronizationServiceDto().getIdStage(),
							 ServiceConstants.TASK_CODE_CCI_INTERIM_STAFFING);
				} else{
					 toDos = todoDao.getTasksByStageIdTask(contactAUDDto.getSynchronizationServiceDto().getIdStage(),
							contactAUDDto.getSynchronizationServiceDto().getCdTask());
				}

				List<TodoDto> compTodos = new ArrayList<TodoDto>();

				for (TodoDto todo : toDos) {
					if ("T".equals(todo.getCdTodoType())) {
						todo.setDtTodoCompleted(new Date());
						compTodos.add(todo);
					}
				}
				todoDao.updateToDosDao(compTodos);
				todoDao.deleteAlertsByStageAndTask(contactAUDDto.getSynchronizationServiceDto().getIdStage(),contactAUDDto.getSynchronizationServiceDto().getCdTask());

			}

		}

		return childContactDto;

	}

  private boolean isDirty(ContactAUDDto contactAUDDto, Contact existingContact) {
    return !ObjectUtils.nullSafeEquals(
        contactAUDDto.getDtContactOccurred(), existingContact.getDtContactOccurred())
        || !ObjectUtils.nullSafeEquals(contactAUDDto.getDtNotification(), existingContact.getDtNotification())
        || !ObjectUtils.nullSafeEquals(contactAUDDto.getTxtSummDiscuss(), existingContact.getTxtSummDiscuss())
        || !ObjectUtils.nullSafeEquals(contactAUDDto.getTxtIdentfdSafetyConc(), existingContact.getTxtIdentfdSafetyConc())
        || !ObjectUtils.nullSafeEquals(contactAUDDto.getIdCaseworker(), existingContact.getIdCaseworker())
        || !ObjectUtils.nullSafeEquals(contactAUDDto.getIdSupervisor(), existingContact.getIdSupervisor())
        || !ObjectUtils.nullSafeEquals(contactAUDDto.getIdDirector(), existingContact.getIdDirector());
  }

	public List<Long> parseStringToList(String split) {
		List<Long> retVal = new LinkedList<>();
		if (split != null && split.length() > 0) {
			if (split.contains(",")) {
				String[] splitted = split.split(",");
				for (int i = 0; i < splitted.length; i++) {
					Long candidate = tryParseLong(splitted[i]);
					if (candidate != null) {
						retVal.add(candidate);
					}
				}
			} else {
				Long candidate = tryParseLong(split);
				if (candidate != null) {
					retVal.add(candidate);
				}
			}
		}
		return retVal;
	}

	private Long tryParseLong(String s) {
		Long retVal = null;
		if (s != null && s.length() > 0) {
			try {
				retVal = Long.parseLong(s);
			} catch (NumberFormatException nfe) {
				// do nothing
			}
		}
		return retVal;
	}

	//For Conclusion page, event and approver status change during add or update
	private void invalidateApproval(ContactAUDDto contactAUDDto, Long invalidateIdEvent) {
		if (invalidateIdEvent != ServiceConstants.ZERO_VAL) {
			if (ServiceConstants.MOBILE_IMPACT) {
				throw new IllegalStateException("Tring to invalidate an approval on MPS!");
			} else {
				ApprovalTaskDto approvalTaskDto = new ApprovalTaskDto();
				approvalTaskDto.setArchInputStructDto((ServiceInputDto) contactAUDDto.getServiceInputDto());
				String ulInvalidateIdEventStr = String.valueOf(invalidateIdEvent);
				int ulInvalidateIdEventInt = Integer.parseInt(ulInvalidateIdEventStr);
				approvalTaskDto.setUlIdEvent(ulInvalidateIdEventInt);
				approvalTaskService.callCcmn05uService(approvalTaskDto);

			}
		}
	}

	/**
	 * Method Name:getCountReqRevContactsForStage Method Description: Retrieves
	 * count of REQ REV from the CONTACT table * given a Stage ID.
	 *
	 * @param deleteContactDto
	 *            the delete contact dto
	 * @return the count req rev contacts for stage
	 */
	private int getCountReqRevContactsForStage(ContactAUDDto deleteContactDto) {
		ContactsForStageDto contactsForStageDto = new ContactsForStageDto();
		contactsForStageDto.setIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage());

		return contactReviewDao.getCountReqRevContactsForStage(contactsForStageDto).getNbrContact();

	}

	/**
	 * 
	 * Method Name: findNewContacts Method Description:Searches for Contacts
	 * which correspond to NEW Events, have a given type, and belong to the
	 * given STAGE. These Contacts will be shell records that must be modified
	 * during this call to CSYS07S().
	 * 
	 * @param deleteContactDto
	 * @return FindContactDto
	 */
	private FindContactDto findNewContacts(ContactAUDDto deleteContactDto) {
		ContactSearchDto contactSearchDto = new ContactSearchDto();
		contactSearchDto.setSzCdContactType(deleteContactDto.getCdContactType());
		contactSearchDto.setSzCdEventStatus(ServiceConstants.CEVTSTAT_NEW);
		contactSearchDto.setUlIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage());
		ServiceInputDto serviceInputDto = new ServiceInputDto();
		serviceInputDto.setUsPageNbr(ServiceConstants.INITIAL_PAGE);
		serviceInputDto.setUlPageSizeNbr(ServiceConstants._CSYS04DO__ROWCSYS04DO_SIZE);
		contactSearchDto.setArchInputStruct(serviceInputDto);
		return contactSearchDao.searchContacts(contactSearchDto);
	}

	/**
	 * 
	 * Method Name: synchronizationServiceDetail Method Description:get contact
	 * details
	 * 
	 * @param deleteContactDto
	 */
	private void synchronizationServiceDetail(ContactAUDDto deleteContactDto) {

		ContactDetailsOutDto contactDetailsOutDto = new ContactDetailsOutDto();
		contactDetailsOutDto.setUlIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage().intValue());
		contactDetailsOutDto.setUlIdEvent(deleteContactDto.getSynchronizationServiceDto().getIdEvent().intValue());
		StageProgramDto stageProgramDto = contactEventPersonDao.getContactDetails(contactDetailsOutDto);
		if (!ObjectUtils.isEmpty(stageProgramDto)) {
			deleteContactDto.setDtLastUpdate2(stageProgramDto.getDtLstUpdate());
			deleteContactDto.getSynchronizationServiceDto().setDtLastUpdate(stageProgramDto.getDtLstUpdt());
		}
	}

	/**
	 * 
	 * Method Name: isTrue Method Description:Checks to see if a given string is
	 * boolean true. This includes checking that the string is not null or
	 * empty.
	 * 
	 * @param value
	 * @return boolean
	 */
	private static boolean isTrue(String value) {
		
		// Fix for defect 12750 - CPS/RCL/CCL - Status of the modified event for a
		// pending investigation conclusion event is not changing to 'COMP' after
		// invalidating approval 
		
		Set<String> positiveSet = new HashSet<String>(Arrays.asList(new String[] { ServiceConstants.INDICATOR_YES,
				ServiceConstants.STRING_IND_Y, ServiceConstants.TRUE}));
		if (isValid(value)) {
			return positiveSet.contains(value.trim());
		} else {
			return ServiceConstants.FALSEVAL;
		}
	}

	/**
	 * 
	 * Method Name: isValid Method Description:Checks to see if a given string
	 * is valid. This includes checking that the string is not null or empty.
	 * 
	 * @param value
	 * @return boolean
	 */
	private static boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		String trimmedString = value.trim();
		return (trimmedString.length() > ServiceConstants.Zero);
	}

	/**
	 * 
	 * Method Name: updateEventForFatality Method Description:USED ONLY FOR
	 * INVALIDATING APPROVALS, SO NOT APPLICAPLE FOR MPS. Updates status column
	 * of EVENT table. Ignores time stamp.
	 * 
	 * @param deleteContactDto
	 */
	private void updateEventForFatality(ContactAUDDto deleteContactDto) {
		UpdateEventiDto updateEventiDto = new UpdateEventiDto();
		ServiceInputDto serviceInputDto = new ServiceInputDto();
		updateEventiDto.setArchInputStructDto(serviceInputDto);
		serviceInputDto.setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		updateEventiDto.setUlIdEvent(deleteContactDto.getIdEvent());
		updateEventiDto.setSzCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
		updateEventDao.updateEvent(updateEventiDto);
	}

	/**
	 * 
	 * Method Name: deleteTodo Method Description:DELETEs from the Todo table
	 * (ignores Time stamp).
	 * 
	 * @param deleteContactDto
	 */
	private void deleteTodo(ContactAUDDto deleteContactDto) {
		DeleteTodoDto deleteTodoDto = new DeleteTodoDto();
		deleteTodoDto.setArchInputStructDto(deleteContactDto.getServiceInputDto());
		deleteTodoDto.setUlIdEvent(deleteContactDto.getSynchronizationServiceDto().getIdEvent());
		todoDeleteDao.deleteTodo(deleteTodoDto);
	}

	/**
	 * 
	 * Method Name: deleteApprovalEventLink Method Description:AUD For
	 * APPROVAL_EVENT_LINK table. (ignores TS ).
	 * 
	 * @param deleteContactDto
	 */
	private void deleteApprovalEventLink(ContactAUDDto deleteContactDto) {
		ApproverEventDto approverEventDto = new ApproverEventDto();
		approverEventDto.setArchInputStructDto(deleteContactDto.getServiceInputDto());
		approverEventDto.setUlIdEvent(deleteContactDto.getSynchronizationServiceDto().getIdEvent());

		adApprovalEventDao.adApprovalEventLink(approverEventDto);
	}

	/**
	 * 
	 * Method Name: searchContacts Method Description:Searches and Retrieves a
	 * page of contacts matching the input search criteria from the Contact
	 * window. This call specifically looks if any other Face To Face contacts
	 * exist for the given stage.
	 * 
	 * @param deleteContactDto
	 * @return FindContactDto
	 */
	private FindContactDto searchContacts(ContactAUDDto deleteContactDto) {
		ContactSearchDto contactSearchDto = new ContactSearchDto();
		FindContactDto findContactDto = new FindContactDto();

		contactSearchDto.setSzCdContactType(ServiceConstants.SVC_CD_CONTACT_TYPE_MONTH_STAT);
		contactSearchDto.setSzCdEventStatus(ServiceConstants.CEVTSTAT_NEW);
		contactSearchDto.setSzCdContactLocation(ServiceConstants.EMPTY_STRING);
		contactSearchDto.setSzCdContactOthers(ServiceConstants.EMPTY_STRING);
		contactSearchDto.setSzCdContactPurpose(ServiceConstants.EMPTY_STRING);
		contactSearchDto.setUlIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage());
		contactSearchDto.setDtScrSearchDateFrom(null);
		contactSearchDto.setDtScrSearchDateTo(null);
		ServiceInputDto serviceInputDto = new ServiceInputDto();
		serviceInputDto.setUsPageNbr(ServiceConstants.INITIAL_PAGE);
		serviceInputDto.setUlPageSizeNbr(ServiceConstants._CSYS04DO__ROWCSYS04DO_SIZE);
		serviceInputDto.setSzUserId(deleteContactDto.getServiceInputDto().getSzUserId());
		contactSearchDto.setArchInputStruct(serviceInputDto);
		contactSearchDao.searchContacts(contactSearchDto);
		return findContactDto;
	}

	/**
	 * 
	 * Method Name: deleteEventPersonLink Method Description: This DAM deletes
	 * all rows from the EVENT_PERSON_LINK for a given ID_EVENT
	 * 
	 * @param deleteContactDto
	 */
	private void deleteEventPersonLink(ContactAUDDto deleteContactDto) {
		EventPersonLinkDeleteDto eventPersonLinkDeleteDto = new EventPersonLinkDeleteDto();
		eventPersonLinkDeleteDto.setArchInputStructDto((ServiceInputDto) deleteContactDto.getServiceInputDto());
		eventPersonLinkDeleteDto.setUlIdEvent(deleteContactDto.getSynchronizationServiceDto().getIdEvent().intValue());
		eventPersonLinkDeleteDto.getArchInputStructDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
		deleteEventPersonLinkDao.deleteEventPersonLink(eventPersonLinkDeleteDto);
	}

	/**
	 * 
	 * Method Name: callPostEvent Method Description:Calls the common
	 * PostEvent() function. PostEvent() calls DAMS that AUD the EVENT table and
	 * it's children.
	 * 
	 * @param serviceInputDto
	 * @param synchronizationServiceDto
	 * @return Integer
	 */
	private Long postEvent(ServiceInputDto serviceInputDto, SynchronizationServiceDto synchronizationServiceDto) {
		PostDto postDto = new PostDto();
		postDto.setArchInputStructDto(serviceInputDto);
		postDto.setRowCcmn01UiG00(synchronizationServiceDto);
		PostEventReq postEventReq = new PostEventReq();
		PostEventPersonDto postEventPersonDto = new PostEventPersonDto();
		postEventPersonDto.setIdPerson(Long.valueOf(postDto.getArchInputStructDto().getSzUserId()));
		List<PostEventPersonDto> postEventPersonDtos = new ArrayList<>();
		postEventPersonDtos.add(postEventPersonDto);
		postEventReq.setPostEventPersonList(postEventPersonDtos);
		postEventReq.setSzCdTask(postDto.getRowCcmn01UiG00().getCdTask());
		postEventReq.setTsLastUpdate(postDto.getRowCcmn01UiG00().getDtLastUpdate());
		postEventReq.setSzCdEventStatus(postDto.getRowCcmn01UiG00().getCdEventStatus());
		postEventReq.setSzCdEventType(postDto.getRowCcmn01UiG00().getCdEventType());
		postEventReq.setDtDtEventOccurred(postDto.getRowCcmn01UiG00().getDtEventOccurred());
		postEventReq.setUlIdEvent(postDto.getRowCcmn01UiG00().getIdEvent());
		postEventReq.setUlIdStage(postDto.getRowCcmn01UiG00().getIdStage());
		postEventReq.setUlIdPerson(postDto.getRowCcmn01UiG00().getIdPerson());
		postEventReq.setSzTxtEventDescr(postDto.getRowCcmn01UiG00().getEventDescr());
		postEventReq.setReqFuncCd(serviceInputDto.getCreqFuncCd());
		PostEventRes postEventRes = eventService.postEvent(postEventReq);
		return postEventRes.getUlIdEvent();

	}

	/**
	 * 
	 * Method Name: deleteEventPersonLinkRecord Method Description:Calls the DAM
	 * to insert EVENT_PERSON_LINK table.
	 * 
	 * @param deleteContactDto
	 */
	private void deleteEventPersonLinkRecord(ContactAUDDto deleteContactDto) {
		
		EventLinkInDto eventLinkInDto = new EventLinkInDto();
		ServiceInputDto serviceInputDto = new ServiceInputDto();
		eventLinkInDto.setArchInputStructDto(serviceInputDto);
		eventLinkInDto.setArchInputStruct(serviceInputDto);
		eventLinkInDto.setIdEvent(deleteContactDto.getIdEvent());

		EventPersonLinkInsertArrayDto eventPersonLinkInsertArrayDto = deleteContactDto
				.getEventPersonLinkInsertArrayDto();
		List<EventPersonLinkDto> eventPersonLinkDtoList = null;
		// Defect 10923 - Added condition for GKNS.
		if (!ObjectUtils.isEmpty(deleteContactDto.getIdEvent()) 
				&& (ServiceConstants.CCNTCTYP_GKIN.equalsIgnoreCase(deleteContactDto.getCdContactType())
				|| ServiceConstants.CCNTCTYP_GKNS.equalsIgnoreCase(deleteContactDto.getCdContactType()))){
			eventPersonLinkDtoList = eventPersonLinkDao.getEventPersonLinkForIdEvent(deleteContactDto.getIdEvent());
		}

		List<EventPersonLinkInsertDto> eventPersonLinkInsertDtoList = new ArrayList<EventPersonLinkInsertDto>();
		if (!ObjectUtils.isEmpty(eventPersonLinkInsertArrayDto)
				&& !CollectionUtils.isEmpty(eventPersonLinkInsertArrayDto.getEventPersonLinkInsertDtoList())) {
			eventPersonLinkInsertDtoList = eventPersonLinkInsertArrayDto.getEventPersonLinkInsertDtoList();
		}
		int eventPersonLinkInsertDtoListSize = eventPersonLinkInsertDtoList.size();
		int count = ServiceConstants.Zero;
		while (eventPersonLinkInsertDtoListSize > ServiceConstants.Zero) {
			// Defect 10923
			Long idPerson = Long.valueOf(eventPersonLinkInsertDtoList.get(count).getIdPerson());
			
			if (!ObjectUtils.isEmpty(eventPersonLinkDtoList)){
				eventPersonLinkDtoList.removeIf(dto-> dto.getIdPerson().equals(idPerson));
			}

			eventLinkInDto.setIdPerson(eventPersonLinkInsertDtoList.get(count).getIdPerson());
			eventLinkInDto.setNotices(eventPersonLinkInsertDtoList.get(count).getIndLanguage());
			eventLinkInDto.setDistributionMethod(eventPersonLinkInsertDtoList.get(count).getIndDstrbutnMthd());
			eventLinkInDto.setIndPersRmvlNotified(eventPersonLinkInsertDtoList.get(count).getIndPersRmvlNotified());
			eventLinkInDto.setIndKinNotifChild(eventPersonLinkInsertDtoList.get(count).getIndKinNotifChild());
			eventLinkInDto.getArchInputStructDto()
					.setCreqFuncCd(eventPersonLinkInsertDtoList.get(count).getCdScrDataAction());
			eventLinkInDto.getArchInputStruct()
					.setCreqFuncCd(eventPersonLinkInsertDtoList.get(count).getCdScrDataAction());
			eventLinkInDto.setDtLastUpdate(eventPersonLinkInsertDtoList.get(count).getDtLastUpdate());
			eventPersonLinkInsertDtoListSize--;
			count++;
			eventPersonLinkProcessDao.ccmn68dAUDdam(eventLinkInDto);
		}
		
		if(!ObjectUtils.isEmpty(eventPersonLinkDtoList) 
				&& (ServiceConstants.CCNTCTYP_GKIN.equalsIgnoreCase(deleteContactDto.getCdContactType())
						|| ServiceConstants.CCNTCTYP_GKNS.equalsIgnoreCase(deleteContactDto.getCdContactType()))){
			for (EventPersonLinkDto eventPersonLinkDto :  eventPersonLinkDtoList){
				eventLinkInDto.setIdPerson(eventPersonLinkDto.getIdPerson());
				eventLinkInDto.setDtLastUpdate(eventPersonLinkDto.getTsLastUpdate());
				eventLinkInDto.getArchInputStructDto()
						.setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
				eventLinkInDto.getArchInputStruct()
						.setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
				eventPersonLinkProcessDao.ccmn68dAUDdam(eventLinkInDto);
			}
		}
	}

	/**
	 * 
	 * Method Name: CallCSESC2D Method Description: Queries records_check and
	 * criminal_history for null CD_CRIM_HIST_ACTION
	 * 
	 * @param deleteContactDto
	 * @return CriminalRecordsDoDto
	 */
	private CriminalRecordsDoDto CallCSESC2D(ContactAUDDto deleteContactDto) {

		CriminalRecordsDto criminalRecordsDto = new CriminalRecordsDto();
		CriminalRecordsDoDto criminalRecordsDoDto = new CriminalRecordsDoDto();
		criminalRecordsDto.setServiceInputDto((ServiceInputDto) deleteContactDto.getServiceInputDto());
		criminalRecordsDto.setUlIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage());
		List<CriminalRecordsDoDto> criminalRecordsList = criminalRecordsDao.getCriminalCheckRecords(criminalRecordsDto);
		if (!CollectionUtils.isEmpty(criminalRecordsList))
			criminalRecordsDoDto = criminalRecordsList.get(0);

		return criminalRecordsDoDto;

	}

	/**
	 * 
	 * Method Name: callDeleteContactNarrative Method Description: delete the
	 * contact narrative for a given ID_EVENT
	 * 
	 * @param idEvent
	 */
	private void callDeleteContactNarrative(Long idEvent) {

		contactNarrativeDao.deleteContactNarrative(idEvent.intValue());

	}

	/**
	 * 
	 * Method Name: callMPSStatsHelper Method Description:This method will
	 * record info into the MPS_USAGE_STATS table for statistical analysis
	 * purpose.
	 * 
	 * @param deleteContactDto
	 */
	private void callMPSStatsHelper(ContactAUDDto deleteContactDto) {
		MPSStatsValueDto mpsStatsDto = new MPSStatsValueDto();
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(deleteContactDto.getServiceInputDto().getCreqFuncCd())) {
			mpsStatsDto.setCdDmlType(ServiceConstants.REQ_FUNC_CD_ADD);
			mpsStatsDto.setIdEvent(deleteContactDto.getIdEvent());

			mpsStatsDto.setIdReference(deleteContactDto.getIdEvent());
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(deleteContactDto.getServiceInputDto().getCreqFuncCd())) {
			mpsStatsDto.setCdDmlType(ServiceConstants.REQ_FUNC_CD_UPDATE);
			mpsStatsDto.setIdEvent(deleteContactDto.getIdEvent());

			mpsStatsDto.setIdReference(deleteContactDto.getIdEvent());
		} else {
			mpsStatsDto.setCdDmlType(ServiceConstants.REQ_FUNC_CD_DELETE);
			mpsStatsDto.setIdEvent(!TypeConvUtil.isNullOrEmpty(deleteContactDto.getSynchronizationServiceDto())
					? deleteContactDto.getSynchronizationServiceDto().getIdEvent()
					: Long.valueOf(ServiceConstants.Zero));

			mpsStatsDto.setIdReference(!TypeConvUtil.isNullOrEmpty(deleteContactDto.getSynchronizationServiceDto())
					? deleteContactDto.getSynchronizationServiceDto().getIdEvent()
					: Long.valueOf(ServiceConstants.Zero));
		}
		mpsStatsDto.setIdCase(deleteContactDto.getIdCase());
		mpsStatsDto.setIdStage(!TypeConvUtil.isNullOrEmpty(deleteContactDto.getSynchronizationServiceDto())
				? deleteContactDto.getSynchronizationServiceDto().getIdStage() : Long.valueOf(ServiceConstants.Zero));
		mpsStatsDto.setCdReference(ServiceConstants.CMPSSTAT_012);

		mPSStatsDao.logStatsToDB(mpsStatsDto);
	}

	/**
	 * 
	 * Method Name: updateTODOEvent Method Description:Calls the DAM to update
	 * status on the Todo table.
	 * 
	 * @param deleteContactDto
	 */
	private void updateTODOEvent(ContactAUDDto deleteContactDto) {
		UpdateToDoDto updateToDoDto = new UpdateToDoDto();
		updateToDoDto.setIdEvent(deleteContactDto.getSynchronizationServiceDto().getIdEvent());

		updateToDoDao.updateTODOEvent(updateToDoDto);

	}

	/**
	 * 
	 * Method Name: createNEWContacts Method Description:Creates a NEW Event, a
	 * Contact shell, a To-Do and conditionally creates a second To-Do ( for a
	 * Supervisor. )
	 * 
	 * @param deleteContactDto1
	 * @param faceToFaceExists
	 * @param cMonthlyStatusType
	 */
	private void createNEWContacts(ContactAUDDto deleteContactDto1, boolean faceToFaceExists, int cMonthlyStatusType) {
		ContactAUDDto deleteContactDto = (ContactAUDDto) deleteContactDto1;
		deleteContactDto.setCdContactLocation(ServiceConstants.EMPTY_STRING);
		deleteContactDto.setCdContactMethod(!StringUtils.isEmpty(deleteContactDto.getCdContactMethod())
				? deleteContactDto.getCdContactMethod() : ServiceConstants.EMPTY_STRING);
		deleteContactDto.setCdContactOthers(!StringUtils.isEmpty(deleteContactDto.getCdContactOthers())
				? deleteContactDto.getCdContactOthers() : ServiceConstants.EMPTY_STRING);
		deleteContactDto.setCdContactPurpose(!StringUtils.isEmpty(deleteContactDto.getCdContactPurpose())
				? deleteContactDto.getCdContactPurpose() : ServiceConstants.EMPTY_STRING);
		deleteContactDto.setTmScrTmCntct(ServiceConstants.EMPTY_STRING);
		deleteContactDto.setDtMonthlySummBegin(ServiceConstants.NULL_CASTOR_DATE_DATE);
		deleteContactDto.setDtMonthlySummEnd(ServiceConstants.NULL_CASTOR_DATE_DATE);
		deleteContactDto.setIndContactAttempted(ServiceConstants.N);
		deleteContactDto.setDtLastUpdate2(ServiceConstants.NULL_CASTOR_DATE_DATE);

		if (cMonthlyStatusType != ServiceConstants.Zero) {
			deleteContactDto.setCdContactType(ServiceConstants.SVC_CD_CONTACT_TYPE_MONTH_STAT);
			deleteContactDto.getSynchronizationServiceDto()
					.setEventDescr(ServiceConstants.TXT_EVENT_DESC_MONTHLY_STATUS);
		}

		deleteContactDto.getSynchronizationServiceDto().setIdEvent(ServiceConstants.ZERO_VAL);
		if (!ServiceConstants.ANOT.equals(deleteContactDto.getCdContactType())
				&& !ServiceConstants.LNOT.equals(deleteContactDto.getCdContactType())) {
			deleteContactDto.setIdEvent(ServiceConstants.ZERO_VAL);
		}
		deleteContactDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		deleteContactDto.getSynchronizationServiceDto().setCdEventStatus(ServiceConstants.CEVTSTAT_NEW);
		deleteContactDto.getSynchronizationServiceDto()
				.setDomicileDeprivationChildToEventDto(new DomicileDeprivationChildToEventDto());
		if (!ServiceConstants.ANOT.equals(deleteContactDto.getCdContactType())
				&& !ServiceConstants.LNOT.equals(deleteContactDto.getCdContactType())) {
			deleteContactDto.setIdEvent(
					postEvent(deleteContactDto.getServiceInputDto(), deleteContactDto.getSynchronizationServiceDto()));

			// Create the To-Do.

			callTodoCommonFunction(deleteContactDto, faceToFaceExists);
		}
		if (ServiceConstants.SVC_CD_CONTACT_45_DAY.equals(deleteContactDto1.getCdContactType())
				|| ServiceConstants.SVC_CD_CONTACT_60_DAY.equals(deleteContactDto1.getCdContactType())) {

			Long ulIdPersonSupervisor = (long) callCCMN60D(deleteContactDto);
			if (ulIdPersonSupervisor != ServiceConstants.ZERO_VAL) {
				deleteContactDto.setIdPerson(ulIdPersonSupervisor);

				callTodoCommonFunction(deleteContactDto, faceToFaceExists);
			}

		}
		if (!ServiceConstants.ANOT.equals(deleteContactDto.getCdContactType())
				&& !ServiceConstants.LNOT.equals(deleteContactDto.getCdContactType())) {
			deleteContactDto.setIdPerson(ServiceConstants.ZERO_VAL);
		}
		deleteContactDto.setDtContactOccurred(new Date());
		audContact(deleteContactDto);
	}

	/**
	 * 
	 * Method Name: callTodoCommonFunction Method Description:Call the common
	 * function that creates to-dos
	 * 
	 * @param deleteContactDto
	 * @param faceToFaceExists
	 */
	private void callTodoCommonFunction(ContactAUDDto deleteContactDto, boolean faceToFaceExists) {
		Date dtTempDate;
		if (!faceToFaceExists) {
			dtTempDate = deleteContactDto.getDtContactOccurred();
		} else {
			dtTempDate = new Date();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtTempDate);
		cal.add(Calendar.DAY_OF_MONTH, ServiceConstants.TODO_DAY_DATE);
		dtTempDate = new Date();
		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
		MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
		if (deleteContactDto.getSynchronizationServiceDto().getCdTask()
				.equals(ServiceConstants.SVC_CD_TASK_CONTACT_SUB)) {
			mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_SUB_MONTHLY_SUMM);
		} else if (deleteContactDto.getSynchronizationServiceDto().getCdTask()
				.equals(ServiceConstants.SVC_CD_TASK_CONTACT_FSC)) {
			mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_FSU_MONTHLY_SUMM);
		} else if (deleteContactDto.getSynchronizationServiceDto().getCdTask()
				.equals(ServiceConstants.SVC_CD_TASK_CONTACT_FMR)) {
			mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_FRE_MONTHLY_SUMM);
		} else if (deleteContactDto.getSynchronizationServiceDto().getCdTask()
				.equals(ServiceConstants.SVC_CD_TASK_CONTACT_FPR)) {
			mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_FPR_MONTHLY_SUMM);
		} else if (deleteContactDto.getSynchronizationServiceDto().getCdTask()
				.equals(ServiceConstants.SVC_CD_TASK_CONTACT_ADO)) {
			mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_ADO_MONTHLY_SUMM);
		} else if (deleteContactDto.getSynchronizationServiceDto().getCdTask()
				.equals(ServiceConstants.SVC_CD_TASK_CONTACT_PAD)) {
			mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_PAD_MONTHLY_SUMM);
		} else if (deleteContactDto.getSynchronizationServiceDto().getCdTask()
				.equals(ServiceConstants.SVC_CD_TASK_CONTACT_APS_INV)) {
			if (ServiceConstants.SVC_CD_CONTACT_TYPE_MONTH_STAT.equals(deleteContactDto.getCdContactType())) {
				mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_INV_MONTHLY_STAT);
			} else if (ServiceConstants.SVC_CD_CONTACT_45_DAY.equals(deleteContactDto.getCdContactType())) {

				mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_INV_45_DAY);
			}

			else if (ServiceConstants.SVC_CD_CONTACT_60_DAY.equals(deleteContactDto.getCdContactType())) {
				mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_INV_60_DAY);
			}
		} else if (deleteContactDto.getSynchronizationServiceDto().getCdTask()
				.equals(ServiceConstants.SVC_CD_TASK_CONTACT_AOC)) {
			if (ServiceConstants.SVC_CD_CONTACT_TYPE_MONTH_STAT.equals(deleteContactDto.getCdContactType())) {
				mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_AOC_MONTHLY_STAT);
			}
		} else if (ServiceConstants.SVC_CD_TASK_CONTACT_APS
				.equals(deleteContactDto.getSynchronizationServiceDto().getCdTask())) {
			if (ServiceConstants.SVC_CD_CONTACT_TYPE_MONTH_STAT.equals(deleteContactDto.getCdContactType())) {
				mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_SVC_MONTHLY_STAT);
			} else if (ServiceConstants.SVC_CD_CONTACT_60_DAY.equals(deleteContactDto.getCdStageType())) {
				mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_INV_60_DAY);
			}
		} else if (ServiceConstants.SVC_CD_TASK_CONTACT_SRR_APS
				.equals(deleteContactDto.getSynchronizationServiceDto().getCdTask())) {
			mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_INV_SRR);
		}
		mergeSplitToDoDto.setDtTodoCfDueFrom(dtTempDate);
		mergeSplitToDoDto.setIdTodoCfStage(deleteContactDto.getSynchronizationServiceDto().getIdStage());
		mergeSplitToDoDto.setIdTodoCfEvent(deleteContactDto.getIdEvent());
		mergeSplitToDoDto.setIdTodoCfPersCrea(ServiceConstants.ZERO_VAL);
		mergeSplitToDoDto.setIdTodoCfPersWkr(deleteContactDto.getIdPerson());
		mergeSplitToDoDto.setIdTodoCfPersAssgn(deleteContactDto.getIdPerson());
		todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);
		todoCreateService.TodoCommonFunction(todoCreateInDto);

	}

	/**
	 * 
	 * Method Name: callCCMN60D Method Description:Retrieves an employee's
	 * supervisor name and id.
	 * 
	 * @param deleteContactDto
	 * @return int
	 */
	private int callCCMN60D(ContactAUDDto deleteContactDto) {
		PersonDiDto personDiDto = new PersonDiDto();
		personDiDto.setIdPerson(deleteContactDto.getIdPerson());
		return personInfoDao.getPersonName(personDiDto).get(ServiceConstants.Zero).getIdPerson().intValue();

	}

	/**
	 * 
	 * Method Name: audContact Method Description:This DAM saves CONTACT data.
	 * 
	 * @param contactAUDDto
	 */
	private void audContact(ContactAUDDto contactAUDDto) {
		contactProcessDao.audContact(buildContactDetailSaveDiDto(contactAUDDto));
	}

	private void updateFollowupDetails(ContactAUDDto contactAUDDto) {
		inrSafetyDao.updateFollowupDetails(buildContactDetailSaveDiDto(contactAUDDto));
	}

	private ContactDetailSaveDiDto buildContactDetailSaveDiDto(ContactAUDDto contactAUDDto) {
		ContactDetailSaveDiDto contactDetailSaveDiDto = new ContactDetailSaveDiDto();
		contactDetailSaveDiDto.setIdEvent(contactAUDDto.getIdEvent());
		contactDetailSaveDiDto.setIdStage(contactAUDDto.getSynchronizationServiceDto().getIdStage());
		contactDetailSaveDiDto.setIdPerson(contactAUDDto.getIdPerson());
		contactDetailSaveDiDto.setDtMonthlySummBegin(contactAUDDto.getDtMonthlySummBegin());
		contactDetailSaveDiDto.setDtMonthlySummEnd(contactAUDDto.getDtMonthlySummEnd());
		contactDetailSaveDiDto.setIndContactAttempted(contactAUDDto.getIndContactAttempted());
		contactDetailSaveDiDto.setDtContactOccurred(contactAUDDto.getDtContactOccurred());
		contactDetailSaveDiDto.setCdContactLocation(contactAUDDto.getCdContactLocation());
		contactDetailSaveDiDto.setCdContactMethod(contactAUDDto.getCdContactMethod());
		contactDetailSaveDiDto.setCdContactOthers(contactAUDDto.getCdContactOthers());
		contactDetailSaveDiDto.setCdContactPurpose(contactAUDDto.getCdContactPurpose());
		contactDetailSaveDiDto.setCdContactType(contactAUDDto.getCdContactType());
		contactDetailSaveDiDto.setTxtClosureDesc(contactAUDDto.getTxtClosureDesc());
		if (!TypeConvUtil.isNullOrEmpty(contactAUDDto.getTmScrTmCntct())) {
			contactDetailSaveDiDto.setTmScrTmCntct(contactAUDDto.getTmScrTmCntct());
		}
		contactDetailSaveDiDto.setCdReasonScreenOut(contactAUDDto.getCdReasonScreenOut());
		contactDetailSaveDiDto.setIndKinRecmd(contactAUDDto.getIndKinRecmd());
		contactDetailSaveDiDto.setNmKnCgvr(contactAUDDto.getNmKnCgvr());
		contactDetailSaveDiDto.setCdRsnNotNeed(contactAUDDto.getCdRsnNotNeed());
		contactDetailSaveDiDto.setAmtNeeded(contactAUDDto.getAmtNeeded());
		contactDetailSaveDiDto.setIndSiblingVisit(contactAUDDto.getIndSiblingVisit());
		contactDetailSaveDiDto.setDtLastUpdate(contactAUDDto.getDtLastUpdate());
		contactDetailSaveDiDto.setDtLastUpdate2(contactAUDDto.getDtLastUpdate2());
		contactDetailSaveDiDto.setCdChildSafety(contactAUDDto.getCdChildSafety());
		contactDetailSaveDiDto.setCdPendLegalAction(contactAUDDto.getCdPendLegalAction());
		contactDetailSaveDiDto.setIndPrinInterview(contactAUDDto.getIndPrinInterview());
		contactDetailSaveDiDto.setCdProfCollateral(contactAUDDto.getCdProfCollateral());
		contactDetailSaveDiDto.setCdAdministrative(contactAUDDto.getCdAdministrative());
		contactDetailSaveDiDto.setComments(contactAUDDto.getComments());
		contactDetailSaveDiDto.setIndAnnounced(contactAUDDto.getIndAnnounced());
		contactDetailSaveDiDto.setIndFamPlnCompleted(contactAUDDto.getIndFamPlnCompleted());
		contactDetailSaveDiDto.setIndSafPlnCompleted(contactAUDDto.getIndSafPlnCompleted());
		contactDetailSaveDiDto.setIndSafConResolved(contactAUDDto.getIndSafConResolved());
		contactDetailSaveDiDto.setIndCourtOrdrSvcs(contactAUDDto.getIndCourtOrdrSvcs());
		if (!ObjectUtils.isEmpty(contactAUDDto.getNbrHours())) {
			contactDetailSaveDiDto.setNbrHours(contactAUDDto.getNbrHours().shortValue());
		}
		if (!ObjectUtils.isEmpty(contactAUDDto.getNbrMins())) {
			contactDetailSaveDiDto.setNbrMins(contactAUDDto.getNbrMins().shortValue());
		}
		/* artf128844 - Changes for FCL: ORDER #9 - START */
		if (!ObjectUtils.isEmpty(contactAUDDto.getCdFtfExceptionRsn())) {
			contactDetailSaveDiDto.setCdFtfExceptionRsn(contactAUDDto.getCdFtfExceptionRsn());
		}
		/* artf128844 - Changes for FCL: ORDER #9 - END */
		if (!ObjectUtils.isEmpty(contactAUDDto.getExtReqReason())) {
			contactDetailSaveDiDto.setCdReqextReason(contactAUDDto.getExtReqReason());
		}
		// CANIRSP-8 I&R Staffing
		contactDetailSaveDiDto.setCreatedOn(contactAUDDto.getCreatedOn());
		contactDetailSaveDiDto.setDtNotification(contactAUDDto.getDtNotification());
		contactDetailSaveDiDto.setTxtSummDiscuss(contactAUDDto.getTxtSummDiscuss());
		contactDetailSaveDiDto.setTxtIdentfdSafetyConc(contactAUDDto.getTxtIdentfdSafetyConc());
		if (contactAUDDto.getIdCaseworker() != null) {
			contactDetailSaveDiDto.setIdCaseworker(contactAUDDto.getIdCaseworker());
			EmployeeDetailDto rawEmployeeData = employeeDao.getEmployeeById(contactAUDDto.getIdCaseworker());
			contactDetailSaveDiDto.setCdJobCaseworker(rawEmployeeData.getCdEmployeeClass());
		}
		if (contactAUDDto.getIdSupervisor() != null) {
			contactDetailSaveDiDto.setIdSupervisor(contactAUDDto.getIdSupervisor());
			EmployeeDetailDto rawEmployeeData = employeeDao.getEmployeeById(contactAUDDto.getIdSupervisor());
			contactDetailSaveDiDto.setCdJobSupervisor(rawEmployeeData.getCdEmployeeClass());
		}
		if (contactAUDDto.getIdDirector() != null) {
			contactDetailSaveDiDto.setIdDirector(contactAUDDto.getIdDirector());
			EmployeeDetailDto rawEmployeeData = employeeDao.getEmployeeById(contactAUDDto.getIdDirector());
			contactDetailSaveDiDto.setCdJobDirector(rawEmployeeData.getCdEmployeeClass());
		}
		ServiceInputDto serviceInputDto = new ServiceInputDto();
		serviceInputDto.setCreqFuncCd(contactAUDDto.getServiceInputDto().getCreqFuncCd());
		serviceInputDto.setSzUserId(contactAUDDto.getServiceInputDto().getSzUserId());
		contactDetailSaveDiDto.setServiceInputDto(serviceInputDto);
		contactDetailSaveDiDto.setInrSafetyFieldDtoList(contactAUDDto.getInrSafetyFieldDtoList());
		contactDetailSaveDiDto.setGroupNum(contactAUDDto.getInrGroupId());
		return contactDetailSaveDiDto;
	}

	/**
	 * 
	 * Method Name: searchContactWindow Method Description:Searches and
	 * Retrieves a page of contacts matching the input search criteria from the
	 * Contact window.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void searchContactWindow(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {

		ContactSearchDto contactSearchDto = new ContactSearchDto();
		contactSearchDto.setSzCdContactType(deleteContactDto.getCdContactType());
		contactSearchDto.setSzCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
		contactSearchDto.setUlIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage());
		contactSearchDto.setDtScrSearchDateFrom(deleteContactDto.getDtMonthlySummBegin());
		contactSearchDto.setDtScrSearchDateTo(deleteContactDto.getDtMonthlySummEnd());

		ServiceInputDto serviceInputDto = new ServiceInputDto();
		serviceInputDto.setUlPageSizeNbr(ServiceConstants._CSYS04DO__ROWCSYS04DO_SIZE);
		serviceInputDto.setSzUserId(deleteContactDto.getServiceInputDto().getSzUserId());
		contactSearchDto.setArchInputStruct(serviceInputDto);
		ServiceOutputDto serviceOutputDto = new ServiceOutputDto();
		serviceOutputDto.setBmoreDataInd(ServiceConstants.ONE);
		serviceOutputDto.setRowQtySize(ServiceConstants.Zero);
		EventIdArrayDto eventIdArrayDto = childContactDto.getEventIdArrayDto();
		FindContactDto findContactDto = new FindContactDto();

		int j = ServiceConstants.INITIAL_PAGE;
		while ("true".equalsIgnoreCase(serviceOutputDto.getBmoreDataInd()) && getEventIdStructCount(
				eventIdArrayDto.getEventIdStructList()) < ServiceConstants._CSYS07SO__EVENTIDSTRUCT_SIZE) {
			serviceInputDto.setUsPageNbr(j++);
			findContactDto = contactSearchDao.searchContacts(contactSearchDto);
		}

		int eventIdStructCount = ServiceConstants.Zero;

		if (eventIdArrayDto != null) {
			eventIdStructCount = getEventIdStructCount(eventIdArrayDto.getEventIdStructList());
		}
		if (eventIdStructCount > ServiceConstants.MAX_REG_SUBMITTED_CONTACTS) {
			throw new ServiceLayerException(ServiceConstants.TOO_MANY_CONTACTS_IN_MONTHLY);
		}
		serviceOutputDto.setRowQtySize(eventIdStructCount);
		serviceOutputDto.setBmoreDataInd(!ObjectUtils.isEmpty(findContactDto.getServiceOutputDto())
				? findContactDto.getServiceOutputDto().getBmoreDataInd() : ServiceConstants.N);
	}

	/**
	 * Method Name: getEventIdStructCount Method Description:getting size
	 * 
	 * @return int
	 */
	private int getEventIdStructCount(ArrayList arrayList) {

		return arrayList.size();
	}

	/**
	 * 
	 * Method Name: getAllegationFacilAllegPerson Method Description:This DAM
	 * performs a full row retrieval from the allegation, facility_allegation
	 * and person tables given an id_stage.
	 * 
	 * @param deleteContactDto
	 */
	private void getAllegationFacilAllegPerson(ContactAUDDto deleteContactDto) {

		AllegationFacilAllegPersonDto allegationFacilAllegPersonDto = new AllegationFacilAllegPersonDto();
		allegationFacilAllegPersonDto.setArchInputStruct((ServiceInputDto) deleteContactDto.getServiceInputDto());
		allegationFacilAllegPersonDto.getArchInputStruct().setUsPageNbr(1);
		allegationFacilAllegPersonDto.getArchInputStruct()
				.setUlPageSizeNbr(ServiceConstants._CLSC16DO__ROWCLSC16DO_SIZE);
		allegationFacilAllegPersonDto
				.setUlIdAllegationStage(deleteContactDto.getSynchronizationServiceDto().getIdStage().intValue());
		FacilAllegPersonDto facilAllegPersonDto = allegFacilDao
				.getAllegationFacilAllegPerson(allegationFacilAllegPersonDto);

		List<AllegationStageVictimDto> allegationStageVictimDtoList = facilAllegPersonDto.getAllegFacilPersonDto()
				.getAllegationStageVictimDtoList();

		for (AllegationStageVictimDto allegationStageVictimDto : allegationStageVictimDtoList) {

			adFacilityAllegationPriorReview(deleteContactDto, allegationStageVictimDto);
		}
	}

	/**
	 * 
	 * Method Name: adFacilityAllegationPriorReview Method Description:This DAM
	 * insert information from the orginal facility investigation into the
	 * FACIL_ALLEG_PRIOR_REVIEW table before a request for review contact is
	 * recorded.
	 * 
	 * @param deleteContactDto
	 * @param allegationStageVictimDto
	 */
	private void adFacilityAllegationPriorReview(ContactAUDDto deleteContactDto,
			AllegationStageVictimDto allegationStageVictimDto) {
		FacilityAllegationPriorDto facilityAllegationPriorDto = new FacilityAllegationPriorDto();
		ServiceInputDto serviceInputDto = deleteContactDto.getServiceInputDto();
		serviceInputDto.setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		facilityAllegationPriorDto.setArchInputStruct(serviceInputDto);
		facilityAllegationPriorDto.setUlIdAllegation(allegationStageVictimDto.getIdAllegation());
		facilityAllegationPriorDto.setUlIdReviewStage(allegationStageVictimDto.getIdAllegationStage());
		facilityAllegationPriorDto.setUlIdReviewVictim(allegationStageVictimDto.getIdVictim());
		facilityAllegationPriorDto.setUlIdReviewAllegedPerp(allegationStageVictimDto.getIdAllegedPerpetrator());
		facilityAllegationPriorDto.setCdReviewAllegDisp(allegationStageVictimDto.getCdAllegDisposition());
		facilityAllegationPriorDto.setCdReviewAllegType(allegationStageVictimDto.getCdAllegType());
		facilityAllegationPriorDto.setCdReviewAllegDispSupr(allegationStageVictimDto.getCdFacilAllegDispSupr());
		facilityAllegationPriorDto.setCdReviewAllegClss(allegationStageVictimDto.getFacilAllegInvClass());
		facilityAllegationPriorDto.setCdReviewAllegClssSupr(allegationStageVictimDto.getCdFacilAllegClssSupr());
		facilAllegPriorReviewDao.adFacilityAllegationPriorReview(facilityAllegationPriorDto);
	}

	/**
	 * 
	 * Method Name: deleteFacilityAllegationPriorReview Method Description:This
	 * DAM deletes information from the FACIL_ALLEG_PRIOR_REVIEW table when a
	 * request for review contact is deleted.
	 * 
	 * @param contactAUDDto
	 */
	private void deleteFacilityAllegationPriorReview(ContactAUDDto contactAUDDto) {
		FacilityAllegationPriorDto facilityAllegationPriorDto = new FacilityAllegationPriorDto();
		facilityAllegationPriorDto.setArchInputStruct((ServiceInputDto) contactAUDDto.getServiceInputDto());
		facilityAllegationPriorDto.setUlIdReviewStage(contactAUDDto.getSynchronizationServiceDto().getIdStage());
		facilityAllegationPriorDto.getArchInputStruct().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
		facilAllegPriorReviewDao.adFacilityAllegationPriorReview(facilityAllegationPriorDto);
	}

	/**
	 * 
	 * Method Name: updateFacilityInvestigationDetail Method Description: This
	 * DAM clears out the CD_FACIL_INVST_ORIG_DISP, CD_FACIL_INVST_ORIG_CLS_RSN,
	 * DT_FACIL_INVST_ORIG_COMPL when a Request for Review is Deleted.
	 * 
	 * @param contactAUDDto
	 */
	private void updateFacilityInvestigationDetail(ContactAUDDto contactAUDDto) {
		ContactsDto contactsDto = new ContactsDto();
		contactsDto.setArchInputStruct((ServiceInputDto) contactAUDDto.getServiceInputDto());
		contactsDto.setUlIdStage(contactAUDDto.getSynchronizationServiceDto().getIdStage());
		contactsDto.getArchInputStruct().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

		facilityInvstDtlUpdateDao.updateFacilityInvestigationDetail(contactsDto);

	}

	/**
	 * 
	 * Method Name: getEarliestContactDate Method Description:Given ID CASE,CD
	 * STAGE and CONTACT TYPE query the CONTACT table and obtain date of the
	 * earliest contact recorded.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void getEarliestContactDate(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {
		ApsInvStageDto apsInvStageDto = new ApsInvStageDto();
		apsInvStageDto.setArchInputStruct((ServiceInputDto) deleteContactDto.getServiceInputDto());
		apsInvStageDto.setUlIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage().intValue());
		apsInvStageDto.setSzCdContactType(deleteContactDto.getCdContactType());
		ContactDateDto contactDateDto = contactDateDao.getEarliestContactDate(apsInvStageDto);
		if (contactDateDto.getDtDTContactOccurred().getYear() != (short) ServiceConstants.FIND_SUCCESS) {
			childContactDto.setDtContactOccurred(contactDateDto.getDtDTContactOccurred());
			getStagesOverallDispFromApsInvDtl(deleteContactDto, childContactDto);
			// Compare the Investigation Start Date With The Date returned from
			// the Dam.If the date returned from this
			// dam ("Earliest 24H Contact date" ) is earlier than the Inv. Start
			// Date, then reset the Inv Start Dt
			// to the 24H Contact DT.

			// double diff =
			// DateHelper.minutesDifference(csys07so.getDtDtInvStart(),
			// csys07so.getDtDTContactOccurred());
			double diff = ServiceConstants.Zero;// Yet to Check and update
			if ((diff / (double) ServiceConstants.ARC_UTL_MINUTES_IN_DAY) > ServiceConstants.Zero) {
				updateAPSInvestigationDetail(deleteContactDto, childContactDto);
				updateStageFatality(deleteContactDto, childContactDto);
				updateStagePrior(deleteContactDto, childContactDto);
				updateEventDate(deleteContactDto, childContactDto);
				updateEventOccureedDate(deleteContactDto, childContactDto);
				updateIncomingDetail(deleteContactDto);
			}
		}
	}

	/**
	 * 
	 * Method Name: getStagesOverallDispFromApsInvDtl Method
	 * Description:Retrieval of Dt Inv Stage Begun from APS_INVST_DETAIL using
	 * ID_STAGE.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void getStagesOverallDispFromApsInvDtl(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {

		InvestigationStageDto investigationStageDto = new InvestigationStageDto();
		investigationStageDto.setArchInputStruct((ServiceInputDto) deleteContactDto.getServiceInputDto());
		investigationStageDto.setUlIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage().intValue());
		ApsInvDtlDto apsInvDtlDto = new ApsInvDtlDto();
		apsInvDtlDto
				.setApsInvDtlStageDto(stagesOverallDispDao.getStagesOverallDispFromApsInvDtl(investigationStageDto));
		childContactDto.setDtInvStart(apsInvDtlDto.getApsInvDtlStageDto().getDtDtApsInvstBegun());
	}

	/**
	 * 
	 * Method Name: updateAPSInvestigationDetail Method Description:Given ID
	 * STAGE this dam updates the Aps Inv Begun Date in the APS INVST DETAIL
	 * table.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void updateAPSInvestigationDetail(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {
		APSInvestigationDto apsInvestigationDto = new APSInvestigationDto();
		apsInvestigationDto.setArchInputStruct((ServiceInputDto) deleteContactDto.getServiceInputDto());
		apsInvestigationDto.setIdApsStage(deleteContactDto.getSynchronizationServiceDto().getIdStage().intValue());
		apsInvestigationDto.getArchInputStruct().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		apsInvestigationDto.setDtDtApsInvstBegun(childContactDto.getDtContactOccurred());
		apsInvstDao.updateAPSInvestigationDetail(apsInvestigationDto);
	}

	/**
	 * 
	 * Method Name: updateStageFatality Method Description:Given ID STAGE this
	 * dam updates the Date Stage Open in the Stage table.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void updateStageFatality(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {
		ContactDiDto contactDiDto = new ContactDiDto();
		contactDiDto.setServiceInputDto((ServiceInputDto) deleteContactDto.getServiceInputDto());
		contactDiDto.setUlIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage());
		contactDiDto.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		contactDiDto.setDtDtStageStart(childContactDto.getDtContactOccurred());
		stageUpdateDao.updateStage(contactDiDto);
	}

	/**
	 * 
	 * Method Name: updateStagePrior Method Description:Given ID STAGE this dam
	 * updates the Date Stage Close in the Stage table.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void updateStagePrior(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {
		StageUpdateDto stageUpdateDto = new StageUpdateDto();
		stageUpdateDto.setArchInputStructDto((ServiceInputDto) deleteContactDto.getServiceInputDto());
		stageUpdateDto.setUlIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage().intValue());
		stageUpdateDto.getArchInputStructDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		stageUpdateDto.setDtDtStageClose(childContactDto.getDtContactOccurred());
		stagePriorDao.updateStage(stageUpdateDto);
	}

	/**
	 * 
	 * Method Name: updateEventDate Method Description:This dam updates the .dt
	 * event occurred for Investigation Stage Opened event update.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void updateEventDate(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {
		EventUpdateInDto eventUpdateInDto = new EventUpdateInDto();
		eventUpdateInDto.setArchInputStruct((ServiceInputDto) deleteContactDto.getServiceInputDto());
		eventUpdateInDto.setUlIdEventStage(deleteContactDto.getSynchronizationServiceDto().getIdStage());
		eventUpdateInDto.setSzTxtEventDescr(ServiceConstants.INV_OPEN_EVENT);
		eventUpdateInDto.getArchInputStruct().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		eventUpdateInDto.setDtDtEventOccurred(childContactDto.getDtContactOccurred());
		eventUpdateDao.updateEvent(eventUpdateInDto);
	}

	/**
	 * 
	 * Method Name: updateEventOccureedDate Method Description:This dam updates
	 * the DT_EVENT_OCCURRED in the EVENT table for the Intake CLosed event.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void updateEventOccureedDate(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {
		EventUpdateStatusDto eventUpdateStatusDto = new EventUpdateStatusDto();
		eventUpdateStatusDto.setArchInputStruct((ServiceInputDto) deleteContactDto.getServiceInputDto());
		eventUpdateStatusDto.setUlIdEventStage(deleteContactDto.getSynchronizationServiceDto().getIdStage());
		eventUpdateStatusDto.setSzTxtEventDescr(ServiceConstants.INT_CLOSED_EVENT);
		eventUpdateStatusDto.getArchInputStruct().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		eventUpdateStatusDto.setDtDtEventOccurred(childContactDto.getDtContactOccurred());
		eventStageDao.updateEvent(eventUpdateStatusDto);
	}

	/**
	 * 
	 * Method Name: updateIncomingDetail Method Description:This dam updates the
	 * DT Incoming Call Disposed in Incoming Detail Table.
	 * 
	 * @param deleteContactDto
	 */
	private void updateIncomingDetail(ContactAUDDto deleteContactDto) {
		ContactIncomingDetailsDto contactIncomingDetailsDto = new ContactIncomingDetailsDto();
		contactIncomingDetailsDto.setArchInputStruct(deleteContactDto.getServiceInputDto());
		contactIncomingDetailsDto.setIdCase(deleteContactDto.getIdCase());
		contactIncomingDetailsDto.setSzCdStage(ServiceConstants.CSTAGES_INT);
		incomingDetailUpdateDao.updateIncomingDetail(contactIncomingDetailsDto);
	}

	/**
	 * 
	 * Method Name: getIdPersonForDifferentRole Method Description:Retrieve DAM
	 * used to get a row from stage person link based on stage, case, role and
	 * cdstage.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void getIdPersonForDifferentRole(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {
		PersonRoleDto personRoleDto = new PersonRoleDto();
		personRoleDto.setArchInputStructDto(deleteContactDto.getServiceInputDto());
		personRoleDto.setIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage());
		personRoleDto.setIdCase(deleteContactDto.getIdCase());
		personRoleDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
		personRoleDto.setCdStage(ServiceConstants.CSTAGES_PAL);
		PersonRoleResultDto personRoleResultDto = stagePersonDao.getIdPersonForDifferentRole(personRoleDto);
		childContactDto.setIdPalStage(personRoleResultDto.getIdStage());
		childContactDto.setIdPlcmtChild(personRoleResultDto.getIdPerson());

	}

	/**
	 * 
	 * Method Name: getUlSysNbrValidationMsg Method Description:Checks the
	 * number of Pal Training Elements taken by the Person and returns True if
	 * it is equal to or more than 3.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void getUlSysNbrValidationMsg(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {

		NbrValidationDto nbrValidationDto = new NbrValidationDto();
		nbrValidationDto.setArchInputStruct((ServiceInputDto) deleteContactDto.getServiceInputDto());
		nbrValidationDto.setIdStage(childContactDto.getIdPalStage());
		nbrValidationDto.setIdPerson(childContactDto.getIdPlcmtChild());
		ValidationMsgDto cmsc14do = sysNbrValidationDao.getUlSysNbrValidationMsg(nbrValidationDto);
		childContactDto.setBindSendPalFollowup(cmsc14do.getUlSysNbrValidationMsg() != ServiceConstants.Zero
				? ServiceConstants.ARCHITECTURE_CONS_Y : ServiceConstants.ARCHITECTURE_CONS_N);
	}

	/**
	 * 
	 * Method Name: getPalWorkerAssigned Method Description: Retrieves the Pal
	 * Worker assigned to the Pal Stage.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void getPalWorkerAssigned(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {
		AdminWorkerInpDto adminWorkerInpDto = new AdminWorkerInpDto();
		adminWorkerInpDto.setServiceInputDto((ServiceInputDto) deleteContactDto.getServiceInputDto());
		adminWorkerInpDto.setIdStage(childContactDto.getIdPalStage());
		adminWorkerInpDto.setCdStagePersRole(ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);

		AdminWorkerOutpDto cinv51do = adminWorkerDao.getPersonInRole(adminWorkerInpDto);
		if (ServiceConstants.Zero < cinv51do.getIdTodoPersAssigned()) {
			childContactDto.setIdPalWorker(cinv51do.getIdTodoPersAssigned());
		}

	}

	/**
	 * 
	 * Method Name: sendToDoToPalWrkr Method Description:this method does insert
	 * and delete operation
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void sendToDoToPalWrkr(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {
		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();

		MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
		todoCreateInDto.setServiceInputDto((ServiceInputDto) deleteContactDto.getServiceInputDto());
		mergeSplitToDoDto.setIdTodoCfEvent(ServiceConstants.ZERO_VAL);
		mergeSplitToDoDto.setDtTodoCfDueFrom(new Date());
		mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_INFO_41_CODE);
		mergeSplitToDoDto.setIdTodoCfPersAssgn(childContactDto.getIdPalWorker());
		mergeSplitToDoDto.setIdTodoCfStage(childContactDto.getIdPalStage());
		todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);

		todoCreateService.TodoCommonFunction(todoCreateInDto);

	}

	/**
	 * 
	 * Method Name: checkPersonVictim Method Description:Checks if the person
	 * was ever a victim by querying the Allegation_History Table.
	 * 
	 * @param deleteContactDto
	 * @param childContactDto
	 */
	private void checkPersonVictim(ContactAUDDto deleteContactDto, ChildContactDto childContactDto) {

		PersonVictimDiDto personVictimDiDto = new PersonVictimDiDto();
		personVictimDiDto.setServiceInputDto((ServiceInputDto) deleteContactDto.getServiceInputDto());
		BindVictimRoleArrayDto bindVictimRoleArrayDto = new BindVictimRoleArrayDto();
		for (int i = ServiceConstants.Zero; i < deleteContactDto.getRowSelected(); i++) {
			personVictimDiDto
					.setUlIdAllegationStage(deleteContactDto.getSynchronizationServiceDto().getIdStage().intValue());
			personVictimDiDto.setUlIdPerson(deleteContactDto.getPersonStatusDtoList().get(i).getUlIdPerson());

			PersonVictimDoDto personVictimDoDto = victimRoleDao.getIndVictimRole(personVictimDiDto);
			bindVictimRoleArrayDto.addBIndVictimRole(personVictimDoDto.getBindVictimRole());
		}
		childContactDto.setIndSendEdit(ServiceConstants.TRUE);
		for (int i = ServiceConstants.Zero; i < deleteContactDto.getRowSelected(); i++) {
			if (isTrue(childContactDto.getBindVictimRoleArrayDto().getBIndVictimRole(i))) {
				childContactDto.setIndSendEdit(ServiceConstants.FALSE);
			}
		}
	}

	/**
	 * 
	 * Method Name: updateInitialContactDate Method Description:Updates the
	 * Investigation Initiated date in CPS_INVST_DETAIL table
	 * 
	 * @param deleteContactDto
	 */
	private void updateInitialContactDate(ContactAUDDto deleteContactDto) {
		InitialContactUpdateDto initialContactUpdateDto = new InitialContactUpdateDto();
		initialContactUpdateDto.setArchInputStructDto((ServiceInputDto) deleteContactDto.getServiceInputDto());
		initialContactUpdateDto.setUlIdStage(deleteContactDto.getSynchronizationServiceDto().getIdStage().intValue());
		initialContactUpdateDto.setDtDtCPSInvstDtlBegun(deleteContactDto.getDtContactOccurred());
		cpsInvstDetailUpdateDao.updateInitialContactDate(initialContactUpdateDto);

	}

	/**
	 * 
	 * Method Name: audEvent Method Description:This DAM is AUD for EVENT, here
	 * set to REQ_FUNC_CD_DELETE a given ID_EVENT with time stamp
	 * 
	 * @param deleteContactDto
	 */
	private void audEvent(ContactAUDDto deleteContactDto) {
		EventInputDto eventInputDto = new EventInputDto();
		eventInputDto.setArchInputStruct((ServiceInputDto) deleteContactDto.getServiceInputDto());
		eventInputDto.setIdEvent(deleteContactDto.getSynchronizationServiceDto().getIdEvent().intValue());
		eventInputDto.setEventLastUpdate(deleteContactDto.getSynchronizationServiceDto().getDtLastUpdate());
		eventInputDto.setReqFunctionCd(ServiceConstants.REQ_FUNC_CD_DELETE);
		eventProcessDao.ccmn46dAUDdam(eventInputDto);

	}

	private void deleteEventFromInrGroup(Long idDeleteEvent, Long idDeletePerson, Long inrGroupId, Long idPersonUpdate,
										 Map<Long, SimpleEventStageDto> personIdToDataMap) {
		// delete all people from this event
		for (SimpleEventStageDto currExistingPersonLink : personIdToDataMap.values()) {
			sdmSafetyAssessmentDao.deleteEventPersonLink(currExistingPersonLink.getIdPerson().intValue(), idDeleteEvent.intValue());
		}
		// delete this person from all events
		for (SimpleEventStageDto currExistingPersonLink : personIdToDataMap.values()) {
			if (!currExistingPersonLink.getIdEvent().equals(idDeleteEvent)) {
				sdmSafetyAssessmentDao.deleteEventPersonLink(idDeletePerson.intValue(), currExistingPersonLink.getIdEvent().intValue());
			} else {
				currExistingPersonLink.setDeleted(true);
			}
		}
		// delete event from group
		ContactAUDDto deleteEventFromGroupParam = new ContactAUDDto();
		deleteEventFromGroupParam.setServiceInputDto(new ServiceInputDto());
		deleteEventFromGroupParam.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
		deleteEventFromGroupParam.setIdEvent(idDeleteEvent);
		deleteEventFromGroupParam.setInrGroupId(inrGroupId);
		contactProcessDao.audInrGrouping(deleteEventFromGroupParam);


		// delete contact record
		ContactAUDDto deleteContactParam = new ContactAUDDto();
		deleteContactParam.setServiceInputDto(new ServiceInputDto());
		deleteContactParam.getServiceInputDto().setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
		deleteContactParam.setSynchronizationServiceDto(new SynchronizationServiceDto());
		deleteContactParam.getSynchronizationServiceDto().setIdEvent(idDeleteEvent);
		deleteContactParam.setIdPerson(idPersonUpdate);
		deleteContactParam.setIdPersonUpdate(idPersonUpdate.intValue());
		deleteContactParam.setIdEvent(idDeleteEvent);
		deleteContactParam.setInrGroupId(inrGroupId);
		audContact(deleteContactParam);

		// delete event record
		EventInputDto eventInputDto = new EventInputDto();
		eventInputDto.setIdEvent(idDeleteEvent);
		eventProcessDao.deleteEvent(eventInputDto);
	}
}
