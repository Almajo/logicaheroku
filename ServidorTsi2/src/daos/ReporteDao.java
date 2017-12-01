package daos;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Entidades.Cliente;
import Entidades.Contenido;

@Stateless
@LocalBean
public class ReporteDao {

    @PersistenceContext
    private EntityManager em;

    

}
