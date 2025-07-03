package us.tx.state.dfps.service.placement.daoimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.placement.dao.ChildPlanPlacementDao;
import us.tx.state.dfps.service.placement.dto.ApprovalInfoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: to retrieve
 * the plan placement information Jan 29, 2018- 5:39:27 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ChildPlanPlacementDaoImpl implements ChildPlanPlacementDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ChildPlanPlacementDaoImpl.getApprovalInfo}")
	private String getApprovalInfoSql;

	@Value("${ChildPlanPlacementDaoImpl.getDecodeValue}")
	private String getDecodeValueSql;

	private static final Logger LOG = Logger.getLogger(ChildPlanPlacementDaoImpl.class);

	/**
	 * 
	 * Method Name: getApprovalInfo (DAM Name : CLSC42D) Method Description:This
	 * DAM retrieves all of the approver and approval data for given eventId
	 * 
	 * @param idEvent
	 * @return approvalInfo
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ApprovalInfoDto getApprovalInfo(Long idEvent) {
		SimpleDateFormat format = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_ddMMYYYY);
		Date maxDate = null;
		List<ApprovalInfoDto> approvalInfoLst = new ArrayList<ApprovalInfoDto>();
		ApprovalInfoDto approvalInfoDto = new ApprovalInfoDto();
		try {
			maxDate = format.parse(ServiceConstants.MAX_JAVA_DATE);
		} catch (ParseException e) {

			LOG.error(e.getMessage());
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(maxDate);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getApprovalInfoSql)
				.addScalar("idApprovers", StandardBasicTypes.LONG)
				.addScalar("dtApproverLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idApproval", StandardBasicTypes.LONG)
				.addScalar("idTodo", StandardBasicTypes.LONG).addScalar("cdApproversStatus", StandardBasicTypes.STRING)
				.addScalar("dtApproversDetermination", StandardBasicTypes.DATE)
				.addScalar("dtApproversRequested", StandardBasicTypes.DATE)
				.addScalar("indApproversHistorical", StandardBasicTypes.STRING)
				.addScalar("approversCmnts", StandardBasicTypes.STRING)
				.addScalar("idApprovalEvent", StandardBasicTypes.LONG)
				.addScalar("dtApprovalLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idName", StandardBasicTypes.LONG)
				.addScalar("dtNameLastUpdate", StandardBasicTypes.DATE)
				.addScalar("indNameInvalid", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("indNamePrimary", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.addScalar("dtNameStartDate", StandardBasicTypes.DATE)
				.addScalar("dtNameEndDate", StandardBasicTypes.DATE).setParameter("idEvent", idEvent)
				.setParameter("idApprovalStatus", ServiceConstants.STATUS_APPROVED)
				// .setParameter("maxDate",cal.getTime())
				.setParameter("indNamePrimary", ServiceConstants.Character_IND_Y)
				.setParameter("indNameInvalid", ServiceConstants.Character_IND_N)
				.setResultTransformer(Transformers.aliasToBean(ApprovalInfoDto.class));
		try {
			//Modified the check the null condition for warranty defect 12045
			approvalInfoLst =  query.list();
			if(!ObjectUtils.isEmpty(approvalInfoLst)){
				approvalInfoDto = approvalInfoLst.get(ServiceConstants.Zero_INT);
			}
		} catch (DataNotFoundException | DataLayerException e) {
			LOG.error("Approver data not found");
		}
		return approvalInfoDto;

	}

	/**
	 * 
	 * Method Name: getApprovalInfo (DAM Name : CSEC88D) Method Description:This
	 * DAM retrieves decode value for given linktableCode, codestablesCode
	 * 
	 * @param codeType
	 * @param linktableCode
	 * @param codestablesCode
	 * @return approvalInfo
	 */
	@Override
	public String getDecodeValue(String codeType, String linktableCode, String codestablesCode) {

		String decode = (String) sessionFactory.getCurrentSession().createSQLQuery(getDecodeValueSql)
				.addScalar("decode", StandardBasicTypes.STRING).setParameter("codeType", codeType)
				.setParameter("linktableCode", linktableCode).setParameter("codestablesCode", codestablesCode)
				.uniqueResult();
		return decode;
	}

}
