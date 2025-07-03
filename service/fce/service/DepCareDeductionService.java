package us.tx.state.dfps.service.fce.service;

import us.tx.state.dfps.service.common.request.DependentCareReadReq;
import us.tx.state.dfps.service.common.response.DependentCareReadRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * DepCareDeductionService is CRUD operation specification for Deduction for
 * Dependent Care Cost Detail Feb 26, 2018- 12:19:37 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface DepCareDeductionService {

	/**
	 * Method Name:read Method Description:read Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 * @return DependentCareReadRes @
	 */
	public DependentCareReadRes readDepCare(DependentCareReadReq dependentCareReadReq);

	/**
	 * Method Name:save Method Description:save Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 * @return DependentCareReadRes @
	 */
	public DependentCareReadRes save(DependentCareReadReq dependentCareReadReq);

	/**
	 * Method Name:getValidAdultDependent Method Description:get
	 * FceDepCareDeduct record for valid Adult and Dependent Person
	 * 
	 * @param dependentCareReadReq
	 * @return DependentCareReadRes @
	 */
	public DependentCareReadRes getValidAdultDependent(DependentCareReadReq dependentCareReadReq);

	/**
	 * Method Name:syncDepCareDeductions Method Description:for an Eligibility
	 * Application re-check if deductions are valid based on fce person ages
	 * 
	 * @param dependentCareReadReq
	 * @return DependentCareReadRes @
	 */
	public DependentCareReadRes syncDepCareDeductions(DependentCareReadReq dependentCareReadReq);

}
