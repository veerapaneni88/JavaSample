package us.tx.state.dfps.service.person.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.person.dao.FaIndivTrainingDao;
import us.tx.state.dfps.service.person.dto.FaIndivTrainLssInDto;
import us.tx.state.dfps.service.person.dto.FaIndivTrainLssOutDto;
import us.tx.state.dfps.service.person.dto.FaIndivTrainingRowOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> May 8, 2018- 6:34:26 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class FaIndivTrainingDaoImpl implements FaIndivTrainingDao {
    @Autowired
    MessageSource messageSource;

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${FaIndivTrainingDaoImpl.getFaIndivTraining}")
    String getFaIndivTraining;

    private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeDaoLog");

    /**
     * Method Name: getFaIndivTraining Method Description: get the detail of
     * FaIndivTraining clss57d
     *
     * @param faIndivTrainLssInDto
     * @param faIndivTrainLssOutDto
     */
    @SuppressWarnings("unchecked")
    @Override
    public void getFaIndivTraining(FaIndivTrainLssInDto faIndivTrainLssInDto,
                                   FaIndivTrainLssOutDto faIndivTrainLssOutDto) {
        log.debug("Entering method getFaIndivTraining in FaIndivTrainingDaoImpl");

        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFaIndivTraining)
                .addScalar("cdIndivTrnType")
				.addScalar("dtIndivTrn", StandardBasicTypes.DATE)
                .addScalar("idIndivTraining", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("indIndivTrnEc", StandardBasicTypes.STRING)
                .addScalar("indIndivTrnAktc", StandardBasicTypes.STRING)
				.addScalar("idNbrIndivTrnHrs", StandardBasicTypes.FLOAT)
				.addScalar("nbrIndivTrnSession", StandardBasicTypes.SHORT)
                .addScalar("txtIndivTrnTitle", StandardBasicTypes.STRING)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING)
                .setParameter("hI_ulIdPerson", faIndivTrainLssInDto.getIdPerson())
                .setResultTransformer(Transformers.aliasToBean(FaIndivTrainingRowOutDto.class)));

        List<FaIndivTrainingRowOutDto> liClss57doDto = new ArrayList<>();
        liClss57doDto = (List<FaIndivTrainingRowOutDto>) sQLQuery1.list();
        faIndivTrainLssOutDto.setTrainingRowOutDtos(liClss57doDto);
        log.debug("Exiting method getFaIndivTraining in FaIndivTrainingDaoImpl");
    }

}
