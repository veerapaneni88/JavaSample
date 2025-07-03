package us.tx.state.dfps.service.casepackage.serviceimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casepackage.dao.LEAgencySearchDao;
import us.tx.state.dfps.service.casepackage.dto.LEAgencySearchDto;
import us.tx.state.dfps.service.casepackage.service.LEAgencySearchService;
import us.tx.state.dfps.service.common.request.LEAgencySearchReq;
import us.tx.state.dfps.service.common.response.LEAgencySearchRes;

@Service
@Transactional
public class LEAgencySearchServiceImpl implements LEAgencySearchService {

	private static final Logger LOG = Logger.getLogger(LEAgencySearchServiceImpl.class);

	@Autowired
	LEAgencySearchDao lEAgencySearchDao;

	/**
	 * 
	 * Method Description: This Method will retrieve all rows from the
	 * CAPS_RESOURCE table given CD_RSRC_TYPE as 'Law Enforcement Agency'
	 * CD_RSRC_STATUS as 'Active' CD_RSRC_STATE as 'Taxes' And one of the
	 * following search criteria nm_resource, addr_rsrc_city or cd_rsrc_cnty
	 * 
	 * @param LEAgencySearchReq
	 * @return LEAgencySearchRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public LEAgencySearchRes getLawEnforcementAgencyList(LEAgencySearchReq laAgencySearchReq) {

		LEAgencySearchRes leAgencySearchRes = new LEAgencySearchRes();
		List<LEAgencySearchDto> leAgencySearchDtoList = lEAgencySearchDao
				.getLawEnforcementAgencyList(laAgencySearchReq);
		leAgencySearchRes.setElAgencySearchDtoList(leAgencySearchDtoList);
		LOG.debug("TransactionId :" + laAgencySearchReq.getTransactionId());
		return leAgencySearchRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve resource email from
	 * RESOURCE_Email table given id_Resource
	 * 
	 * @param leAgencySearchReq
	 * @return List<LEAgencySearchDto> @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<String> getResourceEmail(LEAgencySearchReq leAgencySearchReq) {

		return lEAgencySearchDao.getResourceEmail(leAgencySearchReq);
	}
}
