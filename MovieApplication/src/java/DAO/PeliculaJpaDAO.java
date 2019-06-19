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
import Beans.Director;
import Beans.Formato;
import Beans.Genero;
import Beans.ActorPelicula;
import java.util.ArrayList;
import java.util.Collection;
import Beans.Alquiler;
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
public class PeliculaJpaDAO implements Serializable {

    public PeliculaJpaDAO(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pelicula pelicula) throws RollbackFailureException, Exception {
        if (pelicula.getActorPeliculaCollection() == null) {
            pelicula.setActorPeliculaCollection(new ArrayList<ActorPelicula>());
        }
        if (pelicula.getAlquilerCollection() == null) {
            pelicula.setAlquilerCollection(new ArrayList<Alquiler>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Director dirId = pelicula.getDirId();
            if (dirId != null) {
                dirId = em.getReference(dirId.getClass(), dirId.getDirId());
                pelicula.setDirId(dirId);
            }
            Formato forId = pelicula.getForId();
            if (forId != null) {
                forId = em.getReference(forId.getClass(), forId.getForId());
                pelicula.setForId(forId);
            }
            Genero genId = pelicula.getGenId();
            if (genId != null) {
                genId = em.getReference(genId.getClass(), genId.getGenId());
                pelicula.setGenId(genId);
            }
            Collection<ActorPelicula> attachedActorPeliculaCollection = new ArrayList<ActorPelicula>();
            for (ActorPelicula actorPeliculaCollectionActorPeliculaToAttach : pelicula.getActorPeliculaCollection()) {
                actorPeliculaCollectionActorPeliculaToAttach = em.getReference(actorPeliculaCollectionActorPeliculaToAttach.getClass(), actorPeliculaCollectionActorPeliculaToAttach.getAplId());
                attachedActorPeliculaCollection.add(actorPeliculaCollectionActorPeliculaToAttach);
            }
            pelicula.setActorPeliculaCollection(attachedActorPeliculaCollection);
            Collection<Alquiler> attachedAlquilerCollection = new ArrayList<Alquiler>();
            for (Alquiler alquilerCollectionAlquilerToAttach : pelicula.getAlquilerCollection()) {
                alquilerCollectionAlquilerToAttach = em.getReference(alquilerCollectionAlquilerToAttach.getClass(), alquilerCollectionAlquilerToAttach.getAlqId());
                attachedAlquilerCollection.add(alquilerCollectionAlquilerToAttach);
            }
            pelicula.setAlquilerCollection(attachedAlquilerCollection);
            em.persist(pelicula);
            if (dirId != null) {
                dirId.getPeliculaCollection().add(pelicula);
                dirId = em.merge(dirId);
            }
            if (forId != null) {
                forId.getPeliculaCollection().add(pelicula);
                forId = em.merge(forId);
            }
            if (genId != null) {
                genId.getPeliculaCollection().add(pelicula);
                genId = em.merge(genId);
            }
            for (ActorPelicula actorPeliculaCollectionActorPelicula : pelicula.getActorPeliculaCollection()) {
                Pelicula oldPelIdOfActorPeliculaCollectionActorPelicula = actorPeliculaCollectionActorPelicula.getPelId();
                actorPeliculaCollectionActorPelicula.setPelId(pelicula);
                actorPeliculaCollectionActorPelicula = em.merge(actorPeliculaCollectionActorPelicula);
                if (oldPelIdOfActorPeliculaCollectionActorPelicula != null) {
                    oldPelIdOfActorPeliculaCollectionActorPelicula.getActorPeliculaCollection().remove(actorPeliculaCollectionActorPelicula);
                    oldPelIdOfActorPeliculaCollectionActorPelicula = em.merge(oldPelIdOfActorPeliculaCollectionActorPelicula);
                }
            }
            for (Alquiler alquilerCollectionAlquiler : pelicula.getAlquilerCollection()) {
                Pelicula oldPelIdOfAlquilerCollectionAlquiler = alquilerCollectionAlquiler.getPelId();
                alquilerCollectionAlquiler.setPelId(pelicula);
                alquilerCollectionAlquiler = em.merge(alquilerCollectionAlquiler);
                if (oldPelIdOfAlquilerCollectionAlquiler != null) {
                    oldPelIdOfAlquilerCollectionAlquiler.getAlquilerCollection().remove(alquilerCollectionAlquiler);
                    oldPelIdOfAlquilerCollectionAlquiler = em.merge(oldPelIdOfAlquilerCollectionAlquiler);
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

    public void edit(Pelicula pelicula) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pelicula persistentPelicula = em.find(Pelicula.class, pelicula.getPelId());
            Director dirIdOld = persistentPelicula.getDirId();
            Director dirIdNew = pelicula.getDirId();
            Formato forIdOld = persistentPelicula.getForId();
            Formato forIdNew = pelicula.getForId();
            Genero genIdOld = persistentPelicula.getGenId();
            Genero genIdNew = pelicula.getGenId();
            Collection<ActorPelicula> actorPeliculaCollectionOld = persistentPelicula.getActorPeliculaCollection();
            Collection<ActorPelicula> actorPeliculaCollectionNew = pelicula.getActorPeliculaCollection();
            Collection<Alquiler> alquilerCollectionOld = persistentPelicula.getAlquilerCollection();
            Collection<Alquiler> alquilerCollectionNew = pelicula.getAlquilerCollection();
            if (dirIdNew != null) {
                dirIdNew = em.getReference(dirIdNew.getClass(), dirIdNew.getDirId());
                pelicula.setDirId(dirIdNew);
            }
            if (forIdNew != null) {
                forIdNew = em.getReference(forIdNew.getClass(), forIdNew.getForId());
                pelicula.setForId(forIdNew);
            }
            if (genIdNew != null) {
                genIdNew = em.getReference(genIdNew.getClass(), genIdNew.getGenId());
                pelicula.setGenId(genIdNew);
            }
            Collection<ActorPelicula> attachedActorPeliculaCollectionNew = new ArrayList<ActorPelicula>();
            for (ActorPelicula actorPeliculaCollectionNewActorPeliculaToAttach : actorPeliculaCollectionNew) {
                actorPeliculaCollectionNewActorPeliculaToAttach = em.getReference(actorPeliculaCollectionNewActorPeliculaToAttach.getClass(), actorPeliculaCollectionNewActorPeliculaToAttach.getAplId());
                attachedActorPeliculaCollectionNew.add(actorPeliculaCollectionNewActorPeliculaToAttach);
            }
            actorPeliculaCollectionNew = attachedActorPeliculaCollectionNew;
            pelicula.setActorPeliculaCollection(actorPeliculaCollectionNew);
            Collection<Alquiler> attachedAlquilerCollectionNew = new ArrayList<Alquiler>();
            for (Alquiler alquilerCollectionNewAlquilerToAttach : alquilerCollectionNew) {
                alquilerCollectionNewAlquilerToAttach = em.getReference(alquilerCollectionNewAlquilerToAttach.getClass(), alquilerCollectionNewAlquilerToAttach.getAlqId());
                attachedAlquilerCollectionNew.add(alquilerCollectionNewAlquilerToAttach);
            }
            alquilerCollectionNew = attachedAlquilerCollectionNew;
            pelicula.setAlquilerCollection(alquilerCollectionNew);
            pelicula = em.merge(pelicula);
            if (dirIdOld != null && !dirIdOld.equals(dirIdNew)) {
                dirIdOld.getPeliculaCollection().remove(pelicula);
                dirIdOld = em.merge(dirIdOld);
            }
            if (dirIdNew != null && !dirIdNew.equals(dirIdOld)) {
                dirIdNew.getPeliculaCollection().add(pelicula);
                dirIdNew = em.merge(dirIdNew);
            }
            if (forIdOld != null && !forIdOld.equals(forIdNew)) {
                forIdOld.getPeliculaCollection().remove(pelicula);
                forIdOld = em.merge(forIdOld);
            }
            if (forIdNew != null && !forIdNew.equals(forIdOld)) {
                forIdNew.getPeliculaCollection().add(pelicula);
                forIdNew = em.merge(forIdNew);
            }
            if (genIdOld != null && !genIdOld.equals(genIdNew)) {
                genIdOld.getPeliculaCollection().remove(pelicula);
                genIdOld = em.merge(genIdOld);
            }
            if (genIdNew != null && !genIdNew.equals(genIdOld)) {
                genIdNew.getPeliculaCollection().add(pelicula);
                genIdNew = em.merge(genIdNew);
            }
            for (ActorPelicula actorPeliculaCollectionOldActorPelicula : actorPeliculaCollectionOld) {
                if (!actorPeliculaCollectionNew.contains(actorPeliculaCollectionOldActorPelicula)) {
                    actorPeliculaCollectionOldActorPelicula.setPelId(null);
                    actorPeliculaCollectionOldActorPelicula = em.merge(actorPeliculaCollectionOldActorPelicula);
                }
            }
            for (ActorPelicula actorPeliculaCollectionNewActorPelicula : actorPeliculaCollectionNew) {
                if (!actorPeliculaCollectionOld.contains(actorPeliculaCollectionNewActorPelicula)) {
                    Pelicula oldPelIdOfActorPeliculaCollectionNewActorPelicula = actorPeliculaCollectionNewActorPelicula.getPelId();
                    actorPeliculaCollectionNewActorPelicula.setPelId(pelicula);
                    actorPeliculaCollectionNewActorPelicula = em.merge(actorPeliculaCollectionNewActorPelicula);
                    if (oldPelIdOfActorPeliculaCollectionNewActorPelicula != null && !oldPelIdOfActorPeliculaCollectionNewActorPelicula.equals(pelicula)) {
                        oldPelIdOfActorPeliculaCollectionNewActorPelicula.getActorPeliculaCollection().remove(actorPeliculaCollectionNewActorPelicula);
                        oldPelIdOfActorPeliculaCollectionNewActorPelicula = em.merge(oldPelIdOfActorPeliculaCollectionNewActorPelicula);
                    }
                }
            }
            for (Alquiler alquilerCollectionOldAlquiler : alquilerCollectionOld) {
                if (!alquilerCollectionNew.contains(alquilerCollectionOldAlquiler)) {
                    alquilerCollectionOldAlquiler.setPelId(null);
                    alquilerCollectionOldAlquiler = em.merge(alquilerCollectionOldAlquiler);
                }
            }
            for (Alquiler alquilerCollectionNewAlquiler : alquilerCollectionNew) {
                if (!alquilerCollectionOld.contains(alquilerCollectionNewAlquiler)) {
                    Pelicula oldPelIdOfAlquilerCollectionNewAlquiler = alquilerCollectionNewAlquiler.getPelId();
                    alquilerCollectionNewAlquiler.setPelId(pelicula);
                    alquilerCollectionNewAlquiler = em.merge(alquilerCollectionNewAlquiler);
                    if (oldPelIdOfAlquilerCollectionNewAlquiler != null && !oldPelIdOfAlquilerCollectionNewAlquiler.equals(pelicula)) {
                        oldPelIdOfAlquilerCollectionNewAlquiler.getAlquilerCollection().remove(alquilerCollectionNewAlquiler);
                        oldPelIdOfAlquilerCollectionNewAlquiler = em.merge(oldPelIdOfAlquilerCollectionNewAlquiler);
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
                Integer id = pelicula.getPelId();
                if (findPelicula(id) == null) {
                    throw new NonexistentEntityException("The pelicula with id " + id + " no longer exists.");
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
            Pelicula pelicula;
            try {
                pelicula = em.getReference(Pelicula.class, id);
                pelicula.getPelId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pelicula with id " + id + " no longer exists.", enfe);
            }
            Director dirId = pelicula.getDirId();
            if (dirId != null) {
                dirId.getPeliculaCollection().remove(pelicula);
                dirId = em.merge(dirId);
            }
            Formato forId = pelicula.getForId();
            if (forId != null) {
                forId.getPeliculaCollection().remove(pelicula);
                forId = em.merge(forId);
            }
            Genero genId = pelicula.getGenId();
            if (genId != null) {
                genId.getPeliculaCollection().remove(pelicula);
                genId = em.merge(genId);
            }
            Collection<ActorPelicula> actorPeliculaCollection = pelicula.getActorPeliculaCollection();
            for (ActorPelicula actorPeliculaCollectionActorPelicula : actorPeliculaCollection) {
                actorPeliculaCollectionActorPelicula.setPelId(null);
                actorPeliculaCollectionActorPelicula = em.merge(actorPeliculaCollectionActorPelicula);
            }
            Collection<Alquiler> alquilerCollection = pelicula.getAlquilerCollection();
            for (Alquiler alquilerCollectionAlquiler : alquilerCollection) {
                alquilerCollectionAlquiler.setPelId(null);
                alquilerCollectionAlquiler = em.merge(alquilerCollectionAlquiler);
            }
            em.remove(pelicula);
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

    public List<Pelicula> findPeliculaEntities() {
        return findPeliculaEntities(true, -1, -1);
    }

    public List<Pelicula> findPeliculaEntities(int maxResults, int firstResult) {
        return findPeliculaEntities(false, maxResults, firstResult);
    }

    private List<Pelicula> findPeliculaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pelicula.class));
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

    public Pelicula findPelicula(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pelicula.class, id);
        } finally {
            em.close();
        }
    }

    public int getPeliculaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pelicula> rt = cq.from(Pelicula.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
