package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.contacts.dao.AllegFacilDao;
import us.tx.state.dfps.xmlstructs.inputstructs.AllegationFacilAllegPersonDto;
import us.tx.state.dfps.xmlstructs.outputstructs.AllegFacilPersonDto;
import us.tx.state.dfps.xmlstructs.outputstructs.AllegationStageVictimDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilAllegPersonDto;

@Repository
public class AllegFacilDaoImpl implements AllegFacilDao {

	@Value("${Clsc16dDaoImpl.getAllegationFacilAllegPerson}")
	private transient String getAllegationFacilAllegPerson;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	/**
	 * 
	 * Method Name: getAllegationFacilAllegPerson Method Description: This
	 * method fetches the date from FACIL_ALLEG
	 * 
	 * @param allegationFacilAllegPersonDto
	 * @return FacilAllegPersonDto
	 */
	@Override
	public FacilAllegPersonDto getAllegationFacilAllegPerson(
			AllegationFacilAllegPersonDto allegationFacilAllegPersonDto) {

		AllegFacilPersonDto allegFacilPersonDto = new AllegFacilPersonDto();
		FacilAllegPersonDto facilAllegPersonDto = new FacilAllegPersonDto();

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getAllegationFacilAllegPerson).addScalar("idAllegation", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idAllegationStage", StandardBasicTypes.LONG).addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG)
				.addScalar("cdAllegIncidentStage", StandardBasicTypes.STRING)
				.addScalar("txtAllegDuration", StandardBasicTypes.STRING)
				.addScalar("cdAllegType", StandardBasicTypes.STRING)
				.addScalar("cdAllegDisposition", StandardBasicTypes.STRING)
				.addScalar("cdAllegSeverity", StandardBasicTypes.STRING)
				.addScalar("indAllegCancelHist", StandardBasicTypes.STRING)
				.addScalar("indFacilAllegCancelHist", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegEventLoc", StandardBasicTypes.STRING)
				.addScalar("facilAllegInvClass", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegClssSupr", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegDispSupr", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegSrc", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegSrcSupr", StandardBasicTypes.STRING)
				.addScalar("dtFacilAllegSuprReply", StandardBasicTypes.DATE)
				.addScalar("dtFacilAllegInvstgtr", StandardBasicTypes.DATE)
				.addScalar("cdFacilAllegInjSer", StandardBasicTypes.STRING)
				.addScalar("cdFacilAllegNeglType", StandardBasicTypes.STRING)
				.addScalar("dtFacilAllegIncident", StandardBasicTypes.TIMESTAMP)
				.addScalar("indFacilAllegAbOffGr", StandardBasicTypes.STRING)
				.addScalar("indFacilAllegSupvd", StandardBasicTypes.STRING)
				.addScalar("txtFacilAllegCmnts", StandardBasicTypes.STRING)
				.addScalar("facilAllegMHMR", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("personAge", StandardBasicTypes.LONG)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
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
				.addScalar("nameFirst", StandardBasicTypes.STRING).addScalar("nameMiddle", StandardBasicTypes.STRING)
				.addScalar("nameLast", StandardBasicTypes.STRING).addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.setParameter("idAllegationStage", allegationFacilAllegPersonDto.getUlIdAllegationStage())
				.setResultTransformer(Transformers.aliasToBean(AllegationStageVictimDto.class)));

		List<AllegationStageVictimDto> allegationStageVictimDtoList = (List<AllegationStageVictimDto>) sQLQuery1.list();

		allegFacilPersonDto.setAllegationStageVictimDtoList(allegationStageVictimDtoList);
		facilAllegPersonDto.setAllegFacilPersonDto(allegFacilPersonDto);
		return facilAllegPersonDto;

	}

}
