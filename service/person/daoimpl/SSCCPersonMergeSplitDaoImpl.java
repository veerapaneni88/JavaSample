package us.tx.state.dfps.service.person.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.SsccPlcmtRsrcLinkMc;
import us.tx.state.dfps.service.person.dao.SSCCPersonMergeSplitDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO
 * Implement class for SSCCPersonMergeSplit May 31, 2018- 11:21:56 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class SSCCPersonMergeSplitDaoImpl implements SSCCPersonMergeSplitDao {

	@Value("${SSCCPersonMergeSplitDaoImpl.updateSSCCPlcmtRsrcLinkMC.id}")
	private String updateSSCCPlcmtRsrcLinkMCDupId;

	@Value("${SSCCPersonMergeSplitDaoImpl.updateSSCCPlcmtRsrcLinkMC.endDate}")
	private String updateSSCCPlcmtRsrcLinkMCEndDate;

	@Value("${SSCCPersonMergeSplitDaoImpl.updateSSCCPlcmtRsrcLinkMC.idMedConPerson}")
	private String updateSSCCPlcmtRsrcLinkMCIdMedConPerson;

	@Value("${SSCCPersonMergeSplitDaoImpl.fetchRfrlForMedCon}")
	private String fetchRefferalMedCondender;

	@Value("${SSCCPersonMergeSplitDaoImpl.fetchRfrlForPersonStg}")
	private String fetchRefferalOpenPerson;

	@Value("${SSCCPersonMergeSplitDaoImpl.updatePersonOnSSCCRefFamily}")
	private String updatePersonOnSSCCRefFamilySql;

	@Value("${SSCCPersonMergeSplitDaoImpl.updatePersonOnSSCCPlcmtCircumstance}")
	private String updatePersonOnSSCCPlcmtCircumstanceSql;

	@Value("${SSCCPersonMergeSplitDaoImpl.updatePersonOnSSCCChildPlanParticipant}")
	private String updatePersonOnSSCCChildPlanParticipantSql;

	@Value("${SSCCPersonMergeSplitDaoImpl.updatePersonOnSSCCPlcmtMedConsenter}")
	private String updatePersonOnSSCCPlcmtMedConsenterSql;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: updatePersonOnSSCCPlcmtRsrcLinkMC Method Description:update
	 * Person On SSCCPlcmtRsrcLinkMC
	 * 
	 * @param idClosedPerson
	 * @param idFwdPerson
	 */
	@Override
	public void updatePersonOnSSCCPlcmtRsrcLinkMC(int idClosedPerson, int idFwdPerson) {
		List<SsccPlcmtRsrcLinkMc> ssccPlcmtRsrcLinkMcs = new ArrayList();
		ssccPlcmtRsrcLinkMcs = (List<SsccPlcmtRsrcLinkMc>) sessionFactory.getCurrentSession()
				.createSQLQuery(updateSSCCPlcmtRsrcLinkMCDupId).addScalar("idRsrcSscc", StandardBasicTypes.LONG)
				.setParameter("idForwardPerson", idFwdPerson).setParameter("idClosePerson", idClosedPerson).list();

		if (CollectionUtils.isNotEmpty(ssccPlcmtRsrcLinkMcs)) {
			for (SsccPlcmtRsrcLinkMc ssccPlcmtRsrcLinkMc : ssccPlcmtRsrcLinkMcs) {
				sessionFactory.getCurrentSession().createSQLQuery(updateSSCCPlcmtRsrcLinkMCEndDate)
						.setParameter("idRsrcSscc", ssccPlcmtRsrcLinkMc.getIdRsrcSscc())
						.setParameter("idRsrcMember", idFwdPerson).setParameter("idMedConsenterPerson", idFwdPerson)
						.executeUpdate();
			}

		}

		sessionFactory.getCurrentSession().createSQLQuery(updateSSCCPlcmtRsrcLinkMCIdMedConPerson)
				.setParameter("idForwardPerson", idFwdPerson).setParameter("idClosePerson", idFwdPerson)
				.executeUpdate();

	}

	/**
	 * 
	 * Method Name: fetchSSCCReferralsForPersonInOpenStages Method
	 * Description:fetch SSCCReferrals For Person In Open Stages
	 * 
	 * @param idClosedPerson
	 * @param forMedicalConsenter
	 * @return
	 */
	@Override
	public List<BigDecimal> fetchSSCCReferralsForPersonInOpenStages(int idClosedPerson, boolean forMedicalConsenter) {
		List<BigDecimal> ssccRefferalList = new ArrayList<>();
		if (forMedicalConsenter) {
			ssccRefferalList = (List<BigDecimal>) sessionFactory.getCurrentSession().createSQLQuery(fetchRefferalMedCondender)
					.setParameter("idMedConsenterPerson", idClosedPerson).list();
		} else {
			ssccRefferalList = (List<BigDecimal>) sessionFactory.getCurrentSession().createSQLQuery(fetchRefferalOpenPerson)
					.setParameter("idPerson", idClosedPerson).list();
		}		
		return ssccRefferalList;
	}

	/**
	 * 
	 * Method Name: updatePersonOnSSCCRefFamily Method Description:update Person
	 * On SSCCRefFamily
	 * 
	 * @param idSsccReferral
	 * @param idClosedPerson
	 * @param idFwdPerson
	 */
	@Override
	public void updatePersonOnSSCCRefFamily(int idSsccReferral, int idClosedPerson, int idFwdPerson) {
		sessionFactory.getCurrentSession().createSQLQuery(updatePersonOnSSCCRefFamilySql)
				.setParameter("idForwardPerson", idFwdPerson).setParameter("idSsccReferral", idSsccReferral)
				.setParameter("idClosePerson", idClosedPerson).executeUpdate();
	}

	/**
	 * 
	 * Method Name: updatePersonOnSSCCPlcmtCircumstance Method
	 * Description:update Person On SSCCPlcmtCircumstance
	 * 
	 * @param idSsccReferral
	 * @param idClosedPerson
	 * @param idFwdPerson
	 */
	@Override
	public void updatePersonOnSSCCPlcmtCircumstance(int idSsccReferral, int idClosedPerson, int idFwdPerson) {
		sessionFactory.getCurrentSession().createSQLQuery(updatePersonOnSSCCPlcmtCircumstanceSql)
				.setParameter("idForwardPerson", idFwdPerson).setParameter("idSsccReferral", idSsccReferral)
				.setParameter("idClosePerson", idClosedPerson).executeUpdate();
	}

	/**
	 * 
	 * Method Name: updatePersonOnSSCCChildPlanParticipant Method
	 * Description:update Person On SSCCChildPlanParticipant
	 * 
	 * @param idSsccReferral
	 * @param idClosedPerson
	 * @param idFwdPerson
	 */
	@Override
	public void updatePersonOnSSCCChildPlanParticipant(int idSsccReferral, int idClosedPerson, int idFwdPerson) {
		sessionFactory.getCurrentSession().createSQLQuery(updatePersonOnSSCCChildPlanParticipantSql)
				.setParameter("idForwardPerson", idFwdPerson).setParameter("idSsccReferral", idSsccReferral)
				.setParameter("idClosePerson", idClosedPerson).executeUpdate();
	}

	/**
	 * 
	 * Method Name: updatePersonOnSSCCPlcmtMedConsenter Method
	 * Description:update Person On SSCCPlcmtMedConsenter
	 * 
	 * @param idSsccReferral
	 * @param idClosedPerson
	 * @param idFwdPerson
	 */
	@Override
	public void updatePersonOnSSCCPlcmtMedConsenter(int idSsccReferral, int idClosedPerson, int idFwdPerson) {
		sessionFactory.getCurrentSession().createSQLQuery(updatePersonOnSSCCPlcmtMedConsenterSql)
				.setParameter("idForwardPerson", idFwdPerson).setParameter("idSsccReferral", idSsccReferral)
				.setParameter("idClosePerson", idClosedPerson).executeUpdate();
	}

}
