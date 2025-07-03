package us.tx.state.dfps.service.extreq.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.extreq.ExtreqDto;
import us.tx.state.dfps.service.extreq.dao.ExtreqDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CSVC27s
 * Extension Request Mar 15, 2018- 10:40:04 AM © 2017 Texas Department of Family
 * and Protective Services
 */

@Repository
public class ExtreqDaoImpl implements ExtreqDao {

	private static final Logger log = Logger.getLogger("ServiceBusiness-ExtreqDaoLog");

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ExtReqDaoImpl.getExtreqInfo}")
	private transient String getExtreqInfoSql;

	public ExtreqDaoImpl() {

	}

	/**
	 * Method Name: getExtreqInfo Method Description: Retrieves Approver
	 * information DAM CSEC23D
	 * 
	 * @return ExtreqDto
	 */
	@Override
	public ExtreqDto getExtreqInfo(Long idEvent) {

		List<ExtreqDto> extreqDtoList = new ArrayList<ExtreqDto>();

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getExtreqInfoSql)
				.addScalar("idApprovalEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idApproval", StandardBasicTypes.LONG)
				.addScalar("txtApprovalTopic", StandardBasicTypes.STRING)
				.addScalar("dtApprovalDate", StandardBasicTypes.DATE).addScalar("idApprovers", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idTodo", StandardBasicTypes.LONG)
				.addScalar("cdApproversStatus", StandardBasicTypes.STRING)
				.addScalar("dtApproversDetermination", StandardBasicTypes.DATE)
				.addScalar("dtApproversRequested", StandardBasicTypes.DATE)
				.addScalar("indApproversHistorical", StandardBasicTypes.STRING)
				.addScalar("txtApproversCmnts", StandardBasicTypes.STRING)
				.addScalar("cdEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("txtEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeFirst", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeMiddle", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeLast", StandardBasicTypes.STRING)
				.addScalar("cdEmployeeSuffix", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ExtreqDto.class));

		extreqDtoList = (List<ExtreqDto>) query.list();

		return ObjectUtils.isEmpty(extreqDtoList) ? null : extreqDtoList.get(ServiceConstants.Zero);
	}

}
