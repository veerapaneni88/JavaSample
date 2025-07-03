package us.tx.state.dfps.service.contactnarrlog.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseMergeCustomDao;
import us.tx.state.dfps.service.casepackage.dto.ContactNarrativeDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CpsIntakeReportReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.contact.dto.ContactNarrFormDto;
import us.tx.state.dfps.service.contact.dto.ContactNarrGuideDto;
import us.tx.state.dfps.service.contact.dto.ContactNarraLogFormDto;
import us.tx.state.dfps.service.contactnarrlog.Service.ContactNarrLogAppendService;
import us.tx.state.dfps.service.contactnarrlog.dao.ContactNarrLogAppendDao;
import us.tx.state.dfps.service.contacts.dao.ContactEventPersonDao;
import us.tx.state.dfps.service.contacts.dao.ContactProcessDao;
import us.tx.state.dfps.service.contacts.dao.InrSafetyDao;
import us.tx.state.dfps.service.contacts.dao.StageSearchEventDao;
import us.tx.state.dfps.service.contacts.service.StageClosureEventService;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvContactSdmSafetyAssessDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvIntakePersonPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportIntakeDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportMergedDto;
import us.tx.state.dfps.service.cpsinvreport.dao.CpsInvReportDao;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ContactLogNarrativeFormPrefill;
import us.tx.state.dfps.service.forms.util.ContactNarrPrefillData;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.person.dao.PersonDetailDao;
import us.tx.state.dfps.service.workload.dao.ContactDao;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactFieldDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.InrSafetyFieldDto;

import static us.tx.state.dfps.common.web.WebConstants.INR_CONTACT_TYPES;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactNarrLogAppendServiceImpl CSVC06S Feb 14, 2018- 3:06:19 PM
 * © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ContactNarrLogAppendServiceImpl implements ContactNarrLogAppendService {

	@Autowired
	PcaDao pcaDao;

	@Autowired
	ContactEventPersonDao contactEventPersonDao;

	@Autowired
	ContactNarrLogAppendDao contactNarrLogAppendDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	CapsCaseDao capsCaseDao;

	@Autowired
	CaseMergeCustomDao caseMergeCustomDao;

	@Autowired
	ContactLogNarrativeFormPrefill contactLogNarrativeFormPrefill;

	@Autowired
	ContactNarrPrefillData contactNarrPrefillData;

	@Autowired
	CpsInvReportDao cpsInvReportDao;

	@Autowired
	ContactDao contactDao;

	@Autowired
	InrSafetyDao inrSafetyDao;

	@Autowired
	ContactProcessDao contactProcessDao;

	@Autowired
	StageClosureEventService stageClosureEventService;

	@Autowired
	StageSearchEventDao stageSearchEventDao;

	@Autowired
	PersonDetailDao personDetailDao;

	/*
	 * contact/sdm stage types
	 *
	 */
	private static final Set<String> SDM_AS_CONTACT_STAGES = new HashSet<String>(
			Arrays.asList(new String[] { CodesConstant.CSTAGES_INV, CodesConstant.CSTAGES_AR }));

	private static final String ZERO = "0";
	private static final String DATE_FORMAT = "MM/dd/yyyy";

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getContactNarrLogDetails(CpsIntakeReportReq cpsIntakeReportReq) {

		ContactNarraLogFormDto contactNarraLogFormDto = new ContactNarraLogFormDto();
		Date dtSampleFrom = new Date();
		Date dtSampleTo = new Date();

		// INITIAL SUMMARY SECTION call DAM CallCINT21D
		StageDto stageDto = stageDao.getStageById(cpsIntakeReportReq.getIdStage());
		if (!ObjectUtils.isEmpty(cpsIntakeReportReq.getDtSampleFrom())
				&& !ObjectUtils.isEmpty(cpsIntakeReportReq.getDtSampleTo())) {
			try {
				dtSampleFrom = new SimpleDateFormat(DATE_FORMAT).parse(cpsIntakeReportReq.getDtSampleFrom());
				//Warranty Defect - 11939  - To include the ToDate in the Contact Fetch Query
				dtSampleTo = new SimpleDateFormat(DATE_FORMAT).parse(cpsIntakeReportReq.getDtSampleTo());
				stageDto.setDtStageStart(dtSampleFrom);
			} catch (ParseException e) {
				ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
				serviceLayerException.initCause(e);
				throw serviceLayerException;
			}
		}
		Long intStageId = cpsInvReportDao.getPriorIntStage(cpsIntakeReportReq.getIdStage());
		// MERGED AND INTAKE NARRATIVES
		CpsInvReportMergedDto cpsInvReportMergedDto = new CpsInvReportMergedDto();
		List<CpsInvReportIntakeDto> cpsInvReportIntakeDtos = new ArrayList<CpsInvReportIntakeDto>();
		if (SDM_AS_CONTACT_STAGES.contains(stageDto.getCdStage())) {
			cpsInvReportMergedDto = cpsInvReportDao.getMergedStages(stageDto);
			if (!ObjectUtils.isEmpty(cpsInvReportMergedDto.getStrMergedStages())) {
				CpsInvReportIntakeDto cpsInvReportIntakeDto = null;
				List<CpsInvIntakePersonPrincipalDto> cpsInvReportIntakePersonDtos = cpsInvReportDao
						.getIntakes(cpsInvReportMergedDto.getStrMergedStages());
				for (CpsInvIntakePersonPrincipalDto cpsInvIntakePersonPrincipalDto : cpsInvReportIntakePersonDtos) {
					cpsInvReportIntakeDto = new CpsInvReportIntakeDto();
					//Defect# 11316
					BeanUtils.copyProperties(cpsInvIntakePersonPrincipalDto, cpsInvReportIntakeDto);
					cpsInvReportIntakeDto.setTxtStagePersNotes(cpsInvIntakePersonPrincipalDto.getTxtStagePersNote());
					cpsInvReportIntakeDtos.add(cpsInvReportIntakeDto);
				}
			}
		}

		/**
		 * CONTACTS (FOR A-R,INV INCLUDING INTAKES AND CLOSED-TO-MERGE STAGES
		 **/
		CpsInvReportMergedDto invStagesVb = null;
		CpsInvReportMergedDto arStages = null;
		CpsInvReportMergedDto intStagesVb = null;
		List<CpsInvReportMergedDto> allStages = new ArrayList<CpsInvReportMergedDto>();
		Map<String, String> stageStrings = new HashMap<String, String>();

		if (SDM_AS_CONTACT_STAGES.contains(stageDto.getCdStage())) {
			invStagesVb = getAllInvStages(cpsInvReportMergedDto, stageDto);
			arStages = getAllArStages(invStagesVb, cpsInvReportMergedDto.getStrMergedStages(), stageDto);
			intStagesVb = cpsInvReportDao.getAllIntStages(arStages, stageDto, cpsInvReportIntakeDtos);
			allStages = getAllStages(intStagesVb, arStages, invStagesVb);
		} else {
			CpsInvReportMergedDto otherStage = new CpsInvReportMergedDto();
			otherStage.setCdStage(stageDto.getCdStage());
			otherStage.setStrMergedStages(String.valueOf(stageDto.getIdStage()));
			allStages.add(otherStage);
			stageStrings.put(stageDto.getCdStage(), String.valueOf(stageDto.getIdStage()));
		}
		for (CpsInvReportMergedDto mergeStageDto : allStages) {
			stageStrings.put(mergeStageDto.getCdStage(), String.valueOf(mergeStageDto.getStrMergedStages()));
		}

		List<CpsInvContactSdmSafetyAssessDto> rawContacts = cpsInvReportDao.getContacts(allStages);
		List<CpsInvContactSdmSafetyAssessDto> contacts = new ArrayList<>();
		Set<Long> processedGroups = new HashSet<>();
		for (CpsInvContactSdmSafetyAssessDto currRawContact : rawContacts) {
			if (INR_CONTACT_TYPES.contains(currRawContact.getCdContactType())) {
				Long groupNum = contactProcessDao.getInrGroupNum(currRawContact.getIdEvent());

				if (groupNum != null) {
					if (!processedGroups.contains(groupNum)) {
						// grab extra stuff from contact:
						CpsInvContactSdmSafetyAssessDto dbInrContact = cpsInvReportDao.getInrContactFields(currRawContact.getIdEvent());
						// Date of Notifications; Date of Staffing IRSP-7-AC-1
						currRawContact.setDtDTContactOccurred(dbInrContact.getDtDTContactOccurred());
						currRawContact.setDtNotification(dbInrContact.getDtNotification());
						// caseworker IRSP-7-AC-3
						currRawContact.setIdCaseworker(dbInrContact.getIdCaseworker());
						currRawContact.setNmCaseworker(dbInrContact.getNmCaseworker());
						// supervisor IRSP-7-AC-3
						currRawContact.setIdSupervisor(dbInrContact.getIdSupervisor());
						currRawContact.setNmSupervisor(dbInrContact.getNmSupervisor());
						// director IRSP-7-AC-3
						currRawContact.setIdDirector(dbInrContact.getIdDirector());
						currRawContact.setNmDirector(dbInrContact.getNmDirector());
						// discussions IRSP-7-AC-5
						currRawContact.setTxtSummDiscuss(dbInrContact.getTxtSummDiscuss());
						currRawContact.setTxtIdentfdSafetyConc(dbInrContact.getTxtIdentfdSafetyConc());

						// out of state IRSP-7-AC-4 (which overrides inrSafetyFieldDtoList)
						ContactNarrativeDto intakeAlternative = stageSearchEventDao.getIntakeReportAlternatives(groupNum);

						if (ObjectUtils.isEmpty(intakeAlternative.getCdContactOthers())) {
							// intake narratives IRSP-7-AC-2
							List<Long> intakeStageList = contactProcessDao.getIntakeStageListByGroupId(groupNum);
							List<ContactNarrativeDto> intakeNarrativeList = stageSearchEventDao.getContactDetailIntakeReports(intakeStageList);
							currRawContact.setIntakeNarrativeList(intakeNarrativeList);
						} else {
							currRawContact.setCdInrProviderRegType(intakeAlternative.getCdContactOthers());
							currRawContact.setTxtNarrativeRpt(intakeAlternative.getStrNarrative());
						}

						// IRSP-7-AC-5 inrSafetyFieldDtoList
						ContactFieldDiDto contactFieldDiDto = new ContactFieldDiDto();
						contactFieldDiDto.setGroupNum(groupNum);
						List<InrSafetyFieldDto> inrSafetyFieldDtoList = inrSafetyDao.getFollowUpList(contactFieldDiDto);
						currRawContact.setInrSafetyFieldDtoList(inrSafetyFieldDtoList);
						contacts.add(currRawContact);
						processedGroups.add(groupNum);
					} // let the duplicates get dropped and not added to list
				} else {
					contacts.add(currRawContact); // pre-v2 I&R contact
				}
			} else {
				contacts.add(currRawContact); // normal, other contact
			}

		}
		contactNarraLogFormDto.setContactSdmSafetyAssessDtoList(contacts);

		contactNarraLogFormDto.setCpsInvComDtoList(cpsInvReportDao.getLogContactNames(allStages));

		List<ContactNarrGuideDto> contactGuideList = cpsInvReportDao.getContactGuideList(stageDto, dtSampleFrom,
				dtSampleTo);
		contactNarraLogFormDto.setContactNarrGuideDtoList(contactGuideList);
		contactNarraLogFormDto.setIntakeStageId(intStageId);
		contactNarraLogFormDto.setDtSampleFrom(dtSampleFrom);
		contactNarraLogFormDto.setDtSampleTo(dtSampleTo);
		contactNarraLogFormDto.setIdStage(cpsIntakeReportReq.getIdStage());
		contactNarraLogFormDto.setStageDto(stageDto);
		if (ObjectUtils.isEmpty(cpsIntakeReportReq.getDocType())) {
			contactNarraLogFormDto.setDocType(ServiceConstants.EMPTY_STRING);
		} else {
			contactNarraLogFormDto.setDocType(cpsIntakeReportReq.getDocType());
		}

		return contactLogNarrativeFormPrefill.returnPrefillData(contactNarraLogFormDto);
	}

	/**
	 * 
	 * Method Name: getAllInvStages Method Description:Add in the selected stage
	 * to the merged INV stage string.
	 * 
	 * @param invMergeStagesVb
	 * @param stageDto
	 * @return allInvStagesBean
	 */
	private CpsInvReportMergedDto getAllInvStages(CpsInvReportMergedDto invMergeStagesVb, StageDto stageDto) {
		CpsInvReportMergedDto allInvStagesBean = new CpsInvReportMergedDto();
		allInvStagesBean.setDtEarliestStageStart(invMergeStagesVb.getDtEarliestStageStart());
		allInvStagesBean.setStrMergedStages(
				(!ObjectUtils.isEmpty(invMergeStagesVb) && (ObjectUtils.isEmpty(invMergeStagesVb.getStrMergedStages())
						|| "0".equals(invMergeStagesVb.getStrMergedStages()))) ? stageDto.getIdStage().toString()
								: (invMergeStagesVb.getStrMergedStages() + ',' + stageDto.getIdStage()));
		allInvStagesBean.setCdStage("INV");
		return allInvStagesBean;
	}

	/**
	 * 
	 * Method Name: getAllArStages Method Description:Add in the selected stage
	 * to the merged AR stage string.
	 * 
	 * @param invMergeStagesVb
	 * @param mergedStages
	 * @param stageDto
	 * @return arStages
	 */
	private CpsInvReportMergedDto getAllArStages(CpsInvReportMergedDto invMergeStagesVb, String mergedStages,
			StageDto stageDto) {
		CpsInvReportMergedDto arStages = new CpsInvReportMergedDto();
		arStages.setStrMergedStages(cpsInvReportDao
				.getArStages(invMergeStagesVb.getStrMergedStages(), stageDto.getIdCase()).getStrMergedStages());
		arStages.setCdStage("A-R");
		return arStages;
	}

	/**
	 * 
	 * Method Name: getAllStages Method Description:Add stage string to create
	 * Arraylist of all stages to get contacts. Must add Intake stages first,
	 * which can never be ZERO.
	 * 
	 * @param intStagesVb
	 * @param arStages
	 * @param invStagesVb
	 * @return allStages
	 */
	private List<CpsInvReportMergedDto> getAllStages(CpsInvReportMergedDto intStagesVb, CpsInvReportMergedDto arStages,
			CpsInvReportMergedDto invStagesVb) {
		List<CpsInvReportMergedDto> allStages = new ArrayList<CpsInvReportMergedDto>();
		allStages.add(intStagesVb);
		if (!ZERO.equals(arStages.getStrMergedStages())) {
			allStages.add(arStages);
		}
		if (!ZERO.equals(invStagesVb.getStrMergedStages())) {
			allStages.add(invStagesVb);
		}
		return allStages;
	}

	/**
	 * 
	 * Method Name: getContactNarr Method Description:Method used to return the
	 * pre fill for the contact narrative
	 * 
	 * @param contactNarrativeReq
	 * @return PreFillDataServiceDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getContactNarr(Long idEvent) {
		Contact contact = contactDao.getContactEntityById(idEvent);
		ContactNarrFormDto contactNarrFormDto = new ContactNarrFormDto();
		contactNarrFormDto.setCdContactMethod(contact.getCdContactMethod());
		contactNarrFormDto.setCdContactOthers(contact.getCdContactOthers());
		contactNarrFormDto.setCdContactPurpose(contact.getCdContactPurpose());
		contactNarrFormDto.setDtContactOccurred(DateUtils.formatDatetoString(contact.getDtContactOccurred()));
		contactNarrFormDto.setTmScrTmCntct(DateUtils.getTime(contact.getDtContactOccurred()));
		contactNarrFormDto.setTxtNmPersonFull(contact.getNmContactOth());
		return contactNarrPrefillData.returnPrefillData(contactNarrFormDto);
	}
}
