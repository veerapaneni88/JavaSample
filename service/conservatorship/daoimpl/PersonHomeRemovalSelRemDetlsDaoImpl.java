package us.tx.state.dfps.service.conservatorship.daoimpl;

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

import us.tx.state.dfps.service.conservatorship.dao.PersonHomeRemovalSelRemDetlsDao;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalSelRemDetlsInDto;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalSelRemDetlsOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Implementation for PersonHomeRemovalSelRemDetlsDaoImpl Aug 2,
 * 2017- 8:22:49 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class PersonHomeRemovalSelRemDetlsDaoImpl implements PersonHomeRemovalSelRemDetlsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonHomeRemovalSelRemDetlsDaoImpl.getPersonHomeRemovalDetails}")
	private transient String getPersonHomeRemovalDetails;

	private static final Logger log = Logger.getLogger(PersonHomeRemovalSelRemDetlsDaoImpl.class);

	/**
	 * 
	 * Method Name: getPersonHomeRemovalDetails Method Description: This method
	 * will get data from PERSON_HOME_REMOVAL table.
	 * 
	 * @param personHomeRemovalSelRemDetlsInDto
	 * @return List<PersonHomeRemovalSelRemDetlsOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonHomeRemovalSelRemDetlsOutDto> getPersonHomeRemovalDetails(
			PersonHomeRemovalSelRemDetlsInDto personHomeRemovalSelRemDetlsInDto) {
		log.debug("Entering method getPersonHomeRemovalDetails in PersonHomeRemovalSelRemDetlsDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonHomeRemovalDetails)
				.addScalar("idPersHmRemoval", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameter("idEvent", personHomeRemovalSelRemDetlsInDto.getIdEvent())
				.setResultTransformer(Transformers.aliasToBean(PersonHomeRemovalSelRemDetlsOutDto.class)));

		List<PersonHomeRemovalSelRemDetlsOutDto> personHomeRemovalSelRemDetlsOutDtos = (List<PersonHomeRemovalSelRemDetlsOutDto>) sQLQuery1
				.list();
		log.debug("Exiting method getPersonHomeRemovalDetails in PersonHomeRemovalSelRemDetlsDaoImpl");
		return personHomeRemovalSelRemDetlsOutDtos;
	}
}
