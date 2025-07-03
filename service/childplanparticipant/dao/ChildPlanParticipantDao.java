package us.tx.state.dfps.service.childplanparticipant.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ChildPlanParticipantDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Queries the
 * Child Plan participant table and retrieves a row corresponding to an Event Id
 * Oct 7, 2017- 6:13:33 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface ChildPlanParticipantDao {

	public List<ChildPlanParticipantDto> fetchChildPlanParticipant(Long idChildPlanEvent);

	public Long saveOrUpdateChildPlanParticip(ChildPlanParticipantDto childPlanParticipDto);

	public String deleteChildPlanParticip(Long idChildPlanParticp);

	public ChildPlanParticipantDto fetchSsccChildPlanParticipant(Long idSsccChildPlanParticip);

}
