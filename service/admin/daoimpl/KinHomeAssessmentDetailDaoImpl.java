package us.tx.state.dfps.service.admin.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.KinHomeAssessmentDetail;
import us.tx.state.dfps.service.admin.dao.KinHomeAssessmentDetailDao;

@Repository
public class KinHomeAssessmentDetailDaoImpl implements KinHomeAssessmentDetailDao {

    @Autowired
    MessageSource messageSource;

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${KinHomeAssessmentDetailDaoImpl.getKinHomeAssesmentDtl}")
    private String getKinHomeAssesmentDtlSql;

    private static final Logger log = Logger.getLogger(KinHomeAssessmentDetailDaoImpl.class);


    @Override
    public Long saveKinHomeAssessmentDetail(KinHomeAssessmentDetail kinHomeAssessmentDetail) {
        return (long)  sessionFactory.getCurrentSession().save(kinHomeAssessmentDetail);
    }

    @Override
    public Long getKinHomeAssesmentDtl(Long idSvcAuth) {
        Long idSvcAuthVal = (Long) sessionFactory.getCurrentSession().createSQLQuery(getKinHomeAssesmentDtlSql)
                .addScalar("idKinHomeAssmtDtl", StandardBasicTypes.LONG)
                .setParameter("idSvcAuth", idSvcAuth).uniqueResult();
        return idSvcAuthVal;
    }


}
