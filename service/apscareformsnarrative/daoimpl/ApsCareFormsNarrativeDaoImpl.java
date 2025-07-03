package us.tx.state.dfps.service.apscareformsnarrative.daoimpl;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.Care;
import us.tx.state.dfps.service.apscareformsnarrative.dao.ApsCareFormsNarrativeDao;
import us.tx.state.dfps.service.apscareformsnarrative.dto.ApsCareCategoryDto;
import us.tx.state.dfps.service.apscareformsnarrative.dto.ApsCareDomainDto;
import us.tx.state.dfps.service.apscareformsnarrative.dto.ApsCareFactorDto;
import us.tx.state.dfps.service.apscareformsnarrative.dto.ApsCareFormsDto;

import java.util.List;

@Repository
public class ApsCareFormsNarrativeDaoImpl implements ApsCareFormsNarrativeDao {

    @Value("${ApsCareFormsNarrativeDaoImpl.getCareDomaindata}")
    private String getCareDomaindata;

    @Value("${ApsCareFormsNarrativeDaoImpl.getCareCategoryData}")
    private String getCareCategoryData;

    @Value("${ApsCareFormsNarrativeDaoImpl.getCareFactorData}")
    private String getCareFactorData;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    MessageSource messageSource;

    private static final String ID_EVENT = "idEvent";
    private static final String CD_CAREDOMAIN = "cdCareDomain";

    /**
     * method to get Care data for idEvent Dam-CINVD9D
     * @param idEvent
     * @return
     */
    @Override
    public Care getApsCareData(Long idEvent) {
        Care care = null;
        care = (Care) sessionFactory.getCurrentSession().createCriteria(Care.class)
                .add(Restrictions.eq("event.idEvent",idEvent)).uniqueResult();
        return care;
    }

    /**
     * method to get Care Domain Data for idEvent CINVE1D
     * @param idEvent
     * @return
     */
    @Override
    public List<ApsCareDomainDto> getApsCareDomaindata(Long idEvent){
        List<ApsCareDomainDto> apsCareDomainDto = null;
        SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCareDomaindata)
                .addScalar(ID_EVENT,StandardBasicTypes.LONG)
                .addScalar("cdAllegationFocus",StandardBasicTypes.STRING)
                .addScalar(CD_CAREDOMAIN,StandardBasicTypes.STRING)
                .addScalar("cdDomain",StandardBasicTypes.STRING)
                .addScalar("txtDomain",StandardBasicTypes.STRING)
                .setParameter(ID_EVENT, idEvent).setResultTransformer(Transformers.aliasToBean(ApsCareDomainDto.class)));
        apsCareDomainDto = sQLQuery.list();
        return apsCareDomainDto;

    }
    /**
     * method to get Aps Care Category Data for idEvent CINVE2D
     * @param idEvent
     * @return
     */
    @Override
    public List<ApsCareCategoryDto> getApsCareCategoryData(Long idEvent){
        List<ApsCareCategoryDto> apsCareCategoryDto = null ;
        SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCareCategoryData)
                .addScalar(ID_EVENT,StandardBasicTypes.LONG)
                .addScalar("cdReasonBelieve",StandardBasicTypes.STRING)
                .addScalar("txtCategory",StandardBasicTypes.STRING)
                .addScalar(CD_CAREDOMAIN,StandardBasicTypes.STRING)
                .setParameter(ID_EVENT, idEvent).setResultTransformer(Transformers.aliasToBean(ApsCareCategoryDto.class)));
        apsCareCategoryDto = sQLQuery.list();
        return apsCareCategoryDto;
    }

    /**
     * method to get Aps Care Factor Data for idEvent CINVE3D
     * @param idEvent
     * @return
     */
    @Override
    public List<ApsCareFactorDto> getApsCareFactorData(Long idEvent){
        List<ApsCareFactorDto> apsCareFactorDto = null ;
        SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCareFactorData)
                .addScalar(ID_EVENT,StandardBasicTypes.LONG)
                .addScalar("cdCareFactorResponse",StandardBasicTypes.STRING)
                .addScalar("txtFactor",StandardBasicTypes.STRING)
                .addScalar(CD_CAREDOMAIN,StandardBasicTypes.STRING)
                .setParameter(ID_EVENT, idEvent)
                .setResultTransformer(Transformers.aliasToBean(ApsCareFactorDto.class)));
        apsCareFactorDto = sQLQuery.list();
        return apsCareFactorDto;
    }

}
