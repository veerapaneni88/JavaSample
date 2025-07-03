/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 7, 2018- 10:43:17 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.serviceimpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.FamilyPlanEvalReq;
import us.tx.state.dfps.service.forms.dao.FamilyPlanEvalDao;
import us.tx.state.dfps.service.forms.dto.FamilyAssmtFactDto;
import us.tx.state.dfps.service.forms.dto.FamilyChildNameGaolDto;
import us.tx.state.dfps.service.forms.dto.FamilyDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvaItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanGoalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanParticipantsDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanTaskDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.service.FamilyPlanEvalService;
import us.tx.state.dfps.service.forms.util.FamilyPlanPrefillData;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 7, 2018- 10:43:17 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class FamilyPlanEvalServiceImpl implements FamilyPlanEvalService {

	@Autowired
	private PcaDao pcaDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private EventIdDao eventIdDao;

	@Autowired
	private FamilyPlanEvalDao familyPlanEvalDao;

	@Autowired
	private FamilyPlanPrefillData familyPlanPrefillData;

	@Autowired
	LookupDao lookupDao;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getFamilyPlanService(FamilyPlanEvalReq familyPlanEvalReq) {
		FamilyDto familyDto = new FamilyDto();
		Long idPersonWorker = ServiceConstants.NULL_VAL;
		Date effectiveDate = ServiceConstants.NULL_VALDAT;

		// call CSEC02D
		StageCaseDtlDto stageCaseDtlDto = pcaDao.getStageAndCaseDtls(familyPlanEvalReq.getIdStage());
		familyDto.setStageCaseDtlDto(stageCaseDtlDto);
		// call CCMN19D
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(familyPlanEvalReq.getIdStage(),
				ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
		familyDto.setStagePersonDto(stagePersonDto);
		if (!ObjectUtils.isEmpty(familyDto.getStagePersonDto().getIdTodoPersWorker())) {
			idPersonWorker = familyDto.getStagePersonDto().getIdTodoPersWorker();
			// call CSEC01D
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPersonWorker);
			familyDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}
		// call CSVC43D
		FamilyPlanEvalDto familyPlanEvalDto = familyPlanEvalDao
				.getFamilyPlanEvalTable(familyPlanEvalReq.getIdSvcPlnEvalEvent());
		familyDto.setFamilyPlanEvalDto(familyPlanEvalDto);
		if (ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto())) {
			FamilyPlanEvalDto familyPlanEvalDto2 = new FamilyPlanEvalDto();
			familyPlanEvalDto2.setIdFamilyPlanEvaluation(0l);
			familyPlanEvalDto2.setIdEvent(0l);
			familyDto.setFamilyPlanEvalDto(familyPlanEvalDto2);
		}
		// call CSVC41D
		FamilyPlanDto familyPlanDto = familyPlanEvalDao.getFamilyPlanTable(familyPlanEvalReq.getIdEvent());
		if (!ObjectUtils.isEmpty(familyDto.getFamilyPlanEvalDto())) {
			familyPlanDto.setIdEvalEvent(familyDto.getFamilyPlanEvalDto().getIdEvent());
		}
		familyDto.setFamilyPlanDto(familyPlanDto);
		// call CSVC40D
		List<FamilyPlanEvaItemDto> familyPlanEvaItemDto = familyPlanEvalDao
				.getFamilyPlanEvalItems(familyPlanEvalReq.getIdEvent());
		familyDto.setEvalItemList(familyPlanEvaItemDto);
		// call CCMN45D
		EventIdInDto pCCMN45DInputRec = new EventIdInDto();
		pCCMN45DInputRec.setIdEvent(familyPlanEvalReq.getIdEvent());
		List<EventIdOutDto> eventIdOutDto = eventIdDao.getEventDetailList(pCCMN45DInputRec);
		familyDto.setEventList(eventIdOutDto);
		if (!ObjectUtils.isEmpty(familyDto.getEventList())) {
			for (EventIdOutDto eventIdOutDto2 : familyDto.getEventList()) {
				if (!ObjectUtils.isEmpty(eventIdOutDto2.getTsLastUpdate())) {
					effectiveDate = eventIdOutDto2.getTsLastUpdate();
				} else {
					effectiveDate = lookupDao.getCurrentDate();
				}
			}
		}

		// call CLSC23D
		List<FamilyAssmtFactDto> familyAssmtFactDto = familyPlanEvalDao
				.getDistinctPricipleNames(familyPlanEvalReq.getIdSvcPlnEvalEvent(), effectiveDate);
		if (!ObjectUtils.isEmpty(familyAssmtFactDto)) {
			for (FamilyAssmtFactDto familyAssmtFactDto2 : familyAssmtFactDto) {
				// Warranty Defect - 11974 - To set the Person Type as Child
				// Warranty Defect - 11696 - Added Null Pointer Check to prevent Form Launch Issue
				if ((!ObjectUtils.isEmpty(familyAssmtFactDto2.getCdPersonMaritalStatus()) && !familyAssmtFactDto2.getCdPersonMaritalStatus().equalsIgnoreCase("CH"))
						|| familyAssmtFactDto2.getCdStagePersRelInt().equalsIgnoreCase("PA")
						|| familyAssmtFactDto2.getCdStagePersRelInt().equalsIgnoreCase("PB")) {
					familyAssmtFactDto2.setCdStagePersType(ServiceConstants.ADULT_TYPE);
				} else {
					familyAssmtFactDto2.setCdStagePersType(ServiceConstants.CHILD_TYPE);
				}
			}
		}
		familyDto.setAssessmentList(familyAssmtFactDto);
		// call CLSCB5D
		List<FamilyChildNameGaolDto> familyChildNameGaolDto = familyPlanEvalDao
				.getFamilyChildNameGaol(familyPlanEvalReq.getIdSvcPlnEvalEvent());
		familyDto.setGoalsList(familyChildNameGaolDto);
		// call CLSC24D
		List<FamilyPlanParticipantsDto> familyPlanParticipantsDto = familyPlanEvalDao
				.getFamilyPlanParticipants(familyPlanEvalReq.getIdSvcPlnEvalEvent(), effectiveDate);
		familyDto.setParticipantsList(familyPlanParticipantsDto);
		// call CSVC56D
		List<FamilyPlanGoalDto> familyPlanGoalDto = familyPlanEvalDao
				.getFamilyPlanGoals(familyPlanEvalReq.getIdEvent());
		familyDto.setFamilyPlanList(familyPlanGoalDto);
		// call CSVC52D
		List<FamilyPlanTaskDto> familyPlanTaskDto = familyPlanEvalDao.getFamilyPlanTasks(familyPlanEvalReq.getIdEvent(),
				familyPlanEvalReq.getIdSvcPlnEvalEvent());
		familyDto.setTaskList(familyPlanTaskDto);
		// call CSVC54D
		List<FamilyPlanItemDto> familyPlanItemDto = familyPlanEvalDao
				.getInitialConcernsPlan(familyPlanEvalReq.getIdEvent(), ServiceConstants.DT_APR08_ROLLOUT);
		familyDto.setItemList(familyPlanItemDto);
		// call CSVC58D
		List<FamilyPlanEvalDto> familyPlanEvalDto1 = familyPlanEvalDao
				.getFamDateComplete(familyPlanEvalReq.getIdEvent());
		familyDto.setDateCompList(familyPlanEvalDto1);
		// call CSESC8D
		EventDto eventDto = familyPlanEvalDao.getIndicatartorVals(ServiceConstants.DT_APR08_ROLLOUT_2,
				ServiceConstants.INIT_CONCERNS_VERSION_3, ServiceConstants.INIT_CONCERNS_VERSION_2,
				familyPlanEvalReq.getIdEvent());
		familyDto.setEventDto(eventDto);
		// call CSVC53D
		List<FamilyPlanDto> dateCompletedList = familyPlanEvalDao.getDateCompletedEval(familyPlanEvalReq.getIdEvent(),
				ServiceConstants.DT_APR08_ROLLOUT);
		familyDto.setDateCompletedList(dateCompletedList);
		// call CSVC55D
		List<FamilyPlanDto> initConcernsList = familyPlanEvalDao.getInitialConcerns(familyPlanEvalReq.getIdEvent(),
				ServiceConstants.DT_APR08_ROLLOUT);
		familyDto.setInitConcernsList(initConcernsList);

		return familyPlanPrefillData.returnPrefillData(familyDto);
	}

}
