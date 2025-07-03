package us.tx.state.dfps.service.kin.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.kin.dao.KinPlacementInfoDao;
import us.tx.state.dfps.service.kin.dto.KinPlacementInfoValueDto;
import us.tx.state.dfps.service.kin.service.KinPlacementInfoService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is the
 * Bean class used for Placement Information. The Person is checked if he is the
 * Primary Kin Caregiver, If yes, then the resourceId and the resourceName are
 * retrieved. Sep 6, 2017- 3:27:14 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Service
@Transactional
public class KinPlacementInfoServiceImpl implements KinPlacementInfoService {

	@Autowired
	KinPlacementInfoDao kinPlacementInfoDao;

	/**
	 * Gets the Resource Details based on the personId if the person is Primary
	 * Kinship Caregiver.
	 * 
	 * @param personId
	 * @return KinPlacementInfoValueDto @
	 */

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public KinPlacementInfoValueDto getResourceDetails(long personId) {

		return kinPlacementInfoDao.getResourceDetails(personId);
	}

	/**
	 * Gets the status of the home based on the resourceId
	 * 
	 * @param resourceId
	 * @return KinPlacementInfoValueDto @
	 */

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public KinPlacementInfoValueDto getHomeStatus(long resourceId) {
		return kinPlacementInfoDao.getHomeStatus(resourceId);
	}
}
