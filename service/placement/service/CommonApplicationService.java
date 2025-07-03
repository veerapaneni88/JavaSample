package us.tx.state.dfps.service.placement.service;

import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CommonApplicationController will have all operation which are
 * mapped to Placement module. Feb 9, 2018- 2:13:50 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface CommonApplicationService {

	/**
	 * Method Description: This method is used to retrieve the common
	 * application form. This form fully documents the historical social,
	 * emotional, educational, medical, and family account of the child by
	 * passing IdStage and IdPerson as input request
	 * 
	 * @param commonApplicationReq
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getCommonApplicationForm(CommonApplicationReq commonApplicationReq);

	/**
	 * Method Description: This method is used to retrieve the placement
	 * application form. This form fully documents the Child Information, Trauma
	 * and Trafficking History, Health Care Summary, Substance Abuse,Risk and
	 * sexualized behavior,Education,Family and Placement history emotional,
	 * Transition planning for a Adulthood,Juvenile Justice Involvement account
	 * of the child by passing IdStage and IdPerson as input request
	 * 
	 * @param commonApplicationReq
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getPlacementApplication(CommonApplicationReq commonApplicationReq, boolean docExists);
	
	/**
	 * Method Description: This method is used to retrieve the placement
	 * application form Status and CSA page latest value. 
	 * 
	 * @param commonApplicationReq
	 * @return PreFillDataServiceDto
	 */
	public CommonFormRes getCommonAppStatusAndCSADtls(CommonApplicationReq commonApplicationReq); 
}
