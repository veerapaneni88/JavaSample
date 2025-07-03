package us.tx.state.dfps.service.placement.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dto.ContractCountyInDto;
import us.tx.state.dfps.service.admin.dto.ContractCountyOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description:This class is used to perform operations on the contract
 * county information
 * 
 * Jun 14, 2018- 2:19:10 PM © 2017 Texas Department of Family and Protective
 * Countys
 */
public interface ContractCountyDao {

	/**
	 * Method Name: getContractCountyByNbrCnverPeriod
	 * 
	 * Method Description: retrieves a full row from CONTRACT VERSION given the
	 * ID_CONTRACT and NBR_CNVER_PERIOD.
	 *
	 * @param contactVersionInDto
	 *            -the contract county in dto
	 * @return ContractCountyOutDto
	 */
	public List<ContractCountyOutDto> getContractCounty(ContractCountyInDto contactVersionInDto);

	/**
	 * Method Name: contractCountyAUD; DAM Name: CAUD08D
	 ** 
	 ** Method Description: This method perform CRUD Operation on CONTRACT_COUNTY
	 * table
	 * 
	 * @param contractCountyInDto
	 * @param archInputDto
	 * @return ContractCountyOutDto
	 */
	public ContractCountyOutDto contractCountyAUD(ContractCountyInDto contractCountyInDto,
			ServiceReqHeaderDto archInputDto);
	/**
	 * Method Name: deleteContractCounties
	 * Method Description: This method deletes entries from CONTRACT_COUNTY table
	 * @param contractCountyIdList
	 */
	public void deleteContractCounties(List<Long> contractCountyIdList);
}