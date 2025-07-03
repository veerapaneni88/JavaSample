package us.tx.state.dfps.service.interstatecompact.serviceimpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.PersonRaceDetailsDao;
import us.tx.state.dfps.service.admin.dto.FetchEventDto;
import us.tx.state.dfps.service.admin.dto.FetchEventResultDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceInDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceOutDto;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.childplan.dto.ConcurrentGoalDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventFetDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ICcoverLetterPrefillData;
import us.tx.state.dfps.service.forms.util.ICfinancialPlanPrefillData;
import us.tx.state.dfps.service.forms.util.ICplacementStatusPrefillData;
import us.tx.state.dfps.service.forms.util.ICpriorityHomePrefillData;
import us.tx.state.dfps.service.forms.util.ICtransmittalMemoPrefillData;
import us.tx.state.dfps.service.forms.util.IcpReqPrefillData;
import us.tx.state.dfps.service.icpforms.dto.IcpFormsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpPersonDto;
import us.tx.state.dfps.service.icpforms.dto.IcpReqDto;
import us.tx.state.dfps.service.icpforms.dto.IcpRsrcEntityDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcChildDetailsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcInfoDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcTransmittalDto;
import us.tx.state.dfps.service.interstatecompact.dao.ICPReqDao;
import us.tx.state.dfps.service.interstatecompact.dao.ICPformsDao;
import us.tx.state.dfps.service.interstatecompact.service.ICPformsService;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.person.dto.SupervisorDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.EventChildPlanDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.servicauthform.dao.ServiceAuthFormDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:<Implementation class for interface ICPformsService> May 1, 2018-
 * 10:16:59 AM © 2017 Texas Department of Family and Protective Services
 */

@Service
@Transactional
public class ICPformsServiceImpl implements ICPformsService {

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private ServiceAuthFormDao serviceAuthFormDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private ICPformsDao iCPformsDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private PcaDao pcaDao;

	@Autowired
	private CommonApplicationDao commonApplicationDao;

	@Autowired
	private ChildServicePlanFormDao childServicePlanFormDao;

	@Autowired
	private ICcoverLetterPrefillData iCcoverLetterPrefillData;

	@Autowired
	private ICpriorityHomePrefillData iCpriorityHomePrefillData;

	@Autowired
	private ICfinancialPlanPrefillData iCfinancialPlanPrefillData;

	@Autowired
	private ICplacementStatusPrefillData iCplacementStatusPrefillData;

	@Autowired
	private ICtransmittalMemoPrefillData iCtransmittalMemoPrefillData;

	@Autowired
	private PersonRaceDetailsDao personRaceDetailsDao;

	@Autowired
	private EventFetDao eventFetDao;

	@Autowired
	private ICPReqDao iCPReqDao;

	@Autowired
	private IcpReqPrefillData icpReqPrefillData;

	private static final Logger log = Logger.getLogger(ICPformsServiceImpl.class);

	/**
	 * Service Name: CSUB42S Method Description: This service will get icp01o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 * 
	 *         the service exception
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getICPlacementReqForm(PopulateFormReq populateFormReq) {

		IcpReqDto icpReqDto = new IcpReqDto();

		/* retrieves from ICPC_REQUEST CSESG1D */

		IcpRsrcEntityDto icpRsrcEntityDto1 = iCPReqDao.getRequest(populateFormReq.getIdIcpcRequest());
		icpReqDto.setIcpRsrcEntityDto1(icpRsrcEntityDto1);
		/* retrieves CallCSECF1D */

		IcpPersonDto icpPersonDtoF1 = iCPReqDao.getPlacement(populateFormReq.getIdEvent());
		icpReqDto.setIcpPersonDtoF1(icpPersonDtoF1);

		/* retrieves CallCLSCH0D */

		List<IcpPersonDto> icpPersonDto0 = iCPReqDao.getPerson(populateFormReq.getIdEvent());
		icpReqDto.setIcpPersonDto0(icpPersonDto0);

		/* retrieves CallCLSCH1D */

		List<IcpRsrcEntityDto> icpRsrcEntityDtoList = iCPReqDao.getEntity(populateFormReq.getIdEvent());
		if (0 > icpRsrcEntityDtoList.size()) {
			icpRsrcEntityDtoList.get(0).setIndDispPersFin(ServiceConstants.YES);
			icpRsrcEntityDtoList.get(0).setIndDispPersPlcmt(ServiceConstants.YES);
		}
		/*
		 * The business rule indicates that the form should display the Agency
		 * Address in the sections applicable when both an Agency and Person is
		 * responsible for placement/finances of a child. If only agency is
		 * responsible then we display the Agency address and phone and if only
		 * person is responsible then we display the address and phone of the
		 * person. To achieve this through form mapping the indDispPersPlcmt and
		 * indDispPersFin are being set based on the type of agency returned by
		 * dam. For eg:If one of the rows has agency type as '20' then the
		 * person address should not be displayed in the applicable section of
		 * the form. To achieve this indDispPersPlcmt is set to 'N'.
		 */
		for (IcpRsrcEntityDto icpRsrcEntityDto : icpRsrcEntityDtoList) {

			if (icpRsrcEntityDto.getCdType().equals(ServiceConstants.CPL_20)) {
				icpRsrcEntityDtoList.get(0).setIndDispPersPlcmt(ServiceConstants.NO);
			}
			if (icpRsrcEntityDto.getCdType().equals(ServiceConstants.CPL_10)) {
				icpRsrcEntityDtoList.get(0).setIndDispPersFin(ServiceConstants.NO);
			}
		}
		icpReqDto.setIcpRsrcEntityDtoList(icpRsrcEntityDtoList);

		/* retrieves CallCSECF5D */
		IcpPersonDto icpPersonDtoF5 = iCPReqDao.getPersonRace(populateFormReq.getIdEvent());
		icpReqDto.setIcpPersonDtoF5(icpPersonDtoF5);

		/* retrieves CallCLSS79D */

		PersonRaceInDto personRaceInDto = new PersonRaceInDto();
		if (!ObjectUtils.isEmpty(icpPersonDtoF5.getIdPerson())) {
			personRaceInDto.setIdPerson(icpPersonDtoF5.getIdPerson());
			List<PersonRaceOutDto> personRaceOutDtoList = personRaceDetailsDao.getRaceDetails(personRaceInDto);
			icpReqDto.setPersonRaceOutDtoList(personRaceOutDtoList);
		}

		/* retrieves CallCSECF6D */

		IcpRsrcEntityDto icpRsrcEntityDto6 = iCPReqDao.getResource(populateFormReq.getIdEvent());
		icpReqDto.setIcpRsrcEntityDto6(icpRsrcEntityDto6);

		/* retrieves CallCLSSC6D */

		List<String> enclosure = iCPReqDao.getEnclosure(populateFormReq.getIdEvent());
		icpReqDto.setEnclosure(enclosure);

		/* retrieves CallCLSCH5D */

		List<IcpPersonDto> icpPersonDtoH5 = iCPReqDao.getPersonAddressReq(populateFormReq.getIdEvent());

		// Appending Person Names when multiple people selected as Caregivers
		if (!CollectionUtils.isEmpty(icpPersonDtoH5) && icpPersonDtoH5.stream()
				.filter(dto -> ServiceConstants.ICPCPRTP_60.equals(dto.getCdPersonType()))
				.count() > 1) {

			List<IcpPersonDto> icpcPlacementPersons = icpPersonDtoH5.stream()
					.filter(dto -> ServiceConstants.ICPCPRTP_60.equals(dto.getCdPersonType()))
					.sorted(Comparator.comparing(IcpPersonDto::getCdPlacementType)
							.thenComparing(IcpPersonDto::getIdIcpcRequestPersonLink))
					.collect(Collectors.toList());

			StringBuilder personNames = new StringBuilder(icpcPlacementPersons.get(0).getNmPersonFull());

			for (int i = 1; i < icpcPlacementPersons.size(); i++) {
				personNames.append(", ");
				personNames.append(icpcPlacementPersons.get(i).getNmPersonFull());
			}

			IcpPersonDto icpPersonDto = new IcpPersonDto();
			icpPersonDto.setNmPersonFull(personNames.toString());
			icpPersonDto.setCdPersonType(icpcPlacementPersons.get(0).getCdPersonType());
			icpPersonDto.setAddrPersStLn1(icpcPlacementPersons.get(0).getAddrPersStLn1());
			icpPersonDto.setAddrPersCity(icpcPlacementPersons.get(0).getAddrPersCity());
			icpPersonDto.setAddrPersState(icpcPlacementPersons.get(0).getAddrPersState());
			icpPersonDto.setAddrPersZip(icpcPlacementPersons.get(0).getAddrPersZip());
			icpPersonDto.setNbrPersPhone(icpcPlacementPersons.get(0).getNbrPersPhone());

			icpPersonDtoH5.removeIf(dto -> ServiceConstants.ICPCPRTP_60.equals(dto.getCdPersonType()));
			icpPersonDtoH5.add(icpPersonDto);
		}

		icpReqDto.setIcpPersonDtoH5(icpPersonDtoH5);

		/*
		 ** The Relationship field on the Form should display the type and
		 * relationship of the Legal Guardian. The Relationship is being sent as
		 * a parameter to the form service which is being copied into the
		 ** CSECF9DOutput. This is being mapped to the bookmark on the form
		 * template to display the Relationship.
		 **/

		if (!ObjectUtils.isEmpty(icpPersonDtoF5.getIdPerson())) {
			IcpcChildDetailsDto icpcChildDetailsDto = iCPformsDao.getPersonTypeAndRelation(ServiceConstants.CD_MEMBER,
					icpPersonDtoF5.getIdPerson(), populateFormReq.getIdEvent());
			icpReqDto.setIcpcChildDetailsDto(icpcChildDetailsDto);
		}

		/* CallCCMN45D : Fetch stage Id based on Event Id */

		FetchEventDto fetchEventDto = new FetchEventDto();
		fetchEventDto.setIdEvent(populateFormReq.getIdEvent());
		FetchEventResultDto fetchEventResultDto = eventFetDao.fetchEventDetails(fetchEventDto);
		icpReqDto.setFetchEventResultDto(fetchEventResultDto);

		// CallCSECD1D Retrieves the name of the Primary worker for the case
		// given an icpc event
		Long idPerson = serviceAuthFormDao.getPRPersonId(populateFormReq.getIdEvent());
		icpReqDto.setIdPerson(idPerson);

		/* retrieves worker info CallCSEC01D */

		/*
		 ** If there is no primary business phone or cell for a worker, the phone
		 * number for staff from the mail code-office table will not be
		 * overwritten.
		 */

		if (!ObjectUtils.isEmpty(idPerson)) {
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPerson);
			if (!ObjectUtils.isEmpty(employeePersPhNameDto)) {
				if (!ObjectUtils.isEmpty(employeePersPhNameDto.getCdPhoneType())) {

					if (!(employeePersPhNameDto.getCdPhoneType().equals(ServiceConstants.BUSINESS_PHONE)
							|| employeePersPhNameDto.getCdPhoneType().equals(ServiceConstants.BUSINESS_CELL))) {
						employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getMailCodePhone());
						employeePersPhNameDto.setNbrPhoneExtension(employeePersPhNameDto.getMailCodePhoneExt());
					}
				}
			}

			icpReqDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}

		return icpReqPrefillData.returnPrefillData(icpReqDto);
	}

	/**
	 * Service Name: CSUB43S Method Description: This service will get icp02o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 * 
	 *         the service exception
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getICstatusform(PopulateFormReq populateFormReq) {

		// initialize main dto to be used by all methods.
		IcpFormsDto icpFormsDto = new IcpFormsDto();
		Long idPerson = populateFormReq.getIdPerson();
		Long idEvent = populateFormReq.getIdEvent();
		String cdPersonType = ServiceConstants.UNIT_MEMBER_ROLE_20;

		// CSECF2D retrieves from ICPC_REQUEST
		List<IcpcInfoDto> icpcInfoDtoList = iCPformsDao.getICPCinfo(idEvent);
		if (!ObjectUtils.isEmpty(icpcInfoDtoList) && !ObjectUtils.isEmpty(icpcInfoDtoList.get(0))) {
			icpFormsDto.setIcpcInfoDto(icpcInfoDtoList.get(0));
		}

		// CSECF3D retrieves nmResource
		List<IcpcInfoDto> nmResourceList = iCPformsDao.getNmResource(idEvent);
		icpFormsDto.setNmResourceList(nmResourceList);

		// CSECF4D retrieves person information
		List<IcpcChildDetailsDto> personInfoList = iCPformsDao.getPersonInfo(idEvent);
		icpFormsDto.setPersonInfoList(personInfoList);

		if(idPerson != null) {
			// CSECF7D retrieves detailed person info
			IcpcChildDetailsDto detailedPersonInfo = iCPformsDao.getDetailedPersonInfo(idPerson);
			icpFormsDto.setDetailedPersonInfo(detailedPersonInfo);
		}

		// CSECF8D retrieves detailed person info and nmResource
		IcpcChildDetailsDto detailedPersonInfoWithNmResource = iCPformsDao
				.getDetailedPersonInfoWithNmResource(populateFormReq.getIdResource());
		icpFormsDto.setDetailedPersonInfoWithNmResource(detailedPersonInfoWithNmResource);

		// CLSCH2D retrieves detailed person information such as person full
		// name, sex, type and birth
		List<IcpcChildDetailsDto> personNmTypeList = iCPformsDao.getPersonNmType(idEvent);
		icpFormsDto.setPersonNmTypeList(personNmTypeList);

		if(idPerson != null) {
			// CLSCH3D retrieves detailed person information such as person full
			// name, address, id status
			List<IcpcChildDetailsDto> personAddrInfoList = iCPformsDao.getPersonAddrInfo(idPerson);
			icpFormsDto.setPersonAddrInfoList(personAddrInfoList);
		}

		// CLSCH4D retrieves detailed person information such as nm resource,
		// sex, type and birth.
		List<IcpcChildDetailsDto> personResourceList = iCPformsDao.getPersonResource(populateFormReq.getIdResource());
		icpFormsDto.setPersonResourceList(personResourceList);

		if(idPerson != null) {
			// CSECF9D retrieves idperson, type and relation
			IcpcChildDetailsDto personTypeandRelation = iCPformsDao.getPersonTypeAndRelation(cdPersonType, idPerson,
					idEvent);
			if (!ObjectUtils.isEmpty(personTypeandRelation)) {
				icpFormsDto.setCdRelation(personTypeandRelation.getCdRelation());
			}
			icpFormsDto.setPersonTypeandRelation(personTypeandRelation);
		}

		return iCplacementStatusPrefillData.returnPrefillData(icpFormsDto);
	}

	/**
	 * Service Name: CSUB30S Method Description: This service will get icp14o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 * 
	 *         the service exception
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getPriorityHomeStudyReq(PopulateFormReq populateFormReq) {

		// initialize main dto to be used by all methods.
		IcpFormsDto icpFormsDto = new IcpFormsDto();

		// initialize global variables
		Long idPerson = ServiceConstants.ZERO;
		Long ulIdJobPersSupv = ServiceConstants.ZERO;
		Long idIcpcRequest = ServiceConstants.ZERO;

		// CSEC02D retrieves stage and caps_case table
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(populateFormReq.getIdStage());
		icpFormsDto.setGenericCaseInfoDto(genericCaseInfoDto);

		// CallCSECD1D Retrieves the name of the Primary worker for the case
		// given an icpc event
		idPerson = serviceAuthFormDao.getPRPersonId(populateFormReq.getIdEvent());

		// CallCSEC01D Retreives Employee information about the primary
		// caseworker
		if (!ObjectUtils.isEmpty(idPerson)) {
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPerson);
			ulIdJobPersSupv = employeePersPhNameDto.getIdJobPersSupv();
			icpFormsDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}

		// Call to the CSEC0CD dam to get the icpc child
		IcpcChildDetailsDto icpcChildDetailsDto = iCPformsDao.getIcpcChildDetails(populateFormReq.getIdEvent());
		if (!ObjectUtils.isEmpty(icpcChildDetailsDto)) {
			idIcpcRequest = icpcChildDetailsDto.getIdIcpcRequest();
			icpFormsDto.setIcpcChildDetailsDto(icpcChildDetailsDto);
		}

		// Call CSEC1AD dam to get the icpc information
		IcpcChildDetailsDto icpcDetailsDto = iCPformsDao.getIcpcReqAndTitle(populateFormReq.getIdEvent());
		icpFormsDto.setIcpcDetailsDto(icpcDetailsDto);

		// CallCSEC01D get the worker information
		if (!ObjectUtils.isEmpty(ulIdJobPersSupv)) {
			EmployeePersPhNameDto supervisorPersPhNameDto = employeeDao.searchPersonPhoneName(ulIdJobPersSupv);
			icpFormsDto.setSupervisorPersPhNameDto(supervisorPersPhNameDto);
		}

		// Call CSEC1CD Retrieves resource address and name by given icpc
		// request
		if (!ObjectUtils.isEmpty(idIcpcRequest)) {
			IcpcChildDetailsDto icpcNameAndAddr = iCPformsDao.getAddressandName(idIcpcRequest);
			icpFormsDto.setIcpcNameAndAddr(icpcNameAndAddr);

			// Call CLSC25D DAM that retrieves the Names, address, phones for a
			// IdIcpcRequest
			List<IcpcChildDetailsDto> personDetailByIdReqDtoList = iCPformsDao
					.getPersonDetailsByIdReqest(idIcpcRequest);
			if (!ObjectUtils.isEmpty(personDetailByIdReqDtoList)) {

				// initialize boolean values to find father and mother are selected in the parent section
				boolean isMother = ServiceConstants.FALSEVAL;
				boolean isFather = ServiceConstants.FALSEVAL;

				// Default to UNKNOWN parents.
				for (IcpcChildDetailsDto icpcDto : personDetailByIdReqDtoList) {
					if (ServiceConstants.UNIT_MEMBER_ROLE_30.equals(icpcDto.getCdPersonType())) {
						isMother = ServiceConstants.TRUEVAL;
					}
					if (ServiceConstants.CPRMGLTY_10.equals(icpcDto.getCdPersonType())) {
						isFather = ServiceConstants.TRUEVAL;
					}
				}
				//Defect# 12758 - Modifed condition to set the UNKNOWN if the unknown checkebox selected in the parent section
				if (!isMother) {
					IcpcChildDetailsDto dto = new IcpcChildDetailsDto();
					dto.setNmNameLast(ServiceConstants.LT_UNKNOWN);
					dto.setCdPersonType(ServiceConstants.UNIT_MEMBER_ROLE_30);
					personDetailByIdReqDtoList.add(dto);
				}
				if (!isFather) {
					IcpcChildDetailsDto dto = new IcpcChildDetailsDto();
					dto.setNmNameLast(ServiceConstants.LT_UNKNOWN);
					dto.setCdPersonType(ServiceConstants.CPRMGLTY_10);
					personDetailByIdReqDtoList.add(dto);
				}
			}else {
				personDetailByIdReqDtoList = new ArrayList<>();
				IcpcChildDetailsDto unknownMother = new IcpcChildDetailsDto();
				unknownMother.setNmNameLast(ServiceConstants.LT_UNKNOWN);
				unknownMother.setCdPersonType(ServiceConstants.UNIT_MEMBER_ROLE_30);
				IcpcChildDetailsDto unknownFather = new IcpcChildDetailsDto();
				unknownFather.setNmNameLast(ServiceConstants.LT_UNKNOWN);
				unknownFather.setCdPersonType(ServiceConstants.CPRMGLTY_10);
				personDetailByIdReqDtoList.add(unknownMother);
				personDetailByIdReqDtoList.add(unknownFather);
			} 
			// a row is added to personDetailByIdReqDtoList from
			// icpcChildDetailsDto
			if (!ObjectUtils.isEmpty(icpcChildDetailsDto)) {
				personDetailByIdReqDtoList.add(icpcChildDetailsDto);
			}
			icpFormsDto.setPersonDetailByIdReqDtoList(personDetailByIdReqDtoList);
		}

		return iCpriorityHomePrefillData.returnPrefillData(icpFormsDto);
	}

	/**
	 * Service Name: CSUB32S Method Description: This service will get icp18o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 * 
	 *         the service exception
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getICcoverLetter(PopulateFormReq populateFormReq) {

		// initialize main dto to be used by all methods.
		IcpFormsDto icpFormsDto = new IcpFormsDto();

		// initialize global variables
		Long idPerson = ServiceConstants.ZERO;
		Long idIcpcRequest = ServiceConstants.ZERO;
		Long idChildPlanEvent = ServiceConstants.ZERO;
		Long idPrimarychild = ServiceConstants.ZERO;

		// CSEC02D retrieves stage and caps_case table
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(populateFormReq.getIdStage());
		icpFormsDto.setGenericCaseInfoDto(genericCaseInfoDto);

		// CallCSECD1D Retrieves the name of the Primary worker for the case
		// given an icpc event
		idPerson = serviceAuthFormDao.getPRPersonId(populateFormReq.getIdEvent());

		// CallCSEC01D Retreives Employee information about the primary
		// caseworker
		if (!ObjectUtils.isEmpty(idPerson)) {
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPerson);
			if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)
					&& ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
							|| (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType()))) {

				employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getMailCodePhone());
				employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getMailCodePhoneExt());
			}
			icpFormsDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}

		// Call to the CSEC0CD dam to get the icpc child
		IcpcChildDetailsDto icpcChildDetailsDto = iCPformsDao.getIcpcChildDetails(populateFormReq.getIdEvent());
		icpFormsDto.setIcpcChildDetailsDto(icpcChildDetailsDto);
		if (!ObjectUtils.isEmpty(icpcChildDetailsDto)) {
			if (!ObjectUtils.isEmpty(icpcChildDetailsDto.getIdIcpcRequest())) {
				idIcpcRequest = icpcChildDetailsDto.getIdIcpcRequest();
			}
			if (!ObjectUtils.isEmpty(icpcChildDetailsDto.getIdPerson())) {
				idPrimarychild = icpcChildDetailsDto.getIdPerson();
			}
		}

		// Call CSEC1AD dam to get the icpc information
		IcpcChildDetailsDto icpcDetailsDto = iCPformsDao.getIcpcReqAndTitle(populateFormReq.getIdEvent());
		icpFormsDto.setIcpcDetailsDto(icpcDetailsDto);

		// Call CSEC1CD Retrieves resource address and name by given icpc
		// request
		IcpcChildDetailsDto icpcNameAndAddr = iCPformsDao.getAddressandName(idIcpcRequest);
		icpFormsDto.setIcpcNameAndAddr(icpcNameAndAddr);

		// Call CLSC25D DAM that retrieves the Names, address, phones for a
		// IdIcpcRequest
		List<IcpcChildDetailsDto> personDetailByIdReqDtoList = iCPformsDao.getPersonDetailsByIdReqest(idIcpcRequest);
		// a row is added to personDetailByIdReqDtoList from icpcChildDetailsDto
		if (!ObjectUtils.isEmpty(icpcChildDetailsDto)) {
			personDetailByIdReqDtoList.add(icpcChildDetailsDto);
		}
		icpFormsDto.setPersonDetailByIdReqDtoList(personDetailByIdReqDtoList);

		// Call to the CSEC20D dam to get child plan data
		if (!ObjectUtils.isEmpty(idPrimarychild)) {
			EventChildPlanDto eventChildPlanDto = commonApplicationDao.getEventChildPlanDtls(idPrimarychild);
			if (!ObjectUtils.isEmpty(eventChildPlanDto)) {
				icpFormsDto.setEventChildPlanDto(eventChildPlanDto);
				idChildPlanEvent = eventChildPlanDto.getIdChildPlanEvent();
			}
		}

		if (!ObjectUtils.isEmpty(idChildPlanEvent)) {
			// CSESF3D Retrieves concurrent goals of child plan
			List<ConcurrentGoalDto> concurrentDtoList = childServicePlanFormDao.getConcurrentData(idChildPlanEvent);
			if (!ObjectUtils.isEmpty(concurrentDtoList)) {
				icpFormsDto.setFetchFullConcurrentDto(concurrentDtoList.get(0));
			}
		}

		return iCcoverLetterPrefillData.returnPrefillData(icpFormsDto);
	}

	/**
	 * Service Name: CSUB20S Method Description: This service will get icp20o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 * 
	 *         the service exception
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getICfinancialPlan(PopulateFormReq populateFormReq) {

		// initialize main dto to be used by all methods.
		IcpFormsDto icpFormsDto = new IcpFormsDto();

		// initialize global variables
		Long idPerson = ServiceConstants.ZERO;
		Long idCaseWorker = ServiceConstants.ZERO;
		Long idIcpcRequest = ServiceConstants.ZERO;
		Long idStageInput = ServiceConstants.ZERO;
		;
		String nmPersonFull = ServiceConstants.EMPTY_CHAR;

		// CSES18D retrieves stage id that is linked to an event.
		IcpcChildDetailsDto idEventStage = iCPformsDao.getIdEventStage(populateFormReq.getIdEvent());
		idStageInput = idEventStage.getIdEventStage();
		icpFormsDto.setIdEventStage(idEventStage);

		// CCMN19D Retreives information about the primary caseworker
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(idStageInput, ServiceConstants.PRIMARY);
		icpFormsDto.setStagePersonDto(stagePersonDto);

		// CSECD1D Retrieves the name of the Primary worker for the case given
		// an icpc event
		if (!ObjectUtils.isEmpty(populateFormReq.getIdEvent())) {
			idPerson = serviceAuthFormDao.getPRPersonId(populateFormReq.getIdEvent());
			if (!ObjectUtils.isEmpty(idPerson)) {
				stagePersonDto.setIdTodoPersWorker(idPerson);
			}
		}

		// set idCaseWorker for call to CCMN60D
		idCaseWorker = stagePersonDto.getIdTodoPersWorker();

		// CCMN60D - Supervisor information
		SupervisorDto supervisorDto = pcaDao.getSupervisorPersonId(idCaseWorker);
		icpFormsDto.setSupervisorDto(supervisorDto);

		// CSEC01D get worker info
		if (!ObjectUtils.isEmpty(idPerson)) {
			IcpcChildDetailsDto icpcNameAndAddr = iCPformsDao.getAddressandName(idPerson);

			/*
			 ** CCMN30D errs when the form is launched from a stage other than
			 * the one in which the icpc request was created, so, with the
			 * idCaseworker from CallCSECD1D, copy the CallCSEC01D names to the
			 * pCCMN30DOutputRec fullname.
			 */
			if (!ObjectUtils.isEmpty(icpcNameAndAddr)) {
				StringBuilder str = new StringBuilder();
				if (!ObjectUtils.isEmpty(icpcNameAndAddr.getNmNameLast())) {
					str.append(icpcNameAndAddr.getNmNameLast());
					str.append(ServiceConstants.COMMA);
				}
				if (!ObjectUtils.isEmpty(icpcNameAndAddr.getNmNameFirst())) {
					str.append(icpcNameAndAddr.getNmNameFirst());
				}
				nmPersonFull = str.toString();
			}
			icpFormsDto.setNmPersonFull(nmPersonFull);
			icpFormsDto.setIcpcNameAndAddr(icpcNameAndAddr);
		}

		// CSEC1AD Retrieves Ind Title Ive for an ICPC Placement Request given
		// an id event
		IcpcChildDetailsDto icpcDetailsDto = iCPformsDao.getIcpcReqAndTitle(populateFormReq.getIdEvent());
		icpFormsDto.setIcpcDetailsDto(icpcDetailsDto);
		if (!ObjectUtils.isEmpty(icpcDetailsDto)
				&& !ObjectUtils.isEmpty(icpcDetailsDto.getIdIcpcRequest())) {
			/* set ulIdIcpcRequest for call to CCMN60D */
			idIcpcRequest = icpcDetailsDto.getIdIcpcRequest();
		}

		// Call CSEC15D call to retrieve date field
		if (!ObjectUtils.isEmpty(idStageInput)) {
			StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao.getStagePersonCaseDtl(idStageInput,
					ServiceConstants.PRIMARY_CHILD);
			icpFormsDto.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);
		}

		// CSEC1CD retrieves resource address and name given icpc request
		IcpcChildDetailsDto icpcNameAndAddr = iCPformsDao.getAddressandName(idIcpcRequest);
		icpFormsDto.setIcpcNameAndAddr(icpcNameAndAddr);

		// CLSC25D retrieves the name and address of placement resource
		List<IcpcChildDetailsDto> personDetailByIdReqDtoList = iCPformsDao.getPersonDetailsByIdReqest(idIcpcRequest);
		icpFormsDto.setPersonDetailByIdReqDtoList(personDetailByIdReqDtoList);

		return iCfinancialPlanPrefillData.returnPrefillData(icpFormsDto);
	}

	/**
	 * Service Name: CSUB33S Method Description: This service will get icp22o00
	 * form populated by receiving populateFormReq from controller,
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 * 
	 *         the service exception
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getICtransmittalMemo(PopulateFormReq populateFormReq) {

		// initialize main dto to be used by all methods.
		IcpFormsDto icpFormsDto = new IcpFormsDto();

		// initialize global variables
		Long idEvent100A = ServiceConstants.ZERO;
		Long idEvent100B = ServiceConstants.ZERO;
		Long idRequest100A = ServiceConstants.ZERO;
		Long idStageInput = ServiceConstants.ZERO;
		Long idTransmittal = ServiceConstants.ZERO;
		String receivingStDecode = ServiceConstants.EMPTY_CHAR;
		String sendingStDecode = ServiceConstants.EMPTY_CHAR;
		String inputStateDecode = ServiceConstants.EMPTY_CHAR;
		// CSES18D retrieves stage id that is linked to an event.
		if (!ObjectUtils.isEmpty(populateFormReq.getIdEvent())
				&& !ServiceConstants.ZERO.equals(populateFormReq.getIdEvent())) {
			IcpcChildDetailsDto idEventStage = iCPformsDao.getIdEventStage(populateFormReq.getIdEvent());
			idStageInput = idEventStage.getIdEventStage();
			icpFormsDto.setIdEventStage(idEventStage);
		}

		// Call CSEC15D call to retrieve date field
		if (!ObjectUtils.isEmpty(idStageInput)) {
			StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao.getStagePersonCaseDtl(idStageInput,
					ServiceConstants.PRIMARY_CHILD);
			icpFormsDto.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);
		}

		// CSEC1BD retrieves transmittal information
		if (!ObjectUtils.isEmpty(populateFormReq.getIdTransmittal())) {
			IcpcTransmittalDto icpcTransmittalDto = iCPformsDao.getTransmittalInfo(populateFormReq.getIdTransmittal());

			if (!ObjectUtils.isEmpty(icpcTransmittalDto)) {
				if (ServiceConstants.PLCMT_ACTUAL_TYPE.equalsIgnoreCase(icpcTransmittalDto.getCdRequestType())) {
					idEvent100A = icpcTransmittalDto.getIdEvent();
					idRequest100A = icpcTransmittalDto.getIdIcpcRequest();

					// CSEC1AD retrieve icpc information
					IcpcChildDetailsDto icpcDetailsDto = iCPformsDao.getIcpcReqAndTitle(idEvent100A);
					icpFormsDto.setIcpcDetailsDto(icpcDetailsDto);
				} else {
					idEvent100B = icpcTransmittalDto.getIdEvent();
					icpcTransmittalDto.getIdIcpcRequest();

					// CSEC1DD return Title IVE indicator from placement
					// request.
					List<IcpcChildDetailsDto> titleIVE = iCPformsDao.getTitleIVEind(idEvent100B);
					if (!ObjectUtils.isEmpty(titleIVE)) {
						idEvent100A = titleIVE.get(0).getIdEvent();
						idRequest100A = titleIVE.get(0).getIdIcpcRequest();
						icpFormsDto.setTitleIVE(titleIVE.get(0));
					}
				}

				idTransmittal = icpcTransmittalDto.getIdIcpcTransmittal();
				sendingStDecode = icpcTransmittalDto.getC2Decode();
				receivingStDecode = icpcTransmittalDto.getC1Decode();
				icpcTransmittalDto.getCdRequestType();
			}
			icpFormsDto.setIcpcTransmittalDto(icpcTransmittalDto);
		}

		// CLSS2AD retrieves the indicator transmittal
		List<IcpcTransmittalDto> icpcTransmittalDtoList = iCPformsDao.getTransmittalIndicator(idTransmittal);
		icpFormsDto.setIcpcTransmittalDtoList(icpcTransmittalDtoList);

		if (!ObjectUtils.isEmpty(idRequest100A) && !ServiceConstants.ZERO.equals(idRequest100A)) {
			// Call CSEC1CD Retrieves resource address and name by given icpc
			// request
			IcpcChildDetailsDto icpcNameAndAddr = iCPformsDao.getAddressandName(idRequest100A);
			icpFormsDto.setIcpcNameAndAddr(icpcNameAndAddr);

			// Call CLSC25D DAM that retrieves the Names, address, phones for a
			// IdIcpcRequest
			List<IcpcChildDetailsDto> personDetailByIdReqDtoList = iCPformsDao
					.getPersonDetailsByIdReqest(idRequest100A);
			icpFormsDto.setPersonDetailByIdReqDtoList(personDetailByIdReqDtoList);

		}

		/*
		 ** Since all of our Transmittals will be going to another state, Texas
		 * should always be in the From field, and the other state should be in
		 * the To field. We'll need to check and see which field
		 ** (CD_RECEIVING_STATE or CD_SENDING_STATE) doesn't equal "Texas" and
		 * use that data to populate the To field
		 **
		 */
		if (ServiceConstants.TEXAS.equalsIgnoreCase(sendingStDecode)) {
			inputStateDecode = receivingStDecode;
		} else {
			inputStateDecode = sendingStDecode;
		}

		/*
		 ** If Input State is either CA,OH, or CO, we want to call CLSCH1D to
		 * retrieve the county information
		 */

		if (ServiceConstants.CALIFORNIA.equalsIgnoreCase(inputStateDecode)
				|| ServiceConstants.OHIO.equalsIgnoreCase(inputStateDecode)
				|| ServiceConstants.COLORADO.equalsIgnoreCase(inputStateDecode)
						&& !ServiceConstants.ZERO.equals(idEvent100A)) {
			// clsch1d Retrieves State ICPC addresses given 100A event
			List<IcpcChildDetailsDto> countyAddressDtoList = iCPformsDao.getCountyAddress(idEvent100A);
			if (!ObjectUtils.isEmpty(countyAddressDtoList)) {
				countyAddressDtoList.get(0).setIndDispPersPlcmt(ServiceConstants.STRING_IND_Y);
				countyAddressDtoList.get(0).setIndDispPersFin(ServiceConstants.STRING_IND_Y);

				/*
				 ** The business rule indicates that the form should display the
				 * Agency Address in the sections applicable when both an Agency
				 * and Person is responsible for placement/finances of a child.
				 * If only agency is responsible then we display the Agency
				 * address and phone and if only person is responsible then we
				 * display the address and phone of the person. To achieve this
				 * through form mapping the indDispPersPlcmt and indDispPersFin
				 * are being set based on the type of agency returned by dam.
				 * For eg:If one of the rows has agency type as '20' then the
				 * person address should not be displayed in the applicable
				 * section of the form. To achieve this indDispPersPlcmt is set
				 * to 'N'.
				 */
				for (IcpcChildDetailsDto dto : countyAddressDtoList) {
					if (ServiceConstants.CCH_20.equals(dto.getCdType())) {
						countyAddressDtoList.get(0).setIndDispPersPlcmt(ServiceConstants.STRING_IND_N);
					}
					if (ServiceConstants.CPL_10.equals(dto.getCdType())) {
						countyAddressDtoList.get(0).setIndDispPersFin(ServiceConstants.STRING_IND_N);
					}
				}

				icpFormsDto.setCountyAddressDto(countyAddressDtoList.get(0));
			} else {
				countyAddressDtoList = new ArrayList<>();
				countyAddressDtoList.add(new IcpcChildDetailsDto());
				countyAddressDtoList.get(0).setIndDispPersPlcmt(ServiceConstants.STRING_IND_Y);
				countyAddressDtoList.get(0).setIndDispPersFin(ServiceConstants.STRING_IND_Y);
				icpFormsDto.setCountyAddressDto(countyAddressDtoList.get(0));
			}
		} else {

			// CSEC1ED retrieves address information and phone number of an
			// agency
			List<IcpcChildDetailsDto> addressForAgencyList = iCPformsDao
					.getCountyAddressForAgency(ServiceConstants.CPL_30, inputStateDecode);
			if (!ObjectUtils.isEmpty(addressForAgencyList)) {
				icpFormsDto.setAddressForAgency(addressForAgencyList.get(0));
			}
		}

		// CLSS2BD Retrieve List of Children from Transmittal Page
		List<IcpcChildDetailsDto> childrenNameList = iCPformsDao.getChildrenFullName(idTransmittal);
		icpFormsDto.setChildrenNameList(childrenNameList);

		// CSEC1FD retrieves Other Case Nbr given icpc transmittal
		List<IcpcTransmittalDto> caseNumberList = iCPformsDao.getNumberOtherCase(idTransmittal);
		icpFormsDto.setCaseNumberList(caseNumberList);

		return iCtransmittalMemoPrefillData.returnPrefillData(icpFormsDto);
	}

}
