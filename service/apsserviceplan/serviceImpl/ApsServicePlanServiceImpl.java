package us.tx.state.dfps.service.apsserviceplan.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.apsserviceplan.dao.ApsServicePlanDao;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanCompleteDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanProblemDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanSourceDto;
import us.tx.state.dfps.service.apsserviceplan.service.ApsServicePlanService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsServicePlanServicePrefillData;
import us.tx.state.dfps.service.workload.dto.StageDto;

import java.util.*;
import java.util.stream.Collectors;

import static us.tx.state.dfps.service.common.CodesConstant.CSPSRCTP_ALLEG;
import static us.tx.state.dfps.service.common.CodesConstant.CSPSRCTP_SA;
import static us.tx.state.dfps.service.common.CodesConstant.CSPSRCTP_SNA;

@Repository
public class ApsServicePlanServiceImpl implements ApsServicePlanService {

    @Autowired
    DisasterPlanDao disasterPlanDao;

    @Autowired
    private StageDao stageDao;

    @Autowired
    private CodesDao codesDao;

    @Autowired
    ApsServicePlanDao apsServicePlanDao;

    @Autowired
    ApsServicePlanServicePrefillData apsServicePlanServicePrefillData;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getApsServicePlanReport(CommonHelperReq commonHelperReq) {

        ApsServicePlanCompleteDto spCompleteDto = getServicePlanCompleteDetails(commonHelperReq.getIdStage());

        return apsServicePlanServicePrefillData.returnPrefillData(spCompleteDto);
    }

    @Override
    public ApsServicePlanCompleteDto getServicePlanCompleteDetails(Long idStage) {
        ApsServicePlanCompleteDto apsServicePlanCompleteDto = new ApsServicePlanCompleteDto();

        // Setting Case details
        GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(idStage);
        apsServicePlanCompleteDto.setGenericCaseInfoDto(genericCaseInfoDto);

        // Setting stageDto Details
        apsServicePlanCompleteDto.setStageDto(stageDao.getStageById(idStage));

        apsServicePlanCompleteDto.setPreSingleStage(checkPreSingleStageByStageId(apsServicePlanCompleteDto.getStageDto()));

        // Setting ServicePlan details
        ApsServicePlanDto servicePlanDto = apsServicePlanDao.getServicePlanByStage(idStage);
        apsServicePlanCompleteDto.setApsServicePlanDto(servicePlanDto);

        // Setting Service DTOs
        Map<String, List<ApsServicePlanSourceDto>> sourceDtoMap = new LinkedHashMap<>();
        sourceDtoMap.put(CSPSRCTP_SA, populateServiceSourceDetails(CSPSRCTP_SA, servicePlanDto.getId()));
        sourceDtoMap.put(CSPSRCTP_ALLEG, populateServiceSourceDetails(CSPSRCTP_ALLEG, servicePlanDto.getId()));
        sourceDtoMap.put(CSPSRCTP_SNA, populateServiceSourceDetails(CSPSRCTP_SNA, servicePlanDto.getId()));

        apsServicePlanCompleteDto.setSourceDtoMap(sourceDtoMap);

        // Setting Contract Details
        apsServicePlanCompleteDto.setMonitoringPlanList(apsServicePlanDao.getMonitoringPlansAll(servicePlanDto.getId()));

        return apsServicePlanCompleteDto;
    }

    /**
     * This method will populate the Service details based on the Source Type and Id.
     *
     * @param sourceType
     * @param sourceId
     * @return
     */
    private List<ApsServicePlanSourceDto> populateServiceSourceDetails(String sourceType, Long sourceId) {
        List<ApsServicePlanSourceDto> servicePlanSourceDtos = new ArrayList<>();
        switch (sourceType) {
            case CSPSRCTP_SA:
                servicePlanSourceDtos = apsServicePlanDao.getServicesForSA(sourceId).stream().map(saService -> {
                    List<ApsServicePlanProblemDto> problemDtos = populateServiceProblemDetails(saService.getId());
                    saService.setServiceProblems(problemDtos);
                    return saService;
                }).collect(Collectors.toList());
                Collections.sort(servicePlanSourceDtos, new ApsServicePlanSourceComparator());
                break;
            case CSPSRCTP_SNA:
                servicePlanSourceDtos = apsServicePlanDao.getServicesForSNA(sourceId).stream().map(saService -> {
                    List<ApsServicePlanProblemDto> problemDtos = populateServiceProblemDetails(saService.getId());
                    saService.setServiceProblems(problemDtos);
                    return saService;
                }).collect(Collectors.toList());
                Collections.sort(servicePlanSourceDtos, new ApsServicePlanSourceComparator());
                break;

            case CSPSRCTP_ALLEG:
                servicePlanSourceDtos = apsServicePlanDao.getServicesForALLEG(sourceId).stream().map(saService -> {
                    List<ApsServicePlanProblemDto> problemDtos = populateServiceProblemDetails(saService.getId());
                    saService.setServiceProblems(problemDtos);
                    return saService;
                }).collect(Collectors.toList());
                Collections.sort(servicePlanSourceDtos, new ApsServicePlanSourceComparator());
                break;
            default:
                break;
        }
        return servicePlanSourceDtos;
    }

    /**
     * This method will populate the Problem details of a service by its Id.
     *
     * @param problemId
     * @return
     */
    private List<ApsServicePlanProblemDto> populateServiceProblemDetails(Long problemId) {
        return apsServicePlanDao.getServiceProblems(problemId).stream().map(saProblem -> {
            saProblem.setActions(apsServicePlanDao.getServiceActions(saProblem.getId()));
            return saProblem;
        }).collect(Collectors.toList());
    }

    /**
     * This operation will take stage id as a parameter and check if the stage has the start date before the APS Release Date.
     * If the stage start date is prior to the APS Release Date, this operation will return true, else false.
     *
     * @param stageDto
     * @return
     */
    private boolean checkPreSingleStageByStageId(StageDto stageDto) {
        boolean preSingleStage = false;
        Date defualtDt = codesDao.getAppRelDate(ServiceConstants.CRELDATE_NOV_2020_APS);
        if (!ObjectUtils.isEmpty(stageDto) && !ObjectUtils.isEmpty(stageDto.getDtStageStart())) {
            preSingleStage = DateUtils.isBefore(stageDto.getDtStageStart(), defualtDt);
        }
        return preSingleStage;
    }

    /**
     * Method helps to call aps service plan DAO
     *
     * @param idStage - selected stage id
     * @return - return if record exists 1 else 0
     */
    @Override
    public Long getPcsActionCategoryCount(Long idStage) {
        return apsServicePlanDao.getPcsActionCategoryCountBtStageId(idStage);
    }
}
