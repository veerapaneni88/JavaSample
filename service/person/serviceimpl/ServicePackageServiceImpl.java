package us.tx.state.dfps.service.person.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.FacilityServicePackageDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.ServicePackageDao;
import us.tx.state.dfps.service.person.dto.FacilityServicePackageDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.person.service.ServicePackageService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServicePackageServiceImpl implements ServicePackageService {

    @Autowired
    ServicePackageDao servicePackageDao;

    @Autowired
    FacilityServicePackageDao facilityServicePackageDao;

    @Autowired
    LookupDao lookupDao;

    @Override
    public List<ServicePackageDtlDto> getServicePackageDetails(Long caseId, Long stageId) {
        if(ObjectUtils.isEmpty(caseId) || ObjectUtils.isEmpty(stageId)) {
            return new ArrayList<>();
        }
        List<ServicePackageDtlDto> servicePackageDtlDtoList = servicePackageDao.getServicePackageDetails(caseId, stageId);
        if (!ObjectUtils.isEmpty(servicePackageDtlDtoList)) {
            return servicePackageDtlDtoList.stream().map(dto ->
            {
                dto.setSvcPkgDecode(lookupDao.decode(ServiceConstants.CSVCCODE, dto.getSvcPkgCd()));
                dto.setSvcPkgAddonDecode(lookupDao.decode(ServiceConstants.CSVCCODE, dto.getSvcPkgAddonCd()));
                return dto;
            })
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<ServicePackageDtlDto> getServicePackages(Long caseId, Long stageId) {
        if(ObjectUtils.isEmpty(caseId) || ObjectUtils.isEmpty(stageId)) {
            return new ArrayList<>();
        }
        return servicePackageDao.getServicePackages(caseId, stageId);
    }

    @Override
    public List<ServicePackageDtlDto> getActiveSelectedServicePackage(Long caseId, Long stageId) {
        if(ObjectUtils.isEmpty(caseId) || ObjectUtils.isEmpty(stageId)) {
            return new ArrayList<>();
        }
        return servicePackageDao.getActiveSelectedServicePackage(caseId, stageId);
    }

    @Override
    public List<FacilityServicePackageDto> getFacilityServicePackages(Long resourceId, Date dtPlacementStart) {
        if(ObjectUtils.isEmpty(resourceId)) {
            return new ArrayList<>();
        }
        return servicePackageDao.getFacilityServicePackages(resourceId, dtPlacementStart);
    }

    /**
     * This method will find the Service Packages that are active as of the Placement Start Date.
     * @param caseId
     * @param stageId
     * @param dtPlcmntStart
     * @return
     */
    @Override
    public List<ServicePackageDtlDto> getActiveServicePackages(Long caseId, Long stageId, Date dtPlcmntStart) {
        if(ObjectUtils.isEmpty(caseId) || ObjectUtils.isEmpty(stageId)) {
            return new ArrayList<>();
        }
        return servicePackageDao.getActiveServicePackages(caseId, stageId, dtPlcmntStart);
    }

    @Override
    public List<ServicePackageDtlDto> getActiveFacilitySvcPkgAddons(Long caseId, Long stageId, Date dtPlacementStart){
        if(ObjectUtils.isEmpty(caseId) || ObjectUtils.isEmpty(stageId)) {
            return new ArrayList<>();
        }
        return servicePackageDao.getActiveFacilitySvcPkgAddons(caseId, stageId, dtPlacementStart);
    }

    @Override
    public Boolean addFcltySvcPkgCredentials(Long resourceId, Long caseId, Long stageId, Date dtPlacementStart, Long userId){
        List<ServicePackageDtlDto> svcPkgDtlDtos = getActiveServicePackages(caseId, stageId, dtPlacementStart);
        if(svcPkgDtlDtos!= null && !svcPkgDtlDtos.isEmpty())
            facilityServicePackageDao.addFcltySvcPkgCredentails(resourceId, svcPkgDtlDtos.get(0), dtPlacementStart, userId);
        return Boolean.TRUE;
    }
}