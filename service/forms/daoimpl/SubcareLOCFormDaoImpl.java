/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jan 18, 2018- 10:56:00 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.daoimpl;

import java.util.Date;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonId;
import us.tx.state.dfps.service.forms.dao.SubcareLOCFormDao;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 18, 2018- 10:56:00 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class SubcareLOCFormDaoImpl implements SubcareLOCFormDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${SubcareLOCFormDaoImpl.getPhnNbrbyPersonId}")
	private transient String getPhnNbrbyPersonId;

	/**
	 * Method Description: DAM: CCMN72D This dam will retrieve the child
	 * Medicaid Number and the Medicaid id number.
	 * 
	 * @param cdPersonIdType,
	 *            indPersonIdInvalid, dtPersonIdEnd
	 * @return PersonIdDto @
	 */
	@Override
	public PersonIdDto getMedicaidNbrByPersonId(Long idPerson, String cdPersonIdType, String indPersonIdInvalid,
			Date dtPersonIdEnd) {
		PersonIdDto personIdDto = new PersonIdDto();
		personIdDto = (PersonIdDto) sessionFactory.getCurrentSession().createCriteria(PersonId.class)
				.setProjection(
						Projections.projectionList().add(Projections.property("cdPersonIdType"), "cdPersonIdType")
								.add(Projections.property("indPersonIdInvalid"), "indPersonIdInvalid")
								.add(Projections.property("dtPersonIdEnd"), "dtPersonIdEnd")
								.add(Projections.property("dtPersonIdStart"), "dtPersonIdStart")
								.add(Projections.property("descPersonId"), "descPersonId")
								.add(Projections.property("person.idPerson"), "idPerson")
								.add(Projections.property("nbrPersonIdNumber"), "nbrPersonIdNumber")
								.add(Projections.property("dtLastUpdate"), "dtLastUpdate"))
				.add(Restrictions.eq("person.idPerson", idPerson))
				.add(Restrictions.eq("cdPersonIdType", cdPersonIdType))
				.add(Restrictions.eq("indPersonIdInvalid", indPersonIdInvalid))
				.add(Restrictions.eq("dtPersonIdEnd", dtPersonIdEnd))
				.setResultTransformer(Transformers.aliasToBean(PersonIdDto.class)).uniqueResult();

		return personIdDto;
	}

	/**
	 * Method Description: DAM: CSEC74D This dam will retrieve all child info
	 * based upon an Id Person.
	 * 
	 * @param idPerson
	 * @return PersonDto @
	 */
	@Override
	public PersonDto getChildInfoByPersonId(Long idPerson) {
		PersonDto personDto = new PersonDto();
		personDto = (PersonDto) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.setProjection(Projections.projectionList().add(Projections.property("cdPersonDeath"), "cdPersonDeath")
						.add(Projections.property("cdPersonEthnicGroup"), "cdPersonEthnicGroup")
						.add(Projections.property("cdPersonMaritalStatus"), "cdPersonMaritalStatus")
						.add(Projections.property("cdPersonReligion"), "cdPersonReligion")
						.add(Projections.property("cdPersonLanguage"), "cdPersonLanguage")
						.add(Projections.property("cdPersonSex"), "cdPersonSex")
						.add(Projections.property("cdPersonStatus"), "cdPersonStatus")
						.add(Projections.property("dtPersonBirth"), "dtPersonBirth")
						.add(Projections.property("dtPersonDeath"), "dtPersonDeath")
						.add(Projections.property("nbrPersonAge"), "nbrPersonAge")
						.add(Projections.property("nmPersonFull"), "nmPersonFull")
						.add(Projections.property("txtPersonOccupation"), "txtPersonOccupation")
						.add(Projections.property("indPersCancelHist"), "indPersCancelHist")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("cdPersonLivArr"), "cdPersonLivArr")
						.add(Projections.property("cdPersonChar"), "cdPersonChar")
						.add(Projections.property("indPersonDobApprox"), "indPersonDobApprox")
						.add(Projections.property("nmPersonFirst"), "nmPersonFirst")
						.add(Projections.property("nmPersonMiddle"), "nmPersonMiddle")
						.add(Projections.property("nmPersonLast"), "nmPersonLast")
						.add(Projections.property("cdPersonSuffix"), "cdPersonSuffix")
						.add(Projections.property("idPerson"), "idPerson"))
				.add(Restrictions.eq("idPerson", idPerson))
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class)).uniqueResult();

		return personDto;
		// Made changes to PersonDto!!!!
	}

	/**
	 * Method Name: getPhnNbrbyPersonId Method Description:CSES29D - This DAM
	 * will perform a full row retrieval on the person phone table given
	 * ID_PERSON and the type of phone number to retrieve
	 * 
	 * @param idPerson
	 * @param cdPersonPhoneType
	 * @param indPersonPhnInvalid
	 * @param dtPersonPhoneEnd
	 * @return @
	 */
	@Override
	public PersonPhoneDto getPhnNbrbyPersonId(Long idPerson, String cdPersonPhoneType) {
		PersonPhoneDto personPhoneDto = new PersonPhoneDto();
		personPhoneDto = (PersonPhoneDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPhnNbrbyPersonId).setParameter("idPerson", idPerson)
				.setParameter("cdPersonPhoneType", cdPersonPhoneType))
						.addScalar("idPersonPhone", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("txtPersonPhoneComments", StandardBasicTypes.STRING)
						.addScalar("nbrPersonPhoneExtension", StandardBasicTypes.STRING)
						.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
						.addScalar("dtPersonPhoneStart", StandardBasicTypes.DATE)
						.addScalar("dtPersonPhoneEnd", StandardBasicTypes.DATE)
						.addScalar("indPersonPhnInvalid", StandardBasicTypes.STRING)
						.addScalar("indPersonPhnPrimary", StandardBasicTypes.STRING)
						.addScalar("cdPersonPhoneType", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PersonPhoneDto.class)).uniqueResult();
		return personPhoneDto;
	}

}
