package us.tx.state.dfps.service.forms.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.extreq.ExtreqDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.ContactNarrDto;
import us.tx.state.dfps.service.investigation.dto.FacilityAbuseInvReportDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.workload.dto.ApprovalEventLinkDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;
import us.tx.state.dfps.xmlstructs.outputstructs.AllegationStageVictimDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilInvstInfoDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Takes data
 * from service and uses it to generate prefill string which is used to populate
 * form May 1, 2018- 1:53:33 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Component
public class FacilityAbuseInvReportPrefillData extends DocumentServiceUtil {

	public static final String UNKNOWN = "Unknown";

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		FacilityAbuseInvReportDto prefillDto = (FacilityAbuseInvReportDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonGroupList = new ArrayList<BookmarkDto>();
		// initialize null DTOs
		if (ObjectUtils.isEmpty(prefillDto.getAllegationList())) {
			prefillDto.setAllegationList(new ArrayList<AllegationWithVicDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getAllegationStageVictimList())) {
			prefillDto.setAllegationStageVictimList(new ArrayList<AllegationStageVictimDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getApprovalEventLinkDto())) {
			prefillDto.setApprovalEventLinkDto(new ApprovalEventLinkDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getContactListA())) {
			prefillDto.setContactListA(new ArrayList<ContactDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getContactListB())) {
			prefillDto.setContactListB(new ArrayList<ContactDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getContactListC())) {
			prefillDto.setContactListC(new ArrayList<ContactDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getContactListD())) {
			prefillDto.setContactListD(new ArrayList<ContactDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getContactNarrList())) {
			prefillDto.setContactNarrList(new ArrayList<ContactNarrDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getEmployeeDetailDto())) {
			prefillDto.setEmployeeDetailDto(new EmployeeDetailDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getExtreqDto())) {
			prefillDto.setExtreqDto(new ExtreqDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getFacilInvDtlDto())) {
			prefillDto.setFacilInvDtlDto(new FacilInvDtlDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getFacilInvstInfoDto())) {
			prefillDto.setFacilInvstInfoDto(new FacilInvstInfoDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
			prefillDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getMultiAddressList())) {
			prefillDto.setMultiAddressList(new ArrayList<MultiAddressDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getStagePersDto())) {
			prefillDto.setStagePersDto(new StagePersDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getResourceDto())) {
			prefillDto.setResourceDto(new ResourceDto());
		}

		// parent group cfiv1608
		if (DateUtils.isBefore(prefillDto.getGenericCaseInfoDto().getDtStageStart(), DateUtils.date(2010, 6, 7))) {
			FormDataGroupDto dispPriorJuneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_PRIOR_JUNE,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> dispPriorJuneGroupList = new ArrayList<FormDataGroupDto>();

			// sub group cfiv1609
			FormDataGroupDto priorJuneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_JUNE,
					FormGroupsConstants.TMPLAT_DISP_PRIOR_JUNE);
			List<FormDataGroupDto> priorJuneGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkPriorJuneList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkFacAddrZip = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_ZIP,
					prefillDto.getFacilInvDtlDto().getAddrFacilInvstZip());
			bookmarkPriorJuneList.add(bookmarkFacAddrZip);
			BookmarkDto bookmarkFacAddrCity = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_CITY,
					prefillDto.getFacilInvDtlDto().getAddrFacilInvstCity());
			bookmarkPriorJuneList.add(bookmarkFacAddrCity);
			BookmarkDto bookmarkFacAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.SUM_FAC_ADDR_LINE_COUNTY,
					prefillDto.getFacilInvDtlDto().getAddrFacilInvstCnty(), CodesConstant.CCOUNT);
			bookmarkPriorJuneList.add(bookmarkFacAddrCounty);
			BookmarkDto bookmarkFacAddrState = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_STATE,
					prefillDto.getFacilInvDtlDto().getAddrFacilInvstState());
			bookmarkPriorJuneList.add(bookmarkFacAddrState);
			BookmarkDto bookmarkFacAddrLn1 = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_1,
					prefillDto.getFacilInvDtlDto().getAddrFacilInvstStr1());
			bookmarkPriorJuneList.add(bookmarkFacAddrLn1);
			BookmarkDto bookmarkFacAddrLn2 = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_2,
					prefillDto.getFacilInvDtlDto().getAddrFacilInvstStr2());
			bookmarkPriorJuneList.add(bookmarkFacAddrLn2);
			BookmarkDto bookmarkFacName = createBookmark(BookmarkConstants.SUM_FAC_NAME,
					prefillDto.getFacilInvDtlDto().getNmFacilinvstFacility());
			bookmarkPriorJuneList.add(bookmarkFacName);
			priorJuneGroupDto.setBookmarkDtoList(bookmarkPriorJuneList);

			// sub sub group cfiv1612
			FormDataGroupDto priorMhmrCodeGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_MHMR_CODE,
					FormGroupsConstants.TMPLAT_PRIOR_JUNE);
			List<BookmarkDto> bookmarkPriorMhmrCodeList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkMhmrCode = createBookmark(BookmarkConstants.MHMR_COMP_CODE,
					prefillDto.getFacilInvstInfoDto().getSzCdMhmrCompCode());
			bookmarkPriorMhmrCodeList.add(bookmarkMhmrCode);
			priorMhmrCodeGroupDto.setBookmarkDtoList(bookmarkPriorMhmrCodeList);
			priorJuneGroupList.add(priorMhmrCodeGroupDto);

			priorJuneGroupDto.setFormDataGroupList(priorJuneGroupList);
			dispPriorJuneGroupList.add(priorJuneGroupDto);

			dispPriorJuneGroupDto.setFormDataGroupList(dispPriorJuneGroupList);
			formDataGroupList.add(dispPriorJuneGroupDto);
		}

		// parent group cfiv1610
		else {
			FormDataGroupDto dispPostJuneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_POST_JUNE,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> dispPostJuneGroupList = new ArrayList<FormDataGroupDto>();

			// sub group cfiv1611
			for (MultiAddressDto addressDto : prefillDto.getMultiAddressList()) {
				FormDataGroupDto postJuneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_POST_JUNE,
						FormGroupsConstants.TMPLAT_DISP_POST_JUNE);
				List<BookmarkDto> bookmarkPostJuneList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkFacAddrZip = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_ZIP_POST,
						addressDto.getaAddrRsrcAddrZip());
				bookmarkPostJuneList.add(bookmarkFacAddrZip);
				BookmarkDto bookmarkFacAddrCity = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_CITY_POST,
						addressDto.getaAddrRsrcAddrCity());
				bookmarkPostJuneList.add(bookmarkFacAddrCity);
				BookmarkDto bookmarkFacAddrCounty = createBookmarkWithCodesTable(
						BookmarkConstants.SUM_FAC_ADDR_LINE_COUNTY_POST, addressDto.getaCdRsrcAddrCounty(),
						CodesConstant.CCOUNT);
				bookmarkPostJuneList.add(bookmarkFacAddrCounty);
				BookmarkDto bookmarkFacAddrState = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_STATE_POST,
						addressDto.getaCdRsrcAddrState());
				bookmarkPostJuneList.add(bookmarkFacAddrState);
				BookmarkDto bookmarkFacAddrLn1 = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_1_POST,
						addressDto.getaAddrRsrcAddrStLn1());
				bookmarkPostJuneList.add(bookmarkFacAddrLn1);
				if (!ObjectUtils.isEmpty(addressDto.getaAddrRsrcAddrStLn2())) {
					BookmarkDto bookmarkFacAddrLn2 = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_2_POST,
							addressDto.getaAddrRsrcAddrStLn2());
					bookmarkPostJuneList.add(bookmarkFacAddrLn2);
				}
				BookmarkDto bookmarkFacName = createBookmark(BookmarkConstants.SUM_FAC_NAME_POST,
						addressDto.getaCpNmResource());
				bookmarkPostJuneList.add(bookmarkFacName);

				// sub sub group cfiv1612
				BookmarkDto bookmarkMhmrCompCode = createBookmark(BookmarkConstants.MHMR_COMP_CODE_POST,
						addressDto.getaFilCdMhmrCode());
				bookmarkPostJuneList.add(bookmarkMhmrCompCode);
				// CINV17D

				postJuneGroupDto.setBookmarkDtoList(bookmarkPostJuneList);
				dispPostJuneGroupList.add(postJuneGroupDto);
			}

			dispPostJuneGroupDto.setFormDataGroupList(dispPostJuneGroupList);
			formDataGroupList.add(dispPostJuneGroupDto);
		}

		// parent group cfiv1601
		for (AllegationWithVicDto allegDto : prefillDto.getAllegationList()) {
			FormDataGroupDto allegationGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkAllegationList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAllegDisp = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_DISP,
					allegDto.getaCdAllegDisposition(), CodesConstant.CDISPSTN);
			bookmarkAllegationList.add(bookmarkAllegDisp);
			BookmarkDto bookmarkAllegAlleg = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG,
					allegDto.getaCdAllegType(), CodesConstant.CFACALGT);
			bookmarkAllegationList.add(bookmarkAllegAlleg);
			BookmarkDto bookmarkAllegClass = createBookmarkWithCodesTable(BookmarkConstants.CD_FACIL_ALLEG_CLASS,
					allegDto.getfCdFacilAllegClss(), CodesConstant.CFACCLSS);
			bookmarkAllegationList.add(bookmarkAllegClass);
			BookmarkDto bookmarkAllegAp = createBookmark(BookmarkConstants.ALLEG_DTL_AP, allegDto.getcNmPersonFull());
			bookmarkAllegationList.add(bookmarkAllegAp);
			BookmarkDto bookmarkAllegVictim = createBookmark(BookmarkConstants.ALLEG_DTL_VICTIM,
					allegDto.getB_nmPersonFull());
			bookmarkAllegationList.add(bookmarkAllegVictim);

			allegationGroupDto.setBookmarkDtoList(bookmarkAllegationList);
			formDataGroupList.add(allegationGroupDto);
		}

		// parent group cfiv1603
		for (ContactNarrDto contactNarrDto : prefillDto.getContactNarrList()) {
			if (!ServiceConstants.EFAC.equals(contactNarrDto.getCdContactType())
					&& !ServiceConstants.EEVL.equals(contactNarrDto.getCdContactType())
					&& !ServiceConstants.ECOM.equals(contactNarrDto.getCdContactType())
					&& !ServiceConstants.EEXR.equals(contactNarrDto.getCdContactType())
					&& !ServiceConstants.EFER.equals(contactNarrDto.getCdContactType())) {
				FormDataGroupDto evidenceSummaryGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_EVIDENCE_SUMMARY, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkEvidenceSummaryList = new ArrayList<BookmarkDto>();
				List<BlobDataDto> blobEvidenceSummaryList = new ArrayList<BlobDataDto>();
				BlobDataDto evidenceSummaryNarr = createBlobData(BookmarkConstants.EVIDENCE_SUMMARY_NARR,
						BookmarkConstants.CONTACT_NARRATIVE, contactNarrDto.getIdEvent().toString());
				blobEvidenceSummaryList.add(evidenceSummaryNarr);
				BookmarkDto bookmarkGroupId = createBookmark(BookmarkConstants.UE_GROUPID, contactNarrDto.getIdEvent());
				bookmarkEvidenceSummaryList.add(bookmarkGroupId);

				evidenceSummaryGroupDto.setBlobDataDtoList(blobEvidenceSummaryList);
				evidenceSummaryGroupDto.setBookmarkDtoList(bookmarkEvidenceSummaryList);
				formDataGroupList.add(evidenceSummaryGroupDto);
			}
		}

		// parent group cfiv1602
		for (AllegationStageVictimDto vicDto : prefillDto.getAllegationStageVictimList()) {
			FormDataGroupDto dateAllIncGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DATE_ALL_INC,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkDateAllIncList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDateAllIncDt = createBookmark(BookmarkConstants.DATE_ALL_INC_DT,
					vicDto.getScrDtGeneric1());
			bookmarkDateAllIncList.add(bookmarkDateAllIncDt);
			BookmarkDto bookmarkDateAllIncTxt = createBookmark(BookmarkConstants.DATE_ALL_INC_TXT,
					vicDto.getTxtFacilAllegCmnts());
			bookmarkDateAllIncList.add(bookmarkDateAllIncTxt);
			BookmarkDto bookmarkDateAllIncTm = createBookmark(BookmarkConstants.DATE_ALL_INC_TM,
					(!ServiceConstants.TIME_MINIMUM.equals(vicDto.getTmScrTmGeneric8())) ? vicDto.getTmScrTmGeneric8()
							: UNKNOWN);
			bookmarkDateAllIncList.add(bookmarkDateAllIncTm);

			dateAllIncGroupDto.setBookmarkDtoList(bookmarkDateAllIncList);
			formDataGroupList.add(dateAllIncGroupDto);
		}

		// parent group cfiv1605
		for (ContactDto contactDto : prefillDto.getContactListC()) {
			FormDataGroupDto analysisEvidenceGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_ANALYSIS_OF_EVIDENCE, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkAnalysisEvidenceList = new ArrayList<BookmarkDto>();
			List<BlobDataDto> blobAnalysisEvidenceList = new ArrayList<BlobDataDto>();
			BlobDataDto evidenceSummaryNarr = createBlobData(BookmarkConstants.ANALYSIS_OF_EVIDENCE_NARR,
					BookmarkConstants.CONTACT_NARRATIVE, contactDto.getIdEvent().toString());
			blobAnalysisEvidenceList.add(evidenceSummaryNarr);
			BookmarkDto bookmarkGroupId = createBookmark(BookmarkConstants.UE_GROUPID, contactDto.getIdEvent());
			bookmarkAnalysisEvidenceList.add(bookmarkGroupId);

			analysisEvidenceGroupDto.setBlobDataDtoList(blobAnalysisEvidenceList);
			analysisEvidenceGroupDto.setBookmarkDtoList(bookmarkAnalysisEvidenceList);
			formDataGroupList.add(analysisEvidenceGroupDto);
		}

		// parent group cfiv1613
		if (StringUtils.isNotBlank(prefillDto.getFacilInvstInfoDto().getSzCdPriorCaseHistRev())) {
			FormDataGroupDto dispPriorCaseGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_PRIOR_CASE,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> dispPriorCaseGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkDispPriorCaseList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkGroupId = createBookmark(BookmarkConstants.UE_GROUPID,
					prefillDto.getFacilInvstInfoDto().getUlIdEvent());
			bookmarkDispPriorCaseList.add(bookmarkGroupId);
			dispPriorCaseGroupDto.setBookmarkDtoList(bookmarkDispPriorCaseList);

			// sub group cfiv1614
			if (ServiceConstants.IN_PROCESS.equals(prefillDto.getFacilInvstInfoDto().getSzCdPriorCaseHistRev())) {
				FormDataGroupDto priorCaseNotUsedGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PRIOR_CASE_NOT_USED, FormGroupsConstants.TMPLAT_DISP_PRIOR_CASE);
				dispPriorCaseGroupList.add(priorCaseNotUsedGroupDto);
			}

			// sub group cfiv1615
			else if (ServiceConstants.HALF_DAY.equals(prefillDto.getFacilInvstInfoDto().getSzCdPriorCaseHistRev())) {
				FormDataGroupDto priorCaseConcernsGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PRIOR_CASE_CONCERNS, FormGroupsConstants.TMPLAT_DISP_PRIOR_CASE);
				dispPriorCaseGroupList.add(priorCaseConcernsGroupDto);
			}

			// sub group cfiv1616
			else if (ServiceConstants.CAREGIVER.equals(prefillDto.getFacilInvstInfoDto().getSzCdPriorCaseHistRev())) {
				FormDataGroupDto priorCaseCurrentGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PRIOR_CASE_CURRENT, FormGroupsConstants.TMPLAT_DISP_PRIOR_CASE);
				dispPriorCaseGroupList.add(priorCaseCurrentGroupDto);
			}

			// sub group cfiv1617
			else if (ServiceConstants.PLCMT_RELATIVE
					.equals(prefillDto.getFacilInvstInfoDto().getSzCdPriorCaseHistRev())) {
				FormDataGroupDto priorCaseNoGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_CASE_NO,
						FormGroupsConstants.TMPLAT_DISP_PRIOR_CASE);
				dispPriorCaseGroupList.add(priorCaseNoGroupDto);
			}

			// sub group cfiv1618
			else if (ServiceConstants.LEVEL_CARE_50
					.equals(prefillDto.getFacilInvstInfoDto().getSzCdPriorCaseHistRev())) {
				FormDataGroupDto priorCaseConcCurrentGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PRIOR_CASE_CONC_CURRENT, FormGroupsConstants.TMPLAT_DISP_PRIOR_CASE);
				dispPriorCaseGroupList.add(priorCaseConcCurrentGroupDto);
			}

			dispPriorCaseGroupDto.setFormDataGroupList(dispPriorCaseGroupList);
			formDataGroupList.add(dispPriorCaseGroupDto);
		}

		// parent group cfiv1606
		/*
		 * if (!ObjectUtils.isEmpty(prefillDto.getContactDate())) {
		 * FormDataGroupDto priorReviewGroupDto =
		 * createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_REVIEW,
		 * FormConstants.EMPTY_STRING);
		 * formDataGroupList.add(priorReviewGroupDto); }
		 */

        // parent group 1607
        StringBuilder evidenceList = new StringBuilder();
        // Artifact: artf147216 - SD 56377 : R2 Sev 5 Defect 10107
        // Use the Evidence list coming in Pre-fill dto from EEVL Contact else put the default one.
        if (!ObjectUtils.isEmpty(prefillDto.getEvidenceList())) {
            prefillDto.getEvidenceList().forEach(e -> evidenceList.append(e));
            // END : Artifact: artf147216
        } else {
            List<String> positionList = ServiceConstants.EVIDENCE_LIST;
            for (String position : positionList) {
                evidenceList.append(ServiceConstants.EXHIBIT + position);
                evidenceList.append(ServiceConstants.BREAK);
            }
        }

		FormDataGroupDto evidenceListGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EVIDENCE_200_LIST,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookMarkEvidenceListList = new ArrayList<BookmarkDto>();
		BookmarkDto bookMarkEvidenceListContacts = createBookmark(BookmarkConstants.EVIDENCE_LIST_CONTACTS,
				evidenceList);
		bookMarkEvidenceListList.add(bookMarkEvidenceListContacts);
		evidenceListGroupDto.setBookmarkDtoList(bookMarkEvidenceListList);
		formDataGroupList.add(evidenceListGroupDto);

		// parent group cfiv1619
		for (ContactDto contactDto : prefillDto.getContactListD()) {
			if (!ObjectUtils.isEmpty(contactDto.getIdEvent())) {
				FormDataGroupDto eexrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EEXR,
						FormConstants.EMPTY_STRING);
				List<BlobDataDto> blobEexrList = new ArrayList<BlobDataDto>();
				BlobDataDto blobEexrNarr = createBlobData(BookmarkConstants.EEXR_NARR,
						BookmarkConstants.CONTACT_NARRATIVE, contactDto.getIdEvent().toString());
				blobEexrList.add(blobEexrNarr);

				eexrGroupDto.setBlobDataDtoList(blobEexrList);
				formDataGroupList.add(eexrGroupDto);
			}
		}

		// Non-group bookmarks
		// CSEC02D
		BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				prefillDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonGroupList.add(bookmarkTitleCaseName);
		BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				prefillDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonGroupList.add(bookmarkTitleCaseNumber);

		// CDYN03D - A
		//Fixed Warranty Defect#12010 Issue to fix duplicate bookmark creation 
		if(!ObjectUtils.isEmpty(prefillDto.getContactListA())){
		ContactDto contactDirNotifDto = prefillDto.getContactListA().stream()
				.min(Comparator.comparing(ContactDto::getDtContactOccurred)).get();
		// Warranty Defect - 10924 : Corrected the Incorrect Mapping of Last
		// Update to Contact Occurred Date
		BookmarkDto bookmarkDateDirectorNotif = createBookmark(BookmarkConstants.DATE_DIRECTOR_NOTIF,
				DateUtils.stringDt(contactDirNotifDto.getDtContactOccurred()));
		bookmarkNonGroupList.add(bookmarkDateDirectorNotif);
		BookmarkDto bookmarkTimeDirectorNotif = createBookmark(BookmarkConstants.TIME_DIRECTOR_NOTIF,
				DateUtils.getTime(contactDirNotifDto.getDtContactOccurred()));
		bookmarkNonGroupList.add(bookmarkTimeDirectorNotif);
		}

		// CDYN03D - B
		//Fixed Warranty Defect#12010 Issue to fix duplicate bookmark creation 
		if(!ObjectUtils.isEmpty(prefillDto.getContactListB())){
		ContactDto contactLawNotifDto = prefillDto.getContactListB().stream()
				.min(Comparator.comparing(ContactDto::getDtContactOccurred)).get();
		// Warranty Defect - 10924 : Corrected the Incorrect Mapping of Last
		// Update to Contact Occurred Date
		BookmarkDto bookmarkDateLawNotif = createBookmark(BookmarkConstants.DATE_LAW_NOTIF,
				DateUtils.stringDt(contactLawNotifDto.getDtContactOccurred()));
		bookmarkNonGroupList.add(bookmarkDateLawNotif);
		BookmarkDto bookmarkTimeLawNotif = createBookmark(BookmarkConstants.TIME_LAW_NOTIF,
				DateUtils.getTime(contactLawNotifDto.getDtContactOccurred()));
		bookmarkNonGroupList.add(bookmarkTimeLawNotif);
		}

		// CSES39D
		if (!ObjectUtils.isEmpty(prefillDto.getFacilInvDtlDto().getDtFacilInvstBegun())) {
			BookmarkDto bookmarkDateInvestBegun = createBookmark(BookmarkConstants.DATE_INVEST_BEGUN,
					DateUtils.stringDt(prefillDto.getFacilInvDtlDto().getDtFacilInvstBegun()));
			bookmarkNonGroupList.add(bookmarkDateInvestBegun);
			BookmarkDto bookmarkTimeInvestBegun = createBookmark(BookmarkConstants.TIME_INVEST_BEGUN,
					DateUtils.getTime(prefillDto.getFacilInvDtlDto().getDtFacilInvstBegun()));
			bookmarkNonGroupList.add(bookmarkTimeInvestBegun);
		}
		if (!ObjectUtils.isEmpty(prefillDto.getFacilInvDtlDto().getDtFacilInvstIntake())) {
			BookmarkDto bookmarkDateInitialNotif = createBookmark(BookmarkConstants.DATE_INITIAL_NOTIF,
					DateUtils.stringDt(prefillDto.getFacilInvDtlDto().getDtFacilInvstIntake()));
			bookmarkNonGroupList.add(bookmarkDateInitialNotif);
			BookmarkDto bookmarkTimeInitialNotif = createBookmark(BookmarkConstants.TIME_INITIAL_NOTIF,
					DateUtils.getTime(prefillDto.getFacilInvDtlDto().getDtFacilInvstIntake()));
			bookmarkNonGroupList.add(bookmarkTimeInitialNotif);
		}
		BookmarkDto bookmarkFacAddrZip = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_ZIP,
				prefillDto.getFacilInvDtlDto().getAddrFacilInvstZip());
		bookmarkNonGroupList.add(bookmarkFacAddrZip);
		BookmarkDto bookmarkFacAddrCity = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_CITY,
				prefillDto.getFacilInvDtlDto().getAddrFacilInvstCity());
		bookmarkNonGroupList.add(bookmarkFacAddrCity);
		BookmarkDto bookmarkFacAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.SUM_FAC_ADDR_LINE_COUNTY,
				prefillDto.getFacilInvDtlDto().getAddrFacilInvstCnty(), CodesConstant.CCOUNT);
		bookmarkNonGroupList.add(bookmarkFacAddrCounty);
		BookmarkDto bookmarkFacAddrState = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_STATE,
				prefillDto.getFacilInvDtlDto().getAddrFacilInvstState());
		bookmarkNonGroupList.add(bookmarkFacAddrState);
		BookmarkDto bookmarkFacAddrLn1 = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_1,
				prefillDto.getFacilInvDtlDto().getAddrFacilInvstStr1());
		bookmarkNonGroupList.add(bookmarkFacAddrLn1);
		BookmarkDto bookmarkFacAddrLn2 = createBookmark(BookmarkConstants.SUM_FAC_ADDR_LINE_2,
				prefillDto.getFacilInvDtlDto().getAddrFacilInvstStr2());
		bookmarkNonGroupList.add(bookmarkFacAddrLn2);
		BookmarkDto bookmarkFacName = createBookmark(BookmarkConstants.SUM_FAC_NAME,
				prefillDto.getFacilInvDtlDto().getNmFacilinvstFacility());
		bookmarkNonGroupList.add(bookmarkFacName);

		// CINVB8D
		if (!ObjectUtils.isEmpty(prefillDto.getContactDate())) {
			BookmarkDto bookmarkDateContactOccurred = createBookmark(BookmarkConstants.DATE_CONTACT_OCCURRED,
					DateUtils.stringDt(prefillDto.getContactDate()));
			bookmarkNonGroupList.add(bookmarkDateContactOccurred);
			BookmarkDto bookmarkTimeRequestReview = createBookmark(BookmarkConstants.TIME_REQUEST_REVIEW,
					DateUtils.getTime(prefillDto.getContactDate()));
			bookmarkNonGroupList.add(bookmarkTimeRequestReview);
		}

		// CCMN30D
		BookmarkDto bookmarkNmPersonFull = createBookmark(BookmarkConstants.NM_PERSON_FULL,
				prefillDto.getStagePersDto().getNmPersonFull());
		bookmarkNonGroupList.add(bookmarkNmPersonFull);

		// CCMN69D
		BookmarkDto bookmarkEmpClass = createBookmark(BookmarkConstants.EMP_CLASS,
				prefillDto.getEmployeeDetailDto().getEmployeeClass());
		bookmarkNonGroupList.add(bookmarkEmpClass);

		// CSESF5D
		BookmarkDto bookmarkDtApproversDeterm = createBookmark(BookmarkConstants.DT_APPROVERS_DETERMINATION,
				DateUtils.stringDt(prefillDto.getApprovalDate()));
		bookmarkNonGroupList.add(bookmarkDtApproversDeterm);

		// CSECF0D
		BookmarkDto bookmarkApproverFirst = createBookmark(BookmarkConstants.APPROVER_FIRST,
				prefillDto.getExtreqDto().getNmEmployeeFirst());
		bookmarkNonGroupList.add(bookmarkApproverFirst);
		BookmarkDto bookmarkApproverLast = createBookmark(BookmarkConstants.APPROVER_LAST,
				prefillDto.getExtreqDto().getNmEmployeeLast());
		bookmarkNonGroupList.add(bookmarkApproverLast);
		BookmarkDto bookmarkApproverStatement = createBookmark(BookmarkConstants.APPROVER_STATEMENT,
				prefillDto.getExtreqDto().getTxtApproverStatement());
		bookmarkNonGroupList.add(bookmarkApproverStatement);

		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		prefillData.setBookmarkDtoList(bookmarkNonGroupList);
		prefillData.setFormDataGroupList(formDataGroupList);

		return prefillData;
	}

}
