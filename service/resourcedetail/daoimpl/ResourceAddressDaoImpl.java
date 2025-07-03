package us.tx.state.dfps.service.resourcedetail.daoimpl;

import static us.tx.state.dfps.service.common.CodesConstant.CCNTYREG_999;

import java.util.Date;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.ResourceAddress;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.resource.dto.SchoolDistrictDetailsDto;
import us.tx.state.dfps.service.resourcedetail.dao.ResourceAddressDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Resource
 * Address table updation Jan 30, 2018- 12:10:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class ResourceAddressDaoImpl implements ResourceAddressDao {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ResourceAddressDaoImpl.searchSchoolDistrict}")
	private transient String searchSchoolDistrictSql;

	private static final Logger log = Logger.getLogger(ResourceAddressDaoImpl.class);

	/**
	 * 
	 * Method Name: saveResourceAddress Method Description: This method used for
	 * resource address insert,update and delete operations resourceDetailInDto
	 * request
	 * 
	 * @param resourceDetailInDto
	 * @return result @
	 */
	@Override
	public Long saveResourceAddress(ResourceDetailInDto resourceDetailInDto) {
		log.debug("Entering method saveResourceAddress in ResourceAddressDaoImpl");
		Long result = 0L;
		ResourceAddress resourceAddress = null;
		CapsResource capsResource = new CapsResource();
		Date lastUpdatedDt = resourceDetailInDto.getDtLastUpdate();
		switch (resourceDetailInDto.getCdScrDataAction()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			resourceAddress = new ResourceAddress();
			capsResource.setIdResource(resourceDetailInDto.getIdResource());
			resourceAddress.setCapsResource(capsResource);

			resourceAddress.setAddrRsrcAddrZip(resourceDetailInDto.getRsrcAddrZip());
			resourceAddress.setTxtRsrcAddrComments(resourceDetailInDto.getRsrcAddrComments());
			resourceAddress.setAddrRsrcAddrStLn1(resourceDetailInDto.getRsrcAddrStLn1());
			resourceAddress.setAddrRsrcAddrAttn(resourceDetailInDto.getRsrcAddrAttn());
			resourceAddress.setCdRsrcAddrType(resourceDetailInDto.getCdRsrcAddrType());
			resourceAddress.setAddrRsrcAddrStLn2(resourceDetailInDto.getRsrcAddrStLn2());
			resourceAddress.setCdRsrcAddrCounty(resourceDetailInDto.getRsrcAddrCounty());
			resourceAddress.setCdRsrcAddrSchDist(resourceDetailInDto.getCdRsrcAddrSchDist());
			resourceAddress.setNbrRsrcAddrVid(resourceDetailInDto.getNbrRsrcAddrVid());
			resourceAddress.setAddrRsrcAddrCity(resourceDetailInDto.getRsrcAddrCity());
			resourceAddress.setCdRsrcAddrState(resourceDetailInDto.getCdFacilityState());
			if (!ObjectUtils.isEmpty(resourceAddress.getCdRsrcAddrCounty()) &&
					CCNTYREG_999.equals(resourceAddress.getCdRsrcAddrCounty())) {
				resourceAddress.setNmCnty(null);
			}
			resourceAddress.setIndValdtd(resourceDetailInDto.getIndValdtd());
			resourceAddress.setDtValdtd(resourceDetailInDto.getDtValdtd());
			resourceAddress.setDtLastUpdate(new Date());
			resourceAddress.setDtCreateDate(new Date());
			resourceAddress.setIdCreatedBy(resourceDetailInDto.getIdCreatedBy());
			result = (Long) sessionFactory.getCurrentSession().save(resourceAddress);
			break;

		case ServiceConstants.REQ_FUNC_CD_UPDATE:

			resourceAddress = (ResourceAddress) sessionFactory.getCurrentSession().get(ResourceAddress.class,
					resourceDetailInDto.getIdRsrcAddress());
			if (!ObjectUtils.isEmpty(resourceAddress)) {
				capsResource.setIdResource(resourceDetailInDto.getIdResource());
				resourceAddress.setCapsResource(capsResource);

				resourceAddress.setAddrRsrcAddrZip(resourceDetailInDto.getRsrcAddrZip());
				resourceAddress.setTxtRsrcAddrComments(resourceDetailInDto.getRsrcAddrComments());
				resourceAddress.setAddrRsrcAddrStLn1(resourceDetailInDto.getRsrcAddrStLn1());
				resourceAddress.setAddrRsrcAddrAttn(resourceDetailInDto.getRsrcAddrAttn());
				resourceAddress.setCdRsrcAddrType(resourceDetailInDto.getCdRsrcAddrType());
				resourceAddress.setAddrRsrcAddrStLn2(resourceDetailInDto.getRsrcAddrStLn2());
				resourceAddress.setCdRsrcAddrCounty(resourceDetailInDto.getRsrcAddrCounty());
				resourceAddress.setCdRsrcAddrSchDist(resourceDetailInDto.getCdRsrcAddrSchDist());
				resourceAddress.setNbrRsrcAddrVid(resourceDetailInDto.getNbrRsrcAddrVid());
				resourceAddress.setAddrRsrcAddrCity(resourceDetailInDto.getRsrcAddrCity());
				resourceAddress.setCdRsrcAddrState(resourceDetailInDto.getCdFacilityState());
				if (!ObjectUtils.isEmpty(resourceAddress.getCdRsrcAddrCounty()) &&
						CCNTYREG_999.equals(resourceAddress.getCdRsrcAddrCounty())) {
					resourceAddress.setNmCnty(null);
				}
				resourceAddress.setIndValdtd(resourceDetailInDto.getIndValdtd());
				resourceAddress.setDtValdtd(resourceDetailInDto.getDtValdtd());

				resourceAddress.setDtLastUpdate(resourceDetailInDto.getDtLastUpdate());
				resourceAddress.setIdLastUpdatedPerson(resourceDetailInDto.getIdLastUpdatedPerson());
				sessionFactory.getCurrentSession().update(resourceAddress);
				result = resourceDetailInDto.getIdRsrcAddress();
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_ADD_KIN:
			resourceAddress = (ResourceAddress) sessionFactory.getCurrentSession().get(ResourceAddress.class,
					resourceDetailInDto.getIdResource());
			if (!ObjectUtils.isEmpty(resourceAddress) && !ObjectUtils.isEmpty(resourceAddress.getCdRsrcAddrType())
					&& (resourceAddress.getCdRsrcAddrType().equals(resourceDetailInDto.getCdRsrcAddrType()))
					&& lastUpdatedDt.compareTo(resourceAddress.getDtLastUpdate()) == 0) {
				capsResource.setIdResource(resourceDetailInDto.getIdResource());
				resourceAddress.setCapsResource(capsResource);

				resourceAddress.setAddrRsrcAddrZip(resourceDetailInDto.getRsrcAddrZip());
				resourceAddress.setCdRsrcAddrSchDist(resourceDetailInDto.getCdRsrcAddrSchDist());
				resourceAddress.setAddrRsrcAddrCity(resourceDetailInDto.getRsrcAddrCity());
				resourceAddress.setCdRsrcAddrState(resourceDetailInDto.getCdFacilityState());
				resourceAddress.setTxtRsrcAddrComments(resourceDetailInDto.getRsrcAddrComments());
				resourceAddress.setAddrRsrcAddrStLn1(resourceDetailInDto.getRsrcAddrStLn1());
				resourceAddress.setCdRsrcAddrType(resourceDetailInDto.getCdRsrcAddrType());
				resourceAddress.setAddrRsrcAddrStLn2(resourceDetailInDto.getRsrcAddrStLn2());
				resourceAddress.setCdRsrcAddrCounty(resourceDetailInDto.getRsrcAddrCounty());
				if (!ObjectUtils.isEmpty(resourceAddress.getCdRsrcAddrCounty()) &&
						CCNTYREG_999.equals(resourceAddress.getCdRsrcAddrCounty())) {
					resourceAddress.setNmCnty(null);
				}
				resourceAddress.setIndValdtd(resourceDetailInDto.getIndValdtd());
				resourceAddress.setDtValdtd(resourceDetailInDto.getDtValdtd());

				resourceAddress.setDtLastUpdate(resourceDetailInDto.getDtLastUpdate());
				resourceAddress.setIdLastUpdatedPerson(resourceDetailInDto.getIdLastUpdatedPerson());

				if (resourceAddress.getIdRsrcAddress() < 1) {
					resourceAddress.setDtCreateDate(new Date());
					resourceAddress.setIdCreatedBy(resourceDetailInDto.getIdCreatedBy());
				}
				sessionFactory.getCurrentSession().update(resourceAddress);
				result = resourceDetailInDto.getIdRsrcAddress();
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:

			resourceAddress = (ResourceAddress) sessionFactory.getCurrentSession().get(ResourceAddress.class,
					resourceDetailInDto.getIdRsrcAddress());
			if (!ObjectUtils.isEmpty(resourceAddress)) {
				sessionFactory.getCurrentSession().delete(resourceAddress);
				result = resourceDetailInDto.getIdRsrcAddress();
			}
			break;
		}
		if (result == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("caps.resource.address.nodata.update", null, Locale.US));
		}
		log.debug("Exiting method saveResourceAddress in ResourceAddressDaoImpl");
		return result;
	}

	@Override
	public List<SchoolDistrictDetailsDto> getSchoolDistrict(String cdSchDistTxCounty) {
		log.debug("Entering method getSchoolDistrict in ResourceAddressDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(searchSchoolDistrictSql)
				.addScalar("cdSchDist", StandardBasicTypes.STRING)
				.addScalar("cdSchDistTxCounty", StandardBasicTypes.STRING)
				.addScalar("txtSchDistName", StandardBasicTypes.STRING)
				.setParameter("cdSchDistTxCounty", cdSchDistTxCounty)
				.setResultTransformer(Transformers.aliasToBean(SchoolDistrictDetailsDto.class)));

		List<SchoolDistrictDetailsDto> schoolDistrictDetailsDtoList = (List<SchoolDistrictDetailsDto>) sQLQuery1.list();
		if (CollectionUtils.isEmpty(schoolDistrictDetailsDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("school.district.not.found", null, Locale.US));
		}
		return schoolDistrictDetailsDtoList;
	}

}
