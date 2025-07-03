package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.IncmgDetermFactors;
import us.tx.state.dfps.common.dto.*;
import us.tx.state.dfps.service.apsinhomeintakereport.dto.ApsInHomeIntakeReportDto;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityOutputDto;
import us.tx.state.dfps.service.common.*;
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
 * prefill string for APS Inhome Intake Report Nov 02, 2021- 3:43:13 PM 2017 Texas
 * Department of Family and Protective Services
 * * **********  Change History *********************************
 * 02/08/2022 rayanv artf204701 : Intake Report Adult Protective Services  CFIN0300,CFIN0700 - initial.
 * 10/05/2023 thompswa artf251139 : add Child Death Indicator, apply artf178638 populatePersonListInfo refactor
 */
@Component
public class ApsInHomeIntakeReportPrefillData extends DocumentServiceUtil {

    @Autowired
    CpsIntakeReportFormPrefillData intakePersonPrefill;

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        ApsInHomeIntakeReportDto prefillDto = (ApsInHomeIntakeReportDto) parentDtoobj;
        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();

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

        // For Allegation Detail CFIN0300
        if (prefillDto.getIntakeAllegationList() != null) {
            for (IntakeAllegationDto intakeAllegationDto : prefillDto.getIntakeAllegationList()) {
                FormDataGroupDto allegationGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkAllegationList = new ArrayList<>();
                BookmarkDto bookmarkAllegDtlDurtn = createBookmark(BookmarkConstants.ALLEG_DTL_DURTN,
                        intakeAllegationDto.getIntakeAllegDuration());
                bookmarkAllegationList.add(bookmarkAllegDtlDurtn);
                BookmarkDto bookmarkAllegDtlAlleg = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG,
                        intakeAllegationDto.getCdIntakeAllegType(), CodesConstant.CAPSALLG);
                bookmarkAllegationList.add(bookmarkAllegDtlAlleg);
                BookmarkDto bookmarkAllegDtlAp = createBookmark(BookmarkConstants.ALLEG_DTL_AP,
                        intakeAllegationDto.getNmPerpetrator());
                bookmarkAllegationList.add(bookmarkAllegDtlAp);
                BookmarkDto bookmarkAllegDtlVictim = createBookmark(BookmarkConstants.ALLEG_DTL_VICTIM,
                        intakeAllegationDto.getNmVictim());
                bookmarkAllegationList.add(bookmarkAllegDtlVictim);
                allegationGroupDto.setBookmarkDtoList(bookmarkAllegationList);
                formDataGroupList.add(allegationGroupDto);
            }
        }


        // parent group cfzco00
        if (prefillDto.getApprovalList() != null) {
            for (ApprovalDto approvalDto : prefillDto.getApprovalList()) {
                if (StringUtils.isNotBlank(approvalDto.getCdNameSuffix())) {
                    FormDataGroupDto comma2GroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
                            FormConstants.EMPTY_STRING);
                    formDataGroupList.add(comma2GroupDto);
                }
            }
        }

        // Determination Factors
        if (prefillDto.getIncmgDetermFactorsList() != null) {
            for (IncmgDetermFactors incomingDetFactor : prefillDto.getIncmgDetermFactorsList()) {
                FormDataGroupDto summarySectionFactors = createFormDataGroup(FormGroupsConstants.TMPLAT_DET_FACTR, FormConstants.EMPTY_STRING);
                formDataGroupList.add(summarySectionFactors);
                summarySectionFactors.setBookmarkDtoList(Arrays.asList(
                        createBookmarkWithCodesTable(BookmarkConstants.DETERM_FACTR, incomingDetFactor.getCdIncmgDeterm(), CodesConstant.CADETFCT)
                ));
            }
        }

        // Principal and Collateral Information - artf178638 refactor start
        intakePersonPrefill.populatePersonListInfo(formDataGroupList, intakeReportFormDto, ServiceConstants.VICTIM_TYPE); // parent group cfin0301
        intakePersonPrefill.populatePersonListInfo(formDataGroupList, intakeReportFormDto, ServiceConstants.PERPETRATOR_TYPE); // parent group cfin0102
        intakePersonPrefill.populatePersonListInfo(formDataGroupList, intakeReportFormDto, ServiceConstants.OTHER_PRN_TYPE); // parent group cfin0103
        /** do not get the reporter for the de-identified version cfin0700 */
        if ( ! ServiceConstants.CFIN0700.equals(prefillDto.getFormName())) {
            intakePersonPrefill.populatePersonListInfo(formDataGroupList, intakeReportFormDto, ServiceConstants.REPORTER_TYPE); //  parent group cfin0104
        }
        intakePersonPrefill.populatePersonListInfo(formDataGroupList, intakeReportFormDto, ServiceConstants.COLLATERAL_TYPE); // artf178638 refactor end

        /**
         * Description: Populating the group cfin03ws,cfin07ws
         * GroupName: cfin03ws,cfin07ws
         * BookMark: TMPLAT_WS
         * Condition: bIndIncmgWorkerSafety(CINT65D) = Y
         */
        if (ServiceConstants.Y.equalsIgnoreCase(prefillDto.getIncomingStageDetailsDto().getIndIncmgWorkerSafety())) {
            FormDataGroupDto wsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WS, FormConstants.EMPTY_STRING);
            formDataGroupList.add(wsGroupDto);
            wsGroupDto.setBookmarkDtoList(Arrays.asList(
                    createBookmark(BookmarkConstants.WS_TEXT, prefillDto.getIncomingStageDetailsDto().getTxtIncmgWorkerSafety())
            ));
        }

        // parent groups cfzco00
        if (prefillDto.getWorkerInfoList() != null) {
            for (WorkerInfoDto workerInfoDto : prefillDto.getWorkerInfoList()) {
                if (StringUtils.isNotBlank(workerInfoDto.getCdNameSuffix())) {
                    FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
                            FormConstants.EMPTY_STRING);
                    formDataGroupList.add(commaGroupDto);
                }
            }
        }

        // Non-group bookmarks
        List<BookmarkDto> bookmarkOrphanList = new ArrayList<>();

        // CINT65D
        BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
                prefillDto.getIncomingStageDetailsDto().getNmStage());
        bookmarkOrphanList.add(bookmarkTitleCaseName);

        BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
                prefillDto.getIncomingStageDetailsDto().getIdCase());
        bookmarkOrphanList.add(bookmarkTitleCaseNumber);

        if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtEventOccurred())) {
            BookmarkDto bookmarkSumLeNotifyDate = createBookmark(BookmarkConstants.SUM_LE_NOTIFY_DATE,
                    DateUtils.stringDt(prefillDto.getIncomingStageDetailsDto().getDtEventOccurred()));
            bookmarkOrphanList.add(bookmarkSumLeNotifyDate);
        }

        BookmarkDto bookmarkSumWorkerCity = createBookmark(BookmarkConstants.SUM_WORKER_CITY,
                prefillDto.getIncomingStageDetailsDto().getAddrIncmgWorkerCity());
        bookmarkOrphanList.add(bookmarkSumWorkerCity);


        // Deciaiona/Recommendations- Recorded Call

        BookmarkDto bookmarkHisRecordedCall = createBookmark(BookmarkConstants.HIS_RECORDED_CALL,
                prefillDto.getIncomingStageDetailsDto().getNmIncmgWorkerName());
        bookmarkOrphanList.add(bookmarkHisRecordedCall);

        if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
            BookmarkDto bookmarkHisRecordedCallDate = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_DATE,
                    DateUtils.stringDt(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall()));
            bookmarkOrphanList.add(bookmarkHisRecordedCallDate);
        }
        BookmarkDto bookmarkHisRecordedCallExt = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_EXT,
                prefillDto.getIncomingStageDetailsDto().getIncmgWorkerExt());
        bookmarkOrphanList.add(bookmarkHisRecordedCallExt);

        BookmarkDto bookmarkHisRecordedCallCity = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_CITY,
                prefillDto.getIncomingStageDetailsDto().getAddrIncmgWorkerCity());
        bookmarkOrphanList.add(bookmarkHisRecordedCallCity);
        BookmarkDto bookmarkHisRecordedCallBjn = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_BJN,
                prefillDto.getIncomingStageDetailsDto().getCdEmpBjnEmp());
        bookmarkOrphanList.add(bookmarkHisRecordedCallBjn);

        if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getNbrIncmgWorkerPhone())) {
            BookmarkDto bookmarkHisRecordedCallPhone = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_PHONE,
                    TypeConvUtil.formatPhone(prefillDto.getIncomingStageDetailsDto().getNbrIncmgWorkerPhone()));
            bookmarkOrphanList.add(bookmarkHisRecordedCallPhone);
        }
        if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
            BookmarkDto bookmarkHisRecordedCallTime = createBookmark(BookmarkConstants.HIS_RECORDED_CALL_TIME,
                    DateUtils.getTime(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall()));
            bookmarkOrphanList.add(bookmarkHisRecordedCallTime);
        }

        // Deciaiona/Recommendations- Initial Priority

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
        BookmarkDto bookmarkSumLeJuris = createBookmark(BookmarkConstants.SUM_LE_JURIS,
                prefillDto.getIncomingStageDetailsDto().getNmIncmgJurisdiction());
        bookmarkOrphanList.add(bookmarkSumLeJuris);

        // CINT67D
        if (prefillDto.getWorkerInfoList() != null) {
            for (WorkerInfoDto workerDto : prefillDto.getWorkerInfoList()) {
                if (!ObjectUtils.isEmpty(workerDto.getNbrPersonPhone())) {
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

                if (!ObjectUtils.isEmpty(workerDto.getDtEventModified())) {
                    BookmarkDto bookmarkHisStageChangeTime = createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_TIME,
                            DateUtils.getTime(workerDto.getDtEventModified()));
                    bookmarkOrphanList.add(bookmarkHisStageChangeTime);
                }

                if (!ObjectUtils.isEmpty(workerDto.getDtEventModified())) {
                    BookmarkDto bookmarkHisStageChangeDate = createBookmark(BookmarkConstants.HIS_STAGE_CHANGE_DATE,
                            DateUtils.stringDt(workerDto.getDtEventModified()));
                    bookmarkOrphanList.add(bookmarkHisStageChangeDate);
                }
            }
        }

        // CLSC52D
        //Deciaiona/Recommendations - Approved
        if (prefillDto.getApprovalList() != null) {
            for (ApprovalDto approvalDto : prefillDto.getApprovalList()) {

                if (!ObjectUtils.isEmpty(approvalDto.getDtApproversDetermination())) {
                    BookmarkDto bookmarkHisApprovedDate = createBookmark(BookmarkConstants.HIS_APPROVED_DATE,
                            DateUtils.stringDt(approvalDto.getDtApproversDetermination()));
                    bookmarkOrphanList.add(bookmarkHisApprovedDate);
                }

                if (!ObjectUtils.isEmpty(approvalDto.getNbrPersonPhone())) {
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

                if (!ObjectUtils.isEmpty(approvalDto.getDtApproversDetermination())) {
                    BookmarkDto bookmarkHisApprovedTime = createBookmark(BookmarkConstants.HIS_APPROVED_TIME,
                            DateUtils.getTime(approvalDto.getDtApproversDetermination()));
                    bookmarkOrphanList.add(bookmarkHisApprovedTime);
                }
            }
        }

        // CINT65D
        if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
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

        if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
            BookmarkDto bookmarkDecIntTime = createBookmark(BookmarkConstants.DEC_INT_TIME,
                    DateUtils.getTime(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall()));
            bookmarkOrphanList.add(bookmarkDecIntTime);
        }

        // CLSC52D
        if (prefillDto.getApprovalList() != null) {
            for (ApprovalDto approvalDto : prefillDto.getApprovalList()) {
                if (!ObjectUtils.isEmpty(approvalDto.getDtApproversDetermination())) {
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

                if (!ObjectUtils.isEmpty(approvalDto.getDtApproversDetermination())) {
                    BookmarkDto bookmarkDecSupvTime = createBookmark(BookmarkConstants.DEC_SUPV_TIME,
                            DateUtils.getTime(approvalDto.getDtApproversDetermination()));
                    bookmarkOrphanList.add(bookmarkDecSupvTime);
                }
            }
        }

        // bookmarks only in cfin0700
        if (!("cfin0700".equals(prefillDto.getFormName())) && (prefillDto.getWorkerInfoList() != null)) {
            // CINT67D
            for (WorkerInfoDto workerDto : prefillDto.getWorkerInfoList()) {

                if (!ObjectUtils.isEmpty(workerDto.getDtEventOccurred())) {
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

                if (!ObjectUtils.isEmpty(workerDto.getDtEventOccurred())) {
                    BookmarkDto bookmarkDecSupvTime = createBookmark(BookmarkConstants.DEC_SUPV_TIME,
                            DateUtils.getTime(workerDto.getDtEventOccurred()));
                    bookmarkOrphanList.add(bookmarkDecSupvTime);
                }
            }
        }


        //Call Narrative
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

        if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
            BookmarkDto bookmarkSumDateReported = createBookmark(BookmarkConstants.SUM_DATE_RPTED,
                    DateUtils.stringDt(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall()));
            bookmarkOrphanList.add(bookmarkSumDateReported);
        }

        if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getNbrIncmgWorkerPhone())) {
            BookmarkDto bookmarkSumWorkerPhone = createBookmark(BookmarkConstants.SUM_WORKER_PHONE,
                    TypeConvUtil.formatPhone(prefillDto.getIncomingStageDetailsDto().getNbrIncmgWorkerPhone()));
            bookmarkOrphanList.add(bookmarkSumWorkerPhone);
        }
        BookmarkDto bookmarkSumWorkerExt = createBookmark(BookmarkConstants.SUM_WORKER_EXTENSION,
                prefillDto.getIncomingStageDetailsDto().getIncmgWorkerExt());
        bookmarkOrphanList.add(bookmarkSumWorkerExt);

        BookmarkDto bookmarkSumSpclHandling = createBookmarkWithCodesTable(BookmarkConstants.SUM_SPCL_HANDLING,
                prefillDto.getIncomingStageDetailsDto().getCdIncmgSpecHandling(), CodesConstant.CSPECHND);
        bookmarkOrphanList.add(bookmarkSumSpclHandling);
        BookmarkDto bookmarkSumPriorityDeterm = createBookmarkWithCodesTable(BookmarkConstants.SUM_PRIORITY_DETERM,
                prefillDto.getIncomingStageDetailsDto().getCdStageCurrPriority(), CodesConstant.CPRIORTY);
        bookmarkOrphanList.add(bookmarkSumPriorityDeterm);
        BookmarkDto bookmarkSumWorkerTakingIntake = createBookmark(BookmarkConstants.SUM_WORKER_TAKING_INTAKE,
                prefillDto.getIncomingStageDetailsDto().getNmIncmgWorkerName());
        bookmarkOrphanList.add(bookmarkSumWorkerTakingIntake);


        if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall())) {
            BookmarkDto bookmarkSumTimeRpted = createBookmark(BookmarkConstants.SUM_TIME_RPTED,
                    DateUtils.getTime(prefillDto.getIncomingStageDetailsDto().getDtIncomingCall()));
            bookmarkOrphanList.add(bookmarkSumTimeRpted);
        }

        BookmarkDto bookmarkSumIntakeNum = createBookmark(BookmarkConstants.SUM_INTAKE_NUM,
                prefillDto.getIncomingStageDetailsDto().getIdStage());
        bookmarkOrphanList.add(bookmarkSumIntakeNum);
        bookmarkOrphanList.add(createBookmark(BookmarkConstants.SUM_RELATED_REPORTS,
                prefillDto.getIncomingStageDetailsDto().getTxtRelatedCalls())); // artf169810

		// artf251139 group for child death indicator label for all intakes after 9/1/2023 (by datafix)
		if (!ObjectUtils.isEmpty(prefillDto.getIncomingStageDetailsDto().getIndChildDeath())) {
			FormDataGroupDto indChlDthGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SUM_CHILDDEATH, FormConstants.EMPTY_STRING);
			formDataGroupList.add(indChlDthGroupDto);
            // indicator value is populated outside of the child death indicator label group
			bookmarkOrphanList.add( createBookmark(BookmarkConstants.SUM_CHILDDEATH, prefillDto.getIncomingStageDetailsDto().getIndChildDeath()));
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
