package us.tx.state.dfps.service.approval.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.RetrievalRegionAndCountyReq;
import us.tx.state.dfps.approval.dto.RetrievalRegionAndCountyRes;
import us.tx.state.dfps.service.approval.dao.RetrievalRegionAndCountyDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 19,
 * 2018- 11:05:32 PM © 2018 Texas Department of Family and Protective Services
 */

@Repository
public class RetrievalRegionAndCountyDaoImpl implements RetrievalRegionAndCountyDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${RetrievalRegionAndCountyDaoImpl.retrievalRegionAndCounty}")
	private String retrievalRegionAndCountysql;

	public static final Logger log = Logger.getLogger(RetrievalRegionAndCountyDaoImpl.class);

	/**
	 * Method Name: retrievalRegionAndCounty Method Description: Region
	 * retrieval from Region/County table. Dam Name: CSES82D Service Name:
	 * CCMN35S
	 * 
	 * @param RetrievalRegionAndCountyReq
	 * @return RetrievalRegionAndCountyRes
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RetrievalRegionAndCountyRes retrievalRegionAndCounty(
			RetrievalRegionAndCountyReq retrievalRegionAndCountyReq) {

		log.debug("Entering method retrievalRegionAndCounty in RetrievalRegionAndCountyDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrievalRegionAndCountysql)
				.addScalar("cdRsrcSvcCnty").addScalar("cdRsrcSvcRegion")
				.setResultTransformer(Transformers.aliasToBean(RetrievalRegionAndCountyRes.class)));

		List<RetrievalRegionAndCountyRes> retrievalRegionAndCountyRes = new ArrayList<>();
		retrievalRegionAndCountyRes = (List<RetrievalRegionAndCountyRes>) sQLQuery1.list();

		log.debug("Exiting method retrievalRegionAndCounty in RetrievalRegionAndCountyDaoImpl");
		return (RetrievalRegionAndCountyRes) retrievalRegionAndCountyRes;
	}

}
