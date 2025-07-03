package us.tx.state.dfps.service.childplanparticipant.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.childplan.dto.ChildPlanLegacyDto;
import us.tx.state.dfps.service.childplanparticipant.dao.ChildPlanParticipantDao;
import us.tx.state.dfps.service.childplanparticipant.service.ChildPlanParticipantService;
import us.tx.state.dfps.service.common.request.ChildPlanParticipantInReq;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Implements
 * services related to ChildPlan. Implementation for Csub36sBean.java Oct 10,
 * 2017- 3:08:36 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ChildPlanParticipantServiceImpl implements ChildPlanParticipantService {

	@Autowired
	private ChildPlanParticipantDao childPlanParticipantDao;

	/**
	 * Method Name: childPlanParticipantFetch Method Description:Gets the child
	 * plan participants
	 * 
	 * @param childPlanParticipantInReq
	 * @return ChildPlanParticipantOutRes @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void fetchChildPlanParticipants(Long idChildPlanEvent, ChildPlanLegacyDto childPlanLegacyDto) {
		childPlanLegacyDto
				.setChildPlanParticipDtoList(childPlanParticipantDao.fetchChildPlanParticipant(idChildPlanEvent));

	}

	/**
	 * Calls methods for inserting and updating Child Plan participant table
	 * 
	 * @param caud27di
	 * @
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long saveOrUpdateChildPlanParticip(ChildPlanParticipantInReq childPlanParticipantInReq) {

		return childPlanParticipantDao
				.saveOrUpdateChildPlanParticip(childPlanParticipantInReq.getChildPlanParticipDto());
	}

	/**
	 * Calls methods for deleting Child Plan participant table
	 * 
	 * @param caud27di
	 * @
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String deleteChildPlanParticip(ChildPlanParticipantInReq childPlanParticipantInReq) {
		return childPlanParticipantDao.deleteChildPlanParticip(childPlanParticipantInReq.getIdChildPlanParticip());

	}
}
