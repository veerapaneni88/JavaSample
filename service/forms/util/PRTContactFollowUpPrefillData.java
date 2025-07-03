package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.subcare.dto.PRTContactMainDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * is for prefilling data from PRTContactService to populate form prtcfu> July
 * 2, 2018- 11:23:48 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Component
public class PRTContactFollowUpPrefillData extends DocumentServiceUtil {

	/**
	 * Method Name: returnPrefillData Method Description: prefill data from
	 * PRTContactService for prtcfu form
	 * 
	 * @param parentDtoobj
	 * @return PreFillDataServiceDto
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		PRTContactMainDto prtContactMainDto = (PRTContactMainDto) parentDtoobj;

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		if (!ObjectUtils.isEmpty(prtContactMainDto.getpRTActPlanFollowUpDto().getIdPrtActplnFollowup())) {
			// set prt completor name and prt date
			if (!ObjectUtils.isEmpty(prtContactMainDto.getpRTActPlanFollowUpDto().getDtComplete())) {
				BookmarkDto title = createBookmark(BookmarkConstants.TITLE_PRTCOMPLETOR,
						prtContactMainDto.getFullName());
				BookmarkDto date = createBookmark(BookmarkConstants.TITLE_PRTDATE,
						DateUtils.stringDt(prtContactMainDto.getpRTActPlanFollowUpDto().getDtComplete()));
				BookmarkDto childGoal = createBookmarkWithCodesTable(BookmarkConstants.TITLE_PRTTYPE,
						prtContactMainDto.getpRTActPlanFollowUpDto().getCdType(), CodesConstant.CPRTFLTY);
				BookmarkDto month = createBookmark(BookmarkConstants.TITLE_PRTMONTH,
						prtContactMainDto.getpRTActPlanFollowUpDto().getFollowupMonth());
				bookmarkNonFrmGrpList.add(date);
				bookmarkNonFrmGrpList.add(title);
				bookmarkNonFrmGrpList.add(childGoal);
				bookmarkNonFrmGrpList.add(month);
			}

			// child information
			if (!ObjectUtils.isEmpty(prtContactMainDto.getpRTActPlanFollowUpDto().getChildren())) {
				for (PRTPersonLinkDto dto : prtContactMainDto.getpRTActPlanFollowUpDto().getChildren()) {
					FormDataGroupDto recommendedGoalsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILDINFO,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> groupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto childName = createBookmark(BookmarkConstants.CHILDINFO_NAME, dto.getNmPersonFull());
					if (!ObjectUtils.isEmpty(dto.getDtPrtExit())) {
						BookmarkDto exitDate = createBookmark(BookmarkConstants.CHILDINFO_EXITDATE,
								DateUtils.stringDt(dto.getDtPrtExit()));
						groupbookmarkList.add(exitDate);
					}
					BookmarkDto childGoal = createBookmarkWithCodesTable(BookmarkConstants.CHILDINFO_EXITREASON,
							dto.getCdExitReason(), CodesConstant.CPRTEXRN);
					groupbookmarkList.add(childName);
					groupbookmarkList.add(childGoal);
					recommendedGoalsGroup.setBookmarkDtoList(groupbookmarkList);
					formDataGroupList.add(recommendedGoalsGroup);
				}
			}

			// DEBRIEF OF ROUNDTABLE
			BookmarkDto progress = createBookmark(BookmarkConstants.TXT_DBRF_PROGRESS_MADE,
					prtContactMainDto.getpRTActPlanFollowUpDto().getDebriefProgressMade());
			BookmarkDto chlg = createBookmark(BookmarkConstants.TXT_DBRF_CHLG_IDENTIFIED,
					prtContactMainDto.getpRTActPlanFollowUpDto().getDebriefChlgIdentified());
			BookmarkDto soln = createBookmark(BookmarkConstants.TXT_DBRF_SOLN_IDENTIFIED,
					prtContactMainDto.getpRTActPlanFollowUpDto().getDebriefSolnIdentified());
			if (CodesConstant.CPRTFLTY_30.equals(prtContactMainDto.getpRTActPlanFollowUpDto().getCdType())) {
				FormDataGroupDto indPdParticipBiannualGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CPRTFLTY,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> groupbookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto otherCase = createBookmarkWithCodesTable(BookmarkConstants.CPRTFLTY_30,
						prtContactMainDto.getpRTActPlanFollowUpDto().getIndPdParticipBiannual(),
						CodesConstant.CINVACAN);
				groupbookmarkList.add(otherCase);
				indPdParticipBiannualGroup.setBookmarkDtoList(groupbookmarkList);
				formDataGroupList.add(indPdParticipBiannualGroup);
			}

			bookmarkNonFrmGrpList.add(progress);
			bookmarkNonFrmGrpList.add(chlg);
			bookmarkNonFrmGrpList.add(soln);
		}
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

}
