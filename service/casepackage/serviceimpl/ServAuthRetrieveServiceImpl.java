package us.tx.state.dfps.service.casepackage.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dto.ServAuthRetrieveDto;
import us.tx.state.dfps.service.casepackage.service.ServAuthRetrieveService;
import us.tx.state.dfps.service.common.request.ServAuthRetrieveReq;
import us.tx.state.dfps.service.exception.ServiceLayerException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCON24S Class
 * Description: This class is doing service Implementation for ServAuthRetrieve
 * Apr 04, 2017 - 5:23:07 PM
 */

@Service
@Transactional
public class ServAuthRetrieveServiceImpl implements ServAuthRetrieveService {

	@Autowired
	ServiceAuthorizationDao servAuthRetrieveDao;

	private static final Logger log = Logger.getLogger(ServAuthRetrieveServiceImpl.class);

	/**
	 * 
	 * Method Description: This Method will retrieval the Service Authorization
	 * APS Detail window based on the input request. Service Name : CCON24S
	 * 
	 * @param servAuthRetrieveReq
	 * @return List<ServAuthRetrieveDto>
	 * @,DataNotFoundException
	 */

	@Transactional
	public List<ServAuthRetrieveDto> getAuthDetails(ServAuthRetrieveReq servAuthRetrieveReq) {

		List<ServAuthRetrieveDto> servAuthRetrieveServiceOutput = new ArrayList<ServAuthRetrieveDto>();

		try {

			servAuthRetrieveServiceOutput = servAuthRetrieveDao.getAuthDetails(servAuthRetrieveReq);

		} catch (ServiceLayerException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.toString());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}
		log.info("TransactionId :" + servAuthRetrieveReq.getTransactionId());
		return servAuthRetrieveServiceOutput;
	}

}