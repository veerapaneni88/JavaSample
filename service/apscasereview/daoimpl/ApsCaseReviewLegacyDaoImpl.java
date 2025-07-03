package us.tx.state.dfps.service.apscasereview.daoimpl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CareNarrative;
import us.tx.state.dfps.common.domain.OutcomeMatrixNarrative;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewContactDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewServiceAuthDto;
import us.tx.state.dfps.service.apscasereview.dao.ApsCaseReviewLegacyDao;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.NameDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * APS Case Review (Legacy)
 *
 * @author CHITLA
 * Feb, 2022© Texas Department of Family and Protective Services
 */

@Repository
public class ApsCaseReviewLegacyDaoImpl implements ApsCaseReviewLegacyDao {

    public static final String ID_CASE = "idCase";

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${ApsCaseReviewLegacyDaoImpl.getDonatedCommunityServicesInfo}")
    private String getDonatedCommunityServicesInfoSql;

    @Value("${ApsCaseReviewLegacyDaoImpl.getPersonContacted}")
    private String getPersonContactedSql;

    @Value("${ApsCaseReviewLegacyDaoImpl.getContactsByCase}")
    private String getContactsByCaseSql;

    @Value("${ApsCaseReviewLegacyDaoImpl.getContactInformation}")
    private String getContactInformationSql;

    @Value("${ApsCaseReviewLegacyDaoImpl.getPrimaryOrHistoricalPrimarySql}")
    private String getPrimaryOrHistoricalPrimarySql;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");


    @Override
    public List<Long> getCareEvents(Long idCase) {
        List<Long> careEvents = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CareNarrative.class);
        criteria.add(Restrictions.eq(ID_CASE, idCase));
        criteria.addOrder(Order.asc("dtLastUpdate"));
        List<CareNarrative> careNarratives = criteria.list();
        if (!TypeConvUtil.isNullOrEmpty(careNarratives)) {
            for (CareNarrative narrative : careNarratives) {
                careEvents.add(narrative.getIdEvent());
            }
        }
        return careEvents;
    }

    @Override
    public List<Long> getOutcomeMatrixEvents(Long idCase) {
        List<Long> outcomeMatrixEvents = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OutcomeMatrixNarrative.class);
        criteria.add(Restrictions.eq(ID_CASE, idCase));
        List<OutcomeMatrixNarrative> outcomeMatrixNarratives = criteria.list();
        if (!TypeConvUtil.isNullOrEmpty(outcomeMatrixNarratives)) {
            for (OutcomeMatrixNarrative narrative : outcomeMatrixNarratives) {
                outcomeMatrixEvents.add(narrative.getIdEvent());
            }
        }
        return outcomeMatrixEvents;
    }

    @Override
    public List<ApsCaseReviewServiceAuthDto> getDonatedCommunityServicesInfo(Long idCase) {
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getDonatedCommunityServicesInfoSql)
                .addScalar("serviceAuthService", StandardBasicTypes.STRING)
                .addScalar("serviceAuthDateEffective", StandardBasicTypes.DATE)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("serviceAuthAmount", StandardBasicTypes.DOUBLE)
                .addScalar("resourceName", StandardBasicTypes.STRING)
                .setParameter(ID_CASE, idCase)
                .setResultTransformer(Transformers.aliasToBean(ApsCaseReviewServiceAuthDto.class)));
        return sQLQuery1.list();
    }

    @Override
    public  List<NameDto> getPersonContacted(Long idCase, Long idEvent) {
            SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonContactedSql)
                .addScalar("nmNameFirst", StandardBasicTypes.STRING)
                .addScalar("nmNameMiddle", StandardBasicTypes.STRING)
                .addScalar("nmNameLast", StandardBasicTypes.STRING)
                .addScalar("cdNameSuffix", StandardBasicTypes.STRING)
                .setParameter(ID_CASE, idCase)
                .setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(NameDto.class)));
        return sQLQuery1.list();
    }


    @Override
    public  List<ApsCaseReviewContactDto> getContactsByCase(Long idCase) {
        SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContactsByCaseSql)
                .addScalar("cdContactType", StandardBasicTypes.STRING)
                .addScalar("dtEventOccurred", StandardBasicTypes.DATE)
                .addScalar("dtContactOccurred", StandardBasicTypes.DATE)
                .setParameter(ID_CASE, idCase)
                .setResultTransformer(Transformers.aliasToBean(ApsCaseReviewContactDto.class));
            return query.list();
    }

    @Override
    public  List<ApsCaseReviewContactDto> getContactInformation(Long idCase, Date dtSampleFrom, Date dtSampleTo) {
        SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContactInformationSql)
                .addScalar("idEvent", StandardBasicTypes.LONG)
                .addScalar("cdContactType", StandardBasicTypes.STRING)
                .addScalar("dtEventOccurred", StandardBasicTypes.DATE)
                .addScalar("dtContactOccurred", StandardBasicTypes.DATE)
                .addScalar("indAttempted", StandardBasicTypes.STRING)
                .addScalar("cdContactLocation", StandardBasicTypes.STRING)
                .setParameter(ID_CASE, idCase)
                .setParameter("dtSampleFrom", simpleDateFormat.format(dtSampleFrom))
                .setParameter("dtSampleTo",simpleDateFormat.format(dtSampleTo))
                .setResultTransformer(Transformers.aliasToBean(ApsCaseReviewContactDto.class));
         return  query.list();
    }

    @Override
    public Long getPrimaryOrHistoricalPrimary(Long idCase, String cdStagePersRole) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrimaryOrHistoricalPrimarySql)
                .setParameter(ID_CASE, idCase).setParameter("cdStagePersRole", cdStagePersRole);

        BigDecimal result = (BigDecimal) query.uniqueResult();
        return null!= result? Long.valueOf(result.longValue()):0L;
    }
}
