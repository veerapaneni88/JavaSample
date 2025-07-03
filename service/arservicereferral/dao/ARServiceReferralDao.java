package us.tx.state.dfps.service.arservicereferral.dao;

import java.text.ParseException;
import java.util.List;

import us.tx.state.dfps.service.common.request.ARServRefListReq;
import us.tx.state.dfps.service.common.response.ARServRefDetailRes;
import us.tx.state.dfps.service.servicereferral.dto.ARServRefListDto;

public interface ARServiceReferralDao {

	/**
	 * Method Name: saveARServRefDetails Method Description: Save service
	 * referral to AR_SERV_REF_CHECKLIST table
	 * 
	 * @param arServiceReferralDto
	 * @throws ParseException
	 */
	public void saveARServRefDetails(ARServRefListReq arServiceReferralDto);

	/**
	 * Method Name: updateMultipleServRefs Method Description: This update
	 * method is used to update multiple service referrals.
	 * 
	 * @param idServiceReferrals
	 * @param txtComments
	 * @param cdFinalOutcome
	 * @return long
	 */
	public void updateMultipleServRefs(ARServRefListReq arServiceReferralDto);

	/**
	 * Method Name: deleteARServiceReferral Method Description: Method to delete
	 * ar service referral based on service referral id(s).
	 * 
	 * @param idStage
	 * @return long
	 */
	public long deleteARServiceReferral(long idServiceReferrals);

	/**
	 * Method Name: servRefExists Method Description: Based on the plan type
	 * parameter, this method retrieves initial safety plan completion date or
	 * initial family plan completion date from contacts.
	 * 
	 * @param idStage
	 * @return boolean
	 */
	public boolean servRefExists(long idStage);

	/**
	 * Method Name: deleteEvent Method Description: Delete service referral
	 * event from EVENT table.
	 * 
	 * @param idStage
	 * @param cevnttypChk
	 * @return long
	 */
	public long deleteEvent(long idStage, String cevnttypChk);

	/**
	 * Method Name: getARServRefDetails Method Description: This Method Fetches
	 * Service Referral Details
	 * 
	 * @param arServiceReferralDto
	 * @return List<ArServRefChklist>
	 * @throws ParseException
	 * 
	 */
	public List<ARServRefListDto> getARServRefDetails(long idStage);

	/**
	 * Method Name: getARServiceReferralsDetails Method Description: This Method
	 * Fetches Service Referral Details
	 * 
	 * @param idServRefChklist
	 * @return ARServRefDetailRes
	 */
	public ARServRefDetailRes getARServiceReferralsDetails(long idServRefChklist);

	/**
	 *Method Name:	getPlanCompletionDate
	 *Method Description:
	 *@param idStage
	 *@param string
	 *@return
	 */
	public String getPlanCompletionDate(long idStage, String string);

	/**
	 * Method Name: saveARServRefDetls Method Description: This Method Fetches
	 * Service Referral Details
	 * 
	 * @param idServRefChklist
	 * @return ARServRefDetailRes
	 */
}
