package us.tx.state.dfps.service.apssafetyassmt.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.apssafetyassmt.dao.ApsSafetyAssessmentDao;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentCaretakerDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentContactDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentNarrativeDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentResponseDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentServiceDto;
import us.tx.state.dfps.service.apssafetyassmt.service.ApsSafetyAssessmentService;
import us.tx.state.dfps.service.apsserviceplan.dao.ApsServicePlanDao;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanActionDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanMonitoringDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanProblemDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanSourceDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsSafetyAssessmentFormPrefillData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * service-business- IMPACT APS MODERNIZATION Class Description:
 * ApsSafetyAssessmentServiceImpl.
 * Jan 04, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Service
public class ApsSafetyAssessmentServiceImpl implements ApsSafetyAssessmentService {

    @Autowired
    DisasterPlanDao disasterPlanDao;

    @Autowired
    ApsSafetyAssessmentFormPrefillData apsSafetyAssessmentFormPrefillData;

    @Autowired
    ApsSafetyAssessmentDao apsSafetyAssessmentDao;

    @Autowired
    ApsServicePlanDao apsServicePlanDao;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getApsSafetyAssessmentFormInformation(ApsCommonReq apsSafetyAssmtReportReq) {

        ApsSafetyAssessmentServiceDto apsSafetyAssessmentServiceDto = new ApsSafetyAssessmentServiceDto();
        ApsSafetyAssessmentDto  apsSafetyAssessmentDto;

        // getGenericCaseInfo (DAm Name : CallCSEC02D) Method
        GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(apsSafetyAssmtReportReq.getIdStage());
        apsSafetyAssessmentServiceDto.setGenericCaseInfoDto(genericCaseInfoDto);

        apsSafetyAssessmentDto = getApsSafetyAssessmentDetails(apsSafetyAssmtReportReq);
        apsSafetyAssessmentServiceDto.setApsSafetyAssessmentDto(apsSafetyAssessmentDto);

        return apsSafetyAssessmentFormPrefillData.returnPrefillData(apsSafetyAssessmentServiceDto);

    }

    @Override
    public ApsSafetyAssessmentDto getApsSafetyAssessmentDetails(ApsCommonReq apsSafetyAssmtReportReq) {
        ApsSafetyAssessmentDto apsSafetyAssessmentDto = populateSafetyAssessmentDto(apsSafetyAssmtReportReq);
        if (!Objects.isNull(apsSafetyAssessmentDto)) {
            long spStageId = findShieldServicePlanStage(apsSafetyAssessmentDto.getCaseId(), apsSafetyAssmtReportReq.getIdStage());
            if(0 < spStageId) {
                ApsServicePlanDto apsServicePlanDto = getServicePlan(spStageId, apsSafetyAssessmentDto.getId(), CodesConstant.CSPSRCTP_SA);
                apsSafetyAssessmentDto.setServicePlan(apsServicePlanDto);
            }
        }
        return apsSafetyAssessmentDto;
    }

    private ApsSafetyAssessmentDto populateSafetyAssessmentDto(ApsCommonReq apsSafetyAssmtReportReq) {
        ApsSafetyAssessmentDto apsSafetyAssessmentDto = apsSafetyAssessmentDao.getApsSafetyAssessmentData(apsSafetyAssmtReportReq.getIdEvent());

        if (!Objects.isNull(apsSafetyAssessmentDto)) {
            List<ApsSafetyAssessmentCaretakerDto> selectedCaretakersList = apsSafetyAssessmentDao.getSelectedCaretakersList(apsSafetyAssmtReportReq.getIdEvent());
            List<ApsSafetyAssessmentCaretakerDto> availableCaretakersList = apsSafetyAssessmentDao.getAvailableCaretakersList(apsSafetyAssmtReportReq.getIdStage());
            ApsSafetyAssessmentNarrativeDto narrative = apsSafetyAssessmentDao.getNarrative(apsSafetyAssmtReportReq.getIdEvent());
            List<ApsSafetyAssessmentResponseDto>  responses = apsSafetyAssessmentDao.getResponses(apsSafetyAssessmentDto.getId());
            List<ApsSafetyAssessmentContactDto> contactsList = apsSafetyAssessmentDao.getContacts(apsSafetyAssmtReportReq.getIdEvent());

            apsSafetyAssessmentDto.setPrevSelectedCaretakerList(selectedCaretakersList);
            apsSafetyAssessmentDto.setAvailableCaretakerList(availableCaretakersList);
            apsSafetyAssessmentDto.setNarrative(narrative);
            apsSafetyAssessmentDto.setSafetyResponses(responses);
            apsSafetyAssessmentDto.setContactList(sanitizeContacts(contactsList));
        }
        return apsSafetyAssessmentDto;
    }


    @Override
    public List<ApsSafetyAssessmentContactDto> sanitizeContacts(List<ApsSafetyAssessmentContactDto> contactsList) {
        List<ApsSafetyAssessmentContactDto> modifiedContactList = new ArrayList<>();
        SimpleDateFormat tmFormatter = new SimpleDateFormat("hh:mm a");
        contactsList.forEach(contact -> {
            contact.setCodeContactType(contact.getContactType());
            if (CodesConstant.CCNTCTYP_C24H.equals(contact.getContactType())) {
                contact.setContactType(ServiceConstants.CI_CONTACT_TYPE);
            } else if (CodesConstant.CCNTCTYP_CFTF.equals(contact.getContactType())
                    || CodesConstant.CCNTCTYP_CSAF.equals(contact.getContactType())
                    || CodesConstant.CCNTCTYP_CSVC.equals(contact.getContactType())) {
                contact.setContactType(ServiceConstants.F2F_CONTACT_TYPE);
            }
            contact.setTimeContactOccurred(tmFormatter.format(contact.getTsContactOccurred()));
            modifiedContactList.add(contact);
        });
        return modifiedContactList;
    }

    private ApsServicePlanDto getServicePlanForSA(Long stageId, Long sourceId) {
        ApsServicePlanDto apsServicePlanDto = apsServicePlanDao.getServicePlanByStage(stageId);
        long servicePlanId = apsServicePlanDao.getServicePlanByStage(stageId).getId();
        List<ApsServicePlanMonitoringDto> apsServicePlanMonitoringDtoList = apsServicePlanDao.getMonitoringPlans(servicePlanId, sourceId);
        List<ApsServicePlanMonitoringDto> apsServicePlanActiveMonitoringDtoList = apsServicePlanDao.getActiveMonitoringPlans(servicePlanId);
        List<ApsServicePlanSourceDto>  apsServicePlanSourceDtoList = apsServicePlanDao.getServiceSources(servicePlanId, sourceId);

        apsServicePlanSourceDtoList.forEach(source -> {
            List<ApsServicePlanProblemDto> problemDtoList = apsServicePlanDao.getServiceProblems(source.getId());
            problemDtoList.forEach(problem -> {
                List<ApsServicePlanActionDto> servicePlanActionDtoList = apsServicePlanDao.getServiceActions(problem.getId());
                problem.setActions(servicePlanActionDtoList);
            });
            source.setServiceProblems(problemDtoList);
        });

        apsServicePlanDto.setActiveMonitoringPlan(!apsServicePlanActiveMonitoringDtoList.isEmpty() ? apsServicePlanActiveMonitoringDtoList.get(0) : null);
        apsServicePlanDto.setAssmtMonitoringPlan(!apsServicePlanMonitoringDtoList.isEmpty() ? apsServicePlanMonitoringDtoList.get(0) : null);
        apsServicePlanDto.setSavedServiceSources(apsServicePlanSourceDtoList);

        return apsServicePlanDto;
    }

    private void updateActionForMultiProblems(List<ApsServicePlanSourceDto> sourcesDBList) {
        if (sourcesDBList.isEmpty()) {
            return;
        }
        Set<Long> actionIdSet = new HashSet<>();
        for (ApsServicePlanSourceDto sourceDB: sourcesDBList) {
            for (ApsServicePlanProblemDto problemDB: sourceDB.getServiceProblems()) {
                for (ApsServicePlanActionDto actionDB: problemDB.getActions()) {
                    actionDB.setDisabled(!actionIdSet.add(actionDB.getId()));
                }
            }
        }
    }

    private ApsServicePlanDto getServicePlan(Long stageId, Long sourceId, String sourceType) {
        ApsServicePlanDto apsServicePlanDto = null;
        if (CodesConstant.CSPSRCTP_SA.equals(sourceType)) {
            //SHIELD SIR#1020992 - Begin Update
            apsServicePlanDto = getServicePlanForSA(stageId, sourceId);
            updateActionForMultiProblems(apsServicePlanDto.getSavedServiceSources());
            //SHIELD SIR#1020992 - End Update
        }
        return apsServicePlanDto;
    }

    @Override
    public Long findShieldServicePlanStage(Long caseId, Long lookupStageId) {
        ApsServicePlanDto apsServicePlanDto;
        Long selectedStageId = lookupStageId;
        do {
            //Check if Selected Stage Id has Valid Shield Service Plan Event
            apsServicePlanDto = apsServicePlanDao.getServicePlanByStage(selectedStageId);
            if (apsServicePlanDto != null && apsServicePlanDto.getServicePlanEventId() > 0) {
                return selectedStageId;
            }
            //If SHIELD Service Plan event not found, use Stage Link table to get Progressed Stage Id
            selectedStageId = apsServicePlanDao.getProgressedStageId(caseId, selectedStageId);
        } while (selectedStageId > 0);
        return selectedStageId;
    }

}
