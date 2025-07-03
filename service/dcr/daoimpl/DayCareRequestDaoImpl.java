package us.tx.state.dfps.service.dcr.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
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
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.ApprovalEventLink;
import us.tx.state.dfps.common.domain.Approvers;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.DaycarePersonLink;
import us.tx.state.dfps.common.domain.DaycareRequest;
import us.tx.state.dfps.common.domain.DaycareSvcAuthLink;
import us.tx.state.dfps.common.domain.EmpSecClassLink;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.SsccDaycareRequest;
import us.tx.state.dfps.common.domain.SsccReferral;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.DayCareApproversDto;
import us.tx.state.dfps.common.dto.StaffDto;
import us.tx.state.dfps.service.admin.dto.PersonDiDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.dcr.dao.DayCareRequestDao;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestCountOutDto;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestDto;
import us.tx.state.dfps.service.dcr.dto.DayCareSearchListDto;
import us.tx.state.dfps.service.dcr.dto.SSCCDayCareRequestDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.financial.dto.FinancialDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.workload.dto.EventIdDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Day Care
 * Request Dao Impl Jul 22, 2017- 7:10:31 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
@SuppressWarnings("unchecked")
public class DayCareRequestDaoImpl implements DayCareRequestDao {

	@Value("${DayCareRequestDaoImpl.arePersonInSameRequest}")
	private String arePersonInSameRequest;

	@Value("${DayCareRequestDaoImpl.listRequestForPerson}")
	private String listRequestForPerson;

	@Value("${DayCareRequestDaoImpl.rtrvSvcAuthPerson}")
	private String rtrvSvcAuthPerson;

	@Value("${DayCareRequestDaoImpl.rtrvDayCareReqSvcAuthPersonDtl}")
	private String rtrvDayCareReqSvcAuthPersonDtl;

	@Value("${DayCareRequestDaoImpl.retrieveDayCareRequestSvcAuth}")
	private String retrieveDayCareRequestSvcAuth;

	@Value("${DayCareRequestDaoImpl.fetchDayCareDetailsForSvcAuthEventId}")
	private String fetchDayCareDetailsForSvcAuthEventId;

	@Value("${DayCareRequestDaoImpl.getDaycareCoordinator}")
	private String getDaycareCoordinator;

	@Value("${DayCareRequestDaoImpl.getSSCCReferalForIdPersonDC}")
	private String getSSCCReferalForIdPersonDC;

	@Value("${DayCareRequestDaoImpl.retrieveSSCCDayCareRequest}")
	private String retrieveSSCCDayCareRequest;

	@Value("${DayCareRequestDaoImpl.getAppEventId}")
	private String getAppEventId;

	@Value("${DayCareRequestDaoImpl.retrievePersonDayCareDetails}")
	private String retrievePersonDayCareDetails;

	@Value("${DayCareRequestDaoImpl.getDayCareApprovers}")
	private String getDayCareApprovers;

	@Value("${DayCareRequestDaoImpl.fetchReferralsForAllPersonsInDaycareRequest}")
	private String fetchReferralsForAllPersonsInDaycareRequest;

	@Value("${DayCareRequestDaoImpl.deleteDayCareRequest}")
	private String deleteDayCareRequest;

	@Value("${DayCareRequestDaoImpl.searchFacility}")
	private String searchFacility;

	@Value("${DayCareRequestDaoImpl.getFacilityById}")
	private String getFacilityById;

	@Value("${DayCareRequestDaoImpl.otherDayCareServices}")
	private String otherDayCareServices;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	private LookupDao lookupDao;

	@Value("${DayCareRequestDaoImpl.retrieveDayCareRequestDetail}")
	private String retrieveDayCareRequestDetailSql;

	@Value("${DayCareRequestDaoImpl.populateStaffSuperPhone}")
	private String populateStaffSuperPhone;

	@Value("${DayCareRequestDaoImpl.populateStaffInfo}")
	private String populateStaffInfo;

	@Value("${DayCareRequestDaoImpl.populateSupervisorInfo}")
	private String populateSupervisorInfo;

	@Value("${DayCareRequestDaoImpl.getDaycareCodes}")
	private String getDaycareCodes;

	public DayCareRequestDaoImpl() {

	}

	@Override
	public boolean arePersonsInSameDCRequest(Long idPerson1, Long idPerson2) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(arePersonInSameRequest)
				.addScalar("idDayCareRequest", StandardBasicTypes.LONG).setParameter("idPerson1", idPerson1)
				.setParameter("idPerson2", idPerson2)
				.setResultTransformer(Transformers.aliasToBean(DayCareRequestDto.class));
		DayCareRequestDto countDto = (DayCareRequestDto) query.uniqueResult();
		if (countDto != null) {
			Long count = countDto.getIdDayCareRequest();
			if (count != null && count > 0L)
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public DayCareRequestDto listDcRequestDatesForPerson(Long idPerson) {
		DayCareRequestDto dayCareRequestDto = new DayCareRequestDto();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(listRequestForPerson)
				.addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPersonDeath", StandardBasicTypes.TIMESTAMP).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));
		List<PersonDto> personDtoList = query.list();
		List<DayCarePersonDto> dayCarePersonDtoList = new ArrayList<DayCarePersonDto>();
		for (PersonDto personDto : personDtoList) {
			DayCarePersonDto dayCarePersonDto = new DayCarePersonDto();
			dayCarePersonDto.setDtBegin(personDto.getDtPersonBirth());
			dayCarePersonDto.setDtEnd(personDto.getDtPersonDeath());
			dayCarePersonDtoList.add(dayCarePersonDto);
		}
		dayCareRequestDto.setDayCarePersonDtoList(dayCarePersonDtoList);
		return dayCareRequestDto;
	}

	/**
	 * Method: retrieveSvcAuthPerson Method Description: Retrieve the list of
	 * persons from dayCare related Service Auth
	 * 
	 * @param idSvcAuthEvent
	 *            - Service Authorization event
	 * @return List<PersonDiDto> - returns List of PersonId
	 */
	@Override
	public List<PersonDiDto> retrieveSvcAuthPerson(Long idSvcAuthEvent) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(rtrvSvcAuthPerson)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idSvcAuthEvent", idSvcAuthEvent)
				.setResultTransformer(Transformers.aliasToBean(PersonDiDto.class));
		List<PersonDiDto> personDtoList = (List<PersonDiDto>) query.list();
		return personDtoList;
	}

	/**
	 * 
	 * Method Name: retrieveDayCareRequestSvcAuthPersonDtl Method Description:
	 * Retrieve the person details related to DayCareRequest for Service Auth
	 * Detail
	 * 
	 * @param idDayCareEvent
	 *            - Day Care Service Authorization event
	 * @param idStage
	 *            - ID Stage
	 * @return List<DayCarePersonValueDto> - List of DayCarePersonDto which
	 *         contains list of person having day care
	 */
	@Override
	public List<DayCarePersonDto> retrieveDayCareRequestSvcAuthPersonDtl(Long idDayCareEvent, Long idStage) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(rtrvDayCareReqSvcAuthPersonDtl)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFullName", StandardBasicTypes.STRING)
				.addScalar("cdDetermService", StandardBasicTypes.STRING).addScalar("dtBegin", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).setParameter("idDayCareEvent", idDayCareEvent)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(DayCarePersonDto.class));
		List<DayCarePersonDto> dayCarePersonDtoList = (List<DayCarePersonDto>) query.list();
		return dayCarePersonDtoList;
	}

	/**
	 * 
	 * Method Name: retrieveDayCareRequestSvcAuth Method Description: Retrieve
	 * the day care request event id from dayCare_svc_auth_link table
	 * 
	 * @param idSvcAuthEvent
	 *            - Service Authorization event id
	 * @return dayCareEventDto - Day care event Id
	 */
	public EventIdDto retrieveDayCareRequestSvcAuth(Long idSvcAuthEvent) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(retrieveDayCareRequestSvcAuth)
				.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idSvcAuthEvent", idSvcAuthEvent)
				.setResultTransformer(Transformers.aliasToBean(EventIdDto.class));
		EventIdDto dayCareEventDto = (EventIdDto) query.uniqueResult();
		return dayCareEventDto;
	}

	/**
	 * 
	 * Method Name: fetchDayCareDetailsForSvcAuthEventId Method Description:This
	 * function retrieve the DayCare Request details for the given
	 * ServiceAuthEvent.
	 * 
	 * @param idSvcAuthEvent
	 * @return @
	 */
	@Override
	public DayCareRequestDto fetchDayCareDetailsForSvcAuthEventId(Long idSvcAuthEvent) {
		DayCareRequestDto dayCareRequestDto = new DayCareRequestDto();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(fetchDayCareDetailsForSvcAuthEventId)
				.addScalar("idDayCarePersonLink", StandardBasicTypes.LONG)
				.addScalar("idSvcAuth", StandardBasicTypes.LONG).addScalar("idSvcAuthDtl", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("dtSvcAuthDtlTerm", StandardBasicTypes.DATE)
				.addScalar("cdSvcAuthDtlAuthType", StandardBasicTypes.STRING)
				.addScalar("dtlastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idDayCareRequest", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdPersonType", StandardBasicTypes.STRING)
				.addScalar("cdDetermService", StandardBasicTypes.STRING)
				.addScalar("cdRequestType", StandardBasicTypes.STRING)
				.addScalar("cdDaycareType", StandardBasicTypes.STRING).addScalar("dtBegin", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("txtcmnts", StandardBasicTypes.STRING)
				.addScalar("idFacility", StandardBasicTypes.LONG).addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.addScalar("indSun", StandardBasicTypes.STRING).addScalar("indMon", StandardBasicTypes.STRING)
				.addScalar("indTue", StandardBasicTypes.STRING).addScalar("indWed", StandardBasicTypes.STRING)
				.addScalar("indThu", StandardBasicTypes.STRING).addScalar("indFri", StandardBasicTypes.STRING)
				.addScalar("indSat", StandardBasicTypes.STRING).addScalar("indVarSch", StandardBasicTypes.STRING)
				.addScalar("cdVarSchMaxdays", StandardBasicTypes.STRING)
				.addScalar("cdSummerType", StandardBasicTypes.STRING)
				.addScalar("cdWeekendType", StandardBasicTypes.STRING)
				.addScalar("txtHoursNeeded", StandardBasicTypes.STRING).setParameter("idSvcAuthEvent", idSvcAuthEvent)
				.setResultTransformer(Transformers.aliasToBean(FinancialDto.class));

		FinancialDto financialDto = (FinancialDto) query.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(financialDto)) {

			dayCareRequestDto.setIdDayCareRequest(financialDto.getIdDayCareRequest());
			dayCareRequestDto.setIdSvcAuth(financialDto.getIdSvcAuth());
			dayCareRequestDto.setIdSvcAuthEvent(idSvcAuthEvent);

			DayCarePersonDto dayCarePersonDto = new DayCarePersonDto();
			dayCarePersonDto.setIdSvcAuthDtl(financialDto.getIdSvcAuthDtl());
			dayCarePersonDto.setDtSvcAuthDtlTerm(financialDto.getDtSvcAuthDtlTerm());
			dayCarePersonDto.setCdSvcAuthDtlAuthType(financialDto.getCdSvcAuthDtlAuthType());
			populateDayCarePersonLink(financialDto, dayCarePersonDto);
		}
		return dayCareRequestDto;
	}

	/**
	 * 
	 * Method Name: getDaycareCoordinator Method Description:This function
	 * returns Regional Day Care Coordinator.
	 * 
	 * @param idEvent
	 * @return
	 */
	@Override
	public PersonValueDto getDaycareCoordinator(Integer idEvent) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getDaycareCoordinator)
				.addScalar("firstName", StandardBasicTypes.STRING).addScalar("middleName", StandardBasicTypes.STRING)
				.addScalar("lastName", StandardBasicTypes.STRING).addScalar("nameSuffixCode", StandardBasicTypes.STRING)
				.addScalar("personId", StandardBasicTypes.LONG).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(PersonValueDto.class));
		PersonValueDto personValueDto = (PersonValueDto) query.uniqueResult();
		return personValueDto;

	}

	/**
	 * Method Name: insertDayCareRequest Method Description:Inserts the details
	 * of DayCareRequestValueDto
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long @
	 */
	@Override
	public Long insertDayCareRequest(DayCareRequestDto dayCareRequestValueDto) {

		Long primaryKey = ServiceConstants.LongZero;
		DaycareRequest daycareRequest = new DaycareRequest();

		CapsCase capsCase = new CapsCase();
		Event event = new Event();
		Stage stage = new Stage();

		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIdlastUpdatePerson())) {
			daycareRequest.setIdLastUpdatePerson(dayCareRequestValueDto.getIdlastUpdatePerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIdCreatedPerson())) {
			daycareRequest.setIdCreatedPerson(dayCareRequestValueDto.getIdCreatedPerson());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIdCase())) {
			capsCase.setIdCase(dayCareRequestValueDto.getIdCase());
			daycareRequest.setCapsCase(capsCase);
		}
		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIdStage())) {
			stage.setIdStage(dayCareRequestValueDto.getIdStage());
			daycareRequest.setStage(stage);
		}
		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIdEvent())) {
			event.setIdEvent(dayCareRequestValueDto.getIdEvent());
			daycareRequest.setEvent(event);
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getDtCreated())) {
			daycareRequest.setDtCreated(dayCareRequestValueDto.getDtCreated());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getDtLastUpdate())) {
			daycareRequest.setDtLastUpdate(dayCareRequestValueDto.getDtLastUpdate());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIndInvalid())) {
			daycareRequest.setIndInvalid(dayCareRequestValueDto.getIndInvalid());
		}
		primaryKey = (Long) sessionFactory.getCurrentSession().save(daycareRequest);
		if (TypeConvUtil.isNullOrEmpty(primaryKey)) {
			throw new DataNotFoundException(
					messageSource.getMessage("dayCareRequestDao.insertDayCareRequest.NotFound", null, Locale.US));
		}
		return primaryKey;

	}

	/**
	 * Method Name: insertSSCCDayCareRequest Method Description:This method
	 * inserts new record into SSCC_DAYCARE_REQUEST table.
	 * 
	 * @param ssccDayCareReqDto
	 * @return Long @
	 */
	@Override
	public Long insertSSCCDayCareRequest(SSCCDayCareRequestDto ssccDayCareReqDto) {
		Long primaryKey = ServiceConstants.LongZero;
		SsccDaycareRequest ssccDaycareRequest = new SsccDaycareRequest();

		if (!TypeConvUtil.isNullOrEmpty(ssccDayCareReqDto.getIdDayCareRequest())) {
			ssccDaycareRequest.setIdDaycareRequest(ssccDayCareReqDto.getIdDayCareRequest());
		}
		if (!TypeConvUtil.isNullOrEmpty(ssccDayCareReqDto.getIdlastUpdatePerson())) {
			ssccDaycareRequest.setIdLastUpdatePerson(ssccDayCareReqDto.getIdlastUpdatePerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(ssccDayCareReqDto.getIdCreatedPerson())) {
			ssccDaycareRequest.setIdCreatedPerson(ssccDayCareReqDto.getIdCreatedPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(ssccDayCareReqDto.getCdStatus())) {
			ssccDaycareRequest.setCdStatus(ssccDayCareReqDto.getCdStatus());
		}
		if (!TypeConvUtil.isNullOrEmpty(ssccDayCareReqDto.getIdSSCCReferral())) {
			ssccDaycareRequest.setIdSsccReferral(ssccDayCareReqDto.getIdSSCCReferral());

		}

		if (TypeConvUtil.isNullOrEmpty(ssccDayCareReqDto.getDtCreated())) {
			ssccDaycareRequest.setDtCreated(new Date());
		}
		if (TypeConvUtil.isNullOrEmpty(ssccDayCareReqDto.getDtLastUpdate())) {
			ssccDaycareRequest.setDtLastUpdate(new Date());
		}

		primaryKey = (Long) sessionFactory.getCurrentSession().save(ssccDaycareRequest);
		if (TypeConvUtil.isNullOrEmpty(primaryKey)) {
			throw new DataNotFoundException(
					messageSource.getMessage("dayCareRequestDao.insertSSCCDayCareRequest.NotFound", null, Locale.US));
		}
		return primaryKey;

	}

	/**
	 * Method Name: updateSSCCDayCareRequest Method Description:This method
	 * updates SSCC_DAYCARE_REQUEST table using ssccDayCareRequestDto
	 * 
	 * @param ssccDayCareRequestDto
	 * @return Long @
	 */
	@Override
	public Long updateSSCCDayCareRequest(SSCCDayCareRequestDto ssccDayCareRequestDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccDaycareRequest.class);
		criteria.add(Restrictions.eq("idSsccDaycareRequest", ssccDayCareRequestDto.getIdSSCCDayCareRequest()));

		SsccDaycareRequest ssccDaycareRequest = (SsccDaycareRequest) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(ssccDaycareRequest)) {
			throw new DataNotFoundException(
					messageSource.getMessage("dayCareRequestDao.updateSSCCDayCareRequest.NotFound", null, Locale.US));
		}

		if (!TypeConvUtil.isNullOrEmpty(ssccDayCareRequestDto.getIdlastUpdatePerson())) {
			ssccDaycareRequest.setIdLastUpdatePerson(ssccDayCareRequestDto.getIdlastUpdatePerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(ssccDayCareRequestDto.getIdDayCareRequest())) {
			ssccDaycareRequest.setIdDaycareRequest(ssccDayCareRequestDto.getIdDayCareRequest());
		}
		if (!TypeConvUtil.isNullOrEmpty(ssccDayCareRequestDto.getCdStatus())) {
			ssccDaycareRequest.setCdStatus(ssccDayCareRequestDto.getCdStatus());
		}

		sessionFactory.getCurrentSession().saveOrUpdate(ssccDaycareRequest);
		return ssccDaycareRequest.getIdSsccDaycareRequest();
	}

	/**
	 * Method Name: updateDayCarePersonLink Method Description:Update the
	 * DAYCARE_PERSON_LINK
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long @
	 */
	@Override
	public Long updateDayCarePersonLink(DayCareRequestDto dayCareRequestValueDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DaycarePersonLink.class);
		criteria.add(Restrictions.eq("daycareRequest.idDaycareRequest", dayCareRequestValueDto.getIdDayCareRequest()));
		criteria.add(Restrictions.eq("person.idPerson", dayCareRequestValueDto.getIdPerson()));

		List<DaycarePersonLink> daycarePersonLinkList = (List<DaycarePersonLink>) criteria.list();
		if (TypeConvUtil.isNullOrEmpty(daycarePersonLinkList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("dayCareRequestDao.updateDayCarePersonLink.NotFound", null, Locale.US));
		}
		for (DaycarePersonLink daycarePersonLink : daycarePersonLinkList) {

			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIdlastUpdatePerson())) {
				daycarePersonLink.setIdLastUpdatePerson(dayCareRequestValueDto.getIdlastUpdatePerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getCdDetermService())) {
				daycarePersonLink.setCdDetermService(dayCareRequestValueDto.getCdDetermService());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getCdRequestType())) {
				daycarePersonLink.setCdRequestType(dayCareRequestValueDto.getCdRequestType());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getCdDayCareType())) {
				daycarePersonLink.setCdDaycareType(dayCareRequestValueDto.getCdDayCareType());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getDtBegin())) {
				daycarePersonLink.setDtBegin(dayCareRequestValueDto.getDtBegin());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getDtEnd())) {
				daycarePersonLink.setDtEnd(dayCareRequestValueDto.getDtEnd());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getTxtDayHoursNeeded())) {
				daycarePersonLink.setTxtHoursNeeded(dayCareRequestValueDto.getTxtDayHoursNeeded());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIdFacility())) {
				daycarePersonLink.setIdFacility(dayCareRequestValueDto.getIdFacility());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getCdSummerType())) {
				daycarePersonLink.setCdSummerType(dayCareRequestValueDto.getCdSummerType());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIndSun())) {
				daycarePersonLink.setIndSun(dayCareRequestValueDto.getIndSun());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIndMon())) {
				daycarePersonLink.setIndMon(dayCareRequestValueDto.getIndMon());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIndTue())) {
				daycarePersonLink.setIndTue(dayCareRequestValueDto.getIndTue());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIndWed())) {
				daycarePersonLink.setIndWed(dayCareRequestValueDto.getIndWed());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIndThu())) {
				daycarePersonLink.setIndThu(dayCareRequestValueDto.getIndThu());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIndFri())) {
				daycarePersonLink.setIndFri(dayCareRequestValueDto.getIndFri());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIndSat())) {
				daycarePersonLink.setIndSat(dayCareRequestValueDto.getIndSat());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getCdWeekendType())) {
				daycarePersonLink.setCdWeekendType(dayCareRequestValueDto.getCdWeekendType());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIndVarSch())) {
				daycarePersonLink.setIndVarSch(dayCareRequestValueDto.getIndVarSch());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getCdVarSchMaxDays())) {
				daycarePersonLink.setCdVarSchMaxDays(dayCareRequestValueDto.getCdVarSchMaxDays());
			}
			// artf125518 whatever the user input that will be saved, even null or ""
			daycarePersonLink.setTxtComments(dayCareRequestValueDto.getTxtComments());
			sessionFactory.getCurrentSession().saveOrUpdate(daycarePersonLink);
		}

		return (long) daycarePersonLinkList.size();

	}

	/**
	 * Method Name: retrievePersonDayCareDetails Method Description:Retrieve all
	 * valid daycare requests for input person
	 * 
	 * @param dayCareRequestValueDto
	 * @return DayCareRequestValueDto @
	 */
	@Override
	public DayCareRequestDto retrievePersonDayCareDetails(DayCareRequestDto dayCareRequestValueDto) {
		List<DayCarePersonDto> dayCarePersonDtoList = dayCareRequestValueDto.getDayCarePersonDtoList();
		DayCarePersonDto dayCarePersonDto = dayCarePersonDtoList.get(ServiceConstants.Zero);

		List<DayCarePersonDto> dayCarePersonDtoLists = new ArrayList<>();
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrievePersonDayCareDetails)
				.addScalar("idDaycareRequest", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdPersonType", StandardBasicTypes.STRING)
				.addScalar("cdDetermService", StandardBasicTypes.STRING)
				.addScalar("cdRequestType", StandardBasicTypes.STRING)
				.addScalar("cdDaycareType", StandardBasicTypes.STRING).addScalar("dtBegin", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("txtHoursNeeded", StandardBasicTypes.STRING)
				.addScalar("idFacility", StandardBasicTypes.LONG).addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.addScalar("cdSummerType", StandardBasicTypes.STRING).addScalar("indSun", StandardBasicTypes.STRING)
				.addScalar("indMon", StandardBasicTypes.STRING).addScalar("indTue", StandardBasicTypes.STRING)
				.addScalar("indWed", StandardBasicTypes.STRING).addScalar("indThu", StandardBasicTypes.STRING)
				.addScalar("indFri", StandardBasicTypes.STRING).addScalar("indSat", StandardBasicTypes.STRING)
				.addScalar("cdWeekendType", StandardBasicTypes.STRING).addScalar("indVarSch", StandardBasicTypes.STRING)
				.addScalar("cdVarSchMaxDays", StandardBasicTypes.STRING)
				.addScalar("txtComments", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.setParameter("idEvent", dayCareRequestValueDto.getIdEvent())
				.setParameter("idPerson", dayCarePersonDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(DayCareRequestDto.class));

		List<DayCareRequestDto> dayCareRequestValueDtoList = sQLQuery.list();

		if (TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtoList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("dayCareRequestDao.retrievePersonDayCareDetails.NotFound", null, Locale.US));
		}
		for (DayCareRequestDto dayCareRequestValueDtos : dayCareRequestValueDtoList) {
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIdDayCareRequest())) {
				dayCareRequestValueDto.setIdDayCareRequest(dayCareRequestValueDto.getIdDayCareRequest());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getDtLastUpdate())) {
				dayCareRequestValueDto.setDtLastUpdate(dayCareRequestValueDto.getDtLastUpdate());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIdlastUpdatePerson())) {
				dayCareRequestValueDto.setIdlastUpdatePerson(dayCareRequestValueDto.getIdlastUpdatePerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getDtCreated())) {
				dayCareRequestValueDto.setDtCreated(dayCareRequestValueDto.getDtCreated());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIdCreatedPerson())) {
				dayCareRequestValueDto.setIdCreatedPerson(dayCareRequestValueDto.getIdCreatedPerson());
			}
			DayCarePersonDto dayCarePersonDtoOutPut = new DayCarePersonDto();
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIdDayCareRequest())) {
				dayCarePersonDtoOutPut.setIdDayCareRequest(dayCareRequestValueDto.getIdDayCareRequest());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIdCase())) {
				dayCarePersonDtoOutPut.setIdCase(dayCareRequestValueDto.getIdCase());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIdStage())) {
				dayCarePersonDtoOutPut.setIdStage(dayCareRequestValueDto.getIdStage());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getDtLastUpdate())) {
				dayCarePersonDtoOutPut.setDtLastUpdate(dayCareRequestValueDto.getDtLastUpdate());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIdlastUpdatePerson())) {
				dayCarePersonDtoOutPut.setIdlastUpdatePerson(dayCareRequestValueDto.getIdlastUpdatePerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getDtCreated())) {
				dayCarePersonDtoOutPut.setDtCreated(dayCareRequestValueDto.getDtCreated());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIdCreatedPerson())) {
				dayCarePersonDtoOutPut.setIdCreatedPerson(dayCareRequestValueDto.getIdCreatedPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIdPerson())) {
				dayCarePersonDtoOutPut.setIdPerson(dayCareRequestValueDto.getIdPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getCdPersonType())) {
				dayCarePersonDtoOutPut.setCdPersonType(dayCareRequestValueDto.getCdPersonType());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getCdDetermService())) {
				dayCarePersonDtoOutPut.setCdDetermService(dayCareRequestValueDto.getCdDetermService());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getCdRequestType())) {
				dayCarePersonDtoOutPut.setCdRequestType(dayCareRequestValueDto.getCdRequestType());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getCdDayCareType())) {
				dayCarePersonDtoOutPut.setCdDayCareType(dayCareRequestValueDto.getCdDayCareType());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getDtBegin())) {
				dayCarePersonDtoOutPut.setDtBegin(dayCareRequestValueDto.getDtBegin());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getDtEnd())) {
				dayCarePersonDtoOutPut.setDtEnd(dayCareRequestValueDto.getDtEnd());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getTxtDayHoursNeeded())) {
				dayCarePersonDtoOutPut.setDayHoursNeeded(dayCareRequestValueDto.getTxtDayHoursNeeded());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIdSSCCReferral())) {
				dayCarePersonDtoOutPut.setIdSSCCReferral(dayCareRequestValueDto.getIdSSCCReferral());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getCdSummerType())) {
				dayCarePersonDtoOutPut.setCdSummerType(dayCareRequestValueDto.getCdSummerType());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIndSun())) {
				dayCarePersonDtoOutPut.setIndSun(dayCareRequestValueDto.getIndSun());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIndMon())) {
				dayCarePersonDtoOutPut.setIndMon(dayCareRequestValueDto.getIndMon());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIndTue())) {
				dayCarePersonDtoOutPut.setIndTue(dayCareRequestValueDto.getIndTue());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIndWed())) {
				dayCarePersonDtoOutPut.setIndWed(dayCareRequestValueDto.getIndWed());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIndThu())) {
				dayCarePersonDtoOutPut.setIndThu(dayCareRequestValueDto.getIndThu());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIndFri())) {
				dayCarePersonDtoOutPut.setIndFri(dayCareRequestValueDto.getIndFri());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIndSat())) {
				dayCarePersonDtoOutPut.setIndSat(dayCareRequestValueDto.getIndSat());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getCdWeekendType())) {
				dayCarePersonDtoOutPut.setCdWeekendType(dayCareRequestValueDto.getCdWeekendType());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getIndVarSch())) {
				dayCarePersonDtoOutPut.setIndVarSch(dayCareRequestValueDto.getIndVarSch());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getCdVarSchMaxDays())) {
				dayCarePersonDtoOutPut.setCdVarSchMaxDays(dayCareRequestValueDto.getCdVarSchMaxDays());
			}
			if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDtos.getTxtComments())) {
				dayCarePersonDtoOutPut.setTxtComments(dayCareRequestValueDto.getTxtComments());
			}
			dayCarePersonDtoLists.add(dayCarePersonDtoOutPut);
		}
		dayCareRequestValueDto.setDayCarePersonDtoList(dayCarePersonDtoLists);
		return dayCareRequestValueDto;
	}

	/**
	 * Method Name: updateApproversStatus Method Description:Updates the
	 * Approvers Status Code to Invalid
	 * 
	 * @param idEvent
	 * @return Long @
	 */
	@Override
	public Long updateApproversStatus(Long idEvent) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ApprovalEventLink.class);
		criteria.add(Restrictions.eq("idEvent", idEvent));
		criteria.setProjection(Projections.property("idApproval"));

		List<Long> idApprovalList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(idApprovalList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		Long approvalId = idApprovalList.get(ServiceConstants.Zero);
		if (approvalId != ServiceConstants.LongZero) {

			Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(Event.class);
			criteria1.add(Restrictions.eq("idEvent", idEvent));
			criteria1.add(Restrictions.in("cdTask",
					new String[] { ServiceConstants.TASK_CODE_DAYCARE_SUB, ServiceConstants.TASK_CODE_DAYCARE_FSU,
							ServiceConstants.TASK_CODE_DAYCARE_FRE, ServiceConstants.TASK_CODE_DAYCARE_FPR,
							ServiceConstants.TASK_CODE_DAYCARE_INV, ServiceConstants.TASK_CODE_DAYCARE_AR }));
			Event event = (Event) criteria1.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(event)) {
				throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
			}
			event.setCdEventStatus(ServiceConstants.STAGE_CODE_COMP);
			sessionFactory.getCurrentSession().saveOrUpdate(event);

			Criteria criteria2 = sessionFactory.getCurrentSession().createCriteria(Approvers.class);
			criteria2.add(Restrictions.eq("idApproval", approvalId));
			List<Approvers> approverList = criteria2.list();
			if (TypeConvUtil.isNullOrEmpty(approverList)) {
				throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
			}
			for (Approvers approvers : approverList) {
				approvers.setCdApproversStatus(ServiceConstants.INVALID_APPROVAL_STATUS);
				sessionFactory.getCurrentSession().saveOrUpdate(approvers);
			}
		} else {
			throw new DataNotFoundException(lookupDao.getMessageByNumber(ServiceConstants.SQL_NOT_FOUND));
		}

		return approvalId;
	}

	/**
	 * Method Name: getAppEventId Method Description:Get Daycare Request
	 * Approval id event
	 * 
	 * @param idEvent
	 * @return Long @
	 */
	@Override
	public Long getAppEventId(Long idEvent) {
		BigDecimal eventId = BigDecimal.ZERO;
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAppEventId)
				.setParameter("idEvent", idEvent);
		List<BigDecimal> eventIdList = sQLQuery.list();
		if (TypeConvUtil.isNullOrEmpty(eventIdList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (BigDecimal eventIds : eventIdList) {
			eventId = eventIds;
		}
		return eventId.longValue();
	}

	/**
	 * Method Name: updateAppEventStatus Method Description:Update Daycare
	 * Approval event status to invalid
	 * 
	 * @param idApprovalEvent
	 * @return Long @
	 */
	@Override
	public Long updateAppEventStatus(Long idApprovalEvent) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("idEvent", idApprovalEvent));
		criteria.add(Restrictions.eq("cdEventType", ServiceConstants.EVENT_TYPE_APPROVAL));
		criteria.add(Restrictions.in("cdTask",
				new String[] { ServiceConstants.TASK_CODE_DAYCARE_APP_SUB, ServiceConstants.TASK_CODE_DAYCARE_APP_INV,
						ServiceConstants.TASK_CODE_DAYCARE_APP_FSU, ServiceConstants.TASK_CODE_DAYCARE_APP_FRE,
						ServiceConstants.TASK_CODE_DAYCARE_APP_FPR, ServiceConstants.TASK_CODE_DAYCARE_APP_AR }));
		Event event = (Event) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(event)) {
			throw new DataNotFoundException(
					messageSource.getMessage("dayCareRequestDao.updateAppEventStatus.NotFound", null, Locale.US));
		}
		event.setCdEventStatus(ServiceConstants.STAGE_CODE_COMP);
		sessionFactory.getCurrentSession().saveOrUpdate(event);
		return event.getIdEvent();
	}

	/**
	 * Method Name: retrieveSSCCDayCareRequest Method Description:This method
	 * retrieves SSCC DayCare Request Record using idDayCareRequest
	 * 
	 * @param idEvent
	 * @return SSCCDayCareRequestDto
	 */
	@Override
	public SSCCDayCareRequestDto retrieveSSCCDayCareRequest(Long idEvent) {

		List<SSCCDayCareRequestDto> ssccDayCareRequestDtoList = new ArrayList<>();
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrieveSSCCDayCareRequest)
				.addScalar("idDayCareRequest", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idlastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idSSCCDayCareRequest", StandardBasicTypes.LONG)
				.addScalar("cdStatus", StandardBasicTypes.STRING).addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(SSCCDayCareRequestDto.class));

		ssccDayCareRequestDtoList = sQLQuery.list();
		if (ObjectUtils.isEmpty(ssccDayCareRequestDtoList)) {
			return new SSCCDayCareRequestDto();
		}
		return ssccDayCareRequestDtoList.get(ServiceConstants.Zero);
	}

	/**
	 * Method Name: deleteSSCCDayCareRequest Method Description:This method
	 * deletes SSCC DayCare Request Record using idSSCCDayCareRequest
	 * 
	 * @param idSSCCDayCareRequest
	 * @return Long @
	 */
	@Override
	public Long deleteSSCCDayCareRequest(Long idSSCCDayCareRequest) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccDaycareRequest.class);
		criteria.add(Restrictions.eq("idSsccDaycareRequest", idSSCCDayCareRequest));
		SsccDaycareRequest ssccDaycareRequest = (SsccDaycareRequest) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(ssccDaycareRequest)) {
			throw new DataNotFoundException(
					messageSource.getMessage("dayCareRequestDao.deleteSSCCDayCareRequest.NotFound", null, Locale.US));
		}
		sessionFactory.getCurrentSession().delete(ssccDaycareRequest);
		return ssccDaycareRequest.getIdSsccDaycareRequest();
	}

	/**
	 * Method Name: deleteDayCareRequest Method Description:called complex
	 * delete procedure to delete daycare.
	 * 
	 * @param idEvent
	 * @return Long
	 */
	@Override
	public Long deleteDayCareRequest(Long idEvent) {

		Long value = ServiceConstants.LongZero;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteDayCareRequest)
				.setParameter("idEvent", idEvent);
		value = (long) sQLQuery1.executeUpdate();
		return value;
	}

	/**
	 * Method Name: fetchActiveReferralForChild Method Description:This function
	 * returns Active SSCC Referral for the Child.
	 * 
	 * @param idPerson
	 * @return Long @
	 */
	@Override
	public Long fetchActiveReferralForChild(Long idPerson) {
		Long idSsccReferral = ServiceConstants.LongZero;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferral.class);
		criteria.add(Restrictions.eq("idPerson", idPerson));
		criteria.add(Restrictions.in("cdRefStatus",
				new String[] { ServiceConstants.CSSCCSTA_40, ServiceConstants.CSSCCSTA_50 }));
		criteria.setProjection(Projections.max("idSSCCReferral"));
		idSsccReferral = (Long) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(idSsccReferral)) {
			idSsccReferral = ServiceConstants.LongZero;
		}

		return idSsccReferral;
	}

	/**
	 * Method Name: fetchReferralsForAllPersonsInDaycareRequest Method
	 * Description:This function returns All the Referrals(Active and Inactive)
	 * for the DayCare Request.
	 * 
	 * @param idDayCareRequest
	 * @return List<SSCCRefDto> @
	 */
	@Override
	public List<SSCCRefDto> fetchReferralsForAllPersonsInDaycareRequest(Long idDayCareRequest) {

		List<SSCCRefDto> ssccRefDtoList = null;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchReferralsForAllPersonsInDaycareRequest)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdRefStatus", StandardBasicTypes.STRING).setParameter("idDaycareRequest", idDayCareRequest)
				.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class));

		ssccRefDtoList = sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(ssccRefDtoList)) {
			ssccRefDtoList = new ArrayList<>();
		}
		return ssccRefDtoList;
	}

	/**
	 * Method Name: retrieveDCReqEventForSvcAuthEvent Method Description:This
	 * function returns Day Care Request Event Id for the given Service
	 * Auhtorization Event Id. If No Day Care Request Found, returns 0.
	 * 
	 * @param idSvcAuthEvent
	 * @returnLong @
	 */
	@Override
	public Long retrieveDCReqEventForSvcAuthEvent(Long idSvcAuthEvent) {
		Long idDayCareReqEvent = ServiceConstants.LongZero;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DaycareSvcAuthLink.class);
		criteria.add(Restrictions.eq("eventByIdSvcAuthEvent.idEvent", idSvcAuthEvent));
		List<DaycareSvcAuthLink> daycareSvcAuthLinkList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(daycareSvcAuthLinkList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("dayCareRequestDao.retrieveDCReqEventForSvcAuthEvent.NotFound", null, Locale.US));
		}
		for (DaycareSvcAuthLink daycareSvcAuthLink : daycareSvcAuthLinkList) {
			if (!TypeConvUtil.isNullOrEmpty(daycareSvcAuthLink.getEventByIdDaycareEvent().getIdEvent()))
				idDayCareReqEvent = daycareSvcAuthLink.getEventByIdDaycareEvent().getIdEvent();
		}
		return idDayCareReqEvent;
	}

	/**
	 * Method Name: getDayCareApprovers Method Description:This method returns
	 * array of Long values
	 * 
	 * @param idEvent
	 * @return Long[] @
	 */
	@Override
	public Long[] getDayCareApprovers(Long idEvent) {

		Long[] longValues = null;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getDayCareApprovers)
				.addScalar("idApprovalEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idApproval", StandardBasicTypes.LONG).addScalar("idApprovalPerson", StandardBasicTypes.LONG)
				.addScalar("txtApprovalTopic", StandardBasicTypes.STRING)
				.addScalar("dtApprovalDate", StandardBasicTypes.DATE).addScalar("idApprovers", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idTodo", StandardBasicTypes.LONG)
				.addScalar("cdApproversStatus", StandardBasicTypes.STRING)
				.addScalar("dtApproversDetermination", StandardBasicTypes.DATE)
				.addScalar("dtApproversRequested", StandardBasicTypes.DATE)
				.addScalar("indApproversHistorical", StandardBasicTypes.STRING)
				.addScalar("txtApproversCmnts", StandardBasicTypes.STRING)
				.addScalar("cdEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeFirst", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeMiddle", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeLast", StandardBasicTypes.STRING)
				.addScalar("cdEmployeeSuffix", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(DayCareApproversDto.class));

		List<DayCareApproversDto> dayCareApproversDtoList = sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(dayCareApproversDtoList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("dayCareRequestDao.getDayCareApprovers.NotFound", null, Locale.US));
		}
		Integer listSize = dayCareApproversDtoList.size();
		if (listSize > ServiceConstants.Zero) {
			longValues = new Long[listSize];
			Integer index = ServiceConstants.Zero;
			for (DayCareApproversDto dayCareApproversDto : dayCareApproversDtoList) {
				if (!TypeConvUtil.isNullOrEmpty(dayCareApproversDto.getIdPerson())) {
					longValues[index++] = dayCareApproversDto.getIdPerson();
				}
			}
		}
		return longValues;
	}

	/**
	 * 
	 * Method Name: populateDayCarePersonLink Method Description: This method
	 * populates dayCarePersonDto from financialDto Object.
	 * 
	 * @param financialDto
	 * @param dayCarePersonDto
	 */
	private void populateDayCarePersonLink(FinancialDto financialDto, DayCarePersonDto dayCarePersonDto) {

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto)) {
			dayCarePersonDto.setIdDaycarePersonLink(financialDto.getIdDayCarePersonLink());
			dayCarePersonDto.setIdDayCareRequest(financialDto.getIdDayCareRequest());
			dayCarePersonDto.setIdPerson(financialDto.getIdPerson());
			dayCarePersonDto.setCdPersonType(financialDto.getCdPersonType());
			dayCarePersonDto.setCdDetermService(financialDto.getCdDetermService());
			dayCarePersonDto.setCdRequestType(financialDto.getCdRequestType());
			dayCarePersonDto.setCdDayCareType(financialDto.getCdDaycareType());
			dayCarePersonDto.setDtBegin(financialDto.getDtBegin());
			dayCarePersonDto.setDtEnd(financialDto.getDtEnd());
			dayCarePersonDto.setIdFacilityActive(financialDto.getIdFacility());
			dayCarePersonDto.setDayHoursNeeded(financialDto.getTxtHoursNeeded());
			dayCarePersonDto.setIdSSCCReferral(financialDto.getIdSsccReferral());
			dayCarePersonDto.setIndSun(financialDto.getIndSun());
			dayCarePersonDto.setIndMon(financialDto.getIndMon());
			dayCarePersonDto.setIndTue(financialDto.getIndTue());
			dayCarePersonDto.setIndWed(financialDto.getIndWed());
			dayCarePersonDto.setIndThu(financialDto.getIndThu());
			dayCarePersonDto.setIndFri(financialDto.getIndFri());
			dayCarePersonDto.setIndSat(financialDto.getIndSat());
			dayCarePersonDto.setIndVarSch(financialDto.getIndVarSch());
			dayCarePersonDto.setCdVarSchMaxDays(financialDto.getCdVarSchMaxDays());
			dayCarePersonDto.setCdSummerType(financialDto.getCdSummerType());
			dayCarePersonDto.setCdWeekendType(financialDto.getCdWeekendType());
			dayCarePersonDto.setTxtComments(financialDto.getTxtComments());
			dayCarePersonDto.setDtLastUpdate(financialDto.getDtLastUpdate());
			dayCarePersonDto.setIdlastUpdatePerson(financialDto.getIdLastUpdatePerson());
			dayCarePersonDto.setDtCreated(financialDto.getDtCreated());
			dayCarePersonDto.setIdCreatedPerson(financialDto.getIdCreatedPerson());

		}
	}

	/**
	 * Method Name: updateDayCareRequestDetail Method Description:Updates
	 * Daycare request detail from DAYCARE_REQUEST table.
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long @
	 */
	@Override
	public Long updateDayCareRequestDetail(DayCareRequestDto dayCareRequestValueDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DaycareRequest.class);
		criteria.add(Restrictions.eq("idDaycareRequest", dayCareRequestValueDto.getIdDayCareRequest()));

		DaycareRequest daycareRequest = (DaycareRequest) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(daycareRequest)) {
			throw new DataNotFoundException(
					messageSource.getMessage("dayCareRequestDao.updateDayCareRequestDetail.NotFound", null, Locale.US));
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIdlastUpdatePerson())) {
			daycareRequest.setIdLastUpdatePerson(dayCareRequestValueDto.getIdlastUpdatePerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getIndInvalid())) {
			daycareRequest.setIndInvalid(dayCareRequestValueDto.getIndInvalid());
		}
		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getDtInvalid())) {
			daycareRequest.setDtInvalid(dayCareRequestValueDto.getDtInvalid());
		}
		if (!TypeConvUtil.isNullOrEmpty(dayCareRequestValueDto.getTxtCommentInvalid())) {
			daycareRequest.setTxtCommentInvalid(dayCareRequestValueDto.getTxtCommentInvalid());
		}

		sessionFactory.getCurrentSession().saveOrUpdate(daycareRequest);
		return daycareRequest.getIdDaycareRequest();

	}

	/**
	 * 
	 * Method Name: daycareSearch Method Description: This is DAO implementation
	 * layer for Day Care Search(CLASS)
	 * 
	 * @param searchDto
	 * @return List<DayCareSearchListDto>
	 */
	@Override
	public List<DayCareSearchListDto> daycareSearch(DayCareSearchListDto searchDto) {
		String queryStr = buildSqlFacilitySearch(searchDto, searchFacility, false);
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryStr)
				.addScalar("idFclty", StandardBasicTypes.LONG).addScalar("idFacility", StandardBasicTypes.LONG)
				.addScalar("cdFacilityType", StandardBasicTypes.STRING).addScalar("nbrRegion", StandardBasicTypes.LONG)
				.addScalar("nmFacility", StandardBasicTypes.STRING).addScalar("cdAddrType", StandardBasicTypes.STRING)
				.addScalar("nbrPhone", StandardBasicTypes.STRING).addScalar("nbrPhoneExt", StandardBasicTypes.STRING)
				.addScalar("addrLn1", StandardBasicTypes.STRING).addScalar("addrLn2", StandardBasicTypes.STRING)
				.addScalar("addrCity", StandardBasicTypes.STRING).addScalar("cdAddrState", StandardBasicTypes.STRING)
				.addScalar("addrZip1", StandardBasicTypes.STRING).addScalar("addrZip2", StandardBasicTypes.STRING)
				.addScalar("cdCounty", StandardBasicTypes.STRING).addScalar("nbrCellPhone", StandardBasicTypes.STRING)
				.addScalar("crcActn", StandardBasicTypes.INTEGER).addScalar("advrsnActn", StandardBasicTypes.INTEGER)
				.addScalar("crctvOrAdvrsActn", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(DayCareSearchListDto.class));
		List<DayCareSearchListDto> resultList = null;
		try {
			resultList = query.list();
		} catch (Exception e) {
			resultList = new ArrayList<>();
		}

		return resultList;

	}

	/**
	 * 
	 * Method Name: getFacilityById Method Description: This is DAO
	 * implementation layer for retrieving facility by facility id
	 * 
	 * @param idFacility
	 * @return DayCareSearchListDto
	 */
	@Override
	public DayCareSearchListDto getFacilityById(Long idFacility) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFacilityById)
				.addScalar("idFacility", StandardBasicTypes.LONG).addScalar("cdFacilityType", StandardBasicTypes.STRING)
				.addScalar("nbrRegion", StandardBasicTypes.LONG).addScalar("nmFacility", StandardBasicTypes.STRING)
				.addScalar("cdAddrType", StandardBasicTypes.STRING).addScalar("nbrPhone", StandardBasicTypes.STRING)
				.addScalar("nbrPhoneExt", StandardBasicTypes.STRING).addScalar("addrLn1", StandardBasicTypes.STRING)
				.addScalar("addrLn2", StandardBasicTypes.STRING).addScalar("addrCity", StandardBasicTypes.STRING)
				.addScalar("cdAddrState", StandardBasicTypes.STRING).addScalar("addrZip1", StandardBasicTypes.STRING)
				.addScalar("addrZip2", StandardBasicTypes.STRING).addScalar("cdCounty", StandardBasicTypes.STRING)
				.addScalar("nbrCellPhone", StandardBasicTypes.STRING).setParameter("idFacility", idFacility)
				.setResultTransformer(Transformers.aliasToBean(DayCareSearchListDto.class));
		List<DayCareSearchListDto> dayCareSearchList = query.list();
		DayCareSearchListDto dayCareSearchListDto = null;
		if (CollectionUtils.isNotEmpty(dayCareSearchList))
			dayCareSearchListDto = dayCareSearchList.get(0);
		return dayCareSearchListDto;

	}

	private String buildSqlFacilitySearch(DayCareSearchListDto searchDto, String stubSql, boolean omitAddrType) {
		StringBuilder sql = new StringBuilder();
		sql.append(stubSql);
		if (!ObjectUtils.isEmpty(searchDto.getIdFacility())
				&& !searchDto.getIdFacility().equals(ServiceConstants.ZERO)) {
			sql.append(ServiceConstants.SEARCH_BY_NBR_FCLTY);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(Long.toString(searchDto.getIdFacility()));
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		if (!StringUtils.isEmpty(searchDto.getAddrCity())) {
			sql.append(ServiceConstants.SEARCH_BY_ADDR_CITY);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(searchDto.getAddrCity().toUpperCase());
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		if (!StringUtils.isEmpty(searchDto.getCdFacilityType())) {
			sql.append(ServiceConstants.SEARCH_BY_FCLTY_TYPE);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(searchDto.getCdFacilityType());
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		if (!StringUtils.isEmpty(searchDto.getNmFacility())) {
			sql.append(ServiceConstants.SEARCH_BY_NM_FCLTY);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(9 >= searchDto.getNmFacility().length()
					? (new StringBuilder()).append(searchDto.getNmFacility().replaceAll("'","''").toUpperCase())
							.append(ServiceConstants.PERCENT).toString()
					: (new StringBuilder()).append(searchDto.getNmFacility().replaceAll("'","''").toUpperCase().substring(0, 9))
							.append(ServiceConstants.PERCENT).toString());
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		if (!omitAddrType && !StringUtils.isEmpty(searchDto.getCdAddrType())
				&& (ServiceConstants.ADDR_TYPE_L.equalsIgnoreCase(searchDto.getCdAddrType())
						|| ServiceConstants.ADDR_TYPE_M.equalsIgnoreCase(searchDto.getCdAddrType()))) {
			sql.append(ServiceConstants.SEARCH_BY_ADDR_TYPE);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(searchDto.getCdAddrType().toUpperCase());
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		if (!ObjectUtils.isEmpty(searchDto.getNbrRegion()) && !searchDto.getNbrRegion().equals(ServiceConstants.ZERO)) {
			sql.append(ServiceConstants.SEARCH_BY_NBR_REGION);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(Long.toString(searchDto.getNbrRegion()));
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		if (!StringUtils.isEmpty(searchDto.getNbrPhone())) {
			sql.append(ServiceConstants.SEARCH_BY_NBR_TLPHN);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(searchDto.getNbrPhone());
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		if (!StringUtils.isEmpty(searchDto.getAddrLn1())) {
			sql.append(ServiceConstants.SEARCH_BY_ADDR_LN1);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(13 >= searchDto.getAddrLn1().length()
					? (new StringBuilder()).append(searchDto.getAddrLn1().toUpperCase())
							.append(ServiceConstants.PERCENT).toString()
					: (new StringBuilder()).append(searchDto.getAddrLn1().toUpperCase().substring(0, 13))
							.append(ServiceConstants.PERCENT).toString());
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		if (!StringUtils.isEmpty(searchDto.getCdAddrState())) {
			sql.append(ServiceConstants.SEARCH_BY_ADDR_STATE);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(searchDto.getCdAddrState());
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		if (!StringUtils.isEmpty(searchDto.getAddrZip1())) {
			sql.append(ServiceConstants.SEARCH_BY_ADDR_ZIP1);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(searchDto.getAddrZip1());
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		if (!StringUtils.isEmpty(searchDto.getCdCounty())) {
			sql.append(ServiceConstants.SEARCH_BY_COUNTY);
			sql.append(ServiceConstants.SINGLE_QUOTES);
			sql.append(searchDto.getCdCounty());
			sql.append(ServiceConstants.SINGLE_QUOTES);
		}
		sql.append(ServiceConstants.ORDER_BY_NM_FCLTY);
		return sql.toString();
	}

	/**
	 * 
	 * Method Name: retrieveDayCareRequestDetail Method Description: This is Dao
	 * layer for retrieving DayCareRequest by event id
	 * 
	 * @param idEvent
	 * @return DayCareRequestDto
	 */
	public DayCareRequestDto retrieveDayCareRequestDetail(Long idEvent) {

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(retrieveDayCareRequestDetailSql)
				.addScalar("idDayCareRequest", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idlastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("indInvalid", StandardBasicTypes.STRING).addScalar("dtInvalid", StandardBasicTypes.DATE)
				.addScalar("txtCommentInvalid", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(DayCareRequestDto.class));

		DayCareRequestDto dayCareRequestDto = (DayCareRequestDto) query.uniqueResult();

		return dayCareRequestDto;

	}

	/**
	 * 
	 * Method Name: isDayCareRequestLinkedToServiceAuth Method Description:
	 * 
	 * @param idEvent
	 * @return
	 */
	public boolean isDayCareRequestLinkedToServiceAuth(Long idEvent) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DaycareSvcAuthLink.class);
		criteria.add(Restrictions.eq("eventByIdDaycareEvent.idEvent", idEvent));
		List<DaycareSvcAuthLink> daycareSvcAuthLinkList = criteria.list();
		return CollectionUtils.isNotEmpty(daycareSvcAuthLinkList);

	}

	/**
	 * 
	 * Method Name: generateServiceAuth Method Description:
	 * 
	 * @param idDayCareEvent
	 * @param idUser
	 * @param idSvcAuthEvent
	 * @return
	 */
	public Long generateServiceAuth(Long idDayCareEvent, Long idUser, Long idSvcAuthEvent) {

		DaycareSvcAuthLink daycareSvcAuthLink = new DaycareSvcAuthLink();
		daycareSvcAuthLink.setDtCreated(new Date());
		daycareSvcAuthLink.setDtLastUpdate(new Date());
		daycareSvcAuthLink.setIdCreatedPerson(idUser);
		daycareSvcAuthLink.setIdLastUpdatePerson(idUser);
		Event dayCareEvent = (Event) sessionFactory.getCurrentSession().get(Event.class, idDayCareEvent);
		Event svcAuthEvent = (Event) sessionFactory.getCurrentSession().get(Event.class, idSvcAuthEvent);
		daycareSvcAuthLink.setEventByIdDaycareEvent(dayCareEvent);
		daycareSvcAuthLink.setEventByIdSvcAuthEvent(svcAuthEvent);

		sessionFactory.getCurrentSession().saveOrUpdate(daycareSvcAuthLink);

		return daycareSvcAuthLink.getIdDaycareSvcAuthLink();

	}

	/**
	 * 
	 * Method Name: getStaffInformation Method Description: This is Dao
	 * implementation layer for retrieving StaffInformation by stage id
	 * 
	 * @param idStage
	 * @return StaffDto
	 */
	@Override
	public StaffDto getStaffInformation(Long idStage) {
		StaffDto staffPerson = populateStaff(idStage);

		StaffDto superVisor = populateSupervisorInfo(idStage);
		staffPerson.setIdSupervisor(superVisor.getIdSupervisor());
		staffPerson.setSupervisorName(superVisor.getSupervisorName());

		Query phoneQuery = populateStaffSuperPhone(staffPerson.getIdPerson(), superVisor.getIdSupervisor());

		List<StaffDto> staffAndSupervisior = (List<StaffDto>) phoneQuery.list();

		for (StaffDto staff : staffAndSupervisior) {
			if (staff.getIdPerson().equals(staffPerson.getIdPerson())) {
				staffPerson.setPersonPhone(staff.getPersonPhone());
				staffPerson.setPersonPhoneExt(staff.getPersonPhoneExt());
			}
			if (staff.getIdPerson().equals(superVisor.getIdSupervisor())) {
				staffPerson.setSupervisorPhone(staff.getPersonPhone());
				staffPerson.setSupervisorPhoneExt(staff.getPersonPhoneExt());
			}
		}

		return staffPerson;

	}

	/**
	 * Method Name: populateStaffSuperPhone Method Description:
	 * 
	 * @param staffPerson
	 * @param superVisor
	 * @return
	 */
	private Query populateStaffSuperPhone(Long idPerson, Long idSuperVisor) {
		Query phoneQuery = (Query) sessionFactory.getCurrentSession().createSQLQuery(populateStaffSuperPhone)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("personPhone", StandardBasicTypes.STRING)
				.addScalar("personPhoneExt", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setParameter("idSupervisor", idSuperVisor)
				.setResultTransformer(Transformers.aliasToBean(StaffDto.class));
		return phoneQuery;
	}

	/**
	 * Method Name: populateSupervisorInfo Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	private StaffDto populateSupervisorInfo(Long idStage) {
		Query superVisorquery = (Query) sessionFactory.getCurrentSession().createSQLQuery(populateSupervisorInfo)
				.addScalar("idSupervisor", StandardBasicTypes.LONG)
				.addScalar("supervisorName", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(StaffDto.class));

		StaffDto superVisor = (StaffDto) superVisorquery.list().get(0);
		return superVisor;
	}

	/**
	 * Method Name: populateStaff Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	private StaffDto populateStaff(Long idStage) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(populateStaffInfo)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("personName", StandardBasicTypes.STRING)
				.addScalar("personUnit", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(StaffDto.class));

		StaffDto staffPerson = (StaffDto) query.list().get(0);
		return staffPerson;
	}

	/**
	 * Method Name: getDaycareCodes Method Description: GET DAY CARE CODE
	 * 
	 * @param
	 * @return Map<String, String>
	 */
	@Override
	public Map<String, String> getDaycareCodes() {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getDaycareCodes)
				.addScalar("code", StandardBasicTypes.STRING).addScalar("decode", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CodeAttributes.class));
		List<CodeAttributes> codeList = query.list();
		Map<String, String> codeMap = new LinkedHashMap();
		codeList.stream().forEach(e -> codeMap.put(e.getCode(), e.getDecode()));
		return codeMap;
	}

	/**
	 * Method Name: dayCareService Method Description:
	 * 
	 * @param idDayCareRequest
	 * @return
	 */
	@Override
	public boolean dayCareService(Long idDayCareRequest) {

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(otherDayCareServices)
				.addScalar("dayCareRequestCount", StandardBasicTypes.LONG)
				.setParameter("idDayCareRequest", idDayCareRequest)
				.setResultTransformer(Transformers.aliasToBean(DayCareRequestCountOutDto.class));
		List<DayCareRequestCountOutDto> ListDPRCountDto = query.list();
		if (ObjectUtils.isEmpty(ListDPRCountDto)) {
			return false;
		}

		return true;
	}
	
	/**
	 * Method Name: isIcpcProgramSpecialist Method Description:This method is to
	 * check if whether a person is ICPC program specialist or not.
	 * 
	 * @param idDayCareRequest
	 * @return
	 */
	@Override
	public boolean isIcpcProgramSpecialist(Long idPerson) {

		Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(EmpSecClassLink.class);
		criteria1.add(Restrictions.eq("idPerson", idPerson));
		criteria1.add(Restrictions.eq("securityClass.cdSecurityClassName", ServiceConstants.PLUS_MOD_ICPC));
		List<EmpSecClassLink> empSecClassLink = (List<EmpSecClassLink>) criteria1.list();
		if (ObjectUtils.isEmpty(empSecClassLink)) {
			return false;
		}
		return true;
	}
}
