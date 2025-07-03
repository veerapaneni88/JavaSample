package us.tx.state.dfps.service.approval.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.GetResourceHstryReq;
import us.tx.state.dfps.approval.dto.GetResourceHstryRes;
import us.tx.state.dfps.service.approval.dao.GetResourceCountDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 8,
 * 2018- 3:38:32 PM © 2018 Texas Department of Family and Protective Services
 */
@Repository
public class GetResourceCountDaoImpl implements GetResourceCountDao {

	public static final Logger log = Logger.getLogger(GetResourceCountDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${GetResourceCountDaoImpl.getResourceCount}")
	private String getResourceCountsql;

	/**
	 * Method Name: getResourceCount Method Description: Dam Name: CSEC46D
	 */
	@Override
	public GetResourceHstryRes getResourceCount(GetResourceHstryReq getResourceHstryReq) {
		log.debug("Entering method getResourceCount in GetResourceCountDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResourceCountsql)
				.addScalar("sysNbrSvcDtlCount")
				.setString("cdRshsFaHomeStatus", getResourceHstryReq.getCdRshsFaHomeStatus())
				.setResultTransformer(Transformers.aliasToBean(GetResourceHstryRes.class)));

		GetResourceHstryRes getResourceHstryRes = (GetResourceHstryRes) sQLQuery1.list();

		log.debug("Exiting method getResourceCount in GetResourceCountDaoImpl");
		return getResourceHstryRes;
	}

}
