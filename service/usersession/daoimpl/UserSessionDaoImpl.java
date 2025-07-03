package us.tx.state.dfps.service.usersession.daoimpl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.ImpactUserSession;
import us.tx.state.dfps.service.usersession.dao.UserSessionDao;
import us.tx.state.dfps.service.usersession.dto.UserSessionDto;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * does insert and update operation Sep 8, 2017- 3:22:13 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class UserSessionDaoImpl implements UserSessionDao {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Method Name:insertIntoUserSession Method Description: Method
     * insertIntoUserSession inserts a UserSession record upon login.
     *
     * @param userSessionDto
     * @return long
     */
    @Override
    public long insertIntoUserSession(UserSessionDto userSessionDto) {

        ImpactUserSession impactUserSession = new ImpactUserSession();

        impactUserSession.setIdLoginUser(userSessionDto.getIdLoginUser());
        impactUserSession.setIdLoginAs((long) userSessionDto.getIdLoginAs());
        impactUserSession.setIdSession(userSessionDto.getIdSession());
        impactUserSession.setNmJvm(userSessionDto.getNmJvm());
        impactUserSession.setIpAddress(userSessionDto.getIpAddress());
        Timestamp systemTime = new Timestamp(Calendar.getInstance().getTime().getTime());
        impactUserSession.setTsLogin(systemTime);

        return (long) sessionFactory.getCurrentSession().save(impactUserSession);
    }

    /**
     * Method Name: updateUserSession Method Description:Method
     * updateUserSession updates the CD_LOGOUT_TYPE relating to how the session
     * ended
     *
     * @param userSessionDto
     * @return long
     */
    @Override
    public long updateUserSession(UserSessionDto userSessionDto) {

        long updatedResult = 0;
	/*	Timestamp systemTime = new Timestamp(Calendar.getInstance().getTime().getTime());
		userSessionDto.setTsLogout(systemTime);*/

        ImpactUserSession impactUserSession = (ImpactUserSession) sessionFactory.getCurrentSession()
                .get(ImpactUserSession.class, userSessionDto.getIdImpactUserSession());

        if (impactUserSession != null) {
            impactUserSession.setCdLogoutType(userSessionDto.getCdLogoutType());
            impactUserSession.setTsLogout(userSessionDto.getTsLogout());
            sessionFactory.getCurrentSession().update(impactUserSession);
            updatedResult++;
        }

        return updatedResult;
    }
}