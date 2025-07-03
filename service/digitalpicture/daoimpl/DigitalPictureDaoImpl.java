/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 2, 2017- 5:14:41 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.digitalpicture.daoimpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.ExtDocumentation;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PictureDetail;
import us.tx.state.dfps.service.common.request.DigitalPictureReq;
import us.tx.state.dfps.service.cpsinv.dto.StagePersonValueDto;
import us.tx.state.dfps.service.digitalpicture.dao.DigitalPictureDao;
import us.tx.state.dfps.service.workload.dto.ExternalDocumentDetailDto;
import us.tx.state.dfps.web.casemanagement.bean.PictureDetailBean;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 2, 2017- 5:14:41 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class DigitalPictureDaoImpl implements DigitalPictureDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${DigitalPictureDaoImpl.getExtDocDtl}")
	private String getExtDocDtlSql;

	@Value("${DigitalPictureDaoImpl.getCountExtDocDtl}")
	private String getCountExtDocDtlSql;

	@Value("${DigitalPictureDaoImpl.getDigitalPictureListSql}")
	private String getDigitalPictureListSql;

	@Value("${DigitalPictureDaoImpl.getDigitalPictureDetailsSql}")
	private String getDigitalPictureDetailsSql;

	@Value("${DigitalPictureDaoImpl.getCaseids}")
	private String getCaseids;

	private static final Logger log = Logger.getLogger(DigitalPictureDaoImpl.class);

	public DigitalPictureDaoImpl() {

	}

	@Override
	public PictureDetail addDigitalPicture(DigitalPictureReq digitalPictureReq) {
		PictureDetailBean bean = digitalPictureReq.getPictureDetailBean();
		Person person = null;
		Integer personId = bean.getIdPerson();
		if (personId != 0 && personId != null) {
			person = (Person) sessionFactory.getCurrentSession().get(Person.class, Long.valueOf(personId));
		}
		CapsCase capsCase = null;
		Integer caseId = bean.getIdCase();
		if (caseId != null) {
			capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, Long.valueOf(caseId));
		}
		ExtDocumentation extDocumentation = null;
		Integer extDocId = bean.getIdExtDocumentation();
		if (extDocId != null) {
			extDocumentation = (ExtDocumentation) sessionFactory.getCurrentSession().get(ExtDocumentation.class,
					new Long(extDocId));
		}
		PictureDetail pictureDetail = new PictureDetail();
		pictureDetail.setPerson(person);
		pictureDetail.setCapsCase(capsCase);
		pictureDetail.setExtDocumentation(extDocumentation);
		pictureDetail.setCdPictureType(bean.getCdPictureType());
		pictureDetail.setDtLastUpdate(bean.getDtLastUpdate());
		pictureDetail.setDtPictureTaken(bean.getDtPictureTaken());
		//Defect#13210 setting the uploaded date from the request object
		if (!ObjectUtils.isEmpty(bean.getDtPictureUploaded())) {
			pictureDetail.setDtPictureUploaded(bean.getDtPictureUploaded());
		} else {
			pictureDetail.setDtPictureUploaded(new Date());
		}
		pictureDetail.setNmImage(bean.getNmImage());
		pictureDetail.setNmOtherPhotographer(bean.getNmOtherPhotographer());
		pictureDetail.setNmThumbnail(bean.getNmThumbnail());
		pictureDetail.setTxtDescription(bean.getDescription());
		pictureDetail.setTxtSubject(bean.getSubject());
		sessionFactory.getCurrentSession().save("PictureDetail", pictureDetail);
		log.info("pictureDetail.getIdPictureDetail(): " + pictureDetail.getIdPictureDetail());
		return pictureDetail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.digitalpicture.dao.DigitalPictureDao#
	 * getDigitalPictureDetails(int)
	 */
	@Override
	public PictureDetail getPictureDetailById(int idPictureDetail) {
		PictureDetail pictureDetail = null;
		pictureDetail = (PictureDetail) sessionFactory.getCurrentSession().get(PictureDetail.class,
				Long.valueOf(idPictureDetail));
		return pictureDetail;
	}

	/**
	 * 
	 * Method Description: This DAM is used to get picture details based on
	 * idPictureDetail
	 * 
	 * @param idPictureDetail
	 * @return PictureDetailBean @
	 */
	@Override
	public PictureDetailBean getDigitalPictureDetails(int idPictureDetail, String idUser) {
		PictureDetailBean pictureDetails = (PictureDetailBean) (((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getDigitalPictureDetailsSql).setParameter("idPicDetail", idPictureDetail))
						.addScalar("idPictureDetail", StandardBasicTypes.INTEGER)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idExtDocumentation", StandardBasicTypes.INTEGER)
						.addScalar("idPerson", StandardBasicTypes.INTEGER)
						.addScalar("personName", StandardBasicTypes.STRING)
						.addScalar("idCase", StandardBasicTypes.INTEGER)
						.addScalar("cdPictureType", StandardBasicTypes.STRING)
						.addScalar("subject", StandardBasicTypes.STRING)
						.addScalar("nmOtherPhotographer", StandardBasicTypes.STRING)
						.addScalar("dtPictureTaken", StandardBasicTypes.DATE)
						.addScalar("tmStrAmPmPicTaken", StandardBasicTypes.STRING)
						.addScalar("tmStrPicTaken", StandardBasicTypes.STRING)
						.addScalar("description", StandardBasicTypes.STRING)
						.addScalar("dtPictureUploaded", StandardBasicTypes.DATE)
						.addScalar("nmImage", StandardBasicTypes.STRING)
						.addScalar("nmThumbnail", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PictureDetailBean.class))).uniqueResult();

		pictureDetails.setCaseIds(getCaseIds(Integer.valueOf(idUser)));
		return pictureDetails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.digitalpicture.dao.DigitalPictureDao#
	 * updateDigitalPicture(us.tx.state.dfps.service.common.request.
	 * DigitalPictureReq)
	 */
	@Override
	public PictureDetail updateDigitalPicture(DigitalPictureReq digitalPictureReq) {
		PictureDetail pictureDetail = null;
		PictureDetailBean bean = digitalPictureReq.getPictureDetailBean();
		Integer pictureId = bean.getIdPictureDetail();
		if (pictureId != null) {
			pictureDetail = (PictureDetail) sessionFactory.getCurrentSession().get(PictureDetail.class,
					Long.valueOf(pictureId));
			pictureDetail.setCdPictureType(bean.getCdPictureType());
			pictureDetail.setTxtSubject(bean.getSubject());
			pictureDetail.setNmOtherPhotographer(
					StringUtils.isNotEmpty(bean.getNmOtherPhotographer()) ? bean.getNmOtherPhotographer() : null);
			pictureDetail.setDtPictureTaken(bean.getDtPictureTaken());
			if (StringUtils.isNotEmpty(bean.getDescription()))
				pictureDetail.setTxtDescription(bean.getDescription());
			pictureDetail.setDtLastUpdate(new Date());
		}
		Person person = null;
		Integer personId = bean.getIdPerson();
		if (personId != null) {
			person = (Person) sessionFactory.getCurrentSession().get(Person.class, new Long(personId));
			if (!ObjectUtils.isEmpty(pictureDetail))
				pictureDetail.setPerson(person);
		}
		CapsCase capsCase = null;
		Integer caseId = bean.getIdCase();
		if (caseId != null) {
			capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, new Long(caseId));
			if (!ObjectUtils.isEmpty(pictureDetail))
				pictureDetail.setCapsCase(capsCase);
		}
		sessionFactory.getCurrentSession().update("PictureDetail", pictureDetail);
		return pictureDetail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.digitalpicture.dao.DigitalPictureDao#
	 * getExtDocDetail(int idExtDocDetail)
	 */
	public ExternalDocumentDetailDto getExtDocDetail(int idExtDocDetail) {
		ExternalDocumentDetailDto externalDocumentDetailDto = null;
		Query extDocDtlQuery = sessionFactory.getCurrentSession().createSQLQuery(getExtDocDtlSql)
				.addScalar("idExtSitInfo", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtExtDocObtained", StandardBasicTypes.TIMESTAMP)
				.addScalar("extDocDetails", StandardBasicTypes.STRING)
				.addScalar("cdExtDocType", StandardBasicTypes.STRING)
				.addScalar("extDocLocation", StandardBasicTypes.STRING)
				.addScalar("idDocRepository", StandardBasicTypes.LONG)
				.addScalar("cdExtDocSort", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ExternalDocumentDetailDto.class));
		extDocDtlQuery.setParameter("idExtDocDetail", idExtDocDetail);
		externalDocumentDetailDto = (ExternalDocumentDetailDto) extDocDtlQuery.uniqueResult();
		log.info("externalDocumentDetailDto: " + externalDocumentDetailDto);
		return externalDocumentDetailDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.digitalpicture.dao.DigitalPictureDao#
	 * checkAndUpdateExternalDocumentation(us.tx.state.dfps.service.common.
	 * request.DigitalPictureReq)
	 */
	@Override
	public boolean checkAndUpdateExternalDocumentation(DigitalPictureReq digitalPictureReq) {
		boolean isLastExtDoc = false;
		ExternalDocumentDetailDto externalDocumentDetailDto = null;
		Integer idExtDocumentationOld = digitalPictureReq.getPictureDetailBean().getIdExtDocumentation();
		log.info("idExtDocumentationOld: " + idExtDocumentationOld);
		// Get number of cases associated with that External Document
		Query queryCountPic = sessionFactory.getCurrentSession().createSQLQuery(getCountExtDocDtlSql)
				.addScalar("count_nbr_pic", StandardBasicTypes.BIG_INTEGER);
		queryCountPic.setParameter("idExtDocDetail", idExtDocumentationOld);
		Long count = ((BigInteger) queryCountPic.uniqueResult()).longValue();
		log.info("Totcal count of ext doc: " + count);
		ExtDocumentation extDocumentation = new ExtDocumentation();
		PictureDetailBean bean = digitalPictureReq.getPictureDetailBean();
		externalDocumentDetailDto = getExtDocDetail(bean.getIdExtDocumentation());
		CapsCase capsCase = null;
		Integer caseId = bean.getIdCase();
		if (caseId != null) {
			capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, Long.valueOf(caseId));
		}
		extDocumentation.setDtLastUpdate(new Date());
		extDocumentation.setCapsCase(capsCase);
		extDocumentation.setDtExtDocObtained(new Date());
		extDocumentation.setTxtExtDocDetails(externalDocumentDetailDto.getExtDocDetails());
		extDocumentation.setCdExtDocType(externalDocumentDetailDto.getCdExtDocType());
		extDocumentation.setTxtExtDocLocation(externalDocumentDetailDto.getExtDocLocation());
		extDocumentation.setCdExtDocSort(externalDocumentDetailDto.getCdExtDocSort());
		extDocumentation.setIdDocRepository(externalDocumentDetailDto.getIdDocRepository());
		// insert external doc
		sessionFactory.getCurrentSession().save("ExtDocumentation", extDocumentation);
		// update picture details setting the external documentation id
		PictureDetail pictureDetail = null;
		Integer idPictureDetail = bean.getIdPictureDetail();
		if (idPictureDetail != null) {
			// Insert an External Documentation ID in the Picture_Detail table
			pictureDetail = (PictureDetail) sessionFactory.getCurrentSession().get(PictureDetail.class,
					Long.valueOf(idPictureDetail));
			log.info("Update...new" + extDocumentation.getIdExtDocumentation());
			pictureDetail.setExtDocumentation(extDocumentation);
			sessionFactory.getCurrentSession().update("PictureDetail", pictureDetail);
		}
		if (count == 1) {
			ExtDocumentation extDoc = null;
			if (idExtDocumentationOld != null) {
				extDoc = (ExtDocumentation) sessionFactory.getCurrentSession().get(ExtDocumentation.class,
						Long.valueOf(idExtDocumentationOld));
				log.info("delete..." + extDoc.getIdExtDocumentation());
			}
			// Delete old id from ext documentation
			sessionFactory.getCurrentSession().delete("ExtDocumentation", extDoc);
			isLastExtDoc = true;
		}
		return isLastExtDoc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.digitalpicture.dao.DigitalPictureDao#
	 * checkAndUpdateExternalDocumentation(us.tx.state.dfps.service.common.
	 * request.DigitalPictureReq)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PictureDetailBean> getDigitalPictureList(int extDocmnttnID) {
		List<PictureDetailBean> picBeanList = new ArrayList<PictureDetailBean>();
		Query extDocDtlQuery = sessionFactory.getCurrentSession().createSQLQuery(getDigitalPictureListSql)
				.addScalar("idPictureDetail", StandardBasicTypes.INTEGER)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idExtDocumentation", StandardBasicTypes.INTEGER)
				.addScalar("idPerson", StandardBasicTypes.INTEGER).addScalar("personName", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.INTEGER).addScalar("cdPictureType", StandardBasicTypes.STRING)
				.addScalar("subject", StandardBasicTypes.STRING)
				.addScalar("nmOtherPhotographer", StandardBasicTypes.STRING)
				.addScalar("dtPictureTaken", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPictureUploaded", StandardBasicTypes.TIMESTAMP)
				.addScalar("description", StandardBasicTypes.STRING).addScalar("nmImage", StandardBasicTypes.STRING)
				.addScalar("nmThumbnail", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PictureDetailBean.class));
		extDocDtlQuery.setParameter("idExtDocumentation", extDocmnttnID);
		picBeanList = extDocDtlQuery.list();
		return picBeanList;
	}

	/**
	 * 
	 * Method Description: Gets a list of Case Id in which the User is a Primary
	 * or Secondary
	 * 
	 * @param idUser
	 * @return HashMap @
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private HashMap getCaseIds(int idUser) {
		HashMap caseIds = new HashMap();

		List<StagePersonValueDto> CaseIdList = ((List<StagePersonValueDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getCaseids).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("nmCase", StandardBasicTypes.STRING).setParameter("idPerson", idUser)
				.setResultTransformer(Transformers.aliasToBean(StagePersonValueDto.class)).list());

		CaseIdList.forEach(caseid -> {
			caseIds.put(caseid.getIdCase(), caseid.getNmCase());
		});
		return caseIds;
	}

}
