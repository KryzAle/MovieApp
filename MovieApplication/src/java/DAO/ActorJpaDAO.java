/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Beans.Actor;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Beans.Sexo;
import Beans.ActorPelicula;
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
public class ActorJpaDAO implements Serializable {

    public ActorJpaDAO(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Actor actor) throws RollbackFailureException, Exception {
        if (actor.getActorPeliculaCollection() == null) {
            actor.setActorPeliculaCollection(new ArrayList<ActorPelicula>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Sexo sexId = actor.getSexId();
            if (sexId != null) {
                sexId = em.getReference(sexId.getClass(), sexId.getSexId());
                actor.setSexId(sexId);
            }
            Collection<ActorPelicula> attachedActorPeliculaCollection = new ArrayList<ActorPelicula>();
            for (ActorPelicula actorPeliculaCollectionActorPeliculaToAttach : actor.getActorPeliculaCollection()) {
                actorPeliculaCollectionActorPeliculaToAttach = em.getReference(actorPeliculaCollectionActorPeliculaToAttach.getClass(), actorPeliculaCollectionActorPeliculaToAttach.getAplId());
                attachedActorPeliculaCollection.add(actorPeliculaCollectionActorPeliculaToAttach);
            }
            actor.setActorPeliculaCollection(attachedActorPeliculaCollection);
            em.persist(actor);
            if (sexId != null) {
                sexId.getActorCollection().add(actor);
                sexId = em.merge(sexId);
            }
            for (ActorPelicula actorPeliculaCollectionActorPelicula : actor.getActorPeliculaCollection()) {
                Actor oldActIdOfActorPeliculaCollectionActorPelicula = actorPeliculaCollectionActorPelicula.getActId();
                actorPeliculaCollectionActorPelicula.setActId(actor);
                actorPeliculaCollectionActorPelicula = em.merge(actorPeliculaCollectionActorPelicula);
                if (oldActIdOfActorPeliculaCollectionActorPelicula != null) {
                    oldActIdOfActorPeliculaCollectionActorPelicula.getActorPeliculaCollection().remove(actorPeliculaCollectionActorPelicula);
                    oldActIdOfActorPeliculaCollectionActorPelicula = em.merge(oldActIdOfActorPeliculaCollectionActorPelicula);
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

    public void edit(Actor actor) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Actor persistentActor = em.find(Actor.class, actor.getActId());
            Sexo sexIdOld = persistentActor.getSexId();
            Sexo sexIdNew = actor.getSexId();
            Collection<ActorPelicula> actorPeliculaCollectionOld = persistentActor.getActorPeliculaCollection();
            Collection<ActorPelicula> actorPeliculaCollectionNew = actor.getActorPeliculaCollection();
            if (sexIdNew != null) {
                sexIdNew = em.getReference(sexIdNew.getClass(), sexIdNew.getSexId());
                actor.setSexId(sexIdNew);
            }
            Collection<ActorPelicula> attachedActorPeliculaCollectionNew = new ArrayList<ActorPelicula>();
            for (ActorPelicula actorPeliculaCollectionNewActorPeliculaToAttach : actorPeliculaCollectionNew) {
                actorPeliculaCollectionNewActorPeliculaToAttach = em.getReference(actorPeliculaCollectionNewActorPeliculaToAttach.getClass(), actorPeliculaCollectionNewActorPeliculaToAttach.getAplId());
                attachedActorPeliculaCollectionNew.add(actorPeliculaCollectionNewActorPeliculaToAttach);
            }
            actorPeliculaCollectionNew = attachedActorPeliculaCollectionNew;
            actor.setActorPeliculaCollection(actorPeliculaCollectionNew);
            actor = em.merge(actor);
            if (sexIdOld != null && !sexIdOld.equals(sexIdNew)) {
                sexIdOld.getActorCollection().remove(actor);
                sexIdOld = em.merge(sexIdOld);
            }
            if (sexIdNew != null && !sexIdNew.equals(sexIdOld)) {
                sexIdNew.getActorCollection().add(actor);
                sexIdNew = em.merge(sexIdNew);
            }
            for (ActorPelicula actorPeliculaCollectionOldActorPelicula : actorPeliculaCollectionOld) {
                if (!actorPeliculaCollectionNew.contains(actorPeliculaCollectionOldActorPelicula)) {
                    actorPeliculaCollectionOldActorPelicula.setActId(null);
                    actorPeliculaCollectionOldActorPelicula = em.merge(actorPeliculaCollectionOldActorPelicula);
                }
            }
            for (ActorPelicula actorPeliculaCollectionNewActorPelicula : actorPeliculaCollectionNew) {
                if (!actorPeliculaCollectionOld.contains(actorPeliculaCollectionNewActorPelicula)) {
                    Actor oldActIdOfActorPeliculaCollectionNewActorPelicula = actorPeliculaCollectionNewActorPelicula.getActId();
                    actorPeliculaCollectionNewActorPelicula.setActId(actor);
                    actorPeliculaCollectionNewActorPelicula = em.merge(actorPeliculaCollectionNewActorPelicula);
                    if (oldActIdOfActorPeliculaCollectionNewActorPelicula != null && !oldActIdOfActorPeliculaCollectionNewActorPelicula.equals(actor)) {
                        oldActIdOfActorPeliculaCollectionNewActorPelicula.getActorPeliculaCollection().remove(actorPeliculaCollectionNewActorPelicula);
                        oldActIdOfActorPeliculaCollectionNewActorPelicula = em.merge(oldActIdOfActorPeliculaCollectionNewActorPelicula);
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
                Integer id = actor.getActId();
                if (findActor(id) == null) {
                    throw new NonexistentEntityException("The actor with id " + id + " no longer exists.");
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
            Actor actor;
            try {
                actor = em.getReference(Actor.class, id);
                actor.getActId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The actor with id " + id + " no longer exists.", enfe);
            }
            Sexo sexId = actor.getSexId();
            if (sexId != null) {
                sexId.getActorCollection().remove(actor);
                sexId = em.merge(sexId);
            }
            Collection<ActorPelicula> actorPeliculaCollection = actor.getActorPeliculaCollection();
            for (ActorPelicula actorPeliculaCollectionActorPelicula : actorPeliculaCollection) {
                actorPeliculaCollectionActorPelicula.setActId(null);
                actorPeliculaCollectionActorPelicula = em.merge(actorPeliculaCollectionActorPelicula);
            }
            em.remove(actor);
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

    public List<Actor> findActorEntities() {
        return findActorEntities(true, -1, -1);
    }

    public List<Actor> findActorEntities(int maxResults, int firstResult) {
        return findActorEntities(false, maxResults, firstResult);
    }

    private List<Actor> findActorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Actor.class));
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

    public Actor findActor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Actor.class, id);
        } finally {
            em.close();
        }
    }

    public int getActorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Actor> rt = cq.from(Actor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
