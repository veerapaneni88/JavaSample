package us.tx.state.dfps.service.sscc.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.dto.Option;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.SSCCListHeaderDto;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: SSCC
 * EJB Class Description: SSCCListDao
 * 
 *
 */
@Repository
public interface SSCCListDao {

	/**
	 * Method inserts a row into the SSCC_LIST table
	 * 
	 * @param ssccListDto
	 * @return long
	 * 
	 */
	public long saveSSCCList(SSCCListDto ssccListDto);

	/**
	 * updateSSCCList
	 * 
	 * @param ssccListDto
	 * @return long
	 * 
	 */
	public long updateSSCCList(SSCCListDto ssccListDto);

	/**
	 * @param ssccRefDto
	 * @param ssccListDto
	 * @param userProfileDto
	 * @return SSCCListDto
	 * 
	 */
	public SSCCListDto populateSSCCListValueBeanForFixerUpdate(SSCCRefDto ssccRefDto, SSCCListDto ssccListDto,
			UserProfileDto userProfileDto);

	/**
	 * Method fetches a record from the SSCC_LIST table using Primary Key
	 * 
	 * @param idSSCCReferral
	 * @return SSCCListDto
	 */
	public List<SSCCListDto> fetchSSCCList(long idSSCCReferral);

	/**
	 * 
	 * Method Name: insertSSCCList Method Description:Method inserts a row into
	 * the SSCC_LIST table
	 * 
	 * @param ssccListDto
	 * @return SSCCListDto
	 * @throws DataNotFoundException
	 */
	public SSCCListDto insertSSCCList(SSCCListDto ssccListDto);

	/**
	 * 
	 * Method Name: getSSCCListResults Method Description:Fetches the SSCC LIST
	 * Search Results and sets it into the PaginationResultsBean object.
	 * 
	 * @param ssccListHeaderDto
	 * @return PaginationResultDto
	 * @throws DataNotFoundException
	 */
	public List<SSCCListDto> getSSCCListResults(SSCCListHeaderDto ssccListHeaderDto);

	/**
	 * 
	 * Method Name: fetchRegionUnit Method Description:Fetch all the units from
	 * the unit table and return a list.
	 * 
	 * @return List<Option>
	 * @throws DataNotFoundException
	 */
	public List<Option> fetchRegionUnit();

	/**
	 * 
	 * Method Name: isValidSSCCCatchmentRegion Method Description:Returns true
	 * if the region is a valid SSCC Catchment region
	 * 
	 * @param cdSSCCCntrctRegion
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean isValidSSCCCatchmentRegion(String cdSSCCCntrctRegion);

	/**
	 * Method Name: fetchValidUnitRegionforSSCCUser Method Description:Fetches a
	 * list of valid Regions for a SSCC user
	 * 
	 * @param userID
	 * @return List<String>
	 * @throws DataNotFoundException
	 */
	public List<String> fetchValidUnitRegionforSSCCUser(Long idUser);

	/**
	 * Method Name: fetchValidSSCCRegion Method Description:Fetches a list of
	 * valid SSCC Regions to be displayed in the Region dropdown
	 * 
	 * @return List<String>
	 * @throws DataNotFoundException
	 */
	public List<String> fetchValidSSCCRegion();

	/**
	 * Method Name: fetchCdCatchmentFromIdCatchment Method Description:Fetches
	 * fetch SSCC CdCatchment From IdCatchment
	 * 
	 * @return String
	 */
	public String fetchCdCatchmentFromIdCatchment(Long idSSCCCatchment);

	/**
	 * Method Name: fetchIdCatchmentFromCdCatchment Method Description:Fetch
	 * SSCC idCatchment From cdCatchment
	 * 
	 * @return Long
	 */
	public Long fetchIdCatchmentFromCdCatchment(String cdCatchment);

	/**
	 * Method Name: fetchDefaultCatchmentForSSCCUser Method Description: Fetch
	 * Default Catchment for SSCC user from Unit
	 * 
	 * @param idPerson
	 * @return
	 */
	public String fetchDefaultCatchmentForSSCCUser(Long idPerson);

	/**
	 * Method Name: fetchDefaultSSCCCatchmentForDFPSUser Method Description:
	 * Fetch Default SSCC Catchment for DFPS User from SSCC_REFERRAL
	 * 
	 * @param idWkldPerson
	 * @return
	 */
	public Long fetchDefaultSSCCCatchmentForDFPSUser(Long idWkldPerson);

	/**
	 * Method Name: fetchCatchmentsForRegion Method Description: fetches
	 * catchments from sscc_parameters based on region
	 * 
	 * @param cdContractRegion
	 * @return
	 */
	public List<String> fetchCatchmentsForRegion(String cdContractRegion);

	public Boolean hasStageAccess(Long ulIdStage, Long idUser);
}
