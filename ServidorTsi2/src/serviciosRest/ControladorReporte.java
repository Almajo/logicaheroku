package serviciosRest;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import daos.ClienteDao;
import daos.ReporteDao;

@Path("/reporte")
@Stateless
@LocalBean
public class ControladorReporte {
	
	@EJB
    private ReporteDao reporteDao;

	
	
}
