package us.tx.state.dfps.service.caretakerinfo.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.caretaker.dto.CaretkrInfoCaretakerDto;
import us.tx.state.dfps.service.caretaker.dto.CaretkrInfoResourceDto;
import us.tx.state.dfps.service.caretakerinfo.dao.CareTakerInfoDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Implement
 * the CRUD operation with BD for Caretaker Information page. Feb 8, 2018-
 * 7:59:50 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CareTakerInfoDaoImpl implements CareTakerInfoDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CareTakerInfoDaoImpl.getCareTakerInfo}")
	private String getCareTakerInfoSql;

	@Value("${CareTakerInfoDaoImpl.careTakrResource}")
	private String careTakrResourceSql;

	@Value("${CareTakerInfoDaoImpl.deletecaretakerInfo}")
	private String deletecaretakerInfo;

	private static final Logger log = Logger.getLogger(CareTakerInfoDaoImpl.class);

	/**
	 * Method name: getCareTakerInfo Method description : Used to get the info
	 * on Caretaker Information page. Converted CRES55D to this method.
	 * 
	 * @param careTakerInfoReq
	 * @return CaretakerInformationRes @
	 * 
	 */
	@Override
	public List<CaretkrInfoCaretakerDto> getCareTakerInfo(Long idResource) {
		log.debug("Entering method getCareTakerInfo in CareTakerInfoDaoImpl");
		@SuppressWarnings("unchecked")
		List<CaretkrInfoCaretakerDto> caretkrInfoCaretakerDtoList = sessionFactory.getCurrentSession()
				.createSQLQuery(getCareTakerInfoSql).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("idCaretaker", StandardBasicTypes.LONG).addScalar("nbrCaretkr", StandardBasicTypes.STRING)
				.addScalar("nmCaretkrFname", StandardBasicTypes.STRING)
				.addScalar("nmCaretkrLname", StandardBasicTypes.STRING)
				.addScalar("nmCaretkrMname", StandardBasicTypes.STRING)
				.addScalar("cdCaretkrEthnic", StandardBasicTypes.STRING)
				.addScalar("cdCaretkrRace", StandardBasicTypes.STRING)
				.addScalar("cdCaretkrSex", StandardBasicTypes.STRING)
				.addScalar("dtCaretkrBirth", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaritalStatus", StandardBasicTypes.STRING).setParameter("idResource", idResource)
				.setResultTransformer(Transformers.aliasToBean(CaretkrInfoCaretakerDto.class)).list();

		log.debug("Exiting method getCareTakerInfo in CareTakerInfoDaoImpl");
		return caretkrInfoCaretakerDtoList;
	}

	/**
	 * Method name: getCareTakerInfoFromResource Method description: Used to get
	 * the info on Caretaker Information page. Converted CRES57D to this method.
	 * 
	 * @param careTakerInfoReq
	 * @return CaretakerInformationRes @
	 * 
	 */
	@Override
	public CaretkrInfoResourceDto getCareTakerInfoFromResource(Long idResource) {
		log.debug("Entering method getCareTakerInfoFromResource in CareTakerInfoDaoImpl");
		@SuppressWarnings("unchecked")
		CaretkrInfoResourceDto caretkrInfoResourceDto = null;
		List<CaretkrInfoResourceDto> caretkrResourceDtoList = sessionFactory.getCurrentSession()
				.createSQLQuery(careTakrResourceSql).addScalar("cpaIdResourceRsrc", StandardBasicTypes.LONG)
				.addScalar("hmIdResourceRsrc", StandardBasicTypes.LONG)
				.addScalar("cpaNameRsrc", StandardBasicTypes.STRING)
				.addScalar("nmResourceRsrc", StandardBasicTypes.STRING)
				.addScalar("cdHomeMaritalStatusRsrc", StandardBasicTypes.STRING).setParameter("idResource", idResource)
				.setResultTransformer(Transformers.aliasToBean(CaretkrInfoResourceDto.class)).list();
		if(!ObjectUtils.isEmpty(caretkrResourceDtoList)){
			caretkrInfoResourceDto = caretkrResourceDtoList.get(0);
		}
		log.debug("Exiting method getCareTakerInfoFromResource in CareTakerInfoDaoImpl");
		return caretkrInfoResourceDto;
	}

	/**
	 * Method name: deleteCareTaker Method-Description : Delete a row in the
	 * Caretakers table on CareTakerInformation Page. CRES56D Delete implemented
	 * using this.
	 * 
	 * @param idCareTaker
	 *            .
	 * @ @returns CaretkrInfoCaretakerDto --DeleteSuccess
	 */
	@Override
	public CaretkrInfoCaretakerDto deleteCareTaker(Long idCaretaker) {
		CaretkrInfoCaretakerDto caretkrInfoCaretakerDto = new CaretkrInfoCaretakerDto();
		if (null != idCaretaker) {
			int result = sessionFactory.getCurrentSession().createSQLQuery(deletecaretakerInfo)
					.setParameter("idCaretaker", idCaretaker).executeUpdate();
			if (result > 0) {
				caretkrInfoCaretakerDto.setDeleteSuccess("successful");
			} else {
				caretkrInfoCaretakerDto.setDeleteSuccess("Un-successful");
			}
		}
		return caretkrInfoCaretakerDto;
	}
}
