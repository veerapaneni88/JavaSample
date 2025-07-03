package us.tx.state.dfps.service.centralregistry.centralregistrydaoimpl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.centralregistry.centralregistrydao.CentralRegistryDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.xmlstructs.inputstructs.PersonRoleDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CentralRegistryDaoImpl will implemented all operation defined in
 * CentralRegistryDao Interface related CentralRegistry module.. Apr 27, 2018-
 * 2:02:51 PM © 2017 Texas Department of Family and Protective Services
 */

@Repository
public class CentralRegistryDaoImpl implements CentralRegistryDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${CentralRegistryDaoImpl.getPersonRolesForOpenInv}")
	private String getPersonRolesForOpenInvSql;
	@Value("${CentralRegistryDaoImpl.getPersonRolesForOpenARIn}")
	private String getPersonRolesForOpenARInSql;

	@Value("${CentralRegistryDaoImpl.getSpPersonRole}")
	private String getSpPersonRoleSql;

	@Value("${CentralRegistryDaoImpl.getVictimPersonRoles}")
	private String getVictimPersonRolesSql;

	@Value("${CentralRegistryDaoImpl.getPersonInfo}")
	private String getPersonInfoSql;

	/**
	 * 
	 * Method Name: getPersonRoleDtoli DAM Name : CLSC91D Method
	 * Description:This dam will select the person roles of DB, DP, VP, AP in
	 * open inv stages and place a check mark in the first box.
	 * 
	 * @param idPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonRoleDto> getPersonRolesForOpenInv(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonRolesForOpenInvSql)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.setParameter("idPerson", idPerson).setResultTransformer(Transformers.aliasToBean(PersonRoleDto.class));
		List<PersonRoleDto> personRoleDtoLi = query.list();

		return personRoleDtoLi;
	}

	/**
	 * 
	 * Method Name: getPersonRolesForOpenARIn DAM Name : CLSC9AD Method
	 * Description:This dam will select the person roles of VP, AP of intakes
	 ** tied to the open A-R stage and place a check mark in the box.
	 **
	 * @param idPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonRoleDto> getPersonRolesForOpenARIn(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonRolesForOpenARInSql)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.setParameter("idPerson", idPerson).setResultTransformer(Transformers.aliasToBean(PersonRoleDto.class));
		List<PersonRoleDto> personRoleDtoLi = query.list();

		return personRoleDtoLi;
	}

	/**
	 * 
	 * Method Name: getSpPersonRole DAM Name : CLSC92D Method Description:This
	 * dam will select the role of Sustained Perpetrator and place a check mark
	 * the 3rd question on the form.
	 * 
	 * @param idPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonRoleDto> getSpPersonRole(Long idPerson) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSpPersonRoleSql)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.setParameter("idPerson", idPerson).setResultTransformer(Transformers.aliasToBean(PersonRoleDto.class));
		List<PersonRoleDto> personRoleDtoLi = query.list();

		return personRoleDtoLi;
	}

	/**
	 * 
	 * Method Name: getVictimPersonRoles DAM Name : CLSC93D Method
	 * Description:This dam will select the role of designated victim or perp
	 ** and place a check mark the 4th question on the form.
	 **
	 * @param idPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonRoleDto> getVictimPersonRoles(Long idPerson) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getVictimPersonRolesSql)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.setParameter("idPerson", idPerson).setResultTransformer(Transformers.aliasToBean(PersonRoleDto.class));
		List<PersonRoleDto> personRoleDtoLi = query.list();

		return personRoleDtoLi;
	}

	/**
	 * 
	 * Method Name: getPersonInfo DAM Name: CSES96D Method Description: This dam
	 * will return person information.
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public PersonDto getPersonInfo(Long idPerson) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonInfoSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("dtGenericSysDate", StandardBasicTypes.DATE).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));

		return (PersonDto) query.uniqueResult();
	}

}
