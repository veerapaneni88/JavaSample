package us.tx.state.dfps.service.person.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.person.dao.FaIndivTrainingCountDao;
import us.tx.state.dfps.service.person.dto.FaIndivCountMscInDto;
import us.tx.state.dfps.service.person.dto.FaIndivCountMscOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> May 8, 2018- 6:34:33 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class FaIndivTrainingCountDaoImpl implements FaIndivTrainingCountDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FaIndivTrainingCountDaoImpl.getFaIndivTrainingCount}")
	String getFaIndivTrainingCount;

	private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeDaoLog");

	/**
	 * Method Name: getFaIndivTrainingCount Method Description: get the count of
	 * FaIndivTraining cmsc34d
	 * 
	 * @param faIndivCountMscInDto
	 * @param faIndivCountMscOutDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void getFaIndivTrainingCount(FaIndivCountMscInDto faIndivCountMscInDto,
			FaIndivCountMscOutDto faIndivCountMscOutDto) {
		log.debug("Entering method getFaIndivTrainingCount in FaIndivTrainingCountDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFaIndivTrainingCount)
				.addScalar("ulSysNbrGenericCntr").setParameter("hI_ulIdPerson", faIndivCountMscInDto.getIdPerson())
				.setParameter("hI_szCdIndivTrnType", faIndivCountMscInDto.getCdIndivTrnType())
				.setResultTransformer(Transformers.aliasToBean(FaIndivCountMscOutDto.class)));

		List<FaIndivCountMscOutDto> liCmsc34doDto = new ArrayList<>();
		liCmsc34doDto = (List<FaIndivCountMscOutDto>) sQLQuery1.list();

		log.debug("Exiting method getFaIndivTrainingCount in FaIndivTrainingCountDaoImpl");
	}

}
