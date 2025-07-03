package us.tx.state.dfps.service.workload.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.RcciMrefDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN20S Class
 * Description: Operations for Stage Apr 14, 2017 - 12:19:52 PM
 */
public interface StageWorkloadDao {
	/**
	 * 
	 * Method Description:getStageById
	 * 
	 * @param id
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CINT06S
	public Stage getStageById(Long id);

	/**
	 * 
	 * Method Description:getStagesByCaseId
	 * 
	 * @param caseId
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN20S
	public List<Stage> getStagesByCaseId(Long caseId);

	/**
	 * 
	 * Method Description:searchStageIdsFromLinkByPersonId
	 * 
	 * @param personId
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN88S
	public List<Long> searchStageIdsFromLinkByPersonId(Long personId);

	/**
	 * 
	 * Method Description:searchStageIdsFromLinkByPersonIdAndStageRole
	 * 
	 * @param personId
	 * @param stageRole
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public List<Long> searchStageIdsFromLinkByPersonIdAndStageRole(Long personId, String stageRole);

	/**
	 * 
	 * Method Description:searchStageByNameAndPersonId
	 * 
	 * @param personFull
	 * @param ulIdPerson
	 * @return
	 * @throws DataNotFoundException
	 * @ @throws
	 *       ParseException
	 */
	// CCMNH4D
	public List<Stage> searchStageByNameAndPersonId(String personFull, Long ulIdPerson);

	/**
	 * 
	 * Method Description:updateStage
	 * 
	 * @param stage
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMNH4D
	public void updateStage(Stage stage);

	/**
	 * 
	 * Method Description:updateStagesByUnitId
	 * 
	 * @param unitId
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CAUDC2D
	public void updateStagesByUnitId(Long unitId);

	/**
	 * If the required function is UPDATE: This DAM performs an update of a
	 * record in the STAGE table. If the ID STAGE in the record equals the ID
	 * STAGE value passed in the Input Message, then the DT STAGE CLOSE for the
	 * selected record is updated to the current system's date
	 * 
	 * Service Name - CCMN03U, CCMN88S, DAM Name - CCMND4D
	 * updateStageCloseByStageId
	 * 
	 * @param idStage
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMND4D
	public void updateStageCloseByStageId(Long idStage, Date dtStageClose, String cdStageReasonClosed);

	/**
	 * This DAM will receive ID STAGE from the service and return the associated
	 * record from the STAGE_PERSON_LINK table where the staff person'r role is
	 * "Primary" (PR).
	 * 
	 * Service Name - CCMN88S, CCMN03U, DAM Name - CCMNG2D
	 * getStagePersonLinkByStageRole
	 * 
	 * @param idStage
	 * @param cdStagePersRole
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMNG2D
	public StagePersonLinkDto getStagePersonLinkByStageRole(Long idStage);

	public List<RcciMrefDto> getRcciMrefDataByCaseList(List<Long> idList);

	public void updateClosureReason(Long idStage, String cdStageReasonClosed) ;
}
