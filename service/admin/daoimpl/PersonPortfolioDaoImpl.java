package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.PersonPortfolioDao;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioInDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * Retrieves a full row from the person table Aug 5, 2017- 11:15:40 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class PersonPortfolioDaoImpl implements PersonPortfolioDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonPortfolioDaoImpl.getPersonRecord}")
	private String getPersonRecord;

	private static final Logger log = Logger.getLogger(PersonPortfolioDaoImpl.class);

	public PersonPortfolioDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getPersonRecord Method Description: This method will get
	 * data from Person table. Ccmn44d
	 * 
	 * @param personPortfolioInDto
	 * @return List<PersonPortfolioOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonPortfolioOutDto> getPersonRecord(PersonPortfolioInDto personPortfolioInDto) {
		log.debug("Entering method PersonPortfolioQUERYdam in PersonPortfolioDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonRecord)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdDeathRsnCps", StandardBasicTypes.STRING)
				.addScalar("cdMannerDeath", StandardBasicTypes.STRING)
				.addScalar("cdDeathCause", StandardBasicTypes.STRING)
				.addScalar("cdDeathAutpsyRslt", StandardBasicTypes.STRING)
				.addScalar("cdDeathFinding", StandardBasicTypes.STRING)
				.addScalar("fatalityDetails", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("personSex", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPersonDeath", StandardBasicTypes.TIMESTAMP)
				.addScalar("personAge", StandardBasicTypes.INTEGER).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdNmPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("occupation", StandardBasicTypes.STRING).addScalar("cdOccupation", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("tsSysTsLastUpdate2", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate2", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdDisasterRlf", StandardBasicTypes.STRING)
				.addScalar("indEducationPortfolio", StandardBasicTypes.STRING)
				.addScalar("indAbuseNglctDeathInCare", StandardBasicTypes.STRING)
				.addScalar("cdTribeEligible", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PersonPortfolioOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPerson", personPortfolioInDto.getIdPerson());
		List<PersonPortfolioOutDto> personPortfolioOutDtos = (List<PersonPortfolioOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(personPortfolioOutDtos) && personPortfolioOutDtos.size() == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("PersonPortfolioDaoImpl.person.record.not.found", null, Locale.US));
		}
		log.debug("Exiting method PersonPortfolioQUERYdam in PersonPortfolioDaoImpl");
		return personPortfolioOutDtos;
	}
}
