package us.tx.state.dfps.service.casemanagement.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceFetchPersonDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceRetrieveStageDtlDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSaveNewCaseNameDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSetStageDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateStageDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateStageNameDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateToDoDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceVerifyStagePersonLinkDao;
import us.tx.state.dfps.service.casemanagement.dao.NameResourceUpdateDao;
import us.tx.state.dfps.service.casemanagement.service.CaseMaintenanceEventUpdateService;
import us.tx.state.dfps.service.casemanagement.service.CaseMaintenanceFunctionUpdateService;
import us.tx.state.dfps.service.casemanagement.service.SaveCaseNameService;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseResourceUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseResourceUpdateOutDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseUpdateOutDto;
import us.tx.state.dfps.service.casepackage.dto.CheckStageEventInDto;
import us.tx.state.dfps.service.casepackage.dto.CheckStageEventOutDto;
import us.tx.state.dfps.service.casepackage.dto.CompleteToDoInDto;
import us.tx.state.dfps.service.casepackage.dto.CompleteToDoOutDto;
import us.tx.state.dfps.service.casepackage.dto.PostEventInDto;
import us.tx.state.dfps.service.casepackage.dto.PostEventOutDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveStageInDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveStageOutDto;
import us.tx.state.dfps.service.casepackage.dto.RowEventInDto;
import us.tx.state.dfps.service.casepackage.dto.RtrvPersonInfoInDto;
import us.tx.state.dfps.service.casepackage.dto.RtrvPersonInfoOutDto;
import us.tx.state.dfps.service.casepackage.dto.SaveStageInDto;
import us.tx.state.dfps.service.casepackage.dto.SaveStageOutDto;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkRtrvOutDto;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkUpdateOutDto;
import us.tx.state.dfps.service.casepackage.dto.UpdateStagePersonLinkInDto;
import us.tx.state.dfps.service.casepackage.dto.UpdateStagePersonLinkOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SaveCaseNameReq;
import us.tx.state.dfps.service.common.response.SaveCaseNameRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SaveCaseNameServiceImpl Feb 7, 2018- 5:53:53 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class SaveCaseNameServiceImpl implements SaveCaseNameService {

	// Ccmn06u
	@Autowired
	CaseMaintenanceFunctionUpdateService caseMaintenanceFunctionUpdateService;

	// Ccmn01u
	@Autowired
	CaseMaintenanceEventUpdateService caseMaintenanceEventUpdateService;

	// Cinv81d
	@Autowired
	CaseMaintenanceFetchPersonDao caseMaintenanceFetchPersonDao;

	// Caudb5d
	@Autowired
	NameResourceUpdateDao nameResourceUpdateDao;

	// Ccmn14d
	@Autowired
	CaseMaintenanceSaveNewCaseNameDao caseMaintenanceSaveNewCaseNameDao;

	// Ccmne1d
	@Autowired
	CaseMaintenanceRetrieveStageDtlDao caseMaintenanceRetrieveStageDtlDao;

	// Ccmnd8d
	@Autowired
	CaseMaintenanceUpdateStageNameDao caseMaintenanceUpdateStageNameDao;

	@Autowired
	CaseMaintenanceVerifyStagePersonLinkDao caseMaintenanceVerifyStagePersonLinkDao;

	@Autowired
	CaseMaintenanceUpdateStageDao caseMaintenanceUpdateStageDao;

	@Autowired
	CaseMaintenanceSetStageDao caseMaintenanceSetStageDao;

	@Autowired
	CaseMaintenanceUpdateToDoDao caseMaintenanceUpdateToDoDao;

	public static final String SUBCARE_STAGE = "SUB";
	public static final String ADOPTION_STAGE = "ADO";
	public static final String PAL_STAGE = "PAL";
	public static final String POST_ADOPT_STAGE = "PAD";
	public static final String CD_STAGE_FAD = "FAD";
	public static final String COMPLETE = "COMP";
	public static final String CASE_GENERAL = "CAS";
	public static final String TXT_NAME_CHANGE = "Case name change: ";
	public static final String TXT_TO = " to ";
	public static final String NULL_STRING = "";
	public static final short FND_SUCCESS = 0;

	private static final Logger log = Logger.getLogger(SaveCaseNameServiceImpl.class);

	/**
	 * Method Name:saveCaseNameService Method Description: save the given case
	 * name service
	 *
	 * @param saveCaseNameReq
	 * @return SaveCaseNameRes @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SaveCaseNameRes saveCaseNameService(SaveCaseNameReq saveCaseNameReq) {
		log.debug("Entering method callSaveCaseNameService in SaveCaseNameServiceImpl");
		SaveCaseNameRes saveCaseNameRes = new SaveCaseNameRes();
		short RetVal = 0;
		CheckStageEventInDto checkStageEventInDto = new CheckStageEventInDto();
		CheckStageEventOutDto checkStageEventOutDto = new CheckStageEventOutDto();
		RtrvPersonInfoInDto rtrvPersonInfoInDto = new RtrvPersonInfoInDto();
		RtrvPersonInfoOutDto rtrvPersonInfoOutDto = new RtrvPersonInfoOutDto();
		CapsCaseResourceUpdateInDto capsCaseResourceUpdateInDto = new CapsCaseResourceUpdateInDto();
		CapsCaseResourceUpdateOutDto capsCaseResourceUpdateOutDto = new CapsCaseResourceUpdateOutDto();
		PostEventInDto postEventInDto = new PostEventInDto();
		PostEventOutDto postEventOutDto = new PostEventOutDto();
		if (!TypeConvUtil.isNullOrEmpty(saveCaseNameReq.getIdStage())) {
			checkStageEventInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			checkStageEventInDto.setIdStage(saveCaseNameReq.getIdStage());
			checkStageEventInDto.setCdTask(saveCaseNameReq.getCdTask());
			caseMaintenanceFunctionUpdateService.checkStageEventStatus(checkStageEventInDto, checkStageEventOutDto);
			if (checkStageEventOutDto != null) {
				RetVal = FND_SUCCESS;
			}
		}

		if (FND_SUCCESS == RetVal) {
			if ((SUBCARE_STAGE.equalsIgnoreCase(saveCaseNameReq.getCdStage()))
					|| (PAL_STAGE.equalsIgnoreCase(saveCaseNameReq.getCdStage()))
					|| (ADOPTION_STAGE.equalsIgnoreCase(saveCaseNameReq.getCdStage()))
					|| (POST_ADOPT_STAGE.equalsIgnoreCase(saveCaseNameReq.getCdStage()))) {
				updateStageName(saveCaseNameReq, saveCaseNameRes);
				if (!TypeConvUtil.isNullOrEmpty(saveCaseNameReq.getIdNmPerson())) {
					updateStagePersonLink(saveCaseNameReq, saveCaseNameRes);
					updatePersonLink(saveCaseNameReq, saveCaseNameRes);
				}
			} else {
				updateCaseName(saveCaseNameReq, saveCaseNameRes);
				postEventInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				RowEventInDto rowccmn01uig00Dto = new RowEventInDto();
				postEventInDto.setROWCCMN01UIG00(rowccmn01uig00Dto);
				postEventInDto.getROWCCMN01UIG00().setDtEventOccurred(null);
				postEventInDto.getROWCCMN01UIG00().setCdEventStatus(COMPLETE);
				postEventInDto.getROWCCMN01UIG00().setCdEventType(CASE_GENERAL);
				postEventInDto.getROWCCMN01UIG00().setIdPerson((int) saveCaseNameReq.getIdPerson());
				postEventInDto.getROWCCMN01UIG00().setIdStage((int) saveCaseNameReq.getIdStage());
				postEventInDto.getROWCCMN01UIG00().setTxtEventDescr("Case name change: ");
				postEventInDto.getROWCCMN01UIG00().setTxtEventDescr(
						postEventInDto.getROWCCMN01UIG00().getTxtEventDescr() + saveCaseNameReq.getCurrentCaseName());
				postEventInDto.getROWCCMN01UIG00()
						.setTxtEventDescr(postEventInDto.getROWCCMN01UIG00().getTxtEventDescr() + TXT_TO);
				postEventInDto.getROWCCMN01UIG00().setTxtEventDescr(
						postEventInDto.getROWCCMN01UIG00().getTxtEventDescr() + saveCaseNameReq.getNmCase());
				caseMaintenanceEventUpdateService.postEvent(postEventInDto, postEventOutDto);
				retrieveStageDtl(saveCaseNameReq, saveCaseNameRes);
				postEventInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				postEventInDto.getROWCCMN01UIG00().setDtEventOccurred(null);
				postEventInDto.getROWCCMN01UIG00().setCdEventStatus(COMPLETE);
				postEventInDto.getROWCCMN01UIG00().setCdEventType(CASE_GENERAL);
				postEventInDto.getROWCCMN01UIG00().setIdPerson((int) saveCaseNameReq.getIdPerson());
				postEventInDto.getROWCCMN01UIG00().setIdStage((int) saveCaseNameReq.getIdStage());
				postEventInDto.getROWCCMN01UIG00().setTxtEventDescr("Stage name change: ");
				postEventInDto.getROWCCMN01UIG00().setTxtEventDescr(
						postEventInDto.getROWCCMN01UIG00().getTxtEventDescr() + saveCaseNameReq.getCurrentCaseName());
				postEventInDto.getROWCCMN01UIG00()
						.setTxtEventDescr(postEventInDto.getROWCCMN01UIG00().getTxtEventDescr() + TXT_TO);
				postEventInDto.getROWCCMN01UIG00().setTxtEventDescr(
						postEventInDto.getROWCCMN01UIG00().getTxtEventDescr() + saveCaseNameReq.getNmCase());
				caseMaintenanceEventUpdateService.postEvent(postEventInDto, postEventOutDto);
				if (saveCaseNameReq.getIdTodo() != 0L) {
					updateTodo(saveCaseNameReq, saveCaseNameRes);
				}

				if (CD_STAGE_FAD.equalsIgnoreCase(saveCaseNameReq.getCdStage())) {
					rtrvPersonInfoInDto.setUlIdPerson(saveCaseNameReq.getIdPerson());
					caseMaintenanceFetchPersonDao.fetchPersonDtl(rtrvPersonInfoInDto, rtrvPersonInfoOutDto);
					if (saveCaseNameRes != null) {
						capsCaseResourceUpdateInDto.setReqFuncCd(saveCaseNameReq.getReqFuncCd());
						capsCaseResourceUpdateInDto.setSzNmResource(saveCaseNameReq.getNmCase());
						capsCaseResourceUpdateInDto.setSzNmRsrcLastUpdate(rtrvPersonInfoOutDto.getNmPersonFull());
						capsCaseResourceUpdateInDto.setUlIdRsrcFaHomeStage(saveCaseNameReq.getIdStage());
						nameResourceUpdateDao.updateNameResource(capsCaseResourceUpdateInDto,
								capsCaseResourceUpdateOutDto);
						if (capsCaseResourceUpdateOutDto != null) {

						}
					}
				}

			}
		}

		if (RetVal == FND_SUCCESS) {
		}

		log.debug("Exiting method callSaveCaseNameService in SaveCaseNameServiceImpl");
		return saveCaseNameRes;
	}

	/**
	 * Method Name: updateCaseName Method Description:update case name DAM:
	 * CCMN14D
	 *
	 * @param saveCaseNameReq
	 * @param saveCaseNameRes
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateCaseName(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes) {
		log.debug("Entering method updateCaseName in SaveCaseNameServiceImpl");
		CapsCaseUpdateInDto capsCaseUpdateInDto = new CapsCaseUpdateInDto();
		CapsCaseUpdateOutDto capsCaseUpdateOutDto = new CapsCaseUpdateOutDto();

		capsCaseUpdateInDto.setReqFuncCd(saveCaseNameReq.getReqFuncCd());
		capsCaseUpdateInDto.setUlIdCase(saveCaseNameReq.getIdCase());
		capsCaseUpdateInDto.setSzNmCase(saveCaseNameReq.getNmCase());
		caseMaintenanceSaveNewCaseNameDao.saveNewCaseName(capsCaseUpdateInDto, capsCaseUpdateOutDto);
		if (capsCaseUpdateOutDto != null) {

		}

		log.debug("Exiting method updateCaseName in SaveCaseNameServiceImpl");
	}

	/**
	 * Method Name: retrieveStageDtl Method Description:retrieve Stage Detail
	 * DAM: CCMNE1D
	 *
	 * @param saveCaseNameReq
	 * @param saveCaseNameRes
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void retrieveStageDtl(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes) {
		log.debug("Entering method retrieveStageDtl in SaveCaseNameServiceImpl");
		boolean subcare_stage = false;
		int i = 0;
		RetrieveStageInDto retrieveStageInDto = new RetrieveStageInDto();
		RetrieveStageOutDto retrieveStageOutDto = new RetrieveStageOutDto();
		SaveStageInDto saveStageInDto = new SaveStageInDto();
		SaveStageOutDto saveStageOutDto = new SaveStageOutDto();
		StagePersonLinkUpdateInDto stagePersonLinkUpdateInDtto = new StagePersonLinkUpdateInDto();
		StagePersonLinkUpdateOutDto stagePersonLinkUpdateOutDto = new StagePersonLinkUpdateOutDto();
		UpdateStagePersonLinkInDto updateStagePersonLinkInDto = new UpdateStagePersonLinkInDto();
		StagePersonLinkRtrvInDto stagePersonLinkRtrvInDto = new StagePersonLinkRtrvInDto();
		StagePersonLinkRtrvOutDto stagePersonLinkRtrvOutDto = new StagePersonLinkRtrvOutDto();

		//artf284553 : DEV - INTake stage name does not update
		retrieveStageInDto.setSelectedStageId(saveCaseNameReq.getIdStage());
		retrieveStageInDto.setUlIdCase(saveCaseNameReq.getIdCase());
		caseMaintenanceRetrieveStageDtlDao.fetchStageDtl(retrieveStageInDto, retrieveStageOutDto);
		if (retrieveStageOutDto != null) {
			if (!CollectionUtils.isEmpty(retrieveStageOutDto.getROWCCMNE1DO())) {
				for (i = 0; i < retrieveStageOutDto.getROWCCMNE1DO().size(); i++) {
					subcare_stage = false;
					if ((retrieveStageOutDto.getROWCCMNE1DO().get(i).getCdStage().equalsIgnoreCase(SUBCARE_STAGE))
							|| (retrieveStageOutDto.getROWCCMNE1DO().get(i).getCdStage().equalsIgnoreCase(PAL_STAGE))
							|| (retrieveStageOutDto.getROWCCMNE1DO().get(i).getCdStage()
							.equalsIgnoreCase(ADOPTION_STAGE))
							|| (retrieveStageOutDto.getROWCCMNE1DO().get(i).getCdStage()
							.equalsIgnoreCase(POST_ADOPT_STAGE))) {
						subcare_stage = true;
					}
                //artf263269: Closed stages should not have data changes
					if (!subcare_stage && retrieveStageOutDto.getROWCCMNE1DO().get(i).getDtStageClose()== null) {
						saveStageInDto.setIdStage(retrieveStageOutDto.getROWCCMNE1DO().get(i).getIdStage());
						saveStageInDto.setNmCase(saveCaseNameReq.getNmCase());
						saveStageInDto.setReqFuncCd(saveCaseNameReq.getReqFuncCd());
							caseMaintenanceUpdateStageNameDao.saveStage(saveStageInDto, saveStageOutDto);
						if (saveStageOutDto != null) {

						}
						stagePersonLinkRtrvInDto.setIdPerson(saveCaseNameReq.getIdNmPerson());
						stagePersonLinkRtrvInDto.setIdStage(retrieveStageOutDto.getROWCCMNE1DO().get(i).getIdStage());
						caseMaintenanceVerifyStagePersonLinkDao.verifyStagePersonLink(stagePersonLinkRtrvInDto,
								stagePersonLinkRtrvOutDto);
						if (stagePersonLinkRtrvOutDto != null) {
							stagePersonLinkUpdateInDtto
									.setIdStage(retrieveStageOutDto.getROWCCMNE1DO().get(i).getIdStage());
							caseMaintenanceUpdateStageDao.updateStagePersonLink(stagePersonLinkUpdateInDtto,
									stagePersonLinkUpdateOutDto);
							//artf263269 : Need to make IND_NM_STAGE to 1 for matching PID
							updatePersonLink(saveCaseNameReq, saveCaseNameRes);
							if (stagePersonLinkUpdateOutDto != null) {
								updateStagePersonLinkInDto
										.setUlIdStage(retrieveStageOutDto.getROWCCMNE1DO().get(i).getIdStage());
								{
								}

							}

						}
					}
				}
			}

		}

		log.debug("Exiting method retrieveStageDtl in SaveCaseNameServiceImpl");
	}

	/**
	 * Method Name: updateStageName Method Description: update the stage name
	 * DAM: CCMND8D
	 *
	 * @param saveCaseNameReq
	 * @param saveCaseNameRes
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateStageName(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes) {
		log.debug("Entering method updateStageName in SaveCaseNameServiceImpl");
		SaveStageInDto saveStageInDto = new SaveStageInDto();
		SaveStageOutDto saveStageOutDto = new SaveStageOutDto();
		PostEventInDto postEventInDto = new PostEventInDto();
		PostEventOutDto postEventOutDto = new PostEventOutDto();

		postEventInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventInDto.setROWCCMN01UIG00(new RowEventInDto());
		postEventInDto.getROWCCMN01UIG00().setCdEventStatus(COMPLETE);
		postEventInDto.getROWCCMN01UIG00().setCdEventType(CASE_GENERAL);
		postEventInDto.getROWCCMN01UIG00().setDtEventOccurred(null);
		postEventInDto.getROWCCMN01UIG00().setIdStage(saveCaseNameReq.getIdStage());
		postEventInDto.getROWCCMN01UIG00().setIdPerson(saveCaseNameReq.getIdPerson());
		postEventInDto.getROWCCMN01UIG00().setTxtEventDescr("Stage name change: ");
		postEventInDto.getROWCCMN01UIG00()
				.setTxtEventDescr(postEventInDto.getROWCCMN01UIG00().getTxtEventDescr() + saveCaseNameReq.getCurrentCaseName());
		postEventInDto.getROWCCMN01UIG00()
				.setTxtEventDescr(postEventInDto.getROWCCMN01UIG00().getTxtEventDescr() + " to ");
		postEventInDto.getROWCCMN01UIG00()
				.setTxtEventDescr(postEventInDto.getROWCCMN01UIG00().getTxtEventDescr() + saveCaseNameReq.getNmCase());
		caseMaintenanceEventUpdateService.postEvent(postEventInDto, postEventOutDto);
		saveStageInDto.setIdStage(saveCaseNameReq.getIdStage());
		saveStageInDto.setNmCase(saveCaseNameReq.getNmCase());
		caseMaintenanceUpdateStageNameDao.saveStage(saveStageInDto, saveStageOutDto);

		log.debug("Exiting method updateStageName in SaveCaseNameServiceImpl");
	}

	/**
	 * Method Name: updateTodo Method Description:update todo DAM: CMNH3D
	 *
	 * @param saveCaseNameReq
	 * @param saveCaseNameRes
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateTodo(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes) {
		log.debug("Entering method updateTodo in SaveCaseNameServiceImpl");
		CompleteToDoInDto completeToDoInDto = new CompleteToDoInDto();
		CompleteToDoOutDto completeToDoOutDto = new CompleteToDoOutDto();
		if (completeToDoInDto == (CompleteToDoInDto) null || completeToDoOutDto == (CompleteToDoOutDto) null) {
		}

		completeToDoInDto.setIdTodo(saveCaseNameReq.getIdTodo());
		caseMaintenanceUpdateToDoDao.updateTodo(completeToDoInDto, completeToDoOutDto);

		log.debug("Exiting method updateTodo in SaveCaseNameServiceImpl");
	}

	/**
	 * Method Name: updateStagePersonLink Method Description:update
	 * StagePersonLink DAM: CAUDF0D
	 *
	 * @param saveCaseNameReq
	 * @param saveCaseNameRes
	 * @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateStagePersonLink(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes) {
		log.debug("Entering method updateStagePersonLink in SaveCaseNameServiceImpl");
		StagePersonLinkUpdateInDto stagePersonLinkUpdateInDto = new StagePersonLinkUpdateInDto();
		StagePersonLinkUpdateOutDto stagePersonLinkUpdateOutDto = new StagePersonLinkUpdateOutDto();
		if (stagePersonLinkUpdateInDto == (StagePersonLinkUpdateInDto) null
				|| stagePersonLinkUpdateOutDto == (StagePersonLinkUpdateOutDto) null) {
		}

		stagePersonLinkUpdateInDto.setIdStage(saveCaseNameReq.getIdStage());
		caseMaintenanceUpdateStageDao.updateStagePersonLink(stagePersonLinkUpdateInDto, stagePersonLinkUpdateOutDto);

		log.debug("Exiting method updateStagePersonLink in SaveCaseNameServiceImpl");
	}

	/**
	 * Method Name: updatePersonLink Method Description: update the PersonLink
	 * table DAM: CAUDF1D
	 *
	 * @param saveCaseNameReq
	 * @param saveCaseNameRes
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updatePersonLink(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes) {
		log.debug("Entering method updatePersonLink in SaveCaseNameServiceImpl");
		UpdateStagePersonLinkInDto updateStagePersonLinkInDto = new UpdateStagePersonLinkInDto();
		UpdateStagePersonLinkOutDto updateStagePersonLinkOutDto = new UpdateStagePersonLinkOutDto();
		if (updateStagePersonLinkInDto == (UpdateStagePersonLinkInDto) null
				|| updateStagePersonLinkOutDto == (UpdateStagePersonLinkOutDto) null) {
		}

		updateStagePersonLinkInDto.setUlIdStage(saveCaseNameReq.getIdStage());
		updateStagePersonLinkInDto.setUlIdPerson(saveCaseNameReq.getIdNmPerson());
		caseMaintenanceSetStageDao.setStage(updateStagePersonLinkInDto, updateStagePersonLinkOutDto);

		log.debug("Exiting method updatePersonLink in SaveCaseNameServiceImpl");
	}

}
