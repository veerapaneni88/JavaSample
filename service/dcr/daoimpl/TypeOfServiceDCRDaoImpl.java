/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Apr 3, 2018- 12:13:08 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.dcr.daoimpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.DaycarePersonLink;
import us.tx.state.dfps.common.domain.DaycareSvcAuthLink;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.common.request.DayCareRequestReq;
import us.tx.state.dfps.service.common.response.DayCareRequestRes;
import us.tx.state.dfps.service.dcr.dao.TypeOfServiceDCRDao;
import us.tx.state.dfps.service.dcr.dto.DayCareFacilityDto;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.exception.DataLayerException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the service implementation of the Type of service DCI page Apr 3, 2018-
 * 12:13:08 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class TypeOfServiceDCRDaoImpl implements TypeOfServiceDCRDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${TypeOfServiceDCRDaoImpl.getXmlResponsesLast}")
	public String getXmlResponsesLastSql;

	@Value("${TypeOfServiceDCRDaoImpl.getXmlResponsesSystem}")
	public String getXmlResponsesSystemSql;

	@Value("${TypeOfServiceDCRDaoImpl.getApprovalDate}")
	public String getApprovalDateSql;

	@Value("${TypeOfServiceDCRDaoImpl.deleteResponses}")
	public String deleteResponsesSql;

	@Value("${TypeOfServiceDCRDaoImpl.saveXmlResponses}")
	public String saveXmlResponsesSql;

	@Value("${TypeOfServiceDCRDaoImpl.deleteDayCarePersonFacilLink}")
	public String deleteDayCarePersonFacilLinkSql;

	@Value("${TypeOfServiceDCRDaoImpl.retrieveDayCarePersonFacilLink}")
	public String retrieveDayCarePersonFacilLinkSql;

	@Value("${TypeOfServiceDCRDaoImpl.getSvcDtlOverlapRecsForSvcCatPrsn}")
	public String getSvcDtlOverlapRecsForSvcCatPrsnSql;

	/**
	 * Method Name: getXmlResponsesLast Method Description: This method gets an
	 * XML string that contains the answers to the displayed questions in the
	 * Decision Tree.
	 * 
	 * @param dayCareRequestReq
	 * @return String
	 */
	public String getXmlResponsesLast(int idPerson, int idDayCareRequest) {

		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection;
		String xmlResponses = "";
		try {
			connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			int errorCode = 0;
			CallableStatement callStatement = connection.prepareCall(getXmlResponsesLastSql);
			callStatement.setInt(1, idPerson);
			callStatement.setInt(2, idDayCareRequest);
			callStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(4, java.sql.Types.BIGINT);
			callStatement.execute();
			xmlResponses = callStatement.getString(3);
			errorCode = callStatement.getInt(4);
			if (errorCode != 0) {
				throw new SQLException(xmlResponses);
			}
		} catch (SQLException sqlExp) {
			DataLayerException dataLayerException = new DataLayerException(sqlExp.getMessage());
			dataLayerException.initCause(sqlExp);
			throw dataLayerException;
		}
		return xmlResponses;
	}

	/**
	 * Method Name: getXmlResponsesSystem Method Description: This method gets
	 * an XML string that contains the answers to system questions. This is used
	 * by the Decision Tree.
	 * 
	 * @param dayCareRequestReq
	 * @return String
	 */
	public String getXmlResponsesSystem(int idPerson, int idDayCareRequest, int idUser) {

		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection;
		int errorCode = 0;
		String xmlResponses = "";

		try {
			connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			CallableStatement callStatement = connection.prepareCall(getXmlResponsesSystemSql);
			callStatement.setInt(1, idPerson);
			callStatement.setInt(2, idDayCareRequest);
			callStatement.setInt(3, idUser);

			callStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(5, java.sql.Types.BIGINT);

			callStatement.execute();

			xmlResponses = callStatement.getString(4);
			errorCode = callStatement.getInt(5);

			if (errorCode != 0) {
				throw new SQLException(xmlResponses);
			}
		} catch (SQLException sqlExp) {
			DataLayerException dataLayerException = new DataLayerException(sqlExp.getMessage());
			dataLayerException.initCause(sqlExp);
			throw dataLayerException;
		}

		return xmlResponses;
	}

	/**
	 * Method Name: getApprovalDate Method Description: This method returns the
	 * Day care Request approval date or '12/31/4712' if there is no Approval
	 * Date.
	 * 
	 * @param dayCareRequestReq
	 * @return Date
	 */
	public Date getApprovalDate(int idDayCareRequest) {

		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection;
		Date approvalDate = null;
		try {
			connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			CallableStatement callStatement = connection.prepareCall(getApprovalDateSql);
			callStatement.setInt(1, idDayCareRequest);
			callStatement.registerOutParameter(2, java.sql.Types.TIMESTAMP);
			callStatement.execute();
			approvalDate = callStatement.getTimestamp(2);

		} catch (SQLException sqlExp) {
			DataLayerException dataLayerException = new DataLayerException(sqlExp.getMessage());
			dataLayerException.initCause(sqlExp);
			throw dataLayerException;
		}
		return approvalDate;
	}

	/**
	 * Method Name: deleteResponses Method Description: This method delete the
	 * responses stored in the DAYCARE_PERSON_RESPONSE
	 * 
	 * @param dayCareRequestReq
	 * @return
	 */
	public ServiceResHeaderDto deleteResponses(int idDayCareRequest, int idPerson) {

		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection;
		ServiceResHeaderDto serviceResHeaderDto = new ServiceResHeaderDto();
		try {
			connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			CallableStatement callStatement = connection.prepareCall(deleteResponsesSql);
			callStatement.setInt(1, idPerson);
			callStatement.setInt(2, idDayCareRequest);
			callStatement.execute();
		} catch (SQLException sqlExp) {
			DataLayerException dataLayerException = new DataLayerException(sqlExp.getMessage());
			dataLayerException.initCause(sqlExp);
			throw dataLayerException;
		}

		return serviceResHeaderDto;
	}

	/**
	 * Method Name: saveXmlResponses Method Description: Save the answers to the
	 * displayed questions in the Decision Tree. an XML string that contains
	 * 
	 * @param dayCareRequestReq
	 * @return
	 */
	public int saveXmlResponses(int idDayCareRequest, int idPerson, int idPersonLastUpdated, String xmlResponses) {

		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection;
		int errorCode = 0;
		String errorMessage = "";

		try {
			connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			CallableStatement callStatement = connection.prepareCall(saveXmlResponsesSql);
			callStatement.setInt(1, idPerson);
			callStatement.setInt(2, idDayCareRequest);
			callStatement.setInt(3, idPersonLastUpdated);
			callStatement.setString(4, xmlResponses);
			callStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(6, java.sql.Types.BIGINT);

			callStatement.execute();

			errorMessage = callStatement.getString(5);
			errorCode = callStatement.getInt(6);

			if (errorCode != 0) {
				throw new SQLException(errorMessage);
			}
		} catch (SQLException sqlExp) {
			DataLayerException dataLayerException = new DataLayerException(sqlExp.getMessage());
			dataLayerException.initCause(sqlExp);
			throw dataLayerException;
		}

		return errorCode;
	}

	/**
	 * Method Name: deleteTypeOfService Method Description: This method deletes
	 * child daycare service type
	 * 
	 * @param dayCareRequestDto
	 * @return
	 */
	public void deleteTypeOfService(Long idPerson, Long idDayCareRequest, Long idLastUpdatedPerson) {
		DaycarePersonLink daycarePersonLink = null;
		Criteria daycarePersons = sessionFactory.getCurrentSession().createCriteria(DaycarePersonLink.class)
				.add(Restrictions.eq("person.idPerson", idPerson))
				.add(Restrictions.eq("daycareRequest.idDaycareRequest", idDayCareRequest));
		List<DaycarePersonLink> daycarePersonList = (List<DaycarePersonLink>) daycarePersons.list();
		if (!CollectionUtils.isEmpty(daycarePersonList)) {
			daycarePersonLink = daycarePersonList.get(0);
			daycarePersonLink.setIdLastUpdatePerson(idLastUpdatedPerson);
			daycarePersonLink.setCdRequestType(null);
			daycarePersonLink.setCdDaycareType(null);
			daycarePersonLink.setCdDetermService(null);
			daycarePersonLink.setDtBegin(null);
			daycarePersonLink.setDtEnd(null);
			daycarePersonLink.setTxtHoursNeeded(null);
			daycarePersonLink.setIdFacility(null);
			daycarePersonLink.setCdSummerType(null);
			daycarePersonLink.setIndSun(null);
			daycarePersonLink.setIndMon(null);
			daycarePersonLink.setIndTue(null);
			daycarePersonLink.setIndWed(null);
			daycarePersonLink.setIndThu(null);
			daycarePersonLink.setIndFri(null);
			daycarePersonLink.setIndSat(null);
			daycarePersonLink.setCdWeekendType(null);
			daycarePersonLink.setIndVarSch(null);
			daycarePersonLink.setCdVarSchMaxDays(null);
			daycarePersonLink.setTxtComments(null);
		}
		if (!ObjectUtils.isEmpty(daycarePersonLink))
			sessionFactory.getCurrentSession().saveOrUpdate(daycarePersonLink);
	}

	/**
	 * Method Name: deleteDayCarePersonFacilLink Method Description: This method
	 * is to delete child/caregiver information from the
	 * DAYCARE_PERSON_FACIL_LINK table
	 * 
	 * @param dayCareRequestDto
	 * @return
	 */
	public void deleteDayCarePersonFacilLink(Long idPerson, Long idDayCareRequest, Long IdFacility) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteDayCarePersonFacilLinkSql)
				.setParameter("idPerson", idPerson).setParameter("idDayCareRequest", idDayCareRequest)
				.setParameter("idFacility", IdFacility);
		query.executeUpdate();
	}

	/**
	 * Method Name: retrieveDayCarePersonFacilLink Method Description: This
	 * method is to retrieve child/caregiver information from the
	 * DAYCARE_PERSON_FACIL_LINK table
	 * 
	 * @param dayCareRequestDto
	 * @return DayCareRequestRes
	 */
	@SuppressWarnings("unchecked")
	public DayCareRequestRes retrieveDayCarePersonFacilLink(DayCareRequestReq DayCareRequestReq) {
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();
		List<DayCareFacilityDto> dayCareFacilityDtoList = (List<DayCareFacilityDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(retrieveDayCarePersonFacilLinkSql)
				.setParameter("idPerson", DayCareRequestReq.getIdPerson())
				.setParameter("idDayCareRequest", DayCareRequestReq.getIdDayCareRequest()))
						.addScalar("idDayCareRequest", StandardBasicTypes.LONG)
						.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
						.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
						.addScalar("idFacility", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(DayCareFacilityDto.class)).list();
		dayCareRequestRes.setDayCareFacilityDtoList(dayCareFacilityDtoList);
		return dayCareRequestRes;
	}

	/**
	 * Method Name: getOverlapRecsForSvcAuth Method Description: for a service
	 * auth detail record being saved or added check if its
	 * DT_SVC_AUTH_DTL_BEGIN and DT_SVC_AUTH_DTL_TERM overlap with an existing
	 * record in svc_auth_detail table.
	 * 
	 * @param dayCarePersonDto
	 * @param idPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DayCarePersonDto> getOverlapRecsForSvcAuth(DayCarePersonDto dayCarePersonDto, Long idPerson) {
		List<DayCarePersonDto> svcAuthDtlList = (List<DayCarePersonDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSvcDtlOverlapRecsForSvcCatPrsnSql).setParameter("idPerson", idPerson)
				.setParameter("dtBegin",dayCarePersonDto.getDtBegin())
				.setParameter("dtEnd", dayCarePersonDto.getDtEnd())).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idSvcAuthDtl", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(DayCarePersonDto.class)).list();
		return svcAuthDtlList;
	}
	
	
	/**
	 * Method Name: getSvcAuthLink 
	 * Method Description: Method used to check the requested day care request is same as day care request serivceAuth link table.
	 * @param dayCarePersonDto
	 * @param dayCarePersonDtos
	 * @param idEvent
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean getSvcAuthLink(List<DayCarePersonDto> dayCarePersonDtos, DayCarePersonDto dayCarePersonDto,
			Long idEvent) {
		boolean isOverLapExists = Boolean.FALSE;
		for (DayCarePersonDto dayCarePersonDto2 : dayCarePersonDtos) {
			Criteria daycareSvcAuthLink = sessionFactory.getCurrentSession().createCriteria(DaycareSvcAuthLink.class)
					.add(Restrictions.eq("eventByIdSvcAuthEvent.idEvent", dayCarePersonDto2.getIdSvcAuthDtl()));
			List<DaycareSvcAuthLink> daycarePersonList = (List<DaycareSvcAuthLink>) daycareSvcAuthLink.list();
			if (!ObjectUtils.isEmpty(daycarePersonList)) {
				// set overlap when the event id is different
				isOverLapExists = daycarePersonList.stream()
						.anyMatch(d -> !d.getEventByIdDaycareEvent().getIdEvent().equals(idEvent));
				if (isOverLapExists) {
					break;
				}
			}
		}
		return isOverLapExists;
	}
}
