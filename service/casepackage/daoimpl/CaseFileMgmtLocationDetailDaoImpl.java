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

import us.tx.state.dfps.service.casepackage.dao.CaseFileMgmtLocationDetailDao;
import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtLocatioOutDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtLocationInDto;

@Repository
public class CaseFileMgmtLocationDetailDaoImpl implements CaseFileMgmtLocationDetailDao {
	@Autowired
	MessageSource messageSource;

	@Value("${CaseFileMgmtLocationDetailDaoImpl.caseFileMgmtLocationDetail}")
	private String caseFileMgmtLocationDetail;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeDaoLog");

	/**
	 * Method Name: CaseFileMgmtLocationDetailDaoImpl Method Description:Fetch
	 * office details for CaseFileMgmtLocation
	 * 
	 * @param caseFileMgmtLocationInDto
	 * @return CaseFileMgmtLocatioOutDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CaseFileMgmtLocatioOutDto caseFileMgmtLocationDetail(CaseFileMgmtLocationInDto caseFileMgmtLocationInDto) {
		CaseFileMgmtLocatioOutDto caseFileMgmtLocatioOutDto = null;
		log.debug("Entering method caseFileMgmtLocationDetail in CaseFileMgmtLocationDetailDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(caseFileMgmtLocationDetail)
				.addScalar("idOffice", StandardBasicTypes.LONG).addScalar("nmOfficeName", StandardBasicTypes.STRING)
				.setParameter("addrMailCode", caseFileMgmtLocationInDto.getAddrMailCode())
				.setParameter("cdOfficeProgram", caseFileMgmtLocationInDto.getCdOfficeProgram())
				.setParameter("cdOfficeRegion", caseFileMgmtLocationInDto.getCdOfficeRegion())
				.setResultTransformer(Transformers.aliasToBean(CaseFileMgmtLocatioOutDto.class)));

		List<CaseFileMgmtLocatioOutDto> caseFileMgmtLocatioOutDtos = new ArrayList<>();
		caseFileMgmtLocatioOutDtos = (List<CaseFileMgmtLocatioOutDto>) sQLQuery1.list();

		if (!CollectionUtils.isEmpty(caseFileMgmtLocatioOutDtos) && caseFileMgmtLocatioOutDtos.size() > 0) {
			caseFileMgmtLocatioOutDto = caseFileMgmtLocatioOutDtos.get(0);
		}

		log.debug("Exiting method caseFileMgmtLocationDetail in CaseFileMgmtLocationDetailDaoImpl");

		return caseFileMgmtLocatioOutDto;
	}

}
