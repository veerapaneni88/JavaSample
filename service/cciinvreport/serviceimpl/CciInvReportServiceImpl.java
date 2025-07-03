package us.tx.state.dfps.service.cciinvreport.serviceimpl;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.AfcarsResponse;
import us.tx.state.dfps.common.domain.Allegation;
import us.tx.state.dfps.common.domain.AllegedSxVctmztn;
import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.CvsNotifLog;
import us.tx.state.dfps.common.domain.SafetyPlan;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.notiftolawenforcement.dto.PriorStageDto;
import us.tx.state.dfps.service.admin.dao.AllegationStageCaseDao;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.AllegationStageCaseInDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageCaseOutDto;
import us.tx.state.dfps.service.arreport.dao.ArReportDao;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.casepackage.serviceimpl.CaseSummaryServiceImpl;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvAllegDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvContactDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvIntakeDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvIntakePersonDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvReportDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvReportPersonDto;
import us.tx.state.dfps.service.cciinvreport.dao.CciInvReportDao;
import us.tx.state.dfps.service.cciinvreport.service.CciInvReportService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventFetDao;
import us.tx.state.dfps.service.common.dao.SafetyPlanDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.WorkingNarrativeDao;
import us.tx.state.dfps.service.common.request.CciInvReportReq;
import us.tx.state.dfps.service.common.request.PreDisplayPriorityClosureReq;
import us.tx.state.dfps.service.common.response.FacilRtrvRes;
import us.tx.state.dfps.service.common.response.LicensingInvCnclusnRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dto.StageIncomingDto;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvIntakePersonPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.SafetyPlanDto;
import us.tx.state.dfps.service.cpsinvreport.dao.CpsInvReportDao;
import us.tx.state.dfps.service.dangerindicators.dao.DangerIndicatorsDao;
import us.tx.state.dfps.service.dangerindicators.dto.DangerIndicatorsDto;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CciInvReportPrefillData;
import us.tx.state.dfps.service.investigation.dao.AllegtnDao;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstDtlDao;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstSumDao;
import us.tx.state.dfps.service.investigation.dto.AllegationInvestigationLetterDto;
import us.tx.state.dfps.service.investigation.dto.AllegedSxVctmztnDto;
import us.tx.state.dfps.service.investigation.dto.CVSNotificationDto;
import us.tx.state.dfps.service.investigation.dto.InvstRestraintDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstDtlDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.AfcarsDto;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;
import us.tx.state.dfps.service.person.dto.SupervisorDto;
import us.tx.state.dfps.service.personsearch.dao.PersonSearchDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dto.ResourceAddressDto;
import us.tx.state.dfps.service.subcare.dto.ResourcePhoneDto;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PriorityClosureLicensingDto;
import us.tx.state.dfps.service.workload.dto.RcciMrefDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.service.WorkloadService;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CciInvReportServiceImpl implements CciInvReportService {

    public static final List<String> REl_INT_CPS = Collections
            .unmodifiableList(Arrays.asList("AU", "PA", "GG", "GU", "GV", "PB", "PP", "ST"));

    public static final List<String> REl_INT_AFC = Collections.unmodifiableList(Arrays.asList("HC", "MF", "IP", "IC"));

    public static final String SLASH_DATE_MASK = "MM/dd/yyyy";
    public static final String JAVA_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final DateTimeFormatter javaDateFromat = DateTimeFormatter.ofPattern(JAVA_DATE_FORMAT);
    public static final DateTimeFormatter slashFormat = DateTimeFormatter.ofPattern(SLASH_DATE_MASK);
    public static final String ISO_DATE_MASK = "yyyy-MM-dd";
    public static final DateTimeFormatter isoFormat = DateTimeFormatter.ofPattern(ISO_DATE_MASK);

    @Autowired
    CciInvReportPrefillData cciInvReportPrefillData;

    /** The licensing invst sum dao. */
    @Autowired
    LicensingInvstSumDao licensingInvstSumDao;

    @Autowired
    private DisasterPlanDao disasterPlanDao;

    @Autowired
    PersonDao personDao;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    CpsInvReportDao cpsInvReportDao;

    @Autowired
    LookupDao lookupDao;

    @Autowired
    StageDao stageDao;

    @Autowired
    LicensingInvstDtlDao licensingInvstDtlDao;

    @Autowired
    EventFetDao eventFetDao;

    @Autowired
    NotifToLawEnforcementDao notifToLawEnforcementDao;

    @Autowired
    WorkloadService workLoadService;

    @Autowired
    private CapsResourceDao capsResourceDao;

    @Autowired
    AllegtnDao allegtnDao;

    @Autowired
    SafetyPlanDao safetyPlanDao;

    @Autowired
    CciInvReportDao cciInvReportDao;

    /*@Autowired
    private CpsInvstSummaryDao cpsInvstSummaryDao;*/

    @Autowired
    ArReportDao aRReportDao;

    @Autowired
    private DangerIndicatorsDao dangerIndicatorsDao;

    @Autowired
    WorkingNarrativeDao workingNarrDao;

    @Autowired
    private PersonSearchDao personSearchDao;

    @Autowired
    private CpsIntakeReportDao cpsIntakeReportDao;

    @Autowired
    AllegationStageCaseDao allegationStageCaseDao;

    @Autowired
    StageWorkloadDao stageWorkloadDao;

    public CciInvReportServiceImpl() {
        super();
    }

    private static final Logger logger = Logger.getLogger(CciInvReportServiceImpl.class);
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
            Exception.class })
    public PreFillDataServiceDto getCciInvReport(CciInvReportReq cciInvReportReq){
        CciInvReportDto cciInvReportDto = new CciInvReportDto();

        // First section data
        GenericCaseInfoDto genericCaseInfoDto = new GenericCaseInfoDto();
        StagePersonDto stagePersonDto = new StagePersonDto();
        SupervisorDto supervisorDto = new SupervisorDto();
        StageValueBeanDto stageInfo = stageDao.retrieveStageInfo(cciInvReportReq.getIdStage());
        getFirstSectionDetails(cciInvReportReq, genericCaseInfoDto, cciInvReportDto, stagePersonDto, supervisorDto, stageInfo);

        cciInvReportDto.setLicensingInvstDtlDto(getLicensingInvstDtlDto(cciInvReportReq.getIdStage(), stageInfo));/** Investigation Information **/
        populateIndLbtrSxtrAllegationExist(cciInvReportReq.getIdStage() , cciInvReportDto);
        List<Allegation> allegationList = allegtnDao.getAllegationsByIdStage(cciInvReportReq.getIdStage());
        cciInvReportDto.setAllegationInvestigationLetterDto(getPersonChildDeathDetails(allegationList));
        cciInvReportDto.setAllegationsList(getAllegationsList(allegationList));
        cciInvReportDto.setDangerIndicatorsDto(getDangerIndicatorsInformation(cciInvReportReq.getIdStage()));
        cciInvReportDto.setCciInvIntakeDto(populateCciIntakeDto(cciInvReportReq.getIdStage()));
        List<SafetyPlan> safetyPlanList = safetyPlanDao.getSafetyPlanList(stageInfo.getIdCase(), cciInvReportReq.getIdStage());
        cciInvReportDto.setSafetyPlanDtoList(safetyPlanList.stream().map(safetyPlan -> getSafetyPlanDto(safetyPlan)).collect(Collectors.toList()));
        cciInvReportDto.setPersonSplInfoListPrin(getPersonSplInfoList(cciInvReportReq.getIdStage()));
        populateInvReporterInfo(cciInvReportDto, cciInvReportReq.getIdStage());
        cciInvReportDto.setPrincipalsList(getPrincipalList(cciInvReportReq.getIdStage()));
        cciInvReportDto.setCharacteristicsDtoList(getCharacteristicsList(cciInvReportReq.getIdStage(),stageInfo.getNmStage()));
        cciInvReportDto.setPrnAfcarsDto(getPrnAfcarsDto(cciInvReportReq.getIdStage(),stageInfo.getNmStage()));
        cciInvReportDto.setColAfcarsDto(getColAfcarsDto(cciInvReportReq.getIdStage(),stageInfo.getNmStage()));
        cciInvReportDto.setCollateralList(getCollateralList(cciInvReportReq.getIdStage()));
        cciInvReportDto.setColCharacteristicsDtoList(getColCharacteristicsList(cciInvReportReq.getIdStage(),stageInfo.getNmStage()));
        cciInvReportDto.setLetterDetailList(cciInvReportDao.getLetterDetailList(stageInfo.getIdStage(), stageInfo.getIdCase()));
        cciInvReportDto.setIntakesList(getMergedIntakeNarrative(stageInfo.getIdStage(), stageInfo.getIdCase(), stageInfo.getCdStage()));
        populateMergedInvReporterInfo(cciInvReportDto);
        cciInvReportDto.setContactsList(getContactsList(cciInvReportReq.getIdStage(),cciInvReportDto.getGenericCaseInfoDto()));
        return cciInvReportPrefillData.returnPrefillData(cciInvReportDto);
    }

    private void populateInvReporterInfo(CciInvReportDto cciInvReportDto, Long stageId){
        List<CciInvReportPersonDto> cciInvReportPersonDtoList = getInvReporterInfoList(stageId);
        cciInvReportDto.getPersonSplInfoListPrin().forEach( intakeReporterDto -> {

            CciInvReportPersonDto invSelectedReporterDto = cciInvReportPersonDtoList.stream().
                    filter(invReporterDto -> invReporterDto.getIdPerson().equals(intakeReporterDto.getIdPerson())).findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(invSelectedReporterDto)) {
                invSelectedReporterDto.setTxtStagePersNote(intakeReporterDto.getTxtStagePersNote());
            }else{
                long mergedPersonId = personSearchDao.getForwardPersonInMerge(intakeReporterDto.getIdPerson());
                invSelectedReporterDto = cciInvReportPersonDtoList.stream().
                        filter(invReporterDto -> invReporterDto.getIdPerson().equals(mergedPersonId)).findFirst().orElse(null);
                if(!ObjectUtils.isEmpty(invSelectedReporterDto)) {
                    invSelectedReporterDto.setTxtStagePersNote(intakeReporterDto.getTxtStagePersNote());
                }
            }
        });
        cciInvReportDto.setInvReporterInfoList(cciInvReportPersonDtoList);
    }

    private void populateMergedInvReporterInfo(CciInvReportDto cciInvReportDto){
        List<CciInvReportPersonDto> cciInvReportPersonDtoList = cciInvReportDto.getInvReporterInfoList();
        cciInvReportDto.getIntakesList().forEach( mergedIntakeReporterDto -> {
            CciInvReportPersonDto invSelectedReporterDto = cciInvReportPersonDtoList.stream().
                    filter(invReporterDto -> invReporterDto.getIdPerson().equals(mergedIntakeReporterDto.getIdPerson())).findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(invSelectedReporterDto)) {
                invSelectedReporterDto.setTxtStagePersNote(mergedIntakeReporterDto.getTxtStagePersonNote());
            }
        });
    }
    public void populateIndLbtrSxtrAllegationExist(Long idStage,
                                                   CciInvReportDto cciInvReportDto) {
        AllegationStageCaseInDto pCINVG1DInputRec = new AllegationStageCaseInDto();
        cciInvReportDto.setIndLbtrSxtrAllegationExist(ServiceConstants.AR_NO);
        pCINVG1DInputRec.setIdStage(idStage);
        List<AllegationStageCaseOutDto> cinvg1doDtos = allegationStageCaseDao.caseExistDtls(pCINVG1DInputRec);
        for (AllegationStageCaseOutDto cinvg1doDto : cinvg1doDtos) {
            if (!TypeConvUtil.isNullOrEmpty(cinvg1doDto)) {
                cciInvReportDto.setIndLbtrSxtrAllegationExist(cinvg1doDto.getIndChildSexLaborTrafficExists());
            }
        }
    }

    private List<AfcarsDto> getColAfcarsDto(Long idStage, String cdStgProgram) {
        List<AfcarsDto> afcarsDtoList = new ArrayList<>();
        //display AFCARS characteristics
        if (displayChildPlacementCharacteristics(cdStgProgram) ||
                displayChildInvestCharacteristics(cdStgProgram)) {
            List<AfcarsResponse> afcarsResponseList = cciInvReportDao.getColAfcarsCharacteristicsByStage(idStage);
            if(!ObjectUtils.isEmpty(afcarsResponseList)){
                for(AfcarsResponse res : afcarsResponseList) {
                    AfcarsDto afcarsDto = new AfcarsDto();
                    afcarsDto.setIdAfcarsResponse(res.getIdAfcarsResponse());
                    afcarsDto.setIdPerson(res.getIdPerson());
                    afcarsDto.setCdResponse(res.getCdResponse());
                    afcarsDtoList.add(afcarsDto);
                }
            }
        }
        return afcarsDtoList;
    }

    private List<AfcarsDto> getPrnAfcarsDto(Long idStage, String cdStgProgram) {
        List<AfcarsDto> afcarsDtoList = new ArrayList<>();
        //display AFCARS characteristics
        if (displayChildPlacementCharacteristics(cdStgProgram) ||
                displayChildInvestCharacteristics(cdStgProgram)) {
            List<AfcarsResponse> afcarsResponseList = cciInvReportDao.getPrnAfcarsCharacteristicsByStage(idStage);
            if(!ObjectUtils.isEmpty(afcarsResponseList)){
                for(AfcarsResponse res : afcarsResponseList){
                    AfcarsDto afcarsDto = new AfcarsDto();
                    afcarsDto.setIdAfcarsResponse(res.getIdAfcarsResponse());
                    afcarsDto.setIdPerson(res.getIdPerson());
                    afcarsDto.setCdResponse(res.getCdResponse());
                    afcarsDtoList.add(afcarsDto);
                }
            }
        }
        return afcarsDtoList;
    }

    private DangerIndicatorsDto getDangerIndicatorsInformation(Long idStage) {
        DangerIndicatorsDto dangerIndicatorsDto = dangerIndicatorsDao.getDangerIndicator(idStage);
        return dangerIndicatorsDto;
    }

    private void getFirstSectionDetails(CciInvReportReq cciInvReportReq, GenericCaseInfoDto genericCaseInfoDto,
                                        CciInvReportDto cciInvReportDto, StagePersonDto stagePersonDto, SupervisorDto supervisorDto,
                                        StageValueBeanDto stageInfo){
        Long stageId = cciInvReportReq.getIdStage();
        EmployeePersPhNameDto employeePersPhNameDto = new EmployeePersPhNameDto();
        cciInvReportDto.setGenericCaseInfoDto(disasterPlanDao.getGenericCaseInfo(stageId));
        genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(cciInvReportReq.getIdStage());
        genericCaseInfoDto.setIndCaseSensitive(indTranslate(genericCaseInfoDto.getIndCaseSensitive()));
        genericCaseInfoDto.setDtInvInitiated(cciInvReportDao.getDtInvInitiatedByStage(stageInfo.getIdCase(),stageId));
        if(!ObjectUtils.isEmpty(stageInfo) & null != stageInfo.getCdStageReasonClosed()){
            genericCaseInfoDto.setCdStageReasonClosed(stageInfo.getCdStageReasonClosed());
        }
        cciInvReportDto.setGenericCaseInfoDto(genericCaseInfoDto);

        // get the worker information
        stagePersonDto = stageDao.getStagePersonLinkDetails(stageId, ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);

        if (!ObjectUtils.isEmpty(stagePersonDto)) {
            // supervisor information Call CCMN60D
            supervisorDto = personDao.getSupervisorForPersonId(stagePersonDto.getIdTodoPersWorker());
            /* retrieves worker info CallCSEC01D */

            employeePersPhNameDto = employeeDao.searchPersonPhoneName(stagePersonDto.getIdTodoPersWorker());
        }
        LicensingInvstDtlDto licInvstDtlDto = licensingInvstSumDao.getLicensingInvstDtlDaobyParentId(stageId);
        String sdmRiskFinal = cpsInvReportDao.getValidSdmRa(stageId);

        // Getting MRef value like UI. In UI this logic is implemented in CaseSummary.jsp page.
        String mRef = "";
        try {
            if(null != stageInfo.getDtStageCreated() && null != stageInfo.getDtMultiRef()) {
                mRef = getMrefNumberOfdays(String.valueOf(stageInfo.getDtStageCreated()), String.valueOf(stageInfo.getDtMultiRef()));
            } else {
                List rcciMrefCandidateIdList = new ArrayList();
                rcciMrefCandidateIdList.add(stageInfo.getIdCase());
                List<RcciMrefDto> mrefDtoList = stageWorkloadDao.getRcciMrefDataByCaseList(rcciMrefCandidateIdList);

                // apply RCCI Mref data to result. Since we searched by case, all stages have the same case.
                if (!ObjectUtils.isEmpty(mrefDtoList)) {
                    // Fetch object using stage ID from the list of objects related to Case ID.
                    Optional<RcciMrefDto> currMrefOpt = mrefDtoList.stream().filter(rcciMrefDto -> rcciMrefDto.getIdStage().equals(stageInfo.getIdStage())).findFirst();
                    if(currMrefOpt.isPresent()) {
                        RcciMrefDto currMref = currMrefOpt.get();
                        if (currMref != null && ServiceConstants.INV_Stage.equals(stageInfo.getCdStage())) {
                            Integer refCnt = CaseSummaryServiceImpl.applyRcciMrefThresholds(currMref.getRcciMrefCnt(),
                                    stageInfo.getCdStageProgram(), currMref.getNbrRsrcFacilCapacity(),
                                    currMref.getCdRsrcFacilType());
                            if (null != refCnt) {
                                mRef = "M-Ref" + refCnt;
                            }
                        }
                    }
                }
            }
        } catch (ParseException e) {
            mRef = "";
        }

        cciInvReportDto.setStagePersonDto(stagePersonDto);
        cciInvReportDto.setValidSdmRaString(sdmRiskFinal);
        cciInvReportDto.setMrefString(mRef);
        cciInvReportDto.setSupervisorDto(supervisorDto);
        cciInvReportDto.setEmployeePersPhNameDto(employeePersPhNameDto);
        cciInvReportDto.setLicensingInvstDtlDto(licInvstDtlDto);
    }

    private String getMrefNumberOfdays(String stageCreated, String multiRef) throws ParseException {

        LocalDate stageStartDate = toJavaDateFromInput(stageCreated);
        LocalDate mRefDate = toJavaDateFromInput(multiRef);
        String mRef = "";

        if (stageStartDate != null && mRefDate != null) {

            if (!stageStartDate.isAfter(mRefDate)) {
                mRef = "M-Ref";
                int noOfDays = (int) java.time.temporal.ChronoUnit.DAYS.between(stageStartDate, mRefDate);
                if (noOfDays < 99) {
                    mRef = mRef + noOfDays;
                } else {
                    mRef = mRef + 99;
                }
            }
        }
        return mRef;
    }

    private LocalDate toJavaDateFromInput(String dateString) throws ParseException{
        LocalDate retDate = null;
        // Try to get a date with format yyyy-MM-DD
        if (StringUtils.isNotBlank(dateString)) {
            if (dateString.indexOf('-') > 0) {
                retDate = LocalDate.parse(dateString, isoFormat);
            } else if (dateString.indexOf('/') > 0) {
                // Try to get a date with format mm/dd/yyyy
                retDate = LocalDate.parse(dateString, slashFormat);
            } else if (dateString.length() == 8) {
                StringBuilder sb = new StringBuilder(dateString.substring(0, 2));
                sb.append('/');
                sb.append(dateString.substring(2, 4));
                sb.append('/');
                sb.append(dateString.substring(4, 8));
                dateString = sb.toString();
                retDate = LocalDate.parse(dateString, isoFormat);
            } else {
                retDate = LocalDate.parse(dateString, javaDateFromat);
            }
        }
        return retDate;
    }

    private List<CciInvAllegDto> getAllegationsList(List<Allegation> allegationList) {
        List<CciInvAllegDto> dtoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(allegationList)){
            for(Allegation allegation :allegationList){
                CciInvAllegDto dto = new CciInvAllegDto();
                dto.setIdAllegation(allegation.getIdAllegation());
                dto.setNmVicLast(null!=allegation.getPersonByIdVictim()?allegation.getPersonByIdVictim().getNmPersonLast():"");
                dto.setNmVicFirst(null!=allegation.getPersonByIdVictim()?allegation.getPersonByIdVictim().getNmPersonFirst():"");
                dto.setNmVicMiddle(null!=allegation.getPersonByIdVictim()?allegation.getPersonByIdVictim().getNmPersonMiddle():"");
                dto.setNmVicFull(null!=allegation.getPersonByIdVictim()?allegation.getPersonByIdVictim().getNmPersonFull():"");
                dto.setNmPerpFirst(null!=allegation.getPersonByIdAllegedPerpetrator()?allegation.getPersonByIdAllegedPerpetrator().getNmPersonFirst():"");
                dto.setNmPerpLast(null!=allegation.getPersonByIdAllegedPerpetrator()?allegation.getPersonByIdAllegedPerpetrator().getNmPersonLast():"");
                dto.setNmPerpMiddle(null!=allegation.getPersonByIdAllegedPerpetrator()?allegation.getPersonByIdAllegedPerpetrator().getNmPersonMiddle():"");
                dto.setNmPerpFull(null!=allegation.getPersonByIdAllegedPerpetrator()?allegation.getPersonByIdAllegedPerpetrator().getNmPersonFull():"");
                dto.setCdAllegType(allegation.getCdAllegType());
                dto.setCdAllegDisp(allegation.getCdAllegDisposition());
                dto.setCdAllegSev(allegation.getCdAllegSeverity());
                dto.setTxtDispSev(allegation.getTxtDispstnSeverity());
                dto.setCdChildFatality(getChildFatality(allegation.getIndFatality()));
                dto.setDtPersonDeath(allegation.getPersonByIdVictim().getDtPersonDeath());

                dtoList.add(dto);
            }
        }

        return dtoList;
    }

    private String getChildFatality(String indFatality) {
        String childFatality = ServiceConstants.NOT_APPLICABLE;
        if(null != indFatality && indFatality.equalsIgnoreCase("Y")){
            childFatality = "Yes";
        }else if (null != indFatality && indFatality.equalsIgnoreCase("N")){
            childFatality = "No";
        }

        return childFatality;
    }

    private LicensingInvstDtlDto getLicensingInvstDtlDto(Long idStage, StageValueBeanDto stageInfo) {


        String dec2019Dt = lookupDao.simpleDecode(ServiceConstants.CRELDATE,
                ServiceConstants.CRELDATE_DEC_2019_FCL);
        Date Dec2019 = null;
        Date stageStartDate = null;
        try {
            Dec2019 = DateUtils.toJavaDateFromInput(dec2019Dt);
            stageStartDate = stageInfo.getDtStageStart();
        }
        catch(ParseException e) {
            new ServiceLayerException(e.getMessage());

        }
        LicensingInvstDtlDto licensingInvstDtlDto = licensingInvstSumDao
                .getLicensingInvstDtlDaobyParentId(idStage);
        List<AllegedSxVctmztnDto> allegedSxVctmztnDtoList = new ArrayList();
        List<InvstRestraintDto> invstRestraintDtoList = licensingInvstSumDao
                .getInvstConclusionResById(licensingInvstDtlDto.getIdLicngInvstStage());
        if(ServiceConstants.CPGRMS_RCL.equals(stageInfo.getCdStageProgram()) && (stageStartDate == null || !DateUtils.isBefore(stageStartDate, Dec2019))){
            allegedSxVctmztnDtoList = licensingInvstSumDao
                    .getAllegedSxVctmztnDtoByStageId(licensingInvstDtlDto.getIdLicngInvstStage());

            for (AllegedSxVctmztnDto allegedSxVctmztnDto : allegedSxVctmztnDtoList) {
                AllegedSxVctmztn allegedSxVctmztn = licensingInvstSumDao.getAllegedSxVctmztnDtoById(allegedSxVctmztnDto.getIdVictim(), allegedSxVctmztnDto.getIdStage());
                if(allegedSxVctmztn != null){
                    allegedSxVctmztnDto.setIdAllegedSxVctmztn(allegedSxVctmztn.getIdAllegedSxVctmztn());
                }
            }
        }
        List<String> cdLicEmergRestraint = new ArrayList<>();
        invstRestraintDtoList.forEach(o -> cdLicEmergRestraint.add(o.getCdRstraint()));
        licensingInvstDtlDto.setCdLicEmergRestraint(cdLicEmergRestraint);
        licensingInvstDtlDto.setAllegedSxVctmztnDtoList(allegedSxVctmztnDtoList);
        licensingInvstDtlDto.setCvsNotificationDtoList(populateCVSNotificationData(allegedSxVctmztnDtoList, stageInfo));

        // Code to populate licensing data
        populateLicensingData(idStage,licensingInvstDtlDto);
        return licensingInvstDtlDto;
    }

    private List<CVSNotificationDto> populateCVSNotificationData(List<AllegedSxVctmztnDto> allegedSxVctmztnDtoList, StageValueBeanDto stageInfo){
        if(ServiceConstants.CPGRMS_CCL.equals(stageInfo.getCdStageProgram()) && ObjectUtils.isEmpty(allegedSxVctmztnDtoList)){
            allegedSxVctmztnDtoList = licensingInvstSumDao
                    .getAllegedSxVctmztnDtoByStageId(stageInfo.getIdStage());
        }
        List<CVSNotificationDto> cvsNotificationDtoList = new ArrayList<>();
        for (AllegedSxVctmztnDto allegedSxVctmztnDto : allegedSxVctmztnDtoList) {
            CVSNotificationDto cvsNotificationDto = new CVSNotificationDto();
            cvsNotificationDto.setNameVictim(allegedSxVctmztnDto.getNameVictim());
            List<CvsNotifLog> cvsAutoNotifLogList = licensingInvstDtlDao.getCVSNotificationLog(stageInfo.getIdCase(), allegedSxVctmztnDto.getIdVictim(), ServiceConstants.CMAILTYP_CANE);
            cvsNotificationDto.setAutoEmailSent(ServiceConstants.NO_TEXT);
            if(!CollectionUtils.isEmpty(cvsAutoNotifLogList)) {
                cvsNotificationDto.setAutoEmailSent(ServiceConstants.YES_TEXT);
                cvsNotificationDto.setDateAutoEmailSent(cvsAutoNotifLogList.get(0).getDtEmailSent());
            }
            List<CvsNotifLog> cvsManualNotifLogList = licensingInvstDtlDao.getCVSNotificationLog(stageInfo.getIdCase(), allegedSxVctmztnDto.getIdVictim(), ServiceConstants.CMAILTYP_CMNE);
            if(!CollectionUtils.isEmpty(cvsManualNotifLogList)) {
                cvsNotificationDto.setManualEmailSent(ServiceConstants.STRING_IND_Y.equals(cvsManualNotifLogList.get(0).getIndNotificationSent()) ? ServiceConstants.YES_TEXT
                        :ServiceConstants.NO_TEXT);
                cvsNotificationDto.setDateManualEmailSent(cvsManualNotifLogList.get(0).getDtEmailSent());
            }
            List<TodoDto> todoDtoList = licensingInvstDtlDao.getCVSNotificationAlert(stageInfo.getIdCase(), stageInfo.getIdStage(), allegedSxVctmztnDto.getIdVictim());
            cvsNotificationDto.setAlertSent(ServiceConstants.NO_TEXT);
            if(!CollectionUtils.isEmpty(todoDtoList)) {
                cvsNotificationDto.setAlertSent(ServiceConstants.YES_TEXT);
                cvsNotificationDto.setDateAlertSent(todoDtoList.get(0).getDtTodoCreated());
            }
            cvsNotificationDtoList.add(cvsNotificationDto);
        }
        return cvsNotificationDtoList;

    }


    private void populateLicensingData(Long idStage,  LicensingInvstDtlDto licensingInvstDtlDto){
        PriorStageDto priorStageDto = null;
        if (!ObjectUtils.isEmpty(licensingInvstDtlDto.getIdEvent())) {
            if (ObjectUtils.isEmpty(licensingInvstDtlDto.getIdResource())
                    && ObjectUtils.isEmpty(licensingInvstDtlDto.getIdAffilResource())
                    && ObjectUtils.isEmpty(licensingInvstDtlDto.getNbrAffilAcclaim())
                    && ObjectUtils.isEmpty(licensingInvstDtlDto.getNbrAcclaim())) {
                // retrieves the priorStage information based on idStage
                priorStageDto = notifToLawEnforcementDao
                        .getPriorStagebyId(idStage);
                // retieves the resource facility detail based on the prior
                // stage id
                FacilRtrvRes facilRtrvRes = stageDao.getFacilityDetail(priorStageDto.getIdPriorStage());
                licensingInvstDtlDto.setIdResource(facilRtrvRes.getIdResource());
                licensingInvstDtlDto.setCdRsrcFacilType(facilRtrvRes.getCdIncmgFacilType());
                licensingInvstDtlDto.setNmResource(facilRtrvRes.getNmIncmgFacilName());
                licensingInvstDtlDto.setNmAffilResource(facilRtrvRes.getNmIncmgFacilAffiliated());
            }
            if (ObjectUtils.isEmpty(licensingInvstDtlDto.getNbrAcclaim())) {
                if (ObjectUtils.isEmpty(priorStageDto)) {
                    priorStageDto = notifToLawEnforcementDao.getPriorStagebyId(idStage);
                }
                PreDisplayPriorityClosureReq priorityClosureReq = new PreDisplayPriorityClosureReq();
                priorityClosureReq.setIdStage(priorStageDto.getIdPriorStage());
                PriorityClosureLicensingDto priorityClosureLicensingDto = workLoadService.getLicensingInformation(priorityClosureReq, licensingInvstDtlDto.getIdCase());
                if (null != priorityClosureLicensingDto) {
                    licensingInvstDtlDto.setIdResource(priorityClosureLicensingDto.getIdResource());
                    licensingInvstDtlDto.setNmResource(priorityClosureLicensingDto.getNmResource());
                    licensingInvstDtlDto.setNbrAcclaim(priorityClosureLicensingDto.getNbrAcclaim());
                    licensingInvstDtlDto.setNbrAgency(priorityClosureLicensingDto.getNbrAgency());
                    licensingInvstDtlDto.setNbrBranch(priorityClosureLicensingDto.getNbrBranch());
                    licensingInvstDtlDto.setCdRsrcFacilType(priorityClosureLicensingDto.getCdRsrcFacilType());
                    licensingInvstDtlDto.setClassFacilType(priorityClosureLicensingDto.getClassFacilType());

                    licensingInvstDtlDto.setIdAffilResource(priorityClosureLicensingDto.getIdAffilResource());
                    licensingInvstDtlDto.setNmAffilResource(priorityClosureLicensingDto.getNmAffilResource());
                    licensingInvstDtlDto.setNbrAffilAcclaim(priorityClosureLicensingDto.getNbrAffilAcclaim());
                    licensingInvstDtlDto.setNbrAffilAgency(priorityClosureLicensingDto.getNbrAffilAgency());
                    licensingInvstDtlDto.setNbrAffilBranch(priorityClosureLicensingDto.getNbrAffilBranch());
                    licensingInvstDtlDto.setCdAffilFacilType(priorityClosureLicensingDto.getCdAffilRsrcFacilType());
                    licensingInvstDtlDto.setClassAffilFacilType(priorityClosureLicensingDto.getClassAffilFacilType());
                }
            }
            if(!ObjectUtils.isEmpty(licensingInvstDtlDto.getIdResource())) {
                ResourceAddressDto operationPrimaryAddress = getResourcePrimaryAddress(licensingInvstDtlDto.getIdResource());
                if (!ObjectUtils.isEmpty(operationPrimaryAddress)) {
                    licensingInvstDtlDto.setAddrStLn1(operationPrimaryAddress.getAddrRsrcAddrStLn1());
                    licensingInvstDtlDto.setAddrStLn2(operationPrimaryAddress.getAddrRsrcAddrStLn2());
                    licensingInvstDtlDto.setAddrCity(operationPrimaryAddress.getAddrRsrcAddrCity());
                    licensingInvstDtlDto.setAddrState(operationPrimaryAddress.getCdRsrcAddrState());
                    licensingInvstDtlDto.setAddrZip(operationPrimaryAddress.getAddrRsrcAddrZip());
                    licensingInvstDtlDto.setAddrCounty(operationPrimaryAddress.getCdRsrcAddrCounty());
                }
                ResourcePhoneDto operationPrimaryPhone = getResourcePrimaryPhone(licensingInvstDtlDto.getIdResource());
                licensingInvstDtlDto.setNbrPhone(!ObjectUtils.isEmpty(operationPrimaryPhone) ? operationPrimaryPhone.getNbrRsrcPhone() : licensingInvstDtlDto.getNbrPhone());
           }
            if(!ObjectUtils.isEmpty(licensingInvstDtlDto.getIdAffilResource())) {
                ResourceAddressDto agencyPrimaryAddress = getResourcePrimaryAddress(licensingInvstDtlDto.getIdAffilResource());
                if (!ObjectUtils.isEmpty(agencyPrimaryAddress)) {
                    licensingInvstDtlDto.setAddrAffilStLn1(agencyPrimaryAddress.getAddrRsrcAddrStLn1());
                    licensingInvstDtlDto.setAddrAffilStLn2(agencyPrimaryAddress.getAddrRsrcAddrStLn2());
                    licensingInvstDtlDto.setAddrAffilCity(agencyPrimaryAddress.getAddrRsrcAddrCity());
                    licensingInvstDtlDto.setAddrAffilState(agencyPrimaryAddress.getCdRsrcAddrState());
                    licensingInvstDtlDto.setAddrAffilZip(agencyPrimaryAddress.getAddrRsrcAddrZip());
                    licensingInvstDtlDto.setAddrAffilCounty(agencyPrimaryAddress.getCdRsrcAddrCounty());
                }
                ResourcePhoneDto agencyPrimaryPhone = getResourcePrimaryPhone(licensingInvstDtlDto.getIdAffilResource());
                licensingInvstDtlDto.setNbrAffilPhone(!ObjectUtils.isEmpty(agencyPrimaryPhone) ? agencyPrimaryPhone.getNbrRsrcPhone() : licensingInvstDtlDto.getNbrAffilPhone());
            }
            CapsResource capsResource = capsResourceDao.getCapsResourceById(licensingInvstDtlDto.getIdResource());
            if(!ObjectUtils.isEmpty(capsResource)){
                licensingInvstDtlDto.setNmResourceContact(capsResource.getNmRsrcContact());
            }
        }
    }
    private ResourceAddressDto getResourcePrimaryAddress(Long idResource){
        ResourceAddressDto resourceAddress = null;
        if (!ObjectUtils.isEmpty(idResource)) {
            List<ResourceAddressDto> resourceAddressList = capsResourceDao.getResourceAddress(idResource);
            resourceAddress = resourceAddressList.stream().filter(dto -> ServiceConstants.PRIMARY_ADDRESS_TYPE.equals(
                    dto.getCdRsrcAddrType())).findAny().orElse(null);
        }
        return resourceAddress;

    }
    public ResourcePhoneDto getResourcePrimaryPhone(Long idResource) {
        ResourcePhoneDto primaryResourcePhone = null;
        List<ResourcePhoneDto> resourcePhoneList = capsResourceDao.getResourcePhone(idResource);
        if (!ObjectUtils.isEmpty(resourcePhoneList)) {
            primaryResourcePhone = resourcePhoneList.stream()
                    .filter(phone -> CodesConstant.CRSCPHON_01.equals(phone.getCdRsrcPhoneType())).findAny()
                    .orElse(null);
        }
        return primaryResourcePhone;
    }

    private List<AllegationInvestigationLetterDto> getPersonChildDeathDetails(List<Allegation> allegationList) {

        List<AllegationInvestigationLetterDto> childDeathDetailsList = new ArrayList<>();
        Map<Long, String> personDetailMap = allegationList.stream()
                .filter(a -> a.getPersonByIdVictim() != null)
                .collect(Collectors.toMap(
                        a -> a.getPersonByIdVictim().getIdPerson(),
                        a -> a.getPersonByIdVictim().getNmPersonFull(),
                        (name1, name2) -> name1)
                );

        personDetailMap.keySet().forEach( victimId ->{
            AllegationInvestigationLetterDto selected = new AllegationInvestigationLetterDto();
            selected.setVictimId(victimId);
            selected.setVictimPersonFullName(personDetailMap.get(victimId));
            selected.setHasChildDeathReportCompleted(allegtnDao.hasChildDeathReportCompleted(victimId) ? "Y" : "N");
            childDeathDetailsList.add(selected);
        });
        return childDeathDetailsList;
    }

    private SafetyPlanDto getSafetyPlanDto(SafetyPlan safetyPlan){
        SafetyPlanDto SafetyPlanDto = new SafetyPlanDto();
        SafetyPlanDto.setCreatedDate(safetyPlan.getDtCreated());
        SafetyPlanDto.setId(safetyPlan.getId());
        SafetyPlanDto.setSafetyPlanStatus(safetyPlan.getSafetyPlanStatus());
        if(!ObjectUtils.isEmpty(safetyPlan.getFacility())) {
            SafetyPlanDto.setOperationName(safetyPlan.getFacility().getNmFclty());
            SafetyPlanDto.setOperationNumber(!ObjectUtils.isEmpty(safetyPlan.getFacility().getNbrFclty()) ? safetyPlan.getFacility().getNbrFclty().toString() : "");
        }
        SafetyPlanDto.setEffectiveDate(safetyPlan.getEffectiveDate());
        if(null != safetyPlan.getFacility() && (null != safetyPlan.getFacility().getCapsResource()) ) {
            SafetyPlanDto.setImpactFacilityType(safetyPlan.getFacility().getCapsResource().getCdRsrcFacilType());
        }
        return SafetyPlanDto;
    }

    private List<CharacteristicsDto> getCharacteristicsList(Long idStage, String cdStgProgram) {
        List<CharacteristicsDto> characteristicsDtos = cciInvReportDao.getPrnCharacteristicsByStage(idStage);
        List<CharacteristicsDto> charDtoList = new ArrayList<>();
        boolean displayAPS = displayAPSCharacteristics(cdStgProgram);
        boolean displayChildPlacement = displayChildPlacementCharacteristics(cdStgProgram);
        boolean displayChildInvest = displayChildInvestCharacteristics(cdStgProgram);

        if (characteristicsDtos != null) {
            for(CharacteristicsDto charDto : characteristicsDtos){
                String category = charDto.getCdCharacCategory();
                boolean displayParent = displayParentCharacteristics(charDto.getCdStagePersRelInt(), cdStgProgram);

                if (displayAPS && CodesConstant.CCHRTCAT_CAP.equals(category)) {
                    charDtoList.add(charDto);
                }
                if (displayChildInvest && CodesConstant.CCHRTCAT_CCH.equals(category)) {
                    charDtoList.add(charDto);
                }
                if (displayParent && CodesConstant.CCHRTCAT_CCT.equals(category)) {
                    charDtoList.add(charDto);
                }
                if (displayChildPlacement && CodesConstant.CCHRTCAT_CPL.equals(category)) {
                    charDtoList.add(charDto);
                }
            }
        }
        return charDtoList;
    }

    private boolean displayParentCharacteristics(String relInt, String cdStgProgram) {
        boolean cpsAgeRel = isCPS(cdStgProgram) && REl_INT_CPS.contains(relInt);
        boolean rclRel = isRCL(cdStgProgram) || "FP".equals(relInt);
        boolean afcRel = isAFC(cdStgProgram) || REl_INT_AFC.contains(relInt);
        return cpsAgeRel || rclRel || afcRel;
    }

    private boolean displayAPSCharacteristics(String cdStgProgram) {
        return isAFC(cdStgProgram) || isAPS(cdStgProgram) ;
    }

    private boolean displayChildInvestCharacteristics(String cdStgProgram) {
        return (isAPS(cdStgProgram) == false);
    }

    private boolean displayChildPlacementCharacteristics(String cdStgProgram) {
        return isCPS(cdStgProgram);
    }
    private boolean isCPS(String cdStgProgram) {
        return "CPS".equals(cdStgProgram);
    }

    private boolean isRCL(String cdStgProgram) {
        return "RCL".equals(cdStgProgram);
    }

    private boolean isAFC(String cdStgProgram) {
        return "AFC".equals(cdStgProgram);
    }

    private boolean isAPS(String cdStgProgram) {
        return "APS".equals(cdStgProgram);
    }

    private List<CciInvReportPersonDto> getPrincipalList(Long idStage) {
        return cciInvReportDao.getPrincipals(idStage,ServiceConstants.PRINCIPAL);
    }

    private List<CciInvReportPersonDto> getPersonSplInfoList(Long idStage) {
       Long intakeStageId = cpsInvReportDao.getPriorIntStage(idStage);
       List<CciInvReportPersonDto> cciInvReportPersonDtoList = cciInvReportDao.getPersonSplInfo(intakeStageId);
       if(ObjectUtils.isEmpty(cciInvReportPersonDtoList)) {
           cciInvReportPersonDtoList = new ArrayList<>();
           IncomingStageDetailsDto incomingStageDetailsDto = cpsIntakeReportDao
                   .getStageIncomingDetails(intakeStageId);
           if(ServiceConstants.CRPTRINT_SF.equals(incomingStageDetailsDto.getCdIncmgCallerInt())){
               CciInvReportPersonDto cciInvReportPersonDto = new CciInvReportPersonDto();
               cciInvReportPersonDto.setTxtStagePersNote(!ObjectUtils.isEmpty(incomingStageDetailsDto) ? incomingStageDetailsDto.getTxtReporterNotes() : "");
               cciInvReportPersonDtoList.add(cciInvReportPersonDto);
            }
       }
       return cciInvReportPersonDtoList;
    }

    private List<CciInvReportPersonDto> getInvReporterInfoList(Long idStage) {
        return cciInvReportDao.getPersonSplInfo(idStage);
    }

    private List<CciInvReportPersonDto> getCollateralList(Long idStage) {
        return cciInvReportDao.getPrincipals(idStage, ServiceConstants.COLLATERAL);
    }

    private List<CharacteristicsDto> getColCharacteristicsList(Long idStage, String cdStgProgram) {
        List<CharacteristicsDto> characteristicsDtos = cciInvReportDao.getColCharacteristicsByStage(idStage);
        List<CharacteristicsDto> charDtoList = new ArrayList<>();
        boolean displayAPS = displayAPSCharacteristics(cdStgProgram);
        boolean displayChildPlacement = displayChildPlacementCharacteristics(cdStgProgram);
        boolean displayChildInvest = displayChildInvestCharacteristics(cdStgProgram);

        if (characteristicsDtos != null) {
            for(CharacteristicsDto charDto : characteristicsDtos){
                String category = charDto.getCdCharacCategory();
                boolean displayParent = displayParentCharacteristics(charDto.getCdStagePersRelInt(), cdStgProgram);

                if (displayAPS && CodesConstant.CCHRTCAT_CAP.equals(category)) {
                    charDtoList.add(charDto);
                }
                if (displayChildInvest && CodesConstant.CCHRTCAT_CCH.equals(category)) {
                    charDtoList.add(charDto);
                }
                if (displayParent && CodesConstant.CCHRTCAT_CCT.equals(category)) {
                    charDtoList.add(charDto);
                }
                if (displayChildPlacement && CodesConstant.CCHRTCAT_CPL.equals(category)) {
                    charDtoList.add(charDto);
                }
            }
        }

        return charDtoList;
    }
    private List<CciInvContactDto> getContactsList(Long idStage,GenericCaseInfoDto genericCaseInfoDto) {

        String mergeString = null;
        List<Long> mergedStageList = new ArrayList();
        List<CpsInvIntakePersonPrincipalDto> intakesList = new ArrayList<CpsInvIntakePersonPrincipalDto>();

        if (!ObjectUtils.isEmpty(genericCaseInfoDto) && !ObjectUtils.isEmpty(genericCaseInfoDto.getIdStage())) {

            if (!ObjectUtils.isEmpty(cpsInvReportDao.getMergeHistory(genericCaseInfoDto.getIdCase()))) {
                mergedStageList = workingNarrDao.spIdStage(genericCaseInfoDto.getIdCase(),
                        genericCaseInfoDto.getIdStage(), genericCaseInfoDto.getCdStage());
                StringBuilder sb = new StringBuilder();
                if (!ObjectUtils.isEmpty(mergedStageList)) {
                    for (Long stage : mergedStageList) {
                        sb.append(stage);
                        sb.append(",");
                    }
                    if (ServiceConstants.Zero < sb.length()) {
                        mergeString = sb.toString().substring(0, sb.length() - 1);
                    }
                }
                if (!ObjectUtils.isEmpty(mergeString)) {
                    intakesList = cpsInvReportDao.getIntakes(mergeString);
                }
            }
        }
        Long intakeStageId = cpsInvReportDao.getPriorIntStage(idStage);
        Map<String, String> allStages = getAllStages(mergeString, genericCaseInfoDto, intakesList, intakeStageId);
        mergeString = allStages.get(ServiceConstants.ALL);
        List<CciInvContactDto> contactsList = cciInvReportDao.getContactList(idStage,mergeString);
        if(!ObjectUtils.isEmpty(contactsList)){
            for(CciInvContactDto contactDto: contactsList){
                List<CciInvReportPersonDto> princCollatList = cciInvReportDao.getPrincCollatList(contactDto.getIdEvent(), contactDto.getIdContactStage());
                contactDto.setPrincCollatList(princCollatList);
            }
        }

        return contactsList;
    }

    private Map<String, String> getAllStages(String mergedStages, GenericCaseInfoDto genericCaseInfoDto,
                                             List<CpsInvIntakePersonPrincipalDto> getIntakesList, Long intakeStageId) {
        // get inv stages
        String invMergeString = ServiceConstants.EMPTY_STR;
        if (StringUtils.isBlank(mergedStages) || ServiceConstants.STR_ZERO_VAL.equals(mergedStages)) {
            invMergeString = String.valueOf(genericCaseInfoDto.getIdStage());
        } else {
            invMergeString = mergedStages + ServiceConstants.COMMA + genericCaseInfoDto.getIdStage();
        }
        // get int stages
        String intMergeString = ServiceConstants.EMPTY_STRING;
        intMergeString = String.valueOf(intakeStageId);
        if (!ObjectUtils.isEmpty(invMergeString)) {
            List<CpsInvIntakePersonPrincipalDto> arIntakes = cpsInvReportDao.getIntakes(invMergeString);

            if (!ObjectUtils.isEmpty(arIntakes)) {
                for (CpsInvIntakePersonPrincipalDto intake : arIntakes) {
                     intMergeString += ServiceConstants.COMMA + intake.getIdPriorStage();
                }
            }
        }

        for (CpsInvIntakePersonPrincipalDto intake : getIntakesList) {
           intMergeString += ServiceConstants.COMMA + intake.getIdPriorStage();
        }
        String allStages = intMergeString;

        allStages += StringUtils.isNotBlank(invMergeString) ? (ServiceConstants.COMMA + invMergeString)
                : ServiceConstants.EMPTY_STR;
        Map<String, String> stages = new HashMap<>();
        if(!ObjectUtils.isEmpty(intMergeString))
        {
            stages.put(ServiceConstants.CSTAGES_INT, intMergeString);
        }
        if(!ObjectUtils.isEmpty(invMergeString))
        {
            stages.put(ServiceConstants.CSTAGES_INV, invMergeString);
        }
       if(!ObjectUtils.isEmpty(allStages))
        {
            stages.put(ServiceConstants.ALL, allStages);
        }
        return stages;
    }

    /**
     * Translates indicator character
     *
     * @param p_ind_char
     * @return
     */
    private String indTranslate(String p_ind_char) {
        String retval = ServiceConstants.NULL_STRING;
        if (p_ind_char != null) {
            if (p_ind_char.equals(ServiceConstants.STRING_IND_Y)) {
                retval = ServiceConstants.YES_TEXT;
            } else if (p_ind_char.equals(ServiceConstants.STRING_IND_N)) {
                retval = ServiceConstants.NO_TEXT;
            } else if (p_ind_char.equals(ServiceConstants.INFO_NEEDED)) {
                retval = ServiceConstants.NEEDS_MORE_INFO;
            }
        }
        return retval;
    }

    private CciInvIntakeDto populateCciIntakeDto(Long idStage){
        StageIncomingDto stageIncomingDto = stageDao.getEarliestIntakeDates(idStage);
        StageValueBeanDto stageInfo = stageDao.retrieveStageInfo(stageIncomingDto.getIdPriorStage());
        CciInvIntakeDto cciInvIntakeDto = new CciInvIntakeDto();
        cciInvIntakeDto.setDtIntakeReceived(stageIncomingDto.getDtIncomingCall());
        cciInvIntakeDto.setIdStage(stageIncomingDto.getIdPriorStage());
        cciInvIntakeDto.setCdStageType(stageInfo.getCdStageType());
        return cciInvIntakeDto;
    }

    private List<CciInvIntakePersonDto> getMergedIntakeNarrative(Long stageId, Long caseId, String cdStage){
        List<CciInvIntakePersonDto> intakesList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(stageId)) {
            if (!ObjectUtils.isEmpty(cpsInvReportDao.getMergeHistory(caseId))) {
                List<Long> mergedStageList = workingNarrDao.spIdStage(caseId,
                        stageId, cdStage);
                if (!ObjectUtils.isEmpty(mergedStageList)) {
                    mergedStageList.forEach(stage ->{
                        intakesList.addAll(cciInvReportDao.getIntakes(stage));
                    });

                }
            }
        }
        return intakesList;
    }

}
