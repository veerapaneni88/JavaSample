package us.tx.state.dfps.service.childplanenhancements.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ChildPlanParticipantDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanAUDEvtDetailDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanInsUpdDelOutputDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Updates
 * ChildPlan Nov 8, 2017- 5:36:38 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface ChildPlanInsUpdDelDao {
	/**
	 * Method Name: childPlanUpdateOrDelete Method Description: Updates
	 * ChildPlan
	 * 
	 * @param childPlanInsUpdDelInputDto
	 * @return ChildPlanInsUpdDelOutputDto
	 * @throws DataNotFoundException
	 */
	public ChildPlanInsUpdDelOutputDto childPlanUpdateOrDelete(ChildPlanAUDEvtDetailDto chPlanAUDDetailDto);

	List<ChildPlanParticipantDto> getChildParticipants(Long idEvent);

	Boolean updateDateCopyProvided(List<Long> participantIds);

}
