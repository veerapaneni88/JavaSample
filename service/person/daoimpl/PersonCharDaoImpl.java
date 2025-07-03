package us.tx.state.dfps.service.person.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Characteristics;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.person.dao.PersonCharDao;
import us.tx.state.dfps.service.person.dto.PersCharsDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Dao Impl
 * class for Person Characters> May 7, 2018- 10:38:55 AM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class PersonCharDaoImpl implements PersonCharDao {
	@Autowired
	MessageSource messageSource;
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonCharDaoImpl.getPersonCharList}")
	private String getPersonCharListSql;

	@Value("${PersonCharDaoImpl.getPersonCharDataBySnapshot}")
	private String getPersonCharDataBySnapshot;

	@Value("${PersonCharDao.fetchPersonCharDataSql}")
	private String fetchPersonCharDataSql;

	/**
	 * 
	 * Method Name: getPersonCharList Method Description: get person char list
	 * by idPerson
	 * 
	 * @param idPerson
	 * @return ArrayList<CharacteristicsDto>
	 */
	@Override
	public ArrayList<CharacteristicsDto> getPersonCharList(int idPerson) {
		Query query1 = sessionFactory.getCurrentSession().createSQLQuery(getPersonCharListSql)
				.addScalar("idCharacteristics", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdCharCategory", StandardBasicTypes.STRING)
				.addScalar("cdCharacteristic", StandardBasicTypes.STRING)
				.addScalar("dtCharStart", StandardBasicTypes.DATE).addScalar("dtCharEnd", StandardBasicTypes.DATE)
				.addScalar("indAfcars", StandardBasicTypes.STRING).addScalar("cdStatus", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CharacteristicsDto.class));
		query1.setParameter("idPerson", idPerson);
		return (ArrayList<CharacteristicsDto>) query1.list();
	}

	/**
	 * 
	 * Method Name: savePersonChar Method Description: save person char
	 * 
	 * @param characteristicsDto
	 * @return long
	 */
	@Override
	public long savePersonChar(CharacteristicsDto characteristicsDto) {
		Characteristics characteristics = new Characteristics();
		characteristics.setCdCharacteristic(characteristicsDto.getCdCharacteristic());
		characteristics.setCdCharCategory(characteristicsDto.getCdCharCategory());
		characteristics.setCdStatus(characteristicsDto.getCdStatus());
		characteristics.setDtCharEnd(characteristicsDto.getDtCharEnd());
		characteristics.setDtCharStart(characteristicsDto.getDtCharStart());
		characteristics.setIdPerson(characteristicsDto.getIdPerson());
		characteristics.setDtLastUpdate(new Date());
		characteristics.setIndAfcars(characteristicsDto.getIndAfcars());
		return (long) sessionFactory.getCurrentSession().save(characteristics);
	}

	/**
	 * 
	 * Method Name: updatePersonChar Method Description: update person char
	 * 
	 * @param characteristicsDto
	 * @return long
	 */
	@Override
	public long updatePersonChar(CharacteristicsDto characteristicsDto) {

		Characteristics characteristics = (Characteristics) sessionFactory.getCurrentSession()
				.get(Characteristics.class, characteristicsDto.getIdCharacteristics());
		if (!ObjectUtils.isEmpty(characteristics)) {
			characteristics.setIdPerson(characteristicsDto.getIdPerson());
			if (!ObjectUtils.isEmpty(characteristicsDto.getCdCharacteristic())) {
				characteristics.setCdCharacteristic(characteristicsDto.getCdCharacteristic());
			}
			if (!ObjectUtils.isEmpty(characteristicsDto.getDtCharStart())) {
				characteristics.setDtCharStart(characteristicsDto.getDtCharStart());
			}
			if (!ObjectUtils.isEmpty(characteristicsDto.getDtCharEnd())) {
				characteristics.setDtCharEnd(characteristicsDto.getDtCharEnd());
			}
			if (!ObjectUtils.isEmpty(characteristicsDto.getCdCharCategory())) {
				characteristics.setCdCharCategory(characteristicsDto.getCdCharCategory());
			}
			if (!ObjectUtils.isEmpty(characteristicsDto.getIndAfcars())) {
				characteristics.setIndAfcars(characteristicsDto.getIndAfcars());
			}
			if (!ObjectUtils.isEmpty(characteristicsDto.getCdStatus())) {
				characteristics.setCdStatus(characteristicsDto.getCdStatus());
			}
			sessionFactory.getCurrentSession().saveOrUpdate(characteristics);
			return 1;
		}
		return 0;
	}

	/**
	 * Method Name: getPersonCharList Method Description:Fetches the person
	 * characteristics from snapshot table ( SS_CHARACTERISTICS )
	 * 
	 * @param idPerson
	 * @param cdCharCategory
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return List<PersCharsDto>
	 */
	@Override
	public List<PersCharsDto> getPersonCharData(Long idPerson, String cdCharCategory, Long idReferenceData,
			String cdActionType, String cdSnapshotType) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getPersonCharDataBySnapshot);
		query.setParameter("idReferenceData", idReferenceData);
		query.setParameter("cdSnapShotType", cdSnapshotType);
		query.setParameter("cdActionType", cdActionType);
		query.setParameter("idObject", idPerson);
		query.setParameter("idPerson", idPerson);
		query.setParameter("cdCharCategory", cdCharCategory);
		query.addScalar("idCharacteristics", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdCharCategory", StandardBasicTypes.STRING)
				.addScalar("cdCharacteristic", StandardBasicTypes.STRING)
				.addScalar("cdStatus", StandardBasicTypes.STRING).addScalar("indAfcars", StandardBasicTypes.STRING)
				.addScalar("dtCharStart", StandardBasicTypes.DATE).addScalar("dtCharEnd", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		List<PersCharsDto> personCharDtos = query.setResultTransformer(Transformers.aliasToBean(PersCharsDto.class))
				.list();
		return personCharDtos;
	}

	/**
	 * Method Name: getPersonCharData Method Description:Takes person ID and a
	 * characteristics category ( = CHARACTERISTICS.CD_CHAR_CATEGORY ) and
	 * retrieves the characteristics rows with the given category, which the
	 * person has now or had in the past, with status code for each row, but
	 * including only the most recent row for each characteristic.
	 * 
	 * @param idPerson
	 * @param cdCharCategory
	 * @return List<PersCharsDto>
	 */
	@Override
	public List<PersCharsDto> getPersonCharData(long idPerson, String cdCharCategory) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(fetchPersonCharDataSql);
		query.setParameter("idPersonOut", idPerson);
		query.setParameter("cdCharCategoryOut", cdCharCategory);
		query.setParameter("idPersonIn", idPerson);
		query.setParameter("cdCharCategoryIn", cdCharCategory);
		query.addScalar("idCharacteristics", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdCharCategory", StandardBasicTypes.STRING)
				.addScalar("cdCharacteristic", StandardBasicTypes.STRING)
				.addScalar("dtCharStart", StandardBasicTypes.DATE).addScalar("dtCharEnd", StandardBasicTypes.DATE)
				.addScalar("indAfcars", StandardBasicTypes.STRING).addScalar("cdStatus", StandardBasicTypes.STRING);

		List<PersCharsDto> personCharDtos = query.setResultTransformer(Transformers.aliasToBean(PersCharsDto.class))
				.list();
		return personCharDtos;
	}

}
