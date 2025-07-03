package us.tx.state.dfps.service.forms.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.arinvconclusion.dto.ArEaEligibilityDto;
import us.tx.state.dfps.arinvconclusion.dto.PCSPDto;
import us.tx.state.dfps.service.arreport.dto.ArRelationshipsDto;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.CpsChecklistItemDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvAllegDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvComDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvContactSdmSafetyAssessDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvCrimHistDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvIntakePersonPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvRiskDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvSdmSafetyRiskDto;
import us.tx.state.dfps.service.cpsinv.dto.ServRefDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.lookup.dto.MessageAttribute;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CpsInvReportPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for Form CpsInvReport Apr 9, 2018- 10:53:14 AM © 2017 Texas Department
 * of Family and Protective Services
 *  * **********  Change History *********************************
 * 07/13/2020 thompswa artf159096 : refactor emergency assist
 * 11/30/2020 thompswa artf147275 get the Intake Received date from the StageRtrvOutDto intake object
 */
@Component
public class CpsInvReportPrefillData extends DocumentServiceUtil {

	@Autowired
	LookupService lookupService;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	private CodesDao codesDao;
	
	public static final String REPORTER = "(reporter)";

	private static final String RISK_NOT_INDICATED = "Emergency Assistance eligibility does not need to be determined since risk is not indicated.";


	public CpsInvReportPrefillData() {
		super();
	}
	private static final Logger logger = Logger.getLogger(CpsInvReportPrefillData.class);

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		CpsInvReportDto cpsInvReportDto = (CpsInvReportDto) parentDtoobj;
		//Map<Integer, MessageAttribute> messages = lookupService.getMessages();

		if (ObjectUtils.isEmpty(cpsInvReportDto.getCpsInvstDetailList())) {
			cpsInvReportDto.setCpsInvstDetailList(new ArrayList<CpsInvstDetailDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getEmployeePersPhNameDto())) {
			cpsInvReportDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetRemovalsList())) {
			cpsInvReportDto.setGetRemovalsList(new ArrayList<CpsInvComDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetContactNamesList())) {
			cpsInvReportDto.setGetContactNamesList(new ArrayList<CpsInvComDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetPrincipalsList())) {
			cpsInvReportDto.setGetPrincipalsList(new ArrayList<CpsInvPrincipalDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetCriminalHistoryList())) {
			cpsInvReportDto.setGetCriminalHistoryList(new ArrayList<CpsInvCrimHistDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetRiskAssessmentList())) {
			cpsInvReportDto.setGetRiskAssessmentList(new ArrayList<CpsInvRiskDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetSafetyAssessmentList())) {
			cpsInvReportDto.setGetSafetyAssessmentList(new ArrayList<CpsInvSdmSafetyRiskDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetRiskAreaList())) {
			cpsInvReportDto.setGetRiskAreaList(new ArrayList<CpsInvSdmSafetyRiskDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetRiskFactorsList())) {
			cpsInvReportDto.setGetRiskFactorsList(new ArrayList<CpsInvSdmSafetyRiskDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetSafetyFactorsList())) {
			cpsInvReportDto.setGetSafetyFactorsList(new ArrayList<CpsInvSdmSafetyRiskDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetSdmQaList())) {
			cpsInvReportDto.setGetSdmQaList(new ArrayList<CpsInvSdmSafetyRiskDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetPrincipalsHistoryList())) {
			cpsInvReportDto.setGetPrincipalsHistoryList(new ArrayList<CpsInvIntakePersonPrincipalDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetIntakesList())) {
			cpsInvReportDto.setGetIntakesList(new ArrayList<CpsInvIntakePersonPrincipalDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetContactsp1List())) {
			cpsInvReportDto.setGetContactsp1List(new ArrayList<CpsInvContactSdmSafetyAssessDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetSdmSafetyAssessmentsList())) {
			cpsInvReportDto.setGetSdmSafetyAssessmentsList(new ArrayList<CpsInvContactSdmSafetyAssessDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetAllegationsList())) {
			cpsInvReportDto.setGetAllegationsList(new ArrayList<CpsInvAllegDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetPrnInvAllegationsList())) {
			cpsInvReportDto.setGetPrnInvAllegationsList(new ArrayList<CpsInvAllegDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetRelationshipsList())) {
			cpsInvReportDto.setGetRelationshipsList(new ArrayList<ArRelationshipsDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetSafetyPlacementsList())) {
			cpsInvReportDto.setGetSafetyPlacementsList(new ArrayList<PCSPDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetPersonSplInfoListPrin())) {
			cpsInvReportDto.setGetPersonSplInfoListPrin(new ArrayList<CpsInvIntakePersonPrincipalDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetPersonSplInfoListCol())) {
			cpsInvReportDto.setGetPersonSplInfoListCol(new ArrayList<CpsInvIntakePersonPrincipalDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getServRefDto())) {
			cpsInvReportDto.setServRefDto(new ServRefDto());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getPcspList())) {
			cpsInvReportDto.setPcspList(new ArrayList<PcspDto>());
		}
		if (ObjectUtils.isEmpty(cpsInvReportDto.getEaEligibilityList())) {
			cpsInvReportDto.setEaEligibilityList(new ArrayList<ArEaEligibilityDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// for bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		// civ3646 safetyAssessment

		for (CpsInvSdmSafetyRiskDto cpsInvSdmSafetyRiskDto : cpsInvReportDto.getGetSafetyAssessmentList()) {
			if (cpsInvSdmSafetyRiskDto.getIdEvent() > 0 || cpsInvReportDto.getIsLegacySafetyInterval()) {
				FormDataGroupDto safetyDecisionGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SA,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(safetyDecisionGroup);

				List<BookmarkDto> bookmarkSafetyDecisionList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkSafetyDecisionString = createBookmark(BookmarkConstants.SA_DECISION,
						cpsInvSdmSafetyRiskDto.getCdSafetyDecision());
				bookmarkSafetyDecisionList.add(bookmarkSafetyDecisionString);
				safetyDecisionGroup.setBookmarkDtoList(bookmarkSafetyDecisionList);
			}
		}

		// populateMergedIntake getIntakes

		for (CpsInvIntakePersonPrincipalDto cpsInvIntakePersonPrincipalDto : cpsInvReportDto.getGetIntakesList()) {
			FormDataGroupDto intakeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_MERGE_INTAKE_NARR,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(intakeGroup);
			List<BookmarkDto> bookmarkIntakeList = new ArrayList<BookmarkDto>();
			List<BlobDataDto> bookmarkBlobDataList = new ArrayList<BlobDataDto>();

			BookmarkDto bookmarkDtStageStart = createBookmark(BookmarkConstants.DATE_STAGE_START,
					cpsInvIntakePersonPrincipalDto.getDtStageStart());
			bookmarkIntakeList.add(bookmarkDtStageStart);
			BookmarkDto bookmarkIntakePersRelIntString = createBookmarkWithCodesTable(BookmarkConstants.MERGED_REL_INT,
					cpsInvIntakePersonPrincipalDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
			bookmarkIntakeList.add(bookmarkIntakePersRelIntString);

			BookmarkDto bookmarkDtStageType = createBookmark(BookmarkConstants.STAGE_TYPE,
					cpsInvIntakePersonPrincipalDto.getCdStageType());
			bookmarkIntakeList.add(bookmarkDtStageType);
			BookmarkDto bookmarkDtStageName = createBookmark(BookmarkConstants.MERGED_REPORTER_NAME,
					cpsInvIntakePersonPrincipalDto.getNmPersonFull());
			bookmarkIntakeList.add(bookmarkDtStageName);
			BookmarkDto bookmarkDtStageNotes = createBookmark(BookmarkConstants.MERGED_REPORTER_NOTES,
					cpsInvIntakePersonPrincipalDto.getTxtStagePersNote());
			bookmarkIntakeList.add(bookmarkDtStageNotes);
			BookmarkDto bookmarkPersonId = createBookmark(BookmarkConstants.MERGED_REPORTER_ID,
					ObjectUtils.isEmpty(cpsInvIntakePersonPrincipalDto.getIdPerson())
							|| ServiceConstants.ZERO >= cpsInvIntakePersonPrincipalDto.getIdPerson()
									? ServiceConstants.ZERO : cpsInvIntakePersonPrincipalDto.getIdPerson());
			bookmarkIntakeList.add(bookmarkPersonId);
			BookmarkDto bookmarkStageNum = createBookmark(BookmarkConstants.STAGE_NUMBER,
					cpsInvIntakePersonPrincipalDto.getIdPriorStage());
			bookmarkIntakeList.add(bookmarkStageNum);

			Long id = cpsInvIntakePersonPrincipalDto.getIdPriorStage();
			BlobDataDto bookmarkBlobIdStage = createBlobData(BookmarkConstants.MERGE_NARRATIVE,
					CodesConstant.INCOMING_NARRATIVE_VIEW, id.toString());
			bookmarkBlobDataList.add(bookmarkBlobIdStage);

			intakeGroup.setBookmarkDtoList(bookmarkIntakeList);
			intakeGroup.setBlobDataDtoList(bookmarkBlobDataList);
		}

		// civ3605 Allegation Repeater Group

		for (CpsInvAllegDto cpsInvAllegDto : cpsInvReportDto.getGetAllegationsList()) {

			FormDataGroupDto allegationsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(allegationsGroup);
			List<BookmarkDto> bookmarkAllegList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkAllegDisp = createBookmark(BookmarkConstants.ALG_DISP, cpsInvAllegDto.getCdAllegDisp());
			bookmarkAllegList.add(bookmarkAllegDisp);

			BookmarkDto bookmarkCdAllegSev = createBookmarkWithCodesTable(BookmarkConstants.ALG_SEVERITY,
					cpsInvAllegDto.getCdAllegSev(), CodesConstant.CSEVERTY);
			bookmarkAllegList.add(bookmarkCdAllegSev);
			BookmarkDto bookmarkAllegType = createBookmarkWithCodesTable(BookmarkConstants.ALG_TYPE,
					cpsInvAllegDto.getCdAllegType(), CodesConstant.CABALTYP);
			bookmarkAllegList.add(bookmarkAllegType);
			//artf205003: setting full name of victim based on the first, last, middle and full name values retrieved.
			BookmarkDto bookmarkNmVicFull = createBookmark(BookmarkConstants.ALG_VIC_NAME,
					(ObjectUtils.isEmpty(cpsInvAllegDto.getNmVicFirst()) && ObjectUtils.isEmpty(cpsInvAllegDto.getNmVicLast())) ? cpsInvAllegDto.getNmVicFull() : populateName(cpsInvAllegDto.getNmVicFirst(),cpsInvAllegDto.getNmVicLast(),cpsInvAllegDto.getNmVicMiddle()));
			bookmarkAllegList.add(bookmarkNmVicFull);
			//artf205003: setting full name of perpetrator based on the first, last, middle and full name values retrieved.
			BookmarkDto bookmarkNmPerpFull = createBookmark(BookmarkConstants.ALG_PERP_NAME,
					(ObjectUtils.isEmpty(cpsInvAllegDto.getNmPerpFirst()) && ObjectUtils.isEmpty(cpsInvAllegDto.getNmPerpLast()))? cpsInvAllegDto.getNmPerpFull() : populateName(cpsInvAllegDto.getNmPerpFirst(),cpsInvAllegDto.getNmPerpLast(),cpsInvAllegDto.getNmPerpMiddle()));
			bookmarkAllegList.add(bookmarkNmPerpFull);

			if (!ObjectUtils.isEmpty(cpsInvAllegDto.getDtPersonDeath())) {
				BookmarkDto bookmarkChildFatality = createBookmark(BookmarkConstants.ALG_CHILD_FATALITY,
						cpsInvAllegDto.getCdChildFatality());
				bookmarkAllegList.add(bookmarkChildFatality);
			} else {
				BookmarkDto bookmarkChildFatality = createBookmark(BookmarkConstants.ALG_CHILD_FATALITY,
						ServiceConstants.NOT_APPLICABLE);
				bookmarkAllegList.add(bookmarkChildFatality);
			}

			BookmarkDto bookmarkTxtDispSev = createBookmark(BookmarkConstants.ALG_DISPSTN_SEVERITY,
					formatTextValue(cpsInvAllegDto.getTxtDispSev()));
			bookmarkAllegList.add(bookmarkTxtDispSev);

			allegationsGroup.setBookmarkDtoList(bookmarkAllegList);

		}

		// civ3601 Reporter Repeater Group // Person List - Reporter

		if (!ObjectUtils.isEmpty(cpsInvReportDto.getGetPersonSplInfoListPrin())) {
			FormDataGroupDto reporterGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_REPORTER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(reporterGroup);
			List<BookmarkDto> bookmarkReporterList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkCdStagePersRelInt = createBookmarkWithCodesTable(
					BookmarkConstants.REPORTER_RELATIONSHIP,
					cpsInvReportDto.getGetPersonSplInfoListPrin().get(0).getCdStagePersRelInt(),
					CodesConstant.CRPTRINT);
			bookmarkReporterList.add(bookmarkCdStagePersRelInt);
			BookmarkDto bookmarkCdPersonSex = createBookmarkWithCodesTable(BookmarkConstants.REPORTER_SEX,
					cpsInvReportDto.getGetPersonSplInfoListPrin().get(0).getCdPersonSex(), CodesConstant.CSEX);
			bookmarkReporterList.add(bookmarkCdPersonSex);
			
			BookmarkDto bookmarkRepPersonFull = createBookmark(BookmarkConstants.REPORTER_NAME,
					REPORTER.equalsIgnoreCase(cpsInvReportDto.getGetPersonSplInfoListPrin().get(0).getNmPersonFull())
							? ServiceConstants.EMPTY_STRING
							: cpsInvReportDto.getGetPersonSplInfoListPrin().get(0).getNmPersonFull());
			bookmarkReporterList.add(bookmarkRepPersonFull);
			BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.REPORTER_PID,
					cpsInvReportDto.getGetPersonSplInfoListPrin().get(0).getIdPerson());
			bookmarkReporterList.add(bookmarkIdPerson);
			BookmarkDto bookmarkTxtStagePersNote = createBookmark(BookmarkConstants.REPORTER_NOTES,
					cpsInvReportDto.getGetPersonSplInfoListPrin().get(0).getTxtStagePersNote());
			bookmarkReporterList.add(bookmarkTxtStagePersNote);

			reporterGroup.setBookmarkDtoList(bookmarkReporterList);
		}

		// populate Person List, Principals section.

		for (CpsInvPrincipalDto cpsInvPrincipalDto : cpsInvReportDto.getGetPrincipalsList()) {
			FormDataGroupDto principalGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PRINCIPALS,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(principalGroup);

			List<FormDataGroupDto> principalsGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkPrincipalList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkRepPersonFull = createBookmark(BookmarkConstants.PRN_NAME,
					cpsInvPrincipalDto.getNmPersonFull());
			bookmarkPrincipalList.add(bookmarkRepPersonFull);
			BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.PRN_PERSON_ID,
					cpsInvPrincipalDto.getIdPerson());
			bookmarkPrincipalList.add(bookmarkIdPerson);
			BookmarkDto bookmarkSsn = createBookmark(BookmarkConstants.PRN_SSN, cpsInvPrincipalDto.getNbrPersId());
			bookmarkPrincipalList.add(bookmarkSsn);
			BookmarkDto bookmarkCdStagePersRelInt = createBookmarkWithCodesTable(BookmarkConstants.PRN_RELATIONSHIP,
					cpsInvPrincipalDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
			bookmarkPrincipalList.add(bookmarkCdStagePersRelInt);
			BookmarkDto bookmarkCdStagePersRole = createBookmarkWithCodesTable(BookmarkConstants.PRN_STAGE_ROLE,
					cpsInvPrincipalDto.getCdStagePersRole(), CodesConstant.CROLEALL);
			bookmarkPrincipalList.add(bookmarkCdStagePersRole);

			BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.PRN_DOB,
					DateUtils.stringDt(cpsInvPrincipalDto.getDtPersonBirth()));
			bookmarkPrincipalList.add(bookmarkDtPersonBirth);
			BookmarkDto bookmarkDtPersonDeath = createBookmark(BookmarkConstants.PRN_DOD,
					DateUtils.stringDt(cpsInvPrincipalDto.getDtPersonDeath()));
			bookmarkPrincipalList.add(bookmarkDtPersonDeath);

			BookmarkDto bookmarkPrnAge = createBookmark(BookmarkConstants.PRN_AGE,
					cpsInvPrincipalDto.getNbrPersonAge());
			bookmarkPrincipalList.add(bookmarkPrnAge);
			BookmarkDto bookmarkPrnDeathCode = createBookmarkWithCodesTable(BookmarkConstants.PRN_DEATH_CODE,
					cpsInvPrincipalDto.getCdPersonDeath(), CodesConstant.CRSNDTH2);
			bookmarkPrincipalList.add(bookmarkPrnDeathCode);
			BookmarkDto bookmarkPrnSex = createBookmarkWithCodesTable(BookmarkConstants.PRN_SEX,
					cpsInvPrincipalDto.getCdPersonSex(), CodesConstant.CSEX);
			bookmarkPrincipalList.add(bookmarkPrnSex);
			BookmarkDto bookmarkPrnRace = createBookmark(BookmarkConstants.PRN_RACE, cpsInvPrincipalDto.getPersRace());
			bookmarkPrincipalList.add(bookmarkPrnRace);
			BookmarkDto bookmarkPrnEthnicity = createBookmarkWithCodesTable(BookmarkConstants.PRN_ETHNICITY,
					cpsInvPrincipalDto.getCdEthn(), CodesConstant.CINDETHN);
			bookmarkPrincipalList.add(bookmarkPrnEthnicity);
			BookmarkDto bookmarkPrnAddrLn1 = createBookmark(BookmarkConstants.PRN_ADDR_LINE_1,
					cpsInvPrincipalDto.getAddrPersStLn1());
			bookmarkPrincipalList.add(bookmarkPrnAddrLn1);
			BookmarkDto bookmarkPrnAddrCity = createBookmark(BookmarkConstants.PRN_ADDR_CITY,
					cpsInvPrincipalDto.getAddrPersCity());
			bookmarkPrincipalList.add(bookmarkPrnAddrCity);
			BookmarkDto bookmarkPrnAddrSt = createBookmarkWithCodesTable(BookmarkConstants.PRN_ADDR_ST,
					cpsInvPrincipalDto.getCdPersState(), CodesConstant.CSTATE);
			bookmarkPrincipalList.add(bookmarkPrnAddrSt);
			BookmarkDto bookmarkPrnAddrZip = createBookmark(BookmarkConstants.PRN_ADDR_ZIP,
					cpsInvPrincipalDto.getPersZip());
			bookmarkPrincipalList.add(bookmarkPrnAddrZip);

			principalGroup.setBookmarkDtoList(bookmarkPrincipalList);
			principalGroup.setFormDataGroupList(principalsGroupList);

			for (CpsInvComDto cpsInvComDto : cpsInvReportDto.getGetRemovalsList()) {
				if (cpsInvComDto.getIdPerson().equals(cpsInvPrincipalDto.getIdPerson())) {
					FormDataGroupDto principalRemovalGroup = createFormDataGroup(
							FormGroupsConstants.TMPLAT_PRN_DT_REMOVAL, FormGroupsConstants.TMPLAT_PRINCIPALS);
					
					List<BookmarkDto> bookmarkPrincipalRemovalList = new ArrayList<BookmarkDto>();
					if (!ObjectUtils.isEmpty(cpsInvComDto.getDtRemoval())) {
						BookmarkDto bookmarkDtRemoval = createBookmark(BookmarkConstants.PRN_DT_REMOVAL,
								DateUtils.stringDt(cpsInvComDto.getDtRemoval()));
						bookmarkPrincipalRemovalList.add(bookmarkDtRemoval);
					}
					principalRemovalGroup.setBookmarkDtoList(bookmarkPrincipalRemovalList);
					principalsGroupList.add(principalRemovalGroup);
				}
			}

			// no characteristics group
			if (ServiceConstants.TWO.equals(cpsInvPrincipalDto.getCdPersChar())) {
				FormDataGroupDto noCharGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_NONE,
						FormGroupsConstants.TMPLAT_PRINCIPALS);
				principalsGroupList.add(noCharGroupDto);
			}

			// characteristics groups
			else {
				for (CharacteristicsDto charDto : cpsInvReportDto.getGetCharacteristicsDtoList()) {
					// investigation group
					// Warranty Defect - 12397 - Added Comparison with Person Id and Null Pointer Check for Character Code
					if (ServiceConstants.CCH.equals(charDto.getCdCharacCategory()) && cpsInvPrincipalDto.getIdPerson().equals(charDto.getIdpersonId())
							&& !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
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
					// Warranty Defect - 12397 - Added Comparison with Person Id and Null Pointer Check for Character Code
					else if (ServiceConstants.CPL.equals(charDto.getCdCharacCategory()) && cpsInvPrincipalDto.getIdPerson().equals(charDto.getIdpersonId())
							&& !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
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
					// Warranty Defect - 12397 - Added Comparison with Person Id and Null Pointer Check for Character Code
					else if (ServiceConstants.CASE_SPECIAL_REQUEST_TLETS.equals(charDto.getCdCharacCategory()) && cpsInvPrincipalDto.getIdPerson().equals(charDto.getIdpersonId())
							&& !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
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
					// Warranty Defect - 12397 - Added Comparison with Person Id and Null Pointer Check for Character Code
					else if (ServiceConstants.APS_CHARACTERISTIC.equals(charDto.getCdCharacCategory()) && cpsInvPrincipalDto.getIdPerson().equals(charDto.getIdpersonId())
							&& !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
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

		}

		// populate Person List, Collaterals section. populateCollaterals

		
		for (CpsInvIntakePersonPrincipalDto colDto : cpsInvReportDto.getGetPersonSplInfoListCol()) {
			
			FormDataGroupDto colGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COLLATERALS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkColList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkColName = createBookmark(BookmarkConstants.COL_NAME,
					REPORTER.equalsIgnoreCase(colDto.getNmPersonFull()) ? ServiceConstants.EMPTY_STRING
							: colDto.getNmPersonFull());
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
		
		

		// populate Abuse Neglect History section.

		for (CpsInvRiskDto cpsInvRiskDto : cpsInvReportDto.getGetRiskAssessmentList()) {
			if (cpsInvReportDto.getIsLegacySafetyInterval()
					|| ServiceConstants.ONE_LONG < cpsInvRiskDto.getNbrVersion()) {
				FormDataGroupDto abuseNeglectHistoryGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ABUSE_SEARCH,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(abuseNeglectHistoryGroup);

				List<BookmarkDto> abuseNeglectList = new ArrayList<BookmarkDto>();
				abuseNeglectHistoryGroup.setBookmarkDtoList(abuseNeglectList);

				for (CpsInvSdmSafetyRiskDto cpsInvSdmSafetyRiskDto : cpsInvReportDto.getGetSafetyAssessmentList()) {
					if (ServiceConstants.ZERO < cpsInvSdmSafetyRiskDto.getIdStage()
							&& cpsInvReportDto.getIsLegacySafetyInterval()) {
						BookmarkDto bookmarkAbuseNeglHistCompl = createBookmark(BookmarkConstants.ABUSE_SEARCH_SAFETY,
								cpsInvSdmSafetyRiskDto.getIndAbuseNeglHistCompl());

						abuseNeglectList.add(bookmarkAbuseNeglHistCompl);
					}
				}

				if (cpsInvReportDto.getIsLegacySafetyInterval()) {
					BookmarkDto bookmarkSafety = createBookmark(BookmarkConstants.ABUSE_SEARCH_SAFETYQUESTION,
							ServiceConstants.ABUSE_SEARCH_SAFETYTEXT);
					abuseNeglectList.add(bookmarkSafety);
				}

				if (ServiceConstants.ONE_LONG < cpsInvRiskDto.getNbrVersion()) {
					BookmarkDto bookmarkAbuseNeglHistFound = createBookmarkWithCodesTable(
							BookmarkConstants.ABUSE_SEARCH_PRN, cpsInvRiskDto.getIndAbuseNeglHistFound(),
							CodesConstant.CINVACAN);
					abuseNeglectList.add(bookmarkAbuseNeglHistFound);
					BookmarkDto bookmarkAbuseNeglPrevInv = createBookmarkWithCodesTable(
							BookmarkConstants.ABUSE_SEARCH_PRN, cpsInvRiskDto.getIndAbuseNeglPrevInv(),
							CodesConstant.CINVACAN);
					abuseNeglectList.add(bookmarkAbuseNeglPrevInv);
					BookmarkDto bookmarkAbuseNeglSumm = createBookmark(BookmarkConstants.ABUSE_SEARCH_SUMMARY,
							cpsInvRiskDto.getTxtAbuseNeglSumm());
					abuseNeglectList.add(bookmarkAbuseNeglSumm);

					BookmarkDto bookmarkPQuestion = createBookmark(BookmarkConstants.ABUSE_SEARCH_PRNQUESTION,
							ServiceConstants.ABUSE_SEARCH_PRNTEXT);
					abuseNeglectList.add(bookmarkPQuestion);
					BookmarkDto bookmarkFQuestion = createBookmark(BookmarkConstants.ABUSE_SEARCH_FOUNDQUESTION,
							ServiceConstants.ABUSE_SEARCH_FOUNDTEXT);
					abuseNeglectList.add(bookmarkFQuestion);
					BookmarkDto bookmarkSQuestion = createBookmark(BookmarkConstants.ABUSE_SEARCH_SUMMARYQUESTION,
							ServiceConstants.ABUSE_SEARCH_SUMMARYTEXT);
					abuseNeglectList.add(bookmarkSQuestion);
				}
			}

		}

		// populate safety assessment.

		for (CpsInvSdmSafetyRiskDto cpsInvSdmSafetyRiskDto : cpsInvReportDto.getGetSafetyAssessmentList()) {
			if (ServiceConstants.ZERO < cpsInvSdmSafetyRiskDto.getIdStage()
					&& ServiceConstants.TWO.equals(cpsInvSdmSafetyRiskDto.getNbrVersion())) {

				FormDataGroupDto safetyAssmtGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SAFETY_ASSESSMENT,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(safetyAssmtGroup);
				List<BookmarkDto> bookmarkAssmtList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkRational = createBookmark(BookmarkConstants.SAFETY_RATIONALE,
						cpsInvSdmSafetyRiskDto.getTxtDecisionRational());
				bookmarkAssmtList.add(bookmarkRational);
				safetyAssmtGroup.setBookmarkDtoList(bookmarkAssmtList);

				for (CpsInvSdmSafetyRiskDto cpsInvSdmSafetyRiskDto1 : cpsInvReportDto.getGetSafetyFactorsList()) {

					if (ServiceConstants.ZERO < cpsInvSdmSafetyRiskDto1.getIdStage()
							&& ServiceConstants.TWO.equals(cpsInvSdmSafetyRiskDto1.getNbrVersion())) {
						FormDataGroupDto safetyFactorGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SAFETY_AREA,
								FormConstants.EMPTY_STRING);
						formDataGroupList.add(safetyFactorGroup);

						List<BookmarkDto> bookmarkFactorList = new ArrayList<BookmarkDto>();

						BookmarkDto bookmarkFactor1 = createBookmark(BookmarkConstants.SAFETY_AREA_TEXT,
								cpsInvSdmSafetyRiskDto1.getTxtArea());
						bookmarkFactorList.add(bookmarkFactor1);
						BookmarkDto bookmarkFactor2 = createBookmark(BookmarkConstants.SAFETY_AREA_DISCUSSION,
								cpsInvSdmSafetyRiskDto1.getTxtDiscussFac());
						bookmarkFactorList.add(bookmarkFactor2);
						safetyAssmtGroup.setBookmarkDtoList(bookmarkFactorList);
					}

				}

			}

		}

		// populate populate Legacy Risk Assessment Statement.
		if (!ObjectUtils.isEmpty(cpsInvReportDto.getGenericCaseInfoDto().getDtStageClose()) && DateUtils.isBefore(
				cpsInvReportDto.getGenericCaseInfoDto().getDtStageClose(), ServiceConstants.DEC_1_IMPACT_DATE)) {

			FormDataGroupDto legacyRiskAssessmentGroup = createFormDataGroup(
					FormGroupsConstants.TMPLAT_RISK_ASSESSMENT_0, FormConstants.EMPTY_STRING);
			formDataGroupList.add(legacyRiskAssessmentGroup);

		} else if (!ObjectUtils.isEmpty(cpsInvReportDto.getGenericCaseInfoDto().getDtStageClose())
				&& cpsInvReportDto.getIsLegacyRisk()) {
			FormDataGroupDto preSdmRiskAssessmentGroup = createFormDataGroup(
					FormGroupsConstants.TMPLAT_RISK_FINDING_STAGE, FormConstants.EMPTY_STRING);
			formDataGroupList.add(preSdmRiskAssessmentGroup);

			for (CpsInvRiskDto cpsInvRiskDto : cpsInvReportDto.getGetRiskAssessmentList()) {
				if (ServiceConstants.NO_INTRANET_RISK_ASSMT.equals(cpsInvRiskDto.getIndRiskAssmtIntranet())) {

					FormDataGroupDto riskAssmtFindingGroup = createFormDataGroup(
							FormGroupsConstants.TMPLAT_RISK_FINDING, FormGroupsConstants.TMPLAT_RISK_FINDING_STAGE);
					formDataGroupList.add(riskAssmtFindingGroup);

					List<BookmarkDto> bookmarkAssmtList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkAssesType = createBookmarkWithCodesTable(
							BookmarkConstants.RISK_FINDING_POST_LEGACY, cpsInvRiskDto.getCdRiskAssmtRiskFind(),
							CodesConstant.CCRSKFND);
					bookmarkAssmtList.add(bookmarkAssesType);

					riskAssmtFindingGroup.setBookmarkDtoList(bookmarkAssmtList);

				}

				FormDataGroupDto riskRationaleGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RISK_RATIONALE,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(riskRationaleGroup);

				List<BookmarkDto> bookmarkRationalList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkRational = createBookmark(BookmarkConstants.RISK_RATIONALE,
						cpsInvRiskDto.getTxtFindRational());
				bookmarkRationalList.add(bookmarkRational);

				riskRationaleGroup.setBookmarkDtoList(bookmarkRationalList);
			}

			// area concern

			for (CpsInvSdmSafetyRiskDto cpsInvSdmSafetyRiskDto : cpsInvReportDto.getGetRiskAreaList()) {
				FormDataGroupDto riskAreasGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RISK,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(riskAreasGroup);

				List<FormDataGroupDto> areasGroupList = new ArrayList<FormDataGroupDto>();

				List<BookmarkDto> bookmarkAreasList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkArea = createBookmark(BookmarkConstants.RISK_AREA_CONCERN,
						cpsInvSdmSafetyRiskDto.getTxtArea());
				bookmarkAreasList.add(bookmarkArea);
				
				BookmarkDto bookmarkScale = createBookmarkWithCodesTable(BookmarkConstants.RISK_SCALE,
						cpsInvSdmSafetyRiskDto.getCdRiskAreaConcernScale(), CodesConstant.CRISKSOC);
				bookmarkAreasList.add(bookmarkScale);

				riskAreasGroup.setBookmarkDtoList(bookmarkAreasList);
				riskAreasGroup.setFormDataGroupList(areasGroupList);

				for (CpsInvSdmSafetyRiskDto cpsInvSdmSafetyRiskDto1 : cpsInvReportDto.getGetRiskFactorsList()) {

					if (ServiceConstants.YES.equals(cpsInvSdmSafetyRiskDto1.getTxtFac())
							&& cpsInvSdmSafetyRiskDto1.getIdRiskArea().equals(cpsInvSdmSafetyRiskDto.getIdRiskArea())) {

						FormDataGroupDto riskFactorGroup = createFormDataGroup(
								FormGroupsConstants.TMPLAT_RISK_FACTOR_TEXT, FormGroupsConstants.TMPLAT_RISK);
						areasGroupList.add(riskFactorGroup);

						List<BookmarkDto> bookmarkFactorList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkFac = createBookmark(BookmarkConstants.RISK_FACTOR_TEXT,
								cpsInvSdmSafetyRiskDto1.getTxtFac());
						bookmarkFactorList.add(bookmarkFac);
						BookmarkDto bookmarkFac1 = createBookmarkWithCodesTable(BookmarkConstants.RISK_FACTOR_RESPONSE,
								cpsInvSdmSafetyRiskDto1.getCdRiskFac(), CodesConstant.CRSKRESP);
						bookmarkFactorList.add(bookmarkFac1);

						riskFactorGroup.setBookmarkDtoList(bookmarkFactorList);

					}
				}

				FormDataGroupDto riskConcernGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RISK_CONCERN_TEXT,
						FormGroupsConstants.TMPLAT_RISK);
				areasGroupList.add(riskConcernGroup);

				List<BookmarkDto> bookmarkConcernList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkFac = createBookmark(BookmarkConstants.RISK_CONCERN_TEXT,
						cpsInvSdmSafetyRiskDto.getTxtConcernScale());
				bookmarkConcernList.add(bookmarkFac);

				riskConcernGroup.setBookmarkDtoList(bookmarkConcernList);

			}

		}

		// populateContacts : populateSdmContacts : populateContactContacts

		for (CpsInvContactSdmSafetyAssessDto cpsInvContactSdmSafetyAssessDto : cpsInvReportDto.getGetContactsp1List()) {

			FormDataGroupDto contactsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS,
					FormConstants.EMPTY_STRING);			

			// set the linked names
			ArrayList<String> contactNamesList = new ArrayList<String>();

			for (CpsInvComDto cpsInvComDto : cpsInvReportDto.getGetContactNamesList()) {
				if (cpsInvContactSdmSafetyAssessDto.getIdEvent().toString()
						.equals(cpsInvComDto.getIdEvent().toString())) {
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
						.equals(cpsInvReportDto.getGenericCaseInfoDto().getIdStage())
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

				if(showSDM(cpsInvContactSdmSafetyAssessDto)){
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
				contactsMergeSdmGroupList.add(contactsSdmNamesGroup);

				if (!ObjectUtils.isEmpty(contactNamesList)) {
					for (String sdmName : contactNamesList) {
						contactNames.append(sdmName);
						if (++count < contactNamesList.size()) {
							contactNames.append(ServiceConstants.CAPSCOMMA_SPACE);
						}
					}
				}
				List<BookmarkDto> bookmarkSdmNamesList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkName = createBookmark(BookmarkConstants.CONTACTS_SDM_NAMES,
						contactNames.toString());
				bookmarkSdmNamesList.add(bookmarkName);

				contactsSdmNamesGroup.setBookmarkDtoList(bookmarkSdmNamesList);
				
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

				for (CpsInvSdmSafetyRiskDto cpsInvSdmSafetyRiskDto : cpsInvReportDto.getGetSdmQaList()) {

					if (cpsInvContactSdmSafetyAssessDto.getIdEvent().toString()
							.equals(cpsInvSdmSafetyRiskDto.getIdEvent().toString())) {

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
				csdmGroup.setFormDataGroupList(contactsMergeSdmGroupList);

				// display merge stage if applicable

				if (!cpsInvContactSdmSafetyAssessDto.getIdStage()
						.equals(cpsInvReportDto.getGenericCaseInfoDto().getIdStage())
						&& ServiceConstants.CFACTYP2_97.equals(cpsInvContactSdmSafetyAssessDto.getCdStageRsnCLosed())
						&& !cpsInvContactSdmSafetyAssessDto.getIdStage()
								.equals(cpsInvReportDto.getStageRtrvOutDto().getIdStage())) {
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
				List<BlobDataDto> blobList = new ArrayList<BlobDataDto>();

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

				BlobDataDto blobArContactsNarr = createBlobData(BookmarkConstants.CONTACTS_CON_NARRATIVE,
						BookmarkConstants.CONTACT_NARRATIVE, cpsInvContactSdmSafetyAssessDto.getIdEvent().toString());
				blobList.add(blobArContactsNarr);

				// contact names

				if (!ObjectUtils.isEmpty(contactNamesList)) {
					BookmarkDto bookmarkNamesLabel = createBookmark(BookmarkConstants.CONTACTS_CON_NAMESLABEL,
							ServiceConstants.CONTACTS_CON_NAMESLABELTEXT);
					bookmarkContactsConList.add(bookmarkNamesLabel);
					for (String sdmName : contactNamesList) {
						FormDataGroupDto contactsSdmNamesGroup = createFormDataGroup(
								FormGroupsConstants.TMPLAT_CONTACTS_CON_NAMES, FormGroupsConstants.TMPLAT_CONTACTS_CON);
						contactsMergeSdmGroupList.add(contactsSdmNamesGroup);
						List<BookmarkDto> bookmarkSdmNamesList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkName = createBookmark(BookmarkConstants.CONTACTS_CON_NAMES, sdmName);
						bookmarkSdmNamesList.add(bookmarkName);
						contactsSdmNamesGroup.setBookmarkDtoList(bookmarkSdmNamesList);

					}
				}

				csdmGroup.setBookmarkDtoList(bookmarkContactsConList);
				csdmGroup.setBlobDataDtoList(blobList);
				contactsSdmGroupList.add(csdmGroup);				
			}

			
			contactsGroup.setFormDataGroupList(contactsSdmGroupList);
			formDataGroupList.add(contactsGroup);
		}

		// populateCriminalHistory Get Criminal History

		for (

		CpsInvRiskDto cpsInvRiskDto : cpsInvReportDto.getGetRiskAssessmentList()) {

			if (ServiceConstants.ZERO < cpsInvRiskDto.getNbrVersion()) {
				FormDataGroupDto crimHistEffectGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHIST_EFFECT,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkHistEffectGroupList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCrimHistEff = createBookmark(BookmarkConstants.CHIST_EFFECT,
						cpsInvRiskDto.getTxtCrimHistEff());
				bookmarkHistEffectGroupList.add(bookmarkCrimHistEff);
				formDataGroupList.add(crimHistEffectGroup);
				crimHistEffectGroup.setBookmarkDtoList(bookmarkHistEffectGroupList);
			}
		}

		for (CpsInvCrimHistDto crimHistDto : cpsInvReportDto.getGetCriminalHistoryList()) {
			FormDataGroupDto crimHistGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CRIM_HIST,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkCrimHistList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkPrnChistNmFull = createBookmark(BookmarkConstants.PRN_CHIST_NAME,
			populateName(crimHistDto.getNmPersonFirst(),crimHistDto.getNmPersonLast(),crimHistDto.getNmPersonMiddle()));
			bookmarkCrimHistList.add(bookmarkPrnChistNmFull);
			BookmarkDto bookmarkPrnChistDtSearch = createBookmark(BookmarkConstants.PRN_CHIST_DT_SEARCH,
					DateUtils.stringDt(crimHistDto.getDtRecCheckCompl()));
			bookmarkCrimHistList.add(bookmarkPrnChistDtSearch);
			BookmarkDto bookmarkPrnChistCheckType = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHIST_CHECK_TYPE,
					crimHistDto.getCdRecCheckType(), CodesConstant.CCHKTYPE);
			bookmarkCrimHistList.add(bookmarkPrnChistCheckType);
			BookmarkDto bookmarkPrnChistResult = createBookmark(BookmarkConstants.PRN_CHIST_RESULT,
					FormConstants.EMPTY_STRING);

			if (!ObjectUtils.isEmpty(crimHistDto.getCdCrimHistAction())
					|| !ObjectUtils.isEmpty(crimHistDto.getIdCrimHist())) {

				if ((StringUtils.isBlank(crimHistDto.getCdCrimHistAction()) && 0 < crimHistDto.getIdCrimHist())) {
					bookmarkPrnChistResult = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHIST_RESULT,
							crimHistDto.getCdRecCheckStatus(), CodesConstant.CCRIMSTA);
				} else if (ServiceConstants.ZERO == crimHistDto.getIdCrimHist()) {
					bookmarkPrnChistResult = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHIST_RESULT,
							crimHistDto.getCdRecCheckStatus(), CodesConstant.CCRIMSTA);
				} else if (StringUtils.isNotBlank(crimHistDto.getCdCrimHistAction())) {
					bookmarkPrnChistResult.setBookmarkData(crimHistDto.getCdCrimHistAction());
				}
			}

			if (ObjectUtils.isEmpty(crimHistDto.getIdCrimHist())) {

				bookmarkPrnChistResult = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHIST_RESULT,
						crimHistDto.getCdRecCheckStatus(), CodesConstant.CCRIMSTA);
			}

			bookmarkCrimHistList.add(bookmarkPrnChistResult);
			BookmarkDto bookmarkPrnChistAction = createBookmark(BookmarkConstants.PRN_CHIST_RESULT,
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

		// Popluate sdm safety and risk assessments
		FormDataGroupDto sdmGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SDM, FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> sdmGroupList = new ArrayList<FormDataGroupDto>();
		List<String> households = new ArrayList<String>();
		String riskLvl = null;
		for (CpsInvContactSdmSafetyAssessDto assessDto : cpsInvReportDto.getGetSdmSafetyAssessmentsList()) {
			if (StringUtils.isBlank(assessDto.getHouseholdName())) {
				assessDto.setHouseholdName(ServiceConstants.NONE.toUpperCase());
			}
			if (!households.contains(assessDto.getHouseholdName())) {
				households.add(assessDto.getHouseholdName());
			}
		}
		for (String fullname : households) {
			FormDataGroupDto sdmHshldGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SDM_HSHLD,
					FormGroupsConstants.TMPLAT_SDM);
			List<FormDataGroupDto> sdmHshldGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkSdmHshldList = new ArrayList<BookmarkDto>();
			if (!ServiceConstants.NONE.equalsIgnoreCase(fullname)) {
				BookmarkDto bookmarkSdmHshldLabel = createBookmark(BookmarkConstants.SDM_HSHLDLABEL,
						ServiceConstants.HOUSEHOLD_LABEL);
				bookmarkSdmHshldList.add(bookmarkSdmHshldLabel);
				BookmarkDto bookmarkSdmHshld = createBookmark(BookmarkConstants.SDM_HSHLD, fullname);
				bookmarkSdmHshldList.add(bookmarkSdmHshld);
			}
			for (CpsInvContactSdmSafetyAssessDto assessDto : cpsInvReportDto.getGetSdmSafetyAssessmentsList()) {
				if (fullname.equals(assessDto.getHouseholdName())) {
					FormDataGroupDto sdmAssessGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SDM_HSHLD_ASMT,
							FormGroupsConstants.TMPLAT_SDM_HSHLD);
					List<BookmarkDto> bookmarkSdmAssessList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkSdmAssmtDate = createBookmark(BookmarkConstants.SDM_HSHLD_ASMT_DATE,
							DateUtils.stringDt(assessDto.getDtAssessed()));
					bookmarkSdmAssessList.add(bookmarkSdmAssmtDate);
					BookmarkDto bookmarkSdmAssmtCompDate = createBookmark(BookmarkConstants.SDM_HSHLD_ASMT_COMPDATE,
							DateUtils.stringDt(assessDto.getDtAssmtCompl()));
					bookmarkSdmAssessList.add(bookmarkSdmAssmtCompDate);
					if (ServiceConstants.HOUSEHOLD_TYPERISK.equals(assessDto.getCdAssmtType())) {
						BookmarkDto bookmarkSdmAssmtTypeLabel = createBookmark(
								BookmarkConstants.SDM_HSHLD_ASMT_TYPELABEL, assessDto.getCdAssmtType());
						bookmarkSdmAssessList.add(bookmarkSdmAssmtTypeLabel);
					} else {
						BookmarkDto bookmarkSdmAssmtTypeLabel = createBookmark(
								BookmarkConstants.SDM_HSHLD_ASMT_TYPELABEL, ServiceConstants.HOUSEHOLD_TYPESAFE);
						bookmarkSdmAssessList.add(bookmarkSdmAssmtTypeLabel);
						BookmarkDto bookmarkSdmAssmtFinalRiskLevel = createBookmarkWithCodesTable(
								BookmarkConstants.SDM_HSHLD_ASMT_TYPE, assessDto.getCdAssmtType(),
								CodesConstant.CSDMASMT);
						bookmarkSdmAssessList.add(bookmarkSdmAssmtFinalRiskLevel);
					}
					BookmarkDto bookmarkSdmAssmtDecision = createBookmarkWithCodesTable(
							BookmarkConstants.SDM_HSHLD_ASMT_DECISION, assessDto.getCdSafetyDecision(),
							CodesConstant.CSDMDCSN);
					bookmarkSdmAssessList.add(bookmarkSdmAssmtDecision);
					riskLvl = assessDto.getCdFinalRiskLevel();
					/**
					 * Defect# 10703
					 * Description:- CPS Investigation Report issues.
					 * Fix description: The codes table corrected the same for final risk level.
					 */
					BookmarkDto bookmarkSdmAssmtFinalRiskLevel = createBookmarkWithCodesTable(
							BookmarkConstants.SDM_HSHLD_ASMT_FINALRISKLEVEL, assessDto.getCdFinalRiskLevel(),
							CodesConstant.CSDMRLVL);
					bookmarkSdmAssessList.add(bookmarkSdmAssmtFinalRiskLevel);

					sdmAssessGroup.setBookmarkDtoList(bookmarkSdmAssessList);
					sdmHshldGroupList.add(sdmAssessGroup);
				}
			}

			sdmHshldGroupDto.setBookmarkDtoList(bookmarkSdmHshldList);
			sdmHshldGroupDto.setFormDataGroupList(sdmHshldGroupList);
			sdmGroupList.add(sdmHshldGroupDto);
		}
		riskLvl = getRiskLevel(households, cpsInvReportDto);
		sdmGroupDto.setFormDataGroupList(sdmGroupList);
		formDataGroupList.add(sdmGroupDto);

		// Services and referrals
		if (!ObjectUtils.isEmpty(cpsInvReportDto.getServRefDto().getCpsChecklistDto())) {
			BookmarkDto bookmarkServRef = createBookmark(BookmarkConstants.SERVICES_REFERRALS,
					ServiceConstants.SERVICES_REFERRALS_TEXT);
			if (!ServiceConstants.Y
					.equals(cpsInvReportDto.getServRefDto().getCpsChecklistDto().getIndSvcRefChklstNoRef())) {
				bookmarkServRef.setBookmarkData(ServiceConstants.SERVICES_REFERRALS_DATE + DateUtils
						.stringDt(cpsInvReportDto.getServRefDto().getCpsChecklistDto().getDtFirstReferral()));
			}
			bookmarkNonFormGrpList.add(bookmarkServRef);
			String cdFamilyResp = cpsInvReportDto.getServRefDto().getCpsChecklistDto().getCdFamilyResponse();
			if (ServiceConstants.STRING_ONE.equals(cdFamilyResp)) {
				cdFamilyResp = ServiceConstants.SR_FAMILY_RESPONSE1;
			} else if (ServiceConstants.STRING_TWO.equals(cdFamilyResp)) {
				cdFamilyResp = ServiceConstants.SR_FAMILY_RESPONSE2;
			} else if (ServiceConstants.STRING_THREE.equals(cdFamilyResp)) {
				cdFamilyResp = ServiceConstants.SR_FAMILY_RESPONSE3;
			}
			
			BookmarkDto bookmarkSrFinalOutcome = createBookmark(BookmarkConstants.SR_FINAL_OUTCOME, cdFamilyResp);
			bookmarkNonFormGrpList.add(bookmarkSrFinalOutcome);			
			BookmarkDto bookmarkSrComments = createBookmark(BookmarkConstants.SR_COMMENTS,
					cpsInvReportDto.getServRefDto().getCpsChecklistDto().getChklstComments());
			bookmarkNonFormGrpList.add(bookmarkSrComments);
		} else {
			BookmarkDto bookmarkServRef = createBookmark(BookmarkConstants.SERVICES_REFERRALS,
					ServiceConstants.SERVICES_REFERRALS_TEXT);
			bookmarkNonFormGrpList.add(bookmarkServRef);
		}
		if (!ObjectUtils.isEmpty(cpsInvReportDto.getServRefDto().getCpsChecklistItemList())) {
			FormDataGroupDto srGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SR,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> srGroupList = new ArrayList<FormDataGroupDto>();
			for (CpsChecklistItemDto itemDto : cpsInvReportDto.getServRefDto().getCpsChecklistItemList()) {
				FormDataGroupDto srTypeGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SR_TYPE,
						FormGroupsConstants.TMPLAT_SR);
				List<BookmarkDto> bookmarkSrTypeList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkSrType = createBookmarkWithCodesTable(BookmarkConstants.SR_TYPE,
						itemDto.getCdSrvcReferred(), CodesConstant.CSRCKLST);
				bookmarkSrTypeList.add(bookmarkSrType);

				srTypeGroupDto.setBookmarkDtoList(bookmarkSrTypeList);
				srGroupList.add(srTypeGroupDto);
			}
			srGroupDto.setFormDataGroupList(srGroupList);
			formDataGroupList.add(srGroupDto);
		}

		// Get PCSP
		if (!(ObjectUtils.isEmpty(cpsInvReportDto.getGetSafetyPlacementsList())
				&& ObjectUtils.isEmpty(cpsInvReportDto.getPcspList()))) {
			FormDataGroupDto pcspGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PCSP,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> pcspGroupList = new ArrayList<FormDataGroupDto>();
			for (PcspDto pcspDto : cpsInvReportDto.getPcspList()) {
				FormDataGroupDto casePcspGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PCSP_PLCMT,
						FormGroupsConstants.TMPLAT_PCSP);
				List<FormDataGroupDto> casePcspGroupList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkCasePcspList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNmChild = createBookmark(BookmarkConstants.CHILD_SAFETY_CHILD_NAME,
						pcspDto.getChildName());
				bookmarkCasePcspList.add(bookmarkNmChild);
				BookmarkDto bookmarkNmCaregiver = createBookmark(BookmarkConstants.CHILD_SAFETY_CAREGIVER_NAME,
						pcspDto.getCaregiverName());
				bookmarkCasePcspList.add(bookmarkNmCaregiver);
				BookmarkDto bookmarkDtStart = createBookmark(BookmarkConstants.CHILD_SAFETY_START_DATE,
						DateUtils.stringDt(pcspDto.getDtStart()));
				bookmarkCasePcspList.add(bookmarkDtStart);
				// Warranty Defect - 12540 - To prevent the PCSP Default End Date being Printed
				if(!DateUtils.stringDt(pcspDto.getDtEnd()).equals(ServiceConstants.STAGE_OPEN_DT))
				{
				BookmarkDto bookmarkDtEnd = createBookmark(BookmarkConstants.CHILD_SAFETY_END_DATE,
						DateUtils.stringDt(pcspDto.getDtEnd()));
				bookmarkCasePcspList.add(bookmarkDtEnd);
				}				
				BookmarkDto bookmarkEndReason = createBookmarkWithCodesTable(BookmarkConstants.CHILD_SAFETY_END_REASON,
						pcspDto.getEndReason(), CodesConstant.PCSPEND);
				bookmarkCasePcspList.add(bookmarkEndReason);
				BookmarkDto bookmarkComments = createBookmark(BookmarkConstants.CHILD_SAFETY_COMMENTS,
						cpsInvReportDto.getPcspEndReasonList().get(cpsInvReportDto.getPcspList().indexOf(pcspDto)));
				bookmarkCasePcspList.add(bookmarkComments);

				int pcspExtnSize = pcspDto.getPcspExtnDtlDtoList().size();
				for (int i = pcspExtnSize - 1; i >= 0; i--) {
					FormDataGroupDto pcspExtnGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PCSP_PLCMT_EXTNS,
							FormGroupsConstants.TMPLAT_PCSP_PLCMT);
					List<BookmarkDto> bookmarkPcspExtnList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkRenewalDt = createBookmark(BookmarkConstants.PCSP_EXTN_RENEWAL_DT,
							DateUtils.stringDt(pcspDto.getPcspExtnDtlDtoList().get(i).getRenewalDate()));
					bookmarkPcspExtnList.add(bookmarkRenewalDt);
					BookmarkDto bookmarkExtnExpryDt = createBookmark(BookmarkConstants.PCSP_EXTN_EXPRY_DT,
							DateUtils.stringDt(pcspDto.getPcspExtnDtlDtoList().get(i).getExtensionExpiryDate()));
					bookmarkPcspExtnList.add(bookmarkExtnExpryDt);
					BookmarkDto bookmarkCourtOrderToCont = createBookmark(BookmarkConstants.PCSP_EXTN_COURT_ORDER_TO_CONT,
							pcspDto.getPcspExtnDtlDtoList().get(i).getIndToContPcsp());
					BookmarkDto bookmarkParentAttrnyAgrmnt = createBookmark(BookmarkConstants.PCSP_PARENT_ATTRNY_AGRMNT,
							pcspDto.getPcspExtnDtlDtoList().get(i).getIndAtrnyParentAgrmnt());
					bookmarkPcspExtnList.add(bookmarkCourtOrderToCont);
					bookmarkPcspExtnList.add(bookmarkParentAttrnyAgrmnt);
					pcspExtnGroupDto.setBookmarkDtoList(bookmarkPcspExtnList);
					casePcspGroupList.add(pcspExtnGroupDto);
				}
				casePcspGroupDto.setFormDataGroupList(casePcspGroupList);
				casePcspGroupDto.setBookmarkDtoList(bookmarkCasePcspList);
				pcspGroupList.add(casePcspGroupDto);
			}
			for (PCSPDto pcspDto : cpsInvReportDto.getGetSafetyPlacementsList()) {
				FormDataGroupDto safetyPlcmtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PCSP_PLCMT,
						FormGroupsConstants.TMPLAT_PCSP);
				List<BookmarkDto> bookmarkSafetyPlcmtList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNmChild = createBookmark(BookmarkConstants.CHILD_SAFETY_CHILD_NAME,
						pcspDto.getNmPersNameFull());
				bookmarkSafetyPlcmtList.add(bookmarkNmChild);
				BookmarkDto bookmarkNmCaregiver = createBookmark(BookmarkConstants.CHILD_SAFETY_CAREGIVER_NAME,
						pcspDto.getNmPersCargvrFull());
				bookmarkSafetyPlcmtList.add(bookmarkNmCaregiver);
				BookmarkDto bookmarkDtStart = createBookmark(BookmarkConstants.CHILD_SAFETY_START_DATE,
						DateUtils.stringDt(pcspDto.getDtStart()));
				bookmarkSafetyPlcmtList.add(bookmarkDtStart);
				BookmarkDto bookmarkStatus = createBookmarkWithCodesTable(BookmarkConstants.CHILD_SAFETY_STATUS,
						pcspDto.getCdStatus(), CodesConstant.CPCSPSTA);
				bookmarkSafetyPlcmtList.add(bookmarkStatus);
				BookmarkDto bookmarkDtEnd = createBookmark(BookmarkConstants.CHILD_SAFETY_END_DATE,
						DateUtils.stringDt(pcspDto.getDtEnd()));
				bookmarkSafetyPlcmtList.add(bookmarkDtEnd);
				BookmarkDto bookmarkEndReason = createBookmarkWithCodesTable(BookmarkConstants.CHILD_SAFETY_END_REASON,
						pcspDto.getCdEndRsn(), CodesConstant.CPCSPRSN);
				bookmarkSafetyPlcmtList.add(bookmarkEndReason);
				BookmarkDto bookmarkCaregiverManual = createBookmark(BookmarkConstants.CHILD_SAFETY_CAREGIVER_MANUAL,
						pcspDto.getIndCargvrManual());
				bookmarkSafetyPlcmtList.add(bookmarkCaregiverManual);
				BookmarkDto bookmarkComments = createBookmark(BookmarkConstants.CHILD_SAFETY_COMMENTS,
						pcspDto.getPCSPComments());
				bookmarkSafetyPlcmtList.add(bookmarkComments);

				safetyPlcmtGroupDto.setBookmarkDtoList(bookmarkSafetyPlcmtList);
				pcspGroupList.add(safetyPlcmtGroupDto);
			}
			pcspGroupDto.setFormDataGroupList(pcspGroupList);
			formDataGroupList.add(pcspGroupDto);
		}

		// populate Emergency Assistance section.
		String determ = null;
		String eaCpr = null;
		String eaRra = null;
		if (!ServiceConstants.CITIZENSHIP_STATUS_TMR.equalsIgnoreCase(cpsInvReportDto.getCitizenshipStatus())) {
			//artf162615 display the questions event when there is old risk assessment
			if (cpsInvReportDto.getIsEligible() // artf159096
					|| (!ObjectUtils.isEmpty(cpsInvReportDto.getGetRiskAssessmentList())
					&& (ServiceConstants.IV_E_FIN_AND_MED.equals(
					cpsInvReportDto.getGetRiskAssessmentList().get(0).getCdRiskAssmtRiskFind())
					|| ServiceConstants.MEDCAD_ONLY.equals(cpsInvReportDto
					.getGetRiskAssessmentList().get(0).getCdRiskAssmtRiskFind())
			)) || !ObjectUtils.isEmpty(cpsInvReportDto.getEaEligibilityList())) {
				FormDataGroupDto eaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EA,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkEaList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkEaElig = createBookmark(BookmarkConstants.EA_ELIGIBLE,
						ServiceConstants.EA_ELIGIBLETEXT);
				bookmarkEaList.add(bookmarkEaElig);
				BookmarkDto bookmarkEaIncome = createBookmarkWithCodesTable(BookmarkConstants.EA_INCOME,
						cpsInvReportDto.getCpsInvstDetailList().get(0).getCdCpsInvstDtlFamIncm(), CodesConstant.CEAFINCM);
				bookmarkEaList.add(bookmarkEaIncome);
				bookmarkEaList.add(createBookmarkWithCodesTable(BookmarkConstants.EA_ARC_QUESTION,
						CodesConstant.CELGSTMT_ARC, CodesConstant.CELGSTMT));
				bookmarkEaList.add(createBookmarkWithCodesTable(BookmarkConstants.EA_CPR_QUESTION,
						CodesConstant.CELGSTMT_CPR, CodesConstant.CELGSTMT));
				bookmarkEaList.add(createBookmarkWithCodesTable(BookmarkConstants.EA_RRA_QUESTION,
						CodesConstant.CELGSTMT_RRA, CodesConstant.CELGSTMT));

				for (ArEaEligibilityDto dto : cpsInvReportDto.getEaEligibilityList()) {
					if (!(CodesConstant.CELGSTMT_CPR.equals(dto.getCdEaQuestion()) ||
							CodesConstant.CELGSTMT_RRA.equals(dto.getCdEaQuestion()))) {
						bookmarkEaList.add(createBookmark(BookmarkConstants.EA_ARC_RESPONSE,
								getEaResponse(dto.getIndEaResponse())));
					} else if (!(CodesConstant.CELGSTMT_ARC.equals(dto.getCdEaQuestion()) ||
							CodesConstant.CELGSTMT_RRA.equals(dto.getCdEaQuestion()))) {
						bookmarkEaList.add(createBookmark(BookmarkConstants.EA_CPR_RESPONSE,
								getEaResponse(dto.getIndEaResponse())));
						eaCpr = dto.getIndEaResponse();
					} else if (!(CodesConstant.CELGSTMT_ARC.equals(dto.getCdEaQuestion()) ||
							CodesConstant.CELGSTMT_CPR.equals(dto.getCdEaQuestion()))) {
						bookmarkEaList.add(createBookmark(BookmarkConstants.EA_RRA_RESPONSE,
								getEaResponse(dto.getIndEaResponse())));
						eaRra = dto.getIndEaResponse();
					}

				}

				eaGroupDto.setBookmarkDtoList(bookmarkEaList);
				formDataGroupList.add(eaGroupDto);
			}

			
		}

		// SD 87002: 1.2 Forms and Reports Investigation Report
		if (ServiceConstants.CITIZENSHIP_STATUS_TMR.equalsIgnoreCase(cpsInvReportDto.getCitizenshipStatus())) {
			determ = lookupDao.simpleDecodeSafe(CodesConstant.CEACONCL, ServiceConstants.U);
		} else if(riskLvl != null && (riskLvl.equals("HIGH") || riskLvl.equals("VERYHIGH"))) {
			//artf160264 code changes. Modified the logic to replicate same as UI for displaying the determination value of eligibility section of the form
			if (!cpsInvReportDto.getCpsInvstDetailList().isEmpty() &&
					null != cpsInvReportDto.getCpsInvstDetailList().get(0).getCdCpsInvstDtlFamIncm() &&
					!"I5".equals(cpsInvReportDto.getCpsInvstDetailList().get(0).getCdCpsInvstDtlFamIncm()) &&
					!"I6".equals(cpsInvReportDto.getCpsInvstDetailList().get(0).getCdCpsInvstDtlFamIncm())) {
				if ("N".equals(eaCpr) || "N".equals(eaRra)) {
					determ = lookupDao.simpleDecodeSafe(CodesConstant.CEACONCL, ServiceConstants.N);
				} else {
					determ = lookupDao.simpleDecodeSafe(CodesConstant.CEACONCL, ServiceConstants.Y);
				}
			} else if (!cpsInvReportDto.getCpsInvstDetailList().isEmpty() &&
					null != cpsInvReportDto.getCpsInvstDetailList().get(0).getCdCpsInvstDtlFamIncm() &&
					("I5".equals(cpsInvReportDto.getCpsInvstDetailList().get(0).getCdCpsInvstDtlFamIncm()) ||
							"I6".equals(cpsInvReportDto.getCpsInvstDetailList().get(0).getCdCpsInvstDtlFamIncm()))) {
				determ = lookupDao.simpleDecodeSafe(CodesConstant.CEACONCL, ServiceConstants.N);
			} else if ((!cpsInvReportDto.getCpsInvstDetailList().isEmpty() &&
					null == cpsInvReportDto.getCpsInvstDetailList().get(0).getCdCpsInvstDtlFamIncm()) ||
					cpsInvReportDto.getCpsInvstDetailList().isEmpty()) {
				determ = lookupDao.simpleDecodeSafe(CodesConstant.CEACONCL, ServiceConstants.Y);
			} else {
				determ = lookupDao.simpleDecodeSafe(CodesConstant.CEACONCL, ServiceConstants.Y);
			}
		} else {
			determ = RISK_NOT_INDICATED;
		}
		
		BookmarkDto bookmarkEaDeterm = createBookmark(BookmarkConstants.EADETERMINATION, determ);
		bookmarkNonFormGrpList.add(bookmarkEaDeterm);

		// populate Relationship Information section.
		for (ArRelationshipsDto relationshipDto : cpsInvReportDto.getGetRelationshipsList()) {
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
		if (ObjectUtils.isEmpty(cpsInvReportDto.getGetRelationshipsList())) {
			FormDataGroupDto relGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RELATION,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRelList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkRelNmFull = createBookmark(BookmarkConstants.RELATION_FNAME,
					ServiceConstants.NO_FAMILY_TREE);
			bookmarkRelList.add(bookmarkRelNmFull);

			relGroupDto.setBookmarkDtoList(bookmarkRelList);
			formDataGroupList.add(relGroupDto);
		}

		Date crelNotifRgtsDate = null;
		try {
			crelNotifRgtsDate = DateUtils.toJavaDateFromInput(lookupDao.simpleDecode(ServiceConstants.CRELDATE,
					CodesConstant.CRELDATE_JUNE_24_NTFCTN_RGHTS));
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		if (!ObjectUtils.isEmpty(cpsInvReportDto.getCpsInvstDetailList()) &&
				!ObjectUtils.isEmpty(crelNotifRgtsDate) && (ObjectUtils.isEmpty(cpsInvReportDto.getGenericCaseInfoDto().getDtStageClose())
				|| DateUtils.isAfter(cpsInvReportDto.getGenericCaseInfoDto().getDtStageClose(), crelNotifRgtsDate))) {
			FormDataGroupDto formForNotifRights = createFormDataGroup(FormGroupsConstants.TMPLAT_NOTIFICATION_RIGHTS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkDtoNotifRightsList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkVrblWrtnNtfcnRgts = createBookmarkWithCodesTable(BookmarkConstants.INV_VRB_WRT_NOTIF,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndVrblWrtnNotifRights(), CodesConstant.CINVACAN);
			bookmarkDtoNotifRightsList.add(bookmarkVrblWrtnNtfcnRgts);
			BookmarkDto bookmarkNtfcnRgtsFormUpld = createBookmarkWithCodesTable(BookmarkConstants.INV_NOTIF_FRM_UPD,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndNotifRightsUpld(), CodesConstant.CINVACAN);
			bookmarkDtoNotifRightsList.add(bookmarkNtfcnRgtsFormUpld);
			formForNotifRights.setBookmarkDtoList(bookmarkDtoNotifRightsList);
			formDataGroupList.add(formForNotifRights);
		}

		// CSEC02D GenericCaseInfoDto
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				cpsInvReportDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonFormGrpList.add(bookmarkNmCase);
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				cpsInvReportDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonFormGrpList.add(bookmarkIdCase);
		BookmarkDto bookmarkCdStageType = createBookmark(BookmarkConstants.INTAKE_STAGE_TYPE,
				cpsInvReportDto.getGenericCaseInfoDto().getCdStageType());
		bookmarkNonFormGrpList.add(bookmarkCdStageType);
		BookmarkDto bookmarkDtStageClose = createBookmark(BookmarkConstants.DT_INV_APPROVED,
				DateUtils.stringDt(cpsInvReportDto.getGenericCaseInfoDto().getDtStageClose()));
		bookmarkNonFormGrpList.add(bookmarkDtStageClose);
		BookmarkDto bookmarkCaseCounty = createBookmarkWithCodesTable(BookmarkConstants.STAGE_COUNTY,
				cpsInvReportDto.getGenericCaseInfoDto().getCdCaseCounty(), CodesConstant.CCOUNT);
		bookmarkNonFormGrpList.add(bookmarkCaseCounty);
		BookmarkDto bookmarkStageCurrPriority = createBookmarkWithCodesTable(BookmarkConstants.STAGE_PRIORITY,
				cpsInvReportDto.getGenericCaseInfoDto().getCdStageCurrPriority(), CodesConstant.CPRIORTY);
		bookmarkNonFormGrpList.add(bookmarkStageCurrPriority);
		BookmarkDto bookmarkStageReasonClosed = createBookmarkWithCodesTable(BookmarkConstants.INV_ACTION,
				cpsInvReportDto.getGenericCaseInfoDto().getCdStageReasonClosed(), CodesConstant.CCINVCLS);
		bookmarkNonFormGrpList.add(bookmarkStageReasonClosed);
		BookmarkDto bookmarkCaseSensitive = createBookmark(BookmarkConstants.SENSITIVE_CASE,
				cpsInvReportDto.getGenericCaseInfoDto().getIndCaseSensitive());
		bookmarkNonFormGrpList.add(bookmarkCaseSensitive);

		// CINV95D CpsInvstDetailList
		if (!ObjectUtils.isEmpty(cpsInvReportDto.getCpsInvstDetailList())) {
			BookmarkDto bookmarkOverallDisptn = createBookmarkWithCodesTable(BookmarkConstants.OVERALL_DISP,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getCdCpsOverallDisptn(), CodesConstant.CDISPSTN);
			bookmarkNonFormGrpList.add(bookmarkOverallDisptn);
			BookmarkDto bookmarkChildLaborTraffic = createBookmarkWithCodesTable(BookmarkConstants.INV_LABOR_TRAFFIC,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndChildLaborTraffic(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkChildLaborTraffic);
			BookmarkDto bookmarkChildSexTraffic = createBookmarkWithCodesTable(BookmarkConstants.INV_SEX_TRAFFIC,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndChildLaborTraffic(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkChildSexTraffic);
			BookmarkDto bookmarkCpsLeJointContact = createBookmarkWithCodesTable(BookmarkConstants.INV_JOINT_CNT,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndCpsLeJntCntct(), CodesConstant.CBJNTCNT);
			bookmarkNonFormGrpList.add(bookmarkCpsLeJointContact);

			BookmarkDto bookmarkCpsInvstSafetyPln = createBookmarkWithCodesTable(BookmarkConstants.SAFETY_PLAN_COMPLETE,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndCpsInvstSafetyPln(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkCpsInvstSafetyPln);
			BookmarkDto bookmarkFTMOccurred = createBookmarkWithCodesTable(BookmarkConstants.INV_FTM_OCCURRED,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndFtmOccurred(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkFTMOccurred);			
			BookmarkDto bookmarkFTMOffered = createBookmarkWithCodesTable(BookmarkConstants.INV_FTM_OFFERED,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndFtmOffered(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkFTMOffered);			
			BookmarkDto bookmarkMultiPersFound = createBookmarkWithCodesTable(BookmarkConstants.INV_MULTI_FOUND,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndMultPersFound(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkMultiPersFound);
			BookmarkDto bookmarkMultiPersMerged = createBookmarkWithCodesTable(BookmarkConstants.INV_MULTI_MERGE,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndMultPersMerged(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkMultiPersMerged);
			BookmarkDto bookmarkParentGivenGuide = createBookmarkWithCodesTable(BookmarkConstants.INV_PARENT_GUIDE,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndParentGivenGuide(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkParentGivenGuide);
			BookmarkDto bookmarkParentNotify = createBookmarkWithCodesTable(BookmarkConstants.INV_PARENT_24HR,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndParentNotify24h(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkParentNotify);
			BookmarkDto bookmarkVictimPhoto = createBookmarkWithCodesTable(BookmarkConstants.INV_VIC_PHOTO,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndVictimPhoto(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkVictimPhoto);
			BookmarkDto bookmarkVictimTaped = createBookmarkWithCodesTable(BookmarkConstants.INV_VIC_TAPED,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndVictimTaped(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkVictimTaped);
			BookmarkDto bookmarkCpsInvstAbbrv = createBookmarkWithCodesTable(BookmarkConstants.INV_ABBREVIATED,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndCpsInvstDtlAbbrv(), CodesConstant.CINVACAN);
			bookmarkNonFormGrpList.add(bookmarkCpsInvstAbbrv);

			BookmarkDto bookmarkCdCpsInvstCpsLeJointContact = createBookmarkWithCodesTable(
					BookmarkConstants.INV_NOJOINT,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getCdReasonNoJntCntct(), CodesConstant.CSJNTCNT);
			bookmarkNonFormGrpList.add(bookmarkCdCpsInvstCpsLeJointContact);
			BookmarkDto bookmarkCdVictimPhoto = createBookmarkWithCodesTable(BookmarkConstants.INV_NO_PHOTO_REASON,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getCdVictimNoPhotoRsn(), CodesConstant.CINVNPHT);
			bookmarkNonFormGrpList.add(bookmarkCdVictimPhoto);
			BookmarkDto bookmarkCdVictimTaped = createBookmarkWithCodesTable(BookmarkConstants.INV_VIC_NO_TAPED_REASON,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getCdVictimTaped(), CodesConstant.CVICTPD);
			bookmarkNonFormGrpList.add(bookmarkCdVictimTaped);

			if(!ObjectUtils.isEmpty(cpsInvReportDto.getCpsInvstDetailList().get(0).getIndMeth()))
			{
			BookmarkDto bookmarkIndMeth = createBookmark(BookmarkConstants.INV_METH_DISC,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getIndMeth().equals(ServiceConstants.N)?ServiceConstants.ARNO:ServiceConstants.ARYES);
			bookmarkNonFormGrpList.add(bookmarkIndMeth);
			}
			BookmarkDto bookmarkDtCPSInvstDtlBegun = createBookmark(BookmarkConstants.DT_INV_BEGUN,
					DateUtils.stringDt(cpsInvReportDto.getCpsInvstDetailList().get(0).getDtCpsInvstDtlBegun()));
			bookmarkNonFormGrpList.add(bookmarkDtCPSInvstDtlBegun);
			BookmarkDto bookmarkCPSInvstDtlIntake = createBookmark(BookmarkConstants.DT_INT_REC,
					DateUtils.stringDt(cpsInvReportDto.getStageRtrvOutDto().getDtStageStart())); // artf147275
			bookmarkNonFormGrpList.add(bookmarkCPSInvstDtlIntake);
			BookmarkDto bookmarkCpsInvstDtlComplt = createBookmark(BookmarkConstants.DT_INV_COMPLETED,
					DateUtils.stringDt(cpsInvReportDto.getCpsInvstDetailList().get(0).getDtCpsInvstDtlComplt()));
			bookmarkNonFormGrpList.add(bookmarkCpsInvstDtlComplt);
			BookmarkDto bookmarkCpsInvstCpsLeJointContact = createBookmark(BookmarkConstants.INV_JOINT_CNT_TXT,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getReasonNoJntCntct());
			bookmarkNonFormGrpList.add(bookmarkCpsInvstCpsLeJointContact);
			BookmarkDto bookmarkTxtVictimPhoto = createBookmark(BookmarkConstants.INV_VIC_PHOTO_TXT,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getVictimPhoto());
			bookmarkNonFormGrpList.add(bookmarkTxtVictimPhoto);
			BookmarkDto bookmarkTxtVictimTaped = createBookmark(BookmarkConstants.INV_VIC_TAPED_TXT,
					cpsInvReportDto.getCpsInvstDetailList().get(0).getVictimTaped());
			bookmarkNonFormGrpList.add(bookmarkTxtVictimTaped);

		}

		// CSESB6D GetRiskAssessmentList
		for (CpsInvRiskDto cpsInvRiskDto : cpsInvReportDto.getGetRiskAssessmentList()) {

			if (ServiceConstants.APRV_MODIFY < cpsInvRiskDto.getNbrVersion()) {
				BookmarkDto bookmarkAbuseNeglHistryFound = createBookmarkWithCodesTable(
						BookmarkConstants.ABUSE_SEARCH_PRN, cpsInvRiskDto.getIndAbuseNeglHistFound(),
						CodesConstant.CINVACAN);
				bookmarkNonFormGrpList.add(bookmarkAbuseNeglHistryFound);
				BookmarkDto bookmarkAbuseNeglPrevInv = createBookmarkWithCodesTable(
						BookmarkConstants.ABUSE_SEARCH_FOUND, cpsInvRiskDto.getIndAbuseNeglPrevInv(),
						CodesConstant.CINVACAN);
				bookmarkNonFormGrpList.add(bookmarkAbuseNeglPrevInv);
				BookmarkDto bookmarkAbuseNeglSummary = createBookmark(BookmarkConstants.ABUSE_SEARCH_SUMMARY,
						cpsInvRiskDto.getTxtAbuseNeglSumm());
				bookmarkNonFormGrpList.add(bookmarkAbuseNeglSummary);
			}
		}

		// CSESB7D GetSafetyAssessmentList
		/*
		 * for (CpsInvSdmSafetyRiskDto cpsInvSdmSafetyRiskDto :
		 * cpsInvReportDto.getGetSafetyAssessmentList()) { BookmarkDto
		 * bookmarkAbuseNeglHistCompl =
		 * createBookmarkWithCodesTable(BookmarkConstants.ABUSE_SEARCH_SAFETY,
		 * cpsInvSdmSafetyRiskDto.getIndAbuseNeglHistCompl(),
		 * CodesConstant.CINVACAN);
		 * bookmarkNonFormGrpList.add(bookmarkAbuseNeglHistCompl); }
		 */

		// CCMN19D StagePersonDto
		BookmarkDto bookmarkPersonFull = createBookmark(BookmarkConstants.CASEWORKER_FULL_NM,
				cpsInvReportDto.getStagePersonDto().getNmPersonFull());
		bookmarkNonFormGrpList.add(bookmarkPersonFull);

		// CCMN60D SupervisorDto
		BookmarkDto bookmarkSupvPersonFull = createBookmark(BookmarkConstants.SUPERVISOR_FULL_NM,
				cpsInvReportDto.getSupervisorDto().getNmPersonFull());
		bookmarkNonFormGrpList.add(bookmarkSupvPersonFull);

		// CSEC01D EmployeePersPhNameDto
		BookmarkDto bookmarkAddrMailCodeCity = createBookmark(BookmarkConstants.OFFICE_ADDR_CITY,
				cpsInvReportDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
		bookmarkNonFormGrpList.add(bookmarkAddrMailCodeCity);
		BookmarkDto bookmarkAddrMailCodeStLn1 = createBookmark(BookmarkConstants.OFFICE_ADDR_LINE_1,
				cpsInvReportDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
		bookmarkNonFormGrpList.add(bookmarkAddrMailCodeStLn1);
		BookmarkDto bookmarkAddrMailCodeZip = createBookmark(BookmarkConstants.OFFICE_ADDR_ZIP,
				cpsInvReportDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
		bookmarkNonFormGrpList.add(bookmarkAddrMailCodeZip);

		// CINT66D PersonSplInfoListPrin
		if (!ObjectUtils.isEmpty(cpsInvReportDto.getGetPersonSplInfoListPrin())) {
			BookmarkDto bookmarkCdStagePersRelInt = createBookmarkWithCodesTable(BookmarkConstants.INTAKE_REL_INT,
					cpsInvReportDto.getGetPersonSplInfoListPrin().get(0).getCdStagePersRelInt(),
					CodesConstant.CRPTRINT);
			bookmarkNonFormGrpList.add(bookmarkCdStagePersRelInt);
			BookmarkDto bookmarkRepPersonFull = createBookmark(BookmarkConstants.INTAKE_REPORTER_NAME,
					cpsInvReportDto.getGetPersonSplInfoListPrin().get(0).getNmPersonFull());
			bookmarkNonFormGrpList.add(bookmarkRepPersonFull);
			BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.INTAKE_REPORTER_ID,
					cpsInvReportDto.getGetPersonSplInfoListPrin().get(0).getIdPerson());
			bookmarkNonFormGrpList.add(bookmarkIdPerson);
			if (StringUtils.isNotBlank(cpsInvReportDto.getGetPersonSplInfoListPrin().get(0).getTxtStagePersNote())) {
				BookmarkDto bookmarkTxtStagePersNote = createBookmark(BookmarkConstants.INTAKE_REPORTER_NOTES,
						cpsInvReportDto.getGetPersonSplInfoListPrin().get(0).getTxtStagePersNote().replace("<br>",
								". "));
				bookmarkNonFormGrpList.add(bookmarkTxtStagePersNote);
			}

		}

		// getMethIndicator
		BookmarkDto bookmarkSuspMeth = createBookmarkWithCodesTable(BookmarkConstants.INV_METH_SUSP,
				cpsInvReportDto.getMethSuspId(), CodesConstant.CINVACAN);
		bookmarkNonFormGrpList.add(bookmarkSuspMeth);

		List<BlobDataDto> bookmarkBlobDataList = new ArrayList<BlobDataDto>();
		// CINT21D
		Long id = cpsInvReportDto.getStageRtrvOutDto().getIdStage();
		BlobDataDto bookmarkBlobIdStage = createBlobData(BookmarkConstants.INTAKE_NARRATIVE,
				CodesConstant.INCOMING_NARRATIVE_VIEW, id.toString());
		bookmarkBlobDataList.add(bookmarkBlobIdStage);

		// CINT21DO intakeStageInfo populateIntakeNarrativeSection

		BookmarkDto bookmarkIntakeNarrDtReceived;
		if (ServiceConstants.MSG_PAPER_INTAKE == (int) cpsInvReportDto.getStageRtrvOutDto().getIdSituation()) { // artf169810 cast as int because ServiceConstants.MSG_PAPER_INTAKE was changed from Long
			/*bookmarkIntakeNarrDtReceived = createBookmark(BookmarkConstants.INTAKE_DATE,
					messages.get(ServiceConstants.MSG_PAPER_INTAKE).getTxtMessage());*/
			bookmarkIntakeNarrDtReceived = createBookmark(BookmarkConstants.INTAKE_DATE,
					"Please see the paper file for this intake record.");

		} else {
			bookmarkIntakeNarrDtReceived = createBookmark(BookmarkConstants.INTAKE_DATE,
					DateUtils.stringDt(cpsInvReportDto.getStageRtrvOutDto().getDtStageStart()));
		}
		bookmarkNonFormGrpList.add(bookmarkIntakeNarrDtReceived);
		
		BookmarkDto bookmarkStageNumber = createBookmark(BookmarkConstants.INTAKE_STAGE_NUMBER,
				cpsInvReportDto.getStageRtrvOutDto().getIdStage());
		bookmarkNonFormGrpList.add(bookmarkStageNumber);

		// getMrefStatus
		BookmarkDto bookmarkMrefStatus = createBookmark(BookmarkConstants.MULTI_REF,
				cpsInvReportDto.getGetMrefString());
		bookmarkNonFormGrpList.add(bookmarkMrefStatus);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);
		preFillData.setBlobDataDtoList(bookmarkBlobDataList);	
		return preFillData;
	}

	/* Find High or Very risk level from all households */
	private String getRiskLevel(List<String> households, CpsInvReportDto cpsInvReportDto) {
		if (CollectionUtils.isEmpty(households) || (ObjectUtils.isEmpty(cpsInvReportDto) && CollectionUtils.isEmpty(cpsInvReportDto.getGetSdmSafetyAssessmentsList()))) {
			return "";
		} else {
			Optional<CpsInvContactSdmSafetyAssessDto> dto = cpsInvReportDto.getGetSdmSafetyAssessmentsList().stream()
					.filter(a -> households.contains(a.getHouseholdName()))
					.filter(b -> "HIGH".equalsIgnoreCase(b.getCdFinalRiskLevel()) || "VERYHIGH".equalsIgnoreCase(b.getCdFinalRiskLevel()))
					.findFirst();
			return dto.isPresent() ? dto.get().getCdFinalRiskLevel() : "";
		}
	}

	private String populateName(String firstName,String lastName,String middleName)
	{
		StringBuilder concatName=new StringBuilder();
		if(!ObjectUtils.isEmpty(lastName))
		{
			concatName.append(lastName);
			concatName.append(ServiceConstants.SPACE);
			concatName.append(ServiceConstants.COMMA);
			concatName.append(ServiceConstants.SPACE);			
		}
		if(!ObjectUtils.isEmpty(firstName))
		{
			concatName.append(firstName);	
		}
		if(!ObjectUtils.isEmpty(middleName))
		{
			concatName.append(ServiceConstants.SPACE);
			concatName.append(middleName);	
		}
		
		return concatName.toString();
	}	
	
	public String formatTextValue(String txtToFormat) {		
		String[] txtConcurrently = null;
		if (!ObjectUtils.isEmpty(txtToFormat)) {
			txtConcurrently = txtToFormat.split("\n\n");
		}
		StringBuffer txtConcrBuf = new StringBuffer();
		if (!ObjectUtils.isEmpty(txtConcurrently)) {
			for (String txtConcr : txtConcurrently) {
				txtConcrBuf.append(ServiceConstants.FORM_SPACE+ServiceConstants.FORM_SPACE+ServiceConstants.FORM_SPACE+ServiceConstants.FORM_SPACE+
						ServiceConstants.FORM_SPACE+ServiceConstants.FORM_SPACE);
				txtConcrBuf.append(txtConcr);
				txtConcrBuf.append("<br/><br/>");
			}
		}
		return txtConcrBuf.toString();
	}
	
	
	public String formatCarriageLineFeedValue(String txtToFormat) {		
		String[] txtConcurrently = null;
		if (!ObjectUtils.isEmpty(txtToFormat)) {
			txtConcurrently = txtToFormat.split("\r\n");
		}
		StringBuffer txtConcrBuf = new StringBuffer();
		if (!ObjectUtils.isEmpty(txtConcurrently)) {
			for (String txtConcr : txtConcurrently) {
				txtConcrBuf.append(txtConcr);
				txtConcrBuf.append("<br/>");
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
	 * Added for QCR 62702
	 * @param cpsInvContactSdmSafetyAssessDto
	 * @return
	 */
	private boolean showSDM(CpsInvContactSdmSafetyAssessDto cpsInvContactSdmSafetyAssessDto) {
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
				}
			}
		}
		return showSdm;
	}


}
