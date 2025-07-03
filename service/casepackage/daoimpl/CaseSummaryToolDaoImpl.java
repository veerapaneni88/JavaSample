/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jun 6, 2018- 2:11:54 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casepackage.dao.CaseSummaryToolDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:The
 * Implementation of the Case Summary Tool Dao class. Jun 6, 2018- 2:11:54 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CaseSummaryToolDaoImpl implements CaseSummaryToolDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseSummaryTool.getPersonList}")
	private String getPersonList;

	/**
	 * 
	 * Method Name: getCaseSumToolPersonList Method Description: Retrieve the
	 * person list for the particular stage.
	 * 
	 * @param idStage
	 * @param staffType
	 * @return caseSumToolPersonList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonDto> getCaseSumToolPersonList(Long idStage, String staffType) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonList)
				.addScalar("personPhone", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("txtEmail", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage).setParameter("stagePersType", staffType)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));
		List<PersonDto> caseSumToolPersonList = query.list();
		return caseSumToolPersonList;
	}
}
