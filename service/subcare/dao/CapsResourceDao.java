package us.tx.state.dfps.service.subcare.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dto.CapResourceInDto;
import us.tx.state.dfps.service.admin.dto.CapResourceOutDto;
import us.tx.state.dfps.service.common.request.CvsFaHomeReq;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.response.FacilityDetailRes;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.subcare.dto.CapsResourceDto;
import us.tx.state.dfps.service.subcare.dto.CapsResourceLinkDto;
import us.tx.state.dfps.service.subcare.dto.ResourceAddressDto;
import us.tx.state.dfps.service.subcare.dto.ResourceContactDto;
import us.tx.state.dfps.service.subcare.dto.ResourceEmailDto;
import us.tx.state.dfps.service.subcare.dto.ResourceLanguageDto;
import us.tx.state.dfps.service.subcare.dto.ResourcePhoneDto;
import us.tx.state.dfps.service.subcare.dto.RsrcLinkDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Office DAO
 * Interface Sep 9, 2017- 12:28:27 PM © 2017 Texas Department of Family and
 * Protective Services
 */

public interface CapsResourceDao {

	/**
	 * 
	 * Method Description: Method to check if another primary caregiver is
	 * present for CVS Home window. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return Long @
	 */

	public Long getResourceId(CvsFaHomeReq cvsFaHomeReq);

	/**
	 * 
	 * Method Description: Method to update NM_PERSON details in CAPS_RESOURCE
	 * Table. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @
	 */

	public void updateNmResource(CvsFaHomeReq cvsFaHomeReq, CapsResource capsResource);

	/**
	 * This DAM performs AUD functionality on the FACILITY INVST DTL table. This
	 * DAM only inserts. Service Name : CCMN03U, DAM Name : CINV54D
	 * 
	 * @param idCapsResource
	 * @return CapsResource @
	 */
	public CapsResource getCapsResourceById(Long idCapsResource);

	/**
	 * This DAM performs AUD functionality on the FACILITY INVST DTL table. This
	 * DAM only inserts. Service Name : CRES04D
	 * 
	 * @param idCapsResource
	 * @return CapsResource @
	 */
	public ResourceDto getResourceById(Long idResource);

	/**
	 * Retrieval service called within predisplay of Rsrc Dtl Service Name :
	 * CRES03S
	 * 
	 * @param idCapsResource
	 * @return CapsResource @
	 */
	public ResourceDto getResourceDtl(Long idResource);

	/**
	 * School District simple query Service Name : CRES08D
	 * artf212958 : passing county code to get school dist code
	 * @param cdSchDist
	 * @return String @
	 */
	public String getSchDistName(String cdSchDist,String cdSchDistCounty);

	/**
	 * Rsrc Phone query list Service Name : CRES14D
	 * 
	 * @param idCapsResource
	 * @return ResourcePhoneDto @
	 */
	public List<ResourcePhoneDto> getResourcePhone(Long idResource);

	/**
	 * Resource Link table simple query which checks for sub-contracting Service
	 * Name : CRES15D
	 * 
	 * @param idCapsResource
	 * @return Boolean @
	 */
	public boolean indSubContracting(Long idRsrcLink);

	/**
	 * Resource Link table simple query which checks for prime contracting.
	 * Service Name : CRES38D
	 * 
	 * @param idRsrcLinkParent
	 * @return Boolean @
	 */
	public boolean indPrimeContracting(Long idRsrcLinkParent);

	/**
	 * Returns a row from the Contract County table if an active contract is
	 * found for the resource. Service Name : CRES39D
	 * 
	 * @param idCapsResource
	 * @return Boolean @
	 */
	public boolean indActiveContract(Long idCapsResource);

	/**
	 * Rsrc Address query list Service Name : CRES13D
	 * 
	 * @param idCapsResource
	 * @return ResourceAddressDto @
	 */
	public List<ResourceAddressDto> getResourceAddress(Long idCapsResource);

	/**
	 * Resource Address contract check Service Name : CRES44D
	 * 
	 * @param idRsrcAddress
	 * @return Boolean @
	 */
	public boolean indRsrcContractCheck(Long idRsrcAddress);

	/**
	 * get ResourceEmail Service Name : CLSCG3D
	 * 
	 * @param idResource
	 * @return ResourceEmailDto @
	 */
	public List<ResourceEmailDto> getResourceEmail(Long idResource);

	/**
	 * get ResourceLanguage Service Name : CLSCG4D
	 * 
	 * @param idResource
	 * @return ResourceLanguageDto @
	 */
	public List<ResourceLanguageDto> getResourceLanguage(Long idResource);

	/**
	 * Gets Parents Resource Id and Name Service Name : CSECE9D
	 * 
	 * @param idResource
	 * @return CapsResourceDto @
	 */
	public RsrcLinkDto getParentsResource(Long idRsrcLinkChild, String rsrcLinkType);

	/**
	 * Gets Resource Contact Service Name : CLSSC7D
	 * 
	 * @param idResource
	 * @return CapsResourceDto @
	 */
	public List<ResourceContactDto> getResourceContact(Long idResource);

	/**
	 * 
	 * Method Name: getCapsResourceLink Method Description: CSEC24D
	 * 
	 * @param idRsrcLinkChild
	 * @param cdRsrcLinkType
	 * @return @
	 */
	public CapsResourceLinkDto getCapsResourceLink(Long idRsrcLinkChild, String cdRsrcLinkType);

	/**
	 * Method Name: updateFacilityDetailsInResource Method Description: This
	 * method is used to updateFacilityDetailsInResource
	 * 
	 * @param facilityDetailSaveReq
	 * @ @return FacilityDetailRes
	 */
	public FacilityDetailRes updateFacilityDetailsInResource(FacilityDetailSaveReq facilityDetailSaveReq);

	/**
	 * Method Name: getResourceType Method Description: This method is used to
	 * getResourceType
	 * 
	 * @param capResourceInDto
	 * @ @return List<CapResourceOutDto>
	 */
	public List<CapResourceOutDto> getResourceType(CapResourceInDto capResourceInDto);

	/**
	 * Method Name: updateCapsResourceAUD Method Description: This DAM is used
	 * by the Approval Save screen to update specific columns on the CAPS
	 * RESOURCE table. Service Name : CCMN35S; DAM Name: CAUDB3D
	 * 
	 * @param capsResourceDto
	 * @param archInputDto
	 * @return
	 */
	public void updateCapsResourceAUD(CapsResourceDto capsResourceDto, ServiceReqHeaderDto archInputDto);

	public String getFaHomeStatusByStageId(Long idStage);

	public Long getCapsResourceByStageId(Long idStage);

	public CapsResource getCapsResourceForStageId(Long idStage);
}
