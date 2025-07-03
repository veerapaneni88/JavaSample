package us.tx.state.dfps.service.forms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ChildPlanItemDto;
import us.tx.state.dfps.common.dto.ChildServicePlanDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.admin.dto.CapsPlacemntDto;
import us.tx.state.dfps.service.childplan.dto.ChildParticipantRowDODto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanDetailsDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:DisasterPlanFormPrefillData will implemented returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form csc18o00. April 02, 2018- 2:04:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class ChildServicePlanFormPrefillData extends DocumentServiceUtil {

	@SuppressWarnings("static-access")
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		ChildServicePlanDto childServicePlanDto = (ChildServicePlanDto) parentDtoobj;

		if (ObjectUtils.isEmpty(childServicePlanDto.getCapsPlacemntDto())) {
			childServicePlanDto.setCapsPlacemntDto(new CapsPlacemntDto());
		}

		if (ObjectUtils.isEmpty(childServicePlanDto.getChildPlanDetailsDto())) {
			childServicePlanDto.setChildPlanDetailsDto(new ChildPlanDetailsDto());
		}
		if (ObjectUtils.isEmpty(childServicePlanDto.getChildPlanItemDtoList())) {
			childServicePlanDto.setChildPlanItemDtoList(new ArrayList<ChildPlanItemDto>());
		}
		if (ObjectUtils.isEmpty(childServicePlanDto.getChildPlanParticipantsList())) {
			childServicePlanDto.setChildPlanParticipantsList(new ArrayList<ChildParticipantRowDODto>());
		}

		if (ObjectUtils.isEmpty(childServicePlanDto.getEventDto())) {
			childServicePlanDto.setEventDto(new EventDto());
		}

		if (ObjectUtils.isEmpty(childServicePlanDto.getEventDto2())) {
			childServicePlanDto.setEventDto2(new EventDto());
		}

		if (ObjectUtils.isEmpty(childServicePlanDto.getGenericCaseInfoDto())) {
			childServicePlanDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}

		if (ObjectUtils.isEmpty(childServicePlanDto.getPersonDto())) {
			childServicePlanDto.setPersonDto(new PersonDto());

		}
		if (ObjectUtils.isEmpty(childServicePlanDto.getPersonDto2())) {
			childServicePlanDto.setPersonDto2(new PersonDto());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// create group CSC18O01 and set prefill data

		if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getCdCspPlanType())
				&& (FormConstants.IPN.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
						|| FormConstants.IPT.equals(childServicePlanDto.getPersonDto().getCdCspPlanType()))
				|| FormConstants.IPL.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
				|| FormConstants.IPP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())) {
			FormDataGroupDto tempInitPlcmtSummFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM, FormConstants.EMPTY_STRING);

			List<BookmarkDto> tempInitPlcmtBookmarkList = new ArrayList<BookmarkDto>();
			List<BlobDataDto> tempInitPlcmtBlobDataList = new ArrayList<BlobDataDto>();
			BookmarkDto dtCspNextReviewBookMark = createBookmark(BookmarkConstants.IPS_DATE_OF_NEXT_REVIEW,
					DateUtils.stringDt(childServicePlanDto.getPersonDto().getDtCspNextReview()));

			BookmarkDto dtCspPermGoalTargetBookMark = createBookmark(BookmarkConstants.IPS_PROJCTD_PERMANENCY_DATE,
					DateUtils.stringDt(childServicePlanDto.getPersonDto().getDtcspPermGoalTarget()));

			BookmarkDto cdCspPlanPermGoalBookMark = createBookmarkWithCodesTable(BookmarkConstants.IPS_PERMANENCY_GOAL,
					childServicePlanDto.getPersonDto().getCspPlanPermGoal(), CodesConstant.CCPPRMGL);

			BookmarkDto cdPersonReligionBookMark = createBookmarkWithCodesTable(BookmarkConstants.IPS_CHILD_RELIGION,
					childServicePlanDto.getPersonDto().getCdPersonReligion(), CodesConstant.CRELIGNS);

			BookmarkDto cdIpsDescripancyBookMark = createBookmark(BookmarkConstants.IPS_DISCREPANCY,
					childServicePlanDto.getPersonDto().getTxtCspLosDiscrepancy());

			BookmarkDto txtLengthOfStayBookMark = createBookmark(BookmarkConstants.IPS_ESTIMATED_LENGTH_OF_STAY,
					childServicePlanDto.getPersonDto().getTxtCspLengthOfStay());
			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getIdChildPlanEvent())) {
				BlobDataDto ipsNarrBlobIshBookMark = createBlobData(BookmarkConstants.IPS_NARR_BLOB_ISH,
						CodesConstant.CP_ISH_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());

				BlobDataDto ipsNarrBlobCplBookMark = createBlobData(BookmarkConstants.IPS_NARR_BLOB_CPL,
						CodesConstant.CP_CPL_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());

				BlobDataDto ipsNarrBlobIchBookMark = createBlobData(BookmarkConstants.IPS_NARR_BLOB_ICH,
						CodesConstant.CP_ICH_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
				tempInitPlcmtBlobDataList.add(ipsNarrBlobIchBookMark);
				tempInitPlcmtBlobDataList.add(ipsNarrBlobCplBookMark);
				tempInitPlcmtBlobDataList.add(ipsNarrBlobIshBookMark);
			}

			tempInitPlcmtBookmarkList.add(dtCspPermGoalTargetBookMark);
			tempInitPlcmtBookmarkList.add(txtLengthOfStayBookMark);
			tempInitPlcmtBookmarkList.add(txtLengthOfStayBookMark);
			tempInitPlcmtBookmarkList.add(cdCspPlanPermGoalBookMark);
			tempInitPlcmtBookmarkList.add(cdPersonReligionBookMark);
			tempInitPlcmtBookmarkList.add(cdIpsDescripancyBookMark);
			tempInitPlcmtBookmarkList.add(dtCspNextReviewBookMark);
			tempInitPlcmtSummFrmDataGrpDto.setBookmarkDtoList(tempInitPlcmtBookmarkList);
			tempInitPlcmtSummFrmDataGrpDto.setBlobDataDtoList(tempInitPlcmtBlobDataList);

			List<FormDataGroupDto> subGroupList = new ArrayList<FormDataGroupDto>();

			// create sub group CSC18O02
			FormDataGroupDto tempCpSubForApprovalFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CP_SUBMITTED_FOR_APPROVAL, FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);
			List<BookmarkDto> tempCpSubBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto dtEventOccuredBookMark = createBookmark(BookmarkConstants.CP_SUBMITTED_FOR_APPROVAL,
					DateUtils.stringDt(childServicePlanDto.getEventDto2().getDtEventOccurred()));
			tempCpSubBookMarkList.add(dtEventOccuredBookMark);
			tempCpSubForApprovalFrmDataGrpDto.setBookmarkDtoList(tempCpSubBookMarkList);
			subGroupList.add(tempCpSubForApprovalFrmDataGrpDto);

			// create sub group CSC18O03 and set prefill data

			FormDataGroupDto tempCpWorkerFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED, FormGroupsConstants.TMPLAT_INIT_PLCMT_SUMM);
			List<BookmarkDto> tempCpWorkerBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto nameSuffixBookMark = createBookmarkWithCodesTable(BookmarkConstants.CP_WORKER_SUFFIX_COMP,
					childServicePlanDto.getPersonDto2().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
			BookmarkDto nameFirstBookMark = createBookmark(BookmarkConstants.CP_WORKER_FIRST_COMP,
					childServicePlanDto.getPersonDto2().getNmPersonFirst());
			BookmarkDto nameLastBookMark = createBookmark(BookmarkConstants.CP_WORKER_LAST_COMP,
					childServicePlanDto.getPersonDto2().getNmPersonLast());

			BookmarkDto nameMiddleBookMark = createBookmark(BookmarkConstants.CP_WORKER_MIDDLE_COMP,
					childServicePlanDto.getPersonDto2().getNmPersonMiddle());
			tempCpWorkerBookMarkList.add(nameMiddleBookMark);
			tempCpWorkerBookMarkList.add(nameLastBookMark);
			tempCpWorkerBookMarkList.add(nameFirstBookMark);
			tempCpWorkerBookMarkList.add(nameSuffixBookMark);
			tempCpWorkerBookMarkList.add(dtEventOccuredBookMark);

			/**
			 * Checking the nameSuffix not equal to null. if its not null, we
			 * set the prefill data for cfzco00
			 */
			List<FormDataGroupDto> tempList = new ArrayList<FormDataGroupDto>();
			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto2().getCdPersonSuffix())) {
				FormDataGroupDto tempCommaFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED);

				tempList.add(tempCommaFrmDataGrpDto);

			}
			tempCpWorkerFrmDataGrpDto.setFormDataGroupList(tempList);
			tempCpWorkerFrmDataGrpDto.setBookmarkDtoList(tempCpWorkerBookMarkList);
			subGroupList.add(tempCpWorkerFrmDataGrpDto);
			tempInitPlcmtSummFrmDataGrpDto.setFormDataGroupList(subGroupList);
			formDataGroupList.add(tempInitPlcmtSummFrmDataGrpDto);

		}

		// create group CSC18O04 and set prefill data

		if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getCdCspPlanType())
				&& (FormConstants.RVW.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
						|| FormConstants.RVT.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
						|| FormConstants.RVL.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
						|| FormConstants.RVP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType()))) {
			FormDataGroupDto tempSvcPlanReviewFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1, FormConstants.EMPTY_STRING);

			List<BookmarkDto> tempSvcPlanReviewBookmarkList = new ArrayList<BookmarkDto>();
			List<BlobDataDto> tempSvcPlanReviewBlobDataList = new ArrayList<BlobDataDto>();
			BookmarkDto dtCspNextReviewBookMark = createBookmark(BookmarkConstants.SPR_DATE_OF_NEXT_REVIEW,
					DateUtils.stringDt(childServicePlanDto.getPersonDto().getDtCspNextReview()));

			BookmarkDto dtCspPermGoalTargetBookMark = createBookmark(BookmarkConstants.SPR_PROJECTED_PERMANENCY_DATE,
					DateUtils.stringDt(childServicePlanDto.getPersonDto().getDtcspPermGoalTarget()));

			BookmarkDto cdCspPlanPermGoalBookMark = createBookmarkWithCodesTable(BookmarkConstants.SPR_PERMANENCY_GOAL,
					childServicePlanDto.getPersonDto().getCspPlanPermGoal(), CodesConstant.CCPPRMGL);

			BookmarkDto cdIpsDescripancyBookMark = createBookmark(BookmarkConstants.SPR_DISCREPANCY_LENGTH_OF_STAY,
					childServicePlanDto.getPersonDto().getTxtCspLosDiscrepancy());

			BookmarkDto txtLengthOfStayBookMark = createBookmark(BookmarkConstants.SPR_EST_LENGTH_OF_STAY,
					childServicePlanDto.getPersonDto().getTxtCspLengthOfStay());
			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getIdChildPlanEvent())) {
				BlobDataDto ipsNarrBlobIshBookMark = createBlobData(BookmarkConstants.IPS_NARR_BLOB_CPL,
						CodesConstant.CP_CPL_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
				tempSvcPlanReviewBlobDataList.add(ipsNarrBlobIshBookMark);
			}

			tempSvcPlanReviewBookmarkList.add(dtCspPermGoalTargetBookMark);
			tempSvcPlanReviewBookmarkList.add(txtLengthOfStayBookMark);
			tempSvcPlanReviewBookmarkList.add(txtLengthOfStayBookMark);
			tempSvcPlanReviewBookmarkList.add(cdCspPlanPermGoalBookMark);
			tempSvcPlanReviewBookmarkList.add(cdIpsDescripancyBookMark);
			tempSvcPlanReviewBookmarkList.add(dtCspNextReviewBookMark);
			tempSvcPlanReviewFrmDataGrpDto.setBookmarkDtoList(tempSvcPlanReviewBookmarkList);
			tempSvcPlanReviewFrmDataGrpDto.setBlobDataDtoList(tempSvcPlanReviewBlobDataList);

			List<FormDataGroupDto> subGroupList = new ArrayList<FormDataGroupDto>();

			// create sub group CSC18O02
			FormDataGroupDto tempCpSubForApprovalFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CP_SUBMITTED_FOR_APPROVAL, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1);
			List<BookmarkDto> tempCpSubBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto dtEventOccuredBookMark = createBookmark(BookmarkConstants.CP_SUBMITTED_FOR_APPROVAL,
					DateUtils.stringDt(childServicePlanDto.getEventDto2().getDtEventOccurred()));
			tempCpSubBookMarkList.add(dtEventOccuredBookMark);
			tempCpSubForApprovalFrmDataGrpDto.setBookmarkDtoList(tempCpSubBookMarkList);
			subGroupList.add(tempCpSubForApprovalFrmDataGrpDto);

			// create sub group CSC18O03 and set prefill data

			FormDataGroupDto tempCpWorkerFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1);
			List<BookmarkDto> tempCpWorkerBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto nameSuffixBookMark = createBookmarkWithCodesTable(BookmarkConstants.CP_WORKER_SUFFIX_COMP,
					childServicePlanDto.getPersonDto2().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
			BookmarkDto nameFirstBookMark = createBookmark(BookmarkConstants.CP_WORKER_FIRST_COMP,
					childServicePlanDto.getPersonDto2().getNmPersonFirst());
			BookmarkDto nameLastBookMark = createBookmark(BookmarkConstants.CP_WORKER_LAST_COMP,
					childServicePlanDto.getPersonDto2().getNmPersonLast());

			BookmarkDto nameMiddleBookMark = createBookmark(BookmarkConstants.CP_WORKER_MIDDLE_COMP,
					childServicePlanDto.getPersonDto2().getNmPersonMiddle());
			tempCpWorkerBookMarkList.add(nameMiddleBookMark);
			tempCpWorkerBookMarkList.add(nameLastBookMark);
			tempCpWorkerBookMarkList.add(nameFirstBookMark);
			tempCpWorkerBookMarkList.add(nameSuffixBookMark);
			tempCpWorkerBookMarkList.add(dtEventOccuredBookMark);
			tempCpWorkerFrmDataGrpDto.setBookmarkDtoList(tempCpWorkerBookMarkList);

			/**
			 * Checking the nameSuffix not equal to null. if its not null, we
			 * set the prefill data for cfzco00
			 */
			List<FormDataGroupDto> tempList = new ArrayList<FormDataGroupDto>();
			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto2().getCdPersonSuffix())) {
				FormDataGroupDto tempCommaFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED);

				tempList.add(tempCommaFrmDataGrpDto);

			}
			tempCpWorkerFrmDataGrpDto.setFormDataGroupList(tempList);
			subGroupList.add(tempCpWorkerFrmDataGrpDto);

			// create group CSC18O05 and set prefill data
			FormDataGroupDto tempSprDateFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SPR_DATE_OF_LAST_PLAN, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1);
			List<BookmarkDto> tempSprDateBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto dtLastUpdateBookMark = createBookmark(BookmarkConstants.SPR_DATE_OF_LAST_PLAN,
					DateUtils.stringDt(childServicePlanDto.getChildPlanDetailsDto().getDateOfLastPlan()));
			tempSprDateBookMarkList.add(dtLastUpdateBookMark);
			tempSprDateFrmDataGrpDto.setBookmarkDtoList(tempSprDateBookMarkList);
			subGroupList.add(tempSprDateFrmDataGrpDto);

			// create group CSC18O07 and set prefill data
			FormDataGroupDto tempSprCurrentFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SPR_CURRENT_PLCMT, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_1);
			List<BookmarkDto> tempSprCurrentBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto cdPlcmtLivArrBookMark = createBookmarkWithCodesTable(
					BookmarkConstants.SPR_PLCMT_LIVING_ARRANGEMENT,
					childServicePlanDto.getCapsPlacemntDto().getCdPlcmtLivArr(), CodesConstant.CPLLAFRM);
			BookmarkDto cdRrscFacilTypeBookMark = createBookmarkWithCodesTable(
					BookmarkConstants.SPR_PLCMT_FACILITY_TYPE,
					childServicePlanDto.getCapsPlacemntDto().getCdRsrcFacilType(), CodesConstant.CFACTYP2);
			BookmarkDto nmPlcmtFacilBookMark = createBookmark(BookmarkConstants.SPR_PLCMT_RESIDENCE_NAME,
					childServicePlanDto.getCapsPlacemntDto().getNmPlcmtFacil());
			BookmarkDto nmPlcmtPersonFullBookMark = createBookmark(BookmarkConstants.SPR_PLCMT_RESIDENCE_NAME,
					childServicePlanDto.getCapsPlacemntDto().getNmPlcmtPersonFull());
			tempSprCurrentBookMarkList.add(nmPlcmtPersonFullBookMark);
			tempSprCurrentBookMarkList.add(cdRrscFacilTypeBookMark);
			tempSprCurrentBookMarkList.add(cdPlcmtLivArrBookMark);
			tempSprCurrentBookMarkList.add(nmPlcmtFacilBookMark);
			tempSprCurrentFrmDataGrpDto.setBookmarkDtoList(tempSprCurrentBookMarkList);

			tempSvcPlanReviewFrmDataGrpDto.setFormDataGroupList(subGroupList);
			formDataGroupList.add(tempSvcPlanReviewFrmDataGrpDto);

		}

		// create group CSC18O15 and set prefill data

		if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getCdCspPlanType())
				&& (FormConstants.FRV.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
						|| FormConstants.FRP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType()))) {
			FormDataGroupDto tempSvcPlanReviewFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2, FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> subGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> tempSvcPlanReviewBookmarkList = new ArrayList<BookmarkDto>();
			List<BlobDataDto> tempSvcPlanReviewBlobDataList = new ArrayList<BlobDataDto>();
			BookmarkDto dtCspNextReviewBookMark = createBookmark(BookmarkConstants.SPR_DATE_OF_NEXT_REVIEW,
					DateUtils.stringDt(childServicePlanDto.getPersonDto().getDtCspNextReview()));

			BookmarkDto dtCspPermGoalTargetBookMark = createBookmark(BookmarkConstants.SPR_PROJECTED_PERMANENCY_DATE,
					DateUtils.stringDt(childServicePlanDto.getPersonDto().getDtcspPermGoalTarget()));

			BookmarkDto cdCspPlanPermGoalBookMark = createBookmarkWithCodesTable(BookmarkConstants.SPR_PERMANENCY_GOAL,
					childServicePlanDto.getPersonDto().getCspPlanPermGoal(), CodesConstant.CCPPRMGL);

			BookmarkDto cdIpsDescripancyBookMark = createBookmark(BookmarkConstants.SPR_DISCREPANCY_LENGTH_OF_STAY,
					childServicePlanDto.getPersonDto().getTxtCspLosDiscrepancy());

			BookmarkDto txtLengthOfStayBookMark = createBookmark(BookmarkConstants.SPR_EST_LENGTH_OF_STAY,
					childServicePlanDto.getPersonDto().getTxtCspLengthOfStay());
			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getIdChildPlanEvent())) {
				BlobDataDto ipsNarrBlobIshBookMark = createBlobData(BookmarkConstants.IPS_NARR_BLOB_CPL,
						CodesConstant.CP_CPL_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
				tempSvcPlanReviewBlobDataList.add(ipsNarrBlobIshBookMark);
			}

			tempSvcPlanReviewBookmarkList.add(dtCspPermGoalTargetBookMark);
			tempSvcPlanReviewBookmarkList.add(txtLengthOfStayBookMark);
			tempSvcPlanReviewBookmarkList.add(txtLengthOfStayBookMark);
			tempSvcPlanReviewBookmarkList.add(cdCspPlanPermGoalBookMark);
			tempSvcPlanReviewBookmarkList.add(cdIpsDescripancyBookMark);
			tempSvcPlanReviewBookmarkList.add(dtCspNextReviewBookMark);
			tempSvcPlanReviewFrmDataGrpDto.setBookmarkDtoList(tempSvcPlanReviewBookmarkList);
			tempSvcPlanReviewFrmDataGrpDto.setBlobDataDtoList(tempSvcPlanReviewBlobDataList);

			// create sub group CSC18O02
			FormDataGroupDto tempCpSubForApprovalFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CP_SUBMITTED_FOR_APPROVAL, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2);
			List<BookmarkDto> tempCpSubBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto dtEventOccuredBookMark = createBookmark(BookmarkConstants.CP_SUBMITTED_FOR_APPROVAL,
					DateUtils.stringDt(childServicePlanDto.getEventDto2().getDtEventOccurred()));
			tempCpSubBookMarkList.add(dtEventOccuredBookMark);
			tempCpSubForApprovalFrmDataGrpDto.setBookmarkDtoList(tempCpSubBookMarkList);
			subGroupList.add(tempCpSubForApprovalFrmDataGrpDto);

			// create sub group CSC18O03 and set prefill data

			FormDataGroupDto tempCpWorkerFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2);
			List<BookmarkDto> tempCpWorkerBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto nameSuffixBookMark = createBookmarkWithCodesTable(BookmarkConstants.CP_WORKER_SUFFIX_COMP,
					childServicePlanDto.getPersonDto2().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
			BookmarkDto nameFirstBookMark = createBookmark(BookmarkConstants.CP_WORKER_FIRST_COMP,
					childServicePlanDto.getPersonDto2().getNmPersonFirst());
			BookmarkDto nameLastBookMark = createBookmark(BookmarkConstants.CP_WORKER_LAST_COMP,
					childServicePlanDto.getPersonDto2().getNmPersonLast());

			BookmarkDto nameMiddleBookMark = createBookmark(BookmarkConstants.CP_WORKER_MIDDLE_COMP,
					childServicePlanDto.getPersonDto2().getNmPersonMiddle());
			tempCpWorkerBookMarkList.add(nameMiddleBookMark);
			tempCpWorkerBookMarkList.add(nameLastBookMark);
			tempCpWorkerBookMarkList.add(nameFirstBookMark);
			tempCpWorkerBookMarkList.add(nameSuffixBookMark);
			tempCpWorkerBookMarkList.add(dtEventOccuredBookMark);
			tempCpWorkerFrmDataGrpDto.setBookmarkDtoList(tempCpWorkerBookMarkList);

			/**
			 * Checking the nameSuffix not equal to null. if its not null, we
			 * set the prefill data for cfzco00
			 */
			List<FormDataGroupDto> tempList = new ArrayList<FormDataGroupDto>();
			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto2().getCdPersonSuffix())) {
				FormDataGroupDto tempCommaFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormGroupsConstants.TMPLAT_CP_WORKER_WHO_COMPLETED);

				tempList.add(tempCommaFrmDataGrpDto);

			}
			tempCpWorkerFrmDataGrpDto.setFormDataGroupList(tempList);
			subGroupList.add(tempCpWorkerFrmDataGrpDto);

			// create group CSC18O05 and set prefill data
			FormDataGroupDto tempSprDateFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SPR_DATE_OF_LAST_PLAN, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2);
			List<BookmarkDto> tempSprDateBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto dtLastPBookMark = createBookmark(BookmarkConstants.SPR_DATE_OF_LAST_PLAN,
					DateUtils.stringDt(childServicePlanDto.getChildPlanDetailsDto().getDateOfLastPlan()));
			tempSprDateBookMarkList.add(dtLastPBookMark);
			tempSprDateFrmDataGrpDto.setBookmarkDtoList(tempSprDateBookMarkList);
			subGroupList.add(tempSprDateFrmDataGrpDto);

			// create group CSC18O07 and set prefill data
			FormDataGroupDto tempSprCurrentFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_SPR_CURRENT_PLCMT, FormGroupsConstants.TMPLAT_SVC_PLAN_REVIEW_2);
			List<BookmarkDto> tempSprCurrentBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto cdPlcmtLivArrBookMark = createBookmarkWithCodesTable(
					BookmarkConstants.SPR_PLCMT_LIVING_ARRANGEMENT,
					childServicePlanDto.getCapsPlacemntDto().getCdPlcmtLivArr(), CodesConstant.CPLLAFRM);
			BookmarkDto cdRrscFacilTypeBookMark = createBookmarkWithCodesTable(
					BookmarkConstants.SPR_PLCMT_FACILITY_TYPE,
					childServicePlanDto.getCapsPlacemntDto().getCdRsrcFacilType(), CodesConstant.CFACTYP2);
			BookmarkDto nmPlcmtFacilBookMark = createBookmark(BookmarkConstants.SPR_PLCMT_RESIDENCE_NAME,
					childServicePlanDto.getCapsPlacemntDto().getNmPlcmtFacil());
			BookmarkDto nmPlcmtPersonFullBookMark = createBookmark(BookmarkConstants.SPR_PLCMT_RESIDENCE_NAME,
					childServicePlanDto.getCapsPlacemntDto().getNmPlcmtPersonFull());
			tempSprCurrentBookMarkList.add(nmPlcmtPersonFullBookMark);
			tempSprCurrentBookMarkList.add(cdRrscFacilTypeBookMark);
			tempSprCurrentBookMarkList.add(cdPlcmtLivArrBookMark);
			tempSprCurrentBookMarkList.add(nmPlcmtFacilBookMark);
			tempSprCurrentFrmDataGrpDto.setBookmarkDtoList(tempSprCurrentBookMarkList);

			tempSvcPlanReviewFrmDataGrpDto.setFormDataGroupList(subGroupList);
			formDataGroupList.add(tempSvcPlanReviewFrmDataGrpDto);

		}

		// create group CSC18O08 and set prefill data

		if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getCdCspPlanType())
				&& (FormConstants.IPN.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
						|| FormConstants.IPT.equals(childServicePlanDto.getPersonDto().getCdCspPlanType()))
				|| FormConstants.IPL.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
				|| FormConstants.IPP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
				|| FormConstants.RVW.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
				|| FormConstants.RVT.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
				|| FormConstants.RVL.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
				|| FormConstants.RVP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())) {
			List<FormDataGroupDto> subGroupList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto tempObjNeedsFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_OBJ_NEEDS,
					FormConstants.EMPTY_STRING);

			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getCdCspPlanType())
					&& (FormConstants.RVW.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
							|| FormConstants.RVT.equals(childServicePlanDto.getPersonDto().getCdCspPlanType()))
					|| FormConstants.RVL.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
					|| FormConstants.RVP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())) {

				FormDataGroupDto tempReviewHeadingFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_REVIEW_HEADING, FormGroupsConstants.TMPLAT_OBJ_NEEDS);
				subGroupList.add(tempReviewHeadingFrmDataGrpDto);
			}

			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getCdCspPlanType())
					&& (FormConstants.IPN.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
							|| FormConstants.IPT.equals(childServicePlanDto.getPersonDto().getCdCspPlanType()))
					|| FormConstants.IPL.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
					|| FormConstants.IPP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())) {
				FormDataGroupDto tempInitialHeadingFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_INITIAL_HEADING, FormGroupsConstants.TMPLAT_OBJ_NEEDS);
				subGroupList.add(tempInitialHeadingFrmDataGrpDto);
			}
			// List<BookmarkDto> tempObjNeedsBookmarkList = new
			// ArrayList<BookmarkDto>();
			List<BlobDataDto> tempObjNeedsBlobDataList = new ArrayList<BlobDataDto>();
			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getIdChildPlanEvent())) {
				BlobDataDto objNarrIbpBookMark = createBlobData(BookmarkConstants.OBJ_NARR_IBP,
						CodesConstant.CP_IBP_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());

				BlobDataDto objNarrSenBookMark = createBlobData(BookmarkConstants.OBJ_NARR_SEN,
						CodesConstant.CP_SEN_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());

				BlobDataDto objNarrPhyBookMark = createBlobData(BookmarkConstants.OBJ_NARR_PHY,
						CodesConstant.CP_PHY_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
				BlobDataDto objNarrDvnBookMark = createBlobData(BookmarkConstants.OBJ_NARR_DVN,
						CodesConstant.CP_DVN_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
				BlobDataDto objNarrMdnBookMark = createBlobData(BookmarkConstants.OBJ_NARR_MDN,
						CodesConstant.CP_MDN_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
				BlobDataDto objNarrEdnBookMark = createBlobData(BookmarkConstants.OBJ_NARR_EDN,
						CodesConstant.CP_EDN_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
				tempObjNeedsBlobDataList.add(objNarrEdnBookMark);
				tempObjNeedsBlobDataList.add(objNarrSenBookMark);
				tempObjNeedsBlobDataList.add(objNarrMdnBookMark);
				tempObjNeedsBlobDataList.add(objNarrDvnBookMark);
				tempObjNeedsBlobDataList.add(objNarrIbpBookMark);
				tempObjNeedsBlobDataList.add(objNarrPhyBookMark);
			}

			tempObjNeedsFrmDataGrpDto.setBlobDataDtoList(tempObjNeedsBlobDataList);

			// create sub group CSC18O17

			tempObjNeedsFrmDataGrpDto.setFormDataGroupList(subGroupList);
			formDataGroupList.add(tempObjNeedsFrmDataGrpDto);

		}

		// create group CSC18O09 and set prefill data

		if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getCdCspPlanType())
				&& (FormConstants.IPT.equals(childServicePlanDto.getPersonDto().getCdCspPlanType()))
				|| FormConstants.IPP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
				|| FormConstants.RVT.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
				|| FormConstants.RVP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())) {
			FormDataGroupDto tempTherapNeedsFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_THERAP_NEEDS,
					FormConstants.EMPTY_STRING);

			// List<BookmarkDto> tempObjNeedsBookmarkList = new
			// ArrayList<BookmarkDto>();
			List<BlobDataDto> tempTherapNeedsBlobDataList = new ArrayList<BlobDataDto>();
			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getIdChildPlanEvent())) {
				BlobDataDto therapNarrFamBookMark = createBlobData(BookmarkConstants.THERAP_NARR_FAM,
						CodesConstant.CP_FAM_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());

				BlobDataDto therapNarrTrmBookMark = createBlobData(BookmarkConstants.THERAP_NARR_TRM,
						CodesConstant.CP_TRM_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
				BlobDataDto therapNarrRecBookMark = createBlobData(BookmarkConstants.THERAP_NARR_REC,
						CodesConstant.CP_REC_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());

				BlobDataDto therapNarrPsyBookMark = createBlobData(BookmarkConstants.THERAP_NARR_PSY,
						CodesConstant.CP_PSY_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
				tempTherapNeedsBlobDataList.add(therapNarrPsyBookMark);
				tempTherapNeedsBlobDataList.add(therapNarrRecBookMark);
				tempTherapNeedsBlobDataList.add(therapNarrTrmBookMark);
				tempTherapNeedsBlobDataList.add(therapNarrFamBookMark);
			}
			tempTherapNeedsFrmDataGrpDto.setBlobDataDtoList(tempTherapNeedsBlobDataList);
			formDataGroupList.add(tempTherapNeedsFrmDataGrpDto);

		}

		// create group CSC18O10 and set prefill data

		if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getCdCspPlanType())
				&& (FormConstants.IPL.equals(childServicePlanDto.getPersonDto().getCdCspPlanType()))
				|| FormConstants.IPP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
				|| FormConstants.RVL.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
				|| FormConstants.RVP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())
				|| FormConstants.FRP.equals(childServicePlanDto.getPersonDto().getCdCspPlanType())) {
			FormDataGroupDto tempPalFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PAL,
					FormConstants.EMPTY_STRING);

			// List<BookmarkDto> tempObjNeedsBookmarkList = new
			// ArrayList<BookmarkDto>();
			List<BlobDataDto> tempPalBlobDataList = new ArrayList<BlobDataDto>();
			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getIdChildPlanEvent())) {
				BlobDataDto palNarrPdoBookMark = createBlobData(BookmarkConstants.PAL_NARR_PDO,
						CodesConstant.CP_PDO_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
				tempPalBlobDataList.add(palNarrPdoBookMark);
			}
			if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getIdChildPlanEvent())) {
				BlobDataDto palNarrPalBookMark = createBlobData(BookmarkConstants.PAL_NARR_PAL,
						CodesConstant.CP_PAL_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
				tempPalBlobDataList.add(palNarrPalBookMark);
			}
			tempPalFrmDataGrpDto.setBlobDataDtoList(tempPalBlobDataList);
			formDataGroupList.add(tempPalFrmDataGrpDto);
		}

		/**
		 * Checking the nameSuffix not equal to null. if its not null, we set
		 * the prefill data for cfzco00
		 */
		if (ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getCdPersonSuffix())) {
			FormDataGroupDto tempCommaFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);

			formDataGroupList.add(tempCommaFrmDataGrpDto);

		}

		// create group CSC18o20
		SimpleDateFormat df = new SimpleDateFormat("mm/dd/yyyy hh:mm:ss a", Locale.US);
		try {
			if (childServicePlanDto.getEventDto2().getDtEventOccurred()
					.compareTo(df.parse(FormConstants.DT_EVENT_OCCURRED)) > 0) {

				FormDataGroupDto tempDispIchIshFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DISP_ICH_ISH, FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> tempList = new ArrayList<FormDataGroupDto>();

				if (childServicePlanDto.getEventDto2().getIdEvent()
						.equals(childServicePlanDto.getChildPlanDetailsDto().getIdChildPlanEvent())) {
					if (FormConstants.ADP.equals(childServicePlanDto.getChildPlanDetailsDto().getCspPlanType())
							|| FormConstants.FRP.equals(childServicePlanDto.getChildPlanDetailsDto().getCspPlanType())
							|| FormConstants.FRV.equals(childServicePlanDto.getChildPlanDetailsDto().getCspPlanType())
							|| FormConstants.RVL.equals(childServicePlanDto.getChildPlanDetailsDto().getCspPlanType())
							|| FormConstants.RVP.equals(childServicePlanDto.getChildPlanDetailsDto().getCspPlanType())
							|| FormConstants.RVT.equals(childServicePlanDto.getChildPlanDetailsDto().getCspPlanType())
							|| FormConstants.RVW
									.equals(childServicePlanDto.getChildPlanDetailsDto().getCspPlanType())) {
						FormDataGroupDto tempIchIshFrmDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_ICH_ISH, FormGroupsConstants.TMPLAT_DISP_ICH_ISH);
						tempList.add(tempIchIshFrmDataGrpDto);

					}
				}
				tempDispIchIshFrmDataGrpDto.setFormDataGroupList(tempList);
				formDataGroupList.add(tempDispIchIshFrmDataGrpDto);

			}
		} catch (ParseException e) {

			e.printStackTrace();
		}

		// create group CSC18O12 and set prefill data

		for (ChildPlanItemDto childPlanItemDto : childServicePlanDto.getChildPlanItemDtoList()) {
			FormDataGroupDto tempNeedTaskFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_NEED_TASK_SVC_RPT_GP, FormConstants.EMPTY_STRING);
			ArrayList<BookmarkDto> tempNeedTaskBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto cspItemNeedBookMark = createBookmarkWithCodesTable(BookmarkConstants.NEED_TASK_SVC_NEED,
					childPlanItemDto.getCdCspItemNeed(), CodesConstant.CCPNEEDS);

			BookmarkDto cspItemMethodEvalBookMark = createBookmark(BookmarkConstants.NEED_TASK_SVC_METHOD_EVAL,
					childPlanItemDto.getTxtCspItemMethodEval());

			BookmarkDto cspItemSvcFreqBookMark = createBookmark(BookmarkConstants.NEED_TASK_SVC_SVC_TIME_FRAME,
					childPlanItemDto.getTxtCspItemSvcFreq());

			BookmarkDto cspItemTaskFreqBookMark = createBookmark(BookmarkConstants.NEED_TASK_SVC_TASK_TIME_FRAME,
					childPlanItemDto.getTxtCspItemTaskFreq());

			BookmarkDto cspServiceBookMark = createBookmark(BookmarkConstants.NEED_TASK_SVC_SERVICE,
					childPlanItemDto.getTxtCspService());

			BookmarkDto cspTaskBookMark = createBookmark(BookmarkConstants.NEED_TASK_SVC_TASK,
					childPlanItemDto.getTxtCspTask());
			tempNeedTaskBookmarkList.add(cspServiceBookMark);
			tempNeedTaskBookmarkList.add(cspItemTaskFreqBookMark);
			tempNeedTaskBookmarkList.add(cspItemSvcFreqBookMark);
			tempNeedTaskBookmarkList.add(cspItemMethodEvalBookMark);
			tempNeedTaskBookmarkList.add(cspItemNeedBookMark);
			tempNeedTaskBookmarkList.add(cspTaskBookMark);
			tempNeedTaskFrmDataGrpDto.setBookmarkDtoList(tempNeedTaskBookmarkList);
			formDataGroupList.add(tempNeedTaskFrmDataGrpDto);

		}

		// create group CSC18O14 and set prefill data
		for (ChildParticipantRowDODto childParticipantRowDODto : childServicePlanDto.getChildPlanParticipantsList()) {
			FormDataGroupDto tempPartInfoFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PART_INFO_RPT_GP, FormConstants.EMPTY_STRING);
			ArrayList<BookmarkDto> tempPartInfoBookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto dtCspDateNotifiedBookMark = createBookmark(BookmarkConstants.PART_INFO_DATE_NOTIFICATION,
					DateUtils.stringDt(childParticipantRowDODto.getDtDtCspDateNotified()));
			BookmarkDto dtCspPartCopyGivenBookMark = createBookmark(BookmarkConstants.PART_INFO_DATE_COPY_GIVEN,
					DateUtils.stringDt(childParticipantRowDODto.getDtDtCspPartCopyGiven()));

			BookmarkDto dtCspPartParticipateBookMark = createBookmark(BookmarkConstants.PART_INFO_DATE_PARTICIPATION,
					DateUtils.stringDt(childParticipantRowDODto.getDtDtCspPartParticipate()));
			BookmarkDto dtCspPartNotiTypeBookMark = createBookmarkWithCodesTable(
					BookmarkConstants.PART_INFO_TYPE_NOTIFICATION, childParticipantRowDODto.getSzCdCspPartNotifType(),
					CodesConstant.CPPTNOPE);

			BookmarkDto dtCspPartFullBookMark = createBookmark(BookmarkConstants.PART_INFO_NAME_FULL,
					childParticipantRowDODto.getSzNmCspPartFull());

			BookmarkDto dtCspPartRelationshipBookMark = createBookmark(BookmarkConstants.PART_INFO_RELATIONSHIP,
					childParticipantRowDODto.getSzSdsCspPartRelationship());
			tempPartInfoBookmarkList.add(dtCspPartRelationshipBookMark);
			tempPartInfoBookmarkList.add(dtCspPartNotiTypeBookMark);
			tempPartInfoBookmarkList.add(dtCspPartParticipateBookMark);
			tempPartInfoBookmarkList.add(dtCspPartCopyGivenBookMark);
			tempPartInfoBookmarkList.add(dtCspDateNotifiedBookMark);
			tempPartInfoBookmarkList.add(dtCspPartFullBookMark);
			tempPartInfoFrmDataGrpDto.setBookmarkDtoList(tempPartInfoBookmarkList);

			formDataGroupList.add(tempPartInfoFrmDataGrpDto);

		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		List<BlobDataDto> blobDataNonFrmGrpList = new ArrayList<BlobDataDto>();

		// Populate NmCase from DAM CSEC02D
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				childServicePlanDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkNmCase);

		// Populate IdCase value from DAM CSEC02D
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				childServicePlanDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkIdCase);

		// Populate Child DOB value from DAM CSES27D
		BookmarkDto bookmarkChildDob = createBookmark(BookmarkConstants.TITLE_CHILD_BIRTH_DT,
				DateUtils.stringDt(childServicePlanDto.getPersonDto().getDtPersonBirth()));
		bookmarkNonFrmGrpList.add(bookmarkChildDob);

		// Populate CspChildPlanType value from DAM CSES27D

		BookmarkDto bookmarkCspPlanType = createBookmarkWithCodesTable(BookmarkConstants.TITLE_CHILD_PLAN_TYPE,
				childServicePlanDto.getPersonDto().getCdCspPlanType(), CodesConstant.CCPPLNTP);
		bookmarkNonFrmGrpList.add(bookmarkCspPlanType);

		// Populate TITLE_CHILD_NAME_SUFFIX value from DAM CSES27D
		BookmarkDto bookmarkChildNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.TITLE_CHILD_NAME_SUFFIX,
				childServicePlanDto.getPersonDto().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkChildNameSuffix);

		// Populate first name value from DAM CSES27D
		BookmarkDto bookmarkChildFirst = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_FIRST,
				childServicePlanDto.getPersonDto().getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkChildFirst);

		// Populate Last name value from DAM CSES27D
		BookmarkDto bookmarkChildLast = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_LAST,
				childServicePlanDto.getPersonDto().getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkChildLast);

		// Populate middle name value from DAM CSES27D
		BookmarkDto bookmarkChildMiddle = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_MIDDLE,
				childServicePlanDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkChildMiddle);

		// Populate CHILD_PLAN_PARTICIP_COMMEN value from DAM CSES27D
		BookmarkDto bookmarkParticpComment = createBookmark(BookmarkConstants.CHILD_PLAN_PARTICIP_COMMENT,
				childServicePlanDto.getPersonDto().getTxtCspParticpatnComment());
		bookmarkNonFrmGrpList.add(bookmarkParticpComment);

		if (!ObjectUtils.isEmpty(childServicePlanDto.getPersonDto().getIdChildPlanEvent())) {
			// Populate all blobs value from DAM CSES27D
			BlobDataDto ispNarrVisBlobData = createBlobData(BookmarkConstants.ISP_NARR_VIS, CodesConstant.CP_VIS_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(ispNarrVisBlobData);

			BlobDataDto ispNarrChgBlobData = createBlobData(BookmarkConstants.ISP_NARR_CHG, CodesConstant.CP_CHG_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(ispNarrChgBlobData);

			BlobDataDto ipsNarrAsfBlobData = createBlobData(BookmarkConstants.IPS_NARR_ASF, CodesConstant.CP_ASF_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(ipsNarrAsfBlobData);

			BlobDataDto needTaskSvcNarrSccBlobData = createBlobData(BookmarkConstants.NEED_TASK_SVC_NARR_SSC,
					CodesConstant.CP_SSC_NARR, childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(needTaskSvcNarrSccBlobData);

			BlobDataDto ipsNarrEocBlobData = createBlobData(BookmarkConstants.IPS_NARR_EOC, CodesConstant.CP_EOC_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(ipsNarrEocBlobData);

			BlobDataDto objNarrTrvBlobData = createBlobData(BookmarkConstants.OBJ_NARR_TRV, CodesConstant.CP_TRV_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(objNarrTrvBlobData);

			BlobDataDto objNarrDscBlobData = createBlobData(BookmarkConstants.OBJ_NARR_DSC, CodesConstant.CP_DSC_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(objNarrDscBlobData);

			BlobDataDto objNarrSupBlobData = createBlobData(BookmarkConstants.OBJ_NARR_SUP, CodesConstant.CP_SUP_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(objNarrSupBlobData);

			BlobDataDto ipsNarrPlsBlobData = createBlobData(BookmarkConstants.IPS_NARR_PLS, CodesConstant.CP_PLS_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(ipsNarrPlsBlobData);

			BlobDataDto ipsNarrPlpBlobData = createBlobData(BookmarkConstants.IPS_NARR_PLP, CodesConstant.CP_PLP_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(ipsNarrPlpBlobData);

			BlobDataDto objNarrAprBlobData = createBlobData(BookmarkConstants.OBJ_NARR_APR, CodesConstant.CP_APR_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(objNarrAprBlobData);

			BlobDataDto objNarrCnpBlobData = createBlobData(BookmarkConstants.OBJ_NARR_CNP, CodesConstant.CP_CNP_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(objNarrCnpBlobData);

			BlobDataDto objNarrPerBlobData = createBlobData(BookmarkConstants.OBJ_NARR_PER, CodesConstant.CP_PER_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(objNarrPerBlobData);

			BlobDataDto objNarrAopBlobData = createBlobData(BookmarkConstants.OBJ_NARR_AOP, CodesConstant.CP_AOP_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(objNarrAopBlobData);

			BlobDataDto ipsNarrTplBlobData = createBlobData(BookmarkConstants.IPS_NARR_TPL, CodesConstant.CP_TPL_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(ipsNarrTplBlobData);

			BlobDataDto objNarrVisBlobData = createBlobData(BookmarkConstants.OBJ_NARR_VIS, CodesConstant.CP_PFC_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(objNarrVisBlobData);

			BlobDataDto objNarrPchBlobData = createBlobData(BookmarkConstants.OBJ_NARR_PCH, CodesConstant.CP_PCH_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(objNarrPchBlobData);

			BlobDataDto ispNarrAppBlobData = createBlobData(BookmarkConstants.IPS_NARR_APP, CodesConstant.CP_APP_NARR,
					childServicePlanDto.getPersonDto().getIdChildPlanEvent().intValue());
			blobDataNonFrmGrpList.add(ispNarrAppBlobData);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBlobDataDtoList(blobDataNonFrmGrpList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}

}
