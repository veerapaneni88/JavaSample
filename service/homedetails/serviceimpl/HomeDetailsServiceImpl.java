package us.tx.state.dfps.service.homedetails.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.common.request.FacilityStageInfoReq;
import us.tx.state.dfps.service.facilitystageinfo.service.FacilityStageInfoService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.HomeDetailsPrefillData;
import us.tx.state.dfps.service.homedetails.dao.HomeDetailsDao;
import us.tx.state.dfps.service.homedetails.dto.HomeDetailsDto;
import us.tx.state.dfps.service.homedetails.service.HomeDetailsService;

@Service
@Transactional
public class HomeDetailsServiceImpl implements HomeDetailsService {

    @Autowired
    HomeDetailsDao homeDetailsDao;

    @Autowired
    HomeDetailsPrefillData homeDetailsPrefillData;

    @Autowired
    FacilityStageInfoService facilityStageInfoService;

    @Override
    public PreFillDataServiceDto getHomeDetailsData(FacilityStageInfoReq facilityStageInfoReq) {
        HomeDetailsDto homeDetailsDto = new HomeDetailsDto();

        homeDetailsDto.setHomeInfoDtoList(homeDetailsDao.getHomeInfoDtoList(facilityStageInfoReq.getIdStage()));
        homeDetailsDto.setHouseHoldMembersDtoList(homeDetailsDao.getHouseHoldMembers(facilityStageInfoReq.getIdStage()));
        if(!ObjectUtils.isEmpty(homeDetailsDto.getHomeInfoDtoList())) {
            homeDetailsDto.setChildrenPlacementInfoDtoList(homeDetailsDao.getChildrenInfo(homeDetailsDto.getHomeInfoDtoList().get(0).getIdResource()));
        }
        return homeDetailsPrefillData.returnPrefillData(homeDetailsDto);
    }
}
