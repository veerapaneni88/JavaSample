package us.tx.state.dfps.service.investigation.daoimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Allegation;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.domain.FacilInvstRsrcLink;
import us.tx.state.dfps.common.domain.FacilityInvstDtl;
import us.tx.state.dfps.common.domain.ResourceAddress;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageInDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageOutDto;
import us.tx.state.dfps.service.admin.dto.EventStPerLnkEmpMerRletChkCountOutDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.facility.dto.FacilityInvCnclsnValueDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvCnclsnDetailDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvProviderDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvstRsrcLinkRsrcAddDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.MedicaidMissingDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.ProgramAdminDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.ResourceAddressDto;
import us.tx.state.dfps.service.investigation.dao.FacilityInvCnclsnDao;
import us.tx.state.dfps.service.investigation.dto.FacilAllegInjuryDto;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FacilityInvCnclsnDaoImpl Sep 9, 2017- 10:36:36 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class FacilityInvCnclsnDaoImpl implements FacilityInvCnclsnDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${FacilityInvCnclsnDaoImpl.getInvestigatedFacilityList}")
	private String getInvestigatedFacilityListSql;

	@Value("${FacilityInvCnclsnDaoImpl.getInvestigatedFacility}")
	private String getInvestigatedFacility;

	@Value("${FacilityInvCnclsnDaoImpl.getProgramAdminsAll}")
	private String getProgramAdminsAllSql;

	@Value("${FacilityInvCnclsnDaoImpl.getProgramAdmins}")
	private String getProgramAdminsSql;

	@Value("${FacilityInvCnclsnDaoImpl.getFacilityInstRsrcLinkDtl}")
	private String facilityInstRsrcLinkDtlSql;

	@Value("${FacilityInvCnclsnDaoImpl.getAllegedFacilityDtl}")
	private String allegedFacilityDtlSql;

	/*
	 * @Value("${FacilityInvCnclsnDaoImpl.getRsrcAddressByResourceID}") private
	 * String rsrcAddressByResourceIDSql;
	 */

	@Value("${FacilityInvCnclsnDaoImpl.getFacilityAllegationListDtls}")
	private String facilityAllegationListDtlsSql;

	@Value("${FacilityInvCnclsnDaoImpl.getInvestigatedFacilityListDtls}")
	private String investigatedFacilityListSql;

	@Value("${FacilityInvCnclsnDaoImpl.getInvFaciPrimaryAddressList}")
	private String invFaciPrimaryAddressListSql;

	@Value("${FacilityInvCnclsnDaoImpl.getAllegedVictimsPrimaryAddressList}")
	private String allegedVictimsPrimaryAddressListSql;

	@Value("${FacilityInvCnclsnDaoImpl.getApprovalStatusInfo}")
	private String approvalStatusInfoSql;

	@Value("${FacilityInvCnclsnDaoImpl.getPersMedicaidMissingInd}")
	private String persMedicaidMissingIndSql;

	@Value("${FacilityInvCnclsnDaoImpl.getProgramAdminsAll}")
	private String programAdminsAllSql;

	@Value("${FacilityInvCnclsnDaoImpl.getProgramAdmins}")
	private String programAdminsSql;

	@Value("${FacilityInvCnclsnDaoImpl.reportableConductExists}")
	private String reportableConductExistsSQL;

	@Value("${FacilityInvCnclsnDaoImpl.getMinDateFacilAlleg}")
	private String getMinDateFacilAllegSQL;

	@Value("${FacilityInvCnclsnDaoImpl.getFacilAllegationInjuryDetails}")
	private String getFacilAllegationInjuryDetailsSQL;

	@Value("${FacilityInvCnclsnDaoImpl.getVictimsForStage}")
	private String getVictimsForStageSQL;

	@Value("${EventStPerLnkEmpMerRletChkCountDaoImpl.getMergeCount}")
	private String getMergeCountSQL;

	@Value("${FacilityInvCnclsnDaoImpl.getAdminReviewDetails}")
	private String getAdminReviewDetailsSQL;

	@Value("${FacilityInvCnclsnDaoImpl.getFacilAllegationsForVictim}")
	private String getFacilAllegationsForVictimSQL;

	@Value("${FacilityInvCnclsnDaoImpl.getNameTiedToFacilAllegOfCRC}")
	private String getNameTiedToFacilAllegOfCRCSql;

	@Value("${FacilityInvCnclsnDaoImpl.getResourceAddressBasic}")
	private String getResourceAddressBasicSql;

	@Value("${FacilityInvCnclsnDaoImpl.getResourceAddressAnd}")
	private String getResourceAddressAndSql;

	@Value("${FacilityInvCnclsnDaoImpl.getResourceAddress}")
	private String getResourceAddressSql;

	/**
	 * Method Name: getInvestigatedFacilityList Method Description:This method
	 * is used to get the list of Facilities involved in the Facility
	 * Investigation conclusion.
	 * 
	 * @param stageId
	 *            - The id of the current stage.
	 * @return List<FacilityInvProviderDto> - The list of facility details.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityInvProviderDto> getInvestigatedFacilityList(Long stageId) {

		List<FacilityInvProviderDto> investigatedFacilitiesList = sessionFactory.getCurrentSession()
				.createSQLQuery(getInvestigatedFacilityListSql).addScalar("idFacilRsrcLink", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idFacilResource", StandardBasicTypes.LONG)
				.addScalar("nmFacilInvstFacility", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaintainer", StandardBasicTypes.STRING)
				.addScalar("cdRsrcStatus", StandardBasicTypes.STRING)
				.addScalar("indRsrcWriteHist", StandardBasicTypes.STRING)
				.addScalar("cdFacilInvstOvrallDis", StandardBasicTypes.STRING)
				.addScalar("idRsrcMailAddress", StandardBasicTypes.LONG)
				.addScalar("idRsrcSiteAddress", StandardBasicTypes.LONG)
				.addScalar("cdMhmrCompCode", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(FacilityInvProviderDto.class))
				.setParameter("idStage", stageId).list();

		return investigatedFacilitiesList;

	}

	/**
	 * Method Name: getInvestigatedFacility Method Description: gets the list of
	 * facilities for the given resourceId
	 * 
	 * @param resourceId
	 * @return FacilityInvCnclsnValueDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public FacilityInvCnclsnValueDto getInvestigatedFacility(int allegId) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getInvestigatedFacility)

				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("nmResource", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMhmrCompCode", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CapsResource.class));
		query.setParameter("idAllegation", allegId);
		FacilityInvCnclsnValueDto facilityBean = null;
		List<CapsResource> capsResource = (List<CapsResource>) query.list();
		if (capsResource != null && capsResource.size() > 0)
			for (CapsResource resource : capsResource) {
				facilityBean = new FacilityInvCnclsnValueDto();
				facilityBean.setCdMhmrCompCode(resource.getCdRsrcMhmrCompCode());
				facilityBean.setNmFacilInvstFacility(resource.getNmResource());
				if (resource.getCdRsrcFacilType() != null) {
					facilityBean.setCdRsrcFacilType(resource.getCdRsrcFacilType());
					facilityBean.setIdFacilResource(resource.getIdResource().intValue());
				}
			}
		return facilityBean;

	}

	/**
	 * Method Name: getProgramAdmins Method Description: Retrieve the program
	 * Admins
	 * 
	 * @param idPerson
	 * @ @return List<OptionDto>
	 */
	@SuppressWarnings("unchecked")
	public List<ProgramAdminDto> getProgramAdmins(Long idPerson) {
		Query query = null;
		List<ProgramAdminDto> programAdminDtosList = new LinkedList<ProgramAdminDto>();
		if (!ObjectUtils.isEmpty(idPerson) && 0l != idPerson) {
			query = sessionFactory.getCurrentSession().createSQLQuery(getProgramAdminsAllSql);
			query.setParameter("idPerson", idPerson);
		} else {
			query = sessionFactory.getCurrentSession().createSQLQuery(getProgramAdminsSql);
		}
		programAdminDtosList = (List<ProgramAdminDto>) ((SQLQuery) query).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("firstName", StandardBasicTypes.STRING).addScalar("lastName", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ProgramAdminDto.class)).list();

		return programAdminDtosList;

	}

	/**
	 * Method Name: isDispositionMissing Method Description:Retrieve true if any
	 * of allegation(s) for the input stage is missing a disposition
	 * 
	 * @param idStage
	 * @return Boolean @
	 */
	public Boolean isDispositionMissing(Long idStage) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Allegation.class);
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		criteria.add(Restrictions.isNull("cdAllegDisposition"));
		criteria.setProjection(Projections.rowCount());
		Long rowCount = (Long) criteria.uniqueResult();
		if (rowCount > ServiceConstants.ZERO_VAL) {
			return ServiceConstants.TRUEVAL;
		}
		return ServiceConstants.FALSEVAL;
	}

	/**
	 * Method Description: This Method will retrieve the Facility Invst Resource
	 * link details for the given FacilInvstRsrcLink ID.
	 * 
	 * @param idFacilInvstRsrcLink
	 * @return FacilityInvCnclsnDetailDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityInvstRsrcLinkRsrcAddDto> getInvstRcrcLinkDetails(Long idFacilInvstRsrcLink) {

		List<FacilityInvstRsrcLinkRsrcAddDto> facilityInvstRsrcLinkRsrcAddDtoList = new ArrayList<FacilityInvstRsrcLinkRsrcAddDto>();
		facilityInvstRsrcLinkRsrcAddDtoList = (List<FacilityInvstRsrcLinkRsrcAddDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(facilityInstRsrcLinkDtlSql)
				.setParameter("idFacilInvstRsrcLink", idFacilInvstRsrcLink))
						.addScalar("cdMhmrCompCode", StandardBasicTypes.STRING)
						.addScalar("dtLastUpdated", StandardBasicTypes.DATE)
						.addScalar("cdFacilInvstOvrallDis", StandardBasicTypes.STRING)
						.addScalar("nmFacilInvstFacility", StandardBasicTypes.STRING)
						.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("idFacilRsrcLink", StandardBasicTypes.LONG)
						.addScalar("idFacilResource", StandardBasicTypes.LONG)
						.addScalar("idRsrcMailAddress", StandardBasicTypes.LONG)
						.addScalar("idRsrcSiteAddress", StandardBasicTypes.LONG)
						.addScalar("phoneNumber", StandardBasicTypes.STRING)
						.addScalar("nameFirst", StandardBasicTypes.STRING)
						.addScalar("nameMiddle", StandardBasicTypes.STRING)
						.addScalar("nameLast", StandardBasicTypes.STRING)
						.addScalar("idResource", StandardBasicTypes.LONG)
						.addScalar("idRsrcAddress", StandardBasicTypes.LONG)
						.addScalar("addStreetLine1", StandardBasicTypes.STRING)
						.addScalar("addStreetLine2", StandardBasicTypes.STRING)
						.addScalar("addCity", StandardBasicTypes.STRING)
						.addScalar("addState", StandardBasicTypes.STRING).addScalar("addZip", StandardBasicTypes.STRING)
						.addScalar("addCounty", StandardBasicTypes.STRING)
						.addScalar("addType", StandardBasicTypes.STRING)
						.addScalar("addComments", StandardBasicTypes.STRING)
						.addScalar("nmCnty", StandardBasicTypes.STRING).addScalar("nmCntry", StandardBasicTypes.STRING)
						.addScalar("cdGcdRtrn", StandardBasicTypes.STRING)
						.addScalar("cdAddrRtrn", StandardBasicTypes.STRING)
						.addScalar("nbrGcdLat", StandardBasicTypes.BIG_DECIMAL)
						.addScalar("nbrGcdLong", StandardBasicTypes.BIG_DECIMAL)
						.addScalar("txtMailbltyScore", StandardBasicTypes.STRING)
						.addScalar("indValdtd", StandardBasicTypes.STRING)
						.addScalar("dtValdtd", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(FacilityInvstRsrcLinkRsrcAddDto.class)).list();

		return facilityInvstRsrcLinkRsrcAddDtoList;
	}

	/**
	 * Method Name: getAllegedFacilityIDs Method Description:This Method will
	 * retrieve the list of facilities associated with the allegation for the
	 * given stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return - The list of facility resource ids.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getAllegedFacilityIDs(Long idStage) {

		List<Long> idResource = new ArrayList<Long>();

		Query query = sessionFactory.getCurrentSession().createSQLQuery(allegedFacilityDtlSql)
				.addScalar("ID_RESOURCE", StandardBasicTypes.LONG).setParameter(ServiceConstants.IDSTAGE, idStage);
		if (null != query.list()) {
			idResource = (List<Long>) query.list();
		}
		return idResource;
	}

	/**
	 * Method Description: This Method will retrieve the resource address list
	 * for the given Resource ID.
	 * 
	 * @param idResource
	 * @return List<ResourceAddressDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceAddressDto> getRsrcAddressByResourceID(Long idResource, List<String> facilityTypeCodes) {

		StringBuffer sql = new StringBuffer(getResourceAddressBasicSql);
		if (!CollectionUtils.isEmpty(facilityTypeCodes)) {
			sql = sql.append(" ").append(getResourceAddressAndSql);
		}
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("addStreetLine1", StandardBasicTypes.STRING)
				.addScalar("addStreetLine2", StandardBasicTypes.STRING).addScalar("addCity", StandardBasicTypes.STRING)
				.addScalar("addState", StandardBasicTypes.STRING).addScalar("addZip", StandardBasicTypes.STRING)
				.addScalar("addCounty", StandardBasicTypes.STRING).addScalar("addComments", StandardBasicTypes.STRING)
				.addScalar("cdResourceType", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ResourceAddressDto.class));
		if (!CollectionUtils.isEmpty(facilityTypeCodes)) {
			sqlQuery.setParameterList("facilResourceTypeList", facilityTypeCodes);
		}
		sqlQuery.setParameter("idResource", idResource);

		List<ResourceAddressDto> resourceAddressDtoList = (List<ResourceAddressDto>) sqlQuery.list();
		return resourceAddressDtoList;
	}

	/**
	 * Method Description: This Method will save the resource address details
	 * for Address Type 9.
	 * 
	 * @param resourceAddressDto
	 * @return ResourceAddressDto @
	 */
	@Override
	public ResourceAddressDto saveResourceAddressDtl(ResourceAddressDto resourceAddressDto) {

		ResourceAddress resourceAddressEntity = new ResourceAddress();

		CapsResource capsResourceentity = (CapsResource) sessionFactory.getCurrentSession()
				.createCriteria(CapsResource.class)
				.add(Restrictions.eq("idResource", resourceAddressDto.getIdResource())).uniqueResult();
		resourceAddressEntity.setCapsResource(capsResourceentity);

		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddZip())))
			resourceAddressEntity.setAddrRsrcAddrZip(resourceAddressDto.getAddZip());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddCounty())))
			resourceAddressEntity.setCdRsrcAddrCounty(resourceAddressDto.getAddCounty());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddAttn())))
			resourceAddressEntity.setAddrRsrcAddrAttn(resourceAddressDto.getAddAttn());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddState())))
			resourceAddressEntity.setCdRsrcAddrState(resourceAddressDto.getAddState());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddStreetLine1())))
			resourceAddressEntity.setAddrRsrcAddrStLn1(resourceAddressDto.getAddStreetLine1());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddStreetLine2())))
			resourceAddressEntity.setAddrRsrcAddrStLn2(resourceAddressDto.getAddStreetLine2());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddSchDist())))
			resourceAddressEntity.setCdRsrcAddrSchDist(resourceAddressDto.getAddSchDist());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddType())))
			resourceAddressEntity.setCdRsrcAddrType(resourceAddressDto.getAddType());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddComments())))
			resourceAddressEntity.setTxtRsrcAddrComments(resourceAddressDto.getAddComments());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddrVldtd())))
			resourceAddressEntity.setNbrRsrcAddrVid(resourceAddressDto.getAddrVldtd());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddCity())))
			resourceAddressEntity.setAddrRsrcAddrCity(resourceAddressDto.getAddCity());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getNmCntry())))
			resourceAddressEntity.setNmCntry(resourceAddressDto.getNmCntry());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getNmCnty())))
			resourceAddressEntity.setNmCnty(resourceAddressDto.getNmCnty());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toBigDecimal(resourceAddressDto.getNbrGcdLat())))
			resourceAddressEntity.setNbrGcdLat(resourceAddressDto.getNbrGcdLat());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toBigDecimal(resourceAddressDto.getNbrGcdLong())))
			resourceAddressEntity.setNbrGcdLong(resourceAddressDto.getNbrGcdLong());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getCdAddrRtrn())))
			resourceAddressEntity.setCdAddrRtrn(resourceAddressDto.getCdAddrRtrn());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getCdGcdRtrn())))
			resourceAddressEntity.setCdGcdRtrn(resourceAddressDto.getCdGcdRtrn());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getIndValdtd())))
			resourceAddressEntity.setIndValdtd(resourceAddressDto.getIndValdtd());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getDtValdtd()))) {
			resourceAddressEntity.setDtValdtd(resourceAddressDto.getDtValdtd());
		} else {
			resourceAddressEntity.setDtValdtd(new Date());
		}
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getDtLastUpdate()))) {
			resourceAddressEntity.setDtLastUpdate(resourceAddressDto.getDtLastUpdate());
		} else {
			resourceAddressEntity.setDtLastUpdate(new Date());
		}
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getTxtMailbltyScore())))
			resourceAddressEntity.setTxtMailbltyScore(resourceAddressDto.getTxtMailbltyScore());

		sessionFactory.getCurrentSession().save(resourceAddressEntity);
		resourceAddressDto.setIdRsrcAddress(resourceAddressEntity.getIdRsrcAddress());
		return resourceAddressDto;
	}

	/**
	 * Method Description: This Method will save the facility to the link table
	 * as part of investigation
	 * 
	 * @param facilityInvCnclsnDetailDto
	 * @
	 */
	@Override
	public void saveFacilityRsrcLinkDtl(FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto) {

		FacilInvstRsrcLink facilInvstRsrcLinkEntity = new FacilInvstRsrcLink();

		CapsCase capsCaseEntity = (CapsCase) sessionFactory.getCurrentSession().createCriteria(CapsCase.class)
				.add(Restrictions.eq("idCase", facilityInvCnclsnDetailDto.getIdCase())).uniqueResult();

		Stage stageEntity = (Stage) sessionFactory.getCurrentSession().createCriteria(Stage.class)
				.add(Restrictions.eq("idStage", facilityInvCnclsnDetailDto.getIdStage())).uniqueResult();

		CapsResource capsResourceentity = (CapsResource) sessionFactory.getCurrentSession()
				.createCriteria(CapsResource.class)
				.add(Restrictions.eq("idResource", facilityInvCnclsnDetailDto.getIdFacilResource())).uniqueResult();
		ResourceAddress resourceAddressEntity = new ResourceAddress();
		if (facilityInvCnclsnDetailDto.getIdRsrcMailAddress() > ServiceConstants.ZERO_VAL) {
			resourceAddressEntity = (ResourceAddress) sessionFactory.getCurrentSession()
					.createCriteria(ResourceAddress.class)
					.add(Restrictions.eq("idRsrcAddress", facilityInvCnclsnDetailDto.getIdRsrcMailAddress()))
					.uniqueResult();
		}
		facilInvstRsrcLinkEntity.setCapsCase(capsCaseEntity);
		facilInvstRsrcLinkEntity.setStage(stageEntity);
		facilInvstRsrcLinkEntity.setCapsResource(capsResourceentity);
		facilInvstRsrcLinkEntity.setResourceAddress(resourceAddressEntity);
		if(!ObjectUtils.isEmpty(capsResourceentity) && !ObjectUtils.isEmpty(capsResourceentity.getCdRsrcMhmrCompCode())){
			facilInvstRsrcLinkEntity.setCdMhmrCode(capsResourceentity.getCdRsrcMhmrCompCode());
		}
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(facilityInvCnclsnDetailDto.getIdEvent())))
			facilInvstRsrcLinkEntity.setIdEvent(facilityInvCnclsnDetailDto.getIdEvent());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(facilityInvCnclsnDetailDto.getIdRsrcSiteAddress())))
			facilInvstRsrcLinkEntity.setIdRsrcSiteAddress(facilityInvCnclsnDetailDto.getIdRsrcSiteAddress());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(facilityInvCnclsnDetailDto.getCdFacilInvstOvrallDis())))
			facilInvstRsrcLinkEntity.setCdRsrcOverallDis(facilityInvCnclsnDetailDto.getCdFacilInvstOvrallDis());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(facilityInvCnclsnDetailDto.getCdMhmrCompCode())))
			facilInvstRsrcLinkEntity.setCdMhmrCode(facilityInvCnclsnDetailDto.getCdMhmrCompCode());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(facilityInvCnclsnDetailDto.getNameFirst())))
			facilInvstRsrcLinkEntity.setNmFcltyAdminFirst(facilityInvCnclsnDetailDto.getNameFirst());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(facilityInvCnclsnDetailDto.getNameMiddle())))
			facilInvstRsrcLinkEntity.setNmFcltyAdminMiddle(facilityInvCnclsnDetailDto.getNameMiddle());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(facilityInvCnclsnDetailDto.getNameLast())))
			facilInvstRsrcLinkEntity.setNmFcltyAdminLast(facilityInvCnclsnDetailDto.getNameLast());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(facilityInvCnclsnDetailDto.getDtLastUpdated()))) {
			facilInvstRsrcLinkEntity.setDtLastUpdate(facilityInvCnclsnDetailDto.getDtLastUpdated());
		} else {
			facilInvstRsrcLinkEntity.setDtLastUpdate(new Date());
		}
		sessionFactory.getCurrentSession().save(facilInvstRsrcLinkEntity);
	}

	/**
	 * Method Description: This Method will update the resource address details.
	 * 
	 * @param resourceAddressDto
	 * @
	 */
	@Override
	public void updateResourceAddressDtl(ResourceAddressDto resourceAddressDto) {

		ResourceAddress resourceAddressEntity = (ResourceAddress) sessionFactory.getCurrentSession()
				.createCriteria(ResourceAddress.class)
				.add(Restrictions.eq("idRsrcAddress", resourceAddressDto.getIdRsrcAddress())).uniqueResult();
		CapsResource capsResourceentity = (CapsResource) sessionFactory.getCurrentSession()
				.createCriteria(CapsResource.class)
				.add(Restrictions.eq("idResource", resourceAddressDto.getIdResource())).uniqueResult();
		resourceAddressEntity.setCapsResource(capsResourceentity);

		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddZip())))
			resourceAddressEntity.setAddrRsrcAddrZip(resourceAddressDto.getAddZip());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddCounty())))
			resourceAddressEntity.setCdRsrcAddrCounty(resourceAddressDto.getAddCounty());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddState())))
			resourceAddressEntity.setCdRsrcAddrState(resourceAddressDto.getAddState());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddStreetLine1())))
			resourceAddressEntity.setAddrRsrcAddrStLn1(resourceAddressDto.getAddStreetLine1());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddStreetLine2())))
			resourceAddressEntity.setAddrRsrcAddrStLn2(resourceAddressDto.getAddStreetLine2());
		;
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddType())))
			resourceAddressEntity.setCdRsrcAddrType(resourceAddressDto.getAddType());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddComments())))
			resourceAddressEntity.setTxtRsrcAddrComments(resourceAddressDto.getAddComments());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getAddCity())))
			resourceAddressEntity.setAddrRsrcAddrCity(resourceAddressDto.getAddCity());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getNmCntry())))
			resourceAddressEntity.setNmCntry(resourceAddressDto.getNmCntry());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getNmCnty())))
			resourceAddressEntity.setNmCnty(resourceAddressDto.getNmCnty());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toBigDecimal(resourceAddressDto.getNbrGcdLat())))
			resourceAddressEntity.setNbrGcdLat(resourceAddressDto.getNbrGcdLat());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toBigDecimal(resourceAddressDto.getNbrGcdLong())))
			resourceAddressEntity.setNbrGcdLong(resourceAddressDto.getNbrGcdLong());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getCdAddrRtrn())))
			resourceAddressEntity.setCdAddrRtrn(resourceAddressDto.getCdAddrRtrn());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getCdGcdRtrn())))
			resourceAddressEntity.setCdGcdRtrn(resourceAddressDto.getCdGcdRtrn());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getIndValdtd())))
			resourceAddressEntity.setIndValdtd(resourceAddressDto.getIndValdtd());
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getDtValdtd()))) {
			resourceAddressEntity.setDtValdtd(resourceAddressDto.getDtValdtd());
		} else {
			resourceAddressEntity.setDtValdtd(new Date());
		}
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getDtLastUpdate()))) {
			resourceAddressEntity.setDtLastUpdate(new Date());
		} else {
			resourceAddressEntity.setDtLastUpdate(new Date());
		}
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(resourceAddressDto.getTxtMailbltyScore())))
			resourceAddressEntity.setTxtMailbltyScore(resourceAddressDto.getTxtMailbltyScore());

		sessionFactory.getCurrentSession().saveOrUpdate(resourceAddressEntity);

	}

	/**
	 * Method Description: This Method will update the facility to the link
	 * table as part of investigation
	 * 
	 * @param facilityInvCnclsnDetailDto
	 * @
	 */
	@Override
	public void updateFacilityRsrcLinkDtl(FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto) {

		FacilInvstRsrcLink facilInvstRsrcLinkEntity = (FacilInvstRsrcLink) sessionFactory.getCurrentSession()
				.createCriteria(FacilInvstRsrcLink.class)
				.add(Restrictions.eq("idFacilInvstRsrcLink", facilityInvCnclsnDetailDto.getIdFacilRsrcLink()))
				.uniqueResult();

		CapsCase capsCaseEntity = (CapsCase) sessionFactory.getCurrentSession().createCriteria(CapsCase.class)
				.add(Restrictions.eq("idCase", facilityInvCnclsnDetailDto.getIdCase())).uniqueResult();

		Stage stageEntity = (Stage) sessionFactory.getCurrentSession().createCriteria(Stage.class)
				.add(Restrictions.eq("idStage", facilityInvCnclsnDetailDto.getIdStage())).uniqueResult();

		CapsResource capsResourceentity = (CapsResource) sessionFactory.getCurrentSession()
				.createCriteria(CapsResource.class)
				.add(Restrictions.eq("idResource", facilityInvCnclsnDetailDto.getIdFacilResource())).uniqueResult();
		ResourceAddress resourceAddressEntity = new ResourceAddress();
		if (facilityInvCnclsnDetailDto.getIdRsrcMailAddress() > ServiceConstants.ZERO_VAL) {
			resourceAddressEntity = (ResourceAddress) sessionFactory.getCurrentSession()
					.createCriteria(ResourceAddress.class)
					.add(Restrictions.eq("idRsrcAddress", facilityInvCnclsnDetailDto.getIdRsrcMailAddress()))
					.uniqueResult();
		}
		facilInvstRsrcLinkEntity.setCapsCase(capsCaseEntity);
		facilInvstRsrcLinkEntity.setStage(stageEntity);
		facilInvstRsrcLinkEntity.setCapsResource(capsResourceentity);
		facilInvstRsrcLinkEntity.setResourceAddress(resourceAddressEntity);

		facilInvstRsrcLinkEntity.setCdRsrcOverallDis(facilityInvCnclsnDetailDto.getCdFacilInvstOvrallDis());
		//Commented the code for warranty defect 12570
		//facilInvstRsrcLinkEntity.setCdMhmrCode(facilityInvCnclsnDetailDto.getCdMhmrCompCode());
		facilInvstRsrcLinkEntity.setNmFcltyAdminFirst(facilityInvCnclsnDetailDto.getNameFirst());
		facilInvstRsrcLinkEntity.setNmFcltyAdminMiddle(facilityInvCnclsnDetailDto.getNameMiddle());
		facilInvstRsrcLinkEntity.setNmFcltyAdminLast(facilityInvCnclsnDetailDto.getNameLast());

		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(facilityInvCnclsnDetailDto.getDtLastUpdated()))) {
			facilInvstRsrcLinkEntity.setDtLastUpdate(new Date());
		} else {
			facilInvstRsrcLinkEntity.setDtLastUpdate(new Date());
		}
		sessionFactory.getCurrentSession().saveOrUpdate(facilInvstRsrcLinkEntity);
	}

	/**
	 * Method Description: This Method will delete the facility from the link
	 * table as part of investigation
	 * 
	 * @param facilityInvCnclsnDetailDto
	 * @
	 */
	@Override
	public void deleteFacilityRsrcLinkDtl(FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto) {

		FacilInvstRsrcLink facilInvstRsrcLinkEntity = (FacilInvstRsrcLink) sessionFactory.getCurrentSession()
				.createCriteria(FacilInvstRsrcLink.class)
				.add(Restrictions.eq("idFacilInvstRsrcLink", facilityInvCnclsnDetailDto.getIdFacilRsrcLink()))
				.uniqueResult();

		sessionFactory.getCurrentSession().delete(facilInvstRsrcLinkEntity);

	}

	/**
	 * Method Description: This Method will initialize Facilities OverallDispo
	 * in the Facility link table
	 * 
	 * @param idSatge
	 * @
	 */
	@Override
	public void initializeFacilitiesOverallDispo(Long idSatge) {

		FacilInvstRsrcLink facilInvstRsrcLinkEntity = (FacilInvstRsrcLink) sessionFactory.getCurrentSession()
				.createCriteria(FacilInvstRsrcLink.class).add(Restrictions.eq(ServiceConstants.STAGE_IDSTAGE, idSatge))
				.list().stream().findAny().orElse(null);
		if (!ObjectUtils.isEmpty(facilInvstRsrcLinkEntity)) {
			facilInvstRsrcLinkEntity.setCdRsrcOverallDis(null);
			sessionFactory.getCurrentSession().saveOrUpdate(facilInvstRsrcLinkEntity);
		}

	}

	/**
	 * Method Name: getFacilityAllegationListDetails Method Description:This
	 * Method will retrieve the facility and allegation details associated with
	 * the Stage
	 * 
	 * @param idSatge
	 *            - The id of the current stage.
	 * @return List<FacilityAllegationDto> - The list of facility allegation
	 *         details.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityAllegationDto> getFacilityAllegationListDetails(Long idSatge) {

		List<FacilityAllegationDto> facilityAllegationDtoList = new ArrayList<FacilityAllegationDto>();
		facilityAllegationDtoList = (List<FacilityAllegationDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(facilityAllegationListDtlsSql).setParameter("idStage", idSatge))
						.addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("idAllegationStage", StandardBasicTypes.LONG)
						.addScalar("idAllegation", StandardBasicTypes.LONG)
						.addScalar("idFacilResource", StandardBasicTypes.LONG)
						.addScalar("idFacilRsrcLink", StandardBasicTypes.LONG)
						.addScalar("cdMhmrCompCode", StandardBasicTypes.STRING)
						.addScalar("cdAllegDispo", StandardBasicTypes.STRING)
						.addScalar("dtLastUpdated", StandardBasicTypes.DATE)
						.addScalar("cdFacilAllegClass", StandardBasicTypes.STRING)
						.addScalar("cdFacilAllegDispSupr", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(FacilityAllegationDto.class)).list();

		return facilityAllegationDtoList;

	}

	/**
	 * Method Description: This Method will delete the Resource Address delete
	 * from the Resource table as part of investigation
	 * 
	 * @param facilityInvCnclsnDetailDto
	 * @
	 */
	@Override
	public void deleteResourceAddressDtl(Long idRsrcAdress) {

		ResourceAddress resourceAddressEntity = (ResourceAddress) sessionFactory.getCurrentSession()
				.createCriteria(ResourceAddress.class).add(Restrictions.eq("idRsrcAddress", idRsrcAdress))
				.uniqueResult();

		sessionFactory.getCurrentSession().delete(resourceAddressEntity);

	}

	/**
	 * Method Name: getInvFacilPrimaryAddressList Method Description:This method
	 * is used to retrieve the address of the facilities involved in the
	 * Facility Investigation conclusion.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return List<ResourceAddressDto> - The list of resource address details.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceAddressDto> getInvFacilPrimaryAddressList(Long idStage) {

		List<ResourceAddressDto> resourceAddressDtoList = (List<ResourceAddressDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(invFaciPrimaryAddressListSql).setParameter("idStage", idStage)
				.setParameter("cdRsrcFacilTypeA", CodesConstant.CFACTYP4_16)
				.setParameter("cdRsrcFacilTypeB", CodesConstant.CFACTYP4_17))
						.addScalar("idRsrcAddress", StandardBasicTypes.LONG)
						.addScalar("idResource", StandardBasicTypes.LONG)
						.addScalar("addCity", StandardBasicTypes.STRING)
						.addScalar("addStreetLine1", StandardBasicTypes.STRING)
						.addScalar("addStreetLine2", StandardBasicTypes.STRING)
						.addScalar("addState", StandardBasicTypes.STRING).addScalar("addZip", StandardBasicTypes.STRING)
						.addScalar("addCounty", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(ResourceAddressDto.class)).list();

		return resourceAddressDtoList;
	}

	/**
	 * Method Name: getAllegedVictimsPrimaryAddressList Method Description:This
	 * method is used to retrieve the primary address of the victims in the
	 * facility allegations in the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return - The list of resource address details.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceAddressDto> getAllegedVictimsPrimaryAddressList(Long idStage) {

		List<ResourceAddressDto> resourceAddressDtoList = (List<ResourceAddressDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(allegedVictimsPrimaryAddressListSql)
				.setParameter("idStage", idStage).setParameter("cdPersAddrLinkType", CodesConstant.CADDRTYP_FC))
						.addScalar("addCity", StandardBasicTypes.STRING)
						.addScalar("addStreetLine1", StandardBasicTypes.STRING)
						.addScalar("addStreetLine2", StandardBasicTypes.STRING)
						.addScalar("addState", StandardBasicTypes.STRING).addScalar("addZip", StandardBasicTypes.STRING)
						.addScalar("addCounty", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(ResourceAddressDto.class)).list();

		return resourceAddressDtoList;
	}

	/**
	 * Method Name: getCurrentPriority Method Description:This Method will
	 * retrieve the current priority associated with the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return String - The current priority of the stage.
	 */
	@Override
	public String getCurrentPriority(Long idStage) {

		String stageCurrPriority = ServiceConstants.EMPTY_STRING;
		StageDto stageDto = (StageDto) sessionFactory.getCurrentSession().createCriteria(Stage.class)
				.setProjection(Projections.projectionList().add(Projections.property("cdStageCurrPriority"),
						"cdStageCurrPriority"))
				.add(Restrictions.eq("idStage", idStage)).setResultTransformer(Transformers.aliasToBean(StageDto.class))
				.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(stageDto)) {
			stageCurrPriority = stageDto.getCdStageCurrPriority();
		}
		return stageCurrPriority;
	}

	/**
	 * Method Name: getApprovalStatusInfo Method Description:This Method will
	 * get the approval information using id event.
	 * 
	 * @param idEvent
	 *            - The id of the facility conclusion event.
	 * @return FacilityInvCnclsnDetailDto - The dto will the approval details.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public FacilityInvCnclsnDetailDto getApprovalStatusInfo(Long idEvent) {
		FacilityInvCnclsnDetailDto facilityInvCnclsnDetailDto = new FacilityInvCnclsnDetailDto();
		
		//Fix for defect 12857 - INC000004968284 - PI - 8888 Error accessing Investigation Conclusion
		List<FacilityInvCnclsnDetailDto> facilityInvCnclsnDetailDtoList = (List<FacilityInvCnclsnDetailDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(approvalStatusInfoSql).setParameter("idEvent", idEvent))
						.addScalar("cdApproversStatus", StandardBasicTypes.STRING)
						.addScalar("dtApproversDetermination", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(FacilityInvCnclsnDetailDto.class)).list();

		if(!CollectionUtils.isEmpty(facilityInvCnclsnDetailDtoList)) {
			facilityInvCnclsnDetailDto = facilityInvCnclsnDetailDtoList.get(facilityInvCnclsnDetailDtoList.size() - 1);
		}
		
		return facilityInvCnclsnDetailDto;
	}

	/**
	 * Method Description: This Method will queries the stage person link table
	 * for all clients with person type PRN and rel int SL or OV then queries
	 * the person_id table to check if each of them have an active Medicaid
	 * identifier It returns a map of person id keys and an indicator (Y or N)
	 * for each person indicating if Medicaid identifier is missing.
	 * 
	 * @param idStage
	 * @return Map<Integer, String> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, String> getPersMedicaidMissingInd(Long idStage) {

		Map<Long, String> victimMedMissingMap = new HashMap<Long, String>();

		List<MedicaidMissingDto> medicaidMissingDtoList = (List<MedicaidMissingDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(persMedicaidMissingIndSql).setParameter("idStage", idStage))
						.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("checkMed", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(MedicaidMissingDto.class)).list();

		if (!CollectionUtils.isEmpty(medicaidMissingDtoList)) {
			for (MedicaidMissingDto medicaidMissingDto : medicaidMissingDtoList) {
				if (!victimMedMissingMap.containsKey(medicaidMissingDto.getIdPerson())) {
					victimMedMissingMap.put(medicaidMissingDto.getIdPerson(), medicaidMissingDto.getCheckMed());
				}
			}
		}
		return victimMedMissingMap;
	}

	/**
	 * Method Name: getProgramAdmins Method Description:This method is used to
	 * retrieve the program admins for the New EMR Investigation.
	 * 
	 * @param idPerson
	 *            - The id of program admin
	 * @return List<ProgramAdminDto> - The list of Program Admin details.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, String> getProgramAdminDetails(Long idPerson) {

		Map<Integer, String> programAdminMap = new HashMap<Integer, String>();
		List<ProgramAdminDto> programAdminDtoList = new ArrayList<ProgramAdminDto>();
		if (idPerson != ServiceConstants.ZERO_VAL && idPerson != null) {
			programAdminDtoList = (List<ProgramAdminDto>) ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(programAdminsAllSql).setParameter("idPerson", idPerson))
							.addScalar("idPerson", StandardBasicTypes.LONG)
							.addScalar("firstName", StandardBasicTypes.STRING)
							.addScalar("lastName", StandardBasicTypes.STRING)
							.setResultTransformer(Transformers.aliasToBean(ProgramAdminDto.class)).list();
		} else {
			programAdminDtoList = (List<ProgramAdminDto>) ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(programAdminsSql)).addScalar("idPerson", StandardBasicTypes.LONG)
							.addScalar("firstName", StandardBasicTypes.STRING)
							.addScalar("lastName", StandardBasicTypes.STRING)
							.setResultTransformer(Transformers.aliasToBean(ProgramAdminDto.class)).list();
		}
		if (programAdminDtoList.size() > ServiceConstants.ZERO_SHORT) {
			for (ProgramAdminDto programAdminDto : programAdminDtoList) {
				String personName = programAdminDto.getFirstName().concat(ServiceConstants.COMMA)
						.concat(programAdminDto.getLastName());
				programAdminMap.put(programAdminDto.getIdPerson().intValue(), personName);
			}

		}
		return programAdminMap;
	}

	/**
	 * Method Name: getReportableConductExists Method Description:This method is
	 * used to check if reportable conduct is selected in any of the facility
	 * allegations in the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return Boolean - The boolean value to indicate if reportable conduct
	 *         exists or not.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean getReportableConductExists(Long idStage) {
		boolean isExists = false;
		List<FacilityAllegationDto> list = sessionFactory.getCurrentSession().createSQLQuery(reportableConductExistsSQL)
				.addScalar("cdAllegDispo").setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(FacilityAllegationDto.class)).list();
		if (!CollectionUtils.isEmpty(list)) {
			for (FacilityAllegationDto allegation : list) {
				if (CodesConstant.CDISPSTN_CRC.equals(allegation.getCdAllegDispo())
						|| CodesConstant.CDISPSTN_VRC.equals(allegation.getCdAllegDispo())) {
					isExists = true;
				}
			}
		}
		return isExists;
	}

	/**
	 * Method Name: getFacilityInvestigationDetail Method Description:This
	 * method is used to retrieve the facility investigation details.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @return FacilInvDtlDto - The dto will have the facility investigation
	 *         details.
	 */
	@Override
	public FacilInvDtlDto getFacilityInvestigationDetail(Long idStage) {

		FacilityInvstDtl facilityInvstDtl = (FacilityInvstDtl) sessionFactory.getCurrentSession()
				.createCriteria(FacilityInvstDtl.class).add(Restrictions.eq("stage.idStage", idStage)).uniqueResult();
		FacilInvDtlDto facilInvDtlDto = new FacilInvDtlDto();
		BeanUtils.copyProperties(facilityInvstDtl, facilInvDtlDto);
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getCapsResourceByIdAffilResource())
				&& !ObjectUtils.isEmpty(facilityInvstDtl.getCapsResourceByIdAffilResource().getIdResource())) {
			facilInvDtlDto.setIdAffilResource(facilityInvstDtl.getCapsResourceByIdAffilResource().getIdResource());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getCapsResourceByIdFacilResource())
				&& !ObjectUtils.isEmpty(facilityInvstDtl.getCapsResourceByIdFacilResource().getIdResource())) {
			facilInvDtlDto.setIdFacilResource(facilityInvstDtl.getCapsResourceByIdFacilResource().getIdResource());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getStage())
				&& !ObjectUtils.isEmpty(facilityInvstDtl.getStage().getIdStage())) {
			facilInvDtlDto.setIdStage(facilityInvstDtl.getStage().getIdStage());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getIndStreamlined())) {
			facilInvDtlDto.setIndFacilStreamlined(facilityInvstDtl.getIndStreamlined());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getIndFacilSuperintNotif())) {
			facilInvDtlDto.setIndFacilSuperintNotif(facilityInvstDtl.getIndFacilSuperintNotif());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getIdPrgrmAdminPerson())) {
			facilInvDtlDto.setIdPrgrmAdminPrsn(facilityInvstDtl.getIdPrgrmAdminPerson());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getCdFacilInvstMhmrCode())) {
			facilInvDtlDto.setCdMhmrCompCode(facilityInvstDtl.getCdFacilInvstMhmrCode());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getCdIncidentLocation())) {
			facilInvDtlDto.setCdLocationOfIncident(facilityInvstDtl.getCdIncidentLocation());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getCdPriorCaseHistory())) {
			facilInvDtlDto.setCdPriorCaseHistRev(facilityInvstDtl.getCdPriorCaseHistory());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getNmFacilInvstFacility())) {
			facilInvDtlDto.setNmFacilinvstFacility(facilityInvstDtl.getNmFacilInvstFacility());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getNmFacilInvstAff())) {
			facilInvDtlDto.setNmFacilinvstAff(facilityInvstDtl.getNmFacilInvstAff());
		}

		if (!ObjectUtils.isEmpty(facilityInvstDtl.getNbrFacilInvstPhone())) {
			facilInvDtlDto.setNbrFacilinvstPhone(facilityInvstDtl.getNbrFacilInvstPhone());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getNbrFacilInvstExtension())) {
			facilInvDtlDto.setNbrFacilinvstExtension(facilityInvstDtl.getNbrFacilInvstExtension());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getNbrFacilInvstAffilPhn())) {
			facilInvDtlDto.setNbrFacilinvstAffilPhn(facilityInvstDtl.getNbrFacilInvstAffilPhn());
		}
		if (!ObjectUtils.isEmpty(facilityInvstDtl.getNbrFacilInvstAffilExt())) {
			facilInvDtlDto.setNbrFacilinvstAffilExt(facilityInvstDtl.getNbrFacilInvstAffilExt());
		}

		return facilInvDtlDto;
	}

	/**
	 * Method Name: getFaceToFaceContact Method Description:This method is used
	 * to retrieve the contact details of Face-To-Face contact in the stage.
	 * 
	 * @param contactDto
	 *            - The dto with the input paramters to fetch the face-to-face
	 *            contact details.
	 * @return ContactDto - The dto with the contact details.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ContactDto getFaceToFaceContact(ContactDto request) {
		ContactDto contactDto = new ContactDto();
		//Defect#12755 Check both Initial Face to Face and Initial Contact to show the contact date in facility investigation conclusion page
		List<String> contactType = Arrays.asList(CodesConstant.CCNTCTYP_EINC,CodesConstant.CCNTCTYP_EIFF);
		List<Contact> contactList = sessionFactory.getCurrentSession().createCriteria(Contact.class)
				.add(Restrictions.eq("stage.idStage", request.getIdContactStage()))
				.add(Restrictions.eq("cdContactPurpose", request.getCdContactPurpose()))
				.add(Restrictions.in("cdContactType", contactType))
				//.add(Restrictions.eq("cdContactMethod", CodesConstant.CCNTMETH_FTF)) artf163733 ALM#15472 Initial Contact Not Populating on Conc.	
				.addOrder(Order.asc("dtContactOccurred")).list();
		if (!CollectionUtils.isEmpty(contactList)) {
			BeanUtils.copyProperties(contactList.get(0), contactDto);

		}
		return contactDto;
	}

	/**
	 * Method Name: getMinDateFacilityAllegation Method Description:This method
	 * is used to retrieve the earliest facility allegation date for the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return Date - The date when the allegation occurred.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Date getMinDateFacilityAllegation(Long idStage) {
		Date minFacilityAllegationDate = null;
		List<Date> dateList = sessionFactory.getCurrentSession().createSQLQuery(getMinDateFacilAllegSQL)
				.addScalar("facilityAllegationDate", StandardBasicTypes.TIMESTAMP).setParameter("idStage", idStage)
				.list();
		if (!CollectionUtils.isEmpty(dateList)) {
			minFacilityAllegationDate = dateList.get(0);
		}
		return minFacilityAllegationDate;
	}

	/**
	 * Method Name: getFacilityInjuryDetails Method Description:This method is
	 * used to retrieve the list of Injury details in the Facility Allegations
	 * in the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return List<FacilAllegInjuryDto> - The list of injury details.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FacilAllegInjuryDto> getFacilityInjuryDetails(Long idStage) {
		List<FacilAllegInjuryDto> list = new ArrayList<FacilAllegInjuryDto>();

		list = sessionFactory.getCurrentSession().createSQLQuery(getFacilAllegationInjuryDetailsSQL)
				.addScalar("dtFacilInjuryDtrmntn", StandardBasicTypes.TIMESTAMP)
				.addScalar("idFacilityInjury", StandardBasicTypes.LONG).addScalar("cdFacilInjuryType")
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(FacilAllegInjuryDto.class)).list();
		return list;

	}

	/**
	 * Method Name: getVictimsForStage Method Description:This method is used to
	 * get the list of person details who were entered as victims in the stage.
	 * 
	 * @param idStage
	 *            - The id of the current stage.
	 * @return List<StagePersonLinkDto> - The list of stage person link details.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StagePersonLinkDto> getVictimsForStage(Long idStage) {
		List<StagePersonLinkDto> list = new ArrayList<StagePersonLinkDto>();
		list = sessionFactory.getCurrentSession().createSQLQuery(getVictimsForStageSQL)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdStagePersSearchInd")
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkDto.class)).list();
		return list;
	}

	/**
	 * Method Name: getMergeCount Method Description:This method is used to get
	 * the count of Person Merge for a particular person.
	 * 
	 * @param idPerson
	 *            - The id of the person.
	 * @return Long - The merge count number.
	 */
	@Override
	public Long getMergeCount(Long idPerson) {
		SQLQuery sQLQuery4 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getMergeCountSQL)
				.addScalar("mergeCount", StandardBasicTypes.LONG).setParameter("hI_ulIdPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(EventStPerLnkEmpMerRletChkCountOutDto.class)));
		EventStPerLnkEmpMerRletChkCountOutDto perLnkEmpMerRletChkCountOutDto = (EventStPerLnkEmpMerRletChkCountOutDto) sQLQuery4
				.uniqueResult();
		Long mergeCount = !ObjectUtils.isEmpty(perLnkEmpMerRletChkCountOutDto)
				? perLnkEmpMerRletChkCountOutDto.getMergeCount() : ServiceConstants.ZERO;
		return mergeCount;
	}

	/**
	 * Method Name: getAdminReviewDetails Method Description:This method is used
	 * to retrieve the ARI details.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @return AdminReviewDto - The dto will have the ARI details.
	 */
	@Override
	public AdminReviewDto getAdminReviewDetails(Long idStage) {

		AdminReviewDto adminReviewDto = (AdminReviewDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getAdminReviewDetailsSQL).setParameter("idStage", idStage))
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("idStageRelated", StandardBasicTypes.LONG)
						.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("cdAdminRvAppealResult", StandardBasicTypes.STRING)
						.addScalar("cdAdminRvAppealType", StandardBasicTypes.STRING)
						.addScalar("cdAdminRvAuth", StandardBasicTypes.STRING)
						.addScalar("cdAdminRvStatus", StandardBasicTypes.STRING)
						.addScalar("dtAdminRvAppealNotif", StandardBasicTypes.DATE)
						.addScalar("dtAdminRvAppealReview", StandardBasicTypes.DATE)
						.addScalar("dtAdminRvDue", StandardBasicTypes.DATE)
						.addScalar("dtAdminRvEmgcyRel", StandardBasicTypes.DATE)
						.addScalar("dtAdminRvHearing", StandardBasicTypes.DATE)
						.addScalar("dtAdminRvReqAppeal", StandardBasicTypes.DATE)
						.addScalar("indAdminRvEmgcyRel", StandardBasicTypes.STRING)
						.addScalar("cdStageType", StandardBasicTypes.STRING)
						.addScalar("idUnit", StandardBasicTypes.LONG).addScalar("idSituation", StandardBasicTypes.LONG)
						.addScalar("dtStageClose", StandardBasicTypes.DATE)
						.addScalar("cdClassification", StandardBasicTypes.STRING)
						.addScalar("currentPriority", StandardBasicTypes.STRING)
						.addScalar("cdInitialPriority", StandardBasicTypes.STRING)
						.addScalar("cdReasonPriorityChanged", StandardBasicTypes.STRING)
						.addScalar("cdReasonClosed", StandardBasicTypes.STRING)
						.addScalar("indStageClose", StandardBasicTypes.STRING)
						.addScalar("cdStageCounty", StandardBasicTypes.STRING)
						.addScalar("nmStage", StandardBasicTypes.STRING)
						.addScalar("cdRegion", StandardBasicTypes.STRING)
						.addScalar("dtStageStart", StandardBasicTypes.DATE)
						.addScalar("cdStageProgram", StandardBasicTypes.STRING)
						.addScalar("cdStage", StandardBasicTypes.STRING)
						.addScalar("priorityComments", StandardBasicTypes.STRING)
						.addScalar("closureComments", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(AdminReviewDto.class)).uniqueResult();

		return adminReviewDto;
	}

	/**
	 * Method Name: updateFacilityInvCnclsnDetails Method Description: This
	 * method is used to update the facility investigation details.
	 * 
	 * @param facilInvDtlDto
	 *            - The dto with the facility investigation details which has to
	 *            be updated in the db.
	 * @return FacilInvDtlDto - The dto with the updated last update date and
	 *         other facility investigation details .
	 * @throws Exception
	 */
	@Override
	public FacilInvDtlDto updateFacilityInvCnclsnDetails(FacilInvDtlDto facilInvDtlDto) throws Exception {
		FacilityInvstDtl facilityInvstDtl = (FacilityInvstDtl) sessionFactory.getCurrentSession()
				.createCriteria(FacilityInvstDtl.class)
				.add(Restrictions.eq("stage.idStage", facilInvDtlDto.getIdStage()))
				.add(Restrictions.eq("dtLastUpdate", facilInvDtlDto.getDtLastUpdate())).uniqueResult();

		if (ObjectUtils.isEmpty(facilityInvstDtl)) {
			throw new Exception("Save Failed: The data has been modified by another user. Exit and try again.");
		} else {

			BeanUtils.copyProperties(facilInvDtlDto, facilityInvstDtl);
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getIdAffilResource())) {
				CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
						facilInvDtlDto.getIdAffilResource());
				facilityInvstDtl.setCapsResourceByIdAffilResource(capsResource);
			}
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getIdFacilResource())) {
				CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
						facilInvDtlDto.getIdFacilResource());
				facilityInvstDtl.setCapsResourceByIdFacilResource(capsResource);
			}
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getIdStage())) {
				Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, facilInvDtlDto.getIdStage());
				facilityInvstDtl.setStage(stage);
			}
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getIndFacilStreamlined())) {
				facilityInvstDtl.setIndStreamlined(facilInvDtlDto.getIndFacilStreamlined());
			} else {
				facilityInvstDtl.setIndStreamlined(ServiceConstants.N);
			}
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getIndFacilSuperintNotif())) {
				facilityInvstDtl.setIndFacilSuperintNotif(facilInvDtlDto.getIndFacilSuperintNotif());
			} else {
				facilityInvstDtl.setIndFacilSuperintNotif(ServiceConstants.N);
			}
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getIdPrgrmAdminPrsn())) {
				facilityInvstDtl.setIdPrgrmAdminPerson(facilInvDtlDto.getIdPrgrmAdminPrsn());
			}
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getCdMhmrCompCode())) {
				facilityInvstDtl.setCdFacilInvstMhmrCode(facilInvDtlDto.getCdMhmrCompCode());
			}

			facilityInvstDtl.setCdIncidentLocation(facilInvDtlDto.getCdLocationOfIncident());

			if (!ObjectUtils.isEmpty(facilInvDtlDto.getCdPriorCaseHistRev())) {
				facilityInvstDtl.setCdPriorCaseHistory(facilInvDtlDto.getCdPriorCaseHistRev());
			}
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getNmFacilinvstFacility())) {
				facilityInvstDtl.setNmFacilInvstFacility(facilInvDtlDto.getNmFacilinvstFacility());
			}
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getNmFacilinvstAff())) {
				facilityInvstDtl.setNmFacilInvstAff(facilInvDtlDto.getNmFacilinvstAff());
			}

			if (!ObjectUtils.isEmpty(facilInvDtlDto.getNbrFacilinvstPhone())) {
				facilityInvstDtl.setNbrFacilInvstPhone(facilInvDtlDto.getNbrFacilinvstPhone());
			}
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getNbrFacilinvstExtension())) {
				facilityInvstDtl.setNbrFacilInvstExtension(facilInvDtlDto.getNbrFacilinvstExtension());
			}
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getNbrFacilinvstAffilPhn())) {
				facilityInvstDtl.setNbrFacilInvstAffilPhn(facilInvDtlDto.getNbrFacilinvstAffilPhn());
			}
			if (!ObjectUtils.isEmpty(facilInvDtlDto.getNbrFacilinvstAffilExt())) {
				facilityInvstDtl.setNbrFacilInvstAffilExt(facilInvDtlDto.getNbrFacilinvstAffilExt());
			}
			facilityInvstDtl.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			sessionFactory.getCurrentSession().update(facilityInvstDtl);
			FacilityInvstDtl savedRecord = (FacilityInvstDtl) sessionFactory.getCurrentSession()
					.createCriteria(FacilityInvstDtl.class)
					.add(Restrictions.eq("stage.idStage", facilInvDtlDto.getIdStage())).uniqueResult();
			facilInvDtlDto.setDtLastUpdate(savedRecord.getDtLastUpdate());

		}
		return facilInvDtlDto;
	}

	/**
	 * Method Name: getEvidenceList Method Description:This method is used to
	 * retrieve the list of evidence list contacts in the case.
	 * 
	 * @param idCase
	 *            - The id of the case.
	 * @return List<Long> - The list of the id case .
	 */
	@Override
	public List<Long> getEvidenceList(Long idCase) {
		List<Long> idCaseList = new ArrayList<Long>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contact.class)
				.add(Restrictions.eq("idCase", idCase))
				.add(Restrictions.eq("cdContactType", CodesConstant.CCNTCTYP_EEVL));
		List<Contact> contactList = (List<Contact>) criteria.list();
		if (!CollectionUtils.isEmpty(contactList)) {
			idCaseList = contactList.stream().map(Contact::getIdCase).collect(Collectors.toList());
		}
		return idCaseList;
	}

	/**
	 * Method Name: getFacilityAllegationListForVictim Method Description:This
	 * method is used to check the facility allegation for input - id stage, id
	 * person , disposition.
	 * 
	 * @param pInputDataRec
	 *            - This dto will have the input parameters for retrieving the
	 *            facility allegations.
	 * @return List<AllegationStageOutDto> - The list of facility allegation
	 *         details.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationStageOutDto> getFacilityAllegationListForVictim(AllegationStageInDto allegationStageInDto) {

		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getFacilAllegationsForVictimSQL)
				.setResultTransformer(Transformers.aliasToBean(AllegationStageOutDto.class)));
		sQLQuery.addScalar("idAllegation", StandardBasicTypes.LONG).addScalar("indFatality", StandardBasicTypes.STRING)
				.addScalar("cdAllegSeverity", StandardBasicTypes.STRING)
				.setParameter("idStage", allegationStageInDto.getIdStage())
				.setParameter("cdFacilityInjSer", allegationStageInDto.getCdFacilAllegInjSer())
				.setParameter("idPerson", allegationStageInDto.getIdPerson())
				.setParameterList("dispositionList", allegationStageInDto.getDispositionList());
		List<AllegationStageOutDto> list = new ArrayList<AllegationStageOutDto>();
		list = (List<AllegationStageOutDto>) sQLQuery.list();

		return list;

	}

	/**
	 * Method Name: getNameTiedToFacilAllegOfCRC Method Description:This method
	 * is used to retrieve the administrator name for the facilities involved in
	 * the Facility Investigation conclusion.
	 * 
	 * @param idSatge
	 *            - The id of the stage.
	 * @return - The list of Provider information.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityAllegationDto> getNameTiedToFacilAllegOfCRC(Long idSatge) {

		List<FacilityAllegationDto> facilityAllegationDtoList = new ArrayList<FacilityAllegationDto>();
		facilityAllegationDtoList = (List<FacilityAllegationDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getNameTiedToFacilAllegOfCRCSql).setParameter("idStage", idSatge))
						.addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("idAllegationStage", StandardBasicTypes.LONG)
						.addScalar("idAllegation", StandardBasicTypes.LONG)
						.addScalar("idFacilResource", StandardBasicTypes.LONG)
						.addScalar("idFacilRsrcLink", StandardBasicTypes.LONG)
						.addScalar("cdFacAdminFName", StandardBasicTypes.STRING)
						.addScalar("cdFacAdminLName", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(FacilityAllegationDto.class)).list();

		return facilityAllegationDtoList;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceAddressDto> getRsrcAddressFromCapsRsrc(Long idResource, List<String> facilityTypeCodes) {

		StringBuffer sql = new StringBuffer(getResourceAddressSql);
		if (!CollectionUtils.isEmpty(facilityTypeCodes)) {
			sql = sql.append(" ").append(getResourceAddressAndSql);
		}
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("addStreetLine1", StandardBasicTypes.STRING)
				.addScalar("addStreetLine2", StandardBasicTypes.STRING).addScalar("addCity", StandardBasicTypes.STRING)
				.addScalar("addState", StandardBasicTypes.STRING).addScalar("addZip", StandardBasicTypes.STRING)
				.addScalar("addCounty", StandardBasicTypes.STRING)
				.addScalar("cdResourceType", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ResourceAddressDto.class));
		if (!CollectionUtils.isEmpty(facilityTypeCodes)) {
			sqlQuery.setParameterList("facilResourceTypeList", facilityTypeCodes);
		}
		// Defect 11133 - Set null resource to zero so hibernate doesnt throw incosistent data types
		if (ObjectUtils.isEmpty(idResource)){
			sqlQuery.setParameter("idResource", ServiceConstants.ZERO);
		}else{
			sqlQuery.setParameter("idResource", idResource);
		}

		List<ResourceAddressDto> resourceAddressDtoList = (List<ResourceAddressDto>) sqlQuery.list();
		return resourceAddressDtoList;
	}
}
