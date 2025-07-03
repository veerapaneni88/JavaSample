package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PcaSubsidyInDto;
import us.tx.state.dfps.service.admin.dto.PcaSubsidyOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Clssc Aug 10, 2017- 7:49:24 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PcaSubsidyDao {

	/**
	 * 
	 * Method Name: getPCARecord Method Description: Get PCA record for given
	 * person.
	 * 
	 * @param pInputDataRec
	 * @return List<Clssc8doDto>
	 * @,DataNotFoundException
	 */
	public List<PcaSubsidyOutDto> getPCARecord(PcaSubsidyInDto pInputDataRec);

	/**
	 * 
	 * Method Name: getPCASubsidyRecord Method Description:CCMNI8D - Retrieves
	 * the PCA subsidy record for an event id.
	 * 
	 * @param idEvent
	 * @return
	 */
	public List<PcaSubsidyOutDto> getPCASubsidyRecord(Long idEvent);
}
