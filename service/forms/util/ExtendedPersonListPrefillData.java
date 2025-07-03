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
import us.tx.state.dfps.service.extperson.dto.ExtPersonDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.PersonExtDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ExtendedPersonListPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for Form per02o00 Apr 9, 2018- 10:53:14 AM © 2017 Texas Department of
 * Family and Protective Services
 */

@Component
public class ExtendedPersonListPrefillData extends DocumentServiceUtil {

	public ExtendedPersonListPrefillData() {
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

		ExtPersonDto extPersonDto = (ExtPersonDto) parentDtoobj;

		if (ObjectUtils.isEmpty(extPersonDto.getPersonExtDtoList())) {
			extPersonDto.setPersonExtDtoList(new ArrayList<PersonExtDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// set the prefill data for group per0201

		for (PersonExtDto personExtDto : extPersonDto.getPersonExtDtoList()) {

			FormDataGroupDto tempExtendedPerson = createFormDataGroup(FormGroupsConstants.TMPLAT_EXTENDED_PERSON_LIST,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempExtendedPerson);

			List<BookmarkDto> bookmarkPersonList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.PERSON_COUNTY,
					personExtDto.getCdCounty(), CodesConstant.CCOUNT);
			bookmarkPersonList.add(bookmarkAddrCounty);
			BookmarkDto bookmarkPersonEthnicGroup = createBookmarkWithCodesTable(BookmarkConstants.PERSON_RACE_ETHNIC,
					personExtDto.getCdPersonEthnicGroup(), CodesConstant.CETHNIC);
			bookmarkPersonList.add(bookmarkPersonEthnicGroup);

			BookmarkDto bookmarkPersonAgeApprx = createBookmark(BookmarkConstants.PERSON_AGE_APPRX,
					personExtDto.getIndPersonDobApprox());
			bookmarkPersonList.add(bookmarkPersonAgeApprx);
			BookmarkDto bookmarkPersonGender = createBookmark(BookmarkConstants.PERSON_GENDER,
					personExtDto.getCdPersonSex());
			bookmarkPersonList.add(bookmarkPersonGender);
			BookmarkDto bookmarkPersonDOB = createBookmark(BookmarkConstants.PERSON_DOB,
					DateUtils.stringDt(personExtDto.getDtPersonBirth()));
			bookmarkPersonList.add(bookmarkPersonDOB);
			BookmarkDto bookmarkPersonAge = createBookmark(BookmarkConstants.PERSON_AGE, personExtDto.getPersonAge());
			bookmarkPersonList.add(bookmarkPersonAge);
			BookmarkDto bookmarkPersonCity = createBookmark(BookmarkConstants.PERSON_CITY, personExtDto.getCdCity());
			bookmarkPersonList.add(bookmarkPersonCity);
			BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.PERSON_ADDR_LN1,
					personExtDto.getAddrLine1());
			bookmarkPersonList.add(bookmarkAddrLn1);

			BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.PERSON_ID, personExtDto.getIdPerson());
			bookmarkPersonList.add(bookmarkIdPerson);

			BookmarkDto bookmarkPersonSSNNbr = createBookmark(BookmarkConstants.PERSON_SSN_NBR,
					personExtDto.getPersonIdNumber());
			bookmarkPersonList.add(bookmarkPersonSSNNbr);
			BookmarkDto bookmarkNmPersonFull = createBookmark(BookmarkConstants.NM_PERSON_FULL,
					personExtDto.getNmPersonFull());
			bookmarkPersonList.add(bookmarkNmPersonFull);

			tempExtendedPerson.setBookmarkDtoList(bookmarkPersonList);

			List<FormDataGroupDto> personGroupList = new ArrayList<FormDataGroupDto>();
			tempExtendedPerson.setFormDataGroupList(personGroupList);

			// set the prefill data for group per0204 : sub group of per0201

			if (!ObjectUtils.isEmpty(personExtDto.getDtPersonBirth())
					&& !ObjectUtils.isEmpty(personExtDto.getPersonAge())) {
				FormDataGroupDto ageGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_AGE,
						FormGroupsConstants.TMPLAT_EXTENDED_PERSON_LIST);
				personGroupList.add(ageGroup);
				List<BookmarkDto> ageBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkNbrPersonAge = createBookmark(BookmarkConstants.PERSON_AGE,
						personExtDto.getPersonAge());
				ageBookmarkList.add(bookmarkNbrPersonAge);

				ageGroup.setBookmarkDtoList(ageBookmarkList);

				List<FormDataGroupDto> bracketGroupList = new ArrayList<FormDataGroupDto>();

				if (!ObjectUtils.isEmpty(personExtDto.getDtPersonDeath())) {

					// set the prefill data for group per0205 : sub group of
					// per0204

					FormDataGroupDto ageBracketGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_AGE_OPEN_BRACKET,
							FormGroupsConstants.TMPLAT_AGE);
					bracketGroupList.add(ageBracketGroup);

					// set the prefill data for group per0206 : sub group of
					// per0204

					FormDataGroupDto ageBracketCloseGroup = createFormDataGroup(
							FormGroupsConstants.TMPLAT_AGE_CLOSE_BRACKET, FormGroupsConstants.TMPLAT_AGE);
					bracketGroupList.add(ageBracketCloseGroup);

				}

				ageGroup.setFormDataGroupList(bracketGroupList);
			}

		}

		// bookmarks without groups
		// CSEC02D
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				extPersonDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonFormGrpList.add(bookmarkNmCase);

		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				extPersonDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonFormGrpList.add(bookmarkIdCase);

		// CCMN44D

		BookmarkDto bookmarkNmPerson = createBookmark(BookmarkConstants.EXTENDED_NM_PERSON_FULL,
				extPersonDto.getPersonDto().getNmPersonFull());
		bookmarkNonFormGrpList.add(bookmarkNmPerson);

		BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.EXTENDED_PERSON_ID,
				extPersonDto.getPersonDto().getIdPerson());
		bookmarkNonFormGrpList.add(bookmarkIdPerson);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);

		return preFillData;
	}

}
