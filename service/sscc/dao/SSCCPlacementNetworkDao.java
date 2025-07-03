package us.tx.state.dfps.service.sscc.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dto.SSCCPlcmntRsrcLinkMCDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCParameterDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkResourceDetailDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkResourceDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkValidationDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * SSCCPlacementNetworkDao Aug 16, 2018- 3:06:15 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public interface SSCCPlacementNetworkDao {

	/**
	 * Method Name: setResourceDetailsAddMode Method Description: This method is
	 * used to setResourceDetailsAddMode
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	public void setResourceDetailsAddMode(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: setAgencyHomeListAddMode Method Description: This method is
	 * used to setAgencyHomeListAddMode
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	public void setAgencyHomeListAddMode(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: setResourceDetails Method Description: This method is used
	 * to setResourceDetails
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	public void setResourceDetails(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: setAgencyHomeList Method Description: This method is used to
	 * setAgencyHomeList
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	public void setAgencyHomeList(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: setPlacementNetworkTimeline Method Description: This method
	 * is used to setPlacementNetworkTimeline
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	public void setPlacementNetworkTimeline(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: setMedicalConsenter Method Description: This method is used
	 * to setMedicalConsenter
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	public void setMedicalConsenter(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: setAgencyHomeDetailsAddMode Method Description: This method
	 * is used to setAgencyHomeDetailsAddMode
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	public void setAgencyHomeDetailsAddMode(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: setPlacementAgencyHomeMC Method Description: This method is
	 * used to setPlacementAgencyHomeMC
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	public void setPlacementAgencyHomeMC(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: setAgencyHomeDetailsEditMode Method Description: This method
	 * is used to setAgencyHomeDetailsEditMode
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	public void setAgencyHomeDetailsEditMode(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto);

	/**
	 * Method Name: insertSsccPlcmtRsrcLink Method Description: This method is
	 * used to insertSsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @param idResourceSscc
	 * @return void
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
	 * @param ssccPlacementNetworkResourceDetailDto
	 * @return void
	 */
	public void updateAgencyHomeSsccPlcmtRsrcLink(
			SSCCPlacementNetworkResourceDetailDto ssccPlacementNetworkResourceDetailDto);

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
	 * @return
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
	 * gets the BATCH_SSCC_PARAMETERS details for the network.
	 * 
	 * @param idResource
	 * @return SSCCParameterDto
	 */
	public SSCCParameterDto getSsccResourceHeaderDetails(Long idResource);

	/**
	 * Method Name: getCPANetworkStatus Method Description: This method is used
	 * to get CPA NetworkStatus
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
	 * Method Name: hasPlacementOpen
	 *
	 * Method Description: his method checks Resource has Placement open or
	 * ended. If Placement End date is > Date Start it will return true else
	 * false.
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
	public SSCCPlacementNetworkValidationDto validateResourceId(
			SSCCPlacementNetworkResourceDetailDto ssccPlacementNetworkResourceDetailDto);

	/**
	 *Method Name:	removeSsccPlcmntRsrcLinkMC
	 *Method Description: This method is used to remove SsccPlcmnt RsrcLink MC
	 *@param ssccPlcmntRsrcLinkMCDto
	 */
	public void removeSsccPlcmntRsrcLinkMC(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto);

}
