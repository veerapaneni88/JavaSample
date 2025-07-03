package us.tx.state.dfps.service.security.daoimpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.common.exception.PersonNotFoundException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.security.dao.AppRequestDao;
import us.tx.state.dfps.service.security.dto.AppReqDto;
import us.tx.state.dfps.service.security.request.SSOTokenReq;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Description: This DAO layer has
 * operations to interact with APP_REQUEST table
 */
@Repository
public class AppRequestDaoImpl implements AppRequestDao {

	private static final Logger log = Logger.getLogger(AppRequestDaoImpl.class);

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AppRequestDaoImpl.ldapUsernameSql}")
	private String ldapUsername;

	public AppRequestDaoImpl() {

	}

	/**
	 * Method Name: insertToken Method Description:insertToken - This method
	 * inserts a row in APP_REQUEST table and generates the SSO token.
	 * 
	 * @param ssoTokenReq
	 * @return appReqDto @
	 */
	@Override
	public AppReqDto insertToken(SSOTokenReq ssoTokenReq) {
		boolean appRequestInserted = false;
		int errorCode = 0;
		String errorMessage = null;
		ResultSet resultSet = null;
		AppReqDto appReqDto = null;
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection = null;
		CallableStatement callStatement = null;
		try {
			connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			callStatement = connection.prepareCall("{? = call PKG_APP_REQUEST.FN_INSERT_TOKEN(?, ?, ?, ?, ?)}");
			callStatement.setString(2, ssoTokenReq.getEmpUserName());
			callStatement.setString(3, ssoTokenReq.getSourceApplication());
			callStatement.registerOutParameter(1, 12);
			callStatement.registerOutParameter(4, -10, "p_out");
			callStatement.registerOutParameter(5, -5);
			callStatement.registerOutParameter(6, 12);
			callStatement.executeQuery();
			appRequestInserted = callStatement.getString(1) != null && callStatement.getString(1).equals("TRUE") ? true
					: false;
			errorCode = callStatement.getInt(5);
			errorMessage = callStatement.getString(6);
			if (errorCode < 0) {
				throw new SQLException(errorMessage);
			}
			if (!appRequestInserted) {
				throw new SQLException("Token not inserted!");
			}
			resultSet = (ResultSet) callStatement.getObject(4);
			if (resultSet != null && resultSet.next()) {
				appReqDto = new AppReqDto();
				appReqDto.setIdAppRequest(resultSet.getInt("IdAppRequest"));
				appReqDto.setTokenValue(resultSet.getString("IdToken"));
				appReqDto.setStatus(resultSet.getString("InsertStatus"));
			}
		} catch (SQLException e) {
			DataLayerException dataException = new DataLayerException(e.toString());
			dataException.initCause(e);
			throw dataException;
		} finally {
			try {
				if (null != callStatement)
					callStatement.close();
				if (null != resultSet)
					resultSet.close();
				if (null != connection)
					connection.close();
			} catch (SQLException e) {
				log.error(e.getStackTrace());
			}
		}
		return appReqDto;
	}

	/**
	 * Method Name: getUserNameFromLogonAsId Method Description:insertToken -
	 * This method gets the username from the ldap link table
	 * 
	 * @param idHREmp
	 * @return String username
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getUserNameFromLogonAsId(Long idPerson) {
		String username = null;
		Query q = sessionFactory.getCurrentSession().createSQLQuery(ldapUsername);
		q.setParameter("cd_ldap", ServiceConstants.CD_LDAP);
		q.setParameter("id_Person", idPerson);

		List<String> rowCount = q.list();
		if (!CollectionUtils.isEmpty(rowCount)) {
			username = rowCount.get(0);
		} else {
			throw new PersonNotFoundException(idPerson);
		}
		return username;
	}
}
