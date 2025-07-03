package us.tx.state.dfps.service.placement.daoimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.casepackage.dto.CSAEpisodeDto;
import us.tx.state.dfps.service.casepackage.dto.CsaEpisodesIncdntDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanAdtnlSctnDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEducationDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEmtnlThrptcDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanHealthCareSummaryDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanQrtpPrmnncyMeetingDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanLegalGrdnshpDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanTransAdltAbvDtlDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.person.dto.EducationHistoryDto;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.AllegationCpsInvstDtlDto;
import us.tx.state.dfps.service.placement.dto.CPAdoptionDto;
import us.tx.state.dfps.service.placement.dto.EventChildPlanDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.RsrcAddrPhoneDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.placement.dto.TemporaryAbsenceDto;
import us.tx.state.dfps.service.placement.dto.VisitationPlanInfoDtlsDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CommonApplicationDaoImpl implemented all operation defined in
 * CommonApplicationDao Interface to fetch the records from table which are
 * mapped Placement module. Feb 9, 2018- 2:18:50 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class CommonApplicationDaoImpl implements CommonApplicationDao {

	@Value("${CommonApplicationDaoImpl.getStagePersonCaseDtl}")
	String getStagePersonCaseDtlSql;

	@Value("${CommonApplicationDaoImpl.getPersonLocDtls}")
	String getPersonLocDtlsSql;

	@Value("${CommonApplicationDaoImpl.getAllegationCpsInvstDtls}")
	String getAllegationCpsInvstDtlsSql;

	@Value("${CommonApplicationDaoImpl.getEventChildPlanDtls}")
	String getEventChildPlanDtlsSql;

	@Value("${CommonApplicationDaoImpl.getNameDetails}")
	String getNameDetailsSql;

	@Value("${commonAplicationDaoImpl.getFceEligibilityDtls}")
	String getFceEligibilityDtlsSql;

	@Value("${commonAplicationDaoImpl.getCSAEpisodesDtls}")
	String getCSAEpisodesDtlsSql;

	@Value("${commonAplicationDaoImpl.getPlcmntDtls}")
	String getPlcmntDtlsSql;

	@Value("${commonAplicationDaoImpl.getServiceLevelInfo}")
	String getServiceLevelInfoSql;

	@Value("${commonAplicationDaoImpl.getTrfckngDtl}")
	String getTrfckngDtlSql;

	@Value("${commonAplicationDaoImpl.getEducationDtl}")
	String getEducationDtlSql;

	@Value("${commonAplicationDaoImpl.getRsrcAddrPhoneDtl}")
	String getRsrcAddrPhoneDtlSql;

	@Value("${commonAplicationDaoImpl.getVisitationPlanInfoDtl}")
	String getVisitationPlanInfoDtlSql;

	@Value("${commonAplicationDaoImpl.getPlcmntLogDtls}")
	String getPlcmntLogDtlsSql;

	@Value("${commonAplicationDaoImpl.getCSAEpisodesClosedDtls}")
	String getCSAEpisodesClosedDtlsSql;
	
	@Value("${commonAplicationDaoImpl.getCSAEpisodesClosedIncdntDtl}")
	String getCSAEpisodesClosedIncdntDtlSql;

	@Value("${commonAplicationDaoImpl.getTemporaryAbsenceList}")
	String getTemporaryAbsenceListSql;
	
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Method Description: This method is used to retrieve the Stage,Person,
	 * Case and StagePersonLink information by passing idStage as input request.
	 * Dam Name: CSEC15D
	 * 
	 * @param idStage
	 * @param cdStagePersRole
	 * @return StagePersonLinkCaseDto
	 */
	@Override
	public StagePersonLinkCaseDto getStagePersonCaseDtl(Long idStage, String cdStagePersRole) {
		StagePersonLinkCaseDto stagePersonLinkCaseDto = new StagePersonLinkCaseDto();
		stagePersonLinkCaseDto = (StagePersonLinkCaseDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getStagePersonCaseDtlSql).setParameter("idStage", idStage)
				.setParameter("cdStagePersRole", cdStagePersRole)).addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("cdStageType", StandardBasicTypes.STRING)
						.addScalar("idUnit", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("dtStageClose", StandardBasicTypes.DATE)
						.addScalar("cdStageClassification", StandardBasicTypes.STRING)
						.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
						.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
						.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
						.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
						.addScalar("indStageClose", StandardBasicTypes.STRING)
						.addScalar("txtStagePriorityCmnts", StandardBasicTypes.STRING)
						.addScalar("cdStageCnty", StandardBasicTypes.STRING)
						.addScalar("nmStage", StandardBasicTypes.STRING)
						.addScalar("cdStageRegion", StandardBasicTypes.STRING)
						.addScalar("dtStageStart", StandardBasicTypes.DATE)
						.addScalar("idSituation", StandardBasicTypes.LONG)
						.addScalar("cdStageProgram", StandardBasicTypes.STRING)
						.addScalar("cdStage", StandardBasicTypes.STRING)
						.addScalar("txtStageClosureCmnts", StandardBasicTypes.STRING)
						.addScalar("cdCaseProgram", StandardBasicTypes.STRING)
						.addScalar("cdCaseCounty", StandardBasicTypes.STRING)
						.addScalar("cdCaseSpecialHandling", StandardBasicTypes.STRING)
						.addScalar("indCaseWorkerSafety", StandardBasicTypes.STRING)
						.addScalar("txtCaseWorkerSafety", StandardBasicTypes.STRING)
						.addScalar("txtCaseSensitiveCmnts", StandardBasicTypes.STRING)
						.addScalar("indCaseSensitive", StandardBasicTypes.STRING)
						.addScalar("indCaseArchived", StandardBasicTypes.STRING)
						.addScalar("dtCaseClosed", StandardBasicTypes.DATE)
						.addScalar("cdCaseRegion", StandardBasicTypes.STRING)
						.addScalar("dtCaseOpened", StandardBasicTypes.DATE)
						.addScalar("nmCase", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("cdPersonSex", StandardBasicTypes.STRING)
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.addScalar("nbrPersonAge", StandardBasicTypes.SHORT)
						.addScalar("dtPersonDeath", StandardBasicTypes.DATE)
						.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
						.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
						.addScalar("cdPersonChar", StandardBasicTypes.STRING)
						.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
						.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
						.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
						.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
						.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
						.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
						.addScalar("txtPersonOccupation", StandardBasicTypes.STRING)
						.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
						.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
						.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
						.addScalar("idStagePersonLink", StandardBasicTypes.LONG)
						.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
						.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
						.addScalar("cdStagePersType", StandardBasicTypes.STRING)
						.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
						.addScalar("txtStagePersNotes", StandardBasicTypes.STRING)
						.addScalar("dtStagePersLink", StandardBasicTypes.DATE)
						.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
						.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
						.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
						.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
						.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
						.addScalar("nmPersonLast", StandardBasicTypes.STRING)
						.addScalar("dtCurrent", StandardBasicTypes.DATE)
						.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(StagePersonLinkCaseDto.class)).uniqueResult();
		return stagePersonLinkCaseDto;
	}

	/**
	 * Method Description: This method is used to retrieve the child's most
	 * recent authorized level of care by passing idPerson and cdPlocType as
	 * input request. Dam Name: CSES35D
	 * 
	 * @param idPerson
	 * @param cdPlocType
	 * @return List<PersonLocDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonLocDto> getPersonLocDtls(Long idPerson, String cdPlocType) {
		List<PersonLocDto> personLocDtolist = new ArrayList<PersonLocDto>();
		personLocDtolist = (List<PersonLocDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonLocDtlsSql).setParameter("idPerson", idPerson)
				.setParameter("cdPlocType", cdPlocType)).addScalar("idPlocEvent", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("cdPlocChild", StandardBasicTypes.STRING)
						.addScalar("cdPlocType", StandardBasicTypes.STRING)
						.addScalar("dtPlocEnd", StandardBasicTypes.DATE)
						.addScalar("dtPlocStart", StandardBasicTypes.DATE)
						.addScalar("indPlocCsupSend", StandardBasicTypes.STRING)
						.addScalar("indPlocWriteHistory", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PersonLocDto.class)).list();

		return personLocDtolist;
	}

	/**
	 * Method Description: This method is used to retrieve tall allegation types
	 * and dispositions associated with the Id Person of the child by passing
	 * idPerson as input request. Dam Name: CLSS29D
	 * 
	 * @param idPersonVictim
	 * @return AllegationCpsInvstDtlDto
	 */
	@SuppressWarnings("unchecked")
	public List<AllegationCpsInvstDtlDto> getAllegationCpsInvstDetails(Long idPersonVictim) {
		List<AllegationCpsInvstDtlDto> allegationCpsInvstDtlDtoList = new ArrayList<AllegationCpsInvstDtlDto>();
		allegationCpsInvstDtlDtoList = (List<AllegationCpsInvstDtlDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getAllegationCpsInvstDtlsSql).setParameter("idPersonVictim", idPersonVictim))
						.addScalar("idAllegation", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idAllegationStage", StandardBasicTypes.LONG)
						.addScalar("idPersonVictim", StandardBasicTypes.LONG)
						.addScalar("idPersonAllegedPerpetrator", StandardBasicTypes.LONG)
						.addScalar("cdAllegIncidentStage", StandardBasicTypes.STRING)
						.addScalar("txtAllegDuration", StandardBasicTypes.STRING)
						.addScalar("cdAllegType", StandardBasicTypes.STRING)
						.addScalar("cdAllegDisposition", StandardBasicTypes.STRING)
						.addScalar("cdAllegSeverity", StandardBasicTypes.STRING)
						.addScalar("indAllegCancelHist", StandardBasicTypes.STRING)
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("dtCpsInvstDtlComplt", StandardBasicTypes.DATE)
						.addScalar("dtCpsInvstDtlBegun", StandardBasicTypes.DATE)
						.addScalar("indCpsInvstSafetyPln", StandardBasicTypes.STRING)
						.addScalar("indCpsInvstDtlRaNa", StandardBasicTypes.STRING)
						.addScalar("dtCpsInvstDtlAssigned", StandardBasicTypes.DATE)
						.addScalar("dtCpsInvstDtlIntake", StandardBasicTypes.DATE)
						.addScalar("cdCpsInvstDtlFamIncm", StandardBasicTypes.STRING)
						.addScalar("indCpsInvstDtlEaConcl", StandardBasicTypes.STRING)
						.addScalar("cdCpsInvstDtlOvrllDisptn", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(AllegationCpsInvstDtlDto.class)).list();
		return allegationCpsInvstDtlDtoList;
	}

	/**
	 * Method Description: This method is used to retrieve the most recent row
	 * from the Child Plan and Event table by passing idPerson as input request.
	 * Dam Name: CSEC20D
	 * 
	 * @param idPerson
	 * @return EventChildPlanDto
	 */
	public EventChildPlanDto getEventChildPlanDtls(Long idPerson) {

		EventChildPlanDto eventChildPlanDto = new EventChildPlanDto();
		eventChildPlanDto = (EventChildPlanDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getEventChildPlanDtlsSql).setParameter("idPerson", idPerson))
						.addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.STRING)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("cdCspPlanPermGoal", StandardBasicTypes.STRING)
						.addScalar("cdCspPlanType", StandardBasicTypes.STRING)
						.addScalar("dtCspPermGoalTarget", StandardBasicTypes.DATE)
						.addScalar("dtCspNextReview", StandardBasicTypes.DATE)
						.addScalar("txtCspLengthOfStay", StandardBasicTypes.STRING)
						.addScalar("txtCspLosDiscrepancy", StandardBasicTypes.STRING)
						.addScalar("txtCspParticipComment", StandardBasicTypes.STRING)
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("cdEventType", StandardBasicTypes.STRING)
						.addScalar("idEventPerson", StandardBasicTypes.LONG)
						.addScalar("cdTask", StandardBasicTypes.STRING)
						.addScalar("txtEventDescr", StandardBasicTypes.STRING)
						.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
						.addScalar("cdEventStatus", StandardBasicTypes.STRING)
						.addScalar("cdChildLglGrdnship", StandardBasicTypes.STRING)
						.addScalar("cdChildAdoptdDmstc", StandardBasicTypes.STRING)
						.addScalar("cdChildAdoptdIntnl", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(EventChildPlanDto.class)).uniqueResult();
		return eventChildPlanDto;

	}

	/**
	 * Method Description: This method is used to retrieve the name of the
	 * Primary Child from Name table by passing idPerson as input request. Dam
	 * Name: CSEC35D
	 * 
	 * @param idPerson
	 * @return NameDetailDto
	 */
	@SuppressWarnings("unchecked")
	public NameDetailDto getNameDetails(Long idPerson) {
		List<NameDetailDto> nameDetailDtoLst = new ArrayList<NameDetailDto>();
		nameDetailDtoLst = (List<NameDetailDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getNameDetailsSql).setParameter("idPerson", idPerson))
						.addScalar("idName", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.STRING)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("indNameInvalid", StandardBasicTypes.STRING)
						.addScalar("nmNameFirst", StandardBasicTypes.STRING)
						.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
						.addScalar("nmNameLast", StandardBasicTypes.STRING)
						.addScalar("indNamePrimary", StandardBasicTypes.STRING)
						.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
						.addScalar("dtNameStartDate", StandardBasicTypes.DATE)
						.addScalar("dtNameEndDate", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(NameDetailDto.class)).list();
		// Query return more than one record, so change the return type to list
		// and getting the first element from the result - Added this change for
		// warranty defect 11516
		if (!ObjectUtils.isEmpty(nameDetailDtoLst)) {
			return nameDetailDtoLst.get(ServiceConstants.Zero_INT);
		}
		return new NameDetailDto();
	}

	/**
	 * Method Description: This Method will retrieve the child Medicaid Number
	 * and the Medicaid id number.DAM: CCMN72D
	 * 
	 * @param idPerson
	 * @param cdPersonIdType,
	 * @param indPersonIdInvalid,
	 * @param dtPersonIdEnd
	 * @return PersonIdDto
	 */
	@Override
	public PersonIdDto getMedicaidNbrByPersonId(Long idPerson, String cdPersonIdType, String indPersonIdInvalid,
			Date dtPersonIdEnd) {
		PersonIdDto personIdDto = new PersonIdDto();
		personIdDto = (PersonIdDto) sessionFactory.getCurrentSession().createCriteria(PersonId.class)
				.setProjection(
						Projections.projectionList().add(Projections.property("cdPersonIdType"), "cdPersonIdType")
								.add(Projections.property("indPersonIdInvalid"), "indPersonIdInvalid")
								.add(Projections.property("dtPersonIdEnd"), "dtPersonIdEnd")
								.add(Projections.property("dtPersonIdStart"), "dtPersonIdStart")
								.add(Projections.property("descPersonId"), "idDescPerson")
								.add(Projections.property("person.idPerson"), "idPerson")
								.add(Projections.property("idPersonId"), "idPersonId")
								.add(Projections.property("nbrPersonIdNumber"), "personIdNumber")
								.add(Projections.property("dtLastUpdate"), "dtLastUpdate"))
				.add(Restrictions.eq("person.idPerson", idPerson))
				.add(Restrictions.eq("cdPersonIdType", cdPersonIdType))
				.add(Restrictions.eq("indPersonIdInvalid", indPersonIdInvalid))
				.add(Restrictions.eq("dtPersonIdEnd", dtPersonIdEnd))
				.setResultTransformer(Transformers.aliasToBean(PersonIdDto.class)).uniqueResult();

		return personIdDto;
	}

	/**
	 * Method Description: This Method will retrieve the person details
	 * information from PERSON_DTL table.DAM Name:CSES31D
	 * 
	 * @param personId
	 * @return PersonDtlDto
	 */
	public PersonDtlDto getPersonDtlById(long idPerson) {
		PersonDtlDto personDtlDto = new PersonDtlDto();

		personDtlDto = (PersonDtlDto) sessionFactory.getCurrentSession().createCriteria(PersonDtl.class)
				.setProjection(Projections.projectionList().add(Projections.property("idPerson"), "idPerson")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("amtPersonAnnualIncome"), "amtPersonAnnualIncome")
						.add(Projections.property("cdPersonBirthCity"), "cdPersonBirthCity")
						.add(Projections.property("cdPersonBirthCountry"), "cdPersonBirthCountry")
						.add(Projections.property("cdPersonBirthCounty"), "cdPersonBirthCounty")
						.add(Projections.property("cdPersonBirthState"), "cdPersonBirthState")
						.add(Projections.property("cdPersonCitizenship"), "cdPersonCitizenship")
						.add(Projections.property("cdPersonEyeColor"), "cdPersonEyeColor")
						.add(Projections.property("cdPersonFaHomeRole"), "cdPersonFaHomeRole")
						.add(Projections.property("cdPersonHairColor"), "cdPersonHairColor")
						.add(Projections.property("cdPersonHighestEduc"), "cdPersonHighestEduc")
						.add(Projections.property("indPersonNoUsBrn"), "indPersonNoUsBrn")
						.add(Projections.property("nmPersonLastEmployer"), "nmPersonLastEmployer")
						.add(Projections.property("nmPersonMaidenName"), "nmPersonMaidenName")
						.add(Projections.property("qtyPersonHeightFeet"), "qtyPersonHeightFeet")
						.add(Projections.property("qtyPersonHeightInches"), "qtyPersonHeightInches")
						.add(Projections.property("qtyPersonWeight"), "qtyPersonWeight")
						.add(Projections.property("cdRemovalMothrMarrd"), "removalMothrMarrd")
						.add(Projections.property("cdDocType"), "cdDocType")
						.add(Projections.property("dtUsEntry"), "dtUsEntry")
						.add(Projections.property("txtAlienDocIdentifier"), "alienDocIdentifier")
						.add(Projections.property("dtAlienStatusExpiration"), "dtAlienStatusExpiration")
						.add(Projections.property("dtMostRecentAdoption"), "dtMostRecentAdoption")
						.add(Projections.property("cdAgencyAdoption"), "cdAgencyAdoption"))
				.add(Restrictions.eq("idPerson", idPerson))
				.setResultTransformer(Transformers.aliasToBean(PersonDtlDto.class)).uniqueResult();
		return personDtlDto;
	}

	/**
	 * 
	 * Method Name: getFceEligibility. Method Description: This Method to get
	 * FceEligiblity details by passing idPerson of the child
	 * 
	 * @param idPerson
	 * @return FceEligibilityDto
	 */
	@Override
	public FceEligibilityDto getFceEligibility(Long idPerson) {
		FceEligibilityDto fceEligibilityDto = new FceEligibilityDto();
		fceEligibilityDto = (FceEligibilityDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getFceEligibilityDtlsSql).setParameter("idPerson", idPerson))
						.addScalar("idFceEligibility", StandardBasicTypes.LONG)
						.addScalar("idFceApplication", StandardBasicTypes.LONG)
						.addScalar("idFceReview", StandardBasicTypes.LONG)
						.addScalar("idFcePerson", StandardBasicTypes.LONG)
						.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("idEligibilityEvent", StandardBasicTypes.LONG)
						.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("cdEligibilityActual", StandardBasicTypes.STRING)
						.addScalar("cdEligibilitySelected", StandardBasicTypes.STRING)
						.addScalar("cdMedicaidEligibilityType", StandardBasicTypes.STRING)
						.addScalar("cdPersonCitizenship", StandardBasicTypes.STRING)
						.addScalar("cdBlocChild", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(FceEligibilityDto.class)).uniqueResult();

		return fceEligibilityDto;
	}

	/**
	 * 
	 * Method Name: getCSAEpisodesDtl. Method Description: This Method to get
	 * CSAEpisode details by passing idPerson of the child
	 * 
	 * @param idPerson
	 * @return CSAEpisodeDto
	 */
	@Override
	public CSAEpisodeDto getCSAEpisodesDtl(Long idPerson) {
		CSAEpisodeDto csaEpisodeDto = new CSAEpisodeDto();
		csaEpisodeDto = (CSAEpisodeDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCSAEpisodesDtlsSql).setParameter("idPerson", idPerson))
						.addScalar("idCsaEpisodes", StandardBasicTypes.LONG)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("dtEpisodeStart", StandardBasicTypes.DATE)
						.addScalar("dtEpisodeEnd", StandardBasicTypes.DATE)
						.addScalar("txtEndDtJustfcnt", StandardBasicTypes.STRING)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(CSAEpisodeDto.class)).uniqueResult();
		return csaEpisodeDto;
	}

	/**
	 * 
	 * Method Name: getCSAEpisodesIncdntDtl. Method Description: This Method to
	 * get CSAEpisode Incident details by passing idCsaEpisodes of the child
	 * 
	 * @param idCsaEpisodes
	 * @return CsaEpisodesIncdntDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CsaEpisodesIncdntDto> getCSAEpisodesIncdntDtl(Long idCsaEpisodes) {
		List<CsaEpisodesIncdntDto> csaEpisodesIncdntDtoLst = new ArrayList<CsaEpisodesIncdntDto>();
		csaEpisodesIncdntDtoLst = (List<CsaEpisodesIncdntDto>) sessionFactory.getCurrentSession()
				.createCriteria(CsaEpisodesIncdnt.class)
				.setProjection(Projections.projectionList()
						.add(Projections.property("idCsaEpisodesIncdnt"), "idCsaEpisodesIncdnt")
						.add(Projections.property("csaEpisode.idCsaEpisodes"), "idCsaEpisodes")
						.add(Projections.property("dtIncdnt"), "dtIncdnt")
						.add(Projections.property("txtVctmInfo"), "txtVctmInfo")
						.add(Projections.property("txtIncdntDesc"), "txtIncdntDesc")
						.add(Projections.property("indAppxDt"), "indAppxDt"))
				.add(Restrictions.eq("csaEpisode.idCsaEpisodes", idCsaEpisodes))
				.addOrder(Order.desc("idCsaEpisodesIncdnt"))
				.setResultTransformer(Transformers.aliasToBean(CsaEpisodesIncdntDto.class)).list();
		return csaEpisodesIncdntDtoLst;
	}
	
	
	/**
	 * 
	 * Method Name: getCSAEpisodesIncdntDtl. Method Description: This Method to
	 * get CSAEpisode Incident details by passing idPerson of the child
	 * 
	 * @param idCsaEpisodes
	 * @return CsaEpisodesIncdntDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CsaEpisodesIncdntDto> getCSAEpisodesIncdntDtlByIdPerson(Long idPerson) {
		List<CsaEpisodesIncdntDto> csaEpisodesIncdntDtoLst = new ArrayList<CsaEpisodesIncdntDto>();
		csaEpisodesIncdntDtoLst = (List<CsaEpisodesIncdntDto>) sessionFactory.getCurrentSession()
				.createCriteria(CsaEpisodesIncdnt.class)
				.setProjection(Projections.projectionList()
						.add(Projections.property("idCsaEpisodesIncdnt"), "idCsaEpisodesIncdnt")
						.add(Projections.property("csaEpisode.idCsaEpisodes"), "idCsaEpisodes")
						.add(Projections.property("dtIncdnt"), "dtIncdnt")
						.add(Projections.property("txtVctmInfo"), "txtVctmInfo")
						.add(Projections.property("txtIncdntDesc"), "txtIncdntDesc")
						.add(Projections.property("indAppxDt"), "indAppxDt"))
				.add(Restrictions.eq("idPerson", idPerson))
				.addOrder(Order.desc("idCsaEpisodesIncdnt"))
				.setResultTransformer(Transformers.aliasToBean(CsaEpisodesIncdntDto.class)).list();
		return csaEpisodesIncdntDtoLst;
	}

	/**
	 * 
	 * Method Name: getPlacementDtl. Method Description: This Method to get
	 * latest placement details by passing idPerson of the child
	 * 
	 * @param idPlcmntChild
	 * @return PlacementDtlDto
	 */
	@Override
	public PlacementDtlDto getPlacementDtl(Long idPlcmntChild) {
		PlacementDtlDto placementDtlDto = new PlacementDtlDto();
		placementDtlDto = (PlacementDtlDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPlcmntDtlsSql).setParameter("idPlcmntChild", idPlcmntChild))
						.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
						.addScalar("idPersonPlcmtChild", StandardBasicTypes.LONG)
						.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
						.addScalar("idRsrcAgency", StandardBasicTypes.LONG)
						.addScalar("idRsrcFacil", StandardBasicTypes.LONG)
						.addScalar("addrPlcmtCity", StandardBasicTypes.STRING)
						.addScalar("addrPlcmtCnty", StandardBasicTypes.STRING)
						.addScalar("addrPlcmtLn1", StandardBasicTypes.STRING)
						.addScalar("addrPlcmtLn2", StandardBasicTypes.STRING)
						.addScalar("addrPlcmtSt", StandardBasicTypes.STRING)
						.addScalar("addrPlcmtZip", StandardBasicTypes.STRING)
						.addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
						.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
						.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING)
						.addScalar("cdPlcmtActPlanned", StandardBasicTypes.STRING)
						.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
						.addScalar("cdPlcmtService", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PlacementDtlDto.class)).uniqueResult();
		return placementDtlDto;
	}

	/**
	 * Method Name: getServiceLevelInfo. Method Description: This method is used
	 * to retrieve the child's most recent authorized level of care by passing
	 * idPerson as input request.
	 * 
	 * @param idPerson
	 * @return PersonLocDto
	 */
	@Override
	public PersonLocDto getServiceLevelInfo(Long idPerson) {
		PersonLocDto serviceLvlpersonLocDto = new PersonLocDto();
		serviceLvlpersonLocDto = (PersonLocDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getServiceLevelInfoSql).setParameter("idPerson", idPerson))
						.addScalar("idPlocEvent", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("dtPlocEnd", StandardBasicTypes.DATE)
						.addScalar("cdPlocChild", StandardBasicTypes.STRING)
						.addScalar("cdPlocType", StandardBasicTypes.STRING)
						.addScalar("dtPlocStart", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(PersonLocDto.class)).uniqueResult();
		return serviceLvlpersonLocDto;
	}

	/**
	 * Method Name: getServiceLevelInfo. Method Description: This method is used
	 * to retrieve the child's most recent trafficking by passing idPerson as
	 * input request.
	 * 
	 * @param idPerson
	 * @return TraffickingDto
	 */
	@Override
	public TraffickingDto getTrfckngDtl(Long idPerson) {
		TraffickingDto traffickingDto = new TraffickingDto();
		traffickingDto = (TraffickingDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getTrfckngDtlSql).setParameter("idPerson", idPerson))
						.addScalar("idTrfckngDtl", StandardBasicTypes.LONG)
						.addScalar("cdtrfckngType", StandardBasicTypes.STRING)
						.addScalar("cdtrfckngStat", StandardBasicTypes.STRING)
						.addScalar("cdPrdVctmAgncy", StandardBasicTypes.STRING)
						.addScalar("cdNotifctntype", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(TraffickingDto.class)).uniqueResult();
		return traffickingDto;
	}

	/**
	 * 
	 * Method Name: getChildPlanEmtnlSectnDtl. Method Description: This Method
	 * to get ChildPlanEmtnlSectnDtl by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanEmtnlThrptcDtlDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanEmtnlThrptcDtlDto getChildPlanEmtnlSectnDtl(Long idChildPlanEvent) {
		List<ChildPlanEmtnlThrptcDtlDto> cpEmtnlThrptcDtlDtoLst = new ArrayList<ChildPlanEmtnlThrptcDtlDto>();
		cpEmtnlThrptcDtlDtoLst = (List<ChildPlanEmtnlThrptcDtlDto>) sessionFactory.getCurrentSession()
				.createCriteria(CpEmtnlThrptcDtl.class)
				.setProjection(Projections.projectionList().add(Projections.property("idCpEmtnlTpDtl"), "idEmtnlTpDtl")
						.add(Projections.property("childPlan.idChildPlanEvent"), "idChildPlanEvent")
						.add(Projections.property("txtSpcfcSvcsForChild"), "txtSpcfcSvcsForChild")
						.add(Projections.property("txtEmtnlNeeds"), "txtEmtnlNeeds")
						.add(Projections.property("txtThrpstDiagns"), "txtTherapistDiagnsis")
						.add(Projections.property("indChildCansAsmnt"), "indChildCansAsmnt")
						.add(Projections.property("dtCansAsmnt"), "dtCansAssmt"))
				.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent)).addOrder(Order.desc("dtLastUpdate"))
				.setResultTransformer(Transformers.aliasToBean(ChildPlanEmtnlThrptcDtlDto.class)).list();
		// Query return more than one record, so change the return type to list
		// and getting the first element from the result - Added this change for
		// warranty defect 12204
		if (!ObjectUtils.isEmpty(cpEmtnlThrptcDtlDtoLst)) {
			return cpEmtnlThrptcDtlDtoLst.get(ServiceConstants.Zero_INT);
		} else {
			return new ChildPlanEmtnlThrptcDtlDto();
		}
	}

	/**
	 * 
	 * Method Name: getChildPlanQrtpPtmDtl. Method Description: This Method
	 * to get ChildPlanQrtpPtmDtl by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanQrtpPrmnncyMeetingDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanQrtpPrmnncyMeetingDto getChildPlanQrtpPtmDtl(Long idChildPlanEvent) {
		List<ChildPlanQrtpPrmnncyMeetingDto> cpQrtpPtmDtoLst = new ArrayList<ChildPlanQrtpPrmnncyMeetingDto>();
		cpQrtpPtmDtoLst = (List<ChildPlanQrtpPrmnncyMeetingDto>) sessionFactory.getCurrentSession()
				.createCriteria(CpQrtpPrmnTmMtng.class)
				.setProjection(
						Projections.projectionList().add(Projections.property("idCpQrtpPtm"), "idCpQrtpPtm")
								.add(Projections.property("indCurrentQrtp"), "indCurrentQrtp")
								.add(Projections.property("dtPtmQrtp"), "dtPtmQrtp")
								.add(Projections.property("txtFcltatorName"), "txtFcltatorName")
								.add(Projections.property("txtMtngLocation"), "txtMtngLocation")
								.add(Projections.property("facilitatorTitle"), "facilitatorTitle")
								.add(Projections.property("txtOthrMtngLctn"), "txtOthrMtngLctn"))
				.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent)).addOrder(Order.desc("dtLastUpdate"))
				.setResultTransformer(Transformers.aliasToBean(ChildPlanQrtpPrmnncyMeetingDto.class)).list();
		// Query return more than one record, so change the return type to list
		// and getting the first element from the result - Added this change for
		// warranty defect 12204
		if (!ObjectUtils.isEmpty(cpQrtpPtmDtoLst)) {
			return cpQrtpPtmDtoLst.get(ServiceConstants.Zero_INT);
		} else {
			return new ChildPlanQrtpPrmnncyMeetingDto();
		}
	}

	/**
	 *
	 * Method Name: getChildPlanHlthCareSummDtl. Method Description: This Method
	 * to get ChildPlanHlthCareSummDtl by passing idChildPlanEvent of the child
	 *
	 * @param idChildPlanEvent
	 * @return ChildPlanHealthCareSummaryDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanHealthCareSummaryDto getChildPlanHlthCareSummDtl(Long idChildPlanEvent) {
		List<ChildPlanHealthCareSummaryDto> cpHealthCareSummaryDtoLst = new ArrayList<ChildPlanHealthCareSummaryDto>();
		cpHealthCareSummaryDtoLst = (List<ChildPlanHealthCareSummaryDto>) sessionFactory.getCurrentSession()
				.createCriteria(CpHlthCareSumm.class)
				.setProjection(
						Projections.projectionList().add(Projections.property("idCpHlthCareSumm"), "idHlthCareSumm")
								.add(Projections.property("txtNrsngHours"), "txtNursingHrs")
								.add(Projections.property("dtLastTxMedChckp"), "dtLastTxMedChkp")
								.add(Projections.property("txtHomeHlthCntctInfo"), "txtHmeHlthCnctInfo")
								.add(Projections.property("txtLstDmeSupls"), "txtLstDmeSupls")
								.add(Projections.property("indAmblncTrnsprt"), "indAmbulnceTrnsprt")
								.add(Projections.property("indDnr"), "indDnr"))
				.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent)).addOrder(Order.desc("dtLastUpdate"))
				.setResultTransformer(Transformers.aliasToBean(ChildPlanHealthCareSummaryDto.class)).list();
		// Query return more than one record, so change the return type to list
		// and getting the first element from the result - Added this change for
		// warranty defect 12204
		if (!ObjectUtils.isEmpty(cpHealthCareSummaryDtoLst)) {
			return cpHealthCareSummaryDtoLst.get(ServiceConstants.Zero_INT);
		} else {
			return new ChildPlanHealthCareSummaryDto();
		}
	}




	/**
	 * 
	 * Method Name: getEducationDtls Method Description: This Method is used to
	 * retrieve the child's most recent education Detail information.
	 * 
	 * @param idPerson
	 * @return EducationHistoryDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EducationHistoryDto> getEducationDtls(Long idPerson) {
		List<EducationHistoryDto> educationHistoryDtoList = new ArrayList<EducationHistoryDto>();
		educationHistoryDtoList = (List<EducationHistoryDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getEducationDtlSql).setParameter("idPerson", idPerson))
						.addScalar("idEdHist", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("idResource", StandardBasicTypes.LONG)
						.addScalar("nmEdHistSchool", StandardBasicTypes.STRING)
						.addScalar("nmEdHistSchDist", StandardBasicTypes.STRING)
						.addScalar("cdEdHistEnrollGrade", StandardBasicTypes.STRING)
						.addScalar("cdEdHistWithdrawnGrade", StandardBasicTypes.STRING)
						.addScalar("dtEdHistEnrollDate", StandardBasicTypes.DATE)
						.addScalar("dtEdHistWithdrawn", StandardBasicTypes.DATE)
						.addScalar("dtLastArdiep", StandardBasicTypes.DATE)
						.addScalar("edHistPhone", StandardBasicTypes.STRING)
						.addScalar("addrEdHistStreetLn1", StandardBasicTypes.STRING)
						.addScalar("addrEdHistStreetLn2", StandardBasicTypes.STRING)
						.addScalar("addrEdHistCity", StandardBasicTypes.STRING)
						.addScalar("addrEdHistState", StandardBasicTypes.STRING)
						.addScalar("addrEdHistCnty", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(EducationHistoryDto.class)).list();
		return educationHistoryDtoList;
	}

	/**
	 * 
	 * Method Name: getRsrcAddrPhoneDtl Method Description: This Method is used
	 * to retrieve the child's most recent education Detail information.
	 * 
	 * @param idPerson
	 * @return EducationHistoryDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RsrcAddrPhoneDto getRsrcAddrPhoneDtl(Long idResource) {
		RsrcAddrPhoneDto rsrcAddrPhoneDto = new RsrcAddrPhoneDto();
		// Changed the Query to Fetch records with Latest Records and picking up the latest record in case of multiple records 
		// Warranty Defect Fix #10886
		List<RsrcAddrPhoneDto> rsrcAddrPhoneDtoList = new ArrayList<RsrcAddrPhoneDto>();
		rsrcAddrPhoneDtoList = (List<RsrcAddrPhoneDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getRsrcAddrPhoneDtlSql).setParameter("idResource", idResource))
						.addScalar("idRsrcAddress", StandardBasicTypes.LONG)
						.addScalar("addrRsrcAddrZip", StandardBasicTypes.STRING)
						.addScalar("cdRsrcAddrCounty", StandardBasicTypes.STRING)
						.addScalar("addrRsrcAddrAttn", StandardBasicTypes.STRING)
						.addScalar("cdRsrcAddrState", StandardBasicTypes.STRING)
						.addScalar("addrRsrcAddrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrRsrcAddrStLn2", StandardBasicTypes.STRING)
						.addScalar("cdRsrcAddrSchDist", StandardBasicTypes.STRING)
						.addScalar("cdRsrcAddrType", StandardBasicTypes.STRING)
						.addScalar("addrRsrcAddrCity", StandardBasicTypes.STRING)
						.addScalar("nmCnty", StandardBasicTypes.STRING).addScalar("nmCntry", StandardBasicTypes.STRING)
						.addScalar("idRsrcPhone", StandardBasicTypes.LONG)
						.addScalar("nbrRsrcPhone", StandardBasicTypes.STRING)
						.addScalar("cdRsrcPhoneType", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(RsrcAddrPhoneDto.class)).list();
		if(!ObjectUtils.isEmpty(rsrcAddrPhoneDtoList))
		{
			rsrcAddrPhoneDto=rsrcAddrPhoneDtoList.get(0);	
		}
		return rsrcAddrPhoneDto;
	}

	/**
	 * 
	 * Method Name: getChildPlanEducationDtl. Method Description: This Method to
	 * get ChildPlanEducationDtl by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanEducationDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanEducationDto getChildPlanEducationDtl(Long idChildPlanEvent) {
		List<ChildPlanEducationDto> cpEducationDtoLst = new ArrayList<ChildPlanEducationDto>();
		cpEducationDtoLst = (List<ChildPlanEducationDto>) sessionFactory.getCurrentSession().createCriteria(CpEductnDtl.class)
				.setProjection(Projections.projectionList().add(Projections.property("idCpEductnDtl"), "idEductnDtl")
						.add(Projections.property("indGradeLvl"), "indGrdLvl")
						.add(Projections.property("txtLstExtraActv"), "txtLstExtraActv")
						.add(Projections.property("indChildEnrlldSch"), "indChildEnrlldSch")
						.add(Projections.property("txtEductnStrngth"), "txtEductnStrength")
						.add(Projections.property("txtIntrvntnsSprt"), "txtIntrvntnsSprt"))
				.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent)).addOrder(Order.desc("dtLastUpdate"))
				.setResultTransformer(Transformers.aliasToBean(ChildPlanEducationDto.class)).list();
		// Query return more than one record, so change the return type to list
		// and getting the first element from the result - Added this change for
		// warranty defect 12204
		if (!ObjectUtils.isEmpty(cpEducationDtoLst)) {
			return cpEducationDtoLst.get(ServiceConstants.Zero_INT);
		} else {
			return new ChildPlanEducationDto();
		}
	}

	/**
	 * 
	 * Method Name: getChildPlanTranstmAdultAbv. Method Description: This Method
	 * to get ChildPlanTranstmAdultAbv by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanTransAdltAbvDtlDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanTransAdltAbvDtlDto getChildPlanTranstmAdultAbv(Long idChildPlanEvent) {
		List<ChildPlanTransAdltAbvDtlDto> cpTransAdltAbvDtlDtoLst = new ArrayList<ChildPlanTransAdltAbvDtlDto>();
		cpTransAdltAbvDtlDtoLst = (List<ChildPlanTransAdltAbvDtlDto>) sessionFactory.getCurrentSession()
				.createCriteria(CpTranstnAdultAbvDtl.class)
				.setProjection(Projections.projectionList()
						.add(Projections.property("idCpTranstnAdultAbvDtl"), "idTtsAdultAbvDtl")
						.add(Projections.property("indYouthSkillAssmnt"), "indYouthSkillAssmnt")
						.add(Projections.property("txtSkillChlng"), "txtSkillChlng")
						.add(Projections.property("txtExtnFostercare"), "txtExtnFostercare"))
				.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent)).addOrder(Order.desc("dtLastUpdate"))
				.setResultTransformer(Transformers.aliasToBean(ChildPlanTransAdltAbvDtlDto.class)).list();
		// Query return more than one record, so change the return type to list
		// and getting the first element from the result - Added this change for
		// warranty defect 12204
		if (!ObjectUtils.isEmpty(cpTransAdltAbvDtlDtoLst)) {
			return cpTransAdltAbvDtlDtoLst.get(ServiceConstants.Zero_INT);
		} else {
			return new ChildPlanTransAdltAbvDtlDto();
		}
	}

	/**
	 * 
	 * Method Name: getChildPlanAdtnlSctnDtls. Method Description: This Method
	 * to get ChildPlanAdtnlSctnDtls by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanAdtnlSctnDtlDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanAdtnlSctnDtlDto getChildPlanAdtnlSctnDtls(Long idChildPlanEvent) {
		List<ChildPlanAdtnlSctnDtlDto> cpAdtnlSctnDtlDtoLst = new ArrayList<ChildPlanAdtnlSctnDtlDto>();
		cpAdtnlSctnDtlDtoLst = (List<ChildPlanAdtnlSctnDtlDto>) sessionFactory.getCurrentSession()
				.createCriteria(CpAdtnlSctnDtl.class)
				.setProjection(
						Projections.projectionList().add(Projections.property("idCpAdtnlSctnDtls"), "idCpAdtnlSctnDtls")
								.add(Projections.property("indJuvnleJustcInvlvmnt"), "indJuvnleJustcInvlvmnt")
								.add(Projections.property("txtExplnJuvnleJustc"), "txtExplnJuvnleJustc"))
				.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent)).addOrder(Order.desc("dtLastUpdate"))
				.setResultTransformer(Transformers.aliasToBean(ChildPlanAdtnlSctnDtlDto.class)).list();
		// Query return more than one record, so change the return type to list
		// and getting the first element from the result - Added this change for
		// warranty defect 12204
		if (!ObjectUtils.isEmpty(cpAdtnlSctnDtlDtoLst)) {
			return cpAdtnlSctnDtlDtoLst.get(ServiceConstants.Zero_INT);
		} else {
			return new ChildPlanAdtnlSctnDtlDto();
		}
	}

	/**
	 * 
	 * Method Name: getChildPlanAdoptnDtls. Method Description: This Method to
	 * get ChildPlanAdoptnDtls by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return List<CPAdoptionDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CPAdoptionDto> getChildPlanAdoptnDtls(Long idChildPlanEvent) {
		List<CPAdoptionDto> cpAdoptionDtoList = new ArrayList<CPAdoptionDto>();
		cpAdoptionDtoList = (List<CPAdoptionDto>) sessionFactory.getCurrentSession().createCriteria(CpAdoptnDtl.class)
				.setProjection(Projections.projectionList().add(Projections.property("idCpAdoptnDtl"), "idCpAdoptnDtl")
						.add(Projections.property("cdAgncyType"), "cdAgncyType")
						.add(Projections.property("cdChildAdoptdType"), "cdChildAdoptdType")
						.add(Projections.property("cdCntryChildAdoptd"), "cdCntryChildAdoptd")
						.add(Projections.property("cdStateChildAdoptd"), "cdStateChildAdoptd")
						.add(Projections.property("nmAgncy"), "nmAgncy"))
				.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent))
				.setResultTransformer(Transformers.aliasToBean(CPAdoptionDto.class)).list();
		return cpAdoptionDtoList;
	}

	/**
	 * 
	 * Method Name: getVisitationPlanInfoDtl Method Description: This Method is
	 * used to retrieve the child's most recent Visitation Plan information.
	 * 
	 * @param idPerson
	 * @return List<VisitationPlanInfoDtlsDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<VisitationPlanInfoDtlsDto> getVisitationPlanInfoDtl(Long idPerson, Long idStage) {
		List<VisitationPlanInfoDtlsDto> visitationPlanInfoDtlsDtoLst = new ArrayList<VisitationPlanInfoDtlsDto>();
		visitationPlanInfoDtlsDtoLst = (List<VisitationPlanInfoDtlsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getVisitationPlanInfoDtlSql).setParameter("idPerson", idPerson)
				.setParameter("idStage", idStage)).addScalar("idVisitPlan", StandardBasicTypes.LONG)
						.addScalar("txtLngthOfVisit", StandardBasicTypes.STRING)
						.addScalar("txtVisitFreq", StandardBasicTypes.STRING)
						.addScalar("txtDayAndTime", StandardBasicTypes.STRING)
						.addScalar("txtVisitLoctn", StandardBasicTypes.STRING)
						.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
						.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(VisitationPlanInfoDtlsDto.class)).list();
		return visitationPlanInfoDtlsDtoLst;
	}

	/**
	 * 
	 * Method Name: getChildPlanLegalGrdnShpDtl. Method Description: This Method
	 * to get ChildPlanLegalGrdnShpDtl by passing idChildPlanEvent of the child
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanHealthCareSummaryDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChildPlanLegalGrdnshpDto getChildPlanLegalGrdnShpDtl(Long idChildPlanEvent) {
		// Warranty Defect - 11694 - To retrieve the Latest Legal
		List<ChildPlanLegalGrdnshpDto> childPlanLegalGrdnshpDtoList = null;
		ChildPlanLegalGrdnshpDto childPlanLegalGrdnshp = new ChildPlanLegalGrdnshpDto();
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpLglGrdnshp.class);
		cr.add(Restrictions.eq("childPlan.idChildPlanEvent", idChildPlanEvent));
		cr.addOrder(Order.desc("idCpLglGrdnshp"));
		List<CpLglGrdnshp> list = cr.list();

		if (!ObjectUtils.isEmpty(list)) {
			childPlanLegalGrdnshpDtoList = new ArrayList<>(list.size());
			for (CpLglGrdnshp cpLglGrdnshp : list) {
				ChildPlanLegalGrdnshpDto childPlanLegalGrdnshpDto = new ChildPlanLegalGrdnshpDto();
				DozerBeanMapper mapper = new DozerBeanMapper();
				List<String> mappingFiles = Arrays.asList("globalMapping.xml",
						"dozermappingforChildPlanLegalGuardian.xml");
				mapper.setMappingFiles(mappingFiles);
				mapper.map(cpLglGrdnshp, childPlanLegalGrdnshpDto);
				childPlanLegalGrdnshpDtoList.add(childPlanLegalGrdnshpDto);
			}
			childPlanLegalGrdnshp=childPlanLegalGrdnshpDtoList.get(0);
		}
		return childPlanLegalGrdnshp;
	}

	/**
	 * 
	 * Method Name: getPlacementDtl. Method Description: This Method to get
	 * latest placement details by passing idPerson of the child
	 * 
	 * @param idPlcmntChild
	 * @return PlacementDtlDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PlacementDtlDto> getPlacementLogDtl(Long idPlcmntChild) {
		List<PlacementDtlDto> placementLogDtlList = new ArrayList<PlacementDtlDto>();
		placementLogDtlList = (List<PlacementDtlDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPlcmntLogDtlsSql).setParameter("idPlcmntChild", idPlcmntChild))
						.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
						.addScalar("idPersonPlcmtChild", StandardBasicTypes.LONG)
						.addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
						.addScalar("idRsrcAgency", StandardBasicTypes.LONG)
						.addScalar("idRsrcFacil", StandardBasicTypes.LONG)
						.addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
						.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
						.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
						.addScalar("nmPlcmtFacil", StandardBasicTypes.STRING)
						.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PlacementDtlDto.class)).list();
		return placementLogDtlList;

	}
	
	/**
	 * 
	 * Method Name: getCSAEpisodesDtl. Method Description: This Method to get
	 * CSAEpisode closed details by passing idPerson of the child
	 * 
	 * @param idPerson
	 * @return CSAEpisodeDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CSAEpisodeDto> getCSAEpisodesClosedDtl(Long idPerson) {
		List<CSAEpisodeDto> csaEpisodeDtoList = new ArrayList<CSAEpisodeDto>();
		csaEpisodeDtoList = (List<CSAEpisodeDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCSAEpisodesClosedDtlsSql).setParameter("idPerson", idPerson))
						.addScalar("idCsaEpisodes", StandardBasicTypes.LONG)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("dtEpisodeStart", StandardBasicTypes.DATE)
						.addScalar("dtEpisodeEnd", StandardBasicTypes.DATE)
						.addScalar("txtEndDtJustfcnt", StandardBasicTypes.STRING)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(CSAEpisodeDto.class)).list();
		return csaEpisodeDtoList;
	}
	
	/**
	 * 
	 * Method Name: getCSAEpisodesIncdntDtl. Method Description: This Method to
	 * get CSAEpisode Closed Incident details by passing list of idCsaEpisodes of the child
	 * 
	 * @param idCsaEpisodes
	 * @return CsaEpisodesIncdntDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CsaEpisodesIncdntDto> getCSAEpisodesClosedIncdntDtl(List<Long> idCsaEpisodesList) {
		List<CsaEpisodesIncdntDto> CsaEpisodesIncdntDtoList = new ArrayList<CsaEpisodesIncdntDto>();
		CsaEpisodesIncdntDtoList = (List<CsaEpisodesIncdntDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCSAEpisodesClosedIncdntDtlSql).setParameterList("idCsaEpisodesList", idCsaEpisodesList))
						.addScalar("idCsaEpisodesIncdnt", StandardBasicTypes.LONG)
						.addScalar("idCsaEpisodes", StandardBasicTypes.LONG)
						.addScalar("dtIncdnt", StandardBasicTypes.DATE)
						.addScalar("txtVctmInfo", StandardBasicTypes.STRING)
						.addScalar("txtIncdntDesc", StandardBasicTypes.STRING)
						.addScalar("indAppxDt", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(CsaEpisodesIncdntDto.class)).list();
		return CsaEpisodesIncdntDtoList;
	}

	@Override
	public List<TemporaryAbsenceDto> getTemporaryAbsenceList(Long idPlcmntChild){
		List<TemporaryAbsenceDto> temporaryAbsenceList = new ArrayList<TemporaryAbsenceDto>();
		temporaryAbsenceList = (List<TemporaryAbsenceDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getTemporaryAbsenceListSql).setParameter("idPlcmntChild", idPlcmntChild))
				.addScalar("idPlacementTa", StandardBasicTypes.LONG)
				.addScalar("idLinkedPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("linkedPlacementDesc", StandardBasicTypes.STRING)
				.addScalar("dtTemporaryAbsenceStart", StandardBasicTypes.DATE)
				.addScalar("dtTemporaryAbsenceEnd", StandardBasicTypes.DATE)
				.addScalar("temporaryAbsenceType", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(TemporaryAbsenceDto.class)).list();
		return temporaryAbsenceList;
	}
}
