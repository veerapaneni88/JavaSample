package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.PersonPhoneDetailsDao;
import us.tx.state.dfps.service.admin.dto.PersonPhoneDetailReq;
import us.tx.state.dfps.service.admin.dto.PersonPhoneDetailsDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * PersonPhoneDetailsDaoImpl Jul 6, 2018- 12:11:13 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class PersonPhoneDetailsDaoImpl implements PersonPhoneDetailsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonPhoneDetailsDaoImpl.getPersonPhoneDetails}")
	private String getPersonPhoneDetails;

	@Value("${PersonPhoneDetailsDaoImpl.retrvPersonPhoneIdIntake}")
	private String retrvPersonPhoneIdIntake;

	@Value("${PersonPhoneDetailsDaoImpl.retrvPersonPhoneIdNotIntake}")
	private String retrvPersonPhoneIdNotIntake;

	private static final Logger log = Logger.getLogger(PersonPhoneDetailsDaoImpl.class);

	/**
	 * Method Description: getPersonPhoneDtls - Method will query the
	 * PersonPhone table to fetch the Person phone details for a given person Id
	 * 
	 * @param personPhoneDetailReq
	 * @return personPhoneDtlsList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonPhoneDetailsDto> getPersonPhoneDetails(PersonPhoneDetailReq personPhoneDetailReq) {
		log.debug("Entering method getPersonPhoneDtls in PersonPhoneDetailsDaoImpl");
		List<PersonPhoneDetailsDto> personPhoneDtlsList = new ArrayList<PersonPhoneDetailsDto>();
		StringBuilder retrievePersonPhoneQuery = new StringBuilder(getPersonPhoneDetails);
		boolean isIndIntakeExists = !ObjectUtils.isEmpty(personPhoneDetailReq.getSysIndIntake());
		if (isIndIntakeExists && personPhoneDetailReq.getSysIndIntake().equals(ServiceConstants.STRING_IND_Y)) {
			retrievePersonPhoneQuery.append(ServiceConstants.CONSTANT_SPACE);
			retrievePersonPhoneQuery.append(retrvPersonPhoneIdIntake);
		} else {
			retrievePersonPhoneQuery.append(ServiceConstants.CONSTANT_SPACE);
			retrievePersonPhoneQuery.append(retrvPersonPhoneIdNotIntake);
		}
		Query retrievePersonPhoneDtls = sessionFactory.getCurrentSession()
				.createSQLQuery(retrievePersonPhoneQuery.toString()).addScalar("cdPhoneType").addScalar("nbrPhone")
				.addScalar("nbrPhoneExtension").addScalar("dtDtPersonPhoneStart", StandardBasicTypes.DATE)
				.addScalar("dtDtPersonPhoneEnd", StandardBasicTypes.DATE).addScalar("indPersonPhonePrimary")
				.addScalar("indPersonPhoneInvalid").addScalar("txtPhoneComments")
				.addScalar("idPhone", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PersonPhoneDetailsDto.class));
		retrievePersonPhoneDtls.setLong("idPerson", personPhoneDetailReq.getIdPerson());
		if (isIndIntakeExists && personPhoneDetailReq.getSysIndIntake().equals(ServiceConstants.STRING_IND_Y)) {
			retrievePersonPhoneDtls.setParameter("tsSysTsQuery", personPhoneDetailReq.getTsSysTsQuery());
		}
		personPhoneDtlsList = retrievePersonPhoneDtls.list();

		log.debug("Exiting method getPersonPhoneDtls in PersonPhoneDetailsDaoImpl");
		return personPhoneDtlsList;
	}
}
