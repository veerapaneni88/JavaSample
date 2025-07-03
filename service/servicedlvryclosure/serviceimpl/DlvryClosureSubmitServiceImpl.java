package us.tx.state.dfps.service.servicedlvryclosure.serviceimpl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.servicedlvryclosure.dao.ServiceDlvryClosureDao;
import us.tx.state.dfps.service.servicedlvryclosure.service.DlvryClosureSubmitService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: dlvry
 * closure submit service class May 21, 2018- 3:44:36 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Service
@Transactional
public class DlvryClosureSubmitServiceImpl implements DlvryClosureSubmitService {

	@Autowired
	ServiceDlvryClosureDao serviceDlvryClosureDao;

	/**
	 * Method Name: getPersonIdInFDTC Method Description: to get person list
	 * 
	 * @param caseId
	 * @return HashMap
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public HashMap<Integer, String> getPersonIdInFDTC(Long caseId) {
		return serviceDlvryClosureDao.getPersonIdInFDTC(caseId);
	}

	/**
	 * Method Name: getMostRecentFDTCSubtype Method Description: to get most
	 * recent fdtc sub type
	 * 
	 * @param personId
	 * @return HashMap
	 */
	@SuppressWarnings("rawtypes")
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public HashMap getMostRecentFDTCSubtype(Long personId) {
		return serviceDlvryClosureDao.getMostRecentFDTCSubtype(personId);

	}

	/**
	 * Method Name: getSDMAssessmentStatus Method Description: This method is to
	 * get SDM Assessment Status
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public boolean getSDMAssessmentStatus(Long idStage) {
		return serviceDlvryClosureDao.getSDMAssessmentCount(idStage);
	}

}
