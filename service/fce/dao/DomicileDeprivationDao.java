package us.tx.state.dfps.service.fce.dao;

import java.util.List;

import us.tx.state.dfps.service.domiciledeprivation.dto.PrinciplesDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Implementing the DomicileDeprivation Mar 15, 2018- 12:14:46 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface DomicileDeprivationDao {

	/**
	 * Method Name: findPrinciples Method Description:This method fetches the
	 * PrinciplesList from Database based on idFceEligibility
	 * 
	 * @param idFceEligibility
	 * @return List<PrinciplesListDto> @
	 */
	public List<PrinciplesDto> findPrinciples(Long idFceEligibility);
}
