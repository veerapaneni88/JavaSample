package us.tx.state.dfps.service.apsIdentifiedProblemsSummary.serviceimpl;

import us.tx.state.dfps.common.domain.ApsClientFactors;
import us.tx.state.dfps.service.apsIdentifiedProblemsSummary.service.ApsIdentifiedProblemsSummaryService;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsIdentifiedProblemsSummaryDto;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsIdentifiedProblemsSummaryPrefillData;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApsIdentifiedProblemsSummaryServiceImpl implements ApsIdentifiedProblemsSummaryService {

    @Autowired
    PcaDao pcaDao;

    @Autowired
    ApsIdentifiedProblemsSummaryPrefillData apsIdentifiedProblemsSummaryPrefillData;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getIdentifiedProblemsSummaryDetails(ApsCommonReq apsCommonReq)
    {
        ApsIdentifiedProblemsSummaryDto apsIdentifiedProblemsSummaryDto = new ApsIdentifiedProblemsSummaryDto();

        StageCaseDtlDto stageCaseDtlDto = pcaDao.getStageAndCaseDtls(apsCommonReq.getIdStage());
        apsIdentifiedProblemsSummaryDto.setStageCaseDtlDto(stageCaseDtlDto);

        List<ApsClientFactors> apsClientFactors = getApsClientFactors(apsCommonReq.getIdEvent());
        apsIdentifiedProblemsSummaryDto.setApsClientFactors(apsClientFactors);

        apsIdentifiedProblemsSummaryDto.setIdEvent(apsCommonReq.getIdEvent());

        return apsIdentifiedProblemsSummaryPrefillData.returnPrefillData(apsIdentifiedProblemsSummaryDto);
    }


    private List<ApsClientFactors> getApsClientFactors(long idEvent) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ApsClientFactors.class);
        criteria.add(Restrictions.eq("idEvent", idEvent));
        return (List<ApsClientFactors>) criteria.list();
    }

}
