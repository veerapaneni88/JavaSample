package us.tx.state.dfps.service.guardianshipdtl.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.guardianshipdtl.dao.GuardianshipDtlDao;
import us.tx.state.dfps.service.guardianshipdtl.service.GuardianshipDtlService;

@Service
@Transactional
public class GuardianshipDtlServiceImpl implements GuardianshipDtlService {

	@Autowired
	private GuardianshipDtlDao guardianshipDtlDao;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-GuardianshipDtlServiceLog");

	/**
	 * 
	 * Method Name: isDADFinalOutcomeDocumented Method Description:Method checks
	 * if Final Outcome has been documented on Guardian Detail page for Guardian
	 * Type = DAD
	 * 
	 * @param idCase
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isDADFinalOutcomeDocumented(Long idCase) {
		LOG.debug("Entering method isDADFinalOutcomeDocumented in GuardianshipDtlService");
		boolean finalOutcomeNull = false;
		finalOutcomeNull = guardianshipDtlDao.isFinalOutcomeDocumentedForDAD(idCase);
		LOG.debug("Exiting method isDADFinalOutcomeDocumented in GuardianshipDtlService");
		return finalOutcomeNull;

	}

}
