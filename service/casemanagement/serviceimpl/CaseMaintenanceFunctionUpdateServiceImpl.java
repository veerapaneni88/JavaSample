package us.tx.state.dfps.service.casemanagement.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceFetchEventDtlDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceFetchStageDtlDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceFetchTaskDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSelectStageDao;
import us.tx.state.dfps.service.casemanagement.service.CaseMaintenanceFunctionUpdateService;
import us.tx.state.dfps.service.casepackage.dto.CheckStageEventInDto;
import us.tx.state.dfps.service.casepackage.dto.CheckStageEventOutDto;
import us.tx.state.dfps.service.casepackage.dto.EventRtrvTaskInDto;
import us.tx.state.dfps.service.casepackage.dto.EventRtrvTaskOutDto;
import us.tx.state.dfps.service.casepackage.dto.GetTaskInDto;
import us.tx.state.dfps.service.casepackage.dto.GetTaskOutDto;
import us.tx.state.dfps.service.casepackage.dto.RtrvStageInDto;
import us.tx.state.dfps.service.casepackage.dto.RtrvStageOutDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceFunctionUpdateServiceImpl Feb 7, 2018- 5:53:29 PM
 * © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class CaseMaintenanceFunctionUpdateServiceImpl implements CaseMaintenanceFunctionUpdateService {

	// Cint21d
	@Autowired
	CaseMaintenanceSelectStageDao caseMaintenanceSelectStageDao;

	// Ccmn82d
	@Autowired
	CaseMaintenanceFetchTaskDao caseMaintenanceFetchTaskDao;

	// Clsc71d
	@Autowired
	CaseMaintenanceFetchEventDtlDao caseMaintenanceFetchEventDtlDao;

	// Cses71d
	@Autowired
	CaseMaintenanceFetchStageDtlDao caseMaintenanceFetchStageDtlDao;

	public static final int MAX_NUMBER_OF_EVENTS = 100;
	public static final int NEW_NBR_EVENT_STAT = 0;
	public static final int PROC_NBR_EVENT_STAT = 1;
	public static final int COMP_NBR_EVENT_STAT = 2;
	public static final int PEND_NBR_EVENT_STAT = 3;
	public static final int APRV_NBR_EVENT_STAT = 4;
	public static final String NEW_EVENT_STATUS = "NEW";
	public static final String PROCESS_EVENT_STATUS = "PROC";
	public static final String COMPLETE_EVENT_STATUS = "COMP";
	public static final String PENDING_EVENT_STATUS = "PEND";
	public static final String APPROVE_EVENT_STATUS = "APRV";
	public static final String NULL_STRING = "";
	public static final int INITIAL_PAGE = 1;
	public static final String IND_MULTIPLE_INST_TRUE = "1";
	public static final int YEAR_CLOSED = 4712;

	private static final Logger log = Logger.getLogger(CaseMaintenanceFunctionUpdateServiceImpl.class);

	/**
	 * Method Name: checkStageEventStatus Method Description:check the event
	 * status
	 * 
	 * @param checkStageEventInDto
	 * @param checkStageEventOutDto
	 * @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void checkStageEventStatus(CheckStageEventInDto checkStageEventInDto,
			CheckStageEventOutDto checkStageEventOutDto) {
		log.debug("Entering method checkStageEventStatus in CaseMaintenanceFunctionUpdateServiceImpl");
		int i = 0;
		short sLowestStatus = 0;
		short sTempStatus = 0;
		boolean bStageIsClosed = false;
		StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
		StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
		GetTaskInDto getTaskInDto = new GetTaskInDto();
		GetTaskOutDto getTaskOutDto = new GetTaskOutDto();
		EventRtrvTaskInDto eventRtrvTaskInDto = new EventRtrvTaskInDto();
		EventRtrvTaskOutDto eventRtrvTaskOutDto = new EventRtrvTaskOutDto();

		stageRtrvInDto.setUlIdStage(checkStageEventInDto.getIdStage());
		caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
		if (stageRtrvOutDto != null) {

			if ((!ServiceConstants.ADMIN_REVIEW.equalsIgnoreCase(stageRtrvOutDto.getCdStage()) && !ServiceConstants.FAD_REVIEW.equalsIgnoreCase(stageRtrvOutDto.getCdStage()))
					&& (!TypeConvUtil.isNullOrEmpty(stageRtrvOutDto.getDtStageClose()))) {
				if ((checkStageEventInDto.getCdTask().equalsIgnoreCase("3020"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("9020"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("3520"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("5040"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("2100"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("2310"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("8530"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("7100"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("5640"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("6075"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("4190"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("3290"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("3310"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("3510"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("4370"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("5870"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("7230"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("2375"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("3050"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("8560"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("2385"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("3060"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("9060"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("8570"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("7240"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("5880"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("4380"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("9050"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("3030"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("3040"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("4350"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("4360"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("5850"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("5860"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("7210"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("7220"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("8540"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("8550"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("9840"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("9420"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("2180"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("2730"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("2045"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("2795"))
						|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("2330"))) {
				} else {
					if ((checkStageEventInDto.getCdTask().equalsIgnoreCase("3080"))
							|| (checkStageEventInDto.getCdTask().equalsIgnoreCase("8590"))) {
						retrieveStage(checkStageEventInDto, checkStageEventOutDto, bStageIsClosed);
						if (bStageIsClosed) {
							if (!TypeConvUtil.isNullOrEmpty(bStageIsClosed)) {
							}

						}
					}

					throw new ServiceLayerException("Save Failed: The Stage you are working on has been closed.");
				}
			}

			if (!TypeConvUtil.isNullOrEmpty(checkStageEventInDto.getCdTask())) {
				getTaskInDto.setSzCdTask(checkStageEventInDto.getCdTask());
				caseMaintenanceFetchTaskDao.fetchTaskDtl(getTaskInDto, getTaskOutDto);
				if (getTaskOutDto != null) {

					if (ServiceConstants.REQ_FUNC_CD_ADD == checkStageEventInDto.getReqFuncCd()) {
						eventRtrvTaskInDto.setPageNbr(INITIAL_PAGE);
						eventRtrvTaskInDto.setPageSizeNbr(MAX_NUMBER_OF_EVENTS);
						eventRtrvTaskInDto.setUlIdStage(checkStageEventInDto.getIdStage());
						eventRtrvTaskInDto.setSzCdTask(checkStageEventInDto.getCdTask());
						caseMaintenanceFetchEventDtlDao.fetchEventDtl(eventRtrvTaskInDto, eventRtrvTaskOutDto);
						if (eventRtrvTaskOutDto != null) {

							if (IND_MULTIPLE_INST_TRUE != getTaskOutDto.getIndTaskMultInstance()) {
							}

							if (NULL_STRING != getTaskOutDto.getCdEventStatus()) {
								for (i = 0; i < Integer.parseInt(eventRtrvTaskOutDto.getRowQty()); i++) {
									if (!eventRtrvTaskOutDto.getROWCLSC71DO()[i].getCdEventStatus()
											.equalsIgnoreCase(NEW_EVENT_STATUS)) {
										sTempStatus = NEW_NBR_EVENT_STAT;
									} else if (!eventRtrvTaskOutDto.getROWCLSC71DO()[i].getCdEventStatus()
											.equalsIgnoreCase(PROCESS_EVENT_STATUS)) {
										sTempStatus = PROC_NBR_EVENT_STAT;
									} else if (!eventRtrvTaskOutDto.getROWCLSC71DO()[i].getCdEventStatus()
											.equalsIgnoreCase(COMPLETE_EVENT_STATUS)) {
										sTempStatus = COMP_NBR_EVENT_STAT;
									} else if (!eventRtrvTaskOutDto.getROWCLSC71DO()[i].getCdEventStatus()
											.equalsIgnoreCase(PENDING_EVENT_STATUS)) {
										sTempStatus = PEND_NBR_EVENT_STAT;
									} else if (!eventRtrvTaskOutDto.getROWCLSC71DO()[i].getCdEventStatus()
											.equalsIgnoreCase(APPROVE_EVENT_STATUS)) {
										sTempStatus = APRV_NBR_EVENT_STAT;
									}

									if (NEW_NBR_EVENT_STAT == sTempStatus) {
										i = Integer.parseInt(eventRtrvTaskOutDto.getRowQty());
									}

								}
								if (sTempStatus < sLowestStatus) {
									sLowestStatus = sTempStatus;
								}
								sTempStatus = NEW_NBR_EVENT_STAT;
								if (!getTaskOutDto.getCdEventStatus().equalsIgnoreCase(NEW_EVENT_STATUS)) {
									sTempStatus = NEW_NBR_EVENT_STAT;
								} else if (!getTaskOutDto.getCdEventStatus().equalsIgnoreCase(PROCESS_EVENT_STATUS)) {
									sTempStatus = PROC_NBR_EVENT_STAT;
								} else if (!getTaskOutDto.getCdEventStatus().equalsIgnoreCase(COMPLETE_EVENT_STATUS)) {
									sTempStatus = COMP_NBR_EVENT_STAT;
								} else if (!getTaskOutDto.getCdEventStatus().equalsIgnoreCase(PENDING_EVENT_STATUS)) {
									sTempStatus = PEND_NBR_EVENT_STAT;
								} else if (!getTaskOutDto.getCdEventStatus().equalsIgnoreCase(APPROVE_EVENT_STATUS)) {
									sTempStatus = APRV_NBR_EVENT_STAT;
								}
								if (sLowestStatus < sTempStatus) {
									throw new ServiceLayerException(
											"Save Failed: Another user has made a change in the Stage.");
								}
							}

						}
					}

				}
			}

		}

		log.debug("Exiting method CheckStageEventStatus in CaseMaintenanceFunctionUpdateServiceImpl");
	}

	/**
	 * Method Name: retrieveStage Method Description:fetch stage detail DAM:
	 * CSES71D
	 * 
	 * @param checkStageEventInDto
	 * @param checkStageEventOutDto
	 * @param pbStageIsClosed
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void retrieveStage(CheckStageEventInDto checkStageEventInDto, CheckStageEventOutDto checkStageEventOutDto,
			boolean pbStageIsClosed) {
		log.debug("Entering method retrieveStage in CaseMaintenanceFunctionUpdateServiceImpl");
		RtrvStageInDto rtrvStageInDto = new RtrvStageInDto();
		RtrvStageOutDto rtrvStageOutDto = new RtrvStageOutDto();

		rtrvStageInDto.setIdStage(checkStageEventInDto.getIdStage());
		caseMaintenanceFetchStageDtlDao.fetchStageDtl(rtrvStageInDto, rtrvStageOutDto);
		if (rtrvStageOutDto != null) {
			if ((TypeConvUtil.isNullOrEmpty(rtrvStageOutDto.getDtStageClose()))
					|| (YEAR_CLOSED == rtrvStageOutDto.getDtStageClose().getYear())) {
				pbStageIsClosed = false;
			} else {
				pbStageIsClosed = true;
			}

		}

		log.debug("Exiting method retrieveStage in CaseMaintenanceFunctionUpdateServiceImpl");
	}

	/**
	 * Method Name: updateFunctionalTable Method Description:This is a Common
	 * Function (not a service) which is called by services which update
	 * functional tables. It is packaged in the libappd.a archive library. This
	 * function receives the following inputs: cReqFuncCd, ulIdStage, and
	 * szCdTask. service: Ccmn06u
	 * 
	 * @param checkStageEventInDto
	 * @return CheckStageEventOutDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CheckStageEventOutDto updateFunctionalTable(CheckStageEventInDto checkStageEventInDto) {
		log.debug("Entering method updateFunctionalTable in CaseMaintenanceFunctionUpdateServiceImpl");
		CheckStageEventOutDto checkStageEventOutDto = new CheckStageEventOutDto();
		checkStageEventStatus(checkStageEventInDto, checkStageEventOutDto);

		log.debug("Exiting method updateFunctionalTable in CaseMaintenanceFunctionUpdateServiceImpl");
		return checkStageEventOutDto;
	}

}
