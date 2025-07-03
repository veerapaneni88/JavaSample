package us.tx.state.dfps.service.incomeexpenditures.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.FceIncome;
import us.tx.state.dfps.common.domain.IncomeAndResources;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.fce.IncomeExpenditureDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceIncomeDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION EJB Name: IncomeExpendituresBean
 * Class Description: IncomeExpendituresDao Interface Nov 21, 2017 - 3:45:39 PM
 */

public interface IncomeExpendituresDao {

	/**
	 * Method Name: findPrimaryWorkerForStage Method Description: This method is
	 * used to find primary worker details of given stage
	 * 
	 * @param stageID
	 * @param IncomeExpenditureDto
	 * @
	 */
	public PersonDto findPrimaryWorkerForStage(long stageID, IncomeExpenditureDto incomeExpenditureDto);

	/**
	 * Method Name: findFceIncomeOrResourceForChildOrFamily Method Description:
	 * This method is used to find Income and resources details of child and
	 * family
	 * 
	 * @param stageID
	 * @param IncomeExpenditureDto
	 * @
	 */
	public List<FceIncomeDto> findFceIncomeOrResourceForChildOrFamily(long idFceEligibility, String incomeOrResource,
			String childOrFamily);

	/**
	 * Method Name: saveFceIncomeResource Method Description: This method is
	 * used to save Income and resources details of child and family
	 * 
	 * @param fceIncomeDto
	 * @
	 */
	public void saveFceIncomeResource(FceIncomeDto fceIncomeDto);

	/**
	 * 
	 * Method Name: findFceIncomeOrResource Method Description: This method is
	 * used to find existing fce income or resource detail
	 * 
	 * @param idFceIncome
	 * @
	 */
	public FceIncome findFceIncomeOrResource(long idFceIncome);

	/**
	 * Method Name: syncApplication Method Description: Sync Application data
	 * 
	 * @param IncomeExpenditureDto
	 */
	public void syncFceApplicationStatus(FceEligibilityDto fceEligibilityDto);

	/**
	 * Method Name: getPersonIncomeResourceRequest Method
	 * Description:getPersonIncomeResourceRequest
	 * 
	 * @param PersonId
	 * @return IncomeAndResources>on
	 * @throws DataNotFoundException
	 * @
	 */
	public List<IncomeAndResources> getPersonIncomeForIdPerson(Long PersonId);

	/**
	 * Method Name: saveIncomeAndResource Method Description:This method will
	 * saves the IncomeAndresources
	 *
	 * @param incomeAndResources
	 */
	public void saveIncomeAndResource(IncomeAndResources incomeAndResources);

	/**
	 * Method Name: deleteIncomeAndResourceById Method Description:This method
	 * will deletes the IncomeAndresources
	 *
	 * @param id
	 */
	public void deleteIncomeAndResourceById(Long id);
}
