package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageOutDto;
import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.notification.dto.NotifToReporterDto;
import us.tx.state.dfps.service.person.dto.PersonListDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:NotifToReporterPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for Form cfin0900 Apr 9, 2018- 10:53:14 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class NotifToReporterPrefillData extends DocumentServiceUtil {

	public NotifToReporterPrefillData() {
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

		NotifToReporterDto notifToReporter = (NotifToReporterDto) parentDtoobj;

		if (ObjectUtils.isEmpty(notifToReporter.getIncomingDetailStageOutDtoList())) {
			notifToReporter.setIncomingDetailStageOutDtoList(new ArrayList<IncomingDetailStageOutDto>());
		}
		if (ObjectUtils.isEmpty(notifToReporter.getPersonDtoList())) {
			notifToReporter.setPersonDtoList(new ArrayList<PersonListDto>());
		}
		if (ObjectUtils.isEmpty(notifToReporter.getNamePrimaryEndDateOutDto())) {
			notifToReporter.setNamePrimaryEndDateOutDto(new ArrayList<NamePrimayEndDateOutDto>());
		}
		if (ObjectUtils.isEmpty(notifToReporter.getStageRecDto())) {
			notifToReporter.setStageRecDto(new ArrayList<StagePersonLinkRecordOutDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		// parent group cfin0901
		if (ServiceConstants.SSCC_STATUS_10.equals(notifToReporter.getRtrvStageOutDto().getCdStageReasonClosed())) {
			FormDataGroupDto stageGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_LET1,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkStageList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkIdStage = createBookmark(BookmarkConstants.ID_STAGE,
					notifToReporter.getRtrvStageOutDto().getIdStage());
			bookmarkStageList.add(bookmarkIdStage);
			stageGroupDto.setBookmarkDtoList(bookmarkStageList);
			formDataGroupList.add(stageGroupDto);
		}

		// parent group cfin0902
		if (ServiceConstants.LET_REP2.equals(notifToReporter.getRtrvStageOutDto().getCdStageReasonClosed())) {
			FormDataGroupDto stageGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_LET2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(stageGroupDto);
		}

		// parent group cfin0903
		if (ServiceConstants.STATE_MED_ASST_ONLY
				.equals(notifToReporter.getRtrvStageOutDto().getCdStageReasonClosed())) {
			FormDataGroupDto stageGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_LET3,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(stageGroupDto);
		}

		// parent group cfin0905 CINV34D
		for (PersonListDto personListDto : notifToReporter.getPersonDtoList()) {
			if (ServiceConstants.ALLEG_VICTIM.equals(personListDto.getStagePersRole())) {
				FormDataGroupDto stageGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_NAME,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkStageList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkFirst = createBookmark(BookmarkConstants.REP_CHILD_FIRST_NAME,
						personListDto.getNmPersonFirst());
				bookmarkStageList.add(bookmarkFirst);
				BookmarkDto bookmarkLast = createBookmark(BookmarkConstants.REP_CHILD_LAST_NAME,
						personListDto.getNmpersonLast());
				bookmarkStageList.add(bookmarkLast);

				stageGroupDto.setBookmarkDtoList(bookmarkStageList);

				formDataGroupList.add(stageGroupDto);
			}
		}

		// parent group cfzz0501 CLSC03D
		if (ServiceConstants.CMPSSTAT_001.compareTo(notifToReporter.getCodesTablesDto().getaCode()) < 0) {
			FormDataGroupDto stageGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_BOARD,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkStageList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDecode = createBookmark(BookmarkConstants.HEADER_BOARD_CITY,
					notifToReporter.getCodesTablesDto().getaDecode());
			bookmarkStageList.add(bookmarkDecode);
			BookmarkDto bookmarkDecode2 = createBookmark(BookmarkConstants.HEADER_BOARD_NAME,
					notifToReporter.getCodesTablesDto().getbDecode());
			bookmarkStageList.add(bookmarkDecode2);

			stageGroupDto.setBookmarkDtoList(bookmarkStageList);

			formDataGroupList.add(stageGroupDto);
		}

		// parent group cfzz0601 CLSC03D
		if (ServiceConstants.CMPSSTAT_001.equals(notifToReporter.getCodesTablesDto().getaCode())) {
			FormDataGroupDto stageGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_DIRECTOR,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkStageList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDecode = createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
					notifToReporter.getCodesTablesDto().getaDecode());
			bookmarkStageList.add(bookmarkDecode);
			BookmarkDto bookmarkDecode2 = createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME,
					notifToReporter.getCodesTablesDto().getbDecode());
			bookmarkStageList.add(bookmarkDecode2);

			stageGroupDto.setBookmarkDtoList(bookmarkStageList);

			formDataGroupList.add(stageGroupDto);
		}

		// CINT07D
		for (IncomingDetailStageOutDto incomingDetailStageOutDto : notifToReporter.getIncomingDetailStageOutDtoList()) {
			BookmarkDto bookmarkDtIncomingCall = createBookmark(BookmarkConstants.REP_CALL_DATE,
					DateUtils.stringDt(incomingDetailStageOutDto.getDtIncomingCall()));
			bookmarkNonFormGrpList.add(bookmarkDtIncomingCall);
			BookmarkDto bookmarkNmJurisdiction = createBookmark(BookmarkConstants.AGENCY_NAME,
					incomingDetailStageOutDto.getNmJurisdiction());
			bookmarkNonFormGrpList.add(bookmarkNmJurisdiction);
			BookmarkDto bookmarkIdStage = createBookmark(BookmarkConstants.REP_INTAKE_NUM,
					incomingDetailStageOutDto.getIdStage());
			bookmarkNonFormGrpList.add(bookmarkIdStage);
		}

		// CSEC18D
		CaseInfoDto caseInfoDto = notifToReporter.getCaseInfoDto();

		if(!ObjectUtils.isEmpty(notifToReporter.getCaseInfoDto()))
		{
		if(!ObjectUtils.isEmpty(caseInfoDto.getAddrPersonAddrZip()))
		{
		BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.REP_ADDRESS_ZIP,
				caseInfoDto.getAddrPersonAddrZip());
		bookmarkNonFormGrpList.add(bookmarkAddrZip);
		}
		if(!ObjectUtils.isEmpty(caseInfoDto.getAddrPersonAddrCity()))
		{
		BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.REP_ADDRESS_CITY,
				caseInfoDto.getAddrPersonAddrCity());
		bookmarkNonFormGrpList.add(bookmarkAddrCity);
		}
		if(!ObjectUtils.isEmpty(caseInfoDto.getAddrPersAddrStLn1()))
		{
		BookmarkDto bookmarkStLn1 = createBookmark(BookmarkConstants.REP_ADDRESS_LINE_1,
				caseInfoDto.getAddrPersAddrStLn1());
		bookmarkNonFormGrpList.add(bookmarkStLn1);
		}
		if(!ObjectUtils.isEmpty(caseInfoDto.getAddrPersAddrStLn2()))
		{
		BookmarkDto bookmarkStLn2 = createBookmark(BookmarkConstants.REP_ADDRESS_LINE_2,
				caseInfoDto.getAddrPersAddrStLn2());
		bookmarkNonFormGrpList.add(bookmarkStLn2);
		}
		if(!ObjectUtils.isEmpty(caseInfoDto.getCdPersonAddrState()))
		{
		BookmarkDto bookmarkState = createBookmark(BookmarkConstants.REP_ADDRESS_STATE,
				caseInfoDto.getCdPersonAddrState());
		bookmarkNonFormGrpList.add(bookmarkState);
		}
		if(!ObjectUtils.isEmpty(caseInfoDto.getNmNameFirst()))
		{
		BookmarkDto bookmarkNmNameFirst = createBookmark(BookmarkConstants.REP_FIRST_NAME,
				caseInfoDto.getNmNameFirst());
		bookmarkNonFormGrpList.add(bookmarkNmNameFirst);
		}
		if(!ObjectUtils.isEmpty(caseInfoDto.getNmNameFirst()))
		{
		BookmarkDto bookmarkNmNameFirst1 = createBookmark(BookmarkConstants.REP_ADDRESS_FIRST_NAME,
				caseInfoDto.getNmNameFirst());
		bookmarkNonFormGrpList.add(bookmarkNmNameFirst1);
		}
		if(!ObjectUtils.isEmpty(caseInfoDto.getNmNameLast()))
		{
		BookmarkDto bookmarkNmNameLast = createBookmark(BookmarkConstants.REP_ADDRESS_LAST_NAME,
				caseInfoDto.getNmNameLast());
		bookmarkNonFormGrpList.add(bookmarkNmNameLast);
		}
		if(!ObjectUtils.isEmpty(caseInfoDto.getNmNameLast()))
		{
		BookmarkDto bookmarkNmNameLast1 = createBookmark(BookmarkConstants.REP_LAST_NAME, caseInfoDto.getNmNameLast());
		bookmarkNonFormGrpList.add(bookmarkNmNameLast1);
		}
		}

		// CSEC02D
		if(!ObjectUtils.isEmpty(notifToReporter.getGenericCaseInfoDto().getNmCase()))
		{
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.REP_CASE_NAME,
				notifToReporter.getGenericCaseInfoDto().getNmCase());
		bookmarkNonFormGrpList.add(bookmarkNmCase);
		}
		if(!ObjectUtils.isEmpty(notifToReporter.getGenericCaseInfoDto().getDtSysDtGenericSysdate()))
		{
		BookmarkDto bookmarkDtSysdate = createBookmark(BookmarkConstants.REP_CURRENT_DATE,
				DateUtils.stringDt(notifToReporter.getGenericCaseInfoDto().getDtSysDtGenericSysdate()));
		bookmarkNonFormGrpList.add(bookmarkDtSysdate);
		}

		// CSEC01D
		if (!ObjectUtils.isEmpty(notifToReporter.getEmployeePersPhNameDto())) {
			if (!ObjectUtils.isEmpty(notifToReporter.getEmployeePersPhNameDto().getNmNameFirst())) {
				BookmarkDto bookmarkRepWorkerNameFirst = createBookmark(BookmarkConstants.REP_WORKER_FIRST_NAME,
						notifToReporter.getEmployeePersPhNameDto().getNmNameFirst());
				bookmarkNonFormGrpList.add(bookmarkRepWorkerNameFirst);
			}
			if (!ObjectUtils.isEmpty(notifToReporter.getEmployeePersPhNameDto().getNmNameLast())) {
				BookmarkDto bookmarkRepWorkerNameLast = createBookmark(BookmarkConstants.REP_WORKER_LAST_NAME,
						notifToReporter.getEmployeePersPhNameDto().getNmNameLast());
				bookmarkNonFormGrpList.add(bookmarkRepWorkerNameLast);
			}
			if (!ObjectUtils.isEmpty(notifToReporter.getEmployeePersPhNameDto().getTxtEmployeeClass())) {
				BookmarkDto bookmarkEmployeeClass = createBookmark(BookmarkConstants.REP_WORKER_JOB_CLASS,
						notifToReporter.getEmployeePersPhNameDto().getTxtEmployeeClass());
				bookmarkNonFormGrpList.add(bookmarkEmployeeClass);
			}
		}

		// CINTA1D

		if (!ObjectUtils.isEmpty(notifToReporter.getAddressMatchId())) {
			BookmarkDto bookmarkMatchId = createBookmark(BookmarkConstants.POP_UP, notifToReporter.getAddressMatchId());
			bookmarkNonFormGrpList.add(bookmarkMatchId);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);

		return preFillData;
	}

}
