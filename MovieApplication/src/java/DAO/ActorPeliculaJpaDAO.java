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
import Beans.ActorPelicula;
import Beans.Pelicula;
import Model.exceptions.NonexistentEntityException;
import Model.exceptions.RollbackFailureException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author KryzAle
 */
public class ActorPeliculaJpaDAO implements Serializable {

    public ActorPeliculaJpaDAO(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ActorPelicula actorPelicula) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Actor actId = actorPelicula.getActId();
            if (actId != null) {
                actId = em.getReference(actId.getClass(), actId.getActId());
                actorPelicula.setActId(actId);
            }
            Pelicula pelId = actorPelicula.getPelId();
            if (pelId != null) {
                pelId = em.getReference(pelId.getClass(), pelId.getPelId());
                actorPelicula.setPelId(pelId);
            }
            em.persist(actorPelicula);
            if (actId != null) {
                actId.getActorPeliculaCollection().add(actorPelicula);
                actId = em.merge(actId);
            }
            if (pelId != null) {
                pelId.getActorPeliculaCollection().add(actorPelicula);
                pelId = em.merge(pelId);
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

    public void edit(ActorPelicula actorPelicula) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ActorPelicula persistentActorPelicula = em.find(ActorPelicula.class, actorPelicula.getAplId());
            Actor actIdOld = persistentActorPelicula.getActId();
            Actor actIdNew = actorPelicula.getActId();
            Pelicula pelIdOld = persistentActorPelicula.getPelId();
            Pelicula pelIdNew = actorPelicula.getPelId();
            if (actIdNew != null) {
                actIdNew = em.getReference(actIdNew.getClass(), actIdNew.getActId());
                actorPelicula.setActId(actIdNew);
            }
            if (pelIdNew != null) {
                pelIdNew = em.getReference(pelIdNew.getClass(), pelIdNew.getPelId());
                actorPelicula.setPelId(pelIdNew);
            }
            actorPelicula = em.merge(actorPelicula);
            if (actIdOld != null && !actIdOld.equals(actIdNew)) {
                actIdOld.getActorPeliculaCollection().remove(actorPelicula);
                actIdOld = em.merge(actIdOld);
            }
            if (actIdNew != null && !actIdNew.equals(actIdOld)) {
                actIdNew.getActorPeliculaCollection().add(actorPelicula);
                actIdNew = em.merge(actIdNew);
            }
            if (pelIdOld != null && !pelIdOld.equals(pelIdNew)) {
                pelIdOld.getActorPeliculaCollection().remove(actorPelicula);
                pelIdOld = em.merge(pelIdOld);
            }
            if (pelIdNew != null && !pelIdNew.equals(pelIdOld)) {
                pelIdNew.getActorPeliculaCollection().add(actorPelicula);
                pelIdNew = em.merge(pelIdNew);
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
                Integer id = actorPelicula.getAplId();
                if (findActorPelicula(id) == null) {
                    throw new NonexistentEntityException("The actorPelicula with id " + id + " no longer exists.");
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
            ActorPelicula actorPelicula;
            try {
                actorPelicula = em.getReference(ActorPelicula.class, id);
                actorPelicula.getAplId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The actorPelicula with id " + id + " no longer exists.", enfe);
            }
            Actor actId = actorPelicula.getActId();
            if (actId != null) {
                actId.getActorPeliculaCollection().remove(actorPelicula);
                actId = em.merge(actId);
            }
            Pelicula pelId = actorPelicula.getPelId();
            if (pelId != null) {
                pelId.getActorPeliculaCollection().remove(actorPelicula);
                pelId = em.merge(pelId);
            }
            em.remove(actorPelicula);
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

    public List<ActorPelicula> findActorPeliculaEntities() {
        return findActorPeliculaEntities(true, -1, -1);
    }

    public List<ActorPelicula> findActorPeliculaEntities(int maxResults, int firstResult) {
        return findActorPeliculaEntities(false, maxResults, firstResult);
    }

    private List<ActorPelicula> findActorPeliculaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ActorPelicula.class));
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

    public ActorPelicula findActorPelicula(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ActorPelicula.class, id);
        } finally {
            em.close();
        }
    }

    public int getActorPeliculaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ActorPelicula> rt = cq.from(ActorPelicula.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
