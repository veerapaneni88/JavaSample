/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:This class is used for Add External Organization and External Organization Detail as service
 *Jul 13, 2018- 2:34:07 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.externalorg.dto.OrganizationDetailDto;
import us.tx.state.dfps.common.externalorg.dto.OrganizationIdentifierDtlDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.OrganizationDetailReq;
import us.tx.state.dfps.service.common.response.OrganizationDetailRes;
import us.tx.state.dfps.service.externalorg.dao.ExternalOrganizationDao;
import us.tx.state.dfps.service.externalorg.service.ExternalOrganizationService;
import us.tx.state.dfps.service.person.dao.PersonDao;

@Transactional
@Service
public class ExternalOrganizationServiceImpl implements ExternalOrganizationService {
	private static final String TIN = "TIN";

	@Autowired
	ExternalOrganizationDao externalOrganizationDao;

	@Autowired
	PersonDao personDao;

	/**
	 * 
	 * Method Name: fetchExternalOrganization Method Description: This method is
	 * used to fetch the organization detail
	 * 
	 * @param idEin
	 * @return OrganizationDetailDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public OrganizationDetailDto fetchExternalOrganization(Long idEin) {
		OrganizationDetailDto organizationDetailDto = externalOrganizationDao.fetchExternalOrganization(idEin);

		return organizationDetailDto;
	}

	/**
	 * 
	 * Method Name: getPersonName Method Description: This method is used to
	 * fetch the person detail based on person id.
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public String getPersonName(Long idPerson) {
		String nmPersonFull = "";
		Person person = personDao.getPersonByPersonId(idPerson);
		nmPersonFull = person.getNmPersonFull();
		return nmPersonFull;
	}

	/**
	 * 
	 * Method Name: externalOrganizationAUD Method Description:This method is
	 * used to save the organization detail
	 * 
	 * @param organizationDetailReq
	 * @return OrganizationDetailRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public OrganizationDetailRes externalOrganizationAUD(OrganizationDetailReq organizationDetailReq) {
		OrganizationDetailRes organizationDetailRes = new OrganizationDetailRes();
		// Checking if the request have function code then set the function code
		// to Action Requested. Else set it to update.
		String actionRequested = !ObjectUtils.isEmpty(organizationDetailReq.getReqFuncCd())
				? organizationDetailReq.getReqFuncCd() : ServiceConstants.UPDATE;
		/*
		 * Checking if the Request is having id organization Detail, if not then
		 * it means its a new request to add. Hence calling the add external
		 * organization method, Else checking the requested action whether it is
		 * Delete or update then calling the method respectively.
		 */
		if (ObjectUtils.isEmpty(organizationDetailReq.getOrganizationDetailDto().getIdOrgDtl())
				|| ServiceConstants.ZERO.equals(organizationDetailReq.getOrganizationDetailDto().getIdOrgDtl())) {
			Long idEin = externalOrganizationDao
					.addExternalOrganization(organizationDetailReq.getOrganizationDetailDto());
			organizationDetailRes.setIdEin(idEin);
		} else {
			switch (actionRequested) {
			case ServiceConstants.DELETE:
				organizationDetailRes.setStatus(externalOrganizationDao
						.deleteExternalOrganization(organizationDetailReq.getOrganizationDetailDto().getIdOrgDtl()));
				break;
			default:
				OrganizationDetailDto organizationDetailDto = externalOrganizationDao
						.updateExternalOrganization(organizationDetailReq.getOrganizationDetailDto());
				organizationDetailRes.setOrganizationDetailDto(organizationDetailDto);
				if (!ObjectUtils.isEmpty(organizationDetailDto.getIdEin())) {
					organizationDetailRes.setIdEin(organizationDetailDto.getIdEin());
				}
				break;
			}
		}
		return organizationDetailRes;
	}

	/**
	 * 
	 * Method Name: fetchExternalOrganization Method Description: This method is
	 * used to fetch the organization detail
	 * 
	 * @param organizationDetailReq
	 * @return OrganizationDetailDto
	 */
	@Override
	public OrganizationDetailDto getExtOrgDtls(OrganizationDetailReq organizationDetailReq) {
		return externalOrganizationDao.fetchExternalOrganization(organizationDetailReq.getIdEin());

	}

	/**
	 * 
	 * Method Name: deleteEIPDetails Method Description: This method is used to
	 * delete the Identifier detail, Phone detail and Email details related to
	 * particular organization.
	 * 
	 * @param organizationDetailReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String deleteEIPDetails(OrganizationDetailReq organizationDetailReq) {
		return externalOrganizationDao.deleteEIPDetails(organizationDetailReq.getIdEip(),
				organizationDetailReq.getTableName());
	}

	/**
	 * 
	 * Method Name: getIdentifierDtls Method Description: This method is used to
	 * check if there is already a TIN Identifier Exists with the given ID. for
	 * any other organization.
	 * 
	 * @param organizationDetailReq
	 * @return boolean
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public boolean getIdentifierDtls(OrganizationDetailReq organizationDetailReq) {
		return externalOrganizationDao.getIdentifierDtls(organizationDetailReq.getIdTIN(),
				organizationDetailReq.getIdOrgDtl());

	}

	/**
	 * 
	 * Method Name: getIdentifierDtls Method Description: This method is used to
	 * check if there is already a TIN Identifier Exists with the given ID. for
	 * any other organization.
	 * 
	 * @param organizationDetailReq
	 * @return boolean
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public OrganizationDetailRes validateOrgDetail(OrganizationDetailReq organizationDetailReq) {
		OrganizationDetailRes organizationDetailRes = new OrganizationDetailRes();
		OrganizationDetailDto existingOrgDetails = externalOrganizationDao
				.fetchExternalOrganization(organizationDetailReq.getOrganizationDetailDto().getIdEin());
		// Checking for EIN id whether it exists or not.
		if (ObjectUtils.isEmpty(organizationDetailReq.getOrganizationDetailDto().getIdOrgDtl())
				&& !ObjectUtils.isEmpty(existingOrgDetails.getIdEin())) {
			organizationDetailRes.setFlagEINExist(true);
		} else if (!ObjectUtils.isEmpty(organizationDetailReq.getOrganizationDetailDto().getIdOrgDtl())
				&& !ObjectUtils.isEmpty(existingOrgDetails.getIdEin()) && !organizationDetailReq
						.getOrganizationDetailDto().getIdOrgDtl().equals(existingOrgDetails.getIdOrgDtl())) {
			organizationDetailRes.setFlagEINExist(true);
		}
		// Checking for Identifier Detail whether it exist or not.
		if (!ObjectUtils
				.isEmpty(organizationDetailReq.getOrganizationDetailDto().getListOrganizationIdentifierDtlDto())) {
			List<OrganizationIdentifierDtlDto> orgIdentifierList = organizationDetailReq.getOrganizationDetailDto()
					.getListOrganizationIdentifierDtlDto().stream()
					.filter(identifierDto -> TIN.equalsIgnoreCase(identifierDto.getCdType()))
					.collect(Collectors.toList());
			if (!ObjectUtils.isEmpty(orgIdentifierList) && orgIdentifierList.size() == 1) {
				organizationDetailRes
						.setFlagTINExist(externalOrganizationDao.getIdentifierDtls(orgIdentifierList.get(0).getTxtId(),
								organizationDetailReq.getOrganizationDetailDto().getIdOrgDtl()));
			}
		}
		// Checking for Legal Name whether it is already exist or not.
		organizationDetailRes.setFlagNmLegalExist(
				externalOrganizationDao.validateOrgDetail(organizationDetailReq.getOrganizationDetailDto().getNmLegal(),
						organizationDetailReq.getOrganizationDetailDto().getIdOrgDtl()));
		;
		return organizationDetailRes;

	}

}
