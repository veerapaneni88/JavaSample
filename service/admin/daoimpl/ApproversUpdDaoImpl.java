package us.tx.state.dfps.service.admin.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.ApproversUpdDao;
import us.tx.state.dfps.service.admin.dto.ApproversUpdInDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Execute the
 * DAM that changes the status of all Approvers records on the Approvers Table
 * from PEND to INVD. Aug 8, 2017- 10:47:03 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class ApproversUpdDaoImpl implements ApproversUpdDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ApproversUpdDaoImpl.updateApproverStatus}")
	private String updateApproverStatus;

	private static final Logger log = Logger.getLogger(ApproversUpdDaoImpl.class);

	public ApproversUpdDaoImpl() {
		super();
	}

	/**
	 * Description:This method updates status of all approvers
	 * 
	 * @param pInputDataRec
	 * @return rowCount @
	 */
	@Override
	public int updateApproverStatus(ApproversUpdInDto pInputDataRec) {
		log.debug("Entering method ApproversUpdQUERYdam in ApproversUpdDaoImpl");
		int rowCount = 0;
		switch (pInputDataRec.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateApproverStatus)
					.setParameter("hI_ulIdApproval", pInputDataRec.getIdApproval()));
			rowCount = sQLQuery1.executeUpdate();
			break;
		}
		log.debug("Exiting method ApproversUpdQUERYdam in ApproversUpdDaoImpl");
		return rowCount;
	}
}
