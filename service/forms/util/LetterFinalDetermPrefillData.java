package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.LetterFinalDetermDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: generates
 * prefill string for form civ23o00 Jul 9, 2018- 11:10:26 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Component
public class LetterFinalDetermPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		LetterFinalDetermDto prefillDto = (LetterFinalDetermDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonGroupList = new ArrayList<BookmarkDto>();

		if (!ObjectUtils.isEmpty(prefillDto.getCodesTablesDto())) {
			// parent group cfzz0601
			if (FormConstants.SYSCODE.equals(prefillDto.getCodesTablesDto().getaCode())) {
				FormDataGroupDto headerDirectorGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_HEADER_DIRECTOR, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkHeaderDirectorList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDirectorTitle = createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
						prefillDto.getCodesTablesDto().getaDecode());
				bookmarkHeaderDirectorList.add(bookmarkDirectorTitle);
				BookmarkDto bookmarkDirectorName = createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME,
						prefillDto.getCodesTablesDto().getbDecode());
				bookmarkHeaderDirectorList.add(bookmarkDirectorName);

				headerDirectorGroupDto.setBookmarkDtoList(bookmarkHeaderDirectorList);
				formDataGroupList.add(headerDirectorGroupDto);
			}

			// parent group cfzz0501
			else if (FormConstants.SYSCODE.compareTo(prefillDto.getCodesTablesDto().getaCode()) < 0) {
				FormDataGroupDto headerBoardGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_BOARD,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkHeaderBoardList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkHeaderBoardCity = createBookmark(BookmarkConstants.HEADER_BOARD_CITY,
						prefillDto.getCodesTablesDto().getaDecode());
				bookmarkHeaderBoardList.add(bookmarkHeaderBoardCity);
				BookmarkDto bookmarkHeaderBoardName = createBookmark(BookmarkConstants.HEADER_BOARD_NAME,
						prefillDto.getCodesTablesDto().getbDecode());
				bookmarkHeaderBoardList.add(bookmarkHeaderBoardName);

				headerBoardGroupDto.setBookmarkDtoList(bookmarkHeaderBoardList);
				formDataGroupList.add(headerBoardGroupDto);
			}
		}

		if (!ObjectUtils.isEmpty(prefillDto.getCaseInfoDto())
				&& ServiceConstants.Y.equals(prefillDto.getCaseInfoDto().getIndStagePersReporter())) {
			// parent group civ23o03
			FormDataGroupDto addrRepGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_REP,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkAddrRepList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.REP_ADDRESS_ZIP,
					prefillDto.getCaseInfoDto().getAddrPersonAddrZip());
			bookmarkAddrRepList.add(bookmarkAddrZip);
			BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.REP_ADDRESS_CITY,
					prefillDto.getCaseInfoDto().getAddrPersonAddrCity());
			bookmarkAddrRepList.add(bookmarkAddrCity);
			BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.REP_ADDRESS_LINE_1,
					prefillDto.getCaseInfoDto().getAddrPersAddrStLn1());
			bookmarkAddrRepList.add(bookmarkAddrLn1);
			BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.REP_ADDRESS_LINE_2,
					prefillDto.getCaseInfoDto().getAddrPersAddrStLn2());
			bookmarkAddrRepList.add(bookmarkAddrLn2);
			BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.REP_ADDRESS_STATE,
					prefillDto.getCaseInfoDto().getCdPersonAddrState());
			bookmarkAddrRepList.add(bookmarkAddrState);
			BookmarkDto bookmarkAddrNmSuffix = createBookmarkWithCodesTable(BookmarkConstants.REP_ADDR_NAME_SUFFIX,
					prefillDto.getCaseInfoDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
			bookmarkAddrRepList.add(bookmarkAddrNmSuffix);
			BookmarkDto bookmarkAddrNmFirst = createBookmark(BookmarkConstants.REP_ADDR_NAME_FIRST,
					prefillDto.getCaseInfoDto().getNmNameFirst());
			bookmarkAddrRepList.add(bookmarkAddrNmFirst);
			BookmarkDto bookmarkAddrNmLast = createBookmark(BookmarkConstants.REP_ADDR_NAME_LAST,
					prefillDto.getCaseInfoDto().getNmNameLast());
			bookmarkAddrRepList.add(bookmarkAddrNmLast);
			BookmarkDto bookmarkAddrNmMiddle = createBookmark(BookmarkConstants.REP_ADDR_NAME_MIDDLE,
					prefillDto.getCaseInfoDto().getNmNameMiddle());
			bookmarkAddrRepList.add(bookmarkAddrNmMiddle);

			addrRepGroupDto.setBookmarkDtoList(bookmarkAddrRepList);
			formDataGroupList.add(addrRepGroupDto);

			// parent group civ23o04
			FormDataGroupDto repGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REP,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRepList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkRepNmSuffix = createBookmarkWithCodesTable(BookmarkConstants.REP_NAME_SUFFIX,
					prefillDto.getCaseInfoDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
			bookmarkRepList.add(bookmarkRepNmSuffix);
			BookmarkDto bookmarkRepNmFirst = createBookmark(BookmarkConstants.REP_NAME_FIRST,
					prefillDto.getCaseInfoDto().getNmNameFirst());
			bookmarkRepList.add(bookmarkRepNmFirst);
			BookmarkDto bookmarkRepNmLast = createBookmark(BookmarkConstants.REP_NAME_LAST,
					prefillDto.getCaseInfoDto().getNmNameLast());
			bookmarkRepList.add(bookmarkRepNmLast);
			BookmarkDto bookmarkRepNmMiddle = createBookmark(BookmarkConstants.REP_NAME_MIDDLE,
					prefillDto.getCaseInfoDto().getNmNameMiddle());
			bookmarkRepList.add(bookmarkRepNmMiddle);

			repGroupDto.setBookmarkDtoList(bookmarkRepList);
			formDataGroupList.add(repGroupDto);
		}

		if (!ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
			if (DateUtils.isBefore(prefillDto.getGenericCaseInfoDto().getDtStageStart(),
					DateUtils.getJavaDate(2010, 6, 7))) {
				// parent group civ2305
				FormDataGroupDto priorJuneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_JUNE,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> priorJuneGroupList = new ArrayList<FormDataGroupDto>();

				// sub group civ2306
				if (!ObjectUtils.isEmpty(prefillDto.getFacilInvDtlDto())) {
					FormDataGroupDto facNameGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAC_NAME,
							FormGroupsConstants.TMPLAT_PRIOR_JUNE);
					List<BookmarkDto> bookmarkFacNameList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkRepNmFac = createBookmark(BookmarkConstants.REP_NAME_FACILITY1,
							prefillDto.getFacilInvDtlDto().getNmFacilinvstFacility());
					bookmarkFacNameList.add(bookmarkRepNmFac);

					facNameGroupDto.setBookmarkDtoList(bookmarkFacNameList);
					priorJuneGroupList.add(facNameGroupDto);
				}

				priorJuneGroupDto.setFormDataGroupList(priorJuneGroupList);
				formDataGroupList.add(priorJuneGroupDto);

				// parent group civ2308
				FormDataGroupDto priorJune2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_JUNE2,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> priorJune2GroupList = new ArrayList<FormDataGroupDto>();

				// sub group civ2309
				if (!ObjectUtils.isEmpty(prefillDto.getFacilInvDtlDto())) {
					FormDataGroupDto overallDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_OVERALL_DISP,
							FormGroupsConstants.TMPLAT_PRIOR_JUNE2);
					List<BookmarkDto> bookmarkOverallDispList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkOverallDisp = createBookmarkWithCodesTable(BookmarkConstants.OVERALL_DISP,
							prefillDto.getFacilInvDtlDto().getCdFacilInvstOvrallDis(), CodesConstant.CDISPSTN);
					bookmarkOverallDispList.add(bookmarkOverallDisp);

					overallDispGroupDto.setBookmarkDtoList(bookmarkOverallDispList);
					priorJune2GroupList.add(overallDispGroupDto);
				}

				priorJune2GroupDto.setFormDataGroupList(priorJune2GroupList);
				formDataGroupList.add(priorJune2GroupDto);

				// parent group civ2311
				FormDataGroupDto priorJune3GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_JUNE3,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> priorJune3GroupList = new ArrayList<FormDataGroupDto>();

				// sub group civ2312
				if (!ObjectUtils.isEmpty(prefillDto.getFacilInvDtlDto())) {
					FormDataGroupDto facName2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAC_NAME2,
							FormGroupsConstants.TMPLAT_PRIOR_JUNE3);
					List<BookmarkDto> bookmarkFacName2List = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkRepNmFac = createBookmark(BookmarkConstants.REP_NAME_FACILITY2,
							prefillDto.getFacilInvDtlDto().getNmFacilinvstFacility());
					bookmarkFacName2List.add(bookmarkRepNmFac);

					facName2GroupDto.setBookmarkDtoList(bookmarkFacName2List);
					priorJune3GroupList.add(facName2GroupDto);
				}

				priorJune3GroupDto.setFormDataGroupList(priorJune3GroupList);
				formDataGroupList.add(priorJune3GroupDto);
			}

			else {
				// parent group civ2307
				FormDataGroupDto postJuneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_POST_JUNE,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> postJuneGroupList = new ArrayList<FormDataGroupDto>();

				// sub group civ2314
				if (!ObjectUtils.isEmpty(prefillDto.getMultiAddressDto())) {
					FormDataGroupDto facPostGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_FAC_POST,
							FormGroupsConstants.TMPLAT_POST_JUNE);
					List<BookmarkDto> bookmarkFacPostList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkNmFacPost = createBookmark(BookmarkConstants.NM_FAC_POST,
							prefillDto.getMultiAddressDto().getaCpNmResource());
					bookmarkFacPostList.add(bookmarkNmFacPost);

					facPostGroupDto.setBookmarkDtoList(bookmarkFacPostList);
					postJuneGroupList.add(facPostGroupDto);
				}

				postJuneGroupDto.setFormDataGroupList(postJuneGroupList);
				formDataGroupList.add(postJuneGroupDto);

				// parent group civ2310
				FormDataGroupDto postJune2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_POST_JUNE2,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(postJune2GroupDto);

				// parent group civ2313
				FormDataGroupDto postJune3GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_POST_JUNE3,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> postJune3GroupList = new ArrayList<FormDataGroupDto>();

				// sub group civ2315
				if (!ObjectUtils.isEmpty(prefillDto.getMultiAddressDto())) {
					FormDataGroupDto facPost3GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_FAC_POST3,
							FormGroupsConstants.TMPLAT_POST_JUNE3);
					List<BookmarkDto> bookmarkFacPost3List = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkNmFacPost3 = createBookmark(BookmarkConstants.NM_FAC_POST3,
							prefillDto.getMultiAddressDto().getaCpNmResource());
					bookmarkFacPost3List.add(bookmarkNmFacPost3);

					facPost3GroupDto.setBookmarkDtoList(bookmarkFacPost3List);
					postJune3GroupList.add(facPost3GroupDto);
				}

				postJune3GroupDto.setFormDataGroupList(postJune3GroupList);
				formDataGroupList.add(postJune3GroupDto);
			}
		}

		// Non group bookmarks
		// CSEC01D
		if (!ObjectUtils.isEmpty(prefillDto.getEmployeePersPhNameDto())) {
			BookmarkDto bookmarkRepWorkerSuffix = createBookmarkWithCodesTable(BookmarkConstants.REP_WORKER_SUFFIX,
					prefillDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
			bookmarkNonGroupList.add(bookmarkRepWorkerSuffix);
			BookmarkDto bookmarkRepWorkerFirst = createBookmark(BookmarkConstants.REP_WORKER_NAME_FIRST,
					prefillDto.getEmployeePersPhNameDto().getNmNameFirst());
			bookmarkNonGroupList.add(bookmarkRepWorkerFirst);
			BookmarkDto bookmarkRepWorkerLast = createBookmark(BookmarkConstants.REP_WORKER_NAME_LAST,
					prefillDto.getEmployeePersPhNameDto().getNmNameLast());
			bookmarkNonGroupList.add(bookmarkRepWorkerLast);
			BookmarkDto bookmarkRepWorkerMiddle = createBookmark(BookmarkConstants.REP_WORKER_NAME_MIDDLE,
					prefillDto.getEmployeePersPhNameDto().getNmNameMiddle());
			bookmarkNonGroupList.add(bookmarkRepWorkerMiddle);
		}

		// CSES39D
		if (!ObjectUtils.isEmpty(prefillDto.getFacilInvDtlDto())) {
			BookmarkDto bookmarkRepSysDate = createBookmark(BookmarkConstants.REP_SYSTEM_DATE,
					DateUtils.stringDt(prefillDto.getFacilInvDtlDto().getDtFacilInvstBegun()));
			bookmarkNonGroupList.add(bookmarkRepSysDate);
			BookmarkDto bookmarkDtIntake = createBookmark(BookmarkConstants.REP_DATE_INTAKE,
					DateUtils.stringDt(prefillDto.getFacilInvDtlDto().getDtFacilInvstIntake()));
			bookmarkNonGroupList.add(bookmarkDtIntake);
			BookmarkDto bookmarkOverallDisp = createBookmarkWithCodesTable(BookmarkConstants.REP_OVERALL_DISP,
					prefillDto.getFacilInvDtlDto().getCdFacilInvstOvrallDis(), CodesConstant.CDISPSTN);
			bookmarkNonGroupList.add(bookmarkOverallDisp);
			BookmarkDto bookmarkRepNmFac1 = createBookmark(BookmarkConstants.REP_NAME_FACILITY1,
					prefillDto.getFacilInvDtlDto().getNmFacilinvstFacility());
			bookmarkNonGroupList.add(bookmarkRepNmFac1);
			BookmarkDto bookmarkRepNmFac2 = createBookmark(BookmarkConstants.REP_NAME_FACILITY2,
					prefillDto.getFacilInvDtlDto().getNmFacilinvstFacility());
			bookmarkNonGroupList.add(bookmarkRepNmFac2);
		}

		// CSEC02D
		if (!ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
			BookmarkDto bookmarkCaseNbr = createBookmark(BookmarkConstants.REP_CASE_NBR,
					prefillDto.getGenericCaseInfoDto().getIdCase());
			bookmarkNonGroupList.add(bookmarkCaseNbr);
		}

		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		prefillData.setBookmarkDtoList(bookmarkNonGroupList);
		prefillData.setFormDataGroupList(formDataGroupList);

		return prefillData;
	}

}
