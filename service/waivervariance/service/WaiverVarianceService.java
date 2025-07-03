package us.tx.state.dfps.service.waivervariance.service;

import us.tx.state.dfps.service.common.request.WaiverVarianceReq;
import us.tx.state.dfps.web.waivervariance.dto.WaiverVarianceBean;

public interface WaiverVarianceService {

    public WaiverVarianceBean getWaiverVariance(WaiverVarianceReq waiverVarianceReq);
}
