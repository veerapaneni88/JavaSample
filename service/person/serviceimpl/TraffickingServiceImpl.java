/**
 * 
 */
package us.tx.state.dfps.service.person.serviceimpl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.ChildSxVctmztnIncdnt;
import us.tx.state.dfps.common.domain.EmpSecClassLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.admin.dao.EligibilityDao;
import us.tx.state.dfps.service.admin.dao.EmpSecClassLinkDao;
import us.tx.state.dfps.service.admin.dto.EligibilityInDto;
import us.tx.state.dfps.service.admin.dto.EligibilityOutDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.dto.WorkLoadDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.TraffickingReq;
import us.tx.state.dfps.service.common.response.TraffickingRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.commontodofunction.service.CommonToDoFunctionService;
import us.tx.state.dfps.service.fcl.dao.SexualVictimizationHistoryDao;
import us.tx.state.dfps.service.fcl.service.SexualVictimizationHistoryService;
import us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao;
import us.tx.state.dfps.service.person.dao.TraffickingDao;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.person.service.TraffickingService;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 22, 2018- 5:26:53 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
public class TraffickingServiceImpl implements TraffickingService {

	@Autowired
	TraffickingDao traffickingDao;

	@Autowired
	SexualVictimizationHistoryService sexualVictimizationHistoryService;

	@Autowired
	SexualVictimizationHistoryDao sexualVictimizationHistoryDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	EligibilityDao eligibilityDao;

	@Autowired
	LegalStatusDao legalStatusDao;

	@Autowired
	EmpSecClassLinkDao empSecClassLinkDao;

	@Autowired
	private CommonToDoFunctionService commonToDoFunctionService;


	private Predicate<TraffickingDto> confirmedSxIndPredicate = traffickingDto -> (CodesConstant.CTRFTYP_SXTR.equals(traffickingDto.getCdtrfckngType()) &&
            CodesConstant.CTRFSTAT_CONF.equals(traffickingDto.getCdtrfckngStat()));
	
	

	/**
	 * Method Description: This method is used to call TraffickingDao method to
	 * retrieve information for populating Texas Law Enforcement
	 * Telecommunications System (Trafficking) List window.
	 * 
	 * @param traffickingReq
	 * @return trfckngRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public TraffickingRes getTraffickingList(TraffickingReq traffickingReq) {
		SimpleDateFormat sm = new SimpleDateFormat("MM/dd/yyyy");
		TraffickingRes trfckngRes = new TraffickingRes();
		List<TraffickingDto> traffickingList = new ArrayList<>();
		traffickingList = traffickingDao.getTraffickingList(traffickingReq.getTraffickingDto());
				
		trfckngRes.setTraffickingDtoList(traffickingList);
		return trfckngRes;
	}

	/**
	 * Method Description: This method is used to call TraffickingDao
	 * Trafficking Check window.
	 * 
	 * @param traffickingReq
	 * @return TraffickingRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public TraffickingRes getTraffickingDtl(TraffickingReq traffickingReq) {
		
		
		TraffickingRes traffickingRes = new TraffickingRes();
		List<TraffickingDto> traffickingList = traffickingDao.getTraffickingList(traffickingReq.getTraffickingDto());
		
		
		
		if (!traffickingList.isEmpty()) {
				
			traffickingRes.setTraffickingDtoList(traffickingList);
		}
		return traffickingRes;
	}

	/**
	 * Method Description: This method is used to call TraffickingDao method to
	 * Retrieve and Add Trafficking Details Service Name :
	 * saveTraffickingDetails
	 * 
	 * @param traffickingReq
	 * @return TraffickingRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TraffickingRes saveTraffickingDetails(TraffickingReq traffickingReq) {
		TraffickingRes traffickingRes = new TraffickingRes();
		TraffickingDto traffickingDto = traffickingReq.getTraffickingDto();
		if (!TypeConvUtil.isNullOrEmpty(traffickingDto)) {
			traffickingDao.saveTrafficking(traffickingDto);
		}

		// get the current victimization history or a new one
		SexualVictimHistoryDto existingVictimizationData = sexualVictimizationHistoryService.getSexualVictimHistoryDto(traffickingDto.getIdPerson().longValue());
		if (ObjectUtils.isEmpty(existingVictimizationData)) {
			existingVictimizationData = new SexualVictimHistoryDto();
			existingVictimizationData.setIdPerson(traffickingDto.getIdPerson().longValue());
			existingVictimizationData.setIdCreatedPerson(traffickingDto.getIdCreatedPerson().longValue());
			existingVictimizationData.setIdLastUpdatedPerson(traffickingDto.getIdCreatedPerson().longValue());
		}

		// artf130771 - set SVH indicator, but only for confirmed sex trafficking.
		boolean dirty = updateDtoWithVictimization(existingVictimizationData);

		if (dirty) {
			sexualVictimizationHistoryService.saveOrUpdateSexualVictimHistory(existingVictimizationData);
		}

		return traffickingRes;
	}

	/**
	 * Method Description: This method is used to call TraffickingDao method to
	 * update Trafficking Details
	 * Service Name : updateTraffickingDetails
	 *
	 * @param traffickingReq
	 * @return TraffickingRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TraffickingRes updateTraffickingDetails(TraffickingReq traffickingReq) {
		TraffickingRes traffickingRes = new TraffickingRes();
		TraffickingDto traffickingDto = traffickingReq.getTraffickingDto();

		if (!TypeConvUtil.isNullOrEmpty(traffickingDto) && !TypeConvUtil.isNullOrEmpty(traffickingDto.getIdTrfckngDtl())) {
			traffickingDao.updateTrafficking(traffickingDto);
		}

		SexualVictimHistoryDto existingVictimizationData = sexualVictimizationHistoryService.getSexualVictimHistoryDto(traffickingDto.getIdPerson().longValue());
		if (ObjectUtils.isEmpty(existingVictimizationData)) {
			existingVictimizationData = new SexualVictimHistoryDto();
			existingVictimizationData.setIdPerson(traffickingDto.getIdPerson().longValue());
			existingVictimizationData.setIdCreatedPerson(traffickingDto.getIdCreatedPerson().longValue());
			existingVictimizationData.setIdLastUpdatedPerson(traffickingDto.getIdCreatedPerson().longValue());
		}

		boolean dirty = updateDtoWithVictimization(existingVictimizationData);
		if (dirty) {
			sexualVictimizationHistoryService.saveOrUpdateSexualVictimHistory(existingVictimizationData);
		}

		return traffickingRes;
	}

	private boolean updateDtoWithVictimization(SexualVictimHistoryDto sexualVictimHistoryDto) {
		boolean dirty = false;
		// look for any unconfirmed trafficking
		TraffickingDto paramGetList = new TraffickingDto();
		paramGetList.setIdPerson(BigDecimal.valueOf(sexualVictimHistoryDto.getIdPerson()));
		List<TraffickingDto> traffickingList = traffickingDao.getTraffickingList(paramGetList);
		boolean hasConfTraf = traffickingList.stream().anyMatch(currTrafficking -> CodesConstant.CTRFTYP_SXTR.equals(currTrafficking.getCdtrfckngType()) &&
				CodesConstant.CTRFSTAT_CONF.equals(currTrafficking.getCdtrfckngStat()));
		boolean hasUncTraf = traffickingList.stream().anyMatch(currTrafficking -> CodesConstant.CTRFTYP_SXTR.equals(currTrafficking.getCdtrfckngType()) &&
				CodesConstant.CTRFSTAT_SUSP.equals(currTrafficking.getCdtrfckngStat()));
		List<ChildSxVctmztnIncdnt> vicList = sexualVictimizationHistoryDao.fetchSexualVictimHistory(sexualVictimHistoryDto.getIdPerson());
		boolean hasVicIncdnt = !ObjectUtils.isEmpty(vicList);

		// artf278177 update the confirmed question as needed.
		if (hasConfTraf && (
				ObjectUtils.isEmpty(sexualVictimHistoryDto.getIndChildHasSxVictimHistory()) ||
						ServiceConstants.NO.equals(sexualVictimHistoryDto.getIndChildHasSxVictimHistory()))) {
			sexualVictimHistoryDto.setIndChildHasSxVictimHistory(ServiceConstants.YES);
			dirty = true;
		}

		// artf279006 don't update confirmed incident to N if there are still victimization incidents forcing Y
		if (!hasConfTraf && !hasVicIncdnt && !ObjectUtils.isEmpty(sexualVictimHistoryDto.getIndChildHasSxVictimHistory()) &&
				ServiceConstants.YES.equals(sexualVictimHistoryDto.getIndChildHasSxVictimHistory())) {
			sexualVictimHistoryDto.setIndChildHasSxVictimHistory(ServiceConstants.NO);
			dirty = true;
		}

		// update the unconfirmed question as needed.
		// 12.1.2.1: System will designate a youth of having unconfirmed history of sexual victimization by displaying "Yes" for a question 'Does this child/youth have an unconfirmed history of sexual victimization'
		// Exception: If user had already updated this question to "Yes" with text box description manually, system will NOT overwrite
		if (hasUncTraf && (
				ObjectUtils.isEmpty(sexualVictimHistoryDto.getIndUnconfirmedVictimHistory()) ||
						ServiceConstants.NO.equals(sexualVictimHistoryDto.getIndUnconfirmedVictimHistory()))) {
			sexualVictimHistoryDto.setIndUnconfirmedVictimHistory(ServiceConstants.YES);
			dirty = true;
		}

		// If User modifies existing "Sex Trafficking" incident from "Suspected-Unconfirmed" to "Confirmed" OR
		// If User deletes existing "Suspected-Unconfirmed" Sex Trafficking incident
		// Then 12.1.2.4: System will update the answer for question 'Does this child/youth have an unconfirmed history of sexual victimization' to "No"
		// artf283322 System should retain "Yes" for the unconfirmed history of sexual victimization question because it was user who manually answered Y
		if (!hasUncTraf && !ObjectUtils.isEmpty(sexualVictimHistoryDto.getIndUnconfirmedVictimHistory()) &&
				ServiceConstants.YES.equals(sexualVictimHistoryDto.getIndUnconfirmedVictimHistory())  &&
				sexualVictimHistoryDto.getIdUpdPrsUnconfVicHistUser() == null) {
			sexualVictimHistoryDto.setIndUnconfirmedVictimHistory(ServiceConstants.NO);
			dirty = true;
		}
		return dirty;
	}

	/**
	 * Method Description: This method is used to call TraffickingDao method to
	 * delete Trafficking Details based on conditions and also insert Task into todo table.
	 * Service Name : deleteTraffickingDetails
	 *
	 * @param traffickingReq
	 * @return TraffickingRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TraffickingRes deleteTraffickingDetails(TraffickingReq traffickingReq) {
		TraffickingRes traffickingRes = new TraffickingRes();

		if (!TypeConvUtil.isNullOrEmpty(traffickingReq.getTraffickingDto().getIdTrfckngDtl())) {
			Boolean traffickingDtlDeleted = traffickingDao.deleteTrafficking(traffickingReq.getTraffickingDto().getIdTrfckngDtl());

			// 2. The person has an open SUB stage.
			Long idPerson = traffickingReq.getTraffickingDto().getIdPerson().longValue();
			Long subOpenStagesCnt = stageDao.getSUBOpenStagesCount(idPerson, "PC");

			// 3. The person has an Actual Eligibility of Title IV-E.
			List<EligibilityOutDto> eligibilityRecordList = null;
			EligibilityInDto eligibilityInputDto = new EligibilityInDto();
			eligibilityInputDto.setIdPerson(idPerson);
			eligibilityInputDto.setDtScrDtCurrentDate(new Date());
			// Call CSES38D
			eligibilityRecordList = eligibilityDao.getEligibilityRecord(eligibilityInputDto);
			Boolean actualElgTitleIVE = eligibilityRecordList.stream().anyMatch(e -> e.getCdEligActual().equalsIgnoreCase(ServiceConstants.IV_E));
			if(actualElgTitleIVE) {
				EligibilityOutDto eligibilityOutDto = eligibilityRecordList.stream().filter(e -> e.getCdEligActual().equalsIgnoreCase(ServiceConstants.IV_E)).findFirst().get();

				// The system will send a task to the assigned secondary eligibility specialist or the primary caseworker if the secondary eligibility specialist is not found
				// Find secondary eligibility specialist or the primary caseworker if the secondary eligibility specialist is not found
				List<WorkLoadDto> workLoadDtoList = this.findWhoToSendToDo(eligibilityOutDto.getIdStage());
				// 4.	There are closed congregate care placements in the person's open SUB stage.
				// PLACEMENT TABLE.  IND_CONGREGATE_CARE Flag
				Long idCase = eligibilityOutDto.getIdCase();
				Long closedCongregateSubStagesCnt = stageDao.getSUBStagesCloseCongregateCareCount(idCase);
				if(subOpenStagesCnt > 0 && closedCongregateSubStagesCnt > 0 && traffickingDtlDeleted) {
					Long idEvent = eligibilityOutDto.getIdEligibilityEvent();
					// create task in TODO TABLE.
					createHumanTraffickingToDo(idEvent, eligibilityOutDto.getIdStage(), workLoadDtoList.get(0).getIdWrldPerson());
				}
			}

			SexualVictimHistoryDto existingVictimizationData = sexualVictimizationHistoryService.getSexualVictimHistoryDto(
					traffickingReq.getTraffickingDto().getIdPerson().longValue());
			if (ObjectUtils.isEmpty(existingVictimizationData)) {
				existingVictimizationData = new SexualVictimHistoryDto();
				existingVictimizationData.setIdPerson(traffickingReq.getTraffickingDto().getIdPerson().longValue());
				existingVictimizationData.setIdCreatedPerson(traffickingReq.getTraffickingDto().getIdCreatedPerson().longValue());
				existingVictimizationData.setIdLastUpdatedPerson(traffickingReq.getTraffickingDto().getIdCreatedPerson().longValue());
			}

			boolean dirty = updateDtoWithVictimization(existingVictimizationData);
			if (dirty) {
				sexualVictimizationHistoryService.saveOrUpdateSexualVictimHistory(existingVictimizationData);
			}
		}

		return traffickingRes;
	}

	/**
	 * Method Name: createHumanTraffickingToDo
	 * Method Description:This method insert Task into todo table with 3126 todo task type.
	 *
	 * @param stageId
	 * @return void
	 */
	private void createHumanTraffickingToDo(Long idEvent, Long stageId, Long userId) {
		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
		MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
		mergeSplitToDoDto.setCdTodoCf(ServiceConstants.HT_CD_TODO_INFO);
		mergeSplitToDoDto.setDtTodoCfDueFrom(null);
		mergeSplitToDoDto.setIdTodoCfPersCrea(userId);
		mergeSplitToDoDto.setIdTodoCfStage(stageId);
		mergeSplitToDoDto.setIdTodoCfPersAssgn(userId);	// Assign secondary eligibility specialist or Primary caseworker
		mergeSplitToDoDto.setIdTodoCfPersWkr(userId);
		if (!StringUtils.isEmpty(idEvent)) {
			mergeSplitToDoDto.setIdTodoCfEvent(idEvent);
		}
		todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);
		// Insert into TODO TABLE
		commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);
	}

	/**
	 *
	 * Method Name: findWhoToSendToDo
	 * Method Description: Find if there is a secondary worker and also keep the index of
	 * primary worker. if there is no secondary worker we need to send the todo to primary.
	 *
	 * @param idStage
	 * @return List<WorkLoadDto>
	 */
	private List<WorkLoadDto> findWhoToSendToDo(Long idStage){

		List<WorkLoadDto> workloadDtoList = new ArrayList<>();
		int i = -1;
		int primaryWorkerIndex = 0;
		boolean secondaryWorkerExist = ServiceConstants.FALSEVAL;
		boolean indEmpIsSpecialist = ServiceConstants.FALSEVAL;

		// callCSEC86D
		List<WorkLoadDto> fetchedWorkloadDtoList = legalStatusDao.getWorkLoadsForStage(idStage);

		/*
		 * Find if there is a secondary worker and also keep the index of
		 * primary worker. if there is no secondary worker we need to send the
		 * todo to primary.
		 */
		if (!CollectionUtils.isEmpty(fetchedWorkloadDtoList) && fetchedWorkloadDtoList.size() > 0) {
			for (i = 0; i < fetchedWorkloadDtoList.size(); i++) {
				if (fetchedWorkloadDtoList.get(i).getCdWkldStagePersRole()
						.equalsIgnoreCase(ServiceConstants.PERSON_ROLE_PRIMARY)) {
					primaryWorkerIndex = i;
				} else if (fetchedWorkloadDtoList.get(i).getCdWkldStagePersRole()
						.equalsIgnoreCase(ServiceConstants.PERSON_ROLE_SECONDARY)) {
					secondaryWorkerExist = ServiceConstants.TRUEVAL;
					if(primaryWorkerIndex!=-1){
						break;
					}
				}
			} /* end for loop */
		}

		// if there is a secondary worker, check to see if they are eligibility specialists.
		for (i = 0; i < fetchedWorkloadDtoList.size() && secondaryWorkerExist; i++) {
			indEmpIsSpecialist = ServiceConstants.FALSEVAL;
			if (fetchedWorkloadDtoList.get(i).getCdWkldStagePersRole()
					.equalsIgnoreCase(ServiceConstants.PERSON_ROLE_SECONDARY)) {
				// CallCLSCB4D
				indEmpIsSpecialist = this.getEmployeeSecurityProfile(fetchedWorkloadDtoList.get(i).getIdWrldPerson());
			}
			if (indEmpIsSpecialist) {
				workloadDtoList.add(fetchedWorkloadDtoList.get(i));
			}
		}
		if (workloadDtoList.size() == 0) {
			workloadDtoList.add(fetchedWorkloadDtoList.get(primaryWorkerIndex));
		}
		return workloadDtoList;
	}

	/**
	 *
	 * Method Name: getEmployeeSecurityProfile Method Description: call service
	 * CLSCB4D , check employee security profile
	 *
	 * @param idPerson
	 * @return
	 */
	private Boolean getEmployeeSecurityProfile(long idPerson) {
		Boolean empHasSecurity = Boolean.FALSE;
		List<EmpSecClassLink> empSecClassLinkList = empSecClassLinkDao.getEmployeeSecurityProfile(idPerson);
		for (EmpSecClassLink empSecClassLink : empSecClassLinkList) {
			if (ServiceConstants.CHAR_ONE == empSecClassLink.getSecurityClass().getTxtSecurityClassProfil()
					.charAt(11)) {
				empHasSecurity = Boolean.TRUE;
				break;
			}
		}
		return empHasSecurity;
	}/* CallCLSCB4D */

	/**
	 * Method Description: This method is to get Earliest Intake date Service
	 * Name : getIntakeDate
	 * 
	 * @param idPerson
	 * @return Date
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public Date getIntakeDate(Long idPerson) {
		return traffickingDao.getIntakeDate(idPerson);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public String getNmPerson(long idPerson) {
		return traffickingDao.getNMPerson(idPerson);
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public TraffickingRes getPerson(TraffickingReq traffickingReq) {
		
		TraffickingRes traffickingRes = new  TraffickingRes();
		Person person =	traffickingDao.getPerson(traffickingReq.getTraffickingDto().getIdPerson().longValue());
		TraffickingDto traffickingDto = new TraffickingDto();
		traffickingDto.setIdPerson(new BigDecimal(person.getIdPerson()));
		traffickingRes.setIdPerson(person.getIdPerson());
		traffickingDto.setDtPersonBirth(person.getDtPersonBirth());
		traffickingDto.setDtPersonDeath(person.getDtPersonDeath());
		traffickingRes.setTraffickingDto(traffickingDto);
		return traffickingRes;
	}
	
	/**
	 * Method Description: This method returns true if there are any confirmed Sex
	 * Trafficking Incidents for a person
	 * 
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public boolean getConfirmedSxTraffickingIndicator(long idPerson) {

		TraffickingDto traffickingDto = new TraffickingDto();
		traffickingDto.setIdPerson(BigDecimal.valueOf(idPerson));
		boolean confirmedSxIndicator = false;

		List<TraffickingDto> traffickingList = traffickingDao.getTraffickingList(traffickingDto);

		if (!ObjectUtils.isEmpty(traffickingList)) {

			confirmedSxIndicator = traffickingList.stream().anyMatch(confirmedSxIndPredicate);

		}

		return confirmedSxIndicator;
	}
	
}