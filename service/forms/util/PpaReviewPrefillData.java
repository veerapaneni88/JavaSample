package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.conservatorship.dto.RemovalReasonDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PpaReviewDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.placement.dto.EventChildPlanDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Populates
 * prefill data for form CSC30O00 Jul 3, 2018- 10:45:38 AM © 2017 Texas
 * Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 04/19/2022 thompswa artf212430 : PPM74286 bookmark added for span/input default data.
 */
@Component
public class PpaReviewPrefillData extends DocumentServiceUtil {

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group bookmark
	 * Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		PpaReviewDto ppaReviewDto = (PpaReviewDto) parentDtoobj;

		if (ObjectUtils.isEmpty(ppaReviewDto.getLegalStatusPersonMaxStatusList())) {
			ppaReviewDto.setLegalStatusPersonMaxStatusList(new ArrayList<LegalStatusPersonMaxStatusDtOutDto>());
		}
		if (ObjectUtils.isEmpty(ppaReviewDto.getCaseInfoDtoList())) {
			ppaReviewDto.setCaseInfoDtoList(new ArrayList<CaseInfoDto>());
		}
		if (ObjectUtils.isEmpty(ppaReviewDto.getEventChildPlanDto())) {
			ppaReviewDto.setEventChildPlanDto(new EventChildPlanDto());
		}
		if (ObjectUtils.isEmpty(ppaReviewDto.getRemovalResonDtoList1())) {
			ppaReviewDto.setRemovalResonDtoList1(new ArrayList<RemovalReasonDto>());
		}
		if (ObjectUtils.isEmpty(ppaReviewDto.getRemovalResonDtoList2())) {
			ppaReviewDto.setRemovalResonDtoList2(new ArrayList<RemovalReasonDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// parent group cfzco00
		if (!ObjectUtils.isEmpty(ppaReviewDto.getStagePersonLinkCaseDto().getCdPersonSuffix())) {
			FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(commaGroupDto);
		}

		// parent group csc30o01
		for (CaseInfoDto caseInfoDto : ppaReviewDto.getCaseInfoDtoList()) {
			FormDataGroupDto caseGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.PRN_DOB,
					DateUtils.stringDt(caseInfoDto.getDtPersonBirth()));
			bookmarkPersonList.add(bookmarkDtPersonBirth);
			BookmarkDto bookmarkCdNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.PRN_NAME_SUFFIX,
					caseInfoDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
			bookmarkPersonList.add(bookmarkCdNameSuffix);
			BookmarkDto bookmarkRel = createBookmarkWithCodesTable(BookmarkConstants.PRN_RELATIONSHIP,
					caseInfoDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
			bookmarkPersonList.add(bookmarkRel);
			BookmarkDto bookmarkNmFirst = createBookmark(BookmarkConstants.PRN_NAME_FIRST,
					caseInfoDto.getNmNameFirst());
			bookmarkPersonList.add(bookmarkNmFirst);
			BookmarkDto bookmarkNmLast = createBookmark(BookmarkConstants.PRN_NAME_LAST, caseInfoDto.getNmNameLast());
			bookmarkPersonList.add(bookmarkNmLast);
			BookmarkDto bookmarkNmMiddle = createBookmark(BookmarkConstants.PRN_NAME_MIDDLE,
					caseInfoDto.getNmNameMiddle());
			bookmarkPersonList.add(bookmarkNmMiddle);

			caseGroupDto.setBookmarkDtoList(bookmarkPersonList);
			formDataGroupList.add(caseGroupDto);

			/*
			 * List<FormDataGroupDto> caseGroupList = new ArrayList<FormDataGroupDto>();
			 * caseGroupDto.setFormDataGroupList(caseGroupList);
			 * 
			 * //cfzco00 : sub group of csc30o01 FormDataGroupDto commaGroupDto =
			 * createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
			 * FormGroupsConstants.TMPLAT_PRN); caseGroupList.add(commaGroupDto);
			 */
		}

		// parent group csc30o02
		for (RemovalReasonDto removalReasonDto : ppaReviewDto.getRemovalResonDtoList1()) {
			FormDataGroupDto removalGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_INITIAL_REASON,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkCdRemovalReason = createBookmarkWithCodesTable(BookmarkConstants.REMOVAL_REASON,
					removalReasonDto.getCdRemovalReason(), CodesConstant.CREMFRHR);
			bookmarkPersonList.add(bookmarkCdRemovalReason);

			removalGroupDto.setBookmarkDtoList(bookmarkPersonList);
			formDataGroupList.add(removalGroupDto);
		}

		// parent group csc30o03
		for (RemovalReasonDto removalReasonDto : ppaReviewDto.getRemovalResonDtoList2()) {
			FormDataGroupDto removalGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MOST_RECENT_REASON,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkCdRemovalReason = createBookmarkWithCodesTable(BookmarkConstants.REMOVAL_REASON,
					removalReasonDto.getCdRemovalReason(), CodesConstant.CREMFRHR);
			bookmarkPersonList.add(bookmarkCdRemovalReason);

			removalGroupDto.setBookmarkDtoList(bookmarkPersonList);
			formDataGroupList.add(removalGroupDto);
		}

		// for bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		// CSEC15D StagePersonLinkCaseDto

		if (!ObjectUtils.isEmpty(ppaReviewDto.getStagePersonLinkCaseDto())) {
			BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.TITLE_CHILD_DOB,
					DateUtils.stringDt(ppaReviewDto.getStagePersonLinkCaseDto().getDtPersonBirth()));
			bookmarkNonFormGrpList.add(bookmarkDtPersonBirth);
			BookmarkDto bookmarkCdPersonSuffix = createBookmarkWithCodesTable(BookmarkConstants.TITLE_CHILD_NAME_SUFFIX,
					ppaReviewDto.getStagePersonLinkCaseDto().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
			bookmarkNonFormGrpList.add(bookmarkCdPersonSuffix);

			BookmarkDto bookmarkNmFirst = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_FIRST,
					ppaReviewDto.getStagePersonLinkCaseDto().getNmPersonFirst());
			bookmarkNonFormGrpList.add(bookmarkNmFirst);
			BookmarkDto bookmarkNmLast = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_LAST,
					ppaReviewDto.getStagePersonLinkCaseDto().getNmPersonLast());
			bookmarkNonFormGrpList.add(bookmarkNmLast);
			BookmarkDto bookmarkNmMiddle = createBookmark(BookmarkConstants.TITLE_CHILD_NAME_MIDDLE,
					ppaReviewDto.getStagePersonLinkCaseDto().getNmPersonMiddle());
			bookmarkNonFormGrpList.add(bookmarkNmMiddle);

			BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
					ppaReviewDto.getStagePersonLinkCaseDto().getNmCase());
			bookmarkNonFormGrpList.add(bookmarkNmCase);
			BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_ID,
					ppaReviewDto.getStagePersonLinkCaseDto().getIdCase());
			bookmarkNonFormGrpList.add(bookmarkIdCase);
		}

		// CSEC14D

		if (!ObjectUtils.isEmpty(ppaReviewDto.getpPtDetailsOutDtoAddress())) {
			BookmarkDto bookmarkDtPptDate = createBookmark(BookmarkConstants.DT_PPT_MEETING,
					DateUtils.stringDt(ppaReviewDto.getpPtDetailsOutDtoAddress().getDtPptDate()));
			bookmarkNonFormGrpList.add(bookmarkDtPptDate);
		}

		// CSEC19D

		if (!ObjectUtils.isEmpty(ppaReviewDto.getUnitDto())) {
			BookmarkDto bookmarkNbrUnit = createBookmark(BookmarkConstants.UNIT_NUMBER,
					ppaReviewDto.getUnitDto().getNbrUnit());
			bookmarkNonFormGrpList.add(bookmarkNbrUnit);
		}

		// CSEC32D

		if (!ObjectUtils.isEmpty(ppaReviewDto.getPlacementAUDDto())) {
			BookmarkDto bookmarkDtPlcmtStart = createBookmark(BookmarkConstants.CURR_PLCMNT_DATE,
					DateUtils.stringDt(ppaReviewDto.getPlacementAUDDto().getDtPlcmtStart()));
			bookmarkNonFormGrpList.add(bookmarkDtPlcmtStart);
			BookmarkDto bookmarkCdPlcmtLivArr = createBookmarkWithCodesTable(BookmarkConstants.CURR_PLCMNT_LIV_ARR,
					ppaReviewDto.getPlacementAUDDto().getCdPlcmtLivArr(), CodesConstant.CPLLAFRM);
			bookmarkNonFormGrpList.add(bookmarkCdPlcmtLivArr);
			BookmarkDto bookmarkNmPlcmt = createBookmark(BookmarkConstants.CURR_PLCMNT_NAME,
					ppaReviewDto.getPlacementAUDDto().getPlcmtFacil());
			bookmarkNonFormGrpList.add(bookmarkNmPlcmt);
		}

		// CSEC20D

		BookmarkDto bookmarkDtCspPermGoalTarget = createBookmark(BookmarkConstants.PERMANCY_GOAL_DATE,
				DateUtils.stringDt(ppaReviewDto.getEventChildPlanDto().getDtCspPermGoalTarget()));
		bookmarkNonFormGrpList.add(bookmarkDtCspPermGoalTarget);
		BookmarkDto bookmarkCdCspPlanPermGoal = createBookmarkWithCodesTable(BookmarkConstants.PERMANCY_GOAL,
				ppaReviewDto.getEventChildPlanDto().getCdCspPlanPermGoal(), CodesConstant.CCPPRMGL);
		bookmarkNonFormGrpList.add(bookmarkCdCspPlanPermGoal);

		// CSES32D
		//Fixed Warranty Defect#12004 Issue to fix duplicate bookmark creation 
		if(!ObjectUtils.isEmpty(ppaReviewDto.getLegalStatusPersonMaxStatusList())){
			LegalStatusPersonMaxStatusDtOutDto legalStatusPersonMaxStatusDtOutDto = ppaReviewDto.getLegalStatusPersonMaxStatusList().stream()
						.max(Comparator.comparing(LegalStatusPersonMaxStatusDtOutDto::getTsLastUpdate)).get();
			BookmarkDto bookmarkDtLegalStatStatusDt = createBookmark(BookmarkConstants.LEGAL_STATUS_DATE,
					DateUtils.stringDt(legalStatusPersonMaxStatusDtOutDto.getDtLegalStatStatusDt()));
			bookmarkNonFormGrpList.add(bookmarkDtLegalStatStatusDt);
			BookmarkDto bookmarkCdLegalStatStatus = createBookmarkWithCodesTable(BookmarkConstants.LEGAL_STATUS,
					legalStatusPersonMaxStatusDtOutDto.getCdLegalStatStatus(), CodesConstant.CLEGSTAT);
			bookmarkNonFormGrpList.add(bookmarkCdLegalStatStatus);
			}
		
		// CSEC33D
		if (!ObjectUtils.isEmpty(ppaReviewDto.getPersonLocOutDto())) {
			BookmarkDto bookmarkCdPlocChild = createBookmarkWithCodesTable(BookmarkConstants.CURR_PLCMNT_LCO,
					ppaReviewDto.getPersonLocOutDto().getCdPlocChild(), CodesConstant.CATHPLOC);
			bookmarkNonFormGrpList.add(bookmarkCdPlocChild);
		}

		// CDYN10D -1
		if (!ObjectUtils.isEmpty(ppaReviewDto.getCnsrvtrshpRemovalDto1())) {
			BookmarkDto bookmarkDtRemoval = createBookmark(BookmarkConstants.INITIAL_REMOVAL_DATE,
					DateUtils.stringDt(ppaReviewDto.getCnsrvtrshpRemovalDto1().getDtRemoval()));
			bookmarkNonFormGrpList.add(bookmarkDtRemoval);
		}

		// CDYN10D -2
		if (!ObjectUtils.isEmpty(ppaReviewDto.getCnsrvtrshpRemovalDto2())) {
			BookmarkDto bookmarkDtRemoval = createBookmark(BookmarkConstants.MOST_RECENT_REMOVAL_DATE,
					DateUtils.stringDt(ppaReviewDto.getCnsrvtrshpRemovalDto2().getDtRemoval()));
			bookmarkNonFormGrpList.add(bookmarkDtRemoval);
		}

		// CSEC31D
		if (!ObjectUtils.isEmpty(ppaReviewDto.getPptDetailsOutDto())) {
			BookmarkDto bookmarkDtPptDate = createBookmark(BookmarkConstants.DT_LAST_PPT,
					DateUtils.stringDt(ppaReviewDto.getPptDetailsOutDto().getDtPptDate()));
			bookmarkNonFormGrpList.add(bookmarkDtPptDate);
		}

		// CSEC22D
		if (!ObjectUtils.isEmpty(ppaReviewDto.getEventPerson())) {
			BookmarkDto bookmarkEventPerson = createBookmark(BookmarkConstants.CPS_REASON,
					ppaReviewDto.getEventPerson());
			bookmarkNonFormGrpList.add(bookmarkEventPerson);
		}

		// artf212430 : populate PERMANENCY CONFERENCE section default data
		bookmarkNonFormGrpList.add(createBookmark(DEFTXTPERMCONF, DEFTXTPERMCONF_TEXT));

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);

		return preFillData;

	}

	// artf212430 default data
	private static final String DEFTXTPERMCONF = "DEFTXTPERMCONF";
	private static final String DEFTXTPERMCONF_TEXT = new StringBuilder().append("<br>")
			.append("<b>Date:</b>").append("<br>").append("<br>")
			.append("<b>Subject Child(ren):</b>").append("<br>").append("<br>")
			.append("<b>Description</b>").append("<br>").append("<br>")
			.append("<b>Current Circumstances (child, family, kinship)</b>").append("<br>")
			.append("<b>include placement, behaviors, contacts/visitation with parents/siblings/kinship</b>").append("<br>").append("<br>")
			.append("<b>Permanency Goal</b>").append("<br>").append("<br>")
			.append("<b>Progress in Addressing Permanency Goal</b>").append("<br>")
			.append("<b>include what steps were planned in last conference and what has transpired</b>").append("<br>").append("<br>")
			.append("<b>Barriers</b>").append("<br>").append("<br>")
			.append("<b>Current Family/Kinship resources</b>").append("<br>").append("<br>")
			.append("<b>CPS resources</b>").append("<br>").append("<br>")
			.append("<b>Options / Decisions</b>").append("<br>").append("<br>")
			.append("<b>Next Steps</b>").append("<br>").append("<br>")
			.toString();

}
