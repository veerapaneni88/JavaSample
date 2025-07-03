package us.tx.state.dfps.service.forms.util;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.FamilyPlan;
import us.tx.state.dfps.common.domain.FamilyPlanEval;
import us.tx.state.dfps.common.domain.FamilyPlanNeed;
import us.tx.state.dfps.common.domain.FamilyPlanPartcpnt;
import us.tx.state.dfps.common.domain.FamilyPlanReqrdActn;
import us.tx.state.dfps.common.web.CodesConstant;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.fsna.dto.CpsMonthlyEvalDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.response.SDMRiskReassessmentRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.familyplan.dto.PrincipalParticipantDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.stageutility.dao.StageUtilityDao;
import us.tx.state.dfps.service.workload.dao.ContactDao;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventPersonLinkDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@Component
public class CpsMonthlyEvalPrefillData extends DocumentServiceUtil {

	@Autowired
	LookupDao lookupDao;

	@Autowired
	StageUtilityDao stageUtilityDao;

	@Autowired
	PersonUtil personUtil;

	@Autowired
	EventDao eventDao;

	@Autowired
	EventPersonLinkDao eventPersonLinkDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	CharacteristicsDao characteristicsDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	ContactDao contactDao;

	@Autowired
	LookupService lookupService;


	public static final String FSNA_MSG = "No information found because an exception is provided.";
	
	public static final String NO_RECORDS_EXISTS_MSG = "No records exist";

	public String getTffEvidenceBasedServices(FamilyPlanReqrdActn familyPlanReqrdActn) {
		List<String> tffEvidenceBasedServicesList = new ArrayList<String>();
		if (Objects.nonNull(familyPlanReqrdActn) && Objects.nonNull(familyPlanReqrdActn.getTffevidenceBasedSvcs())) {
			tffEvidenceBasedServicesList.addAll(Objects.nonNull(familyPlanReqrdActn.getTffevidenceBasedSvcs()) ? Arrays.asList(familyPlanReqrdActn.getTffevidenceBasedSvcs().split(",", -1)) : new ArrayList<String>());

		}
		List<String> selectedTffEvidenceSvcsValueList = new ArrayList();
		if (CollectionUtils.isNotEmpty(tffEvidenceBasedServicesList)) {
			HashMap<String, TreeMap<String, CodeAttributes>> codesMap = lookupService.getCodes();
			if (Objects.nonNull(codesMap)) {
				TreeMap<String, CodeAttributes> codeAttributesTreeMap = codesMap.get(CodesConstant.CSVCCODE);
				selectedTffEvidenceSvcsValueList = tffEvidenceBasedServicesList.stream().map(x ->{
							return codeAttributesTreeMap.get(x).getDecode();
				}
				).collect(Collectors.toList());

			}
			return String.join(",", selectedTffEvidenceSvcsValueList);
		}
		return "";
	}

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		/**
		 * TypeCasting parentDtoobj to SijsStatusFormDto
		 */

		CpsMonthlyEvalDto cpsMonthlyEvalDto = (CpsMonthlyEvalDto) parentDtoobj;
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		Boolean isCCExists = ServiceConstants.FALSEVAL;
		Integer caregiverSection = ServiceConstants.Zero;
		Boolean isPCSPExists = ServiceConstants.FALSEVAL;
		Integer pcspSection = ServiceConstants.Zero;
		Boolean isContactExists = ServiceConstants.FALSEVAL;
		Boolean isFSNAExists = ServiceConstants.FALSEVAL;
		Integer fsnaSection = ServiceConstants.Zero;

		/**
		 * Start - Logic for the Calculation of Last Day of Evaluation Month
		 * ------------------------------------------------
		 */

		// Use the Contact Date selected by User
		String[] strdateSplit = cpsMonthlyEvalDto.getDtEvaluation().split("/");
		int[] intdateSplit = new int[strdateSplit.length];
		// Creates the integer array.
		for (int i = 0; i < intdateSplit.length; i++) {
			intdateSplit[i] = Integer.parseInt(strdateSplit[i]);
			// Parses the integer for each string.
		}

		// Setting the First Day and Last Day of Month when the Evaluation was
		// recorded
		LocalDate lastDayOfMonth = new LocalDate(intdateSplit[2], intdateSplit[0], ServiceConstants.One_Integer)
				.dayOfMonth().withMaximumValue();
		LocalDate firstDayOfMonth = new LocalDate(intdateSplit[2], intdateSplit[0], ServiceConstants.One_Integer)
				.dayOfMonth().withMinimumValue();
		Date dtlastdayofEval = lastDayOfMonth.toDate();
		Date dtfirstdayofEval = firstDayOfMonth.toDate();

		// Fetch the Date of Approval of the Monthly Eval Event and assign the
		// same to Last Day of Month when the Evaluation was recorded
		// so that prefill happens with data saved before either date of
		// approval or last day of month whichever is earlier
		ContactDto monthlyEvalContactDto = contactDao.getContactById(cpsMonthlyEvalDto.getIdEvent());
		if (!ObjectUtils.isEmpty(monthlyEvalContactDto.getDtContactApprv())) {
			dtlastdayofEval = monthlyEvalContactDto.getDtContactApprv();
		}

		/**
		 * End - Logic for the Calculation of Last Day of Evaluation Month
		 * --------------------------------------------------
		 */

		/**
		 * Start - Logic for the Caregiver and Children Section Population
		 * --------------------------------------------------
		 */

		// Populate the CAREGIVER(S) AND CHILD(REN)

		List<EventPersonLinkDto> childrenCareGiverSelected = new ArrayList<EventPersonLinkDto>();
		childrenCareGiverSelected = eventPersonLinkDao.getEventPersonLinkForIdEvent(cpsMonthlyEvalDto.getIdEvent());
		if (CollectionUtils.isNotEmpty(childrenCareGiverSelected)) {
			for (EventPersonLinkDto children : childrenCareGiverSelected) {

				// Setting the
				isCCExists = ServiceConstants.TRUEVAL;

				// Value is used create dynamic names to save multiple narrative
				// txt
				caregiverSection = caregiverSection + ServiceConstants.One_Integer;

				PersonDto personDto = personDao.getPersonById(children.getIdPerson());

				List<CharacteristicsDto> personCharacter = characteristicsDao
						.getCharacteristicDetails(children.getIdPerson());

				String relation = stagePersonLinkDao
						.getStagePersonLink(cpsMonthlyEvalDto.getIdStage(), children.getIdPerson())
						.getCdStagePersRelInt();

				String personcharacteristics = ServiceConstants.SPACE;
				if (CollectionUtils.isNotEmpty(personCharacter)) {

					for (CharacteristicsDto characteristicsDto : personCharacter) {
						if (personCharacter.size() == ServiceConstants.Zero_INT) {
                            // Warranty Defect Fix - 11323 - Incorrect Code Type was Mapped
							personcharacteristics = lookupDao.decode(ServiceConstants.PARENT_CARETAKER_CHAR_CATEGORY,
									characteristicsDto.getCdCharacteristic()) + ServiceConstants.COMMA_SPACE;
							break;
						} else {
							// Warranty Defect Fix - 11323 - Incorrect Code Type was Mapped
							personcharacteristics = lookupDao.decode(ServiceConstants.PARENT_CARETAKER_CHAR_CATEGORY,
									characteristicsDto.getCdCharacteristic()) + ServiceConstants.COMMA_SPACE
									+ personcharacteristics;
						}

					}

				}

				FormDataGroupDto tempCcFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CC,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkCcList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCcName = createBookmark(BookmarkConstants.CC_NAME, personDto.getNmPersonFull());
				BookmarkDto bookmarkCcRelation = createBookmark(BookmarkConstants.CC_RELTN,
						lookupDao.decode(ServiceConstants.CRPTRINT, relation));
				BookmarkDto bookmarkCcDOB = createBookmark(BookmarkConstants.CC_DOB,
						TypeConvUtil.formDateFormat(personDto.getDtPersonBirth()));
				BookmarkDto bookmarkTxtAbsentPrn1 = createBookmark(BookmarkConstants.UE_GROUPID,
						personDto.getIdPerson());
				bookmarkCcList.add(bookmarkTxtAbsentPrn1);
				Date lastFPRContact = null;
				if (CollectionUtils.isNotEmpty(cpsMonthlyEvalDto.getContact())) {
					for (ContactDto contactDto : cpsMonthlyEvalDto.getContact()) {

						List<EventPersonLinkDto> PrincipalsCollateralsSelected = new ArrayList<EventPersonLinkDto>();
						if (lookupDao.decode(ServiceConstants.CCNTCTYP, contactDto.getCdContactType())
								.equals(ServiceConstants.Contact)) {
							EventDto contactEvent = eventDao.getEventByid(contactDto.getIdEvent());

							PrincipalsCollateralsSelected = eventPersonLinkDao
									.getEventPersonLinkForIdEvent(contactEvent.getIdEvent());

						}

						if (CollectionUtils.isNotEmpty(PrincipalsCollateralsSelected)) {

							for (EventPersonLinkDto eventPersonLinkDto : PrincipalsCollateralsSelected) {

								if (eventPersonLinkDto.getIdPerson().equals(children.getIdPerson())
										&& ServiceConstants.FTF.equals(contactDto.getCdContactMethod())) {
									lastFPRContact = contactDto.getDtContactOccurred();
									break;
								}
							}
						}

					}
				}
				BookmarkDto bookmarkCcLastContact = createBookmark(BookmarkConstants.CC_LAST_CONTACT,
						TypeConvUtil.formDateFormat(lastFPRContact));

				if (!personcharacteristics.equals(ServiceConstants.SPACE)) {
					BookmarkDto bookmarkPersonCharacter = createBookmark(BookmarkConstants.CC_PERSON_CHARACTER,
							personcharacteristics.substring(ServiceConstants.Zero, personcharacteristics.length() - 3));
					bookmarkCcList.add(bookmarkPersonCharacter);
				}

				List<FormDataGroupDto> absentPrnfrmGroupDtoList = new ArrayList<FormDataGroupDto>();

				if (relation.equals(ServiceConstants.REL_INT_PA) || relation.equals(ServiceConstants.REL_INT_PB)
						|| relation.equals(ServiceConstants.REL_INT_GU)
						|| relation.equals(ServiceConstants.REL_INT_AB)) {
					FormDataGroupDto tempCcAbsentPrnFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CC_ABSENT_PRN, FormGroupsConstants.TMPLAT_CC);				
					List<BookmarkDto> bookmarkAbsentPrntCcList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkTxtAbsentPrn = createBookmark(BookmarkConstants.UE_GROUPID,
							caregiverSection);
					bookmarkAbsentPrntCcList.add(bookmarkTxtAbsentPrn);
					tempCcAbsentPrnFrmDataGrpDto.setBookmarkDtoList(bookmarkAbsentPrntCcList);					
					absentPrnfrmGroupDtoList.add(tempCcAbsentPrnFrmDataGrpDto);
				}

				bookmarkCcList.add(bookmarkCcName);
				bookmarkCcList.add(bookmarkCcRelation);
				bookmarkCcList.add(bookmarkCcDOB);
				bookmarkCcList.add(bookmarkCcLastContact);
				tempCcFrmDataGrpDto.setBookmarkDtoList(bookmarkCcList);
				tempCcFrmDataGrpDto.setFormDataGroupList(absentPrnfrmGroupDtoList);
				formDataGroupList.add(tempCcFrmDataGrpDto);

			}
		}

		if (isCCExists) {
			// Display the Table Header only when the CC section has value
			FormDataGroupDto tempCCHdrFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CC_HDR,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempCCHdrFrmDataGrpDto);
			FormDataGroupDto tempCCAddtnInfoFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CC_ADDTN_INFO, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempCCAddtnInfoFrmDataGrpDto);
		} else {
			// Display "No Records Exists" and Additional Info Text Box when no
			// data is available for CC Section
			FormDataGroupDto tempNoCCFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NO_CC,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempNoCCFrmDataGrpDto);
		}

		/**
		 * End - Logic for the Care giver and Children Section Population
		 * --------------------------------------------------
		 */

		/**
		 * Start - Logic for the PCSP Section Population
		 * -----------------------------------------------------------
		 */

		// Populate the PCSP Section

		if (CollectionUtils.isNotEmpty(cpsMonthlyEvalDto.getPcsp())) {
			for (PcspDto pcspDto : cpsMonthlyEvalDto.getPcsp()) {

				Boolean blankpcspDate = ServiceConstants.FALSEVAL;

				if (!ObjectUtils.isEmpty(pcspDto.getDtEnd()) && pcspDto.getDtEnd().after(new Date())) {
					blankpcspDate = ServiceConstants.TRUEVAL;
				}

				if (blankpcspDate || (!ObjectUtils.isEmpty(pcspDto.getDtEnd())
						&& pcspDto.getDtEnd().after(dtfirstdayofEval) && pcspDto.getDtEnd().before(dtlastdayofEval))) {
					isPCSPExists = ServiceConstants.TRUEVAL;
					pcspSection = pcspSection + ServiceConstants.One_Integer;
					FormDataGroupDto tempPcspFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PCSP,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkPcspList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkPcspChildName = createBookmark(BookmarkConstants.PCSP_CHILD_NAME,
							pcspDto.getChildName());
					BookmarkDto bookmarkPcspStartDate = createBookmark(BookmarkConstants.PCSP_START_DATE,
							TypeConvUtil.formDateFormat(pcspDto.getDtStart()));
					BookmarkDto bookmarkPcspEndDate = createBookmark(BookmarkConstants.PCSP_END_DATE,
							(blankpcspDate == ServiceConstants.TRUEVAL) ? ServiceConstants.SPACE
									: TypeConvUtil.formDateFormat(pcspDto.getDtEnd()));
					BookmarkDto bookmarkPcspCareGiverName = createBookmark(BookmarkConstants.PCSP_CAREGIVER_NAME,
							pcspDto.getCaregiverName());
					
					BookmarkDto bookmarkTxtPcspRel = createBookmark(BookmarkConstants.UE_GROUPID,
							pcspSection);
					
					bookmarkPcspList.add(bookmarkPcspChildName);
					bookmarkPcspList.add(bookmarkPcspStartDate);
					bookmarkPcspList.add(bookmarkPcspEndDate);
					bookmarkPcspList.add(bookmarkPcspCareGiverName);					
					bookmarkPcspList.add(bookmarkTxtPcspRel);					
					tempPcspFrmDataGrpDto.setBookmarkDtoList(bookmarkPcspList);
					formDataGroupList.add(tempPcspFrmDataGrpDto);
				}
			}
		}

		if (isPCSPExists) {
			// Display the Table Header only when the PCSP section has value
			FormDataGroupDto tempPCSPHdrFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PCSP_HDR,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPCSPHdrFrmDataGrpDto);
			FormDataGroupDto tempPCSPAddtnInfoFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PCSP_ADDTN_INFO, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPCSPAddtnInfoFrmDataGrpDto);
		} else {
			// Display "No Records Exists" and Additional Info Text Box when no
			// data is available for PCSP Section
			FormDataGroupDto tempNoPCSPFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NO_PCSP,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempNoPCSPFrmDataGrpDto);
		}
		/**
		 * End - Logic for the PCSP Section Population
		 * -----------------------------------------------------------
		 */

		/**
		 * Start - Logic for the Contact Section Population
		 * -----------------------------------------------------------
		 */
		// Populate the Contact Summary Section Details
		
		if (CollectionUtils.isNotEmpty(cpsMonthlyEvalDto.getContact())) {
			for (ContactDto contactDto : cpsMonthlyEvalDto.getContact()) {

				if (!contactDto.getCdContactType().equals(ServiceConstants.BMTH)
						/*** Defect# 12998 fix ***/
                        && !getDate(contactDto.getDtContactOccurred()).after(cpsMonthlyEvalDto.getDtCntctMnthlySummEnd())
                        && !getDate(contactDto.getDtContactOccurred()).before(cpsMonthlyEvalDto.getDtCntctMnthlySummBeg())) {					
						
					isContactExists = ServiceConstants.TRUEVAL;
					List<FormDataGroupDto> tempFormDataGroupDtoList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto tempContactFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkContactList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkContacType = createBookmark(BookmarkConstants.CONTACT_TYPE,
							lookupDao.decode(ServiceConstants.CCNTCTYP, contactDto.getCdContactType()));
					BookmarkDto bookmarkContacDate = createBookmark(BookmarkConstants.CONTACT_DATE,
							TypeConvUtil.formDateFormat(contactDto.getDtContactOccurred()));
					BookmarkDto bookmarkContacPurpose = createBookmark(BookmarkConstants.CONTACT_PURPOSE,
							lookupDao.decode(ServiceConstants.CCNTPURP, contactDto.getCdContactPurpose()));
					BookmarkDto bookmarkContactMethod = createBookmark(BookmarkConstants.CONTACT_METHOD,
							lookupDao.decode(ServiceConstants.CCNTMETH, contactDto.getCdContactMethod()));
					BookmarkDto bookmarkContactOthers = createBookmark(BookmarkConstants.CONTACT_OTHERS,
							lookupDao.decode(ServiceConstants.COTHCNCT, contactDto.getCdContactOthers()));
					BookmarkDto bookmarkContactBy = createBookmark(BookmarkConstants.CONTACT_BY,
							personUtil.getPersonFullName(contactDto.getIdContactWorker()));
					if (contactDto.getIndContactAttempted().equals(ServiceConstants.Y)) {
						FormDataGroupDto tempContactAttemptFrmDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_CONTACT_ATTEMPT, FormGroupsConstants.TMPLAT_CONTACT);
						tempFormDataGroupDtoList.add(tempContactAttemptFrmDataGrpDto);
						tempContactFrmDataGrpDto.setFormDataGroupList(tempFormDataGroupDtoList);
					}

					List<EventPersonLinkDto> PrincipalsCollateralsSelected = new ArrayList<EventPersonLinkDto>();
					if (lookupDao.decode(ServiceConstants.CCNTCTYP, contactDto.getCdContactType())
							.equals(ServiceConstants.Contact)) {
						EventDto contactEvent = eventDao.getEventByid(contactDto.getIdEvent());

						PrincipalsCollateralsSelected = eventPersonLinkDao
								.getEventPersonLinkForIdEvent(contactEvent.getIdEvent());

					}

					String individualAssessed = ServiceConstants.SPACE;
					if (CollectionUtils.isNotEmpty(PrincipalsCollateralsSelected)) {

						for (EventPersonLinkDto eventPersonLinkDto : PrincipalsCollateralsSelected) {
							if (PrincipalsCollateralsSelected.size() == ServiceConstants.Zero_INT) {
								individualAssessed = personUtil.getPersonFullName(eventPersonLinkDto.getIdPerson())
										+ ServiceConstants.SPACE_SEMI_COLON_SPACE + individualAssessed;
								break;
							} else {
								individualAssessed = personUtil.getPersonFullName(eventPersonLinkDto.getIdPerson())
										+ ServiceConstants.SPACE_SEMI_COLON_SPACE + individualAssessed;
							}

						}

					}

					if (!individualAssessed.equals(ServiceConstants.SPACE)) {
						BookmarkDto bookmarkContactPrnCol = createBookmark(BookmarkConstants.CONTACT_PRN_COL,
								individualAssessed.substring(ServiceConstants.Zero, individualAssessed.length() - 3));
						bookmarkContactList.add(bookmarkContactPrnCol);
					}
					bookmarkContactList.add(bookmarkContacType);
					bookmarkContactList.add(bookmarkContacDate);
					bookmarkContactList.add(bookmarkContacPurpose);
					bookmarkContactList.add(bookmarkContactMethod);
					bookmarkContactList.add(bookmarkContactOthers);
					bookmarkContactList.add(bookmarkContactBy);
					tempContactFrmDataGrpDto.setBookmarkDtoList(bookmarkContactList);
					formDataGroupList.add(tempContactFrmDataGrpDto);
				}

			}
		}

		if (isContactExists) {
			// Display the Table Header only when the Contact section has value
			FormDataGroupDto tempContactHdrFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_HDR,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempContactHdrFrmDataGrpDto);
			FormDataGroupDto tempContactAddtnInfoFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CONTACT_ADDTN_INFO, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempContactAddtnInfoFrmDataGrpDto);
		} else {
			// Display "No Records Exists" and Additional Info Text Box when no
			// data is available for Contact Section
			FormDataGroupDto tempNoContactFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NO_CONTACT,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempNoContactFrmDataGrpDto);
		}

		/**
		 * End - Logic for the Contact Section Population
		 * -----------------------------------------------------------
		 */

		/**
		 * Start - Logic for the FSNA Section Population
		 * -----------------------------------------------------------
		 */

		// Populate the FAMILY STRENGTHS AND NEEDS ASSESSMENT (FSNA)

		if (CollectionUtils.isNotEmpty(cpsMonthlyEvalDto.getFsna())) {
			for (CpsFsnaDto cpsFsnaDto : cpsMonthlyEvalDto.getFsna()) {

				if (cpsFsnaDto.getCdEventStatus().equals(ServiceConstants.EVENT_STAT_COMP)
						|| cpsFsnaDto.getCdEventStatus().equals(ServiceConstants.EVENTSTATUS_APPROVE)
								&& cpsFsnaDto.getDtAsgnmntCmpltd().before(dtlastdayofEval)) {
					isFSNAExists = ServiceConstants.TRUEVAL;

					fsnaSection = fsnaSection + ServiceConstants.One_Integer;

					FormDataGroupDto tempFsnaFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FSNA,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkFsnaList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkFsnaCaregiverName = createBookmark(BookmarkConstants.FSNA_NM_CAREGIVER,
							ServiceConstants.SPACE);
					BookmarkDto bookmarkFsnaTxtDangerWorry = createBookmark(BookmarkConstants.FSNA_TXT_DANGER_WORRY,
							ServiceConstants.SPACE);
					BookmarkDto bookmarkFsnaTxtGoal = createBookmark(BookmarkConstants.FSNA_TXT_GOAL,
							ServiceConstants.SPACE);
					if (!ObjectUtils.isEmpty(cpsFsnaDto.getIdPrmryCrgvrPrnt())
							&& !ObjectUtils.isEmpty(cpsFsnaDto.getIdSecndryCrgvrPrnt())) {
						bookmarkFsnaCaregiverName = createBookmark(BookmarkConstants.FSNA_NM_CAREGIVER,
								personUtil.getPersonFullName(cpsFsnaDto.getIdPrmryCrgvrPrnt())
										+ ServiceConstants.SPACE_SEMI_COLON_SPACE
										+ personUtil.getPersonFullName(cpsFsnaDto.getIdSecndryCrgvrPrnt()));
					} else if (!ObjectUtils.isEmpty(cpsFsnaDto.getIdPrmryCrgvrPrnt())) {
						bookmarkFsnaCaregiverName = createBookmark(BookmarkConstants.FSNA_NM_CAREGIVER,
								personUtil.getPersonFullName(cpsFsnaDto.getIdPrmryCrgvrPrnt()));
					} else if (!ObjectUtils.isEmpty(cpsFsnaDto.getIdSecndryCrgvrPrnt())) {
						bookmarkFsnaCaregiverName = createBookmark(BookmarkConstants.FSNA_NM_CAREGIVER,
								personUtil.getPersonFullName(cpsFsnaDto.getIdSecndryCrgvrPrnt()));
					}

					BookmarkDto bookmarkFsnaDtCompleted = createBookmark(BookmarkConstants.FSNA_DT_COMPLETED,
							TypeConvUtil.formDateFormat(cpsFsnaDto.getDtAsgnmntCmpltd()));

					if (cpsFsnaDto.getCdEventStatus().equals(ServiceConstants.EVENTSTATUS_COMPLETE)) {
						bookmarkFsnaTxtDangerWorry = createBookmark(BookmarkConstants.FSNA_TXT_DANGER_WORRY,
								ObjectUtils.isEmpty(cpsFsnaDto.getTxtDngrWorry()) ? ServiceConstants.EMPTY_LINE
										: cpsFsnaDto.getTxtDngrWorry());
					} else if (cpsFsnaDto.getCdEventStatus().equals(ServiceConstants.EVENTSTATUS_APPROVE)) {
						bookmarkFsnaTxtDangerWorry = createBookmark(BookmarkConstants.FSNA_TXT_DANGER_WORRY, FSNA_MSG);
					}

					if (cpsFsnaDto.getCdEventStatus().equals(ServiceConstants.EVENTSTATUS_COMPLETE)) {
						bookmarkFsnaTxtGoal = createBookmark(BookmarkConstants.FSNA_TXT_GOAL,
								ObjectUtils.isEmpty(cpsFsnaDto.getTxtGoalStatmnts()) ? ServiceConstants.EMPTY_LINE
										: cpsFsnaDto.getTxtGoalStatmnts());
					} else if (cpsFsnaDto.getCdEventStatus().equals(ServiceConstants.EVENTSTATUS_APPROVE)) {
						bookmarkFsnaTxtGoal = createBookmark(BookmarkConstants.FSNA_TXT_GOAL, FSNA_MSG);
					}

					
					BookmarkDto bookmarkFsnaAddInfo = createBookmark(BookmarkConstants.UE_GROUPID,
							 fsnaSection);
					bookmarkFsnaList.add(bookmarkFsnaCaregiverName);
					bookmarkFsnaList.add(bookmarkFsnaDtCompleted);
					bookmarkFsnaList.add(bookmarkFsnaTxtDangerWorry);
					bookmarkFsnaList.add(bookmarkFsnaTxtGoal);					
					bookmarkFsnaList.add(bookmarkFsnaAddInfo);
					tempFsnaFrmDataGrpDto.setBookmarkDtoList(bookmarkFsnaList);
					formDataGroupList.add(tempFsnaFrmDataGrpDto);
				}
			}
		}

		if (!isFSNAExists) {
			// Display "No Records Exists" and Additional Info Text Box when no
			// data is available for FSNA Section
			BookmarkDto bookmarkFsnaAddInfo = createBookmark(BookmarkConstants.NO_FSNA,
					NO_RECORDS_EXISTS_MSG );
			bookmarkNonFrmGrpList.add(bookmarkFsnaAddInfo);
		}

		/**
		 * End - Logic for the FSNA Section Population
		 * -----------------------------------------------------------
		 */

		/**
		 * Start - Logic for the New Family Plan Section Population
		 * -----------------------------------------------------------
		 */

		// Populate the FAMILY PLAN REQUIRED ACTIONS for New Family Plan

		Boolean isFamilyPlanExists = ServiceConstants.FALSEVAL;

		Integer familyPlanSection = ServiceConstants.Zero;
			for (FamilyPlan familyplan : cpsMonthlyEvalDto.getFamilyPlan()) {
				if (CollectionUtils.isNotEmpty(familyplan.getFamilyPlanNeeds())
						&& CollectionUtils.isNotEmpty(familyplan.getFamilyPlanPartcpnts())) {

					if (familyplan.getDtCompleted().before(dtlastdayofEval)) {

						isFamilyPlanExists = ServiceConstants.TRUEVAL;
						familyPlanSection = familyPlanSection + ServiceConstants.One_Integer;
						List<FormDataGroupDto> FamilyPlanList = new ArrayList<FormDataGroupDto>();
						FormDataGroupDto tempFamilyPlanFrmDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_FAMILY_PLAN, FormConstants.EMPTY_STRING);
						List<BookmarkDto> bookmarkFamilyPlanList = new ArrayList<BookmarkDto>();						
						/*
						 * from the list of participants in the participant list ,get the primary
						 * participant and populate the bookmark for each family plan
						 */
						String familyPlanPartcpnts = familyplan
								.getFamilyPlanPartcpnts().stream()
								.filter(participant -> participant
										.getIndPartcpntType().equals(ServiceConstants.PRIMARY_PARTICIPANT))
								.map(d -> personUtil.getPersonFullName(d.getIdPerson()))
								.collect(Collectors.joining(", "));

						BookmarkDto bookmarkFamilyPlanDtCompleted = createBookmark(
								BookmarkConstants.DT_FAMLIY_PLAN,
								TypeConvUtil.formDateFormat(familyplan.getDtCompleted()));
						BookmarkDto bookmarkFamilyPlanPrimaryParticipant = createBookmark(
								BookmarkConstants.NM_FAMILY_PLAN, "Family Plan - "
										+ familyPlanPartcpnts);
						BookmarkDto bookmarkUEGroupId = createBookmark(
								BookmarkConstants.UE_GROUPID,
								familyPlanSection);						
						bookmarkFamilyPlanList.add(bookmarkFamilyPlanDtCompleted);
						bookmarkFamilyPlanList.add(bookmarkFamilyPlanPrimaryParticipant);
						bookmarkFamilyPlanList.add(bookmarkUEGroupId);
						tempFamilyPlanFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanList);
						// Loop through the Family Plan Participant and Family
						// Plan Needs
						int participant =0;
						
						
						for (FamilyPlanNeed familyplanneed : familyplan.getFamilyPlanNeeds()) {
							participant = participant + ServiceConstants.One_Integer;
							
							for (FamilyPlanPartcpnt familyPlanPartcpnt : familyplan.getFamilyPlanPartcpnts()) {									
								
								if (familyplanneed.getIdPerson().equals(familyPlanPartcpnt.getIdPerson())) {

									// Populate the Family Plan Participants
									FormDataGroupDto tempFamilyPlanPartcpntFrmDataGrpDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_PARTICIPANT,
											FormGroupsConstants.TMPLAT_FAMILY_PLAN);
									List<BookmarkDto> bookmarkFamilyPlanParticpnt = new ArrayList<BookmarkDto>();
									BookmarkDto bookmarkFamilyPlanPartcpntName = createBookmark(
											BookmarkConstants.PARTICIPANT_NAME,
											personUtil.getPersonFullName(familyPlanPartcpnt.getIdPerson()));
									BookmarkDto bookmarkTxt1 = createBookmark(
											BookmarkConstants.UE_GROUPID,participant);	
									bookmarkFamilyPlanParticpnt.add(bookmarkTxt1);
									bookmarkFamilyPlanParticpnt.add(bookmarkFamilyPlanPartcpntName);
									tempFamilyPlanPartcpntFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanParticpnt);
									

									// Populate the Required Action Fields
									Integer reqrdActnSection = 0;

									List<FormDataGroupDto> ReqdActnList = new ArrayList<FormDataGroupDto>();
									for (FamilyPlanReqrdActn familyPlanReqrdActn : familyplanneed.getFamilyPlanReqrdActns()) {
										reqrdActnSection = reqrdActnSection + ServiceConstants.One_Integer;
										FormDataGroupDto tempFamilyPlanReqdActnFrmDataGrpDto = createFormDataGroup(
												FormGroupsConstants.TMPLAT_REQ_ACTN,
												FormGroupsConstants.TMPLAT_PARTICIPANT);
										List<BookmarkDto> bookmarkFamilyPlanReqdActn = new ArrayList<BookmarkDto>();
										BookmarkDto bookmarkReqdActn = createBookmark(BookmarkConstants.REQ_ACTN,
												familyPlanReqrdActn.getTxtReqrdActn());

										// TFF Evidence Based Services - START
										if(Objects.nonNull(familyPlanReqrdActn.getIndTffService())) {
											List<FormDataGroupDto> tffEvidenceFormGroupList = new ArrayList<FormDataGroupDto>();
											FormDataGroupDto tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto = createFormDataGroup(
													FormGroupsConstants.TMPLAT_EVIDENCE_BASED_SVCS,
													FormGroupsConstants.TMPLAT_REQ_ACTN);
											List<BookmarkDto> bookmarkFamilyPlanReqdActnTffEvidenceSvcsList = new ArrayList<BookmarkDto>();
											BookmarkDto bookmarkIsTffEvidenceSvc = createBookmark(BookmarkConstants.IS_TFF_EVDNC_SVC,
													familyPlanReqrdActn.getIndTffService());
											BookmarkDto bookmarkTffEvidenceBasedSvcs = createBookmark(BookmarkConstants.TFF_EVDNC_BASED_SVCS,
													getTffEvidenceBasedServices(familyPlanReqrdActn));

											bookmarkFamilyPlanReqdActnTffEvidenceSvcsList.add(bookmarkIsTffEvidenceSvc);
											bookmarkFamilyPlanReqdActnTffEvidenceSvcsList.add(bookmarkTffEvidenceBasedSvcs);
											tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanReqdActnTffEvidenceSvcsList);
											tffEvidenceFormGroupList.add(tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto);
											tempFamilyPlanReqdActnFrmDataGrpDto.setFormDataGroupList(tffEvidenceFormGroupList);
										}
										// TFF Evidence Based Services - END
										BookmarkDto courtOrderedBookmark = new BookmarkDto();
										if (FormConstants.Y.equalsIgnoreCase(familyPlanReqrdActn.getIndCourtOrdrd())) {
											courtOrderedBookmark = createBookmark(BookmarkConstants.COURT_ORDERED, FormConstants.YES);
										} else {
											courtOrderedBookmark = createBookmark(BookmarkConstants.COURT_ORDERED, FormConstants.NO);
										}

										BookmarkDto bookmarkTargetDt = createBookmark(BookmarkConstants.TARGET_DT,
												TypeConvUtil.formDateFormat(familyPlanReqrdActn.getDtTrgtCmpltn()));
										BookmarkDto bookmarkPriorityStatus = createBookmark(
												BookmarkConstants.PRIORITY_STATUS, lookupDao.decode(
														ServiceConstants.CFPRQACP, familyPlanReqrdActn.getCdPriorty()));
										
										BookmarkDto bookmarkTxtCompliance = createBookmark(
												BookmarkConstants.UE_GROUPID, 
														 reqrdActnSection);										
										bookmarkFamilyPlanReqdActn.add(bookmarkReqdActn);
										bookmarkFamilyPlanReqdActn.add(courtOrderedBookmark);
										bookmarkFamilyPlanReqdActn.add(bookmarkTargetDt);
										bookmarkFamilyPlanReqdActn.add(bookmarkPriorityStatus);										
										bookmarkFamilyPlanReqdActn.add(bookmarkTxtCompliance);
										tempFamilyPlanReqdActnFrmDataGrpDto
												.setBookmarkDtoList(bookmarkFamilyPlanReqdActn);
										ReqdActnList.add(tempFamilyPlanReqdActnFrmDataGrpDto);

									}
									tempFamilyPlanPartcpntFrmDataGrpDto.setFormDataGroupList(ReqdActnList);
									FamilyPlanList.add(tempFamilyPlanPartcpntFrmDataGrpDto);
									
								}

							}

					}

						tempFamilyPlanFrmDataGrpDto.setFormDataGroupList(FamilyPlanList);
						formDataGroupList.add(tempFamilyPlanFrmDataGrpDto);

					
				}
				}
			}
	
		//}

		/**
		 * End - Logic for the New Family Plan Section Population
		 * -----------------------------------------------------------
		 */

		/**
		 * Start - Logic for the New Family Plan Evaluation Section Population
		 * -----------------------------------------------------------
		 */

		// Populate the FAMILY PLAN REQUIRED ACTIONS for New Family Plan
		// Evaluations

		if (CollectionUtils.isNotEmpty(cpsMonthlyEvalDto.getFamilyPlanEval())) {
		
			for (FamilyPlanEval familyPlanEval : cpsMonthlyEvalDto.getFamilyPlanEval()) {
				if (CollectionUtils.isNotEmpty(familyPlanEval.getFamilyPlanNeeds())
						&& CollectionUtils.isNotEmpty(familyPlanEval.getFamilyPlanPartcpnts())) {

					if (familyPlanEval.getDtCompleted().before(dtlastdayofEval)) {

						isFamilyPlanExists = ServiceConstants.TRUEVAL;
						familyPlanSection = familyPlanSection + ServiceConstants.One_Integer;
						List<FormDataGroupDto> FamilyPlanList = new ArrayList<FormDataGroupDto>();
						FormDataGroupDto tempFamilyPlanFrmDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_FAMILY_PLAN, FormConstants.EMPTY_STRING);
						List<BookmarkDto> bookmarkFamilyPlanList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkFamilyPlanDtCompleted = createBookmark(
								BookmarkConstants.DT_FAMLIY_PLAN,
								TypeConvUtil.formDateFormat(familyPlanEval.getDtCompleted()));
						String familyPlanPartcpnts = familyPlanEval
								.getFamilyPlanPartcpnts().stream()
								.filter(participant -> participant
										.getIndPartcpntType().equals(ServiceConstants.PRIMARY_PARTICIPANT))
								.map(d -> personUtil.getPersonFullName(d.getIdPerson()))
								.collect(Collectors.joining(", "));
						BookmarkDto bookmarkFamilyPlanPrimaryParticipant = createBookmark(
								BookmarkConstants.NM_FAMILY_PLAN, "Family Plan Evaluation - "
										+ familyPlanPartcpnts);
						BookmarkDto bookmark1 = createBookmark(
								BookmarkConstants.UE_GROUPID,
								familyPlanSection);
						
						bookmarkFamilyPlanList.add(bookmark1);
						bookmarkFamilyPlanList.add(bookmarkFamilyPlanDtCompleted);
						bookmarkFamilyPlanList.add(bookmarkFamilyPlanPrimaryParticipant);
						tempFamilyPlanFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanList);
						// Loop through the Family Plan Participant and Family
						// Plan Needs
						int participant=0;								
				
						for (FamilyPlanNeed familyplanneed : familyPlanEval.getFamilyPlanNeeds()) {
							participant=participant+ ServiceConstants.One_Integer;
							
							for (FamilyPlanPartcpnt familyPlanPartcpnt : familyPlanEval.getFamilyPlanPartcpnts()) {
								if (familyplanneed.getIdPerson().equals(familyPlanPartcpnt.getIdPerson())) { 

									// Populate the Family Plan Participants
									FormDataGroupDto tempFamilyPlanPartcpntFrmDataGrpDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_PARTICIPANT,
											FormGroupsConstants.TMPLAT_FAMILY_PLAN);
									List<BookmarkDto> bookmarkFamilyPlanParticpnt = new ArrayList<BookmarkDto>();
									BookmarkDto bookmarkFamilyPlanPartcpntName = createBookmark(
											BookmarkConstants.PARTICIPANT_NAME,
											personUtil.getPersonFullName(familyPlanPartcpnt.getIdPerson()));
									BookmarkDto bookmarkTxt1 = createBookmark(
											BookmarkConstants.UE_GROUPID, 
											participant);	
									bookmarkFamilyPlanParticpnt.add(bookmarkTxt1);
									bookmarkFamilyPlanParticpnt.add(bookmarkFamilyPlanPartcpntName);
									tempFamilyPlanPartcpntFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanParticpnt);
									

									// Populate the Required Action Fields
									Integer reqrdActnSection = 0;
									List<FormDataGroupDto> ReqdActnList = new ArrayList<FormDataGroupDto>();
									
									for (FamilyPlanReqrdActn familyPlanReqrdActn : familyplanneed.getFamilyPlanReqrdActns()) {
										reqrdActnSection = reqrdActnSection + ServiceConstants.One_Integer;
										FormDataGroupDto tempFamilyPlanReqdActnFrmDataGrpDto = createFormDataGroup(
												FormGroupsConstants.TMPLAT_REQ_ACTN,
												FormGroupsConstants.TMPLAT_PARTICIPANT);
										List<BookmarkDto> bookmarkFamilyPlanReqdActn = new ArrayList<BookmarkDto>();
										BookmarkDto bookmarkReqdActn = createBookmark(BookmarkConstants.REQ_ACTN,
												familyPlanReqrdActn.getTxtReqrdActn());
										// TFF Evidence Based Services - START
										if(Objects.nonNull(familyPlanReqrdActn.getIndTffService())) {
											List<FormDataGroupDto> tffEvidenceFormGroupList = new ArrayList<FormDataGroupDto>();
											FormDataGroupDto tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto = createFormDataGroup(
													FormGroupsConstants.TMPLAT_EVIDENCE_BASED_SVCS,
													FormGroupsConstants.TMPLAT_REQ_ACTN);
											List<BookmarkDto> bookmarkFamilyPlanReqdActnTffEvidenceSvcsList = new ArrayList<BookmarkDto>();
											BookmarkDto bookmarkIsTffEvidenceSvc = createBookmark(BookmarkConstants.IS_TFF_EVDNC_SVC,
													familyPlanReqrdActn.getIndTffService());
											BookmarkDto bookmarkTffEvidenceBasedSvcs = createBookmark(BookmarkConstants.TFF_EVDNC_BASED_SVCS,
													getTffEvidenceBasedServices(familyPlanReqrdActn));

											bookmarkFamilyPlanReqdActnTffEvidenceSvcsList.add(bookmarkIsTffEvidenceSvc);
											bookmarkFamilyPlanReqdActnTffEvidenceSvcsList.add(bookmarkTffEvidenceBasedSvcs);
											tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanReqdActnTffEvidenceSvcsList);
											tffEvidenceFormGroupList.add(tempFamilyPlanReqdActnTFFEvidenceSvcsFrmDataGrpDto);
											tempFamilyPlanReqdActnFrmDataGrpDto.setFormDataGroupList(tffEvidenceFormGroupList);
										}
										// TFF Evidence Based Services - END
										BookmarkDto courtOrderedBookmark = new BookmarkDto();
										if (FormConstants.Y.equalsIgnoreCase(familyPlanReqrdActn.getIndCourtOrdrd())) {
											courtOrderedBookmark = createBookmark(BookmarkConstants.COURT_ORDERED, FormConstants.YES);
										} else {
											courtOrderedBookmark = createBookmark(BookmarkConstants.COURT_ORDERED, FormConstants.NO);
										}

										BookmarkDto bookmarkTargetDt = createBookmark(BookmarkConstants.TARGET_DT,
												TypeConvUtil.formDateFormat(familyPlanReqrdActn.getDtTrgtCmpltn()));
										BookmarkDto bookmarkPriorityStatus = createBookmark(
												BookmarkConstants.PRIORITY_STATUS, lookupDao.decode(
														ServiceConstants.CFPRQACP, familyPlanReqrdActn.getCdPriorty()));
										
										BookmarkDto bookmarkTxtCompliance = createBookmark(
												BookmarkConstants.UE_GROUPID, 
													reqrdActnSection);										
										bookmarkFamilyPlanReqdActn.add(bookmarkReqdActn);
										bookmarkFamilyPlanReqdActn.add(courtOrderedBookmark);
										bookmarkFamilyPlanReqdActn.add(bookmarkTargetDt);
										bookmarkFamilyPlanReqdActn.add(bookmarkPriorityStatus);										
										bookmarkFamilyPlanReqdActn.add(bookmarkTxtCompliance);										
										tempFamilyPlanReqdActnFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanReqdActn);
										ReqdActnList.add(tempFamilyPlanReqdActnFrmDataGrpDto);

									}
									
									tempFamilyPlanPartcpntFrmDataGrpDto.setFormDataGroupList(ReqdActnList);
									FamilyPlanList.add(tempFamilyPlanPartcpntFrmDataGrpDto);
								}

							}

						}

						tempFamilyPlanFrmDataGrpDto.setFormDataGroupList(FamilyPlanList);
						formDataGroupList.add(tempFamilyPlanFrmDataGrpDto);

					}
				}
			}
		}

		/**
		 * End - Logic for the New Family Plan Evaluation Section Population
		 * -----------------------------------------------------------
		 */

		/**
		 * Start - Logic for the Legacy Family Plan/Family Plan Evaluation
		 * Section Population
		 * -----------------------------------------------------------
		 */

		// Populate the FAMILY PLAN REQUIRED ACTIONS for Legacy Family Plan
		// Evaluations/Family Plan

		for (FamilyPlanDto familyPlanDto : cpsMonthlyEvalDto.getLegacyFamilyPlan()) {
			if (CollectionUtils.isNotEmpty(familyPlanDto.getPrincipalParticipantList())) {

				if ((!ObjectUtils.isEmpty(familyPlanDto.getDtCompleted()) &&
						familyPlanDto.getDtCompleted().before(dtlastdayofEval))
						&& ObjectUtils.isEmpty(familyPlanDto.getLegacyFamilyPlan())
						&& ObjectUtils.isEmpty(familyPlanDto.getMessageByEval())
						&& ObjectUtils.isEmpty(familyPlanDto.getMessageByEval())) {
					isFamilyPlanExists = true;
					familyPlanSection = familyPlanSection + ServiceConstants.One_Integer;
					List<FormDataGroupDto> FamilyPlanList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto tempFamilyPlanFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_FAMILY_PLAN, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkFamilyPlanList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkFamilyPlanDtCompleted = createBookmark(BookmarkConstants.DT_FAMLIY_PLAN,
							TypeConvUtil.formDateFormat(familyPlanDto.getDtCompleted()));
					BookmarkDto bookmarkFamilyPlanPrimaryParticipant = null;
					if (familyPlanDto.isIndFamilyPlan() == (ServiceConstants.TRUE_VALUE)) {
						bookmarkFamilyPlanPrimaryParticipant = createBookmark(BookmarkConstants.NM_FAMILY_PLAN,
								"Family Plan ");
					} else {
						bookmarkFamilyPlanPrimaryParticipant = createBookmark(BookmarkConstants.NM_FAMILY_PLAN,
								"Family Plan Evaluation ");
					}
                
					BookmarkDto bookmarkTxtProgressInfo = createBookmark(BookmarkConstants.UE_GROUPID,
							familyPlanSection);							
					bookmarkFamilyPlanList.add(bookmarkTxtProgressInfo);					
					bookmarkFamilyPlanList.add(bookmarkFamilyPlanDtCompleted);
					bookmarkFamilyPlanList.add(bookmarkFamilyPlanPrimaryParticipant);
					tempFamilyPlanFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanList);

					Integer regrdActnSection = 0;
					// Loop through the Family Plan Participant and Family Plan
					// Needs
					for (PrincipalParticipantDto principalParticipantDto : familyPlanDto
							.getPrincipalParticipantList()) {
						if (!(ObjectUtils.isEmpty(principalParticipantDto.getIndParticipant()))
								&& principalParticipantDto.getIndParticipant() == ServiceConstants.Y) {
							regrdActnSection = regrdActnSection + ServiceConstants.One_Integer;

							// Populate the Family Plan Participants
							FormDataGroupDto tempFamilyPlanPartcpntFrmDataGrpDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_PARTICIPANT, FormGroupsConstants.TMPLAT_FAMILY_PLAN);
							List<BookmarkDto> bookmarkFamilyPlanParticpnt = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkFamilyPlanPartcpntName = createBookmark(
									BookmarkConstants.PARTICIPANT_NAME, principalParticipantDto.getTxtPersonName());
							bookmarkFamilyPlanParticpnt.add(bookmarkFamilyPlanPartcpntName);
							BookmarkDto bookmarkTxt1 = createBookmark(
									BookmarkConstants.UE_GROUPID, 
									regrdActnSection);
							bookmarkFamilyPlanParticpnt.add(bookmarkTxt1);
							bookmarkFamilyPlanParticpnt.add(bookmarkFamilyPlanPartcpntName);
							tempFamilyPlanPartcpntFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanParticpnt);

							// Populate the Required Action Fields
							List<FormDataGroupDto> ReqdActnList = new ArrayList<FormDataGroupDto>();
							FormDataGroupDto tempFamilyPlanReqdActnFrmDataGrpDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_REQ_ACTN, FormGroupsConstants.TMPLAT_PARTICIPANT);
							List<BookmarkDto> bookmarkFamilyPlanReqdActn = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkReqdActn = createBookmark(BookmarkConstants.REQ_ACTN,
									ServiceConstants.EMPTY_LINE);
							BookmarkDto bookmarkTargetDt = createBookmark(BookmarkConstants.TARGET_DT,
									ServiceConstants.SPACE);
							BookmarkDto bookmarkPriorityStatus = createBookmark(BookmarkConstants.PRIORITY_STATUS,
									ServiceConstants.SPACE);
							
							BookmarkDto bookmarkTxtCompliance = createBookmark(BookmarkConstants.UE_GROUPID,
									regrdActnSection);							
							bookmarkFamilyPlanReqdActn.add(bookmarkReqdActn);
							bookmarkFamilyPlanReqdActn.add(bookmarkTargetDt);
							bookmarkFamilyPlanReqdActn.add(bookmarkPriorityStatus);							
							bookmarkFamilyPlanReqdActn.add(bookmarkTxtCompliance);							
							tempFamilyPlanReqdActnFrmDataGrpDto.setBookmarkDtoList(bookmarkFamilyPlanReqdActn);
							ReqdActnList.add(tempFamilyPlanReqdActnFrmDataGrpDto);
							tempFamilyPlanPartcpntFrmDataGrpDto.setFormDataGroupList(ReqdActnList);
							FamilyPlanList.add(tempFamilyPlanPartcpntFrmDataGrpDto);

						}

					}

					tempFamilyPlanFrmDataGrpDto.setFormDataGroupList(FamilyPlanList);
					formDataGroupList.add(tempFamilyPlanFrmDataGrpDto);
				}
			}

		}

		/**
		 * End - Logic for the Legacy Family Plan/Family Plan Evaluation Section
		 * Population
		 * -----------------------------------------------------------
		 */

		if (!isFamilyPlanExists) {
			// Display "No Records Exists" and Additional Info Text Box when no
			// data is available for Family Plan Section
			BookmarkDto bookmarkFsnaAddInfo = createBookmark(BookmarkConstants.NO_FAMILY_PLAN,
					 NO_RECORDS_EXISTS_MSG);
			bookmarkNonFrmGrpList.add(bookmarkFsnaAddInfo);
		}

		/**
		 * Start - Logic for the SDM Safety Assessment Section Population
		 * -----------------------------------------------------------
		 */

		Boolean isSafetyAssessmentExists = ServiceConstants.FALSEVAL;

		// Populate the SDM Safety Assessment Section
		if (CollectionUtils.isNotEmpty(cpsMonthlyEvalDto.getSafetyAssmnt())) {
			HashMap<Long, SDMSafetyAssessmentDto> assessmentsListed = new HashMap<Long, SDMSafetyAssessmentDto>();
			Long riskAssessmentStageId = null;
			for (SDMSafetyAssessmentDto safetyAssessmentDto : cpsMonthlyEvalDto.getSafetyAssmnt()) {

				if (safetyAssessmentDto.getDtSafetyAssessed().before(dtlastdayofEval)) {
					//ALM ID : 13182 Safety Assessements can be from all prior stages, 
					// use assessments only from most recent qualified safety assessment stage.
					// only need one assessment per house
					if(assessmentsListed.containsKey(safetyAssessmentDto.getIdHouseHoldPerson())
						|| (riskAssessmentStageId != null && !riskAssessmentStageId.equals(safetyAssessmentDto.getIdStage()))) {
						continue;
					}
					assessmentsListed.put(safetyAssessmentDto.getIdHouseHoldPerson(), safetyAssessmentDto);
					// this is the most recent qualified safety assessment.
					if(riskAssessmentStageId == null) {
						riskAssessmentStageId = safetyAssessmentDto.getIdStage();
					}
					isSafetyAssessmentExists = ServiceConstants.TRUEVAL;

					FormDataGroupDto tempsafetyFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SAFETY_ASSMNT, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkSafetyList = new ArrayList<BookmarkDto>();
					if (!ObjectUtils.isEmpty(safetyAssessmentDto.getIdHouseHoldPerson())) {
						BookmarkDto bookmarkSafetyHouseHoldNm = createBookmark(BookmarkConstants.SAFETY_HOUSEHOLD_NAME,
								personUtil.getPersonFullName(safetyAssessmentDto.getIdHouseHoldPerson().longValue()));
						bookmarkSafetyList.add(bookmarkSafetyHouseHoldNm);
					}
					StageValueBeanDto stageValueBeanDto = stageUtilityDao
							.retrieveStageInfo(safetyAssessmentDto.getIdStage());
					BookmarkDto bookmarkSafetyStage = createBookmark(BookmarkConstants.SAFETY_STAGE,
							stageValueBeanDto.getNmStage());
					BookmarkDto bookmarkSafetyDtCompleted = createBookmark(BookmarkConstants.SAFETY_DT_COMPLETED,
							TypeConvUtil.formDateFormat(safetyAssessmentDto.getDtSafetyAssessed()));
					//artf241661 - Removing the word "SDM" from Safety Assessment section in the 'CPS Monthly Evaluation Assessment' Report
					BookmarkDto bookmarkSafetyDesc = createBookmark(BookmarkConstants.SAFETY_DESC,
							"Safety Assessment - " + lookupDao.decode(ServiceConstants.CSDMASMT,
									safetyAssessmentDto.getAssessmentType()));
					String individualAssessed = ServiceConstants.SPACE;
					if (CollectionUtils.isNotEmpty(safetyAssessmentDto.getSavedPersonAssessed())) {

						for (Integer personid : safetyAssessmentDto.getSavedPersonAssessed()) {
							if (safetyAssessmentDto.getSavedPersonAssessed().size() == ServiceConstants.Zero_INT) {
								individualAssessed = personUtil.getPersonFullName(personid.longValue())
										+ ServiceConstants.SPACE_SEMI_COLON_SPACE + individualAssessed;
								break;
							} else {
								individualAssessed = personUtil.getPersonFullName(personid.longValue())
										+ ServiceConstants.SPACE_SEMI_COLON_SPACE + individualAssessed;
							}

						}

					}

					if (!individualAssessed.equals(ServiceConstants.SPACE)) {
						BookmarkDto bookmarkSafetyPersonAssessed = createBookmark(
								BookmarkConstants.SAFETY_PERSON_ASSESSED,
								individualAssessed.substring(ServiceConstants.Zero, individualAssessed.length() - 3));
						bookmarkSafetyList.add(bookmarkSafetyPersonAssessed);
					}
					BookmarkDto bookmarkSafetyDecision = createBookmark(BookmarkConstants.SAFETY_DECISION, lookupDao
							.decode(ServiceConstants.CSDMDCSN, safetyAssessmentDto.getCdSavedSafetyDecision()));
					bookmarkSafetyList.add(bookmarkSafetyStage);
					bookmarkSafetyList.add(bookmarkSafetyDtCompleted);
					bookmarkSafetyList.add(bookmarkSafetyDesc);
					bookmarkSafetyList.add(bookmarkSafetyDecision);
					tempsafetyFrmDataGrpDto.setBookmarkDtoList(bookmarkSafetyList);
					formDataGroupList.add(tempsafetyFrmDataGrpDto);
				}
			}
		}

		if (isSafetyAssessmentExists) {
			// Display the Table Header only when the Safety Assessment section
			// has value
			FormDataGroupDto tempSafetyAssmntHdrFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SAFETY_ASSMNT_HDR, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempSafetyAssmntHdrFrmDataGrpDto);
		} else {
			// Display "No Records Exists" and Additional Info Text Box when no
			// data is available for Safety Assessment Section
			FormDataGroupDto tempSafetyAssmntFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_NO_SAFETY_ASSMNT, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempSafetyAssmntFrmDataGrpDto);
		}

		/**
		 * End - Logic for the SDM Safety Assessment Section Population
		 * -----------------------------------------------------------
		 */

		/**
		 * Start - Logic for the SDM Risk Re-Assessment Section Population
		 * -----------------------------------------------------------
		 */

		Boolean isRiskReAssessmentExists = ServiceConstants.FALSEVAL;

		// Populate the SDM Risk Re Assessment Section
		if (CollectionUtils.isNotEmpty(cpsMonthlyEvalDto.getRiskReassessment())) {
			HashMap<Long, SDMRiskReassessmentRes> assessmentsListed = new HashMap<Long, SDMRiskReassessmentRes>();
			Long riskAssessmentStageId = null;
			for (SDMRiskReassessmentRes riskReassessmentRes : cpsMonthlyEvalDto.getRiskReassessment()) {

				StageValueBeanDto stageValueBeanDto = stageUtilityDao
						.retrieveStageInfo(riskReassessmentRes.getSdmRiskReasmntDto().getIdStage());
				// Check if the Event Status is 'APRV'
				//ALM ID : 14367 Allow A-R stage Risk Assessments that are in 'COMP' status
				if ((riskReassessmentRes.getSdmRiskReasmntDto().getCdEventStatus().equals(ServiceConstants.EVENTSTATUS_APPROVE)
						|| (ServiceConstants.A_R_STAGE.equals(stageValueBeanDto.getCdStage()) 
								&& ServiceConstants.EVENTSTATUS_COMPLETE.equals(riskReassessmentRes.getSdmRiskReasmntDto().getCdEventStatus())))
						&& riskReassessmentRes.getSdmRiskReasmntDto().getDtAsmnt().before(dtlastdayofEval)) {
					
					//ALM ID : 13182 Risk Assessments can be from all prior stages, 
					// use Risk Assessments/ReAssessments only from most recent qualified Risk Assessment/Reassessment Stage.
					if(assessmentsListed.containsKey(riskReassessmentRes.getSdmRiskReasmntDto().getIdHshldAssessed()) 
						|| (riskAssessmentStageId != null && !riskAssessmentStageId.equals(riskReassessmentRes.getSdmRiskReasmntDto().getIdStage()))) {
						continue;
					}
					assessmentsListed.put(riskReassessmentRes.getSdmRiskReasmntDto().getIdHshldAssessed(), riskReassessmentRes);
					//this is the most recent qualified Risk Assessment/Reassessment.
					if(riskAssessmentStageId == null) {
						riskAssessmentStageId = riskReassessmentRes.getSdmRiskReasmntDto().getIdStage();
					}
					isRiskReAssessmentExists = true;
					FormDataGroupDto tempRiskReAssmntFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_RISK_ASSMNT, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkRiskReAssmntList = new ArrayList<BookmarkDto>();
					if (!ObjectUtils.isEmpty(riskReassessmentRes.getSdmRiskReasmntDto().getIdHshldAssessed())) {
						BookmarkDto bookmarkRiskHoldNm = createBookmark(BookmarkConstants.RISK_HOUSEHOLD_NAME,
								personUtil.getPersonFullName(
										riskReassessmentRes.getSdmRiskReasmntDto().getIdHshldAssessed()));
						bookmarkRiskReAssmntList.add(bookmarkRiskHoldNm);
					}
					BookmarkDto bookmarkSRiskReAssmtafetyStage = createBookmark(BookmarkConstants.RISK_STAGE,
							stageValueBeanDto.getNmStage());
					BookmarkDto bookmarkRiskReAssmntDtCompleted = createBookmark(BookmarkConstants.RISK_DT_APPROVED,
							TypeConvUtil.formDateFormat(riskReassessmentRes.getSdmRiskReasmntDto().getDtAsmnt()));
					//ALM 13182: Risk Reassessments are defined only in FPR stage, using stage id to change the description.
					BookmarkDto bookmarkRiskDesc = null;
					//artf241661 - Removing the word "SDM" from Risk Assessment section in the 'CPS Monthly Evaluation Assessment' Report
					if(cpsMonthlyEvalDto.getIdStage() == riskReassessmentRes.getSdmRiskReasmntDto().getIdStage())
						bookmarkRiskDesc = createBookmark(BookmarkConstants.RISK_DESC, "Risk Reassessment");
					else 
						bookmarkRiskDesc = createBookmark(BookmarkConstants.RISK_DESC, "Risk Assessment");
					String individualAssessed = ServiceConstants.SPACE;

					if (CollectionUtils.isNotEmpty(riskReassessmentRes.getSdmRiskReasmntDto().getHouseholdMembers())) {

						for (Long personid : riskReassessmentRes.getSdmRiskReasmntDto().getHouseholdMembers()) {
							if (riskReassessmentRes.getSdmRiskReasmntDto().getHouseholdMembers()
									.size() == ServiceConstants.Zero_INT) {
								individualAssessed = personUtil.getPersonFullName(personid)
										+ ServiceConstants.SPACE_SEMI_COLON_SPACE + individualAssessed;
								break;
							} else {
								individualAssessed = personUtil.getPersonFullName(personid)
										+ ServiceConstants.SPACE_SEMI_COLON_SPACE + individualAssessed;
							}

						}
						if (!individualAssessed.equals(ServiceConstants.SPACE) && individualAssessed.length() > 3) {
							individualAssessed = individualAssessed.substring(ServiceConstants.Zero, individualAssessed.length() - 3);
						}
					}
					
					BookmarkDto bookmarkSafetyPersonAssessed = createBookmark(BookmarkConstants.RISK_PERSON_ASSESSED, individualAssessed);

					BookmarkDto bookmarkRiskReAssmntScore = createBookmark(BookmarkConstants.RISK_TOTAL_SCORE,
							riskReassessmentRes.getSdmRiskReasmntDto().getRiskScore());
					BookmarkDto bookmarkRiskReAssmntOverride = createBookmark(BookmarkConstants.RISK_OVERRIDES,
							lookupDao.decode(ServiceConstants.CDOVRIDE,
									riskReassessmentRes.getSdmRiskReasmntDto().getCdOverride()));
					BookmarkDto bookmarkRiskReAssmntLevel = createBookmark(BookmarkConstants.RISK_LEVEL,
							lookupDao.decode(ServiceConstants.CSDMRLVL,
									riskReassessmentRes.getSdmRiskReasmntDto().getCdFinalRiskLevel()));
					BookmarkDto bookmarkRiskReAssmntRecommendation = createBookmark(
							BookmarkConstants.RISK_RECOMMENDATION,
							riskReassessmentRes.getSdmRiskReasmntDto().getTxtRecommendation());
					bookmarkRiskReAssmntList.add(bookmarkSRiskReAssmtafetyStage);
					bookmarkRiskReAssmntList.add(bookmarkRiskReAssmntDtCompleted);
					bookmarkRiskReAssmntList.add(bookmarkRiskDesc);
					if (!individualAssessed.equals(ServiceConstants.SPACE)) {
						bookmarkRiskReAssmntList.add(bookmarkSafetyPersonAssessed);
					}
					bookmarkRiskReAssmntList.add(bookmarkRiskReAssmntScore);
					bookmarkRiskReAssmntList.add(bookmarkRiskReAssmntOverride);
					bookmarkRiskReAssmntList.add(bookmarkRiskReAssmntLevel);
					bookmarkRiskReAssmntList.add(bookmarkRiskReAssmntRecommendation);
					tempRiskReAssmntFrmDataGrpDto.setBookmarkDtoList(bookmarkRiskReAssmntList);
					formDataGroupList.add(tempRiskReAssmntFrmDataGrpDto);
				}
			}
		}
		if (isRiskReAssessmentExists) {
			// Display the Table Header only when the Risk Re-Assessment section
			// has value
			FormDataGroupDto tempRiskReAssmntHdrFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_RISK_ASSMNT_HDR, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempRiskReAssmntHdrFrmDataGrpDto);
		} else {
			// Display "No Records Exists" and Additional Info Text Box when no
			// data is available for Risk Re-Assessment Section
			FormDataGroupDto tempNoRiskReAssmntFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_NO_RISK_ASSMNT, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempNoRiskReAssmntFrmDataGrpDto);
		}

		/**
		 * End - Logic for the SDM Risk Re-Assessment Section Population
		 * -----------------------------------------------------------
		 */

		/**
		 * Populating the non form group data into prefill data
		 */

		
		BookmarkDto bookmarkCaseName = createBookmark(BookmarkConstants.CASE_NAME, cpsMonthlyEvalDto.getNmCase());
		BookmarkDto bookmarkDtFprOpen = createBookmark(BookmarkConstants.DT_FPR_OPEN,
				TypeConvUtil.formDateFormat(cpsMonthlyEvalDto.getDtStageOpened()));
		BookmarkDto bookmarkWorkerNameMiddle = createBookmark(BookmarkConstants.CASE_WORKER_NAME,
				cpsMonthlyEvalDto.getNmCaseWorker().replace(FormConstants.PLUS, FormConstants.SPACE));
		BookmarkDto bookmarkDtEval = createBookmark(BookmarkConstants.DT_EVAL, cpsMonthlyEvalDto.getDtEvaluation());
		bookmarkNonFrmGrpList.add(bookmarkCaseName);
		bookmarkNonFrmGrpList.add(bookmarkDtFprOpen);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameMiddle);
		bookmarkNonFrmGrpList.add(bookmarkDtEval);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}
	
    private Date getDate(Date date) {
        try
        {
                       SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                       return formatter.parse(formatter.format(date));
        } catch (Exception e) {
                       // TODO: handle exception
        }
        return date;
}

	
}
