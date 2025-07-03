package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.AdoptionSubsidyInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.AdoptionSubsidyInsUpdDelOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Caud81dDao Aug 10, 2017- 6:35:37 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface AdoptionSubsidyInsUpdDelDao {

	/**
	 * 
	 * Method Name: updateAdoptionSubsidy Method Description: Updates or
	 * insertes Adoption subsidy record
	 * 
	 * @param pInputDataRec
	 * @return Caud81doDto @
	 */
	public AdoptionSubsidyInsUpdDelOutDto updateAdoptionSubsidy(AdoptionSubsidyInsUpdDelInDto pInputDataRec);
}
