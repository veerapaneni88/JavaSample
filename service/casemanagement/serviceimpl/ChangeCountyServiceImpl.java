package us.tx.state.dfps.service.casemanagement.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateCountyDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateStageCountyDao;
import us.tx.state.dfps.service.casemanagement.service.CaseMaintenanceFunctionUpdateService;
import us.tx.state.dfps.service.casemanagement.service.ChangeCountyService;
import us.tx.state.dfps.service.casepackage.dto.CaseCountyUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.CaseCountyUpdateOutDto;
import us.tx.state.dfps.service.casepackage.dto.CheckStageEventInDto;
import us.tx.state.dfps.service.casepackage.dto.CheckStageEventOutDto;
import us.tx.state.dfps.service.casepackage.dto.StageCountyUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.StageCountyUpdateOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ChangeCountyReq;
import us.tx.state.dfps.service.common.response.ChangeCountyRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ChangeCountyServiceImpl Feb 7, 2018- 5:53:38 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class ChangeCountyServiceImpl implements ChangeCountyService {

	@Autowired
	CaseMaintenanceFunctionUpdateService caseMaintenanceFunctionUpdateService;

	@Autowired
	CaseMaintenanceUpdateStageCountyDao caseMaintenanceUpdateStageCountyDao;

	@Autowired
	CaseMaintenanceUpdateCountyDao caseMaintenanceUpdateCountyDao;

	public static final String NULL_STRING = "";

	private static final Logger log = Logger.getLogger(ChangeCountyServiceImpl.class);

	/**
	 * Method Name: changeCountyService Method Description:This Method is used
	 * to Change County
	 * 
	 * @param changeCountyReq
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ChangeCountyRes changeCountyService(ChangeCountyReq changeCountyReq) {
		log.debug("Entering method callChangeCountyService in ChangeCountyServiceImpl");
		ChangeCountyRes changeCountyRes = new ChangeCountyRes();
		CheckStageEventInDto checkStageEventInDto = new CheckStageEventInDto();
		CheckStageEventOutDto checkStageEventOutDto = new CheckStageEventOutDto();
		if (0L != changeCountyReq.getIdStage()) {
			checkStageEventInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			checkStageEventInDto.setIdStage(changeCountyReq.getIdStage());
			checkStageEventInDto.setCdTask(NULL_STRING);
			caseMaintenanceFunctionUpdateService.checkStageEventStatus(checkStageEventInDto, checkStageEventOutDto);
		}

		updateCountyInStage(changeCountyReq, changeCountyRes);
		if (changeCountyRes != null) {

		}
		if (0L != changeCountyReq.getIdCase()) {
			updateCountyInCase(changeCountyReq, changeCountyRes);
			if (changeCountyRes != null) {

			}
		}

		log.debug("Exiting method callChangeCountyService in ChangeCountyServiceImpl");
		return changeCountyRes;
	}

	/**
	 * Method Name: Method Description:The purpose of this dam (ccmn66dAUDdam)
	 * is to UPDATE the CD_STAGE_CNTY column of the STAGE table, given a
	 * particular ID_STAGE. DAM: CCMN66D
	 * 
	 * @param changeCountyReq
	 * @param changeCountyRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateCountyInStage(ChangeCountyReq changeCountyReq, ChangeCountyRes changeCountyRes) {
		log.debug("Entering method updateCountyInStage in ChangeCountyServiceImpl");
		StageCountyUpdateInDto pCCMN66DInputRec = new StageCountyUpdateInDto();
		StageCountyUpdateOutDto pCCMN66DOutputRec = new StageCountyUpdateOutDto();

		pCCMN66DInputRec.setSzCdStageCnty(changeCountyReq.getCdStageCnty());
		pCCMN66DInputRec.setUlIdStage(changeCountyReq.getIdStage());
		pCCMN66DInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		caseMaintenanceUpdateStageCountyDao.updateStageCounty(pCCMN66DInputRec, pCCMN66DOutputRec);

		log.debug("Exiting method updateCountyInStage in ChangeCountyServiceImpl");
	}

	/**
	 * Method Name: updateCountyInCase Method Description:The purpose of this
	 * dam (ccmn38dAUDdam) is to UPDATE the CD_CASE_COUNTY column of the
	 * CAPS_CASE table, given a particular ID_CASE. DAM: CCMN38D
	 * 
	 * @param changeCountyReq
	 * @param changeCountyRes
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateCountyInCase(ChangeCountyReq changeCountyReq, ChangeCountyRes changeCountyRes) {
		log.debug("Entering method updateCountyInCase in ChangeCountyServiceImpl");
		CaseCountyUpdateInDto caseCountyUpdateInDto = new CaseCountyUpdateInDto();
		CaseCountyUpdateOutDto caseCountyUpdateOutDto = new CaseCountyUpdateOutDto();

		caseCountyUpdateInDto.setCdStageCnty(changeCountyReq.getCdStageCnty());
		caseCountyUpdateInDto.setIdCase(changeCountyReq.getIdCase());
		caseCountyUpdateInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		caseMaintenanceUpdateCountyDao.updateCounty(caseCountyUpdateInDto, caseCountyUpdateOutDto);

		log.debug("Exiting method updateCountyInCase in ChangeCountyServiceImpl");
	}

}
