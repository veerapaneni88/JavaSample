package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.EligibilityByPersonInDto;
import us.tx.state.dfps.service.admin.dto.EligibilityByPersonOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Caud18 Aug 12, 2017- 5:05:47 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface EligibilityByPersonDao {

	/**
	 * 
	 * Method Name: updateEligibiltyPeriod Method Description: Inserts and
	 * updates based on elgibility dates
	 * 
	 * @param pInputDataRec
	 * @return Caud18doDto @
	 */
	public EligibilityByPersonOutDto updateEligibiltyPeriod(EligibilityByPersonInDto pInputDataRec);
}
