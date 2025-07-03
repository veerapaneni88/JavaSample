package us.tx.state.dfps.service.placement.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.ContractVersionInDto;
import us.tx.state.dfps.service.admin.dto.ContractVersionOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description:This class is used to perform operations on the contract
 * version information
 * 
 * Jun 14, 2018- 2:19:10 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface ContractVersionDao {

	/**
	 * Method Name: getContractVersionByNbrCnverPeriod
	 * 
	 * Method Description: retrieves a full row from CONTRACT VERSION given the
	 * ID_CONTRACT and NBR_CNVER_PERIOD.
	 *
	 * @param contactVersionInDto
	 *            -the contract version in dto
	 * @return ContractVersionOutDto
	 */
	public List<ContractVersionOutDto> getContractVersionByNbrCnverPeriod(ContractVersionInDto contactVersionInDto);

	/**
	 * Method Name: getLatestContractVersion
	 * 
	 * Method Description: This DAM will receive ID CONTRACT and NBR CNPER
	 * PERIOD (of the previous period) and will return all columns for the
	 * latest contract version record in the CONTRACT VERSION table. DAM:
	 * CSES01D
	 * 
	 * @param contractVersionInDto
	 * @return ContractVersionOutDto
	 */
	public ContractVersionOutDto getLatestContractVersion(ContractVersionInDto contractVersionInDto);
}