package us.tx.state.dfps.service.person.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.RtrvSubPersIdsReq;
import us.tx.state.dfps.service.person.dao.EmpSkillsDao;
import us.tx.state.dfps.service.person.service.EmpSkillsService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN47S Class
 * Description: This class is doing service Implementation for Retrieving Unit
 * Id Mar 24, 2017 - 4:23:07 PM
 */
@Service
@Transactional
public class EmpSkillsServiceImpl implements EmpSkillsService {

	@Autowired
	EmpSkillsDao empSkillsDao;

	private static final Logger log = Logger.getLogger(EmpSkillsServiceImpl.class);

	public EmpSkillsServiceImpl() {
	}

	/**
	 * 
	 * Method Description: This Method will retrieve a sub-set of the
	 * ID_PERSON's passed into this service who have the SKILL(S) which are
	 * passed into this method Service Name: CCMN48S
	 * 
	 * @param rtrvSubPersIdsReq
	 * @return List<Long> @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<Long> getPersonIds(RtrvSubPersIdsReq rtrvSubPersIdsReq) {

		List<Long> rtrvSubsetPerIds = new ArrayList<>();
		List<String> rtrvSubsetroles = new ArrayList<>();

		for (Long ulIdPerson : rtrvSubPersIdsReq.getUlIdPerson()) {

			rtrvSubsetroles = empSkillsDao.getPersonIds(ulIdPerson);

			if (rtrvSubsetroles.containsAll(rtrvSubPersIdsReq.getSzCdEmpSkill())) {
				rtrvSubsetPerIds.add(ulIdPerson);
			}

		}

		log.info("TransactionId :" + rtrvSubPersIdsReq.getTransactionId());
		return rtrvSubsetPerIds;

	}

}
