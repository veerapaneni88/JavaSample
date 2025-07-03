package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import us.tx.state.dfps.common.web.bean.PersonBean;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.common.util.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.person.dto.*;

import us.tx.state.dfps.service.admin.dto.PersonEthnicityOutDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersonComparisonPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for Form PER03O00 Apr 9, 2018- 10:53:14 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class PersonComparisonPrefillData extends DocumentServiceUtil {

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

	public static final String PRIMARY = "Primary: ";
	public static final String INVALID = "Invalid: ";
	public static final String TYPE = "Type: ";
	public static final String ADDRESS = "Address: ";
	public static final String PHONE_NO = "Phone: ";
	public static final String START_DATE = "Start Date: ";
	public static final String END_DATE = "End Date: ";
	public static final String COMMA = ",";
	public static final String NAME = "Name: ";
	public static final String FORWARD_NAME = "Forward Name: ";
	public static final String ID_PERSON_FORWARD = "ID Person Forward: ";
	public static final String CLOSED_NAME = "Closed Name: ";
	public static final String ID_PERSON_CLOSED = "ID Person Closed: ";
	public static final String MERGE_DATE = "Merge Date: ";

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		PersonComparisonDto personComparisonDto = (PersonComparisonDto) parentDtoobj;

		if (ObjectUtils.isEmpty(personComparisonDto.getAddressList())) {
			personComparisonDto.setAddressList(new ArrayList<AddressDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getAddressList2())) {
			personComparisonDto.setAddressList2(new ArrayList<AddressDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonPhoneRetDto())) {
			personComparisonDto.setPersonPhoneRetDto(new ArrayList<PersonPhoneRetDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonPhoneRetDto2())) {
			personComparisonDto.setPersonPhoneRetDto2(new ArrayList<PersonPhoneRetDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonRaceOutDtoList())) {
			personComparisonDto.setPersonRaceOutDtoList(new ArrayList<PersonRaceOutDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonRaceOutDtoList2())) {
			personComparisonDto.setPersonRaceOutDtoList2(new ArrayList<PersonRaceOutDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonEthnicityOutDtos())) {
			personComparisonDto.setPersonEthnicityOutDtos(new ArrayList<PersonEthnicityOutDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getCharDtlList())) {
			personComparisonDto.setCharDtlList(new ArrayList<CharacteristicsDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getCharDtlList2())) {
			personComparisonDto.setCharDtlList2(new ArrayList<CharacteristicsDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonEthnicityOutDtos2())) {
			personComparisonDto.setPersonEthnicityOutDtos2(new ArrayList<PersonEthnicityOutDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getNameHistoryDtlDtoList())) {
			personComparisonDto.setNameHistoryDtlDtoList(new ArrayList<NameHistoryDtlDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getNameHistoryDtlDtoList2())) {
			personComparisonDto.setNameHistoryDtlDtoList2(new ArrayList<NameHistoryDtlDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonIdDtoList())) {
			personComparisonDto.setPersonIdDtoList(new ArrayList<PersonIdDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonIdDtoList2())) {
			personComparisonDto.setPersonIdDtoList2(new ArrayList<PersonIdDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getEducationHistoryDtoList())) {
			personComparisonDto.setEducationHistoryDtoList(new ArrayList<EducationHistoryDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getEducationHistoryDtoList2())) {
			personComparisonDto.setEducationHistoryDtoList2(new ArrayList<EducationHistoryDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonMergeInfoDtoList())) {
			personComparisonDto.setPersonMergeInfoDtoList(new ArrayList<PersonMergeInfoDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonMergeInfoDtoList2())) {
			personComparisonDto.setPersonMergeInfoDtoList2(new ArrayList<PersonMergeInfoDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonEmailDtoList())) {
			personComparisonDto.setPersonEmailDtoList(new ArrayList<PersonEmailDto>());
		}
		//artf257687 - checking for null or empty then initialization the beans/list
		if (ObjectUtils.isEmpty(personComparisonDto.getPersForwardIdentifiers())) {
			personComparisonDto.setPersForwardIdentifiers(new ArrayList<PersonIdentifiersDto>());
		}

		if (ObjectUtils.isEmpty(personComparisonDto.getPersClosedIdentifiers())) {
			personComparisonDto.setPersClosedIdentifiers(new ArrayList<PersonIdentifiersDto>());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersForwardValueBean())) {
			personComparisonDto.setPersForwardValueBean(new PersonBean());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersClosedValueBean())) {
			personComparisonDto.setPersClosedValueBean(new PersonBean());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonPotentialDupDto())) {
			personComparisonDto.setPersonPotentialDupDto(new PersonPotentialDupDto());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonPotentialDupDto2())) {
			personComparisonDto.setPersonPotentialDupDto2(new PersonPotentialDupDto());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getEmployeeDetailDto())) {
			personComparisonDto.setEmployeeDetailDto(new EmployeeDetailDto());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getEmployeeDetailDto2())) {
			personComparisonDto.setEmployeeDetailDto2(new EmployeeDetailDto());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonMergeInfoDto())) {
			personComparisonDto.setPersonMergeInfoDto(new PersonMergeInfoDto());
		}
		if (ObjectUtils.isEmpty(personComparisonDto.getPersonMergeInfoDto2())) {
			personComparisonDto.setPersonMergeInfoDto2(new PersonMergeInfoDto());
		}
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// per0362
		if (!ObjectUtils.isEmpty(personComparisonDto.getPersonDto().getNbrPersonAge())
				&& personComparisonDto.getPersonDto().getNbrPersonAge() != 0) {
			FormDataGroupDto personAgeGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AGE1,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPersonAgeList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkPersonAge = createBookmark(BookmarkConstants.AGE1,
					personComparisonDto.getPersonDto().getNbrPersonAge());
			bookmarkPersonAgeList.add(bookmarkPersonAge);
			personAgeGroupDto.setBookmarkDtoList(bookmarkPersonAgeList);
			formDataGroupList.add(personAgeGroupDto);
		}

		// per0363
		if (!ObjectUtils.isEmpty(personComparisonDto.getPersonDto2().getNbrPersonAge())
				&& personComparisonDto.getPersonDto2().getNbrPersonAge() != 0) {
			FormDataGroupDto personAgeGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_AGE2,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPersonAgeList2 = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkPersonAge2 = createBookmark(BookmarkConstants.AGE2,
					personComparisonDto.getPersonDto2().getNbrPersonAge());
			bookmarkPersonAgeList2.add(bookmarkPersonAge2);
			personAgeGroupDto2.setBookmarkDtoList(bookmarkPersonAgeList2);
			formDataGroupList.add(personAgeGroupDto2);
		}

		// parent group per0358 :Merge close history for person 1 -
		// TMPLAT_MERGE_CLSD1

		if (!ObjectUtils.isEmpty(personComparisonDto.getPersonMergeInfoDto())) {

			FormDataGroupDto mergeGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MERGE_CLSD1,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(mergeGroupDto);

			List<FormDataGroupDto> mergeGroupList = new ArrayList<FormDataGroupDto>();
			mergeGroupDto.setFormDataGroupList(mergeGroupList);

			List<BookmarkDto> bookmarkMergeList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(personComparisonDto.getPersonMergeInfoDto().getNmPersonFirst())) {

				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.FORWARD_NAME, FORWARD_NAME);
				bookmarkMergeList.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.ID_PERSON_FORWARD, ID_PERSON_FORWARD);
				bookmarkMergeList.add(bookmarkInvalid);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.MERGE_DATE, MERGE_DATE);
				bookmarkMergeList.add(bookmarkEndDate);

				BookmarkDto bookmarkDtPersMerge = createBookmark(BookmarkConstants.MERGE_DT_1,
						personComparisonDto.getPersonMergeInfoDto().getDtPersMerge());
				bookmarkMergeList.add(bookmarkDtPersMerge);
				BookmarkDto bookmarkNmPersonFirstMerge = createBookmark(BookmarkConstants.NM_FIRST_FWD1,
						personComparisonDto.getPersonMergeInfoDto().getNmPersonFirst());
				bookmarkMergeList.add(bookmarkNmPersonFirstMerge);
				BookmarkDto bookmarkNmPersonLastMerge = createBookmark(BookmarkConstants.NM_LAST_FWD1,
						personComparisonDto.getPersonMergeInfoDto().getNmPersonLast());
				bookmarkMergeList.add(bookmarkNmPersonLastMerge);
				BookmarkDto bookmarkNmPersonMiddleMerge = createBookmark(BookmarkConstants.NM_MIDDLE_FWD1,
						personComparisonDto.getPersonMergeInfoDto().getNmPersonMiddle());
				bookmarkMergeList.add(bookmarkNmPersonMiddleMerge);
				BookmarkDto bookmarkIdPersonMerge = createBookmark(BookmarkConstants.ID_FWD1,
						personComparisonDto.getPersonMergeInfoDto().getIdPersMergeFrwrd());
				bookmarkMergeList.add(bookmarkIdPersonMerge);

				mergeGroupDto.setBookmarkDtoList(bookmarkMergeList);

				// sub group per0359 :parent group per0358
				FormDataGroupDto closeGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_CLOSE1,
						FormGroupsConstants.TMPLAT_MERGE_CLSD1);
				mergeGroupList.add(closeGroupDto);
				List<BookmarkDto> bookmarkCloseList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkNmPersonFirstClose = createBookmark(BookmarkConstants.NM_FIRST_CLD1,
						personComparisonDto.getPersonDto().getNmPersonFirst());
				bookmarkCloseList.add(bookmarkNmPersonFirstClose);
				BookmarkDto bookmarkNmPersonLastClose = createBookmark(BookmarkConstants.NM_LAST_CLD1,
						personComparisonDto.getPersonDto().getNmPersonLast());
				bookmarkCloseList.add(bookmarkNmPersonLastClose);
				BookmarkDto bookmarkNmPersonMiddleClose = createBookmark(BookmarkConstants.NM_MIDDLE_CLD1,
						personComparisonDto.getPersonDto().getNmPersonMiddle());
				bookmarkCloseList.add(bookmarkNmPersonMiddleClose);
				BookmarkDto bookmarkIdPersonClose = createBookmark(BookmarkConstants.ID_CLD1,
						personComparisonDto.getPersonDto().getIdPerson());
				bookmarkCloseList.add(bookmarkIdPersonClose);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.CLOSED_NAME, CLOSED_NAME);
				bookmarkCloseList.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.ID_PERSON_CLOSED, ID_PERSON_CLOSED);
				bookmarkCloseList.add(bookmarkStartDate);

				closeGroupDto.setBookmarkDtoList(bookmarkCloseList);
			}

		}

		if (!ObjectUtils.isEmpty(personComparisonDto.getPersonMergeInfoDto2())) {

			// parent group per0360 :Merge close history for person 2 -
			// TMPLAT_MERGE_CLSD2

			FormDataGroupDto mergeGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_MERGE_CLSD2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(mergeGroupDto2);

			List<FormDataGroupDto> mergeGroupList2 = new ArrayList<FormDataGroupDto>();
			mergeGroupDto2.setFormDataGroupList(mergeGroupList2);

			List<BookmarkDto> bookmarkMergeList2 = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(personComparisonDto.getPersonMergeInfoDto().getNmPersonFirst())) {

				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.FORWARD_NAME, FORWARD_NAME);
				bookmarkMergeList2.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.ID_PERSON_FORWARD, ID_PERSON_FORWARD);
				bookmarkMergeList2.add(bookmarkInvalid);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.MERGE_DATE, MERGE_DATE);
				bookmarkMergeList2.add(bookmarkEndDate);

				BookmarkDto bookmarkDtPersMerge2 = createBookmark(BookmarkConstants.MERGE_DT_2,
						personComparisonDto.getPersonMergeInfoDto2().getDtPersMerge());
				bookmarkMergeList2.add(bookmarkDtPersMerge2);
				BookmarkDto bookmarkNmPersonFirstMerge2 = createBookmark(BookmarkConstants.NM_FIRST_NAME_FWD2,
						personComparisonDto.getPersonMergeInfoDto2().getNmPersonFirst());
				bookmarkMergeList2.add(bookmarkNmPersonFirstMerge2);
				BookmarkDto bookmarkNmPersonLastMerge2 = createBookmark(BookmarkConstants.NM_LAST_FWD2,
						personComparisonDto.getPersonMergeInfoDto2().getNmPersonLast());
				bookmarkMergeList2.add(bookmarkNmPersonLastMerge2);
				BookmarkDto bookmarkNmPersonMiddleMerge2 = createBookmark(BookmarkConstants.NM_MIDDLE_FWD2,
						personComparisonDto.getPersonMergeInfoDto2().getNmPersonMiddle());
				bookmarkMergeList2.add(bookmarkNmPersonMiddleMerge2);
				BookmarkDto bookmarkIdPersonMerge2 = createBookmark(BookmarkConstants.ID_FWD2,
						personComparisonDto.getPersonMergeInfoDto2().getIdPersMergeFrwrd());
				bookmarkMergeList2.add(bookmarkIdPersonMerge2);
				mergeGroupDto2.setBookmarkDtoList(bookmarkMergeList2);

				// sub group per0361 :parent group per0360
				FormDataGroupDto closeGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_CLOSE2,
						FormGroupsConstants.TMPLAT_MERGE_CLSD2);
				mergeGroupList2.add(closeGroupDto2);
				List<BookmarkDto> bookmarkCloseList2 = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkNmPersonFirstClose2 = createBookmark(BookmarkConstants.NM_FIRST_CLD2,
						personComparisonDto.getPersonDto2().getNmPersonFirst());
				bookmarkCloseList2.add(bookmarkNmPersonFirstClose2);
				BookmarkDto bookmarkNmPersonLastClose2 = createBookmark(BookmarkConstants.NM_LAST_CLD2,
						personComparisonDto.getPersonDto2().getNmPersonLast());
				bookmarkCloseList2.add(bookmarkNmPersonLastClose2);
				BookmarkDto bookmarkNmPersonMiddleClose2 = createBookmark(BookmarkConstants.NM_MIDDLE_CLD2,
						personComparisonDto.getPersonDto2().getNmPersonMiddle());
				bookmarkCloseList2.add(bookmarkNmPersonMiddleClose2);
				BookmarkDto bookmarkIdPersonClose2 = createBookmark(BookmarkConstants.ID_CLD2,
						personComparisonDto.getPersonDto2().getIdPerson());
				bookmarkCloseList2.add(bookmarkIdPersonClose2);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.CLOSED_NAME, CLOSED_NAME);
				bookmarkCloseList2.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.ID_PERSON_CLOSED, ID_PERSON_CLOSED);
				bookmarkCloseList2.add(bookmarkStartDate);

				closeGroupDto2.setBookmarkDtoList(bookmarkCloseList2);

			}
		}

		// per0380 : Citizenship status display for person 1 - TMPLAT_CIT_DISP1
		// : CCMN69D
		FormDataGroupDto citGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CIT_DISP1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(citGroupDto);

		List<FormDataGroupDto> citGroupList = new ArrayList<FormDataGroupDto>();
		citGroupDto.setFormDataGroupList(citGroupList);

		// sub group per0381 :Citizenship status for person 1 - TMPLAT_CIT_1
		FormDataGroupDto dispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CIT_1,
				FormGroupsConstants.TMPLAT_CIT_DISP1);
		citGroupList.add(dispGroupDto);
		List<BookmarkDto> bookmarkdispList = new ArrayList<BookmarkDto>();

		if (!ObjectUtils.isEmpty(personComparisonDto.getPersonDtlDto())) {
			BookmarkDto bookmarkCdPersonCitizenship = createBookmarkWithCodesTable(BookmarkConstants.CIT1,
					personComparisonDto.getPersonDtlDto().getCdPersonCitizenship(), CodesConstant.CCTZNSTA);
			bookmarkdispList.add(bookmarkCdPersonCitizenship);
		}

		citGroupDto.setBookmarkDtoList(bookmarkdispList);

		// per0382 : Citizenship status display for person 2 - TMPLAT_CIT_DISP2
		// : CCMN69D
		FormDataGroupDto citGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_CIT_DISP2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(citGroupDto2);

		List<FormDataGroupDto> citGroupList2 = new ArrayList<FormDataGroupDto>();
		citGroupDto2.setFormDataGroupList(citGroupList2);

		// sub group per0383 :Citizenship status for person 2 - TMPLAT_CIT_2
		FormDataGroupDto dispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_CIT_2,
				FormGroupsConstants.TMPLAT_CIT_DISP2);
		citGroupList2.add(dispGroupDto2);
		List<BookmarkDto> bookmarkdispList2 = new ArrayList<BookmarkDto>();

		if (!ObjectUtils.isEmpty(personComparisonDto.getPersonDtlDto2())) {
			BookmarkDto bookmarkCdPersonCitizenship2 = createBookmarkWithCodesTable(BookmarkConstants.CIT2,
					personComparisonDto.getPersonDtlDto2().getCdPersonCitizenship(), CodesConstant.CCTZNSTA);
			bookmarkdispList2.add(bookmarkCdPersonCitizenship2);
		}

		citGroupDto2.setBookmarkDtoList(bookmarkdispList2);

		// per0303 : Phone history for person 1 - TMPLAT_PHONE1
		for (PersonPhoneRetDto personPhoneRetDto : personComparisonDto.getPersonPhoneRetDto()) {
			FormDataGroupDto phoneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PHONE1,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPhoneList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(personPhoneRetDto.getPersonPhone())) {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, PRIMARY);
				bookmarkPhoneList.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, INVALID);
				bookmarkPhoneList.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, TYPE);
				bookmarkPhoneList.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE, START_DATE);
				bookmarkPhoneList.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, END_DATE);
				bookmarkPhoneList.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.PHONE_NO, PHONE_NO);
				bookmarkPhoneList.add(bookmarkAddress);

			} else {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, ServiceConstants.FORM_SPACE);
				bookmarkPhoneList.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, ServiceConstants.FORM_SPACE);
				bookmarkPhoneList.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, ServiceConstants.FORM_SPACE);
				bookmarkPhoneList.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE,
						ServiceConstants.FORM_SPACE);
				bookmarkPhoneList.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, ServiceConstants.FORM_SPACE);
				bookmarkPhoneList.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.PHONE_NO, ServiceConstants.FORM_SPACE);
				bookmarkPhoneList.add(bookmarkAddress);

			}

			BookmarkDto bookmarkCdPersonPhoneInvalid = createBookmark(BookmarkConstants.INVAL_PHONE1,
					personPhoneRetDto.getIndPersonPhoneInvalid());
			bookmarkPhoneList.add(bookmarkCdPersonPhoneInvalid);
			BookmarkDto bookmarkCdPersonPhonePrimary = createBookmark(BookmarkConstants.PRIM_PHONE1,
					personPhoneRetDto.getIndPersonPhonePrimary());
			bookmarkPhoneList.add(bookmarkCdPersonPhonePrimary);
			BookmarkDto bookmarkDtPersonPhoneEnd = createBookmark(BookmarkConstants.PHONE_ENDDT_1,
					DateUtils.stringDt(personPhoneRetDto.getDtPersonPhoneEnd())
							.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
									: DateUtils.stringDt(personPhoneRetDto.getDtPersonPhoneEnd()));
			bookmarkPhoneList.add(bookmarkDtPersonPhoneEnd);
			BookmarkDto bookmarkDtPersonPhoneStart = createBookmark(BookmarkConstants.PHONE_STARTDT_1,
					DateUtils.stringDt(personPhoneRetDto.getDtPersonPhoneStart()));
			bookmarkPhoneList.add(bookmarkDtPersonPhoneStart);
			BookmarkDto bookmarkCdPersonPhone = createBookmark(BookmarkConstants.NBR_PHONE_1,
					TypeConvUtil.formatPhone(personPhoneRetDto.getPersonPhone()));
			bookmarkPhoneList.add(bookmarkCdPersonPhone);
			BookmarkDto bookmarkCdPersonPhoneType = createBookmarkWithCodesTable(BookmarkConstants.TYPE_PHONE1,
					personPhoneRetDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
			bookmarkPhoneList.add(bookmarkCdPersonPhoneType);

			phoneGroupDto.setBookmarkDtoList(bookmarkPhoneList);
			formDataGroupList.add(phoneGroupDto);
		}

		// per0304 : Phone history for person 2 - TMPLAT_PHONE2
		for (PersonPhoneRetDto personPhoneRetDto : personComparisonDto.getPersonPhoneRetDto2()) {
			FormDataGroupDto phoneGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_PHONE2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(phoneGroupDto2);

			List<BookmarkDto> bookmarkPhoneList2 = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(personPhoneRetDto.getPersonPhone())) {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, PRIMARY);
				bookmarkPhoneList2.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, INVALID);
				bookmarkPhoneList2.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, TYPE);
				bookmarkPhoneList2.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE, START_DATE);
				bookmarkPhoneList2.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, END_DATE);
				bookmarkPhoneList2.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.PHONE_NO, PHONE_NO);
				bookmarkPhoneList2.add(bookmarkAddress);

			} else {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, ServiceConstants.FORM_SPACE);
				bookmarkPhoneList2.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, ServiceConstants.FORM_SPACE);
				bookmarkPhoneList2.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, ServiceConstants.FORM_SPACE);
				bookmarkPhoneList2.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE,
						ServiceConstants.FORM_SPACE);
				bookmarkPhoneList2.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, ServiceConstants.FORM_SPACE);
				bookmarkPhoneList2.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.PHONE_NO, ServiceConstants.FORM_SPACE);
				bookmarkPhoneList2.add(bookmarkAddress);

			}

			BookmarkDto bookmarkCdPersonPhoneInvalid2 = createBookmark(BookmarkConstants.INVAL_PHONE2,
					personPhoneRetDto.getIndPersonPhoneInvalid());
			bookmarkPhoneList2.add(bookmarkCdPersonPhoneInvalid2);
			BookmarkDto bookmarkCdPersonPhonePrimary2 = createBookmark(BookmarkConstants.PRIM_PHONE2,
					personPhoneRetDto.getIndPersonPhonePrimary());
			bookmarkPhoneList2.add(bookmarkCdPersonPhonePrimary2);
			BookmarkDto bookmarkDtPersonPhoneEnd2 = createBookmark(BookmarkConstants.PHONE_ENDDT_2,
					DateUtils.stringDt(personPhoneRetDto.getDtPersonPhoneEnd())
							.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
									: DateUtils.stringDt(personPhoneRetDto.getDtPersonPhoneEnd()));
			bookmarkPhoneList2.add(bookmarkDtPersonPhoneEnd2);
			BookmarkDto bookmarkDtPersonPhoneStart2 = createBookmark(BookmarkConstants.PHONE_STARTDT_2,
					DateUtils.stringDt(personPhoneRetDto.getDtPersonPhoneStart()));
			bookmarkPhoneList2.add(bookmarkDtPersonPhoneStart2);
			BookmarkDto bookmarkCdPersonPhone2 = createBookmark(BookmarkConstants.NBR_PHONE_2,
					TypeConvUtil.formatPhone(personPhoneRetDto.getPersonPhone()));
			bookmarkPhoneList2.add(bookmarkCdPersonPhone2);
			BookmarkDto bookmarkCdPersonPhoneType2 = createBookmarkWithCodesTable(BookmarkConstants.TYPE_PHONE2,
					personPhoneRetDto.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
			bookmarkPhoneList2.add(bookmarkCdPersonPhoneType2);

			phoneGroupDto2.setBookmarkDtoList(bookmarkPhoneList2);
		}

		// per0319 : Name history for person 1 - TMPLAT_NAME_HIST1
		for (NameHistoryDtlDto nameHistoryDtlDto : personComparisonDto.getNameHistoryDtlDtoList()) {
			FormDataGroupDto nameGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NAME_HIST1,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(nameGroupDto);

			List<BookmarkDto> bookmarkNameList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(nameHistoryDtlDto.getNmFirstName())) {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, PRIMARY);
				bookmarkNameList.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, INVALID);
				bookmarkNameList.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, TYPE);
				bookmarkNameList.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE, START_DATE);
				bookmarkNameList.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, END_DATE);
				bookmarkNameList.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.NAME, NAME);
				bookmarkNameList.add(bookmarkAddress);

			} else {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, ServiceConstants.FORM_SPACE);
				bookmarkNameList.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, ServiceConstants.FORM_SPACE);
				bookmarkNameList.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, ServiceConstants.FORM_SPACE);
				bookmarkNameList.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE,
						ServiceConstants.FORM_SPACE);
				bookmarkNameList.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, ServiceConstants.FORM_SPACE);
				bookmarkNameList.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.NAME, ServiceConstants.FORM_SPACE);
				bookmarkNameList.add(bookmarkAddress);

			}

			BookmarkDto bookmarkNameInvalid = createBookmark(BookmarkConstants.INVAL_NAME_HIST1,
					nameHistoryDtlDto.getIndNameInvalid());
			bookmarkNameList.add(bookmarkNameInvalid);
			BookmarkDto bookmarkNamePrimary = createBookmark(BookmarkConstants.PRIM_NAME_HIST1,
					nameHistoryDtlDto.getIndNamePrimary());
			bookmarkNameList.add(bookmarkNamePrimary);
			BookmarkDto bookmarkDtNameEnd = createBookmark(BookmarkConstants.NAME_HIST_ENDDT_1,
					DateUtils.stringDt(nameHistoryDtlDto.getDtNameEnd())
							.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
									: DateUtils.stringDt(nameHistoryDtlDto.getDtNameEnd()));
			bookmarkNameList.add(bookmarkDtNameEnd);
			BookmarkDto bookmarkDtNameStart = createBookmark(BookmarkConstants.NAME_HIST_STARTDT_1,
					DateUtils.stringDt(nameHistoryDtlDto.getDtNameStart()));
			bookmarkNameList.add(bookmarkDtNameStart);
			BookmarkDto bookmarkNmFirstName = createBookmark(BookmarkConstants.NM_FIRST_NAME_HIST1,
					nameHistoryDtlDto.getNmFirstName());
			bookmarkNameList.add(bookmarkNmFirstName);
			BookmarkDto bookmarkNmLastName = createBookmark(BookmarkConstants.NM_LAST_NAME_HIST1,
					nameHistoryDtlDto.getNmLastName());
			bookmarkNameList.add(bookmarkNmLastName);
			BookmarkDto bookmarkNmMiddleName = createBookmark(BookmarkConstants.NM_MIDDLE_NAME_HIST1,
					nameHistoryDtlDto.getNmMiddleName());
			bookmarkNameList.add(bookmarkNmMiddleName);

			nameGroupDto.setBookmarkDtoList(bookmarkNameList);
		}

		// per0320 : Name history for person 2 - TMPLAT_NAME_HIST2
		for (NameHistoryDtlDto nameHistoryDtlDto : personComparisonDto.getNameHistoryDtlDtoList2()) {
			FormDataGroupDto nameGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_NAME_HIST2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(nameGroupDto2);

			List<BookmarkDto> bookmarkNameList2 = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(nameHistoryDtlDto.getNmFirstName())) {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, PRIMARY);
				bookmarkNameList2.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, INVALID);
				bookmarkNameList2.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, TYPE);
				bookmarkNameList2.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE, START_DATE);
				bookmarkNameList2.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, END_DATE);
				bookmarkNameList2.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.NAME, NAME);
				bookmarkNameList2.add(bookmarkAddress);

			} else {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, ServiceConstants.FORM_SPACE);
				bookmarkNameList2.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, ServiceConstants.FORM_SPACE);
				bookmarkNameList2.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, ServiceConstants.FORM_SPACE);
				bookmarkNameList2.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE,
						ServiceConstants.FORM_SPACE);
				bookmarkNameList2.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, ServiceConstants.FORM_SPACE);
				bookmarkNameList2.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.NAME, ServiceConstants.FORM_SPACE);
				bookmarkNameList2.add(bookmarkAddress);

			}

			BookmarkDto bookmarkNameInvalid2 = createBookmark(BookmarkConstants.INVAL_NAME_HIST2,
					nameHistoryDtlDto.getIndNameInvalid());
			bookmarkNameList2.add(bookmarkNameInvalid2);
			BookmarkDto bookmarkNamePrimary2 = createBookmark(BookmarkConstants.PRIM_NAME_HIST2,
					nameHistoryDtlDto.getIndNamePrimary());
			bookmarkNameList2.add(bookmarkNamePrimary2);
			BookmarkDto bookmarkDtNameEnd2 = createBookmark(BookmarkConstants.NAME_HIST_ENDDT_2,
					DateUtils.stringDt(nameHistoryDtlDto.getDtNameEnd())
							.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
									: DateUtils.stringDt(nameHistoryDtlDto.getDtNameEnd()));
			bookmarkNameList2.add(bookmarkDtNameEnd2);
			BookmarkDto bookmarkDtNameStart2 = createBookmark(BookmarkConstants.NAME_HIST_STARTDT_2,
					DateUtils.stringDt(nameHistoryDtlDto.getDtNameStart()));
			bookmarkNameList2.add(bookmarkDtNameStart2);
			BookmarkDto bookmarkNmFirstName2 = createBookmark(BookmarkConstants.NM_FIRST_NAME_HIST2,
					nameHistoryDtlDto.getNmFirstName());
			bookmarkNameList2.add(bookmarkNmFirstName2);
			BookmarkDto bookmarkNmLastName2 = createBookmark(BookmarkConstants.NM_LAST_NAME_HIST2,
					nameHistoryDtlDto.getNmLastName());
			bookmarkNameList2.add(bookmarkNmLastName2);
			BookmarkDto bookmarkNmMiddleName2 = createBookmark(BookmarkConstants.NM_MIDDLE_NAME_HIST2,
					nameHistoryDtlDto.getNmMiddleName());
			bookmarkNameList2.add(bookmarkNmMiddleName2);

			nameGroupDto2.setBookmarkDtoList(bookmarkNameList2);
		}

		// per0329 : Person merge history for person 1 - TMPLAT_MERGE1
		for (PersonMergeInfoDto personMergeInfoDto : personComparisonDto.getPersonMergeInfoDtoList()) {

			FormDataGroupDto mergeHistGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MERGE1,
					FormConstants.EMPTY_STRING);

			List<BookmarkDto> bookmarkMergeHistList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(personMergeInfoDto.getNmPersonFirst())) {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.FORWARD_NAME, FORWARD_NAME);
				bookmarkMergeHistList.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.ID_PERSON_FORWARD, ID_PERSON_FORWARD);
				bookmarkMergeHistList.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.CLOSED_NAME, CLOSED_NAME);
				bookmarkMergeHistList.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.ID_PERSON_CLOSED, ID_PERSON_CLOSED);
				bookmarkMergeHistList.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.MERGE_DATE, MERGE_DATE);
				bookmarkMergeHistList.add(bookmarkEndDate);

				BookmarkDto bookmarkDtPersMergeHist = createBookmark(BookmarkConstants.MERGE_DT_1,
						DateUtils.stringDt(personMergeInfoDto.getDtPersMerge()));
				bookmarkMergeHistList.add(bookmarkDtPersMergeHist);
				BookmarkDto bookmarkNmFirstName = createBookmark(BookmarkConstants.NM_FIRST_FWD1,
						personMergeInfoDto.getNmPersonFirst());
				bookmarkMergeHistList.add(bookmarkNmFirstName);
				BookmarkDto bookmarkNmLastName = createBookmark(BookmarkConstants.NM_LAST_FWD1,
						personMergeInfoDto.getNmPersonLast());
				bookmarkMergeHistList.add(bookmarkNmLastName);
				BookmarkDto bookmarkNmMiddleName = createBookmark(BookmarkConstants.NM_MIDDLE_FWD1,
						personMergeInfoDto.getNmPersonMiddle());
				bookmarkMergeHistList.add(bookmarkNmMiddleName);
				BookmarkDto bookmarkIdPersonMergeHist = createBookmark(BookmarkConstants.ID_FWD1,
						personMergeInfoDto.getIdPersMergeFrwrd());
				bookmarkMergeHistList.add(bookmarkIdPersonMergeHist);
				BookmarkDto bookmarkNmFirstNameC = createBookmark(BookmarkConstants.NM_FIRST_CLD1,
						personMergeInfoDto.getNmPersonFirst2());
				bookmarkMergeHistList.add(bookmarkNmFirstNameC);
				BookmarkDto bookmarkNmLastNameC = createBookmark(BookmarkConstants.NM_LAST_CLD1,
						personMergeInfoDto.getNmPersonLast2());
				bookmarkMergeHistList.add(bookmarkNmLastNameC);
				BookmarkDto bookmarkNmMiddleNameC = createBookmark(BookmarkConstants.NM_MIDDLE_CLD1,
						personMergeInfoDto.getNmPersonMiddle2());
				bookmarkMergeHistList.add(bookmarkNmMiddleNameC);
				BookmarkDto bookmarkIdPersonMergeHistC = createBookmark(BookmarkConstants.ID_CLD1,
						personMergeInfoDto.getIdPersMergeClsd());
				bookmarkMergeHistList.add(bookmarkIdPersonMergeHistC);

				mergeHistGroupDto.setBookmarkDtoList(bookmarkMergeHistList);
			} else {

				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.FORWARD_NAME,
						ServiceConstants.FORM_SPACE);
				bookmarkMergeHistList.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.ID_PERSON_FORWARD,
						ServiceConstants.FORM_SPACE);
				bookmarkMergeHistList.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.CLOSED_NAME, ServiceConstants.FORM_SPACE);
				bookmarkMergeHistList.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.ID_PERSON_CLOSED,
						ServiceConstants.FORM_SPACE);
				bookmarkMergeHistList.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.MERGE_DATE, ServiceConstants.FORM_SPACE);
				bookmarkMergeHistList.add(bookmarkEndDate);

				mergeHistGroupDto.setBookmarkDtoList(bookmarkMergeHistList);

			}
			formDataGroupList.add(mergeHistGroupDto);
		}

		// per0330 : Person merge history for person 2 - TMPLAT_MERGE2
		for (PersonMergeInfoDto personMergeInfoDto : personComparisonDto.getPersonMergeInfoDtoList2()) {

			FormDataGroupDto mergeHistGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_MERGE2,
					FormConstants.EMPTY_STRING);

			List<BookmarkDto> bookmarkMergeHistList2 = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(personMergeInfoDto.getNmPersonFirst())) {

				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.FORWARD_NAME, FORWARD_NAME);
				bookmarkMergeHistList2.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.ID_PERSON_FORWARD, ID_PERSON_FORWARD);
				bookmarkMergeHistList2.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.CLOSED_NAME, CLOSED_NAME);
				bookmarkMergeHistList2.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.ID_PERSON_CLOSED, ID_PERSON_CLOSED);
				bookmarkMergeHistList2.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.MERGE_DATE, MERGE_DATE);
				bookmarkMergeHistList2.add(bookmarkEndDate);

				BookmarkDto bookmarkDtPersMergeHist2 = createBookmark(BookmarkConstants.MERGE_DT_2,
						DateUtils.stringDt(personMergeInfoDto.getDtPersMerge()));
				bookmarkMergeHistList2.add(bookmarkDtPersMergeHist2);
				BookmarkDto bookmarkNmFirstName2 = createBookmark(BookmarkConstants.NM_FIRST_NAME_FWD2,
						personMergeInfoDto.getNmPersonFirst());
				bookmarkMergeHistList2.add(bookmarkNmFirstName2);
				BookmarkDto bookmarkNmLastName2 = createBookmark(BookmarkConstants.NM_LAST_FWD2,
						personMergeInfoDto.getNmPersonLast());
				bookmarkMergeHistList2.add(bookmarkNmLastName2);
				BookmarkDto bookmarkNmMiddleName2 = createBookmark(BookmarkConstants.NM_MIDDLE_FWD2,
						personMergeInfoDto.getNmPersonMiddle());
				bookmarkMergeHistList2.add(bookmarkNmMiddleName2);
				BookmarkDto bookmarkIdPersonMergeHist2 = createBookmark(BookmarkConstants.ID_FWD2,
						personMergeInfoDto.getIdPersMergeFrwrd());
				bookmarkMergeHistList2.add(bookmarkIdPersonMergeHist2);
				BookmarkDto bookmarkNmFirstNameC2 = createBookmark(BookmarkConstants.NM_FIRST_CLD2,
						personMergeInfoDto.getNmPersonFirst2());
				bookmarkMergeHistList2.add(bookmarkNmFirstNameC2);
				BookmarkDto bookmarkNmLastNameC2 = createBookmark(BookmarkConstants.NM_LAST_CLD2,
						personMergeInfoDto.getNmPersonLast2());
				bookmarkMergeHistList2.add(bookmarkNmLastNameC2);
				BookmarkDto bookmarkNmMiddleNameC2 = createBookmark(BookmarkConstants.NM_MIDDLE_CLD2,
						personMergeInfoDto.getNmPersonMiddle2());
				bookmarkMergeHistList2.add(bookmarkNmMiddleNameC2);
				BookmarkDto bookmarkIdPersonMergeHistC2 = createBookmark(BookmarkConstants.ID_CLD2,
						personMergeInfoDto.getIdPersMergeClsd());
				bookmarkMergeHistList2.add(bookmarkIdPersonMergeHistC2);

				mergeHistGroupDto2.setBookmarkDtoList(bookmarkMergeHistList2);

			} else {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.FORWARD_NAME,
						ServiceConstants.FORM_SPACE);
				bookmarkMergeHistList2.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.ID_PERSON_FORWARD,
						ServiceConstants.FORM_SPACE);
				bookmarkMergeHistList2.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.CLOSED_NAME, ServiceConstants.FORM_SPACE);
				bookmarkMergeHistList2.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.ID_PERSON_CLOSED,
						ServiceConstants.FORM_SPACE);
				bookmarkMergeHistList2.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.MERGE_DATE, ServiceConstants.FORM_SPACE);
				bookmarkMergeHistList2.add(bookmarkEndDate);

				mergeHistGroupDto2.setBookmarkDtoList(bookmarkMergeHistList2);
			}
			formDataGroupList.add(mergeHistGroupDto2);
		}

		// per0333 : Potential duplicates for person 1 - TMPLAT_POTENTIAL_DUP1

		if (!ObjectUtils.isEmpty(personComparisonDto.getPersonPotentialDupDto())) {

			FormDataGroupDto dupGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_POTENTIAL_DUP1,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(dupGroupDto);

			List<BookmarkDto> bookmarkDupList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkDtCreated = createBookmark(BookmarkConstants.DUP_DT_CREATE1,
					DateUtils.stringDt(personComparisonDto.getPersonPotentialDupDto().getDtCreated()));
			bookmarkDupList.add(bookmarkDtCreated);
			BookmarkDto bookmarkDtEnd = createBookmark(BookmarkConstants.DUP_DT_END1,
					DateUtils.stringDt(personComparisonDto.getPersonPotentialDupDto().getDtEnd())
							.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
									: DateUtils.stringDt(personComparisonDto.getPersonPotentialDupDto().getDtEnd()));
			bookmarkDupList.add(bookmarkDtEnd);
			BookmarkDto bookmarkReasonNotMerged = createBookmarkWithCodesTable(BookmarkConstants.RSN_NOT_MERGE1,
					personComparisonDto.getPersonPotentialDupDto().getCdReasonNotMerged(), CodesConstant.CRSNNOMG);
			bookmarkDupList.add(bookmarkReasonNotMerged);
			BookmarkDto bookmarkIdDupPerson = createBookmark(BookmarkConstants.DUP_ID1,
					personComparisonDto.getPersonPotentialDupDto().getIdDupPerson());
			bookmarkDupList.add(bookmarkIdDupPerson);

			dupGroupDto.setBookmarkDtoList(bookmarkDupList);
		}

		// per0334 : Potential duplicates for person 2 - TMPLAT_POTENTIAL_DUP2

		if (!ObjectUtils.isEmpty(personComparisonDto.getPersonPotentialDupDto2())) {

			FormDataGroupDto dupGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_POTENTIAL_DUP2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(dupGroupDto2);

			List<BookmarkDto> bookmarkDupList2 = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkDtCreated2 = createBookmark(BookmarkConstants.DUP_DT_CREATE2,
					DateUtils.stringDt(personComparisonDto.getPersonPotentialDupDto2().getDtCreated()));
			bookmarkDupList2.add(bookmarkDtCreated2);
			BookmarkDto bookmarkDtEnd2 = createBookmark(BookmarkConstants.DUP_DT_END2,
					DateUtils.stringDt(personComparisonDto.getPersonPotentialDupDto2().getDtEnd())
							.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
									: DateUtils.stringDt(personComparisonDto.getPersonPotentialDupDto2().getDtEnd()));
			bookmarkDupList2.add(bookmarkDtEnd2);
			BookmarkDto bookmarkReasonNotMerged2 = createBookmarkWithCodesTable(BookmarkConstants.RSN_NOT_MERGE2,
					personComparisonDto.getPersonPotentialDupDto2().getCdReasonNotMerged(), CodesConstant.CRSNNOMG);
			bookmarkDupList2.add(bookmarkReasonNotMerged2);
			BookmarkDto bookmarkIdDupPerson2 = createBookmark(BookmarkConstants.DUP_ID2,
					personComparisonDto.getPersonPotentialDupDto2().getIdDupPerson());
			bookmarkDupList2.add(bookmarkIdDupPerson2);

			dupGroupDto2.setBookmarkDtoList(bookmarkDupList2);
		}

		// parent group per0335 :SSN display for person 1 (non-employee) -
		// TMPLAT_SSN_DISP1
		FormDataGroupDto ssnDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SSN_DISP1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(ssnDispGroupDto);

		List<FormDataGroupDto> ssnDispGroupList = new ArrayList<FormDataGroupDto>();
		ssnDispGroupDto.setFormDataGroupList(ssnDispGroupList);

		// sub group per0336 :parent group per0335
		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersForwardIdentifiers()) {
			if (ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())
					&& ServiceConstants.SOCIAL_SECURITY.equals(personIdDto.getPersonIdType())
					&& ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())) {
				FormDataGroupDto ssnGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SSN_1,
						FormGroupsConstants.TMPLAT_SSN_DISP1);
				ssnDispGroupList.add(ssnGroupDto);
				List<BookmarkDto> bookmarkSsnList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkIdNumber = createBookmark(BookmarkConstants.SSN1, personIdDto.getPersonIdNumber());
				bookmarkSsnList.add(bookmarkIdNumber);

				ssnGroupDto.setBookmarkDtoList(bookmarkSsnList);
			}
		}




			// sub group per0397 :parent group per0335
		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersForwardIdentifiers()) {
			if (ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())
					&& ServiceConstants.SOCIAL_SECURITY.equals(personIdDto.getPersonIdType())
					&& ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())) {
				FormDataGroupDto methodGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SSN_METHOD_1,
						FormGroupsConstants.TMPLAT_SSN_DISP1);
				ssnDispGroupList.add(methodGroupDto);
				List<BookmarkDto> bookmarkMethodList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkCdSsnVerifMeth = createBookmarkWithCodesTable(BookmarkConstants.SSN_METHOD1,
						personIdDto.getSsnVerificationMethod(), CodesConstant.SSNVERIF);
				bookmarkMethodList.add(bookmarkCdSsnVerifMeth);

				methodGroupDto.setBookmarkDtoList(bookmarkMethodList);
			}
		}

		// sub group per0396 :parent group per0335
		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersForwardIdentifiers()) {
			if (ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())
					&& ServiceConstants.SOCIAL_SECURITY.equals(personIdDto.getPersonIdType())
					&& ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())) {
				FormDataGroupDto valGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SSN_VAL_1,
						FormGroupsConstants.TMPLAT_SSN_DISP1);
				ssnDispGroupList.add(valGroupDto);
				List<BookmarkDto> bookmarkValList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkIndValidateByInterface = createBookmark(BookmarkConstants.SSN_VALIDATED1,
						personIdDto.getIndValidatedByInterface());
				bookmarkValList.add(bookmarkIndValidateByInterface);

				valGroupDto.setBookmarkDtoList(bookmarkValList);
			}
		}

		// parent group per0337 :SSN display for person 2 (non-employee) -
		// TMPLAT_SSN_DISP2
		FormDataGroupDto ssnDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_SSN_DISP2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(ssnDispGroupDto2);

		List<FormDataGroupDto> ssnDispGroupList2 = new ArrayList<FormDataGroupDto>();
		ssnDispGroupDto2.setFormDataGroupList(ssnDispGroupList2);

		// sub group per0338 :parent group per0337
		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersClosedIdentifiers()) {
			if (ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())
					&& ServiceConstants.SOCIAL_SECURITY.equals(personIdDto.getPersonIdType())
					&& ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())) {
				FormDataGroupDto ssnGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SSN_2,
						FormGroupsConstants.TMPLAT_SSN_DISP2);
				ssnDispGroupList2.add(ssnGroupDto);
				List<BookmarkDto> bookmarkSsnList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkIdNumber = createBookmark(BookmarkConstants.SSN2, personIdDto.getPersonIdNumber());
				bookmarkSsnList.add(bookmarkIdNumber);

				ssnGroupDto.setBookmarkDtoList(bookmarkSsnList);
			}
		}
				// sub group per0399 :parent group per0337
		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersClosedIdentifiers()) {
			if (ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())
					&& ServiceConstants.SOCIAL_SECURITY.equals(personIdDto.getPersonIdType())
					&& ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())) {
				FormDataGroupDto methodGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SSN_METHOD_2,
						FormGroupsConstants.TMPLAT_SSN_DISP2);
				ssnDispGroupList2.add(methodGroupDto);
				List<BookmarkDto> bookmarkMethodList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkCdSsnVerifMeth = createBookmarkWithCodesTable(BookmarkConstants.SSN_METHOD2,
						personIdDto.getSsnVerificationMethod(), CodesConstant.SSNVERIF);
				bookmarkMethodList.add(bookmarkCdSsnVerifMeth);

				methodGroupDto.setBookmarkDtoList(bookmarkMethodList);
			}
		}

		// sub group per0398 :parent group per0337
		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersClosedIdentifiers()) {
			if (ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())
					&& ServiceConstants.SOCIAL_SECURITY.equals(personIdDto.getPersonIdType())
					&& ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())) {
				FormDataGroupDto valGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SSN_VAL_2,
						FormGroupsConstants.TMPLAT_SSN_DISP2);
				ssnDispGroupList2.add(valGroupDto);
				List<BookmarkDto> bookmarkValList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkIndValidateByInterface = createBookmark(BookmarkConstants.SSN_VALIDATED2,
						personIdDto.getIndValidatedByInterface());
				bookmarkValList.add(bookmarkIndValidateByInterface);

				valGroupDto.setBookmarkDtoList(bookmarkValList);
			}
		}

		// parent group per0340 : Address display for person 1 (non-employee
		// only) - TMPLAT_DISP_ADDR1
		FormDataGroupDto addrDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_ADDR1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(addrDispGroupDto);

		List<FormDataGroupDto> addrDispGroupList = new ArrayList<FormDataGroupDto>();
		addrDispGroupDto.setFormDataGroupList(addrDispGroupList);

		// sub group per0339 Address history for person 1 - TMPLAT_ADDR1 :parent
		// group per0340
		for (AddressDto addressDto : personComparisonDto.getAddressList()) {

			FormDataGroupDto addrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR1,
					FormGroupsConstants.TMPLAT_DISP_ADDR1);
			addrDispGroupList.add(addrGroupDto);
			List<BookmarkDto> bookmarkAddrList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(addressDto.getIndPersAddrLinkInvalid())) {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, PRIMARY);
				bookmarkAddrList.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, INVALID);
				bookmarkAddrList.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, TYPE);
				bookmarkAddrList.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE, START_DATE);
				bookmarkAddrList.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, END_DATE);
				bookmarkAddrList.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.ADDRESS, ADDRESS);
				bookmarkAddrList.add(bookmarkAddress);
				BookmarkDto bookmarkComma = createBookmark(BookmarkConstants.COMMA, COMMA);
				bookmarkAddrList.add(bookmarkComma);

			} else {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE,
						ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.ADDRESS, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkAddress);
				BookmarkDto bookmarkComma = createBookmark(BookmarkConstants.COMMA, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkComma);

			}

			BookmarkDto bookmarkIndPersAddrLinkInvalid = createBookmark(BookmarkConstants.INVAL_ADDR1,
					addressDto.getIndPersAddrLinkInvalid());
			bookmarkAddrList.add(bookmarkIndPersAddrLinkInvalid);
			BookmarkDto bookmarkIndPersAddrLinkPrimary = createBookmark(BookmarkConstants.PRIM_ADDR1,
					addressDto.getIndPersAddrLinkPrimary());
			bookmarkAddrList.add(bookmarkIndPersAddrLinkPrimary);
			BookmarkDto bookmarkDtPersAddrLinkEnd = createBookmark(BookmarkConstants.ADDR_ENDDT_1,
					DateUtils.stringDt(addressDto.getDtPersAddrLinkEnd())
							.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
									: DateUtils.stringDt(addressDto.getDtPersAddrLinkEnd()));
			bookmarkAddrList.add(bookmarkDtPersAddrLinkEnd);
			BookmarkDto bookmarkDtPersAddrLinkStart = createBookmark(BookmarkConstants.ADDR_STARTDT_1,
					DateUtils.stringDt(addressDto.getDtPersAddrLinkStart()));
			bookmarkAddrList.add(bookmarkDtPersAddrLinkStart);
			BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP1, addressDto.getAddrZip());
			bookmarkAddrList.add(bookmarkAddrZip);
			BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY1, addressDto.getAddrCity());
			bookmarkAddrList.add(bookmarkAddrCity);
			BookmarkDto bookmarkAddrPersAddrStLn1 = createBookmark(BookmarkConstants.ADDR_LN1_1,
					addressDto.getAddrPersAddrStLn1());
			bookmarkAddrList.add(bookmarkAddrPersAddrStLn1);
			BookmarkDto bookmarkCdAddrState = createBookmark(BookmarkConstants.ADDR_STATE1,
					addressDto.getCdAddrState());
			bookmarkAddrList.add(bookmarkCdAddrState);
			BookmarkDto bookmarkCdPersAddrLinkType = createBookmarkWithCodesTable(BookmarkConstants.TYPEADDR1,
					addressDto.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
			bookmarkAddrList.add(bookmarkCdPersAddrLinkType);

			addrGroupDto.setBookmarkDtoList(bookmarkAddrList);

			List<FormDataGroupDto> addrGroupList = new ArrayList<FormDataGroupDto>();
			addrGroupDto.setFormDataGroupList(addrGroupList);

			// sub group per0341 TMPLAT_ADDR_LN2_1 :parent group per0339

			FormDataGroupDto addrLn2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_LN2_1,
					FormGroupsConstants.TMPLAT_ADDR1);
			addrGroupList.add(addrLn2GroupDto);
			List<BookmarkDto> bookmarkAddrLnList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(addressDto.getAddrPersAddrStLn2())) {
				BookmarkDto bookmarkAddrPersAddrStLn2 = createBookmark(BookmarkConstants.ADDR_LN2_1,
						addressDto.getAddrPersAddrStLn2());
				bookmarkAddrLnList.add(bookmarkAddrPersAddrStLn2);
				addrLn2GroupDto.setBookmarkDtoList(bookmarkAddrLnList);
			} else {
				BookmarkDto bookmarkAddrPersAddrStLn2 = createBookmark(BookmarkConstants.ADDR_LN2_1,
						ServiceConstants.FORM_SPACE);
				bookmarkAddrLnList.add(bookmarkAddrPersAddrStLn2);
				addrLn2GroupDto.setBookmarkDtoList(bookmarkAddrLnList);
			}

		}

		// parent group per0342 : Address display for person 2 (non-employee
		// only) - TMPLAT_DISP_ADDR2
		FormDataGroupDto addrDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_ADDR2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(addrDispGroupDto2);

		List<FormDataGroupDto> addrDispGroupList2 = new ArrayList<FormDataGroupDto>();
		addrDispGroupDto2.setFormDataGroupList(addrDispGroupList2);

		// sub group per0343 Address history for person 2 - TMPLAT_ADDR2 :parent
		// group per0342
		for (AddressDto addressDto : personComparisonDto.getAddressList2()) {

			FormDataGroupDto addrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR2,
					FormGroupsConstants.TMPLAT_DISP_ADDR2);
			addrDispGroupList2.add(addrGroupDto);
			List<BookmarkDto> bookmarkAddrList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(addressDto.getIndPersAddrLinkInvalid())) {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, PRIMARY);
				bookmarkAddrList.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, INVALID);
				bookmarkAddrList.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, TYPE);
				bookmarkAddrList.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE, START_DATE);
				bookmarkAddrList.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, END_DATE);
				bookmarkAddrList.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.ADDRESS, ADDRESS);
				bookmarkAddrList.add(bookmarkAddress);
				BookmarkDto bookmarkComma = createBookmark(BookmarkConstants.COMMA, COMMA);
				bookmarkAddrList.add(bookmarkComma);

			} else {
				BookmarkDto bookmarkPrimary = createBookmark(BookmarkConstants.PRIMARY, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkPrimary);
				BookmarkDto bookmarkInvalid = createBookmark(BookmarkConstants.INVALID, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkInvalid);
				BookmarkDto bookmarkType = createBookmark(BookmarkConstants.TYPE, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkType);
				BookmarkDto bookmarkStartDate = createBookmark(BookmarkConstants.START_DATE,
						ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkStartDate);
				BookmarkDto bookmarkEndDate = createBookmark(BookmarkConstants.END_DATE, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkEndDate);
				BookmarkDto bookmarkAddress = createBookmark(BookmarkConstants.ADDRESS, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkAddress);
				BookmarkDto bookmarkComma = createBookmark(BookmarkConstants.COMMA, ServiceConstants.FORM_SPACE);
				bookmarkAddrList.add(bookmarkComma);

			}

			BookmarkDto bookmarkIndPersAddrLinkInvalid = createBookmark(BookmarkConstants.INVAL_ADDR2,
					addressDto.getIndPersAddrLinkInvalid());
			bookmarkAddrList.add(bookmarkIndPersAddrLinkInvalid);
			BookmarkDto bookmarkIndPersAddrLinkPrimary = createBookmark(BookmarkConstants.PRIM_ADDR2,
					addressDto.getIndPersAddrLinkPrimary());
			bookmarkAddrList.add(bookmarkIndPersAddrLinkPrimary);
			BookmarkDto bookmarkDtPersAddrLinkEnd = createBookmark(BookmarkConstants.ADDR_ENDDT_2,
					DateUtils.stringDt(addressDto.getDtPersAddrLinkEnd())
							.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
									: DateUtils.stringDt(addressDto.getDtPersAddrLinkEnd()));
			bookmarkAddrList.add(bookmarkDtPersAddrLinkEnd);
			BookmarkDto bookmarkDtPersAddrLinkStart = createBookmark(BookmarkConstants.ADDR_STARTDT_2,
					DateUtils.stringDt(addressDto.getDtPersAddrLinkStart()));
			bookmarkAddrList.add(bookmarkDtPersAddrLinkStart);
			BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP2, addressDto.getAddrZip());
			bookmarkAddrList.add(bookmarkAddrZip);
			BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY2, addressDto.getAddrCity());
			bookmarkAddrList.add(bookmarkAddrCity);
			BookmarkDto bookmarkAddrPersAddrStLn1 = createBookmark(BookmarkConstants.ADDR_LN1_2,
					addressDto.getAddrPersAddrStLn1());
			bookmarkAddrList.add(bookmarkAddrPersAddrStLn1);
			BookmarkDto bookmarkCdAddrState = createBookmark(BookmarkConstants.ADDR_STATE2,
					addressDto.getCdAddrState());
			bookmarkAddrList.add(bookmarkCdAddrState);
			BookmarkDto bookmarkCdPersAddrLinkType = createBookmarkWithCodesTable(BookmarkConstants.TYPEADDR2,
					addressDto.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
			bookmarkAddrList.add(bookmarkCdPersAddrLinkType);

			addrGroupDto.setBookmarkDtoList(bookmarkAddrList);

			List<FormDataGroupDto> addrGroupList = new ArrayList<FormDataGroupDto>();
			addrGroupDto.setFormDataGroupList(addrGroupList);

			// sub group per0344 TMPLAT_ADDR_LN2_2 :parent group per0343

			FormDataGroupDto addrLn2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_LN2_2,
					FormGroupsConstants.TMPLAT_ADDR2);
			addrGroupList.add(addrLn2GroupDto);
			List<BookmarkDto> bookmarkAddrLnList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(addressDto.getAddrPersAddrStLn2())) {
				BookmarkDto bookmarkAddrPersAddrStLn2 = createBookmark(BookmarkConstants.ADDR_LN2_2,
						addressDto.getAddrPersAddrStLn2());
				bookmarkAddrLnList.add(bookmarkAddrPersAddrStLn2);
				addrLn2GroupDto.setBookmarkDtoList(bookmarkAddrLnList);
			} else {
				BookmarkDto bookmarkAddrPersAddrStLn2 = createBookmark(BookmarkConstants.ADDR_LN2_2,
						ServiceConstants.FORM_SPACE);
				bookmarkAddrLnList.add(bookmarkAddrPersAddrStLn2);
				addrLn2GroupDto.setBookmarkDtoList(bookmarkAddrLnList);

			}

		}

		// parent group per0301 : Email display for person 1 (non-employee only)
		// - TMPLAT_DISP_EMAIL1
		FormDataGroupDto emailDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_EMAIL1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(emailDispGroupDto);

		List<FormDataGroupDto> emailDispGroupList = new ArrayList<FormDataGroupDto>();
		emailDispGroupDto.setFormDataGroupList(emailDispGroupList);

		// sub group per0331 Email for person 1 - TMPLAT_EMAIL1 :parent group
		// per0301

		for (PersonEmailDto personEmailDto : personComparisonDto.getPersonEmailDtoList()) {
			FormDataGroupDto emailGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EMAIL1,
					FormGroupsConstants.TMPLAT_DISP_EMAIL1);
			emailDispGroupList.add(emailGroupDto);
			List<BookmarkDto> bookmarkEmailList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkIndInvalidEmail = createBookmark(BookmarkConstants.INVAL_EMAIL1,
					personEmailDto.getIndInvalid());
			bookmarkEmailList.add(bookmarkIndInvalidEmail);
			BookmarkDto bookmarkIndPrimaryEmail = createBookmark(BookmarkConstants.PRIM_EMAIL1,
					personEmailDto.getIndPrimary());
			bookmarkEmailList.add(bookmarkIndPrimaryEmail);
			BookmarkDto bookmarkDtEndEmail = createBookmark(BookmarkConstants.EMAIL_ENDDT_1,
					DateUtils.stringDt(personEmailDto.getDtEnd()).equals(DateUtils.stringDt(ServiceConstants.MAX_DATE))
							? ServiceConstants.EMPTY_STRING : DateUtils.stringDt(personEmailDto.getDtEnd()));
			bookmarkEmailList.add(bookmarkDtEndEmail);
			BookmarkDto bookmarkDtStartEmail = createBookmark(BookmarkConstants.EMAIL_STARTDT_1,
					DateUtils.stringDt(personEmailDto.getDtStart()));
			bookmarkEmailList.add(bookmarkDtStartEmail);
			BookmarkDto bookmarkCdType = createBookmarkWithCodesTable(BookmarkConstants.TYPE_EMAIL1,
					personEmailDto.getCdType(), CodesConstant.CEMLPRTY);
			bookmarkEmailList.add(bookmarkCdType);
			BookmarkDto bookmarkTxtEmail = createBookmark(BookmarkConstants.ADDR_EMAIL_1, personEmailDto.getTxtEmail());
			bookmarkEmailList.add(bookmarkTxtEmail);

			emailGroupDto.setBookmarkDtoList(bookmarkEmailList);
		}

		// parent group per0302 : Email display for person 2 (non-employee only)
		// - TMPLAT_DISP_EMAIL2
		FormDataGroupDto emailDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_EMAIL2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(emailDispGroupDto2);

		List<FormDataGroupDto> emailDispGroupList2 = new ArrayList<FormDataGroupDto>();
		emailDispGroupDto2.setFormDataGroupList(emailDispGroupList2);

		// sub group per0332 Email for person 2 - TMPLAT_EMAIL2 :parent group
		// per0302

		for (PersonEmailDto personEmailDto : personComparisonDto.getPersonEmailDtoList2()) {
			FormDataGroupDto emailGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EMAIL2,
					FormGroupsConstants.TMPLAT_DISP_EMAIL2);
			emailDispGroupList2.add(emailGroupDto);
			List<BookmarkDto> bookmarkEmailList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkIndInvalidEmail = createBookmark(BookmarkConstants.INVAL_EMAIL2,
					personEmailDto.getIndInvalid());
			bookmarkEmailList.add(bookmarkIndInvalidEmail);
			BookmarkDto bookmarkIndPrimaryEmail = createBookmark(BookmarkConstants.PRIM_EMAIL2,
					personEmailDto.getIndPrimary());
			bookmarkEmailList.add(bookmarkIndPrimaryEmail);
			BookmarkDto bookmarkDtEndEmail = createBookmark(BookmarkConstants.EMAIL_ENDDT_2,
					DateUtils.stringDt(personEmailDto.getDtEnd()).equals(DateUtils.stringDt(ServiceConstants.MAX_DATE))
							? ServiceConstants.EMPTY_STRING : DateUtils.stringDt(personEmailDto.getDtEnd()));
			bookmarkEmailList.add(bookmarkDtEndEmail);
			BookmarkDto bookmarkDtStartEmail = createBookmark(BookmarkConstants.EMAIL_STARTDT_2,
					DateUtils.stringDt(personEmailDto.getDtStart()));
			bookmarkEmailList.add(bookmarkDtStartEmail);
			BookmarkDto bookmarkCdType = createBookmarkWithCodesTable(BookmarkConstants.TYPE_EMAIL2,
					personEmailDto.getCdType(), CodesConstant.CEMLPRTY);
			bookmarkEmailList.add(bookmarkCdType);
			BookmarkDto bookmarkTxtEmail = createBookmark(BookmarkConstants.ADDR_EMAIL_2, personEmailDto.getTxtEmail());
			bookmarkEmailList.add(bookmarkTxtEmail);

			emailGroupDto.setBookmarkDtoList(bookmarkEmailList);
		}

		// parent group per0345 : Race display for person 1 (non-employee only)
		// - TMPLAT_DISP_RACE1
		FormDataGroupDto raceDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_RACE1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(raceDispGroupDto);

		List<FormDataGroupDto> raceDispGroupList = new ArrayList<FormDataGroupDto>();
		raceDispGroupDto.setFormDataGroupList(raceDispGroupList);

		// sub group per0305 TMPLAT_RACE1 :parent group per0345

		for (PersonRaceOutDto personRaceOutDto : personComparisonDto.getPersonRaceOutDtoList()) {
			FormDataGroupDto raceGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RACE1,
					FormGroupsConstants.TMPLAT_DISP_RACE1);

			List<BookmarkDto> bookmarkRaceList = new ArrayList<BookmarkDto>();
			if (!ObjectUtils.isEmpty(personRaceOutDto.getCdPersonRace())) {
				BookmarkDto bookmarkCdPersonRace = createBookmarkWithCodesTable(BookmarkConstants.RACE1,
						personRaceOutDto.getCdPersonRace(), CodesConstant.CRACE);
				bookmarkRaceList.add(bookmarkCdPersonRace);
				raceGroupDto.setBookmarkDtoList(bookmarkRaceList);
			} else {
				BookmarkDto bookmarkCdPersonRace = createBookmark(BookmarkConstants.RACE1, ServiceConstants.FORM_SPACE);
				bookmarkRaceList.add(bookmarkCdPersonRace);
				raceGroupDto.setBookmarkDtoList(bookmarkRaceList);
			}
			raceDispGroupList.add(raceGroupDto);

		}

		// parent group per0346 : Race display for person 2 (non-employee only)
		// - TMPLAT_DISP_RACE2
		FormDataGroupDto raceDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_RACE2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(raceDispGroupDto2);

		List<FormDataGroupDto> raceDispGroupList2 = new ArrayList<FormDataGroupDto>();
		raceDispGroupDto2.setFormDataGroupList(raceDispGroupList2);

		// sub group per0306 TMPLAT_RACE2 :parent group per0346

		for (PersonRaceOutDto personRaceOutDto : personComparisonDto.getPersonRaceOutDtoList2()) {
			FormDataGroupDto raceGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RACE2,
					FormGroupsConstants.TMPLAT_DISP_RACE2);

			List<BookmarkDto> bookmarkRaceList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(personRaceOutDto.getCdPersonRace())) {
				BookmarkDto bookmarkCdPersonRace = createBookmarkWithCodesTable(BookmarkConstants.RACE2,
						personRaceOutDto.getCdPersonRace(), CodesConstant.CRACE);
				bookmarkRaceList.add(bookmarkCdPersonRace);
				raceGroupDto.setBookmarkDtoList(bookmarkRaceList);
			} else {
				BookmarkDto bookmarkCdPersonRace = createBookmark(BookmarkConstants.RACE2, ServiceConstants.FORM_SPACE);
				bookmarkRaceList.add(bookmarkCdPersonRace);
				raceGroupDto.setBookmarkDtoList(bookmarkRaceList);
			}

			raceDispGroupList2.add(raceGroupDto);

		}

		// parent group per0347 : Ethnicity display for person 1 (non-employee
		// only) - TMPLAT_DISP_ETH1
		FormDataGroupDto ethnDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_ETH1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(ethnDispGroupDto);

		List<FormDataGroupDto> ethnDispGroupList = new ArrayList<FormDataGroupDto>();
		ethnDispGroupDto.setFormDataGroupList(ethnDispGroupList);

		// sub group per0348 Ethnicity for person 1 - TMPLAT_ETH1 :parent group
		// per0347

		for (PersonEthnicityOutDto personEthnicityOutDto : personComparisonDto.getPersonEthnicityOutDtos()) {
			FormDataGroupDto ethnGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ETH1,
					FormGroupsConstants.TMPLAT_DISP_ETH1);

			List<BookmarkDto> bookmarkEthnList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(personEthnicityOutDto.getCdPersonEthnicity())) {
				BookmarkDto bookmarkCdPersonEthnicity = createBookmarkWithCodesTable(BookmarkConstants.ETHNIC1,
						personEthnicityOutDto.getCdPersonEthnicity(), CodesConstant.CINDETHN);
				bookmarkEthnList.add(bookmarkCdPersonEthnicity);
				ethnGroupDto.setBookmarkDtoList(bookmarkEthnList);
			} else {
				BookmarkDto bookmarkCdPersonEthnicity = createBookmark(BookmarkConstants.ETHNIC1,
						ServiceConstants.FORM_SPACE);
				bookmarkEthnList.add(bookmarkCdPersonEthnicity);
				ethnGroupDto.setBookmarkDtoList(bookmarkEthnList);
			}

			ethnDispGroupList.add(ethnGroupDto);

		}

		// parent group per0349 : Ethnicity display for person 2 (non-employee
		// only) - TMPLAT_DISP_ETH2
		FormDataGroupDto ethnDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_ETH2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(ethnDispGroupDto2);

		List<FormDataGroupDto> ethnDispGroupList2 = new ArrayList<FormDataGroupDto>();
		ethnDispGroupDto2.setFormDataGroupList(ethnDispGroupList2);

		// sub group per0350 Ethnicity for person 2 - TMPLAT_ETH2 :parent group
		// per0349

		for (PersonEthnicityOutDto personEthnicityOutDto : personComparisonDto.getPersonEthnicityOutDtos2()) {
			FormDataGroupDto ethnGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ETH2,
					FormGroupsConstants.TMPLAT_DISP_ETH2);

			List<BookmarkDto> bookmarkEthnList = new ArrayList<BookmarkDto>();

			if (!ObjectUtils.isEmpty(personEthnicityOutDto.getCdPersonEthnicity())) {
				BookmarkDto bookmarkCdPersonEthnicity = createBookmarkWithCodesTable(BookmarkConstants.ETHNIC2,
						personEthnicityOutDto.getCdPersonEthnicity(), CodesConstant.CINDETHN);
				bookmarkEthnList.add(bookmarkCdPersonEthnicity);
				ethnGroupDto.setBookmarkDtoList(bookmarkEthnList);
			} else {
				BookmarkDto bookmarkCdPersonEthnicity = createBookmark(BookmarkConstants.ETHNIC2,
						ServiceConstants.FORM_SPACE);
				bookmarkEthnList.add(bookmarkCdPersonEthnicity);
				ethnGroupDto.setBookmarkDtoList(bookmarkEthnList);
			}
			ethnDispGroupList2.add(ethnGroupDto);
		}

		// parent group per0350 : Characteristics display for person 1
		// (non-employee only) - TMPLAT_DISP_CHAR1
		FormDataGroupDto charDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_CHAR1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(charDispGroupDto);

		List<FormDataGroupDto> charDispGroupList = new ArrayList<FormDataGroupDto>();
		charDispGroupDto.setFormDataGroupList(charDispGroupList);

		// sub group per0309 Characteristics for person 1 - TMPLAT_CHARACTER1
		// :parent group per0350
		for (CharacteristicsDto characteristicsDto : personComparisonDto.getCharDtlList()) {
			FormDataGroupDto charGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHARACTER1,
					FormGroupsConstants.TMPLAT_DISP_CHAR1);
			charDispGroupList.add(charGroupDto);
			List<BookmarkDto> bookmarkCharList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkCdCharCategory = createBookmarkWithCodesTable(BookmarkConstants.CHAR_CATEGORY1,
					characteristicsDto.getCdCharCategory(), CodesConstant.CCHRTCAT);
			bookmarkCharList.add(bookmarkCdCharCategory);
			charGroupDto.setBookmarkDtoList(bookmarkCharList);

			List<FormDataGroupDto> charGroupList = new ArrayList<FormDataGroupDto>();
			charGroupDto.setFormDataGroupList(charGroupList);

			// sub group per0314 TMPLAT_CHAR1_CCH :parent group per0309
			if (ServiceConstants.CCH.equals(characteristicsDto.getCdCharCategory())) {
				FormDataGroupDto cchGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHAR1_CCH,
						FormGroupsConstants.TMPLAT_CHARACTER1);
				charGroupList.add(cchGroupDto);

				List<BookmarkDto> bookmarkCharCchList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCdCharCategoryC = createBookmarkWithCodesTable(BookmarkConstants.CHAR_CCH1,
						characteristicsDto.getCdCharacteristic(), CodesConstant.CCH);
				bookmarkCharCchList.add(bookmarkCdCharCategoryC);
				cchGroupDto.setBookmarkDtoList(bookmarkCharCchList);

			}

			// sub group per0312 TMPLAT_CHAR1_CCT :parent group per0309
			if (ServiceConstants.PARENT_CARETAKER_CHAR_CATEGORY.equals(characteristicsDto.getCdCharCategory())) {
				FormDataGroupDto cctGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHAR1_CCT,
						FormGroupsConstants.TMPLAT_CHARACTER1);
				charGroupList.add(cctGroupDto);

				List<BookmarkDto> bookmarkCharCctList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCdCharCategoryC = createBookmarkWithCodesTable(BookmarkConstants.CHAR_CCT1,
						characteristicsDto.getCdCharacteristic(), CodesConstant.CCT);
				bookmarkCharCctList.add(bookmarkCdCharCategoryC);
				cctGroupDto.setBookmarkDtoList(bookmarkCharCctList);

			}

			// sub group per0313 TMPLAT_CHAR1_CAP :parent group per0309
			if (ServiceConstants.APS_CHARACTERISTIC.equals(characteristicsDto.getCdCharCategory())) {
				FormDataGroupDto capGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHAR1_CAP,
						FormGroupsConstants.TMPLAT_CHARACTER1);
				charGroupList.add(capGroupDto);

				List<BookmarkDto> bookmarkCharCapList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCdCharCategoryC = createBookmarkWithCodesTable(BookmarkConstants.CHAR_CAP1,
						characteristicsDto.getCdCharacteristic(), CodesConstant.CAP);
				bookmarkCharCapList.add(bookmarkCdCharCategoryC);
				capGroupDto.setBookmarkDtoList(bookmarkCharCapList);

			}

			// sub group per0311 TMPLAT_CHAR1_CPL :parent group per0309
			if (ServiceConstants.CPL.equals(characteristicsDto.getCdCharCategory())) {
				FormDataGroupDto cplGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHAR1_CPL,
						FormGroupsConstants.TMPLAT_CHARACTER1);
				charGroupList.add(cplGroupDto);

				List<BookmarkDto> bookmarkCharCplList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCdCharCategoryC = createBookmarkWithCodesTable(BookmarkConstants.CHAR_CPL1,
						characteristicsDto.getCdCharacteristic(), CodesConstant.CPL);
				bookmarkCharCplList.add(bookmarkCdCharCategoryC);
				cplGroupDto.setBookmarkDtoList(bookmarkCharCplList);

			}
		}

		// parent group per0351 : Characteristics display for person 2
		// (non-employee only) - TMPLAT_DISP_CHAR2
		FormDataGroupDto charDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_CHAR2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(charDispGroupDto2);

		List<FormDataGroupDto> charDispGroupList2 = new ArrayList<FormDataGroupDto>();
		charDispGroupDto2.setFormDataGroupList(charDispGroupList2);

		// sub group per0310 Characteristics for person 2 - TMPLAT_CHARACTER2
		// :parent group per0351
		for (CharacteristicsDto characteristicsDto : personComparisonDto.getCharDtlList2()) {
			FormDataGroupDto charGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHARACTER2,
					FormGroupsConstants.TMPLAT_DISP_CHAR2);
			charDispGroupList2.add(charGroupDto);
			List<BookmarkDto> bookmarkCharList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkCdCharCategory = createBookmarkWithCodesTable(BookmarkConstants.CHAR_CATEGORY2,
					characteristicsDto.getCdCharCategory(), CodesConstant.CCHRTCAT);
			bookmarkCharList.add(bookmarkCdCharCategory);
			charGroupDto.setBookmarkDtoList(bookmarkCharList);

			List<FormDataGroupDto> charGroupList = new ArrayList<FormDataGroupDto>();
			charGroupDto.setFormDataGroupList(charGroupList);

			// sub group per0315 TMPLAT_CHAR2_CCH :parent group per0310
			if (ServiceConstants.CCH.equals(characteristicsDto.getCdCharCategory())) {
				FormDataGroupDto cchGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHAR2_CCH,
						FormGroupsConstants.TMPLAT_CHARACTER2);
				charGroupList.add(cchGroupDto);

				List<BookmarkDto> bookmarkCharCchList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCdCharCategoryC = createBookmarkWithCodesTable(BookmarkConstants.CHAR_CCH2,
						characteristicsDto.getCdCharacteristic(), CodesConstant.CCH);
				bookmarkCharCchList.add(bookmarkCdCharCategoryC);
				cchGroupDto.setBookmarkDtoList(bookmarkCharCchList);

			}

			// sub group per0318 TMPLAT_CHAR2_CCT :parent group per0310
			if (ServiceConstants.PARENT_CARETAKER_CHAR_CATEGORY.equals(characteristicsDto.getCdCharCategory())) {
				FormDataGroupDto cctGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHAR2_CCT,
						FormGroupsConstants.TMPLAT_CHARACTER2);
				charGroupList.add(cctGroupDto);

				List<BookmarkDto> bookmarkCharCctList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCdCharCategoryC = createBookmarkWithCodesTable(BookmarkConstants.CHAR_CCT2,
						characteristicsDto.getCdCharacteristic(), CodesConstant.CCT);
				bookmarkCharCctList.add(bookmarkCdCharCategoryC);
				cctGroupDto.setBookmarkDtoList(bookmarkCharCctList);

			}

			// sub group per0317 TMPLAT_CHAR2_CAP :parent group per0310
			if (ServiceConstants.APS_CHARACTERISTIC.equals(characteristicsDto.getCdCharCategory())) {
				FormDataGroupDto capGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHAR2_CAP,
						FormGroupsConstants.TMPLAT_CHARACTER2);
				charGroupList.add(capGroupDto);

				List<BookmarkDto> bookmarkCharCapList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCdCharCategoryC = createBookmarkWithCodesTable(BookmarkConstants.CHAR_CAP2,
						characteristicsDto.getCdCharacteristic(), CodesConstant.CAP);
				bookmarkCharCapList.add(bookmarkCdCharCategoryC);
				capGroupDto.setBookmarkDtoList(bookmarkCharCapList);

			}

			// sub group per0316 TMPLAT_CHAR2_CPL :parent group per0310
			if (ServiceConstants.CPL.equals(characteristicsDto.getCdCharCategory())) {
				FormDataGroupDto cplGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHAR2_CPL,
						FormGroupsConstants.TMPLAT_CHARACTER2);
				charGroupList.add(cplGroupDto);

				List<BookmarkDto> bookmarkCharCplList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCdCharCategoryC = createBookmarkWithCodesTable(BookmarkConstants.CHAR_CPL2,
						characteristicsDto.getCdCharacteristic(), CodesConstant.CPL);
				bookmarkCharCplList.add(bookmarkCdCharCategoryC);
				cplGroupDto.setBookmarkDtoList(bookmarkCharCplList);

			}
		}

		// parent group per0352 : ID history display for person 1 (non-employee
		// only) - TMPLAT_ID_DISP1
		FormDataGroupDto idDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ID_DISP1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(idDispGroupDto);

		List<FormDataGroupDto> idDispGroupList = new ArrayList<FormDataGroupDto>();
		idDispGroupDto.setFormDataGroupList(idDispGroupList);

		for (PersonIdDto personIdDto : personComparisonDto.getPersonIdDtoList()) {

			// sub group per0321 Person IDs for person 1 - TMPLAT_ID_HIST1:
			// parent group per0352
			FormDataGroupDto personGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ID_HIST1,
					FormGroupsConstants.TMPLAT_ID_DISP1);

			idDispGroupList.add(personGroupDto);
			List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkIdPersonInvalid = createBookmark(BookmarkConstants.INVAL_ID_HIST1,
					personIdDto.getIndPersonIdInvalid());
			bookmarkPersonList.add(bookmarkIdPersonInvalid);
			BookmarkDto bookmarkIdEnd = createBookmark(BookmarkConstants.ID_HIST_ENDDT_1,
					DateUtils.stringDt(personIdDto.getDtPersonIdEnd())
							.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
									: DateUtils.stringDt(personIdDto.getDtPersonIdEnd()));
			bookmarkPersonList.add(bookmarkIdEnd);
			BookmarkDto bookmarkIdStart = createBookmark(BookmarkConstants.ID_HIST_STARTDT_1,
					DateUtils.stringDt(personIdDto.getDtPersonIdStart()));
			bookmarkPersonList.add(bookmarkIdStart);
			BookmarkDto bookmarkPersonIdType = createBookmark(BookmarkConstants.TYPE_ID_HIST1,
					personIdDto.getCdPersonIdType());
			bookmarkPersonList.add(bookmarkPersonIdType);
			BookmarkDto bookmarkIdNumber = createBookmark(BookmarkConstants.ID_HIST_NUM_1,
					personIdDto.getPersonIdNumber());
			bookmarkPersonList.add(bookmarkIdNumber);

			personGroupDto.setBookmarkDtoList(bookmarkPersonList);

		}

		// parent group per0353 : ID history display for person 2 (non-employee
		// only) - TMPLAT_ID_DISP2
		FormDataGroupDto idDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_ID_DISP2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(idDispGroupDto2);

		List<FormDataGroupDto> idDispGroupList2 = new ArrayList<FormDataGroupDto>();
		idDispGroupDto2.setFormDataGroupList(idDispGroupList2);

		for (PersonIdDto personIdDto : personComparisonDto.getPersonIdDtoList2()) {

			// sub group per0322 Person IDs for person 2 - TMPLAT_ID_HIST2:
			// parent group per0353
			FormDataGroupDto personGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ID_HIST2,
					FormGroupsConstants.TMPLAT_ID_DISP2);

			idDispGroupList2.add(personGroupDto);
			List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkIdPersonInvalid = createBookmark(BookmarkConstants.INVAL_ID_HIST2,
					personIdDto.getIndPersonIdInvalid());
			bookmarkPersonList.add(bookmarkIdPersonInvalid);
			BookmarkDto bookmarkIdEnd = createBookmark(BookmarkConstants.ID_HIST_ENDDT_2,
					DateUtils.stringDt(personIdDto.getDtPersonIdEnd())
							.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
									: DateUtils.stringDt(personIdDto.getDtPersonIdEnd()));
			bookmarkPersonList.add(bookmarkIdEnd);
			BookmarkDto bookmarkIdStart = createBookmark(BookmarkConstants.ID_HIST_STARTDT_2,
					DateUtils.stringDt(personIdDto.getDtPersonIdStart()));
			bookmarkPersonList.add(bookmarkIdStart);
			BookmarkDto bookmarkPersonIdType = createBookmark(BookmarkConstants.TYPE_ID_HIST2,
					personIdDto.getCdPersonIdType());
			bookmarkPersonList.add(bookmarkPersonIdType);
			BookmarkDto bookmarkIdNumber = createBookmark(BookmarkConstants.ID_HIST_NUM_2,
					personIdDto.getPersonIdNumber());
			bookmarkPersonList.add(bookmarkIdNumber);

			personGroupDto.setBookmarkDtoList(bookmarkPersonList);

		}

		// parent group per0354 : Income and resource display for person 1
		// (non-employee only) - TMPLAT_DISP_IR1
		FormDataGroupDto ir1DispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_IR1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(ir1DispGroupDto);

		List<FormDataGroupDto> ir1DispGroupList = new ArrayList<FormDataGroupDto>();
		ir1DispGroupDto.setFormDataGroupList(ir1DispGroupList);

		// sub group per0325 Income and resources for person 1 - TMPLAT_INC_RES1
		// :parent group per0354

		if (!CollectionUtils.isEmpty(personComparisonDto.getPersonIncomeResourceDto())) {
			for (PersonIncomeResourceDto personIncomeResourceDto : personComparisonDto.getPersonIncomeResourceDto()) {
				FormDataGroupDto ir1GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_INC_RES1,
						FormGroupsConstants.TMPLAT_DISP_IR1);
				ir1DispGroupList.add(ir1GroupDto);
				List<BookmarkDto> bookmarkRsrcList = new ArrayList<BookmarkDto>();
	
				BookmarkDto bookmarkDtIncRsrcFrom = createBookmark(BookmarkConstants.INC_RES_FROM_DT1,
						DateUtils.stringDt(personIncomeResourceDto.getDtIncRsrcFrom()));
				bookmarkRsrcList.add(bookmarkDtIncRsrcFrom);
				BookmarkDto bookmarkDtIncRsrcTo = createBookmark(BookmarkConstants.INC_RES_TO_DT1,
						DateUtils.stringDt(personIncomeResourceDto.getDtIncRsrcTo())
								.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
										: DateUtils.stringDt(
												personIncomeResourceDto.getDtIncRsrcTo()));
				bookmarkRsrcList.add(bookmarkDtIncRsrcTo);
				BookmarkDto bookmarkAmtIncRsrc = createBookmark(BookmarkConstants.INC_RES_AMT1,
						personIncomeResourceDto.getAmtIncRsrc());
				bookmarkRsrcList.add(bookmarkAmtIncRsrc);
				BookmarkDto bookmarkCdIncRsrcIncome = createBookmarkWithCodesTable(BookmarkConstants.INCRES_1,
						personIncomeResourceDto.getCdIncRsrcIncome(), CodesConstant.CINCORES);
				bookmarkRsrcList.add(bookmarkCdIncRsrcIncome);
				BookmarkDto bookmarkCdIncRsrcType = createBookmarkWithCodesTable(BookmarkConstants.INC_RES_TYPE1,
						personIncomeResourceDto.getCdIncRsrcType(), CodesConstant.CINCRSRC);
				bookmarkRsrcList.add(bookmarkCdIncRsrcType);
				ir1GroupDto.setBookmarkDtoList(bookmarkRsrcList);
				}
		}

		// parent group per0355 : Income and resource display for person 2
		// (non-employee only) - TMPLAT_DISP_IR2
		FormDataGroupDto ir1DispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_IR2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(ir1DispGroupDto2);

		List<FormDataGroupDto> ir1DispGroupList2 = new ArrayList<FormDataGroupDto>();
		ir1DispGroupDto2.setFormDataGroupList(ir1DispGroupList2);

		// sub group per0326 Income and resources for person 2 - TMPLAT_INC_RES2
		// :parent group per0355

		if (!CollectionUtils.isEmpty(personComparisonDto.getPersonIncomeResourceDto2())) {
			for (PersonIncomeResourceDto personIncomeResourceDto : personComparisonDto.getPersonIncomeResourceDto2()) {
				FormDataGroupDto ir1GroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_INC_RES2,
						FormGroupsConstants.TMPLAT_DISP_IR2);
				ir1DispGroupList2.add(ir1GroupDto2);
				List<BookmarkDto> bookmarkRsrcList2 = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtIncRsrcFrom2 = createBookmark(BookmarkConstants.INC_RES_FROM_DT2,
						DateUtils.stringDt(personIncomeResourceDto.getDtIncRsrcFrom()));
				bookmarkRsrcList2.add(bookmarkDtIncRsrcFrom2);
				BookmarkDto bookmarkDtIncRsrcTo2 = createBookmark(BookmarkConstants.INC_RES_TO_DT2,
						DateUtils.stringDt(personIncomeResourceDto.getDtIncRsrcTo())
								.equals(DateUtils.stringDt(ServiceConstants.MAX_DATE)) ? ServiceConstants.EMPTY_STRING
										: DateUtils.stringDt(
												personIncomeResourceDto.getDtIncRsrcTo()));
				bookmarkRsrcList2.add(bookmarkDtIncRsrcTo2);
				BookmarkDto bookmarkAmtIncRsrc2 = createBookmark(BookmarkConstants.INC_RES_AMT2,
						personIncomeResourceDto.getAmtIncRsrc());
				bookmarkRsrcList2.add(bookmarkAmtIncRsrc2);
				BookmarkDto bookmarkCdIncRsrcIncome2 = createBookmarkWithCodesTable(BookmarkConstants.INCRES_2,
						personIncomeResourceDto.getCdIncRsrcIncome(), CodesConstant.CINCORES);
				bookmarkRsrcList2.add(bookmarkCdIncRsrcIncome2);
				BookmarkDto bookmarkCdIncRsrcType2 = createBookmarkWithCodesTable(BookmarkConstants.INC_RES_TYPE2,
						personIncomeResourceDto.getCdIncRsrcType(), CodesConstant.CINCRSRC);
				bookmarkRsrcList2.add(bookmarkCdIncRsrcType2);
				ir1GroupDto2.setBookmarkDtoList(bookmarkRsrcList2);
			}
		}

		// parent group per0356 : Education display for person 1 (non-employee
		// only) - TMPLAT_DISP_EDU1
		FormDataGroupDto edu1DispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_EDU1,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> edu1DispGroupList = new ArrayList<FormDataGroupDto>();
		// sub group per0327 Educational history for person 1 - TMPLAT_EDU1
		// :parent group per0356
		for (EducationHistoryDto educationHistoryDto : personComparisonDto.getEducationHistoryDtoList()) {
			if (!ObjectUtils.isEmpty(educationHistoryDto.getIdEdHist())) {
				FormDataGroupDto edu1GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EDU1,
						FormGroupsConstants.TMPLAT_DISP_EDU1);
				List<BookmarkDto> bookmarkEduList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDtHistEnrollDate = createBookmark(BookmarkConstants.EDU_ENROLL_DT1,
						DateUtils.stringDt(educationHistoryDto.getDtEdHistEnrollDate()));
				bookmarkEduList.add(bookmarkDtHistEnrollDate);
				BookmarkDto bookmarkDtHistWithdrawn = createBookmark(BookmarkConstants.EDU_WDRW1,
						DateUtils.stringDt(educationHistoryDto.getDtEdHistWithdrawn()));
				bookmarkEduList.add(bookmarkDtHistWithdrawn);
				BookmarkDto bookmarkHistSchool = createBookmark(BookmarkConstants.EDU_NM1,
						educationHistoryDto.getNmEdHistSchool());
				bookmarkEduList.add(bookmarkHistSchool);
				BookmarkDto bookmarkEnrollGrade = createBookmarkWithCodesTable(BookmarkConstants.EDU_GRADE1,
						educationHistoryDto.getCdEdHistEnrollGrade(), CodesConstant.CSCHGRAD);
				bookmarkEduList.add(bookmarkEnrollGrade);
				BookmarkDto bookmarkWithdrawnGrade = createBookmarkWithCodesTable(BookmarkConstants.EDU_WDRW_GRADE1,
						educationHistoryDto.getCdEdHistWithdrawnGrade(), CodesConstant.CSCHGRAD);
				bookmarkEduList.add(bookmarkWithdrawnGrade);
				edu1GroupDto.setBookmarkDtoList(bookmarkEduList);
				edu1DispGroupList.add(edu1GroupDto);
			}
		}
		edu1DispGroupDto.setFormDataGroupList(edu1DispGroupList);
		formDataGroupList.add(edu1DispGroupDto);
		// parent group per0357 : Education display for person 2 (non-employee
		// only) - TMPLAT_DISP_EDU2
		FormDataGroupDto edu1DispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_EDU2,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> edu1DispGroupList2 = new ArrayList<FormDataGroupDto>();
		// sub group per0328 Educational history for person 2 - TMPLAT_EDU2
		// :parent group per0357

		for (EducationHistoryDto educationHistoryDto : personComparisonDto.getEducationHistoryDtoList2()) {
			FormDataGroupDto edu2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EDU2,
					FormGroupsConstants.TMPLAT_DISP_EDU2);			
			List<BookmarkDto> bookmarkEdu2List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDtHistEnrollDate = createBookmark(BookmarkConstants.EDU_ENROLL_DT2,
					DateUtils.stringDt(educationHistoryDto.getDtEdHistEnrollDate()));
			bookmarkEdu2List.add(bookmarkDtHistEnrollDate);
			BookmarkDto bookmarkDtHistWithdrawn = createBookmark(BookmarkConstants.EDU_WDRW2,
					DateUtils.stringDt(educationHistoryDto.getDtEdHistWithdrawn()));
			bookmarkEdu2List.add(bookmarkDtHistWithdrawn);
			BookmarkDto bookmarkHistSchool = createBookmark(BookmarkConstants.EDU_NM2,
					educationHistoryDto.getNmEdHistSchool());
			bookmarkEdu2List.add(bookmarkHistSchool);
			BookmarkDto bookmarkEnrollGrade = createBookmarkWithCodesTable(BookmarkConstants.EDU_GRADE2,
					educationHistoryDto.getCdEdHistEnrollGrade(), CodesConstant.CSCHGRAD);
			bookmarkEdu2List.add(bookmarkEnrollGrade);
			BookmarkDto bookmarkWithdrawnGrade = createBookmarkWithCodesTable(BookmarkConstants.EDU_WDRW_GRADE2,
					educationHistoryDto.getCdEdHistWithdrawnGrade(), CodesConstant.CSCHGRAD);
			bookmarkEdu2List.add(bookmarkWithdrawnGrade);
			edu2GroupDto.setBookmarkDtoList(bookmarkEdu2List);
			edu1DispGroupList2.add(edu2GroupDto);
		}
		edu1DispGroupDto2.setFormDataGroupList(edu1DispGroupList2);
		formDataGroupList.add(edu1DispGroupDto2);
		// parent group per0364 : TDHS display for person 1 (non-employee)
		// TMPLAT_TDHS_DISP1
		FormDataGroupDto tdhsDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_TDHS_DISP1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(tdhsDispGroupDto);

		List<FormDataGroupDto> tdhsDispGroupList = new ArrayList<FormDataGroupDto>();
		tdhsDispGroupDto.setFormDataGroupList(tdhsDispGroupList);
		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersForwardIdentifiers()) {

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.TDHS)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0391 TDHS description for person 1 -
				// TMPLAT_TDHS_DESC_1: parent group per0364
				FormDataGroupDto tdhsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_TDHS_DESC_1,
						FormGroupsConstants.TMPLAT_TDHS_DISP1);

				tdhsDispGroupList.add(tdhsGroupDto);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDescPerson = createBookmark(BookmarkConstants.TDHS_DESC1,
						personIdDto.getPersonIdDescription());
				bookmarkPersonList.add(bookmarkDescPerson);

				tdhsGroupDto.setBookmarkDtoList(bookmarkPersonList);

			}

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.TDHS)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0365 TDHS for person 1 - TMPLAT_TDHS_1: parent
				// group per0364
				FormDataGroupDto tdhsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_TDHS_1,
						FormGroupsConstants.TMPLAT_TDHS_DISP1);

				tdhsDispGroupList.add(tdhsGroupDto);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDescPerson = createBookmark(BookmarkConstants.TDHS_DESC1,
						personIdDto.getPersonIdDescription());
				bookmarkPersonList.add(bookmarkDescPerson);

				BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.TDHS1, personIdDto.getPersonIdNumber());
				bookmarkPersonList.add(bookmarkIdPerson);

				tdhsGroupDto.setBookmarkDtoList(bookmarkPersonList);

			}
		}

		// parent group per0366 : TDHS display for person 2 (non-employee)
		// TMPLAT_TDHS_DISP2
		FormDataGroupDto tdhsDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_TDHS_DISP2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(tdhsDispGroupDto2);

		List<FormDataGroupDto> tdhsDispGroupList2 = new ArrayList<FormDataGroupDto>();
		tdhsDispGroupDto2.setFormDataGroupList(tdhsDispGroupList2);

		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersClosedIdentifiers()) {

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.TDHS)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0393 TDHS description for person 2 -
				// TMPLAT_TDHS_DESC_2: parent group per0366
				FormDataGroupDto tdhsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_TDHS_DESC_2,
						FormGroupsConstants.TMPLAT_TDHS_DISP2);

				tdhsDispGroupList2.add(tdhsGroupDto);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDescPerson = createBookmark(BookmarkConstants.TDHS_DESC2,
						personIdDto.getPersonIdDescription());
				bookmarkPersonList.add(bookmarkDescPerson);

				tdhsGroupDto.setBookmarkDtoList(bookmarkPersonList);

			}

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.TDHS)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0367 TDHS for person 2 - TMPLAT_TDHS_2: parent
				// group per0366
				FormDataGroupDto tdhsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_TDHS_2,
						FormGroupsConstants.TMPLAT_TDHS_DISP2);

				tdhsDispGroupList2.add(tdhsGroupDto);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDescPerson = createBookmark(BookmarkConstants.TDHS_DESC2,
						personIdDto.getPersonIdDescription());
				bookmarkPersonList.add(bookmarkDescPerson);

				BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.TDHS2, personIdDto.getPersonIdNumber());
				bookmarkPersonList.add(bookmarkIdPerson);

				tdhsGroupDto.setBookmarkDtoList(bookmarkPersonList);

			}
		}

		// parent group per0368 : Medicaid display for person 1 (non-employee) -
		// TMPLAT_MED_DISP1
		FormDataGroupDto medDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MED_DISP1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(medDispGroupDto);

		List<FormDataGroupDto> medDispGroupList = new ArrayList<FormDataGroupDto>();
		medDispGroupDto.setFormDataGroupList(medDispGroupList);

		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersForwardIdentifiers()) {

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.MED)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0394 TDHS description for person 1 -
				// TMPLAT_MED_DESC_1: parent group per0368
				FormDataGroupDto medGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MED_DESC_1,
						FormGroupsConstants.TMPLAT_MED_DISP1);

				medDispGroupList.add(medGroupDto);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDescPerson = createBookmark(BookmarkConstants.MED_DESC1,
						personIdDto.getPersonIdDescription());
				bookmarkPersonList.add(bookmarkDescPerson);

				medGroupDto.setBookmarkDtoList(bookmarkPersonList);

			}

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.MED)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0369 Medicaid number for person 1 -
				// TMPLAT_MED_1: parent
				// group per0368
				FormDataGroupDto medGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MED_1,
						FormGroupsConstants.TMPLAT_MED_DISP1);

				medDispGroupList.add(medGroupDto);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDescPerson = createBookmark(BookmarkConstants.MED_DESC1,
						personIdDto.getPersonIdDescription());
				bookmarkPersonList.add(bookmarkDescPerson);

				BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.MED1, personIdDto.getPersonIdNumber());
				bookmarkPersonList.add(bookmarkIdPerson);

				medGroupDto.setBookmarkDtoList(bookmarkPersonList);

			}
		}

		// parent group per0370 : Medicaid display for person 2(non-employee) -
		// TMPLAT_MED_DISP2
		FormDataGroupDto medDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_MED_DISP2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(medDispGroupDto2);

		List<FormDataGroupDto> medDispGroupList2 = new ArrayList<FormDataGroupDto>();
		medDispGroupDto.setFormDataGroupList(medDispGroupList2);

		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersClosedIdentifiers()) {

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.MED)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0395 TDHS description for person 2 -
				// TMPLAT_MED_DESC_2: parent group per0370
				FormDataGroupDto medGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MED_DESC_2,
						FormGroupsConstants.TMPLAT_MED_DISP2);

				medDispGroupList2.add(medGroupDto);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDescPerson = createBookmark(BookmarkConstants.MED_DESC2,
						personIdDto.getPersonIdDescription());
				bookmarkPersonList.add(bookmarkDescPerson);

				medGroupDto.setBookmarkDtoList(bookmarkPersonList);

			}

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.MED)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0371 Medicaid number for person 2 -
				// TMPLAT_MED_2: parent
				// group per0370
				FormDataGroupDto medGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MED_2,
						FormGroupsConstants.TMPLAT_MED_DISP2);

				medDispGroupList2.add(medGroupDto);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkDescPerson = createBookmark(BookmarkConstants.MED_DESC2,
						personIdDto.getPersonIdDescription());
				bookmarkPersonList.add(bookmarkDescPerson);

				BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.MED2, personIdDto.getPersonIdNumber());
				bookmarkPersonList.add(bookmarkIdPerson);

				medGroupDto.setBookmarkDtoList(bookmarkPersonList);

			}
		}

		// parent group per0372 : Driver's license display for person 1
		// (non-employee) - TMPLAT_DL_DISP1
		FormDataGroupDto dlDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DL_DISP1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(dlDispGroupDto);

		List<FormDataGroupDto> dlDispGroupList = new ArrayList<FormDataGroupDto>();
		dlDispGroupDto.setFormDataGroupList(dlDispGroupList);

		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersForwardIdentifiers()) {

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.DRIVER)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0373 DL number for person 1 - TMPLAT_DL_1:
				// parent group per0372
				FormDataGroupDto dlGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DL_1,
						FormGroupsConstants.TMPLAT_DL_DISP1);

				dlDispGroupList.add(dlGroupDto);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.DL1, personIdDto.getPersonIdNumber());
				bookmarkPersonList.add(bookmarkIdPerson);

				dlGroupDto.setBookmarkDtoList(bookmarkPersonList);

			}
		}

		// parent group per0374 : Driver's license display for person 2
		// (non-employee) - TMPLAT_DL_DISP2
		FormDataGroupDto dlDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_DL_DISP2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(dlDispGroupDto2);

		List<FormDataGroupDto> dlDispGroupList2 = new ArrayList<FormDataGroupDto>();
		dlDispGroupDto2.setFormDataGroupList(dlDispGroupList2);

		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersClosedIdentifiers()) {

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.DRIVER)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0375 DL number for person 2 - TMPLAT_DL_2:
				// parent group per0374
				FormDataGroupDto dlGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DL_2,
						FormGroupsConstants.TMPLAT_DL_DISP2);

				dlDispGroupList2.add(dlGroupDto);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.DL2, personIdDto.getPersonIdNumber());
				bookmarkPersonList.add(bookmarkIdPerson);

				dlGroupDto.setBookmarkDtoList(bookmarkPersonList);

			}
		}

		// parent group per0376 : State photo ID display for person 1
		// (non-employee) - TMPLAT_STP_DISP1
		FormDataGroupDto stpDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_STP_DISP1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(stpDispGroupDto);

		List<FormDataGroupDto> stpDispGroupList = new ArrayList<FormDataGroupDto>();
		stpDispGroupDto.setFormDataGroupList(stpDispGroupList);

		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersForwardIdentifiers()) {

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.STP)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0377 State photo ID number for person 1 -
				// TMPLAT_STP_1: parent group per0376
				FormDataGroupDto stpGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_STP_1,
						FormGroupsConstants.TMPLAT_STP_DISP1);

				stpDispGroupList.add(stpGroupDto);
				List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.STP1, personIdDto.getPersonIdNumber());
				bookmarkPersonList.add(bookmarkIdPerson);

				stpGroupDto.setBookmarkDtoList(bookmarkPersonList);

			}
		}

		// parent group per0378 : State photo ID display for person 2
		// (non-employee) - TMPLAT_STP_DISP2
		FormDataGroupDto stpDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_STP_DISP2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(stpDispGroupDto2);

		List<FormDataGroupDto> stpDispGroupList2 = new ArrayList<FormDataGroupDto>();
		stpDispGroupDto2.setFormDataGroupList(stpDispGroupList2);

		for (PersonIdentifiersDto personIdDto : personComparisonDto.getPersClosedIdentifiers()) {

			if (ServiceConstants.N.equals(personIdDto.getIndPersonIdInvalid())
					&& personIdDto.getPersonIdType().contains(ServiceConstants.STP)
					&& ServiceConstants.MAX_DATE.equals(personIdDto.getDtPersonIdEnd())) {

				// sub group per0379 State photo ID number for person 2 -
				// TMPLAT_STP_2: parent group per0378
				FormDataGroupDto stpGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_STP_2,
						FormGroupsConstants.TMPLAT_STP_DISP2);

				stpDispGroupList2.add(stpGroupDto2);
				List<BookmarkDto> bookmarkPersonList2 = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkIdPerson2 = createBookmark(BookmarkConstants.STP2, personIdDto.getPersonIdNumber());
				bookmarkPersonList2.add(bookmarkIdPerson2);

				stpGroupDto2.setBookmarkDtoList(bookmarkPersonList2);

			}
		}

		// parent group per0384 : TMPLAT_EMP_DISP1
		FormDataGroupDto empDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EMP_DISP1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(empDispGroupDto);

		// parent group per0385 : TMPLAT_EMP_DISP2
		FormDataGroupDto empDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_EMP_DISP2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(empDispGroupDto2);

		// parent group per0386 : TMPLAT_DISP_LIVARR1
		FormDataGroupDto livarr1DispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_LIVARR1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(livarr1DispGroupDto);

		List<FormDataGroupDto> livarr1DispGroupList = new ArrayList<FormDataGroupDto>();
		livarr1DispGroupDto.setFormDataGroupList(livarr1DispGroupList);

		// sub group per0387 TMPLAT_LIVARR_1: parent group per0386
		FormDataGroupDto livarr1GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_LIVARR_1,
				FormGroupsConstants.TMPLAT_DISP_LIVARR1);

		livarr1DispGroupList.add(livarr1GroupDto);
		List<BookmarkDto> bookmarkPersonListLivArr = new ArrayList<BookmarkDto>();

		BookmarkDto bookmarkCdPersonLivArr1 = createBookmarkWithCodesTable(BookmarkConstants.LIVARR1,
				personComparisonDto.getPersonDto().getCdPersonLivArr(), CodesConstant.CLIVARR);
		bookmarkPersonListLivArr.add(bookmarkCdPersonLivArr1);

		livarr1GroupDto.setBookmarkDtoList(bookmarkPersonListLivArr);

		// parent group per0388 : TMPLAT_DISP_LIVARR2
		FormDataGroupDto livarr2DispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_LIVARR2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(livarr2DispGroupDto);

		List<FormDataGroupDto> livarr2DispGroupList = new ArrayList<FormDataGroupDto>();
		livarr2DispGroupDto.setFormDataGroupList(livarr2DispGroupList);

		// sub group per0389 TMPLAT_LIVARR_2: parent group per0388
		FormDataGroupDto livarr2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_LIVARR_2,
				FormGroupsConstants.TMPLAT_DISP_LIVARR2);

		livarr2DispGroupList.add(livarr2GroupDto);
		List<BookmarkDto> bookmarkPersonListLivArr2 = new ArrayList<BookmarkDto>();

		BookmarkDto bookmarkCdPerson2LivArr2 = createBookmarkWithCodesTable(BookmarkConstants.LIVARR2,
				personComparisonDto.getPersonDto2().getCdPersonLivArr(), CodesConstant.CLIVARR);
		bookmarkPersonListLivArr2.add(bookmarkCdPerson2LivArr2);

		livarr1GroupDto.setBookmarkDtoList(bookmarkPersonListLivArr2);

		// parent group per0390 : TMPLAT_EMP_LIVARR1
		FormDataGroupDto empLivDispGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EMP_LIVARR1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(empLivDispGroupDto);

		// parent group per0391 : TMPLAT_EMP_LIVARR2
		FormDataGroupDto empLivDispGroupDto2 = createFormDataGroup(FormGroupsConstants.TMPLAT_EMP_LIVARR2,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(empLivDispGroupDto2);

		// bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();
        // artf213439 : reading person details displaying in demographic from snap table.
		// CSEC74D
		BookmarkDto bookmarkCdPersonSex = createBookmarkWithCodesTable(BookmarkConstants.GENDER1,
				personComparisonDto.getPersForwardValueBean().getSex(), CodesConstant.CSEX);
		bookmarkNonFormGrpList.add(bookmarkCdPersonSex);
		BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.DOB1,
				DateUtils.stringDt(personComparisonDto.getPersForwardValueBean().getDtDateOfBirth()));
		bookmarkNonFormGrpList.add(bookmarkDtPersonBirth);
		BookmarkDto bookmarkDtPersonDeath = createBookmark(BookmarkConstants.DOD1,
				DateUtils.stringDt(personComparisonDto.getPersForwardValueBean().getDtOfDeath()));
		bookmarkNonFormGrpList.add(bookmarkDtPersonDeath);
		BookmarkDto bookmarkNbrPersonAge = createBookmark(BookmarkConstants.AGE1,
				(short) DateUtils.getAge(personComparisonDto.getPersForwardValueBean().getDtDateOfBirth()));
		bookmarkNonFormGrpList.add(bookmarkNbrPersonAge);
		BookmarkDto bookmarkCdPersonSuffix = createBookmarkWithCodesTable(BookmarkConstants.NM_SUFFIX1,
				personComparisonDto.getPersonDto().getCdPersonSuffix(), CodesConstant.CSUFFIX);
		bookmarkNonFormGrpList.add(bookmarkCdPersonSuffix);
		BookmarkDto bookmarkCdPersonDeath = createBookmarkWithCodesTable(BookmarkConstants.DEATHRSN1,
				personComparisonDto.getPersForwardValueBean().getCdReasonForDeath(), CodesConstant.CRSNFDTH);
		bookmarkNonFormGrpList.add(bookmarkCdPersonDeath);
		BookmarkDto bookmarkCdPersonLanguage = createBookmarkWithCodesTable(BookmarkConstants.LANG1,
				personComparisonDto.getPersForwardValueBean().getCdPrimaryLanguage(), CodesConstant.CLANG);
		bookmarkNonFormGrpList.add(bookmarkCdPersonLanguage);
		BookmarkDto bookmarkCdPersonLivArr = createBookmarkWithCodesTable(BookmarkConstants.LIVARR1,
				personComparisonDto.getPersForwardValueBean().getCdLivingArrangement(), CodesConstant.CLIVARR);
		bookmarkNonFormGrpList.add(bookmarkCdPersonLivArr);
		BookmarkDto bookmarkCdPersonMaritalStatus = createBookmarkWithCodesTable(BookmarkConstants.MARITAL1,
				personComparisonDto.getPersForwardValueBean().getCdMaritalStatus(), CodesConstant.CMARSTAT);
		bookmarkNonFormGrpList.add(bookmarkCdPersonMaritalStatus);
		BookmarkDto bookmarkCdPersonReligion = createBookmarkWithCodesTable(BookmarkConstants.RELIG1,
				personComparisonDto.getPersForwardValueBean().getCdReligion(), CodesConstant.CRELIGNS);
		bookmarkNonFormGrpList.add(bookmarkCdPersonReligion);
		BookmarkDto bookmarkNmPersonFirst = createBookmark(BookmarkConstants.NM_FIRST1,
				personComparisonDto.getPersonDto().getNmPersonFirst());
		bookmarkNonFormGrpList.add(bookmarkNmPersonFirst);
		BookmarkDto bookmarkNmPersonLast = createBookmark(BookmarkConstants.NM_LAST1,
				personComparisonDto.getPersonDto().getNmPersonLast());
		bookmarkNonFormGrpList.add(bookmarkNmPersonLast);
		BookmarkDto bookmarkNmPersonMiddle = createBookmark(BookmarkConstants.NM_MIDDLE1,
				personComparisonDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonFormGrpList.add(bookmarkNmPersonMiddle);
		BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.PID1,
				personComparisonDto.getPersonDto().getIdPerson());
		bookmarkNonFormGrpList.add(bookmarkIdPerson);

		BookmarkDto bookmarkCdPersonSex2 = createBookmarkWithCodesTable(BookmarkConstants.GENDER2,
				personComparisonDto.getPersClosedValueBean().getSex(), CodesConstant.CSEX);
		bookmarkNonFormGrpList.add(bookmarkCdPersonSex2);
		BookmarkDto bookmarkDtPersonBirth2 = createBookmark(BookmarkConstants.DOB2,
				DateUtils.stringDt(personComparisonDto.getPersClosedValueBean().getDtDateOfBirth()));
		bookmarkNonFormGrpList.add(bookmarkDtPersonBirth2);
		BookmarkDto bookmarkDtPersonDeath2 = createBookmark(BookmarkConstants.DOD2,
				DateUtils.stringDt(personComparisonDto.getPersClosedValueBean().getDtOfDeath()));
		bookmarkNonFormGrpList.add(bookmarkDtPersonDeath2);
		BookmarkDto bookmarkNbrPersonAge2 = createBookmark(BookmarkConstants.AGE2,
				(short) DateUtils.getAge(personComparisonDto.getPersClosedValueBean().getDtDateOfBirth()));
		bookmarkNonFormGrpList.add(bookmarkNbrPersonAge2);
		BookmarkDto bookmarkCdPersonSuffix2 = createBookmarkWithCodesTable(BookmarkConstants.NM_SUFFIX2,
				personComparisonDto.getPersonDto2().getCdPersonSuffix(), CodesConstant.CSUFFIX);
		bookmarkNonFormGrpList.add(bookmarkCdPersonSuffix2);
		BookmarkDto bookmarkCdPersonDeath2 = createBookmarkWithCodesTable(BookmarkConstants.DEATHRSN2,
				personComparisonDto.getPersClosedValueBean().getCdReasonForDeath(), CodesConstant.CRSNFDTH);
		bookmarkNonFormGrpList.add(bookmarkCdPersonDeath2);
		BookmarkDto bookmarkCdPersonLanguage2 = createBookmarkWithCodesTable(BookmarkConstants.LANG2,
				personComparisonDto.getPersClosedValueBean().getCdPrimaryLanguage(), CodesConstant.CLANG);
		bookmarkNonFormGrpList.add(bookmarkCdPersonLanguage2);
		BookmarkDto bookmarkCdPersonLivArr2 = createBookmarkWithCodesTable(BookmarkConstants.LIVARR2,
				personComparisonDto.getPersClosedValueBean().getCdLivingArrangement(), CodesConstant.CLIVARR);
		bookmarkNonFormGrpList.add(bookmarkCdPersonLivArr2);
		BookmarkDto bookmarkCdPersonMaritalStatus2 = createBookmarkWithCodesTable(BookmarkConstants.MARITAL2,
				personComparisonDto.getPersClosedValueBean().getCdMaritalStatus(), CodesConstant.CMARSTAT);
		bookmarkNonFormGrpList.add(bookmarkCdPersonMaritalStatus2);
		BookmarkDto bookmarkCdPersonReligion2 = createBookmarkWithCodesTable(BookmarkConstants.RELIG2,
				personComparisonDto.getPersClosedValueBean().getCdReligion(), CodesConstant.CRELIGNS);
		bookmarkNonFormGrpList.add(bookmarkCdPersonReligion2);
		BookmarkDto bookmarkNmPersonFirst2 = createBookmark(BookmarkConstants.NM_FIRST2,
				personComparisonDto.getPersonDto2().getNmPersonFirst());
		bookmarkNonFormGrpList.add(bookmarkNmPersonFirst2);
		BookmarkDto bookmarkNmPersonLast2 = createBookmark(BookmarkConstants.NM_LAST2,
				personComparisonDto.getPersonDto2().getNmPersonLast());
		bookmarkNonFormGrpList.add(bookmarkNmPersonLast2);
		BookmarkDto bookmarkNmPersonMiddle2 = createBookmark(BookmarkConstants.NM_MIDDLE2,
				personComparisonDto.getPersonDto2().getNmPersonMiddle());
		bookmarkNonFormGrpList.add(bookmarkNmPersonMiddle2);
		BookmarkDto bookmarkIdPerson2 = createBookmark(BookmarkConstants.PID2,
				personComparisonDto.getPersonDto2().getIdPerson());
		bookmarkNonFormGrpList.add(bookmarkIdPerson2);

		// CLSS80D
		for (PersonEthnicityOutDto personEthnicityOutDto : personComparisonDto.getPersonEthnicityOutDtos()) {
			BookmarkDto bookmarkCdPersonEthnicity1 = createBookmarkWithCodesTable(BookmarkConstants.ETHNIC1,
					personEthnicityOutDto.getCdPersonEthnicity(), CodesConstant.CINDETHN);
			bookmarkNonFormGrpList.add(bookmarkCdPersonEthnicity1);
		}
		for (PersonEthnicityOutDto personEthnicityOutDto : personComparisonDto.getPersonEthnicityOutDtos2()) {
			BookmarkDto bookmarkCdPersonEthnicity2 = createBookmarkWithCodesTable(BookmarkConstants.ETHNIC2,
					personEthnicityOutDto.getCdPersonEthnicity(), CodesConstant.CINDETHN);
			bookmarkNonFormGrpList.add(bookmarkCdPersonEthnicity2);
		}

		// CCMN44D
		BookmarkDto bookmarkCdOccupation = createBookmarkWithCodesTable(BookmarkConstants.OCC1,
				personComparisonDto.getPersonDto41().getCdOccupation(), CodesConstant.COCCUPTN);
		bookmarkNonFormGrpList.add(bookmarkCdOccupation);
		BookmarkDto bookmarkCdOccupation2 = createBookmarkWithCodesTable(BookmarkConstants.OCC2,
				personComparisonDto.getPersonDto42().getCdOccupation(), CodesConstant.COCCUPTN);
		bookmarkNonFormGrpList.add(bookmarkCdOccupation2);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);

		return preFillData;
	}

}
