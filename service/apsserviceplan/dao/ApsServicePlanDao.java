package us.tx.state.dfps.service.apsserviceplan.dao;

import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanActionDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanMonitoringDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanProblemDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanSourceDto;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * service-business- IMPACT APS MODERNIZATION Class
 * Description:ApsServicePlanDao Jan 04, 2022- 1:52:46 PM © 2022 Texas Department
 * of Family and Protective Services
 */
@Service
public interface ApsServicePlanDao {

    /**
     * Method Name: getServicePlanByStage Method Description: getServicePlanByStage.
     *
     * @param idStage
     * @return ApsServicePlanDto
     */
    ApsServicePlanDto getServicePlanByStage(Long idStage);

    /**
     * Method Name: getProgressedStageId Method Description: getProgressedStageId.
     *
     * @param idCase
     * @param idStage
     * @return Long
     */
    Long getProgressedStageId(Long idCase, Long idStage);

    /**
     * Method Name: getServiceSources Method Description: getServiceSources.
     *
     * @param servicePlanId
     * @param sourceId
     * @return List<ApsServicePlanSourceDto>
     */
    List<ApsServicePlanSourceDto>  getServiceSources(Long servicePlanId, Long sourceId);

    /**
     * Method Name: getMonitoringPlans Method Description: getMonitoringPlans.
     *
     * @param servicePlanId
     * @param sourceId
     * @return List<ApsServicePlanMonitoringDto>
     */
    List<ApsServicePlanMonitoringDto> getMonitoringPlans(Long servicePlanId, Long sourceId);

    /**
     * Method Name: getActiveMonitoringPlans Method Description: getActiveMonitoringPlans.
     *
     * @param servicePlanId
     * @return List<ApsServicePlanMonitoringDto>
     */
    List<ApsServicePlanMonitoringDto> getActiveMonitoringPlans(Long servicePlanId);

    /**
     * Method Name: getServiceProblems Method Description: getServiceProblems
     *
     * @param sourceId
     * @return List<ApsServicePlanProblemDto>
     */
    List<ApsServicePlanProblemDto> getServiceProblems(Long sourceId);

    /**
     * Method Name: getServiceActions Method Description: getServiceActions.
     *
     * @param sourceId
     * @return List<ApsServicePlanActionDto>
     */
    List<ApsServicePlanActionDto> getServiceActions(Long sourceId);

    /**
     * Method Name: getServicesForSNA.
     *
     * @param servicePlanId
     * @return List<ApsServicePlanSourceDto>
     */
    List<ApsServicePlanSourceDto> getServicesForSNA(Long servicePlanId);

    /**
     * Method Name: getServicesForALLEG.
     *
     * @param servicePlanId
     * @return List<ApsServicePlanSourceDto>
     */
    List<ApsServicePlanSourceDto> getServicesForALLEG(Long servicePlanId);

    /**
     * Method Name: getServicesForSA.
     *
     * @param servicePlanId
     * @return List<ApsServicePlanSourceDto>
     */
    List<ApsServicePlanSourceDto> getServicesForSA(Long servicePlanId);

    /**
     * Method Name: getMonitoringPlansAll.
     *
     * @param servicePlanId
     * @return List<ApsServicePlanMonitoringDto>
     */
    List<ApsServicePlanMonitoringDto> getMonitoringPlansAll(Long servicePlanId);

    //Long getCompletedSACount(Long caseId);

    /**
     * Method Description:This method returns aps rora SP problem count
     * @param stageId
     * @return Long
     */
    Long getApsSPProblemCount(Long stageId);

    Long getPcsActionCategoryCountBtStageId(Long idStage);
}
