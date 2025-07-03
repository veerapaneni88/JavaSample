package us.tx.state.dfps.service.person.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.UnitDao;
import us.tx.state.dfps.service.common.request.RtrvUnitIdReq;
import us.tx.state.dfps.service.common.response.RtrvUnitIdRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.person.service.RtrvUnitIdService;

/**
 *
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN47S Class
 * Description: This class is doing service Implementation for Retrieving Unit
 * Id Mar 24, 2017 - 4:23:07 PM
 */
@Service
@Transactional
public class RtrvUnitIdServiceImpl implements RtrvUnitIdService {

	@Autowired
	UnitDao rtrvUnitIdDao;

	private static final Logger log = Logger.getLogger(RtrvUnitIdServiceImpl.class);

	public RtrvUnitIdServiceImpl() {
	}

	/**
	 *
	 * Method Description: This Method will retrieve ID UNIT for a Parent Unit,
	 * given CD UNIT PROGRAM, CD UNIT REGION, and NBR UNIT. Service Name:
	 * CCMN47S
	 *
	 * @param rtrvUnitIdReq
	 * @return RtrvUnitIdRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public RtrvUnitIdRes getUnitId(RtrvUnitIdReq rtrvUnitIdReq) {

		return getInternalUnitCandidateId(rtrvUnitIdReq,false);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public RtrvUnitIdRes getUnitCandidateId(RtrvUnitIdReq rtrvUnitIdReq) {

		return getInternalUnitCandidateId(rtrvUnitIdReq,true);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public RtrvUnitIdRes getInternalUnitCandidateId(RtrvUnitIdReq rtrvUnitIdReq,boolean candidateKey) {
		RtrvUnitIdRes rtrvUnitIdRes = new RtrvUnitIdRes();
		ErrorDto errorDto = new ErrorDto();
		UnitDto unitId = (candidateKey)?rtrvUnitIdDao.getUnitCandidateId(rtrvUnitIdReq):rtrvUnitIdDao.getUnitId(rtrvUnitIdReq);

		if (!TypeConvUtil.isNullOrEmpty(unitId)) {
			rtrvUnitIdRes.setUidUnit(unitId.getIdUnit());
		} else {
			errorDto.setErrorMsg(ServiceConstants.ID_PARENT_UNIT_NOT_EXIST);
			errorDto.setErrorCode(ServiceConstants.MSG_CMN_INVALID_PARENT_UNIT);
			rtrvUnitIdRes.setErrorDto(errorDto);
		}
		log.info("TransactionId :" + rtrvUnitIdReq.getTransactionId());
		return rtrvUnitIdRes;
	}
}
