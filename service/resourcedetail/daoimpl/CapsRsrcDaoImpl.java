package us.tx.state.dfps.service.resourcedetail.daoimpl;

import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_ADD;
import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_UPDATE;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.resource.dto.ResourceValueBeanDto;
import us.tx.state.dfps.service.resourcedetail.dao.CapsRsrcDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Selects,inserts,updates CAPS Resource table Jan 30, 2018- 12:08:36 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class CapsRsrcDaoImpl implements CapsRsrcDao {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(CapsRsrcDaoImpl.class);
	@Value("${CapsRsrcDaoImpl.getResourceFaciltyNumber}")
	private  String getFacilityNumberSQL;


	/**
	 * 
	 * Method Name: saveCapsResource Method Description: This method used for
	 * caps resource insert,update and delete operations using
	 * resourceDetailInDto request
	 * 
	 * @param resourceDetailInDto
	 * @return resourceId @
	 */
	@Override
	public Long saveCapsResource(ResourceDetailInDto resourceDetailInDto) {
		log.debug("Entering method saveCapsResource in CapsRsrcDaoImpl");
		Long returnResourceId = 0L;
		CapsResource capsResource = null;
		Date lastUpdatedDt = resourceDetailInDto.getDtLastUpdate();
		switch (resourceDetailInDto.getReqFuncCd()) {
		case REQ_FUNC_CD_ADD:

			capsResource = new CapsResource();
			capsResource.setNmLegal(resourceDetailInDto.getNmLegal());
			capsResource.setNmPrContactFirst(resourceDetailInDto.getNmPrContactFirst());
			capsResource.setNmPrContactLast(resourceDetailInDto.getNmPrContactLast());
			capsResource.setCdPrContactTitle(resourceDetailInDto.getCdPrContactTitle());
			capsResource.setCdRsrcType(resourceDetailInDto.getCdRsrcType());
			capsResource.setNmResource(resourceDetailInDto.getNmResource());
			capsResource.setIndChildSpecificSchedRate(resourceDetailInDto.getIndChildSpecificSchedRate());
			capsResource.setCdRsrcCertBy(resourceDetailInDto.getCdRsrcCertBy());
			capsResource.setCdRsrcCampusType(resourceDetailInDto.getCdRsrcCampusType());
			capsResource.setIndContractedCare(resourceDetailInDto.getIndContractedCare());
			capsResource.setTxtRsrcComments(resourceDetailInDto.getRsrcComments());
			capsResource.setCdRsrcHub(resourceDetailInDto.getCdRsrcHub());
			if (!ObjectUtils.isEmpty(resourceDetailInDto.getNbrRsrcFacilCapacity())) {
				capsResource.setNbrRsrcFacilCapacity(resourceDetailInDto.getNbrRsrcFacilCapacity().longValue());
			}
			capsResource.setCdRsrcTypeSrv(resourceDetailInDto.getCdRsrcTypeSrv());
			capsResource.setCdSsccCatchment(resourceDetailInDto.getCdSsccCatchment());
			capsResource.setCdRsrcStatus(resourceDetailInDto.getCdRsrcStatus());
			capsResource.setCdRsrcOwnership(resourceDetailInDto.getCdRsrcOwnership());
			//PD 91116 : get Acclaim Number only when resource link is available.
			if(!ObjectUtils.isEmpty(resourceDetailInDto.getNbrRsrcFacilAcclaim()) || resourceDetailInDto.getIdRsrcLinkParent()==null) {
				capsResource.setNbrRsrcFacilAcclaim(resourceDetailInDto.getNbrRsrcFacilAcclaim());
			} else {
				capsResource.setNbrRsrcFacilAcclaim(fetchParentFacilityNumber(resourceDetailInDto.getIdRsrcLinkParent()));
			}

			capsResource.setIndSpecialContract(resourceDetailInDto.getIndSpecialContract());
			capsResource.setIndRsrcChildSpecific(resourceDetailInDto.getIndRsrcChildSpecific());
			capsResource.setCdInactiveReason(resourceDetailInDto.getCdInactiveReason());
			capsResource.setTxtInactiveComments(resourceDetailInDto.getInactiveComments());
			capsResource.setCdRsrcMhmrCompCode(resourceDetailInDto.getCdMhmrCompCode());
			capsResource.setNmRsrcContact(resourceDetailInDto.getNmRsrcContact());
			capsResource.setCdRsrcMaintainer(resourceDetailInDto.getCdRsrcMaintainer());
			capsResource.setNbrRsrcCampusNbr(resourceDetailInDto.getNbrSchCampus());
			capsResource.setCdRsrcFacilType(resourceDetailInDto.getCdRsrcFacilType());
			capsResource.setCdInvJurisdiction(resourceDetailInDto.getCdInvJurisdiction());
			capsResource.setNmRsrcLastUpdate(resourceDetailInDto.getNmRsrcLastUpdate());
			capsResource.setIndRsrcTransport(resourceDetailInDto.getIndRsrcTransport());
			capsResource.setDtLastUpdate(new Date());
			capsResource.setDtCreateDate(new Date());
			capsResource.setIdCreatedBy(resourceDetailInDto.getIdCreatedBy());
			returnResourceId = (Long) sessionFactory.getCurrentSession().save(capsResource);
			break;
		case REQ_FUNC_CD_UPDATE:

			capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
					resourceDetailInDto.getIdResource());
			if (!ObjectUtils.isEmpty(capsResource) && lastUpdatedDt.compareTo(capsResource.getDtLastUpdate()) == 0) {
				returnResourceId = resourceDetailInDto.getIdResource();

				capsResource.setNmLegal(resourceDetailInDto.getNmLegal());
				capsResource.setNmPrContactFirst(resourceDetailInDto.getNmPrContactFirst());
				capsResource.setNmPrContactLast(resourceDetailInDto.getNmPrContactLast());
				capsResource.setCdPrContactTitle(resourceDetailInDto.getCdPrContactTitle());
				capsResource.setCdRsrcType(resourceDetailInDto.getCdRsrcType());
				capsResource.setNmResource(resourceDetailInDto.getNmResource());
				capsResource.setIndChildSpecificSchedRate(resourceDetailInDto.getIndChildSpecificSchedRate());
				capsResource.setCdRsrcCertBy(resourceDetailInDto.getCdRsrcCertBy());
				capsResource.setCdRsrcCampusType(resourceDetailInDto.getCdRsrcCampusType());
				capsResource.setIndContractedCare(resourceDetailInDto.getIndContractedCare());
				capsResource.setTxtRsrcComments(resourceDetailInDto.getRsrcComments());
				capsResource.setCdRsrcHub(resourceDetailInDto.getCdRsrcHub());
				if (!ObjectUtils.isEmpty(resourceDetailInDto.getNbrRsrcFacilCapacity())) {
					capsResource.setNbrRsrcFacilCapacity(resourceDetailInDto.getNbrRsrcFacilCapacity().longValue());
				}
				capsResource.setCdRsrcTypeSrv(resourceDetailInDto.getCdRsrcTypeSrv());
				capsResource.setCdSsccCatchment(resourceDetailInDto.getCdSsccCatchment());
				capsResource.setCdRsrcStatus(resourceDetailInDto.getCdRsrcStatus());
				capsResource.setCdRsrcOwnership(resourceDetailInDto.getCdRsrcOwnership());
				capsResource.setNbrRsrcFacilAcclaim(resourceDetailInDto.getNbrRsrcFacilAcclaim());
				capsResource.setIndSpecialContract(resourceDetailInDto.getIndSpecialContract());
				capsResource.setIndRsrcChildSpecific(resourceDetailInDto.getIndRsrcChildSpecific());
				capsResource.setCdInactiveReason(resourceDetailInDto.getCdInactiveReason());
				capsResource.setTxtInactiveComments(resourceDetailInDto.getInactiveComments());
				capsResource.setCdRsrcMhmrCompCode(resourceDetailInDto.getCdMhmrCompCode());
				capsResource.setNmRsrcContact(resourceDetailInDto.getNmRsrcContact());
				capsResource.setCdRsrcMaintainer(resourceDetailInDto.getCdRsrcMaintainer());
				capsResource.setNbrRsrcCampusNbr(resourceDetailInDto.getNbrSchCampus());
				capsResource.setCdRsrcFacilType(resourceDetailInDto.getCdRsrcFacilType());
				capsResource.setCdInvJurisdiction(resourceDetailInDto.getCdInvJurisdiction());
				capsResource.setNmRsrcLastUpdate(resourceDetailInDto.getNmRsrcLastUpdate());
				capsResource.setIndRsrcTransport(resourceDetailInDto.getIndRsrcTransport());
				capsResource.setIdResource(resourceDetailInDto.getIdResource());
				capsResource.setDtLastUpdate(resourceDetailInDto.getDtLastUpdate());
				capsResource.setCdPrfrdCntctMthd(resourceDetailInDto.getCdPrfrdCntctMthd());
				sessionFactory.getCurrentSession().update(capsResource);
			}
			if (returnResourceId == 0) {
				throw new DataNotFoundException(
						messageSource.getMessage("caps.resource.nodata.update", null, Locale.US));
			}
		}
		log.debug("Exiting method saveCapsResource in CapsRsrcDaoImpl");
		return returnResourceId;
	}

	/**
	 *
	 * Method Name: fetchParentFacilityNumber Method Description: This method used for
	 * fetch the facility number for childs/subcontracting  resources with no facilty number from parent resource   using
	 * parent resource ID
	 *
	 * @param resourceID
	 * @return facilityNumber @
	 */
	@Override
	public Long fetchParentFacilityNumber(Long resourceID) {
		return (Long)sessionFactory.getCurrentSession().createSQLQuery(getFacilityNumberSQL)
						.addScalar("nbrRsrcFacilAcclaim", StandardBasicTypes.LONG).setParameter("idResource", resourceID).uniqueResult();


	}
}