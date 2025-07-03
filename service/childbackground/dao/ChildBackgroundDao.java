package us.tx.state.dfps.service.childbackground.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.PrincipalLegalStatusDto;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.person.dto.ChildPlanDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Mar 20,
 * 2018- 12:06:25 PM
 *
 */
public interface ChildBackgroundDao {

	/**
	 * 
	 * Method Name: getChildPlan DAM Name: CSECB9D Method Description:This DAM
	 * will select a full row from the EVENT and CHILD PLAN tables here DT EVNT
	 * occurred is greatest. Created for sir 19882
	 ** 
	 * @param idPerson
	 * @param idStage
	 * @return
	 */

	public ChildPlanDto getChildPlan(Long idPerson, Long idStage);

	/**
	 * 
	 * Method Name: getRmvlDateAndRmvlEvent Dam Name : CDYN10D Method
	 * Description:Retrieves Removal Date and Removal Event
	 * 
	 * @param idPerson
	 * @param cReqFunc
	 * @return
	 */
	public CnsrvtrshpRemovalDto getRmvlDateAndRmvlEvent(Long idPerson, String cReqFunc);

	/**
	 * 
	 * Method Name: getPrincipalLegalStatus DAM Name: CLSC34D Method
	 * Description:Returns Legal Status of Principal
	 * 
	 * @param idStage
	 * @param cdStagePersType
	 * @return
	 */
	public List<PrincipalLegalStatusDto> getPrincipalLegalStatus(Long idStage, String cdStagePersType);

}
