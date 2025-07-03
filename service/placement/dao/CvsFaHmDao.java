package us.tx.state.dfps.service.placement.dao;

import us.tx.state.dfps.service.placement.dto.CvsFaHomeValueDto;

public interface CvsFaHmDao {

	/**
	 * Updates details of person in Person Detail table
	 * 
	 * @param cvsFaHomeValueDto
	 * @
	 */
	public long updatePersonDetail(CvsFaHomeValueDto cvsFaHomeValueDto);

	/**
	 * This method inserts the CvsfaHome page details if the record does not
	 * exist
	 * 
	 * @param cvsFaHomeValueDto
	 * @
	 */
	public long insertIntoPersonDetail(CvsFaHomeValueDto cvsFaHomeValueDto);

	/**
	 * updates primary Kinship CareGiver Indicator in Stage Person Link table
	 * 
	 * @param cvsFaHomeValueDto
	 * @
	 */
	public long updatePrimaryKinshipIndicator(CvsFaHomeValueDto cvsFaHomeValueDto);

	/**
	 * Updates resource Name of person in Caps Resource table
	 * 
	 * @param cvsFaHomeValueDto
	 * @
	 */
	public long updateCapsResourceName(CvsFaHomeValueDto cvsFaHomeValueDto);

	/**
	 * Updates resource ID of person in Placement table
	 * 
	 * @param cvsFaHomeValueDto
	 */
	public long updateResourceId(CvsFaHomeValueDto cvsFaHomeValueDto);

}