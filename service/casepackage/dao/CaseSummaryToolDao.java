/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jun 6, 2018- 2:11:19 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casepackage.dao;

import java.util.List;

import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:The Dao
 * Class for Case Summary Tool Jun 6, 2018- 2:11:19 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface CaseSummaryToolDao {

	/**
	 * 
	 * Method Name: getCaseSumToolPersonList Method Description: Retrieve the
	 * person list for the particular stage.
	 * 
	 * @param idStage
	 * @param staffType
	 */
	List<PersonDto> getCaseSumToolPersonList(Long idStage, String staffType);

}
