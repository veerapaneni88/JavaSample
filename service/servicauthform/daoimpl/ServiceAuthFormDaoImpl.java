package us.tx.state.dfps.service.servicauthform.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.approval.dto.ApprovalFormDataDto;
import us.tx.state.dfps.common.domain.PersonId;
import us.tx.state.dfps.common.domain.PersonMerge;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.domain.SvcAuthEventLink;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.securityauthoriztion.dto.ClientInfoServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.DayCareDetailsDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.DayCareFacilServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.KinshipGroupInfoDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.MedicaidServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.OldestVictimNameDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.SelectForwardPersonDto;
import us.tx.state.dfps.service.servicauthform.dao.ServiceAuthFormDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 1, 2018- 1:52:37 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class ServiceAuthFormDaoImpl implements ServiceAuthFormDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${ServiceAuthFormDaoImpl.oldestVicSql}")
	private String oldestVicSql;

	@Value("${ServiceAuthFormDaoImpl.nbrContractScorSql}")
	private String nbrContractScorSql;

	@Value("${ServiceAuthFormDaoImpl.dayCareDetailsSql}")
	private String dayCareDetailsSql;

	@Value("${ServiceAuthFormDaoImpl.clientInfoServiceAuthSql}")
	private String clientInfoServiceAuthSql;

	@Value("${ServiceAuthFormDaoImpl.kinshipSql}")
	private String kinshipSql;

	@Value("${ServiceAuthFormDaoImpl.prPersonIdSql}")
	private String prPersonIdSql;

	@Value("${ServiceAuthFormDaoImpl.dayCareFacilitySql}")
	private String dayCareFacilitySql;

	@Value("${ServiceAuthFormDaoImpl.getApprvlStatusSql}")
	private String getApprvlStatusSql;

	/**
	 * 
	 * Method Name: getOldestVicName Method Description: Retrieves Oldest Victim
	 * Name using ID STAGE as input from eventValueDto. Dam Name :CSEC78D
	 * 
	 * @param idStage
	 * @param relType
	 * @return OldestVictimNameDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public OldestVictimNameDto getOldestVicName(Long idStage, String relType) {
		// sql in servauthretrievesql file
		List<OldestVictimNameDto> oldestVictimNameDto = (List<OldestVictimNameDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(oldestVicSql).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("indStagePersSecAsgn", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("txtStagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.DATE)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
				.addScalar("nbrPhone", StandardBasicTypes.STRING).addScalar("nbrPersonAge", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE).addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("cdPersonCharacter", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonCounty", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonState", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("txtOccupation", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersonCity", StandardBasicTypes.STRING)
				.addScalar("addrPersonZip", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING).setParameter("cdStagePersRelInt", relType)
				.setParameter("idStage", idStage).setParameter("cdStagePersType", ServiceConstants.PRN_TYPE)
				.setResultTransformer(Transformers.aliasToBean(OldestVictimNameDto.class)).list();
		// Query return more than one record, so change the return type to list
		// and getting the first element from the result - Added this change for
		// warranty defect 11001
		if (!ObjectUtils.isEmpty(oldestVictimNameDto)) {
			return oldestVictimNameDto.get(0);
		}
		return new OldestVictimNameDto();
	}

	/**
	 * 
	 * Method Name: getNbrContractScor Method Description: Retrieves
	 * nbr_contract_scor from contract_period Dam Name : CMSC63D
	 * 
	 * @param idContract
	 * @param dtSvcAuthEff
	 * @return String
	 */
	@Override
	public String getNbrContractScor(Long idContract, Date dtSvcAuthEff) {
		String txScorContractNumber = (String) sessionFactory.getCurrentSession().createSQLQuery(nbrContractScorSql)
				.addScalar("txScorContractNumber", StandardBasicTypes.STRING).setParameter("idContract", idContract)
				.setParameter("dtDate", dtSvcAuthEff).uniqueResult();
		return txScorContractNumber;

	}

	/**
	 * 
	 * Method Name: getminIdSvcAuthEvent Method Description: Get the original
	 * progressed svc auth event Dam Name: Call CSEC0DD
	 * 
	 * @param idSvcAuth
	 * @return Long
	 */
	@Override
	public Long getMinIdSvcAuthEvent(Long idSvcAuth) {
		Long minIdSvcAuthEvent = (Long) sessionFactory.getCurrentSession().createCriteria(SvcAuthEventLink.class)
				.setProjection(Projections.min("idSvcAuthEvent"))
				.add(Restrictions.eq("serviceAuthorization.idSvcAuth", idSvcAuth)).uniqueResult();

		return minIdSvcAuthEvent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DayCareDetailsDto> getDayCareDetails(Long minIdSvcAuthEvent) {
		// sql in daycare sql file
		List<DayCareDetailsDto> dayCareDetailsDtoList = sessionFactory.getCurrentSession()
				.createSQLQuery(dayCareDetailsSql).addScalar("idSvcAuthEvent", StandardBasicTypes.LONG)
				.addScalar("idDaycareEvent", StandardBasicTypes.LONG)
				.addScalar("idDaycareRequest", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idDaycarePersonLink", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdPersonType", StandardBasicTypes.STRING)
				.addScalar("cdDetermService", StandardBasicTypes.STRING)
				.addScalar("cdRequestType", StandardBasicTypes.STRING)
				.addScalar("cdDaycareType", StandardBasicTypes.STRING).addScalar("dtBegin", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("txtComments", StandardBasicTypes.STRING)
				.addScalar("idFacility", StandardBasicTypes.LONG).addScalar("nmFclty", StandardBasicTypes.STRING)
				.addScalar("nbrTelephone", StandardBasicTypes.STRING)
				.addScalar("nbrTlphnExt", StandardBasicTypes.STRING).addScalar("addrLn1", StandardBasicTypes.STRING)
				.addScalar("addrLn2", StandardBasicTypes.STRING).addScalar("addrCity", StandardBasicTypes.STRING)
				.addScalar("cdAddrState", StandardBasicTypes.STRING).addScalar("addrZip1", StandardBasicTypes.STRING)
				.addScalar("addrZip2", StandardBasicTypes.STRING).addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
				.addScalar("indInvalid", StandardBasicTypes.STRING).addScalar("dtInvalid", StandardBasicTypes.DATE)
				.addScalar("indSun", StandardBasicTypes.STRING).addScalar("indMon", StandardBasicTypes.STRING)
				.addScalar("indTue", StandardBasicTypes.STRING).addScalar("indWed", StandardBasicTypes.STRING)
				.addScalar("indThu", StandardBasicTypes.STRING).addScalar("indFri", StandardBasicTypes.STRING)
				.addScalar("indSat", StandardBasicTypes.STRING).addScalar("indVarSch", StandardBasicTypes.STRING)
				.addScalar("cdVarSchMaxDays", StandardBasicTypes.STRING)
				.addScalar("cdSummerType", StandardBasicTypes.STRING)
				.addScalar("cdWeekendType", StandardBasicTypes.STRING)
				.addScalar("txtHoursNeeded", StandardBasicTypes.STRING)
				.addScalar("idSsccReferral", StandardBasicTypes.LONG)
				.addScalar("dtBeginProvider", StandardBasicTypes.DATE)
				.addScalar("dtEndProvider", StandardBasicTypes.DATE).setParameter("idSvcAuthEvent", minIdSvcAuthEvent)
				.setParameter("cdDetermService", ServiceConstants.NOT_QUALIFIED)
				.setParameter("indLocation", ServiceConstants.LOCATION)
				.setParameter("indSun", ServiceConstants.SUNDAY_STRING).setParameter("indMon", ServiceConstants.MONDAY)
				.setParameter("indTue", ServiceConstants.TUESDAY).setParameter("indWed", ServiceConstants.WEDNESDAY)
				.setParameter("indThu", ServiceConstants.THURSDAY)
				.setParameter("indFri", ServiceConstants.FRIDAY_STRING)
				.setParameter("indSat", ServiceConstants.SATURDAY_STRING)
				.setParameter("beforeafter", ServiceConstants.BEFORE_AFTER)
				.setParameter("cdDaycareTypeSchoolAge", ServiceConstants.SCHOOL_AGE)
				.setParameter("indYes", ServiceConstants.Y).setParameter("cdDaycareTypeHalf", ServiceConstants.HALF_DAY)
				.setResultTransformer(Transformers.aliasToBean(DayCareDetailsDto.class)).list();

		return dayCareDetailsDtoList;
	}

	/**
	 * 
	 * Method Name: getSelectForwardPerson Method Description:: Given the
	 * Primary Client person ID retrieved from the service auth table, bring
	 * back all the Person Merge Forward IDs. If no rows found, the person has
	 * not been merged. NO_ROWS_FOUND is okay DAM Name: CLSS71D
	 * 
	 * @param idPerson
	 * @return List<SelectForwardPersonDto>
	 */
	@Override
	public List<SelectForwardPersonDto> getSelectForwardPerson(Long idPerson) {

		@SuppressWarnings("unchecked")
		List<PersonMerge> persMergeList = sessionFactory.getCurrentSession()
				.createCriteria(PersonMerge.class)
				.add(Restrictions.eq("personByIdPersMergeClosed.idPerson", idPerson))
				.add(Restrictions.isNull("dtPersMergeSplit"))
				.list();
		
		List<SelectForwardPersonDto> dtoList = new ArrayList<>();
		
		if(!ObjectUtils.isEmpty(persMergeList)) {
			for(PersonMerge entity:persMergeList) {
				SelectForwardPersonDto dto = new SelectForwardPersonDto();
				dto.setIdPersonMergeForward(entity.getPersonByIdPersMergeForward().getIdPerson());
				dtoList.add(dto);
			}
		}

		return dtoList;
	}

	@Override
	public int getPersonInStageLink(Long idPersonMergeForward, Long idStage) {
		Long personCount = (Long) sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class)
				.add(Restrictions.eq("idStage", idStage)).add(Restrictions.eq("idPerson", idPersonMergeForward))
				.setProjection(Projections.rowCount()).uniqueResult();

		return personCount.intValue();
	}

	/**
	 * 
	 * Method Name: getClientInfoServiceAuth Method Description:
	 * 
	 * @param idPerson
	 * @param idStage
	 * @return ClientInfoServiceAuthDto
	 */
	@Override
	public ClientInfoServiceAuthDto getClientInfoServiceAuth(Long idPerson, Long idStage) {
		// ServAuthRetrieveSql file
		ClientInfoServiceAuthDto clientInfoServiceAuthDto = (ClientInfoServiceAuthDto) sessionFactory
				.getCurrentSession().createSQLQuery(clientInfoServiceAuthSql)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAge", StandardBasicTypes.STRING)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("txtOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("txtStagePersNotes", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.DATE)
				.addScalar("idStagePerson", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.setParameter("idPerson", idPerson).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(ClientInfoServiceAuthDto.class)).uniqueResult();

		if(!ObjectUtils.isEmpty(clientInfoServiceAuthDto)) {
			clientInfoServiceAuthDto.setIdPersonId(idPerson);
			clientInfoServiceAuthDto.setIdStage(idStage);
		}
		return clientInfoServiceAuthDto;
	}

	@Override
	public List<MedicaidServiceAuthDto> getMedicaidServiceAuth(Long idPerson) {

		@SuppressWarnings("unchecked")
		List<MedicaidServiceAuthDto> medicaidServiceAuthDtoList = sessionFactory.getCurrentSession()
				.createCriteria(PersonId.class)
				.setProjection(Projections.projectionList().add(Projections.property("idPersonId"), "idPersonId")
						.add(Projections.property("dtLastUpdate"), "tsLastUpdate")
						.add(Projections.property("person.idPerson"), "idPerson")
						.add(Projections.property("nbrPersonIdNumber"), "nbrPersonIdNumber")
						.add(Projections.property("cdPersonIdType"), "cdPersonIdType")
						.add(Projections.property("descPersonId"), "idDescPerson")
						.add(Projections.property("indPersonIdInvalid"), "indPersonIDInvalid")
						.add(Projections.property("dtPersonIdStart"), "dtPersonIDStart")
						.add(Projections.property("dtPersonIdEnd"), "dtPersonIDEnd"))
				.add(Restrictions.eq("person.idPerson", idPerson))
				.add(Restrictions.eq("indPersonIdInvalid", ServiceConstants.INDICATOR_NO))
				.add(Restrictions.eq("cdPersonIdType", ServiceConstants.HHSS_NUM))
				.setResultTransformer(Transformers.aliasToBean(MedicaidServiceAuthDto.class)).list();

		return medicaidServiceAuthDtoList;
	}

	@Override
	public List<KinshipGroupInfoDto> getKinshipDetails(Long idSvcAuth) {
		// present in servauthretrievesql.properties

		@SuppressWarnings("unchecked")
		List<KinshipGroupInfoDto> kinshipList = sessionFactory.getCurrentSession().createSQLQuery(kinshipSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("dtKinshipEnd", StandardBasicTypes.DATE)
				.addScalar("dtSvcAuthDtlBegin", StandardBasicTypes.DATE)
				.addScalar("dtSvcAuthDtlTerm", StandardBasicTypes.DATE)
				.addScalar("cdSvcAuthService", StandardBasicTypes.STRING).setParameter("idSvcAuth", idSvcAuth)
				.setResultTransformer(Transformers.aliasToBean(KinshipGroupInfoDto.class)).list();

		return kinshipList;
	}

	@Override
	public Long getPRPersonId(Long idEvent) {
		// present in servauthretrievesql.properties
		Long resultId = (Long) sessionFactory.getCurrentSession().createSQLQuery(prPersonIdSql)
				.addScalar("resultId", StandardBasicTypes.LONG).setParameter("idEvent", idEvent)
				.setParameter("cdRolePR", ServiceConstants.PRIMARY)
				.setParameter("cdRoleHP", ServiceConstants.HIST_PRIM_WORKER)
				.setParameter("maxDate", ServiceConstants.MAX_DATE_2).uniqueResult();

		return resultId;
	}

	@Override
	public List<DayCareFacilServiceAuthDto> getDayCareFacility(Long idDaycarePersonLink, Long idDaycareRequest) {
		@SuppressWarnings("unchecked")
		List<DayCareFacilServiceAuthDto> dayCareFacilList = sessionFactory.getCurrentSession()
				.createSQLQuery(dayCareFacilitySql).addScalar("idDaycarePersonLink", StandardBasicTypes.LONG)
				.addScalar("idFacility", StandardBasicTypes.LONG).addScalar("nmFclty", StandardBasicTypes.STRING)
				.addScalar("nbrTelephone", StandardBasicTypes.STRING)
				.addScalar("nbrTlphnExt", StandardBasicTypes.STRING).addScalar("addrLn1", StandardBasicTypes.STRING)
				.addScalar("addrLn2", StandardBasicTypes.STRING).addScalar("addrCity", StandardBasicTypes.STRING)
				.addScalar("cdAddrState", StandardBasicTypes.STRING).addScalar("addrZip1", StandardBasicTypes.STRING)
				.addScalar("addrZip2", StandardBasicTypes.STRING).addScalar("dtBeginProvider", StandardBasicTypes.DATE)
				.addScalar("dtEndProvider", StandardBasicTypes.DATE)
				.setParameter("idDaycarePersonLink", idDaycarePersonLink)
				.setParameter("idDaycareRequest", idDaycareRequest)
				.setParameter("indLocation", ServiceConstants.LOCATION)
				.setResultTransformer(Transformers.aliasToBean(DayCareFacilServiceAuthDto.class)).list();

		return dayCareFacilList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ApprovalFormDataDto getApprovalFormData(Long idEvent) {

		List<ApprovalFormDataDto> approvalFormDataDtoLst = new ArrayList<ApprovalFormDataDto>();
		approvalFormDataDtoLst = (List<ApprovalFormDataDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getApprvlStatusSql).addScalar("idApprovalEvent", StandardBasicTypes.LONG)
				.addScalar("dtApprovalLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idApproval", StandardBasicTypes.LONG)
				.addScalar("idApprovalPerson", StandardBasicTypes.LONG)
				.addScalar("approvalTopic", StandardBasicTypes.STRING)
				.addScalar("approvalDate", StandardBasicTypes.DATE).addScalar("idApprovers", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idTodo", StandardBasicTypes.LONG)
				.addScalar("cdApproversStatus", StandardBasicTypes.STRING)
				.addScalar("dtApproversDetermination", StandardBasicTypes.DATE)
				.addScalar("dtApproversRequested", StandardBasicTypes.DATE)
				.addScalar("indApproversHistorical", StandardBasicTypes.BOOLEAN)
				.addScalar("txtApproversCmnts", StandardBasicTypes.STRING)
				.addScalar("cdEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("txtEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("nmFirst", StandardBasicTypes.STRING).addScalar("nmMiddle", StandardBasicTypes.STRING)
				.addScalar("nmLast", StandardBasicTypes.STRING).addScalar("nmSuffix", StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ApprovalFormDataDto.class)).list();

		// Query return more than one record, so change the return type to list
		// and getting the first element from the result - Added this change for
		// warranty defect 12025
		if (!ObjectUtils.isEmpty(approvalFormDataDtoLst)) {
			return approvalFormDataDtoLst.get(ServiceConstants.Zero_INT);
		}
		return new ApprovalFormDataDto();

	}

}
