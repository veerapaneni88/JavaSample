package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.service.casepackage.dao.CFMgnmtListDao;
import us.tx.state.dfps.service.casepackage.dto.CFMgmntDto;
import us.tx.state.dfps.service.common.request.CFMgmntReq;
import us.tx.state.dfps.service.common.response.CFMgmntRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CFMgnmtListDaoImpl Sep 6, 2017- 1:00:10 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class CFMgnmtListDaoImpl implements CFMgnmtListDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${CFMgnmtListDaoImpl.querySkpTrnInfo}")
	private String querySkpTrnInfo;

	@Value("${CFMgnmtListDaoImpl.getCFMgmntInfo}")
	private String getCFMgmntInfoQuery;

	private static final Logger log = Logger.getLogger("ServiceBusiness-CFMgnmtListDaoImpl");

	/**
	 * 
	 * Method Name: getCFMgmntInfo Method Description: Get CF Management
	 * information
	 * 
	 * @param searchReq
	 * @return CFMgmntDto
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public CFMgmntRes getCFMgmntInfo(CFMgmntReq searchReq) {

		CFMgmntRes cfMgmntRes = new CFMgmntRes();
		cfMgmntRes.setCfMgmntDto(new CFMgmntDto());
		List<CFMgmntDto> cfMgmntDtos = new ArrayList<CFMgmntDto>();
		Queue<Long> caseIdQueue = new LinkedList<Long>();
		caseIdQueue.add(searchReq.getCfMgmntDto().getCaseId());
		while (!caseIdQueue.isEmpty()) {
			SQLQuery query = sessionFactory.openSession().createSQLQuery(getCFMgmntInfoQuery);
			query.setParameter("idCase", caseIdQueue.poll());
			query.addScalar("caseId", StandardBasicTypes.LONG);
			query.addScalar("locatingInformation", StandardBasicTypes.STRING);
			query.setResultTransformer(Transformers.aliasToBean(CFMgmntDto.class));
			List<CFMgmntDto> data = query.list();
			for (CFMgmntDto object : data) {
				if (!StringUtils.isEmpty(object.getLocatingInformation())) {
					cfMgmntDtos.add(object);
					caseIdQueue.offer(object.getCaseId());
				}

			}
		}
		cfMgmntRes.getCfMgmntDto().setLocatingInfo(cfMgmntDtos);
		return cfMgmntRes;

	}

	/**
	 * 
	 * Method Name: removeDuplicateRecord Method Description: Get skp
	 * Transaction information
	 * 
	 * @param cfMgmntReq
	 * @return LinkedHashMap
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public LinkedHashMap getSkpTrnInfo(CFMgmntReq cfMgmntReq) {
		log.debug("in getSkpTrnInfo method");
		LinkedHashMap skpTrnInfoHashMapFromDb = new LinkedHashMap();
		List bindVariablesVector = new ArrayList();

		bindVariablesVector.add(cfMgmntReq.getCfMgmntDto().getCaseId());

		SQLQuery query = sessionFactory.openSession().createSQLQuery(querySkpTrnInfo);
		query.setParameter("idCase", cfMgmntReq.getCfMgmntDto().getCaseId());
		query.addScalar("caseId", StandardBasicTypes.LONG);
		query.addScalar("txtAddSkpTrn", StandardBasicTypes.STRING);

		query.setResultTransformer(Transformers.aliasToBean(CFMgmntDto.class));

		List<CFMgmntDto> data = query.list();
		for (CFMgmntDto object : data) {

			skpTrnInfoHashMapFromDb.put(object.getCaseId(), object.getTxtAddSkpTrn());

		}
		log.debug("in getSkpTrnInfo method" + skpTrnInfoHashMapFromDb);
		return skpTrnInfoHashMapFromDb;
	}

}
