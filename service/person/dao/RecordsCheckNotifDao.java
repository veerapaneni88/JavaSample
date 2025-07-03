package us.tx.state.dfps.service.person.dao;

import us.tx.state.dfps.service.forms.dto.RecordsCheckNotifDto;

public interface RecordsCheckNotifDao {
	/**
	 * Method Description: This method will fetch the Record check notification
	 * details by passing idRecordsCheckNotif as input
	 * 
	 * @param idRecordsCheckNotif
	 * @return RecordsCheckNotifDto @
	 */
	public RecordsCheckNotifDto getRecordsCheckNotification(Long idRecordsCheckNotif);

	/**
	 * Method Description: This method will update the Record check notification
	 * table by passing required input
	 * 
	 * @param RecordsCheckNotifDto
	 * @return Long @
	 */
	public String updateRecordsCheckNotif(RecordsCheckNotifDto recordsCheckNotifDto);

	/**
	 * Method Description: This method will create the new Record check
	 * notification detail.
	 * 
	 * @param RecordsCheckNotifDto
	 * @return Long @
	 */
	public Long insertRecordsCheckNotif(RecordsCheckNotifDto recordsCheckNotifDto);

}
