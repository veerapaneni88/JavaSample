package us.tx.state.dfps.service.fce.service;

import us.tx.state.dfps.service.fce.dto.AgeCitizenshipDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AgeCitizenshipService Feb 20, 2018- 3:14:18 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface AgeCitizenshipService {

	/**
	 * Method: read Method Description:Fetches the Age Citizenship details
	 *
	 * @param idStage
	 *            the id stage
	 * @param idEvent
	 *            the id event
	 * @param idLastUpdatePerson
	 *            the id last update person
	 * @return the age citizenship dto @ the service exception
	 */
	public AgeCitizenshipDto read(long idStage, long idEvent, long idLastUpdatePerson);

	/**
	 * Method: Save Method Description:Saves the Age Citizenship details
	 *
	 * @param ageCitizenshipDto
	 *            the age citizenship dto
	 * @return the string @ the service exception
	 */
	public String save(AgeCitizenshipDto ageCitizenshipDto);

}
