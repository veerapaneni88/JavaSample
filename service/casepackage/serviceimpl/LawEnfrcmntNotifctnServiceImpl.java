package us.tx.state.dfps.service.casepackage.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casepackage.dao.LawEnfrcmntNotifctnDao;
import us.tx.state.dfps.service.casepackage.service.LawEnfrcmntNotifctnService;
import us.tx.state.dfps.service.common.request.LawEnfrcmntNotifctnReq;
import us.tx.state.dfps.service.common.response.LawEnfrcmntNotifctnRes;

/**
 * ImpactIntegration - IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * for Law Enforcement Notification July 27, 2017- 9:45:08 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class LawEnfrcmntNotifctnServiceImpl implements LawEnfrcmntNotifctnService {

	private static final Logger LawEnfrcmntNotifctnServiceImp = Logger.getLogger(LawEnfrcmntNotifctnServiceImpl.class);

	@Autowired
	LawEnfrcmntNotifctnDao lawEnfrcmntNotifctnDao;

	public LawEnfrcmntNotifctnServiceImpl() {
	}

	/**
	 * 
	 * Method Description: This Method will add row in the LAW_ENFRCMNT_NOTIFCTN
	 * table given ID_RESOURCE
	 * 
	 * @param LawEnfrcmntNotifctnReq
	 * @return LawEnfrcmntNotifctnRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public LawEnfrcmntNotifctnRes saveLawEnforcementNotifcn(LawEnfrcmntNotifctnReq lawEnfrcmntNotifctnReq) {
		LawEnfrcmntNotifctnRes lawEnfrcmntNotifctnRes = lawEnfrcmntNotifctnDao
				.saveLawEnforcementNotifcn(lawEnfrcmntNotifctnReq);
		LawEnfrcmntNotifctnServiceImp.debug("TransactionId :" + lawEnfrcmntNotifctnReq.getTransactionId());
		return lawEnfrcmntNotifctnRes;
	}

	/**
	 * 
	 * Method Description: This Method will fetch rows in the
	 * LAW_ENFRCMNT_NOTIFCTN table given ID_STAGE
	 * 
	 * @param LawEnfrcmntNotifctnReq
	 * @return LawEnfrcmntNotifctnRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public LawEnfrcmntNotifctnRes getLawEnforcementNotifctnList(LawEnfrcmntNotifctnReq lawEnfrcmntNotifctnReq) {
		LawEnfrcmntNotifctnRes lawEnfrcmntNotifctnRes = lawEnfrcmntNotifctnDao
				.getLawEnforcementNotifList(lawEnfrcmntNotifctnReq);
		LawEnfrcmntNotifctnServiceImp.debug("TransactionId :" + lawEnfrcmntNotifctnReq.getTransactionId());
		return lawEnfrcmntNotifctnRes;
	}

}
