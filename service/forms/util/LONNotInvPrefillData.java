package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lonnotinv.dto.LONNotInvDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * LONNotInvPrefillData will implement returnPrefillData operation defined in
 * DocumentServiceUtil Interface to populate the prefill data for form Form
 * CFIV2100 Mar 16, 2018- 5:14:11 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Component
public class LONNotInvPrefillData extends DocumentServiceUtil {

	public LONNotInvPrefillData() {
		super();
	}

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

		LONNotInvDto lONNotInvDto = (LONNotInvDto) parentDtoobj;
		if (ObjectUtils.isEmpty(lONNotInvDto.getMultiAddressDtoList())) {
			lONNotInvDto.setMultiAddressDtoList(new ArrayList<MultiAddressDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		// bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		/**
		 * Checking sysCode > 001. If condition satisfies then set the prefill
		 * data for group cfzz0501
		 */

		if (lONNotInvDto.getCodesTablesDto().getaCode().compareTo(FormConstants.SYSCODE) > 0) {
			FormDataGroupDto tempHeaderBoardFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_BOARD,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkHeaderBoardList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkSysDecodeDto = createBookmark(BookmarkConstants.HEADER_BOARD_CITY,
					lONNotInvDto.getCodesTablesDto().getaDecode());
			BookmarkDto bookmarkSysDecode2Dto = createBookmark(BookmarkConstants.HEADER_BOARD_NAME,
					lONNotInvDto.getCodesTablesDto().getbDecode());
			bookmarkHeaderBoardList.add(bookmarkSysDecodeDto);
			bookmarkHeaderBoardList.add(bookmarkSysDecode2Dto);

			tempHeaderBoardFrmDataGrpDto.setBookmarkDtoList(bookmarkHeaderBoardList);
			formDataGroupList.add(tempHeaderBoardFrmDataGrpDto);
		}

		/**
		 * Checking sysCode == 001. If condition satisfies then set the prefill
		 * data for group cfzz0601
		 */

		if (lONNotInvDto.getCodesTablesDto().getaCode().compareTo(FormConstants.SYSCODE) == 0) {
			FormDataGroupDto tempHeaderDirectorFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_HEADER_DIRECTOR, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkHeaderDirectorList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkSysDecodeDto = createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
					lONNotInvDto.getCodesTablesDto().getaDecode());
			BookmarkDto bookmarkSysDecode2Dto = createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME,
					lONNotInvDto.getCodesTablesDto().getbDecode());
			bookmarkHeaderDirectorList.add(bookmarkSysDecodeDto);
			bookmarkHeaderDirectorList.add(bookmarkSysDecode2Dto);

			tempHeaderDirectorFrmDataGrpDto.setBookmarkDtoList(bookmarkHeaderDirectorList);
			formDataGroupList.add(tempHeaderDirectorFrmDataGrpDto);
		}

		// set the prefill data for group cfiv2103

		if (ServiceConstants.JUNE_7_IMPACT_DATE.after(lONNotInvDto.getGenCaseInfoDto().getDtStageStart())) {
			FormDataGroupDto dtStagePostFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_JUNE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(dtStagePostFrmDataGrpDto);

			// set the prefill data for group cfiv2104
			FormDataGroupDto tempChildNameFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_FAC1,
					FormGroupsConstants.TMPLAT_PRIOR_JUNE);

			List<FormDataGroupDto> tempChildNameFrmDataGrpList = new ArrayList<FormDataGroupDto>();

			List<BookmarkDto> bookmarkChildNameList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkFacilinvstFacility = createBookmark(BookmarkConstants.REP_NAME_FACILITY1,
					lONNotInvDto.getFacilInvDtlDto().getNmFacilinvstFacility());
			bookmarkChildNameList.add(bookmarkFacilinvstFacility);

			tempChildNameFrmDataGrpDto.setBookmarkDtoList(bookmarkChildNameList);
			dtStagePostFrmDataGrpDto.setFormDataGroupList(tempChildNameFrmDataGrpList);
			formDataGroupList.add(tempChildNameFrmDataGrpDto);
			formDataGroupList.add(dtStagePostFrmDataGrpDto);
		}

		// set the prefill data for group cfiv2107

		if (ServiceConstants.JUNE_7_IMPACT_DATE.after(lONNotInvDto.getGenCaseInfoDto().getDtStageStart())) {
			FormDataGroupDto dtStagePostFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_JUNE2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(dtStagePostFrmDataGrpDto);

			// set the prefill data for group cfiv2108
			FormDataGroupDto tempChildNameFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_FAC2,
					FormGroupsConstants.TMPLAT_PRIOR_JUNE2);

			List<FormDataGroupDto> tempChildNameFrmDataGrpList = new ArrayList<FormDataGroupDto>();

			List<BookmarkDto> bookmarkChildNameList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkFacilinvstFacility = createBookmark(BookmarkConstants.REP_NAME_FACILITY2,
					lONNotInvDto.getFacilInvDtlDto().getNmFacilinvstFacility());
			bookmarkChildNameList.add(bookmarkFacilinvstFacility);

			tempChildNameFrmDataGrpDto.setBookmarkDtoList(bookmarkChildNameList);
			dtStagePostFrmDataGrpDto.setFormDataGroupList(tempChildNameFrmDataGrpList);
			formDataGroupList.add(tempChildNameFrmDataGrpDto);
			formDataGroupList.add(dtStagePostFrmDataGrpDto);
		}

		// set the prefill data for group cfiv2101

		FormDataGroupDto tmpAddressFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_REP,
				FormConstants.EMPTY_STRING);
		

		List<BookmarkDto> bookmarkRepAddressList = new ArrayList<BookmarkDto>();

		BookmarkDto bookmarkRepAddressZipDto = createBookmark(BookmarkConstants.REP_ADDRESS_ZIP,
				lONNotInvDto.getCaseInfoDto().getAddrPersonAddrZip());
		BookmarkDto bookmarkRepAddressCityDto = createBookmark(BookmarkConstants.REP_ADDRESS_CITY,
				lONNotInvDto.getCaseInfoDto().getAddrPersonAddrCity());
		BookmarkDto bookmarkRepAddressStLn1Dto = createBookmark(BookmarkConstants.REP_ADDRESS_LINE_1,
				lONNotInvDto.getCaseInfoDto().getAddrPersAddrStLn1());
		BookmarkDto bookmarkRepAddressStLn2Dto = createBookmark(BookmarkConstants.REP_ADDRESS_LINE_2,
				lONNotInvDto.getCaseInfoDto().getAddrPersAddrStLn2());
		BookmarkDto bookmarkRepAddressStateDto = createBookmark(BookmarkConstants.REP_ADDRESS_STATE,
				lONNotInvDto.getCaseInfoDto().getCdPersonAddrState());
		BookmarkDto bookmarkCdNameSuffixWorker = createBookmarkWithCodesTable(BookmarkConstants.REP_ADDR_NAME_SUFFIX,
				lONNotInvDto.getCaseInfoDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
		BookmarkDto bookmarkNameFirst = createBookmark(BookmarkConstants.REP_ADDR_NAME_FIRST,
				lONNotInvDto.getCaseInfoDto().getNmNameFirst());
		BookmarkDto bookmarkNameLast = createBookmark(BookmarkConstants.REP_ADDR_NAME_LAST,
				lONNotInvDto.getCaseInfoDto().getNmNameLast());
		BookmarkDto bookmarkNameMiddle = createBookmark(BookmarkConstants.REP_ADDR_NAME_MIDDLE,
				lONNotInvDto.getCaseInfoDto().getNmNameMiddle());
		
	

		bookmarkRepAddressList.add(bookmarkRepAddressZipDto);
		bookmarkRepAddressList.add(bookmarkRepAddressCityDto);
		bookmarkRepAddressList.add(bookmarkRepAddressStLn1Dto);
		bookmarkRepAddressList.add(bookmarkRepAddressStLn2Dto);
		bookmarkRepAddressList.add(bookmarkRepAddressStateDto);
		bookmarkRepAddressList.add(bookmarkCdNameSuffixWorker);
		bookmarkRepAddressList.add(bookmarkNameFirst);
		bookmarkRepAddressList.add(bookmarkNameLast);
		bookmarkRepAddressList.add(bookmarkNameMiddle);

		tmpAddressFrmDataGrpDto.setBookmarkDtoList(bookmarkRepAddressList);
		formDataGroupList.add(tmpAddressFrmDataGrpDto);

		// set the prefill data for group cfiv2102

		if (ServiceConstants.IND_STAGE_CLOSE_Y.equals(lONNotInvDto.getCaseInfoDto().getIndStagePersReporter())) {
			FormDataGroupDto tmpRepFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REP,FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRepAddressList1 = new ArrayList<BookmarkDto>();
			
			
			BookmarkDto bookmarkCdNameSuffixWorker1 = createBookmarkWithCodesTable(
					BookmarkConstants.REP_NAME_SUFFIX, lONNotInvDto.getCaseInfoDto().getCdNameSuffix(),
					CodesConstant.CSUFFIX);
			BookmarkDto bookmarkNameFirst1 = createBookmark(BookmarkConstants.REP_NAME_FIRST,
					lONNotInvDto.getCaseInfoDto().getNmNameFirst());
			BookmarkDto bookmarkNameLast1 = createBookmark(BookmarkConstants.REP_NAME_LAST,
					lONNotInvDto.getCaseInfoDto().getNmNameLast());
			BookmarkDto bookmarkNameMiddle1 = createBookmark(BookmarkConstants.REP_NAME_MIDDLE,
					lONNotInvDto.getCaseInfoDto().getNmNameMiddle());
			bookmarkRepAddressList1.add(bookmarkCdNameSuffixWorker1);
			bookmarkRepAddressList1.add(bookmarkNameFirst1);
			bookmarkRepAddressList1.add(bookmarkNameLast1);
			bookmarkRepAddressList1.add(bookmarkNameMiddle1);

			tmpRepFrmDataGrpDto.setBookmarkDtoList(bookmarkRepAddressList1);
			formDataGroupList.add(tmpRepFrmDataGrpDto);
		}

		// set the prefill data for group cfiv2105

		if (ServiceConstants.JUNE_7_IMPACT_DATE.before(lONNotInvDto.getGenCaseInfoDto().getDtStageStart())
				|| ServiceConstants.JUNE_7_IMPACT_DATE.equals(lONNotInvDto.getGenCaseInfoDto().getDtStageStart())) {
			FormDataGroupDto dtStagePostFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_POST_JUNE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(dtStagePostFrmDataGrpDto);

			// set the prefill data for group cfiv2109
			FormDataGroupDto tempChildNameFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_FAC_POST2,
					FormGroupsConstants.TMPLAT_POST_JUNE);

			List<FormDataGroupDto> tempChildNameFrmDataGrpList = new ArrayList<FormDataGroupDto>();

			List<BookmarkDto> bookmarkChildNameList = new ArrayList<BookmarkDto>();

			for (MultiAddressDto multiAddressDtoListElem : lONNotInvDto.getMultiAddressDtoList()) {
				BookmarkDto bookmarkNmResource = createBookmark(BookmarkConstants.NM_FAC_POST2,
						multiAddressDtoListElem.getaCpNmResource());
				bookmarkChildNameList.add(bookmarkNmResource);
			}

			tempChildNameFrmDataGrpDto.setBookmarkDtoList(bookmarkChildNameList);
			tempChildNameFrmDataGrpList.add(tempChildNameFrmDataGrpDto);
			dtStagePostFrmDataGrpDto.setFormDataGroupList(tempChildNameFrmDataGrpList);
		}

		// set the prefill data for group cfiv2106

		if (ServiceConstants.JUNE_7_IMPACT_DATE.before(lONNotInvDto.getGenCaseInfoDto().getDtStageStart())
				|| ServiceConstants.JUNE_7_IMPACT_DATE.equals(lONNotInvDto.getGenCaseInfoDto().getDtStageStart())) {
			FormDataGroupDto dtStagePostFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_POST_JUNE2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(dtStagePostFrmDataGrpDto);

			// set the prefill data for group cfiv2110
			FormDataGroupDto tempChildNameFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_FAC_POST,
					FormGroupsConstants.TMPLAT_POST_JUNE2);

			List<FormDataGroupDto> tempChildNameFrmDataGrpList = new ArrayList<FormDataGroupDto>();

			List<BookmarkDto> bookmarkChildNameList = new ArrayList<BookmarkDto>();

			for (MultiAddressDto multiAddressDtoListElem : lONNotInvDto.getMultiAddressDtoList()) {
				BookmarkDto bookmarkNmResource = createBookmark(BookmarkConstants.NM_FAC_POST,
						multiAddressDtoListElem.getaCpNmResource());
				bookmarkChildNameList.add(bookmarkNmResource);
			}

			tempChildNameFrmDataGrpDto.setBookmarkDtoList(bookmarkChildNameList);
			tempChildNameFrmDataGrpList.add(tempChildNameFrmDataGrpDto);
			dtStagePostFrmDataGrpDto.setFormDataGroupList(tempChildNameFrmDataGrpList);
		}

	

		// bookmarks for CSEC01D

		BookmarkDto bookmarkCdNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.REP_WORKER_SUFFIX,
				lONNotInvDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
		bookmarkNonFormGrpList.add(bookmarkCdNameSuffix);
		BookmarkDto bookmarkNmNameFirst = createBookmark(BookmarkConstants.REP_WORKER_NAME_FIRST,
				lONNotInvDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonFormGrpList.add(bookmarkNmNameFirst);
		BookmarkDto bookmarkNmNameLast = createBookmark(BookmarkConstants.REP_WORKER_NAME_LAST,
				lONNotInvDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonFormGrpList.add(bookmarkNmNameLast);
		BookmarkDto bookmarkNmNameMiddle = createBookmark(BookmarkConstants.REP_WORKER_NAME_MIDDLE,
				lONNotInvDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonFormGrpList.add(bookmarkNmNameMiddle);

		// bookmarks for CSES39D
		BookmarkDto bookmarkDtFacilInvstBegun = createBookmark(BookmarkConstants.REP_SYSTEM_DATE,
				DateUtils.stringDt(lONNotInvDto.getFacilInvDtlDto().getDtFacilInvstBegun()));
		bookmarkNonFormGrpList.add(bookmarkDtFacilInvstBegun);
		BookmarkDto bookmarkDtFacilInvstIntake = createBookmark(BookmarkConstants.REP_DATE_INTAKE,
				DateUtils.stringDt(lONNotInvDto.getFacilInvDtlDto().getDtFacilInvstIntake()));
		bookmarkNonFormGrpList.add(bookmarkDtFacilInvstIntake);

		// bookmarks for CSEC02D
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.REP_CASE_NBR,
				lONNotInvDto.getGenCaseInfoDto().getIdCase());
		bookmarkNonFormGrpList.add(bookmarkIdCase);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);
		return preFillData;
	}

}
