package us.tx.state.dfps.service.facilitystageinfo.daoimpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.dto.FacilityStageInfoDto;
import us.tx.state.dfps.service.facilitystageinfo.dao.FacilityStageInfoDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FacilityStageInfoDaoImpl will implemented all operation defined in
 * FacilityStageInfoDao Interface related FacilityStageInfo module.. Feb 9, 2018- 2:02:51
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class FacilityStageInfoDaoImpl implements FacilityStageInfoDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${FacilityStageInfoDaoImpl.getFacilityInfo}")
    private String getFacilityInfoSql;

    // CSEC16D
    /**
     * Method Name: getFacilityInfo Method Description: This method is used
     * to FacilityStageInfoDto
     * This dam will return the generic case information needed for all forms.
     *
     * @param idStage
     * @return FacilityStageInfoDto
     */
    @Override
    public FacilityStageInfoDto getFacilityInfo(Long idStage) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getFacilityInfoSql)
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("idCase", StandardBasicTypes.LONG)
                .addScalar("resourceName", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(FacilityStageInfoDto.class));
        return (FacilityStageInfoDto) query.uniqueResult();
    }
}
