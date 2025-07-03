package us.tx.state.dfps.service.childserviceplan.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ChildPlanItemDto;
import us.tx.state.dfps.common.dto.ChildServicePlanDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.admin.dto.CapsPlacemntDto;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.childplan.dto.ChildParticipantRowDODto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanDetailsDto;
import us.tx.state.dfps.service.childserviceplan.service.ChildServicePlan;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ChildServicePlanReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ChildServicePlanFormPrefillData;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Mar 21,
 * 2018- 11:11:05 AM
 *
 */
@Service
@Transactional
public class ChildServicePlanImpl implements ChildServicePlan {

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private ChildServicePlanFormDao childServicePlanFormDao;

	@Autowired
	private ChildServicePlanFormPrefillData childServicePlanFormPrefillData;

	/**
	 * 
	 * Method Name: getChildServicePlan Service Name: CSUB21S Method
	 * Description:The Child's Service Plan establishes detailed case plans for
	 * providing services to children in substitute care and their families. The
	 * case plan identifies needs, formulates structured time limited tasks, and
	 * identifies service providers. This process ensures progres toward the
	 * child's safe return home or alternate permanent placement if the child
	 * cannot return home safely. The Child's service Plan forms have been
	 * combined into one form that will print continuosly. This form consists of
	 * subtemplates that contain the different sections of data.
	 * 
	 * @param childServicePlanReq
	 * @return PreFillDataServiceDto
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getChildServicePlan(ChildServicePlanReq childServicePlanReq) {
		Long idPerson = ServiceConstants.ZERO_VAL;
		Long idEventPerson = ServiceConstants.ZERO_VAL;

		// call DAM CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(childServicePlanReq.getIdStage());

		// call DAM CSES27D
		PersonDto personDto = childServicePlanFormDao.getPersonChildDetails(childServicePlanReq.getIdEvent());
		if (!ObjectUtils.isEmpty(personDto)) {
			idPerson = personDto.getIdChPerson();
		}

		// call DAM CSES28D

		CapsPlacemntDto capsPlacemntDto = childServicePlanFormDao.getResourcePlacementDetail(idPerson);

		// CSEC14D
		ChildPlanDetailsDto childPlanDetailsDto = childServicePlanFormDao.getChildPlanDetails(idPerson,
				childServicePlanReq.getIdEvent());

		// call DAM CSES93D
		EventDto eventDto = childServicePlanFormDao.getIdPersonPerChildPlan(childServicePlanReq.getIdEvent());
		if (!ObjectUtils.isEmpty(eventDto)) {
			idEventPerson = eventDto.getIdPerson();
		}

		// call DAM CSEC74D
		PersonDto personDto2 = childServicePlanFormDao.getPersonDetails(idEventPerson);

		// call DAM CSES30D
		EventDto eventDto2 = childServicePlanFormDao.getEventDetails(childServicePlanReq.getIdEvent());

		// call DAM CLSS07D
		List<ChildPlanItemDto> childPlanItemDtoList = childServicePlanFormDao
				.geChildPlanItems(childServicePlanReq.getIdEvent());

		// call DAM CLSS20D
		List<ChildParticipantRowDODto> childPlanParticipantsList = childServicePlanFormDao
				.getChildPlanParticipants(childServicePlanReq.getIdEvent());

		ChildServicePlanDto childServicePlanDto = new ChildServicePlanDto();
		childServicePlanDto.setCapsPlacemntDto(capsPlacemntDto);
		childServicePlanDto.setChildPlanDetailsDto(childPlanDetailsDto);
		childServicePlanDto.setChildPlanItemDtoList(childPlanItemDtoList);
		childServicePlanDto.setChildPlanParticipantsList(childPlanParticipantsList);
		childServicePlanDto.setEventDto(eventDto);
		childServicePlanDto.setEventDto2(eventDto2);
		childServicePlanDto.setGenericCaseInfoDto(genericCaseInfoDto);
		childServicePlanDto.setPersonDto(personDto);
		childServicePlanDto.setPersonDto2(personDto2);

		return childServicePlanFormPrefillData.returnPrefillData(childServicePlanDto);
	}

}
