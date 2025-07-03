package us.tx.state.dfps.service.guardianshipdtl.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Guardianship;
import us.tx.state.dfps.service.guardianshipdtl.dao.GuardianshipDtlDao;

@Repository
public class GuardianshipDtlDaoImpl implements GuardianshipDtlDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${GuardianshipDtlDaoImpl.isFinalOutcomeDocumentedForDAD}")
	private String isFinalOutcomeDocumentedForDAD;

	/**
	 * 
	 * Method Name: isFinalOutcomeDocumentedForDAD Method Description:Fetches
	 * Guardianship details records for given case where final outcome is null
	 * for DAD type.
	 * 
	 * @param idCase
	 * @return
	 */
	@Override
	public boolean isFinalOutcomeDocumentedForDAD(Long idCase) {
		SQLQuery sqLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(isFinalOutcomeDocumentedForDAD).addScalar("idGuardEvent", StandardBasicTypes.LONG)
				.setParameter("idCase", idCase).setResultTransformer(Transformers.aliasToBean(Guardianship.class)));
		List<Guardianship> guardianshipList = (List<Guardianship>) sqLQuery1.list();
		if (guardianshipList.size() > 0)
			return true;
		return false;
	}
}
