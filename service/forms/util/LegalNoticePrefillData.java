package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.admin.dto.EmployeeDto;
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
import us.tx.state.dfps.service.legal.dto.LegalNoticeDtlDto;
import us.tx.state.dfps.service.legal.dto.LegalNoticeFormDto;
import us.tx.state.dfps.service.legal.dto.LegalNoticeRecpntDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dto.PersonPhoneRetDto;

@Component
public class LegalNoticePrefillData extends DocumentServiceUtil {

	@Autowired
	LookupDao lookupDao;

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		LegalNoticeFormDto legalNoticeFormDto = (LegalNoticeFormDto) parentDtoobj;

		if (null == legalNoticeFormDto.getLegalNoticeDtlDto()) {
			legalNoticeFormDto.setLegalNoticeDtlDto(new LegalNoticeDtlDto());
		}
		if (null == legalNoticeFormDto.getEmployeeDto()) {
			legalNoticeFormDto.setEmployeeDto(new EmployeeDto());
		}
		if (null == legalNoticeFormDto.getPersonPhoneRetDto()) {
			legalNoticeFormDto.setPersonPhoneRetDto(new PersonPhoneRetDto());
		}
		if (null == legalNoticeFormDto.getLegalNoticeRecpntDto()) {
			legalNoticeFormDto.setLegalNoticeRecpntDto(new LegalNoticeRecpntDto());
		}
		if (null == legalNoticeFormDto.getPerson()) {
			legalNoticeFormDto.setPerson(new Person());
		}

		/**
		 * prefill data - TMPLAT_HEADER_DIRECTOR
		 */

		FormDataGroupDto tempHdrDirectorFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_DIRECTOR,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkDirectorDtls = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkDirectorTitle = createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE,
				CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE);
		BookmarkDto bookmarkDirectorName = createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME,
				CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME);
		bookmarkDirectorDtls.add(bookmarkDirectorTitle);
		bookmarkDirectorDtls.add(bookmarkDirectorName);
		tempHdrDirectorFrmDataGrpDto.setBookmarkDtoList(bookmarkDirectorDtls);
		formDataGroupList.add(tempHdrDirectorFrmDataGrpDto);

		/**
		 * prefill data - TMPLAT_COMMA1
		 */

		FormDataGroupDto tempCommaoneFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA1,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(tempCommaoneFrmDataGrpDto);

		/**
		 * prefill data - TMPLAT_ADDRESS
		 * 
		 */

		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeRecpntDto())) {

			FormDataGroupDto tempAddrFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkAddrDtls = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDRESS_LINE_1,
					legalNoticeFormDto.getLegalNoticeRecpntDto().getPersAddrStLn1());
			BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDRESS_LINE_2,
					legalNoticeFormDto.getLegalNoticeRecpntDto().getPersAddrStLn2());
			BookmarkDto bookmarkCity = createBookmark(BookmarkConstants.CITY,
					legalNoticeFormDto.getLegalNoticeRecpntDto().getPersonAddrCity());
			BookmarkDto bookmarkState = createBookmark(BookmarkConstants.STATE,
					legalNoticeFormDto.getLegalNoticeRecpntDto().getCdPersonAddrState());
			BookmarkDto bookmarkZip = createBookmark(BookmarkConstants.ZIP,
					legalNoticeFormDto.getLegalNoticeRecpntDto().getPersonAddrZip());
			bookmarkAddrDtls.add(bookmarkAddrLn1);
			bookmarkAddrDtls.add(bookmarkAddrLn2);
			bookmarkAddrDtls.add(bookmarkCity);
			bookmarkAddrDtls.add(bookmarkState);
			bookmarkAddrDtls.add(bookmarkZip);
			tempAddrFrmDataGrpDto.setBookmarkDtoList(bookmarkAddrDtls);
			formDataGroupList.add(tempAddrFrmDataGrpDto);

		}

		/**
		 * prefill data - TMPLAT_COURT_ADDRESS
		 * 
		 */

		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeDtlDto().getTxtCourtAddrRoom())) {
			FormDataGroupDto tempCourtAddrFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COURT_ADDRESS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkCourtAddrDtls = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkCourtAddrLn1 = createBookmark(BookmarkConstants.COURT_ADDRESS_LINE_1,
					legalNoticeFormDto.getLegalNoticeDtlDto().getTxtCourtAddrRoom());
			bookmarkCourtAddrDtls.add(bookmarkCourtAddrLn1);
			tempCourtAddrFrmDataGrpDto.setBookmarkDtoList(bookmarkCourtAddrDtls);
			formDataGroupList.add(tempCourtAddrFrmDataGrpDto);

		}

		String ChildNameList = null;
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeDtlDto().getChildrenMap())) {
			Map<Long, String> childrenMap = legalNoticeFormDto.getLegalNoticeDtlDto().getChildrenMap();

			for (String value : childrenMap.values()) {				
				// Warranty Defect - 11950 - Legal Notice Name to be Displayed as First Name followed by Last Name
				String[] childName=value.split(",");
				ChildNameList = childName[1]+FormConstants.SPACE +childName[0]+FormConstants.SPACE_COMMA_SPACE+ChildNameList;
			}

			ChildNameList = ChildNameList.substring(0, ChildNameList.length() - 6);

		}

		/**
		 * prefill data - TMPLAT_CHILDS_NAME
		 * 
		 */

		FormDataGroupDto tempChildNmFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILDS_NAME,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkChildNmDtls = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkChildFirstName = createBookmark(BookmarkConstants.CHILD_NAME_FIRST, ChildNameList);
		bookmarkChildNmDtls.add(bookmarkChildFirstName);
		tempChildNmFrmDataGrpDto.setBookmarkDtoList(bookmarkChildNmDtls);
		formDataGroupList.add(tempChildNmFrmDataGrpDto);

		/**
		 * prefill data - TMPLAT_HEARING_NAME
		 * 
		 */

		FormDataGroupDto tempHearingNmFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEARING_NAME,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkHearingNmDtls = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkHearingFirstName = createBookmark(BookmarkConstants.CHILD_HEARING_FIRST, ChildNameList);
		bookmarkHearingNmDtls.add(bookmarkHearingFirstName);
		tempHearingNmFrmDataGrpDto.setBookmarkDtoList(bookmarkHearingNmDtls);
		formDataGroupList.add(tempHearingNmFrmDataGrpDto);

		/**
		 * Populating the non form group data into prefill data
		 */

		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Bookmark for DT_NOTICE

		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeDtlDto().getDtCreated())) {

			BookmarkDto bookmarkNoticeDate = createBookmark(BookmarkConstants.DT_NOTICE,
					TypeConvUtil.formDateFormat(legalNoticeFormDto.getLegalNoticeDtlDto().getDtCreated()));
			bookmarkNonFrmGrpList.add(bookmarkNoticeDate);
		}

		// Bookmark for Additional Information

		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeRecpntDto())) {
			BookmarkDto bookmarkAdditionalInfo = createBookmark(BookmarkConstants.ADDITIONAL_INFORMATION,
					legalNoticeFormDto.getLegalNoticeRecpntDto().getTxtAdditionalMsg());
			bookmarkNonFrmGrpList.add(bookmarkAdditionalInfo);
		}

		// Bookmark for Worker Signature Details
		BookmarkDto bookmarkWorkerEmail = null;
		BookmarkDto bookmarkWorkerTitle = null;
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getEmployeeDto())) {

			bookmarkWorkerEmail = createBookmark(BookmarkConstants.WORKER_EMAIL,
					legalNoticeFormDto.getEmployeeDto().getEmployeeEmailAddress());
			bookmarkWorkerTitle = createBookmark(BookmarkConstants.TITLE,
					legalNoticeFormDto.getEmployeeDto().getEmployeeClass());

		}

		BookmarkDto bookmarkWorkerPhoneNbr = null;
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getPerson().getNbrPersonPhone())) {

			String FormatPhoneNbr = FormConstants.OPEN_BRACKET
					+ legalNoticeFormDto.getPerson().getNbrPersonPhone().substring(0, 3) + FormConstants.CLOSE_BRACKET
					+ legalNoticeFormDto.getPerson().getNbrPersonPhone().substring(3, 6) + FormConstants.HYPHEN
					+ legalNoticeFormDto.getPerson().getNbrPersonPhone().substring(6, 10);
				bookmarkWorkerPhoneNbr = createBookmark(BookmarkConstants.WORKER_PHONE, FormatPhoneNbr);
		}else{
			bookmarkWorkerPhoneNbr = createBookmark(BookmarkConstants.WORKER_PHONE, FormConstants.EMPTY_STRING );
		}

		BookmarkDto bookmarkWorkerNameFirst = null;
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeDtlDto().getNmCreatedPerson())) {
		// Warranty Defect - 11950 - Legal Notice Name to be Displayed as First Name followed by Last Name
			String[] workerName=legalNoticeFormDto.getLegalNoticeDtlDto().getNmCreatedPerson().split(",");
			bookmarkWorkerNameFirst = createBookmark(BookmarkConstants.WORKER_NAME_FIRST,
					workerName[1]+FormConstants.SPACE+workerName[0]);
		}

		BookmarkDto bookmarkCauseNbr = null;
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLglStatusCauseNum())) {
			bookmarkCauseNbr = createBookmark(BookmarkConstants.CAUSE_NBR, legalNoticeFormDto.getLglStatusCauseNum());
		}

		bookmarkNonFrmGrpList.add(bookmarkWorkerNameFirst);
		bookmarkNonFrmGrpList.add(bookmarkWorkerTitle);
		bookmarkNonFrmGrpList.add(bookmarkWorkerPhoneNbr);
		bookmarkNonFrmGrpList.add(bookmarkCauseNbr);
		bookmarkNonFrmGrpList.add(bookmarkWorkerEmail);

		// Bookmark for Hearing Details
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeDtlDto().getCdNoticeType())) {
			BookmarkDto bookmarkHearingType = createBookmark(BookmarkConstants.HEARING_TYPE, lookupDao
					.decode(ServiceConstants.CLEGNOTYP, legalNoticeFormDto.getLegalNoticeDtlDto().getCdNoticeType()));
			bookmarkNonFrmGrpList.add(bookmarkHearingType);
		}

		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeDtlDto().getDtCourtSchedle())) {
			BookmarkDto bookmarkHearingDate = createBookmark(BookmarkConstants.HEARING_DATE,
					TypeConvUtil.formDateFormat(legalNoticeFormDto.getLegalNoticeDtlDto().getDtCourtSchedle()));
			BookmarkDto bookmarkHearingTime = createBookmark(BookmarkConstants.HEARING_TIME,
					legalNoticeFormDto.getLegalNoticeDtlDto().getCourtScheduledTime());
			bookmarkNonFrmGrpList.add(bookmarkHearingDate);
			bookmarkNonFrmGrpList.add(bookmarkHearingTime);
		}

		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeDtlDto().getNmCourt())) {
			BookmarkDto bookmarkHearingCourt = createBookmark(BookmarkConstants.HEARING_COURT,
					legalNoticeFormDto.getLegalNoticeDtlDto().getNmCourt());
			bookmarkNonFrmGrpList.add(bookmarkHearingCourt);
		}

		// Bookmark for Receipient Name

		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeRecpntDto())) {
			if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeRecpntDto().getPersonFull())) {
			// Warranty Defect - 11950 - Legal Notice Name to be Displayed as First Name followed by Last Name
			String[] receipientName=legalNoticeFormDto.getLegalNoticeRecpntDto().getPersonFull().split(",");	
			BookmarkDto bookmarkFirstName = createBookmark(BookmarkConstants.NAME_FIRST,
					receipientName[1]+FormConstants.SPACE+receipientName[0]);
			bookmarkNonFrmGrpList.add(bookmarkFirstName);
			}
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}

}
