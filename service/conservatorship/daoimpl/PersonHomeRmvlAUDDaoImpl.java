package us.tx.state.dfps.service.conservatorship.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.conservatorship.dao.PersonHomeRmvlAUDDao;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemInputDto;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Simple
 * Standard AUD on table PERSON_HOME_REMOVAL Mar 1, 2018- 5:56:01 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class PersonHomeRmvlAUDDaoImpl implements PersonHomeRmvlAUDDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	// caud12d
	@Value("${PersonHomeRmvlAUDDaoImpl.personHomeRmvlAdd}")
	private transient String personHomeRmvlAdd;

	@Value("${PersonHomeRmvlAUDDaoImpl.personHomeRmvlUpdate}")
	private transient String personHomeRmvlUpdate;

	@Value("${PersonHomeRmvlAUDDaoImpl.personHomeRmvlDelete}")
	private transient String personHomeRmvlDelete;

	private static final Logger log = Logger.getLogger(PersonHomeRmvlAUDDaoImpl.class);

	/**
	 * Method Name: personHomeRemovalAUD Method Description:Update person in the
	 * Home in Removal page
	 * 
	 * @param personHomeRemInputDto
	 * @param personHomeRemOutDto
	 * @
	 */
	@Override
	public void personHomeRemovalAUD(PersonHomeRemInputDto personHomeRemInputDto,
			PersonHomeRemOutDto personHomeRemOutDto) {
		log.debug("Entering method personHomeRemovalAUD in PersonHomeRmvlAUDDaoImpl");
		switch (personHomeRemInputDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(personHomeRmvlAdd)
					.setParameter("hI_ulIdPersHmRemoval", personHomeRemInputDto.getIdPersHmRemoval())
					.setParameter("hI_tsLastUpdate", personHomeRemInputDto.getTsLastUpdate())
					.setParameter("hI_ulIdEvent", personHomeRemInputDto.getIdEvent()));
			sQLQuery1.executeUpdate();

			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(personHomeRmvlUpdate)
					.setParameter("hI_ulIdPersHmRemoval", personHomeRemInputDto.getIdPersHmRemoval())
					.setParameter("hI_tsLastUpdate", personHomeRemInputDto.getTsLastUpdate())
					.setParameter("hI_ulIdEvent", personHomeRemInputDto.getIdEvent()));
			sQLQuery2.executeUpdate();

			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(personHomeRmvlDelete)
					.setParameter("hI_ulIdPersHmRemoval", personHomeRemInputDto.getIdPersHmRemoval())
					.setParameter("hI_tsLastUpdate", personHomeRemInputDto.getTsLastUpdate())
					.setParameter("hI_ulIdEvent", personHomeRemInputDto.getIdEvent()));
			sQLQuery3.executeUpdate();

			break;
		}

		log.debug("Exiting method personHomeRemovalAUD in PersonHomeRmvlAUDDaoImpl");
	}

}
