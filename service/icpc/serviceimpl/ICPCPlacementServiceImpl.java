/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Service Implementation for the ICPC Page services
 *Aug 03, 2018 - 4:23:46 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.icpc.serviceimpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ICPCEmailLogReq;
import us.tx.state.dfps.service.common.request.ICPCPlacementReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.FTRelationshipRes;
import us.tx.state.dfps.service.common.response.ICPCPlacementRes;
import us.tx.state.dfps.service.common.response.ListTransmissionRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.JSONUtil;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.commontodofunction.service.CommonToDoFunctionService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationBean;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.familytree.dao.FTRelationshipDao;
import us.tx.state.dfps.service.familytree.utils.FTRelationshipSuggestionUtils;
import us.tx.state.dfps.service.icpc.dao.ICPCNeiceTransmissionDao;
import us.tx.state.dfps.service.icpc.dao.ICPCPlacementDao;
import us.tx.state.dfps.service.icpc.dto.*;
import us.tx.state.dfps.service.icpc.dto.transmittal.NEICETransmittalDocumentType;
import us.tx.state.dfps.service.icpc.dto.transmittal.category.NEICEDocument100AType;
import us.tx.state.dfps.service.icpc.dto.transmittal.category.PlacementResourceRelatedPersonAssociationType;
import us.tx.state.dfps.service.icpc.dto.transmittal.category.ResourceDetailType;
import us.tx.state.dfps.service.icpc.dto.transmittal.core.DateType;
import us.tx.state.dfps.service.icpc.dto.transmittal.entity.EntityType;
import us.tx.state.dfps.service.icpc.dto.transmittal.entity.OrganizationType;
import us.tx.state.dfps.service.icpc.dto.transmittal.enums.PlacementLocationCodeType;
import us.tx.state.dfps.service.icpc.dto.transmittal.enums.PlacementResourceRelatedPersonCategoryCodeType;
import us.tx.state.dfps.service.icpc.dto.transmittal.enums.SexCodeType;
import us.tx.state.dfps.service.icpc.dto.transmittal.location.AddressType;
import us.tx.state.dfps.service.icpc.dto.transmittal.person.ChildType;
import us.tx.state.dfps.service.icpc.dto.transmittal.person.ParentChildAssociationType;
import us.tx.state.dfps.service.icpc.dto.transmittal.person.PersonNameType;
import us.tx.state.dfps.service.icpc.dto.transmittal.person.PersonType;
import us.tx.state.dfps.service.icpc.service.ICPCPlacementService;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.placement.service.PlacementService;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dto.CapsResourceLinkDto;
import us.tx.state.dfps.service.subcare.dto.ResourceAddressDto;
import us.tx.state.dfps.service.workload.dao.ApprovalDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StagePrincipalDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * Implementation for the ICPC Page services Aug 03, 2017- 4:23:46 PM © 2017
 * Texas Department of Family and Protective Services
 */

@Service
@Transactional
public class ICPCPlacementServiceImpl implements ICPCPlacementService {

	@Autowired
	private ICPCPlacementDao icpcPlacementDao;

	@Autowired
	private ICPCNeiceTransmissionDao icpcNeiceTransmissionDao;

	@Autowired
	private LookupDao lookupDao;

	@Autowired
	private PostEventService postEventService;

	@Autowired
	private FTRelationshipDao tfRelationshipDao;

	@Autowired
	private FTRelationshipSuggestionUtils ftRelationshipSuggestionUtils;

	@Autowired
	CaseUtils caseUtils;

	@Autowired
	PlacementService placementService;

	@Autowired
	ApprovalCommonService approvalService;

	@Autowired
	private ApprovalCommonService approvalCommonService;

	@Autowired
	private CommonToDoFunctionService commonToDoFunctionService;
	
	@Autowired
	private WorkLoadDao workLoadDao;

	@Autowired
	private StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	private ApprovalDao approvalDao;

	@Autowired
	private TodoDao todoDao;

	@Autowired
	private CapsResourceDao capsResourceDao;
	public static String CD_TODO_INFO_ALERT  = "ICPC02";
	public static String TO_DO_DESC = "There is a new 100B to be processed.";
	private static final String PRIVATE_AGENCY = "Private Agency";
	private static final String PRIVATE_AGENCY_OTHER = "346";

	private static final String ANOTHER_AGENCY_TO_SUPERVISE = "Another Agency to Supervise";
	private static final String ANOTHER_AGENCY_TO_SUPERVISE_OTHER = "Other";

	/**
	 * 
	 * Method Name: getSummaryInfo Method Description: Get the ICPC Summary Info
	 * for the Case
	 * 
	 * @param idCase
	 * @return ICPCPlacementRequestDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public ICPCPlacementRequestDto getSummaryInfo(Long idCase) {

		ICPCPlacementRequestDto icpcPlacementRequestDto = new ICPCPlacementRequestDto();
		// Get Summary Info
		icpcPlacementRequestDto = icpcPlacementDao.getSummaryInfo(idCase);
		// Get the Transmittal List
		icpcPlacementRequestDto.setTransmittalList(icpcPlacementDao.getAllTransmittalList(idCase));
		// Get the Request List
		icpcPlacementRequestDto.setRequestList(icpcPlacementDao.getAllRequestList(idCase));
		return icpcPlacementRequestDto;
	}

	/**
	 * Method Name: getTransmittalInfo Method Description: Get the transmittal
	 * details for the passed transmittal ID and the ICPC Request ID
	 * 
	 * @param idTransmittal
	 *            - Transmittal ID
	 * @param idICPCRequest
	 *            - ICPC Request ID
	 * @param idStage
	 *            - Stage ID
	 * @return ICPCTransmittalDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public ICPCTransmittalDto getTransmittalInfo(Long idTransmittal, Long idICPCRequest, Long idStage) {

		ICPCTransmittalDto icpcTransmittalDto = new ICPCTransmittalDto();
		List<ICPCPersonDto> siblingsForStage = new ArrayList<>();
		Map<Long, Long> selectedSiblings = new HashMap<>();
		String nmResource;
		icpcTransmittalDto = icpcPlacementDao.getTransmittalInfo(idTransmittal, idICPCRequest, idStage);
		if (!ObjectUtils.isEmpty(icpcTransmittalDto.getIdICPCRequest())) {
			nmResource = icpcPlacementDao.getPlacementResourceName(icpcTransmittalDto.getIdICPCRequest());
			icpcTransmittalDto.setNmResource(nmResource);
		}
		Map<String, String> transmittalTypeDetails = new HashMap<>();
		transmittalTypeDetails = icpcPlacementDao.getTransmittalDetails(idTransmittal);
		if (!ObjectUtils.isEmpty(transmittalTypeDetails)) {
			icpcTransmittalDto.setTransmittalDetails(transmittalTypeDetails);
		}
		if (!ObjectUtils.isEmpty(idICPCRequest) && idICPCRequest > 0) {
			siblingsForStage = icpcPlacementDao.getSiblings(idICPCRequest);
			if (!CollectionUtils.isEmpty(siblingsForStage)) {
				icpcTransmittalDto.setSiblingsForStage(siblingsForStage);
				if (!ObjectUtils.isEmpty(idTransmittal) && idTransmittal > 0) {
					selectedSiblings = icpcPlacementDao.getSelectedChildren(idTransmittal);
					icpcTransmittalDto.setSelectedSiblings(selectedSiblings);
				}
			}
		}

		List<Long> idIcpcRequests = null;

		if (!CollectionUtils.isEmpty(siblingsForStage) && siblingsForStage.stream()
				.map(ICPCPersonDto::getIdPerson)
				.anyMatch(selectedSiblings::containsKey)) {

			List<Long> idSiblingsPersons = siblingsForStage.stream()
					.map(ICPCPersonDto::getIdPerson)
					.filter(selectedSiblings::containsKey)
					.collect(Collectors.toList());

			idIcpcRequests = Optional.ofNullable(
							icpcPlacementDao.getSiblingsRequests(idSiblingsPersons, !ObjectUtils.isEmpty(
									idICPCRequest) && idICPCRequest > 0 ? idICPCRequest : icpcTransmittalDto.getIdICPCRequest()))
					.orElseGet(ArrayList::new)
					.stream()
					.map(ICPCPersonDto::getIdICPCRequest)
					.collect(Collectors.toList());
		} else {
			idIcpcRequests = new ArrayList<>();
		}

		idIcpcRequests.add(!ObjectUtils.isEmpty(
				idICPCRequest) && idICPCRequest > 0 ? idICPCRequest : icpcTransmittalDto.getIdICPCRequest());

		// Changes for Intact Relocation - 100B Documents retrievals from 100B
		if (Arrays.asList(ServiceConstants.ICPCTRTP_150, ServiceConstants.ICPCTRTP_160)
				.contains(icpcTransmittalDto.getCdTransmittalType()) && ServiceConstants.ICPCHSTY_10.equals(
				icpcTransmittalDto.getCdHomeStudyType())) {

			List<ICPCPlacementStatusDto> icpcPlacementStatusDtos = new ArrayList<>();

			for (Long id : idIcpcRequests) {
				Optional.ofNullable(icpcPlacementDao.getPlacementStatusByRequest(id, true))
						.ifPresent(icpcPlacementStatusDtos::add);
			}

			if (!CollectionUtils.isEmpty(icpcPlacementStatusDtos)) {
				idIcpcRequests.addAll(icpcPlacementStatusDtos.stream()
						.map(ICPCPlacementStatusDto::getIdICPCRequest)
						.collect(Collectors.toList()));
			}
		}

		List<ICPCDocumentDto> icpcDocumentDtos = icpcPlacementDao.getAllChildrenDocumentListInfo(idIcpcRequests);

		if (!CollectionUtils.isEmpty(icpcDocumentDtos) && !ObjectUtils.isEmpty(
				icpcTransmittalDto.getIdICPCTransmittal()) && icpcTransmittalDto.getIdICPCTransmittal() > 0) {
			List<Long> linkedDocumentIds = icpcPlacementDao.getTransmittalLinkedDocuments(
					icpcTransmittalDto.getIdICPCTransmittal());

			if (CollectionUtils.isNotEmpty(linkedDocumentIds)) {
				for (ICPCDocumentDto icpcDocumentDto : icpcDocumentDtos) {
					if (linkedDocumentIds.contains(icpcDocumentDto.getIdICPCDocument())) {
						icpcDocumentDto.setDocumentSelected(true);
					}
				}
			}

			icpcTransmittalDto.setDocumentList(icpcDocumentDtos);

		}
		return icpcTransmittalDto;
	}

	/**
	 * 
	 * Method Name: getPlacementStatusInfo Method Description: get ICPC status
	 * report detail information for display Method Description:
	 * 
	 * @param idEvent
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public ICPCPlacementStatusDto getPlacementStatusInfo(Long idEvent) {
		ICPCPlacementStatusDto iCPCPlacementStatusDto = icpcPlacementDao.getPlacementStatusInfo(idEvent);
		iCPCPlacementStatusDto.setIdEvent(idEvent);

		if (!ObjectUtils.isEmpty(iCPCPlacementStatusDto.getGuardian())
				&& !ObjectUtils.isEmpty(iCPCPlacementStatusDto.getGuardian().getIdPerson())) {
			ICPCPersonDto guardian = iCPCPlacementStatusDto.getGuardian();
			ICPCPersonDto child = iCPCPlacementStatusDto.getChild();
			if (!ObjectUtils.isEmpty(child)) {
				FTPersonRelationDto relationDto = icpcPlacementDao.getPersonRelation(child.getIdPerson(),
						guardian.getIdPerson());
				if (!ObjectUtils.isEmpty(relationDto)) {
					guardian.setRelation(relationDto);
					iCPCPlacementStatusDto.setGuardian(guardian);
				}
			}
		}
		return iCPCPlacementStatusDto;
	}

	/**
	 * Method Name: saveTransmittal Method Description: This method saves the
	 * transmittal detail page details.
	 * 
	 * @param icpcTransmittalDto
	 * @return Long
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, rollbackFor = Exception.class)
	public Long saveTransmittal(ICPCTransmittalDto icpcTransmittalDto) {
		Long idTransmittal;
		idTransmittal = icpcPlacementDao.saveTransmittal(icpcTransmittalDto);
		return idTransmittal;
	}

	/**
	 * 
	 * Method Name: getICPCPlacementRequestDetail Method Description: get all
	 * ICPC placement request detail
	 * 
	 * @param idEvent
	 * @param idStage
	 * @param eligibility
	 * @return ICPCPlacementRequestDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public ICPCPlacementRequestDto getICPCPlacementRequestDetail(Long idEvent, Long idStage, Boolean eligibility) {


		// Call getICPCPlacementRequestInfo to get placement request
		// Information.
		ICPCPlacementRequestDto icpcPlacementRequestDto = icpcPlacementDao.getICPCPlacementRequestInfo(idEvent,
				idStage);

		Date releaseDt = null;
		String neiceReleaseDt;

		try {
			neiceReleaseDt = lookupDao.simpleDecode(ServiceConstants.CRELDATE,
					ServiceConstants.JAN_2023_NEICE);
			releaseDt = DateUtils.toJavaDateFromInput(neiceReleaseDt);
		} catch (Exception e) {
			new ServiceLayerException(e.getMessage());
		}

		boolean preNeiceReleaseDate = !ObjectUtils.isEmpty(releaseDt) && !ObjectUtils.isEmpty(
                icpcPlacementRequestDto.getDtCreated()) && DateUtils.isBefore(icpcPlacementRequestDto.getDtCreated(),
                releaseDt);

        if (preNeiceReleaseDate) {
			if (ObjectUtils.isEmpty(icpcPlacementRequestDto.getCdSendingAgency())) {
				icpcPlacementRequestDto.setCdSendingAgency(99999999L);
			}
			if (ObjectUtils.isEmpty(icpcPlacementRequestDto.getCdReceivingAgency())) {
				icpcPlacementRequestDto.setCdReceivingAgency(99999999L);
			}
		}

		if (!StringUtils.isEmpty(icpcPlacementRequestDto.getCdCareType()) && CodesConstant.ICPCCRTP_80.equals(
				icpcPlacementRequestDto.getCdCareType()) && !CollectionUtils.isEmpty(
				icpcPlacementRequestDto.getPlcmntPersonLst())) {

			//List<ICPCPersonDto> icpcPersonDtoLst = null;
			for (ICPCPersonDto guardian : icpcPlacementRequestDto.getPlcmntPersonLst()) {
				//icpcPersonDtoLst = new ArrayList<ICPCPersonDto>();
				//ICPCPersonDto guardian = icpcPersonDto;
				ICPCPersonDto child = icpcPlacementRequestDto.getChild();
				if (child != null) {
					FTPersonRelationDto relationValueBean = getPersonRelation(child.getIdPerson(),
							guardian.getIdPerson());
					if (relationValueBean != null) {
						guardian.setRelation(relationValueBean);
						//icpcPersonDtoLst.add(guardian);
					}
				}
			}
			//icpcPlacementRequestDto.setPlcmntPersonLst(icpcPersonDtoLst);
		}
		// Call getDocumentListInfo to get document info
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIdICPCRequest()))
			icpcPlacementRequestDto
					.setDocumentList(icpcPlacementDao.getDocumentListInfo(icpcPlacementRequestDto.getIdICPCRequest()));
		// Call getPrimaryWorkerInfo to get the primary Worker Info
		icpcPlacementRequestDto.setPrimaryWorker(icpcPlacementDao.getPrimaryWorkerInfo(idStage));
		// Call getEligibility to get sEligibility information
		if (eligibility && !ObjectUtils.isEmpty(icpcPlacementRequestDto.getChild()))
			icpcPlacementRequestDto
					.setIndTitleIVE(icpcPlacementDao.getEligibility(icpcPlacementRequestDto.getChild().getIdPerson()));
		// Call getDisasterRlf to get Disaster Relief information
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getChild())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getChild().getIdPerson())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getIdICPCRequest()))
			icpcPlacementRequestDto.setCdDisasterRlf(icpcPlacementDao.getDisasterRlf(
					icpcPlacementRequestDto.getChild().getIdPerson(), icpcPlacementRequestDto.getIdICPCRequest()));

		// Sending ICPC Agency List
		icpcPlacementRequestDto.setAgencyMap(retrieveICPCAgencyList.apply(icpcPlacementRequestDto.getCdSendingState()));

		// Receiving ICPC Agency List
		icpcPlacementRequestDto.setReceivingAgencyMap(
				retrieveICPCAgencyList.apply(icpcPlacementRequestDto.getCdReceivingState()));

		return icpcPlacementRequestDto;
	}

	/**
	 * Function to retrieve the ICPC Agency List from NEICE based on state code
	 */
	private final Function<String, Map<Long, String>> retrieveICPCAgencyList = (stateCd) -> {

		Map<Long, String> agencyInfoMap = new TreeMap<>();

		List<NeiceStateParticpantDTO> neiceAgencyList = !ObjectUtils.isEmpty(
				stateCd) ? icpcPlacementDao.getSendingAgencyInfo(stateCd) : icpcPlacementDao.getAllAgencyInfo();

		for (NeiceStateParticpantDTO neiceStateParticpantDTO : neiceAgencyList) {
			agencyInfoMap.put(neiceStateParticpantDTO.getNeiceStateParticipantId(),
					neiceStateParticpantDTO.getAgencyNm());
		}

		return agencyInfoMap;
	};

	/**
	 * 
	 * Method Name: getPersonRelation
	 * Method Description: retrieves the guardian PERSON_RELATION for the two given PERSON ids
	 * 
	 * @param idRelatedPerson
	 * @param idPersonRelation
	 * @return FTPersonRelationDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public FTPersonRelationDto getPersonRelation(Long idPersonRelation, Long idRelatedPerson) {
		FTPersonRelationDto reverseRelValueBean = null;
		FTPersonRelationDto relValueBean = null;
		if (!ObjectUtils.isEmpty(idPersonRelation) && idPersonRelation > 0) {
			ArrayList<FTPersonRelationBean> relations = tfRelationshipDao
					.selectRelationshipsWith2Persons(idPersonRelation, idRelatedPerson);

			if (!CollectionUtils.isEmpty(relations)) {
				for (FTPersonRelationBean relation : relations) {
					String relType = lookupDao.simpleDecodeSafe(CodesConstant.ICPCFMRL, relation.getCdRelation());
					if (!ServiceConstants.EMPTY_STRING.equals(relType)) {
						//[artf164931] Defect: 15742 - relValueBean is initialized only when needed to ensure future if() logic runs correctly
						relValueBean = new FTPersonRelationDto();
						BeanUtils.copyProperties(relation, relValueBean);
						break;
					}
					else{
						//[artf163933] Defect: 15500 - initialize reverseRelValueBean before calling BeanUtils.copyProperties() so that it is not null
						reverseRelValueBean = new FTPersonRelationDto();
						BeanUtils.copyProperties(relation, reverseRelValueBean);
					}



				}
				//if a guardian relationship is not found, take the non-guardian relationship and reverse it (e.g. grandchild -> grandparent)
				if (ObjectUtils.isEmpty(relValueBean) && !ObjectUtils.isEmpty(reverseRelValueBean)) {
					ftRelationshipSuggestionUtils.reverseRelationship(reverseRelValueBean);
					relValueBean = reverseRelValueBean;
				}
			}
		}
		return relValueBean;
	}

	/**
	 * Method Name: deleteTransmittal Method Description: Delete the transmittal
	 * details to the database from Placement Request Details page
	 * 
	 * @param icpcPlacementReq
	 * @return void
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteICPCTransmittal(Long idIcpcTransmittal) {
		icpcPlacementDao.deleteICPCTransmittal(idIcpcTransmittal);
	}

	/**
	 * Method Name: saveICPCSummary Method Description: Saves the ICPC Summary
	 * Details
	 * 
	 * @param icpcPlacementRequestDto
	 * @return void
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void saveICPCSummary(ICPCPlacementRequestDto icpcPlacementRequestDto) {
		Long idIntakeStage = 0L;
		idIntakeStage = icpcPlacementDao.retrieveIntakeStageId(icpcPlacementRequestDto.getIdCase(),
				CodesConstant.CSTAGES_INT);
		icpcPlacementRequestDto.setIdStage(idIntakeStage);
		icpcPlacementDao.saveICPCSubmission(icpcPlacementRequestDto);
	}

	/**
	 * 
	 * Method Name: savePlacementRequest Method Description:save the ICPC
	 * Placement Request details
	 * 
	 * @param ICPCPlacementRequestDBDto
	 * @return Long
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, rollbackFor = Exception.class)
	public ICPCPlacementRequestDto savePlacementRequest(ICPCPlacementRequestDBDto icpcPlacementRequestDBDto) {

		boolean plcmntPrsn = false;
		boolean plcmntRsrc = false;
		ICPCPlacementRequestDto icpcPlacementRequestDto = icpcPlacementRequestDBDto.getPlacementRequestValueBean();
		if (icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_03)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_05)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_10)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_20)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_40)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_70)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_80)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_100)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_110)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_120)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_130)) {
			plcmntPrsn = true;
		} else {
			plcmntRsrc = true;
		}

		String nmPrsnRsrcPlcmnt = "";

		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPlcmntResource())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getPlcmntResource()
				.getNmResource()) && plcmntRsrc) {
			nmPrsnRsrcPlcmnt = icpcPlacementRequestDto.getPlcmntResource()
					.getNmResource();
		} else if (!CollectionUtils.isEmpty(icpcPlacementRequestDto.getPlcmntPersonLst()) && !ObjectUtils.isEmpty(
				icpcPlacementRequestDto.getPlcmntPersonLst()
						.get(0)
						.getNmFull()) && plcmntPrsn) {
			nmPrsnRsrcPlcmnt = icpcPlacementRequestDto.getPlcmntPersonLst()
					.get(0)
					.getNmFull();
		}

		// 1. Create new Event for 100A in SUB Stage.
		String eventDescription = lookupDao.simpleDecodeSafe(CodesConstant.ICPCCRTP,
				icpcPlacementRequestDto.getCdCareType()) + ServiceConstants.CONSTANT_SPACE + nmPrsnRsrcPlcmnt;
		EventValueDto eventValBean = icpcPlacementRequestDBDto.getEventValueBean();
		eventValBean.setEventDescr(eventDescription);

		Long idStagePersonLinkPerson = 0L;
		// Get Primary Child for the Stage.
		if (icpcPlacementRequestDto.getChild() != null) {
			idStagePersonLinkPerson = icpcPlacementRequestDto.getChild().getIdPerson();
		}
		// Call CCMN01UI to create Event and EVENT_PERSON_LINK.
		Long idEvent = postEventService.postEvent(eventValBean, ServiceConstants.REQ_FUNC_CD_ADD,
				idStagePersonLinkPerson);
		// Get INT stage id
		Long intakeStageId = icpcPlacementDao.retrieveIntakeStageId(eventValBean.getIdCase(),
				CodesConstant.CSTAGES_INT);
		// Get the submission id
		Long idIcpcSubmission = icpcPlacementDao.getIdICPCSubmission(intakeStageId);

		// 1. Create entry in to the ICPC Submission table
		if (ObjectUtils.isEmpty(idIcpcSubmission) || idIcpcSubmission == 0) {

			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIdStage())) {
				idIcpcSubmission = icpcPlacementDao.getIdICPCSubmission(icpcPlacementRequestDto.getIdStage());
			}
			if (ObjectUtils.isEmpty(idIcpcSubmission) || idIcpcSubmission == 0) {
				idIcpcSubmission = icpcPlacementDao.insertICPCSubmission(intakeStageId, 0L, null, null,
						icpcPlacementRequestDto.getIdCreatedPerson());
			}
		}
		icpcPlacementRequestDto.setIdICPCsubmission(idIcpcSubmission);

		// 2. Create entry in to ICPC Request table
		//todo: add receiving and sending agency
		icpcPlacementRequestDto.setIdCreatedPerson(icpcPlacementRequestDto.getIdLastUpdatePerson());
		Long idIcpcRequest = icpcPlacementDao.insertICPCRequest(idIcpcSubmission,
				icpcPlacementRequestDto.getIdICPCRequestPrtl(), icpcPlacementRequestDto.getCdRequestType(),
				icpcPlacementRequestDto.getCdReceivingState(), icpcPlacementRequestDto.getCdSendingState(),
				icpcPlacementRequestDto.getIdCreatedPerson(),icpcPlacementRequestDto.getCdSendingAgency(),icpcPlacementRequestDto.getCdReceivingAgency());

		// 3. Create Entry into ICPC Event Link table.
		icpcPlacementDao.insertICPCEventLink(idIcpcRequest, idEvent, eventValBean.getIdCase(),
				icpcPlacementRequestDto.getIdLastUpdatePerson(),icpcPlacementRequestDto.getNeiceCaseId());

		// 4. Create entry in to Placement Request table
		icpcPlacementRequestDto.setIdICPCRequest(idIcpcRequest);
		icpcPlacementRequestDto.setIdCreatedPerson(icpcPlacementRequestDto.getIdLastUpdatePerson());
		Long idIcpcPlacementRequest = icpcPlacementDao.insertPlacementRequest(icpcPlacementRequestDto);

		icpcPlacementRequestDto.setIdICPCPlacementRequest(idIcpcPlacementRequest);

		// 4a. Create entry in to the ICPC_REQUEST_PERSON_LINK table - Child
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getChild())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getChild().getIdPerson())) {
			ICPCPersonDto child = icpcPlacementRequestDto.getChild();
			icpcPlacementDao.insertICPCRequestPersonLink(idIcpcRequest, child.getIdPerson(), CodesConstant.ICPCPRTP_80,
					icpcPlacementRequestDto.getIdCreatedPerson(), 0L, child.getNeicePersonId());
		}

		// 5. Create entry in to the ICPC_REQUEST_PERSON_LINK table - Mother
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getMother())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getMother().getIdPerson())) {
			ICPCPersonDto mother = icpcPlacementRequestDto.getMother();
			icpcPlacementDao.insertICPCRequestPersonLink(idIcpcRequest, mother.getIdPerson(), CodesConstant.ICPCPRTP_30,
					icpcPlacementRequestDto.getIdCreatedPerson(), 0L, mother.getNeicePersonId());
		}
		// 6. Create entry in to the ICPC_REQUEST_PERSON_LINK table - Father
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getFather())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getFather().getIdPerson())) {
			ICPCPersonDto father = icpcPlacementRequestDto.getFather();
			icpcPlacementDao.insertICPCRequestPersonLink(idIcpcRequest, father.getIdPerson(), CodesConstant.ICPCPRTP_10,
					icpcPlacementRequestDto.getIdCreatedPerson(), 0L, father.getNeicePersonId());
		}

		// 7. Create entry in to the ICPC_REQUEST_RESOURCE_LINK table - Person
		if (plcmntPrsn) {
			if (!CollectionUtils.isEmpty(icpcPlacementRequestDto.getPlcmntPersonLst())) {

				icpcPlacementDao.deleteICPCRequestPersonLink(idIcpcRequest,CodesConstant.ICPCPRTP_60);

				ICPCPersonDto primaryPersonDto = Optional.ofNullable(icpcPlacementRequestDto.getPlcmntPerson())
						.orElse(new ICPCPersonDto());

				for (ICPCPersonDto icpcPersonDto : icpcPlacementRequestDto.getPlcmntPersonLst()) {

					String placementCode = ServiceConstants.CSTFROLS_SE;

					if (Objects.equals(icpcPersonDto.getIdPerson(), primaryPersonDto.getIdPerson())) {
						placementCode = ServiceConstants.CSTFROLS_PR;
					}

					icpcPlacementDao.insertICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
								icpcPersonDto.getIdPerson(), CodesConstant.ICPCPRTP_60,
								icpcPlacementRequestDto.getIdCreatedPerson(), 0L,
								icpcPersonDto.getNeicePersonId(), placementCode);

				}
			}
		}

		// 8. Create entry in to the ICPC_REQUEST_RESOURCE_LINK table - Resource
		if (plcmntRsrc) {
			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPlcmntResource())) {
				if(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPlcmntResource().getIdResource())){
					icpcPlacementDao.insertICPCRequestResourceLink(idIcpcRequest,
							icpcPlacementRequestDto.getPlcmntResource()
									.getIdResource(), icpcPlacementRequestDto.getIdCreatedPerson(),
							icpcPlacementRequestDto.getPlcmntResource()
									.getIdResourceNeice());
				}
			}
		}
		// 9. Create entry in to the ICPC_REQUEST_ENCLOSURE table
		// Set the Enclosed documents
		List<String> cdEnclosureList = new ArrayList();
		icpcPlacementRequestDto.getEnclosedDocs().stream().filter(dto -> dto.isDocSelected()).forEach(dto -> {
			cdEnclosureList.add(dto.getDocCode());
		});
		icpcPlacementRequestDto.setCdEnclosure(cdEnclosureList);
		if (!CollectionUtils.isEmpty(icpcPlacementRequestDto.getCdEnclosure())) {
			icpcPlacementRequestDto.getCdEnclosure().forEach(cdEnclosure -> icpcPlacementDao
					.insertICPCRequestEnclosure(idEvent, cdEnclosure, icpcPlacementRequestDto.getIdCreatedPerson()));
		}

		// Set the request Type
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencySupervising()))
			icpcPlacementRequestDto.getAgencySupervising().setCdPlacementRqstType(CodesConstant.ICPCAGTP_30);
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyFinResponsible())) {
			icpcPlacementRequestDto.getAgencyFinResponsible().setCdPlacementRqstType(CodesConstant.ICPCAGTP_10);
			// Set the Entity from County
			if (Stream
					.of(CodesConstant.ICPCCRTP_10, CodesConstant.ICPCCRTP_40, CodesConstant.ICPCCRTP_50,
							CodesConstant.ICPCCRTP_60)
					.anyMatch(icpcPlacementRequestDto.getAgencyFinResponsible().getCdAgencyType()::equalsIgnoreCase)) {
				setAgencyEntityForSave(icpcPlacementRequestDto.getAgencyFinResponsible());
			}
		}
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible())) {
			icpcPlacementRequestDto.getAgencyResponsible().setCdPlacementRqstType(CodesConstant.ICPCAGTP_20);
			// Set the Entity from County
			if (Stream
					.of(CodesConstant.ICPCCRTP_10, CodesConstant.ICPCCRTP_40, CodesConstant.ICPCCRTP_50,
							CodesConstant.ICPCCRTP_60)
					.anyMatch(icpcPlacementRequestDto.getAgencyResponsible().getCdAgencyType()::equalsIgnoreCase)) {
				setAgencyEntityForSave(icpcPlacementRequestDto.getAgencyResponsible());
			}
		}

		// 10. Create entry in to the ICPC_PLACEMENT_REQUEST_ENTITY table
		// (Agency
		// Responsible)
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible().getIdEntity())
				&& !(ServiceConstants.OTHER).equals(icpcPlacementRequestDto.getAgencyResponsible().getAgencyCounty())) {
			Long idIcpcPlacementRqstEntity = icpcPlacementDao.insertICPCPlacementRqstEnity(
					icpcPlacementRequestDto.getAgencyResponsible()
							.getIdEntity(), idIcpcPlacementRequest, icpcPlacementRequestDto.getAgencyResponsible()
							.getCdPlacementRqstType(), icpcPlacementRequestDto.getIdCreatedPerson());

			if (PRIVATE_AGENCY.equalsIgnoreCase(icpcPlacementRequestDto.getAgencyResponsible()
					.getAgencyState()) && PRIVATE_AGENCY_OTHER.equals(icpcPlacementRequestDto.getAgencyResponsible()
					.getAgencyCounty())) {

				icpcPlacementDao.insertICPCPlacementRqstEnityOther(idIcpcPlacementRqstEntity,
						icpcPlacementRequestDto.getAgencyResponsible(), icpcPlacementRequestDto.getIdCreatedPerson());

			} else if (PRIVATE_AGENCY.equalsIgnoreCase(icpcPlacementRequestDto.getAgencyResponsible()
					.getAgencyState()) && !ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible()
					.getNbrPhone())) {

				icpcPlacementDao.updateAgencyEntityPhone(icpcPlacementRequestDto.getAgencyResponsible()
						.getIdEntity(), Long.parseLong(icpcPlacementRequestDto.getAgencyResponsible()
						.getNbrPhone()), icpcPlacementRequestDto.getIdCreatedPerson());
			}

		} else if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible())
				&& (ServiceConstants.OTHER).equals(icpcPlacementRequestDto.getAgencyResponsible().getAgencyCounty())) {
			this.createNewAgency(icpcPlacementRequestDto, ServiceConstants.CD_RQST_ENTY_TYPE_20);
		}


		// 10.1. Create entry in to the ICPC_REQUEST_PERSON_LINK table - Person
		// Responsible
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPersonResponsible())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getPersonResponsible().getIdPerson())) {
			ICPCPersonDto personResponsible = icpcPlacementRequestDto.getPersonResponsible();
			icpcPlacementDao.insertICPCRequestPersonLink(idIcpcRequest, personResponsible.getIdPerson(),
					CodesConstant.ICPCPRTP_50, icpcPlacementRequestDto.getIdCreatedPerson(), 0L,
					personResponsible.getNeicePersonId());
		}

		// 10.2. Create entry in to the ICPC_REQUEST_PERSON_LINK table - Person
		// Financially Responsible
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPersonFinResponsible())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getPersonFinResponsible().getIdPerson())) {
			ICPCPersonDto personFinResponsible = icpcPlacementRequestDto.getPersonFinResponsible();
			icpcPlacementDao.insertICPCRequestPersonLink(idIcpcRequest, personFinResponsible.getIdPerson(),
					CodesConstant.ICPCPRTP_40, icpcPlacementRequestDto.getIdCreatedPerson(), 0L,
					personFinResponsible.getNeicePersonId());
		}

		// 11. Create entry in to the ICPC_PLACEMENT_REQUEST_ENTITY table
		// (Agency
		// Financially Responsible)
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyFinResponsible())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyFinResponsible().getIdEntity())
				&& !(ServiceConstants.OTHER)
						.equals(icpcPlacementRequestDto.getAgencyFinResponsible().getAgencyCounty())) {

			Long idIcpcPlacementRqstEntity = null;
			
			if ((ServiceConstants.YES).equalsIgnoreCase(icpcPlacementRequestDto.getIndSameAddress())) {

				idIcpcPlacementRqstEntity = icpcPlacementDao.insertICPCPlacementRqstEnity(
						icpcPlacementRequestDto.getAgencyResponsible().getIdEntity(), idIcpcPlacementRequest,
						icpcPlacementRequestDto.getAgencyFinResponsible().getCdPlacementRqstType(),
						icpcPlacementRequestDto.getIdCreatedPerson());
				
			} else {
				idIcpcPlacementRqstEntity = icpcPlacementDao.insertICPCPlacementRqstEnity(
						icpcPlacementRequestDto.getAgencyFinResponsible().getIdEntity(), idIcpcPlacementRequest,
						icpcPlacementRequestDto.getAgencyFinResponsible().getCdPlacementRqstType(),
						icpcPlacementRequestDto.getIdCreatedPerson());
			}

			if (PRIVATE_AGENCY.equalsIgnoreCase(icpcPlacementRequestDto.getAgencyResponsible()
					.getAgencyState()) && PRIVATE_AGENCY_OTHER.equals(icpcPlacementRequestDto.getAgencyResponsible()
					.getAgencyCounty())) {

				icpcPlacementDao.insertICPCPlacementRqstEnityOther(idIcpcPlacementRqstEntity,
						icpcPlacementRequestDto.getAgencyFinResponsible(), icpcPlacementRequestDto.getIdCreatedPerson());

			}


		} else if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyFinResponsible()) && (ServiceConstants.OTHER)
				.equals(icpcPlacementRequestDto.getAgencyFinResponsible().getAgencyCounty())) {
			this.createNewAgency(icpcPlacementRequestDto, ServiceConstants.CD_RQST_ENTY_TYPE_10);
		}

		// 12. Create entry in to the ICPC_PLACEMENT_REQUEST_ENTITY table
		// (Supervising Agency Information)
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencySupervising())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencySupervising().getIdEntity())
				&& !(ServiceConstants.OTHER).equals(icpcPlacementRequestDto.getAgencySupervising().getAgencyState())) {
			Long idIcpcPlacementRqstEntity = icpcPlacementDao.insertICPCPlacementRqstEnity(
					icpcPlacementRequestDto.getAgencySupervising()
							.getIdEntity(), idIcpcPlacementRequest, icpcPlacementRequestDto.getAgencySupervising()
							.getCdPlacementRqstType(), icpcPlacementRequestDto.getIdCreatedPerson());

		} else if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencySupervising())
				&& ANOTHER_AGENCY_TO_SUPERVISE_OTHER.equals(icpcPlacementRequestDto.getAgencySupervising().getAgencyState())) {

			Long idIcpcPlacementRqstEntity = icpcPlacementDao.insertICPCPlacementRqstEnity(
					icpcPlacementRequestDto.getAgencySupervising()
							.getIdEntity(), idIcpcPlacementRequest, icpcPlacementRequestDto.getAgencySupervising()
							.getCdPlacementRqstType(), icpcPlacementRequestDto.getIdCreatedPerson());

				icpcPlacementDao.insertICPCPlacementRqstEnityOther(idIcpcPlacementRqstEntity,
						icpcPlacementRequestDto.getAgencySupervising(), icpcPlacementRequestDto.getIdCreatedPerson());
		}

		// remove Reject ToDo - this will remove only assigned to the logged in
		// person
		String toDoDesc = ServiceConstants.NOT_APPROVED;
		icpcPlacementDao.endDateToDo(idEvent, toDoDesc, icpcPlacementRequestDto.getIdCreatedPerson());
		icpcPlacementRequestDto.setIdEvent(idEvent);

		if(!ObjectUtils.isEmpty(icpcPlacementRequestDto.isSaveAndSubmit()) &&
				icpcPlacementRequestDto.isSaveAndSubmit()) {
			List<Long> secondaryWorkers = icpcPlacementDao.getSecondaryWorkersAssigned(idEvent);
			createICPCAlertsForSecondaryWorker(icpcPlacementRequestDto, secondaryWorkers);
		}


		return icpcPlacementRequestDto;

	}

	/**
	 * Method Name: updatePlacementRequest Method Description: update the ICPC
	 * Placement Request details
	 * 
	 * @param ICPCPlacementRequestDBDto
	 * @return List<ICPCAgencyDto>
	 */
	public List<ICPCAgencyDto> updatePlacementRequest(ICPCPlacementRequestDBDto icpcPlacementRequestDBDto) {
		List<ICPCAgencyDto> legacyEventsList = null;

		ICPCPlacementRequestDto icpcPlacementRequestDto = icpcPlacementRequestDBDto.getPlacementRequestValueBean();
		EventValueDto eventValueBean = icpcPlacementRequestDBDto.getEventValueBean();
		ICPCPlacementRequestDto plcmntRqstDBValueBean = null;

		plcmntRqstDBValueBean = icpcPlacementDao.getICPCPlacementRequestInfo(icpcPlacementRequestDto.getIdEvent(),
				eventValueBean.getIdStage());
		String nmPrsnRsrcPlcmnt = "";

		boolean plcmntPrsn = false;
		boolean plcmntRsrc = false;
		if (icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_03)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_05)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_10)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_20)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_40)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_70)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_80)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_100)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_110)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_120)
				|| icpcPlacementRequestDto.getCdCareType().equals(ServiceConstants.CARE_TYPE_130)) {
			plcmntPrsn = true;
		} else {
			plcmntRsrc = true;
		}

		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPlcmntResource())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getPlcmntResource()
				.getNmResource()) && plcmntRsrc) {
			nmPrsnRsrcPlcmnt = icpcPlacementRequestDto.getPlcmntResource()
					.getNmResource();
		} else if (!CollectionUtils.isEmpty(icpcPlacementRequestDto.getPlcmntPersonLst()) && !ObjectUtils.isEmpty(
				icpcPlacementRequestDto.getPlcmntPersonLst()
						.get(0)
						.getNmFull()) && plcmntPrsn) {
			nmPrsnRsrcPlcmnt = icpcPlacementRequestDto.getPlcmntPersonLst()
					.get(0)
					.getNmFull();
		}

		String eventDescription = lookupDao.simpleDecodeSafe(CodesConstant.ICPCCRTP,
				icpcPlacementDao.getCareType(
						icpcPlacementRequestDto.getIdICPCPlacementRequest())) + " " + nmPrsnRsrcPlcmnt;

		EventValueDto eventValBean = icpcPlacementRequestDBDto.getEventValueBean();
		eventValBean.setEventDescr(eventDescription);
		// eventValBean.setEventDescr(eventValBean.getEventDescr());
		eventValBean.setEventStatusCode(eventValBean.getEventStatusCode());
		eventValBean.setCdEventType(eventValBean.getCdEventType());

		// 1.1 Update Placement Request Table
		icpcPlacementDao.updateICPCPlacementRequest(icpcPlacementRequestDto);
		// 2. Delete Enclosure table
		icpcPlacementDao.deleteICPCRequestEnclosure(icpcPlacementRequestDto.getIdEvent());

		// 3. Create entry in to the ICPC_REQUEST_ENCLOSURE table
		// Set the Enclosed documents
		List<String> cdEnclosureList = new ArrayList();
		icpcPlacementRequestDto.getEnclosedDocs().stream().filter(dto -> dto.isDocSelected()).forEach(dto -> {
			cdEnclosureList.add(dto.getDocCode());
		});
		icpcPlacementRequestDto.setCdEnclosure(cdEnclosureList);
		if (!CollectionUtils.isEmpty(icpcPlacementRequestDto.getCdEnclosure())) {
			icpcPlacementRequestDto.getCdEnclosure()
					.forEach(cdEnclosure -> icpcPlacementDao.insertICPCRequestEnclosure(
							icpcPlacementRequestDto.getIdEvent(), cdEnclosure,
							icpcPlacementRequestDto.getIdCreatedPerson()));
		}

		// 4. Update ICPC_REQUEST_PERSON_LINK table (Mother)
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getMother())) {
			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getMother()
					.getIdICPCRequestPersonLink())) {
				if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getMother()
						.getIdPerson())) {
					ICPCPersonDto mother = icpcPlacementRequestDto.getMother();
					icpcPlacementDao.updateICPCRequestPersonLink(mother,
							icpcPlacementRequestDto.getIdLastUpdatePerson());
				} else {
					icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
							CodesConstant.ICPCPRTP_30);
				}

			} else if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getMother()
					.getIdPerson())) {
				icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
						CodesConstant.ICPCPRTP_30);
				icpcPlacementDao.insertICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
						icpcPlacementRequestDto.getMother()
								.getIdPerson(), CodesConstant.ICPCPRTP_30,
						icpcPlacementRequestDto.getIdCreatedPerson(), 0L, icpcPlacementRequestDto.getMother()
								.getNeicePersonId());
			}
		}

		// 5. Update ICPC_REQUEST_PERSON_LINK table (Father)
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getFather())) {
			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getFather()
					.getIdICPCRequestPersonLink())) {
				if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getFather()) && !ObjectUtils.isEmpty(
						icpcPlacementRequestDto.getFather()
								.getIdPerson())) {
					ICPCPersonDto father = icpcPlacementRequestDto.getFather();
					icpcPlacementDao.updateICPCRequestPersonLink(father,
							icpcPlacementRequestDto.getIdLastUpdatePerson());
				} else {
					icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
							CodesConstant.ICPCPRTP_10);
				}
			} else if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getFather()
					.getIdPerson())) {
				icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
						CodesConstant.ICPCPRTP_10);
				icpcPlacementDao.insertICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
						icpcPlacementRequestDto.getFather()
								.getIdPerson(), CodesConstant.ICPCPRTP_10,
						icpcPlacementRequestDto.getIdCreatedPerson(), 0L, icpcPlacementRequestDto.getFather()
								.getNeicePersonId());
			}
		}

		String placementResourceNeiceId = null;
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getNeiceCaseId())) {

			placementResourceNeiceId = icpcNeiceTransmissionDao.fetchPlacementResourceNeiceId(
					icpcPlacementRequestDto.getNeiceCaseId());
		}


		// 6. Update ICPC_REQUEST_PERSON_LINK table (Person)
		if (plcmntPrsn) {

			// Deleting the ICPC Request Resource Link
			icpcPlacementDao.deleteICPCRequestResourceLinkById(icpcPlacementRequestDto.getIdICPCRequest());

			if (!CollectionUtils.isEmpty(icpcPlacementRequestDto.getPlcmntPersonLst())) {

				List<Long> idIcpcRequestPersonLinks = icpcPlacementRequestDto.getPlcmntPersonLst()
						.stream()
						.map(ICPCPersonDto::getIdICPCRequestPersonLink)
						.collect(Collectors.toList());

				// Retrieving all the existing Persons who have been removed on Update
				List<ICPCPersonDto> existingPersonToBeRemoved = Optional.ofNullable(plcmntRqstDBValueBean.getPlcmntPersonLst())
						.orElseGet(ArrayList::new)
						.stream()
						.filter(dto -> !idIcpcRequestPersonLinks.contains(dto.getIdICPCRequestPersonLink()))
						.collect(Collectors.toList());

				// Deleting all the existing Persons who have been removed on Update
				if (!CollectionUtils.isEmpty(existingPersonToBeRemoved)) {
					for (ICPCPersonDto icpcPersonDto : existingPersonToBeRemoved) {
						icpcPlacementDao.deleteICPCRequestPersonLinkByPerson(icpcPlacementRequestDto.getIdICPCRequest(),
								CodesConstant.ICPCPRTP_60, icpcPersonDto.getIdPerson());
					}
				}

				ICPCPersonDto primaryPersonDto = Optional.ofNullable(icpcPlacementRequestDto.getPlcmntPerson())
						.orElse(new ICPCPersonDto());

				for (ICPCPersonDto icpcPersonDto : icpcPlacementRequestDto.getPlcmntPersonLst()) {

					String placementCode = ServiceConstants.CSTFROLS_SE;

					if (Objects.equals(icpcPersonDto.getIdPerson(), primaryPersonDto.getIdPerson())) {
						placementCode = ServiceConstants.CSTFROLS_PR;
					}

					if (!ObjectUtils.isEmpty(icpcPersonDto.getIdICPCRequestPersonLink())
							&& icpcPersonDto.getIdICPCRequestPersonLink() != 0) {
						ICPCPersonDto person = icpcPersonDto;
						icpcPlacementDao.updateICPCRequestPersonLink(person,
								icpcPlacementRequestDto.getIdLastUpdatePerson(), placementCode);
					} else {

						icpcPlacementDao.insertICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
								icpcPersonDto.getIdPerson(), CodesConstant.ICPCPRTP_60,
								icpcPlacementRequestDto.getIdCreatedPerson(), 0L,
								!ObjectUtils.isEmpty(
										icpcPersonDto.getNeicePersonId()) ? icpcPersonDto.getNeicePersonId() : placementResourceNeiceId,
								placementCode);
					}
				}
			} else if (CollectionUtils.isEmpty(
					icpcPlacementRequestDto.getPlcmntPersonLst()) && !CollectionUtils.isEmpty(
					plcmntRqstDBValueBean.getPlcmntPersonLst())) {

				icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
						CodesConstant.ICPCPRTP_60);

			}
		}

		// 7. Update ICPC_REQUEST_RESOURCE_LINK table (Resource)
		if (plcmntRsrc) {

			icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
					CodesConstant.ICPCPRTP_60);

			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPlcmntResource())) {
					if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPlcmntResource().getIdICPCRequestResourceLink())
							&& icpcPlacementRequestDto.getPlcmntResource().getIdICPCRequestResourceLink() != 0) {
						//ICPCResourceDto resource = icpcPlacementRequestDto.getPlcmntResourceLst().get(0);
						icpcPlacementDao.updateICPCRequestResourceLink(icpcPlacementRequestDto.getPlcmntResource(),
								icpcPlacementRequestDto.getIdLastUpdatePerson());
					} else if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPlcmntResource().getIdResource())
							&& icpcPlacementRequestDto.getPlcmntResource().getIdResource() != 0) {

						boolean oldRecordsDeleted = icpcPlacementDao.deleteICPCRequestResourceLinkById(
								icpcPlacementRequestDto.getIdICPCRequest());

						icpcPlacementDao.insertICPCRequestResourceLink(icpcPlacementRequestDto.getIdICPCRequest(),
								icpcPlacementRequestDto.getPlcmntResource()
										.getIdResource(), icpcPlacementRequestDto.getIdCreatedPerson(),
								placementResourceNeiceId);
					}
			} else if (ObjectUtils.isEmpty(icpcPlacementRequestDto.getPlcmntResource())
					&& !ObjectUtils.isEmpty(plcmntRqstDBValueBean.getPlcmntResource())) {
				icpcPlacementDao.deleteICPCRequestResourceLinkById(icpcPlacementRequestDto.getIdICPCRequest());
			}
		}

		// 7.1 Update ICPC_REQUEST table.
		icpcPlacementDao.updateICPCRequest(icpcPlacementRequestDto);

		// 8. Create entry in to the ICPC_PLACEMENT_REQUEST_ENTITY table (Agency
		// Responsible)
		Long idEntityAgency = 0L;
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible())) {
			// Set the Placement request Type
			icpcPlacementRequestDto.getAgencyResponsible().setCdPlacementRqstType(CodesConstant.ICPCAGTP_20);
			// setAgencyEntityForSave
			if (Stream
					.of(CodesConstant.ICPCCRTP_10, CodesConstant.ICPCCRTP_40, CodesConstant.ICPCCRTP_50,
							CodesConstant.ICPCCRTP_60)
					.anyMatch(icpcPlacementRequestDto.getAgencyResponsible().getCdAgencyType()::equalsIgnoreCase)) {
				setAgencyEntityForSave(icpcPlacementRequestDto.getAgencyResponsible());
			}
		}
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible())
				&& icpcPlacementRequestDto.getAgencyResponsible().getAgencyCounty() != null
				&& !(ServiceConstants.OTHER).equals(icpcPlacementRequestDto.getAgencyResponsible().getAgencyCounty())) {

			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible().getIdEntity()) && !ObjectUtils
					.isEmpty(icpcPlacementRequestDto.getAgencyResponsible().getIdICPCPlcmntRqstEntity())) {
				icpcPlacementDao.updateICPCPlacementRequestEntity(
						icpcPlacementRequestDto.getAgencyResponsible().getIdICPCPlcmntRqstEntity(),
						icpcPlacementRequestDto.getAgencyResponsible().getIdEntity(),
						icpcPlacementRequestDto.getIdCreatedPerson());

				if (!PRIVATE_AGENCY.equalsIgnoreCase(icpcPlacementRequestDto.getAgencyResponsible()
						.getAgencyState()) && !PRIVATE_AGENCY_OTHER.equals(icpcPlacementRequestDto.getAgencyResponsible()
						.getAgencyCounty())) {
					icpcPlacementDao.deleteICPCPlacementRequestEntityOther(
							icpcPlacementRequestDto.getAgencyResponsible()
									.getIdICPCPlcmntRqstEntity());
				}

			} else {
				// delete the record
				icpcPlacementDao.deleteICPCPlacementRequestEntity(icpcPlacementRequestDto.getIdICPCPlacementRequest(),
						CodesConstant.ICPCAGTP_20);
				if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible().getIdEntity())) {
					Long idICPCPlcmntRqstEntity = icpcPlacementDao.insertICPCPlacementRqstEnity(
							icpcPlacementRequestDto.getAgencyResponsible().getIdEntity(),
							icpcPlacementRequestDto.getIdICPCPlacementRequest(),
							icpcPlacementRequestDto.getAgencyResponsible().getCdPlacementRqstType(),
							icpcPlacementRequestDto.getIdCreatedPerson());

					icpcPlacementRequestDto.getAgencyResponsible().setIdICPCPlcmntRqstEntity(idICPCPlcmntRqstEntity);
				}
			}

			if (PRIVATE_AGENCY.equalsIgnoreCase(icpcPlacementRequestDto.getAgencyResponsible()
					.getAgencyState()) && PRIVATE_AGENCY_OTHER.equals(icpcPlacementRequestDto.getAgencyResponsible()
					.getAgencyCounty())) {
				icpcPlacementDao.updateICPCPlacementRqstEnityOther(
						icpcPlacementRequestDto.getAgencyResponsible().getIdICPCPlcmntRqstEntity(),
						icpcPlacementRequestDto.getAgencyResponsible(),
						icpcPlacementRequestDto.getIdCreatedPerson());

			} else if (PRIVATE_AGENCY.equalsIgnoreCase(icpcPlacementRequestDto.getAgencyResponsible()
					.getAgencyState()) && !ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible()
					.getNbrPhone())) {

				icpcPlacementDao.updateAgencyEntityPhone(icpcPlacementRequestDto.getAgencyResponsible()
						.getIdEntity(), Long.parseLong(icpcPlacementRequestDto.getAgencyResponsible()
						.getNbrPhone()), icpcPlacementRequestDto.getIdCreatedPerson());
			}

		} else if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible())
				&& (ServiceConstants.OTHER).equals(icpcPlacementRequestDto.getAgencyResponsible().getAgencyCounty())) {
			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyResponsible().getIdICPCPlcmntRqstEntity())) {
				// delete the record
				icpcPlacementDao.deleteICPCPlacementRequestEntity(icpcPlacementRequestDto.getIdICPCPlacementRequest(),
						CodesConstant.ICPCAGTP_20);
			}
			idEntityAgency = this.createNewAgency(icpcPlacementRequestDto, ServiceConstants.CD_RQST_ENTY_TYPE_20);
		}

		// 8.1. Update ICPC_REQUEST_PERSON_LINK table (Person Responsible)
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPersonResponsible())) {
			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPersonResponsible().getIdICPCRequestPersonLink())) {
				ICPCPersonDto personResponsible = icpcPlacementRequestDto.getPersonResponsible();
				icpcPlacementDao.updateICPCRequestPersonLink(personResponsible,
						icpcPlacementRequestDto.getIdLastUpdatePerson());
			} else {
				icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
						CodesConstant.ICPCPRTP_50);

				icpcPlacementDao.insertICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
						icpcPlacementRequestDto.getPersonResponsible().getIdPerson(), CodesConstant.ICPCPRTP_50,
						icpcPlacementRequestDto.getIdCreatedPerson(), 0L, icpcPlacementRequestDto.getNeicePersonId());
			}
		}

		// 8.2. Create entry in to the ICPC_REQUEST_PERSON_LINK table - Person
		// Financially Responsible
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPersonFinResponsible())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getPersonFinResponsible().getIdPerson())) {
			/*if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getPersonFinResponsible().getIdICPCRequestPersonLink())) {
				ICPCPersonDto personFinResponsible = icpcPlacementRequestDto.getPersonFinResponsible();
				icpcPlacementDao.updateICPCRequestPersonLink(personFinResponsible,
						icpcPlacementRequestDto.getIdLastUpdatePerson());
			} else {*/
			if(!ObjectUtils.isEmpty(icpcPlacementRequestDto.getIdICPCRequest())) {
				icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
						CodesConstant.ICPCPRTP_40);

				icpcPlacementDao.insertICPCRequestPersonLink(icpcPlacementRequestDto.getIdICPCRequest(),
						icpcPlacementRequestDto.getPersonFinResponsible().getIdPerson(), CodesConstant.ICPCPRTP_40,
						icpcPlacementRequestDto.getIdCreatedPerson(), 0L, icpcPlacementRequestDto.getNeicePersonId());
			}


			//}
		}

		// 9. Create entry in to the ICPC_PLACEMENT_REQUEST_ENTITY table (Agency
		// Financially Responsible)
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyFinResponsible())) {
			// Set the Placement request Type
			icpcPlacementRequestDto.getAgencyFinResponsible().setCdPlacementRqstType(CodesConstant.ICPCAGTP_10);
			// setAgencyEntityForSave
			if (Stream
					.of(CodesConstant.ICPCCRTP_10, CodesConstant.ICPCCRTP_40, CodesConstant.ICPCCRTP_50,
							CodesConstant.ICPCCRTP_60)
					.anyMatch(icpcPlacementRequestDto.getAgencyFinResponsible().getCdAgencyType()::equalsIgnoreCase)) {
				setAgencyEntityForSave(icpcPlacementRequestDto.getAgencyFinResponsible());
			}
		}
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyFinResponsible())
				&& icpcPlacementRequestDto.getAgencyFinResponsible().getAgencyCounty() != null
				&& !(ServiceConstants.OTHER)
						.equals(icpcPlacementRequestDto.getAgencyFinResponsible().getAgencyCounty())) {
			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyFinResponsible().getIdEntity()) && !ObjectUtils
					.isEmpty(icpcPlacementRequestDto.getAgencyFinResponsible().getIdICPCPlcmntRqstEntity())) {
				if ((ServiceConstants.YES).equalsIgnoreCase(icpcPlacementRequestDto.getIndSameAddress())) {
					icpcPlacementDao.updateICPCPlacementRequestEntity(
							icpcPlacementRequestDto.getAgencyFinResponsible().getIdICPCPlcmntRqstEntity(),
							icpcPlacementRequestDto.getAgencyResponsible().getIdEntity(),
							icpcPlacementRequestDto.getIdCreatedPerson());
				} else {
					icpcPlacementDao.updateICPCPlacementRequestEntity(
							icpcPlacementRequestDto.getAgencyFinResponsible().getIdICPCPlcmntRqstEntity(),
							icpcPlacementRequestDto.getAgencyFinResponsible().getIdEntity(),
							icpcPlacementRequestDto.getIdCreatedPerson());
				}

				if (!PRIVATE_AGENCY.equalsIgnoreCase(icpcPlacementRequestDto.getAgencyFinResponsible()
						.getAgencyState()) && !PRIVATE_AGENCY_OTHER.equals(icpcPlacementRequestDto.getAgencyFinResponsible()
						.getAgencyCounty())) {
					icpcPlacementDao.deleteICPCPlacementRequestEntityOther(
							icpcPlacementRequestDto.getAgencyFinResponsible()
									.getIdICPCPlcmntRqstEntity());
				}
			} else {
				// delete the record
				icpcPlacementDao.deleteICPCPlacementRequestEntity(icpcPlacementRequestDto.getIdICPCPlacementRequest(),
						CodesConstant.ICPCAGTP_10);

				if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyFinResponsible().getIdEntity())) {
					Long idICPCPlcmntRqstEntity = icpcPlacementDao.insertICPCPlacementRqstEnity(
							icpcPlacementRequestDto.getAgencyFinResponsible().getIdEntity(),
							icpcPlacementRequestDto.getIdICPCPlacementRequest(),
							icpcPlacementRequestDto.getAgencyFinResponsible().getCdPlacementRqstType(),
							icpcPlacementRequestDto.getIdCreatedPerson());

					icpcPlacementRequestDto.getAgencyFinResponsible().setIdICPCPlcmntRqstEntity(idICPCPlcmntRqstEntity);

					if ((ServiceConstants.YES).equalsIgnoreCase(icpcPlacementRequestDto.getIndSameAddress())) {
						icpcPlacementDao.updateICPCPlacementRequestEntity(idICPCPlcmntRqstEntity,
								icpcPlacementRequestDto.getAgencyResponsible().getIdEntity(),
								icpcPlacementRequestDto.getIdCreatedPerson());
					}

				}
			}

			if (PRIVATE_AGENCY.equalsIgnoreCase(icpcPlacementRequestDto.getAgencyFinResponsible()
					.getAgencyState()) && PRIVATE_AGENCY_OTHER.equals(icpcPlacementRequestDto.getAgencyFinResponsible()
					.getAgencyCounty())) {

				icpcPlacementDao.updateICPCPlacementRqstEnityOther(
						icpcPlacementRequestDto.getAgencyFinResponsible().getIdICPCPlcmntRqstEntity(),
						icpcPlacementRequestDto.getAgencyFinResponsible(),
						icpcPlacementRequestDto.getIdCreatedPerson());

			}

		} else if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyFinResponsible()) && (ServiceConstants.OTHER)
				.equals(icpcPlacementRequestDto.getAgencyFinResponsible().getAgencyCounty())) {
			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencyFinResponsible().getIdICPCPlcmntRqstEntity())) {
				// delete the record
				icpcPlacementDao.deleteICPCPlacementRequestEntity(icpcPlacementRequestDto.getIdICPCPlacementRequest(),
						CodesConstant.ICPCAGTP_10);
			}
			if ((ServiceConstants.YES).equalsIgnoreCase(icpcPlacementRequestDto.getIndSameAddress())) {
				icpcPlacementDao.insertICPCPlacementRqstEnity(idEntityAgency,
						icpcPlacementRequestDto.getIdICPCPlacementRequest(),
						icpcPlacementRequestDto.getAgencyFinResponsible().getCdPlacementRqstType(),
						icpcPlacementRequestDto.getIdCreatedPerson());
			} else {
				this.createNewAgency(icpcPlacementRequestDto, ServiceConstants.CD_RQST_ENTY_TYPE_10);
			}
		}

		// 10. Create entry in to the ICPC_PLACEMENT_REQUEST_ENTITY table
		// (Supervising Agency)
		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencySupervising()))
			icpcPlacementRequestDto.getAgencySupervising().setCdPlacementRqstType(CodesConstant.ICPCAGTP_30);

		//[PD-872] PD 91207: Deleting the previous Supervising agency records whenever CdSprrvsrySrvcs is not selected as ANOTHER_AGENCY_TO_SUPERVISE
		if(ObjectUtils.isEmpty(icpcPlacementRequestDto.getCdSprrvsrySrvcs())
				|| !CodesConstant.ICPCSPSR_10.equalsIgnoreCase(icpcPlacementRequestDto.getCdSprrvsrySrvcs()) ){
			icpcPlacementDao.deleteICPCPlacementRequestEntity(icpcPlacementRequestDto.getIdICPCPlacementRequest(),
					CodesConstant.ICPCAGTP_30);

		}  else if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencySupervising())
				&& !ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencySupervising()
				.getIdEntity())) {

			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencySupervising()
					.getIdEntity()) && !ObjectUtils
					.isEmpty(icpcPlacementRequestDto.getAgencySupervising()
							.getIdICPCPlcmntRqstEntity())) {
				icpcPlacementDao.updateICPCPlacementRequestEntity(
						icpcPlacementRequestDto.getAgencySupervising()
								.getIdICPCPlcmntRqstEntity(),
						icpcPlacementRequestDto.getAgencySupervising()
								.getIdEntity(),
						icpcPlacementRequestDto.getIdCreatedPerson());

				if (!ANOTHER_AGENCY_TO_SUPERVISE_OTHER.equals(icpcPlacementRequestDto.getAgencySupervising()
						.getAgencyState())) {
					icpcPlacementDao.deleteICPCPlacementRequestEntityOther(
							icpcPlacementRequestDto.getAgencySupervising()
									.getIdICPCPlcmntRqstEntity());
				}

			} else if (icpcPlacementRequestDto.getAgencySupervising()
					.getIdEntity() > 0) {
				icpcPlacementDao.deleteICPCPlacementRequestEntity(icpcPlacementRequestDto.getIdICPCPlacementRequest(),
						CodesConstant.ICPCAGTP_30);
				if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencySupervising()
						.getIdEntity())) {
					Long idICPCPlcmntRqstEntity = icpcPlacementDao.insertICPCPlacementRqstEnity(
							icpcPlacementRequestDto.getAgencySupervising()
									.getIdEntity(),
							icpcPlacementRequestDto.getIdICPCPlacementRequest(),
							icpcPlacementRequestDto.getAgencySupervising()
									.getCdPlacementRqstType(),
							icpcPlacementRequestDto.getIdCreatedPerson());

					icpcPlacementRequestDto.getAgencySupervising()
							.setIdICPCPlcmntRqstEntity(idICPCPlcmntRqstEntity);
				}
			}

			if (ANOTHER_AGENCY_TO_SUPERVISE_OTHER.equals(icpcPlacementRequestDto.getAgencySupervising()
					.getAgencyState())) {

				icpcPlacementDao.updateICPCPlacementRqstEnityOther(
						icpcPlacementRequestDto.getAgencySupervising().getIdICPCPlcmntRqstEntity(),
						icpcPlacementRequestDto.getAgencySupervising(),
						icpcPlacementRequestDto.getIdCreatedPerson());

			}

		} /*else if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencySupervising())
				&& (ServiceConstants.OTHER).equals(icpcPlacementRequestDto.getAgencySupervising()
				.getAgencyState())) {

			if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.getAgencySupervising()) && !ObjectUtils
					.isEmpty(icpcPlacementRequestDto.getAgencySupervising().getIdICPCPlcmntRqstEntity())) {
				// Delete the record.
				icpcPlacementDao.deleteICPCPlacementRequestEntity(icpcPlacementRequestDto.getIdICPCPlacementRequest(),
						CodesConstant.ICPCAGTP_30);
			}else {
				this.createNewAgency(icpcPlacementRequestDto, ServiceConstants.CD_RQST_ENTY_TYPE_30);
			}
		}*/

		// Call service to Invalidate and change status of the Placement Request
		if (CodesConstant.CEVTSTAT_PEND.equals(eventValueBean.getEventStatusCode())
				&& (!icpcPlacementRequestDto.isApprovalMode()
						|| icpcPlacementRequestDto.isApprovalModeForStageClosure())
				&& (!icpcPlacementRequestDto.isSaveAndSubmit())) {
			// Call CCMN05UI
			ApprovalCommonInDto approvalCommonInDto = populateApprovalDto(eventValueBean);
			approvalCommonService.callCcmn05uService(approvalCommonInDto);

			eventValueBean.setDtLastUpdate(caseUtils.getEvent(icpcPlacementRequestDto.getIdEvent()).getDtLastUpdate());

			eventValueBean.setCdEventStatus(CodesConstant.CEVTSTAT_PROC);
			eventValueBean.setReqFunctionCode(ServiceConstants.REQ_FUNC_CD_UPDATE);
			Long idStagePersonLinkPerson = 0L;
			// Get Primary Child for the Stage.
			if (icpcPlacementRequestDto.getChild() != null) {
				idStagePersonLinkPerson = icpcPlacementRequestDto.getChild().getIdPerson();
			}
//			postEventService.postEvent(eventValueBean, ServiceConstants.REQ_FUNC_CD_ADD, idStagePersonLinkPerson);
		} else {
			if (!ObjectUtils.isEmpty(eventValueBean.getEventStatusCode())) {
				icpcPlacementDao.updateEvent(icpcPlacementRequestDto.getIdEvent(), eventValueBean);
			}
		}

		// Update portal request status
		String portalStatus = null;

		if (!StringUtils.isEmpty(icpcPlacementRequestDto.getCdDecision())) {
			if ((CodesConstant.ICPCPLDC_10).equalsIgnoreCase(icpcPlacementRequestDto.getCdDecision())) {
				portalStatus = CodesConstant.ICPCRQSB_20;
			} else if ((CodesConstant.ICPCPLDC_20).equalsIgnoreCase(icpcPlacementRequestDto.getCdDecision())) {
				portalStatus = CodesConstant.ICPCRQSB_30;
			} else if ((CodesConstant.ICPCPLDC_30).equalsIgnoreCase(icpcPlacementRequestDto.getCdDecision())) {
				portalStatus = CodesConstant.ICPCRQSB_50;
			}
		}
		Stage stage = caseUtils.getStage(eventValueBean.getIdStage());
		Long idPersonPC = caseUtils.getPrimaryWorkerIdForStage(eventValueBean.getIdStage());
		String cdTask = ServiceConstants.EMPTY_STR;
		if ((CodesConstant.ICPCPLDC_10.equals(icpcPlacementRequestDto.getCdDecision())
				|| CodesConstant.ICPCPLDC_20.equals(icpcPlacementRequestDto.getCdDecision()))
				&& (!(CodesConstant.ICPCPLDC_10.equals(plcmntRqstDBValueBean.getCdDecision())
						&& !(CodesConstant.ICPCPLDC_20.equals(plcmntRqstDBValueBean.getCdDecision()))))) {
			// check if 100B has been created
			boolean present100B = icpcPlacementDao
					.checkCorresponding100BPresent(icpcPlacementRequestDto.getIdICPCPlacementRequest());
			if (!present100B) {
				String toDoDesc = ServiceConstants.EXPIRING_100A;
				String toDoLongDesc = toDoDesc;
				eventValueBean.setIdPerson(idPersonPC);
				String todoTask = getToDoTask(eventValueBean.getCdEventTask());
				if (stage != null && CodesConstant.CSTGTYPE_REG.equals(stage.getCdStageType())) {
					Date dueDate = DateUtils.addToDate(new Date(), 0, 5, 0);
					// A 100A for a SUB or ADO REG stage has a Decision of
					// 'Placement Approved'
					// or 'Placement Approved with Conditions' and a
					// corresponding 100B has not
					// been generated.
					createICPCToDo(toDoDesc, toDoLongDesc, todoTask, cdTask, dueDate, idPersonPC,
							icpcPlacementRequestDto.getIdCreatedPerson(), eventValueBean.getIdStage(),
							icpcPlacementRequestDto.getIdEvent());
				} else if (!ObjectUtils.isEmpty(stage) && CodesConstant.CSTGTYPE_CIC.equals(stage.getCdStageType())) {
					toDoDesc = ServiceConstants.NOT_RECEIVED_100B;
					toDoLongDesc = toDoDesc;
					Date dueDate = DateUtils.addToDate(new Date(), 0, 6, 0);
					// To Do A corresponding 100B has not been received on a
					// C-IC case
					// six months after the 100A is updated with a Decision of
					// 'Placement Approved' or 'Placement Approved with
					// Conditions'.
					createICPCToDo(toDoDesc, toDoLongDesc, todoTask, cdTask, dueDate, idPersonPC,
							icpcPlacementRequestDto.getIdCreatedPerson(), eventValueBean.getIdStage(),
							icpcPlacementRequestDto.getIdEvent());
				}

			}
		}
		String toDoDesc = ServiceConstants.NOT_APPROVED;
		icpcPlacementDao.endDateToDo(icpcPlacementRequestDto.getIdEvent(), toDoDesc,
				icpcPlacementRequestDto.getIdCreatedPerson());
		if (icpcPlacementRequestDto.isApprovalMode()) {
			if (0 != idPersonPC.compareTo(eventValueBean.getIdPerson())) {
				cdTask = eventValueBean.getCdEventTask();
				createICPCToDo(toDoDesc, ServiceConstants.APRV_CHANGE_TODO_DESC, ServiceConstants.CD_TODO_INFO_ALERT,
						cdTask, null, idPersonPC, icpcPlacementRequestDto.getIdCreatedPerson(),
						eventValueBean.getIdStage(), icpcPlacementRequestDto.getIdEvent());
			}
		}

		if (!ObjectUtils.isEmpty(icpcPlacementRequestDto.isSaveAndSubmit())
				&& icpcPlacementRequestDto.isSaveAndSubmit()) {
			List<Long> secondaryWorkers = icpcPlacementDao.getSecondaryWorkersAssigned(eventValueBean.getIdEvent());
			createICPCAlertsForSecondaryWorker(icpcPlacementRequestDto, secondaryWorkers);
		}

		return legacyEventsList;

	}

	/**
	 * Method Description: This method is used to create the new alert for Home Study created for Secondaries
	 *
 	 * @param icpcPlacementRequestDto
	 * @param secondaryWorkers
	 */
	private void createICPCAlertsForSecondaryWorker(ICPCPlacementRequestDto icpcPlacementRequestDto,
													List<Long> secondaryWorkers) {
		for (Long workerId : secondaryWorkers) {
			TodoDto todoDtoDtls = setValuesToTodoDtoAssignForAdd(workerId, icpcPlacementRequestDto,
					"New Home Study Request created for " + icpcPlacementRequestDto.getChild()
							.getNmFull());
			icpcPlacementDao.createAlert(todoDtoDtls);
		}
	}

	public TodoDto setValuesToTodoDtoAssignForAdd(Long workerId, ICPCPlacementRequestDto icpcPlacementRequestDto,
												  String alertDesc) {
		TodoDto todoDtoVal = new TodoDto();
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		date = cal.getTime();
		todoDtoVal.setCdTask(ServiceConstants.EMPTY_STRING);
		todoDtoVal.setCdTodoType(ServiceConstants.TODO_ACTIONS_ALERT);
		todoDtoVal.setDtTodoCompleted(date);
		todoDtoVal.setDtTodoDue(date);
		todoDtoVal.setIdTodoEvent(icpcPlacementRequestDto.getIdEvent());
		todoDtoVal.setIdTodoCase(icpcPlacementRequestDto.getIdCase());
		todoDtoVal.setIdTodoPersCreator(icpcPlacementRequestDto.getIdUser());
		todoDtoVal.setIdTodoStage(icpcPlacementRequestDto.getIdStage());
		todoDtoVal.setTodoLongDesc(ServiceConstants.EMPTY_STRING);
		todoDtoVal.setTodoDesc(alertDesc);
		todoDtoVal.setIdTodoPersAssigned(workerId);
		Long idPrimary = approvalDao.getPrimaryWorkerIdForStage(icpcPlacementRequestDto.getIdStage());
		todoDtoVal.setIdTodoPersWorker(idPrimary);
		todoDtoVal.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		return todoDtoVal;
	}

	/**
	 * 
	 * Method Name: getToDoTask Method Description:This method return cdTask
	 * 
	 * @param cdTask
	 * @return String
	 */
	private static String getToDoTask(String cdTask) {
		if (ServiceConstants.CD_TASK_SUB_PLCMNT_REQ.equals(cdTask))
			return ServiceConstants.CD_TODO_INFO_SUB_PLCMNT_REQ;
		if (ServiceConstants.CD_TASK_ADO_PLCMNT_REQ.equals(cdTask))
			return ServiceConstants.CD_TODO_INFO_ADO_PLCMNT_REQ;
		if (ServiceConstants.CD_TASK_SUB_PLCMNT_STTS.equals(cdTask))
			return ServiceConstants.CD_TODO_INFO_SUB_PLCMNT_STTS;
		if (ServiceConstants.CD_TASK_ADO_PLCMNT_STTS.equals(cdTask))
			return ServiceConstants.CD_TODO_INFO_ADO_PLCMNT_STTS;
		else
			return null;
	}

	/**
	 * 
	 * Method Name: createICPCToDo Method Description:This method create ICPC
	 * ToDo
	 * 
	 * @param toDoDesc
	 * @param toDoLongDesc
	 * @param cdTodoInfoType
	 * @param dtToDoDue
	 * @param idPrsnAssgn
	 * @param idUser
	 * @param idStage
	 * @param idEvent
	 * 
	 */
	public void createICPCToDo(String toDoDesc, String toDoLongDesc, String cdTodoInfoType, String cdTask,
			Date dtToDoDue, Long idPrsnAssgn, Long idUser, Long idStage, Long idEvent) {
		//Defect#13372 - Updated to call the todocommonfunction method to create an alert
		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
		MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
		todoCreateInDto.setServiceInputDto(new ServiceInputDto());
		mergeSplitToDoDto.setCdTodoCf(cdTodoInfoType);
		if(!ObjectUtils.isEmpty(dtToDoDue))
			mergeSplitToDoDto.setDtTodoCfDueFrom(dtToDoDue);
		mergeSplitToDoDto.setIdTodoCfPersCrea(idUser);
		mergeSplitToDoDto.setIdTodoCfStage(idStage);
		mergeSplitToDoDto.setIdTodoCfPersAssgn(idPrsnAssgn);
		mergeSplitToDoDto.setIdTodoCfPersWkr(idUser);
		if (!ObjectUtils.isEmpty(toDoDesc))
			mergeSplitToDoDto.setTodoCfDesc(toDoDesc);
		if (!ObjectUtils.isEmpty(toDoLongDesc))
			mergeSplitToDoDto.setTodoCfLongDesc(toDoLongDesc);
		if (idEvent > 0) {
			mergeSplitToDoDto.setIdTodoCfEvent(idEvent);
		}
		todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);

		commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);

	}

	/**
	 * 
	 * Method Name: createNewAgency Method Description:This method create new
	 * Agency.
	 * 
	 * @param ICPCPlacementRequestDto
	 * @return Long
	 */
	public Long createNewAgency(ICPCPlacementRequestDto icpcPlacementRequestDto, Integer rqstEntityType) {
		Integer agencyType = 0;
		ICPCAgencyDto newAgency = new ICPCAgencyDto();

		if (rqstEntityType == ServiceConstants.CD_RQST_ENTY_TYPE_10) {
			agencyType = ServiceConstants.CD_AGENCY_TYPE_10;
			newAgency = icpcPlacementRequestDto.getAgencyFinResponsible();
		} else if (rqstEntityType == ServiceConstants.CD_RQST_ENTY_TYPE_20) {
			agencyType = ServiceConstants.CD_AGENCY_TYPE_10;
			newAgency = icpcPlacementRequestDto.getAgencyResponsible();
		} else if (rqstEntityType == ServiceConstants.CD_RQST_ENTY_TYPE_30) {
			agencyType = ServiceConstants.CD_AGENCY_TYPE_20;
			newAgency = icpcPlacementRequestDto.getAgencySupervising();
		}

		Long idEntity = icpcPlacementDao.verifyAgencyExist(newAgency, agencyType.longValue());

		if (ObjectUtils.isEmpty(idEntity) || idEntity == ServiceConstants.Zero_Value) {
			// 1. Create Entity
			idEntity = icpcPlacementDao.insertEntity(newAgency.getAgencyOther(),
					icpcPlacementRequestDto.getIdCreatedPerson());
			// 2. Create Entity Address
			icpcPlacementDao.insertEntityAddress(idEntity, newAgency, icpcPlacementRequestDto.getIdCreatedPerson());
			// 3. Create Entity Phone
			if(!ObjectUtils.isEmpty(newAgency.getNbrPhone())){
				icpcPlacementDao.insertEntityPhone(idEntity, newAgency, icpcPlacementRequestDto.getIdCreatedPerson());
			}
			// 4. Create ICPC_AGENCY_TYPE
			icpcPlacementDao.insertICPCAgencyType(idEntity, agencyType.longValue(),
					icpcPlacementRequestDto.getIdCreatedPerson());
		}

		// 5. Insert into ICPC_PLACEMENT_REQUEST_ENTITY
		icpcPlacementDao.insertICPCPlacementRqstEnity(idEntity, icpcPlacementRequestDto.getIdICPCPlacementRequest(),
				newAgency.getCdPlacementRqstType(), icpcPlacementRequestDto.getIdCreatedPerson());

		return idEntity;
	}

	// ICPC Placement Request Selector

	/**
	 * Method Name:getListPlacementRequest Method Description: This method
	 * generates the list for PEND and APRV Status which has a Placement 100-A
	 * Record
	 * 
	 * @param ICPCPlacementReq
	 * @return ICPCPlacementRes
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ICPCPlacementRes getListPlacementRequest(ICPCPlacementReq icpcPlacementReq) {

		List<EventValueDto> vaildPlacementRequests = null;
		String legacynumber = null;
		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();

		vaildPlacementRequests = getValidPlacementRequests(icpcPlacementReq);

		if ((ObjectUtils.isEmpty(vaildPlacementRequests)) || ObjectUtils.isEmpty(vaildPlacementRequests.size())) {

			legacynumber = icpcPlacementDao.getIcpcLegacyNumber(icpcPlacementReq.getIdStage());

			if (!ObjectUtils.isEmpty(legacynumber)) {
				icpcPlacementRes.setIcpcLegacyNumber(legacynumber);

				icpcPlacementRes.setIcpcLegacyMessageByCode(ServiceConstants.MSG_ICPC_NO_100A_EXISTING_CONTINUE);
			} else {

				icpcPlacementRes.setIcpcLegacyNumberMessage(ServiceConstants.MSG_ICPC_NO_100A_EXISTING_CREATE_100A);
			}

		} else {

			icpcPlacementRes.setValidPlacementRequests(vaildPlacementRequests);
		}

		return icpcPlacementRes;
	}

	/**
	 * Method getValidPlacementRequests Method Description: This method
	 * generates the list for PEND and APRV Status which has a Placement 100-A
	 * Record
	 * 
	 * @param ICPCPlacementReq
	 * @return ICPCPlacementRes
	 */
	private List<EventValueDto> getValidPlacementRequests(ICPCPlacementReq icpcPlacementReq) {

		List<EventValueDto> validRequests = new ArrayList<EventValueDto>();
		List<EventValueDto> validPlacementRequests = new ArrayList<EventValueDto>();

		validRequests = icpcPlacementDao.getValidPlacementRequests(icpcPlacementReq);

		for (EventValueDto eventBean : validRequests) {
			// Set LastName to System if first and Last Name is null
			if (!StringUtil.isValid(eventBean.getNmCreatorsFirst())
					&& !StringUtil.isValid(eventBean.getNmCreatorsLast())) {
				eventBean.setNmCreatorsLast("System");
				eventBean.setNmCreatorsFirst(StringUtil.EMPTY_STRING);
			}
			validPlacementRequests.add(eventBean);

		}

		return validPlacementRequests;

	}

	/**
	 * Method Name: getIcpcDocument Method Description: get the document details
	 * for the given idICPCDocuments
	 * 
	 * @param idICPCDocuments
	 * @return List<ICPCDocumentDto>
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<ICPCDocumentDto> getIcpcDocument(List<Long> idICPCDocuments){

		List<ICPCDocumentDto> icpcDocumentDtos = icpcPlacementDao.getIcpcDocument(idICPCDocuments);

		byte[] doc = null;
		int size = 1024;
		int len = 0;
		String mimeType = null;

		if (CollectionUtils.isEmpty(icpcDocumentDtos)) {
			try{			
				for (ICPCDocumentDto icpcDocumentDto : icpcDocumentDtos) {
					InputStream ios = new ByteArrayInputStream(icpcDocumentDto.getDoc());
					if (ios instanceof ByteArrayInputStream) {
						size = ios.available();
						doc = new byte[size];
						len = ios.read(doc, 0, size);
					} else {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						doc = new byte[size];
						while ((len = ios.read(doc, 0, size)) != -1)
							bos.write(doc, 0, len);
						doc = bos.toByteArray();
					}
					mimeType = icpcDocumentDto.getSysMimeType();
					String fileName = icpcDocumentDto.getFileName();
					ICPCDocumentDto icpcDocDto = new ICPCDocumentDto();
					icpcDocDto.setDoc(doc);
					icpcDocDto.setSysMimeType(mimeType);
					icpcDocDto.setFileName(fileName);
					icpcDocDto.setIdICPCDocument(icpcDocumentDto.getIdICPCDocument());
					icpcDocumentDtos.add(icpcDocDto);
				}
			}catch (IOException e) {
				throw new ServiceLayerException(e.getMessage());
			}
		}

		return icpcDocumentDtos;

	}

	/**
	 * 
	 */
	@Override
	public ICPCPlacementRes savePlacementStatus(ICPCPlacementReq icpcPlacementReq) {
		ICPCPlacementStatusDto icpcPlacementStatusDto = icpcPlacementReq.getIcpcPlacementStatusDto();
		// Create new Event for 100B
		EventValueDto reqEvent = icpcPlacementStatusDto.getReqEvent();
		String nmPrsnRsrcPlcmnt = null;

		if (icpcPlacementStatusDto.getPlcmntResource() != null
				&& icpcPlacementStatusDto.getPlcmntResource().getNmResource() != null) {
			nmPrsnRsrcPlcmnt = icpcPlacementStatusDto.getPlcmntResource().getNmResource();
		} else if (icpcPlacementStatusDto.getPlcmntPersonLst() != null) {
			nmPrsnRsrcPlcmnt = icpcPlacementStatusDto.getPlcmntPersonLst().get(0).getNmFull();
		}

		String cdCareType = icpcPlacementDao.getCareType(icpcPlacementStatusDto.getIdICPCPlacementRequest());
		String eventDescription = lookupDao.simpleDecodeSafe(CodesConstant.ICPCCRTP, cdCareType) + " "
				+ nmPrsnRsrcPlcmnt;
		reqEvent.setEventDescr(eventDescription);
		reqEvent.setCdEventType(CodesConstant.CEVNTTYP_ICB);
		reqEvent.setDtEventCreated(new Date());

		Long idStagePersonLinkPerson = null;
		if (icpcPlacementStatusDto != null && icpcPlacementStatusDto.getChild() != null) {
			idStagePersonLinkPerson = icpcPlacementStatusDto.getChild().getIdPerson();
		}
		reqEvent.setCdEventStatus(CodesConstant.CEVTSTAT_PROC);
		//Modified the code for Warranty defect 11529
		Long intakeStageId;
		intakeStageId = icpcPlacementDao.retrieveIntakeStageId(reqEvent.getIdCase(), CodesConstant.CSTAGES_INT);
		Long  idIcpcSubmission= icpcPlacementDao.getIdICPCSubmission(intakeStageId);
		Long idAppEvent = null;
		if (!ObjectUtils.isEmpty(idIcpcSubmission)) {
			idAppEvent = postEventService.postEvent(reqEvent, ServiceConstants.REQ_FUNC_CD_ADD,
					idStagePersonLinkPerson);

			// 2. Create entry in to Placement Request table
			//todo:insert for incoming and outgoing agency
			Long idIcpcRequest = icpcPlacementDao.insertICPCRequest(idIcpcSubmission, 0L, CodesConstant.ICPCFMTP_B,
					icpcPlacementStatusDto.getCdReceivingState(), icpcPlacementStatusDto.getCdSendingState(),
					icpcPlacementStatusDto.getIdLastUpdatePerson(), icpcPlacementStatusDto.getCdSendingAgency(),
					icpcPlacementStatusDto.getCdReceivingAgency());

			// Create Entry into ICPC Event Link table.
			icpcPlacementDao.insertICPCEventLink(idIcpcRequest, idAppEvent, reqEvent.getIdCase(), // eventValBean.getCaseId(),
					icpcPlacementStatusDto.getIdLastUpdatePerson(), icpcPlacementStatusDto.getNeiceCaseId());

			// Create Entry for Primary child
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getChild())
					&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getChild().getIdPerson())) {
				ICPCPersonDto child = icpcPlacementStatusDto.getChild();
				icpcPlacementDao.insertICPCRequestPersonLink(idIcpcRequest, child.getIdPerson(),
						CodesConstant.ICPCPRTP_80, icpcPlacementStatusDto.getIdLastUpdatePerson(), 0L,
						child.getNeicePersonId());
			}

			// Create ICPC Placement Status record
			icpcPlacementStatusDto.setIdICPCRequest(idIcpcRequest);
			icpcPlacementStatusDto.setIdICPCPlacementRequest(icpcPlacementStatusDto.getIdICPCPlacementRequest());
			icpcPlacementStatusDto.setIdCreatedPerson(icpcPlacementStatusDto.getIdLastUpdatePerson());
			icpcPlacementDao.insertPlacementStatus(icpcPlacementStatusDto);

			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getMother())
					&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getMother()
					.getIdPerson())) {
				ICPCPersonDto mother = icpcPlacementStatusDto.getMother();
				icpcPlacementDao.insertICPCRequestPersonLink(idIcpcRequest, mother.getIdPerson(),
						CodesConstant.ICPCPRTP_30, icpcPlacementStatusDto.getIdLastUpdatePerson(), 0L,
						mother.getNeicePersonId());
			}
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getFather())
					&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getFather()
					.getIdPerson())) {
				ICPCPersonDto father = icpcPlacementStatusDto.getFather();
				icpcPlacementDao.insertICPCRequestPersonLink(idIcpcRequest, father.getIdPerson(),
						CodesConstant.ICPCPRTP_10, icpcPlacementStatusDto.getIdLastUpdatePerson(), 0L,
						father.getNeicePersonId());
			}
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getGuardian())
					&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getGuardian()
					.getIdPerson())) {
				ICPCPersonDto guardian = icpcPlacementStatusDto.getGuardian();
				Long idPersonRelation = null;
				if (guardian.getIdPerson() != null) {
					FTPersonRelationDto relation = getPersonRelation(idStagePersonLinkPerson, guardian.getIdPerson());
					if (relation != null) {
						idPersonRelation = relation.getIdPersonRelation();
					}
				}
				icpcPlacementDao.insertICPCRequestPersonLink(idIcpcRequest, guardian.getIdPerson(),
						CodesConstant.ICPCPRTP_20, icpcPlacementStatusDto.getIdLastUpdatePerson(), idPersonRelation,
						guardian.getNeicePersonId());
			}

			if (!CollectionUtils.isEmpty(icpcPlacementStatusDto.getPlcmntPersonLst())) {

				List<IcpcRequestPersonLink> icpcRequestPersonLinks = icpcPlacementDao.getIcpcRequestPersonLinks(
						icpcPlacementStatusDto.getIdICPCPlacementRequest());

				Map<Long, String> placementCodeMap = new HashMap<>();

				if (!CollectionUtils.isEmpty(icpcRequestPersonLinks)) {
					for (IcpcRequestPersonLink icpcRequestPersonLink : icpcRequestPersonLinks) {

						placementCodeMap.put(icpcRequestPersonLink.getPerson()
								.getIdPerson(), icpcRequestPersonLink.getCdPlacementCode());

					}
				}

				for (ICPCPersonDto icpcPersonDto : icpcPlacementStatusDto.getPlcmntPersonLst()) {
					if (!ObjectUtils.isEmpty(icpcPersonDto.getIdPerson())) {
						ICPCPersonDto plcmntPerson = icpcPersonDto;
						icpcPlacementDao.insertICPCRequestPersonLink(idIcpcRequest, plcmntPerson.getIdPerson(),
								CodesConstant.ICPCPRTP_60, icpcPlacementStatusDto.getIdLastUpdatePerson(), 0L,
								plcmntPerson.getNeicePersonId(), placementCodeMap.get(plcmntPerson.getIdPerson()));
					}
				}
			}
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getPlcmntResource())
					&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getPlcmntResource().getIdResource())) {
				ICPCResourceDto plcmntResource = icpcPlacementStatusDto.getPlcmntResource();
				icpcPlacementDao.insertICPCRequestResourceLink(idIcpcRequest, plcmntResource.getIdResource(),
						icpcPlacementStatusDto.getIdCreatedPerson(), plcmntResource.getIdResourceNeice());
			}
			// 8. Create entry in to the ICPC_REQUEST_ENCLOSURE table
			Long idEventEnclosure = idAppEvent;
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getCdEnclosure())) {
				icpcPlacementStatusDto.getCdEnclosure().stream().forEach(o -> icpcPlacementDao
						.insertICPCRequestEnclosure(idEventEnclosure, o, icpcPlacementStatusDto.getIdLastUpdatePerson()));
			}

			// if it exists for '100A Placement Resource expiring in 30 days.' &
			// '100B not received; close the Case.'
			// event ID is for the corresponding 100A
			Long idEvent = icpcPlacementDao.getEventFromRequest(icpcPlacementStatusDto.getIdICPCPlacementRequest());
			if (idEvent > 0) {
				icpcPlacementDao.endDateToDo(idEvent, ServiceConstants.EXPIRING_100A, icpcPlacementReq.getIdUser());
				icpcPlacementDao.endDateToDo(idEvent, ServiceConstants.NOT_RECEIVED_100B, icpcPlacementReq.getIdUser());
			}
			
			//Defect#13372 - Added code to create an alert when a new 100B placement status submitted 
			//and alert will go to the primary and secondary worker assigned to the stage.
			List<Long> assignedWorkersList =
					workLoadDao
					.getAssignedWorkersForStage(icpcPlacementStatusDto.getReqEvent().getIdStage());
			assignedWorkersList.stream()
					.forEach(o -> createICPCToDo(TO_DO_DESC, TO_DO_DESC, CD_TODO_INFO_ALERT,
							icpcPlacementStatusDto.getReqEvent().getCdEventTask(), null, o,
							icpcPlacementStatusDto.getIdLastUpdatePerson(),
							icpcPlacementStatusDto.getReqEvent().getIdStage(), idEventEnclosure));
		}
		ICPCPlacementRes res = new ICPCPlacementRes();
		res.setIdEvent(idAppEvent);
		return res;

	}

	/**
	 * Method Name: populateApprovalDto Method Description: this method is to
	 * populate ApprovalCommonInDto
	 * 
	 * @param eventBean
	 * @return ApprovalCommonInDto
	 */
	private ApprovalCommonInDto populateApprovalDto(EventValueDto eventBean) {

		ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();

		approvalCommonInDto.setUserId(String.valueOf(eventBean.getIdPerson()));
		approvalCommonInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

		approvalCommonInDto.setIdEvent(eventBean.getIdEvent());

		return approvalCommonInDto;
	}

	/**
	 * 
	 * Method Name: getIcpcDocument Method Description: this method is to update
	 * ICPC placement status report detail page
	 *
	 * @param icpcEmailDocumentReq
	 * @return ICPCDocumentRes
	 */
	@Override
	public ICPCPlacementRes updatePlacementStatus(ICPCPlacementReq icpcPlacementReq) {

		ICPCPlacementStatusDto icpcPlacementStatusDto = icpcPlacementReq.getIcpcPlacementStatusDto();
		EventValueDto eventBean = icpcPlacementStatusDto.getReqEvent();

		String nmPrsnRsrcPlcmnt = null;

		if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getPlcmntResource())
				&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getPlcmntResource().getNmResource())) {
			nmPrsnRsrcPlcmnt = icpcPlacementStatusDto.getPlcmntResource().getNmResource();
		} else if (!CollectionUtils.isEmpty(icpcPlacementStatusDto.getPlcmntPersonLst())) {
			nmPrsnRsrcPlcmnt = icpcPlacementStatusDto.getPlcmntPersonLst().get(0).getNmFull();
		}

		String eventDescription = lookupDao.simpleDecodeSafe(CodesConstant.ICPCCRTP,
				icpcPlacementDao.getCareType(icpcPlacementStatusDto.getIdICPCPlacementRequest())) + " "
				+ nmPrsnRsrcPlcmnt;

		EventValueDto eventValBean = icpcPlacementStatusDto.getReqEvent();
		eventValBean.setEventDescr(eventDescription);
		eventValBean.setEventDescr(eventValBean.getEventDescr());
		eventValBean.setEventStatusCode(eventValBean.getEventStatusCode());
		eventValBean.setCdEventType(eventValBean.getCdEventType());

		// icpcPlacementDao.updateEvent( icpcPlacementStatusDto.getIdEvent(),
		// eventValBean );
		icpcPlacementDao.updateIcpcPlacementStatus(icpcPlacementStatusDto);

		// 4. Update ICPC_REQUEST_PERSON_LINK table (Mother)
		if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getMother())) {
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getMother().getIdICPCRequestPersonLink())) {
				if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getMother().getIdPerson())
						&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getMother().getIdPerson())) {
					ICPCPersonDto mother = icpcPlacementStatusDto.getMother();
					icpcPlacementDao.updateICPCRequestPersonLink(mother,
							icpcPlacementStatusDto.getIdLastUpdatePerson());
				} else {
					icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
							CodesConstant.ICPCPRTP_30);
				}
			} else if (icpcPlacementStatusDto.getMother().getIdPerson() != null
					&& !("").equals(icpcPlacementStatusDto.getMother().getIdPerson())) {
				icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
						CodesConstant.ICPCPRTP_30);

				icpcPlacementDao.insertICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
						icpcPlacementStatusDto.getMother().getIdPerson(), CodesConstant.ICPCPRTP_30,
						icpcPlacementStatusDto.getIdCreatedPerson(), 0l, icpcPlacementStatusDto.getNeicePersonId());
			}
			//PD 89881 : 100b parents section reverts to UNKNOWN
			else if (icpcPlacementStatusDto.getMother().getIdPerson() == null) {
				icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
						CodesConstant.ICPCPRTP_30);
			}
		}

		// 5. Update ICPC_REQUEST_PERSON_LINK table (Father)
		if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getFather())) {
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getFather().getIdICPCRequestPersonLink())) {
				if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getFather().getIdPerson())
						&& !("").equals(icpcPlacementStatusDto.getFather().getIdPerson())) {
					ICPCPersonDto father = icpcPlacementStatusDto.getFather();
					icpcPlacementDao.updateICPCRequestPersonLink(father,
							icpcPlacementStatusDto.getIdLastUpdatePerson());
				} else {
					icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
							CodesConstant.ICPCPRTP_10);
				}
			} else if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getFather())
					&& !ObjectUtils.isEmpty((icpcPlacementStatusDto.getFather().getIdPerson()))) {
				icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
						CodesConstant.ICPCPRTP_10);

				icpcPlacementDao.insertICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
						icpcPlacementStatusDto.getFather().getIdPerson(), CodesConstant.ICPCPRTP_10,
						icpcPlacementStatusDto.getIdCreatedPerson(), 0l, icpcPlacementStatusDto.getNeicePersonId());
			}
			//PD 89881 : 100b parents section reverts to UNKNOWN
			else if (icpcPlacementStatusDto.getFather().getIdPerson() == null) {
				icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
						CodesConstant.ICPCPRTP_10);
			}
		}

		if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getGuardian())
				&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getGuardian().getIdPerson())) {
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getGuardian().getIdICPCRequestPersonLink())) {
				ICPCPersonDto guardian = icpcPlacementStatusDto.getGuardian();
				icpcPlacementDao.updateICPCRequestPersonLink(guardian, icpcPlacementStatusDto.getIdLastUpdatePerson());
			} else {
				icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
						CodesConstant.ICPCPRTP_20);
				Long idPersonRelation = 0l;
				if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getGuardian().getRelation()))
					idPersonRelation = icpcPlacementStatusDto.getGuardian().getRelation().getIdPersonRelation();
				icpcPlacementDao.insertICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
						icpcPlacementStatusDto.getGuardian().getIdPerson(), CodesConstant.ICPCPRTP_20,
						icpcPlacementStatusDto.getIdCreatedPerson(), idPersonRelation, icpcPlacementStatusDto.getNeicePersonId());
			}
		}
		// Compact Termination reason is not Legal Custody/Guardianship
		// Awarded or Returned, delete Guardian
		if (!(CodesConstant.ICPCCMTR_60).equals(icpcPlacementStatusDto.getCdCompactTermRsn())) {
			icpcPlacementDao.deleteICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
					CodesConstant.ICPCPRTP_20);
		}

		if (!CollectionUtils.isEmpty(icpcPlacementStatusDto.getPlcmntPersonLst())) {
			for(ICPCPersonDto icpcPersonDto : icpcPlacementStatusDto.getPlcmntPersonLst()) {
				if (!ObjectUtils.isEmpty(icpcPersonDto.getIdICPCRequestPersonLink())) {
					ICPCPersonDto plcmntPerson = icpcPersonDto;
					icpcPlacementDao.updateICPCRequestPersonLink(plcmntPerson,
							icpcPlacementStatusDto.getIdLastUpdatePerson());
				} else {
					icpcPlacementDao.insertICPCRequestPersonLink(icpcPlacementStatusDto.getIdICPCRequest(),
							icpcPersonDto.getIdPerson(), CodesConstant.ICPCPRTP_20,
							icpcPlacementStatusDto.getIdCreatedPerson(), 0l, icpcPersonDto.getNeicePersonId());
				}
			}
		}

		// 2. Delete Enclosure table
		icpcPlacementDao.deleteICPCRequestEnclosure(eventBean.getIdEvent());

		// 3. Create entry in to the ICPC_REQUEST_ENCLOSURE table
		if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getCdEnclosure())) {
			icpcPlacementStatusDto.getCdEnclosure().stream()
					.forEach(o -> icpcPlacementDao.insertICPCRequestEnclosure(eventBean.getIdEvent(), o,
							icpcPlacementStatusDto.getIdLastUpdatePerson()));
		}

		// set Old/New Address if it is not already set
		if (!CollectionUtils.isEmpty(icpcPlacementStatusDto.getPlcmntPersonLst())) {
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getOldAddress())
					&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getOldAddress().getIdPrsnAddress())
					&& ObjectUtils.isEmpty(icpcPlacementStatusDto.getOldAddress().getIdICPCStatPersAddrLink())) {
				icpcPlacementDao.insertIcpcPlcmntStatPerAddr(icpcPlacementStatusDto.getIdICPCPlacementStatus(),
						icpcPlacementStatusDto.getOldAddress().getIdPrsnAddress(), CodesConstant.ICPCADTP_20,
						icpcPlacementStatusDto.getIdLastUpdatePerson());
			}
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getNewAddress())
					&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getNewAddress().getIdPrsnAddress())
					&& ObjectUtils.isEmpty(icpcPlacementStatusDto.getNewAddress().getIdICPCStatPersAddrLink())) {
				icpcPlacementDao.insertIcpcPlcmntStatPerAddr(icpcPlacementStatusDto.getIdICPCPlacementStatus(),
						icpcPlacementStatusDto.getNewAddress().getIdPrsnAddress(), CodesConstant.ICPCADTP_10,
						icpcPlacementStatusDto.getIdLastUpdatePerson());
			}
		} else if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getPlcmntResource())) {
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getOldAddress())
					&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getOldAddress().getIdPrsnAddress())
					&& ObjectUtils.isEmpty(icpcPlacementStatusDto.getOldAddress().getIdICPCStatPersAddrLink())) {
				icpcPlacementDao.insertIcpcPlcmntStatResAddr(icpcPlacementStatusDto.getIdICPCPlacementStatus(),
						icpcPlacementStatusDto.getOldAddress().getIdPrsnAddress(), CodesConstant.ICPCADTP_20,
						icpcPlacementStatusDto.getIdLastUpdatePerson());
			}
			if (!ObjectUtils.isEmpty(icpcPlacementStatusDto.getNewAddress())
					&& !ObjectUtils.isEmpty(icpcPlacementStatusDto.getNewAddress().getIdPrsnAddress())
					&& ObjectUtils.isEmpty(icpcPlacementStatusDto.getNewAddress().getIdICPCStatPersAddrLink())) {
				icpcPlacementDao.insertIcpcPlcmntStatResAddr(icpcPlacementStatusDto.getIdICPCPlacementStatus(),
						icpcPlacementStatusDto.getNewAddress().getIdPrsnAddress(), CodesConstant.ICPCADTP_10,
						icpcPlacementStatusDto.getIdLastUpdatePerson());
			}
		}

		// Call service to Invalidate and change status of the Placement Request

		if (CodesConstant.CEVTSTAT_PEND.equals(eventBean.getEventStatusCode())
				&& (!icpcPlacementStatusDto.isApprovalMode() || icpcPlacementStatusDto.isApprovalModeForStageClosure())
				&& (!icpcPlacementStatusDto.isSaveAndSubmit())) {
			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			approvalCommonInDto.setIdEvent(eventBean.getIdEvent());
			approvalService.callCcmn05uService(approvalCommonInDto);
		} else {
			if (!ObjectUtils.isEmpty(eventValBean.getEventStatusCode())) {
				// icpcPlacementDao.updateEvent(icpcPlacementStatusDto.getIdEvent(),
				// eventValBean);
			}
		}

		// remove Reject ToDo - this will remove only assigned to the logged in
		// person
		String toDoDesc = "Submitted request not Approved, you must resubmit.";
		icpcPlacementDao.endDateToDo(icpcPlacementStatusDto.getIdEvent(), toDoDesc,
				icpcPlacementStatusDto.getIdCreatedPerson());

		return null;

	}

	/**
	 * 
	 * Method Name: getPersonRelation Method Description:
	 * 
	 * @param commonHelperReq
	 * @return
	 */

	@Override
	public FTRelationshipRes getPersonRelation(CommonHelperReq commonHelperReq) {
		ArrayList<FTPersonRelationBean> relations = tfRelationshipDao
				.selectRelationshipsWith2Persons(commonHelperReq.getIdPerson(), commonHelperReq.getIdPerson2());
		FTRelationshipRes res = new FTRelationshipRes();
		res.setFtPersonRelationBeanList(relations);
		return res;
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
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonStringRes updateIcpcEmailDtlLog(ICPCEmailLogReq icpcEmailLogReq) {
		return icpcPlacementDao.updateIcpcEmailDtlLog(icpcEmailLogReq);
	}

	/**
	 * Method Name: getRecentAprvEvent Method Description:This method is used to
	 * retrieve most recent approved CPS idEvent.
	 * 
	 * @param idStage
	 * @return Long
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public Long getRecentAprvEvent(Long idStage) {
		Long idEvent = icpcPlacementDao.getRecentAprvEvent(idStage);
		return idEvent;
	}

	/**
	 * 
	 * Method Name: deletePlacementRequest Method Description:Delete the ICPC
	 * Placement Request details
	 * 
	 * @param ICPCPlacementRequestDBDto
	 * @return Long
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, rollbackFor = Exception.class)
	public Long deletePlacementRequest(ICPCPlacementRequestDBDto icpcPlacementRequestDBDto) {
		//artf254020: ICPC Request id missing to delete the request record
		ICPCPlacementRequestDto placementRequestValueBean = icpcPlacementRequestDBDto.getPlacementRequestValueBean();
		if (placementRequestValueBean.getIdICPCRequest() == null
				&& placementRequestValueBean.getICPCPlacementRequestJsonStr() != null) {
			ICPCPlacementRequestDto icpcPlacementRequestDto = (ICPCPlacementRequestDto) JSONUtil.jsonStringToObject(
					placementRequestValueBean.getICPCPlacementRequestJsonStr(),
					ICPCPlacementRequestDto.class);
			placementRequestValueBean.setIdICPCRequest(icpcPlacementRequestDto.getIdICPCRequest());
		}
		// Delete the ICPC Placement Related Details
		icpcPlacementDao.deleteICPCRequest(placementRequestValueBean);

		return placementRequestValueBean.getIdEvent();

	}

	@Override
	public ListTransmissionRes getTransmissionLst(Long idStage) {
		ListTransmissionRes res = new ListTransmissionRes();
		res.setIcpcTransmissionDto(icpcPlacementDao.getTransmissionLst(idStage));
		return res;
	}

	@Override
	public ListTransmissionRes getTransmissionChildLst(Long idIcpcTransmittal) {
		ListTransmissionRes res = new ListTransmissionRes();
		res.setIcpcTransmissionDto(icpcPlacementDao.getTransmissionChildLst(idIcpcTransmittal));
		return res;
	}

	/**
	 * @param idIcpcTransmittal
	 * @param idPersonNeice
	 * @return
	 */
	@Override
	public ListTransmissionRes getTransmissionAttachments(Long idIcpcTransmittal, Long idPersonNeice) {
		ListTransmissionRes res = new ListTransmissionRes();
		res.setIcpcTransmissionDto(icpcPlacementDao.getTransmissionAttachemnts(idIcpcTransmittal,idPersonNeice));
		return res;
	}

	/**
	 * @param idAttachment
	 * @return
	 */
	@Override
	public ListTransmissionRes getTransmissionAttachment(Long idAttachment) {
		ListTransmissionRes res = new ListTransmissionRes();
		res.setDto(icpcPlacementDao.getTransmissionAttachemnt(idAttachment));
		return res;
	}

	/**
	 * Sets the agency entity for save.
	 *
	 * @param agencyResponsible
	 *            the agency responsible
	 * @param cdAgencyType
	 *            the agency type
	 */
	private void setAgencyEntityForSave(ICPCAgencyDto agencyResponsible) {
		Long agencyCounty = !ObjectUtils.isEmpty(agencyResponsible)
				&& !ObjectUtils.isEmpty(agencyResponsible.getAgencyCounty())
						? Long.parseLong(agencyResponsible.getAgencyCounty()) : 0L;
		agencyResponsible.setIdEntity(agencyCounty);
	}

	@Override
	public void saveNeiceTransmission(ICPCPlacementReq icpcPlacementReq) {

		/*NeiceTransmittalDto neiceTransmittalDto = icpcNeiceTransmissionDao.getNeiceDetails(
				icpcPlacementReq.getIdTransmittal(), icpcPlacementReq.getIdNeiceTransmittalPerson());*/

		ICPCTransmissionDto neiceTransmittalDataDto = icpcNeiceTransmissionDao.fetchNeiceData(
				icpcPlacementReq.getIdTransmittal(), icpcPlacementReq.getIdNeiceTransmittalPerson());

		if (!ObjectUtils.isEmpty(neiceTransmittalDataDto) && !ObjectUtils.isEmpty(
				neiceTransmittalDataDto.getIdPersonNeice()) && !ObjectUtils.isEmpty(
				neiceTransmittalDataDto.getDocumentData())) {

			NEICETransmittalDocumentType neiceTransmittalDocumentType = (NEICETransmittalDocumentType) JSONUtil.jsonStringToObject(
					new String(neiceTransmittalDataDto.getDocumentData()),
					NEICETransmittalDocumentType.class);

			ChildType childType = neiceTransmittalDocumentType.getChildren()
					.stream()
					.filter(ct -> neiceTransmittalDataDto.getIdPersonNeice()
							.equals(ct.getChildAugmentation()
									.getChildNEICEIdentification()
									.getIdentificationID()))
					.findFirst()
					.orElse(null);

			if (!ObjectUtils.isEmpty(childType)) {

				NEICEDocument100AType neiceDocument100AType = childType.getHomeStudyRequest().getNeiceDocument100A();

				String typeOfCareCode = lookupDao.encode(ICPCCodeType.TYPE_OF_CARE.neiceType(),
						neiceDocument100AType.getPlacementResource()
								.getPlacementCategoryCode()
								.value());

				String eventDescription = lookupDao.decode(ICPCCodeType.TYPE_OF_CARE.impactType(), typeOfCareCode);

				StagePersonLinkDto personLinkDto = stagePersonLinkDao.getPrimaryChildIdByIdStage(icpcPlacementReq.getIdStage());

				Event event = icpcNeiceTransmissionDao.createEventDtls(icpcPlacementReq.getIdStage(),
						icpcPlacementReq.getIdUser(), eventDescription, icpcPlacementReq.getIdCase(),
						personLinkDto.getIdPerson());

				IcpcSubmission icpcSubmission = icpcNeiceTransmissionDao.saveIcpcSubmission(icpcPlacementReq.getIdTransmittal(),
						icpcPlacementReq.getIdUser(), icpcPlacementReq.getIdStage());

				IcpcRequest icpcRequest = icpcNeiceTransmissionDao.saveIcpcRequest(neiceDocument100AType, icpcSubmission,
						icpcPlacementReq.getIdUser());

				IcpcEventLink icpcEventLink = icpcNeiceTransmissionDao.saveIcpcEventLink(icpcPlacementReq.getIdCase(),
						icpcPlacementReq.getIdUser(), neiceTransmittalDocumentType.getTransmittalSummaryInformation()
								.getCaseNEICEIdentification()
								.getIdentificationID(), icpcRequest.getIdIcpcRequest(), event);

				NeiceCaseLinkDto neiceCaseLinkDto = icpcNeiceTransmissionDao.fetchNeiceCaseLinkById(
						neiceTransmittalDocumentType.getTransmittalSummaryInformation()
								.getCaseNEICEIdentification()
								.getIdentificationID());

				Date homeStudyDueDt = Optional.ofNullable(neiceTransmittalDocumentType.getRequestDueDate())
						.map(DateType::getDateTime)
						.map(GregorianCalendar::getTime)
						.orElse(null);

				IcpcPlacementRequest plcmntRequest = icpcNeiceTransmissionDao.saveIcpcPlcmtRequest(
						neiceTransmittalDocumentType.getDocumentCreationDate()
								.getDate(), childType, icpcPlacementReq.getIdUser(), icpcRequest.getIdIcpcRequest(),
						event, homeStudyDueDt);

				icpcNeiceTransmissionDao.saveIcpcRequestPersonLink(icpcPlacementReq.getIdUser(), icpcRequest,
						personLinkDto.getIdPerson(), ServiceConstants.ICPCPRTP_80,
						neiceTransmittalDataDto.getIdPersonNeice(), null);

				if (!ObjectUtils.isEmpty(neiceCaseLinkDto)) {
					icpcNeiceTransmissionDao.saveOrUpdateNeicePersonResource(neiceCaseLinkDto.getIdNeiceCaseLink(),
							neiceTransmittalDataDto.getIdPersonNeice(), personLinkDto.getIdPerson(), "C",
							icpcPlacementReq.getIdUser());
				}

				if (!CollectionUtils.isEmpty(icpcPlacementReq.getAttcnmntsSelected())) {
					for (Long attachmentId : icpcPlacementReq.getAttcnmntsSelected()) {
						ICPCTransmissionDto attachmentDto = icpcPlacementDao.getTransmissionAttachemnt(attachmentId);
						IcpcDocument icpcDocument = icpcNeiceTransmissionDao.saveIcpcDocument(attachmentDto, icpcRequest,
								icpcSubmission, icpcPlacementReq, personLinkDto);
						icpcNeiceTransmissionDao.saveIcpcFileStorage(icpcDocument, attachmentDto, icpcPlacementReq);
					}
				}

				boolean isPlacementTypeResource = true;
				boolean agencyResponsiblePerson = false;
				boolean agencyFinResponsiblePerson = false;
				String caregiverResourceName = null;
				String placementResourceNeiceId = neiceDocument100AType.getPlacementResource()
						.getEntity()
						.getEntityAugmentation()
						.getPlacementResourceID()
						.getIdentificationID();

				List<StagePrincipalDto> stagePrincipalDtos = stagePersonLinkDao.getStagePrincipalByIdStageType(
						icpcPlacementReq.getIdStage(), ServiceConstants.PRN_TYPE);

				if (!CollectionUtils.isEmpty(stagePrincipalDtos) && stagePrincipalDtos.stream()
						.anyMatch(dto -> !ServiceConstants.PRIMARY_CHILD.equals(dto.getCdStagePersRole()))) {

					// Parents
					if (!CollectionUtils.isEmpty(childType.getParents())) {

						for (PersonType personType : childType.getParents()) {

							StagePrincipalDto stagePrincipalDto = getMatchedStagePrincipal(stagePrincipalDtos, Optional.ofNullable(personType));

							if (!ObjectUtils.isEmpty(stagePrincipalDto)) {

								String cdPersonType = Optional.ofNullable(personType)
										.map(PersonType::getParentChildAssociation)
										.map(ParentChildAssociationType::getAssociationDescriptionText)
										.filter("mother"::equalsIgnoreCase)
										.map(t -> ServiceConstants.ICPCPRTP_30)
										.orElse(ServiceConstants.ICPCPRTP_10);

								icpcNeiceTransmissionDao.saveIcpcRequestPersonLink(icpcPlacementReq.getIdUser(),
										icpcRequest, stagePrincipalDto.getIdPerson(), cdPersonType, null, null);
							}
						}
					}

					// Agency Responsible
					StagePrincipalDto agencyResponsiblePersonDto = getMatchedStagePrincipal(stagePrincipalDtos,
							Optional.ofNullable(neiceDocument100AType.getIntendedPlacementPlanningResponsibleParty())
									.map(ResourceDetailType::getEntity)
									.map(EntityType::getEntityPerson));

					if (!ObjectUtils.isEmpty(agencyResponsiblePersonDto)) {

						agencyResponsiblePerson = true;

						icpcNeiceTransmissionDao.saveIcpcRequestPersonLink(icpcPlacementReq.getIdUser(),
								icpcRequest, agencyResponsiblePersonDto.getIdPerson(), ServiceConstants.ICPCPRTP_50,
								null, null);
					}

					// Agency Financially Responsible
					StagePrincipalDto agencyFinResponsiblePersonDto = getMatchedStagePrincipal(stagePrincipalDtos,
							Optional.ofNullable(neiceDocument100AType.getIntendedPlacementFinanceResponsibleParty())
									.map(ResourceDetailType::getEntity)
									.map(EntityType::getEntityPerson));

					if (!ObjectUtils.isEmpty(agencyFinResponsiblePersonDto)) {

						agencyFinResponsiblePerson = true;

						icpcNeiceTransmissionDao.saveIcpcRequestPersonLink(icpcPlacementReq.getIdUser(),
								icpcRequest, agencyFinResponsiblePersonDto.getIdPerson(), ServiceConstants.ICPCPRTP_40,
								null, null);
					}

					// Caregiver
					if (Arrays.asList(PlacementLocationCodeType.FOSTER_FAMILY_HOME,
									PlacementLocationCodeType.RELATIVE_NOT_PARENT, PlacementLocationCodeType.PARENT,
									PlacementLocationCodeType.ADOPTIVE_HOME_FINALIZING_IN_RECEIVING_STATE,
									PlacementLocationCodeType.ADOPTIVE_HOME_FINALIZING_IN_SENDING_STATE,
									PlacementLocationCodeType.ADOPTIVE_HOME_PENDING, PlacementLocationCodeType.OTHER)
							.contains(neiceDocument100AType.getPlacementResource()
									.getPlacementCategoryCode())) {

						isPlacementTypeResource = false;

						// TODO:: Verify if this available for all scenarios even when the Type of Care is Other
						Optional<PersonType> entityPersonOptional = Optional.ofNullable(
										neiceDocument100AType.getPlacementResource()
												.getEntity())
								.map(EntityType::getEntityPerson);

						StagePrincipalDto stagePrincipalDto = getMatchedStagePrincipal(stagePrincipalDtos,
								entityPersonOptional);

						if (!ObjectUtils.isEmpty(stagePrincipalDto)) {

							caregiverResourceName = stagePrincipalDto.getNmPersonFull();

							icpcNeiceTransmissionDao.saveIcpcRequestPersonLink(icpcPlacementReq.getIdUser(),
									icpcRequest, stagePrincipalDto.getIdPerson(), ServiceConstants.ICPCPRTP_60,
									placementResourceNeiceId, ServiceConstants.CSTFROLS_PR);

							if (!ObjectUtils.isEmpty(neiceCaseLinkDto)) {
								icpcNeiceTransmissionDao.saveOrUpdateNeicePersonResource(
										neiceCaseLinkDto.getIdNeiceCaseLink(), placementResourceNeiceId,
										stagePrincipalDto.getIdPerson(), "P", icpcPlacementReq.getIdUser());
							}
						}


						if (!CollectionUtils.isEmpty(
								neiceDocument100AType.getPlacementResourceRelatedPersonAssociations()) && neiceDocument100AType.getPlacementResourceRelatedPersonAssociations()
								.stream()
								.anyMatch(pa -> PlacementResourceRelatedPersonCategoryCodeType.CAREGIVER.equals(
										pa.getPlacementResourceRelatedPersonCategoryCode()))) {

							List<PlacementResourceRelatedPersonAssociationType> associationTypes = neiceDocument100AType.getPlacementResourceRelatedPersonAssociations()
									.stream()
									.filter(pa -> PlacementResourceRelatedPersonCategoryCodeType.CAREGIVER.equals(
											pa.getPlacementResourceRelatedPersonCategoryCode()))
									.collect(Collectors.toList());

							for (PlacementResourceRelatedPersonAssociationType associationType : associationTypes) {

								StagePrincipalDto associationDto = getMatchedStagePrincipal(stagePrincipalDtos,
										Optional.ofNullable(associationType.getPerson()));

								if (!ObjectUtils.isEmpty(associationDto)) {
									icpcNeiceTransmissionDao.saveIcpcRequestPersonLink(icpcPlacementReq.getIdUser(),
											icpcRequest, associationDto.getIdPerson(), ServiceConstants.ICPCPRTP_60,
											placementResourceNeiceId, ServiceConstants.CSTFROLS_SE);
								}
							}
						}
					}
				}

				// Placement Resource - Facility
				if (isPlacementTypeResource) {

					Optional<ResourceDetailType> resourceDetailTypeOptional = Optional.ofNullable(neiceDocument100AType.getPlacementResource());

					String placementResourceNm = resourceDetailTypeOptional.map(ResourceDetailType::getEntity)
							.map(EntityType::getEntityOrganization)
							.map(OrganizationType::getOrganizationName)
							.map(String::toLowerCase)
							.orElse(null);

					if (!ObjectUtils.isEmpty(placementResourceNm)) {
						List<CapsResourceLinkDto> capsResourceLinkDtos = icpcNeiceTransmissionDao.fetchResourceDetailsByNeiceName(
								placementResourceNm);

						if (!CollectionUtils.isEmpty(capsResourceLinkDtos)) {

							AddressType addressType = resourceDetailTypeOptional.get().getAddress();
							Long idResource = null;

							for (CapsResourceLinkDto capsResourceLinkDto : capsResourceLinkDtos) {

								List<ResourceAddressDto> resourceAddressDtos = capsResourceDao.getResourceAddress(
										capsResourceLinkDto.getIdResource());

								if (!CollectionUtils.isEmpty(resourceAddressDtos) && resourceAddressDtos.stream()
										.anyMatch(dto -> ServiceConstants.PRIMARY_ADDRESS_TYPE.equals(
												dto.getCdRsrcAddrType()) && dto.getAddrRsrcAddrStLn1()
												.equalsIgnoreCase(
														addressType.getAddressFullText()) && dto.getAddrRsrcAddrCity()
												.equalsIgnoreCase(
														addressType.getLocationCityName()) && dto.getCdRsrcAddrState()
												.equals(addressType.getLocationState()
														.getLocationStateUSPostalServiceCode()
														.value()) && dto.getAddrRsrcAddrZip()
												.substring(0, 5)
												.equals(addressType.getLocationPostalCode()))) {

									caregiverResourceName = capsResourceLinkDto.getNmResource();
									idResource = capsResourceLinkDto.getIdResource();
									break;
								}
							}

							if (!ObjectUtils.isEmpty(idResource)) {
								icpcPlacementDao.insertICPCRequestResourceLink(icpcRequest.getIdIcpcRequest(),
										idResource, icpcPlacementReq.getIdUser(), placementResourceNeiceId);

								if (!ObjectUtils.isEmpty(neiceCaseLinkDto)) {
									icpcNeiceTransmissionDao.saveOrUpdateNeicePersonResource(
											neiceCaseLinkDto.getIdNeiceCaseLink(), placementResourceNeiceId, idResource,
											"R", icpcPlacementReq.getIdUser());
								}
							}
						}
					}
				}

				// Agency Responsible for State Details only
				if (!agencyResponsiblePerson) {

					Optional<EntityType> entityTypeOptional = Optional.ofNullable(
									neiceDocument100AType.getIntendedPlacementPlanningResponsibleParty())
							.map(ResourceDetailType::getEntity);

					addRequestEntityFromNeice(entityTypeOptional, plcmntRequest.getIdIcpcPlacementRequest(),
							icpcPlacementReq.getIdUser(),
							CodesConstant.ICPCAGTP_10);

				}

				// Agency Financially Responsible for State Details only
				if (!agencyFinResponsiblePerson) {

					Optional<EntityType> entityTypeOptional = Optional.ofNullable(
									neiceDocument100AType.getIntendedPlacementFinanceResponsibleParty())
							.map(ResourceDetailType::getEntity);

					addRequestEntityFromNeice(entityTypeOptional, plcmntRequest.getIdIcpcPlacementRequest(),
							icpcPlacementReq.getIdUser(), CodesConstant.ICPCAGTP_20);

				}

				// Update Event Description with the Caregiver/Resource Name
				if (!ObjectUtils.isEmpty(caregiverResourceName)) {
					EventValueDto eventValueDto = new EventValueDto();
					eventValueDto.setEventDescr(event.getTxtEventDescr() + " " + caregiverResourceName);
					eventValueDto.setEventStatusCode(event.getCdEventStatus());
					icpcPlacementDao.updateEvent(event.getIdEvent(), eventValueDto);
				}
			}

			// Update Status to Processed
			if (!ObjectUtils.isEmpty(
					icpcPlacementReq.getProcessedTransmittal()) && icpcPlacementReq.getProcessedTransmittal()) {

				icpcNeiceTransmissionDao.updateNeiceTransmittalStatus(icpcPlacementReq.getIdTransmittal(),
						icpcPlacementReq.getIdUser());
				icpcNeiceTransmissionDao.updatePersonProcessedInd(icpcPlacementReq.getIdTransmittal(),
						icpcPlacementReq.getIdUser(), icpcPlacementReq.getIdNeiceTransmittalPerson());
			}
		}
	}

	/**
	 * Method Name: getMatchedStagePrincipal
	 * Method Description: Retrieves the matched Principal DTO if the Person Type is available
	 *
	 * @param stagePrincipalDtos
	 * @param personTypeOptional
	 * @return
	 */
	private StagePrincipalDto getMatchedStagePrincipal(List<StagePrincipalDto> stagePrincipalDtos,
													   Optional<PersonType> personTypeOptional) {

		StagePrincipalDto stagePrincipalDto = null;

		if (personTypeOptional.map(PersonType::getPersonName)
				.isPresent()) {

			PersonNameType personNameType = personTypeOptional.get()
					.getPersonName();
			Date dateOfBirth = personTypeOptional.map(PersonType::getPersonBirthDate)
					.map(DateType::getDate)
					.orElse(null);
			String sexCode = personTypeOptional.map(PersonType::getPersonSexCode)
					.map(SexCodeType::value)
					.orElse(null);
			/*String ssn = personTypeOptional
					.map(PersonType::getPersonSSNIdentification)
					.map(IdentificationType::getIdentificationID)
					.orElse(null);*/

			// Predicate for Suffix
			/*BiPredicate<StagePrincipalDto, PersonNameType> predicateSuffix = (dto, pn) -> (ObjectUtils.isEmpty(
					pn.getPersonNameSuffixText()) || ObjectUtils.isEmpty(
					dto.getCdPersonSuffix()) || dto.getCdPersonSuffix()
					.equalsIgnoreCase(pn.getPersonNameSuffixText()));*/

			// Predicate for each Name field
			BiPredicate<StagePrincipalDto, PersonNameType> predicateEachField = (dto, pn) -> dto.getNmPersonFirst()
					.equals(pn.getPersonGivenName()) && dto.getNmPersonLast()
					.equals(pn.getPersonSurName());// && predicateSuffix.test(dto, pn);

			stagePrincipalDto = stagePrincipalDtos.stream()
					.filter(dto -> (predicateEachField.test(dto, personNameType)) || dto.getNmPersonFull()
							.equals(personNameType.getPersonFullName()))
					.filter(dto -> ObjectUtils.isEmpty(dateOfBirth) || org.apache.commons.lang.time.DateUtils.isSameDay(
							dateOfBirth, dto.getDtPersonBirth()))
					.filter(dto -> ObjectUtils.isEmpty(sexCode) || sexCode.equals(dto.getCdPersonSex()))
					.findFirst()
					.orElse(null);
		}

		return stagePrincipalDto;

	}

	/**
	 * Method Name: addRequestEntityFromNeice
	 * Method Description: Insert record in ICPC Placement Request Entity if the state agency details are sent
	 *
	 * @param entityTypeOptional
	 * @param idPlacementRequest
	 * @param idUser
	 * @param cdAgencyType
	 * @return
	 */
	private void addRequestEntityFromNeice(Optional<EntityType> entityTypeOptional, Long idPlacementRequest,
										   Long idUser, String cdAgencyType) {

		if (entityTypeOptional
				.map(EntityType::getEntityOrganization)
				.map(OrganizationType::getOrganizationName)
				.isPresent()) {

			String organizationName = entityTypeOptional.get()
					.getEntityOrganization()
					.getOrganizationName();

			NeiceStateParticpantDTO neiceStateParticpantDTO = Optional.ofNullable(
							icpcNeiceTransmissionDao.fetchAgencyStateDetails(organizationName))
					.filter(dto -> !Arrays.asList("CA", "CO", "OH")
							.contains(dto.getStateCode()))
					.orElse(null);

			if (!ObjectUtils.isEmpty(neiceStateParticpantDTO)) {

				Optional.ofNullable(icpcNeiceTransmissionDao.fetchAgencyEntityDetails(
								neiceStateParticpantDTO.getStateNm()
										.toLowerCase()))
						.ifPresent(dto -> icpcPlacementDao.insertICPCPlacementRqstEnity(dto.getIdEntity(),
								idPlacementRequest, cdAgencyType, idUser));

			}
		}
	}

	@Override
	public ListTransmissionRes getSendingAgencyInfo(String stateCode) {
		ListTransmissionRes res = new ListTransmissionRes();

		if (!ObjectUtils.isEmpty(stateCode)) {
			res.setSendingAgency(icpcPlacementDao.getSendingAgencyInfo(stateCode));
		} else {
			res.setSendingAgency(icpcPlacementDao.getAllAgencyInfo());
		}
		return res;
	}

	@Override
	public IcpcEventLink getIcpcEventLinkInfo(Long eventId) {
		return icpcPlacementDao.getIcpcEventLinkInfo(eventId);
	}

	@Override
	public Map<Long, String> getIcpcRequestInfo(Long idICPCRequest){
		return icpcPlacementDao.getIcpcRequestInfo(idICPCRequest);
	}

	/**
	 * Method Name: validateCreateTransmittal
	 * Method Description: Retrieve the Placement Status Details and Document Details for Create
	 * Transmittal Validation
	 *
	 * @param icpcTransmittalDto
	 * @return ICPCCreateTransmittalDto
	 */
	@Override
	public ICPCPlacementRes validateCreateTransmittal(ICPCTransmittalDto icpcTransmittalDto) {

		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();

		if (!ObjectUtils.isEmpty(icpcTransmittalDto) && !ObjectUtils.isEmpty(icpcTransmittalDto.getIdICPCTransmittal())) {

			Map<Long, Long> selectedPersonsMap = icpcPlacementDao.getSelectedChildren(
					icpcTransmittalDto.getIdICPCTransmittal());

			List<ICPCPersonDto> icpcPersonDtos = new ArrayList<>();
			if (!ObjectUtils.isEmpty(selectedPersonsMap)) {
				icpcPlacementRes.setSelectedSiblings(new ArrayList<>(selectedPersonsMap.values()));
				List<Long> idPersons = new ArrayList<>(selectedPersonsMap.values());
				icpcPersonDtos = Optional.ofNullable(
								icpcPlacementDao.getSiblingsRequests(idPersons, icpcTransmittalDto.getIdICPCRequest()))
						.orElseGet(ArrayList::new);
			}
			ICPCPersonDto icpcPersonDto = new ICPCPersonDto();
			icpcPersonDto.setIdPerson(icpcTransmittalDto.getIdPerson());
			icpcPersonDto.setIdICPCRequest(icpcTransmittalDto.getIdICPCRequest());
			icpcPersonDtos.add(icpcPersonDto);

			icpcPlacementRes.setIcpcPersonDtos(icpcPersonDtos);

			List<Long> idIcpcRequests = icpcPersonDtos
					.stream()
					.map(ICPCPersonDto::getIdICPCRequest)
					.collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(idIcpcRequests)) {

				icpcPlacementRes.setIcpcPlacementRequestDtoMap(
						icpcPlacementDao.getPlacementRequestDtlForAll(idIcpcRequests));

				if (ServiceConstants.ICPCTRTP_240.equals(icpcTransmittalDto.getCdTransmittalType()) || (Arrays.asList(
								ServiceConstants.ICPCTRTP_150, ServiceConstants.ICPCTRTP_160)
						.contains(icpcTransmittalDto.getCdTransmittalType()) && ServiceConstants.ICPCHSTY_10.equals(
						icpcTransmittalDto.getCdHomeStudyType()))) {

					boolean getApproved = !ServiceConstants.ICPCTRTP_240.equals(icpcTransmittalDto.getCdTransmittalType());

					Map<Long, ICPCPlacementStatusDto> icpcPlacementStatusDtoMap = new HashMap<>();

					idIcpcRequests.forEach(id -> Optional.ofNullable(icpcPlacementDao.getPlacementStatusByRequest(id, getApproved))
														 .ifPresent(dto -> icpcPlacementStatusDtoMap.put(id, dto)));

					icpcPlacementRes.setIcpcPlacementStatusDtoMap(icpcPlacementStatusDtoMap);
				}
			}

			if (!CollectionUtils.isEmpty(icpcTransmittalDto.getDocumentList())) {

				List<Long> idIcpcDocuments = icpcTransmittalDto.getDocumentList()
						.stream()
						.filter(ICPCDocumentDto::isDocumentSelected)
						.map(ICPCDocumentDto::getIdICPCDocument)
						.collect(Collectors.toList());

				if (!CollectionUtils.isEmpty(idIcpcDocuments)) {

					List<ICPCDocumentDto> icpcDocumentDtos = icpcPlacementDao.getIcpcDocument(idIcpcDocuments);
					icpcPlacementRes.setDocumentList(icpcDocumentDtos);
				}
			}
		}


		return icpcPlacementRes;
	}

	/**
	 * Method Name: saveTransmittalDocs
	 * Method Description: Save the Transmittal selected documents for Transmission
	 *
	 * @param icpcTransmittalDto
	 * @return ICPCPlacementRes
	 */
	@Override
	public void saveTransmittalDocs(ICPCTransmittalDto icpcTransmittalDto) {

		icpcPlacementDao.saveTransmittalDocs(icpcTransmittalDto);
	}

	/**
	 * Method Name: saveTransmittalStatus
	 * Method Description: Save the Transmittal Status received from MuleSoft
	 *
	 * @param icpcTransmittalDto
	 */
	@Override
	public void saveTransmittalStatus(ICPCTransmittalDto icpcTransmittalDto) {

		icpcPlacementDao.saveTransmittalStatus(icpcTransmittalDto);

	}

	/**
	 * Method Name: saveTransmittalOtherMode
	 * Method Description: Save the Transmittal Other Mode
	 *
	 * @param icpcTransmittalDto
	 */
	@Override
	public void saveTransmittalOtherMode(ICPCTransmittalDto icpcTransmittalDto) {

		icpcPlacementDao.saveTransmittalOtherMode(icpcTransmittalDto);

	}
}
