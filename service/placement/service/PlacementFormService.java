package us.tx.state.dfps.service.placement.service;

import us.tx.state.dfps.service.common.request.PlacementFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * Name:PlacementFormService Class Description:This service is used to launch
 * the CVS Placement forms Oct 30, 2017- 3:27:12 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PlacementFormService {

	/**
	 * Method Name:getSubCareLOCAuthorization Method Description:Method to
	 * launch the CVS Placement forms
	 * 
	 * @param placementFormReq
	 * @param formName
	 * @return @
	 */
	public PreFillDataServiceDto getSubCareLOCAuthorization(PlacementFormReq placementFormReq, String formName);
}
