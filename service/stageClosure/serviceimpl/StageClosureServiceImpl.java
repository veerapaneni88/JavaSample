package us.tx.state.dfps.service.stageClosure.serviceimpl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.stageClosure.dao.StageClosureDao;
import us.tx.state.dfps.service.stageClosure.service.StageClosureService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StageClosureServiceImpl Sep 6, 2017- 6:21:51 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class StageClosureServiceImpl implements StageClosureService {
	@Autowired
	StageClosureDao stageClosureDao;

	/**
	 * Method Name: getPersonIdInFDTC Method Description:This method returns the
	 * list of person that have legal actions of type FDTC for a given case
	 * 
	 * @param caseId
	 * @return HashMap<Integer, String>
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public HashMap<Long, String> getPersonIdInFDTC(Long caseId) {

		HashMap<Long, String> resultHashMap = stageClosureDao.getPersonIdInFDTC(caseId);
		return resultHashMap;

	}

	/**
	 * Method Name: getMostRecentFDTCSubtype Method Description:Returns the most
	 * recent FDTC Subtype and Outcome date for a Person in a given case id
	 * 
	 * @param personId
	 * @return HashMap
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public HashMap<String, String> getMostRecentFDTCSubtype(Long personId) {
		return stageClosureDao.getMostRecentFDTCSubtype(personId);

	}

	/**
	 * Method Name: getRunAwayStatus Method Description: This method is to check
	 * runAway status
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public boolean getRunAwayStatus(Long idPerson) {
		return stageClosureDao.getRunAwayStatus(idPerson);
	}
}
