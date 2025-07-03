package us.tx.state.dfps.service.forms.util;


import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.AdminReviewFindings.dto.AdminReviewFindingsDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageReviewDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@Component
public class AdminReviewFindingsPrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {


        AdminReviewFindingsDto adminReviewFindingsDto = (AdminReviewFindingsDto) parentDtoobj;

        List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
        List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

        // Initialize null DTOs

        if (ObjectUtils.isEmpty(adminReviewFindingsDto.getAdminReviewDto())) {
            adminReviewFindingsDto.setAdminReviewDto(new AdminReviewDto());
        }
        if (ObjectUtils.isEmpty(adminReviewFindingsDto.getStageReviewDtolist())) {
            adminReviewFindingsDto.setStageReviewDtolist(new ArrayList<StageReviewDto>());
        }
        if (ObjectUtils.isEmpty(adminReviewFindingsDto.getStageNotReviewDto())) {
            adminReviewFindingsDto.setStageNotReviewDto(new ArrayList<StageReviewDto>());
        }

        if (ObjectUtils.isEmpty(adminReviewFindingsDto.getCaseInfoDto())) {
            adminReviewFindingsDto.setCaseInfoDto(new CaseInfoDto());
        }

        if (ObjectUtils.isEmpty(adminReviewFindingsDto.getEmpWorkerDto())) {
            adminReviewFindingsDto.setEmpWorkerDto(new EmployeePersPhNameDto());
        }
        if (ObjectUtils.isEmpty(adminReviewFindingsDto.getPersonDto())) {
            adminReviewFindingsDto.setPersonDto(new PersonDto());
        }
        if (ObjectUtils.isEmpty(adminReviewFindingsDto.getStagePersonLinkRecordOutDto())) {
            adminReviewFindingsDto.setStagePersonLinkRecordOutDto(new StagePersonLinkRecordOutDto());
        }

        if (ObjectUtils.isEmpty(adminReviewFindingsDto.getStageCaseDtlDto())) {
            adminReviewFindingsDto.setStageCaseDtlDto(new StageCaseDtlDto());

        }

        //CCMNC5D
        BookmarkDto bookmarkTitleCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
                adminReviewFindingsDto.getStageCaseDtlDto().getNmCase());
        bookmarkNonFrmGrpList.add(bookmarkTitleCaseNm);

        BookmarkDto bookmarkTitleCaseId = createBookmark(BookmarkConstants.TITLE_CASE_ID,
                adminReviewFindingsDto.getStageCaseDtlDto().getIdCase());
        bookmarkNonFrmGrpList.add(bookmarkTitleCaseId);

        BookmarkDto bookmarkFormCaseName = createBookmark(BookmarkConstants.FORM_CASE_NAME,
                adminReviewFindingsDto.getStageCaseDtlDto().getNmCase());
        bookmarkNonFrmGrpList.add(bookmarkFormCaseName);

        BookmarkDto bookmarkFormCaseId = createBookmark(BookmarkConstants.FORM_CASE_ID,
                adminReviewFindingsDto.getStageCaseDtlDto().getIdCase());
        bookmarkNonFrmGrpList.add(bookmarkFormCaseId);

        //CINT21D
        BookmarkDto bookmarkStageRetrieved = createBookmarkWithCodesTable(BookmarkConstants.STAGE_RETRIEVED,
                adminReviewFindingsDto.getStageCaseDtlDto().getCdStage(), CodesConstant.CSTAGES);
        bookmarkNonFrmGrpList.add(bookmarkStageRetrieved);

        //CSEC01D
        BookmarkDto bookmarkPrimaryNmFirst = createBookmark(BookmarkConstants.PRIMARY_NAME_FIRST,
                adminReviewFindingsDto.getEmployeePersPhNameDto().getNmNameFirst());
        bookmarkNonFrmGrpList.add(bookmarkPrimaryNmFirst);

        BookmarkDto bookmarkPrimaryNmMiddle = createBookmark(BookmarkConstants.PRIMARY_NAME_MIDDLE,
                adminReviewFindingsDto.getEmployeePersPhNameDto().getNmNameMiddle());
        bookmarkNonFrmGrpList.add(bookmarkPrimaryNmMiddle);

        BookmarkDto bookmarkPrimaryNmLast = createBookmark(BookmarkConstants.PRIMARY_NAME_LAST,
                adminReviewFindingsDto.getEmployeePersPhNameDto().getNmNameLast());
        bookmarkNonFrmGrpList.add(bookmarkPrimaryNmLast);


        //TMPLAT_COMMA_B
        if (!ObjectUtils.isEmpty(adminReviewFindingsDto.getEmployeePersPhNameDto()) && !ObjectUtils.isEmpty(adminReviewFindingsDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
            formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_B,
                    adminReviewFindingsDto.getEmployeePersPhNameDto().getCdNameSuffix()));
        }

        BookmarkDto bookmarkPrimaryNmSuffix = createBookmark(BookmarkConstants.PRIMARY_NAME_SUFFIX,
                adminReviewFindingsDto.getEmployeePersPhNameDto().getCdNameSuffix());
        bookmarkNonFrmGrpList.add(bookmarkPrimaryNmSuffix);


        //CINT21D
        if (!ObjectUtils.isEmpty(adminReviewFindingsDto.getStageCaseDtlDto())) {
            FormDataGroupDto stageNameGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_STAGE_NAME_RETRIEVED,
                    FormConstants.EMPTY_STRING);
            stageNameGroup.setBookmarkDtoList(Collections.singletonList(createBookmark(BookmarkConstants.STAGE_NAME_RETRIEVED,
                    adminReviewFindingsDto.getStageCaseDtlDto().getNmStage())));
            formDataGroupList.add(stageNameGroup);
        }

        //	CSEC35D

        if(!ObjectUtils.isEmpty(adminReviewFindingsDto.getNameDetailDto())) {
            BookmarkDto bookmarkRequestorNameFirst = createBookmark(BookmarkConstants.REQUESTOR_NAME_FIRST,
                    adminReviewFindingsDto.getNameDetailDto().getNmNameFirst());
            bookmarkNonFrmGrpList.add(bookmarkRequestorNameFirst);

            BookmarkDto bookmarkRequestorNameMiddle = createBookmark(BookmarkConstants.REQUESTOR_NAME_MIDDLE,
                    adminReviewFindingsDto.getNameDetailDto().getNmNameMiddle());
            bookmarkNonFrmGrpList.add(bookmarkRequestorNameMiddle);

            BookmarkDto bookmarkRequestorNameLast = createBookmark(BookmarkConstants.REQUESTOR_NAME_LAST,
                    adminReviewFindingsDto.getNameDetailDto().getNmNameLast());
            bookmarkNonFrmGrpList.add(bookmarkRequestorNameLast);

            formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_D,
                    adminReviewFindingsDto.getNameDetailDto().getCdNameSuffix()));

            BookmarkDto bookmarkRequestorNameSuffix = createBookmark(BookmarkConstants.REQUESTOR_NAME_SUFFIX,
                    adminReviewFindingsDto.getNameDetailDto().getCdNameSuffix());
            bookmarkNonFrmGrpList.add(bookmarkRequestorNameSuffix);

        }

        //CINV39D
        BookmarkDto bookmarkRequestorRole = createBookmarkWithCodesTable(BookmarkConstants.REQUESTOR_ROLE,
                adminReviewFindingsDto.getStagePersonLinkRecordOutDto().getCdStagePersRole(), CodesConstant.CINVROLE);
        bookmarkNonFrmGrpList.add(bookmarkRequestorRole);


        BookmarkDto bookmarkConductedByNameFirst = createBookmark(BookmarkConstants.CONDUCTED_BY_NAME_FIRST,
                adminReviewFindingsDto.getEmployeePersPhNameDto().getNmNameFirst());
        bookmarkNonFrmGrpList.add(bookmarkConductedByNameFirst);


        BookmarkDto bookmarkConductedByNameMiddle = createBookmark(BookmarkConstants.CONDUCTED_BY_NAME_MIDDLE,
                adminReviewFindingsDto.getEmployeePersPhNameDto().getNmNameMiddle());
        bookmarkNonFrmGrpList.add(bookmarkConductedByNameMiddle);

        BookmarkDto bookmarkConductedByNameLast = createBookmark(BookmarkConstants.CONDUCTED_BY_NAME_LAST,
                adminReviewFindingsDto.getEmployeePersPhNameDto().getNmNameLast());
        bookmarkNonFrmGrpList.add(bookmarkConductedByNameLast);


        BookmarkDto bookmarkReviewerTitle = createBookmark(BookmarkConstants.REVIEWER_TITLE,
                adminReviewFindingsDto.getEmployeePersPhNameDto().getTxtEmployeeClass());
        bookmarkNonFrmGrpList.add(bookmarkReviewerTitle);


        // TMPLAT_COMMA_C
        if (!ObjectUtils.isEmpty(adminReviewFindingsDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
            formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_C,
                    adminReviewFindingsDto.getEmployeePersPhNameDto().getCdNameSuffix()));
        }

        BookmarkDto bookmarkConductedByNameSuffix = createBookmark(BookmarkConstants.CONDUCTED_BY_NAME_SUFFIX,
                adminReviewFindingsDto.getEmployeePersPhNameDto().getCdNameSuffix());
        bookmarkNonFrmGrpList.add(bookmarkConductedByNameSuffix);


        // CSES65D
        BookmarkDto bookmarkTypeOfAppealReview = createBookmarkWithCodesTable(BookmarkConstants.TYPE_OF_APPEAL_REVIEW,
                adminReviewFindingsDto.getAdminReviewDto().getCdAdminRvAppealType(), CodesConstant.CARVTYPE);
        bookmarkNonFrmGrpList.add(bookmarkTypeOfAppealReview);

        BookmarkDto bookmarkStatusOfAppealReview = createBookmarkWithCodesTable(BookmarkConstants.STATUS_OF_APPEAL_REVIEW,
                adminReviewFindingsDto.getAdminReviewDto().getCdAdminRvStatus(), CodesConstant.CARVSTAT);
        bookmarkNonFrmGrpList.add(bookmarkStatusOfAppealReview);

        BookmarkDto bookmarkAuthorized = createBookmarkWithCodesTable(BookmarkConstants.AUTHORIZED,
                adminReviewFindingsDto.getAdminReviewDto().getCdAdminRvAuth(), CodesConstant.CARVAUTH);
        bookmarkNonFrmGrpList.add(bookmarkAuthorized);

        BookmarkDto bookmarkRequestedByCode = createBookmarkWithCodesTable(BookmarkConstants.REQUESTED_BY_CODE,
                adminReviewFindingsDto.getAdminReviewDto().getCdAdminRvReqBy(), CodesConstant.CARREQBY);
        bookmarkNonFrmGrpList.add(bookmarkRequestedByCode);

        //check once in which DAMS
        BookmarkDto bookmarkRequestedByName = createBookmark(BookmarkConstants.REQUESTED_BY_NAME,
                adminReviewFindingsDto.getAdminReviewDto().getNmAdminRvReqBy());
        bookmarkNonFrmGrpList.add(bookmarkRequestedByName);

        List<BlobDataDto> blobDataDtoList = new ArrayList<>();
        BlobDataDto blobBookmark = createBlobData(BookmarkConstants.ADMIN_REVIEW_NARR,
                BookmarkConstants.ADMIN_REVIEW_NARR, adminReviewFindingsDto.getAdminReviewDto().getIdEvent().toString());
        blobDataDtoList.add(blobBookmark);

        //
        BookmarkDto bookmarkDateReviewRequested = createBookmark(BookmarkConstants.DATE_REVIEW_REQUESTED,
                DateUtils.stringDt(adminReviewFindingsDto.getAdminReviewDto().getDtAdminRvReqAppeal()));
        bookmarkNonFrmGrpList.add(bookmarkDateReviewRequested);

        BookmarkDto bookmarkDateReviewConducted = createBookmark(BookmarkConstants.DATE_REVIEW_CONDUCTED,
                DateUtils.stringDt(adminReviewFindingsDto.getAdminReviewDto().getDtAdminRvAppealReview()));
        bookmarkNonFrmGrpList.add(bookmarkDateReviewConducted);


        BookmarkDto bookmarkNotificationDate = createBookmark(BookmarkConstants.NOTIFICATION_DATE,
                DateUtils.stringDt(adminReviewFindingsDto.getAdminReviewDto().getDtAdminRvAppealNotif()));
        bookmarkNonFrmGrpList.add(bookmarkNotificationDate);

        BookmarkDto bookmarkResultOfAppealReview = createBookmarkWithCodesTable(BookmarkConstants.RESULT_OF_APPEAL_REVIEW,
                adminReviewFindingsDto.getAdminReviewDto().getCdAdminRvAppealResult(), CodesConstant.CARVWRES);
        bookmarkNonFrmGrpList.add(bookmarkResultOfAppealReview);


        //CSES65D
        if (!CodesConstant.CARVTYPE_020.equalsIgnoreCase(adminReviewFindingsDto.getAdminReviewDto().getCdAdminRvAppealType())) {
            //CSES65D
            FormDataGroupDto releaseProcessGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_IF_RELEASE_PROCESS,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(releaseProcessGrp);
            releaseProcessGrp.setBookmarkDtoList(Arrays.asList(
                    createBookmark(BookmarkConstants.EMERGENCY_RELEASE,
                            adminReviewFindingsDto.getAdminReviewDto().getIndAdminRvEmgcyRel()),
                    createBookmark(BookmarkConstants.RELEASE_DATE,
                            DateUtils.stringDt(adminReviewFindingsDto.getAdminReviewDto().getDtAdminRvEmgcyRel())),
                    createBookmark(BookmarkConstants.HEARING_DATE,
                            DateUtils.stringDt(adminReviewFindingsDto.getAdminReviewDto().getDtAdminRvHearing()))
            ));

            //CSES65D
            FormDataGroupDto currentAllegGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CURRENT_ALLEGATIONS_HEADER, BookmarkConstants.EMPTY_STRING);
            formDataGroupList.add(currentAllegGroup);
            //group id 9136 or 9131
            List<FormDataGroupDto> currentAllegGrpList = new ArrayList<FormDataGroupDto>();
            for (StageReviewDto stageCurrentDto : adminReviewFindingsDto.getStageNotReviewDto()) {
                FormDataGroupDto currentAllegGrp = createFormDataGroup(
                        CodesConstant.CPGRMS_CPS.equalsIgnoreCase(adminReviewFindingsDto.getStageCaseDtlDto().getCdStageProgram())
                                ? FormGroupsConstants.TMPLAT_CPS_ALLEGATIONS : FormGroupsConstants.TMPLAT_NOT_CPS_ALLEGATIONS
                        , FormGroupsConstants.TMPLAT_CURRENT_ALLEGATIONS_HEADER);
                currentAllegGrp.setBookmarkDtoList(Arrays.asList(
                        createBookmarkWithCodesTable(BookmarkConstants.PRIOR_DISPOSITION,
                                stageCurrentDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN),
                        createBookmarkWithCodesTable(BookmarkConstants.PRIOR_SEVERITY,
                                stageCurrentDto.getCdAdminAllegSeverity(), CodesConstant.CSEVERTY),
                        createBookmarkWithCodesTable(BookmarkConstants.PRIOR_ALLEGATION,
                                stageCurrentDto.getCdAdminAllegType(), CodesConstant.CABALTYP),
                        createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX,
                                stageCurrentDto.getCdNameSuffix(), CodesConstant.CSUFFIX),
                        createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
                                stageCurrentDto.getNmNameFirst()),
                        createBookmark(BookmarkConstants.VICTIM_NAME_LAST,
                                stageCurrentDto.getNmNameLast()),
                        createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
                                stageCurrentDto.getNmNameMiddle())
                ));
                currentAllegGrpList.add(currentAllegGrp);
            }
            currentAllegGroup.setFormDataGroupList(currentAllegGrpList);

        }


            //CSES65D
        if(! ObjectUtils.isEmpty(adminReviewFindingsDto.getStageReviewDtolist())) {
            FormDataGroupDto priorAllegGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_ALLEGATIONS_HEADER, BookmarkConstants.EMPTY_STRING);
            formDataGroupList.add(priorAllegGroup);
            //group id 9127 or 9115
            List<FormDataGroupDto> priorAllegGrpList = new ArrayList<FormDataGroupDto>();
            for (StageReviewDto stageCurrentDto : adminReviewFindingsDto.getStageReviewDtolist()) {
                FormDataGroupDto priorAllegGrp = createFormDataGroup(
                        CodesConstant.CPGRMS_CPS.equalsIgnoreCase(adminReviewFindingsDto.getStageCaseDtlDto().getCdStageProgram())
                                ? FormGroupsConstants.TMPLAT_CPS_ALLEGATIONS : FormGroupsConstants.TMPLAT_NOT_CPS_ALLEGATIONS
                        , FormGroupsConstants.TMPLAT_PRIOR_ALLEGATIONS_HEADER);
                priorAllegGrp.setBookmarkDtoList(Arrays.asList(
                        createBookmarkWithCodesTable(BookmarkConstants.PRIOR_DISPOSITION,
                                stageCurrentDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN),
                        createBookmarkWithCodesTable(BookmarkConstants.PRIOR_SEVERITY,
                                stageCurrentDto.getCdAdminAllegSeverity(), CodesConstant.CSEVERTY),
                        createBookmarkWithCodesTable(BookmarkConstants.PRIOR_ALLEGATION,
                                stageCurrentDto.getCdAdminAllegType(), CodesConstant.CABALTYP),
                        createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX,
                                stageCurrentDto.getCdNameSuffix(), CodesConstant.CSUFFIX),
                        createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
                                stageCurrentDto.getNmNameFirst()),
                        createBookmark(BookmarkConstants.VICTIM_NAME_LAST,
                                stageCurrentDto.getNmNameLast()),
                        createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
                                stageCurrentDto.getNmNameMiddle())
                ));
                priorAllegGrpList.add(priorAllegGrp);
            }
            priorAllegGroup.setFormDataGroupList(priorAllegGrpList);
        }


        //CSEC01D
        //grp name ccf09o04
        bookmarkNonFrmGrpList.addAll(Arrays.asList(createBookmark(BookmarkConstants.REVIEWER_ADDR_CITY,
                        adminReviewFindingsDto.getEmployeePersPhNameDto().getAddrMailCodeCity()),
                createBookmark(BookmarkConstants.REVIEWER_ADDR_ST_1,
                        adminReviewFindingsDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1()),
                createBookmark(BookmarkConstants.REVIEWER_ADDR_ST_2,
                        adminReviewFindingsDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2()),
                createBookmark(BookmarkConstants.REVIEWER_ADDR_ZIP,
                        adminReviewFindingsDto.getEmployeePersPhNameDto().getAddrMailCodeZip())));



        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
        preFillData.setBlobDataDtoList(blobDataDtoList);


        return preFillData;
    }


}


