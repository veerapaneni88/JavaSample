package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.alternativeresponse.dto.ArConcRepNotifDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvPrincipalDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.recordscheck.dto.EmployeePersonDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Builds
 * prefill string for ARRENOT and ARRENOTS from data obtained by service May 9,
 * 2018- 10:01:16 AM © 2017 Texas Department of Family and Protective Services
 * ********Change History**********
 * 01/09/2023 thompswa artf238090 CodesConstant CCLOSAR replaces LEVEL_CARE adding new admin close codes.
 */
@Component
public class ArConcRepNotifPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		ArConcRepNotifDto prefillDto = (ArConcRepNotifDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonGroupList = new ArrayList<BookmarkDto>();

		// initialize null dtos
		if (ObjectUtils.isEmpty(prefillDto.getPersonDto())) {
			prefillDto.setPersonDto(new PersonDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getAddressValueDto())) {
			prefillDto.setAddressValueDto(new AddressValueDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getCpsInvPrincipalsList())) {
			prefillDto.setCpsInvPrincipalsList(new ArrayList<CpsInvPrincipalDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getWorkerDto())) {
			prefillDto.setWorkerDto(new PersonDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getEmployeePersPhNameDto())) {
			prefillDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getEmployeePersonDto())) {
			prefillDto.setEmployeePersonDto(new EmployeePersonDto());
		}

		// Director info
		BookmarkDto bookmarkHeaderDirectorTitle = createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE,
				ServiceConstants.UNIT_REGION_001, CodesConstant.CBRDTTLE);
		bookmarkNonGroupList.add(bookmarkHeaderDirectorTitle);
		BookmarkDto bookmarkHeaderDirectorName = createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME,
				ServiceConstants.UNIT_REGION_001, CodesConstant.CBRDNAME);
		bookmarkNonGroupList.add(bookmarkHeaderDirectorName);

		// Addressee info
		BookmarkDto bookmarkAddresseeNmFirst = createBookmark(BookmarkConstants.ADDRESSEE_NAME_FIRST,
				prefillDto.getPersonDto().getNmPersonFirst());
		bookmarkNonGroupList.add(bookmarkAddresseeNmFirst);
		BookmarkDto bookmarkAddresseeNmLast = createBookmark(BookmarkConstants.ADDRESSEE_NAME_LAST,
				prefillDto.getPersonDto().getNmPersonLast());
		bookmarkNonGroupList.add(bookmarkAddresseeNmLast);
		BookmarkDto bookmarkAddresseeNmMiddle = createBookmark(BookmarkConstants.ADDRESSEE_NAME_MIDDLE,
				prefillDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonGroupList.add(bookmarkAddresseeNmMiddle);
		if (StringUtils.isNotBlank(prefillDto.getPersonDto().getCdPersonSuffix())) {
			FormDataGroupDto addresseeSuffixGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESSEE_SUFFIX,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(addresseeSuffixGroupDto);
			BookmarkDto bookmarkAddresseeSuffix = createBookmarkWithCodesTable(BookmarkConstants.ADDRESSEE_NAME_SUFFIX,
					prefillDto.getPersonDto().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
			bookmarkNonGroupList.add(bookmarkAddresseeSuffix);
		}

		// Addressee address info
		FormDataGroupDto reporterAddrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESSEE_ADDRESS,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> reporterAddrGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkReporterAddrList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_LINE_1,
				prefillDto.getAddressValueDto().getStreetLn1());
		bookmarkReporterAddrList.add(bookmarkAddrLn1);
		BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_CITY,
				prefillDto.getAddressValueDto().getCity());
		bookmarkReporterAddrList.add(bookmarkAddrCity);
		BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_STATE,
				prefillDto.getAddressValueDto().getState());
		bookmarkReporterAddrList.add(bookmarkAddrState);
		BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_ZIP,
				prefillDto.getAddressValueDto().getZip());
		bookmarkReporterAddrList.add(bookmarkAddrZip);
		if (StringUtils.isNotBlank(prefillDto.getAddressValueDto().getStreetLn2())) {
			FormDataGroupDto addrLn2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESSEE_LINE_2,
					FormGroupsConstants.TMPLAT_ADDRESSEE_ADDRESS);
			List<BookmarkDto> bookmarkAddrLn2List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDRESSEE_ADDRESS_LINE_2,
					prefillDto.getAddressValueDto().getStreetLn2());
			bookmarkAddrLn2List.add(bookmarkAddrLn2);

			addrLn2GroupDto.setBookmarkDtoList(bookmarkAddrLn2List);
			reporterAddrGroupList.add(addrLn2GroupDto);
		}
		reporterAddrGroupDto.setBookmarkDtoList(bookmarkReporterAddrList);
		reporterAddrGroupDto.setFormDataGroupList(reporterAddrGroupList);
		formDataGroupList.add(reporterAddrGroupDto);

		// Principals info
		for (CpsInvPrincipalDto principalDto : prefillDto.getCpsInvPrincipalsList()) {
			FormDataGroupDto childNameGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_NAME,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkChildNameList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkChildNmFirst = createBookmark(BookmarkConstants.CHILD_NAME_FIRST,
					principalDto.getNmPersonFull());
			bookmarkChildNameList.add(bookmarkChildNmFirst);
			childNameGroupDto.setBookmarkDtoList(bookmarkChildNameList);
			formDataGroupList.add(childNameGroupDto);
		}

		// Date
		BookmarkDto bookmarkNotifDate = createBookmark(BookmarkConstants.NOTIFICATION_DATE,
				DateUtils.stringDt(new Date()));
		bookmarkNonGroupList.add(bookmarkNotifDate);

		// Case #
		BookmarkDto bookmarkCaseNumber = createBookmark(BookmarkConstants.CASE_NUMBER, prefillDto.getIdCase());
		bookmarkNonGroupList.add(bookmarkCaseNumber);

		// Reporter info
		BookmarkDto bookmarkReporterNmFirst = createBookmark(BookmarkConstants.REPORTER_FNAME,
				prefillDto.getPersonDto().getNmPersonFirst());
		bookmarkNonGroupList.add(bookmarkReporterNmFirst);
		BookmarkDto bookmarkReporterNmLast = createBookmark(BookmarkConstants.REPORTER_LNAME,
				prefillDto.getPersonDto().getNmPersonLast());
		bookmarkNonGroupList.add(bookmarkReporterNmLast);
		BookmarkDto bookmarkReporterNmMiddle = createBookmark(BookmarkConstants.REPORTER_MNAME,
				prefillDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonGroupList.add(bookmarkReporterNmMiddle);
		if (StringUtils.isNotBlank(prefillDto.getPersonDto().getCdPersonSuffix())) {
			FormDataGroupDto reporterSuffixGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REPORTER_SUFFIX,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkReporterSuffixList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkRepSuffix = createBookmarkWithCodesTable(BookmarkConstants.REPORTER_SUFFIX,
					prefillDto.getPersonDto().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
			bookmarkReporterSuffixList.add(bookmarkRepSuffix);
			reporterSuffixGroupDto.setBookmarkDtoList(bookmarkReporterSuffixList);
			formDataGroupList.add(reporterSuffixGroupDto);
		}

		// Closure Reason
		// 010 Close - Services Completed
		// 020 Close - No Sig Safety Factors/CPS Decision
		// 030 Close - Fam Declined Srvcs/No Safety Threat
		// 040 Close - Unable to Locate
		// 050 Close - Administrative Closure
		// 060 FPR/FBSS
		if (CodesConstant.CCLOSAR_010.equals(prefillDto.getClosureReason())) {
			FormDataGroupDto arCompletedGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AR_COMPLETED,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(arCompletedGroupDto);
		} else if (CodesConstant.CCLOSAR_020.equals(prefillDto.getClosureReason())
				|| CodesConstant.CCLOSAR_030.equals(prefillDto.getClosureReason())
				|| ServiceConstants.ADMIN_CLOSE_RSN.contains(prefillDto.getClosureReason())) {
			FormDataGroupDto arClosedGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AR_CLOSED,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(arClosedGroupDto);
		} else if (CodesConstant.CCLOSAR_040.equals(prefillDto.getClosureReason())) {
			FormDataGroupDto arUnableLocateGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_AR_UNABLE_TO_LOCATE, FormConstants.EMPTY_STRING);
			formDataGroupList.add(arUnableLocateGroupDto);
		} else if (CodesConstant.CCLOSAR_060.equals(prefillDto.getClosureReason())) {
			FormDataGroupDto arFbssGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AR_FBSS,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(arFbssGroupDto);
		}

		// Worker info
		BookmarkDto bookmarkWorkerNmFirst = createBookmark(BookmarkConstants.WORKER_NAME_FIRST,
				prefillDto.getWorkerDto().getNmPersonFirst());
		bookmarkNonGroupList.add(bookmarkWorkerNmFirst);
		BookmarkDto bookmarkWorkerNmLast = createBookmark(BookmarkConstants.WORKER_NAME_LAST,
				prefillDto.getWorkerDto().getNmPersonLast());
		bookmarkNonGroupList.add(bookmarkWorkerNmLast);
		BookmarkDto bookmarkWorkerNmMiddle = createBookmark(BookmarkConstants.WORKER_NAME_MIDDLE,
				prefillDto.getWorkerDto().getNmPersonMiddle());
		bookmarkNonGroupList.add(bookmarkWorkerNmMiddle);
		if (StringUtils.isNotBlank(prefillDto.getWorkerDto().getCdPersonSuffix())) {
			FormDataGroupDto workerCommaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_WORKER,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkWorkerCommaList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkWorkerSuffix = createBookmarkWithCodesTable(BookmarkConstants.WORKER_NAME_SUFFIX,
					prefillDto.getWorkerDto().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
			bookmarkWorkerCommaList.add(bookmarkWorkerSuffix);
			workerCommaGroupDto.setBookmarkDtoList(bookmarkWorkerCommaList);
			formDataGroupList.add(workerCommaGroupDto);
		}

		// Worker title
		if (StringUtils.isNotBlank(prefillDto.getEmployeeClass())) {
			FormDataGroupDto titleGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_TITLE,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkTitleList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkWorkerTitle = createBookmark(BookmarkConstants.WORKER_TITLE,
					prefillDto.getEmployeeClass());
			bookmarkTitleList.add(bookmarkWorkerTitle);
			titleGroupDto.setBookmarkDtoList(bookmarkTitleList);
			formDataGroupList.add(titleGroupDto);
		}

		// Worker address info
		FormDataGroupDto workAddrGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKADDR,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> workAddrGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkWorkAddrList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkWorkAddrLn1 = createBookmark(BookmarkConstants.WORKER_ADDRESS_LINE1,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
		bookmarkWorkAddrList.add(bookmarkWorkAddrLn1);
		BookmarkDto bookmarkWorkAddrCity = createBookmark(BookmarkConstants.WORKER_ADDRESS_CITY,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
		bookmarkWorkAddrList.add(bookmarkWorkAddrCity);
		BookmarkDto bookmarkWorkAddrZip = createBookmark(BookmarkConstants.WORKER_ADDRESS_ZIP,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
		bookmarkWorkAddrList.add(bookmarkWorkAddrZip);
		workAddrGroupDto.setBookmarkDtoList(bookmarkWorkAddrList);
		if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())) {
			FormDataGroupDto workAddrLn2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKADDR_LINE2,
					FormGroupsConstants.TMPLAT_WORKADDR);
			List<BookmarkDto> bookmarkWorkAddrLn2List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkWorkAddrLn2 = createBookmark(BookmarkConstants.WORKER_ADDRESS_LINE2,
					prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
			bookmarkWorkAddrLn2List.add(bookmarkWorkAddrLn2);
			workAddrLn2GroupDto.setBookmarkDtoList(bookmarkWorkAddrLn2List);
			workAddrGroupList.add(workAddrLn2GroupDto);
		}

		workAddrGroupDto.setFormDataGroupList(workAddrGroupList);
		formDataGroupList.add(workAddrGroupDto);

		// Worker phone
		BookmarkDto bookmarkWorkerPhone = createBookmark(BookmarkConstants.WORKER_PHONE,
				TypeConvUtil.formatPhone(prefillDto.getEmployeePersonDto().getNbrPersonPhone()));
		bookmarkNonGroupList.add(bookmarkWorkerPhone);

		// Worker email
		if (StringUtils.isNotBlank(prefillDto.getEmployeePersonDto().getTxtEmployeeEmailAddress())) {
			FormDataGroupDto workerEmailGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_EMAIL,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkWorkerEmailList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkWorkerEmail = createBookmark(BookmarkConstants.WORKER_EMAIL,
					prefillDto.getEmployeePersonDto().getTxtEmployeeEmailAddress());
			bookmarkWorkerEmailList.add(bookmarkWorkerEmail);
			workerEmailGroupDto.setBookmarkDtoList(bookmarkWorkerEmailList);
			formDataGroupList.add(workerEmailGroupDto);
		}

		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		prefillData.setBookmarkDtoList(bookmarkNonGroupList);
		prefillData.setFormDataGroupList(formDataGroupList);

		return prefillData;
	}

}
