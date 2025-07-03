package us.tx.state.dfps.service.casepackage.daoimpl;

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

import us.tx.state.dfps.service.casepackage.dao.CaseFileMgmtUnitDetailFetchDao;
import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtUnitDetailInDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtUnitDetailOutDto;

@Repository
public class CaseFileMgmtUnitDetailFetchDaoImpl implements CaseFileMgmtUnitDetailFetchDao {
	@Autowired
	MessageSource messageSource;

	@Value("${CaseFileMgmtUnitDetailFetchDaoImpl.caseFileMgmtUnitDetailFetch}")
	private String caseFileMgmtUnitDetailFetch;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeDaoLog");

	/**
	 * Method Name: caseFileMgmtUnitDetailFetch Method Description: fetch unit
	 * details for CaseFileMgmtUnitDetail DAM Ccmne1d
	 * 
	 * @param caseFileMgmtUnitDetailInDto
	 * @return CaseFileMgmtUnitDetailOutDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CaseFileMgmtUnitDetailOutDto caseFileMgmtUnitDetailFetch(
			CaseFileMgmtUnitDetailInDto caseFileMgmtUnitDetailInDto) {
		CaseFileMgmtUnitDetailOutDto caseFileMgmtUnitDetailOutDto = null;
		log.debug("Entering method caseFileMgmtUnitDetailFetch in CaseFileMgmtUnitDetailFetchDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(caseFileMgmtUnitDetailFetch)
				.addScalar("idUnit", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("idUnitParent", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nbrUnit", StandardBasicTypes.STRING).addScalar("cdUnitRegion", StandardBasicTypes.STRING)
				.addScalar("cdUnitProgram", StandardBasicTypes.STRING)
				.addScalar("cdUnitSpecialization", StandardBasicTypes.STRING)
				.setParameter("cdUnitProgram", caseFileMgmtUnitDetailInDto.getCdUnitProgram())
				.setParameter("cdUnitRegion", caseFileMgmtUnitDetailInDto.getCdUnitRegion())
				.setParameter("nbrUnit", caseFileMgmtUnitDetailInDto.getNbrUnit())
				.setResultTransformer(Transformers.aliasToBean(CaseFileMgmtUnitDetailOutDto.class)));

		List<CaseFileMgmtUnitDetailOutDto> caseFileMgmtUnitDetailOutDtos = new ArrayList<>();
		caseFileMgmtUnitDetailOutDtos = (List<CaseFileMgmtUnitDetailOutDto>) sQLQuery1.list();
		if (!CollectionUtils.isEmpty(caseFileMgmtUnitDetailOutDtos) && caseFileMgmtUnitDetailOutDtos.size() > 0) {
			caseFileMgmtUnitDetailOutDto = caseFileMgmtUnitDetailOutDtos.get(0);
		}

		log.debug("Exiting method caseFileMgmtUnitDetailFetch in CaseFileMgmtUnitDetailFetchDaoImpl");

		return caseFileMgmtUnitDetailOutDto;
	}

}
