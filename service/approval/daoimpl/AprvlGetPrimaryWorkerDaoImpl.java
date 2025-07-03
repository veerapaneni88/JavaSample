package us.tx.state.dfps.service.approval.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.GetPrimaryWorkerDto;
import us.tx.state.dfps.approval.dto.GetPrimaryWorkerReq;
import us.tx.state.dfps.approval.dto.GetPrimaryWorkerRes;
import us.tx.state.dfps.service.approval.dao.AprvlGetPrimaryWorkerDao;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 11,
 * 2018- 1:09:32 PM © 2018 Texas Department of Family and Protective Services
 */
@Repository
public class AprvlGetPrimaryWorkerDaoImpl implements AprvlGetPrimaryWorkerDao {

	@Value("${AprvlGetPrimaryWorkerDaoImpl.getPrimaryWorkerIdByStage}")
	private String getPrimaryWorkerIdByStagesql;

	@Autowired
	private SessionFactory sessionFactory;

	public static final Logger log = Logger.getLogger(AprvlGetPrimaryWorkerDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public GetPrimaryWorkerRes getPrimaryWorkerIdByStage(GetPrimaryWorkerReq getPrimaryWorkerReq) {
		log.debug("Entering method getPrimaryWorkerIdByStage in AprvlGetPrimaryWorkerDaoImpl");

		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPrimaryWorkerIdByStagesql)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).setString("principal", ServiceConstants.PRINCIPAL)
				.setString("cdStage", ServiceConstants.CSTAGES_ADO)
				.setString("activeStatus", ServiceConstants.ACTIVE_STATUS)
				.setLong("idPerson", getPrimaryWorkerReq.getIdPerson()).setString("primary", ServiceConstants.PRIMARY)
				.setResultTransformer(Transformers.aliasToBean(GetPrimaryWorkerDto.class)));

		GetPrimaryWorkerRes getPrimaryWorkerRes = new GetPrimaryWorkerRes();
		List<GetPrimaryWorkerDto> getPrimaryWorkerDtoList = (List<GetPrimaryWorkerDto>) sqlQuery.list();
		getPrimaryWorkerRes.setGetPrimaryWorkerDtoList(getPrimaryWorkerDtoList);

		log.debug("Exiting method getPrimaryWorkerIdByStage in AprvlGetPrimaryWorkerDaoImpl");
		return getPrimaryWorkerRes;
	}
}
