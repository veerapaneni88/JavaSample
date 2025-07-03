package us.tx.state.dfps.service.forms.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.arinvconclusion.dto.ArEaEligibilityDto;
import us.tx.state.dfps.arinvconclusion.dto.ArInvCnclsnDto;
import us.tx.state.dfps.arinvconclusion.dto.PCSPDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtAreaValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtFactorValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.arreport.dto.ArPrincipalsHistoryDto;
import us.tx.state.dfps.service.arreport.dto.ArRelationshipsDto;
import us.tx.state.dfps.service.arreport.dto.ArReportDto;
import us.tx.state.dfps.service.arreport.dto.ArServiceReferralsDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvComDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvContactSdmSafetyAssessDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvCrimHistDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvIntakePersonPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvSdmSafetyRiskDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.person.dto.SupervisorDto;
import us.tx.state.dfps.service.riskassesment.dto.SdmSafetyRiskAssessmentsDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

import org.apache.log4j.Logger;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Generates
 * prefill string used to populate form Apr 5, 2018- 2:52:12 PM © 2017 Texas
 * Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 06/10/2020 thompswa artf152702 : CPI June 2020 Project - refactor emergency assist
 * 01/03/2022 thompswa artf211437 : AR Report Inconsistency - p2 report is NOT showing the merged intake.
 */
@Component
public class ArReportPrefillData extends DocumentServiceUtil {



	@Autowired
	LookupDao lookupDao;

	@Autowired
	private CodesDao codesDao;
	
	private static final String SDM_HSHLDLABEL_TEXT = "Household:";
	private static final String SDM_HSHLDLABEL_NONE = "NONE:";
	private static final String SDM_HSHLD_ASMT_TYPERISK_TEXT = "Risk";
	private static final String SDM_HSHLD_ASMT_TYPESAFE_TEXT = "Safety - ";
	private static final String EADETERMINATIONTEXT = "Emergency Assistance eligibility does not need to be determined since risk is not indicated.";
	private static final String CSDMRLVL_HIGH = "HIGH";
	private static final String CSDMRLVL_VERYHIGH = "VERYHIGH";
	private static final String EA_ELIGIBLETEXT = "The caseworker is applying for Emergency Assistance on behalf of a child.";
	private static final Logger logger = Logger.getLogger(ArReportPrefillData.class);

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		ArReportDto prefillDto = (ArReportDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonGroupList = new ArrayList<BookmarkDto>();
		List<BlobDataDto> blobList = new ArrayList<BlobDataDto>();
		// Initialize null DTOs
		if (ObjectUtils.isEmpty(prefillDto.getContactList())) {
			prefillDto.setContactList(new ArrayList<ContactDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
			prefillDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getIntStageRtrvOutDto())) {
			prefillDto.setIntStageRtrvOutDto(new StageRtrvOutDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getInvStageRtrvOutDto())) {
			prefillDto.setInvStageRtrvOutDto(new StageRtrvOutDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getStagePersonDto())) {
			prefillDto.setStagePersonDto(new StagePersonDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getSupervisorDto())) {
			prefillDto.setSupervisorDto(new SupervisorDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getEmployeePersPhNameDto())) {
			prefillDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getReporterDto())) {
			prefillDto.setReporterDto(new CpsInvIntakePersonPrincipalDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getAllegationDetailList())) {
			prefillDto.setAllegationDetailList(new ArrayList<AllegationDetailDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getCharacteristicsList())) {
			prefillDto.setCharacteristicsList(new ArrayList<CharacteristicsDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPrincipalsList())) {
			prefillDto.setPrincipalsList(new ArrayList<CpsInvPrincipalDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getCollateralList())) {
			prefillDto.setCollateralList(new ArrayList<CpsInvIntakePersonPrincipalDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getHistAllegList())) {
			prefillDto.setHistAllegList(new ArrayList<FacilityAllegationInfoDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getHistPrincipalsList())) {
			prefillDto.setHistPrincipalsList(new ArrayList<ArPrincipalsHistoryDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getHistRemovalsList())) {
			prefillDto.setHistRemovalsList(new ArrayList<CpsInvComDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getCriminalHistList())) {
			prefillDto.setCriminalHistList(new ArrayList<CpsInvCrimHistDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getInitialSafetyAssmt())) {
			prefillDto.setInitialSafetyAssmt(new ARSafetyAssmtValueDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getClosureSafetyAssmt())) {
			prefillDto.setClosureSafetyAssmt(new ARSafetyAssmtValueDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getIntialEventDto())) {
			prefillDto.setIntialEventDto(new EventDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getClosureEventDto())) {
			prefillDto.setClosureEventDto(new EventDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getServRefList())) {
			prefillDto.setServRefList(new ArrayList<ArServiceReferralsDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getPcspList())) {
			prefillDto.setPcspList(new ArrayList<PCSPDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getArInvConcDto())) {
			prefillDto.setArInvConcDto(new ArInvCnclsnDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getContactNamesList())) {
			prefillDto.setContactNamesList(new ArrayList<CpsInvComDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getRelationshipList())) {
			prefillDto.setRelationshipList(new ArrayList<ArRelationshipsDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getGetIntakesList())) {
			prefillDto.setGetIntakesList(new ArrayList<CpsInvIntakePersonPrincipalDto>());
		}  // artf211437

		// Populate Initial Summary section
		BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				prefillDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonGroupList.add(bookmarkTitleCaseName);
		BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				prefillDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonGroupList.add(bookmarkTitleCaseNumber);
		BookmarkDto bookmarkHeaderDate = createBookmark(BookmarkConstants.HEADER_DATE_INTAKE_RECEIVED,
				DateUtils.stringDt(prefillDto.getGenericCaseInfoDto().getDtStageStart()));
		bookmarkNonGroupList.add(bookmarkHeaderDate);
		BookmarkDto bookmarkNmCaseworker = createBookmark(BookmarkConstants.CASEWORKER_FULL_NAME,
				prefillDto.getStagePersonDto().getNmPersonFull());
		bookmarkNonGroupList.add(bookmarkNmCaseworker);
		BookmarkDto bookmarkDtArInitiated = createBookmark(BookmarkConstants.DATE_AR_INITIATED,
				DateUtils.stringDt(prefillDto.getInvStageRtrvOutDto().getDtStageStart()));
		bookmarkNonGroupList.add(bookmarkDtArInitiated);
		BookmarkDto bookmarkNmSupervisor = createBookmark(BookmarkConstants.SUPERVISOR_FULL_NM,
				prefillDto.getSupervisorDto().getNmPersonFull());
		bookmarkNonGroupList.add(bookmarkNmSupervisor);
		BookmarkDto bookmarkStageCounty = createBookmarkWithCodesTable(BookmarkConstants.STAGE_COUNTY,
				prefillDto.getGenericCaseInfoDto().getCdCaseCounty(), CodesConstant.CCOUNT);
		bookmarkNonGroupList.add(bookmarkStageCounty);
		BookmarkDto bookmarkDtArApproved = createBookmark(BookmarkConstants.DATE_AR_APPROVED,
				DateUtils.stringDt(prefillDto.getInvStageRtrvOutDto().getDtStageClose()));
		bookmarkNonGroupList.add(bookmarkDtArApproved);
		BookmarkDto bookmarkOfficeAddrLn1 = createBookmark(BookmarkConstants.OFFICE_ADDR_LINE_1,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
		bookmarkNonGroupList.add(bookmarkOfficeAddrLn1);
		BookmarkDto bookmarkOfficeAddrCity = createBookmark(BookmarkConstants.OFFICE_ADDR_CITY,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
		bookmarkNonGroupList.add(bookmarkOfficeAddrCity);
		BookmarkDto bookmarkOfficeAddrZip = createBookmark(BookmarkConstants.OFFICE_ADDR_ZIP,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
		bookmarkNonGroupList.add(bookmarkOfficeAddrZip);
		BookmarkDto bookmarkArClosureReason = createBookmarkWithCodesTable(BookmarkConstants.AR_CLOSURE_REASON,
				prefillDto.getInvStageRtrvOutDto().getCdStageReasonClosed(), CodesConstant.CCLOSAR);
		bookmarkNonGroupList.add(bookmarkArClosureReason);
		Boolean safetySelected = ServiceConstants.FALSEVAL;
		String safetyText = ServiceConstants.ARNO;
		Boolean familySelected = ServiceConstants.FALSEVAL;
		String familyText = ServiceConstants.ARNO;
		Boolean mrefSelected = ServiceConstants.FALSEVAL;
		String mrefText = ServiceConstants.EMPTY_STR;
		if (!ObjectUtils.isEmpty(prefillDto.getContactList())) {
			for (ContactDto contactDto : prefillDto.getContactList()) {
				if (!safetySelected && StringUtils.isNotBlank(contactDto.getIndSafPlanComp())
						&& ServiceConstants.Y.equals(contactDto.getIndSafPlanComp())) {
					safetySelected = ServiceConstants.TRUEVAL;
					safetyText = ServiceConstants.ARYES;
				}
				if (!familySelected && StringUtils.isNotBlank(contactDto.getIndFamPlanComp())
						&& ServiceConstants.Y.equals(contactDto.getIndFamPlanComp())) {
					familySelected = ServiceConstants.TRUEVAL;
					familyText = ServiceConstants.ARYES;
				}
				if (!mrefSelected && StringUtils.isNotBlank(prefillDto.getMref())) {
					mrefSelected = ServiceConstants.TRUEVAL;
					mrefText = prefillDto.getMref();
				}
				if (safetySelected && familySelected && mrefSelected) {
					break;
				}
			}
		}
		BookmarkDto bookmarkSafPlanComp = createBookmark(BookmarkConstants.SAFETY_PLAN_COMPLETED, safetyText);
		bookmarkNonGroupList.add(bookmarkSafPlanComp);
		BookmarkDto bookmarkFamPlanComp = createBookmark(BookmarkConstants.FAMILY_PLAN_COMPLETED, familyText);
		bookmarkNonGroupList.add(bookmarkFamPlanComp);
		BookmarkDto bookmarkSensitiveCase = createBookmark(BookmarkConstants.SENSITIVE_CASE,
				indTranslate(prefillDto.getGenericCaseInfoDto().getIndCaseSensitive()));
		bookmarkNonGroupList.add(bookmarkSensitiveCase);
		BookmarkDto bookmarkMref = createBookmark(BookmarkConstants.MULTIPLE_REF, mrefText);
		bookmarkNonGroupList.add(bookmarkMref);

		// Populate Intake Narrative section
		BookmarkDto bookmarkIntakeNarrDtReceived;
		if (ServiceConstants.MSG_PAPER_INTAKE == prefillDto.getIntStageRtrvOutDto().getIdSituation()) {
			/*bookmarkIntakeNarrDtReceived = createBookmark(BookmarkConstants.INTAKE_NARRATIVE_DATE_INTAKE_RECEIVED,
					lookupService.getMessages().get(ServiceConstants.MSG_PAPER_INTAKE).getTxtMessage());*/
			bookmarkIntakeNarrDtReceived = createBookmark(BookmarkConstants.INTAKE_NARRATIVE_DATE_INTAKE_RECEIVED,
					"Please see the paper file for this intake record.");
		} else {
			bookmarkIntakeNarrDtReceived = createBookmark(BookmarkConstants.INTAKE_NARRATIVE_DATE_INTAKE_RECEIVED,
					DateUtils.stringDt(prefillDto.getIntStageRtrvOutDto().getDtStageStart()));
		}
		bookmarkNonGroupList.add(bookmarkIntakeNarrDtReceived);
		BookmarkDto bookmarkIntakeStageId = createBookmark(BookmarkConstants.INTAKE_STAGE_ID,
				prefillDto.getIntStageRtrvOutDto().getIdStage());
		bookmarkNonGroupList.add(bookmarkIntakeStageId);
		BookmarkDto bookmarkIntakeStageType = createBookmark(BookmarkConstants.INTAKE_STAGE_TYPE,
				prefillDto.getIntStageRtrvOutDto().getCdStageType());
		bookmarkNonGroupList.add(bookmarkIntakeStageType);
		BookmarkDto bookmarkNmIntakeReporter = createBookmark(BookmarkConstants.INTAKE_REPORTER_NAME,
				prefillDto.getReporterDto().getNmPersonFull());
		bookmarkNonGroupList.add(bookmarkNmIntakeReporter);
		BookmarkDto bookmarkIntakeRelInt = createBookmarkWithCodesTable(BookmarkConstants.INTAKE_REL_INT,
				prefillDto.getReporterDto().getCdStagePersRelInt(), CodesConstant.CRPTRINT);
		bookmarkNonGroupList.add(bookmarkIntakeRelInt);
		BookmarkDto bookmarkIntakeReporterId = createBookmark(BookmarkConstants.INTAKE_REPORTER_ID,
				prefillDto.getReporterDto().getIdPerson());
		bookmarkNonGroupList.add(bookmarkIntakeReporterId);
		BookmarkDto bookmarkIntakeReporterNotes = createBookmark(BookmarkConstants.INTAKE_REPORTER_NOTES,
				prefillDto.getReporterDto().getTxtStagePersNote());
		bookmarkNonGroupList.add(bookmarkIntakeReporterNotes);

		BlobDataDto blobIntakeNarr = createBlobData(BookmarkConstants.INTAKE_NARRATIVE,
				BookmarkConstants.INCOMING_NARRATIVE_VIEW,
				new Long(prefillDto.getIntStageRtrvOutDto().getIdStage()).toString());
		blobList.add(blobIntakeNarr);

		// populateMergedIntake getIntakes   artf211437

		for (CpsInvIntakePersonPrincipalDto cpsInvIntakePersonPrincipalDto : prefillDto.getGetIntakesList()) {
			FormDataGroupDto intakeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_MERGE_INTAKE_NARR,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkIntakeList = new ArrayList<BookmarkDto>();
			List<BlobDataDto> bookmarkBlobDataList = new ArrayList<BlobDataDto>();

			bookmarkIntakeList.add(createBookmark(BookmarkConstants.DATE_STAGE_START,
					DateUtils.stringDt(cpsInvIntakePersonPrincipalDto.getDtStageStart())));
			bookmarkIntakeList.add(createBookmarkWithCodesTable(BookmarkConstants.MERGED_REL_INT,
					cpsInvIntakePersonPrincipalDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT));
			bookmarkIntakeList.add(createBookmark(BookmarkConstants.STAGE_TYPE,
					cpsInvIntakePersonPrincipalDto.getCdStageType()));
			bookmarkIntakeList.add(createBookmark(BookmarkConstants.MERGED_REPORTER_NAME,
					cpsInvIntakePersonPrincipalDto.getNmPersonFull()));
			bookmarkIntakeList.add(createBookmark(BookmarkConstants.MERGED_REPORTER_NOTES,
					cpsInvIntakePersonPrincipalDto.getTxtStagePersNote()));
			bookmarkIntakeList.add(createBookmark(BookmarkConstants.MERGED_REPORTER_ID,
					ObjectUtils.isEmpty(cpsInvIntakePersonPrincipalDto.getIdPerson())
							|| ServiceConstants.ZERO >= cpsInvIntakePersonPrincipalDto.getIdPerson()
							? ServiceConstants.ZERO : cpsInvIntakePersonPrincipalDto.getIdPerson()));
			bookmarkIntakeList.add(createBookmark(BookmarkConstants.STAGE_NUMBER,
					cpsInvIntakePersonPrincipalDto.getIdPriorStage()));

			bookmarkBlobDataList.add(createBlobData(BookmarkConstants.MERGE_NARRATIVE,
					CodesConstant.INCOMING_NARRATIVE_VIEW, cpsInvIntakePersonPrincipalDto.getIdPriorStage().toString()));

			intakeGroup.setBookmarkDtoList(bookmarkIntakeList);
			intakeGroup.setBlobDataDtoList(bookmarkBlobDataList);
			formDataGroupList.add(intakeGroup);
		}


		// Populate Allegation Detail section
		for (AllegationDetailDto allegDto : prefillDto.getAllegationDetailList()) {
			FormDataGroupDto allegationGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkAllegationList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkNmAlgVic = createBookmark(BookmarkConstants.ALG_VIC_NAME, allegDto.getScrPersVictim());
			bookmarkAllegationList.add(bookmarkNmAlgVic);
			BookmarkDto bookmarkAlgType = createBookmarkWithCodesTable(BookmarkConstants.ALG_TYPE,
					allegDto.getCdAllegType(), CodesConstant.CABALTYP);
			bookmarkAllegationList.add(bookmarkAlgType);
			BookmarkDto bookmarkNmAlgPerp = createBookmark(BookmarkConstants.ALG_PERP_NAME, allegDto.getScrAllegPerp());
			bookmarkAllegationList.add(bookmarkNmAlgPerp);

			allegationGroupDto.setBookmarkDtoList(bookmarkAllegationList);
			formDataGroupList.add(allegationGroupDto);
		}

		// Populate reporter section of person list
		FormDataGroupDto reporterGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REPORTER,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkReporterList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkNmRpt = createBookmark(BookmarkConstants.RPT_NAME,
				prefillDto.getReporterDto().getNmPersonFull());
		bookmarkReporterList.add(bookmarkNmRpt);
		BookmarkDto bookmarkRptRelationship = createBookmarkWithCodesTable(BookmarkConstants.RPT_RELATIONSHIP,
				prefillDto.getReporterDto().getCdStagePersRelInt(), CodesConstant.CRPTRINT);
		bookmarkReporterList.add(bookmarkRptRelationship);
		BookmarkDto bookmarkRptIdPerson = createBookmark(BookmarkConstants.RPT_PERSON_ID,
				prefillDto.getReporterDto().getIdPerson());
		bookmarkReporterList.add(bookmarkRptIdPerson);
		BookmarkDto bookmarkRptCallNotes = createBookmark(BookmarkConstants.RPT_CALL_NOTES,
				formatLineFeedValue(prefillDto.getReporterDto().getTxtStagePersNote()));
		bookmarkReporterList.add(bookmarkRptCallNotes);

		reporterGroupDto.setBookmarkDtoList(bookmarkReporterList);
		formDataGroupList.add(reporterGroupDto);

		// Populate principals section of person list
		for (CpsInvPrincipalDto principalDto : prefillDto.getPrincipalsList()) {
			FormDataGroupDto principalsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRINCIPALS,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> principalsGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkPrincipalsList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkPrnName = createBookmark(BookmarkConstants.PRN_NAME, principalDto.getNmPersonFull());
			bookmarkPrincipalsList.add(bookmarkPrnName);
			BookmarkDto bookmarkPrnPersonId = createBookmark(BookmarkConstants.PRN_PERSON_ID,
					principalDto.getIdPerson());
			bookmarkPrincipalsList.add(bookmarkPrnPersonId);
			BookmarkDto bookmarkPrnSsn = createBookmark(BookmarkConstants.PRN_SSN, principalDto.getNbrPersId());
			bookmarkPrincipalsList.add(bookmarkPrnSsn);
			BookmarkDto bookmarkPrnRelationship = createBookmarkWithCodesTable(BookmarkConstants.PRN_RELATIONSHIP,
					principalDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
			bookmarkPrincipalsList.add(bookmarkPrnRelationship);
			BookmarkDto bookmarkPrnStageRole = createBookmarkWithCodesTable(BookmarkConstants.PRN_STAGE_ROLE,
					principalDto.getCdStagePersRole(), CodesConstant.CROLEALL);
			bookmarkPrincipalsList.add(bookmarkPrnStageRole);
			BookmarkDto bookmarkPrnDob = createBookmark(BookmarkConstants.PRN_DOB,
					DateUtils.stringDt(principalDto.getDtPersonBirth()));
			bookmarkPrincipalsList.add(bookmarkPrnDob);
			BookmarkDto bookmarkPrnDod = createBookmark(BookmarkConstants.PRN_DOD,
					DateUtils.stringDt(principalDto.getDtPersonDeath()));
			bookmarkPrincipalsList.add(bookmarkPrnDod);
			BookmarkDto bookmarkPrnAge = createBookmark(BookmarkConstants.PRN_AGE, principalDto.getNbrPersonAge());
			bookmarkPrincipalsList.add(bookmarkPrnAge);
			BookmarkDto bookmarkPrnDeathCode = createBookmarkWithCodesTable(BookmarkConstants.PRN_DEATH_CODE,
					principalDto.getCdPersonDeath(), CodesConstant.CRSNDTH2);
			bookmarkPrincipalsList.add(bookmarkPrnDeathCode);
			BookmarkDto bookmarkPrnSex = createBookmarkWithCodesTable(BookmarkConstants.PRN_SEX,
					principalDto.getCdPersonSex(), CodesConstant.CSEX);
			bookmarkPrincipalsList.add(bookmarkPrnSex);
			BookmarkDto bookmarkPrnRace = createBookmark(BookmarkConstants.PRN_RACE, principalDto.getPersRace());
			bookmarkPrincipalsList.add(bookmarkPrnRace);
			BookmarkDto bookmarkPrnEthnicity = createBookmarkWithCodesTable(BookmarkConstants.PRN_ETHNICITY,
					principalDto.getCdEthn(), CodesConstant.CINDETHN);
			bookmarkPrincipalsList.add(bookmarkPrnEthnicity);
			BookmarkDto bookmarkPrnAddrLn1 = createBookmark(BookmarkConstants.PRN_ADDR_LINE_1,
					principalDto.getAddrPersStLn1());
			bookmarkPrincipalsList.add(bookmarkPrnAddrLn1);
			BookmarkDto bookmarkPrnAddrCity = createBookmark(BookmarkConstants.PRN_ADDR_CITY,
					principalDto.getAddrPersCity());
			bookmarkPrincipalsList.add(bookmarkPrnAddrCity);
			BookmarkDto bookmarkPrnAddrSt = createBookmarkWithCodesTable(BookmarkConstants.PRN_ADDR_ST,
					principalDto.getCdPersState(), CodesConstant.CSTATE);
			bookmarkPrincipalsList.add(bookmarkPrnAddrSt);
			BookmarkDto bookmarkPrnAddrZip = createBookmark(BookmarkConstants.PRN_ADDR_ZIP, principalDto.getPersZip());
			bookmarkPrincipalsList.add(bookmarkPrnAddrZip);

			// no characteristics group
			if (ServiceConstants.TWO.equals(principalDto.getCdPersChar())) {
				FormDataGroupDto noCharGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_NONE,
						FormGroupsConstants.TMPLAT_PRINCIPALS);
				principalsGroupList.add(noCharGroupDto);
			}

			// characteristics groups
			else {
				for (CharacteristicsDto charDto : prefillDto.getCharacteristicsList()) {
					// investigation group
					if (ServiceConstants.CCH.equals(charDto.getCdCharacCategory())) {
						FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_CCH,
								FormGroupsConstants.TMPLAT_PRINCIPALS);
						List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkCharCch = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHAR_CCH,
								charDto.getCdCharacCode(), CodesConstant.CCH);
						bookmarkChildInvList.add(bookmarkCharCch);
						BookmarkDto bookmarkCharCchStatus = createBookmarkWithCodesTable(
								BookmarkConstants.PRN_CHAR_CCH_STATUS, charDto.getCdStatus(), CodesConstant.CHARSTAT);
						bookmarkChildInvList.add(bookmarkCharCchStatus);

						childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
						principalsGroupList.add(childInvGroupDto);
					}

					// placement group
					else if (ServiceConstants.CPL.equals(charDto.getCdCharacCategory())) {
						FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_CPL,
								FormGroupsConstants.TMPLAT_PRINCIPALS);
						List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkCharCpl = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHAR_CPL,
								charDto.getCdCharacCode(), CodesConstant.CPL);
						bookmarkChildInvList.add(bookmarkCharCpl);
						BookmarkDto bookmarkCharCplStatus = createBookmarkWithCodesTable(
								BookmarkConstants.PRN_CHAR_CPL_STATUS, charDto.getCdStatus(), CodesConstant.CHARSTAT);
						bookmarkChildInvList.add(bookmarkCharCplStatus);

						childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
						principalsGroupList.add(childInvGroupDto);
					}

					// caretaker group
					else if (ServiceConstants.CASE_SPECIAL_REQUEST_TLETS.equals(charDto.getCdCharacCategory())) {
						FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_CCT,
								FormGroupsConstants.TMPLAT_PRINCIPALS);
						List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkCharCct = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHAR_CCT,
								charDto.getCdCharacCode(), CodesConstant.CCT);
						bookmarkChildInvList.add(bookmarkCharCct);

						childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
						principalsGroupList.add(childInvGroupDto);
					}

					// aps group
					else if (ServiceConstants.APS_CHARACTERISTIC.equals(charDto.getCdCharacCategory())) {
						FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_CAP,
								FormGroupsConstants.TMPLAT_PRINCIPALS);
						List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkCharCap = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHAR_CAP,
								charDto.getCdCharacCode(), CodesConstant.CAP);
						bookmarkChildInvList.add(bookmarkCharCap);

						childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
						principalsGroupList.add(childInvGroupDto);
					}
				}
			}

			principalsGroupDto.setBookmarkDtoList(bookmarkPrincipalsList);
			principalsGroupDto.setFormDataGroupList(principalsGroupList);
			formDataGroupList.add(principalsGroupDto);
		}

		// Populate collaterals section of person list
		for (CpsInvIntakePersonPrincipalDto colDto : prefillDto.getCollateralList()) {
			FormDataGroupDto colGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COLLATERALS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkColList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkColName = createBookmark(BookmarkConstants.COL_NAME, colDto.getNmPersonFull());
			bookmarkColList.add(bookmarkColName);
			BookmarkDto bookmarkColRelationship = createBookmarkWithCodesTable(BookmarkConstants.COL_RELATIONSHIP,
					colDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
			bookmarkColList.add(bookmarkColRelationship);
			BookmarkDto bookmarkColSex = createBookmarkWithCodesTable(BookmarkConstants.COL_SEX,
					colDto.getCdPersonSex(), CodesConstant.CSEX);
			bookmarkColList.add(bookmarkColSex);

			colGroupDto.setBookmarkDtoList(bookmarkColList);
			formDataGroupList.add(colGroupDto);
		}

		// Populate Impact History for Principals
		for (ArPrincipalsHistoryDto histPrnDto : prefillDto.getHistPrincipalsList()) {
			FormDataGroupDto prnImpactHistGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_IMPACT_HIST,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> prnImpactHistGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkPrnImpactHistList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkImpPrnName = createBookmark(BookmarkConstants.IMP_PRN_NAME,
					histPrnDto.getNmPersonFull());
			bookmarkPrnImpactHistList.add(bookmarkImpPrnName);
			BookmarkDto bookmarkImpPrnIdCase = createBookmark(BookmarkConstants.IMP_PRN_ID_CASE,
					histPrnDto.getIdCase());
			bookmarkPrnImpactHistList.add(bookmarkImpPrnIdCase);
			BookmarkDto bookmarkImpPrnIdStage = createBookmark(BookmarkConstants.IMP_PRN_ID_STAGE,
					histPrnDto.getIdStage());
			bookmarkPrnImpactHistList.add(bookmarkImpPrnIdStage);
			BookmarkDto bookmarkImpPrnDtIntake = createBookmark(BookmarkConstants.IMP_PRN_DT_INTAKE,
					DateUtils.stringDt(histPrnDto.getDtIntake()));
			bookmarkPrnImpactHistList.add(bookmarkImpPrnDtIntake);

			// Allegations from this stage, if any
			for (FacilityAllegationInfoDto histAllegDto : prefillDto.getHistAllegList()) {
				if (!ObjectUtils.isEmpty(histPrnDto.getIdPerson())
						&& histPrnDto.getIdPerson().equals(histAllegDto.getIdVictim())
						|| !ObjectUtils.isEmpty(histPrnDto.getIdPerson())
								&& histPrnDto.getIdPerson().equals(histAllegDto.getIdAllegedPerpetrator())
						|| !ObjectUtils.isEmpty(histPrnDto.getIdStage())
								&& histPrnDto.getIdStage().equals(histAllegDto.getIdAllegationStage())
						|| !ObjectUtils.isEmpty(histPrnDto.getIdPerson())
								&& histPrnDto.getIdPerson().equals(histAllegDto.getIdPerson())) {
					FormDataGroupDto algTypeGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALG_TYPE,
							FormGroupsConstants.TMPLAT_PRN_IMPACT_HIST);
					List<BookmarkDto> bookmarkAlgTypeList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkImpPrnAlgTyp = createBookmarkWithCodesTable(BookmarkConstants.IMP_PRN_ALG_TYP,
							histAllegDto.getCdAllegType(), CodesConstant.CABALTYP);
					bookmarkAlgTypeList.add(bookmarkImpPrnAlgTyp);
					BookmarkDto bookmarkImpPrnRole = createBookmarkWithCodesTable(BookmarkConstants.IMP_PRN_ROLE,
							histPrnDto.getCdStagePersRole(), CodesConstant.CROLEALL);
					bookmarkAlgTypeList.add(bookmarkImpPrnRole);
					BookmarkDto bookmarkImpPrnAlgDisp = createBookmarkWithCodesTable(BookmarkConstants.IMP_PRN_ALG_DISP,
							histAllegDto.getCdAllegDisposition(), CodesConstant.CDISPSTN);
					bookmarkAlgTypeList.add(bookmarkImpPrnAlgDisp);
					BookmarkDto bookmarkImpPrnAlgSev = createBookmarkWithCodesTable(
							BookmarkConstants.IMP_PRN_ALG_SEVERITY, histAllegDto.getCdAllegSeverity(),
							CodesConstant.CSEVERTY);
					bookmarkAlgTypeList.add(bookmarkImpPrnAlgSev);

					algTypeGroupDto.setBookmarkDtoList(bookmarkAlgTypeList);
					prnImpactHistGroupList.add(algTypeGroupDto);
				}
			}

			// Display risk finding
			BookmarkDto bookmarkImpPrnRiskFind;
			if (ServiceConstants.INVEST.equals(histPrnDto.getCdStage().trim())) {
				bookmarkImpPrnRiskFind = createBookmarkWithCodesTable(BookmarkConstants.IMP_PRN_RISK_FIND,
						histPrnDto.getRiskFinding(), CodesConstant.CCRSKFND);
			} else {
				bookmarkImpPrnRiskFind = createBookmark(BookmarkConstants.IMP_PRN_RISK_FIND,
						histPrnDto.getRiskFinding());
			}
			bookmarkPrnImpactHistList.add(bookmarkImpPrnRiskFind);

			// Removal dates
			for (CpsInvComDto histRemovalDto : prefillDto.getHistRemovalsList()) {
				if (!ObjectUtils.isEmpty(histPrnDto.getIdPerson())
						&& histPrnDto.getIdPerson().equals(histRemovalDto.getIdPerson())
						&& !ObjectUtils.isEmpty(histPrnDto.getIdStage())
						&& histPrnDto.getIdStage().equals(histRemovalDto.getIdStage())
						&& (!ObjectUtils.isEmpty(prefillDto.getIdStage())
								&& !prefillDto.getIdStage().equals(histRemovalDto.getIdStage())
								|| ObjectUtils.isEmpty(prefillDto.getIdStage())
										&& !ObjectUtils.isEmpty(histRemovalDto.getIdStage()))
						&& !ObjectUtils.isEmpty(histRemovalDto.getDtRemoval())) {
					FormDataGroupDto removalGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_IMP_PRN_REMOVE,
							FormGroupsConstants.TMPLAT_PRN_IMPACT_HIST);
					List<BookmarkDto> bookmarkRemovalList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkImpPrnRemoval = createBookmark(BookmarkConstants.IMP_PRN_REMOVAL,
							DateUtils.stringDt(histRemovalDto.getDtRemoval()));
					bookmarkRemovalList.add(bookmarkImpPrnRemoval);

					removalGroupDto.setBookmarkDtoList(bookmarkRemovalList);
					prnImpactHistGroupList.add(removalGroupDto);
				}
			}

			prnImpactHistGroupDto.setBookmarkDtoList(bookmarkPrnImpactHistList);
			prnImpactHistGroupDto.setFormDataGroupList(prnImpactHistGroupList);
			formDataGroupList.add(prnImpactHistGroupDto);
		}

		// Populate Criminal History
		for (CpsInvCrimHistDto crimHistDto : prefillDto.getCriminalHistList()) {
			FormDataGroupDto crimHistGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CRIM_HIST,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkCrimHistList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkPrnChistNmFull = createBookmark(BookmarkConstants.PRN_CHIST_NAME_FULL,
					crimHistDto.getPrnChistName());
			bookmarkCrimHistList.add(bookmarkPrnChistNmFull);
			BookmarkDto bookmarkPrnChistDtSearch = createBookmark(BookmarkConstants.PRN_CHIST_DT_SEARCH,
					DateUtils.stringDt(crimHistDto.getDtRecCheckCompl()));
			bookmarkCrimHistList.add(bookmarkPrnChistDtSearch);
			BookmarkDto bookmarkPrnChistCheckType = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHIST_CHECK_TYPE,
					crimHistDto.getCdRecCheckType(), CodesConstant.CCHKTYPE);
			bookmarkCrimHistList.add(bookmarkPrnChistCheckType);
			BookmarkDto bookmarkPrnChistResult = createBookmark(BookmarkConstants.PRN_CHIST_RESULT,
					FormConstants.EMPTY_STRING);
			if (!ObjectUtils.isEmpty(crimHistDto.getCdRecCheckStatus())) {
				bookmarkPrnChistResult = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHIST_RESULT,
						crimHistDto.getCdRecCheckStatus(), CodesConstant.CCRIMSTA);
			} else if (StringUtils.isNotBlank(crimHistDto.getCdCrimHistAction())) {
				bookmarkPrnChistResult.setBookmarkData(crimHistDto.getCdCrimHistAction());
			}
			bookmarkCrimHistList.add(bookmarkPrnChistResult);
			BookmarkDto bookmarkPrnChistAction = createBookmark(BookmarkConstants.PRN_CHIST_ACTION,
					crimHistDto.getCdCrimHistAction());
			bookmarkCrimHistList.add(bookmarkPrnChistAction);
			BookmarkDto bookmarkPrnChistSummary = createBookmark(BookmarkConstants.PRN_CHIST_SUMMARY,
					crimHistDto.getTxtRecCheckCmmnts());
			bookmarkCrimHistList.add(bookmarkPrnChistSummary);
			BookmarkDto bookmarkPrnChistComments = createBookmark(BookmarkConstants.PRN_CHIST_COMMENTS,
					formatCarriageLineFeedValue(crimHistDto.getTxtCrimHistCmnts()));
			bookmarkCrimHistList.add(bookmarkPrnChistComments);

			crimHistGroupDto.setBookmarkDtoList(bookmarkCrimHistList);
			formDataGroupList.add(crimHistGroupDto);
		}
		
		
		//Populate the SDM Safety and Risk Assessment
		if(!ObjectUtils.isEmpty(prefillDto.getSdmSafetyRiskAssessments()))
		{
		FormDataGroupDto sdmGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SDM,
					FormConstants.EMPTY_STRING);

		List<FormDataGroupDto> sdmformDataGroupList = new ArrayList<FormDataGroupDto>();	
	
		 HashSet<String> household = new HashSet<String>();
	      for (SdmSafetyRiskAssessmentsDto houseHoldDto : prefillDto.getSdmSafetyRiskAssessments()) 
	      {
	    	  if(ObjectUtils.isEmpty(houseHoldDto.getTxtHouseHoldName())){
	    		  houseHoldDto.setTxtHouseHoldName(SDM_HSHLDLABEL_NONE);
	            }
	        if(!household.contains(houseHoldDto.getTxtHouseHoldName())){
	          household.add(houseHoldDto.getTxtHouseHoldName());
	        }
	      }		
			
		for (String householdname :household) {
			
		FormDataGroupDto sdmHsHldGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SDM_HSHLD,
				FormGroupsConstants.TMPLAT_SDM);
		
        List<BookmarkDto> sdmHsHldList = new ArrayList<BookmarkDto>();	
		
        if(!SDM_HSHLDLABEL_NONE.equals(householdname))
        {
		BookmarkDto bookmarksdmHsHldLabel = createBookmark(BookmarkConstants.SDM_HSHLDLABEL,
				SDM_HSHLDLABEL_TEXT);
		sdmHsHldList.add(bookmarksdmHsHldLabel);
		
		BookmarkDto bookmarksdmHsHld = createBookmark(BookmarkConstants.SDM_HSHLD,
				householdname);
		sdmHsHldList.add(bookmarksdmHsHld);
        }
		
		sdmHsHldGroupDto.setBookmarkDtoList(sdmHsHldList);
		
		List<FormDataGroupDto> sdmHsHldAsmtformDataGroupList = new ArrayList<FormDataGroupDto>();			
			
		for (SdmSafetyRiskAssessmentsDto sdmSafetyRiskAssessmentsDto : prefillDto.getSdmSafetyRiskAssessments())
		{
			
		if(householdname.equals(sdmSafetyRiskAssessmentsDto.getTxtHouseHoldName()))
		{
		
		FormDataGroupDto sdmHsHldAsmtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SDM_HSHLD_ASMT,
					FormGroupsConstants.TMPLAT_SDM_HSHLD);
		List<BookmarkDto> sdmHsHldAsmtList = new ArrayList<BookmarkDto>();	
		
		BookmarkDto bookmarksdmHsHldAsmtDate = createBookmark(BookmarkConstants.SDM_HSHLD_ASMT_DATE,
				DateUtils.stringDt(sdmSafetyRiskAssessmentsDto.getDtAssessed()));
		sdmHsHldAsmtList.add(bookmarksdmHsHldAsmtDate);
		
		if(SDM_HSHLD_ASMT_TYPERISK_TEXT.equals(sdmSafetyRiskAssessmentsDto.getCdAssmtType()))
		{
		
		BookmarkDto bookmarksdmHsHldAsmtTypeLabel = createBookmark(BookmarkConstants.SDM_HSHLD_ASMT_TYPELABEL,
				sdmSafetyRiskAssessmentsDto.getCdAssmtType());
		sdmHsHldAsmtList.add(bookmarksdmHsHldAsmtTypeLabel);		
		}
		else
		{
		BookmarkDto bookmarksdmHsHldAsmtTypeLabel = createBookmark(BookmarkConstants.SDM_HSHLD_ASMT_TYPELABEL,
				SDM_HSHLD_ASMT_TYPESAFE_TEXT);
		sdmHsHldAsmtList.add(bookmarksdmHsHldAsmtTypeLabel);			
		}
		
		if(!ObjectUtils.isEmpty(sdmSafetyRiskAssessmentsDto.getCdAssmtType()))
		{
		BookmarkDto bookmarksdmHsHldAsmtType = createBookmarkWithCodesTable(BookmarkConstants.SDM_HSHLD_ASMT_TYPE,
				sdmSafetyRiskAssessmentsDto.getCdAssmtType(),"CSDMASMT");
		sdmHsHldAsmtList.add(bookmarksdmHsHldAsmtType);
		}
		
		if(!ObjectUtils.isEmpty(sdmSafetyRiskAssessmentsDto.getCdSafetyDecision()))
		{
		BookmarkDto bookmarksdmHsHldAsmtDecision = createBookmarkWithCodesTable(BookmarkConstants.SDM_HSHLD_ASMT_DECISION,
				sdmSafetyRiskAssessmentsDto.getCdSafetyDecision(),"CSDMDCSN");
		sdmHsHldAsmtList.add(bookmarksdmHsHldAsmtDecision);
		}
		
		if(!ObjectUtils.isEmpty(sdmSafetyRiskAssessmentsDto.getCdFinalRiskLevel()))
		{
		BookmarkDto bookmarksdmHsHldAsmtFinalRiskLevel = createBookmarkWithCodesTable(BookmarkConstants.SDM_HSHLD_ASMT_FINALRISKLEVEL,
				sdmSafetyRiskAssessmentsDto.getCdFinalRiskLevel(),"CSDMRLVL");
		sdmHsHldAsmtList.add(bookmarksdmHsHldAsmtFinalRiskLevel);
		}
		
		sdmHsHldAsmtGroupDto.setBookmarkDtoList(sdmHsHldAsmtList);
		sdmHsHldAsmtformDataGroupList.add(sdmHsHldAsmtGroupDto);
		}
		}
		sdmHsHldGroupDto.setFormDataGroupList(sdmHsHldAsmtformDataGroupList);
		sdmformDataGroupList.add(sdmHsHldGroupDto);
		}
		sdmGroupDto.setFormDataGroupList(sdmformDataGroupList);	
		formDataGroupList.add(sdmGroupDto);		
		}		

		// Initial safety assessment
		if(!ObjectUtils.isEmpty(prefillDto.getInitialSafetyAssmt().getIdEvent()))
		{
		FormDataGroupDto safetyAssmtInitialGroupDto = fillSafetyAssessment(prefillDto.getInitialSafetyAssmt(),
				prefillDto.getIntialEventDto(), null, bookmarkNonGroupList);
		formDataGroupList.add(safetyAssmtInitialGroupDto);
		}
		// Closure safety assessment
		if(!ObjectUtils.isEmpty(prefillDto.getClosureSafetyAssmt().getIdEvent()))
		{
		FormDataGroupDto safetyAssmtClosureGroupDto = fillSafetyAssessment(prefillDto.getClosureSafetyAssmt(),
				prefillDto.getClosureEventDto(), prefillDto.getInitialSafetyAssmt(), bookmarkNonGroupList);
		formDataGroupList.add(safetyAssmtClosureGroupDto);
		}

		// Populate services and referrals
		BookmarkDto bookmarkSpDtComp = createBookmark(BookmarkConstants.INITIAL_SP_COMPLETION_DATE,
				DateUtils.stringDt(prefillDto.getDtSafPlanComp()));
		bookmarkNonGroupList.add(bookmarkSpDtComp);
		BookmarkDto bookmarkFpDtComp = createBookmark(BookmarkConstants.INITIAL_FP_COMPLETION_DATE,
				DateUtils.stringDt(prefillDto.getDtFamPlanComp()));
		bookmarkNonGroupList.add(bookmarkFpDtComp);
		int i = 0;
		for (ArServiceReferralsDto servRefDto : prefillDto.getServRefList()) {
			String noServRef = ServiceConstants.EMPTY_STR;
			if (0 == i++) {
				if (StringUtils.isNotBlank(servRefDto.getIndReferral())
						&& ServiceConstants.Y.equals(servRefDto.getIndReferral())) {
					noServRef = ServiceConstants.Y;
				} else {
					noServRef = ServiceConstants.N;
				}
				BookmarkDto bookmarkNoServRef = createBookmark(BookmarkConstants.NO_SERVICES_REFERRALS, noServRef);
				bookmarkNonGroupList.add(bookmarkNoServRef);
			}
			FormDataGroupDto servRefGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SR,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkServRefList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkServRefType = createBookmarkWithCodesTable(BookmarkConstants.SR_TYPE,
					servRefDto.getCdSrType(), "CARSRVR");
			bookmarkServRefList.add(bookmarkServRefType);
			BookmarkDto bookmarkServRefSubtype = createBookmarkWithCodesTable(BookmarkConstants.SR_SUBTYPE,
					servRefDto.getCdSrSubtype(), servRefDto.getCdSrType());
			bookmarkServRefList.add(bookmarkServRefSubtype);
			BookmarkDto bookmarkServRefDtReferral = createBookmark(BookmarkConstants.SR_DATE_OF_REFERRAL,
					DateUtils.stringDt(servRefDto.getDtReferral()));
			bookmarkServRefList.add(bookmarkServRefDtReferral);
			BookmarkDto bookmarkServRefFinalOutcome = createBookmarkWithCodesTable(BookmarkConstants.SR_FINAL_OUTCOME,
					servRefDto.getCdFinalOutcome(), "CARSROC");
			bookmarkServRefList.add(bookmarkServRefFinalOutcome);
			BookmarkDto bookmarkServRefPersReferred = createBookmarkWithCodesTable(BookmarkConstants.SR_PERSON_REFERRED,
					servRefDto.getCdPersRef(), "CARSRP");
			bookmarkServRefList.add(bookmarkServRefPersReferred);
			BookmarkDto bookmarkServRefComments = createBookmark(BookmarkConstants.SR_COMMENTS,
					servRefDto.getTxtComments());
			bookmarkServRefList.add(bookmarkServRefComments);

			servRefGroupDto.setBookmarkDtoList(bookmarkServRefList);
			formDataGroupList.add(servRefGroupDto);
		}

		// Populate PCSPs
		for (PCSPDto pcspDto : prefillDto.getPcspList()) {
			FormDataGroupDto pcspGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_SAFETY_PLCMT,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPcspList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkNmChild = createBookmark(BookmarkConstants.CHILD_SAFETY_CHILD_NAME,
					pcspDto.getNmPersNameFull());
			bookmarkPcspList.add(bookmarkNmChild);
			BookmarkDto bookmarkNmCaregiver = createBookmark(BookmarkConstants.CHILD_SAFETY_CAREGIVER_NAME,
					pcspDto.getNmPersCargvrFull());
			bookmarkPcspList.add(bookmarkNmCaregiver);
			BookmarkDto bookmarkDtStart = createBookmark(BookmarkConstants.CHILD_SAFETY_START_DATE,
					DateUtils.stringDt(pcspDto.getDtStart()));
			bookmarkPcspList.add(bookmarkDtStart);
			BookmarkDto bookmarkStatus = createBookmarkWithCodesTable(BookmarkConstants.CHILD_SAFETY_STATUS,
					pcspDto.getCdStatus(), CodesConstant.CPCSPSTA);
			bookmarkPcspList.add(bookmarkStatus);
			BookmarkDto bookmarkDtEnd = createBookmark(BookmarkConstants.CHILD_SAFETY_END_DATE,
					DateUtils.stringDt(pcspDto.getDtEnd()));
			bookmarkPcspList.add(bookmarkDtEnd);
			BookmarkDto bookmarkEndReason = createBookmarkWithCodesTable(BookmarkConstants.CHILD_SAFETY_END_REASON,
					pcspDto.getCdEndRsn(), CodesConstant.CPCSPRSN);
			bookmarkPcspList.add(bookmarkEndReason);
			BookmarkDto bookmarkCaregiverManual = createBookmark(BookmarkConstants.CHILD_SAFETY_CAREGIVER_MANUAL,
					indTranslate(pcspDto.getIndCargvrManual()));
			bookmarkPcspList.add(bookmarkCaregiverManual);
			BookmarkDto bookmarkComments = createBookmark(BookmarkConstants.CHILD_SAFETY_COMMENTS,
					pcspDto.getPCSPComments());
			bookmarkPcspList.add(bookmarkComments);

			pcspGroupDto.setBookmarkDtoList(bookmarkPcspList);
			formDataGroupList.add(pcspGroupDto);
		}

		Date crelARNotifRightsDate = null;
		try {
			crelARNotifRightsDate = DateUtils
					.toJavaDateFromInput(lookupDao.simpleDecode(ServiceConstants.CRELDATE, CodesConstant.CRELDATE_JUNE_24_NTFCTN_RGHTS));
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}

		// Populate A-R info
		if(!ObjectUtils.isEmpty(crelARNotifRightsDate) && (ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto().getDtStageClose())
				|| DateUtils.isAfter(prefillDto.getGenericCaseInfoDto().getDtStageClose(), crelARNotifRightsDate))) {
			FormDataGroupDto notificationRightsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NOTIF_RIGHTS_AFTER_RELEASE,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkNotificationRightsList = new ArrayList<>();
			BookmarkDto indVrdWrtNotificationRights = createBookmark(BookmarkConstants.IND_VRBL_WRTN_NOTIF_RIGHTS,
					indTranslate(prefillDto.getArInvConcDto().getIndVrblWrtnNotifRights()));
			bookmarkNotificationRightsList.add(indVrdWrtNotificationRights);
			BookmarkDto indCopyGuideCpi = createBookmark(BookmarkConstants.IND_COPY_GUIDE_CPI,
					indTranslate(prefillDto.getArInvConcDto().getIndCopyGuideCpi()));;
			bookmarkNotificationRightsList.add(indCopyGuideCpi);
			BookmarkDto indNotifRightsUpld = createBookmark(BookmarkConstants.IND_NOTIF_RIGHTS_UPLD,
					indTranslate(prefillDto.getArInvConcDto().getIndNotifRightsUpld()));;
			bookmarkNotificationRightsList.add(indNotifRightsUpld);
			notificationRightsGroupDto.setBookmarkDtoList(bookmarkNotificationRightsList);
			formDataGroupList.add(notificationRightsGroupDto);
		}
		else {
			FormDataGroupDto notificationRightsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NOTIF_RIGHTS_BEFORE_RELEASE,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkNotificationRightsList = new ArrayList<>();
			BookmarkDto bookmarkParentGivenGuide = createBookmark(BookmarkConstants.PARENTS_IN_HOME_GIVEN_GUIDE,
					indTranslate(prefillDto.getArInvConcDto().getIndParentGivenGuide()));
			bookmarkNotificationRightsList.add(bookmarkParentGivenGuide);
			BookmarkDto bookmarkParentLivingOut = createBookmark(BookmarkConstants.PARENTS_LIVING_OUTSIDE_HOME,
					indTranslate(prefillDto.getArInvConcDto().getIndParentsLivingOutside()));
			bookmarkNotificationRightsList.add(bookmarkParentLivingOut);
			BookmarkDto bookmarkAbsentParentGuide = createBookmark(BookmarkConstants.PARENTS_OUT_OF_HOME_GIVEN_GUIDE,
					indTranslate(prefillDto.getArInvConcDto().getIndAbsentParentGuide()));
			bookmarkNotificationRightsList.add(bookmarkAbsentParentGuide);
			notificationRightsGroupDto.setBookmarkDtoList(bookmarkNotificationRightsList);
			formDataGroupList.add(notificationRightsGroupDto);
		}
		BookmarkDto bookmarkMultPersonsFound = createBookmark(BookmarkConstants.MULTIPLE_PERSON_IDENTIFICATION,
				indTranslate(prefillDto.getArInvConcDto().getIndMultiplePersonsFound()));
		bookmarkNonGroupList.add(bookmarkMultPersonsFound);
		BookmarkDto bookmarkMultMerged = createBookmark(BookmarkConstants.MULTIPLE_MERGED,
				indTranslate(prefillDto.getArInvConcDto().getIndMultiplePersonsMerged()));
		bookmarkNonGroupList.add(bookmarkMultMerged);
		BookmarkDto bookmarkMethAlleged = createBookmark(BookmarkConstants.METHAMPHETAMINE_ALLEGED,
				indTranslate(prefillDto.getArInvConcDto().getIndMethAllgdLongake()));
		bookmarkNonGroupList.add(bookmarkMethAlleged);
		BookmarkDto bookmarkMethConfirmed = createBookmark(BookmarkConstants.METHAMPHETAMINE_CONFIRMED,
				indTranslate(prefillDto.getArInvConcDto().getIndMeth()));
		bookmarkNonGroupList.add(bookmarkMethConfirmed);
		BookmarkDto bookmarkFtmOffered = createBookmark(BookmarkConstants.FAMILY_TEAM_MEETING_OFFERED,
				indTranslate(prefillDto.getArInvConcDto().getIndFTMOffered()));
		bookmarkNonGroupList.add(bookmarkFtmOffered);
		BookmarkDto bookmarkFtmOccured = createBookmark(BookmarkConstants.DID_FAMILY_TEAM_MEETING_OCCUR,
				indTranslate(prefillDto.getArInvConcDto().getIndFTMOccured()));
		bookmarkNonGroupList.add(bookmarkFtmOccured);

		// Populate A-R Contacts
		for (ContactDto contactDto : prefillDto.getContactList()) {
			FormDataGroupDto arContactsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AR_CONTACTS,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> arContactsGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkArContactsList = new ArrayList<BookmarkDto>();
			List<BlobDataDto> blobArContactsList = new ArrayList<BlobDataDto>();
			// Display merge stage if applicable
			if (ServiceConstants.POSITIVE_ONE.equals(contactDto.getIndEmergency())) {
				FormDataGroupDto arContactsMergeGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_AR_CONTACTS_MERGE, FormGroupsConstants.TMPLAT_AR_CONTACTS);
				arContactsGroupList.add(arContactsMergeGroupDto);
			}
			BookmarkDto bookmarkArContactsDtOccurred = createBookmark(BookmarkConstants.AR_CONTACTS_DT_OCCURRED,
					DateUtils.stringDt(contactDto.getDtContactOccurred()));
			bookmarkArContactsList.add(bookmarkArContactsDtOccurred);
			BookmarkDto bookmarkArContactsDtEntered = createBookmark(BookmarkConstants.AR_CONTACTS_DT_ENTERED,
					DateUtils.stringDt(contactDto.getDtContactApprv()));
			bookmarkArContactsList.add(bookmarkArContactsDtEntered);

			// set the linked names
			List<String> namesList = new ArrayList<String>();
			for (CpsInvComDto contactName : prefillDto.getContactNamesList()) {
				if (contactDto.getIdEvent() == contactName.getIdEvent()) {
					namesList.add(contactName.getNmPersonFull());
				}
			}

			// contact names
			for (String name : namesList) {
				FormDataGroupDto arContactsNamesGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_AR_CONTACTS_NAMES, FormGroupsConstants.TMPLAT_AR_CONTACTS);
				List<BookmarkDto> bookmarkArContactsNamesList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkArContactsName = createBookmark(BookmarkConstants.AR_CONTACTS_NAMES, name);
				bookmarkArContactsNamesList.add(bookmarkArContactsName);

				arContactsNamesGroupDto.setBookmarkDtoList(bookmarkArContactsNamesList);
				arContactsGroupList.add(arContactsNamesGroupDto);
			}
			BookmarkDto bookmarkArContactsPurpose = createBookmarkWithCodesTable(BookmarkConstants.AR_CONTACTS_PURPOSE,
					contactDto.getCdContactPurpose(), CodesConstant.CCNTPURP);
			bookmarkArContactsList.add(bookmarkArContactsPurpose);

			// Safety and family plan completion dates are the contact occur
			// date when the indicator is checked
			String spCompDate = ServiceConstants.EMPTY_STR;
			String fpCompDate = ServiceConstants.EMPTY_STR;
			if (StringUtils.isNotBlank(contactDto.getIndSafPlanComp())
					&& ServiceConstants.Y.equals(contactDto.getIndSafPlanComp())) {
				spCompDate = DateUtils.stringDt(contactDto.getDtContactOccurred());
			}
			if (StringUtils.isNotBlank(contactDto.getIndFamPlanComp())
					&& ServiceConstants.Y.equals(contactDto.getIndFamPlanComp())) {
				fpCompDate = DateUtils.stringDt(contactDto.getDtContactOccurred());
			}
			BookmarkDto bookmarkSpCompDate = createBookmark(BookmarkConstants.AR_CONTACTS_SP_COMP_DATE, spCompDate);
			bookmarkArContactsList.add(bookmarkSpCompDate);
			BookmarkDto bookmarkFpCompDate = createBookmark(BookmarkConstants.AR_CONTACTS_FP_COMP_DATE, fpCompDate);
			bookmarkArContactsList.add(bookmarkFpCompDate);

			// Narrative
			if(!ObjectUtils.isEmpty(contactDto.getIdEvent()) || !ObjectUtils.isEmpty(contactDto.getNarrative())) {
				BlobDataDto blobArContactsNarr = createBlobValueData(BookmarkConstants.AR_CONTACTS_NARRATIVE,
						contactDto.getNarrative(), contactDto.getIdTemplate());
				blobArContactsList.add(blobArContactsNarr);
			}

			arContactsGroupDto.setBlobDataDtoList(blobArContactsList);
			arContactsGroupDto.setBookmarkDtoList(bookmarkArContactsList);
			arContactsGroupDto.setFormDataGroupList(arContactsGroupList);
			formDataGroupList.add(arContactsGroupDto);
		}

		// Populate Relationship Information section
		for (ArRelationshipsDto relationshipDto : prefillDto.getRelationshipList()) {
			FormDataGroupDto relGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RELATION,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRelList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkRelNmFull = createBookmark(BookmarkConstants.RELATION_FNAME,
					relationshipDto.getNmPerson2Full());
			bookmarkRelList.add(bookmarkRelNmFull);
			BookmarkDto bookmarkRelId = createBookmark(BookmarkConstants.RELATION_ID, relationshipDto.getIdPerson2());
			bookmarkRelList.add(bookmarkRelId);
			BookmarkDto bookmarkRelRelationship = createBookmark(BookmarkConstants.RELATION_RELATIONSHIP,
					relationshipDto.getRelationship());
			bookmarkRelList.add(bookmarkRelRelationship);
			BookmarkDto bookmarkRelEnded = createBookmark(BookmarkConstants.RELATION_ENDED,
					relationshipDto.getStatus());
			bookmarkRelList.add(bookmarkRelEnded);
			BookmarkDto bookmarkRelContextNmFull = createBookmark(BookmarkConstants.RELATION_CONTEXTFNAME,
					relationshipDto.getNmPerson1Full());
			bookmarkRelList.add(bookmarkRelContextNmFull);
			BookmarkDto bookmarkRelContextId = createBookmark(BookmarkConstants.RELATION_CONTEXTID,
					relationshipDto.getIdPerson1());
			bookmarkRelList.add(bookmarkRelContextId);

			relGroupDto.setBookmarkDtoList(bookmarkRelList);
			formDataGroupList.add(relGroupDto);
		}
		// create default message when no relationships
		if (ObjectUtils.isEmpty(prefillDto.getRelationshipList())) {
			FormDataGroupDto relGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RELATION,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRelList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkRelNmFull = createBookmark(BookmarkConstants.RELATION_FNAME,
					ServiceConstants.NO_FAMILY_TREE);
			bookmarkRelList.add(bookmarkRelNmFull);

			relGroupDto.setBookmarkDtoList(bookmarkRelList);
			formDataGroupList.add(relGroupDto);
		}
		
		
		
		
		// populateContacts : populateSdmContacts : populateContactContacts

		for (CpsInvContactSdmSafetyAssessDto cpsInvContactSdmSafetyAssessDto : prefillDto.getGetContactsp1List()) {			
			
			FormDataGroupDto contactsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS,
					FormConstants.EMPTY_STRING);	

			// set the linked names
			ArrayList<String> contactNamesList = new ArrayList<String>();

			for (CpsInvComDto cpsInvComDto : prefillDto.getContactNamesList1()) {
				if (cpsInvContactSdmSafetyAssessDto.getIdEvent()
						.equals(cpsInvComDto.getIdEvent())) {
					contactNamesList.add(cpsInvComDto.getNmPersonFull());
				}
			}


			// contact is either contact or sdm as contact

			List<FormDataGroupDto> contactsSdmGroupList = new ArrayList<FormDataGroupDto>();
			
			if (StringUtils.isNotBlank(cpsInvContactSdmSafetyAssessDto.getCdScrType())
					&& (ServiceConstants.CSDMASMT_INIT.equals(cpsInvContactSdmSafetyAssessDto.getCdScrType())
							|| ServiceConstants.CSDMASMT_CLOS
									.equals(cpsInvContactSdmSafetyAssessDto.getCdScrType())
							|| ServiceConstants.CSDMASMT_REAS
									.equals(cpsInvContactSdmSafetyAssessDto.getCdScrType()))) {

				FormDataGroupDto csdmGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_SDM,
						FormGroupsConstants.TMPLAT_CONTACTS);
				
				List<FormDataGroupDto> contactsMergeSdmGroupList = new ArrayList<FormDataGroupDto>();
				csdmGroup.setFormDataGroupList(contactsMergeSdmGroupList);

				// display merge stage if applicable

				if (!cpsInvContactSdmSafetyAssessDto.getIdStage()
						.equals(prefillDto.getGenericCaseInfoDto().getIdStage())
						&& ServiceConstants.CFACTYP2_97.equals(cpsInvContactSdmSafetyAssessDto.getCdStageRsnCLosed())) {
					FormDataGroupDto contactsSdmMergeGroup = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CONTACTS_SDM_MERGE, FormGroupsConstants.TMPLAT_CONTACTS_SDM);
					contactsMergeSdmGroupList.add(contactsSdmMergeGroup);

					List<BookmarkDto> bookmarkSdmMergeList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkStage = createBookmark(BookmarkConstants.CONTACTS_SDM_MERGE_STAGE,
							cpsInvContactSdmSafetyAssessDto.getIdStage());
					bookmarkSdmMergeList.add(bookmarkStage);

					contactsSdmMergeGroup.setBookmarkDtoList(bookmarkSdmMergeList);

				}

				List<BookmarkDto> bookmarkContactsSdmList = new ArrayList<BookmarkDto>();

				/**QCR 62702 Change of sub title depending on Date of Assessment completed
				 * Add "SDM" if the investigation completed before 9/1/2020
				 *
				 **/
				BookmarkDto bookmarkSDMSubTitle = null;
				if(showSdm(cpsInvContactSdmSafetyAssessDto)){
					bookmarkSDMSubTitle = createBookmark(BookmarkConstants.TXT_SDM_SUB_TITLE, FormConstants.TXT_SDM);
				}else{
					bookmarkSDMSubTitle = createBookmark(BookmarkConstants.TXT_SDM_SUB_TITLE,FormConstants.EMPTY_STRING);
				}
				bookmarkContactsSdmList.add(bookmarkSDMSubTitle);
				/**End of QCR 62702**/

				BookmarkDto bookmarkContactsStage = createBookmark(BookmarkConstants.CONTACTS_SDM_CDSTAGE,
						cpsInvContactSdmSafetyAssessDto.getCdStage());
				bookmarkContactsSdmList.add(bookmarkContactsStage);
				BookmarkDto bookmarkDtOccured = createBookmark(BookmarkConstants.CONTACTS_SDM_DT,
						DateUtils.stringDt(cpsInvContactSdmSafetyAssessDto.getDtOccured()));
				bookmarkContactsSdmList.add(bookmarkDtOccured);
				BookmarkDto bookmarkDtCompl = createBookmark(BookmarkConstants.CONTACTS_SDM_DTCOMP,
						DateUtils.stringDt(cpsInvContactSdmSafetyAssessDto.getDtAssmtCompl()));
				bookmarkContactsSdmList.add(bookmarkDtCompl);
				BookmarkDto bookmarkType = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_SDM_TYPE,
						cpsInvContactSdmSafetyAssessDto.getCdScrType(), CodesConstant.CSDMASMT);
				bookmarkContactsSdmList.add(bookmarkType);
				BookmarkDto bookmarkSafetyDecision = createBookmarkWithCodesTable(
						BookmarkConstants.CONTACTS_SDM_DECISION, cpsInvContactSdmSafetyAssessDto.getCdSafetyDecision(),
						CodesConstant.CSDMDCSN);
				bookmarkContactsSdmList.add(bookmarkSafetyDecision);
				BookmarkDto bookmarkAssmtDiscussion = createBookmark(BookmarkConstants.CONTACTS_SDM_DISCUSSION,
						cpsInvContactSdmSafetyAssessDto.getTxtAssmtDiscussion());
				bookmarkContactsSdmList.add(bookmarkAssmtDiscussion);

				// sdm names
				StringBuilder contactNames = new StringBuilder();
				int count = 0;
				FormDataGroupDto contactsSdmNamesGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CONTACTS_SDM_NAMES, FormGroupsConstants.TMPLAT_CONTACTS_SDM);
				

				if (!ObjectUtils.isEmpty(cpsInvContactSdmSafetyAssessDto.getContactNamesList())) {
					for (String sdmName : cpsInvContactSdmSafetyAssessDto.getContactNamesList()) {
						contactNames.append(sdmName);
						if (++count < cpsInvContactSdmSafetyAssessDto.getContactNamesList().size()) {
							contactNames.append(ServiceConstants.CAPSCOMMA_SPACE);
						}
					}
				}
				List<BookmarkDto> bookmarkSdmNamesList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkName = createBookmark(BookmarkConstants.CONTACTS_SDM_NAMES,
						contactNames.toString());
				bookmarkSdmNamesList.add(bookmarkName);
				contactsSdmNamesGroup.setBookmarkDtoList(bookmarkSdmNamesList);
				contactsMergeSdmGroupList.add(contactsSdmNamesGroup);

				
				//sdm HouseHold Name
				if(!ObjectUtils.isEmpty(cpsInvContactSdmSafetyAssessDto.getHouseholdName()))
				{
					FormDataGroupDto contactsSdmHouseHoldNameGroup = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CONTACTS_SDM_HOUSEHOLD, FormGroupsConstants.TMPLAT_CONTACTS_SDM);				
					List<BookmarkDto> bookmarkSdmHouseHoldNamesList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkHouseHoldName = createBookmark(BookmarkConstants.CONTACTS_SDM_HOUSEHOLD,
							cpsInvContactSdmSafetyAssessDto.getHouseholdName());
					bookmarkSdmHouseHoldNamesList.add(bookmarkHouseHoldName);
					contactsSdmHouseHoldNameGroup.setBookmarkDtoList(bookmarkSdmHouseHoldNamesList);
					contactsMergeSdmGroupList.add(contactsSdmHouseHoldNameGroup);
				}
				
				// sdm yes responses

				for (CpsInvSdmSafetyRiskDto cpsInvSdmSafetyRiskDto : prefillDto.getGetSdmQaList()) {

					if (cpsInvContactSdmSafetyAssessDto.getIdEvent()
							.equals(cpsInvSdmSafetyRiskDto.getIdEvent())) {

						// showing any response that is YES selected
						if (ServiceConstants.CURRENT_DANGER_INDICATORS
								.equalsIgnoreCase(cpsInvSdmSafetyRiskDto.getCdSection())) {

							FormDataGroupDto sdmDangersGroup = createFormDataGroup(
									FormGroupsConstants.TMPLAT_CONTACTS_SDM_IND,
									FormGroupsConstants.TMPLAT_CONTACTS_SDM);
							

							List<BookmarkDto> bookmarkDangersList = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkDesc = createBookmark(BookmarkConstants.CONTACTS_SDM_IND_DANGER,
									cpsInvSdmSafetyRiskDto.getTxtOtherDesc());
							bookmarkDangersList.add(bookmarkDesc);

							sdmDangersGroup.setBookmarkDtoList(bookmarkDangersList);
							
							contactsMergeSdmGroupList.add(sdmDangersGroup);

						}

						if (ServiceConstants.SAFETY_INTERVENTIONS
								.equalsIgnoreCase(cpsInvSdmSafetyRiskDto.getCdSection())) {

							FormDataGroupDto sdmInterventionsGroup = createFormDataGroup(
									FormGroupsConstants.TMPLAT_CONTACTS_SDM_INTER,
									FormGroupsConstants.TMPLAT_CONTACTS_SDM);
							contactsMergeSdmGroupList.add(sdmInterventionsGroup);
							List<BookmarkDto> bookmarkInterventionsList = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkDesc = createBookmark(BookmarkConstants.CONTACTS_SDM_INTER,
									cpsInvSdmSafetyRiskDto.getTxtOtherDesc());
							bookmarkInterventionsList.add(bookmarkDesc);

							sdmInterventionsGroup.setBookmarkDtoList(bookmarkInterventionsList);

						}

					}

				}
				csdmGroup.setBookmarkDtoList(bookmarkContactsSdmList);
				contactsSdmGroupList.add(csdmGroup);

			} else {

				// Get a contact.
				FormDataGroupDto csdmGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_CON,
						FormGroupsConstants.TMPLAT_CONTACTS);
				

				List<FormDataGroupDto> contactsMergeSdmGroupList = new ArrayList<FormDataGroupDto>();
				

				// display merge stage if applicable

				if (!cpsInvContactSdmSafetyAssessDto.getIdStage()
						.equals(prefillDto.getGenericCaseInfoDto().getIdStage())
						&& ServiceConstants.CFACTYP2_97.equals(cpsInvContactSdmSafetyAssessDto.getCdStageRsnCLosed())
						// Warranty Defect Fix - 12466 - Added Null Pointer Check
						&& !ObjectUtils.isEmpty(prefillDto.getStageRtrvOutDto())
						&& !cpsInvContactSdmSafetyAssessDto.getIdStage()
								.equals(prefillDto.getStageRtrvOutDto().getIdStage())) {
					FormDataGroupDto contactsSdmMergeGroup = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CONTACTS_CON_MERGE, FormGroupsConstants.TMPLAT_CONTACTS_CON);
					contactsMergeSdmGroupList.add(contactsSdmMergeGroup);

					List<BookmarkDto> bookmarkSdmMergeList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkStage = createBookmark(BookmarkConstants.CONTACTS_CON_MERGE_STAGE,
							cpsInvContactSdmSafetyAssessDto.getIdStage());
					bookmarkSdmMergeList.add(bookmarkStage);

					contactsSdmMergeGroup.setBookmarkDtoList(bookmarkSdmMergeList);
				}

				List<BookmarkDto> bookmarkContactsConList = new ArrayList<BookmarkDto>();
				List<BlobDataDto> ContactsblobList = new ArrayList<BlobDataDto>();

				BookmarkDto bookmarkCdStage = createBookmark(BookmarkConstants.CONTACTS_CON_CDSTAGE,
						cpsInvContactSdmSafetyAssessDto.getCdStage());
				bookmarkContactsConList.add(bookmarkCdStage);
				BookmarkDto bookmarkDtOccured = createBookmark(BookmarkConstants.CONTACTS_CON_OCCURRED,
						DateUtils.stringDt(cpsInvContactSdmSafetyAssessDto.getDtOccured()) + ServiceConstants.SPACE
								+ DateUtils.getTime(cpsInvContactSdmSafetyAssessDto.getDtOccured()));
				bookmarkContactsConList.add(bookmarkDtOccured);
				BookmarkDto bookmarkConBy = createBookmark(BookmarkConstants.CONTACTS_CON_BY,
						cpsInvContactSdmSafetyAssessDto.getNmEmployeeFull());
				bookmarkContactsConList.add(bookmarkConBy);
				BookmarkDto bookmarkType = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_CON_TYPE,
						cpsInvContactSdmSafetyAssessDto.getCdContactType(), CodesConstant.CCNTCTYP);
				bookmarkContactsConList.add(bookmarkType);
				BookmarkDto bookmarkPurpose = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_CON_PURPOSE,
						cpsInvContactSdmSafetyAssessDto.getCdContactPurpose(), CodesConstant.CCNTPURP);
				bookmarkContactsConList.add(bookmarkPurpose);
				BookmarkDto bookmarkMethod = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_CON_METHOD,
						cpsInvContactSdmSafetyAssessDto.getCdContactMethod(), CodesConstant.CCNTMETH);
				bookmarkContactsConList.add(bookmarkMethod);
				BookmarkDto bookmarkLocation = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_CON_LOCATION,
						cpsInvContactSdmSafetyAssessDto.getCdContactLocation(), CodesConstant.CCNCTLOC);
				bookmarkContactsConList.add(bookmarkLocation);
				BookmarkDto bookmarkAttempted = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_CON_ATTEMPTED,
						cpsInvContactSdmSafetyAssessDto.getIndContactAttempted(), CodesConstant.CINVACAN);
				bookmarkContactsConList.add(bookmarkAttempted);
				if (ServiceConstants.YES_TEXT.equals(cpsInvContactSdmSafetyAssessDto.getIndNarr())) {
					BookmarkDto bookmarkNarrLabel = createBookmark(BookmarkConstants.CONTACTS_CON_NARRATIVELABEL,
							ServiceConstants.CONTACTS_CON_NARRATIVELABELTEXT);
					bookmarkContactsConList.add(bookmarkNarrLabel);
				}
				if (!ObjectUtils.isEmpty(cpsInvContactSdmSafetyAssessDto.getNarrative())){
					BlobDataDto blobArContactsNarr = createBlobValueData(BookmarkConstants.CONTACTS_CON_NARRATIVE,
							cpsInvContactSdmSafetyAssessDto.getNarrative(), cpsInvContactSdmSafetyAssessDto.getIdTemplate());
					ContactsblobList.add(blobArContactsNarr);
				}

				// contact names

				if (!ObjectUtils.isEmpty(contactNamesList)) {
					BookmarkDto bookmarkNamesLabel = createBookmark(BookmarkConstants.CONTACTS_CON_NAMESLABEL,
							ServiceConstants.CONTACTS_CON_NAMESLABELTEXT);
					bookmarkContactsConList.add(bookmarkNamesLabel);
					for (String sdmName : contactNamesList) {
						
						String contactname[]=null;
						if(null == sdmName || ServiceConstants.EMPTY_STRING.equals(sdmName)) sdmName=ServiceConstants.SPACE;
						if(sdmName.contains(ServiceConstants.COMMA))
						{
						contactname=sdmName.split(ServiceConstants.COMMA);
						if(contactname.length==2)
						{
						sdmName=contactname[1]+ServiceConstants.SPACE+contactname[0];
						}
						else
						{
						sdmName=contactname[0];	
						}
						}
						
						FormDataGroupDto contactsSdmNamesGroup = createFormDataGroup(
								FormGroupsConstants.TMPLAT_CONTACTS_CON_NAMES, FormGroupsConstants.TMPLAT_CONTACTS_CON);						
						List<BookmarkDto> bookmarkSdmNamesList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkName = createBookmark(BookmarkConstants.CONTACTS_CON_NAMES, sdmName);
						bookmarkSdmNamesList.add(bookmarkName);
						contactsSdmNamesGroup.setBookmarkDtoList(bookmarkSdmNamesList);
						contactsMergeSdmGroupList.add(contactsSdmNamesGroup);

					}
				}

				csdmGroup.setFormDataGroupList(contactsMergeSdmGroupList);
				csdmGroup.setBookmarkDtoList(bookmarkContactsConList);
				csdmGroup.setBlobDataDtoList(ContactsblobList);
				contactsSdmGroupList.add(csdmGroup);				
			}

			
			contactsGroup.setFormDataGroupList(contactsSdmGroupList);
			formDataGroupList.add(contactsGroup);
		}

		// Populate the Emergency Assistance Section artf152702 refactor
		String determination = null;
		String eaCpr = null;
		String eaRra = null;
		
		if ( ( !ObjectUtils.isEmpty(prefillDto.getArInvConcDto() ) 
				&& !ObjectUtils.isEmpty(prefillDto.getArInvConcDto().getCdAROverallDisposition() ) 
				&& prefillDto.getArInvConcDto().getCdAROverallDisposition().equals(CodesConstant.CCLOSAR_060) 
				&& !ObjectUtils.isEmpty(prefillDto.getArInvConcDto().getIsRiskIndicated() ) 
				&& prefillDto.getArInvConcDto().getIsRiskIndicated() ) 
				|| isEligible(prefillDto.getSdmSafetyRiskAssessments() ) ) {

				FormDataGroupDto eaGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_EA, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkEaList = new ArrayList<BookmarkDto>();
				bookmarkEaList.add( createBookmark(BookmarkConstants.EA_ELIGIBLE, EA_ELIGIBLETEXT));
				bookmarkEaList.add( createBookmarkWithCodesTable(BookmarkConstants.EA_INCOME,
						prefillDto.getArInvConcDto().getCdFamilyIncome(), CodesConstant.CEAFINCM));
				bookmarkEaList.add( createBookmarkWithCodesTable(BookmarkConstants.EA_ARC_QUESTION, 
						CodesConstant.CELGSTMT_ARC, CodesConstant.CELGSTMT));
				bookmarkEaList.add( createBookmarkWithCodesTable(BookmarkConstants.EA_CPR_QUESTION, 
						CodesConstant.CELGSTMT_CPR, CodesConstant.CELGSTMT));
				bookmarkEaList.add( createBookmarkWithCodesTable(BookmarkConstants.EA_RRA_QUESTION, 
						CodesConstant.CELGSTMT_RRA, CodesConstant.CELGSTMT));
				//[artf160264] ALM-15306 removed the counter to be consistent
				if (!ObjectUtils.isEmpty(prefillDto.getArInvConcDto().getArEaEligibilityDtoList())
						&& 0 < prefillDto.getArInvConcDto().getArEaEligibilityDtoList().size()) {
					//[artf163982] ALM#15522 Some A-R cases still not displaying questions and correct determination in Alternative Response Report
					for(ArEaEligibilityDto arEaEligibilityDto : prefillDto.getArInvConcDto().getArEaEligibilityDtoList()){
						
						if (!(CodesConstant.CELGSTMT_CPR.equals(arEaEligibilityDto.getCdEaQuestion()) ||
								CodesConstant.CELGSTMT_RRA.equals(arEaEligibilityDto.getCdEaQuestion()))) {
							bookmarkEaList.add(createBookmark(BookmarkConstants.EA_ARC_RESPONSE, 
									getEaResponse(arEaEligibilityDto.getIndEaResponse())));
						} else
						if (!(CodesConstant.CELGSTMT_ARC.equals(arEaEligibilityDto.getCdEaQuestion()) ||
								CodesConstant.CELGSTMT_RRA.equals(arEaEligibilityDto.getCdEaQuestion()))) {
							bookmarkEaList.add(createBookmark(BookmarkConstants.EA_CPR_RESPONSE,
									getEaResponse(arEaEligibilityDto.getIndEaResponse())));
							eaCpr = arEaEligibilityDto.getIndEaResponse();
						} else
						if (!(CodesConstant.CELGSTMT_ARC.equals(arEaEligibilityDto.getCdEaQuestion()) ||
								CodesConstant.CELGSTMT_CPR.equals(arEaEligibilityDto.getCdEaQuestion()))) {
							bookmarkEaList.add(createBookmark(BookmarkConstants.EA_RRA_RESPONSE, 
									getEaResponse(arEaEligibilityDto.getIndEaResponse())));
							eaRra = arEaEligibilityDto.getIndEaResponse();
						}
					}
					
				}
				eaGroup.setBookmarkDtoList(bookmarkEaList);
				formDataGroupList.add(eaGroup);
		} 
		
		//artf160264 code changes. Modified the logic to replicate same as UI for displaying the determination value of eligibility section of the form
		if(!prefillDto.getArInvConcDto().getArEaEligibilityDtoList().isEmpty() && 
				null != prefillDto.getArInvConcDto().getCdFamilyIncome() && 
				!"I5".equals(prefillDto.getArInvConcDto().getCdFamilyIncome()) && 
				!"I6".equals(prefillDto.getArInvConcDto().getCdFamilyIncome())){
			if("N".equals(eaCpr) || "N".equals(eaRra)){
				determination = lookupDao.simpleDecodeSafe(CodesConstant.CEACONCL, ServiceConstants.N);
			}else {
				determination = lookupDao.simpleDecodeSafe(CodesConstant.CEACONCL, ServiceConstants.Y);
			}
		}else if(!prefillDto.getArInvConcDto().getArEaEligibilityDtoList().isEmpty() && 
				null != prefillDto.getArInvConcDto().getCdFamilyIncome() && 
				("I5".equals(prefillDto.getArInvConcDto().getCdFamilyIncome()) ||
				"I6".equals(prefillDto.getArInvConcDto().getCdFamilyIncome()))){
			determination = lookupDao.simpleDecodeSafe(CodesConstant.CEACONCL, ServiceConstants.N);
		}else if((!prefillDto.getArInvConcDto().getArEaEligibilityDtoList().isEmpty() && 
				null == prefillDto.getArInvConcDto().getCdFamilyIncome()) ||
				prefillDto.getArInvConcDto().getArEaEligibilityDtoList().isEmpty()){
			determination = lookupDao.simpleDecodeSafe(CodesConstant.CEACONCL, ServiceConstants.Y);
		}else{
			determination = lookupDao.simpleDecodeSafe(CodesConstant.CEACONCL, ServiceConstants.Y);
		}
		
		bookmarkNonGroupList.add( createBookmark(BookmarkConstants.EADETERMINATION, determination));
		
		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		prefillData.setBlobDataDtoList(blobList);
		prefillData.setBookmarkDtoList(bookmarkNonGroupList);
		prefillData.setFormDataGroupList(formDataGroupList);

		return prefillData;
	}



	/**
	 * Method Name: indTranslate Method Description: Translates ind character
	 * into yes/no string
	 * 
	 * @param indChar
	 * @return
	 */
	private String indTranslate(String indChar) {
		String translated = ServiceConstants.EMPTY_STR;
		if (StringUtils.isNotBlank(indChar)) {
			if (ServiceConstants.Y.equals(indChar)) {
				translated = ServiceConstants.ARYES;
			} else if (ServiceConstants.N.equals(indChar)) {
				translated = ServiceConstants.ARNO;
			} else if (ServiceConstants.CD_INACTIVE.equals(indChar)) {
				translated = ServiceConstants.NEEDS_MORE_INFO;
			}
		}
		return translated;
	}

	/**
	 * Method Name: fillSafetyAssessment Method Description: Organizes safety
	 * assessment data into bookmarks based off whether it's the closure or
	 * initial SA
	 * 
	 * @param closureSafetyAssmt
	 * @param closureEventDto
	 * @param initialSafetyAssmt
	 * @param bookmarkNonGroupList
	 * @return FormDataGroupDto
	 */
	private FormDataGroupDto fillSafetyAssessment(ARSafetyAssmtValueDto closureSafetyAssmt, EventDto closureEventDto,
			ARSafetyAssmtValueDto initialSafetyAssmt, List<BookmarkDto> bookmarkNonGroupList) {
		Boolean areaFound = ServiceConstants.FALSEVAL;
		// safety_assessment_group is the group so the header always displays
		FormDataGroupDto safetyAssmtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SA,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> safetyAssmtGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkSafetyAssmtList = new ArrayList<BookmarkDto>();
		FormDataGroupDto safetyAssmtDataGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SA_HASDATA,
				FormGroupsConstants.TMPLAT_SA);
		List<FormDataGroupDto> safetyAssmtDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkSafetyAssmtDataList = new ArrayList<BookmarkDto>();
		// for initial assessment
		if (ObjectUtils.isEmpty(initialSafetyAssmt)) {		
			BookmarkDto bookmarkSafetyAssmtHeader = createBookmark(BookmarkConstants.SA_INITIAL_OR_CLOSURE_HEADER,
					ServiceConstants.INITIAL_ASSESSMENT_AR);
			bookmarkSafetyAssmtList.add(bookmarkSafetyAssmtHeader);
			BookmarkDto bookmarkSafetyAssmtActionHeader = createBookmark(BookmarkConstants.SA_ACTION_HEADER,
					ServiceConstants.INITIAL_ACTION_HEADER);
			bookmarkSafetyAssmtList.add(bookmarkSafetyAssmtActionHeader);
			BookmarkDto bookmarkSafetyAssmtAssessHeader = createBookmark(BookmarkConstants.SA_ASSESS_HEADER,
					ServiceConstants.INITIAL_ASSESSMENT_HEADER);
			bookmarkSafetyAssmtDataList.add(bookmarkSafetyAssmtAssessHeader);		
			// Add the approval date if record present
			if (!ObjectUtils.isEmpty(closureSafetyAssmt.getIdEvent()) && 0 != closureSafetyAssmt.getIdEvent()
					&& StringUtils.isNotBlank(closureEventDto.getCdEventStatus())
					&& ServiceConstants.APPROVED.equals(closureEventDto.getCdEventStatus())) {
				FormDataGroupDto safetyAssmtInitialGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SA_INITIAL,
						FormGroupsConstants.TMPLAT_SA_HASDATA);
				List<BookmarkDto> bookmarkSafetyAssmtInitialList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkSafetyAssmtDtApproved = createBookmark(BookmarkConstants.SA_APPROVED_DATE,
						DateUtils.stringDt(closureEventDto.getDtEventOccurred()));
				bookmarkSafetyAssmtInitialList.add(bookmarkSafetyAssmtDtApproved);
				safetyAssmtInitialGroupDto.setBookmarkDtoList(bookmarkSafetyAssmtInitialList);
				safetyAssmtDataGroupList.add(safetyAssmtInitialGroupDto);
			}
		}
		// for closure assessment
		else {			
			BookmarkDto bookmarkSafetyAssmtHeader = createBookmark(BookmarkConstants.SA_INITIAL_OR_CLOSURE_HEADER,
					ServiceConstants.CLOSURE_ASSESSMENT);
			bookmarkSafetyAssmtList.add(bookmarkSafetyAssmtHeader);
			BookmarkDto bookmarkSafetyAssmtActionHeader = createBookmark(BookmarkConstants.SA_ACTION_HEADER,
					ServiceConstants.CLOSURE_ACTION_HEADER);
			bookmarkSafetyAssmtList.add(bookmarkSafetyAssmtActionHeader);
			BookmarkDto bookmarkSafetyAssmtAssessHeader = createBookmark(BookmarkConstants.SA_ASSESS_HEADER,
					ServiceConstants.CLOSURE_ASSESSMENT_HEADER);
			bookmarkSafetyAssmtDataList.add(bookmarkSafetyAssmtAssessHeader);			
		}

		List<ARSafetyAssmtAreaValueDto> areaList = new ArrayList<ARSafetyAssmtAreaValueDto>();
		if (!ObjectUtils.isEmpty(closureSafetyAssmt.getIdEvent()) && 0 < closureSafetyAssmt.getIdEvent()
				&& !ObjectUtils.isEmpty(closureSafetyAssmt.getaRSafetyAssmtAreas())) {
			areaList = closureSafetyAssmt.getaRSafetyAssmtAreas();
		}
		// Only add the group to the form if there was something to display
		Boolean safetyThreatsHeader = ServiceConstants.FALSEVAL;
		Boolean concHeader = ServiceConstants.FALSEVAL;
		if (!ObjectUtils.isEmpty(areaList)) {
			for (ARSafetyAssmtAreaValueDto areaDto : areaList) {
				areaFound = ServiceConstants.FALSEVAL;
				FormDataGroupDto safetyAreaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SA_SAFETY_AREA,
						FormGroupsConstants.TMPLAT_SA_HASDATA);
				List<FormDataGroupDto> safetyAreaGroupList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkSafetyAreaList = new ArrayList<BookmarkDto>();
				// Display safety threats header at beginning
				if (!safetyThreatsHeader) {
					safetyThreatsHeader = ServiceConstants.TRUEVAL;
					FormDataGroupDto safetyThreatsGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SA_SAFETY_THREATS, FormGroupsConstants.TMPLAT_SA_SAFETY_AREA);
					safetyAreaGroupList.add(safetyThreatsGroupDto);
				}
				// Display conclusion header when those areas appear
				if ((ServiceConstants.ARC_MAX_MONTH == areaDto.getIdArea()
						|| ServiceConstants.VALIDATE_WITH_ERRORS == areaDto.getIdArea()) && !concHeader) {
					concHeader = ServiceConstants.TRUEVAL;
					FormDataGroupDto concHeaderGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SA_CONCLUSION,
							FormGroupsConstants.TMPLAT_SA_SAFETY_AREA);
					safetyAreaGroupList.add(concHeaderGroupDto);
				}
				BookmarkDto bookmarkArea = createBookmark(BookmarkConstants.SA_AREA, areaDto.getArea());
				bookmarkSafetyAreaList.add(bookmarkArea);
				safetyAreaGroupDto.setBookmarkDtoList(bookmarkSafetyAreaList);
				// display factors
				List<ARSafetyAssmtFactorValueDto> factorList = closureSafetyAssmt.getaRSafetyAssmtFactors();
				if (!ObjectUtils.isEmpty(factorList)) {
					for (ARSafetyAssmtFactorValueDto factorDto : factorList) {
						if (StringUtils.isBlank(factorDto.getResponse())) {
							continue; // skip if no response
						} else {
							if (!ObjectUtils.isEmpty(areaDto.getIdArea())
									&& areaDto.getIdArea().equals(factorDto.getIdArea())) {
								areaFound = ServiceConstants.TRUEVAL;
								// The discuss factor value should only display
								// if the closure value is 'N' and initial value
								// is 'Y'
								if (StringUtils.isNotBlank(factorDto.getFactorDepVal())
										&& ServiceConstants.N.equals(factorDto.getFactorDepVal())) {
									// The closure needs to be able to look up
									// answers on initial to determine if we
									// should show the discuss question
									ARSafetyAssmtFactorValueDto closureFactor = getFactor(closureSafetyAssmt,
											factorDto.getIdFactorDep());
									ARSafetyAssmtFactorValueDto initialFactor = getFactor(initialSafetyAssmt,
											closureFactor.getIdFactorInitial());
									if (StringUtils.isBlank(closureFactor.getResponse())
											|| StringUtils.isBlank(initialFactor.getResponse())
											|| !(ServiceConstants.N.equals(closureFactor.getResponse())
													&& ServiceConstants.Y.equals(initialFactor.getResponse()))) {
										continue;
									}
								}
								// Fill in assessment header fields as they are
								// encountered
								if (ServiceConstants.SEVEN_VALUE == factorDto.getIdFactor()
										|| ServiceConstants.DEFAULT_LINE_WIDTH == factorDto.getIdFactor()) {
									BookmarkDto bookmarkSafetyAssmtAction = createBookmark(BookmarkConstants.SA_ACTION,
											factorDto.getResponse());
									bookmarkSafetyAssmtDataList.add(bookmarkSafetyAssmtAction);
								} else if (ServiceConstants.APPROVE_STATE == factorDto.getIdFactor()
										|| ServiceConstants.ID_FACTOR_78.intValue() == factorDto.getIdFactor()) {
									BookmarkDto bookmarkSafetyAssmtAssess = createBookmark(BookmarkConstants.SA_ASSESS,
											factorDto.getResponse());
									bookmarkSafetyAssmtDataList.add(bookmarkSafetyAssmtAssess);
								}
								// Fill in top of report header items
								if (ServiceConstants.DEFAULT_LINE_WIDTH == factorDto.getIdFactor()) {
									BookmarkDto bookmarkClosureActionTake = createBookmark(
											BookmarkConstants.CLOSURE_SA_ACTION_TAKEN, factorDto.getResponse());
									bookmarkNonGroupList.add(bookmarkClosureActionTake);
								} else if (ServiceConstants.ID_FACTOR_78.intValue() == factorDto.getIdFactor()) {
									BookmarkDto bookmarkFinalAssmt = createBookmark(
											BookmarkConstants.CLOSURE_SA_FINAL_ASSESSMENT, factorDto.getResponse());
									bookmarkNonGroupList.add(bookmarkFinalAssmt);
								}
								FormDataGroupDto factorGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SA_FACTOR,
										FormGroupsConstants.TMPLAT_SA_SAFETY_AREA);
								List<BookmarkDto> bookmarkFactorList = new ArrayList<BookmarkDto>();
								List<FormDataGroupDto> factorGroupList = new ArrayList<FormDataGroupDto>();
								StringBuilder answer = new StringBuilder();
								if (ServiceConstants.EVAL_STATE == factorDto.getIdFactor()
										|| ServiceConstants.ID_FACTOR_77.intValue() == factorDto.getIdFactor()
										|| (StringUtils.isNotBlank(factorDto.getIndFactor2())
												&& ServiceConstants.Y.equals(factorDto.getIndFactor2()))
										|| (StringUtils.isNotBlank(factorDto.getFactorDepVal()) && (ServiceConstants.Y
												.equals(factorDto.getFactorDepVal())
												|| ServiceConstants.CD_INACTIVE.equals(factorDto.getFactorDepVal())))) {
									FormDataGroupDto space1GroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_SPACE1, FormGroupsConstants.TMPLAT_SA_FACTOR);
									factorGroupList.add(space1GroupDto);
									FormDataGroupDto space2GroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_SPACE2, FormGroupsConstants.TMPLAT_SA_FACTOR);
									factorGroupList.add(space2GroupDto);
								} else {
									answer.append(ServiceConstants.ANSWER_HEADING);
								}
								BookmarkDto bookmarkSafetyAssmtQuestion = createBookmark(BookmarkConstants.SA_QUESTION,
										factorDto.getFactor());
								bookmarkFactorList.add(bookmarkSafetyAssmtQuestion);
								if (ServiceConstants.TASK_TODO.equals(factorDto.getIndFactorType())) {
									answer.append(factorDto.getResponse());
								} else {
									String answerCode = StringUtils.isBlank(factorDto.getResponse())
											? ServiceConstants.EMPTY_STR : factorDto.getResponse().trim();
									String answerTrans = indTranslate(answerCode);
									if (StringUtils.isNotBlank(answerTrans)) {
										answer.append(answerTrans);
									} else {
										answer.append(answerCode);
									}
								}
								BookmarkDto bookmarkAnswer = createBookmark(BookmarkConstants.SA_ANSWER,
										answer.toString());
								bookmarkFactorList.add(bookmarkAnswer);

								factorGroupDto.setBookmarkDtoList(bookmarkFactorList);
								factorGroupDto.setFormDataGroupList(factorGroupList);
								safetyAreaGroupList.add(factorGroupDto);
							}
						}
					}
					if (areaFound) {
						safetyAreaGroupDto.setBookmarkDtoList(bookmarkSafetyAreaList);
						safetyAreaGroupDto.setFormDataGroupList(safetyAreaGroupList);
						safetyAssmtDataGroupList.add(safetyAreaGroupDto);
					}
				}
			}
			if (areaFound) {
				safetyAssmtDataGroupDto.setBookmarkDtoList(bookmarkSafetyAssmtDataList);
				safetyAssmtDataGroupDto.setFormDataGroupList(safetyAssmtDataGroupList);
				safetyAssmtGroupList.add(safetyAssmtDataGroupDto);
			}
		}
		safetyAssmtGroupDto.setFormDataGroupList(safetyAssmtGroupList);
		safetyAssmtGroupDto.setBookmarkDtoList(bookmarkSafetyAssmtList);

		return safetyAssmtGroupDto;
	}

	/**
	 * Method Name: getFactor Method Description: Gets factor from safety
	 * assessment to determine whether discussion questions should be shown
	 * 
	 * @param safetyAssmtDto
	 * @param idFactor
	 * @return ARSafetyAssmtFactorValueDto
	 */
	private ARSafetyAssmtFactorValueDto getFactor(ARSafetyAssmtValueDto safetyAssmtDto, Integer idFactor) {
		ARSafetyAssmtFactorValueDto factorDto = null;
		Boolean found = ServiceConstants.FALSEVAL;
		if (!ObjectUtils.isEmpty(safetyAssmtDto.getaRSafetyAssmtAreas())) {
			for (ARSafetyAssmtAreaValueDto areaDto : safetyAssmtDto.getaRSafetyAssmtAreas()) {
				if (!ObjectUtils.isEmpty(areaDto) && !ObjectUtils.isEmpty(areaDto.getaRSafetyAssmtFactors())) {
					for (ARSafetyAssmtFactorValueDto factor : areaDto.getaRSafetyAssmtFactors()) {
						if (!ObjectUtils.isEmpty(factor.getIdFactor()) && factor.getIdFactor().equals(idFactor)) {
							factorDto = factor;
							found = ServiceConstants.TRUEVAL;
						}
						if (found) {
							break;
						}
					}
				}
				if (found) {
					break;
				}
			}
		}
		return factorDto;
	}
	
	
    
/**
* populate isEligible for Emergency Assistance section.
* artf152702 handle selected household risk assmt
*/
private boolean isEligible( List<SdmSafetyRiskAssessmentsDto> sdmVb )
{
	boolean eligible = false;
	boolean household = false;
	// sdm assessments before and after household to be separate lists
	List<SdmSafetyRiskAssessmentsDto> sdmList = new ArrayList<>();
	List<SdmSafetyRiskAssessmentsDto> householdSdmList = new ArrayList<>();
	if (null != sdmVb && 0 < sdmVb.size()) {
		for (SdmSafetyRiskAssessmentsDto assmt : sdmVb){
			if (TypeConvUtil.isNullOrEmpty(assmt.getIdHouseholdEvent())) {
				sdmList.add(assmt);
			} else {
				householdSdmList.add(assmt);
			}
		}
		// find the sdm for the one selected household
		if ( 0 < householdSdmList.size()) {
			for (SdmSafetyRiskAssessmentsDto assmt : householdSdmList){
				if (assmt.getIdEvent().equals(assmt.getIdHouseholdEvent() )
						&& SDM_HSHLD_ASMT_TYPERISK_TEXT.equals(assmt.getCdAssmtType() ) ) {
					household = true;
					if(CSDMRLVL_HIGH.equals(assmt.getCdFinalRiskLevel() )
				        		|| CSDMRLVL_VERYHIGH.equals(assmt.getCdFinalRiskLevel() ) ) {
					eligible = true;
					break;
					}
				}
			}
		}
		// before p2 r1.1, there was at most one risk type
		if ( ! household && 0 < sdmList.size()) {
			for(SdmSafetyRiskAssessmentsDto assmt : sdmList){
				if(SDM_HSHLD_ASMT_TYPERISK_TEXT.equals(assmt.getCdAssmtType() )
						&& (CSDMRLVL_HIGH.equals(assmt.getCdFinalRiskLevel() ) 
								|| CSDMRLVL_VERYHIGH.equals(assmt.getCdFinalRiskLevel() ) ) ) {
					eligible = true;
					break;
				}
		    }
	    }
	}
	return eligible;
}


//Warranty Defect Fix - 10702 - To Replace the Carriage Return /Line Feed with Break Tag
	public String formatCarriageLineFeedValue(String txtToFormat) {		
		String[] txtConcurrently = null;
		if (!ObjectUtils.isEmpty(txtToFormat)) {
			txtConcurrently = txtToFormat.split("\r\n");
		}
		StringBuffer txtConcrBuf = new StringBuffer();
		if (!ObjectUtils.isEmpty(txtConcurrently)) {
			for (String txtConcr : txtConcurrently) {
				txtConcrBuf.append(txtConcr);
				txtConcrBuf.append("</br>");
			}
		}
		return txtConcrBuf.toString();
	}	
	
	
	//Warranty Defect Fix - 10702 - To Replace the Line Feed with Break Tag
		public String formatLineFeedValue(String txtToFormat) {		
			String[] txtConcurrently = null;
			if (!ObjectUtils.isEmpty(txtToFormat)) {
				txtConcurrently = txtToFormat.split("\n");
			}
			StringBuffer txtConcrBuf = new StringBuffer();
			if (!ObjectUtils.isEmpty(txtConcurrently)) {
				for (String txtConcr : txtConcurrently) {
					txtConcrBuf.append(txtConcr);
					txtConcrBuf.append("</br>");
				}
			}
			return txtConcrBuf.toString();
		}	
		
		
	    
		/**
		* populate Emergency Assistance getEaResponse artf152702
		*/
		private String getEaResponse( String eaResponse )
		{
			return !TypeConvUtil.isNullOrEmpty(eaResponse) ? 
					lookupDao.simpleDecode(CodesConstant.CINVACAN, eaResponse) : ServiceConstants.SPACE + ServiceConstants.SPACE;
		}

	/**
	 * QCR 62702
	 * @param cpsInvContactSdmSafetyAssessDto
	 * @return
	 */
	private boolean showSdm(CpsInvContactSdmSafetyAssessDto cpsInvContactSdmSafetyAssessDto) {
		boolean showSdm = false;
		Date dtSDMRemoval=codesDao.getAppRelDate(ServiceConstants.CRELDATE_SDM_REMOVAL_2020);
		if (!ObjectUtils.isEmpty(cpsInvContactSdmSafetyAssessDto)) {
			if(!ObjectUtils.isEmpty(cpsInvContactSdmSafetyAssessDto.getDtAssmtCompl())){
				if (cpsInvContactSdmSafetyAssessDto.getDtAssmtCompl().compareTo(dtSDMRemoval)<0){
					showSdm = true;
				}
			} else {
				if(!ObjectUtils.isEmpty(cpsInvContactSdmSafetyAssessDto.getDtOccured())
						&& !ObjectUtils.isEmpty(cpsInvContactSdmSafetyAssessDto.getDtStageClose())
						&& cpsInvContactSdmSafetyAssessDto.getDtOccured().compareTo(dtSDMRemoval)<0
						&& cpsInvContactSdmSafetyAssessDto.getDtStageClose().compareTo(dtSDMRemoval)<0){
					showSdm = true;
				}else{
					showSdm = false;
				}
			}
		}
		return showSdm;
	}


}
