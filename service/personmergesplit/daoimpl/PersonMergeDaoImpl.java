package us.tx.state.dfps.service.personmergesplit.daoimpl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.arinvconclusion.dto.PCSPDto;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.PostEventPersonDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.admin.dto.NameDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.adoptionasstnc.AdoptionAsstncDto;
import us.tx.state.dfps.service.adoptionasstnc.dao.AdoptionAsstncDao;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casemanagement.dao.CPSInvCnlsnDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dto.CSAEpisodeDto;
import us.tx.state.dfps.service.casepackage.dto.CsaEpisodesIncdntDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.PersonPhoneReq;
import us.tx.state.dfps.service.common.request.PhoneReq;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.csa.dao.CSADao;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.familytree.dao.FTRelationshipSearchDao;
import us.tx.state.dfps.service.fcl.dao.MutualNonAggressiveIncidentDao;
import us.tx.state.dfps.service.fcl.dao.SexualVictimizationHistoryDao;
import us.tx.state.dfps.service.investigation.dao.PcspDao;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.AllegationDao;
import us.tx.state.dfps.service.person.dao.CriminalHistoryDao;
import us.tx.state.dfps.service.person.dao.CvsFaHomeDao;
import us.tx.state.dfps.service.person.dao.EducationDao;
import us.tx.state.dfps.service.person.dao.EventPersonDao;
import us.tx.state.dfps.service.person.dao.FceEligibilityDao;
import us.tx.state.dfps.service.person.dao.FinancialAcctDao;
import us.tx.state.dfps.service.person.dao.IncomeResourceDao;
import us.tx.state.dfps.service.person.dao.LetterAllegationLinkDao;
import us.tx.state.dfps.service.person.dao.NameDao;
import us.tx.state.dfps.service.person.dao.PRTPersonMergeSplitDao;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dao.PersonCharDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonEligibilityDao;
import us.tx.state.dfps.service.person.dao.PersonEmailDao;
import us.tx.state.dfps.service.person.dao.PersonEthnicityDao;
import us.tx.state.dfps.service.person.dao.PersonPhoneDao;
import us.tx.state.dfps.service.person.dao.PersonRaceDao;
import us.tx.state.dfps.service.person.dao.RecordsCheckDao;
import us.tx.state.dfps.service.person.dao.ResourceServiceDao;
import us.tx.state.dfps.service.person.dao.SSCCPersonMergeSplitDao;
import us.tx.state.dfps.service.person.dao.TletsDao;
import us.tx.state.dfps.service.person.dao.TraffickingDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.ChildSafetyPlacementDto;
import us.tx.state.dfps.service.person.dto.EducationHistoryDto;
import us.tx.state.dfps.service.person.dto.EducationalNeedDto;
import us.tx.state.dfps.service.person.dto.IncomeAndResourceDto;
import us.tx.state.dfps.service.person.dto.MergeSplitVldMsgDto;
import us.tx.state.dfps.service.person.dto.PersonEmailValueDto;
import us.tx.state.dfps.service.person.dto.PersonIdentifiersDto;
import us.tx.state.dfps.service.person.dto.PersonMergeSplitDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneOutDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneRetDto;
import us.tx.state.dfps.service.person.dto.RecordsCheckDeterminationDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.personmergesplit.dao.PersonMergeDao;
import us.tx.state.dfps.service.personmergesplit.dao.PersonMergeSplitDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;
import us.tx.state.dfps.service.workload.service.WorkloadService;
import us.tx.state.dfps.web.person.bean.CvsFaHomeValueBean;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonValueBean;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonValueBean.PrimaryKeyDataValueBean;



/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO
 * Implement Level class for Person Merge> May 30, 2018- 11:02:41 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class PersonMergeDaoImpl implements PersonMergeDao {

	protected static final String SLASH_DATE_MASK = "MM/dd/yyyy";
	private SimpleDateFormat slashFormat = new SimpleDateFormat(SLASH_DATE_MASK);
	protected static final Date MAX_JAVA_DATE = ServiceConstants.MAX_DATE;
	private static final String ADDR_COMMENT = "Primary set by Person Merge on ";
	@Autowired
	PersonDao personDao;

	@Autowired
	EventUtilityService eventUtilityService;

	@Autowired
	EventService eventService;

	@Autowired
	PcspDao pcspDao;

	@Autowired
	FinancialAcctDao financialAcctDao;

	@Autowired
	PersonEligibilityDao personEligibilityDao;

	@Autowired
	ResourceServiceDao resourceServiceDAO;

	@Autowired
	AllegationDao allegationDao;

	@Autowired
	PersonCharDao personCharDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	FceEligibilityDao fceEligibilityDao;

	@Autowired
	AdoptionAsstncDao adoptionAsstncDao;

	@Autowired
	PersonMergeSplitDao personMergeSplitDao;

	@Autowired
	EducationDao educationDao;

	@Autowired
	PRTPersonMergeSplitDao prtPersonMergeSplitDao;

	@Autowired
	FTRelationshipSearchDao ftRelationshipSearchDao;

	@Autowired
	NameDao nameDao;

	@Autowired
	PersonAddressDao personAddressDao;

	@Autowired
	CvsFaHomeDao cvsFaHomeDao;

	@Autowired
	CriminalHistoryDao criminalHistoryDao;

	@Autowired
	PersonEmailDao personEmailDao;

	@Autowired
	PersonPhoneDao personPhoneDao;

	@Autowired
	PersonEthnicityDao personEthnicityDao;

	@Autowired
	PersonRaceDao personRaceDao;

	@Autowired
	RecordsCheckDao recordsCheckDao;

	@Autowired
	IncomeResourceDao incResDao;

	@Autowired
	EventPersonDao eventPersonDao;

	@Autowired
	SSCCPersonMergeSplitDao ssccPersonMergeSplitDao;

	@Autowired
	TletsDao tletsDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	CSADao csaDao;

	@Autowired
	TraffickingDao traffickingDao;

	@Autowired
	private SexualVictimizationHistoryDao sexualVictimizationHistoryDao;

	@Autowired
	private MutualNonAggressiveIncidentDao mutualNonAggressiveIncidentDao;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private WorkloadService workloadService;

	@Autowired
	private WorkLoadDao workLoadDao;

	@Autowired
	CaseSummaryDao caseSummaryDao;

	@Autowired
	CPSInvCnlsnDao cpsInvCnlsnDao;

	@Autowired
    LetterAllegationLinkDao letterAllegationLinkDao;

	/**
	 * 
	 * Method Name: mergePersons Method Description: merge person
	 * 
	 * @param persMergeValueBean
	 * @param sfPersValueBean
	 */
	@Override
	public void mergePersons(PersonMergeSplitDto persMergeValueBean, SelectForwardPersonValueBean sfPersValueBean) {

		// Process Select Forward Person Data
		// Merge primary name
		mergePrimaryName(persMergeValueBean, sfPersValueBean);

		// Merge name history
		mergeNameHistory(persMergeValueBean);

		// Merge person identifiers
		mergePersonIdentifiers(sfPersValueBean, persMergeValueBean);

		// Merge primary addtress
		int idNewPrimaryAddr = mergePrimaryAddress(persMergeValueBean, sfPersValueBean);

		// Merge address history
		mergePersonAddressHistory(persMergeValueBean, idNewPrimaryAddr);

		// Merge primay email
		mergePrimaryEmail(persMergeValueBean, sfPersValueBean);

		// Merge email history
		mergePersonEmailHistory(persMergeValueBean);

		// Merge primary phone number
		int idNewPrimaryPhone = mergePrimaryPhone(persMergeValueBean, sfPersValueBean);

		// Merge phone history
		mergePersonPhoneHistory(persMergeValueBean, idNewPrimaryPhone);

		// Merge income and resources
		mergeIncomeResources(persMergeValueBean, sfPersValueBean);

		// Merge income and resources history
		mergeIncomeResourcesHistory(persMergeValueBean);

		// Merge educational data
		mergeEducationData(persMergeValueBean, sfPersValueBean);

		// Merge educational history data
		mergeEducationalHistoryData(persMergeValueBean);

		// Merge relationship records
		mergePersonRelationships(persMergeValueBean);

		// Process Select Forward Demographics data
		// Fetch person forward and person closed data
		PersonDto fwdPersonValueBean = personDao.getPersonById((long) persMergeValueBean.getIdForwardPerson());
		PersonDto closedPersonValueBean = personDao.getPersonById((long) persMergeValueBean.getIdClosedPerson());

		// populate merge persons names (as we have those now) into
		// persMergeValueBean
		persMergeValueBean.setNmForwardPerson(fwdPersonValueBean.getNmPersonFull());
		persMergeValueBean.setNmClosedPerson(closedPersonValueBean.getNmPersonFull());

		CvsFaHomeValueBean fwdPersonDtlValueBean = cvsFaHomeDao
				.displayCvsFaHome(persMergeValueBean.getIdForwardPerson());
		CvsFaHomeValueBean closedPersonDtlValueBean = cvsFaHomeDao
				.displayCvsFaHome(persMergeValueBean.getIdClosedPerson());

		// If the primary name for the forward persons changed during name
		// merging
		// then update the new full name field for forward person
		if (!ObjectUtils.isEmpty(persMergeValueBean.getNewNmPrimaryForwardPerson())) {
			fwdPersonValueBean.setNmPersonFull(persMergeValueBean.getNewNmPrimaryForwardPerson());
		}

		// Merge gender
		mergePersonGender(sfPersValueBean, persMergeValueBean, fwdPersonValueBean, closedPersonValueBean);

		// Merge marital status
		mergePersonMaritalStatus(sfPersValueBean, persMergeValueBean, fwdPersonValueBean, closedPersonValueBean);

		// Merge DOB and Date of Death
		mergePersonDOBAndDOD(sfPersValueBean, persMergeValueBean, fwdPersonValueBean, closedPersonValueBean);

		// Merge person langauge
		mergePersonLanguage(sfPersValueBean, persMergeValueBean, fwdPersonValueBean, closedPersonValueBean);

		// Merge person living arrangement
		mergePersonLivingArrangement(sfPersValueBean, persMergeValueBean, fwdPersonValueBean, closedPersonValueBean);

		// Merge person occupation
		mergePersonOccupation(sfPersValueBean, persMergeValueBean, fwdPersonValueBean, closedPersonValueBean);

		// Merge person religion
		mergePersonReligion(sfPersValueBean, persMergeValueBean, fwdPersonValueBean, closedPersonValueBean);

		// If Race/Ethnicity selected from closed person then copy over the
		// race/ethnicity list from closed person
		mergePersonRaceEthnicity(persMergeValueBean, sfPersValueBean, fwdPersonValueBean);

		// Merge Citizenship status
		mergePersonCitizenshipStatus(sfPersValueBean, persMergeValueBean, fwdPersonDtlValueBean,
				closedPersonDtlValueBean);

		// Merge Person birth and prior adoption information
		mergePersonDtlMiscInformation(persMergeValueBean, fwdPersonDtlValueBean, closedPersonDtlValueBean);

		// save person dtl information if any value got changed for person_dtl
		// record
		// '180' is "Citizenship Status", '190' is "CVS/Home Info"
		if (persMergeValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_180)
				|| persMergeValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_190)) {
			// it is possible forward person does not have person detail record
			if (ObjectUtils.isEmpty(fwdPersonDtlValueBean.getIdPerson())
					|| ServiceConstants.ZERO.equals(fwdPersonDtlValueBean.getIdPerson())) {
				// we need to insert the record in person_dtl table
				fwdPersonDtlValueBean.setIdPerson(persMergeValueBean.getIdForwardPerson());
				fwdPersonDtlValueBean.setDtLastUpdate(new Date());
				cvsFaHomeDao.insertIntoPersonDetail(fwdPersonDtlValueBean);
			} else {
				// update the last update date/time from the value which was
				// part of
				// select forward page (we may not have this for staff records)
				if (!ObjectUtils.isEmpty(sfPersValueBean.getCitizenStat().getForwardPerson()))
					fwdPersonDtlValueBean.setDtLastUpdate(
							sfPersValueBean.getCitizenStat().getForwardPerson().getPrimaryKeyData().getDtLastUpdate());

				// we need to insert the record in person_dtl table
				cvsFaHomeDao.updatePersonDetail(fwdPersonDtlValueBean);
			}
		}
		
		// merge records check
		boolean criminalMessageSet = mergeRecordsCheck(persMergeValueBean);

		// merge TLETS. SIR 1006528. TLETS is a record check with Texas police
		// agencies.
		mergeTlets(persMergeValueBean, criminalMessageSet);

		// merge FA training records
		mergeFosterAdoptiveTraining(persMergeValueBean);

		// merge person characteristics and also set the characteristics code on
		// Forward person value bean.
		mergePersonCharacteristics(persMergeValueBean, fwdPersonValueBean);

		personMergeSplitDao.updatePersonOnIncomingPersonMPS(persMergeValueBean.getIdForwardPerson(),
				persMergeValueBean.getIdClosedPerson());

		// merge child safety placements
		mergeChildSafetyPlacements(persMergeValueBean);

		// merge financial accounts
		mergeFinancialAccounts(persMergeValueBean);

		// merge person eligibility
		mergePersonEligibilities(persMergeValueBean);

		// The DHS type person eligibilities are handled little differently
		// Therefore, calling the following function
		mergeDHSPersonEligibilities(persMergeValueBean);

		// Merge SSCC Records in OPEN Stages.
		// Gets all the Referrals (Active and Inactive) for the Closed Person in
		// any Open Stage,
		// and update Closed Person ID with Forward Person ID for these tables.
		// SSCC_REFERRAL_FAMILY, SSCC_PLCMT_MED_CNSNTR, SSCC_PLCMT_CIRCUMSTANCE,
		// SSCC_CHILD_PLAN_PARTICIP
		processSSCCPersonData(persMergeValueBean);

		// Merge PRT
		mergePRT(persMergeValueBean);

		// Merge PCSP
		mergePCSP(persMergeValueBean);

		// Merge legacy PCSP
		mergeLegacyPCSP(persMergeValueBean);

		// Merge Child Sexual Aggression
		mergeChildSexualAggression(persMergeValueBean);

		/* Merge Trafficking */
		mergeTrafficking(persMergeValueBean);

		/* Merge Sexual Victimization Incidents */
		mergeChildSxVctmztnIncdnts(persMergeValueBean);
		
		/*update table PLACEMENT_TA -> ID_RESPITE_PERSON column if any rows present with closed person id 
		 * this table does not have ID_PERSON columns as this is child table to PLACEMENT table
		 * but this has a column which is FK to PERSON table so if records present with closed person, we have to update with fwd person id*/
		updatePlacementTAWithFwdPerson(persMergeValueBean);

		updateRTBExceptionWithFwdPerson(persMergeValueBean);
		/* Merge Mutual Non-Aggressive */
		mergeMutualNonAggressive(persMergeValueBean);

		processWorkloadAlerts(persMergeValueBean);

		// OPEN stage processing
		// Determine if the Person Closed is listed in any open stages.
		// If so, get all Stage IDs and use for processing Open Stage
		// information
		// for Person Closed.
		processOpenStagePersonData(persMergeValueBean);

		// Update the Person Forward Status according to the following business
		// rules: Set the Status = Active if Either the Person Closed = Active
		// OR Person Forward= Active
		// Set the Status = Inactive if Person Closed = Inactive AND
		// Person Forward = Inactive
		if (CodesConstant.CPERSTAT_A.equals(closedPersonValueBean.getCdPersonStatus())
				|| CodesConstant.CPERSTAT_A.equals(fwdPersonValueBean.getCdPersonStatus())) {
			fwdPersonValueBean.setCdPersonStatus(CodesConstant.CPERSTAT_A);
		} else if (CodesConstant.CPERSTAT_I.equals(closedPersonValueBean.getCdPersonStatus())
				&& CodesConstant.CPERSTAT_I.equals(fwdPersonValueBean.getCdPersonStatus())) {
			fwdPersonValueBean.setCdPersonStatus(CodesConstant.CPERSTAT_I);
		}

		// Update the Person Closed Status to 'MERGED'.
		closedPersonValueBean.setCdPersonStatus(CodesConstant.CPERSTAT_M);
		Person closedPerson = new Person();
		BeanUtils.copyProperties(closedPersonValueBean, closedPerson);
		personDao.updatePerson(closedPerson);

		Person fwdPerson = new Person();
		BeanUtils.copyProperties(fwdPersonValueBean, fwdPerson);
		personDao.updatePerson(fwdPerson);

		// Update the Person Forward Categories
		boolean bIndFAHome = false; // indicator if person is PRN or COL in a FA
									// Home
		boolean bIndCase = false; // indicator is person is PRN or COL in a case

		// get all the stages for forward person
		List<StagePersonValueDto> personStageList = personMergeSplitDao
				.getStagesForPersonMergeView(persMergeValueBean.getIdForwardPerson().intValue());
		if (personStageList.stream()
				.anyMatch(stgPersValueBean -> (ServiceConstants.CSTAGES_FAD.equals(stgPersValueBean.getCdStage())
						&& (ServiceConstants.CPRSNTYP_PRN.equals(stgPersValueBean.getCdStagePersType())
								|| CodesConstant.CPRSNTYP_COL.equals(stgPersValueBean.getCdStagePersType())))))
			bIndFAHome = true;

		if (personStageList.stream().anyMatch(
				stgPersValueBean -> (ServiceConstants.CPRSNTYP_PRN.equals(stgPersValueBean.getCdStagePersType())
						|| CodesConstant.CPRSNTYP_COL.equals(stgPersValueBean.getCdStagePersType()))))
			bIndCase = true;

		// Delete all person categories except EMP and FEM.
		// Only if forward person has some category after the merge
		if (bIndFAHome || bIndCase) {
			personMergeSplitDao.deleteNonEmpPersonCategories(persMergeValueBean.getIdForwardPerson());
		}

		// Add needed person categories
		// Add FAH category if person is PRN or COL in a FA Home
		if (bIndFAHome) {
			personMergeSplitDao.savePersonCategory(persMergeValueBean.getIdForwardPerson(), CodesConstant.CPSNDTCT_FAH);
		}
		// Add CAS category if person is PRN or COL in a stage
		if (bIndCase) {
			personMergeSplitDao.savePersonCategory(persMergeValueBean.getIdForwardPerson(), CodesConstant.CPSNDTCT_CAS);
		}

	}

	private void updateRTBExceptionWithFwdPerson(PersonMergeSplitDto persMergeValueBean) {
		personMergeSplitDao.updateRTBExceptionWithFwdPerson(persMergeValueBean.getIdForwardPerson(),persMergeValueBean.getIdClosedPerson(),persMergeValueBean.getIdPersonMergeWorker());

	}

	/* PPM 65209
	 * update table PLACEMENT_TA -> ID_RESPITE_PERSON column if any rows present with closed person id 
	 * this table does not have ID_PERSON columns as this is child table to PLACEMENT table
	 * but this has a column which is FK to PERSON table so if records present with closed person,
	 *  we have to update with fwd person id */
	public void updatePlacementTAWithFwdPerson(PersonMergeSplitDto persMergeValueBean){
		personMergeSplitDao.updatePlacementTAWithFwdPerson(persMergeValueBean.getIdForwardPerson(),persMergeValueBean.getIdClosedPerson(),persMergeValueBean.getIdPersonMergeWorker());
	}

	/**
	 * This function checks if user has selected any of the name components from
	 * closed person, then inserts the new primary name for the forward person. The
	 * existing primary name for the forward is End Dated before entering new
	 * primary name
	 * 
	 * @param Connection         (Database Connection)
	 * @param persMergeValueBean (Person merge data)
	 * @param sfPersValeBean     - Select person forward data
	 * @return
	 */
	private void mergePrimaryName(PersonMergeSplitDto persMergeValueBean, SelectForwardPersonValueBean sfPersValeBean) {
		Name fwdPersonNameDB = null;

		// Update Person Forward Primary Name in the event that any Person
		// Closed
		// Name selections have been made
		// or Name Forward selections are a combination of Person Closed and
		// Person
		// Forward
		if (sfPersValeBean.getFirstName().getClosedPerson().getIsSelected()
				|| sfPersValeBean.getMiddleName().getClosedPerson().getIsSelected()
				|| sfPersValeBean.getLastName().getClosedPerson().getIsSelected()
				|| sfPersValeBean.getSuffix().getClosedPerson().getIsSelected()) {

			Name fwdCurrPrimaryName = nameDao.getNameById(
					(long) sfPersValeBean.getFirstName().getForwardPerson().getPrimaryKeyData().getIdKey());
			Name closedCurrPrimaryName = nameDao
					.getNameById((long) sfPersValeBean.getFirstName().getClosedPerson().getPrimaryKeyData().getIdKey());

			fwdPersonNameDB = new Name();
			// if first name selected from closed person
			if (!ObjectUtils.isEmpty(sfPersValeBean.getFirstName().getClosedPerson())
					&& sfPersValeBean.getFirstName().getClosedPerson().getIsSelected())
				fwdPersonNameDB.setNmNameFirst(closedCurrPrimaryName.getNmNameFirst());
			else
				fwdPersonNameDB.setNmNameFirst(fwdCurrPrimaryName.getNmNameFirst());

			// if middle name selected from closed person
			if (!ObjectUtils.isEmpty(sfPersValeBean.getMiddleName().getClosedPerson())
					&& sfPersValeBean.getMiddleName().getClosedPerson().getIsSelected())
				fwdPersonNameDB.setNmNameMiddle(closedCurrPrimaryName.getNmNameMiddle());
			else
				fwdPersonNameDB.setNmNameMiddle(fwdCurrPrimaryName.getNmNameMiddle());

			// if last name selected from closed person
			if (!ObjectUtils.isEmpty(sfPersValeBean.getLastName().getClosedPerson())
					&& sfPersValeBean.getLastName().getClosedPerson().getIsSelected())
				fwdPersonNameDB.setNmNameLast(closedCurrPrimaryName.getNmNameLast());
			else
				fwdPersonNameDB.setNmNameLast(fwdCurrPrimaryName.getNmNameLast());

			// if suffix selected from closed person
			if (!ObjectUtils.isEmpty(sfPersValeBean.getSuffix().getClosedPerson())
					&& sfPersValeBean.getSuffix().getClosedPerson().getIsSelected())
				fwdPersonNameDB.setCdNameSuffix(closedCurrPrimaryName.getCdNameSuffix());
			else
				fwdPersonNameDB.setCdNameSuffix(fwdCurrPrimaryName.getCdNameSuffix());

			// End date the existing primary name. We want to use dtLastUpdate
			// which
			// was present at the time
			// Name record was read for select forward page
			fwdCurrPrimaryName.setDtLastUpdate(
					sfPersValeBean.getFirstName().getForwardPerson().getPrimaryKeyData().getDtLastUpdate());
			fwdPersonNameDB.setDtLastUpdate(fwdCurrPrimaryName.getDtLastUpdate());
			NameDto nameDto = mappingName(fwdCurrPrimaryName);

			// refer with ram
			nameDao.endDatePersonName(nameDto);

			// Save rest of the information for the forward person's new primary
			// name
			Person person = new Person();
			person.setIdPerson((long) persMergeValueBean.getIdForwardPerson());
			fwdPersonNameDB.setPerson(person);
			fwdPersonNameDB.setIndNamePrimary(ServiceConstants.Y);
			fwdPersonNameDB.setIndNameInvalid(ServiceConstants.N);
			fwdPersonNameDB.setDtNameStartDate(DateUtils.getDateWithoutTime(new Date()));

			// Insert new primary name for the forward person.
			nameDao.saveName(fwdPersonNameDB);

			String newFullName = formatFullName(fwdPersonNameDB.getNmNameFirst(), fwdPersonNameDB.getNmNameMiddle(),
					fwdPersonNameDB.getNmNameLast());

			// record the new primary full name for forward person so that we
			// can
			// save it later on PERSON record for forward person.
			persMergeValueBean.setNewNmPrimaryForwardPerson(newFullName);

			// Record the Name change for Person Merge Update Log
			if (!persMergeValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_100))
				persMergeValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_100, ServiceConstants.IND_TRUE);
		}
	} // end of method mergePrimaryName

	/**
	 * This function merges the names history from closed person to forward person.
	 * Adds any Person Closed Names not present for Person Forward.
	 * 
	 * @param Connection            (Database Connection)
	 * @param persMergeVldValueBean
	 * @return
	 */
	private void mergeNameHistory(PersonMergeSplitDto persMergeVldValueBean) {

		// Fetch all the names for forward person
		List<Name> fwdPersNameList = nameDao.getNamesByPersonId(persMergeVldValueBean.getIdForwardPerson());

		// Fetch all the names for closed person
		List<Name> closedPersNameList = nameDao.getNamesByPersonId(persMergeVldValueBean.getIdClosedPerson());

		// Compare the names and add any Person Closed Names not present for
		// Person
		// Forward.
		for (Name closedPersName : closedPersNameList) {
			boolean bMatchFound = false;
			if (fwdPersNameList.stream().anyMatch(
					fwdPersName -> checkForEquality(closedPersName.getNmNameFirst(), fwdPersName.getNmNameFirst())
							&& checkForEquality(closedPersName.getNmNameMiddle(), fwdPersName.getNmNameMiddle())
							&& checkForEquality(closedPersName.getNmNameLast(), fwdPersName.getNmNameLast())
							&& checkForEquality(closedPersName.getCdNameSuffix(), fwdPersName.getCdNameSuffix())))
				bMatchFound = true;

			// If no match found for the name in forward person then add it to
			// forward
			// person
			if (!bMatchFound) {
				Person person = new Person();
				person.setIdPerson((long) persMergeVldValueBean.getIdForwardPerson());
				closedPersName.setPerson(person);
				closedPersName.setIndNamePrimary(ServiceConstants.N);
				nameDao.saveName(closedPersName);

				// Record the Name change for Person Merge Update Log
				if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_100))
					persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_100, ServiceConstants.IND_TRUE);
			}

		} // end closed person for loop

	} // end of method mergeNameHistory

	/**
	 * This method merges the person identifiers based on user selection in select
	 * person forward page. It also merges the person identifiers history.
	 * 
	 * @param PersonDao           Person data access object
	 * @param sfPersValeBean      (SelectForwardPersonValueBean)
	 * @param PersonMergeSplitDto (personMergeSplitDto)
	 * 
	 * @return
	 */
	private void mergePersonIdentifiers(SelectForwardPersonValueBean sfPersValeBean,
			PersonMergeSplitDto persMergeVldValueBean) {
		// If user has selected the SSN from closed person
		if (!ObjectUtils.isEmpty(sfPersValeBean.getSSN().getClosedPerson())
				&& sfPersValeBean.getSSN().getClosedPerson().getIsSelected()) {
			// End date the existing SSN from forward person
			PersonIdentifiersDto persIdentValueBean = new PersonIdentifiersDto();
			if (!ObjectUtils.isEmpty(sfPersValeBean.getSSN().getForwardPerson().getPrimaryKeyData())) {
				persIdentValueBean.setIdPersonId(
						(long) sfPersValeBean.getSSN().getForwardPerson().getPrimaryKeyData().getIdKey());
				persIdentValueBean.setDtLastUpdated(
						sfPersValeBean.getSSN().getForwardPerson().getPrimaryKeyData().getDtLastUpdate());
				personDao.endDatePersonIdentifier(persIdentValueBean);
			}

			// Fetch closed Person SSN identifier based on the ID
			PersonIdentifiersDto closedPersIdentValueBean = personDao
					.getPersonIdentifier(sfPersValeBean.getSSN().getClosedPerson().getPrimaryKeyData().getIdKey());

			// Add new SSN person identifier for forward person based on the
			// value of
			// closed person
			closedPersIdentValueBean.setIdPersonId(0l);
			closedPersIdentValueBean.setIdPerson((long) persMergeVldValueBean.getIdForwardPerson());
			personDao.savePersonIdentifier(closedPersIdentValueBean);

			// Record change in SSN into Person Merge Update Log
			if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_250))
				persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_250, ServiceConstants.IND_TRUE);
		}

		// If user has selected the TDHS Client # from closed person
		if (!ObjectUtils.isEmpty(sfPersValeBean.getTDHSClient().getClosedPerson())
				&& sfPersValeBean.getTDHSClient().getClosedPerson().getIsSelected()) {
			// End date the existing TDHS Client # from forward person
			PersonIdentifiersDto persIdentValueBean = new PersonIdentifiersDto();
			if (!ObjectUtils.isEmpty(sfPersValeBean.getTDHSClient().getForwardPerson().getPrimaryKeyData())) {
				persIdentValueBean.setIdPersonId(
						(long) sfPersValeBean.getTDHSClient().getForwardPerson().getPrimaryKeyData().getIdKey());
				persIdentValueBean.setDtLastUpdated(
						sfPersValeBean.getTDHSClient().getForwardPerson().getPrimaryKeyData().getDtLastUpdate());
				personDao.endDatePersonIdentifier(persIdentValueBean);
			}
			// Fetch closed Person TDHS Client # based on the Id (PK)
			PersonIdentifiersDto closedPersIdentValueBean = personDao.getPersonIdentifier(
					sfPersValeBean.getTDHSClient().getClosedPerson().getPrimaryKeyData().getIdKey());

			// Add new TDHS Client # for forward person based on the value of
			// closed person
			closedPersIdentValueBean.setIdPersonId(0l);
			closedPersIdentValueBean.setIdPerson((long) persMergeVldValueBean.getIdForwardPerson());
			personDao.savePersonIdentifier(closedPersIdentValueBean);

			// Record change in TDHS Client # into Person Merge Update Log
			if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_260))
				persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_260, ServiceConstants.IND_TRUE);
		}

		// If user has selected the Medicaid # from closed person
		if (!ObjectUtils.isEmpty(sfPersValeBean.getMedicaid().getClosedPerson())
				&& sfPersValeBean.getMedicaid().getClosedPerson().getIsSelected()) {
			// End date the existing Medicaid # from forward person
			PersonIdentifiersDto persIdentValueBean = new PersonIdentifiersDto();
			if (!ObjectUtils.isEmpty(sfPersValeBean.getMedicaid().getForwardPerson().getPrimaryKeyData())) {
				persIdentValueBean.setIdPersonId(
						(long) sfPersValeBean.getMedicaid().getForwardPerson().getPrimaryKeyData().getIdKey());
				persIdentValueBean.setDtLastUpdated(
						sfPersValeBean.getMedicaid().getForwardPerson().getPrimaryKeyData().getDtLastUpdate());
				personDao.endDatePersonIdentifier(persIdentValueBean);
			}
			// Fetch closed Person Medicaid # based on the Id (PK)
			PersonIdentifiersDto closedPersIdentValueBean = personDao
					.getPersonIdentifier(sfPersValeBean.getMedicaid().getClosedPerson().getPrimaryKeyData().getIdKey());

			// Add new Medicaid # for forward person based on the value of
			// closed person
			closedPersIdentValueBean.setIdPersonId(0l);
			closedPersIdentValueBean.setIdPerson((long) persMergeVldValueBean.getIdForwardPerson());
			personDao.savePersonIdentifier(closedPersIdentValueBean);

			// Record change in Medicaid # into Person Merge Update Log
			if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_270))
				persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_270, ServiceConstants.IND_TRUE);
		}

		// If user has selected the Driver License from closed person
		if (!ObjectUtils.isEmpty(sfPersValeBean.getDrivLicNo().getClosedPerson())
				&& sfPersValeBean.getDrivLicNo().getClosedPerson().getIsSelected()) {
			// End date the existing Driver License # from forward person
			PersonIdentifiersDto persIdentValueBean = new PersonIdentifiersDto();
			if (!ObjectUtils.isEmpty(sfPersValeBean.getDrivLicNo().getForwardPerson().getPrimaryKeyData())) {
				persIdentValueBean.setIdPersonId(
						(long) sfPersValeBean.getDrivLicNo().getForwardPerson().getPrimaryKeyData().getIdKey());
				persIdentValueBean.setDtLastUpdated(
						sfPersValeBean.getDrivLicNo().getForwardPerson().getPrimaryKeyData().getDtLastUpdate());
				personDao.endDatePersonIdentifier(persIdentValueBean);
			}

			// Fetch closed Person Driver License based on the Id (PK)
			PersonIdentifiersDto closedPersIdentValueBean = personDao.getPersonIdentifier(
					sfPersValeBean.getDrivLicNo().getClosedPerson().getPrimaryKeyData().getIdKey());

			// Add new Driver License # for forward person based on the value of
			// closed person
			closedPersIdentValueBean.setIdPersonId(0l);
			closedPersIdentValueBean.setIdPerson((long) persMergeVldValueBean.getIdForwardPerson());
			personDao.savePersonIdentifier(closedPersIdentValueBean);

			// Record change in Driver License into Person Merge Update Log
			if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_280))
				persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_280, ServiceConstants.IND_TRUE);
		}

		// If user has selected the State Photo Id from closed person
		if (!ObjectUtils.isEmpty(sfPersValeBean.getStatePhotoId().getClosedPerson())
				&& sfPersValeBean.getStatePhotoId().getClosedPerson().getIsSelected()) {
			// End date the existing Driver License # from forward person
			PersonIdentifiersDto persIdentValueBean = new PersonIdentifiersDto();
			if (!ObjectUtils.isEmpty(sfPersValeBean.getStatePhotoId().getForwardPerson().getPrimaryKeyData())) {
				persIdentValueBean.setIdPersonId(
						(long) sfPersValeBean.getStatePhotoId().getForwardPerson().getPrimaryKeyData().getIdKey());
				persIdentValueBean.setDtLastUpdated(
						sfPersValeBean.getStatePhotoId().getForwardPerson().getPrimaryKeyData().getDtLastUpdate());
				personDao.endDatePersonIdentifier(persIdentValueBean);
			}

			// Fetch closed Person State Photo Id based on the Id (PK)
			PersonIdentifiersDto closedPersIdentValueBean = personDao.getPersonIdentifier(
					sfPersValeBean.getStatePhotoId().getClosedPerson().getPrimaryKeyData().getIdKey());

			// Add new State Photo Id for forward person based on the value of
			// closed person
			closedPersIdentValueBean.setIdPersonId(0l);
			closedPersIdentValueBean.setIdPerson((long) persMergeVldValueBean.getIdForwardPerson());
			personDao.savePersonIdentifier(closedPersIdentValueBean);

			// Record change in State Photo Id into Person Merge Update Log
			if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_290))
				persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_290, ServiceConstants.IND_TRUE);
		}

		// Fetch person identifier list for fwd person
		List<PersonIdentifiersDto> fwdArrList = personDao
				.getPersonIdentifiersList(persMergeVldValueBean.getIdForwardPerson());
		// Fetch person idenifier list for closed person
		List<PersonIdentifiersDto> closedArrList = personDao
				.getPersonIdentifiersList(persMergeVldValueBean.getIdClosedPerson());

		// update missing identifier history from person closed to person
		// forward
		boolean bPersIdentExist = false;
		for (PersonIdentifiersDto closedPersIdentValueBean : closedArrList) {
			bPersIdentExist = false;
			/** Fix for ALM Defect#ALM 13737  Caregiver PID not created in IMPACT.  */
			if (fwdArrList.stream()
					.anyMatch(fwdPersIdentValueBean -> checkForEquality (closedPersIdentValueBean.getPersonIdType(), fwdPersIdentValueBean.getPersonIdType()) &&
							checkForEquality(closedPersIdentValueBean.getPersonIdNumber(),
							fwdPersIdentValueBean.getPersonIdNumber()))) {
				bPersIdentExist = true;
			}

			// if identified does not exist for for forward person then add it
			if (!bPersIdentExist) {
				PersonIdentifiersDto  forwardPersonIdentifiersDto  = new PersonIdentifiersDto();
				BeanUtils.copyProperties(closedPersIdentValueBean , forwardPersonIdentifiersDto ,"idPersonId");
				forwardPersonIdentifiersDto.setIdPerson((long) persMergeVldValueBean.getIdForwardPerson());
				/**
				 * if closed person identifier is active, then set the end date to 
				 * system date when adding it for fwd person
				 * Fix for ALM Defects 14618,14422
				 */
				if (DateUtils.daysDifference(closedPersIdentValueBean.getDtPersonIdEnd(), MAX_JAVA_DATE) == 0) {
					Date today = Calendar.getInstance().getTime();
					forwardPersonIdentifiersDto.setDtPersonIdEnd(today);
				}
				// End of fix for ALM #14618,14422
				personDao.savePersonIdentifier(forwardPersonIdentifiersDto);
				
               /**end of the fix Defect#ALM 13737 **/
				// Record addition of a particular identifier into Person Merge
				// UpdateLog
				switch (closedPersIdentValueBean.getPersonIdType()) {
				case CodesConstant.CNUMTYPE_SSN:
					if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_250))
						persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_250,
								ServiceConstants.IND_TRUE);
					break;
				case CodesConstant.CNUMTYPE_TDHS_CLIENT_NUMBER:
					if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_260))
						persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_260,
								ServiceConstants.IND_TRUE);
					break;
				case CodesConstant.CNUMTYPE_MEDICAID_NUMBER:
					if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_270))
						persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_270,
								ServiceConstants.IND_TRUE);
					break;
				case CodesConstant.CNUMTYPE_DRIVERS_LICENSE_NUMBER:
					if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_280))
						persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_280,
								ServiceConstants.IND_TRUE);
					break;
				case CodesConstant.CNUMTYPE_STATE_PHOTO_ID_NUMBER:
					if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_290))
						persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_290,
								ServiceConstants.IND_TRUE);
					break;
				default:
					if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_300))
						persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_300,
								ServiceConstants.IND_TRUE);
					break;
				}
			}
		}
	}

	/**
	 * Update Person Forward Primary Address in the event that Person Closed Primary
	 * Address was selected
	 * 
	 * @param Connection
	 * @param PersonMergeSplitDto
	 * @param sfPersValeBean      - Select person forward data
	 * @return int ( ID of the primary address added)
	 */
	private int mergePrimaryAddress(PersonMergeSplitDto persMergeVldValueBean,
			SelectForwardPersonValueBean sfPersValeBean) {
		// If Primary Address is selected from Closed person
		if (sfPersValeBean.getAddress().getClosedPerson().getIsSelected()) {
			// fetch the primary address for the closed person and use this
			// address to
			// create new primary address for the forward person
			// sfPersValeBean.getClosedAddressLine1().getDataValueId()
			// Modified the code to call getPersonAddressInfo - Warranty defect 12374
			AddressDto closedPersAddrValueBean = personAddressDao.getPersonAddressInfo(
					(long) sfPersValeBean.getAddress().getClosedPerson().getPrimaryKeyData().getIdKey());
			if (!ObjectUtils.isEmpty(closedPersAddrValueBean)) {
				// If forward person has a current primary address, mark it as
				// non
				// primary
				if (!ObjectUtils.isEmpty(sfPersValeBean.getAddress().getForwardPerson())
						&& !ObjectUtils.isEmpty(sfPersValeBean.getAddress().getForwardPerson().getPrimaryKeyData())
						&& sfPersValeBean.getAddress().getForwardPerson().getPrimaryKeyData().getIdKey() > 0) {
					AddressDto fwdAddrValueBean = new AddressDto();
					fwdAddrValueBean.setIdAddrPersonLink(
							(long) sfPersValeBean.getAddress().getForwardPerson().getPrimaryKeyData().getIdKey());
					fwdAddrValueBean.setTsLastUpdate(
							sfPersValeBean.getAddress().getForwardPerson().getPrimaryKeyData().getDtLastUpdate());
					personAddressDao.changeAddressToNonPrimary(fwdAddrValueBean);
				}
				// Add the new primary address for the person forward based on
				// closed
				// persons primary name. Before that we need to reset of the
				// values

				closedPersAddrValueBean.setIdPerson((long) persMergeVldValueBean.getIdForwardPerson());
				closedPersAddrValueBean.setIdPersonMerge(0l);
				closedPersAddrValueBean.setPersAddrCmnts(ADDR_COMMENT + dateToString(new Date(), slashFormat));
				closedPersAddrValueBean.setDtPersAddrLinkStart(new Date()); // system
																			// date
																			// as
																			// start
																			// date

				personAddressDao.saveAddress(closedPersAddrValueBean);

				// Record any address changes into person merge update log
				if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_310))
					persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_310, ServiceConstants.IND_TRUE);

				return closedPersAddrValueBean.getIdAddrPersonLink().intValue();
			}
		}

		return 0;
	}

	/**
	 * Add any Person Closed Address(es) not present for Person Forward - In the
	 * event the Person Closed has a current Address of a Type not present for the
	 * Person Forward, add that Address to the Person Forward as an address for that
	 * type with the same status as the Person Closed address. - In the event the
	 * Person Closed has a current Address which is also a current Address of the
	 * same Type for the Person Forward, If Start/End (if Present) dates do not
	 * match, set an indicator (ID_PERSON_MERGE) in the Person Forward Address that
	 * a duplicate address with differing Start/End Dates was found.
	 * 
	 * @param Connection
	 * @param personMergeSplitDto
	 * @param idNewPrimaryAddr    - Id of the newly added Primary Address for
	 *                            forward person
	 * @return
	 */
	private void mergePersonAddressHistory(PersonMergeSplitDto personMergeSplitDto, int idNewPrimaryAddr) {

		AddressDtlReq fwdPersonAddrReq = new AddressDtlReq();
		AddressDtlReq closePersAddrReq = new AddressDtlReq();
		fwdPersonAddrReq.setUlIdPerson(personMergeSplitDto.getIdForwardPerson());
		closePersAddrReq.setUlIdPerson(personMergeSplitDto.getIdClosedPerson());
		List<AddressDto> closedPersAddrList = personAddressDao.getAddressList(closePersAddrReq);
		List<AddressDto> fwdPersAddrList = personAddressDao.getAddressList(fwdPersonAddrReq);
		boolean bMatchFound = false;

		for (AddressDto closedPersonAddr : closedPersAddrList) {
			bMatchFound = false;
			AddressDto fwdPersonAddr = null;

			// First try to match address in same type. And then
			// try other address type.
			Optional<AddressDto> fwdOptional = fwdPersAddrList.stream().filter(addressDto -> (checkForEquality(
					closedPersonAddr.getCdPersAddrLinkType(), addressDto.getCdPersAddrLinkType())
					&& checkForEquality(closedPersonAddr.getAddrPersAddrStLn1(), addressDto.getAddrPersAddrStLn1())
					&& checkForEquality(closedPersonAddr.getAddrPersAddrStLn2(), addressDto.getAddrPersAddrStLn2())
					&& checkForEquality(closedPersonAddr.getAddrCity(), addressDto.getAddrCity())
					&& checkForEquality(closedPersonAddr.getCdAddrState(), addressDto.getCdAddrState())
					&& checkForEquality(closedPersonAddr.getAddrZip(), addressDto.getAddrZip())
					&& checkForEquality(closedPersonAddr.getCdAddrCounty(), addressDto.getCdAddrCounty()))).findFirst();
			if (fwdOptional.isPresent()) {
				fwdPersonAddr = fwdOptional.get();
				bMatchFound = true;
			}
			// And now try to find a match in other address types
			if (!bMatchFound) {
				Optional<AddressDto> tmpOptional = fwdPersAddrList.stream().filter(addressDto -> (checkForEquality(
						closedPersonAddr.getAddrPersAddrStLn1(), addressDto.getAddrPersAddrStLn1())
						&& checkForEquality(closedPersonAddr.getAddrPersAddrStLn2(), addressDto.getAddrPersAddrStLn2())
						&& checkForEquality(closedPersonAddr.getAddrCity(), addressDto.getAddrCity())
						&& checkForEquality(closedPersonAddr.getCdAddrState(), addressDto.getCdAddrState())
						&& checkForEquality(closedPersonAddr.getAddrZip(), addressDto.getAddrZip())
						&& checkForEquality(closedPersonAddr.getCdAddrCounty(), addressDto.getCdAddrCounty())))
						.findFirst();
				if (tmpOptional.isPresent()) {
					fwdPersonAddr = tmpOptional.get();
					bMatchFound = true;
				}
			}

			// if address not found, then add it

			if (!bMatchFound) {
				// if the address being added is current and if it is medicaid
				// card
				// address and if forward person already have a current medicaid
				// card
				// address then new address which we are adding for forward
				// person must be added as end dated
				if (CodesConstant.CADDRTYP_MD.equals(closedPersonAddr.getCdPersAddrLinkType())
						&& closedPersonAddr.getDtPersAddrLinkEnd().after(new Date())
						&& (fwdPersAddrList.stream()
								.anyMatch(tmpAddr -> (CodesConstant.CADDRTYP_MD.equals(tmpAddr.getCdPersAddrLinkType())
										&& tmpAddr.getDtPersAddrLinkEnd().after(new Date()))))) {
					// check if person forward has a current address of type MD
					// (Medicaid Card)
					closedPersonAddr.setDtPersAddrLinkEnd(new Date());
				}
				closedPersonAddr.setIdPerson((long) personMergeSplitDto.getIdForwardPerson());
				closedPersonAddr.setIdPersonMerge(0l);

				// as part of merging history we should never save a non end
				// dated addres as Primary

				if (!ObjectUtils.isEmpty(closedPersonAddr.getDtPersAddrLinkEnd())
						&& DateUtils.daysDifference(closedPersonAddr.getDtPersAddrLinkEnd(), MAX_JAVA_DATE) == 0
						&& ServiceConstants.ARCHITECTURE_CONS_Y.equals(closedPersonAddr.getIndPersAddrLinkPrimary())) {
					closedPersonAddr.setIndPersAddrLinkPrimary(ServiceConstants.ARCHITECTURE_CONS_N);
				}

				personAddressDao.saveAddress(closedPersonAddr);

				// Record any address changes into person merge update log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_310))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_310, ServiceConstants.IND_TRUE);

			}
			// if forward person has the matching address
			else if (bMatchFound) {
				// check if the address type also match for the matching address
				if (fwdPersonAddr.getCdPersAddrLinkType().equals(closedPersonAddr.getCdPersAddrLinkType())
						&& !ObjectUtils.isEmpty(fwdPersonAddr.getDtPersAddrLinkStart())
						&& !ObjectUtils.isEmpty(closedPersonAddr.getDtPersAddrLinkStart())
						&& !ObjectUtils.isEmpty(fwdPersonAddr.getDtPersAddrLinkEnd())
						&& !ObjectUtils.isEmpty(closedPersonAddr.getDtPersAddrLinkEnd())) {
					// if start and end date of both the addresses dont match
					// then update
					// ID_PERSON_MERGE on the address_person_link
					// also ignore newly created primary address as part of
					// merge

					if (((int) DateUtils.daysDifference(fwdPersonAddr.getDtPersAddrLinkStart(),
							closedPersonAddr.getDtPersAddrLinkStart()) != 0
							|| (int) DateUtils.daysDifference(fwdPersonAddr.getDtPersAddrLinkEnd(),
									closedPersonAddr.getDtPersAddrLinkEnd()) != 0)
							&& idNewPrimaryAddr != fwdPersonAddr.getIdAddrPersonLink()) {
						AddressPersonLink tmpAddr = personAddressDao
								.getPersonAddress(fwdPersonAddr.getIdAddrPersonLink());
						// update id_person_merge on address_person_link for
						// forward person
						tmpAddr.setIdPersonMerge((long) personMergeSplitDto.getIdPersonMerge());
						personAddressDao.updateAddressPersonLink(tmpAddr);
					}
				} else
				// create new address
				{
					// add the address for the forward person based on the
					// values from
					// closed person. if the address being added is current and
					// if it is medicaid card address
					// and if forward person already have a current medicaid
					// card address
					// then new address which we are adding for forward person
					// must be
					// added as end dated
					if (CodesConstant.CADDRTYP_MD.equals(closedPersonAddr.getCdPersAddrLinkType())
							&& closedPersonAddr.getDtPersAddrLinkEnd().after(new Date())) {
						// check if person forward has a current address of type
						// MD (Medicaid Card)
						if (fwdPersAddrList.stream()
								.anyMatch(tmpAddr -> (CodesConstant.CADDRTYP_MD.equals(tmpAddr.getCdPersAddrLinkType())
										&& tmpAddr.getDtPersAddrLinkEnd().after(new Date())))) {
							closedPersonAddr.setDtPersAddrLinkEnd(new Date());
						}
					}
					closedPersonAddr.setIdPerson((long) personMergeSplitDto.getIdForwardPerson());
					closedPersonAddr.setIdPersonMerge(0l);
					// as part of merging history we should never save a non end
					// dated addres as Primary

					if (!ObjectUtils.isEmpty(closedPersonAddr.getDtPersAddrLinkEnd())
							&& DateUtils.daysDifference(closedPersonAddr.getDtPersAddrLinkEnd(), MAX_JAVA_DATE) == 0
							&& ServiceConstants.ARCHITECTURE_CONS_Y
									.equals(closedPersonAddr.getIndPersAddrLinkPrimary())) {
						closedPersonAddr.setIndPersAddrLinkPrimary(ServiceConstants.ARCHITECTURE_CONS_N);
					}

					personAddressDao.saveAddress(closedPersonAddr);

					// Record any address changes into person merge update log
					if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_310))
						personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_310,
								ServiceConstants.IND_TRUE);
				}
			}
		} // for loop closed person

	}

	/**
	 * Update Person Forward Primary Email Address in the event that Person Closed
	 * Primary Email Address was selected. Maintain current IMPACT business rule
	 * that if a Person has any current Email Addresses, that one must be designated
	 * Primary.
	 * 
	 * @param Connection
	 * @param PersonMergeSplitDto
	 * @param sfPersValeBean      - Select person forward data
	 * @return
	 */
	private void mergePrimaryEmail(PersonMergeSplitDto persMergeVldValueBean,
			SelectForwardPersonValueBean sfPersValeBean) {

		// If Primary Email is selected from Closed person
		if (sfPersValeBean.getEmail().getClosedPerson().getIsSelected()) {
			// fetch the primary email for the closed person and use this email
			// to
			// create new primary email for the forward person
			int closedPersIdPersonEmail = sfPersValeBean.getEmail().getClosedPerson().getPrimaryKeyData().getIdKey();

			PersonEmailValueDto closedPersEmailValueBean = personEmailDao.getPersonEmail(closedPersIdPersonEmail);

			if (!ObjectUtils.isEmpty(closedPersEmailValueBean)) {
				// If forward person has a current primary email, mark it as non
				// primary
				if (!ObjectUtils.isEmpty(sfPersValeBean.getEmail().getForwardPerson().getPrimaryKeyData())) {
					int fwdPersIdPersonEmail = sfPersValeBean.getEmail().getForwardPerson().getPrimaryKeyData()
							.getIdKey();
					if (fwdPersIdPersonEmail > 0) {
						PersonEmailValueDto fwdPersEmailValueBean = personEmailDao.getPersonEmail(fwdPersIdPersonEmail);
						fwdPersEmailValueBean.setIndPrimary('N');
						fwdPersEmailValueBean
								.setLastUpdateIdPerson(persMergeVldValueBean.getIdPersonMergeWorker().intValue());
						personEmailDao.updatePersonEmailInfo(fwdPersEmailValueBean);
					}

				}
				// Add the new primary email for the person forward based on
				// closed
				// persons primary email
				// Before that we need to reset of the values

				PersonEmail closeEmail = new PersonEmail();
				closeEmail.setPerson((Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
						.add(Restrictions.eq("idPerson", persMergeVldValueBean.getIdForwardPerson())).uniqueResult());
				closeEmail.setCdType(closedPersEmailValueBean.getCdEmailType());
				closeEmail.setIndInvalid(closedPersEmailValueBean.getIndInvalid());
				closeEmail.setIndPrimary(closedPersEmailValueBean.getIndPrimary());
				closeEmail.setTxtEmail(closedPersEmailValueBean.getTxtEmail());
				closeEmail.setDtStart(closedPersEmailValueBean.getDtStart());
				closeEmail.setDtEnd(closedPersEmailValueBean.getDtEnd());
				closeEmail.setTxtComments(closedPersEmailValueBean.getTxtComments());
				closeEmail.setDtCreated(new Date());
				closeEmail.setIdCreatedPerson(persMergeVldValueBean.getIdPersonMergeWorker());
				closeEmail.setIdLastUpdatePerson(persMergeVldValueBean.getIdPersonMergeWorker());
				closeEmail.setDtLastUpdate(closedPersEmailValueBean.getLastUpdate());
				sessionFactory.getCurrentSession().save(closeEmail);

				// Record any email changes into person merge update log
				if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_320))
					persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_320, ServiceConstants.IND_TRUE);
			}
		}

	}

	/**
	 * Merge Email address history Add any Person Closed Email Address(es) not
	 * present for Person Forward. - In the event the Person Closed has a current
	 * Email Address of a Type not present for the Person Forward, add that Address
	 * to the Person Forward as a current email address for that type. - In the
	 * event the Person Closed has a current Email Address which is also a current
	 * Email Address of the same Type for the Person Forward, update the Start Date
	 * for the Person Forward if the Person Closed's Start Date is earlier.
	 * 
	 * @param Connection
	 * @param PersonMergeSplitDto
	 * @return
	 */
	private void mergePersonEmailHistory(PersonMergeSplitDto persMergeVldValueBean) {

		List<PersonEmail> fwdPersonEmailList = personEmailDao
				.getPersonEmailList(persMergeVldValueBean.getIdForwardPerson());
		List<PersonEmail> closedPersonEmailList = personEmailDao
				.getPersonEmailList(persMergeVldValueBean.getIdClosedPerson());

		for (PersonEmail closedPersEmailValueBean : closedPersonEmailList) {
			// ignore invalid email ids from closed person
			if (!ServiceConstants.Y.equals(closedPersEmailValueBean.getIndInvalid())) {
				boolean bMatchFound = false;
				PersonEmail fwdPersEmailValueBean = null;
				// if email match found in forward person emails
				Optional<PersonEmail> emailOptional = fwdPersonEmailList.stream()
						.filter(personEmail -> (checkForEquality(personEmail.getTxtEmail(),
								closedPersEmailValueBean.getTxtEmail())
								&& checkForEquality(personEmail.getCdType(), closedPersEmailValueBean.getCdType())))
						.findFirst();
				if (emailOptional.isPresent()) {
					fwdPersEmailValueBean = emailOptional.get();
					bMatchFound = true;
				}

				// if no match found for the email in forward person, add it on
				// forward person
				if (!bMatchFound) {
					Person person = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
							.add(Restrictions.eq("idPerson", persMergeVldValueBean.getIdForwardPerson()))
							.uniqueResult();
					PersonEmail personEmail = new PersonEmail();
					BeanUtils.copyProperties(closedPersEmailValueBean, personEmail);
					personEmail.setPerson(person);
					personEmail.setIdCreatedPerson(persMergeVldValueBean.getIdPersonMergeWorker());
					personEmail.setIdLastUpdatePerson(persMergeVldValueBean.getIdPersonMergeWorker());
					// In the event an added email is current and is marked
					// Primary,
					// set the Primary indicator to 'N'.
					if (personEmail.getIndPrimary() == 'Y' && ObjectUtils.isEmpty(personEmail.getDtEnd())) {
						personEmail.setIndPrimary('N');
					}
					personEmail.setIdPersonEmail(0L);
					personEmail.setDtLastUpdate(new Date());
					sessionFactory.getCurrentSession().save(personEmail);
					// Record any email changes into person merge update log
					if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_320))
						persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_320,
								ServiceConstants.IND_TRUE);

				} else
				// if match found then we need to check more things
				{
					// if person closed and fwd email addresses match and are
					// current

					if (!ObjectUtils.isEmpty(closedPersEmailValueBean.getDtStart())
							&& !ObjectUtils.isEmpty(fwdPersEmailValueBean.getDtStart())
							&& !ObjectUtils.isEmpty(closedPersEmailValueBean.getDtEnd())
							&& !ObjectUtils.isEmpty(fwdPersEmailValueBean.getDtEnd()) && DateUtils.isAfter(
									fwdPersEmailValueBean.getDtStart(), closedPersEmailValueBean.getDtStart())) {

						fwdPersEmailValueBean.setDtStart(closedPersEmailValueBean.getDtStart());
						fwdPersEmailValueBean.setIdLastUpdatePerson(persMergeVldValueBean.getIdPersonMergeWorker());
						fwdPersEmailValueBean.setDtLastUpdate(new Date());
						personEmailDao.updatePersonEmailInfo(fwdPersEmailValueBean);

						// Record any email changes into person merge update log
						if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_320))
							persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_320,
									ServiceConstants.IND_TRUE);

					}
				}
			}
		} // closed person email list for loop

	}

	/**
	 * Updates Person Forward Primary Phone Number in the event that Person Closed
	 * Primary Phone Number was selected.
	 * 
	 * @param Connection
	 * @param PersonMergeSplitDto
	 * @param sfPersValeBean      - Select person forward data
	 * @return id of the newly created primary phone
	 */
	private int mergePrimaryPhone(PersonMergeSplitDto persMergeVldValueBean,
			SelectForwardPersonValueBean sfPersValeBean) {
		// If Primary phone is selected from Closed person
		if (sfPersValeBean.getPhone().getClosedPerson().getIsSelected()) {
			// fetch the primary phone for the closed person and use this to
			// create new primary phone for the forward person
			int closedPersIdPersonPhone = sfPersValeBean.getPhone().getClosedPerson().getPrimaryKeyData().getIdKey();

			// get closed person primary phone
			PersonPhoneOutDto closedPersPhoneDB = personPhoneDao.getPersonPhoneById((long) closedPersIdPersonPhone);

			if (!ObjectUtils.isEmpty(closedPersPhoneDB)) {
				// If forward person has a current primary phone, end date this
				// phone
				if (!ObjectUtils.isEmpty(sfPersValeBean.getPhone().getForwardPerson().getPrimaryKeyData())) {
					int fwdPersIdPersonPhone = sfPersValeBean.getPhone().getForwardPerson().getPrimaryKeyData()
							.getIdKey();
					if (fwdPersIdPersonPhone > 0) {
						PersonPhoneOutDto fwdPersPhoneDB = personPhoneDao
								.getPersonPhoneById((long) fwdPersIdPersonPhone);
						PersonPhoneRetDto personPhoneRetDto = new PersonPhoneRetDto();
						PersonPhoneReq personPhoneReq = new PersonPhoneReq();
						personPhoneRetDto.setIdPersonPhone((long) fwdPersIdPersonPhone);
						personPhoneRetDto.setIndPersonPhoneInvalid(fwdPersPhoneDB.getIndPersonPhoneInvalid());
						personPhoneRetDto.setIndPersonPhonePrimary(fwdPersPhoneDB.getIndPersonPhonePrimary());
						personPhoneRetDto.setDtPersonPhoneEnd(new Date());
						personPhoneReq.setPersonPhoneRetDto(personPhoneRetDto);
						personPhoneDao.updatePersPhone(personPhoneReq);
					}
				}
				// Add the new primary phone for the person forward based on
				// closed persons primary phone
				Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonPhone.class)
						.add(Restrictions.eq("idPersonPhone", new Long(closedPersIdPersonPhone)));
				PersonPhone closePersonPhone = (PersonPhone) criteria.uniqueResult();
				PersonPhone personPhone = new PersonPhone();
				BeanUtils.copyProperties(closePersonPhone, personPhone);
				personPhone.setIdPersonPhone(0L);
				Person forwardPerson = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
						.add(Restrictions.eq("idPerson", persMergeVldValueBean.getIdForwardPerson())).uniqueResult();
				personPhone.setPerson(forwardPerson);
				personPhone.setDtPersonPhoneStart(new Date());
				Long idPhone = (Long) sessionFactory.getCurrentSession().save(personPhone);
				// Record any phone changes into person merge update log
				if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_330))
					persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_330, ServiceConstants.IND_TRUE);

				// return id of the newly created primary phone for forward
				// person
				return idPhone.intValue();
			}
		}

		return 0;
	}

	/**
	 * Add any Person Closed Phone Number(s) not present for Person Forward. For the
	 * Person Closed, get all Phone Numbers and compare with Forward Person For any
	 * Phone Numbers found that DO NOT match, add the corresponding record to Person
	 * Forward, setting all values based upon the Closed Person Value For any Phone
	 * Numbers found that DO match, update the start date to earliest of two
	 * 
	 * @param Connection
	 * @param personMergeSplitDto
	 * @param idNewPrimaryPhone   (Id of the new primary phone for fwd person)
	 * @return
	 */
	private void mergePersonPhoneHistory(PersonMergeSplitDto personMergeSplitDto, long idNewPrimaryPhone) {

		// get phone list for both the persons
		PhoneReq phoneReq = new PhoneReq();
		//
		phoneReq.setIdPerson(personMergeSplitDto.getIdPersonMerge());
		phoneReq.setIndValidOnly(personMergeSplitDto.getIndPersonMergeInvalid());
		//
		//
		List<PersonPhoneRetDto> fwdPersonPhoneList = personPhoneDao.getPersonPhoneDetailList(phoneReq);
		List<PersonPhoneRetDto> closedPersonPhoneList = personPhoneDao.getPersonPhoneDetailList(phoneReq);

		for (PersonPhoneRetDto closedPersPhoneValueBean : closedPersonPhoneList) {
			boolean bMatchFound = false;
			PersonPhoneRetDto fwdPersPhoneValueBean = null;
			// if phone match found in forward person phone
			Optional<PersonPhoneRetDto> phoneOptional = fwdPersonPhoneList.stream()
					.filter(fwdPersonPhoneDto -> (checkForEquality(fwdPersonPhoneDto.getPersonPhone(),
							closedPersPhoneValueBean.getPersonPhone())
							&& checkForEquality(fwdPersonPhoneDto.getPhoneTypeDesc(),
									closedPersPhoneValueBean.getPhoneTypeDesc())))
					.findFirst();
			if (phoneOptional.isPresent()) {
				fwdPersPhoneValueBean = phoneOptional.get();
				bMatchFound = true;
			}

			// if no match found for the phone in forward person, add it on
			// forward person
			if (!bMatchFound) {
				// closedPersPhoneValueBean.setIdPersonPhone((long)
				// personMergeSplitDto.getIdForwardPerson());
				// In the event an added Phone Number is current and is marked
				// Primary, set the Primary indicator to 'N'.
				Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonPhone.class)
						.add(Restrictions.eq("idPersonPhone", closedPersPhoneValueBean.getIdPersonPhone()));
				PersonPhone closePersonPhone = (PersonPhone) criteria.uniqueResult();
				PersonPhone personPhone = new PersonPhone();
				BeanUtils.copyProperties(closePersonPhone, personPhone);
				personPhone.setIdPersonPhone(0L);
				Person forwardPerson = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
						.add(Restrictions.eq("idPerson", personMergeSplitDto.getIdForwardPerson())).uniqueResult();
				personPhone.setPerson(forwardPerson);
				if (ServiceConstants.Y.equals(closedPersPhoneValueBean.getIndPersonPhonePrimary()) && DateUtils
						.daysDifference(closedPersPhoneValueBean.getDtPersonPhoneEnd(), MAX_JAVA_DATE) == 0) {
					personPhone.setIndPersonPhonePrimary(ServiceConstants.N);
				}
				sessionFactory.getCurrentSession().save(personPhone);

				// Record any phone changes into person merge update log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_330))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_330, ServiceConstants.IND_TRUE);

			} else
			// if match found then we need to check more things
			{
				// if person closed and fwd matched phones are current and it is
				// not the newly create primary phone which we might
				// created during primary phone merge in previous step

				if (fwdPersPhoneValueBean.getIdPersonPhone().longValue() != idNewPrimaryPhone
						&& !ObjectUtils.isEmpty(closedPersPhoneValueBean.getDtPersonPhoneStart())
						&& !ObjectUtils.isEmpty(fwdPersPhoneValueBean.getDtPersonPhoneStart())
						&& !ObjectUtils.isEmpty(closedPersPhoneValueBean.getDtPersonPhoneEnd())
						&& !ObjectUtils.isEmpty(fwdPersPhoneValueBean.getDtPersonPhoneEnd())
						&& DateUtils.daysDifference(closedPersPhoneValueBean.getDtPersonPhoneEnd(), MAX_JAVA_DATE) == 0
						&& DateUtils.daysDifference(fwdPersPhoneValueBean.getDtPersonPhoneEnd(), MAX_JAVA_DATE) == 0) {
					// if person forward phone start date is after person closed
					// phone start date

					if (DateUtils.isAfter(fwdPersPhoneValueBean.getDtPersonPhoneStart(),
							closedPersPhoneValueBean.getDtPersonPhoneStart())) {
						Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonPhone.class)
								.add(Restrictions.eq("idPersonPhone", fwdPersPhoneValueBean.getIdPersonPhone()));
						PersonPhone fwdPersonPhone = (PersonPhone) criteria.uniqueResult();
						fwdPersonPhone.setDtPersonPhoneStart(closedPersPhoneValueBean.getDtPersonPhoneStart());
						sessionFactory.getCurrentSession().saveOrUpdate(fwdPersonPhone);
						// Record any phone changes into person merge update log
						if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_330))
							personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_330,
									ServiceConstants.IND_TRUE);

					}
				}
			}
		} // closed person phone list for loop
	}

	/**
	 * Update Person Forward Gender in the event that Person Closed Gender was
	 * selected in Select Person Forward page
	 * 
	 * @param sfPersValeBean        (SelectForwardPersonValueBean)
	 * @param PersonMergeSplitDto   (personMergeSplitDto)
	 * @param fwdPersonValueBean    (Forward Person Data)
	 * @param closedPersonValueBean (Closed Person Data)
	 * 
	 * @return
	 */
	private void mergePersonGender(SelectForwardPersonValueBean sfPersValeBean,
			PersonMergeSplitDto persMergeVldValueBean, PersonDto fwdPersonValueBean, PersonDto closedPersonValueBean) {
		// if user has selected the gender from closed person
		if (!ObjectUtils.isEmpty(sfPersValeBean.getGender().getClosedPerson())
				&& sfPersValeBean.getGender().getClosedPerson().getIsSelected()) {
			// retain the value of gender from closed person
			fwdPersonValueBean.setCdPersonSex(closedPersonValueBean.getCdPersonSex());

			// Record the Gender change for Person Merge Update Log
			if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_110))
				persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_110, ServiceConstants.IND_TRUE);
		}
	}

	/**
	 * Update Person Forward Marital Status in the event that Person Closed Marital
	 * Status was selected in Select Person Forward page
	 * 
	 * @param sfPersValeBean        (SelectForwardPersonValueBean)
	 * @param persMergeVldValueBean (personMergeSplitDto)
	 * @param fwdPersonValueBean    (Forward Person Data)
	 * @param closedPersonValueBean (Closed Person Data)
	 * 
	 * @return
	 */
	private void mergePersonMaritalStatus(SelectForwardPersonValueBean sfPersValeBean,
			PersonMergeSplitDto persMergeVldValueBean, PersonDto fwdPersonValueBean, PersonDto closedPersonValueBean) {
		// if user has selected the marital status from closed person
		if (!ObjectUtils.isEmpty(sfPersValeBean.getMartialStatus().getClosedPerson())
				&& sfPersValeBean.getMartialStatus().getClosedPerson().getIsSelected()) {
			// retain the value of marital status from closed person
			fwdPersonValueBean.setCdPersonMaritalStatus(closedPersonValueBean.getCdPersonMaritalStatus());

			// Record the Marital status change for Person Merge Update Log
			if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_140))
				persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_140, ServiceConstants.IND_TRUE);

		}
	}

	/**
	 * Update Person Forward DOB in the event that Person Closed DOB was selected in
	 * Select Person Forward page. Also update Person DOD in the event that Person
	 * Closed DOD was selected in Select Person Forward page
	 * 
	 * @param sfPersValeBean        (SelectForwardPersonValueBean)
	 * @param PersonMergeSplitDto   (personMergeSplitDto)
	 * @param fwdPersonValueBean    (Forward Person Data)
	 * @param closedPersonValueBean (Closed Person Data)
	 * 
	 * @return
	 */

	private void mergePersonDOBAndDOD(SelectForwardPersonValueBean sfPersValeBean,
			PersonMergeSplitDto persMergeVldValueBean, PersonDto fwdPersonValueBean, PersonDto closedPersonValueBean) {
		// if user has selected the DOB from closed person
		if (!ObjectUtils.isEmpty(sfPersValeBean.getDOB().getClosedPerson())
				&& sfPersValeBean.getDOB().getClosedPerson().getIsSelected()) {
			// retain the value of DOB from closed person
			if (!ObjectUtils.isEmpty(closedPersonValueBean.getDtPersonBirth())) {
				fwdPersonValueBean.setDtPersonBirth(closedPersonValueBean.getDtPersonBirth());

				// Record the DOB change for Person Merge Update Log
				if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_150))
					persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_150, ServiceConstants.IND_TRUE);

			}

			// check if there is change in Approx indicator
			if (((ObjectUtils.isEmpty(closedPersonValueBean.getIndPersonDobApprox())
					|| !Boolean.getBoolean(closedPersonValueBean.getIndPersonDobApprox()))
					&& (!ObjectUtils.isEmpty(fwdPersonValueBean.getIndPersonDobApprox())
							&& Boolean.getBoolean(fwdPersonValueBean.getIndPersonDobApprox())))
					|| ((ObjectUtils.isEmpty(fwdPersonValueBean.getIndPersonDobApprox())
							|| !Boolean.getBoolean(fwdPersonValueBean.getIndPersonDobApprox()))
							&& (!ObjectUtils.isEmpty(closedPersonValueBean.getIndPersonDobApprox())
									&& Boolean.getBoolean(closedPersonValueBean.getIndPersonDobApprox())))) {
				fwdPersonValueBean.setIndPersonDobApprox(closedPersonValueBean.getIndPersonDobApprox());
				if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_170))
					persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_170, ServiceConstants.IND_TRUE);
			}

		}

		// if user has selected the DOD from closed person
		if ((!ObjectUtils.isEmpty(sfPersValeBean.getDOD().getClosedPerson())
				&& sfPersValeBean.getDOD().getClosedPerson().getIsSelected())
				&& (!ObjectUtils.isEmpty(closedPersonValueBean.getDtPersonDeath()))) {
			// retain the value of DOD from closed person
			fwdPersonValueBean.setDtPersonDeath(closedPersonValueBean.getDtPersonDeath());

			// also update reason for death (NON CPS)
			if (!ObjectUtils.isEmpty(closedPersonValueBean.getCdPersonDeath()))
				fwdPersonValueBean.setCdPersonDeath(closedPersonValueBean.getCdPersonDeath());

			// also update reason for death (CPS)
			if (!ObjectUtils.isEmpty(closedPersonValueBean.getCdDeathRsnCps()))
				fwdPersonValueBean.setCdDeathRsnCps(closedPersonValueBean.getCdDeathRsnCps());

			// Record the DOD for Person Merge Update Log
			if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_240))
				persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_240, ServiceConstants.IND_TRUE);

			// When DOD is updated, end date any open eligibilities for the
			// forward person (if any)
			endEligibilitiesForDateOfDeath(persMergeVldValueBean, closedPersonValueBean.getDtPersonDeath());
		}

		// compute the age of the person irrespective of the selection from
		// closed person
		// if date of death is specified
		int currentAge = 0;
		if (!ObjectUtils.isEmpty(fwdPersonValueBean.getDtPersonBirth())) {
			if (!ObjectUtils.isEmpty(fwdPersonValueBean.getDtPersonDeath())) {
				currentAge = DateUtils.getAge(fwdPersonValueBean.getDtPersonBirth(),
						fwdPersonValueBean.getDtPersonDeath());
			} else
				currentAge = DateUtils.getAge(fwdPersonValueBean.getDtPersonBirth());
		}
		// if newly computed age for forward is different for present age, then
		// record this change
		if (currentAge != 0) {
			fwdPersonValueBean.setNbrPersonAge((short) currentAge);
			// Record the Age change for Person Merge Update Log
			if (!persMergeVldValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_160))
				persMergeVldValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_160, ServiceConstants.IND_TRUE);
		}

	}

	/**
	 * Update Person Forward Language in the event that Person Closed Language was
	 * selected in Select Person Forward page.
	 * 
	 * @param sfPersValeBean        (SelectForwardPersonValueBean)
	 * @param personMergeSplitDto   (personMergeSplitDto)
	 * @param fwdPersonValueBean    (Forward Person Data)
	 * @param closedPersonValueBean (Closed Person Data)
	 * 
	 * @return
	 */

	private void mergePersonLanguage(SelectForwardPersonValueBean sfPersValeBean,
			PersonMergeSplitDto personMergeSplitDto, PersonDto fwdPersonValueBean, PersonDto closedPersonValueBean) {
		// if user has selected the Language from closed person
		if ((!ObjectUtils.isEmpty(sfPersValeBean.getLanguage().getClosedPerson())
				&& sfPersValeBean.getLanguage().getClosedPerson().getIsSelected())
				&& (isValid(closedPersonValueBean.getCdPersonLanguage()))) {
			// retain the value of Language code from closed person
			fwdPersonValueBean.setCdPersonLanguage(closedPersonValueBean.getCdPersonLanguage());
			// Record change in language info into Person Merge Update Log
			if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_200))
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_200, ServiceConstants.IND_TRUE);

		}
	}

	/**
	 * Update Person Forward Living Arrangement in the event that Person Closed
	 * Living Arrangement was selected in Select Person Forward page.
	 * 
	 * @param sfPersValeBean        (SelectForwardPersonValueBean)
	 * @param personMergeSplitDto   (personMergeSplitDto)
	 * @param fwdPersonValueBean    (Forward Person Data)
	 * @param closedPersonValueBean (Closed Person Data)
	 * 
	 * @return
	 */

	private void mergePersonLivingArrangement(SelectForwardPersonValueBean sfPersValeBean,
			PersonMergeSplitDto personMergeSplitDto, PersonDto fwdPersonValueBean, PersonDto closedPersonValueBean) {
		// if user has selected the Living Arrangement from closed person
		if ((!ObjectUtils.isEmpty(sfPersValeBean.getLivArrang().getClosedPerson())
				&& sfPersValeBean.getLivArrang().getClosedPerson().getIsSelected())
				&& (isValid(closedPersonValueBean.getCdPersonLivArr()))) {
			// retain the value of Living Arrangement code from closed person
			fwdPersonValueBean.setCdPersonLivArr(closedPersonValueBean.getCdPersonLivArr());

			// Record change in living arrangement info into Person Merge
			// Update Log
			if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_210))
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_210, ServiceConstants.IND_TRUE);

		}
	}

	/**
	 * Update Person Forward Occupation in the event that Person Closed Occupation
	 * was selected in Select Person Forward page.
	 * 
	 * @param sfPersValeBean        (SelectForwardPersonValueBean)
	 * @param personMergeSplitDto   (personMergeSplitDto)
	 * @param fwdPersonValueBean    (Forward Person Data)
	 * @param closedPersonValueBean (Closed Person Data)
	 * 
	 * @return
	 */

	private void mergePersonOccupation(SelectForwardPersonValueBean sfPersValeBean,
			PersonMergeSplitDto personMergeSplitDto, PersonDto fwdPersonValueBean, PersonDto closedPersonValueBean) {
		// if user has selected the Occupation from closed person
		if ((!ObjectUtils.isEmpty(sfPersValeBean.getOccupation().getClosedPerson())
				&& sfPersValeBean.getOccupation().getClosedPerson().getIsSelected())
				&& (isValid(closedPersonValueBean.getTxtPersonOccupation()))) {
			// retain the value of Occupation code from closed person
			fwdPersonValueBean.setTxtPersonOccupation(closedPersonValueBean.getTxtPersonOccupation());
			fwdPersonValueBean.setCdOccupation(closedPersonValueBean.getCdOccupation());
			// Record change in occupation into Person Merge Update Log
			if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_220))
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_220, ServiceConstants.IND_TRUE);

		}
	}

	/**
	 * Update Person Forward Religion in the event that Person Closed Religion was
	 * selected in Select Person Forward page.
	 * 
	 * @param sfPersValeBean        (SelectForwardPersonValueBean)
	 * @param personMergeSplitDto   (personMergeSplitDto)
	 * @param fwdPersonValueBean    (Forward Person Data)
	 * @param closedPersonValueBean (Closed Person Data)
	 * 
	 * @return
	 */

	private void mergePersonReligion(SelectForwardPersonValueBean sfPersValeBean,
			PersonMergeSplitDto personMergeSplitDto, PersonDto fwdPersonValueBean, PersonDto closedPersonValueBean) {
		// if user has selected the Religion from closed person
		if ((!ObjectUtils.isEmpty(sfPersValeBean.getReligion().getClosedPerson())
				&& sfPersValeBean.getReligion().getClosedPerson().getIsSelected())
				&& (isValid(closedPersonValueBean.getCdPersonReligion()))) {
			// retain the value of Religion code from closed person
			fwdPersonValueBean.setCdPersonReligion(closedPersonValueBean.getCdPersonReligion());
			// Record change in Religion into Person Merge Update Log
			if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_230))
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_230, ServiceConstants.IND_TRUE);
		}
	}

	/**
	 * This function checks if user has selected Race from closed Person then it
	 * deletes the Race list from forward person and adds copies over the race list
	 * from closed person. This function performs same function for person ethnicity
	 * 
	 * @param Connection
	 * @param personMergeSplitDto
	 * @param sfPersValeBean      - Select person forward data
	 * @param fwdPersonValueBean  ( Forward Person data)
	 * @return
	 */

	private void mergePersonRaceEthnicity(PersonMergeSplitDto personMergeSplitDto,
			SelectForwardPersonValueBean sfPersValueBean, PersonDto fwdPersonValueBean) {

		boolean bRaceChanged = false;
		boolean bEthnicityChanged = false;

		// check if user selected Race value from closed person
		if (!ObjectUtils.isEmpty(sfPersValueBean.getRace().getClosedPerson())
				&& sfPersValueBean.getRace().getClosedPerson().getIsSelected()) {
			List<PersonRaceDto> closedPersRaceList = personRaceDao
					.getPersonRaceByPersonId(personMergeSplitDto.getIdClosedPerson());
			// if closed person has Race values defined
			if (CollectionUtils.isNotEmpty(closedPersRaceList)) {
				// delete the existing Race list for person forward
				if (!ObjectUtils.isEmpty(sfPersValueBean.getRace().getForwardPerson()) && CollectionUtils
						.isNotEmpty(sfPersValueBean.getRace().getForwardPerson().getPrimaryKeyDataList())) {
					List<PrimaryKeyDataValueBean> persFwdRaceIdList = sfPersValueBean.getRace().getForwardPerson()
							.getPrimaryKeyDataList();
					persFwdRaceIdList.stream().forEach(primaryKeyDataValueBean -> {
						PersonRace tmpRace = (PersonRace) sessionFactory.getCurrentSession().get(PersonRace.class,
								(long) primaryKeyDataValueBean.getIdKey());
						personRaceDao.deletePersonRace(tmpRace);
					});
				}

				closedPersRaceList.stream().forEach(raceValueBean -> {
					PersonRace personRace = (PersonRace) sessionFactory.getCurrentSession()
							.createCriteria(PersonRace.class)
							.add(Restrictions.eq("idPersonRace", raceValueBean.getIdPersonRace())).uniqueResult();
					Person forwardPerson = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
							.add(Restrictions.eq("idPerson", personMergeSplitDto.getIdForwardPerson())).uniqueResult();
					PersonRace newPersonRace = new PersonRace();
					BeanUtils.copyProperties(personRace, newPersonRace);
					newPersonRace.setIdPersonRace(ServiceConstants.ZERO);
					newPersonRace.setPerson(forwardPerson);
					personRaceDao.savePersonRace(newPersonRace);
				});

				bRaceChanged = true;

				// Record the Race change for Person Merge Update Log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_120))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_120, ServiceConstants.IND_TRUE);
			}
		} // end of If for race

		// check if user selected Ethnicity value from closed person
		if (!ObjectUtils.isEmpty(sfPersValueBean.getEthnicity().getClosedPerson())
				&& sfPersValueBean.getEthnicity().getClosedPerson().getIsSelected()) {
			List<PersonEthnicityDto> closedPersEthList = personEthnicityDao
					.getPersonEthnicityByPersonId(personMergeSplitDto.getIdClosedPerson());

			// if closed person has Ethnicity values defined
			if (CollectionUtils.isNotEmpty(closedPersEthList)) {
				// delete the existing Ethnicity list for person forward
				if (!ObjectUtils.isEmpty(sfPersValueBean.getEthnicity()) && !ObjectUtils
						.isEmpty(sfPersValueBean.getEthnicity().getForwardPerson().getPrimaryKeyDataList())) {
					List<PrimaryKeyDataValueBean> persFwdEthIdList = sfPersValueBean.getEthnicity().getForwardPerson()
							.getPrimaryKeyDataList();
					persFwdEthIdList.stream().forEach(primaryKeyDataValueBean -> {
						// delete the existing ethnicity list for person forward
						PersonEthnicity tmpEth = (PersonEthnicity) sessionFactory.getCurrentSession()
								.get(PersonEthnicity.class, (long) primaryKeyDataValueBean.getIdKey());
						personEthnicityDao.deletePersonEthnicity(tmpEth);
					});
				}
				// copy over the ethnicity values from closed person to forward
				// person
				List<PrimaryKeyDataValueBean> persClosedEthIdList = sfPersValueBean.getEthnicity().getClosedPerson()
						.getPrimaryKeyDataList();
				persClosedEthIdList.stream().forEach(primaryKeyDataValueBean -> {
					// delete the existing ethnicity list for person forward
					PersonEthnicity closedEth = (PersonEthnicity) sessionFactory.getCurrentSession()
							.get(PersonEthnicity.class, (long) primaryKeyDataValueBean.getIdKey());
					Person person = new Person();
					person.setIdPerson(personMergeSplitDto.getIdForwardPerson());
					closedEth.setPerson(person);
					personEthnicityDao.savePersonEthnicity(closedEth);
				});
				bEthnicityChanged = true;

				// Record the Race change for Person Merge Update Log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_130))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_130, ServiceConstants.IND_TRUE);

			}
		} // end of If for ethnicity

		// Compute PERSON.CD_PERSON_ETHNIC_GROUP value again based on new values
		// of
		// Race and Ethnicity
		if (bRaceChanged || bEthnicityChanged) {
			List<PersonRaceDto> fwdPersRaceList = personRaceDao
					.getPersonRaceByPersonId(personMergeSplitDto.getIdForwardPerson());
			List<PersonEthnicityDto> fwdPersEthList = personEthnicityDao
					.getPersonEthnicityByPersonId(personMergeSplitDto.getIdForwardPerson());
			String cdEthnicGroup = null;
			if (CollectionUtils.isNotEmpty(fwdPersEthList)) {
				String cdEthnicity = fwdPersEthList.get(0).getCdPersonEthnicity();
				cdEthnicGroup = getRaceEthnicityGroup(cdEthnicity, fwdPersRaceList);
			}
			// if computed ethnic group is not null and it is different from
			// existing ethnic group
			// for the forward person, then apply new ethnic group to forward
			// person
			if (isValid(cdEthnicGroup) && !cdEthnicGroup.equals(fwdPersonValueBean.getCdPersonEthnicGroup())) {
				fwdPersonValueBean.setCdPersonEthnicGroup(cdEthnicGroup);
			}
		}

	}

	/**
	 * Update Person forward Citizenship Status if user has selected Citizenship
	 * status from closed person in Select Person Forward page.
	 * 
	 * @param sfPersValeBean           (SelectForwardPersonValueBean)
	 * @param personMergeSplitDto      (personMergeSplitDto)
	 * @param fwdPersonDtlValueBean    (Forward Person Detail Data)
	 * @param closedPersonDtlValueBean (Closed Person Detail Data)
	 * 
	 * @return
	 */
	private void mergePersonCitizenshipStatus(SelectForwardPersonValueBean sfPersValeBean,
			PersonMergeSplitDto personMergeSplitDto, CvsFaHomeValueBean fwdPersonDtlValueBean,
			CvsFaHomeValueBean closedPersonDtlValueBean) {
		// if user has selected the citizenship status from closed person
		if ((!ObjectUtils.isEmpty(sfPersValeBean.getCitizenStat().getClosedPerson())
				&& sfPersValeBean.getCitizenStat().getClosedPerson().getIsSelected())
				&& ((!ObjectUtils.isEmpty(closedPersonDtlValueBean.getCdPersonBirthCitizenship())))) {
			fwdPersonDtlValueBean.setCdPersonBirthCitizenship(closedPersonDtlValueBean.getCdPersonBirthCitizenship());
			// Record change in citizenship status in Person Merge Update
			// Log
			if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_180))
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_180, ServiceConstants.IND_TRUE);
		}
	}

	/**
	 * Update following data elements which are missing on forward person - Update
	 * Person Forward Birth Information in the event that Person Closed values exist
	 * but do nhot exist for Person Forward. - Update Person Forward Prior Adoption
	 * Information in the event that Person Closed values exist but do not exist for
	 * Person Forward
	 * 
	 * @param SelectForwardPersonValueBean sfPersValueBean
	 * @param personMergeSplitDto
	 * @param CvsFaHomeValueBean           fwdPersonDtlValueBean
	 * @param CvsFaHomeValueBean           closedPersonDtlValueBean
	 * 
	 * @return
	 */
	private void mergePersonDtlMiscInformation(PersonMergeSplitDto personMergeSplitDto,
			CvsFaHomeValueBean fwdPersonDtlValueBean, CvsFaHomeValueBean closedPersonDtlValueBean) {
		boolean bAnyUpdate = false;

		// check for PERSON_DTL.CD_PERSON_BIRTH_STATE
		if (!isValid(fwdPersonDtlValueBean.getCdPersonBirthState())
				&& isValid(closedPersonDtlValueBean.getCdPersonBirthState())) {
			fwdPersonDtlValueBean.setCdPersonBirthState(closedPersonDtlValueBean.getCdPersonBirthState());
			bAnyUpdate = true;
		}
		// check for PERSON_DTL.CD_PERSON_BIRTH_COUNTRY
		if (!isValid(fwdPersonDtlValueBean.getCdPersonBirthCountry())
				&& isValid(closedPersonDtlValueBean.getCdPersonBirthCountry())) {
			fwdPersonDtlValueBean.setCdPersonBirthCountry(closedPersonDtlValueBean.getCdPersonBirthCountry());
			bAnyUpdate = true;
		}
		// check for PERSON_DTL.CD_PERSON_BIRTH_CITY
		if (!isValid(fwdPersonDtlValueBean.getCdPersonBirthCity())
				&& isValid(closedPersonDtlValueBean.getCdPersonBirthCity())) {
			fwdPersonDtlValueBean.setCdPersonBirthCity(closedPersonDtlValueBean.getCdPersonBirthCity());
			bAnyUpdate = true;
		}
		// check for PERSON_DTL.IND_PERSON_NO_US_BRN
		if (!isValid(fwdPersonDtlValueBean.getIndPersonNoUsBrn())
				&& isValid(closedPersonDtlValueBean.getIndPersonNoUsBrn())) {
			fwdPersonDtlValueBean.setIndPersonNoUsBrn(closedPersonDtlValueBean.getIndPersonNoUsBrn());
			bAnyUpdate = true;
		}
		// check for PERSON_DTL.CD_PERSON_BIRTH_COUNTY
		if (!isValid(fwdPersonDtlValueBean.getCdPersonBirthCounty())
				&& isValid(closedPersonDtlValueBean.getCdPersonBirthCounty())) {
			fwdPersonDtlValueBean.setCdPersonBirthCounty(closedPersonDtlValueBean.getCdPersonBirthCounty());
			bAnyUpdate = true;
		}
		// check for PERSON_DTL.CD_REMOVAL_MOTHR_MARRD
		if (!isValid(fwdPersonDtlValueBean.getCdRemovalMothrMarrd())
				&& isValid(closedPersonDtlValueBean.getCdRemovalMothrMarrd())) {
			fwdPersonDtlValueBean.setCdRemovalMothrMarrd(closedPersonDtlValueBean.getCdRemovalMothrMarrd());
			bAnyUpdate = true;
		}

		// Update prior adoption information
		// check for PERSON_DTL.CD_EVER_ADOPTED (Previously Adopted)
		if (!isValid(fwdPersonDtlValueBean.getCdEverAdopted())
				&& isValid(closedPersonDtlValueBean.getCdEverAdopted())) {
			fwdPersonDtlValueBean.setCdEverAdopted(closedPersonDtlValueBean.getCdEverAdopted());
			bAnyUpdate = true;
		}
		// PERSON_DTL.DT_MOST_RECENT_ADOPTION (Actual/Estimated Date)
		if (ObjectUtils.isEmpty(fwdPersonDtlValueBean.getDtMostRecentAdoption())
				&& !ObjectUtils.isEmpty(closedPersonDtlValueBean.getDtMostRecentAdoption())) {
			fwdPersonDtlValueBean.setDtMostRecentAdoption(closedPersonDtlValueBean.getDtMostRecentAdoption());
			bAnyUpdate = true;
		}
		// PERSON_DTL.IND_ADOPT_DATE_UNKNOWN (Previous Date Unknown)
		if (!isValid(fwdPersonDtlValueBean.getIndAdoptDateUnknown())
				&& isValid(closedPersonDtlValueBean.getIndAdoptDateUnknown())) {
			fwdPersonDtlValueBean.setIndAdoptDateUnknown(closedPersonDtlValueBean.getIndAdoptDateUnknown());
			bAnyUpdate = true;
		}

		// PERSON_DTL.CD_AGENCY_ADOPTION (Type of Adoption Agency)
		if (!isValid(fwdPersonDtlValueBean.getCdAgencyAdoption())
				&& isValid(closedPersonDtlValueBean.getCdAgencyAdoption())) {
			fwdPersonDtlValueBean.setCdAgencyAdoption(closedPersonDtlValueBean.getCdAgencyAdoption());
			bAnyUpdate = true;
		}

		// PERSON_DTL.CD_EVER_ADOPT_INTERNATL (Prior International Adoption)
		if (!isValid(fwdPersonDtlValueBean.getCdEverAdoptInternational())
				&& isValid(closedPersonDtlValueBean.getCdEverAdoptInternational())) {
			fwdPersonDtlValueBean.setCdEverAdoptInternational(closedPersonDtlValueBean.getCdEverAdoptInternational());
			bAnyUpdate = true;
		}

		// Record change in Person Dtl (CVS) info into Person Merge Update Log
		if (bAnyUpdate && !personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_190))
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_190, ServiceConstants.IND_TRUE);

	}

	/**
	 * Update Person Forward current Income and Resources in the event that Person
	 * Closed current Income and Resources was selected. Also maintain current
	 * IMPACT business rule that a Person may only have one current Income and
	 * Resource record for any given type and that there may not be any date overlap
	 * for any type
	 * 
	 * @param Connection
	 * @param personMergeSplitDto
	 * @param sfPersValeBean      - Select person forward data
	 * @return
	 */

	private void mergeIncomeResources(PersonMergeSplitDto personMergeSplitDto,
			SelectForwardPersonValueBean sfPersValueBean) {

		// if income and resources are selected from closed person,
		if (!ObjectUtils.isEmpty(sfPersValueBean.getCurrIncmRsrc().getClosedPerson())
				&& sfPersValueBean.getCurrIncmRsrc().getClosedPerson().getIsSelected()) {
			if (!ObjectUtils.isEmpty(sfPersValueBean.getCurrIncmRsrc().getForwardPerson()) && !ObjectUtils
					.isEmpty(sfPersValueBean.getCurrIncmRsrc().getForwardPerson().getPrimaryKeyDataList())) {
				// get list of all current income and resources for forward
				// person which were present
				// at the time Select Forward Person page was loaded
				ArrayList<PrimaryKeyDataValueBean> fwdPersonCurrIncmRsrcList = sfPersValueBean.getCurrIncmRsrc()
						.getForwardPerson().getPrimaryKeyDataList();

				// set the end date on the current income and resources for
				// forward person
				fwdPersonCurrIncmRsrcList.stream().forEach(pkData -> {
					IncomeAndResources incResValueBean = incResDao.getIncomeAndResource(pkData.getIdKey());
					if (ObjectUtils.isEmpty(incResValueBean.getDtIncRsrcTo())
							|| DateUtils.daysDifference(incResValueBean.getDtIncRsrcTo(), MAX_JAVA_DATE) == 0) {
						incResValueBean.setDtLastUpdate(pkData.getDtLastUpdate());
						incResValueBean.setDtIncRsrcTo(new Date());
						incResValueBean.setDtLastUpdate(new Date());
						//  PD 90488 - Added Update Person Id as requested
						incResValueBean.setIdLastUpdatePerson(personMergeSplitDto.getIdPersonMergeWorker());
						incResDao.updateIncomeAndResource(incResValueBean);

					}
				});
			}

			// get the current income and resources for closed person,
			// add the record to the Person Forward with DT_INC_RSRC_FROM =
			// System Date
			ArrayList<PrimaryKeyDataValueBean> closedPersonCurrIncmRsrcList = sfPersValueBean.getCurrIncmRsrc()
					.getClosedPerson().getPrimaryKeyDataList();
			closedPersonCurrIncmRsrcList.stream().forEach(pkData -> {
				IncomeAndResources incResValueBean = incResDao.getIncomeAndResource(pkData.getIdKey());
				if (ObjectUtils.isEmpty(incResValueBean.getDtIncRsrcTo())
						|| DateUtils.daysDifference(incResValueBean.getDtIncRsrcTo(), MAX_JAVA_DATE) == 0) {
					IncomeAndResources incomeAndResources = new IncomeAndResources();
					BeanUtils.copyProperties(incResValueBean, incomeAndResources);
					incomeAndResources.setDtIncRsrcFrom(new Date());
					Person person = new Person();
					person.setIdPerson(personMergeSplitDto.getIdForwardPerson());
					incomeAndResources.setPersonByIdPerson(person);
					incomeAndResources.setIdIncRsrc(0l);
					incomeAndResources.setDtLastUpdate(new Date());
					//  PD 90488 - Added Update Person Id as requested
					incResValueBean.setIdCreatedPerson(personMergeSplitDto.getIdPersonMergeWorker());
					incResValueBean.setIdLastUpdatePerson(personMergeSplitDto.getIdPersonMergeWorker());
					incResValueBean.setDtCreated(new Date());
					incResDao.saveIncomeAndResource(incomeAndResources);
				}
			});
			// Record any IncomeResource update into person merge update log
			if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_370))
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_370, ServiceConstants.IND_TRUE);
		}
	}

	/**
	 * Add any non-current Person Closed Income and Resource records not present for
	 * Person Forward. In the event Person Closed has current Income and resource
	 * records that were not selected and do not match Person Forward, end date them
	 * when adding to Person Forward.
	 * 
	 * @param Connection
	 * @param personMergeSplitDto - Select person forward data
	 * @return
	 */
	private void mergeIncomeResourcesHistory(PersonMergeSplitDto personMergeSplitDto) {

		// get all income and resources for forward and closed persons
		// (including non current ones too)
		ArrayList<IncomeAndResourceDto> fwdPerCurrIncmRsrcList = incResDao
				.getIncomeAndResourceList(personMergeSplitDto.getIdForwardPerson(), null, false);
		ArrayList<IncomeAndResourceDto> closedPerCurrIncmRsrcList = incResDao
				.getIncomeAndResourceList(personMergeSplitDto.getIdClosedPerson(), null, false);

		boolean anyIncRes = false;

		// for each Income and Resource from closed person
		for (IncomeAndResourceDto closedIncResValueBean : closedPerCurrIncmRsrcList) {
			// check if this income and resource exists for forward person
			boolean bMatchFound = false;
			// match on CD_INC_RSRC_TYPE, AMT_INC_RSRC, CD_INC_RSRC_INCOME
			if (fwdPerCurrIncmRsrcList.stream()
					.anyMatch(fwdIncResValueBean -> (checkForEquality(closedIncResValueBean.getType(),
							fwdIncResValueBean.getType())
							&& closedIncResValueBean.getAmount() == fwdIncResValueBean.getAmount()
							&& checkForEquality(closedIncResValueBean.getIncomeOrResource(),
									fwdIncResValueBean.getIncomeOrResource()))))
				bMatchFound = true;
			// if income and resource record does not match with any of forward
			// person's data, add it to forward person
			if (!bMatchFound) {
				// if the DtIncRsrcTo is non end dated, then set the end date to
				// system
				// date before adding to forward person.

				IncomeAndResources incResValueBean = incResDao
						.getIncomeAndResource(closedIncResValueBean.getIdIncRsrc().intValue());

				if (ObjectUtils.isEmpty(incResValueBean.getDtIncRsrcTo())
						|| DateUtils.daysDifference(incResValueBean.getDtIncRsrcTo(), MAX_JAVA_DATE) == 0) {
					IncomeAndResources incomeAndResources = new IncomeAndResources();
					BeanUtils.copyProperties(incResValueBean, incomeAndResources);
					incomeAndResources.setDtIncRsrcFrom(new Date());
					Person person = new Person();
					person.setIdPerson(personMergeSplitDto.getIdForwardPerson());
					incomeAndResources.setPersonByIdPerson(person);
					incomeAndResources.setIdIncRsrc(0l);
					incomeAndResources.setDtIncRsrcTo(new Date());
					incomeAndResources.setDtLastUpdate(new Date());
					//  PD 90488 - Added Update Person Id as requested
					incomeAndResources.setIdCreatedPerson(personMergeSplitDto.getIdPersonMergeWorker());
					incomeAndResources.setIdLastUpdatePerson(personMergeSplitDto.getIdPersonMergeWorker());

					incResDao.saveIncomeAndResource(incResValueBean);
				}
				anyIncRes = true;
			}
		}

		// Record any IncomeResource update into person merge update log
		if (anyIncRes && !personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_370)) {
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_370, ServiceConstants.IND_TRUE);
		}

	}

	/**
	 * Add any Closed Person Relationship(s) not present for Person Forward. - In
	 * the event the Person Closed has a Family Tree Relationship not present for
	 * the Person Forward, add that Relationship to the Person Forward. - In the
	 * event the Person Closed has a Family Tree Relationship which is also present
	 * in the Person Forward, augment the Relationship.
	 * 
	 * @param Connection
	 * @param personMergeSplitDto
	 * @param sfPersValeBean      - Select person forward data
	 * @return
	 */

	private void mergePersonRelationships(PersonMergeSplitDto personMergeSplitDto) {

		// get all the relationships for forward and closed persons
		List<FTPersonRelationDto> fwdPerRelList = ftRelationshipSearchDao
				.selectAllDirectRelationships(personMergeSplitDto.getIdForwardPerson());
		List<FTPersonRelationDto> closedPerRelList = ftRelationshipSearchDao
				.selectAllDirectRelationships(personMergeSplitDto.getIdClosedPerson());

		// variable to indicate if any relationship added to forward person as
		// part of merge
		boolean bAnyRelAdded = false;

		// for each relationship of closed person
		for (FTPersonRelationDto closedPersRelBean : closedPerRelList) {
			// skip invalid relationships
			/* ALM defect :15530 - If the dtInvalid is null, the record is valid
			   and should be considered for relationship merge. */
			if (ObjectUtils.isEmpty(closedPersRelBean.getDtInvalid())) {
				long idPersonToCompare1 = closedPersRelBean.getIdRelatedPerson();
				String relToCompare1 = closedPersRelBean.getCdRelation();

				// normalize the relationship so that closed person is on left
				// side
				if (personMergeSplitDto.getIdClosedPerson().equals(closedPersRelBean.getIdRelatedPerson())) {
					idPersonToCompare1 = closedPersRelBean.getIdPerson();
					// reverse the relation
					relToCompare1 = lookupDao.simpleDecodeSafe(CodesConstant.CRLINVRT,
							closedPersRelBean.getCdRelation());
				}

				boolean bMatchFound = false;

				// for each relationship of closed person
				// check if relationship exists in forward person
				FTPersonRelationDto fwdPersRelBean = new FTPersonRelationDto();

				for (FTPersonRelationDto tmpPersonRelationDto : fwdPerRelList) {
					// skip invalid relationships
					if (ObjectUtils.isEmpty(fwdPersRelBean.getDtInvalid())) {
						fwdPersRelBean = tmpPersonRelationDto;
						long idPersonToCompare2 = fwdPersRelBean.getIdRelatedPerson();
						String relToCompare2 = fwdPersRelBean.getCdRelation();

						// normalize the relationship so that closed person is
						// on left
						// side
						if (personMergeSplitDto.getIdForwardPerson() == fwdPersRelBean.getIdRelatedPerson()) {
							idPersonToCompare2 = fwdPersRelBean.getIdPerson();
							// reverse the relation
							relToCompare2 = lookupDao.simpleDecodeSafe(CodesConstant.CRLINVRT,
									fwdPersRelBean.getCdRelation());
						}

						// check if relation exists
						if ((idPersonToCompare1 == idPersonToCompare2)
								&& checkForEquality(relToCompare1, relToCompare2)) {
							bMatchFound = true;
							break;
						}
					}
				} // end of loop for forward person

				// If relationship does not exist for forward person, then add
				// it
				if (!bMatchFound) {
					if (personMergeSplitDto.getIdClosedPerson().equals(closedPersRelBean.getIdPerson())) {
						closedPersRelBean.setIdPerson((Long) personMergeSplitDto.getIdForwardPerson());
					} else
						closedPersRelBean.setIdRelatedPerson((Long) personMergeSplitDto.getIdForwardPerson());

					// Update CD_ORIGIN only if closed person person
					// relationship origin is not EX
					if (!CodesConstant.CRELSGOR_EX.equals(closedPersRelBean.getCdOrigin()))
						closedPersRelBean.setCdOrigin(CodesConstant.CRELSGOR_UA);

					closedPersRelBean.setIdLastUpdatePerson((Long) personMergeSplitDto.getIdPersonMergeWorker());

					// bAnyRelAdded Update flag only if closed person
					// person relationship origin is not EX
					if (!CodesConstant.CRELSGOR_EX.equals(closedPersRelBean.getCdOrigin()))
						bAnyRelAdded = true;

					ftRelationshipSearchDao.insertRelationship(closedPersRelBean);

				}

				// if match was found in forward person
				if (bMatchFound) {

					// If closed person relationship is EX and forward
					// person matching relationship is not EX
					// then we dont update forward person relationship. Idea
					// being
					// that relationship created in IMPACT are
					// more reliable than EX relationship which comes over from
					// ICPC.
					if (CodesConstant.CRELSGOR_EX.equals(closedPersRelBean.getCdOrigin())
							&& !CodesConstant.CRELSGOR_EX.equals(fwdPersRelBean.getCdOrigin()))
						continue;

					boolean ifAnyUpdate = false;
					// For the group of related elements below if the Person
					// forward does
					// not have any of these elements populated, they will be
					// brought forward from the closed person
					if ((ObjectUtils.isEmpty(fwdPersRelBean.getCdType())
							&& ObjectUtils.isEmpty(fwdPersRelBean.getCdLineage())
							&& ObjectUtils.isEmpty(fwdPersRelBean.getCdSeparation()))
							&& (!ObjectUtils.isEmpty(closedPersRelBean.getCdType())
									|| !ObjectUtils.isEmpty(closedPersRelBean.getCdLineage())
									|| !ObjectUtils.isEmpty(closedPersRelBean.getCdSeparation()))) {
						fwdPersRelBean.setCdType(closedPersRelBean.getCdType());
						fwdPersRelBean.setCdLineage(closedPersRelBean.getCdLineage());
						fwdPersRelBean.setCdSeparation(closedPersRelBean.getCdSeparation());
						ifAnyUpdate = true;
					}

					// If the Forward Person does not have an effective date,
					// bring
					// the Closed Person's date forward.
					if (ObjectUtils.isEmpty(fwdPersRelBean.getDtEffective())
							&& !ObjectUtils.isEmpty(closedPersRelBean.getDtEffective())) {
						fwdPersRelBean.setDtEffective(closedPersRelBean.getDtEffective());
						ifAnyUpdate = true;
					}

					// For the group of related elements below, closed
					// Relationship
					// has a DT_ENDED and the forward doesnt, keep the data from
					// closed relationship
					if (ObjectUtils.isEmpty(fwdPersRelBean.getDtEnded())
							&& !ObjectUtils.isEmpty(closedPersRelBean.getDtEnded())) {
						fwdPersRelBean.setDtEnded(closedPersRelBean.getDtEnded());
						fwdPersRelBean.setDtDissolution(closedPersRelBean.getDtDissolution());
						fwdPersRelBean.setCdEndReason(closedPersRelBean.getCdEndReason());

						ifAnyUpdate = true;
					}
					// Comments to be brought forward if forward person does not
					// have any
					if (ObjectUtils.isEmpty(fwdPersRelBean.getTxtRelComments())) {
						fwdPersRelBean.setTxtRelComments(closedPersRelBean.getTxtRelComments());
						ifAnyUpdate = true;
					}

					// if any field updated on fwdPersRelBean then update the
					// relationship
					if (ifAnyUpdate) {
						// Update CD_ORIGIN only if firward person
						// person relationship origin is not EX
						if (!CodesConstant.CRELSGOR_EX.equals(fwdPersRelBean.getCdOrigin()))
							fwdPersRelBean.setCdOrigin(CodesConstant.CRELSGOR_UU);

						fwdPersRelBean.setIdLastUpdatePerson((Long) personMergeSplitDto.getIdPersonMergeWorker());

						ftRelationshipSearchDao.updateRelationship(fwdPersRelBean);

						// Record any family tree update into person merge
						// update log
						// Update the update log if fwd person cd_origin is not
						// EX
						if (!CodesConstant.CRELSGOR_EX.equals(fwdPersRelBean.getCdOrigin())
								&& !personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_340))
							personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_340,
									ServiceConstants.IND_TRUE);
					}

				} // if match was found
			}
		} // end of loop on closed person relationships

		// Add following message when family Tree Relationships from the
		// Person Closed has been added to the Person Forward as part of
		// processing.
		if (bAnyRelAdded) {
			MergeSplitVldMsgDto infoMsg = new MergeSplitVldMsgDto();
			infoMsg.setMessageInt(Messages.MSG_POST_MERGE_FAMILY_TREE);
			personMergeSplitDto.getPostMergeInfoDataList().add(infoMsg);

			// Record any family tree update into person merge update log
			if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_340))
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_340, ServiceConstants.IND_TRUE);
		}

	}

	/**
	 * Update Person Forward current Education in the event that Person Closed
	 * current Education was selected. Maintain current IMPACT business rule that a
	 * Person may have only one active Education record at any time.
	 * 
	 * @param Connection
	 * @param personMergeSplitDto
	 * @param sfPersValeBean      - Select person forward data
	 * @return
	 */

	private void mergeEducationData(PersonMergeSplitDto personMergeSplitDto,
			SelectForwardPersonValueBean sfPersValeBean) {

		// if current education is selected from closed person,
		if (!ObjectUtils.isEmpty(sfPersValeBean.getSchool().getClosedPerson())
				&& sfPersValeBean.getSchool().getClosedPerson().getIsSelected()) {
			// check if person forward also has current educational record
			if (!ObjectUtils.isEmpty(sfPersValeBean.getSchool().getForwardPerson())
					&& !ObjectUtils.isEmpty(sfPersValeBean.getSchool().getForwardPerson().getPrimaryKeyData())
					&& sfPersValeBean.getSchool().getForwardPerson().getPrimaryKeyData().getIdKey() > 0) {
				// get education history record based on the primary key of the
				// education history record
				EducationalHistory eduHistValueBean = educationDao.getEducationHistory(
						sfPersValeBean.getSchool().getForwardPerson().getPrimaryKeyData().getIdKey());

				if (!ObjectUtils.isEmpty(eduHistValueBean)
						&& !ObjectUtils.isEmpty(eduHistValueBean.getDtEdhistWithdrawnDate())) {
					// End date the education history record for the forward
					// person
					eduHistValueBean.setDtEdhistWithdrawnDate(new Date());
					eduHistValueBean.setCdEdhistEnrollGrade(eduHistValueBean.getCdEdhistEnrollGrade());
					eduHistValueBean.setDtLastUpdate(
							sfPersValeBean.getSchool().getForwardPerson().getPrimaryKeyData().getDtLastUpdate());
					EducationHistoryDto educationHistoryDto = new EducationHistoryDto();
					BeanUtils.copyProperties(eduHistValueBean, educationHistoryDto);
					educationDao.updateEducationalHistory(educationHistoryDto);
				}
			}

			// Get the current educational record for closed person
			EducationalHistory eduHistValueBean = educationDao
					.getEducationHistory(sfPersValeBean.getSchool().getClosedPerson().getPrimaryKeyData().getIdKey());

			// if record found and it is still current then copy it over to
			// forward person
			if (!ObjectUtils.isEmpty(eduHistValueBean)
					&& ObjectUtils.isEmpty(eduHistValueBean.getDtEdhistWithdrawnDate())) {
				// Get the corresponding education need records for closed
				// person
				List<EducationalNeedDto> eduNeedsList = educationDao
						.getEducationalNeedListForHist(eduHistValueBean.getIdEdhist().intValue(), 0, 0, null, null);

				// Add the record to forward person
				Person person = new Person();
				person.setIdPerson(personMergeSplitDto.getIdForwardPerson());
				eduHistValueBean.setPerson(person);
				EducationalHistory educationHis = new EducationalHistory();
				BeanUtils.copyProperties(eduHistValueBean, educationHis);
				educationHis.setIdEdhist(0L);
				sessionFactory.getCurrentSession().save(educationHis);

				// Record any education data update into person merge update log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_360))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_360, ServiceConstants.IND_TRUE);

				// Save the corresponding education needs records to forward
				// person
				eduNeedsList.stream().forEach(eduNeedValBean -> {
					// set the EdHistId for the forward person
					// eduHistValueBean at this moment has forward person
					// EdHistId
					eduNeedValBean.setIdEducationHistory(eduHistValueBean.getIdEdhist().intValue());
					// set the forward person Id
					eduNeedValBean.setIdPerson(personMergeSplitDto.getIdForwardPerson().intValue());
					EducationalNeedDto educationalNeedDto = new EducationalNeedDto();
					BeanUtils.copyProperties(eduNeedValBean, educationalNeedDto);
					educationDao.saveEducationalNeed(educationalNeedDto);
				});
			}
		}
	}

	/**
	 * Add any Person Closed Education records not present for Person Forward. For
	 * any Person Closed Education records added to the Person Forward, add any
	 * corresponding Educational Need records present.
	 * 
	 * @param Connection
	 * @param personMergeSplitDto
	 * @param sfPersValeBean      - Select person forward data
	 * @return
	 */
	private void mergeEducationalHistoryData(PersonMergeSplitDto personMergeSplitDto) {

		List<EducationalHistory> fwdPersEduHistList = educationDao
				.getEducationHistoryList(personMergeSplitDto.getIdForwardPerson());
		List<EducationalHistory> closedPersEduHistList = educationDao
				.getEducationHistoryList(personMergeSplitDto.getIdClosedPerson());

		// variable which indicates if any education data updated for forward
		// perosn
		boolean bAnyUpdate = false;

		for (EducationalHistory closedPersEduHistValueBean : closedPersEduHistList) {
			boolean bMatchFound = false;
			// compare closed and fwd records based on ID_RESOURCE ,
			// CD_EDHIST_ENROLL_GRADE
			if (fwdPersEduHistList.stream().anyMatch(fwdPersEduHistValueBean -> ((
					(closedPersEduHistValueBean.getCapsResource() != null
							&& fwdPersEduHistValueBean.getCapsResource() != null)
							? (closedPersEduHistValueBean
					.getCapsResource().getIdResource().equals(fwdPersEduHistValueBean.getCapsResource().getIdResource())
					|| ((ServiceConstants.ZERO.equals(closedPersEduHistValueBean.getCapsResource().getIdResource())
							&& ServiceConstants.ZERO.equals(fwdPersEduHistValueBean.getCapsResource().getIdResource()))
							&& checkForEquality(closedPersEduHistValueBean.getNmEdhistSchool(),
									fwdPersEduHistValueBean.getNmEdhistSchool())))
							: false)
					&& checkForEquality(closedPersEduHistValueBean.getCdEdhistEnrollGrade(),
							fwdPersEduHistValueBean.getCdEdhistEnrollGrade()))))
				bMatchFound = true;
			// if no match found, add the record to forward person
			if (!bMatchFound) {
				bAnyUpdate = true;

				// Get the corresponding education need records for closed
				// person

				List<EducationalNeedDto> eduNeedsList = educationDao.getEducationalNeedListForHist(
						closedPersEduHistValueBean.getIdEdhist().intValue(), 0, 0, null, null);
				Person person = new Person();
				person.setIdPerson(personMergeSplitDto.getIdForwardPerson());
				closedPersEduHistValueBean.setPerson(person);
				EducationalHistory educationHist = new EducationalHistory();
				BeanUtils.copyProperties(closedPersEduHistValueBean, educationHist);
				educationHist.setIdEdhist(0L);
				sessionFactory.getCurrentSession().save(educationHist);

				// Save the corresponding education needs records to forward
				// person
				eduNeedsList.stream().forEach(eduNeedValBean -> {
					// set the EdHistId for the forward person
					// eduHistValueBean at this moment has forward person
					// EdHistId
					eduNeedValBean.setIdEducationHistory(closedPersEduHistValueBean.getIdEdhist().intValue());
					// set the forward person Id
					eduNeedValBean.setIdPerson(personMergeSplitDto.getIdForwardPerson().intValue());
					EducationalNeedDto educationalNeedDto = new EducationalNeedDto();
					BeanUtils.copyProperties(eduNeedValBean, educationalNeedDto);
					educationDao.saveEducationalNeed(educationalNeedDto);
				});
			}
		}

		if (bAnyUpdate && !personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_360)) {
			// Record any education data update into person merge update log
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_360, ServiceConstants.IND_TRUE);
		}
	}

	/**
	 * 
	 * Method Name: mergeRecordsCheck Method Description:Add all Person Closed
	 * Record Check History records to Person Forward. With the exception of Central
	 * Registry checks (RECORDS_CHECK. CD_REC_CHECK_CHECK_TYPE = '85' (Central
	 * Registry) in which case only add unduplicated records to Person Forward
	 * 
	 * @param personMergeSplitDto
	 * @return
	 */

	private boolean mergeRecordsCheck(PersonMergeSplitDto personMergeSplitDto) {
		boolean criminalMessageSet = false;

		List<RecordsCheck> closedPersRecChkList = recordsCheckDao
				.getRecordsCheckListByPersonId(personMergeSplitDto.getIdClosedPerson());
		List<RecordsCheck> fwdPersRecChkList = recordsCheckDao
				.getRecordsCheckListByPersonId(personMergeSplitDto.getIdForwardPerson());
		boolean fwdPersCrimHisChecksAdded = false;

		for (RecordsCheck recChkValueBean : closedPersRecChkList) {

			boolean bAddRecChk = false;

			if (CodesConstant.CCHKTYPE_85.equals(recChkValueBean.getCdRecCheckCheckType())) {
				// if forward person has records check record for same type but
				// different date then add it to forward person
				boolean bMatching85 = false;
				if (fwdPersRecChkList.stream()
						.anyMatch(recChkFwdPersValueBean -> (CodesConstant.CCHKTYPE_85
								.equals(recChkFwdPersValueBean.getCdRecCheckCheckType())
								&& (int) DateUtils.daysDifference(recChkValueBean.getDtRecCheckRequest(),
										recChkFwdPersValueBean.getDtRecCheckRequest()) == 0)))
					bMatching85 = true;
				// if no matching 85 was found on fwd person then we need to add
				// it
				if (!bMatching85) {
					bAddRecChk = true;
				}
			} else {
				bAddRecChk = true;
			}
			/*
			 * if record check record to be added to forward person and it is a completed
			 * record check then we create copy of the data for forward person
			 */
			if (bAddRecChk && !ObjectUtils.isEmpty(recChkValueBean.getDtRecCheckCompleted())) {
				long idRecCheck = recChkValueBean.getIdRecCheck();
				RecordsCheckNarr recChkNarr = recordsCheckDao.getRecordsCheckNarrative(idRecCheck);
				RecordsCheck recordsCheck = new RecordsCheck();
				BeanUtils.copyProperties(recChkValueBean, recordsCheck);
				Person person = new Person();
				person.setIdPerson(personMergeSplitDto.getIdForwardPerson());
				recordsCheck.setPersonByIdRecCheckPerson(person);
				recordsCheck.setIdRecCheck(0l);
				long idRecCheckForward = recordsCheckDao.saveRecordsCheck(recordsCheck);
				/*
				 * any forward recCheck[j] record may map to records check types (
				 * CRIMINAL_HIST_NARRs for CCHKTYPEs '10', '20, '80' occur in DB )
				 */
				if (idRecCheckForward > 0) {
					mergeCriminalHistory(idRecCheck, idRecCheckForward);
				}
				// save record check determination history
				List<RecChkDetermHistory> recChkDetermHist = recordsCheckDao.getRecordsCheckDetHistList(idRecCheck);
				recChkDetermHist.stream().forEach(recDetHisValueBean -> {
					RecChkDetermHistory recChkDetermHistory = new RecChkDetermHistory();
					BeanUtils.copyProperties(recDetHisValueBean, recChkDetermHistory);
					recChkDetermHistory.setIdRecChkDetermHistory(0l);
					recChkDetermHistory.setIdRecCheck((long) idRecCheckForward);
					recChkDetermHistory.setIdPerson(personMergeSplitDto.getIdForwardPerson());
					recChkDetermHistory.setDtLastUpdate(new Date());
					recordsCheckDao.saveRecordsCheckDetermHist(recChkDetermHistory);
				});

				// Save record check narrative for new record check record
				if (!ObjectUtils.isEmpty(recChkNarr)) {
					RecordsCheckNarr newRecChkNarr = new RecordsCheckNarr();
					BeanUtils.copyProperties(recChkNarr, newRecChkNarr);
					newRecChkNarr.setIdRecCheck(idRecCheckForward);
					newRecChkNarr.setDtLastUpdate(new Date());
					recordsCheckDao.saveRecordsCheckNarrative(newRecChkNarr);
				}

				// If we now added following type of records check, then set a
				// variable
				if (CodesConstant.CCHKTYPE_10.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_15.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_20.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_25.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_80.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_81.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_82.equals(recChkValueBean.getCdRecCheckCheckType())) {
					fwdPersCrimHisChecksAdded = true;
				}

				// Record any Records Check update into person merge update log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_440))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_440, ServiceConstants.IND_TRUE);
			}
			// if in-progress records check, then we update person Id on it.
			else if (ObjectUtils.isEmpty(recChkValueBean.getDtRecCheckCompleted())) {
				List<RecChkDetermHistory> recChkDetermHistList = recordsCheckDao
						.getRecordsCheckDetHistList(recChkValueBean.getIdRecCheck());

				// update the forward person Id on the record check record which
				// is not yet complete

				recordsCheckDao.updatePersonOnRecordsChk(recChkValueBean.getIdRecCheck(),
						personMergeSplitDto.getIdForwardPerson());

				// update the forward person Id on the associated
				// REC_CHK_DETERM_HISTORY
				recChkDetermHistList.stream().forEach(recChkDetermHist -> {
					RecordsCheckDeterminationDto checkDeterminationDto = new RecordsCheckDeterminationDto();
					BeanUtils.copyProperties(recChkDetermHist, checkDeterminationDto);
					recordsCheckDao.updatePersonOnRecChkDetHist(recChkValueBean.getIdRecCheck(),
							personMergeSplitDto.getIdForwardPerson());
				});
				// If we now added or updated following type of records check,
				// then set a variable
				if (CodesConstant.CCHKTYPE_10.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_15.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_20.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_25.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_80.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_81.equals(recChkValueBean.getCdRecCheckCheckType())
						|| CodesConstant.CCHKTYPE_82.equals(recChkValueBean.getCdRecCheckCheckType())) {
					fwdPersCrimHisChecksAdded = true;
				}
				// Record any Records Check update into person merge update log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_440))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_440, ServiceConstants.IND_TRUE);
			}

		} // end for loop for closed person rec check list

		// if we have added those criminal history check records for forward
		// person,
		// then we need to show an information message post merge to the user
		if (fwdPersCrimHisChecksAdded) {
			MergeSplitVldMsgDto infoMsg = new MergeSplitVldMsgDto();
			infoMsg.setMessageInt(Messages.MSG_POST_MERGE_CRIM);
			personMergeSplitDto.getPostMergeInfoDataList().add(infoMsg);
			criminalMessageSet = true;
		}
		return criminalMessageSet;
	}

	/**
	 * Add all Criminal History records to Person Forward.
	 *
	 * @param Connection
	 * @param long
	 * @param long
	 * @return
	 */
	private void mergeCriminalHistory(long idRecCheck, long idRecCheckForward) {
		// Modified the code to insert the forward person criminal history
		// records in Criminal history and criminal history tables - Warranty defect
		// 11185
		List<CriminalHistory> crimHistList = criminalHistoryDao.getCriminalHistoryList(idRecCheck);
		crimHistList.stream().forEach(criminalHistory -> {
			CriminalHistory criminalHistoryNew = new CriminalHistory();
			BeanUtils.copyProperties(criminalHistory, criminalHistoryNew);
			RecordsCheck recordsCheck = (RecordsCheck) sessionFactory.getCurrentSession().get(RecordsCheck.class,
					idRecCheckForward);
			criminalHistoryNew.setRecordsCheck(recordsCheck);
			criminalHistoryNew.setIdCrimHist(null);
			criminalHistoryNew.setDtLastModified(null);
			long idCrimHistForward = criminalHistoryDao.saveCriminalHistory(criminalHistoryNew);
			if (idCrimHistForward > 0) {
				if (criminalHistoryDao.isCrimHistNarrPresentForCriminalHistory(criminalHistory.getIdCrimHist())) {
					CriminalHistNarr crimHistNarr = criminalHistoryDao
							.getMergeCriminalHistNarr(criminalHistory.getIdCrimHist());
					CriminalHistNarr CriminalHistNarrNew = new CriminalHistNarr();
					CriminalHistNarrNew.setIdCrimHist(idCrimHistForward);
					CriminalHistNarrNew.setDtLastUpdate(crimHistNarr.getDtLastUpdate());
					CriminalHistNarrNew.setIdDocumentTemplate(crimHistNarr.getIdDocumentTemplate());
					CriminalHistNarrNew.setNarrative(crimHistNarr.getNarrative());
					criminalHistoryDao.saveMergeCriminalHistNarr(CriminalHistNarrNew);
				}
			}
		});

	}

	/**
	 * If the closed person has TLETS data, write records for forward person. Also
	 * detect whether to set TLETS indicator for post merge page.
	 * 
	 * @param Connection
	 * @param PersonMergeSplitDto @
	 */

	private void mergeTlets(PersonMergeSplitDto persMergeValueBean, boolean criminalMessageSet) {

		boolean closedPersonHasTlets = false;

		// This lookup gets all and only the table fields - omitting a few other
		// fields that the Tlets value
		// bean also supports. The list is ordered by conducted date (when the
		// check was done) ascending. We
		// don't care about the forward person having TLETS or not - only the
		// closed person having it should
		// trigger messages and log row.

		List<TletsCheck> closedPersonTletsList = tletsDao.getTletsListForPerson(persMergeValueBean.getIdClosedPerson());

		if (CollectionUtils.isNotEmpty(closedPersonTletsList)) {
			// Lookup row, change the person ID to the forward one, then write
			// as new row.
			for (TletsCheck tempTlets : closedPersonTletsList) {

				// The dao gets a new sequence number and uses it on the new
				// row.
				int oldIdTletsCheck = tempTlets.getIdTletsCheck().intValue();

				// Have to do this here instead of above to prevent false
				// positives.
				if (oldIdTletsCheck > 0) {
					closedPersonHasTlets = true;
				}
				/*
				 * Save the same row as a new row with the forward person ID replacing the
				 * closed person ID. Note that in the pre existing TletsDao save method used
				 * below, tletsPersonId is used instead of idPerson for the table field
				 * ID_PERSON.
				 */
				Person person = new Person();
				person.setIdPerson(persMergeValueBean.getIdForwardPerson());
				tempTlets.setPerson(person);
				TletsCheck newTlets = tletsDao.saveTlets(tempTlets);

				// Now get the new row TLETS_CHECK.ID_TLETS_CHECK to use on the
				// new row on the companion table.
				int newIdTletsCheck = newTlets.getIdTletsCheck().intValue();
				/*
				 * The companion table, TLETS_CHECK_NARR, must have new rows written to match
				 * the ones (if any) linked to the old rows on the main table. So, lookup any
				 * narrative rows for oldIdTletsCheck, and if there are any, write each to a new
				 * narrative row with newIdTletsCheck.
				 */
				List<TletsCheckNarr> listOfNarrBeans = tletsDao.getNarrListForTletsCheckId(oldIdTletsCheck);
				if (CollectionUtils.isNotEmpty(listOfNarrBeans)) {
					listOfNarrBeans.stream().forEach(tempTNVBean -> {
						// only call insert if the narrative exists
						int sqlSelectedLength = tletsDao.isTletsCheckNarrPresent(tempTNVBean.getIdTletsCheckNarr());
						if (sqlSelectedLength > 0) {
							tempTNVBean.setIdTletsCheckNarr(newIdTletsCheck);
							tletsDao.insertRowOnTletsCheckNarr(tempTNVBean);
						}
					});
				} // end if there are narrative rows for the closed person
			} // end outer for loop thru closed person TLETS check rows
		} // end if there are any rows in closed person Tlets list

		// Record any TLETS update into person merge update log
		if (closedPersonHasTlets) {
			// if we have added those criminal history check records for forward
			// person,
			// then we need to show an information message post merge to the
			// user
			if (!criminalMessageSet) {
				MergeSplitVldMsgDto infoMsg = new MergeSplitVldMsgDto();
				infoMsg.setMessageInt(Messages.MSG_POST_MERGE_CRIM);
				persMergeValueBean.getPostMergeInfoDataList().add(infoMsg);
			}
			if (!persMergeValueBean.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_460)) {
				persMergeValueBean.getUpdateLogMap().put(CodesConstant.CPMFLDCT_460, ServiceConstants.IND_TRUE);
			}
		}
	} // end mergeTlets()

	/**
	 * Add all Person Closed Foster / Adoptive Training History not present for
	 * Person Forward.
	 * 
	 * @param Connection
	 * @param personMergeSplitDto
	 * @return
	 */

	private void mergeFosterAdoptiveTraining(PersonMergeSplitDto personMergeSplitDto) {

		List<FaIndivTraining> fwdPersFATrnList = resourceServiceDAO
				.getFATrainingList(personMergeSplitDto.getIdForwardPerson());
		List<FaIndivTraining> closedPersFATrnList = resourceServiceDAO
				.getFATrainingList(personMergeSplitDto.getIdClosedPerson());

		closedPersFATrnList.stream().forEach(closedPersFATrnBean -> {
			boolean bTrainingExists = false;
			if (fwdPersFATrnList.stream()
					.anyMatch(fwdPersFATrnBean -> (closedPersFATrnBean.getCdIndivTrnType()
							.equals(fwdPersFATrnBean.getCdIndivTrnType())
							&& (int) DateUtils.daysDifference(closedPersFATrnBean.getDtIndivTrn(),
									fwdPersFATrnBean.getDtIndivTrn()) == 0)))
				bTrainingExists = true;

			// if training record does not exist for forward person then add it
			if (!bTrainingExists) {
				Person person = new Person();
				person.setIdPerson(personMergeSplitDto.getIdForwardPerson());
				closedPersFATrnBean.setPerson(person);
				FaIndivTraining faIndivTraining = new FaIndivTraining();
				BeanUtils.copyProperties(closedPersFATrnBean, faIndivTraining);
				faIndivTraining.setIdIndivTraining(0L);
				resourceServiceDAO.saveFATrainingRecord(faIndivTraining);

				// Record any FA Training update into person merge update log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_450))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_450, ServiceConstants.IND_TRUE);
			}
		});
	}

	/**
	 * Add a Person Closed Characteristics records not present for Person Forward.
	 * Person Characteristics are added and processed by category. In the event the
	 * Person Closed has a Current Characteristic which is also a Current
	 * Characteristic of the same Category & Type for the Person Forward, then
	 * update the start and end date as per the business rules.
	 * 
	 * @param Connection
	 * @param personMergeSplitDto
	 * @param PersonValueBean
	 * @return
	 */

	private void mergePersonCharacteristics(PersonMergeSplitDto personMergeSplitDto, PersonDto fwdPersonValueBean) {

		ArrayList<CharacteristicsDto> fwdPersCharList = personCharDao
				.getPersonCharList(personMergeSplitDto.getIdForwardPerson().intValue());
		ArrayList<CharacteristicsDto> closedPersCharList = personCharDao
				.getPersonCharList(personMergeSplitDto.getIdClosedPerson().intValue());
		boolean fwdPersCharListUpdated = false;

		// First merge the CAP (APS) Person Characteristics
		boolean bUpdateStatus = mergePersonCharacteristicsByType(personMergeSplitDto, fwdPersCharList,
				closedPersCharList, CodesConstant.CCHRTCAT_CAP);
		fwdPersCharListUpdated = fwdPersCharListUpdated || bUpdateStatus;

		// Record any APS characteristics update into person merge update log
		if (bUpdateStatus && !personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_380))
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_380, ServiceConstants.IND_TRUE);

		// merge the CCT (Parent/Caretaker)
		bUpdateStatus = mergePersonCharacteristicsByType(personMergeSplitDto, fwdPersCharList, closedPersCharList,
				CodesConstant.CCHRTCAT_CCT);
		fwdPersCharListUpdated = fwdPersCharListUpdated || bUpdateStatus;

		// Record any CCT characteristics update into person merge update log
		if (bUpdateStatus && !personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_400))
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_400, ServiceConstants.IND_TRUE);

		// merge the CCH (Child-Investigation) Person Characteristics
		bUpdateStatus = mergePersonCharacteristicsByType(personMergeSplitDto, fwdPersCharList, closedPersCharList,
				CodesConstant.CCHRTCAT_CCH);
		fwdPersCharListUpdated = fwdPersCharListUpdated || bUpdateStatus;

		// Record any CCH characteristics update into person merge update log
		if (bUpdateStatus && !personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_390))
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_390, ServiceConstants.IND_TRUE);

		// merge the CPL (Child-Placement)
		bUpdateStatus = mergePersonCharacteristicsByType(personMergeSplitDto, fwdPersCharList, closedPersCharList,
				CodesConstant.CCHRTCAT_CPL);
		fwdPersCharListUpdated = fwdPersCharListUpdated || bUpdateStatus;

		// Record any CPL characteristics update into person merge update log
		if (bUpdateStatus && !personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_410))
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_410, ServiceConstants.IND_TRUE);

		// get person characteristics list for the forward person once again
		// this is used for calculating PERSON.CD_PERSON_CHAR (Code Type =
		// CPERCHAR) field value
		// 0 - No records exist
		// 1 - Characteristics exist
		// 2 - No characteristics exist
		fwdPersCharList = personCharDao
				.getPersonCharList(new Long(personMergeSplitDto.getIdForwardPerson()).intValue());
		if (CollectionUtils.isEmpty(fwdPersCharList)){
			fwdPersonValueBean.setCdPersonChar(CodesConstant.CPERCHAR_0);
			//[artf170584] ALM-16411 : If the forwarded person has no characteristics and old person has characteristics then
			// PERSON.CD_PERSON_CHAR  needs to be updated to '1' to show in Person Characteristics section  in forward Person details page.
			if(fwdPersCharListUpdated){
				 fwdPersonValueBean.setCdPersonChar(CodesConstant.CPERCHAR_1);
			 }
		}else {
			fwdPersonValueBean.setCdPersonChar(CodesConstant.CPERCHAR_2);
			if (fwdPersCharList.stream().anyMatch(fwdPersCharValueBean -> ((int) DateUtils
					.daysDifference(fwdPersCharValueBean.getDtCharEnd(), MAX_JAVA_DATE) == 0)))
				fwdPersonValueBean.setCdPersonChar(CodesConstant.CPERCHAR_1);
		}

	}

	/**
	 * 
	 * Method Name: mergePersonCharacteristicsByType Method Description:Add a Person
	 * Closed Characteristics records not present for Person Forward. Person
	 * Characteristics are added and processed by category. In the event the Person
	 * Closed has a Current Characteristic which is also a Current Characteristic of
	 * the same Category & Type for the Person Forward, then update the start and
	 * end date as per the business rules.
	 * 
	 * @param personMergeSplitDto
	 * @param fwdPersCharList
	 * @param closedPersCharList
	 * @param charCatg
	 * @return
	 */
	private boolean mergePersonCharacteristicsByType(PersonMergeSplitDto personMergeSplitDto,
			ArrayList<CharacteristicsDto> fwdPersCharList, ArrayList<CharacteristicsDto> closedPersCharList,
			String charCatg) {

		boolean bAnyCharUpdateForFwdPers = false;

		for (CharacteristicsDto closedPersCharValueBean : closedPersCharList) {
			CharacteristicsDto fwdPersCharValueBean = null;

			// if it is not the desired category, then skip
			if (!charCatg.equals(closedPersCharValueBean.getCdCharCategory()))
				continue;

			boolean bMatchFound = false;

			Optional<CharacteristicsDto> tmpCharOptional = fwdPersCharList.stream()
					.filter(tmpCharDto -> (checkForEquality(closedPersCharValueBean.getCdCharCategory(),
							tmpCharDto.getCdCharCategory())))
					.findFirst();
			if (tmpCharOptional.isPresent()) {
				fwdPersCharValueBean = tmpCharOptional.get();
				bMatchFound = true;
			}

			// if match not found, then add characteristics for forward person
			if (!bMatchFound) {
				closedPersCharValueBean.setIdPerson(personMergeSplitDto.getIdForwardPerson());
				bAnyCharUpdateForFwdPers = true;
				personCharDao.savePersonChar(closedPersCharValueBean);
			} else {
				boolean bUpdateDates = false;

				// For any Person Characteristics found above that DO match,
				// compare, DT_CHAR_START, DT_CHAR_END for Characteristics
				// category of type CAP
				if (CodesConstant.CCHRTCAT_CAP.equals(charCatg)
						&& ((int) DateUtils.daysDifference(closedPersCharValueBean.getDtCharStart(),
								fwdPersCharValueBean.getDtCharStart()) != 0
								|| (int) DateUtils.daysDifference(closedPersCharValueBean.getDtCharEnd(),
										fwdPersCharValueBean.getDtCharEnd()) != 0)) {
					bUpdateDates = true;
				}
				// compare, DT_CHAR_START, DT_CHAR_END, CD_STATUS for
				// Characteristics
				// category of type CAP

				else if (!checkForEquality(closedPersCharValueBean.getCdCharCategory(),
						fwdPersCharValueBean.getCdCharCategory())
						|| (int) DateUtils.daysDifference(closedPersCharValueBean.getDtCharStart(),
								fwdPersCharValueBean.getDtCharStart()) != 0
						|| (int) DateUtils.daysDifference(closedPersCharValueBean.getDtCharEnd(),
								fwdPersCharValueBean.getDtCharEnd()) != 0) {
					bUpdateDates = true;
				}

				if (bUpdateDates) {
					// If for both DT_CHAR_END is '12/31/4712'(i.e., Current)

					if (DateUtils.daysDifference(closedPersCharValueBean.getDtCharEnd(), MAX_JAVA_DATE) == 0
							&& DateUtils.daysDifference(fwdPersCharValueBean.getDtCharEnd(), MAX_JAVA_DATE) == 0) {
						boolean bUpdateFwdChar = false;
						// If DT_CHAR_START for Person Closed is < Person
						// Forward,
						// set Person Forward value for DT_CHAR_START = Person
						// Closed

						if (DateUtils.isBefore(closedPersCharValueBean.getDtCharStart(),
								closedPersCharValueBean.getDtCharStart())) {
							fwdPersCharValueBean.setDtCharStart(closedPersCharValueBean.getDtCharStart());
							bUpdateFwdChar = true;
						}
						// For any characteristics other than CAP,
						// If CD_STATUS = 'D' for Person Closed and 'S' for
						// Person Forward,
						// update Person Forward record CD_STATUS = 'D'
						if (!CodesConstant.CCHRTCAT_CAP.equals(charCatg)
								&& CodesConstant.CHARSTAT_D.equals(closedPersCharValueBean.getCdCharCategory())
								&& CodesConstant.CHARSTAT_S.equals(fwdPersCharValueBean.getCdCharCategory())) {
							fwdPersCharValueBean.setCdCharCategory(CodesConstant.CHARSTAT_D);
							bUpdateFwdChar = true;
						}
						if (bUpdateFwdChar) {
							bAnyCharUpdateForFwdPers = true;
							personCharDao.updatePersonChar(fwdPersCharValueBean);
						}
					}
					// If Person Closed DT_CHAR_END = '12/31/4712'(Current) and
					// Person Forward <> '12/31/4712' (Not Current),
					// Update Person Forward, set DT_CHAR_END =
					// '12/31/4712'(Make Current).
					else if (DateUtils.daysDifference(closedPersCharValueBean.getDtCharEnd(), MAX_JAVA_DATE) == 0
							&& DateUtils.daysDifference(fwdPersCharValueBean.getDtCharEnd(), MAX_JAVA_DATE) != 0) {
						fwdPersCharValueBean.setDtCharEnd(MAX_JAVA_DATE);
						personCharDao.updatePersonChar(fwdPersCharValueBean);
						bAnyCharUpdateForFwdPers = true;
					}
				} // end of if bUpdateDates
			} // end of bMatchFound
		} // end of for loop for person closed characteristics

		return bAnyCharUpdateForFwdPers;
	}

	/**
	 * 
	 * Method Name: mergeChildSafetyPlacements Method Description:1. Check if Person
	 * Closed is listed as either the Child or Caregiver in a Child Safety
	 * Placement. If no, do nothing, if yes 2. Check if the Stage the Child Safety
	 * Placement was Opened is still Open (INV or FPR) OR 3. If the Child Safety
	 * Placement was opened in an INV Stage that has closed, check if the Stage
	 * progressed to an FPR Stage that is still open. If yes to either 4. Update
	 * Placement (Child or Caregiver value as applicable) for Person Closed to
	 * Person Forward.
	 * 
	 * @param personMergeSplitDto
	 */
	private void mergeChildSafetyPlacements(PersonMergeSplitDto personMergeSplitDto) {

		// fetch the child safety placements for closed person Id where it
		// exists as
		// Child
		List<ChildSafetyPlacementDto> childPCSPArr = pcspDao
				.getPCSPListForPerson(personMergeSplitDto.getIdClosedPerson().intValue(), 0);
		childPCSPArr.stream().forEach(pcspBean -> {
			boolean bUpdateNeeded = false;
			if (ObjectUtils.isEmpty(pcspBean.getDtStageClose()))
				bUpdateNeeded = true;
			else if (!ObjectUtils.isEmpty(pcspBean.getDtStageClose())
					&& CodesConstant.CSTAGES_INV.equals(pcspBean.getCdStage())) {
				// check if this INV stage progressed to a FPR stage which is
				// still open
				List<Stage> stageList = stageDao.getStageProgressedList(pcspBean.getIdStage());
				if (stageList.stream().anyMatch(stage -> (ObjectUtils.isEmpty(stage.getDtStageClose())
						&& CodesConstant.CSTAGES_FPR.equals(stage.getCdStage()))))
					bUpdateNeeded = true;
			}
			// update the forward person Id on ChildSafetyPlacement where closed
			// person ID exists as Child
			if (bUpdateNeeded) {
				PCSPDto pcspDto = new PCSPDto();
				BeanUtils.copyProperties(pcspBean, pcspDto);
				pcspDao.updateChildIdOnPCSP(new Long(personMergeSplitDto.getIdForwardPerson()).intValue(), pcspDto);

				// Record any child safety placement update into person merge
				// update log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_350))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_350, ServiceConstants.IND_TRUE);
			}
		});
		// fetch the child safety placements for closed person Id where it
		// exists as Caregiver
		List<ChildSafetyPlacementDto> caregiverPCSPArr = pcspDao.getPCSPListForPerson(0,
				personMergeSplitDto.getIdClosedPerson().intValue());
		caregiverPCSPArr.stream().forEach(pcspBean -> {
			boolean bUpdateNeeded = false;
			if (ObjectUtils.isEmpty(pcspBean.getDtStageClose()))
				bUpdateNeeded = true;
			else if (!ObjectUtils.isEmpty(pcspBean.getDtStageClose())
					&& CodesConstant.CSTAGES_INV.equals(pcspBean.getCdStage())) {
				// check if this INV stage progressed to a FPR stage which is
				// still open
				List<Stage> stageList = stageDao.getStageProgressedList(pcspBean.getIdStage().intValue());
				if (stageList.stream().anyMatch(stage -> (ObjectUtils.isEmpty(stage.getDtStageClose())
						&& CodesConstant.CSTAGES_FPR.equals(stage.getCdStage()))))
					bUpdateNeeded = true;
			}
			// update the forward person Id on ChildSafetyPlacement where closed
			// person ID exists as Caregiver
			if (bUpdateNeeded) {
				PCSPDto pcspDto = new PCSPDto();
				BeanUtils.copyProperties(pcspBean, pcspDto);
				pcspDao.updateCaregiverIdOnPCSP(personMergeSplitDto.getIdForwardPerson().intValue(), pcspDto);

				// Record any child safety placement update into person merge
				// update log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_350))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_350, ServiceConstants.IND_TRUE);

			}
		});
	}

	/**
	 * Check if the Person Closed has any Active Financial Accounts. If no, do
	 * nothing, if yes to any, - Update each active Financial Account for Person
	 * Closed to Person Forward. Note that recent relaxed Financial Account
	 * validations do not allow a Person Merge if - Person Closed has a Financial
	 * Checking Account with transactions linked to an invoice - Both Person Closed
	 * and Person Forward have an Active Checking Account
	 * 
	 * @param Connection
	 * @param personMergeSplitDto
	 * @return
	 */
	private void mergeFinancialAccounts(PersonMergeSplitDto personMergeSplitDto) {

		// Fetch Active Checking accounts for person closed.
		ArrayList<Long> finAcctList = financialAcctDao.fetchActiveAccountList(personMergeSplitDto.getIdClosedPerson());

		// Update the Person Id on the account as Person ID of the forward
		// person
		finAcctList.stream().forEach(finAcctId -> {
			financialAcctDao.updatePersonOnFinancialAccount(personMergeSplitDto.getIdForwardPerson(), finAcctId);
			// Record any financial account update into person merge update log
			if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_420))
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_420, ServiceConstants.IND_TRUE);
		});
	}

	/**
	 * 
	 * Method Name: mergePRT Method Description:Merge PRT records Gets all the
	 * closed PRT records for the Closed Person in any Open Stage, and update Closed
	 * Person ID with Forward Person ID for these tables.
	 * 
	 * @param persMergeValueBean
	 */
	public void mergePRT(PersonMergeSplitDto persMergeValueBean) {

		Long idClosedPerson = persMergeValueBean.getIdClosedPerson();
		Long idFwdPerson = persMergeValueBean.getIdForwardPerson();
		boolean allowMerge = true;
		boolean isFwdPerInActivePRT = prtPersonMergeSplitDao.isActivePRT(idFwdPerson);
		boolean isClosedPerInActivePRT = prtPersonMergeSplitDao.isActivePRT(idClosedPerson);

		if (isFwdPerInActivePRT && isClosedPerInActivePRT) {
			allowMerge = false;
		} else {
			allowMerge = true;
		}

		// Fetches all PRT Action Plan (closed) in all the OPEN stages for
		// Closed Person. Closed Person must be on Person List, Stage must be
		// Open.
		if (allowMerge) {
			List<Long> prtActPlnList = prtPersonMergeSplitDao.getPRTActPlnForPerson(idClosedPerson);
			// For Each action plan update all the Records in the
			// prt_person_link.
			prtActPlnList.stream().forEach(idPrtActionPlan -> {
				int idPrtActPln = (!ObjectUtils.isEmpty(idPrtActionPlan)) ? idPrtActionPlan.intValue() : 0;
				// Update all PRT Records where ID_PERSON is Closed Person.
				prtPersonMergeSplitDao.updatePersonOnPrtActPlan(idFwdPerson.intValue(), idPrtActPln,
						idClosedPerson.intValue());
			});
		}
		// Fetches all prt connections (in active and closed prt)
		List<Long> prtConnectionList = prtPersonMergeSplitDao.getPRTConnectionForPerson(idClosedPerson);
		prtConnectionList.stream().forEach(idPrtConnection -> {
			Long idPrtConn = (!ObjectUtils.isEmpty(idPrtConnection)) ? idPrtConnection : 0L;
			// Update all PRT connection records where ID_PERSON is Closed
			// Person.
			personMergeSplitDao.updatePersonOnPrtForSplit(idClosedPerson, idPrtConn, idFwdPerson);
		});
	}

	/**
	 * 
	 * Method Name: bothChildInSameAssessment Method Description: Check for
	 * duplicate in NEW PCSP
	 * 
	 * @param persMergeValueBean
	 */
	public void bothChildInSameAssessment(PersonMergeSplitDto persMergeValueBean,List<Integer> pcspPlcmntPerClosedList,List<Integer> pcspPlcmntPerForwardList) {
		int idClosedPerson = persMergeValueBean.getIdClosedPerson().intValue();
		int idFwdPerson = persMergeValueBean.getIdForwardPerson().intValue();
		pcspPlcmntPerClosedList.stream().forEach(closedPerRow -> {
			if (pcspPlcmntPerForwardList.stream().noneMatch(perForwardRow -> (closedPerRow.intValue() != 0
					&& perForwardRow.intValue() != 0 && closedPerRow.intValue() == perForwardRow.intValue()))) {
				int idPcspPlacement = closedPerRow.intValue();
				if (idPcspPlacement != 0) {
					// update placement if records from both pids aren't
					// overlapping dates
					personMergeSplitDao.updatePcspPlcmntForMerge(idFwdPerson, idPcspPlacement, idClosedPerson);
				}
			}
		});
	}

	/**
	 * Merge PCSP Records Gets all the closed PCSP records for the Closed Person in
	 * any Open Stage, and update Closed Person ID with Forward Person ID for these
	 * tables.
	 * 
	 * @param PersonMergeSplitDto
	 */
	public void mergeLegacyPCSP(PersonMergeSplitDto persMergeValueBean) {

		int idClosedPerson = persMergeValueBean.getIdClosedPerson().intValue();
		int idFwdPerson = persMergeValueBean.getIdForwardPerson().intValue();
		List<Long> childPCSPList = personMergeSplitDao.getLegacyChildForSplit(idClosedPerson, idFwdPerson);
		List<Long> caregiverPCSPList = personMergeSplitDao.getLegacyCaregiverForSplit(idClosedPerson, idFwdPerson);
		// Merge children
		childPCSPList.stream().forEach(childrenPCSP -> {
			int idChildSafetyPlcmnt = childrenPCSP.intValue();
			if (idChildSafetyPlcmnt != 0) {
				// update pcsp_asmnt only if the closed pid is the pcsp
				// assessment caregiver.
				personMergeSplitDao.updateChildSafetyPlcmtForSplit(idFwdPerson, idChildSafetyPlcmnt, idClosedPerson);
			}
		});

		// Merge caregiver
		caregiverPCSPList.stream().forEach(caregiverPCSP -> {
			int idChildSafetyPlcmnt = caregiverPCSP.intValue();
			if (idChildSafetyPlcmnt != 0) {
				// update pcsp_asmnt only if the closed pid is the pcsp
				// assessment caregiver.
				personMergeSplitDao.updateCaregiverSafetyPlcmtForSplit(idFwdPerson, idChildSafetyPlcmnt,
						idClosedPerson);
			}
		});
	}

	/**
	 * 
	 * Method Name: mergePCSP Method Description:Merge PCSP Records Gets all the
	 * closed PCSP records for the Closed Person in any Open Stage, and update
	 * Closed Person ID with Forward Person ID for these tables.
	 * 
	 * @param persMergeValueBean
	 */
	public void mergePCSP(PersonMergeSplitDto persMergeValueBean) {

		int idClosedPerson = persMergeValueBean.getIdClosedPerson().intValue();
		int idFwdPerson = persMergeValueBean.getIdForwardPerson().intValue();
		List<PcspAsmnt> pcspAsmntClosedList = personMergeSplitDao.getPcspAsmntByCaregiver(idClosedPerson);
		List<Integer> pcspPlcmntPerForwardList = personMergeSplitDao.getPcspPlcmntForMerge(idFwdPerson);
		List<Integer> pcspPlcmntPerClosedList = personMergeSplitDao.getPcspPlcmntForMerge(idClosedPerson);
		List<PcspPrsnLink> pcspPersonLinkList = personMergeSplitDao.getPcspPrsnLink(idClosedPerson);
		// Merge Assessment
		pcspAsmntClosedList.stream().forEach(caregiverAssessment -> {
			if (caregiverAssessment.getIdPcspAsmnt() != 0) {
				// update pcsp_asmnt only if the closed pid is the pcsp
				// assessment caregiver.
				personMergeSplitDao.updateCaregiverOnPCSPAsmnt(idFwdPerson, caregiverAssessment.getIdPcspAsmnt(), idClosedPerson);
			}
		});
		if (CollectionUtils.isNotEmpty(pcspPlcmntPerForwardList)
				&& CollectionUtils.isNotEmpty(pcspPlcmntPerClosedList)) {
			bothChildInSameAssessment(persMergeValueBean,pcspPlcmntPerClosedList,pcspPlcmntPerForwardList);
		} else {
			// Merge Placement
			pcspPlcmntPerClosedList.forEach(idPcspPlacement -> {
				if (idPcspPlacement != 0) {
					// update placement if records from both pids aren't
					// overlapping dates
					personMergeSplitDao.updatePcspPlcmntForMerge(idFwdPerson, idPcspPlacement, idClosedPerson);
				}
			});
		}
		// Merge person link
		pcspPersonLinkList.forEach(pcspPrsnLink -> {
			if (pcspPrsnLink.getIdPcspPrsnLink() != 0L) {
				// update pcsp_prsn_link only if the closed pid is the pcsp
				// assessment other household member.
				personMergeSplitDao.updatePcspPrsnLink(idClosedPerson, pcspPrsnLink.getIdPcspPrsnLink(), idFwdPerson, true);
			}
		});
	}

	/**
	 * 
	 * Method Name: mergePersonEligibilities Method Description:This function merges
	 * the person eligibility records from closed person to forward person. This
	 * will also include merging of open as well the history records.
	 * 
	 * @param personMergeSplitDto
	 */
	private void mergePersonEligibilities(PersonMergeSplitDto personMergeSplitDto) {

		ArrayList<PersonEligibility> fwdPersEligList = personEligibilityDao
				.getPersonEligibilityList(personMergeSplitDto.getIdForwardPerson());
		ArrayList<PersonEligibility> closedPersEligList = personEligibilityDao
				.getPersonEligibilityList(personMergeSplitDto.getIdClosedPerson());
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", personMergeSplitDto.getIdForwardPerson()));
		Person person = (Person) criteria.uniqueResult();
		for (PersonEligibility closedPersEligValueBean : closedPersEligList) {
			// Medical Eligibility (MED) is a legacy code that will not be
			// considered or processed for Person Merge.
			// No need to process TLR type of eligibility. There is a batch
			// program which will end the
			// eligibility for closed person and new person added to stage
			// person link
			// skip DHS type of eligibilities as these are handled in separate
			// function
			if (CodesConstant.CEVNTTYP_MED.equals(closedPersEligValueBean.getCdPersEligEligType())
					|| CodesConstant.CCLIELIG_TLR.equals(closedPersEligValueBean.getCdPersEligEligType())
					|| isDHSEligibilityType(closedPersEligValueBean.getCdPersEligEligType()))
				continue;

			Date cs = closedPersEligValueBean.getDtPersEligStart();
			Date ce = closedPersEligValueBean.getDtPersEligEnd();
			// compute eligibiltity end date (lesser of deny and end date)
			// for closed person eligibility

			if (CodesConstant.CCLIELIG_EA.equals(closedPersEligValueBean.getCdPersEligEligType())
					&& !ObjectUtils.isEmpty(closedPersEligValueBean.getDtPersEligEaDeny()) && ((int) DateUtils
							.daysDifference(closedPersEligValueBean.getDtPersEligEaDeny(), MAX_JAVA_DATE) != 0)) {
				ce = closedPersEligValueBean.getDtPersEligEaDeny();
			} else if (!ObjectUtils.isEmpty(closedPersEligValueBean.getDtDeny())
					&& ((int) DateUtils.daysDifference(closedPersEligValueBean.getDtDeny(), MAX_JAVA_DATE) != 0)) {
				ce = closedPersEligValueBean.getDtDeny();
			}

			boolean bInvalidRecord = false;
			// if end date is earlier than start date then it is already invalid
			// in that case this record need to be copied to forward person as
			// it is
			// so we should skip all overlap calculations checking
			if (DateUtils.daysDifference(ce, cs) < 0.0)
				bInvalidRecord = true;

			// check if closed person has current eligibility
			boolean bClosedEligCurrent = false;
			boolean bDiscardClosedElig = false;
			if ((int) DateUtils.daysDifference(ce, new Date()) > 0)
				bClosedEligCurrent = true;

			// now process rest of the eligibilities
			boolean bMatchFound = false;
			if (!bInvalidRecord) {
				for (PersonEligibility fwdPersEligValueBean : fwdPersEligList) {
					if (checkForEquality(closedPersEligValueBean.getCdPersEligEligType(),
							fwdPersEligValueBean.getCdPersEligEligType())) {
						Date ts = fwdPersEligValueBean.getDtPersEligStart();
						Date te = fwdPersEligValueBean.getDtPersEligEnd();
						// compute eligibiltity end date (lesser of deny and end
						// date)
						// for closed person eligibility

						if (CodesConstant.CCLIELIG_EA.equals(fwdPersEligValueBean.getCdPersEligEligType())
								&& !ObjectUtils.isEmpty(fwdPersEligValueBean.getDtPersEligEaDeny())
								&& ((int) DateUtils.daysDifference(fwdPersEligValueBean.getDtPersEligEaDeny(),
										MAX_JAVA_DATE) != 0)) {
							te = fwdPersEligValueBean.getDtPersEligEaDeny();
						} else if (!ObjectUtils.isEmpty(fwdPersEligValueBean.getDtDeny()) && ((int) DateUtils
								.daysDifference(fwdPersEligValueBean.getDtDeny(), MAX_JAVA_DATE) != 0)) {
							te = fwdPersEligValueBean.getDtDeny();
						}

						// if invalid record then continue

						if (!ObjectUtils.isEmpty(te) && DateUtils.daysDifference(te, ts) < 0.0)
							continue;

						// if person closed has current eligibility and if
						// forward
						// person eligibility is current
						// and the start date is same for both and end date of
						// forward
						// person is >= closed then
						// then closed person current eligibility need to be
						// discarded

						if (bClosedEligCurrent && (int) DateUtils.daysDifference(te, new Date()) > 0
								&& (int) DateUtils.daysDifference(cs, ts) == 0
								&& (int) DateUtils.daysDifference(te, ce) >= 0) {
							bDiscardClosedElig = true;
						}

						// Compare based on Eligibility type and start date and
						// end date

						if (checkForEquality(closedPersEligValueBean.getCdPersEligEligType(),
								fwdPersEligValueBean.getCdPersEligEligType())
								&& (int) DateUtils.daysDifference(cs, ts) == 0
								&& (int) DateUtils.daysDifference(ce, te) == 0) {
							bMatchFound = true;
							break;
						}
					}
				}
			}

			if (bDiscardClosedElig)
				continue;

			// if eligibility record is not found in forward person, then add it
			if (!bMatchFound) {
				// In the event an added eligibility would cause an overlap of
				// eligibility for the same type/ same period, we compute the
				// start date
				// end deny date for the closed person record so that it fits
				// well in the fwd person
				// eligibilities gap in a nice way
				boolean bOverlap = false;
				if (!bInvalidRecord) {
					for (PersonEligibility fwdPersEligValueBean : fwdPersEligList) {
						if (checkForEquality(closedPersEligValueBean.getCdPersEligEligType(),
								fwdPersEligValueBean.getCdPersEligEligType())) {

							Date fs = fwdPersEligValueBean.getDtPersEligStart();
							Date fe = fwdPersEligValueBean.getDtPersEligEnd();

							// compute eligibiltity end date (lesser of deny and
							// end
							// date)
							// for forward person eligibility

							if (CodesConstant.CCLIELIG_EA.equals(fwdPersEligValueBean.getCdPersEligEligType())
									&& !ObjectUtils.isEmpty(fwdPersEligValueBean.getDtPersEligEaDeny())
									&& ((int) DateUtils.daysDifference(fwdPersEligValueBean.getDtPersEligEaDeny(),
											MAX_JAVA_DATE) != 0)) {
								fe = fwdPersEligValueBean.getDtPersEligEaDeny();
							} else if (!ObjectUtils.isEmpty(fwdPersEligValueBean.getDtDeny()) && ((int) DateUtils
									.daysDifference(fwdPersEligValueBean.getDtDeny(), MAX_JAVA_DATE) != 0)) {
								fe = fwdPersEligValueBean.getDtDeny();
							}

							// check for overalap

							if ((DateUtils.daysDifference(fs, cs) <= 0.0 && DateUtils.daysDifference(cs, fe) < 0.0
									&& DateUtils.daysDifference(ce, fe) > 0.0)
									|| (DateUtils.daysDifference(fs, cs) >= 0.0
											&& DateUtils.daysDifference(fe, ce) <= 0.0)) {
								cs = DateUtils.addToDate(fe, 0, 0, 1);
								bOverlap = true;
							}

							else if (DateUtils.daysDifference(cs, fs) < 0.0 && DateUtils.daysDifference(ce, fs) > 0.0
									&& DateUtils.daysDifference(ce, fe) <= 0.0) {
								ce = DateUtils.addToDate(fs, 0, 0, -1);
								bOverlap = true;
							}

							else if (DateUtils.daysDifference(fs, cs) <= 0.0
									&& DateUtils.daysDifference(fe, ce) >= 0.0) {
								// basically mark the record as invalid
								ce = DateUtils.addToDate(cs, 0, 0, -1);
								bOverlap = true;
								break;
							}
						}
					} // end of for loop for overlapping comparison
				}

				// if overlap was found for eligibility records from closed
				// person, then
				// set the deny date for it before adding the person eligibility
				// record to the forward person
				if (bOverlap) {
					// if computed end date on closed record is MAX date, then
					// we
					// need to add the record as invalid (only in case of
					// overlap)
					// The reason being we dont want to add new open eligibility
					// for forward person
					// in case person forward has an overlapping closed
					// eligibility

					if ((int) DateUtils.daysDifference(ce, MAX_JAVA_DATE) == 0) {
						ce = DateUtils.addToDate(cs, 0, 0, -1);
						ce = cs;
					}
					// if end date is less that start date, it means we need to
					// add the closed person record with original dates but a
					// new deny date
					// which is one day earlier that start date (basically
					// adding an invalid record)

					if (DateUtils.daysDifference(ce, cs) < 0.0) {
						if (CodesConstant.CCLIELIG_EA.equals(closedPersEligValueBean.getCdPersEligEligType())) {
							closedPersEligValueBean.setDtPersEligEaDeny(closedPersEligValueBean.getDtPersEligStart());
							closedPersEligValueBean.setDtDeny(null);
						} else {
							closedPersEligValueBean.setDtDeny(closedPersEligValueBean.getDtPersEligStart());
							closedPersEligValueBean.setDtPersEligEaDeny(null);
						}
					} else {
						closedPersEligValueBean.setDtPersEligStart(cs);
						if (CodesConstant.CCLIELIG_EA.equals(closedPersEligValueBean.getCdPersEligEligType())) {
							closedPersEligValueBean.setDtPersEligEaDeny(ce);
							closedPersEligValueBean.setDtDeny(null);
						} else {
							// if calculated end date and existing end
							// date are not same then set deny date
							if (DateUtils.daysDifference(ce, closedPersEligValueBean.getDtPersEligEnd()) != 0)
								closedPersEligValueBean.setDtDeny(ce);
							closedPersEligValueBean.setDtPersEligEaDeny(null);
						}
					}

				}
				// also fetch the person eligibility detail record for this
				// eligibility
				// record for closed person
				ArrayList<PersonEligibilityDetail> persEligDtlList = personEligibilityDao
						.getPersonEligibilityDetailList(closedPersEligValueBean.getIdPersElig());

				closedPersEligValueBean.setPerson(person);
				PersonEligibility closePersonEligibility = new PersonEligibility();
				BeanUtils.copyProperties(closedPersEligValueBean, closePersonEligibility);
				closePersonEligibility.setIdPersElig(0l);
				personEligibilityDao.savePersonEligibility(closePersonEligibility);

				// Add this newly added record on forward person to the fwd
				// person eligibility list
				// This is need to ensure this gets used in next comparison.
				fwdPersEligList.add(closedPersEligValueBean);

				// save person eligibility detail record for forward person
				persEligDtlList.stream().forEach(closedPersEligDtlBean -> {
					closedPersEligDtlBean.setPerson(person);
					Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(PersonEligibility.class)
							.add(Restrictions.eq("idPersElig", closedPersEligValueBean.getIdPersElig()));
					PersonEligibility personEligibility = (PersonEligibility) criteria1.uniqueResult();
					closedPersEligDtlBean.setPersonEligibility(personEligibility);
					PersonEligibilityDetail personEligibilityDetail = new PersonEligibilityDetail();
					BeanUtils.copyProperties(closedPersEligDtlBean, personEligibilityDetail);
					personEligibilityDetail.setIdPersEligDetail(0l);
					personEligibilityDao.savePersonEligibilityDetail(personEligibilityDetail);
				});

				// Record any Person Eligibilities update into person merge
				// update log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_430))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_430, ServiceConstants.IND_TRUE);

			} // end of IF when no matching record was found
		}
	}

	/**
	 * This function merges the DHS type person eligibility records from closed
	 * person to forward person.
	 * 
	 * @param Connection
	 * @param personMergeSplitDto
	 * @return
	 */
	private void mergeDHSPersonEligibilities(PersonMergeSplitDto personMergeSplitDto) {

		ArrayList<PersonEligibility> fwdPersEligList = personEligibilityDao
				.getPersonEligibilityList(personMergeSplitDto.getIdForwardPerson());
		ArrayList<PersonEligibility> closedPersEligList = personEligibilityDao
				.getPersonEligibilityList(personMergeSplitDto.getIdClosedPerson());
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", personMergeSplitDto.getIdForwardPerson()));
		Person person = (Person) criteria.uniqueResult();
		for (PersonEligibility closedPersEligValueBean : closedPersEligList) {
			// Skip person eligibilities other than DHS as those are handled in
			// different method
			if (!isDHSEligibilityType(closedPersEligValueBean.getCdPersEligEligType()))
				continue;

			// now process DHS Eligibilities
			boolean bOverlappingType = false;
			PersonEligibility fwdOverlappingPersEligValueBean = new PersonEligibility();
			// Compare based on Eligibility type and start date and end date
			boolean bMatchFound = (fwdPersEligList.stream()
					.anyMatch(fwdPersEligValueBean -> isDHSEligibilityType(fwdPersEligValueBean.getCdPersEligEligType())
							&& (checkForEquality(closedPersEligValueBean.getCdPersEligEligType(),
									fwdPersEligValueBean.getCdPersEligEligType())
									&& (int) DateUtils.daysDifference(closedPersEligValueBean.getDtPersEligStart(),
											fwdPersEligValueBean.getDtPersEligStart()) == 0
									&& (int) DateUtils.daysDifference(closedPersEligValueBean.getDtPersEligEnd(),
											fwdPersEligValueBean.getDtPersEligEnd()) == 0))) ? true : false;
			// Compare based on start date and end date
			if (!bMatchFound) {
				Optional<PersonEligibility> personEligibilityOptional = fwdPersEligList.stream().filter(
						fwdPersEligValueBean -> (isDHSEligibilityType(fwdPersEligValueBean.getCdPersEligEligType()))
								&& ((int) DateUtils.daysDifference(closedPersEligValueBean.getDtPersEligStart(),
										fwdPersEligValueBean.getDtPersEligStart()) == 0
										&& (int) DateUtils.daysDifference(closedPersEligValueBean.getDtPersEligEnd(),
												fwdPersEligValueBean.getDtPersEligEnd()) == 0))
						.findFirst();
				if (personEligibilityOptional.isPresent()) {
					bOverlappingType = true;
					fwdOverlappingPersEligValueBean = personEligibilityOptional.get();
				}
			}
			// if no match found, the add the eligibility to forward person
			if (!bMatchFound && !bOverlappingType) {
				// also fetch the person eligibility detail record for this
				// eligibility
				// record for closed person
				ArrayList<PersonEligibilityDetail> persEligDtlList = personEligibilityDao
						.getPersonEligibilityDetailList(closedPersEligValueBean.getIdPersElig().intValue());
				closedPersEligValueBean.setPerson(person);

				PersonEligibility closePersonEligibility = new PersonEligibility();
				BeanUtils.copyProperties(closedPersEligValueBean, closePersonEligibility);
				closePersonEligibility.setIdPersElig(0l);
				personEligibilityDao.savePersonEligibility(closePersonEligibility);

				// save person eligibility detail record for forward person
				persEligDtlList.stream().forEach(closedPersEligDtlBean -> {
					closedPersEligDtlBean.setPerson(person);
					Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(PersonEligibility.class)
							.add(Restrictions.eq("idPersElig", closedPersEligValueBean.getIdPersElig()));
					PersonEligibility personEligibility = (PersonEligibility) criteria1.uniqueResult();
					closedPersEligDtlBean.setPersonEligibility(personEligibility);
					PersonEligibilityDetail personEligibilityDetail = new PersonEligibilityDetail();
					BeanUtils.copyProperties(closedPersEligDtlBean, personEligibilityDetail);
					personEligibilityDetail.setIdPersEligDetail(0l);
					personEligibilityDao.savePersonEligibilityDetail(personEligibilityDetail);
				});

				// Record any Person Eligibilities update into person merge
				// update log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_430))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_430, ServiceConstants.IND_TRUE);
			}
			// in this case do the ranking for eligibility type
			else if (!bMatchFound && bOverlappingType
					&& getDHSEligibilityRank(closedPersEligValueBean.getCdPersEligEligType()) > getDHSEligibilityRank(
							fwdOverlappingPersEligValueBean.getCdPersEligEligType())) {
				// If Person Closed Rank is higher than Person Forward
				// Replace the corresponding record to Person Forward with the
				// Person Closed record.
				// If person forward rank is higher, then do nothing
				// fetch the person eligibility detail record for
				// eligibility record from closed person
				ArrayList<PersonEligibilityDetail> closedPersEligDtlList = personEligibilityDao
						.getPersonEligibilityDetailList(closedPersEligValueBean.getIdPersElig().intValue());

				// Delete the person eligibility detail record for forward
				// person
				// and add them from closed person

				personEligibilityDao.deleteDetailsForPersonEligibility(fwdOverlappingPersEligValueBean.getIdPersElig());

				// replace forward person eligibility record with closed
				// record

				fwdOverlappingPersEligValueBean.setCdPersEligEligType(closedPersEligValueBean.getCdPersEligEligType());
				fwdOverlappingPersEligValueBean
						.setCdPersEligPrgClosed(closedPersEligValueBean.getCdPersEligPrgClosed());
				fwdOverlappingPersEligValueBean.setCdPersEligPrgOpen(closedPersEligValueBean.getCdPersEligPrgOpen());
				fwdOverlappingPersEligValueBean.setCdPersEligPrgStart(closedPersEligValueBean.getCdPersEligPrgStart());
				fwdOverlappingPersEligValueBean.setDtDeny(closedPersEligValueBean.getDtDeny());
				fwdOverlappingPersEligValueBean.setDtPersEligEaDeny(closedPersEligValueBean.getDtPersEligEaDeny());
				fwdOverlappingPersEligValueBean.setDtPersEligEnd(closedPersEligValueBean.getDtPersEligEnd());
				fwdOverlappingPersEligValueBean.setDtPersEligStart(closedPersEligValueBean.getDtPersEligStart());
				fwdOverlappingPersEligValueBean.setDtPurged(closedPersEligValueBean.getDtPurged());
				fwdOverlappingPersEligValueBean.setIndPersEligMhmr(closedPersEligValueBean.getIndPersEligMhmr());

				personEligibilityDao.updatePersonEligibility(fwdOverlappingPersEligValueBean);

				// save person eligibility detail record for forward person
				for (PersonEligibilityDetail closedPersEligDtlBean : closedPersEligDtlList) {
					// closedPersEligValueBean.getPersonEligibilityId() has
					// forward person elig Id now
					closedPersEligDtlBean.setPerson(person);
					Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(PersonEligibility.class)
							.add(Restrictions.eq("idPersElig", fwdOverlappingPersEligValueBean.getIdPersElig()));
					PersonEligibility personEligibility = (PersonEligibility) criteria1.uniqueResult();
					closedPersEligDtlBean.setPersonEligibility(personEligibility);
					PersonEligibilityDetail personEligibilityDetail = new PersonEligibilityDetail();
					BeanUtils.copyProperties(closedPersEligDtlBean, personEligibilityDetail);
					personEligibilityDetail.setIdPersEligDetail(0l);
					personEligibilityDao.savePersonEligibilityDetail(personEligibilityDetail);
				}

				// Record any Person Eligibilities update into person merge
				// update log
				if (!personMergeSplitDto.getUpdateLogMap().containsKey(CodesConstant.CPMFLDCT_430))
					personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_430, ServiceConstants.IND_TRUE);
			}
		}

	}

	/**
	 * This function checks if a particular eligibility is DHS type
	 * 
	 * @param cdPersonEligType
	 * @return boolean
	 */
	private boolean isDHSEligibilityType(String cdPersonEligType) {

		// Skip person eligibilities other than DHS as those are handled in
		// different method
		return (CodesConstant.CCLIELIG_001.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_002.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_003.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_004.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_005.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_006.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_007.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_008.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_009.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_010.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_011.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_012.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_013.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_014.equals(cdPersonEligType)
				|| CodesConstant.CCLIELIG_015.equals(cdPersonEligType)) ? true : false;
	}

	/**
	 * This function returns the ranking order of a DHS type eligibility
	 * 
	 * @param cdPersonEligType
	 * @return
	 */
	private int getDHSEligibilityRank(String cdPersonEligType) {
		int pos = 0;
		switch (cdPersonEligType) {
		case CodesConstant.CCLIELIG_014:
			pos = 1;
			break;
		case CodesConstant.CCLIELIG_011:
			pos = 2;
			break;
		case CodesConstant.CCLIELIG_013:
			pos = 3;
			break;
		case CodesConstant.CCLIELIG_010:
			pos = 4;
			break;
		case CodesConstant.CCLIELIG_004:
			pos = 5;
			break;
		case CodesConstant.CCLIELIG_005:
			pos = 6;
			break;
		case CodesConstant.CCLIELIG_006:
			pos = 7;
			break;
		case CodesConstant.CCLIELIG_001:
			pos = 8;
			break;
		case CodesConstant.CCLIELIG_012:
			pos = 9;
			break;
		case CodesConstant.CCLIELIG_008:
			pos = 10;
			break;
		case CodesConstant.CCLIELIG_009:
			pos = 11;
			break;
		case CodesConstant.CCLIELIG_007:
			pos = 12;
			break;
		case CodesConstant.CCLIELIG_002:
			pos = 13;
			break;
		case CodesConstant.CCLIELIG_003:
			pos = 14;
			break;
		case CodesConstant.CCLIELIG_015:
			pos = 15;
			break;
		default:
			pos = 0;
			break;
		}

		return (-1) * pos;
	}

	/**
	 * 
	 * Method Name: processOpenStagePersonData Method Description:This method
	 * updates the forward Person ID on various data which contain the closed Person
	 * ID. Only the data pertaining to open stages is updated.
	 * 
	 * @param PersonMergeSplitDB
	 */
	@Override
	public void processOpenStagePersonData(PersonMergeSplitDto personMergeSplitDto) {

		List<StagePersDto> closedPersOpenStgList = stageDao
				.getStagesByIdPerson(personMergeSplitDto.getIdClosedPerson());
		
		/** Fix for ALM Defect#13464: person merge  should not update the  ALLEGATION records after stage closure */
		if(null != closedPersOpenStgList) {
		 closedPersOpenStgList = closedPersOpenStgList.stream().filter(stagePersDto -> stagePersDto.getDtStageClose() == null).collect(Collectors.toList());
		}
		 //End of fix for ALM Defect#13464

		// if there are no open stages for the person (closed) then no needto //
		// proceed
		if (CollectionUtils.isEmpty(closedPersOpenStgList))
			return;
		
		

		// update the ID_VICTIM and ID_PERPETRATOR with forward person if they
		// // exist // as closed person
		closedPersOpenStgList.stream().forEach(stagePersDto -> {
			allegationDao.updateAllegationVictim(stagePersDto.getIdStage(), personMergeSplitDto.getIdClosedPerson(),
					personMergeSplitDto.getIdForwardPerson(), personMergeSplitDto.getIdPersonMergeWorker());
			allegationDao.updateAllegationPerpetrator(stagePersDto.getIdStage(),
					personMergeSplitDto.getIdClosedPerson(), personMergeSplitDto.getIdForwardPerson(),
					personMergeSplitDto.getIdPersonMergeWorker());
		});

		// fetch duplicate allegations
		List<AllegationDto> dupAllgList = allegationDao
				.getDuplicateAllegations(personMergeSplitDto.getIdForwardPerson());

		// Following logic will decide which allegations are duplicates and
		// delete // them. If the cd_stage are the same // then it will
		// delete the newest allegation. If the cd_stages are // different, then
		// it will delete the INV allegation.
		for (AllegationDto allgValueBean : dupAllgList) {

			if (CodesConstant.CSTAGES_INT.equals(allgValueBean.getCdAllegIncidentStage())
					&& CodesConstant.CSTAGES_INV.equals(allgValueBean.getCdAllegIncidentStage2())) {
				Allegation allgToDel = new Allegation();
				allgToDel.setIdAllegation(allgValueBean.getIdAllegation2());
				allgToDel.setDtLastUpdate(allgValueBean.getDtLastUpdate2());

                //delete referenced allegation from LetterAllegationLink before deleting from allegation table
				letterAllegationLinkDao.deleteOrUpdateLetterAllegationLink(allgToDel.getIdAllegation(),
						allgValueBean.getIdAllegation(), personMergeSplitDto.getIdPersonMergeWorker());

				// delete the duplicate allegation
				allegationDao.deleteAllegation(allgToDel);
				// Reset the deleted IdAllegation to 0. This allows later to //
				// know which Allegations have been deleted.
				allgValueBean.setIdAllegation2(0l);
			} else if (CodesConstant.CSTAGES_INT.equals(allgValueBean.getCdAllegIncidentStage2())
					&& CodesConstant.CSTAGES_INV.equals(allgValueBean.getCdAllegIncidentStage())) {
				Allegation allgToDel = new Allegation();
				allgToDel.setIdAllegation(allgValueBean.getIdAllegation());
				allgToDel.setDtLastUpdate(allgValueBean.getDtLastUpdate());

				//delete or update referenced allegation from LetterAllegationLink before deleting from allegation table
				letterAllegationLinkDao.deleteOrUpdateLetterAllegationLink(allgToDel.getIdAllegation(),
						allgValueBean.getIdAllegation2(), personMergeSplitDto.getIdPersonMergeWorker());

				// delete the duplicate allegation
				allegationDao.deleteAllegation(allgToDel);
				// Reset the deleted IdAllegation to 0. This allows later to
				// know which Allegations have been deleted.
				allgValueBean.setIdAllegation(0l);
			} else if (allgValueBean.getDtLastUpdate2().getTime() > allgValueBean.getDtLastUpdate().getTime()) {
				Allegation allgToDel = new Allegation();
				allgToDel.setIdAllegation(allgValueBean.getIdAllegation2());
				allgToDel.setDtLastUpdate(allgValueBean.getDtLastUpdate2());

				//delete or update referenced allegation from LetterAllegationLink before deleting from allegation table
				letterAllegationLinkDao.deleteOrUpdateLetterAllegationLink(allgToDel.getIdAllegation(),
						allgValueBean.getIdAllegation(), personMergeSplitDto.getIdPersonMergeWorker());

				// delete the duplicate allegation
				allegationDao.deleteAllegation(allgToDel);
				// Reset the deleted IdAllegation to 0. This allows later to
				// know which Allegations have been deleted.
				allgValueBean.setIdAllegation2(0l);
			} else if (allgValueBean.getDtLastUpdate().getTime() >= allgValueBean.getDtLastUpdate2().getTime()) {
				Allegation allgToDel = new Allegation();
				allgToDel.setIdAllegation(allgValueBean.getIdAllegation());
				allgToDel.setDtLastUpdate(allgValueBean.getDtLastUpdate());

				//delete or update referenced allegation from LetterAllegationLink before deleting from allegation table
				letterAllegationLinkDao.deleteOrUpdateLetterAllegationLink(allgToDel.getIdAllegation(),
						allgValueBean.getIdAllegation2(), personMergeSplitDto.getIdPersonMergeWorker());

				// delete the duplicate allegation
				allegationDao.deleteAllegation(allgToDel);
				// Reset the deleted IdAllegation to 0. This allows later to
				// know which Allegations have been deleted.
				allgValueBean.setIdAllegation(0l);
			}

		} // end of for loop for deleting allegation

		// if there were duplicate allegations, then blank out following fields
		// from
		// allegation Allegation Severity (CD_ALLEG_SEVERITY),
		// Rationale for the Disposition based upon Severity
		// TXT_DISPSTN_SEVERITY)
		// IND_CO_SLPG_CHILD_DTH &IND_CO_SLPG_SUBSTANCE
		// SIR 1005875, also null the IND_FATALITY and set the last update
		// person ID.
		for (AllegationDto allgValueBean : dupAllgList) {
			// Commenting the below code as this logic is moved to the allegation Dao.
			/*
			 * Allegation allgToReset = new Allegation();
			 * allgToReset.setIdLastUpdatePerson(personMergeSplitDto.getIdPersonMergeWorker(
			 * ));
			 * 
			 * if (allgValueBean.getIdAllegation() != 0) {
			 * allgToReset.setIdAllegation(allgValueBean.getIdAllegation());
			 * allgToReset.setDtLastUpdate(allgValueBean.getDtLastUpdate()); } else if
			 * (allgValueBean.getIdAllegation2() != 0) {
			 * allgToReset.setIdAllegation(allgValueBean.getIdAllegation2());
			 * allgToReset.setDtLastUpdate(allgValueBean.getDtLastUpdate2()); } else
			 * continue; allgToReset.setPersonByIdVictim((Person)
			 * sessionFactory.getCurrentSession().get(Person.class,
			 * allgValueBean.getIdVictim())); allgToReset.setStage((Stage)
			 * sessionFactory.getCurrentSession().get(Stage.class,
			 * allgValueBean.getIdStage()));
			 */
			// call dao to reset the fields
			allegationDao.resetDispositionAndSeverity(allgValueBean, personMergeSplitDto.getIdPersonMergeWorker());
		}
		// end duplicate allegations loop

		// If the Merge resulted in duplicate allegation(s) in an open stage.
		// The duplicate was removed, any disposition, severity, supporting
		// rationale & other questions which may have been entered have also
		// been removed from the remaining allegation. Set the post merge
		// information message
		if (CollectionUtils.isNotEmpty(dupAllgList)) {
			MergeSplitVldMsgDto infoMsg = new MergeSplitVldMsgDto();
			infoMsg.setMessageInt(Messages.MSG_POST_MERGE_ALLEG);
			personMergeSplitDto.getPostMergeInfoDataList().add(infoMsg);
		}

		// fetch the events related to open stages

		List<EventDto> openStgEventList = eventPersonDao
				.getOpenStageEventsForAPerson(personMergeSplitDto.getIdClosedPerson());
		openStgEventList.stream().forEach(eplValueBean -> {
			if (isValid(eplValueBean.getCdTask())) {
				int taskCode = (new Integer(eplValueBean.getCdTask())).intValue();
				if (taskCode == 9234 // (SUB - Medical Consenter)
						|| taskCode == 9232 // (ADO - Medical Consenter)
						|| taskCode == 7060 // (FPR - Family Assessment)
						|| taskCode == 4140 // (FSU - Family Assessment)
						|| taskCode == 5590 // (FRE - Family Assessment)
						|| taskCode == 2350 // (CPS/INV - Conservatorship Removal)
						|| taskCode == 5830 // (CPS/FRE - Conservatorship Removal)
						|| taskCode == 4330 // (CPS/FSU - Conservatorship Removal)
						|| taskCode == 7190 // (CPS/FPR - Conservatorship Removal)
						|| taskCode == 8560 // (CPS/ADO - Legal Status)
						|| taskCode == 5870 // (CPS/FRE - Legal Status)
						|| taskCode == 4370 // (CPS/FSU - Legal Status)
						|| taskCode == 3050 // (CPS/SUB - Legal Status)
						|| taskCode == 2375 // (CPS/INV - Legal Status)
						|| taskCode == 9050 // (CPS/PAD - Legal Status)
						|| taskCode == 7230 // (CPS/FPR - Legal Status)
						|| taskCode == 8540 // (CPS/ADO - Legal Actions)
						|| taskCode == 7210 // (CPS/FPR - Legal Actions)
						|| taskCode == 4360 // (CPS/FSU - Legal Actions)
						|| taskCode == 6130 // (APS/SVC - Legal Actions)
						|| taskCode == 2145 // (APS/INV - Legal Actions)
						|| taskCode == 3030 // (CPS/SUB - Legal Actions)
						|| taskCode == 4350 // (CPS/FSU - Legal Actions)
						|| taskCode == 5850 // (CPS/FRE - Legal Actions)
						|| taskCode == 2355 // (CPS/INV - Legal Actions)
						|| taskCode == 5050 // (APS/AOC - Legal Actions)
						|| taskCode == 2140 // (APS/INV - Guardianship Detail)
						|| taskCode == 6120 // (APS/SVC - Guardianship Detail)
						|| taskCode == 6125 // (APS/AOC - Guardianship Detail)
						|| taskCode == 3010 // (CPS/SUB - Contacts/Summaries) (Kinship Notification)
						|| taskCode == 4120 // (CPS/FSU - Contacts/Summaries)
						|| taskCode == 5570 // (CPS/FRE - Contacts/Summaries)
						|| taskCode == 3160 // (CPS/SUB - Child Service Plan (Substitute Care))
						|| taskCode == 8660 // (CPS/ADO - Child Service Plan (Adoption))
						|| taskCode == 3180 // (CPS/SUB - Permanency Planning Meeting (PPM))
						|| taskCode == 8680 // (CPS/ADO - Permanency Planning Meeting (PPM))
						|| taskCode == 3080 // (CPS/SUB - Placement)
						|| taskCode == 9570 // (CPS/PCA - Placement)
						|| taskCode == 8590 // (CPS/ADO - Placement)
						|| taskCode == 9080 // (CPS/PAD - Placement)
						|| taskCode == 3430 // (CPS/SUB - Foster Care Application)
						|| taskCode == 3120 // (CPS/SUB - Eligibility Summary)
						|| taskCode == 3440 // (CPS/SUB - Foster Care Review)
						|| taskCode == 4150 // (CPS/FSU - Family Plan)
						|| taskCode == 7080 // (CPS/FPR - Family Plan)
						|| taskCode == 5600 // (CPS/FRE - Family Plan)
						|| taskCode == 7300 // (CPS/FPR - Family Plan Evaluation)
						|| taskCode == 7310 // (CPS/FRE - Family Plan Evaluation)
						|| taskCode == 7320 // (CPS/FSU - Family Plan Evaluation)
						|| taskCode == 9620 // (CPS/SUB - ICPC)
						|| taskCode == 9621 // (CPS/ADO - ICPC)
						|| taskCode == 9622 // (CPS/SUB - ICPC)
						|| taskCode == 9623 // (CPS/ADO - ICPC)
						|| taskCode == 9581 // (CPS/PCA - PCA application)
						|| taskCode == 9601 // (CPS/SUB - PCA application)
						|| taskCode == 9101 // (CPS/PAD - Adoption Assistance application)
						|| taskCode == 8611 // (CPS/ADO - Adoption Assistance application)
						|| taskCode == 2315 // (CPS/INV - Daycare request)
						|| taskCode == 7115 // (CPS/FPR - Daycare request)
						|| taskCode == 5645 // (CPS/FRE - Daycare request)
						|| taskCode == 4195 // (CPS/FSU - Daycare request)
						|| taskCode == 3025 // (CPS/SUB - Daycare request)
						|| taskCode == 9880 // (CPS/A-R - Daycare request)
						|| taskCode == 7400 // (CPS/FSU - SDM FSNA)
						|| taskCode == 7410 // (CPS/FRE - SDM FSNA)
						|| taskCode == 7420 // (CPS/FPR - SDM FSNA)
						|| taskCode == 7490 // (CPS/FSU - SDM Reunification Assessment)
						|| taskCode == 7470 // (CPS/FRE - SDM Risk Reassessment)
						|| taskCode == 7480 // (CPS/FPR - SDM Risk Reassessment)
						|| ServiceConstants.CD_TASK_SA_AR.equals(eplValueBean.getCdTask()) // (CPS/AR - SDM Safety Assessment)
						|| ServiceConstants.CD_TASK_SA.equals(eplValueBean.getCdTask()) // (CPS/INV - SDM Safety Assessment)
						|| ServiceConstants.TASK_7430.equals(eplValueBean.getCdTask()) // (CPS/FSU - SDM Safety Assessment)
						|| ServiceConstants.TASK_7440.equals(eplValueBean.getCdTask()) // (CPS/FRE - SDM Safety Assessment)
						|| ServiceConstants.TASK_7450.equals(eplValueBean.getCdTask()) // (CPS/FPR - SDM Safety Assessment)
						|| ServiceConstants.CD_TASK_RA.equals(eplValueBean.getCdTask()) // (CPS/INV - SDM Risk Assessment)
						|| ServiceConstants.CD_TASK_RA_AR.equals(eplValueBean.getCdTask()) // (CPS/AR - SDM Risk Assessment)
						|| ServiceConstants.FBSS_REF_TASK_CODE_CPSINV.equals(eplValueBean.getCdTask()) // INV FBSS Created after 09/10/2020 rel
						|| ServiceConstants.FBSS_REF_TASK_CODE_CPSAR.equals(eplValueBean.getCdTask()) // A-R FBSS Created after 09/10/2020 rel
				) {
					personMergeSplitDao.updatePersonClosedWithForward(
							(new Integer(eplValueBean.getCdTask())).intValue(), eplValueBean.getCdEventType(),
							eplValueBean.getIdEvent(), eplValueBean.getIdStage(), eplValueBean.getEventDescr(),
							personMergeSplitDto.getIdForwardPerson(), personMergeSplitDto.getIdClosedPerson());
				}
			}
		});

		// end loop thru open stage events

		// Determine if both the Person Closed and Person Forward are both
		// listed in
		// any Open Stage(s). If so, after processing, this would result in a
		// duplicate.
		// Fetch list of open stages which have both forward and closed persons
		ArrayList<StageDto> arrOpenStageList = stageDao.getOpenStagesForWithPersons(
				personMergeSplitDto.getIdForwardPerson(), personMergeSplitDto.getIdClosedPerson());
		for (StageDto stageDto : arrOpenStageList) {
			Long idOpenStage = stageDto.getIdStage();
			// fetch the list of persons in the stage in a special order of Role
			ArrayList<StagePersonValueDto> splList = stageDao.getStagePersonListByRole(idOpenStage);
			int x = 0;
			int rowToUpdate = -1;
			StagePersonValueDto dataToUpdate = new StagePersonValueDto();

			for (int j = 0; j < splList.size(); j++) {
				StagePersonValueDto spl = splList.get(j);
				if ((spl.getIdPerson().equals(personMergeSplitDto.getIdForwardPerson()))
						|| (spl.getIdPerson().equals(personMergeSplitDto.getIdClosedPerson()))) {
					// If the stage person record we delete has some of the
					// attributes
					// which need to be carried over to final record in SPL then
					// we store them in dataToUpdate object

					// if person is a PRINCIPAL then we need to store it
					if ("PRN".equals(spl.getCdStagePersType())) {
						dataToUpdate.setCdStagePersType(spl.getCdStagePersType());
					}

					// Set the indicator if NmStage indicator is set
					if (!ObjectUtils.isEmpty(spl.getIndNmStage()) && spl.getIndNmStage().equals(1l))
						dataToUpdate.setIndNmStage(1l);

					// Set the indicator if person is primary kinship caregiver
					if (!ObjectUtils.isEmpty(spl.getIndKinPrCaregiver())
							&& ServiceConstants.ARCHITECTURE_CONS_Y.equals(spl.getIndKinPrCaregiver())) {
						dataToUpdate.setIndKinPrCaregiver(spl.getIndKinPrCaregiver());
					}
					// Set the indicator if InLaw indicator is set
					if (!ObjectUtils.isEmpty(spl.getIndStagePersInLaw())
							&& ServiceConstants.ARCHITECTURE_CONS_Y.equals(spl.getIndStagePersInLaw())) {
						dataToUpdate.setIndStagePersInLaw(spl.getIndStagePersInLaw());
					}
					// Set the indicator if Reporter indicator is set
					if (!ObjectUtils.isEmpty(spl.getIndStagePersReporter())
							&& ServiceConstants.ARCHITECTURE_CONS_Y.equals(spl.getIndStagePersReporter())) {
						dataToUpdate.setIndStagePersReporter(spl.getIndStagePersReporter());
					}
					// Set the indicator if stage person notes are set
					if (!ObjectUtils.isEmpty(spl.getStagePersNotes())) {
						dataToUpdate.setStagePersNotes(spl.getStagePersNotes());
					}
					// Set the indicator if caring adult indicator is set
					if (!ObjectUtils.isEmpty(spl.getIndCaringAdult())
							&& ServiceConstants.ARCHITECTURE_CONS_Y.equals(spl.getIndCaringAdult())) {
						dataToUpdate.setIndCaringAdult(spl.getIndCaringAdult());
					}
					// Set the indicator if NYTD Contact indicator is set
					if (!ObjectUtils.isEmpty(spl.getIndNytdContact())
							&& ServiceConstants.ARCHITECTURE_CONS_Y.equals(spl.getIndNytdContact())) {
						dataToUpdate.setIndNytdContact(spl.getIndNytdContact());
					}
					// Set the indicator if NYTD Contact Primary indicator is
					// set
					if (!ObjectUtils.isEmpty(spl.getIndNytdContactPrimary())
							&& ServiceConstants.ARCHITECTURE_CONS_Y.equals(spl.getIndNytdContactPrimary())) {
						dataToUpdate.setIndNytdContactPrimary(spl.getIndNytdContactPrimary());
					}

					if (x == 0) {
						rowToUpdate = j;
					} else {
						// delete the duplicate rows from stage_person_link
						stageDao.deleteStagePersonLink(spl);
						// if the both Person Merge candidates are in the same
						// Open Stage
						// and as a result of processing, the role of the Person
						// Forward
						// in the stage is updated, then set a post merge
						// information message
						if (spl.getIdPerson() == personMergeSplitDto.getIdForwardPerson()) {
							MergeSplitVldMsgDto infoMsg = new MergeSplitVldMsgDto();
							infoMsg.setMessageInt(Messages.MSG_POST_MERGE_ROLE);
							ArrayList<String> embedData = new ArrayList<>();
							embedData.add(String.valueOf(spl.getIdStage()));
							infoMsg.setMessageDataList(embedData);
							personMergeSplitDto.getPostMergeInfoDataList().add(infoMsg);
						}
					}
					x++;
				}
			}
			// update the forward person on closed person in remaining
			// stage_person_link after the delete. Also update few other
			// columns in this state_person_link
			if (rowToUpdate >= 0) {
				StagePersonValueDto spl = (StagePersonValueDto) splList.get(rowToUpdate);
				personMergeSplitDao.updatePersonOnStagePersonLink(personMergeSplitDto.getIdForwardPerson(),
						spl.getIdStagePersonLink(), dataToUpdate);
			}
		}

		// Update event_person_link rows for open stages for closed person
		personMergeSplitDao.updatePersonOnEventPersonLink(personMergeSplitDto.getIdForwardPerson(),
				personMergeSplitDto.getIdClosedPerson());

		// Update stage_person_link rows for open stages for closed person
		personMergeSplitDao.updatePersonOnStagePersonLink(personMergeSplitDto.getIdForwardPerson(),
				personMergeSplitDto.getIdClosedPerson());

		//Update substance tracker rows for open CPS-INV stages for closed person
		closedPersOpenStgList.stream()
				.filter(stagePersDto -> CodesConstant.CSTAGES_INV.equalsIgnoreCase(stagePersDto.getCdStage()) &&
						CodesConstant.CPGRMS_CPS.equalsIgnoreCase(stagePersDto.getCdStageProgram()))
				.forEach(stagePersDto -> cpsInvCnlsnDao.updateSubstanceTrackerForPersonMerge(stagePersDto.getIdStage(),
						personMergeSplitDto.getIdClosedPerson(),personMergeSplitDto.getIdForwardPerson()));

		Set<Long> closedPersonOpenStageIds = closedPersOpenStgList.stream().map(StagePersDto::getIdStage).collect(Collectors.toSet());

		cpsInvCnlsnDao.updateVictimOnClosureLetters(closedPersonOpenStageIds, personMergeSplitDto.getIdPersonMergeWorker(),
				personMergeSplitDto.getIdClosedPerson(), personMergeSplitDto.getIdForwardPerson());

		personMergeSplitDao.updatePersonOnLetterAllegationLink(closedPersonOpenStageIds, personMergeSplitDto.getIdPersonMergeWorker(),
				personMergeSplitDto.getIdClosedPerson(), personMergeSplitDto.getIdForwardPerson());

	}

	/**
	 *
	 * Method Name: processSSCCPersonData Method Description:Merge SSCC Records in
	 * OPEN Stages. Gets all the Referrals (Active and Inactive) for the Closed
	 * Person in any Open Stage, and update Closed Person ID with Forward Person ID
	 * for these tables.
	 *
	 * @param persMergeValueBean
	 */
	public void processSSCCPersonData(PersonMergeSplitDto persMergeValueBean) {

		int idClosedPerson = persMergeValueBean.getIdClosedPerson().intValue();
		int idFwdPerson = persMergeValueBean.getIdForwardPerson().intValue();
		ssccPersonMergeSplitDao.updatePersonOnSSCCPlcmtRsrcLinkMC(idClosedPerson, idFwdPerson);

		// Fetch the Referrals (Active and Inactive) in all the OPEN stages for
		// Closed Person. // Closed Person must be on Person List, Stage must be
		// Open.
		// This Referrals will be used to process // SCC Referral Family
		// Records, SSCC Placement Circumstance, SSCC Child
		// Plan
		List<BigDecimal> ssccRefList = ssccPersonMergeSplitDao.fetchSSCCReferralsForPersonInOpenStages(idClosedPerson,
				false);

		// For Each Referral update all the Records in each of the following
		// sections.
		ssccRefList.stream().forEach(idSsccReferralObj -> {
			int idSsccReferral = (idSsccReferralObj != null) ? idSsccReferralObj.intValue() : 0;

			// Update all SCC Referral Family Records where ID_PERSON is Closed
			// Person.
			ssccPersonMergeSplitDao.updatePersonOnSSCCRefFamily(idSsccReferral, idClosedPerson, idFwdPerson);

			// Update all SSCC Placement Circumstance records ID_PLCMT_PERSON is
			// Closed Person.
			ssccPersonMergeSplitDao.updatePersonOnSSCCPlcmtCircumstance(idSsccReferral, idClosedPerson, idFwdPerson);

			// Update all SSCC Child Plan Records where ID_PERSON is Closed //

			ssccPersonMergeSplitDao.updatePersonOnSSCCChildPlanParticipant(idSsccReferral, idClosedPerson, idFwdPerson);
		});

		// Fetch the Referrals to be processed for SSCC Placement Medical
		// //Concentor
		// Search all Open SSCC Referrals, Closed Person may or may not be on //
		// Person List, Stage must be Open.
		// Get all the Referrals where id_person not null and id_person is PC in
		// an open stage and // Medical consenter table has closed Person Id in
		// it.
		List<BigDecimal> ssccRefListForMedCons = ssccPersonMergeSplitDao
				.fetchSSCCReferralsForPersonInOpenStages(idClosedPerson, true);
		// For Each Referral update all the Records in each of the following
		// sections.
		ssccRefListForMedCons.stream().forEach(idSsccReferralObj -> {
			// changed ssccRefList to ssccRefListForMedCons
			int idSsccReferral = (idSsccReferralObj != null) ? idSsccReferralObj.intValue() : 0;

			// Update all SSCC Placement Medical Concentor Records where
			// ID_MED_CONSENTER_PERSON is closed person.
			ssccPersonMergeSplitDao.updatePersonOnSSCCPlcmtMedConsenter(idSsccReferral, idClosedPerson, idFwdPerson);
		});
	}

	/**
	 *
	 * Method Name: endEligibilitiesForDateOfDeath Method Description:This method is
	 * called for ending the eligibilities (FCE, ADO) when date of death is
	 * specified for the forward person.
	 *
	 * @param personMergeSplitDto
	 * @param dtDeath
	 */
	private void endEligibilitiesForDateOfDeath(PersonMergeSplitDto personMergeSplitDto, Date dtDeath) {

		if (!ObjectUtils.isEmpty(dtDeath)) {
			// fetch active eligibility for the forward person ArrayList
			// eligList
			ArrayList<Eligibility> eligList = fceEligibilityDao
					.fetchActiveFceForPerson(personMergeSplitDto.getIdForwardPerson().intValue());

			long idFceEvent = 0;
			String cdEligSelected = null;
			boolean bMedicaidUpdateWritten = false;

			if (CollectionUtils.isNotEmpty(eligList)) {

				// ideally there will be only one active FC eligibility
				// Eligibility
				Eligibility fceEligDb = eligList.get(0);
				idFceEvent = fceEligDb.getIdEligEvent();
				cdEligSelected = fceEligDb.getCdEligSelected();

				// set the end date for eligibility record to Date of the death
				fceEligibilityDao.endDateFCEligibility(fceEligDb, dtDeath);

				EventValueDto eventValueDto = eventUtilityService.fetchEventInfo(idFceEvent);

				// if event status is already COMP then we need not excute following
				if (CodesConstant.CEVTSTAT_PROC.equals(eventValueDto.getCdEventStatus())) {
					PostEventReq postEventReq = new PostEventReq();

					PostEventPersonDto postEventPersonDto = new PostEventPersonDto();
					postEventPersonDto.setIdPerson(eventValueDto.getIdPerson());

					List<PostEventPersonDto> postEventPersonDtos = new ArrayList<>();
					postEventPersonDtos.add(postEventPersonDto);

					postEventReq.setPostEventPersonList(postEventPersonDtos);

					postEventReq.setSzCdTask(eventValueDto.getCdEventTask());
					postEventReq.setTsLastUpdate(eventValueDto.getDtLastUpdate());
					postEventReq.setSzCdEventStatus(eventValueDto.getCdEventStatus());
					postEventReq.setSzCdEventType(eventValueDto.getCdEventType());
					postEventReq.setDtDtEventOccurred(eventValueDto.getDtEventOccurred());
					postEventReq.setUlIdEvent(eventValueDto.getIdEvent());
					postEventReq.setUlIdStage(eventValueDto.getIdStage());
					postEventReq.setUlIdPerson(eventValueDto.getIdPerson());
					postEventReq.setSzTxtEventDescr(eventValueDto.getEventDescr());
					postEventReq.setUlIdCase(eventValueDto.getIdCase());

					eventService.postEvent(postEventReq);
				}

				// if selected eligibility is IV E, State Paid, MAO then write a
				// //
				// record to // medicaid update table
				if (CodesConstant.CELIGIBI_010.equals(cdEligSelected)
						|| CodesConstant.CELIGIBI_020.equals(cdEligSelected)
						|| CodesConstant.CELIGIBI_030.equals(cdEligSelected)) {
					personMergeSplitDao.insertMedicaidUpdate(personMergeSplitDto.getIdForwardPerson(),
							eventValueDto.getIdStage(), eventValueDto.getIdCase(), eventValueDto.getIdEvent(), "FCD",
							"DEN");

					// set the flag so that we dont write to medicaid_update
					// again
					bMedicaidUpdateWritten = true;
				}
			}
			// if fc eligibility exists
			// Fetch list of adoption active eligibility for the person

			List<AdoptionAsstncDto> adptEligList = adoptionAsstncDao
					.fetchActiveAdpForPerson(personMergeSplitDto.getIdForwardPerson());

			if (CollectionUtils.isNotEmpty(adptEligList)) {

				for (AdoptionAsstncDto adoptionAsstncDto : adptEligList) {
					String cdAdpSubType = adoptionAsstncDto.getAdoptionAsstncTypeCode();

					// for non recurring subsidy, dont do anything.
					if (CodesConstant.CSUBTYPE_17.equals(cdAdpSubType))
						continue;

					// set the end date on Adoption subsidy record
					adoptionAsstncDao.endDateAdoptionSubsidy(adoptionAsstncDto, dtDeath, CodesConstant.CSUBCLOS_CS);

					// fetch list of events associated with adoption_subsity
					// (ADPT_SUB_EVENT_LINK)
					// it is possible that one event in ADO stageand one event
					// in //
					// PAD stage
					Long idTempEvent = 0l;
					Long idTempStage = 0l;
					Long idTempCase = 0l;

					List<EventDto> adptEventList = adoptionAsstncDao
							.getAdptSubsidyEventList(adoptionAsstncDto.getAdoptionAsstncId().intValue());
					if (CollectionUtils.isNotEmpty(adptEventList)) {
						EventDto adptSubEvent = adptEventList.get(0);
						EventValueDto eventValueDto = eventUtilityService.fetchEventInfo(adptSubEvent.getIdEvent());
						idTempEvent = eventValueDto.getIdEvent();
						idTempStage = eventValueDto.getIdStage();
						idTempCase = eventValueDto.getIdCase();

						// if event status is already COMP then we need not
						// excute
						// //
						// following
						if (CodesConstant.CEVTSTAT_PROC.equals(eventValueDto.getCdEventStatus())) {
							PostEventReq postEventReq = new PostEventReq();

							PostEventPersonDto postEventPersonDto = new PostEventPersonDto();
							postEventPersonDto.setIdPerson(eventValueDto.getIdPerson());

							List<PostEventPersonDto> postEventPersonDtos = new ArrayList<>();
							postEventPersonDtos.add(postEventPersonDto);

							postEventReq.setPostEventPersonList(postEventPersonDtos);

							postEventReq.setSzCdTask(eventValueDto.getCdEventTask());
							postEventReq.setTsLastUpdate(eventValueDto.getDtLastUpdate());
							postEventReq.setSzCdEventStatus(eventValueDto.getCdEventStatus());
							postEventReq.setSzCdEventType(eventValueDto.getCdEventType());
							postEventReq.setDtDtEventOccurred(eventValueDto.getDtEventOccurred());
							postEventReq.setUlIdEvent(eventValueDto.getIdEvent());
							postEventReq.setUlIdStage(eventValueDto.getIdStage());
							postEventReq.setUlIdPerson(eventValueDto.getIdPerson());
							postEventReq.setSzTxtEventDescr(eventValueDto.getEventDescr());
							postEventReq.setUlIdCase(eventValueDto.getIdCase());
							eventService.postEvent(postEventReq);
						}

					}
					// if selected eligibility is IV E, State Paid, MAO then
					// write a
					// //
					// record to // medicaid update table
					if (!bMedicaidUpdateWritten
							&& (!ObjectUtils.isEmpty(idTempEvent) && ServiceConstants.ZERO.equals(idTempEvent))
							&& (CodesConstant.CSUBTYPE_01.equals(cdAdpSubType)
									|| CodesConstant.CSUBTYPE_05.equals(cdAdpSubType)
									|| CodesConstant.CSUBTYPE_07.equals(cdAdpSubType)
									|| CodesConstant.CSUBTYPE_11.equals(cdAdpSubType)
									|| CodesConstant.CSUBTYPE_13.equals(cdAdpSubType)
									|| CodesConstant.CSUBTYPE_15.equals(cdAdpSubType))) {
						personMergeSplitDao.insertMedicaidUpdate(personMergeSplitDto.getIdForwardPerson(), idTempStage,
								idTempCase, idTempEvent, "ADS", "DEN");

						// set the flag so that we dont write to medicaid_update
						// again
						bMedicaidUpdateWritten = true;
					}
				}
			} // if adoption eligibility exists
		}
	}

	/**
	 * Combines up to the first 14 characters of the last name, a comma, the first 8
	 * of the first name, a space, and the first letter of the middle name to create
	 * a full name string that is 25 characters or less
	 *
	 * @param firstName  The first name
	 * @param middleName The middle name
	 * @param lastName   The last name
	 *
	 * @return A <code>java.lang.String</code> of up to 25 characters of the format
	 *         LAST,FIRST M, where LAST is up to 14 characters of the lastName,
	 *         FIRST is up to 8 characters of the firstName, and M is the first
	 *         character of the middleName.
	 */
	private String formatFullName(String firstName, String middleName, String lastName) {
		StringBuilder fullName = new StringBuilder("");
		// If the last name is not null put the first 14 characters of it
		// it into the buffer
		if (!ObjectUtils.isEmpty(lastName)) {
			if (lastName.length() > ServiceConstants.LAST_NAME_LENGTH) {
				fullName.append(lastName.substring(0, ServiceConstants.LAST_NAME_LENGTH));
			} else {
				fullName.append(lastName);
			}
			fullName.append(",");
		}

		// If the first name is not null put the first 8 characters of
		// it into the buffer
		if (!ObjectUtils.isEmpty(firstName)) {
			if (firstName.length() > ServiceConstants.FIRST_NAME_LENGTH) {
				fullName.append(firstName.substring(0, ServiceConstants.FIRST_NAME_LENGTH));
			} else {
				fullName.append(firstName);
			}
		}

		// If the middle name is not null the first character
		// put it into the buffer */
		if (!ObjectUtils.isEmpty(middleName)) {
			fullName.append(" ");
			if (middleName.length() > ServiceConstants.MIDDLE_NAME_LENGTH) {
				fullName.append(middleName.substring(0, ServiceConstants.MIDDLE_NAME_LENGTH));
			} else {
				fullName.append(middleName);
			}
		}

		// return a full name that is 25 characters or less
		return fullName.toString();
	}

	private String dateToString(Date date, DateFormat dateFormat) {
		if (ObjectUtils.isEmpty(date)) {
			return "";
		}
		return dateFormat.format(date);
	}

	/**
	 * Helper method that decides if two fields are the same - equal or both null.
	 *
	 * @param thisString     the first String to be compared
	 * @param comparedString the second string to be compared
	 * @return true if the two Strings are equal or both null
	 */
	private boolean checkForEquality(String thisString, String comparedString) {
		boolean result;
		if (thisString == null) {
			if (comparedString == null) {
				result = true;
			} else {
				result = false;
			}
		} else {
			result = thisString.equals(comparedString);
		}
		return result;
	}

	/**
	 *
	 * Method Name: isValid Method Description:Checks to see if a given string is
	 * valid. This includes checking that the string is not null or empty.
	 *
	 * @param value
	 * @return
	 */
	private boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		String trimmedString = value.trim();
		return (trimmedString.length() > 0);
	}

	/**
	 *
	 * Method Name: getRaceEthnicityGroup Method Description:get race ethnicity
	 * group
	 *
	 * @param ethnicity
	 * @param personRaceList
	 * @return
	 */
	private String getRaceEthnicityGroup(String ethnicity, List<PersonRaceDto> personRaceList) {

		String group = "";
		int numRaces = 0;

		numRaces = personRaceList.size();
		List<String> raceList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(personRaceList)) {
			personRaceList.stream().forEach(race -> raceList.add(race.getCdPersonRace()));
		}
		boolean isHisp = true;

		if (ServiceConstants.ETHNICITY_UNABLE_TO_DETERMINE.equals(ethnicity)
				&& raceList.contains(ServiceConstants.RACE_UNABLE_TO_DETERMINE)) {
			group = ServiceConstants.GROUP_UNABLE_TO_DETERMINE;
		} else if (ServiceConstants.ETHNICITY_DECLINE_TO_INDICATE.equals(ethnicity)
				&& raceList.contains(ServiceConstants.RACE_DECLINE_TO_INDICATE)) {
			group = ServiceConstants.GROUP_DECLINE_TO_INDICATE;
		} else {
			isHisp = ServiceConstants.ETHNICITY_HISPANIC.equals(ethnicity);
			if (numRaces > 2) {

				if (ServiceConstants.ETHNICITY_UNABLE_TO_DETERMINE.equals(ethnicity)) {
					group = ServiceConstants.GROUP_MULTIPLE_UTD;
				} else if (ServiceConstants.ETHNICITY_DECLINE_TO_INDICATE.equals(ethnicity)) {
					group = ServiceConstants.GROUP_MULTIPLE_DC;
				} else {
					group = (isHisp) ? ServiceConstants.GROUP_MULTIPLE_HISPANIC
							: ServiceConstants.GROUP_MULTIPLE_NON_HISPANIC;
				}

			} // end if multiple races
			else if (numRaces == 2) {
				if (raceList.contains(ServiceConstants.RACE_BLACK) && raceList.contains(ServiceConstants.RACE_WHITE)) {
					if (ServiceConstants.ETHNICITY_UNABLE_TO_DETERMINE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_BLACK_WHITE_UTD;
					} else if (ServiceConstants.ETHNICITY_DECLINE_TO_INDICATE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_BLACK_WHITE_DC;
					} else {
						group = (isHisp) ? ServiceConstants.GROUP_BLACK_WHITE_HISPANIC
								: ServiceConstants.GROUP_BLACK_WHITE_NON_HISPANIC;
					} // end if black and white
				} // end else 2 races
				else {
					if (ServiceConstants.ETHNICITY_UNABLE_TO_DETERMINE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_MULTIPLE_UTD;
					} else if (ServiceConstants.ETHNICITY_DECLINE_TO_INDICATE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_MULTIPLE_DC;
					} else {
						group = (isHisp) ? ServiceConstants.GROUP_MULTIPLE_HISPANIC
								: ServiceConstants.GROUP_MULTIPLE_NON_HISPANIC;
					}
				} // end else not black and white
			} // end else 2 races
			else {

				if (raceList.contains(ServiceConstants.RACE_AMERIND)) {
					if (ServiceConstants.ETHNICITY_UNABLE_TO_DETERMINE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_AMERIND_UTD;
					} else if (ServiceConstants.ETHNICITY_DECLINE_TO_INDICATE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_AMERIND_DC;
					} else {
						group = (isHisp) ? ServiceConstants.GROUP_AMERIND_HISPANIC
								: ServiceConstants.GROUP_AMERIND_NON_HISPANIC;
					}
				} else if (raceList.contains(ServiceConstants.RACE_ASIAN)) {
					if (ServiceConstants.ETHNICITY_UNABLE_TO_DETERMINE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_ASIAN_UTD;
					} else if (ServiceConstants.ETHNICITY_DECLINE_TO_INDICATE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_ASIAN_DC;
					} else {
						group = (isHisp) ? ServiceConstants.GROUP_ASIAN_HISPANIC
								: ServiceConstants.GROUP_ASIAN_NON_HISPANIC;
					}
				} else if (raceList.contains(ServiceConstants.RACE_BLACK)) {
					if (ServiceConstants.ETHNICITY_UNABLE_TO_DETERMINE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_BLACK_UTD;
					} else if (ServiceConstants.ETHNICITY_DECLINE_TO_INDICATE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_BLACK_DC;
					} else {
						group = (isHisp) ? ServiceConstants.GROUP_BLACK_HISPANIC
								: ServiceConstants.GROUP_BLACK_NON_HISPANIC;
					}
				} else if (raceList.contains(ServiceConstants.RACE_HAWAIIAN)) {
					if (ServiceConstants.ETHNICITY_UNABLE_TO_DETERMINE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_HAWAIIAN_UTD;
					} else if (ServiceConstants.ETHNICITY_DECLINE_TO_INDICATE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_HAWAIIAN_DC;
					} else {
						group = (isHisp) ? ServiceConstants.GROUP_HAWAIIAN_HISPANIC
								: ServiceConstants.GROUP_HAWAIIAN_NON_HISPANIC;
					}
				} else if (raceList.contains(ServiceConstants.RACE_WHITE)) {
					if (ServiceConstants.ETHNICITY_UNABLE_TO_DETERMINE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_WHITE_UTD;
					} else if (ServiceConstants.ETHNICITY_DECLINE_TO_INDICATE.equals(ethnicity)) {
						group = ServiceConstants.GROUP_WHITE_DC;
					} else {
						group = (isHisp) ? ServiceConstants.GROUP_WHITE_HISPANIC
								: ServiceConstants.GROUP_WHITE_NON_HISPANIC;
					}
				} else if (raceList.contains(ServiceConstants.RACE_UNABLE_TO_DETERMINE)) {
					group = (isHisp) ? ServiceConstants.GROUP_UNABLE_HISPANIC
							: ServiceConstants.GROUP_UNABLE_NON_HISPANIC;

					if (ServiceConstants.ETHNICITY_DECLINE_TO_INDICATE.equals(ethnicity)) {
						group = ServiceConstants.RACE_UNABLE_TO_DETERMINE_DC;
					}
				} else if (raceList.contains(ServiceConstants.RACE_DECLINE_TO_INDICATE)) {
					group = (isHisp) ? ServiceConstants.GROUP_DECLINE_HISPANIC
							: ServiceConstants.GROUP_DECLINE_NON_HISPANIC;

					if (ServiceConstants.ETHNICITY_UNABLE_TO_DETERMINE.equals(ethnicity)) {
						group = ServiceConstants.RACE_DECLINE_TO_INDICATE_UD;
					}
				} else {
					group = "";
				}
			}
		}

		return group;

	}

	/**
	 *
	 * Method Name: mappingName Method Description: Mapping Name to NameDto
	 *
	 * @param name
	 * @return
	 */
	private NameDto mappingName(Name name) {
		if (ObjectUtils.isEmpty(name))
			return null;
		NameDto nameDto = new NameDto();
		nameDto.setCdNameSuffix(name.getCdNameSuffix());
		nameDto.setIdName(name.getIdName());
		nameDto.setIdPerson(name.getPerson().getIdPerson());
		nameDto.setTsLastUpdate(name.getDtLastUpdate());
		nameDto.setIndNameInvalid(name.getIndNameInvalid());
		nameDto.setIndNamePrimary(name.getIndNamePrimary());
		nameDto.setNmNameFirst(name.getNmNameFirst());
		nameDto.setNmNameLast(name.getNmNameLast());
		nameDto.setNmNameMiddle(name.getNmNameMiddle());
		nameDto.setIndNameEmp(name.getIndNameEmp().toString());
		nameDto.setDtNameStart(name.getDtNameStartDate());
		nameDto.setDtNameEnd(name.getDtNameEndDate());
		return nameDto;
	}

	/**
	 *
	 * Method Name: mergeChildSexualAggressionIncdnts Method Description: This method
	 * merges child sexual aggression details
	 *
	 * @param personMergeSplitDto
	 * @return
	 */
	private void mergeChildSexualAggressionIncdnts(PersonMergeSplitDto personMergeSplitDto) {
		// For closed person, all the incidents will be moved to forward person
		Long idForwardPerson = personMergeSplitDto.getIdForwardPerson();
		Long idClosedPerson = personMergeSplitDto.getIdClosedPerson();

		List<CsaEpisodesIncdntDto> closedEpisodeIncdntList = csaDao.fetchCSAIncidentList(idClosedPerson);
		// All incidents for closed person, move to forward person
		closedEpisodeIncdntList.stream().forEach(
			incidentDto -> {
				incidentDto.setIdLastUpdatePerson(personMergeSplitDto.getIdPersonMergeWorker());
				incidentDto.setIdPerson(idForwardPerson);
				csaDao.updateCsaIncident(incidentDto);
			});

		// Update the ID_VICTIM closed pid to forward person
		boolean cnt = personMergeSplitDao.updateIfClosedPersonVictim(personMergeSplitDto);
		
		if (cnt || !ObjectUtils.isEmpty(closedEpisodeIncdntList))
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_470, ServiceConstants.IND_TRUE);
	}

	/**
	 *
	 * Method Name: mergeChildSexualAggression Method Description: This method
	 * merges child sexual aggression details
	 *
	 * @param personMergeSplitDto
	 * @return
	 */
	private void mergeChildSexualAggression(PersonMergeSplitDto personMergeSplitDto) {

		// For closed person, all the incidents will be moved to forward person
		Long idForwardPerson = personMergeSplitDto.getIdForwardPerson();
		Long idClosedPerson = personMergeSplitDto.getIdClosedPerson();
		List<CSAEpisodeDto> closedEpisodeList = csaDao.fetchCSAList(idClosedPerson);
		List<CSAEpisodeDto> forwardEpisodeList = csaDao.fetchCSAList(idForwardPerson);

		CSAEpisodeDto activeClosedEpisodeDto = null;
		CSAEpisodeDto activeForwardEpisodeDto = null;
		// Get forward person's opened Episode
		if (CollectionUtils.isNotEmpty(forwardEpisodeList) && forwardEpisodeList.stream()
				.anyMatch(forwardEpisode -> ObjectUtils.isEmpty(forwardEpisode.getDtEpisodeEnd()))) {
			activeForwardEpisodeDto = forwardEpisodeList.stream()
					.filter(forwardEpisode -> ObjectUtils.isEmpty(forwardEpisode.getDtEpisodeEnd())).findFirst().get();
		}
		// Get closed person's opened Episode
		if (CollectionUtils.isNotEmpty(closedEpisodeList) && closedEpisodeList.stream()
				.anyMatch(closedEpisode -> ObjectUtils.isEmpty(closedEpisode.getDtEpisodeEnd()))) {
			activeClosedEpisodeDto = closedEpisodeList.stream()
					.filter(closedEpisode -> ObjectUtils.isEmpty(closedEpisode.getDtEpisodeEnd())).findFirst().get();
		}
		// move closed person's episode to forward person
		if (CollectionUtils.isNotEmpty(closedEpisodeList)) {
			closedEpisodeList.stream().forEach(closedEpisodeDto -> {
				// For Closed Episode, directly move to forward person
				if (!ObjectUtils.isEmpty(closedEpisodeDto.getDtEpisodeEnd())) {
					closedEpisodeDto.setIdLastUpdatePerson(personMergeSplitDto.getIdPersonMergeWorker());
					closedEpisodeDto.setIdPerson(idForwardPerson);
					csaDao.updateCsaEpisode(closedEpisodeDto);
				}

			});

			if (!ObjectUtils.isEmpty(activeClosedEpisodeDto)) {
				// if forward person has no opened episode, closed person has
				// opened episode, move the opened episode from closed person to
				// forward person
				if (ObjectUtils.isEmpty(activeForwardEpisodeDto)) {
					activeClosedEpisodeDto.setIdLastUpdatePerson(personMergeSplitDto.getIdPersonMergeWorker());
					activeClosedEpisodeDto.setIdPerson(idForwardPerson);
					csaDao.updateCsaEpisode(activeClosedEpisodeDto);
				} else {
					// if forward person and closed person have opened episode,
					// close closed person's episode and move it to forward
					// person.
					activeClosedEpisodeDto.setDtEpisodeEnd(new Date());
					activeClosedEpisodeDto.setIdPerson(idForwardPerson);
					csaDao.updateCsaEpisode(activeClosedEpisodeDto);

					}
				}
			}

			// FCL SIH update all aggression incidents (Keeping above code to preserve existing episode history data)
			mergeChildSexualAggressionIncdnts(personMergeSplitDto);
		}
	

	/**
	 * Method Name: mergeTrafficking Method Description: This method merges
	 * Trafficking details
	 *
	 * @param personMergeSplitDto
	 * @return
	 */
	private void mergeTrafficking(PersonMergeSplitDto personMergeSplitDto) {

		 Predicate<TraffickingDto> confirmedSxIndPredicate = traffickingDto -> (CodesConstant.CTRFTYP_SXTR.equals(traffickingDto.getCdtrfckngType()) &&
	            CodesConstant.CTRFSTAT_CONF.equals(traffickingDto.getCdtrfckngStat()));
		/*
		 * For Closed Person, All Trafficking records will be moved to Forward Person.
		 */
		Long idClosedPerson = personMergeSplitDto.getIdClosedPerson();
		Long idForwardPerson = personMergeSplitDto.getIdForwardPerson();

		/* Fetching Closed persons records */
		TraffickingDto closedPersonDto = new TraffickingDto();
		closedPersonDto.setIdPerson(new BigDecimal(idClosedPerson));
		List<TraffickingDto> closedPersonList = traffickingDao.getTraffickingList(closedPersonDto);

		/* Fetching Forward persons records */
		TraffickingDto forwardPersonDto = new TraffickingDto();
		forwardPersonDto.setIdPerson(new BigDecimal(idForwardPerson));
		List<TraffickingDto> forwardPersonList = traffickingDao.getTraffickingList(forwardPersonDto);

		if (!ObjectUtils.isEmpty(closedPersonList) || !ObjectUtils.isEmpty(forwardPersonList)) {
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_480, ServiceConstants.IND_TRUE);
		}
		/*
		 * If Closed Person have Trafficking Records, Merging Closed Person records to
		 * Forward Person Records, else Forwading records remain same.
		 */
		if (!CollectionUtils.isEmpty(closedPersonList)) {
			List<TraffickingDto> closedPersonFilteredList = new ArrayList<>();
			if (CollectionUtils.isEmpty(forwardPersonList)) {
				closedPersonFilteredList.addAll(closedPersonList);
			} else {
				for (TraffickingDto cPerson : closedPersonList) {
					boolean existInForwardPerson = false;
					for (TraffickingDto fPerson : forwardPersonList) {
						if (!ObjectUtils.isEmpty(fPerson) && !ObjectUtils.isEmpty(cPerson) && ((!ObjectUtils
								.isEmpty(fPerson.getCdtrfckngType()) && !ObjectUtils.isEmpty(cPerson.getCdtrfckngType())
								&& !fPerson.getCdtrfckngType().equalsIgnoreCase(cPerson.getCdtrfckngType()))
								|| (!ObjectUtils.isEmpty(fPerson.getCdtrfckngStat())
										&& !ObjectUtils.isEmpty(cPerson.getCdtrfckngStat())
										&& !fPerson.getCdtrfckngStat().equalsIgnoreCase(cPerson.getCdtrfckngStat()))
								|| (!ObjectUtils.isEmpty(fPerson.getCdNotifctntype())
										&& !ObjectUtils.isEmpty(cPerson.getCdNotifctntype())
										&& !fPerson.getCdNotifctntype().equalsIgnoreCase(cPerson.getCdNotifctntype()))
								|| ((!ObjectUtils.isEmpty(fPerson.getDtWrkrNotified())
										&& !ObjectUtils.isEmpty(cPerson.getDtWrkrNotified())
										&& !fPerson.getDtWrkrNotified().equals(cPerson.getDtWrkrNotified()))
										|| ((!ObjectUtils.isEmpty(fPerson.getDtWrkrNotified())
												&& !ObjectUtils.isEmpty(cPerson.getDtWrkrNotified())
												&& fPerson.getDtWrkrNotified().equals(cPerson.getDtWrkrNotified()))
												&& (!ObjectUtils.isEmpty(fPerson.getTimeWrkNotifd())
														&& !ObjectUtils.isEmpty(cPerson.getTimeWrkNotifd())
														&& !fPerson.getTimeWrkNotifd()
																.equals(cPerson.getTimeWrkNotifd()))))

								|| ((!ObjectUtils.isEmpty(fPerson.getDtleNotifd())
										&& !ObjectUtils.isEmpty(cPerson.getDtleNotifd())
										&& !fPerson.getDtleNotifd().equals(cPerson.getDtleNotifd()))
										|| ((!ObjectUtils.isEmpty(fPerson.getDtleNotifd())
												&& !ObjectUtils.isEmpty(cPerson.getDtleNotifd())
												&& fPerson.getDtleNotifd().equals(cPerson.getDtleNotifd()))
												&& (!ObjectUtils.isEmpty(fPerson.getDtleNotifdTime())
														&& !ObjectUtils.isEmpty(cPerson.getDtleNotifdTime())
														&& !fPerson.getDtleNotifdTime()
																.equals(cPerson.getDtleNotifdTime()))))
								|| ((ObjectUtils.isEmpty(fPerson.getCdPrdVctmAgncy())
										|| ObjectUtils.isEmpty(cPerson.getCdPrdVctmAgncy())
										|| !fPerson.getCdPrdVctmAgncy().equalsIgnoreCase(cPerson.getCdPrdVctmAgncy()))
										|| (ObjectUtils.isEmpty(fPerson.getCdPrdVctmztnCvs())
												|| ObjectUtils.isEmpty(cPerson.getCdPrdVctmztnCvs())
												|| !fPerson.getCdPrdVctmztnCvs()
														.equalsIgnoreCase(cPerson.getCdPrdVctmztnCvs()))
										|| (ObjectUtils.isEmpty(fPerson.getCdPrdVctmztnInv())
												|| ObjectUtils.isEmpty(cPerson.getCdPrdVctmztnInv())
												|| !fPerson.getCdPrdVctmztnInv()
														.equalsIgnoreCase(cPerson.getCdPrdVctmztnInv()))
										|| (ObjectUtils.isEmpty(fPerson.getCdPrdVctmztnPal())
												|| ObjectUtils.isEmpty(cPerson.getCdPrdVctmztnPal())
												|| !fPerson.getCdPrdVctmztnPal()
														.equalsIgnoreCase(cPerson.getCdPrdVctmztnPal())))))
							continue;
						else {
							existInForwardPerson = true;
							break;
						}
					}
					if (!existInForwardPerson) {
						closedPersonFilteredList.add(cPerson);
					}
				}
			}

			if (!ObjectUtils.isEmpty(closedPersonFilteredList)) {
				closedPersonFilteredList.forEach(closedTrfckngrec -> {
					closedTrfckngrec
							.setIdLastupdatePerson(new BigDecimal(personMergeSplitDto.getIdPersonMergeWorker()));
					closedTrfckngrec.setIdFwdPerson(new BigDecimal(idForwardPerson));
					closedTrfckngrec.setIdClosedPerson(new BigDecimal(idClosedPerson));
					traffickingDao.personMergeSaveTrafficking(closedTrfckngrec);
				});

				boolean confirmedSxIndicator = closedPersonFilteredList.stream().anyMatch(confirmedSxIndPredicate);

				if(confirmedSxIndicator) {
					sexualVictimizationHistoryDao.updateSexualHistoryQuestionToYes(BigDecimal.valueOf(idForwardPerson), String.valueOf(personMergeSplitDto.getIdPersonMergeWorker()));
				}
			}
		}

	}

	/**
	 * Method Name: mergeMutualNonAggressive
	 * Method Description: This method merges Child Mutual Non-Aggressive Incidents
	 *
	 * @param personMergeSplitDto
	 */
	private void mergeMutualNonAggressive(PersonMergeSplitDto personMergeSplitDto) {

		// Move Mutual incidents from closed person to forward person
		Long idClosedPerson = personMergeSplitDto.getIdClosedPerson();
		Long idForwardPerson = personMergeSplitDto.getIdForwardPerson();

		// Skip redudant piece of code, already got executed in mergeChildSxVctmztnIncdnts()
		/*
		ChildSxVctmztn closedPersonSxVctmztn = sexualVictimizationHistoryDao.getChildSxVctmztnByPersonId(idClosedPerson);
		ChildSxVctmztn fwdPersonSxVctmztn = sexualVictimizationHistoryDao.getChildSxVctmztnByPersonId(idForwardPerson);

		if (!ObjectUtils.isEmpty(closedPersonSxVctmztn) && !ObjectUtils.isEmpty(fwdPersonSxVctmztn)) {

			updatechildSxHistoryVctmztn(closedPersonSxVctmztn, fwdPersonSxVctmztn,
					personMergeSplitDto.getIdPersonMergeWorker(),personMergeSplitDto);
			if (!ObjectUtils.isEmpty(closedPersonSxVctmztn) || !ObjectUtils.isEmpty(fwdPersonSxVctmztn)) {
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_490, ServiceConstants.IND_TRUE);
			}

		} if (!ObjectUtils.isEmpty(closedPersonSxVctmztn) && ObjectUtils.isEmpty(fwdPersonSxVctmztn)) {

			//if closed person has history and then no record for forward person,create forward person record based on closed person
			createChildSxHistoryVctmztn(closedPersonSxVctmztn, personMergeSplitDto.getIdPersonMergeWorker(), idForwardPerson);
			if (!ObjectUtils.isEmpty(closedPersonSxVctmztn) || !ObjectUtils.isEmpty(fwdPersonSxVctmztn)) {
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_490, ServiceConstants.IND_TRUE);
			}
		}
		*/

		List<ChildSxMutalIncdnt> closedPersonSxMutualIncdntList = mutualNonAggressiveIncidentDao
				.getMutualNonAggressiveIncidents(idClosedPerson);
		List<ChildSxMutalIncdnt> forwardPersonSxMutualIncdntList = mutualNonAggressiveIncidentDao
				.getMutualNonAggressiveIncidents(idForwardPerson);

		if (!ObjectUtils.isEmpty(closedPersonSxMutualIncdntList) || !ObjectUtils.isEmpty(forwardPersonSxMutualIncdntList)) {
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_490, ServiceConstants.IND_TRUE);
		}
		updateSxMutualIncdnts(closedPersonSxMutualIncdntList, forwardPersonSxMutualIncdntList, idForwardPerson,
				personMergeSplitDto.getIdPersonMergeWorker());
		
		personMergeSplitDao.updateIfClosedPersonMutual(personMergeSplitDto);

	}

	/**
	 * Method Name: mergeChildSxVctmztnIncdnts Method Description: This method
	 * merges ChildSxVctmztnIncdnts
	 *
	 * @param personMergeSplitDto
	 * @return
	 */
	private void mergeChildSxVctmztnIncdnts(PersonMergeSplitDto personMergeSplitDto) {
		/*
		 * For Closed Person, All SVH incident records will be moved to Forward Person.
		 */
		Long idClosedPerson = personMergeSplitDto.getIdClosedPerson();
		Long idForwardPerson = personMergeSplitDto.getIdForwardPerson();
		ChildSxVctmztn closedPersonSxVctmztn = sexualVictimizationHistoryDao.getChildSxVctmztnByPersonId(idClosedPerson);
		ChildSxVctmztn fwdPersonSxVctmztn = sexualVictimizationHistoryDao.getChildSxVctmztnByPersonId(idForwardPerson);
		if (!ObjectUtils.isEmpty(closedPersonSxVctmztn) && !ObjectUtils.isEmpty(fwdPersonSxVctmztn)) {

			updatechildSxHistoryVctmztn(closedPersonSxVctmztn, fwdPersonSxVctmztn,
					personMergeSplitDto.getIdPersonMergeWorker(),personMergeSplitDto);
			if (!ObjectUtils.isEmpty(closedPersonSxVctmztn) || !ObjectUtils.isEmpty(fwdPersonSxVctmztn)) {
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_490, ServiceConstants.IND_TRUE);
			}

		} if (!ObjectUtils.isEmpty(closedPersonSxVctmztn) && ObjectUtils.isEmpty(fwdPersonSxVctmztn)) {

			//if closed person has history and then no record for forward person,create forward person record based on closed person
			createChildSxHistoryVctmztn(closedPersonSxVctmztn,personMergeSplitDto.getIdPersonMergeWorker(),idForwardPerson);
			if (!ObjectUtils.isEmpty(closedPersonSxVctmztn) || !ObjectUtils.isEmpty(fwdPersonSxVctmztn)) {
				personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_490, ServiceConstants.IND_TRUE);
			}
		}

		List<ChildSxVctmztnIncdnt> ClosedPersonSxVctmztnIncdntList = sexualVictimizationHistoryDao
				.fetchSexualVictimHistory(idClosedPerson);
		List<ChildSxVctmztnIncdnt> forwardPersonSxVctmztnIncdntList = sexualVictimizationHistoryDao
				.fetchSexualVictimHistory(idForwardPerson);

		if (!ObjectUtils.isEmpty(ClosedPersonSxVctmztnIncdntList) || !ObjectUtils.isEmpty(forwardPersonSxVctmztnIncdntList)) {
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_490, ServiceConstants.IND_TRUE);
		}
		updatesxVctmztnIncdnts(ClosedPersonSxVctmztnIncdntList, forwardPersonSxVctmztnIncdntList, idForwardPerson,
				personMergeSplitDto.getIdPersonMergeWorker());

		// Update ID_PERSON_AGGRESSOR closed pids to forward person
		boolean cnt = personMergeSplitDao.updateIfClosedPersonAggressor(personMergeSplitDto);
		if (cnt) {
			personMergeSplitDto.getUpdateLogMap().put(CodesConstant.CPMFLDCT_490, ServiceConstants.IND_TRUE);
		}
	}
	
	/**
	 * Method Name: createChildSxHistoryVctmztn Method Description: This method
	 * creates a sx vctmzn history record from the closed person data
	 *
	 * @param ChildSxVctmztn
	 * @param long idPersonMergeWorker
	 * @return void
	 */

	private void createChildSxHistoryVctmztn(ChildSxVctmztn closedPersonSxVctmztn, Long idPersonMergeWorker,Long idForwardPerson) {

		ChildSxVctmztn forwardChildSxVctmztn = new ChildSxVctmztn();
		Person person = (Person) sessionFactory.getCurrentSession().load(Person.class, idForwardPerson);
		forwardChildSxVctmztn.setIndChildSxVctmztnHist(closedPersonSxVctmztn.getIndChildSxVctmztnHist());
		forwardChildSxVctmztn.setTxtPreviousUnconfirmFinds(closedPersonSxVctmztn.getTxtPreviousUnconfirmFinds());
		forwardChildSxVctmztn.setTxtSupervisionContactDesc(closedPersonSxVctmztn.getTxtSupervisionContactDesc());
		forwardChildSxVctmztn.setPerson(person);
		forwardChildSxVctmztn.setDtCreated(new Date());
		forwardChildSxVctmztn.setDtLastUpdate(new Date());
		forwardChildSxVctmztn.setIdLastUpdatePerson(idPersonMergeWorker);
		forwardChildSxVctmztn.setIdCreatedPerson(idPersonMergeWorker);
		sessionFactory.getCurrentSession().save(forwardChildSxVctmztn);
	}

	/**
	 * Method Name: updateSxMutualIncdnts
	 * Method Description: This method will bring forward incidents where the Date of Incident fields do match
	 * ChildSxMutalIncdnts
	 *
	 * @param List<ChildSxMutalIncdnt> closedPersonSxMutualIncdntList
	 * @param List<ChildSxMutalIncdnt> forwardPersonSxMutualIncdntList
	 * @param Long                       idForwardPerson
	 * @param Long                       idPersonMergeWorker
	 * @return
	 */
	private void updateSxMutualIncdnts(List<ChildSxMutalIncdnt> closedPersonSxMutualIncdntList,
										List<ChildSxMutalIncdnt> forwardPersonSxMutualIncdntList, Long idForwardPerson,
										Long idPersonMergeWorker) {

		List<ChildSxMutalIncdnt> toBeUpdateClosedPersonList = new ArrayList<>();
		Predicate<List<ChildSxMutalIncdnt>> predicateSxMutualIncdnt = childSxMutualIncdntList -> ObjectUtils
				.isEmpty(childSxMutualIncdntList);
		Person person = (Person) sessionFactory.getCurrentSession().load(Person.class, idForwardPerson);

		//Functional Interface used as mapper to update forward person data
		Function<ChildSxMutalIncdnt, ChildSxMutalIncdnt> forwardPersonmapper = childSxMutalIncdnt -> {

			childSxMutalIncdnt.setPerson(person);
			childSxMutalIncdnt.setIdLastUpdatePerson(idPersonMergeWorker);
			childSxMutalIncdnt.setDtLastUpdate(new Date());
			return childSxMutalIncdnt;
		};

		if (!predicateSxMutualIncdnt.test(closedPersonSxMutualIncdntList)
				&& !predicateSxMutualIncdnt.test(forwardPersonSxMutualIncdntList)) {

			//if there is no match for Date of incident between closed & forward the closed person data is moved to forward person
			toBeUpdateClosedPersonList = closedPersonSxMutualIncdntList.stream()
					.filter(closedPersonSxMutualIncdnt -> forwardPersonSxMutualIncdntList.stream()
							.noneMatch(forwardEntity -> forwardEntity.getDtIncident()
									.equals(closedPersonSxMutualIncdnt.getDtIncident())))
					.map(forwardPersonmapper).collect(Collectors.toList());
		} else if (!predicateSxMutualIncdnt.test(closedPersonSxMutualIncdntList)
				&& predicateSxMutualIncdnt.test(forwardPersonSxMutualIncdntList)) {

			//if closed person has data and no data present for forward person then closed person data is moved to forward person
			toBeUpdateClosedPersonList = closedPersonSxMutualIncdntList.stream().map(forwardPersonmapper)
					.collect(Collectors.toList());
		}
		toBeUpdateClosedPersonList
				.forEach(childSxMutualIncdnt -> sessionFactory.getCurrentSession().update(childSxMutualIncdnt));

	}

	/**
	 * Method Name: updatesxVctmztnIncdnts Method Description: This method will
	 * bring forward incidents where the Date of Incident fields do match
	 * ChildSxVctmztnIncdnts
	 *
	 * @param List<ChildSxVctmztnIncdnt> closedPersonSxVctmztnIncdntList
	 * @param List<ChildSxVctmztnIncdnt> forwardPersonSxVctmztnIncdntList
	 * @param Long                       idForwardPerson
	 * @param Long                       idPersonMergeWorker
	 * @return
	 */
	private void updatesxVctmztnIncdnts(List<ChildSxVctmztnIncdnt> closedPersonSxVctmztnIncdntList,
			List<ChildSxVctmztnIncdnt> forwardPersonSxVctmztnIncdntList, Long idForwardPerson,
			Long idPersonMergeWorker) {

		List<ChildSxVctmztnIncdnt> toBeUpdateClosedPersonList = new ArrayList<>();
		Predicate<List<ChildSxVctmztnIncdnt>> predicateSxVctmztnIncdnt = ChildSxVctmztnIncdntList -> ObjectUtils
				.isEmpty(ChildSxVctmztnIncdntList);
		Person person = (Person) sessionFactory.getCurrentSession().load(Person.class, idForwardPerson);

		//Functional Interface used as mapper to update forward person data
		Function<ChildSxVctmztnIncdnt, ChildSxVctmztnIncdnt> forwardPersonmapper = childSxVctmztnIncdnt -> {

			childSxVctmztnIncdnt.setPerson(person);
			childSxVctmztnIncdnt.setIdLastUpdatePerson(idPersonMergeWorker);
			childSxVctmztnIncdnt.setDtLastUpdate(new Date());
			return childSxVctmztnIncdnt;
		};

		if (!predicateSxVctmztnIncdnt.test(closedPersonSxVctmztnIncdntList)
				&& !predicateSxVctmztnIncdnt.test(forwardPersonSxVctmztnIncdntList)) {

			//if there is no match for Date of incident between closed & forward the closed person data is moved to forward person
			toBeUpdateClosedPersonList = closedPersonSxVctmztnIncdntList.stream()
					.filter(closedPersonSxVctmztnIncdnt -> forwardPersonSxVctmztnIncdntList.stream()
							.noneMatch(forwardEntity -> forwardEntity.getDtIncident()
									.equals(closedPersonSxVctmztnIncdnt.getDtIncident())))
					.map(forwardPersonmapper).collect(Collectors.toList());
		} else if (!predicateSxVctmztnIncdnt.test(closedPersonSxVctmztnIncdntList)
				&& predicateSxVctmztnIncdnt.test(forwardPersonSxVctmztnIncdntList)) {

			//if closed person has data and no data present for forward person then closed person data is moved to forward person
			toBeUpdateClosedPersonList = closedPersonSxVctmztnIncdntList.stream().map(forwardPersonmapper)
					.collect(Collectors.toList());
		}
		toBeUpdateClosedPersonList
				.forEach(childSxVctmztnIncdnt -> sessionFactory.getCurrentSession().update(childSxVctmztnIncdnt));

	}

	/**
	 * Method Name: updatechildSxHistoryVctmztn Method Description: This method will
	 * retain the 'yes' answer to the question 'Does this child/youth have a history
	 * of sexual victimization from closed to Forward person
	 * @param personMergeSplitDto
	 *
	 * @param ChildSxVctmztn closedPersonSxVctmztn
	 * @param ChildSxVctmztn fwdPersonSxVctmztn
	 * @param Long           idForwardPerson
	 * @return
	 */
	private void updatechildSxHistoryVctmztn(ChildSxVctmztn closedPersonSxVctmztn, ChildSxVctmztn fwdPersonSxVctmztn,
			Long idPersonMergeWorker, PersonMergeSplitDto personMergeSplitDto) {

		Predicate<ChildSxVctmztn> predicatesxvctmztn = childPerson -> "Y"
				.equalsIgnoreCase(childPerson.getIndChildSxVctmztnHist());
				StringBuilder txtPreviousUnconfirmFinds = null;
				StringBuilder txtSupervisionContactDesc = null;
		Predicate<StringBuilder> strLengthCheck = str -> str.length() < 4000;

		// Either closed or forward person has sx vctmzn history then the forward person indicator is set to 'Y'

		if (!predicatesxvctmztn.test(fwdPersonSxVctmztn) && predicatesxvctmztn.test(closedPersonSxVctmztn)) {

			fwdPersonSxVctmztn.setIdLastUpdatePerson(idPersonMergeWorker);
			fwdPersonSxVctmztn.setIndChildSxVctmztnHist("Y");
			fwdPersonSxVctmztn.setDtLastUpdate(new Date());


		}

		if(!ObjectUtils.isEmpty(closedPersonSxVctmztn.getTxtPreviousUnconfirmFinds()) && !ObjectUtils.isEmpty(fwdPersonSxVctmztn.getTxtPreviousUnconfirmFinds())) {
			txtPreviousUnconfirmFinds = new StringBuilder();
			txtPreviousUnconfirmFinds.append(fwdPersonSxVctmztn.getTxtPreviousUnconfirmFinds()).append("\n").append(closedPersonSxVctmztn.getTxtPreviousUnconfirmFinds());
			if(strLengthCheck.test(txtPreviousUnconfirmFinds)) {
			 fwdPersonSxVctmztn.setTxtPreviousUnconfirmFinds(txtPreviousUnconfirmFinds.toString());
			}
		}
		else if(!ObjectUtils.isEmpty(closedPersonSxVctmztn.getTxtPreviousUnconfirmFinds()) && ObjectUtils.isEmpty(fwdPersonSxVctmztn.getTxtPreviousUnconfirmFinds())) {
			fwdPersonSxVctmztn.setTxtPreviousUnconfirmFinds(closedPersonSxVctmztn.getTxtPreviousUnconfirmFinds());
		}
		if(!ObjectUtils.isEmpty(closedPersonSxVctmztn.getTxtSupervisionContactDesc()) && !ObjectUtils.isEmpty(fwdPersonSxVctmztn.getTxtSupervisionContactDesc())) {
			txtSupervisionContactDesc = new StringBuilder();
			txtSupervisionContactDesc.append(fwdPersonSxVctmztn.getTxtSupervisionContactDesc()).append("\n").append(closedPersonSxVctmztn.getTxtSupervisionContactDesc());
			if(strLengthCheck.test(txtSupervisionContactDesc)) {
			 fwdPersonSxVctmztn.setTxtSupervisionContactDesc(txtSupervisionContactDesc.toString());
			}
		}
		else if(!ObjectUtils.isEmpty(closedPersonSxVctmztn.getTxtSupervisionContactDesc()) && ObjectUtils.isEmpty(fwdPersonSxVctmztn.getTxtSupervisionContactDesc())) {
			fwdPersonSxVctmztn.setTxtSupervisionContactDesc(closedPersonSxVctmztn.getTxtSupervisionContactDesc());
		}


		sessionFactory.getCurrentSession().saveOrUpdate(fwdPersonSxVctmztn);
	}


	/**
	 * Checks if the closed person exists in an RCL INV and then calls processPersonMergeWorkloadAlerts to create alerts.
	 * @param persMergeValueBean
	 */
	private void processWorkloadAlerts(PersonMergeSplitDto persMergeValueBean){

		List<StagePersonValueDto> openRCLINVStages=workLoadDao.getOpenRCLINVStagesForPerson(persMergeValueBean.getIdClosedPerson());

		if(!openRCLINVStages.isEmpty()){
			openRCLINVStages.forEach(stagePersonValueDto -> {
				SelectStageDto intakeStage = caseSummaryDao.getStage(stagePersonValueDto.getIdStage(), ServiceConstants.STAGE_PRIOR);
				StagePersonValueDto fwdPersWithRCLINVInfo=new StagePersonValueDto();
				fwdPersWithRCLINVInfo.setIdPerson(persMergeValueBean.getIdForwardPerson());
				fwdPersWithRCLINVInfo.setIdStage(stagePersonValueDto.getIdStage());
				fwdPersWithRCLINVInfo.setIdCase(stagePersonValueDto.getIdCase());

				workloadService.processPersonMergeWorkloadAlerts(fwdPersWithRCLINVInfo,intakeStage
						,persMergeValueBean.getIdPersonMergeWorker(),true);
			});

		}
	}

}
