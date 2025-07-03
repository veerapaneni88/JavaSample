/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 30, 2017- 8:43:11 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.conservatorship.dao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 30, 2017- 8:43:11 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface CnsrvtrshpRemovalAlertDao {

	/**
	 * Method Name: CVSRemovalAlert Method Description: This method will trigger
	 * alert in “CVS Removal” page
	 * 
	 * @param cnsrvtrshpRemovalAlertReq
	 * @param idEvent
	 * @param idPerson
	 * @return @
	 */
	public String getAlertForCVSRemoval(Long idStage, String stageProgram, Long idCase, Long idTodoEvent,
			Long idToDoPersCreator);

}
