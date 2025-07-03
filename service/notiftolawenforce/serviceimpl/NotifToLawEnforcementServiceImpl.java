package us.tx.state.dfps.service.notiftolawenforce.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.notiftolawenforcement.dto.NotifToLawEnforceDto;
import us.tx.state.dfps.notiftolawenforcement.dto.PriorStageDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.IncomingDetailStageDao;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageInDto;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageOutDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.NotifToLawEnforceReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.ContactListSearchDto;
import us.tx.state.dfps.service.contacts.dao.ContactSearchDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.forms.dao.HistoricalPriWorkerDao;
import us.tx.state.dfps.service.forms.dto.HistoricalPriWorkerDto;
import us.tx.state.dfps.service.forms.dto.HistoricalPriWorkerInDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.NotifToLEAgencyAbusePrefillData;
import us.tx.state.dfps.service.forms.util.NotifToLawEnforcePrefillData;
import us.tx.state.dfps.service.investigation.dao.FacilityInvSumDao;
import us.tx.state.dfps.service.investigation.dao.FacilityInvestigationDao;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.notiftolawenforce.service.NotifToLawEnforcementService;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.xmlstructs.inputstructs.FacilityInvestigationDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactDateDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilInvstInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Populates
 * the APS Facility Notice to Law Enforcement form.> Tuxedo Service Name:
 * cinv80s Mar 14, 2018- 5:17:08 PM © 2017 Texas Department of Family and
 * Protective Services
 */

@Service
@Transactional
public class NotifToLawEnforcementServiceImpl implements NotifToLawEnforcementService {

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private NotifToLawEnforcementDao notifToLawEnforcementDao;

	@Autowired
	private IncomingDetailStageDao incomingDetailStageDao;

	@Autowired
	private FacilityInvestigationDao facilityInvestigationDao;

	@Autowired
	private HistoricalPriWorkerDao historicalPriWorkerDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private FacilityInvSumDao facilityInvSumDao;

	@Autowired
	private NotifToLawEnforcePrefillData notifToLawEnforcePrefillData;

	@Autowired
	private NotifToLEAgencyAbusePrefillData notifToLEAgencyAbusePrefillData;
	
	@Autowired
	private ContactSearchDao contactSearchDoa;
	
	@Autowired
	LookupDao lookupDao;

	
	/** The message source. */
	@Autowired
	MessageSource messageSource;
	private static final Logger log = Logger.getLogger(NotifToLawEnforcementServiceImpl.class);

	/**
	 * Service Name: cinv80s Method Description:This class populates the APS
	 * Facility Notice to Law Enforcement form.
	 *
	 * @param notifToLawEnforceReq
	 * @return PreFillDataServiceDto @ the service exception
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getLawEnforcementNoticed(NotifToLawEnforceReq notifToLawEnforceReq) {
		Long idStage = ServiceConstants.ZERO;
		Long idPerson = ServiceConstants.ZERO;
		Long idCase = ServiceConstants.ZERO;
		NotifToLawEnforceDto notifToLawEnforceDto = new NotifToLawEnforceDto();

		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(notifToLawEnforceReq.getIdStage());
		if (!ObjectUtils.isEmpty(genericCaseInfoDto)) {
			idCase = genericCaseInfoDto.getIdCase();
			notifToLawEnforceDto.setGenericCaseInfoDto(genericCaseInfoDto);
		}
		// cses39d
		FacilInvDtlDto facilInvDtlDto = notifToLawEnforcementDao
				.getFacilityInvDtlbyId(notifToLawEnforceReq.getIdStage());
		notifToLawEnforceDto.setFacilInvDtlDto(facilInvDtlDto);

		// cinv86d
		PriorStageDto priorStageDto = notifToLawEnforcementDao.getPriorStagebyId(notifToLawEnforceReq.getIdStage());
		notifToLawEnforceDto.setPriorStageDto(priorStageDto);
		if (!ObjectUtils.isEmpty(priorStageDto)) {
			idStage = priorStageDto.getIdPriorStage();
			// cint07d
			IncomingDetailStageInDto incomingDetailStageInDto = new IncomingDetailStageInDto();
			incomingDetailStageInDto.setIdStage(idStage);
			List<IncomingDetailStageOutDto> IncomingDetailStageOutDtoList = incomingDetailStageDao
					.getIncomingDetail(incomingDetailStageInDto);
			notifToLawEnforceDto.setIncomingDetailStageOutDtoList(IncomingDetailStageOutDtoList.get(0));
		}

		// CINV17D
		FacilityInvestigationDto facilityInvestigationDto = new FacilityInvestigationDto();
		facilityInvestigationDto.setUlIdStage(notifToLawEnforceReq.getIdStage().intValue());
		FacilInvstInfoDto facilInvstInfoDto = facilityInvestigationDao
				.getFacilityInvestigationDetail(facilityInvestigationDto);
		notifToLawEnforceDto.setFacilInvstInfoDto(facilInvstInfoDto);
		

		// ccmn19d
		HistoricalPriWorkerInDto historicalPriWorkerInDto = new HistoricalPriWorkerInDto();
		historicalPriWorkerInDto.setIdStage(notifToLawEnforceReq.getIdStage());
		historicalPriWorkerInDto.setCdStagePersRole(ServiceConstants.PRIMARY_WORKER);
		List<HistoricalPriWorkerDto> historicalPriWorkerDtoList = historicalPriWorkerDao
				.getHistoricalPriWorker(historicalPriWorkerInDto);
		notifToLawEnforceDto.setHistoricalPriWorkerDtoList(historicalPriWorkerDtoList);
		// csec01d
		if (!ObjectUtils.isEmpty(historicalPriWorkerDtoList)) {
			idPerson = historicalPriWorkerDtoList.get(0).getIdTodoPersWorker();
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPerson);
			if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)
					&& !((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
							|| (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType()))) {

				employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getNbrMailCodePhone());
				employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrMailCodePhoneExt());

			}
			notifToLawEnforceDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}

		// new ADS change, select allegation type values
		// Get allegations
		List<FacilityAllegationInfoDto> allegationValue = facilityInvSumDao
				.getAllegationType(notifToLawEnforceReq.getIdStage());
		//Fixed Warranty Defect#12004 Modified list to get the decode value for allegation type
		if (!ObjectUtils.isEmpty(allegationValue)) {
			for (FacilityAllegationInfoDto facilityAllegationInfoDto : allegationValue) {
				facilityAllegationInfoDto.setCdAllegType(
						lookupDao.simpleDecodeSafe(CodesConstant.CAPSALLG, facilityAllegationInfoDto.getCdAllegType()));
			}
		}
		notifToLawEnforceDto.setAllegationValue(allegationValue);

		// Get contacts for populating dates
		ContactListSearchDto contactListSearchDto = new ContactListSearchDto();
		contactListSearchDto.setIdStage(notifToLawEnforceReq.getIdStage());
		List<String> contactTypeList = new ArrayList<String>();
		contactTypeList.add(ServiceConstants.EREG);
		List<String> contactPurposeList = new ArrayList<String>();
		contactPurposeList.add(ServiceConstants.ANOT);
		List<String> contactOtherList = new ArrayList<String>();
		contactOtherList.add(ServiceConstants.CLAW);		
		contactListSearchDto.setCdContactTypeList(contactTypeList);
		contactListSearchDto.setCdContactPurposeList(contactPurposeList);
		contactListSearchDto.setCdContactOthersList(contactOtherList);
		
		List<ContactDto> contactsDto = contactSearchDoa.searchContactList(contactListSearchDto);
		ContactDateDto initialContDate = notifToLawEnforcementDao.getMinContactDate(notifToLawEnforceReq.getIdStage());
		notifToLawEnforceDto.setInitialContDate(initialContDate);
		notifToLawEnforceDto.setContactListDto(contactsDto);

		// CLSCGCD
		List<MultiAddressDto> multiAddressDtoList = notifToLawEnforcementDao
				.getMultiAddress(notifToLawEnforceReq.getIdStage(), idCase);
		if (!ObjectUtils.isEmpty(multiAddressDtoList)
				&& ServiceConstants.AFC_NOTICE_TO_LE.equals(notifToLawEnforceReq.getFormName())) {
			notifToLawEnforceDto.setMultiAddressDtos(multiAddressDtoList);
		}

		notifToLawEnforceDto.setTransactionId(notifToLawEnforceReq.getTransactionId());
		log.info("TransactionId :" + notifToLawEnforceReq.getTransactionId());

		if (ServiceConstants.AFC_NOTICE_TO_LE.equals(notifToLawEnforceReq.getFormName())) {
			return notifToLawEnforcePrefillData.returnPrefillData(notifToLawEnforceDto);

		} else {
			return notifToLEAgencyAbusePrefillData.returnPrefillData(notifToLawEnforceDto);
		}
	}
}
