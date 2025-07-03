package us.tx.state.dfps.service.person.daoimpl;

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
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.PersonPotentialDup;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.dao.PotentialDupDao;
import us.tx.state.dfps.service.person.dto.PersonPotentialDupDto;


/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * PersonDupDaoImpl for PersonDupDao. Sep 25, 2017- 11:14:21 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PotentialDupDaoImpl implements PotentialDupDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${PersonDupDaoImpl.getPersonPotentialDupList}")
	private String getPersonPotentialDupListSql;

	@Value("${PersonDupDaoImpl.getPersonPotentialDup}")
	private String getPersonPotentialDupSql;


	@Value("${PersonPotentialDupDaoImpl.getActivePersonPotentialDupDetail}")
	private String getActivePersonPotentialDupDetailSql;

	private static final Logger log = Logger.getLogger("ServiceBusiness-PersonDupDaoLog");

	/**
	 * Method Name: getPersonPotentialDupList Method Description: Returns list
	 * of person Potential Duplicates.
	 * 
	 * @param idPerson
	 * @return List<PersonPotentialDupDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonPotentialDupDto> getPersonPotentialDupList(Long idPerson) {
		List<PersonPotentialDupDto> PersonPotentialDupList = (List<PersonPotentialDupDto>) (((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(getPersonPotentialDupListSql).setParameter("idPerson", idPerson))
						.addScalar("idPersonPotentialDup", StandardBasicTypes.LONG)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("idDupPerson", StandardBasicTypes.LONG).addScalar("idStaff", StandardBasicTypes.LONG)
						.addScalar("cdReasonNotMerged", StandardBasicTypes.STRING)
						.addScalar("invalid", StandardBasicTypes.STRING).addScalar("merged", StandardBasicTypes.STRING)
						.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("comments", StandardBasicTypes.STRING)
						.addScalar("dtCreated", StandardBasicTypes.DATE)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(PersonPotentialDupDto.class))).list();
		return PersonPotentialDupList;
	}

	/**
	 * Method Name: getPersonPotentialDup Method Description:Retrieves potential
	 * Duplicate and other information related to a person given primary key
	 * 
	 * @param idPerson
	 * @return PersonPotentialDupDto @
	 */
	@Override
	public PersonPotentialDupDto getPersonPotentialDup(Long idPerson) {

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonPotentialDupSql)
				.addScalar("idPersonPotentialDup", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idDupPerson", StandardBasicTypes.LONG)
				.addScalar("idStaff", StandardBasicTypes.LONG).addScalar("cdReasonNotMerged", StandardBasicTypes.STRING)
				.addScalar("invalid", StandardBasicTypes.STRING).addScalar("merged", StandardBasicTypes.STRING)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("comments", StandardBasicTypes.STRING)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idPersonPotentialDup", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonPotentialDupDto.class));

		PersonPotentialDupDto potentialDupDto = new PersonPotentialDupDto();
		if (!TypeConvUtil.isNullOrEmpty(sqlQuery)) {
			potentialDupDto = (PersonPotentialDupDto) sqlQuery.uniqueResult();
		}

		return potentialDupDto;
	}
	/**
	 * Method Name: savePersPotentialDupInfo Method Description:save potential
	 * Duplicate information
	 * 
	 * @param personPotentialDup
	 * @param operation
	 * @return void @
	 **/
	@Override
	public void savePersPotentialDupInfo(PersonPotentialDup personPotentialDup, String operation) {
		if (operation.equals(ServiceConstants.REQ_FUNC_CD_ADD))
			sessionFactory.getCurrentSession().persist(personPotentialDup);
		else if (operation.equals(ServiceConstants.REQ_FUNC_CD_UPDATE))
			sessionFactory.getCurrentSession()
					.saveOrUpdate(sessionFactory.getCurrentSession().merge(personPotentialDup));
	}

	/**
	 * Method Name: getActivePersonPotentialDupDetail Method Description:
	 * Returns person duplicate given idPerson and idDupPerson.
	 * 
	 * @param idPerson
	 * @param idDupPerson
	 * @return PersonPotentialDupDto
	 */
	@Override
	public PersonPotentialDupDto getActivePersonPotentialDupDetail(Long idPerson, Long idDupPerson) {

		PersonPotentialDupDto personPotentialDupDto = null;
		List<PersonPotentialDupDto> personPotentialDupList = (List<PersonPotentialDupDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(getActivePersonPotentialDupDetailSql)
				.addScalar("idPersonPotentialDup", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idDupPerson", StandardBasicTypes.LONG)
				.addScalar("idStaff", StandardBasicTypes.LONG).addScalar("cdReasonNotMerged", StandardBasicTypes.STRING)
				.addScalar("invalid", StandardBasicTypes.STRING).addScalar("merged", StandardBasicTypes.STRING)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("comments", StandardBasicTypes.STRING)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idPerson", idPerson).setParameter("idDupPerson", idDupPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonPotentialDupDto.class))).list();

		if (!ObjectUtils.isEmpty(personPotentialDupList)) {

			personPotentialDupDto = personPotentialDupList.get(0);
		}
		return personPotentialDupDto;
	}
}
