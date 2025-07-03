/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *May 11, 2018- 2:24:32 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.subcare.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.SubVisitationPlanPrefill;
import us.tx.state.dfps.service.subcare.service.SubVisitationPlanService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This method
 * is used to retrieve the information on the subcare Visitation Plan form. May
 * 11, 2018- 2:24:32 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
public class SubVisitationPlanServiceImpl implements SubVisitationPlanService {

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	SubVisitationPlanPrefill subVisitationPlanPrefill;

	/**
	 * 
	 * Method Description:This method is used to retrieve the information Child
	 * Safety Evaluation letter by passing idStage and idEvent as input request
	 * 
	 * @param CommonHelperReq
	 * @return PreFillDataServiceDto
	 * @throws InvalidRequestException
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getSubVisitPlanDetail(CommonHelperReq commonHelperReq) {

		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(commonHelperReq.getIdStage());

		return subVisitationPlanPrefill.returnPrefillData(genericCaseInfoDto);
	}

}
