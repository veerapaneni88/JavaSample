package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.ContactIncomingDetailsDto;

public interface IncomingDetailUpdateDao {
	/**
	 * 
	 * Method Name: updateIncomingDetail Method Description:Update the Intake
	 * disposed date on the Incoming Detail Table.
	 * 
	 * @param contactIncomingDetailsDto
	 * @return @
	 */
	public long updateIncomingDetail(ContactIncomingDetailsDto contactIncomingDetailsDto);
}
