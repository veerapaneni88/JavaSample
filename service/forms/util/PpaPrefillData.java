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
import us.tx.state.dfps.service.forms.dto.PpaDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.prt.dto.PRTParticipantDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Populates
 * prefill data for form CSC39O00 Feb 20, 2018- 10:20:01 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Component
public class PpaPrefillData extends DocumentServiceUtil {

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
		PpaDto ppaDto = (PpaDto) parentDtoobj;

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// csc39o01 PPT Participation List

		if (!ObjectUtils.isEmpty(ppaDto.getpRTParticipantDto())) {
			for (PRTParticipantDto ppDto : ppaDto.getpRTParticipantDto()) {
				FormDataGroupDto pptGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PART_INFO_RPT_GP,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPptList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPart = createBookmark(BookmarkConstants.PART_INFO_DATE_OF_PARTCP,
						DateUtils.stringDt(ppDto.getDtPart()));
				bookmarkPptList.add(bookmarkPart);
				BookmarkDto bookmarkPartDate = createBookmark(BookmarkConstants.PART_INFO_DATE_OF_NOTIF,
						DateUtils.stringDt(ppDto.getDtPartDate()));
				bookmarkPptList.add(bookmarkPartDate);
				BookmarkDto bookmarkNotifType = createBookmarkWithCodesTable(BookmarkConstants.PART_INFO_TYPE_OF_NOTIF,
						ppDto.getCdNotifType(), CodesConstant.CPPTNOTM);
				bookmarkPptList.add(bookmarkNotifType);
				BookmarkDto bookmarkNmPartFull = createBookmark(BookmarkConstants.PART_INFO_NAME,
						ppDto.getNmPartFull());
				bookmarkPptList.add(bookmarkNmPartFull);
				BookmarkDto bookmarkSdsPartRel = createBookmark(BookmarkConstants.PART_INFO_RELTNSP_TO_CASE,
						ppDto.getSdsPartRel());
				bookmarkPptList.add(bookmarkSdsPartRel);
				pptGroupDto.setBookmarkDtoList(bookmarkPptList);
				formDataGroupList.add(pptGroupDto);
			}
		}

		// for bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		// CSEC02D GenericCaseInfoDto
		if (!ObjectUtils.isEmpty(ppaDto.getGenericCaseInfoDto())) {
			BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
					ppaDto.getGenericCaseInfoDto().getNmStage());
			bookmarkNonFormGrpList.add(bookmarkNmCase);
			BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
					ppaDto.getGenericCaseInfoDto().getIdStage());
			bookmarkNonFormGrpList.add(bookmarkIdCase);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);

		return preFillData;
	}

}
