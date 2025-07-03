package us.tx.state.dfps.service.waivervariance.daoimpl;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.waivervariance.dao.WaiverVarianceDao;
import us.tx.state.dfps.web.waivervariance.dto.WaiverVarianceBean;

@Repository
public class WaiverVarianceDaoImpl implements WaiverVarianceDao {

    public static final String NEW = "1";
    public static final String NEW_USING = "2";
    @Autowired
    MessageSource messageSource;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private EventDao eventDao;
    @Autowired
    private CapsResourceDao capsResourceDao;
    @Autowired
    @Value("${WaiverVarianceDaoImpl.getWaiverVarianceByEventId}")
    private String getWaiverVarianceByEventId;

    @Override
    public WaiverVarianceBean getWaiverVarianceDetails(Long idEvent, Long idStage, String pageMode) {
        WaiverVarianceBean waiverVarianceBean = new WaiverVarianceBean();

        if (NEW_USING.equalsIgnoreCase(pageMode)) {
            waiverVarianceBean = getWaiverVarianceBean(idEvent, waiverVarianceBean);
            waiverVarianceBean.setIdEvent(0L);
            waiverVarianceBean.setIdWaiverVariance(0L);
        } else if (idEvent != 0) {
            waiverVarianceBean = getWaiverVarianceBean(idEvent, waiverVarianceBean);
            waiverVarianceBean.setEventStatus(eventDao.getEventStatus(idEvent));
        } else if (idEvent == 0) {
            waiverVarianceBean.setIdEvent(0L);
        }

        waiverVarianceBean.setFadHomeStatus(capsResourceDao.getFaHomeStatusByStageId(idStage));

        return waiverVarianceBean;
    }

    private WaiverVarianceBean getWaiverVarianceBean(Long idEvent, WaiverVarianceBean waiverVarianceBean) {

        SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getWaiverVarianceByEventId)
                .setResultTransformer(Transformers.aliasToBean(WaiverVarianceBean.class));
        sqlQuery.addScalar("idWaiverVariance", StandardBasicTypes.LONG);
        sqlQuery.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
        sqlQuery.addScalar("idEvent", StandardBasicTypes.LONG);
        sqlQuery.addScalar("idStage", StandardBasicTypes.LONG);
        sqlQuery.addScalar("idResource", StandardBasicTypes.LONG);
        sqlQuery.addScalar("cdRequest", StandardBasicTypes.STRING);
        sqlQuery.addScalar("indRelative", StandardBasicTypes.STRING);
        sqlQuery.addScalar("indFictiveKin", StandardBasicTypes.STRING);
        sqlQuery.addScalar("indUnrelated", StandardBasicTypes.STRING);
        sqlQuery.addScalar("txtRqstDesc1", StandardBasicTypes.STRING);
        sqlQuery.addScalar("txtRqstDesc2", StandardBasicTypes.STRING);
        sqlQuery.addScalar("txtRqstDesc3", StandardBasicTypes.STRING);
        sqlQuery.addScalar("txtRqstDesc4", StandardBasicTypes.STRING);
        sqlQuery.addScalar("txtRqstDesc5", StandardBasicTypes.STRING);
        sqlQuery.addScalar("txtRqstDesc6", StandardBasicTypes.STRING);
        sqlQuery.addScalar("txtRqstDesc7", StandardBasicTypes.STRING);
        sqlQuery.addScalar("nbrChldrnInCare", StandardBasicTypes.LONG);
        sqlQuery.addScalar("txtAges", StandardBasicTypes.STRING);
        sqlQuery.addScalar("indEmail", StandardBasicTypes.STRING);
        sqlQuery.addScalar("indMail", StandardBasicTypes.STRING);
        sqlQuery.addScalar("indFax", StandardBasicTypes.STRING);
        sqlQuery.addScalar("indHandDlvrd", StandardBasicTypes.STRING);
        sqlQuery.addScalar("idStndrd", StandardBasicTypes.LONG);
        sqlQuery.addScalar("dtOrgnlRcv", StandardBasicTypes.DATE);
        sqlQuery.addScalar("dtRqstUntil", StandardBasicTypes.DATE);
        sqlQuery.addScalar("dtEffctv", StandardBasicTypes.DATE);
        sqlQuery.addScalar("dtExprtn", StandardBasicTypes.DATE);
        sqlQuery.addScalar("cdStatus", StandardBasicTypes.STRING);
        sqlQuery.addScalar("cdDenialRsn", StandardBasicTypes.STRING);
        sqlQuery.addScalar("cdAdminRvwStts", StandardBasicTypes.STRING);
        sqlQuery.addScalar("dtAdminRvwRqstd", StandardBasicTypes.DATE);
        sqlQuery.addScalar("dtAdminRvwDcsn", StandardBasicTypes.DATE);
        sqlQuery.addScalar("dtAdminRvwRsltSent", StandardBasicTypes.DATE);
        sqlQuery.addScalar("txtCondition1", StandardBasicTypes.STRING);
        sqlQuery.addScalar("txtCondition2", StandardBasicTypes.STRING);
        sqlQuery.addScalar("txtCondition3", StandardBasicTypes.STRING);
        sqlQuery.addScalar("cdResult", StandardBasicTypes.STRING);
        sqlQuery.addScalar("dtOutcome", StandardBasicTypes.DATE);
        sqlQuery.addScalar("indHomeVrfd", StandardBasicTypes.STRING);
        sqlQuery.addScalar("idCreatedPerson", StandardBasicTypes.LONG);
        sqlQuery.addScalar("dtCreated", StandardBasicTypes.DATE);
        sqlQuery.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG);
        sqlQuery.setParameter("idEvent", idEvent);
        waiverVarianceBean = (WaiverVarianceBean) sqlQuery.uniqueResult();
        return waiverVarianceBean;
    }
}
