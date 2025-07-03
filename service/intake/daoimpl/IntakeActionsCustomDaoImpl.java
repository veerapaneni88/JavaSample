package us.tx.state.dfps.service.intake.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.response.RtrvAllegRes;
import us.tx.state.dfps.service.intake.dao.IntakeActionsCustomDao;
import us.tx.state.dfps.service.intake.dto.RowCintAllegationDto;

@Repository
public class IntakeActionsCustomDaoImpl implements IntakeActionsCustomDao {

	@Value("${IntakeActions.getAllegations}")
	private transient String getAllegationssql;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	public IntakeActionsCustomDaoImpl() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public RtrvAllegRes getAllegations(Long idStage) {
		RtrvAllegRes allegRtrvRecOut = new RtrvAllegRes();
		List<RowCintAllegationDto> listCintAllegation = new ArrayList<RowCintAllegationDto>();

		listCintAllegation = (List<RowCintAllegationDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getAllegationssql).setParameter("hi_ulIdStage", idStage))
						.addScalar("idAllegation", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idVictim", StandardBasicTypes.LONG)
						.addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG).addScalar("scrAllegPerp")
						.addScalar("cdAllegPerpSuffix").addScalar("scrPersVictim").addScalar("cdPersVictimSuffix")
						.addScalar("tsLastUpdate").addScalar("cdIntakeAllegType").addScalar("intakeAllegDuration")
						.setResultTransformer(Transformers.aliasToBean(RowCintAllegationDto.class)).list();
		allegRtrvRecOut.setRowCintAllegation(listCintAllegation);

		return allegRtrvRecOut;

	}

}
