package us.tx.state.dfps.service.financial.daoimpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.DaycareSvcAuthLink;
import us.tx.state.dfps.common.domain.OnlineParameters;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.financial.dao.ServiceAuthExtCommDao;
import us.tx.state.dfps.service.financial.dto.ServiceAuthExtCommDto;
import us.tx.state.dfps.service.financial.dto.ServiceAuthTWCBaselineDto;

@Repository
public class ServiceAuthExtCommDaoImpl implements ServiceAuthExtCommDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ServiceAuthExtCommDaoImpl.selectLatestServiceAuthExtComm}")
	private transient String selectLatestServiceAuthExtComm;

	@Value("${ServiceAuthExtCommDaoImpl.selectSvcAuthTWCBaselineForSvcAuth}")
	private transient String selectSvcAuthTWCBaselineForSvcAuth;

	@Value("${ServiceAuthExtCommDaoImpl.retrieveDayCareSvcAuthId}")
	private String retrieveDayCareSvcAuthId;

	/**
	 * Method Name: selectOnlineParameterValue Method Description: This method
	 * returns the value for the the given Key Name from ONLINE_PARAMETERS
	 * table.
	 * 
	 * @param onlineParamTwcAutTrans
	 * @return String
	 */
	@Override
	public String selectOnlineParameterValue(String onlineParamTwcAutTrans) {
		String txtValue = ServiceConstants.EMPTY_STR;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OnlineParameters.class);
		criteria.add(Restrictions.eq("txtName", onlineParamTwcAutTrans));
		List<OnlineParameters> onlineParametersList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(onlineParametersList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("ServiceAuthExtCommDao.selectOnlineParameterValue.NotFound", null, Locale.US));
		}
		for (OnlineParameters onlineParameters : onlineParametersList) {
			txtValue = onlineParameters.getTxtValue();
		}

		return txtValue;
	}

	/**
	 * Method Name: retrieveDayCareSvcAuthId Method Description:This method
	 * retrieves Service Authorization ID associated with Day Care Request
	 * Event.
	 * 
	 * @param idDayCareEvent
	 * @return Long @
	 */
	@Override
	public Long retrieveDayCareSvcAuthId(Long idDayCareEvent) {
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrieveDayCareSvcAuthId)
				.setParameter("idDaycareEvent", idDayCareEvent);
		Long idSvcAuth;
		if (ObjectUtils.isEmpty(sQLQuery1.uniqueResult())) {
			idSvcAuth = 0L;
		} else {
			idSvcAuth = ((BigDecimal) sQLQuery1.uniqueResult()).longValue();
		}
		return idSvcAuth.longValue();
	}

	/**
	 * Method Name: retrieveDayCareSvcAuthEventId Method Description:This method
	 * retrieves Service Authorization Event ID associated with Day Care Request
	 * Event.
	 * 
	 * @param idDayCareEvent
	 * @return Long @
	 */
	@Override
	public Long retrieveDayCareSvcAuthEventId(Long idDayCareEvent) {
		Long idSvcAuthEvent = ServiceConstants.LongZero;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DaycareSvcAuthLink.class);
		criteria.add(Restrictions.eq("eventByIdDaycareEvent.idEvent", idDayCareEvent));
		criteria.setProjection(Projections.max("eventByIdSvcAuthEvent.idEvent"));
		idSvcAuthEvent = (Long) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(idSvcAuthEvent)) {
			throw new DataNotFoundException(messageSource
					.getMessage("ServiceAuthExtCommDao.retrieveDayCareSvcAuthEventId.NotFound", null, Locale.US));
		}
		return idSvcAuthEvent;

	}

	/**
	 * 
	 * Method Name: selectLatestServiceAuthExtComm Method Description:This
	 * method fetches Latest SVCAUTH_EXT_COMM Record for the given Service
	 * Authorization ID.
	 * 
	 * @param idSvcAuth
	 * @return ServiceAuthExtCommDto @
	 */
	@Override
	public ServiceAuthExtCommDto selectLatestServiceAuthExtComm(Long idSvcAuth) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectLatestServiceAuthExtComm).addScalar("idSvcauthExtComm", StandardBasicTypes.LONG)
				.addScalar("idSvcAuth", StandardBasicTypes.LONG).addScalar("idSvcAuthEvent", StandardBasicTypes.LONG)
				.addScalar("cdType", StandardBasicTypes.STRING).addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("txtXmlSent", StandardBasicTypes.STRING)
				.addScalar("txtXmlResponse", StandardBasicTypes.STRING).addScalar("dtRequest", StandardBasicTypes.DATE)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idRelatedSvcauthExtComm", StandardBasicTypes.LONG)

				.setParameter("idSvcAuth", idSvcAuth)

				.setResultTransformer(Transformers.aliasToBean(ServiceAuthExtCommDto.class)));

		ServiceAuthExtCommDto svcAuthCommValueBean = (ServiceAuthExtCommDto) sQLQuery1.uniqueResult();

		return svcAuthCommValueBean;
	}

	/**
	 * 
	 * Method Name: selectSvcAuthTWCBaselineForSvcAuth Method Description: This
	 * method returns the latest Communication baseline between TWC and DFPS for
	 * the given Service Authorization.
	 * 
	 * @param idSvcAuth
	 * @return @
	 */
	@Override
	public List<ServiceAuthTWCBaselineDto> selectSvcAuthTWCBaselineForSvcAuth(Long idSvcAuth) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectSvcAuthTWCBaselineForSvcAuth)
				.addScalar("idSvcAuthDtlTwcBaseline", StandardBasicTypes.LONG)
				.addScalar("idSvcAuthDtl", StandardBasicTypes.LONG)
				.addScalar("idSvcauthExtComm", StandardBasicTypes.LONG)
				.addScalar("idPrevFacility", StandardBasicTypes.LONG)
				.addScalar("dtSvcAuthDtlTerm", StandardBasicTypes.DATE)
				.addScalar("cdSvcAuthDtlAuthType", StandardBasicTypes.STRING)
				.addScalar("dtLastRequestSent", StandardBasicTypes.DATE).addScalar("indSun", StandardBasicTypes.STRING)
				.addScalar("indMon", StandardBasicTypes.STRING).addScalar("indTue", StandardBasicTypes.STRING)
				.addScalar("indWed", StandardBasicTypes.STRING).addScalar("indThu", StandardBasicTypes.STRING)
				.addScalar("indFri", StandardBasicTypes.STRING).addScalar("indSat", StandardBasicTypes.STRING)
				.addScalar("cdSummerType", StandardBasicTypes.STRING)
				.addScalar("cdWeekendType", StandardBasicTypes.STRING).addScalar("indVarSch", StandardBasicTypes.STRING)
				.addScalar("cdVarSchMaxDays", StandardBasicTypes.STRING)

				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("txtHoursNeeded", StandardBasicTypes.STRING)
				.addScalar("txtComments", StandardBasicTypes.STRING).setParameter("idSvcAuth", idSvcAuth)
				.setResultTransformer(Transformers.aliasToBean(ServiceAuthTWCBaselineDto.class)));

		List<ServiceAuthTWCBaselineDto> serviceAuthTWCBaselineDto = sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(serviceAuthTWCBaselineDto))
			throw new DataNotFoundException(
					messageSource.getMessage("ServiceAuthTWCBaselineDto.is.null", null, Locale.US));

		return serviceAuthTWCBaselineDto;
	}

}
