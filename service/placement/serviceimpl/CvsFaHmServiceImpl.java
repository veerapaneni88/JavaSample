package us.tx.state.dfps.service.placement.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.response.CvsFaHmRes;
import us.tx.state.dfps.service.placement.dao.CvsFaHmDao;
import us.tx.state.dfps.service.placement.dto.CvsFaHomeValueDto;
import us.tx.state.dfps.service.placement.service.CvsFaHmService;

@Service
@Transactional
public class CvsFaHmServiceImpl implements CvsFaHmService {

	@Autowired
	CvsFaHmDao cvsFaHmDao;

	/*
	 * This method saves the CvsfaHome page details in person detail table by
	 * calling the below method.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CvsFaHmRes updatePersonDetail(CvsFaHomeValueDto cvsFaHomeValueDto){

		CvsFaHmRes cvsFaHmRes = new CvsFaHmRes();
		cvsFaHmRes.setTotalRecCount(cvsFaHmDao.updatePersonDetail(cvsFaHomeValueDto));
		return cvsFaHmRes;
	}

	/*
	 * This method inserts the CvsfaHome page details if the record does not
	 * exist
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CvsFaHmRes insertIntoPersonDetail(CvsFaHomeValueDto cvsFaHomeValueDto){

		CvsFaHmRes cvsFaHmRes = new CvsFaHmRes();
		cvsFaHmRes.setTotalRecCount(cvsFaHmDao.insertIntoPersonDetail(cvsFaHomeValueDto));
		return cvsFaHmRes;
	}

	/*
	 * This method saves Primary careGsiver information on CvsfaHome page
	 * details in stage person link table.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CvsFaHmRes updatePrimaryKinshipIndicator(CvsFaHomeValueDto cvsFaHomeValueDto) {

		CvsFaHmRes cvsFaHmRes = new CvsFaHmRes();
		cvsFaHmRes.setTotalRecCount(cvsFaHmDao.updatePrimaryKinshipIndicator(cvsFaHomeValueDto));
		return cvsFaHmRes;
	}

	/*
	 * This method updates resource name in caps resource table,Primary Kinship
	 * caregiver indicator in Stage Person Link table,and resource id in caps
	 * placement table if the primary kinship care giver check box is checked.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CvsFaHmRes updateKinIndPersonNameResourceId(CvsFaHomeValueDto cvsFaHomeValueDto){
		CvsFaHmRes cvsFaHmRes = new CvsFaHmRes();
		cvsFaHmDao.updateCapsResourceName(cvsFaHomeValueDto);
		cvsFaHmDao.updatePrimaryKinshipIndicator(cvsFaHomeValueDto);

		cvsFaHmRes.setTotalRecCount(cvsFaHmDao.updateResourceId(cvsFaHomeValueDto));
		return cvsFaHmRes;
	}

}