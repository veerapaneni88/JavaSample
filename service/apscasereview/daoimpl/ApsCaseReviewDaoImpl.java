package us.tx.state.dfps.service.apscasereview.daoimpl;

import org.hibernate.Query;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewContactNamesDto;
import us.tx.state.dfps.service.apscasereview.ApsStagePersonDto;
import us.tx.state.dfps.service.apscasereview.dao.ApsCaseReviewDao;


import java.math.BigDecimal;
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
 * Description:ApsCaseReviewDao Jan 21, 2022- 1:52:46 PM © 2022 Texas Department
 * of Family and Protective Services
 */

@Repository
public class ApsCaseReviewDaoImpl implements ApsCaseReviewDao {

    private static final Logger log = Logger.getLogger(ApsCaseReviewDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${ApsCaseReviewDaoImpl.getPersonsContactedByCase}")
    private String getPersonsContactedByCaseSql;

    @Value("${ApsCaseReviewDaoImpl.getPersonStageInfo}")
    private String getPersonStageInfoSql;

    @Value("${ApsCaseReviewDaoImpl.getWorkerOrSupervisorByCaseId}")
    private String getWorkerOrSupervisorByCaseIdSql;



    @Override
    public List<ApsCaseReviewContactNamesDto> getPersonsContactedByCase(Long idCase) {
            log.debug("Entering method getPersonsContactedByCase in ApsCaseReviewDaoImpl");
            List<ApsCaseReviewContactNamesDto> apsSafetyAssessmentContactDtoList = null;
            SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonsContactedByCaseSql)
                    .addScalar("eventId", StandardBasicTypes.LONG)
                    .addScalar("fullName",StandardBasicTypes.STRING)
                    .addScalar("type",StandardBasicTypes.STRING)
                    .addScalar("suffix",StandardBasicTypes.STRING)
                    .setParameter("idCase", idCase)
                    .setResultTransformer(Transformers.aliasToBean(ApsCaseReviewContactNamesDto.class)));
        apsSafetyAssessmentContactDtoList = sQLQuery1.list();
            return apsSafetyAssessmentContactDtoList;
        }

    @Override
    public ApsStagePersonDto getPersonStageInfo(Long stagePersonLinkId, Long personId) {
        log.debug("Entering method getPersonStageInfo in ApsCaseReviewDaoImpl");
        ApsStagePersonDto apsStagePersonDto;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonStageInfoSql)
                .addScalar("szCdStagePersRole", StandardBasicTypes.STRING)
                .addScalar("szTxtStagePersNotes",StandardBasicTypes.STRING)
                .addScalar("szCdStagePersRelInt",StandardBasicTypes.STRING)
                .addScalar("cdPersonSex",StandardBasicTypes.STRING)
                .addScalar("nbrPersonAge",StandardBasicTypes.INTEGER)
                .addScalar("dtDtPersonDeath",StandardBasicTypes.DATE)
                .addScalar("dtDtPersonBirth",StandardBasicTypes.DATE)
                .addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
                .addScalar("szCdPersonDeath",StandardBasicTypes.STRING)
                .addScalar("szCdPersonMaritalStatus",StandardBasicTypes.STRING)
                .addScalar("szCdPersonLanguage",StandardBasicTypes.STRING)
                .addScalar("szCdPersonEthnicGroup", StandardBasicTypes.STRING)
                .addScalar("szNbrPersonIdNumber",StandardBasicTypes.STRING)
                .addScalar("szCdPersAddrLinkType",StandardBasicTypes.STRING)
                .addScalar("szTxtPersAddrCmnts",StandardBasicTypes.STRING)
                .addScalar("addrZip", StandardBasicTypes.STRING)
                .addScalar("szCdAddrState",StandardBasicTypes.STRING)
                .addScalar("szAddrCity",StandardBasicTypes.STRING)
                .addScalar("szAddrPersAddrStLn1",StandardBasicTypes.STRING)
                .addScalar("szAddrPersAddrStLn2", StandardBasicTypes.STRING)
                .addScalar("szCdAddrCounty",StandardBasicTypes.STRING)
                .addScalar("szTxtPhoneComments",StandardBasicTypes.STRING)
                .addScalar("nbrPhoneExtension",StandardBasicTypes.STRING)
                .addScalar("szCdPhoneType",StandardBasicTypes.STRING)
                .addScalar("szNmNameFirst",StandardBasicTypes.STRING)
                .addScalar("szNmNameMiddle",StandardBasicTypes.STRING)
                .addScalar("szNmNameLast", StandardBasicTypes.STRING)
                .addScalar("szCdNameSuffix",StandardBasicTypes.STRING)
                .addScalar("nbrPhone",StandardBasicTypes.STRING)
                .addScalar("szAddrPersAddrAttn",StandardBasicTypes.STRING)
                .addScalar("indStagePersReporter",StandardBasicTypes.STRING)
                .setParameter("idStagePersonLink", stagePersonLinkId)
                .setParameter("idPerson", personId)
                .setResultTransformer(Transformers.aliasToBean(ApsStagePersonDto.class)));
        apsStagePersonDto =  (ApsStagePersonDto)  sQLQuery1.uniqueResult();
        return apsStagePersonDto;
    }

    /**
     * Method helps to query and get the selected cases primary worker id
     *
     * @param idCase - selected case
     * @param stagePersonRole - stage person role
     * @return primary worker id
     */
    @Override
    public Long getPrimaryWorkerOrSupervisorByCaseId(Long idCase, String stagePersonRole) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getWorkerOrSupervisorByCaseIdSql)
                .setParameter("idCase", idCase)
                .setParameter("cdStagePersRole", stagePersonRole);
        BigDecimal result = (BigDecimal) query.uniqueResult();

        return ObjectUtils.isEmpty(result) ? null : result.longValue();
    }

}
