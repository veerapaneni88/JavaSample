package us.tx.state.dfps.service.admin.daoimpl;

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

import us.tx.state.dfps.service.admin.dao.UnitEmpLinkDao;
import us.tx.state.dfps.service.admin.dto.EmpLinkInDto;
import us.tx.state.dfps.service.admin.dto.EmpLinkOutDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkInDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Retrieve
 * data from Unit Emp Link Aug 10, 2017- 2:30:31 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class UnitEmpLinkDaoImpl implements UnitEmpLinkDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${UnitEmpLinkDaoImpl.getUnitEmpLinkRecord}")
	private transient String getUnitEmpLinkRecord;

	@Value("${UnitEmpLinkDao.getUnitEmpLinkDtl}")
	private String getUnitEmpLinkDtlSql;
	
	@Value("${UnitEmpLinkDaoImpl.getEmpLinkOutDto}")
	private String getEmpLinkOutDtoSql;

	private static final Logger log = Logger.getLogger(UnitEmpLinkDaoImpl.class);

	public UnitEmpLinkDaoImpl() {
		super();
	}

	/**
	 *
	 * @param pInputDataRec
	 * @return List<Csec04doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UnitEmpLinkOutDto> getUnitEmpLinkRecord(UnitEmpLinkInDto pInputDataRec) {
		log.debug("Entering method UnitEmpLinkQUERYdam in UnitEmpLinkDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getUnitEmpLinkRecord)
				.addScalar("idUnitEmpLink", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idUnit", StandardBasicTypes.LONG).addScalar("cdUnitMemberRole")
				.addScalar("cdUnitMemberInOut").setString("hI_V1", ServiceConstants.V1)
				.setParameter("cdUnitMemberRole", pInputDataRec.getCdUnitMemberRole())
				.setParameter("idPerson", pInputDataRec.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(UnitEmpLinkOutDto.class)));
		List<UnitEmpLinkOutDto> liCsec04doDto = (List<UnitEmpLinkOutDto>) sQLQuery1.list();
		log.debug("Exiting method UnitEmpLinkQUERYdam in UnitEmpLinkDaoImpl");
		return liCsec04doDto;
	}

	/**
	 *
	 * @param pInputDataRec
	 * @return List<Csec04doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmpLinkOutDto> getUnitEmpLinkRecord(EmpLinkInDto pInputDataRec) {
		log.debug("Entering method getUnitEmpLinkRecord in Csec04dDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEmpLinkOutDtoSql)
				.addScalar("idUnitEmpLink", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idUnit", StandardBasicTypes.LONG).addScalar("cdUnitMemberRole")
				.addScalar("cdUnitMemberInOut").setString("hI_V1", ServiceConstants.V1)
				.setParameter("cdUnitMemberRole", pInputDataRec.getCdUnitMemberRole())
				.setParameter("idPerson", pInputDataRec.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(EmpLinkOutDto.class)));

		List<EmpLinkOutDto> liCsec04doDto = (List<EmpLinkOutDto>) sQLQuery1.list();
		log.debug("Exiting method getUnitEmpLinkRecord in Csec04dDaoImpl");
		return liCsec04doDto;
	}

	@Override
	public List<UnitEmpLinkDto> getUnitEmpLinkDtl(UnitEmpLinkDto unitEmpLinkDto) {

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getUnitEmpLinkDtlSql)
				.addScalar("idEmployeeSkill", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdEmpSkill", StandardBasicTypes.STRING).addScalar("idUnitEmpLink", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate1", StandardBasicTypes.DATE).addScalar("idUnit", StandardBasicTypes.LONG)
				.addScalar("unitMemberRole", StandardBasicTypes.STRING)
				.addScalar("unitMemberInOut", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate2", StandardBasicTypes.DATE).addScalar("nbrUnit", StandardBasicTypes.STRING)
				.addScalar("cdUnitRegion", StandardBasicTypes.STRING)
				.addScalar("cdUnitProgram", StandardBasicTypes.STRING)
				.addScalar("idUnitParent", StandardBasicTypes.LONG)
				.addScalar("cdUnitSpecialization", StandardBasicTypes.STRING)
				.setParameter("cdUnitRegion", unitEmpLinkDto.getCdUnitRegion())
				.setParameter("cdUnitProgram", unitEmpLinkDto.getCdUnitProgram())
				.setParameter("cdEmpSkill", unitEmpLinkDto.getCdEmpSkill())
				.setResultTransformer(Transformers.aliasToBean(UnitEmpLinkDto.class)));
		List<UnitEmpLinkDto> unitEmpLinkList = (List<UnitEmpLinkDto>) sQLQuery1.list();
		return unitEmpLinkList;
	}
}
