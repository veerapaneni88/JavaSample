package us.tx.state.dfps.service.admin.serviceimpl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.IncomingPersonMps;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.mobile.IncomingPersonMpsDto;
import us.tx.state.dfps.service.admin.dao.AdoptionSubsidyDao;
import us.tx.state.dfps.service.admin.dao.AdoptionSubsidyInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.AdptSubEventLinkDao;
import us.tx.state.dfps.service.admin.dao.AdptSubMaxEventLinkDao;
import us.tx.state.dfps.service.admin.dao.EligibilityByPersonDao;
import us.tx.state.dfps.service.admin.dao.EligibilityDao;
import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dao.EventStPerLnkEmpMerRletChkCountDao;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dao.EventUpdEventStatusDao;
import us.tx.state.dfps.service.admin.dao.IncomingDetailStageDao;
import us.tx.state.dfps.service.admin.dao.LegalStatusPersonMaxStatusDtDao;
import us.tx.state.dfps.service.admin.dao.NameSeqInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.PcaSubsidyDao;
import us.tx.state.dfps.service.admin.dao.PcaSubsidyUpdCloseRsnDao;
import us.tx.state.dfps.service.admin.dao.PersonCategoryInsUpdDelCountDao;
import us.tx.state.dfps.service.admin.dao.PersonEthnicityIdPersonDao;
import us.tx.state.dfps.service.admin.dao.PersonPortfolioDao;
import us.tx.state.dfps.service.admin.dao.PersonRaceInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.PersonStageLinkCatgIdDao;
import us.tx.state.dfps.service.admin.dao.PlacementActPlannedDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkNmStageDao;
import us.tx.state.dfps.service.admin.dao.StageStagePersonLinkDao;
import us.tx.state.dfps.service.admin.dao.TempStagePersLinkDao;
import us.tx.state.dfps.service.admin.dao.TletsCheckDao;
import us.tx.state.dfps.service.admin.dao.TodoInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.WorkloadStgPerLinkSelDao;
import us.tx.state.dfps.service.admin.dto.AdoptionSubsidyInDto;
import us.tx.state.dfps.service.admin.dto.AdoptionSubsidyInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.AdoptionSubsidyOutDto;
import us.tx.state.dfps.service.admin.dto.AdptSubEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.AdptSubEventLinkOutDto;
import us.tx.state.dfps.service.admin.dto.AdptSubMaxEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.AdptSubMaxEventLinkOutDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.CommonTodoInDto;
import us.tx.state.dfps.service.admin.dto.EligibilityByPersonInDto;
import us.tx.state.dfps.service.admin.dto.EligibilityInDto;
import us.tx.state.dfps.service.admin.dto.EligibilityOutDto;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.EventStPerLnkEmpMerRletChkCountInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.EventUpdEventStatusInDto;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageInDto;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageOutDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.admin.dto.NameSeqInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.PcaSubsidyInDto;
import us.tx.state.dfps.service.admin.dto.PcaSubsidyOutDto;
import us.tx.state.dfps.service.admin.dto.PcaSubsidyUpdCloseRsnInDto;
import us.tx.state.dfps.service.admin.dto.PersonCategoryInsUpdDelCountInDto;
import us.tx.state.dfps.service.admin.dto.PersonDetailUpdateDto;
import us.tx.state.dfps.service.admin.dto.PersonDtlUpdateDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityIdPersonInDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioInDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioOutDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.PersonStageLinkCatgIdInDto;
import us.tx.state.dfps.service.admin.dto.PersonStageLinkCatgIdOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedInDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkNmStageInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkNmStageOutDto;
import us.tx.state.dfps.service.admin.dto.StageStagePersonLinkInDto;
import us.tx.state.dfps.service.admin.dto.StageStagePersonLinkOutDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.TempStagePersLinkInDto;
import us.tx.state.dfps.service.admin.dto.TempStagePersLinkOutDto;
import us.tx.state.dfps.service.admin.dto.TletsCheckInDto;
import us.tx.state.dfps.service.admin.dto.TletsCheckOutDto;
import us.tx.state.dfps.service.admin.dto.TodoInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelInDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelOutDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.CommonTodoService;
import us.tx.state.dfps.service.admin.service.PersonDetailUpdateService;
import us.tx.state.dfps.service.admin.service.PostEventStageStatusService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.IncomingPersonMpsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dao.MedicaidUpdateDao;
import us.tx.state.dfps.service.person.dto.MedicaidUpdateDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: service
 * implementation for PersonDetailUpdate Aug 14, 2017- 11:46:19 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class PersonDetailUpdateServiceImpl implements PersonDetailUpdateService {

	@Autowired
	MessageSource messageSource;

	// Ccmn06u
	@Autowired
	StageEventStatusCommonService stageEventStatusCommonService;

	// Csub40u
	@Autowired
	CommonTodoService commonTodoService;

	// Ccmn44d
	@Autowired
	PersonPortfolioDao personPortfolioDao;

	// Cses38d
	@Autowired
	EligibilityDao eligibilityDao;

	// Caud18d
	@Autowired
	EligibilityByPersonDao eligibilityByPersonDao;

	// Ccmn45d
	@Autowired
	EventIdDao eventIdDao;

	// Ccmn01u
	@Autowired
	PostEventStageStatusService postEventStageStatusService;

	// Clss69d
	@Autowired
	AdoptionSubsidyDao adoptionSubsidyDao;

	// Caud81d
	@Autowired
	AdoptionSubsidyInsUpdDelDao adoptionSubsidyInsUpdDelDao;

	// Clssc4d
	@Autowired
	AdptSubEventLinkDao adptSubEventLinkDao;

	// Clssc5d
	@Autowired
	AdptSubMaxEventLinkDao adptSubMaxEventLinkDao;

	// Caud99d
	@Autowired
	MedicaidUpdateDao medicaidUpdateDao;

	// Clsc72d
	@Autowired
	StageStagePersonLinkDao stageStagePersonLinkDao;

	// Cses32d
	@Autowired
	LegalStatusPersonMaxStatusDtDao legalStatusPersonMaxStatusDtDao;

	// Cses34d
	@Autowired
	PlacementActPlannedDao placementActPlannedDao;

	// Cinv41d
	@Autowired
	PersonStageLinkCatgIdDao personStageLinkCatgIdDao;

	// Ccmnc2d
	@Autowired
	PersonCategoryInsUpdDelCountDao personCategoryInsUpdDelCountDao;

	// Ccmn43d
	@Autowired
	TodoInsUpdDelDao todoInsUpdDelDao;

	// Cinv51d
	@Autowired
	WorkloadStgPerLinkSelDao workloadStgPerLinkSelDao;

	// Cinv32d
	@Autowired
	NameSeqInsUpdDelDao nameSeqInsUpdDelDao;

	// Clssc8d
	@Autowired
	PcaSubsidyDao pcaSubsidyDao;

	// Caudm5d
	@Autowired
	PcaSubsidyUpdCloseRsnDao pcaSubsidyUpdCloseRsnDao;

	// Ccmn62d
	@Autowired
	EventUpdEventStatusDao eventUpdEventStatusDao;

	// Ccmn87d
	@Autowired
	EventStagePersonLinkInsUpdDao eventStagePersonLinkInsUpdDao;

	// Ccmn05u
	@Autowired
	ApprovalCommonService approvalCommonService;

	// Cses92d
	@Autowired
	TempStagePersLinkDao tempStagePersLinkDao;

	// Cint07d
	@Autowired
	IncomingDetailStageDao incomingDetailStageDao;

	// Caudd5d
	@Autowired
	PersonRaceInsUpdDelDao personRaceInsUpdDelDao;

	// Caudd4d
	@Autowired
	PersonEthnicityIdPersonDao personEthnicityIdPersonDao;

	// Cint60d
	@Autowired
	EventStPerLnkEmpMerRletChkCountDao eventStPerLnkEmpMerRletChkCountDao;

	// Clscf9d
	@Autowired
	StagePersonLinkNmStageDao stagePersonLinkNmStageDao;

	// Clsch6d
	@Autowired
	TletsCheckDao tletsCheckDao;

	@Autowired
	PersonDtlService personDtlService;

	private static final Logger log = Logger.getLogger(PersonDetailUpdateServiceImpl.class);
	@Autowired
	private MobileUtil mobileUtil;

	/**
	 * Method : personDetailUpdate Method Description: This updates person
	 * record.
	 *
	 * @param personDetailUpdateiDto
	 * @return PersonDetailUpdateoDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonDetailUpdateDto personDetailUpdate(PersonDtlUpdateDto personDetailUpdateiDto) {
		log.debug("Entering method personDetailUpdate in PersonDetailUpdateServiceImpl");
		PersonDetailUpdateDto personDetailUpdateoDto = new PersonDetailUpdateDto();
		String RetVal = ServiceConstants.FND_SUCCESS;
		// Clsc72d
		StageStagePersonLinkOutDto stageStagePersonLinkOutDto = new StageStagePersonLinkOutDto();
		boolean bNewDeath = false;
		boolean bToDoFlag = false;
		SimpleDateFormat sdf = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_yyyyMMddHHmmss);
		String strDate = sdf.format(personDetailUpdateiDto.getTsSysTsLastUpdate2());
		Timestamp newLastUpdatedTime = Timestamp.valueOf(strDate);
		if (!ServiceConstants.REQ_FUNC_CD_ADD.equals(personDetailUpdateiDto.getReqFuncCd())) {
			CommonHelperReq commonHelperReq = new CommonHelperReq();
			commonHelperReq.setIdPerson(personDetailUpdateiDto.getIdPerson());
			PersonDto personDto = personDtlService.getPersonDetail(commonHelperReq);
			if (!ObjectUtils.isEmpty(personDto)) {
				if (!personDto.getDtLastUpdate().equals(newLastUpdatedTime)) {
					ErrorDto errorDto = new ErrorDto();
					errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
					personDetailUpdateoDto.setErrorDto(errorDto);
					personDetailUpdateoDto.setIdPerson(personDetailUpdateiDto.getIdPerson());
					return personDetailUpdateoDto;
				}
			}
		}

		if (!StringUtils.isEmpty(personDetailUpdateiDto.getSysIndGeneric())
				&& personDetailUpdateiDto.getSysIndGeneric().equals(ServiceConstants.STRING_IND_N)) {


			/**
			 * Check Stage/Event common function
			 **/
			// CCMN06U
			StageTaskInDto stageTaskInDto = new StageTaskInDto();
			stageTaskInDto.setReqFuncCd(personDetailUpdateiDto.getReqFuncCd());
			stageTaskInDto.setIdStage(personDetailUpdateiDto.getIdStage());
			stageTaskInDto.setCdTask(personDetailUpdateiDto.getCdTask());
			try {
				RetVal = stageEventStatusCommonService.checkStageEventStatus(stageTaskInDto);
				if (RetVal.equals(ServiceConstants.ARC_SUCCESS)) {
					RetVal = ServiceConstants.FND_SUCCESS;
				} else {
					RetVal = ServiceConstants.FND_FAIL;
				}
			} catch (DataNotFoundException e) {
				log.error("Error occured in CCMN06 service");
			}
		}
		/**
		 * Only complete below retrieval/update DAMS if a Death Date is new or
		 * has been changed.
		 */
		if ((!(TypeConvUtil.isNullOrEmpty(personDetailUpdateiDto.getDtPersonDeath())))
				&& (!(ServiceConstants.REQ_FUNC_CD_ADD.equals(personDetailUpdateiDto.getReqFuncCd())))) {
			if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
				/**
				 * Retrieve prior record for input message child to check if
				 * date of death is new (CCMN44D)
				 */
				// CCMN44D
				PersonPortfolioInDto personPortfolioInDto = new PersonPortfolioInDto();
				personPortfolioInDto.setIdPerson(personDetailUpdateiDto.getIdPerson());
				List<PersonPortfolioOutDto> portfolioOutDtos = personPortfolioDao.getPersonRecord(personPortfolioInDto);
				if (!(TypeConvUtil.isNullOrEmpty(portfolioOutDtos))) {
					if (TypeConvUtil.isNullOrEmpty(portfolioOutDtos.get(0).getDtPersonDeath())) {
						bNewDeath = true;
					}
					RetVal = ServiceConstants.FND_SUCCESS;
				}
			}
			if (bNewDeath) {
				stageStagePersonLinkOutDto = updateRecords_ForChangedDeathDate(personDetailUpdateiDto, RetVal);
			}
		}
		/**
		 * If we are deleting the person call CallVerifyCaseName to verify that
		 * the person is not a designated case name for the stage
		 */
		if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(personDetailUpdateiDto.getReqFuncCd())) {
			// Clscf9d
			List<StagePersonLinkNmStageOutDto> stagePersonLinkNmStageOutDtos = verifyCaseName(personDetailUpdateiDto);
			if (null != stagePersonLinkNmStageOutDtos && stagePersonLinkNmStageOutDtos.size() > 0) {
				RetVal = Long.toString(ServiceConstants.MSG_PERSON_CASE_NAME);
			} else {
				RetVal = ServiceConstants.FND_SUCCESS;
			}
		}
		if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(personDetailUpdateiDto.getReqFuncCd())) {
			// Clsch6d
			List<TletsCheckOutDto> tletsCheckOutDtos = verifyTlets(personDetailUpdateiDto);
			if (null != tletsCheckOutDtos && tletsCheckOutDtos.size() > 0) {
				RetVal = Long.toString(ServiceConstants.MSG_TLETS_PERSON_DELETE);
			} else {
				RetVal = ServiceConstants.FND_SUCCESS;
			}
		}
		if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
			PersonStageLinkCatgIdInDto personStageLinkCatgIdInDto = mapCINV41DTO(personDetailUpdateiDto);
			try {
				PersonStageLinkCatgIdOutDto personStageLinkCatgIdOutDto = personStageLinkCatgIdDao
						.updateInvestigationPersonDetail(personStageLinkCatgIdInDto);
				if (null != personStageLinkCatgIdOutDto && personStageLinkCatgIdOutDto.getIdPerson() > 0) {
					personDetailUpdateoDto.setIdPerson(personStageLinkCatgIdOutDto.getIdPerson());
					RetVal = ServiceConstants.FND_SUCCESS;
				} else {
					log.error("Error occured in  updating tables.");
					RetVal = ServiceConstants.FND_FAIL;
				}

			} catch (DataNotFoundException e) {
				log.error("Error occured in  updating tables.");
				RetVal = ServiceConstants.FND_FAIL;
			}
			if (ServiceConstants.FND_SUCCESS.equals(RetVal)
					&& ServiceConstants.REQ_FUNC_CD_DELETE.equals(personDetailUpdateiDto.getReqFuncCd())) {
				/**
				 * Check if the given ID PERSON is involved in any other stages
				 * or events and if not, deletes the person from the database
				 * using the delete person stored procedure.
				 */
				EventStPerLnkEmpMerRletChkCountInDto eventStPerLnkEmpMerRletChkCountInDto = new EventStPerLnkEmpMerRletChkCountInDto();
				eventStPerLnkEmpMerRletChkCountInDto.setIdPerson(personDetailUpdateiDto.getIdPerson());
				eventStPerLnkEmpMerRletChkCountInDto.setIndDelPerson(ServiceConstants.STRING_IND_Y);
				try {
					eventStPerLnkEmpMerRletChkCountDao.deletePerson(eventStPerLnkEmpMerRletChkCountInDto);
				} catch (DataNotFoundException e) {
					log.error("Person record didn't got deleted");
				}
			}
			if (null != personDetailUpdateiDto.getCdCategoryCategory()
					&& (StringUtils.isEmpty(personDetailUpdateiDto.getIndChkd())
					|| !personDetailUpdateiDto.getIndChkd().equals(ServiceConstants.STRING_IND_Y))) {
				PersonCategoryInsUpdDelCountInDto personCategoryInsUpdDelCountInDto = new PersonCategoryInsUpdDelCountInDto();
				if (!CollectionUtils.isEmpty(personDetailUpdateiDto.getCdCategoryCategory())) {
					for (String category : personDetailUpdateiDto.getCdCategoryCategory()) {
						personCategoryInsUpdDelCountInDto.setCdCategoryCategory(category);
						personCategoryInsUpdDelCountInDto.setIdPerson(personDetailUpdateoDto.getIdPerson());
						personCategoryInsUpdDelCountInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
						try {
							personCategoryInsUpdDelCountDao.updatePersonCategory(personCategoryInsUpdDelCountInDto);
							RetVal = ServiceConstants.FND_SUCCESS;
						} catch (DataNotFoundException eupdatePersonCategory) {
							log.error("Person category record not inserted/updated");
						}
					}
				}
			}
			if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
				PersonRaceInsUpdDelInDto personRaceInsUpdDelInDto = new PersonRaceInsUpdDelInDto();
				if (!CollectionUtils.isEmpty(personDetailUpdateiDto.getPersonRaceDtoList())) {
					for (PersonRaceDto personRace : personDetailUpdateiDto.getPersonRaceDtoList()) {
						personRaceInsUpdDelInDto.setIdPerson(personDetailUpdateoDto.getIdPerson());
						personRaceInsUpdDelInDto.setCdPersonRace(personRace.getCdPersonRace());
						personRaceInsUpdDelInDto.setReqFuncCd(personRace.getPersonRaceAction());
						if (!StringUtils.isEmpty(personRaceInsUpdDelInDto.getReqFuncCd()) && !(personRaceInsUpdDelInDto
								.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_NO_ACTION))) {
							try {
								personRaceInsUpdDelDao.updatePersonRaceRecord(personRaceInsUpdDelInDto);
							} catch (DataNotFoundException e) {
								log.error(" Person record not found in Person Race.");
							}
						}
					}
				}
			}
			if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
				PersonEthnicityIdPersonInDto personEthnicityIdPersonInDto = new PersonEthnicityIdPersonInDto();
				if (!CollectionUtils.isEmpty(personDetailUpdateiDto.getPersonEthnicityDtoList())) {
					for (PersonEthnicityDto personEthnicity : personDetailUpdateiDto.getPersonEthnicityDtoList()) {
						personEthnicityIdPersonInDto.setIdPerson(personDetailUpdateoDto.getIdPerson());
						personEthnicityIdPersonInDto.setCdPersonEthnicity(personEthnicity.getCdPersonEthnicity());
						personEthnicityIdPersonInDto.setReqFuncCd(personEthnicity.getPersonEthnicityAction());
					}
				}
				if (!TypeConvUtil.isNullOrEmpty(personEthnicityIdPersonInDto.getCdPersonEthnicity())) {
					try {
						personEthnicityIdPersonDao.deletePersonEthnicity(personEthnicityIdPersonInDto);
					} catch (DataNotFoundException e) {
						log.error(" No new ethinicity record got inserted.");
					}
				}
			}
			if ((ServiceConstants.REQ_FUNC_CD_ADD.equals(personDetailUpdateiDto.getReqFuncCd()))) {
				NameSeqInsUpdDelInDto nameSeqInsUpdDelInDto = mapNameSeqInsUpdDelInDto(personDetailUpdateiDto,
						personDetailUpdateoDto);
				if (!ObjectUtils.isEmpty(nameSeqInsUpdDelInDto)) {
					try {
						nameSeqInsUpdDelDao.updateNameRecord(nameSeqInsUpdDelInDto);
					} catch (DataNotFoundException enamerecord) {
						log.error(" Person name not got inserted.");
					}
				}
			}
			if (null != personDetailUpdateiDto.getSysIndCreateToDo()
					&& !personDetailUpdateiDto.getSysIndCreateToDo().equals("N")
					&& null != stageStagePersonLinkOutDto) {
				bToDoFlag = true;
				if (!ObjectUtils.isEmpty(personDetailUpdateiDto.getIdStage())
						&& personDetailUpdateiDto.getIdStage().equals(stageStagePersonLinkOutDto.getIdStage())) {
					bToDoFlag = false;
				}
				if (bToDoFlag) {
					WorkloadStgPerLinkSelInDto workloadStgPerLinkSelInDto = new WorkloadStgPerLinkSelInDto();
					workloadStgPerLinkSelInDto.setIdStage(personDetailUpdateiDto.getIdStage());
					workloadStgPerLinkSelInDto.setCdStagePersRole(ServiceConstants.CD_PRIMARY_WORKER);
					List<WorkloadStgPerLinkSelOutDto> workloadStgPerLinkSelOutDtos;
					try {
						workloadStgPerLinkSelOutDtos = workloadStgPerLinkSelDao.getWorkLoad(workloadStgPerLinkSelInDto);
						if (null != workloadStgPerLinkSelOutDtos && workloadStgPerLinkSelOutDtos.size() > 0) {
							personDetailUpdateiDto
									.setIdTodoPersAssigned(workloadStgPerLinkSelOutDtos.get(0).getIdTodoPersAssigned());
						}
					} catch (DataNotFoundException eliCinv51doDto) {
						log.error(" No record found for ID stage in workload table.");
						TempStagePersLinkInDto tempStagePersLinkInDto = new TempStagePersLinkInDto();
						tempStagePersLinkInDto.setIdStage(personDetailUpdateiDto.getIdStage());
						tempStagePersLinkInDto.setCdStagePersRole(ServiceConstants.CD_PRIMARY_WORKER);
						List<TempStagePersLinkOutDto> tempStagePersLinkOutDtos;
						try {
							tempStagePersLinkOutDtos = tempStagePersLinkDao
									.getTempStagePersonLink(tempStagePersLinkInDto);
							if (null != tempStagePersLinkOutDtos && tempStagePersLinkOutDtos.size() > 0) {
								personDetailUpdateiDto
										.setIdTodoPersAssigned(tempStagePersLinkOutDtos.get(0).getIdTempStagePerson());
							}
						} catch (DataNotFoundException eliCses92doDto) {
							log.error(" No temporary record found for stage and person link.");
							IncomingDetailStageInDto incomingDetailStageInDto = new IncomingDetailStageInDto();
							incomingDetailStageInDto.setIdStage(personDetailUpdateiDto.getIdStage());
							List<IncomingDetailStageOutDto> incomingDetailStageOutDtos;
							try {
								incomingDetailStageOutDtos = incomingDetailStageDao
										.getIncomingDetail(incomingDetailStageInDto);
								if (null != incomingDetailStageOutDtos && incomingDetailStageOutDtos.size() > 0) {
									if (incomingDetailStageOutDtos.get(0).getCdIncmgStatus()
											.equalsIgnoreCase(ServiceConstants.INTAKE_OPEN)) {
										personDetailUpdateiDto
												.setIdTodoPersAssigned(incomingDetailStageOutDtos.get(0).getIdPerson());
									}
									if (incomingDetailStageOutDtos.get(0).getCdIncmgStatus()
											.equalsIgnoreCase(ServiceConstants.INTAKE_SUBMIT_FOR_APPROVAL)) {
										personDetailUpdateiDto
												.setIdTodoPersAssigned(incomingDetailStageOutDtos.get(0).getIdPerson());
									}
								}
							} catch (DataNotFoundException dataNotFoundException) {
								log.error(" No record found for ID stage in income category.");
								personDetailUpdateiDto.setIdTodoPersAssigned(0L);
							}
						}
					}
				}
				if ((personDetailUpdateiDto.getIdTodoPersAssigned() != personDetailUpdateiDto.getIdPersonId())
						&& (personDetailUpdateiDto.getIdTodoPersAssigned() != 0)) {
					TodoInsUpdDelInDto todoInsUpdDelInDto = mapTodoInsUpdDelInDto(personDetailUpdateiDto);
					try {
						todoInsUpdDelDao.cudTODO(todoInsUpdDelInDto);
					} catch (DataNotFoundException e) {
						log.error("No TO-DO activity happen");
					}
				}
				// }
				if (!ObjectUtils.isEmpty(personDetailUpdateiDto.getIdEvent())
						&& (personDetailUpdateiDto.getIdEvent() != 0)
						&& (!personDetailUpdateiDto.getSysNbrReserved1())) {
					DemoteEvents(personDetailUpdateiDto);
				}
			}
			log.debug("Exiting method callPersonDetailUpdateService in PersonDetailUpdateServiceImpl");
		}
		return personDetailUpdateoDto;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public StageStagePersonLinkOutDto updateRecords_ForChangedDeathDate(PersonDtlUpdateDto pInputMsg, String RetVal) {
		int lTempLegalStatus = 0;
		boolean bEligExists = false;
		String szTempCdEligSelected = null;
		long ulTempIdEvent = 0;
		// artf200892:
		long ulTempIdEventStageId = 0;
		long ulTempIdEventCaseId = 0;
		boolean bPcaExists = false;
		boolean bAdopExists = false;
		String szTempCdAdopSubDetrm = null;
		long ulOutputId_Event69 = 0;
		MedicaidUpdateDto pCAUD99DInputRec = new MedicaidUpdateDto();
		long ulAdopTempEvent = 0;
		long ulOutputId_EventC7 = 0;
		long ulTempIdEventStageIdc7 = 0;
		long ulTempIdEventStageIdLS = 0;
		boolean bTodoNecessary = false;
		StageStagePersonLinkOutDto objClsc72doDto = null;
		if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
			/**
			 * Retrieve/update active record from ELIGIBILITY table using
			 ** CSES38D & CAUD18D
			 */
			EligibilityInDto pCSES38DInputRec = new EligibilityInDto();
			Date DtToday = new Date();
			pCSES38DInputRec.setIdPerson(pInputMsg.getIdPerson());
			pCSES38DInputRec.setDtScrDtCurrentDate(DtToday);
			List<EligibilityOutDto> liCses38doDto = null;
			try {
				liCses38doDto = eligibilityDao.getEligibilityRecord(pCSES38DInputRec);
			} catch (DataNotFoundException e) {
				log.error("No active eligibility record exists for given PersonId");
				liCses38doDto = null;
			}
			if (!CollectionUtils.isEmpty(liCses38doDto)) {
				RetVal = ServiceConstants.FND_SUCCESS;
				bEligExists = true;
				szTempCdEligSelected = liCses38doDto.get(0).getCdEligSelected();
				ulTempIdEvent = liCses38doDto.get(0).getIdEligibilityEvent();
				// artf200892: Assigning StageId and caseId to local variable from liCses38doDto object
				ulTempIdEventStageId = liCses38doDto.get(0).getIdStage();
				/**
				 ** Call CAUD18D to update active record from ELIGIBILITY Record
				 * will be passed into function through liCses38doDto.get(0)
				 */
				EligibilityByPersonInDto pCAUD18DInputRec = MapCaud18dto(pInputMsg, liCses38doDto.get(0));
				eligibilityByPersonDao.updateEligibiltyPeriod(pCAUD18DInputRec);
				log.debug("active records are updated with eligibility period.");
			} else {
				/**
				 * if there are no active eligibility record exists for given
				 * PersonId
				 */
				RetVal = ServiceConstants.FND_SUCCESS;
			}
		}
		if ((bEligExists) && (ServiceConstants.FND_SUCCESS.equals(RetVal))) {
			RetVal = retrieveEvent_And_UpdatePostEvent(pInputMsg, ulTempIdEvent);
		}
		if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
			/**
			 * Retrieve/update active record from ADOPTION SUBSIDY
			 */
			AdoptionSubsidyInDto pCLSS69DInputRec = new AdoptionSubsidyInDto();
			pCLSS69DInputRec.setAdptSubPerson(pInputMsg.getIdPerson());
			pCLSS69DInputRec.setDtPersonDeath(pInputMsg.getDtPersonDeath());
			List<AdoptionSubsidyOutDto> liClss69doDto = null;
			try {
				liClss69doDto = adoptionSubsidyDao.getAdoptionSubsidyRecord(pCLSS69DInputRec);
			} catch (DataNotFoundException e) {
				log.error("No Adoption subsidy available for this person");
				liClss69doDto = null;
			}
			if (null != liClss69doDto && liClss69doDto.size() > 0) {
				RetVal = ServiceConstants.FND_SUCCESS;
				/**
				 ** Set "adopt exists" indicator to true. This indicator will be
				 * used by CAUD99D (below).
				 */
				bAdopExists = true;
				for (AdoptionSubsidyOutDto objClss69doDto : liClss69doDto) {
					if (objClss69doDto.getIdAdptSub() != 0) {
						szTempCdAdopSubDetrm = objClss69doDto.getCdAdptSubDeterm();
						// ulTempIdAdptSub[iAdsCtr]=objClss69doDto.getUlIdAdptSub();
						ulOutputId_Event69 = objClss69doDto.getIdAdptSub();
						AdoptionSubsidyInsUpdDelInDto pCAUD81DInputRec = MapCAU81iDTO(pInputMsg, objClss69doDto);
						adoptionSubsidyInsUpdDelDao.updateAdoptionSubsidy(pCAUD81DInputRec);
						log.debug("Adoption subsidy for the person is updated/deleted");
						AdptSubEventLinkInDto pCLSSC4DInputRec = new AdptSubEventLinkInDto();
						pCLSSC4DInputRec.setIdAdptSub(objClss69doDto.getIdAdptSub());
						List<AdptSubEventLinkOutDto> liClssc4doDto = null;
						try {
							liClssc4doDto = adptSubEventLinkDao.getAdoptionEventLink(pCLSSC4DInputRec);
							if (null != liClssc4doDto && liClssc4doDto.size() > 0) {
								ulTempIdEvent = liClssc4doDto.get(0).getIdEvent();
								//artf200892 : setting EventstageId to ulTempIdEventStageId
								ulTempIdEventStageId = getEventRecord(ulTempIdEvent).getIdStage();
							}
						} catch (DataNotFoundException e) {
							log.error("no event id is linked with given adoption subsidy");
							RetVal = ServiceConstants.FND_SUCCESS;
						}
					}
				}
			} else {
				/**
				 * No Adoption subsidy available for this person then assign
				 * success to RetVal
				 */
				RetVal = ServiceConstants.FND_SUCCESS;
			}
		}
		if (ServiceConstants.FND_SUCCESS.equals(RetVal)
				&& pInputMsg.getCdStage().equalsIgnoreCase(ServiceConstants.CD_STAGE_PCA)) {
			/**
			 * Retrieve/update active record from PCA SUBSIDY
			 */
			PcaSubsidyInDto pCLSSC8DInputRec = new PcaSubsidyInDto();
			pCLSSC8DInputRec.setIdPerson(pInputMsg.getIdPerson());
			pCLSSC8DInputRec.setDtPersonDeath(pInputMsg.getDtPersonDeath());
			List<PcaSubsidyOutDto> liClssc8doDto = null;
			try {
				liClssc8doDto = pcaSubsidyDao.getPCARecord(pCLSSC8DInputRec);
				if (null != liClssc8doDto && liClssc8doDto.size() > 0) {
					RetVal = ServiceConstants.FND_SUCCESS;
					bPcaExists = true;
					liClssc8doDto.get(0).getIdPCASubsidy();
					if (null != liClssc8doDto.get(0).getIdEvent()) {
						ulOutputId_EventC7 = liClssc8doDto.get(0).getIdEvent();
						ulTempIdEventStageIdc7 = getEventRecord(ulOutputId_EventC7).getIdStage();
					}
					PcaSubsidyUpdCloseRsnInDto pCAUDM5DInputRec = new PcaSubsidyUpdCloseRsnInDto();
					pCAUDM5DInputRec.setReqFuncCd(pInputMsg.getReqFuncCd());
					pCAUDM5DInputRec.setIdPCASubsidy(liClssc8doDto.get(0).getIdPCASubsidy());
					pCAUDM5DInputRec.setCdCloseRsn(ServiceConstants.CHILD_DEATH_CODE);
					pCAUDM5DInputRec.setTsLastUpdate(liClssc8doDto.get(0).getTsLastUpdate());
					pCAUDM5DInputRec.setDtEnd(liClssc8doDto.get(0).getDtEnd());
					try {
						pcaSubsidyUpdCloseRsnDao.updatePCASubsidy(pCAUDM5DInputRec);
						RetVal = ServiceConstants.FND_SUCCESS;
					} catch (DataNotFoundException e) {
						log.error("PCA record didn't got updated.");
						RetVal = ServiceConstants.FND_FAIL;
					}
				}
			} catch (DataNotFoundException e) {
				log.error("no PCA record exists for the person id.");
				/**
				 * No PCA record exists for the person id then assign success to
				 * RetVal
				 */
				RetVal = ServiceConstants.FND_SUCCESS;
			}
		}
		if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
			/**
			 * Add row to MEDICAID UPDATE table
			 */
			if ((bEligExists) && ((!ServiceConstants.IV_E.equalsIgnoreCase(szTempCdEligSelected))
					|| (!ServiceConstants.STATE_PAID.equalsIgnoreCase(szTempCdEligSelected))
					|| (!ServiceConstants.MAO.equalsIgnoreCase(szTempCdEligSelected)))) {
				/**
				 *
				 ** Set bAdopExists flag to false. We only want one record added
				 * to the Medicaid table so if Elig record conditions are met,
				 * then no other row should be added.
				 */
				bAdopExists = false;
				bPcaExists = false;
				pCAUD99DInputRec.setDtLastUpdate(new Date());
				// artf200892: setting EventstageId to pCAUD99DInputRec
				pCAUD99DInputRec.setIdMedUpdStage(ulTempIdEventStageId);
				pCAUD99DInputRec.setIdMedUpdPerson(pInputMsg.getIdPerson());
				pCAUD99DInputRec.setIdMedUpdRecord(ulTempIdEvent);
				pCAUD99DInputRec.setCdMedUpdType(ServiceConstants.ELIG_DETRM_TYPE);
				pCAUD99DInputRec.setCdMedUpdTransType(ServiceConstants.DENY);
				int rowCount = insertMedicaidUpdate(pCAUD99DInputRec);
				if (rowCount > 0) {
					RetVal = ServiceConstants.FND_SUCCESS;
				} else {
					log.error("Medicaid Table not got updated.");
					RetVal = ServiceConstants.FND_FAIL;
				}
			}
			/*
			 * for (iAdsCtr = 0; iAdsCtr < 10 && 0 != ulTempIdAdptSub;
			 * iAdsCtr++) {
			 */
			if ((bAdopExists) && ((!ServiceConstants.IV_E_FIN_AND_MED.equalsIgnoreCase(szTempCdAdopSubDetrm))
					|| (!ServiceConstants.MEDCAD_ONLY.equalsIgnoreCase(szTempCdAdopSubDetrm))
					|| (!ServiceConstants.STATE_FIN_AND_MED.equalsIgnoreCase(szTempCdAdopSubDetrm))
					|| (!ServiceConstants.STATE_MED_ONLY.equalsIgnoreCase(szTempCdAdopSubDetrm))
					|| (!ServiceConstants.STATE_FIN_AND_MED_ASST.equalsIgnoreCase(szTempCdAdopSubDetrm))
					|| (!ServiceConstants.STATE_MED_ASST_ONLY.equalsIgnoreCase(szTempCdAdopSubDetrm)))) {
				pCAUD99DInputRec.setDtLastUpdate(new Date());
				pCAUD99DInputRec.setIdMedUpdStage(ulTempIdEventStageId);
				pCAUD99DInputRec.setIdMedUpdPerson(pInputMsg.getIdPerson());
				pCAUD99DInputRec.setIdMedUpdRecord(ulTempIdEvent);
				pCAUD99DInputRec.setCdMedUpdType(ServiceConstants.ADOP_SUB_TYPE);
				pCAUD99DInputRec.setCdMedUpdTransType(ServiceConstants.DENY);
				int rowCount = insertMedicaidUpdate(pCAUD99DInputRec);
				if (rowCount > 0) {
					RetVal = ServiceConstants.FND_SUCCESS;
				} else {
					log.error("Medicaid Table not got updated.");
					RetVal = ServiceConstants.FND_FAIL;
				}
			}
			// }
		}
		if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
			if (bPcaExists) {
				pCAUD99DInputRec.setDtLastUpdate(new Date());
				pCAUD99DInputRec.setIdMedUpdStage(ulTempIdEventStageIdc7);
				pCAUD99DInputRec.setIdMedUpdPerson(pInputMsg.getIdPerson());
				pCAUD99DInputRec.setIdMedUpdRecord(ulOutputId_EventC7);
				pCAUD99DInputRec.setCdMedUpdType(ServiceConstants.PCA_ELIG_DETRM_TYPE);
				pCAUD99DInputRec.setCdMedUpdTransType(ServiceConstants.DENY);
				int rowCount = insertMedicaidUpdate(pCAUD99DInputRec);
				if (rowCount > 0) {
					RetVal = ServiceConstants.FND_SUCCESS;
				} else {
					log.error("Medicaid Table not got updated.");
					RetVal = ServiceConstants.FND_FAIL;
				}
			}
			if ((bAdopExists) && (ServiceConstants.FND_SUCCESS.equals(RetVal))) {
				/**
				 * Retrieve/update active record from EVENT
				 */
				RetVal = retrieveEvent_And_UpdatePostEvent(pInputMsg, ulTempIdEvent);
			}
			if ((bAdopExists) && (pInputMsg.getCdStage().equalsIgnoreCase(ServiceConstants.POST_ADOPT))) {
				AdptSubMaxEventLinkInDto pCLSSC5DInputRec = new AdptSubMaxEventLinkInDto();
				pCLSSC5DInputRec.setIdAdptSub(ulOutputId_Event69);
				List<AdptSubMaxEventLinkOutDto> liClssc5doDto = null;
				try {
					liClssc5doDto = adptSubMaxEventLinkDao.getMaxEventID(pCLSSC5DInputRec);
					if (ulOutputId_Event69 != 0) {
						ulTempIdEvent = liClssc5doDto.get(0).getIdEvent();
						RetVal = ServiceConstants.FND_SUCCESS;
					}
				} catch (DataNotFoundException e) {
					log.error("No record found for given event id. ");
					RetVal = ServiceConstants.FND_SUCCESS;
				}
				/**
				 * Retrieve/update active record from EVENT
				 */
				RetVal = retrieveEvent_And_UpdatePostEvent(pInputMsg, ulTempIdEvent);
			}
			if ((bPcaExists) && (ServiceConstants.FND_SUCCESS.equals(RetVal))) {
				/**
				 * Retrieve/update active record from EVENT
				 */
				RetVal = retrieveEvent_And_UpdatePostEvent(pInputMsg, ulTempIdEvent);
			}
			if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
				/**
				 * Retrieve row to verify if child was in Actual placement
				 */
				PlacementActPlannedInDto pCSES34DInputRec = new PlacementActPlannedInDto();
				pCSES34DInputRec.setIdPlcmtChild(pInputMsg.getIdPerson());
				List<PlacementActPlannedOutDto> liCses34doDto = null;
				try {
					liCses34doDto = placementActPlannedDao.getPlacementRecord(pCSES34DInputRec);
					if (null != liCses34doDto && liCses34doDto.size() > 0) {
						ulAdopTempEvent = liCses34doDto.get(0).getIdPlcmtEvent();
						if (ulAdopTempEvent != 0) {
							bTodoNecessary = true;
							RetVal = ServiceConstants.FND_SUCCESS;
						}
					}
				} catch (DataNotFoundException e) {
					log.error("No placement record exists for given person id");
					bTodoNecessary = false;
					RetVal = ServiceConstants.FND_SUCCESS;
				}
			}
			if (bTodoNecessary && ServiceConstants.FND_SUCCESS.equals(RetVal)) {
				StageStagePersonLinkInDto pCLSC72DInputRec = new StageStagePersonLinkInDto();
				// long tempPersonId=5632531;
				pCLSC72DInputRec.setIdPerson(pInputMsg.getIdPerson());
				List<StageStagePersonLinkOutDto> liClsc72doDto = null;
				try {
					liClsc72doDto = stageStagePersonLinkDao.retrieveStagePersonLinkPID(pCLSC72DInputRec);
					if (null != liClsc72doDto && liClsc72doDto.size() > 0) {
						RetVal = ServiceConstants.FND_SUCCESS;
						objClsc72doDto = liClsc72doDto.get(0);
						CommonTodoInDto pTodoCommonInput = new CommonTodoInDto();
						// pTodoCommonInput.setDtSysDtTodoCfDueFrom(ServiceConstants.NULL_DATE);
						if (!ServiceConstants.SUB_CARE.equalsIgnoreCase(objClsc72doDto.getCdStage())) {
							pTodoCommonInput.setSysCdTodoCf(ServiceConstants.SUB_DTH_TSK_CD);
						} else if (!ServiceConstants.ADOPT.equalsIgnoreCase(objClsc72doDto.getCdStage())) {
							pTodoCommonInput.setSysCdTodoCf(ServiceConstants.ADP_DTH_TSK_CD);
						} else if (!ServiceConstants.POST_ADOPT.equalsIgnoreCase(objClsc72doDto.getCdStage())) {
							pTodoCommonInput.setSysCdTodoCf(ServiceConstants.PAD_DTH_TSK_CD);
						}
						pTodoCommonInput.setSysIdTodoCfStage(objClsc72doDto.getIdStage());
						pTodoCommonInput.setSysIdTodoCfPersCrea(pInputMsg.getIdPersonId());
						pTodoCommonInput.setSysIdTodoCfPersAssgn(objClsc72doDto.getIdPerson());
						pTodoCommonInput.setSysIdTodoCfPersWkr(objClsc72doDto.getIdPerson());
						pTodoCommonInput.setSysIdTodoCfEvent(ulAdopTempEvent);
						try {
							commonTodoService.TodoCommonFunction(pTodoCommonInput);
						} catch (DataNotFoundException e) {
							log.error("Error in Todo function");
						}
						RetVal = ServiceConstants.FND_SUCCESS;
					}
				} catch (DataNotFoundException e) {
					log.error("No stage person record exists for given person id");
					RetVal = ServiceConstants.FND_SUCCESS;
				}
			}

			if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
				/**
				 * This retrieves the most recent legal status for child
				 */
				LegalStatusPersonMaxStatusDtInDto pCSES32DInputRec = new LegalStatusPersonMaxStatusDtInDto();
				pCSES32DInputRec.setIdPerson(pInputMsg.getIdPerson());
				try {
					List<LegalStatusPersonMaxStatusDtOutDto> liCses32doDto = legalStatusPersonMaxStatusDtDao
							.getRecentLegelStatusRecord(pCSES32DInputRec);
					if (null != liCses32doDto && liCses32doDto.size() > 0) {
						RetVal = ServiceConstants.FND_SUCCESS;
						LegalStatusPersonMaxStatusDtOutDto objCses32doDto = liCses32doDto.get(0);
						if (null != objCses32doDto.getCdLegalStatStatus()) {
							try {
								lTempLegalStatus = Integer.parseInt(objCses32doDto.getCdLegalStatStatus());
								//artf200892 : setting EventstageId to ulTempIdEventStageIdLS
								ulTempIdEventStageIdLS = getEventRecord(objCses32doDto.getIdLegalStatEvent()).getIdStage();
							} catch (NumberFormatException e) {
								lTempLegalStatus = 0;
							}
						}
						if ((!ServiceConstants.CAPS_PROG_CPS.equalsIgnoreCase(pInputMsg.getCdStageProgram()))
								&& (((ServiceConstants.LEG_ST_MIN <= lTempLegalStatus)
								&& (ServiceConstants.LEG_ST_MAX >= lTempLegalStatus))
								|| ((ServiceConstants.MAX_CLD_AGE >= pInputMsg.getNbrPersonAge())
								&& ((!ServiceConstants.ABU_NEG_DTH1
								.equalsIgnoreCase(pInputMsg.getCdPersonDeath()))
								|| (!ServiceConstants.ABU_NEG_DTH2
								.equalsIgnoreCase(pInputMsg.getCdPersonDeath()))
								|| (!ServiceConstants.ABU_NEG_DTH3
								.equalsIgnoreCase(pInputMsg.getCdPersonDeath())))))) {
							     //artf200892 : calling createAlertInStage() to create alerts for stage.
							    createAlertInStage(pInputMsg, ulTempIdEventStageId);
							    createAlertInStage(pInputMsg, ulTempIdEventStageIdc7);
							    createAlertInStage(pInputMsg, ulTempIdEventStageIdLS);
						}
					} else {
						RetVal = ServiceConstants.FND_SUCCESS;
					}
				} catch (DataNotFoundException e) {
					log.error("No legal record exist for the child.");
					RetVal = ServiceConstants.FND_SUCCESS;
				}
			}
		}
		return objClsc72doDto;
	}
	//artf200892 : createAlertInStage method added to create alerts.
	private void createAlertInStage(PersonDtlUpdateDto pInputMsg, Long idStage) {
		if(idStage == null || idStage == 0l)
			return;
		CommonTodoInDto pTodoCommonInput = new CommonTodoInDto();
		pTodoCommonInput.setDtSysDtTodoCfDueFrom(null);
		pTodoCommonInput.setSysIdTodoCfStage(idStage);
		pTodoCommonInput.setSysCdTodoCf(ServiceConstants.ALERT_DTH_TSK_CD);
		pTodoCommonInput.setSysIdTodoCfPersCrea(pInputMsg.getIdPersonId());
		try {
			commonTodoService.TodoCommonFunction(pTodoCommonInput);
		} catch (DataNotFoundException e) {
			log.error("Error in Todo function");
		}
	}
	/**
	 * Method Name: retrieveEvent_And_UpdatePostEvent Method Description: This
	 * method retrieves the event record and applies post event from CCMN01
	 * service
	 *
	 * @param pInputMsg
	 * @param ulTempIdEvent
	 * @return RetVal @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public String retrieveEvent_And_UpdatePostEvent(PersonDtlUpdateDto pInputMsg, long ulTempIdEvent) {
		String RetVal;
		String szCdCodeFlag;
		long ulOutputIdEvent = 0;
		/**
		 * Retrieve/update active record from EVENT table using CCMN45D and
		 * CCMN01U (PostEvent)
		 */
		EventIdOutDto pCCMN45DOutputRec = null;
		try {
			pCCMN45DOutputRec = getEventRecord(ulTempIdEvent);
		} catch (DataNotFoundException e) {
			log.error("No event record present");
			pCCMN45DOutputRec = null;
		}
		if (ulTempIdEvent != 0 && null != pCCMN45DOutputRec) {
			RetVal = ServiceConstants.FND_SUCCESS;
			szCdCodeFlag = ServiceConstants.REQ_FUNC_CD_UPDATE;
			PostEventStageStatusInDto pCCMN01UInputRec = mapCCMN01iDto(pInputMsg, pCCMN45DOutputRec);
			ulOutputIdEvent = updateEvent(pCCMN01UInputRec, szCdCodeFlag);
			if (ulOutputIdEvent > 0) {
				RetVal = ServiceConstants.FND_SUCCESS;
			} else {
				log.error("The post event failed. ");
				RetVal = ServiceConstants.FND_FAIL;
			}
		} else {
			/**
			 * If there is no record present for getEventRecord then assign
			 * success to RetVal
			 */
			RetVal = ServiceConstants.FND_SUCCESS;
		}
		return RetVal;
	}

	/**
	 *
	 * Method Name: mapCCMN01iDto Method Description:Maps input DTO to
	 * Ccmn01uiDto
	 *
	 * @param pInputMsg
	 * @param pCCMN45DOutputRec
	 * @return Ccmn01uiDto
	 */
	private PostEventStageStatusInDto mapCCMN01iDto(PersonDtlUpdateDto pInputMsg, EventIdOutDto pCCMN45DOutputRec) {
		PostEventStageStatusInDto pCCMN01UInputRec = new PostEventStageStatusInDto();
		String szTxtEventDescr;
		String szTempTxtEventDescr;
		if (!pInputMsg.getSysNbrReserved1()) {
			pCCMN01UInputRec.setCdEventStatus(ServiceConstants.EVENT_STATUS_COMPLETE);
		} else {
			pCCMN01UInputRec.setCdEventStatus(ServiceConstants.EVENT_STATUS_PENDING);
		}
		pCCMN01UInputRec.setIdEvent(pCCMN45DOutputRec.getIdEvent());
		pCCMN01UInputRec.setIdStage(pCCMN45DOutputRec.getIdStage());
		pCCMN01UInputRec.setIdPerson(pCCMN45DOutputRec.getIdPerson());
		pCCMN01UInputRec.setCdTask(pCCMN45DOutputRec.getCdTask());
		pCCMN01UInputRec.setCdEventType(pCCMN45DOutputRec.getCdEventType());
		pCCMN01UInputRec.setDtEventOccurred(pCCMN45DOutputRec.getDtEventOccurred());
		szTxtEventDescr = pCCMN45DOutputRec.getEventDescr();
		szTxtEventDescr = szTxtEventDescr + ServiceConstants.NULL_STRING;
		szTxtEventDescr = szTxtEventDescr + ServiceConstants.END_STRING;
		szTxtEventDescr = szTxtEventDescr + ServiceConstants.NULL_STRING;
		szTempTxtEventDescr = Integer.toString(pInputMsg.getDtPersonDeath().getMonth()
				+ pInputMsg.getDtPersonDeath().getMonth() + pInputMsg.getDtPersonDeath().getMonth());
		szTxtEventDescr = szTxtEventDescr + szTempTxtEventDescr;
		pCCMN01UInputRec.setEventDescr(szTxtEventDescr);
		pCCMN01UInputRec.setDtEventLastUpdate(pCCMN45DOutputRec.getTsLastUpdate());
		return pCCMN01UInputRec;
	}

	/**
	 *
	 * Method Name: MapCaud18dto Method Description: Maps input DTO to
	 * Caud18didto
	 *
	 * @param pInputMsg
	 * @param pCSES38DOutputRec
	 * @return Caud18diDto @
	 */
	public EligibilityByPersonInDto MapCaud18dto(PersonDtlUpdateDto pInputMsg, EligibilityOutDto pCSES38DOutputRec) {
		EligibilityByPersonInDto pCAUD18DInputRec = new EligibilityByPersonInDto();
		pCAUD18DInputRec.setReqFuncCd(pInputMsg.getReqFuncCd());
		pCAUD18DInputRec.setIdEligibilityEvent(pCSES38DOutputRec.getIdEligibilityEvent());
		pCAUD18DInputRec.setIdPerson(pCSES38DOutputRec.getIdPerson());
		pCAUD18DInputRec.setIdPersonUpdate(pCSES38DOutputRec.getIdPersonUpdate());
		pCAUD18DInputRec.setCdEligActual(pCSES38DOutputRec.getCdEligActual());
		pCAUD18DInputRec.setCdEligCsupQuest1(pCSES38DOutputRec.getCdEligCsupQuest1());
		pCAUD18DInputRec.setCdEligCsupQuest2(pCSES38DOutputRec.getCdEligCsupQuest2());
		pCAUD18DInputRec.setCdEligCsupQuest3(pCSES38DOutputRec.getCdEligCsupQuest3());
		pCAUD18DInputRec.setCdEligCsupQuest4(pCSES38DOutputRec.getCdEligCsupQuest4());
		pCAUD18DInputRec.setCdEligCsupQuest5(pCSES38DOutputRec.getCdEligCsupQuest5());
		pCAUD18DInputRec.setCdEligCsupQuest6(pCSES38DOutputRec.getCdEligCsupQuest6());
		pCAUD18DInputRec.setCdEligCsupQuest7(pCSES38DOutputRec.getCdEligCsupQuest7());
		pCAUD18DInputRec.setCdEligMedEligGroup(pCSES38DOutputRec.getCdEligMedEligGroup());
		pCAUD18DInputRec.setCdEligSelected(pCSES38DOutputRec.getCdEligSelected());
		pCAUD18DInputRec.setIndEligCsupSend(pCSES38DOutputRec.getIndEligCsupSend());
		pCAUD18DInputRec.setIndEligWriteHistory(pCSES38DOutputRec.getIndEligWriteHistory());
		pCAUD18DInputRec.setEligComment(pCSES38DOutputRec.getEligComment());
		// pCAUD18DInputRec.setBSysIndPrfrmValidation(FND_NO);
		pCAUD18DInputRec.setSysIndPrfrmValidation(ServiceConstants.STRING_IND_N);
		pCAUD18DInputRec.setTsLastUpdate(pCSES38DOutputRec.getTsLastUpdate());
		pCAUD18DInputRec.setDtEligStart(pCSES38DOutputRec.getDtEligStart());
		pCAUD18DInputRec.setDtEligEnd(pCSES38DOutputRec.getDtEligEnd());
		log.debug("Exiting method MapCaud18dto in PersonDetailUpdateServiceImpl");
		return pCAUD18DInputRec;
	}

	/**
	 *
	 * Method Name: getEventRecord Method Description: Get event record
	 *
	 * @param ulTempIdEvent
	 * @return Ccmn45doDto @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public EventIdOutDto getEventRecord(long ulTempIdEvent) {
		log.debug("Entering method getEventRecord in PersonDetailUpdateServiceImpl");
		EventIdInDto pCCMN45DInputRec = new EventIdInDto();
		pCCMN45DInputRec.setIdEvent(ulTempIdEvent);
		List<EventIdOutDto> liCcmn45doDto = null;
		try {
			liCcmn45doDto = eventIdDao.getEventDetailList(pCCMN45DInputRec);
			if (!TypeConvUtil.isNullOrEmpty(liCcmn45doDto)) {
				return liCcmn45doDto.get(0);
			}
		} catch (DataNotFoundException e) {
			log.error("No event record exists");
		}
		log.debug("Exiting method getEventRecord in PersonDetailUpdateServiceImpl");
		return null;
	}

	/**
	 *
	 * Method Name: updateEvent Method Description: Call to CCMN01 service and
	 * fetch event Id..
	 *
	 * @param pCCMN01UInputRec
	 * @param szCdCodeFlag
	 * @return @
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long updateEvent(PostEventStageStatusInDto pCCMN01UInputRec, String szCdCodeFlag) {
		log.debug("Entering method updateEvent in PersonDetailUpdateServiceImpl");
		long ulOutputIdEvent = 0;
		pCCMN01UInputRec.setReqFuncCd(szCdCodeFlag);
		pCCMN01UInputRec.setReqFuncCd(szCdCodeFlag);
		PostEventStageStatusOutDto pCCMN01UOutputRec = null;
		try {
			pCCMN01UOutputRec = postEventStageStatusService.callPostEventStageStatusService(pCCMN01UInputRec);
			if (null != pCCMN01UOutputRec) {
				ulOutputIdEvent = pCCMN01UOutputRec.getIdEvent();
			}
		} catch (DataNotFoundException e) {
			log.error("No event id got from CCMN01 service");
		}
		log.debug("Exiting method updateEvent in PersonDetailUpdateServiceImpl");
		return ulOutputIdEvent;
	}

	/**
	 *
	 * Method Name: MapCAU81iDTO Method Description: Maps input dto to
	 * Caud81didto
	 *
	 * @param pInputMsg
	 * @param pCLSS69DOutputRec
	 * @return Caud81diDto @
	 */
	public AdoptionSubsidyInsUpdDelInDto MapCAU81iDTO(PersonDtlUpdateDto pInputMsg,
													  AdoptionSubsidyOutDto pCLSS69DOutputRec) {
		AdoptionSubsidyInsUpdDelInDto pCAUD81DInputRec = new AdoptionSubsidyInsUpdDelInDto();
		pCAUD81DInputRec.setReqFuncCd(pInputMsg.getReqFuncCd());
		pCAUD81DInputRec.setIdAdptSub(pCLSS69DOutputRec.getIdAdptSub());
		pCAUD81DInputRec.setAdptSubPerson(pCLSS69DOutputRec.getAdptSubPerson());
		pCAUD81DInputRec.setIdAdptSubPayee(pCLSS69DOutputRec.getIdAdptSubPayee());
		pCAUD81DInputRec.setIdPlcmtEvent(pCLSS69DOutputRec.getIdPlcmtEvent());
		if (null != pCLSS69DOutputRec.getAmtAdptSub()) {
			pCAUD81DInputRec.setAmtAdptSub(pCLSS69DOutputRec.getAmtAdptSub());
		}
		pCAUD81DInputRec.setCdAdptSubDeterm(pCLSS69DOutputRec.getCdAdptSubDeterm());
		pCAUD81DInputRec.setIndAdptSubThirdParty(pCLSS69DOutputRec.getIndAdptSubThirdParty());
		pCAUD81DInputRec.setIndAdptSubProcess(pCLSS69DOutputRec.getIndAdptSubProcess());
		pCAUD81DInputRec.setAdptSubRsn(pCLSS69DOutputRec.getAdptSubRsn());
		pCAUD81DInputRec.setCdAdptSubCloseRsn(ServiceConstants.CHILD_DEATH_CODE);
		pCAUD81DInputRec.setTsLastUpdate(pCLSS69DOutputRec.getTsLastUpdate());
		return pCAUD81DInputRec;
	}

	/**
	 *
	 * Method Name: insertMedicaidUpdate Method Description: Updates Medicaid
	 * Update table.
	 *
	 * @param pCAUD99DInputRec
	 * @return int
	 * @return @
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int insertMedicaidUpdate(MedicaidUpdateDto pCAUD99DInputRec) {
		log.debug("Entering method insertMedicaidUpdate in PersonDetailUpdateServiceImpl");
		int rowcount = 0;
		pCAUD99DInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		try {
			medicaidUpdateDao.addMedicaidUpdate(pCAUD99DInputRec);
			rowcount = 1;
		} catch (DataNotFoundException e) {
			rowcount = 0;
			log.error("No record got inserted into Medicaid update.");
		}
		log.debug("Exiting method insertMedicaidUpdate in PersonDetailUpdateServiceImpl");
		return rowcount;
	}

	/**
	 *
	 * Method Name: MapCINV41DTO Method Description: Maps the input object to
	 * Cinv41diDto
	 *
	 * @param pInputMsg
	 * @return Cinv41diDto @
	 */
	public PersonStageLinkCatgIdInDto mapCINV41DTO(PersonDtlUpdateDto pInputMsg) {
		log.debug("Entering method mapCINV41DTO in PersonDetailUpdateServiceImpl");
		PersonStageLinkCatgIdInDto pCINV41DInputRec = new PersonStageLinkCatgIdInDto();
		pCINV41DInputRec.setDtStagePersLink(new Date());
		pCINV41DInputRec.setCdStagePersType(pInputMsg.getCdStagePersType());
		pCINV41DInputRec.setCdStagePersRole(pInputMsg.getCdStagePersRole());
		pCINV41DInputRec.setCdStagePersRelInt(pInputMsg.getCdStagePersRelInt());
		pCINV41DInputRec.setIndStagePersReporter(pInputMsg.getIndStagePersReporter());
		pCINV41DInputRec.setIdStagePerson(pInputMsg.getIdStagePerson());
		pCINV41DInputRec.setIndCdStagePersSearch(pInputMsg.getIndCdStagePersSearch());
		pCINV41DInputRec.setIndStagePersInLaw(pInputMsg.getIndStagePersInLaw());
		pCINV41DInputRec.setPersonSex(pInputMsg.getPersonSex());
		pCINV41DInputRec.setCdDisasterRlf(pInputMsg.getCdDisasterRlf());
		pCINV41DInputRec.setIndCaringAdult(pInputMsg.getIndCaringAdult());
		pCINV41DInputRec.setNbrPersonAge(pInputMsg.getNbrPersonAge());
		pCINV41DInputRec.setNmPersonFull(pInputMsg.getNmPersonFull());
		pCINV41DInputRec.setNameFirst(pInputMsg.getNmNameFirst());
		pCINV41DInputRec.setNameLast(pInputMsg.getNmNameLast());
		pCINV41DInputRec.setNameMiddle(pInputMsg.getNmNameMiddle());
		pCINV41DInputRec.setCdPersonSuffix(pInputMsg.getCdNameSuffix());
		pCINV41DInputRec.setIndPersonDobApprox(pInputMsg.getIndPersonDobApprox());
		pCINV41DInputRec.setCdPersonDeath(pInputMsg.getCdPersonDeath());
		pCINV41DInputRec.setCdMannerDeath(pInputMsg.getCdMannerDeath());
		pCINV41DInputRec.setCdDeathRsnCps(pInputMsg.getCdDeathRsnCps());
		pCINV41DInputRec.setCdDeathCause(pInputMsg.getCdDeathCause());
		pCINV41DInputRec.setCdDeathAutpsyRslt(pInputMsg.getCdDeathAutpsyRslt());
		pCINV41DInputRec.setCdDeathFinding(pInputMsg.getCdDeathFinding());
		pCINV41DInputRec.setFatalityDetails(pInputMsg.getFatalityDetails());
		pCINV41DInputRec.setCdPersonMaritalStatus(pInputMsg.getCdPersonMaritalStatus());
		pCINV41DInputRec.setCdPersonLanguage(pInputMsg.getCdPersonLanguage());
		pCINV41DInputRec.setCdPersonEthnicGroup(pInputMsg.getCdPersonEthnicGroup());
		// pCINV41DInputRec.setSzCdCategoryCategory(pInputMsg.getSzCdCategoryCategory());
		pCINV41DInputRec.setCdPersonStatus(pInputMsg.getPersonStatus());
		pCINV41DInputRec.setIdStage(pInputMsg.getIdStage());
		pCINV41DInputRec.setIdPerson(pInputMsg.getIdPerson());
		pCINV41DInputRec.setCdPersonChar(pInputMsg.getCdPersonChar());
		pCINV41DInputRec.setCdPersGuardCnsrv(pInputMsg.getCdPersGuardCnsrv());
		pCINV41DInputRec.setCdPersonReligion(pInputMsg.getCdPersonReligion());
		pCINV41DInputRec.setCdPersonLivArr(pInputMsg.getCdPersonLivArr());
		pCINV41DInputRec.setDtPersonDeath(pInputMsg.getDtPersonDeath());
		pCINV41DInputRec.setOccupation(pInputMsg.getOccupation());
		pCINV41DInputRec.setCdOccupation(pInputMsg.getCdOccupation());
		pCINV41DInputRec.setNbrPersonIdNumber(pInputMsg.getNbrPersonIdNumber());
		pCINV41DInputRec.setIdCase(pInputMsg.getIdCase());
		pCINV41DInputRec.setIndEducationPortfolio(pInputMsg.getIndEducationPortfolio());
		pCINV41DInputRec.setCdTribeEligible(pInputMsg.getCdTribeEligible());
		pCINV41DInputRec.setIndNytdDesgContact(pInputMsg.getIndNytdDesgContact());
		pCINV41DInputRec.setIndNytdPrimary(pInputMsg.getIndNytdPrimary());
		pCINV41DInputRec.setReqFuncCd(pInputMsg.getReqFuncCd());
		pCINV41DInputRec.setDtPersonBirth(pInputMsg.getDtPersonBirth());
		pCINV41DInputRec.setTsLastUpdate(pInputMsg.getTsLastUpdate());
		pCINV41DInputRec.setTsSysTsLastUpdate2(pInputMsg.getTsSysTsLastUpdate2());
		log.debug("Exiting method mapCINV41DTO in PersonDetailUpdateServiceImpl");
		return pCINV41DInputRec;
	}

	/**
	 *
	 * Method Name: mapTodoInsUpdDelInDto Method Description:Maps the input
	 * object to TodoInsUpdDelInDto Ccmn43d
	 *
	 * @param personDetailUpdateiDto
	 * @return TodoInsUpdDelInDto
	 */
	public TodoInsUpdDelInDto mapTodoInsUpdDelInDto(PersonDtlUpdateDto personDetailUpdateiDto) {
		log.debug("Entering method mapTodoInsUpdDelInDto in PersonDetailUpdateServiceImpl");
		TodoInsUpdDelInDto todoInsUpdDelInDto = new TodoInsUpdDelInDto();
		Date System_date;
		String tempToDoDescBuffer;
		System_date = null;
		todoInsUpdDelInDto.setCdTodoTask(null);
		todoInsUpdDelInDto.setCdTodoType(ServiceConstants.ALERT);
		todoInsUpdDelInDto.setDtTodoCompleted(System_date);
		todoInsUpdDelInDto.setDtTodoCreated(System_date);
		todoInsUpdDelInDto.setDtTodoDue(System_date);
		todoInsUpdDelInDto.setDtTaskDue(null);
		todoInsUpdDelInDto.setIdCase(personDetailUpdateiDto.getIdCase());
		todoInsUpdDelInDto.setIdEvent(0L);
		todoInsUpdDelInDto.setIdTodoPersAssigned(personDetailUpdateiDto.getIdTodoPersAssigned());
		todoInsUpdDelInDto.setIdTodoPersCreator(0L);
		todoInsUpdDelInDto.setIdTodoPersWorker(0L);
		todoInsUpdDelInDto.setIdStage(personDetailUpdateiDto.getIdStage());
		String buffer = null;
		tempToDoDescBuffer = ServiceConstants.TODO_DESC;
		buffer = replace_str(tempToDoDescBuffer, "{NAME}", personDetailUpdateiDto.getNmPersonFull());
		todoInsUpdDelInDto.setTodoDesc(buffer);
		todoInsUpdDelInDto.setTodoLongDesc(personDetailUpdateiDto.getNmPersonFull());
		todoInsUpdDelInDto.setTodoLongDesc(todoInsUpdDelInDto.getTodoLongDesc() + ServiceConstants.TODO_LONG_DESC);
		// pCCMN43DInputRec.setcReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		todoInsUpdDelInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_ADD);
		/*
		 * pCCMN43DInputRec.setUlIdEvent(pInputMsg.getUlIdEvent());
		 * pCCMN43DInputRec.setUlIdTodoInfo(pInputMsg.getUlIdTodoInfo());
		 * pCCMN43DInputRec.setUlIdTodoPersCreator(pInputMsg.
		 * getUlIdTodoPersCreator());
		 * pCCMN43DInputRec.setUlIdTodoPersWorker(pInputMsg.
		 * getUlIdTodoPersWorker());
		 */
		log.debug("Exiting method mapTodoInsUpdDelInDto in PersonDetailUpdateServiceImpl");
		return todoInsUpdDelInDto;
	}

	/**
	 *
	 * Method Name: mapNameSeqInsUpdDelInDto Method Description:Maps the input
	 * object to Cinv32diDto
	 *
	 * @param personDetailUpdateiDto
	 * @param personDetailUpdateoDto
	 * @return NameSeqInsUpdDelInDto
	 */
	public NameSeqInsUpdDelInDto mapNameSeqInsUpdDelInDto(PersonDtlUpdateDto personDetailUpdateiDto,
														  PersonDetailUpdateDto personDetailUpdateoDto) {
		log.debug("Entering method mapNameSeqInsUpdDelInDto in PersonDetailUpdateServiceImpl");
		NameSeqInsUpdDelInDto nameSeqInsUpdDelInDto = null;
		if (!ObjectUtils.isEmpty(personDetailUpdateiDto.getNameDto())
				&& !ObjectUtils.isEmpty(personDetailUpdateiDto.getNameDto().get(0))) {
			nameSeqInsUpdDelInDto = new NameSeqInsUpdDelInDto();
			nameSeqInsUpdDelInDto.setCdNameSuffix(personDetailUpdateiDto.getNameDto().get(0).getCdNameSuffix());
			nameSeqInsUpdDelInDto.setDtNameEnd(ServiceConstants.GENERIC_END_DATE);
			nameSeqInsUpdDelInDto.setDtNameStart(personDetailUpdateiDto.getNameDto().get(0).getDtNameStart());
			nameSeqInsUpdDelInDto.setIdName(personDetailUpdateiDto.getNameDto().get(0).getIdName());
			nameSeqInsUpdDelInDto.setIdPerson(personDetailUpdateoDto.getIdPerson());
			nameSeqInsUpdDelInDto.setIndNameInvalid(personDetailUpdateiDto.getNameDto().get(0).getIndNameInvalid());
			nameSeqInsUpdDelInDto.setIndNamePrimary(personDetailUpdateiDto.getNameDto().get(0).getIndNamePrimary());
			nameSeqInsUpdDelInDto.setNmNameFirst(personDetailUpdateiDto.getNameDto().get(0).getNmNameFirst());
			nameSeqInsUpdDelInDto.setNmNameLast(personDetailUpdateiDto.getNameDto().get(0).getNmNameLast());
			nameSeqInsUpdDelInDto.setNmNameMiddle(personDetailUpdateiDto.getNameDto().get(0).getNmNameMiddle());
			nameSeqInsUpdDelInDto.setReqFuncCd(personDetailUpdateiDto.getNameDto().get(0).getCdScrDataAction());
			log.debug("Exiting method mapNameSeqInsUpdDelInDto in PersonDetailUpdateServiceImpl");
		}
		return nameSeqInsUpdDelInDto;
	}

	/**
	 *
	 * Method Name: DemoteEvents Method Description: Call set of Demote code
	 * when not in "Approver Mode"
	 *
	 * @param personDetailUpdateiDto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void DemoteEvents(PersonDtlUpdateDto personDetailUpdateiDto) {
		log.debug("Entering method DemoteEvents in PersonDetailUpdateServiceImpl");
		EventUpdEventStatusInDto eventUpdEventStatusInDto = new EventUpdEventStatusInDto();
		// Ccmn62doDto pCCMN62DOutputRec = new Ccmn62doDto();
		ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
		EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
		eventUpdEventStatusInDto.setIdEvent(personDetailUpdateiDto.getIdEvent());
		eventUpdEventStatusInDto.setCdEventStatus(ServiceConstants.EVENT_STATUS_PROCESS);
		eventUpdEventStatusInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		try {
			eventUpdEventStatusDao.updateEvent(eventUpdEventStatusInDto);
		} catch (DataNotFoundException e) {
			log.error("No Data found for the given ulIdEvent ");
		}
		eventStagePersonLinkInsUpdInDto.setIdStage(personDetailUpdateiDto.getIdStage());
		eventStagePersonLinkInsUpdInDto.setCdEventType(ServiceConstants.CONCL_EVENT_TYPE);
		eventStagePersonLinkInsUpdInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpdOutDtos = null;
		try {
			eventStagePersonLinkInsUpdOutDtos = eventStagePersonLinkInsUpdDao
					.getEventAndStatusDtls(eventStagePersonLinkInsUpdInDto);
		} catch (DataNotFoundException eobjCcmn87doDto) {
			log.error("No Data found for the given input ");
		}
		if (null != eventStagePersonLinkInsUpdOutDtos && eventStagePersonLinkInsUpdOutDtos.size() > 0) {
			if (!eventStagePersonLinkInsUpdOutDtos.get(0).getCdEventStatus()
					.equalsIgnoreCase(ServiceConstants.EVENT_STATUS_PENDING)) {
				approvalCommonInDto.setReqFuncCd(personDetailUpdateiDto.getReqFuncCd());
				approvalCommonInDto.setSysNbrReserved1(personDetailUpdateiDto.getSysNbrReserved1());
				approvalCommonInDto.setIdEvent(personDetailUpdateiDto.getIdEvent());
				try {
					approvalCommonService.callCcmn05uService(approvalCommonInDto);
				} catch (DataNotFoundException e) {
					log.error("Error occured while invoking CCMN05 service");
				}
			}
		}
		log.debug("Exiting method DemoteEvents in PersonDetailUpdateServiceImpl");
	}

	/**
	 *
	 * Method Name: verifyCaseName Method Description: This Function is designed
	 * to check for existing case name from the Stage_Person_link table.
	 *
	 * @param personDetailUpdateiDto
	 * @return List<StagePersonLinkNmStageOutDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StagePersonLinkNmStageOutDto> verifyCaseName(PersonDtlUpdateDto personDetailUpdateiDto) {
		log.debug("Entering method verifyCaseName in PersonDetailUpdateServiceImpl");
		StagePersonLinkNmStageInDto stagePersonLinkNmStageInDto = new StagePersonLinkNmStageInDto();
		stagePersonLinkNmStageInDto.setIdPerson(personDetailUpdateiDto.getIdPerson());
		stagePersonLinkNmStageInDto.setIdStage(personDetailUpdateiDto.getIdStage());
		List<StagePersonLinkNmStageOutDto> stagePersonLinkNmStageOutDtos = stagePersonLinkNmStageDao
				.verifyPersonDeleted(stagePersonLinkNmStageInDto);
		log.debug("Exiting method verifyCaseName in PersonDetailUpdateServiceImpl");
		return stagePersonLinkNmStageOutDtos;
	}

	/**
	 *
	 * Method Name: CallVerifyTlets Method Description: This Function is
	 * designed to check for existing person from the Tlets table.
	 *
	 * @param personDetailUpdateiDto
	 * @return List<TletsCheckOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<TletsCheckOutDto> verifyTlets(PersonDtlUpdateDto personDetailUpdateiDto) {
		log.debug("Entering method verifyTlets in PersonDetailUpdateServiceImpl");
		TletsCheckInDto tletsCheckInDto = new TletsCheckInDto();
		tletsCheckInDto.setIdPerson(personDetailUpdateiDto.getIdPerson());
		List<TletsCheckOutDto> tletsCheckOutDtos = tletsCheckDao.verifyTLETSPerson(tletsCheckInDto);
		log.debug("Exiting method verifyTlets in PersonDetailUpdateServiceImpl");
		return tletsCheckOutDtos;
	}

	public IncomingPersonMpsRes personDetailUpdateForMPS(IncomingPersonMpsDto incomingPersonMpsDto){
		IncomingPersonMpsRes personMpsRes = new IncomingPersonMpsRes();
		incomingPersonMpsDto = personStageLinkCatgIdDao.updateMPSPersonDetail(incomingPersonMpsDto);
		personMpsRes.setIncomingPersonMpsDto(incomingPersonMpsDto);
		return personMpsRes;
	}


	@Override
	public PersonIdDto addMpsPersonDetails(Long idMPSPerson) {
		PersonIdDto personDto =  personStageLinkCatgIdDao.addMPSPersonDetails(idMPSPerson);
		Person person = personStageLinkCatgIdDao.getPersonDtl(personDto.getIdPerson());
		IncomingPersonMps incomingPersonMps = personStageLinkCatgIdDao.getIncomingMPSPersonDetail(idMPSPerson);
		long idPerson = person.getIdPerson();
		personStageLinkCatgIdDao.saveMPSPersonRace(idMPSPerson, person);
		personStageLinkCatgIdDao.saveMPSPersonEthniciy(person , incomingPersonMps);
		if(incomingPersonMps.getCdAddrType()!=null && !StringUtils.isEmpty(incomingPersonMps.getCdAddrType())) {
			personStageLinkCatgIdDao.saveMPSPersonAddress(idPerson, incomingPersonMps);
		}
		if(incomingPersonMps.getCdPhoneType()!=null && !StringUtils.isEmpty(incomingPersonMps.getCdPhoneType())) {
			personStageLinkCatgIdDao.saveMPSPhone(person, incomingPersonMps);
		}
		if(incomingPersonMps.getCdEmailType()!=null && !StringUtils.isEmpty(incomingPersonMps.getCdEmailType())) {
			personStageLinkCatgIdDao.saveMPSEmail(person, incomingPersonMps);
		}
		personStageLinkCatgIdDao.saveMPSPersonInd(person, incomingPersonMps);
		return personDto;
	}

	/**
	 *
	 * Method Name: replace_str Method Description: Replaces substr with value
	 * passed as rep
	 *
	 * @param orig
	 * @param substr
	 * @param rep
	 * @return String
	 */
	public String replace_str(String orig, String substr, String rep) {
		String p = null;
		if (orig.contains(substr)) {
			p = orig.replace(substr, rep);
		}
		return p;
	}
}
