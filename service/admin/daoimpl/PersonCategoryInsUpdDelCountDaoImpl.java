package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.PersonCategoryInsUpdDelCountDao;
import us.tx.state.dfps.service.admin.dto.PersonCategoryInsUpdDelCountInDto;
import us.tx.state.dfps.service.admin.dto.PersonCategoryInsUpdDelCountOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Update
 * Person category table Aug 11, 2017- 2:36:31 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class PersonCategoryInsUpdDelCountDaoImpl implements PersonCategoryInsUpdDelCountDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(PersonCategoryInsUpdDelCountDaoImpl.class);

	@Value("${PersonCategoryInsUpdDelCountDaoImpl.getPersonCategoryCount}")
	private String getPersonCategoryCount;

	@Value("${PersonCategoryInsUpdDelCountDaoImpl.insertPersonCategory}")
	private String insertPersonCategory;

	@Value("${PersonCategoryInsUpdDelCountDaoImpl.updatePersonCategory}")
	private String updatePersonCategory;

	public PersonCategoryInsUpdDelCountDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: updatePersonCategory Method Description: Update Person
	 * Category record
	 * 
	 * @param personCategoryInsUpdDelCountInDto
	 * @return int
	 */
	@Override
	public int updatePersonCategory(PersonCategoryInsUpdDelCountInDto personCategoryInsUpdDelCountInDto) {
		log.debug("Entering method PersonCategoryInsUpdDelCountInDto in PersonCategoryInsUpdDelCountDaoImpl");
		int rowCount = 0;
		switch (personCategoryInsUpdDelCountInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			rowCount = insertPersonCategory(personCategoryInsUpdDelCountInDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			rowCount = updatePersoncategory(personCategoryInsUpdDelCountInDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			break;
		}
		log.debug("Exiting method PersonCategoryInsUpdDelCountInDto in PersonCategoryInsUpdDelCountDaoImpl");
		return rowCount;
	}

	/**
	 * Method Name: updatePersoncategory Method Description: update a record in
	 * Person category
	 * 
	 * @param personCategoryInsUpdDelCountInDto
	 * @return int
	 */
	private int updatePersoncategory(PersonCategoryInsUpdDelCountInDto personCategoryInsUpdDelCountInDto) {
		int rowCount;
		SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updatePersonCategory)
				.setParameter("hI_szCdCategoryCategory", personCategoryInsUpdDelCountInDto.getCdCategoryCategory())
				.setParameter("hI_ulIdPerson", personCategoryInsUpdDelCountInDto.getIdPerson()).setParameter(
						"hI_szWcdCdPersonCategory2", personCategoryInsUpdDelCountInDto.getWcdCdPersonCategory2()));
		rowCount = sQLQuery3.executeUpdate();
		return rowCount;
	}

	/**
	 * Method Name: insertPersonCategory Method Description: Insert a new record
	 * in Person category Ccmnc2d
	 * 
	 * @param personCategoryInsUpdDelCountInDto
	 * @return int
	 */
	private int insertPersonCategory(PersonCategoryInsUpdDelCountInDto personCategoryInsUpdDelCountInDto) {
		long h_ulCategoryCount = 0;
		int rowCount = 0;
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonCategoryCount)
				.addScalar("categoryCount", StandardBasicTypes.LONG)
				.setParameter("hI_szCdCategoryCategory", personCategoryInsUpdDelCountInDto.getCdCategoryCategory())
				.setParameter("hI_ulIdPerson", personCategoryInsUpdDelCountInDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(PersonCategoryInsUpdDelCountOutDto.class)));
		List<PersonCategoryInsUpdDelCountOutDto> personCategoryInsUpdDelCountOutDtos = (List<PersonCategoryInsUpdDelCountOutDto>) sQLQuery1
				.list();
		if (null != personCategoryInsUpdDelCountOutDtos && personCategoryInsUpdDelCountOutDtos.size() > 0) {
			h_ulCategoryCount = personCategoryInsUpdDelCountOutDtos.get(0).getCategoryCount();
		}
		if (0 == h_ulCategoryCount) {
			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertPersonCategory)
					.setParameter("hI_szCdCategoryCategory", personCategoryInsUpdDelCountInDto.getCdCategoryCategory())
					.setParameter("hI_ulIdPerson", personCategoryInsUpdDelCountInDto.getIdPerson()));
			rowCount = sQLQuery2.executeUpdate();
		}
		return rowCount;
	}
}
