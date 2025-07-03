package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.childplan.dto.*;
import us.tx.state.dfps.service.common.*;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.ArrayList;
import java.util.List;

@Component
/**
 * Name:ChildPlanOfServicePrefillData Description: ChildPlanOfServicePrefillData
 * will implemented returnPrefillData operation defined in DocumentServiceUtil
 * Interface to populate the prefill data for form ChildPlanOfService.(Child
 * Plan Of Service) April 24, 2018 - 04:40:29 PM
 */
public class ChildPlanOfServicePrefillData extends DocumentServiceUtil {

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		ChildPlanOfServiceDto childPlanOfServiceDto = (ChildPlanOfServiceDto) parentDtoobj;

		if (null == childPlanOfServiceDto.getChildPlanOfServiceDtlDto()) {
			childPlanOfServiceDto.setChildPlanOfServiceDtlDto(new ChildPlanOfServiceDtlDto());
		}
		if (null == childPlanOfServiceDto.getChildPlanInformationDto()) {
			childPlanOfServiceDto.setChildPlanInformationDto(new ChildPlanInformationDto());
		}
		List<FormDataGroupDto> formDataGroupDtoList = new ArrayList<FormDataGroupDto>();

		// Adding independent bookmarks
		List<BookmarkDto> bookmarkDtoList = new ArrayList<BookmarkDto>();

		BookmarkDto bookmarkDtoCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getNmCase());
		bookmarkDtoList.add(bookmarkDtoCaseNm);

		BookmarkDto bookmarkDtoCaseNo = createBookmark(BookmarkConstants.TITLE_CASE_NBR,
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIdCase());
		bookmarkDtoList.add(bookmarkDtoCaseNo);

		BookmarkDto bookmarkDtoChildNm = createBookmark(BookmarkConstants.TITLE_CHILD_NM,
				childPlanOfServiceDto.getChildPlanInformationDto().getNmChild());
		bookmarkDtoList.add(bookmarkDtoChildNm);

		BookmarkDto bookmarkDtoChildDOB = createBookmark(BookmarkConstants.TITLE_CHILD_DOB,
				DateUtils.stringDt(childPlanOfServiceDto.getChildPlanInformationDto().getDtChildBirth()));
		bookmarkDtoList.add(bookmarkDtoChildDOB);

		// DFPS INFORMATION
		BookmarkDto bookmarkDtoCaseWrkrNm = createBookmark(BookmarkConstants.WORKER_NAME,
				childPlanOfServiceDto.getChildPlanInformationDto().getNmDfpsPrmryWorker());
		bookmarkDtoList.add(bookmarkDtoCaseWrkrNm);

		BookmarkDto bookmarkDtoSupervisrNm = createBookmark(BookmarkConstants.SUPERVISOR_NAME,
				childPlanOfServiceDto.getChildPlanInformationDto().getNmDfpsSupervisor());
		bookmarkDtoList.add(bookmarkDtoSupervisrNm);

		BookmarkDto bookmarkDtoDfpsUnit = createBookmark(BookmarkConstants.DFPS_UNIT,
				childPlanOfServiceDto.getChildPlanInformationDto().getNmCaseWorkerUnit());
		bookmarkDtoList.add(bookmarkDtoDfpsUnit);

		// CHILD INFORMATION
		BookmarkDto bookmarkDtoChildFullNm = createBookmark(BookmarkConstants.CHILD_FULL_NAME,
				childPlanOfServiceDto.getChildPlanInformationDto().getNmChild());
		bookmarkDtoList.add(bookmarkDtoChildFullNm);

		BookmarkDto bookmarkDtoChildBirthDt = createBookmark(BookmarkConstants.CHILD_DOB,
				DateUtils.stringDt(childPlanOfServiceDto.getChildPlanInformationDto().getDtChildBirth()));
		bookmarkDtoList.add(bookmarkDtoChildBirthDt);

		BookmarkDto bookmarkDtoGender = createBookmarkWithCodesTable(BookmarkConstants.CHILD_SEX,
				childPlanOfServiceDto.getChildPlanInformationDto().getCdChildGender(), CodesConstant.CSEX);
		bookmarkDtoList.add(bookmarkDtoGender);

		BookmarkDto bookmarkDtoLegalRegion = createBookmark(BookmarkConstants.CHILD_LEGAL_REGION,
				childPlanOfServiceDto.getChildPlanInformationDto().getCdChildLglReg());
		bookmarkDtoList.add(bookmarkDtoLegalRegion);

		BookmarkDto bookmarkDtoEthnic = createBookmarkWithCodesTable(BookmarkConstants.CHILD_ETHNIC,
				childPlanOfServiceDto.getChildPlanInformationDto().getCdChildEthnicity(), CodesConstant.CINDETHN);
		bookmarkDtoList.add(bookmarkDtoEthnic);

		BookmarkDto bookmarkDtoRace = createBookmarkWithCodesTable(BookmarkConstants.CHILD_RACE,
				childPlanOfServiceDto.getChildPlanInformationDto().getCdChildRace(), CodesConstant.CRACE);
		bookmarkDtoList.add(bookmarkDtoRace);

		BookmarkDto bookmarkDtoLegalCounty = createBookmarkWithCodesTable(BookmarkConstants.CHILD_LEGAL_COUNTY,
				childPlanOfServiceDto.getChildPlanInformationDto().getCdChildLglCnty(), CodesConstant.CCOUNT);
		bookmarkDtoList.add(bookmarkDtoLegalCounty);

		BookmarkDto bookmarkDtoPlanType = createBookmarkWithCodesTable(BookmarkConstants.TYPE_PLAN,
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType(), CodesConstant.CCPPLNTP);
		bookmarkDtoList.add(bookmarkDtoPlanType);

		// To conditionally Display Reason for Review
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getTxtTriggRvwRsn())) {
			FormDataGroupDto formDataGrpReasonReview = createFormDataGroup(FormGroupsConstants.TMPLAT_REASON_REVIEW,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkDtoReasonReviewList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDtoReasonReview = createBookmark(BookmarkConstants.REASON_REVIEW,
					formatTextValue(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getTxtTriggRvwRsn()));
			bookmarkDtoReasonReviewList.add(bookmarkDtoReasonReview);
			formDataGrpReasonReview.setBookmarkDtoList(bookmarkDtoReasonReviewList);
			formDataGroupDtoList.add(formDataGrpReasonReview);

		}

		BookmarkDto bookmarkDtoCurrCare = createBookmarkWithCodesTable(BookmarkConstants.CURRENT_CARE,
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCurrentLvlCare(), CodesConstant.CBILPLOC);
		bookmarkDtoList.add(bookmarkDtoCurrCare);

		BookmarkDto bookmarkDtoStartDt = createBookmark(BookmarkConstants.START_DATE,
				DateUtils.stringDt(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtStartLoc()));
		bookmarkDtoList.add(bookmarkDtoStartDt);

		BookmarkDto bookmarkDtoEndDt = createBookmark(BookmarkConstants.END_DATE,
				DateUtils.stringDt(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtEndLoc()));
		bookmarkDtoList.add(bookmarkDtoEndDt);

		//BR15.6
		//Recommended Service Package
		if(!ObjectUtils.isEmpty(childPlanOfServiceDto.getRcmdSvcPackageDtlDto())) {
			BookmarkDto bookmarkDtoRecSvcPkg = createBookmarkWithCodesTable(BookmarkConstants.RCMD_SERVICE_PKG,
					childPlanOfServiceDto.getRcmdSvcPackageDtlDto().getSvcPkgCd(), CodesConstant.CSVCCODE);
			bookmarkDtoList.add(bookmarkDtoRecSvcPkg);

			BookmarkDto bookmarkDtoRcmdStartDt = createBookmark(BookmarkConstants.RCMD_SVC_PKG_START_DATE,
					DateUtils.stringDt(childPlanOfServiceDto.getRcmdSvcPackageDtlDto().getDtSvcStart()));
			bookmarkDtoList.add(bookmarkDtoRcmdStartDt);

			BookmarkDto bookmarkDtoRcmdEndDt = createBookmark(BookmarkConstants.RCMD_SVC_PKG_END_DATE,
					DateUtils.stringDt(childPlanOfServiceDto.getRcmdSvcPackageDtlDto().getDtSvcEnd()));
			bookmarkDtoList.add(bookmarkDtoRcmdEndDt);
		}
		//Selected Service Package
		if(!ObjectUtils.isEmpty(childPlanOfServiceDto.getSelSvcPackageDtlDto())) {
			BookmarkDto bookmarkDtoSelSvcPkg = createBookmarkWithCodesTable(BookmarkConstants.SLCTD_SERVICE_PKG,
					childPlanOfServiceDto.getSelSvcPackageDtlDto().getSvcPkgCd(), CodesConstant.CSVCCODE);
			bookmarkDtoList.add(bookmarkDtoSelSvcPkg);

			BookmarkDto bookmarkDtoSelStartDt = createBookmark(BookmarkConstants.SLCTD_SVC_PKG_START_DATE,
					DateUtils.stringDt(childPlanOfServiceDto.getSelSvcPackageDtlDto().getDtSvcStart()));
			bookmarkDtoList.add(bookmarkDtoSelStartDt);

			BookmarkDto bookmarkDtoSelEndDt = createBookmark(BookmarkConstants.SLCTD_SVC_PKG_END_DATE,
					DateUtils.stringDt(childPlanOfServiceDto.getSelSvcPackageDtlDto().getDtSvcEnd()));
			bookmarkDtoList.add(bookmarkDtoSelEndDt);
		}


		BookmarkDto bookmarkDtoDtPlanCompleted = createBookmark(BookmarkConstants.DATE_PLAN_COMPLETE,
				DateUtils.stringDt(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtCspPlanCompleted()));
		bookmarkDtoList.add(bookmarkDtoDtPlanCompleted);

		BookmarkDto bookmarkDtoDtCurrPlacmnt = createBookmark(BookmarkConstants.EFFECTIVE_DATE_PLAN,
				DateUtils.stringDt(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtCspEfctvPlan()));
		bookmarkDtoList.add(bookmarkDtoDtCurrPlacmnt);

		//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification
		BookmarkDto bookmarkDtoDtInitialBor = createBookmark(BookmarkConstants.DATE_INITIAL_BOR,
				DateUtils.stringDt(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtInitialBor()));
		bookmarkDtoList.add(bookmarkDtoDtInitialBor);

		BookmarkDto bookmarkDtoDtMostRecentBor = createBookmark(BookmarkConstants.DATE_MOST_RECENT_BOR,
				DateUtils.stringDt(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtMostRecentBor()));
		bookmarkDtoList.add(bookmarkDtoDtMostRecentBor);

		// PLACEMENT INFORMATION

		BookmarkDto bookmarkDtoDtCurrPlcmnt = createBookmark(BookmarkConstants.DATE_CURRENT_PLACEMENT,
				DateUtils.stringDt(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtPlcmt()));
		bookmarkDtoList.add(bookmarkDtoDtCurrPlcmnt);

		BookmarkDto bookmarkDtoPlacmntType = createBookmarkWithCodesTable(BookmarkConstants.PLACEMENT_TYPE,
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdPlcmtTyp(), CodesConstant.CPLMNTYP);
		bookmarkDtoList.add(bookmarkDtoPlacmntType);

		BookmarkDto bookmarkDtoAgencyNm = createBookmark(BookmarkConstants.AGENCY_NAME,
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getNmAgncy());
		bookmarkDtoList.add(bookmarkDtoAgencyNm);

		// To conditionally Display
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getNmCaseMgr())) {
			FormDataGroupDto formDataGrpAgencyCaseSupervisr = createFormDataGroup(
					FormGroupsConstants.TMPLAT_AGENCY_CASE_SUPERVISOR, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkDtoAgencyCaseSupervisrList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDtoAgencyCaseSupervisr = createBookmark(BookmarkConstants.AGENCY_CASE_SUPERVISOR,
					childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getNmCaseMgr());
			bookmarkDtoAgencyCaseSupervisrList.add(bookmarkDtoAgencyCaseSupervisr);
			formDataGrpAgencyCaseSupervisr.setBookmarkDtoList(bookmarkDtoAgencyCaseSupervisrList);
			formDataGroupDtoList.add(formDataGrpAgencyCaseSupervisr);
		}

		// To conditionally Display
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIndChildRcvngSvcs())) {
			FormDataGroupDto formDataGrpChildReceivingTreatmnt = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CHILD_RECEIVING_TREATMENT, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkDtoAgencyCaseSupervisrList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDtoChildReceivingTreatmnt = createBookmark(BookmarkConstants.CHILD_RECEIVING_TREATMENT,
					childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIndChildRcvngSvcs()
							.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT : ServiceConstants.NO_TEXT);
			bookmarkDtoAgencyCaseSupervisrList.add(bookmarkDtoChildReceivingTreatmnt);
			formDataGrpChildReceivingTreatmnt.setBookmarkDtoList(bookmarkDtoAgencyCaseSupervisrList);
			formDataGroupDtoList.add(formDataGrpChildReceivingTreatmnt);
		}

		String childTreatmentType = ServiceConstants.SPACE;

		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIndTrtmntSvcsTypEmdo())) {
			if (childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIndTrtmntSvcsTypEmdo()
					.equals(ServiceConstants.Y)) {
				childTreatmentType += "Emotional Disorders ,";
			}

		}
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIndTrtmntSvcsTypPmn())) {

			if (childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIndTrtmntSvcsTypPmn()
					.equals(ServiceConstants.Y)) {
				childTreatmentType += "Primary Medical Needs ,";
			}
		}
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIndTrtmntSvcsTypId())) {

			if (childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIndTrtmntSvcsTypId()
					.equals(ServiceConstants.Y)) {
				childTreatmentType += "Intellectual Disability ,";
			}
		}
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIndTrtmntSvcsTypAsd())) {
			if (childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIndTrtmntSvcsTypAsd()
					.equals(ServiceConstants.Y)) {
				childTreatmentType += "Autism Spectrum Disorder ,";
			}
		}

		// To conditionally Display
		if (!ObjectUtils.isEmpty(childTreatmentType)) {
			FormDataGroupDto formDataGrpTreatmntType = createFormDataGroup(
					FormGroupsConstants.TMPLAT_CHILD_TREATMENT_TYPE, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkDtoTreatmntTypeList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDtoTreatmntType = createBookmark(BookmarkConstants.CHILD_TREATMENT_TYPE,
					childTreatmentType.substring(0, childTreatmentType.length() - 1));
			bookmarkDtoTreatmntTypeList.add(bookmarkDtoTreatmntType);
			formDataGrpTreatmntType.setBookmarkDtoList(bookmarkDtoTreatmntTypeList);
			formDataGroupDtoList.add(formDataGrpTreatmntType);

		}

		// PRIOR ADOPTION/GUARDIANSHIP INFORMATION
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildAdoptdDmstc())) {
			if (FormConstants.ONE.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildAdoptdDmstc())) {
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setCdChildAdoptdDmstc(FormConstants.YES);
			} else if (FormConstants.TWO
					.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildAdoptdDmstc())) {
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setCdChildAdoptdDmstc(FormConstants.NO);
			} else {
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto()
						.setCdChildAdoptdDmstc(FormConstants.UNABLETODETERMINE);
			}
		}
		BookmarkDto bookmarkPriorAdoptDomestic = createBookmark(BookmarkConstants.PRIORADOPT_DOMESTIC,
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildAdoptdDmstc());
		bookmarkDtoList.add(bookmarkPriorAdoptDomestic);

		List<FormDataGroupDto> formDataGrpTmpPriorAdoptDomesticList = new ArrayList<FormDataGroupDto>();

		if (!TypeConvUtil.isNullOrEmpty(childPlanOfServiceDto.getChildPlanPriorAdpInfoDomesticDtoList())) {

			for (ChildPlanPriorAdpInfoDto childPlanPriorDto : childPlanOfServiceDto
					.getChildPlanPriorAdpInfoDomesticDtoList()) {

				FormDataGroupDto formDataGrpTmpPriorAdoptDomestic = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PRIORADOPT_DOMESTICALLY, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkDtoTmpPriorAdoptDomesticList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtoActEstDtDomestic = createBookmark(BookmarkConstants.ACTUAL_EST_DATE_DOMESTIC,
						DateUtils.stringDt(childPlanPriorDto.getDtActlEstCnsmted()));
				bookmarkDtoTmpPriorAdoptDomesticList.add(bookmarkDtoActEstDtDomestic);

				BookmarkDto bookmarkDtoCdAgencyType = createBookmarkWithCodesTable(
						BookmarkConstants.TYPE_AGENCY_DOMESTIC, childPlanPriorDto.getCdAgncyType(),
						CodesConstant.CAGNADOP);
				bookmarkDtoTmpPriorAdoptDomesticList.add(bookmarkDtoCdAgencyType);

				BookmarkDto bookmarkDtoNmAgncy = createBookmark(BookmarkConstants.AGENCY_NAME_DOMESTIC,
						childPlanPriorDto.getNmAgncy());
				bookmarkDtoTmpPriorAdoptDomesticList.add(bookmarkDtoNmAgncy);

				BookmarkDto bookmarkDtoCdStateChildAdoptd = createBookmarkWithCodesTable(
						BookmarkConstants.STATE_DOMESTIC, childPlanPriorDto.getCdStateChildAdoptd(),
						CodesConstant.CSTATE);
				bookmarkDtoTmpPriorAdoptDomesticList.add(bookmarkDtoCdStateChildAdoptd);
				formDataGrpTmpPriorAdoptDomestic.setBookmarkDtoList(bookmarkDtoTmpPriorAdoptDomesticList);
				formDataGrpTmpPriorAdoptDomesticList.add(formDataGrpTmpPriorAdoptDomestic);
			}
		}

		if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildAdoptdDmstc())) {
			formDataGroupDtoList.addAll(formDataGrpTmpPriorAdoptDomesticList);
		}

		// Prior Adoption Internationally
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildAdoptdIntnl())) {
			if (FormConstants.ONE.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildAdoptdIntnl())) {
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setCdChildAdoptdIntnl(FormConstants.YES);
			} else if (FormConstants.TWO
					.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildAdoptdIntnl())) {
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setCdChildAdoptdIntnl(FormConstants.NO);
			} else {
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto()
						.setCdChildAdoptdIntnl(FormConstants.UNABLETODETERMINE);
			}
		}
		BookmarkDto bookmarkPriorAdoptIntnl = createBookmark(BookmarkConstants.PRIORADOPT_INTNL,
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildAdoptdIntnl());
		bookmarkDtoList.add(bookmarkPriorAdoptIntnl);

		List<FormDataGroupDto> formDataGrpTmpPriorAdoptIntrntnlList = new ArrayList<FormDataGroupDto>();

		if (!TypeConvUtil.isNullOrEmpty(childPlanOfServiceDto.getChildPlanPriorAdpInfoInterntlDtoList())) {

			for (ChildPlanPriorAdpInfoDto childPlanPriorDto : childPlanOfServiceDto
					.getChildPlanPriorAdpInfoInterntlDtoList()) {

				FormDataGroupDto formDataGrpTmpPriorAdoptIntrntnl = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PRIORADOPT_INTERNATIONALLY, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkDtoTmpPriorAdoptIntrntnlList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtoActEstDtDomestic = createBookmark(
						BookmarkConstants.ACTUAL_EST_DATE_INTERNATIONAL,
						DateUtils.stringDt(childPlanPriorDto.getDtActlEstCnsmted()));
				bookmarkDtoTmpPriorAdoptIntrntnlList.add(bookmarkDtoActEstDtDomestic);

				BookmarkDto bookmarkDtoCdAgencyType = createBookmarkWithCodesTable(
						BookmarkConstants.TYPE_AGENCY_INTERNATIONAL, childPlanPriorDto.getCdAgncyType(),
						CodesConstant.CAGNADOP);
				bookmarkDtoTmpPriorAdoptIntrntnlList.add(bookmarkDtoCdAgencyType);

				BookmarkDto bookmarkDtoNmAgncy = createBookmark(BookmarkConstants.AGENCY_NAME_INTERNATIONAL,
						childPlanPriorDto.getNmAgncy());
				bookmarkDtoTmpPriorAdoptIntrntnlList.add(bookmarkDtoNmAgncy);

				BookmarkDto bookmarkDtoCdStateChildAdoptd = createBookmarkWithCodesTable(
						BookmarkConstants.COUNTRY_INTERNATIONAL, childPlanPriorDto.getCdCntryChildAdoptd(),
						CodesConstant.CCOUNTRY);
				bookmarkDtoTmpPriorAdoptIntrntnlList.add(bookmarkDtoCdStateChildAdoptd);
				formDataGrpTmpPriorAdoptIntrntnl.setBookmarkDtoList(bookmarkDtoTmpPriorAdoptIntrntnlList);
				formDataGrpTmpPriorAdoptIntrntnlList.add(formDataGrpTmpPriorAdoptIntrntnl);

			}
		}

		if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildAdoptdIntnl())) {
			formDataGroupDtoList.addAll(formDataGrpTmpPriorAdoptIntrntnlList);
		}

		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildLglGrdnship())) {
			if (FormConstants.ONE.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildLglGrdnship())) {
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setCdChildLglGrdnship(FormConstants.YES);
			} else if (FormConstants.TWO
					.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildLglGrdnship())) {
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().setCdChildLglGrdnship(FormConstants.NO);
			} else {
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto()
						.setCdChildLglGrdnship(FormConstants.UNABLETODETERMINE);
			}
		}
		BookmarkDto bookmarkPriorLeglGurdnshp = createBookmark(BookmarkConstants.PREV_LEGALGUARDSHIP,
				childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildLglGrdnship());
		bookmarkDtoList.add(bookmarkPriorLeglGurdnshp);

		List<FormDataGroupDto> formDataGrpTmpPriorAdoptLeglGuardshpList = new ArrayList<FormDataGroupDto>();

		if (!TypeConvUtil.isNullOrEmpty(childPlanOfServiceDto.getChildPlanLegalGrdnshpDtoList())) {
			for (ChildPlanLegalGrdnshpDto childPlnLglGrdnshpDto : childPlanOfServiceDto
					.getChildPlanLegalGrdnshpDtoList()) {

				FormDataGroupDto formDataGrpTmpPriorAdoptLeglGuardshp = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PRIORADOPT_PREV_LEGALGUARDIANSHIP, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkDtoTmpPriorLeglGuardshpList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtoActEstLglGrdshp = createBookmark(
						BookmarkConstants.ACTUAL_EST_DATE_PREV_LEGALGUARDSHIP,
						DateUtils.stringDt(childPlnLglGrdnshpDto.getDtActlEstLglGrdnship()));
				bookmarkDtoTmpPriorLeglGuardshpList.add(bookmarkDtoActEstLglGrdshp);

				BookmarkDto bookmarkDtoNmLglGrdShp = createBookmark(BookmarkConstants.WITH_WHOM_PREV_LEGALGUARDSHIP,
						childPlnLglGrdnshpDto.getNmPrvLglGrdnship());
				bookmarkDtoTmpPriorLeglGuardshpList.add(bookmarkDtoNmLglGrdShp);

				formDataGrpTmpPriorAdoptLeglGuardshp.setBookmarkDtoList(bookmarkDtoTmpPriorLeglGuardshpList);
				formDataGrpTmpPriorAdoptLeglGuardshpList.add(formDataGrpTmpPriorAdoptLeglGuardshp);
			}
		}
		if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildLglGrdnship())) {

			formDataGroupDtoList.addAll(formDataGrpTmpPriorAdoptLeglGuardshpList);
		}
		// CHILD HISTORY
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto())) {
			BookmarkDto bookmarkDtoChildHistory = createBookmark(BookmarkConstants.RSN_CHILD_CAME,
					formatTextValue(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getTxtChildHist()));
			bookmarkDtoList.add(bookmarkDtoChildHistory);

			// INITIAL FAMILY/GENETIC HISTORY
			BookmarkDto bookmarkDtoFamGeneticHistory = createBookmark(BookmarkConstants.CHILD_FAMILY_GENETICHISTORY,
					formatTextValue(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getTxtInitFmlyHist()));
			bookmarkDtoList.add(bookmarkDtoFamGeneticHistory);

			// CHILD'S STRENGTHS,INTERESTS AND PERSONALITY
			BookmarkDto bookmarkDtoStrInterest = createBookmark(BookmarkConstants.STRENGTH_INTEREST,
					formatTextValue(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getTxtChildIntrstPrsnlty()));
			bookmarkDtoList.add(bookmarkDtoStrInterest);
		}

		// PLAN FOR VISITATION AND CONTACTS WITH FAMILY
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto())) {
			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getIndChildSib())) {
				childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().setIndChildSib(FormConstants.YES);
			} else if (FormConstants.N
					.equals(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getIndChildSib())) {
				childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().setIndChildSib(FormConstants.NO);
			}
			BookmarkDto bookmarkDtoChildSiblngs = createBookmark(BookmarkConstants.CHILD_SIBLINGS,
					childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getIndChildSib());

			bookmarkDtoList.add(bookmarkDtoChildSiblngs);

			BookmarkDto bookmarkDtoFamRel = createBookmark(BookmarkConstants.FAMILY_REL,
					formatTextValue(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getTxtLastFmlyReltnshp()));
			bookmarkDtoList.add(bookmarkDtoFamRel);

			BookmarkDto bookmarkDtoSummVisitCntct = createBookmark(BookmarkConstants.VISITATION_NOTALLOWED,
					formatTextValue(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getTxtVisitCntctExpln()));
			bookmarkDtoList.add(bookmarkDtoSummVisitCntct);

			BookmarkDto bookmarkDtoTypCntctApproved = createBookmark(BookmarkConstants.TYPE_CONTACT_APPROVED,
					formatTextValue(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getTxtTypCntctApprvd()));
			bookmarkDtoList.add(bookmarkDtoTypCntctApproved);

			BookmarkDto bookmarkDtoVisitSchedl = createBookmark(BookmarkConstants.VISITATION_SCHEDULE,
					formatTextValue(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getTxtVisitSchedl()));
			bookmarkDtoList.add(bookmarkDtoVisitSchedl);

			BookmarkDto bookmarkDtoEfrtMntnFamily = createBookmark(BookmarkConstants.EFFORTS_FAMILY_CONNECTIONS,
					formatTextValue(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getTxtEfrtMntnCnctnFmly()));
			bookmarkDtoList.add(bookmarkDtoEfrtMntnFamily);

			BookmarkDto bookmarkDtoSummVisit = createBookmark(BookmarkConstants.SUMMARY_VISITATION,
					formatTextValue(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getTxtSummVisitCntct()));
			bookmarkDtoList.add(bookmarkDtoSummVisit);

			BookmarkDto bookmarkDtoIdentifiedGoalVisit = createBookmark(BookmarkConstants.IDENTIFIED_GOALS_VISIT,
					formatTextValue(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getTxtIdentfdGoals()));
			bookmarkDtoList.add(bookmarkDtoIdentifiedGoalVisit);

			BookmarkDto bookmarkDtoIdentifiedPlans = createBookmark(BookmarkConstants.IDENTIFIED_NEEDS_ADDRESS_VISIT,
					formatTextValue(childPlanOfServiceDto.getChildPlanVisitationCnctFmlyDto().getTxtIdentfdPlans()));
			bookmarkDtoList.add(bookmarkDtoIdentifiedPlans);
		}

		// CHILD'S BASIC NEEDS
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto())) {
			BookmarkDto bookmarkDtoChildBasicNeeds = createBookmark(BookmarkConstants.CHILD_BASICNEEDS,
					formatTextValue(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getTxtChildBasicNeeds()));
			bookmarkDtoList.add(bookmarkDtoChildBasicNeeds);

			// JUVENILE JUSTICE INVOLVEMENT
			// [artf132297] Defect 13543 - adding additional else/if check for 'N' so that no response / null values remain null and don't default to 'No'
			if (FormConstants.Y
					.equals(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getIndJuvnleJustcInvlvmnt())) {
				childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().setIndJuvnleJustcInvlvmnt(FormConstants.YES);
			} else if (FormConstants.N
					.equals(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getIndJuvnleJustcInvlvmnt())) {
				childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().setIndJuvnleJustcInvlvmnt(FormConstants.NO);
			}
			BookmarkDto bookmarkDtoindJuvnleJustc = createBookmark(BookmarkConstants.JUVENILE_HISTORY_INVOLVEMENT,
					childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getIndJuvnleJustcInvlvmnt());
			bookmarkDtoList.add(bookmarkDtoindJuvnleJustc);

			if (FormConstants.YES
					.equals(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getIndJuvnleJustcInvlvmnt())) {
				FormDataGroupDto formDataGrpIndJuvnleHist = createFormDataGroup(
						FormGroupsConstants.TMPLAT_JUVENILE_HISTORY, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkIndJuvnleHistList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoindJuvnleHist = createBookmark(BookmarkConstants.JUVENILE_HISTORY,
						formatTextValue(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getTxtExplnJuvnleJustc()));
				bookmarkIndJuvnleHistList.add(bookmarkDtoindJuvnleHist);

				formDataGrpIndJuvnleHist.setBookmarkDtoList(bookmarkIndJuvnleHistList);
				formDataGroupDtoList.add(formDataGrpIndJuvnleHist);
			}

		}

		// YOUTH WHO ARE PREGNANT OR PARENTING
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanYouthParentingDto())) {
			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndChildCurrPergnt())) {
				childPlanOfServiceDto.getChildPlanYouthParentingDto().setIndChildCurrPergnt(FormConstants.YES);
			} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndChildCurrPergnt())) {
				childPlanOfServiceDto.getChildPlanYouthParentingDto().setIndChildCurrPergnt(FormConstants.NO);
			}

			if (childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndChildCurrPergnt()
					.equals(FormConstants.YES)) {
				FormDataGroupDto formDataGrpIsYouthPregnt = createFormDataGroup(
						FormGroupsConstants.TMPLAT_YOUTH_PREGNANT, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkIsYouthPregntList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtoIsYouthPregnt = createBookmark(BookmarkConstants.YOUTH_PREGNANT,
						childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndChildCurrPergnt());
				bookmarkIsYouthPregntList.add(bookmarkDtoIsYouthPregnt);
				formDataGrpIsYouthPregnt.setBookmarkDtoList(bookmarkIsYouthPregntList);
				formDataGroupDtoList.add(formDataGrpIsYouthPregnt);
			}

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndYouthWithChild())) {
				childPlanOfServiceDto.getChildPlanYouthParentingDto().setIndYouthWithChild(FormConstants.YES);
			} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndYouthWithChild())) {
				childPlanOfServiceDto.getChildPlanYouthParentingDto().setIndYouthWithChild(FormConstants.NO);
			}

			BookmarkDto bookmarkDtoHasYouthChild = createBookmark(BookmarkConstants.YOUTH_CHILD,
					childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndYouthWithChild());
			bookmarkDtoList.add(bookmarkDtoHasYouthChild);

			if (FormConstants.YES
					.equals(childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndYouthWithChild())) {
				FormDataGroupDto formDataGrpChildReside = createFormDataGroup(
						FormGroupsConstants.TMPLAT_YOUTH_CHILD_RESIDE, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkChildResideList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoChildReside = createBookmark(BookmarkConstants.YOUTH_CHILD_RESIDE,
						formatTextValue(childPlanOfServiceDto.getChildPlanYouthParentingDto().getTxtYouthReside()));
				bookmarkChildResideList.add(bookmarkDtoChildReside);

				formDataGrpChildReside.setBookmarkDtoList(bookmarkChildResideList);
				formDataGroupDtoList.add(formDataGrpChildReside);

				FormDataGroupDto formDataGrpYouthParenting = createFormDataGroup(
						FormGroupsConstants.TMPLAT_YOUTH_ROLE_PARENTING, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkYouthParentingList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoYouthParentngRole = createBookmark(BookmarkConstants.YOUTH_ROLE_PARENTING,
						formatTextValue(childPlanOfServiceDto.getChildPlanYouthParentingDto().getTxtYouthPrntgRole()));
				bookmarkYouthParentingList.add(bookmarkDtoYouthParentngRole);

				formDataGrpYouthParenting.setBookmarkDtoList(bookmarkYouthParentingList);
				formDataGroupDtoList.add(formDataGrpYouthParenting);

			}

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndChildInDfpsCvs())) {
				childPlanOfServiceDto.getChildPlanYouthParentingDto().setIndChildInDfpsCvs(FormConstants.YES);
			} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndChildInDfpsCvs())) {
				childPlanOfServiceDto.getChildPlanYouthParentingDto().setIndChildInDfpsCvs(FormConstants.NO);
			}

			BookmarkDto bookmarkDtoDFPSConservtrsp = createBookmark(BookmarkConstants.YOUTH_CHILD_DFPS_CONSERVATORSHIP,
					childPlanOfServiceDto.getChildPlanYouthParentingDto().getIndChildInDfpsCvs());
			bookmarkDtoList.add(bookmarkDtoDFPSConservtrsp);

		}

		// SERVICES TO ADDRESS HIGH RISK BEHAVIOUR
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanHighRiskServicesDto())) {
			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSelfHarm())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndSelfHarm(FormConstants.YES);
			} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSelfHarm())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndSelfHarm(FormConstants.NO);
			}
			BookmarkDto bookmarkDtoSelfHarm = createBookmark(BookmarkConstants.CHILD_RISK_SELFHARM,
					childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSelfHarm());
			bookmarkDtoList.add(bookmarkDtoSelfHarm);

			if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSelfHarm())) {
				FormDataGroupDto formDataGrpSelfHarm = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CHILD_RISK_SELFHARM, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSelfHarmList = new ArrayList<BookmarkDto>();
				// Warranty Defect Fix - 12346 - Incorrect Book mark Naming Convention
				BookmarkDto bookmarkDtoTxtSelfHarm = createBookmark(BookmarkConstants.RISK_SELFHARM_BEHAVIOR_ADDRESS,
						formatTextValue(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getTxtSelfHarm()));
				bookmarkSelfHarmList.add(bookmarkDtoTxtSelfHarm);

				formDataGrpSelfHarm.setBookmarkDtoList(bookmarkSelfHarmList);
				formDataGroupDtoList.add(formDataGrpSelfHarm);
			}

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSuicdeBhvr())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndSuicdeBhvr(FormConstants.YES);
			} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSuicdeBhvr())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndSuicdeBhvr(FormConstants.NO);
			}

			BookmarkDto bookmarkDtoindSuicideBehaviour = createBookmark(BookmarkConstants.CHILD_SUICIDAL_BEHAVIOR,
					childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSuicdeBhvr());
			bookmarkDtoList.add(bookmarkDtoindSuicideBehaviour);

			if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSuicdeBhvr())) {
				FormDataGroupDto formDataGrpSuicdeBhvr = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CHILD_SUICIDAL_BEHAVIOR, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSuicdeBhvrList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoTxtSuicideBehaviour = createBookmark(BookmarkConstants.SUICIDAL_BEHAVIOR_ADDRESS,
						formatTextValue(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getTxtSuicdeBhvr()));
				bookmarkSuicdeBhvrList.add(bookmarkDtoTxtSuicideBehaviour);

				formDataGrpSuicdeBhvr.setBookmarkDtoList(bookmarkSuicdeBhvrList);
				formDataGroupDtoList.add(formDataGrpSuicdeBhvr);
			}

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSxBhvrIdentfd())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndSxBhvrIdentfd(FormConstants.YES);
			} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSxBhvrIdentfd())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndSxBhvrIdentfd(FormConstants.NO);
			}
			BookmarkDto bookmarkDtoIndSxBhvrIdntfd = createBookmark(BookmarkConstants.SEXUAL_BEHAVIOR_PROBLEM,
					childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSxBhvrIdentfd());
			bookmarkDtoList.add(bookmarkDtoIndSxBhvrIdntfd);

			if (FormConstants.YES
					.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSxBhvrIdentfd())) {
				FormDataGroupDto formDataGrpSxBhvrIdentfd = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SEXUAL_BEHAVIOR_PROBLEM, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSxBhvrIdentfdList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoTxtSxBhvrIdntfd = createBookmark(BookmarkConstants.SEXUAL_BEHAVIOR_ADDRESS,
						formatTextValue(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getTxtSxBhvrIdentfd()));
				bookmarkSxBhvrIdentfdList.add(bookmarkDtoTxtSxBhvrIdntfd);

				formDataGrpSxBhvrIdentfd.setBookmarkDtoList(bookmarkSxBhvrIdentfdList);
				formDataGroupDtoList.add(formDataGrpSxBhvrIdentfd);
			}

			if (FormConstants.Y
					.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSxAggrsvIdentfd())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndSxAggrsvIdentfd(FormConstants.YES);
			} else if (FormConstants.N
					.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSxAggrsvIdentfd())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndSxAggrsvIdentfd(FormConstants.NO);
			}

			BookmarkDto bookmarkDtoIndSxAggresv = createBookmark(BookmarkConstants.SEXUAL_AGGRESSIVE,
					childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSxAggrsvIdentfd());
			bookmarkDtoList.add(bookmarkDtoIndSxAggresv);

			if (FormConstants.YES.equalsIgnoreCase(
					childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndSxAggrsvIdentfd())) {

				FormDataGroupDto formDataGrpPlanEnsureSfty = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SEXUAL_AGGRESSIVE, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPlanEnsureSftyList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoChildSafetyPln = createBookmark(BookmarkConstants.CHILD_SAFETY_PLAN,
						formatTextValue(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getTxtPlanEnsureSfty()));
				bookmarkPlanEnsureSftyList.add(bookmarkDtoChildSafetyPln);
				BookmarkDto bookmarkDtoTxtSxAggresv = createBookmark(BookmarkConstants.ENSURE_CHILD_SAFETY,
						formatTextValue(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getTxtSxAggrsvIdentfd()));
				bookmarkPlanEnsureSftyList.add(bookmarkDtoTxtSxAggresv);

				formDataGrpPlanEnsureSfty.setBookmarkDtoList(bookmarkPlanEnsureSftyList);
				formDataGroupDtoList.add(formDataGrpPlanEnsureSfty);

			}

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskHarmOthr())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndRiskHarmOthr(FormConstants.YES);
			} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskHarmOthr())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndRiskHarmOthr(FormConstants.NO);
			}
			BookmarkDto bookmarkDtoIndRiskHarmOthers = createBookmark(BookmarkConstants.RISK_HARM_OTHERS,
					childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskHarmOthr());
			bookmarkDtoList.add(bookmarkDtoIndRiskHarmOthers);

			if (FormConstants.YES
					.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskHarmOthr())) {
				FormDataGroupDto formDataGrpRiskHarmOthr = createFormDataGroup(
						FormGroupsConstants.TMPLAT_RISK_HARM_OTHERS, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRiskHarmOthrList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoTextRiskHarmOthers = createBookmark(BookmarkConstants.RISK_PLANS_ADDRESS,
						formatTextValue(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getTxtRiskOthr()));
				bookmarkRiskHarmOthrList.add(bookmarkDtoTextRiskHarmOthers);

				formDataGrpRiskHarmOthr.setBookmarkDtoList(bookmarkRiskHarmOthrList);
				formDataGroupDtoList.add(formDataGrpRiskHarmOthr);
			}

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskWandrng())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndRiskWandrng(FormConstants.YES);
			} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskWandrng())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndRiskWandrng(FormConstants.NO);
			}
			BookmarkDto bookmarkDtoIndRiskWandrng = createBookmark(BookmarkConstants.RISK_NIGHT_LEAVING,
					childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskWandrng());
			bookmarkDtoList.add(bookmarkDtoIndRiskWandrng);

			if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskWandrng())) {
				FormDataGroupDto formDataGrpRiskWandrng = createFormDataGroup(
						FormGroupsConstants.TMPLAT_RISK_NIGHT_LEAVING, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRiskWandrngList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoTxtRiskWandering = createBookmark(BookmarkConstants.RISK_BEHAVIOR_ADDRESS,
						formatTextValue(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getTxtRiskWandrng()));
				bookmarkRiskWandrngList.add(bookmarkDtoTxtRiskWandering);

				formDataGrpRiskWandrng.setBookmarkDtoList(bookmarkRiskWandrngList);
				formDataGroupDtoList.add(formDataGrpRiskWandrng);
			}

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskRunaway())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndRiskRunaway(FormConstants.YES);
			} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskRunaway())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndRiskRunaway(FormConstants.NO);
			}
			BookmarkDto bookmarkDtoIndRiskRunAway = createBookmark(BookmarkConstants.RISK_RUNAWAY,
					childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskRunaway());
			bookmarkDtoList.add(bookmarkDtoIndRiskRunAway);

			if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndRiskRunaway())) {
				FormDataGroupDto formDataGrpRiskRunaway = createFormDataGroup(FormGroupsConstants.TMPLAT_RISK_RUNAWAY,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRiskRunawayList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoTxtRiskRunAway = createBookmark(BookmarkConstants.RUNAWAY_PLANS_ADDRESS,
						formatTextValue(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getTxtRiskRunaway()));
				bookmarkRiskRunawayList.add(bookmarkDtoTxtRiskRunAway);

				formDataGrpRiskRunaway.setBookmarkDtoList(bookmarkRiskRunawayList);
				formDataGroupDtoList.add(formDataGrpRiskRunaway);
			}

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndOthr())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndOthr(FormConstants.YES);
			} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndOthr())) {
				childPlanOfServiceDto.getChildPlanHighRiskServicesDto().setIndOthr(FormConstants.NO);
			}
			BookmarkDto bookmarkDtoIndOthr = createBookmark(BookmarkConstants.OTHER_RISK,
					childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndOthr());
			bookmarkDtoList.add(bookmarkDtoIndOthr);

			if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getIndOthr())) {
				FormDataGroupDto formDataGrpOthr = createFormDataGroup(FormGroupsConstants.TMPLAT_OTHER_BEHAVIOR,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkOthrList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoTxtOthr = createBookmark(BookmarkConstants.OTHER_BEHAVIOR_ADDRESS,
						formatTextValue(childPlanOfServiceDto.getChildPlanHighRiskServicesDto().getTxtOthr()));
				bookmarkOthrList.add(bookmarkDtoTxtOthr);
				formDataGrpOthr.setBookmarkDtoList(bookmarkOthrList);
				formDataGroupDtoList.add(formDataGrpOthr);
			}

		}

		// SUPPORT SERVICES TO CAREGIVER
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto())) {
			BookmarkDto bookmarkDtoCareGivrServices = createBookmark(BookmarkConstants.CAREGIVER_SERVICES,
					formatTextValue(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getTxtSvcsCrgvr()));
			bookmarkDtoList.add(bookmarkDtoCareGivrServices);

			BookmarkDto bookmarkDtoRespiteCr = createBookmark(BookmarkConstants.RESPITE_CARE,
					formatTextValue(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getTxtResptCare()));
			bookmarkDtoList.add(bookmarkDtoRespiteCr);

			// TRAVEL
			BookmarkDto bookmarkDtoTravel = createBookmark(BookmarkConstants.TRAVEL_ADDTNL_COMNTS,
					formatTextValue(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getTxtTrvlAdtnlCmnts()));
			bookmarkDtoList.add(bookmarkDtoTravel);

			// PLANS FOR DISCHARGE
			BookmarkDto bookmarkDtoDischargePlans = createBookmark(BookmarkConstants.TRAVEL_PLANED_DISCHARGES,
					formatTextValue(childPlanOfServiceDto.getChildPlanAdtnlSctnDtlDto().getTxtPlanForDschrg()));
			bookmarkDtoList.add(bookmarkDtoDischargePlans);
		}

		// INTELLECTUAL AND DEVELOPMENTAL
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanIntellectualDevelopDto())) {
			BookmarkDto bookmarkDtoIntlectualFunc = createBookmark(BookmarkConstants.INTELLECTUAL_FUNCTION,
					formatTextValue(childPlanOfServiceDto.getChildPlanIntellectualDevelopDto().getTxtIntlctlFunctn()));
			bookmarkDtoList.add(bookmarkDtoIntlectualFunc);

			BookmarkDto bookmarkDtoDevlpmtStrengths = createBookmark(BookmarkConstants.DEVELOPMENTAL_STRENGTHS,
					formatTextValue(childPlanOfServiceDto.getChildPlanIntellectualDevelopDto().getTxtDvlpmntlStrngthNeeds()));
			bookmarkDtoList.add(bookmarkDtoDevlpmtStrengths);

			BookmarkDto bookmarkDtoTxtStrtgy = createBookmark(BookmarkConstants.STRATEGIES_ADDRESS,
					formatTextValue(childPlanOfServiceDto.getChildPlanIntellectualDevelopDto().getTxtStrtgy()));
			bookmarkDtoList.add(bookmarkDtoTxtStrtgy);

			if (ServiceConstants.YES
					.equals(childPlanOfServiceDto.getChildPlanIntellectualDevelopDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotApplIntellectual = createBookmark(BookmarkConstants.NA_INTELLECTUAL_GOAL,
						ServiceConstants.NA_STRING);
				bookmarkDtoList.add(bookmarkDtoNotApplIntellectual);
			} else if (!TypeConvUtil.isNullOrEmpty(
					childPlanOfServiceDto.getChildPlanIntellectualDevelopDto().getChildPlanGoalDtoList())) {
				List<FormDataGroupDto> formDataGroupIntlctualGoalsList = new ArrayList<FormDataGroupDto>();

				for (ChildPlanGoalDto childPlanGoal : childPlanOfServiceDto.getChildPlanIntellectualDevelopDto()
						.getChildPlanGoalDtoList()) {
					FormDataGroupDto formDataGroupIntlctualGoals = createFormDataGroup(
							FormGroupsConstants.TMPLAT_INTELLECTUAL_GOALS, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkIntlctualGoalList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoIntlectualGoal = createBookmark(BookmarkConstants.INTELLECTUAL_GOALS,
							formatTextValue(childPlanGoal.getTxtGoals()));
					bookmarkIntlctualGoalList.add(bookmarkDtoIntlectualGoal);

					BookmarkDto bookmarkDtoIntlectualDtTargt = createBookmark(
							BookmarkConstants.INTELLECTUAL_TARGET_DATE, DateUtils.stringDt(childPlanGoal.getDtTrgt()));
					bookmarkIntlctualGoalList.add(bookmarkDtoIntlectualDtTargt);

					BookmarkDto bookmarkDtoIntlectualCriteria = createBookmark(BookmarkConstants.INTELLECTUAL_CRITERIA,
							formatTextValue(childPlanGoal.getTxtCritraAchevd()));
					bookmarkIntlctualGoalList.add(bookmarkDtoIntlectualCriteria);

					BookmarkDto bookmarkDtoIntlectualIntervntn = createBookmark(
							BookmarkConstants.INTELLECTUAL_INTERVENTIONS, formatTextValue(childPlanGoal.getTxtPlndIntrvntns()));
					bookmarkIntlctualGoalList.add(bookmarkDtoIntlectualIntervntn);

					BookmarkDto bookmarkDtoIntlectualFreq = createBookmark(BookmarkConstants.INTELLECTUAL_FREQUENCY,
							formatTextValue(childPlanGoal.getTxtFreq()));
					bookmarkIntlctualGoalList.add(bookmarkDtoIntlectualFreq);

					BookmarkDto bookmarkDtoIntlectualRespnsblePrty = createBookmark(
							BookmarkConstants.INTELLECTUAL_RESPONSIBLE, formatTextValue(childPlanGoal.getTxtRspnsParty()));
					bookmarkIntlctualGoalList.add(bookmarkDtoIntlectualRespnsblePrty);

					if(!childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType().equals(ServiceConstants.CHILD_PLAN_INITIAL))
					{
					List<FormDataGroupDto> formDataGroupTmplatIntlectualProgSummryList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupTmplatIntlectualProgSummry  = createFormDataGroup(
								FormGroupsConstants.TMPLAT_INTELLECTUAL_PROGRESS, FormGroupsConstants.TMPLAT_INTELLECTUAL_GOALS);
					List<BookmarkDto> bookmarkIntlectualProgSummrylist = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoIntlectualProgSummry = createBookmark(
							BookmarkConstants.INTELLECTUAL_PROGRESS, childPlanGoal.getTxtProgrssSumm());
					bookmarkIntlectualProgSummrylist.add(bookmarkDtoIntlectualProgSummry);
					formDataGroupTmplatIntlectualProgSummry.setBookmarkDtoList(bookmarkIntlectualProgSummrylist);
					formDataGroupTmplatIntlectualProgSummryList.add(formDataGroupTmplatIntlectualProgSummry);
					formDataGroupIntlctualGoals.setFormDataGroupList(formDataGroupTmplatIntlectualProgSummryList);					
					}

					formDataGroupIntlctualGoals.setBookmarkDtoList(bookmarkIntlctualGoalList);
					formDataGroupIntlctualGoalsList.add(formDataGroupIntlctualGoals);
				}

				formDataGroupDtoList.addAll(formDataGroupIntlctualGoalsList);
			}
		}
		int age = DateUtils.getAge(childPlanOfServiceDto.getChildPlanInformationDto().getDtChildBirth());

		// EDUCATION
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanEducationDto())) {
			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndChildEnrlldSch())) {
				childPlanOfServiceDto.getChildPlanEducationDto().setIndChildEnrlldSch(FormConstants.YES);
			} else {
				childPlanOfServiceDto.getChildPlanEducationDto().setIndChildEnrlldSch(FormConstants.NO);
			}

			BookmarkDto bookmarkDtoIndChildEnrlldSch = createBookmark(BookmarkConstants.ENROLLED_SCHOOL,
					childPlanOfServiceDto.getChildPlanEducationDto().getIndChildEnrlldSch());
			bookmarkDtoList.add(bookmarkDtoIndChildEnrlldSch);

			if (FormConstants.NO.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndChildEnrlldSch())) {
				FormDataGroupDto formDataGrpIndExplnEnrlldSch = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ENROLLED_SCHOOL_EXPLAIN, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkIndExplnEnrlldSchList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkIndExplnEnrlldSch = createBookmark(BookmarkConstants.ENROLLED_SCHOOL_EXPLAIN,
						formatTextValue(childPlanOfServiceDto.getChildPlanEducationDto().getTxtExplnEnrlldSch()));
				bookmarkIndExplnEnrlldSchList.add(bookmarkIndExplnEnrlldSch);
				formDataGrpIndExplnEnrlldSch.setBookmarkDtoList(bookmarkIndExplnEnrlldSchList);
				formDataGroupDtoList.add(formDataGrpIndExplnEnrlldSch);

			}

			if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndChildEnrlldSch())) {
				FormDataGroupDto formDataGrpSchoolInfo = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ENROLLED_SCHOOL_YES, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSchoolInfoList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoNmSchoolDist = createBookmark(BookmarkConstants.SCHOOL_DISTRICT,
						formatTextValue(childPlanOfServiceDto.getChildPlanEducationDto().getNmSchoolDist()));
				bookmarkSchoolInfoList.add(bookmarkDtoNmSchoolDist);

				BookmarkDto bookmarkDtoNmSchool = createBookmark(BookmarkConstants.SCHOOL_NAME,
						formatTextValue(childPlanOfServiceDto.getChildPlanEducationDto().getNmSchool()));
				bookmarkSchoolInfoList.add(bookmarkDtoNmSchool);

				BookmarkDto bookmarkDtoNmEductnDecsn = createBookmark(BookmarkConstants.EDU_DECISIONMAKER,
						formatTextValue(childPlanOfServiceDto.getChildPlanEducationDto().getNmEductnDecsn()));
				bookmarkSchoolInfoList.add(bookmarkDtoNmEductnDecsn);

				BookmarkDto bookmarkDtoCdGrade = createBookmarkWithCodesTable(BookmarkConstants.CHILD_GRADE,
						childPlanOfServiceDto.getChildPlanEducationDto().getCdGrade(), CodesConstant.CSCHGRAD);
				bookmarkSchoolInfoList.add(bookmarkDtoCdGrade);

				if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndGrdLvl())) {
					childPlanOfServiceDto.getChildPlanEducationDto().setIndGrdLvl(FormConstants.YES);
				} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndGrdLvl())) {
					childPlanOfServiceDto.getChildPlanEducationDto().setIndGrdLvl(FormConstants.NO);
				}
				BookmarkDto bookmarkDtoIndGrdLvl = createBookmark(BookmarkConstants.ON_GRADELEVEL,
						childPlanOfServiceDto.getChildPlanEducationDto().getIndGrdLvl());
				bookmarkSchoolInfoList.add(bookmarkDtoIndGrdLvl);

				if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndTutoringSvcs())) {
					childPlanOfServiceDto.getChildPlanEducationDto().setIndTutoringSvcs(FormConstants.YES);
				} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndTutoringSvcs())) {
					childPlanOfServiceDto.getChildPlanEducationDto().setIndTutoringSvcs(FormConstants.NO);
				}
				BookmarkDto bookmarkDtoNeedTutoring = createBookmark(BookmarkConstants.NEED_TUTORING,
						childPlanOfServiceDto.getChildPlanEducationDto().getIndTutoringSvcs());
				bookmarkSchoolInfoList.add(bookmarkDtoNeedTutoring);

				formDataGrpSchoolInfo.setBookmarkDtoList(bookmarkSchoolInfoList);
				formDataGroupDtoList.add(formDataGrpSchoolInfo);
			}

			if (FormConstants.NO.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndGrdLvl())
					|| FormConstants.YES
							.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndTutoringSvcs())) {
				FormDataGroupDto formDataGrpEduPlnAddress = createFormDataGroup(
						FormGroupsConstants.TMPLAT_EDU_PLAN_ADDRESS, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkEduPlnAddressList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkEduPlnAddress = createBookmark(BookmarkConstants.EDU_PLAN_ADDRESS,
						formatTextValue(childPlanOfServiceDto.getChildPlanEducationDto().getTxtPlanToAddr()));
				bookmarkDtoList.add(bookmarkEduPlnAddress);

				bookmarkEduPlnAddressList.add(bookmarkEduPlnAddress);
				formDataGrpEduPlnAddress.setBookmarkDtoList(bookmarkEduPlnAddressList);
				formDataGroupDtoList.add(formDataGrpEduPlnAddress);

			}

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndChildRcvngSvcs())) {
				childPlanOfServiceDto.getChildPlanEducationDto().setIndChildRcvngSvcs(FormConstants.YES);
			} else {
				childPlanOfServiceDto.getChildPlanEducationDto().setIndChildRcvngSvcs(FormConstants.NO);
			}

			if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndChildEnrlldSch())) {
				FormDataGroupDto formDataGrpSchoolInfo = createFormDataGroup(FormGroupsConstants.TMPLAT_RECEIVE_SPL_EDU,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSchoolInfoList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoReceiveSplEdu = createBookmark(BookmarkConstants.RECEIVE_SPL_EDU,
						childPlanOfServiceDto.getChildPlanEducationDto().getIndChildRcvngSvcs());
				bookmarkSchoolInfoList.add(bookmarkDtoReceiveSplEdu);

				formDataGrpSchoolInfo.setBookmarkDtoList(bookmarkSchoolInfoList);
				formDataGroupDtoList.add(formDataGrpSchoolInfo);

			}

			if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndChildRcvngSvcs())) {
				FormDataGroupDto formDataGrpSchoolInfo = createFormDataGroup(FormGroupsConstants.TMPLAT_IEPGOAL_504PLAN,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSchoolInfoList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoIepGoals = createBookmark(BookmarkConstants.IEPGOAL_504PLAN,
						formatTextValue(childPlanOfServiceDto.getChildPlanEducationDto().getTxtIepGoals()));
				bookmarkSchoolInfoList.add(bookmarkDtoIepGoals);

				formDataGrpSchoolInfo.setBookmarkDtoList(bookmarkSchoolInfoList);
				formDataGroupDtoList.add(formDataGrpSchoolInfo);

			}

			if (age >= 16 && FormConstants.YES
					.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndChildRcvngSvcs())) {
				FormDataGroupDto formDataGrpSchoolInfo = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SPL_EDU_SERVICES_CHILD16, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSchoolInfoList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoTxtSpecEduSvc = createBookmark(BookmarkConstants.SPL_EDU_SERVICES_CHILD16,
						formatTextValue(childPlanOfServiceDto.getChildPlanEducationDto().getTxtSpeclEductnSvcs()));
				bookmarkSchoolInfoList.add(bookmarkDtoTxtSpecEduSvc);

				formDataGrpSchoolInfo.setBookmarkDtoList(bookmarkSchoolInfoList);
				formDataGroupDtoList.add(formDataGrpSchoolInfo);

			}
			if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndChildEnrlldSch())) {
				FormDataGroupDto formDataGrpSchoolInfo = createFormDataGroup(
						FormGroupsConstants.TMPLAT_EDU_STRENGTH_NEED, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSchoolInfoList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoEductnStren = createBookmark(BookmarkConstants.EDU_STRENGTH_NEED,
						formatTextValue(childPlanOfServiceDto.getChildPlanEducationDto().getTxtEductnStrength()));
				bookmarkSchoolInfoList.add(bookmarkDtoEductnStren);

				BookmarkDto bookmarkDtoInternvSuprt = createBookmark(BookmarkConstants.INTERVEN_SUPPORT,
						formatTextValue(childPlanOfServiceDto.getChildPlanEducationDto().getTxtIntrvntnsSprt()));
				bookmarkSchoolInfoList.add(bookmarkDtoInternvSuprt);

				BookmarkDto bookmarkDtoTxtLstExtraActv = createBookmark(BookmarkConstants.CHILD_EXTRACURRICULAR,
						formatTextValue(childPlanOfServiceDto.getChildPlanEducationDto().getTxtLstExtraActv()));
				bookmarkSchoolInfoList.add(bookmarkDtoTxtLstExtraActv);

				BookmarkDto bookmarkDtoEduSummry = createBookmark(BookmarkConstants.EDU_SUMMARY,
						formatTextValue(childPlanOfServiceDto.getChildPlanEducationDto().getTxtEductnSumm()));
				bookmarkSchoolInfoList.add(bookmarkDtoEduSummry);

				formDataGrpSchoolInfo.setBookmarkDtoList(bookmarkSchoolInfoList);
				formDataGroupDtoList.add(formDataGrpSchoolInfo);

			}

			List<FormDataGroupDto> formDataGroupEduGoalsList = new ArrayList<FormDataGroupDto>();

			if (ServiceConstants.YES.equals(childPlanOfServiceDto.getChildPlanEducationDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotApplEducation = createBookmark(BookmarkConstants.NA_EDU_GOAL,
						ServiceConstants.NA_STRING);
				bookmarkDtoList.add(bookmarkDtoNotApplEducation);
			}

			else if (!TypeConvUtil
					.isNullOrEmpty(childPlanOfServiceDto.getChildPlanEducationDto().getChildPlanGoalDtoList())) {

				for (ChildPlanGoalDto childPlanGoalDto : childPlanOfServiceDto.getChildPlanEducationDto()
						.getChildPlanGoalDtoList()) {
					FormDataGroupDto formDataGroupEduGoals = createFormDataGroup(FormGroupsConstants.TMPLAT_EDU_GOALS,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkDtoEduGoalList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoEduGoal = createBookmark(BookmarkConstants.EDU_GOALS,
							formatTextValue(childPlanGoalDto.getTxtGoals()));
					bookmarkDtoEduGoalList.add(bookmarkDtoEduGoal);

					BookmarkDto bookmarkDtoDtTrgt = createBookmark(BookmarkConstants.EDU_TARGET_DATE,
							DateUtils.stringDt(childPlanGoalDto.getDtTrgt()));
					bookmarkDtoEduGoalList.add(bookmarkDtoDtTrgt);

					BookmarkDto bookmarkTxtCritria = createBookmark(BookmarkConstants.EDU_CRITERIA,
							formatTextValue(childPlanGoalDto.getTxtCritraAchevd()));
					bookmarkDtoEduGoalList.add(bookmarkTxtCritria);

					BookmarkDto bookmarkDtoIntrvntions = createBookmark(BookmarkConstants.EDU_INTERVENTIONS,
							formatTextValue(childPlanGoalDto.getTxtPlndIntrvntns()));
					bookmarkDtoEduGoalList.add(bookmarkDtoIntrvntions);

					BookmarkDto bookmarkDtoTxtFreq = createBookmark(BookmarkConstants.EDU_FREQUENCY,
							formatTextValue(childPlanGoalDto.getTxtFreq()));
					bookmarkDtoEduGoalList.add(bookmarkDtoTxtFreq);

					BookmarkDto bookmarkDtoEduRespnsbl = createBookmark(BookmarkConstants.EDU_RESPONSIBLE,
							formatTextValue(childPlanGoalDto.getTxtRspnsParty()));
					bookmarkDtoEduGoalList.add(bookmarkDtoEduRespnsbl);

					if(!childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType().equals(ServiceConstants.CHILD_PLAN_INITIAL))
					{
					List<FormDataGroupDto> formDataGroupTmplatIntlectualProgSummryList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupTmplatIntlectualProgSummry  = createFormDataGroup(
								FormGroupsConstants.TMPLAT_EDU_PROGRESS, FormGroupsConstants.TMPLAT_EDU_GOALS);
					List<BookmarkDto> bookmarkIntlectualProgSummrylist = new ArrayList<BookmarkDto>();	
					BookmarkDto bookmarkDtoEduProgrs = createBookmark(BookmarkConstants.EDU_PROGRESS,
							formatTextValue(childPlanGoalDto.getTxtProgrssSumm()));
					bookmarkIntlectualProgSummrylist.add(bookmarkDtoEduProgrs);
					formDataGroupTmplatIntlectualProgSummry.setBookmarkDtoList(bookmarkIntlectualProgSummrylist);
					formDataGroupTmplatIntlectualProgSummryList.add(formDataGroupTmplatIntlectualProgSummry);
					formDataGroupEduGoals.setFormDataGroupList(formDataGroupTmplatIntlectualProgSummryList);					
					}
					formDataGroupEduGoals.setBookmarkDtoList(bookmarkDtoEduGoalList);
					formDataGroupEduGoalsList.add(formDataGroupEduGoals);
				}
				formDataGroupDtoList.addAll(formDataGroupEduGoalsList);
			}

		}

		// EMOTIONAL/THERAPEUTIC/PSYCHOLOGICAL
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto())) {
			String indCnfrmdSexTrfcng = childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto()
					.getIndChildCnfrmdSxTrfckng();
			if (ObjectUtils.isEmpty(indCnfrmdSexTrfcng)) {
				indCnfrmdSexTrfcng = ServiceConstants.NO_TEXT;
			}
			BookmarkDto bookmarkDtoIndChildCnfrmSxTrafc = createBookmark(BookmarkConstants.CONFIRM_SEXTRAFF,
					indCnfrmdSexTrfcng.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT
							: ServiceConstants.NO_TEXT);
			bookmarkDtoList.add(bookmarkDtoIndChildCnfrmSxTrafc);
			String indChildSuspdSxTrfckng = childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto()
					.getIndChildSuspdSxTrfckng();
			if (ObjectUtils.isEmpty(indChildSuspdSxTrfckng)) {
				indChildSuspdSxTrfckng = ServiceConstants.NO_TEXT;
			}
			BookmarkDto bookmarkDtoIndChildSuspdSxTrafc = createBookmark(BookmarkConstants.SUSPECT_SEXTRAFF,
					indChildSuspdSxTrfckng.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT
							: ServiceConstants.NO_TEXT);
			bookmarkDtoList.add(bookmarkDtoIndChildSuspdSxTrafc);
			String indChildCnfrmdLbrTrfckng = childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto()
					.getIndChildCnfrmdLbrTrfckng();
			if (ObjectUtils.isEmpty(indChildCnfrmdLbrTrfckng)) {
				indChildCnfrmdLbrTrfckng = ServiceConstants.NO_TEXT;
			}
			BookmarkDto bookmarkDtoIndChildCnfrmLbrTrafc = createBookmark(BookmarkConstants.CONFIRM_LABOURTRAFF,
					indChildCnfrmdLbrTrfckng.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT
							: ServiceConstants.NO_TEXT);
			bookmarkDtoList.add(bookmarkDtoIndChildCnfrmLbrTrafc);
			String indChildSuspdLbrTrfckng = childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto()
					.getIndChildSuspdLbrTrfckng();
			if (ObjectUtils.isEmpty(indChildSuspdLbrTrfckng)) {
				indChildSuspdLbrTrfckng = ServiceConstants.NO_TEXT;
			}
			BookmarkDto bookmarkDtoIndChildSuspdLbrTrfckng = createBookmark(BookmarkConstants.SUSPECT_LABOURTRAFF,
					indChildSuspdLbrTrfckng.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT
							: ServiceConstants.NO_TEXT);
			bookmarkDtoList.add(bookmarkDtoIndChildSuspdLbrTrfckng);

			if (FormConstants.Y
					.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildCnfrmdSxTrfckng())
					|| FormConstants.Y
							.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildSuspdSxTrfckng())
					|| FormConstants.Y
							.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildCnfrmdLbrTrfckng())
					|| FormConstants.Y.equals(
							childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildSuspdLbrTrfckng())) {
				FormDataGroupDto formDataGrpServSprtAssist = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SERVICESSUPPORT_ASSIST, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkServSprtAssistList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoServSprtAssist = createBookmark(BookmarkConstants.SERVICESSUPPORT_ASSIST,
						formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getTxtSpcfcSvcsForChild()));
				bookmarkServSprtAssistList.add(bookmarkDtoServSprtAssist);
				formDataGrpServSprtAssist.setBookmarkDtoList(bookmarkServSprtAssistList);
				formDataGroupDtoList.add(formDataGrpServSprtAssist);
			}

		if(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().isIndEventStartFfpsa()){
			List<FormDataGroupDto> formDataGroupEventStartFfpsaList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupEventStartFfpsa = createFormDataGroup(
					FormGroupsConstants.TMPLAT_EVENT_START_FFPSA, FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> formDataGrpOtherEventList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkEventStartFfpsaList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDtoCareCordntnSrvc = createBookmarkWithCodesTable(BookmarkConstants.CARE_COORDINATION_SERVICES,
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildCareCordntnSrvc(),
					CodesConstant.CAFCTRS1);
			bookmarkEventStartFfpsaList.add(bookmarkDtoCareCordntnSrvc);

			BookmarkDto bookmarkDtoHmnTrfckngAdvSrvc = createBookmarkWithCodesTable(BookmarkConstants.HUMAN_TRAFFICKING_ADVOCACY,
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildHmnTrfckngAdvSrvc(),
					CodesConstant.CAFCTRS1);
			bookmarkEventStartFfpsaList.add(bookmarkDtoHmnTrfckngAdvSrvc);

			BookmarkDto bookmarkDtoCnslnHmnTrfckngVct = createBookmarkWithCodesTable(BookmarkConstants.HUMAN_TRAFFICKING_VICTIMIZATION,
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildCnslnHmnTrfckngVct(),
					CodesConstant.CAFCTRS1);
			bookmarkEventStartFfpsaList.add(bookmarkDtoCnslnHmnTrfckngVct);

			BookmarkDto bookmarkDtoSxlExptnIdntfn = createBookmarkWithCodesTable(BookmarkConstants.CHILD_SEXUAL_EXPLOITATION,
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildSxlExptnIdntfn(),
					CodesConstant.CAFCTRS1);
			bookmarkEventStartFfpsaList.add(bookmarkDtoSxlExptnIdntfn);

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildSxlExptnIdntfn())) {
				FormDataGroupDto formDataGrpRecntScore = createFormDataGroup(
						FormGroupsConstants.TMPLAT_RECENT_SCORE, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRecntScoreList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoRecntScore = createBookmark(BookmarkConstants.RECENT_SCORE,
						formatTextValue(mapRecentScore(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getSelRecntScore())));
				bookmarkRecntScoreList.add(bookmarkDtoRecntScore);

				formDataGrpRecntScore.setBookmarkDtoList(bookmarkRecntScoreList);
				formDataGrpOtherEventList.add(formDataGrpRecntScore);
			}

			BookmarkDto bookmarkDtoHmnTrfckngSrvc = createBookmarkWithCodesTable(BookmarkConstants.HUMAN_TRAFFICKING_SERVICES,
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildHmnTrfckngSrvc(),
					CodesConstant.CAFCTRS1);
			bookmarkEventStartFfpsaList.add(bookmarkDtoHmnTrfckngSrvc);

			if (FormConstants.ONE.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getCkbCrisIntSrvc())) {
				BookmarkDto bookmarkDtoCrisisInterventionServices = createBookmark(BookmarkConstants.CRISIS_INTERVENTION_SERVICES,
						ServiceConstants.CRISIS_INTERVENTION_SERVICES);
				bookmarkEventStartFfpsaList.add(bookmarkDtoCrisisInterventionServices);
			}
			if (FormConstants.ONE.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getCkbDrpInCntrSrvc())) {
				BookmarkDto bookmarkDtoDropInCenterServices = createBookmark(BookmarkConstants.DROP_IN_CENTER_SERVICES,
						ServiceConstants.DROP_IN_CENTER_SERVICES);
				bookmarkEventStartFfpsaList.add(bookmarkDtoDropInCenterServices);
			}
			if (FormConstants.ONE.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getCkbEmplSupSrvc())) {
				BookmarkDto bookmarkDtoEmploymentSupportServices = createBookmark(BookmarkConstants.EMPLOYMENT_SUPPORT_SERVICES,
						ServiceConstants.EMPLOYMENT_SUPPORT_SERVICES);
				bookmarkEventStartFfpsaList.add(bookmarkDtoEmploymentSupportServices);
			}
			if (FormConstants.ONE.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getCkbSubAbuSrvc())) {
				BookmarkDto bookmarkDtoSubstanceAbuseServices = createBookmark(BookmarkConstants.SUBSTANCE_ABUSE_SERVICES,
						ServiceConstants.SUBSTANCE_ABUSE_SERVICES);
				bookmarkEventStartFfpsaList.add(bookmarkDtoSubstanceAbuseServices);
			}
			if (FormConstants.ONE.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getCkbSurPeerSupGrp())) {
				BookmarkDto bookmarkDtoSurvivorPeerSupportGroup = createBookmark(BookmarkConstants.SURVIVOR_PEER_SUPPORT_GROUP,
						ServiceConstants.SURVIVOR_PEER_SUPPORT_GROUP);
				bookmarkEventStartFfpsaList.add(bookmarkDtoSurvivorPeerSupportGroup);
			}
			if (FormConstants.ONE.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getCkbOthrSrvc())) {
				BookmarkDto bookmarkDtoOtherServices = createBookmark(BookmarkConstants.OTHER_SERVICES,
						ServiceConstants.OTHER_SERVICES);
				bookmarkEventStartFfpsaList.add(bookmarkDtoOtherServices);
			}
			if (FormConstants.ONE.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getCkbNoOthrSrvc())) {
				BookmarkDto bookmarkDtoNoOtherServicesReceived = createBookmark(BookmarkConstants.NO_OTHER_SERVICES_RECEIVED,
						ServiceConstants.NO_OTHER_SERVICES_RECEIVED);
				bookmarkEventStartFfpsaList.add(bookmarkDtoNoOtherServicesReceived);
			}
			if (FormConstants.ONE.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getCkbOthrSrvc())) {
				FormDataGroupDto formDataGrpOthrSrvcRcvd = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SERVICES_RECEIVED, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkOthrSrvcRcvdList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoOthrSrvcRcvd = createBookmark(BookmarkConstants.CHILD_SERVICES_RECEIVED,
						formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getTxtChildOthrSrvcRcvd()));
				bookmarkOthrSrvcRcvdList.add(bookmarkDtoOthrSrvcRcvd);

				formDataGrpOthrSrvcRcvd.setBookmarkDtoList(bookmarkOthrSrvcRcvdList);
				formDataGrpOtherEventList.add(formDataGrpOthrSrvcRcvd);
			}
			formDataGroupEventStartFfpsa.setFormDataGroupList(formDataGrpOtherEventList);
			formDataGroupEventStartFfpsa.setBookmarkDtoList(bookmarkEventStartFfpsaList);
			formDataGroupEventStartFfpsaList.add(formDataGroupEventStartFfpsa);
			formDataGroupDtoList.addAll(formDataGroupEventStartFfpsaList);
		}

			BookmarkDto bookmarkDtoTraumaHist = createBookmark(BookmarkConstants.TRAUMA_HISTORY,
					formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getTxtChildTraumaHist()));
			bookmarkDtoList.add(bookmarkDtoTraumaHist);

			BookmarkDto bookmarkDtoIndCansAsmnt = createBookmarkWithCodesTable(BookmarkConstants.CANS_ASSESS,
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildCansAsmnt(),
					CodesConstant.CAFCTRS1);
			bookmarkDtoList.add(bookmarkDtoIndCansAsmnt);

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildCansAsmnt())) {
				FormDataGroupDto formDataGrpChildCansAsmnt = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CANS_DATE_ASSESS, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkChildCansAsmntList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoDtCansAsmnt = createBookmark(BookmarkConstants.CANS_DATE_ASSESS,
						DateUtils.stringDt(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getDtCansAssmt()));
				bookmarkChildCansAsmntList.add(bookmarkDtoDtCansAsmnt);

				BookmarkDto bookmarkDtoNmClnclPrfsnl = createBookmark(BookmarkConstants.CANS_CLINICAL_PROFESSIONAL,
						formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getNmClnclPrfsnl()));
				bookmarkChildCansAsmntList.add(bookmarkDtoNmClnclPrfsnl);
				formDataGrpChildCansAsmnt.setBookmarkDtoList(bookmarkChildCansAsmntList);
				formDataGroupDtoList.add(formDataGrpChildCansAsmnt);

				FormDataGroupDto formDataGrpRecmndCansAssmnt = createFormDataGroup(
						FormGroupsConstants.TMPLAT_RECOMMEND_CANS_ASSESS, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRecmndCansAssmntList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoRecmndCansAssmnt = createBookmark(BookmarkConstants.RECOMMEND_CANS_ASSESS,
						formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getTxtRecmndtnCansAsmnt()));
				bookmarkRecmndCansAssmntList.add(bookmarkDtoRecmndCansAssmnt);

				formDataGrpRecmndCansAssmnt.setBookmarkDtoList(bookmarkRecmndCansAssmntList);
				formDataGroupDtoList.add(formDataGrpRecmndCansAssmnt);
			}

			BookmarkDto bookmarkDtoEmotnlNeeds = createBookmark(BookmarkConstants.EMOTIONAL_NEEDS,
					formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getTxtEmtnlNeeds()));
			bookmarkDtoList.add(bookmarkDtoEmotnlNeeds);

			BookmarkDto bookmarkDtoChildTherapst = createBookmarkWithCodesTable(BookmarkConstants.CHILD_THERAPIST,
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildThrpst(), CodesConstant.CAFCTRS1);
			bookmarkDtoList.add(bookmarkDtoChildTherapst);

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndChildThrpst())) {
				FormDataGroupDto formDataGrpTherapstNm = createFormDataGroup(FormGroupsConstants.TMPLAT_THERAPIST_NAME,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkTherapstNmList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoTherapstNm = createBookmark(BookmarkConstants.THERAPIST_NAME,
						formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getNmThrpst()));
				bookmarkTherapstNmList.add(bookmarkDtoTherapstNm);

				formDataGrpTherapstNm.setBookmarkDtoList(bookmarkTherapstNmList);
				formDataGroupDtoList.add(formDataGrpTherapstNm);

				FormDataGroupDto formDataGrpTherapstInfo = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CHILD_THERAPIST_YES, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkTherapstInfoList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoTherapstImpresn = createBookmark(BookmarkConstants.THERAPIST_IMPRESSIONS,
						formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getTxtTherapistDiagnsis()));
				bookmarkTherapstInfoList.add(bookmarkDtoTherapstImpresn);

				BookmarkDto bookmarkDtoTherapyStrength = createBookmark(BookmarkConstants.THERAPY_STRENGTH,
						formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getTxtThrptcStrngth()));
				bookmarkTherapstInfoList.add(bookmarkDtoTherapyStrength);

				formDataGrpTherapstInfo.setBookmarkDtoList(bookmarkTherapstInfoList);
				formDataGroupDtoList.add(formDataGrpTherapstInfo);

			}

			BookmarkDto bookmarkDtoPsychEval = createBookmarkWithCodesTable(BookmarkConstants.PSYCHOLOGICAL_EVALUATION,
					childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndPsychEvaltn(), CodesConstant.CAFCTRS1);
			bookmarkDtoList.add(bookmarkDtoPsychEval);

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndPsychEvaltn())) {
				FormDataGroupDto formDataGrpPsychEvalInfo = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DATE_PSYCHOLOGICAL, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPsychEvalInfoList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoDtPsych = createBookmark(BookmarkConstants.DATE_PSYCHOLOGICAL,
						DateUtils.stringDt(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getDtPsych()));
				bookmarkPsychEvalInfoList.add(bookmarkDtoDtPsych);

				BookmarkDto bookmarkDtoPsycholgclClncl = createBookmark(BookmarkConstants.PSYCHOLOGICAL_CLINICAL_PROF,
						formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getNmClnclPrfsnlPsych()));
				bookmarkPsychEvalInfoList.add(bookmarkDtoPsycholgclClncl);

				formDataGrpPsychEvalInfo.setBookmarkDtoList(bookmarkPsychEvalInfoList);
				formDataGroupDtoList.add(formDataGrpPsychEvalInfo);

				FormDataGroupDto formDataGrpCurrDiagnsis = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PSYCHOLOGICAL_EVALUATION_YES, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkCurrDiagnsisList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoCurrDiagnsis = createBookmark(BookmarkConstants.CURRENT_DIAGNOSIS,
						formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getTxtCurrDiagnsis()));
				bookmarkCurrDiagnsisList.add(bookmarkDtoCurrDiagnsis);

				formDataGrpCurrDiagnsis.setBookmarkDtoList(bookmarkCurrDiagnsisList);
				formDataGroupDtoList.add(formDataGrpCurrDiagnsis);
			}

			BookmarkDto bookmarkDtoPsychRecmndtn = createBookmark(BookmarkConstants.PSYCHOLOGICAL_RECOMEND,
					formatTextValue(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getTxtPsychRecmndtn()));
			bookmarkDtoList.add(bookmarkDtoPsychRecmndtn);

			List<FormDataGroupDto> formDataGroupTmpEmotinlGoalsList = new ArrayList<FormDataGroupDto>();

			if (ServiceConstants.YES.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotApplEmotional = createBookmark(BookmarkConstants.NA_EMOTIONAL_GOAL,
						ServiceConstants.NA_STRING);
				bookmarkDtoList.add(bookmarkDtoNotApplEmotional);
			} else if (!TypeConvUtil
					.isNullOrEmpty(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getChildPlanGoalDtoList())) {

				for (ChildPlanGoalDto childPlanGoal : childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto()
						.getChildPlanGoalDtoList()) {
					List<BookmarkDto> bookmarkTmpEmtnlGoalList = new ArrayList<BookmarkDto>();
					FormDataGroupDto formDataGroupTmpEmotinlGoals = createFormDataGroup(
							FormGroupsConstants.TMPLAT_EMOTIONAL_GOALS, FormConstants.EMPTY_STRING);

					BookmarkDto bookmarkDtoIntlectualGoal = createBookmark(BookmarkConstants.EMOTIONAL_GOALS,
							formatTextValue(childPlanGoal.getTxtGoals()));
					bookmarkTmpEmtnlGoalList.add(bookmarkDtoIntlectualGoal);

					BookmarkDto bookmarkDtoIntlectualDtTargt = createBookmark(BookmarkConstants.EMOTIONAL_TARGET_DATE,
							DateUtils.stringDt(childPlanGoal.getDtTrgt()));
					bookmarkTmpEmtnlGoalList.add(bookmarkDtoIntlectualDtTargt);

					BookmarkDto bookmarkDtoIntlectualCriteria = createBookmark(BookmarkConstants.EMOTIONAL_CRITERIA,
							formatTextValue(childPlanGoal.getTxtCritraAchevd()));
					bookmarkTmpEmtnlGoalList.add(bookmarkDtoIntlectualCriteria);

					BookmarkDto bookmarkDtoIntlectualIntervntn = createBookmark(
							BookmarkConstants.EMOTIONAL_INTERVENTIONS, childPlanGoal.getTxtPlndIntrvntns());
					bookmarkTmpEmtnlGoalList.add(bookmarkDtoIntlectualIntervntn);

					BookmarkDto bookmarkDtoIntlectualFreq = createBookmark(BookmarkConstants.EMOTIONAL_FREQUENCY,
							formatTextValue(childPlanGoal.getTxtFreq()));
					bookmarkTmpEmtnlGoalList.add(bookmarkDtoIntlectualFreq);

					BookmarkDto bookmarkDtoIntlectualRespnsblePrty = createBookmark(
							BookmarkConstants.EMOTIONAL_RESPONSIBLE, formatTextValue(childPlanGoal.getTxtRspnsParty()));
					bookmarkTmpEmtnlGoalList.add(bookmarkDtoIntlectualRespnsblePrty);

					if(!childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType().equals(ServiceConstants.CHILD_PLAN_INITIAL))
					{
					List<FormDataGroupDto> formDataGroupTmplatIntlectualProgSummryList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupTmplatIntlectualProgSummry  = createFormDataGroup(
								FormGroupsConstants.TMPLAT_EMOTIONAL_PROGRESS, FormGroupsConstants.TMPLAT_EMOTIONAL_GOALS);
					List<BookmarkDto> bookmarkIntlectualProgSummrylist = new ArrayList<BookmarkDto>();	
					BookmarkDto bookmarkDtoIntlectualProgSummry = createBookmark(BookmarkConstants.EMOTIONAL_PROGRESS,
							formatTextValue(childPlanGoal.getTxtProgrssSumm()));
					bookmarkIntlectualProgSummrylist.add(bookmarkDtoIntlectualProgSummry);
					formDataGroupTmplatIntlectualProgSummry.setBookmarkDtoList(bookmarkIntlectualProgSummrylist);
					formDataGroupTmplatIntlectualProgSummryList.add(formDataGroupTmplatIntlectualProgSummry);
					formDataGroupTmpEmotinlGoals.setFormDataGroupList(formDataGroupTmplatIntlectualProgSummryList);					
					}
					formDataGroupTmpEmotinlGoals.setBookmarkDtoList(bookmarkTmpEmtnlGoalList);
					formDataGroupTmpEmotinlGoalsList.add(formDataGroupTmpEmotinlGoals);
				}
				formDataGroupDtoList.addAll(formDataGroupTmpEmotinlGoalsList);

			} else if (ServiceConstants.YES
					.equals(childPlanOfServiceDto.getChildPlanEmtnlThrptcDtlDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotApplEmotional = createBookmark(BookmarkConstants.NA_EMOTIONAL_GOAL,
						ServiceConstants.NA_STRING);
				bookmarkDtoList.add(bookmarkDtoNotApplEmotional);
			}

		}

		// BEHAVIOR
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPalnBehaviorMgntDto())) {

			BookmarkDto bookmarkDtoBehaviorApproach = createBookmark(BookmarkConstants.BEHAVIOR_APPROCH,
					formatTextValue(childPlanOfServiceDto.getChildPalnBehaviorMgntDto().getTxtBhvrMgmtApprch()));
			bookmarkDtoList.add(bookmarkDtoBehaviorApproach);

			BookmarkDto bookmarkDtoDisciplnTechniq = createBookmark(BookmarkConstants.DISCIPLINE_TECHNIQUE,
					formatTextValue(childPlanOfServiceDto.getChildPalnBehaviorMgntDto().getTxtDscplnTechnq()));
			bookmarkDtoList.add(bookmarkDtoDisciplnTechniq);

			BookmarkDto bookmarkDtoBehaviorTechniq = createBookmark(BookmarkConstants.BEHAVIOR_TECHNIQUE,
					formatTextValue(childPlanOfServiceDto.getChildPalnBehaviorMgntDto().getTxtBhvrMgmtTechnq()));
			bookmarkDtoList.add(bookmarkDtoBehaviorTechniq);

			BookmarkDto bookmarkDtoBehaviorStrength = createBookmark(BookmarkConstants.BEHAVIOR_STRENGTH,
					formatTextValue(childPlanOfServiceDto.getChildPalnBehaviorMgntDto().getTxtBhvrStrngthNeeds()));
			bookmarkDtoList.add(bookmarkDtoBehaviorStrength);

			List<FormDataGroupDto> formDataGroupBehaviorList = new ArrayList<FormDataGroupDto>();
			if (ServiceConstants.YES.equals(childPlanOfServiceDto.getChildPalnBehaviorMgntDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotEduSummry = createBookmark(BookmarkConstants.NA_BEHAVIOUR_GOAL,
						ServiceConstants.NA_STRING);
				bookmarkDtoList.add(bookmarkDtoNotEduSummry);
			} else if (!TypeConvUtil
					.isNullOrEmpty(childPlanOfServiceDto.getChildPalnBehaviorMgntDto().getChildPlanGoalDtoList())) {

				for (ChildPlanGoalDto childPlanGoal : childPlanOfServiceDto.getChildPalnBehaviorMgntDto()
						.getChildPlanGoalDtoList()) {

					FormDataGroupDto formDataGroupBehavior = createFormDataGroup(
							FormGroupsConstants.TMPLAT_BEHAVIORL_GOALS, FormConstants.EMPTY_STRING);

					List<BookmarkDto> bookmarkBehaviorList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkDtoIntlectualGoal = createBookmark(BookmarkConstants.BEHAVIOR_GOALS,
							formatTextValue(childPlanGoal.getTxtGoals()));
					bookmarkBehaviorList.add(bookmarkDtoIntlectualGoal);

					BookmarkDto bookmarkDtoIntlectualDtTargt = createBookmark(BookmarkConstants.BEHAVIOR_TARGET_DATE,
							DateUtils.stringDt(childPlanGoal.getDtTrgt()));
					bookmarkBehaviorList.add(bookmarkDtoIntlectualDtTargt);

					BookmarkDto bookmarkDtoIntlectualCriteria = createBookmark(BookmarkConstants.BEHAVIOR_CRITERIA,
							formatTextValue(childPlanGoal.getTxtCritraAchevd()));
					bookmarkBehaviorList.add(bookmarkDtoIntlectualCriteria);

					BookmarkDto bookmarkDtoIntlectualIntervntn = createBookmark(
							BookmarkConstants.BEHAVIOR_INTERVENTIONS, formatTextValue(childPlanGoal.getTxtPlndIntrvntns()));
					bookmarkBehaviorList.add(bookmarkDtoIntlectualIntervntn);

					BookmarkDto bookmarkDtoIntlectualFreq = createBookmark(BookmarkConstants.BEHAVIOR_FREQUENCY,
							formatTextValue(childPlanGoal.getTxtFreq()));
					bookmarkBehaviorList.add(bookmarkDtoIntlectualFreq);

					BookmarkDto bookmarkDtoIntlectualRespnsblePrty = createBookmark(
							BookmarkConstants.BEHAVIOR_RESPONSIBLE, formatTextValue(childPlanGoal.getTxtRspnsParty()));
					bookmarkBehaviorList.add(bookmarkDtoIntlectualRespnsblePrty);

					if(!childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType().equals(ServiceConstants.CHILD_PLAN_INITIAL))
					{
					List<FormDataGroupDto> formDataGroupTmplatIntlectualProgSummryList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupTmplatIntlectualProgSummry  = createFormDataGroup(
								FormGroupsConstants.TMPLAT_BEHAVIOR_PROGRESS, FormGroupsConstants.TMPLAT_BEHAVIORL_GOALS);
					List<BookmarkDto> bookmarkIntlectualProgSummrylist = new ArrayList<BookmarkDto>();	
					BookmarkDto bookmarkDtoIntlectualProgSummry = createBookmark(BookmarkConstants.BEHAVIOR_PROGRESS,
							formatTextValue(childPlanGoal.getTxtProgrssSumm()));
					bookmarkIntlectualProgSummrylist.add(bookmarkDtoIntlectualProgSummry);
					formDataGroupTmplatIntlectualProgSummry.setBookmarkDtoList(bookmarkIntlectualProgSummrylist);
					formDataGroupTmplatIntlectualProgSummryList.add(formDataGroupTmplatIntlectualProgSummry);
					formDataGroupBehavior.setFormDataGroupList(formDataGroupTmplatIntlectualProgSummryList);					
					}
					formDataGroupBehavior.setBookmarkDtoList(bookmarkBehaviorList);
					formDataGroupBehaviorList.add(formDataGroupBehavior);
				}
				formDataGroupDtoList.addAll(formDataGroupBehaviorList);

			} else if (ServiceConstants.YES
					.equals(childPlanOfServiceDto.getChildPalnBehaviorMgntDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotEduSummry = createBookmark(BookmarkConstants.NA_BEHAVIOUR_GOAL,
						ServiceConstants.NA_STRING);
				bookmarkDtoList.add(bookmarkDtoNotEduSummry);
			}

		}

		// HEALTH CARE SUMMARY
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto())) {
			BookmarkDto bookmarkDtoNmPriMedCons = createBookmark(BookmarkConstants.MEDICAL_CONSENTER_NAME,
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNmPrimaryMedCons());
			bookmarkDtoList.add(bookmarkDtoNmPriMedCons);

			if (!ObjectUtils
					.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtMedCnsntrTrngCmpltd())) {

				FormDataGroupDto formDataGrpPlnsCompThs = createFormDataGroup(
						FormGroupsConstants.TMPLAT_TRAINING_COMPLETED, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPlnsCompThsList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtoDtMedCnstrTrngCmpltd = createBookmark(
						BookmarkConstants.CONSENTER_DATE_TRAININGCOMPLETED, DateUtils.stringDt(
								childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtMedCnsntrTrngCmpltd()));
				bookmarkPlnsCompThsList.add(bookmarkDtoDtMedCnstrTrngCmpltd);
				formDataGrpPlnsCompThs.setBookmarkDtoList(bookmarkPlnsCompThsList);
				formDataGroupDtoList.add(formDataGrpPlnsCompThs);
			}

			BookmarkDto bookmarkDtoIndChildRecvMedChkUp = createBookmarkWithCodesTable(
					BookmarkConstants.RECEIVED_THS_CHECKUP,
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndChildRecvMedChkup(),
					CodesConstant.CAFCTRS1);
			bookmarkDtoList.add(bookmarkDtoIndChildRecvMedChkUp);

			if (FormConstants.N
					.equals(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndChildRecvMedChkup())) {
				FormDataGroupDto formDataGrpPlnsCompThs = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PLANS_COMPLETE_THS, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPlnsCompThsList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoPlnsCompThs = createBookmark(BookmarkConstants.PLANS_COMPLETE_THS,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtPlansStepMedChkup()));
				bookmarkPlnsCompThsList.add(bookmarkDtoPlnsCompThs);

				formDataGrpPlnsCompThs.setBookmarkDtoList(bookmarkPlnsCompThsList);
				formDataGroupDtoList.add(formDataGrpPlnsCompThs);
			}

			if (FormConstants.Y
					.equals(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndChildRecvMedChkup())) {
				FormDataGroupDto formDataGrpRecvMedChkup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_RECEIVED_THS_CHECKUP, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRecvMedChkupList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoDtLastTxMedChkp = createBookmark(BookmarkConstants.DATE_THS, DateUtils
						.stringDt(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtLastTxMedChkp()));
				bookmarkRecvMedChkupList.add(bookmarkDtoDtLastTxMedChkp);

				BookmarkDto bookmarkDtoDtNxtDueMedChkp = createBookmark(BookmarkConstants.NEXT_DUEDATE_THS_, DateUtils
						.stringDt(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtNextDueTxMedChkp()));
				bookmarkRecvMedChkupList.add(bookmarkDtoDtNxtDueMedChkp);

				BookmarkDto bookmarkDtoNmClncPrfsnlMedChkp = createBookmark(BookmarkConstants.THS_CLINICAL_PROFES,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNmClnclPrfsnlTxMedChkp()));
				bookmarkRecvMedChkupList.add(bookmarkDtoNmClncPrfsnlMedChkp);

				BookmarkDto bookmarkDtoTxtAddressTxMedChkp = createBookmark(BookmarkConstants.THS_CLINICAL_PROFES_ADDR,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtAddressTxMedChkp()));
				bookmarkRecvMedChkupList.add(bookmarkDtoTxtAddressTxMedChkp);

				BookmarkDto bookmarkDtoTxtPhnTxMedChkp = createBookmark(BookmarkConstants.THS_CLINICAL_PROFES_PHONE,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtPhoneNbrTxMedChkp()));
				bookmarkRecvMedChkupList.add(bookmarkDtoTxtPhnTxMedChkp);

				formDataGrpRecvMedChkup.setBookmarkDtoList(bookmarkRecvMedChkupList);
				formDataGroupDtoList.add(formDataGrpRecvMedChkup);
			}

			BookmarkDto bookmarkDtoMedCndtn = createBookmark(BookmarkConstants.MED_COND,
					formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtAnyMedCndtns()));
			bookmarkDtoList.add(bookmarkDtoMedCndtn);

			BookmarkDto bookmarkDtoMedStrengths = createBookmark(BookmarkConstants.MED_STRENGTH,
					formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtMedStrngth()));
			bookmarkDtoList.add(bookmarkDtoMedStrengths);

			BookmarkDto bookmarkDtoMedNeedPln = createBookmark(BookmarkConstants.MED_NEEDS_ADDRESS,
					formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtMedNeedPlan()));
			bookmarkDtoList.add(bookmarkDtoMedNeedPln);

			BookmarkDto bookmarkDtoIndPrimMedNeeds = createBookmarkWithCodesTable(BookmarkConstants.PRIM_MED_NEEDS,
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndChildIdentfdPrmyMed(),
					CodesConstant.CAFCTRS1);
			bookmarkDtoList.add(bookmarkDtoIndPrimMedNeeds);

			if (FormConstants.Y
					.equals(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndChildIdentfdPrmyMed())) {
				FormDataGroupDto formDataGrpMedDiagnsis = createFormDataGroup(FormGroupsConstants.TMPLAT_MED_DIAGNOSIS,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkMedDiagnsisList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoMedDiagnsis = createBookmark(BookmarkConstants.MED_DIAGNOSIS,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtExplnDiagnsis()));
				bookmarkMedDiagnsisList.add(bookmarkDtoMedDiagnsis);

				BookmarkDto bookmarkDtoMedSpeclst = createBookmark(BookmarkConstants.MED_SPECIALIST,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtLstMedSpclCnctInfo()));
				bookmarkMedDiagnsisList.add(bookmarkDtoMedSpeclst);

				BookmarkDto bookmarkDtoPrimHosptl = createBookmark(BookmarkConstants.PRIM_HOSPITAL,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtPrmyHospCnctInfo()));
				bookmarkMedDiagnsisList.add(bookmarkDtoPrimHosptl);

				BookmarkDto bookmarkDtoNursngHr = createBookmark(BookmarkConstants.NURSING_HOURS,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtNursingHrs()));
				bookmarkMedDiagnsisList.add(bookmarkDtoNursngHr);

				BookmarkDto bookmarkDtoTxtHmHlthCntctInfo = createBookmark(BookmarkConstants.HOME_HEALTH_AGENCY,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtHmeHlthCnctInfo()));
				bookmarkMedDiagnsisList.add(bookmarkDtoTxtHmHlthCntctInfo);

				BookmarkDto bookmarkDtoMedEqupmnt = createBookmark(BookmarkConstants.MED_EQUIPMENT,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtLstDmeSupls()));
				bookmarkMedDiagnsisList.add(bookmarkDtoMedEqupmnt);

				formDataGrpMedDiagnsis.setBookmarkDtoList(bookmarkMedDiagnsisList);
				formDataGroupDtoList.add(formDataGrpMedDiagnsis);
			}

			if (!ObjectUtils
					.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndAmbulnceTrnsprt())) {
				BookmarkDto bookmarkDtoTrnsprtAmbulnc = createBookmark(BookmarkConstants.TRANSPORT_AMBULANCE,
						(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndAmbulnceTrnsprt()
								.equals(FormConstants.Y)) ? FormConstants.YES : FormConstants.NO);
				bookmarkDtoList.add(bookmarkDtoTrnsprtAmbulnc);
			}

			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndDnr())) {
				BookmarkDto bookmarkDtoIsDnr = createBookmark(BookmarkConstants.IS_DNR,
						(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndDnr().equals(FormConstants.Y))
								? FormConstants.YES : FormConstants.NO);
				bookmarkDtoList.add(bookmarkDtoIsDnr);
			}

			BookmarkDto bookmarkDtoIsNPM = createBookmarkWithCodesTable(BookmarkConstants.IS_NPM,
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndNonPsychMedctn(),
					CodesConstant.CAFCTRS1);
			bookmarkDtoList.add(bookmarkDtoIsNPM);

			BookmarkDto bookmarkDtoIsPM = createBookmarkWithCodesTable(BookmarkConstants.IS_PM,
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndPsychMedctn(),
					CodesConstant.CAFCTRS1);
			bookmarkDtoList.add(bookmarkDtoIsPM);

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndPsychMedctn())) {
				FormDataGroupDto formDataGrpPsychMedctn = createFormDataGroup(FormGroupsConstants.TMPLAT_IS_PM,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPsychMedctnList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoDtLstMdctnRev = createBookmark(BookmarkConstants.DATE_LAST_MED_REVIEW, DateUtils
						.stringDt(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtLastMedctnRevw()));
				bookmarkPsychMedctnList.add(bookmarkDtoDtLstMdctnRev);

				BookmarkDto bookmarkDtoNmRevwgPhy = createBookmark(BookmarkConstants.NAME_REVIEW_PHYSICIAN,
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNmRevwgPhysician());
				bookmarkPsychMedctnList.add(bookmarkDtoNmRevwgPhy);

				BookmarkDto bookmarkDtoAddrPhysicn = createBookmark(BookmarkConstants.ADDR_REVIEW_PHYSICIAN,
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getAddrPhysicn());
				bookmarkPsychMedctnList.add(bookmarkDtoAddrPhysicn);

				BookmarkDto bookmarkDtoNbrReviewPhysicn = createBookmark(BookmarkConstants.PHONE_REVIEW_PHYSICIAN,
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNbrPhysicianPhone());
				bookmarkPsychMedctnList.add(bookmarkDtoNbrReviewPhysicn);

				formDataGrpPsychMedctn.setBookmarkDtoList(bookmarkPsychMedctnList);
				formDataGroupDtoList.add(formDataGrpPsychMedctn);
			}

			if (!ObjectUtils
					.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtRsnPsychMedctnChng())) {
				FormDataGroupDto formDataGrpRsnPsychMedctn = createFormDataGroup(
						FormGroupsConstants.TMPLAT_REASON_CHANGE_PM, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRsnPsychMedctnList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoRsnPsychMedctn = createBookmark(BookmarkConstants.REASON_CHANGE_PM,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtRsnPsychMedctnChng()));
				bookmarkRsnPsychMedctnList.add(bookmarkDtoRsnPsychMedctn);

				formDataGrpRsnPsychMedctn.setBookmarkDtoList(bookmarkRsnPsychMedctnList);
				formDataGroupDtoList.add(formDataGrpRsnPsychMedctn);
			}

			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getCdPrevPsychHosp())) {
				if (!childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getCdPrevPsychHosp()
						.equals(FormConstants.UNDEFINED)) {
					BookmarkDto bookmarkDtoHasPsychHosp = createBookmark(BookmarkConstants.HAS_PSYCHIATRIC_HOSP,
							(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getCdPrevPsychHosp()
									.equals(FormConstants.ONE)) ? FormConstants.YES : FormConstants.NO);
					bookmarkDtoList.add(bookmarkDtoHasPsychHosp);
				} else {
					BookmarkDto bookmarkDtoHasPsychHosp = createBookmark(BookmarkConstants.HAS_PSYCHIATRIC_HOSP,
							FormConstants.UNKNOWN);
					bookmarkDtoList.add(bookmarkDtoHasPsychHosp);
				}

			}

			BookmarkDto bookmarkDtoDatePsychHosp = createBookmark(BookmarkConstants.DATE_LAST_PSYCHIATRIC_HOSP,
					DateUtils.stringDt(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtLastPsychHosp()));
			bookmarkDtoList.add(bookmarkDtoDatePsychHosp);

			if (FormConstants.ONE
					.equals(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getCdPrevPsychHosp())) {
				FormDataGroupDto formDataGrpPrevPsychHosp = createFormDataGroup(
						FormGroupsConstants.TMPLAT_HAS_PSYCHIATRIC_HOSP, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPrevPsychHospList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkNmPsychHosp = createBookmark(BookmarkConstants.NAME_PSYCHIATRIC_HOSP,
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNmPsychHosp());
				bookmarkPrevPsychHospList.add(bookmarkNmPsychHosp);

				BookmarkDto bookmarkPhyscnAddrHosp = createBookmark(BookmarkConstants.PHYSCIAN_HOSPITAL_ADDRESS,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getAddrHosp()));
				bookmarkPrevPsychHospList.add(bookmarkPhyscnAddrHosp);

				formDataGrpPrevPsychHosp.setBookmarkDtoList(bookmarkPrevPsychHospList);
				formDataGroupDtoList.add(formDataGrpPrevPsychHosp);

				FormDataGroupDto formDataGrpPsychHospYes = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PSYCHIATRIC_HOSP_YES, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPsychHospYesList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkNmAdmitPhyscn = createBookmark(BookmarkConstants.NAME_ADMIT_PHYSCIAN,
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNmAdmitPhysician());
				bookmarkPsychHospYesList.add(bookmarkNmAdmitPhyscn);

				BookmarkDto bookmarkPhysNbrHospPhn = createBookmark(BookmarkConstants.PHYSCIAN_HOSPITAL_PHONE,
						childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNbrHospPhone());
				bookmarkPsychHospYesList.add(bookmarkPhysNbrHospPhn);

				BookmarkDto bookmarkResultHosp = createBookmark(BookmarkConstants.RESULTS_HOSPITALIZATION,
						formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtSummHosp()));
				bookmarkPsychHospYesList.add(bookmarkResultHosp);

				formDataGrpPsychHospYes.setBookmarkDtoList(bookmarkPsychHospYesList);
				formDataGroupDtoList.add(formDataGrpPsychHospYes);

			}

			BookmarkDto bookmarkDtoDtLstDentl = createBookmark(BookmarkConstants.DATE_LAST_DENTAL, DateUtils
					.stringDt(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtLastTxMedChkpDent()));
			bookmarkDtoList.add(bookmarkDtoDtLstDentl);

			BookmarkDto bookmarkDtoDtNxtDueDateDentl = createBookmark(BookmarkConstants.NEXT_DUEDATE_DENTAL,
					DateUtils.stringDt(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtNextDueDent()));
			bookmarkDtoList.add(bookmarkDtoDtNxtDueDateDentl);

			BookmarkDto bookmarkDtoDentistNm = createBookmark(BookmarkConstants.DENTIST_NAME,
					formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNmDentist()));
			bookmarkDtoList.add(bookmarkDtoDentistNm);

			BookmarkDto bookmarkDtoDentistAddrss = createBookmark(BookmarkConstants.DENTIST_ADDRESS,
					formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtAddressDent()));
			bookmarkDtoList.add(bookmarkDtoDentistAddrss);

			BookmarkDto bookmarkDtoDentistPh = createBookmark(BookmarkConstants.DENTIST_PHONE,
					formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtPhoneNbrDent()));
			bookmarkDtoList.add(bookmarkDtoDentistPh);

			BookmarkDto bookmarkDtoTxtSummHosp = createBookmark(BookmarkConstants.SUMMARY_DENTAL_NEEDS,
					formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtDentSumm()));
			bookmarkDtoList.add(bookmarkDtoTxtSummHosp);

			BookmarkDto bookmarkDtoDtLstVision = createBookmark(BookmarkConstants.DATE_LAST_VISION,
					DateUtils.stringDt(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtLastVisnScrng()));
			bookmarkDtoList.add(bookmarkDtoDtLstVision);

			BookmarkDto bookmarkDtoDtNxtDueVision = createBookmark(BookmarkConstants.NEXT_DUEDATE_VISION,
					DateUtils.stringDt(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtNxtDueVision()));
			bookmarkDtoList.add(bookmarkDtoDtNxtDueVision);

			BookmarkDto bookmarkDtotxtClnclPrfsnlVisn = createBookmark(BookmarkConstants.VISION_CLINICAL_PROFES,
					formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtClnclPrfsnlVisn()));
			bookmarkDtoList.add(bookmarkDtotxtClnclPrfsnlVisn);

			BookmarkDto bookmarkDtoAddrVsn = createBookmark(BookmarkConstants.VISIONL_PROFES_ADDR,
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getAddrVisn());
			bookmarkDtoList.add(bookmarkDtoAddrVsn);

			BookmarkDto bookmarkDtonbrVisnPhone = createBookmark(BookmarkConstants.VISION_PROFES_PHONE,
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNbrVisnPhone());
			bookmarkDtoList.add(bookmarkDtonbrVisnPhone);

			BookmarkDto bookmarkDtoVisnNeeds = createBookmark(BookmarkConstants.VISION_NEEDS,
					formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtSummVisn()));
			bookmarkDtoList.add(bookmarkDtoVisnNeeds);

			BookmarkDto bookmarkDtoDtLstHearing = createBookmark(BookmarkConstants.DATE_LAST_HEARING, DateUtils
					.stringDt(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtLastHearngScrng()));
			bookmarkDtoList.add(bookmarkDtoDtLstHearing);

			BookmarkDto bookmarkNmDtNxtHearingDue = createBookmark(BookmarkConstants.NEXT_DUEDATE_HEARING, DateUtils
					.stringDt(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getDtNxtHearngScrngDue()));
			bookmarkDtoList.add(bookmarkNmDtNxtHearingDue);

			BookmarkDto bookmarkNmHearngClncPrfsnl = createBookmark(BookmarkConstants.HEARING_CLINICAL_PROFES,
					formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtClnclPrfsnlHearngScrng()));
			bookmarkDtoList.add(bookmarkNmHearngClncPrfsnl);

			BookmarkDto bookmarAddrHearngClncPrfsnl = createBookmark(BookmarkConstants.HEARINGL_PROFES_ADDR,
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getAddrHearngScrng());
			bookmarkDtoList.add(bookmarAddrHearngClncPrfsnl);

			BookmarkDto bookmarPhHearngClncPrfsnl = createBookmark(BookmarkConstants.HEARING_PROFES_PHONE,
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getNbrHearngScrngPhone());
			bookmarkDtoList.add(bookmarPhHearngClncPrfsnl);

			BookmarkDto bookmarkHearingNeeds = createBookmark(BookmarkConstants.HEARING_NEEDS,
					formatTextValue(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getTxtSummHearngScrng()));
			bookmarkDtoList.add(bookmarkHearingNeeds);

			List<FormDataGroupDto> formDataGroupTmplatHealthCareList = new ArrayList<FormDataGroupDto>();

			if (ServiceConstants.YES.equals(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotApplHealthSummary = createBookmark(BookmarkConstants.NA_HEALTH_SUMM_GOAL,
						ServiceConstants.NA_STRING);
				bookmarkDtoList.add(bookmarkDtoNotApplHealthSummary);
			} else if (!TypeConvUtil.isNullOrEmpty(
					childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getChildPlanGoalDtoList())) {

				for (ChildPlanGoalDto childPlanGoal : childPlanOfServiceDto.getChildPlanHealthCareSummaryDto()
						.getChildPlanGoalDtoList()) {
					FormDataGroupDto formDataGroupTmplatHealthCare = createFormDataGroup(
							FormGroupsConstants.TMPLAT_HEALTHCARE_GOALS, FormConstants.EMPTY_STRING);

					List<BookmarkDto> bookmarkTmplatHealthCareList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoIntlectualGoal = createBookmark(BookmarkConstants.HEALTHCARE_GOALS,
							formatTextValue(childPlanGoal.getTxtGoals()));
					bookmarkTmplatHealthCareList.add(bookmarkDtoIntlectualGoal);

					BookmarkDto bookmarkDtoIntlectualDtTargt = createBookmark(BookmarkConstants.HEALTHCARE_TARGET_DATE,
							DateUtils.stringDt(childPlanGoal.getDtTrgt()));
					bookmarkTmplatHealthCareList.add(bookmarkDtoIntlectualDtTargt);

					BookmarkDto bookmarkDtoIntlectualCriteria = createBookmark(BookmarkConstants.HEALTHCARE_CRITERIA,
							formatTextValue(childPlanGoal.getTxtCritraAchevd()));
					bookmarkTmplatHealthCareList.add(bookmarkDtoIntlectualCriteria);

					BookmarkDto bookmarkDtoIntlectualIntervntn = createBookmark(
							BookmarkConstants.HEALTHCARE_INTERVENTIONS, formatTextValue(childPlanGoal.getTxtPlndIntrvntns()));
					bookmarkTmplatHealthCareList.add(bookmarkDtoIntlectualIntervntn);

					BookmarkDto bookmarkDtoIntlectualFreq = createBookmark(BookmarkConstants.HEALTHCARE_FREQUENCY,
							formatTextValue(childPlanGoal.getTxtFreq()));
					bookmarkTmplatHealthCareList.add(bookmarkDtoIntlectualFreq);

					BookmarkDto bookmarkDtoIntlectualRespnsblePrty = createBookmark(
							BookmarkConstants.HEALTHCARE_RESPONSIBLE, formatTextValue(childPlanGoal.getTxtRspnsParty()));
					bookmarkTmplatHealthCareList.add(bookmarkDtoIntlectualRespnsblePrty);

					if(!childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType().equals(ServiceConstants.CHILD_PLAN_INITIAL))
					{
					List<FormDataGroupDto> formDataGroupTmplatIntlectualProgSummryList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupTmplatIntlectualProgSummry  = createFormDataGroup(
								FormGroupsConstants.TMPLAT_HEALTHCARE_PROGRESS, FormGroupsConstants.TMPLAT_HEALTHCARE_GOALS);
					List<BookmarkDto> bookmarkIntlectualProgSummrylist = new ArrayList<BookmarkDto>();	
					BookmarkDto bookmarkDtoIntlectualProgSummry = createBookmark(BookmarkConstants.HEALTHCARE_PROGRESS,
							formatTextValue(childPlanGoal.getTxtProgrssSumm()));
					bookmarkIntlectualProgSummrylist.add(bookmarkDtoIntlectualProgSummry);
					formDataGroupTmplatIntlectualProgSummry.setBookmarkDtoList(bookmarkIntlectualProgSummrylist);
					formDataGroupTmplatIntlectualProgSummryList.add(formDataGroupTmplatIntlectualProgSummry);
					formDataGroupTmplatHealthCare.setFormDataGroupList(formDataGroupTmplatIntlectualProgSummryList);					
					}

					formDataGroupTmplatHealthCare.setBookmarkDtoList(bookmarkTmplatHealthCareList);
					formDataGroupTmplatHealthCareList.add(formDataGroupTmplatHealthCare);
				}
				formDataGroupDtoList.addAll(formDataGroupTmplatHealthCareList);
			}

			List<FormDataGroupDto> formDataGroupTmplatNPMList = new ArrayList<FormDataGroupDto>();

			if (!TypeConvUtil.isNullOrEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto())) {

				if (FormConstants.Y
						.equals(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndNonPsychMedctn())) {

					if (!TypeConvUtil.isNullOrEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto()
							.getChidPlanNonPsychMedctnDtlDtoList())) {

						for (ChidPlanPsychMedctnDtlDto chidPlanPsychMedctn : childPlanOfServiceDto
								.getChildPlanHealthCareSummaryDto().getChidPlanNonPsychMedctnDtlDtoList()) {

							FormDataGroupDto formDataGroupTmplatNPM = createFormDataGroup(
									FormGroupsConstants.TMPLAT_NPM, FormConstants.EMPTY_STRING);

							List<BookmarkDto> bookmarkTmplatNPM = new ArrayList<BookmarkDto>();

							BookmarkDto bookmarkDtoTxtMedcn = createBookmark(BookmarkConstants.CURRENT_NPM,
									chidPlanPsychMedctn.getTxtMedctn());
							bookmarkTmplatNPM.add(bookmarkDtoTxtMedcn);

							BookmarkDto bookmarkDtoTxtMedictn = createBookmark(
									BookmarkConstants.PRESCRIB_PHYSICIAN_CONTACT_NPM,
									formatTextValue(chidPlanPsychMedctn.getTxtPhysicnCntctInfo()));
							bookmarkTmplatNPM.add(bookmarkDtoTxtMedictn);

							BookmarkDto bookmarkDtoTxtDosageFreq = createBookmark(
									BookmarkConstants.DOSAGE_FREQUENCY_NPM, formatTextValue(chidPlanPsychMedctn.getTxtDosageFreqncy()));
							bookmarkTmplatNPM.add(bookmarkDtoTxtDosageFreq);

							BookmarkDto bookmarkDtoTxtRsn = createBookmark(BookmarkConstants.REASON_NPM,
									formatTextValue(chidPlanPsychMedctn.getTxtRsn()));
							bookmarkTmplatNPM.add(bookmarkDtoTxtRsn);

							BookmarkDto bookmarkDtoTxtSideEffect = createBookmark(BookmarkConstants.SIDEEFFECT_NPM,
									formatTextValue(chidPlanPsychMedctn.getTxtSideEfct()));
							bookmarkTmplatNPM.add(bookmarkDtoTxtSideEffect);
							formDataGroupTmplatNPM.setBookmarkDtoList(bookmarkTmplatNPM);
							formDataGroupTmplatNPMList.add(formDataGroupTmplatNPM);

						}
					}

				}
			}

			formDataGroupDtoList.addAll(formDataGroupTmplatNPMList);

			List<FormDataGroupDto> formDataGroupTmplatPMList = new ArrayList<FormDataGroupDto>();

			if (!TypeConvUtil.isNullOrEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto())) {

				if (FormConstants.Y
						.equals(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto().getIndPsychMedctn())) {

					if (!TypeConvUtil.isNullOrEmpty(childPlanOfServiceDto.getChildPlanHealthCareSummaryDto()
							.getChidPlanPsychMedctnDtlDtoList())) {

						for (ChidPlanPsychMedctnDtlDto chidPlanPsychMedctn : childPlanOfServiceDto
								.getChildPlanHealthCareSummaryDto().getChidPlanPsychMedctnDtlDtoList()) {

							FormDataGroupDto formDataGroupTmplatPM = createFormDataGroup(FormGroupsConstants.TMPLAT_PM,
									FormConstants.EMPTY_STRING);

							List<BookmarkDto> bookmarkTmplatPM = new ArrayList<BookmarkDto>();

							BookmarkDto bookmarkDtoTxtMedcn = createBookmark(BookmarkConstants.PRESCRIBED_NPM,
									formatTextValue(chidPlanPsychMedctn.getTxtMedctn()));
							bookmarkTmplatPM.add(bookmarkDtoTxtMedcn);

							BookmarkDto bookmarkDtoTxtMedictn = createBookmark(
									BookmarkConstants.PRESCRIB_PHYSICIAN_CONTACT_PM,
									formatTextValue(chidPlanPsychMedctn.getTxtPhysicnCntctInfo()));
							bookmarkTmplatPM.add(bookmarkDtoTxtMedictn);

							BookmarkDto bookmarkDtoTxtDosageFreq = createBookmark(BookmarkConstants.DOSAGE_FREQUENCY_PM,
									formatTextValue(chidPlanPsychMedctn.getTxtDosageFreqncy()));
							bookmarkTmplatPM.add(bookmarkDtoTxtDosageFreq);

							BookmarkDto bookmarkDtoTxtRsn = createBookmark(BookmarkConstants.REASON_PM,
									formatTextValue(chidPlanPsychMedctn.getTxtRsn()));
							bookmarkTmplatPM.add(bookmarkDtoTxtRsn);

							BookmarkDto bookmarkDtoTxtSideEffect = createBookmark(BookmarkConstants.SIDE_EFFECT_PM,
									formatTextValue(chidPlanPsychMedctn.getTxtSideEfct()));
							bookmarkTmplatPM.add(bookmarkDtoTxtSideEffect);
							formDataGroupTmplatPM.setBookmarkDtoList(bookmarkTmplatPM);
							formDataGroupTmplatPMList.add(formDataGroupTmplatPM);

						}
					}
				}
			}

			formDataGroupDtoList.addAll(formDataGroupTmplatPMList);
		}

		// SUPERVISION
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanSupervisionDto())) {

			if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanSupervisionDto().getIndSprvsnInHome())) {
				childPlanOfServiceDto.getChildPlanSupervisionDto().setIndSprvsnInHome(FormConstants.YES);
			} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanSupervisionDto().getIndSprvsnInHome())) {
				childPlanOfServiceDto.getChildPlanSupervisionDto().setIndSprvsnInHome(FormConstants.NO);
			}
			BookmarkDto bookmarkDtoIndSupervisnInside = createBookmark(BookmarkConstants.IS_SUPERVISION_INSIDE,
					childPlanOfServiceDto.getChildPlanSupervisionDto().getIndSprvsnInHome());
			bookmarkDtoList.add(bookmarkDtoIndSupervisnInside);

			if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanSupervisionDto().getIndSprvsnInHome())) {
				FormDataGroupDto formDataGrpInHomeSprvsn = createFormDataGroup(
						FormGroupsConstants.TMPLAT_IS_SUPERVISION_INSIDE, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkInHomeSprvsnList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoTxtInHmSpvsn = createBookmark(BookmarkConstants.PLAN_SUPERVISION_INSIDE,
						formatTextValue(childPlanOfServiceDto.getChildPlanSupervisionDto().getTxtInHomeSprvsn()));
				bookmarkInHomeSprvsnList.add(bookmarkDtoTxtInHmSpvsn);

				formDataGrpInHomeSprvsn.setBookmarkDtoList(bookmarkInHomeSprvsnList);
				formDataGroupDtoList.add(formDataGrpInHomeSprvsn);
			}

			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanSupervisionDto().getIndSprvsnOutHme())) {
				if (FormConstants.Y.equals(childPlanOfServiceDto.getChildPlanSupervisionDto().getIndSprvsnOutHme())) {
					childPlanOfServiceDto.getChildPlanSupervisionDto().setIndSprvsnOutHme(FormConstants.YES);
				} else if (FormConstants.N.equals(childPlanOfServiceDto.getChildPlanSupervisionDto().getIndSprvsnOutHme())) {
					childPlanOfServiceDto.getChildPlanSupervisionDto().setIndSprvsnOutHme(FormConstants.NO);
				}
			}

			BookmarkDto bookmarkDtoIndSprvsnOutHm = createBookmark(BookmarkConstants.IS_SUPERVISION_OUTSIDE,
					childPlanOfServiceDto.getChildPlanSupervisionDto().getIndSprvsnOutHme());
			bookmarkDtoList.add(bookmarkDtoIndSprvsnOutHm);

			if (FormConstants.YES.equals(childPlanOfServiceDto.getChildPlanSupervisionDto().getIndSprvsnOutHme())) {
				FormDataGroupDto formDataGrpIndSprvsnOutHme = createFormDataGroup(
						FormGroupsConstants.TMPLAT_IS_SUPERVISION_OUTSIDE, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkIndSprvsnOutHmeList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoPlnForSprvsn = createBookmark(BookmarkConstants.PLAN_SUPERVISION_OUTSIDE,
						formatTextValue(childPlanOfServiceDto.getChildPlanSupervisionDto().getTxtOutHomeSprvsn()));
				bookmarkIndSprvsnOutHmeList.add(bookmarkDtoPlnForSprvsn);

				formDataGrpIndSprvsnOutHme.setBookmarkDtoList(bookmarkIndSprvsnOutHmeList);
				formDataGroupDtoList.add(formDataGrpIndSprvsnOutHme);
			}

			BookmarkDto bookmarkDtoOthrSupervisnIssue = createBookmark(BookmarkConstants.OTHER_SUPERVISION_ISSUE,
					formatTextValue(childPlanOfServiceDto.getChildPlanSupervisionDto().getTxtOthrSprvsnIssue()));
			bookmarkDtoList.add(bookmarkDtoOthrSupervisnIssue);

			List<FormDataGroupDto> formDataGroupTmplatSuprvsnGoalsList = new ArrayList<FormDataGroupDto>();

			if (ServiceConstants.YES.equals(childPlanOfServiceDto.getChildPlanSupervisionDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotApplSupervision = createBookmark(BookmarkConstants.NA_SUPERVISION_GOAL,
						ServiceConstants.NA_STRING);
				bookmarkDtoList.add(bookmarkDtoNotApplSupervision);
			} else if (!TypeConvUtil
					.isNullOrEmpty(childPlanOfServiceDto.getChildPlanSupervisionDto().getChildPlanGoalDtoList())) {

				for (ChildPlanGoalDto childPlanGoal : childPlanOfServiceDto.getChildPlanSupervisionDto()
						.getChildPlanGoalDtoList()) {
					FormDataGroupDto formDataGroupTmplatSuprvsnGoals = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SUPERVISION_GOALS, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkTmplatSuprvisnGoalsList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoIntlectualGoal = createBookmark(BookmarkConstants.SUPERVISION_GOALS,
							formatTextValue(childPlanGoal.getTxtGoals()));
					bookmarkTmplatSuprvisnGoalsList.add(bookmarkDtoIntlectualGoal);

					BookmarkDto bookmarkDtoIntlectualDtTargt = createBookmark(BookmarkConstants.SUPERVISION_TARGET_DATE,
							DateUtils.stringDt(childPlanGoal.getDtTrgt()));
					bookmarkTmplatSuprvisnGoalsList.add(bookmarkDtoIntlectualDtTargt);

					BookmarkDto bookmarkDtoIntlectualCriteria = createBookmark(BookmarkConstants.SUPERVISION_CRITERIA,
							formatTextValue(childPlanGoal.getTxtCritraAchevd()));
					bookmarkTmplatSuprvisnGoalsList.add(bookmarkDtoIntlectualCriteria);

					BookmarkDto bookmarkDtoIntlectualIntervntn = createBookmark(
							BookmarkConstants.SUPERVISION_INTERVENTIONS, formatTextValue(childPlanGoal.getTxtPlndIntrvntns()));
					bookmarkTmplatSuprvisnGoalsList.add(bookmarkDtoIntlectualIntervntn);

					BookmarkDto bookmarkDtoIntlectualFreq = createBookmark(BookmarkConstants.SUPERVISION_FREQUENCY,
							formatTextValue(childPlanGoal.getTxtFreq()));
					bookmarkTmplatSuprvisnGoalsList.add(bookmarkDtoIntlectualFreq);

					BookmarkDto bookmarkDtoIntlectualRespnsblePrty = createBookmark(
							BookmarkConstants.SUPERVISION_RESPONSIBLE, formatTextValue(childPlanGoal.getTxtRspnsParty()));
					bookmarkTmplatSuprvisnGoalsList.add(bookmarkDtoIntlectualRespnsblePrty);

					
					if(!childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType().equals(ServiceConstants.CHILD_PLAN_INITIAL))
					{
					List<FormDataGroupDto> formDataGroupTmplatIntlectualProgSummryList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupTmplatIntlectualProgSummry  = createFormDataGroup(
								FormGroupsConstants.TMPLAT_SUPERVISION_PROGRESS, FormGroupsConstants.TMPLAT_SUPERVISION_GOALS);
					List<BookmarkDto> bookmarkIntlectualProgSummrylist = new ArrayList<BookmarkDto>();	
					BookmarkDto bookmarkDtoIntlectualProgSummry = createBookmark(BookmarkConstants.SUPERVISION_PROGRESS,
							formatTextValue(childPlanGoal.getTxtProgrssSumm()));
					bookmarkIntlectualProgSummrylist.add(bookmarkDtoIntlectualProgSummry);
					formDataGroupTmplatIntlectualProgSummry.setBookmarkDtoList(bookmarkIntlectualProgSummrylist);
					formDataGroupTmplatIntlectualProgSummryList.add(formDataGroupTmplatIntlectualProgSummry);
					formDataGroupTmplatSuprvsnGoals.setFormDataGroupList(formDataGroupTmplatIntlectualProgSummryList);					
					}
					
					formDataGroupTmplatSuprvsnGoals.setBookmarkDtoList(bookmarkTmplatSuprvisnGoalsList);
					formDataGroupTmplatSuprvsnGoalsList.add(formDataGroupTmplatSuprvsnGoals);
				}
				formDataGroupDtoList.addAll(formDataGroupTmplatSuprvsnGoalsList);
			}

		}

		// SOCIAL AND RECREATIONAL
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanSocialRecreationalDto())) {
			BookmarkDto bookmarkDtoCmntySoclNeeds = createBookmark(BookmarkConstants.COMMUNITY_SOCIAL_NEEDS,
					formatTextValue(childPlanOfServiceDto.getChildPlanSocialRecreationalDto().getTxtCmntySoclNeed()));
			bookmarkDtoList.add(bookmarkDtoCmntySoclNeeds);

			BookmarkDto bookmarkDtoTxtPlnNormlcy = createBookmark(BookmarkConstants.ENSURE_NORMALCY,
					formatTextValue(childPlanOfServiceDto.getChildPlanSocialRecreationalDto().getTxtPlanNrmlcy()));
			bookmarkDtoList.add(bookmarkDtoTxtPlnNormlcy);

			BookmarkDto bookmarkDtoEnsrConctns = createBookmark(BookmarkConstants.ENSURE_CONNECTIONS,
					formatTextValue(childPlanOfServiceDto.getChildPlanSocialRecreationalDto().getTxtPlanEnsureCnctns()));
			bookmarkDtoList.add(bookmarkDtoEnsrConctns);

			BookmarkDto bookmarkDtoSummRecrtnlActvty = createBookmark(BookmarkConstants.RECREATIONAL_ACTIVITIES,
					formatTextValue(childPlanOfServiceDto.getChildPlanSocialRecreationalDto().getTxtSummRecrtnalActvty()));
			bookmarkDtoList.add(bookmarkDtoSummRecrtnlActvty);

			BookmarkDto bookmarkDtoSocialNeedsAddress = createBookmark(BookmarkConstants.SOCIAL_NEEDS_ADDRESS,
					formatTextValue(childPlanOfServiceDto.getChildPlanSocialRecreationalDto().getTxtNeedsPlanAddr()));
			bookmarkDtoList.add(bookmarkDtoSocialNeedsAddress);

			if (CodesConstant.CBILPLOC_230
					.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCurrentLvlCare())
					|| CodesConstant.CBILPLOC_240
							.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCurrentLvlCare())) {
				FormDataGroupDto formDataGrpSocialTherptvValue = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DATE_CONC_PERM_GOAL, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSocialTherptvValueList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDtoSocialTherptvValue = createBookmark(BookmarkConstants.SOCIAL_THERAPEUTIC_VALUE,
						formatTextValue(childPlanOfServiceDto.getChildPlanSocialRecreationalDto().getTxtThrptcValue()));
				bookmarkSocialTherptvValueList.add(bookmarkDtoSocialTherptvValue);

				formDataGrpSocialTherptvValue.setBookmarkDtoList(bookmarkSocialTherptvValueList);
				formDataGroupDtoList.add(formDataGrpSocialTherptvValue);
			}

			List<FormDataGroupDto> formDataGroupTmplatSoclGoalsList = new ArrayList<FormDataGroupDto>();
			if (ServiceConstants.YES.equals(childPlanOfServiceDto.getChildPlanSocialRecreationalDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotApplSocialRecreation = createBookmark(
						BookmarkConstants.NA_SOCIAL_RECREATION_GOAL, ServiceConstants.NA_STRING);
				bookmarkDtoList.add(bookmarkDtoNotApplSocialRecreation);
			} else if (!TypeConvUtil.isNullOrEmpty(
					childPlanOfServiceDto.getChildPlanSocialRecreationalDto().getChildPlanGoalDtoList())) {

				for (ChildPlanGoalDto childPlanGoal : childPlanOfServiceDto.getChildPlanSocialRecreationalDto()
						.getChildPlanGoalDtoList()) {
					FormDataGroupDto formDataGroupTmplatSoclGoals = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SOCIAL_GOALS, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkTmplatTmplatSoclList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoIntlectualGoal = createBookmark(BookmarkConstants.SOCIAL_GOALS,
							formatTextValue(childPlanGoal.getTxtGoals()));
					bookmarkTmplatTmplatSoclList.add(bookmarkDtoIntlectualGoal);

					BookmarkDto bookmarkDtoIntlectualDtTargt = createBookmark(BookmarkConstants.SOCIAL_TARGET_DATE,
							DateUtils.stringDt(childPlanGoal.getDtTrgt()));
					bookmarkTmplatTmplatSoclList.add(bookmarkDtoIntlectualDtTargt);

					BookmarkDto bookmarkDtoIntlectualCriteria = createBookmark(BookmarkConstants.SOCIAL_CRITERIA,
							formatTextValue(childPlanGoal.getTxtCritraAchevd()));
					bookmarkTmplatTmplatSoclList.add(bookmarkDtoIntlectualCriteria);

					BookmarkDto bookmarkDtoIntlectualIntervntn = createBookmark(BookmarkConstants.SOCIAL_INTERVENTIONS,
							formatTextValue(childPlanGoal.getTxtPlndIntrvntns()));
					bookmarkTmplatTmplatSoclList.add(bookmarkDtoIntlectualIntervntn);

					BookmarkDto bookmarkDtoIntlectualFreq = createBookmark(BookmarkConstants.SOCIAL_FREQUENCY,
							formatTextValue(childPlanGoal.getTxtFreq()));
					bookmarkTmplatTmplatSoclList.add(bookmarkDtoIntlectualFreq);

					BookmarkDto bookmarkDtoIntlectualRespnsblePrty = createBookmark(
							BookmarkConstants.SOCIAL_RESPONSIBLE, formatTextValue(childPlanGoal.getTxtRspnsParty()));
					bookmarkTmplatTmplatSoclList.add(bookmarkDtoIntlectualRespnsblePrty);

					if(!childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType().equals(ServiceConstants.CHILD_PLAN_INITIAL))
					{
					List<FormDataGroupDto> formDataGroupTmplatIntlectualProgSummryList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupTmplatIntlectualProgSummry  = createFormDataGroup(
								FormGroupsConstants.TMPLAT_SOCIAL_PROGRESS, FormGroupsConstants.TMPLAT_SOCIAL_GOALS);
					List<BookmarkDto> bookmarkIntlectualProgSummrylist = new ArrayList<BookmarkDto>();	
					BookmarkDto bookmarkDtoIntlectualProgSummry = createBookmark(BookmarkConstants.SOCIAL_PROGRESS,
							formatTextValue(childPlanGoal.getTxtProgrssSumm()));
					bookmarkIntlectualProgSummrylist.add(bookmarkDtoIntlectualProgSummry);
					formDataGroupTmplatIntlectualProgSummry.setBookmarkDtoList(bookmarkIntlectualProgSummrylist);
					formDataGroupTmplatIntlectualProgSummryList.add(formDataGroupTmplatIntlectualProgSummry);
					formDataGroupTmplatSoclGoals.setFormDataGroupList(formDataGroupTmplatIntlectualProgSummryList);	
					}

					formDataGroupTmplatSoclGoals.setBookmarkDtoList(bookmarkTmplatTmplatSoclList);
					formDataGroupTmplatSoclGoalsList.add(formDataGroupTmplatSoclGoals);
				}
				formDataGroupDtoList.addAll(formDataGroupTmplatSoclGoalsList);
			}

		}

		// TRANSITIONING TO SUCCESSFUL ADULTHOOD(FOR YOUTH AGE 13 AND OLDER)
		if ((age >= 13) || (!ObjectUtils.isEmpty(childPlanOfServiceDto.getTransAdulthoodBelowThirteenDto())
				&& ServiceConstants.YES.equals(
						childPlanOfServiceDto.getTransAdulthoodBelowThirteenDto().getIndOvrideChildUnderThirteen()))) {
			List<FormDataGroupDto> formDataGroupTmplatTransitionThirteenList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTmplatTransitionThirteen = createFormDataGroup(
					FormGroupsConstants.TMPLAT_YOUTH13, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkTransitionThirteenList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkDtoYouth13Comm = createBookmark(BookmarkConstants.YOUTH13_COMMUN,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodBelowThirteenDto().getTxtCmnctnChlng()));
			bookmarkTransitionThirteenList.add(bookmarkDtoYouth13Comm);

			BookmarkDto bookmarkDtoYouth13NeedsAddr = createBookmark(BookmarkConstants.YOUTH13_NEEDS_ADDRESS,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodBelowThirteenDto().getTxtNeedsPlanAddrCmnctn()));
			bookmarkTransitionThirteenList.add(bookmarkDtoYouth13NeedsAddr);

			BookmarkDto bookmarkDtoYouth13TxtRltnshpChlng = createBookmark(BookmarkConstants.YOUTH13_REL_STRENGTHS,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodBelowThirteenDto().getTxtRltnshpChlng()));
			bookmarkTransitionThirteenList.add(bookmarkDtoYouth13TxtRltnshpChlng);

			BookmarkDto bookmarkDtoYouth13PlnAddrComm = createBookmark(BookmarkConstants.YOUTH13_REL_NEEDS_ADDRESS,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodBelowThirteenDto().getTxtNeedsPlanAddrRltnshp()));
			bookmarkTransitionThirteenList.add(bookmarkDtoYouth13PlnAddrComm);

			List<FormDataGroupDto> formDataGroupTmplatYouthAge13List = new ArrayList<FormDataGroupDto>();
			if (ServiceConstants.YES.equals(childPlanOfServiceDto.getTransAdulthoodBelowThirteenDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotApplTransitioning = createBookmark(
						BookmarkConstants.NA_TRANSITIONING_THIRTEEN_GOAL, ServiceConstants.NA_STRING);
				bookmarkTransitionThirteenList.add(bookmarkDtoNotApplTransitioning);
			} else if (!ObjectUtils
					.isEmpty(childPlanOfServiceDto.getTransAdulthoodBelowThirteenDto().getChildPlanGoalDtoList())) {

				for (ChildPlanGoalDto childPlanGoal : childPlanOfServiceDto.getTransAdulthoodBelowThirteenDto()
						.getChildPlanGoalDtoList()) {
					FormDataGroupDto formDataGroupTmplatYouthAge13 = createFormDataGroup(
							FormGroupsConstants.TMPLAT_YOUTH13_GOALS, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkTmplatTmplatYouthAge13List = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoIntlectualGoal = createBookmark(BookmarkConstants.YOUTH13_GOALS,
							formatTextValue(childPlanGoal.getTxtGoals()));
					bookmarkTmplatTmplatYouthAge13List.add(bookmarkDtoIntlectualGoal);

					BookmarkDto bookmarkDtoIntlectualDtTargt = createBookmark(BookmarkConstants.YOUTH13_TARGET_DATE,
							DateUtils.stringDt(childPlanGoal.getDtTrgt()));
					bookmarkTmplatTmplatYouthAge13List.add(bookmarkDtoIntlectualDtTargt);

					BookmarkDto bookmarkDtoIntlectualCriteria = createBookmark(BookmarkConstants.YOUTH13_CRITERIA,
							formatTextValue(childPlanGoal.getTxtCritraAchevd()));
					bookmarkTmplatTmplatYouthAge13List.add(bookmarkDtoIntlectualCriteria);

					BookmarkDto bookmarkDtoIntlectualIntervntn = createBookmark(BookmarkConstants.YOUTH13_INTERVENTIONS,
							formatTextValue(childPlanGoal.getTxtPlndIntrvntns()));
					bookmarkTmplatTmplatYouthAge13List.add(bookmarkDtoIntlectualIntervntn);

					BookmarkDto bookmarkDtoIntlectualFreq = createBookmark(BookmarkConstants.YOUTH13_FREQUENCY,
							formatTextValue(childPlanGoal.getTxtFreq()));
					bookmarkTmplatTmplatYouthAge13List.add(bookmarkDtoIntlectualFreq);

					BookmarkDto bookmarkDtoIntlectualRespnsblePrty = createBookmark(
							BookmarkConstants.YOUTH13_RESPONSIBLE, formatTextValue(childPlanGoal.getTxtRspnsParty()));
					bookmarkTmplatTmplatYouthAge13List.add(bookmarkDtoIntlectualRespnsblePrty);

					if(!childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType().equals(ServiceConstants.CHILD_PLAN_INITIAL))
					{
					List<FormDataGroupDto> formDataGroupTmplatIntlectualProgSummryList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupTmplatIntlectualProgSummry  = createFormDataGroup(
								FormGroupsConstants.TMPLAT_YOUTH13_PROGRESS, FormGroupsConstants.TMPLAT_YOUTH13_GOALS);
					List<BookmarkDto> bookmarkIntlectualProgSummrylist = new ArrayList<BookmarkDto>();					
					BookmarkDto bookmarkDtoIntlectualProgSummry = createBookmark(BookmarkConstants.YOUTH13_PROGRESS,
							formatTextValue(childPlanGoal.getTxtProgrssSumm()));
					bookmarkIntlectualProgSummrylist.add(bookmarkDtoIntlectualProgSummry);					
					formDataGroupTmplatIntlectualProgSummry.setBookmarkDtoList(bookmarkIntlectualProgSummrylist);
					formDataGroupTmplatIntlectualProgSummryList.add(formDataGroupTmplatIntlectualProgSummry);
					formDataGroupTmplatYouthAge13.setFormDataGroupList(formDataGroupTmplatIntlectualProgSummryList);					
					}

					formDataGroupTmplatYouthAge13.setBookmarkDtoList(bookmarkTmplatTmplatYouthAge13List);
					formDataGroupTmplatYouthAge13List.add(formDataGroupTmplatYouthAge13);
				}
			}
			formDataGroupTmplatTransitionThirteen.setBookmarkDtoList(bookmarkTransitionThirteenList);
			formDataGroupTmplatTransitionThirteen.setFormDataGroupList(formDataGroupTmplatYouthAge13List);
			formDataGroupTmplatTransitionThirteenList.add(formDataGroupTmplatTransitionThirteen);
			formDataGroupDtoList.addAll(formDataGroupTmplatTransitionThirteenList);
		}

		// TRANSITIONING TO SUCCESSFUL ADULTHOOD(FOR YOUTH AGE 14 AND OLDER)
		if ((age >= 14) || (!ObjectUtils.isEmpty(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto())
				&& ServiceConstants.YES.equals(
						childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndOvrideChildUnderFourteen()))) {
			List<FormDataGroupDto> formDataGroupTmplatTransitionFourteenList = new ArrayList<FormDataGroupDto>();
			List<FormDataGroupDto> formDataGroupTmplatYouthAge14List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTmplatTransitionFourteen = createFormDataGroup(
					FormGroupsConstants.TMPLAT_YOUTH14, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkTransitionFourteenList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils
					.isEmpty(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndYouthSkillAssmnt())) {
				BookmarkDto bookmarkDtoIndYouthSkillAssmnt = createBookmark(BookmarkConstants.HAS_LIFESKILLS,
						(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndYouthSkillAssmnt()
								.equals(FormConstants.Y)) ? FormConstants.YES : FormConstants.NO);
				bookmarkTransitionFourteenList.add(bookmarkDtoIndYouthSkillAssmnt);

				if (childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndYouthSkillAssmnt()
						.equals(ServiceConstants.Y)) {

					FormDataGroupDto formDataGroupTmplatDateLifeSkills = createFormDataGroup(
							FormGroupsConstants.TMPLAT_DATE_LIFESKILLS, FormGroupsConstants.TMPLAT_YOUTH14);
					List<BookmarkDto> bookmarkDateLifeSkillsList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkDtoDtLifeSkills = createBookmark(BookmarkConstants.DATE_LIFESKILLS,
							DateUtils.stringDt(
									childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getDtLifeSkillAssmnt()));
					bookmarkDateLifeSkillsList.add(bookmarkDtoDtLifeSkills);
					formDataGroupTmplatDateLifeSkills.setBookmarkDtoList(bookmarkDateLifeSkillsList);
					formDataGroupTmplatYouthAge14List.add(formDataGroupTmplatDateLifeSkills);

					FormDataGroupDto formDataGroupTmplatAddrNeedSkillsAssmnt = createFormDataGroup(
							FormGroupsConstants.TMPLAT_STRENGTH_IDENTIFIED_LIFESKILLS,
							FormGroupsConstants.TMPLAT_YOUTH14);
					List<BookmarkDto> bookmarkNeedSkillsAssmntList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkTxtAddrNeedSkillsAssmnt = createBookmark(
							BookmarkConstants.STRENGTH_IDENTIFIED_LIFESKILLS,
							formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtAddrNeedSkillsAssmnt()));
					bookmarkNeedSkillsAssmntList.add(bookmarkTxtAddrNeedSkillsAssmnt);
					formDataGroupTmplatAddrNeedSkillsAssmnt.setBookmarkDtoList(bookmarkNeedSkillsAssmntList);
					formDataGroupTmplatYouthAge14List.add(formDataGroupTmplatAddrNeedSkillsAssmnt);

				}

				if (childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndYouthSkillAssmnt()
						.equals(ServiceConstants.N)) {
					FormDataGroupDto formDataGroupTmplatPlnCmpLifeSkills = createFormDataGroup(
							FormGroupsConstants.TMPLAT_PLAN_COMPLETE_LIFESKILLS, FormGroupsConstants.TMPLAT_YOUTH14);
					List<BookmarkDto> bookmarkPlnCmpLifeSkillsList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoPlnCmpLifeSkills = createBookmark(BookmarkConstants.PLAN_COMPLETE_LIFESKILLS,
							formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtPlanSkillAssmnt()));
					bookmarkPlnCmpLifeSkillsList.add(bookmarkDtoPlnCmpLifeSkills);
					formDataGroupTmplatPlnCmpLifeSkills.setBookmarkDtoList(bookmarkPlnCmpLifeSkillsList);
					formDataGroupTmplatYouthAge14List.add(formDataGroupTmplatPlnCmpLifeSkills);

				}

			}

			BookmarkDto bookmarkDtoLyfSkillStrength = createBookmark(BookmarkConstants.LIFESKILLS_STRENGTH,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtSkillChlng()));
			bookmarkTransitionFourteenList.add(bookmarkDtoLyfSkillStrength);

			BookmarkDto bookmarkTxtEvavLifeSkill = createBookmark(BookmarkConstants.PROGRESS_EVAL_LIFESKILLS,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtEvavlLifeSkill()));
			bookmarkTransitionFourteenList.add(bookmarkTxtEvavLifeSkill);

			BookmarkDto bookmarkDtoMentlHlthChlng = createBookmark(BookmarkConstants.MENTAL_HEALTH_MED_STRENGTH,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtMentlHlthChlng()));
			bookmarkTransitionFourteenList.add(bookmarkDtoMentlHlthChlng);

			BookmarkDto bookmarkDtoMentHlthChlng = createBookmark(BookmarkConstants.MENTAL_HEALTH_NEEDS_ADDRESS,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtNeedsPlanAddr()));
			bookmarkTransitionFourteenList.add(bookmarkDtoMentHlthChlng);

			BookmarkDto bookmarkDtoIntrstClgSchl = createBookmark(BookmarkConstants.YOUTH_INTEREST_COLLEGE,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtIntrstClgSchl()));
			bookmarkTransitionFourteenList.add(bookmarkDtoIntrstClgSchl);

			BookmarkDto bookmarkDtoEduNeedsColg = createBookmark(BookmarkConstants.EDU_NEEDS_COLLEGE,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtEductnNeed()));
			bookmarkTransitionFourteenList.add(bookmarkDtoEduNeedsColg);

			BookmarkDto bookmarkDtoDiscsNeedsClg = createBookmark(BookmarkConstants.DISCUS_NEEDS_COLLEGE,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtNeedSupprtPlansClg()));
			bookmarkTransitionFourteenList.add(bookmarkDtoDiscsNeedsClg);

			BookmarkDto bookmarkDtoStatusPalSkills = createBookmark(BookmarkConstants.STATUS_PAL_SKILLS,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtStatPalSkills()));
			bookmarkTransitionFourteenList.add(bookmarkDtoStatusPalSkills);

			if ((age >= 16) || (!ObjectUtils.isEmpty(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto())
					&& ServiceConstants.YES.equals(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto()
							.getIndOvrideCompltUnderSixteen()))) {
				FormDataGroupDto formDataGroupTmplatYouthAge16 = createFormDataGroup(FormGroupsConstants.TMPLAT_YOUTH16,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkTmplatTmplatYouthAge16List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtoParPalActvty = createBookmark(BookmarkConstants.PARTICIPATION_PAL_ACTIVITIES,
						formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtPartcptPalActivy()));
				bookmarkTmplatTmplatYouthAge16List.add(bookmarkDtoParPalActvty);

				BookmarkDto bookmarkDtoCareerGuidnceStrength = createBookmark(
						BookmarkConstants.CAREER_GUIDANCE_STRENGTH,
						formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtCarrerGuid()));
				bookmarkTmplatTmplatYouthAge16List.add(bookmarkDtoCareerGuidnceStrength);

				BookmarkDto bookmarkDtoGuidnceAddr = createBookmark(BookmarkConstants.CAREER_GUIDANCE_NEEDS_ADDRESS,
						formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtNeedsPlanAddrCarrer()));
				bookmarkTmplatTmplatYouthAge16List.add(bookmarkDtoGuidnceAddr);

				BookmarkDto bookmarkDtoHousngNeeds = createBookmark(BookmarkConstants.HOUSING_NEEDS16,
						formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtHousingNeed()));
				bookmarkTmplatTmplatYouthAge16List.add(bookmarkDtoHousngNeeds);

				if (!ObjectUtils.isEmpty(
						childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndYouthRmngFostercare())) {
					BookmarkDto bookmarkDtoYouthFC = createBookmark(BookmarkConstants.YOUTH_REMAIN_FC,
							(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndYouthRmngFostercare()
									.equals(FormConstants.Y)) ? FormConstants.YES : FormConstants.NO);
					bookmarkTmplatTmplatYouthAge16List.add(bookmarkDtoYouthFC);

					if (childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndYouthRmngFostercare()
							.equals(FormConstants.N)) {
						List<FormDataGroupDto> formDataGroupTmplatYouthPlanSupportList = new ArrayList<FormDataGroupDto>();
						FormDataGroupDto formDataGroupTmplatYouthPlanSupport = createFormDataGroup(
								FormGroupsConstants.TMPLAT_YOUTH_PLAN_SUPPORT, FormGroupsConstants.TMPLAT_YOUTH16);
						List<BookmarkDto> bookmarkTmplatYouthPlanSupportList = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkDtoYouthPlnSupprt = createBookmark(BookmarkConstants.YOUTH_PLAN_SUPPORT,
								formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtSupprtYouthPlan()));
						bookmarkTmplatYouthPlanSupportList.add(bookmarkDtoYouthPlnSupprt);
						formDataGroupTmplatYouthPlanSupport.setBookmarkDtoList(bookmarkTmplatYouthPlanSupportList);
						formDataGroupTmplatYouthPlanSupportList.add(formDataGroupTmplatYouthPlanSupport);
						formDataGroupTmplatYouthAge16.setFormDataGroupList(formDataGroupTmplatYouthPlanSupportList);

					}
				}

				BookmarkDto bookmarkDtoExtnFC = createBookmark(BookmarkConstants.EXTENDED_FC17,
						formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtExtnFostercare()));
				bookmarkTmplatTmplatYouthAge16List.add(bookmarkDtoExtnFC);

				formDataGroupTmplatYouthAge16.setBookmarkDtoList(bookmarkTmplatTmplatYouthAge16List);
				formDataGroupTmplatYouthAge14List.add(formDataGroupTmplatYouthAge16);
			}
			BookmarkDto bookmarkDtoYouthTrnst = createBookmark(BookmarkConstants.OTHER_NEEDS_SUPPORT_YOUTH,
					formatTextValue(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getTxtSupprtYouthTranst()));
			bookmarkTransitionFourteenList.add(bookmarkDtoYouthTrnst);
			// ,,,
			/*
			 * BookmarkDto bookmarkDtoBrthCert =
			 * createBookmark(BookmarkConstants.BIRTH_CERTI_YOUTH,
			 * childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().
			 * getIndPrsnlDocBc()) ;
			 * bookmarkTransitionFourteenList.add(bookmarkDtoBrthCert);
			 */
			if (ServiceConstants.YES
					.equals(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndPrsnlDocBc())) {
				FormDataGroupDto formDataGroupTmplatBrthCert = createFormDataGroup(
						FormGroupsConstants.TMPLAT_BIRTH_CERTI_YOUTH, FormConstants.EMPTY_STRING);
				formDataGroupTmplatYouthAge14List.add(formDataGroupTmplatBrthCert);
			}
			if (ServiceConstants.YES
					.equals(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndPrsnlDocSsc())) {
				FormDataGroupDto formDataGroupTmplatSSNYouth = createFormDataGroup(FormGroupsConstants.TMPLAT_SSN_YOUTH,
						FormConstants.EMPTY_STRING);
				formDataGroupTmplatYouthAge14List.add(formDataGroupTmplatSSNYouth);
			}
			if (ServiceConstants.YES
					.equals(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndPrsnlDocDl())) {
				FormDataGroupDto formDataGroupTmplatDL = createFormDataGroup(FormGroupsConstants.TMPLAT_DL_YOUTH,
						FormConstants.EMPTY_STRING);
				formDataGroupTmplatYouthAge14List.add(formDataGroupTmplatDL);
			}
			if (ServiceConstants.YES
					.equals(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndPrsnlDocPr())) {
				FormDataGroupDto formDataGroupTmplatBrthPR = createFormDataGroup(FormGroupsConstants.TMPLAT_PR_YOUTH,
						FormConstants.EMPTY_STRING);
				formDataGroupTmplatYouthAge14List.add(formDataGroupTmplatBrthPR);
			}

			if (ServiceConstants.YES.equals(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotApplTransitioning14 = createBookmark(
						BookmarkConstants.NA_TRANSITIONING_FOURTEEN_GOAL, ServiceConstants.NA_STRING);
				bookmarkTransitionFourteenList.add(bookmarkDtoNotApplTransitioning14);
			} else if (!ObjectUtils
					.isEmpty(childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto().getChildPlanGoalDtoList())) {

				for (ChildPlanGoalDto childPlanGoal : childPlanOfServiceDto.getTransAdulthoodAboveFourteenDto()
						.getChildPlanGoalDtoList()) {
					FormDataGroupDto formDataGroupTmplatYouthAge14 = createFormDataGroup(
							FormGroupsConstants.TMPLAT_YOUTH14_GOALS, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkTmplatTmplatYouthAge14List = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoIntlectualGoal = createBookmark(BookmarkConstants.YOUTH14_GOALS,
							formatTextValue(childPlanGoal.getTxtGoals()));
					bookmarkTmplatTmplatYouthAge14List.add(bookmarkDtoIntlectualGoal);

					BookmarkDto bookmarkDtoIntlectualDtTargt = createBookmark(BookmarkConstants.YOUTH14_TARGET_DATE,
							DateUtils.stringDt(childPlanGoal.getDtTrgt()));
					bookmarkTmplatTmplatYouthAge14List.add(bookmarkDtoIntlectualDtTargt);

					BookmarkDto bookmarkDtoIntlectualCriteria = createBookmark(BookmarkConstants.YOUTH14_CRITERIA,
							formatTextValue(childPlanGoal.getTxtCritraAchevd()));
					bookmarkTmplatTmplatYouthAge14List.add(bookmarkDtoIntlectualCriteria);

					BookmarkDto bookmarkDtoIntlectualIntervntn = createBookmark(BookmarkConstants.YOUTH14_INTERVENTIONS,
							formatTextValue(childPlanGoal.getTxtPlndIntrvntns()));
					bookmarkTmplatTmplatYouthAge14List.add(bookmarkDtoIntlectualIntervntn);

					BookmarkDto bookmarkDtoIntlectualFreq = createBookmark(BookmarkConstants.YOUTH14_FREQUENCY,
							formatTextValue(childPlanGoal.getTxtFreq()));
					bookmarkTmplatTmplatYouthAge14List.add(bookmarkDtoIntlectualFreq);

					BookmarkDto bookmarkDtoIntlectualRespnsblePrty = createBookmark(
							BookmarkConstants.YOUTH14_RESPONSIBLE, formatTextValue(childPlanGoal.getTxtRspnsParty()));
					bookmarkTmplatTmplatYouthAge14List.add(bookmarkDtoIntlectualRespnsblePrty);

					if(!childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType().equals(ServiceConstants.CHILD_PLAN_INITIAL))
					{						
					List<FormDataGroupDto> formDataGroupTmplatIntlectualProgSummryList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupTmplatIntlectualProgSummry  = createFormDataGroup(
									FormGroupsConstants.TMPLAT_YOUTH14_PROGRESS, FormGroupsConstants.TMPLAT_YOUTH14_GOALS);
					List<BookmarkDto> bookmarkIntlectualProgSummrylist = new ArrayList<BookmarkDto>();	
					BookmarkDto bookmarkDtoIntlectualProgSummry = createBookmark(BookmarkConstants.YOUTH14_PROGRESS,
							formatTextValue(childPlanGoal.getTxtProgrssSumm()));
					bookmarkTmplatTmplatYouthAge14List.add(bookmarkDtoIntlectualProgSummry);
					formDataGroupTmplatIntlectualProgSummry.setBookmarkDtoList(bookmarkIntlectualProgSummrylist);
					formDataGroupTmplatIntlectualProgSummryList.add(formDataGroupTmplatIntlectualProgSummry);
					formDataGroupTmplatYouthAge14.setFormDataGroupList(formDataGroupTmplatIntlectualProgSummryList);
					}					
					formDataGroupTmplatYouthAge14.setBookmarkDtoList(bookmarkTmplatTmplatYouthAge14List);
					formDataGroupTmplatYouthAge14List.add(formDataGroupTmplatYouthAge14);

				}
			}
			formDataGroupTmplatTransitionFourteen.setBookmarkDtoList(bookmarkTransitionFourteenList);
			formDataGroupTmplatTransitionFourteen.setFormDataGroupList(formDataGroupTmplatYouthAge14List);
			formDataGroupTmplatTransitionFourteenList.add(formDataGroupTmplatTransitionFourteen);
			formDataGroupDtoList.addAll(formDataGroupTmplatTransitionFourteenList);
		}

		// TREATMENT SERVICES
		if ((ServiceConstants.YES.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getIndChildRcvngSvcs()))
				&& !ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanTreatmentServiceDto())) {
			List<FormDataGroupDto> formDataGroupTmplatTrtmntServcsList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataGroupTmplatTrtmntServcs = createFormDataGroup(FormGroupsConstants.TMPLAT_TREATMENT,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkTrtmntServcsList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDtoTreatmntCriteria = createBookmark(BookmarkConstants.TREATMENT_CRITERIA,
					formatTextValue(childPlanOfServiceDto.getChildPlanTreatmentServiceDto().getTxtTrtmntCritra()));
			bookmarkTrtmntServcsList.add(bookmarkDtoTreatmntCriteria);

			BookmarkDto bookmarkDtoTreatmntServices = createBookmark(BookmarkConstants.TREATMENT_SERVICES,
					formatTextValue(childPlanOfServiceDto.getChildPlanTreatmentServiceDto().getTxtTrtmntSpclSrvc()));
			bookmarkTrtmntServcsList.add(bookmarkDtoTreatmntServices);

			BookmarkDto bookmarkDtoTreatmntIntrvntn = createBookmark(BookmarkConstants.TREATMENT_TYPES,
					formatTextValue(childPlanOfServiceDto.getChildPlanTreatmentServiceDto().getTxtTrtSrvcTypes()));
			bookmarkTrtmntServcsList.add(bookmarkDtoTreatmntIntrvntn);

			BookmarkDto bookmarkDtoIntervntnTreatmnt = createBookmark(BookmarkConstants.INTERVENTION_TREATMENT,
					formatTextValue(childPlanOfServiceDto.getChildPlanTreatmentServiceDto().getTxtTrtmntIntrvntn()));
			bookmarkTrtmntServcsList.add(bookmarkDtoIntervntnTreatmnt);

			BookmarkDto bookmarkDtoTrnsitnlLiving = createBookmark(BookmarkConstants.TRANSITIONAL_LIVING,
					formatTextValue(childPlanOfServiceDto.getChildPlanTreatmentServiceDto().getTxtTrnstnalLivingScnt()));
			bookmarkTrtmntServcsList.add(bookmarkDtoTrnsitnlLiving);

			BookmarkDto bookmarkDtoAdditinlTreatmnt = createBookmark(BookmarkConstants.ADDITIONAL_TREATMENT,
					formatTextValue(childPlanOfServiceDto.getChildPlanTreatmentServiceDto().getTxtAddtnalTrtmntSrvc()));
			bookmarkTrtmntServcsList.add(bookmarkDtoAdditinlTreatmnt);

			BookmarkDto bookmarkDtoChildrnIntense = createBookmark(BookmarkConstants.CHILDREN_INTENSE,
					formatTextValue(childPlanOfServiceDto.getChildPlanTreatmentServiceDto().getTxtIntenseLoc()));
			bookmarkTrtmntServcsList.add(bookmarkDtoChildrnIntense);

			List<FormDataGroupDto> formDataGroupTmplatTrtmntGoalsList = new ArrayList<FormDataGroupDto>();

			if (ServiceConstants.YES
					.equals(childPlanOfServiceDto.getChildPlanIntellectualDevelopDto().getIndNaGoal())) {
				BookmarkDto bookmarkDtoNotApplTreatment = createBookmark(BookmarkConstants.NA_TREATMENT_GOAL,
						ServiceConstants.NA_STRING);
				bookmarkTrtmntServcsList.add(bookmarkDtoNotApplTreatment);
			} else if (!ObjectUtils
					.isEmpty(childPlanOfServiceDto.getChildPlanTreatmentServiceDto().getChildPlanGoalDtoList())) {

				for (ChildPlanGoalDto childPlanGoal : childPlanOfServiceDto.getChildPlanTreatmentServiceDto()
						.getChildPlanGoalDtoList()) {
					FormDataGroupDto formDataGroupTmplatTrtmnt = createFormDataGroup(
							FormGroupsConstants.TMPLAT_TREATMENT_GOALS, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkTrtmntGoalsist = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoIntlectualGoal = createBookmark(BookmarkConstants.TREATMENT_GOALS,
							formatTextValue(childPlanGoal.getTxtGoals()));
					bookmarkTrtmntGoalsist.add(bookmarkDtoIntlectualGoal);

					BookmarkDto bookmarkDtoIntlectualDtTargt = createBookmark(BookmarkConstants.TREATMENT_TARGET_DATE,
							DateUtils.stringDt(childPlanGoal.getDtTrgt()));
					bookmarkTrtmntGoalsist.add(bookmarkDtoIntlectualDtTargt);

					BookmarkDto bookmarkDtoIntlectualCriteria = createBookmark(
							BookmarkConstants.TREATMENT_CRITERIA_GOAL, formatTextValue(childPlanGoal.getTxtCritraAchevd()));
					bookmarkTrtmntGoalsist.add(bookmarkDtoIntlectualCriteria);

					BookmarkDto bookmarkDtoIntlectualIntervntn = createBookmark(
							BookmarkConstants.TREATMENT_INTERVENTIONS, formatTextValue(childPlanGoal.getTxtPlndIntrvntns()));
					bookmarkTrtmntGoalsist.add(bookmarkDtoIntlectualIntervntn);

					BookmarkDto bookmarkDtoIntlectualFreq = createBookmark(BookmarkConstants.TREATMENT_FREQUENCY,
							formatTextValue(childPlanGoal.getTxtFreq()));
					bookmarkTrtmntGoalsist.add(bookmarkDtoIntlectualFreq);

					BookmarkDto bookmarkDtoIntlectualRespnsblePrty = createBookmark(
							BookmarkConstants.TREATMENT_RESPONSIBLE, formatTextValue(childPlanGoal.getTxtRspnsParty()));
					bookmarkTrtmntGoalsist.add(bookmarkDtoIntlectualRespnsblePrty);

					if(!childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanType().equals(ServiceConstants.CHILD_PLAN_INITIAL))
					{
					List<FormDataGroupDto> formDataGroupTmplatIntlectualProgSummryList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupTmplatIntlectualProgSummry  = createFormDataGroup(
								FormGroupsConstants.TMPLAT_TREATMENT_PROGRESS, FormGroupsConstants.TMPLAT_TREATMENT_GOALS);
					List<BookmarkDto> bookmarkIntlectualProgSummrylist = new ArrayList<BookmarkDto>();						
					BookmarkDto bookmarkDtoIntlectualProgSummry = createBookmark(BookmarkConstants.TREATMENT_PROGRESS,
							formatTextValue(childPlanGoal.getTxtProgrssSumm()));
					bookmarkIntlectualProgSummrylist.add(bookmarkDtoIntlectualProgSummry);
					formDataGroupTmplatIntlectualProgSummry.setBookmarkDtoList(bookmarkIntlectualProgSummrylist);
					formDataGroupTmplatIntlectualProgSummryList.add(formDataGroupTmplatIntlectualProgSummry);
					formDataGroupTmplatTrtmnt.setFormDataGroupList(formDataGroupTmplatIntlectualProgSummryList);					
					}
					formDataGroupTmplatTrtmnt.setBookmarkDtoList(bookmarkTrtmntGoalsist);
					formDataGroupTmplatTrtmntGoalsList.add(formDataGroupTmplatTrtmnt);
				}
			}

			formDataGroupTmplatTrtmntServcs.setBookmarkDtoList(bookmarkTrtmntServcsList);
			formDataGroupTmplatTrtmntServcs.setFormDataGroupList(formDataGroupTmplatTrtmntGoalsList);
			formDataGroupTmplatTrtmntServcsList.add(formDataGroupTmplatTrtmntServcs);
			formDataGroupDtoList.addAll(formDataGroupTmplatTrtmntServcsList);
		}

		// CHILD AND FAMILY TEAM PARTICIPATION IN PLAN
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto())) {
			BookmarkDto bookmarkDtoChildPar = createBookmark(BookmarkConstants.CHILD_NO_PARTICIPATION,
					formatTextValue(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getTxtChildRefuse()));
			bookmarkDtoList.add(bookmarkDtoChildPar);

			BookmarkDto bookmarkDtoReviewedPlan = createBookmark(BookmarkConstants.WHO_REVIEWED_PLAN,
					formatTextValue(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getTxtRevwdPlanWthChild()));
			bookmarkDtoList.add(bookmarkDtoReviewedPlan);

			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getCdChildAgrmntPlan())) {
				if (childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getCdChildAgrmntPlan()
						.equals(ServiceConstants.A)) {
					BookmarkDto bookmarkDtoChildAgreedPlan = createBookmark(BookmarkConstants.CHILD_AGREED_PLAN,
							ServiceConstants.NOT);
					bookmarkDtoList.add(bookmarkDtoChildAgreedPlan);
				} else {
					BookmarkDto bookmarkDtoChildAgreedPlan = createBookmarkWithCodesTable(
							BookmarkConstants.CHILD_AGREED_PLAN,
							childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getCdChildAgrmntPlan(),
							CodesConstant.CAFCTRS1);
					bookmarkDtoList.add(bookmarkDtoChildAgreedPlan);
				}
			}

			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getCdChildAgrmntPlan())
					&& FormConstants.N.equalsIgnoreCase(
							childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getCdChildAgrmntPlan())) {
				FormDataGroupDto formDataGroupTmplatTrtmntChildNotAgree = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CHILD_AGREED_PLAN_EXPLN, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPartcptnistChildNotAgree = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtoChildNotAgreedPlan = createBookmark(BookmarkConstants.CHILD_NOTAGREED_PLAN,
						formatTextValue(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getTxtChildAgrmntPlan()));
				bookmarkPartcptnistChildNotAgree.add(bookmarkDtoChildNotAgreedPlan);
				formDataGroupTmplatTrtmntChildNotAgree.setBookmarkDtoList(bookmarkPartcptnistChildNotAgree);
				formDataGroupDtoList.add(formDataGroupTmplatTrtmntChildNotAgree);
			}
			BookmarkDto bookmarkDtoCmntsChild = createBookmark(BookmarkConstants.COMMENTS_CHILD,
					formatTextValue(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getTxtCmmntsFrmChild()));
			bookmarkDtoList.add(bookmarkDtoCmntsChild);

			BookmarkDto bookmarkDtoCWPhone = createBookmark(BookmarkConstants.CASEWORKER_PHONE,
					childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getNbrCswrkrPhone());
			bookmarkDtoList.add(bookmarkDtoCWPhone);

			BookmarkDto bookmarkDtoAtrnyPhone = createBookmark(BookmarkConstants.ATTORNEY_AD_LITEM,
					childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getNbrAtrnyPhone());
			bookmarkDtoList.add(bookmarkDtoAtrnyPhone);

			BookmarkDto bookmarkDtoGuardianPh = createBookmark(BookmarkConstants.GUARDIAN_AD_LITEM,
					childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getNbrGrdnPhone());
			bookmarkDtoList.add(bookmarkDtoGuardianPh);

			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getIndMthrPartcpt())) {
				BookmarkDto bookmarkDtoMotherPartcpt = createBookmark(BookmarkConstants.MOTHER_PARTICIPATION,
						childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getIndMthrPartcpt()
								.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT : ServiceConstants.NO_TEXT);
				bookmarkDtoList.add(bookmarkDtoMotherPartcpt);

				if (childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getIndMthrPartcpt()
						.equals(ServiceConstants.N)) {
					FormDataGroupDto formDataGroupTmplatMotherParticptn = createFormDataGroup(
							FormGroupsConstants.TMPLAT_MOTHER_PARTICIPATION_COMMENTS, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkPartcptnist = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoTeamParName = createBookmark(
							BookmarkConstants.NO_MOTHER_PARTICIPATION_COMMENTS,
							formatTextValue(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getTxtMthrPartcpt()));
					bookmarkPartcptnist.add(bookmarkDtoTeamParName);
					formDataGroupTmplatMotherParticptn.setBookmarkDtoList(bookmarkPartcptnist);
					formDataGroupDtoList.add(formDataGroupTmplatMotherParticptn);
				}
			}

			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getIndFthrPartcpt())) {
				BookmarkDto bookmarkDtoFatherPartcpt = createBookmark(BookmarkConstants.FATHER_PARTICIPATION,
						childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getIndFthrPartcpt()
								.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT : ServiceConstants.NO_TEXT);
				bookmarkDtoList.add(bookmarkDtoFatherPartcpt);

				if (childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getIndFthrPartcpt()
						.equals(ServiceConstants.N)) {
					FormDataGroupDto formDataGroupTmplatMotherParticptn = createFormDataGroup(
							FormGroupsConstants.TMPLAT_FATHER_PARTICIPATION_COMMENTS, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkPartcptnist = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoTeamParName = createBookmark(
							BookmarkConstants.NO_FATHER_PARTICIPATION_COMMENTS,
							formatTextValue(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getTxtFthrPartcpt()));
					bookmarkPartcptnist.add(bookmarkDtoTeamParName);
					formDataGroupTmplatMotherParticptn.setBookmarkDtoList(bookmarkPartcptnist);
					formDataGroupDtoList.add(formDataGroupTmplatMotherParticptn);
				}
			}

			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getIndCrgvrPartcpt())) {
				BookmarkDto bookmarkDtoCareGiverPartcpt = createBookmark(BookmarkConstants.CAREGIVER_PARTICIPATION,
						childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getIndCrgvrPartcpt()
								.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT : ServiceConstants.NO_TEXT);
				bookmarkDtoList.add(bookmarkDtoCareGiverPartcpt);

				if (childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getIndCrgvrPartcpt()
						.equals(ServiceConstants.N)) {
					FormDataGroupDto formDataGroupTmplatMotherParticptn = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CAREGIVER_PARTICIPATION_COMMENTS, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkPartcptnist = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDtoTeamParName = createBookmark(
							BookmarkConstants.NO_CAREGIVER_PARTICIPATION_COMMENTS,
							formatTextValue(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getTxtCrgvrPartcpt()));
					bookmarkPartcptnist.add(bookmarkDtoTeamParName);
					formDataGroupTmplatMotherParticptn.setBookmarkDtoList(bookmarkPartcptnist);
					formDataGroupDtoList.add(formDataGroupTmplatMotherParticptn);
				}
			}

			List<FormDataGroupDto> formDataGroupTmplatParticptnList = new ArrayList<FormDataGroupDto>();

			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto())) {

				BookmarkDto bookmarkDtoChildParticip = createBookmark(BookmarkConstants.CHILD_PARTICIPATION,
						childPlanOfServiceDto.getChildPlanInformationDto().getNmChild());
				bookmarkDtoList.add(bookmarkDtoChildParticip);

				if (!ObjectUtils.isEmpty(
						childPlanOfServiceDto.getChildPlanFmlyTeamPrtctpnDto().getChildPlanPartcptDevDtoList())) {
					 /*  ALM defect 15362 :  From the list of child plan participants, check if there exists a record that matches the child's person id.
					  *  if yes, use that record to create child's date of participation and date copy given book marks. If multiple records exist , use the first record.
					  *  All other records are used to create participant book marks. If no record exists that matches child's person id, check if there
					  *  exists a record with relationship as 'Self' and matches the name with child's name . If yes that record will be used to create child's book mark.
					  *  If multiple records exist , then use the first record. All other records are used to create participant book marks.
			    	  */
				    List<ChildPlanParticipDto> familyTeamParticipationList = new ArrayList<ChildPlanParticipDto>();
					ChildPlanParticipDto childPlanDto = null;
					boolean isChildBookMarked = false;
					for (ChildPlanParticipDto childPlanParticipDto : childPlanOfServiceDto
							.getChildPlanFmlyTeamPrtctpnDto().getChildPlanPartcptDevDtoList()) {

						// Warranty Defect - 11242 - Added Null Pointer Check which caused the Child Plan Launch Fail
						if(!ObjectUtils.isEmpty(childPlanParticipDto.getNmCspPartFull())&&!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanInformationDto().getNmChild())) {
   						   if (!ObjectUtils.isEmpty(childPlanParticipDto.getIdPerson()) &&
									childPlanParticipDto.getIdPerson().equals(childPlanOfServiceDto.getChildPlanInformationDto().getIdPerson()) && !isChildBookMarked) {
							   childPlanDto = childPlanParticipDto;
							   isChildBookMarked = true;
							}else{
							   familyTeamParticipationList.add(childPlanParticipDto);
						   }
					    }
					}
					if(childPlanDto!= null){
						createChildParticipation(bookmarkDtoList,childPlanDto);
						createFamilyTeamParticipationList(familyTeamParticipationList,formDataGroupTmplatParticptnList);
					}else{
						for (ChildPlanParticipDto childAndFamilyPlanParticipDto : familyTeamParticipationList) {
							if(!ObjectUtils.isEmpty(childAndFamilyPlanParticipDto.getNmCspPartFull())
									&&!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanInformationDto().getNmChild())
							        &&!ObjectUtils.isEmpty(childAndFamilyPlanParticipDto.getSdsCspPartRelationship())) {
								if (childAndFamilyPlanParticipDto.getNmCspPartFull()
										.equalsIgnoreCase(childPlanOfServiceDto.getChildPlanInformationDto().getNmChild())
								&& childAndFamilyPlanParticipDto.getSdsCspPartRelationship().equalsIgnoreCase(ServiceConstants.SELF_RELATIONSHIP)) {
									createChildParticipation(bookmarkDtoList,childAndFamilyPlanParticipDto);
									familyTeamParticipationList.remove(childAndFamilyPlanParticipDto);
									break;
								}
							}
						}
						createFamilyTeamParticipationList(familyTeamParticipationList,formDataGroupTmplatParticptnList);
						/* End of ALM defect 15362 */
					}

					formDataGroupDtoList.addAll(formDataGroupTmplatParticptnList);

				}
			}
		}
		// PERMANENCY
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto())) {
			BookmarkDto bookmarkChLegalStatus = createBookmarkWithCodesTable(BookmarkConstants.CHILD_LEGAL_STATUS,
					childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdChildLglStatus(), CodesConstant.CLEGSTAT);
			bookmarkDtoList.add(bookmarkChLegalStatus);

			BookmarkDto bookmarkTxtPermGoal = createBookmarkWithCodesTable(BookmarkConstants.DFPS_PRIM_PERM_GOAL,
					childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdCspPlanPermGoal(), CodesConstant.CCPPRMGL);
			bookmarkDtoList.add(bookmarkTxtPermGoal);

			BookmarkDto bookmarkDtTxtPermGoal = createBookmark(BookmarkConstants.DATE_PRIM_PERM_GOAL,
					DateUtils.stringDt(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtCspPermGoalTarget()));
			bookmarkDtoList.add(bookmarkDtTxtPermGoal);

			// [artf132297] Defect 13543 - keeping value as null/blank if no selection was made instead of defaulting to "None"
			if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdConcurrentGoal())
					&& !childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdConcurrentGoal()
							.equals(FormConstants.STR_ZERO)) {
				BookmarkDto bookmarkDFPSConcPermGoal = createBookmarkWithCodesTable(
						BookmarkConstants.DFPS_CONC_PERM_GOAL,
						childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdConcurrentGoal(),
						CodesConstant.CCPPRMGL);
				bookmarkDtoList.add(bookmarkDFPSConcPermGoal);

				FormDataGroupDto formDataGrpDtConcPermGoal = createFormDataGroup(
						FormGroupsConstants.TMPLAT_DATE_CONC_PERM_GOAL, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkDtConcPermGoalList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtConcPermGoal = createBookmark(BookmarkConstants.DATE_CONC_PERM_GOAL, DateUtils
						.stringDt(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getDtCspCncrntPermGoal()));
				bookmarkDtConcPermGoalList.add(bookmarkDtConcPermGoal);
				formDataGrpDtConcPermGoal.setBookmarkDtoList(bookmarkDtConcPermGoalList);
				formDataGroupDtoList.add(formDataGrpDtConcPermGoal);
			}

			if (FormConstants.STR_ZERO
					.equals(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getCdConcurrentGoal())) {
				FormDataGroupDto formDataGrpNoConcGoal = createFormDataGroup(FormGroupsConstants.TMPLAT_NO_CONC_GOAL,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkNoConcGoalList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNoConcGoal = createBookmark(BookmarkConstants.NO_CONC_GOAL,
						formatTextValue(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getTxtNoConGoals()));
				bookmarkNoConcGoalList.add(bookmarkNoConcGoal);
				formDataGrpNoConcGoal.setBookmarkDtoList(bookmarkNoConcGoalList);
				formDataGroupDtoList.add(formDataGrpNoConcGoal);

			}

			BookmarkDto bookmarkExplPermGoal = createBookmark(BookmarkConstants.EXPL_PERM_GOAL,
					formatTextValue(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getTxtPermGoal()));
			bookmarkDtoList.add(bookmarkExplPermGoal);

			BookmarkDto bookmarkChLenOfStay = createBookmark(BookmarkConstants.EST_LENG_PLACEMENT,
					formatTextValue(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getTxtCspLengthOfStay()));
			bookmarkDtoList.add(bookmarkChLenOfStay);

			BookmarkDto bookmarkLosDiscrepncy = createBookmark(BookmarkConstants.GOAL_LENG_PLACEMENT,
					formatTextValue(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getTxtCspLosDiscrepancy()));
			bookmarkDtoList.add(bookmarkLosDiscrepncy);

			BookmarkDto bookmarkTxtProgrssByCrGivr = createBookmark(BookmarkConstants.CAREGIVER_PERM_GOAL,
					formatTextValue(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getTxtProgrssByCrgvr()));
			bookmarkDtoList.add(bookmarkTxtProgrssByCrGivr);

			BookmarkDto bookmarkDFPSPermGoal = createBookmark(BookmarkConstants.DFPS_PERM_GOAL,
					formatTextValue(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getTxtProgrssByDfps()));
			bookmarkDtoList.add(bookmarkDFPSPermGoal);

			BookmarkDto bookmarkDtScheduled = createBookmark(BookmarkConstants.DATES_SCHEDULED,
					formatTextValue(childPlanOfServiceDto.getChildPlanOfServiceDtlDto().getTxtDtSchedl()));
			bookmarkDtoList.add(bookmarkDtScheduled);
		}

		//QRTP
		if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto())
				&& !ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndCurrentQrtp())) {
			ChildPlanQrtpPrmnncyMeetingDto cpQrtpPrmnncyMeetingDto = childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto();

			if (ServiceConstants.N.equalsIgnoreCase(cpQrtpPrmnncyMeetingDto.getIndCurrentQrtp())) {
				BookmarkDto bookmarkQrtpIndicator = createBookmark(BookmarkConstants.QRTP_IND,
						childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndCurrentQrtp()
								.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT : ServiceConstants.NO_TEXT);
				bookmarkDtoList.add(bookmarkQrtpIndicator);
			} else {
				BookmarkDto bookmarkQrtpIndicator = createBookmark(BookmarkConstants.QRTP_IND,
						childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndCurrentQrtp()
								.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT : ServiceConstants.NO_TEXT);
				bookmarkDtoList.add(bookmarkQrtpIndicator);

				FormDataGroupDto formDataQrtpIndicator = createFormDataGroup(FormGroupsConstants.TMPLAT_QRTP_IND_YES,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkQrtpList = new ArrayList<>();
				List<FormDataGroupDto> formDataGroupQrtpList = new ArrayList<>();

				if(!(ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getDtPtmQrtp()) &&
						ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtFcltatorName()) &&
						ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtMtngLocation()) &&
						ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getFacilitatorTitle()))) {

					FormDataGroupDto formDataGroupMeetingInfo= createFormDataGroup(FormGroupsConstants.TMPLAT_QRTP_MEETING_INFO,
							FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGroupMeetingInfo);

					List<FormDataGroupDto> formDataGroupMeetingInfoList = new ArrayList<>();

					if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getDtPtmQrtp())) {
						FormDataGroupDto formDataGroupTmplatQrtpPtmDate = createFormDataGroup(
								FormGroupsConstants.TMPLAT_QRTP_PTM_DATE, FormGroupsConstants.TMPLAT_QRTP_MEETING_INFO);
						formDataGroupMeetingInfoList.add(formDataGroupTmplatQrtpPtmDate);

						List<BookmarkDto> ptmDateBookMarkList = new ArrayList<>();
						BookmarkDto bookmarkPtmDate = createBookmark(BookmarkConstants.QRTP_PTM_DATE,
								DateUtils.stringDt(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getDtPtmQrtp()));
						ptmDateBookMarkList.add(bookmarkPtmDate);
						formDataGroupTmplatQrtpPtmDate.setBookmarkDtoList(ptmDateBookMarkList);
					}

					if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtFcltatorName())) {
						FormDataGroupDto formDataGroupTmplatFacilitator = createFormDataGroup(
								FormGroupsConstants.TMPLAT_QRTP_FACILITATOR_NM, FormGroupsConstants.TMPLAT_QRTP_MEETING_INFO);
						formDataGroupMeetingInfoList.add(formDataGroupTmplatFacilitator);

						List<BookmarkDto> facilitatorNmBookMarkList = new ArrayList<>();
						BookmarkDto bookmarkNmMeetingFacilitator = createBookmark(BookmarkConstants.NM_MEETING_FACILITATOR,
								childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtFcltatorName());
						facilitatorNmBookMarkList.add(bookmarkNmMeetingFacilitator);
						formDataGroupTmplatFacilitator.setBookmarkDtoList(facilitatorNmBookMarkList);
					}

					if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtMtngLocation())) {
						FormDataGroupDto formDataGroupTmplatMeetingLoc = createFormDataGroup(
								FormGroupsConstants.TMPLAT_QRTP_MEETING_LOCATION, FormGroupsConstants.TMPLAT_QRTP_MEETING_INFO);
						formDataGroupMeetingInfoList.add(formDataGroupTmplatMeetingLoc);

						List<BookmarkDto> meetingLocationBookMarkList = new ArrayList<>();
						BookmarkDto bookmarkMeetingHeld = createBookmarkWithCodesTable(BookmarkConstants.MEETING_HELD,
								childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtMtngLocation(), CodesConstant.CCQRTPLM);
						meetingLocationBookMarkList.add(bookmarkMeetingHeld);
						formDataGroupTmplatMeetingLoc.setBookmarkDtoList(meetingLocationBookMarkList);
					}

					if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getFacilitatorTitle())) {
						FormDataGroupDto formDataGroupTmplatFacilitatorTitle = createFormDataGroup(
								FormGroupsConstants.TMPLAT_QRTP_FACILITATOR_TITLE, FormGroupsConstants.TMPLAT_QRTP_MEETING_INFO);
						formDataGroupMeetingInfoList.add(formDataGroupTmplatFacilitatorTitle);

						List<BookmarkDto> facilitatorTitleBookMarkList = new ArrayList<>();
						BookmarkDto bookmarkFacilitatorTitle = createBookmark(BookmarkConstants.TITLE_MEETING_FACILITATOR,
								childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getFacilitatorTitle());
						facilitatorTitleBookMarkList.add(bookmarkFacilitatorTitle);
						formDataGroupTmplatFacilitatorTitle.setBookmarkDtoList(facilitatorTitleBookMarkList);
					}
					formDataGroupMeetingInfo.setFormDataGroupList(formDataGroupMeetingInfoList);
				}

				//participants
				List<ChildPlanQrtpPtmParticipDto> ptmParticipationList = childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getChildPlanQrtpPtmParticipDtoList();
				if(!CollectionUtils.isEmpty(ptmParticipationList)) {
					FormDataGroupDto formDataGroupTmplatQrtpParticptnTitle = createFormDataGroup(
							FormGroupsConstants.TMPLAT_QRTP_PARTICIPANT_TITLE, FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGroupTmplatQrtpParticptnTitle);

					for (ChildPlanQrtpPtmParticipDto childPlanParticipDto : ptmParticipationList) {
						FormDataGroupDto formDataGroupTmplatQrtpParticptn = createFormDataGroup(
								FormGroupsConstants.TMPLAT_QRTP_PARTICIPANT, FormGroupsConstants.TMPLAT_QRTP_IND_YES);
						formDataGroupQrtpList.add(formDataGroupTmplatQrtpParticptn);
						List<FormDataGroupDto> formDataGroupParticipList = new ArrayList<>();

						List<BookmarkDto> bookmarkPartcptnList = new ArrayList<>();

						if (!ObjectUtils.isEmpty(childPlanParticipDto.getNmQrtpPartFull())) {
							BookmarkDto bookmarkDtoTeamParName = createBookmark(BookmarkConstants.PTM_PARTICIPANT_NAME,
									formatTextValue(childPlanParticipDto.getNmQrtpPartFull()));
							bookmarkPartcptnList.add(bookmarkDtoTeamParName);
						}

						if (!ObjectUtils.isEmpty(childPlanParticipDto.getSdsQrtpPartRelationship())) {
							BookmarkDto bookmarkDtoRelInt = createBookmark(BookmarkConstants.PTM_REL_INT,
									formatTextValue(childPlanParticipDto.getSdsQrtpPartRelationship()));
							bookmarkPartcptnList.add(bookmarkDtoRelInt);
						}

						if (!ObjectUtils.isEmpty(childPlanParticipDto.getTxtReltnshpOthr())) {
							FormDataGroupDto formDataGroupTmplatRelOthr = createFormDataGroup(
									FormGroupsConstants.TMPLAT_QRTP_REL_OTHER, FormGroupsConstants.TMPLAT_QRTP_PARTICIPANT);
							formDataGroupParticipList.add(formDataGroupTmplatRelOthr);
							BookmarkDto bookmarkDtoRelIfOther = createBookmark(BookmarkConstants.PTM_REL_OTHER,
									formatTextValue(childPlanParticipDto.getTxtReltnshpOthr()));
							bookmarkPartcptnList.add(bookmarkDtoRelIfOther);
							formDataGroupTmplatRelOthr.setBookmarkDtoList(bookmarkPartcptnList);
						}

						if (!ObjectUtils.isEmpty(childPlanParticipDto.getCdQrtpPartNotifType())) {
							FormDataGroupDto formDataGroupTmplatNotifType = createFormDataGroup(
									FormGroupsConstants.TMPLAT_QRTP_NOTIF_TYPE, FormGroupsConstants.TMPLAT_QRTP_PARTICIPANT);
							formDataGroupParticipList.add(formDataGroupTmplatNotifType);
							List<BookmarkDto> bookmarkNotifTypeList = new ArrayList<>();

							BookmarkDto bookmarkDtoNotifType = createBookmark(BookmarkConstants.PTM_NOTIF_TYPE,
									formatTextValue(childPlanParticipDto.getCdQrtpPartNotifType().equalsIgnoreCase("ver") ? ServiceConstants.VERBAL_TEXT : ServiceConstants.WRITTEN_TEXT));
							bookmarkNotifTypeList.add(bookmarkDtoNotifType);
							formDataGroupTmplatNotifType.setBookmarkDtoList(bookmarkNotifTypeList);
						}

						if (!ObjectUtils.isEmpty(childPlanParticipDto.getCdAttendance())) {
							FormDataGroupDto formDataGroupTmplatAttendance = createFormDataGroup(
									FormGroupsConstants.TMPLAT_QRTP_ATTENDANCE, FormGroupsConstants.TMPLAT_QRTP_PARTICIPANT);
							formDataGroupParticipList.add(formDataGroupTmplatAttendance);
							List<BookmarkDto> bookmarkAttendanceList = new ArrayList<>();

							BookmarkDto bookmarkDtoAttendance = createBookmarkWithCodesTable(BookmarkConstants.PTM_ATTENDANCE,
									childPlanParticipDto.getCdAttendance(), CodesConstant.CCQRTPAT);
							bookmarkAttendanceList.add(bookmarkDtoAttendance);
							formDataGroupTmplatAttendance.setBookmarkDtoList(bookmarkAttendanceList);
						}

						if (!ObjectUtils.isEmpty(childPlanParticipDto.getDtQrtpPartParticipate())) {
							FormDataGroupDto formDataGroupTmplatDate = createFormDataGroup(
									FormGroupsConstants.TMPLAT_QRTP_PARTICIP_DATE, FormGroupsConstants.TMPLAT_QRTP_PARTICIPANT);
							formDataGroupParticipList.add(formDataGroupTmplatDate);
							List<BookmarkDto> bookmarkDateList = new ArrayList<>();

							BookmarkDto bookmarkDtoChiParDate = createBookmark(
									BookmarkConstants.DATE_PTM_PARTICIPATION,
									DateUtils.stringDt(childPlanParticipDto.getDtQrtpPartParticipate()));
							bookmarkDateList.add(bookmarkDtoChiParDate);
							formDataGroupTmplatDate.setBookmarkDtoList(bookmarkDateList);
						}
						formDataGroupTmplatQrtpParticptn.setBookmarkDtoList(bookmarkPartcptnList);
						formDataGroupTmplatQrtpParticptn.setFormDataGroupList(formDataGroupParticipList);
					}
				}

                //notification to all participants
				if(!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndNotifAgrmntFam())){
					FormDataGroupDto formDataGroupNotifAgrmntFam = createFormDataGroup(FormGroupsConstants.TMPLAT_NOTIF_ALL_PARTICIP,
							FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGroupNotifAgrmntFam);

					List<BookmarkDto> bookmarkNotifAgrmntList = new ArrayList<>();
					BookmarkDto bookmarkNotifAllParticipInd = createBookmark(BookmarkConstants.NOTIF_ALL_PARTICIPANTS,
							childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndNotifAgrmntFam()
									.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT : ServiceConstants.NO_TEXT);
					bookmarkNotifAgrmntList.add(bookmarkNotifAllParticipInd);
					formDataGroupNotifAgrmntFam.setBookmarkDtoList(bookmarkNotifAgrmntList);

					List<FormDataGroupDto> formDataGroupNotifAgrmntFamList = new ArrayList<>();
					if (ServiceConstants.N
							.equals(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndNotifAgrmntFam())) {
						FormDataGroupDto formDataGrpNotifAllParticip = createFormDataGroup(FormGroupsConstants.TMPLAT_NOTIF_ALL_PARTICIP_EXPLAIN,
								FormGroupsConstants.TMPLAT_NOTIF_ALL_PARTICIP);
						formDataGroupNotifAgrmntFamList.add(formDataGrpNotifAllParticip);

						List<BookmarkDto> bookmarkNotifExplainList = new ArrayList<>();
						BookmarkDto bookmarkNotifExplain = createBookmark(BookmarkConstants.NOTIF_ALL_PARTICIPANTS_EXP,
								formatTextValue(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtNotifAgrmntFam()));
						bookmarkNotifExplainList.add(bookmarkNotifExplain);
						formDataGrpNotifAllParticip.setBookmarkDtoList(bookmarkNotifExplainList);

						formDataGroupNotifAgrmntFam.setFormDataGroupList(formDataGroupNotifAgrmntFamList);
					}
				}

				//child 14 yrs or older
				if(!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndConsultRegMeetngParticip())){
					FormDataGroupDto formDataGroupConsultRegMeetng = createFormDataGroup(FormGroupsConstants.TMPLAT_CONSULT_REG_MEETING_PARTICIP,
							FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGroupConsultRegMeetng);

					List<BookmarkDto> bookmarkList = new ArrayList<>();
					BookmarkDto bookmarkIndDto = createBookmark(BookmarkConstants.CONSULT_REG_MEETING_PARTICIP,
							childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndConsultRegMeetngParticip()
									.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT : ServiceConstants.NO_TEXT);

					bookmarkList.add(bookmarkIndDto);
					formDataGroupConsultRegMeetng.setBookmarkDtoList(bookmarkList);

					List<FormDataGroupDto> formDataGroupConsultList = new ArrayList<>();
					if (ServiceConstants.N
							.equals(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndConsultRegMeetngParticip())) {
						FormDataGroupDto formDataGrpConsultRegMeetng = createFormDataGroup(FormGroupsConstants.TMPLAT_CONSULT_REG_MEETING_PARTICIP_EXPLAIN,
								FormGroupsConstants.TMPLAT_CONSULT_REG_MEETING_PARTICIP);
						formDataGroupConsultList.add(formDataGrpConsultRegMeetng);

						List<BookmarkDto> bookmarkExplainList = new ArrayList<>();
						BookmarkDto bookmarkExplain = createBookmark(BookmarkConstants.CONSULT_REG_MEETING_PARTICIP_EXP,
								formatTextValue(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtConsultRegMeetngParticip()));
						bookmarkExplainList.add(bookmarkExplain);
						formDataGrpConsultRegMeetng.setBookmarkDtoList(bookmarkExplainList);
						formDataGroupConsultRegMeetng.setFormDataGroupList(formDataGroupConsultList);
					}
				}

				//EFFORTS FOR CHILD SUPPORT
				if(!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndEffortsChldSpprt())){
					FormDataGroupDto formDataGroupEffortChildSupport = createFormDataGroup(FormGroupsConstants.TMPLAT_EFFORTS_CHILD_SUPPORT,
							FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGroupEffortChildSupport);

					List<BookmarkDto> bookmarkChildSupportList = new ArrayList<>();
					BookmarkDto bookmarkChildSupportDto = createBookmark(BookmarkConstants.EFFORTS_CHILD_SUPPORT,
							childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndEffortsChldSpprt()
									.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT : ServiceConstants.NO_TEXT);
					bookmarkChildSupportList.add(bookmarkChildSupportDto);
					formDataGroupEffortChildSupport.setBookmarkDtoList(bookmarkChildSupportList);

					List<FormDataGroupDto> formDataGroupSupportList = new ArrayList<>();
					if (ServiceConstants.N
							.equals(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndEffortsChldSpprt())) {
						FormDataGroupDto formDataGrpChildSupport = createFormDataGroup(FormGroupsConstants.TMPLAT_EFFORTS_CHILD_SUPPORT_EXPLAIN,
								FormGroupsConstants.TMPLAT_EFFORTS_CHILD_SUPPORT);
						formDataGroupSupportList.add(formDataGrpChildSupport);

						List<BookmarkDto> bookmarkChildSupportExplainList = new ArrayList<>();
						BookmarkDto bookmarkChildSupportExplain = createBookmark(BookmarkConstants.EFFORTS_CHILD_SUPPORT_EXP,
								formatTextValue(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtEffortsChldSpprt()));
						bookmarkChildSupportExplainList.add(bookmarkChildSupportExplain);
						formDataGrpChildSupport.setBookmarkDtoList(bookmarkChildSupportExplainList);

						formDataGroupEffortChildSupport.setFormDataGroupList(formDataGroupSupportList);
					}
				}

				//family reunion
				if(!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndFmlyReunion())){
					FormDataGroupDto formDataGrpFamilyReunion = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_REUNION,
							FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGrpFamilyReunion);

					List<BookmarkDto> bookmarkFmlyReunionList = new ArrayList<>();
					BookmarkDto bookmarkReunionDto = createBookmark(BookmarkConstants.FAMILY_REUNION,
							childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndFmlyReunion()
									.equals(ServiceConstants.Y) ? ServiceConstants.YES_TEXT : ServiceConstants.NO_TEXT);
					bookmarkFmlyReunionList.add(bookmarkReunionDto);
					formDataGrpFamilyReunion.setBookmarkDtoList(bookmarkFmlyReunionList);

					List<FormDataGroupDto> formDataGroupReunionList = new ArrayList<>();
					if (ServiceConstants.N
							.equals(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getIndFmlyReunion())) {
						FormDataGroupDto formDataGrpFmlyReunionExplain = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_REUNION_EXPLAIN,
								FormGroupsConstants.TMPLAT_FAMILY_REUNION);
						formDataGroupReunionList.add(formDataGrpFmlyReunionExplain);

						List<BookmarkDto> bookmarkFmlyReunionExpList = new ArrayList<>();
						BookmarkDto bookmarkFmlyReunionExplain = createBookmark(BookmarkConstants.FAMILY_REUNION_EXP,
								formatTextValue(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtFamReunionNoInvlvmt()));
						bookmarkFmlyReunionExpList.add(bookmarkFmlyReunionExplain);
						formDataGrpFmlyReunionExplain.setBookmarkDtoList(bookmarkFmlyReunionExpList);

						formDataGrpFamilyReunion.setFormDataGroupList(formDataGroupReunionList);

					}
				}

				//sibling discussion
				if(!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtSblngDiscsn())){
					FormDataGroupDto formDataGrpSiblingDiscussion = createFormDataGroup(FormGroupsConstants.TMPLAT_SIBLING_DISCUSSION,
							FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGrpSiblingDiscussion);

					List<BookmarkDto> bookmarkSiblingDiscussionList = new ArrayList<>();
					BookmarkDto bookmarkSiblingDiscDto = createBookmark(BookmarkConstants.SIBLING_DISCUSSION,
							formatTextValue(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtSblngDiscsn()));
					bookmarkSiblingDiscussionList.add(bookmarkSiblingDiscDto);
					formDataGrpSiblingDiscussion.setBookmarkDtoList(bookmarkSiblingDiscussionList);
				}

				//SHORT term goal discussion
				if(!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtShrtTermGoalDisc())){
					FormDataGroupDto formDataGrpShortTermDiscussion = createFormDataGroup(FormGroupsConstants.TMPLAT_SHORT_TERM_GOAL_DISCUSSION,
							FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGrpShortTermDiscussion);

					List<BookmarkDto> bookmarkShortTermDiscList = new ArrayList<>();
					BookmarkDto bookmarkShortTermDiscDto = createBookmark(BookmarkConstants.SHORT_TERM_GOAL_DISCUSSION,
							formatTextValue(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtShrtTermGoalDisc()));
					bookmarkShortTermDiscList.add(bookmarkShortTermDiscDto);
					formDataGrpShortTermDiscussion.setBookmarkDtoList(bookmarkShortTermDiscList);
				}

				//SHORT term goal established
				if(!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtShrtTermGoalEstablishd())){
					FormDataGroupDto formDataGrpShortTermEstd = createFormDataGroup(FormGroupsConstants.TMPLAT_SHORT_TERM_GOAL_ESTD,
							FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGrpShortTermEstd);

					List<BookmarkDto> bookmarkShortTermEstdList = new ArrayList<>();
					BookmarkDto bookmarkShortTermEstdDto = createBookmark(BookmarkConstants.SHORT_TERM_GOAL_ESTD,
							formatTextValue(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtShrtTermGoalEstablishd()));
					bookmarkShortTermEstdList.add(bookmarkShortTermEstdDto);
					formDataGrpShortTermEstd.setBookmarkDtoList(bookmarkShortTermEstdList);
				}

				//long term goal discussion
				if(!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtLongTermGoalDisc())){
					FormDataGroupDto formDataGrpLongTermDiscussion = createFormDataGroup(FormGroupsConstants.TMPLAT_LONG_TERM_GOAL_DISCUSSION,
							FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGrpLongTermDiscussion);

					List<BookmarkDto> bookmarkLongTermDiscList = new ArrayList<>();
					BookmarkDto bookmarkLongTermDto = createBookmark(BookmarkConstants.LONG_TERM_GOAL_DISCUSSION,
							formatTextValue(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtLongTermGoalDisc()));
					bookmarkLongTermDiscList.add(bookmarkLongTermDto);
					formDataGrpLongTermDiscussion.setBookmarkDtoList(bookmarkLongTermDiscList);
				}
				//long term goal established
				if (!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtLongTermGoalEstablishd())) {
					FormDataGroupDto formDataGrpLongTermEstd = createFormDataGroup(FormGroupsConstants.TMPLAT_LONG_TERM_GOAL_ESTD,
							FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGrpLongTermEstd);

					List<BookmarkDto> bookmarkLongTermGoalEstdList = new ArrayList<>();
					BookmarkDto bookmarkLongTermEstdDto = createBookmark(BookmarkConstants.LONG_TERM_GOAL_ESTD,
							formatTextValue(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtLongTermGoalEstablishd()));
					bookmarkLongTermGoalEstdList.add(bookmarkLongTermEstdDto);
					formDataGrpLongTermEstd.setBookmarkDtoList(bookmarkLongTermGoalEstdList);
				}

				//assessment Recommendations
				if(!ObjectUtils.isEmpty(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtAssesmntRecmndatns())){
					FormDataGroupDto formDataGrpAssessmentRecommendations = createFormDataGroup(FormGroupsConstants.TMPLAT_ASSESSEMENT_RECOMMEDATIONS,
							FormGroupsConstants.TMPLAT_QRTP_IND_YES);
					formDataGroupQrtpList.add(formDataGrpAssessmentRecommendations);

					List<BookmarkDto> bookmarkRecommendationList = new ArrayList<>();
					BookmarkDto bookmarkRecommendDto = createBookmark(BookmarkConstants.ASSESSEMENT_RECOMMEDATIONS,
							formatTextValue(childPlanOfServiceDto.getChildPlanQrtpPrmnncyMeetingDto().getTxtAssesmntRecmndatns()));
					bookmarkRecommendationList.add(bookmarkRecommendDto);
					formDataGrpAssessmentRecommendations.setBookmarkDtoList(bookmarkRecommendationList);
				}


				formDataQrtpIndicator.setBookmarkDtoList(bookmarkQrtpList);
				formDataQrtpIndicator.setFormDataGroupList(formDataGroupQrtpList);
				formDataGroupDtoList.add(formDataQrtpIndicator);
			}
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupDtoList);
		preFillData.setBookmarkDtoList(bookmarkDtoList);
		return preFillData;
	}

	/**
	 * ALM defect 15362 - create family team participation book mark
	 * @param familyTeamParticipationList
	 * @param formDataGroupTmplatParticptnList
	 */
	private void createFamilyTeamParticipationList(List<ChildPlanParticipDto> familyTeamParticipationList, List<FormDataGroupDto> formDataGroupTmplatParticptnList){

		for (ChildPlanParticipDto childPlanParticipDto : familyTeamParticipationList) {
			FormDataGroupDto formDataGroupTmplatMotherParticptn = createFormDataGroup(
					FormGroupsConstants.TMPLAT_PARTICIPANT, FormConstants.EMPTY_STRING);

			List<BookmarkDto> bookmarkPartcptnist = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDtoTeamParName = createBookmark(BookmarkConstants.TEAM_PARTICIPANT,
					formatTextValue(childPlanParticipDto.getNmCspPartFull()));
			bookmarkPartcptnist.add(bookmarkDtoTeamParName);

			BookmarkDto bookmarkDtoChiParDate = createBookmark(
					BookmarkConstants.DATE_TEAM_PARTICIPATION,
					DateUtils.stringDt(childPlanParticipDto.getDtCspPartParticipate()));
			bookmarkPartcptnist.add(bookmarkDtoChiParDate);
			//Fix for defect 13530 - INC000004934701 - R2-Error launching CPOS form
			if (!ObjectUtils.isEmpty(childPlanParticipDto.getDtCspPartCopyGiven())) {
				BookmarkDto bookmarkDtoCopyParCopyGiven = createBookmark(
						BookmarkConstants.DATE_TEAM_COPY_PROVIDE,
						DateUtils.stringDt(childPlanParticipDto.getDtCspPartCopyGiven()));

				bookmarkPartcptnist.add(bookmarkDtoCopyParCopyGiven);
			}
			formDataGroupTmplatMotherParticptn.setBookmarkDtoList(bookmarkPartcptnist);
			formDataGroupTmplatParticptnList.add(formDataGroupTmplatMotherParticptn);
		}

	}

	/**
	 * ALM defect 15362 - create child participation book mark
	 * @param bookmarkDtoList
	 * @param childPlanParticipDto
	 */
	private void createChildParticipation(List<BookmarkDto> bookmarkDtoList,ChildPlanParticipDto childPlanParticipDto){
		BookmarkDto bookmarkDtoDateChildParticip = createBookmark(
				BookmarkConstants.DATE_CHILD_PARTICIPATION,
				DateUtils.stringDt(childPlanParticipDto.getDtCspPartParticipate()));
		bookmarkDtoList.add(bookmarkDtoDateChildParticip);

		BookmarkDto bookmarkDtoDateChildCopyProvided = createBookmark(
				BookmarkConstants.DATE_CHILD_COPY_PROVIDE,
				DateUtils.stringDt(childPlanParticipDto.getDtCspPartCopyGiven()));
		bookmarkDtoList.add(bookmarkDtoDateChildCopyProvided);
	}

	
	// Warranty Defect Fix - 12090 - To Replace the Carriage Return /Line Feed with Break Tag
		public String formatTextValue(String txtToFormat) {		
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

		public String mapRecentScore(String rsVal){
			String recentScore="";
			if(FormConstants.RECNT_SCR_01.equals(rsVal)){
				recentScore=FormConstants.RECNT_SCR_VAL_01;
			}else if(FormConstants.RECNT_SCR_02.equals(rsVal)){
				recentScore=FormConstants.RECNT_SCR_VAL_02;
			}else if(FormConstants.RECNT_SCR_03.equals(rsVal)){
				recentScore=FormConstants.RECNT_SCR_VAL_03;
			}
		return recentScore;
		}

}
