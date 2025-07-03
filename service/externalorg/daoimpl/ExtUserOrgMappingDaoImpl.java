/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Interface for External User Organization Role Mapping Data access Layer.
 *July 17, 2018- 3:33:39 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.daoimpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import oracle.jdbc.OracleTypes;
import us.tx.state.dfps.common.domain.ExtUserOrgLink;
import us.tx.state.dfps.common.domain.ExtUserRsrcLink;
import us.tx.state.dfps.common.domain.ExtrnlUser;
import us.tx.state.dfps.common.domain.OrgDtl;
import us.tx.state.dfps.common.domain.OrgRoleDtl;
import us.tx.state.dfps.common.domain.OrgRsrcLink;
import us.tx.state.dfps.common.domain.OrgType;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.externalorg.dto.ExtOrgRoleMappingDto;
import us.tx.state.dfps.common.externalorg.dto.ExtOrgRoleMappingHistoryDtl;
import us.tx.state.dfps.common.externalorg.dto.ExternalOrgDto;
import us.tx.state.dfps.common.externalorg.dto.ExtrnlEmployeeDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.ExtUserMgmntRes;
import us.tx.state.dfps.service.externalorg.dao.ExtUserOrgMappingDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@Repository
public class ExtUserOrgMappingDaoImpl implements ExtUserOrgMappingDao {

	public ExtUserOrgMappingDaoImpl() {
	}

	@Value("${ExtUserMgmnt.getOrgResourcesList}")
	private String getOrgResourcesListSql;

	@Value("${ExtUserMgmnt.getOrgDtls}")
	private String getOrgDtlsSql;

	@Value("${ExtUserMgmnt.getOrgTypesList}")
	private String getOrgTypesSql;

	@Value("${ExtUserMgmnt.deleteOrgResources}")
	private String deleteOrgResourcesSql;

	@Value("${ExtUserMgmnt.deleteUserRsrcLink}")
	private String deleteUserRsrcLinkSql;

	@Value("${ExtUserMgmnt.getExtOrgRsrcDtls}")
	private String getExtOrgRsrcDtlsSql;

	@Value("${ExtUserMgmnt.checkResourceExistsInOrg}")
	private String checkResourceExistsInOrgSql;

	@Value("${ExtUserMgmnt.facilityTypes}")
	private String facilityTypesSql;
	@Value("${ExtUserMgmnt.orderBy}")
	private String orderBySql;

	@Value("${ExtUserOrgMappingDaoImpl.getHistory}")
	private String getHistorySql;

	@Value("${ExtUserMgmnt.getExtUserRsrcList}")
	private String getExtUserRsrcListSql;

	@Value("${ExtUserMgmnt.getBgCheckAcctId}")
	private String getBgCheckAcctIdSql;

	@Value("${ExtUserMgmnt.getExternaluserId}")
	private String getExternaluserIdSql;

	@Value("${ExtUserMgmnt.checkExtUserBgCheckClear}")
	private String checkExtUserBgCheckClearSql;

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(ExtUserOrgMappingDaoImpl.class);

	@Autowired
	PersonDao personDao;
	public static final List<String> facilityTypesCodes = Arrays.asList("34", "54", "55", "35", "53", "33", "56", "36",
			"51", "73", "31", "72", "68", "60", "67", "80", "30", "14", "40", "38", "74", "41", "75", "58", "59", "39",
			"32", "52", "57", "37", "63", "64");

	/**
	 * Method Name: getExtUserOrgMappingList Method Description:This method is
	 * used to get the resources in an external organization.
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public ExtOrgRoleMappingDto getExtUserOrgMappingList(Long idPerson) {

		ExtOrgRoleMappingDto extOrgRoleMappingDto = new ExtOrgRoleMappingDto();

		ExtrnlEmployeeDto extEmployeeDtl = null;
		extEmployeeDtl = new ExtrnlEmployeeDto();
		PersonDto p = personDao.getPersonById(idPerson);
		extEmployeeDtl.setIdPerson(p.getIdPerson());
		extEmployeeDtl.setIdExtrnlEmpLogon(getExternaluserId(idPerson));
		extEmployeeDtl.setNmExtrnlEmpFirst(p.getNmPersonFirst());
		extEmployeeDtl.setNmExtrnlEmpLast(p.getNmPersonLast());
		extEmployeeDtl.setNmExtrnlEmpMiddle(p.getNmPersonMiddle());
		extEmployeeDtl.setNmExtrnlSuffix(p.getCdPersonSuffix());
		extOrgRoleMappingDto.setExtEmployeeDtl(extEmployeeDtl);

		List<ExtUserOrgLink> extUserOrgLink = (List<ExtUserOrgLink>) sessionFactory.getCurrentSession()
				.createCriteria(ExtUserOrgLink.class).add(Restrictions.eq("idPerson", idPerson)).list();
		List<ExternalOrgDto> orgsList = new ArrayList<>();
		if (!ObjectUtils.isEmpty(extUserOrgLink)) {
			extUserOrgLink.stream().forEach(link -> {
				ExternalOrgDto orgDto = new ExternalOrgDto();
				BeanUtils.copyProperties(link.getOrgDtl(), orgDto);
				if (!ObjectUtils.isEmpty(link.getOrgRoleDtls())) {
					List<String> userRoles = new ArrayList<>();
					link.getOrgRoleDtls().stream().forEach(role -> {
						userRoles.add(role.getCdUserRole());
					});
					orgDto.setUserRoles(userRoles);
				}
				orgDto.setIdExtUserOrgLink(link.getIdExtUserOrgLink());
				ExternalOrgDto dto = orgsList.stream()
						.filter(o -> o.getIdExtUserOrgLink().equals(orgDto.getIdExtUserOrgLink())).findAny()
						.orElse(null);
				if (ObjectUtils.isEmpty(dto))
					orgsList.add(orgDto);
			});
		}
		extOrgRoleMappingDto.setOrgsList(orgsList);
		return extOrgRoleMappingDto;

	}

	/**
	 * Method Name: getOrgResourcesList Method Description:This method is used
	 * to get the resources in an external organization.
	 * 
	 * @param idOrgDtl
	 * @return ExtOrgRoleMappingDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ExtOrgRoleMappingDto getOrgResourcesList(Long idOrgEin, boolean indRCCPRole) {
		ExtOrgRoleMappingDto extOrgRoleMappingDto = new ExtOrgRoleMappingDto();
		List<ExternalOrgDto> extOrgList = new ArrayList<ExternalOrgDto>();
		ExternalOrgDto externalOrgDto = new ExternalOrgDto();
		OrgDtl orgDtl = (OrgDtl) sessionFactory.getCurrentSession().createCriteria(OrgDtl.class)
				.add(Restrictions.eq("idEin", idOrgEin)).uniqueResult();

		if (!ObjectUtils.isEmpty(orgDtl)) {
			BeanUtils.copyProperties(orgDtl, externalOrgDto);
			if (!CollectionUtils.isEmpty(orgDtl.getOrgTypes())) {
				List<String> orgTypes = orgDtl.getOrgTypes().stream().map(OrgType::getCdType)
						.collect(Collectors.toList());
				externalOrgDto.setCdOrgTypes(orgTypes);
			}
		}

		// Call the '' stored proc to get the bg check accnt id
		Long idBgCheckAccntId = null;
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		try {
			Connection connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			CallableStatement callableStatement = connection.prepareCall(getBgCheckAcctIdSql);
			try {
				callableStatement.setInt(1, orgDtl.getIdEin().intValue());
				callableStatement.registerOutParameter(2, OracleTypes.NUMBER);
				callableStatement.registerOutParameter(3, OracleTypes.VARCHAR);
				callableStatement.execute();
				if (ObjectUtils.isEmpty(callableStatement.getString(3))) {
					idBgCheckAccntId = callableStatement.getLong(2);
				}

			} catch (Exception e) {
				log.debug("Exception occured while accessing the stored procedure " + e.getMessage());
			} finally {
				if (!ObjectUtils.isEmpty(callableStatement))
					callableStatement.close();
				if (!ObjectUtils.isEmpty(connection))
					connection.close();
			}
		} catch (SQLException e1) {
			log.debug("Exception occured while accessing the stored procedure " + e1.getMessage());
		}
		StringBuffer sqlQuery = new StringBuffer(getOrgResourcesListSql);

		if (indRCCPRole) {
			sqlQuery.append(" ").append(facilityTypesSql);
		}

		// sqlQuery.append(" ").append(orderBySql);
		// Get the resources for the organization
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery.toString());

		query.setResultTransformer(Transformers.aliasToBean(ResourceDetailInDto.class));
		query.addScalar("nmResource", StandardBasicTypes.STRING).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("rsrcAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("rsrcAddrCity", StandardBasicTypes.STRING)
				.addScalar("rsrcAddrCounty", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcType", StandardBasicTypes.STRING).addScalar("cdRsrcStatus", StandardBasicTypes.STRING)
				.addScalar("nbrFacilPhone", StandardBasicTypes.STRING)
				.addScalar("nbrFacilPhoneExt", StandardBasicTypes.STRING)
				.addScalar("rsrcEmailAddress", StandardBasicTypes.STRING).setParameter("idOrgEin", idOrgEin);
		if (indRCCPRole) {
			query.setParameterList("facilityTypeCodes", facilityTypesCodes);
		}
		List<ResourceDetailInDto> resourceList = query.list();
		if (!ObjectUtils.isEmpty(externalOrgDto)) {
			externalOrgDto.setResources(resourceList);
			externalOrgDto.setIdBgCheckAccntId(idBgCheckAccntId);
			extOrgList.add(externalOrgDto);
		}

		extOrgRoleMappingDto.setOrgsList(extOrgList);
		return extOrgRoleMappingDto;
	}

	/**
	 * Method Name: deleteOrgResources Method Description:This method is used to
	 * delete the resources from an external organization.
	 * 
	 * @param idOrgDtl
	 * @param idResourcesList
	 */
	@Override
	public void deleteOrgResources(Long idOrgDtl, List<Long> idResourcesList) {

		// Deleting the record from the ORG_RSRC_LINK table
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteOrgResourcesSql);
		sqlQuery.setParameter("idOrgDtl", idOrgDtl).setParameterList("resourceList", idResourcesList);
		sqlQuery.executeUpdate();

		// Deleting from the EXT_USER_RSRC_LINK table.
		SQLQuery sqlQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteUserRsrcLinkSql);
		sqlQuery1.setParameterList("resourceList", idResourcesList);
		sqlQuery1.executeUpdate();
	}

	/**
	 * Method Name: addOrganizationResource Method Description:This method is
	 * used to add resources to an external organization.
	 * 
	 * @param idOrgDtl
	 * @param idResourcesList
	 * @param idUser
	 * @return ExtUserMgmntRes
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ExtUserMgmntRes addOrganizationResource(Long idOrgDtl, List<Long> idResourcesList, Long idUser) {

		/*
		 * Calling the method to filter out the resources from which are already
		 * existing in other/same organization.
		 */
		Map<String, Object> returnMap = checkResourceExists(idResourcesList);
		ExtUserMgmntRes extUserMgmntRes = new ExtUserMgmntRes();
		List<Long> resourcesToBeAddedList = (List<Long>) returnMap.get("resourcesNotInOtherOrg");
		/*
		 * Iterating over the resource ids which are not present and creating
		 * the entity which has to be persisted.
		 */
		if (!CollectionUtils.isEmpty(resourcesToBeAddedList)) {
			resourcesToBeAddedList.forEach(idResource -> {
				// Setting the values in the entity to be saved
				OrgRsrcLink orgRsrcLink = new OrgRsrcLink();
				OrgDtl orgDtl = (OrgDtl) sessionFactory.getCurrentSession().get(OrgDtl.class, idOrgDtl);
				orgRsrcLink.setOrgDtl(orgDtl);
				orgRsrcLink.setIdResource(idResource);
				orgRsrcLink.setIdCreatedPerson(idUser);
				orgRsrcLink.setIdLastUpdatePerson(idUser);
				orgRsrcLink.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
				orgRsrcLink.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
				// saving the entity to create a link between resource and org.
				sessionFactory.getCurrentSession().save(orgRsrcLink);
			});

		}
		/*
		 * Setting the error for resources which were already present and the
		 * user tried to add those as new resources.
		 */
		extUserMgmntRes.setErrorList((List<String>) returnMap.get("errorList"));

		return extUserMgmntRes;

	}

	/**
	 * Method Name: saveAssignOrgRoleDtls Method Description:This method is used
	 * to save the Assign Org & Role details.
	 * 
	 * @param extOrgRoleMappingDto
	 * @return ExtUserMgmntRes
	 */
	@Override
	public ExtUserMgmntRes saveAssignOrgRoleDtls(ExtOrgRoleMappingDto extOrgRoleMappingDto, Long idUser) {
		ExtUserMgmntRes extUserMgmntRes = new ExtUserMgmntRes();
		Long idExtUserOrgLink = extOrgRoleMappingDto.getExtEmployeeDtl().getIdExtUserOrgLink();
		// Get the existing org user link record
		if (!ObjectUtils.isEmpty(idExtUserOrgLink)) {
			// call the update method
			extUserMgmntRes = updateAssignOrgRoleDetails(extOrgRoleMappingDto, idUser);

		} else {
			// call the save method
			extUserMgmntRes = addAssignOrgRoleDetails(extOrgRoleMappingDto, idUser);
		}

		return extUserMgmntRes;
	}

	/**
	 * Method Name: getAssignOrgRoleDtls Method Description:This method is used
	 * to get the Assign Org & Role details.
	 * 
	 * @param idExtUserOrgLink
	 * @return ExtOrgRoleMappingDto
	 */
	@Override
	public ExtOrgRoleMappingDto getAssignOrgRoleDtls(Long idExtUserOrgLink) {
		ExtUserOrgLink extUserOrgLink = new ExtUserOrgLink();
		ExtOrgRoleMappingDto extOrgRoleMappingDto = new ExtOrgRoleMappingDto();
		ExtrnlEmployeeDto extrnlEmployeeDto = new ExtrnlEmployeeDto();
		ExternalOrgDto externalOrgDto = new ExternalOrgDto();
		List<ExternalOrgDto> orgList = new ArrayList<ExternalOrgDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExtUserOrgLink.class)
				.add(Restrictions.eq("idExtUserOrgLink", idExtUserOrgLink));
		extUserOrgLink = (ExtUserOrgLink) criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();

		if (!ObjectUtils.isEmpty(extUserOrgLink)) {
			externalOrgDto.setIdOrgDtl(extUserOrgLink.getOrgDtl().getIdOrgDtl());
			externalOrgDto.setIdEin(extUserOrgLink.getOrgDtl().getIdEin());
			externalOrgDto.setNmLegal(extUserOrgLink.getOrgDtl().getNmLegal());
			externalOrgDto.setNmOther(extUserOrgLink.getOrgDtl().getNmOthr());
			if (!CollectionUtils.isEmpty(extUserOrgLink.getOrgRoleDtls())) {
				List<String> userRoles = extUserOrgLink.getOrgRoleDtls().stream().map(OrgRoleDtl::getCdUserRole)
						.collect(Collectors.toList());
				externalOrgDto.setUserRoles(userRoles);
			}
			orgList.add(externalOrgDto);
			extOrgRoleMappingDto.setOrgsList(orgList);

			extrnlEmployeeDto.setIdPerson(extUserOrgLink.getIdPerson());
			extrnlEmployeeDto.setIdExtUserOrgLink(extUserOrgLink.getIdExtUserOrgLink());
			PersonDto p = personDao.getPersonById(extUserOrgLink.getIdPerson());
			extrnlEmployeeDto.setIdPerson(p.getIdPerson());
			extrnlEmployeeDto.setIdExtrnlEmpLogon(getExternaluserId(extUserOrgLink.getIdPerson()));
			extrnlEmployeeDto.setNmExtrnlEmpFirst(p.getNmPersonFirst());
			extrnlEmployeeDto.setNmExtrnlEmpLast(p.getNmPersonLast());
			extrnlEmployeeDto.setNmExtrnlEmpMiddle(p.getNmPersonMiddle());
			extrnlEmployeeDto.setNmExtrnlSuffix(p.getCdPersonSuffix());
			extOrgRoleMappingDto.setExtEmployeeDtl(extrnlEmployeeDto);
		}
		return extOrgRoleMappingDto;
	}

	/**
	 * Method Name: checkResourceExists Method Description:This method is used
	 * to filter out the resources which are to be added and create error list
	 * for which already exists and was tried to be added.
	 * 
	 * @param idResourcesList
	 * @return returnMap
	 */
	@SuppressWarnings("unchecked")

	private Map<String, Object> checkResourceExists(List<Long> idResourcesList) {
		Map<String, Object> returnMap = new HashMap<String, Object>();

		List<String> errorList = new ArrayList<String>();
		// get the resources already exists in the db from the ones which the
		// user
		// wanted to add.
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getExtOrgRsrcDtlsSql)
				.addScalar("nmLegal").addScalar("nmResource").addScalar("idResource", StandardBasicTypes.LONG)
				.setParameterList("resourceList", idResourcesList)
				.setResultTransformer(Transformers.aliasToBean(ResourceDetailInDto.class));
		List<Long> resourcesNotInOtherOrg = new ArrayList<Long>();

		List<ResourceDetailInDto> resourceList = query.list();
		// Forming the list of error messages
		if (!CollectionUtils.isEmpty(resourceList)) {
			for (ResourceDetailInDto resourceDetailInDto : resourceList) {
				String errorMsg = "Resource " + resourceDetailInDto.getNmResource()
						+ " already exists in the organization " + resourceDetailInDto.getNmLegal();
				errorList.add(errorMsg);

			}
			/*
			 * filtering out the resources which can be added from the complete
			 * list of resources which the user wanted to add.
			 */
			idResourcesList.forEach(resource -> {
				ResourceDetailInDto resourceDetailInDto = resourceList.stream()
						.filter(r -> r.getIdResource().equals(resource)).findAny().orElse(null);
				if (ObjectUtils.isEmpty(resourceDetailInDto)) {

					resourcesNotInOtherOrg.add(resource);
				}
			});
		} else {
			resourcesNotInOtherOrg.addAll(idResourcesList);
		}
		// returning the filtered resource ids list and list of error messages.
		returnMap.put("errorList", errorList);
		returnMap.put("resourcesNotInOtherOrg", resourcesNotInOtherOrg);
		return returnMap;

	}

	/**
	 * Method Name: updateAssignOrgRoleDetails Method Description:This method is
	 * used to update the assign org & role details.
	 * 
	 * @param extOrgRoleMappingDto
	 * @param idUser
	 * @return extUserMgmntRes
	 */
	private ExtUserMgmntRes updateAssignOrgRoleDetails(ExtOrgRoleMappingDto extOrgRoleMappingDto, Long idUser) {
		ExtUserMgmntRes extUserMgmntRes = new ExtUserMgmntRes();
		Set<OrgRoleDtl> set = new HashSet<OrgRoleDtl>();
		ExternalOrgDto externalOrgDto = extOrgRoleMappingDto.getOrgsList().get(0);
		List<String> userRoles = externalOrgDto.getUserRoles();
		Long idExtUserOrgLink = extOrgRoleMappingDto.getExtEmployeeDtl().getIdExtUserOrgLink();
		ExtUserOrgLink extUserOrgLink = (ExtUserOrgLink) sessionFactory.getCurrentSession().get(ExtUserOrgLink.class,
				idExtUserOrgLink);
		extUserOrgLink.getOrgRoleDtls().forEach(userRole -> {
			OrgRoleDtl orgRoleDtl = new OrgRoleDtl();
			BeanUtils.copyProperties(userRole, orgRoleDtl);
			set.add(orgRoleDtl);
		});

		if (CollectionUtils.isEmpty(extUserOrgLink.getOrgRoleDtls())) {
			extUserOrgLink.getOrgRoleDtls().addAll(new HashSet<>());
		}

		if (!ObjectUtils.isEmpty(extUserOrgLink)) {
			// Check if the organization from the front end is the same as in
			// the db
			extUserOrgLink.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			extUserOrgLink.setIdLastUpdatePerson(idUser);
			OrgDtl orgDtl = (OrgDtl) sessionFactory.getCurrentSession().createCriteria(OrgDtl.class)
					.add(Restrictions.eq("idEin", externalOrgDto.getIdEin())).uniqueResult();

			extUserOrgLink.setIdPerson(extOrgRoleMappingDto.getExtEmployeeDtl().getIdPerson());
			extUserOrgLink.setOrgDtl(orgDtl);

			if (CollectionUtils.isEmpty(extUserOrgLink.getOrgRoleDtls())) {
				createOrgRoleDetails(userRoles, idUser, extUserOrgLink);
			} else {
				// check against the one's already in the db and the one's
				// coming from the list
				for (OrgRoleDtl orgRoleDtl : set) {
					String cdUserRole = userRoles.stream()
							.filter(userRole -> userRole.equals(orgRoleDtl.getCdUserRole())).findAny().orElse(null);
					if (ObjectUtils.isEmpty(cdUserRole)) {
						OrgRoleDtl orgRoleDtl1 = extUserOrgLink.getOrgRoleDtls().stream()
								.filter(org -> org.getIdOrgRoleDtl().equals(orgRoleDtl.getIdOrgRoleDtl())).findAny()
								.orElse(null);
						extUserOrgLink.getOrgRoleDtls().remove(orgRoleDtl1);
					}
				}
				// Check for values which are newly added from the front end and
				// not present in
				// the db.
				for (String cdUserRole : userRoles) {
					OrgRoleDtl existingOrgRoleDtl = extUserOrgLink.getOrgRoleDtls().stream()
							.filter(orgRole -> orgRole.getCdUserRole().equals(cdUserRole)).findAny().orElse(null);
					if (ObjectUtils.isEmpty(existingOrgRoleDtl)) {
						OrgRoleDtl orgRoleDtl = new OrgRoleDtl();
						orgRoleDtl.setExtUserOrgLink(extUserOrgLink);
						orgRoleDtl.setCdUserRole(cdUserRole);

						orgRoleDtl.setDtLastUpdate(
								Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

						orgRoleDtl.setIdCreatedPerson(idUser);
						orgRoleDtl.setDtCreated(
								Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

						orgRoleDtl.setIdLastUpdatePerson(idUser);
						extUserOrgLink.getOrgRoleDtls().add(orgRoleDtl);
					}
				}
			}

			sessionFactory.getCurrentSession().update(extUserOrgLink);
		}
		extUserMgmntRes.setIdExtOrgPrsnLink(extUserOrgLink.getIdExtUserOrgLink());
		return extUserMgmntRes;
	}

	/**
	 * Method Name: addAssignOrgRoleDetails Method Description:This method is
	 * used to create a new record for the assign org & role details.
	 * 
	 * @param extOrgRoleMappingDto
	 * @param idUser
	 * @return extUserMgmntRes
	 */
	private ExtUserMgmntRes addAssignOrgRoleDetails(ExtOrgRoleMappingDto extOrgRoleMappingDto, Long idUser) {
		ExtUserMgmntRes extUserMgmntRes = new ExtUserMgmntRes();
		ExtUserOrgLink extUserOrgLink = null;
		ExternalOrgDto externalOrgDto = extOrgRoleMappingDto.getOrgsList().get(0);
		List<String> userRoles = externalOrgDto.getUserRoles();
		Long idExtUserOrgLink = extOrgRoleMappingDto.getExtEmployeeDtl().getIdExtUserOrgLink();

		OrgDtl orgDtl = (OrgDtl) sessionFactory.getCurrentSession().createCriteria(OrgDtl.class)
				.add(Restrictions.eq("idEin", externalOrgDto.getIdEin())).uniqueResult();

		// Check if the person exists already in the current organization
		extUserOrgLink = (ExtUserOrgLink) sessionFactory.getCurrentSession().createCriteria(ExtUserOrgLink.class)
				.add(Restrictions.eq("idPerson", extOrgRoleMappingDto.getExtEmployeeDtl().getIdPerson()))
				.add(Restrictions.eq("orgDtl.idOrgDtl", orgDtl.getIdOrgDtl())).uniqueResult();
		if (!ObjectUtils.isEmpty(extUserOrgLink)) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorMsg("Person already exists in the Organization");
			extUserMgmntRes.setErrorDto(errorDto);
		} else {
			extUserOrgLink = new ExtUserOrgLink();
			extUserOrgLink.setIdPerson(extOrgRoleMappingDto.getExtEmployeeDtl().getIdPerson());
			extUserOrgLink.setOrgDtl(orgDtl);
			if (ObjectUtils.isEmpty(idExtUserOrgLink)) {
				extUserOrgLink.setIdCreatedPerson(idUser);
				extUserOrgLink.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

			}
			extUserOrgLink.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			extUserOrgLink.setIdLastUpdatePerson(idUser);

			extUserOrgLink.setOrgRoleDtls(new HashSet<>());

			createOrgRoleDetails(userRoles, idUser, extUserOrgLink);
			if (ObjectUtils.isEmpty(idExtUserOrgLink)) {
				idExtUserOrgLink = (Long) sessionFactory.getCurrentSession().save(extUserOrgLink);
			}
			extUserMgmntRes.setIdExtOrgPrsnLink(idExtUserOrgLink);

		}
		return extUserMgmntRes;
	}

	/**
	 * Method Name: createOrgRoleDetails Method Description:This method is used
	 * to create the Set of OrgRoleDtl entity for the assign org & role details
	 * entity.
	 * 
	 * @param cdOrgTypes
	 * @param idUser
	 * @param extUserOrgLink
	 */
	private void createOrgRoleDetails(List<String> userRoles, Long idUser, ExtUserOrgLink extUserOrgLink) {
		HashSet<OrgRoleDtl> orgRoleList = new HashSet<OrgRoleDtl>();
		if (!CollectionUtils.isEmpty(userRoles)) {
			for (String userRole : userRoles) {
				OrgRoleDtl orgRoleDtl = new OrgRoleDtl();
				orgRoleDtl.setExtUserOrgLink(extUserOrgLink);
				orgRoleDtl.setCdUserRole(userRole);

				orgRoleDtl.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

				orgRoleDtl.setIdCreatedPerson(idUser);
				orgRoleDtl.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

				orgRoleDtl.setIdLastUpdatePerson(idUser);

				orgRoleList.add(orgRoleDtl);

			}
			extUserOrgLink.getOrgRoleDtls().addAll(orgRoleList);
		}

	}

	/**
	 * Method Name: getHistoryOrgLink Method Description: This method used to
	 * get the history records associated to idExtUserOrgLink
	 * 
	 * @param idExtUserOrgLink
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExtOrgRoleMappingHistoryDtl> getExtUserOrgMappingHistory(Long idExtUserOrgLink) {
		return (List<ExtOrgRoleMappingHistoryDtl>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getHistorySql)
				.setResultTransformer(Transformers.aliasToBean(ExtOrgRoleMappingHistoryDtl.class)))
						.addScalar("idEinHistory", StandardBasicTypes.LONG).addScalar("nmLegalHistory")
						.addScalar("cdOperatio").addScalar("dtLastUpdated", StandardBasicTypes.TIMESTAMP)
						.addScalar("nmLastUpdatedby").addScalar("idLastUpdated", StandardBasicTypes.LONG)
						.setParameter("idExtUserOrgLink", idExtUserOrgLink).list();
	}

	/**
	 * 
	 * Method Name: deleteExtUserOrgLinks Method Description:
	 * 
	 * @param idPerson
	 * @param idExtUserOrgLink
	 * @return
	 */
	@Override
	public ServiceResHeaderDto deleteExtUserOrgLinks(Long idPerson, Long idExtUserOrgLink, OrgDtl orgDtl) {
		Criteria delCriteria = sessionFactory.getCurrentSession().createCriteria(ExtUserOrgLink.class);

		if (!ObjectUtils.isEmpty(idPerson))
			delCriteria.add(Restrictions.eq("idPerson", idPerson));

		if (!ObjectUtils.isEmpty(idExtUserOrgLink))
			delCriteria.add(Restrictions.eq("idExtUserOrgLink", idExtUserOrgLink));

		if (!ObjectUtils.isEmpty(orgDtl))
			delCriteria.add(Restrictions.eq("orgDtl", orgDtl));

		List<ExtUserOrgLink> delEntities = (List<ExtUserOrgLink>) delCriteria.list();

		delEntities.stream().forEach(entity -> sessionFactory.getCurrentSession().delete(entity));
		return new ServiceResHeaderDto();
	}

	/**
	 * 
	 * Method Name: deleteExtUserResRoles Method Description:
	 * 
	 * @param idExtUserRsrcLink
	 * @param idOrgRoleDtl
	 * @return
	 */
	@Override
	public ServiceResHeaderDto deleteExtUserResRoles(Long idExtUserRsrcLink, Long idOrgRoleDtl) {

		if (!ObjectUtils.isEmpty(idExtUserRsrcLink)) {
			ExtUserRsrcLink extUserRsrcLink = (ExtUserRsrcLink) sessionFactory.getCurrentSession()
					.createCriteria(ExtUserRsrcLink.class).add(Restrictions.eq("idExtUserRsrcLink", idExtUserRsrcLink))
					.uniqueResult();
			sessionFactory.getCurrentSession().delete(extUserRsrcLink);
		}
		if (!ObjectUtils.isEmpty(idOrgRoleDtl)) {
			OrgRoleDtl orgRoleDtl = (OrgRoleDtl) sessionFactory.getCurrentSession().createCriteria(OrgRoleDtl.class)
					.add(Restrictions.eq("idOrgRoleDtl", idOrgRoleDtl)).uniqueResult();
			sessionFactory.getCurrentSession().delete(orgRoleDtl);
		}

		return new ServiceResHeaderDto();
	}

	/**
	 * Method Name: saveExtUserRsrcLink Method Description: Dao method to Save
	 * the ExternalUserResourceLink records.
	 * 
	 * @param orgUserLink
	 * @param idUser
	 * @return addedRowCount
	 */
	@Override
	public Long saveExtUserRsrcLink(ExternalOrgDto orgUserLink, Long idUser) {
		// Save the list of External User and Resource Link.
		List<Long> addedResourcesList = new ArrayList<>();
		ExtUserOrgLink extUsrOrgLnk = (ExtUserOrgLink) sessionFactory.getCurrentSession().get(ExtUserOrgLink.class,
				orgUserLink.getIdExtUserOrgLink());
		if (!ObjectUtils.isEmpty(orgUserLink.getResources())) {
			orgUserLink.getResources().forEach(resource -> {
				ExtUserRsrcLink usrRsrcLink = new ExtUserRsrcLink();
				usrRsrcLink.setDtCreated(new Date());
				usrRsrcLink.setDtLastUpdate(new Date());
				usrRsrcLink.setIdCreatedPerson(idUser);
				usrRsrcLink.setIdLastUpdatePerson(idUser);
				usrRsrcLink.setExtUserOrgLink(extUsrOrgLnk);
				usrRsrcLink.setIdResource(resource.getIdResource());
				Long idlink = (Long) sessionFactory.getCurrentSession().save(usrRsrcLink);
				if (!StringUtils.isEmpty(idlink) && !ServiceConstants.ZERO.equals(idlink)) {
					addedResourcesList.add(resource.getIdResource());
				}
			});
		}
		return !ObjectUtils.isEmpty(addedResourcesList) ? addedResourcesList.size() : ServiceConstants.ZERO;
	}

	/**
	 * Method Name: getExtUserRsrcDtls Method Description:This method is used to
	 * retrieve the external user resource association details.
	 * 
	 * @param idExtUserOrgLink
	 * @return ExtOrgRoleMappingDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ExtOrgRoleMappingDto getExtUserRsrcDtls(Long idExtUserOrgLink) {

		ExtOrgRoleMappingDto extOrgRoleMappingDto = getAssignOrgRoleDtls(idExtUserOrgLink);
		// Get the resources for the organization
		List<ResourceDetailInDto> resourceList = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getExtUserRsrcListSql)
				.setResultTransformer(Transformers.aliasToBean(ResourceDetailInDto.class)))
						.addScalar("nmResource", StandardBasicTypes.STRING)
						.addScalar("idResource", StandardBasicTypes.LONG)
						.addScalar("idExtUserRsrcLink", StandardBasicTypes.LONG)
						.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
						.addScalar("cdRsrcStatus", StandardBasicTypes.STRING)
						.addScalar("cdRsrcType", StandardBasicTypes.STRING)
						.setParameter("idUserOrgLink", idExtUserOrgLink).list();
		extOrgRoleMappingDto.getOrgsList().get(0).setResources(resourceList);
		return extOrgRoleMappingDto;
	}

	/**
	 * Method Name: deleteExtUserRsrcAssociation Method Description:This method
	 * is used to delete the external user to resource association.
	 * 
	 * @param idExtUserRsrcLink
	 */
	@Override
	public void deleteExtUserRsrcAssociation(Long idExtUserRsrcLink) {
		ExtUserRsrcLink extUserRsrcLink = (ExtUserRsrcLink) sessionFactory.getCurrentSession()
				.load(ExtUserRsrcLink.class, idExtUserRsrcLink);

		if (!ObjectUtils.isEmpty(extUserRsrcLink)) {
			sessionFactory.getCurrentSession().delete(extUserRsrcLink);
		}
	}

	/**
	 * 
	 * Method Name: getExternaluserId, this method is to get the User id Method
	 * Description:
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public String getExternaluserId(Long idPerson) {

		return (String) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getExternaluserIdSql))
				.addScalar("idExtrnlEmpLogon").setParameter("idPerson", idPerson).uniqueResult();

	}

	/**
	 * Method Name: setExtrnlUserLoginAgrmntDtl Method Description:This method
	 * will update agreement accept related column when user first time login
	 * and clicks on Agree button over agreement consent page
	 * 
	 * @param idExtrnlUser
	 */
	public CommonHelperRes setExtrnlUserLoginAgrmntDtl(Long idExtrnlUser) {
		// Fetch entity for external login user
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		ExtrnlUser extrnlUser = (ExtrnlUser) sessionFactory.getCurrentSession().createCriteria(ExtrnlUser.class)
				.add(Restrictions.eq("idPerson", idExtrnlUser)).uniqueResult();
		if (!ObjectUtils.isEmpty(extrnlUser)) {
			// Update agreement accept indicator to Y
			extrnlUser.setIndExtrnlUserConsent(ServiceConstants.UNIT_IND_EXTERNAL_Y);
			// Update agreement accept date with today's date
			extrnlUser.setDtConsent(new Date());
			sessionFactory.getCurrentSession().update(extrnlUser);
			commonHelperRes.setResult(ServiceConstants.TRUE_VALUE);
		}
		return commonHelperRes;
	}

	/**
	 * Method Name: checkExtUserBgCheckClear Method Description: Method Name:
	 * checkExtUserBgCheckClear Method Description:This method will check if the
	 * background check for the user is cleared and active.
	 * 
	 * @param idUser
	 * @param idOrgEin
	 * @param orgRsrcAsscPageName
	 * @return ErrorDto
	 */
	@Override
	public ErrorDto checkExtUserBgCheckClear(Long idUser, Long idOrgEin, String orgRsrcAsscPageName) {
		ErrorDto errorDto = null;
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();

		try {
			Connection connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			CallableStatement callableStatement = connection.prepareCall(checkExtUserBgCheckClearSql);
			try {
				callableStatement.setInt(1, idUser.intValue());
				callableStatement.setInt(2, idOrgEin.intValue());
				callableStatement.setString(3, orgRsrcAsscPageName);
				callableStatement.registerOutParameter(1, OracleTypes.NUMBER);
				callableStatement.registerOutParameter(2, OracleTypes.NUMBER);
				callableStatement.registerOutParameter(3, OracleTypes.VARCHAR);
				callableStatement.registerOutParameter(4, OracleTypes.VARCHAR);
				callableStatement.registerOutParameter(5, OracleTypes.VARCHAR);
				callableStatement.execute();

				if (ServiceConstants.N.equals(callableStatement.getString(4))) {
					errorDto = new ErrorDto();
					errorDto.setErrorMsg(callableStatement.getString(5));
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.debug("Exception occured while accessing the stored procedure " + e.getMessage());
			} finally {
				if (!ObjectUtils.isEmpty(callableStatement))
					callableStatement.close();
				if (!ObjectUtils.isEmpty(connection))
					connection.close();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			log.debug("Exception occured while accessing the stored procedure " + e1.getMessage());
		}
		return errorDto;
	}

}
