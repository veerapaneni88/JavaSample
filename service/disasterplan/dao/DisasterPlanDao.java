package us.tx.state.dfps.service.disasterplan.dao;

import java.util.Date;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.common.dto.WorkerDetailDto;
import us.tx.state.dfps.service.subcare.dto.ResourceAddressDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:DisasterPlanDao will implemented all operation defined in
 * DisasterPlanService Interface related DisasterPlan module. Feb 9, 2018-
 * 2:02:21 PM © 2017 Texas Department of Family and Protective Services
 */

public interface DisasterPlanDao {

	/**
	 * 
	 * Method Name: getGenericCaseInfo (DAm Name : CallCSEC02D) Method
	 * Description:This dam will return the generic case information needed for
	 * all forms.
	 * 
	 * @param idStage
	 * @return
	 */
	public GenericCaseInfoDto getGenericCaseInfo(Long idStage);

	/**
	 * 
	 * Method Name: getGenericCaseInfo (DAm Name : CallCLSCB1D) Method
	 * Description:This dam will retrieve the primary worker supervisor
	 * associated with the stage
	 * 
	 * @param idStage
	 * @return
	 */
	public Long getPrimaryWorkerOrSupervisor(Long idCase);

	/**
	 * 
	 * Method Name: getWorkerInfoById (DAm Name : CallCSEC01D) Method
	 * Description:This dam will retrieve all worker info based upon an Id
	 * Person
	 * 
	 * @param idPerson
	 * @return
	 */

	public WorkerDetailDto getWorkerInfoById(Long idPerson);

	/**
	 * 
	 * Method Name: getWorkerInfoById (DAm Name : CallCRES0AD) Method
	 * Description:This DAM retrieves all of the address data given a resource
	 * id and address type
	 * 
	 * @param idResource
	 * @return
	 */

	public ResourceAddressDto getResourceAddress(Long idResource);

	/**
	 * 
	 * Method Name: getWorkerInfoById (DAm Name : CallCSEC34D) Method
	 * Description: Dam will retrieve an address of a specified type(CD PERS
	 * ADDR LINK TYPE) from the Person Address table
	 * 
	 * @param idPerson
	 * @return
	 */

	public PersonAddressDto getPersonAddress(Long idPerson, String cdPersAddrLinkType, Date dtScrDtCurrentDate);

}
