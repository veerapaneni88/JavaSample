package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import us.tx.state.dfps.approval.dto.ApprovalFormDataDto;
import us.tx.state.dfps.approval.dto.ApprovalFormDto;
import us.tx.state.dfps.approval.dto.ApproverJobHistoryDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.ApprovalRejectionPersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Creates
 * prefill data object to populate form Mar 14, 2018- 2:32:24 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Component
public class ApprovalFormPrefillData extends DocumentServiceUtil {

	/**
	 * Method Name: returnPrefillData Method Description: Creates bookmarks and
	 * groups for form
	 * 
	 * @param parentDtoobj
	 * @return PreFillDataServiceDto
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		ApprovalFormDto approvalFormDto = (ApprovalFormDto) parentDtoobj;
		if (null == approvalFormDto.getApprovalFormDataDto()) {
			approvalFormDto.setApprovalFormDataDto(new ApprovalFormDataDto());
		}
		if (null == approvalFormDto.getApprovalRejectionPersonDto()) {
			approvalFormDto.setApprovalRejectionPersonDto(new ApprovalRejectionPersonDto());
		}
		if (null == approvalFormDto.getApproverList()) {
			approvalFormDto.setApproverList(new ArrayList<ApproverJobHistoryDto>());
		}
		if (null == approvalFormDto.getEventDetailsList()) {
			approvalFormDto.setEventDetailsList(new ArrayList<EventDto>());
		}
		if (null == approvalFormDto.getEventList()) {
			approvalFormDto.setEventList(new ArrayList<EventDto>());
		}
		if (null == approvalFormDto.getStageRtrvOutDto()) {
			approvalFormDto.setStageRtrvOutDto(new StageRtrvOutDto());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// parent group cfzco00
		if (StringUtils.isNotBlank(approvalFormDto.getApprovalFormDataDto().getNmSuffix())) {
			FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(commaGroupDto);
		}

		// parent group cfa11o01
		for (EventDto eventDto : approvalFormDto.getEventList()) {
			FormDataGroupDto eventDescGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EVENT_DESC,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkEventDescList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkEventDesc = createBookmark(BookmarkConstants.EVENT_DESCP,
					eventDto.getEventDescription());
			bookmarkEventDescList.add(bookmarkEventDesc);
			eventDescGroupDto.setBookmarkDtoList(bookmarkEventDescList);
			formDataGroupList.add(eventDescGroupDto);
		}

		// parent group cfa11o02
		for (ApproverJobHistoryDto approverDto : approvalFormDto.getApproverList()) {
			FormDataGroupDto approverGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_APVD_BY,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> approverGroupDtoList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkApproverList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDateApvdRejc = createBookmark(BookmarkConstants.DATE_APVD_REJC,
					DateUtils.stringDt(approverDto.getDtApproversDetermination()));
			BookmarkDto bookmarkDateSubm = createBookmark(BookmarkConstants.DATE_SUBM,
					DateUtils.stringDt(approverDto.getDtApproversRequested()));
			BookmarkDto bookmarkApvlStat = createBookmarkWithCodesTable(BookmarkConstants.APVL_STAT,
					approverDto.getCdApproversStatus(), CodesConstant.CAPPDESG);
			BookmarkDto bookmarkApvrTitle = createBookmark(BookmarkConstants.APVR_TITLE, approverDto.getDecode());
			BookmarkDto bookmarkNmSuffix = createBookmarkWithCodesTable(BookmarkConstants.NAME_SUFFIX,
					approverDto.getNmSuffix(), CodesConstant.CSUFFIX2);
			BookmarkDto bookmarkNmFirst = createBookmark(BookmarkConstants.NAME_FIRST, approverDto.getNmFirst());
			BookmarkDto bookmarkNmLast = createBookmark(BookmarkConstants.NAME_LAST, approverDto.getNmLast());
			BookmarkDto bookmarkNmMiddle = createBookmark(BookmarkConstants.NAME_MIDDLE, approverDto.getNmMiddle());
			bookmarkApproverList.add(bookmarkDateApvdRejc);
			bookmarkApproverList.add(bookmarkDateSubm);
			bookmarkApproverList.add(bookmarkApvlStat);
			bookmarkApproverList.add(bookmarkApvrTitle);
			bookmarkApproverList.add(bookmarkNmSuffix);
			bookmarkApproverList.add(bookmarkNmFirst);
			bookmarkApproverList.add(bookmarkNmLast);
			bookmarkApproverList.add(bookmarkNmMiddle);
			approverGroupDto.setBookmarkDtoList(bookmarkApproverList);

			// sub group cfzco00
			if (StringUtils.isNotBlank(approverDto.getNmSuffix())) {
				FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormGroupsConstants.TMPLAT_APVD_BY);
				approverGroupDtoList.add(commaGroupDto);
			}

			// sub group cfa11o03
			if (ServiceConstants.EMP_SKILL_023.equals(approverDto.getCdEmpSkill())) {
				FormDataGroupDto apvrMswGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_APVR_MSW,
						FormGroupsConstants.TMPLAT_APVD_BY);
				approverGroupDtoList.add(apvrMswGroupDto);
			}

			approverGroupDto.setFormDataGroupList(approverGroupDtoList);
			formDataGroupList.add(approverGroupDto);
		}

		// parent group cfa11o04
		if (StringUtils.isNotBlank(approvalFormDto.getApprovalRejectionPersonDto().getApproversCmnts())) {
			FormDataGroupDto commentsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMENTS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkCommentsList = new ArrayList<BookmarkDto>();
			BookmarkDto BookmarkRejectionComments = createBookmark(BookmarkConstants.REJECTION_COMMENTS,
					approvalFormDto.getApprovalRejectionPersonDto().getApproversCmnts());
			bookmarkCommentsList.add(BookmarkRejectionComments);
			commentsGroupDto.setBookmarkDtoList(bookmarkCommentsList);
			formDataGroupList.add(commentsGroupDto);
		}

		// parent group cfa11o05
		if (ServiceConstants.Y.equals(approvalFormDto.getApprovalRejectionPersonDto().getIndApsEffort())) {
			FormDataGroupDto apsEffortGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_APS_EFFORT,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(apsEffortGroupDto);
		}

		// parent group cfa11o06
		FormDataGroupDto careEnteredGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CARE_ENTERED,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(careEnteredGroupDto);

		// parent group cfa11o07
		if (ServiceConstants.Y.equals(approvalFormDto.getApprovalRejectionPersonDto().getIndEvidence())) {
			FormDataGroupDto evidenceGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EVIDENCE,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> evidenceGroupDtoList = new ArrayList<FormDataGroupDto>();

			// sub group cfa1
			if (ServiceConstants.Y.equals(approvalFormDto.getApprovalRejectionPersonDto().getIndMissingEvidRptr())) {
				FormDataGroupDto meReporterGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_M_E_REPORTER,
						FormGroupsConstants.TMPLAT_EVIDENCE);
				evidenceGroupDtoList.add(meReporterGroupDto);
			}

			// sub group cfa2
			if (ServiceConstants.Y.equals(approvalFormDto.getApprovalRejectionPersonDto().getIndMissingEvidCol())) {
				FormDataGroupDto meCollateralGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_M_E_COLLATERAL,
						FormGroupsConstants.TMPLAT_EVIDENCE);
				evidenceGroupDtoList.add(meCollateralGroupDto);
			}

			// sub group cfa3
			if (ServiceConstants.Y.equals(approvalFormDto.getApprovalRejectionPersonDto().getIndMissingEvidAp())) {
				FormDataGroupDto meApGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_M_E_AP,
						FormGroupsConstants.TMPLAT_EVIDENCE);
				evidenceGroupDtoList.add(meApGroupDto);
			}

			// sub group cfa7
			if (ServiceConstants.Y.equals(approvalFormDto.getApprovalRejectionPersonDto().getIndMissingEvidOth())) {
				FormDataGroupDto meOtherGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_M_E_OTHER,
						FormGroupsConstants.TMPLAT_EVIDENCE);
				evidenceGroupDtoList.add(meOtherGroupDto);
			}

			// sub group cfa5
			if (ServiceConstants.Y.equals(approvalFormDto.getApprovalRejectionPersonDto().getIndMissingEvidMp())) {
				FormDataGroupDto meMedProfGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_M_E_MED_PROF,
						FormGroupsConstants.TMPLAT_EVIDENCE);
				evidenceGroupDtoList.add(meMedProfGroupDto);
			}

			// sub group cfa6
			if (ServiceConstants.Y.equals(approvalFormDto.getApprovalRejectionPersonDto().getIndMissingEvidDe())) {
				FormDataGroupDto meDocumentaryGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_M_E_DOCUMENTARY,
						FormGroupsConstants.TMPLAT_EVIDENCE);
				evidenceGroupDtoList.add(meDocumentaryGroupDto);
			}

			// sub group cfa4
			if (ServiceConstants.Y.equals(approvalFormDto.getApprovalRejectionPersonDto().getIndMissingEvidPhotos())) {
				FormDataGroupDto mePhotoGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_M_E_PHOTO,
						FormGroupsConstants.TMPLAT_EVIDENCE);
				evidenceGroupDtoList.add(mePhotoGroupDto);
			}

			evidenceGroupDto.setFormDataGroupList(evidenceGroupDtoList);
			formDataGroupList.add(evidenceGroupDto);
		}

		// parent group cfa11o08
		if (ServiceConstants.Y.equals(approvalFormDto.getApprovalRejectionPersonDto().getIndDiscretionary())) {
			FormDataGroupDto discretionaryGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISCRETIONARY,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(discretionaryGroupDto);
		}

		// group-less bookmarks
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		// cint21d
		BookmarkDto bookmarkStage = createBookmarkWithCodesTable(BookmarkConstants.STAGE,
				approvalFormDto.getStageRtrvOutDto().getCdStage(), CodesConstant.CSTAGES);
		bookmarkNonFormGrpList.add(bookmarkStage);
		BookmarkDto bookmarkStageType = createBookmarkWithCodesTable(BookmarkConstants.STAGE_TYPE,
				approvalFormDto.getStageRtrvOutDto().getCdStageType(), CodesConstant.CSTGTYPE);
		bookmarkNonFormGrpList.add(bookmarkStageType);
		BookmarkDto bookmarkStageName = createBookmark(BookmarkConstants.STAGE_NAME,
				approvalFormDto.getStageRtrvOutDto().getNmStage());
		bookmarkNonFormGrpList.add(bookmarkStageName);
		BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				approvalFormDto.getStageRtrvOutDto().getIdCase());
		bookmarkNonFormGrpList.add(bookmarkTitleCaseNumber);

		// cses04d
		BookmarkDto bookmarkWorkerSuffix = createBookmarkWithCodesTable(BookmarkConstants.WORKER_SUBM_SUFFIX,
				approvalFormDto.getApprovalFormDataDto().getNmSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFormGrpList.add(bookmarkWorkerSuffix);
		BookmarkDto bookmarkWorkerFirst = createBookmark(BookmarkConstants.WORKER_SUBM_FIRST,
				approvalFormDto.getApprovalFormDataDto().getNmFirst());
		bookmarkNonFormGrpList.add(bookmarkWorkerFirst);
		BookmarkDto bookmarkWorkerLast = createBookmark(BookmarkConstants.WORKER_SUBM_LAST,
				approvalFormDto.getApprovalFormDataDto().getNmLast());
		bookmarkNonFormGrpList.add(bookmarkWorkerLast);
		BookmarkDto bookmarkWorkerMiddle = createBookmark(BookmarkConstants.WORKER_SUBM_MIDDLE,
				approvalFormDto.getApprovalFormDataDto().getNmMiddle());
		bookmarkNonFormGrpList.add(bookmarkWorkerMiddle);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);
		return preFillData;
	}

}
