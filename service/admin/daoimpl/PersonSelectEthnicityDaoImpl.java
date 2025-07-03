package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.PersonSelectEthnicityDao;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityInDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * Retrieves Person Ethinicity Information on the Person Aug 5, 2017- 11:49:30
 * AM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class PersonSelectEthnicityDaoImpl implements PersonSelectEthnicityDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonSelectEthnicityDaoImpl.getPersonEthnicity}")
	private String getPersonEthnicity;

	private static final Logger log = Logger.getLogger(PersonSelectEthnicityDaoImpl.class);

	public PersonSelectEthnicityDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getPersonEthnicity Method Description: This method will get
	 * data from PERSON_ETHNICITY table. Clss80d
	 * 
	 * @param personEthnicityInDto
	 * @return List<PersonEthnicityOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonEthnicityOutDto> getPersonEthnicity(PersonEthnicityInDto personEthnicityInDto) {
		log.debug("Entering method getPersonEthnicity in PersonSelectEthnicityDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonEthnicity)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicity", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PersonEthnicityOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPerson", personEthnicityInDto.getIdPerson());
		List<PersonEthnicityOutDto> personEthnicityOutDtos = (List<PersonEthnicityOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(personEthnicityOutDtos) && personEthnicityOutDtos.size() == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clss80dDaoImpl.PersonEthnicity.not.found", null, Locale.US));
		}
		log.debug("Exiting method getPersonEthnicity in PersonSelectEthnicityDaoImpl");
		return personEthnicityOutDtos;
	}
}
