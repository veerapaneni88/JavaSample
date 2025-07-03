package us.tx.state.dfps.service.snooplog.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.SnoopLogReq;
import us.tx.state.dfps.service.snooplog.dao.SnoopMappingDao;
import us.tx.state.dfps.service.snooplog.service.SnoopMappingService;

@Service
@Transactional
public class SnoopMappingServiceImpl implements SnoopMappingService {

	@Autowired
	SnoopMappingDao snoopMappingDao;

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void storeSnoopMappingLog(SnoopLogReq snoopLogReq) {
		snoopMappingDao.SnoopLogMapping(snoopLogReq);

	}

}
