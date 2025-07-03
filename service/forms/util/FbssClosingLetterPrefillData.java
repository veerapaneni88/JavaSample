package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.FbssClosingLetterDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AssignWorkloadPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form WKLD0100. March 8, 2018- 2:04:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */

@Component

public class FbssClosingLetterPrefillData extends DocumentServiceUtil {

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

		FbssClosingLetterDto fbssClosingLetterDto = (FbssClosingLetterDto) parentDtoobj;

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		List<FormDataGroupDto> formDataTempGroupList = new ArrayList<FormDataGroupDto>();

		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		/** LOGO CAPTION **/

		BookmarkDto bookmarkDirectorTitle = createBookmarkWithCodesTable(BookmarkConstants.DIRECTOR_TITLE,
				FormConstants.CBRDTTLE_CODE, CodesConstant.CBRDTTLE);
		BookmarkDto bookmarkDirectorName = createBookmarkWithCodesTable(BookmarkConstants.DIRECTOR_NAME,
				FormConstants.CBRDNAME_CODE, CodesConstant.CBRDNAME);

		/** MAIL DATE **/
		BookmarkDto bookmarkSysDate = createBookmark(BookmarkConstants.SYSTEM_DATE,
				TypeConvUtil.formDateFormat(new java.util.Date()));

		bookmarkNonFrmGrpList.add(bookmarkDirectorTitle);
		bookmarkNonFrmGrpList.add(bookmarkDirectorName);
		bookmarkNonFrmGrpList.add(bookmarkSysDate);

		/** ADDRESSEE NAME **/

		if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPerson())) {

			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPerson().getNmPersonFirst())) {
			BookmarkDto bookmarkAddressFName = createBookmark(BookmarkConstants.ADDRESSEE_FNAME,
					fbssClosingLetterDto.getPerson().getNmPersonFirst());
			bookmarkNonFrmGrpList.add(bookmarkAddressFName);
			/** DEAR ADDRESSEE NAME **/

			BookmarkDto bookmarkDearFName = createBookmark(BookmarkConstants.DEAR_FNAME,
					fbssClosingLetterDto.getPerson().getNmPersonFirst());
			
			bookmarkNonFrmGrpList.add(bookmarkDearFName);
			}
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPerson().getNmPersonMiddle())) {
			BookmarkDto bookmarkAddressMName = createBookmark(BookmarkConstants.ADDRESSEE_MNAME,
					fbssClosingLetterDto.getPerson().getNmPersonMiddle());
			bookmarkNonFrmGrpList.add(bookmarkAddressMName);
			
			BookmarkDto bookmarkDearMName = createBookmark(BookmarkConstants.DEAR_MNAME,
					fbssClosingLetterDto.getPerson().getNmPersonMiddle());
			bookmarkNonFrmGrpList.add(bookmarkDearMName);
			}
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPerson().getNmPersonLast())) {
			BookmarkDto bookmarkAddressLName = createBookmark(BookmarkConstants.ADDRESSEE_LNAME,
					fbssClosingLetterDto.getPerson().getNmPersonLast());
			bookmarkNonFrmGrpList.add(bookmarkAddressLName);
			
			BookmarkDto bookmarkDearLName = createBookmark(BookmarkConstants.DEAR_LNAME,
					fbssClosingLetterDto.getPerson().getNmPersonLast());			
			bookmarkNonFrmGrpList.add(bookmarkDearLName);
			}			

			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPerson().getCdPersonSuffix())) {
				FormDataGroupDto tempAddressSuffix = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESSEE_SUFFIX,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSuffixList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAddressSuffix = createBookmarkWithCodesTable(BookmarkConstants.ADDRESSEE_SUFFIX,
						fbssClosingLetterDto.getPerson().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
				bookmarkSuffixList.add(bookmarkAddressSuffix);
				tempAddressSuffix.setBookmarkDtoList(bookmarkSuffixList);
				formDataGroupList.add(tempAddressSuffix);

			}			

			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPerson())) {
				if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPerson().getCdPersonSuffix())) {
				FormDataGroupDto tempdearSuffix = createFormDataGroup(FormGroupsConstants.TMPLAT_DEAR_SUFFIX,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkdearSuffixList = new ArrayList<BookmarkDto>();				
				BookmarkDto bookmarkdearSuffix = createBookmarkWithCodesTable(BookmarkConstants.DEAR_SUFFIX,
						fbssClosingLetterDto.getPerson().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
				bookmarkdearSuffixList.add(bookmarkdearSuffix);
				tempdearSuffix.setBookmarkDtoList(bookmarkdearSuffixList);
				formDataGroupList.add(tempdearSuffix);
				}
			}
		}

		/** ADDRESSEE ADDRESS **/

		if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPersonaddressValueDto())) {

			FormDataGroupDto tempDearAddr = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkDearAddr = new ArrayList<BookmarkDto>();
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPersonaddressValueDto().getStreetLn1())) {

			BookmarkDto bookmarkDearAddrLn1 = createBookmark(BookmarkConstants.ADDRESS_LINE1,
					fbssClosingLetterDto.getPersonaddressValueDto().getStreetLn1());
			bookmarkDearAddr.add(bookmarkDearAddrLn1);
			}
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPersonaddressValueDto().getCity())) {
			BookmarkDto bookmarkDearAddrCity = createBookmark(BookmarkConstants.ADDRESS_CITY,
					fbssClosingLetterDto.getPersonaddressValueDto().getCity());
			bookmarkDearAddr.add(bookmarkDearAddrCity);
			}
			//defect #13108 added code to populate state in address to field of FBSS closing letter.
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPersonaddressValueDto().getState())) {
			BookmarkDto bookmarkDearAddrState = createBookmark(BookmarkConstants.ADDRESS_STATE,
					fbssClosingLetterDto.getPersonaddressValueDto().getState());
			bookmarkDearAddr.add(bookmarkDearAddrState);
			}
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPersonaddressValueDto().getZip())) {
			BookmarkDto bookmarkDearAddrZip = createBookmark(BookmarkConstants.ADDRESS_ZIP,
					fbssClosingLetterDto.getPersonaddressValueDto().getZip());
			bookmarkDearAddr.add(bookmarkDearAddrZip);
			}			
			
			tempDearAddr.setBookmarkDtoList(bookmarkDearAddr);

			// formDataGroupList.add(tempWorkAddr);
				if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getPersonaddressValueDto().getStreetLn2())) {
					FormDataGroupDto tempDearAddrLn2 = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESS_LINE2,
							FormGroupsConstants.TMPLAT_ADDRESS);
					List<BookmarkDto> bookmarkDearAddrLine2 = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkDearAddrLn2 = createBookmark(BookmarkConstants.ADDRESS_LINE2,
							TypeConvUtil.formatPhone(fbssClosingLetterDto.getPersonaddressValueDto().getStreetLn2()));
					
					bookmarkDearAddrLine2.add(bookmarkDearAddrLn2);
					
					tempDearAddrLn2.setBookmarkDtoList(bookmarkDearAddrLine2);
					formDataTempGroupList.add(tempDearAddrLn2);
				}

			tempDearAddr.setFormDataGroupList(formDataTempGroupList);
			formDataGroupList.add(tempDearAddr);

		}

		/** WORKER NAME **/

		if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getNameDto())) {

			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getNameDto().getNmNameFirst())) {
			BookmarkDto bookmarkWorkerFName = createBookmark(BookmarkConstants.WORKER_FNAME,
					fbssClosingLetterDto.getNameDto().getNmNameFirst());
			bookmarkNonFrmGrpList.add(bookmarkWorkerFName);
			}
			/**Defect#13486 Commented to remove middle name from Worker name ***/
			/*if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getNameDto().getNmNameMiddle())) {
			BookmarkDto bookmarkWorkerMName = createBookmark(BookmarkConstants.WORKER_MNAME,
					fbssClosingLetterDto.getNameDto().getNmNameMiddle());
			bookmarkNonFrmGrpList.add(bookmarkWorkerMName);
			}*/
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getNameDto().getNmNameLast())) {
			BookmarkDto bookmarkWorkerLName = createBookmark(BookmarkConstants.WORKER_LNAME,
					fbssClosingLetterDto.getNameDto().getNmNameLast());
			bookmarkNonFrmGrpList.add(bookmarkWorkerLName);
			}
			
			
			

			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getNameDto().getCdNameSuffix()))

			{

				FormDataGroupDto tempWorkerSuffix = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_SUFFIX,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkWorkerSuffixList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkWorkerSuffix = createBookmarkWithCodesTable(BookmarkConstants.WORKER_SUFFIX,
						fbssClosingLetterDto.getPerson().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
				bookmarkWorkerSuffixList.add(bookmarkWorkerSuffix);
				tempWorkerSuffix.setBookmarkDtoList(bookmarkWorkerSuffixList);
				formDataGroupList.add(tempWorkerSuffix);

			}
		}

		/** WORKER MAIL AND PHONE **/

		// Warranty Defect Fix - 11375 - Modified the Logic to display the Address and Phone Number as Default Value
		if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getWorkeraddressValueDto())) {

			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getWorkeraddressValueDto().getStreetLn1()))
			{
			BookmarkDto bookmarkWorkAddrLn1 = createBookmark(BookmarkConstants.WORKADDR_LINE1,
					fbssClosingLetterDto.getWorkeraddressValueDto().getStreetLn1());
			bookmarkNonFrmGrpList.add(bookmarkWorkAddrLn1);
			}
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getWorkeraddressValueDto().getCity()))
			{
			BookmarkDto bookmarkWorkAddrCity = createBookmark(BookmarkConstants.WORKADDR_CITY,
					fbssClosingLetterDto.getWorkeraddressValueDto().getCity());
			bookmarkNonFrmGrpList.add(bookmarkWorkAddrCity);
			}
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getWorkeraddressValueDto().getZip()))
			{
			BookmarkDto bookmarkWorkAddrZip = createBookmark(BookmarkConstants.WORKADDR_ZIP,
					fbssClosingLetterDto.getWorkeraddressValueDto().getZip());
			bookmarkNonFrmGrpList.add(bookmarkWorkAddrZip);
			}
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getWorkeraddressValueDto().getZip()))
			{
			BookmarkDto bookmarkWorkAddrPhone = createBookmark(BookmarkConstants.WORKADDR_PHONE,
					TypeConvUtil.formatPhone(fbssClosingLetterDto.getMailDto().getPhone()));
			bookmarkNonFrmGrpList.add(bookmarkWorkAddrPhone);
			}
			
			List<FormDataGroupDto> formDataGroupworkAddrList = new ArrayList<FormDataGroupDto>();
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getWorkeraddressValueDto().getStreetLn2())) {

				FormDataGroupDto tempWorkAddrLn2 = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKADDR_LINE2,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkWorkAddrLine2 = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkWorkAddrLn2 = createBookmark(BookmarkConstants.WORKADDR_LINE2,
						fbssClosingLetterDto.getWorkeraddressValueDto().getStreetLn2());
				bookmarkWorkAddrLine2.add(bookmarkWorkAddrLn2);
				tempWorkAddrLn2.setBookmarkDtoList(bookmarkWorkAddrLine2);
				formDataGroupList.add(tempWorkAddrLn2);

			}

			
			if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getMailDto())) {
				if (!TypeConvUtil.isNullOrEmpty(fbssClosingLetterDto.getMailDto().getPhoneExtension())) {
					List<BookmarkDto> bookmarkPhoneNoExtz = new ArrayList<BookmarkDto>();
					FormDataGroupDto tempWorkPhoneNoExtn = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKADDR_PHONE,
							FormConstants.EMPTY_STRING);
					BookmarkDto bookmarkPhoneNoExtn = createBookmark(BookmarkConstants.WORKADDR_PHONE_EXT,
						fbssClosingLetterDto.getMailDto().getPhoneExtension());
					bookmarkPhoneNoExtz.add(bookmarkPhoneNoExtn);
					tempWorkPhoneNoExtn.setBookmarkDtoList(bookmarkPhoneNoExtz);
					formDataGroupList.add(tempWorkPhoneNoExtn);
				}

			}	

		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);

		return preFillData;

	}
}
