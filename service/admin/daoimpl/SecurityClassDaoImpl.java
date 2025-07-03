/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Sep 18, 2018- 11:56:23 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.SecurityClass;
import us.tx.state.dfps.service.admin.dao.SecurityClassDao;
import us.tx.state.dfps.service.admin.dto.SecurityProfileMaintDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SecurityProfileMaintReq;
import us.tx.state.dfps.service.common.response.SecurityProfileMaintRes;
import us.tx.state.dfps.service.exception.DataLayerException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Sep 18, 2018- 11:56:23 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class SecurityClassDaoImpl implements SecurityClassDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${SecurityClassDao.updateSecurityClass}")
	private String updateSecurityClassSql;

	@Value("${SecurityClassDao.deleteSecurityClass}")
	private String deleteSecurityClassSql;

	/**
	 * 
	 * Method Name: getSecurityProfiles Method Description:to fetch profiles
	 * 
	 * @return List<SecurityProfileMaintDto>
	 */
	@Override
	public List<SecurityProfileMaintDto> getSecurityProfiles() {

		List<SecurityProfileMaintDto> securityProfiles = new ArrayList<>();

		securityProfiles = sessionFactory.getCurrentSession().createCriteria(SecurityClass.class)
				.setProjection(
						Projections.projectionList().add(Projections.property("cdSecurityClassName"), "nmSecurityClass")
								.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
								.add(Projections.property("txtSecurityClassProfil"), "txtSecurityClassProfil")
								.add(Projections.property("indRestrict"), "indRestrict"))
				.addOrder(Order.asc("cdSecurityClassName"))
				.setResultTransformer(Transformers.aliasToBean(SecurityProfileMaintDto.class)).list();

		return securityProfiles;

	}

	/**
	 * 
	 * Method Name: getSecurityProfileByName Method Description: to fetch
	 * profile by name
	 * 
	 * @param nmSecurityClass
	 * @return SecurityProfileMaintDto
	 */
	@Override
	public SecurityProfileMaintDto getSecurityProfileByName(String nmSecurityClass) {
		SecurityProfileMaintDto securityProfile = new SecurityProfileMaintDto();

		securityProfile = (SecurityProfileMaintDto) sessionFactory.getCurrentSession()
				.createCriteria(SecurityClass.class)
				.setProjection(
						Projections.projectionList().add(Projections.property("cdSecurityClassName"), "nmSecurityClass")
								.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
								.add(Projections.property("txtSecurityClassProfil"), "txtSecurityClassProfil")
								.add(Projections.property("indRestrict"), "indRestrict"))
				.add(Restrictions.eq("cdSecurityClassName", nmSecurityClass))
				.setResultTransformer(Transformers.aliasToBean(SecurityProfileMaintDto.class)).uniqueResult();

		return securityProfile;
	}

	/**
	 * Method Name: securityProfileAUD Method Description: to save , update ,
	 * delete the profile
	 * 
	 * @param securityProfileMaintReq
	 * @return SecurityProfileMaintRes
	 */
	@Override
	public SecurityProfileMaintRes securityProfileAUD(SecurityProfileMaintReq securityProfileMaintReq) {

		SecurityProfileMaintRes securityProfileMaintRes = new SecurityProfileMaintRes();
		SecurityProfileMaintDto profileDto = securityProfileMaintReq.getSecurityProfile();
		switch (securityProfileMaintReq.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			SecurityClass securityClassEntity = new SecurityClass();
			securityClassEntity.setCdSecurityClassName(profileDto.getNmSecurityClass());
			securityClassEntity.setTxtSecurityClassProfil(profileDto.getTxtSecurityClassProfil());
			securityClassEntity.setIndRestrict(profileDto.getIndRestrict());
			securityClassEntity.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().save(securityClassEntity);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(updateSecurityClassSql);
			query.setParameter("nmSecurityClass", profileDto.getNmSecurityClass());
			query.setParameter("txtSecurityClassProfil", profileDto.getTxtSecurityClassProfil());
			query.setParameter("dtLastUpdate", new Date());
			query.setParameter("indRestrict", profileDto.getIndRestrict());
			query.setParameter("nmSecurityClassOld", securityProfileMaintReq.getNmSecurityClass());
			// this try block added to handle validation, and custom error code
			// for duplicate record , as legacy also depending on the SQL errors
			try {
				query.executeUpdate();
			} catch (ConstraintViolationException ex) {
				throw new DataLayerException("Class already in use",
						Long.valueOf(ServiceConstants.MSG_ARC_CLASS_IN_USE), null);
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			SQLQuery deleteSQLQuery = ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(deleteSecurityClassSql)
					.setParameter("nmSecurityClass", profileDto.getNmSecurityClass()));
			// this try block added to handle validation, and custom error code
			// for duplicate record , as legacy also depending on the SQL errors
			try {
				deleteSQLQuery.executeUpdate();
			} catch (ConstraintViolationException ce) {
				throw new DataLayerException("Class already in use",
						Long.valueOf(ServiceConstants.MSG_ARC_CLASS_IN_USE), null);
			}
			break;

		}

		securityProfileMaintRes.setSecurityProfile(profileDto);
		return securityProfileMaintRes;

	}

}
