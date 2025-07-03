package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.PersonEthnicityIdPersonDao;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityIdPersonInDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This dao
 * performs ADD/UPDATE/DELETE functionality on the PERSON_ETHNICITY table. Aug
 * 10, 2017- 12:49:52 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class PersonEthnicityIdPersonDaoImpl implements PersonEthnicityIdPersonDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(PersonEthnicityIdPersonDaoImpl.class);

	@Value("${PersonEthnicityIdPersonDaoImpl.deletePersonEthnicity}")
	private String deletePersonEthnicity;

	@Value("${PersonEthnicityIdPersonDaoImpl.insertPersonEthnicity}")
	private String insertPersonEthnicity;

	public PersonEthnicityIdPersonDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: deletePersonEthnicity Method Description: Delete the
	 * previous ethinicity record for the person,if exists and insert a new one.
	 * 
	 * @param personEthnicityIdPersonInDto
	 * @return int
	 */
	@Override
	public int deletePersonEthnicity(PersonEthnicityIdPersonInDto personEthnicityIdPersonInDto) {
		log.debug("Entering method caudd4dAUDdam in PersonEthnicityIdPersonDaoImpl");
		int rowCount1 = 0;
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deletePersonEthnicity)
				.setParameter("hI_ulIdPerson", personEthnicityIdPersonInDto.getIdPerson()));
		int rowCount = sQLQuery1.executeUpdate();
		/**
		 * If the delete was successful or the row was not found, then add the
		 * ethnicity
		 */
		if (rowCount > -1) {
			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertPersonEthnicity)
					.setParameter("hI_ulIdPerson", personEthnicityIdPersonInDto.getIdPerson())
					.setParameter("hI_szCdPersonEthnicity", personEthnicityIdPersonInDto.getCdPersonEthnicity()));
			rowCount1 = sQLQuery2.executeUpdate();
		}
		if (rowCount1 == 0) {
			throw new DataNotFoundException(messageSource
					.getMessage("PersonEthnicityIdPersonDaoImpl.new.ethinicity.record.not.inserted", null, Locale.US));
		}
		log.debug("Exiting method caudd4dAUDdam in PersonEthnicityIdPersonDaoImpl");
		return rowCount;
	}
}
