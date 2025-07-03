package us.tx.state.dfps.service.admin.daoimpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.EventStPerLnkEmpMerRletChkCountDao;
import us.tx.state.dfps.service.admin.dto.EventStPerLnkEmpMerRletChkCountInDto;
import us.tx.state.dfps.service.admin.dto.EventStPerLnkEmpMerRletChkCountOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This DAO
 * takes a ID PERSON and ID STAGE and determines if a person is involved in any
 * stages besides the given stage or if the person is involved in any events. If
 * the person is not linked to any other events/stages, a true flag is returned.
 * Otherwise, if a person is involved in other stages and/or events, a false
 * flag is returned. This flag represents whether or not a person can be deleted
 * from the database. If the flag is set to true, a stored procedure is invoked
 * that deletes the person from the appropriate tables. Aug 10, 2017- 12:26:09
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class EventStPerLnkEmpMerRletChkCountDaoImpl implements EventStPerLnkEmpMerRletChkCountDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EventStPerLnkEmpMerRletChkCountDaoImpl.getStagePersLinkCount}")
	private String getStagePersLinkCount;

	@Value("${EventStPerLnkEmpMerRletChkCountDaoImpl.getEventCount}")
	private String getEventCount;

	@Value("${EventStPerLnkEmpMerRletChkCountDaoImpl.getEmployeeCount}")
	private String getEmployeeCount;

	@Value("${EventStPerLnkEmpMerRletChkCountDaoImpl.getMergeCount}")
	private String getMergeCount;

	@Value("${EventStPerLnkEmpMerRletChkCountDaoImpl.getTletsCount}")
	private String getTletsCount;

	@Value("${EventStPerLnkEmpMerRletChkCountDaoImpl.deletePerson}")
	private String deletePerson;

	@Value("${EventStPerLnkEmpMerRletChkCountDaoImpl.deleteIntakePerson}")
	private String deleteIntakePerson;

	private static final Logger log = Logger.getLogger(EventStPerLnkEmpMerRletChkCountDaoImpl.class);

	public EventStPerLnkEmpMerRletChkCountDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: deletePerson Method Description: Delete person record if
	 * there is no child cascading exists
	 * 
	 * @param eventStPerLnkEmpMerRletChkCountInDto
	 * @return EventStPerLnkEmpMerRletChkCountOutDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EventStPerLnkEmpMerRletChkCountOutDto deletePerson(
			EventStPerLnkEmpMerRletChkCountInDto eventStPerLnkEmpMerRletChkCountInDto) {
		log.debug("Entering method deletePerson in EventStPerLnkEmpMerRletChkCountDaoImpl");
		long ulLinkCount = 0;
		EventStPerLnkEmpMerRletChkCountOutDto pOutputDataRec = new EventStPerLnkEmpMerRletChkCountOutDto();
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStagePersLinkCount)
				.addScalar("stagePersLinkCount", StandardBasicTypes.LONG)
				.setParameter("hI_ulIdPerson", eventStPerLnkEmpMerRletChkCountInDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(EventStPerLnkEmpMerRletChkCountOutDto.class)));
		EventStPerLnkEmpMerRletChkCountOutDto liCint60doDto_StagePersLinkCount = (EventStPerLnkEmpMerRletChkCountOutDto) sQLQuery1
				.uniqueResult();
		long hO_ulStagePersLinkCount = liCint60doDto_StagePersLinkCount.getStagePersLinkCount();
		SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEventCount)
				.addScalar("eventCount", StandardBasicTypes.LONG)
				.setParameter("hI_ulIdPerson", eventStPerLnkEmpMerRletChkCountInDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(EventStPerLnkEmpMerRletChkCountOutDto.class)));
		List<EventStPerLnkEmpMerRletChkCountOutDto> liCint60doDtoEventCount = (List<EventStPerLnkEmpMerRletChkCountOutDto>) sQLQuery2
				.list();
		long hO_ulEventCount = liCint60doDtoEventCount.get(0).getEventCount();
		SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEmployeeCount)
				.addScalar("employeeCount", StandardBasicTypes.LONG)
				.setParameter("hI_ulIdPerson", eventStPerLnkEmpMerRletChkCountInDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(EventStPerLnkEmpMerRletChkCountOutDto.class)));
		List<EventStPerLnkEmpMerRletChkCountOutDto> liCint60doDto_EmployeeCount = (List<EventStPerLnkEmpMerRletChkCountOutDto>) sQLQuery3
				.list();
		long hO_ulEmployeeCount = liCint60doDto_EmployeeCount.get(0).getEmployeeCount();
		SQLQuery sQLQuery4 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getMergeCount)
				.addScalar("mergeCount", StandardBasicTypes.LONG)
				.setParameter("hI_ulIdPerson", eventStPerLnkEmpMerRletChkCountInDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(EventStPerLnkEmpMerRletChkCountOutDto.class)));
		List<EventStPerLnkEmpMerRletChkCountOutDto> liCint60doDto_MergeCount = (List<EventStPerLnkEmpMerRletChkCountOutDto>) sQLQuery4
				.list();
		long hO_ulMergeCount = liCint60doDto_MergeCount.get(0).getMergeCount();
		SQLQuery sQLQuery5 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getTletsCount)
				.addScalar("tletsCount", StandardBasicTypes.LONG)
				.setParameter("hI_ulIdPerson", eventStPerLnkEmpMerRletChkCountInDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(EventStPerLnkEmpMerRletChkCountOutDto.class)));
		List<EventStPerLnkEmpMerRletChkCountOutDto> liCint60doDto_TletsCount = (List<EventStPerLnkEmpMerRletChkCountOutDto>) sQLQuery5
				.list();
		long hO_ulTletsCount = liCint60doDto_TletsCount.get(0).getTletsCount();
		/*
		 ** If a count was returned, add up both the event, stage, and employee
		 ** links count to determine if the person is involved in any other
		 * stages (besides intake) and events or is an employee. If the link
		 ** count is 0, then we know the person is not involved in anything else.
		 ** Otherwise, the person has been linked to something else and cannot be
		 * deleted from the database.
		 */
		ulLinkCount = hO_ulStagePersLinkCount + hO_ulEventCount + hO_ulEmployeeCount + hO_ulMergeCount
				+ hO_ulTletsCount;
		if (ulLinkCount > 1) {
			pOutputDataRec.setSysIndGeneric("false");
		} else if (eventStPerLnkEmpMerRletChkCountInDto.getIndDelPerson().equals(ServiceConstants.STRING_IND_Y)) {
			pOutputDataRec.setSysIndGeneric("true");
			deletePersonSPCall(eventStPerLnkEmpMerRletChkCountInDto);
		} else {
			pOutputDataRec.setSysIndGeneric("true");
			SQLQuery sQLQuery7 = (((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteIntakePerson)
					.setParameter("hI_ulIdPerson", eventStPerLnkEmpMerRletChkCountInDto.getIdPerson()))
							.addEntity(EventStPerLnkEmpMerRletChkCountOutDto.class));
			List<EventStPerLnkEmpMerRletChkCountOutDto> liCint60doDto3 = (List<EventStPerLnkEmpMerRletChkCountOutDto>) sQLQuery7
					.list();
			if (TypeConvUtil.isNullOrEmpty(liCint60doDto3)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Cint60dDaoImpl.person.record.not.deleted", null, Locale.US));
			}
		}
		log.debug("Exiting method deletePerson in EventStPerLnkEmpMerRletChkCountDaoImpl");
		return pOutputDataRec;
	}

	/**
	 * Method Name: deletePersonSPCall Method Description:
	 * 
	 * @param pInputDataRec
	 */
	private void deletePersonSPCall(EventStPerLnkEmpMerRletChkCountInDto pInputDataRec) {
		SessionImpl sessionImpl = (SessionImpl) sessionFactory.getCurrentSession();
		Connection connection = sessionImpl.connection();
		CallableStatement callStatement = null;
		try {
			callStatement = connection.prepareCall("{call COMPLEX_DELETE.DELETE_PERSON(?)}");
			callStatement.setInt(1, (int) pInputDataRec.getIdPerson());
			int count = callStatement.executeUpdate();
			// personDeleted = callStatement.getString(0) != null &&
			// callStatement.getInt(0) == 1 ? true : false;
			if (count == 0) {
				throw new SQLException(
						messageSource.getMessage("Cint60dDaoImpl.person.record.not.deleted", null, Locale.US));
			}
		} catch (SQLException e) {
			DataLayerException dataLayerException = new DataLayerException(e.toString());
			dataLayerException.initCause(e);
			throw dataLayerException;
		} finally {
			try {
				if (null != callStatement)
					callStatement.close();
			} catch (SQLException e) {
				log.error(e.getStackTrace());
			}
		}
	}

}
