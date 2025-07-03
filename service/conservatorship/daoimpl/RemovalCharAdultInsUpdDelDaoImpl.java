package us.tx.state.dfps.service.conservatorship.daoimpl;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.RemovalCharAdult;
import us.tx.state.dfps.common.domain.RemovalCharAdultId;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateLikeExpression;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.RemovalCharAdultInsUpdDelDao;
import us.tx.state.dfps.service.cvs.dto.RemovalCharAdultInsUpdDelInDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Caud32dDaoImpl Aug 15, 2017- 4:08:01 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class RemovalCharAdultInsUpdDelDaoImpl implements RemovalCharAdultInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(RemovalCharAdultInsUpdDelDaoImpl.class);

	/**
	 *
	 * @param removalCharAdultInsUpdDelInDto
	 * @return @
	 */
	@Override
	public void removalCharAdultInsUpdDel(RemovalCharAdultInsUpdDelInDto removalCharAdultInsUpdDelInDto) {
		log.debug("Entering method RemovalCharAdultInsUpdDelQUERYdam in RemovalCharAdultInsUpdDelDaoImpl");
		switch (removalCharAdultInsUpdDelInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			saveRemovalCharAdult(removalCharAdultInsUpdDelInDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			updateRemovalCharAdult(removalCharAdultInsUpdDelInDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			deleteRemovalCharAdult(removalCharAdultInsUpdDelInDto);
			break;
		}
		log.debug("Exiting method RemovalCharAdultInsUpdDelQUERYdam in RemovalCharAdultInsUpdDelDaoImpl");
	}

	private void deleteRemovalCharAdult(RemovalCharAdultInsUpdDelInDto removalCharAdultInsUpdDelInDto) {
		if (!TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto)) {
			RemovalCharAdult removalcharadult = new RemovalCharAdult();
			if (!TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto.getIdEvent())
					&& !TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto.getTsLastUpdate())
					&& !TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto.getCdRemovAdultChar())) {
				Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RemovalCharAdult.class);
				Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
						removalCharAdultInsUpdDelInDto.getIdEvent());
				if (TypeConvUtil.isNullOrEmpty(event)) {
					throw new DataNotFoundException(
							messageSource.getMessage("Caud32diDto.event.not.found", null, Locale.US));
				}
				RemovalCharAdultId removalCharAdultId = new RemovalCharAdultId();
				removalCharAdultId.setCdRemovAdultChar(removalCharAdultInsUpdDelInDto.getCdRemovAdultChar());
				removalCharAdultId.setIdRemovalEvent(removalCharAdultInsUpdDelInDto.getIdEvent());
				criteria.add(new DateLikeExpression("dtLastUpdate",
						TypeConvUtil.formatDate12Hr(removalCharAdultInsUpdDelInDto.getTsLastUpdate())));
				criteria.add(Restrictions.eq("id", removalCharAdultId));
				removalcharadult = (RemovalCharAdult) criteria.uniqueResult();
			}
			if (TypeConvUtil.isNullOrEmpty(removalcharadult)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Caud32diDto.RemovalCharAdult.not.found", null, Locale.US));
			}
			sessionFactory.getCurrentSession().delete(removalcharadult);
		}
	}

	private void saveRemovalCharAdult(RemovalCharAdultInsUpdDelInDto removalCharAdultInsUpdDelInDto) {
		if (!TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto)) {
			RemovalCharAdult removalcharadult = new RemovalCharAdult();
			if (!TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto.getIdEvent())) {
				Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
						removalCharAdultInsUpdDelInDto.getIdEvent());
				if (TypeConvUtil.isNullOrEmpty(event)) {
					throw new DataNotFoundException(
							messageSource.getMessage("Caud32diDto.event.not.found", null, Locale.US));
				}
				removalcharadult.setEvent(event);
			}
			if (!TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto.getTsLastUpdate())) {
				removalcharadult.setDtLastUpdate(removalCharAdultInsUpdDelInDto.getTsLastUpdate());
			} else {
				removalcharadult.setDtLastUpdate(new Date());
			}
			if (!TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto.getCdRemovAdultChar())) {
				RemovalCharAdultId removalCharAdultId = new RemovalCharAdultId();
				removalCharAdultId.setCdRemovAdultChar(removalCharAdultInsUpdDelInDto.getCdRemovAdultChar());
				removalCharAdultId.setIdRemovalEvent(removalCharAdultInsUpdDelInDto.getIdEvent());
				removalcharadult.setId(removalCharAdultId);
			}
			sessionFactory.getCurrentSession().save(removalcharadult);
		}
	}

	private void updateRemovalCharAdult(RemovalCharAdultInsUpdDelInDto removalCharAdultInsUpdDelInDto) {
		if (!TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto)) {
			RemovalCharAdult removalcharadult = new RemovalCharAdult();
			if (!TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto.getIdEvent())
					&& !TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto.getTsLastUpdate())) {
				Date minDate = removalCharAdultInsUpdDelInDto.getTsLastUpdate();
				Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
				Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RemovalCharAdult.class);
				Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
						removalCharAdultInsUpdDelInDto.getIdEvent());
				if (TypeConvUtil.isNullOrEmpty(event)) {
					throw new DataNotFoundException(
							messageSource.getMessage("Caud32diDto.event.not.found", null, Locale.US));
				}
				criteria.add(Restrictions.eq("event", event));
				criteria.add(Restrictions.ge("dtLastUpdate", minDate));
				criteria.add(Restrictions.lt("dtLastUpdate", maxDate));
				removalcharadult = (RemovalCharAdult) criteria.uniqueResult();
			}
			if (TypeConvUtil.isNullOrEmpty(removalcharadult)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Caud32diDto.RemovalCharAdult.not.found", null, Locale.US));
			}
			if (!TypeConvUtil.isNullOrEmpty(removalCharAdultInsUpdDelInDto.getCdRemovAdultChar())) {
				RemovalCharAdultId removalCharAdultId = new RemovalCharAdultId();
				removalCharAdultId.setCdRemovAdultChar(removalCharAdultInsUpdDelInDto.getCdRemovAdultChar());
				removalCharAdultId.setIdRemovalEvent(removalCharAdultInsUpdDelInDto.getIdEvent());
				removalcharadult.setId(removalCharAdultId);
			}
			sessionFactory.getCurrentSession().saveOrUpdate(removalcharadult);
		}
	}
}
