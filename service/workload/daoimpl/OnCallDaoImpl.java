package us.tx.state.dfps.service.workload.daoimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.common.domain.EmpOnCallLink;
import us.tx.state.dfps.common.domain.OnCall;
import us.tx.state.dfps.common.domain.OnCallCounty;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.admin.dto.AddOnCallInDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.RtrvOnCallCntyDto;
import us.tx.state.dfps.service.workload.dao.OnCallDao;
import us.tx.state.dfps.service.workload.dto.EmpOnCallLinkDto;
import us.tx.state.dfps.service.workload.dto.OnCallScheduleDetailDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is provides the method definitions to handle the save/update/delete
 * operations on the ON_CALL,ON_CALL_COUNTY and EMP_ON_CALL_LINK tables. Feb 10,
 * 2018- 9:04:55 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class OnCallDaoImpl implements OnCallDao {

	@Autowired
	MessageSource messageSource;

	@Value("${OnCallDaoImpl.getCurrentOnCallByPersonId}")
	private String getCurrentOnCallByPersonIdSql;

	@Value("${OnCallDaoImpl.getOnCallByPersonId}")
	private String getOnCallByPersonIdSql;

	@Value("${OnCallDaoImpl.getOnCallCnty}")
	private String getOnCallCnty;

	@Value("${OnCallDaoImpl.deleteEmpOnCallLink}")
	private String deleteEmpOnCallLink;

	@Value("${OnCallDaoImpl.getRouterPersonOnCall}")
	private String getRouterPersonOnCall;

	@Autowired
	private SessionFactory sessionFactory;

	public OnCallDaoImpl() {

	}

	/**
	 * 
	 * Method Description:getCurrentOnCallByPersonId
	 * 
	 * @param personId
	 * @return List<Long>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getCurrentOnCallByPersonId(Long personId) {
		List<Long> onCalls = new ArrayList<>();
		Date date = new Date();

		Query queryOnCall = sessionFactory.getCurrentSession().createQuery(getCurrentOnCallByPersonIdSql);
		queryOnCall.setParameter("idPerson", personId);
		queryOnCall.setParameter("date1", date);
		queryOnCall.setMaxResults(100);
		onCalls = (List<Long>) queryOnCall.list();

		if (TypeConvUtil.isNullOrEmpty(onCalls)) {
			throw new DataNotFoundException(
					messageSource.getMessage("currentOnCall.not.found.personId", null, Locale.US));
		}

		return onCalls;
	}

	/**
	 * 
	 * Method Description:getOnCallByPersonId
	 * 
	 * @param personId
	 * @return List<EmpOnCallLink>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmpOnCallLink> getOnCallByPersonId(Long personId) {
		List<EmpOnCallLink> onCalls = null;

		Query queryOnCall = sessionFactory.getCurrentSession().createQuery(getOnCallByPersonIdSql);
		queryOnCall.setParameter("idPerson", personId);
		queryOnCall.setMaxResults(100);
		onCalls = queryOnCall.list();

		if (TypeConvUtil.isNullOrEmpty(onCalls)) {
			throw new DataNotFoundException(
					messageSource.getMessage("onCall.not.found.personId" + personId, null, Locale.US));
		}

		return onCalls;
	}

	/**
	 * Method Name: deleteEmpOnCallLink Method Description:This method is used
	 * to delete record from the EMP_ON_CALL_LINK tables
	 * 
	 * @param empOnCallLink
	 *            - The instance of the domain entity which needs to be deleted
	 *            from the db.
	 */
	@Override
	public void deleteEmpOnCallLink(EmpOnCallLink empOnCallLink) {

		sessionFactory.getCurrentSession().delete(empOnCallLink);

	}

	/**
	 * Method Name: addOnCall Method Description:This method is used to create a
	 * new on call schedule.
	 * 
	 * @param onCallDto
	 *            - This dto is used to hold the values related to the on call
	 *            details.
	 * @return OnCallScheduleDetailDto- This dto is used to hold the values of
	 *         the saved on call schedules.
	 */
	@Override
	public OnCallScheduleDetailDto addOnCall(AddOnCallInDto onCallDto) {
		OnCallScheduleDetailDto callScheduleDetailDto = new OnCallScheduleDetailDto();
		OnCall onCall = new OnCall();
		// Setting the values in the entity object before calling the save
		// method.
		onCall.setCdOnCallProgram(onCallDto.getCdOnCallProgram());
		onCall.setCdOnCallType(onCallDto.getCdOnCallType());
		onCall.setDtOnCallStart(onCallDto.getDtOnCallStart());
		onCall.setDtOnCallEnd(onCallDto.getDtOnCallEnd());
		onCall.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		onCall.setIndOnCallFilled(onCallDto.getIndOnCallFilled());
		// Calling the save method to insert a record in the ON_CALL table
		Long idOnCallSaved = (Long) sessionFactory.getCurrentSession().save(onCall);

		// Iterating over the counties to insert records in the ON_CALL_COUNTY
		// table.
		onCallDto.getCdOnCallCounty().forEach(cdOnCallCounty -> {
			OnCallCounty onCallCounty = new OnCallCounty();
			onCallCounty.setIdOnCall(idOnCallSaved);
			onCallCounty.setCdOnCallCounty(cdOnCallCounty);
			onCallCounty.setCdOnCallRegion(onCallDto.getCdRegion());
			onCallCounty.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			sessionFactory.getCurrentSession().save(onCallCounty);

		});
		OnCall savedOnCall = (OnCall) sessionFactory.getCurrentSession().get(OnCall.class, idOnCallSaved);
		callScheduleDetailDto.setIdOnCall(savedOnCall.getIdOnCall());
		callScheduleDetailDto.setDtLastUpdate(savedOnCall.getDtLastUpdate());
		return callScheduleDetailDto;
	}

	/**
	 * Method Name: updateOnCall Method Description:This method is used to
	 * update the on call details in the ON_CALL and ON_CALL_COUNTY tables.
	 * 
	 * @param onCallDto
	 *            - The dto with the values to be updated in the ON_CALL and
	 *            ON_CALL_COUNTY tables.
	 * @return - The id of the on call which was updated.
	 */
	@Override
	public Long updateOnCall(AddOnCallInDto onCallDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OnCall.class);
		criteria.add(Restrictions.eq("idOnCall", onCallDto.getIdOnCall()));
		criteria.add(Restrictions.le("dtLastUpdate", onCallDto.getDtLastUpdate()));

		// Fetching the entity object based on the idOnCall and the last updated
		// date
		OnCall onCall = (OnCall) criteria.uniqueResult();
		// Setting the values to be updated in the entity object to be updated
		// in the
		// ON_CALL table.
		if (onCallDto != null && onCall != null) {
			if (onCallDto.getCdOnCallProgram() != null) {
				onCall.setCdOnCallProgram(onCallDto.getCdOnCallProgram());
			}
			if (onCallDto.getCdOnCallType() != null) {
				onCall.setCdOnCallType(onCallDto.getCdOnCallType());
			}
			if (onCallDto.getDtOnCallStart() != null) {
				onCall.setDtOnCallStart(onCallDto.getDtOnCallStart());
			}
			if (onCallDto.getDtOnCallEnd() != null) {
				onCall.setDtOnCallEnd(onCallDto.getDtOnCallEnd());
			}
			onCall.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			// Calling the update method to update the record in the ON_CALL
			// table.
			sessionFactory.getCurrentSession().saveOrUpdate(onCall);
		}

		// Update the counties for the particular on call detail
		Criteria onCallCountyCriteria = sessionFactory.getCurrentSession().createCriteria(OnCallCounty.class);
		onCallCountyCriteria.add(Restrictions.eq("idOnCall", onCallDto.getIdOnCall()));
		List<OnCallCounty> onCallCountyList = (List<OnCallCounty>) onCallCountyCriteria.list();
		if (!CollectionUtils.isEmpty(onCallCountyList)) {

			// Delete the list of counties for the particular on call schedule
			onCallCountyList.forEach(onCallCounty -> {
				Session session = sessionFactory.openSession();
				OnCallCounty onCallCountyEntity = (OnCallCounty) session.get(OnCallCounty.class,
						onCallCounty.getIdOnCallCounty());
				session.delete(onCallCountyEntity);
				session.beginTransaction().commit();
				session.close();
			});
		}
		// Insert the new counties for the on call schedule
		onCallDto.getCdOnCallCounty().forEach(cdOnCallCounty -> {
			OnCallCounty onCallCounty = new OnCallCounty();
			onCallCounty.setIdOnCall(onCallDto.getIdOnCall());
			onCallCounty.setCdOnCallCounty(cdOnCallCounty);
			onCallCounty.setCdOnCallRegion(onCallDto.getCdRegion());
			onCallCounty.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			sessionFactory.getCurrentSession().save(onCallCounty);
		});

		return null != onCall ? onCall.getIdOnCall() : 0l;
	}

	/**
	 * Method Name: deleteOnCall Method Description:This method is used to
	 * delete a on call schedule.
	 * 
	 * @param onCallDto
	 *            - The dto with the values of the on call which needs to
	 *            deleted.
	 */
	@Override
	public void deleteOnCall(AddOnCallInDto addOnCallInDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OnCall.class);
		criteria.add(Restrictions.eq("idOnCall", addOnCallInDto.getIdOnCall()));
		// Fetching the entity object based on the idOnCall.
		OnCall onCall = (OnCall) criteria.uniqueResult();
		if (onCall != null) {
			// Calling the delete method to delete the record from the ON_CALL
			// table.
			sessionFactory.getCurrentSession().delete(onCall);
		}
	}

	/**
	 * Method Name: addEmpOnCallLink Method Description:This method is used to
	 * insert a record in the EMP_ON_CALL_LINK table
	 * 
	 * @param empOnCallLinkDto
	 *            - The dto holds the values which are used to populate the
	 *            EMP_ON_CALL_LINK table.
	 * @return
	 */
	@Override
	public Long addEmpOnCallLink(EmpOnCallLinkDto empOnCallLinkDto) {
		EmpOnCallLink empOnCallLink = new EmpOnCallLink();
		// Populating the entity object with the input values to be saved in the
		// EMP_ON_CALL_LINK table.
		empOnCallLink.setIdPerson(empOnCallLinkDto.getIdPerson());
		empOnCallLink.setIdOnCall(empOnCallLinkDto.getIdOnCall());
		empOnCallLink.setCdEmpOnCallDesig(empOnCallLinkDto.getCdEmpOnCallDesig());
		empOnCallLink.setNbrEmpOnCallPhone1(empOnCallLinkDto.getEmpOnCallPhone1());
		empOnCallLink.setNbrEmpOnCallExt1(empOnCallLinkDto.getEmpOnCallExt1());
		empOnCallLink.setNbrEmpOnCallPhone2(empOnCallLinkDto.getEmpOnCallPhone2());
		empOnCallLink.setNbrEmpOnCallExt2(empOnCallLinkDto.getEmpOnCallExt2());
		empOnCallLink.setNbrEmpOnCallCntctOrd(empOnCallLinkDto.getEmpOnCallCntctOrd());
		empOnCallLink.setIdEmpOnCallLink(empOnCallLinkDto.getIdEmpOnCallLink());
		empOnCallLink.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		sessionFactory.getCurrentSession().save(empOnCallLink);

		return empOnCallLink.getIdEmpOnCallLink();

	}

	/**
	 * Method Name: updateEmpOnCalllink Method Description:This method updates
	 * the EMP_ON_CALL_LINK table.
	 * 
	 * @param empOnCallLinkDto
	 *            - The dto holds the values which are used to populate the
	 *            EMP_ON_CALL_LINK table.
	 * @return
	 */
	@Override
	public Long updateEmpOnCalllink(EmpOnCallLinkDto empOnCallLinkDto) {
		// Fetching the entity object based on the idEmpOnCallLink and last
		// updated
		// date..
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmpOnCallLink.class);
		criteria.add(Restrictions.eq("idEmpOnCallLink", empOnCallLinkDto.getIdEmpOnCallLink()));
		criteria.add(Restrictions.le("dtLastUpdate", empOnCallLinkDto.getDtLastUpdate()));

		// Populating the entity object with the values to be updated in the
		// EMP_ON_CALL_LINK table.
		EmpOnCallLink empOnCallLink = (EmpOnCallLink) criteria.uniqueResult();
		if (empOnCallLinkDto != null && empOnCallLink != null) {
			if (empOnCallLinkDto.getCdEmpOnCallDesig() != null) {
				empOnCallLink.setCdEmpOnCallDesig(empOnCallLinkDto.getCdEmpOnCallDesig());
			}
			if (empOnCallLinkDto.getEmpOnCallPhone1() != null) {
				empOnCallLink.setNbrEmpOnCallPhone1(empOnCallLinkDto.getEmpOnCallPhone1());
			}
			if (empOnCallLinkDto.getEmpOnCallExt1() != null) {
				empOnCallLink.setNbrEmpOnCallExt1(empOnCallLinkDto.getEmpOnCallExt1());
			}
			if (empOnCallLinkDto.getEmpOnCallPhone2() != null) {
				empOnCallLink.setNbrEmpOnCallPhone2(empOnCallLinkDto.getEmpOnCallPhone2());
			}
			if (empOnCallLinkDto.getEmpOnCallExt2() != null) {
				empOnCallLink.setNbrEmpOnCallExt2(empOnCallLinkDto.getEmpOnCallExt2());
			}
			if (empOnCallLinkDto.getEmpOnCallCntctOrd() != null) {
				empOnCallLink.setNbrEmpOnCallCntctOrd(empOnCallLinkDto.getEmpOnCallCntctOrd());
			}
			// Calling the update method to update the record in the db.
			sessionFactory.getCurrentSession().saveOrUpdate(empOnCallLink);
		}
		return null != empOnCallLink ? empOnCallLink.getIdEmpOnCallLink() : 0l;

	}

	/**
	 * Method Name: deleteEmpOnCallLink Method Description:This method is used
	 * to delete a record in the EMP_ON_CALL_LINK table.
	 * 
	 * EMP_ON_CALL_LINK table.
	 * 
	 * @param empOnCallLinkDto
	 *            - The dto holds the values which are used to populate the
	 *            EMP_ON_CALL_LINK table.
	 */
	@Override
	public void deleteEmpOnCallLink(EmpOnCallLinkDto empOnCallLinkDto) {
		// Fetching the entity object based on the idEmpOnCallLink and last
		// updated
		// date.
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmpOnCallLink.class);
		criteria.add(Restrictions.eq("idEmpOnCallLink", empOnCallLinkDto.getIdEmpOnCallLink()));
		criteria.add(Restrictions.le("dtLastUpdate", empOnCallLinkDto.getDtLastUpdate()));

		EmpOnCallLink empOnCallLink = (EmpOnCallLink) criteria.uniqueResult();
		if (empOnCallLink != null) {
			// Calling the delete method to delete the record from the
			// EMP_ON_CALL_LINK
			// table.
			sessionFactory.getCurrentSession().delete(empOnCallLink);
		}

	}

	/**
	 * Method Name: deleteEmpOnCallLinkByOnCallId Method Description:This method
	 * is used to delete employee from the EMP_ON_CALL_LINK table based on the
	 * on call id.
	 * 
	 * @param idOncall
	 *            - The value of the on call for which the employee/employees
	 *            has to be deleted.
	 */
	@Override
	public void deleteEmpOnCallLinkByOnCallId(long idOncall) {

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(deleteEmpOnCallLink);
		query.setParameter("idOnCall", idOncall);
		query.executeUpdate();
	}

	/**
	 * 
	 * Method Name: getOnCallCounty Method Description: This method gets
	 * information from On Call County table.
	 * 
	 * @param cdRegion
	 *            - The code of the region.
	 * @param idOnCall
	 *            - The id of the on call schedule.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RtrvOnCallCntyDto> getOnCallCounty(String cdRegion, Long idOnCall) {

		List<RtrvOnCallCntyDto> rtrvOnCallCntyDtoList = (List<RtrvOnCallCntyDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getOnCallCnty).addScalar("cdOnCallCounty", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idOnCall", idOnCall)
				.setParameter("cdRegn", cdRegion)
				.setResultTransformer(Transformers.aliasToBean(RtrvOnCallCntyDto.class)).list();

		return rtrvOnCallCntyDtoList;
	}

	/**
	 * 
	 * Method Name: updateOnCallFiled Method Description: This method updates
	 * the onCallFiled column for the particular ON_CALL
	 * 
	 * @param onCallDto
	 *            - The dto with the values to be updated in the ON_CALL and
	 *            table.
	 * @return
	 */
	@Override
	public Date updateOnCallFiled(AddOnCallInDto onCallDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OnCall.class);
		criteria.add(Restrictions.eq("idOnCall", onCallDto.getIdOnCall()));
		criteria.add(Restrictions.le("dtLastUpdate", onCallDto.getDtLastUpdate()));

		OnCall onCall = (OnCall) criteria.uniqueResult();

		if (onCallDto != null && onCall != null) {
			onCall.setIndOnCallFilled(onCallDto.getIndOnCallFilled());
			onCall.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			sessionFactory.getCurrentSession().saveOrUpdate(onCall);
		}
		OnCall updateOnCall = (OnCall) sessionFactory.getCurrentSession().get(OnCall.class, onCallDto.getIdOnCall());
		return updateOnCall.getDtLastUpdate();
	}

	@Override
	public Person getRouterPersonOnCall(String cdProgram, List<String> cdCounty) {

		Query query =  sessionFactory.getCurrentSession().createSQLQuery(getRouterPersonOnCall)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setParameter("cdProgram", cdProgram)
				.setParameterList("cdCounty", cdCounty)
				.setResultTransformer(Transformers.aliasToBean(Person.class));

		return (Person) query.uniqueResult();
	}
}
