
package us.tx.state.dfps.service.fostercarereview.dao;

import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.fostercarereview.dto.FosterCareReviewDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 14, 2018- 3:29:57 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FosterCareReviewDao {

	public CommonHelperRes confirm(FosterCareReviewDto fosterCareReviewDto);

	/**
	 * Method Name: isFCReviewCreatedOnAfter1stOct2010 Method Description:Return
	 * true if the foster care review was created on or after Oct 1st 2010 FCON
	 * change date for Extended foster care.
	 * 
	 * @param idReviewEvent
	 * @return boolean @
	 */
	public boolean isFCReviewCreatedOnAfter1stOct2010(Long idReviewEvent);

	public boolean isFCReviewCreatedOnAfter1stOct2010ByIdFceReview(Long idFceReview);

	public boolean isEntryLevelLegalStatusPresent(FosterCareReviewDto fosterCareReviewDto);

}
