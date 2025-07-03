package us.tx.state.dfps.service.facilitystageinfo.service;

import us.tx.state.dfps.common.dto.FacilityStageInfoDto;
import us.tx.state.dfps.service.common.request.FacilityStageInfoReq;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * FacilityStageInfoService will have all operation which are mapped to FacilityStageInfo
 * module. Feb 9, 2018- 2:01:02 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface FacilityStageInfoService {

    public FacilityStageInfoDto getFacilityInfo(FacilityStageInfoReq facilityStageInfoReq);
}
