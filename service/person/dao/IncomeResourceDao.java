package us.tx.state.dfps.service.person.dao;

import java.util.ArrayList;
import java.util.Date;

import us.tx.state.dfps.common.domain.IncomeAndResources;
import us.tx.state.dfps.service.person.dto.IncomeAndResourceDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is Dao
 * class for Income Resource> May 7, 2018- 10:45:36 AM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface IncomeResourceDao {
	/**
	 * Method Name: getLatestActiveStartDate Method Description: get
	 * Latest(Date) Active Income & Resource for person id.
	 * 
	 * @param idPerson
	 * @return Date
	 * @throws ParseException
	 */
	public Date getLatestActiveStartDate(Long idPerson);

	/**
	 * Method Name: getIncomeAndResourceList Method Description: This dao will
	 * fetch IncomeAndResourceDtoList.
	 * 
	 * @param idPerson
	 * @param sortBy
	 * @param activeFlag
	 * @return List<IncomeAndResourceDto>
	 */
	public ArrayList<IncomeAndResourceDto> getIncomeAndResourceList(long idPerson, String sortBy, boolean activeFlag);

	/**
	 * 
	 * Method Name: getIncomeAndResourceList Method Description:Fetches the
	 * person income and resources list from snapshot table
	 * (SS_INCOME_AND_RESOURCES) ( For example: This method is used for
	 * displaying the Select Forward person details in post person merge page)
	 * 
	 * @param idPerson
	 * @param sortBy
	 * @param activeFlag
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return
	 */
	public ArrayList<IncomeAndResourceDto> getIncomeAndResourceList(Long idPerson, String sortBy, Boolean activeFlag,
			Long idReferenceData, String cdActionType, String cdSnapshotType);

	/**
	 * 
	 * Method Name: getIncomeAndResource Method Description:
	 * 
	 * @param idIncomeAndResource
	 * @return
	 */
	public IncomeAndResources getIncomeAndResource(int idIncomeAndResource);

	public void saveIncomeAndResource(IncomeAndResources incomeAndResources);

	public void updateIncomeAndResource(IncomeAndResources incomeAndResources);
}
