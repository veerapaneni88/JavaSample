package us.tx.state.dfps.service.personmergesplit.daoimpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.MergeSplitVldMsgDto;
import us.tx.state.dfps.service.person.dto.PersonMergeSplitDto;
import us.tx.state.dfps.service.person.dto.PersonPotentialDupDto;
import us.tx.state.dfps.service.personmergesplit.dao.PersonMergeSplitDao;
import us.tx.state.dfps.service.personmergesplit.dto.CaseValueDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeSplitValueDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeUpdateLogDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Impl
 * Level class for Person Merge Split> May 30, 2018- 11:56:55 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@SuppressWarnings("unchecked")
@Repository
public class PersonMergeSplitDaoImpl implements PersonMergeSplitDao {

	@Value("${PersonMergeSplitDaoImpl.getPRTForSplit}")
	private String getPRTForSplitSql;

	@Value("${PersonMergeSplitDaoImpl.getPRTConnectionForSplit}")
	private String getPRTConnectionForSplitSql;

	@Value("${PersonMergeSplitDaoImpl.getStagesForPersonMergeView}")
	private String getStagesForPersonMergeViewSql;

	@Value("${PersonMergeSplitDaoImpl.getPcspPrsnLinkForSplit}")
	private String getPcspPrsnLinkForSplitSql;

	@Value("${PersonMergeSplitDaoImpl.getPcspAsmntForSplit}")
	private String getPcspAsmntForSplitSql;

	@Value("${PersonMergeSplitDaoImpl.getPcspPlcmntForSplit}")
	private String getPcspPlcmntForSplitSql;

	@Value("${PersonMergeSplitDaoImpl.getPcspPlcmntForMerge}")
	private String getPcspPlcmntForMergeSql;

	@Value("${PersonMergeSplitDaoImpl.getLegacyChildForSplit}")
	private String getLegacyChildForSplitSql;

	@Value("${PersonMergeSplitDaoImpl.getLegacyCaregiverForSplit}")
	private String getLegacyCaregiverForSplitSql;

	@Value("${PersonMergeSplitDaoImpl.updateCaregiverSafetyPlcmtForSplit}")
	private String updateCaregiverSafetyPlcmtForSplitSql;

	@Value("${PersonMergeSplitDaoImpl.getAffectedStagesStaff}")
	private String getAffectedStagesStaffSql;

	@Value("${PersonMergeSplitDaoImpl.getSnapshotTableList}")
	private String getSnapshotTableListSql;

	@Value("${PersonMergeSplitDaoImpl.getActivePersonPotentialDupDetail}")
	private String getActivePersonPotentialDupDetailSql;

	@Value("${PersonMergeSplitDaoImpl.getPersonPotentialDupList}")
	private String getPersonPotentialDupListSql;

	@Value("${PersonMergeSplitDaoImpl.updatePersonPotentialDupInfo}")
	private String updatePersonPotentialDupInfoSql;

	@Value("${PersonMergeSplitDaoImpl.updatePcspPlcmntForSplit}")
	private String updatePcspPlcmntForSplit;

	@Value("${PersonMergeSplitDaoImpl.updateChildSafetyPlcmtForSplit}")
	private String updateChildSafetyPlcmtForSplit;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_ON_MED_CONSENTER}")
	private String updatePersonClosedWithForwardMedConsenter;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_FAMILY_ASSMT_FACTORS}")
	private String updatePersonClosedWithForwardFamilyFactors;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_HOME_REMOVAL}")
	private String updatePersonClosedWithForwardHomeRemoval;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_LEGAL_STATUS}")
	private String updatePersonClosedWithForwardLegaStatus;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_LEGAL_ACTION}")
	private String updatePersonClosedWithForwardLegalAction;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_GUARDIANSHIP}")
	private String updatePersonClosedWithForwardPersonGuardianship;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_CONTACT_PERSON_NARR}")
	private String updatePersonClosedWithForwardContactPersonNarr;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_CONTACT_GUIDE_NARR}")
	private String updatePersonClosedWithForwardContactGuideNarr;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_CHILD_PLAN_PARTICIP}")
	private String updatePersonOnEventPersonChildPlanParticip;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_PPT_PARTICIPANT}")
	private String updatePersonClosedWithForwardPptPersonParticipant;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_PLACEMENT}")
	private String updatePersonClosedWithForwardPersonPlacement;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_FAMILY_PLAN_CANDIDACY}")
	private String updatePersonClosedWithForwardFamilyPlanCandicacy;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PCA_ELIG_APP}")
	private String updatePersonClosedWithForwardPcaEligApp;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_ADPT_ELIG_APP}")
	private String updatePersonClosedWithForwardADPTEligApp;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_DAYCARE_REQUEST}")
	private String updatePersonClosedWithForwardDayCareRequest;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_FCE_PERSON}")
	private String updatePersonClosedWithForwardFcePerson;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_FCE_INCOME}")
	private String updatePersonClosedWithForwardDayFceIncome;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_ICPC_REQUEST_PERSON}")
	private String updatePersonClosedWithForwardIcpcRequestPerson;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_PERSON_ICPC_TRANSMITTAL_CHILD}")
	private String updatePersonClosedWithForwardIcpcTransmittalChild;

	@Value("${PersonMergeSplitDaoImpl.updatePersonOnEventPersonLink}")
	private String updatePersonOnEventPersonLinkSql;

	@Value("${PersonMergeSplitDaoImpl.updatePersonOnStagePersonLink}")
	private String updatePersonOnStagePersonLinkSql;

	@Value("${PersonMergeSplit.getForwardCaseInCaseMerge}")
	private String getForwardCaseInCaseMergeSql;

	@Value("${PersonMergeSplit.getStagesUpdatedInMerge}")
	private String getStagesUpdatedInMergeSql;

	@Value("${PersonMergeSplit.getPersonMergeHierarchyList}")
	private String getPersonMergeHierarchyListSql;

	@Value("${PersonMergeSplitDaoImpl.getMergeList}")
	private String getMergeListSql;

	@Value("${PersonMergeSplitDaoImpl.getPersonMergeUpdateLogList}")
	private String getPersonMergeUpdateLogListSql;

	@Value("${PersonMergeSplitDaoImpl.getPersonMergeSelectFieldListSql}")
	private String getPersonMergeSelectFieldListSql;

	@Value("${PersonMergeSplitDaoImpl.createSnapshot}")
	private String createSnapshotSql;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_FAMILY_PLAN_NEEDS}")
	private String updateFamilyPlanNeeds;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_FAMILY_PLAN_EVAL_NEEDS}")
	private String updateFamilyPlanEvalNeeds;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_FAMILY_PLAN_PART}")
	private String updateFamilyPlanPartcpnt;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_FAMILY_PLAN_EVAL_PART}")
	private String updateFamilyPlanEvalPartcpnt;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_CPS_FSNA_RSPNS}")
	private String updateCpsFsnaRspns;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_REUNFCTN_ASMNT_SECNDRY}")
	private String updateReunfctnAsmntSecndry;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_REUNFCTN_ASMNT_PRMRY}")
	private String updateReunfctnAsmntPrmry;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_REUNFCTN_ASMNT_HSHLD}")
	private String updateReunfctnAsmntHshld;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_REUNFCTN_ASMNT_CHLD}")
	private String updateReunfctnAsmntChld;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_RISK_REASMNT_HSHLD}")
	private String updateRiskReasmntHshld;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_RISK_REASMNT_PRMRY}")
	private String updateRiskReasmntPrmry;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_RISK_REASMNT_SECNDRY}")
	private String updateRiskReasmntSecndry;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_CPS_FSNA_SECNDRY}")
	private String updateCpsFsnaSecndry;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForward.SQL_UPDATE_CPS_FSNA_PRMRY}")
	private String updateCpsFsnaPrmry;

	@Value("${PersonMergeSplitDaoImpl.getSxVctmznIncdntForSplit}")
	private String getSxVctmznIncdntForSplit;

	@Value("${PersonMergeSplitDaoImpl.updateSxVctmznIncdntForSplit}")
	private String updateSxVctmznIncdntForSplit;

	@Value("${PersonMergeSplitDaoImpl.updateSxVctmznIncdntForSplit2}")
	private String updateSxVctmznIncdntForSplit2;

	@Value("${PersonMergeSplitDaoImpl.getSxMutualIncdntForSplit}")
	private String getSxMutualIncdntForSplit;

	@Value("${PersonMergeSplitDaoImpl.updateSxMutualIncdntForSplit}")
	private String updateSxMutualIncdntForSplit;

	@Value("${PersonMergeSplitDaoImpl.updateSxMutualIncdntForSplit2}")
	private String updateSxMutualIncdntForSplit2;

	@Value("${PersonMergeSplitDaoImpl.getBeforeMergesxvctmztn}")
	private String getBeforeMergesxvctmztn;

	@Value("${PersonMergeSplitDaoImpl.getBeforeMergeSxMutualIncdnt}")
	private String getBeforeMergeSxMutualIncdnt;

	@Value("${PersonMergeSplitDaoImpl.getClosedBeforeMergesxvctmztnIncdnt}")
	private String getClosedBeforeMergesxvctmztnIncdnt;

	@Value("${PersonMergeSplitDaoImpl.getClosedBeforeMergeSxMutualIncdnt}")
	private String getClosedBeforeMergeSxMutualIncdnt;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForwardForSafetyAssessment}")
	private String updatePersonClosedWithForwardForSafetyAssessment;

	@Value("${PersonMergeSplitDaoImpl.updtPrsnClsdWdFwdRiskAssessmentHshld}")
	private String updtPrsnClsdWdFwdRiskAssessmentHshld;

	@Value("${PersonMergeSplitDaoImpl.updtPrsnClsdWdFwdRiskAssessmentPrmryCrgvr}")
	private String updtPrsnClsdWdFwdRiskAssessmentPrmryCrgvr;

	@Value("${PersonMergeSplitDaoImpl.updtPrsnClsdWdFwdRiskAssessmentSndryCrgvr}")
	private String updtPrsnClsdWdFwdRiskAssessmentSndryCrgvr;

	@Value("${PersonMergeSplitDaoImpl.updatePersonClosedWithForwardForFBSSReferral}")
	private String updatePersonClosedWithForwardForFBSSReferral;

	@Value("${PersonMergeSplitDaoImpl.updateVictimClosedWithForwardForLtrAllegationLink}")
	private String updateVictimClosedWithForwardForLtrAllegationLink;

	@Value("${PersonMergeSplitDaoImpl.updateAllegPerpertratorClosedWithForwardForLtrAllegationLink}")
	private String updateAllegPerpertratorClosedWithForwardForLtrAllegationLink;
	
	@Value("${PersonMergeSplitDaoImpl.updateIfClosedPersonAggressor}")
	private String updateIfClosedPersonAggressor;
	
	@Value("${PersonMergeSplitDaoImpl.updatePlacementTAWithFwdPerson.SQL_UPDATE_PLACEMENT_TA}")
	private String updatePlacementTAWithFwdPerson;

	@Value("${PersonMergeSplitDaoImpl.updateIfClosedPersonMutual}")
	private String updateIfClosedPersonMutual;
	
	@Value("${PersonMergeSplitDaoImpl.updateIfClosedPersonVictim}")
	private String updateIfClosedPersonVictim;
	
	@Value("${PersonMergeSplitDaoImpl.splitPlacementTAWithFwdPerson.SQL_SPLIT_PLACEMENT_TA}")
	private String splitPlacementTAWithFwdPerson;

	@Value("${PersonMergeSplitDaoImpl.updateChildRtbExceptnWithFwdPerson.SQL_CHILD_RTB_EXCEPTN}")
	String updateChildRtbExceptnWithFwdPerson;

	@Value("${PersonMergeSplitDaoImpl.getRtbExceptionsForSplit}")
	private String getRtbExceptionsForSplit;

	@Value("${PersonMergeSplitDaoImpl.getClosedBeforeMergeRtbExceptions}")
	private String getClosedBeforeMergeRtbExceptions;

	@Value("${PersonMergeSplitDaoImpl.updateRtbExceptionsForSplit}")
	private String updateRtbExceptionsForSplit;


	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	/**
	 *
	 * Method Name: getPRTForSplit Method Description: get PRT for split
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return List<Long>
	 */
	@Override
	public List<Long> getPRTForSplit(Long idPersMerge, Long idFwdPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getPRTForSplitSql)
				.addScalar("idPrtPersonLink", StandardBasicTypes.LONG)
				.addScalar("idPrtActionPlan", StandardBasicTypes.LONG).setParameter("idPersonMerge", idPersMerge)
				.setParameter("idPerson", idFwdPerson).setParameter("idPersonMerge", idPersMerge).list();
	}

	/**
	 *
	 * Method Name: updatePersonOnPrtForSplit Method Description: update person
	 * PRT for split
	 *
	 * @param idClosedPerson
	 * @param idPrtPersLink
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public void updatePersonOnPrtForSplit(Long idClosedPerson, Long idPrtPersLink, Long idFwdPerson) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrtPersonLink.class);
		criteria.add(Restrictions.eq("idPrtPersonLink", idPrtPersLink));
		criteria.add(Restrictions.eq("person.idPerson", idFwdPerson));

		PrtPersonLink prtPersonLink = (PrtPersonLink) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(prtPersonLink)) {
			prtPersonLink.getPerson().setIdPerson(idClosedPerson);
			sessionFactory.getCurrentSession().saveOrUpdate(prtPersonLink);
		}
	}

	/**
	 *
	 * Method Name: getPRTConnectionForSplit Method Description: get PRT
	 * Connection for split
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public List<Long> getPRTConnectionForSplit(Long idPersMerge, Long idFwdPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getPRTConnectionForSplitSql)
				.addScalar("idPrtConnection", StandardBasicTypes.LONG).setParameter("idPersonMerge", idPersMerge)
				.setParameter("idPerson", idFwdPerson).setParameter("idPersonMerge", idPersMerge).list();
	}

	/**
	 *
	 * Method Name: updatePRTConnectionOnSplit Method Description: update PRT
	 * Connection on Split
	 *
	 * @param idClosedPerson
	 * @param idPrtConn
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public void updatePRTConnectionOnSplit(Long idClosedPerson, Long idPrtConn, Long idFwdPerson) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrtConnection.class);
		criteria.add(Restrictions.eq("idPrtConnection", idPrtConn));
		criteria.add(Restrictions.eq("person.idPerson", idFwdPerson));

		PrtConnection prtConnection = (PrtConnection) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(prtConnection)) {
			prtConnection.getPerson().setIdPerson((long) idClosedPerson);
			sessionFactory.getCurrentSession().saveOrUpdate(prtConnection);
		}
	}

	/**
	 * Method Name: getPersonMergeInfo Method Description: This method is used
	 * to update Hand Written Data.
	 *
	 * @param idPersonMerge
	 * @return PersonMergeSplitValueDto
	 * @throws DataNotFoundException
	 */
	@Override
	public PersonMergeSplitValueDto getPersonMergeInfo(Long idPersonMerge) {

		PersonMerge personMerge = (PersonMerge) sessionFactory.getCurrentSession().get(PersonMerge.class,
				idPersonMerge);

		PersonMergeSplitValueDto persMergeSplitValueDto = new PersonMergeSplitValueDto();

		if (!TypeConvUtil.isNullOrEmpty(personMerge)) {
			if (!TypeConvUtil.isNullOrEmpty(personMerge.getPersonByIdPersMergeForward())) {
				persMergeSplitValueDto
						.setNmForwardPerson(personMerge.getPersonByIdPersMergeForward().getNmPersonFull());
			}
			if (!TypeConvUtil.isNullOrEmpty(personMerge.getPersonByIdPersMergeClosed())) {
				persMergeSplitValueDto.setNmClosedPerson(personMerge.getPersonByIdPersMergeClosed().getNmPersonFull());
			}
			if (!TypeConvUtil.isNullOrEmpty(personMerge.getPersonByIdPersMergeSplitWrkr())) {
				persMergeSplitValueDto
						.setNmPersonMergeSplitWorker(personMerge.getPersonByIdPersMergeSplitWrkr().getNmPersonFull());
			}
			if (!TypeConvUtil.isNullOrEmpty(personMerge.getPersonByIdPersMergeWrkr())) {
				persMergeSplitValueDto
						.setNmPersonMergeWorker(personMerge.getPersonByIdPersMergeWrkr().getNmPersonFull());
			}

			persMergeSplitValueDto.setIdPersonMerge(personMerge.getIdPersonMerge());
			if (!TypeConvUtil.isNullOrEmpty(personMerge.getPersonByIdPersMergeForward())) {
				persMergeSplitValueDto.setIdForwardPerson(personMerge.getPersonByIdPersMergeForward().getIdPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(personMerge.getPersonByIdPersMergeClosed())) {
				persMergeSplitValueDto.setIdClosedPerson(personMerge.getPersonByIdPersMergeClosed().getIdPerson());
			}

			persMergeSplitValueDto.setDtLastUpdate(personMerge.getDtLastUpdate());

			if (!TypeConvUtil.isNullOrEmpty(personMerge.getPersonByIdPersMergeWrkr())
					&& !TypeConvUtil.isNullOrEmpty(personMerge.getPersonByIdPersMergeWrkr().getIdPerson()))
				persMergeSplitValueDto.setIdPersonMergeWorker(personMerge.getPersonByIdPersMergeWrkr().getIdPerson());

			if (!TypeConvUtil.isNullOrEmpty(personMerge.getPersonByIdPersMergeSplitWrkr())
					&& !TypeConvUtil.isNullOrEmpty(personMerge.getPersonByIdPersMergeSplitWrkr().getIdPerson()))
				persMergeSplitValueDto
						.setIdPersonMergeSplitWorker(personMerge.getPersonByIdPersMergeSplitWrkr().getIdPerson());

			if (!TypeConvUtil.isNullOrEmpty(personMerge.getIndPersMergeInvalid()))
				persMergeSplitValueDto.setIndPersonMergeInvalid(personMerge.getIndPersMergeInvalid().toString());

			if (!TypeConvUtil.isNullOrEmpty(personMerge.getDtPersMergeSplit()))
				persMergeSplitValueDto.setDtPersonMergeSplit(personMerge.getDtPersMergeSplit());

			if (!TypeConvUtil.isNullOrEmpty(personMerge.getDtPersMerge()))
				persMergeSplitValueDto.setDtPersonMerge(personMerge.getDtPersMerge());

			if (!TypeConvUtil.isNullOrEmpty(personMerge.getIdMergeGroup()))
				persMergeSplitValueDto.setIdMergeGroup(personMerge.getIdMergeGroup());

			if (!TypeConvUtil.isNullOrEmpty(personMerge.getIndDirectMerge()))
				persMergeSplitValueDto.setIndDirectMerge(personMerge.getIndDirectMerge());

			if (!TypeConvUtil.isNullOrEmpty(personMerge.getIdGroupLink()))
				persMergeSplitValueDto.setIdGroupLink(personMerge.getIdGroupLink());

		}
		return persMergeSplitValueDto;
	}

	/**
	 *
	 * Method Name: getPersonMergeListForForward Method Description:Fetches the
	 * Person Merge records where person exist as forward
	 *
	 * @param idForwardPerson
	 * @param considerInvalidAlso
	 * @return
	 */
	@Override
	public List<PersonMerge> getPersonMergeListForForward(long idForwardPerson, boolean considerInvalidAlso) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonMerge.class)
				.add(Restrictions.eq("personByIdPersMergeForward.idPerson", idForwardPerson))
				.add(Restrictions.eqOrIsNull("dtPersMergeSplit", null));
		if (!considerInvalidAlso)
			criteria.add(Restrictions.eq("indPersMergeInvalid", ServiceConstants.N_CHAR));
		return criteria.list();

	}

	/**
	 *
	 * Method Name: updatePersonMergeByPersonMergeDto Method Description: Update
	 * Person Merge by personMergeSplitValueDto
	 *
	 * @param persMergeSplitDto
	 * @return
	 */
	@Override
	public void updatePersonMergeByPersonMergeDto(PersonMergeSplitValueDto persMergeSplitDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonMerge.class);
		criteria.add(Restrictions.eq("idPersonMerge", persMergeSplitDto.getIdPersonMerge()));
		List<PersonMerge> personMergeList = criteria.list();
		Person personForward = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", (long) persMergeSplitDto.getIdForwardPerson())).uniqueResult();
		Set<PersonMerge> personMergesForIdPersMergeSplitWrkr = new HashSet<PersonMerge>();
		for (PersonMerge persMerge : personMergeList) {
			if (persMerge.getDtLastUpdate().getTime() <= persMergeSplitDto.getDtLastUpdate().getTime()) {
				persMerge.setPersonByIdPersMergeForward(personForward);
				persMerge.setDtPersMerge(persMergeSplitDto.getDtPersonMerge());
				Person personClosed = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
						.add(Restrictions.eq("idPerson", (long) persMergeSplitDto.getIdClosedPerson())).uniqueResult();
				persMerge.setPersonByIdPersMergeClosed(personClosed);
				Person personByIdPersMergeWorker = (Person) sessionFactory.getCurrentSession()
						.createCriteria(Person.class)
						.add(Restrictions.eq("idPerson", (long) persMergeSplitDto.getIdPersonMergeWorker()))
						.uniqueResult();
				persMerge.setPersonByIdPersMergeWrkr(personByIdPersMergeWorker);
				Person personByIdSplitWorker = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
						.add(Restrictions.eq("idPerson", (long) persMergeSplitDto.getIdPersonMergeSplitWorker()))
						.uniqueResult();
				persMerge.setPersonByIdPersMergeSplitWrkr(personByIdSplitWorker);
				Character indPersMergeInvalid = persMergeSplitDto.getIndPersonMergeInvalid().trim().charAt(0);
				persMerge.setIndPersMergeInvalid(indPersMergeInvalid);
				persMerge.setDtPersMergeSplit(persMergeSplitDto.getDtPersonMergeSplit());
				if (!ObjectUtils.isEmpty(persMergeSplitDto.getIdMergeGroup()))
					persMerge.setIdMergeGroup(persMergeSplitDto.getIdMergeGroup());
				persMerge.setIndDirectMerge(persMergeSplitDto.getIndDirectMerge());
				if (!ObjectUtils.isEmpty(persMergeSplitDto.getIdGroupLink()))
					persMerge.setIdGroupLink(persMergeSplitDto.getIdGroupLink());
				personMergesForIdPersMergeSplitWrkr.add(persMerge);
			}
			personForward.setPersonMergesForIdPersMergeSplitWrkr(personMergesForIdPersMergeSplitWrkr);
		}
		if (!ObjectUtils.isEmpty(personForward)) {
			sessionFactory.getCurrentSession().saveOrUpdate(personForward);
		}
	}

	/**
	 *
	 * Method Name: updatePersonMerge Method Description: update Person Merge
	 *
	 * @param personMerge
	 * @return
	 */
	@Override
	public void updatePersonMerge(PersonMerge personMerge) {
		sessionFactory.getCurrentSession().saveOrUpdate(personMerge);
	}

	/**
	 *
	 * Method Name: getStagesForPersonMergeView Method Description: get stages
	 * for Person Merge View
	 *
	 * @param idPersonMerge
	 * @return
	 */
	@Override
	public List<StagePersonValueDto> getStagesForPersonMergeView(long idPersonMerge) {
		return sessionFactory.getCurrentSession().createSQLQuery(getStagesForPersonMergeViewSql)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("nmStage", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtStageStart", StandardBasicTypes.DATE)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING).setParameter("idPersonInput", idPersonMerge)
				.setResultTransformer(Transformers.aliasToBean(StagePersonValueDto.class)).list();
	}

	/**
	 *
	 * Method Name: deleteNonEmpPersonCategories Method Description: delete non
	 * employee person categories
	 *
	 * @param idPersonMerge
	 * @return
	 */
	@Override
	public void deleteNonEmpPersonCategories(long idPersonMerge) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonCategory.class);
		criteria.add(Restrictions.eq("idPerson", idPersonMerge));
		criteria.add(Restrictions.ne("cdPersonCategory", "FEM"));
		criteria.add(Restrictions.ne("cdPersonCategory", "EMP"));
		List<PersonCategory> personCategory = criteria.list();
		personCategory.stream().forEach(person -> sessionFactory.getCurrentSession().delete(person));
	}

	/**
	 *
	 * Method Name: savePersonCategory Method Description: save person category
	 *
	 * @param idPersonMerge
	 * @param cpsnDtctString
	 * @return
	 */
	@Override
	public void savePersonCategory(long idPersonMerge, String cpsnDtctString) {
		if (idPersonMerge > 0 && !ObjectUtils.isEmpty(cpsnDtctString)) {
			PersonCategory personCategory = new PersonCategory();

			personCategory.setIdPerson(idPersonMerge);
			personCategory.setCdPersonCategory(cpsnDtctString);
			personCategory.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().save(personCategory);
		}
	}

	/**
	 *
	 * Method Name: getPcspPrsnLinkForSplit Method Description: get PCSP Person
	 * Link For split
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public List<Integer> getPcspPrsnLinkForSplit(long idPersMerge, long idFwdPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getPcspPrsnLinkForSplitSql)
				.addScalar("idPcspPrsnLink", StandardBasicTypes.LONG).setParameter("idPersonMerge", idPersMerge)
				.setParameter("idPerson", idFwdPerson).setParameter("idReferenceData", idPersMerge).list();
	}

	/**
	 * Method Name: getPcspPrsnLink Method Description: gets the PCSP person link by person
	 * @param idPerson
	 * @return
	 */
	public List<PcspPrsnLink> getPcspPrsnLink(long idPerson) {
		return sessionFactory.getCurrentSession().createCriteria(PcspPrsnLink.class)
				.add(Restrictions.eq("person.idPerson", idPerson)).list();
	}

	/**
	 *
	 * Method Name: updatePcspPrsnLinkForSplit Method Description: update PCSP
	 * Person Link For Split
	 *
	 * @param idClosedPerson
	 * @param idPcspPersLink
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public void updatePcspPrsnLink(long idClosedPerson, long idPcspPersLink, long idFwdPerson,boolean isMerge) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PcspPrsnLink.class);
		criteria.add(Restrictions.eq("idPcspPrsnLink", idPcspPersLink));
		if(isMerge){
			criteria.add(Restrictions.eq("person.idPerson", idClosedPerson));
		} else{
			criteria.add(Restrictions.eq("person.idPerson", idFwdPerson));
		}

		PcspPrsnLink pcspPrsnLink = (PcspPrsnLink) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(pcspPrsnLink)) {
			if(isMerge){
				Person forwardPerson = (Person) sessionFactory.getCurrentSession().get(Person.class, idFwdPerson);
				pcspPrsnLink.setPerson(forwardPerson);
			} else{
				Person closedPerson = (Person) sessionFactory.getCurrentSession().get(Person.class, idClosedPerson);
				pcspPrsnLink.setPerson(closedPerson);
			}
			sessionFactory.getCurrentSession().saveOrUpdate(pcspPrsnLink);
		}
	}

	/**
	 *
	 * Method Name: getPcspAsmntForSplit Method Description: Get PCSP Asmnt for
	 * Split
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public List<Integer> getPcspAsmntForSplit(long idPersMerge, long idFwdPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getPcspAsmntForSplitSql)
				.addScalar("idPcspAsmnt", StandardBasicTypes.LONG).setParameter("idPersonMerge", idPersMerge)
				.setParameter("idPrsnCrgvr", idFwdPerson).setParameter("idReferenceData", idPersMerge).list();
	}

	/**
	 * Method Name: getPcspAsmntByCaregiver Method Description: gets PCSP assessments for the caregiver
	 * @param idPerson
	 * @return
	 */
	public List<PcspAsmnt> getPcspAsmntByCaregiver(long idPerson){
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PcspAsmnt.class)
				.add(Restrictions.eq("person.idPerson", idPerson));
		return criteria.list();
	}

	/**
	 *
	 * Method Name: updatePcspAsmntForSplit Method Description: Update PCSP
	 * Asmnt for split
	 *
	 * @param idClosedPerson
	 * @param idPcspAssessment
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public void updatePcspAsmntForSplit(long idClosedPerson, long idPcspPlacement, long idFwdPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updatePcspPlcmntForSplit);
		query.setParameter("closedPersonId", idClosedPerson);
		query.setParameter("idPcspPlacement", idPcspPlacement);
		query.setParameter("forwardPersonId", idFwdPerson);
		query.executeUpdate();
	}

	/**
	 *
	 * Method Name: getPcspPlcmntForSplit Method Description: Get PCSP Plcmnt
	 * For Split
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public List<Integer> getPcspPlcmntForSplit(long idPersMerge, long idFwdPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getPcspPlcmntForSplitSql)
				.addScalar("idPcspPlcmnt", StandardBasicTypes.LONG).setParameter("idPersonMerge", idPersMerge)
				.setParameter("idPerson", idFwdPerson).setParameter("idReferenceData", idPersMerge).list();
	}

	/**
	 * Method Name:getPcspPlcmntForMerge Method Description: gets PCSP Placements by person id
	 * @param idPerson
	 * @return
	 */
	public List<Integer> getPcspPlcmntForMerge(long idPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getPcspPlcmntForMergeSql)
				.addScalar("idPcspPlcmnt", StandardBasicTypes.LONG)
				.setParameter("idPerson", idPerson).list();
	}

	/**
	 *
	 * Method Name: updatePcspPlcmntForSplit Method Description: Update PCSP
	 * Placement for split
	 *
	 * @param idClosedPerson
	 * @param idPcspPlacement
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public void updatePcspPlcmntForSplit(long idClosedPerson, long idPcspPlacement, long idFwdPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updatePcspPlcmntForSplit);
		query.setParameter("closedPersonId", idClosedPerson);
		query.setParameter("idPcspPlacement", idPcspPlacement);
		query.setParameter("forwardPersonId", idFwdPerson);
		query.executeUpdate();
	}

	/**
	 * Updates the PCSP placements for merge
	 * @param idForwardPerson
	 * @param idPcspPlacement
	 * @param idClosedPerson
	 */
	public void updatePcspPlcmntForMerge(long idForwardPerson, long idPcspPlacement, long idClosedPerson){
		PcspPlcmnt pcspPlcmnt = (PcspPlcmnt) sessionFactory.getCurrentSession().createCriteria(PcspPlcmnt.class)
				.add(Restrictions.eq("idPcspPlcmnt", idPcspPlacement))
				.add(Restrictions.eq("person.idPerson", idClosedPerson))
				.uniqueResult();
		Person forwardPerson = (Person) sessionFactory.getCurrentSession().get(Person.class, idForwardPerson);
		pcspPlcmnt.setPerson(forwardPerson);
		sessionFactory.getCurrentSession().saveOrUpdate(pcspPlcmnt);
	}

	@Override
	public List<Long> getLegacyChildForSplit(long idPersMerge, long idFwdPerson) {

		return sessionFactory.getCurrentSession().createSQLQuery(getLegacyChildForSplitSql)
				.addScalar("idChildSafetyPlcmt", StandardBasicTypes.LONG).setParameter("idPersonMerge", idPersMerge)
				.setParameter("idPerson", idFwdPerson).setParameter("idReferenceData", idPersMerge).list();
	}

	/**
	 *
	 * Method Name: updateChildSafetyPlcmtForSplit Method Description: Update
	 * Chlid's safety placement for split
	 *
	 * @param idClosedPerson
	 * @param idLegacyPCSP
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public void updateChildSafetyPlcmtForSplit(long idClosedPerson, long idLegacyPCSP, long idFwdPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateChildSafetyPlcmtForSplit);
		query.setParameter("closedPersonId", idClosedPerson);
		query.setParameter("idLegacyPCSP", idLegacyPCSP);
		query.setParameter("forwardPersonId", idFwdPerson);
		query.executeUpdate();
	}

	/**
	 *
	 * Method Name: getLegacyCaregiverForSplit Method Description: get legacy
	 * caregiver for split
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public List<Long> getLegacyCaregiverForSplit(long idPersMerge, long idFwdPerson) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getLegacyCaregiverForSplitSql)
				.addScalar("idChildSafetyPlcmt", StandardBasicTypes.LONG);

		query.setParameter("idPersonMerge", idPersMerge);
		query.setParameter("idReferenceData", idPersMerge);
		query.setParameter("idCaregvrPerson", idFwdPerson);

		return query.list();
	}

	/**
	 *
	 * Method Name: updateCaregiverSafetyPlcmtForSplit Method Description:
	 * update caregiver safety placement for split
	 *
	 * @param idClosedPerson
	 * @param idPcspCaregiver
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public void updateCaregiverSafetyPlcmtForSplit(long idClosedPerson, long idPcspCaregiver, long idFwdPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateCaregiverSafetyPlcmtForSplitSql);
		query.setParameter("closedPersonId", idClosedPerson);
		query.setParameter("safetyPltChildId", idPcspCaregiver);
		query.setParameter("forwardPersonId", idFwdPerson);
		query.executeUpdate();
	}

	/**
	 * Updates the caregiver on PCSP assessment
	 * @param idForwardPerson
	 * @param idPcspAsmnt
	 * @param idClosedPerson
	 */
	public void updateCaregiverOnPCSPAsmnt(long idForwardPerson, long idPcspAsmnt,long idClosedPerson){
		PcspAsmnt pcspAsmnt = (PcspAsmnt) sessionFactory.getCurrentSession().createCriteria(PcspAsmnt.class)
				.add(Restrictions.eq("idPcspAsmnt", idPcspAsmnt))
				.add(Restrictions.eq("person.idPerson", idClosedPerson))
				.uniqueResult();
		Person forwardPerson = (Person) sessionFactory.getCurrentSession().get(Person.class, idForwardPerson);
		pcspAsmnt.setPerson(forwardPerson);
		sessionFactory.getCurrentSession().saveOrUpdate(pcspAsmnt);
	}

	/**
	 *
	 * Method Name: savePersonMerge Method Description: save person merge
	 * details
	 *
	 * @param personMergeSplitDB
	 * @return
	 */
	@Override
	public long savePersonMerge(PersonMergeSplitDto personMergeSplitDB) {
		PersonMerge personMerge = new PersonMerge();

		Person personIdMerFrd = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", personMergeSplitDB.getIdForwardPerson())).uniqueResult();
		Person personIdMerCld = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", personMergeSplitDB.getIdClosedPerson())).uniqueResult();
		Person personIdMerWrk = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", personMergeSplitDB.getIdPersonMergeWorker())).uniqueResult();
		personMerge.setPersonByIdPersMergeForward(personIdMerFrd);
		personMerge.setPersonByIdPersMergeClosed(personIdMerCld);
		personMerge.setPersonByIdPersMergeWrkr(personIdMerWrk);

		if (!ObjectUtils.isEmpty(personMergeSplitDB.getIdPersonMergeSplitWorker())
				&& personMergeSplitDB.getIdPersonMergeSplitWorker() > 0) {

			Person personIdSplitWrk = new Person();
			personIdSplitWrk.setIdPerson(personMergeSplitDB.getIdPersonMergeSplitWorker());

			personMerge.setPersonByIdPersMergeSplitWrkr(personIdSplitWrk);

		}

		personMerge.setDtPersMerge(personMergeSplitDB.getDtPersonMerge());

		if (!ObjectUtils.isEmpty(personMergeSplitDB.getIndPersonMergeInvalid()))
			personMerge.setIndPersMergeInvalid(personMergeSplitDB.getIndPersonMergeInvalid().charAt(0));

		if (!ObjectUtils.isEmpty(personMergeSplitDB.getDtPersonMergeSplit())) {
			personMerge.setDtPersMergeSplit(personMergeSplitDB.getDtPersonMergeSplit());
		}
		if (!ObjectUtils.isEmpty(personMergeSplitDB.getIdMergeGroup()) && personMergeSplitDB.getIdMergeGroup() > 0)
			personMerge.setIdMergeGroup(personMergeSplitDB.getIdMergeGroup());

		if (!ObjectUtils.isEmpty(personMergeSplitDB.getIndDirectMerge()))
			personMerge.setIndDirectMerge(personMergeSplitDB.getIndDirectMerge());

		if (!ObjectUtils.isEmpty(personMergeSplitDB.getIdGroupLink()) && personMergeSplitDB.getIdGroupLink() > 0)
			personMerge.setIdGroupLink(personMergeSplitDB.getIdGroupLink());
		personMerge.setDtLastUpdate(new Date());
		Long result = (Long) sessionFactory.getCurrentSession().save(personMerge);
		if (!ObjectUtils.isEmpty(result) && (ObjectUtils.isEmpty(personMergeSplitDB.getIdMergeGroup())
				|| ServiceConstants.ZERO.equals(personMergeSplitDB.getIdMergeGroup()))) {
			personMerge.setIdMergeGroup(result);
			sessionFactory.getCurrentSession().update(personMerge);
		}

		if (result != null)
			return result;

		return 0;
	}

	/**
	 *
	 * Method Name: getSnapshotTableList Method Description: Get Snapshot Table
	 * list details
	 *
	 * @param cacnType
	 * @return
	 */
	@Override
	public ArrayList<SnapshotTblList> getSnapshotTableList(String cacnType) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSnapshotTableListSql)
				.addScalar("idSnapshotTblList", StandardBasicTypes.LONG)
				.addScalar("txtSnapshotTableName", StandardBasicTypes.STRING)
				.addScalar("txtSourceTable", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(SnapshotTblList.class));

		query.setParameter("cdActionType", cacnType);
		return (ArrayList<SnapshotTblList>) query.list();
	}

	/**
	 *
	 * Method Name: savePersonMergeSnapshot Method Description: save person
	 * merge to snapshot table
	 *
	 * @param snapshotHeader
	 * @return
	 */
	@Override
	public void savePersonMergeSnapshot(SnapshotHeader snapshotHeader) {
		sessionFactory.getCurrentSession().save(snapshotHeader);
	}

	/**
	 *
	 * Method Name: savePersonMergeSnapshotDtl Method Description: save person
	 * merge snapshot dtl
	 *
	 * @param snapshotDtl
	 * @return
	 */
	@Override
	public void savePersonMergeSnapshotDtl(SnapshotDtl snapshotDtl) {
		sessionFactory.getCurrentSession().save(snapshotDtl);
	}

	/**
	 *
	 * Method Name: getAffectedStagesStaff Method Description: Get affected
	 * stage staff details
	 *
	 * @param idForwardPerson
	 * @param idClosedPerson
	 * @param idMergePerson
	 * @return
	 */
	@Override
	public ArrayList<StagePersonLink> getAffectedStagesStaff(long idFwdPerson, long idClosedPerson,
			long idMergePerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAffectedStagesStaffSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLink.class));

		query.setParameter("idFwdPerson", idFwdPerson);
		query.setParameter("idClosedPerson", idClosedPerson);
		query.setParameter("idMergePerson", idMergePerson);

		return (ArrayList<StagePersonLink>) query.list();
	}

	/**
	 *
	 * Method Name: savePersonMergeSplitValidationLog Method Description: save
	 * person merge split validation log
	 *
	 * @param personMergeSplitDB
	 * @param msgNumber
	 * @param errorMessage
	 * @param cerrType
	 * @param cactnType
	 * @param personMergeForwardStep
	 * @return
	 */
	@Override
	public void savePersonMergeSplitValidationLog(PersonMergeSplitDto personMergeSplitDB, int msgNumber,
			String errorMessage, String cerrType, String cactnType, int personMergeForwardStep) {
		PersonMergeValidationLog personMergeValidationLog = new PersonMergeValidationLog();

		personMergeValidationLog.setIdPersonForward(personMergeSplitDB.getIdForwardPerson());
		personMergeValidationLog.setIdPersonClosed(personMergeSplitDB.getIdClosedPerson());
		personMergeValidationLog.setIdPersonMerge(personMergeSplitDB.getIdPersonMerge());
		personMergeValidationLog.setCdErrorType(cerrType);
		personMergeValidationLog.setNbrMessage(msgNumber);
		personMergeValidationLog.setCdActionType(cactnType);
		personMergeValidationLog.setTxtMessage(errorMessage);
		personMergeValidationLog.setNbrStep((byte) personMergeForwardStep);
		personMergeValidationLog.setIdCreatedPerson(personMergeSplitDB.getIdPersonMergeWorker());
		personMergeValidationLog.setIdLastUpdatePerson(personMergeSplitDB.getIdPersonMergeWorker());
		personMergeValidationLog.setDtCreated(new Date());
		personMergeValidationLog.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(personMergeValidationLog);
	}

	/**
	 *
	 * Method Name: savePersonMergeSelectField Method Description:his method
	 * insert a row into PERSON_MERGE_SELECT_FIELD. This table contains the
	 * fields and their source (Forward/Closed) selected during Select Forward
	 * Person
	 *
	 * @param personMergeSelectField
	 * @return
	 */
	@Override
	public void savePersonMergeSelectField(PersonMergeSelectField personMergeSelectField) {
		// Here i need a clarification regarding passing parameter. Here i have
		// to get a dto instead i got entity obj
		sessionFactory.getCurrentSession().save(personMergeSelectField);
	}

	/**
	 *
	 * Method Name: savePersonMergeUpdateLog Method Description: save person
	 * merge update log
	 *
	 * @param idPersonMerge
	 * @param category
	 * @param idPersonMergeWorker
	 * @return
	 */
	@Override
	public void savePersonMergeUpdateLog(int idPersonMerge, String category, int idPersonMergeWorker) {
		PersonMergeUpdateLog personMergeUpdateLog = new PersonMergeUpdateLog();
		personMergeUpdateLog.setIdPersonMerge(idPersonMerge);
		personMergeUpdateLog.setCdFieldCategory(category);
		personMergeUpdateLog.setIdCreatedPerson(idPersonMergeWorker);
		personMergeUpdateLog.setIdLastUpdatePerson(idPersonMergeWorker);
		personMergeUpdateLog.setDtCreated(new Date());
		personMergeUpdateLog.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(personMergeUpdateLog);
	}

	/**
	 *
	 * Method Name: getActivePersonPotentialDupDetail Method Description: get
	 * active person potential duplicate detail
	 *
	 * @param idClosedPerson
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public PersonPotentialDupDto getActivePersonPotentialDupDetail(int idClosedPerson, int idFwdPerson) {
		PersonPotentialDupDto personPotentialDupDto = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getActivePersonPotentialDupDetailSql)
				.addScalar("idPersonPotentialDup", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idDupPerson", StandardBasicTypes.LONG)
				.addScalar("idWrkrPerson", StandardBasicTypes.LONG).addScalar("indInvalids", StandardBasicTypes.STRING)
				.addScalar("indMergeds", StandardBasicTypes.STRING).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("comments", StandardBasicTypes.STRING).addScalar("merged", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PersonPotentialDupDto.class));
		query.setParameter("idPersonFrstCon", idClosedPerson);
		query.setParameter("idDupPersonFrstCon", idFwdPerson);
		query.setParameter("idPersonSecCon", idClosedPerson);
		query.setParameter("idDupPersonSecCon", idFwdPerson);
		List<PersonPotentialDupDto> personPotentialDupDtoList = (List<PersonPotentialDupDto>) query.list();
		if (!ObjectUtils.isEmpty(personPotentialDupDtoList) && 0 < personPotentialDupDtoList.size()) {
			personPotentialDupDto = personPotentialDupDtoList.get(0);
		}
		return personPotentialDupDto;
	}

	/**
	 *
	 * Method Name: updatePersonPotentialDupInfo Method Description: Update
	 * Person potential duplicate info
	 *
	 * @param personPotentialDup
	 * @return
	 */
	@Override
	public void updatePersonPotentialDupInfo(PersonPotentialDupDto personPotentialDupDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonPotentialDup.class)
				.add(Restrictions.eq("idPersonPotentialDup", personPotentialDupDto.getIdPersonPotentialDup()));
		PersonPotentialDup personPotentialDup = (PersonPotentialDup) criteria.uniqueResult();
		personPotentialDup.setIndInvalid(personPotentialDupDto.getIndInvalids());
		personPotentialDup.setIndMerged(personPotentialDupDto.getIndMergeds());
		personPotentialDup.setTxtComments(personPotentialDupDto.getComments());
		personPotentialDup.setCdRsnNotMerged(personPotentialDupDto.getMerged());
		personPotentialDup.setDtEnd(personPotentialDupDto.getDtEnd());
		sessionFactory.getCurrentSession().saveOrUpdate(personPotentialDup);
	}

	/**
	 *
	 * Method Name: getPersonPotentialDupList Method Description: Get person
	 * potential duplicate list
	 *
	 * @param idClosedPerson
	 * @return
	 */
	@Override
	public List<PersonPotentialDup> getPersonPotentialDupList(Long idClosedPerson) {
		Criterion rest1 = Restrictions.eq("personByIdPerson.idPerson", idClosedPerson);
		Criterion rest2 = Restrictions.eq("personByIdDupPerson.idPerson", idClosedPerson);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonPotentialDup.class)
				.add(Restrictions.or(rest1, rest2)).add(Restrictions.ne("indMerged", "Y"));

		return criteria.list();
	}

	/**
	 *
	 * Method Name: updatePersonPotentialDupRec Method Description: Update
	 * Person potential duplicate record
	 *
	 * @param personPotentialDup
	 * @return
	 */
	@Override
	public void updatePersonPotentialDupRec(PersonPotentialDup personPotentialDup) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonPotentialDupInfoSql);
		query.setParameter("indInvalid", personPotentialDup.getIndInvalid());
		query.setParameter("txtComments", personPotentialDup.getTxtComments());
		query.setParameter("indMerged", personPotentialDup.getIndMerged());
		query.setParameter("cdRsnNotMerged", personPotentialDup.getCdRsnNotMerged());
		query.setParameter("idPersonPotentialDup", personPotentialDup.getIdPersonPotentialDup());
		query.setParameter("dtEnd", personPotentialDup.getDtEnd());
		query.executeUpdate();
	}

	/**
	 *
	 * Method Name: updatePersonOnIncomingPersonMPS Method Description: update
	 * person incoming person mps
	 *
	 * @param fwdPersonId
	 * @param closedPersonId
	 * @return
	 */
	@Override
	public void updatePersonOnIncomingPersonMPS(Long fwdPersonId, Long closedPersonId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IncomingPersonMps.class);
		criteria.add(Restrictions.eq("person.idPerson", closedPersonId));
		List<IncomingPersonMps> incomingPersonMpsList = criteria.list();
		Person person = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", fwdPersonId)).uniqueResult();
		incomingPersonMpsList.stream().forEach(incomingPersonMps -> {
			incomingPersonMps.setPerson(person);
			sessionFactory.getCurrentSession().saveOrUpdate(incomingPersonMps);
		});
	}

	/**
	 *
	 * Method Name: insertMedicaidUpdate Method Description: insert medicaid
	 * update detail to database
	 *
	 * @param idPerson
	 * @param idStage
	 * @param idCase
	 * @param idEvent
	 * @param cdMedUpdType
	 * @param cdTranType
	 * @return
	 */
	@Override
	public void insertMedicaidUpdate(Long idPerson, Long idStage, Long idCase, Long idEvent, String cdMedUpdType,
			String cdTranType) {
		MedicaidUpdate medicaidUpdate = new MedicaidUpdate();
		Person person = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", idPerson)).uniqueResult();
		medicaidUpdate.setPerson(person);
		medicaidUpdate.getStage().setIdStage(idStage);
		medicaidUpdate.setIdMedUpdRecord(idEvent);
		medicaidUpdate.setCdMedUpdType(cdMedUpdType);
		medicaidUpdate.setCdMedUpdTransType(cdTranType);
		medicaidUpdate.setIdCase(idCase);
		sessionFactory.getCurrentSession().saveOrUpdate(medicaidUpdate);
	}

	@Override
	public void updatePersonClosedWithForward(int cdTaskCode, String cdEventType, Long idEvent, Long idStage,
			String txtEventDescr, Long idForwardPerson, Long idClosedPerson) {
		Query query = null;
		if (CodesConstant.CEVNTTYP_MCD.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardMedConsenter);
		} else if (ServiceConstants.CEVNTTYP_ASM.equals(cdEventType)) {
			// Defect Fix# 13766: Update merged person in SDM Safety Assessment for Open AR/INV/FSU/FPR/FRE Stages.
			if (Stream
					.of(ServiceConstants.CD_TASK_SA_AR, ServiceConstants.CD_TASK_SA, ServiceConstants.TASK_7430,
							ServiceConstants.TASK_7440, ServiceConstants.TASK_7450)
					.anyMatch(c -> c.equals(String.valueOf(cdTaskCode)))){
				query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardForSafetyAssessment);
			}else{
				query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardFamilyFactors);
			}
		} else if (ServiceConstants.CEVNTTYP_REM.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardHomeRemoval);
		} else if (ServiceConstants.CEVNTTYP_LES.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardLegaStatus);
		} else if (CodesConstant.CEVNTTYP_LEG.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardLegalAction);
		} else if (CodesConstant.CEVNTTYP_GUA.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardPersonGuardianship);
		} else if (ServiceConstants.CEVNTTYP_CON.equals(cdEventType)
				&& (cdTaskCode == 3010 || cdTaskCode == 4120 || cdTaskCode == 5570)) {

			if (StringUtils.isNotEmpty(txtEventDescr) && txtEventDescr.indexOf("Kinship") >= 0) {
				query = sessionFactory.getCurrentSession()
						.createSQLQuery(updatePersonClosedWithForwardContactPersonNarr);
			} else {
				query = sessionFactory.getCurrentSession()
						.createSQLQuery(updatePersonClosedWithForwardContactGuideNarr);
			}
		}

		else if (ServiceConstants.CEVNTTYP_CSP.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonOnEventPersonChildPlanParticip);
		} else if (CodesConstant.CEVNTTYP_PPT.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession()
					.createSQLQuery(updatePersonClosedWithForwardPptPersonParticipant);
		} else if (ServiceConstants.CEVNTTYP_PLA.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardPersonPlacement);
		} else if (CodesConstant.CEVNTTYP_PLN.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardFamilyPlanCandicacy);
		} else if (ServiceConstants.CEVNTTYP_PEA.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardPcaEligApp);
		} else if (CodesConstant.CEVNTTYP_AAA.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardADPTEligApp);
		} else if (CodesConstant.CEVNTTYP_DCR.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardDayCareRequest);
		}

		if (!ObjectUtils.isEmpty(query)) {
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
		}
		if (CodesConstant.CEVNTTYP_FCA.equals(cdEventType) || CodesConstant.CEVNTTYP_FCD.equals(cdEventType)
				|| CodesConstant.CEVNTTYP_FCR.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardFcePerson);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardDayFceIncome);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
		}
		if (CodesConstant.CEVNTTYP_ICA.equals(cdEventType) || CodesConstant.CEVNTTYP_ICB.equals(cdEventType)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardIcpcRequestPerson);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
			query = sessionFactory.getCurrentSession()
					.createSQLQuery(updatePersonClosedWithForwardIcpcTransmittalChild);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();

		}
		// For family plan, if it is not created from Legacy, also need update FAMILY_PLAN_NEEDS and FAMILY_PLAN_PARTCPNT TABLE
		if (CodesConstant.CEVNTTYP_PLN.equals(cdEventType)) {
			//Update family plan
			if (cdTaskCode == 4150 || cdTaskCode == 7080 || cdTaskCode == 5600) {
				query = sessionFactory.getCurrentSession().createSQLQuery(updateFamilyPlanNeeds);
				query.setParameter("idForwardPerson", idForwardPerson);
				query.setParameter("idClosedPerson", idClosedPerson);
				query.setParameter("idEvent", idEvent);
				query.executeUpdate();
				query = sessionFactory.getCurrentSession().createSQLQuery(updateFamilyPlanPartcpnt);
				query.setParameter("idForwardPerson", idForwardPerson);
				query.setParameter("idClosedPerson", idClosedPerson);
				query.setParameter("idEvent", idEvent);
				query.executeUpdate();
			}
			//Update family plan eval
			else if (cdTaskCode == 7300 || cdTaskCode == 7310 || cdTaskCode == 7320) {
				query = sessionFactory.getCurrentSession().createSQLQuery(updateFamilyPlanEvalNeeds);
				query.setParameter("idForwardPerson", idForwardPerson);
				query.setParameter("idClosedPerson", idClosedPerson);
				query.setParameter("idEvent", idEvent);
				query.executeUpdate();
				query = sessionFactory.getCurrentSession().createSQLQuery(updateFamilyPlanEvalPartcpnt);
				query.setParameter("idForwardPerson", idForwardPerson);
				query.setParameter("idClosedPerson", idClosedPerson);
				query.setParameter("idEvent", idEvent);
				query.executeUpdate();
			}
		}
		//CPS_FSNA
		if (cdTaskCode == 7400 || cdTaskCode == 7410 || cdTaskCode == 7420) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updateCpsFsnaRspns);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
			query = sessionFactory.getCurrentSession().createSQLQuery(updateCpsFsnaPrmry);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
			query = sessionFactory.getCurrentSession().createSQLQuery(updateCpsFsnaSecndry);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
		} 
		//SDM REUNFCTN ASMNT
		if (cdTaskCode == 7490) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updateReunfctnAsmntSecndry);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
			query = sessionFactory.getCurrentSession().createSQLQuery(updateReunfctnAsmntChld);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
			query = sessionFactory.getCurrentSession().createSQLQuery(updateReunfctnAsmntHshld);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
			query = sessionFactory.getCurrentSession().createSQLQuery(updateReunfctnAsmntPrmry);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
		}
		//SDM RISK REASMNT
		if (cdTaskCode == 7480 || cdTaskCode == 7470) {
			query = sessionFactory.getCurrentSession().createSQLQuery(updateRiskReasmntHshld);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
			query = sessionFactory.getCurrentSession().createSQLQuery(updateRiskReasmntPrmry);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
			query = sessionFactory.getCurrentSession().createSQLQuery(updateRiskReasmntSecndry);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
		}

		// Defect Fix# 13766: CPS SDM RISK Assemssments
		if(ServiceConstants.CEVNTTYP_ASM.equals(cdEventType) && Stream
				.of(ServiceConstants.CD_TASK_RA_AR, ServiceConstants.CD_TASK_RA)
				.anyMatch(c -> c.equals(String.valueOf(cdTaskCode)))){
			query = sessionFactory.getCurrentSession().createSQLQuery(updtPrsnClsdWdFwdRiskAssessmentHshld);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
			query = sessionFactory.getCurrentSession().createSQLQuery(updtPrsnClsdWdFwdRiskAssessmentPrmryCrgvr);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
			query = sessionFactory.getCurrentSession().createSQLQuery(updtPrsnClsdWdFwdRiskAssessmentSndryCrgvr);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
		}

		// Defect Fix# 15594: FBSS Referrals created after 09/10/2020 release
		if(ServiceConstants.CEVNTTYP_FRM.equals(cdEventType) && Stream
				.of(ServiceConstants.FBSS_REF_TASK_CODE_CPSINV, ServiceConstants.FBSS_REF_TASK_CODE_CPSAR)
				.anyMatch(c -> c.equals(String.valueOf(cdTaskCode)))){
			query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonClosedWithForwardForFBSSReferral);
			query.setParameter("idForwardPerson", idForwardPerson);
			query.setParameter("idClosedPerson", idClosedPerson);
			query.setParameter("idEvent", idEvent);
			query.executeUpdate();
		}
	}

	/**
	 * 
	 * Method Name: updatePersonOnEventPersonLink Method Description: update
	 * person on event person link
	 * 
	 * @param idForwardPerson
	 * @param idClosedPerson
	 * @return
	 */
	@Override
	public void updatePersonOnEventPersonLink(long idFwdPerson, long idClosedPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonOnEventPersonLinkSql);
		query.setParameter("idForwardPerson", idFwdPerson);
		query.setParameter("idClosedPerson", idClosedPerson);
		query.executeUpdate();
	}

	/**
	 * 
	 * Method Name: updatePersonOnStagePersonLink Method Description:update
	 * person on stage person link
	 * 
	 * @param idForwardPerson
	 * @param idClosedPerson
	 * @return
	 */
	@Override
	public void updatePersonOnStagePersonLink(Long idForwardPerson, Long idClosedPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updatePersonOnStagePersonLinkSql);
		query.setParameter("idForwardPerson", idForwardPerson);
		query.setParameter("idClosedPerson", idClosedPerson);
		query.executeUpdate();
	}

	@Override
	public void updatePersonOnLetterAllegationLink(Set<Long> openStageIds, Long personMergeWorkerId,
												   Long closedPersonId, Long forwardPersonId){
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateVictimClosedWithForwardForLtrAllegationLink);
		query.setParameter("idForwardPerson", forwardPersonId);
		query.setParameter("idClosedPerson", closedPersonId);
		query.setParameter("idLastUpdatePerson", personMergeWorkerId);
		query.setParameterList("stages", openStageIds);
		query.executeUpdate();

		query = sessionFactory.getCurrentSession().createSQLQuery(updateAllegPerpertratorClosedWithForwardForLtrAllegationLink);
		query.setParameter("idForwardPerson", forwardPersonId);
		query.setParameter("idClosedPerson", closedPersonId);
		query.setParameter("idLastUpdatePerson", personMergeWorkerId);
		query.setParameterList("stages", openStageIds);
		query.executeUpdate();

	}

	@Override
	public List<String> getPersonMergeUpdateLogList(Long idPersonMerge) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonMergeUpdateLog.class);
		criteria.add(Restrictions.eq("idPersonMerge", idPersonMerge));

		List<PersonMergeUpdateLog> personMergeUpdateLogList = criteria.list();
		List<String> personMergeLogList = new ArrayList<>();
		personMergeUpdateLogList.stream()
				.forEach(personMergeUpdateLog -> personMergeLogList.add(personMergeUpdateLog.getCdFieldCategory()));
		return personMergeLogList;
	}

	/**
	 * Method Name: getPersonMergeUpdateLogListByIdPersonMerge Method
	 * Description: This method gets the Person Merge update log (fields
	 * affected by a merge)
	 * 
	 * @param idPersonMerge
	 * @return List<PersonMergeUpdateLogDto>
	 */
	@Override
	public List<PersonMergeUpdateLogDto> getPersonMergeUpdateLogListByIdPersonMerge(Long idPersonMerge) {
		List<PersonMergeUpdateLogDto> cdFieldCategory = new ArrayList<>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonMergeUpdateLogListSql);
		query.setParameter("idPersonMerge", idPersonMerge)
				.setResultTransformer(Transformers.aliasToBean(PersonMergeUpdateLogDto.class));

		cdFieldCategory = query.list();

		return cdFieldCategory;
	}

	/**
	 * Method Name: getPersonMergeMessages Method Description:This method gets
	 * all the messages saved during a person merge These include - Information
	 * messages shown to user - Validation messages - Post merge messages
	 * 
	 * @param idPersonMerge
	 * @return List<MergeSplitVldMsgDto>
	 */
	@Override
	public List<MergeSplitVldMsgDto> getPersonMergeMessages(Long idPersonMerge) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonMergeValidationLog.class);
		criteria.add(Restrictions.eq("idPersonMerge", idPersonMerge));
		List<PersonMergeValidationLog> personMergeValidationLogList = criteria.list();
		List<MergeSplitVldMsgDto> mergeSplitVldMsgDtoList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(personMergeValidationLogList)) {
			personMergeValidationLogList.stream().forEach(personMergeValidationLog -> {
				MergeSplitVldMsgDto mergeSplitVldMsgDto = new MergeSplitVldMsgDto();
				if (!TypeConvUtil.isNullOrEmpty(personMergeValidationLog.getCdErrorType())) {
					mergeSplitVldMsgDto.setMsgCategory(personMergeValidationLog.getCdErrorType());
				}

				if (!TypeConvUtil.isNullOrEmpty(personMergeValidationLog.getNbrMessage())) {
					mergeSplitVldMsgDto.setMessageInt(new Long(personMergeValidationLog.getNbrMessage()).intValue());
				}

				if (!TypeConvUtil.isNullOrEmpty(personMergeValidationLog.getTxtMessage())) {
					mergeSplitVldMsgDto.setMessage(personMergeValidationLog.getTxtMessage());
				}

				if (!TypeConvUtil.isNullOrEmpty(personMergeValidationLog.getNbrStep())) {
					mergeSplitVldMsgDto.setStep(new Integer(personMergeValidationLog.getNbrStep()));
				}

				mergeSplitVldMsgDtoList.add(mergeSplitVldMsgDto);
			});
		}
		return mergeSplitVldMsgDtoList;
	}

	/**
	 * Method Name: getForwardCaseInCaseMerge Method Description: Fetch forward
	 * case in case of case merge
	 * 
	 * @param caseId
	 * @return CaseValueDto
	 * @throws DataNotFoundException
	 */
	@Override
	public CaseValueDto getForwardCaseInCaseMerge(Long caseId) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getForwardCaseInCaseMergeSql)
				.addScalar("indCaseSensitive", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.setParameter("idCaseMerge", caseId).setResultTransformer(Transformers.aliasToBean(CaseValueDto.class));

		CaseValueDto caseValueDto = (CaseValueDto) sqlQuery.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(caseValueDto)) {
			CaseValueDto valueDto = new CaseValueDto();
			valueDto.setIdCase(ServiceConstants.ZERO_VAL);
			valueDto.setIndCaseSensitive(ServiceConstants.N);
			return valueDto;
		}

		return caseValueDto;
	}

	/**
	 * Method Name: getStagesUpdatedInMerge Method Description:This function
	 * fetches the list of open stages updated in a particular merge. These will
	 * be the open stages for closed person at the time of merge.
	 * 
	 * @param idPersonMerge
	 * @return List<StagePersonValueDto>
	 * @throws DataNotFoundException
	 */
	@Override
	public List<StagePersonValueDto> getStagesUpdatedInMerge(Long idPersonMerge) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStagesUpdatedInMergeSql)
				.addScalar("indSensitiveCase", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStage", StandardBasicTypes.LONG)
				.addScalar("nmStage", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtStageStart", StandardBasicTypes.DATE)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING).setParameter("idPersonMerge", idPersonMerge)
				.setResultTransformer(Transformers.aliasToBean(StagePersonValueDto.class));

		return sqlQuery.list();
	}

	/**
	 * Method Name: getForwardPersonInMerge Method Description: This method
	 * returns the forward person Id for a person
	 * 
	 * @param ulIdPerson
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@Override
	public Long getForwardPersonInMerge(Long ulIdPerson) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonMerge.class);
		criteria.add(Restrictions.eq("indPersMergeInvalid", ServiceConstants.N.charAt(0)));
		criteria.add(Restrictions.eq("personByIdPersMergeClosed.idPerson", ulIdPerson));

		List<PersonMerge> personMergeList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(personMergeList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("PersonMergeSplit.ForwardPersonInMerge.NotFound", null, Locale.US));
		}

		return personMergeList.get(ServiceConstants.Zero).getPersonByIdPersMergeForward().getIdPerson();
	}

	/**
	 * Method Name: checkIfMergeListLegacy Method Description: This method
	 * checks if the merge list to be fetched in a legacy stye for a person
	 * forward
	 * 
	 * @param fwdPersonId
	 * @return Boolean
	 */
	@Override
	public Boolean checkIfMergeListLegacy(Long fwdPersonId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonMerge.class);
		criteria.add(Restrictions.isNull("dtPersMergeSplit"));
		criteria.add(Restrictions.eq("indPersMergeInvalid", ServiceConstants.N_CHAR));
		criteria.add(Restrictions.isNull("indDirectMerge"));
		criteria.add(Restrictions.eq("personByIdPersMergeForward.idPerson", fwdPersonId));
		criteria.setProjection(Projections.count("idPersonMerge"));
		Boolean mergeListLegacy = false;
		Long rowCount = (Long) criteria.uniqueResult();
		if (rowCount > 20) {
			mergeListLegacy = true;
		}
		return mergeListLegacy;
	}

	/**
	 * Method Name: getPersonMergeHierarchyList Method Description: This method
	 * returns the merge hierarchy list for a forward person.
	 * 
	 * @param fwdPersonId
	 * @param mergeListLegacy
	 * @return List<PersonMergeSplitValueDto>
	 */
	@Override
	public List<PersonMergeSplitValueDto> getPersonMergeHierarchyList(Long fwdPersonId, Boolean mergeListLegacy) {
		ArrayList<PersonMergeSplitValueDto> personMergeSplitValueDtoList = new ArrayList<PersonMergeSplitValueDto>();
		boolean bLoopExists = false;
		if (!mergeListLegacy) {
			SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(getPersonMergeHierarchyListSql).addScalar("idPersonMerge", StandardBasicTypes.LONG)
					.addScalar("idForwardPerson", StandardBasicTypes.LONG)
					.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
					.addScalar("dtPersonMerge", StandardBasicTypes.DATE)
					.addScalar("idClosedPerson", StandardBasicTypes.LONG)
					.addScalar("idPersonMergeWorker", StandardBasicTypes.LONG)
					.addScalar("idPersonMergeSplitWorker", StandardBasicTypes.LONG)
					.addScalar("indPersonMergeInvalid", StandardBasicTypes.STRING)
					.addScalar("dtPersonMergeSplit", StandardBasicTypes.DATE)
					.addScalar("idMergeGroup", StandardBasicTypes.LONG)
					.addScalar("indDirectMerge", StandardBasicTypes.STRING)
					.addScalar("idGroupLink", StandardBasicTypes.LONG).setParameter("ulIdPerson", fwdPersonId)
					.setResultTransformer(Transformers.aliasToBean(PersonMergeSplitValueDto.class));

			personMergeSplitValueDtoList = (ArrayList<PersonMergeSplitValueDto>) sqlQuery.list();

			if (CollectionUtils.isEmpty(personMergeSplitValueDtoList)) {
				bLoopExists = true;
			}

		}
		if (bLoopExists || mergeListLegacy) {

			SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getMergeListSql)
					.addScalar("idPersonMerge", StandardBasicTypes.LONG)
					.addScalar("idForwardPerson", StandardBasicTypes.LONG)
					.addScalar("dtLastUpdate", StandardBasicTypes.LONG)
					.addScalar("dtPersonMerge", StandardBasicTypes.LONG)
					.addScalar("idClosedPerson", StandardBasicTypes.LONG)
					.addScalar("idPersonMergeWorker", StandardBasicTypes.LONG)
					.addScalar("idPersonMergeSplitWorker", StandardBasicTypes.LONG)
					.addScalar("indPersonMergeInvalid", StandardBasicTypes.LONG)
					.addScalar("dtPersonMergeSplit", StandardBasicTypes.LONG)
					.addScalar("idMergeGroup", StandardBasicTypes.LONG)
					.addScalar("indDirectMerge", StandardBasicTypes.LONG)
					.addScalar("idGroupLink", StandardBasicTypes.LONG)
					.addScalar("nmForwardPerson", StandardBasicTypes.LONG)
					.addScalar("nmClosedPerson", StandardBasicTypes.LONG)
					.addScalar("nmPersonMergeWorker", StandardBasicTypes.LONG)
					.addScalar("nmPersonMergeSplitWorker", StandardBasicTypes.LONG)
					.setParameter("fwdPersonId", fwdPersonId)
					.setResultTransformer(Transformers.aliasToBean(PersonMergeSplitValueDto.class));

			personMergeSplitValueDtoList = (ArrayList<PersonMergeSplitValueDto>) sqlQuery.list();
			personMergeSplitValueDtoList.stream().forEach(personMergeSplitValueDto -> {
				if (personMergeSplitValueDto.getIdForwardPerson() == fwdPersonId
						&& !ServiceConstants.Y.equals(personMergeSplitValueDto.getIndPersonMergeInvalid())
						&& personMergeSplitValueDto.getIdClosedPerson() != 999999999
						&& personMergeSplitValueDto.getIdPersonMergeSplitWorker() == 0) {
					personMergeSplitValueDto.setIndDirectMerge(ServiceConstants.Y);
				} else {
					personMergeSplitValueDto.setIndDirectMerge(ServiceConstants.N);
				}
				personMergeSplitValueDto.setIndLegacy(ServiceConstants.Y);
			});
		}
		return personMergeSplitValueDtoList;
	}

	/**
	 * Method Name: getPersonMergeSelectFieldList Method Description: This
	 * function fetches the selections made by a user at Select Forward Person
	 * page during a merge.
	 * 
	 * @param idPersonMerge
	 * @return List<PersonMergeUpdateLogDto>
	 */
	@Override
	public List<PersonMergeUpdateLogDto> getPersonMergeSelectFieldList(Long idPersonMerge) {
		List<PersonMergeUpdateLogDto> selectFieldList = new ArrayList<>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonMergeSelectFieldListSql)
				.addScalar("cdRole", StandardBasicTypes.STRING)
				.addScalar("txtSelectFieldName", StandardBasicTypes.STRING);

		query.setParameter("idPersonMerge", idPersonMerge)
				.setResultTransformer(Transformers.aliasToBean(PersonMergeUpdateLogDto.class));

		selectFieldList = query.list();

		return selectFieldList;
	}

	/**
	 * 
	 * Method Name: createSnapshot Method Description: run procedure to create
	 * snapshot
	 * 
	 * @param idSnapshotTable
	 * @param idSnapshotDtl
	 * @param idPerson
	 * @throws SQLException
	 */
	@Override
	public void createSnapshot(Long idSnapshotTable, Long idSnapshotDtl, Long idPerson) throws SQLException {
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
		CallableStatement callableStatement = null;
		try {
			callableStatement = connection.prepareCall(createSnapshotSql);
			callableStatement.setLong(1, idSnapshotTable);
			callableStatement.setLong(2, idSnapshotDtl);
			callableStatement.setLong(3, idPerson);
			callableStatement.execute();
			callableStatement.close();
			connection.close();
		} catch (Exception e) {
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;

		} finally {
			if (!ObjectUtils.isEmpty(callableStatement))
				callableStatement.close();
			if (!ObjectUtils.isEmpty(connection))
				connection.close();
		}
	}

	/**
	 * 
	 * Method Name: updatePersonOnStagePersonLink Method Description:This
	 * function updates forward person Id on a specific stage person link
	 * record. It also updates boolean bIndCaregiver = false; int indNmStage =
	 * 0;
	 * 
	 * @param idForwardPerson
	 * @param idClosedPerson
	 * @param idStagePersonLink
	 * @param dataToUpdate
	 */
	@Override
	public void updatePersonOnStagePersonLink(Long idForwardPerson, Long idStagePersonLink,
			StagePersonValueDto dataToUpdate) {
		StagePersonLink stagePersonLink = (StagePersonLink) sessionFactory.getCurrentSession()
				.createCriteria(StagePersonLink.class).add(Restrictions.eq("idStagePersonLink", idStagePersonLink))
				.uniqueResult();
		if (!ObjectUtils.isEmpty(stagePersonLink)) {
			stagePersonLink.setIdPerson(idForwardPerson);
			if (!ObjectUtils.isEmpty(dataToUpdate.getCdStagePersType()))
				stagePersonLink.setCdStagePersType(dataToUpdate.getCdStagePersType());
			if (!ObjectUtils.isEmpty(dataToUpdate.getIndStagePersInLaw()))
				stagePersonLink.setIndStagePersInLaw(dataToUpdate.getIndStagePersInLaw());
			if (!ObjectUtils.isEmpty(dataToUpdate.getStagePersNotes()))
				stagePersonLink.setTxtStagePersNotes(dataToUpdate.getStagePersNotes());
			if (!ObjectUtils.isEmpty(dataToUpdate.getIndStagePersReporter()))
				stagePersonLink.setIndStagePersReporter(dataToUpdate.getIndStagePersReporter());
			if (!ObjectUtils.isEmpty(dataToUpdate.getIndNmStage()) && dataToUpdate.getIndNmStage().equals(1L))
				stagePersonLink.setIndNmStage(Boolean.TRUE);
			if (!ObjectUtils.isEmpty(dataToUpdate.getIndKinPrCaregiver()))
				stagePersonLink.setIndKinPrCaregiver(dataToUpdate.getIndKinPrCaregiver());
			if (!ObjectUtils.isEmpty(dataToUpdate.getIndNytdContact()))
				stagePersonLink.setIndNytdContact(dataToUpdate.getIndNytdContact());
			if (!ObjectUtils.isEmpty(dataToUpdate.getIndNytdContactPrimary()))
				stagePersonLink.setIndNytdContactPrimary(dataToUpdate.getIndNytdContactPrimary());
			sessionFactory.getCurrentSession().save(stagePersonLink);
		}
	}

	/**
	 *
	 * Method Name: getSxMutualIncdntForSplit
	 * Method Description: get Sexual mutual Incidents for split
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return List<Long>
	 */
	public List<Long> getSxMutualIncdntForSplit(Long idPersMerge, Long idForwardPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getSxMutualIncdntForSplit)
				.addScalar("IDCHILDSXMUTUALINCDNT", StandardBasicTypes.LONG).setParameter("idPersonMerge", idPersMerge)
				.setParameter("idPerson", idForwardPerson).setParameter("idReferenceData", idPersMerge).list();
	}

	/**
	 *
	 * Method Name: updateSxMutualIncdntForSplit
	 * Method Description: Update CHILD_SX_MUTUAL_INCDNT for split
	 *
	 * @param idClosedPerson
	 * @param idChildMUTUALINCDNT
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public void updateSxMutualIncdntForSplit(long idClosedPerson, long idChildVCTMZTNINCDNT, long idFwdPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateSxMutualIncdntForSplit);
		query.setParameter("closedPersonId", idClosedPerson);
		query.setParameter("idChildMUTUALINCDNT", idChildVCTMZTNINCDNT);
		query.setParameter("forwardPersonId", idFwdPerson);
		query.executeUpdate();

		// Update id_person_mutual
		query = sessionFactory.getCurrentSession().createSQLQuery(updateSxMutualIncdntForSplit2);
		query.setParameter("closedPersonId", idClosedPerson);
		query.setParameter("idChildMUTUALINCDNT", idChildVCTMZTNINCDNT);
		query.setParameter("forwardPersonId", idFwdPerson);
		query.executeUpdate();
	}

	/**
	 * 
	 * Method Name: getSxVctmznIncdntForSplit Method Description: get Sexual
	 * victimization Incidents for split
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return List<Long>
	 */
	public List<Long> getSxVctmznIncdntForSplit(Long idPersMerge, Long idForwardPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getSxVctmznIncdntForSplit)
				.addScalar("IDCHILDSXVCTMZTNINCDNT", StandardBasicTypes.LONG).setParameter("idPersonMerge", idPersMerge)
				.setParameter("idPerson", idForwardPerson).setParameter("idReferenceData", idPersMerge).list();
	}


	/**
	 *
	 * Method Name: updatePcspAsmntForSplit Method Description: Update
	 * CHILD_SX_VCTMZTN_INCDNT for split
	 *
	 * @param idClosedPerson
	 * @param idChildVCTMZTNINCDNT
	 * @param idForwardPerson
	 * @return
	 */
	@Override
	public void updateSxVctmznIncdntForSplit(long idClosedPerson, long idChildVCTMZTNINCDNT, long idFwdPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateSxVctmznIncdntForSplit);
		query.setParameter("closedPersonId", idClosedPerson);
		query.setParameter("idChildVCTMZTNINCDNT", idChildVCTMZTNINCDNT);
		query.setParameter("forwardPersonId", idFwdPerson);
		query.executeUpdate();

		// Update id_person_aggressor
		query = sessionFactory.getCurrentSession().createSQLQuery(updateSxVctmznIncdntForSplit2);
		query.setParameter("closedPersonId", idClosedPerson);
		query.setParameter("idChildVCTMZTNINCDNT", idChildVCTMZTNINCDNT);
		query.setParameter("forwardPersonId", idFwdPerson);
		query.executeUpdate();
	}

	/**
	 *
	 * Method Name: getSxVctmznIncdntForSplit Method Description: get Sexual
	 * victimization Incidents for split
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return List<Long>
	 */
	public List<Long> getRtbExceptionsForSplit(Long idPersMerge, Long idForwardPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getRtbExceptionsForSplit)
				.addScalar("IDCHILDRTBEXCEPTN", StandardBasicTypes.LONG).setParameter("idPersonMerge", idPersMerge)
				.setParameter("idPerson", idForwardPerson).setParameter("idReferenceData", idPersMerge).list();
	}

	/**
	 * 
	 * Method Name: updatePcspAsmntForSplit Method Description: Update
	 * CHILD_SX_VCTMZTN_INCDNT for split
	 * 
	 * @param idClosedPerson
	 * @param idChildVCTMZTNINCDNT
	 * @param idForwardPerson
	 * @return
	 */

	public void updateRtbExceptionsForSplit(long idClosedPerson, long idChildRtbExceptn, long idFwdPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateRtbExceptionsForSplit);
		query.setParameter("closedPersonId", idClosedPerson);
		query.setParameter("idChildRtbExceptn", idChildRtbExceptn);
		query.setParameter("forwardPersonId", idFwdPerson);
		query.executeUpdate();
	}

	/**
	 * 
	 * Method Name: getAfterBeforeSxVctmztn Method Description: gets Sexual
	 * Vctmztn History data Before merge
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return Object
	 */
	public Object getBeforeSplitSxVctmztn(Long idPersMerge, Long idForwardPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getBeforeMergesxvctmztn)
				.addScalar("IDSSCHILDSXVCTMZTN", StandardBasicTypes.LONG).setParameter("idPerson", idForwardPerson)
				.setParameter("idReferenceData", idPersMerge).setParameter("idPerson", idForwardPerson).uniqueResult();
	}

	/**
	 * 
	 * Method Name: updateChildSxVctmztn Method Description: This method will
	 * update the ChildSxVctmztn Data
	 * 
	 * @param ChildSxVctmztn
	 * 
	 */
	public void updateChildSxVctmztn(ChildSxVctmztn childSxVctmztn) {

		sessionFactory.getCurrentSession().saveOrUpdate(childSxVctmztn);
	}

	/**
	 *
	 * Method Name: getBeforeSplitSxMutualIncdnt
	 * Method Description: gets Sexual Mutual Incident data Before merge
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return Object
	 */
	public Object getBeforeSplitSxMutualIncdnt(Long idPersMerge, Long idForwardPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getBeforeMergeSxMutualIncdnt)
				.addScalar("IDSSCHILDSXMUTUAL", StandardBasicTypes.LONG).setParameter("idPerson", idForwardPerson)
				.setParameter("idReferenceData", idPersMerge).setParameter("idPerson", idForwardPerson).uniqueResult();
	}

	/**
	 * 
	 * Method Name: getClosedPersonBeforeSplitSxVctmztnIncdnt Method
	 * Description: gets svh incidents data Before merge for closed person
	 * 
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return Object
	 */
	public List<Long> getClosedPersonBeforeSplitSxVctmztnIncdnt(Long idPersMerge, Long closedPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getClosedBeforeMergesxvctmztnIncdnt)
				.addScalar("IDSSCHILDSXVCTMZTNINCDNT", StandardBasicTypes.LONG).setParameter("idPerson", closedPerson)
				.setParameter("idReferenceData", idPersMerge).setParameter("idPerson", closedPerson).list();
	}


	/**
	 *
	 * Method Name: getClosedPersonBeforeSplitRtbExceptions Method
	 * Description: gets RTB exceptions  data Before merge for closed person
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return Object
	 */
	public List<Long> getClosedPersonBeforeSplitRtbExceptions(Long idPersMerge, Long closedPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getClosedBeforeMergeRtbExceptions)
				.addScalar("IDCHILDRTBEXCEPTN", StandardBasicTypes.LONG).setParameter("idPerson", closedPerson)
				.setParameter("idReferenceData", idPersMerge).setParameter("idPerson", closedPerson).list();
	}

	@Override
	public void updateRtbExceptionsForSplit(Long idClosedPerson, long idChildRtbExceptn, Long idFwdPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateRtbExceptionsForSplit);
		query.setParameter("forwardPersonId", idFwdPerson);
		query.setParameter("idChildRtbExceptn", idChildRtbExceptn);
		query.setParameter("closedPersonId", idClosedPerson);
		query.executeUpdate();
	}

	/* PPM 65209
	 * update table PLACEMENT_TA -> ID_RESPITE_PERSON column if any rows present with closed person id 
	 * this table does not have ID_PERSON columns as this is child table to PLACEMENT table
	 * but this has a column which is FK to PERSON table so if records present with closed person,
	 *  we have to update with fwd person id */
	public void updatePlacementTAWithFwdPerson(Long idForwardPerson, Long idClosedPerson, Long idPersonMergeWorker){
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updatePlacementTAWithFwdPerson);
		query.setParameter("idForwardPerson", idForwardPerson);
		query.setParameter("idPersonMergeWorker", idPersonMergeWorker);
		query.setParameter("idClosedPerson", idClosedPerson);
		query.executeUpdate();
	}
	
	/*ppm 65209	
	 * update table PLACEMENT_TA -> ID_RESPITE_PERSON column if any rows present with forward person id 
	 * this table does not have ID_PERSON columns as this is child table to PLACEMENT table
	 * but this has a column which is FK to PERSON table so if records present with forward person, we have to update with closed person id*/
	public void splitPlacementTAWithFwdPerson(PersonMergeSplitDto personMergeSplitDto){
		Query query = sessionFactory.getCurrentSession().createSQLQuery(splitPlacementTAWithFwdPerson);
		query.setParameter("idClosedPerson", personMergeSplitDto.getIdClosedPerson());
		query.setParameter("idPersonMergeWorker", personMergeSplitDto.getIdPersonMergeWorker());
		query.setParameter("idForwardPerson", personMergeSplitDto.getIdForwardPerson());
		query.executeUpdate();
	}


	@Override
	public void updateRTBExceptionWithFwdPerson(Long idForwardPerson, Long idClosedPerson, Long idPersonMergeWorker) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateChildRtbExceptnWithFwdPerson);
		query.setParameter("idForwardPerson", idForwardPerson);
		query.setParameter("idPersonMergeWorker", idPersonMergeWorker);
		query.setParameter("idClosedPerson", idClosedPerson);
		query.executeUpdate();
	}

	/**
	 *
	 * Method Name: getClosedPersonBeforeSplitSxMutualIncdnt Method
	 * Description: gets sih incidents data Before merge for closed person
	 *
	 * @param idPersMerge
	 * @param idForwardPerson
	 * @return Object
	 */
	public List<Long> getClosedPersonBeforeSplitSxMutualIncdnt(Long idPersMerge, Long closedPerson) {
		return sessionFactory.getCurrentSession().createSQLQuery(getClosedBeforeMergeSxMutualIncdnt)
				.addScalar("IDSSCHILDSXMUTUALINCDNT", StandardBasicTypes.LONG).setParameter("idPerson", closedPerson)
				.setParameter("idReferenceData", idPersMerge).setParameter("idPerson", closedPerson).list();
	}
	
	/**
	 * this method is used to update the closed person to forward person if closed person is aggressor in CHILD_SX_VCTMZTN_INCDNT table
	 * @param personMergeSplitDto
	 */
	public boolean updateIfClosedPersonAggressor(PersonMergeSplitDto personMergeSplitDto){
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateIfClosedPersonAggressor);
		query.setParameter("idForwardPerson", personMergeSplitDto.getIdForwardPerson());
		query.setParameter("idPersonMergeWorker", personMergeSplitDto.getIdPersonMergeWorker());
		query.setParameter("idClosedPerson", personMergeSplitDto.getIdClosedPerson());
		int cnt =query.executeUpdate();
		return cnt>0;
	}
	
	/**
	 * this method is used to update the closed person to forward person if closed person is mutual in CHILD_SX_MUTUAL_INCDNT table
	 * @param personMergeSplitDto
	 */
	public void updateIfClosedPersonMutual(PersonMergeSplitDto personMergeSplitDto){
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateIfClosedPersonMutual);
		query.setParameter("idForwardPerson", personMergeSplitDto.getIdForwardPerson());
		query.setParameter("idPersonMergeWorker", personMergeSplitDto.getIdPersonMergeWorker());
		query.setParameter("idClosedPerson", personMergeSplitDto.getIdClosedPerson());
		query.executeUpdate();
	}
	
	public boolean updateIfClosedPersonVictim(PersonMergeSplitDto personMergeSplitDto){
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateIfClosedPersonVictim);
		query.setParameter("idForwardPerson", personMergeSplitDto.getIdForwardPerson());
		query.setParameter("idPersonMergeWorker", personMergeSplitDto.getIdPersonMergeWorker());
		query.setParameter("idClosedPerson", personMergeSplitDto.getIdClosedPerson());
		int cnt =query.executeUpdate();
		return cnt>0;
	}
}
