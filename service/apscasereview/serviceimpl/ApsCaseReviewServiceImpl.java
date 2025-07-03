package us.tx.state.dfps.service.apscasereview.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.ApsInvstDetail;
import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.common.dto.WorkerDetailDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.FormattingHelper;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewContactNamesDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewServiceDto;
import us.tx.state.dfps.service.apscasereview.ApsStagePersonDto;
import us.tx.state.dfps.service.apscasereview.dao.ApsCaseReviewDao;
import us.tx.state.dfps.service.apscasereview.dao.ApsCaseReviewLegacyDao;
import us.tx.state.dfps.service.apscasereview.service.ApsCaseReviewService;
import us.tx.state.dfps.service.apsrora.dao.ApsRoraDao;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraDto;
import us.tx.state.dfps.service.apsrora.service.ApsRoraService;
import us.tx.state.dfps.service.apssafetyassmt.dao.ApsSafetyAssessmentDao;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentContactDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentDto;
import us.tx.state.dfps.service.apssafetyassmt.service.ApsSafetyAssessmentService;
import us.tx.state.dfps.service.apsserviceplan.dao.ApsServicePlanDao;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanCompleteDto;
import us.tx.state.dfps.service.apsserviceplan.service.ApsServicePlanService;
import us.tx.state.dfps.service.apssna.dao.ApsSnaDao;
import us.tx.state.dfps.service.apssna.dto.ApsStrengthsAndNeedsAssessmentDto;
import us.tx.state.dfps.service.apssna.service.ApsSnaFormService;
import us.tx.state.dfps.service.casemanagement.dao.CpsIntakeNotificationDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.CaseSummaryDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ApsCaseReviewRequest;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsCaseReviewShieldVersionPrefillData;
import us.tx.state.dfps.service.intake.dto.IncmgDetermFactorsDto;
import us.tx.state.dfps.service.investigation.dao.ApsInvstDetailDao;
import us.tx.state.dfps.service.person.dao.AllegationDao;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.ServiceAuthDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps case review
 * Jan 21, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Service
public class ApsCaseReviewServiceImpl implements ApsCaseReviewService {


    @Autowired
    ApsSafetyAssessmentDao apsSafetyAssessmentDao;

    @Autowired
    DisasterPlanDao disasterPlanDao;

    @Autowired
    ApsInvstDetailDao apsInvstDetailDao;

    @Autowired
    CpsIntakeNotificationDao cpsIntakeNotificationDao;

    @Autowired
    private CpsIntakeReportDao cpsIntakeReportDao;

    @Autowired
    private StageDao stageDao;

    @Autowired
    private ApsCaseReviewDao apsCaseReviewDao;

    @Autowired
    ApsRoraDao apsRoraDao;

    @Autowired
    AllegationDao allegationDao;

    @Autowired
    StagePersonLinkDao stagePersonLinkDao;

    @Autowired
    ApsSnaDao apsSnaDao;

    @Autowired
    ApsServicePlanDao apsServicePlanDao;

    @Autowired
    ServiceAuthorizationDao serviceAuthorizationDao;

    @Autowired
    ApsSafetyAssessmentService apsSafetyAssessmentService;

    @Autowired
    ApsRoraService apsRoraService;

    @Autowired
    ApsSnaFormService apsSnaService;

    @Autowired
    ApsServicePlanService apsServicePlanService;

    @Autowired
    ApsCaseReviewShieldVersionPrefillData apsCaseReviewShieldVersionPrefillData;

    @Autowired
    CaseSummaryDao caseSummaryDao;

    @Autowired
    private ApsCaseReviewLegacyDao apsCaseReviewLegacyDao;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getApsCaseReviewShieldVersionInformation(ApsCaseReviewRequest apsCaseReviewRequest) {

        ApsCaseReviewServiceDto apsCaseReviewServiceDto = new ApsCaseReviewServiceDto();

        List<ApsCaseReviewDto> apsCaseReviewDtoList = getApsCaseReview(apsCaseReviewRequest);
        apsCaseReviewServiceDto.setApsCaseReviewDtoList(apsCaseReviewDtoList);

        StageValueBeanDto stageValueBeanDto = getStageCaseInfo(apsCaseReviewRequest.getIdCase());
        apsCaseReviewServiceDto.setStageValueBeanDto(stageValueBeanDto);

        List<ApsSafetyAssessmentContactDto> apsSaContactList = getSaContacts(apsCaseReviewRequest.getIdCase());
        apsCaseReviewServiceDto.setApsSaContactList(apsSaContactList);

        List<ApsCaseReviewContactNamesDto> apsCaseReviewContactNamesDtoList = getPersonsContactedByCase(apsCaseReviewRequest.getIdCase());
        apsCaseReviewServiceDto.setApsCaseReviewContactNamesDtoList(apsCaseReviewContactNamesDtoList);

        List<ApsSafetyAssessmentDto> safetyEventList = getSafetyAssessmentsEventsByCase(apsCaseReviewRequest.getIdCase());
        apsCaseReviewServiceDto.setSafetyEventList(safetyEventList);

        List<ApsSafetyAssessmentDto> safetyDbList = getSafetyDbList(safetyEventList);
        apsCaseReviewServiceDto.setSafetyDbList(safetyDbList);

        List<ApsRoraDto> apsRoraDtoList = getApsRoraInfo(apsCaseReviewRequest.getIdCase());
        apsCaseReviewServiceDto.setApsRoraDtoList(apsRoraDtoList);

        List<ApsStrengthsAndNeedsAssessmentDto> apsSnaDtoList = getApsSnaDetails(apsCaseReviewRequest.getIdCase());
        apsCaseReviewServiceDto.setApsSnaDtoList(apsSnaDtoList);

        List<AllegationDto> allegationDtoList = getAllegationDetails(apsCaseReviewRequest.getIdCase());
        apsCaseReviewServiceDto.setAllegationDtoList(allegationDtoList);

        List<ApsStagePersonDto> principalDtoList = getPersonsInfo(apsCaseReviewRequest.getIdCase(),ServiceConstants.PRINCIPAL);
        apsCaseReviewServiceDto.setPrincipalDtoList(principalDtoList);

        List<ApsStagePersonDto> collateralDtoList = getPersonsInfo(apsCaseReviewRequest.getIdCase(),ServiceConstants.COLLATERAL);
        apsCaseReviewServiceDto.setCollateralDtoList(collateralDtoList);

        List<ApsInvstDetail> apsInvestigationDetailList = getInvestigationDetails(apsCaseReviewDtoList);
        apsCaseReviewServiceDto.setApsInvstDetailList(apsInvestigationDetailList);

        List<StageValueBeanDto> svcDeliveryClosureCheckList = getSvcDeliveryClosureCheckList(apsCaseReviewRequest.getIdCase());
        apsCaseReviewServiceDto.setSvcDeliveryClosureCheckList(svcDeliveryClosureCheckList);

        List<ServiceAuthDto> svcAuthEventInfoList = getSvcAuthEventInfo (apsCaseReviewRequest.getIdCase());
        apsCaseReviewServiceDto.setSvcAuthEventInfoList(svcAuthEventInfoList);

        return apsCaseReviewShieldVersionPrefillData.returnPrefillData(apsCaseReviewServiceDto);
    }


    /**
     * Method getInvestigationDetails  returns the Investigation details of stage
     * @param apsCaseReviewDtoList
     * @return List<ApsInvstDetail>
     */
    private List<ApsInvstDetail> getInvestigationDetails(List<ApsCaseReviewDto> apsCaseReviewDtoList) {
        List<ApsInvstDetail>  apsInvstDetailList = null;
        if (!apsCaseReviewDtoList.isEmpty()) {
            Long stageId = apsCaseReviewDtoList.get(0).getInvStageId();
            apsInvstDetailList = apsInvstDetailDao.getApsInvstDetailbyParentId(stageId);
        }
        return apsInvstDetailList;
    }

    /**
     * Method getPersonsInfo  returns the Investigation details of stage
     * @param idCase
     * @param cdStagePersonType
     * @return List<ApsStagePersonDto>
     */
    private List<ApsStagePersonDto> getPersonsInfo(Long idCase, String cdStagePersonType) {
        List<ApsStagePersonDto> personDtoList = new ArrayList<>();
        List<StagePersonLinkDto> stagePersonLinkDtoList = stagePersonLinkDao.getStgPersonLinkDtlsBasedOnCase(idCase, cdStagePersonType);
        stagePersonLinkDtoList.forEach(stagePersonLinkDto -> {
            ApsStagePersonDto apsStagePersonDto =  apsCaseReviewDao.getPersonStageInfo(stagePersonLinkDto.getIdStagePersonLink(), stagePersonLinkDto.getIdPerson());
            personDtoList.add(apsStagePersonDto);
        });
        return personDtoList;
    }


    /**
     * Method getApsCaseReview will populate  all the case related information(Inv stage summary, Intake details, service plan information)
     * @param apsCaseReviewRequest
     * @return List<ApsCaseReviewDto>
     */
    private  List<ApsCaseReviewDto> getApsCaseReview(ApsCaseReviewRequest apsCaseReviewRequest) {
        ApsCaseReviewDto apsCaseReviewDto = new ApsCaseReviewDto();
        apsCaseReviewDto.setCaseNumber(apsCaseReviewRequest.getIdCase());
        apsCaseReviewDto.setEventIdList(apsInvstDetailDao.getInvNarrativeEvents(apsCaseReviewRequest.getIdCase(),0L));
        //
        getCaseSummary(apsCaseReviewRequest.getIdCase(), apsCaseReviewDto);
        // First get the primary worker's information
        getWorkerDetails(apsCaseReviewRequest.getIdCase(), apsCaseReviewDto);

        // Now get the supervisor for the primary worker
        if (apsCaseReviewDto.getIdSupervisor() != 0L) {
            getSupervisorDetails(apsCaseReviewDto.getIdSupervisor(), apsCaseReviewDto);
        }
        getApsInvestigationDetails(apsCaseReviewRequest.getIdCase(), apsCaseReviewDto);
        getIncomingDeterminationFactorsByStage(apsCaseReviewDto.getPriorStageId(), apsCaseReviewDto);
        getServicePlanDetails(apsCaseReviewDto.getPriorStageId(), apsCaseReviewDto);
        return getIntakeStageSummary(apsCaseReviewRequest.getIdCase(), apsCaseReviewDto);
    }

    private void getCaseSummary(Long idCase, ApsCaseReviewDto apsCaseReviewDto) {
        CaseSummaryDto caseSummaryDto = caseSummaryDao.getCaseDetails(idCase);
        apsCaseReviewDto.setDtCaseClosed(caseSummaryDto.getDtCaseClosed());
    }


    /**
     * Method getServicePlanDetails will populate the service plan details based on the stageId
     * @param priorStageId
     * @param apsCaseReviewDto
     */
    private void getServicePlanDetails(long priorStageId, ApsCaseReviewDto apsCaseReviewDto) {
        long spStageId = apsSafetyAssessmentService.findShieldServicePlanStage(apsCaseReviewDto.getCaseNumber(), priorStageId);
        if (0 < spStageId){
            ApsServicePlanCompleteDto apsServicePlanCompleteDto = apsServicePlanService.getServicePlanCompleteDetails(spStageId);
            apsCaseReviewDto.setApsServicePlanCompleteDto(apsServicePlanCompleteDto);
        }
    }


    /**
     * Method getWorkerDetails will populate the worker details  based on the caseId
     * @param idCase - selected case
     * @param apsCaseReviewDto - review dto
     */
    private void getWorkerDetails(Long idCase, ApsCaseReviewDto apsCaseReviewDto) {
        Long idPerson;
        if(null != apsCaseReviewDto.getDtCaseClosed()){
            idPerson = apsCaseReviewDao.getPrimaryWorkerOrSupervisorByCaseId(idCase, ServiceConstants.HISTORICAL_PRIMARY);
        }else{
            idPerson  = apsCaseReviewLegacyDao.getPrimaryOrHistoricalPrimary(idCase, ServiceConstants.PRIMARY_WORKER);
        }

       if(!TypeConvUtil.isNullOrEmpty(idPerson)) {
           WorkerDetailDto workerDetailDto = disasterPlanDao.getWorkerInfoById(idPerson);
           if (!ObjectUtils.isEmpty(workerDetailDto)) {
               apsCaseReviewDto.setPrimaryFirstNm(workerDetailDto.getNmNameFirst());
               apsCaseReviewDto.setPrimaryMiddleNm(workerDetailDto.getNmNameMiddle());
               apsCaseReviewDto.setPrimaryLastNm(workerDetailDto.getNmNameLast());
               apsCaseReviewDto.setPrimarySuffix(workerDetailDto.getCdNameSuffix());
               apsCaseReviewDto.setIdPrimaryWorker(idPerson);
               apsCaseReviewDto.setIdSupervisor(workerDetailDto.getIdJobPersSupv());
           }
       }
    }

    /**
     * Method getWorkerDetails will populate the super visor details  based on the supervisor id
     * @param idSupervisor
     * @param apsCaseReviewDto
     */
    private void getSupervisorDetails(Long idSupervisor, ApsCaseReviewDto apsCaseReviewDto) {
        WorkerDetailDto workerDetailDto = disasterPlanDao.getWorkerInfoById(idSupervisor);
        if (!ObjectUtils.isEmpty(workerDetailDto)) {
            apsCaseReviewDto.setSupervisorfirstNm(workerDetailDto.getNmNameFirst());
            apsCaseReviewDto.setSupervisorMiddleNm(workerDetailDto.getNmNameMiddle());
            apsCaseReviewDto.setSupervisorLastNm(workerDetailDto.getNmNameLast());
            apsCaseReviewDto.setSupervisorSuffix(workerDetailDto.getCdNameSuffix());
        }
    }

    /**
     * Method getApsInvestigationDetails will populate  based on the case id
     * @param caseId
     * @param apsCaseReviewDto
     */
    private void getApsInvestigationDetails(Long caseId, ApsCaseReviewDto apsCaseReviewDto) {
        ApsCaseReviewDto pApsCaseReviewDto = apsInvstDetailDao.getApsInvestigationDetails(caseId, CodesConstant.CSTAGES_INV);
        if (!ObjectUtils.isEmpty(pApsCaseReviewDto)) {
            apsCaseReviewDto.setPriorStageId(pApsCaseReviewDto.getPriorStageId());
            apsCaseReviewDto.setDtApsInvstBegun(pApsCaseReviewDto.getDtApsInvstBegun());
            apsCaseReviewDto.setDtDtApsInvstCmplt(pApsCaseReviewDto.getDtDtApsInvstCmplt());
            apsCaseReviewDto.setSzCdApsInvstOvrallDisp(pApsCaseReviewDto.getSzCdApsInvstOvrallDisp());
            apsCaseReviewDto.setSzCdApsInvstFinalPrty(pApsCaseReviewDto.getSzCdApsInvstFinalPrty());
            apsCaseReviewDto.setBIndClient(pApsCaseReviewDto.getBIndClient());
            apsCaseReviewDto.setBIndECS(pApsCaseReviewDto.getBIndECS());
            apsCaseReviewDto.setBIndExtDoc(pApsCaseReviewDto.getBIndExtDoc());
            apsCaseReviewDto.setBIndFamViolence(pApsCaseReviewDto.getBIndFamViolence());
            apsCaseReviewDto.setBIndLegalAction(pApsCaseReviewDto.getBIndLegalAction());
            apsCaseReviewDto.setSzTxtClientOther(pApsCaseReviewDto.getSzTxtClientOther());
            apsCaseReviewDto.setSzTxtMethodComm(pApsCaseReviewDto.getSzTxtMethodComm());
            apsCaseReviewDto.setSzTxtTrnsNameRlt(pApsCaseReviewDto.getSzTxtTrnsNameRlt());
            apsCaseReviewDto.setSzTxtAltComm(pApsCaseReviewDto.getSzTxtAltComm());
            apsCaseReviewDto.setDtDtClientAdvised(pApsCaseReviewDto.getDtDtClientAdvised());
            apsCaseReviewDto.setCdClosureType(pApsCaseReviewDto.getCdClosureType());
            apsCaseReviewDto.setCdInterpreter(pApsCaseReviewDto.getCdInterpreter());
            apsCaseReviewDto.setInvStageId(pApsCaseReviewDto.getInvStageId());
        }
    }

    /**
     * Method getIncomingDeterminationFactorsByStage will populate all the determination factors as a List based on the stage id
     * @param stageId
     * @param apsCaseReviewDto
     */
    private void getIncomingDeterminationFactorsByStage(Long stageId, ApsCaseReviewDto apsCaseReviewDto) {
        List<IncmgDetermFactorsDto> incomingDeterminationFactorsDtoList = cpsIntakeNotificationDao.getincmgDetermFactorsById(stageId);
        List<String> incomingDetermFactors = incomingDeterminationFactorsDtoList.stream().map(IncmgDetermFactorsDto::getCdIncmgDeterm).collect(Collectors.toList());
        apsCaseReviewDto.setCdIncmgDeterm(incomingDetermFactors);
    }

    /**
     * Method getIntakeStageSummary will populate the intake stage summary based on the case id
     * @param idCase
     * @param pApsCaseReviewDto
     * @return List<ApsCaseReviewDto>
     */
    private  List<ApsCaseReviewDto> getIntakeStageSummary(Long idCase, ApsCaseReviewDto pApsCaseReviewDto) {
        List<ApsCaseReviewDto> apsCaseReviewDtoList = new ArrayList<>();
        List<IncomingStageDetailsDto> incomingStageDetailsDtoList = cpsIntakeReportDao.getCaseIncomingDetails(idCase, pApsCaseReviewDto.getInvStageId());
        incomingStageDetailsDtoList.forEach(incomingStageDetailsDto -> {
            ApsCaseReviewDto apsCaseReviewDto = buildApsCaseReviewObject(pApsCaseReviewDto);
            apsCaseReviewDto.setCaseNumber(idCase);
            apsCaseReviewDto.setIdStage(incomingStageDetailsDto.getIdStage());
            apsCaseReviewDto.setTmTmIncmgCall(FormattingHelper.formatTime(incomingStageDetailsDto.getDtIncomingCall()));
            apsCaseReviewDto.setSzCdStageCurrPriority(incomingStageDetailsDto.getCdStageCurrPriority() != null ? Long.parseLong(incomingStageDetailsDto.getCdStageCurrPriority()) : 0L);
            apsCaseReviewDto.setSzCdIncmgSpecHandling(incomingStageDetailsDto.getCdIncmgSpecHandling());
            apsCaseReviewDto.setSzCdIncmgAllegType(incomingStageDetailsDto.getCdIncmgAllegType());
            apsCaseReviewDto.setDtDtIncomingCall(incomingStageDetailsDto.getDtIncomingCall());
            apsCaseReviewDto.setBIndIncmgWorkerSafety(incomingStageDetailsDto.getIndIncmgWorkerSafety());
            apsCaseReviewDto.setBIndIncmgSensitive(incomingStageDetailsDto.getIndIncmgSensitive());
            apsCaseReviewDtoList.add(apsCaseReviewDto);
        });
        return apsCaseReviewDtoList;
    }

    /**
     * Method buildApsCaseReviewObject will populate the case review object
     * @param pApsCaseReviewDto
     * @return ApsCaseReviewDto
     */
    private ApsCaseReviewDto buildApsCaseReviewObject(ApsCaseReviewDto pApsCaseReviewDto) {
        ApsCaseReviewDto apsCaseReviewDto = new ApsCaseReviewDto();
        apsCaseReviewDto.setCaseName(pApsCaseReviewDto.getCaseName());
        apsCaseReviewDto.setCaseNumber(pApsCaseReviewDto.getCaseNumber());
        apsCaseReviewDto.setIdPrimaryWorker(pApsCaseReviewDto.getIdPrimaryWorker());
        apsCaseReviewDto.setPrimaryFirstNm(pApsCaseReviewDto.getPrimaryFirstNm());
        apsCaseReviewDto.setPrimaryMiddleNm(pApsCaseReviewDto.getPrimaryMiddleNm());
        apsCaseReviewDto.setPrimaryLastNm(pApsCaseReviewDto.getPrimaryLastNm());
        apsCaseReviewDto.setPrimarySuffix(pApsCaseReviewDto.getPrimarySuffix());
        apsCaseReviewDto.setIdSupervisor(pApsCaseReviewDto.getIdSupervisor());
        apsCaseReviewDto.setSupervisorfirstNm(pApsCaseReviewDto.getSupervisorfirstNm());
        apsCaseReviewDto.setSupervisorMiddleNm(pApsCaseReviewDto.getSupervisorMiddleNm());
        apsCaseReviewDto.setSupervisorLastNm(pApsCaseReviewDto.getSupervisorLastNm());
        apsCaseReviewDto.setSupervisorSuffix(pApsCaseReviewDto.getSupervisorSuffix());
        apsCaseReviewDto.setDtCaseClosed(pApsCaseReviewDto.getDtCaseClosed());
        apsCaseReviewDto.setPriorStageId(pApsCaseReviewDto.getPriorStageId());
        apsCaseReviewDto.setInvStageId(pApsCaseReviewDto.getInvStageId());
        apsCaseReviewDto.setDtDtApsInvstCmplt(pApsCaseReviewDto.getDtDtApsInvstCmplt());
        apsCaseReviewDto.setDtApsInvstBegun(pApsCaseReviewDto.getDtApsInvstBegun());
        apsCaseReviewDto.setSzCdApsInvstOvrallDisp(pApsCaseReviewDto.getSzCdApsInvstOvrallDisp());
        apsCaseReviewDto.setSzCdApsInvstFinalPrty(pApsCaseReviewDto.getSzCdApsInvstFinalPrty());
        apsCaseReviewDto.setBIndExtDoc(pApsCaseReviewDto.getBIndExtDoc());
        apsCaseReviewDto.setBIndFamViolence(pApsCaseReviewDto.getBIndFamViolence());
        apsCaseReviewDto.setBIndLegalAction(pApsCaseReviewDto.getBIndLegalAction());
        apsCaseReviewDto.setBIndClient(pApsCaseReviewDto.getBIndClient());
        apsCaseReviewDto.setBIndECS(pApsCaseReviewDto.getBIndECS());
        apsCaseReviewDto.setSzTxtClientOther(pApsCaseReviewDto.getSzTxtClientOther());
        apsCaseReviewDto.setSzTxtMethodComm(pApsCaseReviewDto.getSzTxtMethodComm());
        apsCaseReviewDto.setSzTxtTrnsNameRlt(pApsCaseReviewDto.getSzTxtTrnsNameRlt());
        apsCaseReviewDto.setSzTxtAltComm(pApsCaseReviewDto.getSzTxtAltComm());
        apsCaseReviewDto.setDtDtClientAdvised(pApsCaseReviewDto.getDtDtClientAdvised());
        apsCaseReviewDto.setCdClosureType(pApsCaseReviewDto.getCdClosureType());
        apsCaseReviewDto.setCdInterpreter(pApsCaseReviewDto.getCdInterpreter());
        apsCaseReviewDto.setEventIdList(pApsCaseReviewDto.getEventIdList());
        apsCaseReviewDto.setCdIncmgDeterm(pApsCaseReviewDto.getCdIncmgDeterm());
        apsCaseReviewDto.setApsServicePlanCompleteDto(pApsCaseReviewDto.getApsServicePlanCompleteDto());
        return apsCaseReviewDto;
    }


    /**
     * Method getStageCaseInfo will populate the stage case information
     * @param idCase
     * @return StageValueBeanDto
     */
    private StageValueBeanDto getStageCaseInfo(Long idCase) {
        return  stageDao.getStageCaseInfo(idCase);
    }


    /**
     * Method getAllegationDetails will populate the allegation details based on case
     * @param idCase
     * @return List<AllegationDto>
     */
    private List<AllegationDto> getAllegationDetails(Long idCase) {
        return  allegationDao.getAllegationDetailsBasedOnCase(idCase);
    }

    /**
     * Method getSaContacts will populate the safety assessment contacts based on case
     * @param idCase
     * @return List<ApsSafetyAssessmentContactDto>
     */
    private List<ApsSafetyAssessmentContactDto> getSaContacts(Long idCase) {
        List<ApsSafetyAssessmentContactDto> apsSafetyAssessmentContactDtoList = apsSafetyAssessmentDao.getSaContacts(idCase);
        return apsSafetyAssessmentService.sanitizeContacts(apsSafetyAssessmentContactDtoList);
    }


    /**
     * Method getSaContacts will populate the safety assessment contacts based on case
     * @param idCase
     * @return List<ApsSafetyAssessmentContactDto>
     */
    private List<ApsCaseReviewContactNamesDto> getPersonsContactedByCase(Long idCase) {
        List<ApsCaseReviewContactNamesDto> apsCaseReviewContactNamesDtoList = apsCaseReviewDao.getPersonsContactedByCase(idCase);
        apsCaseReviewContactNamesDtoList.forEach(apsCaseReviewContactNamesDto -> {
            if (null != apsCaseReviewContactNamesDto.getSuffix() && 0 < apsCaseReviewContactNamesDto.getSuffix().length()) {
                String fullName = apsCaseReviewContactNamesDto.getFullName() + ", " + apsCaseReviewContactNamesDto.getSuffix();
                apsCaseReviewContactNamesDto.setFullName(fullName);
            }
        });
        return apsCaseReviewContactNamesDtoList;
    }

    /**
     * Method getSafetyAssessmentsEventsByCase will populate the safety assessment events based on case
     * @param idCase
     * @return List<ApsSafetyAssessmentDto>
     */
    private List<ApsSafetyAssessmentDto> getSafetyAssessmentsEventsByCase(Long idCase) {
        return apsSafetyAssessmentDao.getSaEvents(idCase);
    }


    /**
     * Method getSafetyDbList will populate the safety assessment data
     * @param safetyEventList
     * @return List<ApsSafetyAssessmentDto>
     */
    private List<ApsSafetyAssessmentDto> getSafetyDbList(List<ApsSafetyAssessmentDto> safetyEventList) {
        List<ApsSafetyAssessmentDto> safetyDbList = new ArrayList<>();
        ApsCommonReq apsSafetyAssmtReportReq = new ApsCommonReq();
        safetyEventList.forEach(apsSafetyAssessmentDto -> {
            apsSafetyAssmtReportReq.setIdEvent(apsSafetyAssessmentDto.getEventId());
            apsSafetyAssmtReportReq.setIdStage(apsSafetyAssessmentDto.getStageId());
            apsSafetyAssessmentDto = apsSafetyAssessmentService.getApsSafetyAssessmentDetails(apsSafetyAssmtReportReq);
            safetyDbList.add(apsSafetyAssessmentDto);
        });
        return safetyDbList;
    }

    /**
     * Method getApsRoraInfo will populate the RISK OF RECIDIVISM ASSESSMENT information based on case
     * @param idCase
     * @return List<ApsRoraDto>
     */
    private List<ApsRoraDto> getApsRoraInfo(Long idCase) {
        List<ApsRoraDto> apsRoraDtos = new ArrayList<>();
        List<ApsRoraDto>  apsRoraDtoList = apsRoraDao.getRoraInformation(idCase);
        ApsCommonReq apsRoraReportReq = new ApsCommonReq();
        apsRoraDtoList.forEach(pApsRoraDto -> {
            apsRoraReportReq.setIdStage(pApsRoraDto.getStageId());
            apsRoraReportReq.setIdEvent(pApsRoraDto.getEventId());
            ApsRoraDto apsRoraDto = apsRoraService.getApsRoraFullDetails(apsRoraReportReq);
            apsRoraDto.setScoredRiskLevelCode(pApsRoraDto.getScoredRiskLevelCode());
            apsRoraDto.setDiscretionaryOverrideCode(pApsRoraDto.getDiscretionaryOverrideCode());
            apsRoraDto.setReasonForOverrideText(pApsRoraDto.getReasonForOverrideText());
            apsRoraDto.setFinalRiskLevelCode(pApsRoraDto.getFinalRiskLevelCode());
            apsRoraDto.setDtRoraComplete(pApsRoraDto.getDtRoraComplete());
            apsRoraDto.setEventId(pApsRoraDto.getEventId());
            apsRoraDto.setStageId(pApsRoraDto.getStageId());
            apsRoraDto.setApsRoraAssessmentId(pApsRoraDto.getApsRoraAssessmentId());
            apsRoraDto.setCaseId(idCase);
            apsRoraDtos.add(apsRoraDto);
        });
        return apsRoraDtos;
    }

    /**
     * Method getApsSnaDetails will populate the STRENGTHS AND NEEDS ASSESSMENTS information based on case
     * @param idCase
     * @return List<ApsStrengthsAndNeedsAssessmentDto>
     */
    private List<ApsStrengthsAndNeedsAssessmentDto> getApsSnaDetails(Long idCase) {
        return apsSnaService.getApsSnaDetailsforCaseReview(idCase);
    }

    /**
     * Method getSvcDeliveryClosureCheckList will populate the service delivery closure check list information based on case
     * @param idCase
     * @return List<StageValueBeanDto>
     */
    private List<StageValueBeanDto> getSvcDeliveryClosureCheckList(Long idCase) {
        return stageDao.getSvcDeliveryClosureCheckList(idCase);
    }

    /**
     * Method getSvcAuthEventInfo will populate the PURCHASED CLIENT SERVICES information based on case
     * @param idCase
     * @return List<ServiceAuthDto>
     */
    private List<ServiceAuthDto> getSvcAuthEventInfo(Long idCase) {
        return serviceAuthorizationDao.getSvcAuthEventInfo(idCase);
    }


}
