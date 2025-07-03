package us.tx.state.dfps.service.homedetails.service;

import us.tx.state.dfps.service.common.request.FacilityStageInfoReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

public interface HomeDetailsService {
    PreFillDataServiceDto getHomeDetailsData(FacilityStageInfoReq facilityStageInfoReq);
}
