package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.Locale;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.IncomingDetailUpdateDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactIncomingDetailsDto;

@Repository
public class IncomingDetailUpdateDaoImpl implements IncomingDetailUpdateDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cinvc7dDaoImpl.updateIncomingDetail}")
	private String updateIncomingDetailSql;

	/**
	 * 
	 * Method Name: updateIncomingDetail Method Description:Update the Intake
	 * disposed date on the Incoming Detail Table.
	 * 
	 * @param contactIncomingDetailsDto
	 * @return @
	 */
	@Override
	public long updateIncomingDetail(ContactIncomingDetailsDto cinvc7di) {
		long rowCount = 0;
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(cinvc7di.getArchInputStruct().getCreqFuncCd())) {
			Query query = sessionFactory.getCurrentSession().createSQLQuery(updateIncomingDetailSql);
			query.setParameter("dtIncomingCallDisposed", cinvc7di.getDtIncomingCallDisposed());
			query.setParameter("idCase", cinvc7di.getIdCase());
			query.setParameter("cdStage", cinvc7di.getSzCdStage());

			rowCount = (long) query.executeUpdate();
		} else {
			throw new DataNotFoundException(messageSource.getMessage("ArcErrBadFuncCd", null, Locale.US));
		}
		return rowCount;
	}

}
