package us.tx.state.dfps.service.workload.daoimpl;

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

import us.tx.state.dfps.service.workload.dao.ApprovalRejectionDao;
import us.tx.state.dfps.service.workload.dto.ApprovalRejectionPersonDto;
import us.tx.state.dfps.service.workload.dto.RejectApprovalDto;

@Repository
public class ApprovalRejectionDaoImpl implements ApprovalRejectionDao {

	@Value("${ApprovalRejectionPersonDaoImpl.getApprovalRejectionDtls}")
	private String getApprovalRejectionDtls;

	@Value("${ApprovalRejectionPersonDaoImpl.saveApprovalRejectionDtls}")
	private String saveApprovalRejectionDtlsSql;

	@Autowired
	private SessionFactory sessionFactory;

	public ApprovalRejectionDaoImpl() {

	}

	/**
	 * 
	 * Method Description:CCMNI3D dam that retrieves all row from the
	 * APPROVAL_REJECTION table for any given stage Dam Name: CCMNI3D
	 * 
	 * @param idCase
	 * @param idStage
	 * @return List<ApprovalRejectionPersonSearchDto> @ @
	 */
	@SuppressWarnings("unchecked")
	public List<ApprovalRejectionPersonDto> approvalRejectionPersonSearch(Long idCase, Long idStage) {
		List<ApprovalRejectionPersonDto> list = new ArrayList<ApprovalRejectionPersonDto>();

		list = (List<ApprovalRejectionPersonDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getApprovalRejectionDtls).setParameter("idCase", idCase)
				.setParameter("idStage", idStage)).addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.addScalar("idApprovalRejection", StandardBasicTypes.LONG)
						.addScalar("idRejector", StandardBasicTypes.LONG)
						.addScalar("dtRejection", StandardBasicTypes.TIMESTAMP)
						.addScalar("indApsEffort", StandardBasicTypes.STRING)
						.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
						.addScalar("indProblems", StandardBasicTypes.STRING)
						.addScalar("indEvidence", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidRptr", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidAp", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidMp", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidCol", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidPhotos", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidDe", StandardBasicTypes.STRING)
						.addScalar("indMissingEvidOth", StandardBasicTypes.STRING)
						.addScalar("indDiscretionary", StandardBasicTypes.STRING)
						.addScalar("approversCmnts", StandardBasicTypes.STRING)
						.addScalar("indCpsCollaterals", StandardBasicTypes.STRING)
						.addScalar("indCpsServices", StandardBasicTypes.STRING)
						.addScalar("indCpsPolicy", StandardBasicTypes.STRING)
						.addScalar("indCpsInterviews", StandardBasicTypes.STRING)
						.addScalar("indCpsCriminalHistory", StandardBasicTypes.STRING)
						.addScalar("indCpsFactors", StandardBasicTypes.STRING)
						.addScalar("indCpsHistory", StandardBasicTypes.STRING)
						.addScalar("indCpsPreviousInv", StandardBasicTypes.STRING)
						.addScalar("indCpsAllegations", StandardBasicTypes.STRING)
						.addScalar("indCpsDrugTesting", StandardBasicTypes.STRING)
						.addScalar("indCpsDisposition", StandardBasicTypes.STRING)
						.addScalar("indCpsRiskAssessment", StandardBasicTypes.STRING)
						.addScalar("indCpsSearch", StandardBasicTypes.STRING)
						.addScalar("indCpsLawEnforcement", StandardBasicTypes.STRING)
						.addScalar("indCpsHomeVisit", StandardBasicTypes.STRING)
						.addScalar("indCpsAddServices", StandardBasicTypes.STRING)
						.addScalar("indCpsLegalAction", StandardBasicTypes.STRING)
						.addScalar("indCpsRoles", StandardBasicTypes.STRING)
						.addScalar("indCpsPrincipals", StandardBasicTypes.STRING)
						.addScalar("indCpsOther", StandardBasicTypes.STRING)
						.addScalar("cdOvrllDisptn", StandardBasicTypes.STRING)
						.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
						.addScalar("dtDeterminationRecorded", StandardBasicTypes.TIMESTAMP)
						.addScalar("cdJobClass", StandardBasicTypes.STRING)
						.addScalar("indEmpConfirmedHrmis", StandardBasicTypes.STRING)
						.addScalar("indAfcInterviews", StandardBasicTypes.STRING)
						.addScalar("indAfcFollowUp", StandardBasicTypes.STRING)
						.addScalar("indAfcEvidence", StandardBasicTypes.STRING)
						.addScalar("indAfcIncomplete", StandardBasicTypes.STRING)
						.addScalar("indAfcInconsistent", StandardBasicTypes.STRING)
						.addScalar("indAfcNotSupported", StandardBasicTypes.STRING)
						.addScalar("indAfcNotSummarized", StandardBasicTypes.STRING)
						.addScalar("indAfcConcerns", StandardBasicTypes.STRING)
						.addScalar("indAfcOther", StandardBasicTypes.STRING)
						.addScalar("indCclIncomplete", StandardBasicTypes.STRING)
						.addScalar("indCclCollaterals", StandardBasicTypes.STRING)
						.addScalar("indCclInterviews", StandardBasicTypes.STRING)
						.addScalar("indCclCitations", StandardBasicTypes.STRING)
						.addScalar("indCclExternal", StandardBasicTypes.STRING)
						.addScalar("indCclAbuse", StandardBasicTypes.STRING)
						.addScalar("indCclAllegations", StandardBasicTypes.STRING)
						.addScalar("indCclEvidence", StandardBasicTypes.STRING)
						.addScalar("indInCompleteCCLRejection", StandardBasicTypes.STRING)
						.addScalar("indCclPersonList", StandardBasicTypes.STRING)
						.addScalar("indCclOther", StandardBasicTypes.STRING)
						.addScalar("indApsRora", StandardBasicTypes.STRING)
						.addScalar("indApsIcs", StandardBasicTypes.STRING)
						.addScalar("indApsRootCause", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(ApprovalRejectionPersonDto.class)).list();

		return list;
	}

	/**
	 * Method Name:saveRejectionApproval Method Description:This Method is used
	 * to save the InComplete CCL Investigation Checkbox for CCL Program.
	 * 
	 * @param rejectApprovalDto
	 * @return
	 */
	@Override
	public void saveRejectionApproval(RejectApprovalDto rejectApprovalDto) {
		Query query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(saveApprovalRejectionDtlsSql)
				.setParameter("idCase", rejectApprovalDto.getIdCase())
				.setParameter("idStage", rejectApprovalDto.getIdStage())).setParameter("indInCompleteCCLRejection",
						rejectApprovalDto.getIndInCompleteCCLRejection());
		query.executeUpdate();
	}

}
