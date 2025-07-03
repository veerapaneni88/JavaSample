package us.tx.state.dfps.service.workload.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.UnitEmpLink;
import us.tx.state.dfps.common.domain.Workload;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AssignWorkloadReq;
import us.tx.state.dfps.service.workload.dao.UnitSummaryDao;
import us.tx.state.dfps.service.workload.dto.UnitSummaryDto;

@Repository
public class UnitSummaryDaoImpl implements UnitSummaryDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	SessionFactory sessionFactory;

	@Value("${UnitSummaryDaoImpl.unitValidity}")
	private String getUnitValitySql;

	@Value("${UnitSummaryDaoImpl.getUnitSummary}")
	private String getUnitSummarySql;

	@Value("${UnitSummaryDaoImpl.getInvSvcAssignments}")
	private String invSql;

	public UnitSummaryDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This method is designed to retrieve the ID UNIT, Unit
	 * Approver's ID PERSON and the Unit Approver's Unit Member Role given CD
	 * UNIT PROGRAM, CD UNIT REGION, and NBR UNIT.
	 * 
	 * @param assignWorkloadReq
	 * @return List<UnitSummaryDto> @ DAM Name: CCMN33D
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<UnitSummaryDto> unitValidity(AssignWorkloadReq assignWorkloadReq) {
		List<UnitSummaryDto> UnitList = (List<UnitSummaryDto>) sessionFactory.getCurrentSession()
				.createQuery(getUnitValitySql).setParameter("program", assignWorkloadReq.getSzCdUnitProgram())
				.setParameter("region", assignWorkloadReq.getSzCdUnitRegion())
				.setParameter("nbrUnit", assignWorkloadReq.getSzNbrUnit())
				.setParameter("external", assignWorkloadReq.getIndExternal())
				.setResultTransformer(Transformers.aliasToBean(UnitSummaryDto.class)).list();
		if (UnitList.isEmpty()) {
			return new ArrayList<UnitSummaryDto>();
		} else {
			return UnitList;
		}
	}

	/**
	 * 
	 * Method Description: This method is designed to compare the CD UNIT MEMBER
	 * ROLE of a unit member given an ID PERSON and an ID UNIT to two CD UNIT
	 * MEMBER ROLEs that it is given.
	 * 
	 * @param unitSummaryDto
	 * @return Long
	 */
	@Override
	public Long checkAcessForUnit(UnitSummaryDto unitSummaryDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UnitEmpLink.class, "unitEmp");
		Criterion rest1 = Restrictions.eq("unitEmp.idPerson", unitSummaryDto.getIdPerson());
		Criterion rest2 = Restrictions.eq("unitEmp.idUnit", unitSummaryDto.getIdUnit());
		criteria.add(rest1);
		criteria.add(rest2);
		Criterion rest3 = Restrictions.lt("unitEmp.cdUnitMemberRole", unitSummaryDto.getCdUnitMemberRole());
		Criterion rest4 = Restrictions.eq("unitEmp.cdUnitMemberRole", ServiceConstants.UNIT_MEMBER_ROLE_CLERK);
		Criterion orRestri = Restrictions.or(rest3, rest4);
		criteria.add(orRestri);
		criteria.setProjection(Projections.property("idPerson"));
		List<Long> listOfPersons = criteria.list();
		if (listOfPersons.isEmpty()) {
			return null;
		} else {
			return listOfPersons.get(0);
		}
	}

	/**
	 * 
	 * Method Description:This method is designed to retrieve all columns from
	 * the Unit Summary View given an ID UNIT
	 * 
	 * @param unitSummaryDto,assignWorkloadReq
	 * @return List<UnitSummaryDto> @ DAM Name: CCMN67D
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UnitSummaryDto> getUnitSummary(UnitSummaryDto unitSummaryDto, AssignWorkloadReq assignWorkloadReq) {
		StringBuilder str = new StringBuilder();
		str.append(getUnitSummarySql);
		if (unitSummaryDto.getOrderBy().equals(ServiceConstants.SORT_BY_NAME)) {
			str.append("  ORDER BY employee.nmEmployeeLast  ");
		} else if (unitSummaryDto.getOrderBy().equals(ServiceConstants.SORT_BY_POSITION)) {
			str.append(" ORDER BY cdEmpBjnEmp  ");
		} else if (unitSummaryDto.getOrderBy().equals(ServiceConstants.SORT_BY_IN_UNIT)) {
			str.append("  ORDER BY unitEmpLink.cdUnitMemberInOut");
		}
		List<UnitSummaryDto> UnitList = (List<UnitSummaryDto>) sessionFactory.getCurrentSession()
				.createQuery(str.toString()).setParameter("idUnit", unitSummaryDto.getIdUnit())
				.setResultTransformer(Transformers.aliasToBean(UnitSummaryDto.class)).list();
		if (UnitList.isEmpty()) {
			return new ArrayList<UnitSummaryDto>();
		} else {
			for (UnitSummaryDto dto : UnitList) {
				dto.setProgram(assignWorkloadReq.getSzCdUnitProgram());
				dto.setRegion(assignWorkloadReq.getSzCdUnitRegion());
				dto.setExternal(assignWorkloadReq.getIndExternal());
				dto.setUnit(assignWorkloadReq.getSzNbrUnit());
			}
			return UnitList;
		}
	}

	/**
	 * 
	 * Method Description:This method is called by the Unit Summary Search
	 * service, this method retrieves the total assaigments, Total Primary
	 * stages for the given PERSON ID
	 * 
	 * @param idPerson,
	 *            flag
	 * @return Long @ DAM Name: CSEC79D, CSEC80D
	 */
	@Override
	public Long getTotalAssignments(Long idPerson, String flag) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Workload.class, "workload");
		criteria.add(Restrictions.eq("workload.id.idWkldPerson", idPerson));
		if (flag.equals(ServiceConstants.TPA))
			criteria.add(Restrictions.eq("workload.id.cdWkldStagePersRole", ServiceConstants.PR));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.count("workload.id.idWkldStage"));
		criteria.setProjection(projList);
		Long cnt = (Long) criteria.uniqueResult();
		return cnt;
	}

	/**
	 * 
	 * Method Description:This method will retrieve the total number of
	 * investigations assigned to the workload that are less than 60 days old
	 * and greater than 30 days.
	 * 
	 * @param idPerson,
	 *            flag
	 * @return Long @ DAM Name: CSECC3D, CSECC4D
	 */
	@Override
	public Long getInvSvcAssignmentsTa(Long idPerson, String flag) {
		StringBuilder str = new StringBuilder();
		str.append(invSql);
		if (flag.equals(ServiceConstants.TA_30)) {
			str.append(" and s.cdStage='INV'  ");
			str.append(" and SYSDATE-s.dtStageStart > 30 ");
		} else if (flag.equals(ServiceConstants.TA_60)) {
			str.append(" and s.cdStage='SVC'  ");
			str.append(" and s.dtStageStart < (SYSDATE - 60) ");
		}
		Long cnt = (Long) sessionFactory.getCurrentSession().createQuery(str.toString())
				.setParameter("idPerson", idPerson).uniqueResult();
		return cnt;
	}

	/**
	 * 
	 * Method Description:This method will retrieve all PRIMARY aps
	 * investigation assignments that are over 30 days old and 60 days old.
	 * 
	 * @param idPerson,
	 *            flag
	 * @return Long @ DAM Name: CSECC5D, CSECC6D
	 */
	@Override
	public Long getInvSvcAssignmentsTpa(Long idPerson, String flag) {
		StringBuilder str = new StringBuilder();
		str.append(invSql);
		str.append(" and w.id.cdWkldStagePersRole='PR' ");
		if (flag.equals(ServiceConstants.TA_30)) {
			str.append(" and s.cdStage='INV'  ");
			str.append(" AND s.dtStageStart < (SYSDATE - 30) ");
		} else if (flag.equals(ServiceConstants.TA_60)) {
			str.append(" and s.cdStage='SVC'  ");
			str.append(" AND s.dtStageStart < (SYSDATE - 60) ");
		}
		Long cnt = (Long) sessionFactory.getCurrentSession().createQuery(str.toString())
				.setParameter("idPerson", idPerson).uniqueResult();
		return cnt;
	}
}
