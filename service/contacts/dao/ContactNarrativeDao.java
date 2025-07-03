package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactNarrativeStageDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactNarrativeStageDoDto;

import java.util.List;

public interface ContactNarrativeDao {
	/**
	 * 
	 * Method Name: deleteContactNarrative Method Description:
	 * deleteContactNarrative by idevent
	 * 
	 * @param idEvent
	 * @
	 */
	public long deleteContactNarrative(int idEvent);

	/**
	 * 
	 * Method Name: getNarrativeExists Method Description:fetches from contact
	 * narrative
	 * 
	 * @param csesa9di
	 * @return
	 * @throws DataNotFoundException
	 */
	public ContactNarrativeStageDoDto getNarrativeExists(ContactNarrativeStageDiDto csesa9di)
			throws DataNotFoundException;

    /**
     *
     * Method Name: getContactNarrativeDocs
     * Method Description: Fetch Narratives by idEvent
     *
     * @param idEvents
     * @
     */
    public List<byte[]> getContactNarrativeDocs(List<Long> idEvents);
}
