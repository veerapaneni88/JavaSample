/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 11, 2017- 5:59:08 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casemanagement.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.casemanagement.dto.ExternalDocumentationDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 11, 2017- 5:59:08 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ExternalDocumentDao {

	/**
	 * Method Name: externaldocumentationAUD Description:This method performs
	 * adds, updates, and deletes on the EXT_DOCUMENTATION table
	 * 
	 * @param ExternalDocumentationDto
	 * @return Long @
	 */

	public Long externaldocumentationAUD(ExternalDocumentationDto externalDocumentationAUDDto, String cReqFuncCd,
			Long idExtDocSelected);

	/**
	 * Method Name: fetchExternaldocumentation Description: retrieves all
	 * documents associated with the ID CASE
	 * 
	 * @return List<ExternalDocumentationDto>
	 * @param idCase
	 * @param idUser
	 * 
	 */
	public List<ExternalDocumentationDto> fetchExternaldocumentation(long idCase);

	public Date getIntakeDate(Long idCase);

}
