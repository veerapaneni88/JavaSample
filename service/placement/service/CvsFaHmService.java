package us.tx.state.dfps.service.placement.service;

import us.tx.state.dfps.service.common.response.CvsFaHmRes;
import us.tx.state.dfps.service.placement.dto.CvsFaHomeValueDto;

public interface CvsFaHmService {

	/**
	 * This method saves the CvsfaHome page details in person detail table.
	 * 
	 * @param cvsFaHomeValueDto
	 * @return @
	 */
	public CvsFaHmRes updatePersonDetail(CvsFaHomeValueDto cvsFaHomeValueDto);

	/**
	 * This method inserts the CvsfaHome page details if the record does not
	 * exist
	 * 
	 * @param cvsFaHomeValueDto
	 * @return @
	 */
	public CvsFaHmRes insertIntoPersonDetail(CvsFaHomeValueDto cvsFaHomeValueDto);

	/**
	 * This method saves Primary careGiver information on CvsfaHome page details
	 * in stage person link table.
	 * 
	 * @param cvsFaHomeValueDto
	 * @return @
	 */
	public CvsFaHmRes updatePrimaryKinshipIndicator(CvsFaHomeValueDto cvsFaHomeValueDto);

	/**
	 * This method updates resource name in caps resource table,Primary Kinship
	 * caregiver indicator in Stage Person Link table,and resource id in caps
	 * placement table if the primary kinship caregiver checkbox is checked.
	 * 
	 * @param cvsFaHomeValueDto
	 * @return @
	 */
	public CvsFaHmRes updateKinIndPersonNameResourceId(CvsFaHomeValueDto cvsFaHomeValueDto);

}