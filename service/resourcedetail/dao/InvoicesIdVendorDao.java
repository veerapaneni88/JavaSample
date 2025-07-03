package us.tx.state.dfps.service.resourcedetail.dao;

import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Updating the invoice table Feb 2, 2018- 1:35:13 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface InvoicesIdVendorDao {
	/**
	 * 
	 * Method Name: updateInvoice Method Description: This method used for
	 * updating the Invoice info using resourceDetailInDto request
	 * 
	 * @param resourceDetailInDto
	 * @return @
	 */
	public int updateInvoice(ResourceDetailInDto resourceDetailInDto);
}
