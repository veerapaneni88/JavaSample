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

import us.tx.state.dfps.service.admin.dao.EventPersonLinkMergeViewCountDao;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkMergeViewCountInDto;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkMergeViewCountOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * Retrieves an indicator that the person is involved in an active event. Aug
 * 5,2017- 9:03:15 AM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class EventPersonLinkMergeViewCountDaoImpl implements EventPersonLinkMergeViewCountDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EventPersonLinkMergeViewCountDaoImpl.getRecordCount}")
	private String getRecordCount;

	private static final Logger log = Logger.getLogger(EventPersonLinkMergeViewCountDaoImpl.class);

	public EventPersonLinkMergeViewCountDaoImpl() {
		super();
	}

	/**
	 * Method Name: getRecordCount Method Description: This method will give the
	 * count of rows meeting the criteria.
	 * 
	 * @param eventPersonLinkMergeViewCountInDto
	 * @return List<EventPersonLinkMergeViewCountOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventPersonLinkMergeViewCountOutDto> getRecordCount(
			EventPersonLinkMergeViewCountInDto eventPersonLinkMergeViewCountInDto) {
		log.debug("Entering method EventPersonLinkMergeViewCountQUERYdam in EventPersonLinkMergeViewCountDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRecordCount)
				.addScalar("sysNbrNumberOfRows", StandardBasicTypes.INTEGER)
				.setResultTransformer(Transformers.aliasToBean(EventPersonLinkMergeViewCountOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPerson", eventPersonLinkMergeViewCountInDto.getIdPerson());
		List<EventPersonLinkMergeViewCountOutDto> liCinv79doDto = (List<EventPersonLinkMergeViewCountOutDto>) sQLQuery1
				.list();
		log.debug("Exiting method EventPersonLinkMergeViewCountQUERYdam in EventPersonLinkMergeViewCountDaoImpl");
		return liCinv79doDto;
	}
}
