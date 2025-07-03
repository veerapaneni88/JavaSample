package us.tx.state.dfps.service.placement.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dto.ContractServiceInDto;
import us.tx.state.dfps.service.admin.dto.ContractServiceOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description:This class is used to perform operations on the contract
 * service information
 * 
 * Jun 14, 2018- 2:19:10 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface ContractServiceDao {

	/**
	 * Method Name: getContractService
	 * 
	 * Method Description: retrieves a full row from CONTRACT_SERVICE given the
	 * ID_CONTRACT
	 *
	 * @param contactServiceInDto
	 *            -the contract service in dto
	 * @return ContractServiceOutDto
	 */
	public List<ContractServiceOutDto> getContractService(ContractServiceInDto contactServiceInDto);

	/**
	 * Method Name: contractServiceAUD; DAM Name: CAUD17D
	 ** 
	 ** Method Description: This method perform CRUD Operation on
	 * CONTRACT_SERVICE table
	 * 
	 * @param contractServiceInDto
	 * @param archInputDto
	 * @return ContractServiceOutDto
	 */
	public ContractServiceOutDto contractServiceAUD(ContractServiceInDto contractServiceInDto,
			ServiceReqHeaderDto archInputDto);
}