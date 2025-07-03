package us.tx.state.dfps.service.subcare.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.SSCCExceptCareDesignationDto;
import us.tx.state.dfps.service.common.request.ExceptionalCareReq;
import us.tx.state.dfps.service.common.response.ExceptionalCareRes;
import us.tx.state.dfps.service.subcare.dao.ExceptionalCareDao;
import us.tx.state.dfps.service.subcare.dto.ExceptionalCareDto;
import us.tx.state.dfps.service.subcare.service.ExceptionalCareService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is the
 * Service class for Exceptional Care. Apr 12, 2018- 6:02:07 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class ExceptionalCareServiceImpl implements ExceptionalCareService {

	@Autowired
	ExceptionalCareDao exceptionalCareDao;

	/**
	 * 
	 * Method Name: displayExceptCareList Method Description: method to get the
	 * list of exception care
	 * 
	 * @param req
	 * @return
	 */
	@Override
	public ExceptionalCareRes displayExceptCareList(ExceptionalCareReq req) {
		ExceptionalCareRes res = new ExceptionalCareRes();
		List<ExceptionalCareDto> exceptionalCareDtoList = exceptionalCareDao.displayExceptCareList(req);
		res.setExceptionalCareDtoList(exceptionalCareDtoList);
		ExceptionalCareDto exceptionalCareDto = exceptionalCareDao.getPlacementDates(req.getIdPlcmtEvent());
		if (!ObjectUtils.isEmpty(exceptionalCareDto)) {
			res.setDtPlcmtStart(exceptionalCareDto.getDtPlcmtStart());
			res.setDtPlcmtEnd(exceptionalCareDto.getDtPlcmtEnd());
		}
		res.setSsccExceptCareDesignationDto(exceptionalCareDao.getActiveChildPlcmtReferral(req.getIdStage()));
		return res;
	}

	/**
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExceptionalCareRes updateSsccExceptCare(ExceptionalCareReq req) {

		return exceptionalCareDao.updateSsccExceptCare(req);
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExceptionalCareRes updateSsccECStartDate(ExceptionalCareReq req) {
		return exceptionalCareDao.updateSsccECStartDate(req);
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExceptionalCareRes updateSsccECStartEndDates(ExceptionalCareReq req) {
		return exceptionalCareDao.updateSsccECStartEndDates(req);
	}

	/**
	 * Method Name: getPlacementDates Method Description:Gets placement start
	 * and end dates
	 * 
	 * @param idPlcmtEvent
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExceptionalCareRes getPlacementDates(Long idPlcmtEvent) {
		ExceptionalCareRes exceptionalCareRes = new ExceptionalCareRes();
		ExceptionalCareDto exceptionalCareDto = exceptionalCareDao.getPlacementDates(idPlcmtEvent);
		if (!ObjectUtils.isEmpty(exceptionalCareDto)) {
			exceptionalCareRes.setExceptionalCareDto(exceptionalCareDto);
		}
		return exceptionalCareRes;
	}

	/**
	 * Method Name: saveExceptionalCare Method Description: Insert a new record
	 * into SSCC_EXCEPTIONAL_CARE or Updates a record from the same table.
	 * 
	 * @param exceptionalCareDto
	 * @param cdSavetype
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExceptionalCareRes saveExceptionalCare(ExceptionalCareDto exceptionalCareDto, String cdSavetype) {
		return exceptionalCareDao.SaveExceptionalCare(exceptionalCareDto, cdSavetype);

	}

	/**
	 * Method Name: getActiveChildPlcmtReferral Method Description:Gets an
	 * active child sscc referral from the SSCC_REFERRAL table
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExceptionalCareRes getActiveChildPlcmtReferral(Long idStage) {
		ExceptionalCareRes exceptionalCareRes = new ExceptionalCareRes();
		SSCCExceptCareDesignationDto ssccExceptCareDesignationDto = exceptionalCareDao
				.getActiveChildPlcmtReferral(idStage);
		exceptionalCareRes.setSsccExceptCareDesignationDto(ssccExceptCareDesignationDto);

		return exceptionalCareRes;
	}

	/**
	 * Method Name: getExcpCareDays Method Description: Get's number of
	 * exceptional care days in a contract period
	 * 
	 * @param exceptionalCareDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExceptionalCareRes getExcpCareDays(ExceptionalCareDto exceptionalCareDto) {
		ExceptionalCareRes exceptionalCareRes = new ExceptionalCareRes();
		Integer nbrOfDays = exceptionalCareDao.getExcpCareDays(exceptionalCareDto);
		SSCCExceptCareDesignationDto ssccExceptCareDesignationDto = new SSCCExceptCareDesignationDto();
		ssccExceptCareDesignationDto.setNbrTotalBudgetDays(ObjectUtils.isEmpty(nbrOfDays) ? 0 : nbrOfDays);
		exceptionalCareRes.setSsccExceptCareDesignationDto(ssccExceptCareDesignationDto);
		return exceptionalCareRes;
	}

}
