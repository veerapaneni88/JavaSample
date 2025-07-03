package us.tx.state.dfps.service.fcl.service;

import java.math.BigDecimal;
import java.util.List;

import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
/**
 * 
 *
 *Class Description:<Service interface for Sexual Victimization History >
 *Oct 15, 2019- 2:40:21 PM
 *© 2019 Texas Department of Family and Protective Services
 */
public interface SexualVictimizationHistoryService {
	/**
	 * 
	 *Method Name:	getSexualVictimHistoryDto
	 *Method Description: Get Sexual Victimization History details by person id
	 *@param idPerson
	 *@return SexualVictimHistoryDto
	 */
	public SexualVictimHistoryDto getSexualVictimHistoryDto(Long idPerson);
	
	/**
	 * 
	 *Method Name:	saveOrUpdateSexualVictimHistory
	 *Method Description: Save or update Sexual Victim history
	 *@param sexualVictimHistoryDto
	 *@return
	 */
	public void saveOrUpdateSexualVictimHistory(SexualVictimHistoryDto sexualVictimHistoryDto);
	
	/**
	 * 
	 *Method Name:	deleteIncidents
	 *Method Description: delete incidents based on incident ids
	 *@param incidentIds
	 *@param idPerson
	 *@param dtLastUpdate
	 */
	public void deleteIncidents(List<Long> incidentIds, Long idPerson);
}
