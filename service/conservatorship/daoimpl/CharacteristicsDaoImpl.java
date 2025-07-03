package us.tx.state.dfps.service.conservatorship.daoimpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Characteristics;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB14S Class
 * Description: This Method extends BaseDao and implements CharacterisitcsDao.
 * This is used to retrieve Characterisitcs details from database. May 1, 2017 -
 * 3:26:39 PM
 */
@Repository
public class CharacteristicsDaoImpl implements CharacteristicsDao {

	@Value("${CharacteristicsDaoImpl.getCharDtls}")
	private String charDtls;

	@Value("${CharacteristicsDaoImpl.getCharactersiticsIdsByPersonId}")
	private String getCharactersiticsIdsByPersonIdSql;

	@Value("${CharacteristicsDaoImpl.getCharByPersonIdAndCategory}")
	private String getCharByPersonIdAndCategory;

	@Value("${CharacteristicsDaoImpl.getCharacteristicDetailsDate}")
	private String getCharacteristicDetailsDate;

	@Value("${CharacteristicsDaoImpl.getCharacteristicDetails}")
	private String getCharacteristicDetails;

	@Value("${CharacteristicsDaoImpl.getCharDtlsForMPS}")
	private String chartDtlsForMPS;

	@Autowired
	MessageSource messageSource;

	@Autowired
	public SessionFactory sessionFactory;

	@Autowired
	private MobileUtil mobileUtil;

	public CharacteristicsDaoImpl() {

	}

	/**
	 * Method Description: This method will give full row select of the
	 * CHARACTERISTICS table for a given date for a given ID PERSON. Service
	 * Name: CSUB14S DAM: CLSS60D
	 * 
	 * @param personId
	 * @param startDate
	 * @param endDate
	 * @return CharacteristicsDto
	 * @ @throws
	 *       ParseException
	 */
	@SuppressWarnings("unchecked")
	public List<CharacteristicsDto> getCharDtls(Long personId, Date startDate, Date endDate) {
		List<CharacteristicsDto> charDtlList = new ArrayList<CharacteristicsDto>();
		String strtDt = DateUtils.formatDatetoString(startDate);
		String endDt = DateUtils.formatDatetoString(endDate);
		String strQuery = charDtls;
		if(mobileUtil.isMPSEnvironment()){
			strQuery = chartDtlsForMPS;
		}
		charDtlList = (List<CharacteristicsDto>) sessionFactory.getCurrentSession().createSQLQuery(strQuery)
				.addScalar("idCharacteristics", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdCharacteristic", StandardBasicTypes.STRING)
				.addScalar("cdCharCategory", StandardBasicTypes.STRING)
				.addScalar("dtCharStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCharEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCharacteristics", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).setParameter("idPrsn", personId)
				.setParameter("dt_CharStrt", strtDt).setParameter("dt_CharEnd", endDt)
				.setResultTransformer(Transformers.aliasToBean(CharacteristicsDto.class)).list();

		return charDtlList;

	}

	/**
	 * This DAM update of PERSON CHARACTERISTICS table. It does a full row add,
	 * or updates the end date. There is no delete functionality.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV48D
	 * 
	 * @param personId
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getCharacteristicsIdsByPersonId(Long personId) {
		List<Long> charaIds = new ArrayList<>();

		Query queryCharacteristics = sessionFactory.getCurrentSession()
				.createSQLQuery(getCharactersiticsIdsByPersonIdSql);
		queryCharacteristics.setParameter("idPerson", personId);
		queryCharacteristics.setParameter("endDate", ServiceConstants.GENERIC_END_DATE);
		charaIds = queryCharacteristics.list();

		return charaIds;
	}

	/**
	 * This DAM update of PERSON CHARACTERISTICS table. It does a full row add,
	 * or updates the end date. There is no delete functionality.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV48D
	 * 
	 * @param characteristics
	 * @
	 */
	@Override
	public void characteristicsSave(Characteristics characteristics) {
		// Transaction tx=sessionFactory.getCurrentSession().beginTransaction();
		sessionFactory.getCurrentSession().save(characteristics);
		// tx.commit();

	}

	/**
	 * This DAM update of PERSON CHARACTERISTICS table. It does a full row add,
	 * or updates the end date. There is no delete functionality.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV48D
	 * 
	 * @param characteristics
	 * @
	 */
	@Override
	public void characteristicsUpdate(Characteristics characteristics) {
		// Transaction tx =
		// sessionFactory.getCurrentSession().beginTransaction();
		sessionFactory.getCurrentSession().update(characteristics);
		// tx.commit();

	}

	/**
	 * This DAM update of PERSON CHARACTERISTICS table. It does a full row add,
	 * or updates the end date. There is no delete functionality.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV48D
	 * 
	 * @param idChara
	 * @return @
	 */
	@Override
	public Characteristics getCharacteristicsById(Long idChara) {
		return (Characteristics) sessionFactory.getCurrentSession().createCriteria(Characteristics.class)
				.add(Restrictions.eq("idCharacteristics", idChara)).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao#
	 * getCharByPersonIdAndCategory(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CharacteristicsDto> getCharByPersonIdAndCategory(Long idPerson, String cdCharCategory) {
		SQLQuery queryCharacteristics = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCharByPersonIdAndCategory).addScalar("idCharacteristics", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdCharacteristic", StandardBasicTypes.STRING)
				.addScalar("dtCharStart", StandardBasicTypes.DATE).addScalar("dtCharEnd", StandardBasicTypes.DATE)
				.addScalar("cdCharCategory", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setParameter("cdCharCategory", cdCharCategory)
				.setResultTransformer(Transformers.aliasToBean(CharacteristicsDto.class)));
		return queryCharacteristics.list();

	}

	/**
	 * 
	 * Method Name: getCharacteristicDetails Method Description:Retrieves the
	 * characteristic details from the CHARACTERISTIC table based on the Person
	 * ID and the effective end characteristic date
	 * 
	 * @param idpersonId
	 * @return List
	 */
	@Override
	public List<CharacteristicsDto> getCharacteristicDetails(Long idpersonId) {

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCharacteristicDetails)
				.setResultTransformer(Transformers.aliasToBean(CharacteristicsDto.class)));

		sQLQuery1.addScalar("idCharacteristics", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idPerson", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtCharStart", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtCharEnd", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("cdCharCategory", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdCharacteristic", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStatus", StandardBasicTypes.STRING);

		sQLQuery1.setParameter("hI_idPerson", idpersonId);
		sQLQuery1.setParameter("hI_characEndDate", ServiceConstants.STAGE_OPEN_DT);

		List<CharacteristicsDto> characteristicsDtoList = (List<CharacteristicsDto>) sQLQuery1.list();

		if (TypeConvUtil.isNullOrEmpty(characteristicsDtoList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("characteristicsDtoList.notFound", null, Locale.US));
		}

		return characteristicsDtoList;
	}

	/**
	 * 
	 * Method Name: getCharacteristicDetailsDate Method Description:Retrieves
	 * the characteristic details from the CHARACTERISTIC table based on the
	 * Person ID, the effective start characteristic date and the effective end
	 * characteristic date.
	 * 
	 * @param idpersonId
	 * @param effectiveDate
	 * @return List
	 */
	@Override
	public List<CharacteristicsDto> getCharacteristicDetailsDate(Long idpersonId, Date effectiveDate) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCharacteristicDetailsDate)
				.setResultTransformer(Transformers.aliasToBean(CharacteristicsDto.class)));

		sQLQuery1.addScalar("idCharacteristics", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idPerson", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtCharStart", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtCharEnd", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("cdCharCategory", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdCharacteristic", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStatus", StandardBasicTypes.STRING);

		sQLQuery1.setParameter("hI_idPerson", idpersonId);
		sQLQuery1.setParameter("dtCharStart", effectiveDate);
		sQLQuery1.setParameter("characEndDate", effectiveDate);

		List<CharacteristicsDto> characteristicsDtoList = (List<CharacteristicsDto>) sQLQuery1.list();

		if (TypeConvUtil.isNullOrEmpty(characteristicsDtoList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("characteristicsDtoList.notFound", null, Locale.US));
		}

		return characteristicsDtoList;
	}
}
