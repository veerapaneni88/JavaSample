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
import us.tx.state.dfps.service.prt.dto.PRTParticipantDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.subcare.dto.PRTContactMainDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * is for prefilling data from PRTContactService to populate form prtcap> July
 * 2, 2018- 11:23:48 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Component
public class PRTContactActionPrefillData extends DocumentServiceUtil {

	/**
	 * Method Name: returnPrefillData Method Description: prefill data from
	 * PRTContactService for prtcap form
	 * 
	 * @param parentDtoobj
	 * @return PreFillDataServiceDto
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		PRTContactMainDto prtContactMainDto = (PRTContactMainDto) parentDtoobj;

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		if (!ObjectUtils.isEmpty(prtContactMainDto.getpRTActionPlanDto().getIdPrtActionPlan())) {
			// set prt completor name and prt date
			if (!ObjectUtils.isEmpty(prtContactMainDto.getpRTActionPlanDto().getDtComplete())) {
				BookmarkDto title = createBookmark(BookmarkConstants.TITLE_PRTCOMPLETOR,
						prtContactMainDto.getFullName());
				BookmarkDto date = createBookmark(BookmarkConstants.TITLE_PRTDATE,
						DateUtils.stringDt(prtContactMainDto.getpRTActionPlanDto().getDtComplete()));
				bookmarkNonFrmGrpList.add(date);
				bookmarkNonFrmGrpList.add(title);
			}

			// child information
			if (!ObjectUtils.isEmpty(prtContactMainDto.getpRTActionPlanDto().getChildren())) {
				for (PRTPersonLinkDto dto : prtContactMainDto.getpRTActionPlanDto().getChildren()) {
					FormDataGroupDto recommendedGoalsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILDINFO,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> groupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto childName = createBookmark(BookmarkConstants.CHILDINFO_NAME, dto.getNmPersonFull());
					BookmarkDto childGoal = createBookmarkWithCodesTable(BookmarkConstants.CHILDINFO_CDRCMNDPRIMARYGOAL,
							dto.getCdRcmndPrimaryGoal(), CodesConstant.CCPPRMGL);
					BookmarkDto concurrentGoal = createBookmarkWithCodesTable(
							BookmarkConstants.CHILDINFO_CDRCMNDCONCURRENTGOAL, prtContactMainDto.getFullName(),
							CodesConstant.CCPPRMGL);
					groupbookmarkList.add(childName);
					groupbookmarkList.add(childGoal);
					groupbookmarkList.add(concurrentGoal);
					recommendedGoalsGroup.setBookmarkDtoList(groupbookmarkList);
					formDataGroupList.add(recommendedGoalsGroup);
				}
			}
			// PARTICIPANT DOCUMENTATION
			if (!ObjectUtils.isEmpty(prtContactMainDto.getpRTActionPlanDto().getPrtParticipantDto())) {
				for (PRTParticipantDto dto : prtContactMainDto.getpRTActionPlanDto().getPrtParticipantDto()) {
					FormDataGroupDto participantGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PARTICIPANT,
							FormConstants.EMPTY_STRING);
					BookmarkDto name = createBookmark(BookmarkConstants.PARTICIPANT_NAME, dto.getStrPersonName());
					BookmarkDto prtRole = createBookmarkWithCodesTable(BookmarkConstants.PARTICIPANT_CDPRTROLE,
							dto.getCdPrtRole(), CodesConstant.CPRTROLE);
					List<BookmarkDto> groupbookmarkList = new ArrayList<BookmarkDto>();
					groupbookmarkList.add(name);
					groupbookmarkList.add(prtRole);
					participantGroup.setBookmarkDtoList(groupbookmarkList);
					formDataGroupList.add(participantGroup);
				}
			}
			// DEBRIEF OF ROUNDTABLE
			BookmarkDto coment = createBookmark(BookmarkConstants.TXTDBRFRECOMEND,
					prtContactMainDto.getpRTActionPlanDto().getDbrfRecomend());
			BookmarkDto fam = createBookmark(BookmarkConstants.TXTDBRFEXPLTOFAM,
					prtContactMainDto.getpRTActionPlanDto().getDbrfExplToFam());
			BookmarkDto qstn = createBookmark(BookmarkConstants.TXTDBRFUNANSWRQSTN,
					prtContactMainDto.getpRTActionPlanDto().getDbrfUnanswrQstn());
			BookmarkDto otherCase = createBookmark(BookmarkConstants.TXTDBRFOTHRCASE,
					prtContactMainDto.getpRTActionPlanDto().getDbrfOthrCase());
			bookmarkNonFrmGrpList.add(coment);
			bookmarkNonFrmGrpList.add(fam);
			bookmarkNonFrmGrpList.add(qstn);
			bookmarkNonFrmGrpList.add(otherCase);
		}
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}

}
