package us.tx.state.dfps.service.familyplan.serviceimpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.ServPlanEvalRecDto;
import us.tx.state.dfps.common.dto.ServicePlanDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanDao;
import us.tx.state.dfps.service.familyplan.dao.FamilyServicePlanDao;
import us.tx.state.dfps.service.familyplan.service.FamilyServicePlanService;
import us.tx.state.dfps.service.forms.dao.FamilyPlanEvalDao;
import us.tx.state.dfps.service.forms.dto.FamilyAssmtFactDto;
import us.tx.state.dfps.service.forms.dto.FamilyServicePlanDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.FamilyServicePlanPrefillData;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes dao
 * calls and returns prefill string for form CFSD0500 May 2, 2018- 4:36:00 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class FamilyServicePlanServiceImpl implements FamilyServicePlanService {

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private FamilyPlanDao familyPlanDao;

	@Autowired
	private FamilyServicePlanDao familyServicePlanDao;

	@Autowired
	private ChildServicePlanFormDao childServicePlanFormDao;

	@Autowired
	private FamilyPlanEvalDao familyPlanEvalDao;

	@Autowired
	private FamilyServicePlanPrefillData prefillData;

	/**
	 * Method Name: getServicePlan Method Description: Makes dao calls and
	 * returns prefill string for Family Service Plan
	 * 
	 * @param req
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getServicePlan(CommonApplicationReq req) {
		// Declare main dto and global variables
		FamilyServicePlanDto prefillDto = new FamilyServicePlanDto();
		Long idPerson = ServiceConstants.ZERO_VAL;
		Date effectiveDate = new Date();

		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(req.getIdStage());
		prefillDto.setGenericCaseInfoDto(genericCaseInfoDto);

		// CCMN19D
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(req.getIdStage(),
				ServiceConstants.PRIMARY_WORKER_ROLE);
		if (!ObjectUtils.isEmpty(stagePersonDto)) {
			idPerson = stagePersonDto.getIdTodoPersWorker();
			prefillDto.setStagePersonDto(stagePersonDto);

			// CSEC01D
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPerson);
			if (!ObjectUtils.isEmpty(employeePersPhNameDto)) {
				if (!((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
						|| (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType()))) {
					employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getNbrMailCodePhone());
					employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrMailCodePhoneExt());

				}
				prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
			}
		}

		// CSVC04D
		ServicePlanDto servicePlanDto = familyPlanDao.getServicePlanByIdEvent(req.getIdEvent());
		if (!ObjectUtils.isEmpty(servicePlanDto)) {
			servicePlanDto.setDtLastUpdate(null);
			if (!ObjectUtils.isEmpty(servicePlanDto.getDtSvcPlanNextRevw())) {
				servicePlanDto.setDtLastUpdate(servicePlanDto.getDtSvcPlanNextRevw());
			}
			String dateString = DateUtils.stringDt(servicePlanDto.getDtLastUpdate());
			genericCaseInfoDto.setTmScrTmGeneric1(dateString.substring(0, 3) + dateString.substring(6));
			prefillDto.setGenericCaseInfoDto(genericCaseInfoDto);
			prefillDto.setServicePlanDto(servicePlanDto);
		}

		// CSVC11D
		List<ServPlanEvalRecDto> servicePlanItemsList = familyServicePlanDao.getServicePlanItems(req.getIdEvent(),
				ServiceConstants.ZERO_VAL);
		prefillDto.setServicePlanItemsList(servicePlanItemsList);

		// CLSS27D
		List<ServPlanEvalRecDto> servicePlanProblemsList = familyServicePlanDao
				.getServicePlanProblems(req.getIdEvent());
		prefillDto.setServicePlanProblemsList(servicePlanProblemsList);

		// CLSS28D
		List<ServPlanEvalRecDto> servicePlanGoalsList = familyServicePlanDao.getServicePlanGoals(req.getIdEvent());
		prefillDto.setServicePlanGoalsList(servicePlanGoalsList);

		// CCMN45D
		List<EventDto> eventList = childServicePlanFormDao.fetchEventDetails(req.getIdEvent());
		if (!ObjectUtils.isEmpty(eventList)) {
			EventDto eventDto = eventList.get(0);
			if (ServiceConstants.APPROVED.equals(eventDto.getCdEventStatus())) {
				effectiveDate = eventDto.getDtLastUpdate();
			}
			prefillDto.setEventDto(eventDto);
		}

		// CLSC23D
		List<FamilyAssmtFactDto> familyAssmtList = familyPlanEvalDao.getDistinctPricipleNames(req.getIdEvent(),
				effectiveDate);
		if (!ObjectUtils.isEmpty(familyAssmtList)) {
			Boolean firstAdult = ServiceConstants.FALSEVAL;
			Boolean firstChild = ServiceConstants.FALSEVAL;
			for (FamilyAssmtFactDto familyAssmtDto : familyAssmtList) {
				familyAssmtDto.setComma(ServiceConstants.EMPTY_STR);
				if (!ServiceConstants.MARITAL_STATUS_CHILD.equals(familyAssmtDto.getCdPersonMaritalStatus())
						|| ServiceConstants.ROLE_PARENT.equals(familyAssmtDto.getCdStagePersRelInt())
						|| ServiceConstants.ROLE_B_PARENT.equals(familyAssmtDto.getCdStagePersRelInt())) {
					familyAssmtDto.setCdStagePersType(ServiceConstants.ADULT_TYPE);
					if (!firstAdult) {
						firstAdult = ServiceConstants.TRUEVAL;
					} else {
						familyAssmtDto.setComma(ServiceConstants.COMMA);
					}
				} else {
					familyAssmtDto.setCdStagePersType(ServiceConstants.CHILD_TYPE);
					if (!firstChild) {
						firstChild = ServiceConstants.TRUEVAL;
					} else {
						familyAssmtDto.setComma(ServiceConstants.COMMA);
					}
				}
			}
			prefillDto.setFamilyAssmtList(familyAssmtList);
		}

		// CLSC55D
		List<PersonDto> permanencyGoalsList = familyServicePlanDao.getPermanencyGoals(req.getIdEvent());
		prefillDto.setPermanencyGoalsList(permanencyGoalsList);

		return prefillData.returnPrefillData(prefillDto);
	}

}
