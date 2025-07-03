package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ApprovalDto;
import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.common.dto.NameDto;
import us.tx.state.dfps.common.dto.PhoneInfoDto;
import us.tx.state.dfps.common.dto.PriorityChangeInfoDto;
import us.tx.state.dfps.common.dto.WorkerInfoDto;
import us.tx.state.dfps.service.common.*;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsintakereport.service.CpsIntakeReportService;
import us.tx.state.dfps.service.cpsintakereportform.dto.CpsIntakeReportFormDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.intake.dto.IncmgDetermFactorsDto;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.person.dto.PersonAddrLinkDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * Class Description:CpsIntakeReportFormPrefillData
 * will implement returnPrefillData operation defined in DocumentServiceUtil
 * Interface to populate the prefill data for form CFIN0100.
 * Feb 9, 2018- 2:04:05 PM
 * © 2017 Texas Department of Family and Protective Services 
 * * **********  Change History *********************************
 * 09/28/2020 thompswa artf164166 : Use incoming_detail when Reporter Rel-Int is FPS Staff
 * 12/15/2020 thompswa artf169810 : getTxtRelatedCalls for new bookmark, SUM_RELATED_REPORTS.
 * 04/06/2021 olivea3  artf176887 Priority Change Comments use txtStagePriorityCmnts : defect 16905
 * 11/22/2022 thompswa artf178638 : Intake discrepancy ModPhase2 vs Legacy add populatePersonListInfo common method
 * 10/05/2023 thompswa artf251139 : add Child Death Indicator
 * 04/09/2024 thompswa artf261922 : Reporter Email to display on the Intake Report Form
 * 08/26/2024 thompswa artf268014 : Intake Report Form Question ppm63139 BR 40.2
 */

@Component
public class CpsIntakeReportFormPrefillData extends DocumentServiceUtil {

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 *
	 * @param parentDtoobj
	 *
	 * @return PreFillData
	 */

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		CpsIntakeReportFormDto cpsIntakeReportFormDto = (CpsIntakeReportFormDto) parentDtoobj;

		if (null == cpsIntakeReportFormDto.getIncomingStageDetailsDto()) {
			cpsIntakeReportFormDto.setIncomingStageDetailsDto(new IncomingStageDetailsDto());
		}
		IncomingStageDetailsDto incomingDetail = cpsIntakeReportFormDto.getIncomingStageDetailsDto();

		if (null == cpsIntakeReportFormDto.getWorkerInfoDtoList()) {
			cpsIntakeReportFormDto.setWorkerInfoDtoList(new ArrayList<WorkerInfoDto>());
		}

		if (null == cpsIntakeReportFormDto.getApprovalDtoList()) {
			cpsIntakeReportFormDto.setApprovalDtoList(new ArrayList<ApprovalDto>());
		}

		if (null == cpsIntakeReportFormDto.getEmployeePersPhNameDto()) {
			cpsIntakeReportFormDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (null == cpsIntakeReportFormDto.getIntakeAllegationDtoList()) {
			cpsIntakeReportFormDto.setIntakeAllegationDtoList(new ArrayList<IntakeAllegationDto>());
		}
		if (null == cpsIntakeReportFormDto.getPersonAddrLinkDtoList()) {
			cpsIntakeReportFormDto.setPersonAddrLinkDtoList(new ArrayList<PersonAddrLinkDto>());
		}
		if (null == cpsIntakeReportFormDto.getPersonPhoneDtoList()) {
			cpsIntakeReportFormDto.setPersonPhoneDtoList(new ArrayList<PhoneInfoDto>());
		}
		if (null == cpsIntakeReportFormDto.getVictims()) {
			cpsIntakeReportFormDto.setVictims(new ArrayList<PersonDto>());
		}
		if (null == cpsIntakeReportFormDto.getNameAliasList()) {
			cpsIntakeReportFormDto.setNameAliasList(new ArrayList<NameDto>());
		}
		if (null == cpsIntakeReportFormDto.getShowScreener()) {
			cpsIntakeReportFormDto.setShowScreener(ServiceConstants.FALSE);
		}


		/**
		 * Description: Populating the non form group data into prefill data
		 * GroupName: None 
		 * BookMark: 
		 * Condition: None 
		 */
		List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<BookmarkDto>();

		bookmarkDtoDefaultDtoList.addAll(Arrays.asList(
				createBookmark(BookmarkConstants.TITLE_CASE_NAME, incomingDetail.getNmStage())
				, createBookmark(BookmarkConstants.TITLE_CASE_NUMBER, incomingDetail.getIdCase())
				, createBookmark(BookmarkConstants.SUM_INTAKE_NUM, incomingDetail.getIdStage())
				, createBookmark(BookmarkConstants.SUM_DATE_RPTED, !ObjectUtils.isEmpty(incomingDetail.getDtIncomingCall())
						? DateUtils.stringDt(DateUtils.getDateWithoutTime(incomingDetail.getDtIncomingCall())) : ServiceConstants.EMPTY_STR)
				, createBookmark(BookmarkConstants.SUM_PRIM_ALLEG, incomingDetail.getCdIncmgAllegType())
				, createBookmark(BookmarkConstants.SUM_TIME_RPTED, !ObjectUtils.isEmpty(incomingDetail.getDtIncomingCall())
						? DateUtils.getTime(incomingDetail.getDtIncomingCall()) : ServiceConstants.EMPTY_STR)
				, createBookmark(BookmarkConstants.SUM_RELATED_REPORTS, incomingDetail.getTxtRelatedCalls()) // artf169810
				, createBookmark(BookmarkConstants.SUM_WORKER_SAFETY_ISSUES, incomingDetail.getIndIncmgWorkerSafety())
				, createBookmark(BookmarkConstants.SUM_LE_NOTIFY_DATE, !ObjectUtils.isEmpty(incomingDetail.getDtEventOccurred())
						? DateUtils.stringDt(incomingDetail.getDtEventOccurred()) : ServiceConstants.EMPTY_STR)
				, createBookmark(BookmarkConstants.SUM_SENSITIVE_ISSUE, incomingDetail.getIndIncmgSensitive())
				, createBookmark(BookmarkConstants.SUM_LE_JURIS, incomingDetail.getNmIncmgJurisdiction())
				, createBookmark(BookmarkConstants.SUM_SUSP_METH_LAB, incomingDetail.getIndIncmgSuspMeth())
				, createBookmark(BookmarkConstants.SUM_CHILDDEATH, incomingDetail.getIndChildDeath()) // artf251139
				, createBookmark(BookmarkConstants.SUM_SPCL_HANDLING, incomingDetail.getCdIncmgSpecHandling())
				, createBookmark(BookmarkConstants.SUM_PRIORITY_DETERM, incomingDetail.getCdStageCurrPriority())
				, createBookmark(BookmarkConstants.SUM_RSN_FOR_CLOSR, incomingDetail.getCdStageReasonClosed())
				, createBookmark(BookmarkConstants.SUM_WORKER_TAKING_INTAKE, incomingDetail.getNmIncmgWorkerName())
				, createBookmark(BookmarkConstants.SUM_WORKER_CITY, incomingDetail.getAddrIncmgWorkerCity())
				, createBookmark(BookmarkConstants.SUM_WORKER_PHONE, !ObjectUtils.isEmpty(incomingDetail.getNbrIncmgWorkerPhone())
						? TypeConvUtil.formatPhone((incomingDetail.getNbrIncmgWorkerPhone())) : ServiceConstants.EMPTY_STR)
				, createBookmark(BookmarkConstants.SUM_WORKER_EXTENSION, incomingDetail.getNbrIncmgWorkerExt())
				, createBookmark(BookmarkConstants.CALL_NARR_WORKER_SAFETY, incomingDetail.getTxtIncmgWorkerSafety())
				, createBookmark(BookmarkConstants.CALL_NARR_SENSITIVE_ISSUE, incomingDetail.getTxtIncmgSensitive())
				, createBookmark(BookmarkConstants.CALL_NARR_SUSP_METH_LAB, incomingDetail.getTxtIncmgSuspMeth())
				, createBookmark(BookmarkConstants.HIS_RECORDED_CALL_DATE, !ObjectUtils.isEmpty(incomingDetail.getDtIncomingCall())
						? DateUtils.stringDt(incomingDetail.getDtIncomingCall()) : ServiceConstants.EMPTY_STR)
				, createBookmark(BookmarkConstants.HIS_RECORDED_CALL_EXT, incomingDetail.getNbrIncmgWorkerExt())
				, createBookmark(BookmarkConstants.HIS_RECORDED_CALL_PHONE, !ObjectUtils.isEmpty(incomingDetail.getNbrIncmgWorkerPhone())
						? TypeConvUtil.formatPhone((incomingDetail.getNbrIncmgWorkerPhone())) : ServiceConstants.EMPTY_STR)
				, createBookmark(BookmarkConstants.HIS_RECORDED_CALL_CITY, incomingDetail.getAddrIncmgWorkerCity())
				, createBookmark(BookmarkConstants.HIS_RECORDED_CALL_BJN, incomingDetail.getCdEmpBjnEmp())
				, createBookmark(BookmarkConstants.HIS_CURR_PRIORITY, incomingDetail.getCdStageCurrPriority())
				, createBookmark(BookmarkConstants.HIS_INITIAL_PRIORITY, incomingDetail.getCdStageInitialPriority())
				, createBookmark(BookmarkConstants.HIS_RSN_FOR_CLOSURE, incomingDetail.getCdStageReasonClosed())
				, createBookmark(BookmarkConstants.HIS_RECORDED_CALL, incomingDetail.getNmIncmgWorkerName())
				, createBookmark(BookmarkConstants.HIS_RECORDED_CALL_TIME, !ObjectUtils.isEmpty(incomingDetail.getDtIncomingCall())
						? DateUtils.getTime(incomingDetail.getDtIncomingCall()) : ServiceConstants.EMPTY_STR)
		));
		for (WorkerInfoDto workerInfo : cpsIntakeReportFormDto.getWorkerInfoDtoList()) {
			bookmarkDtoDefaultDtoList.addAll(Arrays.asList(
					createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_PHONE, !ObjectUtils.isEmpty(workerInfo.getNbrPersonPhone())
							? TypeConvUtil.formatPhone(workerInfo.getNbrPersonPhone()) : ServiceConstants.EMPTY_STR)
					, createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_EXT, workerInfo.getNbrPersonPhoneExtension())
					, createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_CITY, workerInfo.getAddrMailCodeCity())
					, createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_BJN, workerInfo.getCdJobBjn())
					, createBookmarkWithCodesTable(BookmarkConstants.HIS_STAGE_CHANGE_SUFFIX, workerInfo.getCdNameSuffix(), CodesConstant.CSUFFIX2)
					, createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_FIRST, workerInfo.getNmNameFirst())
					, createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_LAST, workerInfo.getNmNameLast())
					, createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_MIDDLE, workerInfo.getNmNameMiddle())
					, createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_DATE, !ObjectUtils.isEmpty(workerInfo.getDtEventModified())
							? DateUtils.stringDt(workerInfo.getDtEventModified()) : ServiceConstants.EMPTY_STR)
					, createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_TIME, !ObjectUtils.isEmpty(workerInfo.getDtEventModified())
							? DateUtils.getTime(workerInfo.getDtEventModified()) : ServiceConstants.EMPTY_STR)
			));
			//Avoiding the duplicate bookmark
			break;
		}
		for (ApprovalDto appr : cpsIntakeReportFormDto.getApprovalDtoList()) {
			bookmarkDtoDefaultDtoList.addAll(Arrays.asList(
					createBookmark(BookmarkConstants.HIS_APPROVED_DATE, !ObjectUtils.isEmpty(appr.getDtApproversDetermination())
							? DateUtils.stringDt(appr.getDtApproversDetermination()) : ServiceConstants.EMPTY_STR)
					, createBookmark(BookmarkConstants.HIS_APPROVED_PHONE, !ObjectUtils.isEmpty(appr.getNbrPersonPhone())
							? TypeConvUtil.formatPhone(appr.getNbrPersonPhone()) : ServiceConstants.EMPTY_STR)
					, createBookmark(BookmarkConstants.HIS_APPROVED_EXT, appr.getNbrPersonPhoneExtension())
					, createBookmark(BookmarkConstants.HIS_APPROVED_CITY, appr.getAddrMailCodeCity())
					, createBookmark(BookmarkConstants.HIS_APPROVED_BJN, appr.getCdJobBjn())
					, createBookmarkWithCodesTable(BookmarkConstants.HIS_APPROVED_SUFFIX, appr.getCdNameSuffix(), CodesConstant.CSUFFIX2)
					, createBookmark(BookmarkConstants.HIS_APPROVED_FIRST, appr.getNmNameFirst())
					, createBookmark(BookmarkConstants.HIS_APPROVED_LAST, appr.getNmNameLast())
					, createBookmark(BookmarkConstants.HIS_APPROVED_MIDDLE, appr.getNmNameMiddle())
					, createBookmark(BookmarkConstants.HIS_APPROVED_TIME, !ObjectUtils.isEmpty(appr.getDtApproversDetermination())
							? DateUtils.getTime(appr.getDtApproversDetermination()) : ServiceConstants.EMPTY_STR)
			));
			//Avoiding the duplicate bookmark
			break;
		}

		//making groups with no parent group.
		//creating groups using formDatagroup

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();


		/**
		 * Description: Populating the group cfin01ws
		 * GroupName: cfin01ws 
		 * BookMark: TMPLAT_WS
		 * Condition: bIndIncmgWorkerSafety(CINT65D) = Y  
		 */
		if (ServiceConstants.Y.equalsIgnoreCase(cpsIntakeReportFormDto.getIncomingStageDetailsDto().getIndIncmgWorkerSafety())) {
			FormDataGroupDto tmplatWsGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_WS, FormConstants.EMPTY_STRING);
			formDataGroupList.add(tmplatWsGrp);
			tmplatWsGrp.setBookmarkDtoList(Arrays.asList(
					createBookmark(BookmarkConstants.WS_TEXT, cpsIntakeReportFormDto.getIncomingStageDetailsDto().getTxtIncmgWorkerSafety())
			));
		}
		// artf251139 ind child death
		if(Arrays.asList(ServiceConstants.Y, ServiceConstants.N).contains(incomingDetail.getIndChildDeath())) {
			FormDataGroupDto indChlDthGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SUM_CHILDDEATH, FormConstants.EMPTY_STRING);
			formDataGroupList.add(indChlDthGroupDto);
		}
		/**
		 * Description: artf268014 Screener Question
		 * BookMark for group: TMPLAT_SCREENER
		 * Always show screener question if answered,
		 * else okay to not show screener if VictimAge6AndAbove
		 */
		if (ServiceConstants.TRUE.equals(cpsIntakeReportFormDto.getShowScreener())) {
			FormDataGroupDto screenGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SCREENER, ServiceConstants.EMPTY_STRING);
			formDataGroupList.add(screenGroup);
			screenGroup.setBookmarkDtoList(Arrays.asList(
					createBookmark(BookmarkConstants.SUM_OPEN_CASE_AT_INTAKE,
							!TypeConvUtil.isNullOrEmpty(incomingDetail.getIndFoundOpenCase())
							? incomingDetail.getIndFoundOpenCase()
							: incomingDetail.getIndFoundOpenCaseAtIntake())
					, createBookmark(BookmarkConstants.SUM_FORMALLY_SCREENED, incomingDetail.getIndFormallyScreened())
					, createBookmark(BookmarkConstants.SUM_SCREENER_TITLE, cpsIntakeReportFormDto.getEmployeePersPhNameDto().getCdJobClass())
					, createBookmark(BookmarkConstants.SUM_SCREENER_NAME_FIRST, cpsIntakeReportFormDto.getEmployeePersPhNameDto().getNmNameFirst())
					, createBookmark(BookmarkConstants.SUM_SCREENER_NAME_LAST, cpsIntakeReportFormDto.getEmployeePersPhNameDto().getNmNameLast())
			));
		}
		/**
		 * Description: Populating the fields inside the group cfin0106
		 * GroupName: cfin0106
		 * BookMark for group: TMPLAT_DET_FACTR
		 * BookMark for field: DETERM_FACTR
		 * Field: 	CdIncmgDeterm  
		 */
		for (IncmgDetermFactorsDto incmgFactors : cpsIntakeReportFormDto.getIncmgDetermFactors()) {
			FormDataGroupDto summarySectionFactors = createFormDataGroup(FormGroupsConstants.TMPLAT_DET_FACTR, FormConstants.EMPTY_STRING);
			formDataGroupList.add(summarySectionFactors);
			summarySectionFactors.setBookmarkDtoList(Arrays.asList(
					createBookmark(BookmarkConstants.DETERM_FACTR, incmgFactors.getCdIncmgDeterm())
			));
		}
		/**
		 * Description: Allegation for this particular intake
		 * GroupName: cfin0107
		 * BookMark: TMPLAT_ALLEGATION
		 * BookMark for field: ALLEG_DTL_ALLEG, ALLEG_DTL_AP, ALLEG_DTL_VICTIM
		 * Fields:	szCdIntakeAllegType,   	szScrAllegPerp, 	szScrPersVictim
		 */
		for (IntakeAllegationDto intkAlleg : cpsIntakeReportFormDto.getIntakeAllegationDtoList()) {
			FormDataGroupDto allegationGrpData = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION, FormConstants.EMPTY_STRING);
			formDataGroupList.add(allegationGrpData);
			allegationGrpData.setBookmarkDtoList(Arrays.asList(
					createBookmark(BookmarkConstants.ALLEG_DTL_ALLEG, intkAlleg.getCdIntakeAllegType())
					, createBookmark(BookmarkConstants.ALLEG_DTL_AP, intkAlleg.getNmPerpetrator()) //check if szScrAllegPerp stands for getPersonByIdAllegedPerpetrator
					, createBookmark(BookmarkConstants.ALLEG_DTL_VICTIM, intkAlleg.getNmVictim()) // check if szScrPersVictim holds value for getPersonByIdVictim
			));
		}
		/** Principal and Collateral Information - artf178638 refactor start */
		populatePersonListInfo(formDataGroupList, cpsIntakeReportFormDto, ServiceConstants.VICTIM_TYPE);
		populatePersonListInfo(formDataGroupList, cpsIntakeReportFormDto, ServiceConstants.PERPETRATOR_TYPE);
		populatePersonListInfo(formDataGroupList, cpsIntakeReportFormDto, ServiceConstants.OTHER_PRN_TYPE);
		populatePersonListInfo(formDataGroupList, cpsIntakeReportFormDto, ServiceConstants.REPORTER_TYPE);
		populatePersonListInfo(formDataGroupList, cpsIntakeReportFormDto, ServiceConstants.COLLATERAL_TYPE); // artf178638 refactor end

		/**
		 * Description: Priority change history
		 * GroupName: cfin0108 
		 * BookMark: TMPLAT_PRIORITY_CHANGES
		 * Condition: None
		 */
		for (PriorityChangeInfoDto priority : cpsIntakeReportFormDto.getPriorityChangeInfoDtoList()) {
			FormDataGroupDto fdPriorityChange = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIORITY_CHANGES, FormConstants.EMPTY_STRING);
			formDataGroupList.add(fdPriorityChange);
			fdPriorityChange.setBookmarkDtoList(Arrays.asList(
					createBookmark(BookmarkConstants.HIS_PRIORITY_CHANGE, priority.getCdEventStatus())
					, createBookmarkWithCodesTable(BookmarkConstants.HIS_PRIORITY_REASON, priority.getCdStageRsnPriorityChgd(), CodesConstant.CRSNPRIO)
			));
			/** artf176887 defect 16905:  for first (most-recent) Priority Change instance,
			 * use txtStagePriorityCmnts from Stage in serviceImpl was swapped in.
			 * If the Bookmark data is not null, create a FormDataGroup
			 * and add the Bookmark to it, then add the FormDataGroup to the TMPLAT_PRIORITY_CHANGES_COMMENTS FormDataGroup.
			 * This way, the Comments section for that Priority Change will only show if it is not null.
			 */
			BookmarkDto bkPriorExpl = createBookmark(BookmarkConstants.HIS_PRIORITY_EXPL, priority.getTxtStagePriorityCmnts());
			if (StringUtils.isNotEmpty(bkPriorExpl.getBookmarkData())) {
				FormDataGroupDto fdPriorityChangeComments = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PRIORITY_CHANGES_COMMENTS
						, FormGroupsConstants.TMPLAT_PRIORITY_CHANGES);
				fdPriorityChange.setFormDataGroupList(Arrays.asList(fdPriorityChangeComments));
				fdPriorityChangeComments.setBookmarkDtoList(Arrays.asList(bkPriorExpl));
			}
		}

		/** add all to prefill data*/
		PreFillDataServiceDto preFillDataServiceDto = new PreFillDataServiceDto();
		// Blobs
		preFillDataServiceDto.setBlobDataDtoList( new ArrayList<>(Arrays.asList(createBlobData(BookmarkConstants.CALL_NARR_BLOB,
				ServiceConstants.INCOMING_NARRATIVE_VIEW,  cpsIntakeReportFormDto.getIncomingStageDetailsDto().getIdStage().intValue()))));
		// Groups
		preFillDataServiceDto.setFormDataGroupList(formDataGroupList);
		// top level bookmarks
		preFillDataServiceDto.setBookmarkDtoList(bookmarkDtoDefaultDtoList);

		return preFillDataServiceDto;
	}

	/**
	 * Description: Person List information for CPS case
	 * GroupName: Primary or parent group - formDataGroupName
	 * SubGroups: commaGroupDto, addressGroupDto, phoneGroupDto, aliasGroupDto
	 * Condition: personType
	 * artf178638 Creating primary group here
	 */
	public void populatePersonListInfo(List<FormDataGroupDto> formDataGroupList
			, CpsIntakeReportFormDto cpsIntakeReportFormDto, int personType) {
		List<PersonDto> personDtoList = new ArrayList<>();
		String formDataGroupName = FormConstants.EMPTY_STRING;
		Map<String, String> map = new HashMap<String, String>();
		IncomingStageDetailsDto incmgDtls = null;

		switch (personType) {

			case ServiceConstants.VICTIM_TYPE:
				personDtoList = cpsIntakeReportFormDto.getVictims();
				formDataGroupName = FormGroupsConstants.TMPLAT_VICTIM;
				map = ServiceConstants.VICTIM_MAP;
				break;

			case ServiceConstants.PERPETRATOR_TYPE:
				personDtoList = cpsIntakeReportFormDto.getPerpetrators();
				formDataGroupName = FormGroupsConstants.TMPLAT_ALLEG_PERP;
				map = ServiceConstants.AP_MAP;
				break;

			case ServiceConstants.OTHER_PRN_TYPE:
				personDtoList = cpsIntakeReportFormDto.getOtherPrinciples();
				formDataGroupName = FormGroupsConstants.TMPLAT_PRINC_OTHER;
				map = ServiceConstants.PRINC_MAP;
				break;

			case ServiceConstants.REPORTER_TYPE:
				personDtoList = cpsIntakeReportFormDto.getReporters();
				formDataGroupName = FormGroupsConstants.TMPLAT_REPORTER;
				map = ServiceConstants.RPT_MAP;
				incmgDtls = cpsIntakeReportFormDto.getIncomingStageDetailsDto(); // artf164166
				break;

			case ServiceConstants.COLLATERAL_TYPE:
				personDtoList = cpsIntakeReportFormDto.getCollaterals();
				formDataGroupName = FormGroupsConstants.TMPLAT_COLLATERAL;
				map = ServiceConstants.COL_MAP;
				break;
		}
		for (PersonDto person : personDtoList) {
			/** Create and add a primary group to the formDataGroupList */
			FormDataGroupDto primaryGroupDto
					= createFormDataGroup(formDataGroupName, FormConstants.EMPTY_STRING);
			formDataGroupList.add(primaryGroupDto);
			/** Create and set the list of all subgroups and bookmarks to the primary group */
			List<FormDataGroupDto> primaryGroupDtoList = new ArrayList<FormDataGroupDto>();
			primaryGroupDto.setFormDataGroupList(primaryGroupDtoList);
			/** Create and set the list of fields to the primary group */
			List<BookmarkDto> personBookmarkDtoList = new ArrayList<>();
			primaryGroupDto.setBookmarkDtoList(personBookmarkDtoList);
			personBookmarkDtoList.addAll(Arrays.asList(
					createBookmark( map.getOrDefault(ServiceConstants.NAME_FIRST, ServiceConstants.NOT ), person.getNmPersonFirst())
					, createBookmark( map.getOrDefault(ServiceConstants.NAME_MIDDLE, ServiceConstants.NOT ), person.getNmPersonMiddle())
					, createBookmark( map.getOrDefault(ServiceConstants.NAME_LAST, ServiceConstants.NOT ), person.getNmPersonLast())
					, createBookmarkWithCodesTable( map.getOrDefault(ServiceConstants.NAME_SUFFIX, ServiceConstants.NOT ), person.getCdPersonSuffix(), CodesConstant.CSUFFIX2)
					, createBookmarkWithCodesTable( map.getOrDefault(ServiceConstants.RELTNSP, ServiceConstants.NOT ), person.getCdStagePersRelInt(), CodesConstant.CRPTRINT)
					, createBookmarkWithCodesTable( map.getOrDefault(ServiceConstants.ROLE, ServiceConstants.NOT ), person.getCdStagePersRole(), CodesConstant.CROLEALL)
					, createBookmark( map.getOrDefault(ServiceConstants.AGE, ServiceConstants.NOT ), person.getNbrPersonAge())
					, createBookmark( map.getOrDefault(ServiceConstants.SSN, ServiceConstants.NOT ), !ObjectUtils.isEmpty(person.getNbrPersonIdNumber())
							? TypeConvUtil.formatSSN(person.getNbrPersonIdNumber()) : FormConstants.EMPTY_STRING)
					, createBookmarkWithCodesTable( map.getOrDefault(ServiceConstants.LANG, ServiceConstants.NOT ), person.getCdPersonLanguage(), CodesConstant.CLANG)
					, createBookmark( map.getOrDefault(ServiceConstants.DOB_APPROX, ServiceConstants.NOT ), person.getIndPersonDobApprox())
					, createBookmark( map.getOrDefault(ServiceConstants.DOB, ServiceConstants.NOT ), !ObjectUtils.isEmpty(person.getDtPersonBirth())
							? DateUtils.stringDt(person.getDtPersonBirth()) : FormConstants.EMPTY_STRING)
					, createBookmark( map.getOrDefault(ServiceConstants.DOD, ServiceConstants.NOT ), !ObjectUtils.isEmpty(person.getDtPersonDeath())
							? DateUtils.stringDt(person.getDtPersonDeath()) : person.getDtPersonDeath())
					, createBookmarkWithCodesTable( map.getOrDefault(ServiceConstants.ETHNCTY, ServiceConstants.NOT ), person.getCdPersonEthnicGroup(), CodesConstant.CETHNIC)
					, createBookmarkWithCodesTable( map.getOrDefault(ServiceConstants.SEX, ServiceConstants.NOT ), person.getCdPersonSex(), CodesConstant.CSEX)
					, createBookmarkWithCodesTable( map.getOrDefault(ServiceConstants.RSN, ServiceConstants.NOT ), person.getCdPersonDeath(), CodesConstant.CRSNFDTH)
					, createBookmarkWithCodesTable( map.getOrDefault(ServiceConstants.MARITAL, ServiceConstants.NOT ), person.getCdPersonMaritalStatus(), CodesConstant.CMARSTAT)
					, createBookmark( map.getOrDefault(ServiceConstants.IN_LAW, ServiceConstants.NOT ), person.getIndStagePersInLaw())
					, createBookmark( map.getOrDefault(ServiceConstants.NOTES, ServiceConstants.NOT ), person.getTxtStagePersNotes())
					, createBookmark( map.getOrDefault(ServiceConstants.EMAIL, ServiceConstants.NOT ), person.getTxtEmail()) // artf261922
			));
			/**  --Places a comma when the name has a suffix -- TMPLAT_COMMA  */
			if (StringUtils.isNotBlank(person.getCdPersonSuffix())) {
				/** Create and add the Subgroup */
				primaryGroupDtoList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA, formDataGroupName));
			}
			/** -- Full address for person information with notes */
			for (PersonAddrLinkDto addr : cpsIntakeReportFormDto.getPersonAddrLinkDtoList()) {
				if (addr.getIdPerson() == person.getIdPerson()) {
					/** Create and add the Subgroup */
					FormDataGroupDto addressGroupDto
							= createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_FULL, formDataGroupName);
					primaryGroupDtoList.add(addressGroupDto);
					/** Create and set the list of fields to the subgroup */
					addressGroupDto.setBookmarkDtoList(Arrays.asList(
							createBookmark(BookmarkConstants.ADDR_ZIP, addr.getAddrPersonAddrZip())
							, createBookmark(BookmarkConstants.ADDR_CITY, addr.getAddrPersonAddrCity())
							, createBookmark(BookmarkConstants.ADDR_ATTN, addr.getPersAddressAttention())
							, createBookmark(BookmarkConstants.ADDR_LN_1, addr.getAddrPersAddrStLn1())
							, createBookmark(BookmarkConstants.ADDR_LN_2, addr.getAddrPersAddrStLn2())
							, createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY, addr.getCdPersonAddrCounty(), CodesConstant.CCOUNT)
							, createBookmark(BookmarkConstants.ADDR_STATE, addr.getCdPersonAddrState())
							, createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE, addr.getCdPersAddrLinkType(), CodesConstant.CADDRTYP)
							, createBookmark(BookmarkConstants.ADDR_NOTES, addr.getPersAddrCmnts())
					));
				}
			}
			/** -- artf164166 Use incoming_detail when Reporter Rel-Int is FPS Staff */
			if (ServiceConstants.REPORTER_TYPE == personType
					&& CpsIntakeReportService.FPS_STAFF_REPORTER.equals(person.getIdPerson())) {
				/** Create and add the Subgroup */
				FormDataGroupDto addressGroupDto
						= createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_FULL, formDataGroupName);
				primaryGroupDtoList.add(addressGroupDto);
				/** Create and set the list of fields to the subgroup */
				addressGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmark(BookmarkConstants.ADDR_ZIP, incmgDtls.getAddrIncmgZip())
						, createBookmark(BookmarkConstants.ADDR_CITY, incmgDtls.getAddrIncomingCallerCity())
						, createBookmark(BookmarkConstants.ADDR_LN_1, incmgDtls.getAddrIncmgStreetLn1())
						, createBookmark(BookmarkConstants.ADDR_LN_2, incmgDtls.getAddrIncmgStreetLn2())
						, createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY, incmgDtls.getCdIncomingCallerCounty(), CodesConstant.CCOUNT)
						, createBookmark(BookmarkConstants.ADDR_STATE, incmgDtls.getCdIncomingCallerState())
						, createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE, incmgDtls.getCdIncmgCallerAddrType(), CodesConstant.CADDRTYP)
				));
			}
			/** -- Phone information for person with notes */
			for (PhoneInfoDto phone : cpsIntakeReportFormDto.getPersonPhoneDtoList()) {
				if (phone.getIdPerson().equals(person.getIdPerson())) {
					/** Create and add the Subgroup */
					FormDataGroupDto phoneGroupDto
							= createFormDataGroup(FormGroupsConstants.TMPLAT_PHONE, formDataGroupName);
					primaryGroupDtoList.add(phoneGroupDto);
					/** Create and set the list of fields to the subgroup */
					phoneGroupDto.setBookmarkDtoList(Arrays.asList(
							createBookmark(BookmarkConstants.PHONE_NUMBER, !ObjectUtils.isEmpty(phone.getNbrPersonPhone())
									? TypeConvUtil.formatPhone(phone.getNbrPersonPhone()) : FormConstants.EMPTY_STRING)
							, createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION, phone.getNbrPersonPhoneExtension())
							, createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE, phone.getCdPersonPhoneType(), CodesConstant.CPHNTYP)
							, createBookmark(BookmarkConstants.PHONE_NOTES, phone.getTxtPersonPhoneComments())
					));
				}
			}
			/** -- artf164166 Use incoming_detail when Reporter Rel-Int is FPS Staff */
			if (ServiceConstants.REPORTER_TYPE == personType
					&& CpsIntakeReportService.FPS_STAFF_REPORTER.equals(person.getIdPerson())) {
				/** Create and add the Subgroup */
				FormDataGroupDto phoneGroupDto
						= createFormDataGroup(FormGroupsConstants.TMPLAT_PHONE, formDataGroupName);
				primaryGroupDtoList.add(phoneGroupDto);
				/** Create and set the list of fields to the subgroup */
				phoneGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmark(BookmarkConstants.PHONE_NUMBER, !ObjectUtils.isEmpty(incmgDtls.getNbrIncomingCallerPhone())
								? TypeConvUtil.formatPhone(incmgDtls.getNbrIncomingCallerPhone()) : FormConstants.EMPTY_STRING)
						, createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION, incmgDtls.getNbrIncmgCallerPhonExt())
						, createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE, incmgDtls.getCdIncmgCallerPhonType(), CodesConstant.CPHNTYP)
				));
				/** -- artf261922 Email information from incoming_detail */
				personBookmarkDtoList.add(createBookmark(BookmarkConstants.RPT_EMAIL, incmgDtls.getTxtReporterEmail()));
			}
			/**   Alias information for person TMPLAT_ALIAS */
			for (NameDto name : cpsIntakeReportFormDto.getNameAliasList()) {
				if (name.getIdPerson() == person.getIdPerson()) {
					/** Create and add the Subgroup */
					FormDataGroupDto aliasGroupDto
							= createFormDataGroup(FormGroupsConstants.TMPLAT_ALIAS, formDataGroupName);
					primaryGroupDtoList.add(aliasGroupDto);
					/** Create and set the list of fields to the subgroup */
					aliasGroupDto.setBookmarkDtoList(Arrays.asList(
							createBookmark(BookmarkConstants.ALIAS_NAME_FIRST, name.getFirstName())
							, createBookmark(BookmarkConstants.ALIAS_NAME_LAST, name.getLastName())
							, createBookmark(BookmarkConstants.ALIAS_NAME_MIDDLE, name.getMiddleName())
					));
				}
			}
		}
	}
}
