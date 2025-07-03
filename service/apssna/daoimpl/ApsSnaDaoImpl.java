package us.tx.state.dfps.service.apssna.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.ApsSna;
import us.tx.state.dfps.service.apssna.dao.ApsSnaDao;
import us.tx.state.dfps.service.apssna.dto.ApsSnaAnswerDto;
import us.tx.state.dfps.service.apssna.dto.ApsSnaFormResponseDTO;
import us.tx.state.dfps.service.apssna.dto.ApsSnaResponseDto;
import us.tx.state.dfps.service.apssna.dto.ApsStrengthsAndNeedsAssessmentDto;

import java.util.List;

/**
 * service-business- IMPACT APS MODERNIZATION Class
 * Description:ApsSnaDaoImpl Jan 04, 2022- 1:52:46 PM © 2022 Texas Department
 * of Family and Protective Services
 */

@Repository
public class ApsSnaDaoImpl implements ApsSnaDao {
    private static final Logger log = Logger.getLogger(ApsSnaDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${ApsSnaDaoImpl.getApsSnaEvents}")
    private String getApsSnaEventsSql;

    @Value("${ApsSnaDaoImpl.getApsSnaResponses}")
    private String getSnaResponsesSql;

    @Value("${ApsSnaDaoImpl.getAnswersbyDomainId}")
    private String getAnswersbyDomainId;

    @Value("${ApsSnaDaoImpl.getApsSNAResponsebyIdApsSna}")
    private String getApsSNAResponsebyIdApsSna;

    private String apsSNAAnswerLkpId = "apsSNAAnswerLkpId";
    private String apsSNADomainLkpId = "apsSNADomainLkpId";

    @Override
    public List<ApsStrengthsAndNeedsAssessmentDto> getSnaEvents(Long idCase) {
        log.debug("Entering method getSnaEvents in ApsSnaDaoImpl");
        List<ApsStrengthsAndNeedsAssessmentDto> apsSnaDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsSnaEventsSql)
                .addScalar("assessmentTypeCd", StandardBasicTypes.STRING)
                .addScalar("eventStatus", StandardBasicTypes.STRING)
                .addScalar("dateSNAComplete",StandardBasicTypes.DATE)
                .addScalar("eventId", StandardBasicTypes.LONG)
                .addScalar("stageId",StandardBasicTypes.LONG)
                .setParameter("idCase", idCase)
                .setResultTransformer(Transformers.aliasToBean(ApsStrengthsAndNeedsAssessmentDto.class)));
        apsSnaDtoList =sQLQuery1.list();

        return apsSnaDtoList;
    }

    @Override
    public List<ApsSnaResponseDto> getSnaResponses(Long idEvent, String code) {
        log.debug("Entering method getSnaResponses in ApsSnaDaoImpl");
        List<ApsSnaResponseDto> apsSnaResponseDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSnaResponsesSql)
                .addScalar("domainCode", StandardBasicTypes.STRING)
                .addScalar("answerText", StandardBasicTypes.STRING)
                .addScalar("domainText", StandardBasicTypes.STRING)
                .addScalar("apsSNADomainLkpId",StandardBasicTypes.LONG)
                .addScalar("apsSNAId",StandardBasicTypes.LONG)
                .addScalar("indIncludeServicePlan",StandardBasicTypes.BOOLEAN)
                .addScalar(apsSNAAnswerLkpId,StandardBasicTypes.LONG)
                .addScalar("description",StandardBasicTypes.STRING)
                .setParameter("codeValue", code)
                .setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(ApsSnaResponseDto.class)));
        apsSnaResponseDtoList =sQLQuery1.list();
        return apsSnaResponseDtoList;
    }

    /**
     * @param idEvent
     * @return
     */
    @Override
    public ApsSna getApsSna(Long idEvent){
        log.debug("Entering method getApsSna in ApsSnaDaoImpl");
        ApsSna apsSna = null;
        Criteria criteriaApsSna = sessionFactory.getCurrentSession().createCriteria(ApsSna.class);
        criteriaApsSna.add(Restrictions.eq("event.idEvent", idEvent));
        apsSna = (ApsSna)criteriaApsSna.uniqueResult();
        return apsSna;
    }

    /**
     * @param idApsSnaDomainLookup
     * @return
     */
    @Override
    public List<ApsSnaAnswerDto> getApsSnaAnswers(Long idApsSnaDomainLookup) {
        log.debug("Entering method getApsSnaAnswers in ApsSnaDaoImpl");
        List<ApsSnaAnswerDto> apsSnaAnswerLookupList = null;
        if(idApsSnaDomainLookup != null) {
            SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAnswersbyDomainId)
                    .addScalar(apsSNAAnswerLkpId, StandardBasicTypes.LONG)
                    .addScalar("answerText", StandardBasicTypes.STRING)
                    .addScalar("answerCode", StandardBasicTypes.STRING)
                    .addScalar("apsSNADomainLkpId",StandardBasicTypes.LONG)
                    .addScalar("orderId",StandardBasicTypes.LONG)
                    .addScalar("codeValue",StandardBasicTypes.STRING)
                    .setParameter("apsSNADomainLkpId",idApsSnaDomainLookup)
                    .setResultTransformer(Transformers.aliasToBean(ApsSnaAnswerDto.class)));
            apsSnaAnswerLookupList = sQLQuery1.list();
        }

        return apsSnaAnswerLookupList;
    }

    /**
     * @param idApsSna
     * @return
     */
    @Override
    public List<ApsSnaFormResponseDTO> getApsSnaResponseByIdEvent(Long idApsSna) {
        log.debug("Entering method getApsSnaResponseByIdEven in ApsSnaDaoImpl");
        List<ApsSnaFormResponseDTO> apsSnaResponses = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsSNAResponsebyIdApsSna)
                .addScalar("idApsna", StandardBasicTypes.LONG)
                .addScalar("apsSNADomainLkpId", StandardBasicTypes.LONG)
                .addScalar("apsSNAAnswerLkpId", StandardBasicTypes.LONG)
                .addScalar("txtOtherDescription", StandardBasicTypes.STRING)
                .addScalar("cdSNADomain",StandardBasicTypes.STRING)
                .addScalar("txtDomain",StandardBasicTypes.STRING)
                .setParameter("idApsna",idApsSna)
                .setResultTransformer(Transformers.aliasToBean(ApsSnaFormResponseDTO.class)));
            apsSnaResponses = sQLQuery1.list();

        return apsSnaResponses;
    }
}
