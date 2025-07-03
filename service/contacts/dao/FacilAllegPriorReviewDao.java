package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.FacilityAllegationPriorDto;

public interface FacilAllegPriorReviewDao {
	/**
	 * 
	 * Method Name: adFacilityAllegationPriorReview Method Description:this
	 * method inserts and delete in FACIL_ALLEG_PRIOR_REVIEW Table.
	 * 
	 * @param facilityAllegationPriorDto
	 * @return long @
	 */
	public long adFacilityAllegationPriorReview(FacilityAllegationPriorDto facilityAllegationPriorDto);
}
