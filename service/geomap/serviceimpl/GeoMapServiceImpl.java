package us.tx.state.dfps.service.geomap.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.GeoMapReq;
import us.tx.state.dfps.service.common.response.AddressDtlRes;
import us.tx.state.dfps.service.geomap.dao.GeoMapDao;
import us.tx.state.dfps.service.geomap.service.GeoMapService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * is used to get the address of all the schools with in given boundaries Aug
 * 07, 2017- 4:24:28 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
@Transactional
public class GeoMapServiceImpl implements GeoMapService {

	@Autowired
	GeoMapDao geoMapDao;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public AddressDtlRes getSchoolAddressDtls(GeoMapReq req) {
		return geoMapDao.getSchoolAddressDtls(req);
	}

}
