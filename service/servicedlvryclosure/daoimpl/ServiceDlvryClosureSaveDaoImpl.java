package us.tx.state.dfps.service.servicedlvryclosure.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.servicedlvryclosure.dao.ServiceDlvryClosureSaveDao;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureSaveDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: class to
 * save service dlvry delv decision Jun 4, 2018- 5:06:52 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ServiceDlvryClosureSaveDaoImpl implements ServiceDlvryClosureSaveDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ServiceDlvryClosureDaoImpl.updateSvcDecision}")
	private String updateSvcDecision;

	@Value("${ServiceDlvryClosureDaoImpl.insertSvcDecision}")
	private String insertSvcDecision;

	private static final Logger log = Logger.getLogger("ServiceBusiness-ServiceDlvryClosureDaoImpl");

	/**
	 * Method Name: saveOrUpdatevcDelvDecision Method Description: method to
	 * save delv decision
	 * 
	 * @param dlvryClosureSaveDto
	 * @return int
	 */
	@Override
	public int saveOrUpdatevcDelvDecision(DlvryClosureSaveDto dlvryClosureSaveDto) {
		log.debug("Entering method saveOrUpdatevcDelvDecision in ServiceDlvryClosureDaoImpl");

		int rowCount = 0;
		switch (dlvryClosureSaveDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertSvcDecision)
					.setLong("hI_idStage", dlvryClosureSaveDto.getIdStage())
					.setDate("hI_dtSvcDelvDecision", dlvryClosureSaveDto.getDtSvcDelvDecision()));
			rowCount = sQLQuery.executeUpdate();
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQueryUpdate = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateSvcDecision)
					.setLong("hI_idStage", dlvryClosureSaveDto.getIdStage())
					.setDate("hI_dtSvcDelvDecision", dlvryClosureSaveDto.getDtSvcDelvDecision())
					.setDate("hI_lastUpdate", dlvryClosureSaveDto.getLastUpdate()));
			rowCount = sQLQueryUpdate.executeUpdate();
			break;
		}
		log.debug("Exiting method saveOrUpdatevcDelvDecision in ServiceDlvryClosureDaoImpl");
		return rowCount;
	}

}
