package us.tx.state.dfps.service.monthlyassessmentadoption.dao;


import us.tx.state.dfps.monthlyassessmentadoption.dto.MonthlyAssessmentAdoptionDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:MonthlyAssessmentAdoptionDao will implemented all operation defined in
 * MonthlyAssessmentAdoptionService Interface related MonthlyAssessmentAdoption module. Feb 9, 2018-
 * 2:02:21 PM © 2017 Texas Department of Family and Protective Services
 */
public interface MonthlyAssessmentAdoptionDao {

    /**
     * Method Name: getMonthlyAssessmentAdoption Method Description:This method returns
     * MonthlyAssessmentAdoptionDto
     *
     * @param idStage
     * @return MonthlyAssessmentAdoptionDto
     */
    public MonthlyAssessmentAdoptionDto getMonthlyAssessmentAdoption(Long idStage);
}
