package us.tx.state.dfps.service.sscc.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.SsccPlcmtRsrcLink;
import us.tx.state.dfps.common.domain.SsccPlcmtRsrcLinkMc;
import us.tx.state.dfps.service.admin.dto.SSCCPlcmntRsrcLinkMCDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCParameterDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkResourceDetailDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkResourceDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkTimelineDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkValidationDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlcmntAgncyHmMCDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlcmntNtwrkAgencyHomeDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.sscc.dao.SSCCPlacementNetworkDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * SSCCPlacementNetworkDaoImpl Sep 6, 2018- 4:09:40 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class SSCCPlacementNetworkDaoImpl implements SSCCPlacementNetworkDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${SSCCPlacementNetworkDaoImpl.getSsccPlacementNetworkList}")
	private String getSsccPlacementNetworkList;

	@Value("${SSCCPlacementNetworkDaoImpl.getAgencyHomeAddList}")
	private String getAgencyHomeAddList;

	@Value("${SSCCPlacementNetworkDaoImpl.getResourceDetailsEditMode}")
	private String getResourceDetailsEditMode;

	@Value("${SSCCPlacementNetworkDaoImpl.getAgencyHomeList}")
	private String getAgencyHomeList;

	@Value("${SSCCPlacementNetworkDaoImpl.getPlacementNetworkTimeline}")
	private String getPlacementNetworkTimeline;

	@Value("${SSCCPlacementNetworkDaoImpl.getAgencyHmDetailsAdd}")
	private String getAgencyHmDetailsAdd;

	@Value("${SSCCPlacementNetworkDaoImpl.getSsccPlcmntAgencyHomeMC}")
	private String getSsccPlcmntAgencyHomeMC;

	@Value("${SSCCPlacementNetworkDaoImpl.getAgencyHmDetailsEdit}")
	private String getAgencyHmDetailsEdit;

	@Value("${SSCCPlacementNetworkDaoImpl.getAgencyHomeFilterInNetwrk}")
	private String getAgencyHomeFilterInNetwrk;

	@Value("${SSCCPlacementNetworkDaoImpl.getAgencyHomeFilterOutNetwrk}")
	private String getAgencyHomeFilterOutNetwrk;

	@Value("${SSCCPlacementNetworkDaoImpl.checkAgencyHomeActiveInOtherCPA}")
	private String checkAgencyHomeActiveInOtherCPA;

	@Value("${SSCCPlacementNetworkDaoImpl.getSsccResourceDetails}")
	private String getSsccResourceDetails;

	@Value("${SSCCPlacementNetworkDaoImpl.getPlacementEndDateOther}")
	private String getPlacementEndDateOther;

	@Value("${SSCCPlacementNetworkDaoImpl.getPlacementEndDateCpa}")
	private String getPlacementEndDateCpa;

	@Value("${SSCCPlacementNetworkDaoImpl.verifyRsrcAlreadyAddedToSscc}")
	private String verifyRsrcAlreadyAddedToSscc;

	@Value("${SSCCPlacementNetworkDaoImpl.verifyRsrcIsValid}")
	private String verifyRsrcIsValid;

	@Value("${SSCCPlacementNetworkDaoImpl.verifyRsrcOtherFcltyType}")
	private String verifyRsrcOtherFcltyType;

	@Value("${SSCCPlacementNetworkDaoImpl.verifyRsrcValidFcltyType}")
	private String verifyRsrcValidFcltyType;

	@Value("${SSCCPlacementNetworkDaoImpl.verifyRsrcActive}")
	private String verifyRsrcActive;

	@Autowired
	FormattingUtils formattingUtils;

	/**
	 * Method Name: getSsccPlacementNetworkList
	 *
	 * Method Description: This method gets SsccPlacementNetworkList
	 * 
	 * @param idRsrcSscc
	 * @return List<SSCCPlacementNetworkListDto>
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<SSCCPlacementNetworkListDto> getSsccPlacementNetworkList(Long idRsrcSscc) {
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getSsccPlacementNetworkList);
		sqlQuery.setParameter("cdPlcmntRSRCLinkFirst", CodesConstant.CSSCCLNK_10);
		sqlQuery.setParameter("cdPlcmntRSRCLinkSecond", CodesConstant.CSSCCLNK_20);
		sqlQuery.setParameter("idRsrcSscc", idRsrcSscc);
		sqlQuery.addScalar("nmResource");
		sqlQuery.addScalar("cdRsrcFacilType");
		sqlQuery.addScalar("dtStart", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdNetworkStatus");
		sqlQuery.addScalar("nmUserLastMdfd");
		sqlQuery.addScalar("cdRsrcStatus");
		sqlQuery.addScalar("addrRsrcStLn");
		sqlQuery.addScalar("addrRsrcCity");
		sqlQuery.addScalar("cdRsrcCnty");
		sqlQuery.addScalar("nbrRsrcPhn");
		sqlQuery.addScalar("nbrRsrcPhoneExt");
		sqlQuery.addScalar("idRsrcMember", StandardBasicTypes.LONG);
		sqlQuery.addScalar("networkStatusDecode");
		sqlQuery.addScalar("countyDecode");
		sqlQuery.addScalar("facilityTypeDecode");
		sqlQuery.addScalar("resourceStatusDecode");
		sqlQuery.addScalar("cdRsrcRegion");
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SSCCPlacementNetworkListDto.class));
		sqlQuery.setFetchSize(10000);
		List<SSCCPlacementNetworkListDto> ssccPlacementNetworkList = sqlQuery.list();

		return ssccPlacementNetworkList;

	}

	/**
	 * Method Name: setResourceDetailsAddMode
	 *
	 * Method Description: This method is used to set ResourceDetails in AddMode
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 */
	@Override
	public void setResourceDetailsAddMode(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {
		SSCCPlacementNetworkResourceDetailDto existingDto = ssccPlacementNetworkResourceDto
				.getSsccPlacementNetworkResourceDetailDto();
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
				.setProjection(Projections.projectionList().add(Projections.property("idResource").as("idResource"))
						.add(Projections.property("nmResource").as("nmResource"))
						.add(Projections.property("nmLegal").as("nmLegal"))
						.add(Projections.property("cdRsrcType").as("cdRsrcType"))
						.add(Projections.property("cdRsrcFacilType").as("cdRsrcFacilType"))
						.add(Projections.property("idResource").as("idRsrcMember"))
						.add(Projections.property("idResource").as("idRsrcCpa")))
				.add(Restrictions.eq("idResource",
						ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdResource()));

		SSCCPlacementNetworkResourceDetailDto ssccPlcmntNtwrkResourceDetailDto = (SSCCPlacementNetworkResourceDetailDto) cr
				.setResultTransformer(Transformers.aliasToBean(SSCCPlacementNetworkResourceDetailDto.class))
				.uniqueResult();
		ssccPlcmntNtwrkResourceDetailDto.setIdRsrcSscc(existingDto.getIdRsrcSscc());
		ssccPlacementNetworkResourceDto.setSsccPlacementNetworkResourceDetailDto(ssccPlcmntNtwrkResourceDetailDto);
	}

	/**
	 * Method Name: setAgencyHomeListAddMode
	 *
	 * Method Description: This method is used to set AgencyHomeList in
	 * SSCCPlacementNetworkResourceDto in AddMode
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void setAgencyHomeListAddMode(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {

		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getAgencyHomeAddList);
		sqlQuery.setParameter("cdRsrcType", CodesConstant.CRSCLINK_02);
		sqlQuery.setParameter("cdRsrcStatus", CodesConstant.CRSCSTAT_01);
		sqlQuery.setParameter("idRsrcLinkParent",
				ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdResource());
		sqlQuery.addScalar("nmResource");
		sqlQuery.addScalar("cdRsrcFacilType");
		sqlQuery.addScalar("idResource", StandardBasicTypes.LONG);
		sqlQuery.addScalar("cdRsrcRegion");
		sqlQuery.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdNetworkStatus");
		sqlQuery.addScalar("nmUserLastMdfd");
		sqlQuery.addScalar("addrRsrcStLn");
		sqlQuery.addScalar("addrRsrcCity");
		sqlQuery.addScalar("cdRsrcCnty");
		sqlQuery.addScalar("nbrRsrcPhn");
		sqlQuery.addScalar("nbrRsrcPhoneExt");
		sqlQuery.addScalar("facilityTypeDecode");
		sqlQuery.addScalar("countyDecode");
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SSCCPlcmntNtwrkAgencyHomeDto.class));

		List<SSCCPlcmntNtwrkAgencyHomeDto> ssccPlcmntNtwrkAgencyHomeList = sqlQuery.list();

		ssccPlacementNetworkResourceDto.setSsccPlcmntNtwrkAgencyHomeList(ssccPlcmntNtwrkAgencyHomeList);

	}

	/**
	 * Method Name: setResourceDetails
	 *
	 * Method Description: This method is used to set ResourceDetails in
	 * SSCCPlacementNetworkResourceDto
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 */
	@Override
	public void setResourceDetails(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {

		SSCCPlacementNetworkResourceDetailDto existingDto = ssccPlacementNetworkResourceDto
				.getSsccPlacementNetworkResourceDetailDto();

		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getResourceDetailsEditMode);
		sqlQuery.addScalar("idSsccPlcmtRsrcLink", StandardBasicTypes.LONG).addScalar("nmResource")
				.addScalar("cdRsrcFacilType").addScalar("nmLegal").addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("cdRsrcType").addScalar("cdNetworkStatusRsn").addScalar("txtComment")

				.addScalar("idRsrcCpa", StandardBasicTypes.LONG).addScalar("dtStart", StandardBasicTypes.DATE)
				.addScalar("cdPlcmtRsrcLinkType").addScalar("cdNetworkStatus")
				.setParameter("idRsrcsscc", existingDto.getIdRsrcSscc())
				.setParameter("idRsrcMember", existingDto.getIdResource())
				.setResultTransformer(Transformers.aliasToBean(SSCCPlacementNetworkResourceDetailDto.class));

		SSCCPlacementNetworkResourceDetailDto ssccPlcmntNtwrkResourceDetailDto = (SSCCPlacementNetworkResourceDetailDto) sqlQuery
				.uniqueResult();
		ssccPlcmntNtwrkResourceDetailDto.setIdRsrcSscc(existingDto.getIdRsrcSscc());
		ssccPlcmntNtwrkResourceDetailDto.setIdRsrcMember(existingDto.getIdResource());
		ssccPlacementNetworkResourceDto.setSsccPlacementNetworkResourceDetailDto(ssccPlcmntNtwrkResourceDetailDto);
	}

	/**
	 * Method Name: setAgencyHomeList
	 *
	 * Method Description: This method is used to set AgencyHomeList in
	 * SSCCPlacementNetworkResourceDto
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void setAgencyHomeList(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getAgencyHomeList);
		sqlQuery.setParameter("idRsrcCpa",
				ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdResource());
		sqlQuery.setParameter("idRsrcMember",
				ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdResource());
		sqlQuery.setParameter("idRsrcSscc",
				ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdRsrcSscc());
		sqlQuery.setParameter("cdRsrcStatus", CodesConstant.CRSCSTAT_01);
		sqlQuery.setParameter("idRsrcLinkParent",
				ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdResource());
		sqlQuery.addScalar("idSsccPlcmtRsrcLink", StandardBasicTypes.LONG);
		sqlQuery.addScalar("nmResource");
		sqlQuery.addScalar("cdRsrcFacilType");
		sqlQuery.addScalar("idResource", StandardBasicTypes.LONG);
		sqlQuery.addScalar("cdRsrcRegion");
		sqlQuery.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sqlQuery.addScalar("idRsrcCpa", StandardBasicTypes.LONG);
		sqlQuery.addScalar("cdNetworkStatus");
		sqlQuery.addScalar("nmUserLastMdfd");
		sqlQuery.addScalar("addrRsrcStLn");
		sqlQuery.addScalar("addrRsrcCity");
		sqlQuery.addScalar("cdRsrcCnty");
		sqlQuery.addScalar("nbrRsrcPhn");
		sqlQuery.addScalar("nbrRsrcPhoneExt");
		sqlQuery.addScalar("networkStatusDecode");
		sqlQuery.addScalar("countyDecode");
		sqlQuery.addScalar("facilityTypeDecode");

		sqlQuery.setResultTransformer(Transformers.aliasToBean(SSCCPlcmntNtwrkAgencyHomeDto.class));
		sqlQuery.setFetchSize(10000);
		List<SSCCPlcmntNtwrkAgencyHomeDto> ssccPlcmntNtwrkAgencyHomeList = sqlQuery.list();

		ssccPlacementNetworkResourceDto.setSsccPlcmntNtwrkAgencyHomeList(ssccPlcmntNtwrkAgencyHomeList);

	}

	/**
	 * Method Name: setPlacementNetworkTimeline
	 *
	 * Method Description: This method is used to set PlacementNetwork Time line
	 * in SSCCPlacementNetworkResourceDto
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void setPlacementNetworkTimeline(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {

		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getPlacementNetworkTimeline);

		sqlQuery.setParameter("idRsrcMember",
				ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdResource());
		sqlQuery.setParameter("idRsrcSscc",
				ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdRsrcSscc());
		sqlQuery.addScalar("cdNetworkStatusRsn");
		sqlQuery.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sqlQuery.addScalar("txtComment");
		sqlQuery.addScalar("dtStart", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdNetworkStatus");
		sqlQuery.addScalar("nmUserLastMdfd");
		sqlQuery.addScalar("idRsrcCpa", StandardBasicTypes.LONG);

		sqlQuery.setResultTransformer(Transformers.aliasToBean(SSCCPlacementNetworkTimelineDto.class));

		List<SSCCPlacementNetworkTimelineDto> ssccPlcmntNtwrkTimeLineList = sqlQuery.list();

		if (!ObjectUtils.isEmpty(ssccPlcmntNtwrkTimeLineList)
				&& ServiceConstants.Y.equals(ssccPlacementNetworkResourceDto.getIndAgencyHomeDetailsEditMode())) {
			ssccPlcmntNtwrkTimeLineList = ssccPlcmntNtwrkTimeLineList
					.stream().filter(timeLine -> ssccPlacementNetworkResourceDto
							.getSsccPlacementNetworkResourceDetailDto().getIdRsrcCpa().equals(timeLine.getIdRsrcCpa()))
					.collect(Collectors.toList());
		}
		ssccPlacementNetworkResourceDto.setSsccPlcmntNtwrkTimeLineList(ssccPlcmntNtwrkTimeLineList);

	}

	/**
	 * Method Name: setMedicalConsenter
	 *
	 * Method Description: This method is used to set MedicalConsenter in
	 * SSCCPlacementNetworkResourceDto
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void setMedicalConsenter(SSCCPlacementNetworkResourceDto ssccPlcmntNtwrkResourceDto) {

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(SsccPlcmtRsrcLinkMc.class);
		Criterion idRsrcSscc = Restrictions.eq("idRsrcSscc",
				ssccPlcmntNtwrkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdRsrcSscc());
		Criterion idRsrcMember = Restrictions.eq("idRsrcMember",
				ssccPlcmntNtwrkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdResource());
		LogicalExpression restriction = Restrictions.and(idRsrcSscc, idRsrcMember);
		cr.add(restriction).addOrder(Order.desc("dtLastUpdate"));

		List<SsccPlcmtRsrcLinkMc> ssccPlcmtRsrcLinkMcList = (List<SsccPlcmtRsrcLinkMc>) cr.list();

		List<SSCCPlcmntRsrcLinkMCDto> ssccPlcmntRsrcLinkMCList = new ArrayList<SSCCPlcmntRsrcLinkMCDto>();
		ssccPlcmtRsrcLinkMcList.forEach(ssccPlcmtRsrcLinkMc -> {
			SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto = new SSCCPlcmntRsrcLinkMCDto();
			ssccPlcmntRsrcLinkMCDto.setIdSSCCPlcmtRsrcLinkMC(ssccPlcmtRsrcLinkMc.getIdSsccPlcmtRsrcLinkMc());
			ssccPlcmntRsrcLinkMCDto.setDtLastUpdate(ssccPlcmtRsrcLinkMc.getDtLastUpdate());
			ssccPlcmntRsrcLinkMCDto.setIdLastUpdatePerson(ssccPlcmtRsrcLinkMc.getIdLastUpdatePerson());
			ssccPlcmntRsrcLinkMCDto.setDtCreated(ssccPlcmtRsrcLinkMc.getDtCreated());
			ssccPlcmntRsrcLinkMCDto.setIdCreatedPerson(ssccPlcmtRsrcLinkMc.getIdCreatedPerson());
			ssccPlcmntRsrcLinkMCDto.setDtMcRemoved(ssccPlcmtRsrcLinkMc.getDtMcRemoved());
			ssccPlcmntRsrcLinkMCDto.setIdRsrcSSCC(ssccPlcmtRsrcLinkMc.getIdRsrcSscc());
			ssccPlcmntRsrcLinkMCDto.setIdRsrcMember(ssccPlcmtRsrcLinkMc.getIdRsrcMember());
			ssccPlcmntRsrcLinkMCDto.setIdMedConsenterPerson(ssccPlcmtRsrcLinkMc.getIdMedConsenterPerson());
			ssccPlcmntRsrcLinkMCDto.setTxtComment(ssccPlcmtRsrcLinkMc.getTxtComment());
			ssccPlcmntRsrcLinkMCDto
					.setNmPerson(formattingUtils.formatName(ssccPlcmtRsrcLinkMc.getIdMedConsenterPerson()));
			ssccPlcmntRsrcLinkMCDto
					.setNmLastUpdatePerson(formattingUtils.formatName(ssccPlcmtRsrcLinkMc.getIdLastUpdatePerson()));
			ssccPlcmntRsrcLinkMCDto
					.setNmAddedByPerson(formattingUtils.formatName(ssccPlcmtRsrcLinkMc.getIdCreatedPerson()));
			ssccPlcmntRsrcLinkMCList.add(ssccPlcmntRsrcLinkMCDto);
		});
		ssccPlcmntNtwrkResourceDto.setSsccPlcmntRsrcLinkMCList(ssccPlcmntRsrcLinkMCList);

	}

	/**
	 * Method Name: setAgencyHomeDetailsAddMode
	 *
	 * Method Description: This method is used to set AgencyHomeDetails in
	 * AddMode
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 */

	@Override
	public void setAgencyHomeDetailsAddMode(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {
		SSCCPlacementNetworkResourceDetailDto existingDto = ssccPlacementNetworkResourceDto
				.getSsccPlacementNetworkResourceDetailDto();
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getAgencyHmDetailsAdd);

		sqlQuery.setParameter("idRsrcLinkChild",
				ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdResource());
		sqlQuery.addScalar("nmResource");
		sqlQuery.addScalar("cdRsrcFacilType");
		sqlQuery.addScalar("nmLegal");
		sqlQuery.addScalar("idResource", StandardBasicTypes.LONG);
		sqlQuery.addScalar("cdRsrcType");
		sqlQuery.addScalar("idRsrcCpa", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idRsrcMember", StandardBasicTypes.LONG);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SSCCPlacementNetworkResourceDetailDto.class));

		SSCCPlacementNetworkResourceDetailDto ssccPlcmntNtwrkResourceDetailDto = (SSCCPlacementNetworkResourceDetailDto) sqlQuery
				.uniqueResult();

		ssccPlcmntNtwrkResourceDetailDto.setIdRsrcSscc(existingDto.getIdRsrcSscc());
		ssccPlcmntNtwrkResourceDetailDto.setIdRsrcMember(existingDto.getIdResource());
		ssccPlacementNetworkResourceDto.setSsccPlacementNetworkResourceDetailDto(ssccPlcmntNtwrkResourceDetailDto);
	}

	/**
	 * Method Name: setPlacementAgencyHomeMC
	 *
	 * Method Description: This method is used to set PlacementAgency HomeMC
	 * List in SSCCPlacementNetworkResourceDto
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void setPlacementAgencyHomeMC(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {

		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getSsccPlcmntAgencyHomeMC);

		sqlQuery.setParameter("idResource",
				ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdResource());
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SSCCPlcmntAgncyHmMCDto.class));
		sqlQuery.addScalar("idCaretaker", StandardBasicTypes.LONG);
		sqlQuery.addScalar("nmCaretkrFname");
		sqlQuery.addScalar("nmCaretkrMname");
		sqlQuery.addScalar("nmCaretkrLname");
		sqlQuery.addScalar("cdCaretkrSuffix");
		sqlQuery.addScalar("cdCaretkrSex");
		sqlQuery.addScalar("idPerson", StandardBasicTypes.LONG);
		sqlQuery.addScalar("nmPersonFull");
		sqlQuery.addScalar("dtCaretkrBirth", StandardBasicTypes.DATE);
		List<SSCCPlcmntAgncyHmMCDto> ssccPlcmntAgncyHmMCList = sqlQuery.list();

		if (!ObjectUtils.isEmpty(ssccPlcmntAgncyHmMCList)) {
			ssccPlcmntAgncyHmMCList.stream().forEach(agencyMc -> {
				agencyMc.setFullLegalName(FormattingUtils.formatFullLegalName(agencyMc.getNmCaretkrFname(),
						agencyMc.getNmCaretkrMname(), agencyMc.getNmCaretkrLname(), agencyMc.getCdCaretkrSuffix()));
			});
		}
		ssccPlacementNetworkResourceDto.setSsccPlcmntAgncyHmMCList(ssccPlcmntAgncyHmMCList);
	}

	/**
	 * Method Name: setAgencyHomeDetailsEditMode
	 *
	 * Method Description: This method is used to set AgencyHomeDetails in
	 * EditMode in SSCCPlacementNetworkResourceDto
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 */

	@Override
	public void setAgencyHomeDetailsEditMode(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {

		SSCCPlacementNetworkResourceDetailDto existingDto = ssccPlacementNetworkResourceDto
				.getSsccPlacementNetworkResourceDetailDto();

		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getAgencyHmDetailsEdit);
		sqlQuery.setParameter("idSsccPlcmntLink",
				ssccPlacementNetworkResourceDto.getSsccPlacementNetworkResourceDetailDto().getIdSsccPlcmtRsrcLink());
		sqlQuery.addScalar("idSsccPlcmtRsrcLink", StandardBasicTypes.LONG);
		sqlQuery.addScalar("nmResource");
		sqlQuery.addScalar("cdRsrcFacilType");
		sqlQuery.addScalar("nmLegal");
		sqlQuery.addScalar("idResource", StandardBasicTypes.LONG);
		sqlQuery.addScalar("cdRsrcType");
		sqlQuery.addScalar("cdNetworkStatusRsn");
		sqlQuery.addScalar("txtComment");
		sqlQuery.addScalar("cdPlcmtRsrcLinkType");
		sqlQuery.addScalar("idRsrcCpa", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dtStart", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdNetworkStatus");
		sqlQuery.addScalar("idRsrcMember", StandardBasicTypes.LONG);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SSCCPlacementNetworkResourceDetailDto.class));

		SSCCPlacementNetworkResourceDetailDto ssccPlcmntNtwrkResourceDetailDto = (SSCCPlacementNetworkResourceDetailDto) sqlQuery
				.uniqueResult();

		ssccPlcmntNtwrkResourceDetailDto.setIdRsrcSscc(existingDto.getIdRsrcSscc());
		ssccPlcmntNtwrkResourceDetailDto.setIdRsrcMember(existingDto.getIdResource());
		ssccPlcmntNtwrkResourceDetailDto.setIdResource(existingDto.getIdResource());
		ssccPlacementNetworkResourceDto.setSsccPlacementNetworkResourceDetailDto(ssccPlcmntNtwrkResourceDetailDto);
	}

	/**
	 * Method Name: insertSsccPlcmtRsrcLink
	 *
	 * Method Description: This method is used to insert SsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @param idResourceSscc
	 */
	@Override
	public void insertSsccPlcmtRsrcLink(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto,
			Long idResourceSscc) {

		SSCCPlacementNetworkResourceDetailDto ssccPlacementNetworkResourceDetailDto = ssccPlacementNetworkResourceDto
				.getSsccPlacementNetworkResourceDetailDto();
		List<Long> selectedAgencies = ssccPlacementNetworkResourceDto.getSelectedAgencies();

		// Get the Resource Link type
		String cdPlcmtRsrcLinkType = getPlacementRsrcLinkType(
				ssccPlacementNetworkResourceDetailDto.getCdRsrcFacilType());
		SsccPlcmtRsrcLink ssccPlcmtRsrcLinkEntity = new SsccPlcmtRsrcLink();

		ssccPlcmtRsrcLinkEntity.setIdLastUpdate(ssccPlacementNetworkResourceDetailDto.getIdCreatedPerson());
		ssccPlcmtRsrcLinkEntity.setIdCreatedPerson(ssccPlacementNetworkResourceDetailDto.getIdCreatedPerson());
		ssccPlcmtRsrcLinkEntity.setIdRsrcSscc(idResourceSscc);
		ssccPlcmtRsrcLinkEntity.setIdRsrcMember(ssccPlacementNetworkResourceDetailDto.getIdResource());
		if (CodesConstant.CFACTYP5_60.equals(ssccPlacementNetworkResourceDetailDto.getCdRsrcFacilType())) {
			ssccPlcmtRsrcLinkEntity.setIdRsrcCpa(ssccPlacementNetworkResourceDetailDto.getIdResource());
		} else {
			ssccPlcmtRsrcLinkEntity.setIdRsrcCpa(null);
		}
		ssccPlcmtRsrcLinkEntity.setCdPlcmtRsrcLinkType(cdPlcmtRsrcLinkType);
		ssccPlcmtRsrcLinkEntity.setCdNetworkStatus(ssccPlacementNetworkResourceDetailDto.getCdNetworkStatus());
		ssccPlcmtRsrcLinkEntity.setDtStart(ssccPlacementNetworkResourceDetailDto.getDtStart());
		ssccPlcmtRsrcLinkEntity.setTxtComment(ssccPlacementNetworkResourceDetailDto.getTxtComment());
		ssccPlcmtRsrcLinkEntity.setCdNetworkStatusRsn(ssccPlacementNetworkResourceDetailDto.getCdNetworkStatusRsn());
		ssccPlcmtRsrcLinkEntity.setDtCreated(new Date());
		ssccPlcmtRsrcLinkEntity.setDtLastUpdate(new Date());

		sessionFactory.getCurrentSession().save(ssccPlcmtRsrcLinkEntity);

		// Insert Agency Homes in to SSCC_PLCMT_RSRC_LINK table
		if (!ObjectUtils.isEmpty(selectedAgencies)) {
			for (Long idResource : selectedAgencies) {
				SsccPlcmtRsrcLink ssccPlcmtRsrcLinkAgencyEntity = new SsccPlcmtRsrcLink();
				ssccPlcmtRsrcLinkAgencyEntity
						.setIdLastUpdate(ssccPlacementNetworkResourceDetailDto.getIdCreatedPerson());
				ssccPlcmtRsrcLinkAgencyEntity
						.setIdCreatedPerson(ssccPlacementNetworkResourceDetailDto.getIdCreatedPerson());
				ssccPlcmtRsrcLinkAgencyEntity.setIdRsrcSscc(idResourceSscc);
				ssccPlcmtRsrcLinkAgencyEntity.setIdRsrcMember(idResource);
				ssccPlcmtRsrcLinkAgencyEntity.setIdRsrcCpa(ssccPlacementNetworkResourceDetailDto.getIdResource());
				ssccPlcmtRsrcLinkAgencyEntity.setCdPlcmtRsrcLinkType(CodesConstant.CSSCCLNK_30);
				ssccPlcmtRsrcLinkAgencyEntity
						.setCdNetworkStatus(ssccPlacementNetworkResourceDetailDto.getCdNetworkStatus());
				ssccPlcmtRsrcLinkAgencyEntity.setDtStart(ssccPlacementNetworkResourceDetailDto.getDtStart());
				ssccPlcmtRsrcLinkAgencyEntity.setTxtComment(ssccPlacementNetworkResourceDetailDto.getTxtComment());
				ssccPlcmtRsrcLinkAgencyEntity
						.setCdNetworkStatusRsn(ssccPlacementNetworkResourceDetailDto.getCdNetworkStatusRsn());
				ssccPlcmtRsrcLinkAgencyEntity.setDtCreated(new Date());
				ssccPlcmtRsrcLinkAgencyEntity.setDtLastUpdate(new Date());
				sessionFactory.getCurrentSession().save(ssccPlcmtRsrcLinkAgencyEntity);
			}
		}
	}

	/**
	 * Method Name: getPlacementRsrcLinkType
	 *
	 * Method Description: This method is used to get Placement RsrcLinkType
	 * 
	 * @param cdRsrcFacilType
	 * @return String
	 */
	public String getPlacementRsrcLinkType(String cdRsrcFacilType) {
		String cdPlcmtRsrcLinkType = ServiceConstants.EMPTY_STRING;

		if (CodesConstant.CFACTYP5_80.equals(cdRsrcFacilType) || CodesConstant.CFACTYP5_64.equals(cdRsrcFacilType)
				|| CodesConstant.CFACTYP5_67.equals(cdRsrcFacilType)
				|| CodesConstant.CFACTYP5_93.equals(cdRsrcFacilType)
				|| CodesConstant.CFACTYP5_68.equals(cdRsrcFacilType)
				|| CodesConstant.CFACTYP5_65.equals(cdRsrcFacilType)
				|| CodesConstant.CFACTYP5_69.equals(cdRsrcFacilType)) {
			cdPlcmtRsrcLinkType = CodesConstant.CSSCCLNK_10;
		} else if (CodesConstant.CFACTYP5_60.equals(cdRsrcFacilType)) {
			cdPlcmtRsrcLinkType = CodesConstant.CSSCCLNK_20;
		} else {
			cdPlcmtRsrcLinkType = CodesConstant.CSSCCLNK_30;
		}

		return cdPlcmtRsrcLinkType;
	}

	/**
	 * Method Name: insertAgencyHomeSsccPlcmtRsrcLink
	 *
	 * Method Description: This method is used to insert AgencyHome
	 * SsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 * @param idResourceSscc
	 */
	@Override
	public void insertAgencyHomeSsccPlcmtRsrcLink(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto,
			Long idResourceSscc) {

		SSCCPlacementNetworkResourceDetailDto ssccPlacementNetworkResourceDetailDto = ssccPlacementNetworkResourceDto
				.getSsccPlacementNetworkResourceDetailDto();

		if (!ObjectUtils.isEmpty(ssccPlacementNetworkResourceDetailDto)) {
			SsccPlcmtRsrcLink ssccPlcmtRsrcLinkEntity = new SsccPlcmtRsrcLink();

			ssccPlcmtRsrcLinkEntity.setIdLastUpdate(ssccPlacementNetworkResourceDetailDto.getIdCreatedPerson());
			ssccPlcmtRsrcLinkEntity.setIdCreatedPerson(ssccPlacementNetworkResourceDetailDto.getIdCreatedPerson());
			ssccPlcmtRsrcLinkEntity.setIdRsrcSscc(idResourceSscc);
			ssccPlcmtRsrcLinkEntity.setIdRsrcMember(ssccPlacementNetworkResourceDetailDto.getIdResource());
			ssccPlcmtRsrcLinkEntity.setIdRsrcCpa(ssccPlacementNetworkResourceDetailDto.getIdRsrcCpa());

			ssccPlcmtRsrcLinkEntity.setCdPlcmtRsrcLinkType(CodesConstant.CSSCCLNK_30);
			ssccPlcmtRsrcLinkEntity.setCdNetworkStatus(ssccPlacementNetworkResourceDetailDto.getCdNetworkStatus());
			ssccPlcmtRsrcLinkEntity.setDtStart(ssccPlacementNetworkResourceDetailDto.getDtStart());
			ssccPlcmtRsrcLinkEntity.setTxtComment(ssccPlacementNetworkResourceDetailDto.getTxtComment());
			ssccPlcmtRsrcLinkEntity
					.setCdNetworkStatusRsn(ssccPlacementNetworkResourceDetailDto.getCdNetworkStatusRsn());
			ssccPlcmtRsrcLinkEntity.setDtCreated(new Date());
			ssccPlcmtRsrcLinkEntity.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().save(ssccPlcmtRsrcLinkEntity);
		}

	}

	/**
	 * Method Name: updateAgencyHomeSsccPlcmtRsrcLink
	 *
	 * Method Description: This method is used to update AgencyHome
	 * SsccPlcmtRsrcLink
	 * 
	 * @param ssccPlacementNetworkResourceDetailDto
	 */

	@Override
	public void updateAgencyHomeSsccPlcmtRsrcLink(
			SSCCPlacementNetworkResourceDetailDto ssccPlacementNetworkResourceDetailDto) {
		if (!ObjectUtils.isEmpty(ssccPlacementNetworkResourceDetailDto)) {
			SsccPlcmtRsrcLink ssccPlcmtRsrcLinkEntity = (SsccPlcmtRsrcLink) sessionFactory.getCurrentSession()
					.get(SsccPlcmtRsrcLink.class, ssccPlacementNetworkResourceDetailDto.getIdSsccPlcmtRsrcLink());
			if (!ObjectUtils.isEmpty(ssccPlcmtRsrcLinkEntity)) {
				ssccPlcmtRsrcLinkEntity.setIdLastUpdate(ssccPlacementNetworkResourceDetailDto.getIdCreatedPerson());
				ssccPlcmtRsrcLinkEntity.setCdNetworkStatus(ssccPlacementNetworkResourceDetailDto.getCdNetworkStatus());
				ssccPlcmtRsrcLinkEntity.setDtStart(ssccPlacementNetworkResourceDetailDto.getDtStart());
				ssccPlcmtRsrcLinkEntity.setTxtComment(ssccPlacementNetworkResourceDetailDto.getTxtComment());
				ssccPlcmtRsrcLinkEntity
						.setCdNetworkStatusRsn(ssccPlacementNetworkResourceDetailDto.getCdNetworkStatusRsn());
				sessionFactory.getCurrentSession().update(ssccPlcmtRsrcLinkEntity);
			}
		}

	}

	/**
	 * Method Name: updateSsccPlcmtRsrcLink
	 * 
	 * Method Description: This method is called user clicks on Effective Date
	 * Hyperlink on SSCC Placement Network List and clicks on Save button on
	 * Placement Network Details page.
	 * 
	 * This method updates the data in SSCC_PLCMT_RSRC_LINK table. If the
	 * resource is CPA it will update the Agency Home Details in
	 * SSCC_PLCMT_RSRC_LINK table too.
	 * 
	 * @param ssccPlacementNetworkResourceDto
	 */
	@Override
	public void updateSsccPlcmtRsrcLink(SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto) {
		SSCCPlacementNetworkResourceDetailDto ssccPlacementNetworkResourceDetailDto = ssccPlacementNetworkResourceDto
				.getSsccPlacementNetworkResourceDetailDto();

		setAgencyHomeList(ssccPlacementNetworkResourceDto);

		List<SSCCPlcmntNtwrkAgencyHomeDto> ssccPlcmntNtwrkAgencyHomeList = ssccPlacementNetworkResourceDto
				.getSsccPlcmntNtwrkAgencyHomeList();

		if (!ObjectUtils.isEmpty(ssccPlacementNetworkResourceDetailDto)) {
			updateAgencyHomeSsccPlcmtRsrcLink(ssccPlacementNetworkResourceDetailDto);
		}

		if (!ObjectUtils.isEmpty(ssccPlcmntNtwrkAgencyHomeList)) {
			for (SSCCPlcmntNtwrkAgencyHomeDto ssccPlcmntNtwrkAgencyHomeDto : ssccPlcmntNtwrkAgencyHomeList) {
				if (!ObjectUtils.isEmpty(ssccPlcmntNtwrkAgencyHomeDto.getIdSsccPlcmtRsrcLink())) {
					boolean updateAgencyHome = false;
					updateAgencyHome = canUpdateAgencyHomeNetworkStatus(
							ssccPlacementNetworkResourceDetailDto.getCdNetworkStatus(),
							ssccPlcmntNtwrkAgencyHomeDto.getCdNetworkStatus());
					if (updateAgencyHome) {
						ssccPlacementNetworkResourceDetailDto
								.setIdSsccPlcmtRsrcLink(ssccPlcmntNtwrkAgencyHomeDto.getIdSsccPlcmtRsrcLink());
						updateAgencyHomeSsccPlcmtRsrcLink(ssccPlacementNetworkResourceDetailDto);
					}
				}

			}
		}
	}

	/**
	 * Method Name: canUpdateAgencyHomeNetworkStatus
	 *
	 * Method Description: This method checks when CPA network status updated,
	 * whether Agency home status can update or not.
	 * 
	 * @param cpaNetworkStatus
	 * @param agencyHmNetworkStatus
	 * @return boolean
	 */
	public boolean canUpdateAgencyHomeNetworkStatus(String cpaNetworkStatus, String agencyHmNetworkStatus) {
		boolean updateAgencyHome = false;

		if (!ObjectUtils.isEmpty(cpaNetworkStatus) && !ObjectUtils.isEmpty(agencyHmNetworkStatus)) {
			if (CodesConstant.CSSCCNET_10.equals(cpaNetworkStatus)
					&& (CodesConstant.CSSCCNET_10.equals(agencyHmNetworkStatus)
							|| CodesConstant.CSSCCNET_20.equals(agencyHmNetworkStatus)
							|| CodesConstant.CSSCCNET_30.equals(agencyHmNetworkStatus)
							|| CodesConstant.CSSCCNET_40.equals(agencyHmNetworkStatus))) {
				updateAgencyHome = false;
			} else if (CodesConstant.CSSCCNET_20.equals(cpaNetworkStatus)) {
				if (CodesConstant.CSSCCNET_10.equals(agencyHmNetworkStatus)) {
					updateAgencyHome = true;
				} else if (CodesConstant.CSSCCNET_20.equals(agencyHmNetworkStatus)
						|| CodesConstant.CSSCCNET_30.equals(agencyHmNetworkStatus)
						|| CodesConstant.CSSCCNET_40.equals(agencyHmNetworkStatus)) {
					updateAgencyHome = false;
				}
			} else if (CodesConstant.CSSCCNET_30.equals(cpaNetworkStatus)) {
				if (CodesConstant.CSSCCNET_10.equals(agencyHmNetworkStatus)
						|| CodesConstant.CSSCCNET_20.equals(agencyHmNetworkStatus)) {
					updateAgencyHome = true;
				} else if (CodesConstant.CSSCCNET_30.equals(agencyHmNetworkStatus)
						|| CodesConstant.CSSCCNET_40.equals(agencyHmNetworkStatus)) {
					updateAgencyHome = false;
				}
			} else if (CodesConstant.CSSCCNET_40.equals(cpaNetworkStatus)) {
				if (CodesConstant.CSSCCNET_10.equals(agencyHmNetworkStatus)
						|| CodesConstant.CSSCCNET_20.equals(agencyHmNetworkStatus)
						|| CodesConstant.CSSCCNET_30.equals(agencyHmNetworkStatus)) {
					updateAgencyHome = true;
				} else if (CodesConstant.CSSCCNET_40.equals(agencyHmNetworkStatus)) {
					updateAgencyHome = false;
				}
			}
		}

		return updateAgencyHome;
	}

	/**
	 * Method Name: insertSsccPlcmntRsrcLinkMC
	 *
	 * Method Description: This method is used to insert SsccPlcmntRsrcLinkMC
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 */

	@Override
	public void insertSsccPlcmntRsrcLinkMC(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto) {

		SsccPlcmtRsrcLinkMc ssccPlcmtRsrcLinkMc = new SsccPlcmtRsrcLinkMc();
		ssccPlcmtRsrcLinkMc.setIdLastUpdatePerson(ssccPlcmntRsrcLinkMCDto.getIdLastUpdatePerson());
		ssccPlcmtRsrcLinkMc.setIdCreatedPerson(ssccPlcmntRsrcLinkMCDto.getIdCreatedPerson());
		ssccPlcmtRsrcLinkMc.setIdRsrcSscc(ssccPlcmntRsrcLinkMCDto.getIdRsrcSSCC());
		ssccPlcmtRsrcLinkMc.setIdRsrcMember(ssccPlcmntRsrcLinkMCDto.getIdRsrcMember());
		ssccPlcmtRsrcLinkMc.setIdMedConsenterPerson(ssccPlcmntRsrcLinkMCDto.getIdMedConsenterPerson());
		ssccPlcmtRsrcLinkMc.setTxtComment(ssccPlcmntRsrcLinkMCDto.getTxtComment());
		sessionFactory.getCurrentSession().save(ssccPlcmtRsrcLinkMc);

	}

	/**
	 * Method Name: updateSsccPlcmntRsrcLinkMC
	 *
	 * Method Description: This method is called when user clicks on Save button
	 * on the Designated Medical consenters Details
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 */

	@Override
	public void updateSsccPlcmntRsrcLinkMC(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto) {
		SsccPlcmtRsrcLinkMc ssccPlcmtRsrcLinkMc = (SsccPlcmtRsrcLinkMc) sessionFactory.getCurrentSession()
				.get(SsccPlcmtRsrcLinkMc.class, ssccPlcmntRsrcLinkMCDto.getIdSSCCPlcmtRsrcLinkMC());
		if (!ObjectUtils.isEmpty(ssccPlcmtRsrcLinkMc)) {
			ssccPlcmtRsrcLinkMc.setIdLastUpdatePerson(ssccPlcmntRsrcLinkMCDto.getIdLastUpdatePerson());
			ssccPlcmtRsrcLinkMc.setIdCreatedPerson(ssccPlcmntRsrcLinkMCDto.getIdCreatedPerson());
			ssccPlcmtRsrcLinkMc.setIdRsrcSscc(ssccPlcmntRsrcLinkMCDto.getIdRsrcSSCC());
			ssccPlcmtRsrcLinkMc.setIdRsrcMember(ssccPlcmntRsrcLinkMCDto.getIdRsrcMember());
			ssccPlcmtRsrcLinkMc.setIdMedConsenterPerson(ssccPlcmntRsrcLinkMCDto.getIdMedConsenterPerson());
			ssccPlcmtRsrcLinkMc.setTxtComment(ssccPlcmntRsrcLinkMCDto.getTxtComment());
			ssccPlcmtRsrcLinkMc.setDtMcRemoved(ssccPlcmntRsrcLinkMCDto.getDtMcRemoved());
			sessionFactory.getCurrentSession().update(ssccPlcmtRsrcLinkMc);
			ssccPlcmntRsrcLinkMCDto.getIdSSCCPlcmtRsrcLinkMC();
		}
	}

	/**
	 * Method Name: removeSsccPlcmntRsrcLinkMC
	 * 
	 * Method Description: This method is used to remove SsccPlcmntRsrcLinkMC
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 */
	@Override
	public void removeSsccPlcmntRsrcLinkMC(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto) {
		SsccPlcmtRsrcLinkMc ssccPlcmtRsrcLinkMc = (SsccPlcmtRsrcLinkMc) sessionFactory.getCurrentSession()
				.get(SsccPlcmtRsrcLinkMc.class, ssccPlcmntRsrcLinkMCDto.getIdSSCCPlcmtRsrcLinkMC());
		if (!ObjectUtils.isEmpty(ssccPlcmtRsrcLinkMc)) {
			ssccPlcmtRsrcLinkMc.setIdLastUpdatePerson(ssccPlcmntRsrcLinkMCDto.getIdLastUpdatePerson());
			ssccPlcmtRsrcLinkMc.setDtMcRemoved(ssccPlcmntRsrcLinkMCDto.getDtMcRemoved());
			sessionFactory.getCurrentSession().update(ssccPlcmtRsrcLinkMc);
		}
	}
	/**
	 * Method Name: getSSCCPlcmntRsrcLinkMCById
	 *
	 * Method Description: This method is used to get SSCCPlcmntRsrcLinkMC By Id
	 * 
	 * @param IdSSCCPlcmtRsrcLinkMC
	 * @return SSCCPlcmntRsrcLinkMCDto
	 */

	@Override
	public SSCCPlcmntRsrcLinkMCDto getSSCCPlcmntRsrcLinkMCById(Long IdSSCCPlcmtRsrcLinkMC) {
		SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto = null;
		SsccPlcmtRsrcLinkMc ssccPlcmtRsrcLinkMc = (SsccPlcmtRsrcLinkMc) sessionFactory.getCurrentSession()
				.get(SsccPlcmtRsrcLinkMc.class, IdSSCCPlcmtRsrcLinkMC);

		if (!ObjectUtils.isEmpty(ssccPlcmtRsrcLinkMc)) {
			ssccPlcmntRsrcLinkMCDto = new SSCCPlcmntRsrcLinkMCDto();
			ssccPlcmntRsrcLinkMCDto = populateSSCCPlcmntRsrcLinkMCDto(ssccPlcmntRsrcLinkMCDto, ssccPlcmtRsrcLinkMc);
		}

		return ssccPlcmntRsrcLinkMCDto;
	}

	/**
	 * Method Name: getRsrcMedCnsntrByRsrcPrsn
	 *
	 * Method Description: This method returns the active count of medical
	 * consenter records for a cpa or other facility med consenter person
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 * @return SSCCPlcmntRsrcLinkMCDto
	 */
	@Override
	public SSCCPlcmntRsrcLinkMCDto getRsrcMedCnsntrByRsrcPrsn(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccPlcmtRsrcLinkMc.class);
		Criterion conjuction = Restrictions.conjunction()
				.add(Restrictions.eq("idRsrcSscc", ssccPlcmntRsrcLinkMCDto.getIdRsrcSSCC()))
				.add(Restrictions.eq("idRsrcMember", ssccPlcmntRsrcLinkMCDto.getIdRsrcMember()))
				.add(Restrictions.eq("idMedConsenterPerson", ssccPlcmntRsrcLinkMCDto.getIdMedConsenterPerson()))
				.add(Restrictions.isNull("dtMcRemoved"));
		criteria.add(conjuction);
		SsccPlcmtRsrcLinkMc ssccPlcmtRsrcLinkMc = (SsccPlcmtRsrcLinkMc) criteria.uniqueResult();

		if (!ObjectUtils.isEmpty(ssccPlcmtRsrcLinkMc)) {
			ssccPlcmntRsrcLinkMCDto = populateSSCCPlcmntRsrcLinkMCDto(ssccPlcmntRsrcLinkMCDto, ssccPlcmtRsrcLinkMc);
		}

		return ssccPlcmntRsrcLinkMCDto;
	}

	/**
	 * Method Name: populateSSCCPlcmntRsrcLinkMCDto
	 *
	 * Method Description: This method is used to populate
	 * SSCCPlcmntRsrcLinkMCDto
	 * 
	 * @param ssccPlcmntRsrcLinkMCDto
	 * @param ssccPlcmtRsrcLinkMc
	 * @return SSCCPlcmntRsrcLinkMCDto
	 */
	private SSCCPlcmntRsrcLinkMCDto populateSSCCPlcmntRsrcLinkMCDto(SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto,
			SsccPlcmtRsrcLinkMc ssccPlcmtRsrcLinkMc) {
		ssccPlcmntRsrcLinkMCDto.setIdSSCCPlcmtRsrcLinkMC(ssccPlcmtRsrcLinkMc.getIdSsccPlcmtRsrcLinkMc());
		ssccPlcmntRsrcLinkMCDto.setDtLastUpdate(ssccPlcmtRsrcLinkMc.getDtLastUpdate());
		ssccPlcmntRsrcLinkMCDto.setIdLastUpdatePerson(ssccPlcmtRsrcLinkMc.getIdLastUpdatePerson());
		ssccPlcmntRsrcLinkMCDto
				.setNmLastUpdatePerson(formattingUtils.formatName(ssccPlcmtRsrcLinkMc.getIdLastUpdatePerson()));

		ssccPlcmntRsrcLinkMCDto.setDtCreated(ssccPlcmtRsrcLinkMc.getDtCreated());
		ssccPlcmntRsrcLinkMCDto.setIdCreatedPerson(ssccPlcmtRsrcLinkMc.getIdCreatedPerson());
		ssccPlcmntRsrcLinkMCDto
				.setNmAddedByPerson(formattingUtils.formatName(ssccPlcmtRsrcLinkMc.getIdCreatedPerson()));
		ssccPlcmntRsrcLinkMCDto.setTxtComment(ssccPlcmtRsrcLinkMc.getTxtComment());
		ssccPlcmntRsrcLinkMCDto.setIdMedConsenterPerson(ssccPlcmtRsrcLinkMc.getIdMedConsenterPerson());
		ssccPlcmntRsrcLinkMCDto.setNmPerson(formattingUtils.formatName(ssccPlcmtRsrcLinkMc.getIdMedConsenterPerson()));
		ssccPlcmntRsrcLinkMCDto.setDtMcRemoved(ssccPlcmtRsrcLinkMc.getDtMcRemoved());
		ssccPlcmntRsrcLinkMCDto.setIdRsrcSSCC(ssccPlcmtRsrcLinkMc.getIdRsrcSscc());
		ssccPlcmntRsrcLinkMCDto.setIdRsrcMember(ssccPlcmtRsrcLinkMc.getIdRsrcMember());
		return ssccPlcmntRsrcLinkMCDto;
	}

	/**
	 * Method Name: isAgncyHmActvInOtherRsrc
	 *
	 * Method Description: This method checks if the Inactive Agency Hm in under
	 * another Active CPA
	 * 
	 * @param ssccPlacementNetworkResourceDetailDto
	 * @return boolean
	 */

	@Override
	public boolean isAgncyHmActvInOtherRsrc(
			SSCCPlacementNetworkResourceDetailDto ssccPlacementNetworkResourceDetailDto) {
		boolean isAgncyHomeActiveInOtherRsrc = false;
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(checkAgencyHomeActiveInOtherCPA);
		sqlQuery.setParameter("idRsrcSscc", ssccPlacementNetworkResourceDetailDto.getIdRsrcSscc());
		sqlQuery.setParameter("idRsrcCpa", ssccPlacementNetworkResourceDetailDto.getIdRsrcCpa());
		sqlQuery.setParameter("idRsrcMember", ssccPlacementNetworkResourceDetailDto.getIdRsrcMember());
		BigDecimal idRsrcmember = (BigDecimal) sqlQuery.uniqueResult();
		if (!ObjectUtils.isEmpty(idRsrcmember) && 0L != idRsrcmember.longValue()) {
			isAgncyHomeActiveInOtherRsrc = true;
		}
		return isAgncyHomeActiveInOtherRsrc;
	}

	/**
	 * Method Name: getSsccResourceHeaderDetails
	 *
	 * Method Description: This method gets the BATCH_SSCC_PARAMETERS details
	 * for the network.
	 * 
	 * @param idResource
	 * @return SSCCParameterDto
	 */

	@Override
	public SSCCParameterDto getSsccResourceHeaderDetails(Long idResource) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSsccResourceDetails)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("dtStart", StandardBasicTypes.DATE)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING)
				.addScalar("cdSSCCCatchment", StandardBasicTypes.STRING).setParameter("idResource", idResource)
				.setMaxResults(1).setResultTransformer(Transformers.aliasToBean(SSCCParameterDto.class));

		SSCCParameterDto ssccParameterDto = (SSCCParameterDto) sqlQuery.uniqueResult();
		return ssccParameterDto;
	}

	/**
	 * Method Name: getCPANetworkStatus
	 *
	 * Method Description: This method is used to get CPA NetworkStatus
	 * 
	 * @param idRsrcCpa
	 * @param idRsrcSscc
	 * @param cdPlcmtRsrcLinkType
	 * @return String
	 */
	@Override
	public String getCPANetworkStatus(Long idRsrcCpa, Long idRsrcSscc) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccPlcmtRsrcLink.class);
		criteria.add(Restrictions.eq("cdPlcmtRsrcLinkType", CodesConstant.CSSCCLNK_20));
		criteria.add(Restrictions.eq("idRsrcCpa", idRsrcCpa));
		criteria.add(Restrictions.eq("idRsrcSscc", idRsrcSscc));

		SsccPlcmtRsrcLink ssccPlcmtRsrcLink = (SsccPlcmtRsrcLink) criteria.uniqueResult();

		return ssccPlcmtRsrcLink.getCdNetworkStatus();
	}

	/**
	 * Method Name: hasPlacementOpen
	 *
	 * Method Description: This method checks Resource has Placement open or
	 * ended. If Placement End date is > Date Start it will return true else
	 * false.
	 * 
	 * @param idResource
	 * @param idRsrcSscc
	 * @param placementLinkType
	 * @param dtStart
	 * @return Boolean
	 */
	@Override
	public Boolean hasPlacementOpen(Long idResource, Long idRsrcSscc, String placementLinkType, Date dtStart) {
		Boolean indPlacementOpen = Boolean.FALSE;
		SQLQuery sqlQuery = null;
		if (CodesConstant.CSSCCLNK_10.equals(placementLinkType)
				|| CodesConstant.CSSCCLNK_30.equals(placementLinkType)) {
			sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPlacementEndDateOther);
		} else {
			sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPlacementEndDateCpa);
		}
		sqlQuery.setParameter("idRsrcSscc", idRsrcSscc);
		sqlQuery.setParameter("idResource", idResource);
		sqlQuery.setParameter("dtStart", dtStart);

		BigDecimal openPlacementCount = (BigDecimal) sqlQuery.uniqueResult();

		if (!ObjectUtils.isEmpty(openPlacementCount) && openPlacementCount.longValue() > 0) {
			indPlacementOpen = Boolean.TRUE;
		}
		return indPlacementOpen;
	}

	/**
	 * Method Name: validateResourceId
	 *
	 * Method Description: This method is used to validate Resource Id before
	 * adding the resource to network.
	 * 
	 * @param ssccPlacementNetworkResourceDetailDto
	 * @return SSCCPlacementNetworkValidationDto
	 */
	@Override
	public SSCCPlacementNetworkValidationDto validateResourceId(
			SSCCPlacementNetworkResourceDetailDto ssccPlacementNetworkResourceDetailDto) {
		SSCCPlacementNetworkValidationDto ssccPlacementNetworkValidationDto = new SSCCPlacementNetworkValidationDto();

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(verifyRsrcAlreadyAddedToSscc);

		Long idResource = ssccPlacementNetworkResourceDetailDto.getIdResource();
		Long idResourceSscc = ssccPlacementNetworkResourceDetailDto.getIdRsrcSscc();

		sqlQuery.setParameter("idResource", idResource);
		sqlQuery.setParameter("idResourceSscc", idResourceSscc);

		BigDecimal resultCount = (BigDecimal) sqlQuery.uniqueResult();

		if (!ObjectUtils.isEmpty(resultCount) && resultCount.longValue() > 0) {
			ssccPlacementNetworkValidationDto.setIndRsrcAlreadyAdded(Boolean.TRUE);
		} else
			ssccPlacementNetworkValidationDto.setIndRsrcAlreadyAdded(Boolean.FALSE);

		if (!ssccPlacementNetworkValidationDto.getIndRsrcAlreadyAdded()) {
			sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(verifyRsrcIsValid);
			sqlQuery.setParameter("idResource", idResource);
			resultCount = (BigDecimal) sqlQuery.uniqueResult();
			if (!ObjectUtils.isEmpty(resultCount) && resultCount.longValue() > 0) {
				ssccPlacementNetworkValidationDto.setIndRsrcValid(Boolean.TRUE);
			} else
				ssccPlacementNetworkValidationDto.setIndRsrcValid(Boolean.FALSE);
			if (ssccPlacementNetworkValidationDto.getIndRsrcValid()) {
				sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(verifyRsrcOtherFcltyType);
				sqlQuery.setParameter("idResource", idResource);
				resultCount = (BigDecimal) sqlQuery.uniqueResult();
				if (!ObjectUtils.isEmpty(resultCount) && resultCount.longValue() > 0) {
					ssccPlacementNetworkValidationDto.setIndRsrcOtherFclty(Boolean.TRUE);
				} else
					ssccPlacementNetworkValidationDto.setIndRsrcOtherFclty(Boolean.FALSE);

				if (ssccPlacementNetworkValidationDto.getIndRsrcOtherFclty()) {
					sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(verifyRsrcValidFcltyType);
					sqlQuery.setParameter("idResource", idResource);
					resultCount = (BigDecimal) sqlQuery.uniqueResult();
					if (!ObjectUtils.isEmpty(resultCount) && resultCount.longValue() > 0) {
						ssccPlacementNetworkValidationDto.setIndRsrcValidFcltyType(Boolean.TRUE);
					} else
						ssccPlacementNetworkValidationDto.setIndRsrcValidFcltyType(Boolean.FALSE);
					if (ssccPlacementNetworkValidationDto.getIndRsrcValidFcltyType()) {
						sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(verifyRsrcActive);
						sqlQuery.setParameter("idResource", idResource);
						resultCount = (BigDecimal) sqlQuery.uniqueResult();
						if (!ObjectUtils.isEmpty(resultCount) && resultCount.longValue() > 0) {
							ssccPlacementNetworkValidationDto.setIndRsrcActive(Boolean.TRUE);
						} else
							ssccPlacementNetworkValidationDto.setIndRsrcActive(Boolean.FALSE);
					}
				}
			}
		}

		return ssccPlacementNetworkValidationDto;

	}
}
