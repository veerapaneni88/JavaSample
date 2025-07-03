package us.tx.state.dfps.service.person.daoimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.IncomingEthnicity;
import us.tx.state.dfps.common.domain.IncomingRace;
import us.tx.state.dfps.common.domain.PersonEthnicity;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dao.PersonRaceEthnicityDao;
import us.tx.state.dfps.service.person.dto.RaceEthnicityDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersonRaceEthnicityDaoImpl Oct 5, 2017- 11:04:02 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PersonRaceEthnicityDaoImpl implements PersonRaceEthnicityDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	SessionFactory sessionFactory;

	@Value("${PersonRaceEthnicityDaoImpl.getPersonRaceList}")
	private String getPersonRaceListSql;

	@Value("${PersonRaceEthnicityDaoImpl.selectPersonRace}")
	private String selectPersonRace;

	@Value("${PersonRaceEthnicityDaoImpl.selectPersonEthnicity}")
	private String selectPersonEthnicity;

	/**
	 * Method Name: getPersonRaceList Method Description: Get Person Race for
	 * input person id.
	 * 
	 * @param idPerson
	 * @return ArrayList<PersonRaceDto>
	 * @throws DataNotFoundException
	 */
	@Override
	public ArrayList<PersonRaceDto> getPersonRaceList(Long idPerson) throws DataNotFoundException {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonRaceListSql)
				.addScalar("idPersonRace", StandardBasicTypes.LONG).addScalar("dtRaceUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdPersonRace", StandardBasicTypes.STRING)
				.setParameter("idPerson", idPerson).setResultTransformer(Transformers.aliasToBean(PersonRaceDto.class));

		return (ArrayList<PersonRaceDto>) query.list();
	}

	/**
	 * Method Name: getPersonEthnicityList Method Description: This function
	 * fetches the Person Ethnicity list for a Person
	 * 
	 * @param idPerson
	 * @return List<PersonEthnicityDto>
	 */
	@Override
	public List<PersonEthnicityDto> getPersonEthnicityList(Long idPerson) throws DataNotFoundException {
		List<PersonEthnicityDto> personEthnicityDtos = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonEthnicity.class);
		criteria.add(Restrictions.eq("person.idPerson", idPerson));
		List<PersonEthnicity> personEthnicities = criteria.list();
		for (PersonEthnicity personEthnicity : personEthnicities) {
			PersonEthnicityDto personEthnicityDto = new PersonEthnicityDto();

			personEthnicityDto.setIdPersonEthnicity(personEthnicity.getIdPersonEthnicity());
			personEthnicityDto.setDtEthnicityUpdate(personEthnicity.getDtLastUpdate());
			personEthnicityDto.setIdPerson(personEthnicity.getPerson().getIdPerson());
			personEthnicityDto.setCdPersonEthnicity(personEthnicity.getCdEthnicity());

			personEthnicityDtos.add(personEthnicityDto);
		}
		if (TypeConvUtil.isNullOrEmpty(personEthnicities)) {
			throw new DataNotFoundException(
					messageSource.getMessage("person.personEthnicity.notFound", null, Locale.US));
		}
		return personEthnicityDtos;
	}

	/**
	 * Method Name: queryPersonRace Method Description:fetch cdRace
	 * 
	 * @param personId
	 * @param reb
	 * @return RaceEthnicityDto
	 */
	public RaceEthnicityDto queryPersonRace(Long personId, RaceEthnicityDto reb) throws DataNotFoundException {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IncomingRace.class);
		criteria.add(Restrictions.eq("incomingPerson.idIncmgPerson", personId));

		List<IncomingRace> ethnicity = (List<IncomingRace>) criteria.list();

		if (TypeConvUtil.isNullOrEmpty(ethnicity)) {
			throw new DataNotFoundException((messageSource.getMessage("Common.noRecordFound", null, Locale.US)));
		}
		RaceEthnicityDto.Races raceEthnicityDto = new RaceEthnicityDto.Races();
		for (IncomingRace incomingRace : ethnicity) {
			raceEthnicityDto.put(incomingRace.getCdRace(), ServiceConstants.CONJUNCTION_AND);
		}

		reb.setRaces(raceEthnicityDto);
		return reb;

	}

	/**
	 * Method Name: queryPersonEthnicity Method Description:fetch cdEthnicity
	 * 
	 * @param personId
	 * @return String
	 */
	public String queryPersonEthnicity(Long personId) throws DataNotFoundException {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IncomingEthnicity.class);
		criteria.add(Restrictions.eq("incomingPerson.idIncmgPerson", personId));

		IncomingEthnicity ethnicity = (IncomingEthnicity) criteria.list().get(ServiceConstants.Zero);

		if (TypeConvUtil.isNullOrEmpty(ethnicity)) {
			throw new DataNotFoundException((messageSource.getMessage("Common.noRecordFound", null, Locale.US)));
		}
		return ethnicity.getCdEthnicity();

	}

	/**
	 * Method Name: selectPersonRace Method Description: This method returns
	 * Person Races separated by comma.
	 *
	 * @param personIdList1
	 * @return Map
	 * @throws DataNotFoundException
	 */
	@Override
	public Map selectPersonRace(List<Integer> personIdList1) throws DataNotFoundException {

		Map personRaceMap = new HashMap<>();

		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectPersonRace)
				.setResultTransformer(Transformers.aliasToBean(PersonRaceDto.class)));

		query.addScalar("idPerson", StandardBasicTypes.LONG);
		query.addScalar("cdPersonRace", StandardBasicTypes.STRING);

		query.setParameter("idPersonList", personIdList1);

		List<PersonRaceDto> personRaceDtos = query.list();

		if (TypeConvUtil.isNullOrEmpty(personRaceDtos)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		for (PersonRaceDto personRaceDto : personRaceDtos) {
			Long idPerson = personRaceDto.getIdPerson();
			String race = personRaceDto.getCdPersonRace();
			personRaceMap.put(idPerson, race);
		}

		return personRaceMap;
	}

	/**
	 * Method Name: selectPersonEthnicity Method Description: This method
	 * returns Person Ethnicity separated by comma.
	 *
	 * @param personIdList1
	 * @return Map
	 * @throws DataNotFoundException
	 */
	@Override
	public Map selectPersonEthnicity(List<Integer> personIdList1) throws DataNotFoundException {

		Map personEthnicityMap = new HashMap<>();

		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectPersonEthnicity)
				.setResultTransformer(Transformers.aliasToBean(PersonEthnicityDto.class)));

		query.addScalar("idPerson", StandardBasicTypes.LONG);
		query.addScalar("cdPersonEthnicity", StandardBasicTypes.STRING);

		query.setParameter("idPersonList", personIdList1);

		List<PersonEthnicityDto> personEthnicityDtos = query.list();

		if (TypeConvUtil.isNullOrEmpty(personEthnicityDtos)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		for (PersonEthnicityDto personEthnicityDto : personEthnicityDtos) {
			Long idPerson = personEthnicityDto.getIdPerson();
			String ethnicity = personEthnicityDto.getCdPersonEthnicity();
			personEthnicityMap.put(idPerson, ethnicity);
		}

		return personEthnicityMap;
	}

}