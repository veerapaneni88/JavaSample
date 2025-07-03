package us.tx.state.dfps.service.adoptionasstnc.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.AdoptionSubsidy;
import us.tx.state.dfps.common.domain.AdptEligApplication;
import us.tx.state.dfps.common.domain.AdptEligRecert;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.PersonLoc;
import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.service.admin.dao.AdminWorkerDao;
import us.tx.state.dfps.service.admin.dto.AdminWorkerInpDto;
import us.tx.state.dfps.service.admin.dto.AdminWorkerOutpDto;
import us.tx.state.dfps.service.adoptionasstnc.AdoptionAsstncDto;
import us.tx.state.dfps.service.adoptionasstnc.dao.AdoptionAsstncDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This method
 * implements AdoptionAsstncDaoImpl Oct 31, 2017- 2:11:29 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class AdoptionAsstncDaoImpl implements AdoptionAsstncDao {

	@Value("${AdoptionAsstncDaoImpl.fetchActiveAdpForPerson}")
	private String fetchActiveAdpForPersonSql;

	@Value("${AdoptionAsstncDaoImpl.getAdptSubsidyEventList}")
	private String getAdptSubsidyEventListSql;

	@Value("${AdoptionAsstncDao.adoptionPlacementDate}")
	private String adoptionPlacementDateSql;

	@Value("${AdoptionAsstncDaoImpl.fetchLatestOpenAdptAsstncRecord}")
	private String fetchLatestOpenAdptAsstncRecordSql;

	@Value("${AdoptionAsstncDaoImpl.getAlocOnAdptAssistAgrmntSignDt}")
	private String getAlocOnAdptAssistAgrmntSignDtSql;

	@Value("${AdoptionAsstncDaoImpl.getAdoptAssistRsnClosure}")
	private String getAdoptAssistRsnClosureSql;

	@Value("${AdoptionAsstncDaoImpl.getAdptPlcmtInfo}")
	private String getAdptPlcmtInfoSql;

	@Value("${AdoptionAsstncDaoImpl.fetchADOPlacements}")
	private String fetchADOPlacementsSql;

	@Value("${AdoptionAsstncDaoImpl.getAdoProcessStatus}")
	private String getAdoProcessStatusSql;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	StageDao stageDao;

	@Autowired
	AdminWorkerDao adminWorkerDao;

	@Autowired
	LookupDao lookupDao;

	private static final Logger log = Logger.getLogger(AdoptionAsstncDaoImpl.class);

	/**
	 * Method Name: fetchActiveAdpForPerson Method Description:Fetches the
	 * Active adoption assistance record for the given person id if one exists.
	 * 
	 * @param idPerson
	 * @return List<AdoptionAsstncDto> @
	 */
	@Override
	public List<AdoptionAsstncDto> fetchActiveAdpForPerson(Long personId) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchActiveAdpForPersonSql)
				.addScalar("adoptionAsstncId", StandardBasicTypes.LONG)
				.addScalar("adoptionAsstncDateLastUpdate", StandardBasicTypes.DATE)
				.addScalar("personId", StandardBasicTypes.LONG).addScalar("payeeId", StandardBasicTypes.LONG)
				.addScalar("placementEvenId", StandardBasicTypes.LONG)
				.addScalar("adoptionAsstncAmount", StandardBasicTypes.DOUBLE)
				.addScalar("closureReasonCode", StandardBasicTypes.STRING)
				.addScalar("adoptionAsstncTypeCode", StandardBasicTypes.STRING)
				.addScalar("dateAgreementReturned", StandardBasicTypes.DATE)
				.addScalar("dateAgreementSent", StandardBasicTypes.DATE)
				.addScalar("dateApplicationReturned", StandardBasicTypes.DATE)
				.addScalar("dateApplicationSent", StandardBasicTypes.DATE)
				.addScalar("dateApproved", StandardBasicTypes.DATE).addScalar("dateStart", StandardBasicTypes.DATE)
				.addScalar("dateEnd", StandardBasicTypes.DATE).addScalar("dateLastInvoice", StandardBasicTypes.DATE)
				.addScalar("thirdPartyInsurance", StandardBasicTypes.STRING)
				.addScalar("ipreviouslyProcessed", StandardBasicTypes.STRING)
				.addScalar("reasonNeeded", StandardBasicTypes.STRING)
				.addScalar("idAdptEligApplication", StandardBasicTypes.LONG)
				.addScalar("idAdptEligRecert", StandardBasicTypes.LONG)
				.addScalar("dtNextRecert", StandardBasicTypes.DATE)
				.addScalar("cdWithdrawRsn", StandardBasicTypes.STRING).addScalar("dtWithdraw", StandardBasicTypes.DATE)
				.addScalar("txtWithdrawRsn", StandardBasicTypes.STRING)
				.addScalar("indEligOverride", StandardBasicTypes.STRING).setParameter("idPerson", personId)
				.setResultTransformer(Transformers.aliasToBean(AdoptionAsstncDto.class));

		List<AdoptionAsstncDto> adoptionAsstncDtoList = query.list();
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("AdoptionAsstncDto.not.found", null, Locale.US));
		}
		for (AdoptionAsstncDto adoptionAsstncDto : adoptionAsstncDtoList) {
			if (!TypeConvUtil.isNullOrEmpty(adoptionAsstncDto.getThirdPartyInsurance())
					&& adoptionAsstncDto.getThirdPartyInsurance().equals(ServiceConstants.Y)) {
				adoptionAsstncDto.setHasThirdPartyInsurance(Boolean.TRUE);
				adoptionAsstncDto.setThirdPartyInsurance(null);
			} else {
				adoptionAsstncDto.setHasThirdPartyInsurance(Boolean.FALSE);
				adoptionAsstncDto.setThirdPartyInsurance(null);
			}
			if (!TypeConvUtil.isNullOrEmpty(adoptionAsstncDto.getIpreviouslyProcessed())
					&& adoptionAsstncDto.getIpreviouslyProcessed().equals(ServiceConstants.Y)) {
				adoptionAsstncDto.setPreviouslyProcessed(Boolean.TRUE);
				adoptionAsstncDto.setIpreviouslyProcessed(null);
			} else {
				adoptionAsstncDto.setPreviouslyProcessed(Boolean.FALSE);
				adoptionAsstncDto.setIpreviouslyProcessed(null);
			}
		}
		return adoptionAsstncDtoList;

	}

	/**
	 * Method Name: endDateAdoptionSubsidy Method Description:This method set
	 * the end date on adoption subsidy record
	 * 
	 * @param adoptionAsstncDto
	 * @param dtSubsidyEnd
	 * @param cdSubsidyEndReason
	 * @return int
	 */
	@Override
	public int endDateAdoptionSubsidy(AdoptionAsstncDto adoptionAsstncDto, Date dtSubsidyEnd,
			String cdSubsidyEndReason) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdoptionSubsidy.class);
		criteria.add(Restrictions.eq("idAdptSub", adoptionAsstncDto.getAdoptionAsstncId()));
		criteria.add(Restrictions.le("dtLastUpdate", adoptionAsstncDto.getAdoptionAsstncDateLastUpdate()));

		AdoptionSubsidy adoptionSubsidy = (AdoptionSubsidy) criteria.uniqueResult();

		adoptionSubsidy.setDtAdptSubEnd(dtSubsidyEnd);
		adoptionSubsidy.setCdAdptSubCloseRsn(cdSubsidyEndReason);

		sessionFactory.getCurrentSession().saveOrUpdate(adoptionSubsidy);
		if (adoptionSubsidy != null)
			return 1;
		return 0;
	}

	/**
	 * Method Name: getAdptSubsidyEventList Method Description:This method
	 * fetches the list of events associated with a adoption_subsidy record
	 * 
	 * @param idAdptSub
	 * @return List<EventDto>
	 */
	@Override
	public List<EventDto> getAdptSubsidyEventList(int idAdptSub) {

		List<EventDto> eventDtoList = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAdptSubsidyEventListSql).addScalar("idEvent")
				.addScalar("idCase").setParameter("idAdptSub", idAdptSub)
				.setResultTransformer(Transformers.aliasToBean(EventDto.class));

		eventDtoList = query.list();
		return eventDtoList;

	}

	/**
	 * Method Name: queryEarliestAdoptionAsstncRecord Method Description:Queries
	 * the earliest adoption assistance record for the given person id and
	 * resource id combination, if one exists.
	 * 
	 * @param personId
	 * @param resourceId
	 * @return AdoptionAsstncDto @
	 */
	@Override
	public AdoptionAsstncDto queryEarliestAdoptionAsstncRecord(Long personId, Long resourceId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdoptionSubsidy.class);
		criteria.add(Restrictions.eq("person.idPerson", personId));
		criteria.add(Restrictions.eq("capsResource.idResource", resourceId));
		criteria.addOrder(Order.asc("dtAdptSubEffective"));
		List<AdoptionSubsidy> adoptionSubsidieList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(adoptionSubsidieList)) {
			throw new DataNotFoundException(messageSource.getMessage("AdoptionSubsidy.NotFound", null, Locale.US));
		}
		AdoptionSubsidy adoptionSubsidy = adoptionSubsidieList.get(ServiceConstants.Zero);
		AdoptionAsstncDto earliestAdoptionAsstncRecord = new AdoptionAsstncDto();
		earliestAdoptionAsstncRecord.setAdoptionAsstncId(adoptionSubsidy.getIdAdptSub());
		earliestAdoptionAsstncRecord.setAdoptionAsstncDateLastUpdate(adoptionSubsidy.getDtLastUpdate());
		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getPerson())) {
			earliestAdoptionAsstncRecord.setPersonId(adoptionSubsidy.getPerson().getIdPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getCapsResource())) {
			earliestAdoptionAsstncRecord.setPayeeId(adoptionSubsidy.getCapsResource().getIdResource());
		}
		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getPlacement())) {
			earliestAdoptionAsstncRecord.setPlacementEvenId(adoptionSubsidy.getPlacement().getIdPlcmtEvent());
		}
		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAmtAdptSub())) {
			earliestAdoptionAsstncRecord.setAdoptionAsstncAmount(adoptionSubsidy.getAmtAdptSub().doubleValue());
		}
		earliestAdoptionAsstncRecord.setClosureReasonCode(adoptionSubsidy.getCdAdptSubCloseRsn());
		earliestAdoptionAsstncRecord.setAdoptionAsstncTypeCode(adoptionSubsidy.getCdAdptSubDeterm());
		earliestAdoptionAsstncRecord.setDateAgreementReturned(adoptionSubsidy.getDtAdptSubAgreeRetn());
		earliestAdoptionAsstncRecord.setDateAgreementSent(adoptionSubsidy.getDtAdptSubAgreeSent());
		earliestAdoptionAsstncRecord.setDateApplicationReturned(adoptionSubsidy.getDtAdptSubAgreeRetn());
		earliestAdoptionAsstncRecord.setDateApplicationSent(adoptionSubsidy.getDtAdptSubAppSent());
		earliestAdoptionAsstncRecord.setDateApproved(adoptionSubsidy.getDtAdptSubApprvd());
		earliestAdoptionAsstncRecord.setDateStart(adoptionSubsidy.getDtAdptSubEffective());
		earliestAdoptionAsstncRecord.setDateEnd(adoptionSubsidy.getDtAdptSubEnd());
		earliestAdoptionAsstncRecord.setDateLastInvoice(adoptionSubsidy.getDtAdptSubLastInvc());
		earliestAdoptionAsstncRecord.setHasThirdPartyInsurance(toBoolean(adoptionSubsidy.getIndAdptSubThirdParty()));
		earliestAdoptionAsstncRecord.setPreviouslyProcessed(toBoolean(adoptionSubsidy.getIndAdptSubProcess()));
		earliestAdoptionAsstncRecord.setReasonNeeded(adoptionSubsidy.getTxtAdptSubRsn());
		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAdptEligApplication())) {
			earliestAdoptionAsstncRecord
					.setIdAdptEligApplication(adoptionSubsidy.getAdptEligApplication().getIdAdptEligApplication());
		}

		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAdptEligRecert())) {
			earliestAdoptionAsstncRecord.setIdAdptEligRecert(adoptionSubsidy.getAdptEligRecert().getIdAdptEligRecert());
		}
		earliestAdoptionAsstncRecord.setDtNextRecert(adoptionSubsidy.getDtNextRecert());
		earliestAdoptionAsstncRecord.setCdWithdrawRsn(adoptionSubsidy.getCdWithdrawRsn());
		earliestAdoptionAsstncRecord.setTxtWithdrawRsn(adoptionSubsidy.getTxtWithdrawRsn());
		earliestAdoptionAsstncRecord.setDtWithdraw(adoptionSubsidy.getDtWithdraw());
		earliestAdoptionAsstncRecord.setIndEligOverride(adoptionSubsidy.getIndEligOverride());
		return earliestAdoptionAsstncRecord;
	}

	private Boolean toBoolean(String value) {
		if (!TypeConvUtil.isNullOrEmpty(value) && value.equals(ServiceConstants.Y)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Method Name: queryAlocWithGreatestStartDate Method Description:Queries
	 * the child's ALOC with the greatest start date, whether active or not.
	 * 
	 * @param personId
	 * @return String @
	 */
	@Override
	public String queryAlocWithGreatestStartDate(Long personId) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonLoc.class);
		criteria.add(Restrictions.eq("cdPlocType", ServiceConstants.CPLOCELG_ALOC));
		criteria.add(Restrictions.eq("person.idPerson", personId));
		criteria.addOrder(Order.desc("dtPlocStart"));
		List<PersonLoc> personLocList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(personLocList)) {
			throw new DataNotFoundException(messageSource.getMessage("PersonLoc.NotFound", null, Locale.US));
		}
		return personLocList.get(ServiceConstants.Zero).getCdPlocChild();
	}

	/**
	 * Method Name: queryPlacementWithGreatestStartDate Method
	 * Description:Queries the child's Placement with the greatest start date
	 * 
	 * @param personId
	 * @param resourceId
	 * @return Date @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PlacementDto queryPlacementWithGreatestStartDate(Long personId, Long resourceId) {
		PlacementDto placementDto = new PlacementDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Placement.class);
		criteria.add(Restrictions.eq("personByIdPlcmtChild.idPerson", personId));
		criteria.add(Restrictions.eq("capsResourceByIdRsrcFacil.idResource", resourceId));
		criteria.add(Restrictions.eq("dtPlcmtEnd", DateUtils.getDefaultFutureDate()));
		criteria.add(Restrictions.eq("cdPlcmtActPlanned", ServiceConstants.A));
		criteria.addOrder(Order.desc("dtPlcmtStart"));
		criteria.addOrder(Order.desc("idPlcmtEvent"));
		Placement placement = (Placement) criteria.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(placement)) {
			BeanUtils.copyProperties(placementDto, placement);
		}

		return placementDto;
	}

	/**
	 * Method Name: adoptionPlacementDate Method Description:Queries the child's
	 * most recent ADO Placement start date(for the person).
	 * 
	 * @param personId
	 * @return Date @
	 */
	@Override
	public Date adoptionPlacementDate(Long personId) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(adoptionPlacementDateSql);
		query.setParameter("idPlcmtChild", personId);
		List<Date> adoptionPlcmtDateList = query.list();
		if (TypeConvUtil.isNullOrEmpty(adoptionPlcmtDateList)) {
			throw new DataNotFoundException(messageSource.getMessage("AdoptionPlcmtDate.NotFound", null, Locale.US));
		}
		return adoptionPlcmtDateList.get(ServiceConstants.Zero);
	}

	/**
	 * Method Name: fetchLatestOpenAdptAsstncRecord Method Description: Fetches
	 * the latest open in PROC state - adoption assistance record for the given
	 * person id if one exists.
	 * 
	 * @param idPerson
	 * @return Long @
	 */
	@Override
	public Long fetchLatestOpenAdptAsstncRecord(Long idPerson) {
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(fetchLatestOpenAdptAsstncRecordSql);
		sqlQuery.setParameter("idPerson", idPerson);
		List<BigDecimal> idAdptSubList = sqlQuery.list();
		if (TypeConvUtil.isNullOrEmpty(idAdptSubList)
				|| TypeConvUtil.isNullOrEmpty(idAdptSubList.get(ServiceConstants.Zero))) {
			throw new DataNotFoundException(messageSource.getMessage("idAdptSubList.NotFound", null, Locale.US));
		}
		return idAdptSubList.get(ServiceConstants.Zero).longValue();
	}

	/**
	 * Method Name: fetchAdptAsstncRecord Method Description: Fetches the
	 * adoption assistance record for the given idAdptSub
	 * 
	 * @param idAdptSub
	 * @return AdoptionAsstncDto @
	 */
	@Override
	public AdoptionAsstncDto fetchAdptAsstncRecord(Long idAdptSub) {
		AdoptionSubsidy adoptionSubsidy = (AdoptionSubsidy) sessionFactory.getCurrentSession()
				.get(AdoptionSubsidy.class, idAdptSub);
		if (TypeConvUtil.isNullOrEmpty(adoptionSubsidy)) {
			throw new DataNotFoundException(messageSource.getMessage("AdoptionSubsidy.NotFound", null, Locale.US));
		}
		AdoptionAsstncDto adoptionAsstncDto = new AdoptionAsstncDto();
		adoptionAsstncDto.setAdoptionAsstncId(adoptionSubsidy.getIdAdptSub());
		adoptionAsstncDto.setAdoptionAsstncDateLastUpdate(adoptionSubsidy.getDtLastUpdate());
		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getPerson())) {
			adoptionAsstncDto.setPersonId(adoptionSubsidy.getPerson().getIdPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getCapsResource())) {
			adoptionAsstncDto.setPayeeId(adoptionSubsidy.getCapsResource().getIdResource());
		}
		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getPlacement())) {
			adoptionAsstncDto.setPlacementEvenId(adoptionSubsidy.getPlacement().getIdPlcmtEvent());
		}
		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAmtAdptSub())) {
			adoptionAsstncDto.setAdoptionAsstncAmount(adoptionSubsidy.getAmtAdptSub().doubleValue());
		}
		adoptionAsstncDto.setClosureReasonCode(adoptionSubsidy.getCdAdptSubCloseRsn());
		adoptionAsstncDto.setAdoptionAsstncTypeCode(adoptionSubsidy.getCdAdptSubDeterm());
		adoptionAsstncDto.setDateAgreementReturned(adoptionSubsidy.getDtAdptSubAgreeRetn());
		adoptionAsstncDto.setDateAgreementSent(adoptionSubsidy.getDtAdptSubAgreeSent());
		adoptionAsstncDto.setDateApplicationReturned(adoptionSubsidy.getDtAdptSubAgreeRetn());
		adoptionAsstncDto.setDateApplicationSent(adoptionSubsidy.getDtAdptSubAppSent());
		adoptionAsstncDto.setDateApproved(adoptionSubsidy.getDtAdptSubApprvd());
		adoptionAsstncDto.setDateStart(adoptionSubsidy.getDtAdptSubEffective());
		adoptionAsstncDto.setDateEnd(adoptionSubsidy.getDtAdptSubEnd());
		adoptionAsstncDto.setDateLastInvoice(adoptionSubsidy.getDtAdptSubLastInvc());
		adoptionAsstncDto.setHasThirdPartyInsurance(toBoolean(adoptionSubsidy.getIndAdptSubThirdParty()));
		adoptionAsstncDto.setPreviouslyProcessed(toBoolean(adoptionSubsidy.getIndAdptSubProcess()));
		adoptionAsstncDto.setReasonNeeded(adoptionSubsidy.getTxtAdptSubRsn());
		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAdptEligApplication())) {
			adoptionAsstncDto
					.setIdAdptEligApplication(adoptionSubsidy.getAdptEligApplication().getIdAdptEligApplication());
		}

		if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAdptEligRecert())) {
			adoptionAsstncDto.setIdAdptEligRecert(adoptionSubsidy.getAdptEligRecert().getIdAdptEligRecert());
		}
		adoptionAsstncDto.setDtNextRecert(adoptionSubsidy.getDtNextRecert());
		adoptionAsstncDto.setCdWithdrawRsn(adoptionSubsidy.getCdWithdrawRsn());
		adoptionAsstncDto.setTxtWithdrawRsn(adoptionSubsidy.getTxtWithdrawRsn());
		adoptionAsstncDto.setDtWithdraw(adoptionSubsidy.getDtWithdraw());
		adoptionAsstncDto.setIndEligOverride(adoptionSubsidy.getIndEligOverride());
		return adoptionAsstncDto;

	}

	/**
	 * Method Name: fetchAllAdptAsstncRecord Method Description: Fetches the all
	 * the adoption assistance record for the given person id if one exists.
	 * 
	 * @param personId
	 * @return List<AdoptionAsstncDto> @
	 */
	@Override
	public List<AdoptionAsstncDto> fetchAllAdptAsstncRecord(Long personId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdoptionSubsidy.class);
		criteria.add(Restrictions.eq("person.idPerson", personId));
		criteria.addOrder(Order.desc("idAdptSub"));
		List<AdoptionSubsidy> adoptionSubsidieList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(adoptionSubsidieList)) {
			throw new DataNotFoundException(messageSource.getMessage("AdoptionSubsidy.NotFound", null, Locale.US));
		}
		List<AdoptionAsstncDto> adoptionAsstncDtoList = new ArrayList<AdoptionAsstncDto>();
		for (AdoptionSubsidy adoptionSubsidy : adoptionSubsidieList) {
			AdoptionAsstncDto adoptionAsstncDto = new AdoptionAsstncDto();
			adoptionAsstncDto.setAdoptionAsstncId(adoptionSubsidy.getIdAdptSub());
			adoptionAsstncDto.setAdoptionAsstncDateLastUpdate(adoptionSubsidy.getDtLastUpdate());
			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getPerson())) {
				adoptionAsstncDto.setPersonId(adoptionSubsidy.getPerson().getIdPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getCapsResource())) {
				adoptionAsstncDto.setPayeeId(adoptionSubsidy.getCapsResource().getIdResource());
			}
			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getPlacement())) {
				adoptionAsstncDto.setPlacementEvenId(adoptionSubsidy.getPlacement().getIdPlcmtEvent());
			}
			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAmtAdptSub())) {
				adoptionAsstncDto.setAdoptionAsstncAmount(adoptionSubsidy.getAmtAdptSub().doubleValue());
			}
			adoptionAsstncDto.setClosureReasonCode(adoptionSubsidy.getCdAdptSubCloseRsn());
			adoptionAsstncDto.setAdoptionAsstncTypeCode(adoptionSubsidy.getCdAdptSubDeterm());
			adoptionAsstncDto.setDateAgreementReturned(adoptionSubsidy.getDtAdptSubAgreeRetn());
			adoptionAsstncDto.setDateAgreementSent(adoptionSubsidy.getDtAdptSubAgreeSent());
			adoptionAsstncDto.setDateApplicationReturned(adoptionSubsidy.getDtAdptSubAgreeRetn());
			adoptionAsstncDto.setDateApplicationSent(adoptionSubsidy.getDtAdptSubAppSent());
			adoptionAsstncDto.setDateApproved(adoptionSubsidy.getDtAdptSubApprvd());
			adoptionAsstncDto.setDateStart(adoptionSubsidy.getDtAdptSubEffective());
			adoptionAsstncDto.setDateEnd(adoptionSubsidy.getDtAdptSubEnd());
			adoptionAsstncDto.setDateLastInvoice(adoptionSubsidy.getDtAdptSubLastInvc());
			adoptionAsstncDto.setHasThirdPartyInsurance(toBoolean(adoptionSubsidy.getIndAdptSubThirdParty()));
			adoptionAsstncDto.setPreviouslyProcessed(toBoolean(adoptionSubsidy.getIndAdptSubProcess()));
			adoptionAsstncDto.setReasonNeeded(adoptionSubsidy.getTxtAdptSubRsn());
			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAdptEligApplication())) {
				adoptionAsstncDto
						.setIdAdptEligApplication(adoptionSubsidy.getAdptEligApplication().getIdAdptEligApplication());
			}

			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAdptEligRecert())) {
				adoptionAsstncDto.setIdAdptEligRecert(adoptionSubsidy.getAdptEligRecert().getIdAdptEligRecert());
			}
			adoptionAsstncDto.setDtNextRecert(adoptionSubsidy.getDtNextRecert());
			adoptionAsstncDto.setCdWithdrawRsn(adoptionSubsidy.getCdWithdrawRsn());
			adoptionAsstncDto.setTxtWithdrawRsn(adoptionSubsidy.getTxtWithdrawRsn());
			adoptionAsstncDto.setDtWithdraw(adoptionSubsidy.getDtWithdraw());
			adoptionAsstncDto.setIndEligOverride(adoptionSubsidy.getIndEligOverride());
			adoptionAsstncDtoList.add(adoptionAsstncDto);
		}
		return adoptionAsstncDtoList;
	}

	/**
	 * Method Name: isAdptAsstncCreatedPostAugRollout Method Description:Returns
	 * true if the Adoption Assistance Record was created after august rollout.
	 * 
	 * @param idAdptSub
	 * @return Boolean @
	 */
	/**
	 * Method Name: isAdptAsstncCreatedPostAugRollout Method Description:Returns
	 * true if the Adoption Assistance Record was created after august rollout.
	 * 
	 * @param idAdptSub
	 * @return Boolean @
	 */
	@Override
	public Boolean isAdptAsstncCreatedPostAugRollout(Long idAdptSub) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(ServiceConstants.AdptAsstncCreatedPostAugRolloutSql);
		stringBuilder.append(ServiceConstants.SINGLE_QUOTES);
		stringBuilder.append(idAdptSub);
		stringBuilder.append(ServiceConstants.SINGLE_QUOTES);
		stringBuilder.append(ServiceConstants.AdptAsstncCreatedPostAugRollSql);

		stringBuilder.append(ServiceConstants.To_Date);
		stringBuilder.append(
				lookupDao.simpleDecodeSafe(ServiceConstants.CRELDATE, ServiceConstants.CRELDATE_AUG_2010_IMPACT));

		stringBuilder.append(ServiceConstants.To_Day);

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		List<BigDecimal> list = query.list();
		if (!TypeConvUtil.isNullOrEmpty(list)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;

	}

	/**
	 * Method Name: getAdoptAssistForRsrcAndChild Method Description:Fetches the
	 * list of AdoptionAsstncValueBean with the for the Resource and Child
	 * Combination if one exists.
	 * 
	 * @param personId
	 * @param idResource
	 * @return List<AdoptionAsstncDto> @
	 */
	@Override
	public List<AdoptionAsstncDto> getAdoptAssistForRsrcAndChild(Long personId, Long idResource) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdoptionSubsidy.class);
		criteria.add(Restrictions.eq("person.idPerson", personId));
		criteria.add(Restrictions.eq("capsResource.idResource", idResource));
		criteria.add(Restrictions.neProperty("dtAdptSubEffective", "dtAdptSubEnd"));
		criteria.addOrder(Order.desc("idAdptSub"));
		List<AdoptionSubsidy> adoptionSubsidieList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(adoptionSubsidieList)) {
			return null;
		}
		List<AdoptionAsstncDto> adoptionAsstncDtoList = new ArrayList<AdoptionAsstncDto>();
		for (AdoptionSubsidy adoptionSubsidy : adoptionSubsidieList) {
			AdoptionAsstncDto adoptionAsstncDto = new AdoptionAsstncDto();
			adoptionAsstncDto.setAdoptionAsstncId(adoptionSubsidy.getIdAdptSub());
			adoptionAsstncDto.setAdoptionAsstncDateLastUpdate(adoptionSubsidy.getDtLastUpdate());
			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getPerson())) {
				adoptionAsstncDto.setPersonId(adoptionSubsidy.getPerson().getIdPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getCapsResource())) {
				adoptionAsstncDto.setPayeeId(adoptionSubsidy.getCapsResource().getIdResource());
			}
			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getPlacement())) {
				adoptionAsstncDto.setPlacementEvenId(adoptionSubsidy.getPlacement().getIdPlcmtEvent());
			}
			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAmtAdptSub())) {
				adoptionAsstncDto.setAdoptionAsstncAmount(adoptionSubsidy.getAmtAdptSub().doubleValue());
			}
			adoptionAsstncDto.setClosureReasonCode(adoptionSubsidy.getCdAdptSubCloseRsn());
			adoptionAsstncDto.setAdoptionAsstncTypeCode(adoptionSubsidy.getCdAdptSubDeterm());
			adoptionAsstncDto.setDateAgreementReturned(adoptionSubsidy.getDtAdptSubAgreeRetn());
			adoptionAsstncDto.setDateAgreementSent(adoptionSubsidy.getDtAdptSubAgreeSent());
			adoptionAsstncDto.setDateApplicationReturned(adoptionSubsidy.getDtAdptSubAgreeRetn());
			adoptionAsstncDto.setDateApplicationSent(adoptionSubsidy.getDtAdptSubAppSent());
			adoptionAsstncDto.setDateApproved(adoptionSubsidy.getDtAdptSubApprvd());
			adoptionAsstncDto.setDateStart(adoptionSubsidy.getDtAdptSubEffective());
			adoptionAsstncDto.setDateEnd(adoptionSubsidy.getDtAdptSubEnd());
			adoptionAsstncDto.setDateLastInvoice(adoptionSubsidy.getDtAdptSubLastInvc());
			adoptionAsstncDto.setHasThirdPartyInsurance(toBoolean(adoptionSubsidy.getIndAdptSubThirdParty()));
			adoptionAsstncDto.setPreviouslyProcessed(toBoolean(adoptionSubsidy.getIndAdptSubProcess()));
			adoptionAsstncDto.setReasonNeeded(adoptionSubsidy.getTxtAdptSubRsn());
			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAdptEligApplication())) {
				adoptionAsstncDto
						.setIdAdptEligApplication(adoptionSubsidy.getAdptEligApplication().getIdAdptEligApplication());
			}

			if (!TypeConvUtil.isNullOrEmpty(adoptionSubsidy.getAdptEligRecert())) {
				adoptionAsstncDto.setIdAdptEligRecert(adoptionSubsidy.getAdptEligRecert().getIdAdptEligRecert());
			}
			adoptionAsstncDto.setDtNextRecert(adoptionSubsidy.getDtNextRecert());
			adoptionAsstncDto.setCdWithdrawRsn(adoptionSubsidy.getCdWithdrawRsn());
			adoptionAsstncDto.setTxtWithdrawRsn(adoptionSubsidy.getTxtWithdrawRsn());
			adoptionAsstncDto.setDtWithdraw(adoptionSubsidy.getDtWithdraw());
			adoptionAsstncDto.setIndEligOverride(adoptionSubsidy.getIndEligOverride());
			adoptionAsstncDtoList.add(adoptionAsstncDto);
		}
		return adoptionAsstncDtoList;
	}

	/**
	 * Method Name: getAlocOnAdptAssistAgrmntSignDt Method Description:Retrieves
	 * the Authorized Level of Care (ALOC) on the day when Adoption Assist
	 * Agreement was Signed.
	 * 
	 * @param personId
	 * @param dtAdptAsstAgreement
	 * @return String @
	 */
	@Override
	public String getAlocOnAdptAssistAgrmntSignDt(Long personId, Date dtAdptAsstAgreement) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getAlocOnAdptAssistAgrmntSignDtSql);
		query.setParameter("cdPlocType", ServiceConstants.CPLOCELG_ALOC);
		query.setParameter("idPerson", personId);
		query.setParameter("dtAdptAsstAgreement", dtAdptAsstAgreement);

		List<String> cdPLOCChildList = query.list();
		if (TypeConvUtil.isNullOrEmpty(cdPLOCChildList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("AuthorizedLevelofCare.NotFound", null, Locale.US));
		}
		return cdPLOCChildList.get(ServiceConstants.Zero);
	}

	/**
	 * Method Name: getAdoptAssistForStage Method Description:Fetches the list
	 * of adoption subsidy events that are in PROC status
	 * 
	 * @param idStage
	 * @return List<Long> @
	 */
	@Override
	public List<Long> getAdoptAssistForStage(Long idStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("cdEventType", ServiceConstants.SUBSIDY_TYPE));
		criteria.add(Restrictions.eq("cdEventStatus", ServiceConstants.PROCESS_EVENT_STATUS));
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		List<Event> eventList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(eventList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("AdoptionSubsidyEvents.NotFound", null, Locale.US));
		}
		List<Long> adoptionSubsidyEventList = new ArrayList<>();
		for (Event event : eventList) {
			adoptionSubsidyEventList.add(event.getIdEvent());
		}
		return adoptionSubsidyEventList;
	}

	/**
	 * Method Name: getAdoptAssistRsnClosure Method Description:Fetches the
	 * closure reason for the adoption subsidy for that particular event Id
	 * 
	 * @param idEvent
	 * @return AdoptionAsstncDto @
	 */
	@Override
	public AdoptionAsstncDto getAdoptAssistRsnClosure(Long idEvent) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAdoptAssistRsnClosureSql)
				.addScalar("closureReasonCode", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(AdoptionAsstncDto.class));
		List<AdoptionAsstncDto> adoptionAsstncDtoList = sqlQuery.list();
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("ClosureReason.NotFound", null, Locale.US));
		}
		return adoptionAsstncDtoList.get(ServiceConstants.Zero);
	}

	/**
	 * Method Name: getAdptPlcmtInfo Method Description: Retrieves the Adoptive
	 * Placement informations.
	 * 
	 * @param idAdptSub
	 * @return AdoptionAsstncDto @
	 */
	@Override
	public AdoptionAsstncDto getAdptPlcmtInfo(Long idAdptSub) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAdptPlcmtInfoSql)
				.addScalar("adoptionAsstncId", StandardBasicTypes.LONG)
				.addScalar("placementEvenId", StandardBasicTypes.LONG)
				.addScalar("datePlacementEnd", StandardBasicTypes.DATE).setParameter("idAdptSub", idAdptSub)
				.setResultTransformer(Transformers.aliasToBean(AdoptionAsstncDto.class));
		List<AdoptionAsstncDto> adoptionAsstncDtoList = query.list();
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncDtoList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("AdoptivePlacementInformations.NotFound", null, Locale.US));
		}
		return adoptionAsstncDtoList.get(ServiceConstants.Zero);
	}

	/**
	 * Method Name: updateAdptSubsidy Method Description:This method updates
	 * ADOPTION_SUBSIDY table using ApplicationBackgroundValueService.
	 * 
	 * @param adptAsstncValueBeanDto
	 * @return Long @
	 */
	@Override
	public Long updateAdptSubsidy(AdoptionAsstncDto adptAsstncValueBeanDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdoptionSubsidy.class);
		criteria.add(Restrictions.eq("idAdptSub", adptAsstncValueBeanDto.getAdoptionAsstncId()));

		AdoptionSubsidy adoptionSubsidy = (AdoptionSubsidy) criteria.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(adptAsstncValueBeanDto.getIdAdptEligApplication())) {

			AdptEligApplication eligApplication = new AdptEligApplication();
			eligApplication.setIdAdptEligApplication(adptAsstncValueBeanDto.getIdAdptEligApplication());
			adoptionSubsidy.setAdptEligApplication(eligApplication);
		}
		if (!TypeConvUtil.isNullOrEmpty(adptAsstncValueBeanDto.getIdAdptEligRecert())) {

			AdptEligRecert adaptEligRecert = new AdptEligRecert();
			adaptEligRecert.setIdAdptEligRecert(adptAsstncValueBeanDto.getIdAdptEligRecert());
			adoptionSubsidy.setAdptEligRecert(adaptEligRecert);
		}

		adoptionSubsidy.setDtNextRecert(adptAsstncValueBeanDto.getDtNextRecert());
		sessionFactory.getCurrentSession().saveOrUpdate(adoptionSubsidy);

		return Long.valueOf(criteria.list().size());
	}

	/**
	 * Method Name: findEligibilityOrPrimayWorkerForStage Method
	 * Description:Retrives the Eligibility Or PrimayWorker
	 * 
	 * @param stageId
	 * @return Long
	 */
	@Override
	public Long findEligibilityOrPrimayWorkerForStage(Long stageId) {
		Long idWorker = ServiceConstants.ZERO_VAL;
		try {
			String[] eligWorkerProfiles = { "72" };
			List<StagePersonLinkDto> fcWorkers = stageDao.findWorkersForStage(stageId, eligWorkerProfiles);
			if (!TypeConvUtil.isNullOrEmpty(fcWorkers) && fcWorkers.size() > 0) {
				idWorker = fcWorkers.get(0).getIdPerson();

			} else {
				AdminWorkerInpDto adminWorkerInpDto = new AdminWorkerInpDto();
				adminWorkerInpDto.setIdStage(stageId);
				adminWorkerInpDto.setCdStagePersRole(ServiceConstants.CROLEALL_PR);
				AdminWorkerOutpDto adminWorkerOutpDto = new AdminWorkerOutpDto();
				adminWorkerOutpDto = adminWorkerDao.getPersonInRole(adminWorkerInpDto);
				idWorker = adminWorkerOutpDto.getIdTodoPersAssigned();
			}

		} catch (DataNotFoundException de) {
			log.error(de.getMessage());
		}

		return idWorker;
	}

	@Override
	public List<PlacementDto> fetchADOPlacements(Long idStageADOForPlacements) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchADOPlacementsSql)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("dtEventCreated", StandardBasicTypes.DATE)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("txtEventDescr", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setParameter("idEventStage", idStageADOForPlacements)
				.setResultTransformer(Transformers.aliasToBean(PlacementDto.class));
		List<PlacementDto> placementDtoList = query.list();
		if (TypeConvUtil.isNullOrEmpty(placementDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("event.not.found.attributes", null, Locale.US));
		}
		return placementDtoList;
	}

	@Override
	public Boolean getAdoProcessStatus(Long idStage) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getAdoProcessStatusSql)
				.setParameter("idStage", idStage);

		BigDecimal adoRecordCount = (BigDecimal) query.uniqueResult();
		if (!ObjectUtils.isEmpty(adoRecordCount) && adoRecordCount.intValue() > 0) {
			return Boolean.TRUE;
		} else
			return Boolean.FALSE;
	}
}
