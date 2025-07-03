package us.tx.state.dfps.service.casepackage.daoimpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.RecordsRetention;
import us.tx.state.dfps.common.domain.ServiceAuthorization;
import us.tx.state.dfps.service.casepackage.dao.RecordsRetentionDao;
import us.tx.state.dfps.service.casepackage.dto.RecordsRetentionDto;
import us.tx.state.dfps.service.casepackage.dto.RecordsRetnDestDtlsDto;
import us.tx.state.dfps.service.common.response.RecordsRetentionRtrvRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: SSCC
 * EJB Class Description: RecordsRetentionDaoImpl Mar 26, 2017 - 8:58:10 PM
 */
@Repository
public class RecordsRetentionDaoImpl implements RecordsRetentionDao {

	@Value("${RecordsRetentionDaoImpl.getRecordsRetentionByCaseId}")
	private String getRecordsRetentionByCaseIdSql;
	
	@Value("${RecordsRetentionDaoImpl.getDestructionDate}")
	private String getDestructionDateSql;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	public RecordsRetentionDaoImpl() {

	}

	/**
	 * This DAM will retrieve a full row from RECORDS RETENTION table and will
	 * take as input ID_CASE
	 * 
	 * Service Name - CCFC19S, Dam Name - CSES56D
	 * 
	 * @param ulIdCase
	 * @return @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public RecordsRetentionRtrvRes getRecordsRetentionByCaseId(Long ulIdCase) {

		RecordsRetentionRtrvRes recordsRetentionRtrvRes = new RecordsRetentionRtrvRes();

		Query queryCapsCase = sessionFactory.getCurrentSession().createSQLQuery(getRecordsRetentionByCaseIdSql)
				.addScalar("idRecRtnCase", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdRecRtnRetenType", StandardBasicTypes.STRING)
				.addScalar("dtRecRtnDstryActual", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtRecRtnDstryElig", StandardBasicTypes.TIMESTAMP)
				.addScalar("txtRecRtnDstryDtRsn", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(RecordsRetentionRtrvRes.class));
		queryCapsCase.setParameter("idCase", ulIdCase);

		recordsRetentionRtrvRes = (RecordsRetentionRtrvRes) queryCapsCase.uniqueResult();

		return recordsRetentionRtrvRes;
	}
	
	/**
	 * Method Name: getDestructionDate Method Description: This method will call
	 * the stored procedure proc_calcrecretn from the package PKG_RECRETN to
	 * calculate the destruction date and retention type
	 * 
	 * @param idCase
	 * @return RecordRetentionDataInDto
	 */
	public RecordsRetnDestDtlsDto getDestructionDate (Long idCase) {
		
		int errorCode = 0;
		String errorMessage = null;
		SessionImpl sessionImpl = (SessionImpl) sessionFactory.getCurrentSession();
		Connection connection = sessionImpl.connection();
		CallableStatement callStatement = null;
		RecordsRetnDestDtlsDto recordsRetnDestDtlsDto = new RecordsRetnDestDtlsDto();
		// Exception Handling done here in order to close the connection, else
		// this would have been handled through the aspect
		try {
			callStatement = connection.prepareCall(getDestructionDateSql);
			//Input Parameter to the stored proc is the case ID.
			callStatement.setString(1, idCase.toString());
			//Register the output parameters to be received from the stored proc.
			callStatement.registerOutParameter(2, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(3, java.sql.Types.DATE);
			callStatement.registerOutParameter(4, java.sql.Types.BIGINT);
			callStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
			callStatement.execute();
			Date destructionDate = callStatement.getDate(3);
			recordsRetnDestDtlsDto.setDtDestruction(destructionDate);
			String retentionType = callStatement.getString(2);
			recordsRetnDestDtlsDto.setCdRecordRetnType(retentionType);
			errorCode = callStatement.getInt(4); 
			errorMessage = callStatement.getString(5);
			if (errorCode != 0) {
				recordsRetnDestDtlsDto.setErrorCode(Long.valueOf(errorCode));
				recordsRetnDestDtlsDto.setErrorDescription(errorMessage);
			}
			// Any exception occurs, catch it and throw the data exception to
			// roll back the transaction which will be then caught by the aspect
		} catch (SQLException e) {
			DataLayerException dataException = new DataLayerException(errorCode + errorMessage + e.getMessage());
			dataException.initCause(e);
			throw dataException;
		} finally {
			try {
				callStatement.close();
			} catch (SQLException e) {
				DataLayerException dataException = new DataLayerException(errorCode + errorMessage + e.getMessage());
				dataException.initCause(e);
				throw dataException;
			}
		}
		return recordsRetnDestDtlsDto;
	}

	public RecordsRetentionDto getRecordsRetentionDestructionDate(Long idCase) {

		int errorCode = 0;
		String errorMessage = null;
		SessionImpl sessionImpl = (SessionImpl) sessionFactory.getCurrentSession();
		Connection connection = sessionImpl.connection();
		CallableStatement callStatement = null;
		RecordsRetentionDto recordsRetentionDto = new RecordsRetentionDto();
		recordsRetentionDto.setIdCase(idCase);
		recordsRetentionDto.setIdRecRtnCase(idCase);
		// Exception Handling done here in order to close the connection, else
		// this would have been handled through the aspect
		try {
			callStatement = connection.prepareCall(getDestructionDateSql);
			//Input Parameter to the stored proc is the case ID.
			callStatement.setString(1, idCase.toString());
			//Register the output parameters to be received from the stored proc.
			callStatement.registerOutParameter(2, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(3, java.sql.Types.DATE);
			callStatement.registerOutParameter(4, java.sql.Types.BIGINT);
			callStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
			callStatement.execute();

			Date destructionDate = callStatement.getDate(3);
			recordsRetentionDto.setDtRecRtnDestroyActual(destructionDate);
			String retentionType = callStatement.getString(2);
			recordsRetentionDto.setCdRecRetentionType(retentionType);

			errorCode = callStatement.getInt(4);
			errorMessage = callStatement.getString(5);
			// Any exception occurs, catch it and throw the data exception to
			// roll back the transaction which will be then caught by the aspect
		} catch (SQLException e) {
			DataLayerException dataException = new DataLayerException(errorCode + errorMessage + e.getMessage());
			dataException.initCause(e);
			throw dataException;
		} finally {
			try {
				callStatement.close();
			} catch (SQLException e) {
				DataLayerException dataException = new DataLayerException(errorCode + errorMessage + e.getMessage());
				dataException.initCause(e);
				throw dataException;
			}
		}
		return recordsRetentionDto;
	}

	public void insertRecordsRetention(RecordsRetentionDto retentionDto) {
		RecordsRetention retention = new RecordsRetention();

		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, retentionDto.getIdRecRtnCase());
		if (TypeConvUtil.isNullOrEmpty(capsCase)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.capscase", null, Locale.US));
		}
		retention.setCapsCase(capsCase);
		retention.setDtLastUpdate(new Date());
		retention.setCdRecRtnRetenType(retentionDto.getCdRecRetentionType());
		retention.setDtRecRtnDstryActual(retentionDto.getDtRecRtnDestroyActual());
		retention.setDtRecRtnDstryElig(retentionDto.getDtRecRtnDestroyEligible());
		retention.setTxtRecRtnDstryDtRsn(retentionDto.getRecRtnDestoryDtReason());

		sessionFactory.getCurrentSession().save(retention);
	}
}