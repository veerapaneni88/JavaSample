package us.tx.state.dfps.service.hsegh.daoimpl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.dto.ChildPlanRecordDto;
import us.tx.state.dfps.common.dto.PersonOnHseghDto;
import us.tx.state.dfps.common.dto.ServicePlanDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.hsegh.dao.HseghDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:HseghDaoImpl
 * will implemented all operation defined in HseghDao Interface related HSEGH
 * module.. Feb 9, 2018- 2:02:51 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class HseghDaoImpl implements HseghDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${HseghDaoImpl.getConservatorshipById}")
	private String getConservatorshipByIdSql;

	@Value("${HseghDaoImpl.getRmvlReasonForCnsrvtrshpRemoval}")
	private String getRemovalReasonSql;

	@Value("${HseghDaoImpl.getOldestApprFP}")
	private String getOldestApprFPSql;

	@Value("${HseghDaoImpl.getAllPeopleOnHsegh}")
	private String getAllPeopleOnHseghSql;

	@Value("${HseghDaoImpl.getChildPlanRecords}")
	private String getChildPlanRecordsSql;

	@SuppressWarnings("unchecked")
	@Override
	public List<CnsrvtrshpRemovalDto> getConservatorshipById(Long idVictim) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getConservatorshipByIdSql)
				.addScalar("idRemovalEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idVictim", StandardBasicTypes.LONG).addScalar("dtRemoval", StandardBasicTypes.DATE)
				.addScalar("indRemovalNaCare", StandardBasicTypes.CHARACTER)
				.addScalar("indRemovalNaChild", StandardBasicTypes.CHARACTER)
				.addScalar("RemovalAgeMo", StandardBasicTypes.LONG).addScalar("RemovalAgeYr", StandardBasicTypes.LONG)
				.setParameter("idVictim", idVictim)
				.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemovalDto.class));

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CnsrvtrshpRemovalDto> getRmvlReasonForCnsrvtrshpRemoval(Long idVictim) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRemovalReasonSql)
				.addScalar("idRemovalEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idVictim", StandardBasicTypes.LONG).addScalar("dtRemoval", StandardBasicTypes.DATE)
				.addScalar("indRemovalNaCare", StandardBasicTypes.CHARACTER)
				.addScalar("indRemovalNaChild", StandardBasicTypes.CHARACTER)
				.addScalar("RemovalAgeMo", StandardBasicTypes.LONG).addScalar("RemovalAgeYr", StandardBasicTypes.LONG)
				.addScalar("cdRemovalReason", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate2", StandardBasicTypes.DATE).setParameter("idVictim", idVictim)
				.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemovalDto.class));

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ServicePlanDto> getOldestApprFP(Long idPerson) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getOldestApprFPSql)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idEventStage", StandardBasicTypes.LONG)
				.addScalar("cdEventType", StandardBasicTypes.STRING).addScalar("idEventPerson", StandardBasicTypes.LONG)
				.addScalar("cdTask", StandardBasicTypes.STRING).addScalar("txtEventDescr", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("idEventPersLink", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate2", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idSvcPlanEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate3", StandardBasicTypes.DATE)
				.addScalar("idFamAssmtEvent", StandardBasicTypes.LONG)
				.addScalar("dtSvcPlanComplt", StandardBasicTypes.DATE)
				.addScalar("dtSvcPlanPartcp", StandardBasicTypes.DATE)
				.addScalar("cdSvcPlanType", StandardBasicTypes.STRING)
				.addScalar("dtSvcPlanNextRevw", StandardBasicTypes.DATE)
				.addScalar("txtSvcPlanRsnInvlvmnt", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlnStrnthsRsrcs", StandardBasicTypes.STRING)
				.addScalar("indSvcPlanClientCmnt", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanPartcp", StandardBasicTypes.STRING)
				.addScalar("dtSvcPlanGivenClients", StandardBasicTypes.DATE)
				.addScalar("dtSvcPlanSgndParent1", StandardBasicTypes.DATE)
				.addScalar("dtSvcPlanSgndParent2", StandardBasicTypes.DATE)
				.addScalar("dtSvcPlanSgndSupv", StandardBasicTypes.DATE)
				.addScalar("dtSvcPlanSgndWorker", StandardBasicTypes.DATE).setParameter("idPerson", idPerson)
				.setParameter("eventStatus", ServiceConstants.APPROVAL)
				.setParameter("taskCode", ServiceConstants.TASK_CODE)
				.setResultTransformer(Transformers.aliasToBean(ServicePlanDto.class));
		return query.list();
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public List<PersonOnHseghDto> getAllPeopleOnHsegh(Long idStage) {

		Calendar cal = Calendar.getInstance();
		cal.set(cal.YEAR, ServiceConstants.ARC_MAX_YEAR);
		cal.set(cal.MONTH, cal.DECEMBER);
		cal.set(cal.DATE, ServiceConstants.ARC_MAX_DAY);
		cal.set(cal.HOUR_OF_DAY, ServiceConstants.Zero_INT);
		cal.set(cal.MINUTE, ServiceConstants.Zero_INT);
		cal.set(cal.SECOND, ServiceConstants.Zero_INT);
		cal.set(cal.MILLISECOND, ServiceConstants.Zero_INT);

		Date maxDate = new Date(cal.getTime().getTime());
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAllPeopleOnHseghSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("amtPersonAnnualIncome", StandardBasicTypes.LONG)
				.addScalar("cdPersonBirthCity", StandardBasicTypes.STRING)
				.addScalar("cdPersonBirthCountry", StandardBasicTypes.STRING)
				.addScalar("cdPersonBirthCounty", StandardBasicTypes.STRING)
				.addScalar("cdPersonBirthState", StandardBasicTypes.STRING)
				.addScalar("cdPersonCitizenship", StandardBasicTypes.STRING)
				.addScalar("cdPersonEyeColor", StandardBasicTypes.STRING)
				.addScalar("cdPersonFaHomeRole", StandardBasicTypes.STRING)
				.addScalar("cdPersonHairColor", StandardBasicTypes.STRING)
				.addScalar("cdPersonHighestEduc", StandardBasicTypes.STRING)
				.addScalar("indPersonNoUsBrn", StandardBasicTypes.CHARACTER)
				.addScalar("nmPersonLastEmployer", StandardBasicTypes.STRING)
				.addScalar("nmPersonMaidenName", StandardBasicTypes.STRING)
				.addScalar("qtyPersonHeightFeet", StandardBasicTypes.LONG)
				.addScalar("qtyPersonHeightInches", StandardBasicTypes.LONG)
				.addScalar("qtyPersonWeight", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate2", StandardBasicTypes.DATE).addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("personAge", StandardBasicTypes.LONG)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.CHARACTER)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("txtPersonOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING).addScalar("idName", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate3", StandardBasicTypes.DATE)
				.addScalar("indNameInvalid", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("indNamePrimary", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.addScalar("dtNameStartDate", StandardBasicTypes.DATE)
				.addScalar("dtNameEndDate", StandardBasicTypes.DATE)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate4", StandardBasicTypes.DATE).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("txtStagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.DATE)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("idPersonId", StandardBasicTypes.LONG).addScalar("dtLastUpdate5", StandardBasicTypes.DATE)
				.addScalar("personIdNumber", StandardBasicTypes.STRING)
				.addScalar("cdPersonIdType", StandardBasicTypes.STRING)
				.addScalar("descPersonId", StandardBasicTypes.STRING)
				.addScalar("indPersonIdInvalid", StandardBasicTypes.STRING)
				.addScalar("dtPersonIdStart", StandardBasicTypes.DATE)
				.addScalar("dtPersonIdEnd", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setParameter("cdPersonIdType", ServiceConstants.CD_PERSON_ID_TYPE).setParameter("maxDate", maxDate)
				.setResultTransformer(Transformers.aliasToBean(PersonOnHseghDto.class));

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChildPlanRecordDto> getChildPlanRecords(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getChildPlanRecordsSql)
				.addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdCspPlanPermGoal", StandardBasicTypes.STRING)
				.addScalar("cdCspPlanType", StandardBasicTypes.STRING)
				.addScalar("dtCspPermGoalTarget", StandardBasicTypes.DATE)
				.addScalar("dtCspNextReview", StandardBasicTypes.DATE)
				.addScalar("txtCspLengthOfStay", StandardBasicTypes.STRING)
				.addScalar("txtCspLosDiscrepancy", StandardBasicTypes.STRING)
				.addScalar("txtCspParticipComment", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(ChildPlanRecordDto.class));

		return query.list();
	}

}
