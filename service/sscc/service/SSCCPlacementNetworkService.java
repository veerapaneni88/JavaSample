package us.tx.state.dfps.service.sscc.service;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.admin.dto.SSCCPlcmntRsrcLinkMCDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCParameterDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkResourceDetailDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkResourceDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkValidationDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * SSCCPlacementNetworkService Sep 6, 2018- 4:11:59 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface SSCCPlacementNetworkService {

	/**
	 * Method Name: getResourceDetailsAddMode Method Description: This method is
	 * used to get Resource Details in AddMode
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return SSCCPlacementNetworkResourceDto
	 */
	public SSCCPlacementNetworkResourceDto getResourceDetailsAddMode(
			SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: getSsccPlacementNetworkDetails Method Description: This
	 * method is used to getSsccPlacementNetworkDetails
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return SSCCPlacementNetworkResourceDto
	 */
	public SSCCPlacementNetworkResourceDto getSsccPlacementNetworkDetails(
			SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: insertSsccPlcmtRsrcLink Method Description: This method is
	 * used to insertSsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @param idResourceSscc
	 */
	public void insertSsccPlcmtRsrcLink(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto,
			Long idResourceSscc);

	/**
	 * Method Name: insertAgencyHomeSsccPlcmtRsrcLink Method Description: This
	 * method is used to insertAgencyHomeSsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @param idResourceSscc
	 * @return void
	 */
	public void insertAgencyHomeSsccPlcmtRsrcLink(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto,
			Long idResourceSscc);

	/**
	 * Method Name: updateAgencyHomeSsccPlcmtRsrcLink Method Description: This
	 * method is used to updateAgencyHomeSsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	public void updateAgencyHomeSsccPlcmtRsrcLink(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: updateSsccPlcmtRsrcLink Method Description: This method is
	 * used to updateSsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	public void updateSsccPlcmtRsrcLink(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: insertSsccPlcmntRsrcLinkMC Method Description: This method
	 * is used to insertSsccPlcmntRsrcLinkMC
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 * @return void
	 */
	public void insertSsccPlcmntRsrcLinkMC(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto);

	/**
	 * Method Name: updateSsccPlcmntRsrcLinkMC Method Description: This method
	 * is used to updateSsccPlcmntRsrcLinkMC
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 * @return void
	 */
	public void updateSsccPlcmntRsrcLinkMC(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto);

	/**
	 * Method Name: getSSCCPlcmntRsrcLinkMCById Method Description: This method
	 * is used to getSSCCPlcmntRsrcLinkMCById
	 * 
	 * @param IdSSCCPlcmtRsrcLinkMC
	 * @return SSCCPlcmntRsrcLinkMCDto
	 */
	public SSCCPlcmntRsrcLinkMCDto getSSCCPlcmntRsrcLinkMCById(Long IdSSCCPlcmtRsrcLinkMC);

	/**
	 * Method Name: getRsrcMedCnsntrByRsrcPrsn Method Description: This method
	 * is used to getRsrcMedCnsntrByRsrcPrsn
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 * @return SSCCPlcmntRsrcLinkMCDto
	 */
	public SSCCPlcmntRsrcLinkMCDto getRsrcMedCnsntrByRsrcPrsn(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto);

	/**
	 * Method Name: isAgncyHmActvInOtherRsrc Method Description: This method is
	 * used to isAgncyHmActvInOtherRsrc
	 * 
	 * @param ssccPlcmntNtwrkResourceDetailDto
	 * @return boolean
	 */
	public boolean isAgncyHmActvInOtherRsrc(SSCCPlacementNetworkResourceDetailDto ssccPlcmntNtwrkResourceDetailDto);

	/**
	 * Method Name: getSsccResourceHeaderDetails Method Description: This method
	 * is used to get SSCCParameterDto
	 * 
	 * @param idResource
	 * @return SSCCParameterDto
	 */
	public SSCCParameterDto getSsccResourceHeaderDetails(Long idResource);

	/**
	 * Method Name: getCPANetworkStatus Method Description: This method is used
	 * to get CPA Network Status
	 * 
	 * @param idRsrcCpa
	 * @param idRsrcSscc
	 * @return String
	 */
	public String getCPANetworkStatus(Long idRsrcCpa, Long idRsrcSscc);

	/**
	 * Method Name: getSsccPlacementNetworkList Method Description: This method
	 * is used to getSsccPlacementNetworkList
	 * 
	 * @param idRsrcSscc
	 * @return List<SSCCPlacementNetworkListDto>
	 */
	public List<SSCCPlacementNetworkListDto> getSsccPlacementNetworkList(Long idRsrcSscc);

	/**
	 * Method Name: hasPlacementOpen Method Description: his method checks
	 * Resource has Placement open or ended. If Placement End date is > Date
	 * Start it will return true else false.
	 * 
	 * @param idResource
	 * @param idRsrcSscc
	 * @param placementLinkType
	 * @param dtStart
	 * @return Boolean
	 */
	public Boolean hasPlacementOpen(Long idResource, Long idRsrcSscc, String placementLinkType, Date dtStart);

	/**
	 * Method Name: validateResourceId Method Description: This method is used
	 * to validate Resource Id
	 * 
	 * @param ssccPlacementNetworkResourceDetailDto
	 * @return SSCCPlacementNetworkValidationDto
	 */
	SSCCPlacementNetworkValidationDto validateResourceId(
			SSCCPlacementNetworkResourceDetailDto ssccPlacementNetworkResourceDetailDto);

	/**
	 *Method Name:	removeSsccPlcmntRsrcLinkMC
	 *Method Description: This method is used to remove Sscc Plcmnt RsrcLink MC
	 *@param ssccPlcmntRsrcLinkMCDto
	 */
	public void removeSsccPlcmntRsrcLinkMC(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto);

}
