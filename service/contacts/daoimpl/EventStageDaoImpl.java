package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.Locale;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.EventStageDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.EventUpdateStatusDto;

@Repository
public class EventStageDaoImpl implements EventStageDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cinvc9dDaoImpl.updateEvent}")
	private String updateEvent;

	/**
	 * 
	 * Method Name: updateEvent Method Description:update event table using
	 * stage link table.
	 * 
	 * @param eventUpdateStatusDto
	 * @return long @
	 */
	@Override
	public long updateEvent(EventUpdateStatusDto eventUpdateStatusDto) {
		long updatedRersult = 0;
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(eventUpdateStatusDto.getArchInputStruct().getCreqFuncCd())) {
			Query query = sessionFactory.getCurrentSession().createSQLQuery(updateEvent);
			query.setParameter("dtEventOccurred", eventUpdateStatusDto.getDtDtEventOccurred());
			query.setParameter("txtEventDescr", eventUpdateStatusDto.getSzTxtEventDescr());
			query.setParameter("idStage", eventUpdateStatusDto.getUlIdEventStage());
			updatedRersult = (long) query.executeUpdate();
		} else {
			throw new DataNotFoundException(messageSource.getMessage("ArcErrBadFuncCd", null, Locale.US));
		}
		return updatedRersult;
	}

}
