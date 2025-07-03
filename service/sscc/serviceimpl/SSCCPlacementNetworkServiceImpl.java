package us.tx.state.dfps.service.sscc.serviceimpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dto.SSCCPlcmntRsrcLinkMCDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCParameterDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkResourceDetailDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkResourceDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkValidationDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.sscc.dao.SSCCPlacementNetworkDao;
import us.tx.state.dfps.service.sscc.service.SSCCPlacementNetworkService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * SSCCPlacementNetworkServiceImpl Sep 6, 2018- 4:12:11 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class SSCCPlacementNetworkServiceImpl implements SSCCPlacementNetworkService {

	@Autowired
	SSCCPlacementNetworkDao ssccPlacementNetworkDao;

	/**
	 * Method Name: getSsccPlacementNetworkList
	 *
	 * Method Description: This method is used to get SSCC Placement Network
	 * List
	 * 
	 * @param idRsrcSscc
	 * @return List<SSCCPlacementNetworkListDto>
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SSCCPlacementNetworkListDto> getSsccPlacementNetworkList(Long idRsrcSscc) {
		return ssccPlacementNetworkDao.getSsccPlacementNetworkList(idRsrcSscc);
	}

	/**
	 * Method Name: getResourceDetailsAddMode
	 *
	 * Method Description: This method is used to get Resource Details in
	 * AddMode
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return SSCCPlacementNetworkResourceDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlacementNetworkResourceDto getResourceDetailsAddMode(
			SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {

		ssccPlacementNetworkDao.setResourceDetailsAddMode(ssccPlacementNetworkResourceDto);
		ssccPlacementNetworkDao.setAgencyHomeListAddMode(ssccPlacementNetworkResourceDto);
		return ssccPlacementNetworkResourceDto;

	}

	/**
	 * Method Name: getSsccPlacementNetworkDetails
	 *
	 * Method Description: This method is used to get SSCC Placement Network
	 * Details
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return SSCCPlacementNetworkResourceDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlacementNetworkResourceDto getSsccPlacementNetworkDetails(
			SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {
		String indAgencyHomeDetailsAddMode = ssccPlacementNetworkResourceDto.getIndAgencyHomeDetailsAddMode();
		String indAgencyHomeDetailsEditMode = ssccPlacementNetworkResourceDto.getIndAgencyHomeDetailsEditMode();
		String indResourceDetailsAddMode = ssccPlacementNetworkResourceDto.getIndResourceDetailsAddMode();

		if (ServiceConstants.Y.equals(indAgencyHomeDetailsAddMode))
			ssccPlacementNetworkDao.setAgencyHomeDetailsAddMode(ssccPlacementNetworkResourceDto);
		else if (ServiceConstants.Y.equals(indAgencyHomeDetailsEditMode))
			ssccPlacementNetworkDao.setAgencyHomeDetailsEditMode(ssccPlacementNetworkResourceDto);
		else if (ServiceConstants.Y.equals(indResourceDetailsAddMode))
			ssccPlacementNetworkDao.setResourceDetailsAddMode(ssccPlacementNetworkResourceDto);
		else
			ssccPlacementNetworkDao.setResourceDetails(ssccPlacementNetworkResourceDto);

		// Agency
		if (ServiceConstants.Y.equals(indAgencyHomeDetailsAddMode)
				|| ServiceConstants.Y.equals(indAgencyHomeDetailsEditMode)) {
			ssccPlacementNetworkDao.setPlacementAgencyHomeMC(ssccPlacementNetworkResourceDto);
		} else if (ServiceConstants.Y.equals(indResourceDetailsAddMode)) {
			ssccPlacementNetworkDao.setAgencyHomeListAddMode(ssccPlacementNetworkResourceDto);
		} else {
			ssccPlacementNetworkDao.setMedicalConsenter(ssccPlacementNetworkResourceDto);
			ssccPlacementNetworkDao.setAgencyHomeList(ssccPlacementNetworkResourceDto);
		}

		ssccPlacementNetworkDao.setPlacementNetworkTimeline(ssccPlacementNetworkResourceDto);
		return ssccPlacementNetworkResourceDto;

	}

	/**
	 * Method Name: insertSsccPlcmtRsrcLink
	 *
	 * Method Description: This method is used to insert SsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @param idResourceSscc
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void insertSsccPlcmtRsrcLink(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto,
			Long idResourceSscc) {
		ssccPlacementNetworkDao.insertSsccPlcmtRsrcLink(ssccPlacementNetworkResourceDto, idResourceSscc);
	}

	/**
	 * Method Name: insertAgencyHomeSsccPlcmtRsrcLink
	 *
	 * Method Description: This method is used to insert AgencyHome
	 * SsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @param idResourceSscc
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void insertAgencyHomeSsccPlcmtRsrcLink(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto,
			Long idResourceSscc) {
		ssccPlacementNetworkDao.insertAgencyHomeSsccPlcmtRsrcLink(ssccPlacementNetworkResourceDto, idResourceSscc);
	}

	/**
	 * Method Name: updateAgencyHomeSsccPlcmtRsrcLink
	 *
	 * Method Description: This method is used to update AgencyHome
	 * SsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @return void
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateAgencyHomeSsccPlcmtRsrcLink(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {
		ssccPlacementNetworkDao.updateAgencyHomeSsccPlcmtRsrcLink(
				ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto());
	}

	/**
	 * Method Name: updateSsccPlcmtRsrcLink
	 *
	 * Method Description: This method is used to update SsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateSsccPlcmtRsrcLink(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {
		ssccPlacementNetworkDao.updateSsccPlcmtRsrcLink(ssccPlacementNetworkResourceDto);
	}

	/**
	 * Method Name: insertSsccPlcmntRsrcLinkMC
	 *
	 * Method Description: This method is used to insert SsccPlcmntRsrcLinkMC
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void insertSsccPlcmntRsrcLinkMC(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto) {
		ssccPlacementNetworkDao.insertSsccPlcmntRsrcLinkMC(ssccPlcmntRsrcLinkMCDto);
	}

	/**
	 * Method Name: updateSsccPlcmntRsrcLinkMC
	 * 
	 * Method Description: This method is used to update SsccPlcmntRsrcLinkMC
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateSsccPlcmntRsrcLinkMC(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto) {
		ssccPlacementNetworkDao.updateSsccPlcmntRsrcLinkMC(ssccPlcmntRsrcLinkMCDto);
	}

	/**
	 * Method Name: removeSsccPlcmntRsrcLinkMC
	 * 
	 * Method Description: This method is used to remove SsccPlcmntRsrcLinkMC
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void removeSsccPlcmntRsrcLinkMC(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto) {
		ssccPlacementNetworkDao.removeSsccPlcmntRsrcLinkMC(ssccPlcmntRsrcLinkMCDto);
	}
	/**
	 * Method Name: getSSCCPlcmntRsrcLinkMCByID Method Description: This method
	 * is used to get SSCCPlcmntRsrcLinkMC By ID
	 * 
	 * @param idSSCCPlcmtRsrcLinkMC
	 * @return SSCCPlcmntRsrcLinkMCDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmntRsrcLinkMCDto getSSCCPlcmntRsrcLinkMCById(Long idSSCCPlcmtRsrcLinkMC) {
		SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto = ssccPlacementNetworkDao
				.getSSCCPlcmntRsrcLinkMCById(idSSCCPlcmtRsrcLinkMC);
		return ssccPlcmntRsrcLinkMCDto;
	}

	/**
	 * Method Name: getRsrcMedCnsntrByRsrcPrsn
	 *
	 * Method Description: This method is used to get Rsrc MedCnsntr By RsrcPrsn
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 * @return SSCCPlcmntRsrcLinkMCDto
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmntRsrcLinkMCDto getRsrcMedCnsntrByRsrcPrsn(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto) {
		ssccPlcmntRsrcLinkMCDto = ssccPlacementNetworkDao.getRsrcMedCnsntrByRsrcPrsn(ssccPlcmntRsrcLinkMCDto);
		return ssccPlcmntRsrcLinkMCDto;
	}

	/**
	 * Method Name: isAgncyHmActvInOtherRsrc
	 *
	 * Method Description: This method is used to check whether agency is active
	 * in any other resource
	 * 
	 * @param ssccPlcmntNtwrkResourceDetailDto
	 * @return boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean isAgncyHmActvInOtherRsrc(SSCCPlacementNetworkResourceDetailDto ssccPlcmntNtwrkResourceDetailDto) {
		return ssccPlacementNetworkDao.isAgncyHmActvInOtherRsrc(ssccPlcmntNtwrkResourceDetailDto);
	}

	/**
	 * Method Name: getSsccResourceHeaderDetails
	 * 
	 * Method Description: This method is used to get SSCCParameterDto
	 * 
	 * @param idResource
	 * @return SSCCParameterDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCParameterDto getSsccResourceHeaderDetails(Long idResource) {
		return ssccPlacementNetworkDao.getSsccResourceHeaderDetails(idResource);
	}

	/**
	 * Method Name: getCPANetworkStatus
	 *
	 * Method Description: This method is used to get CPA Network Status
	 * 
	 * @param idRsrcCpa
	 * @param idRsrcSscc
	 * @return String
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getCPANetworkStatus(Long idRsrcCpa, Long idRsrcSscc) {
		return ssccPlacementNetworkDao.getCPANetworkStatus(idRsrcCpa, idRsrcSscc);
	}

	/**
	 * Method Name: hasPlacementOpen
	 *
	 * Method Description: This method checks Resource has Placement open or
	 * ended. If Placement End date is > Date Start it will return true else
	 * false.
	 * 
	 * @param idResource
	 * @param idRsrcSscc
	 * @param placementLinkType
	 * @param dtStart
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean hasPlacementOpen(Long idResource, Long idRsrcSscc, String placementLinkType, Date dtStart) {
		return ssccPlacementNetworkDao.hasPlacementOpen(idResource, idRsrcSscc, placementLinkType, dtStart);
	}

	/**
	 * Method Name: validateResourceId
	 *
	 * Method Description: This method is used to validate Resource Id
	 * 
	 * @param ssccPlacementNetworkResourceDetailDto
	 * @return SSCCPlacementNetworkValidationDto
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlacementNetworkValidationDto validateResourceId(
			SSCCPlacementNetworkResourceDetailDto ssccPlacementNetworkResourceDetailDto) {
		return ssccPlacementNetworkDao.validateResourceId(ssccPlacementNetworkResourceDetailDto);
	}
}
