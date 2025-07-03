package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.FacilityServicePackageDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import java.util.Date;
import java.util.List;

public interface FacilityServicePackageDao {

    public List<FacilityServicePackageDto> getFacilityServicePackageDtoList(
            long idResource);

    public void addFcltySvcPkgCredentails(Long resourceId, ServicePackageDtlDto svcPkgDtlDto, Date dtPlacementStart, Long userId);
}
