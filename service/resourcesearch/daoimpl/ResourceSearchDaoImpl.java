package us.tx.state.dfps.service.resourcesearch.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.ResourceUtil;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.resource.dto.ResourceSearchResultDto;
import us.tx.state.dfps.service.resource.dto.ResourceValueBeanDto;
import us.tx.state.dfps.service.resourcesearch.dao.ResourceSearchDao;
import us.tx.state.dfps.service.subcare.dto.ResourceRtbExceptionDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * implements the methods declared in ResourceSearchDao Interface> Oct 31, 2017-
 * 6:46:36 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class ResourceSearchDaoImpl implements ResourceSearchDao {

	public ResourceSearchDaoImpl() {
		// Contructor method created for sonarQube
	}
	private static final Logger log = Logger.getLogger(ResourceSearchDaoImpl.class);
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ResourceSearchDaoImpl.getResourceDetails}")
	private transient String getResourceDetailsSql;

	@Value("${ResourceSearchDaoImpl.getResourceDetailsUsingIdPlcmtEvent}")
	private transient String getResourceDetailsUsingIdPlcmtEventSql;

	@Value("${ResourceSearchDaoImpl.getChildResources}")
	private transient String getChildResourcesSql;

	@Value("${ResourceSearchDaoImpl.selectResourceDetails}")
	private transient String selectResourceDetailsSql;

	@Value("${ResourceSearchDaoImpl.getResourceRtbExceptions}")
	private transient String getResourceRtbExceptionsSql;

	@Value("${ResourceSearchDaoImpl.findResourceRtbExceptionById}")
	private transient String findResourceRtbExceptionByIdSql;

	@Value("${ResourceSearchDaoImpl.addResourceRtbExceptionNextVal}")
	private transient String addResourceRtbExceptionNextVal;

	@Value("${ResourceSearchDaoImpl.addResourceRtbException}")
	private transient String addResourceRtbException;

	@Value("${ResourceSearchDaoImpl.updateResourceRtbException1}")
	private transient String updateResourceRtbException1;

	@Value("${ResourceSearchDaoImpl.updateResourceRtbException2}")
	private transient String updateResourceRtbException2;

	@Value("${ResourceSearchDaoImpl.updateResourceRtbException3}")
	private transient String updateResourceRtbException3;

	@Value("${ResourceSearchDaoImpl.getVendorDetails}")
	private transient String getVendorDetailsSql;

	@Value("${ResourceSearchDaoImpl.getFcl02ResourceDetails}")
	private transient String getFcl02ResourceDetails;

	private static final String WHITESPACE = " ";
	public static final char PERCENT = '%';

	/**
	 * Method Name: getResourceDetails Method Description: This method gets the
	 * details of the resource based on the resourceId.
	 * 
	 * @param resourceId
	 * @return ResourceSearchValueDto
	 */
	@Override
	public ResourceSearchResultDto getResourceDetails(String resourceId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getResourceDetailsSql)
				.addScalar("resourceName", StandardBasicTypes.STRING)
				.addScalar("identificationNum", StandardBasicTypes.STRING)
				.addScalar("streetAddress", StandardBasicTypes.STRING)
				.addScalar("streetAddress2", StandardBasicTypes.STRING).addScalar("nameCity", StandardBasicTypes.STRING)
				.addScalar("stateName", StandardBasicTypes.STRING).addScalar("zipCode", StandardBasicTypes.STRING)
				.addScalar("nameCounty", StandardBasicTypes.STRING)
				.addScalar("schoolDistrict", StandardBasicTypes.STRING)
				.addScalar("addressComments", StandardBasicTypes.STRING)
				.addScalar("phoneNumber", StandardBasicTypes.STRING)
				.addScalar("phoneExtension", StandardBasicTypes.STRING)
				.addScalar("phoneComments", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdateRa", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdateRp", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdateCr", StandardBasicTypes.DATE).addScalar("rsrcStatus", StandardBasicTypes.STRING)
				.addScalar("vendorId", StandardBasicTypes.STRING);
		query.setParameter("idResource", resourceId)
				.setResultTransformer(Transformers.aliasToBean(ResourceSearchResultDto.class));

		ResourceSearchResultDto resourceSearchValueDto = (ResourceSearchResultDto) query.list().get(0);

		String zipCodePrefix = ServiceConstants.BLANK;
		String zipCodeSuffix = ServiceConstants.BLANK;
		String zipCode = resourceSearchValueDto.getZipCode();
		StringTokenizer stoken = new StringTokenizer(zipCode, ServiceConstants.ZIP_CODE);
		if (stoken.countTokens() == ServiceConstants.TOKEN_COUNT) {
			zipCodePrefix = stoken.nextToken();
			zipCodeSuffix = ServiceConstants.EMPTY_STRING;
		} else {
			zipCodePrefix = stoken.nextToken();
			zipCodeSuffix = stoken.nextToken();
		}
		resourceSearchValueDto.setZipCode(zipCodePrefix);
		resourceSearchValueDto.setZipCodeSuffix(zipCodeSuffix);

		return resourceSearchValueDto;
	}

	/**
	 * Method Name: getResourceDetailsUsingIdPlcmtEvent Method Description: This
	 * method retrieves Resource Details using Placement Event Id.
	 * 
	 * @param idPlcmtEvent
	 * @return ResourceValueBeanDto
	 */
	@Override
	public ResourceValueBeanDto getResourceDetailsUsingIdPlcmtEvent(Long idPlcmtEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getResourceDetailsUsingIdPlcmtEventSql)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("addrRsrcStLn1", StandardBasicTypes.STRING)
				.addScalar("addrRsrcStLn2", StandardBasicTypes.STRING)
				.addScalar("addrRsrcCity", StandardBasicTypes.STRING)
				.addScalar("cdRsrcState", StandardBasicTypes.STRING).addScalar("addrRsrcZip", StandardBasicTypes.STRING)
				.addScalar("addrRsrcAttn", StandardBasicTypes.STRING).addScalar("cdRsrcCnty", StandardBasicTypes.STRING)
				.addScalar("cdRsrcInvolClosure", StandardBasicTypes.STRING)
				.addScalar("cdRsrcClosureRsn", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSourceInquiry", StandardBasicTypes.STRING)
				.addScalar("cdRsrcType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCampusType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaintainer", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSchDist", StandardBasicTypes.STRING)
				.addScalar("cdRsrcOwnership", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcHub", StandardBasicTypes.STRING).addScalar("cdRsrcCertBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcOperBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSetting", StandardBasicTypes.STRING)
				.addScalar("cdRsrcPayment", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCategory", StandardBasicTypes.STRING)
				.addScalar("cdRsrcEthnicity", StandardBasicTypes.STRING)
				.addScalar("cdRsrcLanguage", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRecmndReopen", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRegion", StandardBasicTypes.STRING)
				.addScalar("cdRsrcReligion", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRespite", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeStatus", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType1", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType2", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType3", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType4", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType5", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType6", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType7", StandardBasicTypes.STRING)
				.addScalar("cdRsrcStatus", StandardBasicTypes.STRING)
				.addScalar("dtRsrcMarriage", StandardBasicTypes.DATE).addScalar("dtRsrcClose", StandardBasicTypes.DATE)
				.addScalar("dtRsrcCert", StandardBasicTypes.DATE)
				.addScalar("idRsrcFaHomeStage", StandardBasicTypes.LONG)
				.addScalar("idRsrcFaHomeEvent", StandardBasicTypes.LONG)
				.addScalar("indRsrcWriteHist", StandardBasicTypes.STRING)
				.addScalar("indRsrcCareProv", StandardBasicTypes.STRING)
				.addScalar("indRsrcEmergPlace", StandardBasicTypes.STRING)
				.addScalar("indRsrcInactive", StandardBasicTypes.STRING)
				.addScalar("indRsrcTransport", StandardBasicTypes.STRING)
				.addScalar("indRsrcIndivStudy", StandardBasicTypes.STRING)
				.addScalar("indRsrcNonprs", StandardBasicTypes.STRING)
				.addScalar("nmRsrcLastUpdate", StandardBasicTypes.STRING)
				.addScalar("nmResource", StandardBasicTypes.STRING)
				.addScalar("nmRsrcNameIndex", StandardBasicTypes.STRING)
				.addScalar("nmRsrcContact", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcPhn", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcPhoneExt", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcFacilCapacity", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFacilAcclaim", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcVid", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcCampusNbr", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntChildren", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntFeAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntFeAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntMaAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntMaAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcAnnualIncome", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFmAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFmAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcMaAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcMaAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcOpenSlots", StandardBasicTypes.LONG)
				.addScalar("txtRsrcAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("txtRsrcComments", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMhmrCompCode", StandardBasicTypes.STRING)
				.addScalar("dtCclUpdate", StandardBasicTypes.DATE)
				.addScalar("cdRsrcMhmrSite", StandardBasicTypes.STRING)
				.addScalar("indRsrcContracted", StandardBasicTypes.STRING)
				.addScalar("nmLegal", StandardBasicTypes.STRING).addScalar("cdCertifyEntity", StandardBasicTypes.STRING)
				.addScalar("indRelativeCrgvr", StandardBasicTypes.STRING)
				.addScalar("indFictiveCrgvr", StandardBasicTypes.STRING)
				.addScalar("nbrPersons", StandardBasicTypes.LONG)
				.addScalar("indSignedAgreement", StandardBasicTypes.STRING)
				.addScalar("indIncomeQual", StandardBasicTypes.STRING)
				.addScalar("indManualGiven", StandardBasicTypes.STRING)
				.addScalar("indAllKinEmployed", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeTypes8", StandardBasicTypes.STRING)
				.addScalar("indRsrcChildSpecific", StandardBasicTypes.STRING)
				.addScalar("cdRsrcTypeSrv", StandardBasicTypes.STRING)
				.addScalar("indChildSpecificSchedRate", StandardBasicTypes.STRING)
				.addScalar("indSubsidyOnly", StandardBasicTypes.STRING)
				.addScalar("cdInvJurisdiction", StandardBasicTypes.STRING)
				.addScalar("indMultiLanguage", StandardBasicTypes.STRING)
				.addScalar("nmResourceUpper", StandardBasicTypes.STRING)
				.addScalar("nmPrContactFirst", StandardBasicTypes.STRING)
				.addScalar("nmPrContactLast", StandardBasicTypes.STRING)
				.addScalar("cdPrContactTitle", StandardBasicTypes.STRING)
				.addScalar("cdInactiveReason", StandardBasicTypes.STRING)
				.addScalar("txtInactiveComments", StandardBasicTypes.STRING)
				.addScalar("indContractedCare", StandardBasicTypes.STRING)
				.addScalar("indSpecialContract", StandardBasicTypes.STRING)
				.addScalar("indNonprsPca", StandardBasicTypes.STRING)
				.addScalar("indUnrelatedCrgvr", StandardBasicTypes.STRING);

		query.setParameter("idPlcmtEvent", idPlcmtEvent)
				.setResultTransformer(Transformers.aliasToBean(ResourceValueBeanDto.class));

		ResourceValueBeanDto resourceValueBeanDto = (ResourceValueBeanDto) query.list().get(0);

		return resourceValueBeanDto;
	}

	/**
	 * Method Name: getChildResources Method Description: This method retrieves
	 * the Child Resources of the given type for the Parent Resource.
	 * 
	 * @param resourceId
	 * @param cdRsrcLinkType
	 * @return List<ResourceValueBeanDto>
	 */
	@Override
	public List<ResourceValueBeanDto> getChildResources(Long resourceId, String cdRsrcLinkType) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getChildResourcesSql)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("addrRsrcStLn1", StandardBasicTypes.STRING)
				.addScalar("addrRsrcStLn2", StandardBasicTypes.STRING)
				.addScalar("addrRsrcCity", StandardBasicTypes.STRING)
				.addScalar("cdRsrcState", StandardBasicTypes.STRING).addScalar("addrRsrcZip", StandardBasicTypes.STRING)
				.addScalar("addrRsrcAttn", StandardBasicTypes.STRING).addScalar("cdRsrcCnty", StandardBasicTypes.STRING)
				.addScalar("cdRsrcInvolClosure", StandardBasicTypes.STRING)
				.addScalar("cdRsrcClosureRsn", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSourceInquiry", StandardBasicTypes.STRING)
				.addScalar("cdRsrcType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCampusType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaintainer", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSchDist", StandardBasicTypes.STRING)
				.addScalar("cdRsrcOwnership", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcHub", StandardBasicTypes.STRING).addScalar("cdRsrcCertBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcOperBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSetting", StandardBasicTypes.STRING)
				.addScalar("cdRsrcPayment", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCategory", StandardBasicTypes.STRING)
				.addScalar("cdRsrcEthnicity", StandardBasicTypes.STRING)
				.addScalar("cdRsrcLanguage", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRecmndReopen", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRegion", StandardBasicTypes.STRING)
				.addScalar("cdRsrcReligion", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRespite", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeStatus", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType1", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType2", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType3", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType4", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType5", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType6", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType7", StandardBasicTypes.STRING)
				.addScalar("cdRsrcStatus", StandardBasicTypes.STRING)
				.addScalar("dtRsrcMarriage", StandardBasicTypes.DATE).addScalar("dtRsrcClose", StandardBasicTypes.DATE)
				.addScalar("dtRsrcCert", StandardBasicTypes.DATE)
				.addScalar("idRsrcFaHomeStage", StandardBasicTypes.LONG)
				.addScalar("idRsrcFaHomeEvent", StandardBasicTypes.LONG)
				.addScalar("indRsrcWriteHist", StandardBasicTypes.STRING)
				.addScalar("indRsrcCareProv", StandardBasicTypes.STRING)
				.addScalar("indRsrcEmergPlace", StandardBasicTypes.STRING)
				.addScalar("indRsrcInactive", StandardBasicTypes.STRING)
				.addScalar("indRsrcTransport", StandardBasicTypes.STRING)
				.addScalar("indRsrcIndivStudy", StandardBasicTypes.STRING)
				.addScalar("indRsrcNonprs", StandardBasicTypes.STRING)
				.addScalar("nmRsrcLastUpdate", StandardBasicTypes.STRING)
				.addScalar("nmResource", StandardBasicTypes.STRING)
				.addScalar("nmRsrcNameIndex", StandardBasicTypes.STRING)
				.addScalar("nmRsrcContact", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcPhn", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcPhoneExt", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcFacilCapacity", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFacilAcclaim", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcVid", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcCampusNbr", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntChildren", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntFeAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntFeAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntMaAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntMaAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcAnnualIncome", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFmAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFmAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcMaAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcMaAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcOpenSlots", StandardBasicTypes.LONG)
				.addScalar("txtRsrcAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("txtRsrcComments", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMhmrCompCode", StandardBasicTypes.STRING)
				.addScalar("dtCclUpdate", StandardBasicTypes.DATE)
				.addScalar("cdRsrcMhmrSite", StandardBasicTypes.STRING)
				.addScalar("indRsrcContracted", StandardBasicTypes.STRING)
				.addScalar("nmLegal", StandardBasicTypes.STRING).addScalar("cdCertifyEntity", StandardBasicTypes.STRING)
				.addScalar("indRelativeCrgvr", StandardBasicTypes.STRING)
				.addScalar("indFictiveCrgvr", StandardBasicTypes.STRING)
				.addScalar("nbrPersons", StandardBasicTypes.LONG)
				.addScalar("indSignedAgreement", StandardBasicTypes.STRING)
				.addScalar("indIncomeQual", StandardBasicTypes.STRING)
				.addScalar("indManualGiven", StandardBasicTypes.STRING)
				.addScalar("indAllKinEmployed", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeTypes8", StandardBasicTypes.STRING)
				.addScalar("indRsrcChildSpecific", StandardBasicTypes.STRING)
				.addScalar("cdRsrcTypeSrv", StandardBasicTypes.STRING)
				.addScalar("indChildSpecificSchedRate", StandardBasicTypes.STRING)
				.addScalar("indSubsidyOnly", StandardBasicTypes.STRING)
				.addScalar("cdInvJurisdiction", StandardBasicTypes.STRING)
				.addScalar("indMultiLanguage", StandardBasicTypes.STRING)
				.addScalar("nmResourceUpper", StandardBasicTypes.STRING)
				.addScalar("nmPrContactFirst", StandardBasicTypes.STRING)
				.addScalar("nmPrContactLast", StandardBasicTypes.STRING)
				.addScalar("cdPrContactTitle", StandardBasicTypes.STRING)
				.addScalar("cdInactiveReason", StandardBasicTypes.STRING)
				.addScalar("txtInactiveComments", StandardBasicTypes.STRING)
				.addScalar("indContractedCare", StandardBasicTypes.STRING)
				.addScalar("indSpecialContract", StandardBasicTypes.STRING)
				.addScalar("indNonprsPca", StandardBasicTypes.STRING)
				.addScalar("indUnrelatedCrgvr", StandardBasicTypes.STRING);

		query.setParameter("idRsrcLinkParent", resourceId).setParameter("cdRsrcLinkType", cdRsrcLinkType)
				.setResultTransformer(Transformers.aliasToBean(ResourceValueBeanDto.class));

		List<ResourceValueBeanDto> resourceValueBeanDtos = query.list();

		return resourceValueBeanDtos;
	}

	/**
	 * Method Name: selectResourceDetails Method Description: This method
	 * retrieves Resource Details using idResource
	 * 
	 * @param idResource
	 * @return ResourceValueBeanDto @
	 */
	@Override
	public ResourceValueBeanDto selectResourceDetails(Long idResource) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(selectResourceDetailsSql)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("addrRsrcStLn1", StandardBasicTypes.STRING)
				.addScalar("addrRsrcStLn2", StandardBasicTypes.STRING)
				.addScalar("addrRsrcCity", StandardBasicTypes.STRING)
				.addScalar("cdRsrcState", StandardBasicTypes.STRING).addScalar("addrRsrcZip", StandardBasicTypes.STRING)
				.addScalar("addrRsrcAttn", StandardBasicTypes.STRING).addScalar("cdRsrcCnty", StandardBasicTypes.STRING)
				.addScalar("cdRsrcInvolClosure", StandardBasicTypes.STRING)
				.addScalar("cdRsrcClosureRsn", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSourceInquiry", StandardBasicTypes.STRING)
				.addScalar("cdRsrcType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCampusType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaintainer", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSchDist", StandardBasicTypes.STRING)
				.addScalar("cdRsrcOwnership", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcHub", StandardBasicTypes.STRING).addScalar("cdRsrcCertBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcOperBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSetting", StandardBasicTypes.STRING)
				.addScalar("cdRsrcPayment", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCategory", StandardBasicTypes.STRING)
				.addScalar("cdRsrcEthnicity", StandardBasicTypes.STRING)
				.addScalar("cdRsrcLanguage", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRecmndReopen", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRegion", StandardBasicTypes.STRING)
				.addScalar("cdRsrcReligion", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRespite", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeStatus", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType1", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType2", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType3", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType4", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType5", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType6", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType7", StandardBasicTypes.STRING)
				.addScalar("cdRsrcStatus", StandardBasicTypes.STRING)
				.addScalar("dtRsrcMarriage", StandardBasicTypes.DATE).addScalar("dtRsrcClose", StandardBasicTypes.DATE)
				.addScalar("dtRsrcCert", StandardBasicTypes.DATE)
				.addScalar("idRsrcFaHomeStage", StandardBasicTypes.LONG)
				.addScalar("idRsrcFaHomeEvent", StandardBasicTypes.LONG)
				.addScalar("indRsrcWriteHist", StandardBasicTypes.STRING)
				.addScalar("indRsrcCareProv", StandardBasicTypes.STRING)
				.addScalar("indRsrcEmergPlace", StandardBasicTypes.STRING)
				.addScalar("indRsrcInactive", StandardBasicTypes.STRING)
				.addScalar("indRsrcTransport", StandardBasicTypes.STRING)
				.addScalar("indRsrcIndivStudy", StandardBasicTypes.STRING)
				.addScalar("indRsrcNonprs", StandardBasicTypes.STRING)
				.addScalar("nmRsrcLastUpdate", StandardBasicTypes.STRING)
				.addScalar("nmResource", StandardBasicTypes.STRING)
				.addScalar("nmRsrcNameIndex", StandardBasicTypes.STRING)
				.addScalar("nmRsrcContact", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcPhn", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcPhoneExt", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcFacilCapacity", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFacilAcclaim", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcVid", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcCampusNbr", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntChildren", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntFeAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntFeAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntMaAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcIntMaAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcAnnualIncome", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFmAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFmAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcMaAgeMax", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcMaAgeMin", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcOpenSlots", StandardBasicTypes.LONG)
				.addScalar("txtRsrcAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("txtRsrcComments", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMhmrCompCode", StandardBasicTypes.STRING)
				.addScalar("dtCclUpdate", StandardBasicTypes.DATE)
				.addScalar("cdRsrcMhmrSite", StandardBasicTypes.STRING)
				.addScalar("indRsrcContracted", StandardBasicTypes.STRING)
				.addScalar("nmLegal", StandardBasicTypes.STRING).addScalar("cdCertifyEntity", StandardBasicTypes.STRING)
				.addScalar("indRelativeCrgvr", StandardBasicTypes.STRING)
				.addScalar("indFictiveCrgvr", StandardBasicTypes.STRING)
				.addScalar("nbrPersons", StandardBasicTypes.LONG)
				.addScalar("indSignedAgreement", StandardBasicTypes.STRING)
				.addScalar("indIncomeQual", StandardBasicTypes.STRING)
				.addScalar("indManualGiven", StandardBasicTypes.STRING)
				.addScalar("indAllKinEmployed", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeTypes8", StandardBasicTypes.STRING)
				.addScalar("indRsrcChildSpecific", StandardBasicTypes.STRING)
				.addScalar("cdRsrcTypeSrv", StandardBasicTypes.STRING)
				.addScalar("indChildSpecificSchedRate", StandardBasicTypes.STRING)
				.addScalar("indSubsidyOnly", StandardBasicTypes.STRING)
				.addScalar("cdInvJurisdiction", StandardBasicTypes.STRING)
				.addScalar("indMultiLanguage", StandardBasicTypes.STRING)
				.addScalar("nmResourceUpper", StandardBasicTypes.STRING)
				.addScalar("nmPrContactFirst", StandardBasicTypes.STRING)
				.addScalar("nmPrContactLast", StandardBasicTypes.STRING)
				.addScalar("cdPrContactTitle", StandardBasicTypes.STRING)
				.addScalar("cdInactiveReason", StandardBasicTypes.STRING)
				.addScalar("txtInactiveComments", StandardBasicTypes.STRING)
				.addScalar("indContractedCare", StandardBasicTypes.STRING)
				.addScalar("indSpecialContract", StandardBasicTypes.STRING)
				.addScalar("indNonprsPca", StandardBasicTypes.STRING)
				.addScalar("indUnrelatedCrgvr", StandardBasicTypes.STRING);

		query.setParameter("idResource", idResource)
				.setResultTransformer(Transformers.aliasToBean(ResourceValueBeanDto.class));

		ResourceValueBeanDto resourceValueBeanDto = (ResourceValueBeanDto) query.list().get(0);

		return resourceValueBeanDto;
	}

	/**
	 * Method Name: executeSearch Method Description: Search for a resource
	 * based on any number of input parameters specified in the input
	 * 
	 * @param resourceSearchValueDto
	 * @return resourceSearchValueDtoList
	 */

	@Override
	public List<ResourceSearchResultDto> executeSearch(ResourceSearchResultDto resourceSearchValueDto) {

		String sql = ServiceConstants.BLANK;

		boolean isAddResource = resourceSearchValueDto.getAddResource();
		// If you are coming from Add Resource page
		if (isAddResource) {
			sql = getSearchSQL(resourceSearchValueDto, isAddResource);

		} else {
			sql = getSearchSQL(resourceSearchValueDto);
		}
		System.out.println("sql ==> " + sql);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("resourceName", StandardBasicTypes.STRING)
				.addScalar("identificationNum", StandardBasicTypes.STRING)
				.addScalar("resourceInactive", StandardBasicTypes.STRING)
				.addScalar("rsrcContracted", StandardBasicTypes.STRING)
				.addScalar("resourceTypes", StandardBasicTypes.STRING)
				.addScalar("streetAddress", StandardBasicTypes.STRING)
				.addScalar("facilityType", StandardBasicTypes.STRING)
				.addScalar("streetAddress2", StandardBasicTypes.STRING)
				.addScalar("phoneNumber", StandardBasicTypes.STRING).addScalar("nameCounty", StandardBasicTypes.STRING)
				.addScalar("phoneExtension", StandardBasicTypes.STRING)
				.addScalar("invJurisdiction", StandardBasicTypes.STRING)
				.addScalar("indRelativeCaregiver", StandardBasicTypes.STRING)
				.addScalar("indFictiveCaregiver", StandardBasicTypes.STRING)
				.addScalar("rtbStatus", StandardBasicTypes.BOOLEAN)
				.addScalar("stateName", StandardBasicTypes.STRING)
				.addScalar("zipCode", StandardBasicTypes.STRING)
				.addScalar("streetAddress2Actuall", StandardBasicTypes.STRING);
		List<ResourceSearchResultDto> resourceSearchValueDtoList = query
				.setResultTransformer(Transformers.aliasToBean(ResourceSearchResultDto.class)).list();
		return resourceSearchValueDtoList;
	}

	/**
	 * Method Name: getSearchSQL Method Description: creates a sql string
	 * 
	 * @param resourceSearchValueDto
	 * @param isAddResource
	 * @return String
	 */
	public String getSearchSQL(ResourceSearchResultDto resourceSearchValueDto, Boolean isAddResource) {
		StringBuilder sql = new StringBuilder();

		sql.append(ServiceConstants.SELECT_CAPS_RESOURCE_COLUMNS);
		sql.append(ServiceConstants.FROM_CAPS_RESOURCE);
		sql.append(ServiceConstants.WHERE_ADDR_RSRC_ST_LN_1);
		sql.append(ServiceConstants.SINGLE_QUOTES);
		sql.append(resourceSearchValueDto.getStreetAddress());
		sql.append(ServiceConstants.SINGLE_QUOTES);
		sql.append(ServiceConstants.AND_ADDR_RSRC_CITY);
		sql.append(ServiceConstants.SINGLE_QUOTES);
		sql.append(resourceSearchValueDto.getNameCity());
		sql.append(ServiceConstants.SINGLE_QUOTES);
		sql.append(ServiceConstants.AND_CD_RSRC_STATE);
		sql.append(ServiceConstants.SINGLE_QUOTES);
		sql.append(resourceSearchValueDto.getStateName());
		sql.append(ServiceConstants.SINGLE_QUOTES);
		if(resourceSearchValueDto.getZipCode() != null) {
			sql.append(ServiceConstants.AND_SUBSTR_OF_ADDR_RSRC_ZIP);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(getLeftPadStr(resourceSearchValueDto.getZipCode()));
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		sql.append(ServiceConstants.AND_ROW_NUM);

		return sql.toString();
	}

	private String getLeftPadStr(String zipCode) {
		if(zipCode != null && zipCode.length()>= 5){
			return zipCode.substring(0,5);
		} else {
			return org.apache.commons.lang3.StringUtils.leftPad(zipCode, 5, '0');
		}
	}

	/**
	 * Method Name: getSearchSQL Method Description: creates a sql string
	 * 
	 * @param resourceSearchValueDto
	 * @return
	 */
	public String getSearchSQL(ResourceSearchResultDto resourceSearchValueDto) {
		String inactive = StringUtils.isEmpty(resourceSearchValueDto.getResourceInactive()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getResourceInactive();
		String resType = StringUtils.isEmpty(resourceSearchValueDto.getResourceTypes()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getResourceTypes();
		String resName = StringUtils.isEmpty(resourceSearchValueDto.getResourceName()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getResourceName();
		resName = ResourceUtil.removeOracleSpChars(resName);
		String resAddr1 = StringUtils.isEmpty(resourceSearchValueDto.getStreetAddress()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getStreetAddress();
		resAddr1 = ResourceUtil.removeOracleSpChars(resAddr1);
		String idType = StringUtils.isEmpty(resourceSearchValueDto.getIdentificationType()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getIdentificationType();
		String idNum = StringUtils.isEmpty(resourceSearchValueDto.getIdentificationNum()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getIdentificationNum();
		String rsrcContracted = StringUtils.isEmpty(resourceSearchValueDto.getRsrcContracted())
				? ServiceConstants.EMPTY_STR : resourceSearchValueDto.getRsrcContracted();
		String facType = StringUtils.isEmpty(resourceSearchValueDto.getFacilityType()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getFacilityType();
		String loc = StringUtils.isEmpty(resourceSearchValueDto.getLevelCare()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getLevelCare();
		String region = StringUtils.isEmpty(resourceSearchValueDto.getRsrcRegion()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getRsrcRegion();
		String city = StringUtils.isEmpty(resourceSearchValueDto.getNameCity()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getNameCity();
		String county = StringUtils.isEmpty(resourceSearchValueDto.getNameCounty()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getNameCounty();
		String zip = StringUtils.isEmpty(resourceSearchValueDto.getZipCode()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getZipCode();
		String zipSuffix = StringUtils.isEmpty(resourceSearchValueDto.getZipCodeSuffix()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getZipCodeSuffix();
		String state = StringUtils.isEmpty(resourceSearchValueDto.getStateName()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getStateName();
		String effectiveDate = StringUtils.isEmpty(resourceSearchValueDto.getEffectiveDate())
				? ServiceConstants.EMPTY_STR : resourceSearchValueDto.getEffectiveDate();
		String age = ServiceConstants.EMPTY_STRING;
		String category = StringUtils.isEmpty(resourceSearchValueDto.getCategory()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getCategory();
		String service = StringUtils.isEmpty(resourceSearchValueDto.getService()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getService();
		String program = StringUtils.isEmpty(resourceSearchValueDto.getProgram()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getProgram();
		String sex = StringUtils.isEmpty(resourceSearchValueDto.getGenderServed()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getGenderServed();
		String svcState = StringUtils.isEmpty(resourceSearchValueDto.getServiceStateName()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getServiceStateName();
		String svcRegion = StringUtils.isEmpty(resourceSearchValueDto.getServiceRegion()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getServiceRegion();
		String svcCounty = StringUtils.isEmpty(resourceSearchValueDto.getServiceCounty()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getServiceCounty();
		String facServtype = StringUtils.isEmpty(resourceSearchValueDto.getFacilityServiceType())
				? ServiceConstants.EMPTY_STR : resourceSearchValueDto.getFacilityServiceType();
		String indDonated = StringUtils.isEmpty(resourceSearchValueDto.getDonatedService()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getDonatedService();
		String invJuris = StringUtils.isEmpty(resourceSearchValueDto.getInvJurisdiction()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getInvJurisdiction();
		String language = StringUtils.isEmpty(resourceSearchValueDto.getLanguage()) ? ServiceConstants.EMPTY_STR
				: resourceSearchValueDto.getLanguage();
		Boolean rtbStatus = resourceSearchValueDto.getRtbStatus();

		String resourceStatus = ServiceConstants.BLANK;
		if ((ServiceConstants.CODE_02).equals(inactive)) {
			resourceStatus = ServiceConstants.CODE_02;
		} else if ((ServiceConstants.CODE_01.equals(inactive) || ServiceConstants.ACTV.equals(inactive))) {
			resourceStatus = ServiceConstants.CODE_01;
		}

		Boolean isIntakeReq = ResourceUtil.isIntakeRequest();
		Boolean isIntakeLawEnfReq = ResourceUtil.isIntakeLawEnfRequest();

		Boolean isAddResource = ServiceConstants.FALSEVAL;
		if (resourceSearchValueDto.getAddResource())
			isAddResource = ServiceConstants.TRUEVAL;
		resourceSearchValueDto.setAddResource(isAddResource);

		Boolean usingResourceService = ServiceConstants.FALSEVAL;

		if (!StringUtils.isEmpty(resourceSearchValueDto.getAgeServed())
				&& !resourceSearchValueDto.getAgeServed().equals(ServiceConstants.EMPTY_STRING)) {
			Long ageInMonths = Long.parseLong(resourceSearchValueDto.getAgeServed()) * ServiceConstants.ARC_MAX_MONTH;
			age = Long.toString(ageInMonths);
		}
		String clientChar = resourceSearchValueDto.getClientCharacteristics();
		Boolean servieCategorySearch = StringUtil.isValid(service) || StringUtil.isValid(category);

		Boolean characteristicsSearch = StringUtil.isValid(age) || StringUtil.isValid(sex)
				|| StringUtil.isValid(clientChar);

		StringBuilder sql = new StringBuilder();
		StringBuilder whereClause = new StringBuilder();
		Boolean whereClauseStarted = ServiceConstants.FALSEVAL;

		Boolean svcStatePresent = ServiceConstants.FALSEVAL;
		if (StringUtil.isValid(svcState) && (servieCategorySearch || StringUtil.isValid(program)
				|| StringUtil.isValid(svcRegion) || StringUtil.isValid(svcCounty))) {
			svcStatePresent = ServiceConstants.TRUEVAL;
		}

		if (StringUtil.isValid(idNum)) {

			idNum = idNum.trim();

			if (StringUtil.isValid(idNum) && ServiceConstants.CON_IDEN_TYPE.equals(idType)) {
				whereClause.append(ServiceConstants.CONTRACT_TABLE);
			}

			whereClause.append(ServiceConstants.WHERE2);

			if (ServiceConstants.CON_IDEN_TYPE.equals(idType)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.JOIN_CONDITION);
				whereClauseStarted = ServiceConstants.TRUEVAL;

				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_ID_CONTRACT);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(idNum);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}
			// Account Number Search.
		    if (ServiceConstants.CNU_IDEN_TYPE.equals(idType)){
		          whereClause.setLength (0);
		          whereClause.append (ServiceConstants.WHERE_CONTRACT_NUMBER);
		          whereClause.append(ServiceConstants.SINGLE_QUOTES);
		          whereClause.append(idNum);
		          whereClause.append(ServiceConstants.SINGLE_QUOTES);
		          whereClauseStarted = ServiceConstants.TRUEVAL;
		    }
			if (ServiceConstants.RSC_IDEN_TYPE.equals(idType)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_ID_RESOURCE);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(idNum);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}
			if (ServiceConstants.MHM_IDEN_TYPE.equals(idType)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_CD_RSRC_MHMR_COMP_CODE);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(idNum);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}
			if (ServiceConstants.LIC_IDEN_TYPE.equals(idType)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_NBR_RSRC_FACIL_ACCLAIM);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(idNum);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}
			if (ServiceConstants.PHN_IDEN_TYPE.equals(idType)) {
				whereClause.setLength(ServiceConstants.Zero);
				whereClause.append(ServiceConstants.WHERE_NBR_RSRC_PHONE);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(idNum);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}
		} else if (!StringUtil.isValid(effectiveDate)) {

			if (StringUtil.isValid(resType) && resType.equals(ServiceConstants.CODE_02) && StringUtil.isValid(zip)) {
				whereClause.append(ServiceConstants.LAW_ENFORC_ZIP_TABLE);
			}
			if (StringUtil.isValid(loc)) {
				whereClause.append(ServiceConstants.FACILITY_LOC_TABLE);
			}

			if (StringUtil.isValid(facType) && StringUtil.isValid(facServtype)) {
				whereClause.append(ServiceConstants.FACILITY_SERVICE_TYPE_TABLE);
			}

			if ((StringUtil.isValid(program) || svcStatePresent || StringUtil.isValid(svcRegion)
					|| StringUtil.isValid(svcCounty)) && !characteristicsSearch && !servieCategorySearch) {
				usingResourceService = ServiceConstants.TRUEVAL;
				whereClause.append(ServiceConstants.RESOURCE_SERVICE_TABLE);
			}

			if (StringUtil.isValid(language)) {
				whereClause.append(ServiceConstants.RESOURCE_LANGUAGE_TABLE);
			}

			if (isIntakeReq) {
				whereClause.append(ServiceConstants.RSRC_LINK_TABLE);
			}

			whereClause.append(ServiceConstants.APPEND_WHERE);

			if (StringUtil.isValid(invJuris)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_CD_INV_JURISDICTION);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(invJuris);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (StringUtil.isValid(county)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_CD_RSRC_CNTY);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(county);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if ((StringUtil.isValid(program) || svcStatePresent || StringUtil.isValid(svcRegion)
					|| StringUtil.isValid(svcCounty)) && !characteristicsSearch && !servieCategorySearch) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.JOIN_CONDITION_ID_RESOURCE);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (StringUtil.isValid(loc)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.JOIN_CONDITION_ID_RESOURCE_2);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (StringUtil.isValid(resType) && resType.equals(ServiceConstants.CODE_02) && StringUtil.isValid(zip)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.JOIN_CONDITION_ID_RESOURCE_3);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (servieCategorySearch && !characteristicsSearch) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_ID_RESOURCE_IN);
				whereClause.append(ServiceConstants.SELECT_ID_RESOURCE_RS);
				Boolean whereInnerSelectClauseStarted = ServiceConstants.FALSEVAL;
				if (StringUtil.isValid(category)) {
					if (whereInnerSelectClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.WHERE_CD_RSRC_SVC_CATEG_RSRC);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(category);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereInnerSelectClauseStarted = ServiceConstants.TRUEVAL;
				}
				if (StringUtil.isValid(service)) {
					if (whereInnerSelectClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.WHERE_CD_RSRC_SVC_SERVICE);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(service);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereInnerSelectClauseStarted = ServiceConstants.TRUEVAL;
				}

				{
					if (StringUtil.isValid(svcRegion)) {
						if (whereInnerSelectClauseStarted) {
							whereClause.append(ServiceConstants.AND2);
						}
						if (svcRegion.equals(ServiceConstants.SVC_REGION)) {
							whereClause.append(ServiceConstants.WHERE_CD_RSRC_SVC_REGION);
						} else {
							whereClause.append(ServiceConstants.WHERE_CD_RSRC_SVC_REGION_2);
							whereClause.append(ServiceConstants.SINGLE_QUOTES);
							whereClause.append(svcRegion);
							whereClause.append(ServiceConstants.SINGLE_QUOTES);
						}
						whereInnerSelectClauseStarted = ServiceConstants.TRUEVAL;
					}
					if (StringUtil.isValid(svcCounty)) {
						if (whereInnerSelectClauseStarted) {
							whereClause.append(ServiceConstants.AND2);
						}
						whereClause.append(ServiceConstants.WHERE_CD_RSRC_SVC_CNTY);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(svcCounty);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereInnerSelectClauseStarted = ServiceConstants.TRUEVAL;
					}

					if (svcStatePresent) {
						if (whereInnerSelectClauseStarted) {
							whereClause.append(ServiceConstants.AND2);
						}
						whereClause.append(ServiceConstants.WHERE_CD_RSRC_SVC_STATE);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(svcState);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereInnerSelectClauseStarted = ServiceConstants.TRUEVAL;
					}
				}

				if (StringUtil.isValid(program)) {
					if (whereInnerSelectClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.WHERE_CD_RSRC_SVC_PROGRAM);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(program);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereInnerSelectClauseStarted = ServiceConstants.TRUEVAL;
				}
				whereClause.append(ServiceConstants.GROUP_BY_CONDITION);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (!servieCategorySearch && characteristicsSearch) {
				age = age.trim();
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_ID_RESOURCE_IN);
				whereClause.append(ServiceConstants.SELECT_ID_RESOURCE_RC);
				whereClauseStarted = ServiceConstants.FALSEVAL;
				if ((StringUtil.isValid(age)) && (!StringUtil.isValid(sex))) {
					whereClause.append(ServiceConstants.CHAR_SQUARE_BRACKET_OPEN);
					whereClause.append(ServiceConstants.CHAR_SQUARE_BRACKET_OPEN);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(age);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_F_AGE);
					whereClause.append(ServiceConstants.CHAR_SQUARE_BRACKET_OPEN);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(age);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_M_AGE);
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}
				if ((StringUtil.isValid(age)) && (StringUtil.isValid(sex))) {
					if (sex.equals(ServiceConstants.FEMALE)) {
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(age);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_F_AGE_2);
					}
					if (sex.equals(ServiceConstants.MALE)) {
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(age);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_M_AGE_2);
					}
					if (sex.equals(ServiceConstants.BISEXUAL)) {
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(age);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_F_AGE_3);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(age);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_M_AGE_2);
						whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_SEX);
					}
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}
				if ((!StringUtil.isValid(age)) && (StringUtil.isValid(sex))) {
					if (sex.equals(ServiceConstants.FEMALE)) {
						whereClause.append(ServiceConstants.FEMALE_CD_RSRC_CHAR_SEX);
					}
					if (sex.equals(ServiceConstants.MALE)) {
						whereClause.append(ServiceConstants.MALE_CD_RSRC_CHAR_SEX);
					}
					if (sex.equals(ServiceConstants.BISEXUAL)) {
						whereClause.append(ServiceConstants.BISEXUAL_CD_RSRC_CHAR_SEX);
					}
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}
				if (StringUtil.isValid(clientChar)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_CHRCTR);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(clientChar);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}

				{
					if (StringUtil.isValid(state)) {
						if (whereClauseStarted) {
							whereClause.append(ServiceConstants.AND2);
						}
						whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_STATE);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(state);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClauseStarted = ServiceConstants.TRUEVAL;
					}
					if (StringUtil.isValid(svcRegion)) {
						if (whereClauseStarted) {
							whereClause.append(ServiceConstants.AND2);
						}
						if (svcRegion.equals(ServiceConstants.SVC_REGION)) {
							whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_REGION);
						} else {
							whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_REGION_2);
							whereClause.append(ServiceConstants.SINGLE_QUOTES);
							whereClause.append(svcRegion);
							whereClause.append(ServiceConstants.SINGLE_QUOTES);
						}
						whereClauseStarted = ServiceConstants.TRUEVAL;
					}
					if (StringUtil.isValid(svcCounty)) {
						if (whereClauseStarted) {
							whereClause.append(ServiceConstants.AND2);
						}
						whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_CNTY);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(svcCounty);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClauseStarted = ServiceConstants.TRUEVAL;
					}
				}
				if (StringUtil.isValid(program)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_PROGRAM);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(program);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}
				whereClause.append(ServiceConstants.GROUP_BY_CONDITION);
			}

			if (servieCategorySearch && characteristicsSearch) {
				age = age.trim();

				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClauseStarted = ServiceConstants.TRUEVAL;
				whereClause.append(ServiceConstants.WHERE_ID_RESOURCE_IN);
				whereClause.append(ServiceConstants.SELECT_ID_RESOURCE_RC);
				whereClauseStarted = ServiceConstants.FALSEVAL;

				if ((StringUtil.isValid(age)) && (!StringUtil.isValid(sex))) {
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(age);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_F_AGE);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(age);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_M_AGE);
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}
				if ((StringUtil.isValid(age)) && (StringUtil.isValid(sex))) {
					if (sex.equals(ServiceConstants.FEMALE)) {
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(age);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_F_AGE_2);

					}
					if (sex.equals(ServiceConstants.MALE)) {
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(age);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_M_AGE_2);

					}
					if (sex.equals(ServiceConstants.BISEXUAL)) {
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(age);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_F_AGE_3);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(age);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(ServiceConstants.BETWEEN_NBR_RSRC_CHAR_M_AGE_2);
						whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_SEX);

					}
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}
				if ((!StringUtil.isValid(age)) && (StringUtil.isValid(sex))) {
					if (sex.equals(ServiceConstants.FEMALE)) {
						whereClause.append(ServiceConstants.FEMALE_CD_RSRC_CHAR_SEX);
					}
					if (sex.equals(ServiceConstants.MALE)) {
						whereClause.append(ServiceConstants.MALE_CD_RSRC_CHAR_SEX);
					}
					if (sex.equals(ServiceConstants.BISEXUAL)) {
						whereClause.append(ServiceConstants.BISEXUAL_CD_RSRC_CHAR_SEX);
					}
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}

				if (StringUtil.isValid(clientChar)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_CHRCTR);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(clientChar);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}

				if (StringUtil.isValid(program)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_PROGRAM);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(program);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}

				if (StringUtil.isValid(category)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_CATEG_RSRC);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(category);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}

				if (StringUtil.isValid(service)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_SERVICE);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(service);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}

				{
					if (StringUtil.isValid(state)) {
						if (whereClauseStarted) {
							whereClause.append(ServiceConstants.AND2);
						}
						whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_STATE);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(state);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClauseStarted = ServiceConstants.TRUEVAL;
					}

					if (StringUtil.isValid(svcRegion)) {
						if (whereClauseStarted) {
							whereClause.append(ServiceConstants.AND2);
						}
						if (svcRegion.equals(ServiceConstants.SVC_REGION)) {
							whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_REGION);
						} else {
							whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_REGION_2);
							whereClause.append(ServiceConstants.SINGLE_QUOTES);
							whereClause.append(svcRegion);
							whereClause.append(ServiceConstants.SINGLE_QUOTES);
						}
						whereClauseStarted = ServiceConstants.TRUEVAL;
					}

					if (StringUtil.isValid(svcCounty)) {
						if (whereClauseStarted) {
							whereClause.append(ServiceConstants.AND2);
						}
						whereClause.append(ServiceConstants.WHERE_CD_RSRC_CHAR_CNTY);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(svcCounty);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClauseStarted = ServiceConstants.TRUEVAL;
					}
				}
				whereClause.append(ServiceConstants.GROUP_BY_CONDITION);

			}

			if (StringUtil.isValid(resType)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_CD_RSRC_TYPE);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(resType);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (StringUtil.isValid(program) && !servieCategorySearch && !characteristicsSearch) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_RS_CD_RSRC_SVC_PROGRAM);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(program);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (StringUtil.isValid(svcRegion) && ((!servieCategorySearch && !characteristicsSearch))) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				if (svcRegion.equals(ServiceConstants.SVC_REGION)) {
					whereClause.append(ServiceConstants.WHERE_RS_CD_RSRC_SVC_REGION);
				} else {
					whereClause.append(ServiceConstants.WHERE_RS_CD_RSRC_SVC_REGION_2);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(svcRegion);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
				}
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (StringUtil.isValid(svcCounty) && ((!servieCategorySearch && !characteristicsSearch))) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_RS_CD_RSRC_SVC_CNTY);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(svcCounty);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (StringUtil.isValid(city)) {
				city = city.trim();
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_CR_ADDR_RSRC_CITY);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(city.toUpperCase());
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (StringUtil.isValid(state)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				whereClause.append(ServiceConstants.WHERE_CR_CD_RSRC_STATE);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(state);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (StringUtil.isValid(zip)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}
				if (StringUtil.isValid(resType) && resType.equals(ServiceConstants.CODE_02)) {
					if (StringUtil.isValid(zipSuffix) && (zip.length() == ServiceConstants.FIVE_NUM)
							&& (zipSuffix.length() == ServiceConstants.FOUR_NUM)) {
						whereClause.append(ServiceConstants.REPLACE_LEZ_NBR_LAW_ENFORC_ZIP);
						zip = zip.concat(zipSuffix);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(zip);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
					} else {
						whereClause.append(ServiceConstants.REPLACE_LEZ_NBR_LAW_ENFORC_ZIP_2);
						zip = zip + ServiceConstants.PERCENT;
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
						whereClause.append(zip);
						whereClause.append(ServiceConstants.SINGLE_QUOTES);
					}
				} else if (StringUtil.isValid(zipSuffix) && (zip.length() == ServiceConstants.FIVE_NUM)
						&& (zipSuffix.length() == ServiceConstants.FOUR_NUM)) {
					whereClause.append(ServiceConstants.REPLACE_CR_ADDR_RSRC_ZIP);
					zip = zip.concat(zipSuffix);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(zip);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
				} else {
					whereClause.append(ServiceConstants.REPLACE_CR_ADDR_RSRC_ZIP_2);
					zip = zip + ServiceConstants.PERCENT;
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(zip);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
				}

				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			if (StringUtil.isValid(inactive)) {
				if (StringUtil.isValid(resourceStatus)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.CR_CD_RSRC_STATUS);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(resourceStatus);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}
			}

			if (StringUtil.isValid(rsrcContracted)) {
				if (rsrcContracted.equals(ServiceConstants.ONE)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.CR_IND_RSRC_CONTRACTED_Y);
					whereClauseStarted = ServiceConstants.TRUEVAL;
				} else if (rsrcContracted.equals(ServiceConstants.TWO)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.CR_IND_RSRC_CONTRACTED_N);
					whereClauseStarted = ServiceConstants.TRUEVAL;
				}
			}

			if (StringUtil.isValid(facType)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.AND2);
				}

				if (ServiceConstants.RESOURCE_TYPES_06.equals(resType)
						&& ServiceConstants.FACILITY_TYPE_92.equals(facType)) {
					whereClause.append(ServiceConstants.CR_IND_CONTRACTED_CARE);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(ServiceConstants.CHAR_IND_Y);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
				} else {
					whereClause.append(ServiceConstants.CR_CD_RSRC_FACIL_TYPE);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(facType);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
				}

				if (StringUtil.isValid(facServtype)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.JOIN_CONDITION_ID_RESOURCE_4);

					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.WHERE_FS_CD_FACIL_SVC_TYPE);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);
					whereClause.append(facServtype);
					whereClause.append(ServiceConstants.SINGLE_QUOTES);

					if (StringUtil.isValid(inactive) && inactive.equals(ServiceConstants.ACTV)) {
						whereClause.append(ServiceConstants.WHERE_FS_DT_END);
						whereClause.append(getNULLDateFormat());
						whereClause.append(ServiceConstants.CLOSEBRAC);
					}
				}
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}

			Boolean LOCCodeFound = ServiceConstants.FALSEVAL;
			if (StringUtil.isValid(loc)) {
				if (loc.equals(ServiceConstants.LEVEL_CARE_10)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_1);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_20)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_2);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_30)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_3);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_40)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_4);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_50)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_5);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_60)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_6);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_90)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_7);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_100)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_8);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_110)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_9);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_210)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_1_OR_2);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_220)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_3_OR_4);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_230)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_5);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_240)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_6);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				if (loc.equals(ServiceConstants.LEVEL_CARE_250)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_10);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				//added TFC
				if (loc.equals(ServiceConstants.LEVEL_CARE_260)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_11);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				//Added Intense
				if (loc.equals(ServiceConstants.LEVEL_CARE_270)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_12);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				//artf213667 Dev 140.9 New Service Level for QRTP in Resource Search
				if (loc.equals(ServiceConstants.LEVEL_CARE_280)) {
					if (whereClauseStarted) {
						whereClause.append(ServiceConstants.AND2);
					}
					whereClause.append(ServiceConstants.FL_CD_FLOC_STATUS_13);
					whereClauseStarted = ServiceConstants.TRUEVAL;
					LOCCodeFound = ServiceConstants.TRUEVAL;
				}
				
				if (LOCCodeFound) {
					whereClause.append(ServiceConstants.WHERE_FL_DT_FLOC_END);
					whereClause.append(getNULLDateFormat());
					whereClause.append(ServiceConstants.CLOSEBRAC);
				}
			}

			if (StringUtil.isValid(language)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.SQL_AND_STATEMENT);
				}
				whereClause.append(ServiceConstants.JOIN_CONDITION_ID_RESOURCE_5);
				whereClauseStarted = ServiceConstants.TRUEVAL;

				whereClause.append(ServiceConstants.SQL_AND_STATEMENT);
				whereClause.append(ServiceConstants.WHERE_RL_CD_LANGUAGE);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(language);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(ServiceConstants.SQL_AND_STATEMENT);
				whereClause.append(ServiceConstants.WHERE_RL_DT_END);
				whereClause.append(getNULLDateFormat());
				whereClause.append(ServiceConstants.CLOSEBRAC);

			}

			if (isIntakeReq) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.SQL_AND_STATEMENT);
				}
				whereClause.append(ServiceConstants.JOIN_CONDITION_ID_RESOURCE_6);
				whereClauseStarted = ServiceConstants.TRUEVAL;
			}
		} else if (StringUtil.isValid(effectiveDate)) {

			if (indDonated.equals(ServiceConstants.DONATED_SERVICE)) {
				sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_SELECT);
				sql.append(ServiceConstants.CONTRACT_COUNTY_TABLE);

				//Changes for Defect 14070. The Resource Search should include Resource Type of 07 and 01.				
				if(resType.length()==2) { //If resType's length is 2, then use resType's value in the SQL.
					sql.append(ServiceConstants.WHERE_CR_CD_RSRC_TYPE);
					sql.append(ServiceConstants.SINGLE_QUOTES);
					sql.append(resType);
					sql.append(ServiceConstants.SINGLE_QUOTES);
				} else if(resType.length()>2 && ServiceConstants.PRVDR_OR_SSCC_PRVDR.equals(resType)) { //If resType equals '07,01'
					sql.append(ServiceConstants.WHERE_CD_RSRC_TYPE_IN);
				}

				sql.append(ServiceConstants.WHERE_CR_CD_RSRC_STATUS);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(resourceStatus);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(ServiceConstants.WHERE_CR_CD_CNCNTY_SERVICE);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(service);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(ServiceConstants.WHERE_CC_CD_CNCNTY_COUNTY);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(county);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(ServiceConstants.WHERE_CC_DT_CNCNTY_EFFECTIVE);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(effectiveDate);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(ServiceConstants.WHERE_CC_DT_CNCNTY_EFFECTIVE_DATE);
				sql.append(ServiceConstants.WHERE_CC_DT_CNCNTY__END);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(effectiveDate);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(ServiceConstants.WHERE_CC_DT_CNCNTY__END_DATE);
				sql.append(ServiceConstants.JOIN_CONDITION_ID_RESOURCE_7);
				if (rtbStatus != null) {
					sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1).append(rtbStatus ? " WHERE " : " WHERE NOT ").append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_2);
				} else {
					sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1);
				}
				sql.append(ServiceConstants.ORDER_CONDITION);
			} else {
				sql.append(ServiceConstants.RESOURCE_SERVICE_SEARCH_RTB_SELECT);
				sql.append(ServiceConstants.RESOURCE_SERVICE_TABLE);
				sql.append(" ");
				sql.append(ServiceConstants.WHERE_CR_CD_RSRC_TYPE);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(resType);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(ServiceConstants.WHERE_CR_CD_RSRC_STATUS);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(resourceStatus);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(ServiceConstants.WHERE_RS_CD_RSRC_SVC_SERVICE);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(service);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(ServiceConstants.SQL_AND_STATEMENT);
				sql.append(ServiceConstants.WHERE_RS_CD_RSRC_SVC_CNTY);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(county);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(ServiceConstants.SQL_AND_STATEMENT);
				sql.append(ServiceConstants.WHERE_RS_CD_RSRC_SVC_REGION_2);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(region);
				sql.append(ServiceConstants.SINGLE_QUOTES);
				sql.append(ServiceConstants.SQL_AND_STATEMENT);
				sql.append(ServiceConstants.JOIN_CONDITION_ID_RESOURCE);
				if (rtbStatus != null) {
					sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1).append(rtbStatus ? " WHERE " : " WHERE NOT ").append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_2);
				} else {
					sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1);
				}
				sql.append(ServiceConstants.ORDER_CONDITION);
			}
		}

		if (!TypeConvUtil.isNullOrEmpty(resourceSearchValueDto.getSsccRsrcId())
				&& resourceSearchValueDto.getSsccRsrcId() > ServiceConstants.ZERO_VAL) {
			whereClause.append(ServiceConstants.AND2);
			whereClause.append(ServiceConstants.WHERE_ID_RESOURCE_IN);
			whereClause.append(ServiceConstants.OPENBRAC);
			whereClause.append(ServiceConstants.SELECT_ID_RSRC_MEMBER);
			whereClause.append(resourceSearchValueDto.getSsccRsrcId());
			whereClause.append(ServiceConstants.WHERE_DT_START);
			whereClause.append(ServiceConstants.SINGLE_QUOTES);

			whereClause.append(DateUtils.dateString(resourceSearchValueDto.getSsccDtPlcmtStart()));
			whereClause.append(ServiceConstants.SINGLE_QUOTES);
			whereClause.append(ServiceConstants.TRUNC_DATE_FORMAT);
			whereClause.append(ServiceConstants.WHERE_DT_END);
			whereClause.append(ServiceConstants.SINGLE_QUOTES);

			whereClause.append(DateUtils.dateString(resourceSearchValueDto.getSsccDtPlcmtStart()));
			whereClause.append(ServiceConstants.SINGLE_QUOTES);

			whereClause.append(ServiceConstants.TRUNC_DATE_FORMAT);
			whereClause.append(ServiceConstants.CLOSEBRACS);
			whereClause.append(ServiceConstants.UNION_SELECT_ID_RSRC_MEMBER);
			whereClause.append(resourceSearchValueDto.getSsccRsrcId());
			whereClause.append(ServiceConstants.TRUNC_DT_START);
			whereClause.append(ServiceConstants.SINGLE_QUOTES);

			whereClause.append(DateUtils.dateString(resourceSearchValueDto.getSsccDtPlcmtStart()));
			whereClause.append(ServiceConstants.SINGLE_QUOTES);

			whereClause.append(ServiceConstants.TRUNC_DATE_FORMAT);
			whereClause.append(ServiceConstants.DT_JOIN_CONDITION);
			whereClause.append(ServiceConstants.SQL_CLOSE_PAREN_STATEMENT);
		}

		Boolean isResNamePresent = ServiceConstants.FALSEVAL;
		Boolean isResAddrPresent = ServiceConstants.FALSEVAL;
		String resNmClause2Sql = ServiceConstants.EMPTY_STRING;

		if (isIntakeLawEnfReq) { // already broken, probably unused
			whereClauseStarted = ServiceConstants.TRUEVAL;
			if (StringUtil.isValid(resName)) {
				if (whereClauseStarted) {
					whereClause.append(ServiceConstants.SQL_AND_STATEMENT);
				}
				whereClause.append(ServiceConstants.UPPER_NM_RESOURCE);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
				whereClause.append(ServiceConstants.PERCENT);
				whereClause.append(resName.toUpperCase());
				whereClause.append(ServiceConstants.PERCENT);
				whereClause.append(ServiceConstants.SINGLE_QUOTES);
			}
			sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_SELECT);
			sql.append(whereClause)
					.append(ServiceConstants.SQL_UNION_COMMAND).append(ServiceConstants.SERLECT_DISTINCT_COLUMNS) // currently broken, probably unused, note the where clause (SERLECT_DISTINCT_COLUMNS) added here
					.append(ServiceConstants.SINGLE_QUOTES).append(county).append(ServiceConstants.SINGLE_QUOTES)
					.append(ServiceConstants.AND_CR_CD_RSRC_TYPE).append(ServiceConstants.SINGLE_QUOTES) // which is incompatable with the where clause (WHERE_CR_CD_RSRC_TYPE) added here.
					.append(resType).append(ServiceConstants.SINGLE_QUOTES)
					.append(ServiceConstants.AND_CR_CD_RSRC_STATE).append(ServiceConstants.SINGLE_QUOTES).append(state)
					.append(ServiceConstants.SINGLE_QUOTES).append(ServiceConstants.WHERE_CR_CD_RSRC_STATUS)
					.append(ServiceConstants.SINGLE_QUOTES).append(resourceStatus)
					.append(ServiceConstants.SINGLE_QUOTES).append(ServiceConstants.ROWNUM);
			if (rtbStatus != null) {
				sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1).append(rtbStatus ? " WHERE " : " WHERE NOT ").append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_2);
			} else {
				sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1);
			}
		} else if (StringUtil.isValid(resAddr1) && StringUtil.isValid(resName)) { // VERIFIED
			String tmpWhereClause = (TypeConvUtil.isNullOrEmpty(whereClause)
					|| ServiceConstants.WHERE.equals(whereClause.toString().trim())) ? whereClause.toString()
							: whereClause.toString() + ServiceConstants.SQL_AND_STATEMENT;
			String resAddrClauseSql = getResAddrSearchClause(resAddr1, ServiceConstants.TRUEVAL);
			String resNmClauseSql = getResNameSearchClause(resName);

			String searchSql = ServiceConstants.RESOURCE_ID_SEARCH_RTB_SELECT;
			if (isIntakeReq) {
				searchSql = ResourceUtil.replace(searchSql, ServiceConstants.CR_CD_INV_JURISDICTION,
						ServiceConstants.REPLACE_VALUE);
			}
			if (ServiceConstants.SERVER_IMPACT) {
				resNmClause2Sql = getResNameSearchClsSecondPart(resName, whereClause, resAddrClauseSql, isIntakeReq,
						usingResourceService);
				searchSql = ResourceUtil.replace(searchSql, ServiceConstants.LTRIM_CR_NM_RESOURCE,
						ServiceConstants.REPLACE_VALUE_1);
				sql.append(searchSql).append(tmpWhereClause).append(resNmClauseSql)
						.append(ServiceConstants.SQL_AND_STATEMENT).append(resAddrClauseSql);

				addRownumSelectAroundQry(sql, ServiceConstants.ADD_ROW_NUM_SELECT_AROUND_QRY_3, usingResourceService);
				sql.append(resNmClause2Sql);
			} else {
				searchSql = ResourceUtil.replace(searchSql, ServiceConstants.LTRIM_CR_NM_RESOURCE,
						ServiceConstants.REPLACE_VALUE_2);
				sql.append(searchSql).append(tmpWhereClause).append(resNmClauseSql)
						.append(ServiceConstants.SQL_AND_STATEMENT).append(resAddrClauseSql);
			}
			if (rtbStatus != null) {
				sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1).append(rtbStatus ? " WHERE " : " WHERE NOT ").append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_2);
			} else {
				sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1);
			}
			isResNamePresent = ServiceConstants.TRUEVAL;
			isResAddrPresent = ServiceConstants.TRUEVAL;
		} else if (StringUtil.isValid(resName)) { // VERIFIED
			String tmpWhereClause = (TypeConvUtil.isNullOrEmpty(whereClause)
					|| ServiceConstants.WHERE.equals(whereClause.toString().trim())) ? whereClause.toString()
							: whereClause.toString();
			//if (rtbStatus != null) { tmpWhereClause = new StringBuilder(tmpWhereClause).append(rtbStatus ? " WHERE " : " WHERE NOT ").append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1).toString(); };
			String resNmClause = getResNameSearchClause(resName);
			String searchSql = ServiceConstants.RESOURCE_ID_SEARCH_RTB_SELECT;
			if (isIntakeReq) {
				searchSql = ResourceUtil.replace(searchSql, ServiceConstants.CR_CD_INV_JURISDICTION,
						ServiceConstants.REPLACE_VALUE);
			}
			if (ServiceConstants.SERVER_IMPACT) {
				resNmClause2Sql = getResNameSearchClsSecondPart(resName, whereClause, ServiceConstants.EMPTY_STRING,
						isIntakeReq, usingResourceService);
				searchSql = ResourceUtil.replace(searchSql, ServiceConstants.LTRIM_CR_NM_RESOURCE,
						ServiceConstants.REPLACE_VALUE_1);
				sql.append(searchSql).append(tmpWhereClause).append(ServiceConstants.SQL_AND_STATEMENT).append(resNmClause);
				addRownumSelectAroundQry(sql, ServiceConstants.ADD_ROW_NUM_SELECT_AROUND_QRY_3, usingResourceService);
				sql.append(resNmClause2Sql);
			} else {
				searchSql = ResourceUtil.replace(searchSql, ServiceConstants.LTRIM_CR_NM_RESOURCE,
						ServiceConstants.REPLACE_VALUE_2);
				sql.append(searchSql).append(tmpWhereClause).append(ServiceConstants.SQL_AND_STATEMENT).append(resNmClause);
			}
			if (rtbStatus != null) {
				sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1).append(rtbStatus ? " WHERE " : " WHERE NOT ").append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_2);
			} else {
				sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1);
			}
			isResNamePresent = ServiceConstants.TRUEVAL;
		} else if (StringUtil.isValid(resAddr1)) { // VERIFIED
			String tmpWhereClause = (TypeConvUtil.isNullOrEmpty(whereClause)
					|| ServiceConstants.WHERE.equals(whereClause.toString().trim())) ? whereClause.toString()
							: whereClause.toString() + ServiceConstants.SQL_AND_STATEMENT;
			String resAddrClause = getResAddrSearchClause(resAddr1, ServiceConstants.FALSEVAL);
			String searchSql = ServiceConstants.RESOURCE_ID_SEARCH_RTB_SELECT;
			if (isIntakeReq) {
				searchSql = ResourceUtil.replace(searchSql, ServiceConstants.CR_CD_INV_JURISDICTION,
						ServiceConstants.REPLACE_VALUE);
			}
			if (ServiceConstants.SERVER_IMPACT) {
				searchSql = ResourceUtil.replace(searchSql, ServiceConstants.LTRIM_CR_NM_RESOURCE,
						ServiceConstants.REPLACE_VALUE_1);
				sql.append(searchSql).append(tmpWhereClause).append(resAddrClause);

				addRownumSelectAroundQry(sql, ServiceConstants.ADD_ROW_NUM_SELECT_AROUND_QRY_1, usingResourceService);
			} else {
				searchSql = ResourceUtil.replace(searchSql, ServiceConstants.LTRIM_CR_NM_RESOURCE,
						ServiceConstants.REPLACE_VALUE_2);
				sql.append(searchSql).append(tmpWhereClause).append(resAddrClause);
			}
			if (rtbStatus != null) {
				sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1).append(rtbStatus ? " WHERE " : " WHERE NOT ").append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_2);
			} else {
				sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1);
			}
			isResAddrPresent = ServiceConstants.TRUEVAL;
		}

		if (isResNamePresent == ServiceConstants.FALSEVAL && isResAddrPresent == ServiceConstants.FALSEVAL
				&& isIntakeLawEnfReq == ServiceConstants.FALSEVAL
				&& StringUtil.isValid(effectiveDate) == ServiceConstants.FALSEVAL) {
			String searchSql = ServiceConstants.RESOURCE_ID_SEARCH_RTB_SELECT;
			if (isIntakeReq) {
				searchSql = ResourceUtil.replace(searchSql, ServiceConstants.CR_CD_INV_JURISDICTION, ServiceConstants.REPLACE_VALUE);
			}

			sql.append(searchSql).append(whereClause);
			if (rtbStatus != null) sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1).append(rtbStatus ? " WHERE " : " WHERE NOT ").append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_2);

			if (ServiceConstants.SERVER_IMPACT) {
				addRownumSelectAroundQry(sql, ServiceConstants.ADD_ROW_NUM_SELECT_AROUND_QRY_2, usingResourceService);
			}
			if (rtbStatus == null) sql.append(ServiceConstants.RESOURCE_ID_SEARCH_RTB_POST_WHERE_1);
			sql.append(ServiceConstants.ORDER_CONDITION);
		}

		return sql.toString();

	}

	/**
	 * Method Name: getResNameSearchClause Method Description: creates sql query
	 * 
	 * @param resName
	 * @return String
	 */
	private String getResNameSearchClause(String resName) {

		resName = resName.trim();
		String resNmSearchClause = ServiceConstants.BLANK;
		Boolean whereClauseStarted = ServiceConstants.FALSEVAL;
		if (ServiceConstants.MOBILE_IMPACT) {
			if (StringUtil.isValid(resName)) {
				resName = resName.trim();
				resName = resName.toUpperCase();
				List<String> nameVector = parseString(resName.trim());
				if (nameVector.size() != ServiceConstants.Zero) {
					if (resName.length() == ServiceConstants.INDEX_2) {
						resNmSearchClause += ServiceConstants.WHERE_UPPER_CR_NM_RSRC_NAME_INDEX
								+ ServiceConstants.SINGLE_QUOTES + (resName) + ServiceConstants.SINGLE_QUOTES;
						whereClauseStarted = ServiceConstants.TRUEVAL;
					}
					Integer initialValue = ServiceConstants.Zero;
					for (String resNameVector : nameVector) {
						if (whereClauseStarted) {
							resNmSearchClause += ServiceConstants.AND2;
						}

						resNmSearchClause += ServiceConstants.WHERE_UPPER_CR_NM_RESOURCE_LIKE;
						resNameVector = ServiceConstants.PERCENT + ServiceConstants.SINGLE_QUOTES + resNameVector
								+ ServiceConstants.SINGLE_QUOTES + ServiceConstants.PERCENT + (resNameVector);
						whereClauseStarted = ServiceConstants.TRUEVAL;
						initialValue++;
					}
				}
			}
		} else {
			resNmSearchClause = ServiceConstants.CONTAINS_QUERY1_CR_NM_RESOURCE
					+ ResourceUtil.escapeOracleSpChar(resName) + ServiceConstants.CONTAINS_QUERY1_CONTIUED;
		}

		return resNmSearchClause;

	}

	/**
	 * Method Name: parseString Method Description:parseString using
	 * StringTokenizer
	 * 
	 * @param searchParm
	 * @return List<String>
	 */
	private List<String> parseString(String searchParm) {

		StringTokenizer st = new StringTokenizer(searchParm, WHITESPACE);
		List<String> myVector = new ArrayList<>();

		while (st.hasMoreTokens()) {
			String wordtoCheck = st.nextToken();

			myVector.add(wordtoCheck);

		}
		return myVector;
	}

	/**
	 * Method Name: getResNameSearchClsSecondPart Method Description: creates
	 * Sql query
	 * 
	 * @param resName
	 * @param whereClause
	 * @param resAddrClause
	 * @param isIntakeReq
	 * @param usingResourceService
	 * @return String
	 */
	private String getResNameSearchClsSecondPart(String resName, StringBuilder whereClause, String resAddrClause,
			Boolean isIntakeReq, Boolean usingResourceService) {

		String tmpWhereClause = (TypeConvUtil.isNullOrEmpty(whereClause)
				|| ServiceConstants.WHERE.equals(whereClause.toString().trim())) ? whereClause.toString()
						: whereClause.toString() + ServiceConstants.SQL_AND_STATEMENT;
		String union = ServiceConstants.SQL_UNION;
		StringBuilder sql = new StringBuilder(ServiceConstants.SINGLE_WHITESPACE);
		String addrConj = StringUtil.isValid(resAddrClause) ? ServiceConstants.SQL_AND_STATEMENT
				: ServiceConstants.EMPTY_STRING;
		if (ResourceUtil.isSingleCharPresent(resName)) {
			sql.append(ServiceConstants.SELECT_COLUMNS);
			if (isIntakeReq) {
				sql.append(ServiceConstants.ADD_COLUMNS);
			}
			sql.append(ServiceConstants.FROM_TABLE_CR).append(tmpWhereClause).append(ServiceConstants.WHERE_NM_RESOURCE)
					.append(ResourceUtil.escapeOracleSpChar(resName)).append(ServiceConstants.PERCENT_END_STRING)
					.append(addrConj).append(resAddrClause);

			if (ServiceConstants.SERVER_IMPACT) {
				addRownumSelectAroundQry(sql, ServiceConstants.ADD_ROW_NUM_SELECT_AROUND_QRY_3, usingResourceService);
			}
		} else {
			sql.append(ServiceConstants.SELECT_COLUMNS);
			if (isIntakeReq) {
				sql.append(ServiceConstants.ADD_COLUMNS);
			}
			sql.append(ServiceConstants.FROM_TABLE_CR).append(tmpWhereClause)
					.append(ServiceConstants.WHERE_UPPER_NM_RESOURCE).append(ResourceUtil.escapeOracleSpChar(resName))
					.append(ServiceConstants.END_PERCENT_BRACS).append(addrConj).append(resAddrClause);

			if (ServiceConstants.SERVER_IMPACT) {
				addRownumSelectAroundQry(sql, ServiceConstants.ADD_ROW_NUM_SELECT_AROUND_QRY_3, usingResourceService);
			}
		}
		sql.insert(ServiceConstants.Zero, union);

		return sql.toString();
	}

	/**
	 * Method Name: getResAddrSearchClause Method Description:creates Sql query
	 * 
	 * @param resAddr1
	 * @param isResNamePresent
	 * @return String
	 */
	private String getResAddrSearchClause(String resAddr1, Boolean isResNamePresent) {

		resAddr1 = resAddr1.trim();
		Integer index = (isResNamePresent) ? ServiceConstants.INDEX_2 : ServiceConstants.INDEX_1;

		String resAddrSearchClause = ServiceConstants.EMPTY_STRING;
		if (ServiceConstants.MOBILE_IMPACT) {
			resAddrSearchClause = ServiceConstants.WHERE_CR_ADDR_RSRC_ST_LN_1 + ServiceConstants.SINGLE_QUOTES
					+ (resAddr1) + ServiceConstants.SINGLE_QUOTES;
		} else {
			resAddrSearchClause = ServiceConstants.CONTAINS_QUERY2_CR_ADDR_RSRC_ST_LN_1
					+ ResourceUtil.escapeOracleSpChar(resAddr1) + ServiceConstants.CONTAINS_QUERY2_CONTIUED + index
					+ ServiceConstants.CONTAINS_QUERY2_CONDITION;
		}

		return resAddrSearchClause;
	}

	/**
	 * Method Name: addRownumSelectAroundQry Method Description: adds Rownum in
	 * select query
	 * 
	 * @param sql
	 * @param noOfRowsReturned
	 * @param usingResourceService
	 */
	private void addRownumSelectAroundQry(StringBuilder sql, Long noOfRowsReturned, Boolean usingResourceService) {
		if (usingResourceService) {
			sql.insert(ServiceConstants.Zero, ServiceConstants.PRE_SELECT_FOR_RESOURCE_SERVICE);
			sql.append(ServiceConstants.WHERE_ROWNUM_LESS_THAN).append(noOfRowsReturned).append(WHITESPACE);
		} else {
			sql.append(ServiceConstants.WHERE_ROWNUM_GREATER_THAN).append(noOfRowsReturned);
		}
	}

	/**
	 * Method Name: getNULLDateFormat Method Description: gives constant Date
	 * 
	 * @return String
	 */
	private String getNULLDateFormat() {
		String date = ServiceConstants.EMPTY_STRING;

		if (ServiceConstants.SERVER_IMPACT) {
			date = ServiceConstants.NULL_DATE_FORMAT;
		} else {
			date = ServiceConstants.NULL_DATE_FORMAT_COSTANT_DATE;
		}

		return date;
	}

	/**
	 * Method Name: getResourceStageAsso Method Description: Gets the Resource
	 * ids values for the given stage
	 * 
	 * @param resourceSearchDBDto
	 * @param resourceIds
	 * @return PaginationResultDto
	 */
	@Override
	public List<ResourceSearchResultDto> getResourceStageAsso(ResourceSearchResultDto resourceSearchValueDto,
			List<String> resourceIds) {

		List<ResourceSearchResultDto> resultsList = new ArrayList<>();

		String sql = ServiceConstants.EMPTY_STRING;

		sql = getResourceIdSQL(resourceIds);

		List<BigDecimal> idResourceList = sessionFactory.getCurrentSession().createSQLQuery(sql).list();

		if (!idResourceList.isEmpty()) {
			for (BigDecimal bigDecimal : idResourceList) {
				ResourceSearchResultDto newResourceSearchValueDto = new ResourceSearchResultDto();
				newResourceSearchValueDto.setIdentificationNum(bigDecimal.toString());
				resultsList.add(newResourceSearchValueDto);
			}
		}

		return resultsList;

	}

	/**
	 * Method Name: getResourceIdSQL Method Description: creates sql query for
	 * getResourceStageAsso method
	 * 
	 * @param resourceIds
	 * @return String
	 */
	private String getResourceIdSQL(List<String> resourceIds) {

		StringBuilder sql = new StringBuilder();
		sql.append(ServiceConstants.RESOURCE_STAGE_SQL);
		for (String resourceId : resourceIds) {
			sql.append(ServiceConstants.SINGLE_COMMA);
			sql.append(resourceId);
		}

		sql.append(ServiceConstants.CLOSEBRAC);

		return sql.toString();
	}

	/**
	 * Method Name: populateParentResInfo Method Description: populates
	 * ParentResourceName in ResourceSearchValueDto
	 * 
	 * @param sortedResourceSet
	 * @param parentResIdList
	 * @return SortedSet<ResourceSearchResultDto>
	 */
	@Override
	public SortedSet<ResourceSearchResultDto> populateParentResInfo(
			SortedSet<ResourceSearchResultDto> sortedResourceSet, List<String> parentResIdList) {
		if (parentResIdList.size() > ServiceConstants.Zero) {
			StringBuilder parentRsrsSql = new StringBuilder();
			parentRsrsSql.append(ServiceConstants.SELECT_FOR_POPULATE_PARENT_RES_INFO);
			Integer index = ServiceConstants.Zero;
			for (String parentResId : parentResIdList) {
				parentRsrsSql.append(parentResId);
				if (index < parentResIdList.size() - ServiceConstants.ONE_VAL) {
					parentRsrsSql.append(ServiceConstants.COMMA_SPACE);
				}
				index++;
			}
			parentRsrsSql.append(ServiceConstants.CLOSEBRAC);

			Query query = sessionFactory.getCurrentSession().createSQLQuery(parentRsrsSql.toString())
					.addScalar("resourceName", StandardBasicTypes.STRING)
					.addScalar("identificationNum", StandardBasicTypes.STRING);
			List<ResourceSearchResultDto> resourceSearchValueDtoList = query.list();
			for (ResourceSearchResultDto resourceSearchValueDto : resourceSearchValueDtoList) {
				String parentResId = resourceSearchValueDto.getIdentificationNum();
				String parentResName = resourceSearchValueDto.getResourceName();
				for (ResourceSearchResultDto resourceSearchValueDto2 : sortedResourceSet) {
					if (parentResId.equals(resourceSearchValueDto2.getParentResourceId())) {
						resourceSearchValueDto2.setParentResourceName(parentResName);
						break;
					}
				}
			}

		}
		return sortedResourceSet;
	}

	// artf187193 BR 1.2 Manual Override of RTB Indicator
	public List<ResourceRtbExceptionDto> findResourceRtbExceptions(Long resourceId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getResourceRtbExceptionsSql)
				.addScalar("idResourceRtbException", StandardBasicTypes.LONG)
				.addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("createdDate", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("lastUpdateDate", StandardBasicTypes.DATE)
				.addScalar("idUpdatedPerson", StandardBasicTypes.LONG)
				.addScalar("comments", StandardBasicTypes.STRING)
				.addScalar("endedDate", StandardBasicTypes.DATE);
		query.setParameter("idResource", resourceId)
				.setResultTransformer(Transformers.aliasToBean(ResourceRtbExceptionDto.class));

		return query.list();
	}

	// artf187193 BR 1.2 Manual Override of RTB Indicator
	public List<ResourceRtbExceptionDto> findResourceRtbExceptionById(Long idResourceRtbException) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(findResourceRtbExceptionByIdSql)
				.addScalar("idResourceRtbException", StandardBasicTypes.LONG)
				.addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("createdDate", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("lastUpdateDate", StandardBasicTypes.DATE)
				.addScalar("idUpdatedPerson", StandardBasicTypes.LONG)
				.addScalar("comments", StandardBasicTypes.STRING)
				.addScalar("endedDate", StandardBasicTypes.DATE);
		query.setParameter("idResourceRtbException", idResourceRtbException)
				.setResultTransformer(Transformers.aliasToBean(ResourceRtbExceptionDto.class));

		return query.list();
	}

	public Long addResourceRtbException(ResourceRtbExceptionDto rtbException) {
		Query idQuery = sessionFactory.getCurrentSession().createSQLQuery(addResourceRtbExceptionNextVal);
		Long newId = ((BigDecimal)idQuery.uniqueResult()).longValue();

		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(addResourceRtbException)
				.setParameter("idResourceRtbException", newId)
				.setParameter("idResource", rtbException.getIdResource())
				.setParameter("idPerson", rtbException.getIdPerson())
				.setParameter("idCreatedPerson", rtbException.getIdCreatedPerson())
				.setParameter("idUpdatedPerson", rtbException.getIdUpdatedPerson())
				.setParameter("comments", rtbException.getComments())
		);
		int rowsCreated = query.executeUpdate();
		return newId;
	}

	public Long updateResourceRtbException(ResourceRtbExceptionDto rtbException) {
		StringBuilder sqlBuilder = new StringBuilder(updateResourceRtbException1);
		if (rtbException.getEndedDate() != null) {
			sqlBuilder.append(updateResourceRtbException2);
		}
		sqlBuilder.append(updateResourceRtbException3);
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString())
				.setParameter("idUpdatedPerson", rtbException.getIdUpdatedPerson()));
		if (rtbException.getEndedDate() != null) {
			query.setParameter("endedDate", rtbException.getEndedDate());
		}
		query.setParameter("comments", rtbException.getComments())
			 .setParameter("idResourceRtbException", rtbException.getIdResourceRtbException());
		int rowupdated = query.executeUpdate();
		return Long.valueOf(rowupdated);
	}

	@Override
	public String  getVendorDetails(String identificationNumber) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getVendorDetailsSql)
				.addScalar("vendorId", StandardBasicTypes.STRING);
		query.setParameter("idResource", identificationNumber)
				.setResultTransformer(Transformers.aliasToBean(ResourceSearchResultDto.class));
		int  result = query.list().size();
		String vendorId = null;
		if(result>0) {
			ResourceSearchResultDto resourceSearchResultDto = (ResourceSearchResultDto) query.list().get(0);
			vendorId = resourceSearchResultDto.getVendorId();
		}
		return vendorId;
	}

	@Override
	public List<Long> getFcl02Resources(List<String> idFacilities){
		Query queryCharacteristics = sessionFactory.getCurrentSession().createSQLQuery(getFcl02ResourceDetails)
				.addScalar("idResource", StandardBasicTypes.LONG);
		return queryCharacteristics.setParameterList("idFacilities",idFacilities).list();
	}
}
