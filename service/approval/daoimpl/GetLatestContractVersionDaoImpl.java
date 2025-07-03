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

import us.tx.state.dfps.approval.dto.GetLatestContractVersionReq;
import us.tx.state.dfps.approval.dto.GetLatestContractVersionRes;
import us.tx.state.dfps.service.approval.dao.GetLatestContractVersionDao;

@Repository
public class GetLatestContractVersionDaoImpl implements GetLatestContractVersionDao {

	public static final Logger log = Logger.getLogger(ApprovalStatusUpdateCapsResourceDaoImpl.class);

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ApprovalStatusUpdateCapsResourceDaoImpl.getLatestContractVersion}")
	private String getLatestContractVersionsql;

	@SuppressWarnings("unchecked")
	@Override
	public GetLatestContractVersionRes getLatestContractVersion(
			GetLatestContractVersionReq getLatestContractVersionReq) {

		log.debug("Entering method getLatestContractVersion in GetLatestContractVersionDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLatestContractVersionsql)
				.addScalar("idCnver").addScalar("idContract").addScalar("idCntrctWkr").addScalar("nbrCnverPeriod")
				.addScalar("nbrCnverVersion").addScalar("nbrCnverNoShowPct").addScalar("indCnverVerLock")
				.addScalar("txtCnverComment").addScalar("dtCnverCreate").addScalar("dtCnverEffective")
				.addScalar("dtCnverEnd").addScalar("lastUpdate")
				.setLong("nbrCnverPeriod", getLatestContractVersionReq.getNbrCnverPeriod())
				.setLong("idContract", getLatestContractVersionReq.getIdContract())
				.setResultTransformer(Transformers.aliasToBean(GetLatestContractVersionRes.class)));

		List<GetLatestContractVersionRes> getLatestContractVersionRes = new ArrayList<>();
		getLatestContractVersionRes = (List<GetLatestContractVersionRes>) sQLQuery1.list();

		log.debug("Exiting method getLatestContractVersion in GetLatestContractVersionDaoImpl");
		return (GetLatestContractVersionRes) getLatestContractVersionRes;
	}

}
