package us.tx.state.dfps.service.approval.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.ResourceHistoryCountReq;
import us.tx.state.dfps.approval.dto.ResourceHistoryCountRes;
import us.tx.state.dfps.service.approval.dao.ResourceHistoryCountDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 15,
 * 2018- 05:25:32 PM © 2018 Texas Department of Family and Protective Services
 */
@Repository
public class ResourceHistoryCountDaoImpl implements ResourceHistoryCountDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ResourceHistoryCountDaoImpl.resourceHistoryCount}")
	private String resourceHistoryCountsql;

	public static final Logger log = Logger.getLogger(ApprovalStatusUpdateCapsResourceDaoImpl.class);

	/**
	 * Method Name:resourceHistoryCount Method Description: This method will
	 * count the resource history based on the ID_RSHS_FA_HOME_STAGE and
	 * CD_RSHS_FA_HOME_STATUS
	 * 
	 * Dam Name: CMSC46D Service Name: CCMN35S
	 * 
	 * @param resourceHistoryCountReq
	 * @return ResourceHistoryCountRes
	 */
	@Override
	public ResourceHistoryCountRes resourceHistoryCount(ResourceHistoryCountReq resourceHistoryCountReq) {
		log.debug("Entering method resourceHistoryCount in ResourceHistoryCountDaoImpl");

		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(resourceHistoryCountsql)
				.addScalar("sysNbrGenericCntr", StandardBasicTypes.LONG)
				.setLong("idRsrcFaHomeStage", resourceHistoryCountReq.getIdRsrcFaHomeStage())
				.setString("cdRshsFaHomeStatus", resourceHistoryCountReq.getCdRshsFaHomeStatus())
				.setResultTransformer(Transformers.aliasToBean(ResourceHistoryCountRes.class)));

		log.debug("Exiting method resourceHistoryCount in ResourceHistoryCountDaoImpl");
		return (ResourceHistoryCountRes) sqlQuery.uniqueResult();
	}

}
