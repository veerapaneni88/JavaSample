package us.tx.state.dfps.service.populateform.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dao.CpsInvstDetailStageIdDao;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.NamePrimayEndDateDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkRecordDao;
import us.tx.state.dfps.service.admin.dao.StageSituationDao;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdOutDto;
import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateInDto;
import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordOutDto;
import us.tx.state.dfps.service.admin.dto.StageSituationInDto;
import us.tx.state.dfps.service.admin.dto.StageSituationOutDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvAllegDto;
import us.tx.state.dfps.service.cpsinvreport.dao.CpsInvReportDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PopulateFormPrefillData;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.person.dto.PersonEmailDto;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;
import us.tx.state.dfps.service.person.dto.PopFormDto;
import us.tx.state.dfps.service.populateform.dao.LetterToParentDao;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.populateform.service.PopulateFormService;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;

/**
 * Service-business- ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo
 * Service Name: CINV38S Class Description: This class is doing service
 * Implementation for PopulateFormService Jan 15, 2018 - 11:32:34 AM © 2017
 * Texas Department of Family and Protective Services
 * ********Change History**********
 * 01/02/2023 thompswa artf238090 PPM 73576 filter allegation list for letter recipient
 *
 */

@Service
@Transactional
public class PopulateFormServiceImpl implements PopulateFormService {

	@Autowired
	private PopulateFormDao populateFormDao;

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private NamePrimayEndDateDao objCcmn40dDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private StageProgDao stageProgDao;

	@Autowired
	private StageSituationDao objClss30dDao;

	@Autowired
	private PersonAddressDao personAddressDao;

	@Autowired
	private CpsInvstDetailStageIdDao cpsInvstDetailsStageIdDao;

	@Autowired
	private StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	private StagePersonLinkRecordDao objCinv39dDao;

	@Autowired
	PopulateFormPrefillData populateFormPrefillData;
	
	/** The message source. */
	@Autowired
	MessageSource messageSource;

	@Autowired
	CpsInvReportDao cpsInvReportDao;

	@Autowired
	private LetterToParentDao letterToParentDao;

	@Autowired
	LookupDao lookupDao;


	private static final Logger log = Logger.getLogger(PopulateFormServiceImpl.class);

	public PopulateFormServiceImpl() {

	}

	/**
	 * Service Name: CINV38S Method Description: This service will get forms
	 * populated by receiving populateFormReq from controller, then retrieving
	 * data from caps_case, stage, code_type, stage_prog, person, person_email,
	 * etc tables to get the forms populated.
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return popFormDto @ the service exception
	 */

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getFormsPopulated(PopulateFormReq populateFormReq) {
		PopFormDto popFormDto = new PopFormDto();

		Boolean bOpenStage = Boolean.FALSE;
		long ulIdCase = 0;
		long ulIdPerson = 0;

		/*
		 ** retrieves stage and caps_case table CSEC02D return the generic case
		 * info needed for all forms
		 */

		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(populateFormReq.getIdStage());
		ulIdCase = genericCaseInfoDto.getIdCase();
		popFormDto.setRowQty(ServiceConstants.POSITIVE_ONE);
		popFormDto.setGenericCaseInfoDto(genericCaseInfoDto);
		/*
		 ** retrieves letterhead info CLSC03D to retrieve the board members and
		 * the executive director information for the header.
		 */

		CodesTablesDto codesTablesDto = populateLetterDao
				.getPersonInfoByCode(ServiceConstants.TITLE, ServiceConstants.NAME).get(0);
		popFormDto.setCodesTablesDto(codesTablesDto);

		/*
		 * CallCCMN40D(pInputMsg,pOutputMsg); used to retrieve the primary name
		 * for a given id_person;
		 */
		NamePrimayEndDateInDto namePrimayEndDateInDto = new NamePrimayEndDateInDto();
		namePrimayEndDateInDto.setIdPerson(populateFormReq.getIdPerson());
		List<NamePrimayEndDateOutDto> namePrimaryEndDateOutDto = objCcmn40dDao.getFullName(namePrimayEndDateInDto);
		popFormDto.setNamePrimayEndDateOutDto(namePrimaryEndDateOutDto);

		/*
		 ** retrieves letterhead info CCMNB8D to retrieve stage closure
		 * information
		 */

		if (!ObjectUtils.isEmpty(genericCaseInfoDto.getCdStageReasonClosed())) {
			List<StageProgDto> stageProgDtoList = stageProgDao.getStgProgroession(genericCaseInfoDto.getCdStage(),
					genericCaseInfoDto.getCdStageProgram(), genericCaseInfoDto.getCdStageReasonClosed());
			popFormDto.setStageProgDto(stageProgDtoList);
			if (!ObjectUtils.isEmpty(stageProgDtoList)) {
				if ((ServiceConstants.SUBCAREPP).equals(stageProgDtoList.get(0).getCdStageProgRsnClose())
						|| (ServiceConstants.RSUBCARE).equals(stageProgDtoList.get(0).getCdStageProgRsnClose())) {
					StageSituationInDto stageSituationInDto = new StageSituationInDto();
					stageSituationInDto.setIdCase(ulIdCase);
					List<StageSituationOutDto> stageDetails = objClss30dDao.getStageDetails(stageSituationInDto);
					for (StageSituationOutDto stageSituation : stageDetails) {
						if (!(ServiceConstants.CSTAGES_ARI).equals(stageSituation.getCdStage())
								&& null == stageSituation.getDtStageClose()) {
							bOpenStage = Boolean.TRUE;
						}

					}

					popFormDto.setStageDetails(stageDetails);
					if (bOpenStage) {
						stageProgDtoList.get(0).setIndStageProgClose(ServiceConstants.STR_ONE_VAL);
					} else {
						stageProgDtoList.get(0).setIndStageProgClose(ServiceConstants.STR_ZERO_VAL);
					}
					popFormDto.setStageProgDto(stageProgDtoList);
				}
			}
		}

		/*
		 * CCMN96D used to retrieve the addresses for the id_person
		 */

		AddressDtlReq addressDtlReq = new AddressDtlReq();
		addressDtlReq.setUlIdPerson(populateFormReq.getIdPerson());
		List<AddressDto> addressDto = personAddressDao.getAddressList(addressDtlReq);
		if (!TypeConvUtil.isNullOrEmpty(addressDto) && 0 < addressDto.size()) {
			popFormDto.setAddressDto(addressDto.get(0));
		}

		/*
		 * CINV95D used to retrieve the date of the intake call.
		 */
		CpsInvstDetailStageIdInDto cpsInvstDetailStageIdInDto = new CpsInvstDetailStageIdInDto();
		cpsInvstDetailStageIdInDto.setIdStage(populateFormReq.getIdStage());
		List<CpsInvstDetailStageIdOutDto> cpsInvstDetailStageIdOutDto = cpsInvstDetailsStageIdDao
				.getInvstDtls(cpsInvstDetailStageIdInDto);
		if (!TypeConvUtil.isNullOrEmpty(cpsInvstDetailStageIdOutDto) && 0 < cpsInvstDetailStageIdOutDto.size()) {
			popFormDto.setCpsInvstDetailStageIdOutDto(cpsInvstDetailStageIdOutDto.get(0));
		}
		/**
		 * populate Allegation Detail section.
		 * artf238090 getting from cpsInvReportDao for consistency
		 */
		Boolean cfiv3200 = false;
		if (CodesConstant.CCNTCTYP_ANGN.equalsIgnoreCase(populateFormReq.getFormName())
				|| CodesConstant.CONNOTTY_CFIV3200.equalsIgnoreCase(populateFormReq.getFormName())) cfiv3200 = true;
		List<CpsInvAllegDto> getAllegationsList = cpsInvReportDao.getAllegationsSafe(populateFormReq.getIdStage(), cfiv3200);
		List<CpsInvAllegDto> allegationsList = new ArrayList<CpsInvAllegDto>();
		for (CpsInvAllegDto dto : getAllegationsList) {
			if (!ObjectUtils.isEmpty(dto.getIdAllegPrep()) && dto.getIdAllegPrep().equals(populateFormReq.getIdPerson())) {
				allegationsList.add(dto);
				if (!cfiv3200) {
					// convert spanish characters to html
					dto.setDecodeAllegType(TypeConvUtil.formatSpanish(dto.getDecodeAllegType()));
				}
			}

		}
		popFormDto.setAllegationsList(allegationsList);

		/*
		 * CLSSB0D is used to retrieve all of the unique dispositions for a
		 * given ulIdAllegationStage and ulIdAllegedPerpetrator.
		 */
		// Changed the Return Type to List - Warranty Defect 10787
		List<AllegationWithVicDto> recipientDisp = new ArrayList<>();
		List<AllegationWithVicDto> allegationWithUqVicDto = populateFormDao.getUqDispositonById(populateFormReq.getIdStage(),
				populateFormReq.getIdPerson());
		// artf238090 look up the decode
		for (AllegationWithVicDto perpDisp : allegationWithUqVicDto) {
			perpDisp.setAlgCdAllegDisposition(lookupDao.decode(cfiv3200 ? CodesConstant.CCIVALDS : CodesConstant.CCIVADSP,
					perpDisp.getaCdAllegDisposition()));
			if (!cfiv3200) {
				// convert spanish characters to html
				perpDisp.setAlgCdAllegDisposition(TypeConvUtil.formatSpanish(perpDisp.getAlgCdAllegDisposition()));
			}
			recipientDisp.add(perpDisp);
		}
		popFormDto.setAllegationWithVicDto(recipientDisp);

		/*
		 * CLSSAAD is used to retrieve all of the unique allegations for a given
		 * id_stage.
		 */

		List<AllegationDto> allegationDtoList = populateFormDao.getUqAllegationById(populateFormReq.getIdStage(),
				populateFormReq.getIdPerson());
		popFormDto.setAllegationDtoList(allegationDtoList);
		
		if(ObjectUtils.isEmpty(allegationDtoList))
		{
			throw new FormsException(messageSource.getMessage("NoFormData", null, Locale.US));
		}

		/*
		 * CLSCE7D is used to retrieves disposition from ALLEGATION in join with
		 * STAGE PERSON LINK and STAGE for a given ID_PERSON and ID_STAGE.
		 * DISTINCT.
		 */
		
		// Changed the Return Type to List - Warranty Defect 10787
		List<AllegationWithVicDto> allegationWithUqStageDto = populateFormDao.getUqStageById(populateFormReq.getIdStage(),
				populateFormReq.getIdPerson());
		
		
		// Warranty Defect - 12271 - To show the Role Removal for Ruled Out Scenario Only.
		if (!ObjectUtils.isEmpty(allegationWithUqStageDto)) {
		
		List<AllegationWithVicDto> ruledOutAllegation = allegationWithUqStageDto.stream()
				.filter(dto -> !dto.getAlgCdAllegDisposition().equals(ServiceConstants.RULED_OUT))
				.collect(Collectors.toList());				

		if (ObjectUtils.isEmpty(ruledOutAllegation)) {
			popFormDto.setAllegationWithVicUqStageDto(allegationWithUqStageDto.get(0));
		}
		}			

		/*
		 * CCMNB9D is used to retrieves the historically primary worker
		 * associated with the stage. switch(rc)
		 * 
		 */
		List<StagePersonLinkDto> stagePersonLinkDtoList = new ArrayList<StagePersonLinkDto>();
		stagePersonLinkDtoList = stagePersonLinkDao.getStagePersonLinkByIdStage(populateFormReq.getIdStage());
		popFormDto.setStagePersonLinkDtoList(stagePersonLinkDtoList);

		if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDtoList)) {
			for (StagePersonLinkDto stagePerson : stagePersonLinkDtoList) {
				if (stagePerson.getCdStagePersRole().equalsIgnoreCase(ServiceConstants.HISTORICAL_WORKER_ROLE)
						|| stagePerson.getCdStagePersRole().equalsIgnoreCase(ServiceConstants.PRIMARY_WORKER_ROLE)) {
					ulIdPerson = stagePerson.getIdPerson();
					break;

				}
			}

			/*
			 * retrieves worker info CallCSEC01D will return all of the
			 * information needed for a worker. switch(rc)
			 */

			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(ulIdPerson);
			if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)
					&& !((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
							|| (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType()))) {

				employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getNbrMailCodePhone());
				employeePersPhNameDto.setNbrPhoneExtension(employeePersPhNameDto.getNbrMailCodePhoneExt());

			}
			popFormDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}

		/*
		 * CINV39D used to retrieves a row from STAGE_PERSON_LINK to determine
		 * if the person passed to this service is school personnel.
		 */
		StagePersonLinkRecordInDto stagePersonLinkRecordInDto = new StagePersonLinkRecordInDto();
		stagePersonLinkRecordInDto.setIdPerson(populateFormReq.getIdPerson());
		stagePersonLinkRecordInDto.setIdStage(populateFormReq.getIdStage());
		List<StagePersonLinkRecordOutDto> stageRecDto = objCinv39dDao
				.getStagePersonLinkRecord(stagePersonLinkRecordInDto);
		if (!TypeConvUtil.isNullOrEmpty(stageRecDto) && 0 < stageRecDto.size()) {
			popFormDto.setStagePersonLinkRecordOutDto(stageRecDto.get(0));

		}

		/*
		 * CINV81D is used to retrieves gender for spanish translation.
		 */
		PersonGenderSpanishDto personGenderSpanishDto = populateFormDao.isSpanGender(populateFormReq.getIdPerson());
		popFormDto.setPersonGenderSpanishDto(personGenderSpanishDto);
		/*
		 * CSES0BD is used to return email of letter recipient
		 */
		List<PersonEmailDto> personEmailDtoList = populateFormDao.returnEmailById(populateFormReq.getIdPerson());
		if (!TypeConvUtil.isNullOrEmpty(personEmailDtoList) && 0 < personEmailDtoList.size()) {
			popFormDto.setPersonEmailDto(personEmailDtoList.get(0));
		}

		popFormDto.setFormName(populateFormReq.getFormName());
		popFormDto.setTransactionId(populateFormReq.getTransactionId());
		popFormDto.setUlIdPerson(populateFormReq.getIdPerson());
		log.info("TransactionId :" + populateFormReq.getTransactionId());
		return populateFormPrefillData.returnPrefillData(popFormDto);
	}

}
