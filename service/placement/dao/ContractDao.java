package us.tx.state.dfps.service.placement.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.Contract;
import us.tx.state.dfps.common.domain.ContractPeriod;
import us.tx.state.dfps.common.dto.ContractDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.ContractPeriodAUDRes;
import us.tx.state.dfps.service.placement.dto.ContractContractPeriodInDto;
import us.tx.state.dfps.service.placement.dto.ContractContractPeriodOutDto;
import us.tx.state.dfps.service.placement.dto.ContractCountyOutDto;
import us.tx.state.dfps.service.placement.dto.ContractCountyPeriodInDto;
import us.tx.state.dfps.service.placement.dto.ContractCountyPeriodOutDto;
import us.tx.state.dfps.service.resource.dto.ContractPeriodDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description:This class is used to perform operations on the contract
 * information
 * 
 * Jan 10, 2018- 2:19:10 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface ContractDao {

	/**
	 * Method Name: getContractCountyPeriod
	 * 
	 * Method Description: retrieves a full row from CONTRACT COUNTY AND
	 * CONTRACT PERIOD given the hosts and the date period.
	 *
	 * @param contractCntyPeriodInDto
	 *            -the contract cnty period in dto
	 * @return ContractCountyPeriodOutDto
	 */
	public ContractCountyPeriodOutDto getContractCountyPeriod(ContractCountyPeriodInDto contractCntyPeriodInDto);

	/**
	 * 
	 * Method Name: getContarctCounty
	 * 
	 * Method Description:DAM CLSS70D retrieves the IdContract given an Id
	 * Resource Rsrc County, and the DtPlcmtStart.
	 * 
	 * @param contractCntyPeriodInDto
	 * @return ContractCountyOutDto
	 */
	public List<ContractCountyOutDto> getContarctCounty(ContractCountyPeriodInDto contractCntyPeriodInDto);

	/**
	 * 
	 * Method Name: getContarctContractPeriod
	 * 
	 * Method Description:(DAM CSEC72D ) select a full row from the contract &
	 * contract period table. Will not look for PENDING contracts
	 * 
	 * @param contractContractPeriodInDto
	 * @return ContractContractPeriodOutDto
	 */
	public ContractContractPeriodOutDto getContarctContractPeriod(
			ContractContractPeriodInDto contractContractPeriodInDto);

	/**
	 * 
	 * Method Name: getContractIdByVendorandPlcmtDate
	 * 
	 * Method Description: Retrieves Id Contract given a DT PLCMT START and a
	 * NBR RSRC ADDR VID (Vendor ID).
	 * 
	 * DAM:csec87dQUERYdam
	 *
	 * @param nmBatchParam
	 * @param dtPlcmtStart
	 * @return
	 */
	public List<Long> getContractIdByVendorandPlcmtDate(String nmBatchParam, Date dtPlcmtStart);

	/**
	 * 
	 * Method Name: csec73dQUERYdam
	 * 
	 * Method Description:Retrieves Id Contract given a DT PLCMT START and a NBR
	 * RSRC ADDR VID (Vendor ID).
	 * 
	 * DAM: csec73dQUERYdam
	 * 
	 * @param nmBatchParam
	 * @param dtPlcmtStart
	 * @return
	 */
	public List<Long> getContractByVendorandPlcmtDate(String nmBatchParam, Date dtPlcmtStart);

	/**
	 * 
	 * Method Name: getCountyContracted
	 * 
	 * Method Description: This is a List DAM that determines if a specific ID
	 * resource, service, county combination is contracted. DAM:cres33
	 * 
	 * @param contractCntyPeriodInDto
	 * @return CommonStringRes - Response to show if the given combination of
	 *         input is contacted.
	 */
	public CommonStringRes getCountyContracted(ContractCountyPeriodInDto contractCntyPeriodInDto);

	/**
	 * Method Name:getContractById Method Description: This method gets contract
	 * entity using contract id
	 * 
	 * @param contractId
	 * @return
	 */
	public Contract getContractById(Long contractId);

	/**
	 * This method is used to get contract and contract period by contract id,
	 * period and status.
	 * 
	 * @param contractContractPeriodInDto
	 * @return
	 */
	public ContractContractPeriodOutDto getCntrctByIdPeriodAndStatus(
			ContractContractPeriodInDto contractContractPeriodInDto);

	/**
	 * Method Name: getContractPeriodById Method Description: This method is
	 * used to get contract period information using contract id and
	 * cnperPeriod.
	 * 
	 * @param idContract
	 * @param nbrCnperPeriod
	 * @return
	 */
	public ContractPeriod getContractPeriodById(Long idContract, byte nbrCnperPeriod);

	/**
	 * Method Name: contractAUD Method Description: This method is used by
	 * Service CCMN35S. DAM CAUD01D. Used to perform CRUD operation on Contract
	 * table
	 * 
	 * @param contractDto
	 * @param archInputDto
	 * @return
	 */
	public ContractDto contractAUD(ContractDto contractDto, ServiceReqHeaderDto archInputDto);

	/**
	 * Method Name: contractPeriodAUD DAM Name: CAUD20D
	 ** 
	 ** Method Description: This method perform CRUD Operation on CONTRACT_PERIOD
	 * table and its children records on 1) Add: Simple Insert full row Set
	 * pOutputDataRec->ulSysCdGenericReturnCode=TRUE
	 **
	 ** 2) Update: Simple Update full row, then Check for CLOSURE_DATE of
	 * CONTRACT_PERIOD against Max EFFECTIVE_DATE of CONTRACT_VERSION If
	 * CLOSURE_DATE > EFFECTIVE_DATE ==> return TRUE Else return FALSE in host
	 * output variable pOutputDataRec->ulSysCdGenericReturnCode
	 **
	 ** 3) Delete: Also delete 3 other tables: CONTRACT_VERSION,
	 * CONTRACT_SERVICE, CONTRACT_COUNTY besides deleting CONTRACT_PERIOD Set
	 * pOutputDataRec->ulSysCdGenericReturnCode=TRUE
	 * 
	 * @param contractPeriodInDto
	 * @param archInputDto
	 */
	public ContractPeriodAUDRes contractPeriodAUD(ContractContractPeriodInDto contractPeriodInDto,
			ServiceReqHeaderDto archInputDto);

	public List<ContractCountyOutDto> getContarctCountyWithoutService(ContractCountyPeriodInDto contractCntyPeriodInDto);

	public Long getIdContractForResourceId(Long resourceId);

	public ContractPeriodDto getLatestCPForResourceId(String resourceId);
}
