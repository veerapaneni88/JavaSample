/**
 * 
 */
package us.tx.state.dfps.service.casepackage.dao;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.LEAgencySearchDto;
import us.tx.state.dfps.service.common.request.LEAgencySearchReq;

public interface LEAgencySearchDao {

	/**
	 * 
	 * Method Description: This Method will retrieve rows from the CAPS_RESOURCE
	 * table given CD_RSRC_TYPE as 'Law Enforcement Agency' CD_RSRC_STATUS as
	 * 'Active' CD_RSRC_STATE as 'Taxes' And one of the following search
	 * criteria nm_resource, addr_rsrc_city or cd_rsrc_cnty
	 * 
	 * @param leAgencySearchReq
	 * @return List<LEAgencySearchDto> @
	 */

	public List<LEAgencySearchDto> getLawEnforcementAgencyList(LEAgencySearchReq leAgencySearchReq);

	/**
	 * 
	 * Method Description: This Method will retrieve resource email from
	 * RESOURCE_Email table given id_Resource
	 * 
	 * @param leAgencySearchReq
	 * @return List<LEAgencySearchDto> @
	 */
	public List<String> getResourceEmail(LEAgencySearchReq leAgencySearchReq);

}
