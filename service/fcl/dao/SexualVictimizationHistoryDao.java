package us.tx.state.dfps.service.fcl.dao;

import java.math.BigDecimal;
import java.util.List;

import us.tx.state.dfps.common.domain.ChildSxVctmztn;
import us.tx.state.dfps.common.domain.ChildSxVctmztnIncdnt;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;

/**
 * 
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:<Dao class for Sexual Victimization History>
 *Oct 11, 2019- 12:03:01 PM
 *© 2017 Texas Department of Family and Protective Services
 */
public interface SexualVictimizationHistoryDao {
	/**
	 * 
	 *Method Name:	fetchSexualVitimHistory
	 *Method Description: get Sexual Victimization History list by person id
	 *@param idPerson
	 *@return List<ChildSxVctmztnIncdnt>
	 */
	public List<ChildSxVctmztnIncdnt> fetchSexualVictimHistory(Long idPerson);

	List<ChildSxVctmztnIncdnt> getIdPersonAggressorIncidents(Long idPersonAggressor);

	/**
	 * 
	 *Method Name:	saveIncident
	 *Method Description: save incidents
	 *@param incidentDtos
	 */
	public void saveIncident(List<SexualVictimIncidentDto> incidentDtos);
	/**
	 * 
	 *Method Name:	deleteIncident
	 *Method Description: delete incident
	 *@param idIncidents
	 *@param idUser
	 */
	public void deleteIncident(List<Long> idIncidents, Long idUser);
	
	/**
	 * 
	 *Method Name:	getChildSxVctmztnById
	 *Method Description: Get Child Sexual Victim History record by id
	 *@param idChildSxVctmztn
	 *@return ChildSxVctmztn
	 */
	public ChildSxVctmztn getChildSxVctmztnById(Long idChildSxVctmztn);
	/**
	 * 
	 *Method Name:	getChildSxVctmztnByPersonId
	 *Method Description: Get Child Sexual Victim History record by person id
	 *@param idPerson
	 *@return ChildSxVctmztn
	 */
	public ChildSxVctmztn getChildSxVctmztnByPersonId(Long idPerson);
	
	/**
	 * 
	 *Method Name:	saveChildSxVctmztn
	 *Method Description: save Child Sexual Victim History
	 *@param sexualVictimHistoryDto
	 */
	public void saveChildSxVctmztn(SexualVictimHistoryDto sexualVictimHistoryDto);
	
	
	/**
	 * Method Name:	updateSexualHistoryQuestionToYes
	 * Method Description: Typically this is used when a Trafficking incident has been created. If there is an existing
	 * record for the person in CHILD_SX_VCTMZTN and the indicator for a confirmed history is already set to Y, this
	 * method does nothing. If the record exists and is set to N, this method updated to Y, and the audit fields. If
	 * the record does not exist, it is created and set to Y.
	 * artf130771 - set SVH indicator
	 *
	 * @param idPerson id of the person that we want to indicate has a confirmed history of sexual victimization
	 * @param createdBy id of the worker that is making the change
	 */
	public void updateSexualHistoryQuestionToYes(BigDecimal idPerson, String createdBy);

	/**
	 * written for PPM 65209
	 * @param idPlacementTa
	 * @return
	 */
	public List<ChildSxVctmztnIncdnt> fetchSexualVictimHistoryByTA(Long idPlacementTa);
	public void updateSVHForTA(Long idPlacementTa, Long user);
}
