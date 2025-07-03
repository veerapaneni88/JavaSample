package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.AdoptionSubsidyInDto;
import us.tx.state.dfps.service.admin.dto.AdoptionSubsidyOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Clss69d Aug 10, 2017- 4:02:41 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface AdoptionSubsidyDao {

	/**
	 * 
	 * Method Name: getAdoptionSubsidyRecord Method Description: This fetches
	 * row from adoption for person Subsidy
	 * 
	 * @param pInputDataRec
	 * @return List<Clss69doDto> @
	 */
	public List<AdoptionSubsidyOutDto> getAdoptionSubsidyRecord(AdoptionSubsidyInDto pInputDataRec);
}
