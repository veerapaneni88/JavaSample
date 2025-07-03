package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.FetchNamesDao;
import us.tx.state.dfps.service.casepackage.dto.RetrieveNamesInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveNamesOutputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrievePrincipalOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 10:39:00 PM © 2017 Texas Department of
 * Family and Protective Services
 */

// Ccmnd7dDaoImpl
@Repository
public class FetchNamesDaoImpl implements FetchNamesDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FetchNamesDaoImpl.strCCMND7DCURSORQuery}")
	private String strCCMND7DCURSORQuery;

	private static final Logger log = Logger.getLogger(FetchNamesDaoImpl.class);

	/**
	 * Method Name: fetchNames Method Description:This Method is used to fetch
	 * the information of stage Names
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fetchNames(RetrieveNamesInputDto retrieveNamesInputDto, RetrieveNamesOutputDto retrieveNamesOutputDto) {
		log.debug("Entering method fetchNames in FetchNamesDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCCMND7DCURSORQuery)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("idNmPerson", StandardBasicTypes.LONG)
				.setParameter("idStage", retrieveNamesInputDto.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(RetrievePrincipalOutDto.class)));

		List<RetrievePrincipalOutDto> principalOutDtos = new ArrayList<>();
		principalOutDtos = (List<RetrievePrincipalOutDto>) sQLQuery1.list();

		retrieveNamesOutputDto.setPrincipals(principalOutDtos);

		log.debug("Exiting method fetchNames in FetchNamesDaoImpl");
	}

}
