package us.tx.state.dfps.service.facilitystageinfo.dao;

import us.tx.state.dfps.common.dto.FacilityStageInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FacilityStageInfoDao will implemented all operation defined in
 * FacilityStageInfoService Interface related FacilityStageInfo module. Feb 9, 2018-
 * 2:02:21 PM © 2017 Texas Department of Family and Protective Services
 */
public interface FacilityStageInfoDao {

    /**
     * Method Name: getFacilityInfo Method Description:This method returns
     * FacilityStageInfoDto
     *
     * @param idStage
     * @return FacilityStageInfoDto
     */
    public FacilityStageInfoDto getFacilityInfo(Long idStage);
}
