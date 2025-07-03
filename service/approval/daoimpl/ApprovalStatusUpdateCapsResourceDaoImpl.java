package us.tx.state.dfps.service.approval.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.AprvlStatusUpdateCapsResourceReq;
import us.tx.state.dfps.service.approval.dao.ApprovalStatusUpdateCapsResourceDao;

@Repository
public class ApprovalStatusUpdateCapsResourceDaoImpl implements ApprovalStatusUpdateCapsResourceDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ApprovalStatusUpdateCapsResourceDaoImpl.updateCapsResource}")
	private String updateCapsResourcesql;

	public static final Logger log = Logger.getLogger(ApprovalStatusUpdateCapsResourceDaoImpl.class);

	/**
	 * Method Name: updateCapsResource Method Description:This Method will
	 * Executes the query which updates the Resource table given the Id Event.
	 * DAM NAME: CAUD52D Service Name: CCMN35S
	 * 
	 * @param aprvlStatusUpdateCapsResourceReq
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public void updateCapsResource(AprvlStatusUpdateCapsResourceReq aprvlStatusUpdateCapsResourceReq) {
		log.debug("Entering method updateCapsResource in ApprovalStatusUpdateCapsResourceDaoImpl");

		sessionFactory.getCurrentSession().createSQLQuery(updateCapsResourcesql)
				.setLong("idResource", aprvlStatusUpdateCapsResourceReq.getIdResource())
				.setString("cdRsrcStatus", aprvlStatusUpdateCapsResourceReq.getCdRsrcStatus())
				.setString("cdRsrcFaHomeStatus", aprvlStatusUpdateCapsResourceReq.getCdRsrcFaHomeStatus())
				.setString("personFullname", aprvlStatusUpdateCapsResourceReq.getPersonFullname())
				.executeUpdate();

		log.debug("Exiting method updateCapsResource in ApprovalStatusUpdateCapsResourceDaoImpl");
	}

}
