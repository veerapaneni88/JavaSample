package us.tx.state.dfps.service.person.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.ServicePkgAttnDtlReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.ServicePkgAttnDtlRes;
import us.tx.state.dfps.service.person.dto.FacilityServicePackageDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.person.service.ServicePackageService;

import java.util.List;
import java.util.Locale;

@Api(tags = {"identity"})
@RestController
@RequestMapping("/servicepackage")
public class ServicePackageController {

    @Autowired
    ServicePackageService servicePackageService;

    @Autowired
    MessageSource messageSource;

    @PostMapping(value = "/getServicePackageDetails", headers = {"Accept=application/json"})
    public ServicePkgAttnDtlRes getServicePackageDetails(@RequestBody ServicePkgAttnDtlReq request) {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getCaseId()) || ObjectUtils.isEmpty(request.getStageId())) {
            throwCustomException();
        }
        List<ServicePackageDtlDto> servicePackageDtlDtoList = servicePackageService.getServicePackageDetails(request.getCaseId(), request.getStageId());
        ServicePkgAttnDtlRes servicePkgAttnDtlRes = new ServicePkgAttnDtlRes();
        servicePkgAttnDtlRes.setServicePackageDtlDtos(servicePackageDtlDtoList);
        return servicePkgAttnDtlRes;
    }

    @PostMapping(value = "/getServicePackages", headers = {"Accept=application/json"})
    public ServicePkgAttnDtlRes getServicePackages(@RequestBody ServicePkgAttnDtlReq request) {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getCaseId()) || ObjectUtils.isEmpty(request.getStageId())) {
            throwCustomException();
        }
        List<ServicePackageDtlDto> servicePackageDtlDtoList = servicePackageService.getServicePackages(request.getCaseId(), request.getStageId());
        ServicePkgAttnDtlRes servicePkgAttnDtlRes = new ServicePkgAttnDtlRes();
        servicePkgAttnDtlRes.setServicePackageDtlDtos(servicePackageDtlDtoList);
        return servicePkgAttnDtlRes;
    }

    @PostMapping(value = "/getFcltyServicePackages", headers = {"Accept=application/json"})
    public ServicePkgAttnDtlRes getFacilityServicePackages(@RequestBody ServicePkgAttnDtlReq request) {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getResourceId())) {
            throwCustomException();
        }
        List<FacilityServicePackageDto> facilityServicePackageDtoList = servicePackageService.getFacilityServicePackages(request.getResourceId(),
                request.getDtPlacementStart());
        ServicePkgAttnDtlRes servicePkgAttnDtlRes = new ServicePkgAttnDtlRes();
        servicePkgAttnDtlRes.setFacilityServicePackageDtoList(facilityServicePackageDtoList);
        return servicePkgAttnDtlRes;
    }

    @PostMapping(value = "/getActiveServicePackages", headers = {"Accept=application/json"})
    public ServicePkgAttnDtlRes getActiveServicePackages(@RequestBody ServicePkgAttnDtlReq request) {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getCaseId()) || ObjectUtils.isEmpty(request.getStageId())) {
            throwCustomException();
        }
        List<ServicePackageDtlDto> servicePackageDtlDtoList = servicePackageService.getActiveServicePackages(request.getCaseId(), request.getStageId(),
                request.getDtPlacementStart());
        ServicePkgAttnDtlRes servicePkgAttnDtlRes = new ServicePkgAttnDtlRes();
        servicePkgAttnDtlRes.setServicePackageDtlDtos(servicePackageDtlDtoList);
        return servicePkgAttnDtlRes;
    }

    @PostMapping(value = "/getActiveFacilitySvcPkgAddons", headers = {"Accept=application/json"})
    public ServicePkgAttnDtlRes getActiveFacilitySvcPkgAddons(@RequestBody ServicePkgAttnDtlReq request) {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getCaseId()) || ObjectUtils.isEmpty(request.getStageId())) {
            throwCustomException();
        }
        List<ServicePackageDtlDto> servicePackageDtlDtoList = servicePackageService.getActiveFacilitySvcPkgAddons(request.getCaseId(), request.getStageId(),
                request.getDtPlacementStart());
        ServicePkgAttnDtlRes servicePkgAttnDtlRes = new ServicePkgAttnDtlRes();
        servicePkgAttnDtlRes.setServicePackageDtlDtos(servicePackageDtlDtoList);
        return servicePkgAttnDtlRes;
    }

    private void throwCustomException(){
        throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
    }

    @PostMapping(value = "/addFcltySvcPkgCredentails", headers = {"Accept=application/json"})
    public CommonBooleanRes addFcltySvcPkgCredentails(@RequestBody ServicePkgAttnDtlReq request) {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getCaseId()) || ObjectUtils.isEmpty(request.getStageId())) {
            throwCustomException();
        }
        CommonBooleanRes res = new CommonBooleanRes();
        res.setExists(servicePackageService.addFcltySvcPkgCredentials(request.getResourceId(), request.getCaseId(), request.getStageId(),
                request.getDtPlacementStart(), request.getUserId()));
        return res;
    }
}
