package us.tx.state.dfps.service.fce.service;

import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.FosterCareReviewReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.fostercarereview.dto.FosterCareReviewDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 14, 2018- 2:54:18 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FosterCareReviewService {

	/**
	 * Method Name: isFCReviewCreatedOnAfter1stOct2010 Method Description:Return
	 * true if the foster care review was created on or after Oct 1st 2010 FCON
	 * change date for Extended foster care.
	 * 
	 * @param idEvent
	 * @return boolean @
	 */
	public boolean isFCReviewCreatedOnAfter1stOct2010(Long idEvent);

	/**
	 * Method Name: isFCReviewCreatedOnAfter1stOct2010ByIdFceReview Method
	 * Description: Return true if the foster care review was created on or
	 * after Oct 1st 2010 FCON change date for Extended foster care.
	 * 
	 * @param idFceReview
	 * @return boolean @
	 */
	public boolean isFCReviewCreatedOnAfter1stOct2010ByIdFceReview(Long idFceReview);

	/**
	 * Method Name: isEntryLevelLegalStatusPresent Method Description: Check if
	 * Entry level Legal Status present for the child
	 * 
	 * @param fosterCareReviewDto
	 * @return boolean @
	 */
	public boolean isEntryLevelLegalStatusPresent(FosterCareReviewDto fosterCareReviewDto);

	/**
	 * Method Name: save Method Description: save data in bean
	 * 
	 * @param fosterCareReviewReq
	 * @ @
	 */
	public void save(FosterCareReviewReq fosterCareReviewReq);

	/**
	 * Method Name: submit Method Description: check to make sure the
	 * application is "complete" before creating a todo to give the eligibility
	 * specialist
	 * 
	 * @param fosterCareReviewReq
	 * @
	 */
	public void submit(FosterCareReviewReq fosterCareReviewReq);

	/**
	 * Method Name: updateSystemDerivedParentalDeprivation Method Description:
	 * This is executed in a separate transaction so we ensure we have all the
	 * latest information when we calculate system-derived parental deprivation
	 * 
	 * @param commonEventIdReq
	 * @
	 */
	public void updateSystemDerivedParentalDeprivation(CommonEventIdReq commonEventIdReq);

	/**
	 * Method Name: closeEligibility Method Description: Prematurely close the
	 * eligibility
	 * 
	 * @param fosterCareReviewReq
	 * @
	 */
	public void closeEligibility(FosterCareReviewReq fosterCareReviewReq);

	/**
	 * Method Name: confirm Method Description: confirm the eligibility and
	 * close the review
	 * 
	 * @param fosterCareReviewReq
	 * @return @
	 */
	public CommonHelperRes confirm(FosterCareReviewReq fosterCareReviewReq);

	/**
	 * Method Name: readFosterCareReview Method Description: Read data for
	 * FosterCareReviewBean; sync data with the rest of the system
	 * 
	 * @param fosterCareReviewReq
	 * @return @
	 */
	public FosterCareReviewDto read(Long idStage, Long idEvent, Long idLastUpdatePerson, Boolean indNewUsing);

	/**
	 * Method Name: determineEligibility Method Description: calculate
	 * eligibility/reasons not eligible
	 * 
	 * @param fosterCareReviewReq
	 * @return FosterCareReviewDto
	 * @
	 */
	public FosterCareReviewDto determineEligibility(FosterCareReviewDto fosterCareReviewDto);

	/**
	 * Method Name: enableFosterGoupMessage Method Description: This method
	 * tells weather to show fostercare message or not
	 */
	public CommonBooleanRes enableFosterGoupMessage();

	/**
	 * Method Name: isFCReviewCreatedOnAfter1stOct2010 Method Description:Return
	 * true if the foster care review was created on or after Oct 1st 2010 FCON
	 * change date for Extended foster care.
	 * 
	 * @param idEvent
	 * @return boolean @
	 */
	public boolean isFCReviewCreatedOnAfter1stOct2010ForDetermineEligibility(Long idEvent);

}
