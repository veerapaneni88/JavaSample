package us.tx.state.dfps.service.forms.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.cciinvReport.dto.*;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.SafetyPlanDto;
import us.tx.state.dfps.service.dangerindicators.dto.DangerIndicatorsDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.AllegationInvestigationLetterDto;
import us.tx.state.dfps.service.investigation.dto.AllegedSxVctmztnDto;
import us.tx.state.dfps.service.investigation.dto.CVSNotificationDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.person.dto.AfcarsDto;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CciInvReportPrefillData extends DocumentServiceUtil {

    @Autowired
    LookupService lookupService;

    @Autowired
    LookupDao lookupDao;

    @Autowired
    private CodesDao codesDao;


    public static final String REPORTER = "(reporter)";
    public static final String NO_RESTRAINT = "No Restraint";
    public static final String NO_DANGER_IND_PRESENT = "No Danger Indicators present.";


    public CciInvReportPrefillData() {
        super();
    }
    private static final Logger logger = Logger.getLogger(CciInvReportPrefillData.class);

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        CciInvReportDto cciInvReportDto = (CciInvReportDto) parentDtoobj;

        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();
        List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
        List<BlobDataDto> bookmarkBlobDataList = new ArrayList<BlobDataDto>();


        BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
                cciInvReportDto.getGenericCaseInfoDto().getNmCase());
        bookmarkNonFormGrpList.add(bookmarkNmCase);
        BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
                cciInvReportDto.getGenericCaseInfoDto().getIdCase());
        bookmarkNonFormGrpList.add(bookmarkIdCase);

        BookmarkDto bookmarkCciInvstDtlIntake = createBookmark(BookmarkConstants.DT_INT_REC,
                DateUtils.stringDt(cciInvReportDto.getLicensingInvstDtlDto().getDtLicngInvstIntake()));
        bookmarkNonFormGrpList.add(bookmarkCciInvstDtlIntake);

        // Overall Disposition
        String CciInvstDtlOverallDispFullForm = ServiceConstants.EMPTY_STRING;
        if(!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getCdLicngInvstOvrallDisp())) {
            CciInvstDtlOverallDispFullForm = lookupDao.simpleDecode(ServiceConstants.CCIVALDS,
                    cciInvReportDto.getLicensingInvstDtlDto().getCdLicngInvstOvrallDisp());
        }
        BookmarkDto bookmarkCciInvstDtlOverallDisp = createBookmark(BookmarkConstants.OVERALL_DISP,
                CciInvstDtlOverallDispFullForm);
        bookmarkNonFormGrpList.add(bookmarkCciInvstDtlOverallDisp);

        // Recommended Action - Column: CD_STAGE_REASON_CLOSED,   Table: Stage
        BookmarkDto bookmarkCciInvstDtlRecAction = createBookmarkWithCodesTable(BookmarkConstants.REC_ACTION,
                cciInvReportDto.getGenericCaseInfoDto().getCdStageReasonClosed(), ServiceConstants.CLCRECAT);
        bookmarkNonFormGrpList.add(bookmarkCciInvstDtlRecAction);

        BookmarkDto bookmarkDtCciInvstDtlBegun = createBookmark(BookmarkConstants.DT_INV_BEGUN,
                DateUtils.stringDt(cciInvReportDto.getGenericCaseInfoDto().getDtInvInitiated()));
        bookmarkNonFormGrpList.add(bookmarkDtCciInvstDtlBegun);

        BookmarkDto bookmarkCciInvstDtlComplt = createBookmark(BookmarkConstants.DT_INV_COMPLETED,
                DateUtils.stringDt(cciInvReportDto.getLicensingInvstDtlDto().getDtLicngInvstComplt()));
        bookmarkNonFormGrpList.add(bookmarkCciInvstDtlComplt);

        // CCMN19D StagePersonDto
        BookmarkDto bookmarkPersonFull = createBookmark(BookmarkConstants.CASEWORKER_FULL_NM,
                cciInvReportDto.getStagePersonDto().getNmPersonFull());
        bookmarkNonFormGrpList.add(bookmarkPersonFull);

        // CCMN60D SupervisorDto
        BookmarkDto bookmarkSupvPersonFull = createBookmark(BookmarkConstants.SUPERVISOR_FULL_NM,
                cciInvReportDto.getSupervisorDto().getNmPersonFull());
        bookmarkNonFormGrpList.add(bookmarkSupvPersonFull);

        // CSEC01D EmployeePersPhNameDto
        BookmarkDto bookmarkAddrMailCodeCity = createBookmark(BookmarkConstants.OFFICE_ADDR_CITY,
                cciInvReportDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
        bookmarkNonFormGrpList.add(bookmarkAddrMailCodeCity);
        BookmarkDto bookmarkAddrMailCodeStLn1 = createBookmark(BookmarkConstants.OFFICE_ADDR_LINE_1,
                cciInvReportDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
        bookmarkNonFormGrpList.add(bookmarkAddrMailCodeStLn1);
        BookmarkDto bookmarkAddrMailCodeZip = createBookmark(BookmarkConstants.OFFICE_ADDR_ZIP,
                cciInvReportDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
        bookmarkNonFormGrpList.add(bookmarkAddrMailCodeZip);

        BookmarkDto bookmarkDtStageClose = createBookmark(BookmarkConstants.DT_INV_APPROVED,
                DateUtils.stringDt(cciInvReportDto.getGenericCaseInfoDto().getDtStageClose()));
        bookmarkNonFormGrpList.add(bookmarkDtStageClose);
        BookmarkDto bookmarkCaseCounty = createBookmarkWithCodesTable(BookmarkConstants.STAGE_COUNTY,
                cciInvReportDto.getEmployeePersPhNameDto().getAddrMailCodeCounty(), CodesConstant.CCOUNT);
        bookmarkNonFormGrpList.add(bookmarkCaseCounty);
        BookmarkDto bookmarkStageCurrPriority = createBookmarkWithCodesTable(BookmarkConstants.STAGE_PRIORITY,
                cciInvReportDto.getGenericCaseInfoDto().getCdStageCurrPriority(), CodesConstant.CPRIORTY);
        bookmarkNonFormGrpList.add(bookmarkStageCurrPriority);
        BookmarkDto bookmarkStageReasonClosed = createBookmarkWithCodesTable(BookmarkConstants.INV_ACTION,
                cciInvReportDto.getGenericCaseInfoDto().getCdStageReasonClosed(), CodesConstant.CCINVCLS);
        bookmarkNonFormGrpList.add(bookmarkStageReasonClosed);
        BookmarkDto bookmarkCaseSensitive = createBookmark(BookmarkConstants.SENSITIVE_CASE,
                cciInvReportDto.getGenericCaseInfoDto().getIndCaseSensitive());
        bookmarkNonFormGrpList.add(bookmarkCaseSensitive);

        // First section - Safety Plan Completed
        if(cciInvReportDto.getSafetyPlanDtoList().stream().anyMatch(safetyPlnDto -> safetyPlnDto.getSafetyPlanStatus().equalsIgnoreCase(ServiceConstants.SAFETY_PLAN_STATUS_INE))){
            BookmarkDto bookmarkSafetyPlanCompleted = createBookmark(BookmarkConstants.SAFETY_PLAN_COMPLETE, ServiceConstants.YES_TEXT);
            bookmarkNonFormGrpList.add(bookmarkSafetyPlanCompleted);
        } else {
            BookmarkDto bookmarkSafetyPlanCompleted = createBookmark(BookmarkConstants.SAFETY_PLAN_COMPLETE, ServiceConstants.NO_TEXT);
            bookmarkNonFormGrpList.add(bookmarkSafetyPlanCompleted);
        }

        // Multiple Referral
        BookmarkDto bookmarkMultipleReferral = createBookmark(BookmarkConstants.MULTI_REF,
                cciInvReportDto.getMrefString());
        bookmarkNonFormGrpList.add(bookmarkMultipleReferral);


        // Licensing Operation Information
        BookmarkDto bookmarkFacName = createBookmark(BookmarkConstants.FAC_NAME,
                cciInvReportDto.getLicensingInvstDtlDto().getNmResource());
        bookmarkNonFormGrpList.add(bookmarkFacName);
        BookmarkDto bookmarkFacType = createBookmarkWithCodesTable(BookmarkConstants.FAC_TYPE,
                cciInvReportDto.getLicensingInvstDtlDto().getCdRsrcFacilType(), CodesConstant.CFACTYP2);
        bookmarkNonFormGrpList.add(bookmarkFacType);

        bookmarkNonFormGrpList.add(createBookmark(BookmarkConstants.RESOURCE_ID,
                cciInvReportDto.getLicensingInvstDtlDto().getIdResource()));
        bookmarkNonFormGrpList.add(createBookmark(BookmarkConstants.CLASS_OP_NUMBER, new StringBuilder()
                .append(!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getNbrAcclaim()) ? cciInvReportDto.getLicensingInvstDtlDto().getNbrAcclaim() + FormConstants.SPACE : FormConstants.EMPTY_STRING)
                .append(!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getNbrAgency()) ? cciInvReportDto.getLicensingInvstDtlDto().getNbrAgency() + FormConstants.SPACE : FormConstants.EMPTY_STRING)
                .append(!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getNbrBranch()) ? cciInvReportDto.getLicensingInvstDtlDto().getNbrBranch() : FormConstants.EMPTY_STRING).toString()));
        BookmarkDto bookmarkFacSuper = createBookmark(BookmarkConstants.FAC_SUPERINTENDENT,
                cciInvReportDto.getLicensingInvstDtlDto().getNmResourceContact());
        bookmarkNonFormGrpList.add(bookmarkFacSuper);

        BookmarkDto bookmarkFacAddrLn1 = createBookmark(BookmarkConstants.FAC_ADDR_LN_1,
                cciInvReportDto.getLicensingInvstDtlDto().getAddrStLn1());
        bookmarkNonFormGrpList.add(bookmarkFacAddrLn1);
        BookmarkDto bookmarkFacAddrLn2 = createBookmark(BookmarkConstants.FAC_ADDR_LN_2,
                cciInvReportDto.getLicensingInvstDtlDto().getAddrStLn2());
        bookmarkNonFormGrpList.add(bookmarkFacAddrLn2);
        BookmarkDto bookmarkFacAddrCity = createBookmark(BookmarkConstants.FAC_ADDR_CITY,
                cciInvReportDto.getLicensingInvstDtlDto().getAddrCity());
        bookmarkNonFormGrpList.add(bookmarkFacAddrCity);
        BookmarkDto bookmarkFacAddrState = createBookmark(BookmarkConstants.FAC_ADDR_STATE,
                cciInvReportDto.getLicensingInvstDtlDto().getAddrState());
        bookmarkNonFormGrpList.add(bookmarkFacAddrState);
        BookmarkDto bookmarkFacAddrZip = createBookmark(BookmarkConstants.FAC_ADDR_ZIP,
                cciInvReportDto.getLicensingInvstDtlDto().getAddrZip());
        bookmarkNonFormGrpList.add(bookmarkFacAddrZip);
        BookmarkDto bookmarkFacAddrCounty = createBookmarkWithCodesTable(BookmarkConstants.FAC_ADDR_COUNTY,
                cciInvReportDto.getLicensingInvstDtlDto().getAddrCounty(), CodesConstant.CCOUNT);
        bookmarkNonFormGrpList.add(bookmarkFacAddrCounty);

        if(!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getNbrPhone())) {
            BookmarkDto bookmarkFacPhone = createBookmark(BookmarkConstants.FAC_PHON,
                    TypeConvUtil.formatPhone(cciInvReportDto.getLicensingInvstDtlDto().getNbrPhone()));
            bookmarkNonFormGrpList.add(bookmarkFacPhone);
        }

        Date crelNotifRgtsDate = null;
        try {
            crelNotifRgtsDate = DateUtils.toJavaDateFromInput(lookupDao.simpleDecode(ServiceConstants.CRELDATE,
                    CodesConstant.CRELDATE_JUNE_24_NTFCTN_RGHTS));
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        if (!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto()) &&
                !ObjectUtils.isEmpty(crelNotifRgtsDate) && (ObjectUtils.isEmpty(cciInvReportDto.getGenericCaseInfoDto().getDtStageClose())
                || DateUtils.isAfter(cciInvReportDto.getGenericCaseInfoDto().getDtStageClose(), crelNotifRgtsDate))) {
            FormDataGroupDto formForNotifRights = createFormDataGroup(FormGroupsConstants.TMPLAT_NOTIFICATION_RIGHTS,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkDtoNotifRightsList = new ArrayList<BookmarkDto>();
            BookmarkDto bookmarkVrblWrtnNtfcnRgts = createBookmarkWithCodesTable(BookmarkConstants.INV_VRB_WRT_NOTIF,
                    cciInvReportDto.getLicensingInvstDtlDto().getIndVrblWrtnNotifRights(), CodesConstant.CINVACAN);
            bookmarkDtoNotifRightsList.add(bookmarkVrblWrtnNtfcnRgts);
            BookmarkDto bookmarkNtfcnRgtsFormUpld = createBookmarkWithCodesTable(BookmarkConstants.INV_NOTIF_FRM_UPD,
                    cciInvReportDto.getLicensingInvstDtlDto().getIndNotifRightsUpld(), CodesConstant.CINVACAN);
            bookmarkDtoNotifRightsList.add(bookmarkNtfcnRgtsFormUpld);
            formForNotifRights.setBookmarkDtoList(bookmarkDtoNotifRightsList);
            formDataGroupList.add(formForNotifRights);
        }

        if (!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto()) && CodesConstant.CFACTYP2_60.equalsIgnoreCase(cciInvReportDto.getLicensingInvstDtlDto().getCdRsrcFacilType())) {
            FormDataGroupDto agencyHomeGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AGENCY,
                    FormConstants.EMPTY_STRING);
            agencyHomeGroupDto.setBookmarkDtoList(Arrays.asList(
                    createBookmark(BookmarkConstants.AGENCY_HOME_NAME, cciInvReportDto.getLicensingInvstDtlDto().getNmAffilResource()),
                    createBookmarkWithCodesTable(BookmarkConstants.AGENCY_HOME_TYPE, cciInvReportDto.getLicensingInvstDtlDto().getCdAffilFacilType(), CodesConstant.CFACTYP2),
                    createBookmark(BookmarkConstants.AGENCY_RESOURCEID, cciInvReportDto.getLicensingInvstDtlDto().getIdAffilResource()),
                    createBookmark(BookmarkConstants.AGENCY_ADDR_LN_1,cciInvReportDto.getLicensingInvstDtlDto().getAddrAffilStLn1()),
                    createBookmark(BookmarkConstants.AGENCY_ADDR_CITY, cciInvReportDto.getLicensingInvstDtlDto().getAddrAffilCity()),
                    createBookmark(BookmarkConstants.AGENCY_ADDR_ZIP, cciInvReportDto.getLicensingInvstDtlDto().getAddrAffilZip()),
                    createBookmarkWithCodesTable(BookmarkConstants.AGENCY_ADDR_COUNTY, cciInvReportDto.getLicensingInvstDtlDto().getAddrAffilCounty(), CodesConstant.CCOUNT),
                    createBookmark(BookmarkConstants.AGENCY_ADDR_STATE, cciInvReportDto.getLicensingInvstDtlDto().getAddrAffilState()),
                    createBookmark(BookmarkConstants.AGENCY_HOME_PHONE,
                            !ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getNbrAffilPhone())
                                    ? TypeConvUtil.getPhoneWithFormat(cciInvReportDto.getLicensingInvstDtlDto().getNbrAffilPhone(),null) : FormConstants.EMPTY_STRING),
                    createBookmark(BookmarkConstants.AGENCY_CLASSOPNUM, new StringBuilder()
                            .append(!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getNbrAffilAcclaim()) ? cciInvReportDto.getLicensingInvstDtlDto().getNbrAffilAcclaim() + FormConstants.SPACE : FormConstants.EMPTY_STRING)
                            .append(!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getNbrAffilAgency()) ? cciInvReportDto.getLicensingInvstDtlDto().getNbrAffilAgency() + FormConstants.SPACE : FormConstants.EMPTY_STRING)
                            .append(!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getNbrAffilBranch()) ? cciInvReportDto.getLicensingInvstDtlDto().getNbrAffilBranch() : FormConstants.EMPTY_STRING).toString())));
            if (!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getAddrAffilStLn2())) {
                FormDataGroupDto addr2GroupDto = createFormDataGroup(
                        FormGroupsConstants.TMPLAT_AGENCY_ADDR2, FormGroupsConstants.TMPLAT_AGENCY);
                addr2GroupDto.setBookmarkDtoList(Arrays.asList(createBookmark(BookmarkConstants.AGENCY_ADDR_LN_2,
                        cciInvReportDto.getLicensingInvstDtlDto().getAddrAffilStLn2())));
                agencyHomeGroupDto.setFormDataGroupList(Arrays.asList(addr2GroupDto));
            }
            formDataGroupList.add(agencyHomeGroupDto);
        }

        /* Intake Narrative */

        BookmarkDto bookmarkIntakeNarrDtReceived = createBookmark(BookmarkConstants.INTAKE_DATE,
                DateUtils.stringDt(cciInvReportDto.getCciInvIntakeDto().getDtIntakeReceived()));
        bookmarkNonFormGrpList.add(bookmarkIntakeNarrDtReceived);

        BookmarkDto bookmarkStageNumber = createBookmark(BookmarkConstants.INTAKE_STAGE_NUMBER,
                cciInvReportDto.getCciInvIntakeDto().getIdStage());
        bookmarkNonFormGrpList.add(bookmarkStageNumber);
        BookmarkDto bookmarkIntakeStageType = createBookmark(BookmarkConstants.INTAKE_STAGE_TYPE,
                cciInvReportDto.getCciInvIntakeDto().getCdStageType());
        bookmarkNonFormGrpList.add(bookmarkIntakeStageType);

        if (!ObjectUtils.isEmpty(cciInvReportDto.getPersonSplInfoListPrin())) {
            BookmarkDto bookmarkCdIntStageRelInt = createBookmarkWithCodesTable(BookmarkConstants.INTAKE_REL_INT,
                    cciInvReportDto.getPersonSplInfoListPrin().get(0).getCdStagePersRelInt(),
                    CodesConstant.CRPTRINT);
            bookmarkNonFormGrpList.add(bookmarkCdIntStageRelInt);
            BookmarkDto bookmarkReporterName = createBookmark(BookmarkConstants.INTAKE_REPORTER_NAME,
                    cciInvReportDto.getPersonSplInfoListPrin().get(0).getNmPersonFull());
            bookmarkNonFormGrpList.add(bookmarkReporterName);
            BookmarkDto bookmarkIntReporterId= createBookmark(BookmarkConstants.INTAKE_REPORTER_ID,
                    cciInvReportDto.getPersonSplInfoListPrin().get(0).getIdPerson());
            bookmarkNonFormGrpList.add(bookmarkIntReporterId);
            if (!ObjectUtils.isEmpty(cciInvReportDto.getPersonSplInfoListPrin().get(0).getTxtStagePersNote())) {
                BookmarkDto bookmarkTxtIntPersonNote = createBookmark(BookmarkConstants.INTAKE_REPORTER_NOTES,
                        cciInvReportDto.getPersonSplInfoListPrin().get(0).getTxtStagePersNote().replace("<br>",
                                ". "));
                bookmarkNonFormGrpList.add(bookmarkTxtIntPersonNote);
            }
        }
        Long id = cciInvReportDto.getCciInvIntakeDto().getIdStage();
        BlobDataDto bookmarkBlobIdStage = createBlobData(BookmarkConstants.INTAKE_NARRATIVE,
                CodesConstant.INCOMING_NARRATIVE_VIEW, id.toString());
        bookmarkBlobDataList.add(bookmarkBlobIdStage);

        /* Merged Intake Narrative */
        for (CciInvIntakePersonDto cciInvIntakePersonDto : cciInvReportDto.getIntakesList()) {
            FormDataGroupDto intakeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_MERGE_INTAKE_NARR,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(intakeGroup);
            List<BookmarkDto> bookmarkIntakeList = new ArrayList<BookmarkDto>();
            List<BlobDataDto> bookmarkMergeBlobDataList = new ArrayList<BlobDataDto>();

            BookmarkDto bookmarkDtStageStart = createBookmark(BookmarkConstants.DATE_STAGE_START,
                    DateUtils.stringDt(cciInvIntakePersonDto.getDtStageStart()));
            bookmarkIntakeList.add(bookmarkDtStageStart);
            BookmarkDto bookmarkIntakePersRelIntString = createBookmarkWithCodesTable(BookmarkConstants.MERGED_REL_INT,
                    cciInvIntakePersonDto.getCdStagePersonRelInt(), CodesConstant.CRPTRINT);
            bookmarkIntakeList.add(bookmarkIntakePersRelIntString);

            BookmarkDto bookmarkDtStageType = createBookmark(BookmarkConstants.STAGE_TYPE,
                    cciInvIntakePersonDto.getCdStageType());
            bookmarkIntakeList.add(bookmarkDtStageType);
            BookmarkDto bookmarkDtStageName = createBookmark(BookmarkConstants.MERGED_REPORTER_NAME,
                    cciInvIntakePersonDto.getNmPersonFull());
            bookmarkIntakeList.add(bookmarkDtStageName);
            BookmarkDto bookmarkDtStageNotes = createBookmark(BookmarkConstants.MERGED_REPORTER_NOTES,
                    cciInvIntakePersonDto.getTxtStagePersonNote());
            bookmarkIntakeList.add(bookmarkDtStageNotes);
            BookmarkDto bookmarkPersonId = createBookmark(BookmarkConstants.MERGED_REPORTER_ID,
                   cciInvIntakePersonDto.getIdPerson());
            bookmarkIntakeList.add(bookmarkPersonId);
            BookmarkDto bookmarkStageNum = createBookmark(BookmarkConstants.STAGE_NUMBER,
                    cciInvIntakePersonDto.getIdPriorStage());
            bookmarkIntakeList.add(bookmarkStageNum);

            Long idPriorStage = cciInvIntakePersonDto.getIdPriorStage();
            BlobDataDto bookmarkMergedBlobIdStage = createBlobData(BookmarkConstants.MERGE_NARRATIVE,
                    CodesConstant.INCOMING_NARRATIVE_VIEW, idPriorStage.toString());
            bookmarkMergeBlobDataList.add(bookmarkMergedBlobIdStage);

            intakeGroup.setBookmarkDtoList(bookmarkIntakeList);
            intakeGroup.setBlobDataDtoList(bookmarkMergeBlobDataList);
        }

        /* Alerts of Alleged Behaviors */
        if(!cciInvReportDto.getLicensingInvstDtlDto().getAllegedSxVctmztnDtoList().isEmpty()) {
            FormDataGroupDto allegedSxVctmztnGroupHeading = createFormDataGroup(FormGroupsConstants.TMPLAT_AL_ALLGED_BHVR_HEADING,
                    FormConstants.EMPTY_SPACE);
            formDataGroupList.add(allegedSxVctmztnGroupHeading);
            List<FormDataGroupDto> formDataGroupVctmztnList = new ArrayList<FormDataGroupDto>();
            for (AllegedSxVctmztnDto allegedSxVctmztnDto : cciInvReportDto.getLicensingInvstDtlDto().getAllegedSxVctmztnDtoList()) {
                FormDataGroupDto allegedSxVctmztnGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ALERTS_ALLGED_BHVR,
                        FormGroupsConstants.TMPLAT_AL_ALLGED_BHVR_HEADING);
                formDataGroupVctmztnList.add(allegedSxVctmztnGroup);
                List<BookmarkDto> bookmarkAllegSxVctmztnList = new ArrayList();
                BookmarkDto bookmarkVictim = createBookmark(BookmarkConstants.VICTIM, allegedSxVctmztnDto.getNameVictim());
                bookmarkAllegSxVctmztnList.add(bookmarkVictim);

                BookmarkDto bookmarkCaseWorker = createBookmark(BookmarkConstants.CASEWORKER,
                        allegedSxVctmztnDto.getNameSubWorker());
                bookmarkAllegSxVctmztnList.add(bookmarkCaseWorker);
                BookmarkDto bookmarkSupervisor = createBookmark(BookmarkConstants.SUPERVISOR,
                        allegedSxVctmztnDto.getNameSupervisor());
                bookmarkAllegSxVctmztnList.add(bookmarkSupervisor);

                if ("Y".equalsIgnoreCase(allegedSxVctmztnDto.getIndAllegedVctmCsa())) {
                    bookmarkAllegSxVctmztnList.add(createBookmark(BookmarkConstants.ALG_VCTM_OF_CSA,
                            ServiceConstants.CHECKED));
                }
                if ("Y".equalsIgnoreCase(allegedSxVctmztnDto.getIndAllegedSxAggression())) {
                    bookmarkAllegSxVctmztnList.add(createBookmark(BookmarkConstants.ALG_AGGRSR_OF_CSA,
                            ServiceConstants.CHECKED));
                }
                if ("Y".equalsIgnoreCase(allegedSxVctmztnDto.getIndAllegedSxBehaviorProblem())) {
                    bookmarkAllegSxVctmztnList.add(createBookmark(BookmarkConstants.ALG_SXL_BHVR_PRBLM,
                            ServiceConstants.CHECKED));
                }
                if ("Y".equalsIgnoreCase(allegedSxVctmztnDto.getIndAllegedHumanTrafficking())) {
                    bookmarkAllegSxVctmztnList.add(createBookmark(BookmarkConstants.ALG_HUMAN_TRFCKNG,
                            ServiceConstants.CHECKED));
                }
                allegedSxVctmztnGroup.setBookmarkDtoList(bookmarkAllegSxVctmztnList);
            }
            allegedSxVctmztnGroupHeading.setFormDataGroupList(formDataGroupVctmztnList);
        }
        /* Notification to CVS of Closed Investigation */
        for(CVSNotificationDto cvsNotificationDto : cciInvReportDto.getLicensingInvstDtlDto().getCvsNotificationDtoList()){
            FormDataGroupDto cvsNotificationGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_NTFCTN_CVS_CLSD_INV,
                    FormConstants.EMPTY_SPACE);
            formDataGroupList.add(cvsNotificationGroup);
            List<BookmarkDto> bookmarkCvsNotificationList = new ArrayList();

            BookmarkDto bookmarkAllegVictim = createBookmark(BookmarkConstants.ALLEG_VICTIM,
                    cvsNotificationDto.getNameVictim());
            bookmarkCvsNotificationList.add(bookmarkAllegVictim);
            BookmarkDto bookmarkAlertNtfctn = createBookmark(BookmarkConstants.ALERT_NTFCTN,
                    cvsNotificationDto.getAlertSent());
            bookmarkCvsNotificationList.add(bookmarkAlertNtfctn);

            BookmarkDto bookmarkDtAlertNtfctn= createBookmark(BookmarkConstants.DT_ALERT_NTFCTN,
                    DateUtils.stringDt(cvsNotificationDto.getDateAlertSent()));
            bookmarkCvsNotificationList.add(bookmarkDtAlertNtfctn);

            BookmarkDto bookmarkEmailNtfctn = createBookmark(BookmarkConstants.EMAIL_NTFCTN,
                    cvsNotificationDto.getAutoEmailSent());
            bookmarkCvsNotificationList.add(bookmarkEmailNtfctn);

            BookmarkDto bookmarkDtEmailNtfctn = createBookmark(BookmarkConstants.DT_EMAIL_NTFCTN,
                    DateUtils.stringDt(cvsNotificationDto.getDateAutoEmailSent()));
            bookmarkCvsNotificationList.add(bookmarkDtEmailNtfctn);

            BookmarkDto bookmarkMnlEmailNtfctn = createBookmark(BookmarkConstants.MANUAL_EMAIL_NTFCTN,
                    cvsNotificationDto.getManualEmailSent());
            bookmarkCvsNotificationList.add(bookmarkMnlEmailNtfctn);

            BookmarkDto bookmarkDtMnlEmailNtfctn = createBookmark(BookmarkConstants.DT_MANUAL_EMAIL,
                    DateUtils.stringDt(cvsNotificationDto.getDateManualEmailSent()));
            bookmarkCvsNotificationList.add(bookmarkDtMnlEmailNtfctn);

            cvsNotificationGroup.setBookmarkDtoList(bookmarkCvsNotificationList);

        }

        //Letter Detail
        if(!CollectionUtils.isEmpty(cciInvReportDto.getLetterDetailList())){
            for (CciInvLetterDto letterDto : cciInvReportDto.getLetterDetailList()) {

                FormDataGroupDto letterDetailGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_LETTERDETAIL,
                        FormConstants.EMPTY_STRING);
                formDataGroupList.add(letterDetailGroup);
                List<BookmarkDto> bookmarkLetterDetailList = new ArrayList<BookmarkDto>();

                BookmarkDto bookmarkDateEntered = createBookmark(BookmarkConstants.DT_ENTERED, letterDto.getDtCreated());
                bookmarkLetterDetailList.add(bookmarkDateEntered);
                BookmarkDto bookmarkLetterType = createBookmarkWithCodesTable(BookmarkConstants.LTR_TYP,
                        letterDto.getLetterType(), CodesConstant.CLTTRTYP);
                bookmarkLetterDetailList.add(bookmarkLetterType);
                BookmarkDto bookmarkLetterTo = createBookmark(BookmarkConstants.LTR_TO,
                        (letterDto.getLetterTo()));
                bookmarkLetterDetailList.add(bookmarkLetterTo);
                BookmarkDto bookmarkLetterMethod = createBookmarkWithCodesTable(BookmarkConstants.LTR_MTHD,
                        letterDto.getLetterMethod(), CodesConstant.CLTRMTD);
                bookmarkLetterDetailList.add(bookmarkLetterMethod);
                BookmarkDto bookmarkLetterId = createBookmark(BookmarkConstants.LTR_ID,
                        letterDto.getIdLetter());
                bookmarkLetterDetailList.add(bookmarkLetterId);
                letterDetailGroup.setBookmarkDtoList(bookmarkLetterDetailList);
            }
        }else{
            FormDataGroupDto letterDetailGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_LETTERDETAIL,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(letterDetailGroup);
            List<BookmarkDto> bookmarkLetterDetailList = new ArrayList<BookmarkDto>();

            BookmarkDto bookmarkDateEntered = createBookmark(BookmarkConstants.DT_ENTERED, ServiceConstants.NOT_APPLICABLE);
            bookmarkLetterDetailList.add(bookmarkDateEntered);
            BookmarkDto bookmarkLetterType = createBookmark(BookmarkConstants.LTR_TYP, ServiceConstants.NOT_APPLICABLE);
            bookmarkLetterDetailList.add(bookmarkLetterType);
            BookmarkDto bookmarkLetterTo = createBookmark(BookmarkConstants.LTR_TO, ServiceConstants.NOT_APPLICABLE);
            bookmarkLetterDetailList.add(bookmarkLetterTo);
            BookmarkDto bookmarkLetterMethod = createBookmark(BookmarkConstants.LTR_MTHD, ServiceConstants.NOT_APPLICABLE);
            bookmarkLetterDetailList.add(bookmarkLetterMethod);
            BookmarkDto bookmarkLetterId = createBookmark(BookmarkConstants.LTR_ID, ServiceConstants.NOT_APPLICABLE);
            bookmarkLetterDetailList.add(bookmarkLetterId);
            letterDetailGroup.setBookmarkDtoList(bookmarkLetterDetailList);
        }

        //Allegation Details
        if(!CollectionUtils.isEmpty(cciInvReportDto.getAllegationsList())){
            for (CciInvAllegDto cciInvAllegDto : cciInvReportDto.getAllegationsList()) {

                FormDataGroupDto allegationsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
                        FormConstants.EMPTY_STRING);
                formDataGroupList.add(allegationsGroup);
                List<BookmarkDto> bookmarkAllegList = new ArrayList<BookmarkDto>();

                BookmarkDto bookmarkAllegDisp = createBookmark(BookmarkConstants.ALG_DISP, cciInvAllegDto.getCdAllegDisp());
                bookmarkAllegList.add(bookmarkAllegDisp);

                BookmarkDto bookmarkAllegId = createBookmark(BookmarkConstants.ALG_ID, cciInvAllegDto.getIdAllegation());
                bookmarkAllegList.add(bookmarkAllegId);

                BookmarkDto bookmarkCdAllegSev = createBookmarkWithCodesTable(BookmarkConstants.ALG_SEVERITY,
                        cciInvAllegDto.getCdAllegSev(), CodesConstant.CSEVERTY);
                bookmarkAllegList.add(bookmarkCdAllegSev);
                BookmarkDto bookmarkAllegType = createBookmarkWithCodesTable(BookmarkConstants.ALG_TYPE,
                        cciInvAllegDto.getCdAllegType(), CodesConstant.CABALTYP);
                bookmarkAllegList.add(bookmarkAllegType);
                BookmarkDto bookmarkNmVicFull = createBookmark(BookmarkConstants.ALG_VIC_NAME,
                        (ObjectUtils.isEmpty(cciInvAllegDto.getNmVicFirst()) && ObjectUtils.isEmpty(cciInvAllegDto.getNmVicLast())) ? cciInvAllegDto.getNmVicFull() : populateName(cciInvAllegDto.getNmVicFirst(),cciInvAllegDto.getNmVicLast(),cciInvAllegDto.getNmVicMiddle()));
                bookmarkAllegList.add(bookmarkNmVicFull);
                BookmarkDto bookmarkNmPerpFull = createBookmark(BookmarkConstants.ALG_PERP_NAME,
                        (ObjectUtils.isEmpty(cciInvAllegDto.getNmPerpFirst()) && ObjectUtils.isEmpty(cciInvAllegDto.getNmPerpLast()))? cciInvAllegDto.getNmPerpFull() : populateName(cciInvAllegDto.getNmPerpFirst(),cciInvAllegDto.getNmPerpLast(),cciInvAllegDto.getNmPerpMiddle()));
                bookmarkAllegList.add(bookmarkNmPerpFull);

                if (!ObjectUtils.isEmpty(cciInvAllegDto.getDtPersonDeath())) {
                    BookmarkDto bookmarkChildFatality = createBookmark(BookmarkConstants.ALG_CHILD_FATALITY,
                            cciInvAllegDto.getCdChildFatality());
                    bookmarkAllegList.add(bookmarkChildFatality);
                } else {
                    BookmarkDto bookmarkChildFatality = createBookmark(BookmarkConstants.ALG_CHILD_FATALITY,
                            ServiceConstants.NOT_APPLICABLE);
                    bookmarkAllegList.add(bookmarkChildFatality);
                }

                BookmarkDto bookmarkTxtDispSev = createBookmark(BookmarkConstants.ALG_DISPSTN_SEVERITY,
                        formatTextValue(cciInvAllegDto.getTxtDispSev()));
                bookmarkAllegList.add(bookmarkTxtDispSev);

                allegationsGroup.setBookmarkDtoList(bookmarkAllegList);
            }
        }else{
            FormDataGroupDto allegationsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(allegationsGroup);
            List<BookmarkDto> bookmarkAllegList = new ArrayList<BookmarkDto>();

            BookmarkDto bookmarkAllegDisp = createBookmark(BookmarkConstants.ALG_DISP, ServiceConstants.NOT_APPLICABLE);
            bookmarkAllegList.add(bookmarkAllegDisp);
            BookmarkDto bookmarkAllegId = createBookmark(BookmarkConstants.ALG_ID, ServiceConstants.NOT_APPLICABLE);
            bookmarkAllegList.add(bookmarkAllegId);
            BookmarkDto bookmarkCdAllegSev = createBookmarkWithCodesTable(BookmarkConstants.ALG_SEVERITY,
                    ServiceConstants.NOT_APPLICABLE, CodesConstant.CSEVERTY);
            bookmarkAllegList.add(bookmarkCdAllegSev);
            BookmarkDto bookmarkAllegType = createBookmarkWithCodesTable(BookmarkConstants.ALG_TYPE,
                    ServiceConstants.NOT_APPLICABLE, CodesConstant.CABALTYP);
            bookmarkAllegList.add(bookmarkAllegType);
            BookmarkDto bookmarkNmVicFull = createBookmark(BookmarkConstants.ALG_VIC_NAME,
                    (ServiceConstants.NOT_APPLICABLE));
            bookmarkAllegList.add(bookmarkNmVicFull);
            BookmarkDto bookmarkNmPerpFull = createBookmark(BookmarkConstants.ALG_PERP_NAME,
                    (ServiceConstants.NOT_APPLICABLE));
            bookmarkAllegList.add(bookmarkNmPerpFull);
            BookmarkDto bookmarkChildFatality = createBookmark(BookmarkConstants.ALG_CHILD_FATALITY,
                        ServiceConstants.NOT_APPLICABLE);
            bookmarkAllegList.add(bookmarkChildFatality);

            BookmarkDto bookmarkTxtDispSev = createBookmark(BookmarkConstants.ALG_DISPSTN_SEVERITY,
                    formatTextValue(ServiceConstants.NOT_APPLICABLE));
            bookmarkAllegList.add(bookmarkTxtDispSev);

            allegationsGroup.setBookmarkDtoList(bookmarkAllegList);
        }

        // Danger Indicators Information
        if(ObjectUtils.isEmpty(cciInvReportDto.getDangerIndicatorsDto())){
            // No Danger Indicators present.
            BookmarkDto bookmarkNoDangerIndicatorsPresent = createBookmark(BookmarkConstants.NO_DANGER_IND_PRESENT,
                    NO_DANGER_IND_PRESENT);
            bookmarkNonFormGrpList.add(bookmarkNoDangerIndicatorsPresent);
        }else{
            DangerIndicatorsDto dangerIndicatorsDto = cciInvReportDto.getDangerIndicatorsDto();
            boolean isAllDangerIndicatorsNo = true;
            // Question: 1
            if(dangerIndicatorsDto.getIndCgSerPhHarm().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrCaregiverSeriousInjGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CAREGIVER_SERIOUS_INJURY,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrCaregiverSeriousInjGroup);

                List<BookmarkDto> bookmarkCaregiverSeriousInjList = new ArrayList();

                BookmarkDto bookmarkCaregiverPhysicalHarm = createBookmarkWithCodesTable(BookmarkConstants.DNG_CAREGIVER_PHY_HARM,
                        dangerIndicatorsDto.getIndCgSerPhHarm(), CodesConstant.CINVACAN);
                bookmarkCaregiverSeriousInjList.add(bookmarkCaregiverPhysicalHarm);
                List<FormDataGroupDto> formDataGroupCareGiverList = new ArrayList<FormDataGroupDto>();
                if(dangerIndicatorsDto.getIndCgSerPhHarmInj().equalsIgnoreCase(ServiceConstants.Y)) {
                    FormDataGroupDto seriousInjGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SERIOUS_INJURY,
                            FormGroupsConstants.TMPLAT_CAREGIVER_SERIOUS_INJURY);
                    formDataGroupCareGiverList.add(seriousInjGroup);
                    BookmarkDto bookmarkCaregiverSerInjury = createBookmark(BookmarkConstants.DNG_CAREGIVER_SERIOUS_INJURY,
                            ServiceConstants.CHECKED);
                    bookmarkCaregiverSeriousInjList.add(bookmarkCaregiverSerInjury);
                }
                if(dangerIndicatorsDto.getIndCgSerPhHarmThr().equalsIgnoreCase(ServiceConstants.Y)) {
                    FormDataGroupDto seriousPhyForceGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SERIOUS_PHY_FORCE,
                            FormGroupsConstants.TMPLAT_CAREGIVER_SERIOUS_INJURY);
                    formDataGroupCareGiverList.add(seriousPhyForceGroup);
                    BookmarkDto bookmarkCaregiverSerTru = createBookmark(BookmarkConstants.DNG_CAREGIVER_SERIOUS_PHY_FORCE,
                            ServiceConstants.CHECKED);
                    bookmarkCaregiverSeriousInjList.add(bookmarkCaregiverSerTru);
                }

                if(dangerIndicatorsDto.getIndCgSerPhHarmPhForce().equalsIgnoreCase(ServiceConstants.Y)) {
                    FormDataGroupDto seriousThreatGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SERIOUS_THREAT,
                            FormGroupsConstants.TMPLAT_CAREGIVER_SERIOUS_INJURY);
                    formDataGroupCareGiverList.add(seriousThreatGroup);
                    BookmarkDto bookmarkCaregiverSerForce = createBookmark(BookmarkConstants.DNG_CAREGIVER_SERIOUS_THREAT,
                            ServiceConstants.CHECKED);
                    bookmarkCaregiverSeriousInjList.add(bookmarkCaregiverSerForce);
                }
                dngrCaregiverSeriousInjGroup.setFormDataGroupList(formDataGroupCareGiverList);

                dngrCaregiverSeriousInjGroup.setBookmarkDtoList(bookmarkCaregiverSeriousInjList);
            }

            // Question: 2
            if(dangerIndicatorsDto.getIndChSexAbSus().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrChildSexAbuseGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_SEXUAL_ABUSE,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrChildSexAbuseGroup);

                List<BookmarkDto> bookmarkChildSexAbuseList = new ArrayList();

                BookmarkDto bookmarkSexAbuseSus = createBookmark(BookmarkConstants.DNG_CHILD_SEX_ABUSE,
                        ServiceConstants.YES_TEXT);
                bookmarkChildSexAbuseList.add(bookmarkSexAbuseSus);
                List<FormDataGroupDto> formDataGroupChildSexualAbuseList = new ArrayList<FormDataGroupDto>();

                // IND_CG_SER_PH_HARM_INJ
                if(dangerIndicatorsDto.getIndChSexAbSusCg().equalsIgnoreCase(ServiceConstants.Y)){
                    FormDataGroupDto dngrChildSexCaregiverGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_SEX_CAREGIVER,
                            FormGroupsConstants.TMPLAT_CHILD_SEXUAL_ABUSE);
                    formDataGroupChildSexualAbuseList.add(dngrChildSexCaregiverGroup);
                    BookmarkDto bookmarkCaregiverSerInjury = createBookmark(BookmarkConstants.DNG_CHILD_SEX_CAREGIVER,
                            ServiceConstants.CHECKED);
                    bookmarkChildSexAbuseList.add(bookmarkCaregiverSerInjury);
                }

                // IND_CG_SER_PH_HARM_PH_FORCE
                if(dangerIndicatorsDto.getIndChSexAbSusOh().equalsIgnoreCase(ServiceConstants.Y)){
                    FormDataGroupDto dngrChildSexOtherHouseholdGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_SEX_OTHER_HOUSEHOLD,
                            FormGroupsConstants.TMPLAT_CHILD_SEX_CAREGIVER);
                    formDataGroupChildSexualAbuseList.add(dngrChildSexOtherHouseholdGroup);
                    BookmarkDto bookmarkCaregiverSerForce = createBookmark(BookmarkConstants.DNG_CHILD_SEX_OTHER_HOUSEHOLD,
                            ServiceConstants.CHECKED);
                    bookmarkChildSexAbuseList.add(bookmarkCaregiverSerForce);
                }

                // IND_CG_SER_PH_HARM_THR
                if(dangerIndicatorsDto.getIndChSexAbSusUnk().equalsIgnoreCase(ServiceConstants.Y)){
                    FormDataGroupDto dngrChildSexUnknownGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_SEX_UNKNOWN_PERSON,
                            FormGroupsConstants.TMPLAT_CHILD_SEX_CAREGIVER);
                    formDataGroupChildSexualAbuseList.add(dngrChildSexUnknownGroup);
                    BookmarkDto bookmarkCaregiverSerTru = createBookmark(BookmarkConstants.DNG_CHILD_SEX_UNKNOWN_PERSON,
                            ServiceConstants.CHECKED);
                    bookmarkChildSexAbuseList.add(bookmarkCaregiverSerTru);
                }
                dngrChildSexAbuseGroup.setFormDataGroupList(formDataGroupChildSexualAbuseList);
                dngrChildSexAbuseGroup.setBookmarkDtoList(bookmarkChildSexAbuseList);
            }

            // Question: 3
            if(dangerIndicatorsDto.getIndCgAwPotHarm().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrCareGiverAwPotHarmGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DNG_CAREGIVER_AW_POT_HARM,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrCareGiverAwPotHarmGroup);

                List<BookmarkDto> bookmarkCareGiverAwPotHarmList = new ArrayList();
                BookmarkDto bookmarkCaregiverAwPotHarm = createBookmark(BookmarkConstants.DNG_CAREGIVER_AW_POT_HARM,
                        ServiceConstants.YES_TEXT);
                bookmarkCareGiverAwPotHarmList.add(bookmarkCaregiverAwPotHarm);
                dngrCareGiverAwPotHarmGroup.setBookmarkDtoList(bookmarkCareGiverAwPotHarmList);
            }

            // Question: 4
            if(dangerIndicatorsDto.getIndCgNoExpForInj().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrCaregiverNoExpForInjGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DNG_CAREGIVER_NO_EXP_FOR_INJ,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrCaregiverNoExpForInjGroup);
                List<BookmarkDto> bookmarkCaregiverNoExpForInjList = new ArrayList();

                BookmarkDto bookmarkCaregiverNoExpForInj = createBookmark(BookmarkConstants.DNG_CAREGIVER_NO_EXP_FOR_INJ,
                        ServiceConstants.YES_TEXT);
                bookmarkCaregiverNoExpForInjList.add(bookmarkCaregiverNoExpForInj);
                dngrCaregiverNoExpForInjGroup.setBookmarkDtoList(bookmarkCaregiverNoExpForInjList);
            }
            // Question: 5
            if(dangerIndicatorsDto.getIndCgDnMeetChNeedsFc().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrCaregiverDnMeetChNeedsFcGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DNG_CAREGIVER_DN_MEET_CH_NEEDS_FC,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrCaregiverDnMeetChNeedsFcGroup);
                List<BookmarkDto> bookmarkCaregiverDnMeetChNeedsFcList = new ArrayList();
                BookmarkDto bookmarkCaregiverDnMeetChNeedsFc = createBookmark(BookmarkConstants.DNG_CAREGIVER_DN_MEET_CH_NEEDS_FC,
                        ServiceConstants.YES_TEXT);
                bookmarkCaregiverDnMeetChNeedsFcList.add(bookmarkCaregiverDnMeetChNeedsFc);
                dngrCaregiverDnMeetChNeedsFcGroup.setBookmarkDtoList(bookmarkCaregiverDnMeetChNeedsFcList);
            }

            // Question: 6
            if(dangerIndicatorsDto.getIndCgDnMeetChNeedsMed().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrCaregiverDnMeetChNeedsMedGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DNG_CAREGIVER_DN_MEET_CH_NEEDS_MED,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrCaregiverDnMeetChNeedsMedGroup);
                List<BookmarkDto> bookmarkCaregiverDnMeetChNeedsMedList = new ArrayList();
                BookmarkDto bookmarkCaregiverDnMeetChNeedsMed = createBookmark(BookmarkConstants.DNG_CAREGIVER_DN_MEET_CH_NEEDS_MED,
                        ServiceConstants.YES_TEXT);
                bookmarkCaregiverDnMeetChNeedsMedList.add(bookmarkCaregiverDnMeetChNeedsMed);
                dngrCaregiverDnMeetChNeedsMedGroup.setBookmarkDtoList(bookmarkCaregiverDnMeetChNeedsMedList);
            }

            // Question: 7
            if(dangerIndicatorsDto.getIndBadLivConds().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrBadLivCondsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DNG_BAD_LIV_CONDS,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrBadLivCondsGroup);
                List<BookmarkDto> bookmarkBadLivCondsList = new ArrayList();
                BookmarkDto bookmarkBadLivConds = createBookmark(BookmarkConstants.DNG_BAD_LIV_CONDS,
                        ServiceConstants.YES_TEXT);
                bookmarkBadLivCondsList.add(bookmarkBadLivConds);
                dngrBadLivCondsGroup.setBookmarkDtoList(bookmarkBadLivCondsList);
            }

            // Question: 8
            if(dangerIndicatorsDto.getIndCgSubAbCantSupCh().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrCaregiverSubAbCantSusChGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DNG_CAREGIVER_SUB_AB_CANT_SUP_CH,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrCaregiverSubAbCantSusChGroup);
                List<BookmarkDto> bookmarkCaregiverSubAbCantSusChList = new ArrayList();
                BookmarkDto bookmarkCaregiverSubAbCantSusCh = createBookmark(BookmarkConstants.DNG_CAREGIVER_SUB_AB_CANT_SUP_CH,
                        ServiceConstants.YES_TEXT);
                bookmarkCaregiverSubAbCantSusChList.add(bookmarkCaregiverSubAbCantSusCh);
                dngrCaregiverSubAbCantSusChGroup.setBookmarkDtoList(bookmarkCaregiverSubAbCantSusChList);
            }

            // Question: 9
            if(dangerIndicatorsDto.getIndDomVioDan().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrDomVioDanGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DNG_DOM_VIO_DAN,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrDomVioDanGroup);
                List<BookmarkDto> bookmarkDomVioDanList = new ArrayList();

                BookmarkDto bookmarkDomVioDan = createBookmark(BookmarkConstants.DNG_DOM_VIO_DAN,
                        ServiceConstants.YES_TEXT);
                bookmarkDomVioDanList.add(bookmarkDomVioDan);
                dngrDomVioDanGroup.setBookmarkDtoList(bookmarkDomVioDanList);
            }

            // Question: 10
            if(dangerIndicatorsDto.getIndCgDesChNeg().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrCaregiverDesChNegGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DNG_CAREGIVER_DES_CH_NEG,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrCaregiverDesChNegGroup);
                List<BookmarkDto> bookmarkCaregiverDesChNegList = new ArrayList();

                BookmarkDto bookmarkCaregiverDesChNeg = createBookmark(BookmarkConstants.DNG_CAREGIVER_DES_CH_NEG,
                        ServiceConstants.YES_TEXT);
                bookmarkCaregiverDesChNegList.add(bookmarkCaregiverDesChNeg);
                dngrCaregiverDesChNegGroup.setBookmarkDtoList(bookmarkCaregiverDesChNegList);
            }

            // Question: 11
            if(dangerIndicatorsDto.getIndCgDisCantSupCh().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrCaregiverSubAbCantSusChGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DNG_CAREGIVER_DIS_CANT_SUP_CH,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrCaregiverSubAbCantSusChGroup);
                List<BookmarkDto> bookmarkCaregiverSubAbCantSusChList = new ArrayList();

                BookmarkDto bookmarkCaregiverDisCantSupCh = createBookmark(BookmarkConstants.DNG_CAREGIVER_DIS_CANT_SUP_CH,
                        ServiceConstants.YES_TEXT);
                bookmarkCaregiverSubAbCantSusChList.add(bookmarkCaregiverDisCantSupCh);
                dngrCaregiverSubAbCantSusChGroup.setBookmarkDtoList(bookmarkCaregiverSubAbCantSusChList);
            }

            // Question: 12
            if(dangerIndicatorsDto.getIndCgRefAccChToInv().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrCaregiverRefAccChToInvGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DNG_CAREGIVER_REF_ACC_CH_TO_INV,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrCaregiverRefAccChToInvGroup);
                List<BookmarkDto> bookmarkCaregiverRefAccChToInvList = new ArrayList();

                BookmarkDto bookmarkCaregiverRefAccChToInv = createBookmark(BookmarkConstants.DNG_CAREGIVER_REF_ACC_CH_TO_INV,
                        ServiceConstants.YES_TEXT);
                bookmarkCaregiverRefAccChToInvList.add(bookmarkCaregiverRefAccChToInv);
                dngrCaregiverRefAccChToInvGroup.setBookmarkDtoList(bookmarkCaregiverRefAccChToInvList);
            }

            // Question: 13
            if(dangerIndicatorsDto.getIndCgPrMalTrtHist().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrCaregiverPrMalTrtHistGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DNG_CAREGIVER_PR_MAL_TRT_HIST,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrCaregiverPrMalTrtHistGroup);
                List<BookmarkDto> bookmarkCaregiverPrMalTrtHistList = new ArrayList();

                BookmarkDto bookmarkCaregiverPrMalTrtHist = createBookmark(BookmarkConstants.DNG_CAREGIVER_PR_MAL_TRT_HIST,
                        ServiceConstants.YES_TEXT);
                bookmarkCaregiverPrMalTrtHistList.add(bookmarkCaregiverPrMalTrtHist);
                dngrCaregiverPrMalTrtHistGroup.setBookmarkDtoList(bookmarkCaregiverPrMalTrtHistList);
            }

            // Question: 14
            if(dangerIndicatorsDto.getIndOtherDangers().equalsIgnoreCase(ServiceConstants.Y)) {
                isAllDangerIndicatorsNo = false;
                FormDataGroupDto dngrOtherDangersGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_OTHER_DANGERS,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(dngrOtherDangersGroup);
                List<BookmarkDto> bookmarkCaregiverPrMalTrtHistList = new ArrayList();

                BookmarkDto bookmarkOtherDangers = createBookmark(BookmarkConstants.OTHER_DANGERS,
                        ServiceConstants.YES_TEXT);
                bookmarkCaregiverPrMalTrtHistList.add(bookmarkOtherDangers);

                BookmarkDto bookmarkOtherDangersText = createBookmark(BookmarkConstants.OTHER_DANGERS_TEXT,
                        dangerIndicatorsDto.getTxtOtherDangers());
                bookmarkCaregiverPrMalTrtHistList.add(bookmarkOtherDangersText);

                dngrOtherDangersGroup.setBookmarkDtoList(bookmarkCaregiverPrMalTrtHistList);
            }
            if(isAllDangerIndicatorsNo){
                // No Danger Indicators present.
                BookmarkDto bookmarkNoDangerIndicatorsPresent = createBookmark(BookmarkConstants.NO_DANGER_IND_PRESENT,
                        NO_DANGER_IND_PRESENT);
                bookmarkNonFormGrpList.add(bookmarkNoDangerIndicatorsPresent);
            }

            // SAFETY_DECISION - code type: CSFPDCD
            if(!ObjectUtils.isEmpty(dangerIndicatorsDto.getCdSftyDcsn())) {
                BookmarkDto bookmarkOtherDangers = createBookmark(BookmarkConstants.SAFETY_DECISION,
                        lookupDao.simpleDecode(ServiceConstants.CSFPDCD, dangerIndicatorsDto.getCdSftyDcsn()));
                bookmarkNonFormGrpList.add(bookmarkOtherDangers);
            }
        }

        /* Investigation Information */
        if(!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getIndRestraint())){
            BookmarkDto bookmarkNoRestraint = createBookmark(BookmarkConstants.NO_RESTRAINT,
                    NO_RESTRAINT);
            bookmarkNonFormGrpList.add(bookmarkNoRestraint);
        }else{
            for(String restraint : cciInvReportDto.getLicensingInvstDtlDto().getCdLicEmergRestraint()) {
                FormDataGroupDto restraintsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_TYPES_OF_RESTRAINTS,
                        FormConstants.EMPTY_SPACE);
                formDataGroupList.add(restraintsGroup);
                List<BookmarkDto> bookmarkRestraintList = new ArrayList();

                BookmarkDto bookmarkRestraint = createBookmarkWithCodesTable(BookmarkConstants.RESTRAINT,
                        restraint, CodesConstant.CRESTRNT);
                bookmarkRestraintList.add(bookmarkRestraint);

                restraintsGroup.setBookmarkDtoList(bookmarkRestraintList);
            }
        }
        BookmarkDto bookmarkIndChildDeath = null;
        if(!ObjectUtils.isEmpty(cciInvReportDto.getAllegationInvestigationLetterDto())
                && ObjectUtils.isEmpty(cciInvReportDto.getAllegationInvestigationLetterDto().stream()
                .filter(alleg->alleg.getHasChildDeathReportCompleted().equalsIgnoreCase("Y")).collect(Collectors.toList()))){
            bookmarkIndChildDeath = createBookmarkWithCodesTable(BookmarkConstants.IND_CHILD_DEATH,
                    ServiceConstants.NO, CodesConstant.CINVACAN);
            bookmarkNonFormGrpList.add(bookmarkIndChildDeath);

        }else{
            bookmarkIndChildDeath = createBookmarkWithCodesTable(BookmarkConstants.IND_CHILD_DEATH,
                    ServiceConstants.YES, CodesConstant.CINVACAN);
            bookmarkNonFormGrpList.add(bookmarkIndChildDeath);
            FormDataGroupDto chldDeathHdrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_DEATH_HDR,
                    FormConstants.EMPTY_SPACE);
            formDataGroupList.add(chldDeathHdrGroup);
            List<FormDataGroupDto> chdDeathGroupList = new ArrayList<FormDataGroupDto>();
            chldDeathHdrGroup.setFormDataGroupList(chdDeathGroupList);
            for(AllegationInvestigationLetterDto allegationInvestigationLetterDto : cciInvReportDto.getAllegationInvestigationLetterDto()) {
                if(allegationInvestigationLetterDto.getHasChildDeathReportCompleted().equalsIgnoreCase(ServiceConstants.YES)){
                    FormDataGroupDto childDeathGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_DEATH,
                            FormGroupsConstants.TMPLAT_CHILD_DEATH_HDR);
                    chdDeathGroupList.add(childDeathGroup);
                    List<BookmarkDto> bookmarkChildDeathList = new ArrayList();
                    BookmarkDto bookmarkChildName = createBookmark(BookmarkConstants.CHILD_NAME,
                            allegationInvestigationLetterDto.getVictimPersonFullName());
                    bookmarkChildDeathList.add(bookmarkChildName);

                    BookmarkDto bookmarkChkChildDeath = createBookmark(BookmarkConstants.CHK_CHILD_DEATH,
                            ServiceConstants.CHECKED);
                    bookmarkChildDeathList.add(bookmarkChkChildDeath);
                    childDeathGroup.setBookmarkDtoList(bookmarkChildDeathList);
                }
            }
        }
        BookmarkDto bookmarkIllegalOper = createBookmarkWithCodesTable(BookmarkConstants.ILLEGAL_OPERATIONS,
                cciInvReportDto.getLicensingInvstDtlDto().getIndIllegalOperations(), CodesConstant.CINVACAN);
        bookmarkNonFormGrpList.add(bookmarkIllegalOper);

        BookmarkDto bookmarkIndChldAlleg= createBookmarkWithCodesTable(BookmarkConstants.IND_CHLD_ALLEG,
                cciInvReportDto.getLicensingInvstDtlDto().getIndChildInvlvedInAlleg(),CodesConstant.CINVACAN );
        bookmarkNonFormGrpList.add(bookmarkIndChldAlleg);

        if(ServiceConstants.AR_NO.equals(cciInvReportDto.getIndLbtrSxtrAllegationExist())) {
            List<BookmarkDto> bookmarkChildTrafficList = new ArrayList<BookmarkDto>();
            FormDataGroupDto childTrafficGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_TRAFFIC,
                    FormConstants.EMPTY_STRING);
            bookmarkChildTrafficList.add(createBookmark(BookmarkConstants.STAGE_PROGRAM,
                    cciInvReportDto.getGenericCaseInfoDto().getCdStageProgram()));

            if (!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getIndChildSexTraffic())) {
                bookmarkChildTrafficList.add(createBookmarkWithCodesTable(BookmarkConstants.INV_SEX_TRAFFIC,
                        cciInvReportDto.getLicensingInvstDtlDto().getIndChildSexTraffic(), CodesConstant.CINVACAN));
            } else {
                bookmarkChildTrafficList.add(createBookmark(BookmarkConstants.INV_SEX_TRAFFIC,
                        formatTextValue(ServiceConstants.NOT_APPLICABLE)));
            }

            if (!ObjectUtils.isEmpty(cciInvReportDto.getLicensingInvstDtlDto().getIndChildLaborTraffic())) {
                bookmarkChildTrafficList.add(createBookmarkWithCodesTable(BookmarkConstants.INV_LABOR_TRAFFIC,
                        cciInvReportDto.getLicensingInvstDtlDto().getIndChildLaborTraffic(), CodesConstant.CINVACAN));
            } else {
                bookmarkChildTrafficList.add(createBookmark(BookmarkConstants.INV_LABOR_TRAFFIC,
                        formatTextValue(ServiceConstants.NOT_APPLICABLE)));
            }
            childTrafficGroup.setBookmarkDtoList(bookmarkChildTrafficList);
            formDataGroupList.add(childTrafficGroup);
        }

        /* Safety Plan Detail */
        if(!CollectionUtils.isEmpty(cciInvReportDto.getSafetyPlanDtoList())) {
            for (SafetyPlanDto safetyPlanDto : cciInvReportDto.getSafetyPlanDtoList()) {
                FormDataGroupDto safetyPlanGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SAFETY_PLAN,
                        FormConstants.EMPTY_SPACE);
                List<BookmarkDto> bookmarkSafetyPlanList = new ArrayList();
                BookmarkDto bookmarkDateEntered = createBookmark(BookmarkConstants.SP_DATE_ENTERED, DateUtils.stringDt(safetyPlanDto.getCreatedDate()));
                bookmarkSafetyPlanList.add(bookmarkDateEntered);

                BookmarkDto bookmarkSafetyPlanId = createBookmark(BookmarkConstants.SAFETY_PLAN_ID,
                        safetyPlanDto.getId());
                bookmarkSafetyPlanList.add(bookmarkSafetyPlanId);

                BookmarkDto bookmarkSafetyPlanStatus = createBookmarkWithCodesTable(BookmarkConstants.SP_STATUS,
                        safetyPlanDto.getSafetyPlanStatus(), CodesConstant.CSFPLNST);
                bookmarkSafetyPlanList.add(bookmarkSafetyPlanStatus);

                BookmarkDto bookmarkSpOperationName = createBookmark(BookmarkConstants.SP_OPERATION_NAME,
                        safetyPlanDto.getOperationName());
                bookmarkSafetyPlanList.add(bookmarkSpOperationName);

                BookmarkDto bookmarkSpOperationNumber = createBookmark(BookmarkConstants.SP_OPERATION_NUMBER,
                        safetyPlanDto.getOperationNumber());
                bookmarkSafetyPlanList.add(bookmarkSpOperationNumber);

                BookmarkDto bookmarkImpactFacilityType = createBookmarkWithCodesTable(BookmarkConstants.SP_IMPACT_FACILITY_TYPE,
                        safetyPlanDto.getImpactFacilityType(), CodesConstant.CFACTYP2);
                bookmarkSafetyPlanList.add(bookmarkImpactFacilityType);

                BookmarkDto bookmarkSpEffectiveDate = createBookmark(BookmarkConstants.SP_EFFECTIVE_DATE,
                        DateUtils.stringDt(safetyPlanDto.getEffectiveDate()));
                bookmarkSafetyPlanList.add(bookmarkSpEffectiveDate);

                safetyPlanGroup.setBookmarkDtoList(bookmarkSafetyPlanList);
                formDataGroupList.add(safetyPlanGroup);

            }
        }else{
            FormDataGroupDto safetyPlanGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SAFETY_PLAN,
                    FormConstants.EMPTY_SPACE);
            List<BookmarkDto> bookmarkSafetyPlanList = new ArrayList();
            BookmarkDto bookmarkDateEntered = createBookmark(BookmarkConstants.SP_DATE_ENTERED, ServiceConstants.NOT_APPLICABLE);
            bookmarkSafetyPlanList.add(bookmarkDateEntered);

            BookmarkDto bookmarkSafetyPlanId = createBookmark(BookmarkConstants.SAFETY_PLAN_ID,
                    ServiceConstants.NOT_APPLICABLE);
            bookmarkSafetyPlanList.add(bookmarkSafetyPlanId);

            BookmarkDto bookmarkSafetyPlanStatus = createBookmark(BookmarkConstants.SP_STATUS,
                    ServiceConstants.NOT_APPLICABLE);
            bookmarkSafetyPlanList.add(bookmarkSafetyPlanStatus);

            BookmarkDto bookmarkSpOperationName = createBookmark(BookmarkConstants.SP_OPERATION_NAME,
                    ServiceConstants.NOT_APPLICABLE);
            bookmarkSafetyPlanList.add(bookmarkSpOperationName);

            BookmarkDto bookmarkSpOperationNumber = createBookmark(BookmarkConstants.SP_OPERATION_NUMBER,
                    ServiceConstants.NOT_APPLICABLE);
            bookmarkSafetyPlanList.add(bookmarkSpOperationNumber);

            BookmarkDto bookmarkImpactFacilityType = createBookmark(BookmarkConstants.SP_IMPACT_FACILITY_TYPE,
                    ServiceConstants.NOT_APPLICABLE);
            bookmarkSafetyPlanList.add(bookmarkImpactFacilityType);

            BookmarkDto bookmarkSpEffectiveDate = createBookmark(BookmarkConstants.SP_EFFECTIVE_DATE,
                    ServiceConstants.NOT_APPLICABLE);
            bookmarkSafetyPlanList.add(bookmarkSpEffectiveDate);

            safetyPlanGroup.setBookmarkDtoList(bookmarkSafetyPlanList);
            formDataGroupList.add(safetyPlanGroup);
        }

        // Person List - Reporter

        for (CciInvReportPersonDto personPricipalDto : cciInvReportDto.getInvReporterInfoList()) {
            FormDataGroupDto reporterGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_REPORTER,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(reporterGroup);
            List<BookmarkDto> bookmarkReporterList = new ArrayList<BookmarkDto>();

            BookmarkDto bookmarkCdStagePersRelInt = createBookmarkWithCodesTable(
                    BookmarkConstants.REPORTER_RELATIONSHIP,
                    personPricipalDto.getCdStagePersRelInt(),
                    CodesConstant.CRPTRINT);
            bookmarkReporterList.add(bookmarkCdStagePersRelInt);
            BookmarkDto bookmarkCdPersonSex = createBookmarkWithCodesTable(BookmarkConstants.REPORTER_SEX,
                    personPricipalDto.getCdPersonSex(), CodesConstant.CSEX);
            bookmarkReporterList.add(bookmarkCdPersonSex);

                BookmarkDto bookmarkRepPersonFull = createBookmark(BookmarkConstants.REPORTER_NAME,
                    REPORTER.equalsIgnoreCase(personPricipalDto.getNmPersonFull())
                                ? ServiceConstants.EMPTY_STRING
                                : personPricipalDto.getNmPersonFull());
                bookmarkReporterList.add(bookmarkRepPersonFull);
            BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.REPORTER_PID,
                    personPricipalDto.getIdPerson());
                bookmarkReporterList.add(bookmarkIdPerson);
            if (!ObjectUtils.isEmpty(personPricipalDto.getTxtStagePersNote())) {
                BookmarkDto bookmarkTxtStagePersNote = createBookmark(BookmarkConstants.REPORTER_NOTES,
                        personPricipalDto.getTxtStagePersNote().replace("<br>",
                                ". ").replace("\n", " "));
                bookmarkReporterList.add(bookmarkTxtStagePersNote);
            }

            reporterGroup.setBookmarkDtoList(bookmarkReporterList);
        }

        // populate Person List, Principals section.

        for (CciInvReportPersonDto cciInvReportPersonDto : cciInvReportDto.getPrincipalsList()) {
            FormDataGroupDto principalGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PRINCIPALS,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(principalGroup);

            List<FormDataGroupDto> principalsGroupList = new ArrayList<FormDataGroupDto>();
            List<BookmarkDto> bookmarkPrincipalList = new ArrayList<BookmarkDto>();

            BookmarkDto bookmarkRepPersonFull = createBookmark(BookmarkConstants.PRN_NAME,
                    cciInvReportPersonDto.getNmPersonFull());
            bookmarkPrincipalList.add(bookmarkRepPersonFull);
            BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.PRN_PERSON_ID,
                    cciInvReportPersonDto.getIdPerson());
            bookmarkPrincipalList.add(bookmarkIdPerson);
            BookmarkDto bookmarkSsn = createBookmark(BookmarkConstants.PRN_SSN, cciInvReportPersonDto.getNbrPersId());
            bookmarkPrincipalList.add(bookmarkSsn);
            BookmarkDto bookmarkCdStagePersRelInt = createBookmarkWithCodesTable(BookmarkConstants.PRN_RELATIONSHIP,
                    cciInvReportPersonDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
            bookmarkPrincipalList.add(bookmarkCdStagePersRelInt);
            BookmarkDto bookmarkCdStagePersRole = createBookmarkWithCodesTable(BookmarkConstants.PRN_STAGE_ROLE,
                    cciInvReportPersonDto.getCdStagePersRole(), CodesConstant.CROLEALL);
            bookmarkPrincipalList.add(bookmarkCdStagePersRole);

            BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.PRN_DOB,
                    DateUtils.stringDt(cciInvReportPersonDto.getDtPersonBirth()));
            bookmarkPrincipalList.add(bookmarkDtPersonBirth);
            BookmarkDto bookmarkDtPersonDeath = createBookmark(BookmarkConstants.PRN_DOD,
                    DateUtils.stringDt(cciInvReportPersonDto.getDtPersonDeath()));
            bookmarkPrincipalList.add(bookmarkDtPersonDeath);

            BookmarkDto bookmarkPrnAge = createBookmark(BookmarkConstants.PRN_AGE,
                    cciInvReportPersonDto.getNbrPersonAge());
            bookmarkPrincipalList.add(bookmarkPrnAge);
            BookmarkDto bookmarkPrnDeathCode = createBookmarkWithCodesTable(BookmarkConstants.PRN_DEATH_CODE,
                    cciInvReportPersonDto.getCdPersonDeath(), CodesConstant.CRSNDTH2);
            bookmarkPrincipalList.add(bookmarkPrnDeathCode);
            BookmarkDto bookmarkPrnSex = createBookmarkWithCodesTable(BookmarkConstants.PRN_SEX,
                    cciInvReportPersonDto.getCdPersonSex(), CodesConstant.CSEX);
            bookmarkPrincipalList.add(bookmarkPrnSex);
            BookmarkDto bookmarkPrnDtLegalStatus = createBookmark(BookmarkConstants.PRN_DT_LEGAL_STATUS,
                    DateUtils.stringDt(cciInvReportPersonDto.getDtLegalStatus()));
            bookmarkPrincipalList.add(bookmarkPrnDtLegalStatus);
            BookmarkDto bookmarkPrnLegalStatus = createBookmarkWithCodesTable(BookmarkConstants.PRN_LEGAL_STATUS,
                    cciInvReportPersonDto.getCdLegalStatus(), CodesConstant.CLEGSTAT);
            bookmarkPrincipalList.add(bookmarkPrnLegalStatus);

            BookmarkDto bookmarkPrnRace = createBookmark(BookmarkConstants.PRN_RACE, cciInvReportPersonDto.getPersRace());
            bookmarkPrincipalList.add(bookmarkPrnRace);
            BookmarkDto bookmarkPrnEthnicity = createBookmarkWithCodesTable(BookmarkConstants.PRN_ETHNICITY,
                    cciInvReportPersonDto.getCdEthn(), CodesConstant.CINDETHN);
            bookmarkPrincipalList.add(bookmarkPrnEthnicity);
            BookmarkDto bookmarkPrnAddrLn1 = createBookmark(BookmarkConstants.PRN_ADDR_LINE_1,
                    cciInvReportPersonDto.getAddrPersStLn1());
            bookmarkPrincipalList.add(bookmarkPrnAddrLn1);
            BookmarkDto bookmarkPrnAddrCity = createBookmark(BookmarkConstants.PRN_ADDR_CITY,
                    cciInvReportPersonDto.getAddrPersCity());
            bookmarkPrincipalList.add(bookmarkPrnAddrCity);
            BookmarkDto bookmarkPrnAddrSt = createBookmarkWithCodesTable(BookmarkConstants.PRN_ADDR_ST,
                    cciInvReportPersonDto.getCdPersState(), CodesConstant.CSTATE);
            bookmarkPrincipalList.add(bookmarkPrnAddrSt);
            BookmarkDto bookmarkPrnAddrZip = createBookmark(BookmarkConstants.PRN_ADDR_ZIP,
                    cciInvReportPersonDto.getPersZip());
            bookmarkPrincipalList.add(bookmarkPrnAddrZip);
            principalGroup.setBookmarkDtoList(bookmarkPrincipalList);
            principalGroup.setFormDataGroupList(principalsGroupList);

            // no characteristics group
            if (ServiceConstants.TWO.equals(cciInvReportPersonDto.getCdPersChar())) {
                FormDataGroupDto noCharGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_NONE,
                        FormGroupsConstants.TMPLAT_PRINCIPALS);
                principalsGroupList.add(noCharGroupDto);
            } else if(CollectionUtils.isEmpty(cciInvReportDto.getCharacteristicsDtoList())
             || checkIfAnyCharExists(cciInvReportDto.getCharacteristicsDtoList(), cciInvReportPersonDto.getIdPerson())){
                FormDataGroupDto noRecordGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_NO_RECORD,
                        FormGroupsConstants.TMPLAT_PRINCIPALS);
                principalsGroupList.add(noRecordGroupDto);
            }

            // characteristics groups
            else {
                boolean hasAtleastOnceCharacteristic = false;
                for (CharacteristicsDto charDto : cciInvReportDto.getCharacteristicsDtoList()) {
                    // investigation group

                    if (ServiceConstants.CCH.equals(charDto.getCdCharacCategory()) && cciInvReportPersonDto.getIdPerson().equals(charDto.getIdpersonId())
                            && !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
                        FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_CCH,
                                FormGroupsConstants.TMPLAT_PRINCIPALS);
                        List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
                        BookmarkDto bookmarkCatCch = createBookmarkWithCodesTable(BookmarkConstants.PRN_CAT_CCH,
                                charDto.getCdCharacCategory(), CodesConstant.CCHRTCAT);
                        bookmarkChildInvList.add(bookmarkCatCch);
                        BookmarkDto bookmarkCharCch = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHAR_CCH,
                                charDto.getCdCharacCode(), CodesConstant.CCH);
                        bookmarkChildInvList.add(bookmarkCharCch);
                        BookmarkDto bookmarkCharCchStatus = createBookmarkWithCodesTable(
                                BookmarkConstants.PRN_CHAR_CCH_STATUS, charDto.getCdStatus(), CodesConstant.CHARSTAT);
                        bookmarkChildInvList.add(bookmarkCharCchStatus);

                        childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
                        principalsGroupList.add(childInvGroupDto);
                        hasAtleastOnceCharacteristic = true;
                    }

                    // placement group
                    else if (ServiceConstants.CPL.equals(charDto.getCdCharacCategory()) && cciInvReportPersonDto.getIdPerson().equals(charDto.getIdpersonId())
                            && !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
                        FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_CPL,
                                FormGroupsConstants.TMPLAT_PRINCIPALS);
                        List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
                        BookmarkDto bookmarkCatCpl = createBookmarkWithCodesTable(BookmarkConstants.PRN_CAT_CPL,
                                charDto.getCdCharacCategory(), CodesConstant.CCHRTCAT);
                        bookmarkChildInvList.add(bookmarkCatCpl);
                        BookmarkDto bookmarkCharCpl = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHAR_CPL,
                                charDto.getCdCharacCode(), CodesConstant.CPL);
                        bookmarkChildInvList.add(bookmarkCharCpl);
                        BookmarkDto bookmarkCharCplStatus = createBookmarkWithCodesTable(
                                BookmarkConstants.PRN_CHAR_CPL_STATUS, charDto.getCdStatus(), CodesConstant.CHARSTAT);
                        bookmarkChildInvList.add(bookmarkCharCplStatus);

                        childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
                        principalsGroupList.add(childInvGroupDto);
                        hasAtleastOnceCharacteristic = true;
                    }

                    // caretaker group
                    else if (ServiceConstants.CASE_SPECIAL_REQUEST_TLETS.equals(charDto.getCdCharacCategory()) && cciInvReportPersonDto.getIdPerson().equals(charDto.getIdpersonId())
                            && !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
                        FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_CCT,
                                FormGroupsConstants.TMPLAT_PRINCIPALS);
                        List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
                        BookmarkDto bookmarkCatCct = createBookmarkWithCodesTable(BookmarkConstants.PRN_CAT_CCT,
                                charDto.getCdCharacCategory(), CodesConstant.CCHRTCAT);
                        bookmarkChildInvList.add(bookmarkCatCct);
                        BookmarkDto bookmarkCharCct = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHAR_CCT,
                                charDto.getCdCharacCode(), CodesConstant.CCT);
                        bookmarkChildInvList.add(bookmarkCharCct);
                        BookmarkDto bookmarkCharCctStatus = createBookmarkWithCodesTable(
                                BookmarkConstants.PRN_CHAR_CCT_STATUS, charDto.getCdStatus(), CodesConstant.CHARSTAT);
                        bookmarkChildInvList.add(bookmarkCharCctStatus);
                        childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
                        principalsGroupList.add(childInvGroupDto);
                        hasAtleastOnceCharacteristic = true;
                    }

                    // aps group
                    else if (ServiceConstants.APS_CHARACTERISTIC.equals(charDto.getCdCharacCategory()) && cciInvReportPersonDto.getIdPerson().equals(charDto.getIdpersonId())
                            && !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
                        FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_CAP,
                                FormGroupsConstants.TMPLAT_PRINCIPALS);
                        List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
                        BookmarkDto bookmarkCatCap = createBookmarkWithCodesTable(BookmarkConstants.PRN_CAT_CAP,
                                charDto.getCdCharacCategory(), CodesConstant.CCHRTCAT);
                        bookmarkChildInvList.add(bookmarkCatCap);
                        BookmarkDto bookmarkCharCap = createBookmarkWithCodesTable(BookmarkConstants.PRN_CHAR_CAP,
                                charDto.getCdCharacCode(), CodesConstant.CAP);
                        bookmarkChildInvList.add(bookmarkCharCap);
                        BookmarkDto bookmarkCharCapStatus = createBookmarkWithCodesTable(
                                BookmarkConstants.PRN_CHAR_CAP_STATUS, charDto.getCdStatus(), CodesConstant.CHARSTAT);
                        bookmarkChildInvList.add(bookmarkCharCapStatus);

                        childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
                        principalsGroupList.add(childInvGroupDto);
                        hasAtleastOnceCharacteristic = true;
                    }
                }
                //afcars
                if(hasAtleastOnceCharacteristic){
                    for(AfcarsDto afcarsDto : cciInvReportDto.getPrnAfcarsDto()){
                        if (cciInvReportPersonDto.getIdPerson().equals(afcarsDto.getIdPerson())) {
                            FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CHAR_AFCARS,
                                    FormGroupsConstants.TMPLAT_PRINCIPALS);
                            List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
                            BookmarkDto bookmarkCatCap = createBookmark(BookmarkConstants.PRN_CAT_AFCARS,
                                    "AFCARS Disability");
                            bookmarkChildInvList.add(bookmarkCatCap);
                            BookmarkDto bookmarkCharCap = createBookmark(BookmarkConstants.PRN_CHAR_AFCARS,
                                    Integer.parseInt(cciInvReportPersonDto.getNbrPersonAge()) < 18 ?"Is the child disabled?" : "Was disabled as a child?");
                            bookmarkChildInvList.add(bookmarkCharCap);
                            BookmarkDto bookmarkCharCapStatus = createBookmarkWithCodesTable(
                                    BookmarkConstants.PRN_CHAR_AFCARS_STATUS, afcarsDto.getCdResponse(), CodesConstant.AFCRSDBL);
                            bookmarkChildInvList.add(bookmarkCharCapStatus);

                            childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
                            principalsGroupList.add(childInvGroupDto);
                        }
                    }
                }
            }
        }

        // populate Collaterals section. populateCollaterals


        for (CciInvReportPersonDto colDto : cciInvReportDto.getCollateralList()) {

            FormDataGroupDto colGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COLLATERALS,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(colGroupDto);
            List<FormDataGroupDto> collateralGroupList = new ArrayList<FormDataGroupDto>();
            List<BookmarkDto> bookmarkCollateralList = new ArrayList<BookmarkDto>();

            BookmarkDto bookmarkRepPersonFull = createBookmark(BookmarkConstants.COL_NAME,
                    colDto.getNmPersonFull());
            bookmarkCollateralList.add(bookmarkRepPersonFull);
            BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.COL_PERSON_ID,
                    colDto.getIdPerson());
            bookmarkCollateralList.add(bookmarkIdPerson);
            BookmarkDto bookmarkSsn = createBookmark(BookmarkConstants.COL_SSN, colDto.getNbrPersId());
            bookmarkCollateralList.add(bookmarkSsn);
            BookmarkDto bookmarkCdStagePersRelInt = createBookmarkWithCodesTable(BookmarkConstants.COL_RELATIONSHIP,
                    colDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
            bookmarkCollateralList.add(bookmarkCdStagePersRelInt);
            BookmarkDto bookmarkCdStagePersRole = createBookmarkWithCodesTable(BookmarkConstants.COL_STAGE_ROLE,
                    colDto.getCdStagePersRole(), CodesConstant.CROLEALL);
            bookmarkCollateralList.add(bookmarkCdStagePersRole);

            BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.COL_DOB,
                    DateUtils.stringDt(colDto.getDtPersonBirth()));
            bookmarkCollateralList.add(bookmarkDtPersonBirth);
            BookmarkDto bookmarkDtPersonDeath = createBookmark(BookmarkConstants.COL_DOD,
                    DateUtils.stringDt(colDto.getDtPersonDeath()));
            bookmarkCollateralList.add(bookmarkDtPersonDeath);

            BookmarkDto bookmarkPrnAge = createBookmark(BookmarkConstants.COL_AGE,
                    colDto.getNbrPersonAge());
            bookmarkCollateralList.add(bookmarkPrnAge);
            BookmarkDto bookmarkPrnDeathCode = createBookmarkWithCodesTable(BookmarkConstants.COL_DEATH_CODE,
                    colDto.getCdPersonDeath(), CodesConstant.CRSNDTH2);
            bookmarkCollateralList.add(bookmarkPrnDeathCode);
            BookmarkDto bookmarkPrnSex = createBookmarkWithCodesTable(BookmarkConstants.COL_SEX,
                    colDto.getCdPersonSex(), CodesConstant.CSEX);
            bookmarkCollateralList.add(bookmarkPrnSex);
            BookmarkDto bookmarkPrnDtLegalStatus = createBookmark(BookmarkConstants.COL_DT_LEGAL_STATUS,
                    DateUtils.stringDt(colDto.getDtLegalStatus()));
            bookmarkCollateralList.add(bookmarkPrnDtLegalStatus);
            BookmarkDto bookmarkPrnLegalStatus = createBookmarkWithCodesTable(BookmarkConstants.COL_LEGAL_STATUS,
                    colDto.getCdLegalStatus(), CodesConstant.CLEGSTAT);
            bookmarkCollateralList.add(bookmarkPrnLegalStatus);

            BookmarkDto bookmarkPrnRace = createBookmark(BookmarkConstants.COL_RACE, colDto.getPersRace());
            bookmarkCollateralList.add(bookmarkPrnRace);
            BookmarkDto bookmarkPrnEthnicity = createBookmarkWithCodesTable(BookmarkConstants.COL_ETHNICITY,
                    colDto.getCdEthn(), CodesConstant.CINDETHN);
            bookmarkCollateralList.add(bookmarkPrnEthnicity);
            BookmarkDto bookmarkPrnAddrLn1 = createBookmark(BookmarkConstants.COL_ADDR_LINE_1,
                    colDto.getAddrPersStLn1());
            bookmarkCollateralList.add(bookmarkPrnAddrLn1);
            BookmarkDto bookmarkPrnAddrCity = createBookmark(BookmarkConstants.COL_ADDR_CITY,
                    colDto.getAddrPersCity());
            bookmarkCollateralList.add(bookmarkPrnAddrCity);
            BookmarkDto bookmarkPrnAddrSt = createBookmarkWithCodesTable(BookmarkConstants.COL_ADDR_ST,
                    colDto.getCdPersState(), CodesConstant.CSTATE);
            bookmarkCollateralList.add(bookmarkPrnAddrSt);
            BookmarkDto bookmarkPrnAddrZip = createBookmark(BookmarkConstants.COL_ADDR_ZIP,
                    colDto.getPersZip());
            bookmarkCollateralList.add(bookmarkPrnAddrZip);
            colGroupDto.setBookmarkDtoList(bookmarkCollateralList);
            colGroupDto.setFormDataGroupList(collateralGroupList);

            // no characteristics group
            if (ServiceConstants.TWO.equals(colDto.getCdPersChar())) {
                FormDataGroupDto noCharGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COL_CHAR_NONE,
                        FormGroupsConstants.TMPLAT_COLLATERALS);
                collateralGroupList.add(noCharGroupDto);
            } else if (CollectionUtils.isEmpty(cciInvReportDto.getColCharacteristicsDtoList())
                    || checkIfAnyCharExists(cciInvReportDto.getColCharacteristicsDtoList(), colDto.getIdPerson())) {
                FormDataGroupDto noRecordGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COL_CHAR_NO_RECORDS,
                        FormGroupsConstants.TMPLAT_COLLATERALS);
                collateralGroupList.add(noRecordGroupDto);
            }

            // characteristics groups
            else {
                boolean hasAtleastOnceCharacteristic = false;
                for (CharacteristicsDto charDto : cciInvReportDto.getColCharacteristicsDtoList()) {
                    // investigation group

                    if (ServiceConstants.CCH.equals(charDto.getCdCharacCategory()) && colDto.getIdPerson().equals(charDto.getIdpersonId())
                            && !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
                        FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COL_CHAR_CCH,
                                FormGroupsConstants.TMPLAT_COLLATERALS);
                        List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
                        BookmarkDto bookmarkCatCch = createBookmarkWithCodesTable(BookmarkConstants.COL_CAT_CCH,
                                charDto.getCdCharacCategory(), CodesConstant.CCHRTCAT);
                        bookmarkChildInvList.add(bookmarkCatCch);
                        BookmarkDto bookmarkCharCch = createBookmarkWithCodesTable(BookmarkConstants.COL_CHAR_CCH,
                                charDto.getCdCharacCode(), CodesConstant.CCH);
                        bookmarkChildInvList.add(bookmarkCharCch);
                        BookmarkDto bookmarkCharCchStatus = createBookmarkWithCodesTable(
                                BookmarkConstants.COL_CHAR_CCH_STATUS, charDto.getCdStatus(), CodesConstant.CHARSTAT);
                        bookmarkChildInvList.add(bookmarkCharCchStatus);

                        childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
                        collateralGroupList.add(childInvGroupDto);
                        hasAtleastOnceCharacteristic = true;
                    }

                    // placement group
                    else if (ServiceConstants.CPL.equals(charDto.getCdCharacCategory()) && colDto.getIdPerson().equals(charDto.getIdpersonId())
                            && !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
                        FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COL_CHAR_CPL,
                                FormGroupsConstants.TMPLAT_COLLATERALS);
                        List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
                        BookmarkDto bookmarkCatCpl = createBookmarkWithCodesTable(BookmarkConstants.COL_CAT_CPL,
                                charDto.getCdCharacCategory(), CodesConstant.CCHRTCAT);
                        bookmarkChildInvList.add(bookmarkCatCpl);
                        BookmarkDto bookmarkCharCpl = createBookmarkWithCodesTable(BookmarkConstants.COL_CHAR_CPL,
                                charDto.getCdCharacCode(), CodesConstant.CPL);
                        bookmarkChildInvList.add(bookmarkCharCpl);
                        BookmarkDto bookmarkCharCplStatus = createBookmarkWithCodesTable(
                                BookmarkConstants.COL_CHAR_CPL_STATUS, charDto.getCdStatus(), CodesConstant.CHARSTAT);
                        bookmarkChildInvList.add(bookmarkCharCplStatus);

                        childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
                        collateralGroupList.add(childInvGroupDto);
                        hasAtleastOnceCharacteristic = true;
                    }

                    // caretaker group
                    else if (ServiceConstants.CASE_SPECIAL_REQUEST_TLETS.equals(charDto.getCdCharacCategory()) && colDto.getIdPerson().equals(charDto.getIdpersonId())
                            && !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
                        FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COL_CHAR_CCT,
                                FormGroupsConstants.TMPLAT_COLLATERALS);
                        List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
                        BookmarkDto bookmarkCatCct = createBookmarkWithCodesTable(BookmarkConstants.COL_CAT_CCT,
                                charDto.getCdCharacCategory(), CodesConstant.CCHRTCAT);
                        bookmarkChildInvList.add(bookmarkCatCct);
                        BookmarkDto bookmarkCharCct = createBookmarkWithCodesTable(BookmarkConstants.COL_CHAR_CCT,
                                charDto.getCdCharacCode(), CodesConstant.CCT);
                        bookmarkChildInvList.add(bookmarkCharCct);

                        childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
                        collateralGroupList.add(childInvGroupDto);
                        hasAtleastOnceCharacteristic = true;
                    }

                    // aps group
                    else if (ServiceConstants.APS_CHARACTERISTIC.equals(charDto.getCdCharacCategory()) && colDto.getIdPerson().equals(charDto.getIdpersonId())
                            && !ObjectUtils.isEmpty(charDto.getCdCharacCode())) {
                        FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COL_CHAR_CAP,
                                FormGroupsConstants.TMPLAT_COLLATERALS);
                        List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
                        BookmarkDto bookmarkCatCap = createBookmarkWithCodesTable(BookmarkConstants.COL_CAT_CAP,
                                charDto.getCdCharacCategory(), CodesConstant.CCHRTCAT);
                        bookmarkChildInvList.add(bookmarkCatCap);
                        BookmarkDto bookmarkCharCap = createBookmarkWithCodesTable(BookmarkConstants.COL_CHAR_CAP,
                                charDto.getCdCharacCode(), CodesConstant.CAP);
                        bookmarkChildInvList.add(bookmarkCharCap);

                        childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
                        collateralGroupList.add(childInvGroupDto);
                        hasAtleastOnceCharacteristic = true;
                    }
                }
                //afcars
                if(hasAtleastOnceCharacteristic){
                    for(AfcarsDto afcarsDto : cciInvReportDto.getColAfcarsDto()) {
                        if (colDto.getIdPerson().equals(afcarsDto.getIdPerson())) {
                            FormDataGroupDto childInvGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COL_CHAR_AFCARS,
                                    FormGroupsConstants.TMPLAT_COLLATERALS);
                            List<BookmarkDto> bookmarkChildInvList = new ArrayList<BookmarkDto>();
                            BookmarkDto bookmarkCatCap = createBookmark(BookmarkConstants.COL_CAT_AFCARS,
                                    "AFCARS Disability");
                            bookmarkChildInvList.add(bookmarkCatCap);
                            BookmarkDto bookmarkCharCap = createBookmark(BookmarkConstants.COL_CHAR_AFCARS,
                                    Integer.parseInt(colDto.getNbrPersonAge()) < 18 ? "Is the child disabled?" : "Was disabled as a child?");
                            bookmarkChildInvList.add(bookmarkCharCap);
                            BookmarkDto bookmarkCharCapStatus = createBookmarkWithCodesTable(
                                    BookmarkConstants.COL_CHAR_AFCARS_STATUS, afcarsDto.getCdResponse(), CodesConstant.AFCRSDBL);
                            bookmarkChildInvList.add(bookmarkCharCapStatus);

                            childInvGroupDto.setBookmarkDtoList(bookmarkChildInvList);
                            collateralGroupList.add(childInvGroupDto);
                        }
                    }
                }
            }
        }
        /* Investigation Contacts */
        for (CciInvContactDto contactDto : cciInvReportDto.getContactsList()) {
            FormDataGroupDto contactsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(contactsGroup);
            List<FormDataGroupDto> contactGroupList = new ArrayList<FormDataGroupDto>();
            List<BookmarkDto> bookmarkContactsConList = new ArrayList<BookmarkDto>();
            List<BlobDataDto> blobList = new ArrayList<BlobDataDto>();

            BookmarkDto bookmarkCdStage = createBookmark(BookmarkConstants.CONTACTS_CON_CDSTAGE,
                    contactDto.getCdStage());
            bookmarkContactsConList.add(bookmarkCdStage);

            BookmarkDto bookmarkDtOccured = createBookmark(BookmarkConstants.CONTACTS_CON_OCCURRED,
                    DateUtils.stringDt(contactDto.getDtContactOccurred()) + ServiceConstants.SPACE
                            + DateUtils.getTime(contactDto.getDtContactOccurred()));
            bookmarkContactsConList.add(bookmarkDtOccured);
            BookmarkDto bookmarkConBy = createBookmark(BookmarkConstants.CONTACTS_CON_BY,
                    contactDto.getNmPersonFull());
            bookmarkContactsConList.add(bookmarkConBy);

            BookmarkDto bookmarkType = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_CON_TYPE,
                    contactDto.getCdContactType(), CodesConstant.CCNTCTYP);
            bookmarkContactsConList.add(bookmarkType);

            BookmarkDto bookmarkPurpose = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_CON_PURPOSE,
                    contactDto.getCdContactPurpose(), CodesConstant.CCNTPURP);
            bookmarkContactsConList.add(bookmarkPurpose);

            BookmarkDto bookmarkMethod = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_CON_METHOD,
                    contactDto.getCdContactMethod(), CodesConstant.CCNTMETH);
            bookmarkContactsConList.add(bookmarkMethod);

            BookmarkDto bookmarkLocation = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_CON_LOCATION,
                    contactDto.getCdContactLocation(), CodesConstant.CCNCTLOC);
            bookmarkContactsConList.add(bookmarkLocation);

            if (ServiceConstants.YES.equalsIgnoreCase(contactDto.getIndContactAttempted())) {
                bookmarkContactsConList.add(createBookmark(BookmarkConstants.CONTACTS_CON_ATTEMPTED,
                        ServiceConstants.CHECKED));
            }

            BookmarkDto bookmarkOthers = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_CON_OTHERS,
                    contactDto.getCdContactOthers(), CodesConstant.COTHCNCT);
            bookmarkContactsConList.add(bookmarkOthers);

            if(!ObjectUtils.isEmpty(contactDto.getPrincCollatList())){

                FormDataGroupDto princCollatHdrGroupDto = createFormDataGroup(FormGroupsConstants.TEMP_PRINC_COLLAT,
                        FormGroupsConstants.TMPLAT_CONTACTS);
                contactGroupList.add(princCollatHdrGroupDto);
                List<FormDataGroupDto> prnCollatGroupList = new ArrayList<FormDataGroupDto>();
                princCollatHdrGroupDto.setFormDataGroupList(prnCollatGroupList);
                for(CciInvReportPersonDto princCollatDto : contactDto.getPrincCollatList()){
                    FormDataGroupDto princCollatGroupDto = createFormDataGroup(FormGroupsConstants.TEMP_CON_PRINC_COLLAT,
                            FormGroupsConstants.TEMP_PRINC_COLLAT);
                    List<BookmarkDto> bookmarkPrincCollatList = new ArrayList<BookmarkDto>();
                    BookmarkDto bookmarkPrincCollatName = createBookmark(BookmarkConstants.PRINC_COLLAT_NAME,
                            princCollatDto.getNmPersonFull());
                    bookmarkPrincCollatList.add(bookmarkPrincCollatName);

                    BookmarkDto bookmarkPrincCollatType = createBookmarkWithCodesTable(BookmarkConstants.PRINC_COLLAT_TYPE,
                            princCollatDto.getCdStagePersType(), CodesConstant.CPRSNTYP);
                    bookmarkPrincCollatList.add(bookmarkPrincCollatType);

                    BookmarkDto bookmarkPrincCollatRole = createBookmarkWithCodesTable(BookmarkConstants.PRINC_COLLAT_ROLE,
                            princCollatDto.getCdStagePersRole(), CodesConstant.CROLEALL);
                    bookmarkPrincCollatList.add(bookmarkPrincCollatRole);

                    BookmarkDto bookmarkPrincCollatRel = createBookmarkWithCodesTable(BookmarkConstants.PRINC_COLLAT_REL,
                            princCollatDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
                    bookmarkPrincCollatList.add(bookmarkPrincCollatRel);

                    princCollatGroupDto.setBookmarkDtoList(bookmarkPrincCollatList);
                    prnCollatGroupList.add(princCollatGroupDto);
                }

            }

           if (!ObjectUtils.isEmpty(contactDto.getNarrative())) {
                BookmarkDto bookmarkNarrLabel = createBookmark(BookmarkConstants.CONTACTS_CON_NARRATIVELABEL,
                        ServiceConstants.CONTACTS_CON_NARRATIVELABELTEXT);
                bookmarkContactsConList.add(bookmarkNarrLabel);
            }

            BlobDataDto blobContactsNarr = createBlobData(BookmarkConstants.CONTACTS_CON_NARRATIVE,
                    BookmarkConstants.CONTACT_NARRATIVE, contactDto.getIdEvent().toString());
            blobList.add(blobContactsNarr);

            contactsGroup.setBookmarkDtoList(bookmarkContactsConList);
            contactsGroup.setFormDataGroupList(contactGroupList);
            contactsGroup.setBlobDataDtoList(blobList);
        }


        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);
        preFillData.setBlobDataDtoList(bookmarkBlobDataList);

        return preFillData;
    }

    private boolean checkIfAnyCharExists(List<CharacteristicsDto> characteristicsDtoList, Long idPerson) {
        Optional<CharacteristicsDto> dto = null;
        if(!CollectionUtils.isEmpty(characteristicsDtoList)){
           dto = characteristicsDtoList.stream().filter(c -> c.getIdpersonId().equals(idPerson)).findFirst();
        }
        return  !dto.isPresent();
    }

    private String populateName(String firstName,String lastName,String middleName)
    {
        StringBuilder concatName=new StringBuilder();
        if(!ObjectUtils.isEmpty(lastName))
        {
            concatName.append(lastName);
            concatName.append(ServiceConstants.SPACE);
            concatName.append(ServiceConstants.COMMA);
            concatName.append(ServiceConstants.SPACE);
        }
        if(!ObjectUtils.isEmpty(firstName))
        {
            concatName.append(firstName);
        }
        if(!ObjectUtils.isEmpty(middleName))
        {
            concatName.append(ServiceConstants.SPACE);
            concatName.append(middleName);
        }

        return concatName.toString();
    }

    public String formatTextValue(String txtToFormat) {
        String[] txtConcurrently = null;
        if (!ObjectUtils.isEmpty(txtToFormat)) {
            txtConcurrently = txtToFormat.split("\n\n");
        }
        StringBuffer txtConcrBuf = new StringBuffer();
        if (!ObjectUtils.isEmpty(txtConcurrently)) {
            for (String txtConcr : txtConcurrently) {
                txtConcrBuf.append(ServiceConstants.FORM_SPACE+ServiceConstants.FORM_SPACE+ServiceConstants.FORM_SPACE+ServiceConstants.FORM_SPACE+
                        ServiceConstants.FORM_SPACE+ServiceConstants.FORM_SPACE);
                txtConcrBuf.append(txtConcr);
                txtConcrBuf.append("<br/><br/>");
            }
        }
        return txtConcrBuf.toString();
    }
}
