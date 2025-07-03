package us.tx.state.dfps.service.pcgoal.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PCGoalReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PCGoalPrefillData;
import us.tx.state.dfps.service.pcgoal.dao.PCGoalDao;
import us.tx.state.dfps.service.pcgoal.dto.PCGoalDto;
import us.tx.state.dfps.service.pcgoal.dto.PglDetailDto;
import us.tx.state.dfps.service.pcgoal.service.PCGoalService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CCMN56S Mar
 * 5, 2018- 2:07:04 PM © 2017 Texas Department of Family and Protective Services
 */

@Service
@Transactional
public class PCGoalServiceImpl implements PCGoalService {

	@Autowired
	private PCGoalDao pCGoalDao;

	@Autowired
	private PCGoalPrefillData pCGoalPrefillData;

	public PCGoalServiceImpl() {
		super();
	}

	/*
	 * Method Name: getPCGoal Method Description: Populates form ccmn0100, which
	 * outputs Permanency Goal and Concurrent Goal definitions. Definitions are
	 * to be included on the CPOS and FPOS forms.
	 * 
	 * @return PreFillDataServiceDto
	 * 
	 * @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getPCGoal(PCGoalReq pCGoalReq) {

		/*
		 * CCMNP1D: select all rows in static table PERM_GOAL_LOOKUP
		 */

		PCGoalDto pCGoalDto = new PCGoalDto();

		List<PglDetailDto> pglDetailDtoList = pCGoalDao.getPglDetailsDto();
		if (!ObjectUtils.isEmpty(pglDetailDtoList))
			pglDetailDtoList.get(0).setCdPermGoal(ServiceConstants.CONSTANT_SPACE);

		pCGoalDto.setPglDetailDtoList(pglDetailDtoList);

		return pCGoalPrefillData.returnPrefillData(pCGoalDto);
	}

}
