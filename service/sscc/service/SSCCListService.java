package us.tx.state.dfps.service.sscc.service;

import java.util.List;

import us.tx.state.dfps.common.dto.Option;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.common.request.SSCCListReq;
import us.tx.state.dfps.service.common.response.SSCCListRes;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.SSCCListHeaderDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SSCCListService Oct 26, 2017- 2:49:09 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface SSCCListService {

	/**
	 * Method Name: fetchSSCCListResults Method Description:Fetches the SSCC
	 * LIST Search Results and sets it into the PaginationResultsBean object as
	 * an array list.
	 * 
	 * @param ssccListHeaderDto
	 * @param userDto
	 * @return SSCCListRes @
	 */
	public SSCCListRes fetchSSCCListResults(SSCCListHeaderDto ssccListHeaderDto);

	/**
	 * 
	 * Method Name: fetchRegionUnit Method Description:Returns an arraylist of
	 * Unit Region Option objects
	 * 
	 * @return List<Option> @
	 */
	public List<Option> fetchRegionUnit();

	/**
	 * Method Name: fetchValidSSCCRegion Method Description:Returns an arraylist
	 * of valid SSCC Regions
	 * 
	 * @param userDto
	 * @return List<String> @
	 */
	public List<String> fetchValidSSCCRegion(Long idUser, String cdRegion);

	/**
	 * 
	 * Method Name: computeExcludeOptions Method Description: Returns a hashset
	 * of codes that need to be excluded from the Region dropdown
	 * 
	 * @param validRegionList
	 * @return HashSet<String> @
	 */
	public List<String> computeExcludeOptions(List<String> validRegionList);

	/**
	 * Method Name: fetchSSCCList Method Description:Fetches a row from the
	 * SSCC_LIST table using Referral Id
	 * 
	 * @param idSSCCReferral
	 * @return SSCCListDto
	 *
	 */
	public List<SSCCListDto> fetchSSCCList(Long idSSCCReferral);

	/**
	 * Method Name: fetchLatestPlacement Method Description: This method
	 * retrieves Latest Placement for the given Stage
	 * 
	 * @param idStage
	 * @return PlacementDto @
	 */
	public PlacementDto fetchLatestPlacement(Long idStage);

	/**
	 * Method Name: saveSSCCList Method Description:Inserts a row into the
	 * SSCC_LIST table
	 * 
	 * @param ssccListDto
	 * @return SSCCListDto @
	 */
	public SSCCListDto saveSSCCList(SSCCListDto ssccListDto);

	/**
	 * Method Name: updateSSCCList Method Description: Updates a row into the
	 * SSCC_LIST table
	 * 
	 * @param ssccListDto
	 * @return Long @
	 */
	public Long updateSSCCList(SSCCListDto ssccListDto);

	/**
	 * Method Name: userHasAccessToSSCCListPage Method Description:Returns true
	 * if the region is a valid SSCC Catchment region
	 * 
	 * @param userDto
	 * @return Boolean @
	 */
	public Boolean userHasAccessToSSCCListPage(UserProfileDto userDto);

	/**
	 * Method Name: fetchCdCatchmentFromIdCatchment Method Description: fetch
	 * CdCatchment From IdCatchment
	 * 
	 * @param ssccListDto
	 * @return ssccListDto @
	 */
	public SSCCListDto fetchCdCatchmentFromIdCatchment(SSCCListDto ssccListDto);

	/**
	 * Method Name: Method Description:
	 * 
	 * @param ssccListDto
	 * @return boolean @
	 */
	public Boolean isUserSSCCExternal(SSCCListDto ssccListDto);

	/**
	 * Method Name: Method Description:
	 * 
	 * @param ssccListDto
	 * @return boolean @
	 */
	public Boolean userHasSSCCCatchmentAccess(SSCCListDto ssccListDto);

	public SSCCListRes displaySSCCListBean(SSCCListReq ssccListReq);

	public List<String> fetchExcludeOptionsforCatchment(List<String> validCatchmentList);
}
