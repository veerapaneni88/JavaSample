package us.tx.state.dfps.service.hsegh.service;

import us.tx.state.dfps.service.common.request.HseghReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * HseghService will have all operation which are mapped to HSEGH module. Feb
 * 22, 2018- 2:01:02 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface HseghService {

	/**
	 * 
	 * Method Name: getHsegh Service Name: CSUB71S Description: The Health,
	 * Social, Educational, and Genetic History (HSEGH) is required by the Texas
	 * Family Code, chapter 16.032 to be completed before placing a child for
	 * adoption with anyone other than a biological relative or relative by
	 ** marriage or if the child has been in substitute care for more than twelve
	 * months. the worker will begin gathering information at the point of
	 * investigation and removal. The worker can then add information throughout
	 * th life of the case. The HSEGH fully documents the child's readiness for
	 * adoption and informs the adoptive parents about the child's history and
	 * needs. Even if the child's permanency plan is not adoption, the HSEGH
	 * will be required is the child is in substitute care for more than twelve
	 * months. External documentation such as Education log, Medical/Mental log
	 * will be attached to the HSEGH for the caretakers to review.
	 * 
	 * @param hseghReq
	 * @return @
	 */
	public PreFillDataServiceDto getHsegh(HseghReq hseghReq);

}
