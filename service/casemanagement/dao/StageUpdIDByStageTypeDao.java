/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:interface to update stage 
 *Apr 18, 2018- 11:41:06 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casemanagement.dto.StageUpdIDByStageTypeInDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:method to
 * update Apr 9, 2018- 12:18:45 PM © 2017 Texas Department of Family and
 * Protective Services.
 */
public interface StageUpdIDByStageTypeDao {

	/**
	 * update Stage table
	 *
	 * @param pInputDataRec
	 *            the input data rec
	 * @return the string
	 */
	String caud42dAUDdam(StageUpdIDByStageTypeInDto pInputDataRec);
}
