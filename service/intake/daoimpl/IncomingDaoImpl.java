package us.tx.state.dfps.service.intake.daoimpl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.IncomingDetail;
import us.tx.state.dfps.service.common.request.PriorityClosureSaveReq;
import us.tx.state.dfps.service.intake.dao.IncomingDao;
import us.tx.state.dfps.service.person.dao.PersonDao;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Mar 30, 2017 - 4:15:27 PM
 */
@Repository
public class IncomingDaoImpl implements IncomingDao {

	@Autowired
	PersonDao personDao;

	@Autowired
	private SessionFactory sessionFactory;
	private static final Logger log = Logger.getLogger(IncomingDaoImpl.class);

	public IncomingDaoImpl() {

	}

	/**
	 * 
	 * Method Description: Method for Priority Closure Save/Close. This Updates
	 * IncomingDetail.
	 * 
	 * @param PriorityClosureSaveReq
	 * @return @ Tuxedo Service Name: CINT21S
	 */
	@Override
	public void updateIncomingDetail(IncomingDetail incomingDetail, PriorityClosureSaveReq priorityClosureSaveReq) {

		IncomingDetail incomingDetailUpdate = new IncomingDetail();
		Criteria incomingCriteria = sessionFactory.getCurrentSession().createCriteria(IncomingDetail.class);
		incomingCriteria.add(Restrictions.eq("idStage", priorityClosureSaveReq.getPriorityClosureDto().getIdStage()));
		incomingDetailUpdate = (IncomingDetail) incomingCriteria.uniqueResult();
		incomingDetailUpdate.getCapsResource();
		incomingDetailUpdate.setCdIncmgStatus(incomingDetail.getCdIncmgStatus());
		incomingDetailUpdate.setDtLastUpdate(new Date());
		//artf132997 : person list not displaying
		incomingDetailUpdate.setDtIncomingCallDisposed(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(incomingDetailUpdate));

		log.info("TransactionId :" + priorityClosureSaveReq.getTransactionId());
	}

}
