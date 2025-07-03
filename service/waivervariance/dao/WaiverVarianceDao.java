package us.tx.state.dfps.service.waivervariance.dao;

import us.tx.state.dfps.web.waivervariance.dto.WaiverVarianceBean;

public interface WaiverVarianceDao {

    public WaiverVarianceBean getWaiverVarianceDetails(Long idEvent, Long idStage, String pageMode);
}
