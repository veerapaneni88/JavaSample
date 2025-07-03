package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.FetchAllVictimsForCaseDao;
import us.tx.state.dfps.service.casepackage.dto.RetrieveAllVictimsInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveAllVictimsOutputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveVictimOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 10:42:06 PM © 2017 Texas Department of
 * Family and Protective Services
 */
// Clsc90dDaoImpl
@Repository
public class FetchAllVictimsForCaseDaoImpl implements FetchAllVictimsForCaseDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FetchAllVictimsForCaseDaoImpl.strCLSC90DCURSORQuery}")
	private String strCLSC90DCURSORQuery;

	private static final Logger log = Logger.getLogger(FetchAllVictimsForCaseDaoImpl.class);

	/**
	 * Method Name: retrieveAllVictim Method Description:This Method is used to
	 * retrieve the information of all Victims
	 * 
	 * @param retrieveAllVictimsInputDto
	 * @param retrieveAllVictimsOutputDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void retrieveAllVictim(RetrieveAllVictimsInputDto retrieveAllVictimsInputDto,
			RetrieveAllVictimsOutputDto retrieveAllVictimsOutputDto) {
		log.debug("Entering method retrieveAllVictim in FetchAllVictimsForCaseDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCLSC90DCURSORQuery)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.setParameter("idStage", retrieveAllVictimsInputDto.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(RetrieveVictimOutDto.class)));

		List<RetrieveVictimOutDto> victimList = null;
		victimList = (List<RetrieveVictimOutDto>) sQLQuery1.list();
		if (!CollectionUtils.isEmpty(victimList)) {
			retrieveAllVictimsOutputDto.setVictimList(victimList);
		}
	}

}
