package us.tx.state.dfps.service.casepackage.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casepackage.dao.RecordsRetentionAUDDao;
import us.tx.state.dfps.service.casepackage.dto.RecordRetentionDataInDto;
import us.tx.state.dfps.service.casepackage.dto.RecordRetentionDataOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

@Repository
public class RecordsRetentionAUDDaoImpl implements RecordsRetentionAUDDao {
	@Autowired
	MessageSource messageSource;

	@Value("${RecordsRetentionAUDDaoImpl.recordsRetentionAdd")
	private String recordsRetentionAdd;

	@Value("${RecordsRetentionAUDDaoImpl.recordsRetentionUpdate}")
	private String recordsRetentionUpdate;

	@Value("${RecordsRetentionAUDDaoImpl.recordsRetentionDelete}")
	private String recordsRetentionDelete;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeDaoLog");

	/**
	 * Method Name: recordsRetentionAUD Method Description: save
	 * recordsRetention data DAM caud75d
	 * 
	 * @param recordRetentionDataInDto
	 * @param recordRetentionDataOutDto
	 * @return @
	 */
	@Override
	public void recordsRetentionAUD(RecordRetentionDataInDto recordRetentionDataInDto,
			RecordRetentionDataOutDto recordRetentionDataOutDto) {
		log.debug("Entering method recordsRetentionAUD in RecordsRetentionAUDDaoImpl");
		switch (recordRetentionDataInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(recordsRetentionAdd)
					.setParameter("txtRecRtnDstryDtRsn", recordRetentionDataInDto.getTxtRecRtnDstryDtRsn())
					.setParameter("dtRecRtnDstryActual", recordRetentionDataInDto.getDtRecRtnDstryActual())
					.setParameter("dtRecRtnDstryElig", recordRetentionDataInDto.getDtRecRtnDstryElig())
					.setParameter("tsLastUpdate", recordRetentionDataInDto.getTsLastUpdate())
					.setParameter("idCase", recordRetentionDataInDto.getIdCase())
					.setParameter("cdRecRtnRetenType", recordRetentionDataInDto.getCdRecRtnRetenType()));
			sQLQuery1.executeUpdate();

			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(recordsRetentionUpdate)
					.setParameter("txtRecRtnDstryDtRsn", recordRetentionDataInDto.getTxtRecRtnDstryDtRsn())
					.setParameter("dtRecRtnDstryActual", recordRetentionDataInDto.getDtRecRtnDstryActual())
					.setParameter("dtRecRtnDstryElig", recordRetentionDataInDto.getDtRecRtnDstryElig())
					.setParameter("tsLastUpdate", recordRetentionDataInDto.getTsLastUpdate())
					.setParameter("idCase", recordRetentionDataInDto.getIdCase())
					.setParameter("cdRecRtnRetenType", recordRetentionDataInDto.getCdRecRtnRetenType()));
			sQLQuery2.executeUpdate();

			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(recordsRetentionDelete)
					.setParameter("tsLastUpdate", recordRetentionDataInDto.getTsLastUpdate())
					.setParameter("idCase", recordRetentionDataInDto.getIdCase()));
			sQLQuery3.executeUpdate();

			break;
		}

		log.debug("Exiting method recordsRetentionAUD in RecordsRetentionAUDDaoImpl");
	}

}
