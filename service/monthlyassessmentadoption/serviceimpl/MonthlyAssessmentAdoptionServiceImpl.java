package us.tx.state.dfps.service.monthlyassessmentadoption.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.monthlyassessmentadoption.dto.MonthlyAssessmentAdoptionDto;
import us.tx.state.dfps.service.common.request.MonthlyAssessmentAdoptionReq;
import us.tx.state.dfps.service.monthlyassessmentadoption.dao.MonthlyAssessmentAdoptionDao;
import us.tx.state.dfps.service.monthlyassessmentadoption.service.MonthlyAssessmentAdoptionService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:MonthlyAssessmentAdoptionServiceImpl will implemented all operation defined in
 * MonthlyAssessmentAdoptionService Interface related MonthlyAssessmentAdoption module. Feb 9, 2018-
 * 2:01:28 PM © 2017 Texas Department of Family and Protective Services
 */

@Service
@Transactional
public class MonthlyAssessmentAdoptionServiceImpl implements MonthlyAssessmentAdoptionService {

    @Autowired
    private MonthlyAssessmentAdoptionDao monthlyAssessmentAdoptionDao;

    @Override
    public MonthlyAssessmentAdoptionDto getMonthlyAssessmentAdoptionData(MonthlyAssessmentAdoptionReq facilityStageInfoReq) {
        MonthlyAssessmentAdoptionDto monthlyAssessmentAdoptionDto = new MonthlyAssessmentAdoptionDto();


        return monthlyAssessmentAdoptionDto;
    }
}
