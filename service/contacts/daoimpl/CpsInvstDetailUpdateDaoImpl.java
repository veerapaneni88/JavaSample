package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CpsInvstDetail;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.CpsInvstDetailUpdateDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.InitialContactUpdateDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CpsInvstDetailUpdateDaoImpl Aug 2, 2018- 6:32:34 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class CpsInvstDetailUpdateDaoImpl implements CpsInvstDetailUpdateDao {
	@Autowired
	MessageSource messageSource;
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cinvf2dDaoImpl.doesInitialContactExist}")
	private String doesInitialContactExistSql;

	/**
	 * 
	 * Method Name: updateInitialContactDate Method Description:This method
	 * updates CPS_INVST_DETAIL table.
	 * 
	 * @param initialContactUpdateDto
	 * @return long
	 */
	@Override
	public long updateInitialContactDate(InitialContactUpdateDto initialContactUpdateDto) {
		boolean exists = doesInitialContactExist(initialContactUpdateDto);
		long Result = ServiceConstants.LongZero;
		if (exists) {

			Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(CpsInvstDetail.class);
			Stage stage = new Stage();
			stage.setIdStage((long) initialContactUpdateDto.getUlIdStage());
			criteria1.add(Restrictions.eq("stage.idStage", (long) initialContactUpdateDto.getUlIdStage()));

			List<CpsInvstDetail> cpsInvstDetailList = criteria1.list();
			if (TypeConvUtil.isNullOrEmpty(cpsInvstDetailList)) {
				throw new DataNotFoundException(
						messageSource.getMessage(ServiceConstants.SQL_NOT_FOUND, null, Locale.US));
			}

			for (CpsInvstDetail cpsInvstDetail : cpsInvstDetailList) {
				cpsInvstDetail.setDtCpsInvstDtlBegun(initialContactUpdateDto.getDtDtCPSInvstDtlBegun());
				sessionFactory.getCurrentSession().saveOrUpdate(cpsInvstDetail);
				Result++;
			}
		}

		return Result;

	}

	/**
	 * Method Name: doesInitialContactExist Method Description: Check if the
	 * contact purpose is initial
	 * 
	 * @param initialContactUpdateDto
	 * @return boolean
	 */
	private boolean doesInitialContactExist(InitialContactUpdateDto initialContactUpdateDto) {

		SQLQuery initialContactExist = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(doesInitialContactExistSql).addScalar("count", StandardBasicTypes.LONG)
				.setParameter("idStage", initialContactUpdateDto.getUlIdStage());

		if ((Long) initialContactExist.uniqueResult() > 0) {
			return true;
		}
		return false;
	}

}
