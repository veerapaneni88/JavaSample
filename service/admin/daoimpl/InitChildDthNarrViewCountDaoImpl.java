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

import us.tx.state.dfps.service.admin.dao.InitChildDthNarrViewCountDao;
import us.tx.state.dfps.service.admin.dto.InitChildDthNarrViewCountInDto;
import us.tx.state.dfps.service.admin.dto.InitChildDthNarrViewCountOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Retrieves
 * all data entry fields Aug 6, 2017- 8:40:20 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class InitChildDthNarrViewCountDaoImpl implements InitChildDthNarrViewCountDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${InitChildDthNarrViewCountDaoImpl.selectCountQuery}")
	private String selectCountQuery;

	private static final Logger log = Logger.getLogger(InitChildDthNarrViewCountDaoImpl.class);

	public InitChildDthNarrViewCountDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getEventRelatedRecords Method Description: This method will
	 * give the count. Cdyn25d
	 * 
	 * @param initChildDthNarrViewCountInDto
	 * @return List<InitChildDthNarrViewCountOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<InitChildDthNarrViewCountOutDto> getEventRelatedRecords(
			InitChildDthNarrViewCountInDto initChildDthNarrViewCountInDto) {
		log.debug("Entering method getEventRelatedRecords in InitChildDthNarrViewCountDaoImpl");
		StringBuilder query = new StringBuilder(selectCountQuery);
		query.append(initChildDthNarrViewCountInDto.getSysTxtTablename());
		query.append(" WHERE ID_EVENT = '");
		query.append(initChildDthNarrViewCountInDto.getIdEvent());
		query.append('\'');
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(query.toString())
				.addScalar("sysNbrUlongKey", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(InitChildDthNarrViewCountOutDto.class)));
		List<InitChildDthNarrViewCountOutDto> initChildDthNarrViewCountOutDtos = (List<InitChildDthNarrViewCountOutDto>) sQLQuery1
				.list();
		log.debug("Exiting method getEventRelatedRecords in InitChildDthNarrViewCountDaoImpl");
		return initChildDthNarrViewCountOutDtos;
	}
}
