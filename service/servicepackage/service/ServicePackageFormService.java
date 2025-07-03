package us.tx.state.dfps.service.servicepackage.service;

import us.tx.state.dfps.service.common.request.ServicePackageReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 *
 */
public interface ServicePackageFormService {
    /**
     *
     * @param servicePackageReq
     * @return
     */
    PreFillDataServiceDto getServicePackageDetails(ServicePackageReq servicePackageReq);
}
