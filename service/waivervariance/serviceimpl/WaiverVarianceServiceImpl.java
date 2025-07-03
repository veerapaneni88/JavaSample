package us.tx.state.dfps.service.waivervariance.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.WaiverVarianceReq;
import us.tx.state.dfps.service.waivervariance.dao.WaiverVarianceDao;
import us.tx.state.dfps.service.waivervariance.service.WaiverVarianceService;
import us.tx.state.dfps.web.waivervariance.dto.WaiverVarianceBean;

@Service
@Transactional
public class WaiverVarianceServiceImpl implements WaiverVarianceService {

    @Autowired
    private WaiverVarianceDao waiverVarianceDao;

    @Override
    public WaiverVarianceBean getWaiverVariance(WaiverVarianceReq waiverVarianceReq) {
        return waiverVarianceDao.getWaiverVarianceDetails(waiverVarianceReq.getIdEvent(), waiverVarianceReq.getIdStage(), waiverVarianceReq.getPageMode());
    }
}
