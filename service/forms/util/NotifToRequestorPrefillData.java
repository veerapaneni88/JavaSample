package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
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
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.NotifToRequestorDto;
import us.tx.state.dfps.service.workload.dto.PrimaryWorkerDto;
import us.tx.state.dfps.service.workload.dto.StageReviewDto;


/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Prefill
 * data for CCF11O00 Mar 7, 2018- 3:59:09 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Component
public class NotifToRequestorPrefillData extends DocumentServiceUtil {

	/**
	 * Method Name: returnPrefillData Method Description: Creates bookmarks and
	 * groups for form
	 * 
	 * @param parentDtoobj
	 * @return PreFillDataServiceDto
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		NotifToRequestorDto prefillDto = (NotifToRequestorDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		// orphan bookmarks
		List<BookmarkDto> bookmarkNonFormGroupList = new ArrayList<BookmarkDto>();

		if (null == prefillDto.getAdminReviewDto()) {
			prefillDto.setAdminReviewDto(new AdminReviewDto());
		}
		if (null == prefillDto.getEmployeePersPhNameDto()) {
			prefillDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (null == prefillDto.getNameDetailDto()) {
			prefillDto.setNameDetailDto(new NameDetailDto());
		}
		if (null == prefillDto.getPersonAddressDto()) {
			prefillDto.setPersonAddressDto(new PersonAddressDto());
		}
		if (null == prefillDto.getPrimaryWorkerDto()) {
			prefillDto.setPrimaryWorkerDto(new PrimaryWorkerDto());
		}
		if (null == prefillDto.getStageReviewNoDto()) {
			prefillDto.setStageReviewNoDto(new ArrayList<StageReviewDto>());
		}
		if (null == prefillDto.getStageReviewYesDto()) {
			prefillDto.setStageReviewYesDto(new ArrayList<StageReviewDto>());
		}
		if (null == prefillDto.getStageRtrvOutDto()) {
			prefillDto.setStageRtrvOutDto(new StageRtrvOutDto());
		}

//		// CLSC03D
		bookmarkNonFormGroupList.addAll(Arrays.asList(
				createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE, CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE),
				createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME, CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME)));

		// DAM CSEC35D
		BookmarkDto bookmarkReq1NmSuffix = createBookmarkWithCodesTable(BookmarkConstants.REQUESTER1_NAME_SUFFIX,
				prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
		bookmarkNonFormGroupList.add(bookmarkReq1NmSuffix);
		BookmarkDto bookmarkReq1NmFirst = createBookmark(BookmarkConstants.REQUESTER1_NAME_FIRST,
				prefillDto.getNameDetailDto().getNmNameFirst());
		bookmarkNonFormGroupList.add(bookmarkReq1NmFirst);
		BookmarkDto bookmarkReq1NmLast = createBookmark(BookmarkConstants.REQUESTER1_NAME_LAST,
				prefillDto.getNameDetailDto().getNmNameLast());
		bookmarkNonFormGroupList.add(bookmarkReq1NmLast);
		BookmarkDto bookmarkReq1NmMiddle = createBookmark(BookmarkConstants.REQUESTER1_NAME_MIDDLE,
				prefillDto.getNameDetailDto().getNmNameMiddle());
		bookmarkNonFormGroupList.add(bookmarkReq1NmMiddle);
		BookmarkDto bookmarkReq2NmSuffix = createBookmarkWithCodesTable(BookmarkConstants.REQUESTER2_NAME_SUFFIX,
				prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
		bookmarkNonFormGroupList.add(bookmarkReq2NmSuffix);
		BookmarkDto bookmarkReq2NmFirst = createBookmark(BookmarkConstants.REQUESTER2_NAME_FIRST,
				prefillDto.getNameDetailDto().getNmNameFirst());
		bookmarkNonFormGroupList.add(bookmarkReq2NmFirst);
		BookmarkDto bookmarkReq2NmLast = createBookmark(BookmarkConstants.REQUESTER2_NAME_LAST,
				prefillDto.getNameDetailDto().getNmNameLast());
		bookmarkNonFormGroupList.add(bookmarkReq2NmLast);
		BookmarkDto bookmarkReq2NmMiddle = createBookmark(BookmarkConstants.REQUESTER2_NAME_MIDDLE,
				prefillDto.getNameDetailDto().getNmNameMiddle());
		bookmarkNonFormGroupList.add(bookmarkReq2NmMiddle);


		// DAM CSEC34D
		BookmarkDto bookmarkReq1AddrZip = createBookmark(BookmarkConstants.REQUESTER1_ADDR_ZIP,
				prefillDto.getPersonAddressDto().getAddrPersonAddrZip());
		bookmarkNonFormGroupList.add(bookmarkReq1AddrZip);
		BookmarkDto bookmarkReq1AddrCity = createBookmark(BookmarkConstants.REQUESTER1_ADDR_CITY,
				prefillDto.getPersonAddressDto().getAddrPersonAddrCity());
		bookmarkNonFormGroupList.add(bookmarkReq1AddrCity);
		BookmarkDto bookmarkReq1AddrSt1 = createBookmark(BookmarkConstants.REQUESTER1_ADDR_ST_1,
				prefillDto.getPersonAddressDto().getAddrPersAddrStLn1());
		bookmarkNonFormGroupList.add(bookmarkReq1AddrSt1);
		BookmarkDto bookmarkReq1AddrSt2 = createBookmark(BookmarkConstants.REQUESTER1_ADDR_ST_2,
				!ObjectUtils.isEmpty(prefillDto.getPersonAddressDto().getAddrPersAddrStLn2())
				? prefillDto.getPersonAddressDto().getAddrPersAddrStLn2()
						: ServiceConstants.EMPTY_STRING);
		bookmarkNonFormGroupList.add(bookmarkReq1AddrSt2);
		BookmarkDto bookmarkReq1AddrState = createBookmark(BookmarkConstants.REQUESTER1_ADDR_STATE,
				prefillDto.getPersonAddressDto().getCdPersonAddrState());
		bookmarkNonFormGroupList.add(bookmarkReq1AddrState);

		// group ccf11o03
		if (0 == CodesConstant.CARVWRES_010.compareTo(prefillDto.getAdminReviewDto().getCdAdminRvAppealResult())) {
			FormDataGroupDto allegUpheldGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATIONS_UPHELD,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(allegUpheldGroupDto);
		}

		// group ccf11o05
		else {
			FormDataGroupDto allegReversedGroupDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_ALLEGATIONS_REVERSED, FormConstants.EMPTY_STRING);
			formDataGroupList.add(allegReversedGroupDto);
		}

		// group ccf11o02
		FormDataGroupDto futureContactGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FUTURE_CONTACT,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(futureContactGroupDto);

		// group ccf11o04
		//We are avoiding to create new bookmarkDto objects and instead keeping as fixed-size list backed up by an array.
		// Code enhancement for memory optimization.
		for (StageReviewDto stageReviewDto : prefillDto.getStageReviewYesDto()) {
			FormDataGroupDto priorAllegGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_ALLEGATIONS,
					FormConstants.EMPTY_STRING);
			priorAllegGroupDto.setBookmarkDtoList(Arrays.asList(
					createBookmarkWithCodesTable(BookmarkConstants.PRIOR_DISPOSTION,
							stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN)
					,createBookmarkWithCodesTable(BookmarkConstants.PRIOR_ALLEGATIONS,
							stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP)
					,createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX,
							stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX)
					,createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
							stageReviewDto.getNmNameFirst())
					,createBookmark(BookmarkConstants.VICTIM_NAME_LAST,
							stageReviewDto.getNmNameLast())
					,createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
							stageReviewDto.getNmNameMiddle())
			));
			formDataGroupList.add(priorAllegGroupDto);

		}

		// group ccf11o06
		for (StageReviewDto stageReviewDto : prefillDto.getStageReviewNoDto()) {
			FormDataGroupDto priorAllegGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CURRENT_ALLEGATIONS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkPriorAllegList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkPriorDisp = createBookmarkWithCodesTable(BookmarkConstants.PRIOR_DISPOSITION,
					stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN);
			BookmarkDto bookmarkPriorAlleg = createBookmarkWithCodesTable(BookmarkConstants.PRIOR_ALLEGATION,
					stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP);
			BookmarkDto bookmarkNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX,
					stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX);
			BookmarkDto bookmarkNameFirst = createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
					stageReviewDto.getNmNameFirst());
			BookmarkDto bookmarkNameLast = createBookmark(BookmarkConstants.VICTIM_NAME_LAST,
					stageReviewDto.getNmNameLast());
			BookmarkDto bookmarkNameMiddle = createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
					stageReviewDto.getNmNameMiddle());
			bookmarkPriorAllegList.add(bookmarkNameMiddle);
			bookmarkPriorAllegList.add(bookmarkNameLast);
			bookmarkPriorAllegList.add(bookmarkNameFirst);
			bookmarkPriorAllegList.add(bookmarkPriorAlleg);
			bookmarkPriorAllegList.add(bookmarkPriorDisp);
			bookmarkPriorAllegList.add(bookmarkNameSuffix);
			priorAllegGroupDto.setBookmarkDtoList(bookmarkPriorAllegList);
			formDataGroupList.add(priorAllegGroupDto);
		}

		// DAM CSEC01D
		BookmarkDto bookmarkNotificationDate = createBookmark(BookmarkConstants.NOTIFICATION_DATE,
				DateUtils.stringDt(prefillDto.getEmployeePersPhNameDto().getDtEmpTermination()));
		bookmarkNonFormGroupList.add(bookmarkNotificationDate);
		BookmarkDto bookmarkUser2AddrCity = createBookmark(BookmarkConstants.USER2_ADDR_CITY,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
		bookmarkNonFormGroupList.add(bookmarkUser2AddrCity);
		BookmarkDto bookmarkUser2Addr1 = createBookmark(BookmarkConstants.USER2_ADDR1,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
		bookmarkNonFormGroupList.add(bookmarkUser2Addr1);
		BookmarkDto bookmarkUser2AddrSt2 = createBookmark(BookmarkConstants.USER2_ADDR_ST_2,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
		bookmarkNonFormGroupList.add(bookmarkUser2AddrSt2);
		BookmarkDto bookmarkUser2AddrZip = createBookmark(BookmarkConstants.USER2_ADDR_ZIP,
				prefillDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
		bookmarkNonFormGroupList.add(bookmarkUser2AddrZip);
		BookmarkDto bookmarkUserNmSuffix = createBookmarkWithCodesTable(BookmarkConstants.USER_NAME_SUFFIX,
				prefillDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
		bookmarkNonFormGroupList.add(bookmarkUserNmSuffix);
		BookmarkDto bookmarkUser2Phone = createBookmark(BookmarkConstants.USER2_PHONE,
				TypeConvUtil.formatPhone(prefillDto.getEmployeePersPhNameDto().getNbrMailCodePhone()));
		bookmarkNonFormGroupList.add(bookmarkUser2Phone);
		BookmarkDto bookmarkUserNmFirst = createBookmark(BookmarkConstants.USER_NAME_FIRST,
				prefillDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonFormGroupList.add(bookmarkUserNmFirst);
		BookmarkDto bookmarkUserNmLast = createBookmark(BookmarkConstants.USER_NAME_LAST,
				prefillDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonFormGroupList.add(bookmarkUserNmLast);
		BookmarkDto bookmarkUserNmMiddle = createBookmark(BookmarkConstants.USER_NAME_MIDDLE,
				prefillDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonFormGroupList.add(bookmarkUserNmMiddle);
		BookmarkDto bookmarkUser2NmFirst = createBookmark(BookmarkConstants.USER2_NAME_FIRST,
				prefillDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonFormGroupList.add(bookmarkUser2NmFirst);
		BookmarkDto bookmarkUser2NmLast = createBookmark(BookmarkConstants.USER2_NAME_LAST,
				prefillDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonFormGroupList.add(bookmarkUser2NmLast);
		BookmarkDto bookmarkUser2NmMiddle = createBookmark(BookmarkConstants.USER2_NAME_MIDDLE,
				prefillDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonFormGroupList.add(bookmarkUser2NmMiddle);
		BookmarkDto bookmarkUserTitle = createBookmark(BookmarkConstants.USER_TITLE,
				prefillDto.getEmployeePersPhNameDto().getTxtEmployeeClass());
		bookmarkNonFormGroupList.add(bookmarkUserTitle);

		// group ccf11o07
		if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			FormDataGroupDto user2NameGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_USER2_NAME_CD,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkUser2NameList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkUser2Name = createBookmark(BookmarkConstants.USER2_NAME_CD_SUFFIX,
					prefillDto.getEmployeePersPhNameDto().getCdNameSuffix());
			bookmarkUser2NameList.add(bookmarkUser2Name);
			user2NameGroupDto.setBookmarkDtoList(bookmarkUser2NameList);
			formDataGroupList.add(user2NameGroupDto);
		}

		// group ccf11o08
		if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())) {
			FormDataGroupDto user2Addr2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_USER2_ADDR2,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkUser2Addr2List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkUser2Addr2 = createBookmark(BookmarkConstants.USER2_ADDR2_TXT,
					prefillDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
			bookmarkUser2Addr2List.add(bookmarkUser2Addr2);
			user2Addr2GroupDto.setBookmarkDtoList(bookmarkUser2Addr2List);
			formDataGroupList.add(user2Addr2GroupDto);
		}

		// group ccf11o09
		if (StringUtils.isNotBlank(prefillDto.getEmployeePersPhNameDto().getNbrMailCodePhoneExt())) {
			FormDataGroupDto user2ExtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_USER2_PHONE_EXT,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkUser2ExtList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkUser2Ext = createBookmark(BookmarkConstants.USER2_PHONE_EXT_EXTENSION,
					prefillDto.getEmployeePersPhNameDto().getNbrMailCodePhoneExt());
			bookmarkUser2ExtList.add(bookmarkUser2Ext);
			user2ExtGroupDto.setBookmarkDtoList(bookmarkUser2ExtList);
			formDataGroupList.add(user2ExtGroupDto);
		}

		// group ccf13o00
		if (StringUtils.isNotBlank(prefillDto.getAdministrativeReviewInvestigativeFindingsDto().getNbrMailCodePhone())) {
			FormDataGroupDto user2ExtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_USER2_PHONE_EXT,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkUser2ExtList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkUser2Ext = createBookmark(BookmarkConstants.USER2_PHONE_EXT_EXTENSION,
					prefillDto.getAdministrativeReviewInvestigativeFindingsDto().getNbrMailCodePhone());
			bookmarkUser2ExtList.add(bookmarkUser2Ext);
			user2ExtGroupDto.setBookmarkDtoList(bookmarkUser2ExtList);
			formDataGroupList.add(user2ExtGroupDto);
		}

		// group ccf11o01
		FormDataGroupDto furtherInfoGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FURTHER_INFORMATION,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(furtherInfoGroupDto);


		// DAM CINT21D
		BookmarkDto bookmarkCaseId = createBookmark(BookmarkConstants.CASE_ID,
				prefillDto.getStageRtrvOutDto().getIdCase());
		bookmarkNonFormGroupList.add(bookmarkCaseId);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGroupList);
		return preFillData;
	}

}
