package us.tx.state.dfps.service.apssafetyassmt.daoimpl;

import us.tx.state.dfps.service.apssafetyassmt.dao.ApsSafetyAssessmentDao;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentCaretakerDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentContactDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentNarrativeDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentResponseDto;

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
 * Description:ApsSafetyAssessmentDaoImpl Jan 04, 2022- 1:52:46 PM © 2022 Texas Department
 * of Family and Protective Services
 */

@Repository
public class ApsSafetyAssessmentDaoImpl implements ApsSafetyAssessmentDao {

    private static final Logger log = Logger.getLogger(ApsSafetyAssessmentDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;


    @Value("${ApsSafetyAssessmentDaoImpl.getApsSafetyAssessment}")
    private String getApsSafetyAssessmentSql;

    @Value("${ApsSafetyAssessmentDaoImpl.getContacts}")
    private String getContactsSql;

    @Value("${ApsSafetyAssessmentDaoImpl.getSelectedCaretakers}")
    private String getSelectedCaretakersSql;

    @Value("${ApsSafetyAssessmentDaoImpl.getAvailableCaretakers}")
    private String getAvailableCaretakerSql;

    @Value("${ApsSafetyAssessmentDaoImpl.getNarrative}")
    private String getNarrativeSql;

    @Value("${ApsSafetyAssessmentDaoImpl.getResponses}")
    private String getResponsesSql;

    @Value("${ApsSafetyAssessmentDaoImpl.getSaContacts}")
    private String getSaContactsSql;

    @Value("${ApsSafetyAssessmentDaoImpl.getSaEvents}")
    private String getSaEventsSql;


    @Override
    public ApsSafetyAssessmentDto getApsSafetyAssessmentData(Long idEvent) {
        log.debug("Entering method getApsSafetyAssessmentData in ApsSafetyAssessmentDaoImpl");
        ApsSafetyAssessmentDto apsSafetyAssessmentDto = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsSafetyAssessmentSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("stageId",StandardBasicTypes.LONG)
                .addScalar("dateLastUpdate",StandardBasicTypes.DATE)
                .addScalar("formVersionNumber",StandardBasicTypes.LONG)
                .addScalar("formVersionId",StandardBasicTypes.LONG)
                .addScalar("caseId", StandardBasicTypes.LONG)
                .addScalar("eventId", StandardBasicTypes.LONG)
                .addScalar("eventStatus", StandardBasicTypes.STRING)
                .addScalar("eventDateLastUpdate", StandardBasicTypes.DATE)
                .addScalar("taskCode", StandardBasicTypes.STRING)
                .addScalar("dateAssessmentCompleted", StandardBasicTypes.DATE)
                .addScalar("initialStagePriority", StandardBasicTypes.STRING)
                .addScalar("currentStagePriority", StandardBasicTypes.STRING)
                .addScalar("stageCode", StandardBasicTypes.STRING)
                .addScalar("stageTypeCode", StandardBasicTypes.STRING)
                .addScalar("stageDateLastUpdate", StandardBasicTypes.DATE)
                .addScalar("stagePriorityCmnts", StandardBasicTypes.STRING)
                .addScalar("indImmediateIntervention", StandardBasicTypes.STRING)
                .addScalar("indReferralRequired", StandardBasicTypes.STRING)
                .addScalar("indInterventionsInPlace", StandardBasicTypes.STRING)
                .addScalar("indCaseInitiationComplete", StandardBasicTypes.STRING)
                .addScalar("savedSafetyDecisionCode", StandardBasicTypes.STRING)
                .addScalar("indCaretakerNotApplicable", StandardBasicTypes.STRING)
                .addScalar("assessmentType", StandardBasicTypes.STRING)
                .setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(ApsSafetyAssessmentDto.class)));
        apsSafetyAssessmentDto =(ApsSafetyAssessmentDto) sQLQuery1.uniqueResult();

        return apsSafetyAssessmentDto;
    }



    @Override
    public List<ApsSafetyAssessmentContactDto> getContacts(Long idEvent) {
        log.debug("Entering method getContacts in ApsSafetyAssessmentDaoImpl");
        List<ApsSafetyAssessmentContactDto> apsSafetyAssessmentContactDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContactsSql)
                .addScalar("contactEventId", StandardBasicTypes.LONG)
                .addScalar("contactWorkerId",StandardBasicTypes.LONG)
                .addScalar("indContactAttempted",StandardBasicTypes.STRING)
                .addScalar("contactType",StandardBasicTypes.STRING)
                .addScalar("dateContactOccurred",StandardBasicTypes.DATE)
                .addScalar("dateLastUpdate",StandardBasicTypes.DATE)
                .addScalar("contactWorkerFullName",StandardBasicTypes.STRING)
                .addScalar("tsContactOccurred",StandardBasicTypes.TIMESTAMP)
                .addScalar("idApsSaEvent", StandardBasicTypes.LONG)
                .setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(ApsSafetyAssessmentContactDto.class)));
        apsSafetyAssessmentContactDtoList = sQLQuery1.list();

        return apsSafetyAssessmentContactDtoList;
    }

    @Override
    public List<ApsSafetyAssessmentCaretakerDto> getSelectedCaretakersList(Long idEvent) {
        log.debug("Entering method getSelectedCaretakersList in ApsSafetyAssessmentDaoImpl");
        List<ApsSafetyAssessmentCaretakerDto> apsSafetyAssessmentCaretakerDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSelectedCaretakersSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("fullName",StandardBasicTypes.STRING)
                .addScalar("caretakerType",StandardBasicTypes.STRING)
                .addScalar("caretakerRole",StandardBasicTypes.STRING)
                .addScalar("caretakerRelation",StandardBasicTypes.STRING)
                .addScalar("dateLastUpdate",StandardBasicTypes.DATE)
                .addScalar("eventId", StandardBasicTypes.LONG)
                .addScalar("caseId", StandardBasicTypes.LONG)
                .setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(ApsSafetyAssessmentCaretakerDto.class)));
        apsSafetyAssessmentCaretakerDtoList = sQLQuery1.list();

        return apsSafetyAssessmentCaretakerDtoList;
    }


    @Override
    public List<ApsSafetyAssessmentCaretakerDto> getAvailableCaretakersList(Long idStage) {
        log.debug("Entering method getAvailableCaretakersList in ApsSafetyAssessmentDaoImpl");
        List<ApsSafetyAssessmentCaretakerDto> apsSafetyAssessmentCaretakerDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAvailableCaretakerSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("fullName",StandardBasicTypes.STRING)
                .addScalar("caretakerType",StandardBasicTypes.STRING)
                .addScalar("caretakerRole",StandardBasicTypes.STRING)
                .addScalar("caretakerRelation",StandardBasicTypes.STRING)
                .addScalar("dateLastUpdate",StandardBasicTypes.DATE)
                .addScalar("eventId", StandardBasicTypes.LONG)
                .addScalar("caseId", StandardBasicTypes.LONG)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(ApsSafetyAssessmentCaretakerDto.class)));
        apsSafetyAssessmentCaretakerDtoList = sQLQuery1.list();

        return apsSafetyAssessmentCaretakerDtoList;
    }

    @Override
    public ApsSafetyAssessmentNarrativeDto getNarrative(Long idEvent) {
        log.debug("Entering method getNarrative in ApsSafetyAssessmentDaoImpl");
        ApsSafetyAssessmentNarrativeDto apsSafetyAssessmentNarrativeDto = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getNarrativeSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("createdPersonId",StandardBasicTypes.LONG)
                .addScalar("dateLastUpdate",StandardBasicTypes.DATE)
                .addScalar("updatedPersonId",StandardBasicTypes.LONG)
                .setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(ApsSafetyAssessmentNarrativeDto.class)));
        apsSafetyAssessmentNarrativeDto =(ApsSafetyAssessmentNarrativeDto) sQLQuery1.uniqueResult();

        return apsSafetyAssessmentNarrativeDto;
    }

    @Override
    public List<ApsSafetyAssessmentResponseDto> getResponses(Long idApsSafetyAssessment) {
        log.debug("Entering method getResponses in ApsSafetyAssessmentDaoImpl");
        List<ApsSafetyAssessmentResponseDto> apsSafetyAssessmentResponseDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResponsesSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("safetyAssessmentId",StandardBasicTypes.LONG)
                .addScalar("questionLookupId",StandardBasicTypes.LONG)
                .addScalar("questionResponseCode",StandardBasicTypes.STRING)
                .addScalar("dateLastUpdate",StandardBasicTypes.DATE)
                .addScalar("questionCode",StandardBasicTypes.STRING)
                .addScalar("questionText", StandardBasicTypes.STRING)
                .addScalar("sectionCode", StandardBasicTypes.STRING)
                .addScalar("questionOrder", StandardBasicTypes.LONG)
                .addScalar("questionDefinition", StandardBasicTypes.STRING)
                .setParameter("idApsSa", idApsSafetyAssessment)
                .setResultTransformer(Transformers.aliasToBean(ApsSafetyAssessmentResponseDto.class)));
        apsSafetyAssessmentResponseDtoList = sQLQuery1.list();

        return apsSafetyAssessmentResponseDtoList;
    }

    @Override
    public List<ApsSafetyAssessmentContactDto> getSaContacts(Long idCase) {
        log.debug("Entering method getSaContactsSql in ApsSafetyAssessmentDaoImpl");
        List<ApsSafetyAssessmentContactDto> apsSafetyAssessmentContactDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSaContactsSql)
                .addScalar("idApsSaEvent", StandardBasicTypes.LONG)
                .addScalar("contactEventId", StandardBasicTypes.LONG)
                .addScalar("contactType",StandardBasicTypes.STRING)
                .addScalar("cdStage",StandardBasicTypes.STRING)
                .addScalar("idStage",StandardBasicTypes.LONG)
                .addScalar("dateContactEntered",StandardBasicTypes.DATE)
                .addScalar("contactWorkerId",StandardBasicTypes.LONG)
                .addScalar("indContactAttempted",StandardBasicTypes.STRING)
                .addScalar("dateContactOccurred",StandardBasicTypes.DATE)
                .addScalar("tsContactOccurred",StandardBasicTypes.TIMESTAMP)
                .addScalar("location",StandardBasicTypes.STRING)
                .addScalar("contactWorkerFullName",StandardBasicTypes.STRING)
                .addScalar("contactMethodType",StandardBasicTypes.STRING)
                .addScalar("contactPurpose",StandardBasicTypes.STRING)
                .setParameter("idCase", idCase)
                .setResultTransformer(Transformers.aliasToBean(ApsSafetyAssessmentContactDto.class)));
        apsSafetyAssessmentContactDtoList = sQLQuery1.list();
        return apsSafetyAssessmentContactDtoList;
    }

    @Override
    public List<ApsSafetyAssessmentDto> getSaEvents(Long idCase) {
        log.debug("Entering method getSaEvents in ApsSafetyAssessmentDaoImpl");
        List<ApsSafetyAssessmentDto> apsSafetyAssessmentDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSaEventsSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("eventId", StandardBasicTypes.LONG)
                .addScalar("assessmentType",StandardBasicTypes.STRING)
                .addScalar("stageCode",StandardBasicTypes.STRING)
                .addScalar("stageId",StandardBasicTypes.LONG)
                .addScalar("dateAssessmentCompleted",StandardBasicTypes.DATE)
                .setParameter("idCase", idCase)
                .setResultTransformer(Transformers.aliasToBean(ApsSafetyAssessmentDto.class)));
        apsSafetyAssessmentDtoList = sQLQuery1.list();
        return apsSafetyAssessmentDtoList;
    }

}







