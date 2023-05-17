package ru.mpei.fqw.repository;

import org.springframework.stereotype.Repository;
import ru.mpei.fqw.model.FaultCurrentModel;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class RepositoryIml {
    @PersistenceContext
    private EntityManager em;

    public void save(Object object) {
        em.persist(object);
    }
    public List<FaultCurrentModel> getFaultCurrentInfo(){
        return em.createQuery("SELECT e FROM FaultCurrentModel e" , FaultCurrentModel.class).setMaxResults(300).getResultList();
    }

}

