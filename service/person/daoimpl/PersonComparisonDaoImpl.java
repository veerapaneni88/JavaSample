package us.tx.state.dfps.service.person.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.person.dao.PersonComparisonDao;
import us.tx.state.dfps.service.person.dto.PersonEmailDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.PersonIncomeResourceDto;
import us.tx.state.dfps.service.person.dto.PersonMergeInfoDto;
import us.tx.state.dfps.service.person.dto.PersonPotentialDupDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao methods
 * for Person Comparison Form per03o00 May 30, 2018- 4:14:31 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PersonComparisonDaoImpl implements PersonComparisonDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${perscompdao.getDemInfo}")
	private String getDemInfoSql;

	@Value("${perscompdao.getPersDup}")
	private String getPersDupSql;

	@Value("${perscompdao.getPersEmail}")
	private String getPersEmailSql;

	@Value("${perscompdao.getMergeIds}")
	private String getMergeIdsSql;

	@Value("${perscompdao.getPersIncomeResrc}")
	private String getPersIncomeResrcSql;

	@Value("${perscompdao.getPersIntake}")
	private String getPersIntakeSql;

	@Value("${perscompdao.getPersInv}")
	private String getPersInvSql;

	/**
	 * Method Name: getDemInfo Method Description: This DAM will Join the Person
	 * Merge Table to retrieve the Id Person Forward for the Given host
	 * variable. It joins will the Address, Id, Name and Person table to
	 * retrieve demographic information for the Id Pers Merge Forward. CSEC67D
	 * 
	 * @param idPerson
	 * @return PersonMergeInfoDto
	 */
	@Override
	public PersonMergeInfoDto getDemInfo(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getDemInfoSql)
				.addScalar("idPersMergeFrwrd", StandardBasicTypes.LONG)
				.addScalar("idPersMergeClsd", StandardBasicTypes.LONG).addScalar("dtPersMerge", StandardBasicTypes.DATE)
				.addScalar("cdPersSex", StandardBasicTypes.STRING).addScalar("dtPersBirth", StandardBasicTypes.DATE)
				.addScalar("dtPersDeath", StandardBasicTypes.DATE)
				.addScalar("cdPersEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("nbrPersIdNum", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrCity", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("cdPersAddrCnty", StandardBasicTypes.STRING)
				.addScalar("nmNameLast", StandardBasicTypes.STRING).addScalar("nmNameMiddle", StandardBasicTypes.STRING)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonMergeInfoDto.class));
		List<PersonMergeInfoDto> list = (List<PersonMergeInfoDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * Method Name: getMergeIds Method Description: Display all merged ID_PERSON
	 * for a given ID_PERSON and their latest PRIMARY names, regardless whether
	 * these merged names are invalid or not. CLSC46D
	 * 
	 * @param idPerson
	 * @return List<PersonMergeInfoDto>
	 */
	@Override
	public List<PersonMergeInfoDto> getMergeIds(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getMergeIdsSql)
				.addScalar("idPersonMerge", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPersMergeFrwrd", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("idPersMergeClsd", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull2", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst2", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle2", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast2", StandardBasicTypes.STRING)
				.addScalar("idPersonMergeWorker", StandardBasicTypes.LONG)
				.addScalar("nmEmployeeLast", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeFirst", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeMiddle", StandardBasicTypes.STRING)
				.addScalar("idPersMergeSplitWrkr", StandardBasicTypes.LONG)
				.addScalar("nmEmployeeLast2", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeFirst2", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeMiddle2", StandardBasicTypes.STRING)
				.addScalar("indPersonMergeInvalid", StandardBasicTypes.STRING)
				.addScalar("dtPersMergeSplit", StandardBasicTypes.DATE)
				.addScalar("dtPersMerge", StandardBasicTypes.DATE).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonMergeInfoDto.class));
		List<PersonMergeInfoDto> list = (List<PersonMergeInfoDto>) query.list();
		return list;
	}

	/**
	 * Method Name: getPersDup Method Description:Full row retrieval from
	 * PERSON_POTENTIAL_DUP. CLSSB4D
	 * 
	 * @param idPerson
	 * @return PersonPotentialDupDto
	 */
	@Override
	public PersonPotentialDupDto getPersDup(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersDupSql)
				.addScalar("idPersonPotentialDup", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idDupPerson", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("cdReasonNotMerged", StandardBasicTypes.STRING)
				.addScalar("idWrkrPerson", StandardBasicTypes.LONG).addScalar("indInvalids", StandardBasicTypes.STRING)
				.addScalar("indMergeds", StandardBasicTypes.STRING).addScalar("comments", StandardBasicTypes.STRING)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonPotentialDupDto.class));
		List<PersonPotentialDupDto> list = (List<PersonPotentialDupDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * Method Name: getPersEmail Method Description:Retrieves all rows from
	 * PERSON_EMAIL for input idPerson CLSSB3D
	 * 
	 * @param idPerson
	 * @return List<PersonEmailDto>
	 */
	@Override
	public List<PersonEmailDto> getPersEmail(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersEmailSql)
				.addScalar("idPersonEmail", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdType", StandardBasicTypes.STRING).addScalar("indPrimary", StandardBasicTypes.STRING)
				.addScalar("indInvalid", StandardBasicTypes.STRING).addScalar("txtEmail", StandardBasicTypes.STRING)
				.addScalar("dtStart", StandardBasicTypes.DATE).addScalar("dtEnd", StandardBasicTypes.DATE)
				.addScalar("txtComments", StandardBasicTypes.STRING).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonEmailDto.class));
		List<PersonEmailDto> list = (List<PersonEmailDto>) query.list();
		return list;
	}

	/**
	 * Method Name: getPersIncomeResrc Method Description:This DAM will do a
	 * full row retrieval from the INCOME AND RESOURCES table an join with the
	 * PERSON table to retrieve the worker's name using Id Event. CLSS58D
	 * 
	 * @param idPerson
	 * @return List<PersonIncomeResourceDto>
	 */
	@Override
	public List<PersonIncomeResourceDto> getPersIncomeResrc(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersIncomeResrcSql)
				.addScalar("idIncRsrc", StandardBasicTypes.LONG).addScalar("idIncRsrcWorker", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("amtIncRsrc", StandardBasicTypes.DOUBLE)
				.addScalar("cdIncRsrcType", StandardBasicTypes.STRING)
				.addScalar("cdIncRsrcIncome", StandardBasicTypes.STRING)
				.addScalar("dtIncRsrcFrom", StandardBasicTypes.DATE).addScalar("dtIncRsrcTo", StandardBasicTypes.DATE)
				.addScalar("indIncRsrcNotAccess", StandardBasicTypes.STRING)
				.addScalar("sdsIncRsrcSource", StandardBasicTypes.STRING)
				.addScalar("sdsIncRsrcVerfMethod", StandardBasicTypes.STRING)
				.addScalar("incRsrcDesc", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonIncomeResourceDto.class));
		List<PersonIncomeResourceDto> list = (List<PersonIncomeResourceDto>) query.list();
		return list;
	}

	/**
	 * Method Name: getPersIntakeInv Method Description:This DAM will retrieve
	 * all of the identifiers for a person from the person_id table. The numbers
	 * are sorted differently for investigation and intake. CINT17D
	 * 
	 * @param idPerson
	 * @return List<PersonIdDto>
	 */
	@Override
	public List<PersonIdDto> getPersIntakeInv(Long idPerson, Boolean indIntake, Date dtSysTsQuery) {
		Query query = null;
		if (indIntake) {
			query = sessionFactory.getCurrentSession().createSQLQuery(getPersIntakeSql)
					.addScalar("idPersonId", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
					.addScalar("cdPersonIdType", StandardBasicTypes.STRING)
					.addScalar("descPersonId", StandardBasicTypes.STRING)
					.addScalar("indPersonIdInvalid", StandardBasicTypes.STRING)
					.addScalar("dtPersonIdStart", StandardBasicTypes.DATE)
					.addScalar("dtPersonIdEnd", StandardBasicTypes.DATE)
					.addScalar("indValidateByInterface", StandardBasicTypes.STRING)
					.addScalar("cdSsnSource", StandardBasicTypes.STRING)
					.addScalar("cdSsnVerifMeth", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
					.setParameter("dtSysTsQuery", dtSysTsQuery)
					.setResultTransformer(Transformers.aliasToBean(PersonIdDto.class));
		} else {
			query = sessionFactory.getCurrentSession().createSQLQuery(getPersInvSql)
					.addScalar("idPersonId", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
					.addScalar("cdPersonIdType", StandardBasicTypes.STRING)
					.addScalar("descPersonId", StandardBasicTypes.STRING)
					.addScalar("indPersonIdInvalid", StandardBasicTypes.STRING)
					.addScalar("dtPersonIdStart", StandardBasicTypes.DATE)
					.addScalar("dtPersonIdEnd", StandardBasicTypes.DATE)
					.addScalar("indValidateByInterface", StandardBasicTypes.STRING)
					.addScalar("cdSsnSource", StandardBasicTypes.STRING)
					.addScalar("cdSsnVerifMeth", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
					.setResultTransformer(Transformers.aliasToBean(PersonIdDto.class));

		}

		List<PersonIdDto> list = (List<PersonIdDto>) query.list();
		return list;
	}

}
