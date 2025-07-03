package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.LetterWhenUtcDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: generates
 * prefill string for forms cfiv4800 and cfiv4900 Jul 6, 2018- 9:13:24 AM © 2017
 * Texas Department of Family and Protective Services
 * ********Change History**********
 * 05/18/2023 thompswa artf244344 PPM 73576 refactor and replace child list with case name.
 */
@Component
public class LetterWhenUtcPrefillData extends DocumentServiceUtil {
	
	private static String CFIV4900 = "cfiv4900";

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		LetterWhenUtcDto prefillDto = (LetterWhenUtcDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// Non-group bookmarks
		List<BookmarkDto> bookmarkNonGroupList = new ArrayList<BookmarkDto>();

		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();

		// Initialize null DTOs
		if (ObjectUtils.isEmpty(prefillDto.getCaseInfoList())) {
			prefillDto.setCaseInfoList(new ArrayList<CaseInfoDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getCodesTablesList())) {
			prefillDto.setCodesTablesList(new ArrayList<CodesTablesDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getStagePersonLinkList())) {
			prefillDto.setStagePersonLinkList(new ArrayList<StagePersonLinkDto>());
		}

		for (CodesTablesDto codesTablesDto : prefillDto.getCodesTablesList()) {
			// parent-group cfiv4821/cfiv4921
			if (FormConstants.SYSCODE.compareTo(codesTablesDto.getaCode()) < 0) {
				FormDataGroupDto headerBoardGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_BOARD, FormConstants.EMPTY_STRING);
				headerBoardGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.HEADER_BOARD_CITY, codesTablesDto.getaDecode())
						, createBookmark(BookmarkConstants.HEADER_BOARD_NAME, codesTablesDto.getbDecode())
				));
				formDataGroupList.add(headerBoardGroupDto);
			}
			// parent-group cfiv4822/cfiv4922
			else if (FormConstants.SYSCODE.equals(codesTablesDto.getaCode())) {
				FormDataGroupDto headerDirectorGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_HEADER_DIRECTOR, FormConstants.EMPTY_STRING);
				headerDirectorGroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE, codesTablesDto.getaDecode())
						, createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME, codesTablesDto.getbDecode())
				));
				formDataGroupList.add(headerDirectorGroupDto);
			}
		}

		// CCMN40D - letter recipient
		if (!ObjectUtils.isEmpty(prefillDto.getEmpNameDto())) {
			bookmarkNonGroupList.addAll(Arrays.asList(
				createBookmark(BookmarkConstants.NAME, TypeConvUtil.getNameWithSuffix(
						prefillDto.getEmpNameDto().getNmNameFirst(),
						prefillDto.getEmpNameDto().getNmNameMiddle(),
						prefillDto.getEmpNameDto().getNmNameLast(),
						prefillDto.getEmpNameDto().getCdNameSuffix(),
						false))));
		}

		// letter recipient address parent-group cfiv4861/cfiv4961
		if (!ObjectUtils.isEmpty(prefillDto.getAddressDto())) {
			List<FormDataGroupDto> personAddressGroupDtoList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto personAddressGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON_ADDRESS,
					FormConstants.EMPTY_STRING);
			personAddressGroupDto.setBookmarkDtoList(Arrays.asList(
					createBookmark(BookmarkConstants.PERSON_ADDRESS_LINE_1, prefillDto.getAddressDto().getAddrPersAddrStLn1())
					, createBookmark(BookmarkConstants.PERSON_ADDRESS_CITY, prefillDto.getAddressDto().getAddrCity())
					, createBookmarkWithCodesTable(BookmarkConstants.PERSON_ADDRESS_STATE,
							prefillDto.getAddressDto().getCdAddrState(), CodesConstant.CSTATE)
					, createBookmark(BookmarkConstants.PERSON_ADDRESS_ZIP, prefillDto.getAddressDto().getAddrZip())
			));
			// addr_ln2 sub-group
			if (StringUtils.isNotBlank(prefillDto.getAddressDto().getAddrPersAddrStLn2())) {
				FormDataGroupDto persAddrLn2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON_ADDRESS_LINE_2,
						FormGroupsConstants.TMPLAT_PERSON_ADDRESS);
				persAddrLn2GroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.PERSON_ADDRESS_LINE_2,
						prefillDto.getAddressDto().getAddrPersAddrStLn2())));
				personAddressGroupDtoList.add(persAddrLn2GroupDto);
			}
			personAddressGroupDto.setFormDataGroupList(personAddressGroupDtoList);
			formDataGroupList.add(personAddressGroupDto);
		}

		// CSEC02D - case/stage details
		if (!ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
			bookmarkNonGroupList.addAll(Arrays.asList(createBookmark(BookmarkConstants.SYSTEM_DATE,
							DateUtils.stringDt(prefillDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()))
					, createBookmark(BookmarkConstants.CASE_NUMBER, prefillDto.getGenericCaseInfoDto().getIdCase())
					, createBookmark(BookmarkConstants.CASE_NAME, prefillDto.getGenericCaseInfoDto().getNmCase())
			));
		}

		// salutation only for spanish version
		if (CFIV4900.equalsIgnoreCase(prefillDto.getFormName())) {
			// parent group cfiv4971
			if (!ServiceConstants.FEMALE.equals(prefillDto.getPersonGenderSpanishDto().getCdPersonSex())) {
				formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_DEAR_NONFEMALE, FormConstants.EMPTY_STRING));
			}
			// parent group cfiv4972
			else {
				formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_DEAR_FEMALE, FormConstants.EMPTY_STRING));
			}
		}
		// CSEC01D
		if (!ObjectUtils.isEmpty(prefillDto.getEmployeePersPhNameDto())) {
			bookmarkNonGroupList.addAll(Arrays.asList(
					createBookmark(BookmarkConstants.WORKER_NAME, TypeConvUtil.getNameWithSuffix(
							prefillDto.getEmployeePersPhNameDto().getNmNameFirst(),
							prefillDto.getEmployeePersPhNameDto().getNmNameMiddle(),
							prefillDto.getEmployeePersPhNameDto().getNmNameLast(),
							prefillDto.getEmployeePersPhNameDto().getCdNameSuffix(),
					false))
					, createBookmark(BookmarkConstants.WORKER_ADDRESS_LINE_1, prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1())
					, createBookmark(BookmarkConstants.WORKER_ADDRESS_CITY, prefillDto.getEmployeePersPhNameDto().getAddrMailCodeCity())
					, createBookmark(BookmarkConstants.WORKER_ADDRESS_ZIP, prefillDto.getEmployeePersPhNameDto().getAddrMailCodeZip())
					, createBookmark(BookmarkConstants.WORKER_PHONE, TypeConvUtil.getPhoneWithFormat(
							prefillDto.getEmployeePersPhNameDto().getNbrPhone(),
							prefillDto.getEmployeePersPhNameDto().getNbrPhoneExtension()))
			));
			// parent group cfiv4831/cfiv4931
			if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())) {
				FormDataGroupDto workerLn2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_STREET2, FormConstants.EMPTY_STRING);
				workerLn2GroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.WORKER_ADDRESS_LINE_2,
						prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())));
				formDataGroupList.add(workerLn2GroupDto);
			}
		}
		prefillData.setBookmarkDtoList(bookmarkNonGroupList);
		prefillData.setFormDataGroupList(formDataGroupList);

		return prefillData;
	}

}
