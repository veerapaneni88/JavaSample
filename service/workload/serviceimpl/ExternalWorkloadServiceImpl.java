/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 10, 2018- 5:22:26 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.workload.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.RCCPWorkloadReq;
import us.tx.state.dfps.service.common.response.RCCPWorkloadRes;
import us.tx.state.dfps.service.workload.dao.ExternalWorkloadDao;
import us.tx.state.dfps.service.workload.service.ExternalWorkloadService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 10, 2018- 5:22:26 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
public class ExternalWorkloadServiceImpl implements ExternalWorkloadService {

	@Autowired
	ExternalWorkloadDao externalWorkloadDao;

	@Autowired
	StageDao stageDao;

	/**
	 * 
	 * Method Name: getExternalWorkloadDetails Method Description:
	 * 
	 * @param rccpWorkloadReq
	 * @return RCCPWorkloadRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RCCPWorkloadRes getExternalWorkloadDetails(RCCPWorkloadReq rccpWorkloadReq) {

		RCCPWorkloadRes rccpWorkloadRes = new RCCPWorkloadRes();
		rccpWorkloadRes.setRccpWorkLoadDtos(
				externalWorkloadDao.getExternalWorkloadDetails(rccpWorkloadReq.getRccpWorkLoadDto()));
		return rccpWorkloadRes;

	}

	/**
	 * 
	 * Method Name: getStageDetails Method Description:
	 * 
	 * @param rccpWorkloadReq
	 * @return RCCPWorkloadRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RCCPWorkloadRes getStageDetails(RCCPWorkloadReq rccpWorkloadReq) {

		RCCPWorkloadRes rccpWorkloadRes = new RCCPWorkloadRes();
		rccpWorkloadRes.setSatgeDto(stageDao.getStageById(rccpWorkloadReq.getRccpWorkLoadDto().getIdStage()));
		return rccpWorkloadRes;

	}

	/**
	 * Method Name: searchWorkload Method Description:
	 * 
	 * @param rccpWorkloadReq
	 * @return RCCPWorkloadRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RCCPWorkloadRes searchWorkload(RCCPWorkloadReq rccpWorkloadReq) {
		RCCPWorkloadRes rccpWorkloadRes = new RCCPWorkloadRes();
		rccpWorkloadRes.setRccpWorkLoadDtos(
				externalWorkloadDao.searchExternalWorkloadDetails(rccpWorkloadReq.getRccpWorkLoadDto()));
		return rccpWorkloadRes;
	}

}
