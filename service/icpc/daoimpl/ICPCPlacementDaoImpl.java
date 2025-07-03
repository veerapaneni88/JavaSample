/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: DAO Implementation for the ICPC page services
 *Aug 03, 2018- 4:24:24 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.icpc.daoimpl;
import org.omg.CORBA.Object;
import us.tx.state.dfps.common.domain.IcpcDocument;
import us.tx.state.dfps.common.domain.IcpcTransmittal;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ICPCEmailLogReq;
import us.tx.state.dfps.service.common.request.ICPCPlacementReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.icpc.dao.ICPCPlacementDao;
import us.tx.state.dfps.service.icpc.dto.*;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.personsearch.dao.PersonSearchDao;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: DAO
 * Implementation for the ICPC page services Aug 03, 2018- 4:24:24 PM © 2017
 * Texas Department of Family and Protective Services
 */
@SuppressWarnings("unchecked")
@Repository
public class ICPCPlacementDaoImpl implements ICPCPlacementDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PersonSearchDao personSearchDao;

	@Value("${ICPCPlacementDaoImpl.getSummaryInfo}")
	private String getSummaryInfo;

	@Value("${ICPCPlacementDaoImpl.getTransmittalListInfo}")
	private String getTransmittalListInfo;

	@Value("${ICPCPlacementDaoImpl.getAllTransmittalInfo}")
	private String getAllTransmittalInfo;

	@Value("${ICPCPlacementDaoImpl.getAllRequestInfo}")
	private String getAllRequestInfo;

	@Value("${ICPCPlacementDaoImpl.getTransmittalInfo}")
	private String getTransmittalInfo;

	@Value("${ICPCPlacementDaoImpl.getTransmittalRequestInfo}")
	private String getTransmittalRequestInfo;

	@Value("${ICPCPlacementDaoImpl.getPlacementResourceName}")
	private String getPlacementResourceName;

	@Value("${PlacementDaoImpl.getTransmittalSiblings}")
	private String getTransmittalSiblings;

	@Value("${ICPCPlacementDaoImpl.getPlacementStatusInfo}")
	private String getPlacementStatusInfoSql;

	@Value("${ICPCPlacementDaoImpl.getPlacementResourceInfo}")
	private String getPlacementResourceInfoSql;

	@Value("${ICPCPlacementDaoImpl.getPlacementStatusPerson}")
	private String getPlacementStatusPersonSql;

	@Value("${ICPCPlacementDaoImpl.getPrimayChildInfo}")
	private String getPrimayChildInfoSql;

	@Value("${ICPCPlacementDaoImpl.getAgencyDetailsList}")
	private String getAgencyDetailsList;

	@Value("${ICPCPlacementDaoImpl.agencyDetails}")
	private String agencyDetails;

	@Value("${ICPCPlacementDaoImpl.agencyDetailsForOtherPlacement}")
	private String agencyDetailsForOtherPlacement;

	@Value("${ICPCPlacementDaoImpl.getDocumentListInfo}")
	private String getDocumentListInfo;

	@Value("${ICPCPlacementDaoImpl.getPlacementRequestInfo}")
	private String getPlacementRequestInfo;

	@Value("${ICPCPlacementDaoImpl.getPortalResource}")
	private String getPortalResource;

	@Value("${ICPCPlacementDaoImpl.getPlacementRequestPerson}")
	private String getPlacementRequestPerson;

	@Value("${ICPCPlacementDaoImpl.getPersonPhone}")
	private String getPersonPhone;

	@Value("${ICPCPlacementDaoImpl.getPlacementResourceInfo}")
	private String getPlacementResourceInfo;

	@Value("${ICPCPlacementDaoImpl.getChildRequest}")
	private String getChildRequest;

	@Value("${ICPCPlacementDaoImpl.getPlacementAddress.personLinkAddress}")
	private String personLinkAddress;

	@Value("${ICPCPlacementDaoImpl.getPlacementAddress.personNewAddress}")
	private String personNewAddress;

	@Value("${ICPCPlacementDaoImpl.getPlacementAddress.personOldAddress}")
	private String personOldAddress;

	@Value("${ICPCPlacementDaoImpl.getPlacementAddress.resourceLinkAddress}")
	private String resourceLinkAddress;

	@Value("${ICPCPlacementDaoImpl.getPlacementAddress.resourceNewAddress}")
	private String resourceNewAddress;

	@Value("${ICPCPlacementDaoImpl.getPlacementAddress.personOldAddress}")
	private String resourceOldAddress;

	@Value("${ICPCPlacementDaoImpl.getStatusPersonAddressLink}")
	private String getStatusPersonAddressLinkSql;

	@Value("${ICPCPlacementDaoImpl.getStatusResourceAddressLink}")
	private String getStatusResourceAddressLinkSql;

	@Value("${ICPCPlacementDaoImpl.getrequestEnclosure}")
	private String getrequestEnclosureSql;

	@Value("${ICPCPlacementDaoImpl.getPrimaryWorkerInfo}")
	private String getPrimaryWorkerInfo;

	@Value("${ICPCPlacementDaoImpl.getEligibility}")
	private String getEligibility;

	@Value("${ICPCPlacementDaoImpl.getDisasterReqRlf}")
	private String getDisasterReqRlf;

	@Value("${ICPCPlacementDaoImpl.retrieveStageId}")
	private String retrieveIntakeStageId;

	@Value("${ICPCPlacementDaoImpl.getICPCportalRequestStage}")
	private String getICPCportalRequestStage;

	@Value("${ICPCPlacementDaoImpl.verifyAgencyExist}")
	private String verifyAgencyExist;

	@Value("${ICPCPlacementDaoImpl.getICPCLegacyNumber}")
	private String getICPCLegacyNumber;

	@Value("${ICPCPlacementDaoImpl.getIcpcDocument}")
	private String getIcpcDocument;
	
	@Value("${ICPCPlacementDaoImpl.getIcpcPrimaryChild}")
	private String getIcpcPrimaryChild;

	@Value("${ICPCPlacementDaoImpl.getEventFromRequest}")
	private String getEventFromRequestSql;

	@Value("${ICPCPlacementDaoImpl.getRecentAprvEvent}")
	private String getRecentAprvEvent;

	@Value("${ICPCPlacementDaoImpl.getTransmissionLst}")
	private String getTransmissionLst;

	@Value("${ICPCPlacementDaoImpl.getAllPendingTransmissionLst}")
	private String getAllPendingTransmissionLst;

	@Value("${ICPCPlacementDaoImpl.getTransmissionChildLst}")
	private String getTransmissionChildLst;

	@Value("${ICPCPlacementDaoImpl.getTransmissionAttachments}")
	private String getTransmissionAttachments;

	@Value("${ICPCPlacementDaoImpl.getTransmissionAttachment}")
	private String getTransmissionAttachment;

	@Value("${ICPCPlacementDaoImpl.getSecondaryWorkers}")
	private String getSecondaryWorkers;

	@Value("${ICPCPlacementDaoImpl.getAgencyInfo}")
	private String getAgencyInfo;

	@Value("${ICPCPlacementDaoImpl.getAllAgencyInfo}")
	private String getAllAgencyInfo;

	@Value("${ICPCPlacementDaoImpl.getICPCEventLink}")
	private String getICPCEventLink;

	@Value("${ICPCPlacementDaoImpl.getICPCRequest}")
	private String getICPCRequest;

	@Value("${ICPCPlacementDaoImpl.getAgency}")
	private String getAgency;

	@Value("${ICPCPlacementDaoImpl.getPlacementStatusInfoByRequest}")
	private String getPlacementStatusByRequestSql;

	@Value("${ICPCPlacementDaoImpl.getAprvPlacementStatusInfoByRequest}")
	private String getAprvPlacementStatusInfoByRequestSql;

	@Value("${ICPCPlacementDaoImpl.getTransmittalLinkDocuments}")
	private String getTransmittalLinkDocumentsSql;

	@Value("${ICPCPlacementDaoImpl.getSiblingsRequests}")
	private String getSiblingsRequestsSql;

	@Value("${ICPCPlacementDaoImpl.getAllChildrenDocumentListInfo}")
	private String getAllChildrenDocumentListInfoSql;

	@Value("${ICPCPlacementDaoImpl.getPlacementRequestEventIds}")
	private String getPlacementRequestEventIdsSql;

	@Value("${ICPCPlacementDaoImpl.fetchChildDetailsFromStaging}")
	private String fetchChildDetailsFromStagingSql;

	@Value("${ICPCPlacementDaoImpl.updateAgencyEntityPhone}")
	private String updateAgencyEntityPhoneSql;

	@Value("${ICPCPlacementDaoImpl.getLinkedNeiceChildren}")
	private String getLinkedNeiceChildrenSql;

	@Autowired
	MessageSource messageSource;

	public static final String PLACEMENT_RESOURCE_TYPE = "R";
	public static final String PLACEMENT_PERSON_TYPE = "P";

	/**
	 * 
	 * Method Name: getSummaryInfo Method Description: Get the ICPC Summary Info
	 * for the Case
	 * 
	 * @param idCase
	 * @return ICPCPlacementRequestDto
	 */
	@Override
	public ICPCPlacementRequestDto getSummaryInfo(Long idCase) {

		ICPCPlacementRequestDto icpcPlacementRequestDto = null;

		List<ICPCPlacementRequestDto> icpcPlacementRequestDtoList = (List<ICPCPlacementRequestDto>) sessionFactory
				.getCurrentSession().createSQLQuery(getSummaryInfo)
				.addScalar("idICPCsubmission", StandardBasicTypes.LONG)
				.addScalar("nbrLegacyCase", StandardBasicTypes.STRING)
				.addScalar("nbrOtherCase", StandardBasicTypes.STRING).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(ICPCPlacementRequestDto.class)).list();
		if (!CollectionUtils.isEmpty(icpcPlacementRequestDtoList))
			icpcPlacementRequestDto = icpcPlacementRequestDtoList.get(0);
		if (ObjectUtils.isEmpty(icpcPlacementRequestDto)) {
			icpcPlacementRequestDto = new ICPCPlacementRequestDto();
		}
		return icpcPlacementRequestDto;
	}

	/**
	 * 
	 * Method Name: getTransmittalListInfo Method Description: Get the
	 * Transmittal List for the ICPC Request
	 * 
	 * @param idICPCRequest
	 * @return List<ICPCTransmittalDto>
	 */
	@Override
	public List<ICPCTransmittalDto> getTransmittalListInfo(Long idICPCRequest) {

		List<ICPCTransmittalDto> transmittalList = new ArrayList<ICPCTransmittalDto>();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getTransmittalListInfo)
				.addScalar("idICPCTransmittal", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("cdTransmittalType", StandardBasicTypes.STRING)
				.addScalar("cdSendingState", StandardBasicTypes.STRING)
				.addScalar("cdReceivingState", StandardBasicTypes.STRING)
				.addScalar("cdAdditionalInfo", StandardBasicTypes.STRING).addScalar("dtSent", StandardBasicTypes.DATE)
				.addScalar("nmAttn", StandardBasicTypes.STRING).addScalar("txtOther", StandardBasicTypes.STRING)
				.addScalar("txtComment", StandardBasicTypes.STRING)
				.addScalar("cdTransmittalStatus", StandardBasicTypes.STRING)
				.addScalar("txtStatusDesc", StandardBasicTypes.STRING)
				.setParameter("idICPCRequest", idICPCRequest)
				.setResultTransformer(Transformers.aliasToBean(ICPCTransmittalDto.class));

		transmittalList = (List<ICPCTransmittalDto>) query.list();
		return transmittalList;
	}

	/**
	 * 
	 * Method Name: getAllTransmittalList Method Description: Get all the
	 * Transmittal List for the Case
	 * 
	 * @param idCase
	 * @return List<ICPCTransmittalDto>
	 */
	@Override
	public List<ICPCTransmittalDto> getAllTransmittalList(Long idCase) {

		List<ICPCTransmittalDto> transmittalList = new ArrayList<ICPCTransmittalDto>();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getAllTransmittalInfo)
				.addScalar("nmResource", StandardBasicTypes.STRING).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("idICPCTransmittal", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("cdTransmittalType", StandardBasicTypes.STRING)
				.addScalar("cdSendingState", StandardBasicTypes.STRING)
				.addScalar("cdReceivingState", StandardBasicTypes.STRING)
				.addScalar("cdAdditionalInfo", StandardBasicTypes.STRING).addScalar("dtSent", StandardBasicTypes.DATE)
				.addScalar("nmAttn", StandardBasicTypes.STRING).addScalar("txtOther", StandardBasicTypes.STRING)
				.addScalar("txtComment", StandardBasicTypes.STRING)
				.addScalar("cdTransmittalStatus", StandardBasicTypes.STRING)
				.addScalar("txtStatusDesc", StandardBasicTypes.STRING)
				.setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(ICPCTransmittalDto.class));

		transmittalList = (List<ICPCTransmittalDto>) query.list();
		return transmittalList;
	}

	/**
	 * 
	 * Method Name: getAllRequestList Method Description: Get all the ICPC
	 * Request List for the Case
	 * 
	 * @param idCase
	 * @return List<ICPCTransmittalDto>
	 */
	@Override
	public List<ICPCRequestDto> getAllRequestList(Long idCase) {

		List<ICPCRequestDto> requestList = new ArrayList<>();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getAllRequestInfo)
				.addScalar("cdRequestType", StandardBasicTypes.STRING).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStatus", StandardBasicTypes.STRING).addScalar("dtSentReceived", StandardBasicTypes.DATE)
				.addScalar("cdDecision", StandardBasicTypes.STRING)
				.addScalar("nmPlacementResource", StandardBasicTypes.STRING)
				.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(ICPCRequestDto.class));

		requestList = (List<ICPCRequestDto>) query.list();

		return requestList;
	}

	/**
	 * Method Name: getTransmittalInfo Method Description: Get the Transmittal
	 * Info for the passed ICPC request ID or Transmittal ID
	 * 
	 * @param idTransmittal
	 * @param idICPCRequest
	 * @param idStage
	 * @return ICPCTransmittalDto
	 */
	@Override
	public ICPCTransmittalDto getTransmittalInfo(Long idTransmittal, Long idICPCRequest, Long idStage) {

		ICPCTransmittalDto icpcTransmittalDto = new ICPCTransmittalDto();
		String sql = ServiceConstants.EMPTY_STRING;
		Long idInput;
		if (!ObjectUtils.isEmpty(idTransmittal) && idTransmittal > 0) {
			sql = getTransmittalInfo;
			idInput = idTransmittal;
		} else {
			sql = getTransmittalRequestInfo;
			idInput = idICPCRequest;
		}

		Query query = (Query) sessionFactory.getCurrentSession()
				.createSQLQuery(sql)
				.addScalar("idICPCTransmittal", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("cdRequestType", StandardBasicTypes.STRING)
				.addScalar("cdSendingState", StandardBasicTypes.STRING)
				.addScalar("cdReceivingState", StandardBasicTypes.STRING)
				.addScalar("cdTransmittalType", StandardBasicTypes.STRING)
				.addScalar("cdAdditionalInfo", StandardBasicTypes.STRING)
				.addScalar("dtSent", StandardBasicTypes.DATE)
				.addScalar("nmAttn", StandardBasicTypes.STRING)
				.addScalar("txtOther", StandardBasicTypes.STRING)
				.addScalar("txtComment", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmPerson", StandardBasicTypes.STRING)
				.addScalar("idNeiceCase", StandardBasicTypes.STRING)
				.addScalar("idPersonNeice", StandardBasicTypes.STRING)
				.addScalar("cdHomeStudyType", StandardBasicTypes.STRING)
				.addScalar("cdTransmittalPurpose", StandardBasicTypes.STRING)
				.addScalar("txtTransmittalPurpose", StandardBasicTypes.STRING)
				.addScalar("dtHomeStudyDue", StandardBasicTypes.DATE)
				.addScalar("cdUrgentReqReason", StandardBasicTypes.STRING)
				.addScalar("txtUrgentReqReason", StandardBasicTypes.STRING)
				.addScalar("indConcurrence", StandardBasicTypes.STRING)
				.addScalar("cdTransmittalStatus", StandardBasicTypes.STRING)
				.addScalar("txtTransmittalStatus", StandardBasicTypes.STRING)
				.addScalar("txtStatusDesc", StandardBasicTypes.STRING)
				.addScalar("cdOtherMode", StandardBasicTypes.STRING)
				.addScalar("txtOtherModeReason", StandardBasicTypes.STRING)
				.addScalar("indNeiceStateParticipant", StandardBasicTypes.STRING)
				.addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("indNotOldestChild", StandardBasicTypes.STRING)
				.addScalar("idOldestChildEvent", StandardBasicTypes.STRING)
				.setParameter("idInput", idInput)
				.setResultTransformer(Transformers.aliasToBean(ICPCTransmittalDto.class));
		icpcTransmittalDto = (ICPCTransmittalDto) query.uniqueResult();
		if (ObjectUtils.isEmpty(icpcTransmittalDto)) {
			icpcTransmittalDto = new ICPCTransmittalDto();
		}
		if (!ObjectUtils.isEmpty(icpcTransmittalDto) && ObjectUtils.isEmpty(icpcTransmittalDto.getDtSent())) {
			icpcTransmittalDto.setDtSent(new Date());
		}

		List<ICPCPlacementRequestDto> icpcPlacementRequestDtoList = sessionFactory.getCurrentSession()
				.createSQLQuery(getLinkedNeiceChildrenSql)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("nmPlacementResource", StandardBasicTypes.STRING) // nmPlacementResource is used as placeholder for NM_PERSON_FULL
				.setParameter("idIcpcRequest", idICPCRequest)
				.setResultTransformer(Transformers.aliasToBean(ICPCPlacementRequestDto.class))
				.list();

		if (!CollectionUtils.isEmpty(icpcPlacementRequestDtoList)) {
			icpcTransmittalDto.setLinkedChildMap(new TreeMap<>());
			for (ICPCPlacementRequestDto icpcPlacementRequestDto : icpcPlacementRequestDtoList) {
				icpcTransmittalDto.getLinkedChildMap()
						.put(String.valueOf(icpcPlacementRequestDto.getIdEvent()),
								icpcPlacementRequestDto.getNmPlacementResource());
			}
		}

		return icpcTransmittalDto;
	}

	/**
	 * Method Name: getPlacementResourceName Method Description: Gets the
	 * resource name for the passed ICPC Request ID
	 * 
	 * @param idICPCRequest
	 * @return String
	 */
	@Override
	public String getPlacementResourceName(Long idICPCRequest) {

		List<String> resourceNameList = new ArrayList<>();
		String nmPlacementResource = ServiceConstants.EMPTY_STRING;
		// Fix for defect 12839 - INC000004983012 - CPS - Specific Stage - 8888 error
		// code displayed when user tries to add a transmittal in 100A Placement Request
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getPlacementResourceName)
				.addScalar("nmResource", StandardBasicTypes.STRING).setParameter("idICPCRequest", idICPCRequest);
		
		resourceNameList = (List<String>) query.list();
		
		for (String nmResource : resourceNameList) {
			if (!StringUtils.isEmpty(nmResource)) {
				nmPlacementResource = nmResource;
			}
		}
		return nmPlacementResource;
	}

	/**
	 * Method Name: getTransmittalDetails Method Description: Get the
	 * Transmittal Details for the passed Transmittal ID
	 * 
	 * @param idTransmittal
	 * @return Map<String, String>
	 */
	public Map<String, String> getTransmittalDetails(Long idTransmittal) {

		Map<String, String> transmittalDtls = new HashMap<>();
		IcpcTransmittal icpcTransmittalEntity = (IcpcTransmittal) sessionFactory.getCurrentSession()
				.load(IcpcTransmittal.class, idTransmittal);
		List<IcpcTransmittalDetail> icpcTransmittalList = (List<IcpcTransmittalDetail>) sessionFactory
				.getCurrentSession().createCriteria(IcpcTransmittalDetail.class)
				.add(Restrictions.eq("icpcTransmittal", icpcTransmittalEntity)).list();
		transmittalDtls = icpcTransmittalList.stream().collect(
				Collectors.toMap(x -> x.getCdTransmittalIndicatorType(), x -> x.getIndTransmittal().toString()));
		return transmittalDtls;
	}

	/**
	 * Method Name: getSiblings Method Description: Get the list of Siblings for
	 * the passed ICPC Request ID
	 * 
	 * @param idICPCRequest
	 * @return List<ICPCPersonDto>
	 */
	public List<ICPCPersonDto> getSiblings(Long idICPCRequest) {

		List<ICPCPersonDto> siblings = new ArrayList<>();
		siblings = (List<ICPCPersonDto>) sessionFactory.getCurrentSession().createSQLQuery(getTransmittalSiblings)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmFull", StandardBasicTypes.STRING)
				.addScalar("cdPersonType", StandardBasicTypes.STRING).setParameter("idICPCRequest", idICPCRequest)
				.setResultTransformer(Transformers.aliasToBean(ICPCPersonDto.class)).list();
		return siblings;
	}

	/**
	 * Method Name: getSelectedChildren Method Description: <To be Added>
	 * 
	 * @param idTransmittal
	 * @return Map<Long, Long>
	 */
	public Map<Long, Long> getSelectedChildren(Long idTransmittal) {

		List<Long> selectedChildren = new ArrayList<>();
		Map<Long, Long> selectedChildrenMap = new HashMap<>();
		IcpcTransmittal icpcTransmittalEntity = (IcpcTransmittal) sessionFactory.getCurrentSession()
				.load(IcpcTransmittal.class, idTransmittal);
		selectedChildren = (List<Long>) sessionFactory.getCurrentSession().createCriteria(IcpcTransmittalChild.class)
				.setProjection(Projections.property("person.idPerson"))
				.add(Restrictions.eq("icpcTransmittal", icpcTransmittalEntity)).list();
		if (!CollectionUtils.isEmpty(selectedChildren)) {
			selectedChildrenMap = selectedChildren.stream().collect(Collectors.toMap(x -> x, x -> x));
		}
		return selectedChildrenMap;
	}

	/**
	 * Method Name: getPlacementStatusInfo Method Description: get ICPC status
	 * report detail information for display Method Description:
	 * 
	 * @param idEvent
	 * 
	 * @return
	 */
	@Override
	public ICPCPlacementStatusDto getPlacementStatusInfo(Long idEvent) {

		// retrieve placement status details
		ICPCPlacementStatusDto iCPCPlacementStatusDto = (ICPCPlacementStatusDto) sessionFactory.getCurrentSession()
				.createSQLQuery(getPlacementStatusInfoSql)
				.addScalar("idICPCPlacementStatus", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("idICPCPlacementRequest", StandardBasicTypes.LONG)
				.addScalar("idICPCSubmission", StandardBasicTypes.LONG)
				.addScalar("cdReceivingState")
				.addScalar("cdSendingState")
				.addScalar("cdPlacementStatus")
				.addScalar("dtPlacement", StandardBasicTypes.DATE)
				.addScalar("dtPlacementWithdrawn", StandardBasicTypes.DATE)
				.addScalar("cdCompactTermRsn")
				.addScalar("txtReasonOther")
				.addScalar("dtPlacementTerm")
				.addScalar("txtNotes")
				.addScalar("txtRelationGuardian")
				.addScalar("dtCreated", StandardBasicTypes.DATE)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ICPCPlacementStatusDto.class))
				.uniqueResult();
		// retrieve Placement status person
		List<ICPCPersonDto> icpcPersonList = (List<ICPCPersonDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getPlacementStatusPersonSql).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmFull").addScalar("cdSex").addScalar("cdPersonType")
				.addScalar("idICPCPersonRelation", StandardBasicTypes.LONG)
				.addScalar("idICPCRequestPersonLink", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG).addScalar("dateOfBirth", StandardBasicTypes.DATE)
				.addScalar("nbrSsn")
				.addScalar("neicePersonId", StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ICPCPersonDto.class)).list();
		List<ICPCPersonDto> icpcPersonLst = new ArrayList<>();
		icpcPersonList.stream().forEach(person -> {

			switch (person.getCdPersonType()) {
				case CodesConstant.ICPCPRTP_10:
					iCPCPlacementStatusDto.setFather(person);
					break;
				case CodesConstant.ICPCPRTP_20:
					iCPCPlacementStatusDto.setGuardian(person);
					break;
				case CodesConstant.ICPCPRTP_30:
					iCPCPlacementStatusDto.setMother(person);
					break;
				case CodesConstant.ICPCPRTP_60:
					icpcPersonLst.add(person);
					break;
				case CodesConstant.ICPCPRTP_80:
					iCPCPlacementStatusDto.setChild(person);
					break;
			}

		});
		iCPCPlacementStatusDto.setPlcmntPersonLst(icpcPersonLst);

		// Get the Placement Resource information
		ICPCResourceDto iCPCResourceDto = (ICPCResourceDto) sessionFactory.getCurrentSession()
				.createSQLQuery(getPlacementResourceInfoSql).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("nmResource").addScalar("idICPCRequestResourceLink", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("idResourceNeice", StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ICPCResourceDto.class)).list().stream().findFirst()
				.orElse(null);
		if (!ObjectUtils.isEmpty(iCPCResourceDto))
			iCPCPlacementStatusDto.setPlcmntResource(iCPCResourceDto);
		// get Child Information

		if (ObjectUtils.isEmpty(iCPCPlacementStatusDto.getChild()) || ObjectUtils.isEmpty(iCPCPlacementStatusDto.getChild().getIdPerson())) {

			ICPCPersonDto child = (ICPCPersonDto) sessionFactory.getCurrentSession()
					.createSQLQuery(getPrimayChildInfoSql)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("nmFull")
					.addScalar("cdSex")
					.addScalar("dateOfBirth", StandardBasicTypes.DATE)
					.addScalar("nbrSsn")
					.setParameter("idEvent", idEvent)
					.setResultTransformer(Transformers.aliasToBean(ICPCPersonDto.class))
					.list()
					.stream()
					.findFirst()
					.orElse(null);
			if (!ObjectUtils.isEmpty(child)) {

				iCPCPlacementStatusDto.setChild(child);
			}
		}
		if (!CollectionUtils.isEmpty(iCPCPlacementStatusDto.getPlcmntPersonLst())) {
			Long idSttsPersAddrLinkNew = getStatusPersonAddressLink(iCPCPlacementStatusDto.getIdICPCPlacementStatus(),
					CodesConstant.ICPCADTP_10);
			Long idSttsPersAddrLinkOld = getStatusPersonAddressLink(iCPCPlacementStatusDto.getIdICPCPlacementStatus(),
					CodesConstant.ICPCADTP_20);

			iCPCPlacementStatusDto
					.setNewAddress(getPlacementAddress(iCPCPlacementStatusDto.getPlcmntPersonLst().get(0).getIdPerson(),
							idSttsPersAddrLinkNew, PLACEMENT_PERSON_TYPE, CodesConstant.ICPCADTP_10));
			iCPCPlacementStatusDto
					.setOldAddress(getPlacementAddress(iCPCPlacementStatusDto.getPlcmntPersonLst().get(0).getIdPerson(),
							idSttsPersAddrLinkOld, PLACEMENT_PERSON_TYPE, CodesConstant.ICPCADTP_20));
		} else if (iCPCPlacementStatusDto.getPlcmntResource() != null) {
			Long idSttsRsrcAddrLinkNew = getStatusResourceAddressLink(iCPCPlacementStatusDto.getIdICPCPlacementStatus(),
					CodesConstant.ICPCADTP_10);
			Long idSttsRsrcAddrLinkOld = getStatusResourceAddressLink(iCPCPlacementStatusDto.getIdICPCPlacementStatus(),
					CodesConstant.ICPCADTP_20);
			iCPCPlacementStatusDto
					.setNewAddress(getPlacementAddress(iCPCPlacementStatusDto.getPlcmntResource().getIdResource(),
							idSttsRsrcAddrLinkNew, PLACEMENT_RESOURCE_TYPE, CodesConstant.ICPCADTP_10));
			iCPCPlacementStatusDto
					.setOldAddress(getPlacementAddress(iCPCPlacementStatusDto.getPlcmntResource().getIdResource(),
							idSttsRsrcAddrLinkOld, PLACEMENT_RESOURCE_TYPE, CodesConstant.ICPCADTP_20));
		}

		// Request Enclosure
		List<String> cdEnclosure = getrequestEnclosure(idEvent);
		if (!ObjectUtils.isEmpty(cdEnclosure))
			iCPCPlacementStatusDto.setCdEnclosure(cdEnclosure);
		// get transmittal info
		iCPCPlacementStatusDto.setTransmittalList(getTransmittalListInfo(iCPCPlacementStatusDto.getIdICPCRequest()));
		// get document info
		iCPCPlacementStatusDto.setDocumentList(getDocumentListInfo(iCPCPlacementStatusDto.getIdICPCRequest()));

		return iCPCPlacementStatusDto;
	}

	/**
	 * 
	 * Method Name: getPersonRelation this method to get FTPersonRelationDto for
	 * guardian Method Description:
	 * 
	 * @param idPerson
	 * @param idPerson2
	 * @return
	 */
	@Override
	public FTPersonRelationDto getPersonRelation(Long idPerson, Long idPerson2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Method Name: saveTransmittal Method Description: This method saves the
	 * transmittal detail page details.
	 * 
	 * @param transmittalValueBean
	 * @return Long
	 */
	@Override
	public Long saveTransmittal(ICPCTransmittalDto icpcTransmittalDto) {
		Long idTransmittal = icpcTransmittalDto.getIdICPCTransmittal();
		IcpcTransmittal icpcTransmittal;
		// for new record
		// Create entry in to Placement Request table. insert transmittal
		// details
		if (idTransmittal == 0l) {
			icpcTransmittal = new IcpcTransmittal();
			populateTransmittalDtl(icpcTransmittalDto, icpcTransmittal);
			if(!ObjectUtils.isEmpty(icpcTransmittal.getNmAttn()) && !(icpcTransmittal.getNmAttn().length() > 60)){
			idTransmittal = (Long) sessionFactory.getCurrentSession().save(icpcTransmittal);
			}
			if(ObjectUtils.isEmpty(icpcTransmittal.getNmAttn())){
				idTransmittal = (Long) sessionFactory.getCurrentSession().save(icpcTransmittal);
			}
		} else { // update record
			icpcTransmittal = (IcpcTransmittal) sessionFactory.getCurrentSession().get(IcpcTransmittal.class,
					icpcTransmittalDto.getIdICPCTransmittal());
			populateTransmittalDtl(icpcTransmittalDto, icpcTransmittal);
			if(!ObjectUtils.isEmpty(icpcTransmittal.getNmAttn()) && icpcTransmittal.getNmAttn().length() > 60){
				icpcTransmittal.setNmAttn("");
			}
			sessionFactory.getCurrentSession().update(icpcTransmittal);
		}

		return idTransmittal;
	}

	/**
	 * Method Name: populateTransmittalDtl Method Description: This method is
	 * used to populate transmittal details.
	 * 
	 * @param transmittalValueBean
	 * @param icpcTransmittal
	 */
	private void populateTransmittalDtl(ICPCTransmittalDto icpcTransmittalDto, IcpcTransmittal icpcTransmittal) {
		if (!CollectionUtils.isEmpty(icpcTransmittalDto.getTransmittalDetails())) {
			icpcTransmittal.getIcpcTransmittalDetails().clear();
			populateTransmittalDetail(icpcTransmittalDto, icpcTransmittal);
		}
		icpcTransmittal.getIcpcTransmittalChilds().clear();
		if (!CollectionUtils.isEmpty(icpcTransmittalDto.getSelectedSiblings())) {
			populateTransmittalChild(icpcTransmittalDto, icpcTransmittal);
		}
		populateTransmittal(icpcTransmittalDto, icpcTransmittal);
	}

	/**
	 * Method Name: populateTransmittal Method Description: This method is used
	 * to set the values in ICPC_TRANSMITTAL table from ICPCTransmittalDto.
	 * 
	 * @param icpcTransmittalDto
	 * @param icpcTransmittal
	 */
	private void populateTransmittal(ICPCTransmittalDto icpcTransmittalDto, IcpcTransmittal icpcTransmittal) {
		IcpcRequest icpcRequest = new IcpcRequest();
		icpcTransmittal.setCdAddtnlInfo(icpcTransmittalDto.getCdAdditionalInfo());
		icpcTransmittal.setCdTransmittalType(icpcTransmittalDto.getCdTransmittalType());
		icpcTransmittal.setDtSent(icpcTransmittalDto.getDtSent());
		if (!ObjectUtils.isEmpty(icpcTransmittalDto.getIdICPCRequest()) && icpcTransmittalDto.getIdICPCRequest() != 0l) {
			icpcRequest = (IcpcRequest) sessionFactory.getCurrentSession().get(IcpcRequest.class,
					icpcTransmittalDto.getIdICPCRequest());
			icpcTransmittal.setIcpcRequest(icpcRequest);
		} else {
			icpcTransmittal.setIcpcRequest(icpcRequest);
		}
		if (ObjectUtils.isEmpty(icpcTransmittal.getIdIcpcTransmittal())) {
			icpcTransmittal.setIdCreatedPerson(icpcTransmittalDto.getIdCreatedPerson());
			icpcTransmittal.setDtCreated(new Date());
		}
		icpcTransmittal.setDtLastUpdate(new Date());
		icpcTransmittal.setIdLastUpdatePerson(icpcTransmittalDto.getIdLastUpdatePerson());
		if (Arrays.asList(ServiceConstants.ICPCTMST_10, ServiceConstants.ICPCTMST_70)
				.contains(icpcTransmittal.getCdTransmittalStatus())) {
			icpcTransmittal.setCdTransmittalStatus(ServiceConstants.ICPCTMST_70);
		} else if (Arrays.asList(ServiceConstants.ICPCTMST_40, ServiceConstants.ICPCTMST_80)
				.contains(icpcTransmittal.getCdTransmittalStatus())) {
			icpcTransmittal.setCdTransmittalStatus(ServiceConstants.ICPCTMST_80);
		} else {
			icpcTransmittal.setCdTransmittalStatus(ServiceConstants.ICPCTMST_30);
		}
		icpcTransmittal.setNmAttn(icpcTransmittalDto.getNmAttn());
		icpcTransmittal.setTxtComment(icpcTransmittalDto.getTxtComment());
		icpcTransmittal.setTxtOther(icpcTransmittalDto.getTxtOther());
		icpcTransmittal.setCdHomeStudyType(icpcTransmittalDto.getCdHomeStudyType());
		icpcTransmittal.setCdTransmittalPurpose(icpcTransmittalDto.getCdTransmittalPurpose());
		icpcTransmittal.setTxtTransmittalPurpose(icpcTransmittalDto.getTxtTransmittalPurpose());
		icpcTransmittal.setDtHomeStudyDue(icpcTransmittalDto.getDtHomeStudyDue());
		icpcTransmittal.setCdUrgentReqReason(icpcTransmittalDto.getCdUrgentReqReason());
		icpcTransmittal.setTxtUrgentReqReason(icpcTransmittalDto.getTxtUrgentReqReason());
		icpcTransmittal.setIndConcurrence(icpcTransmittalDto.getIndConcurrence());
		icpcTransmittal.setIndNotOldestChild(icpcTransmittalDto.getIndNotOldestChild());
		icpcTransmittal.setIdOldestChildEvent(!ObjectUtils.isEmpty(icpcTransmittalDto.getIdOldestChildEvent())
				? Long.parseLong(icpcTransmittalDto.getIdOldestChildEvent())
				: null);
	}

	/**
	 * Method Name: populateTransmittalChild Method Description: This method is
	 * used to set the values in ICPC_TRANSMITTAL_CHILD table from
	 * ICPCTransmittalDto.
	 * 
	 * @param icpcTransmittalDto
	 * @param icpcTransmittal
	 */
	private void populateTransmittalChild(ICPCTransmittalDto icpcTransmittalDto, IcpcTransmittal icpcTransmittal) {
		Map<Long, Long> map = icpcTransmittalDto.getSelectedSiblings();
		map.entrySet().stream().forEach(e -> {
			IcpcTransmittalChild transmittalChild = new IcpcTransmittalChild();
			transmittalChild.setIdLastUpdatePerson(icpcTransmittalDto.getIdLastUpdatePerson());
			transmittalChild.setIdCreatedPerson(icpcTransmittalDto.getIdLastUpdatePerson());
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, Long.valueOf(e.getKey()));
			transmittalChild.setPerson(person);
			transmittalChild.setIcpcTransmittal(icpcTransmittal);
			icpcTransmittal.getIcpcTransmittalChilds().add(transmittalChild);
		});
	}

	/**
	 * Method Name: populateTransmittalDetail Method Description:This method is
	 * used to set the values in ICPC_TRANSMITTAL_DETAIL table from
	 * ICPCTransmittalDto.
	 * 
	 * @param icpcTransmittalDto
	 * @param icpcTransmittal
	 */
	private void populateTransmittalDetail(ICPCTransmittalDto icpcTransmittalDto, IcpcTransmittal icpcTransmittal) {
		Map<String, String> map = icpcTransmittalDto.getTransmittalDetails();
		map.entrySet().stream().forEach(e -> {
			IcpcTransmittalDetail transmittalDetail = new IcpcTransmittalDetail();
			transmittalDetail.setIdLastUpdatePerson(icpcTransmittalDto.getIdLastUpdatePerson());
			transmittalDetail.setIdCreatedPerson(icpcTransmittalDto.getIdLastUpdatePerson());
			transmittalDetail.setCdTransmittalIndicatorType(e.getKey());
			transmittalDetail.setIndTransmittal(e.getValue().toCharArray()[0]);
			transmittalDetail.setDtCreated(new Date());
			transmittalDetail.setDtLastUpdate(new Date());
			transmittalDetail.setIcpcTransmittal(icpcTransmittal);
			icpcTransmittal.getIcpcTransmittalDetails().add(transmittalDetail);
		});
	}

	/**
	 * Method Name: getAllAgencyICPCVector Method Description: get all agency
	 * information
	 * 
	 * @return List<ICPCDocumentDto>
	 */
	@Override
	public List<ICPCDocumentDto> getDocumentListInfo(Long idICPCRequest) {
		List<ICPCDocumentDto> documentList = (List<ICPCDocumentDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getDocumentListInfo).addScalar("idICPCDocument", StandardBasicTypes.LONG)
				.addScalar("idLastupdatePerson", StandardBasicTypes.LONG)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("idICPCSubmission", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdType", StandardBasicTypes.STRING).addScalar("dtUpload", StandardBasicTypes.DATE)
				.addScalar("fileName", StandardBasicTypes.STRING).addScalar("sysMimeType", StandardBasicTypes.STRING)
				.addScalar("txtDetails", StandardBasicTypes.STRING)
				.addScalar("txtKeyDocStore", StandardBasicTypes.STRING).setParameter("idICPCRequest", idICPCRequest)
				.setResultTransformer(Transformers.aliasToBean(ICPCDocumentDto.class)).list();

		return documentList;
	}

	/**
	 * Method Name: getAllAgencyICPCVector Method Description: get all agency
	 * information
	 * 
	 * @param idEvent
	 * @param idStage
	 * @return ICPCPlacementRequestDto
	 */
	@Override
	public ICPCPlacementRequestDto getICPCPlacementRequestInfo(Long idEvent, Long idStage) {
		ICPCPlacementRequestDto icpcPlacementRequestDto = getPlacementRequestDtl(idEvent);
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIdICPCRequest())) {
			// get the portal resource if it is a portal request
			icpcPlacementRequestDto
					.setPortalResourceDetail(getPortalResourceDetail(icpcPlacementRequestDto.getIdICPCRequest()));
		}
		// get the person list for that ICPC request
		List<ICPCPersonDto> icpcRequestPersonList = getPlacementRequestPersonList(idEvent);
		List<ICPCPersonDto> icpcPersonList = new ArrayList<>();
		// set person depends on the person code to the response
		if (!CollectionUtils.isEmpty(icpcRequestPersonList)) {
			icpcRequestPersonList.stream().forEach(person -> {
				switch (person.getCdPersonType()) {
					case CodesConstant.ICPCPRTP_10:
						icpcPlacementRequestDto.setFather(person);
						break;
					case CodesConstant.ICPCPRTP_30:
						icpcPlacementRequestDto.setMother(person);
						break;
					case CodesConstant.ICPCPRTP_60:
						person.setNbrPhone(getPersonPhone(person.getIdPerson()));
						icpcPersonList.add(person);
						break;
					case CodesConstant.ICPCPRTP_40:
						// Call getIdPersonAddress to get person address
						AddressValueDto addressValueDto = personSearchDao.getIdPersonAddress(person.getIdPerson());
						if (!ObjectUtils.isEmpty(addressValueDto)) {
							person.setAddrLine1(addressValueDto.getStreetLn1());
							person.setAddrLine2(addressValueDto.getStreetLn2());
							person.setAddrCity(addressValueDto.getCity());
							person.setAddrState(addressValueDto.getState());
							person.setAddrZip(addressValueDto.getZip());
							person.setNbrPhone(getPersonPhone(person.getIdPerson()));
							icpcPlacementRequestDto.setPersonFinResponsible(person);
						}
						break;
					case CodesConstant.ICPCPRTP_50:
						// Call getIdPersonAddress to get person address
						AddressValueDto addressValue = personSearchDao.getIdPersonAddress(person.getIdPerson());
						if (!ObjectUtils.isEmpty(addressValue)) {
							person.setAddrLine1(addressValue.getStreetLn1());
							person.setAddrLine2(addressValue.getStreetLn2());
							person.setAddrCity(addressValue.getCity());
							person.setAddrState(addressValue.getState());
							person.setAddrZip(addressValue.getZip());
							person.setNbrPhone(getPersonPhone(person.getIdPerson()));
						}
						icpcPlacementRequestDto.setPersonResponsible(person);
						break;

					case CodesConstant.ICPCPRTP_80:
						icpcPlacementRequestDto.setChild(person);
						break;
				}

			});
			icpcPersonList.sort(Comparator.comparing(ICPCPersonDto::getIdICPCRequestPersonLink));
			icpcPlacementRequestDto.setPlcmntPerson(
					!CollectionUtils.isEmpty(icpcPersonList) ? icpcPersonList.get(0) : null);
			icpcPlacementRequestDto.setPlcmntPersonLst(icpcPersonList);
		}
		// Get the Placement Resource information
		icpcPlacementRequestDto.setPlcmntResource(getPlacementRequestResource(idEvent));

		// Get the Placement Child information
		if ((ObjectUtils.isEmpty(icpcPlacementRequestDto.getChild()) || ObjectUtils.isEmpty(
				icpcPlacementRequestDto.getChild()
						.getIdPerson())) && !ObjectUtils.isEmpty(idStage)) {
			icpcPlacementRequestDto.setChild(getRequestChild(idStage));
		}
		if (!CollectionUtils.isEmpty(icpcPlacementRequestDto.getPlcmntPersonLst())) {
			for (ICPCPersonDto icpcPersonDto : icpcPlacementRequestDto.getPlcmntPersonLst()) {
				if (icpcPersonDto.getIdPerson() > 0) {
					icpcPlacementRequestDto
							.setNewAddress(getPlacementAddress(icpcPersonDto.getIdPerson(), 0L,
									PLACEMENT_PERSON_TYPE, CodesConstant.ICPCADTP_10));
					icpcPlacementRequestDto
							.setOldAddress(getPlacementAddress(icpcPersonDto.getIdPerson(), 0L,
									PLACEMENT_PERSON_TYPE, CodesConstant.ICPCADTP_20));
				}
			}
		} else if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPlcmntResource())) {
			icpcPlacementRequestDto
					.setNewAddress(getPlacementAddress(icpcPlacementRequestDto.getPlcmntResource()
									.getIdResource(), 0L,
							PLACEMENT_RESOURCE_TYPE, CodesConstant.ICPCADTP_10));
			icpcPlacementRequestDto
					.setOldAddress(getPlacementAddress(icpcPlacementRequestDto.getPlcmntResource()
									.getIdResource(), 0L,
							PLACEMENT_RESOURCE_TYPE, CodesConstant.ICPCADTP_20));

		}
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto)
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getIdICPCRequest())) {
			// get transmittal info
			icpcPlacementRequestDto
					.setTransmittalList(getTransmittalListInfo(icpcPlacementRequestDto.getIdICPCRequest()));
			// get document info
			icpcPlacementRequestDto.setDocumentList(getDocumentListInfo(icpcPlacementRequestDto.getIdICPCRequest()));
		}
		// get the List of Enclosed Documents for the placement request
		icpcPlacementRequestDto.setCdEnclosure(getEnclosedDocuments(idEvent));

		// Agency information
		icpcPlacementRequestDto.setAgencyICPCDtoList(getICPCAgencyDetailsList(idEvent));

		return icpcPlacementRequestDto;
	}

	/**
	 * Method Name: getPlacementRequestDel Method Description:
	 * 
	 * @param idEvent
	 * @return
	 */
	private ICPCPlacementRequestDto getPlacementRequestDtl(Long idEvent) {

		ICPCPlacementRequestDto icpcPlacementRequestDto = (ICPCPlacementRequestDto) sessionFactory.getCurrentSession()
				.createSQLQuery(getPlacementRequestInfo).addScalar("idICPCRequestPrtl", StandardBasicTypes.LONG)
				.addScalar("idICPCPlacementRequest", StandardBasicTypes.LONG)
				.addScalar("idICPCsubmission", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("dtReceived", StandardBasicTypes.DATE)
				.addScalar("indPriority", StandardBasicTypes.STRING)
				.addScalar("indICWAEligible", StandardBasicTypes.STRING)
				.addScalar("indTitleIVE", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatus", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatus", StandardBasicTypes.STRING)
				.addScalar("cdCareType", StandardBasicTypes.STRING)
				.addScalar("txtCareType", StandardBasicTypes.STRING)
				.addScalar("pubPrivBtn", StandardBasicTypes.STRING)
				.addScalar("cdSubsidy", StandardBasicTypes.STRING)
				.addScalar("cdInitialReport", StandardBasicTypes.STRING)
				.addScalar("cdSprrvsrySrvcs", StandardBasicTypes.STRING)
				.addScalar("txtSupSrvs", StandardBasicTypes.STRING)
				.addScalar("cdSprvsryRprts", StandardBasicTypes.STRING)
				.addScalar("txtSupRprts", StandardBasicTypes.STRING)
				.addScalar("homeStudyDt", StandardBasicTypes.DATE)
				.addScalar("cdDenialReason", StandardBasicTypes.STRING)
				.addScalar("txtDenialReason", StandardBasicTypes.STRING)
				.addScalar("txtNotes", StandardBasicTypes.STRING)
				.addScalar("dtHomeAssessSent", StandardBasicTypes.DATE)
				.addScalar("dtHomeAssessRcvd", StandardBasicTypes.DATE)
				.addScalar("cdDecision", StandardBasicTypes.STRING)
				.addScalar("dtDecision", StandardBasicTypes.DATE)
				.addScalar("txtPlacementRemarks", StandardBasicTypes.STRING)
				.addScalar("cdViolation", StandardBasicTypes.STRING)
				.addScalar("cdWithdrawalReason", StandardBasicTypes.STRING)
				.addScalar("indNaturalDisaster", StandardBasicTypes.STRING)
				.addScalar("txtNaturalDisaster", StandardBasicTypes.STRING)
				.addScalar("cdReceivingState", StandardBasicTypes.STRING)
				.addScalar("cdSendingState", StandardBasicTypes.STRING)
				.addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("cdReceivingAgency", StandardBasicTypes.LONG)
				.addScalar("cdSendingAgency", StandardBasicTypes.LONG)
				.addScalar("neiceCaseId", StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ICPCPlacementRequestDto.class)).uniqueResult();
		if (ObjectUtils.isEmpty(icpcPlacementRequestDto)) {
			icpcPlacementRequestDto = new ICPCPlacementRequestDto();
		}
		return icpcPlacementRequestDto;
	}

	/**
	 * Method Description: Fetch the Portal Resource Detail
	 * 
	 * @param idICPCPlacement
	 * @return String
	 */
	public String getPortalResourceDetail(Long idICPCRequest) {

		String portalResourceDetail = (String) sessionFactory.getCurrentSession().createSQLQuery(getPortalResource)
				.addScalar("resourceDetail", StandardBasicTypes.STRING).setParameter("idICPCRequest", idICPCRequest)
				.uniqueResult();
		return portalResourceDetail;
	}

	/**
	 * Method Description: Fetch the ICPC Request Person Details
	 * 
	 * @param idEvent
	 * @return List<IcpcPersonDto>
	 */
	public List<ICPCPersonDto> getPlacementRequestPersonList(Long idEvent) {
		//PID: 90866 : SqlDateTime Overflow - adding date of birth column to add a validation as part of ICPC transmittal
		List<ICPCPersonDto> icpcPersonDtoList = (List<ICPCPersonDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getPlacementRequestPerson).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmFull", StandardBasicTypes.STRING).addScalar("cdSex", StandardBasicTypes.STRING)
				.addScalar("cdPersonType", StandardBasicTypes.STRING).addScalar("nbrSsn", StandardBasicTypes.STRING)
				.addScalar("idICPCRequestPersonLink", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("neiceCaseId", StandardBasicTypes.STRING)
				.addScalar("neicePersonId", StandardBasicTypes.STRING)
				.addScalar("dateOfBirth", StandardBasicTypes.DATE).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ICPCPersonDto.class)).list();
		return icpcPersonDtoList;
	}

	/**
	 * Method Description: the phone number giving the person id
	 * 
	 * @param String
	 * @return idPerson
	 */
	public String getPersonPhone(Long idPerson) {
		return (String) sessionFactory.getCurrentSession().createSQLQuery(getPersonPhone)
				.addScalar("personPhone", StandardBasicTypes.STRING).setParameter("idPerson", idPerson).uniqueResult();

	}

	/**
	 * Method Description: Fetch the ICPC Request Resource Details
	 * 
	 * @param idEvent
	 * @return ICPCResourceDto
	 */
	public ICPCResourceDto getPlacementRequestResource(Long idEvent) {

		ICPCResourceDto icpcResourceDto = (ICPCResourceDto) sessionFactory.getCurrentSession()
				.createSQLQuery(getPlacementResourceInfoSql)
				.addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("nmResource", StandardBasicTypes.STRING)
				.addScalar("idICPCRequestResourceLink", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("idResourceNeice", StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				//.setResultTransformer(Transformers.aliasToBean(ICPCResourceDto.class));
				.setResultTransformer(Transformers.aliasToBean(ICPCResourceDto.class))
				.list()
				.stream()
				.findFirst()
				.orElse(null);

		if (ObjectUtils.isEmpty(icpcResourceDto)) {
			return new ICPCResourceDto();
		}
		return icpcResourceDto;
	}

	/**
	 * Method Description: Fetch the Request Child Details
	 * 
	 * @param idStage
	 * @return ICPCPersonDto
	 */
	public ICPCPersonDto getRequestChild(Long idStage) {

		ICPCPersonDto icpcChildDto = (ICPCPersonDto) sessionFactory.getCurrentSession().createSQLQuery(getChildRequest)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmFull", StandardBasicTypes.STRING)
				.addScalar("cdSex", StandardBasicTypes.STRING).addScalar("nbrSsn", StandardBasicTypes.STRING)
				.addScalar("dateOfBirth", StandardBasicTypes.DATE).addScalar("cdPersonType", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage).setResultTransformer(Transformers.aliasToBean(ICPCPersonDto.class))
				.list().stream().findFirst().orElse(null);

		return icpcChildDto;
	}

	/**
	 * Method Description: Fetch Placement address
	 * 
	 * @param idStage
	 * @return ICPCPlacementAddressDto
	 */
	public ICPCPlacementAddressDto getPlacementAddress(Long idPlcmntOwner, Long idAddr, String plcmntType,
			String addrType) {
		String sql = ServiceConstants.EMPTY_STRING;
		switch (plcmntType) {
		case ServiceConstants.PLACEMENT_PERSON_TYPE:
			if (!ObjectUtils.isEmpty(idAddr) && idAddr > 0) // both Old and New
															// Address
			{
				sql = personLinkAddress;
			} else if (CodesConstant.ICPCADTP_10.equals(addrType)) {
				sql = personNewAddress;
			} else {
				sql = personOldAddress;
			}
			break;
		case ServiceConstants.PLACEMENT_RESOURCE_TYPE:
			if (!ObjectUtils.isEmpty(idAddr) && idAddr > 0) // both Old and New
															// Address
			{
				sql = resourceLinkAddress;
			} else if (CodesConstant.ICPCADTP_10.equals(addrType)) {
				sql = resourceNewAddress;
			} else {
				sql = resourceOldAddress;
			}
			break;
		}
		Long param;
		if (ObjectUtils.isEmpty(idAddr) || 0 == idAddr) {
			param = idPlcmntOwner;
		} else {
			param = idAddr;
		}
		ICPCPlacementAddressDto icpcPlacementAddressDto = null;
		if(!ObjectUtils.isEmpty(param)) {
			icpcPlacementAddressDto = (ICPCPlacementAddressDto) sessionFactory.getCurrentSession()
					.createSQLQuery(sql).addScalar("idPrsnAddress", StandardBasicTypes.LONG)
					.addScalar("addrStLn1", StandardBasicTypes.STRING).addScalar("addrStLn2", StandardBasicTypes.STRING)
					.addScalar("addrCity", StandardBasicTypes.STRING).addScalar("cdAddrState", StandardBasicTypes.STRING)
					.addScalar("addrZip", StandardBasicTypes.STRING).addScalar("cdCounty", StandardBasicTypes.STRING)
					.addScalar("idICPCStatPersAddrLink", StandardBasicTypes.LONG).setParameter("idAddr", param)
					.setResultTransformer(Transformers.aliasToBean(ICPCPlacementAddressDto.class)).list().stream()
					.findFirst().orElse(null);
		}else{
			icpcPlacementAddressDto = new ICPCPlacementAddressDto();
		}

		return icpcPlacementAddressDto;
	}

	/**
	 * Method Description: Fetch the List of Enclosed Documents for the
	 * placement request
	 * 
	 * @param idEvent
	 * @return List<String>
	 */
	public List<String> getEnclosedDocuments(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IcpcRequestEnclosure.class,
				ServiceConstants.IRE);
		criteria.createAlias("IRE.event", ServiceConstants.EVT);
		criteria.add(Restrictions.eq("EVT.idEvent", idEvent));
		criteria.setProjection(Projections.property("IRE.cdEnclosure"));
		return criteria.list();
	}

	/**
	 * Method Description: Fetch the Agency Details List
	 * 
	 * @param idEvent
	 * @return List<ICPCAgencyDto>
	 */
	public List<ICPCAgencyDto> getICPCAgencyDetailsList(Long idEvent) {
		List<ICPCAgencyDto> agencyDetailsList = (List<ICPCAgencyDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(agencyDetails).addScalar("idEntity", StandardBasicTypes.LONG)
				.addScalar("agencyCounty", StandardBasicTypes.STRING)
				.addScalar("cdAgencyType", StandardBasicTypes.STRING).addScalar("addrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrStLn2", StandardBasicTypes.STRING).addScalar("addrCity", StandardBasicTypes.STRING)
				.addScalar("cdState", StandardBasicTypes.STRING).addScalar("addrZip", StandardBasicTypes.STRING)
				.addScalar("nbrPhone", StandardBasicTypes.STRING)
				.addScalar("cdPlacementRqstType", StandardBasicTypes.STRING)
				.addScalar("idICPCPlcmntRqstEntity", StandardBasicTypes.LONG).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ICPCAgencyDto.class)).list();

		if (!CollectionUtils.isEmpty(agencyDetailsList)) {

			for (ICPCAgencyDto agencyDetails : agencyDetailsList) {
				if (ServiceConstants.OTHER.equalsIgnoreCase(agencyDetails.getAgencyCounty())) {
					ICPCAgencyDto icpcAgencyDto = getICPCAgencyOtherPlacementDetails(agencyDetails.getIdICPCPlcmntRqstEntity());

					if (!ObjectUtils.isEmpty(icpcAgencyDto)) {
						agencyDetails.setAgencyOther(icpcAgencyDto.getAgencyOther());
						agencyDetails.setAddrStLn1(icpcAgencyDto.getAddrStLn1());
						agencyDetails.setAddrStLn2(icpcAgencyDto.getAddrStLn2());
						agencyDetails.setAddrCity(icpcAgencyDto.getAddrCity());
						agencyDetails.setCdState(icpcAgencyDto.getCdState());
						agencyDetails.setAddrZip(icpcAgencyDto.getAddrZip());
						agencyDetails.setNbrPhone(icpcAgencyDto.getNbrPhone());
					}
				}
			}

			agencyDetailsList.replaceAll(icpcAgency -> {
				icpcAgency.setAgencyState(icpcAgency.getAgencyCounty());
				if (!ObjectUtils.isEmpty(icpcAgency.getAgencyCounty())&& !(Stream
						.of(ServiceConstants.CD_AGENCY_TYPE60, ServiceConstants.CD_AGENCY_TYPE50,
								ServiceConstants.CD_AGENCY_TYPE40, ServiceConstants.CD_AGENCY_TYPE10)
						.anyMatch(icpcAgency.getAgencyCounty()::equalsIgnoreCase)))
					icpcAgency.setAgencyCounty(null);
				return icpcAgency;
			});
		}

		return agencyDetailsList;
	}

	/**
	 * Method Description: Fetch the Agency Other Placement Details
	 *
	 * @param idICPCPlcmntRqstEntity
	 * @return ICPCAgencyDto
	 */
	private ICPCAgencyDto getICPCAgencyOtherPlacementDetails(Long idICPCPlcmntRqstEntity) {
		return (ICPCAgencyDto) sessionFactory.getCurrentSession()
				.createSQLQuery(agencyDetailsForOtherPlacement)
				.addScalar("idICPCPlcmntRqstEntity", StandardBasicTypes.LONG)
				.addScalar("agencyOther", StandardBasicTypes.STRING)
				.addScalar("addrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrStLn2", StandardBasicTypes.STRING)
				.addScalar("addrCity", StandardBasicTypes.STRING)
				.addScalar("cdState", StandardBasicTypes.STRING)
				.addScalar("addrZip", StandardBasicTypes.STRING)
				.addScalar("nbrPhone", StandardBasicTypes.STRING)
				.setParameter("idICPCPlcmntRqstEntity", idICPCPlcmntRqstEntity)
				.setResultTransformer(Transformers.aliasToBean(ICPCAgencyDto.class))
				.uniqueResult();
	}

	@Override
	public Long getStatusPersonAddressLink(Long idIcpcPlacementStatus, String cdAddrType) {
		return (Long) sessionFactory.getCurrentSession().createSQLQuery(getStatusPersonAddressLinkSql)
				.addScalar("idSttsPersAddrLink", StandardBasicTypes.LONG)
				.setParameter("idIcpcPlacementStatus", idIcpcPlacementStatus).setParameter("cdAddressType", cdAddrType)
				.list().stream().findFirst().orElse(null);
	}

	@Override
	public Long getStatusResourceAddressLink(Long idIcpcPlacementStatus, String cdAddrType) {
		return (Long) sessionFactory.getCurrentSession().createSQLQuery(getStatusResourceAddressLinkSql)
				.addScalar("idSttsRsrcAddr", StandardBasicTypes.LONG)
				.setParameter("idIcpcPlacementStatus", idIcpcPlacementStatus).setParameter("cdAddressType", cdAddrType)
				.list().stream().findFirst().orElse(null);
	}

	@Override
	public List<String> getrequestEnclosure(Long idEvent) {
		return (List<String>) sessionFactory.getCurrentSession().createSQLQuery(getrequestEnclosureSql)
				.setParameter("idEvent", idEvent).list();
	}

	/**
	 * Method Name: getPrimaryWorkerInfo Method Description: get aPrimary Worker
	 * Info
	 * 
	 * @param idStage
	 * @return ICPCPersonDto
	 */
	@Override
	public ICPCPersonDto getPrimaryWorkerInfo(Long idStage) {

		ICPCPersonDto icpcPersonDto=new ICPCPersonDto();
		icpcPersonDto = (ICPCPersonDto) sessionFactory.getCurrentSession()
				.createSQLQuery(getPrimaryWorkerInfo).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("addrLine1", StandardBasicTypes.STRING).addScalar("addrLine2", StandardBasicTypes.STRING)
				.addScalar("addrCity", StandardBasicTypes.STRING).addScalar("addrZip", StandardBasicTypes.STRING)
				.addScalar("nbrPhone", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("stagePersonRole", CodesConstant.CROLEALL_PR)
				.setResultTransformer(Transformers.aliasToBean(ICPCPersonDto.class)).uniqueResult();
		
		String businessPhone = null;
		if (!ObjectUtils.isEmpty(icpcPersonDto))
		{
			icpcPersonDto.setAddrState(ServiceConstants.CSTATE_TX); // Defect 11189
			businessPhone = getPrimaryWorkerPhone(icpcPersonDto.getIdPerson());		
			icpcPersonDto.setNbrPhone(businessPhone);
		}
		return icpcPersonDto;
	}

	/**
	 * Method Name: getEligibility Method Description: get Eligibility
	 * information
	 * 
	 * @param idPersonChild
	 * @return String
	 */
	@Override
	public String getEligibility(Long idPersonChild) {

		String titleIVE = (String) sessionFactory.getCurrentSession().createSQLQuery(getEligibility)
				.addScalar("titleIVE", StandardBasicTypes.STRING).setParameter("idPersonChild", idPersonChild)
				.uniqueResult();
		if (StringUtils.isEmpty(titleIVE)) {
			titleIVE = "P";
		}
		return titleIVE;
	}

	/**
	 * Method Name: getDisasterRlf Method Description: get Disaster Relief
	 * information
	 * 
	 * @param idPersonChild
	 * @return String
	 */
	@Override
	public String getDisasterRlf(Long idPersonChild, Long idICPCPlacementRequest) {

		String personDisasterRlf = null;
		if (ObjectUtils.isEmpty(idICPCPlacementRequest) || idICPCPlacementRequest == 0) {

			personDisasterRlf = (String) sessionFactory.getCurrentSession().createCriteria(Person.class)
					.setProjection(Projections.property("cdDisasterRlf"))
					.add(Restrictions.eq("idPerson", idPersonChild)).uniqueResult();

		} else {
			personDisasterRlf = (String) sessionFactory.getCurrentSession().createSQLQuery(getDisasterReqRlf)
					.addScalar("disasterRlf", StandardBasicTypes.STRING).setParameter("idPerson", idPersonChild)
					.setParameter("idICPCPlacementRequest", idICPCPlacementRequest).uniqueResult();

		}

		return personDisasterRlf;
	}

	/**
	 * Method Name: deleteICPCTransmittal Method Description: Delete the
	 * transmittal details to the database from Placement Request Details page
	 * 
	 * @param idIcpcTransmittal
	 *
	 */
	public void deleteICPCTransmittal(Long idIcpcTransmittal) {
		IcpcTransmittal icpcTransmittal = (IcpcTransmittal) sessionFactory.getCurrentSession()
				.get(IcpcTransmittal.class, idIcpcTransmittal);
		if (!ObjectUtils.isEmpty(icpcTransmittal)) {
			sessionFactory.getCurrentSession().delete(icpcTransmittal);
		}
	}

	/**
	 * 
	 * Method Name: saveICPCSubmission Method Description:inserts or updates the
	 * ICPC Summary Record.
	 * 
	 * @param icpcPlacementRequestDto
	 * @return void
	 */
	@Override
	public void saveICPCSubmission(ICPCPlacementRequestDto icpcPlacementRequestDto) {

		IcpcSubmission icpcSubmission = null;
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIdICPCsubmission())
				&& icpcPlacementRequestDto.getIdICPCsubmission() > ServiceConstants.ZERO) {
			icpcSubmission = (IcpcSubmission) sessionFactory.getCurrentSession().load(IcpcSubmission.class,
					icpcPlacementRequestDto.getIdICPCsubmission());
		} else {
			icpcSubmission = new IcpcSubmission();
		}
		icpcSubmission.setNbrIcpcCaseLegacy(icpcPlacementRequestDto.getNbrLegacyCase());
		icpcSubmission.setNbrIcpcCaseOtherState(icpcPlacementRequestDto.getNbrOtherCase());
		icpcSubmission.setIdCreatedPerson(icpcPlacementRequestDto.getIdCreatedPerson());
		icpcSubmission.setIdStage(icpcPlacementRequestDto.getIdStage());

		icpcSubmission.setDtLastUpdate(new Date());
		icpcSubmission.setIdLastUpdatePerson(icpcPlacementRequestDto.getIdLastUpdatePerson());
		if (ObjectUtils.isEmpty(icpcPlacementRequestDto.getIdICPCsubmission())) {
			icpcSubmission.setDtCreated(new Date());

		}
		sessionFactory.getCurrentSession().saveOrUpdate(icpcSubmission);
	}

	/**
	 * 
	 * Method Name: retrieveIntakeStageId Method Description:retreives the
	 * intake Stage Id
	 * 
	 * @param icpcPlacementRequestDto
	 * @return void
	 */
	public Long retrieveIntakeStageId(Long idCase, String cdStage) {
		List<Long> intakeStageIdList = (List<Long>) sessionFactory.getCurrentSession()
				.createSQLQuery(retrieveIntakeStageId).addScalar("idStage", StandardBasicTypes.LONG)
				.setParameter("idCase", idCase).setParameter("cdStage", cdStage).list();
		Long idIntakeStage = intakeStageIdList.get(0);
		return idIntakeStage;
	}

	/**
	 * 
	 * Method Name: getIdICPCSubmission Method Description:Get a new Submission
	 * id
	 * 
	 * @param idStage
	 * @return Long
	 */
	@Override
	public Long getIdICPCSubmission(Long idStage) {
		Long idIcpcSubmission = ServiceConstants.ZERO;				
		List<Long> idIcpcSubmissionList = (List<Long>) sessionFactory.getCurrentSession().createCriteria(IcpcSubmission.class)
				.setProjection(Projections.property("idIcpcSubmission")).add(Restrictions.eq("idStage", idStage))
				.list();
		
		if(!ObjectUtils.isEmpty(idIcpcSubmissionList)){
			idIcpcSubmission = idIcpcSubmissionList.get(0);
		}
		return idIcpcSubmission;
	}

	/**
	 * Method Name: getIdICPCSubmissionByRequest Method Description:Get a new Submission id
	 * @param idIcpcRequest
	 * @return
	 */
	@Override
	public Long getIdICPCSubmissionByRequest(Long idIcpcRequest) {
		Long idIcpcSubmission = ServiceConstants.ZERO;
		List<Long> idIcpcSubmissionList = (List<Long>) sessionFactory.getCurrentSession().createCriteria(IcpcRequest.class)
				.setProjection(Projections.property("idIcpcSubmission")).add(Restrictions.eq("idIcpcRequest", idIcpcRequest))
				.list();

		if(!ObjectUtils.isEmpty(idIcpcSubmissionList)){
			idIcpcSubmission = idIcpcSubmissionList.get(0);
		}
		return idIcpcSubmission;
	}

	/**
	 * 
	 * Method Name: insertICPCSubmission Method Description: This method inserts
	 * record into ICPC_SUBMISSION table.
	 * 
	 * @param idStage
	 * @param idIcpcReqSubmission
	 * @param confirmation
	 * @param icpcCaseOtherState
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	@Override
	public Long insertICPCSubmission(Long idStage, Long idIcpcReqSubmission, String confirmation,
			String icpcCaseOtherState, Long idLastUpdatePerson) {
		IcpcSubmission icpcSubmission = new IcpcSubmission();
		icpcSubmission.setNbrIcpcCaseLegacy(null);
		icpcSubmission.setIdReqSubmission(idIcpcReqSubmission);
		icpcSubmission.setNbrIcpcCaseOtherState(icpcCaseOtherState);
		icpcSubmission.setIdCreatedPerson(idLastUpdatePerson);
		icpcSubmission.setIdStage(idStage);
		icpcSubmission.setNbrConfirmation(confirmation);
		icpcSubmission.setIdLastUpdatePerson(idLastUpdatePerson);
		icpcSubmission.setDtCreated(new Date());
		icpcSubmission.setDtLastUpdate(new Date());
		Long idICPCsubmission = (Long) sessionFactory.getCurrentSession().save(icpcSubmission);
		return idICPCsubmission;
	}

	/**
	 * 
	 * Method Name: insertICPCRequest Method Description: This method inserts
	 * record into ICPC_REQUEST table.
	 * 
	 * @param idIcpcReqSubmission
	 * @param idIcpcRequestPrtl
	 * @param cdRequestType
	 * @param cdReceivingState
	 * @param cdSendingState
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	public Long insertICPCRequest(Long idIcpcSubmission, Long idIcpcRequestPrtl, String cdRequestType,
			String cdReceivingState, String cdSendingState, Long idLastUpdatePerson,
			 Long idSendingStageAgency,Long idRecevingStateAgency) {

		IcpcRequest icpcRequest = new IcpcRequest();
		icpcRequest.setIdIcpcSubmission(idIcpcSubmission);
		icpcRequest.setIdIcpcPortalRequest(idIcpcRequestPrtl);
		icpcRequest.setCdRequestType(cdRequestType);
		icpcRequest.setIdCreatedPerson(idLastUpdatePerson);
		icpcRequest.setCdReceivingState(cdReceivingState);
		icpcRequest.setCdSendingState(cdSendingState);
		icpcRequest.setIdSendingStateAgency(idSendingStageAgency);
		icpcRequest.setIdReceivingStateAgency(idRecevingStateAgency);
		icpcRequest.setIdLastUpdatePerson(idLastUpdatePerson);
		icpcRequest.setDtCreated(new Date());
		icpcRequest.setDtLastUpdate(new Date());
		Long idICPCRequest = (Long) sessionFactory.getCurrentSession().save(icpcRequest);
		return idICPCRequest;
	}

	/**
	 * 
	 * Method Name: insertICPCEventLink Method Description: This method inserts
	 * record into ICPC_EVENT_LINK table.
	 * @param idIcpcRequest
	 * @param idEvent
	 * @param idCase
	 * @param idLastUpdatePerson
	 * @param neiceCaseId
	 */
	@Override
	public void insertICPCEventLink(Long idIcpcRequest, Long idEvent, Long idCase, Long idLastUpdatePerson, String neiceCaseId) {

		IcpcEventLink icpcEventLink = new IcpcEventLink();
		icpcEventLink.setIdIcpcRequest(idIcpcRequest);
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, idCase);
		if (!ObjectUtils.isEmpty(capsCase))
			icpcEventLink.setCapsCase(capsCase);
		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, idEvent);
		if (!ObjectUtils.isEmpty(event))
			icpcEventLink.setEvent(event);
		icpcEventLink.setIdCreatedPerson(idLastUpdatePerson);
		icpcEventLink.setIdLastUpdatePerson(idLastUpdatePerson);
		icpcEventLink.setDtCreated(new Date());
		icpcEventLink.setDtLastUpdate(new Date());
		icpcEventLink.setIdNeiceCase(neiceCaseId);
		sessionFactory.getCurrentSession().save(icpcEventLink);

	}

	/**
	 * 
	 * Method Name: insertPlacementRequest Method Description: This method
	 * inserts record into ICPC_PLACEMENT_REQUEST table.
	 * 
	 * @param ICPCPlacementRequestDto
	 * @return Long
	 */
	@Override
	public Long insertPlacementRequest(ICPCPlacementRequestDto icpcPlacementRequestDto) {

		IcpcPlacementRequest icpcPlacementRequest = new IcpcPlacementRequest();

		icpcPlacementRequest.setIdIcpcRequest(icpcPlacementRequestDto.getIdICPCRequest()); // ID_ICPC_REQUEST
		icpcPlacementRequest.setDtReceived(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getDtReceived())
				? icpcPlacementRequestDto.getDtReceived() : null);
		icpcPlacementRequest.setIndPriority(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIndPriority())
				? icpcPlacementRequestDto.getIndPriority().charAt(0) : null); // IND_PRIORITY
		icpcPlacementRequest.setIndIcwaEligible(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIndICWAEligible())
				? icpcPlacementRequestDto.getIndICWAEligible().charAt(0) : null); // IND_ICWA_ELIGIBLE
		icpcPlacementRequest.setIndTitleIve(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIndTitleIVE())
				? icpcPlacementRequestDto.getIndTitleIVE().charAt(0) : null); // IND_TITLE_IVE
		icpcPlacementRequest.setCdLegalStatus(icpcPlacementRequestDto.getCdLegalStatus()); // CD_LEGAL_STATUS
		icpcPlacementRequest.setTxtLegalStatus(icpcPlacementRequestDto.getTxtLegalStatus()); // TXT_LEGAL_STATUS
		icpcPlacementRequest.setCdCareType(icpcPlacementRequestDto.getCdCareType()); // CD_CARE_TYPE
		icpcPlacementRequest.setTxtxCareType(icpcPlacementRequestDto.getTxtCareType());
		icpcPlacementRequest.setCdInitialReport(icpcPlacementRequestDto.getCdInitialReport()); // CD_INITIAL_REPORT
		icpcPlacementRequest.setCdSprvsrySrvcs(icpcPlacementRequestDto.getCdSprrvsrySrvcs()); // CD_SPRVSRY_SRVCS
		icpcPlacementRequest.setCdSprvsryRprts(icpcPlacementRequestDto.getCdSprvsryRprts()); // CD_SPRVSRY_RPRTS
		icpcPlacementRequest.setTxtNotes(icpcPlacementRequestDto.getTxtNotes()); // TXT_NOTES
		icpcPlacementRequest.setDtHomeAssessSent(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getDtHomeAssessSent())
				? icpcPlacementRequestDto.getDtHomeAssessSent() : null);
		icpcPlacementRequest.setDtHomeAssessRcvd(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getDtHomeAssessRcvd())
				? icpcPlacementRequestDto.getDtHomeAssessRcvd() : null);
		icpcPlacementRequest.setCdDecision(icpcPlacementRequestDto.getCdDecision()); // CD_DECISION
		icpcPlacementRequest.setDtDecision(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getDtDecision())
				? icpcPlacementRequestDto.getDtDecision() : null);
		icpcPlacementRequest.setTxtPlacementRemarks(icpcPlacementRequestDto.getTxtPlacementRemarks()); // TXT_PLACEMENT_REMARKS
		icpcPlacementRequest.setCdViolation(icpcPlacementRequestDto.getCdViolation()); // CD_VIOLATION
		icpcPlacementRequest.setCdWithdrawalReason(icpcPlacementRequestDto.getCdWithdrawalReason()); // CD_WITHDRAWAL_REASON
		icpcPlacementRequest.setIndNaturalDisaster(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIndNaturalDisaster())
				? icpcPlacementRequestDto.getIndNaturalDisaster().charAt(0) : null);
		icpcPlacementRequest.setTxtNaturalDisaster(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getTxtNaturalDisaster())
				&& !ServiceConstants.EMPTY_STRING.equals(icpcPlacementRequestDto.getTxtNaturalDisaster())
						? icpcPlacementRequestDto.getTxtNaturalDisaster() : null);
		icpcPlacementRequest.setDtCreated(new Date());
		icpcPlacementRequest.setDtLastUpdate(new Date());
		icpcPlacementRequest.setIdCreatedPerson(icpcPlacementRequestDto.getIdCreatedPerson()); // ID_CREATED_PERSON
		icpcPlacementRequest.setIdLastUpdatePerson(icpcPlacementRequestDto.getIdLastUpdatePerson()); // ID_LAST_UPDATE_PERSON
		icpcPlacementRequest.setTxtLegalStatus(icpcPlacementRequestDto.getTxtLegalStatus());
		icpcPlacementRequest.setTxtxCareType(icpcPlacementRequestDto.getTxtCareType());
		icpcPlacementRequest.setIndPubPrivPlcmt(icpcPlacementRequestDto.getPubPrivBtn());
		icpcPlacementRequest.setCdSubsidy(icpcPlacementRequestDto.getCdSubsidy());
		icpcPlacementRequest.setTxtSprvsrySrvcs(icpcPlacementRequestDto.getTxtSupSrvs());
		icpcPlacementRequest.setTxtSprvsryRprts(icpcPlacementRequestDto.getTxtSupRprts());
		icpcPlacementRequest.setCdReasonDenial(icpcPlacementRequestDto.getCdDenialReason());
		icpcPlacementRequest.setTxtReasonDenial(icpcPlacementRequestDto.getTxtDenialReason());
		icpcPlacementRequest.setDtHomeStudyDue(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getHomeStudyDt())
				? icpcPlacementRequestDto.getHomeStudyDt() : null);

		Long idIcpcPlacementRequest = (Long) sessionFactory.getCurrentSession().save(icpcPlacementRequest);

		return idIcpcPlacementRequest;
	}

	@Override
	public List<IcpcRequestPersonLink> getIcpcRequestPersonLinks(Long idICPCPlacementRequest) {

		IcpcPlacementRequest icpcPlacementRequest = (IcpcPlacementRequest) sessionFactory.getCurrentSession()
				.createCriteria(IcpcPlacementRequest.class)
				.add(Restrictions.eq("idIcpcPlacementRequest", idICPCPlacementRequest))
				.uniqueResult();

		// Fetch ICPC_REQUEST_PERSON_LINK
		return (List<IcpcRequestPersonLink>) sessionFactory.getCurrentSession()
				.createCriteria(IcpcRequestPersonLink.class)
				.add(Restrictions.eq("icpcRequest.idIcpcRequest", icpcPlacementRequest.getIdIcpcRequest()))
				.add(Restrictions.eq("cdPersonType", CodesConstant.ICPCPRTP_60))
				.list();
	}

	/**
	 *
	 * Method Name: insertICPCRequestPersonLink Method Description: This method
	 * inserts record into ICPC_REQUEST_PERSON_LINK table.
	 *  @param idIcpcRequest
	 * @param idPerson
	 * @param cdRequestPersonType
	 * @param idLastUpdatePerson
	 * @param idPersonRelation
	 * @param neicePersonId
	 */
	@Override
	public void insertICPCRequestPersonLink(Long idIcpcRequest, Long idPerson, String cdRequestPersonType,
											Long idLastUpdatePerson, Long idPersonRelation, String neicePersonId) {

		insertICPCRequestPersonLink(idIcpcRequest, idPerson, cdRequestPersonType, idLastUpdatePerson, idPersonRelation,
				neicePersonId, null);

	}

	/**
	 * 
	 * Method Name: insertICPCRequestPersonLink Method Description: This method
	 * inserts record into ICPC_REQUEST_PERSON_LINK table.
	 *  @param idIcpcRequest
	 * @param idPerson
	 * @param cdRequestPersonType
	 * @param idLastUpdatePerson
	 * @param idPersonRelation
	 * @param neicePersonId
	 * @param cdPlacementCode
	 */
	@Override
	public void insertICPCRequestPersonLink(Long idIcpcRequest, Long idPerson, String cdRequestPersonType,
											Long idLastUpdatePerson, Long idPersonRelation, String neicePersonId,
											String cdPlacementCode) {
		
		if (null == idPersonRelation) {
			idPersonRelation = 0l;
		}
		IcpcRequestPersonLink icpcRequestPersonLink = new IcpcRequestPersonLink();
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, idPerson);
		if (!ObjectUtils.isEmpty(person))
			icpcRequestPersonLink.setPerson(person);
		PersonRelation personRelation = (PersonRelation) sessionFactory.getCurrentSession().get(PersonRelation.class,
				idPersonRelation);
		if (!ObjectUtils.isEmpty(personRelation))
			icpcRequestPersonLink.setPersonRelation(personRelation);
		IcpcRequest icpcRequest = (IcpcRequest) sessionFactory.getCurrentSession().get(IcpcRequest.class,
				idIcpcRequest);
		if (!ObjectUtils.isEmpty(icpcRequest))
			icpcRequestPersonLink.setIcpcRequest(icpcRequest);
		icpcRequestPersonLink.setCdPersonType(cdRequestPersonType);
		icpcRequestPersonLink.setIdCreatedPerson(idLastUpdatePerson);
		icpcRequestPersonLink.setIdLastUpdatePerson(idLastUpdatePerson);
		icpcRequestPersonLink.setIdPersonNeice(neicePersonId);
		icpcRequestPersonLink.setCdPlacementCode(cdPlacementCode);
		icpcRequestPersonLink.setDtCreated(new Date());
		icpcRequestPersonLink.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(icpcRequestPersonLink);

	}

	/**
	 * 
	 * Method Name: insertICPCRequestEnclosure Method Description: This method
	 * inserts record into ICPC_REQUEST_ENCLOSURE table.
	 * 
	 * @param idEvent
	 * @param cdEnclosure
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	@Override
	public Long insertICPCRequestEnclosure(Long idEvent, String cdEnclosure, Long idLastUpdatePerson) {
		IcpcRequestEnclosure icpcRequestEnclosure = new IcpcRequestEnclosure();
		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, idEvent);
		icpcRequestEnclosure.setEvent(event);
		icpcRequestEnclosure.setCdEnclosure(cdEnclosure);
		icpcRequestEnclosure.setIdLastUpdatePerson(idLastUpdatePerson);
		icpcRequestEnclosure.setIdCreatedPerson(idLastUpdatePerson);
		icpcRequestEnclosure.setDtLastUpdate(new Date());
		icpcRequestEnclosure.setDtCreated(new Date());
		return (Long) sessionFactory.getCurrentSession().save(icpcRequestEnclosure);
	}

	/**
	 * 
	 * Method Name: insertICPCRequestResourceLink Method Description: This
	 * method inserts record into ICPC_REQUEST_RESOURCE_LINK table.
	 * 
	 * @param idIcpcRequest
	 * @param idResource
	 * @param idLastUpdatePerson
	 */
	@Override
	public void insertICPCRequestResourceLink(Long idIcpcRequest, Long idResource, Long idLastUpdatePerson, String idResourceNeice) {
		IcpcRequestResourceLink icpcRequestResourceLink = new IcpcRequestResourceLink();
		IcpcRequest icpcRequest = (IcpcRequest) sessionFactory.getCurrentSession().get(IcpcRequest.class,
				idIcpcRequest);
		if (!ObjectUtils.isEmpty(icpcRequest))
			icpcRequestResourceLink.setIcpcRequest(icpcRequest);
		CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
				idResource);
		if (!ObjectUtils.isEmpty(capsResource)) {
			icpcRequestResourceLink.setCapsResource(capsResource);
		}
		icpcRequestResourceLink.setIdResourceNeice(idResourceNeice);
		icpcRequestResourceLink.setIdLastUpdatePerson(idLastUpdatePerson);
		icpcRequestResourceLink.setIdCreatedPerson(idLastUpdatePerson);
		icpcRequestResourceLink.setDtLastUpdate(new Date());
		icpcRequestResourceLink.setDtCreated(new Date());
		sessionFactory.getCurrentSession().save(icpcRequestResourceLink);

	}

	/**
	 * 
	 * Method Name: insertEntity Method Description:This method inserts record
	 * into ENTITY table.
	 * 
	 * @param entity
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	@Override
	public Long insertEntity(String entity, Long idLastUpdatePerson) {
		IcpcEntity icpcEntity = new IcpcEntity();
		icpcEntity.setNmEntity(entity); // NM_ENTITY
		icpcEntity.setCdEntityType(String.valueOf(ServiceConstants.AGENCY_10)); // CD_ENTITY_TYPE
		icpcEntity.setIdCreatedPerson(idLastUpdatePerson); // ID_CREATED_PERSON
		icpcEntity.setIdLastUpdatePerson(idLastUpdatePerson); // ID_LAST_UPDATE_PERSON
		icpcEntity.setDtLastUpdate(new Date());
		icpcEntity.setDtCreated(new Date());
		// Execute insert Query.
		return (Long) sessionFactory.getCurrentSession().save(icpcEntity);
	}

	/**
	 * 
	 * Method Name: insertEntityAddress Method Description:This method inserts
	 * record into ENTITY_ADDRESS table.
	 * 
	 * @param idEntity
	 * @param icpcAgencyDto
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	@Override
	public void insertEntityAddress(Long idEntity, ICPCAgencyDto icpcAgencyDto, Long idLastUpdatePerson) {
		EntityAddress entityAddress = new EntityAddress();
		entityAddress.setIdEntyAddressOwner(idEntity); // (This field stores
														// Primary
		entityAddress.setAddrStLn1(icpcAgencyDto.getAddrStLn1()); // ADDR_ST_LN1
		entityAddress.setAddrStLn2(icpcAgencyDto.getAddrStLn2()); // ADDR_ST_LN2
		entityAddress.setAddrCity(icpcAgencyDto.getAddrCity()); // ADDR_CITY
		entityAddress.setCdState(icpcAgencyDto.getCdState()); // CD_STATE
		entityAddress.setAddrZip(icpcAgencyDto.getAddrZip()); // ADDR_ZIP
		entityAddress.setIdCreatedPerson(idLastUpdatePerson); // ID_CREATED_PERSON
		entityAddress.setIdLastUpdatePerson(idLastUpdatePerson); // ID_LAST_UPDATE_PERSON
		entityAddress.setDtLastUpdate(new Date());
		entityAddress.setDtCreated(new Date());

		// Execute insert Query.
		sessionFactory.getCurrentSession().saveOrUpdate(entityAddress);
	}

	/**
	 * 
	 * Method Name: insertEntityPhone Method Description:This method inserts
	 * record into ENTITY_PHONE table.
	 * 
	 * @param idEntity
	 * @param icpcAgencyDto
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	@Override
	public void insertEntityPhone(Long idEntity, ICPCAgencyDto icpcAgencyDto, Long idLastUpdatePerson) {
		EntityPhone entityPhone = new EntityPhone();

		entityPhone.setIdEntityPhoneOwner(idEntity); // ID_ENTY_PHONE_OWNER(This
		// field stores Primary key id
		// of ENTITY)
		entityPhone.setCdPhoneType(CodesConstant.CPHNTYP_BS); // CD_PHONE_TYPE
		entityPhone.setNbrPhone(Long.parseLong(
				!ObjectUtils.isEmpty(icpcAgencyDto.getNbrPhone()) && icpcAgencyDto.getNbrPhone().length() > 10
						? icpcAgencyDto.getNbrPhone().substring(1, 4) + icpcAgencyDto.getNbrPhone().substring(6, 9)
								+ icpcAgencyDto.getNbrPhone().substring(10, 14)
						: icpcAgencyDto.getNbrPhone())); // NBR_PHONE
		entityPhone.setNbrPhoneExtension(null); // NBR_PHONE_EXT

		entityPhone.setIdCreatedPerson(idLastUpdatePerson); // ID_CREATED_PERSON
		entityPhone.setIdLastUpdatePerson(idLastUpdatePerson); // ID_LAST_UPDATE_PERSON
		entityPhone.setDtLastUpdate(new Date());
		entityPhone.setDtCreated(new Date());
		// Execute insert Query.
		sessionFactory.getCurrentSession().saveOrUpdate(entityPhone);

	}

	/**
	 * 
	 * Method Name: insertICPCAgencyType Method Description:This method inserts
	 * record into ICPC_AGENCY_TYPE table.
	 * 
	 * @param idEntity
	 * @param agencyType
	 * @param idLastUpdatePerson
	 */
	@Override
	public void insertICPCAgencyType(Long idEntity, Long agencyType, Long idLastUpdatePerson) {

		IcpcAgencyType icpcAgencyType = new IcpcAgencyType();
		IcpcEntity entity = new IcpcEntity();
		entity.setIdicpcEntity(idEntity);
		icpcAgencyType.setEntity(entity); // ID_ENTITY
		icpcAgencyType.setCdAgencyType(String.valueOf(agencyType)); // CD_AGENCY_TYPE
		icpcAgencyType.setIdCreatedPerson(idLastUpdatePerson); // ID_CREATED_PERSON
		icpcAgencyType.setIdLastUpdatePerson(idLastUpdatePerson); // ID_LAST_UPDATE_PERSON
		icpcAgencyType.setDtLastUpdate(new Date());
		icpcAgencyType.setDtCreated(new Date());
		// Execute insert Query.
		sessionFactory.getCurrentSession().saveOrUpdate(icpcAgencyType);
	}

	/**
	 * 
	 * Method Name: insertIcpcPlacementRqstEnity Method Description: This method
	 * inserts record into ICPC_PLACEMENT_REQUEST table.
	 * 
	 * @param idEntity
	 * @param idIcpcPlacementRequest
	 * @param cdPlcmntRqstType
	 * @param IdCreatedPerson
	 * @return Long
	 */
	@Override
	public Long insertICPCPlacementRqstEnity(Long idEntity, Long idIcpcPlacementRequest, String cdPlcmntRqstType,
			Long IdCreatedPerson) {

		IcpcPlacementRequestEntity icpcPlacementRequestEntity = new IcpcPlacementRequestEntity();
		IcpcEntity icpcEntity = new IcpcEntity();
		icpcEntity.setIdicpcEntity(idEntity);
		icpcPlacementRequestEntity.setIcpcEntity(icpcEntity); // ID_ENTITY

		IcpcPlacementRequest icpcReq = new IcpcPlacementRequest();
		icpcReq.setIdIcpcPlacementRequest(idIcpcPlacementRequest);
		icpcPlacementRequestEntity.setIcpcPlacementRequest(icpcReq); // ID_ICPC_PLACEMENT_REQUEST

		icpcPlacementRequestEntity.setCdType(cdPlcmntRqstType); // CD_TYPE
		icpcPlacementRequestEntity.setIdCreatedPerson(IdCreatedPerson); // ID_CREATED_PERSON
		icpcPlacementRequestEntity.setIdLastUpdatePerson(IdCreatedPerson); // ID_LAST_UPDATE_PERSON
		icpcPlacementRequestEntity.setDtLastUpdate(new Date());
		icpcPlacementRequestEntity.setDtCreated(new Date());

		// Execute insert Query.
		return (Long) sessionFactory.getCurrentSession().save(icpcPlacementRequestEntity);
	}

	/**
	 *
	 * Method Name: insertICPCPlacementRqstEnityOther
	 * Method Description:This method inserts record into ICPC_PLCMT_REQ_ENTITY_OTHER table.
	 *
	 * @param idIcpcPlacementRqstEntity
	 * @param icpcAgencyDto
	 * @param idLastUpdatePerson
	 *
	 */
	@Override
	public void insertICPCPlacementRqstEnityOther(Long idIcpcPlacementRqstEntity, ICPCAgencyDto icpcAgencyDto,
												  Long idLastUpdatePerson) {
		IcpcPlcmtReqEntityOther icpcPlcmtReqEntityOther = new IcpcPlcmtReqEntityOther();
		icpcPlcmtReqEntityOther.setIdIcpcPlcmtReqEntity(idIcpcPlacementRqstEntity);
		icpcPlcmtReqEntityOther.setNmEntity(icpcAgencyDto.getAgencyOther());
		icpcPlcmtReqEntityOther.setAddrStLn1(icpcAgencyDto.getAddrStLn1());
		icpcPlcmtReqEntityOther.setAddrStLn2(icpcAgencyDto.getAddrStLn2());
		icpcPlcmtReqEntityOther.setAddrCity(icpcAgencyDto.getAddrCity());
		icpcPlcmtReqEntityOther.setCdState(icpcAgencyDto.getCdState());
		icpcPlcmtReqEntityOther.setAddrZip(icpcAgencyDto.getAddrZip());
		icpcPlcmtReqEntityOther.setIdCreatedPerson(idLastUpdatePerson);
		icpcPlcmtReqEntityOther.setIdLastUpdatePerson(idLastUpdatePerson);
		icpcPlcmtReqEntityOther.setDtLastUpdate(new Date());
		icpcPlcmtReqEntityOther.setDtCreated(new Date());
		if (!ObjectUtils.isEmpty(icpcAgencyDto.getNbrPhone()) && !StringUtils.isEmpty(icpcAgencyDto.getNbrPhone())) {
			String phoneNum = icpcAgencyDto.getNbrPhone().replaceAll("[\\s\\-()]", "");
			icpcPlcmtReqEntityOther.setNbrPhone(Long.parseLong(phoneNum));
		}
		icpcPlcmtReqEntityOther.setNbrPhoneExtension(null);

		// Execute insert Query.
		sessionFactory.getCurrentSession()
				.saveOrUpdate(icpcPlcmtReqEntityOther);
	}

	/**
	 *
	 * Method Name: updateICPCPlacementRqstEnityOther
	 * Method Description:This method inserts record into ICPC_PLCMT_REQ_ENTITY_OTHER table.
	 *
	 * @param idIcpcPlacementRqstEntity
	 * @param icpcAgencyDto
	 * @param idLastUpdatePerson
	 *
	 */
	@Override
	public void updateICPCPlacementRqstEnityOther(Long idIcpcPlacementRqstEntity, ICPCAgencyDto icpcAgencyDto,
										   Long idLastUpdatePerson) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IcpcPlcmtReqEntityOther.class);
		criteria.add(Restrictions.eq("idIcpcPlcmtReqEntity", idIcpcPlacementRqstEntity));
		IcpcPlcmtReqEntityOther icpcPlcmtReqEntityOther = (IcpcPlcmtReqEntityOther) criteria.list().stream()
				.findAny().orElse(null);

		if (!ObjectUtils.isEmpty(icpcPlcmtReqEntityOther)) {

			icpcPlcmtReqEntityOther.setNmEntity(icpcAgencyDto.getAgencyOther());
			icpcPlcmtReqEntityOther.setAddrStLn1(icpcAgencyDto.getAddrStLn1());
			icpcPlcmtReqEntityOther.setAddrStLn2(icpcAgencyDto.getAddrStLn2());
			icpcPlcmtReqEntityOther.setAddrCity(icpcAgencyDto.getAddrCity());
			icpcPlcmtReqEntityOther.setCdState(icpcAgencyDto.getCdState());
			icpcPlcmtReqEntityOther.setAddrZip(icpcAgencyDto.getAddrZip());
			icpcPlcmtReqEntityOther.setIdLastUpdatePerson(idLastUpdatePerson);
			icpcPlcmtReqEntityOther.setDtLastUpdate(new Date());
			if(!ObjectUtils.isEmpty(icpcAgencyDto.getNbrPhone()) && !StringUtils.isEmpty(icpcAgencyDto.getNbrPhone())) {
				String phoneNum = icpcAgencyDto.getNbrPhone().replaceAll("[\\s\\-()]", "");
				icpcPlcmtReqEntityOther.setNbrPhone(Long.parseLong(phoneNum));
			}

			sessionFactory.getCurrentSession()
					.saveOrUpdate(icpcPlcmtReqEntityOther);

			sessionFactory.getCurrentSession().flush();
		} else {
			insertICPCPlacementRqstEnityOther(idIcpcPlacementRqstEntity, icpcAgencyDto, idLastUpdatePerson);
		}
	}

	/**
	 * 
	 * Method Name: verifyAgencyExist Method Description: This method gets
	 * Agency id
	 * 
	 * @param toDoDesc
	 * @param idEvent
	 * @param idUser
	 */
	@Override
	public void endDateToDo(Long idEvent, String toDoDesc, Long idUser) {
		Criteria criteria = null;
		if (!ObjectUtils.isEmpty(idUser)) {
			criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
			criteria.add(Restrictions.eq("event.idEvent", idEvent));
			criteria.add(Restrictions.eq("txtTodoDesc", toDoDesc));
			criteria.add(Restrictions.eq("cdTodoType", ServiceConstants.TODO_ACTIONS_TASK));
			criteria.add(Restrictions.isNull("dtTodoCompleted"));
			criteria.add(Restrictions.eq("personByIdTodoPersAssigned.idPerson", idUser));

		} else {
			criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
			criteria.add(Restrictions.eq("event.idEvent", idEvent));
			criteria.add(Restrictions.eq("txtTodoDesc", toDoDesc));
			criteria.add(Restrictions.eq("cdTodoTask", ServiceConstants.TODO_ACTIONS_TASK));
			criteria.add(Restrictions.isNull("dtTodoCompleted"));

		}

		List<Todo> todoList = criteria.setResultTransformer(Transformers.aliasToBean(Todo.class)).list();

		todoList.forEach(todo -> {
			todo.setDtTodoCompleted(new Date());
			todo.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(todo);
		});
	}

	/**
	 * 
	 * Method Name: verifyAgencyExist Method Description: This method gets
	 * Agency id
	 * 
	 * @param icpcAgencyDto
	 * @param cdAgencyType
	 * @return Long
	 */
	@Override
	public Long verifyAgencyExist(ICPCAgencyDto icpcAgencyDto, Long cdAgencyType) {
		StringBuffer sql = new StringBuffer(verifyAgencyExist);

		// Create Bind Variables Vector.
		String agencyOther = icpcAgencyDto.getAgencyOther();
		String addrStLn1 = !StringUtils.isEmpty(icpcAgencyDto.getAddrStLn1()) ? icpcAgencyDto.getAddrStLn1() : null;
		String addrStLn2 = !StringUtils.isEmpty(icpcAgencyDto.getAddrStLn2()) ? icpcAgencyDto.getAddrStLn1() : null;
		String addrCity = !StringUtils.isEmpty(icpcAgencyDto.getAddrCity()) ? icpcAgencyDto.getAddrStLn1() : null;
		String cdState = !StringUtils.isEmpty(icpcAgencyDto.getCdState()) ? icpcAgencyDto.getAddrStLn1() : null;
		String addrZip = !StringUtils.isEmpty(icpcAgencyDto.getAddrZip()) ? icpcAgencyDto.getAddrStLn1() : null;

		Long idEntity = (Long) sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
				.addScalar("idEntity", StandardBasicTypes.LONG).setParameter("agencyOther", agencyOther)
				.setParameter("cdAgencyType", cdAgencyType).setParameter("addrStLn1", addrStLn1)
				.setParameter("addrStLn2", addrStLn2).setParameter("addrCity", addrCity)
				.setParameter("cdState", cdState).setParameter("addrZip", addrZip).uniqueResult();

		return idEntity;

	}

	/**
	 * Method getValidPlacementRequests Method Description: This method
	 * generates the list for PEND and APRV Status which has a Placement 100-A
	 * Record
	 * 
	 * @param icpcPlacementReq
	 * @return List<EventValueDto>
	 */
	@Override
	public List<EventValueDto> getValidPlacementRequests(ICPCPlacementReq icpcPlacementReq) {
		List<EventValueDto> eventBeanList = null;

		eventBeanList = (ArrayList<EventValueDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getICPCportalRequestStage).addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("cdRequestType", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("cdEventType", StandardBasicTypes.STRING).addScalar("eventDescr", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("nmStagePersonFull", StandardBasicTypes.STRING)
				.addScalar("nmCreatorsFirst", StandardBasicTypes.STRING)
				.addScalar("nmCreatorsLast", StandardBasicTypes.STRING)
				.setParameter("idStage", icpcPlacementReq.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(EventValueDto.class)).list();

		return eventBeanList;
	}

	/**
	 * Method getIcpcLegacyNumber Method Description: This method generates the
	 * Legacy number for the given record which doesn't have a list in Placement
	 * 100-A
	 * 
	 * @param idStage
	 * @return String
	 */

	@Override
	public String getIcpcLegacyNumber(Long idStage) {

		String icpcLegacyNumber = null;

		icpcLegacyNumber = (String) sessionFactory.getCurrentSession().createSQLQuery(getICPCLegacyNumber)
				.addScalar("icpcLegacyNumber", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.uniqueResult();

		return icpcLegacyNumber;

	}

	/**
	 * Method Name: getIcpcDocument Method Description: get the document details
	 * for the given idICPCDocuments from ICPC_DOCUMENT,ICPC_FILE_STORAGE
	 * 
	 * @param idICPCDocuments
	 * @return List<ICPCDocumentDto>
	 */
	public List<ICPCDocumentDto> getIcpcDocument(List<Long> idICPCDocuments) {

		List<ICPCDocumentDto> docValueList = null;
		// Retrieve File
		Query queryDocument = sessionFactory.getCurrentSession()
				.createSQLQuery(getIcpcDocument)
				.addScalar("idICPCDocument", StandardBasicTypes.LONG)
				.addScalar("doc", StandardBasicTypes.BINARY)
				.addScalar("sysMimeType", StandardBasicTypes.STRING)
				.addScalar("fileName", StandardBasicTypes.STRING)
				.addScalar("fileSize", StandardBasicTypes.BIG_DECIMAL)
				.setParameterList("idIcpcDocuments", idICPCDocuments)
				.setResultTransformer(Transformers.aliasToBean(ICPCDocumentDto.class));

		docValueList = (List<ICPCDocumentDto>) queryDocument.list();
		if (CollectionUtils.isEmpty(docValueList)) {
			throw new DataNotFoundException(messageSource.getMessage("personHomeRetrieve.input.data", null, Locale.US));
		}

		return docValueList;

	}
	/**
	 * 
	 *Method Name:	getIcpcPrimaryChild
	 *Method Description:This method is used to get the primary child for the icpc request
	 *@param idICPCRequest
	 *@return
	 */
	public Long getIcpcPrimaryChild(Long idICPCRequest) {
		Long idPrimary = null;
		// Retrieve File
		idPrimary = (Long) sessionFactory.getCurrentSession().createSQLQuery(getIcpcPrimaryChild)
				.addScalar("idPrimary", StandardBasicTypes.LONG)
				.setParameter("idICPCRequest", idICPCRequest).uniqueResult();
		return idPrimary;

	}

	/**
	 * Method Name:	getTransmissionLst
	 * Method Description: This method is used to retrieve the matched pending Home Study Request else all
	 * the pending Home Study Request
	 *
	 * @param idStage
	 * @return List<ICPCTransmissionDto>
	 */
	@Override
	public List<ICPCTransmissionDto> getTransmissionLst(Long idStage) {

		List<ICPCTransmissionDto> transmissionlst = null;

		Query queryDocument = sessionFactory.getCurrentSession().createSQLQuery(getTransmissionLst)
				.addScalar("idIcpcTransmittal", StandardBasicTypes.LONG)
				.addScalar("dtDocumentCreate", StandardBasicTypes.DATE)
				.addScalar("cdPlcmtCategory", StandardBasicTypes.STRING)
				.addScalar("entityName", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(ICPCTransmissionDto.class));

		transmissionlst = (List<ICPCTransmissionDto>) queryDocument.list();

		if (CollectionUtils.isEmpty(transmissionlst)) {
			queryDocument = sessionFactory.getCurrentSession().createSQLQuery(getAllPendingTransmissionLst)
					.addScalar("idIcpcTransmittal", StandardBasicTypes.LONG)
					.addScalar("dtDocumentCreate", StandardBasicTypes.DATE)
					.addScalar("cdPlcmtCategory", StandardBasicTypes.STRING)
					.addScalar("entityName", StandardBasicTypes.STRING)
					.setResultTransformer(Transformers.aliasToBean(ICPCTransmissionDto.class));
			transmissionlst = (List<ICPCTransmissionDto>) queryDocument.list();
		}

		if (!CollectionUtils.isEmpty(transmissionlst)) {

			List<Long> idIcpcTransmittals = transmissionlst.stream()
					.map(ICPCTransmissionDto::getIdIcpcTransmittal)
					.collect(Collectors.toList());

			List<ICPCTransmissionDto> personTransmissionDtos = (List<ICPCTransmissionDto>) sessionFactory.getCurrentSession()
					.createSQLQuery(fetchChildDetailsFromStagingSql)
					.addScalar("idIcpcTransmittal", StandardBasicTypes.LONG)
					.addScalar("idNeiceTransmittalPerson", StandardBasicTypes.LONG)
					.addScalar("nmPersonFull", StandardBasicTypes.STRING)
					.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
					.setParameterList("idNeiceTransmittals", idIcpcTransmittals)
					.setResultTransformer(Transformers.aliasToBean(ICPCTransmissionDto.class))
					.list();

			if (!CollectionUtils.isEmpty(personTransmissionDtos)) {

				Map<Long, List<ICPCTransmissionDto>> personTransmissionDtoMap = personTransmissionDtos.stream()
						.collect(Collectors.groupingBy(ICPCTransmissionDto::getIdIcpcTransmittal));

				transmissionlst.forEach(dto -> {
					List<ICPCTransmissionDto> personTransmissionList = Optional.ofNullable(
									personTransmissionDtoMap.get(dto.getIdIcpcTransmittal()))
							.orElseGet(ArrayList::new);

					dto.setOldestChildNm(personTransmissionList
							.stream()
							.min(Comparator.comparing(ICPCTransmissionDto::getIdNeiceTransmittalPerson))
							.map(ICPCTransmissionDto::getNmPersonFull)
							.orElse(!CollectionUtils.isEmpty(personTransmissionList) ? personTransmissionList.get(0)
									.getNmPersonFull() : null));
				});
			}
		}

		return transmissionlst;
	}

	@Override
	public List<ICPCTransmissionDto> getTransmissionChildLst(Long idIcpcTransmittal) {
		List<ICPCTransmissionDto> transmissionlst = null;

		Query queryDocument = sessionFactory.getCurrentSession().createSQLQuery(getTransmissionChildLst)
				.addScalar("idNeiceTransmittalPerson", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("idPersonNeice", StandardBasicTypes.STRING)
				.setParameter("idIcpcTransmittal", idIcpcTransmittal)
				.setResultTransformer(Transformers.aliasToBean(ICPCTransmissionDto.class));

		transmissionlst = (List<ICPCTransmissionDto>) queryDocument.list();
		if (CollectionUtils.isEmpty(transmissionlst)) {
			throw new DataNotFoundException(messageSource.getMessage("transmissionlst.noRecordsFound", null, Locale.US));
		}

		return transmissionlst;
	}

	/**
	 * @param idIcpcTransmittal
	 * @param idPersonNeice
	 * @return
	 */
	@Override
	public List<ICPCTransmissionDto> getTransmissionAttachemnts(Long idIcpcTransmittal, Long idPersonNeice) {
		List<ICPCTransmissionDto> transmissionAttchlst = null;

		Query queryDocument = sessionFactory.getCurrentSession().createSQLQuery(getTransmissionAttachments)
				.addScalar("idIcpcTransmittal", StandardBasicTypes.LONG)
				.addScalar("idNeiceAttachment", StandardBasicTypes.LONG)
				.addScalar("nmAttachment", StandardBasicTypes.STRING)
				.addScalar("cdDocType", StandardBasicTypes.STRING)
				.addScalar("idPersonNeice", StandardBasicTypes.STRING)
				.addScalar("attchmntFormat", StandardBasicTypes.STRING)
				.addScalar("documentData", StandardBasicTypes.BINARY)
				.addScalar("documentDescription", StandardBasicTypes.STRING)
				.setParameter("idIcpcTransmittal", idIcpcTransmittal)
				.setParameter("idPersonNeice", idPersonNeice)
				.setResultTransformer(Transformers.aliasToBean(ICPCTransmissionDto.class));

		transmissionAttchlst = (List<ICPCTransmissionDto>) queryDocument.list();
		if (CollectionUtils.isEmpty(transmissionAttchlst)) {
			throw new DataNotFoundException(messageSource.getMessage("transmissionlst.noRecordsFound", null, Locale.US));
		} else {

			transmissionAttchlst.forEach(dto -> {
				if (!ObjectUtils.isEmpty(dto.getDocumentData())) {
					dto.setDocumentData(Base64.getDecoder()
							.decode(dto.getDocumentData()));
				}
			});

		}

		return transmissionAttchlst;
	}

	/**
	 * @param idAttachment
	 * @return
	 */
	@Override
	public ICPCTransmissionDto getTransmissionAttachemnt(Long idAttachment) {

		ICPCTransmissionDto transmissionAttch = null;

		Query queryDocument = sessionFactory.getCurrentSession()
				.createSQLQuery(getTransmissionAttachment)
				.addScalar("idNeiceAttachment", StandardBasicTypes.LONG)
				.addScalar("nmAttachment", StandardBasicTypes.STRING)
				.addScalar("cdDocType", StandardBasicTypes.STRING)
				.addScalar("attchmntFormat", StandardBasicTypes.STRING)
				.addScalar("documentData", StandardBasicTypes.BINARY)
				.addScalar("documentDescription", StandardBasicTypes.STRING)
				.setParameter("idAttachment", idAttachment)
				.setResultTransformer(Transformers.aliasToBean(ICPCTransmissionDto.class));

		transmissionAttch = (ICPCTransmissionDto) queryDocument.list()
				.stream()
				.findFirst()
				.orElse(null);

		if (ObjectUtils.isEmpty(transmissionAttch)) {
			throw new DataNotFoundException(
					messageSource.getMessage("transmissionlst.noRecordsFound", null, Locale.US));
		} else {

			if (!ObjectUtils.isEmpty(transmissionAttch.getDocumentData())) {
				transmissionAttch.setDocumentData(Base64.getDecoder()
						.decode(transmissionAttch.getDocumentData()));
			}
		}

		return transmissionAttch;
	}

	/**
	 * @param inputStream
	 * @return
	 */
	private byte[] getBytes(InputStream inputStream) {
		BufferedInputStream buffInputStream = new BufferedInputStream(inputStream);
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		try {
			byte[] b = new byte[2048];
			for (int n = buffInputStream.read(b); n > 0; n = buffInputStream.read(b)) {
				byteOutputStream.write(b, 0, n);
			}
		} catch (Exception e) {
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		return byteOutputStream.toByteArray();
	}

	/**
	 * 
	 * Method Name: getCareType Method Description:
	 * 
	 * @param idICPCPlacementRequest
	 * @return
	 */
	@Override
	public String getCareType(Long idICPCPlacementRequest) {
		IcpcPlacementRequest icpcPlacementRequest = (IcpcPlacementRequest) sessionFactory.getCurrentSession()
				.createCriteria(IcpcPlacementRequest.class)
				.add(Restrictions.eq("idIcpcPlacementRequest", idICPCPlacementRequest)).uniqueResult();

		return icpcPlacementRequest.getCdCareType();
	}

	@Override
	public Long insertPlacementStatus(ICPCPlacementStatusDto icpcPlacementStatusDto) {
		IcpcPlacementStatus icpcPlacementStatus = new IcpcPlacementStatus();
		icpcPlacementStatus.setIdIcpcRequest(icpcPlacementStatusDto.getIdICPCRequest());
		IcpcPlacementRequest icpcPlacementRequest = new IcpcPlacementRequest();
		icpcPlacementRequest.setIdIcpcPlacementRequest(icpcPlacementStatusDto.getIdICPCPlacementRequest());
		icpcPlacementStatus.setIcpcPlacementRequest(icpcPlacementRequest);
		icpcPlacementStatus.setCdCompactTermRsn(icpcPlacementStatusDto.getCdCompactTermRsn());

		icpcPlacementStatus.setCdPlacementStatus(icpcPlacementStatusDto.getCdPlacementStatus());

		if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getDtPlacement()))
			icpcPlacementStatus.setDtPlacement(icpcPlacementStatusDto.getDtPlacement());

		if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getDtPlacementWithdrawn())
				&& !ObjectUtils.isEmpty((icpcPlacementStatusDto.getDtPlacementWithdrawn())))
			icpcPlacementStatus.setDtPlacementWithdrawn(icpcPlacementStatusDto.getDtPlacementWithdrawn());

		icpcPlacementStatus.setTxtGuardianRelation(icpcPlacementStatusDto.getTxtRelationGuardian());
		icpcPlacementStatus.setDtPlacementWithdrawn(icpcPlacementStatusDto.getDtPlacementWithdrawn());
		icpcPlacementStatus.setTxtReasonOther(icpcPlacementStatusDto.getTxtReasonOther());
		if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getDtPlacementTerm()))
			icpcPlacementStatus.setDtPlcmntTerm(icpcPlacementStatusDto.getDtPlacementTerm());
		icpcPlacementStatus.setTxtTerminationNotes(icpcPlacementStatusDto.getTxtNotes());
		icpcPlacementStatus.setIdCreatedPerson(icpcPlacementStatusDto.getIdCreatedPerson());
		icpcPlacementStatus.setDtCreated(new Date());
		icpcPlacementStatus.setIdLastUpdatePerson(icpcPlacementStatusDto.getIdLastUpdatePerson());
		icpcPlacementStatus.setDtLastUpdate(new Date());
		Long idICPCPlacementStatus = (Long) sessionFactory.getCurrentSession().save(icpcPlacementStatus);
		return idICPCPlacementStatus;
	}

	@Override
	public Long getEventFromRequest(Long idICPCPlacementRequest) {
		return (Long) sessionFactory.getCurrentSession().createSQLQuery(getEventFromRequestSql)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameter("idICPCPlacementRequest", idICPCPlacementRequest).list().stream().findAny().orElse(null);
	}

	/**
	 * 
	 * Method Name: updateICPCPlacementRequest Method Description: This method
	 * inserts record into ICPC_PLACEMENT_REQUEST table.
	 * 
	 * @param icpcPlacementRequestDto
	 * @return Long
	 */
	@Override
	public void updateICPCPlacementRequest(ICPCPlacementRequestDto icpcPlacementRequestDto) {
		IcpcPlacementRequest icpcPlacementRequest = (IcpcPlacementRequest) sessionFactory.getCurrentSession()
				.get(IcpcPlacementRequest.class, icpcPlacementRequestDto.getIdICPCPlacementRequest());

		icpcPlacementRequest.setCdLegalStatus(icpcPlacementRequestDto.getCdLegalStatus());
		icpcPlacementRequest.setTxtLegalStatus(icpcPlacementRequestDto.getTxtLegalStatus());
		icpcPlacementRequest.setCdCareType(icpcPlacementRequestDto.getCdCareType());
		icpcPlacementRequest.setTxtxCareType(icpcPlacementRequestDto.getTxtCareType());
		icpcPlacementRequest.setCdSubsidy(icpcPlacementRequestDto.getCdSubsidy());
		icpcPlacementRequest.setIndPubPrivPlcmt(icpcPlacementRequestDto.getPubPrivBtn());
		icpcPlacementRequest.setDtHomeStudyDue(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getHomeStudyDt())
				? icpcPlacementRequestDto.getHomeStudyDt() : null);
		icpcPlacementRequest.setTxtSprvsrySrvcs(icpcPlacementRequestDto.getTxtSupSrvs());
		icpcPlacementRequest.setTxtSprvsryRprts(icpcPlacementRequestDto.getTxtSupRprts());
		icpcPlacementRequest.setCdReasonDenial(icpcPlacementRequestDto.getCdDenialReason());
		icpcPlacementRequest.setTxtReasonDenial(icpcPlacementRequestDto.getTxtDenialReason());
		icpcPlacementRequest.setCdInitialReport(icpcPlacementRequestDto.getCdInitialReport());
		icpcPlacementRequest.setCdSprvsrySrvcs(icpcPlacementRequestDto.getCdSprrvsrySrvcs());
		icpcPlacementRequest.setCdSprvsryRprts(icpcPlacementRequestDto.getCdSprvsryRprts());
		icpcPlacementRequest.setCdDecision(icpcPlacementRequestDto.getCdDecision());
		icpcPlacementRequest.setCdViolation(icpcPlacementRequestDto.getCdViolation());
		icpcPlacementRequest.setCdWithdrawalReason(icpcPlacementRequestDto.getCdWithdrawalReason());
		icpcPlacementRequest.setDtReceived(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getDtReceived())
				? icpcPlacementRequestDto.getDtReceived() : null);
		icpcPlacementRequest.setDtHomeAssessSent(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getDtHomeAssessSent())
				? icpcPlacementRequestDto.getDtHomeAssessSent() : null);
		icpcPlacementRequest.setDtHomeAssessRcvd(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getDtHomeAssessRcvd())
				? icpcPlacementRequestDto.getDtHomeAssessRcvd() : null);
		icpcPlacementRequest.setDtDecision(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getDtDecision())
				? icpcPlacementRequestDto.getDtDecision() : null);
		icpcPlacementRequest.setIndPriority(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIndPriority())
				? icpcPlacementRequestDto.getIndPriority().charAt(0) : null);
		icpcPlacementRequest.setIndIcwaEligible(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIndICWAEligible())
				? icpcPlacementRequestDto.getIndICWAEligible().charAt(0) : null);
		icpcPlacementRequest.setIndTitleIve(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIndTitleIVE())
				? icpcPlacementRequestDto.getIndTitleIVE().charAt(0) : null);
		icpcPlacementRequest.setIndNaturalDisaster(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIndNaturalDisaster())
				? icpcPlacementRequestDto.getIndNaturalDisaster().charAt(0) : null);
		icpcPlacementRequest.setTxtNaturalDisaster(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getTxtNaturalDisaster())
				? icpcPlacementRequestDto.getTxtNaturalDisaster() : null);
		icpcPlacementRequest.setTxtPlacementRemarks(icpcPlacementRequestDto.getTxtPlacementRemarks());
		icpcPlacementRequest.setTxtNotes(icpcPlacementRequestDto.getTxtNotes());
		icpcPlacementRequest.setIdLastUpdatePerson(icpcPlacementRequestDto.getIdLastUpdatePerson());


		sessionFactory.getCurrentSession().saveOrUpdate(icpcPlacementRequest);
	}

	/**
	 * 
	 * Method Name: deleteICPCRequestEnclosure Method Description: This method
	 * delete record from ICPC_REQUEST_ENCLOSURE table.
	 * 
	 * @param idEvent
	 */
	@Override
	public void deleteICPCRequestEnclosure(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IcpcRequestEnclosure.class);
		criteria.add(Restrictions.eq("event.idEvent", idEvent));
		List<IcpcRequestEnclosure> icpcRequestEnclosureList = (List<IcpcRequestEnclosure>) criteria.list();
		if (!CollectionUtils.isEmpty(icpcRequestEnclosureList)) {
			icpcRequestEnclosureList.forEach(icpcRequestEnclosure -> {
				sessionFactory.getCurrentSession().delete(icpcRequestEnclosure);
			});
		}
	}

	/**
	 * 
	 * Method Name: updateICPCRequestPersonLink Method Description: This method
	 * update a record from ICPC_REQUEST_PERSON_LINK table.
	 * 
	 * @param idPerson
	 * @param icpcPersonDto
	 */
	@Override
	public void updateICPCRequestPersonLink(ICPCPersonDto icpcPersonDto, Long idPerson) {

		IcpcRequestPersonLink icpcRequestPersonLink = new IcpcRequestPersonLink();
		if (!ObjectUtils.isEmpty(icpcPersonDto.getIdICPCRequestPersonLink())) {
			icpcRequestPersonLink = (IcpcRequestPersonLink) sessionFactory.getCurrentSession()
					.get(IcpcRequestPersonLink.class, icpcPersonDto.getIdICPCRequestPersonLink());
		}
		if (!ObjectUtils.isEmpty(icpcPersonDto.getIdPerson())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, icpcPersonDto.getIdPerson());
			icpcRequestPersonLink.setPerson(person);
		}
		icpcRequestPersonLink.setIdLastUpdatePerson(idPerson);
		icpcRequestPersonLink.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(icpcRequestPersonLink);

	}

	/**
	 *
	 * Method Name: updateICPCRequestPersonLink Method Description: This method
	 * update a record from ICPC_REQUEST_PERSON_LINK table.
	 *
	 * @param idPerson
	 * @param icpcPersonDto
	 * @param cdPlacementCode
	 */
	@Override
	public void updateICPCRequestPersonLink(ICPCPersonDto icpcPersonDto, Long idPerson, String cdPlacementCode) {

		IcpcRequestPersonLink icpcRequestPersonLink = new IcpcRequestPersonLink();
		if (!ObjectUtils.isEmpty(icpcPersonDto.getIdICPCRequestPersonLink())) {
			icpcRequestPersonLink = (IcpcRequestPersonLink) sessionFactory.getCurrentSession()
					.get(IcpcRequestPersonLink.class, icpcPersonDto.getIdICPCRequestPersonLink());
		}
		if (!ObjectUtils.isEmpty(icpcPersonDto.getIdPerson())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, icpcPersonDto.getIdPerson());
			icpcRequestPersonLink.setPerson(person);
		}
		icpcRequestPersonLink.setCdPlacementCode(cdPlacementCode);
		icpcRequestPersonLink.setIdLastUpdatePerson(idPerson);
		icpcRequestPersonLink.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(icpcRequestPersonLink);

	}

	/**
	 * 
	 * Method Name: deleteIcpcRequestPersonLink Method Description: This method
	 * delete a record from ICPC_REQUEST_PERSON_LINK table.
	 * 
	 * @param idIcpcRequest
	 * @param cdPersonType
	 */
	@Override
	public void deleteICPCRequestPersonLink(Long idIcpcRequest, String cdPersonType) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IcpcRequestPersonLink.class);
		criteria.add(Restrictions.eq("cdPersonType", cdPersonType));
		criteria.add(Restrictions.eq("icpcRequest.idIcpcRequest", idIcpcRequest));
		IcpcRequestPersonLink icpcRequestPersonLink = (IcpcRequestPersonLink) criteria.list().stream().findAny()
				.orElse(null);
		if (!ObjectUtils.isEmpty(icpcRequestPersonLink)) {
			sessionFactory.getCurrentSession().delete(icpcRequestPersonLink);
		}
	}

	/**
	 *
	 * Method Name: deleteIcpcRequestPersonLink Method Description: This method
	 * delete a record from ICPC_REQUEST_PERSON_LINK table.
	 *
	 * @param idIcpcRequest
	 * @param cdPersonType
	 * @param idPerson
	 */
	@Override
	public void deleteICPCRequestPersonLinkByPerson(Long idIcpcRequest, String cdPersonType, Long idPerson) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IcpcRequestPersonLink.class);
		criteria.add(Restrictions.eq("cdPersonType", cdPersonType));
		criteria.add(Restrictions.eq("icpcRequest.idIcpcRequest", idIcpcRequest));
		criteria.add(Restrictions.eq("person.idPerson", idPerson));
		IcpcRequestPersonLink icpcRequestPersonLink = (IcpcRequestPersonLink) criteria.list().stream().findAny()
				.orElse(null);
		if (!ObjectUtils.isEmpty(icpcRequestPersonLink)) {
			sessionFactory.getCurrentSession().delete(icpcRequestPersonLink);
		}
	}

	/**
	 * 
	 * Method Name: updateICPCRequestResourceLink Method Description: This
	 * method update a record from ICPC_REQUEST_RESOURCE_LINK table.
	 * 
	 * @param icpcResourceDto
	 * @param idLastUpdatePerson
	 *
	 */
	@Override
	public void updateICPCRequestResourceLink(ICPCResourceDto icpcResourceDto, Long idLastUpdatePerson) {
		IcpcRequestResourceLink icpcRequestResourceLink = new IcpcRequestResourceLink();
		if (!ObjectUtils.isEmpty(icpcResourceDto.getIdICPCRequestResourceLink())) {
			icpcRequestResourceLink = (IcpcRequestResourceLink) sessionFactory.getCurrentSession()
					.get(IcpcRequestResourceLink.class, icpcResourceDto.getIdICPCRequestResourceLink());
		}
		if (!ObjectUtils.isEmpty(icpcResourceDto.getIdICPCRequest())) {
			IcpcRequest icpcRequest = (IcpcRequest) sessionFactory.getCurrentSession().get(IcpcRequest.class,
					icpcResourceDto.getIdICPCRequest());
			icpcRequestResourceLink.setIcpcRequest(icpcRequest);
		}
		CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
				icpcResourceDto.getIdResource());
		if (!ObjectUtils.isEmpty(capsResource))
			icpcRequestResourceLink.setCapsResource(capsResource);
		icpcRequestResourceLink.setIdLastUpdatePerson(idLastUpdatePerson);
		icpcRequestResourceLink.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(icpcRequestResourceLink);

	}

	/**
	 *
	 * Method Name: deleteICPCRequestResourceLink Method Description: This
	 * method delete a record from ICPC_REQUEST_RESOURCE_LINK table.
	 *
	 * @param idIcpcRequest
	 *
	 */
	@Override
	public boolean deleteICPCRequestResourceLinkById(Long idIcpcRequest) {
		boolean recordsDeleted = false;
		Criteria criteria = sessionFactory.getCurrentSession()
				.createCriteria(IcpcRequestResourceLink.class);
		criteria.add(Restrictions.eq("icpcRequest.idIcpcRequest", idIcpcRequest));
		List<IcpcRequestResourceLink> icpcRequestResourceLinks = (List<IcpcRequestResourceLink>) criteria.list();
		if (!CollectionUtils.isEmpty(icpcRequestResourceLinks)) {
			icpcRequestResourceLinks.forEach(link -> sessionFactory.getCurrentSession()
					.delete(link));
			recordsDeleted = true;
		}
		return recordsDeleted;
	}

	/**
	 * 
	 * Method Name: deleteICPCRequestResourceLink Method Description: This
	 * method delete a record from ICPC_REQUEST_RESOURCE_LINK table.
	 * 
	 * @param idIcpcRequestResourceLink
	 *
	 */
	@Override
	public void deleteICPCRequestResourceLink(Long idIcpcRequestResourceLink) {
		IcpcRequestResourceLink icpcRequestResourceLink = (IcpcRequestResourceLink) sessionFactory.getCurrentSession()
				.get(IcpcRequestResourceLink.class, idIcpcRequestResourceLink);
		sessionFactory.getCurrentSession().delete(icpcRequestResourceLink);
	}

	/**
	 * 
	 * Method Name: updateICPCRequest Method Description: This method update a
	 * record from ICPC_REQUEST table.
	 * 
	 * @param icpcPlacementRequestDto
	 *
	 */
	@Override
	public void updateICPCRequest(ICPCPlacementRequestDto icpcPlacementRequestDto) {
		IcpcRequest icpcRequest = (IcpcRequest) sessionFactory.getCurrentSession().get(IcpcRequest.class,
				icpcPlacementRequestDto.getIdICPCRequest());

		icpcRequest.setCdSendingState(icpcPlacementRequestDto.getCdSendingState());
		icpcRequest.setCdReceivingState(icpcPlacementRequestDto.getCdReceivingState());
		icpcRequest.setIdSendingStateAgency(icpcPlacementRequestDto.getCdSendingAgency());
		icpcRequest.setIdReceivingStateAgency(icpcPlacementRequestDto.getCdReceivingAgency());
		icpcRequest.setIdLastUpdatePerson(icpcPlacementRequestDto.getIdCreatedPerson());
		icpcRequest.setDtLastUpdate(new Date());

		sessionFactory.getCurrentSession().saveOrUpdate(icpcRequest);
	}

	/**
	 * Method Name: updateICPCPlacementRequestEntity Method Description: this
	 * method update a record from ICPC_PLACEMENT_REQUEST_ENTITY table (Agency
	 * Financially Responsible)
	 * 
	 * @param idIcpcPlcmntRqstEntity
	 * @param idEntity
	 * @param IdLastUpdatePerson
	 */
	@Override
	public void updateICPCPlacementRequestEntity(Long idIcpcPlcmntRqstEntity, Long idEntity, Long IdLastUpdatePerson) {

		IcpcPlacementRequestEntity icpcPlacementRequestEntity = (IcpcPlacementRequestEntity) sessionFactory
				.getCurrentSession().get(IcpcPlacementRequestEntity.class, idIcpcPlcmntRqstEntity);

		IcpcEntity icpcEntity = new IcpcEntity();
		icpcEntity.setIdicpcEntity(idEntity);
		
		icpcPlacementRequestEntity.setIcpcEntity(icpcEntity);

		icpcPlacementRequestEntity.setIdLastUpdatePerson(IdLastUpdatePerson);
		icpcPlacementRequestEntity.setIdIcpcPlacementRqstEntity(idIcpcPlcmntRqstEntity);
		sessionFactory.getCurrentSession().saveOrUpdate(icpcPlacementRequestEntity);

		sessionFactory.getCurrentSession().flush();
	}

	/**
	 * Method Name: deleteICPCPlacementRequestEntity Method Description: this
	 * method delete a record from ICPC_PLACEMENT_REQUEST_ENTITY table (Agency
	 * Financially Responsible)
	 * 
	 * @param idIcpcPlcmntRqstEntity
	 * @param cdAgencyType
	 */
	@Override
	public void deleteICPCPlacementRequestEntity(Long idIcpcPlacementRequest, String cdAgencyType) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IcpcPlacementRequestEntity.class);
		criteria.add(Restrictions.eq("cdType", cdAgencyType));
		criteria.add(Restrictions.eq("icpcPlacementRequest.idIcpcPlacementRequest", idIcpcPlacementRequest));
		IcpcPlacementRequestEntity icpcPlacementRequestEntity = (IcpcPlacementRequestEntity) criteria.list().stream()
				.findAny().orElse(null);
		if (!ObjectUtils.isEmpty(icpcPlacementRequestEntity)) {

			deleteICPCPlacementRequestEntityOther(icpcPlacementRequestEntity.getIdIcpcPlacementRqstEntity());

			sessionFactory.getCurrentSession()
					.delete(icpcPlacementRequestEntity);
		}
	}

	/**
	 * Method Name: deleteICPCPlacementRequestEntityOther
	 * Method Description: this method delete a record from ICPC_PLCMT_REQ_ENTITY_OTHER table (Agency
	 * Financially Responsible)
	 *
	 * @param idIcpcPlacementRequestEntity
	 */
	@Override
	public void deleteICPCPlacementRequestEntityOther(Long idIcpcPlacementRequestEntity) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IcpcPlcmtReqEntityOther.class);
		criteria.add(Restrictions.eq("idIcpcPlcmtReqEntity", idIcpcPlacementRequestEntity));
		IcpcPlcmtReqEntityOther icpcPlcmtReqEntityOther = (IcpcPlcmtReqEntityOther) criteria.list().stream()
				.findAny().orElse(null);
		if (!ObjectUtils.isEmpty(icpcPlcmtReqEntityOther))
			sessionFactory.getCurrentSession().delete(icpcPlcmtReqEntityOther);
	}

	/**
	 * Method Name: getPrimaryWorkerPhone Method Description: this method to get
	 * Primary Worker Phone
	 * 
	 * @param idPerson
	 * 
	 * @return String
	 */
	public String getPrimaryWorkerPhone(Long idPerson) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonPhone.class)
				.setProjection(Projections.property("nbrPersonPhone"));
		criteria.add(Restrictions.eq("cdPersonPhoneType", ServiceConstants.indPersonPhoneType));
		criteria.add(Restrictions.eq("person.idPerson", idPerson));
		criteria.add(Restrictions.eq("indPersonPhonePrimary", ServiceConstants.STRING_IND_Y));
		criteria.add(
				Restrictions.eq("dtPersonPhoneEnd", DateUtils.stringToDate(ServiceConstants.MAX_JAVA_DATE_STRING)));
		return (String) criteria.uniqueResult();
	}

	/**
	 * 
	 * Method Name: getIcpcDocument Method Description: this method is to update
	 * ICPC placement status report detail page
	 *
	 * @param icpcPlacementStatusDto
	 * @return ICPCDocumentRes
	 */
	@Override
	public void updateIcpcPlacementStatus(ICPCPlacementStatusDto icpcPlacementStatusDto) {
		IcpcPlacementStatus icpcPlacementStatus = (IcpcPlacementStatus) sessionFactory.getCurrentSession()
				.createCriteria(IcpcPlacementStatus.class)
				.add(Restrictions.eq("idIcpcPlacementStatus", icpcPlacementStatusDto.getIdICPCPlacementStatus()))
				.uniqueResult();
		icpcPlacementStatus.setIdIcpcRequest(icpcPlacementStatusDto.getIdICPCRequest());

		icpcPlacementStatus.setCdPlacementStatus(icpcPlacementStatusDto.getCdPlacementStatus());

		if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getDtPlacement()))
			icpcPlacementStatus.setDtPlacement(icpcPlacementStatusDto.getDtPlacement());

		if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getDtPlacementWithdrawn())
				&& !ObjectUtils.isEmpty((icpcPlacementStatusDto.getDtPlacementWithdrawn())))
			icpcPlacementStatus.setDtPlacementWithdrawn(icpcPlacementStatusDto.getDtPlacementWithdrawn());
		icpcPlacementStatus.setCdCompactTermRsn(icpcPlacementStatusDto.getCdCompactTermRsn());
		icpcPlacementStatus.setTxtGuardianRelation(icpcPlacementStatusDto.getTxtRelationGuardian());
		icpcPlacementStatus.setDtPlacementWithdrawn(icpcPlacementStatusDto.getDtPlacementWithdrawn());
		icpcPlacementStatus.setTxtReasonOther(icpcPlacementStatusDto.getTxtReasonOther());
		if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getDtPlacementTerm()))
			icpcPlacementStatus.setDtPlcmntTerm(icpcPlacementStatusDto.getDtPlacementTerm());
		icpcPlacementStatus.setTxtTerminationNotes(icpcPlacementStatusDto.getTxtNotes());

		icpcPlacementStatus.setIdLastUpdatePerson(icpcPlacementStatusDto.getIdLastUpdatePerson());
		icpcPlacementStatus.setDtLastUpdate(new Date());

		sessionFactory.getCurrentSession().saveOrUpdate(icpcPlacementStatus);

	}

	/**
	 * Method Name: updateEvent MethoUpdate event d Description: this method
	 * updates the event table
	 * 
	 * @param idEvent
	 * @param eventValueBean
	 */
	@Override
	public void updateEvent(Long idEvent, EventValueDto eventValueBean) {
		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, idEvent);
		event.setTxtEventDescr(eventValueBean.getEventDescr());
		event.setCdEventStatus(eventValueBean.getEventStatusCode());
		sessionFactory.getCurrentSession().update(event);
	}

	/**
	 * Method Name:checkCorresponding100BPresent Method Description: this method
	 * check if 100B has been created
	 * 
	 * @param idICPCPlcmntRequest
	 * @return boolean
	 */
	@Override
	public boolean checkCorresponding100BPresent(Long idICPCPlcmntRequest) {
		IcpcPlacementStatus icpcPlacementStatus = (IcpcPlacementStatus) sessionFactory.getCurrentSession()
				.get(IcpcPlacementStatus.class, idICPCPlcmntRequest);
		return !ObjectUtils.isEmpty(icpcPlacementStatus) ? true : false;
	}

	/**
	 * 
	 * Method Name: insertIcpcPlcmntStatPerAddr Method Description:
	 * 
	 * @param idICPCPlacementStatus
	 * @param idPrsnAddress
	 * @param icpcadtp20
	 * @param idLastUpdatePerson
	 */
	@Override
	public void insertIcpcPlcmntStatPerAddr(Long idICPCPlacementStatus, Long idPrsnAddress, String cdAddressType,
			Long idLastUpdatePerson) {
		IcpcStatusPersAddrLink entity = new IcpcStatusPersAddrLink();

		IcpcPlacementStatus icpcPlacementStatus = new IcpcPlacementStatus();
		icpcPlacementStatus.setIdIcpcPlacementStatus(idICPCPlacementStatus);
		entity.setIcpcPlacementStatus(icpcPlacementStatus);

		PersonAddress personAddress = new PersonAddress();
		personAddress.setIdPersonAddr(idPrsnAddress);
		entity.setPersonAddress(personAddress);

		entity.setCdAddressType(cdAddressType);
		entity.setIdCreatedPerson(idLastUpdatePerson);
		entity.setDtCreated(new Date());

		entity.setIdLastUpdatePerson(idLastUpdatePerson);
		entity.setDtLastUpdate(new Date());

	}

	/**
	 * 
	 * Method Name: insertIcpcPlcmntStatResAddr Method Description:
	 * 
	 * @param idICPCPlacementStatus
	 * @param idResAddress
	 * @param icpcadtp20
	 * @param idLastUpdatePerson
	 */
	@Override
	public void insertIcpcPlcmntStatResAddr(Long idICPCPlacementStatus, Long idResAddress, String cdAddressType,
			Long idLastUpdatePerson) {
		IcpcStatusRsrcAddrLink entity = new IcpcStatusRsrcAddrLink();

		IcpcPlacementStatus icpcPlacementStatus = new IcpcPlacementStatus();
		icpcPlacementStatus.setIdIcpcPlacementStatus(idICPCPlacementStatus);
		entity.setIcpcPlacementStatus(icpcPlacementStatus);

		entity.setIdResourceAddr(idResAddress);
		entity.setCdAddressType(cdAddressType);
		entity.setIdCreatedPerson(idLastUpdatePerson);
		entity.setIdLastUpdatePerson(idLastUpdatePerson);
		entity.setDtCreated(new Date());
		entity.setDtLastUpdate(new Date());

	}

	/**
	 * 
	 * Method Name: updateIcpcEmailDtlLog Method Description: This method is to
	 * update ICPC placement Email detail Log tables(ICPC_EMAIL_LOG,
	 * ICPC_EMAIL_DOC_LOG) when the email has been sent successfully.
	 *
	 * @param icpcEmailLogReq
	 * @return CommonStringRes
	 */
	public CommonStringRes updateIcpcEmailDtlLog(ICPCEmailLogReq icpcEmailLogReq) {

		CommonStringRes commonStringRes = new CommonStringRes();

		// set values to parent table ICPC_EMAIL_LOG
		IcpcEmailLog icpcEmailLog = new IcpcEmailLog();
		icpcEmailLog.setCdEmailType(icpcEmailLogReq.getCdEmailType());
		icpcEmailLog.setTxtEmailSndr(icpcEmailLogReq.getTxtEmailSndr());
		icpcEmailLog.setTxtEmailRecpnt(icpcEmailLogReq.getTxtEamilRecpnt());
		icpcEmailLog.setTxtEmailSbjt(icpcEmailLogReq.getTxtEmailSbjt());
		icpcEmailLog.setTxtEmailBody(icpcEmailLogReq.getTxtEmailBody());
		icpcEmailLog.setIndEmailBody(icpcEmailLogReq.getIndEmailBody());
		// map parent entity IcpcTransmittal to child entity IcpcEmailLog if
		// exists
		if (!ObjectUtils.isEmpty(icpcEmailLogReq.getIdIcpcTransmittal())
				&& icpcEmailLogReq.getIdIcpcTransmittal() != 0) {
			IcpcTransmittal icpcTransmittal = new IcpcTransmittal();
			icpcTransmittal.setIdIcpcTransmittal(icpcEmailLogReq.getIdIcpcTransmittal());
			icpcEmailLog.setIdIcpcTransmittal(icpcTransmittal);
		}
		if (!ObjectUtils.isEmpty(icpcEmailLogReq.getIdCreatedPerson()) && icpcEmailLogReq.getIdCreatedPerson() != 0) {
			icpcEmailLog.setIdCreatedPerson(icpcEmailLogReq.getIdCreatedPerson());
			icpcEmailLog.setIdLastUpdatePerson(icpcEmailLogReq.getIdCreatedPerson());
		} else {
			icpcEmailLog.setIdCreatedPerson(icpcEmailLogReq.getIdPerson());
			icpcEmailLog.setIdLastUpdatePerson(icpcEmailLogReq.getIdPerson());
		}
		icpcEmailLog.setTxtEmailSndr(icpcEmailLogReq.getTxtEmailSndr());
		icpcEmailLog.setDtCreated(new Date());
		icpcEmailLog.setIdEvent(icpcEmailLogReq.getIdEvent());
		icpcEmailLog.setIdLastUpdatePerson(icpcEmailLogReq.getIdLastupdatePerson());
		icpcEmailLog.setDtLastUpdate(new Date());

		// set values in child table ICPC_EMAIL_DOC_LOG
		IcpcEmailDocLog icpcEmailDocLog;
		if (!CollectionUtils.isEmpty(icpcEmailLogReq.getIcpcEmailDocLogDtos())) {
			for (ICPCEmailDocLogDto icpcEmailDocLogDto : icpcEmailLogReq.getIcpcEmailDocLogDtos()) {
				icpcEmailDocLog = new IcpcEmailDocLog();
				icpcEmailDocLog.setIndUploaded(icpcEmailDocLogDto.getIndUploaded());
				icpcEmailDocLog.setNmFile(icpcEmailDocLogDto.getFileName());
				// map parent entity IcpcDocument to child entity
				// IcpcEmailDocLog if exists
				if (!ObjectUtils.isEmpty(icpcEmailDocLogDto.getIdICPCDocument())
						&& icpcEmailDocLogDto.getIdICPCDocument() != 0) {
					IcpcDocument icpcDocument = new IcpcDocument();
					icpcDocument.setIdIcpcDocument(icpcEmailDocLogDto.getIdICPCDocument());
					icpcEmailDocLog.setIdIcpcDocument(icpcDocument);
				}
				icpcEmailDocLog.setIdCreatedPerson(icpcEmailDocLogDto.getIdCreatedPerson());
				icpcEmailDocLog.setIdLastUpdatePerson(icpcEmailDocLogDto.getIdLastupdatePerson());
				// map parent entity to child entity
				icpcEmailDocLog.setIdIcpcEmailLog(icpcEmailLog);
				icpcEmailLog.getIcpcEmailDocLogs().add(icpcEmailDocLog);
				icpcEmailDocLog.setDtLastUpdate(new Date());

			}
		}

		sessionFactory.getCurrentSession().saveOrUpdate(icpcEmailLog);
		commonStringRes.setCommonRes(ServiceConstants.SAVE_SUCCESS);
		return commonStringRes;
	}

	/**
	 * Method Name: getRecentAprvEvent Method Description:This method is used to
	 * retrieve most recent approved CPS idEvent.
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getRecentAprvEvent(Long idStage) {

		Long idEvent = ServiceConstants.ZERO_VAL;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRecentAprvEvent)
				.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idStage", idStage);
		idEvent = ((Long) query.uniqueResult());
		return idEvent;
	}

	/**
	 * 
	 * Method Name: deleteICPCRequest Method Description: This method delete
	 * record from ICPC_REQUEST,ICPC_EVENT_LINK,ICPC_PLACEMENT_REQUEST,
	 * ICPC_REQUEST_PERSON_LINK,ICPC_REQUEST_RESOURCE_LINK table.
	 * 
	 * @param ICPCPlacementRequestDto
	 */
	@Override
	public void deleteICPCRequest(ICPCPlacementRequestDto icpcPlacementRequestDto) {

		// Delete ICPC_REQUEST_ENCLOSURE Table
		deleteICPCRequestEnclosure(icpcPlacementRequestDto.getIdEvent());

		// Delete ICPC_REQUEST_RESOURCE_LINK
		Criteria fetchIcpcRequestResourceLink = sessionFactory.getCurrentSession()
				.createCriteria(IcpcRequestResourceLink.class);
		fetchIcpcRequestResourceLink
				.add(Restrictions.eq("icpcRequest.idIcpcRequest", icpcPlacementRequestDto.getIdICPCRequest()));
		List<IcpcRequestResourceLink> icpcRequestResourceLinkList = (List<IcpcRequestResourceLink>) fetchIcpcRequestResourceLink
				.list();
		if (!CollectionUtils.isEmpty(icpcRequestResourceLinkList)) {
			icpcRequestResourceLinkList.forEach(icpcRequestResourceLink -> {
				sessionFactory.getCurrentSession().delete(icpcRequestResourceLink);
			});

		}

		// Delete ICPC_REQUEST_PERSON_LINK
		Criteria fetchIcpcRequestPersonLink = sessionFactory.getCurrentSession()
				.createCriteria(IcpcRequestPersonLink.class);
		fetchIcpcRequestPersonLink
				.add(Restrictions.eq("icpcRequest.idIcpcRequest", icpcPlacementRequestDto.getIdICPCRequest()));
		List<IcpcRequestPersonLink> icpcRequestPersonLinkList = (List<IcpcRequestPersonLink>) fetchIcpcRequestPersonLink
				.list();
		if (!CollectionUtils.isEmpty(icpcRequestPersonLinkList)) {
			icpcRequestPersonLinkList.forEach(icpcRequestPersonLink -> {
				sessionFactory.getCurrentSession().delete(icpcRequestPersonLink);
			});

		}

		// Delete ICPC_PLACEMENT_REQUEST
		Criteria fetchIcpcPlacementRequest = sessionFactory.getCurrentSession()
				.createCriteria(IcpcPlacementRequest.class);
		fetchIcpcPlacementRequest.add(Restrictions.eq("idIcpcRequest", icpcPlacementRequestDto.getIdICPCRequest()));
		List<IcpcPlacementRequest> icpcPlacementRequestList = (List<IcpcPlacementRequest>) fetchIcpcPlacementRequest
				.list();
		if (!CollectionUtils.isEmpty(icpcPlacementRequestList)) {
			icpcPlacementRequestList.forEach(icpcPlacementRequest -> {

				// Delete ICPC_PLACEMENT_REQUEST_ENTITY
				Criteria fetchIcpcPlacementRequestEntity = sessionFactory.getCurrentSession()
						.createCriteria(IcpcPlacementRequestEntity.class);
				fetchIcpcPlacementRequestEntity.add(Restrictions.eq("icpcPlacementRequest.idIcpcPlacementRequest",
						icpcPlacementRequest.getIdIcpcPlacementRequest()));
				List<IcpcPlacementRequestEntity> icpcPlacementRequestEntityList = (List<IcpcPlacementRequestEntity>) fetchIcpcPlacementRequestEntity
						.list();
				if (!CollectionUtils.isEmpty(icpcPlacementRequestEntityList)) {
					icpcPlacementRequestEntityList.forEach(icpcPlacementRequestEntity -> {
						sessionFactory.getCurrentSession().delete(icpcPlacementRequestEntity);
					});
				}

				sessionFactory.getCurrentSession().delete(icpcPlacementRequest);

			});

		}

		// Delete ICPC_EVENT_LINK
		Criteria fetchIcpcEventLink = sessionFactory.getCurrentSession().createCriteria(IcpcEventLink.class);
		fetchIcpcEventLink.add(Restrictions.eq("idIcpcRequest", icpcPlacementRequestDto.getIdICPCRequest()));
		List<IcpcEventLink> icpcEventLinkList = (List<IcpcEventLink>) fetchIcpcEventLink.list();
		if (!CollectionUtils.isEmpty(icpcEventLinkList)) {
			icpcEventLinkList.forEach(icpcEventLink -> {
				sessionFactory.getCurrentSession().delete(icpcEventLink);
			});

		}

		// Delete ICPC_TRANSMITTAL

		Criteria fetchIcpcTransmittal = sessionFactory.getCurrentSession().createCriteria(IcpcTransmittal.class);
		fetchIcpcTransmittal
				.add(Restrictions.eq("icpcRequest.idIcpcRequest", icpcPlacementRequestDto.getIdICPCRequest()));
		List<IcpcTransmittal> icpcTransmittalList = (List<IcpcTransmittal>) fetchIcpcTransmittal.list();
		if (!CollectionUtils.isEmpty(icpcTransmittalList)) {
			icpcTransmittalList.forEach(icpcTransmittal -> {
				sessionFactory.getCurrentSession().delete(icpcTransmittal);
			});

		}
		//artf254020: handling requests with case-Specific file deletion issues
		Criteria fetchOtherIcpcRequests = sessionFactory.getCurrentSession().createCriteria(IcpcRequest.class);
		fetchOtherIcpcRequests.add(Restrictions.eq("idIcpcSubmission", icpcPlacementRequestDto.getIdICPCsubmission()));
		fetchOtherIcpcRequests.add(Restrictions.ne("idIcpcRequest", icpcPlacementRequestDto.getIdICPCRequest()));
		List<IcpcRequest> fetchOtherIcpcRequestList = (List<IcpcRequest>) fetchOtherIcpcRequests.list();
		// Delete ICPC Submission when no other requests with related to it
		boolean deleteICPCSubmission = CollectionUtils.isEmpty(fetchOtherIcpcRequestList);

		// Delete ICPC_DOCUMENT
		Criteria fetchIcpcDocument = sessionFactory.getCurrentSession().createCriteria(IcpcDocument.class);
		Disjunction or = Restrictions.disjunction();
		or.add(Restrictions.eq("icpcRequest.idIcpcRequest", icpcPlacementRequestDto.getIdICPCRequest()));
		or.add(Restrictions.eq("icpcSubmission.idIcpcSubmission", icpcPlacementRequestDto.getIdICPCsubmission()));
		fetchIcpcDocument.add(or);
		List<IcpcDocument> icpcDocumentList = (List<IcpcDocument>) fetchIcpcDocument.list();
		if (!CollectionUtils.isEmpty(icpcDocumentList)) {
			icpcDocumentList.forEach(icpcDocument -> {
				//artf254020: handling requests with case-Specific file deletion issues
				if(deleteICPCSubmission || icpcDocument.getIcpcSubmission()==null){
					// Delete ICPC_FILE_STORAGE
					Criteria fetchIcpcFileStorage = sessionFactory.getCurrentSession()
							.createCriteria(IcpcFileStorage.class);
					fetchIcpcFileStorage
							.add(Restrictions.eq("icpcDocument.idIcpcDocument", icpcDocument.getIdIcpcDocument()));
					List<IcpcFileStorage> icpcFileStorageList = (List<IcpcFileStorage>) fetchIcpcFileStorage.list();
					if (!CollectionUtils.isEmpty(icpcFileStorageList)) {
						icpcFileStorageList.forEach(icpcFileStorage -> {
							sessionFactory.getCurrentSession().delete(icpcFileStorage);
						});
					}
					sessionFactory.getCurrentSession().delete(icpcDocument);
				}
			});

		}

		// Delete ICPC_REQUEST
		Criteria fetchIcpcRequest = sessionFactory.getCurrentSession().createCriteria(IcpcRequest.class);
		fetchIcpcRequest.add(Restrictions.eq("idIcpcRequest", icpcPlacementRequestDto.getIdICPCRequest()));
		List<IcpcRequest> icpcRequestList = (List<IcpcRequest>) fetchIcpcRequest.list();
		if (!CollectionUtils.isEmpty(icpcRequestList)) {
			icpcRequestList.forEach(icpcRequest -> {
				sessionFactory.getCurrentSession().delete(icpcRequest);
			});

		}

		// Delete ICPC_SUBMISSION
		//artf254020: handling requests with case-Specific file deletion issues
		if(deleteICPCSubmission) {
			Criteria fetchIcpcSubmission = sessionFactory.getCurrentSession().createCriteria(IcpcSubmission.class);
			fetchIcpcSubmission.add(Restrictions.eq("idIcpcSubmission", icpcPlacementRequestDto.getIdICPCsubmission()));
			List<IcpcSubmission> icpcSubmissionList = (List<IcpcSubmission>) fetchIcpcSubmission.list();
			if (!CollectionUtils.isEmpty(icpcSubmissionList)) {
				icpcSubmissionList.forEach(icpcSubmission -> {
					sessionFactory.getCurrentSession().delete(icpcSubmission);
				});
			}
		}

		// Delete EVENT_PERSON_LINK

		Criteria fetchEventPersonLink = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);
		fetchEventPersonLink.add(Restrictions.eq("event.idEvent", icpcPlacementRequestDto.getIdEvent()));
		List<EventPersonLink> eventPersonLinkList = (List<EventPersonLink>) fetchEventPersonLink.list();
		if (!CollectionUtils.isEmpty(eventPersonLinkList)) {
			eventPersonLinkList.forEach(eventPersonLink -> {
				sessionFactory.getCurrentSession().delete(eventPersonLink);
			});

		}

		// Delete EVENT
		Criteria fetchEvent = sessionFactory.getCurrentSession().createCriteria(Event.class);
		fetchEvent.add(Restrictions.eq("idEvent", icpcPlacementRequestDto.getIdEvent()));
		Event event = (Event) fetchEvent.uniqueResult();
		sessionFactory.getCurrentSession().delete(event);

	}



	/**
	 * @param idTransmission
	 * @param idNeiceTransmittalPerson
	 */
	@Override
	public void saveTransmission(Long idTransmission, Long idNeiceTransmittalPerson) {

	}

	@Override
	public List<Long> getSecondaryWorkersAssigned(Long idEvent) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSecondaryWorkers).addScalar("idWkldperson", StandardBasicTypes.LONG);
		sqlQuery.setParameter("role", ServiceConstants.SECONDARY_WORKER);
		sqlQuery.setParameter("eventId", idEvent);
		return sqlQuery.list();
	}

	/**
	 * Method Name: getSendingAgencyInfo Method Description: Get the
	 * Transmittal Details for the passed Transmittal ID
	 *
	 * @param stateCode
	 * @return List<NeiceStateParticpantDTO>
	 */
	public List<NeiceStateParticpantDTO> getSendingAgencyInfo(String stateCode) {

		return (List<NeiceStateParticpantDTO>) sessionFactory
				.getCurrentSession()
				.createSQLQuery(getAgencyInfo)
				.addScalar("neiceStateParticipantId", StandardBasicTypes.LONG)
				.addScalar("agencyNm", StandardBasicTypes.STRING)
				.setParameter("stateCode", stateCode)
				.setResultTransformer(Transformers.aliasToBean(NeiceStateParticpantDTO.class))
				.list();
	}

	/**
	 * Method Name: getSendingAgencyInfo Method Description: Get the
	 * Transmittal Details for the passed Transmittal ID
	 *
	 * @return List<NeiceStateParticpantDTO>
	 */
	public List<NeiceStateParticpantDTO> getAllAgencyInfo(){

		List<NeiceStateParticpantDTO> neiceStateParticipantList = (List<NeiceStateParticpantDTO>) sessionFactory
				.getCurrentSession().createSQLQuery(getAllAgencyInfo)
				.addScalar("neiceStateParticipantId", StandardBasicTypes.LONG)
				.addScalar("agencyNm", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(NeiceStateParticpantDTO.class)).list();
		return neiceStateParticipantList;


	}

	/**
	 * Method Name: getSendingAgencyInfo Method Description: Get the
	 * Transmittal Details for the passed Transmittal ID
	 *
	 * @param eventId
	 * @return IcpcEventLink
	 */
	public IcpcEventLink getIcpcEventLinkInfo(Long eventId){

		List<IcpcEventLink> icpcEventLink = (List<IcpcEventLink>) sessionFactory.getCurrentSession().createSQLQuery(getICPCEventLink)
				.addScalar("idNeiceCase", StandardBasicTypes.STRING)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.setParameter("event", eventId).setResultTransformer(Transformers.aliasToBean(IcpcEventLink.class)).list();

			return icpcEventLink.get(0);
	}

	/**
	 * Method Name: getSendingAgencyInfo Method Description: Get the
	 * Transmittal Details for the passed Transmittal ID
	 *
	 * @param idICPCRequest
	 * @return Map<Long, String>
	 */
	public Map<Long, String> getIcpcRequestInfo(Long idICPCRequest){

		Map<Long, String>  icpcMap = new TreeMap<>();
		List<ICPCRequestDto> icpcRequest = (List<ICPCRequestDto>) sessionFactory.getCurrentSession().createSQLQuery(getICPCRequest)
				.addScalar("cdSendingState", StandardBasicTypes.STRING)
				.addScalar("cdReceivingState", StandardBasicTypes.STRING)
				.addScalar("idSendingStateAgency", StandardBasicTypes.LONG)
				.addScalar("idSendingStateAgency", StandardBasicTypes.LONG)
				.setParameter("idIcpcRequest", idICPCRequest).setResultTransformer(Transformers.aliasToBean(ICPCRequestDto.class)).list();
		if(icpcRequest.get(0).getIdSendingStateAgency() != null) {
			List<NeiceStateParticipant> agancyData = (List<NeiceStateParticipant>) sessionFactory.getCurrentSession().createSQLQuery(getAgency)
					.addScalar("agencyNm", StandardBasicTypes.STRING).setParameter("neiceStateParticipantId", icpcRequest.get(0).getIdSendingStateAgency()).setResultTransformer(Transformers.aliasToBean(NeiceStateParticipant.class)).list();
			icpcMap.put(icpcRequest.get(0).getIdSendingStateAgency(), agancyData.get(0).getAgencyNm());
		}

		if(icpcRequest.get(0).getIdReceivingStateAgency() != null) {
			List<NeiceStateParticipant> agancyData1 = (List<NeiceStateParticipant>) sessionFactory.getCurrentSession().createSQLQuery(getAgency)
					.addScalar("agencyNm", StandardBasicTypes.STRING).setParameter("neiceStateParticipantId", icpcRequest.get(0).getIdReceivingStateAgency()).setResultTransformer(Transformers.aliasToBean(NeiceStateParticipant.class)).list();

			icpcMap.put(icpcRequest.get(0).getIdReceivingStateAgency(), agancyData1.get(0).getAgencyNm());
		}
		return icpcMap;
	}

	/**
	 * Method Name: getPlacementStatusByRequest
	 * Method Description: Get the Placement Status details by ICPC Request
	 *
	 * @param idICPCRequest
	 * @param getApproved
	 * @return ICPCPlacementStatusDto
	 */
	@Override
	public ICPCPlacementStatusDto getPlacementStatusByRequest(Long idICPCRequest, boolean getApproved) {

		String sqlQuery = getPlacementStatusByRequestSql;

		if (getApproved) {
			sqlQuery = getAprvPlacementStatusInfoByRequestSql;
		}

		// retrieve placement status details
		return (ICPCPlacementStatusDto) sessionFactory.getCurrentSession()
				.createSQLQuery(sqlQuery)
				.addScalar("idICPCPlacementStatus", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("idICPCPlacementRequest", StandardBasicTypes.LONG)
				.addScalar("cdPlacementStatus")
				.addScalar("dtPlacement", StandardBasicTypes.DATE)
				.addScalar("dtPlacementWithdrawn", StandardBasicTypes.DATE)
				.addScalar("cdCompactTermRsn")
				.addScalar("txtReasonOther")
				.addScalar("dtPlacementTerm")
				.setParameter("idIcpcRequest", idICPCRequest)
				.setResultTransformer(Transformers.aliasToBean(ICPCPlacementStatusDto.class))
				.uniqueResult();
	}

	/**
	 * Method Name: saveTransmittalDocs
	 * Method Description: Save the Transmittal selected documents for Transmission
	 *
	 * @param icpcTransmittalDto
	 */
	@Override
	public void saveTransmittalDocs(ICPCTransmittalDto icpcTransmittalDto) {

		IcpcTransmittal icpcTransmittal = (IcpcTransmittal) sessionFactory.getCurrentSession()
				.get(IcpcTransmittal.class, icpcTransmittalDto.getIdICPCTransmittal());

		if (!ObjectUtils.isEmpty(icpcTransmittal)) {

			icpcTransmittal.setIdNeiceTransmittalReq(icpcTransmittalDto.getIdNeiceTransmittalReq());
			icpcTransmittal.setCdTransmittalOtherMode(null);
			icpcTransmittal.setTxtOtherModeReason(null);
			icpcTransmittal.setIdLastUpdatePerson(icpcTransmittalDto.getIdLastUpdatePerson());
			icpcTransmittal.setDtLastUpdate(new Date());

			if (!CollectionUtils.isEmpty(icpcTransmittalDto.getDocumentList()) && icpcTransmittalDto.getDocumentList()
					.stream()
					.anyMatch(ICPCDocumentDto::isDocumentSelected)) {

				Set<Long> selectedIdIcpcDocuments = icpcTransmittalDto.getDocumentList()
						.stream()
						.filter(ICPCDocumentDto::isDocumentSelected)
						.map(ICPCDocumentDto::getIdICPCDocument)
						.collect(Collectors.toSet());

				Set<IcpcTransmittalDocLink> icpcTransmittalDocLinkSet = new HashSet<>(
						icpcTransmittal.getIcpcTransmittalDocLinks());

				// Removing the elements which are not present in the incoming DTO collection
				icpcTransmittalDocLinkSet.removeIf(link -> !selectedIdIcpcDocuments.contains(link.getIcpcDocument()
						.getIdIcpcDocument()));

				Set<Long> linkedIdIcpcDocuments = icpcTransmittalDocLinkSet
						.stream()
						.map(dto -> dto.getIcpcDocument()
								.getIdIcpcDocument())
						.collect(Collectors.toSet());

				// Removing the elements from DTO which are already present in the newly created Hash Set
				icpcTransmittalDto.getDocumentList()
						.removeIf(dto -> linkedIdIcpcDocuments.contains(dto.getIdICPCDocument()));

				icpcTransmittalDocLinkSet.addAll(icpcTransmittalDto.getDocumentList()
						.stream()
						.filter(ICPCDocumentDto::isDocumentSelected)
						.map(dto -> {

							IcpcDocument icpcDocument = (IcpcDocument) sessionFactory.getCurrentSession()
									.get(IcpcDocument.class, dto.getIdICPCDocument());

							IcpcTransmittalDocLink icpcTransmittalDocLink = new IcpcTransmittalDocLink();
							icpcTransmittalDocLink.setIcpcDocument(icpcDocument);
							icpcTransmittalDocLink.setIcpcTransmittal(icpcTransmittal);
							icpcTransmittalDocLink.setIcpcRequest(icpcDocument.getIcpcRequest());
							icpcTransmittalDocLink.setTsCreated(new Date());
							icpcTransmittalDocLink.setIdCreatedBy(icpcTransmittalDto.getIdCreatedPerson());
							icpcTransmittalDocLink.setTsLastUpdate(new Date());
							return icpcTransmittalDocLink;
						})
						.collect(Collectors.toSet()));

				icpcTransmittal.getIcpcTransmittalDocLinks()
						.clear();
				icpcTransmittal.getIcpcTransmittalDocLinks().addAll(icpcTransmittalDocLinkSet);
			} else {

				icpcTransmittal.getIcpcTransmittalDocLinks()
						.clear();
			}

			sessionFactory.getCurrentSession()
					.saveOrUpdate(icpcTransmittal);

			sessionFactory.getCurrentSession().flush();

		}
	}

	/**
	 * Method Name: saveTransmittalStatus
	 * Method Description: Save the Transmittal Status received from MuleSoft
	 *
	 * @param icpcTransmittalDto
	 */
	@Override
	public void saveTransmittalStatus(ICPCTransmittalDto icpcTransmittalDto) {

		IcpcTransmittal icpcTransmittal = (IcpcTransmittal) sessionFactory.getCurrentSession()
				.get(IcpcTransmittal.class, icpcTransmittalDto.getIdICPCTransmittal());

		if (!ObjectUtils.isEmpty(icpcTransmittal)) {

			icpcTransmittal.setCdTransmittalStatus(icpcTransmittalDto.getCdTransmittalStatus());
			icpcTransmittal.setTxtTransmittalSttsDesc(icpcTransmittalDto.getTxtTransmittalSttsDesc());
			icpcTransmittal.setIdLastUpdatePerson(icpcTransmittalDto.getIdLastUpdatePerson());
			icpcTransmittal.setDtLastUpdate(new Date());
			icpcTransmittal.setTxtNeiceTransactionResponse(icpcTransmittalDto.getTxtNeiceTransactionResponse());
			sessionFactory.getCurrentSession().saveOrUpdate(icpcTransmittal);

			sessionFactory.getCurrentSession().flush();
		}

	}

	/**
	 * Method Name: getTransmittalLinkedDocuments
	 * Method Description: Retrieve all the linked documents from Transmittal
	 *
	 * @param idIcpcTransmittal
	 */
	@Override
	public List<Long> getTransmittalLinkedDocuments(Long idIcpcTransmittal) {

		Query query = sessionFactory.getCurrentSession()
				.createSQLQuery(getTransmittalLinkDocumentsSql)
				.addScalar("idIcpcDocument", StandardBasicTypes.LONG)
				.setParameter("idIcpcTransmittal", idIcpcTransmittal);
		return (List<Long>) query.list();
	}

	/**
	 * Method Name: getSiblingsRequests
	 * Method Description: Retrieve the ICPC Request IDs for all siblings
	 *
	 * @param idPersons
	 * @param idIcpcRequest
	 */
	@Override
	public List<ICPCPersonDto> getSiblingsRequests(List<Long> idPersons, Long idIcpcRequest) {

		Query query = sessionFactory.getCurrentSession()
				.createSQLQuery(getSiblingsRequestsSql)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.setParameterList("idPersons", idPersons)
				.setParameter("idIcpcRequest", idIcpcRequest)
				.setResultTransformer(Transformers.aliasToBean(ICPCPersonDto.class));;

		return (List<ICPCPersonDto>) query.list();
	}

	/**
	 * Method Name: getAllChildDocumentListInfo
	 * Method Description: Retrieves the documents from all the selected children
	 *
	 * @return List<ICPCDocumentDto>
	 */
	@Override
	public List<ICPCDocumentDto> getAllChildrenDocumentListInfo(List<Long> idICPCRequests) {

		return (List<ICPCDocumentDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getAllChildrenDocumentListInfoSql)
				.addScalar("idICPCDocument", StandardBasicTypes.LONG)
				.addScalar("idLastupdatePerson", StandardBasicTypes.LONG)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("idICPCSubmission", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdType", StandardBasicTypes.STRING)
				.addScalar("dtUpload", StandardBasicTypes.DATE)
				.addScalar("fileName", StandardBasicTypes.STRING)
				.addScalar("sysMimeType", StandardBasicTypes.STRING)
				.addScalar("txtDetails", StandardBasicTypes.STRING)
				.addScalar("txtKeyDocStore", StandardBasicTypes.STRING)
				.setParameterList("idICPCRequests", idICPCRequests)
				.setResultTransformer(Transformers.aliasToBean(ICPCDocumentDto.class))
				.list();
	}

	/**
	 * Method Name: saveTransmittalOtherMode
	 * Method Description: Save the Transmittal Other Mode
	 *
	 * @param icpcTransmittalDto
	 */
	@Override
	public void saveTransmittalOtherMode(ICPCTransmittalDto icpcTransmittalDto) {

		IcpcTransmittal icpcTransmittal = (IcpcTransmittal) sessionFactory.getCurrentSession()
				.get(IcpcTransmittal.class, icpcTransmittalDto.getIdICPCTransmittal());

		if (!ObjectUtils.isEmpty(icpcTransmittal)) {

			icpcTransmittal.setCdTransmittalOtherMode(icpcTransmittalDto.getCdOtherMode());
			icpcTransmittal.setTxtOtherModeReason(icpcTransmittalDto.getTxtOtherModeReason());
			icpcTransmittal.setIdLastUpdatePerson(icpcTransmittalDto.getIdLastUpdatePerson());
			icpcTransmittal.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(icpcTransmittal);

			sessionFactory.getCurrentSession().flush();
		}

	}

	/**
	 * Method Name: getPlacementRequestDtlForAll
	 * Method Description: Retrieve Placement Request Details for all ID_ICPC_REQUEST's
	 *
	 * @param idIcpcRequests
	 * @return
	 */
	@Override
	public Map<Long, ICPCPlacementRequestDto> getPlacementRequestDtlForAll(List<Long> idIcpcRequests) {

		List<ICPCRequestDto> icpcRequestDtos = (List<ICPCRequestDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getPlacementRequestEventIdsSql)
				.addScalar("idICPCRequest", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameterList("idIcpcRequests", idIcpcRequests)
				.setResultTransformer(Transformers.aliasToBean(ICPCRequestDto.class))
				.list();

		if (CollectionUtils.isEmpty(icpcRequestDtos)) {
			icpcRequestDtos = new ArrayList<>();
		}

		Map<Long, ICPCPlacementRequestDto> icpcPlacementStatusDtoMap = new HashMap<>();

		icpcRequestDtos.forEach(dto -> icpcPlacementStatusDtoMap.put(dto.getIdICPCRequest(),
				getICPCPlacementRequestInfo(dto.getIdEvent(), null)));

		return icpcPlacementStatusDtoMap;
	}

	/**
	 * Method Name: createAlert
	 * Method Description: This method is used to create the alert
	 *
	 * @param todoDto
	 */
	public void createAlert(TodoDto todoDto) {
		Long result = ServiceConstants.ZERO;
		Todo todo = new Todo();
		if (todoDto.getIdTodoPersAssigned() != 0) {
			Person person = new Person();
			person.setIdPerson(todoDto.getIdTodoPersAssigned());
			todo.setPersonByIdTodoPersAssigned(person);
		}
		todo.setCdTodoTask(null);
		todo.setCdTodoType(todoDto.getCdTodoType());
		Stage stage = new Stage();
		stage.setIdStage(todoDto.getIdTodoStage());
		todo.setStage(stage);
		todo.setDtTodoDue(todoDto.getDtTodoDue());
		todo.setTxtTodoDesc(todoDto.getTodoDesc());
		todo.setTxtTodoLongDesc(null);
		todo.setDtTodoTaskDue(todoDto.getDtTodoTaskDue());
		todo.setDtTodoCompleted(todoDto.getDtTodoCompleted());
		todo.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(todo);
	}

	/**
	 * Method Name: updateAgencyEntityPhone
	 * Method Description: This method is used to update the Agency Entity Phone details
	 *
	 * @param idEntity
	 * @param nbrPhone
	 * @param idUser
	 */
	@Override
	public void updateAgencyEntityPhone(Long idEntity, Long nbrPhone, Long idUser) {

		sessionFactory.getCurrentSession().createSQLQuery(updateAgencyEntityPhoneSql)
				.setParameter("idEntity", idEntity)
				.setParameter("nbrPhone", nbrPhone)
				.setParameter("idUser", idUser)
				.executeUpdate();

	}
}
