package us.tx.state.dfps.service.apsrora.daoimpl;

import us.tx.state.dfps.service.apsrora.dao.ApsRoraDao;
import us.tx.state.dfps.service.apsrora.dto.APSRoraAnswerDto;
import us.tx.state.dfps.service.apsrora.dto.APSRoraFollowupAnswerDto;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraDto;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraResponseDto;
import us.tx.state.dfps.service.apssna.dto.ApsStrengthsAndNeedsAssessmentDto;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * service-business- IMPACT APS MODERNIZATION Class
 * Description:ApsRoraDaoImpl Jan 04, 2022- 1:52:46 PM © 2022 Texas Department
 * of Family and Protective Services
 */

@Repository
public class ApsRoraDaoImpl implements ApsRoraDao {

    private static final Logger log = Logger.getLogger(ApsRoraDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${ApsRoraDaoImpl.getApsRora}")
    private String getApsRoraSql;

    @Value("${ApsRoraDaoImpl.getApsRoraResponse}")
    private String getApsRoraResponseSql;

    @Value("${ApsRoraDaoImpl.getApsRoraAnswers}")
    private String getApsRoraAnswersSql;

    @Value("${ApsRoraDaoImpl.getApsRoraFollowupAnswers}")
    private String getApsRoraFollowupAnswersSql;

    @Value("${ApsRoraDaoImpl.getRoraInfo}")
    private String getRoraInfoSql;

    @Override
    public ApsRoraDto getRoraReportData(Long idEvent) {
        log.debug("Entering method getRoraReportData in EventDaoImpl");
        ApsRoraDto apsRoraDto = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsRoraSql)
                .addScalar("apsRoraId", StandardBasicTypes.LONG)
                .addScalar("stageId",StandardBasicTypes.LONG)
                .addScalar("dtCreated",StandardBasicTypes.DATE)
                .addScalar("createdPersonId",StandardBasicTypes.LONG)
                .addScalar("dtLastUpdate",StandardBasicTypes.DATE)
                .addScalar("updatedPersonId", StandardBasicTypes.LONG)
                .addScalar("selfNeglectScore", StandardBasicTypes.INTEGER)
                .addScalar("aneScore", StandardBasicTypes.INTEGER)
                .addScalar("mandatoryOverrideInd", StandardBasicTypes.BOOLEAN)
                .addScalar("selfNeglectRiskLevelCode", StandardBasicTypes.STRING)
                .addScalar("aneRiskLevelCode", StandardBasicTypes.STRING)
                .addScalar("scoredRiskLevelCode", StandardBasicTypes.STRING)
                .addScalar("commentText", StandardBasicTypes.STRING)
                .addScalar("reasonForOverrideText", StandardBasicTypes.STRING)
                .addScalar("discretionaryOverrideCode", StandardBasicTypes.STRING)
                .addScalar("finalRiskLevelCode", StandardBasicTypes.STRING)
                .addScalar("versionNumber", StandardBasicTypes.INTEGER)
                .addScalar("apsRoraAssessmentId", StandardBasicTypes.LONG)
                .addScalar("caseId", StandardBasicTypes.LONG)
                .addScalar("eventId", StandardBasicTypes.LONG)
                .addScalar("eventStatus", StandardBasicTypes.STRING)
                .addScalar("eventDateLastUpdate", StandardBasicTypes.DATE)
                .addScalar("taskCode", StandardBasicTypes.STRING)
                .setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(ApsRoraDto.class)));
        apsRoraDto =(ApsRoraDto) sQLQuery1.uniqueResult();

        return apsRoraDto;
    }

    @Override
    public List<ApsRoraResponseDto> getRoraResponseReportData(Long apsRoraId) {
        log.debug("Entering method getRoraResponseReportData in EventDaoImpl");
        List<ApsRoraResponseDto> apsRoraResponseDtoList = new ArrayList<>();
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsRoraResponseSql)
                .addScalar("apsRoraResponseId",StandardBasicTypes.LONG)
                .addScalar("apsRoraId",StandardBasicTypes.LONG)
                .addScalar("questionLookupId",StandardBasicTypes.LONG)
                .addScalar("answerLookupId",StandardBasicTypes.LONG)
                .addScalar("questionOrder",StandardBasicTypes.LONG)
                .addScalar("dateLastUpdate", StandardBasicTypes.DATE)
                .addScalar("questionCode", StandardBasicTypes.STRING)
                .addScalar("questionDefinition", StandardBasicTypes.STRING)
                .addScalar("questionText", StandardBasicTypes.STRING)
                .addScalar("sectionCode", StandardBasicTypes.STRING)
                .setParameter("apsRoraId", apsRoraId)
                .setResultTransformer(Transformers.aliasToBean(ApsRoraResponseDto.class)));
        apsRoraResponseDtoList = sQLQuery1.list();
        return apsRoraResponseDtoList;
    }

    @Override
    public List<APSRoraAnswerDto> getRoraAnswerReportData(Long apsRoraQuestionLookUpId) {
        log.debug("Entering method getRoraAnswerReportData in EventDaoImpl");
        List<APSRoraAnswerDto> apsRoraAnswerDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsRoraAnswersSql)
                .addScalar("roraAnswerId",StandardBasicTypes.LONG)
                .addScalar("apsRoraQuestionLookupId",StandardBasicTypes.LONG)
                .addScalar("selfNeglectIndexValue",StandardBasicTypes.LONG)
                .addScalar("aneIndexValue",StandardBasicTypes.LONG)
                .addScalar("orderNumber",StandardBasicTypes.LONG)
                .addScalar("roraAnswerCode", StandardBasicTypes.STRING)
                .addScalar("answerText", StandardBasicTypes.STRING)
                .setParameter("apsRoraQuestionLookUpId", apsRoraQuestionLookUpId)
                .setResultTransformer(Transformers.aliasToBean(APSRoraAnswerDto.class)));
        apsRoraAnswerDtoList = sQLQuery1.list();

        return apsRoraAnswerDtoList;

    }

    @Override
    public List<APSRoraFollowupAnswerDto> getRoraFollowupAnswerReportData(Long apsRoraResponseId, Long apsRoraAnswerLookUpId) {
        log.debug("Entering method getRoraFollowupAnswerReportData in EventDaoImpl");
        List<APSRoraFollowupAnswerDto> apsRoraFollowupAnswerDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsRoraFollowupAnswersSql)
                .addScalar("followupAnswerId",StandardBasicTypes.LONG)
                .addScalar("responseId",StandardBasicTypes.LONG)
                .addScalar("answerLookupId",StandardBasicTypes.LONG)
                .addScalar("answerText", StandardBasicTypes.STRING)
                .addScalar("orderNumber", StandardBasicTypes.LONG)
                .addScalar("indApsRoraFollowup", StandardBasicTypes.STRING)
                .setParameter("apsRoraResponseId", apsRoraResponseId)
                .setParameter("apsRoraAnswerLookUpId", apsRoraAnswerLookUpId)
                .setResultTransformer(Transformers.aliasToBean(APSRoraFollowupAnswerDto.class)));
        apsRoraFollowupAnswerDtoList =sQLQuery1.list();

        return apsRoraFollowupAnswerDtoList;
    }

    @Override
    public List<ApsRoraDto> getRoraInformation(Long idCase) {
        log.debug("Entering method getRoraInfo in EventDaoImpl");
        List<ApsRoraDto> apsRoraDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRoraInfoSql)
                .addScalar("scoredRiskLevelCode", StandardBasicTypes.STRING)
                .addScalar("discretionaryOverrideCode", StandardBasicTypes.STRING)
                .addScalar("reasonForOverrideText", StandardBasicTypes.STRING)
                .addScalar("finalRiskLevelCode", StandardBasicTypes.STRING)
                .addScalar("dtRoraComplete",StandardBasicTypes.DATE)
                .addScalar("eventId", StandardBasicTypes.LONG)
                .addScalar("stageId",StandardBasicTypes.LONG)
                .addScalar("apsRoraAssessmentId",StandardBasicTypes.LONG)
                .setParameter("idCase", idCase)
                .setResultTransformer(Transformers.aliasToBean(ApsRoraDto.class)));
        apsRoraDtoList =sQLQuery1.list();

        return apsRoraDtoList;
    }



}
