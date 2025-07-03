package us.tx.state.dfps.service.medicalconsenter.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.medcareconsenter.dto.PersonPhoneMedCareDto;
import us.tx.state.dfps.service.medicalconsenter.dao.NamePersonPhoneDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:NamePersonPhoneDaoImpl to get the information of Phone records.
 * Feb 9, 2018- 1:45:51 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class NamePersonPhoneDaoImpl implements NamePersonPhoneDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${NamePersonPhoneDaoImpl.getPersonPhoneDtls}")
	private String getPersonPhoneDtls;

	/**
	 * Method Description: This Service is used to fetch the Person's Phone
	 * records using idPerson and Phone Type. DAM Service : CSES29D
	 * 
	 * @param idPerson
	 * @return PersonPhoneMedCareDto @
	 */
	@Override
	public PersonPhoneMedCareDto getPersonPhoneRecords(Long idPerson) {

		PersonPhoneMedCareDto personPhoneBusiness;

		personPhoneBusiness = getPhoneRecords(idPerson, ServiceConstants.BUSINESS_PHONE);
		if (TypeConvUtil.isNullOrEmpty(personPhoneBusiness)) {
			PersonPhoneMedCareDto personPhoneCell = getPhoneRecords(idPerson, ServiceConstants.BUSINESS_CELL);
			if (!TypeConvUtil.isNullOrEmpty(personPhoneCell)) {
				personPhoneBusiness = personPhoneCell;

			}
		}

		return personPhoneBusiness;
	}

	/**
	 * Method Description: This Service is used to fetch the Person's Phone
	 * records using idPerson and Phone Type.
	 * 
	 * @param idPerson,
	 *            phoneType
	 * @return PersonPhoneMedCareDto @
	 */
	private PersonPhoneMedCareDto getPhoneRecords(Long idPerson, String phoneType) {
		// Changed to List for Warranty Defect - 10851
		List<PersonPhoneMedCareDto> personPhoneMedCareDtoCellList = new ArrayList<PersonPhoneMedCareDto>();
		PersonPhoneMedCareDto personPhoneMedCareDtoCell = null;
		try {
			personPhoneMedCareDtoCellList = (List<PersonPhoneMedCareDto>) sessionFactory.getCurrentSession()
					.createSQLQuery(getPersonPhoneDtls).addScalar("idPhone", StandardBasicTypes.LONG)
					.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("txtPhoneComments", StandardBasicTypes.STRING)
					.addScalar("nbrPhoneExtension", StandardBasicTypes.STRING)
					.addScalar("nbrPhone", StandardBasicTypes.STRING)
					.addScalar("dtPersonPhoneStart", StandardBasicTypes.DATE)
					.addScalar("dtPersonPhoneEnd", StandardBasicTypes.DATE)
					.addScalar("indPersonPhoneInvalid", StandardBasicTypes.STRING)
					.addScalar("indPersonPhonePrimary", StandardBasicTypes.STRING)
					.addScalar("cdPhoneType", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
					.setParameter("cdPhoneType", phoneType)
					.setResultTransformer(Transformers.aliasToBean(PersonPhoneMedCareDto.class)).list();
			
			// Changed to List for Warranty Defect - 10851
			if(!ObjectUtils.isEmpty(personPhoneMedCareDtoCellList))
			{
				personPhoneMedCareDtoCell= personPhoneMedCareDtoCellList.get(0);
			}
			
		} catch (Exception e) {
			DataLayerException dataException = new DataLayerException(e.getMessage());
			dataException.initCause(e);
			throw dataException;
		}

		return personPhoneMedCareDtoCell;
	}

}
