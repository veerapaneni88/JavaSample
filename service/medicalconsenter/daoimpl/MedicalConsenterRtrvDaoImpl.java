package us.tx.state.dfps.service.medicalconsenter.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.medicalconsenter.dao.MedicalConsenterRtrvDao;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:MedicalConsenterRtrvDaoImpl Oct 25, 2017- 4:32:52 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class MedicalConsenterRtrvDaoImpl implements MedicalConsenterRtrvDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${MedicalConsenterRtrvDaoImpl.getMedicalConsenterDtls}")
	private transient String getMedicalConsenterDtls;

	@Value("${PersonMedicalConsenterRtrvDaoImpl.getMedicalConstDtls}")
	private transient String getMedicalConstDtls;

	private static final Logger log = Logger.getLogger(MedicalConsenterRtrvDaoImpl.class);

	/**
	 * Method Description: This method retrieves the Names of the Medical
	 * Consenter associated with a Primary Child. DAM Name: CLSS99D
	 * 
	 * @param medicalDto
	 * @return List<MedicalConsenterOutDto> @
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<MedicalConsenterDto> getMedicalConsenterDtls(MedicalConsenterDto medicalDto) {
		log.debug("Entering method getMedicalConsenterDtls in MedicalConsenterRtrvDaoImpl");
		List<MedicalConsenterDto> medicalConsenterList = null;
		Query queryMedicalDtls = sessionFactory.getCurrentSession().createSQLQuery(getMedicalConsenterDtls)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.addScalar("idMedConsenterPerson", StandardBasicTypes.LONG)
				.addScalar("cdMedConsenterType", StandardBasicTypes.STRING)
				.addScalar("cdMedConsenterOrder", StandardBasicTypes.STRING)
				.addScalar("cdDfpsDesig", StandardBasicTypes.STRING)
				.addScalar("dtMedConsStart", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(MedicalConsenterDto.class));
		queryMedicalDtls.setParameter("idPerson", medicalDto.getIdPerson());
		queryMedicalDtls.setParameter("idCase", medicalDto.getIdCase());
		medicalConsenterList = (List<MedicalConsenterDto>) queryMedicalDtls.list();
		log.debug("Exiting method getMedicalConsenterDtls in MedicalConsenterRtrvDaoImpl");
		return medicalConsenterList;
	}

	/**
	 * Method Description: This method retrieves the Name and effective Start
	 * date of the Medical Consenter assigned to a Primary Child. DAM Name:
	 * CSECD4D
	 * 
	 * @param medicalConstrDto
	 * @return List<MedicalConsenterRtrvOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MedicalConsenterDto> getMedicalConsenterRecords(MedicalConsenterDto medicalConstrDto) {
		log.debug("Entering method getMedicalConsenterRecords in PersonMedicalConsenterRtrvDaoImpl");
		List<MedicalConsenterDto> medicalConsenterRtrvDtoList = null;
		Query queryMedicalDtls = sessionFactory.getCurrentSession().createSQLQuery(getMedicalConstDtls)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.addScalar("dtMedConsStart", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(MedicalConsenterDto.class));
		queryMedicalDtls.setParameter("idPerson", medicalConstrDto.getIdPerson());
		queryMedicalDtls.setParameter("idCase", medicalConstrDto.getIdCase());
		medicalConsenterRtrvDtoList = (List<MedicalConsenterDto>) queryMedicalDtls.list();
		log.debug("Exiting method getMedicalConsenterRecords in PersonMedicalConsenterRtrvDaoImpl");
		return medicalConsenterRtrvDtoList;
	}
}
