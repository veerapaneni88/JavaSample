package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactsDto;

public interface FacilityInvstDtlUpdateDao {
	/**
	 * 
	 * Method Name: updateFacilityInvestigationDetail Method Description:This
	 * DAM nulls out the CD_FACIL_INVST_ORIG_DISP, CD_FACIL_INVST_ORIG_CLS_RSN,
	 * DT_FACIL_INVST_ORIG_COMPL when a Request for Review is Deleted.
	 * 
	 * @param contactsDto
	 * @return long
	 * @throws DataNotFoundException
	 */
	public long updateFacilityInvestigationDetail(ContactsDto contactsDto);
}
