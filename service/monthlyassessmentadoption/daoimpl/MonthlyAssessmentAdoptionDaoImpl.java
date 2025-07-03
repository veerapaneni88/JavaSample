package us.tx.state.dfps.service.monthlyassessmentadoption.daoimpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.monthlyassessmentadoption.dto.MonthlyAssessmentAdoptionDto;
import us.tx.state.dfps.service.monthlyassessmentadoption.dao.MonthlyAssessmentAdoptionDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:MonthlyAssessmentAdoptionDaoImpl will implemented all operation defined in
 * MonthlyAssessmentAdoptionDao Interface related MonthlyAssessmentAdoption module.. Feb 9, 2018- 2:02:51
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class MonthlyAssessmentAdoptionDaoImpl implements MonthlyAssessmentAdoptionDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${MonthlyAssessmentAdoptionDaoImpl.getCAPSResource}")
    private String getCAPSResourceSql;

    // CSEC16D
    /**
     * Method Name: getMonthlyAssessmentAdoption Method Description: This method is used
     * to MonthlyAssessmentAdoptionDto
     * This dam will return the generic case information needed for all forms.
     *
     * @param idStage
     * @return MonthlyAssessmentAdoptionDto
     */
    @Override
    public MonthlyAssessmentAdoptionDto getMonthlyAssessmentAdoption(Long idStage) {
//        Query query = sessionFactory.getCurrentSession().createSQLQuery(getCAPSResourceSql)
//                .addScalar("idResource", StandardBasicTypes.LONG)
//                .setResultTransformer(Transformers.aliasToBean(MonthlyAssessmentAdoptionDto.class));
//        return (MonthlyAssessmentAdoptionDto) query.uniqueResult();
        return null;
    }
}
