package us.tx.state.dfps.service.childbackground.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.dto.PrincipalLegalStatusDto;
import us.tx.state.dfps.service.childbackground.dao.ChildBackgroundDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.person.dto.ChildPlanDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ChildBackgroundDaoImpl will implemented all operation defined in
 * ChildBackroundDao Interface related ChildBackground module.. March 27, 2018-
 * 2:02:51 PM © 2017 Texas Department of Family and Protective Services
 */

@Repository
public class ChildBackgroundDaoImpl implements ChildBackgroundDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${ChildBackgroundDaoImpl.getChildPlan}")
	private String getChildPlanSql;

	@Value("${ChildBackgroundDaoImpl.getRmvlDateAndRmvlEventMax}")
	private String getRmvlDateAndRmvlEventMaxSql;

	@Value("${ChildBackgroundDaoImpl.getRmvlDateAndRmvlEventMin}")
	private String getRmvlDateAndRmvlEventMinSql;

	@Value("${ChildBackgroundDaoImpl.getPrincipalLegalStatus}")
	private String getPrincipalLegalStatusSql;

	/**
	 * 
	 * Method Name: getChildPlan DAM Name: CSECB9D Method Description:This DAM
	 * will select a full row from the EVENT and CHILD PLAN tables here DT EVNT
	 * occurred is greatest. Created for sir 19882
	 ** 
	 * @param idPerson
	 * @param idStage
	 * @return
	 */
	@Override
	public ChildPlanDto getChildPlan(Long idPerson, Long idStage) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getChildPlanSql)
				.addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdCspPlanPermGoal", StandardBasicTypes.STRING)
				.addScalar("cdCspPlanType", StandardBasicTypes.STRING)
				.addScalar("dtCspPermGoalTarget", StandardBasicTypes.DATE)
				.addScalar("dtCspNextReview", StandardBasicTypes.DATE)
				.addScalar("txtCspLengthOfStay", StandardBasicTypes.STRING)
				.addScalar("txtCspLosDiscrepancy", StandardBasicTypes.STRING)
				.addScalar("txtCspParticipComment", StandardBasicTypes.STRING)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idEventStage", StandardBasicTypes.LONG)
				.addScalar("cdEventType", StandardBasicTypes.STRING).addScalar("idEventPerson", StandardBasicTypes.LONG)
				.addScalar("cdTask", StandardBasicTypes.STRING).addScalar("txtEventDescr", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setParameter("idStage", idStage).setResultTransformer(Transformers.aliasToBean(ChildPlanDto.class));

		return (ChildPlanDto) query.uniqueResult();

	}

	/**
	 * 
	 * Method Name: getRmvlDateAndRmvlEvent Dam Name : CDYN10D Method
	 * Description:Retrieves Removal Date and Removal Event
	 * 
	 * @param idPerson
	 * @param cReqFunc
	 * @return
	 */

	@Override
	public CnsrvtrshpRemovalDto getRmvlDateAndRmvlEvent(Long idPerson, String cReqFunc) {
		CnsrvtrshpRemovalDto cnsrvtrshpRemovalDto = new CnsrvtrshpRemovalDto();
		StringBuilder getRmvlSql = new StringBuilder();
		if (cReqFunc.equals(ServiceConstants.STR_ONE_VAL)) {
			getRmvlSql.append(getRmvlDateAndRmvlEventMaxSql);
		} else {
			getRmvlSql.append(getRmvlDateAndRmvlEventMinSql);
		}

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRmvlSql.toString())
				.addScalar("idRemovalEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("dtRemoval", StandardBasicTypes.DATE).addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("indRemovalNaCare", StandardBasicTypes.CHARACTER)
				.addScalar("indRemovalNaChild", StandardBasicTypes.CHARACTER)
				.addScalar("removalAgeMo", StandardBasicTypes.LONG).addScalar("removalAgeYr", StandardBasicTypes.LONG)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemovalDto.class));
		// [artf165612] - Defect Fix# 15964 - unable to enter PPM conference note
		cnsrvtrshpRemovalDto = (CnsrvtrshpRemovalDto) query.list().get(0);// .uniqueResult();

		return cnsrvtrshpRemovalDto;
	}

	/**
	 * 
	 * Method Name: getPrincipalLegalStatus DAM Name: CLSC34D Method
	 * Description:Returns Legal Status of Principal
	 * 
	 * @param idStage
	 * @param cdStagePersType
	 * @return
	 */

	@SuppressWarnings("unchecked")
	@Override

	public List<PrincipalLegalStatusDto> getPrincipalLegalStatus(Long idStage, String cdStagePersType) {
		List<PrincipalLegalStatusDto> principalLegalStatusDtoList = new ArrayList<PrincipalLegalStatusDto>();

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPrincipalLegalStatusSql)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatStatusDt", StandardBasicTypes.DATE)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCourtNbr", StandardBasicTypes.STRING)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("txtStagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.DATE)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("cdStagePersType", cdStagePersType)
				.setResultTransformer(Transformers.aliasToBean(PrincipalLegalStatusDto.class));
		principalLegalStatusDtoList = query.list();

		return principalLegalStatusDtoList;

	}

}
