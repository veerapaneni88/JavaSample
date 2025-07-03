/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 13, 2018- 11:16:54 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.workload.daoimpl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.workload.dao.ExternalWorkloadDao;
import us.tx.state.dfps.service.workload.dto.RCCPWorkloadDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 13, 2018- 11:16:54 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class ExternalWorkloadDaoImpl implements ExternalWorkloadDao {

	@Value("${ExternalWorkloadDao.getWorkload}")
	private String getWorkloadSql;

	@Value("${ExternalWorkloadDao.serach}")
	private String serach;

	@Value("${ExternalWorkloadDao.serachByPersonId}")
	private String serachByPersonId;

	@Value("${ExternalWorkloadDao.serachByStatus}")
	private String serachByStatus;

	@Value("${ExternalWorkloadDao.serachByDateOfBirth}")
	private String serachByDateOfBirth;

	@Value("${ExternalWorkloadDao.serachByDueDate}")
	private String serachByDueDate;

	@Value("${ExternalWorkloadDao.serachByLastEdited}")
	private String serachByLastEdited;

	@Value("${ExternalWorkloadDao.serachByPrimaryWorker}")
	private String serachByPrimaryWorker;

	@Value("${ExternalWorkloadDao.serachByNmFirst}")
	private String serachByNmFirst;

	@Value("${ExternalWorkloadDao.serachByNmLast}")
	private String serachByNmLast;

	/*
	 * @Value("${ExternalWorkloadDao.serachByNmFosterParent}") private String
	 * serachByNmFosterParent ;
	 */

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: getExternalWorkloadDetails Method Description:
	 * 
	 * @param rccpWorkloadDto
	 * @return List<RCCPWorkloadDto>
	 */
	@Override
	public List<RCCPWorkloadDto> getExternalWorkloadDetails(RCCPWorkloadDto rccpWorkloadDto) {
		List<RCCPWorkloadDto> rccpWorkloadDtlsList = null;

		Query query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getWorkloadSql)
				.setParameter("idUser", rccpWorkloadDto.getIdUser())).addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("nmPerson", StandardBasicTypes.STRING)
						.addScalar("dtDateOfBirth", StandardBasicTypes.DATE)
						.addScalar("idResource", StandardBasicTypes.LONG)
						.addScalar("nmResource", StandardBasicTypes.STRING)
						// .addScalar("nmFosterParent",
						// StandardBasicTypes.STRING)
						.addScalar("cdStatus", StandardBasicTypes.STRING)
						.addScalar("dtDueDate", StandardBasicTypes.DATE)
						.addScalar("lastEdited", StandardBasicTypes.STRING)
						.addScalar("primaryWorker", StandardBasicTypes.STRING)
						.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("nmCase", StandardBasicTypes.STRING)/*.addScalar("idEvent", StandardBasicTypes.LONG)*/
						.addScalar("alertsAvailable", StandardBasicTypes.STRING)
						.addScalar("cpExitst", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(RCCPWorkloadDto.class));
		rccpWorkloadDtlsList = (List<RCCPWorkloadDto>) query.list();
		return rccpWorkloadDtlsList;
	}

	/**
	 * 
	 * Method Name: searchExternalWorkloadDetails Method Description:
	 * 
	 * @param rccpWorkloadDto
	 * @return List<RCCPWorkloadDto>
	 */
	public List<RCCPWorkloadDto> searchExternalWorkloadDetails(RCCPWorkloadDto rccpWorkloadDto) {
		List<RCCPWorkloadDto> rccpWorkloadDtlsList = null;

		StringBuilder qryString = new StringBuilder(serach);
		qryString.append(" ( ");
		qryString.append(getWorkloadSql);
		qryString.append(" )");
		boolean isWhereAdded = false;

		if (!ObjectUtils.isEmpty(rccpWorkloadDto.getIdPerson())) {
			isWhereAdded(qryString, isWhereAdded);
			qryString.append(serachByPersonId);
			isWhereAdded = true;
		}
		if (!ObjectUtils.isEmpty(rccpWorkloadDto.getCdStatus())) {
			isWhereAdded(qryString, isWhereAdded);
			addAnd(qryString, isWhereAdded);
			isWhereAdded = true;

			qryString.append(serachByStatus);

		}

		if (!ObjectUtils.isEmpty(rccpWorkloadDto.getDtDateOfBirth())) {
			isWhereAdded(qryString, isWhereAdded);

			addAnd(qryString, isWhereAdded);
			isWhereAdded = true;
			qryString.append(serachByDateOfBirth);

		}
		if (!ObjectUtils.isEmpty(rccpWorkloadDto.getDtDueDate())) {
			isWhereAdded(qryString, isWhereAdded);

			addAnd(qryString, isWhereAdded);
			qryString.append(serachByDueDate);
			isWhereAdded = true;

		}
		if (StringUtils.isNotBlank(rccpWorkloadDto.getLastEdited())) {
			isWhereAdded(qryString, isWhereAdded);

			addAnd(qryString, isWhereAdded);

			qryString.append(serachByLastEdited);
			isWhereAdded = true;

		}
		if (StringUtils.isNotBlank(rccpWorkloadDto.getPrimaryWorker())) {
			isWhereAdded(qryString, isWhereAdded);

			addAnd(qryString, isWhereAdded);

			qryString.append(serachByPrimaryWorker);
			isWhereAdded = true;

		}
		/*
		 * if(StringUtils.isNotBlank(rccpWorkloadDto.getNmFosterParent())){
		 * isWhereAdded(qryString,isWhereAdded) ;
		 * 
		 * if(isWhereAdded){ qryString.append(ServiceConstants.SPACE +
		 * ServiceConstants.AND ) ; }
		 * 
		 * qryString.append(serachByNmFosterParent); isWhereAdded = true;
		 * 
		 * }
		 */
		if (StringUtils.isNotBlank(rccpWorkloadDto.getNmFirst())) {
			isWhereAdded(qryString, isWhereAdded);

			addAnd(qryString, isWhereAdded);

			qryString.append(serachByNmFirst);
			isWhereAdded = true;

		}
		if (StringUtils.isNotBlank(rccpWorkloadDto.getNmLast())) {
			isWhereAdded(qryString, isWhereAdded);

			addAnd(qryString, isWhereAdded);

			qryString.append(serachByNmLast);

		}

		Query query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(qryString.toString())
				.setParameter("idUser", rccpWorkloadDto.getIdUser())).addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("nmPerson", StandardBasicTypes.STRING)
						.addScalar("nmFirst", StandardBasicTypes.STRING).addScalar("nmLast", StandardBasicTypes.STRING)
						.addScalar("dtDateOfBirth", StandardBasicTypes.DATE)
						.addScalar("idResource", StandardBasicTypes.LONG)
						.addScalar("nmResource", StandardBasicTypes.STRING)
						// .addScalar("nmFosterParent",
						// StandardBasicTypes.STRING)
						.addScalar("cdStatus", StandardBasicTypes.STRING)
						.addScalar("dtDueDate", StandardBasicTypes.DATE)
						.addScalar("lastEdited", StandardBasicTypes.STRING)
						.addScalar("primaryWorker", StandardBasicTypes.STRING)
						.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("nmCase", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("alertsAvailable", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(RCCPWorkloadDto.class));
		if (qryString.toString().contains(":idPerson")) {
			query.setParameter("idPerson", rccpWorkloadDto.getIdPerson());
		}
		if (qryString.toString().contains(":cdStatus")) {
			query.setParameter("cdStatus", rccpWorkloadDto.getCdStatus());
		}
		if (qryString.toString().contains(":dtDateOfBirth")) {
			query.setParameter("dtDateOfBirth", DateUtils.dateStringInSlashFormat(rccpWorkloadDto.getDtDateOfBirth()));
		}
		if (qryString.toString().contains(":dtDueDate")) {
			query.setParameter("dtDueDate", DateUtils.dateStringInSlashFormat(rccpWorkloadDto.getDtDueDate()));
		}
		if (qryString.toString().contains(":lastEdited")) {
			query.setParameter("lastEdited", TypeConvUtil.stringHelperForLike(rccpWorkloadDto.getLastEdited()));
		}
		if (qryString.toString().contains(":primaryWorker")) {
			query.setParameter("primaryWorker", TypeConvUtil.stringHelperForLike(rccpWorkloadDto.getPrimaryWorker()));
		}
		if (qryString.toString().contains(":nmFirst")) {

			query.setParameter("nmFirst", TypeConvUtil.stringHelperForLike(rccpWorkloadDto.getNmFirst()));
		}
		if (qryString.toString().contains(":nmLast")) {
			query.setParameter("nmLast", TypeConvUtil.stringHelperForLike(rccpWorkloadDto.getNmLast()));
		}

		rccpWorkloadDtlsList = (List<RCCPWorkloadDto>) query.list();
		return rccpWorkloadDtlsList;
	}

	/**
	 * Method Name: addAnd Method Description:
	 * 
	 * @param qryString
	 * @param isWhereAdded
	 */
	private void addAnd(StringBuilder qryString, boolean isWhereAdded) {
		if (isWhereAdded) {
			qryString.append(ServiceConstants.SPACE + ServiceConstants.AND + ServiceConstants.SPACE);
		}
	}

	/**
	 * 
	 * Method Name: isWhereAdded Method Description:
	 * 
	 * @param query
	 * @param isWhereAdded
	 */
	private void isWhereAdded(StringBuilder query, boolean isWhereAdded) {
		if (!isWhereAdded) {
			query.append(ServiceConstants.SPACE + ServiceConstants.WHERE + ServiceConstants.SPACE);
		}
	}

}
