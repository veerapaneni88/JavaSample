package us.tx.state.dfps.service.kincaregiverhomeassmnt.daoimpl;

import org.springframework.util.ObjectUtils;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.kincaregiverhomedetails.dto.KinCareGiverAddressDto;
import us.tx.state.dfps.kincaregiverhomedetails.dto.KinCareGiverCaseInfoDto;
import us.tx.state.dfps.service.kincaregiverhomeassmnt.dao.KinCareGiverHomeAssmntDao;

/**
 * service-business - Kinship CareGiver Home Assessment Template (KIN12O00)
 * 07/19/2021 kurmav Artifact artf192718 : Prefill Service for KIN12O00
 */
@Repository
public class KinCareGiverHomeAssmntDaoImpl implements KinCareGiverHomeAssmntDao {

    @Autowired
    SessionFactory sessionFactory;

    @Value("${KinCareGiverHomeAssmntDaoImpl.getKinCareGiverAddressSql}")
    private String careGiverAddressSql;

    @Value("${KinCareGiverHomeAssmntDaoImpl.getKinCareGiverCaseInfoSql}")
    private String caseInfoSql;

    /**
     *
     * Method Name: getKinCareGiverCaseInfo Method Description: Retrieves Case Information (Case Name, Case Id)
     *  using ID STAGE as input. Dam Name :CSEC02D
     *
     * @param idStage
     * @return KinCareGiverCaseInfoDto
     */
    @Override
    public KinCareGiverCaseInfoDto getKinCareGiverCaseInfo(Long idStage) {

       return (KinCareGiverCaseInfoDto)sessionFactory
                .getCurrentSession().createSQLQuery(caseInfoSql)
                .addScalar("caseName", StandardBasicTypes.STRING)
                .addScalar("stageName", StandardBasicTypes.STRING)
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("idCase", StandardBasicTypes.LONG)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(KinCareGiverCaseInfoDto.class)).uniqueResult();
    }

    /**
     *
     * Method Name: getKinCareGiverAddress Method Description: Retrieves Care Giver Address Info
     * using idResource as input. Dam Name : CRES0AD
     *
     * @param idResource
     * @return KinCareGiverAddressDto
     */
    @Override
    public KinCareGiverAddressDto getKinCareGiverAddress(Long idResource) {

        return (KinCareGiverAddressDto) sessionFactory
                .getCurrentSession().createSQLQuery(careGiverAddressSql)
                .addScalar("addressLine1", StandardBasicTypes.STRING)
                .addScalar("addressLine2", StandardBasicTypes.STRING)
                .addScalar("city", StandardBasicTypes.STRING)
                .addScalar("state", StandardBasicTypes.STRING)
                .addScalar("county", StandardBasicTypes.STRING)
                .addScalar("zipCode", StandardBasicTypes.STRING)
                .addScalar("resourceName", StandardBasicTypes.STRING)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("idResourceAddress", StandardBasicTypes.LONG)
                .setParameter("idResource", idResource)
                .setResultTransformer(Transformers.aliasToBean(KinCareGiverAddressDto.class)).uniqueResult();
    }
}
