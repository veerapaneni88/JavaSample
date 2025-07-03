package us.tx.state.dfps.service.adoptionplan.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ChildPlanItemDto;
import us.tx.state.dfps.service.admin.dto.CapsPlacemntDto;
import us.tx.state.dfps.service.adoptionplan.dto.AdoptionPlanCposDto;
import us.tx.state.dfps.service.adoptionplan.service.AdoptionPlanCposService;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.childplan.dto.ChildParticipantRowDODto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.AdoptionPlanCposPrefillData;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes calls
 * to DAO and returns prefill Apr 16, 2018- 1:15:14 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Service
@Transactional
public class AdoptionPlanCposServiceImpl implements AdoptionPlanCposService {

	@Autowired
	ChildServicePlanFormDao childServicePlanFormDao;

	@Autowired
	CommonApplicationDao commonApplicationDao;

	@Autowired
	AdoptionPlanCposPrefillData prefillData;

	/**
	 * Method Name: getPlan Method Description: Gathers data about case plan for
	 * a child and pre-adoptive family in an adoptive placement
	 * 
	 * @param req
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getPlan(CommonApplicationReq req) {
		// Create main DTO and global variables
		AdoptionPlanCposDto prefillDto = new AdoptionPlanCposDto();
		Long idPerson = ServiceConstants.ZERO_VAL;

		// CCMN45D
		List<EventDto> eventDetailsList = childServicePlanFormDao.fetchEventDetails(req.getIdEvent());
		prefillDto.setEventDetailsList(eventDetailsList);

		// CSES27D
		PersonDto personDto = childServicePlanFormDao.getPersonChildDetails(req.getIdEvent());
		if (!ObjectUtils.isEmpty(personDto)) {
			idPerson = personDto.getIdChPerson();
			if (!ObjectUtils.isEmpty(eventDetailsList)) {
				personDto.setDtPersonDeath(eventDetailsList.get(0).getDtEventOccurred());
			}
			prefillDto.setPersonDto(personDto);

			// CSES28D
			CapsPlacemntDto capsPlacementDto = childServicePlanFormDao.getResourcePlacementDetail(idPerson);
			prefillDto.setCapsPlacementDto(capsPlacementDto);

			// CSEC35D
			NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(idPerson);
			prefillDto.setNameDetailDto(nameDetailDto);
		}

		// CLSS07D
		List<ChildPlanItemDto> childPlanItemsList = childServicePlanFormDao.geChildPlanItems(req.getIdEvent());
		prefillDto.setChildPlanItemsList(childPlanItemsList);

		// CLSS20D
		List<ChildParticipantRowDODto> childParticipantList = childServicePlanFormDao
				.getChildPlanParticipants(req.getIdEvent());
		prefillDto.setChildParticipantList(childParticipantList);

		return prefillData.returnPrefillData(prefillDto);
	}

}
