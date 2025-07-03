package us.tx.state.dfps.service.facilitystageinfo.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.common.dto.FacilityStageInfoDto;
import us.tx.state.dfps.service.common.request.FacilityStageInfoReq;
import us.tx.state.dfps.service.facilitystageinfo.dao.FacilityStageInfoDao;
import us.tx.state.dfps.service.facilitystageinfo.service.FacilityStageInfoService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FacilityStageInfoServiceImpl will implemented all operation defined in
 * FacilityStageInfoService Interface related FacilityStageInfo module. Feb 9, 2018-
 * 2:01:28 PM © 2017 Texas Department of Family and Protective Services
 */

@Service
@Transactional
public class FacilityStageInfoServiceImpl implements FacilityStageInfoService {

    @Autowired
    private FacilityStageInfoDao facilityStageInfoDao;

    @Override
    public FacilityStageInfoDto getFacilityInfo(FacilityStageInfoReq facilityStageInfoReq) {
        return facilityStageInfoDao.getFacilityInfo(facilityStageInfoReq.getIdStage());
    }
}
