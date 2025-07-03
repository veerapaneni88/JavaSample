package us.tx.state.dfps.service.personlistbystage.daoimpl;

import java.util.List;
import java.util.Locale;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.ContactPrincipalsCollateralsDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.personlistbystage.dao.PersonListByStageDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Fetches
 * Person Details for Stage Information Oct 31, 2017- 10:22:17 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PersonListByStageDaoImpl implements PersonListByStageDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${PersonListByStageDao.getPersonDetailsForStage}")
	private String getPersonDetailsForStage;
	
	@Value("${PersonListByStageDao.getPRNPersonDetailsForStage}")
	private String getPRNPersonDetailsForStage;
	
	
	@Value("${PersonListByStageDao.getIndChildSxVctmzinHistory}")
	private String getIndChildSxVctmzinHistory;

	/**
	 * Method Name: getPersonDetailsForStage Method Description:The
	 * getPersonDetailsForStage method returns contacts info for all of people
	 * associated with the the stage.
	 * 
	 * @param idStage
	 * @param cdStagePersType
	 * @return List<ContactPrincipalsCollateralsDto>
	 */
	@Override
	public List<ContactPrincipalsCollateralsDto> getPersonDetailsForStage(long idStage, String cdStagePersType) {

		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getPersonDetailsForStage);
		query.setParameter("idStage", idStage);
		query.setParameter("cdStagePersType", cdStagePersType);
		query.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.addScalar("personAge", StandardBasicTypes.LONG).addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idStagePerson", StandardBasicTypes.LONG);

		List<ContactPrincipalsCollateralsDto> principalsCollateralsList = query
				.setResultTransformer(Transformers.aliasToBean(ContactPrincipalsCollateralsDto.class)).list();
		if (TypeConvUtil.isNullOrEmpty(principalsCollateralsList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("personListByStage.personListStageDORowDtos.notFound", null, Locale.US));
		}
		return principalsCollateralsList;
	}
	
	/**
	 * Method Name: getPRNPersonDetailsForStage Method Description:The
	 * getPRNPersonDetailsForStage method returns contacts info for all of people
	 * associated with the the stage.
	 * 
	 * @param idStage
	 * @param cdStagePersType
	 * @return List<ContactPrincipalsCollateralsDto>
	 */
	
	@Override
	public List<ContactPrincipalsCollateralsDto> getPRNPersonDetailsForStage(long idStage, String stageType) {

			
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getPRNPersonDetailsForStage);
		query.setParameter("idStage", idStage);
		query.setParameter("cdStagePersType",stageType);
		query.addScalar("personAge", StandardBasicTypes.LONG)
		     .addScalar("idPerson", StandardBasicTypes.LONG)
		     .addScalar("dtPersonBirth", StandardBasicTypes.DATE);
				
		List<ContactPrincipalsCollateralsDto> principalsList = query
				.setResultTransformer(Transformers.aliasToBean(ContactPrincipalsCollateralsDto.class)).list();
	
		return principalsList;
	}
	
	/**
	 * Method Name: getIndChildSxVctmzinHistory
	 * Method Description:The getIndChildSxVctmzinHistory method returns true or false as an indicator of 
	 * child sexual victimization question's answer for the person 
	 * associated with the  stage.
	 * 
	 * @param idPerson
	 * @return boolean 
	 */
	
	@Override
	public boolean getIndChildSxVctmzinHistory(long idPerson) {
		String indChildSxAnswer = "";
		boolean result = true;
		
		SQLQuery query = (sessionFactory.getCurrentSession().createSQLQuery(getIndChildSxVctmzinHistory));
		query.setParameter("idPerson", idPerson);
		
		Object obj = query.uniqueResult();
		
		result = (obj == null) ? false : true;
		
		return result;
	}
}
