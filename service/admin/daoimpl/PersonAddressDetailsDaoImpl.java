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

import us.tx.state.dfps.service.admin.dao.PersonAddressDetailsDao;
import us.tx.state.dfps.service.admin.dto.PersonAddressDetailsDto;
import us.tx.state.dfps.service.admin.dto.PersonAddressDetailsReq;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * PersonAddressDetailsDaoImpl Jul 6, 2018- 12:05:01 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class PersonAddressDetailsDaoImpl implements PersonAddressDetailsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonAddressDetailsDaoImpl.getPersonAddressDetails}")
	private String getPersonAddressDetails;

	@Value("${PersonAddressDetailsDaoImpl.retrvPersonAdresIdIntake}")
	private String retrvPersonAdresIdIntake;

	@Value("${PersonAddressDetailsDaoImpl.retrvPersonAdresIdNotIntake}")
	private String retrvPersonAdresIdNotIntake;

	private static final Logger log = Logger.getLogger(PersonAddressDetailsDaoImpl.class);

	/**
	 * Method Description: getPersonAddressDtls - Method will query the
	 * PersonAddress table to fetch the Person address details for a given
	 * person Id.
	 *
	 * @param personAddressDetailsReq
	 *            the person address details req
	 * @return personAdressDtlsList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonAddressDetailsDto> getPersonAddressDtls(PersonAddressDetailsReq personAddressDetailsReq) {
		log.debug("Entering method getPersonAddressDtls in PersonAddressDetailsDaoImpl");
		List<PersonAddressDetailsDto> personAdressDtlsList = new ArrayList<PersonAddressDetailsDto>();
		StringBuilder retrievePersonAdressesQuery = new StringBuilder(getPersonAddressDetails);
		boolean isIndIntakeExists = !ObjectUtils.isEmpty(personAddressDetailsReq.getIndIntake());
		if (isIndIntakeExists && personAddressDetailsReq.getIndIntake().equals(ServiceConstants.STRING_IND_Y)) {
			retrievePersonAdressesQuery.append(ServiceConstants.CHAR_SPACE);
			retrievePersonAdressesQuery.append(retrvPersonAdresIdIntake);
		} else {
			retrievePersonAdressesQuery.append(ServiceConstants.CHAR_SPACE);
			retrievePersonAdressesQuery.append(retrvPersonAdresIdNotIntake);
		}
		Query retrievePersonAdresses = sessionFactory.getCurrentSession()
				.createSQLQuery(retrievePersonAdressesQuery.toString()).addScalar("addrZip").addScalar("cdAddrState")
				.addScalar("addrCity").addScalar("addrPersAddrStLn1").addScalar("addrPersAddrStLn2")
				.addScalar("addrPersAddrAttn").addScalar("cdAddrCounty")
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("txtPersAddrCmnts")
				.addScalar("cdPersAddrLinkType").addScalar("dtPersAddrLinkStart", StandardBasicTypes.DATE)
				.addScalar("dtPersAddrLinkEnd", StandardBasicTypes.DATE).addScalar("indPersAddrLinkPrimary")
				.addScalar("indPersAddrLinkInvalid").addScalar("idAddress", StandardBasicTypes.LONG)
				.addScalar("idAddrPersonLink", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate2", StandardBasicTypes.DATE).addScalar("idPersonMerge", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PersonAddressDetailsDto.class));
		retrievePersonAdresses.setParameter("idPerson", personAddressDetailsReq.getIdPerson());
		if (isIndIntakeExists && personAddressDetailsReq.getIndIntake().equals(ServiceConstants.STRING_IND_Y)) {
			retrievePersonAdresses.setParameter("tsSysTsQuery", personAddressDetailsReq.getTsSysTsQuery());
		}
		personAdressDtlsList = retrievePersonAdresses.list();

		log.debug("Exiting method getPersonAddressDtls in PersonAddressDetailsDaoImpl");
		return personAdressDtlsList;
	}
}
