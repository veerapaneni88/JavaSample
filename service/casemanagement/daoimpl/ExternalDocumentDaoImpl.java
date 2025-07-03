/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 11, 2017- 6:02:10 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.casemanagement.dto.ExternalDocumentationDto;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.ExtDocumentation;
import us.tx.state.dfps.common.domain.PictureDetail;
import us.tx.state.dfps.service.casemanagement.dao.ExternalDocumentDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 11, 2017- 6:02:10 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class ExternalDocumentDaoImpl implements ExternalDocumentDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ExternalDocumentDaoImpl.getIntakeDateSql}")
	private String getIntakeDateSql;

	public ExternalDocumentDaoImpl() {

	}

	/**
	 * Method Name: externaldocumentationAUD Description:This method performs
	 * adds, updates, and deletes on the EXT_DOCUMENTATION table
	 * 
	 * @param ExternalDocumentationDto
	 * @return Long @
	 */

	@Override
	public Long externaldocumentationAUD(ExternalDocumentationDto externalDocumentationDto, String cReqFuncCd,
			Long idExtDocSelected) {
		Long result = 0L;
		// The following code is for adding a new External documentation
		if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(cReqFuncCd)) {
			ExtDocumentation extDoc = new ExtDocumentation();
			CapsCase capsCase = new CapsCase();
			if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getIdCase()))
				capsCase.setIdCase(externalDocumentationDto.getIdCase());
			extDoc.setCapsCase(capsCase);
			extDoc.setDtExtDocObtained(externalDocumentationDto.getDtExternalDocumentObtained());
			if(ObjectUtils.isEmpty(extDoc.getDtExtDocObtained())) extDoc.setDtExtDocObtained(new Date());
			
			extDoc.setDtLastUpdate(new Date());
			if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getExternalDocumentDetails()))
				extDoc.setTxtExtDocDetails(externalDocumentationDto.getExternalDocumentDetails());
			if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getExternalDocumentType()))
				extDoc.setCdExtDocType(externalDocumentationDto.getExternalDocumentType());
			if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getExternalDocumentLocation()))
				extDoc.setTxtExtDocLocation(externalDocumentationDto.getExternalDocumentLocation());
			if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getExternalDocumentSort()))
				extDoc.setCdExtDocSort(externalDocumentationDto.getExternalDocumentSort());
			if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getIdDocumentRepository()))
				extDoc.setIdDocRepository(externalDocumentationDto.getIdDocumentRepository());
			// will return the generated primary key
			result = (Long) sessionFactory.getCurrentSession().save(extDoc);
		}
		// The following code is for Updating an External documentation
		else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(cReqFuncCd)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExtDocumentation.class)
					.add(Restrictions.eq("idExtDocumentation", idExtDocSelected));
			ExtDocumentation extDoc = (ExtDocumentation) criteria.uniqueResult();
			if (null != extDoc) {
				if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getDtExternalDocumentObtained()))
					extDoc.setDtExtDocObtained(externalDocumentationDto.getDtExternalDocumentObtained());
				if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getExternalDocumentDetails()))
					extDoc.setTxtExtDocDetails(externalDocumentationDto.getExternalDocumentDetails());
				if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getExternalDocumentType()))
					extDoc.setCdExtDocType(externalDocumentationDto.getExternalDocumentType());
				if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getExternalDocumentLocation()))
					extDoc.setTxtExtDocLocation(externalDocumentationDto.getExternalDocumentLocation());
				if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getExternalDocumentSort()))
					extDoc.setCdExtDocSort(externalDocumentationDto.getExternalDocumentSort());
				if (!TypeConvUtil.isNullOrEmpty(externalDocumentationDto.getIdDocumentRepository()))
					extDoc.setIdDocRepository(externalDocumentationDto.getIdDocumentRepository());
				sessionFactory.getCurrentSession().update(extDoc);
				result = idExtDocSelected;
			}
		}
		// The following code is for deleting an External documentation
		else if (ServiceConstants.REQ_FUNC_CD_DELETE.equalsIgnoreCase(cReqFuncCd)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExtDocumentation.class)
					.add(Restrictions.eq("idExtDocumentation", idExtDocSelected));
			ExtDocumentation extDoc = (ExtDocumentation) criteria.uniqueResult();
			if (null != extDoc) {
				sessionFactory.getCurrentSession().delete(extDoc);
				result = idExtDocSelected;
			} else {
				result = -1L;
			}
		}
		return result;
	}

	/**
	 * Method Name: fetchExternaldocumentation Description: retrieves all
	 * documents associated with the ID CASE
	 * 
	 * @return List<ExternalDocumentationDto>
	 * @param idCase
	 * @param idUser
	 */
	@Override
	public List<ExternalDocumentationDto> fetchExternaldocumentation(long idCase) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExtDocumentation.class)
				.setProjection(Projections.projectionList().add(Projections.property("capsCase.idCase"), "idCase")
						.add(Projections.property("idExtDocumentation"), "idExtSitInfo")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("dtExtDocObtained"), "dtExternalDocumentObtained")
						.add(Projections.property("txtExtDocDetails"), "externalDocumentDetails")
						.add(Projections.property("cdExtDocType"), "externalDocumentType")
						.add(Projections.property("txtExtDocLocation"), "externalDocumentLocation")
						.add(Projections.property("cdExtDocSort"), "externalDocumentSort")
						.add(Projections.property("idDocRepository"), "idDocumentRepository"))
				.add(Restrictions.eq("capsCase.idCase", idCase)).addOrder(Order.asc("cdExtDocSort"))
				.addOrder(Order.desc("dtExtDocObtained"));
		// Transform the criteria result to List<ExternalDocumentationDto>
		List<ExternalDocumentationDto> extDocList = (List<ExternalDocumentationDto>) criteria
				.setResultTransformer(Transformers.aliasToBean(ExternalDocumentationDto.class)).list();
		// for Digital Pictures external documentation get the number of digital
		// pictures
		extDocList.stream().forEach(dto -> {
			if ("PD".equalsIgnoreCase(dto.getExternalDocumentType())) {
				dto.setCountPictureDetail(getCountPictDetail(dto.getIdExtSitInfo()));
			} else {
				dto.setCountPictureDetail(0);
			}
		});
		return extDocList;
	}

	/**
	 * Method Name: getIntakeDate Description: will return the open Date for a
	 * givin case ID
	 * 
	 * @return Date
	 * @param idCase
	 */
	@Override
	public Date getIntakeDate(Long idCase) {
		Query queryDate = sessionFactory.getCurrentSession().createSQLQuery(getIntakeDateSql);
		queryDate.setParameter("idCase", idCase);
		Date intakeDate = ((Date) queryDate.uniqueResult());
		if (intakeDate == null) {
			CapsCase caseobj = (CapsCase) sessionFactory.getCurrentSession().load(CapsCase.class, idCase);
			intakeDate = caseobj.getDtCaseOpened();
		}
		return intakeDate;
	}

	/**
	 * Method Name: getCountPictDetail Description: will return number of
	 * pictures for each external documentation iID
	 * 
	 * @return int
	 * @param idExtDoc
	 */
	private int getCountPictDetail(long idExtDoc) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PictureDetail.class);
		criteria.add(Restrictions.eq("extDocumentation.idExtDocumentation", idExtDoc));
		criteria.setProjection(Projections.rowCount());
		int count = Integer.valueOf(criteria.uniqueResult().toString());
		return count;
	}
}
