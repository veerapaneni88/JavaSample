package us.tx.state.dfps.service.apscasereview.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ApsInvstDetail;
import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.pcsphistoryform.dto.CareDetailDto;
import us.tx.state.dfps.pcsphistoryform.dto.CareNarrativeInfoDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.FormattingHelper;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewContactDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewLegacyServiceDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewServiceAuthDto;
import us.tx.state.dfps.service.apscasereview.ApsStagePersonDto;
import us.tx.state.dfps.service.apscasereview.dao.ApsCaseReviewDao;
import us.tx.state.dfps.service.apscasereview.dao.ApsCaseReviewLegacyDao;
import us.tx.state.dfps.service.apscasereview.service.ApsCaseReviewLegacyService;
import us.tx.state.dfps.service.casemanagement.dao.CpsIntakeNotificationDao;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ApsCaseReviewRequest;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.NameDto;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.APSCaseReviewLegacyPrefillData;
import us.tx.state.dfps.service.intake.dto.IncmgDetermFactorsDto;
import us.tx.state.dfps.service.investigation.dao.ApsInvstDetailDao;
import us.tx.state.dfps.service.person.dao.AllegationDao;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.ServiceAuthDto;
import us.tx.state.dfps.service.populateform.dao.PcspHistoryFormDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * APS Case Review (Legacy)
 *
 * @author CHITLA
 * Feb, 2022© Texas Department of Family and Protective Services
 */

@Service
public class ApsCaseReviewLegacyServiceImpl implements ApsCaseReviewLegacyService {

    @Autowired
    private CpsIntakeReportDao cpsIntakeReportDao;

    @Autowired
    private ApsInvstDetailDao apsInvstDetailDao;

    @Autowired
    private CpsIntakeNotificationDao cpsIntakeNotificationDao;

    @Autowired
    private APSCaseReviewLegacyPrefillData apsCaseReviewLegacyPrefillData;

    @Autowired
    private StageDao stageDao;

    @Autowired
    private AllegationDao allegationDao;

    @Autowired
    private StagePersonLinkDao stagePersonLinkDao;

    @Autowired
    private ApsCaseReviewDao apsCaseReviewDao;

    @Autowired
    private ApsCaseReviewLegacyDao apsCaseReviewLegacyDao;

    @Autowired
    private PcspHistoryFormDao pcspHistoryFormDao;

    @Autowired
    private ServiceAuthorizationDao serviceAuthorizationDao;

    @Autowired
    private EmployeeDao employeeDao;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getApsCaseReviewLegacyVersionInformation(ApsCaseReviewRequest apsCaseReviewRequest) {

        Long caseId = apsCaseReviewRequest.getIdCase();
        ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto = new ApsCaseReviewLegacyServiceDto();

        StageValueBeanDto stageValueBeanDto = getStageCaseInfo(caseId);
        apsCaseReviewServiceDto.setStageValueBeanDto(stageValueBeanDto);

        Date dtSampleFrom = stageValueBeanDto.getDtCaseOpened();
        Date dtSampleTo = new Date();

        ApsCaseReviewDto apsCaseReviewDto = getApsCaseReview(apsCaseReviewRequest, stageValueBeanDto);
        apsCaseReviewServiceDto.setApsCaseReviewDto(apsCaseReviewDto);

        List<ApsInvstDetail> apsInvstDetailList = getInvestigationDetails(apsCaseReviewDto);
        apsCaseReviewServiceDto.setApsInvstDetailList(apsInvstDetailList);

        apsCaseReviewServiceDto.setContactsList(apsCaseReviewLegacyDao.getContactsByCase(caseId));

        List<StageValueBeanDto> svcDeliveryClosureCheckList = getSvcDeliveryClosureCheckList(caseId);
        apsCaseReviewServiceDto.setSvcDeliveryClosureCheckList(svcDeliveryClosureCheckList);

        List<AllegationDto> allegationDtoList = getAllegationDetails(caseId);
        apsCaseReviewServiceDto.setAllegationDtoList(allegationDtoList);

         apsCaseReviewServiceDto.setPrincipalDtoList(getPersonsInfo(caseId, ServiceConstants.PRINCIPAL));

        List<ApsStagePersonDto> colPersonDtoList = getPersonsInfo(caseId, ServiceConstants.COLLATERAL);
        apsCaseReviewServiceDto.setCollateralDtoList(getCollateralInfo(colPersonDtoList, apsCaseReviewServiceDto));

        apsCaseReviewServiceDto.setCareEvents(apsCaseReviewLegacyDao.getCareEvents(caseId));
        apsCaseReviewServiceDto.setOutcomeMatrixEvents(apsCaseReviewLegacyDao.getOutcomeMatrixEvents(caseId));

        apsCaseReviewServiceDto.setCareDetailDto(getOutcomeMatrixSummary(caseId));
        apsCaseReviewServiceDto.setCareNarrativeInfoDto(getCareNarrativeInfo(caseId));

        apsCaseReviewServiceDto.setSvcAuthEventInfoList(getSvcAuthEventInfo(caseId));
        apsCaseReviewServiceDto.setDonatedCommunityServicesList(getDonatedCommunityServicesInfo(caseId));

        getContactInformation(apsCaseReviewServiceDto, caseId, dtSampleFrom, dtSampleTo);

        return apsCaseReviewLegacyPrefillData.returnPrefillData(apsCaseReviewServiceDto);
    }


    private void getContactInformation(ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto, Long caseId, Date dtSampleFrom, Date dtSampleTo) {
        List<ApsCaseReviewContactDto> caseContacts = apsCaseReviewLegacyDao.getContactInformation(caseId, dtSampleFrom, dtSampleTo);
        List<ApsCaseReviewContactDto> contactInfoList = new ArrayList<>();
        if (!TypeConvUtil.isNullOrEmpty(caseContacts) && caseContacts.size() > 0) {
            for (ApsCaseReviewContactDto contactDto : caseContacts) {
                populatePersonContacted(contactDto, caseId);
                contactInfoList.add(contactDto);
            }
        }
        apsCaseReviewServiceDto.setContactInfoList(contactInfoList);
    }

    private void populatePersonContacted(ApsCaseReviewContactDto contactDto, Long caseId) {
        List<NameDto> personList = apsCaseReviewLegacyDao.getPersonContacted(caseId, contactDto.getIdEvent());
        contactDto.setPersonsContacted(personList);
    }

    private ApsCaseReviewDto getApsCaseReview(ApsCaseReviewRequest apsCaseReviewRequest, StageValueBeanDto stageValueBeanDto) {
        ApsCaseReviewDto apsCaseReviewDto = new ApsCaseReviewDto();
        apsCaseReviewDto.setCaseNumber(apsCaseReviewRequest.getIdCase());
        apsCaseReviewDto.setEventIdList(apsInvstDetailDao.getInvNarrativeEvents(apsCaseReviewRequest.getIdCase(), 0L));

        // Get the primary worker's information
        getWorkerDetails(apsCaseReviewRequest.getIdCase(), apsCaseReviewDto, stageValueBeanDto);

        // Get supervisor for the primary worker
        if (apsCaseReviewDto.getIdSupervisor() != 0L) {
            getSupervisorDetails(apsCaseReviewDto.getIdSupervisor(), apsCaseReviewDto);
        }
        getApsInvestigationDetails(apsCaseReviewRequest.getIdCase(), apsCaseReviewDto);
        getIncomingDetermFactorsById(apsCaseReviewDto.getPriorStageId(), apsCaseReviewDto);
        return getIntakeStageSummary(apsCaseReviewRequest.getIdCase(), apsCaseReviewDto);
    }

    private void getWorkerDetails(Long idCase, ApsCaseReviewDto apsCaseReviewDto, StageValueBeanDto stageValueBeanDto) {
        Long idPerson;
        if (null != stageValueBeanDto.getDtCaseClosed()) {
            idPerson = apsCaseReviewLegacyDao.getPrimaryOrHistoricalPrimary(idCase, ServiceConstants.HISTORICAL_PRIMARY);
        } else {
            idPerson = apsCaseReviewLegacyDao.getPrimaryOrHistoricalPrimary(idCase, ServiceConstants.PRIMARY_WORKER);
        }
        // Call CSEC01D
        if (idPerson != 0L) {
            EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPerson);
            if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
                apsCaseReviewDto.setPrimaryFirstNm(employeePersPhNameDto.getNmNameFirst());
                apsCaseReviewDto.setPrimaryMiddleNm(employeePersPhNameDto.getNmNameMiddle());
                apsCaseReviewDto.setPrimaryLastNm(employeePersPhNameDto.getNmNameLast());
                apsCaseReviewDto.setPrimarySuffix(employeePersPhNameDto.getCdNameSuffix());
                apsCaseReviewDto.setIdPrimaryWorker(idPerson);
                apsCaseReviewDto.setIdSupervisor(employeePersPhNameDto.getIdJobPersSupv());
            }
        }
    }

    private void getSupervisorDetails(Long idSupervisor, ApsCaseReviewDto apsCaseReviewDto) {
        // Call CSEC01D
        EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idSupervisor);
        if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
            apsCaseReviewDto.setSupervisorfirstNm(employeePersPhNameDto.getNmNameFirst());
            apsCaseReviewDto.setSupervisorMiddleNm(employeePersPhNameDto.getNmNameMiddle());
            apsCaseReviewDto.setSupervisorLastNm(employeePersPhNameDto.getNmNameLast());
            apsCaseReviewDto.setSupervisorSuffix(employeePersPhNameDto.getCdNameSuffix());
        }
    }

    private void getApsInvestigationDetails(Long caseId, ApsCaseReviewDto apsCaseReviewDto) {
        ApsCaseReviewDto apsCaseReviewDto1 = apsInvstDetailDao.getApsInvestigationDetails(caseId, CodesConstant.CSTAGES_INV);
        if (!ObjectUtils.isEmpty(apsCaseReviewDto1)) {
            apsCaseReviewDto.setPriorStageId(apsCaseReviewDto1.getPriorStageId());
            apsCaseReviewDto.setDtApsInvstBegun(apsCaseReviewDto1.getDtApsInvstBegun());
            apsCaseReviewDto.setDtDtApsInvstCmplt(apsCaseReviewDto1.getDtDtApsInvstCmplt());
            apsCaseReviewDto.setDtApsInvstCltAssmt(apsCaseReviewDto1.getDtApsInvstCltAssmt());
            apsCaseReviewDto.setSzCdApsInvstOvrallDisp(apsCaseReviewDto1.getSzCdApsInvstOvrallDisp());
            apsCaseReviewDto.setSzCdApsInvstFinalPrty(apsCaseReviewDto1.getSzCdApsInvstFinalPrty());
            apsCaseReviewDto.setBIndClient(apsCaseReviewDto1.getBIndClient());
            apsCaseReviewDto.setBIndECS(apsCaseReviewDto1.getBIndECS());
            apsCaseReviewDto.setBIndExtDoc(apsCaseReviewDto1.getBIndExtDoc());
            apsCaseReviewDto.setBIndFamViolence(apsCaseReviewDto1.getBIndFamViolence());
            apsCaseReviewDto.setBIndLegalAction(apsCaseReviewDto1.getBIndLegalAction());
            apsCaseReviewDto.setSzTxtClientOther(apsCaseReviewDto1.getSzTxtClientOther());
            apsCaseReviewDto.setSzTxtMethodComm(apsCaseReviewDto1.getSzTxtMethodComm());
            apsCaseReviewDto.setSzTxtTrnsNameRlt(apsCaseReviewDto1.getSzTxtTrnsNameRlt());
            apsCaseReviewDto.setSzTxtAltComm(apsCaseReviewDto1.getSzTxtAltComm());
            apsCaseReviewDto.setDtDtClientAdvised(apsCaseReviewDto1.getDtDtClientAdvised());
            apsCaseReviewDto.setCdClosureType(apsCaseReviewDto1.getCdClosureType());
            apsCaseReviewDto.setCdInterpreter(apsCaseReviewDto1.getCdInterpreter());
            apsCaseReviewDto.setInvStageId(apsCaseReviewDto1.getInvStageId());
        }
    }

    private ApsCaseReviewDto getIntakeStageSummary(Long idCase, ApsCaseReviewDto apsCaseReviewDto) {
        IncomingStageDetailsDto incomingStageDetailsDto = cpsIntakeReportDao.getStageIncomingDetails(apsCaseReviewDto.getPriorStageId());
        apsCaseReviewDto.setCaseNumber(idCase);
        apsCaseReviewDto.setIdStage(incomingStageDetailsDto.getIdStage());
        apsCaseReviewDto.setTmTmIncmgCall(FormattingHelper.formatTime(incomingStageDetailsDto.getDtIncomingCall()));
        apsCaseReviewDto.setSzCdStageCurrPriority(incomingStageDetailsDto.getCdStageCurrPriority() != null ? Long.parseLong(incomingStageDetailsDto.getCdStageCurrPriority()) : 0L);
        apsCaseReviewDto.setSzCdIncmgSpecHandling(incomingStageDetailsDto.getCdIncmgSpecHandling());
        apsCaseReviewDto.setSzCdIncmgAllegType(incomingStageDetailsDto.getCdIncmgAllegType());
        apsCaseReviewDto.setDtDtIncomingCall(incomingStageDetailsDto.getDtIncomingCall());
        apsCaseReviewDto.setBIndIncmgWorkerSafety(incomingStageDetailsDto.getIndIncmgWorkerSafety());
        apsCaseReviewDto.setBIndIncmgSensitive(incomingStageDetailsDto.getIndIncmgSensitive());
        return apsCaseReviewDto;
    }

    private void getIncomingDetermFactorsById(Long stageId, ApsCaseReviewDto apsCaseReviewDto) {
        List<IncmgDetermFactorsDto> incmgDetermFactorsDtoList = cpsIntakeNotificationDao.getincmgDetermFactorsById(stageId);
        List<String> incomingDetermFactors = incmgDetermFactorsDtoList.stream().map(IncmgDetermFactorsDto::getCdIncmgDeterm).collect(Collectors.toList());
        apsCaseReviewDto.setCdIncmgDeterm(incomingDetermFactors);
    }

    private StageValueBeanDto getStageCaseInfo(Long idCase) {
        return stageDao.getStageCaseInfo(idCase);
    }

    private List<ApsInvstDetail> getInvestigationDetails(ApsCaseReviewDto apsCaseReviewDto) {
        List<ApsInvstDetail> apsInvstDetailList = null;
        Long stageId = apsCaseReviewDto.getInvStageId();
        apsInvstDetailList = apsInvstDetailDao.getApsInvstDetailbyParentId(stageId);
        return apsInvstDetailList;
    }

    private List<StageValueBeanDto> getSvcDeliveryClosureCheckList(Long idCase) {
        return stageDao.getSvcDeliveryClosureCheckList(idCase);
    }

    private List<AllegationDto> getAllegationDetails(Long idCase) {
        return allegationDao.getAllegationDetailsBasedOnCase(idCase);
    }

    private List<ApsStagePersonDto> getPersonsInfo(Long idCase, String cdStagePersonType) {
        List<ApsStagePersonDto> personDtoList = new ArrayList<>();
        List<StagePersonLinkDto> stagePersonLinkDtoList = stagePersonLinkDao.getStgPersonLinkDtlsBasedOnCase(idCase, cdStagePersonType);
        stagePersonLinkDtoList.forEach(stagePersonLinkDto -> {
            ApsStagePersonDto apsStagePersonDto = apsCaseReviewDao.getPersonStageInfo(stagePersonLinkDto.getIdStagePersonLink(), stagePersonLinkDto.getIdPerson());
            personDtoList.add(apsStagePersonDto);
        });
        return personDtoList;
    }
    
    private List<ApsStagePersonDto> getCollateralInfo(List<ApsStagePersonDto> personDtoList, ApsCaseReviewLegacyServiceDto apsCaseReviewServiceDto) {
        List<ApsStagePersonDto> collateralList = new ArrayList<>();
        List<ApsStagePersonDto> reporterList = new ArrayList<>();
        personDtoList.forEach(apsStagePersonDto -> {
            if (apsStagePersonDto.getIndStagePersReporter().equals(ServiceConstants.Y)) {
                reporterList.add(apsStagePersonDto);
            } else {
                collateralList.add(apsStagePersonDto);
            }
        });
        apsCaseReviewServiceDto.setReporterDtoList(reporterList);
        return collateralList;
    }

    private List<CareDetailDto> getOutcomeMatrixSummary(Long idCase) {
        return pcspHistoryFormDao.getCareDetailInfo(idCase);
    }

    private List<CareNarrativeInfoDto> getCareNarrativeInfo(Long idCase) {
        return pcspHistoryFormDao.getCareNarrativeInfo(idCase);
    }

    private List<ServiceAuthDto> getSvcAuthEventInfo(Long idCase) {
        return serviceAuthorizationDao.getSvcAuthEventInfo(idCase);
    }

    private List<ApsCaseReviewServiceAuthDto> getDonatedCommunityServicesInfo(Long idCase) {
        return apsCaseReviewLegacyDao.getDonatedCommunityServicesInfo(idCase);
    }

}
