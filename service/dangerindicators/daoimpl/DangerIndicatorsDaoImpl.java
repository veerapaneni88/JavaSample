package us.tx.state.dfps.service.dangerindicators.daoimpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.dangerindicators.dao.DangerIndicatorsDao;
import us.tx.state.dfps.service.dangerindicators.dto.DangerIndicatorsDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import org.hibernate.transform.Transformers;

@Repository
public class DangerIndicatorsDaoImpl implements DangerIndicatorsDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${DangerIndicatorsDaoImpl.getDangerIndicator}")
    private String getDangerIndicators;

    @Override
    public DangerIndicatorsDto getDangerIndicator(Long stageId) throws DataNotFoundException {

        Query query = sessionFactory.getCurrentSession().createSQLQuery(getDangerIndicators)
                .addScalar("caseId", StandardBasicTypes.LONG)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("indCgSerPhHarm", StandardBasicTypes.STRING)
                .addScalar("indCgSerPhHarmInj", StandardBasicTypes.STRING)
                .addScalar("indCgSerPhHarmThr", StandardBasicTypes.STRING)
                .addScalar("indCgSerPhHarmPhForce", StandardBasicTypes.STRING)
                .addScalar("indChSexAbSus", StandardBasicTypes.STRING)
                .addScalar("indChSexAbSusCg", StandardBasicTypes.STRING)
                .addScalar("indChSexAbSusOh", StandardBasicTypes.STRING)
                .addScalar("indChSexAbSusUnk", StandardBasicTypes.STRING)
                .addScalar("indCgAwPotHarm", StandardBasicTypes.STRING)
                .addScalar("indCgNoExpForInj", StandardBasicTypes.STRING)
                .addScalar("indCgDnMeetChNeedsFc", StandardBasicTypes.STRING)
                .addScalar("indCgDnMeetChNeedsMed", StandardBasicTypes.STRING)
                .addScalar("indBadLivConds", StandardBasicTypes.STRING)
                .addScalar("indCgSubAbCantSupCh", StandardBasicTypes.STRING)
                .addScalar("indDomVioDan", StandardBasicTypes.STRING)
                .addScalar("indCgDesChNeg", StandardBasicTypes.STRING)
                .addScalar("indCgDisCantSupCh", StandardBasicTypes.STRING)
                .addScalar("indCgRefAccChToInv", StandardBasicTypes.STRING)
                .addScalar("indCgPrMalTrtHist", StandardBasicTypes.STRING)
                .addScalar("indOtherDangers", StandardBasicTypes.STRING)
                .addScalar("txtOtherDangers", StandardBasicTypes.STRING)
                .addScalar("cdSftyDcsn", StandardBasicTypes.STRING)
                .addScalar("txtComments", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(DangerIndicatorsDto.class));
        query.setParameter(ServiceConstants.idStage, stageId);

        return (DangerIndicatorsDto) query.uniqueResult();
    }
}
