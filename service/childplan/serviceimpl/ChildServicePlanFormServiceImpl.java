package us.tx.state.dfps.service.childplan.serviceimpl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.admin.dto.CapsPlacemntDto;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.childplan.dto.ChildParticipantRowDODto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanDetailsDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanDetailsOutputDto;
import us.tx.state.dfps.service.childplan.dto.ConcurrentGoalDto;
import us.tx.state.dfps.service.childplan.service.ChildServicePlanFormService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ChildPlanReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ChildServicePlanPrefillData;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * DescriptionImplementation for ChildPlanService Nov 9, 2017- 10:44:41 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ChildServicePlanFormServiceImpl implements ChildServicePlanFormService {

	@Autowired
	private ChildServicePlanFormDao childServicePlanFormDao;

	@Autowired
	private DisasterPlanDao disasterPlanDaoImpl;

	@Autowired
	private ChildServicePlanPrefillData childServicePlanPrefillData;

	private static final Logger Log = Logger.getLogger(ChildServicePlanFormServiceImpl.class);

	/**
	 * Method Description: This method establishes detailed case plans for
	 * providing services to children in substitute care and their families
	 * Service name :CSUB82S
	 * 
	 * @param childPlanReq
	 * @return childPlanDto @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getChildServicesPlan(ChildPlanReq childPlanReq) {
		Log.debug("Entering method getChildServicesPlan in ChildServicePlanFormServiceImpl");
		// Initialize main dto and global variables
		ChildPlanDetailsOutputDto childPlanDto = new ChildPlanDetailsOutputDto();
		Long idPerson = ServiceConstants.ZERO_VAL;
		Long idEventPerson = ServiceConstants.ZERO_VAL;
		Date dtAugRollout = DateUtils.date(2009, 8, 30);
		Date dtNovRollout = DateUtils.date(2009, 11, 15);
		Boolean augDiff = ServiceConstants.FALSEVAL;
		Boolean novDiff = ServiceConstants.FALSEVAL;
		EventDto eventDto = new EventDto();

		// CCMN45D
		List<EventDto> eventDtoList = childServicePlanFormDao.fetchEventDetails(childPlanReq.getIdEvent());
		if (!ObjectUtils.isEmpty(eventDtoList)) {
			eventDto = eventDtoList.get(0);
			augDiff = DateUtils.isBefore(eventDto.getDtEventOccurred(), dtAugRollout);
			novDiff = DateUtils.isBefore(eventDto.getDtEventOccurred(), dtNovRollout);
		}
		childPlanDto.setEventDtoList(eventDtoList);
		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDaoImpl.getGenericCaseInfo(childPlanReq.getIdStage());
		childPlanDto.setGenericCaseInfoDto(genericCaseInfoDto);
		// CSES27D
		PersonDto personDto = childServicePlanFormDao.getPersonChildDetails(childPlanReq.getIdEvent());
		if (!ObjectUtils.isEmpty(personDto)) {
			idPerson = personDto.getIdChPerson();
			if (augDiff && ServiceConstants.APPROVED.equals(eventDto.getCdEventStatus())) {
				personDto.setCdPersonLivArr(ServiceConstants.N);
			} else {
				personDto.setCdPersonLivArr(ServiceConstants.Y);
			}
			if (novDiff && ServiceConstants.APPROVED.equals(eventDto.getCdEventStatus())) {
				personDto.setCdPersGuardCnsrv(ServiceConstants.N);
			} else {
				personDto.setCdPersGuardCnsrv(ServiceConstants.Y);
			}
			if (!ServiceConstants.Y.equals(personDto.getIndNoConGoal())) {
				personDto.setIndNoConGoal(ServiceConstants.N);
			}
			// CSES28D
			CapsPlacemntDto capsPlacemntDto = childServicePlanFormDao.getResourcePlacementDetail(idPerson);
			childPlanDto.setCapsPlacemntDto(capsPlacemntDto);
			// CSESF3D
			List<ConcurrentGoalDto> concurrentDtoList = childServicePlanFormDao
					.getConcurrentData(childPlanReq.getIdEvent());
			childPlanDto.setConcurrentDtoList(concurrentDtoList);
			// CSEC14D
			ChildPlanDetailsDto childPlanDetailsDto = childServicePlanFormDao.getChildPlanDetails(idPerson,
					childPlanReq.getIdEvent());
			childPlanDto.setChildPlanDetailsDto(childPlanDetailsDto);
		}
		childPlanDto.setPersonDto(personDto);
		// CSES93D
		EventDto eventChildList = childServicePlanFormDao.getIdPersonPerChildPlan(childPlanReq.getIdEvent());
		if (!ObjectUtils.isEmpty(eventChildList)) {
			idEventPerson = ObjectUtils.isEmpty(eventChildList.getIdApprovalPerson()) ? eventChildList.getIdPerson()
					: eventChildList.getIdApprovalPerson();
		}
		childPlanDto.setEventChildList(eventChildList);
		// CSEC74D
		PersonDto personList = childServicePlanFormDao.getPersonDetails(idEventPerson);
		childPlanDto.setPersonList(personList);
		// CSES30D
		EventDto eventList = childServicePlanFormDao.getEventDetails(childPlanReq.getIdEvent());
		childPlanDto.setEventList(eventList);
		// CLSS20D
		List<ChildParticipantRowDODto> childServiceParcipantList = childServicePlanFormDao
				.getChildPlanParticipants(childPlanReq.getIdEvent());
		childPlanDto.setChildServiceParcipantList(childServiceParcipantList);
		childPlanDto.setFormName(childPlanReq.getFormName());
		Log.debug("Exiting method getChildServicesPlan in ChildServicePlanFormServiceImpl");
		// calling prefillData method with medicalDto as I/p parameter
		return childServicePlanPrefillData.returnPrefillData(childPlanDto);
	}

}
