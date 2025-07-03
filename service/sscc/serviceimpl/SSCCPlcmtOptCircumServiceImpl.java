package us.tx.state.dfps.service.sscc.serviceimpl;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.hibernate.annotations.common.util.StringHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.EntityAddress;
import us.tx.state.dfps.common.domain.EntityDomain;
import us.tx.state.dfps.common.domain.EntityPhone;
import us.tx.state.dfps.common.domain.Name;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonCategory;
import us.tx.state.dfps.common.domain.SsccPlcmtCircumstance;
import us.tx.state.dfps.common.domain.SsccPlcmtHeader;
import us.tx.state.dfps.common.domain.SsccPlcmtInfo;
import us.tx.state.dfps.common.domain.SsccPlcmtMedCnsntr;
import us.tx.state.dfps.common.domain.SsccPlcmtName;
import us.tx.state.dfps.common.domain.SsccPlcmtNarr;
import us.tx.state.dfps.common.domain.SsccPlcmtPlaced;
import us.tx.state.dfps.common.domain.SsccReferral;
import us.tx.state.dfps.common.dto.PlacementValueDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.admin.dao.PersonStagePersonLinkTypeDao;
import us.tx.state.dfps.service.admin.dto.AddrPhoneDto;
import us.tx.state.dfps.service.admin.dto.PersonStagePersonLinkTypeInDto;
import us.tx.state.dfps.service.admin.dto.PersonStagePersonLinkTypeOutDto;
import us.tx.state.dfps.service.admin.dto.SSCCPlcmntRsrcLinkMCDto;
import us.tx.state.dfps.service.admin.service.AddrPhoneRtrvService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casepackage.dao.SSCCTimelineDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCResourceDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCTimelineDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EntityDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.PlcmtInfoRsrcRetrivalReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.facilityservicetype.dto.FacilityServiceTypeOutDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.MedicaidUpdateDao;
import us.tx.state.dfps.service.person.dao.NameDao;
import us.tx.state.dfps.service.person.dao.PersonCategoryDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonPhoneDao;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.person.dto.PersonCategoryDto;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneRetDto;
import us.tx.state.dfps.service.placement.service.PlacementInfoRetrivalService;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.sscc.dao.SSCCListDao;
import us.tx.state.dfps.service.sscc.dao.SSCCPlacementNetworkDao;
import us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao;
import us.tx.state.dfps.service.sscc.dao.SSCCRefDao;
import us.tx.state.dfps.service.sscc.dto.EntityAddressDto;
import us.tx.state.dfps.service.sscc.dto.EntityPhoneDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtCircumstanceDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtHeaderDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtInfoDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtMedCnsntrDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtNameDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtNarrDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtOptCircumDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtPlacedDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtStateDto;
import us.tx.state.dfps.service.sscc.service.SSCCPlcmtOptCircumService;
import us.tx.state.dfps.service.sscc.util.SSCCPlcmtOptCircumUtil;
import us.tx.state.dfps.service.sscc.util.SSCCRefUtil;
import us.tx.state.dfps.service.stageutility.dao.StageUtilityDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dao.ExceptionalCareDao;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.ResourceAddressDto;
import us.tx.state.dfps.service.subcare.dto.ResourcePhoneDto;
import us.tx.state.dfps.service.workload.dao.AddressDao;
import us.tx.state.dfps.service.workload.dao.MedicalConsenterDao;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Implementation Class for SSCC Placement Option Circumnstances Services. Aug
 * 10, 2018- 6:24:58 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
@Transactional
public class SSCCPlcmtOptCircumServiceImpl implements SSCCPlcmtOptCircumService {

	private static final Logger log = Logger.getLogger(SSCCPlcmtOptCircumServiceImpl.class);

	@Autowired
	SSCCPlcmtOptCircumDao ssccPlcmtOptCircumDao;

	@Autowired
	EntityDao entityDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	PersonStagePersonLinkTypeDao personStageLinkTypeDao;

	@Autowired
	SSCCRefDao ssccRefDao;

	@Autowired
	StageUtilityDao stageUtilityDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	SSCCPlacementNetworkDao ssccPlcmtNtwrkDao;

	@Autowired
	NameDao nameDao;

	@Autowired
	AddressDao addressDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	FormattingUtils formattingUtil;

	@Autowired
	CapsResourceDao resourceDao;

	@Autowired
	AddrPhoneRtrvService addrPhoneRtrvSvc;

	@Autowired
	PlacementInfoRetrivalService plcmtInfoRtrvSvc;

	@Autowired
	PlacementDao placementDao;

	@Autowired
	SSCCTimelineDao ssccTimeLineDao;

	@Autowired
	SSCCPlcmtOptCircumUtil ssccPlcmtOptCircumUtil;

	@Autowired
	SSCCRefUtil ssccRefutil;

	@Autowired
	SSCCListDao ssccListDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	MedicaidUpdateDao medicaidUpdateDao;

	@Autowired
	ExceptionalCareDao exceptionalCare;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	PersonCategoryDao personCategoryDao;

	@Autowired
	MedicalConsenterDao medicalConcenterDao;

	@Autowired
	PersonPhoneDao personPhoneDao;

	/**
	 * Method Name: readSSCCPlcmtOptCirum Method Description: The Service
	 * implementation for reading the SSCC Placement Option and Circumstance
	 * Page. The method gets the idSSCCPlcmtHeader and fetches all applicable
	 * records in one go and filters the same out on the basis of requirement to
	 * show on the screen.
	 * 
	 * @param ssccPlcmtHeaderDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto readSSCCPlcmtOptCirum(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("readSSCCPlcmtOptCirum method in SSCCPlcmtOptCircumServiceImpl : Execution Started.");
		SSCCPlcmtHeaderDto ssccPlcmtHeaderDto = ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto();
		Long idSSCCPlcmntHeader = ssccPlcmtHeaderDto.getIdSSCCPlcmtHeader();
		// Check if Active SSCCRef/IdPrimaryChild attribute is set in the Dto.
		// If not Setup the SSCCPlcmtOptCircumDto for the stage.
		if (StringUtils.isEmpty(ssccPlcmtOptCircumDto.getIdPrimaryChild())) {
			setUpSSCCPlcmtOptCircum(ssccPlcmtOptCircumDto);
		}
		SsccPlcmtHeader ssccPlcmtHdr = ssccPlcmtOptCircumDao.readSSCCPlcmtHeader(idSSCCPlcmntHeader);
		// Check if valid SSCCPlcmtHeader
		if (!ObjectUtils.isEmpty(ssccPlcmtHdr)) {
			Long idEntityAddress = ServiceConstants.ZERO_VAL;
			Long idEntity = ServiceConstants.ZERO_VAL;
			Long idEntityPhone = ServiceConstants.ZERO_VAL;
			// If sscc Plcmt Hdr is not null. Set the DTO value.
			BeanUtils.copyProperties(ssccPlcmtHdr, ssccPlcmtHeaderDto);
			ssccPlcmtHeaderDto.setIdSSCCReferral(ssccPlcmtHdr.getSsccReferral().getIdSSCCReferral());
			// Set the value if the this sscc placement is an option.
			if (CodesConstant.CSSCCPTY_10.equals(ssccPlcmtHdr.getCdSSCCPlcmtType())) {
				log.info(
						"readSSCCPlcmtOptCirum method in SSCCPlcmtOptCircumServiceImpl : Creating SSCC Placement Options.");
				ssccPlcmtOptCircumDto.setIsOption(Boolean.TRUE);
				// Set the Role if the user is a fixer.
				ssccPlcmtOptCircumUtil.checkIfFixer(ssccPlcmtOptCircumDto);
				// If the current placement type is option type fetch the
				// placement name record with latest nbr version.
				SsccPlcmtName ssccPlcmtName = Collections.max(ssccPlcmtHdr.getSsccPlcmtNames(),
						Comparator.comparing(SsccPlcmtName::getNbrVersion));
				SSCCPlcmtNameDto ssccPlcmtNameDto = new SSCCPlcmtNameDto();
				BeanUtils.copyProperties(ssccPlcmtName, ssccPlcmtNameDto);
				ssccPlcmtNameDto.setIdSSCCPlcmtHeader(idSSCCPlcmntHeader);
				ssccPlcmtOptCircumDto.setSsccPlcmtNameDto(ssccPlcmtNameDto);
				ssccPlcmtHeaderDto.setCurrNbrVersion(ssccPlcmtNameDto.getNbrVersion());
				idEntityAddress = ssccPlcmtNameDto.getIdEntityAddress();
				idEntityPhone = ssccPlcmtNameDto.getIdEntityPhone();
				// Check if SSCC Med Consenter records exists, set the values.
				HashMap<String, SSCCPlcmtMedCnsntrDto> ssccPlcmtMedCnsntrDtoMap = new HashMap<>();
				if (!ObjectUtils.isEmpty(ssccPlcmtHdr.getSsccPlcmtMedCnsntrs())) {
					SsccPlcmtMedCnsntr ssccPlcmtCnsntr = Collections.max(ssccPlcmtHdr.getSsccPlcmtMedCnsntrs(),
							Comparator.comparing(SsccPlcmtMedCnsntr::getNbrVersion));
					Long ltstVer = ssccPlcmtCnsntr.getNbrVersion();
					ssccPlcmtHdr.getSsccPlcmtMedCnsntrs().stream().forEach(c -> {
						if (ltstVer.equals(c.getNbrVersion())) {
							SSCCPlcmtMedCnsntrDto medDto = new SSCCPlcmtMedCnsntrDto();
							BeanUtils.copyProperties(c, medDto);
							medDto.setIdSSCCPlcmtHeader(idSSCCPlcmntHeader);
							ssccPlcmtMedCnsntrDtoMap.put(medDto.getCdMedConsenterType(), medDto);
						}
					});
				}
				ssccPlcmtOptCircumDto.setSsccPlcmtMedCnsntrDtoMap(ssccPlcmtMedCnsntrDtoMap);
				// SetMedCnsntrDefaults value
				ssccPlcmtOptCircumDto = setMedCnsntrDefaults(ssccPlcmtOptCircumDto);
				// Check and set the Count of active Med Concenter with Courth
				// Auth as Y.
				ssccPlcmtOptCircumDto.setMcCourtAuthCnt(
						ssccPlcmtOptCircumDao.getMcCourtAuthCnt(ssccPlcmtHeaderDto.getIdSSCCReferral()));

				// if Placement info Record exist set the same
				if (!ObjectUtils.isEmpty(ssccPlcmtHdr.getSsccPlcmtInfos())) {
					SsccPlcmtInfo ssccPlcmtInfo = Collections.max(ssccPlcmtHdr.getSsccPlcmtInfos(),
							Comparator.comparing(SsccPlcmtInfo::getNbrVersion));
					SSCCPlcmtInfoDto ssccPlcmtInfoDto = new SSCCPlcmtInfoDto();
					BeanUtils.copyProperties(ssccPlcmtInfo, ssccPlcmtInfoDto);
					ssccPlcmtInfoDto.setIdSSCCPlcmtHeader(idSSCCPlcmntHeader);
					ssccPlcmtOptCircumDto.setSsccPlcmtInfoDto(ssccPlcmtInfoDto);
				}
				SSCCPlcmtPlacedDto ssccPlcmtPlacedDto = new SSCCPlcmtPlacedDto();
				// If SSCC Placement Placed Records exists
				if (!ObjectUtils.isEmpty(ssccPlcmtHdr.getSsccPlcmtPlaceds())) {
					SsccPlcmtPlaced ssccPlcmtPlcd = Collections.max(ssccPlcmtHdr.getSsccPlcmtPlaceds(),
							Comparator.comparing(SsccPlcmtPlaced::getNbrVersion));
					BeanUtils.copyProperties(ssccPlcmtPlcd, ssccPlcmtPlacedDto);
					ssccPlcmtPlacedDto.setIdSSCCPlcmtHeader(idSSCCPlcmntHeader);
				}
				ssccPlcmtOptCircumDto.setSsccPlcmtPlacedDto(ssccPlcmtPlacedDto);
				// reterieve Narratives if exists.
				SSCCPlcmtNarrDto ssccPlcmtNarrDto = new SSCCPlcmtNarrDto();
				if (!ObjectUtils.isEmpty(ssccPlcmtHdr.getSsccPlcmtNarrs())) {
					SsccPlcmtNarr ssccPlcmtNarr = Collections.max(ssccPlcmtHdr.getSsccPlcmtNarrs(),
							Comparator.comparing(SsccPlcmtNarr::getNbrVersion));
					BeanUtils.copyProperties(ssccPlcmtNarr, ssccPlcmtNarrDto);
					ssccPlcmtNarrDto.setIdSSCPlcmtHeader(idSSCCPlcmntHeader);
				}
				ssccPlcmtOptCircumDto.setSsccPlcmtNarrDto(ssccPlcmtNarrDto);
			} else {
				log.info(
						"readSSCCPlcmtOptCirum method in SSCCPlcmtOptCircumServiceImpl : Creating SSCC Placement Cicumnstances.");
				// If the SSC Placemet Circumstance reocords exists fetch and
				// set the correct version no.
				ssccPlcmtOptCircumDto.setIsOption(Boolean.FALSE);
				SsccPlcmtCircumstance ssccPlcmtCircustance = Collections.max(ssccPlcmtHdr.getSsccPlcmtCircumstances(),
						Comparator.comparing(SsccPlcmtCircumstance::getNbrVersion));
				ssccPlcmtHeaderDto.setCurrNbrVersion(ssccPlcmtCircustance.getNbrVersion());
				SSCCPlcmtCircumstanceDto ssccPlcmtCircumsDto = new SSCCPlcmtCircumstanceDto();
				BeanUtils.copyProperties(ssccPlcmtCircustance, ssccPlcmtCircumsDto);
				ssccPlcmtCircumsDto.setIdSSCCPlcmtHeader(idSSCCPlcmntHeader);
				ssccPlcmtOptCircumDto.setSsccPlcmtCircumstanceDto(ssccPlcmtCircumsDto);
				idEntityAddress = ssccPlcmtCircumsDto.getIdEntityAddress();
				idEntityPhone = ssccPlcmtCircumsDto.getIdEntityPhone();
			}
			// Set the Evaluation Person Name.
			if (!StringUtils.isEmpty(ssccPlcmtHeaderDto.getIdEvalPerson())) {
				ssccPlcmtHeaderDto.setNmEvalPerson(formattingUtil.formatName(ssccPlcmtHeaderDto.getIdEvalPerson()));
			}
			// set the sscc placement header Dto back to returning object.
			ssccPlcmtOptCircumDto.setSsccPlcmtHeaderDto(ssccPlcmtHeaderDto);
			// If the Entity record exist fetch and set the values.
			if (SSCCPlcmtOptCircumUtil.isNonZeroLong(idEntityPhone)) {
				EntityPhoneDto entityPhoneDto = entityDao.getEntityPhone(idEntityPhone, true);
				ssccPlcmtOptCircumDto.setEntityPhoneDto(entityPhoneDto);
				idEntity = entityPhoneDto.getIdEntityPhoneOwner();
				ssccPlcmtOptCircumDto.setEntityDto(entityDao.getEntity(idEntity));
			}
			if (SSCCPlcmtOptCircumUtil.isNonZeroLong(idEntityAddress)) {
				EntityAddressDto entityAddrDto = entityDao.getEntityAddress(idEntityAddress, true);
				ssccPlcmtOptCircumDto.setEntityAddressDto(entityAddrDto);
				if (StringUtils.isEmpty(idEntity) || ServiceConstants.ZERO_VAL.equals(idEntity)) {
					idEntity = entityAddrDto.getIdEntyAddressOwner();
					ssccPlcmtOptCircumDto.setEntityDto(entityDao.getEntity(idEntity));
				}
			}
			// check if the header id is not the active referral id of the stage
			// make the page view only.
			if (!ssccPlcmtOptCircumDto.getIdActiveRef()
					.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCReferral())) {
				ssccPlcmtOptCircumDto.setPageMode(ServiceConstants.PAGE_MODE_VIEW);
			}
			// fetch SSCCRefDto for Validations
			if (!StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCReferral())) {
				ssccPlcmtOptCircumDto.setSsccRefDto(ssccRefDao
						.fetchReferralByPK(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCReferral()));
			}
			// fetch sscc referrals for the given idSSCCRef and fetch and set
			// the SSCC Resorce.
			ssccPlcmtOptCircumDto.setSsccResourceDto(
					ssccRefDao.fetchSSCCResourceInfo(ssccPlcmtHdr.getSsccReferral().getCdCntrctRegion(),
							ssccPlcmtHdr.getSsccReferral().getIdSSCCCatchment()));
			// read the person info from person table or resource info from
			// resource table as long as circumnstance
			// is not approved or option is not saved to placement.
			ssccPlcmtOptCircumDto = readInitRsrcPers(ssccPlcmtOptCircumDto);
			// Set the SSCC Time Line
			ssccPlcmtOptCircumDto.setSsccTimelineList(fetchSSCCTimeLine(ssccPlcmtOptCircumDto));
		}
		log.info("readSSCCPlcmtOptCirum method in SSCCPlcmtOptCircumServiceImpl : Returning response.");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: setMedCnsntrDefaults Method Description: This method sets
	 * defaults for Person List, Person Name, Person ID for each Medical
	 * Consenter View
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return ssccPlcmtOptCircumDto
	 */
	private SSCCPlcmtOptCircumDto setMedCnsntrDefaults(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("setMedCnsntrDefaults method in SSCCPlcmtOptCircumServiceImpl : Execution Started.");
		Long idStage = ssccPlcmtOptCircumDto.getIdStage();
		ArrayList<StageValueBeanDto> pcStageList = new ArrayList<>();
		Set<Long> validPersons = null;
		boolean setSelType = false;
		int indexFPRI = 0;
		int indexFBUP = 1;
		if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap())) {
			// Fetch the Primary Medical Concenter record.
			SSCCPlcmtMedCnsntrDto ssccPlcmtMedCnsntr = ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap()
					.get(CodesConstant.CMCTYPE_FPRI);
			if (CodesConstant.CSSCCMDC_10.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())
					|| CodesConstant.CSSCCMDC_20.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())
					|| CodesConstant.CSSCCMDC_30.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())) {
				// Check if Medical concenter type is 10, 20 or 30. Set Valid
				// Principals for the Stage
				validPersons = getValidPrincipalsForStage(idStage);
				setSelType = !ObjectUtils.isEmpty(validPersons);
			} else if (CodesConstant.CSSCCMDC_40.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())
					|| CodesConstant.CSSCCMDC_50.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())) {
				// Check if Medical concenter type is 40 or 50. Set the Valid
				// DFPS Staff List.
				validPersons = getValidDfpsStaff(idStage,
						!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccResourceDto())
								? ssccPlcmtOptCircumDto.getSsccResourceDto().getCdSSCCCatchment()
								: ServiceConstants.EMPTY_STRING);
			} else if (CodesConstant.CSSCCMDC_60.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())) {
				// Set the primary child as the valid person.
				validPersons = new HashSet<>();
				validPersons.add(ssccPlcmtOptCircumDto.getIdPrimaryChild());
			} else if (CodesConstant.CSSCCMDC_70.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())) {
				validPersons = getValidCPBParents(idStage);
				if (validPersons.size() == 1 && CodesConstant.CSSCCSTA_10
						.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus())) {
					ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().remove(CodesConstant.CMCTYPE_FBUP);
				}
			} else if (CodesConstant.CSSCCMDC_80.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())
					|| CodesConstant.CSSCCMDC_90.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())
					|| CodesConstant.CSSCCMDC_100.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())) {
				// get valid persons to select from designated medical
				// consenters from resource network module
				Long idRsrcFacil = CodesConstant.CSSCCMDC_90.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())
						? ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcAgency()
						: ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcFacil();
				validPersons = ssccPlcmtOptCircumDao
						.getCpaOthrMedCnsntrs(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcSSCC(), idRsrcFacil);
				setSelType = (!StringUtils.isEmpty(ssccPlcmtMedCnsntr.getIdMedConsenterPerson())
						&& !ServiceConstants.ZERO_VAL.equals(ssccPlcmtMedCnsntr.getIdMedConsenterPerson()));
			}

			// If any other state than new status add save med consenter person
			// to valid Persons
			if (!CodesConstant.CSSCCSTA_10.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus())) {
				for (Map.Entry<String, SSCCPlcmtMedCnsntrDto> entry : ssccPlcmtOptCircumDto
						.getSsccPlcmtMedCnsntrDtoMap().entrySet()) {
					ssccPlcmtMedCnsntr = entry.getValue();
					if (!StringUtils.isEmpty(ssccPlcmtMedCnsntr.getIdMedConsenterPerson())
							&& !ServiceConstants.ZERO_VAL.equals(ssccPlcmtMedCnsntr.getIdMedConsenterPerson())) {
						if (CodesConstant.CSSCCMDC_90.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())
								&& (CodesConstant.CMCTYPE_FPRI.equals(ssccPlcmtMedCnsntr.getCdMedConsenterType())
										|| CodesConstant.CMCTYPE_SPRI
												.equals(ssccPlcmtMedCnsntr.getCdMedConsenterType()))) {
							continue;
						}
						validPersons.add(ssccPlcmtMedCnsntr.getIdMedConsenterPerson());
					}
				}
			}

			ssccPlcmtOptCircumDto.setMedCnsntrOptions(getDesigneeOptions(validPersons));

			if (CodesConstant.CSSCCMDC_70.equals(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType())) {
				// Check if Parent record retrieved.
				if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getMedCnsntrOptions())) {
					pcStageList = stageDao.getStageListForPC(
							ssccPlcmtOptCircumDto.getMedCnsntrOptions().firstEntry().getKey().intValue(), false);
					if (ObjectUtils.isEmpty(pcStageList) && ssccPlcmtOptCircumDto.getMedCnsntrOptions().size() == 2) {
						indexFPRI = 1;
						indexFBUP = 0;
					} else {
						indexFPRI = 0;
						indexFBUP = 1;
					}
				}
			}

			// Loop through the already available SSCC Placement Concenter Map
			// to set the default med concenter value.
			for (Map.Entry<String, SSCCPlcmtMedCnsntrDto> entry : ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap()
					.entrySet()) {
				ssccPlcmtMedCnsntr = entry.getValue();
				if (setSelType) {
					ssccPlcmtMedCnsntr = setSelTypeAndData(ssccPlcmtMedCnsntr, ssccPlcmtOptCircumDto);
				} else {
					ssccPlcmtMedCnsntr.setPerSelType(null);
					ssccPlcmtMedCnsntr.setValidateFlag(Boolean.TRUE);
					if (ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType().equals(CodesConstant.CSSCCMDC_40)
							|| ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType().equals(CodesConstant.CSSCCMDC_50)
							|| ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType().equals(CodesConstant.CSSCCMDC_60)
							|| ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType().equals(CodesConstant.CSSCCMDC_70)) {
						ssccPlcmtMedCnsntr.setValidateFlag(Boolean.FALSE);
					}

					if (ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType().equals(CodesConstant.CSSCCMDC_40)
							|| ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType().equals(CodesConstant.CSSCCMDC_50)) {
						ssccPlcmtMedCnsntr.setIdMedConsenterPersonSl(ssccPlcmtMedCnsntr.getIdMedConsenterPerson());
					}

					if (ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType().equals(CodesConstant.CSSCCMDC_60)) {
						ssccPlcmtMedCnsntr
								.setNmPersonFirst(ssccPlcmtOptCircumDto.getMedCnsntrOptions().firstEntry().getValue());
						ssccPlcmtMedCnsntr.setIdMedConsenterPerson(
								new Long(ssccPlcmtOptCircumDto.getMedCnsntrOptions().firstEntry().getKey()));
					}

					if (ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType().equals(CodesConstant.CSSCCMDC_70)
							&& (ssccPlcmtOptCircumDto.getMedCnsntrOptions().size() > 0)) {
						if (ssccPlcmtMedCnsntr.getCdMedConsenterType().equals(CodesConstant.CMCTYPE_FPRI)) {
							if (ssccPlcmtMedCnsntr.getIdMedConsenterPerson() == 0) {
								ssccPlcmtMedCnsntr.setNmPersonFirst(
										new ArrayList<String>(ssccPlcmtOptCircumDto.getMedCnsntrOptions().values())
												.get(indexFPRI));
								ssccPlcmtMedCnsntr.setIdMedConsenterPerson(
										new ArrayList<Long>(ssccPlcmtOptCircumDto.getMedCnsntrOptions().keySet())
												.get(indexFPRI));
							} else {
								ssccPlcmtMedCnsntr.setNmPersonFirst(
										formattingUtil.formatName(ssccPlcmtMedCnsntr.getIdMedConsenterPerson()));
							}
						}

						if (ssccPlcmtMedCnsntr.getCdMedConsenterType().equals(ServiceConstants.CMCTYPE_FBUP)) {
							if (ssccPlcmtMedCnsntr.getIdMedConsenterPerson() == 0) {
								ssccPlcmtMedCnsntr.setNmPersonLast(
										new ArrayList<String>(ssccPlcmtOptCircumDto.getMedCnsntrOptions().values())
												.get(indexFBUP));
								ssccPlcmtMedCnsntr.setIdMedConsenterPerson(
										new ArrayList<Long>(ssccPlcmtOptCircumDto.getMedCnsntrOptions().keySet())
												.get(indexFBUP));
							} else {
								ssccPlcmtMedCnsntr.setNmPersonLast(
										formattingUtil.formatName(ssccPlcmtMedCnsntr.getIdMedConsenterPerson()));
							}
						}
					}
				}
				entry.setValue(ssccPlcmtMedCnsntr);
			}
			log.info("setMedCnsntrDefaults method in SSCCPlcmtOptCircumServiceImpl : Execution Ended.");
			// Set the Information Message List for each concenter.
			ssccPlcmtOptCircumDto
					.setMedCnsntrMsgList(setMedCnsntrInfoMsgs(ssccPlcmtMedCnsntr.getCdMedCnsntrSelectType()));
		}
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: getValidPrincipalsForStage Method Description: Method
	 * fetches all the Principal Person in the Stage having Age greater than 17
	 * and date of death as blank in the system.
	 * 
	 * @param idStage
	 * @return personSet
	 */
	private Set<Long> getValidPrincipalsForStage(Long idStage) {
		log.info("getValidPrincipalsForStage method in SSCCPlcmtOptCircumServiceImpl : Execution Started.");
		Set<Long> personSet = new HashSet<>();
		PersonStagePersonLinkTypeInDto personStagePersonLinkTypeInDto = new PersonStagePersonLinkTypeInDto();
		personStagePersonLinkTypeInDto.setIdStage(idStage);
		personStagePersonLinkTypeInDto.setCdStagePersType(CodesConstant.CPRSNALL_STF);
		// Call the service to fetch all Non-Staff PRN persons from Stage Person
		// Link.
		List<PersonStagePersonLinkTypeOutDto> personInStageList = personStageLinkTypeDao
				.getPersonDetails(personStagePersonLinkTypeInDto);
		if (!ObjectUtils.isEmpty(personInStageList)) {
			// Filter only the principal Person from the list.
			personInStageList = personInStageList.stream()
					.filter(p -> CodesConstant.CPRSNALL_PRN.equals(p.getCdStagePersType()))
					.collect(Collectors.toList());
			personInStageList.stream().forEach(person -> {
				// Check the age and Date of Death of all the Principal in the
				// Stage and add to return set.
				int personAge = 0;
				if (!StringUtils.isEmpty(person.getDtPersonBirth())) {
					personAge = DateUtils.getAge(person.getDtPersonBirth());
				} else if (!StringUtils.isEmpty(person.getIndPersonDobApprox())
						&& ServiceConstants.Y.equalsIgnoreCase(person.getIndPersonDobApprox())
						&& (!StringUtils.isEmpty(person.getPersonAge())
								&& !ServiceConstants.ZERO_VAL.equals(person.getPersonAge()))) {
					Date birthDate = DateUtils.getJavaDateFromAge(person.getPersonAge());
					personAge = DateUtils.getAge(birthDate);
				}
				if (personAge >= ServiceConstants.AGE_MAX_DIFF && StringUtils.isEmpty(person.getDtPersonDeath())) {
					personSet.add(person.getIdPerson());
				}
			});
		}
		log.info("getValidPrincipalsForStage method in SSCCPlcmtOptCircumServiceImpl : Returning personSet.");
		return personSet;
	}

	/**
	 * Method Name: getValidDfpsStaff Method Description: The method returns a
	 * Set of valid staff id's that belong to the stage and SSCC contract Region
	 * 
	 * @param idStage
	 * @param cdSSCCCntrctCatchment
	 * @return
	 */
	private Set<Long> getValidDfpsStaff(Long idStage, String cdSSCCCntrctCatchment) {
		// fetch the DFPS Staff for the current stage.
		Set<Long> validDfpsStaff = ssccPlcmtOptCircumDao.getDfpsStaff(idStage);
		if (!ObjectUtils.isEmpty(validDfpsStaff)) {
			validDfpsStaff = validDfpsStaff.stream()
					.filter(stf -> !ssccRefDao.isUserSSCCExternal(stf, cdSSCCCntrctCatchment))
					.collect(Collectors.toSet());
		}
		return validDfpsStaff;
	}

	/**
	 * Method Name: getValidCPBParents Method Description: Method retuns the CPB
	 * Parents list
	 * 
	 * @param idStage
	 * @return
	 */
	private Set<Long> getValidCPBParents(Long idStage) {
		log.info("getValidCPBParents method in SSCCPlcmtOptCircumServiceImpl : Execution Started.");
		Set<Long> personSet = new HashSet<>();
		PersonStagePersonLinkTypeInDto personStagePersonLinkTypeInDto = new PersonStagePersonLinkTypeInDto();
		personStagePersonLinkTypeInDto.setIdStage(idStage);
		personStagePersonLinkTypeInDto.setCdStagePersType(CodesConstant.CPRSNALL_STF);
		// Call the service to fetch all Non-Staff PRN persons from Stage Person
		// Link.
		List<PersonStagePersonLinkTypeOutDto> personInStageList = personStageLinkTypeDao
				.getPersonDetails(personStagePersonLinkTypeInDto);
		if (!ObjectUtils.isEmpty(personInStageList)) {
			personInStageList.stream().forEach(person -> {
				if (!StringUtils.isEmpty(person.getDtPersonDeath())) {
					ArrayList<StageValueBeanDto> pcStageList = stageDao
							.getStageListForPC(person.getIdPerson().intValue(), false);
					if (!StringUtils.isEmpty(pcStageList)
							&& SSCCPlcmtOptCircumUtil.CPB_PARENT_LIST.contains(person.getCdStagePersRelInt())) {
						personSet.add(person.getIdPerson());
						return;
					}
				}
			});

			// If the above loop found a primary child on Stage having as Parent
			// in current stage, find the other parent and return.
			if (personSet.size() == 1) {
				personInStageList.stream().forEach(person -> {
					if (!StringUtils.isEmpty(person.getDtPersonDeath())) {
						if (SSCCPlcmtOptCircumUtil.CPB_PARENT_LIST.contains(person.getCdStagePersRelInt())) {
							personSet.add(person.getIdPerson());
						}
						if (personSet.size() == 2) {
							return;
						}
					}
				});
			}

		}
		log.info("getValidCPBParents method in SSCCPlcmtOptCircumServiceImpl : Returning personSet.");
		return personSet;
	}

	/**
	 * Method Name: setUpSSCCPlcmtOptCircum Method Description: The method is
	 * called before loading the SSCCPlcmtOptCircum data.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void setUpSSCCPlcmtOptCircum(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("setUpSSCCPlcmtOptCircum method in SSCCPlcmtOptCircumServiceImpl : Execution Started.");
		Long idPrimaryChild = stageUtilityDao.findPrimaryChildForStage(ssccPlcmtOptCircumDto.getIdStage());
		ssccPlcmtOptCircumDto.setIdPrimaryChild(idPrimaryChild);
		// Set primary Child's detail to SSCCPlcmtOptCircumDto.
		Date personDob = personDao.getPerson(idPrimaryChild).getDtPersonBirth();
		ssccPlcmtOptCircumDto.setDtPersonBirth(personDob);
		// Get active referral Id For Stage.
		List<SSCCRefDto> activeSSCCRef = (List<SSCCRefDto>) ssccRefDao
				.fetchActiveSSCCRefForStage(ssccPlcmtOptCircumDto.getIdStage(), ServiceConstants.PLACEMENT_REFERRAL);
		// If the activessccref are 0 or more than 1, set the active ref id as 0
		if (!ObjectUtils.isEmpty(activeSSCCRef) && activeSSCCRef.size() == 1) {
			// get the referall dto.
			SSCCRefDto activeRef = activeSSCCRef.get(0);
			ssccPlcmtOptCircumDto.setIdActiveRef(activeRef.getIdSSCCReferral());
			// Set the dt Recorded Referral. This need to validate the placement
			// start date before saving placement Info.
			ssccPlcmtOptCircumDto.setDtSSCCRefRecorded(StringUtils.isEmpty(activeRef.getDtRecordedDfps())
					? activeRef.getDtRecorded() : activeRef.getDtRecordedDfps());
			// fetch and set the SSCC Resource DTO for display on the Page.
			SSCCResourceDto ssccRsrcDto = ssccRefDao.fetchSSCCResourceInfo(activeRef.getCdCntrctRegion(),
					activeRef.getIdSSCCCatchment());
			ssccPlcmtOptCircumDto.setSsccResourceDto(ssccRsrcDto);
			// check for the page mode with respect to the user.
			if (ssccRefDao.isUserSSCCExternal(ssccPlcmtOptCircumDto.getIdUser(), ssccRsrcDto.getCdSSCCCatchment())) {
				ssccPlcmtOptCircumDto.setRole(ServiceConstants.ROLESSCC);
				if (!ssccRefDao.userHasSSCCCatchmentAccess(ssccPlcmtOptCircumDto.getIdUser(),
						ssccRsrcDto.getCdSSCCCatchment())) {
					ssccPlcmtOptCircumDto.setPageMode(ServiceConstants.PAGE_MODE_VIEW);
				}
			} else {
				ssccPlcmtOptCircumDto.setRole(ServiceConstants.ROLEDFPS);
			}
		} else {
			ssccPlcmtOptCircumDto.setIdActiveRef(ServiceConstants.ZERO_VAL);
			ssccPlcmtOptCircumDto.setPageMode(ServiceConstants.PAGE_MODE_VIEW);
		}
		log.info("setUpSSCCPlcmtOptCircum method in SSCCPlcmtOptCircumServiceImpl : Returning resonse.");
	}

	/**
	 * Method Name: setSelTypeAndData Method Description: This method sets the
	 * default radio button selection for each input medical consenter
	 * 
	 * @param ssccPlcmtMedCnsntrDto
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtMedCnsntrDto setSelTypeAndData(SSCCPlcmtMedCnsntrDto ssccPlcmtMedCnsntrDto,
			SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("setSelTypeAndData method in SSCCPlcmtOptCircumServiceImpl : Execution Started.");
		Map<Long, String> validPersons = ssccPlcmtOptCircumDto.getMedCnsntrOptions();

		ssccPlcmtMedCnsntrDto.setPerSelType(ServiceConstants.EMPTY_STRING);
		ssccPlcmtMedCnsntrDto.setIdMedConsenterPersonSl(ServiceConstants.ZERO_VAL);
		if (SSCCPlcmtOptCircumUtil.isNonZeroLong(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson())) {
			// Set the personsel type and id from the valid person map.
			Map.Entry<Long, String> person = validPersons.entrySet().stream()
					.filter(entry -> entry.getKey().equals(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson())).findAny()
					.orElse(null);
			if (!ObjectUtils.isEmpty(person)) {
				ssccPlcmtMedCnsntrDto.setPerSelType(ServiceConstants.PERS_LIST);
				ssccPlcmtMedCnsntrDto.setIdMedConsenterPersonSl(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson());
			}

		}
		// If the persSelType is still null assign the value
		if (StringUtils.isEmpty(ssccPlcmtMedCnsntrDto.getPerSelType())) {
			if (!StringUtils.isEmpty(ssccPlcmtMedCnsntrDto.getNmPersonFirst())) {
				ssccPlcmtMedCnsntrDto.setPerSelType(ServiceConstants.PERS_NM);
			}
			if (ServiceConstants.ZERO_VAL.equals(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson())) {
				List<Long> personList = ssccPlcmtOptCircumDao.findMedCnsntrPersByNmDobSsn(ssccPlcmtMedCnsntrDto);
				if (!ObjectUtils.isEmpty(personList)) {
					ssccPlcmtMedCnsntrDto.setIdMedConsenterPerson(personList.get(0));
				}
			}
		}

		// If persSelType is not calculated above We shall calculate with
		// display name.
		if (StringUtils.isEmpty(ssccPlcmtMedCnsntrDto.getPerSelType())
				&& SSCCPlcmtOptCircumUtil.isNonZeroLong(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson())) {
			ssccPlcmtMedCnsntrDto.setPerSelType(ServiceConstants.PERS_ID);
			// ssccPlcmtMedCnsntrDto.setIdMedConsenterPerson(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson());
		}

		// Set the Medical Concenter Name for Select type.
		if (!StringUtils.isEmpty(ssccPlcmtMedCnsntrDto.getPerSelType())
				&& (SSCCPlcmtOptCircumUtil.isNonZeroLong(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson()))) {
			Person personDomain = personDao.getPersonByPersonId(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson());
			if (!ObjectUtils.isEmpty(personDomain) && (personDomain.getNames().size() > 0)) {
				Name persNameDomain = nameDao.getActivePrimaryName(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson());
				if (!ObjectUtils.isEmpty(persNameDomain)) {
					ssccPlcmtMedCnsntrDto.setNmPersonFirst(persNameDomain.getNmNameFirst());
					ssccPlcmtMedCnsntrDto.setNmPersonLast(persNameDomain.getNmNameLast());
				} else {
					ssccPlcmtMedCnsntrDto.setNmPersonFirst(ServiceConstants.EMPTY_STRING);
					ssccPlcmtMedCnsntrDto.setNmPersonLast(ServiceConstants.EMPTY_STRING);
				}

				if (!StringUtils.isEmpty(personDomain.getDtPersonBirth())) {
					ssccPlcmtMedCnsntrDto.setDtPersonBirth(personDomain.getDtPersonBirth());
					ssccPlcmtMedCnsntrDto.setAge(DateUtils.getAge(ssccPlcmtMedCnsntrDto.getDtPersonBirth()));
				}

				String persSSN = ssccPlcmtOptCircumDao.getSSNByPerson(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson());
				if (!StringUtils.isEmpty(persSSN)) {
					ssccPlcmtMedCnsntrDto.setNbrPersonSsn(persSSN);
				}
				ssccPlcmtMedCnsntrDto.setValidateFlag(Boolean.FALSE);

				// Set new attributes for phase III medical consenter display
				AddressValueDto addressDto = addressDao
						.fetchCurrentPrimaryAddress(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson());
				if (!ObjectUtils.isEmpty(addressDto)) {
					// set county
					ssccPlcmtMedCnsntrDto.setCounty(addressDto.getCounty());
					// set state
					ssccPlcmtMedCnsntrDto.setState(addressDto.getState());
				}
				// set marital Status Code
				ssccPlcmtMedCnsntrDto.setCdMaritalStatus(personDomain.getCdPersonMaritalStatus());
				// set full name
				ssccPlcmtMedCnsntrDto
						.setFullName(formattingUtil.formatName(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson()));
				// get comments from designated medical consenters from network
				// resource agency
				SSCCPlcmntRsrcLinkMCDto ssccPlcmtRsrcLinkMCDto = new SSCCPlcmntRsrcLinkMCDto();
				ssccPlcmtRsrcLinkMCDto.setIdRsrcSSCC(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcSSCC());
				// for agency homes use idrsrcagency else id facility
				if (CodesConstant.CSSCCMDC_90.equals(ssccPlcmtMedCnsntrDto.getCdMedCnsntrSelectType())
						&& (CodesConstant.CMCTYPE_FBUP.equals(ssccPlcmtMedCnsntrDto.getCdMedConsenterType())
								|| CodesConstant.CMCTYPE_SBUP.equals(ssccPlcmtMedCnsntrDto.getCdMedConsenterType()))) {
					ssccPlcmtRsrcLinkMCDto
							.setIdRsrcMember(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcAgency());
				} else {
					ssccPlcmtRsrcLinkMCDto
							.setIdRsrcMember(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcFacil());
				}
				ssccPlcmtRsrcLinkMCDto.setIdMedConsenterPerson(ssccPlcmtMedCnsntrDto.getIdMedConsenterPerson());
				// get resource link record for the above set parameters
				ssccPlcmtRsrcLinkMCDto = ssccPlcmtNtwrkDao.getRsrcMedCnsntrByRsrcPrsn(ssccPlcmtRsrcLinkMCDto);
				// set comment from medical consenter resource link table
				ssccPlcmtMedCnsntrDto.setTxtComment(ssccPlcmtRsrcLinkMCDto.getTxtComment());
			}
		}
		log.info("setSelTypeAndData method in SSCCPlcmtOptCircumServiceImpl : Returning Response.");
		return ssccPlcmtMedCnsntrDto;
	}

	/**
	 * Method Name: setMedCnsntrInfoMsgs Method Description: This method sets
	 * the Error messages in sequence
	 * 
	 * @param cdMedCnsntrSelectType
	 * @return errorMessageList
	 */
	private List<String> setMedCnsntrInfoMsgs(String cdMedCnsntrSelectType) {
		log.info("setMedCnsntrInfoMsgs method in SSCCPlcmtOptCircumServiceImpl : Execution Started.");
		List<String> errorMessageList = new ArrayList<>();
		// Set the First Error Message.
		switch (cdMedCnsntrSelectType) {
		case CodesConstant.CSSCCMDC_60:
			errorMessageList.add(lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_YTH_OVER_18));
			break;
		case CodesConstant.CSSCCMDC_70:
			errorMessageList.add(lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_YTH_CPB));
			break;
		default:
			errorMessageList.add(lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_INFO));
			break;
		}
		// Set the Line 2 and 3 Error Messages.
		if (CodesConstant.CSSCCMDC_10.equals(cdMedCnsntrSelectType)
				|| CodesConstant.CSSCCMDC_80.equals(cdMedCnsntrSelectType)) {
			errorMessageList.add(lookupDao.addMessageParameter(
					lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_PRM_INFO),
					ServiceConstants.MED_CNSNTR_VIEW10_PRM_INFO));
			errorMessageList.add(lookupDao.addMessageParameter(
					lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_SEC_INFO),
					ServiceConstants.MED_CNSNTR_VIEW10_SEC_INFO));
		} else if (CodesConstant.CSSCCMDC_20.equals(cdMedCnsntrSelectType)
				|| CodesConstant.CSSCCMDC_90.equals(cdMedCnsntrSelectType)) {
			errorMessageList.add(lookupDao.addMessageParameter(
					lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_PRM_INFO),
					ServiceConstants.MED_CNSNTR_VIEW20_PRM_INFO));
			errorMessageList.add(lookupDao.addMessageParameter(
					lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_SEC_INFO),
					ServiceConstants.MED_CNSNTR_VIEW20_SEC_INFO));
		} else if (CodesConstant.CSSCCMDC_30.equals(cdMedCnsntrSelectType)
				|| CodesConstant.CSSCCMDC_100.equals(cdMedCnsntrSelectType)) {
			errorMessageList.add(lookupDao.addMessageParameter(
					lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_PRM_INFO),
					ServiceConstants.MED_CNSNTR_VIEW30_PRM_INFO));
			errorMessageList.add(lookupDao.addMessageParameter(
					lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_SEC_INFO),
					ServiceConstants.MED_CNSNTR_VIEW30_SEC_INFO));
		} else if (CodesConstant.CSSCCMDC_40.equals(cdMedCnsntrSelectType)) {
			errorMessageList.add(lookupDao.addMessageParameter(
					lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_PRM_INFO),
					ServiceConstants.MED_CNSNTR_VIEW40_PRM_INFO));
			errorMessageList.add(lookupDao.addMessageParameter(
					lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_SEC_INFO),
					ServiceConstants.MED_CNSNTR_VIEW40_SEC_INFO));
		} else if (CodesConstant.CSSCCMDC_50.equals(cdMedCnsntrSelectType)) {
			errorMessageList.add(lookupDao.addMessageParameter(
					lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_PRM_INFO),
					ServiceConstants.MED_CNSNTR_VIEW50_PRM_INFO));
			errorMessageList.add(lookupDao.addMessageParameter(
					lookupDao.getMessageByNumber(ServiceConstants.MSG_SSCC_PLCMT_MC_SEC_INFO),
					ServiceConstants.MED_CNSNTR_VIEW50_SEC_INFO));
		}
		log.info("setMedCnsntrInfoMsgs method in SSCCPlcmtOptCircumServiceImpl : Returning Errorm Message List.");
		return errorMessageList;
	}

	/**
	 * Method Name: getDesigneeOptions Method Description: Method retuns the
	 * Designee Map Sorted.
	 * 
	 * @param validDesignee
	 * @return designeeOptions
	 */
	private TreeMap<Long, String> getDesigneeOptions(Set<Long> validDesignee) {
		// Create the map of valid person sorted on the Person Name.
		Map<Long, String> unsortedMap = new HashMap<>();
		validDesignee.forEach(person -> {
			unsortedMap.put(person, formattingUtil.formatName(person));
		});
		TreeMap<Long, String> designeeOptions = new TreeMap<>(new ValueComparator(unsortedMap));
		designeeOptions.putAll(unsortedMap);
		return designeeOptions;
	}

	/**
	 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Custom
	 * Comparator for creating sorted map sorted by Person name. Aug 14, 2018-
	 * 7:06:15 PM © 2017 Texas Department of Family and Protective Services
	 */
	private class ValueComparator implements Comparator<Long> {
		private Map<Long, String> map;

		public ValueComparator(Map<Long, String> map) {
			this.map = map;
		}

		@Override
		public int compare(Long a, Long b) {
			return map.get(a).compareTo(map.get(b));
		}
	}

	/**
	 * Method Name: readInitRsrcPers Method Description: This service method set
	 * the Initial Resource and Person values. Base on the Option or
	 * Circumstance Resource Address or Phone Should be read from orginal Table
	 * or Entity Tables.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto readInitRsrcPers(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("readInitRsrcPers method in SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// check how to read the AddressPhone details, If the details to be read
		// from Orginal table.
		if (!SSCCPlcmtOptCircumUtil.readAddressPhone(ssccPlcmtOptCircumDto)) {
			// If it is circumstance return the dto.
			if (!ssccPlcmtOptCircumDto.getIsOption()) {
				return ssccPlcmtOptCircumDto;
			} else {
				// If the record is an Option, modify the ssccplcmentNameDto to
				// set the name of th resource.
				SSCCPlcmtNameDto ssccPlcmtNameDto = ssccPlcmtOptCircumDto.getSsccPlcmtNameDto();
				// Before setting the Resource and view. Check and set
				// enablePlcmtRsrcView or enablePlcmtSilView.
				List<String> exLivArrOptions = lookupDao.getCategoryListingDecode(CodesConstant.CLASIL);
				ssccPlcmtOptCircumDto
						.setEnablePlcmtRsrcView(!exLivArrOptions.contains(ssccPlcmtNameDto.getCdPlcmtLivArr()));
				ssccPlcmtOptCircumDto
						.setEnablePlcmtSilView(exLivArrOptions.contains(ssccPlcmtNameDto.getCdPlcmtLivArr()));
				ssccPlcmtNameDto.setNmRsrcFacil(ssccPlcmtOptCircumDto.getEntityDto().getNmEntity());
				// Read the Resource Detail if applicable.
				if (!ObjectUtils.isEmpty(ssccPlcmtNameDto.getIdRsrcAgency())) {
					ssccPlcmtNameDto.setNmRsrcAgency(
							resourceDao.getResourceDtl(ssccPlcmtNameDto.getIdRsrcAgency()).getNmResource());
				}
				// set the modified SSCCPlcmtNameDto back to main dto and return
				ssccPlcmtOptCircumDto.setSsccPlcmtNameDto(ssccPlcmtNameDto);
			}
			return ssccPlcmtOptCircumDto;
		}
		// If the Placement a Circumstance then read the details from entity
		// Table.
		if (!ssccPlcmtOptCircumDto.getIsOption()) {
			SSCCPlcmtCircumstanceDto ssccPlcmtCircumDto = ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto();
			if (!StringUtils.isEmpty(ssccPlcmtCircumDto.getIdPlcmtPerson())) {
				ssccPlcmtOptCircumDto.getEntityDto()
						.setNmEntity(formattingUtil.formatName(ssccPlcmtCircumDto.getIdPlcmtPerson()));
				AddrPhoneDto addressPhoneDto = addrPhoneRtrvSvc
						.callAddrPhoneRtrvService(ssccPlcmtCircumDto.getIdPlcmtPerson());
				if (!ObjectUtils.isEmpty(addressPhoneDto)) {
					if (ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getEntityAddressDto())) {
						ssccPlcmtOptCircumDto.setEntityAddressDto(new EntityAddressDto());
					}
					ssccPlcmtOptCircumDto.getEntityAddressDto().setAddrStLn1(addressPhoneDto.getAddrPersAddrStLn1());
					ssccPlcmtOptCircumDto.getEntityAddressDto().setAddrStLn2(addressPhoneDto.getAddrPersAddrStLn2());
					ssccPlcmtOptCircumDto.getEntityAddressDto().setAddrCity(addressPhoneDto.getAddrCity());
					ssccPlcmtOptCircumDto.getEntityAddressDto().setCdState(addressPhoneDto.getCdAddrState());
					ssccPlcmtOptCircumDto.getEntityAddressDto().setCdCounty(addressPhoneDto.getCdAddrCounty());
					ssccPlcmtOptCircumDto.getEntityAddressDto().setAddrZip(addressPhoneDto.getAddrZip());
				}
			}
		} else {
			// If Placement is an Option. update the Resource Information from
			// Original Resource Table.
			SSCCPlcmtNameDto ssccPlcmtNameDto = ssccPlcmtOptCircumDto.getSsccPlcmtNameDto();
			// Before setting the Resource and view. Check and set
			// enablePlcmtRsrcView or enablePlcmtSilView.
			List<String> exLivArrOptions = lookupDao.getCategoryListingDecode(CodesConstant.CLASIL);
			ssccPlcmtOptCircumDto
					.setEnablePlcmtRsrcView(!exLivArrOptions.contains(ssccPlcmtNameDto.getCdPlcmtLivArr()));
			ssccPlcmtOptCircumDto.setEnablePlcmtSilView(exLivArrOptions.contains(ssccPlcmtNameDto.getCdPlcmtLivArr()));
			if (!ObjectUtils.isEmpty(ssccPlcmtNameDto.getIdRsrcFacil())) {
				ResourceDto resource = resourceDao.getResourceDtl(ssccPlcmtNameDto.getIdRsrcFacil());
				ssccPlcmtOptCircumDto = getResourceDtl(ssccPlcmtOptCircumDto, resource, false);
			}
		}
		log.info("readInitRsrcPers method in SSCCPlcmtOptCircumServiceImpl : Return Response");
		return ssccPlcmtOptCircumDto;

	}

	/**
	 * Method Name: getResourceDtl Method Description: Method alters the values
	 * in SSCCPlacementOptCircum bean on with respect to the Given Resource.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @param resourceDto
	 * @param validate
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private SSCCPlcmtOptCircumDto getResourceDtl(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto, ResourceDto resourceDto,
			Boolean validate) {
		log.info("getResourceDtl method in SSCCPlcmtOptCircumServiceImpl : Execution Started");
		SSCCPlcmtNameDto ssccPlcmtNameDto = ssccPlcmtOptCircumDto.getSsccPlcmtNameDto();
		ssccPlcmtNameDto.setNmRsrcAgency(!StringUtils.isEmpty(resourceDto.getNmRsrcLinkParent())
				? resourceDto.getNmRsrcLinkParent() : ServiceConstants.EMPTY_STRING);
		ssccPlcmtNameDto.setIdRsrcAgency(resourceDto.getIdRsrcLinkParent());
		ssccPlcmtNameDto.setNmRsrcFacil(!StringUtils.isEmpty(resourceDto.getNmResource()) ? resourceDto.getNmResource()
				: ServiceConstants.EMPTY_STRING);
		ssccPlcmtNameDto.setIdRsrcFacil(resourceDto.getIdResource());
		ssccPlcmtNameDto.setCdRsrcFacilType(!StringUtils.isEmpty(resourceDto.getCdRsrcFacilType())
				? resourceDto.getCdRsrcFacilType() : ServiceConstants.EMPTY_STRING);
		// Call the Placement Info Retrival Service for: csub31s
		PlcmtInfoRsrcRetrivalReq req = new PlcmtInfoRsrcRetrivalReq();
		req.setIdResource(ssccPlcmtNameDto.getIdRsrcFacil());
		req.setAddrPlcmtCnty(ServiceConstants.EMPTY_STRING);
		req.setCdRsrcFacilType(ssccPlcmtNameDto.getCdRsrcFacilType());
		req.setCdPlcmtType(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdSSCCPlcmtType());
		req.setIdPlcmtChild(ssccPlcmtOptCircumDto.getIdPrimaryChild());
		req.setDtPlcmtStart(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart());
		req.setIndPlcmetEmerg(ServiceConstants.N);
		req.setIdStage(ssccPlcmtOptCircumDto.getIdStage());
		List<FacilityServiceTypeOutDto> facilitySvcList = plcmtInfoRtrvSvc.getPlacementRsrcInfo(req)
				.getFacilityServiceTypeDtoList();
		// End: Call the Placement Info Retrival Service for: csub31s

		List<String> exLvngArrOptions = new ArrayList<>();

		boolean facilTypGRO = CodesConstant.CFACTYP2_80
				.equals(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdRsrcFacilType());
		boolean facilTypRTC = CodesConstant.CFACTYP2_64
				.equals(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdRsrcFacilType());
		boolean facilTypSIL = CodesConstant.CFACTYP2_93
				.equals(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdRsrcFacilType());
		boolean cpaAgencyHms = SSCCPlcmtOptCircumUtil.CPA_FOSTER_OR_PRE_CONSUM_PRNTS
				.contains(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdRsrcFacilType());
		// Set/Reset the FLAGS ENABLE_PLCMT_OPT_SIL_VIEW and
		// ENABLE_PLCMT_OPT_RSRC_VIEW
		ssccPlcmtOptCircumDto.setEnablePlcmtSilView(Boolean.FALSE);
		ssccPlcmtOptCircumDto.setEnablePlcmtRsrcView(Boolean.TRUE);
		ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().setEnablePlcmtOptSILView(Boolean.FALSE);
		ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().setEnablePlcmtOptRSRCView(Boolean.TRUE);
		ssccPlcmtOptCircumDto.setExcludeLvngArngOptns(new ArrayList<>());

		int cntLvngArngmnt = !ObjectUtils.isEmpty(facilitySvcList) ? facilitySvcList.size() : ServiceConstants.Zero_INT;

		if ((facilTypGRO || facilTypRTC) && ServiceConstants.Zero_INT == cntLvngArngmnt && validate) {
			ssccPlcmtOptCircumUtil.setInformationMsg(ServiceConstants.MSG_NO_FACIL_TYPE_EXISTS, ssccPlcmtOptCircumDto);
		} else if ((facilTypGRO || facilTypRTC) && cntLvngArngmnt > ServiceConstants.Zero_INT) {
			ssccPlcmtOptCircumDto.setLvngArngCodeTbl(CodesConstant.CLARES);
			exLvngArrOptions = lookupDao.getCategoryListingDecode(CodesConstant.CLARES);
			List<String> lvngArngFrmPlcmnt = facilitySvcList.stream().map(FacilityServiceTypeOutDto::getCdPlcmtLivArr)
					.collect(Collectors.toList());
			lvngArngFrmPlcmnt = lvngArngFrmPlcmnt.stream().filter(a -> SSCCPlcmtOptCircumUtil.checkGroResCodes(a))
					.collect(Collectors.toList());
			exLvngArrOptions.removeAll(lvngArngFrmPlcmnt);
		} else if (facilTypSIL) {
			ssccPlcmtOptCircumDto.setLvngArngCodeTbl(CodesConstant.CLASIL);
			exLvngArrOptions = silInSSCCValidation(ssccPlcmtOptCircumDto, validate);
			if (validate) {
				ssccPlcmtOptCircumDto.setEntityAddressDto(null);
				ssccPlcmtOptCircumDto.setEntityPhoneDto(null);
				if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getErrorMsgList())) {
					exLvngArrOptions = null;
					ssccPlcmtNameDto.setNmRsrcAgency(null);
					ssccPlcmtNameDto.setIdRsrcAgency(null);
					ssccPlcmtNameDto.setNmRsrcFacil(null);
					ssccPlcmtNameDto.setIdRsrcFacil(null);
					ssccPlcmtNameDto.setCdRsrcFacilType(null);
				}
			}
			// Set/Reset the FLAGS ENABLE_PLCMT_OPT_SIL_VIEW and
			// ENABLE_PLCMT_OPT_RSRC_VIEW
			ssccPlcmtOptCircumDto.setEnablePlcmtSilView(Boolean.TRUE);
			ssccPlcmtOptCircumDto.setEnablePlcmtRsrcView(Boolean.FALSE);
			ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().setEnablePlcmtOptSILView(Boolean.TRUE);
			ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().setEnablePlcmtOptRSRCView(Boolean.FALSE);
		} else if (cpaAgencyHms) {
			ssccPlcmtOptCircumDto.setLvngArngCodeTbl(CodesConstant.CFACTYP2);
			exLvngArrOptions = ssccPlcmtOptCircumUtil.getCpaAgncyHmsCodes();
			if (validate) {
				ssccPlcmtOptCircumDto.getSsccPlcmtNameDto()
						.setCdPlcmtLivArr(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdRsrcFacilType());
			}
		} else {
			ssccPlcmtOptCircumDto.setLvngArngCodeTbl(CodesConstant.CFACTYP2);
			exLvngArrOptions = ssccPlcmtOptCircumUtil.getDefaultCodes();
			if (CodesConstant.CFACTYP2_67.equals(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdRsrcFacilType())
					&& validate) {
				ssccPlcmtOptCircumDto.getSsccPlcmtNameDto()
						.setCdPlcmtLivArr(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdRsrcFacilType());
			}
		}
		// Find the Primary address for the Resource
		ResourceAddressDto addressDetail = SSCCPlcmtOptCircumUtil.findResourcePrimaryAddress(resourceDto);
		if (!ObjectUtils.isEmpty(addressDetail) && ssccPlcmtOptCircumDto.getEnablePlcmtRsrcView()) {
			EntityAddressDto entityAddress = new EntityAddressDto();
			entityAddress.setAddrStLn1(addressDetail.getAddrRsrcAddrStLn1());
			entityAddress.setAddrStLn2(addressDetail.getAddrRsrcAddrStLn2());
			entityAddress.setAddrCity(addressDetail.getAddrRsrcAddrCity());
			entityAddress.setCdState(addressDetail.getCdRsrcAddrState());
			entityAddress.setCdCounty(addressDetail.getCdRsrcAddrCounty());
			entityAddress.setAddrZip(addressDetail.getAddrRsrcAddrZip());
			ssccPlcmtOptCircumDto.setEntityAddressDto(entityAddress);
		}
		ssccPlcmtOptCircumDto.setExcludeLvngArngOptns(exLvngArrOptions);
		ssccPlcmtOptCircumDto.setSsccPlcmtNameDto(ssccPlcmtNameDto);
		log.info("getResourceDtl method in SSCCPlcmtOptCircumServiceImpl : Return Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: silInSSCCValidation Method Description: This method will be
	 * called when placement type is 30 and resource facility type is SIL.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @param searchResultDto
	 * @return List<String>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<String> silInSSCCValidation(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto, boolean validate) {
		log.info("silInSSCCValidation method in SSCCPlcmtOptCircumServiceImpl : Execution Started");
		boolean indError = false;
		List<String> exLivArrSIL = new ArrayList<>();
		int persAge = ServiceConstants.Zero_INT;
		boolean isPersBdMonthSamePlcmtStartDtMonth = false;

		if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getDtPersonBirth())) {
			persAge = DateUtils.getPersonListAge(ssccPlcmtOptCircumDto.getDtPersonBirth(),
					ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart());
			// Check if person birth month is same as placement month.
			if (ssccPlcmtOptCircumDto.getDtPersonBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					.getMonthValue() == ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue()) {
				isPersBdMonthSamePlcmtStartDtMonth = true;
			}
		}
		if (validate) {
			if (persAge < ServiceConstants.AGE_MAX_DIFF) {
				Date prntDtPlcmtSt = ssccPlcmtOptCircumDao.getCorrospondingPlcmtDt(ssccPlcmtOptCircumDto.getIdStage(),
						ssccPlcmtOptCircumDto.getSsccResourceDto().getIdSSCCResource(),
						ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcFacil());
				if (ObjectUtils.isEmpty(prntDtPlcmtSt)) {
					ssccPlcmtOptCircumUtil.setErrorMessage(ServiceConstants.MSG_SIL_CORRESPOND_PARENT_PLCMT_REQ,
							ssccPlcmtOptCircumDto);
					indError = true;
				} else if (prntDtPlcmtSt.after(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart())) {
					ssccPlcmtOptCircumUtil.setErrorMessage(ServiceConstants.MSG_SIL_CORRESPOND_PARENT_PLCMT_REQ,
							ssccPlcmtOptCircumDto);
					indError = true;
				} else if (CodesConstant.CSTGTYPE_REG.equals(ssccPlcmtOptCircumDto.getCdStageType())) {
					ssccPlcmtOptCircumDto.setDispSilPrntDfps(Boolean.TRUE);
				}
			} else if (persAge >= ServiceConstants.AGE_MAX_DIFF && !(persAge < ServiceConstants.AGE_22
					|| (persAge == ServiceConstants.AGE_22 && isPersBdMonthSamePlcmtStartDtMonth))) {
				ssccPlcmtOptCircumUtil.setErrorMessage(ServiceConstants.MSG_SIL_YOUTH_AGE_VALIDATION,
						ssccPlcmtOptCircumDto);
				indError = true;
			}

			if (!indError) {
				ssccPlcmtOptCircumUtil.setInformationMsg(ServiceConstants.MSG_SIL_ADDR_VALIDATION,
						ssccPlcmtOptCircumDto);
			}
		}
		exLivArrSIL = filterLivArrSILResourceSvc(ssccPlcmtOptCircumDto, persAge);
		log.info("silInSSCCValidation method in SSCCPlcmtOptCircumServiceImpl : Return Response");
		return exLivArrSIL;
	}

	/**
	 * Method Name: filterLivArrSILResourceSvc Method Description: This Method
	 * filters SIL living Arrangement based on SIL Resource services of '70'
	 * series.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @param personAge
	 * @return List<String>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<String> filterLivArrSILResourceSvc(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto, int personAge) {
		log.info("filterLivArrSILResourceSvc method in SSCCPlcmtOptCircumServiceImpl : Execution Started");
		final List<String> UNDRAGE_SIL_RSRC_SVC = Arrays.asList(CodesConstant.CSILSSCC_SF, CodesConstant.CSILSSCC_SG,
				CodesConstant.CSILSSCC_SH, CodesConstant.CSILSSCC_SI, CodesConstant.CSILSSCC_SJ);
		final List<String> OVRAGE_SIL_RSRC_SVC = Arrays.asList(CodesConstant.CSILSSCC_SA, CodesConstant.CSILSSCC_SB,
				CodesConstant.CSILSSCC_SC, CodesConstant.CSILSSCC_SD, CodesConstant.CSILSSCC_SE, CodesConstant.CSILSSCC_SK, CodesConstant.CSILSSCC_SL,
				CodesConstant.CSILSSCC_SM, CodesConstant.CSILSSCC_SN);
		List<String> exLivArrSIL = new ArrayList<>();
		List<String> allLivArrSIL = lookupDao.getCategoryListingDecode(CodesConstant.CLASIL);
		exLivArrSIL.addAll(allLivArrSIL);
		Long idResource = ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcFacil();
		List<PlacementValueDto> silLivArrList = placementDao.getSILRsrsSvc(idResource);
		if (!ObjectUtils.isEmpty(silLivArrList)) {
			List<String> rsrcSvcFrmPlcmt = silLivArrList.stream().map(PlacementValueDto::getCdResourceService)
					.collect(Collectors.toList());
			// Since the rsrcSvcFrmPlcmt is List of Decodes, convert it to list
			// of codes.
			rsrcSvcFrmPlcmt.replaceAll(r -> lookupDao.encode(CodesConstant.CSILSSCC, r));
			if (personAge < ServiceConstants.AGE_MAX_DIFF) {
				rsrcSvcFrmPlcmt = rsrcSvcFrmPlcmt.stream().filter(s -> UNDRAGE_SIL_RSRC_SVC.contains(s))
						.collect(Collectors.toList());
			} else {
				rsrcSvcFrmPlcmt = rsrcSvcFrmPlcmt.stream().filter(s -> OVRAGE_SIL_RSRC_SVC.contains(s))
						.collect(Collectors.toList());
			}
			exLivArrSIL.removeAll(rsrcSvcFrmPlcmt);
		}
		log.info("filterLivArrSILResourceSvc method in SSCCPlcmtOptCircumServiceImpl : Return Respnse");
		return exLivArrSIL;
	}

	/**
	 * Method Name: fetchSSCCTimeLine Method Description: Method fetches the
	 * SSCCtimeline list if the Timelines are to be shown on SSCC Placement
	 * Option Circumstance Page.
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return List<SSCCTimelineDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SSCCTimelineDto> fetchSSCCTimeLine(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		SSCCTimelineDto ssccTimeline = new SSCCTimelineDto();
		ssccTimeline.setIdStage(ssccPlcmtOptCircumDto.getIdStage());
		ssccTimeline.setIdSsccReferral(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCReferral());
		ssccTimeline.setIdReference(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader());
		ssccTimeline.setCdTimelineTableName(CodesConstant.CSSCCTBL_20);
		List<SSCCTimelineDto> ssccTimeLineDtoList = ssccTimeLineDao.getSSCCTimelineList(ssccTimeline);
		return ssccTimeLineDtoList;
	}

	/**
	 * Method Name: save Method Description: This service method saves the
	 * incoming details that include, sscc placement header, placement name,
	 * medical consenters, placement info, circumstance, placement discussion
	 * creates necessary sscc timelines updates the corresponding SSCC list
	 * records finally sets the created or updated placement header dto to the
	 * response dto
	 * 
	 * @Param ssccPlcmtOptCircumDto
	 * @return ssccPlcmtOptCircumDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto save(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		// If the Placement Info was being edited. Save the savme.
		if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtInfo()) {
			// set the Placement name indDraft to N and Save the record.
			ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setIndDraft(ServiceConstants.N);
			saveSSCCPlcmtName(ssccPlcmtOptCircumDto);
			// Save the Med Concenter Section.
			saveSSCCPlcmtMedCnsntrs(ssccPlcmtOptCircumDto);
			// Set plcmt Info ind draft to N and save the Record.
			ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().setIndDraft(ServiceConstants.N);
			saveSSCCPlcmtInfo(ssccPlcmtOptCircumDto);

			// Set the Status.
			ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(CodesConstant.CSSCCSTA_30);
			// Save the SSCC Time Line Record.
			ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
					ServiceConstants.TIMELINE_ADD_OPTION));
			// Save SSCC fields for Referral.
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_TYPE, ssccPlcmtOptCircumDto);
			saveToSSCCList(SSCCPlcmtOptCircumUtil.DT_PLCMT_OPTION_RECORDED, ssccPlcmtOptCircumDto);
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_STATUS, ssccPlcmtOptCircumDto);

		}
		// If the Placement Discussion Is Being editied.
		if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtDiscuss()) {
			// If IndDocComp checkbox is checked. Change the Header Status. and
			// save to timeline Records.
			if (ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIndDocComp())) {
				ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(CodesConstant.CSSCCSTA_110);
				ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil
						.populateSSCCTimelineValue(ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_PLACED_OPTION));
				saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_STATUS, ssccPlcmtOptCircumDto);
			}
			// Save record to ssccc plcmt placed.
			saveSSCCPlcmtPlaced(ssccPlcmtOptCircumDto);
		}
		// If Circumstance Record is being saved and enabled payment
		// Determination section is enabled and state is in APPROVE_SSCCC_STATE.
		if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEnablePayDeter() || (!ssccPlcmtOptCircumDto.getIsOption()
				&& SSCCPlcmtOptCircumUtil.APPROVE_SSCC_STATE == ssccPlcmtOptCircumDto.getCurrentState())) {
			// Set Ind Draft as N and change the values in circumsntance.
			ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().setIndDraft(ServiceConstants.N);
			saveSSCCPlcmtCircumstance(ssccPlcmtOptCircumDto);
		}
		// Finally Update the Header.
		saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: saveSSCCPlcmtCircumstance Method Description: This method
	 * inserts entity, entity phone, entity address if any and also saves or
	 * updates the placement circumstance
	 * 
	 * @param ssccPlcmtOptCircumDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private void saveSSCCPlcmtCircumstance(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		EntityDomain entityDomain = new EntityDomain();
		EntityPhone entityPhone = new EntityPhone();
		EntityAddress entityAddress = new EntityAddress();
		SsccPlcmtHeader ssccPlcmtHeader = new SsccPlcmtHeader();
		ssccPlcmtHeader.setIdSSCCPlcmtHeader(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader());
		SsccPlcmtCircumstance ssccPlcmtCircumstance = new SsccPlcmtCircumstance();
		SSCCPlcmtCircumstanceDto ssccPlcmtCircumstanceDto = ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto();
		BeanUtils.copyProperties(ssccPlcmtOptCircumDto.getEntityDto(), entityDomain);
		BeanUtils.copyProperties(ssccPlcmtOptCircumDto.getEntityPhoneDto(), entityPhone);
		BeanUtils.copyProperties(ssccPlcmtOptCircumDto.getEntityAddressDto(), entityAddress);
		if (ObjectUtils.isEmpty(ssccPlcmtCircumstanceDto.getIdSSCCPlcmtCircumstance())) {
			// add mode
			ssccPlcmtCircumstanceDto.setIdCreatedPerson(ssccPlcmtOptCircumDto.getIdUser());
			ssccPlcmtCircumstanceDto.setNbrVersion(ssccPlcmtCircumstanceDto.getNbrVersion() + 1l);
			BeanUtils.copyProperties(ssccPlcmtCircumstanceDto, ssccPlcmtCircumstance);
			// Save the Entity Records and then Circumstance.
			entityDomain.setCdEntityType(CodesConstant.CENTYTYP_30);
			if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEnablePlcmtCircumPrsnView()) {
				if (SSCCPlcmtOptCircumUtil.isNonZeroLong(ssccPlcmtOptCircumDto.getEntityPhoneDto().getNbrPhone())) {
					entityDomain.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
					entityDao.saveEntity(entityDomain);
				}
			} else if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEnablePlcmtCircumFacilView()) {
				entityDomain.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
				entityDao.saveEntity(entityDomain);
				entityAddress.setIdEntyAddressOwner(entityDomain.getIdEntity());
				entityAddress.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
				entityDao.saveEntityAddress(entityAddress);
			}
			// Insert the Intity Phone.
			if (SSCCPlcmtOptCircumUtil.isNonZeroLong(ssccPlcmtOptCircumDto.getEntityPhoneDto().getNbrPhone())) {
				entityPhone.setIdEntityPhoneOwner(entityDomain.getIdEntity());
				entityPhone.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
				entityDao.saveEntityPhone(entityPhone);
			}
		} else {
			ssccPlcmtCircumstance = ssccPlcmtOptCircumDao
					.getSSCCPlcmtCircumstance(ssccPlcmtCircumstanceDto.getIdSSCCPlcmtCircumstance());
			ssccPlcmtCircumstanceDto.setIdCreatedPerson(ssccPlcmtCircumstance.getIdCreatedPerson());
			ssccPlcmtCircumstanceDto.setDtCreated(ssccPlcmtCircumstance.getDtCreated());
			BeanUtils.copyProperties(ssccPlcmtCircumstanceDto, ssccPlcmtCircumstance);
			// If Person view and aheader status changed to approve, rescind or
			// reject then save resource name, address, phone in entity table.
			if (SSCCPlcmtOptCircumUtil.isNonZeroLong(ssccPlcmtCircumstanceDto.getIdPlcmtPerson())
					&& !SSCCPlcmtOptCircumUtil.readAddressPhone(ssccPlcmtOptCircumDto)
					&& ssccPlcmtOptCircumDao.checkHeaderStatusChanged(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto())) {
				if (StringUtils.isEmpty(entityDomain.getIdEntity())
						|| ServiceConstants.ZERO.equals(entityDomain.getIdEntity())) {
					entityDomain.setCdEntityType(CodesConstant.CENTYTYP_30);
					entityDomain.setIdCreatedPerson(ssccPlcmtOptCircumDto.getIdUser());
					entityDomain.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
					entityDao.saveEntity(entityDomain);
				}
				entityAddress.setDtCreated(ssccPlcmtCircumstanceDto.getDtCreated());
				entityAddress.setIdEntyAddressOwner(entityDomain.getIdEntity());
				entityDao.saveEntityAddress(entityAddress);
			}
			if (!ssccPlcmtCircumstanceDto.getIndPaymentSsccNotif()
					.equals(ssccPlcmtCircumstance.getIndPaymentSsccNotif())) {
				ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
						ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_SSCC_NOTIF_CIRCUMSTANCE));
			}
		}
		if (!ObjectUtils.isEmpty(entityPhone)) {
			ssccPlcmtCircumstance.setIdEntityPhone(entityPhone.getIdEntityPhone());
		}
		if (!ObjectUtils.isEmpty(entityAddress)) {
			ssccPlcmtCircumstance.setIdEntityAddress(entityAddress.getIdEntityAddress());
		}
		ssccPlcmtCircumstance.setSsccPlcmtHeader(ssccPlcmtHeader);
		ssccPlcmtCircumstance.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
		ssccPlcmtOptCircumDao.saveSsccPlcmtCircumstance(ssccPlcmtCircumstance);
		// Save the saved values back from DB.
		BeanUtils.copyProperties(entityPhone, ssccPlcmtOptCircumDto.getEntityPhoneDto());
		BeanUtils.copyProperties(entityAddress, ssccPlcmtOptCircumDto.getEntityAddressDto());
		BeanUtils.copyProperties(entityDomain, ssccPlcmtOptCircumDto.getEntityDto());
		BeanUtils.copyProperties(ssccPlcmtCircumstance, ssccPlcmtCircumstanceDto);
		ssccPlcmtCircumstanceDto
				.setIdSSCCPlcmtHeader(ssccPlcmtCircumstance.getSsccPlcmtHeader().getIdSSCCPlcmtHeader());
		ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCurrNbrVersion(ssccPlcmtCircumstance.getNbrVersion());
		ssccPlcmtOptCircumDto.setSsccPlcmtCircumstanceDto(ssccPlcmtCircumstanceDto);
	}

	/**
	 * Method Name: saveSSCCPlcmtPlaced Method Description: This methods inserts
	 * or updates SSCCPlcmtPlaced
	 * 
	 * @param ssccPlcmtOptCircumDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private void saveSSCCPlcmtPlaced(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		// from request dto
		SSCCPlcmtPlacedDto ssccPlcmtPlacedDto = ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto();
		// Set the Current version no. from the Header. Applicable in add
		// scenario.
		ssccPlcmtPlacedDto.setNbrVersion(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCurrNbrVersion());
		// initializations
		SsccPlcmtPlaced ssccPlcmtPlaced = new SsccPlcmtPlaced();
		if (!ObjectUtils.isEmpty(ssccPlcmtPlacedDto.getIdSSCCPlcmtPlaced())) {
			ssccPlcmtPlaced = ssccPlcmtOptCircumDao.getSSCCPlcmtPlaced(ssccPlcmtPlacedDto.getIdSSCCPlcmtPlaced());
			ssccPlcmtPlacedDto.setIdCreatedPerson(ssccPlcmtPlaced.getIdCreatedPerson());
			ssccPlcmtPlacedDto.setDtCreated(ssccPlcmtPlaced.getDtCreated());
		}
		ssccPlcmtPlacedDto.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
		BeanUtils.copyProperties(ssccPlcmtPlacedDto, ssccPlcmtPlaced);
		if (ObjectUtils.isEmpty(ssccPlcmtPlaced.getSsccPlcmtHeader())) {
			SsccPlcmtHeader ssccPlcmtHeader = new SsccPlcmtHeader();
			ssccPlcmtHeader.setIdSSCCPlcmtHeader(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader());
			ssccPlcmtPlaced.setSsccPlcmtHeader(ssccPlcmtHeader);
		}
		ssccPlcmtOptCircumDao.saveSSCCPlcmtPlaced(ssccPlcmtPlaced);
		BeanUtils.copyProperties(ssccPlcmtPlaced, ssccPlcmtPlacedDto);
		ssccPlcmtPlacedDto.setIdSSCCPlcmtHeader(ssccPlcmtPlaced.getSsccPlcmtHeader().getIdSSCCPlcmtHeader());
		ssccPlcmtOptCircumDto.setSsccPlcmtPlacedDto(ssccPlcmtPlacedDto);
	}

	/**
	 * Method Name: saveSSCCPlcmtInfo
	 * 
	 * Method Description: This method inserts or updates the sscc placement
	 * information section on the SSCC Placement Option and Circumstance Detail
	 * page
	 * 
	 * @param ssccPlcmtOptCircumDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private void saveSSCCPlcmtInfo(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		// initializations
		SsccPlcmtInfo ssccPlcmtInfo = new SsccPlcmtInfo();
		// from request dto
		SSCCPlcmtInfoDto ssccPlcmtInfoDto = ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto();
		// Set the version no. from the header. Will be applicable in Add
		// Scenario.
		ssccPlcmtInfoDto.setNbrVersion(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCurrNbrVersion());
		if (!ObjectUtils.isEmpty(ssccPlcmtInfoDto.getIdSSCCPlcmtInfo())) {
			// update mode
			ssccPlcmtInfo = ssccPlcmtOptCircumDao.getSSCCPlcmtInfo(ssccPlcmtInfoDto.getIdSSCCPlcmtInfo());
			if (!ssccPlcmtInfoDto.getIndExceptCare().equals(ssccPlcmtInfo.getIndExceptCare())) {
				ssccPlcmtOptCircumDto.setAllowSaveToPlcmtInfo(false);
			}
			ssccPlcmtInfoDto.setIdCreatedPerson(ssccPlcmtInfo.getIdCreatedPerson());
			ssccPlcmtInfoDto.setDtCreated(ssccPlcmtInfo.getDtCreated());
		}
		ssccPlcmtInfoDto.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
		BeanUtils.copyProperties(ssccPlcmtInfoDto, ssccPlcmtInfo);
		if (ObjectUtils.isEmpty(ssccPlcmtInfo.getSsccPlcmtHeader())) {
			SsccPlcmtHeader ssccPlcmtHeader = new SsccPlcmtHeader();
			ssccPlcmtHeader.setIdSSCCPlcmtHeader(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader());
			ssccPlcmtInfo.setSsccPlcmtHeader(ssccPlcmtHeader);
		}
		ssccPlcmtOptCircumDao.saveSSCCPlcmtInfo(ssccPlcmtInfo);
		BeanUtils.copyProperties(ssccPlcmtInfo, ssccPlcmtInfoDto);
		ssccPlcmtInfoDto.setIdSSCCPlcmtHeader(ssccPlcmtInfo.getSsccPlcmtHeader().getIdSSCCPlcmtHeader());
		ssccPlcmtOptCircumDto.setSsccPlcmtInfoDto(ssccPlcmtInfoDto);
	}

	/**
	 * Method Name: saveSSCCPlcmtMedCnsntrs
	 * 
	 * Method Description: This method inserts or updates the Medical consenter
	 * section on the SSCC Placement Option and Circumstance Detail page
	 * 
	 * @param ssccPlcmtOptCircumDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private void saveSSCCPlcmtMedCnsntrs(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		// initializations
		SsccPlcmtHeader ssccPlcmtHeader = new SsccPlcmtHeader();
		ssccPlcmtHeader.setIdSSCCPlcmtHeader(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader());
		// from request dto
		HashMap<String, SSCCPlcmtMedCnsntrDto> ssccPlcmtMedCnsntrDtoMap = ssccPlcmtOptCircumDto
				.getSsccPlcmtMedCnsntrDtoMap();
		if (ssccPlcmtMedCnsntrDtoMap.size() > 0) {
			ssccPlcmtMedCnsntrDtoMap.entrySet().stream().forEach(entry -> {
				SSCCPlcmtMedCnsntrDto ssccPlcmtMedCnsntrDto = entry.getValue();
				SsccPlcmtMedCnsntr ssccPlcmtMedCnsntr = new SsccPlcmtMedCnsntr();
				if (ObjectUtils.isEmpty(ssccPlcmtMedCnsntrDto.getIdSSCCPlcmtMedCnsntr())) {
					// Set the Current Nbr Version from the Header.
					ssccPlcmtMedCnsntrDto
							.setNbrVersion(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCurrNbrVersion());
					// Make the NmPersonFirst/Last Field empty as we need not save the names, and On read 
					// It will always be read from Person table.
					BeanUtils.copyProperties(ssccPlcmtMedCnsntrDto, ssccPlcmtMedCnsntr,"nmPersonFirst","nmPersonLast");
					ssccPlcmtMedCnsntr.setSsccPlcmtHeader(ssccPlcmtHeader);
				} else {
					// update The Med Concenters.
					if (Stream
							.of(CodesConstant.CMCTYPE_FPRI, CodesConstant.CMCTYPE_SPRI, CodesConstant.CMCTYPE_FBUP,
									CodesConstant.CMCTYPE_SBUP)
							.anyMatch(ssccPlcmtMedCnsntrDto.getCdMedConsenterType()::equalsIgnoreCase)) {
						ssccPlcmtMedCnsntr = ssccPlcmtOptCircumDao
								.getSSCCPlcmtMedCnsntr(ssccPlcmtMedCnsntrDto.getIdSSCCPlcmtMedCnsntr());
						ssccPlcmtMedCnsntrDto.setIdCreatedPerson(ssccPlcmtMedCnsntr.getIdCreatedPerson());
						ssccPlcmtMedCnsntrDto.setDtCreated(ssccPlcmtMedCnsntr.getDtCreated());
						ssccPlcmtMedCnsntrDto.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
						if (!ssccPlcmtMedCnsntrDto.getIndBypass().equals(ssccPlcmtMedCnsntr.getIndBypass()))
							ssccPlcmtOptCircumDto.setAllowSaveToPlcmtInfo(false);
						// Make the NmPersonFirst/Last Field empty as we need not save the names, and On read 
						// It will always be read from Person table.
						BeanUtils.copyProperties(ssccPlcmtMedCnsntrDto, ssccPlcmtMedCnsntr,"nmPersonFirst","nmPersonLast");
						ssccPlcmtMedCnsntr.setSsccPlcmtHeader(ssccPlcmtHeader);
						ssccPlcmtMedCnsntr.setIndDraft(ServiceConstants.N);
					}
				}
				ssccPlcmtOptCircumDao.saveSSCCPlcmtMedCnsntr(ssccPlcmtMedCnsntr);
				// set back the saved db values to returning DTO.
				BeanUtils.copyProperties(ssccPlcmtMedCnsntr, ssccPlcmtMedCnsntrDto, "nmPersonFirst","nmPersonLast");
				ssccPlcmtMedCnsntrDto
						.setIdSSCCPlcmtHeader(ssccPlcmtMedCnsntr.getSsccPlcmtHeader().getIdSSCCPlcmtHeader());
				ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(ssccPlcmtMedCnsntr.getCdMedConsenterType(),
						ssccPlcmtMedCnsntrDto);
			});
		}
	}

	/**
	 * Method Name: saveSSCCPlcmtName
	 *
	 * Method Description: This method inserts or updates the sscc placement
	 * Name section on the SSCC Placement Option and Circumstance Detail page
	 *
	 * @param ssccPlcmtOptCircumDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private void saveSSCCPlcmtName(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		// initializations
		EntityDomain entityDomain = new EntityDomain();
		EntityPhone entityPhone = new EntityPhone();
		EntityAddress entityAddress = new EntityAddress();
		SsccPlcmtName ssccPlcmtName = new SsccPlcmtName();
		// from request dto
		SSCCPlcmtNameDto ssccPlcmtNameDto = ssccPlcmtOptCircumDto.getSsccPlcmtNameDto();
		BeanUtils.copyProperties(ssccPlcmtOptCircumDto.getEntityDto(), entityDomain);
		BeanUtils.copyProperties(ssccPlcmtOptCircumDto.getEntityPhoneDto(), entityPhone);
		BeanUtils.copyProperties(ssccPlcmtOptCircumDto.getEntityAddressDto(), entityAddress);
		SsccPlcmtHeader ssccPlcmtHeader = new SsccPlcmtHeader();
		ssccPlcmtHeader.setIdSSCCPlcmtHeader(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader());
		// add mode
		if (ObjectUtils.isEmpty(ssccPlcmtNameDto.getIdSSCCPlcmtName())) {
			// Always increement the current Nbr Version in Name record whenever
			// inserting.
			ssccPlcmtNameDto.setNbrVersion(ssccPlcmtNameDto.getNbrVersion() + 1l);
			BeanUtils.copyProperties(ssccPlcmtNameDto, ssccPlcmtName);
			// Save the entity records and then placement Name
			entityDomain.setNmEntity(ssccPlcmtNameDto.getNmRsrcFacil());
			if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEnablePlcmtOptSILView()) {
				// If SIL View always insert Address, Phone and Entity Name
				entityDomain.setCdEntityType(CodesConstant.CENTYTYP_20);
				entityDomain.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
				entityDao.saveEntity(entityDomain);
				entityAddress.setIdEntyAddressOwner(entityDomain.getIdEntity());
				entityAddress.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
				entityDao.saveEntityAddress(entityAddress);
				entityPhone.setIdEntityPhoneOwner(entityDomain.getIdEntity());
				entityPhone.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
				entityDao.saveEntityPhone(entityPhone);
			} else {
				// Else only insert Phone, if Phone is entered.
				entityDomain.setCdEntityType(CodesConstant.CENTYTYP_30);
				if (SSCCPlcmtOptCircumUtil.isNonZeroLong(entityPhone.getNbrPhone())) {
					entityDao.saveEntity(entityDomain);
					entityPhone.setIdEntityPhoneOwner(entityDomain.getIdEntity());
					entityPhone.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
					entityDao.saveEntityPhone(entityPhone);
				}
			}
		} else {
			// update mode
			// fetch existing SSCC Placement Name from DB
			ssccPlcmtName = ssccPlcmtOptCircumDao.getSSCCPlcmtName(ssccPlcmtNameDto.getIdSSCCPlcmtName());
			ssccPlcmtNameDto.setIdCreatedPerson(ssccPlcmtName.getIdCreatedPerson());
			ssccPlcmtNameDto.setDtCreated(ssccPlcmtName.getDtCreated());
			BeanUtils.copyProperties(ssccPlcmtNameDto, ssccPlcmtName);
			ssccPlcmtName.setSsccPlcmtHeader(ssccPlcmtHeader);
			if (!ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEnablePlcmtOptSILView()
					&& !SSCCPlcmtOptCircumUtil.readAddressPhone(ssccPlcmtOptCircumDto)
					&& ssccPlcmtOptCircumDao.checkHeaderStatusChanged(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto())) {
				if (StringUtils.isEmpty(entityDomain.getIdEntity())
						|| ServiceConstants.ZERO.equals(entityDomain.getIdEntity())) {
					entityDomain.setNmEntity(ssccPlcmtNameDto.getNmRsrcFacil());
					entityDomain.setCdEntityType(CodesConstant.CENTYTYP_30);
					entityDomain.setIdCreatedPerson(ssccPlcmtOptCircumDto.getIdUser());
					entityDomain.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
					entityDao.saveEntity(entityDomain);
				}
				entityAddress.setIdEntyAddressOwner(entityDomain.getIdEntity());
				entityAddress.setDtCreated(ssccPlcmtNameDto.getDtCreated());
				entityDao.saveEntityAddress(entityAddress);
			}
		}
		if (!ObjectUtils.isEmpty(entityPhone)) {
			ssccPlcmtName.setIdEntityPhone(entityPhone.getIdEntityPhone());
		}
		if (!ObjectUtils.isEmpty(entityAddress)) {
			ssccPlcmtName.setIdEntityAddress(entityAddress.getIdEntityAddress());
		}
		ssccPlcmtName.setSsccPlcmtHeader(ssccPlcmtHeader);
		ssccPlcmtName.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
		ssccPlcmtOptCircumDao.saveSSCCPlcmtName(ssccPlcmtName);
		// Set the Saved Values back from the DB.
		BeanUtils.copyProperties(entityPhone, ssccPlcmtOptCircumDto.getEntityPhoneDto());
		BeanUtils.copyProperties(entityAddress, ssccPlcmtOptCircumDto.getEntityAddressDto());
		BeanUtils.copyProperties(entityDomain, ssccPlcmtOptCircumDto.getEntityDto());
		BeanUtils.copyProperties(ssccPlcmtName, ssccPlcmtNameDto);
		ssccPlcmtNameDto.setIdSSCCPlcmtHeader(ssccPlcmtName.getSsccPlcmtHeader().getIdSSCCPlcmtHeader());
		ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCurrNbrVersion(ssccPlcmtName.getNbrVersion());
		ssccPlcmtOptCircumDto.setSsccPlcmtNameDto(ssccPlcmtNameDto);
	}

	/**
	 * Method Name: saveSSCCPlcmtHeader Method Description: This method inserts
	 * or updates the sscc header information for the SSCC Placement Option and
	 * Circumstance Detail page
	 *
	 * @param ssccPlcmtOptCircumDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private void saveSSCCPlcmtHeader(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		// initializations
		Long idSSCCReferral = ssccPlcmtOptCircumDto.getIdActiveRef();
		SsccPlcmtHeader ssccPlcmtHeader = new SsccPlcmtHeader();
		SsccReferral ssccReferral = new SsccReferral();

		// dtos
		SSCCPlcmtHeaderDto ssccPlcmtHeaderDto = ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto();
		// from request dto
		ssccReferral.setIdSSCCReferral(!StringUtils.isEmpty(ssccPlcmtHeaderDto.getIdSSCCReferral())
				? ssccPlcmtHeaderDto.getIdSSCCReferral() : idSSCCReferral);
		// save header record
		if (StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader())) {
			// insert header record
			BeanUtils.copyProperties(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto(), ssccPlcmtHeader);
			ssccPlcmtHeader.setDtCreated(new Date());
		} else {
			// update header record
			ssccPlcmtHeader = ssccPlcmtOptCircumDao
					.readSSCCPlcmtHeader(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader());
			// if dt Plcmt start has changed do not allow to save to placement
			// info
			if (ssccPlcmtOptCircumDao.checkDtPlcmtStartChanged(ssccPlcmtHeaderDto)) {
				ssccPlcmtOptCircumDto.setAllowSaveToPlcmtInfo(false);
			}
			// for a circumstance if Ind 14 Day Plcmt has been selected then
			// update Dt_plcmt_circ_start and expire on SSCC List Table
			if (ServiceConstants.STRING_IND_Y.equals(ssccPlcmtHeaderDto.getInd14dayPlcmt())) {
				if (!ssccPlcmtHeader.getDtPlcmtStart().equals(ssccPlcmtHeaderDto.getDtPlcmtStart())) {
					saveToSSCCList(SSCCPlcmtOptCircumUtil.DT_PLCMT_CIRC_START, ssccPlcmtOptCircumDto);
				}
				if (!ssccPlcmtHeader.getDtPlcmtStart().equals(ssccPlcmtHeaderDto.getDtPlcmtStart())
						|| SSCCPlcmtOptCircumUtil.checkFieldChanged(ssccPlcmtHeader.getDtExpectReturn(),
						ssccPlcmtHeaderDto.getDtExpectReturn())) {
					saveToSSCCList(SSCCPlcmtOptCircumUtil.DT_PLCMT_CIRC_EXPIRE, ssccPlcmtOptCircumDto);
				}
			}
			// if SSCC Fixer is changing of these fields then a timeline record
			// is created
			if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditHeaderForFixer()) {
				if (!ssccPlcmtHeaderDto.getCdSSCCOptionType().equals(ssccPlcmtHeader.getCdSSCCOptionType())) {
					ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
							ssccPlcmtOptCircumDto,
							SSCCPlcmtOptCircumUtil.OPTION_TYPE + ServiceConstants.TIMELINE_FIELD_UPDATE_OPTION));
				}
				if (!ssccPlcmtHeaderDto.getIndPriorComm().equals(ssccPlcmtHeader.getIndPriorComm())) {
					ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
							ssccPlcmtOptCircumDto,
							SSCCPlcmtOptCircumUtil.IND_PRIOR_COMM + ServiceConstants.TIMELINE_FIELD_UPDATE_OPTION));
				}
				if (SSCCPlcmtOptCircumUtil.checkFieldChanged(ssccPlcmtHeader.getDtRecordedSSCC(),
						ssccPlcmtHeaderDto.getDtRecordedSSCC())) {
					ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
							ssccPlcmtOptCircumDto,
							SSCCPlcmtOptCircumUtil.DT_REC_SSCC + ServiceConstants.TIMELINE_FIELD_UPDATE_OPTION));
				}
				if (SSCCPlcmtOptCircumUtil.checkFieldChanged(ssccPlcmtHeader.getDtRecordedDfps(),
						ssccPlcmtHeaderDto.getDtRecordedDfps())) {
					ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
							ssccPlcmtOptCircumDto,
							SSCCPlcmtOptCircumUtil.DT_REC_DFPS + ServiceConstants.TIMELINE_FIELD_UPDATE_OPTION));
				}
			}
			BeanUtils.copyProperties(ssccPlcmtHeaderDto, ssccPlcmtHeader);
		}
		ssccPlcmtHeader.setSsccReferral(ssccReferral);
		ssccPlcmtOptCircumDao.saveSSCCPlcmtHeader(ssccPlcmtHeader);
		BeanUtils.copyProperties(ssccPlcmtHeader, ssccPlcmtHeaderDto);
		ssccPlcmtHeaderDto.setIdSSCCReferral(ssccReferral.getIdSSCCReferral());
		ssccPlcmtOptCircumDto.setSsccPlcmtHeaderDto(ssccPlcmtHeaderDto);
	}

	/**
	 * Method Name: saveSSCCPlcmtNarr Method Description: Method Creates the
	 * SSCC Plcmt Narr with new version.
	 *
	 * @param ssccPlcmtOptCircumDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private void saveSSCCPlcmtNarr(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		SsccPlcmtNarr ssccPlcmtNarr = ssccPlcmtOptCircumDao.getSSCCPlcmtNarrative(
				ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader(),
				ssccPlcmtOptCircumDto.getSsccPlcmtNarrDto().getNbrVersion());
		SsccPlcmtNarr ssccPlcmtNarrUpd = new SsccPlcmtNarr();
		ssccPlcmtNarrUpd.setIdCreatedPerson(ssccPlcmtOptCircumDto.getIdUser());
		ssccPlcmtNarrUpd.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
		ssccPlcmtNarrUpd.setIdDocumentTemplate(ssccPlcmtNarr.getIdDocumentTemplate());
		ssccPlcmtNarrUpd.setSsccPlcmtHeader(ssccPlcmtNarr.getSsccPlcmtHeader());
		ssccPlcmtNarrUpd.setNarrative(ssccPlcmtNarr.getNarrative());
		ssccPlcmtNarrUpd.setNbrVersion(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCurrNbrVersion());
		ssccPlcmtOptCircumDao.saveSSCCPlcmtNarr(ssccPlcmtNarrUpd);
		BeanUtils.copyProperties(ssccPlcmtNarrUpd, ssccPlcmtOptCircumDto.getSsccPlcmtNarrDto());
		ssccPlcmtOptCircumDto.getSsccPlcmtNarrDto()
				.setIdSSCPlcmtHeader(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader());
	}

	/**
	 * Method Name: saveAndContinue Method Description: Service to save the
	 * corrosponding section when Save and Continue Button is clicked on the
	 * page.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return ssccPlcmtOptCircumDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto saveAndContinue(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("saveAndContinue method saveAndContinue in SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// Check for Outstanding status for incoming record
		if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtHeader()
				&& !StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdSSCCPlcmtType())) {
			getDupOptOrCircumStatus(ssccPlcmtOptCircumDto);
		}
		if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtName()) {
			validateMedCnsntrView(ssccPlcmtOptCircumDto);
		}
		if (ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getErrorMsgList())
				&& StringUtils.isEmpty(ssccPlcmtOptCircumDto.getOutstandingStatus())) {
			String indSilPrntDfps = ServiceConstants.N;
			// save header record
			saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
			// init flags to indicate various informational messages that are
			// needed during edit
			ssccPlcmtOptCircumDto.setChangeMedCnsntr(Boolean.FALSE);
			ssccPlcmtOptCircumDto.setChangeExcpCareForLimit(Boolean.FALSE);
			ssccPlcmtOptCircumDto.setChangeExcpCareForSil(Boolean.FALSE);
			ssccPlcmtOptCircumDto.setDupCrGvr(Boolean.FALSE);
			// if placement and location is being edited for Option
			if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtName()) {
				// set indicator draft as true
				ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setIndDraft(ServiceConstants.Y);
				// if in sscc or dfps edit mode
				if (SSCCPlcmtOptCircumUtil.PROPOSE_SSCC_EDIT_STATE == ssccPlcmtOptCircumDto.getCurrentState()
						|| SSCCPlcmtOptCircumUtil.PROPOSE_DFPS_EDIT_STATE == ssccPlcmtOptCircumDto.getCurrentState()
						&& SSCCPlcmtOptCircumUtil.isNonZeroLong(
						ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdSSCCPlcmtName())) {
					// delete temporary placement name and reinsert with
					// new version number
					ssccPlcmtOptCircumDao
							.deletePlcmtName(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdSSCCPlcmtName());
					ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setIdSSCCPlcmtName(null);
					// Code to Delete Entity Records.
					entityDao.deleteEntityRecords(ssccPlcmtOptCircumDto.getEntityDto(),
							ssccPlcmtOptCircumDto.getEntityAddressDto(), ssccPlcmtOptCircumDto.getEntityPhoneDto());
					ssccPlcmtOptCircumDto.getEntityDto().setIdEntity(null);
					ssccPlcmtOptCircumDto.getEntityAddressDto().setIdEntityAddress(null);
					ssccPlcmtOptCircumDto.getEntityPhoneDto().setIdEntityPhone(null);
					ssccPlcmtOptCircumDto.getSsccPlcmtNameDto()
							.setNbrVersion(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCurrNbrVersion() - 1);
				}
				saveSSCCPlcmtName(ssccPlcmtOptCircumDto);
				indSilPrntDfps = ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIndSilPrntDfps();
			}
			// if placement circumstance is being edited that it in edit mode
			if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtCircumstance()) {
				// Delete temp circumstance record and re insert with new
				// version
				// number
				if (SSCCPlcmtOptCircumUtil.isNonZeroLong(
						ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().getIdSSCCPlcmtCircumstance())) {
					ssccPlcmtOptCircumDao.deletePlcmtCircumstance(
							ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().getIdSSCCPlcmtCircumstance());
					ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().setIdSSCCPlcmtCircumstance(null);
					// Code to Delete Entity Records.
					entityDao.deleteEntityRecords(ssccPlcmtOptCircumDto.getEntityDto(),
							ssccPlcmtOptCircumDto.getEntityAddressDto(), ssccPlcmtOptCircumDto.getEntityPhoneDto());
					ssccPlcmtOptCircumDto.getEntityDto().setIdEntity(null);
					ssccPlcmtOptCircumDto.getEntityAddressDto().setIdEntityAddress(null);
					ssccPlcmtOptCircumDto.getEntityPhoneDto().setIdEntityPhone(null);
					ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto()
							.setNbrVersion(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCurrNbrVersion() - 1);
				}
				ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().setIndDraft(ServiceConstants.Y);
				saveSSCCPlcmtCircumstance(ssccPlcmtOptCircumDto);
			}
			// if med consenter is being modified
			if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtMedCnsntr()) {
				// in the setup section for edit method
				if (SSCCPlcmtOptCircumUtil.PROPOSE_SSCC_EDIT_STATE == ssccPlcmtOptCircumDto.getCurrentState()
						|| SSCCPlcmtOptCircumUtil.PROPOSE_DFPS_EDIT_STATE == ssccPlcmtOptCircumDto.getCurrentState()) {
					// Delete the med Concenter
					if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap())) {
						List<Long> idMdclConsenters = ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().values()
								.stream().map(o -> o.getIdSSCCPlcmtMedCnsntr()).collect(Collectors.toList()).stream()
								.filter(o -> !StringUtils.isEmpty(o)).collect(Collectors.toList());
						ssccPlcmtOptCircumDao.deletePlcmtMedCnsntrs(idMdclConsenters);
						ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().entrySet().forEach(entry -> {
							entry.getValue().setIdSSCCPlcmtMedCnsntr(null);
						});
					}
				}
				// save med consenter records
				saveSSCCPlcmtMedCnsntrs(ssccPlcmtOptCircumDto);
			}
			// and save to placement info table
			if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtInfo()) {
				// Delete the saved record.
				if (SSCCPlcmtOptCircumUtil
						.isNonZeroLong(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getIdSSCCPlcmtInfo())) {
					ssccPlcmtOptCircumDao
							.deletePlcmtInfo(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getIdSSCCPlcmtInfo());
					ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().setIdSSCCPlcmtInfo(null);
				}
				ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().setIndDraft(ServiceConstants.Y);
				saveSSCCPlcmtInfo(ssccPlcmtOptCircumDto);
			}
			// read ssccPlcmtOptCircumDto after section has been saved
			// ssccPlcmtOptCircumDto =
			// readSSCCPlcmtOptCirum(ssccPlcmtOptCircumDto);
			// if in SSCC or DFPS Edit mode and placement name was finished
			// editing
			if ((SSCCPlcmtOptCircumUtil.PROPOSE_SSCC_EDIT_STATE == ssccPlcmtOptCircumDto.getCurrentState()
					|| SSCCPlcmtOptCircumUtil.PROPOSE_DFPS_EDIT_STATE == ssccPlcmtOptCircumDto.getCurrentState())
					&& ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtName()) {
				// init med consenter for save and continue
				SSCCPlcmtMedCnsntrDto ssccPlcmtMedCnsntrDto = new SSCCPlcmtMedCnsntrDto();
				SSCCPlcmtMedCnsntrDto newSsccPlcmtMedCnsntrDto = new SSCCPlcmtMedCnsntrDto();
				boolean changeMedCnsntr = true;
				ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setIndSilPrntDfps(indSilPrntDfps);
				ssccPlcmtOptCircumDto = initMedCnsntr(ssccPlcmtOptCircumDto);
				newSsccPlcmtMedCnsntrDto = ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap()
						.get(CodesConstant.CMCTYPE_FPRI);
				// if med consenter view has changed remove old med consenter
				// records for new version
				if (!StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader())) {
					// Fetch the current saved record from DB.
					SsccPlcmtMedCnsntr ssccPlcmtMedCnsntrDB = ssccPlcmtOptCircumDao.getSavedSSCCMedCnsntr(
							ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader(),
							CodesConstant.CMCTYPE_FPRI);
					if (!ObjectUtils.isEmpty(ssccPlcmtMedCnsntrDB)
							&& !StringUtils.isEmpty(ssccPlcmtMedCnsntrDB.getIdSSCCPlcmtMedCnsntr())) {
						if (!ObjectUtils.isEmpty(newSsccPlcmtMedCnsntrDto)) {
							changeMedCnsntr = !newSsccPlcmtMedCnsntrDto.getCdMedCnsntrSelectType()
									.equals(ssccPlcmtMedCnsntrDto.getCdMedCnsntrSelectType());
						} else {
							changeMedCnsntr = false;
						}
					}
					// if med consenter view is the same copy over the original
					// version to new version
					if (!changeMedCnsntr) {
						ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(ServiceConstants.CMCTYPE_FPRI,
								ssccPlcmtMedCnsntrDto);
						ssccPlcmtMedCnsntrDB = ssccPlcmtOptCircumDao.getSavedSSCCMedCnsntr(
								ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader(),
								CodesConstant.CMCTYPE_SPRI);
						if (!ObjectUtils.isEmpty(ssccPlcmtMedCnsntrDB)
								&& !StringUtils.isEmpty(ssccPlcmtMedCnsntrDB.getIdSSCCPlcmtMedCnsntr())) {
							ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(ServiceConstants.CMCTYPE_SPRI,
									ssccPlcmtMedCnsntrDto);
						}
						ssccPlcmtMedCnsntrDB = ssccPlcmtOptCircumDao.getSavedSSCCMedCnsntr(
								ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader(),
								CodesConstant.CMCTYPE_FBUP);
						if (!ObjectUtils.isEmpty(ssccPlcmtMedCnsntrDB)
								&& !StringUtils.isEmpty(ssccPlcmtMedCnsntrDB.getIdSSCCPlcmtMedCnsntr())) {
							ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(ServiceConstants.CMCTYPE_FBUP,
									ssccPlcmtMedCnsntrDto);
						}
						ssccPlcmtMedCnsntrDB = ssccPlcmtOptCircumDao.getSavedSSCCMedCnsntr(
								ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader(),
								CodesConstant.CMCTYPE_SBUP);
						if (!ObjectUtils.isEmpty(ssccPlcmtMedCnsntrDB)
								&& !StringUtils.isEmpty(ssccPlcmtMedCnsntrDB.getIdSSCCPlcmtMedCnsntr())) {
							ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(ServiceConstants.CMCTYPE_SBUP,
									ssccPlcmtMedCnsntrDto);
						}
						ssccPlcmtOptCircumDto = setMedCnsntrDefaults(ssccPlcmtOptCircumDto);
					}
				}
				ssccPlcmtOptCircumDto.setChangeMedCnsntr(changeMedCnsntr);
			}
			// Set the Initial MedConcenter if the Bean doesn't have the values.
			// i.e. it is not saved in DB. This will be required after State
			// Caculation
			if (ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().size() == 0) {
				ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setIndSilPrntDfps(indSilPrntDfps);
				ssccPlcmtOptCircumDto = initMedCnsntr(ssccPlcmtOptCircumDto);
			}
			// Set the excpCareBudgetDaysExceed attribute, which is required for
			// Editing Placement Info Section.
			if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdSSCCPlcmtName())) {
				ssccPlcmtOptCircumDto.setExcpCareBgtDysExcdWthPlcmtDt(ssccPlcmtOptCircumDao
						.excpCareBudgetDaysExceeded(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto(), Boolean.TRUE));
				ssccPlcmtOptCircumDto.setExcpCareBgtDysExcdWthOutPlcmtDt(ssccPlcmtOptCircumDao
						.excpCareBudgetDaysExceeded(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto(), Boolean.FALSE));
			}

			// Call Initialize RsrcPers before returning.
			ssccPlcmtOptCircumDto = readInitRsrcPers(ssccPlcmtOptCircumDto);
		}
		log.info("saveAndContinue method saveAndContinue in SSCCPlcmtOptCircumServiceImpl : Return Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: saveOnValidate Method Description: Service Method for saving
	 * on Validate and Changing the revision No. if applicable.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto saveOnValidate(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto, boolean checkErrorWrn) {
		log.info("saveOnValidate method in SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// If Fixer is reverting status back from placed to saved Content.
		if (SSCCPlcmtOptCircumUtil.PLACED_DFPSFIXER_STATE == ssccPlcmtOptCircumDto.getCurrentState()
				|| SSCCPlcmtOptCircumUtil.PLACED_FIXER_STATE == ssccPlcmtOptCircumDto.getCurrentState()) {
			// If Ind doc Complete is not checked than status is being reverted.
			if (!ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIndDocComp())) {
				// Set the status back to Saved Content
				ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(ServiceConstants.CSSCCSTA_30);
				// Save teh timeline record for status change.
				ssccTimeLineDao
						.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
								ServiceConstants.TIMELINE_FIELD_UPDATE_STATUS
										.replace(ServiceConstants.OLD_STATUS,
												lookupDao.simpleDecodeSafe(CodesConstant.CSSCCSTA,
														CodesConstant.CSSCCSTA_110))
										.replace(ServiceConstants.NEW_STATUS, lookupDao
												.simpleDecodeSafe(CodesConstant.CSSCCSTA, CodesConstant.CSSCCSTA_30))));
				// Save the Status to SSCC List.
				saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_STATUS, ssccPlcmtOptCircumDto);
			}
		}
		// Initiate the Flag to allow save to Placement Information Page.
		ssccPlcmtOptCircumDto.setAllowSaveToPlcmtInfo(true);
		// Save the header Record.
		saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
		// Check if Med Concenter is changed.
		if (ssccPlcmtOptCircumDao.checkForRevChange(ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap())) {
			ssccPlcmtOptCircumDto.setAllowSaveToPlcmtInfo(false);
			// Create the New Version for the Placement.
			ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setIdSSCCPlcmtName(null);
			// Clear the entity ids if available, so that new entry is created
			// for latest version.
			if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getEntityDto())) {
				ssccPlcmtOptCircumDto.getEntityDto().setIdEntity(null);
			}
			if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getEntityAddressDto())) {
				ssccPlcmtOptCircumDto.getEntityAddressDto().setIdEntityAddress(null);
			}
			if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getEntityPhoneDto())) {
				ssccPlcmtOptCircumDto.getEntityPhoneDto().setIdEntityPhone(null);
			}
			saveSSCCPlcmtName(ssccPlcmtOptCircumDto);
			// Loop through the Medconcenter Map and set the id as null to
			// create the new record with updated version.
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().entrySet().stream().forEach(entry -> {
				entry.getValue().setIdSSCCPlcmtMedCnsntr(null);
			});
			saveSSCCPlcmtMedCnsntrs(ssccPlcmtOptCircumDto);
			// Create Placment Info Revision.
			ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().setIdSSCCPlcmtInfo(null);
			saveSSCCPlcmtInfo(ssccPlcmtOptCircumDto);
			// Create the Placed Record Revision.
			ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().setIdSSCCPlcmtPlaced(null);
			saveSSCCPlcmtPlaced(ssccPlcmtOptCircumDto);
			// Create the Narrative revision.
			saveSSCCPlcmtNarr(ssccPlcmtOptCircumDto);
		} else {
			saveSSCCPlcmtMedCnsntrs(ssccPlcmtOptCircumDto);
			saveSSCCPlcmtInfo(ssccPlcmtOptCircumDto);
		}
		// If the Status is not Saved Content after save on validate. Set the
		// Error Message.
		if (checkErrorWrn
				&& !CodesConstant.CSSCCSTA_30.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus())) {
			ssccPlcmtOptCircumUtil.setErrorWarningGroup(ssccPlcmtOptCircumDto);
		}
		ssccPlcmtOptCircumDto = readInitRsrcPers(ssccPlcmtOptCircumDto);
		log.info("saveOnValidate method in SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: initMedCnsntr Method Description: Method initialize the
	 * Various Medical Concenter Views.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return ssccPlcmtOptCircumDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto initMedCnsntr(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("initMedCnsntr method in SSCCPlcmtOptCircumServiceImpl : Execution Started");
		if (DateUtils.getAge(ssccPlcmtOptCircumDto.getDtPersonBirth(),
				ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart()) >= ServiceConstants.MAX_CLD_AGE) {
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(ServiceConstants.CMCTYPE_FPRI,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_60, CodesConstant.CMCTYPE_FPRI));
		} else if (CodesConstant.CSTGTYPE_CPB.equals(ssccPlcmtOptCircumDto.getCdStageType())
				|| (!StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIndSilPrntDfps())
				&& ServiceConstants.Y
				.equals(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIndSilPrntDfps()))) {
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(ServiceConstants.CMCTYPE_FPRI,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_70, CodesConstant.CMCTYPE_FPRI));
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(ServiceConstants.CMCTYPE_FBUP,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_70, CodesConstant.CMCTYPE_FBUP));
		} else if (SSCCPlcmtOptCircumUtil.GRO_EMER_CARE_SVCS
				.contains(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdPlcmtLivArr())) {
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(ServiceConstants.CMCTYPE_FPRI,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_80, CodesConstant.CMCTYPE_FPRI));
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_FBUP,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_80, CodesConstant.CMCTYPE_FBUP));

		} else if (SSCCPlcmtOptCircumUtil.CPA_FOSTER_OR_PRE_CONSUM_PRNTS
				.contains(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdPlcmtLivArr())) {
			// initialized med cnsntrs from Network Resource Tables
			SSCCPlcmtMedCnsntrDto ssccPrMedCnsntr = new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_90,
					CodesConstant.CMCTYPE_FPRI);
			SSCCPlcmtMedCnsntrDto ssccScPrMedCnsntr = new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_90,
					CodesConstant.CMCTYPE_SPRI);
			// get Agency Home medical consenters

			HashMap<String, Long> agncyHmMedCnsntrs = ssccPlcmtOptCircumDao.getAgencyHmMedCnsntr(ssccPlcmtOptCircumDto);
			if (agncyHmMedCnsntrs.size() > 0) {
				if (agncyHmMedCnsntrs.containsKey(CodesConstant.CMCTYPE_FPRI))
					ssccPrMedCnsntr.setIdMedConsenterPerson(agncyHmMedCnsntrs.get(CodesConstant.CMCTYPE_FPRI));
				if (agncyHmMedCnsntrs.containsKey(CodesConstant.CMCTYPE_SPRI))
					ssccScPrMedCnsntr.setIdMedConsenterPerson(agncyHmMedCnsntrs.get(CodesConstant.CMCTYPE_SPRI));
			}
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_FPRI, ssccPrMedCnsntr);
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_SPRI, ssccScPrMedCnsntr);
			// Changed Medcnsntr type from 20 - 90
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_FBUP,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_90, CodesConstant.CMCTYPE_FBUP));
		} else if (SSCCPlcmtOptCircumUtil.GRO_CHILD_CARE
				.contains(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdPlcmtLivArr())
				&& ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIndGroCottage())) {
			// Changed Medcnsntr type from 30 - 100
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_FPRI,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_100, CodesConstant.CMCTYPE_FPRI));

			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_FBUP,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_100, CodesConstant.CMCTYPE_FBUP));

			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_SPRI,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_100, CodesConstant.CMCTYPE_SPRI));

			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_SBUP,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_100, CodesConstant.CMCTYPE_SBUP));
		} else if (SSCCPlcmtOptCircumUtil.GRO_TRT_SRVCS_INTL_DIS
				.contains(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdPlcmtLivArr())) {
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_FPRI,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_40, CodesConstant.CMCTYPE_FPRI));
			// changed from SPRI - FBUP
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_FBUP,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_40, CodesConstant.CMCTYPE_FBUP));
		} else if (SSCCPlcmtOptCircumUtil.GRO_TRT_SRVCS_EMOT_DIS
				.contains(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdPlcmtLivArr())
				|| (SSCCPlcmtOptCircumUtil.GRO_CHILD_CARE
				.contains(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdPlcmtLivArr())
				&& !ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIndGroCottage()))
				|| (!StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIndSilPrntDfps())
				&& !ServiceConstants.Y
				.equals(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIndSilPrntDfps()))) {
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_FPRI,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_50, CodesConstant.CMCTYPE_FPRI));
			// changed from SPRI - FBUP
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().put(CodesConstant.CMCTYPE_FBUP,
					new SSCCPlcmtMedCnsntrDto(CodesConstant.CSSCCMDC_50, CodesConstant.CMCTYPE_FBUP));
		}
		ssccPlcmtOptCircumDto = setMedCnsntrDefaults(ssccPlcmtOptCircumDto);
		log.info("initMedCnsntr method in SSCCPlcmtOptCircumServiceImpl : Return Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: setResource Method Description: Method sets the
	 * SSCCPlcmtOptCircumDto with the selected Resource Values.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @param selectedIdResource
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto setResource(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto, Long selectedIdResource) {
		log.info("setResource method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		ResourceDto resource = resourceDao.getResourceDtl(selectedIdResource);
		ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setCdPlcmtLivArr(ServiceConstants.EMPTY_STRING);
		ssccPlcmtOptCircumDto = getResourceDtl(ssccPlcmtOptCircumDto, resource, true);
		// fetch the Resource Primary Phone.
		ResourcePhoneDto phoneDetail = SSCCPlcmtOptCircumUtil.findResourcePrimaryPhone(resource);
		if (!ObjectUtils.isEmpty(phoneDetail) && !ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getEntityPhoneDto())) {
			ssccPlcmtOptCircumDto.getEntityPhoneDto().setNbrPhone(Long.valueOf(phoneDetail.getNbrRsrcPhone()));
			if (!StringUtils.isEmpty(phoneDetail.getNbrRsrcPhoneExt())) {
				ssccPlcmtOptCircumDto.getEntityPhoneDto()
						.setNbrPhoneExtension(Integer.valueOf(phoneDetail.getNbrRsrcPhoneExt()));
			}
		}
		// If the facility type is 68 then default the livivng arrangement to
		// basic child care.
		if (CodesConstant.CFACTYP2_68.equals(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdRsrcFacilType())) {
			ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setCdPlcmtLivArr(CodesConstant.CLARES_68);
		}
		log.info("setResource method SSCCPlcmtOptCircumServiceImpl : Returing Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: setPerson Method Description: Method sets the values related
	 * to selected Person in the SSCCPlcmtOptCircum Dto.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @param selectedPerson
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto setPerson(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto, PersonListDto selectedPerson) {
		log.info("setPerson method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().setIdPlcmtPerson(selectedPerson.getIdPerson());
		ssccPlcmtOptCircumDto.getEntityDto().setNmEntity(selectedPerson.getPersonFull());
		AddrPhoneDto addrPhoneDto = addrPhoneRtrvSvc.callAddrPhoneRtrvService(selectedPerson.getIdPerson());
		if (!ObjectUtils.isEmpty(addrPhoneDto)) {
			// change the edit on whether person address exists to allow
			// selection of person without and address
			// if placement type is unauthorized.
			ssccPlcmtOptCircumDto.getEntityAddressDto().setAddrStLn1(addrPhoneDto.getAddrPersAddrStLn1());
			ssccPlcmtOptCircumDto.getEntityAddressDto().setAddrStLn2(addrPhoneDto.getAddrPersAddrStLn2());
			ssccPlcmtOptCircumDto.getEntityAddressDto().setAddrCity(addrPhoneDto.getAddrCity());
			ssccPlcmtOptCircumDto.getEntityAddressDto().setCdState(addrPhoneDto.getCdAddrState());
			ssccPlcmtOptCircumDto.getEntityAddressDto().setCdCounty(addrPhoneDto.getCdAddrCounty());
			ssccPlcmtOptCircumDto.getEntityAddressDto().setAddrZip(addrPhoneDto.getAddrZip());
			if (!StringUtils.isEmpty(addrPhoneDto.getNbrPhone())) {
				// Set the Person Phone if available.
				ssccPlcmtOptCircumDto.getEntityPhoneDto().setNbrPhone(Long.valueOf(addrPhoneDto.getNbrPhone()));
				if (!StringUtils.isEmpty(addrPhoneDto.getNbrPhoneExtension())) {
					ssccPlcmtOptCircumDto.getEntityPhoneDto()
							.setNbrPhoneExtension(Integer.valueOf(addrPhoneDto.getNbrPhone()));
				}
			} else {
				// Else find the Primary phone for the selected person and set.
				PersonPhoneRetDto personPhone = personPhoneDao
						.getPersonPrimaryActivePhone(selectedPerson.getIdPerson());
				if (!ObjectUtils.isEmpty(personPhone)) {
					ssccPlcmtOptCircumDto.getEntityPhoneDto().setNbrPhone(Long.valueOf(personPhone.getPersonPhone()));
					if (!StringUtils.isEmpty(personPhone.getPersonPhoneExtension())) {
						ssccPlcmtOptCircumDto.getEntityPhoneDto()
								.setNbrPhoneExtension(Integer.valueOf(personPhone.getPersonPhoneExtension()));
					}
				}
			}
		}
		log.info("setPerson method SSCCPlcmtOptCircumServiceImpl : Returing Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: saveToSSCCList Method Description: The method saves to sscc
	 * list for each specified column for a referral that effects placment
	 * Option and Circumstances.
	 *
	 * @param property
	 * @param ssccPlcmtOptCircumDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private void saveToSSCCList(String property, SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("saveToSSCCList method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		SSCCListDto ssccListDto = ssccListDao
				.fetchSSCCList(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCReferral()).get(0);
		if (ssccListDto.getIdSSCCReferral().equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCReferral())) {
			ssccListDto.setIdLastUpdatePerson(ssccPlcmtOptCircumDto.getIdUser());
			// Check the property changed and update accordingly.
			switch (property) {
				case SSCCPlcmtOptCircumUtil.CD_PLCMT_CIRC_STATUS:
					ssccListDto.setCdPlcmtCircStatus(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus());
					ssccListDto.setDtPlcmtCircStatus(new Date());
					break;
				case SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_STATUS:
					ssccListDto.setCdPlcmtOptionStatus(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus());
					ssccListDto.setDtPlcmtOptionStatus(new Date());
					break;
				case SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_TYPE:
					ssccListDto.setCdPlcmtOptionType(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdSSCCOptionType());
					break;
				case SSCCPlcmtOptCircumUtil.DT_PLCMT_OPTION_RECORDED:
					if (StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtRecordedSSCC())) {
						ssccListDto.setDtPlcmtOptionRecorded(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtRecorded());
					} else {
						ssccListDto.setDtPlcmtOptionRecorded(
								ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtRecordedSSCC());
					}
					break;
				case SSCCPlcmtOptCircumUtil.DT_PLCMT_CIRC_START:
					ssccListDto.setDtPlcmtCircStart(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart());
					break;
				case SSCCPlcmtOptCircumUtil.DT_PLCMT_CIRC_EXPIRE:
					if (StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtActual())) {
						ssccListDto.setDtPlcmtCircExpire(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtExpectReturn());
					} else {
						ssccListDto.setDtPlcmtCircExpire(null);
					}
					break;
				case SSCCPlcmtOptCircumUtil.ID_PLCMT_RSRC:
					if (SSCCPlcmtOptCircumUtil
							.isNonZeroLong(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcAgency())) {
						ssccListDto.setIdPlcmtRsrc(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcAgency());
					} else if (SSCCPlcmtOptCircumUtil
							.isNonZeroLong(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcFacil())) {
						ssccListDto.setIdPlcmtRsrc(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcFacil());
					}
					break;
				case SSCCPlcmtOptCircumUtil.ID_PLCMT_EVENT:
					ssccListDto.setIdPlcmtEvent(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdPlcmtEvent());
					break;
				case SSCCPlcmtOptCircumUtil.IND_EFC:
					ssccListDto.setIndEfc(ServiceConstants.STRING_IND_Y);
					ssccListDto.setIndEfcActive(ServiceConstants.STRING_IND_Y);
					break;
				case SSCCPlcmtOptCircumUtil.IND_PLCMT_SSCC:
					ssccListDto.setIndPlcmtSscc(ServiceConstants.STRING_IND_Y);
					break;
				default:
					break;
			}
			ssccListDao.updateSSCCList(ssccListDto);
		}
		log.info("saveToSSCCList method SSCCPlcmtOptCircumServiceImpl : Execution Ended");
	}

	/**
	 *
	 * Method Name: cancelAndRestart Method Description: This method deletes all
	 * the child tables when sscc header is deleted.
	 *
	 * @param ssccPlcmtOptCircumDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto cancelAndRestart(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("cancelAndRestart method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// When current state is new state delete all the placement related
		// records which are saved.
		SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDtoRes = new SSCCPlcmtOptCircumDto();
		// deletes all the medical consenters
		if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap())
				&& !ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().isEmpty()) {
			/*
			 * List<Long> idMdclConsenters =
			 * ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().values().
			 * stream() .map(o ->
			 * o.getIdSSCCPlcmtMedCnsntr()).collect(Collectors.toList());
			 */
			List<Long> idMdclConsenters = ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().values().stream()
					.map(o -> o.getIdSSCCPlcmtMedCnsntr()).collect(Collectors.toList()).stream()
					.filter(o -> !StringUtils.isEmpty(o)).collect(Collectors.toList());
			ssccPlcmtOptCircumDao.deletePlcmtMedCnsntrs(idMdclConsenters);
		}
		// deletes sscc placement circumstance
		if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto()) && !ObjectUtils
				.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().getIdSSCCPlcmtCircumstance())) {
			ssccPlcmtOptCircumDao.deletePlcmtCircumstance(
					ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().getIdSSCCPlcmtCircumstance());
		}

		// deletes sscc placement name
		if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto())
				&& !ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdSSCCPlcmtName())) {
			ssccPlcmtOptCircumDao.deletePlcmtName(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdSSCCPlcmtName());
		}

		// deletes sscc placement info
		if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto())
				&& !ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getIdSSCCPlcmtInfo())) {
			ssccPlcmtOptCircumDao.deletePlcmtInfo(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getIdSSCCPlcmtInfo());
		}

		// deletes sscc placement placed
		if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto())
				&& !ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getIdSSCCPlcmtPlaced())) {
			ssccPlcmtOptCircumDao
					.deletePlcmtPlaced(ssccPlcmtOptCircumDto.getSsccPlcmtPlacedDto().getIdSSCCPlcmtPlaced());
		}
		// deletes sscc placement narrative
		if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNarrDto())
				&& !ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNarrDto().getIdSSCCPlcmtNarr())) {
			ssccPlcmtOptCircumDao.deletePlcmtNarr(ssccPlcmtOptCircumDto.getSsccPlcmtNarrDto().getIdSSCCPlcmtNarr());
		}

		// deletes sscc placement header
		if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto())
				&& !ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader())
				&& SSCCPlcmtOptCircumUtil.VERSION_CHECK_STATE != ssccPlcmtOptCircumDto.getCurrentState()) {
			ssccPlcmtOptCircumDao
					.deletePlcmtHeader(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader());
		}
		if (SSCCPlcmtOptCircumUtil.NEW_STATE != ssccPlcmtOptCircumDto.getCurrentState()) {
			// save status to SSCC List record
			ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(null);
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_STATUS, ssccPlcmtOptCircumDto);
			ssccPlcmtOptCircumDtoRes.setIdActiveRef(ssccPlcmtOptCircumDto.getIdActiveRef());
		} else {
			ssccPlcmtOptCircumDtoRes.setIdStage(ssccPlcmtOptCircumDto.getIdStage());
			ssccPlcmtOptCircumDtoRes.setIdUser(ssccPlcmtOptCircumDto.getIdUser());
			ssccPlcmtOptCircumDtoRes.setSsccPlcmtStateDto(new SSCCPlcmtStateDto());
			setUpSSCCPlcmtOptCircum(ssccPlcmtOptCircumDtoRes);
		}
		log.info("cancelAndRestart method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDtoRes;
	}

	/**
	 * Method Name: approveWithOutSave Method Description: The service handles
	 * the request when approve without save to placement Info button is clicked
	 * on SSCCPlcmtOptCircum Page.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return ssccPlcmtOptCircumDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto approveWithOutSave(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("approveWithOutSave method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// if the previous status is reject insert timeline with status change
		// record
		if (CodesConstant.CSSCCSTA_100.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus())) {
			ssccTimeLineDao
					.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
							ServiceConstants.TIMELINE_FIELD_UPDATE_STATUS
									.replace(ServiceConstants.OLD_STATUS,
											lookupDao.simpleDecodeSafe(CodesConstant.CSSCCSTA,
													CodesConstant.CSSCCSTA_100))
									.replace(ServiceConstants.NEW_STATUS, lookupDao
											.simpleDecodeSafe(CodesConstant.CSSCCSTA, CodesConstant.CSSCCSTA_150))));

		} else {// INsert timeline with approval message.
			ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
					ServiceConstants.TIMELINE_APPROVED_WITHOUT_SAVE_OPTION));
		}
		// Set the status to approved without save.
		ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(CodesConstant.CSSCCSTA_150);
		// Sae the record to sscc list records.
		saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_STATUS, ssccPlcmtOptCircumDto);
		// Call Save On Validate Service and return the bean.
		ssccPlcmtOptCircumDto = saveOnValidate(ssccPlcmtOptCircumDto, false);
		log.info("approveWithOutSave method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: reject Method Description: Reject Service for
	 * SSCCPlacementOptionCircum Page.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return ssccPlcmtOptCircumDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto reject(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("reject method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// For Placement Option
		if (ssccPlcmtOptCircumDto.getIsOption()) {
			// if previous status is Approve without save write timeline record.
			if (CodesConstant.CSSCCSTA_150.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus())) {
				ssccTimeLineDao
						.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
								ServiceConstants.TIMELINE_FIELD_UPDATE_STATUS
										.replace(ServiceConstants.OLD_STATUS,
												lookupDao.simpleDecodeSafe(CodesConstant.CSSCCSTA,
														CodesConstant.CSSCCSTA_150))
										.replace(ServiceConstants.NEW_STATUS, lookupDao.simpleDecodeSafe(
												CodesConstant.CSSCCSTA, CodesConstant.CSSCCSTA_100))));
			} else { // write regular reject timeline record
				ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil
						.populateSSCCTimelineValue(ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_REJECT_OPTION));
			}
			// Set satus to reject.
			ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(CodesConstant.CSSCCSTA_100);
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_STATUS, ssccPlcmtOptCircumDto);
			// Call save on Validate
			ssccPlcmtOptCircumDto = saveOnValidate(ssccPlcmtOptCircumDto, false);
		} else {
			saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
		}
		ssccPlcmtOptCircumDto = readInitRsrcPers(ssccPlcmtOptCircumDto);
		log.info("reject method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: saveToPlaceInfo Method Description: Implementation of
	 * service to Crate the Placement record when Approve With Save to Placment
	 * Info is Clicked on SSCC Plcmt Opt Circum Page.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto saveToPlaceInfo(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("saveToPlaceInfo method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// Call Save on Validate without checking for Error Warning, as Some of
		// the changes could have been
		// done by user on the page after validate.
		ssccPlcmtOptCircumDto = saveOnValidate(ssccPlcmtOptCircumDto, false);
		if (ssccPlcmtOptCircumDto.getAllowSaveToPlcmtInfo()) {
			// if previous status is reject or approve without save write
			// timeline records for status change
			if (CodesConstant.CSSCCSTA_100.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus())) {
				ssccTimeLineDao
						.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
								ServiceConstants.TIMELINE_FIELD_UPDATE_STATUS
										.replace(ServiceConstants.OLD_STATUS,
												lookupDao.simpleDecodeSafe(CodesConstant.CSSCCSTA,
														CodesConstant.CSSCCSTA_100))
										.replace(ServiceConstants.NEW_STATUS, lookupDao.simpleDecodeSafe(
												CodesConstant.CSSCCSTA, CodesConstant.CSSCCSTA_120))));

			} else if (CodesConstant.CSSCCSTA_150.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus())) {
				ssccTimeLineDao
						.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
								ServiceConstants.TIMELINE_FIELD_UPDATE_STATUS
										.replace(ServiceConstants.OLD_STATUS,
												lookupDao.simpleDecodeSafe(CodesConstant.CSSCCSTA,
														CodesConstant.CSSCCSTA_150))
										.replace(ServiceConstants.NEW_STATUS, lookupDao.simpleDecodeSafe(
												CodesConstant.CSSCCSTA, CodesConstant.CSSCCSTA_120))));
			}
			// Set Status to Saved.
			ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(CodesConstant.CSSCCSTA_120);
			saveSSCCPlcmtName(ssccPlcmtOptCircumDto);
			saveToSSCCList(ServiceConstants.CD_PLCMT_OPTION_STATUS, ssccPlcmtOptCircumDto);
			// Create the Placement Event.
			Long idPlcmtEvent = insertPlacementEvent(ssccPlcmtOptCircumDto);
			if (SSCCPlcmtOptCircumUtil.isNonZeroLong(idPlcmtEvent)) {
				ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setIdPlcmtEvent(idPlcmtEvent);
				// Insert the Record to Placement.
				placementDao.savePlacement(SSCCPlcmtOptCircumUtil.populatePlacementInfoDto(ssccPlcmtOptCircumDto));
				// Add Entry to Medicaid_update table.
				medicaidUpdateDao
						.addMedicaidUpdate(SSCCPlcmtOptCircumUtil.populateMedicaidForAdd(ssccPlcmtOptCircumDto));
				// If Indicator ExcepCare Flag is set to Yes Save record.
				if (ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getIndExceptCare())) {
					exceptionalCare
							.saveExceptionalCare(SSCCPlcmtOptCircumUtil.populateExcepCareDto(ssccPlcmtOptCircumDto));
					saveToSSCCList(SSCCPlcmtOptCircumUtil.IND_EFC, ssccPlcmtOptCircumDto);
				}
				// End Date the Current Med Consenter and create new record,
				// also save the same in SSCC Plcmt Med Cnsntr.
				insertEndMedConsenters(ssccPlcmtOptCircumDto);

				// Insert Narrative Records.
				if (SSCCPlcmtOptCircumUtil
						.isNonZeroLong(ssccPlcmtOptCircumDto.getSsccPlcmtNarrDto().getIdSSCCPlcmtNarr())) {
					ssccPlcmtOptCircumDao.savePlcmtIssueNarr(ssccPlcmtOptCircumDto.getSsccPlcmtNarrDto(),
							ssccPlcmtOptCircumDto.getIdCase(), idPlcmtEvent);
				}

				// Save to timeline that record was crated.
				ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil
						.populateSSCCTimelineValue(ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_SAVED_OPTION
								+ ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdPlcmtEvent().toString() + "</a>"));
				// Save Placement Header
				saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
				// Save to SSCC List
				saveToSSCCList(ServiceConstants.ID_PLCMT_EVENT, ssccPlcmtOptCircumDto);
				saveToSSCCList(ServiceConstants.ID_PLCMT_RSRC, ssccPlcmtOptCircumDto);
				saveToSSCCList(ServiceConstants.IND_PLCMT_SSCC, ssccPlcmtOptCircumDto);
				// Update SSCC Referral Record with indPlcmtData.
				ssccRefDao.updateSSCCRefIndPlcmtData(ssccPlcmtOptCircumDto.getIdActiveRef());
			}
		}
		log.info("saveToPlaceInfo method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: insertPlacementEvent Method Description: Method sets the
	 * values and call the Post Event SErvice to Create the Placement Event.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return idPlacement
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private Long insertPlacementEvent(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("insertPlacementEvent method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		PostEventIPDto plcmtEvntDto = new PostEventIPDto();
		List<PostEventDto> eventPersonList = new ArrayList<>();
		String livArrDecode = null;
		StringBuffer eventDescr = new StringBuffer();
		// Set the Event Description.
		eventDescr.append(
				"Act Start " + DateUtils.stringDt(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart()));
		eventDescr.append("               ");
		eventDescr.append(!StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getNmRsrcFacil())
				? StringHelper.truncate(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getNmRsrcFacil(), 14)
				: ServiceConstants.EMPTY_STRING + " ");
		if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEnablePlcmtOptSILView()) {
			livArrDecode = lookupDao.simpleDecodeSafe(ServiceConstants.CLASIL,
					ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdPlcmtLivArr());
		} else {
			livArrDecode = lookupDao.simpleDecodeSafe(ServiceConstants.CFACTYP2,
					ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdPlcmtLivArr());
		}
		eventDescr.append(!StringUtils.isEmpty(livArrDecode) ? StringHelper.truncate(livArrDecode, 21)
				: ServiceConstants.EMPTY_STRING);
		plcmtEvntDto.setEventDescr(eventDescr.toString());
		plcmtEvntDto.setCdTask(ServiceConstants.FCE_PLACEMENT_TASK);
		plcmtEvntDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
		plcmtEvntDto.setCdEventType(ServiceConstants.CEVNTTYP_PLA);
		plcmtEvntDto.setIdCase(ssccPlcmtOptCircumDto.getIdCase());
		plcmtEvntDto.setIdStage(ssccPlcmtOptCircumDto.getIdStage());
		plcmtEvntDto.setIdPerson(ssccPlcmtOptCircumDto.getIdUser());
		plcmtEvntDto.setDtEventOccurred(new Date());
		// Create Event Person Link Record. With primary Child
		PostEventDto eventPerson = new PostEventDto();
		eventPerson.setIdPerson(ssccPlcmtOptCircumDto.getIdPrimaryChild());
		eventPerson.setCdScrDataAction(ServiceConstants.ADD);
		eventPersonList.add(eventPerson);
		plcmtEvntDto.setPostEventDto(eventPersonList);
		ServiceReqHeaderDto reqHeader = new ServiceReqHeaderDto();
		reqHeader.setReqFuncCd(ServiceConstants.ADD);
		// Call the Post Event Service to Save the Event.
		PostEventOPDto savedPlcmtEvnt = postEventService.checkPostEventStatus(plcmtEvntDto, reqHeader);
		log.info("insertPlacementEvent method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return savedPlcmtEvnt.getIdEvent();
	}

	/**
	 * Method Name: insertEndMedConsenters Method Description: Method Ends
	 * Exisiting Med Consenters and Add new ones for SSCC Placement
	 *
	 * @param ssccPlcmtOptCircumDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private void insertEndMedConsenters(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("insertEndMedConsenters method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		HashMap<String, MedicalConsenterDto> impactMedConsenters = ssccPlcmtOptCircumDao
				.getActiveMedCnsntr(ssccPlcmtOptCircumDto.getIdActiveRef());
		// Change the Ind Court Auth if applicable.
		final String indCourtAuth = (!ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEnableCourtAuth()
				|| ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIndCourtAuth()))
				? ServiceConstants.Y : ServiceConstants.N;
		ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().entrySet().stream().forEach(entry -> {
			if (SSCCPlcmtOptCircumUtil.isNonZeroLong(entry.getValue().getIdMedConsenterPerson())
					&& !ServiceConstants.Y.equals(entry.getValue().getIndBypass())) {
				StagePersonValueDto stagePersonDto = stageDao.selectStagePersonLink(
						entry.getValue().getIdMedConsenterPerson(), ssccPlcmtOptCircumDto.getIdStage());
				if (ServiceConstants.ZERO.equals(stagePersonDto.getIdPerson())
						&& !(CodesConstant.CSSCCMDC_40.equals(entry.getValue().getCdMedCnsntrSelectType())
						|| CodesConstant.CSSCCMDC_50.equals(entry.getValue().getCdMedCnsntrSelectType()))) {
					// Create the Stage Person Link Record.
					StagePersonLinkDto insStagePersonLink = new StagePersonLinkDto();
					insStagePersonLink.setIdStage(ssccPlcmtOptCircumDto.getIdStage());
					insStagePersonLink.setIdPerson(entry.getValue().getIdMedConsenterPerson());
					insStagePersonLink.setIdCase(ssccPlcmtOptCircumDto.getIdCase());
					insStagePersonLink.setCdStagePersRole(CodesConstant.CROLES_NO);
					insStagePersonLink.setIndStagePersInLaw(ServiceConstants.N);
					insStagePersonLink.setCdStagePersType(CodesConstant.CPRSNTYP_PRN);
					insStagePersonLink.setCdStagePersSearchInd(CodesConstant.CSRCHSTA_R);
					insStagePersonLink.setStagePersNotes("Inserted Medical Consenter for SSCC Option");
					insStagePersonLink.setDtStagePersLink(new Date());
					// Set the RelInt values, for Resource caretaker record mark
					// it as SG else for other views mark it as FP.
					if (CodesConstant.CSSCCMDC_20.equals(entry.getValue().getCdMedCnsntrSelectType())
							|| CodesConstant.CSSCCMDC_90.equals(entry.getValue().getCdMedCnsntrSelectType())) {
						if (CodesConstant.CMCTYPE_FPRI.equals(entry.getKey())
								|| CodesConstant.CMCTYPE_SPRI.equals(entry.getKey())) {
							insStagePersonLink.setCdStagePersRelInt(CodesConstant.CRELPRN2_FP);
						}
						if (CodesConstant.CMCTYPE_FBUP.equals(entry.getKey())
								|| CodesConstant.CMCTYPE_SBUP.equals(entry.getKey())) {
							insStagePersonLink.setCdStagePersRelInt(CodesConstant.CRELPRN2_SG);
						}
					} else if (Stream
							.of(CodesConstant.CSSCCMDC_10, CodesConstant.CSSCCMDC_30, CodesConstant.CSSCCMDC_80,
									CodesConstant.CSSCCMDC_100)
							.anyMatch(entry.getValue().getCdMedCnsntrSelectType()::equals)) {
						insStagePersonLink.setCdStagePersRelInt(CodesConstant.CRELPRN2_SG);
					}
					insStagePersonLink.setIndStagePersReporter(ServiceConstants.N);
					insStagePersonLink.setIndStagePersPrSecAsgn(ServiceConstants.N);
					insStagePersonLink.setIndNmStage(Boolean.FALSE);
					insStagePersonLink.setIndCaringAdult(ServiceConstants.N);
					insStagePersonLink.setIndNytdContact(ServiceConstants.CHAR_IND_N);
					insStagePersonLink.setIndNytdContactPrimary(ServiceConstants.CHAR_IND_N);
					ServiceReqHeaderDto reqHeader = new ServiceReqHeaderDto();
					reqHeader.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
					// Save the Stage Person Link Record
					stagePersonLinkDao.getStagePersonLinkAUD(insStagePersonLink, reqHeader);
					// if person Status is null or inactive, set it to Active.
					ssccPlcmtOptCircumDao.updatePersonStatusOnSSCCPlcmt(entry.getValue().getIdMedConsenterPerson());
					// if there is no entry for CAS in Person Category insert
					// record.
					List<PersonCategoryDto> personCatgList = personDao
							.getPersonCategoryList(entry.getValue().getIdMedConsenterPerson());
					boolean cas = false;
					if (!ObjectUtils.isEmpty(personCatgList)) {
						cas = personCatgList.stream()
								.anyMatch(percat -> CodesConstant.CPSNDTCT_CAS.equals(percat.getCdPersonCategory()));
					}
					if (!cas) {
						PersonCategory personCategory = new PersonCategory();
						personCategory.setIdPerson(entry.getValue().getIdMedConsenterPerson());
						personCategory.setCdPersonCategory(CodesConstant.CPSNDTCT_CAS);
						personCategory.setDtLastUpdate(new Date());
						personCategoryDao.savePersonCategory(personCategory);
					}
				}
				MedicalConsenterDto medicalConsenterDto = impactMedConsenters.get(entry.getKey());
				if (!ObjectUtils.isEmpty(medicalConsenterDto)) {
					medicalConsenterDto
							.setDtMedConsEnd(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart());
					medicalConcenterDao.updateEndDateConsenterRecord(medicalConsenterDto);
				}
				// Create the relevant Event.
				PostEventIPDto eventDto = new PostEventIPDto();
				eventDto.setEventDescr("Medical Consenter Completion Event");
				eventDto.setCdTask(ServiceConstants.TASK_9234);
				eventDto.setCdEventStatus(CodesConstant.CEVTSTAT_COMP);
				eventDto.setCdEventType(CodesConstant.CEVNTTYP_MCD);
				eventDto.setIdCase(ssccPlcmtOptCircumDto.getIdCase());
				eventDto.setIdStage(ssccPlcmtOptCircumDto.getIdStage());
				eventDto.setIdPerson(ssccPlcmtOptCircumDto.getIdUser());
				// Create Event Person Link Record. With primary Child
				List<PostEventDto> eventPersonList = new ArrayList<>();
				PostEventDto eventPerson = new PostEventDto();
				eventPerson.setIdPerson(ssccPlcmtOptCircumDto.getIdPrimaryChild());
				eventPerson.setCdScrDataAction(ServiceConstants.ADD);
				eventPersonList.add(eventPerson);
				eventDto.setPostEventDto(eventPersonList);
				ServiceReqHeaderDto reqHeader = new ServiceReqHeaderDto();
				reqHeader.setReqFuncCd(ServiceConstants.ADD);
				// Create the New Med Consenter Event.
				PostEventOPDto medCnsntrEvent = postEventService.checkPostEventStatus(eventDto, reqHeader);
				if (SSCCPlcmtOptCircumUtil.isNonZeroLong(medCnsntrEvent.getIdEvent())) {
					// Set the values to create Med Consenter Record.
					medicalConsenterDto = new MedicalConsenterDto();
					medicalConsenterDto.setIdEvent(medCnsntrEvent.getIdEvent());
					medicalConsenterDto.setIdPerson(ssccPlcmtOptCircumDto.getIdPrimaryChild());
					medicalConsenterDto.setIdCase(ssccPlcmtOptCircumDto.getIdCase());
					medicalConsenterDto.setIdMedConsenterPerson(entry.getValue().getIdMedConsenterPerson());
					medicalConsenterDto.setCdMedConsenterType(entry.getKey());
					entry.setValue(SSCCPlcmtOptCircumUtil.evaluateCourtAuthDFPSDesig(entry.getValue(), indCourtAuth));
					medicalConsenterDto.setCdCourtAuth(entry.getValue().getCdCourtAuth());
					medicalConsenterDto.setCdDfpsDesig(entry.getValue().getCdDfpsDesig());
					medicalConsenterDto
							.setDtMedConsStart(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart());
					medicalConsenterDto.setIndCourtAuth(indCourtAuth);
					// add new med consenter
					medicalConcenterDao.addMedicalConsenterDetail(medicalConsenterDto);
					// update event id to sscc plcmt med consenter Entry to
					// Update in the table.
					entry.getValue().setIdEvent(medCnsntrEvent.getIdEvent());
				}
			}
		});
		// Update the SSCCPlcmt Med Cnsntr Details With the Event Id.
		saveSSCCPlcmtMedCnsntrs(ssccPlcmtOptCircumDto);
		log.info("insertEndMedConsenters method SSCCPlcmtOptCircumServiceImpl : Returning Response");
	}

	/**
	 * Method Name: rescind Method Description: Service Handles the Rescind
	 * Request on SSCC Placement Option and Circumstance Page.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto rescind(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("rescind method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// Set the Header status to Rescind and save the Header.
		ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(CodesConstant.CSSCCSTA_70);
		saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
		// Save the Timeline record for rescind.
		if (ssccPlcmtOptCircumDto.getIsOption()) {
			ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
					ServiceConstants.TIMELINE_RESCIND_OPTION));
			saveSSCCPlcmtName(ssccPlcmtOptCircumDto);
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_STATUS, ssccPlcmtOptCircumDto);
		} else {
			ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
					ServiceConstants.TIMELINE_RESCIND_CIRCUMSTANCE));
			saveSSCCPlcmtCircumstance(ssccPlcmtOptCircumDto);
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_CIRC_STATUS, ssccPlcmtOptCircumDto);
		}
		log.info("rescind method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: saveAndTransmit Method Description: Service to create the
	 * Save and Transmit on SSCC Plcmt Opt Circum Page, and move the status to
	 * PROPOSE.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto saveAndTransmit(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("saveAndTransmit method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// Set the Header status to PROPOSE and save the Header.
		ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(CodesConstant.CSSCCSTA_60);
		saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
		// If PlcmtCircumstance is being editted
		if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtCircumstance()) {
			// set the Ind Dtraft to N
			ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().setIndDraft(ServiceConstants.N);
			// save the Circumstance Record.
			saveSSCCPlcmtCircumstance(ssccPlcmtOptCircumDto);
			// Save the timeline record.
			ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
					ServiceConstants.TIMELINE_ADD_CIRCUMSTANCE));
			// Set the Status to SSCC List.
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_CIRC_STATUS, ssccPlcmtOptCircumDto);
			// If Ind 14 day placement is checked update dt_plcmt_circ_start in
			// SSCC List table for referral.
			if (ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getInd14dayPlcmt())) {
				saveToSSCCList(SSCCPlcmtOptCircumUtil.DT_PLCMT_CIRC_START, ssccPlcmtOptCircumDto);
			}
		}
		// If Placement Info was being edited.
		if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtInfo()) {
			// set plcmt Name ind draft to N and save record
			ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setIndDraft(ServiceConstants.N);
			saveSSCCPlcmtName(ssccPlcmtOptCircumDto);
			// save med consenters.
			saveSSCCPlcmtMedCnsntrs(ssccPlcmtOptCircumDto);
			// set Plcmt Info Ind draft to false and save record.
			ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().setIndDraft(ServiceConstants.N);
			saveSSCCPlcmtInfo(ssccPlcmtOptCircumDto);
			// Save the timeline record.
			ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
					ServiceConstants.TIMELINE_ADD_OPTION));
			// Save to sscc List fields for referral.
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_TYPE, ssccPlcmtOptCircumDto);
			saveToSSCCList(SSCCPlcmtOptCircumUtil.DT_PLCMT_OPTION_RECORDED, ssccPlcmtOptCircumDto);
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_STATUS, ssccPlcmtOptCircumDto);
		}
		log.info("saveAndTransmit method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: updateAndNotify Method Description: Method handle the
	 * SErvice request for Update and Notfy button on SSCC Plcmt Opt Circum
	 * Page.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return ssccPlcmtOptCircumDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto updateAndNotify(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("updateAndNotify method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// Save the Placement Header.
		saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
		// if Placement Circum was being edited.
		if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtCircumstance()) {
			// Delete the temp circumstance version and save circumstance record
			// with new version.
			ssccPlcmtOptCircumDao.deletePlcmtCircumstance(
					ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().getIdSSCCPlcmtCircumstance());
			// Code to Delete Entity Records.
			entityDao.deleteEntityRecords(ssccPlcmtOptCircumDto.getEntityDto(),
					ssccPlcmtOptCircumDto.getEntityAddressDto(), ssccPlcmtOptCircumDto.getEntityPhoneDto());
			ssccPlcmtOptCircumDto.getEntityDto().setIdEntity(null);
			ssccPlcmtOptCircumDto.getEntityAddressDto().setIdEntityAddress(null);
			ssccPlcmtOptCircumDto.getEntityPhoneDto().setIdEntityPhone(null);
			ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().setIndDraft(ServiceConstants.N);
			ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().setIdSSCCPlcmtCircumstance(null);
			ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto()
					.setNbrVersion(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCurrNbrVersion() - 1l);
			saveSSCCPlcmtCircumstance(ssccPlcmtOptCircumDto);
			// Save the timeline info.
			ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
					ServiceConstants.TIMELINE_UPDATE_CIRCUMSTANCE));
		}
		// If Placement Info was edited.
		if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEditPlcmtInfo()) {
			// save Placemtn Name.
			ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setIndDraft(ServiceConstants.N);
			saveSSCCPlcmtName(ssccPlcmtOptCircumDto);
			// Save med Consenter Records.
			saveSSCCPlcmtMedCnsntrs(ssccPlcmtOptCircumDto);
			// delete temp Plcmt Info version and create the same with new
			// version.
			ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().setIndDraft(ServiceConstants.N);
			ssccPlcmtOptCircumDao.deletePlcmtInfo(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getIdSSCCPlcmtInfo());
			saveSSCCPlcmtInfo(ssccPlcmtOptCircumDto);
			// Save the timeline record.
			ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
					ServiceConstants.TIMELINE_UPDATE_OPTION));
		}
		log.info("updateAndNotify method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: setUpSectionToEdit Method Description: Service to setup the
	 * section to Edit. When Edit Request is made from SSCC Plcmt Opt Circum
	 * page.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto setUpSectionToEdit(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("setUpSectionToEdit method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// Save the header record.
		saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
		// Init flags to indicate various informational Messages that are needed
		// during edit.
		ssccPlcmtOptCircumDto.setChangeMedCnsntr(false);
		ssccPlcmtOptCircumDto.setChangeExcpCareForLimit(false);
		ssccPlcmtOptCircumDto.setChangeExcpCareForSil(false);

		// If Option Setup the Edit related to Option.
		if (ssccPlcmtOptCircumDto.getIsOption()) {
			// If the placement name or med consenter or placmt info edit button
			// are clicked insert the
			// placement name as draft and read the SSCCPlcmtOptCircum
			if (SSCCPlcmtOptCircumUtil.EDIT_SECTION_OPT_NAME.equals(ssccPlcmtOptCircumDto.getSectionToEdit())
					|| SSCCPlcmtOptCircumUtil.EDIT_SECTION_OPT_MED.equals(ssccPlcmtOptCircumDto.getSectionToEdit())
					|| SSCCPlcmtOptCircumUtil.EDIT_SECTION_PLCMT_INFO
					.equals(ssccPlcmtOptCircumDto.getSectionToEdit())) {
				ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setIndDraft(ServiceConstants.Y);
				ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setIdSSCCPlcmtName(null);
				saveSSCCPlcmtName(ssccPlcmtOptCircumDto);
			}
			// If medconsenter or plcmt info edit buttons are clicked insert
			// Medcnsntr records as draft.
			if (SSCCPlcmtOptCircumUtil.EDIT_SECTION_OPT_MED.equals(ssccPlcmtOptCircumDto.getSectionToEdit())
					|| SSCCPlcmtOptCircumUtil.EDIT_SECTION_PLCMT_INFO
					.equals(ssccPlcmtOptCircumDto.getSectionToEdit())) {
				if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap())) {
					// Loop through the Medconcenter Map and set the id as null
					// to create the new record with updated version.
					ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().entrySet().stream().forEach(entry -> {
						entry.getValue().setIdSSCCPlcmtMedCnsntr(null);
						entry.getValue().setIndDraft(ServiceConstants.Y);
					});
				}
				setMedCnsntrDefaults(ssccPlcmtOptCircumDto);
				saveSSCCPlcmtMedCnsntrs(ssccPlcmtOptCircumDto);
			}
			// if PlcmtInfo edit button is clicked insert Info record as draft.
			if (SSCCPlcmtOptCircumUtil.EDIT_SECTION_PLCMT_INFO.equals(ssccPlcmtOptCircumDto.getSectionToEdit())) {
				ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().setIndDraft(ServiceConstants.Y);
				ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().setIdSSCCPlcmtInfo(null);
				saveSSCCPlcmtInfo(ssccPlcmtOptCircumDto);
			}
		} else {
			// INcase of Circumstance set up sections to edit.
			if (SSCCPlcmtOptCircumUtil.EDIT_SECTION_CIRCUM.equals(ssccPlcmtOptCircumDto.getSectionToEdit())) {
				ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().setIndDraft(ServiceConstants.Y);
				ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().setIdSSCCPlcmtCircumstance(null);
				// Clear the entity ids if available, so that new entry is
				// created for latest version.
				if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getEntityDto())) {
					ssccPlcmtOptCircumDto.getEntityDto().setIdEntity(null);
				}
				if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getEntityAddressDto())) {
					ssccPlcmtOptCircumDto.getEntityAddressDto().setIdEntityAddress(null);
				}
				if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getEntityPhoneDto())) {
					ssccPlcmtOptCircumDto.getEntityPhoneDto().setIdEntityPhone(null);
				}
				saveSSCCPlcmtCircumstance(ssccPlcmtOptCircumDto);
			}
		}
		// if Edit Plcmt Info set the exeptional care flag for informational
		// messages.
		if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto())
				&& !StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdSSCCPlcmtName())) {
			ssccPlcmtOptCircumDto.setExcpCareBgtDysExcdWthOutPlcmtDt(ssccPlcmtOptCircumDao
					.excpCareBudgetDaysExceeded(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto(), Boolean.FALSE));
		}
		log.info("setUpSectionToEdit method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: cancelUpdate Method Description: Service method handles the
	 * Cancel Update scenario when cancel update request is done after edit in
	 * SSCC Plcmt Opt Circum page.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto cancelUpdate(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("cancelUpdate method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// In case of cancel update request. Delete the current Draft version
		// and
		// Return with Fresh Read.
		if (!StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdSSCCPlcmtName())
				&& ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIndDraft())) {
			ssccPlcmtOptCircumDao.deletePlcmtName(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdSSCCPlcmtName());
		}
		if (!StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getIdSSCCPlcmtInfo())
				&& ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getIndDraft())) {
			ssccPlcmtOptCircumDao.deletePlcmtInfo(ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().getIdSSCCPlcmtInfo());
		}
		// deletes all the medical consenters
		if (!ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap())
				&& !ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().isEmpty()) {
			List<Long> idMdclConsenters = ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().values().stream()
					.map(o -> o.getIdSSCCPlcmtMedCnsntr()).collect(Collectors.toList()).stream()
					.filter(o -> !StringUtils.isEmpty(o)).collect(Collectors.toList());
			ssccPlcmtOptCircumDao.deletePlcmtMedCnsntrs(idMdclConsenters);
		}
		if (!StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().getIdSSCCPlcmtCircumstance())
				&& ServiceConstants.Y.equals(ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().getIndDraft())) {
			ssccPlcmtOptCircumDao.deletePlcmtCircumstance(
					ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().getIdSSCCPlcmtCircumstance());
		}
		log.info("cancelUpdate method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: acknowledgeOnly Method Description: Service Method for
	 * updating the Status to Acknowledge for SSCC Plcmt Opt Circum page when
	 * DFPS User clicks Acknowledge only.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto acknowledgeOnly(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("acknowledgeOnly method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// Set the Header status to Acknowledge.
		ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(CodesConstant.CSSCCSTA_50);
		// save Header.
		saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
		// insert timeline record for option or circumstance , and update the
		// status is SSCC List.
		if (ssccPlcmtOptCircumDto.getIsOption()) {
			ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
					ServiceConstants.TIMELINE_ACK_OPTION));
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_STATUS, ssccPlcmtOptCircumDto);
		} else {
			ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto,
					ServiceConstants.TIMELINE_ACK_CIRCUMSTANCE));
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_CIRC_STATUS, ssccPlcmtOptCircumDto);
		}
		log.info("acknowledgeOnly method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: approveWithMod Method Description: Service method to Save
	 * the Header. On SSCCPlcmtOptCircum Page Only header needs to be saved when
	 * Approve/ApproveWithMod buttons are clicked by DFPS User.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto approveWithMod(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("approveWithMod method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// save Header.
		saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
		log.info("approveWithMod method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: finalizeEval Method Description: Service Method to Update
	 * the status of Placement, and insert the timeline record, SSCC List record
	 * when the Evaluation by DFPS user is finalized.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto finalizeEval(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("finalizeEval method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// if the circumstance is in propose state on click of Finalize eval,
		// record has to be inserted to timeline.
		boolean ackTimeline = CodesConstant.CSSCCSTA_60
				.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus());
		// if The record is being rejected.
		if (CodesConstant.CSSCCEVL_30.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdEvalAction())) {
			// set the rject status on header.
			ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(CodesConstant.CSSCCSTA_100);
			// insert the timeline record.
			if (ssccPlcmtOptCircumDto.getIsOption()) {
				if (ackTimeline) {
					ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil
							.populateSSCCTimelineValue(ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_ACK_OPTION));
				}
				ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil
						.populateSSCCTimelineValue(ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_REJECT_OPTION));
			} else {
				if (ackTimeline) {
					ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
							ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_ACK_CIRCUMSTANCE));
				}
				ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
						ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_REJECT_CIRCUMSTANCE));
			}
		} else { // If the record is being approeed.
			// set the approved status to header.
			ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdStatus(CodesConstant.CSSCCSTA_90);
			// if the record is approved with modification.
			if (CodesConstant.CSSCCEVL_20.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdEvalAction())) {
				if (ssccPlcmtOptCircumDto.getIsOption()) {
					// Save Medical consenter and placement info record.
					saveSSCCPlcmtMedCnsntrs(ssccPlcmtOptCircumDto);
					ssccPlcmtOptCircumDto.getSsccPlcmtInfoDto().setIndDraft(ServiceConstants.N);
					saveSSCCPlcmtInfo(ssccPlcmtOptCircumDto);
					// Save the timeline record.
					if (ackTimeline) {
						ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
								ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_ACK_OPTION));
					}
					ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
							ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_APP_WITH_MOD_OPTION));
				} else {
					if (ackTimeline) {
						ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
								ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_ACK_CIRCUMSTANCE));
					}
					ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
							ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_APP_WITH_MOD_CIRCUMSTANCE));
				}
			} else { // If the record is being approved without any
				// modification.
				if (ssccPlcmtOptCircumDto.getIsOption()) {
					if (ackTimeline) {
						ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
								ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_ACK_OPTION));
					}
					ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil
							.populateSSCCTimelineValue(ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_APP_OPTION));
				} else {
					if (ackTimeline) {
						ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
								ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_ACK_CIRCUMSTANCE));
					}
					ssccTimeLineDao.insertSSCCTimeline(ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(
							ssccPlcmtOptCircumDto, ServiceConstants.TIMELINE_APP_CIRCUMSTANCE));
				}
			}
		}
		// Save placement name or circumstaqnce record and update status in
		// SSCCList record.
		if (ssccPlcmtOptCircumDto.getIsOption()) {
			ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().setIndDraft(ServiceConstants.N);
			saveSSCCPlcmtName(ssccPlcmtOptCircumDto);
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_OPTION_STATUS, ssccPlcmtOptCircumDto);
		} else {
			ssccPlcmtOptCircumDto.getSsccPlcmtCircumstanceDto().setIndDraft(ServiceConstants.N);
			saveSSCCPlcmtCircumstance(ssccPlcmtOptCircumDto);
			saveToSSCCList(SSCCPlcmtOptCircumUtil.CD_PLCMT_CIRC_STATUS, ssccPlcmtOptCircumDto);
		}
		// set Date Evaluation, person who evaluated and save the header record.
		ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setDtEval(new Date());
		ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setIdEvalPerson(ssccPlcmtOptCircumDto.getIdUser());
		// save Header.
		saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
		log.info("finalizeEval method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: cancelEval Method Description: Cancel Eval Service Method
	 * for reverting the Evaluation State of SSCCPlcmtOptCircum Records.
	 *
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCPlcmtOptCircumDto cancelEval(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("cancelEval method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		// reset date recorded dfps and eval action.
		if (SSCCPlcmtOptCircumUtil.APPROVE_WITH_MOD_STATE == ssccPlcmtOptCircumDto.getCurrentState()
				|| SSCCPlcmtOptCircumUtil.EVAL_STATE == ssccPlcmtOptCircumDto.getCurrentState()
				|| SSCCPlcmtOptCircumUtil.PROPOSE_DFPS_EDIT_STATE == ssccPlcmtOptCircumDto.getCurrentState()) {
			if (CodesConstant.CSSCCSTA_60.equals(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdStatus())) {
				ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setDtRecordedDfps(null);
				ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().setCdEvalAction(null);
				saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
			}
		}
		// If the Edit button is clicked during evaluation by DFPS
		// user(specifically or Approve with Modification)
		// Cancel the updates made, if any (Delete the Draft records.)
		ssccPlcmtOptCircumDto = cancelUpdate(ssccPlcmtOptCircumDto);
		log.info("cancelEval method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return ssccPlcmtOptCircumDto;
	}

	/**
	 * Method Name: getDupOptOrCircumStatus Method Description: Service to Check
	 * the Outstanding Status Available for SSCCPlcmt Opt and circum.
	 *
	 * @param idSSCCReferral
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private void getDupOptOrCircumStatus(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("getDupOptOrCircumStatus method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		String status = ssccPlcmtOptCircumDao.getDupOptOrCircumStatus(ssccPlcmtOptCircumDto.getIdActiveRef(),
				ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getCdSSCCPlcmtType());
		ssccPlcmtOptCircumDto.setOutstandingStatus(status);
		log.info("getDupOptOrCircumStatus method SSCCPlcmtOptCircumServiceImpl : Returning Response");
	}

	/**
	 * Method Name: checkNarrativeExist Method Description: Service Method used
	 * to validate if Narrative exist at the time of save.
	 *
	 * @param idSSCCPlcmtHdr
	 * @param nbrVersion
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean checkNarrativeExist(Long idSSCCPlcmtHdr, Long nbrVersion) {
		log.info("checkNarrativeExist method SSCCPlcmtOptCircumServiceImpl : Execution Started");
		SsccPlcmtNarr ssccPlcmtNarr = ssccPlcmtOptCircumDao.getSSCCPlcmtNarrative(idSSCCPlcmtHdr, nbrVersion);
		log.info("checkNarrativeExist method SSCCPlcmtOptCircumServiceImpl : Returning Response");
		return !ObjectUtils.isEmpty(ssccPlcmtNarr) && !StringUtils.isEmpty(ssccPlcmtNarr.getIdSSCCPlcmtNarr());
	}

	/**
	 *Method Name:	validateMedCnsntrView
	 *Method Description: Method Validates the Medical Concenter View before saving the
	 *Placement Name and Location details for selected Resource and Living Arrangement.
	 *@param ssccPlcmtOptCircumDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	private void validateMedCnsntrView(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		// Check if the Living Arrangment can be used to place the child.
		// Required this flag for validation.
		if (ssccPlcmtOptCircumDto.getSsccPlcmtStateDto().getEnablePlcmtOptSILView()
				&& !StringUtils.isEmpty(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdPlcmtLivArr())) {
			if ((DateUtils.getAge(ssccPlcmtOptCircumDto.getDtPersonBirth(),
					ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getDtPlcmtStart()) < ServiceConstants.MAX_CLD_AGE)
					&& ssccPlcmtOptCircumDao.getCrspndingPlcmtSILPair(ssccPlcmtOptCircumDto) <= ServiceConstants.ZERO) {
				ssccPlcmtOptCircumUtil.setErrorMessage(ServiceConstants.MSG_SIL_LIV_ARR_PARENT_CHILD_VALIDATION,
						ssccPlcmtOptCircumDto);
			}
		}
		if (ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getErrorMsgList())) {
			// check if the selected resource as valid Med Concenters to be
			// proposed
			// accordingly set the validation flag.
			ssccPlcmtOptCircumDto.getSsccPlcmtNameDto()
					.setIdRsrcSSCC(ssccPlcmtOptCircumDto.getSsccResourceDto().getIdSSCCResource());
			ssccPlcmtOptCircumDto = initMedCnsntr(ssccPlcmtOptCircumDto);
			SSCCPlcmtMedCnsntrDto ssccFPRIMedCnsntr = ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap()
					.get(CodesConstant.CMCTYPE_FPRI);
			if (!ObjectUtils.isEmpty(ssccFPRIMedCnsntr)) {
				if (Stream.of(CodesConstant.CSSCCMDC_80, CodesConstant.CSSCCMDC_90, CodesConstant.CSSCCMDC_100)
						.anyMatch(ssccFPRIMedCnsntr.getCdMedCnsntrSelectType()::equals)
						&& ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getMedCnsntrOptions())) {
					ssccPlcmtOptCircumUtil.setErrorMessage(ServiceConstants.MSG_SSCC_PLCMT_NO_MED_CNSNTR_LNK_CPA,
							ssccPlcmtOptCircumDto);
				}
				if (CodesConstant.CSSCCMDC_90.equals(ssccFPRIMedCnsntr.getCdMedCnsntrSelectType())
						&& StringUtils.isEmpty(ssccFPRIMedCnsntr.getIdMedConsenterPerson())) {
					ssccPlcmtOptCircumUtil.setErrorMessage(ServiceConstants.MSG_SSCC_PLCMT_NO_MED_CNSNTR_LNK_AGNCY_HM,
							ssccPlcmtOptCircumDto);
				}
				if (CodesConstant.CSSCCMDC_70.equals(ssccFPRIMedCnsntr.getCdMedCnsntrSelectType())
						&& ObjectUtils.isEmpty(ssccPlcmtOptCircumDto.getMedCnsntrOptions())) {
					ssccPlcmtOptCircumUtil.setErrorMessage(ServiceConstants.MSG_SSCC_PLCMT_NO_PRNT_FOR_CPB,
							ssccPlcmtOptCircumDto);
				}
			}
			ssccPlcmtOptCircumDto.getSsccPlcmtMedCnsntrDtoMap().clear();
			ssccPlcmtOptCircumDto.setDupCrGvr(Boolean.FALSE);
		}
	}
}
