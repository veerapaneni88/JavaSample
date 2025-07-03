package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.ContactsForStageDto;
import us.tx.state.dfps.xmlstructs.outputstructs.NbrContactDto;

public interface ContactReviewDao {

	/**
	 * Method Name: getCountReqRevContactsForStage Method Description:Selects
	 * number of request for review contacts given an id-stage.
	 *
	 * @param contactsForStageDto
	 *            the contacts for stage dto
	 * @return NbrContactDto
	 */
	public NbrContactDto getCountReqRevContactsForStage(ContactsForStageDto contactsForStageDto);
}
