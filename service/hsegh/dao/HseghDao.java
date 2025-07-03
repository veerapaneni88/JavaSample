package us.tx.state.dfps.service.hsegh.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ChildPlanRecordDto;
import us.tx.state.dfps.common.dto.PersonOnHseghDto;
import us.tx.state.dfps.common.dto.ServicePlanDto;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Feb 21,
 * 2018- 2:12:46 PM
 *
 */
public interface HseghDao {

	/**
	 * 
	 * Method Name: getConservatorshipById Method Description: Retrieves every
	 * Conservatorship for a given idPerson. DAM Name: CLSS42D
	 * 
	 * @param idPerson
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<CnsrvtrshpRemovalDto> getConservatorshipById(Long idVictim);

	/**
	 * 
	 * Method Name: getRmvlReasonForCnsrvtrshpRemoval DAM Name: CLSS43D Method
	 * Description:
	 * 
	 * @param idVictim
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<CnsrvtrshpRemovalDto> getRmvlReasonForCnsrvtrshpRemoval(Long idVictim);

	/**
	 * 
	 * Method Name: getOldestApprFP DAM Name: CSEC41D Method Description:Given
	 * an ID PERSON, this DAM will retreive the oldest APPROVED FAMILY PLAN that
	 * involves that person. (If there are multiple in the same day, then it
	 * orders them by DT_LAST_UPDATE in descending order
	 * 
	 * @param idVictim
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<ServicePlanDto> getOldestApprFP(Long idPerson);

	/**
	 * 
	 * Method Name: getAllPeopleOnHsegh DAM Name : CLSC43D Method Description:
	 * This dam will retrieve rows from the StagePersonLink, Person PersonDtl,
	 * Name, & Person Id tables.
	 * 
	 * @param idStage
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<PersonOnHseghDto> getAllPeopleOnHsegh(Long idStage);

	/**
	 * 
	 * Method Name: getChildPlanRecords DAM Name: CLSS41D Method Description:
	 * This DAM retrieves every CHILD PLAN record for a given ID STAGE
	 * 
	 * @param idStage
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<ChildPlanRecordDto> getChildPlanRecords(Long idStage);

}
