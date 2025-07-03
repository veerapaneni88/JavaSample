package us.tx.state.dfps.service.kincaregiverrsrcrequest.daoimpl;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.kincaregiverresourcerequest.dto.CaseWorkerAddressDto;
import us.tx.state.dfps.kincaregiverresourcerequest.dto.CaseWorkerNameDto;
import us.tx.state.dfps.kincaregiverresourcerequest.dto.CaseWorkerPhoneDto;
import us.tx.state.dfps.kincaregiverresourcerequest.dto.KinCareGiverContractInfoDto;
import us.tx.state.dfps.service.common.request.KinCareGiverResourceReq;
import us.tx.state.dfps.service.kincaregiverrsrcrequest.dao.KinCareGiverResourceRequestDao;

import java.util.List;

/**
 * Kinship Caregiver Resource/Contract Request Template (KIN10O00)
 * 07/21/2021 kurmav Artifact artf192721 : Prefill Service Dao for KIN10O00
 */
@Repository
public class KinCareGiverResourceRequestDaoImpl implements KinCareGiverResourceRequestDao {

    @Autowired
    SessionFactory sessionFactory;

    @Value("${KinCareGiverResourceRequestDaoImpl.getKinCareGiverContractInfoSql}")
    private String contractInfoSql;

    @Value("${KinCareGiverResourceRequestDaoImpl.getCaseWorkerNameSql}")
    private String caseworkerNameSql;

    @Value("${KinCareGiverResourceRequestDaoImpl.getCaseWorkerPhoneSql}")
    private String caseWorkerPhoneSql;

    @Value("${KinCareGiverResourceRequestDaoImpl.getCaseWorkerAddressSql}")
    private String caseWorkerAddressSql;

    /**
     *
     * Method Name: getKinCareGiverContractInfo Method Description: Retrieves Contract Info (Placement duration, address)
     *  using ID STAGE, ID Case, ID Resource as input. Dam Name :CSECE1D
     *
     * @param careGiverResourceReq
     * @return KinCareGiverContractInfoDto
     */
    @Override
    public KinCareGiverContractInfoDto getKinCareGiverContractInfo(KinCareGiverResourceReq careGiverResourceReq) {
        List<KinCareGiverContractInfoDto> contractInfoList= (List<KinCareGiverContractInfoDto>) sessionFactory.getCurrentSession()
                .createSQLQuery(contractInfoSql)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("firstName", StandardBasicTypes.STRING)
                .addScalar("middleName", StandardBasicTypes.STRING)
                .addScalar("lastName", StandardBasicTypes.STRING)
                .addScalar("streetLine1", StandardBasicTypes.STRING)
                .addScalar("streetLine2", StandardBasicTypes.STRING)
                .addScalar("city", StandardBasicTypes.STRING)
                .addScalar("state", StandardBasicTypes.STRING)
                .addScalar("zip", StandardBasicTypes.STRING)
                .addScalar("phone", StandardBasicTypes.STRING)
                .addScalar("phoneExtension", StandardBasicTypes.STRING)
                .addScalar("vendorId", StandardBasicTypes.LONG)
                .addScalar("placementStartDate", StandardBasicTypes.DATE)
                .addScalar("placementEndDate", StandardBasicTypes.DATE)
                .setParameter("idStage", careGiverResourceReq.getIdStage())
                .setParameter("idCase",careGiverResourceReq.getIdCase())
                .setParameter("idResource",careGiverResourceReq.getIdResource())
                .setResultTransformer(Transformers.aliasToBean(KinCareGiverContractInfoDto.class)).list();

        if(contractInfoList != null && !contractInfoList.isEmpty()){
            return contractInfoList.get(0);
        }
        return new KinCareGiverContractInfoDto();
    }

    /**
     *
     * Method Name: getCaseWorkerName Method Description: Retrieves Primary Case Worker Name
     *  using ID STAGE as input. Dam Name :CCMN19D
     *
     * @param idStage
     * @return CaseWorkerNameDto
     */
    @Override
    public CaseWorkerNameDto getCaseWorkerName(Long idStage) {
        return (CaseWorkerNameDto)sessionFactory
                .getCurrentSession().createSQLQuery(caseworkerNameSql)
                .addScalar("fullName", StandardBasicTypes.STRING)
                .addScalar("stageName", StandardBasicTypes.STRING)
                .addScalar("idPerson", StandardBasicTypes.LONG)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(CaseWorkerNameDto.class)).uniqueResult();
    }

    /**
     *
     * Method Name: getCaseWorkerPhone Method Description: Retrieves phone type and phone
     * from the PERSON_PHONE table. Dam Name :CLSS0DD
     *
     * @param idPerson
     * @return CaseWorkerPhoneDto
     */
    @Override
    public CaseWorkerPhoneDto getCaseWorkerPhone(Long idPerson) {
        return (CaseWorkerPhoneDto)sessionFactory
                .getCurrentSession().createSQLQuery(caseWorkerPhoneSql)
                .addScalar("phoneNumber", StandardBasicTypes.STRING)
                .addScalar("phoneExtension", StandardBasicTypes.STRING)
                .addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("idPhone", StandardBasicTypes.LONG)
                .addScalar("phoneType", StandardBasicTypes.STRING)
                .addScalar("primaryPhone", StandardBasicTypes.STRING)
                .setParameter("idPerson", idPerson)
                .setResultTransformer(Transformers.aliasToBean(CaseWorkerPhoneDto.class)).uniqueResult();
    }

    /**
     *
     * Method Name: getCaseWorkerAddress Method Description: Retrieves active primary address, phone number, and name
     *  using idPerson as input. Dam Name :CSEC01D
     *
     * @param idPerson
     * @return CaseWorkerAddressDto
     */
    @Override
    public CaseWorkerAddressDto getCaseWorkerAddress(Long idPerson) {

        return (CaseWorkerAddressDto)sessionFactory
                .getCurrentSession().createSQLQuery(caseWorkerAddressSql)
                .addScalar("streetLine1", StandardBasicTypes.STRING)
                .addScalar("streetLine2", StandardBasicTypes.STRING)
                .addScalar("city", StandardBasicTypes.STRING)
                .addScalar("zip", StandardBasicTypes.STRING)
                .addScalar("county", StandardBasicTypes.STRING)
                .addScalar("mailCode", StandardBasicTypes.STRING)
                .addScalar("phoneNumber", StandardBasicTypes.STRING)
                .addScalar("phoneExtension", StandardBasicTypes.STRING)
                .setParameter("idPerson", idPerson)
                .setResultTransformer(Transformers.aliasToBean(CaseWorkerAddressDto.class)).uniqueResult();
    }
}
