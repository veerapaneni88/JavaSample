package us.tx.state.dfps.service.kincaregiverhomeassmnt.daoimpl;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.KinHomeAssessmentDetail;
import us.tx.state.dfps.common.domain.KinHomeAssessmentDetailComments;
import us.tx.state.dfps.common.domain.PlacementAudit;
import us.tx.state.dfps.service.kincaregiverhomeassmnt.dao.CvsKinCaregiverHomeAssmtDao;

import java.util.List;

/**
 * service-business - Kinship CareGiver Home Assessment (CVSKINHOMEASSESSMENT)
 * 02/20/2025 thompswa ppm84014 : Prefill Service for CVSKINHOMEASSESSMENT
 */
@Repository
public class CvsKinCaregiverHomeAssmtDaoImpl  implements CvsKinCaregiverHomeAssmtDao {

    @Autowired
    SessionFactory sessionFactory;
    @Value("${CvsKinCaregiverHomeAssmtDaoImpl.getKinCaregiverInfoSql}")
    private String caseCaregiverInfoSql;

    @Value("${CvsKinCaregiverHomeAssmtDaoImpl.getKinPlacementInfoSql}")
    private String placementInfoSql;

    @Value("${CvsKinCaregiverHomeAssmtDaoImpl.getKinHomeCommentsSql}")
    private String kinCommentsSql;

    /**
     *
     * Method Name: getKinCaregiverCaseInfoByKamEvent Method Description: Retrieves Case/Caregiver Information
     *  using ID for KAM EVENT as input.
     *
     * @param idEvent
     * @return KinHomeAssessmentDetail
     */
    @Override
    public KinHomeAssessmentDetail getKinCaregiverCaseInfoByKamEvent(Long idEvent) {


        return (KinHomeAssessmentDetail)sessionFactory
                .getCurrentSession().createSQLQuery(caseCaregiverInfoSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("eventId", StandardBasicTypes.LONG)
                .addScalar("caseId", StandardBasicTypes.LONG)
                .addScalar("stageId", StandardBasicTypes.LONG)
                .addScalar("svcAuthId", StandardBasicTypes.LONG)
                .addScalar("kinCaregiverId", StandardBasicTypes.LONG)
                .addScalar("serviceAuthorizedDate", StandardBasicTypes.DATE)
                .addScalar("dtHmAssmtSubmitted", StandardBasicTypes.DATE)
                .addScalar("approvedDate", StandardBasicTypes.DATE)
                .addScalar("deniedDate", StandardBasicTypes.DATE)
                .addScalar("cdApproved", StandardBasicTypes.STRING)
                .addScalar("autoPopulated", StandardBasicTypes.STRING)
                .addScalar("saveCompleteClicked", StandardBasicTypes.STRING)
                .addScalar("fixedEdited", StandardBasicTypes.STRING)
                .addScalar("indCrimHist", StandardBasicTypes.STRING)
                .addScalar("indAbuseNeglectHist", StandardBasicTypes.STRING)
                .addScalar("indOtherRsn", StandardBasicTypes.STRING)
                .addScalar("indCrimHistKinSafetyEval", StandardBasicTypes.STRING)
                .addScalar("indCrimHistAddendum", StandardBasicTypes.STRING)
                .addScalar("indAbuseNeglKinSafetyEval", StandardBasicTypes.STRING)
                .addScalar("indAbuseNeglAddendum", StandardBasicTypes.STRING)
                .setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(KinHomeAssessmentDetail.class)).uniqueResult();
    }

    /**
     *
     * Method Name: getKinPlacementInfoById Method Description: Retrieves placement child Information
     *  using ID for KinHomeAssessmentDetail as input.
     *
     * @param idKinHomeAssessmentDetail
     * @return PlacementAudit
     */
    @Override
    public List<PlacementAudit> getKinPlacementInfoById(Long idKinHomeAssessmentDetail) {


        return (List<PlacementAudit> )sessionFactory
                .getCurrentSession().createSQLQuery(placementInfoSql)
                .addScalar("idPlcmtChild", StandardBasicTypes.LONG)
                .addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
                .setParameter("idKinHomeAssessmentDetail", idKinHomeAssessmentDetail)
                .setResultTransformer(Transformers.aliasToBean(PlacementAudit.class)).list();
    }


    /**
     *
     * Method Name: getKinHomeCommentsById Method Description: Retrieves comments
     *  using ID for KinHomeAssessmentDetail as input.
     *
     * @param idKinHomeAssessmentDetail
     * @return KinHomeAssessmentDetailComments
     */
    @Override
    public List<KinHomeAssessmentDetailComments> getKinHomeCommentsById(Long idKinHomeAssessmentDetail) {


        return (List<KinHomeAssessmentDetailComments> )sessionFactory
                .getCurrentSession().createSQLQuery(kinCommentsSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("commentTypeCode", StandardBasicTypes.STRING)
                .addScalar("comments", StandardBasicTypes.STRING)
                .addScalar("idCreatedPerson", StandardBasicTypes.LONG)
                .setParameter("idKinHomeAssessmentDetail", idKinHomeAssessmentDetail)
                .setResultTransformer(Transformers.aliasToBean(KinHomeAssessmentDetailComments.class)).list();
    }



}
