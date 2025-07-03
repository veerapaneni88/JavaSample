package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Calendar;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.UpdateEventDao;
import us.tx.state.dfps.service.admin.dto.UpdateEventiDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Demote the
 * status of all previously captured approval related events to Complete Aug
 * 8,2017- 10:39:00 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class UpdateEventDaoImpl implements UpdateEventDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${UpdateEventDaoImpl.updateEventSql}")
	private String updateEvent;

	private static final Logger log = Logger.getLogger(UpdateEventDaoImpl.class);

	/**
	 * Method Name: updateEvent Description:This method updates event status DAM
	 * Name: CCMN62D
	 * 
	 * @param updateEventiDto
	 * @return rowCount
	 */
	@Override
	public Integer updateEvent(UpdateEventiDto updateEventiDto) throws DataNotFoundException {
		log.debug("Entering method updateEvent in UpdateEventDaoImpl");
		Integer rowCount = ServiceConstants.Zero;
		if (!TypeConvUtil.isNullOrEmpty(updateEventiDto)
				&& ServiceConstants.REQ_FUNC_CD_UPDATE.equals(updateEventiDto.getcReqFuncCd())) {
			rowCount = sessionFactory.getCurrentSession().createSQLQuery(updateEvent)
					.setParameter("cdEventStatus", updateEventiDto.getSzCdEventStatus())
					.setParameter("dtEventModified", Calendar.getInstance().getTime())
					.setParameter("idEvent", updateEventiDto.getUlIdEvent()).executeUpdate();
			if (rowCount == ServiceConstants.Zero) {
				throw new DataNotFoundException(
						messageSource.getMessage("UpdateEventDaoImpl.not.found.hostulIdEvent", null, Locale.US));
			}
		}
		log.debug("Exiting method updateEvent in UpdateEventDaoImpl");
		return Integer.valueOf(rowCount);
	}
}
