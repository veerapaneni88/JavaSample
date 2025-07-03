package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.person.dto.FacilityServicePackageDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;

import java.util.Date;
import java.util.List;

public interface ServicePackageService {

    /**
     * Method Name: getServicePkgAttentionDtl
     * Method Description:This method is use to
     * retrieve service package details from PERSON_ADDON_SVC_PKG and PERSON_SVC_PKG tables by giving case id and Stage id
     *
     * @param caseId
     * @param stageId
     * @return List<ServicePackageDtlDto>
     */
    List<ServicePackageDtlDto> getServicePackageDetails(Long caseId, Long stageId);

    /**
     * Method Name: getServicePackages
     * Method Description:This method is use to get all the service packages from PERSON_SVC_PKG table by giving case id and Stage id.
     *
     * @param caseId
     * @param stageId
     * @return List<ServicePackageDtlDto>
     */
    List<ServicePackageDtlDto> getServicePackages(Long caseId, Long stageId);

    /**
     * Method Name: getActiveSelectedServicePackage
     * Method Description:This method is use to get all the Active Selected service packages from PERSON_SVC_PKG table by giving case id and Stage id.
     *
     * @param caseId
     * @param stageId
     * @return List<ServicePackageDtlDto>
     */
    List<ServicePackageDtlDto> getActiveSelectedServicePackage(Long caseId, Long stageId);

    /**
     * Method Name: getFacilityServicePackages
     * Method Description: This method is use to get all the facility service package details from fclty_svc_pkg and FCLTY_SVC_PKG_DTL tables by giving resource id.
     *
     * @param resourceId
     * @param dtPlacementStart
     * @return List<FacilityServicePackageDto>
     */
    List<FacilityServicePackageDto> getFacilityServicePackages(Long resourceId, Date dtPlacementStart);

    /**
     * Method Name: getActiveServicePackages
     * Method Description: This method get the active service packages using case id, stage id resource id.
     *
     * @param caseId
     * @param stageId
     * @param dtPlacementStart
     * @return List<ServicePackageDtlDto>
     */
    List<ServicePackageDtlDto> getActiveServicePackages(Long caseId, Long stageId, Date dtPlacementStart);

    List<ServicePackageDtlDto> getActiveFacilitySvcPkgAddons(Long caseId, Long stageId, Date dtPlacementStart);

    Boolean addFcltySvcPkgCredentials(Long resourceId, Long caseId, Long stageId, Date dtPlacementStart, Long userId);
}
