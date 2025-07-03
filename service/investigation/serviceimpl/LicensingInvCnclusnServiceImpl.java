package us.tx.state.dfps.service.investigation.serviceimpl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.CloseStageCaseInputDto;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.notiftolawenforcement.dto.PriorStageDto;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.admin.dao.AllegationStageCaseDao;
import us.tx.state.dfps.service.admin.dao.AllegationStageDao;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dao.UpdateToDoDao;
import us.tx.state.dfps.service.admin.dto.AllegationStageCaseInDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageCaseOutDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageInDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageOutDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDto;
import us.tx.state.dfps.service.admin.dto.FetchEventResultDto;
import us.tx.state.dfps.service.admin.dto.FetchEventRowDto;
import us.tx.state.dfps.service.admin.dto.UpdateToDoDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casemanagement.dao.CPSInvCnlsnDao;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.SituationDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventFetDao;
import us.tx.state.dfps.service.common.dao.SafetyPlanDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dto.StageIncomingDto;
import us.tx.state.dfps.service.dangerindicators.dto.DangerIndicatorsDto;
import us.tx.state.dfps.service.dangerindicators.service.DangerIndicatorsService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.investigation.dao.AllegtnDao;
import us.tx.state.dfps.service.investigation.dao.FacilAllgDtlDao;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstCnclsnDao;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstDtlDao;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstSumDao;
import us.tx.state.dfps.service.investigation.dao.SrvreferralslDao;
import us.tx.state.dfps.service.investigation.dto.*;
import us.tx.state.dfps.service.investigation.service.LicensingInvstCnclusnService;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.person.dao.CriminalHistoryDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.personutility.service.PersonUtilityService;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.resourcesearch.dao.ResourceSearchDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcontractor.service.SbcntrListService;
import us.tx.state.dfps.service.workload.dto.*;
import us.tx.state.dfps.service.workload.service.CloseStageCaseService;
import us.tx.state.dfps.service.workload.service.WorkloadService;

import static java.util.Calendar.*;
import static us.tx.state.dfps.service.common.ServiceConstants.CD_AGENCY_TYPE60;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Service
 * class for sending input to LicensingInvstCnclusnDao class> May 27, 2018-
 * 3:05:39 PM © 2017 Texas Department of Family and Protective Services.
 * * *************** Change History ****************************************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusions
 * 06/09/2020 kanakas artf152402 : Prior investigation overwritten by later
 * 06/10/2020 kanakas artf152403 : Columns not populating on ALLEGED_SX_VCT
 */
@Service
@Transactional
public class LicensingInvCnclusnServiceImpl implements LicensingInvstCnclusnService {

    /** The Constant COR_ACT. */
    public static final String COR_ACT = "20";

    /** The Constant OTHER. */
    public static final String OTHER = "XX";

    /** The Constant VICTIM_DOB_EDIT. */
    public static final int VICTIM_DOB_EDIT = 0;

    /** The Constant PERS_SEARCH_EDIT. */
    public static final int PERS_SEARCH_EDIT = 1;

    /** The Constant PERS_CHARACTER_EDIT. */
    public static final int PERS_CHARACTER_EDIT = 2;

    /** The Constant RSN_DTH_EDIT. */
    public static final int RSN_DTH_EDIT = 6;

    /** The Constant DATE_RSN_DTH_EDIT. */
    public static final int DATE_RSN_DTH_EDIT = 7;

    /** The Constant PERSON_ROLE_BOTH. */
    public static final String PERSON_ROLE_BOTH = "DB";

    /** The Constant PERSON_ROLE_VICTIM. */
    public static final String PERSON_ROLE_VICTIM = "DV";

    /** The Constant PERSON_SEARCH_R. */
    public static final String PERSON_SEARCH_R = "R";

    /** The Constant PERSON_SEARCH_V. */
    public static final String PERSON_SEARCH_V = "V";

    /** The Constant PERSON_STAFF. */
    public static final String PERSON_STAFF = "STF";

    /** The Constant PERSON_TYPE_PRN. */
    public static final String PERSON_TYPE_PRN = "PRN";

    /** The Constant PERSON_TYPE_COL. */
    public static final String PERSON_TYPE_COL = "COL";

    /** The Constant PERSON_OLDEST_VICTIM. */
    public static final String PERSON_OLDEST_VICTIM = "OV";

    /** The Constant AN_IN_OPEN_CASE. */
    public static final String AN_IN_OPEN_CASE = "ABN";

    /** The Constant AN_IN_PRIOR_CASE. */
    public static final String AN_IN_PRIOR_CASE = "ABO";

    /** The Constant AN_NO_PRIOR_CASE. */
    public static final String AN_NO_PRIOR_CASE = "ABP";

    /** The Constant NOT_AN_RELATED. */
    public static final String NOT_AN_RELATED = "NAB";

    /** The Constant EVENT_TYPE_PRIORITY_CHANGE. */
    public static final String EVENT_TYPE_PRIORITY_CHANGE = "PRT";

    /** The Constant EVENT_TYPE_CONTACT. */
    public static final String EVENT_TYPE_CONTACT = "CON";

    /** The Constant EVENT_TYPE_MED_MENTAL_ASSESS. */
    public static final String EVENT_TYPE_MED_MENTAL_ASSESS = "MED";

    /** The Constant ACTION_CODE_CLOSE. */
    public static final String ACTION_CODE_CLOSE = "C";

    public static final String NO = "N";

    private String SAFETY_PLAN_STATUS = "INP";

    @Autowired
    private MessageSource messageSource;
    /** The licensing invst sum dao. */
    @Autowired
    LicensingInvstSumDao licensingInvstSumDao;

    /** The stage dao. */
    @Autowired
    StageDao stageDao;

    /** The event fet dao. */
    @Autowired
    EventFetDao eventFetDao;

    /** The srvreferrals dao. */
    @Autowired
    SrvreferralslDao srvreferralsDao;

    /** The notif to law enforcement dao. */
    @Autowired
    NotifToLawEnforcementDao notifToLawEnforcementDao;

    /** The approval common service. */
    @Autowired
    ApprovalCommonService approvalCommonService;

    /** The post event. */
    @Autowired
    PostEventService postEventService;

    /** The licensing invst dtl dao. */
    @Autowired
    LicensingInvstDtlDao licensingInvstDtlDao;

    @Autowired
    LicensingInvstCnclsnDao licensingInvstCnclsnDao;

    /** The caps case dao. */
    @Autowired
    CapsCaseDao capsCaseDao;

    /** The situation dao. */
    @Autowired
    SituationDao situationDao;

    /** The resource search dao. */
    @Autowired
    ResourceSearchDao resourceSearchDao;

    @Autowired
    LookupDao lookupDao;

    /** The check stage event status service. */
    @Autowired
    CheckStageEventStatusService checkStageEventStatusService;

    /** The facil allg dtl dao. */
    @Autowired
    FacilAllgDtlDao facilAllgDtlDao;

    /** The event stage person link ins upd dao. */
    @Autowired
    EventStagePersonLinkInsUpdDao eventStagePersonLinkInsUpdDao;

    /** The person dao. */
    @Autowired
    PersonDao personDao;

    /** The update to do dao. */
    @Autowired
    UpdateToDoDao updateToDoDao;

    /** The close stage case service. */
    @Autowired
    CloseStageCaseService closeStageCaseService;

    /** The stage person link dao. */
    @Autowired
    StagePersonLinkDao stagePersonLinkDao;

    /** The allegation stage dao. */
    @Autowired
    AllegationStageDao allegationStageDao;

    /** The person utility service. */
    @Autowired
    PersonUtilityService personUtilityService;

    @Autowired
    AllegationStageCaseDao allegationStageCaseDao;

    @Autowired
    CPSInvCnlsnDao cnlsnDao;

    @Autowired
    EventDao eventDao;

    @Autowired
    AllegtnDao allegtnDao;

    @Autowired
    CapsResourceDao capsResourceDao;

    @Autowired
    WorkloadService workLoadService;

    @Autowired
    SafetyPlanDao safetyPlanDao;

    @Autowired
    SbcntrListService sbcntrListService;

    @Autowired
    DangerIndicatorsService dangerIndicatorsService;

    @Autowired
    CriminalHistoryDao criminalHistoryDao;

    private static final Logger log = Logger.getLogger(LicensingInvCnclusnServiceImpl.class);

    /**
     * Method Name: retrieveLicensingInvConclusion Method Description: This
     * method is used to retrieve the information required for Licensing
     * Investigation Conclusion Page
     *
     * @param licensingInvCnclusnReq
     *            the licensing inv cnclusn req
     * @return the licensing inv cnclusn res
     */
    private LicensingInvCnclusnRes retrieveLicensingInvConclusion(LicensingInvCnclusnReq licensingInvCnclusnReq) {
        LicensingInvCnclusnRes licensingInvCnclusnRes = new LicensingInvCnclusnRes();
        PriorStageDto priorStageDto = null;
        StageValueBeanDto stageInfo = stageDao.retrieveStageInfo(licensingInvCnclusnReq.getIdStage());
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
        // retrieve licensing investigation conclusion details based on stage
        // id.
        LicensingInvstDtlDto licensingInvstDtlDto = licensingInvstSumDao
                .getLicensingInvstDtlDaobyParentId(licensingInvCnclusnReq.getIdStage());
        List<AllegedSxVctmztnDto> allegedSxVctmztnDtoList = new ArrayList();
        if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getIdEvent()) || 0L == licensingInvCnclusnReq.getIdEvent()) {
            licensingInvCnclusnReq.setIdEvent(licensingInvstDtlDto.getIdEvent());
        }
        // retrieve investigation restraint list bsed on the idLicngInvstStage
        List<InvstRestraintDto> invstRestraintDtoList = licensingInvstSumDao
                .getInvstConclusionResById(licensingInvstDtlDto.getIdLicngInvstStage());
        if(stageStartDate == null || !DateUtils.isBefore(stageStartDate, Dec2019)){
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
        if (!ObjectUtils.isEmpty(licensingInvCnclusnReq.getIdEvent())) {
            FetchEventDto fetchEventDto = new FetchEventDto();
            fetchEventDto.setIdEvent(licensingInvCnclusnReq.getIdEvent());
            // This call retrieves the event information using event id.
            FetchEventResultDto fetchEventResultDto = eventFetDao.fetchEventDetails(fetchEventDto);
            licensingInvCnclusnRes.setFetchEventRowDto(fetchEventResultDto.getFetchEventRowDto());
            if (ObjectUtils.isEmpty(licensingInvstDtlDto.getIdResource())
                    && ObjectUtils.isEmpty(licensingInvstDtlDto.getIdAffilResource())
                    && ObjectUtils.isEmpty(licensingInvstDtlDto.getNbrAffilAcclaim())
                    && ObjectUtils.isEmpty(licensingInvstDtlDto.getNbrAcclaim())) {
                // retrieves the priorStage information based on idStage
                priorStageDto = notifToLawEnforcementDao
                        .getPriorStagebyId(licensingInvCnclusnReq.getIdStage());
                // retieves the resource facility detail based on the prior
                // stage id
                FacilRtrvRes facilRtrvRes = stageDao.getFacilityDetail(priorStageDto.getIdPriorStage());
                licensingInvstDtlDto.setIdResource(facilRtrvRes.getIdResource());
                licensingInvstDtlDto.setCdRsrcFacilType(facilRtrvRes.getCdIncmgFacilType());
                licensingInvstDtlDto.setAddrCity(facilRtrvRes.getAddrIncmgFacilCity());
                licensingInvstDtlDto.setAddrStLn1(facilRtrvRes.getAddrIncmgFacilStLn1());
                licensingInvstDtlDto.setNmResource(facilRtrvRes.getNmIncmgFacilName());
                licensingInvstDtlDto.setAddrAffilStLn2(facilRtrvRes.getAddrIncmgFacilStLn2());
                licensingInvstDtlDto.setAddrCounty(facilRtrvRes.getCdIncmgFacilCnty());
                licensingInvstDtlDto.setAddrState(facilRtrvRes.getCdIncmgFacilCnty());
                licensingInvstDtlDto.setAddrZip(facilRtrvRes.getAddrIncmgFacilZip());
                licensingInvstDtlDto.setNbrPhone(facilRtrvRes.getNbrIncmgFacilPhone());
                licensingInvstDtlDto.setNbrPhoneExt(facilRtrvRes.getNbrIncmgFacilPhoneExt());
                licensingInvstDtlDto.setNmAffilResource(facilRtrvRes.getNmIncmgFacilAffiliated());
            }
        }
        if (ObjectUtils.isEmpty(licensingInvstDtlDto.getNbrAcclaim())) {
            if (ObjectUtils.isEmpty(priorStageDto)) {
                priorStageDto = notifToLawEnforcementDao.getPriorStagebyId(licensingInvCnclusnReq.getIdStage());
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

        // gets the stage information based on stage id
        StageDto stageDto = stageDao.getStageById(licensingInvCnclusnReq.getIdStage());
        // This call is used to get earliestIntakeDates based on stage id.
        StageIncomingDto stageIncomingDto = stageDao.getEarliestIntakeDates(licensingInvCnclusnReq.getIdStage());
        licensingInvstDtlDto.setDtLicngInvstIntake(stageIncomingDto.getDtIncomingCall());
        licensingInvstDtlDto.setIdPriorStage(stageIncomingDto.getIdPriorStage());
        licensingInvstDtlDto.setAllegedSxVctmztnDtoList(allegedSxVctmztnDtoList);
        licensingInvstDtlDto.setCvsNotificationDtoList(populateCVSNotificationData(allegedSxVctmztnDtoList,
                stageInfo.getIdCase(), stageInfo.getIdStage()));
        licensingInvCnclusnRes.setLicensingInvstDtlDto(licensingInvstDtlDto);
        licensingInvCnclusnRes.setStageDto(stageDto);
        return licensingInvCnclusnRes;
    }

    private List<CVSNotificationDto> populateCVSNotificationData(List<AllegedSxVctmztnDto> allegedSxVctmztnDtoList, Long idCase, Long idStage){
        List<CVSNotificationDto> cvsNotificationDtoList = new ArrayList<>();
        for (AllegedSxVctmztnDto allegedSxVctmztnDto : allegedSxVctmztnDtoList) {
            CVSNotificationDto cvsNotificationDto = new CVSNotificationDto();
            cvsNotificationDto.setIdVictim(allegedSxVctmztnDto.getIdVictim());
            cvsNotificationDto.setIdCaseWorker(allegedSxVctmztnDto.getIdWorkerPerson());
            cvsNotificationDto.setIdSupervisor(allegedSxVctmztnDto.getIdSupervisorPerson());
            cvsNotificationDto.setNameVictim(allegedSxVctmztnDto.getNameVictim());
            cvsNotificationDto.setNameCaseWorker(allegedSxVctmztnDto.getNameSubWorker());
            cvsNotificationDto.setNameSupervisor(allegedSxVctmztnDto.getNameSupervisor());
            List<CvsNotifLog> cvsAutoNotifLogList = licensingInvstDtlDao.getCVSNotificationLog(idCase, allegedSxVctmztnDto.getIdVictim(), ServiceConstants.CMAILTYP_CANE);
			cvsNotificationDto.setAutoEmailSent(ServiceConstants.STRING_IND_N);
            if(!CollectionUtils.isEmpty(cvsAutoNotifLogList)) {
				cvsNotificationDto.setAutoEmailSent(ServiceConstants.STRING_IND_Y);
                cvsNotificationDto.setDateAutoEmailSent(cvsAutoNotifLogList.get(0).getDtEmailSent());
            }
            List<CvsNotifLog> cvsManualNotifLogList = licensingInvstDtlDao.getCVSNotificationLog(idCase, allegedSxVctmztnDto.getIdVictim(), ServiceConstants.CMAILTYP_CMNE);
            if(!CollectionUtils.isEmpty(cvsManualNotifLogList)) {
				cvsNotificationDto.setManualEmailSent(ServiceConstants.STRING_IND_Y.equals(cvsManualNotifLogList.get(0).getIndNotificationSent()) ? ServiceConstants.STRING_IND_Y
						:ServiceConstants.STRING_IND_N);
                cvsNotificationDto.setDateManualEmailSent(cvsManualNotifLogList.get(0).getDtEmailSent());
            }
            List<TodoDto> todoDtoList = licensingInvstDtlDao.getCVSNotificationAlert(idCase, idStage, allegedSxVctmztnDto.getIdVictim());
			cvsNotificationDto.setAlertSent(ServiceConstants.STRING_IND_N);
            if(!CollectionUtils.isEmpty(todoDtoList)) {
				cvsNotificationDto.setAlertSent(ServiceConstants.STRING_IND_Y);
                cvsNotificationDto.setDateAlertSent(todoDtoList.get(0).getDtTodoCreated());
            }
            cvsNotificationDtoList.add(cvsNotificationDto);
        }
        return cvsNotificationDtoList;

    }

    /**
     * This method is used to get licensing investigation conclusion, class,
     * stage and event information.
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public LicensingInvCnclusnRes displayLicensingInvConclusion(LicensingInvCnclusnReq licensingInvCnclusnReq) {
        // gets the stage, event and licensing information
        LicensingInvCnclusnRes licensingInvCnclusnRes = retrieveLicensingInvConclusion(licensingInvCnclusnReq);
        if (!ObjectUtils.isEmpty(licensingInvCnclusnRes)
                && !ObjectUtils.isEmpty(licensingInvCnclusnRes.getLicensingInvstDtlDto())
                && !ObjectUtils.isEmpty(licensingInvCnclusnRes.getLicensingInvstDtlDto().getNbrAcclaim())) {
            List<ClassFacilityDto> classFacilityDtolist = licensingInvstDtlDao.getClassFacilityView(
                    licensingInvCnclusnRes.getLicensingInvstDtlDto().getNbrAcclaim(), null, null, null);
            if (!classFacilityDtolist.isEmpty()) {
                licensingInvCnclusnRes.setClassFacilityDto(classFacilityDtolist.get(0));
                licensingInvCnclusnRes.getLicensingInvstDtlDto().setClassFacilType(classFacilityDtolist.get(0).getDescFacilityType());
                LicensingInvstDtlDto approvalInfo = licensingInvstDtlDao.getApprovalInfo(
                        licensingInvCnclusnReq.getIdStage(), classFacilityDtolist.get(0).getIdClassFacility());
                licensingInvCnclusnRes.getLicensingInvstDtlDto()
                        .setDtLicngInvstExtAprvl(approvalInfo.getDtLicngInvstExtAprvl());
                licensingInvCnclusnRes.getLicensingInvstDtlDto().setTxtExtnAprvlRsn(approvalInfo.getTxtExtnAprvlRsn());
            }
            if (!ObjectUtils.isEmpty(licensingInvCnclusnRes)
                    && !ObjectUtils.isEmpty(licensingInvCnclusnRes.getLicensingInvstDtlDto())
                    && CD_AGENCY_TYPE60.equalsIgnoreCase(licensingInvCnclusnRes.getLicensingInvstDtlDto().getCdRsrcFacilType())
                    && !ObjectUtils.isEmpty(licensingInvCnclusnRes.getLicensingInvstDtlDto().getNbrAffilAcclaim())
                    && ObjectUtils.isEmpty(licensingInvCnclusnRes.getLicensingInvstDtlDto().getClassAffilFacilType())) {
                //Get Class Facility Type for the Agency
                List<ClassFacilityDto> agencyClassFacilityDtolist = licensingInvstDtlDao.getClassFacilityView(
                        licensingInvCnclusnRes.getLicensingInvstDtlDto().getNbrAffilAcclaim(), null, null, ServiceConstants.A);
                if (!agencyClassFacilityDtolist.isEmpty()) {
                    licensingInvCnclusnRes.getLicensingInvstDtlDto().setClassAffilFacilType(agencyClassFacilityDtolist.get(0).getDescFacilityType());
                }
            }
        }
        // This method gets the indicator if all questions were answered as y in
        // allegation page.
        licensingInvCnclusnRes.setDisplayMsgAllegQuestionY(
                licensingInvstDtlDao.fetchAllegQuestionYAnswers(licensingInvCnclusnReq.getIdStage()));

        //artf218948 : add Child Sex/Labor Trafficking Questions
        populateIndLbtrSxtrAllegExist(licensingInvCnclusnReq.getIdStage(), licensingInvCnclusnRes);

        List<Allegation> allegationList = allegtnDao.getAllegationsByIdStage(licensingInvCnclusnReq.getIdStage());

        licensingInvCnclusnRes.setChildUnderSixMessage(populateChildUderSixMessage(allegationList));
        licensingInvCnclusnRes.setIndSafetyPlanInProc(isSafetyPlanInProc(licensingInvCnclusnReq.getIdStage()));
        licensingInvCnclusnRes.setSubContractorResrcIdList(getSubContractorList(licensingInvCnclusnRes.getLicensingInvstDtlDto().getIdResource()));
        licensingInvCnclusnRes.setAllegationInvestigationLetterDto(getPersonChildDeathDetails(allegationList));

        return licensingInvCnclusnRes;
    }

    //artf218948 : This Method checks for LBTR/SXTR allegations exists
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void populateIndLbtrSxtrAllegExist(Long idStage,
                                              LicensingInvCnclusnRes licensingInvCnclusnRes) {
        log.debug("Entering method CallCINVG1D in populateIndLbtrSxtrAllegExist");
        AllegationStageCaseInDto pCINVG1DInputRec = new AllegationStageCaseInDto();
        licensingInvCnclusnRes.setIndLbtrSxtrAllegExist(ServiceConstants.AR_NO);
        pCINVG1DInputRec.setIdStage(idStage);
        List<AllegationStageCaseOutDto> cinvg1doDtos = allegationStageCaseDao.caseExistDtls(pCINVG1DInputRec);
        for (AllegationStageCaseOutDto cinvg1doDto : cinvg1doDtos) {
            if (!TypeConvUtil.isNullOrEmpty(cinvg1doDto)) {
                licensingInvCnclusnRes.setIndLbtrSxtrAllegExist(cinvg1doDto.getIndChildSexLaborTrafficExists());
            }
        }
        log.debug("Exiting method CallCINVG1D in populateIndLbtrSxtrAllegExist");
    }

    /**
     * Method Name: getOverallDispositionExists
     * Method Description: This method is used to rquery the database and see if a stage has an overall disposition
     * present.
     * artf128755 - CCI reporter letter
     *
     * @param licensingInvCnclusnReq stage to be searched is passed as idStage in DTO.
     * @return DTO structure containing the result, LicensingInvCnclusnRes.overallDispositionExists will be set to true
     * or false depending on result.
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public LicensingInvCnclusnRes getOverallDispositionExists(LicensingInvCnclusnReq licensingInvCnclusnReq) {
        long idStage = licensingInvCnclusnReq.getIdStage();
        LicensingInvCnclusnRes retVal = new LicensingInvCnclusnRes();
        retVal.setOverallDispositionExists(licensingInvstDtlDao.getOverallDispositionExists(idStage));
        return retVal;
    }

    /**
     * This service updates information modified on the Licensing Investigation
     * Conclusion window. It updates a row in the LICENSING INVST DTL, EVENT,
     * and STAGE tables. The service also invalidates an approval if one is
     * pending for the current ID EVENT.
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void saveLicensingInvConclusion(LicensingInvCnclusnReq licensingInvCnclusnReq) {
        LicensingInvstDtlDto licensingInvstDtlDto = licensingInvCnclusnReq.getLicensingInvstDtlDto();
        FetchEventRowDto fetchEventRowDto = licensingInvCnclusnReq.getFetchEventRowDto();
        StageDto stageDto = licensingInvCnclusnReq.getStageDto();
        StageDto stageForDate = stageDao.getStageById(licensingInvstDtlDto.getIdLicngInvstStage());
        InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
        inCheckStageEventStatusDto.setCdTask(fetchEventRowDto.getCdTask());
        inCheckStageEventStatusDto.setIdStage(licensingInvstDtlDto.getIdLicngInvstStage());

        //artf129782: Licensing Investigation Conclusion
        String dec2019Dt = lookupDao.simpleDecode(ServiceConstants.CRELDATE,
                ServiceConstants.CRELDATE_DEC_2019_FCL );
        Date Dec2019 = null;
        Date stageStartDate = null;
        try {
            Dec2019 = DateUtils.toJavaDateFromInput(dec2019Dt);
            if(stageForDate != null){
                stageStartDate = stageForDate.getDtStageStart();
            }
        }
        catch(ParseException e) {
            new ServiceLayerException(e.getMessage());

        }
        // gets the indicator if the stage is exists.
        Boolean eventStageStatus = checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
        if (eventStageStatus) {
            if (fetchEventRowDto.getCdEventStatus().equals(ServiceConstants.EVENTSTATUS_PENDING)) {
                ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
                approvalCommonInDto.setIdEvent(fetchEventRowDto.getIdEvent());
                // Invalidates pending approval. The Invalidate Approval
                // Function utilizes six dams to accomplish the following steps:
                // 1) Given the ID EVENT of the functional window find out which
                // approval is related, 2) Update the Approval Event to COMPlete
                // (intervene update check as well), 3) Find all events related
                // to the approval found in step 1 (functional window may be
                // only one part of an approval package), 4) Demote all events
                // found in step 3 (save your ID EVENT which you will update w/
                // Post Event Common Function) from PENDing approval to
                // COMPleted (all events are COMPlete prior to submission for
                // approval), and lastly 4) all supervisors who were asked to
                // approve or reject are invalidated on the Approvers table
                // (their ToDo’s remain).
                approvalCommonService.callCcmn05uService(approvalCommonInDto);
            }
            if (!ObjectUtils.isEmpty(licensingInvstDtlDto.getDtLicngInvstAssigned())
                    && !ObjectUtils.isEmpty(licensingInvstDtlDto.getDtLicngInvstBegun())
                    && !ObjectUtils.isEmpty(licensingInvstDtlDto.getDtLicngInvstComplt())
                    && !ObjectUtils.isEmpty(licensingInvstDtlDto.getDtLicngInvstIntake())
                    && !ObjectUtils.isEmpty(licensingInvstDtlDto.getCdLicngInvstOvrallDisp())
                    && !ObjectUtils.isEmpty(stageDto.getCdStageReasonClosed())) {
                if (!ObjectUtils.isEmpty(licensingInvstDtlDto.getCdLicngInvstCoractn())
                        && COR_ACT.equals(stageDto.getCdStageReasonClosed())) {
                    if ((OTHER.equals(licensingInvstDtlDto.getCdLicngInvstCoractn())
                            && !ObjectUtils.isEmpty(licensingInvstDtlDto.getTxtLicngInvstNoncomp()))
                            || (!OTHER.equals(licensingInvstDtlDto.getCdLicngInvstCoractn()))) {
                        fetchEventRowDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
                    }
                } else if (!COR_ACT.equals(stageDto.getCdStageReasonClosed())) {
                    fetchEventRowDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
                }
            } else {
                fetchEventRowDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_PROCESS);
            }
            PostEventIPDto postEventIPDto = new PostEventIPDto();
            ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
            serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
            BeanUtils.copyProperties(fetchEventRowDto, postEventIPDto);
            postEventIPDto.setIdCase(stageDto.getIdCase());
            postEventIPDto.setEventDescr(fetchEventRowDto.getTxtEventDescr());
            // Update EVENT record
            PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
            if (!ObjectUtils.isEmpty(postEventOPDto.getIdEvent())) {
                LicensingInvstDtl licensingInvstDtl = new LicensingInvstDtl();
                BeanUtils.copyProperties(licensingInvstDtlDto, licensingInvstDtl);
                if (!ObjectUtils.isEmpty(licensingInvstDtlDto.getIdLicngInvstStage())) {
                    Stage stage = stageDao.getStageEntityById(licensingInvstDtlDto.getIdLicngInvstStage());
                    licensingInvstDtl.setStage(stage);
                }
                // Save fields related to the LICENSING INVST DTL table
                licensingInvstDtlDao.saveLicensingInvstDtl(licensingInvstDtl);
                if(licensingInvCnclusnReq.isOperationInfoChanged()){
                    List<SafetyPlan> safetyPlanList = safetyPlanDao.getSafetyPlanList(stageDto.getIdStage(), SAFETY_PLAN_STATUS);
                    if(!safetyPlanList.isEmpty()){
                        licensingInvstDtlDao.updateSafetyPlanFacilityId(stageDto.getIdStage(), SAFETY_PLAN_STATUS);
                    }
                }
                // CallAUDRestraint
                addOrUpdateInvstRestraint(ServiceConstants.REQ_FUNC_CD_DELETE, fetchEventRowDto, licensingInvstDtlDto);
                // CallAUDRestraint
                addOrUpdateInvstRestraint(ServiceConstants.REQ_FUNC_CD_ADD, fetchEventRowDto, licensingInvstDtlDto);
                Stage stage = new Stage();
                BeanUtils.copyProperties(stageDto, stage);
                if (!ObjectUtils.isEmpty(stageDto.getIdCase())) {
                    CapsCase capsCase = capsCaseDao.getCapsCaseEntityById(stageDto.getIdCase());
                    stage.setCapsCase(capsCase);
                }
                if (!ObjectUtils.isEmpty(stageDto.getIdSituation())) {
                    Situation situation = situationDao.getSituationEntityById(stageDto.getIdSituation());
                    stage.setSituation(situation);
                }
                // Update STAGE record
                stageDao.updateStage(stage);
            }
            //artf129782: Licensing Investigation Conclusion
            if(DateUtils.isAfter(stageStartDate, Dec2019)){
                List<AllegedSxVctmztnDto> allegedSxVctmztnDtoList = licensingInvstDtlDto.getAllegedSxVctmztnDtoList();
                if(allegedSxVctmztnDtoList != null && allegedSxVctmztnDtoList.size() != 0){
                    for (AllegedSxVctmztnDto allegedSxVctmztnDto : allegedSxVctmztnDtoList) {
                        List<AllegedSxVctmztnDto> savedAllegedSxVctmztnDtoList = new ArrayList<AllegedSxVctmztnDto>();
                        AllegedSxVctmztnDto savedAllegedSxVctmztnDto = new AllegedSxVctmztnDto();
                        AllegedSxVctmztn allegedSxVctmztn = new AllegedSxVctmztn();
                        allegedSxVctmztn = populateAllegedSxVctmztn(allegedSxVctmztnDto, allegedSxVctmztn, licensingInvstDtlDto.getIdCreatedPerson());
                        savedAllegedSxVctmztnDtoList = licensingInvstSumDao.getAllegedSxVctmztnDtoByStageIdPid(allegedSxVctmztnDto.getIdStage(), allegedSxVctmztnDto.getIdVictim());
                        if  (savedAllegedSxVctmztnDtoList.size() > 0){
                            savedAllegedSxVctmztnDto = savedAllegedSxVctmztnDtoList.get(0);
                        }
                        licensingInvstDtlDao.saveAllegedBehaviorDetails(allegedSxVctmztn, savedAllegedSxVctmztnDto);
                    }
                }
            }
            if(!CollectionUtils.isEmpty(licensingInvCnclusnReq.getLicensingInvstDtlDto().getCvsNotificationDtoList())){
                licensingInvCnclusnReq.getLicensingInvstDtlDto().getCvsNotificationDtoList().stream().forEach(cvsNotificationDto -> createManualEmailNotification(cvsNotificationDto, licensingInvCnclusnReq ));
            }
        }
    }

    private void createManualEmailNotification(CVSNotificationDto cvsNotificationDto, LicensingInvCnclusnReq licensingInvCnclusnReq){
		List<CvsNotifLog> cvsManualNotifLogList = licensingInvstDtlDao.getCVSNotificationLog(licensingInvCnclusnReq.getStageDto().getIdCase(), cvsNotificationDto.getIdVictim(), ServiceConstants.CMAILTYP_CMNE);
        if(ServiceConstants.STRING_IND_Y.equals(cvsNotificationDto.getManualEmailSent())) {
            if(!CollectionUtils.isEmpty(cvsManualNotifLogList)) {
				if(!cvsNotificationDto.getManualEmailSent().equals(cvsManualNotifLogList.get(0).getIndNotificationSent())){
					licensingInvstDtlDao.deleteCvsNotificationLog(cvsManualNotifLogList.get(0));
					saveCvsNotifLog(populateCvsNotificationLog(licensingInvCnclusnReq, null, cvsNotificationDto, ServiceConstants.CMAILTYP_CMNE));
				}else {
					if (!cvsNotificationDto.getDateManualEmailSent().equals(cvsManualNotifLogList.get(0).getDtEmailSent())) {
                    cvsManualNotifLogList.get(0).setDtEmailSent(cvsNotificationDto.getDateManualEmailSent());
                    licensingInvstDtlDao.saveCvsNotificationLog(cvsManualNotifLogList.get(0));
                }
				}
            }else {
				saveCvsNotifLog(populateCvsNotificationLog(licensingInvCnclusnReq, null, cvsNotificationDto, ServiceConstants.CMAILTYP_CMNE));
			}
		}else if(ServiceConstants.STRING_IND_N.equals(cvsNotificationDto.getManualEmailSent())){
			if(!CollectionUtils.isEmpty(cvsManualNotifLogList)) {
				if(!cvsManualNotifLogList.get(0).getIndNotificationSent().equals(cvsNotificationDto.getManualEmailSent())){
					licensingInvstDtlDao.deleteCvsNotificationLog(cvsManualNotifLogList.get(0));
					saveCvsNotifLog(populateCvsNotificationLog( licensingInvCnclusnReq, null, cvsNotificationDto, ServiceConstants.CMAILTYP_CMNE));
				}
			}else{
				saveCvsNotifLog(populateCvsNotificationLog( licensingInvCnclusnReq, null, cvsNotificationDto, ServiceConstants.CMAILTYP_CMNE));
			}
		}
	}

 	private CvsNotifLogDto populateCvsNotificationLog(LicensingInvCnclusnReq licensingInvCnclusnReq, Long emailLogId, CVSNotificationDto cvsNotificationDto,  String emailType){
		CvsNotifLogDto cvsNotifLogDto = new CvsNotifLogDto();
		cvsNotifLogDto.setIdCase(licensingInvCnclusnReq.getStageDto().getIdCase());
		cvsNotifLogDto.setIdEmailLog(emailLogId);
		cvsNotifLogDto.setIdVictimPerson(cvsNotificationDto.getIdVictim());
		cvsNotifLogDto.setCdEmailType(emailType);
		cvsNotifLogDto.setDtEmailSent( cvsNotificationDto.getDateManualEmailSent());
		cvsNotifLogDto.setIdCreatedPerson(licensingInvCnclusnReq.getLicensingInvstDtlDto().getIdCreatedPerson());
		cvsNotifLogDto.setDtCreated(new Date());
		cvsNotifLogDto.setIdLastUpdatePerson(licensingInvCnclusnReq.getLicensingInvstDtlDto().getIdCreatedPerson());
		cvsNotifLogDto.setDtLastUpdate(new Date());
		cvsNotifLogDto.setIndNotificationSent(cvsNotificationDto.getManualEmailSent());
		return cvsNotifLogDto;
    }

    public PostEventIPDto populateCVSNotificationEvent(String txtEventDescription,
                                                       Long personId , Long stageId , Long caseId, Long victimId){
        PostEventIPDto postEventIPDto = new PostEventIPDto();
        postEventIPDto.setIdStage(stageId);
        postEventIPDto.setIdPerson(personId);
        postEventIPDto.setIdCase(caseId);
        postEventIPDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_COMPLETE);
        postEventIPDto.setCdEventType(ServiceConstants.INV_NOTIFICATION_EVENT_TYPE);
        postEventIPDto.setDtEventOccurred(postEventIPDto.getDtEventOccurred());
        postEventIPDto.setEventDescr(txtEventDescription);
        if(!ObjectUtils.isEmpty(victimId)) {
            PostEventDto postEventDto = new PostEventDto();
            postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
            postEventDto.setIdPerson(victimId);
            List<PostEventDto> postEventPersonList = new ArrayList<>();
            postEventPersonList.add(postEventDto);
            postEventIPDto.setPostEventDto(postEventPersonList);
        }
        return postEventIPDto;
    }

    /**
     * This method is used to save the information and validate all the events
     * related to stage and retrieves the error messages.
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public LicensingInvCnclusnRes saveAndSubmitLicensingInvConclusion(LicensingInvCnclusnReq licensingInvCnclusnReq) {
        saveLicensingInvConclusion(licensingInvCnclusnReq);
        // Calls the method to validate all the events related to stage
        LicensingInvCnclusnRes licensingInvCnclusnRes = validateLicInvCnclusnOnSubmit(licensingInvCnclusnReq);
        // Method validates race and ethnicity data for principals in Lice.
        // Investigation.
        Boolean raceStatMissing = personUtilityService.isPRNRaceStatMissing(licensingInvCnclusnReq.getIdStage());
        if (!ObjectUtils.isEmpty(raceStatMissing) && raceStatMissing) {
            addError(25016, licensingInvCnclusnRes.getErrorDtoList());
        }
        // This method decides whether the informational message 'Immediately
        // answer the Child Fatality Allegation question in all allegations for
        // the deceased victime(s).' will be displayed.
        if (licensingInvstDtlDao.fetchAllegQuestionAnswers(licensingInvCnclusnReq.getIdStage())) {
            addError(56090, licensingInvCnclusnRes.getErrorDtoList());
        }
        return licensingInvCnclusnRes;
    }

    /**
     * Method Name: addOrUpdateInvstRestraint Method Description: this method is
     * used update the retraint table.
     *
     * @param cdReqFunc
     *            the code req func
     * @param fetchEventRowDto
     *            the fetch event row dto
     * @param licensingInvstDtlDto
     *            the licensing invst dtl dto
     * @return the invst restraint dto
     */
    private InvstRestraintDto addOrUpdateInvstRestraint(String cdReqFunc, FetchEventRowDto fetchEventRowDto,
                                                        LicensingInvstDtlDto licensingInvstDtlDto) {
        InvstRestraintDto invstRestraintDto = new InvstRestraintDto();
        invstRestraintDto.setReqFuncCd(cdReqFunc);
        invstRestraintDto.setDtLastUpdate(fetchEventRowDto.getDtLastUpdate());
        invstRestraintDto.setIdStage(fetchEventRowDto.getIdStage());
        // deletes the existing records
        if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(cdReqFunc)) {
            licensingInvstDtlDao.invstConclusionRestraintUpdate(invstRestraintDto);
        } else if (!ObjectUtils.isEmpty(licensingInvstDtlDto.getCdLicEmergRestraint())) {
            // Adds the added restraints on the page.
            licensingInvstDtlDto.getCdLicEmergRestraint().forEach(o -> {
                invstRestraintDto.setCdRstraint(o);
                licensingInvstDtlDao.invstConclusionRestraintUpdate(invstRestraintDto);
            });
        }
        return invstRestraintDto;
    }

    /**
     * This method is used to validate the class information entered by the user
     * and retrieves the errors if invalid.
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public LicensingInvCnclusnRes validateAndGetClassInfo(LicensingInvCnclusnReq licensingInvCnclusnReq) {
        LicensingInvCnclusnRes licensingInvCnclusnRes = new LicensingInvCnclusnRes();
        List<ClassFacilityDto> classFacilityDtolist = licensingInvstDtlDao.getClassFacilityView(
                licensingInvCnclusnReq.getNbrRsrcFacilAcclaim(), licensingInvCnclusnReq.getNbrAgency(),
                licensingInvCnclusnReq.getNbrBranch(), licensingInvCnclusnReq.getIndAgencyHome());
        ErrorDto errorDto = new ErrorDto();
        if (classFacilityDtolist.isEmpty()) {
            errorDto.setErrorCode(55032);
        } else if (classFacilityDtolist.size() > 1) {
            errorDto.setErrorCode(9004);
        } else {
            LicensingInvstDtlDto licensingInvstDtlDto = new LicensingInvstDtlDto();
            licensingInvCnclusnRes.setResourceValueBeanDto(licensingInvstDtlDao
                    .getResourceByFacilityNbr(classFacilityDtolist.get(0).getNbrFacility().longValue()));
            licensingInvCnclusnRes.setClassFacilityDto(classFacilityDtolist.get(0));
            LicensingInvstDtlDto approvalInfo = licensingInvstDtlDao.getApprovalInfo(
                    licensingInvCnclusnReq.getIdStage(), classFacilityDtolist.get(0).getIdClassFacility());
            licensingInvstDtlDto.setDtLicngInvstExtAprvl(approvalInfo.getDtLicngInvstExtAprvl());
            licensingInvstDtlDto.setTxtExtnAprvlRsn(approvalInfo.getTxtExtnAprvlRsn());
            licensingInvCnclusnRes.setLicensingInvstDtlDto(licensingInvstDtlDto);
        }
        licensingInvCnclusnRes.setErrorDto(errorDto);
        return licensingInvCnclusnRes;
    }

    @Override
    @Transactional
    public CommonHelperRes stageHasContactTypeToPerson(CommonHelperReq commonHelperReq) {
        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setHasContactToPerson(licensingInvstCnclsnDao.hasContactTypeToPerson(commonHelperReq.getIdStage(), commonHelperReq.getIdContactType(), commonHelperReq.getIdPerson()));
        return commonHelperRes;
    }

    /**
     * This service performs server side validation for the Licensing
     * Investigation Conclusion window. The edits performed by the service
     * depend on the decode string in DCD_EDIT_PROCESS. Once all required edits
     * are passed, the service will 1) set all the to-dos associated with the
     * input ID_EVENT to "COMPLETE", 2) return a list of all the ID_EVENTs
     * associated with the input ID_STAGE, 3) Close the stage and the case if
     * "Save and Close" was selected in the client.
     *
     * @param licensingInvCnclusnReq
     *            the licensing inv cnclusn req
     * @return the licensing inv cnclusn res
     */
    private LicensingInvCnclusnRes validateLicInvCnclusnOnSubmit(LicensingInvCnclusnReq licensingInvCnclusnReq) {
        LicensingInvCnclusnRes licensingInvCnclusnRes = new LicensingInvCnclusnRes();
        List<ErrorDto> errorDtoList = new ArrayList<>();
        boolean indMissingdeathReason = false;
        InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
        inCheckStageEventStatusDto.setCdTask(licensingInvCnclusnReq.getCdTask());
        inCheckStageEventStatusDto.setIdStage(licensingInvCnclusnReq.getIdStage());
        // Call CheckStageEventStatus
        Boolean eventStageStatus = checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
        if (eventStageStatus) {
            // This call handles edits for Victim DOB, Person Search, and Person
            // Characteristics. At least one of these edits is always required.
            validateEachPerson(licensingInvCnclusnReq, errorDtoList);
            List<StagePrincipalDto> stagePrincipalDtoList = getPrinciplesAndReasonofdeath(licensingInvCnclusnReq,
                    errorDtoList, indMissingdeathReason);
            if (!ObjectUtils.isEmpty(stagePrincipalDtoList) && !stagePrincipalDtoList.isEmpty()
                    && !ObjectUtils.isEmpty(licensingInvCnclusnReq.getDcdEditProcess()) && ServiceConstants.STRING_IND_Y
                    .charAt(0) == licensingInvCnclusnReq.getDcdEditProcess().charAt(6)) {
                // for loop to process each PRN and Reason of Death retrieved
                stagePrincipalDtoList.forEach(o -> {
                    licensingInvCnclusnReq.setCdPersonDeath(o.getCdPersonDeath());
                    // This Call checks to see if the person returned is a
                    // victim in a allegation with a disposition of RTB.
                    checkPersonIsVictim(licensingInvCnclusnReq, errorDtoList, indMissingdeathReason, o.getIdPerson());
                });
                /*
                 * for (int i = 0; (i < stagePrincipalDtoList.size() ||
                 * indMissingdeathReason); i++) { // need // to // revisit
                 * licensingInvCnclusnReq.setCdPersonDeath(stagePrincipalDtoList
                 * .get(i).getCdPersonDeath()); // This Call checks to see if
                 * the person returned is a // victim in a allegation with a
                 * disposition of RTB.
                 * checkPersonIsVictim(licensingInvCnclusnReq, errorDtoList,
                 * indMissingdeathReason,
                 * stagePrincipalDtoList.get(i).getIdPerson()); }
                 */

                //artf218948 : validation logic to check for LBTR/SXTR allegations exists but no trafficing details or Questions not answered
                boolean isChildSexLaborTraffic = licensingInvstCnclsnDao.isChildSexLaborTrafficking(licensingInvCnclusnReq.getIdStage());

                List<Allegation> allegationList = cnlsnDao.checkTrfckngRecPendList(licensingInvCnclusnReq.getIdStage());

                if (!isChildSexLaborTraffic) {
                    addError(ServiceConstants.MSG_CHILD_SXTR_LBTR_REQ, errorDtoList);
                }
                boolean isCrimHistPending = criminalHistoryDao.isCrimHistPending(licensingInvCnclusnReq.getIdStage());

                if(isCrimHistPending){
                    addError(ServiceConstants.CRML_HIST_CHECK, errorDtoList);
                    HashMap personDetail = criminalHistoryDao.checkCrimHistAction(licensingInvCnclusnReq.getIdStage());
                    if (!ObjectUtils.isEmpty(personDetail)) {
                        licensingInvCnclusnRes.setPersonDetail(personDetail);
                    }
                }


                //artf220024 : The error message is a link to trafficking page, it needs victim id and name
                if (!CollectionUtils.isEmpty(allegationList)) {
                    allegationList.forEach(algtn ->
                            {
                                switch (algtn.getCdAllegDisposition()) {
                                    case CodesConstant.CDISPSTN_RTB:
                                        addError(ServiceConstants.MSG_INVRTB_TRAFFICKING, "" + algtn.getPersonByIdVictim().getIdPerson() + ":" + algtn.getPersonByIdVictim().getNmPersonFull(), errorDtoList);
                                        break;
                                    case CodesConstant.CDISPSTN_UTD:
                                        addError(ServiceConstants.MSG_INVUTD_TRAFFICKING, "" + algtn.getPersonByIdVictim().getIdPerson() + ":" + algtn.getPersonByIdVictim().getNmPersonFull(), errorDtoList);
                                        break;
                                    default:
                                        break;
                                }
                            }
                    );
                }
            }

            if (ObjectUtils.isEmpty(errorDtoList) || errorDtoList.isEmpty()) {
                UpdateToDoDto updateToDoDto = new UpdateToDoDto();
                updateToDoDto.setIdEvent(licensingInvCnclusnReq.getIdEvent());
                // Set all TODOs associated with event to COMPLETED
                // CINV43D
                updateToDoDao.completeTodo(updateToDoDto);
                // Retrieve all the events associated with the Investigation
                List<Long> events = retrieveEvents(licensingInvCnclusnReq, errorDtoList);
                licensingInvCnclusnRes.setEventIds(events);
                // If "Save and Close" was clicked and no warnings were found,
                // then close the stage and case.
                if (ACTION_CODE_CLOSE.equals(licensingInvCnclusnReq.getReqFuncCd()) && errorDtoList.isEmpty()) {
                    closeStage(licensingInvCnclusnReq, events);
                }
            }
        }

        // Validate Danger Indicators
        DangerIndicatorsDto dangerIndicatorsDto = dangerIndicatorsService.getDangerIndicator(licensingInvCnclusnReq.getIdStage());

        // Fetch based on "IN EFFECT" STATUS
        List<SafetyPlan> safetyPlanList = safetyPlanDao.getSafetyPlanList(licensingInvCnclusnReq.getIdStage(), "INE");

        // Check for each mandatory field.
        if(ObjectUtils.isEmpty(dangerIndicatorsDto) || checkForDangerIndicators(dangerIndicatorsDto)) {
            // MSG_DNG_IND_INFO - Please answer the Danger Indicator questions on Danger Indicators Information page.
            addError(57338, errorDtoList);
            // 1. The system should be able to display the below error on the 'Error List Page' when the User is submitting the Investigation for Approval,
            // if 'Safety Decision' field on 'Danger Indicators Information' page is 'Unsafe. One or more Danger Indicators are present; DC closes the Operation/RC requests respite care for the children'
            // and there is no 'Safety Plan' with status 'In Effect'
        } else if(null != dangerIndicatorsDto.getCdSftyDcsn() && dangerIndicatorsDto.getCdSftyDcsn().equals("USF") && safetyPlanList.isEmpty()){
            // MSG_DNG_IND_SFTY_PLN_INFO - Please add a Safety Plan with status 'In Effect'.
            addError(57340, errorDtoList);
        }

        licensingInvCnclusnRes.setErrorDtoList(errorDtoList);
        return licensingInvCnclusnRes;
    }

    private Boolean checkForDangerIndicators(DangerIndicatorsDto dto){
        if (null != dto) {
            if( ObjectUtils.isEmpty(dto.getIndCgSerPhHarm()) || ObjectUtils.isEmpty(dto.getIndChSexAbSus()) ||
                    ObjectUtils.isEmpty(dto.getIndCgAwPotHarm()) || ObjectUtils.isEmpty(dto.getIndCgNoExpForInj()) ||
                    ObjectUtils.isEmpty(dto.getIndCgDnMeetChNeedsFc()) || ObjectUtils.isEmpty(dto.getIndCgDnMeetChNeedsMed()) ||
                    ObjectUtils.isEmpty(dto.getIndBadLivConds()) || ObjectUtils.isEmpty(dto.getIndCgSubAbCantSupCh()) ||
                    ObjectUtils.isEmpty(dto.getIndDomVioDan()) || ObjectUtils.isEmpty(dto.getIndCgDesChNeg()) ||
                    ObjectUtils.isEmpty(dto.getIndCgDisCantSupCh()) || ObjectUtils.isEmpty(dto.getIndCgRefAccChToInv()) ||
                    ObjectUtils.isEmpty(dto.getIndCgPrMalTrtHist()) || ObjectUtils.isEmpty(dto.getIndOtherDangers()) ||
                    ObjectUtils.isEmpty(dto.getCdSftyDcsn()) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calls the functions that close the Investigation Stage. The
     * CloseStageCase Function is used by those windows which need to close a
     * case.
     *
     * @param licensingInvCnclusnReq
     *            the licensing inv cnclusn req
     * @param events
     *            the events
     */
    private void closeStage(LicensingInvCnclusnReq licensingInvCnclusnReq, List<Long> events) {
        // Update the status of all Investigation Events to Approved
        // CCMN62D
        events.forEach(o -> facilAllgDtlDao.getEventDetailsUpdate(o, ServiceConstants.EVENTSTATUS_APPROVE));
        CloseStageCaseInputDto closeStageCaseInputDto = new CloseStageCaseInputDto();
        closeStageCaseInputDto.setIdStage(licensingInvCnclusnReq.getIdStage());
        closeStageCaseInputDto.setCdStage(licensingInvCnclusnReq.getCdStage());
        closeStageCaseInputDto.setCdStageProgram(licensingInvCnclusnReq.getCdStageprogram());
        closeStageCaseInputDto.setCdStageReasonClosed(licensingInvCnclusnReq.getCdStageReasonClosed());
        // Close the Investigation Stage and the Case
        // CloseStageCase
        closeStageCaseService.closeStageCase(closeStageCaseInputDto);
    }

    /**
     * Calls the DAM to retrieve all the events from the EVENT table given the
     * specific search criteria.
     *
     * @param licensingInvCnclusnReq
     *            the licensing inv cnclusn req
     * @param errorDtoList
     *            the error dto list
     * @return the list
     */
    private List<Long> retrieveEvents(LicensingInvCnclusnReq licensingInvCnclusnReq, List<ErrorDto> errorDtoList) {
        EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
        eventStagePersonLinkInsUpdInDto.setIdStage(licensingInvCnclusnReq.getIdStage());
        eventStagePersonLinkInsUpdInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);

        //Fix for defect 13112 - ANE Ext -APPROVERS.CD_EXTN_DAYS is NULL - FCL Req
        eventStagePersonLinkInsUpdInDto.setCdEventType(ServiceConstants.NO_EVENT_TYPE);
        eventStagePersonLinkInsUpdInDto.setCdTask(ServiceConstants.NO_TASK);

        // CCMN87D
        List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpdOutDtoList = eventStagePersonLinkInsUpdDao
                .getEventAndStatusDtls(eventStagePersonLinkInsUpdInDto);
        eventStagePersonLinkInsUpdOutDtoList = eventStagePersonLinkInsUpdOutDtoList.stream()
                .filter(event -> !ServiceConstants.APPROVAL_EVENT_TYPE.equals(event.getCdEventType())
                        && !ServiceConstants.INV_NOTIFICATION_EVENT_TYPE.equals(event.getCdEventType())
                        && !ServiceConstants.EVENT_TYPE_LETTER.equals(event.getCdEventType())
                        && !ServiceConstants.CD_TASK_CPS_INV_CON.equals(event.getCdTask())
                        && !ServiceConstants.CD_TASK_RCL_INV_CON_EXTREQ.equals(event.getCdTask())
                        && !ServiceConstants.CD_TASK_CCL_INV_CON_EXTREQ.equals(event.getCdTask()))
                .collect(Collectors.toList());
        List<Long> events = new ArrayList<>();
        if (!ObjectUtils.isEmpty(eventStagePersonLinkInsUpdOutDtoList)) {
            boolean stageErrorExists = false;
            for (EventStagePersonLinkInsUpdOutDto eventStagePersonLinkInsUpdOutDto : eventStagePersonLinkInsUpdOutDtoList) {
                if (!ServiceConstants.EVENT_STATUS_COMPLETE
                        .equals(eventStagePersonLinkInsUpdOutDto.getCdEventStatus())) {
                    boolean contactEventNotNew = !(EVENT_TYPE_CONTACT
                            .equals(eventStagePersonLinkInsUpdOutDto.getCdEventType())
                            && ServiceConstants.EVENT_STATUS_NEW
                            .equals(eventStagePersonLinkInsUpdOutDto.getCdEventStatus()));
                    boolean priorityChangeNotNew = !(EVENT_TYPE_PRIORITY_CHANGE
                            .equals(eventStagePersonLinkInsUpdOutDto.getCdEventType())
                            && ServiceConstants.EVENT_STATUS_NEW
                            .equals(eventStagePersonLinkInsUpdOutDto.getCdEventStatus()));
                    boolean medMentalAssessNotNew = !(EVENT_TYPE_MED_MENTAL_ASSESS
                            .equals(eventStagePersonLinkInsUpdOutDto.getCdEventType())
                            && ServiceConstants.EVENT_STATUS_NEW
                            .equals(eventStagePersonLinkInsUpdOutDto.getCdEventStatus()));
                    // A "NEW" Safety pLan Event is eligible for closure
                    boolean safetyPlanEventType = ServiceConstants.SAFETY_PLAN_EVENT_TYPE
                            .equals(eventStagePersonLinkInsUpdOutDto.getCdEventType());
                    // If the event is a "NEW" Contact, "NEW" Priority, or "NEW"
                    // Medical/Mental event, then do not return the "uncompleted
                    // event" warning. The event is eligible for closure
                    if (contactEventNotNew && priorityChangeNotNew && medMentalAssessNotNew
                            && !safetyPlanEventType && !stageErrorExists) {
                        addError(4079, errorDtoList);
                        stageErrorExists = true;
                    }
                }
                events.add(eventStagePersonLinkInsUpdOutDto.getIdEvent());
            }
        }
        return events;
    }

    /**
     * This DAM now checks to see if the person returned is a victim in an
     * allegation with allegation severity of fatal.In order for allegation
     * severity to be enabled for the user to set it to FATAL, disposition would
     * have to be set to RTB.
     *
     * @param licensingInvCnclusnReq
     *            the licensing inv cnclusn req
     * @param errorDtoList
     *            the error dto list
     * @param indMissingdeathReason
     *            the date rsn dth
     * @param idPerson
     *            the id person
     */
    private void checkPersonIsVictim(LicensingInvCnclusnReq licensingInvCnclusnReq, List<ErrorDto> errorDtoList,
                                     boolean indMissingdeathReason, Long idPerson) {
        AllegationStageInDto allegationStageInDto = new AllegationStageInDto();
        allegationStageInDto.setIdStage(licensingInvCnclusnReq.getIdStage());
        allegationStageInDto.setIdPerson(idPerson);
        // CSES97D
        List<AllegationStageOutDto> allegationStageOutDtoList = allegationStageDao
                .getAllegationDtls(allegationStageInDto);
        // In cases where the victim the DAM is searching on has a death code of
        // A/N, if the DAM does not find allegation severity of FATAL, a message
        // is set for the user to fix the inconsistency.
        if (ObjectUtils.isEmpty(allegationStageOutDtoList) || allegationStageOutDtoList.isEmpty()) {
            if ((AN_IN_OPEN_CASE.equals(licensingInvCnclusnReq.getCdPersonDeath())
                    || AN_IN_PRIOR_CASE.equals(licensingInvCnclusnReq.getCdPersonDeath())
                    || AN_NO_PRIOR_CASE.equals(licensingInvCnclusnReq.getCdPersonDeath())) && !indMissingdeathReason) {
                indMissingdeathReason = true;
                addError(4127, errorDtoList);
            }
        }
        // In cases where the victim the DAM is searching on has a death code
        // that is NOT A/N, if the DAM DOES find allegation severity of FATAL, a
        // message is set for the user to fix the inconsistency.
        else if (("FT".equals(allegationStageOutDtoList.get(0).getCdAllegSeverity())
                && NOT_AN_RELATED.equals(licensingInvCnclusnReq.getCdPersonDeath()))
                || (!"FT".equals(allegationStageOutDtoList.get(0).getCdAllegSeverity())
                && (AN_IN_OPEN_CASE.equals(licensingInvCnclusnReq.getCdPersonDeath())
                || AN_IN_PRIOR_CASE.equals(licensingInvCnclusnReq.getCdPersonDeath())
                || AN_NO_PRIOR_CASE.equals(licensingInvCnclusnReq.getCdPersonDeath())))) {
            indMissingdeathReason = true;
            addError(4127, errorDtoList);
        }
    }

    /**
     * This function will perform a retrieve from the joined PERSON and STAGE
     * PERSON LINK tables given the ID STAGE. The function will then validate
     * each person returned, using checks based on the Edit Process that it is
     * passed into the service.
     *
     * @param licensingInvCnclusnReq
     *            the licensing inv cnclusn req
     * @param errorDtoList
     *            the error dto list
     */
    private void validateEachPerson(LicensingInvCnclusnReq licensingInvCnclusnReq, List<ErrorDto> errorDtoList) {
        boolean victimDob = false;
        boolean personSearch = false;
        boolean personCharacter = false;
        int countov = 0;
        // CINV34D
        List<PersonListDto> personListDto = personDao.getPersonListByIdStage(licensingInvCnclusnReq.getIdStage(),
                PERSON_STAFF);
        // Loop through personListDto, running validation processes
        // Once than an edit flag has been set to TRUE, that same edit is not
        // performed again. If all three edit flags have been set, we exit out
        // of for loop
        for (PersonListDto personDto : personListDto) {
            boolean unknownName = false;
            // Check to see if the name is unknown. Unknown names are defined as
            // system generated unknown names.System generated unknown names
            // populate the full name of a person with Unknown #(being either a
            // number from 1-9 or a person id). The first and last name fields
            // will
            // be left blank.Therefore, if the first and last names are
            // blank,then
            // the name is system generated, so set the unknown name flag to
            // true
            if (ObjectUtils.isEmpty(personDto.getNmPersonFirst()) && ObjectUtils.isEmpty(personDto.getNmpersonLast())) {
                unknownName = true;
            }
            // If person type is Collateral do not set edit flag
            if (!ObjectUtils.isEmpty(licensingInvCnclusnReq.getDcdEditProcess())
                    && ServiceConstants.STRING_IND_Y.charAt(0) == licensingInvCnclusnReq.getDcdEditProcess().charAt(1)
                    && !PERSON_SEARCH_R.equals(personDto.getIndStagePersSearch())
                    && !PERSON_SEARCH_V.equals(personDto.getIndStagePersSearch())
                    && !PERSON_TYPE_COL.equals(personDto.getStagePersType()) && !personSearch && !unknownName) {
                personSearch = true;
            }
            // Person Characteristics should only be required for principals
            // (PRNs). The following if-statement has been modified accordingly.
            // If Person Characteristics are required and none are entered, set
            // flag to post warning. Also if the name is not unknown, then set
            // the edit flag.
            // This is only evaluated to TRUE in the if statement if the field
            // is NULL. But the field can be saved back to '0', and the error is
            // not being received, and it should be. So, added to the if
            // statement checking for NULL.
            if (!ObjectUtils.isEmpty(licensingInvCnclusnReq.getDcdEditProcess())
                    && ServiceConstants.STRING_IND_Y.charAt(0) == licensingInvCnclusnReq.getDcdEditProcess().charAt(2)
                    && ObjectUtils.isEmpty(personDto.getPersonChar())
                    && PERSON_TYPE_PRN.equals(personDto.getStagePersType()) && !personCharacter && !unknownName) {
                personCharacter = true;
            }
            // Check that all principals, not just victims, have a DOB. If one
            // does not, set flag to post warning. Also if the name is not
            // unknown, then set the edit flag.
            if (!ObjectUtils.isEmpty(licensingInvCnclusnReq.getDcdEditProcess())
                    && ServiceConstants.STRING_IND_Y.charAt(0) == licensingInvCnclusnReq.getDcdEditProcess().charAt(0)
                    && PERSON_TYPE_PRN.equals(personDto.getStagePersType())
                    && ObjectUtils.isEmpty(personDto.getDtPersonBirth()) && !victimDob && !unknownName) {
                victimDob = true;
            }
            // If Person role is OV increment the countov by 1
            if (PERSON_OLDEST_VICTIM.equals(personDto.getStagePersRelInt())) {
                countov++;
            }
            // Break out of for loop if all warning flags are set
            if (personCharacter && victimDob && personSearch) {
                break;
            }
        }
        // Post Person Characteristics warning if flag set
        if (personCharacter)
            addError(4054, errorDtoList);
        // Post Victim Date of Birth warning if flag set
        if (victimDob)
            addError(4055, errorDtoList);
        // Post Person Search warning if flag set
        if (personSearch)
            addError(4056, errorDtoList);
        // If countov is greater than add to error list the error message
        // MSG_INT_ONE_PRINCIPAL_OV
        if (countov > 1)
            addError(3048, errorDtoList);
    }

    /**
     * This method will retrieve a list of principals in the home and data about
     * these people using Id Stage from input.
     *
     * @param licensingInvCnclusnReq
     *            the licensing inv cnclusn req
     * @param errorDtoList
     *            the error dto list
     * @param indMissingdeathReason
     *            the date rsn dth
     * @return the principles and reasonofdeath
     */
    private List<StagePrincipalDto> getPrinciplesAndReasonofdeath(LicensingInvCnclusnReq licensingInvCnclusnReq,
                                                                  List<ErrorDto> errorDtoList, boolean indMissingdeathReason) {
        // CLSC18D
        List<StagePrincipalDto> stagePrincipalDtoList = stagePersonLinkDao
                .getStagePrincipalByIdStageType(licensingInvCnclusnReq.getIdStage(), PERSON_TYPE_PRN);
        if (!stagePrincipalDtoList.isEmpty()) {
            // Check for the condition to determine if the date of death field
            // is not null and the reason for death field is null. Populate
            // output message
            if (!ObjectUtils.isEmpty(licensingInvCnclusnReq.getDcdEditProcess()) && ServiceConstants.STRING_IND_Y
                    .charAt(0) == licensingInvCnclusnReq.getDcdEditProcess().charAt(7)) {
                for (StagePrincipalDto stagePrincipalDto : stagePrincipalDtoList) {
                    if (!ObjectUtils.isEmpty(stagePrincipalDto.getDtPersonDeath())
                            && ObjectUtils.isEmpty(stagePrincipalDto.getCdPersonDeath()) && !indMissingdeathReason) {
                        indMissingdeathReason = true;
                        break;
                    }
                }
            }

        }
        if (indMissingdeathReason) {
            addError(4132, errorDtoList);
        }
        return stagePrincipalDtoList;
    }

    /**
     * Adds the error.
     *
     * @param errorCode
     *            the error code
     * @param errorDtoList
     *            the error dto list
     */
    private void addError(int errorCode, List<ErrorDto> errorDtoList) {
        ErrorDto error = new ErrorDto();
        error.setErrorCode(errorCode);
        errorDtoList.add(error);
    }

    private void addError(int errorCode, String message, List<ErrorDto> errorDtoList) {
        ErrorDto error = new ErrorDto();
        error.setErrorCode(errorCode);
        error.setErrorMsg(message);
        errorDtoList.add(error);
    }

    /**
     * artf129782: Licensing Investigation Conclusion
     * @param allegedSxVctmztnDto
     * @param allegedSxVctmztn
     * @param idCreatedPerson
     * @return
     */
    private AllegedSxVctmztn  populateAllegedSxVctmztn(AllegedSxVctmztnDto allegedSxVctmztnDto, AllegedSxVctmztn allegedSxVctmztn, long idPerson){

        Person victim = new Person();
        Stage stage = new Stage();
        Stage subStage = new Stage();
        Person subWorker = new Person();
        Person supervisor = new Person();

        if (allegedSxVctmztnDto.getIdAllegedSxVctmztn() != 0) {
            allegedSxVctmztn.setIdLastUpdatePerson(new BigDecimal(idPerson));
            allegedSxVctmztn.setIdAllegedSxVctmztn(allegedSxVctmztnDto.getIdAllegedSxVctmztn());
        }
        else{
            allegedSxVctmztn.setIdCreatedPerson(new BigDecimal(idPerson));
            allegedSxVctmztn.setIdLastUpdatePerson(new BigDecimal(idPerson));
        }
        if (allegedSxVctmztnDto.getIdVictim() != null) {
            victim.setIdPerson(allegedSxVctmztnDto.getIdVictim());
            allegedSxVctmztn.setVictim(victim);
        }
        if (allegedSxVctmztnDto.getIdStage() != null) {

            stage.setIdStage(allegedSxVctmztnDto.getIdStage());
            allegedSxVctmztn.setStage(stage);
        }
        if (allegedSxVctmztnDto.getIdSubStage() != null) {
            subStage.setIdStage(allegedSxVctmztnDto.getIdSubStage());
            allegedSxVctmztn.setSubStage(subStage);
        }
        if (allegedSxVctmztnDto.getIdSupervisorPerson() != null) {
            subWorker.setIdPerson(allegedSxVctmztnDto.getIdWorkerPerson());
            allegedSxVctmztn.setSubWorker(subWorker);
        }
        if (allegedSxVctmztnDto.getIdWorkerPerson() != null) {
            supervisor.setIdPerson(allegedSxVctmztnDto.getIdSupervisorPerson());
            allegedSxVctmztn.setSupervisor(supervisor);
        }
        if (allegedSxVctmztnDto.getIndAllegedHumanTrafficking() == null) {
            allegedSxVctmztn.setIndAllegedHumanTrafficking(NO);
        }
        else {
            allegedSxVctmztn.setIndAllegedHumanTrafficking(allegedSxVctmztnDto.getIndAllegedHumanTrafficking());
        }
        if (allegedSxVctmztnDto.getIndAllegedSxAggression() == null) {
            allegedSxVctmztn.setIndAllegedSxAggression(NO);
        }
        else {
            allegedSxVctmztn.setIndAllegedSxAggression(allegedSxVctmztnDto.getIndAllegedSxAggression());
        }
        if (allegedSxVctmztnDto.getIndAllegedSxBehaviorProblem() == null) {
            allegedSxVctmztn.setIndAllegedSxBehaviorProblem(NO);
        }
        else {
            allegedSxVctmztn.setIndAllegedSxBehaviorProblem(allegedSxVctmztnDto.getIndAllegedSxBehaviorProblem());
        }
        if (allegedSxVctmztnDto.getIndAllegedVctmCsa() == null) {
            allegedSxVctmztn.setIndAllegedVctmCsa(NO);
        }
        else {
            allegedSxVctmztn.setIndAllegedVctmCsa(allegedSxVctmztnDto.getIndAllegedVctmCsa());
        }
        return allegedSxVctmztn;

    }

    /**
     * Save alleged behavior
     * artf129782: Licensing Investigation Conclusion
     * updated the code for artf152402
     * @param licensingInvCnclusnReq
     *            the licensing inv cnclusn req
     */
    public LicensingInvCnclusnRes saveAllegedBehavior(LicensingInvCnclusnReq licensingInvCnclusnReq){
        LicensingInvCnclusnRes licensingInvCnclusnRes = new LicensingInvCnclusnRes();
        LicensingInvstDtlDto licensingInvstDtlDto = licensingInvCnclusnReq.getLicensingInvstDtlDto();
        StageDto stageForDate = stageDao.getStageById(licensingInvstDtlDto.getIdLicngInvstStage());
        String dec2019Dt = lookupDao.simpleDecode(ServiceConstants.CRELDATE,
                ServiceConstants.CRELDATE_DEC_2019_FCL );
        Date Dec2019 = null;
        Date stageStartDate = null;
        try {
            Dec2019 = DateUtils.toJavaDateFromInput(dec2019Dt);
            if(stageForDate != null){
                stageStartDate = stageForDate.getDtStageStart();
            }
        }
        catch(ParseException e) {
            new ServiceLayerException(e.getMessage());

        }
        if(stageStartDate == null || DateUtils.isAfter(stageStartDate, Dec2019)){
            List<AllegedSxVctmztnDto> allegedSxVctmztnDtoList = licensingInvstDtlDto.getAllegedSxVctmztnDtoList();
            //artf152402 - updated the code to prevent overwriting the rown in the ALLEGED_SX_VCT for the previous investigation
            if(allegedSxVctmztnDtoList != null && allegedSxVctmztnDtoList.size() != 0){
                for (AllegedSxVctmztnDto allegedSxVctmztnDto : allegedSxVctmztnDtoList) {
                    List<AllegedSxVctmztnDto> savedAllegedSxVctmztnDtoList = new ArrayList<AllegedSxVctmztnDto>();
                    AllegedSxVctmztnDto savedAllegedSxVctmztnDto = new AllegedSxVctmztnDto();
                    AllegedSxVctmztn allegedSxVctmztn = new AllegedSxVctmztn();
                    allegedSxVctmztn = populateAllegedSxVctmztn(allegedSxVctmztnDto, allegedSxVctmztn, licensingInvstDtlDto.getIdCreatedPerson());
                    savedAllegedSxVctmztnDtoList = licensingInvstSumDao.getAllegedSxVctmztnDtoByStageIdPid(allegedSxVctmztnDto.getIdStage(), allegedSxVctmztnDto.getIdVictim());
                    if  (savedAllegedSxVctmztnDtoList.size() > 0){
                        savedAllegedSxVctmztnDto = savedAllegedSxVctmztnDtoList.get(0);
                    }
                    licensingInvstDtlDao.saveAllegedBehaviorDetails(allegedSxVctmztn, savedAllegedSxVctmztnDto);
                }
            }
        }
        return licensingInvCnclusnRes;
    }

    /**
     * Method Name: updateResourceName
     * Method Description: This method is used to update case name, stage name and county if IMPACT Operation Resource ID
     * is different than the original resource Id
     * @param nameChangeReq
     * @return CommonHelperRes
     */
    @Transactional
    public CommonHelperRes updateResourceNameAndCounty(NameChangeReq nameChangeReq){
        CommonHelperRes response = new CommonHelperRes();
        ResourceDto resourceDto = capsResourceDao.getResourceById(nameChangeReq.getUpdatedResourceId());
        licensingInvstDtlDao.updateCaseNameAndCounty(nameChangeReq, resourceDto.getCdRsrcCnty());
        licensingInvstDtlDao.updateStageNameAndCounty(nameChangeReq, resourceDto.getCdRsrcCnty());
        Long eventId = createNameChangeEvent(nameChangeReq);
        response.setIdEvent(eventId);
        return response;
    }

    /**
     * Create an event in Event with desc containing old name, new name
     * @param nameChangeReq
     * @return
     */
    private Long createNameChangeEvent(NameChangeReq nameChangeReq) {
        Event event = new Event();
        String currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
        event.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
        event.setCdEventType(ServiceConstants.NAME_CHANGE_EVENT_TYPE);
        event.setTxtEventDescr(String.format(ServiceConstants.NAME_CHANGE_EVENT_DEC, nameChangeReq.getExistingResourceName(), ObjectUtils.isEmpty(nameChangeReq.getExistingResourceId()) ? "" : nameChangeReq.getExistingResourceId(),
                nameChangeReq.getUpdatedResourceName(), nameChangeReq.getUpdatedResourceId(), nameChangeReq.getPageName(), currentTime));
        event.setDtEventOccurred(new Date());
        event.setDtLastUpdate(new Date());
        event.setDtEventCreated(new Date());
        event.setStage(stageDao.getStageEntityById(nameChangeReq.getStageId()));
        event.setIdCase(nameChangeReq.getCaseId());
        Person person = new Person();
        person.setIdPerson(nameChangeReq.getUserId());
        event.setPerson(person);
        return eventDao.updateEvent(event, ServiceConstants.REQ_FUNC_CD_ADD);
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

    private String populateChildUderSixMessage( List<Allegation> allegationList) {
        String childUderSixMessage = ServiceConstants.EMPTY_STRING;
        String systemGeneratedRespone = ServiceConstants.NO_TEXT;

        int yesCount = (int) allegationList.stream().filter(e ->
                !ObjectUtils.isEmpty(e.getDtIncdnt()) && !ObjectUtils.isEmpty(e.getPersonByIdVictim())
                        && !ObjectUtils.isEmpty(e.getPersonByIdVictim().getDtPersonBirth())
                        && (getAgeInYears(e.getPersonByIdVictim().getDtPersonBirth(), e.getDtIncdnt())<6)
                        && !ServiceConstants.STRING_IND_Y.equals(e.getIndDtInjury())
                        && !ServiceConstants.STRING_IND_Y.equals(e.getPersonByIdVictim().getIndPersonDobApprox())).count();

        int unableToDetermineCount = (int) allegationList.stream().filter(e ->
                (ObjectUtils.isEmpty(e.getDtIncdnt()) && ServiceConstants.CSTAGES_INT.equals(e.getCdAllegIncidentStage()))
                        || ServiceConstants.STRING_IND_Y.equals(e.getIndDtInjury())
                        ||ServiceConstants.STRING_IND_Y.equals(e.getPersonByIdVictim().getIndPersonDobApprox())).count();

        if ( yesCount > 0) {
            systemGeneratedRespone = ServiceConstants.QUOTES_YES_TEXT;
        } else {
            systemGeneratedRespone = unableToDetermineCount > 0 ? ServiceConstants.TEXT_UNABLE_TO_DETERMINE :
                    ServiceConstants.QUOTES_NO_TEXT;
        }
        childUderSixMessage = messageSource.getMessage(ServiceConstants.MSG_LICENSING_INV_CONCLUSION_CHILD_UNDERSIX,
                new String[]{systemGeneratedRespone}, Locale.US);
        return childUderSixMessage;
    }

    private String isSafetyPlanInProc(Long idStage){
        List<SafetyPlan> safetyPlanList = safetyPlanDao.getSafetyPlanList(idStage, SAFETY_PLAN_STATUS);
        return safetyPlanList.isEmpty()? ServiceConstants.N : ServiceConstants.Y;
    }

    private List<Long> getSubContractorList(Long idResource){
        if(!ObjectUtils.isEmpty(idResource)) {
            SubcontrListRtrvReq pInputMsg = new SubcontrListRtrvReq();
            pInputMsg.setIdRsrcLinkParent(idResource);
            SubcontrListRtrvRes subcontrListRtrvRes = sbcntrListService.findSubcontractor(pInputMsg);
            if (!ObjectUtils.isEmpty(subcontrListRtrvRes) && !ObjectUtils.isEmpty(subcontrListRtrvRes.getSbcntrListRtrvoDtoList())) {
                return subcontrListRtrvRes.getSbcntrListRtrvoDtoList().stream().map(e -> e.getIdRsrcLinkChild()).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    private int getAgeInYears(Date dob, Date aDate) {
        Calendar dobCal = Calendar.getInstance(Locale.US);
        dobCal.setTime(dob);
        Calendar aDateCal = Calendar.getInstance(Locale.US);
        aDateCal.setTime(aDate);
        int diff = aDateCal.get(YEAR) - dobCal.get(YEAR);
        if (dobCal.get(MONTH) > aDateCal.get(MONTH) ||
                (dobCal.get(MONTH) == aDateCal.get(MONTH) && dobCal.get(DATE) > aDateCal.get(DATE))) {
            diff--;
        }
        return diff;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public Long saveCvsNotifLog( CvsNotifLogDto cvsNotifLogDto) {
        CvsNotifLog cvsNotifLog = new CvsNotifLog();
        cvsNotifLog.setIdCase(cvsNotifLogDto.getIdCase());
        cvsNotifLog.setIdEvent(cvsNotifLogDto.getIdEvent());
        cvsNotifLog.setIdEmailLog(cvsNotifLogDto.getIdEmailLog());
        cvsNotifLog.setIdVictimPerson(cvsNotifLogDto.getIdVictimPerson());
        cvsNotifLog.setCdEmailType(cvsNotifLogDto.getCdEmailType());
		cvsNotifLog.setDtEmailSent(cvsNotifLogDto.getDtEmailSent());
        cvsNotifLog.setIdCreatedPerson(cvsNotifLogDto.getIdCreatedPerson());
        cvsNotifLog.setDtCreated(new Date());
        cvsNotifLog.setIdLastUpdatePerson(cvsNotifLogDto.getIdLastUpdatePerson());
        cvsNotifLog.setDtLastUpdate(new Date());
		cvsNotifLog.setIndNotificationSent(cvsNotifLogDto.getIndNotificationSent());
        return licensingInvstDtlDao.saveCvsNotificationLog(cvsNotifLog);
    }

    /**
     * Method Name: getIntakeStage
     * Method Description:This method is used to get
     * the intake date of the Intake stage.
     *
     * @param idStage
     *            - The id of the current stage.
     * @return LicensingInvCnclusnRes
     */
    public LicensingInvCnclusnRes getIntakeDate(Long idStage) {
        LicensingInvCnclusnRes licensingInvCnclusnRes = new LicensingInvCnclusnRes();
        LicensingInvstDtlDto licensingInvstDtlDto = new LicensingInvstDtlDto();
        licensingInvstDtlDto.setDtLicngInvstIntake(stageDao.getEarliestIntakeDates(idStage).getDtIncomingCall());
        licensingInvCnclusnRes.setLicensingInvstDtlDto(licensingInvstDtlDto);
        return licensingInvCnclusnRes;
    }

    /**
     * Method Name: hasManualNotification
     * Method Description: This method is used to check if manual notification is sent for the person
     * @param caseId
     * @param personId
     * @return CommonHelperRes
     */
    @Transactional
    public CommonHelperRes hasManualNotification(Long caseId, Long personId){
        CommonHelperRes response = new CommonHelperRes();
        List<CvsNotifLog> cvsManualNotifLogList = licensingInvstDtlDao.getCVSNotificationLog(caseId, personId, ServiceConstants.CMAILTYP_CMNE);
        Boolean result = cvsManualNotifLogList.stream().anyMatch(cvsManualNotif -> !ObjectUtils.isEmpty(cvsManualNotif.getDtEmailSent()) && ServiceConstants.STRING_IND_Y.equals(cvsManualNotif.getIndNotificationSent()));
        response.setResult(result);
        return response;
    }
}