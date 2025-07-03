/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jun 6, 2018- 2:12:56 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casepackage.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casepackage.dao.CaseSummaryToolDao;
import us.tx.state.dfps.service.casepackage.service.CaseSummaryToolService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CaseSumToolRes;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:The Service
 * Implementation of Case Summary Tool. Jun 6, 2018- 2:12:56 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class CaseSummaryToolServiceImpl implements CaseSummaryToolService {

	@Autowired
	CaseSummaryToolDao caseSummaryToolDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	LookupDao lookupDao;

	/**
	 * 
	 * Method Name: getCaseSumToolPersonList Method Description: Calls Dao get
	 * method to retrieve the person list.
	 * 
	 * @param idStage
	 * @return caseSumToolRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CaseSumToolRes getCaseSumToolPersonList(Long idStage) {
		CaseSumToolRes caseSumToolRes = new CaseSumToolRes();
		List<PersonDto> retrievePersonList = caseSummaryToolDao.getCaseSumToolPersonList(idStage,
				ServiceConstants.STAFF_TYPE);
		caseSumToolRes.setRetrievePersonList(retrievePersonList);
		for (PersonDto personDto : retrievePersonList) {
			personDto.setCdStagePersRelInt(lookupDao.decode(CodesConstant.CRELVICT, personDto.getCdStagePersRelInt()));
		}
		return caseSumToolRes;
	}
}
