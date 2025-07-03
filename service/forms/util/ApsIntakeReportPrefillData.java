package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.IncmgDetermFactors;
import us.tx.state.dfps.common.dto.ApprovalDto;
import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.common.dto.NameDto;
import us.tx.state.dfps.common.dto.PhoneInfoDto;
import us.tx.state.dfps.common.dto.PriorityChangeInfoDto;
import us.tx.state.dfps.common.dto.WorkerInfoDto;
import us.tx.state.dfps.common.dto.AgencyHomeInfoDto;
import us.tx.state.dfps.common.dto.ClassIntakeDto;
import us.tx.state.dfps.service.apsintakereport.dto.ApsIntakeReportDto;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityOutputDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsintakereportform.dto.CpsIntakeReportFormDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.person.dto.PersonAddrLinkDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Generates
 * prefill string for form population Mar 19, 2018- 9:52:22 AM © 2017 Texas
 * Department of Family and Protective Services
 * * **********  Change History *********************************
 * 09/28/2020 thompswa artf164166 : Use incoming_detail when Reporter Rel-Int is FPS Staff
 * 12/15/2020 thompswa artf169810 : getTxtRelatedCalls for new bookmark, SUM_RELATED_REPORTS.
 * 05/05/2020 kurmav artf220333 Populate class information, agency information in cfin0200
 * 11/22/2022 thompswa artf178638 : Intake discrepancy ModPhase2 vs Legacy add populatePersonListInfo common method
 * 10/05/2023 thompswa artf251139 : add Child Death Indicator, get populatePersonListInfo from CpsIntakeReportFormPrefillData
 */
@Component
public class ApsIntakeReportPrefillData extends DocumentServiceUtil {

	@Autowired
	CpsIntakeReportFormPrefillData intakePersonPrefill;

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		ApsIntakeReportDto prefillDto = (ApsIntakeReportDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// Initialize null DTOs
		CpsIntakeReportFormDto intakeReportFormDto = new CpsIntakeReportFormDto();
		if (ObjectUtils.isEmpty(prefillDto.getApprovalList())) {
			prefillDto.setApprovalList(new ArrayList<ApprovalDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getCollateralsList())) {
			prefillDto.setCollateralsList(new ArrayList<PersonDto>());
		}
		intakeReportFormDto.setCollaterals(prefillDto.getCollateralsList());
		if (ObjectUtils.isEmpty(prefillDto.getEventDetailsList())) {
			prefillDto.setEventDetailsList(new ArrayList<EventDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getIncmgDetermFactorsList())) {
			prefillDto.setIncmgDetermFactorsList(new ArrayList<IncmgDetermFactors>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getIncomingFacilityDto())) {
			prefillDto.setIncomingFacilityDto(new RetreiveIncomingFacilityOutputDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto())) {
			prefillDto.setIncomingStageDetailsDto(new IncomingStageDetailsDto());
		}
		intakeReportFormDto.setIncomingStageDetailsDto(prefillDto.getIncomingStageDetailsDto());
		if (ObjectUtils.isEmpty(prefillDto.getIntakeAllegationList())) {
			prefillDto.setIntakeAllegationList(new ArrayList<IntakeAllegationDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getNamesList())) {
			prefillDto.setNamesList(new ArrayList<NameDto>());
		}
		intakeReportFormDto.setNameAliasList(prefillDto.getNamesList());
		if (ObjectUtils.isEmpty(prefillDto.getOthersList())) {
			prefillDto.setOthersList(new ArrayList<PersonDto>());
		}
		intakeReportFormDto.setOtherPrinciples(prefillDto.getOthersList());
		if (ObjectUtils.isEmpty(prefillDto.getPerpsList())) {
			prefillDto.setPerpsList(new ArrayList<PersonDto>());
		}
		intakeReportFormDto.setPerpetrators(prefillDto.getPerpsList());
		if (ObjectUtils.isEmpty(prefillDto.getPersonAddrLinkList())) {
			prefillDto.setPersonAddrLinkList(new ArrayList<PersonAddrLinkDto>());
		}
		intakeReportFormDto.setPersonAddrLinkDtoList(prefillDto.getPersonAddrLinkList());
		if (ObjectUtils.isEmpty(prefillDto.getPhoneInfoList())) {
			prefillDto.setPhoneInfoList(new ArrayList<PhoneInfoDto>());
		}
		intakeReportFormDto.setPersonPhoneDtoList(prefillDto.getPhoneInfoList());
		if (ObjectUtils.isEmpty(prefillDto.getPriorityChangeInfoList())) {
			prefillDto.setPriorityChangeInfoList(new ArrayList<PriorityChangeInfoDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getReportersList())) {
			prefillDto.setReportersList(new ArrayList<PersonDto>());
		}
		intakeReportFormDto.setReporters(prefillDto.getReportersList());
		if (ObjectUtils.isEmpty(prefillDto.getVictimsList())) {
			prefillDto.setVictimsList(new ArrayList<PersonDto>());
		}
		intakeReportFormDto.setVictims(prefillDto.getVictimsList());
		if (ObjectUtils.isEmpty(prefillDto.getWorkerInfoList())) {
			prefillDto.setWorkerInfoList(new ArrayList<WorkerInfoDto>());
		}
		if (ObjectUtils.isEmpty(prefillDto.getIntakeDto())) { // artf220333
			prefillDto.setIntakeDto(new ClassIntakeDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getAgencyHomeInfoDto())) { // artf220333
			prefillDto.setAgencyHomeInfoDto(new AgencyHomeInfoDto());
		}
		if (ObjectUtils.isEmpty(prefillDto.getResourceInfoDto())) { // artf220333
			prefillDto.setResourceInfoDto(new AgencyHomeInfoDto());
		}

		// only for cfin0200
		if ("cfin0200".equals(prefillDto.getFormName())) {
			// parent group cfin0108

			boolean isMostRecentPriorityChange = true; //most recent Priority Change will be the first one
			for (PriorityChangeInfoDto priorityChangeDto : prefillDto.getPriorityChangeInfoList()) {
				FormDataGroupDto priorityChangesGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_PRIORITY_CHANGES, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPriorityList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkHisPriorityChange = createBookmark(BookmarkConstants.HIS_PRIORITY_CHANGE,
						priorityChangeDto.getCdEventStatus());
				bookmarkPriorityList.add(bookmarkHisPriorityChange);
				BookmarkDto bookmarkHisPriorityReason = createBookmarkWithCodesTable(
						BookmarkConstants.HIS_PRIORITY_REASON, priorityChangeDto.getCdStageRsnPriorityChgd(),
						CodesConstant.CRSNPRIO);
				bookmarkPriorityList.add(bookmarkHisPriorityReason);

				//[artf176887] defect 16905: for first (most-recent) Priority Change instance, use txtStagePriorityCmnts
				//	from Stage to create the HIS_PRIORITY_EXPL Bookmark
				BookmarkDto bookmarkHisPriorityExpl;
				if(isMostRecentPriorityChange) {
					bookmarkHisPriorityExpl = createBookmark(BookmarkConstants.HIS_PRIORITY_EXPL,
							prefillDto.getIncomingStageDetailsDto().getTxtStagePriorityCmnts());
					isMostRecentPriorityChange = false;
				} else {
					bookmarkHisPriorityExpl = createBookmark(BookmarkConstants.HIS_PRIORITY_EXPL,
							priorityChangeDto.getTxtStagePriorityCmnts());
				}
				//[artf176887] defect 16905: if the Bookmark data is not null, create a FormDataGroup and add the Bookmark
				//	to it, then add the FormDataGroup to the TMPLAT_PRIORITY_CHANGES_COMMENTS FormDataGroup. This way, the
				//	Comments section for that Priority Change will only show if it is not null
				if(StringUtils.isNotEmpty(bookmarkHisPriorityExpl.getBookmarkData())) {
					FormDataGroupDto fdPriorityChangeComments = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIORITY_CHANGES_COMMENTS, FormGroupsConstants.TMPLAT_PRIORITY_CHANGES);
					List<BookmarkDto> bkPriorityChangeCommentsList = new ArrayList<BookmarkDto>();
					bkPriorityChangeCommentsList.add(bookmarkHisPriorityExpl);
					fdPriorityChangeComments.setBookmarkDtoList(bkPriorityChangeCommentsList);
					List<FormDataGroupDto> fdPriorityChangeCommentsList = new ArrayList<FormDataGroupDto>();
					fdPriorityChangeCommentsList.add(fdPriorityChangeComments);
					priorityChangesGroupDto.setFormDataGroupList(fdPriorityChangeCommentsList);
				}
				priorityChangesGroupDto.setBookmarkDtoList(bookmarkPriorityList);
				formDataGroupList.add(priorityChangesGroupDto);
			}

			// parent group cfzco00
			for (ApprovalDto approvalDto : prefillDto.getApprovalList()) {
				if (StringUtils.isNotBlank(approvalDto.getCdNameSuffix())) {
					FormDataGroupDto comma2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(comma2GroupDto);
				}
			}

			// artf178637 add rcci mref count
			if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getNbrMultiRefInvCnt())) {
				formDataGroupList.add( createFormDataGroup(FormGroupsConstants.TMPLAT_MREF_LABEL_GRP, FormConstants.EMPTY_STRING));
				FormDataGroupDto mrefValueGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MREF_VALUE_GRP, FormConstants.EMPTY_STRING);
				mrefValueGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmark(BookmarkConstants.MULTI_REF, prefillDto.getIncomingStageDetailsDto().getNbrMultiRefInvCnt())));
				formDataGroupList.add(mrefValueGroupDto);
			}
		}
		// parent group cfin0302
		for (IntakeAllegationDto intakeAllegationDto : prefillDto.getIntakeAllegationList()) {
			FormDataGroupDto allegationGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION, FormConstants.EMPTY_STRING);
			allegationGroupDto.setBookmarkDtoList(Arrays.asList(
					createBookmark(BookmarkConstants.ALLEG_DTL_DURTN, intakeAllegationDto.getIntakeAllegDuration()) // ignored by cfin0200
					, createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG, intakeAllegationDto.getCdIntakeAllegType()
							, "cfin0200".equals(prefillDto.getFormName()) ? CodesConstant.CCLICALT : CodesConstant.CAPSALLG)
					, createBookmark(BookmarkConstants.ALLEG_DTL_AP, intakeAllegationDto.getNmPerpetrator())
					, createBookmark(BookmarkConstants.ALLEG_DTL_VICTIM, intakeAllegationDto.getNmVictim())
			));
			formDataGroupList.add(allegationGroupDto);
		}

		// parent group cfin0106
		for (IncmgDetermFactors incomingDetFactor : prefillDto.getIncmgDetermFactorsList()) {
			// attribute exists for cfin0400 but not cfin0200 or cfin0800
			if (!("cfin0400".equals(prefillDto.getFormName())) || ("cfin0400".equals(prefillDto.getFormName())
					&& incomingDetFactor.getCdIncmgDetermType().contains(ServiceConstants.FOSTER))) {
				FormDataGroupDto detFactorGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DET_FACTR, FormConstants.EMPTY_STRING);
				// Codes table name depends on which form
				detFactorGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmarkWithCodesTable(BookmarkConstants.DETERM_FACTR, incomingDetFactor.getCdIncmgDeterm()
								, "cfin0200".equals(prefillDto.getFormName()) ?  CodesConstant.CDETFACT :  CodesConstant.CADETFCT)
				));
				formDataGroupList.add(detFactorGroupDto);
			}
		}

		// Principal and Collateral Information - artf178638 refactor start
		intakePersonPrefill.populatePersonListInfo(formDataGroupList, intakeReportFormDto, ServiceConstants.VICTIM_TYPE); // parent group cfin0101 (cfin0200) and cfin0301 (cfin0400 & cfin0800)
		intakePersonPrefill.populatePersonListInfo(formDataGroupList, intakeReportFormDto, ServiceConstants.PERPETRATOR_TYPE); // parent group cfin0102
		intakePersonPrefill.populatePersonListInfo(formDataGroupList, intakeReportFormDto, ServiceConstants.OTHER_PRN_TYPE); // parent group cfin0103
		/** do not get the reporter for the de-identified versions cfin0700 and cfin0800 */
		if ( ! Arrays.asList(ServiceConstants.CFIN0700, ServiceConstants.CFIN0800).contains(prefillDto.getFormName()))
			intakePersonPrefill.populatePersonListInfo(formDataGroupList, intakeReportFormDto, ServiceConstants.REPORTER_TYPE);
		intakePersonPrefill.populatePersonListInfo(formDataGroupList, intakeReportFormDto, ServiceConstants.COLLATERAL_TYPE); // artf178638 refactor end

		// parent group cfin02ws (cfin0200) and cfin04ws (cfin0400) and cfin08ws
		// (cfin0800)
		if (ServiceConstants.Y.equalsIgnoreCase(prefillDto.getIncomingStageDetailsDto().getIndIncmgWorkerSafety())) {
			FormDataGroupDto wsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkWsList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkWsText = createBookmark(BookmarkConstants.WS_TEXT,
					prefillDto.getIncomingStageDetailsDto().getTxtIncmgWorkerSafety());
			bookmarkWsList.add(bookmarkWsText);
			wsGroupDto.setBookmarkDtoList(bookmarkWsList);
			formDataGroupList.add(wsGroupDto);
		}

		// parent groups cfzco00
		for (WorkerInfoDto workerInfoDto : prefillDto.getWorkerInfoList()) {
			if (StringUtils.isNotBlank(workerInfoDto.getCdNameSuffix())) {
				FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(commaGroupDto);
			}
		}

		// Non-group bookmarks
		List<BookmarkDto> bookmarkOrphanList = new ArrayList<BookmarkDto>();
		
		// bookmarks only in cfin0200
		if ("cfin0200".equals(prefillDto.getFormName())) {
			// CINT65D
			if(!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtEventOccurred())) {
			BookmarkDto bookmarkSumLeNotifyDate = createBookmark(BookmarkConstants.SUM_LE_NOTIFY_DATE,
					DateUtils.stringDt(prefillDto.getIncomingStageDetailsDto().getDtEventOccurred()));
			bookmarkOrphanList.add(bookmarkSumLeNotifyDate);
			}
			
			if(!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
			BookmarkDto bookmarkHisRecordedCallDate = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_DATE,
					DateUtils.stringDt(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall()));
			bookmarkOrphanList.add(bookmarkHisRecordedCallDate);
			}
			
			BookmarkDto bookmarkHisRecordedCallExt = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_EXT,
					prefillDto.getIncomingStageDetailsDto().getIncmgWorkerExt());
			bookmarkOrphanList.add(bookmarkHisRecordedCallExt);
			
			if(!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getNbrIncmgWorkerPhone())) {
			BookmarkDto bookmarkHisRecordedCallPhone = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_PHONE,
					TypeConvUtil.formatPhone(prefillDto.getIncomingStageDetailsDto().getNbrIncmgWorkerPhone()));
			bookmarkOrphanList.add(bookmarkHisRecordedCallPhone);
			}
			
			BookmarkDto bookmarkSumWorkerCity = createBookmark(BookmarkConstants.SUM_WORKER_CITY,
					prefillDto.getIncomingStageDetailsDto().getAddrIncmgWorkerCity());
			bookmarkOrphanList.add(bookmarkSumWorkerCity);
			BookmarkDto bookmarkHisRecordedCallCity = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_CITY,
					prefillDto.getIncomingStageDetailsDto().getAddrIncmgWorkerCity());
			bookmarkOrphanList.add(bookmarkHisRecordedCallCity);
			BookmarkDto bookmarkHisRecordedCallBjn = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_BJN,
					prefillDto.getIncomingStageDetailsDto().getCdEmpBjnEmp());
			bookmarkOrphanList.add(bookmarkHisRecordedCallBjn);
			BookmarkDto bookmarkSumPrimAlleg = createBookmarkWithCodesTable(BookmarkConstants.SUM_PRIM_ALLEG,
					prefillDto.getIncomingStageDetailsDto().getCdIncmgAllegType(), CodesConstant.CCLICALT);
			bookmarkOrphanList.add(bookmarkSumPrimAlleg);
			BookmarkDto bookmarkHisCurrPriority = createBookmarkWithCodesTable(BookmarkConstants.HIS_CURR_PRIORITY,
					prefillDto.getIncomingStageDetailsDto().getCdStageCurrPriority(), CodesConstant.CPRIORTY);
			bookmarkOrphanList.add(bookmarkHisCurrPriority);
			BookmarkDto bookmarkHisInitialPriority = createBookmarkWithCodesTable(
					BookmarkConstants.HIS_INITIAL_PRIORITY,
					prefillDto.getIncomingStageDetailsDto().getCdStageInitialPriority(), CodesConstant.CPRIORTY);
			bookmarkOrphanList.add(bookmarkHisInitialPriority);
			BookmarkDto bookmarkSumRsnForClosr;
			BookmarkDto bookmarkHisRsnForClosure;
			if (StringUtils.isNotBlank(prefillDto.getIncomingStageDetailsDto().getCdStageReasonClosed())) {
				bookmarkSumRsnForClosr = createBookmarkWithCodesTable(BookmarkConstants.SUM_RSN_FOR_CLOSR,
						prefillDto.getIncomingStageDetailsDto().getCdStageReasonClosed(), CodesConstant.CCLOSUR1);
				bookmarkHisRsnForClosure = createBookmarkWithCodesTable(BookmarkConstants.HIS_RSN_FOR_CLOSURE,
						prefillDto.getIncomingStageDetailsDto().getCdStageReasonClosed(), CodesConstant.CCLOSUR1);
			} else {
				bookmarkSumRsnForClosr = createBookmark(BookmarkConstants.SUM_RSN_FOR_CLOSR,
						prefillDto.getIncomingStageDetailsDto().getCdStageReasonClosed());
				bookmarkHisRsnForClosure = createBookmark(BookmarkConstants.HIS_RSN_FOR_CLOSURE,
						prefillDto.getIncomingStageDetailsDto().getCdStageReasonClosed());
			}
			bookmarkOrphanList.add(bookmarkSumRsnForClosr);
			bookmarkOrphanList.add(bookmarkHisRsnForClosure);
			BookmarkDto bookmarkHisRecordedCall = createBookmark(BookmarkConstants.HIS_RECORDED_CALL,
					prefillDto.getIncomingStageDetailsDto().getNmIncmgWorkerName());
			bookmarkOrphanList.add(bookmarkHisRecordedCall);
			BookmarkDto bookmarkSumLeJuris = createBookmark(BookmarkConstants.SUM_LE_JURIS,
					prefillDto.getIncomingStageDetailsDto().getNmIncmgJurisdiction());
			bookmarkOrphanList.add(bookmarkSumLeJuris);
			
			if(!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
			BookmarkDto bookmarkHisRecordedCallTime = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_TIME,
					DateUtils.getTime(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall()));
			bookmarkOrphanList.add(bookmarkHisRecordedCallTime);
			}

			// CINT67D
			for (WorkerInfoDto workerDto : prefillDto.getWorkerInfoList()) {
				
				if(!ObjectUtils.isEmpty(workerDto.getNbrPersonPhone())) {
				BookmarkDto bookmarkHisStageChangePhone = createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_PHONE,
						TypeConvUtil.formatPhone(workerDto.getNbrPersonPhone()));
				bookmarkOrphanList.add(bookmarkHisStageChangePhone);
				}
				BookmarkDto bookmarkHisStageChangeExt = createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_EXT,
						workerDto.getNbrPersonPhoneExtension());
				bookmarkOrphanList.add(bookmarkHisStageChangeExt);
				BookmarkDto bookmarkHisStageChangeCity = createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_CITY,
						workerDto.getAddrMailCodeCity());
				bookmarkOrphanList.add(bookmarkHisStageChangeCity);
				BookmarkDto bookmarkHisStageChangeBjn = createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_BJN,
						workerDto.getCdJobBjn());
				bookmarkOrphanList.add(bookmarkHisStageChangeBjn);
				BookmarkDto bookmarkHisStageChangeSuffix = createBookmarkWithCodesTable(
						BookmarkConstants.HIS_STAGE_CHANGE_SUFFIX, workerDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				bookmarkOrphanList.add(bookmarkHisStageChangeSuffix);
				BookmarkDto bookmarkHisStageChangeFirst = createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_FIRST,
						workerDto.getNmNameFirst());
				bookmarkOrphanList.add(bookmarkHisStageChangeFirst);
				BookmarkDto bookmarkHisStageChangeLast = createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_LAST,
						workerDto.getNmNameLast());
				bookmarkOrphanList.add(bookmarkHisStageChangeLast);
				BookmarkDto bookmarkHisStageChangeMiddle = createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_MIDDLE,
						workerDto.getNmNameMiddle());
				bookmarkOrphanList.add(bookmarkHisStageChangeMiddle);
				
				if(!ObjectUtils.isEmpty(workerDto.getDtEventModified())) {
				BookmarkDto bookmarkHisStageChangeTime = createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_TIME,
						DateUtils.getTime(workerDto.getDtEventModified()));
				bookmarkOrphanList.add(bookmarkHisStageChangeTime);
				}
				
				if(!ObjectUtils.isEmpty(workerDto.getDtEventModified())) {
				BookmarkDto bookmarkHisStageChangeDate = createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_DATE,
						DateUtils.stringDt(workerDto.getDtEventModified()));
				bookmarkOrphanList.add(bookmarkHisStageChangeDate);
				}
			}

			// CLSC52D
			for (ApprovalDto approvalDto : prefillDto.getApprovalList()) {
				
				if(!ObjectUtils.isEmpty(approvalDto.getDtApproversDetermination())) {
				BookmarkDto bookmarkHisApprovedDate = createBookmark(BookmarkConstants.HIS_APPROVED_DATE,
						DateUtils.stringDt(approvalDto.getDtApproversDetermination()));
				bookmarkOrphanList.add(bookmarkHisApprovedDate);
				}
				
				if(!ObjectUtils.isEmpty(approvalDto.getNbrPersonPhone())) {
				BookmarkDto bookmarkHisApprovedPhone = createBookmark(BookmarkConstants.HIS_APPROVED_PHONE,
						TypeConvUtil.formatPhone(approvalDto.getNbrPersonPhone()));
				bookmarkOrphanList.add(bookmarkHisApprovedPhone);
				}
				BookmarkDto bookmarkHisApprovedExt = createBookmark(BookmarkConstants.HIS_APPROVED_EXT,
						approvalDto.getNbrPersonPhoneExtension());
				bookmarkOrphanList.add(bookmarkHisApprovedExt);
				BookmarkDto bookmarkHisApprovedCity = createBookmark(BookmarkConstants.HIS_APPROVED_CITY,
						approvalDto.getAddrMailCodeCity());
				bookmarkOrphanList.add(bookmarkHisApprovedCity);
				BookmarkDto bookmarkHisApprovedBjn = createBookmark(BookmarkConstants.HIS_APPROVED_BJN,
						approvalDto.getCdJobBjn());
				bookmarkOrphanList.add(bookmarkHisApprovedBjn);
				BookmarkDto bookmarkHisApprovedSuffix = createBookmarkWithCodesTable(
						BookmarkConstants.HIS_APPROVED_SUFFIX, approvalDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				bookmarkOrphanList.add(bookmarkHisApprovedSuffix);
				BookmarkDto bookmarkHisApprovedFirst = createBookmark(BookmarkConstants.HIS_APPROVED_FIRST,
						approvalDto.getNmNameFirst());
				bookmarkOrphanList.add(bookmarkHisApprovedFirst);
				BookmarkDto bookmarkHisApprovedLast = createBookmark(BookmarkConstants.HIS_APPROVED_LAST,
						approvalDto.getNmNameLast());
				bookmarkOrphanList.add(bookmarkHisApprovedLast);
				BookmarkDto bookmarkHisApprovedMiddle = createBookmark(BookmarkConstants.HIS_APPROVED_MIDDLE,
						approvalDto.getNmNameMiddle());
				bookmarkOrphanList.add(bookmarkHisApprovedMiddle);
				
				if(!ObjectUtils.isEmpty(approvalDto.getDtApproversDetermination())) {
				BookmarkDto bookmarkHisApprovedTime = createBookmark(BookmarkConstants.HIS_APPROVED_TIME,
						DateUtils.getTime(approvalDto.getDtApproversDetermination()));
				bookmarkOrphanList.add(bookmarkHisApprovedTime);
				}
			}
            // artf220333
			if (!ObjectUtils.isEmpty(prefillDto.getIntakeDto().getIncidentDate())) {
				bookmarkOrphanList.add(createBookmark(BookmarkConstants.SUM_INCIDENT_DATE,
						DateUtils.stringDt(prefillDto.getIntakeDto().getIncidentDate())));
				bookmarkOrphanList.add(createBookmark(BookmarkConstants.SUM_INCIDENT_TIME,
						DateUtils.getTime(prefillDto.getIntakeDto().getIncidentDate())));
			}
			if ("Y".equalsIgnoreCase(prefillDto.getIntakeDto().getSelfReport())) {
				bookmarkOrphanList.add(createBookmark(BookmarkConstants.SUM_SELF_REPORT,
						ServiceConstants.CHECKED));
			}
			bookmarkOrphanList.add(createBookmark(BookmarkConstants.RESOURCE_ID,
					prefillDto.getResourceInfoDto().getResourceId()));
			bookmarkOrphanList.add(createBookmark(BookmarkConstants.CLASS_OP_NUMBER, new StringBuilder()
					.append(!ObjectUtils.isEmpty(prefillDto.getResourceInfoDto().getFacilityNumber()) ? prefillDto.getResourceInfoDto().getFacilityNumber() + FormConstants.SPACE : FormConstants.EMPTY_STRING)
					.append(!ObjectUtils.isEmpty(prefillDto.getResourceInfoDto().getAgencyNumber()) ? prefillDto.getResourceInfoDto().getAgencyNumber() + FormConstants.SPACE : FormConstants.EMPTY_STRING)
					.append(!ObjectUtils.isEmpty(prefillDto.getResourceInfoDto().getBranchNumber()) ? prefillDto.getResourceInfoDto().getBranchNumber() : FormConstants.EMPTY_STRING).toString()));
            // This will add agency home if incoming facility is Child Placing Agency
			if (!ObjectUtils.isEmpty(prefillDto.getAgencyHomeInfoDto().getResourceId()) && 0 < prefillDto.getAgencyHomeInfoDto().getResourceId()) {
				FormDataGroupDto agencyHomeGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AGENCY,
						FormConstants.EMPTY_STRING);
				agencyHomeGroupDto.setBookmarkDtoList(Arrays.asList(
				createBookmark(BookmarkConstants.AGENCY_HOME_NAME, prefillDto.getAgencyHomeInfoDto().getFacilityName()),
				createBookmark(BookmarkConstants.AGENCY_HOME_TYPE, prefillDto.getAgencyHomeInfoDto().getFacilityType()),
				createBookmark(BookmarkConstants.AGENCY_RESOURCEID, prefillDto.getAgencyHomeInfoDto().getResourceId()),
				createBookmark(BookmarkConstants.AGENCY_ADDR_LN_1,prefillDto.getAgencyHomeInfoDto().getAgencyAddrStreetLn1()),
				createBookmark(BookmarkConstants.AGENCY_ADDR_CITY, prefillDto.getAgencyHomeInfoDto().getAgencyAddrCity()),
				createBookmark(BookmarkConstants.AGENCY_ADDR_ZIP, prefillDto.getAgencyHomeInfoDto().getAgencyAddrZip()),
				createBookmarkWithCodesTable(BookmarkConstants.AGENCY_ADDR_COUNTY, prefillDto.getAgencyHomeInfoDto().getAgencyAddrCounty(), CodesConstant.CCOUNT),
				createBookmark(BookmarkConstants.AGENCY_ADDR_STATE, prefillDto.getAgencyHomeInfoDto().getAgencyAddrState()),
				createBookmark(BookmarkConstants.AGENCY_HOME_PHONE,
						!ObjectUtils.isEmpty(prefillDto.getAgencyHomeInfoDto().getAgencyHomePhone())
								? TypeConvUtil.getPhoneWithFormat(prefillDto.getAgencyHomeInfoDto().getAgencyHomePhone(),null) : FormConstants.EMPTY_STRING),
				createBookmark(BookmarkConstants.AGENCY_CLASSOPNUM, new StringBuilder()
					.append(!ObjectUtils.isEmpty(prefillDto.getAgencyHomeInfoDto().getFacilityNumber()) ? prefillDto.getAgencyHomeInfoDto().getFacilityNumber() + FormConstants.SPACE : FormConstants.EMPTY_STRING)
					.append(!ObjectUtils.isEmpty(prefillDto.getAgencyHomeInfoDto().getAgencyNumber()) ? prefillDto.getAgencyHomeInfoDto().getAgencyNumber() + FormConstants.SPACE : FormConstants.EMPTY_STRING)
					.append(!ObjectUtils.isEmpty(prefillDto.getAgencyHomeInfoDto().getBranchNumber()) ? prefillDto.getAgencyHomeInfoDto().getBranchNumber() : FormConstants.EMPTY_STRING).toString())
				));
				if (!ObjectUtils.isEmpty(prefillDto.getAgencyHomeInfoDto().getAgencyAddrStreetLn2())) {
					FormDataGroupDto addr2GroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_AGENCY_ADDR2, FormGroupsConstants.TMPLAT_AGENCY);
					addr2GroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.AGENCY_ADDR_LN_2,
							prefillDto.getAgencyHomeInfoDto().getAgencyAddrStreetLn2())));
					agencyHomeGroupDto.setFormDataGroupList(Arrays.asList(addr2GroupDto));
				}
				formDataGroupList.add(agencyHomeGroupDto);
			}
		}

		// bookmarks only in cfin0400 & cfin0800
		else {
			// CINT65D
			if(!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
			BookmarkDto bookmarkDecIntDate = createBookmark(BookmarkConstants.DEC_INT_DATE,
					DateUtils.stringDt(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall()));
			bookmarkOrphanList.add(bookmarkDecIntDate);
			}
			
			
			BookmarkDto bookmarkSumWorkerOfficeCity = createBookmark(BookmarkConstants.SUM_WORKER_OFFICE_CITY,
					prefillDto.getIncomingStageDetailsDto().getAddrIncmgWorkerCity());
			bookmarkOrphanList.add(bookmarkSumWorkerOfficeCity);
			BookmarkDto bookmarkDecIntCity = createBookmark(BookmarkConstants.DEC_INT_CITY,
					prefillDto.getIncomingStageDetailsDto().getAddrIncmgWorkerCity());
			bookmarkOrphanList.add(bookmarkDecIntCity);
			BookmarkDto bookmarkSumPrimAlleg = createBookmarkWithCodesTable(BookmarkConstants.SUM_PRIM_ALLEG,
					prefillDto.getIncomingStageDetailsDto().getCdIncmgAllegType(), CodesConstant.CAPSALLG);
			bookmarkOrphanList.add(bookmarkSumPrimAlleg);
			BookmarkDto bookmarkDecSuprPriority = createBookmarkWithCodesTable(BookmarkConstants.DEC_SUPV_PRIORITY,
					prefillDto.getIncomingStageDetailsDto().getCdStageCurrPriority(), CodesConstant.CPRIORTY);
			bookmarkOrphanList.add(bookmarkDecSuprPriority);
			BookmarkDto bookmarkDecIntPriority = createBookmarkWithCodesTable(BookmarkConstants.DEC_INT_PRIORITY,
					prefillDto.getIncomingStageDetailsDto().getCdStageInitialPriority(), CodesConstant.CPRIORTY);
			bookmarkOrphanList.add(bookmarkDecIntPriority);
			BookmarkDto bookmarkDecSupvRsnChange = createBookmarkWithCodesTable(
					BookmarkConstants.DEC_SUPV_RSN_FOR_CHANGE,
					prefillDto.getIncomingStageDetailsDto().getCdStageRsnPriorityChgd(), CodesConstant.CRSNPRIO);
			bookmarkOrphanList.add(bookmarkDecSupvRsnChange);
			BookmarkDto bookmarkDecIntName = createBookmark(BookmarkConstants.DEC_INT_NAME,
					prefillDto.getIncomingStageDetailsDto().getNmIncmgWorkerName());
			bookmarkOrphanList.add(bookmarkDecIntName);
			
			if(!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
			BookmarkDto bookmarkDecIntTime = createBookmark(BookmarkConstants.DEC_INT_TIME,
					DateUtils.getTime(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall()));
			bookmarkOrphanList.add(bookmarkDecIntTime);
			}
		}

		// bookmarks only in cfin0400
		if ("cfin0400".equals(prefillDto.getFormName())) {
			// CLSC52D
			for (ApprovalDto approvalDto : prefillDto.getApprovalList()) {
				if(!ObjectUtils.isEmpty(approvalDto.getDtApproversDetermination())) {
				BookmarkDto bookmarkDecSupvDate = createBookmark(BookmarkConstants.DEC_SUPV_DATE,
						DateUtils.stringDt(approvalDto.getDtApproversDetermination()));
				bookmarkOrphanList.add(bookmarkDecSupvDate);
				}

				BookmarkDto bookmarkDecSupvCity = createBookmark(BookmarkConstants.DEC_SUPV_CITY,
						approvalDto.getAddrMailCodeCity());
				bookmarkOrphanList.add(bookmarkDecSupvCity);
				BookmarkDto bookmarkDecSupvSuffix = createBookmarkWithCodesTable(BookmarkConstants.DEC_SUPV_NAME_SUFFIX,
						approvalDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				bookmarkOrphanList.add(bookmarkDecSupvSuffix);
				BookmarkDto bookmarkDecSupvFirst = createBookmark(BookmarkConstants.DEC_SUPV_NAME_FIRST,
						approvalDto.getNmNameFirst());
				bookmarkOrphanList.add(bookmarkDecSupvFirst);
				BookmarkDto bookmarkDecSupvLast = createBookmark(BookmarkConstants.DEC_SUPV_NAME_LAST,
						approvalDto.getNmNameLast());
				bookmarkOrphanList.add(bookmarkDecSupvLast);
				BookmarkDto bookmarkDecSupvMiddle = createBookmark(BookmarkConstants.DEC_SUPV_NAME_MIDDLE,
						approvalDto.getNmNameMiddle());
				bookmarkOrphanList.add(bookmarkDecSupvMiddle);

				if(!ObjectUtils.isEmpty(approvalDto.getDtApproversDetermination())) {
				BookmarkDto bookmarkDecSupvTime = createBookmark(BookmarkConstants.DEC_SUPV_TIME,
						DateUtils.getTime(approvalDto.getDtApproversDetermination()));
				bookmarkOrphanList.add(bookmarkDecSupvTime);
				}
			}
		}

		// bookmarks only in cfin0800
		else if ("cfin0800".equals(prefillDto.getFormName())) {
			// CINT67D
			for (WorkerInfoDto workerDto : prefillDto.getWorkerInfoList()) {

				if(!ObjectUtils.isEmpty(workerDto.getDtEventOccurred())) {
				BookmarkDto bookmarkDecSupvDate = createBookmark(BookmarkConstants.DEC_SUPV_DATE,
						DateUtils.stringDt(workerDto.getDtEventOccurred()));
				bookmarkOrphanList.add(bookmarkDecSupvDate);
				}

				BookmarkDto bookmarkDecSupvCity = createBookmark(BookmarkConstants.DEC_SUPV_CITY,
						workerDto.getAddrMailCodeCity());
				bookmarkOrphanList.add(bookmarkDecSupvCity);
				BookmarkDto bookmarkDecSupvSuffix = createBookmarkWithCodesTable(BookmarkConstants.DEC_SUPV_NAME_SUFFIX,
						workerDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				bookmarkOrphanList.add(bookmarkDecSupvSuffix);
				BookmarkDto bookmarkDecSupvFirst = createBookmark(BookmarkConstants.DEC_SUPV_NAME_FIRST,
						workerDto.getNmNameFirst());
				bookmarkOrphanList.add(bookmarkDecSupvFirst);
				BookmarkDto bookmarkDecSupvLast = createBookmark(BookmarkConstants.DEC_SUPV_NAME_LAST,
						workerDto.getNmNameLast());
				bookmarkOrphanList.add(bookmarkDecSupvLast);
				BookmarkDto bookmarkDecSupvMiddle = createBookmark(BookmarkConstants.DEC_SUPV_NAME_MIDDLE,
						workerDto.getNmNameMiddle());
				bookmarkOrphanList.add(bookmarkDecSupvMiddle);

				if(!ObjectUtils.isEmpty(workerDto.getDtEventOccurred())) {
				BookmarkDto bookmarkDecSupvTime = createBookmark(BookmarkConstants.DEC_SUPV_TIME,
						DateUtils.getTime(workerDto.getDtEventOccurred()));
				bookmarkOrphanList.add(bookmarkDecSupvTime);
				}
			}
		}

		// CINT09D
		BookmarkDto bookmarkFacName = createBookmark(BookmarkConstants.FAC_NAME,
				prefillDto.getIncomingFacilityDto().getNmIncmgFacilName());
		bookmarkOrphanList.add(bookmarkFacName);
		BookmarkDto bookmarkFacAddrCity = createBookmark(BookmarkConstants.FAC_ADDR_CITY,
				prefillDto.getIncomingFacilityDto().getAddrIncmgFacilCity());
		bookmarkOrphanList.add(bookmarkFacAddrCity);
		BookmarkDto bookmarkFacAddrLn1 = createBookmark(BookmarkConstants.FAC_ADDR_LN_1,
				prefillDto.getIncomingFacilityDto().getAddrIncmgFacilStLn1());
		bookmarkOrphanList.add(bookmarkFacAddrLn1);
		BookmarkDto bookmarkFacAddrLn2 = createBookmark(BookmarkConstants.FAC_ADDR_LN_2,
				prefillDto.getIncomingFacilityDto().getAddrIncmgFacilStLn2());
		bookmarkOrphanList.add(bookmarkFacAddrLn2);
		BookmarkDto bookmarkFacAddrZip = createBookmark(BookmarkConstants.FAC_ADDR_ZIP,
				prefillDto.getIncomingFacilityDto().getAddrIncmgFacilZip());
		bookmarkOrphanList.add(bookmarkFacAddrZip);
		BookmarkDto bookmarkFacAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.FAC_ADDR_COUNTY,
				prefillDto.getIncomingFacilityDto().getCdIncmgFacilCnty(), CodesConstant.CCOUNT);
		bookmarkOrphanList.add(bookmarkFacAddrCounty);
		BookmarkDto bookmarkFacAddrState = createBookmark(BookmarkConstants.FAC_ADDR_STATE,
				prefillDto.getIncomingFacilityDto().getCdIncmgFacilState());
		bookmarkOrphanList.add(bookmarkFacAddrState);
		BookmarkDto bookmarkFacType = createBookmarkWithCodesTable(BookmarkConstants.FAC_TYPE,
				prefillDto.getIncomingFacilityDto().getCdIncmgFacilType(), CodesConstant.CFACTYP2);
		bookmarkOrphanList.add(bookmarkFacType);
		
		if(!ObjectUtils.isEmpty(prefillDto.getIncomingFacilityDto().getNbrIncmgFacilPhone())) {
		BookmarkDto bookmarkFacPhone = createBookmark(BookmarkConstants.FAC_PHON,
				TypeConvUtil.formatPhone(prefillDto.getIncomingFacilityDto().getNbrIncmgFacilPhone()));
		bookmarkOrphanList.add(bookmarkFacPhone);
		}
		
		BookmarkDto bookmarkFacPhoneExt = createBookmark(BookmarkConstants.FAC_PHON_EXT,
				prefillDto.getIncomingFacilityDto().getNbrIncmgFacilPhoneExt());
		bookmarkOrphanList.add(bookmarkFacPhoneExt);
		BookmarkDto bookmarkFacAffiliated = createBookmark(BookmarkConstants.FAC_AFFILIATED,
				prefillDto.getIncomingFacilityDto().getNmIncmgFacilAffiliated());
		bookmarkOrphanList.add(bookmarkFacAffiliated);
		BookmarkDto bookmarkFacSuper = createBookmark(BookmarkConstants.FAC_SUPERINTENDENT,
				prefillDto.getIncomingFacilityDto().getNmIncmgFacilSuprtdant());
		bookmarkOrphanList.add(bookmarkFacSuper);
		BookmarkDto bookmarkFacUnitWard = createBookmark(BookmarkConstants.FAC_UNIT_WARD,
				prefillDto.getIncomingFacilityDto().getNmUnitWard());
		bookmarkOrphanList.add(bookmarkFacUnitWard);
		BookmarkDto bookmarkFacSpclInst = createBookmark(BookmarkConstants.FAC_SPCL_INST,
				prefillDto.getIncomingFacilityDto().getTxtFacilCmnts());
		bookmarkOrphanList.add(bookmarkFacSpclInst);

		// CINT65D
		BookmarkDto bookmarkCallNarrWs = createBookmark(BookmarkConstants.CALL_NARR_WORKER_SAFETY,
				prefillDto.getIncomingStageDetailsDto().getTxtIncmgWorkerSafety());
		bookmarkOrphanList.add(bookmarkCallNarrWs);
		BookmarkDto bookmarkCallNarrSensitiveIssue = createBookmark(BookmarkConstants.CALL_NARR_SENSITIVE_ISSUE,
				prefillDto.getIncomingStageDetailsDto().getTxtIncmgSensitive());
		bookmarkOrphanList.add(bookmarkCallNarrSensitiveIssue);
		BookmarkDto bookmarkSumSensitiveIssue = createBookmark(BookmarkConstants.SUM_SENSITIVE_ISSUE,
				prefillDto.getIncomingStageDetailsDto().getIndIncmgSensitive());
		bookmarkOrphanList.add(bookmarkSumSensitiveIssue);
		BookmarkDto bookmarkSumWsIssues = createBookmark(BookmarkConstants.SUM_WORKER_SAFETY_ISSUES,
				prefillDto.getIncomingStageDetailsDto().getIndIncmgWorkerSafety());
		bookmarkOrphanList.add(bookmarkSumWsIssues);
		
		if(!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
		BookmarkDto bookmarkSumDateReported = createBookmark(BookmarkConstants.SUM_DATE_RPTED,
				DateUtils.stringDt(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall()));
		bookmarkOrphanList.add(bookmarkSumDateReported);
		}
		
		BookmarkDto bookmarkSumWorkerExt = createBookmark(BookmarkConstants.SUM_WORKER_EXTENSION,
				prefillDto.getIncomingStageDetailsDto().getIncmgWorkerExt());
		bookmarkOrphanList.add(bookmarkSumWorkerExt);
		
		if(!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getNbrIncmgWorkerPhone())) {
		BookmarkDto bookmarkSumWorkerPhone = createBookmark(BookmarkConstants.SUM_WORKER_PHONE,
				TypeConvUtil.formatPhone(prefillDto.getIncomingStageDetailsDto().getNbrIncmgWorkerPhone()));
		bookmarkOrphanList.add(bookmarkSumWorkerPhone);
		}
		
		
		BookmarkDto bookmarkSumSpclHandling = createBookmarkWithCodesTable(BookmarkConstants.SUM_SPCL_HANDLING,
				prefillDto.getIncomingStageDetailsDto().getCdIncmgSpecHandling(), CodesConstant.CSPECHND);
		bookmarkOrphanList.add(bookmarkSumSpclHandling);
		BookmarkDto bookmarkSumPriorityDeterm = createBookmarkWithCodesTable(BookmarkConstants.SUM_PRIORITY_DETERM,
				prefillDto.getIncomingStageDetailsDto().getCdStageCurrPriority(), CodesConstant.CPRIORTY);
		bookmarkOrphanList.add(bookmarkSumPriorityDeterm);
		BookmarkDto bookmarkSumWorkerTakingIntake = createBookmark(BookmarkConstants.SUM_WORKER_TAKING_INTAKE,
				prefillDto.getIncomingStageDetailsDto().getNmIncmgWorkerName());
		bookmarkOrphanList.add(bookmarkSumWorkerTakingIntake);
		BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				prefillDto.getIncomingStageDetailsDto().getNmStage());
		bookmarkOrphanList.add(bookmarkTitleCaseName);
		
		if(!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
		BookmarkDto bookmarkSumTimeRpted = createBookmark(BookmarkConstants.SUM_TIME_RPTED,
				DateUtils.getTime(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall()));
		bookmarkOrphanList.add(bookmarkSumTimeRpted);
		}
		BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				prefillDto.getIncomingStageDetailsDto().getIdCase());
		bookmarkOrphanList.add(bookmarkTitleCaseNumber);
		BookmarkDto bookmarkSumIntakeNum = createBookmark(BookmarkConstants.SUM_INTAKE_NUM,
				prefillDto.getIncomingStageDetailsDto().getIdStage());
		bookmarkOrphanList.add(bookmarkSumIntakeNum);
		bookmarkOrphanList.add( createBookmark(BookmarkConstants.SUM_RELATED_REPORTS,
				prefillDto.getIncomingStageDetailsDto().getTxtRelatedCalls())); // artf169810

		// artf251139 for cfin0200 only, SUM_CHILDDEATH indicator attaches to the label subgroup
		if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getIndChildDeath())) {
			FormDataGroupDto indChlDthGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SUM_CHILDDEATH, FormConstants.EMPTY_STRING);
			formDataGroupList.add(indChlDthGroupDto);
			if ("cfin0200".equals(prefillDto.getFormName())) {
				indChlDthGroupDto.setBookmarkDtoList(Arrays.asList(
						createBookmark(BookmarkConstants.SUM_CHILDDEATH, prefillDto.getIncomingStageDetailsDto().getIndChildDeath())
				));
			} else {
				// if not cfin0200, indicator value is populated outside of the child death indicator label group
				bookmarkOrphanList.add( createBookmark(BookmarkConstants.SUM_CHILDDEATH, prefillDto.getIncomingStageDetailsDto().getIndChildDeath()));
			}
		}

		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		// Blobs
		prefillData.setBlobDataDtoList( new ArrayList<>(Arrays.asList(createBlobData(BookmarkConstants.CALL_NARR_BLOB,
				ServiceConstants.INCOMING_NARRATIVE_VIEW,  prefillDto.getIncomingStageDetailsDto().getIdStage().intValue()))));
		// Groups
		prefillData.setFormDataGroupList(formDataGroupList);
		// top level bookmarks
		prefillData.setBookmarkDtoList(bookmarkOrphanList);

		return prefillData;
	}
}
