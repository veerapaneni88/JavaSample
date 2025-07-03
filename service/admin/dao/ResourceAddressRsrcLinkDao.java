package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.ResourceAddressRsrcLinkInDto;
import us.tx.state.dfps.service.admin.dto.ResourceAddressRsrcLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * interface calls Cres02dDaoImpl Aug 9, 2017- 7:22:28 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ResourceAddressRsrcLinkDao {

	public List<ResourceAddressRsrcLinkOutDto> searchSubContractor(ResourceAddressRsrcLinkInDto pInputDataRec);
}
