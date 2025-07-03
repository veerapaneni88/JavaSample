package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonRace;
import us.tx.state.dfps.service.admin.dao.PersonRaceInsUpdDelDao;
import us.tx.state.dfps.service.admin.dto.PersonRaceInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceInsUpdDelOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This dao
 * performs ADD/UPDATE/DELETE functionality on the PERSON_ETHNICITY table. Aug
 * 10, 2017- 2:08:40 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class PersonRaceInsUpdDelDaoImpl implements PersonRaceInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(PersonRaceInsUpdDelDaoImpl.class);

	public PersonRaceInsUpdDelDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: updatePersonRaceRecord Method Description: update the Person
	 * Race record Caudd5d
	 * 
	 * @param personRaceInsUpdDelInDto
	 * @return PersonRaceInsUpdDelOutDto
	 */
	@Override
	public PersonRaceInsUpdDelOutDto updatePersonRaceRecord(PersonRaceInsUpdDelInDto personRaceInsUpdDelInDto) {
		log.debug("Entering method updatePersonRaceRecord in PersonRaceInsUpdDelDaoImpl");
		PersonRaceInsUpdDelOutDto objCaudd5doDto = new PersonRaceInsUpdDelOutDto();
		switch (personRaceInsUpdDelInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			savePersonRace(personRaceInsUpdDelInDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			deletePersonRace(personRaceInsUpdDelInDto);
			break;
		}
		objCaudd5doDto.setTotalRecCount((long) 0);
		log.debug("Exiting method updatePersonRaceRecord in PersonRaceInsUpdDelDaoImpl");
		return objCaudd5doDto;
	}

	/**
	 * 
	 * Method Name: deletePersonRace Method Description: Deletes the Person race
	 * record
	 * 
	 * @param iCaudd5diDto
	 */
	public void deletePersonRace(PersonRaceInsUpdDelInDto iCaudd5diDto) {
		if (iCaudd5diDto != null) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonRace.class);
			criteria.add(Restrictions.eq("person.idPerson", iCaudd5diDto.getIdPerson()));
			criteria.add(Restrictions.eq("cdRace", iCaudd5diDto.getCdPersonRace()));
			PersonRace personRace = (PersonRace) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(personRace)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Caudd5dDaoImpl.PersonRace.not.found.personId", null, Locale.US));
			}
			sessionFactory.getCurrentSession().delete(personRace);

		}
	}

	/**
	 * 
	 * Method Name: savePersonRace Method Description: Inserts the Person race
	 * record Caudd5d
	 * 
	 * @param personRaceInsUpdDelInDto
	 */
	public void savePersonRace(PersonRaceInsUpdDelInDto personRaceInsUpdDelInDto) {
		if (personRaceInsUpdDelInDto != null) {
			PersonRace personRace = new PersonRace();
			if (personRaceInsUpdDelInDto.getIdPerson() != 0) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						Long.valueOf(personRaceInsUpdDelInDto.getIdPerson()));
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(
							messageSource.getMessage("Caudd5dDaoImpl.PersonRace.not.found.personId", null, Locale.US));
				}
				personRace.setPerson(person);
			}
			if (null != personRaceInsUpdDelInDto.getCdPersonRace()) {
				personRace.setCdRace(personRaceInsUpdDelInDto.getCdPersonRace());
			}
			personRace.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().save(personRace);
		}
	}
}
