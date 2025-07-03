package us.tx.state.dfps.service.pal.dao;

import us.tx.state.dfps.service.pal.dto.PalFollowUpDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for PalFollowUpDao. Oct 9, 2017- 11:27:01 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PalFollowUpDao {

	/**
	 * Method Name: selectPal Method Description: Retrieves information from
	 * fields CD_NO_ILS_REASON and DT_TRAINING_CMPLTD in from the PAL table.
	 * 
	 * @param palFollowUpDto
	 * @return PalFollowUpDto
	 */
	public PalFollowUpDto selectPal(PalFollowUpDto palFollowUpDto);

	/**
	 * Method Name: selectPalFollowUp Method Description: Queries the specified
	 * Pal Follow Up records from the database.
	 * 
	 * @param palFollowUpDto
	 * @return PalFollowUpDto
	 */
	public PalFollowUpDto selectPalFollowUp(PalFollowUpDto palFollowUpDto);

	/**
	 * Method Name: insertPalFollowUp Method Description: inserts all records
	 * that make up a pal_follow_up .
	 * 
	 * @param palFollowUpDto
	 * @return PalFollowUpDto
	 */
	public PalFollowUpDto insertPalFollowUp(PalFollowUpDto palFollowUpDto);

	/**
	 * Method Name: updatePal Method Description: Updates Pal table.
	 * 
	 * @param palFollowUpDto
	 * @return PalFollowUpDto
	 */
	public PalFollowUpDto updatePal(PalFollowUpDto palFollowUpDto);
}
