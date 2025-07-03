package us.tx.state.dfps.service.workload.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.workload.dao.AssignDao;
import us.tx.state.dfps.service.workload.dto.AssignmentGroupDto;
import us.tx.state.dfps.service.workload.dto.AvailStaffGroupDto;
import us.tx.state.dfps.service.workload.dto.OnCallDto;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Tuxedo
 * DAM Name :CCMN27D, CCMN28D, CCMN29D, CCMN79D, CDYN03D Class Description:
 * Assign Dao Impl Class May 1, 2017 - 10:52:30 PM
 */

@Repository
public class AssignDaoImpl implements AssignDao {

	@Value("${Assign.getAvailStaffInfo}")
	private String getAvailStaffInfoSql;

	@Value("${Assign.getAssignmentGroup}")
	private String getAssignmentGroupSql;

	@Value("${Assign.onCallAvailStaff}")
	private String getOnCallAvailStaffSql;

	@Value("${Assign.getOnCallDtls}")
	private String getOnCallDtlsSql;

	@Value("${Assign.indCSSReviewContact}")
	private String getIndCSSReviewContactSql;

	@Autowired
	private SessionFactory sessionFactory;

	public AssignDaoImpl() {

	}

	// ccmn27d
	/**
	 * 
	 * Method Description: This method is used to retrieve Available Staff based
	 * on idUnit passed to DAM. Tuxedo Service Name:CCMN80S Tuxedo DAM Name:
	 * CCMN27D
	 * 
	 * @param idUnit
	 * @return availStaff @
	 */
	@SuppressWarnings("unchecked")
	public List<AvailStaffGroupDto> getAvailStaffInfo(Long idUnit) {

		List<AvailStaffGroupDto> availStaff = null;

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getAvailStaffInfoSql)
				.addScalar("unit", StandardBasicTypes.STRING).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("bjnJob", StandardBasicTypes.STRING)
				.addScalar("dtEmpLastAssigned", StandardBasicTypes.TIMESTAMP)
				.addScalar("phone", StandardBasicTypes.STRING).addScalar("phoneExtension", StandardBasicTypes.STRING)
				.addScalar("nmOfficeName", StandardBasicTypes.STRING).addScalar("idUnit", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("recipientEmailAddress", StandardBasicTypes.STRING)
				//Added to include External Organization in Assign page PPM#45615
				.addScalar("externalOrg", StandardBasicTypes.STRING)
				.setParameter("idUnit", idUnit)
				.setResultTransformer(Transformers.aliasToBean(AvailStaffGroupDto.class));

		availStaff = (List<AvailStaffGroupDto>) query.list();

		return availStaff;
	}

	// ccmn29d
	/**
	 * 
	 * Method Description: Tuxedo Service Name:CCMN80S Tuxedo DAM Name :CCMN29D
	 * 
	 * @param idStage
	 * @return assignmentGroup @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AssignmentGroupDto> getAssignmentgroup(Long idStage) {
		List<AssignmentGroupDto> assignmentGroup = null;

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getAssignmentGroupSql)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("cdStageCnty", StandardBasicTypes.STRING)
				.addScalar("nmStage", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("idStagePerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("dtMultiRef", StandardBasicTypes.TIMESTAMP).addScalar("idUnit", StandardBasicTypes.LONG)
				.addScalar("workerEmailAddres", StandardBasicTypes.STRING)
				//Added to include External Organization in Assign page PPM#45615
				.addScalar("externalOrg", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(AssignmentGroupDto.class));

		assignmentGroup = (List<AssignmentGroupDto>) query.list();

		return assignmentGroup;
	}

	// ccmn79d
	/**
	 * 
	 * Method Description: Tuxedo Service Name:CCMN80S Tuxedo DAM Name : CCMN79D
	 * 
	 * @param onCallProgram
	 * @param onCallCounty
	 * @param dtOnCallStart
	 * @return onCallDto @
	 */
	@Override
	public OnCallDto getOnCallId(String onCallProgram, String onCallCounty, Date dtOnCallStart) {
		OnCallDto onCallDto = new OnCallDto();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getOnCallDtlsSql)
				.addScalar("cdRegion", StandardBasicTypes.STRING).addScalar("cdOnCallCounty", StandardBasicTypes.STRING)
				.addScalar("cdOnCallProgram", StandardBasicTypes.STRING)
				.addScalar("cdOnCallType", StandardBasicTypes.STRING)
				.addScalar("dtOnCallStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtOnCallEnd", StandardBasicTypes.TIMESTAMP).addScalar("idOnCall", StandardBasicTypes.LONG)
				.setParameter("cdOnCallProgram", onCallProgram).setParameter("cdOnCallCounty", onCallCounty)
				.setParameter("dtOnCallStart", dtOnCallStart)
				.setResultTransformer(Transformers.aliasToBean(OnCallDto.class));

		onCallDto = (OnCallDto) query.uniqueResult();

		return onCallDto;
	}

	// CDYN03D
	/**
	 * 
	 * Method Description: Tuxedo Service Name: CCMN80S Tuxedo DAM Name :
	 * CDYN03D
	 * 
	 * @param idStage
	 * @return indCSSReviewContact @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean getIndCSSReviewContact(Long idStage) {
		Boolean indCSSReviewContact = Boolean.FALSE;

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getIndCSSReviewContactSql)
				.setParameter("idStage", idStage).setParameter("cssReviewFull", ServiceConstants.CSS_REVIEW_FULL)
				.setParameter("cssReviewOther", ServiceConstants.CSS_REVIEW_OTHER)
				.setParameter("cssReviewScreened", ServiceConstants.CSS_REVIEW_SCREENED);

		List<Query> contact = query.list();

		indCSSReviewContact = (contact.size() > 0) ? true : false;

		return indCSSReviewContact;
	}

	/**
	 * 
	 * Method Description: Tuxedo Service Name:CCMN80S Tuxedo DAM Name :CCMN28D
	 * 
	 * @param idOnCall
	 * @return onCallavailStaff @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AvailStaffGroupDto> getOnCallEmp(Long idOnCall) {
		List<AvailStaffGroupDto> onCallavailStaff = null;

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getOnCallAvailStaffSql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("bjnJob", StandardBasicTypes.STRING)
				.addScalar("dtEmpLastAssigned", StandardBasicTypes.TIMESTAMP)
				.addScalar("phone", StandardBasicTypes.STRING).addScalar("phoneExtension", StandardBasicTypes.STRING)
				.addScalar("nmOfficeName", StandardBasicTypes.STRING)
				.addScalar("cdEmpOnCallDesig", StandardBasicTypes.STRING)
				.addScalar("empOnCallPhone1", StandardBasicTypes.STRING)
				.addScalar("empOnCallExt1", StandardBasicTypes.STRING)
				.addScalar("empOnCallPhone2", StandardBasicTypes.STRING)
				.addScalar("empOnCallExt2", StandardBasicTypes.STRING)
				.addScalar("empOnCallCntctOrd", StandardBasicTypes.LONG).addScalar("idUnit", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idOnCall", idOnCall)
				.setParameter("endDate", ServiceConstants.GENERIC_END_DATE)
				.setResultTransformer(Transformers.aliasToBean(AvailStaffGroupDto.class));

		onCallavailStaff = (List<AvailStaffGroupDto>) query.list();

		return onCallavailStaff;
	}
}
