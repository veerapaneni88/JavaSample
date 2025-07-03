package us.tx.state.dfps.service.geomap.daoimpl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.request.GeoMapReq;
import us.tx.state.dfps.service.common.response.AddressDtlRes;
import us.tx.state.dfps.service.geomap.dao.GeoMapDao;
import us.tx.state.dfps.service.person.dto.AddressDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao Class to
 * get the school address based on given boundaries Aug 11, 2017- 4:32:25 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class GeoMapDaoImpl implements GeoMapDao {

	@Value("${GeoMapDaoImpl.getSchoolAddressDtls}")
	private String getAllSchoolAddressDtls;

	@Autowired
	private SessionFactory sessionFactory;

	public GeoMapDaoImpl() {

	}

	/**
	 * This method is used to get the address details of all the schools
	 * 
	 * @return AddressDtlRes with list of address details
	 * @param req
	 *            this contains the boundaries to fetch the schools
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AddressDtlRes getSchoolAddressDtls(GeoMapReq req) {
		AddressDtlRes addressDtlRes = new AddressDtlRes();
		List<AddressDto> addressDtoList = null;

		Query querySchoolAddress = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAllSchoolAddressDtls)
				.addScalar("facilityNm", StandardBasicTypes.STRING)
				.addScalar("cdfacilityDistrict", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("addrCity", StandardBasicTypes.STRING).addScalar("cdAddrState", StandardBasicTypes.STRING)
				.addScalar("nmCntry", StandardBasicTypes.STRING).addScalar("addrZip", StandardBasicTypes.STRING)
				.addScalar("phone", StandardBasicTypes.STRING).addScalar("phoneExt", StandardBasicTypes.STRING)
				.addScalar("gcdLatStr", StandardBasicTypes.STRING).addScalar("gcdLongStr", StandardBasicTypes.STRING)
				.addScalar("districtNm", StandardBasicTypes.STRING).setParameter("minlat", req.getMinLat())
				.setParameter("maxlat", req.getMaxLat()).setParameter("minLon", req.getMinLon())
				.setParameter("maxLon", req.getMaxLon())
				.setResultTransformer(Transformers.aliasToBean(AddressDto.class));

		addressDtoList = (List<AddressDto>) querySchoolAddress.list();
		addressDtlRes.setAddressList(addressDtoList);
		return addressDtlRes;
	}

}
