package us.tx.state.dfps.service.approval.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.GetPrincipalsReq;
import us.tx.state.dfps.approval.dto.GetPrincipalsRes;
import us.tx.state.dfps.service.approval.dao.GetPrincipalsDao;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 11,
 * 2018- 5:33:32 PM © 2018 Texas Department of Family and Protective Services
 */
@Repository
public class GetPrincipalsDaoImpl implements GetPrincipalsDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${GetPrincipalsDaoImpl.getPrincipals}")
	private String getPrincipalssql;

	public static final Logger log = Logger.getLogger(ApprovalStatusUpdateCapsResourceDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public GetPrincipalsRes getPrincipals(GetPrincipalsReq getPrincipalsResq) {

		log.debug("Entering method getPrincipals in GetPrincipalsDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPrincipalssql)
				.addScalar("idPerson").addScalar("nmPersonFull").setLong("idStage", getPrincipalsResq.getIdStage())
				.setString("principal", ServiceConstants.PRINCIPAL)
				.setResultTransformer(Transformers.aliasToBean(GetPrincipalsRes.class)));

		List<GetPrincipalsRes> getPrincipalsRes = new ArrayList<>();
		getPrincipalsRes = (List<GetPrincipalsRes>) sQLQuery1.list();
		log.debug("Exiting method getPrincipals in GetPrincipalsDaoImpl");
		return (GetPrincipalsRes) getPrincipalsRes;
	}

}
