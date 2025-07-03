package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.ChildBackgroundServiceDto;
import us.tx.state.dfps.common.dto.PrincipalLegalStatusDto;
import us.tx.state.dfps.common.dto.WorkerDetailDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dto.EligibilityOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.conservatorship.dto.RemovalCharAdultDto;
import us.tx.state.dfps.service.conservatorship.dto.RemovalReasonDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.ChildPlanDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ChildBackgroundFormPrefillData will implemented returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form CSC28O00. March 20, 2018- 2:04:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class ChildBackgroundFormPrefillData extends DocumentServiceUtil {

	@SuppressWarnings("static-access")
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		ChildBackgroundServiceDto childBackgroundServiceDto = (ChildBackgroundServiceDto) parentDtoobj;

		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getCaseInfoDtoList())) {
			childBackgroundServiceDto.setCaseInfoDtoList(new ArrayList<CaseInfoDto>());
		}

		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getCnsrvtrshpRemovalDto())) {
			childBackgroundServiceDto.setCnsrvtrshpRemovalDto(new CnsrvtrshpRemovalDto());
		}
		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getEligibilityOutDto())) {
			childBackgroundServiceDto.setEligibilityOutDto(new EligibilityOutDto());
		}
		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getPersonBlocLocDto())) {
			childBackgroundServiceDto.setPersonBlocLocDto(new PersonLocDto());
		}

		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getPersonDto())) {
			childBackgroundServiceDto.setPersonDto(new PersonDto());
		}

		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getPersonIdDto())) {
			childBackgroundServiceDto.setPersonIdDto(new PersonIdDto());
		}
		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getPlacementActPlannedOutDto())) {
			childBackgroundServiceDto.setPlacementActPlannedOutDto(new PlacementActPlannedOutDto());
		}

		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getPrincipalLegalStatusDto())) {
			childBackgroundServiceDto.setPrincipalLegalStatusDto(new ArrayList<PrincipalLegalStatusDto>());
		}

		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getRemovalCharAdultDtoList())) {
			childBackgroundServiceDto.setRemovalCharAdultDtoList(new ArrayList<RemovalCharAdultDto>());
		}

		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getRemovalResonDtoList())) {
			childBackgroundServiceDto.setRemovalResonDtoList(new ArrayList<RemovalReasonDto>());
		}

		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getStagePersonLinkCaseDto())) {
			childBackgroundServiceDto.setStagePersonLinkCaseDto(new StagePersonLinkCaseDto());
		}

		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getWorkerDetailDto())) {
			childBackgroundServiceDto.setWorkerDetailDto(new WorkerDetailDto());
		}

		if (TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getChildPlanDto())) {
			childBackgroundServiceDto.setChildPlanDto(new ChildPlanDto());
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		/**
		 * Checking the nameSuffix not equal to null. if its not null, we set
		 * the preill data for cfzco00
		 */

		if (!TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getStagePersonLinkCaseDto().getCdPersonSuffix())) {
			FormDataGroupDto tempCommaFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);

			formDataGroupList.add(tempCommaFrmDataGrpDto);

		}

		// create group csc28o01 and set prefill data
		for (RemovalReasonDto rmvlReason : childBackgroundServiceDto.getRemovalResonDtoList()) {
			FormDataGroupDto tempRmvlReasFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REMOVAL_RSN,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRmvrsnList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkRmvlRsn = createBookmarkWithCodesTable(BookmarkConstants.REMOVAL_RSN,
					rmvlReason.getCdRemovalReason(), CodesConstant.CREMFRHR);
			bookmarkRmvrsnList.add(bookmarkRmvlRsn);
			tempRmvlReasFrmDataGrpDto.setBookmarkDtoList(bookmarkRmvrsnList);

			formDataGroupList.add(tempRmvlReasFrmDataGrpDto);

		}

		// create group csc28o02 and set prefill data

		for (RemovalCharAdultDto rmvlCharAdultDto : childBackgroundServiceDto.getRemovalCharAdultDtoList()) {
			FormDataGroupDto tempRmvlCharAdultFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CARETAKER_CHAR, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRmvlCharAdultList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkRmvlCharAdult = createBookmarkWithCodesTable(BookmarkConstants.REMOVAL_ADULT_CHAR,
					rmvlCharAdultDto.getCdRemovAdultChar(), CodesConstant.CREMCHCT);
			bookmarkRmvlCharAdultList.add(bookmarkRmvlCharAdult);
			tempRmvlCharAdultFrmDataGrpDto.setBookmarkDtoList(bookmarkRmvlCharAdultList);

			formDataGroupList.add(tempRmvlCharAdultFrmDataGrpDto);
		}

		/**
		 * Checking the nameSuffix not equal to null. if its not null, we set
		 * the preill data for cfzco00
		 */

		if (!TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getWorkerDetailDto().getCdNameSuffix())) {
			FormDataGroupDto tempComma2FrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
					FormConstants.EMPTY_STRING);

			formDataGroupList.add(tempComma2FrmDataGrpDto);

		}

		// create group csc28o05 and set prefill data

		for (CaseInfoDto caseInfoDto : childBackgroundServiceDto.getCaseInfoDtoList()) {
			FormDataGroupDto tempPrnsFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRNS,
					FormConstants.EMPTY_STRING);

			List<BookmarkDto> tempPrnsBookmarkList = new ArrayList<BookmarkDto>();

			BookmarkDto addrCityAddBookMark = createBookmark(BookmarkConstants.PRN_ADDR_CITY,
					caseInfoDto.getAddrPersonAddrCity());
			BookmarkDto addrStateAddBookMark = createBookmark(BookmarkConstants.PRN_ADDR_STATE,
					caseInfoDto.getCdPersonAddrState());
			BookmarkDto nameSuffixAddBookMark = createBookmarkWithCodesTable(BookmarkConstants.PRN_NAME_SUFFIX,
					caseInfoDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
			BookmarkDto stagePersRelint = createBookmarkWithCodesTable(BookmarkConstants.PRN_RELATIONSHIP,
					caseInfoDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);

			BookmarkDto nmFirstAddBookMark = createBookmark(BookmarkConstants.PRN_NAME_FIRST,
					caseInfoDto.getNmNameFirst());
			BookmarkDto nmLastAddBookMark = createBookmark(BookmarkConstants.PRN_NAME_LAST,
					caseInfoDto.getNmNameLast());
			BookmarkDto nmMiddleAddBookMark = createBookmark(BookmarkConstants.PRN_NAME_MIDDLE,
					caseInfoDto.getNmNameMiddle());

			tempPrnsBookmarkList.add(nmMiddleAddBookMark);
			tempPrnsBookmarkList.add(stagePersRelint);
			tempPrnsBookmarkList.add(nameSuffixAddBookMark);
			tempPrnsBookmarkList.add(addrStateAddBookMark);
			tempPrnsBookmarkList.add(nmLastAddBookMark);
			tempPrnsBookmarkList.add(addrCityAddBookMark);
			tempPrnsBookmarkList.add(nmFirstAddBookMark);

			List<FormDataGroupDto> frmDataGroupList = new ArrayList<FormDataGroupDto>();

			// create group cfzo00
			if (!TypeConvUtil.isNullOrEmpty(caseInfoDto.getCdNameSuffix())) {
				FormDataGroupDto tempCommaFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormGroupsConstants.TMPLAT_PRNS);
				frmDataGroupList.add(tempCommaFrmDataGrpDto);

			}

			// create group csc28o06 and set prefill data

			for (PrincipalLegalStatusDto principalLegalStatusDto : childBackgroundServiceDto
					.getPrincipalLegalStatusDto()) {
				if (principalLegalStatusDto.getIdPerson().equals(caseInfoDto.getIdPerson())) {
					FormDataGroupDto legalStatusFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_LEGAL_STATUS, FormGroupsConstants.TMPLAT_PRNS);
					List<BookmarkDto> legalStatusBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto legalStatusDtAddBookMark = createBookmark(BookmarkConstants.PRN_LEGAL_STAT_DATE,
							TypeConvUtil.formDateFormat(principalLegalStatusDto.getDtLegalStatStatusDt()));
					BookmarkDto legalStatusAddBookMark = createBookmarkWithCodesTable(BookmarkConstants.PRN_LEGAL_STAT,
							principalLegalStatusDto.getCdLegalStatStatus(), CodesConstant.CLEGSTAT);
					legalStatusBookmarkList.add(legalStatusAddBookMark);
					legalStatusBookmarkList.add(legalStatusDtAddBookMark);
					legalStatusFrmDataGrpDto.setBookmarkDtoList(legalStatusBookmarkList);
					frmDataGroupList.add(legalStatusFrmDataGrpDto);

				}
			}

			tempPrnsFrmDataGrpDto.setBookmarkDtoList(tempPrnsBookmarkList);
			tempPrnsFrmDataGrpDto.setFormDataGroupList(frmDataGroupList);
			formDataGroupList.add(tempPrnsFrmDataGrpDto);

		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		List<BlobDataDto> blobDataNonFrmGrpList = new ArrayList<BlobDataDto>();

		// Populate child DOB value from DAM CSEC15D
		BookmarkDto bookmarkPersonBirth = createBookmark(BookmarkConstants.TITLE_CHILD_DOB,
				TypeConvUtil.formDateFormat(childBackgroundServiceDto.getStagePersonLinkCaseDto().getDtPersonBirth()));
		bookmarkNonFrmGrpList.add(bookmarkPersonBirth);

		// Populate currentDate value from DAM CSEC15D
		BookmarkDto bookmarkDtCurrDate = createBookmark(BookmarkConstants.DATE,
				TypeConvUtil.formDateFormat(childBackgroundServiceDto.getStagePersonLinkCaseDto().getDtCurrent()));
		bookmarkNonFrmGrpList.add(bookmarkDtCurrDate);

		// Populate nmSuffix value from DAM CSEC15D
		BookmarkDto bookmarkNmSuffix = createBookmarkWithCodesTable(BookmarkConstants.TITLE_CHILD_NAME_SUFFIX,
				childBackgroundServiceDto.getStagePersonLinkCaseDto().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkNmSuffix);

		// Populate nmCase value from DAM CSEC15D
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				childBackgroundServiceDto.getStagePersonLinkCaseDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkNmCase);

		// Populate nmFirst value from DAM CSEC15D
		BookmarkDto bookmarkNmFirst = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_FIRST,
				childBackgroundServiceDto.getStagePersonLinkCaseDto().getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkNmFirst);

		// Populate nmLast value from DAM CSEC15D
		BookmarkDto bookmarkNmLast = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_LAST,
				childBackgroundServiceDto.getStagePersonLinkCaseDto().getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkNmLast);

		// Populate nmMiddle value from DAM CSEC15D
		BookmarkDto bookmarkNmMiddle = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_MIDDLE,
				childBackgroundServiceDto.getStagePersonLinkCaseDto().getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkNmMiddle);

		// Populate tmScrTmGeneric8 value from DAM CSEC15D
		BookmarkDto bookmarkTime = createBookmark(BookmarkConstants.TIME,
				childBackgroundServiceDto.getStagePersonLinkCaseDto().getTmScrTmGeneric8());
		bookmarkNonFrmGrpList.add(bookmarkTime);

		// Populate idCase value from DAM CSEC15D
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_ID,
				childBackgroundServiceDto.getStagePersonLinkCaseDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkIdCase);

		// Populate personIdNumber value from DAM CCMN72D
		BookmarkDto bookmarkPersonIdNumber = createBookmark(BookmarkConstants.CHILD_SSN,
				childBackgroundServiceDto.getPersonIdDto().getPersonIdNumber());
		bookmarkNonFrmGrpList.add(bookmarkPersonIdNumber);

		// Populate child sex value from DAM CCMN44D
		BookmarkDto bookmarkChildSex = createBookmarkWithCodesTable(BookmarkConstants.CHILD_SEX,
				childBackgroundServiceDto.getPersonDto().getCdPersonSex(), CodesConstant.CSEX);
		bookmarkNonFrmGrpList.add(bookmarkChildSex);

		// Populate child ethnic value from DAM CCMN44D
		BookmarkDto bookmarkChildEthnic = createBookmarkWithCodesTable(BookmarkConstants.CHILD_ETHNIC,
				childBackgroundServiceDto.getPersonDto().getCdPersonEthnicGroup(), CodesConstant.CETHNIC);
		bookmarkNonFrmGrpList.add(bookmarkChildEthnic);

		// Populate dtPlcmtStart value from DAM CSES34D
		BookmarkDto bookmarkdtPlcmtStart = createBookmark(BookmarkConstants.FACILITY_DATE_PLCMT, TypeConvUtil
				.formDateFormat(childBackgroundServiceDto.getPlacementActPlannedOutDto().getDtPlcmtStart()));
		bookmarkNonFrmGrpList.add(bookmarkdtPlcmtStart);

		// Populate AddrPlcmtCity value from DAM CSES34D
		BookmarkDto bookmarkdtPlcmtCity = createBookmark(BookmarkConstants.FACILITY_ADDR_CITY,
				childBackgroundServiceDto.getPlacementActPlannedOutDto().getAddrPlcmtCity());
		bookmarkNonFrmGrpList.add(bookmarkdtPlcmtCity);

		// Populate AddrPlcmtLn1 value from DAM CSES34D
		BookmarkDto bookmarkAddrPlcmntLn1 = createBookmark(BookmarkConstants.FACILITY_ADDR_ST_LN1,
				childBackgroundServiceDto.getPlacementActPlannedOutDto().getAddrPlcmtLn1());
		bookmarkNonFrmGrpList.add(bookmarkAddrPlcmntLn1);

		// Populate AddrPlcmtLn1 value from DAM CSES34D
		BookmarkDto bookmarkAddrPlcmntLn2 = createBookmark(BookmarkConstants.FACILITY_ADDR_ST_LN2,
				childBackgroundServiceDto.getPlacementActPlannedOutDto().getAddrPlcmtLn2());
		bookmarkNonFrmGrpList.add(bookmarkAddrPlcmntLn2);

		// Populate AddrPlcmtState value from DAM CSES34D
		BookmarkDto bookmarkAddrPlcmntSt = createBookmark(BookmarkConstants.FACILITY_ADDR_STATE,
				childBackgroundServiceDto.getPlacementActPlannedOutDto().getAddrPlcmtSt());
		bookmarkNonFrmGrpList.add(bookmarkAddrPlcmntSt);

		// Populate AddrPlcmtZip value from DAM CSES34D
		BookmarkDto bookmarkAddrPlcmntZip = createBookmark(BookmarkConstants.FACILITY_ADDR_ZIP,
				childBackgroundServiceDto.getPlacementActPlannedOutDto().getAddrPlcmtZip());
		bookmarkNonFrmGrpList.add(bookmarkAddrPlcmntZip);

		// Populate cdPlcmtLivArr value from DAM CSES34D
		BookmarkDto bookmarkAddrPlcmntLivArr = createBookmarkWithCodesTable(BookmarkConstants.PLACEMENT_TYPE,
				childBackgroundServiceDto.getPlacementActPlannedOutDto().getCdPlcmtLivArr(), CodesConstant.CPLLAFRM);
		bookmarkNonFrmGrpList.add(bookmarkAddrPlcmntLivArr);

		// Populate cdNmPlcmtFacil value from DAM CSES34D
		BookmarkDto bookmarkCdPlcmntFacil = createBookmark(BookmarkConstants.PLACEMENT_NAME,
				childBackgroundServiceDto.getPlacementActPlannedOutDto().getNmPlcmtFacil());
		bookmarkNonFrmGrpList.add(bookmarkCdPlcmntFacil);

		if(!ObjectUtils.isEmpty(childBackgroundServiceDto.getRecommendedServicePackage())) {
			//Recommended Service Package
			FormDataGroupDto rmdServicePackage = createFormDataGroup(FormGroupsConstants.TMPLAT_RMDSERVICEPKG, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkSvcPkgList = new ArrayList<>();
			bookmarkSvcPkgList.add(createBookmarkWithCodesTable(BookmarkConstants.RECOMMENDED_SERVICE_PACKAGE, childBackgroundServiceDto.getRecommendedServicePackage().getSvcPkgCd(), CodesConstant.CSVCCODE));
			rmdServicePackage.setBookmarkDtoList(bookmarkSvcPkgList);
			formDataGroupList.add(rmdServicePackage);

			//Selected Service Package
			FormDataGroupDto selServicePackage = createFormDataGroup(FormGroupsConstants.TMPLAT_SELSERVICEPKG, FormConstants.EMPTY_STRING);
			if(!ObjectUtils.isEmpty(childBackgroundServiceDto.getSelectedServicePackage())) {
				bookmarkSvcPkgList = new ArrayList<>();
				bookmarkSvcPkgList.add(createBookmarkWithCodesTable(BookmarkConstants.LATEST_SELECTED_SERVICE_PACKAGE, childBackgroundServiceDto.getSelectedServicePackage().getSvcPkgCd(), CodesConstant.CSVCCODE));
				selServicePackage.setBookmarkDtoList(bookmarkSvcPkgList);
				createAddonBookMark(childBackgroundServiceDto,formDataGroupList);
			}
			formDataGroupList.add(selServicePackage);
		} else {
			// Populate cdPlocChild value from DAM CSES35D
			FormDataGroupDto loc = createFormDataGroup(FormGroupsConstants.TMPLAT_LOC, FormConstants.EMPTY_STRING);
			loc.setBookmarkDtoList(Arrays.asList(createBookmarkWithCodesTable(BookmarkConstants.AUTHORIZED_LOC,
					childBackgroundServiceDto.getPersonBlocLocDto().getCdPlocChild(), CodesConstant.CATHPLOC)));
			formDataGroupList.add(loc);
		}

		// Populate dtEligEnd value from DAM CSES38D
		// if dtEligEnd not equals max end date , add it to prefill data
		if (!TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getEligibilityOutDto().getDtEligEnd())) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(childBackgroundServiceDto.getEligibilityOutDto().getDtEligEnd());
			if (FormConstants.MAX_YEAR != cal.get(cal.YEAR)) {
				BookmarkDto bookmarkdtEligEnd = createBookmark(BookmarkConstants.ELIG_DATE_END,
						TypeConvUtil.formDateFormat(childBackgroundServiceDto.getEligibilityOutDto().getDtEligEnd()));
				bookmarkNonFrmGrpList.add(bookmarkdtEligEnd);
			}
		}

		// Populate dtEligReview value from DAM CSES38D
		BookmarkDto bookmarkdtEligReview = createBookmark(BookmarkConstants.ELIG_DATE_REVIEW,
				TypeConvUtil.formDateFormat(childBackgroundServiceDto.getEligibilityOutDto().getDtEligReview()));
		bookmarkNonFrmGrpList.add(bookmarkdtEligReview);

		// Populate dtEligStart value from DAM CSES38D
		BookmarkDto bookmarkdtEligStart = createBookmark(BookmarkConstants.ELIG_DATE_START,
				TypeConvUtil.formDateFormat(childBackgroundServiceDto.getEligibilityOutDto().getDtEligStart()));
		bookmarkNonFrmGrpList.add(bookmarkdtEligStart);

		// Populate cdEligMedEligGroup value from DAM CSES38D
		BookmarkDto bookmarkCdEligMed = createBookmarkWithCodesTable(BookmarkConstants.ELIG_MED_ELIG_GROUP,
				childBackgroundServiceDto.getEligibilityOutDto().getCdEligMedEligGroup(), CodesConstant.CELIGMED);
		bookmarkNonFrmGrpList.add(bookmarkCdEligMed);

		// Populate cdEligSelected value from DAM CSES38D
		BookmarkDto bookmarkCdEligSelected = createBookmarkWithCodesTable(BookmarkConstants.ELIG_STATUS,
				childBackgroundServiceDto.getEligibilityOutDto().getCdEligSelected(), CodesConstant.CELIGIBI);
		bookmarkNonFrmGrpList.add(bookmarkCdEligSelected);

		// Populate dtCspPermGoalTarget value from DAM CSECB9D
		BookmarkDto bookmarkDtCspPermGoalTarget = createBookmark(BookmarkConstants.PERMANCY_GOALS_DATE,
				TypeConvUtil.formDateFormat(childBackgroundServiceDto.getChildPlanDto().getDtCspPermGoalTarget()));
		bookmarkNonFrmGrpList.add(bookmarkDtCspPermGoalTarget);

		// Populate cdCspPlanPermGoal value from DAM CSECB9D
		BookmarkDto bookmarkCdCspPlanPermGoal = createBookmarkWithCodesTable(BookmarkConstants.PERMANCY_GOALS,
				childBackgroundServiceDto.getChildPlanDto().getCdCspPlanPermGoal(), CodesConstant.CCPPRMGL);
		bookmarkNonFrmGrpList.add(bookmarkCdCspPlanPermGoal);

		if (!TypeConvUtil.isNullOrEmpty(childBackgroundServiceDto.getChildPlanDto().getIdChildPlanEvent())) {
			// Populate NARR_PSY_NEEDS value from DAM CSECB9D
			BlobDataDto blobDataNarrPsyNeeds = createBlobData(BookmarkConstants.NARR_PSY_NEEDS,
					CodesConstant.CP_PSY_NARR,
					childBackgroundServiceDto.getChildPlanDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(blobDataNarrPsyNeeds);

			// Populate NARR_MED_NEEDS value from DAM CSECB9D
			BlobDataDto blobDataNarrMedNeeds = createBlobData(BookmarkConstants.NARR_MED_NEEDS,
					CodesConstant.CP_MDN_NARR,
					childBackgroundServiceDto.getChildPlanDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(blobDataNarrMedNeeds);

			// Populate NARR_EMOT_NEEDS value from DAM CSECB9D
			BlobDataDto blobDataNarrEmotNeeds = createBlobData(BookmarkConstants.NARR_EMOT_NEEDS,
					CodesConstant.CP_SEN_NARR,
					childBackgroundServiceDto.getChildPlanDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(blobDataNarrEmotNeeds);

			// Populate NARR_DEVL_NEEDS value from DAM CSECB9D
			BlobDataDto blobDataNarrDevlNeeds = createBlobData(BookmarkConstants.NARR_DEVL_NEEDS,
					CodesConstant.CP_DVN_NARR,
					childBackgroundServiceDto.getChildPlanDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(blobDataNarrDevlNeeds);

			// Populate NARR_EDUC_NEEDS value from DAM CSECB9D
			BlobDataDto blobDataNarrEduNeeds = createBlobData(BookmarkConstants.NARR_EDUC_NEEDS,
					CodesConstant.CP_EDN_NARR,
					childBackgroundServiceDto.getChildPlanDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(blobDataNarrEduNeeds);
		}

		// Populate dtRemoval value from DAM CDYN10D

		BookmarkDto bookmarkDtRemoval = createBookmark(BookmarkConstants.REMOVAL_DATE,
				TypeConvUtil.formDateFormat(childBackgroundServiceDto.getCnsrvtrshpRemovalDto().getDtRemoval()));
		bookmarkNonFrmGrpList.add(bookmarkDtRemoval);

		// Populate NameSuffix value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.PRIMARY_WORKER_NM_SUFFIX,
				childBackgroundServiceDto.getWorkerDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameSuffix);

		// Populate nmFirst value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameFirst = createBookmark(BookmarkConstants.PRIMARY_WORKER_NAME_FIRST,
				childBackgroundServiceDto.getWorkerDetailDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameFirst);

		// Populate nmLast value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameLast = createBookmark(BookmarkConstants.PRIMARY_WORKER_NAME_LAST,
				childBackgroundServiceDto.getWorkerDetailDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameLast);

		// Populate nmMiddle value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameMiddle = createBookmark(BookmarkConstants.PRIMARY_WORKER_NAME_MIDDLE,
				childBackgroundServiceDto.getWorkerDetailDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameMiddle);

		// Populate AddrMailCodeCity value from DAM CSEC01D
		BookmarkDto bookmarkWorkerAddrMailCodeCity = createBookmark(BookmarkConstants.PRIMARY_WORKER_CITY,
				childBackgroundServiceDto.getWorkerDetailDto().getAddrMailCodeCity());
		bookmarkNonFrmGrpList.add(bookmarkWorkerAddrMailCodeCity);
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBlobDataDtoList(blobDataNonFrmGrpList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);

		return preFillData;

	}

	private void createAddonBookMark(ChildBackgroundServiceDto childBackgroundServiceDto, List<FormDataGroupDto> formDataGroupList) {
		FormDataGroupDto addonPkg = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDONPKG, FormConstants.EMPTY_STRING);
		if(!CollectionUtils.isEmpty(childBackgroundServiceDto.getAddonServicePackages())) {
			String decode = childBackgroundServiceDto.getAddonServicePackages().stream().
					map(pkg -> (String) getDecodedValue(pkg.getSvcPkgAddonCd(), CodesConstant.CSVCCODE)).
					collect(Collectors.joining(","));
			addonPkg.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.ADDON_SERVICE_PACKAGES, decode)));
		}
		formDataGroupList.add(addonPkg);
	}

}
