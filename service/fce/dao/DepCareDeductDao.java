package us.tx.state.dfps.service.fce.dao;

import java.util.List;

import us.tx.state.dfps.service.common.request.DependentCareReadReq;
import us.tx.state.dfps.service.common.response.DependentCareReadRes;
import us.tx.state.dfps.service.fce.FceDepCareDeductDto;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;

public interface DepCareDeductDao {

	/**
	 * Method Name:read Method Description:read Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 * @return List<FcePersonDto>
	 * @throws Exception
	 */
	public List<FcePersonDto> getValidPersonsAsAdults(DependentCareReadReq dependentCareReadReq);

	/**
	 * Method Name:getValidAdultDependent Method Description:get
	 * FceDepCareDeduct record for valid Adult and Dependent Person
	 * 
	 * @param dependentCareReadReq
	 * @return List<FceDepCareDeductDto>
	 * @throws Exception
	 */
	public List<FceDepCareDeductDto> getValidAdultDependent(DependentCareReadReq dependentCareReadReq);

	/**
	 * Method Name: getFceDepCareDeduct Method Description: Read Dependent Care
	 * Cost Deduction
	 * 
	 * @param dependentCareReadReq
	 * @return fceDepCareDeductDto
	 * @throws Exception
	 */
	public FceDepCareDeductDto getFceDepCareDeduct(DependentCareReadReq dependentCareReadReq);

	/**
	 * Method Name: getValidPersonsAsDependents Method Description: Get
	 * Dependents' information
	 * 
	 * @param dependentCareReadReq
	 * @return List<FcePersonDto>
	 * @throws Exception
	 */
	public List<FcePersonDto> getValidPersonsAsDependents(DependentCareReadReq dependentCareReadReq);

	/**
	 * Method Name: getFcePrincipalsAge Method Description: Get Principal Age
	 * for Deduction for Dependent Care Cost Detail
	 * 
	 * @param dependentCareReadReq
	 * @return List<FcePersonDto>
	 * @throws Exception
	 */
	public List<FcePersonDto> getFcePrincipalsAge(DependentCareReadReq dependentCareReadReq);

	/**
	 * Method Name: getDepDeductionSum Method Description: Get Dependent Care
	 * Deduction Sum for Deduction for Dependent Care Cost Detail
	 * 
	 * @param dependentCareReadReq
	 * @return double
	 * @throws Exception
	 */
	public Double getDepDeductionSum(DependentCareReadReq dependentCareReadReq);

	/**
	 * Method Name: insertFceDepCareDeduct Method Description: Add
	 * FceDepCareDeduct record for Deduction for Dependent Care Cost Detail
	 * 
	 * @param fceDepCareDeductDto
	 * @throws Exception
	 */
	public void insertFceDepCareDeduct(FceDepCareDeductDto fceDepCareDeductDto);

	/**
	 * Method Name: updateFceDepCareDeductInvalid Method Description: Update
	 * FceDepCareDeduct record for Invalid Adult and Dependent Person
	 * 
	 * @param fceDepCareDeductDto
	 * @throws Exception
	 */
	public void updateFceDepCareDeductInvalid(FceDepCareDeductDto fceDepCareDeductDto);

	/**
	 * Method Name: updateInvalidDependentInfo Method Description: Update
	 * Invalid Dependent in database
	 * 
	 * @param fceDepCareDeductDto
	 * @throws Exception
	 */
	public void updateInvalidDependentInfo(FceDepCareDeductDto fceDepCareDeductDto);

	/**
	 * Method Name: getValidDependents Method Description: Get valid Dependents
	 * 
	 * @param idFceEligibility
	 * @param idFceDependentPerson
	 * @return dependentCareReadRes
	 * @throws Exception
	 */
	public DependentCareReadRes getValidDependents(Long idFceEligibility, Long idFceDependentPerson);

	/**
	 * Method Name: findFceDepCareDeduct Method Description: Find out Dependent
	 * Care Deduction
	 * 
	 * @param idFceEligibility
	 * @param valid
	 * @return List<FceDepCareDeductDto>
	 * @throws Exception
	 */
	public List<FceDepCareDeductDto> findFceDepCareDeduct(Long idFceEligibility, Boolean valid);

	/**
	 * Method Name: isAdultDependentDup Method Description: Check whether or not
	 * Adult/Dependent is duplicate
	 * 
	 * @param idFceEligibility
	 * @param idFceAdultPerson
	 * @param idFceDependentPerson
	 * @return boolean
	 * @throws Exception
	 */
	public Boolean isAdultDependentDup(Long idFceEligibility, Long idFceAdultPerson, Long idFceDependentPerson);

	/**
	 * Method Name: isAdultInDependentColumn Method Description: check the Adult
	 * whether or not it is in dependent Column
	 * 
	 * @param idFceEligibility
	 * @param idFceAdultPerson
	 * @return boolean
	 * @throws Exception
	 */
	public Boolean isAdultInDependentColumn(Long idFceEligibility, Long idFceAdultPerson);

	/**
	 * Method Name: isDependentInAdultColumn Method Description: check the
	 * dependent whether or not it is in Adult Column
	 * 
	 * @param idFceEligibility
	 * @param idFceDependentPerson
	 * @return boolean
	 * @throws Exception
	 */
	public Boolean isDependentInAdultColumn(Long idFceEligibility, Long idFceDependentPerson);

}
