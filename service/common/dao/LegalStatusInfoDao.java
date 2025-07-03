/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 6, 2017- 5:46:34 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.common.dao;

import java.util.List;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Nov 6, 2017- 5:46:34 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface LegalStatusInfoDao {

	/**
	 * Method Name: fetchLegalStatusListForChild Method Description: Fetches all
	 * the Legal Statuses
	 * 
	 * @param idChildPerson
	 * @return List<LegalStatusDto>
	 * @throws DataNotFoundException
	 */
	public List<LegalStatusDto> fetchLegalStatusListForChild(Long idChildPerson);

}
