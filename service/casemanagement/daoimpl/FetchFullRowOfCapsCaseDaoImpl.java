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
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.FetchFullRowOfCapsCaseDao;
import us.tx.state.dfps.service.casepackage.dto.RetrieveCapsCaseInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveCapsCaseOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 10:39:48 PM © 2017 Texas Department of
 * Family and Protective Services
 */
// Ccmnd9dDaoImpl
@Repository
public class FetchFullRowOfCapsCaseDaoImpl implements FetchFullRowOfCapsCaseDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FetchFullRowOfCapsCaseDaoImpl.strCCMND9DCURSORQuery}")
	private String strCCMND9DCURSORQuery;

	private static final Logger log = Logger.getLogger(FetchFullRowOfCapsCaseDaoImpl.class);

	/**
	 * Method Name: retrieveCapsCase Method Description:This Method is used to
	 * retrieve the information of CapsCase
	 * 
	 * @param retrieveCapsCaseInputDto
	 * @param retrieveCapsCaseOutputDto
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void retrieveCapsCase(RetrieveCapsCaseInputDto retrieveCapsCaseInputDto,
			RetrieveCapsCaseOutputDto retrieveCapsCaseOutputDto) {
		log.debug("Entering method retrieveCapsCase in FetchFullRowOfCapsCaseDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCCMND9DCURSORQuery)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("indCaseWorkerSafety", StandardBasicTypes.STRING)
				.addScalar("indCaseSensitive", StandardBasicTypes.STRING)
				.addScalar("indCaseArchived", StandardBasicTypes.STRING)
				.addScalar("dtCaseClosed", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCaseOpened", StandardBasicTypes.TIMESTAMP)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("cdCaseCounty", StandardBasicTypes.STRING)
				.addScalar("cdCaseProgram", StandardBasicTypes.STRING)
				.addScalar("cdCaseRegion", StandardBasicTypes.STRING)
				.addScalar("cdCaseSpeclHndlg", StandardBasicTypes.STRING)
				.addScalar("txtCaseWorkerSafety", StandardBasicTypes.STRING)
				.addScalar("txtCaseSensitiveCmnts", StandardBasicTypes.STRING)
				.addScalar("txtSpecHandling", StandardBasicTypes.STRING).addScalar("nmCase", StandardBasicTypes.STRING)
				.setParameter("idCase", retrieveCapsCaseInputDto.getIdCase())
				.setResultTransformer(Transformers.aliasToBean(RetrieveCapsCaseOutputDto.class)));

		List<RetrieveCapsCaseOutputDto> retrieveCapsCaseOutputDtos = new ArrayList<>();
		retrieveCapsCaseOutputDtos = (List<RetrieveCapsCaseOutputDto>) sQLQuery1.list();
		if (!CollectionUtils.isEmpty(retrieveCapsCaseOutputDtos)) {
			retrieveCapsCaseOutputDto.setNmCase(retrieveCapsCaseOutputDtos.get(0).getNmCase());
		}

		log.debug("Exiting method retrieveCapsCase in FetchFullRowOfCapsCaseDaoImpl");
	}

}
