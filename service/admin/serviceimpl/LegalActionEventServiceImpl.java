package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.LegalActionEventDao;
import us.tx.state.dfps.service.admin.dto.LegalActionEventInDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventOutDto;
import us.tx.state.dfps.service.admin.service.LegalActionEventService;
import us.tx.state.dfps.service.common.request.LegalActionEventReq;
import us.tx.state.dfps.service.common.response.LegalActionEventRes;
import us.tx.state.dfps.service.common.response.LegalActionsRes;
import us.tx.state.dfps.service.workload.dto.StageDto;

@Service
@Transactional
public class LegalActionEventServiceImpl implements LegalActionEventService {

	@Autowired
	LegalActionEventDao legalActionEventDao;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public LegalActionEventRes getMostRecentFDTCSubtype(LegalActionEventReq legalActionEventReq) {
		LegalActionEventRes legalActionEventRes = new LegalActionEventRes();
		List<LegalActionEventOutDto> legalActionEventOutDtoList = legalActionEventDao
				.getMostRecentFDTCSubtype(legalActionEventReq.getLegalActionEventInDto());
		List<LegalActionEventOutDto> legalActionEventOutDtoListRes = new ArrayList<>();
		if(!ObjectUtils.isEmpty(legalActionEventOutDtoList) && 0 < legalActionEventOutDtoList.size()){
			legalActionEventOutDtoListRes.add(legalActionEventOutDtoList.get(0));
		}else{
			legalActionEventOutDtoListRes = legalActionEventOutDtoList;
		}
		legalActionEventRes.setLegalActionEventOutDtoList(legalActionEventOutDtoListRes);
		return legalActionEventRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public LegalActionEventRes getOpenFBSSStage(LegalActionEventReq legalActionEventReq) {
		LegalActionEventRes legalActionEventRes = new LegalActionEventRes();
		List<StageDto> stageDtos = legalActionEventDao.getOpenFBSSStage(legalActionEventReq.getLegalActionEventInDto());
		legalActionEventRes.setStageDtos(stageDtos);
		return legalActionEventRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public LegalActionEventRes getLegalActionRelFictiveKin(LegalActionEventReq legalActionEventReq) {
		LegalActionEventRes legalActionEventRes = new LegalActionEventRes();
		LegalActionEventOutDto legalActionEventOutDto = legalActionEventDao
				.getLegalActionRelFictiveKin(legalActionEventReq.getLegalActionEventInDto());

		legalActionEventRes.setLegalActionEventOutDto(legalActionEventOutDto);
		return legalActionEventRes;
	}

	/**
	 * This is a method to set all input parameters(of the stored procedure).
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public LegalActionsRes executeStoredProc(List<Object> arrayList) {
		return legalActionEventDao.executeStoredProc(arrayList);
	}
	

	/**
	 * Method Name: selectLatestLegalActionOutcome
	 * Method Description: selects the most recent outcome for Legal action
	 *
	 * @param legalActionEventInDto
	 * @return LegalActionEventOutDto
	 */
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public LegalActionEventOutDto selectLatestLegalActionOutcome(LegalActionEventInDto legalActionEventInDto) {
		return legalActionEventDao.selectLatestLegalActionOutcome(legalActionEventInDto);
	}

}
