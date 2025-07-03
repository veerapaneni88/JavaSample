/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:This class is used for External Organization Detail
 *Jul 13, 2018- 2:34:55 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ExtUserOrgLink;
import us.tx.state.dfps.common.domain.OrgAddr;
import us.tx.state.dfps.common.domain.OrgDtl;
import us.tx.state.dfps.common.domain.OrgDtlStatTrckng;
import us.tx.state.dfps.common.domain.OrgEmailDtl;
import us.tx.state.dfps.common.domain.OrgIdentifierDtl;
import us.tx.state.dfps.common.domain.OrgPhoneDtl;
import us.tx.state.dfps.common.domain.OrgType;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.externalorg.dto.ExtOrgRoleMappingDto;
import us.tx.state.dfps.common.externalorg.dto.ExternalOrgDto;
import us.tx.state.dfps.common.externalorg.dto.OrganizationAddressDto;
import us.tx.state.dfps.common.externalorg.dto.OrganizationDetailDto;
import us.tx.state.dfps.common.externalorg.dto.OrganizationEmailDtlDto;
import us.tx.state.dfps.common.externalorg.dto.OrganizationIdentifierDtlDto;
import us.tx.state.dfps.common.externalorg.dto.OrganizationPhoneDtlDto;
import us.tx.state.dfps.common.externalorg.dto.OrganizationStatusHistoryDto;
import us.tx.state.dfps.common.externalorg.dto.OrganizationTypeDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.externalorg.dao.ExtUserOrgMappingDao;
import us.tx.state.dfps.service.externalorg.dao.ExternalOrganizationDao;

@Repository
public class ExternalOrganizationDaoImpl implements ExternalOrganizationDao {

	private static final String NM_LEGAL = "nmLegal";
	private static final String ID_ORG_DTL = "idOrgDtl";
	private static final String FAILURE = "failure";
	private static final String SUCCESS = "success";
	private static final String EMAIL = "E";
	private static final String PHONE = "P";
	private static final String IDENTIFIER = "I";
	private static final Logger log = Logger.getLogger(ExternalOrganizationDaoImpl.class);
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	ExtUserOrgMappingDao extUserOrgMappingDao;

	@Value("${ExtOrgDetail.getOrgDetailByNmLegal}")
	private String getOrgDetailSql;

	@Value("${ExtOrgDetail.getOrgContractCount}")
	private String getOrgContractCountSql;

	/**
	 * 
	 * Method Name: fetchExternalOrganization Method Description: This method is
	 * used to fetch the organization detail based on idEin
	 * 
	 * @param idEin
	 * @return OrganizationDetailDto
	 */
	@Override
	public OrganizationDetailDto fetchExternalOrganization(Long idEin) {
		log.info("fetchExternalOrganization Method in ExternalOrganizationDaoImpl : Execution Started.");
		OrganizationDetailDto organizationDetailDto = new OrganizationDetailDto();
		// fetching the Distinct Entity based on given EIN id.
		OrgDtl orgDtl = (OrgDtl) sessionFactory.getCurrentSession().createCriteria(OrgDtl.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).add(Restrictions.eq("idEin", idEin))
				.uniqueResult();
		// Checking if the Entity is empty or not.
		if (!ObjectUtils.isEmpty(orgDtl)) {
			// Copying the property from entity to Dto for Org Detail.
			BeanUtils.copyProperties(orgDtl, organizationDetailDto);
			// Checking if the org Address detail is empty or not.
			if (!ObjectUtils.isEmpty(orgDtl.getOrgAddrs())) {
				// Creating the list of the org Address DTO's to populate from
				// the Entity.
				List<OrganizationAddressDto> orgAddrList = new ArrayList<>();
				orgDtl.getOrgAddrs().stream().forEach(addr -> {
					OrganizationAddressDto addrDto = new OrganizationAddressDto();
					// Copying the property from entity to Dto for address.
					BeanUtils.copyProperties(addr, addrDto);
					orgAddrList.add(addrDto);
				});
				organizationDetailDto.setListOrganizationAddressDto(orgAddrList);
			}
			// Checking if the org type detail is empty or not.
			if (!ObjectUtils.isEmpty(orgDtl.getOrgTypes())) {
				// Creating the list of the org type DTO's to populate from the
				// Entity.
				List<OrganizationTypeDto> orgTypeList = new ArrayList<>();
				orgDtl.getOrgTypes().stream().forEach(orgType -> {
					OrganizationTypeDto orgTypeDto = new OrganizationTypeDto();
					// Copying the property from entity to Dto for org type.
					BeanUtils.copyProperties(orgType, orgTypeDto);
					orgTypeList.add(orgTypeDto);
				});
				organizationDetailDto.setListOrganizationTypeDto(orgTypeList);
			}
			// Checking if the org identifier detail is empty or not.
			if (!ObjectUtils.isEmpty(orgDtl.getOrgIdentifierDtls())) {
				// Creating the list of the org type DTO's to populate from the
				// Entity.
				List<OrganizationIdentifierDtlDto> orgIdentifierList = new ArrayList<>();
				orgDtl.getOrgIdentifierDtls().stream().forEach(orgIdentifierDtl -> {
					OrganizationIdentifierDtlDto orgIdentifierDtlDto = new OrganizationIdentifierDtlDto();
					// Copying the property from entity to Dto for identifier.
					BeanUtils.copyProperties(orgIdentifierDtl, orgIdentifierDtlDto);
					orgIdentifierList.add(orgIdentifierDtlDto);
				});
				organizationDetailDto.setListOrganizationIdentifierDtlDto(orgIdentifierList);
			}
			// Checking if the org phone detail is empty or not.
			if (!ObjectUtils.isEmpty(orgDtl.getOrgPhoneDtls())) {
				// Creating the list of the org phone DTO's to populate from the
				// Entity.
				List<OrganizationPhoneDtlDto> orgPhoneDtlList = new ArrayList<>();
				orgDtl.getOrgPhoneDtls().stream().forEach(orgPhoneDtl -> {
					OrganizationPhoneDtlDto orgPhoneDtlDto = new OrganizationPhoneDtlDto();
					// Copying the property from entity to Dto for phone.
					BeanUtils.copyProperties(orgPhoneDtl, orgPhoneDtlDto);
					orgPhoneDtlList.add(orgPhoneDtlDto);
				});
				organizationDetailDto.setListOrganizationPhoneDtlDto(orgPhoneDtlList);
			}
			// Checking if the org phone detail is empty or not.
			if (!ObjectUtils.isEmpty(orgDtl.getOrgEmailDtls())) {
				// Creating the list of the org email DTO's to populate from the
				// Entity.
				List<OrganizationEmailDtlDto> orgEmailDtlList = new ArrayList<>();
				orgDtl.getOrgEmailDtls().stream().forEach(orgEmailDtl -> {
					OrganizationEmailDtlDto orgEmailDtlDto = new OrganizationEmailDtlDto();
					// Copying the property from entity to Dto for email.
					BeanUtils.copyProperties(orgEmailDtl, orgEmailDtlDto);
					orgEmailDtlList.add(orgEmailDtlDto);
				});
				organizationDetailDto.setListOrganizationEmailDtlDto(orgEmailDtlList);
			}
			// Checking if the org status history phone detail is empty or not.
			if (!ObjectUtils.isEmpty(orgDtl.getOrgDtlStatHists())) {
				// Creating the list of the org status history DTO's to populate
				// from the Entity.
				List<OrganizationStatusHistoryDto> orgStatusHistoryList = new ArrayList<>();
				orgDtl.getOrgDtlStatHists().stream().forEach(orgDtlStatHist -> {
					OrganizationStatusHistoryDto orgStatusHistoryDto = new OrganizationStatusHistoryDto();
					// Copying the property from entity to Dto for status
					// history.
					BeanUtils.copyProperties(orgDtlStatHist, orgStatusHistoryDto);
					orgStatusHistoryList.add(orgStatusHistoryDto);
				});
				organizationDetailDto.setListOrganizationStatusHistoryDto(orgStatusHistoryList);
			}
		}
		return organizationDetailDto;
	}

	/**
	 * 
	 * Method Name: addExternalOrganization Method Description:This method is
	 * used to save the record for External Organization Screen into DB.
	 * 
	 * @param organizationDetailDto
	 * @return Long
	 */
	@Override
	public Long addExternalOrganization(OrganizationDetailDto organizationDetailDto) {
		log.info("addExternalOrganization Method in ExternalOrganizationDaoImpl : Execution Started.");
		// creating an instance of orgDtl Entity to be persisted into the DB.
		OrgDtl orgDtlEntity = new OrgDtl();
		BeanUtils.copyProperties(organizationDetailDto, orgDtlEntity);
		if (ObjectUtils.isEmpty(organizationDetailDto.getIdOrgDtl())) {
			orgDtlEntity.setDtCreated(new Date());
		}
		orgDtlEntity.setDtLastUpdate(new Date());
		// Checking if the address details is not null then persist the entity
		// accordingly
		if (!ObjectUtils.isEmpty(organizationDetailDto.getListOrganizationAddressDto())) {
			// Creating an instance of OrgAddr Entity to be persisted into the
			// DB.
			Set<OrgAddr> orgAddrsEntitySet = new HashSet<>();
			organizationDetailDto.getListOrganizationAddressDto().stream().forEach(addr -> {
				OrgAddr orgAddrEntity = new OrgAddr();
				BeanUtils.copyProperties(addr, orgAddrEntity);
				orgAddrEntity.setDtCreated(new Date());
				orgAddrEntity.setIdCreatedPerson(organizationDetailDto.getIdCreatedPerson());
				orgAddrEntity.setOrgDtl(orgDtlEntity);
				orgAddrEntity.setDtLastUpdate(new Date());
				orgAddrEntity.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
				orgAddrsEntitySet.add(orgAddrEntity);
			});
			orgDtlEntity.setOrgAddrs(orgAddrsEntitySet);
		}
		// Checking if the email details is not null then persist the entity
		// accordingly
		if (!ObjectUtils.isEmpty(organizationDetailDto.getListOrganizationEmailDtlDto())) {
			// Creating an instance of OrgEmailDtl Entity to be persisted into
			// the DB.
			Set<OrgEmailDtl> orgEmailDtlsEntitySet = new HashSet<>();
			organizationDetailDto.getListOrganizationEmailDtlDto().stream().forEach(email -> {
				email.setIdOrgEmailDtl(null);
				OrgEmailDtl orgEmailDtlEntity = new OrgEmailDtl();
				BeanUtils.copyProperties(email, orgEmailDtlEntity);
				orgEmailDtlEntity.setDtCreated(new Date());
				orgEmailDtlEntity.setIdCreatedPerson(organizationDetailDto.getIdCreatedPerson());
				orgEmailDtlEntity.setDtLastUpdate(new Date());
				orgEmailDtlEntity.setOrgDtl(orgDtlEntity);
				orgEmailDtlEntity.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
				orgEmailDtlsEntitySet.add(orgEmailDtlEntity);
			});
			orgDtlEntity.setOrgEmailDtls(orgEmailDtlsEntitySet);
		}
		// Checking if the identifier details is not null then persist the
		// entity accordingly
		if (!ObjectUtils.isEmpty(organizationDetailDto.getListOrganizationIdentifierDtlDto())) {
			// Creating an instance of OrgIdentifierDtl Entity to be persisted
			// into the DB.
			Set<OrgIdentifierDtl> orgIdentifierDtlsEntitySet = new HashSet<>();
			organizationDetailDto.getListOrganizationIdentifierDtlDto().stream().forEach(identifier -> {
				identifier.setIdOrgIdentifierDtl(null);
				OrgIdentifierDtl orgIdentifierDtlEntity = new OrgIdentifierDtl();
				BeanUtils.copyProperties(identifier, orgIdentifierDtlEntity);
				orgIdentifierDtlEntity.setDtCreated(new Date());
				orgIdentifierDtlEntity.setIdCreatedPerson(organizationDetailDto.getIdCreatedPerson());
				orgIdentifierDtlEntity.setOrgDtl(orgDtlEntity);
				orgIdentifierDtlEntity.setDtLastUpdate(new Date());
				orgIdentifierDtlEntity.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
				orgIdentifierDtlsEntitySet.add(orgIdentifierDtlEntity);

			});
			orgDtlEntity.setOrgIdentifierDtls(orgIdentifierDtlsEntitySet);
		}
		// Checking if the phone details is not null then persist the entity
		// accordingly
		if (!ObjectUtils.isEmpty(organizationDetailDto.getListOrganizationPhoneDtlDto())) {
			// Creating an instance of OrgPhoneDtl Entity to be persisted into
			// the DB.
			Set<OrgPhoneDtl> orgPhoneDtlsEntitySet = new HashSet<>();
			organizationDetailDto.getListOrganizationPhoneDtlDto().stream().forEach(phone -> {
				phone.setIdOrgPhoneDtl(null);
				OrgPhoneDtl orgPhoneDtlEntity = new OrgPhoneDtl();
				BeanUtils.copyProperties(phone, orgPhoneDtlEntity);
				orgPhoneDtlEntity.setDtCreated(new Date());
				orgPhoneDtlEntity.setIdCreatedPerson(organizationDetailDto.getIdCreatedPerson());
				orgPhoneDtlEntity.setOrgDtl(orgDtlEntity);
				orgPhoneDtlEntity.setDtLastUpdate(new Date());
				orgPhoneDtlEntity.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
				orgPhoneDtlsEntitySet.add(orgPhoneDtlEntity);
			});
			orgDtlEntity.setOrgPhoneDtls(orgPhoneDtlsEntitySet);
		}
		// Checking if the Organization Type details is not null then persist
		// the entity accordingly
		if (!ObjectUtils.isEmpty(organizationDetailDto.getOrgType())) {
			// Creating an instance of OrgType Entity to be persisted into the
			// DB.
			Set<OrgType> orgTypesEntitySet = new HashSet<>();
			organizationDetailDto.getOrgType().stream().forEach(orgType -> {
				OrgType orgTypeEntity = new OrgType();
				BeanUtils.copyProperties(orgType, orgTypeEntity);
				orgTypeEntity.setCdType(orgType);
				orgTypeEntity.setDtCreated(new Date());
				orgTypeEntity.setIdCreatedPerson(organizationDetailDto.getIdCreatedPerson());
				orgTypeEntity.setOrgDtl(orgDtlEntity);
				orgTypeEntity.setDtLastUpdate(new Date());
				orgTypeEntity.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
				orgTypesEntitySet.add(orgTypeEntity);
			});
			orgDtlEntity.setOrgTypes(orgTypesEntitySet);
		}
		// Creating an instance of OrgDtlStatTrckng Entity to be persisted into
		// the DB for the save
		Set<OrgDtlStatTrckng> orgDtlStatHistsEntitySet = new HashSet<>();
		OrgDtlStatTrckng orgDtlStatHistEntity = new OrgDtlStatTrckng();
		orgDtlStatHistEntity.setOrgDtl(orgDtlEntity);
		orgDtlStatHistEntity.setDtCreated(new Date());
		orgDtlStatHistEntity.setDtLastUpdate(new Date());
		orgDtlStatHistEntity.setDtStatusEff(new Date());
		orgDtlStatHistEntity.setIdCreatedPerson(organizationDetailDto.getIdCreatedPerson());
		orgDtlStatHistEntity.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
		if (!ObjectUtils.isEmpty(organizationDetailDto.getTxtInactvRsn())) {
			orgDtlStatHistEntity.setTxtInactvRsn(organizationDetailDto.getTxtInactvRsn());
		}
		orgDtlStatHistEntity.setCdOrgStatus(organizationDetailDto.getCdOrgStatus());
		orgDtlStatHistsEntitySet.add(orgDtlStatHistEntity);
		orgDtlEntity.setOrgDtlStatHists(orgDtlStatHistsEntitySet);
		Long idOrgDtl = (Long) sessionFactory.getCurrentSession().save(orgDtlEntity);
		log.debug("External Organization" + idOrgDtl);
		return organizationDetailDto.getIdEin();
	}

	/**
	 * 
	 * Method Name: updateExternalOrganization Method Description:This method is
	 * used to update the record for External Organization screen into DB.
	 * 
	 * @param organizationDetailDto
	 * @return OrganizationDetailRes
	 */
	@Override
	public OrganizationDetailDto updateExternalOrganization(OrganizationDetailDto organizationDetailDto) {
		log.info("updateExternalOrganization Method in ExternalOrganizationDaoImpl : Execution Started.");
		OrganizationDetailDto orgDetailDto = new OrganizationDetailDto();
		OrgDtl orgDtlEntity = (OrgDtl) sessionFactory.getCurrentSession().get(OrgDtl.class,
				organizationDetailDto.getIdOrgDtl());
		if (organizationDetailDto.getDtLastUpdate()
				.compareTo(orgDtlEntity.getDtLastUpdate()) == ServiceConstants.Zero_INT) {
			organizationDetailDto.setIdCreatedPerson(orgDtlEntity.getIdCreatedPerson());
			organizationDetailDto.setDtCreated(orgDtlEntity.getDtCreated());
			organizationDetailDto.setDtLastUpdate(new Date());

			// If the Organization Status is changed then adding a record to
			// Organization History Table to record the Change in Organization
			// Status.
			if (!ObjectUtils.isEmpty(organizationDetailDto.getCdOrgStatus())
					&& !ObjectUtils.isEmpty(orgDtlEntity.getCdOrgStatus())
					&& !organizationDetailDto.getCdOrgStatus().equalsIgnoreCase(orgDtlEntity.getCdOrgStatus())) {
				// Creating an instance of OrgDtlStatHist Entity to be persisted
				// into the DB.
				OrgDtlStatTrckng orgDtlStatHistEntity = new OrgDtlStatTrckng();
				orgDtlStatHistEntity.setOrgDtl(orgDtlEntity);
				orgDtlStatHistEntity.setDtCreated(new Date());
				orgDtlStatHistEntity.setDtLastUpdate(new Date());
				orgDtlStatHistEntity.setDtStatusEff(new Date());
				orgDtlStatHistEntity.setIdCreatedPerson(organizationDetailDto.getIdCreatedPerson());
				orgDtlStatHistEntity.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
				if (!ObjectUtils.isEmpty(organizationDetailDto.getTxtInactvRsn())) {
					orgDtlStatHistEntity.setTxtInactvRsn(organizationDetailDto.getTxtInactvRsn());
				}
				orgDtlStatHistEntity.setCdOrgStatus(organizationDetailDto.getCdOrgStatus());
				orgDtlEntity.getOrgDtlStatHists().add(orgDtlStatHistEntity);
			}

			BeanUtils.copyProperties(organizationDetailDto, orgDtlEntity);
			// If the Incoming Request is having List of Organization Address
			// List then update the existing one.
			if (!ObjectUtils.isEmpty(orgDtlEntity.getOrgAddrs())
					&& !ObjectUtils.isEmpty(organizationDetailDto.getListOrganizationAddressDto())) {
				orgDtlEntity.getOrgAddrs().stream().forEach(orgAddr -> {
					OrganizationAddressDto orgAddrDto = organizationDetailDto.getListOrganizationAddressDto().stream()
							.filter(organizationAddressDto -> organizationAddressDto.getIdOrgAddr()
									.equals(orgAddr.getIdOrgAddr()))
							.findAny().orElse(null);
					if (!ObjectUtils.isEmpty(orgAddrDto)) {
						orgAddrDto.setDtCreated(orgAddr.getDtCreated());
						orgAddrDto.setIdCreatedPerson(orgAddr.getIdCreatedPerson());
						orgAddrDto.setDtLastUpdate(new Date());
						orgAddrDto.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
						BeanUtils.copyProperties(orgAddrDto, orgAddr);
					}

				});
			}
			// If the Incoming Request is having List of Organization Email List
			// then update the existing one and Add the New records into
			// Organization Email Details Table
			if (!ObjectUtils.isEmpty(organizationDetailDto.getListOrganizationEmailDtlDto())) {
				Set<OrgEmailDtl> orgEmailDltListSet = new HashSet<>();
				organizationDetailDto.getListOrganizationEmailDtlDto().stream().forEach(orgEmailDto -> {
					// If the IdOrgEmailDtl is not existing then add the new
					// record into table.
					if (!ObjectUtils.isEmpty(orgEmailDto) && 0 == orgEmailDto.getIdOrgEmailDtl()) {
						orgEmailDto.setIdOrgEmailDtl(null);
						OrgEmailDtl orgEmailDtlEntity = new OrgEmailDtl();
						BeanUtils.copyProperties(orgEmailDto, orgEmailDtlEntity);
						orgEmailDtlEntity.setDtCreated(new Date());
						orgEmailDtlEntity.setDtLastUpdate(new Date());
						orgEmailDtlEntity.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
						orgEmailDtlEntity.setIdCreatedPerson(organizationDetailDto.getIdCreatedPerson());
						orgEmailDtlEntity.setOrgDtl(orgDtlEntity);
						orgEmailDltListSet.add(orgEmailDtlEntity);
					} else {
						if (!ObjectUtils.isEmpty(orgDtlEntity.getOrgEmailDtls())) {
							orgDtlEntity.getOrgEmailDtls().stream().forEach(orgEmails -> {
								// If any of the details are getting updated in
								// already existing Record then updating the
								// Record.
								if (!ObjectUtils.isEmpty(orgEmailDto)
										&& orgEmailDto.getIdOrgEmailDtl().equals(orgEmails.getIdOrgEmailDtl())) {
									orgEmailDto.setDtCreated(orgEmails.getDtCreated());
									orgEmailDto.setIdCreatedPerson(orgEmails.getIdCreatedPerson());
									orgEmailDto.setDtLastUpdate(new Date());
									orgEmailDto.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
									BeanUtils.copyProperties(orgEmailDto, orgEmails);
								}
							});
						}
					}
				});
				orgDtlEntity.getOrgEmailDtls().addAll(orgEmailDltListSet);
			}
			// If the Incoming Request is having List of Organization Identifier
			// List then update the existing one and Add the New records into
			// Organization Identifier Details Table
			if (!ObjectUtils.isEmpty(organizationDetailDto.getListOrganizationIdentifierDtlDto())) {
				Set<OrgIdentifierDtl> orgIdentifierDtlListSet = new HashSet<>();
				organizationDetailDto.getListOrganizationIdentifierDtlDto().stream().forEach(orgIdentifierDtlDto -> {
					// If the IdOrgIdentifierDtl is not existing then add the
					// new record into table.
					if (!ObjectUtils.isEmpty(orgIdentifierDtlDto) && 0 == orgIdentifierDtlDto.getIdOrgIdentifierDtl()) {
						orgIdentifierDtlDto.setIdOrgIdentifierDtl(null);
						OrgIdentifierDtl orgIdentifierDtlEntity = new OrgIdentifierDtl();
						BeanUtils.copyProperties(orgIdentifierDtlDto, orgIdentifierDtlEntity);
						orgIdentifierDtlEntity.setDtCreated(new Date());
						orgIdentifierDtlEntity.setDtLastUpdate(new Date());
						orgIdentifierDtlEntity.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
						orgIdentifierDtlEntity.setIdCreatedPerson(organizationDetailDto.getIdCreatedPerson());
						orgIdentifierDtlEntity.setOrgDtl(orgDtlEntity);
						orgIdentifierDtlListSet.add(orgIdentifierDtlEntity);
					} else {
						if (!ObjectUtils.isEmpty(orgDtlEntity.getOrgIdentifierDtls())) {
							orgDtlEntity.getOrgIdentifierDtls().stream().forEach(orgIdentifier -> {
								// If any of the details are getting updated in
								// already existing Record then updating the
								// Record.
								if (!ObjectUtils.isEmpty(orgIdentifierDtlDto) && orgIdentifierDtlDto
										.getIdOrgIdentifierDtl().equals(orgIdentifier.getIdOrgIdentifierDtl())) {
									orgIdentifierDtlDto.setDtCreated(orgIdentifier.getDtCreated());
									orgIdentifierDtlDto.setIdCreatedPerson(orgIdentifier.getIdCreatedPerson());
									orgIdentifierDtlDto.setDtLastUpdate(new Date());
									orgIdentifierDtlDto
											.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
									BeanUtils.copyProperties(orgIdentifierDtlDto, orgIdentifier);
								}
							});
						}

					}
				});
				orgDtlEntity.getOrgIdentifierDtls().addAll(orgIdentifierDtlListSet);
			}
			// If the Incoming Request is having List of Organization Phone List
			// then update the existing one adn Add the New records into
			// Organization Phone Details Table
			if (!ObjectUtils.isEmpty(organizationDetailDto.getListOrganizationPhoneDtlDto())) {
				Set<OrgPhoneDtl> orgPhoneDtlListSet = new HashSet<>();
				organizationDetailDto.getListOrganizationPhoneDtlDto().stream().forEach(orgPhoneDtlDto -> {
					// If the IdOrgPhoneDtl is not existing then add the record
					// into table.
					if (!ObjectUtils.isEmpty(orgPhoneDtlDto) && 0 == orgPhoneDtlDto.getIdOrgPhoneDtl()) {
						orgPhoneDtlDto.setIdOrgPhoneDtl(null);
						OrgPhoneDtl orgPhoneDtlEntity = new OrgPhoneDtl();
						BeanUtils.copyProperties(orgPhoneDtlDto, orgPhoneDtlEntity);
						orgPhoneDtlEntity.setDtCreated(new Date());
						orgPhoneDtlEntity.setDtLastUpdate(new Date());
						orgPhoneDtlEntity.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
						orgPhoneDtlEntity.setIdCreatedPerson(organizationDetailDto.getIdCreatedPerson());
						orgPhoneDtlEntity.setOrgDtl(orgDtlEntity);
						orgPhoneDtlListSet.add(orgPhoneDtlEntity);
					} else {
						if (!ObjectUtils.isEmpty(orgDtlEntity.getOrgPhoneDtls())) {
							orgDtlEntity.getOrgPhoneDtls().stream().forEach(orgPhone -> {
								// If any of the details are getting updated in
								// already existing Record then updating the
								// Record.
								if (!ObjectUtils.isEmpty(orgPhoneDtlDto)
										&& orgPhoneDtlDto.getIdOrgPhoneDtl().equals(orgPhone.getIdOrgPhoneDtl())) {
									orgPhoneDtlDto.setDtCreated(orgPhone.getDtCreated());
									orgPhoneDtlDto.setIdCreatedPerson(orgPhone.getIdCreatedPerson());
									orgPhoneDtlDto.setDtLastUpdate(new Date());
									orgPhoneDtlDto.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
									BeanUtils.copyProperties(orgPhoneDtlDto, orgPhone);
								}
							});
						}

					}
				});
				orgDtlEntity.getOrgPhoneDtls().addAll(orgPhoneDtlListSet);
			}

			// Below code will add and remove the Organization Type based on the
			// Selected input.
			if (!ObjectUtils.isEmpty(orgDtlEntity.getOrgTypes())
					&& !ObjectUtils.isEmpty(organizationDetailDto.getOrgType())) {
				Set<OrgType> orgTypeListSet = new HashSet<>();
				Set<OrgType> orgTypeListToBeRemoved = new HashSet<>();
				List<String> existingOrgType = new ArrayList<>();

				orgDtlEntity.getOrgTypes().stream().forEach(orgType -> {
					String organizationTypeDto = organizationDetailDto.getOrgType().stream()
							.filter(orgTypeDto -> orgTypeDto.equalsIgnoreCase(orgType.getCdType())).findAny()
							.orElse(null);
					if (ObjectUtils.isEmpty(organizationTypeDto)) {
						// ORG Type Record need to be deleted.
						orgTypeListToBeRemoved.add(orgType);
					} else {
						// ORG Type Record already present.
						existingOrgType.add(orgType.getCdType());
					}
				});
				// Remove all the ORG Type which are not there in incoming ORG
				// Type
				// list.
				orgDtlEntity.getOrgTypes().removeAll(orgTypeListToBeRemoved);
				// Add the ORG Type which are not saved already.
				organizationDetailDto.getOrgType().stream().forEach(orgTypeDto -> {
					if (!existingOrgType.contains(orgTypeDto)) {
						OrgType orgTypeEntity = new OrgType();
						BeanUtils.copyProperties(orgTypeDto, orgTypeEntity);
						orgTypeEntity.setCdType(orgTypeDto);
						orgTypeEntity.setDtCreated(new Date());
						orgTypeEntity.setIdCreatedPerson(organizationDetailDto.getIdCreatedPerson());
						orgTypeEntity.setOrgDtl(orgDtlEntity);
						orgTypeEntity.setDtLastUpdate(new Date());
						orgTypeEntity.setIdLastUpdatePerson(organizationDetailDto.getIdLastUpdatePerson());
						orgTypeListSet.add(orgTypeEntity);
					}
				});
				orgDtlEntity.getOrgTypes().addAll(orgTypeListSet);
			}
			sessionFactory.getCurrentSession().saveOrUpdate(orgDtlEntity);
			orgDetailDto.setIdEin(organizationDetailDto.getIdEin());
		} else {
			ErrorDto erorrDto = new ErrorDto();
			erorrDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			orgDetailDto.setErrorDto(erorrDto);
		}
		return orgDetailDto;
	}

	/**
	 * 
	 * Method Name: deleteExternalOrganization Method Description:This method is
	 * used to delete the record for External Organization from DB.
	 * 
	 * @param idOrgDtl
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deleteExternalOrganization(Long idOrgDtl) {
		log.info("deleteExternalOrganization Method in ExternalOrganizationDaoImpl : Execution Started.");
		String status = "";
		// Load the organization Details Assessment and Delete the entries.
		OrgDtl orgDtlEntity = (OrgDtl) sessionFactory.getCurrentSession().get(OrgDtl.class, idOrgDtl);

		// Check for OrgRsrcLink for Org & resource Mapping
		ExtOrgRoleMappingDto extOrgRoleMappingDto = extUserOrgMappingDao.getOrgResourcesList(orgDtlEntity.getIdEin(),
				false);
		if (!ObjectUtils.isEmpty(extOrgRoleMappingDto) && !ObjectUtils.isEmpty(extOrgRoleMappingDto.getOrgsList())
				&& !ObjectUtils.isEmpty(extOrgRoleMappingDto.getOrgsList().get(0))) {
			ExternalOrgDto externalOrgDto = extOrgRoleMappingDto.getOrgsList().get(0);
			// Checking for the Mapped Resource if resources are found then do
			// not proceed with Organization Deletion.
			// And send back the message that Resource Mapping found.
			if (!ObjectUtils.isEmpty(externalOrgDto.getResources())) {
				status = "resourceMapping";
				// Checking for EinBgcAcctLink for Active Contract if found do
				// not proceed with Organization Deletion.
				// and Send message stating that Active Contracts Found.
			} else if (contractExists(externalOrgDto.getIdOrgDtl())) {
				status = "activeContracts";
			} else {
				// Checking for the Role mapping.
				List<ExtUserOrgLink> extUserOrgLinkEntity = (List<ExtUserOrgLink>) sessionFactory.getCurrentSession()
						.createCriteria(ExtUserOrgLink.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
						.add(Restrictions.eq("orgDtl.idOrgDtl", idOrgDtl)).list();
				// If no role mapping found then proceed with the Deletion.
				if (ObjectUtils.isEmpty(extUserOrgLinkEntity)) {
					if (!ObjectUtils.isEmpty(orgDtlEntity)) {
						extUserOrgMappingDao.deleteExtUserOrgLinks(null, null, orgDtlEntity);
						sessionFactory.getCurrentSession().delete(orgDtlEntity);
						status = "success";
					}
					// Else do not delete and send a message back stating that
					// User Mapping found.
				} else {
					status = "userMapping";
				}
			}

		}
		return status;
	}

	private boolean contractExists(Long idOrgDtl) {

		boolean contractExists = false;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getOrgContractCountSql)
				.addScalar("contractCount", StandardBasicTypes.LONG).setParameter(ID_ORG_DTL, idOrgDtl);
		Long contractCount = (Long) query.uniqueResult();
		if (!ObjectUtils.isEmpty(contractCount) && contractCount > 0) {
			contractExists = true;
		}
		return contractExists;
	}

	/**
	 * 
	 * Method Name: deleteEIPDetails Method Description: This method is used to
	 * delete the Identifier detail, Phone detail and Email details related to
	 * particular organization.
	 * 
	 * @param organizationDetailReq
	 * @return String
	 */
	@Override
	public String deleteEIPDetails(Long idEip, String tableName) {
		String status = "";
		switch (tableName) {
		case IDENTIFIER:
			// Load the OrgIdentifier Details Assessment and Delete the entries.
			OrgIdentifierDtl orgIdentifierDtlEntity = (OrgIdentifierDtl) sessionFactory.getCurrentSession()
					.get(OrgIdentifierDtl.class, idEip);
			if (!ObjectUtils.isEmpty(orgIdentifierDtlEntity)) {
				orgIdentifierDtlEntity.getOrgDtl().getOrgIdentifierDtls().remove(orgIdentifierDtlEntity);
				sessionFactory.getCurrentSession().delete(orgIdentifierDtlEntity);
				status = SUCCESS;
			}
			break;
		case PHONE:
			// Load the OrgPhone Details Assessment and Delete the entries.
			OrgPhoneDtl orgPhoneDtlEntity = (OrgPhoneDtl) sessionFactory.getCurrentSession().get(OrgPhoneDtl.class,
					idEip);
			if (!ObjectUtils.isEmpty(orgPhoneDtlEntity)) {
				orgPhoneDtlEntity.getOrgDtl().getOrgPhoneDtls().remove(orgPhoneDtlEntity);
				sessionFactory.getCurrentSession().delete(orgPhoneDtlEntity);
				status = SUCCESS;
			}
			break;
		case EMAIL:
			// Load the OrgEmail Details Assessment and Delete the entries.
			OrgEmailDtl orgEmailDtlEntity = (OrgEmailDtl) sessionFactory.getCurrentSession().get(OrgEmailDtl.class,
					idEip);
			if (!ObjectUtils.isEmpty(orgEmailDtlEntity)) {
				orgEmailDtlEntity.getOrgDtl().getOrgEmailDtls().remove(orgEmailDtlEntity);
				sessionFactory.getCurrentSession().delete(orgEmailDtlEntity);
				status = SUCCESS;
			}
			break;

		default:
			// If the table name identifier does not match any of the given do
			// nothing.
			status = FAILURE;
			break;
		}
		return status;
	}

	/**
	 * 
	 * Method Name: getIdentifierDtls Method Description: This method is used to
	 * check if there is already a TIN Identifier Exists with the given ID. for
	 * any other organization.
	 * 
	 * @param idTIN
	 * @param idOrgDtl
	 * @return boolean
	 */
	@Override
	public boolean getIdentifierDtls(String idTIN, Long idOrgDtl) {
		log.info("getIdentifierDtls Method in ExternalOrganizationDaoImpl : Execution Started.");
		// Setting the TIN found indicator as false by default.
		boolean flagTIN = false;
		// Load the organization Identifier Details for the Given ID.
		OrgIdentifierDtl orgIdentifierDtlEntity = (OrgIdentifierDtl) sessionFactory.getCurrentSession()
				.createCriteria(OrgIdentifierDtl.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("txtId", idTIN)).add(Restrictions.eq("cdType", "TIN")).uniqueResult();
		// Checking if the fetched Entity is not empty which concludes the ID
		// TIN is already present in then Table.
		if (!ObjectUtils.isEmpty(orgIdentifierDtlEntity)) {
			/*
			 * Conditions:- 1. if the Id Organization detail is empty, which
			 * means its a new organization hence record is already 2. if the Id
			 * Organization Detail is not equal to the organization Detail
			 * entity associated with the fetched Identifier Detail. Both the
			 * mentioned scenario indicates that the TIN is already exiting for
			 * an organization in DB.
			 */
			if (ObjectUtils.isEmpty(idOrgDtl)) {
				flagTIN = true;
			} else if (!idOrgDtl.equals(orgIdentifierDtlEntity.getOrgDtl().getIdOrgDtl())) {
				flagTIN = true;
			}
		}

		return flagTIN;
	}

	/**
	 * 
	 * Method Name: validateOrgDetail Method Description:This method is used to
	 * Validate the Org details
	 * 
	 * @param idTIN
	 * @param idOrgDtl
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean validateOrgDetail(String nmLegal, Long idOrgDtl) {
		log.info("getIdentifierDtls Method in ExternalOrganizationDaoImpl : Execution Started.");
		boolean flagNmLegal = false;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getOrgDetailSql)
				.addScalar(ID_ORG_DTL, StandardBasicTypes.LONG).setParameter(NM_LEGAL, nmLegal.trim());
		List<Long> idOrgDtlList = (List<Long>) query.list();
		if (!ObjectUtils.isEmpty(idOrgDtl) && !ObjectUtils.isEmpty(idOrgDtlList)
				&& idOrgDtlList.stream().filter(o -> !o.equals(idOrgDtl)).findFirst().isPresent()) {
			flagNmLegal = true;
		} else if (ObjectUtils.isEmpty(idOrgDtl) && !ObjectUtils.isEmpty(idOrgDtlList)) {
			flagNmLegal = true;
		}
		return flagNmLegal;
	}

}
