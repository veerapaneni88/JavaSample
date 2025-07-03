package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.EventPersonAdminLinkDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Ccmn68dDao
 * Aug 6, 2017- 7:44:58 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface EventPersonLinkAdminDao {
	public void updateEventPersonLink(EventPersonAdminLinkDto iCcmn68diDto);

	public long modifyEventPersonLink(EventPersonAdminLinkDto ccmn68di);
}
