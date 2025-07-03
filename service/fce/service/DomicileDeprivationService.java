package us.tx.state.dfps.service.fce.service;

import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.domiciledeprivation.dto.DomicileDeprivationDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for implementing DomicileDeprivation Service Mar 15, 2018- 12:13:16 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface DomicileDeprivationService {

	/**
	 * Method Name: fetchDomicileDeprivation Method Description: This method
	 * fetches the DomicileDeprivation details
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idLastUpdatePerson
	 * @return DomicileDeprivationDto @
	 */
	public DomicileDeprivationDto fetchDomicileDeprivation(Long idStage, Long idEvent, Long idLastUpdatePerson);

	/**
	 * Method Name: saveDomicileDeprivation Method Description: This method
	 * saves the new DomicileDeprivation details
	 * 
	 * @param domicileDeprivationDto
	 * @return CommonHelperRes @
	 */
	public CommonHelperRes saveDomicileDeprivation(DomicileDeprivationDto domicileDeprivationDto);

}
