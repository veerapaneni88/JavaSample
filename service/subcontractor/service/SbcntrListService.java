package us.tx.state.dfps.service.subcontractor.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.ResourceReq;
import us.tx.state.dfps.service.common.request.SubcontrAreaServedReq;
import us.tx.state.dfps.service.common.request.SubcontrListRtrvReq;
import us.tx.state.dfps.service.common.response.ResourceRes;
import us.tx.state.dfps.service.common.response.SubcontrAreaServedRes;
import us.tx.state.dfps.service.common.response.SubcontrListRtrvRes;
import us.tx.state.dfps.service.common.response.SubcontrListSaveRes;
import us.tx.state.dfps.service.subcontractor.dto.SubcontrListSaveiDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description: This class will perform an add, update and/or delete to
 * the Resource Link table.
 *
 * Aug 16, 2017- 2:44:27 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface SbcntrListService {

	/**
	 * Method Name: saveSubContractorList
	 * 
	 * Method Description: This method will perform an add, update and/or delete
	 * to the Resource Link table.
	 * 
	 * @param pInputMsg
	 * @return SbcntrListSaveoDto @
	 */
	public SubcontrListSaveRes saveSubContractorList(List<SubcontrListSaveiDto> pInputMsg);

	/**
	 * 
	 * Method Name: findSubcontractor
	 * 
	 * Method Description:This service will retrieve all resources that have
	 * been designated as subcontractor for the prime resource. It will also
	 * retrieve all services for which the prime resource may provide.
	 * 
	 * @param pInputMsg
	 * @return @
	 */
	public SubcontrListRtrvRes findSubcontractor(SubcontrListRtrvReq pInputMsg);

	/**
	 * Method Name: getAreaServedList
	 * 
	 * Method Description:service name: CRES05S
	 * 
	 * @param subcontrAreaServedReq
	 * @return
	 */
	public SubcontrAreaServedRes getAreaServedList(SubcontrAreaServedReq subcontrAreaServedReq);

	/**
	 * Method Name: getResourceInfoById
	 * 
	 * Method Description:This Method is used to get the Resource Information by
	 * resource id
	 * 
	 * @param resourceReq
	 *            - resource holding resource id
	 * @return resourceRes - response having resource information
	 */
	public ResourceRes getResourceInfoById(ResourceReq resourceReq);
}
