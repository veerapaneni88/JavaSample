package us.tx.state.dfps.service.admin.daoimpl;

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

import us.tx.state.dfps.service.admin.dao.IncomingDetailStageDao;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageInDto;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This dao was
 * written to select a row from the INCOMING_DETAIL table using the unique key,
 * IdStage Aug 10, 2017- 2:26:55 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class IncomingDetailStageDaoImpl implements IncomingDetailStageDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${IncomingDetailStageDaoImpl.getIncomingDetail}")
	private String getIncomingDetail;

	private static final Logger log = Logger.getLogger(IncomingDetailStageDaoImpl.class);

	public IncomingDetailStageDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getIncomingDetail Method Description: Fetch the incomedetail
	 * record for unique stage id. Cint07d
	 * 
	 * @param incomingDetailStageInDto
	 * @return List<Cint07doDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IncomingDetailStageOutDto> getIncomingDetail(IncomingDetailStageInDto incomingDetailStageInDto) {
		log.debug("Entering method getIncomingDetail in IncomingDetailStageDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIncomingDetail)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("addrIncomingCallerCity")
				.addScalar("cdIncomingCallerState").addScalar("cdIncomingCallerCounty").addScalar("cdIncmgSex")
				.addScalar("addrIncmgStreetLn1").addScalar("addrIncmgStreetLn2").addScalar("addrIncmgZip")
				.addScalar("nmIncmgRegardingFirst").addScalar("nmIncmgRegardingLast").addScalar("cdIncomingDisposition")
				.addScalar("cdIncomingProgramType").addScalar("dtIncomingCall", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdIncomingCallType").addScalar("nmIncomingCallerFirst").addScalar("nmIncomingCallerLast")
				.addScalar("nmIncomingCallerMiddle").addScalar("cdIncomingCallerSuffix")
				.addScalar("incomingCallerPhone").addScalar("incmgCallerExt")
				.addScalar("dtIncomingCallDisposed", StandardBasicTypes.TIMESTAMP).addScalar("cdIncmgAddrType")
				.addScalar("cdIncmgPhoneType").addScalar("cdIncmgSpecHandling").addScalar("indIncmgSensitive")
				.addScalar("indIncmgWorkerSafety").addScalar("incmgWorkerSafety").addScalar("incomgSensitive")
				.addScalar("indIncmgNoFactor").addScalar("nmJurisdiction").addScalar("cdIncmgStatus")
				.addScalar("cdIncmgAllegType").addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("addrIncWkrCity").addScalar("nbrIncWkrPhone").addScalar("nbrIncWkrExt")
				.addScalar("nmIncWkrName").addScalar("cdIncmgCallerInt").addScalar("incmgUnit")
				.addScalar("cdIncmgRegion").addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("indIncmgSuspMeth").addScalar("incomgSuspMeth").addScalar("incmgSpecHandling")
				.addScalar("incomgRelateCalls").addScalar("indOpenCaseFound").addScalar("indIncmgReEntry")
				.addScalar("cdIncmgReEntryRsn").addScalar("indIncmgWorkerId").addScalar("cdDisasterRlf")
				.addScalar("incomingSecCallerPhone").addScalar("incmgSecCallerExt").addScalar("cdIncmgSecPhoneType")
				.addScalar("reporterNotes").setParameter("hI_ulIdStage", incomingDetailStageInDto.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(IncomingDetailStageOutDto.class)));
		List<IncomingDetailStageOutDto> incomingDetailStageOutDtos = (List<IncomingDetailStageOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(incomingDetailStageOutDtos)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cint07dDaoImpl.No.income.record.detail", null, Locale.US));
		}
		log.debug("Exiting method getIncomingDetail in IncomingDetailStageDaoImpl");
		return incomingDetailStageOutDtos;
	}
}
