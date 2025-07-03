package us.tx.state.dfps.service.admin.daoimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO Impls
 * for fetching event details Aug 5, 2017- 3:12:14 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class EventStagePersonLinkInsUpdDaoImpl implements EventStagePersonLinkInsUpdDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	@Value("${EventStagePersonLinkInsUpdDaoImpl.getEventDetailsThreeTableOne}")
	private transient String getEventDetailsThreeTableOne;

	@Autowired
	@Value("${EventStagePersonLinkInsUpdDaoImpl.getEventDetailsFourTable}")
	private transient String getEventDetailsFourTable;

	@Autowired
	@Value("${EventStagePersonLinkInsUpdDaoImpl.getEventCaseDetailsThreeTable}")
	private transient String getEventCaseDetailsThreeTable;

	@Autowired
	@Value("${EventStagePersonLinkInsUpdDaoImpl.getEventDetailsThreeTable}")
	private transient String getEventDetailsThreeTable;

	private static final Logger log = Logger.getLogger(EventStagePersonLinkInsUpdDaoImpl.class);

	public EventStagePersonLinkInsUpdDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getEventAndStatusDtls Method Description: This method will
	 * dynamically retrieves data from event and stage table.
	 * 
	 * @param pInputDataRec
	 * @return List<EventStagePersonLinkInsUpdOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventStagePersonLinkInsUpdOutDto> getEventAndStatusDtls(EventStagePersonLinkInsUpdInDto pInputDataRec) {
		boolean checkFourTable = false;
		String hostszDynamicSQL = null;
		String SQL_TASK_WHERE = ServiceConstants.SQL_TASK_WHERE + pInputDataRec.getCdTask();
		String SQL_ID_CASE_WHERE = ServiceConstants.SQL_ID_CASE_WHERE + pInputDataRec.getIdCase();
		String SQL_ID_EVENT_PERSON_STAFF_WHERE = ServiceConstants.SQL_ID_EVENT_PERSON_STAFF_WHERE
				+ pInputDataRec.getIdEventPerson();
		String SQL_ID_PERSON_CLIENT_WHERE = ServiceConstants.SQL_ID_PERSON_CLIENT_WHERE + pInputDataRec.getIdPerson();
		String SQL_ID_PERSON_CLIENT_WHERE_2 = ServiceConstants.SQL_ID_PERSON_CLIENT_WHERE_2
				+ pInputDataRec.getIdPerson();
		String SQL_ID_SITUATION_WHERE = ServiceConstants.SQL_ID_SITUATION_WHERE + pInputDataRec.getIdSituation();
		String SQL_ID_STAGE_WHERE = ServiceConstants.SQL_ID_STAGE_WHERE + pInputDataRec.getIdStage();
		String SQL_EVENT_TYPE_WHERE = ServiceConstants.SQL_EVENT_TYPE_WHERE + pInputDataRec.getCdEventType() + "'";
		DateFormat dateFormat = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_YY_MM_DD);
		String startDate = null;
		String endDate = null;
		if (pInputDataRec.getDtScrDtStartDt() != null) {
			startDate = dateFormat.format(pInputDataRec.getDtScrDtStartDt());
		}
		if (pInputDataRec.getDtScrDtEventEnd() != null) {
			endDate = dateFormat.format(pInputDataRec.getDtScrDtEventEnd());
		}
		String SQL_DATE_FROM_WHERE = ServiceConstants.SQL_DATE_FROM_WHERE + ServiceConstants.SQL_DATE_TO_WHERE
				+ ServiceConstants.EMPTY_CHAR + startDate + ServiceConstants.EMPTY_CHAR
				+ ServiceConstants.DATE_IN_STRING;
		String SQL_DATE_TO_WHERE = ServiceConstants.SQL_DATE_FROM_WHERELT + ServiceConstants.SQL_DATE_TO_WHERE
				+ ServiceConstants.EMPTY_CHAR + endDate + ServiceConstants.EMPTY_CHAR + ServiceConstants.DATE_IN_STRING;
		log.debug("Entering method EventStagePersonLinkInsUpdQUERYdam in EventStagePersonLinkInsUpdDaoImpl");
		/*
		 * if (pInputDataRec.getUlIdPerson() == 0 &&
		 * pInputDataRec.getUlIdStage() == 0 && pInputDataRec.getUlIdCase() ==
		 * 0) { }
		 */
		String getEventDetailsFourTable = this.getEventDetailsFourTable;
		switch (pInputDataRec.getCdReqFunction()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			hostszDynamicSQL = getEventDetailsFourTable;
			checkFourTable = true;
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			String getEventCaseDetailsThreeTable = this.getEventCaseDetailsThreeTable;
			String getEventDetailsThreeTable = this.getEventDetailsThreeTable;
			if (ServiceConstants.AOM_ACTION_ECS.equalsIgnoreCase(pInputDataRec.getAomActionEcs())) {
				hostszDynamicSQL = getEventCaseDetailsThreeTable;
				checkFourTable = false;
			} else {
				hostszDynamicSQL = getEventDetailsThreeTable;
				checkFourTable = false;
			}
			break;
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdTask())) {
			hostszDynamicSQL = AppendSQL(hostszDynamicSQL, SQL_TASK_WHERE, ServiceConstants.CONJUNCTION_AND);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdCase()) && pInputDataRec.getIdCase() != 0) {
			hostszDynamicSQL = AppendSQL(hostszDynamicSQL, SQL_ID_CASE_WHERE, ServiceConstants.CONJUNCTION_AND);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdEventPerson()) && pInputDataRec.getIdEventPerson() != 0) {
			hostszDynamicSQL = AppendSQL(hostszDynamicSQL, SQL_ID_EVENT_PERSON_STAFF_WHERE,
					ServiceConstants.CONJUNCTION_AND);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdPerson()) && pInputDataRec.getIdPerson() != 0) {
			hostszDynamicSQL = AppendSQL(hostszDynamicSQL, SQL_ID_PERSON_CLIENT_WHERE,
					ServiceConstants.CONJUNCTION_AND);
			hostszDynamicSQL = AppendSQL(hostszDynamicSQL, SQL_ID_PERSON_CLIENT_WHERE_2,
					ServiceConstants.CONJUNCTION_AND);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdSituation()) && pInputDataRec.getIdSituation() != 0) {
			hostszDynamicSQL = AppendSQL(hostszDynamicSQL, SQL_ID_SITUATION_WHERE, ServiceConstants.CONJUNCTION_AND);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdStage()) && pInputDataRec.getIdStage() != 0) {
			hostszDynamicSQL = AppendSQL(hostszDynamicSQL, SQL_ID_STAGE_WHERE, ServiceConstants.CONJUNCTION_AND);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdEventType())) {
			hostszDynamicSQL = AppendSQL(hostszDynamicSQL, SQL_EVENT_TYPE_WHERE, ServiceConstants.CONJUNCTION_AND);
		}
		if ((pInputDataRec.getDtScrDtStartDt() != null)) {
			hostszDynamicSQL = AppendSQL(hostszDynamicSQL, SQL_DATE_FROM_WHERE, ServiceConstants.CONJUNCTION_AND);
		}
		if ((pInputDataRec.getDtScrDtEventEnd() != null)) {
			hostszDynamicSQL = AppendSQL(hostszDynamicSQL, SQL_DATE_TO_WHERE, ServiceConstants.CONJUNCTION_AND);
		}
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(hostszDynamicSQL)
				.setResultTransformer(Transformers.aliasToBean(EventStagePersonLinkInsUpdOutDto.class)));
		sQLQuery1.addScalar("cdEventStatus", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdEventType", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStage", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtEventOccurred", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("idCase", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idEvent", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("nmStage", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("scrCaseWorker", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("eventDescr", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdTask", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtEventCreated", StandardBasicTypes.TIMESTAMP);
		if (checkFourTable) {
			sQLQuery1.addScalar("indCaseSensitive", StandardBasicTypes.STRING);
		}
		List<EventStagePersonLinkInsUpdOutDto> liCcmn87doDto = (List<EventStagePersonLinkInsUpdOutDto>) sQLQuery1
				.list();
		log.debug("Exiting method EventStagePersonLinkInsUpdQUERYdam in EventStagePersonLinkInsUpdDaoImpl");
		return liCcmn87doDto;
	}

	/**
	 * Method desc:AppendSQL
	 * 
	 * @param pszDynamicSQL
	 * @param pszSQLStatement
	 * @param cConjunction
	 * @return void @
	 */
	public String AppendSQL(String hostszDynamicSQL, String pszSQLStatement, String cConjunction) {
		log.debug("Entering method EventStagePersonLinkInsUpdQUERYdam in EventStagePersonLinkInsUpdDaoImpl");
		if (cConjunction.equals(ServiceConstants.CONJUNCTION_PAREN)) {
			hostszDynamicSQL = hostszDynamicSQL.concat(ServiceConstants.SQL_OPEN_PAREN_STATEMENT);
		} else if (cConjunction.equals(ServiceConstants.CONJUNCTION_OR)) {
			hostszDynamicSQL = hostszDynamicSQL.concat(ServiceConstants.SQL_OR_STATEMENT);
		} else if (cConjunction.equals(ServiceConstants.CONJUNCTION_AND)) {
			hostszDynamicSQL = hostszDynamicSQL.concat(ServiceConstants.SQL_AND_STATEMENT);
		}
		hostszDynamicSQL = hostszDynamicSQL + pszSQLStatement;
		log.debug("Exiting method EventStagePersonLinkInsUpdQUERYdam in EventStagePersonLinkInsUpdDaoImpl");
		return hostszDynamicSQL;
	}
}
