package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.StageCasePersonDao;
import us.tx.state.dfps.xmlstructs.inputstructs.StageCasePersonInDto;
import us.tx.state.dfps.xmlstructs.outputstructs.StageCasePersonOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StageCasePersonDaoImpl Oct 31, 2017- 11:03:05 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class StageCasePersonDaoImpl implements StageCasePersonDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageCasePersonDaoImpl.getPrincipalsForStage}")
	private String getPrincipalsForStageSql;

	/**
	 * Method Name: getPrincipalsForStage Method Description:Gets PRIMARY
	 * CASEWORKER from STAGE_PERSON_LINK.
	 * 
	 * @param stageCasePersonInDto
	 * @return StageCasePersonOutDto
	 */
	@Override
	public StageCasePersonOutDto getPrincipalsForStage(StageCasePersonInDto stageCasePersonInDto) {
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPrincipalsForStageSql)
				.addScalar("ulIdPerson", StandardBasicTypes.LONG).addScalar("szNmPersonFull", StandardBasicTypes.STRING)
				.addScalar("szCdStage", StandardBasicTypes.STRING)
				.addScalar("szCdStageProgram", StandardBasicTypes.STRING)
				.setParameter("idStage", stageCasePersonInDto.getUlIdStage())
				.setParameter("cdStagePersRole", ServiceConstants.PR)
				.setResultTransformer(Transformers.aliasToBean(StageCasePersonOutDto.class));
		List<StageCasePersonOutDto> stageCasePersonOutDtoList = new ArrayList<>();
		stageCasePersonOutDtoList = (List<StageCasePersonOutDto>) sQLQuery1.list();
		if (0 < stageCasePersonOutDtoList.size()) {
			return stageCasePersonOutDtoList.get(ServiceConstants.Zero);
		}
		return null;
	}

}
