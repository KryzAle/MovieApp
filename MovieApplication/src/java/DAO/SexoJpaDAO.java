/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Beans.Actor;
import Beans.Sexo;
import Model.exceptions.NonexistentEntityException;
import Model.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author KryzAle
 */
public class SexoJpaDAO implements Serializable {

    public SexoJpaDAO(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sexo sexo) throws RollbackFailureException, Exception {
        if (sexo.getActorCollection() == null) {
            sexo.setActorCollection(new ArrayList<Actor>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Actor> attachedActorCollection = new ArrayList<Actor>();
            for (Actor actorCollectionActorToAttach : sexo.getActorCollection()) {
                actorCollectionActorToAttach = em.getReference(actorCollectionActorToAttach.getClass(), actorCollectionActorToAttach.getActId());
                attachedActorCollection.add(actorCollectionActorToAttach);
            }
            sexo.setActorCollection(attachedActorCollection);
            em.persist(sexo);
            for (Actor actorCollectionActor : sexo.getActorCollection()) {
                Sexo oldSexIdOfActorCollectionActor = actorCollectionActor.getSexId();
                actorCollectionActor.setSexId(sexo);
                actorCollectionActor = em.merge(actorCollectionActor);
                if (oldSexIdOfActorCollectionActor != null) {
                    oldSexIdOfActorCollectionActor.getActorCollection().remove(actorCollectionActor);
                    oldSexIdOfActorCollectionActor = em.merge(oldSexIdOfActorCollectionActor);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Sexo sexo) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Sexo persistentSexo = em.find(Sexo.class, sexo.getSexId());
            Collection<Actor> actorCollectionOld = persistentSexo.getActorCollection();
            Collection<Actor> actorCollectionNew = sexo.getActorCollection();
            Collection<Actor> attachedActorCollectionNew = new ArrayList<Actor>();
            for (Actor actorCollectionNewActorToAttach : actorCollectionNew) {
                actorCollectionNewActorToAttach = em.getReference(actorCollectionNewActorToAttach.getClass(), actorCollectionNewActorToAttach.getActId());
                attachedActorCollectionNew.add(actorCollectionNewActorToAttach);
            }
            actorCollectionNew = attachedActorCollectionNew;
            sexo.setActorCollection(actorCollectionNew);
            sexo = em.merge(sexo);
            for (Actor actorCollectionOldActor : actorCollectionOld) {
                if (!actorCollectionNew.contains(actorCollectionOldActor)) {
                    actorCollectionOldActor.setSexId(null);
                    actorCollectionOldActor = em.merge(actorCollectionOldActor);
                }
            }
            for (Actor actorCollectionNewActor : actorCollectionNew) {
                if (!actorCollectionOld.contains(actorCollectionNewActor)) {
                    Sexo oldSexIdOfActorCollectionNewActor = actorCollectionNewActor.getSexId();
                    actorCollectionNewActor.setSexId(sexo);
                    actorCollectionNewActor = em.merge(actorCollectionNewActor);
                    if (oldSexIdOfActorCollectionNewActor != null && !oldSexIdOfActorCollectionNewActor.equals(sexo)) {
                        oldSexIdOfActorCollectionNewActor.getActorCollection().remove(actorCollectionNewActor);
                        oldSexIdOfActorCollectionNewActor = em.merge(oldSexIdOfActorCollectionNewActor);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sexo.getSexId();
                if (findSexo(id) == null) {
                    throw new NonexistentEntityException("The sexo with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Sexo sexo;
            try {
                sexo = em.getReference(Sexo.class, id);
                sexo.getSexId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sexo with id " + id + " no longer exists.", enfe);
            }
            Collection<Actor> actorCollection = sexo.getActorCollection();
            for (Actor actorCollectionActor : actorCollection) {
                actorCollectionActor.setSexId(null);
                actorCollectionActor = em.merge(actorCollectionActor);
            }
            em.remove(sexo);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Sexo> findSexoEntities() {
        return findSexoEntities(true, -1, -1);
    }

    public List<Sexo> findSexoEntities(int maxResults, int firstResult) {
        return findSexoEntities(false, maxResults, firstResult);
    }

    private List<Sexo> findSexoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sexo.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Sexo findSexo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sexo.class, id);
        } finally {
            em.close();
        }
    }

    public int getSexoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sexo> rt = cq.from(Sexo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
