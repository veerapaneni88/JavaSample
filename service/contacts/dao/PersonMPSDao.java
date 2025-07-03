/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *May 7, 2018- 4:29:25 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.contacts.dao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> May 7, 2018- 4:30:17 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PersonMPSDao {

	/**
	 * Method Name: getNbrMPSPersStage Method Description: This method number of
	 * the MPS person related with the given stage
	 * 
	 * @param idStage
	 * @param stagePerRel
	 * @return
	 */
	public boolean getNbrMPSPersStage(Long idStage, String stagePersRelType);

}
