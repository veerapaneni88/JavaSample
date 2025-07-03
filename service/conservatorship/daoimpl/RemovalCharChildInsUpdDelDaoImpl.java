package us.tx.state.dfps.service.conservatorship.daoimpl;

import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.RemovalCharChild;
import us.tx.state.dfps.common.domain.RemovalCharChildId;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateLikeExpression;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.RemovalCharChildInsUpdDelDao;
import us.tx.state.dfps.service.cvs.dto.RemovalCharChildInsUpdDelInDto;
import us.tx.state.dfps.service.cvs.dto.RemovalCharChildInsUpdDelOutDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * does insert or update operation Aug 14, 2017- 11:55:49 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class RemovalCharChildInsUpdDelDaoImpl implements RemovalCharChildInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(RemovalCharChildInsUpdDelDaoImpl.class);

	/**
	 * Method Name: caud31dAUDdam Method Description:this method does This
	 * Method does insert and delete operation
	 *
	 * @param removalCharChildInsUpdDelInDto
	 * @return Caud31doDto @
	 */
	@Override
	public RemovalCharChildInsUpdDelOutDto removalCharChildInsUpdDel(
			RemovalCharChildInsUpdDelInDto removalCharChildInsUpdDelInDto) {
		log.debug("Entering method removalCharChildInsUpdDel in RemovalCharChildInsUpdDelDaoImpl");
		Long rowCount;
		RemovalCharChildInsUpdDelOutDto Caud31doDto = new RemovalCharChildInsUpdDelOutDto();
		switch (removalCharChildInsUpdDelInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			if (null != removalCharChildInsUpdDelInDto) {
				if (0 != removalCharChildInsUpdDelInDto.getIdEvent()) {
					RemovalCharChild removalCharChild = new RemovalCharChild();
					if (null != removalCharChildInsUpdDelInDto.getTsLastUpdate()) {
						removalCharChild.setDtLastUpdate(removalCharChildInsUpdDelInDto.getTsLastUpdate());
					} else {
						removalCharChild.setDtLastUpdate(new Date());
					}
					if (null != removalCharChildInsUpdDelInDto.getCdRemovChildChar()) {
						RemovalCharChildId removalCharChildId = new RemovalCharChildId();
						removalCharChildId.setCdRemovChildChar(removalCharChildInsUpdDelInDto.getCdRemovChildChar());
						removalCharChildId.setIdRemovalEvent(removalCharChildInsUpdDelInDto.getIdEvent());
						removalCharChild.setId(removalCharChildId);
					}
					if (null != removalCharChildInsUpdDelInDto.getIndCharChildCurrent()) {
						removalCharChild
								.setIndCharChildCurrent(removalCharChildInsUpdDelInDto.getIndCharChildCurrent());
					}
					if (null != removalCharChildInsUpdDelInDto.getIdCase()) {
						removalCharChild.setIdCase(removalCharChildInsUpdDelInDto.getIdCase());
					}
					sessionFactory.getCurrentSession().saveOrUpdate(removalCharChild);
					if (TypeConvUtil.isNullOrEmpty(removalCharChild.getId())) {
						throw new DataNotFoundException(
								messageSource.getMessage("Caud31d.removalCharChild.not.inserted", null, Locale.US));
					} else {
						rowCount = (long) 1;
						Caud31doDto.setTotalRecCount(rowCount);
					}
				}
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			if (null != removalCharChildInsUpdDelInDto) {
				Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RemovalCharChild.class);
				Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
						removalCharChildInsUpdDelInDto.getIdEvent());
				RemovalCharChildId removalCharChildId = new RemovalCharChildId();
				removalCharChildId.setIdRemovalEvent(removalCharChildInsUpdDelInDto.getIdEvent());
				removalCharChildId.setCdRemovChildChar(removalCharChildInsUpdDelInDto.getCdRemovChildChar());
				criteria.add(Restrictions.eq("event", event));
				criteria.add(Restrictions.eq("id", removalCharChildId));
				criteria.add(new DateLikeExpression("dtLastUpdate",
						TypeConvUtil.formatDate12Hr(removalCharChildInsUpdDelInDto.getTsLastUpdate())));
				RemovalCharChild removalCharChild = (RemovalCharChild) criteria.uniqueResult();
				if (TypeConvUtil.isNullOrEmpty(removalCharChild)) {
					throw new DataNotFoundException(messageSource.getMessage(
							"Caud31d.removalCharChild.not.found.eventId.and.dtLastUpdate", null, Locale.US));
				}
				if (null != removalCharChildInsUpdDelInDto.getIndCharChildCurrent()) {
					removalCharChild.setIndCharChildCurrent(removalCharChildInsUpdDelInDto.getIndCharChildCurrent());
				}
				sessionFactory.getCurrentSession().saveOrUpdate(removalCharChild);
				Caud31doDto.setTotalRecCount((long) 1);
			}
			break;
		}
		log.debug("Exiting method removalCharChildInsUpdDel in RemovalCharChildInsUpdDelDaoImpl");
		return Caud31doDto;
	}
}
