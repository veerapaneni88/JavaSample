package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.casepackage.dto.CasePersonListDto;
import us.tx.state.dfps.service.casepackage.dto.CasePersonListFormDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CasePersonListFormPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form per01o00. March 20, 2018- 2:04:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */

@Component
public class CasePersonListFormPrefillData extends DocumentServiceUtil {

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

	@Autowired
	LookupDao lookupDao;

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		CasePersonListFormDto casePersonListFormDto = (CasePersonListFormDto) parentDtoobj;

		if (null == casePersonListFormDto.getGenericCaseInfoDto()) {
			casePersonListFormDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}
		if (null == casePersonListFormDto.getCasePersonListDto()) {
			List<CasePersonListDto> casePersonListDto = null;
			casePersonListFormDto.setCasePersonListDto(casePersonListDto);
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		/**
		 * prefill data for group per0101 - TMPLAT_CASE_PERSON_LIST
		 */

		List<CasePersonListDto> casePersonListDtolist = casePersonListFormDto.getCasePersonListDto();

		if (casePersonListDtolist.size() > ServiceConstants.Zero_INT) {

			for (CasePersonListDto casePersonListDto : casePersonListDtolist) {

				FormDataGroupDto tempCasePersonListFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CASE_PERSON_LIST, FormConstants.EMPTY_STRING);

				List<FormDataGroupDto> tempCasePeronListformDataGroupList = new ArrayList<FormDataGroupDto>();

				List<BookmarkDto> bookmarCasePersonList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPersonAgeApprx = createBookmark(BookmarkConstants.PERSON_AGE_APPRX,
						casePersonListDto.getIndPersonDobApprox());
				BookmarkDto bookmarkPersonGender = createBookmark(BookmarkConstants.PERSON_GENDER,
						casePersonListDto.getCdPersonSex());
				BookmarkDto bookmarkPersonDOB = createBookmark(BookmarkConstants.PERSON_DOB,
						TypeConvUtil.formDateFormat(casePersonListDto.getDtPersonBirth()));
				BookmarkDto bookmarkPersonDOD = createBookmark(BookmarkConstants.PERSON_DOD,
						TypeConvUtil.formDateFormat(casePersonListDto.getDtPersonDeath()));
				BookmarkDto bookmarkPersonAge = createBookmark(BookmarkConstants.PERSON_AGE,
						casePersonListDto.getNbrAge());
				BookmarkDto bookmarkPersonCity = createBookmark(BookmarkConstants.PERSON_CITY,
						casePersonListDto.getAddrPersonCity());
				BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.PERSON_ADDR_LN1,
						casePersonListDto.getAddrPersonStLn1());

				BookmarkDto bookmarkPersonCounty = null;
				if (!TypeConvUtil.isNullOrEmpty(casePersonListDto.getCdPersonCounty())) {
					bookmarkPersonCounty = createBookmark(BookmarkConstants.PERSON_COUNTY,
							lookupDao.decode(ServiceConstants.CCOUNT, casePersonListDto.getCdPersonCounty()));
				} else {
					bookmarkPersonCounty = createBookmark(BookmarkConstants.PERSON_COUNTY, FormConstants.EMPTY_STRING);

				}

				BookmarkDto bookmarkPersonRaceEthnic = null;

				if (!TypeConvUtil.isNullOrEmpty(casePersonListDto.getCdPersonEthnicGroup())) {
					bookmarkPersonRaceEthnic = createBookmark(BookmarkConstants.PERSON_RACE_ETHNIC,
							lookupDao.decode(ServiceConstants.CETHNIC, casePersonListDto.getCdPersonEthnicGroup()));
				} else {
					bookmarkPersonRaceEthnic = createBookmark(BookmarkConstants.PERSON_RACE_ETHNIC,
							FormConstants.EMPTY_STRING);

				}
				BookmarkDto bookmarkPersonSSNNbr = createBookmark(BookmarkConstants.PERSON_SSN_NBR,
						casePersonListDto.getNbrPersonIdNumber());
				BookmarkDto bookmarkNmPersonFull = createBookmark(BookmarkConstants.NM_PERSON_FULL,
						casePersonListDto.getNmPersonFull());
				BookmarkDto bookmarkPersonId = createBookmark(BookmarkConstants.PERSON_ID,
						casePersonListDto.getIdPerson());
				bookmarCasePersonList.add(bookmarkPersonAgeApprx);
				bookmarCasePersonList.add(bookmarkPersonGender);
				bookmarCasePersonList.add(bookmarkPersonDOB);
				bookmarCasePersonList.add(bookmarkPersonDOD);
				bookmarCasePersonList.add(bookmarkPersonAge);
				bookmarCasePersonList.add(bookmarkPersonCity);
				bookmarCasePersonList.add(bookmarkAddrLn1);
				bookmarCasePersonList.add(bookmarkPersonCounty);
				bookmarCasePersonList.add(bookmarkPersonRaceEthnic);
				bookmarCasePersonList.add(bookmarkPersonSSNNbr);
				bookmarCasePersonList.add(bookmarkNmPersonFull);
				bookmarCasePersonList.add(bookmarkPersonId);
				tempCasePersonListFrmDataGrpDto.setBookmarkDtoList(bookmarCasePersonList);

				/**
				 * prefill data for group per0102 - TMPLAT_AGE
				 */

				FormDataGroupDto tempAgeFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AGE,
						FormGroupsConstants.TMPLAT_CASE_PERSON_LIST);
				if (!TypeConvUtil.isNullOrEmpty(casePersonListDto.getDtPersonBirth())) {

					List<BookmarkDto> bookmarAgelist = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkAge = createBookmark(BookmarkConstants.PERSON_AGE,
							casePersonListDto.getNbrAge());
					bookmarAgelist.add(bookmarkAge);
					tempAgeFrmDataGrpDto.setBookmarkDtoList(bookmarAgelist);
				}

				tempCasePeronListformDataGroupList.add(tempAgeFrmDataGrpDto);

				if (!TypeConvUtil.isNullOrEmpty(casePersonListDto.getDtPersonDeath())) {

					/**
					 * prefill data for group per0103 - TMPLAT_AGE_OPEN_BRACKET
					 */

					FormDataGroupDto tempAgeOpenBracket = createFormDataGroup(
							FormGroupsConstants.TMPLAT_AGE_OPEN_BRACKET, FormGroupsConstants.TMPLAT_AGE);
					tempCasePeronListformDataGroupList.add(tempAgeOpenBracket);

					/**
					 * prefill data for group per0104 - TMPLAT_AGE_CLOSE_BRACKET
					 */

					FormDataGroupDto tempAgeCloseBracket = createFormDataGroup(
							FormGroupsConstants.TMPLAT_AGE_CLOSE_BRACKET, FormGroupsConstants.TMPLAT_AGE);
					tempCasePeronListformDataGroupList.add(tempAgeCloseBracket);

				}

				tempCasePersonListFrmDataGrpDto.setFormDataGroupList(tempCasePeronListformDataGroupList);
				formDataGroupList.add(tempCasePersonListFrmDataGrpDto);
			}

		}

		/**
		 * Populating the non form group data into prefill data
		 */

		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				casePersonListFormDto.getGenericCaseInfoDto().getNmCase());
		BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				casePersonListFormDto.getGenericCaseInfoDto().getIdCase());

		bookmarkNonFrmGrpList.add(bookmarkTitleCaseName);
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNumber);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

}
