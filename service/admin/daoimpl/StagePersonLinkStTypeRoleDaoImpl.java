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

import us.tx.state.dfps.service.admin.dao.StagePersonLinkStTypeRoleDao;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkStTypeRoleInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkStTypeRoleOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Cint20dDaoImpl Aug 6, 2017- 3:14:28 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class StagePersonLinkStTypeRoleDaoImpl implements StagePersonLinkStTypeRoleDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(StagePersonLinkStTypeRoleDaoImpl.class);

	@Autowired
	@Value("${StagePersonLinkStTypeRoleDaoImpl.stagePersonDtls}")
	String stagePersonDtls;

	public StagePersonLinkStTypeRoleDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: stagePersonDtls Method Description: This method will get
	 * data from Stage Person Link table.
	 * 
	 * @param pInputDataRec
	 * @return List<StagePersonLinkStTypeRoleOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StagePersonLinkStTypeRoleOutDto> stagePersonDtls(
			StagePersonLinkStTypeRoleInDto stagePersonLinkStTypeRoleInDto) throws DataNotFoundException {
		log.debug("Entering method StagePersonLinkStTypeRoleQUERYdam in StagePersonLinkStTypeRoleDaoImpl");
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(stagePersonDtls)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkStTypeRoleOutDto.class)));
		sqlQuery.addScalar("cdStagePersRole", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStagePersType", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indCdStagePersSearch", StandardBasicTypes.STRING);
		sqlQuery.addScalar("stagePersNotes", StandardBasicTypes.STRING);
		sqlQuery.addScalar("dtStagePersLink", StandardBasicTypes.TIMESTAMP);
		sqlQuery.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indStagePersReporter", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING);
		sqlQuery.addScalar("idStage", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idPerson", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idStagePerson", StandardBasicTypes.LONG);
		sqlQuery.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP);
		sqlQuery.setParameter("hI_ulIdStage", stagePersonLinkStTypeRoleInDto.getIdStage());
		sqlQuery.setParameter("hI_szCdStagePersType", stagePersonLinkStTypeRoleInDto.getCdStagePersType());
		sqlQuery.setParameter("hI_szCdStagePersRole", stagePersonLinkStTypeRoleInDto.getCdStagePersRole());
		List<StagePersonLinkStTypeRoleOutDto> liCint20doDto = (List<StagePersonLinkStTypeRoleOutDto>) sqlQuery.list();
		if (TypeConvUtil.isNullOrEmpty(liCint20doDto)) {
			throw new DataNotFoundException(messageSource
					.getMessage("StagePersonLinkStTypeRoleDaoImpl.not.found.hI_ulIdStage", null, Locale.US));
		}
		log.debug("Exiting method StagePersonLinkStTypeRoleQUERYdam in StagePersonLinkStTypeRoleDaoImpl");
		return liCint20doDto;
	}
}
