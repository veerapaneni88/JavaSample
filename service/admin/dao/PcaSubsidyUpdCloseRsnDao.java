package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.PcaSubsidyUpdCloseRsnInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Caudm Aug 10, 2017- 8:22:31 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PcaSubsidyUpdCloseRsnDao {

	/**
	 * 
	 * Method Name: updatePCASubsidy Method Description: Update the PCA record
	 * for the Person
	 * 
	 * @param pInputDataRec
	 * @return int
	 * @,DataNotFoundException
	 */
	public int updatePCASubsidy(PcaSubsidyUpdCloseRsnInDto pInputDataRec);
}
