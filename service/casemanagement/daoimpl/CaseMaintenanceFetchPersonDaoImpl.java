package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceFetchPersonDao;
import us.tx.state.dfps.service.casepackage.dto.RtrvPersonInfoInDto;
import us.tx.state.dfps.service.casepackage.dto.RtrvPersonInfoOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceFetchPersonDaoImpl Feb 7, 2018- 5:50:36 PM © 2017
 * Texas Department of Family and Protective Services
 * *************  Change History ********************************
 * artf192340 - 7/12/2021 eggadr - Dev - Case Maintenance for FAD Stage
 */
@Repository
public class CaseMaintenanceFetchPersonDaoImpl implements CaseMaintenanceFetchPersonDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceFetchPersonDaoImpl.strCINV81D_CURSORQuery}")
	private transient String strCINV81D_CURSORQuery;

	private static final Logger log = Logger.getLogger(CaseMaintenanceFetchPersonDaoImpl.class);

	/**
	 *
	 * Method Name: fetchPersonDtl Method Description:This Method is used to
	 * fetch Person Dtl DAM:cinv81d
	 * 
	 * @param rtrvPersonInfoInDto
	 * @param rtrvPersonInfoOutDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fetchPersonDtl(RtrvPersonInfoInDto rtrvPersonInfoInDto, RtrvPersonInfoOutDto rtrvPersonInfoOutDto) {
		log.debug("Entering method fetchPersonDtl in CaseMaintenanceFetchPersonDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCINV81D_CURSORQuery)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				//artf192340
				.addScalar("nbrPersonAge", StandardBasicTypes.SHORT)
				.addScalar("dtPersonDeath", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("txtOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.setParameter("idPerson", rtrvPersonInfoInDto.getUlIdPerson())
				.setResultTransformer(Transformers.aliasToBean(RtrvPersonInfoOutDto.class)));

		List<RtrvPersonInfoOutDto> rtrvPersonInfoOutDtos = new ArrayList<>();
		rtrvPersonInfoOutDtos = (List<RtrvPersonInfoOutDto>) sQLQuery1.list();

		if (!CollectionUtils.isEmpty(rtrvPersonInfoOutDtos)) {
			BeanUtils.copyProperties(rtrvPersonInfoOutDtos.get(0), rtrvPersonInfoOutDto);
		}
		log.debug("Exiting method fetchPersonDtl in CaseMaintenanceFetchPersonDaoImpl");
	}

}
