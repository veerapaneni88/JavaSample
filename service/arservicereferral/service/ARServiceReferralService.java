package us.tx.state.dfps.service.arservicereferral.service;

import java.text.ParseException;
import java.util.List;

import us.tx.state.dfps.service.common.request.ARServRefDetailReq;
import us.tx.state.dfps.service.common.request.ARServRefListReq;
import us.tx.state.dfps.service.common.response.ARServRefDetailRes;
import us.tx.state.dfps.service.common.response.ARServRefListRes;
import us.tx.state.dfps.service.common.response.ARServiceReferralUpdtRes;
import us.tx.state.dfps.service.servicereferral.dto.ARServRefListDto;

public interface ARServiceReferralService {

	/**
	 * Method Name: saveARServRefDetails Method Description: Method to save
	 * arService referral values.
	 * 
	 * @param arServiceReferralDto
	 * @return arServiceReferralSaveRes
	 * 
	 * @throws ParseException
	 */
	public List<ARServRefListDto> saveARServRefDetails(ARServRefListReq arServRefListReq);

	/**
	 * Method Name: deleteARServiceReferral Method Description: Method to delete
	 * arService referral based on service referral id(s).
	 * 
	 * @param idServiceReferrals
	 * @param idStage
	 * @return arServiceReferralDelRes
	 * 
	 */
	public ARServRefListRes deleteARServiceReferral(long idServiceReferrals, long idStage);

	/**
	 * Method Name: getARServRefDetails Method Description: This method fetches
	 * Service Referral Details
	 * 
	 * @param arServiceReferralDto
	 * @return List<ArServRefChklist>
	 * @throws ParseException
	 * 
	 */
	public ARServRefListRes getARServRefDetails(long idStage);

	/**
	 * Method Name: getARServiceReferralsDetails Method Description: This method
	 * fetches Service Referral Details
	 * 
	 * @param arServRefDetailReq
	 * @return ARServRefDetailRes
	 */
	public ARServRefDetailRes getARServiceReferralsDetails(ARServRefDetailReq arServRefDetailReq);

	/**
	 * Update multiple service referrals.
	 *
	 * @param idServiceReferrals the id service referrals
	 * @param txtComments the text comments
	 * @param cdFinalOutcome the code final outcome
	 * @return the AR service referral update response
	 */
	public ARServiceReferralUpdtRes updateMultipleServRefs(String[] idServiceReferrals, String txtComments,
			String cdFinalOutcome);
}