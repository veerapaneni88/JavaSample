package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.admin.dto.EmpOfficeDto;
import us.tx.state.dfps.service.casepackage.dao.OfficeDao;
import us.tx.state.dfps.service.casepackage.dto.OfficeDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCFC21S Class
 * Description: This Method implements OfficeDao. This is used to retrieve
 * office details from database. Mar 24, 2017 - 6:50:30 PM
 */
@Repository
public class OfficeDaoImpl implements OfficeDao {

	@Autowired
	MessageSource messageSource;

	@Value("${OfficeDaoImpl.getOfficeById}")
	private String getOfficeByIdSql;

	@Value("${OfficeDaoImpl.getOfficeEntityById}")
	private String getOfficeEntityByIdSql;

	@Autowired
	private SessionFactory sessionFactory;

	public OfficeDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This Method will retrive the office details from
	 * Office table DAM:CCMN00D Service: CCFC21S
	 * 
	 * @param ulIdOffice
	 * @return @
	 */
	@Override
	public OfficeDto getOfficeDetails(Long ulIdOffice) {

		OfficeDto officeDtls = new OfficeDto();

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(Office.class)
				.setProjection(
						Projections.projectionList().add(Projections.property("cdOfficeProgram"), "cdOfficeProgram")
								.add(Projections.property("cdOfficeRegion"), "cdOfficeRegion")
								.add(Projections.property("nmOfficeName"), "nmOfficeName")
								.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
								.add(Projections.property("cdOfficeMail"), "cdOfficeMail"))

				.add(Restrictions.eq("idOffice", ulIdOffice));

		officeDtls = (OfficeDto) cr.setResultTransformer(Transformers.aliasToBean(OfficeDto.class)).uniqueResult();
		return officeDtls;
	}

	/**
	 * 
	 * Method Description:getOfficeById
	 * 
	 * @param id
	 * @return @ @
	 */

	@Override
	public EmpOfficeDto getOfficeById(Long id) {
		EmpOfficeDto office = null;

		Query queryOffice = sessionFactory.getCurrentSession().createSQLQuery(getOfficeByIdSql)
				.addScalar("cdOfficeMail", StandardBasicTypes.STRING).addScalar("idOffice", StandardBasicTypes.LONG)
				.addScalar("nmOfficeName", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(EmpOfficeDto.class));
		queryOffice.setParameter("idSearch", id);

		office = (EmpOfficeDto) queryOffice.uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(office)) {
			throw new DataNotFoundException(messageSource.getMessage("office.not.found.idOffice", null, Locale.US));
		}
		return office;
	}

	@Override
	public Office getOfficeEntityById(Long id) {
		Office office = null;

		Query queryOffice = sessionFactory.getCurrentSession().createQuery(getOfficeEntityByIdSql);
		queryOffice.setParameter("idSearch", id);

		office = (Office) queryOffice.uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(office)) {
			throw new DataNotFoundException(messageSource.getMessage("office.not.found.idOffice", null, Locale.US));
		}
		return office;
	}

	/**
	 * 
	 * This CCMNA5D DAM returns ID OFFICE and NM OFFICE NAME from the OFFICE
	 * table when passed CD OFFICE PROGRAM, CD OFFICE REGION and CD OFFICE MAIL
	 * 
	 * @param ulIdOffice
	 * @return @
	 */
	@Override
	public Office getOfficeName(String cdOfficeMail, String cdOfficeRegion, String cdOfficeProgram) {
		Office office;

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(Office.class)
				.setProjection(Projections.projectionList().add(Projections.property("nmOfficeName"), "nmOfficeName")
						.add(Projections.property("idOffice"), "idOffice"))
				.add(Restrictions.eq("cdOfficeProgram", cdOfficeProgram))
				.add(Restrictions.eq("cdOfficeRegion", cdOfficeRegion))
				.add(Restrictions.eq("cdOfficeMail", cdOfficeMail));

		office = (Office) cr.setResultTransformer(Transformers.aliasToBean(Office.class)).uniqueResult();

		return office;
	}

	public Office createOffice(String cdOfficeMail, String cdOfficeRegion, String cdOfficeProgram, String cdOfficeName){
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		Office office = new Office();
		office.setCdOfficeMail(cdOfficeMail);
		office.setCdOfficeRegion(cdOfficeRegion);
		office.setCdOfficeProgram(cdOfficeProgram);
		office.setNmOfficeName(cdOfficeName);
		office.setDtLastUpdate(currentDate);
		office.setIdOffice((Long) sessionFactory.getCurrentSession().save(office));
		return office;
	}


	@Override
	public MailCode getMailCode(String cdMailCode) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MailCode.class);
		criteria.add(Restrictions.eq("cdMailCode", cdMailCode));
		criteria.add(Restrictions.eq("indMailCodeInvalid", "N"));

		return (MailCode) criteria.uniqueResult();

	}

}
