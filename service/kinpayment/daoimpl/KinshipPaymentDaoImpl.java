package us.tx.state.dfps.service.kinpayment.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.KinMonthlyExtPayments;
import us.tx.state.dfps.service.kin.dto.KinMonthlyExtPaymentDto;
import us.tx.state.dfps.service.kin.dto.KinPaymentApplicationRes;
import us.tx.state.dfps.service.kin.dto.KinPaymentCareGiverDto;
import us.tx.state.dfps.service.kin.dto.KinPaymentChildDto;
import us.tx.state.dfps.service.kinpayment.dao.KinshipPaymentDao;

@Repository
public class KinshipPaymentDaoImpl implements KinshipPaymentDao {

    @Autowired
    SessionFactory sessionFactory;

    @Value("${KinshipPaymentDaoImpl.getKinCareGiverDetailsSql}")
    private String getKinCareGiverDetailsSql;

    @Value("${KinshipPaymentDaoImpl.getKinChildDetailsSql}")
    private String getKinChildDetailsSql;

    @Value("${KinshipPaymentDaoImpl.getKinLegalOutcomeDateSql}")
    private String getKinLegalOutcomeDateSql;

    @Value("${KinshipPaymentDaoImpl.getMonthlyExtensionSql}")
    private String getMonthlyExtensionSql;

    @Value("${KinshipPaymentDaoImpl.updateKinMonthlyPayments}")
    private String updateKinMonthlyPaymentsSql;

    @Override
    public KinPaymentCareGiverDto getKinCareGiverDetailsSql(Long idStage, Long idResource) {
        return (KinPaymentCareGiverDto) sessionFactory
                .getCurrentSession().createSQLQuery(getKinCareGiverDetailsSql)
                .addScalar("firstName", StandardBasicTypes.STRING)
                .addScalar("lastName", StandardBasicTypes.STRING)
                .addScalar("middleName", StandardBasicTypes.STRING)
                .addScalar("SSN", StandardBasicTypes.STRING)
                .addScalar("addrLn1", StandardBasicTypes.STRING)
                .addScalar("city", StandardBasicTypes.STRING)
                .addScalar("state", StandardBasicTypes.STRING)
                .addScalar("zipCode", StandardBasicTypes.STRING)
                .addScalar("phone", StandardBasicTypes.STRING)
                .addScalar("relationshipToChild", StandardBasicTypes.STRING)
                .addScalar("noOfPersons", StandardBasicTypes.LONG)
                .addScalar("annualIncome", StandardBasicTypes.LONG)
                .setParameter("idStage", idStage)
                .setParameter("idResource", idResource)
                .setResultTransformer(Transformers.aliasToBean(KinPaymentCareGiverDto.class)).uniqueResult();
    }

    @Override
    public KinPaymentChildDto getKinChildDetailsSql(Long idResource, Long idPerson) {
        return (KinPaymentChildDto) sessionFactory
                .getCurrentSession().createSQLQuery(getKinChildDetailsSql)
                .addScalar("firstName", StandardBasicTypes.STRING)
                .addScalar("lastName", StandardBasicTypes.STRING)
                .addScalar("dateOfBirth", StandardBasicTypes.DATE)
                .addScalar("placementStartDate", StandardBasicTypes.DATE)
                .setParameter("idResource", idResource)
                .setParameter("idPerson", idPerson)
                .setResultTransformer(Transformers.aliasToBean(KinPaymentChildDto.class)).uniqueResult();
    }

    @Override
    public Date getKinLegalOutcomeDateSql(Long idCase, Long idPerson) {
        KinPaymentApplicationRes kinPaymentApplicationRes = (KinPaymentApplicationRes) sessionFactory
                .getCurrentSession().createSQLQuery(getKinLegalOutcomeDateSql)
                .addScalar("kinChildLegalOutcomeDate", StandardBasicTypes.DATE)
                .setParameter("idCase", idCase)
                .setParameter("idPerson", idPerson)
                .setResultTransformer(Transformers.aliasToBean(KinPaymentApplicationRes.class)).uniqueResult();
        return kinPaymentApplicationRes.getKinChildLegalOutcomeDate();
    }

    @Override
    public List<KinMonthlyExtPaymentDto> getMonthlyExtensionSql(Long eventId) {
        SQLQuery sqlQuery = ((SQLQuery) sessionFactory
                .getCurrentSession().createSQLQuery(getMonthlyExtensionSql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("childId", StandardBasicTypes.LONG)
                .addScalar("startDate", StandardBasicTypes.DATE)
                .addScalar("endDate", StandardBasicTypes.DATE)
                .addScalar("goodCause", StandardBasicTypes.STRING)
                .addScalar("comments", StandardBasicTypes.STRING)
                .setParameter("idEvent", eventId)
                .setResultTransformer(Transformers.aliasToBean(KinMonthlyExtPaymentDto.class)));
        List<KinMonthlyExtPaymentDto> kinMonthlyExtPayments = sqlQuery.list();
        return kinMonthlyExtPayments;
    }

    @Override
    public void updateMonthlyPayment(Long svcAuthDtlId, Long eventId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(KinMonthlyExtPayments.class);
        criteria.add(Restrictions.eq("eventId", eventId));
        KinMonthlyExtPayments kinMonthlyExtPayments = (KinMonthlyExtPayments) criteria.uniqueResult();
        kinMonthlyExtPayments.setServiceAuthDetailId(svcAuthDtlId);
        session.saveOrUpdate(kinMonthlyExtPayments);
    }

}
