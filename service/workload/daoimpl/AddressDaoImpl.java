/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Sep 20, 2017- 12:09:23 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.workload.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.workload.dao.AddressDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Sep 20, 2017- 12:09:23 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class AddressDaoImpl implements AddressDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${AddressDaoImpl.fetchCurrentPrimaryAddress}")
	private String fetchCurrentPrimaryAddress;

	@Value("${AddressDaoImpl.fetchCurrentPrimaryAddress1}")
	private String fetchCurrentPrimaryAddresssql;

	@Autowired
	MessageSource messageSource;

	@Value("${AddressDaoImpl.isRmRsAddressExist}")
	private transient String isRmRsAddressExist;

	@Value("${AddressDaoImpl.fetchCurrentPrimaryAddress1}")
	private transient String fetchCurrentPrimaryAddressSql;

	/**
	 * Method Name: isRmRsAddressExist Method Description: This method checks if
	 * any open address of following address types exists Residence-Mailing,
	 * Residence
	 * 
	 * @param personId
	 * @return Boolean
	 */
	@Override
	public Boolean isRmRsAddressExist(long personId) {

		Query queryContact = sessionFactory.getCurrentSession().createSQLQuery(isRmRsAddressExist)
				.setParameter("idPerson", personId);

		BigDecimal count = (BigDecimal) queryContact.uniqueResult();

		if (count.longValue() > ServiceConstants.LongZero) {
			return Boolean.TRUE;

		}
		return Boolean.FALSE;

	}

	/**
	 * Method Name: fetchCurrentPrimaryAddress Method Description: This method
	 * fetches the current primary address for a person
	 * 
	 * @param idForwardPerson
	 * @return AddressValueDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AddressValueDto fetchCurrentPrimaryAddress(Long idForwardPerson) {
		// Warranty Defect Fix - 12092 - To fetch the List and then get the Latest record
		AddressValueDto currentPrimaryAddressDto=new AddressValueDto();		
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchCurrentPrimaryAddressSql)
				.addScalar("zip", StandardBasicTypes.STRING).addScalar("state", StandardBasicTypes.STRING)
				.addScalar("city", StandardBasicTypes.STRING).addScalar("streetLn1", StandardBasicTypes.STRING)
				.addScalar("streetLn2", StandardBasicTypes.STRING).addScalar("county", StandardBasicTypes.STRING)
				.setParameter("idPerson", idForwardPerson)
				.setResultTransformer(Transformers.aliasToBean(AddressValueDto.class));		
		List<AddressValueDto> addressValueDtoList=sqlQuery.list();
		if(!ObjectUtils.isEmpty(addressValueDtoList))
		{
			currentPrimaryAddressDto=addressValueDtoList.get(0);
		};
		return currentPrimaryAddressDto;
	}
	
	
	
	/**
	 * Method Name: fetchCurrentPrimaryAddressList Method Description: This method
	 * fetches the current primary address for a person
	 * 
	 * @param idForwardPerson
	 * @return AddressValueDto @
	 */
	// Added a new Method for Fetching AddressList - Warranty Defect 10792
	@SuppressWarnings("unchecked")
	@Override
	public List<AddressValueDto> fetchCurrentPrimaryAddressList(Long idForwardPerson) {
		List<AddressValueDto> addressValueDtoList=new ArrayList<AddressValueDto>();
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchCurrentPrimaryAddressSql)
				.addScalar("zip", StandardBasicTypes.STRING).addScalar("state", StandardBasicTypes.STRING)
				.addScalar("city", StandardBasicTypes.STRING).addScalar("streetLn1", StandardBasicTypes.STRING)
				.addScalar("streetLn2", StandardBasicTypes.STRING).addScalar("county", StandardBasicTypes.STRING)
				.setParameter("idPerson", idForwardPerson)
				.setResultTransformer(Transformers.aliasToBean(AddressValueDto.class));
		addressValueDtoList=(List<AddressValueDto>) sqlQuery.list();
		return addressValueDtoList;
	}

	/**
	 * 
	 * Method Name: fetchCurrentPrimaryAddress Method Description: This method
	 * fetches the current primary address for a person from snapshot tables
	 * (SS_ADDRESS_PERSON_LINK, SS_PERSON_ADDRESS) This is used for showing
	 * Select Person Forward data in Post person merge page.
	 * 
	 * @param idPerson
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @ @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AddressValueDto fetchCurrentPrimaryAddress(Long idPerson, Long idReferenceData, String cdActionType,
			String cdSnapshotType) {
		List<AddressValueDto> addressValueDtoLst = new ArrayList<AddressValueDto>();
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchCurrentPrimaryAddress)
				.addScalar("personId", StandardBasicTypes.INTEGER).addScalar("streetLn1", StandardBasicTypes.STRING)
				.addScalar("streetLn2", StandardBasicTypes.STRING).addScalar("attention", StandardBasicTypes.STRING)
				.addScalar("city", StandardBasicTypes.STRING).addScalar("state", StandardBasicTypes.STRING)
				.addScalar("county", StandardBasicTypes.STRING).addScalar("zip", StandardBasicTypes.STRING)
				.addScalar("primary", StandardBasicTypes.STRING).addScalar("invalid", StandardBasicTypes.STRING)
				.addScalar("addressType", StandardBasicTypes.STRING).addScalar("endDate", StandardBasicTypes.DATE)
				.addScalar("startDate", StandardBasicTypes.DATE).addScalar("idPersonAddr", StandardBasicTypes.INTEGER)
				.addScalar("idAddrPersonLink", StandardBasicTypes.INTEGER)
				.addScalar("txtAplComments", StandardBasicTypes.STRING)
				.addScalar("idPersonMerge", StandardBasicTypes.INTEGER)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idReferenceData", idReferenceData)
				.setParameter("cdSnapshotType", cdSnapshotType).setParameter("cdActionType", cdActionType)
				.setParameter("idObject", idPerson).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(AddressValueDto.class));
		// Query return more than one record, so change the return type to list
		// and getting the first element from the result - Added this change for
		// warranty defect 12375
		addressValueDtoLst = (List<AddressValueDto>) sqlQuery.list();
		if (!ObjectUtils.isEmpty(addressValueDtoLst) && addressValueDtoLst.size() > ServiceConstants.Zero_INT) {
			return addressValueDtoLst.get(ServiceConstants.Zero_INT);
		}
		return new AddressValueDto();
	}
}
