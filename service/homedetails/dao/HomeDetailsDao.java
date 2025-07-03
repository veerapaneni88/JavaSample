package us.tx.state.dfps.service.homedetails.dao;

import us.tx.state.dfps.service.homedetails.dto.ChildrenPlacementInfoDto;
import us.tx.state.dfps.service.homedetails.dto.HomeInfoDto;
import us.tx.state.dfps.service.homedetails.dto.HouseHoldMembersDto;

import java.util.List;

public interface HomeDetailsDao {
    HomeInfoDto getHomeInfoDto(Long idStage);
    List<HomeInfoDto> getHomeInfoDtoList(Long idStage);
    List<HouseHoldMembersDto> getHouseHoldMembers(Long idStage);
    List<ChildrenPlacementInfoDto> getChildrenInfo(Long idResource);
}
