package us.tx.state.dfps.service.fahomestudy.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.service.common.request.FAHomeStudyReq;
import us.tx.state.dfps.service.fahomestudy.dao.FAHomeStudyDao;
import us.tx.state.dfps.service.fahomestudy.dto.FAHomeStudyDto;
import us.tx.state.dfps.service.fahomestudy.service.FAHomeStudyService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.FAHomeStudyPrefillData;
import us.tx.state.dfps.service.homedetails.dao.HomeDetailsDao;
import us.tx.state.dfps.service.homedetails.dto.HomeInfoDto;
import us.tx.state.dfps.service.homedetails.dto.HouseHoldMembersDto;

import java.util.List;

@Transactional
@Service
public class FAHomeStudyServiceImpl implements FAHomeStudyService {

    @Autowired
    FAHomeStudyDao faHomeStudyDao;

    @Autowired
    HomeDetailsDao homeDetailsDao;

    @Autowired
    FAHomeStudyPrefillData faHomeStudyPrefillData;

    @Override
    public PreFillDataServiceDto getHomeStudyDetail(FAHomeStudyReq faHomeStudyReq) {
        FAHomeStudyDto homeStudyDto = new FAHomeStudyDto();
        List<HomeInfoDto> homeInfoDtoList = homeDetailsDao.getHomeInfoDtoList(faHomeStudyReq.getIdStage());
        if(null != homeInfoDtoList && !homeInfoDtoList.isEmpty()){
            homeStudyDto.setHomeInfoDto(homeInfoDtoList.get(0));
        }
        List<HouseHoldMembersDto> houseHoldMembersDtoList = faHomeStudyDao.getHouseHoldMembersInfo(faHomeStudyReq.getIdStage());
        homeStudyDto.setHouseHoldMembers(houseHoldMembersDtoList);
        homeStudyDto.setInquiryDateDto(faHomeStudyDao.getInquiryDate(faHomeStudyReq.getIdStage()));
        return faHomeStudyPrefillData.returnPrefillData(homeStudyDto);
    }
}
