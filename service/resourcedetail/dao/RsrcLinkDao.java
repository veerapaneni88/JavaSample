package us.tx.state.dfps.service.resourcedetail.dao;

import us.tx.state.dfps.service.common.response.SubcontrListSaveRes;
import us.tx.state.dfps.service.resource.detail.dto.RsrcLinkInsUpdDelInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Updating Rsrc link table Feb 2, 2018- 1:56:16 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface RsrcLinkDao {

	/**
	 * 
	 * Method Name: saveRsrcLink Dam : CAUD26D Method Description: This method
	 * used for rsrc link table insert,update and delete operations
	 * 
	 * @param rsrcLinkInsUpdDelInDto
	 * @return subcontrListSaveRes
	 */
	public SubcontrListSaveRes saveRsrcLink(RsrcLinkInsUpdDelInDto rsrcLinkInsUpdDelInDto);
}
