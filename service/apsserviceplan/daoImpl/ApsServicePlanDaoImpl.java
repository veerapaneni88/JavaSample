package us.tx.state.dfps.service.apsserviceplan.daoImpl;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import us.tx.state.dfps.service.apsserviceplan.dao.ApsServicePlanDao;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanActionDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanMonitoringDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanProblemDto;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanSourceDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class ApsServicePlanDaoImpl implements ApsServicePlanDao {

    private static final Logger log = Logger.getLogger(ApsServicePlanDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;


    @Value("${ApsServicePlanDaoImpl.getServicePlanByStage}")
    private String getServicePlanByStageSql;

    @Value("${ApsServicePlanDaoImpl.getNewStageProgressionId}")
    private String getNewStageProgressionId;

    @Value("${ApsServicePlanDaoImpl.getServiceSource}")
    private String getServiceSourceSql;

    @Value("${ApsServicePlanDaoImpl.getMonitoringPlan}")
    private String getMonitoringPlanSql;

    @Value("${ApsServicePlanDaoImpl.getActiveMonitoringPlan}")
    private String getActiveMonitoringPlanSql;

    @Value("${ApsServicePlanDaoImpl.getServiceProblem}")
    private String getServiceProblemSql;

    @Value("${ApsServicePlanDaoImpl.getServiceAction}")
    private String getServiceActionSql;

    @Value("${ApsServicePlanDaoImpl.getMonitoringPlansAll}")
    private String getMonitoringPlanAllSql;

    @Value("${ApsServicePlanDaoImpl.getServicesForSNA}")
    private String getServicesForSNASql;

    @Value("${ApsServicePlanDaoImpl.getServicesForALLEG}")
    private String getServicesForALLEGSql;

    @Value("${ApsServicePlanDaoImpl.getServicesForSA}")
    private String getServicesForSASql;

    @Value("${ApsServicePlanDaoImpl.getCompletedSACount}")
    private String getCompletedSACountSql;




    @Value("${ApsServicePlanDaoImpl.getApsSPProblemCount}")
    private String getApsSPProblemCountSql;

    @Value("${ApsServicePlanDaoImpl.getPcsActionCategoryCount}")
    private String getPcsActionCategorySql;

    @Override
    public ApsServicePlanDto getServicePlanByStage(Long idStage) {
        log.debug("Entering method getServicePlanByStage in ApsServicePlanDaoImpl");
        ApsServicePlanDto apsServicePlanDto = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getServicePlanByStageSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("servicePlanEventId", StandardBasicTypes.LONG)
                .addScalar("caseId", StandardBasicTypes.LONG)
                .addScalar("dateLastUpdate", StandardBasicTypes.DATE)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(ApsServicePlanDto.class)));
        apsServicePlanDto = (ApsServicePlanDto) sQLQuery1.uniqueResult();

        if(apsServicePlanDto != null){
            apsServicePlanDto.setCurrServiceSources(getServiceSources(apsServicePlanDto.getId(), apsServicePlanDto.getCaseId()));
        }

        return apsServicePlanDto;
    }

    @Override
    public Long getProgressedStageId(Long idCase, Long idStage) {
        log.debug("Entering method getProgressedStageId in ApsServicePlanDaoImpl");
        Long stageId = 0L;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getNewStageProgressionId)
                .setParameter("idPriorStage", idStage)
                .setParameter("idCase", idCase));
        List<BigDecimal> priorStageList = sQLQuery1.list();
        if(!CollectionUtils.isEmpty(priorStageList)){
            for(BigDecimal id : priorStageList){
                stageId = id.longValue();
            }
        }
        return stageId;
    }

    @Override
    public List<ApsServicePlanSourceDto> getServiceSources(Long servicePlanId, Long sourceId) {
        log.debug("Entering method queryServicePlanForSA in ApsServicePlanDaoImpl");
        List<ApsServicePlanSourceDto> apsServicePlanSourceDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getServiceSourceSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("servicePlanId", StandardBasicTypes.LONG)
                .addScalar("saId", StandardBasicTypes.LONG)
                .addScalar("snaId", StandardBasicTypes.LONG)
                .addScalar("allegationId", StandardBasicTypes.LONG)
                .addScalar("saResponseId", StandardBasicTypes.LONG)
                .addScalar("snaResponseId", StandardBasicTypes.LONG)
                .addScalar("allegPerpetratorId", StandardBasicTypes.LONG)
                .addScalar("sourceType", StandardBasicTypes.STRING)
                .addScalar("sourceCode", StandardBasicTypes.STRING)
                .addScalar("sourceText", StandardBasicTypes.STRING)
                .addScalar("allegPerpetratorName", StandardBasicTypes.STRING)
                .addScalar("dateLastUpdate", StandardBasicTypes.DATE)
                .setParameter("servicePlanId1", servicePlanId)
                .setParameter("servicePlanId2", servicePlanId)
                .setParameter("sourceId1", sourceId)
                .setParameter("sourceId2", sourceId)
                .setResultTransformer(Transformers.aliasToBean(ApsServicePlanSourceDto.class)));
        apsServicePlanSourceDtoList =  sQLQuery1.list();

        return apsServicePlanSourceDtoList;
    }

    @Override
    public List<ApsServicePlanMonitoringDto> getMonitoringPlans(Long servicePlanId, Long sourceId) {
        log.debug("Entering method getMonitoringPlans in ApsServicePlanDaoImpl");
        List<ApsServicePlanMonitoringDto> apsServicePlanMonitoringDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getMonitoringPlanSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("servicePlanId", StandardBasicTypes.LONG)
                .addScalar("safetyAssessmentId", StandardBasicTypes.LONG)
                .addScalar("monitoringPlanSourceCode", StandardBasicTypes.STRING)
                .addScalar("numberOfContactsReqd", StandardBasicTypes.LONG)
                .addScalar("planDescription", StandardBasicTypes.STRING)
                .addScalar("noOfFaceToFaceContactsRequired", StandardBasicTypes.LONG)
                .addScalar("dateLastUpdate", StandardBasicTypes.DATE)
                .addScalar("monitoringPlanStartDate", StandardBasicTypes.DATE)
                .addScalar("monitoringPlanEndDate", StandardBasicTypes.DATE)
                .setParameter("servicePlanId", servicePlanId)
                .setParameter("sourceId", sourceId)
                .setResultTransformer(Transformers.aliasToBean(ApsServicePlanMonitoringDto.class)));
        apsServicePlanMonitoringDtoList =  sQLQuery1.list();

        return apsServicePlanMonitoringDtoList;
    }

    @Override
    public List<ApsServicePlanMonitoringDto> getActiveMonitoringPlans(Long servicePlanId) {
        log.debug("Entering method getActiveMonitoringPlans in ApsServicePlanDaoImpl");
        List<ApsServicePlanMonitoringDto> apsServicePlanMonitoringDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getActiveMonitoringPlanSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("servicePlanId", StandardBasicTypes.LONG)
                .addScalar("safetyAssessmentId", StandardBasicTypes.LONG)
                .addScalar("monitoringPlanSourceCode", StandardBasicTypes.STRING)
                .addScalar("numberOfContactsReqd", StandardBasicTypes.LONG)
                .addScalar("planDescription", StandardBasicTypes.STRING)
                .addScalar("noOfFaceToFaceContactsRequired", StandardBasicTypes.LONG)
                .addScalar("dateLastUpdate", StandardBasicTypes.DATE)
                .addScalar("monitoringPlanStartDate", StandardBasicTypes.DATE)
                .addScalar("monitoringPlanEndDate", StandardBasicTypes.DATE)
                .setParameter("servicePlanId", servicePlanId)
                .setResultTransformer(Transformers.aliasToBean(ApsServicePlanMonitoringDto.class)));
        apsServicePlanMonitoringDtoList =  sQLQuery1.list();

        return apsServicePlanMonitoringDtoList;
    }

    @Override
    public List<ApsServicePlanProblemDto> getServiceProblems(Long sourceId) {
        log.debug("Entering method getServiceProblems in ApsServicePlanDaoImpl");
        List<ApsServicePlanProblemDto> apsServicePlanProblemDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getServiceProblemSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("serviceSourceId", StandardBasicTypes.LONG)
                .addScalar("outcomeType", StandardBasicTypes.STRING)
                .addScalar("problemDescription", StandardBasicTypes.STRING)
                .addScalar("dateLastUpdate", StandardBasicTypes.DATE)
                .setParameter("sourceId", sourceId)
                .setResultTransformer(Transformers.aliasToBean(ApsServicePlanProblemDto.class)));
        apsServicePlanProblemDtoList =  sQLQuery1.list();

        return apsServicePlanProblemDtoList;
    }

    @Override
    public List<ApsServicePlanActionDto> getServiceActions(Long sourceId) {
        log.debug("Entering method getServiceActions in ApsServicePlanDaoImpl");
        List<ApsServicePlanActionDto> apsServicePlanActionDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getServiceActionSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("actionProblemLinkId", StandardBasicTypes.LONG)
                .addScalar("idApsSp", StandardBasicTypes.LONG)
                .addScalar("categoryCode", StandardBasicTypes.STRING)
                .addScalar("resultsCode", StandardBasicTypes.STRING)
                .addScalar("description", StandardBasicTypes.STRING)
                .addScalar("dateLastUpdate", StandardBasicTypes.DATE)
                .setParameter("sourceId", sourceId)
                .setResultTransformer(Transformers.aliasToBean(ApsServicePlanActionDto.class)));
        apsServicePlanActionDtoList =  sQLQuery1.list();

        return apsServicePlanActionDtoList;
    }

    @Override
    public List<ApsServicePlanSourceDto> getServicesForSNA(Long servicePlanId) {
        log.debug("Entering method getServicesForSNA in ApsServicePlanDaoImpl");
        List<ApsServicePlanSourceDto> apsServicePlanSourceDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getServicesForSNASql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("servicePlanId", StandardBasicTypes.LONG)
                .addScalar("saId", StandardBasicTypes.LONG)
                .addScalar("snaId", StandardBasicTypes.LONG)
                .addScalar("allegationId", StandardBasicTypes.LONG)
                .addScalar("saResponseId", StandardBasicTypes.LONG)
                .addScalar("snaResponseId", StandardBasicTypes.LONG)
                .addScalar("allegPerpetratorId", StandardBasicTypes.LONG)
                .addScalar("sourceType", StandardBasicTypes.STRING)
                .addScalar("sourceCode", StandardBasicTypes.STRING)
                .addScalar("sourceText", StandardBasicTypes.STRING)
                .addScalar("allegPerpetratorName", StandardBasicTypes.STRING)
                .addScalar("dateLastUpdate", StandardBasicTypes.DATE)
                .setParameter("servicePlanId", servicePlanId)
                .setResultTransformer(Transformers.aliasToBean(ApsServicePlanSourceDto.class)));
        apsServicePlanSourceDtoList =  sQLQuery1.list();

        return apsServicePlanSourceDtoList;
    }

    @Override
    public List<ApsServicePlanSourceDto> getServicesForALLEG(Long servicePlanId) {
        log.debug("Entering method getServicesForALLEG in ApsServicePlanDaoImpl");
        List<ApsServicePlanSourceDto> apsServicePlanSourceDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getServicesForALLEGSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("servicePlanId", StandardBasicTypes.LONG)
                .addScalar("saId", StandardBasicTypes.LONG)
                .addScalar("snaId", StandardBasicTypes.LONG)
                .addScalar("allegationId", StandardBasicTypes.LONG)
                .addScalar("saResponseId", StandardBasicTypes.LONG)
                .addScalar("snaResponseId", StandardBasicTypes.LONG)
                .addScalar("allegPerpetratorId", StandardBasicTypes.LONG)
                .addScalar("sourceType", StandardBasicTypes.STRING)
                .addScalar("sourceCode", StandardBasicTypes.STRING)
                .addScalar("sourceText", StandardBasicTypes.STRING)
                .addScalar("allegPerpetratorName", StandardBasicTypes.STRING)
                .addScalar("dateLastUpdate", StandardBasicTypes.DATE)
                .setParameter("servicePlanId", servicePlanId)
                .setResultTransformer(Transformers.aliasToBean(ApsServicePlanSourceDto.class)));
        apsServicePlanSourceDtoList =  sQLQuery1.list();

        return apsServicePlanSourceDtoList;
    }

    @Override
    public List<ApsServicePlanSourceDto> getServicesForSA(Long servicePlanId) {
        log.debug("Entering method getServicesForSA in ApsServicePlanDaoImpl");
        List<ApsServicePlanSourceDto> apsServicePlanSourceDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getServicesForSASql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("servicePlanId", StandardBasicTypes.LONG)
                .addScalar("saId", StandardBasicTypes.LONG)
                .addScalar("snaId", StandardBasicTypes.LONG)
                .addScalar("allegationId", StandardBasicTypes.LONG)
                .addScalar("saResponseId", StandardBasicTypes.LONG)
                .addScalar("snaResponseId", StandardBasicTypes.LONG)
                .addScalar("allegPerpetratorId", StandardBasicTypes.LONG)
                .addScalar("sourceType", StandardBasicTypes.STRING)
                .addScalar("sourceCode", StandardBasicTypes.STRING)
                .addScalar("sourceText", StandardBasicTypes.STRING)
                .addScalar("allegPerpetratorName", StandardBasicTypes.STRING)
                .addScalar("dateLastUpdate", StandardBasicTypes.DATE)
                .setParameter("servicePlanId", servicePlanId)
                .setResultTransformer(Transformers.aliasToBean(ApsServicePlanSourceDto.class)));
        apsServicePlanSourceDtoList =  sQLQuery1.list();

        return apsServicePlanSourceDtoList;
    }

    @Override
    public List<ApsServicePlanMonitoringDto> getMonitoringPlansAll(Long servicePlanId) {
        log.debug("Entering method getMonitoringPlans in ApsServicePlanDaoImpl");
        List<ApsServicePlanMonitoringDto> apsServicePlanMonitoringDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getMonitoringPlanAllSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("servicePlanId", StandardBasicTypes.LONG)
                .addScalar("safetyAssessmentId", StandardBasicTypes.LONG)
                .addScalar("monitoringPlanSourceCode", StandardBasicTypes.STRING)
                .addScalar("numberOfContactsReqd", StandardBasicTypes.LONG)
                .addScalar("planDescription", StandardBasicTypes.STRING)
                .addScalar("noOfFaceToFaceContactsRequired", StandardBasicTypes.LONG)
                .addScalar("dateLastUpdate", StandardBasicTypes.DATE)
                .addScalar("monitoringPlanStartDate", StandardBasicTypes.DATE)
                .addScalar("monitoringPlanEndDate", StandardBasicTypes.DATE)
                .setParameter("servicePlanId", servicePlanId)
                .setResultTransformer(Transformers.aliasToBean(ApsServicePlanMonitoringDto.class)));
        apsServicePlanMonitoringDtoList =  sQLQuery1.list();

        return apsServicePlanMonitoringDtoList;
    }

    @Override
    public Long getApsSPProblemCount(Long stageId) {
        Long count = 0l;
        if (null != stageId) {
            Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getApsSPProblemCountSql)
                    .setParameter("idEvntStg", stageId);
            count = ((BigDecimal) query.uniqueResult()).longValue();
        }
        return count;
    }

    /**
     * Method helps to execute the query and returns the count
     *
     * @param idStage - selected id stage
     * @return - record exists returns 1 else 0
     */
    @Override
    @Transactional
    public Long getPcsActionCategoryCountBtStageId(Long idStage) {
        Long count = 0l;
        if (null != idStage) {
            Query query =  sessionFactory.getCurrentSession().createSQLQuery(getPcsActionCategorySql)
                    .setParameter("idStage", idStage);
            count = ((BigDecimal) query.uniqueResult()).longValue();
        }
        return count;
    }
}


