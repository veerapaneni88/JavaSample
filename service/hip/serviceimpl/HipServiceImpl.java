package us.tx.state.dfps.service.hip.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.request.HipGroupDtlReq;
import us.tx.state.dfps.service.common.request.UpdtRecordsReq;
import us.tx.state.dfps.service.common.response.FindrsRecordsRes;
import us.tx.state.dfps.service.common.response.HipGroupDtlRes;
import us.tx.state.dfps.service.common.response.HipGroupsRes;
import us.tx.state.dfps.service.common.response.UpdtRecordsRes;
import us.tx.state.dfps.service.hip.dao.HipDao;
import us.tx.state.dfps.service.hip.dto.HipFileDtlDto;
import us.tx.state.dfps.service.hip.dto.HipGroupDto;
import us.tx.state.dfps.service.hip.dto.HipPersonDto;
import us.tx.state.dfps.service.hip.service.HipService;

@Service
public class HipServiceImpl implements HipService {

	@Autowired
	HipDao hipDao;

	/**
	 * This service is to get all records for state wide intake
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public HipGroupsRes getHipGroups(ServiceReqHeaderDto serviceReqHeaderDto) {
		HipGroupsRes hipGroupsRes = new HipGroupsRes();
		List<HipGroupDto> hipGroups = hipDao.getHipGroups(serviceReqHeaderDto);
		hipGroupsRes.setHipGroups(hipGroups);
		return hipGroupsRes;
	}

	/**
	 * This service is to get the group details for state wide intake
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public HipGroupDtlRes getHipGroupDetail(HipGroupDtlReq hipGroupDtlReq) {
		HipGroupDtlRes hipGroupDtlRes = new HipGroupDtlRes();
		List<HipPersonDto> hipPersons = hipDao.getHipGroupDetail(hipGroupDtlReq);
		hipGroupDtlRes.setHipPersons(hipPersons);
		if (hipPersons.size() > 0)
			hipDao.updtHipGroup(hipGroupDtlReq);
		return hipGroupDtlRes;
	}

	/**
	 * This service is to get all HIP records for FINDRS to match and non match
	 * on the screen, this service is for IMPACT
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FindrsRecordsRes getHipFindrsRecords() {
		List<HipFileDtlDto> records = hipDao.getHipFindrsRecords();
		FindrsRecordsRes findrsRecordsRes = new FindrsRecordsRes();
		findrsRecordsRes.setRecords(records);
		return findrsRecordsRes;
	}

	/**
	 * This service is to update match and non match in HIP tables , when FINDRS
	 * team does their update on the screen.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public UpdtRecordsRes updtHipFindrsRecords(UpdtRecordsReq updtRecordsReq) {
		hipDao.updtHipFindrsRecords(updtRecordsReq);
		if (updtRecordsReq.isSaveandContinue())
			return hipDao.chkFileComp(updtRecordsReq);
		else
			return null;
	}

}
