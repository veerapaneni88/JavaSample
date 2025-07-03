package us.tx.state.dfps.service.arservicereferral.daoimpl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ArServRefChklist;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.arservicereferral.dao.ARServiceReferralDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.ARServRefListReq;
import us.tx.state.dfps.service.common.response.ARServRefDetailRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.FormsTemplateDto;
import us.tx.state.dfps.service.servicereferral.dto.ARServRefDetailsDto;
import us.tx.state.dfps.service.servicereferral.dto.ARServRefListDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ARServiceReferralDaoImpl for ARServiceReferral Sep 8, 2017- 2:29:35 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class ARServiceReferralDaoImpl implements ARServiceReferralDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Value("${ARServiceReferralDaoImpl.selectFamilyPlanDateComplete}")
	private String selectFamilyPlanDateCompleteSql;

	@Value("${ARServiceReferralDaoImpl.selectInitialSafetyPlanComplete}")
	private String selectInitialSafetyPlanCompleteSql;

	@Autowired
	ARServiceReferralDao arServiceReferralDao;

	@Autowired
	EventDao eventDao;

	/**
	 * Method Name: saveARServRefDetls Method Description: Continuation for
	 * saveARServRefDetails.
	 * 
	 * @param arServiceReferralDto
	 * @param arServRefChklist
	 */
	@Override
	public void saveARServRefDetails(ARServRefListReq arServiceReferralDto) {
		ARServRefListDto arRefListDto = arServiceReferralDto.getArServRefList().get(0);
		ArServRefChklist arServRefChklist = new ArServRefChklist();
		if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdArServRefChklist())) {
			arServRefChklist.setIdArServRefChklist(arRefListDto.getIdArServRefChklist());
		}
		if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdCase())) {
			arServRefChklist.setIdCase(arRefListDto.getIdCase());
		}
		if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdStage())) {
			arServRefChklist.setIdStage(arRefListDto.getIdStage());
		}

		long idEvent = 0L;
		if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdEvent())) {
			idEvent = arRefListDto.getIdEvent();
		}
		if (idEvent > 0) {
			arServRefChklist.setIdEvent(arRefListDto.getIdEvent());
		} else {
			idEvent = createEvent(arServiceReferralDto);
			arServRefChklist.setIdEvent(idEvent);
		}
		arServRefChklist.setIndReferral(arRefListDto.getIndNoServicReferrals());
		arServRefChklist.setCdPersRef(arRefListDto.getCdPersRef());
		arServRefChklist.setCdSrType(arRefListDto.getCdSrType());
		arServRefChklist.setCdSrSubtype(arRefListDto.getCdSrSubtype());
		if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getComments())) {
			arServRefChklist.setTxtComments(arRefListDto.getComments());
		}
		if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getCdFinalOutcome())) {
			arServRefChklist.setCdFinalOutcome(arRefListDto.getCdFinalOutcome());
		}

		arServRefChklist.setIdLastUpdatePerson(arRefListDto.getIdLastUpdatePerson());
		arServRefChklist.setIdCreatedPerson(arRefListDto.getIdCreatedPerson());
		if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getDtReferral())) {
			arServRefChklist.setDtReferral(DateUtils.stringToDate(arRefListDto.getDtReferral()));
		} else {
			arServRefChklist.setDtReferral(new Date());
		}
		arServRefChklist.setDtLastUpdate(new Date());
		arServRefChklist.setDtCreated(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(arServRefChklist);
	}

	/**
	 * Method Name: updateMultipleServRefs Method Description: This update
	 * method is used to update multiple service referrals.
	 * 
	 * @param idServiceReferrals
	 * @param txtComments
	 * @param cdFinalOutcome
	 * @return long
	 */
	@Override
	public void updateMultipleServRefs(ARServRefListReq arServiceReferralDto) {
		if (!CollectionUtils.isEmpty(arServiceReferralDto.getArServRefList())) {
			for (ARServRefListDto arRefListDto : arServiceReferralDto.getArServRefList()) {
				ArServRefChklist arServRefChklist = new ArServRefChklist();
				if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdArServRefChklist())) {
					arServRefChklist = (ArServRefChklist) sessionFactory.getCurrentSession().get(ArServRefChklist.class,
							arRefListDto.getIdArServRefChklist());
					if (!ObjectUtils.isEmpty(arServRefChklist)
							&& !ObjectUtils.isEmpty(arServRefChklist.getIdArServRefChklist())) {
						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdCase()))
							arServRefChklist.setIdCase(arRefListDto.getIdCase());

						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdStage()))
							arServRefChklist.setIdStage(arRefListDto.getIdStage());

						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdEvent()))
							arServRefChklist.setIdEvent(arRefListDto.getIdEvent());

						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIndNoServicReferrals()))
							arServRefChklist.setIndReferral(arRefListDto.getIndNoServicReferrals());

						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getCdPersRef()))
							arServRefChklist.setCdPersRef(arRefListDto.getCdPersRef());

						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getCdSrType()))
							arServRefChklist.setCdSrType(arRefListDto.getCdSrType());

						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getCdSrSubtype()))
							arServRefChklist.setCdSrSubtype(arRefListDto.getCdSrSubtype());

						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getComments()))
							arServRefChklist.setTxtComments(arRefListDto.getComments());

						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getCdFinalOutcome()))
							arServRefChklist.setCdFinalOutcome(arRefListDto.getCdFinalOutcome());

						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdLastUpdatePerson()))
							arServRefChklist.setIdLastUpdatePerson(arRefListDto.getIdLastUpdatePerson());

						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getDtReferral())) {
							arServRefChklist.setDtReferral(DateUtils.stringToDate(arRefListDto.getDtReferral()));
						} else {
							arServRefChklist.setDtReferral(new Date());
						}
						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdCreatedPerson()))
							arServRefChklist.setIdCreatedPerson(arRefListDto.getIdCreatedPerson());

						arServRefChklist.setDtLastUpdate(new Date());

						if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getDtCreated()))
							arServRefChklist.setDtCreated(arRefListDto.getDtCreated());

						sessionFactory.getCurrentSession().merge(arServRefChklist);
					}
				}
			}
		}
	}

	/**
	 * Method Name: deleteARServiceReferral Method Description: Method to delete
	 * ar service referral based on service referral id(s).
	 * 
	 * @param idStage
	 * @return long
	 */
	@Override
	public long deleteARServiceReferral(long idServiceRef) {
		long result = 0;
		ArServRefChklist arServRefChklist = (ArServRefChklist) sessionFactory.getCurrentSession()
				.get(ArServRefChklist.class, idServiceRef);
		if (arServRefChklist != null) {
			result++;
			sessionFactory.getCurrentSession().delete(arServRefChklist);
		}

		return result;

	}

	/**
	 * Method Name: createEvent Method Description: This method is used to
	 * create an event.
	 * 
	 * @param arServiceReferralDto
	 * @return
	 */
	private Long createEvent(ARServRefListReq arServiceReferralDto) {

		Timestamp systemTime = new Timestamp(Calendar.getInstance().getTime().getTime());
		Event event = new Event();
		Long idEvent = 0L;
		if (arServiceReferralDto != null) {
			Stage stage = new Stage();
			event.setCdEventType(ServiceConstants.CEVNTTYP_CHK);
			Person person = new Person();
			for (ARServRefListDto arRefListDto : arServiceReferralDto.getArServRefList()) {
				long idWorker = 0L;
				if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdWorker())) {
					idWorker = arRefListDto.getIdWorker();
				} else if (!TypeConvUtil.isNullOrEmpty(arRefListDto.getIdLastUpdatePerson())) {
					idWorker = arRefListDto.getIdLastUpdatePerson();
				}
				person.setIdPerson(idWorker);
				event.setIdCase(arRefListDto.getIdCase());
				stage.setIdStage(arRefListDto.getIdStage());
				event.setStage(stage);
				event.setPerson(person);
			}
			event.setCdTask(ServiceConstants.AR_SERV_REF_TASK);
			event.setTxtEventDescr(ServiceConstants.AR_SERV_REF);
			event.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
			event.setDtEventOccurred(systemTime);
			event.setDtEventCreated(systemTime);
			event.setDtEventModified(systemTime);
			event.setDtLastUpdate(systemTime);
			String operation = ServiceConstants.ADD;
			idEvent = eventDao.updateEvent(event, operation);
		}

		return idEvent;
	}

	/**
	 * Method Name: servRefExists Method Description: Based on the plan type
	 * parameter, this method retrieves initial safety plan completion date or
	 * initial family plan completion date from contacts.
	 * 
	 * @param idStage
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean servRefExists(long idStage) {
		boolean servRefExists = false;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArServRefChklist.class);
		criteria.add(Restrictions.eq("idStage", (long) idStage));

		List<ArServRefChklist> chklists = criteria.list();
		if (chklists.size() > 0)
			servRefExists = true;

		return servRefExists;
	}

	/**
	 * Method Name: deleteEvent Method Description: Delete service referral
	 * event from EVENT table.
	 * 
	 * @param idStage
	 * @param cevnttypChk
	 * @return long
	 */
	@SuppressWarnings("unchecked")
	@Override
	public long deleteEvent(long idStage, String cevnttypChk) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		criteria.add(Restrictions.eq("cdEventType", cevnttypChk));
		long count = 0;
		List<Event> events = criteria.list();

		for (Event event : events) {
			sessionFactory.getCurrentSession().delete(event);
			count++;
		}

		return count;
	}

	/**
	 * Method Name: getARServRefDetails Method Description: This Method Fetches
	 * Service Referral Details
	 * 
	 * @param arServiceReferralDto
	 * @return List<ArServRefChklist>
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ARServRefListDto> getARServRefDetails(long idStage) {

		List<ArServRefChklist> arServRefChklist = new ArrayList<ArServRefChklist>();
		List<ARServRefListDto> arServRefChkDtoList = new ArrayList<ARServRefListDto>();
		ARServRefListDto arServRefListDto1 = null;
		if (idStage != 0) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArServRefChklist.class);
			criteria.add(Restrictions.eq("idStage", Long.valueOf(idStage)));
			arServRefChklist = criteria.list();
			for (ArServRefChklist arServRefListDto : arServRefChklist) {
				arServRefListDto1 = new ARServRefListDto();
				BeanUtils.copyProperties(arServRefListDto, arServRefListDto1);
				if (!TypeConvUtil.isNullOrEmpty(arServRefListDto.getDtReferral())) {
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					String dtReferral = sdf.format(arServRefListDto.getDtReferral());

					arServRefListDto1.setDtReferral(dtReferral);
				}
				arServRefListDto1.setIndNoServicReferrals(arServRefListDto.getIndReferral());
				arServRefListDto1.setComments(arServRefListDto.getTxtComments());
				arServRefChkDtoList.add(arServRefListDto1);
			}

		}
		return arServRefChkDtoList;
	}

	/**
	 * Method Name: getARServRefDetails Method Description: This Method Fetches
	 * Service Referral Details
	 * 
	 * @param idServRefChklist
	 * @return ARServRefDetailRes
	 */
	@Override
	public ARServRefDetailRes getARServiceReferralsDetails(long idServRefChklist) {

		ARServRefDetailRes arServRefDetailRes = new ARServRefDetailRes();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArServRefChklist.class);
		criteria.add(Restrictions.eq("idArServRefChklist", idServRefChklist));
		ArServRefChklist arServRefChklist = new ArServRefChklist();
		arServRefChklist = (ArServRefChklist) criteria.uniqueResult();
		List<ARServRefDetailsDto> arServRefDetailsDto = new ArrayList<ARServRefDetailsDto>();
		ARServRefDetailsDto arServiceRefDto = new ARServRefDetailsDto();
		BeanUtils.copyProperties(arServRefChklist, arServiceRefDto);
		SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy");
		String dtReferral = date.format(arServiceRefDto.getDtReferral());
		arServiceRefDto.setDtRef(dtReferral);
		arServRefDetailsDto.add(arServiceRefDto);
		arServRefDetailRes.setServRefList(arServRefDetailsDto);
		return arServRefDetailRes;
	}

	
	@Override
	public String getPlanCompletionDate(long idStage, String string) {
		
		String sql=selectFamilyPlanDateCompleteSql;
		if("ISP".equals(string)) {
			sql=selectInitialSafetyPlanCompleteSql;
		}
	SQLQuery sqlQuery=(SQLQuery) sessionFactory.getCurrentSession()
		.createSQLQuery(sql).addScalar("OCCURRED", StandardBasicTypes.DATE)
		.setParameter("idStage", idStage);
	Date eventOccured=(Date) sqlQuery.uniqueResult();
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
	String eventOccuredString = !ObjectUtils.isEmpty(eventOccured)?date.format(eventOccured):null;
		return eventOccuredString;
	}
}