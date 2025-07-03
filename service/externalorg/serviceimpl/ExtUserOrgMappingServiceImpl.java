/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Interface for External User Organization Mapping Service.
 *July 17, 2018- 3:33:39 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.externalorg.dto.ExtOrgRoleMappingDto;
import us.tx.state.dfps.common.externalorg.dto.ExtOrgRoleMappingHistoryDtl;
import us.tx.state.dfps.common.externalorg.dto.ExternalOrgDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ExtUserMgmntReq;
import us.tx.state.dfps.service.common.request.ExtUserOrgMappingHistoryReq;
import us.tx.state.dfps.service.common.request.ExtUserOrgMappingListReq;
import us.tx.state.dfps.service.common.request.ExtUserOrgResourceLinkDelReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.ExtUserMgmntRes;
import us.tx.state.dfps.service.common.response.ExtUserOrgMappingHistoryRes;
import us.tx.state.dfps.service.common.response.ExtUserOrgMappingListRes;
import us.tx.state.dfps.service.externalorg.dao.ExtUserOrgMappingDao;
import us.tx.state.dfps.service.externalorg.service.ExtUserOrgMappingService;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;

@Service
public class ExtUserOrgMappingServiceImpl implements ExtUserOrgMappingService {

	public ExtUserOrgMappingServiceImpl() {
	}

	@Autowired
	ExtUserOrgMappingDao extUserOrgMappingDao;

	public static final String ORG_RSRC_ASSC_PAGE_NAME = "ORGRESOURCEASSOCIATION";

	public static final String ASSIGN_ORG_ROLE_PAGE_NAME = "ASSIGNORGROLE";

	public static final String RCCP_USER_ROLE = "RCCP";

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExtUserOrgMappingListRes getExtUserOrgMappingList(ExtUserOrgMappingListReq extUserOrgMappingListReq) {
		ExtOrgRoleMappingDto extOrgRoleMappingDto = extUserOrgMappingDao
				.getExtUserOrgMappingList(extUserOrgMappingListReq.getIdPerson());
		ExtUserOrgMappingListRes res = new ExtUserOrgMappingListRes();
		res.setExtOrgRoleMappingDto(extOrgRoleMappingDto);
		return res;
	}

	/**
	 * Method Name: getOrgResourcesList Method Description:This method is used
	 * to get the resources in an external organization.
	 * 
	 * @param idOrgEin
	 * @return ExtUserMgmntRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExtUserMgmntRes getOrgResourcesList(Long idOrgEin, boolean indRCCPUser) {
		ExtUserMgmntRes extUserMgmntRes = new ExtUserMgmntRes();
		/*
		 * Calling the dao implementation to get the get the list of resource
		 * which exists for the organization.
		 */
		ExtOrgRoleMappingDto extOrgRoleMappingDto = extUserOrgMappingDao.getOrgResourcesList(idOrgEin, indRCCPUser);
		/*
		 * setting the organization resources in the response and returning the
		 * response.
		 */
		extUserMgmntRes.setExtOrgRoleMappingDto(extOrgRoleMappingDto);
		return extUserMgmntRes;
	}

	/**
	 * Method Name: deleteOrgResources Method Description:This method is used to
	 * delete the resources from an external organization.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ExtUserMgmntRes deleteOrgResources(ExtUserMgmntReq extUserMgmntReq) {
		ExtUserMgmntRes extUserMgmntRes = new ExtUserMgmntRes();
		ExternalOrgDto externalOrgDto = extUserMgmntReq.getExtOrgRoleMappingDto().getOrgsList().get(0);
		Long idOrgDtl = externalOrgDto.getIdOrgDtl();
		List<Long> idResourcesList = new ArrayList<Long>();
		// Getting the resource id list which are to be deleted from the
		// organization.
		idResourcesList = externalOrgDto.getResources().stream()
				.filter(resource -> ServiceConstants.Y.equals(resource.getIndRsrcRemoval()))
				.map(ResourceDetailInDto::getIdResource).collect(Collectors.toList());
		// Calling the dao implementation to delete the resources from the org.
		extUserOrgMappingDao.deleteOrgResources(idOrgDtl, idResourcesList);
		return extUserMgmntRes;
	}

	/**
	 * Method Name: addOrganizationResource Method Description:This method is
	 * used to add resources to an external organization.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ExtUserMgmntRes addOrganizationResource(ExtUserMgmntReq extUserMgmntReq) {

		ExternalOrgDto externalOrgDto = extUserMgmntReq.getExtOrgRoleMappingDto().getOrgsList().get(0);
		Long idOrgDtl = externalOrgDto.getIdOrgDtl();
		List<Long> idResourcesList = new ArrayList<Long>();
		// Getting the id resource list which are to be saved to the
		// organization.
		idResourcesList = externalOrgDto.getResources().stream().map(ResourceDetailInDto::getIdResource)
				.collect(Collectors.toList());
		// Calling the dao implementation to add to the external organization.
		return extUserOrgMappingDao.addOrganizationResource(idOrgDtl, idResourcesList, extUserMgmntReq.getIdUser());

	}

	/**
	 * Method Name: saveAssignOrgRoleDtls Method Description:This method is used
	 * to save the Assign Org & Role details.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ExtUserMgmntRes saveAssignOrgRoleDtls(ExtUserMgmntReq extUserMgmntReq) {
		ExtUserMgmntRes extUserMgmntRes = null;
		ErrorDto errorDto = null;
		if (extUserMgmntReq.getExtOrgRoleMappingDto().getOrgsList().get(0).getUserRoles().contains(RCCP_USER_ROLE)) {
			errorDto = extUserOrgMappingDao.checkExtUserBgCheckClear(
					extUserMgmntReq.getExtOrgRoleMappingDto().getExtEmployeeDtl().getIdPerson(),
					extUserMgmntReq.getExtOrgRoleMappingDto().getOrgsList().get(0).getIdEin(),
					ASSIGN_ORG_ROLE_PAGE_NAME);
		}

		if (ObjectUtils.isEmpty(errorDto)) {
			// Calling the dao implementation to save the assign org & role
			// details.
			extUserMgmntRes = extUserOrgMappingDao.saveAssignOrgRoleDtls(extUserMgmntReq.getExtOrgRoleMappingDto(),
					extUserMgmntReq.getIdUser());
		} else {
			extUserMgmntRes = new ExtUserMgmntRes();
			extUserMgmntRes.setErrorDto(errorDto);
		}
		return extUserMgmntRes;
	}

	/**
	 * Method Name: getAssignOrgRoleDtls Method Description:This method is used
	 * to get the Assign Org & Role details.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExtUserMgmntRes getAssignOrgRoleDtls(ExtUserMgmntReq extUserMgmntReq) {
		ExtUserMgmntRes extUserMgmntRes = new ExtUserMgmntRes();
		// Calling the dao implementation to retrieve the assign org details.
		ExtOrgRoleMappingDto extOrgRoleMappingDto = extUserOrgMappingDao
				.getAssignOrgRoleDtls(extUserMgmntReq.getIdExtUserOrgLink());
		// setting the assign org role details in the response and returning the
		// response.
		extUserMgmntRes.setExtOrgRoleMappingDto(extOrgRoleMappingDto);
		return extUserMgmntRes;
	}

	/**
	 * Method Name: getExtUserOrgMappingHistory Method Description:This method
	 * is used to get the history record
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExtUserOrgMappingHistoryRes getExtUserOrgMappingHistory(ExtUserOrgMappingHistoryReq req) {
		List<ExtOrgRoleMappingHistoryDtl> historyRecords = extUserOrgMappingDao
				.getExtUserOrgMappingHistory(req.getIdExtUserOrgLink());
		ExtUserOrgMappingHistoryRes res = new ExtUserOrgMappingHistoryRes();
		res.setHistoryRecords(historyRecords);
		return res;
	}

	/**
	 * Method Name: saveExtUserRsrcLink Method Description: Service to Save the
	 * External User Resource Link. Only the newly added Resources to the user
	 * will be passed as resource list of ExternalOrgDto which are directly
	 * saved to DB.
	 * 
	 * @param orgUserLink
	 * @param idUser
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ExtUserMgmntRes saveExtUserRsrcLink(ExternalOrgDto orgUserLink, Long idUser) {
		ExtUserMgmntRes res = new ExtUserMgmntRes();
		res.setTotalRecCount(extUserOrgMappingDao.saveExtUserRsrcLink(orgUserLink, idUser));
		return res;
	}

	/**
	 * 
	 * Method Name: deleteExtUserOrgResourceLink Method Description:
	 * 
	 * @param req
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ServiceResHeaderDto deleteExtUserOrgResourceLink(ExtUserOrgResourceLinkDelReq req) {
		ServiceResHeaderDto res = null;
		if (!ObjectUtils.isEmpty(req.getIdPerson()) || !ObjectUtils.isEmpty(req.getIdExtUserOrgLink()))
			res = extUserOrgMappingDao.deleteExtUserOrgLinks(req.getIdPerson(), req.getIdExtUserOrgLink(), null);
		else
			res = extUserOrgMappingDao.deleteExtUserResRoles(req.getIdExtUserRsrcLink(), req.getIdOrgRoleDtl());
		return res;
	}

	/**
	 * Method Name: getExtUserRsrcDtls Method Description:This method is used to
	 * retrieve the external user resource association details.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExtUserMgmntRes getExtUserRsrcDtls(ExtUserMgmntReq extUserMgmntReq) {
		ExtUserMgmntRes extUserMgmntRes = new ExtUserMgmntRes();

		ExtOrgRoleMappingDto extOrgRoleMappingDto = extUserOrgMappingDao
				.getExtUserRsrcDtls(extUserMgmntReq.getIdExtUserOrgLink());

		extUserMgmntRes.setExtOrgRoleMappingDto(extOrgRoleMappingDto);
		return extUserMgmntRes;
	}

	/**
	 * Method Name: deleteExtUserRsrcAssociation Method Description:This method
	 * is used to delete the external user to resource association.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ExtUserMgmntRes deleteExtUserRsrcAssociation(ExtUserMgmntReq extUserMgmntReq) {
		ExtUserMgmntRes extUserMgmntRes = new ExtUserMgmntRes();
		extUserOrgMappingDao.deleteExtUserRsrcAssociation(extUserMgmntReq.getIdExtUserRsrcLink());

		return extUserMgmntRes;
	}

	/**
	 * Method Name: setExtrnlUserLoginAgrmntDtl Method Description: This method
	 * will interact with dao layer method to update external user agreement
	 * accept detail
	 * 
	 * @param CommonHelperReq
	 * @return CommonHelperRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CommonHelperRes setExtrnlUserLoginAgrmntDtl(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		extUserOrgMappingDao.setExtrnlUserLoginAgrmntDtl(commonHelperReq.getUserID());
		return commonHelperRes;

	}

	/**
	 * Method Name: checkExtUserBgCheckClear Method Description:This method will
	 * interact with the dao implementation to check if the background check for
	 * the user is cleared and active.
	 * 
	 * @param extUserMgmntReq
	 * @return ExtUserMgmntRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ExtUserMgmntRes checkExtUserBgCheckClear(ExtUserMgmntReq extUserMgmntReq) {
		ExtUserMgmntRes extUserMgmntRes = new ExtUserMgmntRes();
		/*
		 * Calling the dao implementation to check if the background check for
		 * the user is cleared and active.
		 */
		ErrorDto errorDto = extUserOrgMappingDao.checkExtUserBgCheckClear(extUserMgmntReq.getIdUser(),
				extUserMgmntReq.getIdOrgEin(), ORG_RSRC_ASSC_PAGE_NAME);
		if (!ObjectUtils.isEmpty(errorDto)) {
			extUserMgmntRes.setErrorDto(errorDto);
		}
		// returning the response from the service implementation.
		return extUserMgmntRes;
	}

}
