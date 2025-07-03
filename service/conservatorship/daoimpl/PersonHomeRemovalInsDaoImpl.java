package us.tx.state.dfps.service.conservatorship.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.conservatorship.dao.PersonHomeRemovalInsDao;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalInsInDto;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalInsOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Insert
 * PersonHomeRemoval table Aug 15, 2017- 5:15:55 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class PersonHomeRemovalInsDaoImpl implements PersonHomeRemovalInsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonHomeRemovalInsDaoImpl.savePersonHomeRemoval}")
	private transient String savePersonHomeRemoval;

	private static final Logger log = Logger.getLogger(PersonHomeRemovalInsDaoImpl.class);

	/**
	 *
	 * @param personHomeRemovalInsInDto
	 * @return Caud41doDto @
	 */
	@Override
	public PersonHomeRemovalInsOutDto personHomeRemovalAUD(PersonHomeRemovalInsInDto personHomeRemovalInsInDto) {
		log.debug("Entering method PersonHomeRemovalInsQUERYdam in PersonHomeRemovalInsDaoImpl");
		PersonHomeRemovalInsOutDto caud41doDto = new PersonHomeRemovalInsOutDto();
		switch (personHomeRemovalInsInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			savePersonHomeRemoval(personHomeRemovalInsInDto);
			break;
		}
		log.debug("Exiting method PersonHomeRemovalInsQUERYdam in PersonHomeRemovalInsDaoImpl");
		return caud41doDto;
	}

	/**
	 * Method Name: savePersonHomeRemoval Method Description: Insert
	 * PersonHomeRemoval table
	 * 
	 * @param personHomeRemovalInsInDto
	 */
	public void savePersonHomeRemoval(PersonHomeRemovalInsInDto personHomeRemovalInsInDto) {
		log.debug("Entering method PersonHomeRemovalInsQUERYdam in PersonHomeRemovalInsDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(savePersonHomeRemoval)
				.setParameter("hI_ulSysIdNewEvent", personHomeRemovalInsInDto.getSysIdNewEvent())
				.setParameter("hI_ulIdEvent", personHomeRemovalInsInDto.getIdEvent()));
		sQLQuery1.executeUpdate();
		log.debug("Exiting method PersonHomeRemovalInsQUERYdam in PersonHomeRemovalInsDaoImpl");
	}
}
