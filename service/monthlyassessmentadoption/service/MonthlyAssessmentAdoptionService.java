package us.tx.state.dfps.service.monthlyassessmentadoption.service;

import us.tx.state.dfps.monthlyassessmentadoption.dto.MonthlyAssessmentAdoptionDto;
import us.tx.state.dfps.service.common.request.MonthlyAssessmentAdoptionReq;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * MonthlyAssessmentAdoptionService will have all operation which are mapped to MonthlyAssessmentAdoption
 * module. Feb 9, 2018- 2:01:02 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface MonthlyAssessmentAdoptionService {

    public MonthlyAssessmentAdoptionDto getMonthlyAssessmentAdoptionData(MonthlyAssessmentAdoptionReq monthlyAssessmentAdoptionReq);
}
