package us.tx.state.dfps.service.security.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.security.dao.AppRequestDao;
import us.tx.state.dfps.service.security.dto.AppReqDto;
import us.tx.state.dfps.service.security.request.SSOTokenReq;
import us.tx.state.dfps.service.security.service.SecurityService;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Class Description: Implementation
 * Class for SecurityService that has methods pertaining to security related
 * operations. Apr 12, 2017 - 1:32:43 PM
 */
@Service
@Transactional
public class SecurityServiceImpl implements SecurityService {

	@Autowired
	AppRequestDao appRequestDao;

	/**
	 * Method Name: insertToken Method Description:insertToken - This method
	 * inserts a row in APP_REQUEST and generates the SSO token.
	 * 
	 * @param ssoTokenReq
	 * @return appReqDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AppReqDto insertToken(SSOTokenReq ssoTokenReq) {

		AppReqDto aapReqDto = null;
		if (ssoTokenReq.getIdPerson() == null) {
			aapReqDto = appRequestDao.insertToken(ssoTokenReq);
		} else {
			String username = appRequestDao.getUserNameFromLogonAsId(ssoTokenReq.getIdPerson());
			if (null != username) {
				ssoTokenReq.setEmpUserName(username);
				aapReqDto = appRequestDao.insertToken(ssoTokenReq);
			}
		}
		return aapReqDto;
	}
}
