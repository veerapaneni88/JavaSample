package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.admin.dto.PersonVictimDiDto;
import us.tx.state.dfps.service.admin.dto.PersonVictimDoDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

public interface VictimRoleDao {
	/**
	 * 
	 * Method Name: getIndVictimRole Method Description:this method fetches
	 * idAllegHistory from ALLEGATION_HISTORY table.
	 * 
	 * @param personVictimDiDto
	 * @return PersonVictimDoDto
	 * @throws DataNotFoundException
	 */
	public PersonVictimDoDto getIndVictimRole(PersonVictimDiDto personVictimDiDto);
}
