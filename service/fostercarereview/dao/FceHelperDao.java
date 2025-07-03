/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 15, 2017- 10:58:24 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.fostercarereview.dao;

import us.tx.state.dfps.service.fce.EligibilitySummaryDto;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter
 * the description of class> Nov 15, 2017- 10:58:24 AM © 2017 Texas Department
 * of Family and Protective Services
 */

// this class for FceHelper
public interface FceHelperDao {

	void verifyCanSave(EligibilitySummaryDto eligibilitySummaryDto);

	void verifyOpenStage(long idStage);

	long verifyNonZero(String propertyName, long value);

}
