package us.tx.state.dfps.service.casepackage.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.service.SpecialHandlingService;
import us.tx.state.dfps.service.common.request.SpecialHandlingReq;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN81S Class
 * Description: This class is doing service Implementation for Special Handling
 * Mar 23, 2017 - 3:23:07 PM
 */

@Service
@Transactional
public class SpecialHandlingServiceImpl implements SpecialHandlingService {

	@Autowired
	CapsCaseDao capsCaseDao;

	private static final Logger log = Logger.getLogger(SpecialHandlingServiceImpl.class);

	/**
	 * 
	 * Method Description: This Method will retrieve either all or only the
	 * valid rows for a given person id depending upon whether the invalid SYS
	 * IND VALID ONLY indicator is set. Service Name: CCMN81S
	 * 
	 * @param specialHandlingReq
	 * @return CapsCaseDto @
	 */
	@Transactional
	public CapsCaseDto getSpclHndlng(SpecialHandlingReq specialHandlingReq) {

		CapsCaseDto spclHndlngServiceOutput = capsCaseDao.getSpclHndlng(specialHandlingReq);
		log.info("TransactionId :" + specialHandlingReq.getTransactionId());
		return spclHndlngServiceOutput;
	}
}
