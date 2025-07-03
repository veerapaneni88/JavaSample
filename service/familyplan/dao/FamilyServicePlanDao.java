package us.tx.state.dfps.service.familyplan.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ServPlanEvalRecDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * methods that make database calls May 2, 2018- 3:39:48 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface FamilyServicePlanDao {

	/**
	 * Method Name: getServicePlanItems Method Description: Gets all rows from
	 * Service plan table for given id (DAM: CSVC11D)
	 * 
	 * @param idSvcPlanEvent
	 * @param idSvcPlanItem
	 * @return List<ServPlanEvalRecDto>
	 */
	public List<ServPlanEvalRecDto> getServicePlanItems(Long idSvcPlanEvent, Long idSvcPlanItem);

	/**
	 * Method Name: getServicePlanProblems Method Description: Gets problems
	 * with a certain plan event (DAM: CLSS27D)
	 * 
	 * @param idSvcPlanEvent
	 * @return List<ServPlanEvalRecDto>
	 */
	public List<ServPlanEvalRecDto> getServicePlanProblems(Long idSvcPlanEvent);

	/**
	 * Method Name: getServicePlanGoals Method Description: Gets goals with a
	 * certain plan event (DAM: CLSS27D)
	 * 
	 * @param idSvcPlanEvent
	 * @return List<ServPlanEvalRecDto>
	 */
	public List<ServPlanEvalRecDto> getServicePlanGoals(Long idSvcPlanEvent);

	/**
	 * Method Name: getPermanencyGoals Method Description: Gets child permanency
	 * goals for an event (DAM: CLSC55D)
	 * 
	 * @param idEvent
	 * @return List<PersonDto>
	 */
	public List<PersonDto> getPermanencyGoals(Long idEvent);

}
