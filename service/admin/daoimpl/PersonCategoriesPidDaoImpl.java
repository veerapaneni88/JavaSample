package us.tx.state.dfps.service.admin.daoimpl;

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

import us.tx.state.dfps.service.admin.dao.PersonCategoriesPidDao;
import us.tx.state.dfps.service.admin.dto.PersonCategoryInDto;
import us.tx.state.dfps.service.admin.dto.PersonCategoryOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * retrieves all the category information for a person. This is a list using id
 * person. Aug 5, 2017- 11:51:57 AM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class PersonCategoriesPidDaoImpl implements PersonCategoriesPidDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonCategoriesPidDaoImpl.getAllCaegoriesForPID}")
	private String getAllCaegoriesForPID;

	private static final Logger log = Logger.getLogger(PersonCategoriesPidDaoImpl.class);

	public PersonCategoriesPidDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getAllCaegoriesForPID Method Description: This method will
	 * get data from PERSON_CATEGORY table. Cinv29d
	 * 
	 * @param personCategoryInDto
	 * @return List<PersonCategoryOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonCategoryOutDto> getAllCaegoriesForPID(PersonCategoryInDto personCategoryInDto) {
		log.debug("Entering method getAllCaegoriesForPID in PersonCategoriesPidDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAllCaegoriesForPID)
				.addScalar("cdCategoryCategory", StandardBasicTypes.STRING)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("idPersonCategory", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PersonCategoryOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPerson", personCategoryInDto.getIdPerson());
		List<PersonCategoryOutDto> personCategoryOutDtos = (List<PersonCategoryOutDto>) sQLQuery1.list();
		log.debug("Exiting method getAllCaegoriesForPID in PersonCategoriesPidDaoImpl");
		return personCategoryOutDtos;
	}
}
