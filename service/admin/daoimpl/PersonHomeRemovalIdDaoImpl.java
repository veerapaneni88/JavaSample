package us.tx.state.dfps.service.admin.daoimpl;

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

import us.tx.state.dfps.service.admin.dao.PersonHomeRemovalIdDao;
import us.tx.state.dfps.service.admin.dto.PersonHomeRemovalInDto;
import us.tx.state.dfps.service.admin.dto.PersonHomeRemovalOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * Retrieves a full row from the Person Home Removal table. Aug 5, 2017- 2:19:41
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class PersonHomeRemovalIdDaoImpl implements PersonHomeRemovalIdDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonHomeRemovalIdDaoImpl.getPersonHomeRemoval}")
	private String getPersonHomeRemoval;

	private static final Logger log = Logger.getLogger(PersonHomeRemovalIdDaoImpl.class);

	public PersonHomeRemovalIdDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getPersonHomeRemoval Method Description: This method will
	 * get data from PERSON_HOME_REMOVAL table. Cses46d
	 * 
	 * @param personHomeRemovalInDto
	 * @return List<PersonHomeRemovalOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonHomeRemovalOutDto> getPersonHomeRemoval(PersonHomeRemovalInDto personHomeRemovalInDto) {
		log.debug("Entering method getPersonHomeRemoval in PersonHomeRemovalIdDaoImpl");
		if (personHomeRemovalInDto.getIdCase() == null)
			personHomeRemovalInDto.setIdCase(0L);
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonHomeRemoval)
				.addScalar("idPersHmRemoval", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PersonHomeRemovalOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPersHmRemoval", personHomeRemovalInDto.getIdPersHmRemoval());
		sQLQuery1.setParameter("hI_ulIdCase", personHomeRemovalInDto.getIdCase());
		List<PersonHomeRemovalOutDto> personHomeRemovalOutDtos = (List<PersonHomeRemovalOutDto>) sQLQuery1.list();
		log.debug("Exiting method getPersonHomeRemoval in PersonHomeRemovalIdDaoImpl");
		return personHomeRemovalOutDtos;
	}
}
