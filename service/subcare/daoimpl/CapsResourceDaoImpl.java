package us.tx.state.dfps.service.subcare.daoimpl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Contract;
import us.tx.state.dfps.common.domain.ContractCounty;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.ResourceAddress;
import us.tx.state.dfps.common.domain.ResourceContact;
import us.tx.state.dfps.common.domain.ResourceEmail;
import us.tx.state.dfps.common.domain.ResourceLanguage;
import us.tx.state.dfps.common.domain.ResourcePhone;
import us.tx.state.dfps.common.domain.RsrcLink;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dto.CapResourceInDto;
import us.tx.state.dfps.service.admin.dto.CapResourceOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.ApproversDao;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CvsFaHomeReq;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.response.FacilityDetailRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataMismatchException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dto.CapsResourceDto;
import us.tx.state.dfps.service.subcare.dto.CapsResourceLinkDto;
import us.tx.state.dfps.service.subcare.dto.ResourceAddressDto;
import us.tx.state.dfps.service.subcare.dto.ResourceContactDto;
import us.tx.state.dfps.service.subcare.dto.ResourceEmailDto;
import us.tx.state.dfps.service.subcare.dto.ResourceLanguageDto;
import us.tx.state.dfps.service.subcare.dto.ResourcePhoneDto;
import us.tx.state.dfps.service.subcare.dto.RsrcLinkDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Method
 * implements OfficeDao. This is used to retrieve count of case worker based on
 * PR Role from database. Sep 9, 2017- 12:29:33 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class CapsResourceDaoImpl implements CapsResourceDao {

	@Autowired
	MessageSource messageSource;

	@Value("${CapsResourceDaoImpl.getResourceId}")
	private String resourceIdSql;

	@Value("${CapsResourceDaoImpl.getParentResource}")
	private String parentResourceSql;

	@Value("${CapsResourceDaoImpl.getSchDistname}")
	private String getSchDistnameSql;

	@Value("${CapsResourceDaoImpl.getCapsResourceLinkSql}")
	private String getCapsResourceLinkSql;

	@Value("${CapsResourceDaoImpl.getResource}")
	private String getResource;

	@Value("${CapsResourceDaoImpl.getResourceDateByIdStageSql}")
	private String getResourceDateByIdStageSql;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	ApproversDao approversDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	StageDao stageDao;

	private static final Logger log = Logger.getLogger(CapsResourceDaoImpl.class);

	public CapsResourceDaoImpl() {

	}

	/**
	 * 
	 * Method Description: Method to check if another primary caregiver is
	 * present for CVS Home window. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return Long @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public Long getResourceId(CvsFaHomeReq cvsFaHomeReq) {
		Long resourceId = null;
		resourceId = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(resourceIdSql)
				.setParameter("idStage", cvsFaHomeReq.getStagePersonLinkDto().getIdStage()))
						.addScalar("idResource", StandardBasicTypes.LONG).uniqueResult();
		log.info("TransactionId :" + cvsFaHomeReq.getTransactionId());
		return resourceId;
	}

	/**
	 * 
	 * Method Description: Method to update NM_PERSON details in CAPS_RESOURCE
	 * Table. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @
	 */
	@Override
	public void updateNmResource(CvsFaHomeReq cvsFaHomeReq, CapsResource capsResource) {

		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(capsResource));

		log.info("TransactionId :" + cvsFaHomeReq.getTransactionId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.subcare.dao.CapsResourceDao#getResourceType(us.
	 * tx. state.dfps.service.admin.dto.CapResourceInDto)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CapResourceOutDto> getResourceType(CapResourceInDto capResourceInDto) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResource)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCertBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcOperBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSetting", StandardBasicTypes.STRING)
				.addScalar("cdRsrcPayment", StandardBasicTypes.STRING).addScalar("dtRsrcCert", StandardBasicTypes.DATE)
				.addScalar("dtRsrcClose", StandardBasicTypes.DATE).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcFacilCapacity", StandardBasicTypes.SHORT)
				.setResultTransformer(Transformers.aliasToBean(CapResourceOutDto.class)));
		sQLQuery1.setParameter("idResource", capResourceInDto.getIdResourceService());
		List<CapResourceOutDto> capResourceOutDtoList = (List<CapResourceOutDto>) sQLQuery1.list();

		return capResourceOutDtoList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.subcare.dao.CapsResourceDao#
	 * updateFacilityDetailsInResource(us.tx.state.dfps.service.common.request.
	 * FacilityDetailSaveReq)
	 */
	@Override
	public FacilityDetailRes updateFacilityDetailsInResource(FacilityDetailSaveReq facilityDetailSaveReq) {
		FacilityDetailRes res = new FacilityDetailRes();
		ErrorDto errorDto = new ErrorDto();
		Criteria crCapsResource = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
				.add(Restrictions.eq("idResource", facilityDetailSaveReq.getIdResource()));
		CapsResource capsResource = (CapsResource) crCapsResource.uniqueResult();

		String dateStr = facilityDetailSaveReq.getTempLastUpdate();

		Date lastUpdate = null;
		try {
			lastUpdate = DateUtils.parseDate(dateStr,
					new String[] { ServiceConstants.DATE_FORMAT_MMDDYYYY, ServiceConstants.DATE_FORMAT_yyyyMMddHHmmssS,
							ServiceConstants.DATE_FORMAT_yyyyMMddHHss, ServiceConstants.DATE_FORMAT_MMddyyyyTHHmmss });
			if (!lastUpdate.equals(capsResource.getDtLastUpdate())) {
				res.setHasError(ServiceConstants.TRUEVAL);
				errorDto.setErrorMsg(
						"Facility Detail Update Failed - Dt Last update is not equal to the value in table");
				res.setErrorDto(errorDto);
			} else {
				capsResource.setCdRsrcCertBy(facilityDetailSaveReq.getCdRsrcCertBy());
				capsResource.setCdRsrcOperBy(facilityDetailSaveReq.getCdRsrcOperBy());
				capsResource.setCdRsrcSetting(facilityDetailSaveReq.getCdRsrcSetting());
				capsResource.setCdRsrcPayment(facilityDetailSaveReq.getCdRsrcPayment());
				capsResource
						.setNbrRsrcFacilCapacity(!ObjectUtils.isEmpty(facilityDetailSaveReq.getNbrRsrcFacilCapacity())
								? facilityDetailSaveReq.getNbrRsrcFacilCapacity().longValue() : null);
				capsResource.setNmRsrcLastUpdate(facilityDetailSaveReq.getNmRsrcLastUpdate());
				capsResource.setDtRsrcCert(facilityDetailSaveReq.getDtRsrcCert());
				capsResource.setDtRsrcClose(facilityDetailSaveReq.getDtRsrcClose());
				capsResource.setDtLastUpdate(new Date());

				sessionFactory.getCurrentSession().saveOrUpdate(capsResource);
				res.setHasError(ServiceConstants.FALSEVAL);
				errorDto.setErrorMsg(ServiceConstants.EMPTY_STRING);
				res.setErrorDto(errorDto);
			}
		} catch (ParseException e) {
			res.setHasError(ServiceConstants.TRUEVAL);
			errorDto.setErrorMsg("Facility Detail Update Failed - Dt Last Updated is Failed ParseException");

			DataMismatchException dataMismatchException = new DataMismatchException(
					"Facility Detail Update Failed - Dt Last Updated is Failed ParseException");
			dataMismatchException.initCause(e);
			throw dataMismatchException;
		}
		return res;
	}

	@Override
	public CapsResource getCapsResourceById(Long idCapsResource) {
		CapsResource capsResource = new CapsResource();
		Criteria crCapsResource = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
				.add(Restrictions.eq("idResource", idCapsResource));
		capsResource = (CapsResource) crCapsResource.uniqueResult();
		return capsResource;
	}

	public String getFaHomeStatusByStageId(Long idStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
				.add(Restrictions.eq("stage.idStage", idStage));
		CapsResource capsResource = (CapsResource) criteria.uniqueResult();
		return capsResource.getCdRsrcFaHomeStatus();
	}

	public Long getCapsResourceByStageId(Long idStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
				.add(Restrictions.eq("stage.idStage", idStage));
		CapsResource capsResource = (CapsResource) criteria.uniqueResult();
		return !ObjectUtils.isEmpty(capsResource) && !ObjectUtils.isEmpty(capsResource.getIdResource()) ?
				capsResource.getIdResource() : 0L;
	}

	public CapsResource getCapsResourceForStageId(Long idStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
				.add(Restrictions.eq("stage.idStage", idStage));
		return (CapsResource) criteria.uniqueResult();
	}

	/**
	 * Retrieval service called within predisplay of Rsrc Dtl Service Name :
	 * CRES03S
	 *
	 * @param idCapsResource
	 * @return CapsResource @
	 */

	@Override
	public ResourceDto getResourceDtl(Long idResource) {

		ResourceDto resourceDto = getResourceById(idResource);
		resourceDto.setResourcePhoneLists(getResourcePhone(idResource));

		if (indSubContracting(idResource)) {
			resourceDto.setIndRsrcSub(ServiceConstants.STRING_IND_Y);
		} else {
			resourceDto.setIndRsrcSub(ServiceConstants.STRING_IND_N);
		}

		if (indPrimeContracting(idResource)) {
			resourceDto.setIndRsrcPrime(ServiceConstants.STRING_IND_Y);
		} else {
			resourceDto.setIndRsrcPrime(ServiceConstants.STRING_IND_N);
		}

		if (indActiveContract(idResource)) {
			resourceDto.setIndRsrcContracted(ServiceConstants.STRING_IND_Y);
		} else {
			resourceDto.setIndRsrcContracted(ServiceConstants.STRING_IND_N);
		}

		resourceDto.setResourceAddressLists(getResourceAddress(idResource));
		String schoolDistCode = null;
		String schoolDistCounty = null;

		for (ResourceAddressDto resourceAddressDto : resourceDto.getResourceAddressLists()) {
			String resAddrType = resourceAddressDto.getCdRsrcAddrType();
			if (ServiceConstants.PRIMARY_ADDRESS_TYPE.equals(resAddrType)) {
				schoolDistCode = resourceAddressDto.getCdRsrcAddrSchDist();
				schoolDistCounty = resourceAddressDto.getCdRsrcAddrCounty();
				break;
			}
		}
		if (schoolDistCode != null && schoolDistCounty != null) {
			//artf212958 : passing county code to get school dist code
			resourceDto.setTxtSchDistName(getSchDistName(schoolDistCode,schoolDistCounty));
		}

		for (ResourceAddressDto resourceAddressDto : resourceDto.getResourceAddressLists()) {
			if (indRsrcContractCheck(resourceAddressDto.getIdRsrcAddress())) {
				resourceAddressDto.setIndRsrcContracted(String.valueOf(ServiceConstants.STRING_IND_Y));
			} /*
				 * else {
				 * resourceAddressDto.setIndRsrcContracted(String.valueOf(
				 * ServiceConstants. STRING_IND_Y)); }
				 */
		}

		resourceDto.setResourceEmailLists(getResourceEmail(idResource));

		resourceDto.setResourceLanguageLists(getResourceLanguage(idResource));

		String rsrcLinkType = "";
		if(null == resourceDto.getNbrRsrcFacilAcclaim() || resourceDto.getNbrRsrcFacilAcclaim() == 0L){
			if("Y".equalsIgnoreCase(resourceDto.getIndChildSpecificSchedRate()) ||
					"Y".equalsIgnoreCase(resourceDto.getIndRsrcChildSpecific())){
				rsrcLinkType = ServiceConstants.CD_RSRC_LINK_CHILD_SPECIFIC;
			}else if("Y".equalsIgnoreCase(resourceDto.getIndSpecialContract())){
				rsrcLinkType = ServiceConstants.CD_RSRC_LINK_SPECIAL_CON;
			}
		}else{
			rsrcLinkType =ServiceConstants.CD_RSRC_LINK_CPA_HOME;
		}
		RsrcLinkDto parentResource = getParentsResource(idResource,rsrcLinkType);

		if (!ObjectUtils.isEmpty(parentResource)) {
			resourceDto.setIdRsrcLinkParent(parentResource.getIdRsrcLinkParent());
			resourceDto.setNmRsrcLinkParent(parentResource.getNmResourceParent());
			resourceDto.setCdRsrcTypeLink(parentResource.getCdRsrcLinkType());
		}
		resourceDto.setResourceContactLists(getResourceContact(idResource));
		return resourceDto;
	}

	/**
	 * Rsrc Phone query list Service Name : CRES14D
	 * 
	 * @param idCapsResource
	 * @return ResourcePhoneDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourcePhoneDto> getResourcePhone(Long idResource) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ResourcePhone.class)
				.createAlias("capsResource", "capsResource").add(Restrictions.eq("capsResource.idResource", idResource))
				.setProjection(Projections.projectionList().add(Projections.property("idRsrcPhone"), "idRsrcPhone")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("nbrRsrcPhone"), "nbrRsrcPhone")
						.add(Projections.property("nbrRsrcPhoneExt"), "nbrRsrcPhoneExt")
						.add(Projections.property("txtRsrcPhoneComments"), "txtRsrcPhoneComments")
						.add(Projections.property("capsResource.idResource"), "idResource")
						.add(Projections.property("cdRsrcPhoneType"), "cdRsrcPhoneType"))
				.setResultTransformer(Transformers.aliasToBean(ResourcePhoneDto.class));

		return cr.list();
	}

	/**
	 * Resource Link table simple query which checks for sub-contracting Service
	 * Name : CRES15D
	 * 
	 * @param idCapsResource
	 * @return Boolean @
	 */
	@Override
	public boolean indSubContracting(Long idRsrcLinkChild) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(RsrcLink.class)
				.createAlias("capsResourceByIdRsrcLinkChild", "capsResourceByIdRsrcLinkChild")
				.add(Restrictions.and(Restrictions.eq("capsResourceByIdRsrcLinkChild.idResource", idRsrcLinkChild),
						Restrictions.eq("cdRsrcLinkType", "01")))
				.setProjection(Projections.rowCount());

		Long rowCount = (Long) cr.uniqueResult();

		return rowCount > 0;
	}

	/**
	 * Resource Link table simple query which checks for prime contracting.
	 * Service Name : CRES38D
	 * 
	 * @param idRsrcLinkParent
	 * @return Boolean @
	 */

	@Override
	public boolean indPrimeContracting(Long idRsrcLinkParent) {

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(RsrcLink.class)
				.createAlias("capsResourceByIdRsrcLinkParent", "capsResourceByIdRsrcLinkParent")
				.add(Restrictions.and(Restrictions.eq("capsResourceByIdRsrcLinkParent.idResource", idRsrcLinkParent),
						Restrictions.eq("cdRsrcLinkType", "01")))
				.setProjection(Projections.rowCount());

		Long rowCount = (Long) cr.uniqueResult();

		return rowCount > 0;
	}

	/**
	 * Returns a row from the Contract County table if an active contract is
	 * found for the resource. Service Name : CRES39D
	 * 
	 * @param idCapsResource
	 * @return Boolean @
	 */
	@Override
	public boolean indActiveContract(Long idCapsResource) {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ContractCounty.class)
				.createAlias("capsResource", "capsResource")
				.add(Restrictions.and(Restrictions.eq("capsResource.idResource", idCapsResource), Restrictions
						.or(Restrictions.ge("dtCncntyEnd", cal.getTime()), Restrictions.isNull("dtCncntyEnd"))))
				.setProjection(Projections.rowCount());

		Long rowCount = (Long) cr.uniqueResult();

		return rowCount > 0;
	}

	/**
	 * Rsrc Address query list Service Name : CRES13D
	 * 
	 * @param idCapsResource
	 * @return ResourceAddressDto @
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceAddressDto> getResourceAddress(Long idResource) {

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ResourceAddress.class)
				.createAlias("capsResource", "capsResource")
				.add(Restrictions.and(Restrictions.eq("capsResource.idResource", idResource),
						Restrictions.ne("cdRsrcAddrType", "09"), Restrictions.ne("cdRsrcAddrType", "08")))
				.setProjection(Projections.projectionList().add(Projections.property("idRsrcAddress"), "idRsrcAddress")
						.add(Projections.property("capsResource.idResource"), "idResource")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("addrRsrcAddrZip"), "addrRsrcAddrZip")
						.add(Projections.property("cdRsrcAddrCounty"), "cdRsrcAddrCounty")
						.add(Projections.property("addrRsrcAddrAttn"), "addrRsrcAddrAttn")
						.add(Projections.property("cdRsrcAddrState"), "cdRsrcAddrState")
						.add(Projections.property("addrRsrcAddrStLn1"), "addrRsrcAddrStLn1")
						.add(Projections.property("addrRsrcAddrStLn2"), "addrRsrcAddrStLn2")
						.add(Projections.property("cdRsrcAddrSchDist"), "cdRsrcAddrSchDist")
						.add(Projections.property("cdRsrcAddrType"), "cdRsrcAddrType")
						.add(Projections.property("txtRsrcAddrComments"), "txtRsrcAddrComments")
						.add(Projections.property("nbrRsrcAddrVid"), "nbrRsrcAddrVid")
						.add(Projections.property("addrRsrcAddrCity"), "addrRsrcAddrCity"))
				.setResultTransformer(Transformers.aliasToBean(ResourceAddressDto.class));

		return cr.list();
	}

	/**
	 * Resource Address contract check Service Name : CRES44D
	 * 
	 * @param idRsrcAddress
	 * @return Boolean @
	 */
	@Override
	public boolean indRsrcContractCheck(Long idRsrcAddress) {

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(Contract.class)
				.createAlias("resourceAddress", "resourceAddress")
				.add(Restrictions.eq("resourceAddress.idRsrcAddress", idRsrcAddress))
				.setProjection(Projections.rowCount());

		Long rowCount = (Long) cr.uniqueResult();

		return rowCount > 0;
	}

	/**
	 * get ResourceEmail Service Name : CLSCG3D
	 * 
	 * @param idResource
	 * @return ResourceEmailDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceEmailDto> getResourceEmail(Long idResource) {

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ResourceEmail.class)
				.add(Restrictions.eq("idResource", idResource))
				.setProjection(Projections.projectionList()
						.add(Projections.property("idResourceEmail"), "idResourceEmail")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idResource"), "idResource")
						.add(Projections.property("cdEmailType"), "cdEmailType")
						.add(Projections.property("indPrimary"), "indPrimary")
						.add(Projections.property("dtStart"), "dtStart").add(Projections.property("dtEnd"), "dtEnd")
						.add(Projections.property("txtEmailAddress"), "txtEmailAddress")
						.add(Projections.property("txtEmailComment"), "txtEmailComment")
						.add(Projections.property("idEmpLastUpdate"), "idEmpLastUpdate"))
				.addOrder(Order.asc("idResourceEmail"))
				.setResultTransformer(Transformers.aliasToBean(ResourceEmailDto.class));

		return cr.list();
	}

	/**
	 * get ResourceLanguage Service Name : CLSCG4D
	 * 
	 * @param idResource
	 * @return ResourceLanguageDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceLanguageDto> getResourceLanguage(Long idResource) {

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ResourceLanguage.class)
				.add(Restrictions.eq("idResource", idResource)).add(Restrictions.isNull("dtEnd"))
				.setProjection(Projections.projectionList()
						.add(Projections.property("idResourceLanguage"), "idResourceLanguage")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idResource"), "idResource")
						.add(Projections.property("cdLanguage"), "cdLanguage")
						.add(Projections.property("dtStart"), "dtStart").add(Projections.property("dtEnd"), "dtEnd")
						.add(Projections.property("idEmpLastUpdate"), "idEmpLastUpdate"))
				.addOrder(Order.asc("dtLastUpdate"))
				.setResultTransformer(Transformers.aliasToBean(ResourceLanguageDto.class));

		return cr.list();
	}

	/**
	 * Gets Parents Resource Id and Name Service Name : CSECE9D
	 * 
	 * @param idResource
	 * @return CapsResourceDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RsrcLinkDto getParentsResource(Long idRsrcLinkChild,String cdRsrcLinkType) {
		RsrcLinkDto linkResource = null;
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(parentResourceSql);
		query.setParameter("idRsrcLinkChild", idRsrcLinkChild);
		query.setParameter("cdRsrcLinkType", cdRsrcLinkType);
		query.addScalar("idRsrcLinkParent", StandardBasicTypes.LONG);
		query.addScalar("nmResourceParent", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcLinkType", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(RsrcLinkDto.class));
		// RsrcLinkDto result = (RsrcLinkDto) query.uniqueResult();
		List<RsrcLinkDto> resultList = (List<RsrcLinkDto>) query.list();
		if (!ObjectUtils.isEmpty(resultList)) {
			linkResource = resultList.get(0);
		}
		return linkResource;
	}

	/**
	 * Gets Resource Contact Service Name : CLSSC7D
	 * 
	 * @param idResource
	 * @return CapsResourceDto @
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceContactDto> getResourceContact(Long idResource) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ResourceContact.class)
				.add(Restrictions.eq("idResource", idResource))
				.setProjection(
						Projections.projectionList().add(Projections.property("idResourceContact"), "idResourceContact")
								.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
								.add(Projections.property("idResource"), "idResource")
								.add(Projections.property("cdContactType"), "cdContactType")
								.add(Projections.property("txtContactName"), "txtContactName")
								.add(Projections.property("txtContactPhone"), "txtContactPhone")
								.add(Projections.property("txtContactTitle"), "txtContactTitle"))
				.setResultTransformer(Transformers.aliasToBean(ResourceContact.class));

		return cr.list();

	}

	/**
	 * School District simple query Service Name : CRES08D
	 * artf212958 : passing county code to get school dist code
	 * @param cdSchDist, cdSchDistCounty
	 * @return String @
	 */

	@Override
	public String getSchDistName(String cdSchDist,String cdSchDistCounty) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getSchDistnameSql);
		query.setParameter("cdSchDist", cdSchDist);
		query.setParameter("cdSchDistCounty", cdSchDistCounty);
		//artf241753  : Added null check for result list before fetching
		List result = query.list();
		return  CollectionUtils.isNotEmpty(result) ? (String) result.get(0) : "";
	}

	/**
	 * This DAM performs AUD functionality on the FACILITY INVST DTL table. This
	 * DAM only inserts. Service Name : CRES04D DAM Name: CRES04D Service Name:
	 * CCMN35S
	 * 
	 * @param idCapsResource
	 * @return CapsResource @
	 */
	@Override
	public ResourceDto getResourceById(Long idResource) {

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
				.add(Restrictions.eq("idResource", idResource))
				.setProjection(Projections.projectionList().add(Projections.property("idResource"), "idResource")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("addrRsrcStLn1"), "addrRsrcStLn1")
						.add(Projections.property("addrRsrcStLn2"), "addrRsrcStLn2")
						.add(Projections.property("addrRsrcCity"), "addrRsrcCity")
						.add(Projections.property("cdRsrcState"), "cdRsrcState")
						.add(Projections.property("addrRsrcZip"), "addrRsrcZip")
						.add(Projections.property("addrRsrcAttn"), "addrRsrcAttn")
						.add(Projections.property("cdRsrcCnty"), "cdRsrcCnty")
						.add(Projections.property("cdRsrcInvolClosure"), "cdRsrcInvolClosure")
						.add(Projections.property("cdRsrcClosureRsn"), "cdRsrcClosureRsn")
						.add(Projections.property("cdRsrcSourceInquiry"), "cdRsrcSourceInquiry")
						.add(Projections.property("cdRsrcType"), "cdRsrcType")
						.add(Projections.property("cdRsrcCampusType"), "cdRsrcCampusType")
						.add(Projections.property("cdRsrcMaintainer"), "cdRsrcMaintainer")
						.add(Projections.property("cdRsrcSchDist"), "cdRsrcSchDist")
						.add(Projections.property("cdRsrcOwnership"), "cdRsrcOwnership")
						.add(Projections.property("cdRsrcSchDist"), "cdRsrcSchDist")
						.add(Projections.property("cdRsrcOwnership"), "cdRsrcOwnership")
						.add(Projections.property("cdRsrcFacilType"), "cdRsrcFacilType")
						.add(Projections.property("cdRsrcHub"), "cdRsrcHub")
						.add(Projections.property("cdRsrcCertBy"), "cdRsrcCertBy")
						.add(Projections.property("cdRsrcOperBy"), "cdRsrcOperBy")
						.add(Projections.property("cdRsrcSetting"), "cdRsrcSetting")
						.add(Projections.property("cdRsrcPayment"), "cdRsrcPayment")
						.add(Projections.property("cdRsrcCategory"), "cdRsrcCategory")
						.add(Projections.property("cdRsrcEthnicity"), "cdRsrcEthnicity")
						.add(Projections.property("cdRsrcLanguage"), "cdRsrcLanguage")
						.add(Projections.property("cdRsrcMaritalStatus"), "cdRsrcMaritalStatus")
						.add(Projections.property("cdRsrcRecmndReopen"), "cdRsrcRecmndReopen")
						.add(Projections.property("cdRsrcRegion"), "cdRsrcRegion")
						.add(Projections.property("cdRsrcReligion"), "cdRsrcReligion")
						.add(Projections.property("cdRsrcRespite"), "cdRsrcRespite")
						.add(Projections.property("cdRsrcFaHomeStatus"), "cdRsrcFaHomeStatus")
						.add(Projections.property("cdRsrcFaHomeType1"), "cdRsrcFaHomeType1")
						.add(Projections.property("cdRsrcFaHomeType2"), "cdRsrcFaHomeType2")
						.add(Projections.property("cdRsrcFaHomeType3"), "cdRsrcFaHomeType3")
						.add(Projections.property("cdRsrcFaHomeType4"), "cdRsrcFaHomeType4")
						.add(Projections.property("cdRsrcFaHomeType5"), "cdRsrcFaHomeType5")
						.add(Projections.property("cdRsrcFaHomeType6"), "cdRsrcFaHomeType6")
						.add(Projections.property("cdRsrcFaHomeType7"), "cdRsrcFaHomeType7")
						.add(Projections.property("cdRsrcStatus"), "cdRsrcStatus")
						.add(Projections.property("dtRsrcMarriage"), "dtRsrcMarriage")
						.add(Projections.property("dtRsrcClose"), "dtRsrcClose")
						.add(Projections.property("dtRsrcCert"), "dtRsrcCert")
						.add(Projections.property("indRsrcWriteHist"), "indRsrcWriteHist")
						.add(Projections.property("indRsrcCareProv"), "indRsrcCareProv")
						.add(Projections.property("indRsrcEmergPlace"), "indRsrcEmergPlace")
						.add(Projections.property("indRsrcInactive"), "indRsrcInactive")
						.add(Projections.property("indRsrcTransport"), "indRsrcTransport")
						.add(Projections.property("indRsrcIndivStudy"), "indRsrcIndivStudy")
						.add(Projections.property("indRsrcNonprs"), "indRsrcNonprs")
						.add(Projections.property("nmRsrcLastUpdate"), "nmRsrcLastUpdate")
						.add(Projections.property("nmResource"), "nmResource")
						.add(Projections.property("nmRsrcNameIndex"), "nmRsrcNameIndex")
						.add(Projections.property("nmRsrcContact"), "nmRsrcContact")
						.add(Projections.property("nbrRsrcPhn"), "nbrRsrcPhn")
						.add(Projections.property("nbrRsrcPhoneExt"), "nbrRsrcPhoneExt")
						.add(Projections.property("nbrRsrcFacilCapacity"), "nbrRsrcFacilCapacity")
						.add(Projections.property("nbrRsrcFacilAcclaim"), "nbrRsrcFacilAcclaim")
						.add(Projections.property("nbrRsrcVid"), "nbrRsrcVid")
						.add(Projections.property("nbrRsrcCampusNbr"), "nbrRsrcCampusNbr")
						.add(Projections.property("nbrRsrcIntChildren"), "nbrRsrcIntChildren")
						.add(Projections.property("nbrRsrcIntFeAgeMax"), "nbrRsrcIntFeAgeMax")
						.add(Projections.property("nbrRsrcIntFeAgeMin"), "nbrRsrcIntFeAgeMin")
						.add(Projections.property("nbrRsrcIntMaAgeMax"), "nbrRsrcIntMaAgeMax")
						.add(Projections.property("nbrRsrcIntMaAgeMin"), "nbrRsrcIntMaAgeMin")
						.add(Projections.property("nbrRsrcAnnualIncome"), "nbrRsrcAnnualIncome")
						.add(Projections.property("nbrRsrcFmAgeMax"), "nbrRsrcFmAgeMax")
						.add(Projections.property("nbrRsrcFmAgeMin"), "nbrRsrcFmAgeMin")
						.add(Projections.property("nbrRsrcMaAgeMax"), "nbrRsrcMaAgeMax")
						.add(Projections.property("nbrRsrcMaAgeMin"), "nbrRsrcMaAgeMin")
						.add(Projections.property("nbrRsrcOpenSlots"), "nbrRsrcOpenSlots")
						.add(Projections.property("txtRsrcAddrCmnts"), "txtRsrcAddrCmnts")
						.add(Projections.property("txtRsrcComments"), "txtRsrcComments")
						.add(Projections.property("cdRsrcMhmrCompCode"), "cdRsrcMhmrCompCode")
						.add(Projections.property("dtCclUpdate"), "dtCclUpdate")
						.add(Projections.property("cdRsrcMhmrSite"), "cdRsrcMhmrSite")
						.add(Projections.property("indRsrcContracted"), "indRsrcContracted")
						.add(Projections.property("nmLegal"), "nmLegal")
						.add(Projections.property("cdCertifyEntity"), "cdCertifyEntity")
						.add(Projections.property("indRelativeCrgvr"), "indRelativeCrgvr")
						.add(Projections.property("indFictiveCrgvr"), "indFictiveCrgvr")
						.add(Projections.property("nbrPersons"), "nbrPersons")
						.add(Projections.property("indSignedAgreement"), "indSignedAgreement")
						.add(Projections.property("indIncomeQual"), "indIncomeQual")
						.add(Projections.property("indManualGiven"), "indManualGiven")
						.add(Projections.property("indAllKinEmployed"), "indAllKinEmployed")
						.add(Projections.property("cdRshsFaHomeTypes8"), "cdRshsFaHomeTypes8")
						.add(Projections.property("indRsrcChildSpecific"), "indRsrcChildSpecific")
						.add(Projections.property("cdRsrcTypeSrv"), "cdRsrcTypeSrv")
						.add(Projections.property("indChildSpecificSchedRate"), "indChildSpecificSchedRate")
						.add(Projections.property("indSubsidyOnly"), "indSubsidyOnly")
						.add(Projections.property("cdInvJurisdiction"), "cdInvJurisdiction")
						.add(Projections.property("indMultiLanguage"), "indMultiLanguage")
						.add(Projections.property("nmResourceUpper"), "nmResourceUpper")
						.add(Projections.property("nmPrContactFirst"), "nmPrContactFirst")
						.add(Projections.property("nmPrContactLast"), "nmPrContactLast")
						.add(Projections.property("cdPrContactTitle"), "cdPrContactTitle")
						.add(Projections.property("cdInactiveReason"), "cdInactiveReason")
						.add(Projections.property("txtInactiveComments"), "txtInactiveComments")
						.add(Projections.property("indContractedCare"), "indContractedCare")
						.add(Projections.property("indSpecialContract"), "indSpecialContract")
						.add(Projections.property("indNonprsPca"), "indNonprsPca")
						.add(Projections.property("indUnrelatedCrgvr"), "indUnrelatedCrgvr")
						.add(Projections.property("cdFacilityCareType"), "cdFacilityCareType")
						.add(Projections.property("cdSsccCatchment"), "cdSsccCatchment")
						.add(Projections.property("cdPrfrdCntctMthd"), "cdPrfrdCntctMthd")
						.add(Projections.property("stage.idStage"), "idStage")
						.add(Projections.property("event.idEvent"), "idEvent")
						.add(Projections.property("addrRsrcZip"), "addrRsrcZip"))
				.setResultTransformer(Transformers.aliasToBean(ResourceDto.class));

		return (ResourceDto) cr.uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.subcare.dao.CapsResourceDao#getCapsResourceLink(
	 * java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CapsResourceLinkDto getCapsResourceLink(Long idRsrcLinkChild, String cdRsrcLinkType) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getCapsResourceLinkSql);
		query.addScalar("idResource", StandardBasicTypes.LONG);
		query.addScalar("dtLastUpdateCaps", StandardBasicTypes.DATE);
		query.addScalar("addrRsrcStLn1", StandardBasicTypes.STRING);
		query.addScalar("addrRsrcStLn2", StandardBasicTypes.STRING);
		query.addScalar("addrRsrcCity", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcState", StandardBasicTypes.STRING);
		query.addScalar("addrRsrcZip", StandardBasicTypes.STRING);
		query.addScalar("addrRsrcAttn", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcCnty", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcInvolClosure", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcClosureRsn", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcCampusType", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcCategory", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcCertBy", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcEthnicity", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcFaHomeStatus", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcFaHomeType1", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcFaHomeType2", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcFaHomeType3", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcFaHomeType4", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcFaHomeType5", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcFaHomeType6", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcFaHomeType7", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcLanguage", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcMaintainer", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcMaritalStatus", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcOperBy", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcOwnership", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcPayment", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcRecmndReopen", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcRegion", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcReligion", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcRespite", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcSchDist", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcSetting", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcSourceInquiry", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcStatus", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcType", StandardBasicTypes.STRING);
		query.addScalar("dtRsrcMarriage", StandardBasicTypes.DATE);
		query.addScalar("dtRsrcCert", StandardBasicTypes.DATE);
		query.addScalar("dtRsrcClose", StandardBasicTypes.DATE);
		query.addScalar("idEvent", StandardBasicTypes.LONG);
		query.addScalar("idStage", StandardBasicTypes.LONG);
		query.addScalar("indRsrcCareProv", StandardBasicTypes.STRING);
		query.addScalar("indRsrcEmergPlace", StandardBasicTypes.STRING);
		query.addScalar("indRsrcInactive", StandardBasicTypes.STRING);
		query.addScalar("indRsrcIndivStudy", StandardBasicTypes.STRING);
		query.addScalar("indRsrcNonprs", StandardBasicTypes.STRING);
		query.addScalar("indRsrcTransport", StandardBasicTypes.STRING);
		query.addScalar("indRsrcWriteHist", StandardBasicTypes.STRING);
		query.addScalar("nmRsrcLastUpdate", StandardBasicTypes.STRING);
		query.addScalar("nmResource", StandardBasicTypes.STRING);
		query.addScalar("nmRsrcContact", StandardBasicTypes.STRING);
		query.addScalar("nbrRsrcAnnualIncome", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcCampusNbr", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcFacilAcclaim", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcFacilCapacity", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcFmAgeMax", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcFmAgeMin", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcMaAgeMax", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcMaAgeMin", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcIntChildren", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcIntFeAgeMax", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcIntFeAgeMin", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcIntMaAgeMax", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcIntMaAgeMin", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcOpenSlots", StandardBasicTypes.LONG);
		query.addScalar("nbrRsrcPhn", StandardBasicTypes.STRING);
		query.addScalar("nbrRsrcPhoneExt", StandardBasicTypes.STRING);
		query.addScalar("nbrRsrcVid", StandardBasicTypes.STRING);
		query.addScalar("txtRsrcAddrCmnts", StandardBasicTypes.STRING);
		query.addScalar("txtRsrcComments", StandardBasicTypes.STRING);
		query.addScalar("idRsrcLink", StandardBasicTypes.LONG);
		query.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		query.addScalar("idRsrcLinkParent", StandardBasicTypes.LONG);
		query.addScalar("idRsrcLinkChild", StandardBasicTypes.LONG);
		query.addScalar("cdRsrcLinkService", StandardBasicTypes.STRING);
		query.addScalar("cdRsrcLinkType", StandardBasicTypes.STRING);
		query.setParameter("idRsrcLinkChild", idRsrcLinkChild);
		query.setParameter("cdRsrcLinkType", cdRsrcLinkType);
		query.setResultTransformer(Transformers.aliasToBean(CapsResourceLinkDto.class));
		List<CapsResourceLinkDto> capsResourceLinkDtoList = query.list();
		if (capsResourceLinkDtoList != null && capsResourceLinkDtoList.size() > 0) {
			return capsResourceLinkDtoList.get(0);
		}
		return null;

	}

	/**
	 * Method Name: updateCapsResourceAUD Method Description: Description: This
	 * DAM is used by the Approval Save screen to update specific columns on the
	 * CAPS RESOURCE table. Service Name : CCMN35S; DAM Name: CAUDB3D
	 * 
	 * @param capsResourceDto
	 * @param archInputDto
	 * @return
	 */
	@Override
	public void updateCapsResourceAUD(CapsResourceDto capsResourceDto, ServiceReqHeaderDto archInputDto) {
		log.debug("Entering method updateCapsResourceAUD in CapsResourceDaoImpl");
		switch (archInputDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CapsResource.class);
			criteria.add(Restrictions.eq("stage.idStage", capsResourceDto.getIdStage()));
			CapsResource capsResource = (CapsResource) criteria.uniqueResult();

			if (!ObjectUtils.isEmpty(capsResource) && !ObjectUtils.isEmpty(capsResourceDto)) {
				capsResourceDto.setCdRsrcClosureRsn(capsResource.getCdRsrcClosureRsn());

				if (!ObjectUtils.isEmpty(capsResourceDto.getCdRsrcFaHomeStatus()))
					capsResource.setCdRsrcFaHomeStatus(capsResourceDto.getCdRsrcFaHomeStatus());

				if (!ObjectUtils.isEmpty(capsResourceDto.getCdRsrcStatus()))
					capsResource.setCdRsrcStatus(capsResourceDto.getCdRsrcStatus());

				if (!ObjectUtils.isEmpty(capsResourceDto.getIndRsrcWriteHist()))
					capsResource.setIndRsrcWriteHist(capsResourceDto.getIndRsrcWriteHist());
				Date dtRsrcDate = capsResource.getDtRsrcCert();
				Date dtApprvDet = approversDao.getDateApproversDetermination(capsResourceDto.getIdApproval());
				if (ServiceConstants.HOME_STATUS_APVD_ACT.equals(capsResourceDto.getCdRsrcFaHomeStatus())) {
					if (TypeConvUtil.isNullOrEmpty(dtRsrcDate) || dtRsrcDate.after(dtApprvDet)) {
						capsResource.setDtRsrcCert(dtApprvDet);
					} else {
						capsResource.setDtRsrcCert(dtRsrcDate);
					}
				} else if (ServiceConstants.HOME_CLOSED_STATUS.equals(capsResourceDto.getCdRsrcFaHomeStatus())) {
					if (!TypeConvUtil.isNullOrEmpty(dtRsrcDate) && dtRsrcDate.after(dtApprvDet)) {
						throw new DataLayerException(
								messageSource.getMessage("saveapprovalstatus.dtRsrcCert.invalid", null, Locale.US));
					} else {
						capsResource.setDtRsrcClose(dtApprvDet);
					}
				}
				Criteria criteriaEvent = sessionFactory.getCurrentSession().createCriteria(Event.class);
				criteriaEvent.add(Restrictions.eq("idEvent", capsResourceDto.getIdEvent()));
				Event event = (Event) criteriaEvent.uniqueResult();
				if (TypeConvUtil.isNullOrEmpty(event)) {
					throw new DataNotFoundException(messageSource
							.getMessage("CapsResourveDaoImpl.updateCapsResourceAUD.not.found", null, Locale.US));
				}
				capsResource.setEvent(event);
				Stage stage = stageDao.getStageEntityById(capsResourceDto.getIdStage());
				capsResource.setStage(stage);
				sessionFactory.getCurrentSession().saveOrUpdate(capsResource);
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_ADD:
		case ServiceConstants.REQ_FUNC_CD_DELETE:
		default:
			break;
		}

		log.debug("Exiting method updateCapsResourceAUD in CapsResourceDaoImpl");
	}
}
