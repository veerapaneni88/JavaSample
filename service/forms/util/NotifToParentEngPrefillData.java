package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.NotifToParentDto;
import us.tx.state.dfps.service.workload.dto.StageReviewDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 6, 2018- 2:13:59 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class NotifToParentEngPrefillData extends DocumentServiceUtil {

	/*
	 * Form Name :ccf10o00(Notification to Parent/Professional Reporter (ARIF))
	 */

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		NotifToParentDto notifToParentDto = (NotifToParentDto) parentDtoobj;

		if (null == notifToParentDto.getCodesTablesDtolist()) {
			notifToParentDto.setCodesTablesDtolist(new ArrayList<CodesTablesDto>());
		}
		if (null == notifToParentDto.getStageReviewDtolist()) {
			notifToParentDto.setStageReviewDtolist(new ArrayList<StageReviewDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// CLSC03D
		if (!TypeConvUtil.isNullOrEmpty(notifToParentDto.getCodesTablesDtolist())) {
			for (CodesTablesDto codesTableDto : notifToParentDto.getCodesTablesDtolist()) {
				if (codesTableDto.getaCode().compareTo(FormConstants.SYSCODE) > 0) {
					FormDataGroupDto ifRtbGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_BOARD,
							FormConstants.EMPTY_STRING);

					List<BookmarkDto> bookmarkIfRtbList = new ArrayList<BookmarkDto>();
					BookmarkDto szSysDecode = createBookmark(BookmarkConstants.HEADER_BOARD_CITY,
							codesTableDto.getaDecode());
					BookmarkDto szSysDecode2 = createBookmark(BookmarkConstants.HEADER_BOARD_NAME,
							codesTableDto.getbDecode());
					bookmarkIfRtbList.add(szSysDecode);
					bookmarkIfRtbList.add(szSysDecode2);
					ifRtbGroupDto.setBookmarkDtoList(bookmarkIfRtbList);
					formDataGroupList.add(ifRtbGroupDto);
				}
				if (codesTableDto.getaCode().compareTo(FormConstants.SYSCODE) == 0) {
					FormDataGroupDto ifRtbGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_DIRECTOR,
							FormConstants.EMPTY_STRING);

					List<BookmarkDto> bookmarkIfRtbList = new ArrayList<BookmarkDto>();
					BookmarkDto szSysDecode = createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
							codesTableDto.getaDecode());
					BookmarkDto szSysDecode2 = createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME,
							codesTableDto.getbDecode());
					bookmarkIfRtbList.add(szSysDecode);
					bookmarkIfRtbList.add(szSysDecode2);
					ifRtbGroupDto.setBookmarkDtoList(bookmarkIfRtbList);
					formDataGroupList.add(ifRtbGroupDto);
				}
			}
		}

		// CLSC65D
		if (!TypeConvUtil.isNullOrEmpty(notifToParentDto.getStageReviewDtolist())) {
			for (StageReviewDto stageReviewDto : notifToParentDto.getStageReviewDtolist()) {
				FormDataGroupDto ifRtbGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_ALLEGATIONS,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkIfRtbList = new ArrayList<BookmarkDto>();
				BookmarkDto cdAdminAllegDisposition = createBookmarkWithCodesTable(BookmarkConstants.PRIOR_DISPOSITION,
						stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN);
				BookmarkDto cdAdminAllegSeverity = createBookmarkWithCodesTable(BookmarkConstants.PRIOR_SEVERITY,
						stageReviewDto.getCdAdminAllegSeverity(), CodesConstant.CSEVERTY);
				BookmarkDto cdAdminAllegType = createBookmarkWithCodesTable(BookmarkConstants.PRIOR_ALLEGATION,
						stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP);
				BookmarkDto cdNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX,
						stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX);
				BookmarkDto nmNameFirst = createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
						stageReviewDto.getNmNameFirst());
				BookmarkDto nmNameLast = createBookmark(BookmarkConstants.VICTIM_NAME_LAST,
						stageReviewDto.getNmNameLast());
				BookmarkDto nmNameMiddle = createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
						stageReviewDto.getNmNameMiddle());
				bookmarkIfRtbList.add(cdAdminAllegDisposition);
				bookmarkIfRtbList.add(cdAdminAllegSeverity);
				bookmarkIfRtbList.add(cdAdminAllegType);
				bookmarkIfRtbList.add(cdNameSuffix);
				bookmarkIfRtbList.add(nmNameFirst);
				bookmarkIfRtbList.add(nmNameLast);
				bookmarkIfRtbList.add(nmNameMiddle);
				ifRtbGroupDto.setBookmarkDtoList(bookmarkIfRtbList);
				formDataGroupList.add(ifRtbGroupDto);

				FormDataGroupDto currentAlleg = createFormDataGroup(FormGroupsConstants.TMPLAT_CURRENT_ALLEGATIONS,
						FormConstants.EMPTY_STRING);

				List<BookmarkDto> bookmarkIfRtbList2 = new ArrayList<BookmarkDto>();
				BookmarkDto cdAdminAllegDisposition2 = createBookmarkWithCodesTable(BookmarkConstants.PRIOR_DISPOSITION,
						stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN);
				BookmarkDto cdAdminAllegSeverity2 = createBookmarkWithCodesTable(BookmarkConstants.PRIOR_SEVERITY,
						stageReviewDto.getCdAdminAllegSeverity(), CodesConstant.CSEVERTY);
				BookmarkDto cdAdminAllegType2 = createBookmarkWithCodesTable(BookmarkConstants.PRIOR_ALLEGATION,
						stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP);
				BookmarkDto cdNameSuffix2 = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX,
						stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX);
				BookmarkDto nmNameFirst2 = createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
						stageReviewDto.getNmNameFirst());
				BookmarkDto nmNameLast2 = createBookmark(BookmarkConstants.VICTIM_NAME_LAST,
						stageReviewDto.getNmNameLast());
				BookmarkDto nmNameMiddle2 = createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
						stageReviewDto.getNmNameMiddle());
				bookmarkIfRtbList2.add(cdAdminAllegDisposition2);
				bookmarkIfRtbList2.add(cdAdminAllegSeverity2);
				bookmarkIfRtbList2.add(cdAdminAllegType2);
				bookmarkIfRtbList2.add(cdNameSuffix2);
				bookmarkIfRtbList2.add(nmNameFirst2);
				bookmarkIfRtbList2.add(nmNameLast2);
				bookmarkIfRtbList2.add(nmNameMiddle2);
				currentAlleg.setBookmarkDtoList(bookmarkIfRtbList2);
				formDataGroupList.add(currentAlleg);
			}
		}

		// CSES63D
		if (notifToParentDto.getAdminReviewdto().getCdAdminRvAppealType().compareTo(FormConstants.SYSCODE2) == 0) {
			FormDataGroupDto tmpReview1 = createFormDataGroup(FormGroupsConstants.TMPLAT_REVIEW_TYPE_1,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tmpReview1);
		}
		if (notifToParentDto.getAdminReviewdto().getCdAdminRvAppealType().compareTo(FormConstants.SYSCODE2) != 0) {
			FormDataGroupDto tmpReview2 = createFormDataGroup(FormGroupsConstants.TMPLAT_REVIEW_TYPE_2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tmpReview2);
		}

		/*
		 * Populating the non form group data into prefill data. !!bookmarks
		 */

		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		// CSEC34D_PersonAddressDto
		BookmarkDto parentaddrzip = createBookmark(BookmarkConstants.PARENT_ADDR_ZIP,
				notifToParentDto.getPersonAddressDto().getAddrPersonAddrZip());
		BookmarkDto szAddrCity = createBookmark(BookmarkConstants.PARENT_ADDR_CITY,
				notifToParentDto.getPersonAddressDto().getAddrPersonAddrCity());
		BookmarkDto szAddrPersAddrStLn1 = createBookmark(BookmarkConstants.PARENT_ADDR_ST_1,
				notifToParentDto.getPersonAddressDto().getAddrPersAddrStLn1());
		BookmarkDto szAddrPersAddrStLn2 = createBookmark(BookmarkConstants.PARENT_ADDR_ST_2,
				notifToParentDto.getPersonAddressDto().getAddrPersAddrStLn2());
		BookmarkDto szCdAddrState = createBookmarkWithCodesTable(BookmarkConstants.PARENT_ADDR_STATE,
				notifToParentDto.getPersonAddressDto().getCdPersonAddrState(), CodesConstant.CSTATE);
		bookmarkNonFrmGrpList.add(parentaddrzip);
		bookmarkNonFrmGrpList.add(szAddrCity);
		bookmarkNonFrmGrpList.add(szAddrPersAddrStLn1);
		bookmarkNonFrmGrpList.add(szAddrPersAddrStLn2);
		bookmarkNonFrmGrpList.add(szCdAddrState);

		// CSEC35D
		BookmarkDto szCdNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.PARENT_NAME_SUFFIX,
				notifToParentDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
		BookmarkDto szCdNameSuffix2 = createBookmarkWithCodesTable(BookmarkConstants.PARENT2_NAME_SUFFIX,
				notifToParentDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
		BookmarkDto szNmNameFirst = createBookmark(BookmarkConstants.PARENT_NAME_FIRST,
				notifToParentDto.getNameDetailDto().getNmNameFirst());
		BookmarkDto szNmNameFirst2 = createBookmark(BookmarkConstants.PARENT2_NAME_FIRST,
				notifToParentDto.getNameDetailDto().getNmNameFirst());
		BookmarkDto szNmNameLast = createBookmark(BookmarkConstants.PARENT_NAME_LAST,
				notifToParentDto.getNameDetailDto().getNmNameLast());
		BookmarkDto szNmNameLast2 = createBookmark(BookmarkConstants.PARENT2_NAME_LAST,
				notifToParentDto.getNameDetailDto().getNmNameLast());
		BookmarkDto szNmNameMiddle = createBookmark(BookmarkConstants.PARENT_NAME_MIDDLE,
				notifToParentDto.getNameDetailDto().getNmNameMiddle());
		BookmarkDto szNmNameMiddle2 = createBookmark(BookmarkConstants.PARENT2_NAME_MIDDLE,
				notifToParentDto.getNameDetailDto().getNmNameMiddle());
		BookmarkDto szCdNameSuffix3 = createBookmarkWithCodesTable(BookmarkConstants.REQUESTER_NAME_SUFFIX,
				notifToParentDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
		BookmarkDto szNmNameFirst3 = createBookmark(BookmarkConstants.REQUESTER_NAME_FIRST,
				notifToParentDto.getNameDetailDto().getNmNameFirst());
		BookmarkDto szNmNameLast3 = createBookmark(BookmarkConstants.REQUESTER_NAME_LAST,
				notifToParentDto.getNameDetailDto().getNmNameLast());
		BookmarkDto szNmNameMiddle3 = createBookmark(BookmarkConstants.REQUESTER_NAME_MIDDLE,
				notifToParentDto.getNameDetailDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(szCdNameSuffix);
		bookmarkNonFrmGrpList.add(szCdNameSuffix2);
		bookmarkNonFrmGrpList.add(szNmNameFirst);
		bookmarkNonFrmGrpList.add(szNmNameFirst2);
		bookmarkNonFrmGrpList.add(szNmNameLast);
		bookmarkNonFrmGrpList.add(szNmNameLast2);
		bookmarkNonFrmGrpList.add(szNmNameMiddle);
		bookmarkNonFrmGrpList.add(szNmNameMiddle2);
		bookmarkNonFrmGrpList.add(szCdNameSuffix3);
		bookmarkNonFrmGrpList.add(szNmNameFirst3);
		bookmarkNonFrmGrpList.add(szNmNameLast3);
		bookmarkNonFrmGrpList.add(szNmNameMiddle3);

		// CSEC01D EmployeePersPhNameDto
		if (!TypeConvUtil.isNullOrEmpty(notifToParentDto.getEmployeePersPhNameDto())) {
			BookmarkDto dtDtEmpTermination = createBookmark(BookmarkConstants.NOTIFICATION_DATE,
					DateUtils.stringDt(notifToParentDto.getEmployeePersPhNameDto().getDtEmpTermination()));
			BookmarkDto lNbrPhone = createBookmark(BookmarkConstants.USER_PHONE,
					TypeConvUtil.formatPhone(notifToParentDto.getEmployeePersPhNameDto().getNbrPhone()));
			BookmarkDto lNbrPhoneExtension = createBookmark(BookmarkConstants.USER_PHONE_EXTENSION,
					notifToParentDto.getEmployeePersPhNameDto().getNbrPhoneExtension());
			BookmarkDto szCdNameSuffix4 = createBookmarkWithCodesTable(BookmarkConstants.USER_NAME_SUFFIX,
					notifToParentDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
			BookmarkDto szNmNameFirst4 = createBookmark(BookmarkConstants.USER_NAME_FIRST,
					notifToParentDto.getEmployeePersPhNameDto().getNmNameFirst());
			BookmarkDto nmNameLast = createBookmark(BookmarkConstants.USER_NAME_LAST,
					notifToParentDto.getEmployeePersPhNameDto().getNmNameLast());
			BookmarkDto nmNameMiddle = createBookmark(BookmarkConstants.USER_NAME_MIDDLE,
					notifToParentDto.getEmployeePersPhNameDto().getNmNameMiddle());
			bookmarkNonFrmGrpList.add(dtDtEmpTermination);
			bookmarkNonFrmGrpList.add(lNbrPhone);
			bookmarkNonFrmGrpList.add(lNbrPhoneExtension);
			bookmarkNonFrmGrpList.add(szCdNameSuffix4);
			bookmarkNonFrmGrpList.add(szNmNameFirst4);
			bookmarkNonFrmGrpList.add(nmNameLast);
			bookmarkNonFrmGrpList.add(nmNameMiddle);
		}
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);

		return preFillData;
	}
}
