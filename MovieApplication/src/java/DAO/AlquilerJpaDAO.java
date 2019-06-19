/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Beans.Alquiler;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Beans.Pelicula;
import Beans.Socio;
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
public class AlquilerJpaDAO implements Serializable {

    public AlquilerJpaDAO(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Alquiler alquiler) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pelicula pelId = alquiler.getPelId();
            if (pelId != null) {
                pelId = em.getReference(pelId.getClass(), pelId.getPelId());
                alquiler.setPelId(pelId);
            }
            Socio socId = alquiler.getSocId();
            if (socId != null) {
                socId = em.getReference(socId.getClass(), socId.getSocId());
                alquiler.setSocId(socId);
            }
            em.persist(alquiler);
            if (pelId != null) {
                pelId.getAlquilerCollection().add(alquiler);
                pelId = em.merge(pelId);
            }
            if (socId != null) {
                socId.getAlquilerCollection().add(alquiler);
                socId = em.merge(socId);
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

    public void edit(Alquiler alquiler) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Alquiler persistentAlquiler = em.find(Alquiler.class, alquiler.getAlqId());
            Pelicula pelIdOld = persistentAlquiler.getPelId();
            Pelicula pelIdNew = alquiler.getPelId();
            Socio socIdOld = persistentAlquiler.getSocId();
            Socio socIdNew = alquiler.getSocId();
            if (pelIdNew != null) {
                pelIdNew = em.getReference(pelIdNew.getClass(), pelIdNew.getPelId());
                alquiler.setPelId(pelIdNew);
            }
            if (socIdNew != null) {
                socIdNew = em.getReference(socIdNew.getClass(), socIdNew.getSocId());
                alquiler.setSocId(socIdNew);
            }
            alquiler = em.merge(alquiler);
            if (pelIdOld != null && !pelIdOld.equals(pelIdNew)) {
                pelIdOld.getAlquilerCollection().remove(alquiler);
                pelIdOld = em.merge(pelIdOld);
            }
            if (pelIdNew != null && !pelIdNew.equals(pelIdOld)) {
                pelIdNew.getAlquilerCollection().add(alquiler);
                pelIdNew = em.merge(pelIdNew);
            }
            if (socIdOld != null && !socIdOld.equals(socIdNew)) {
                socIdOld.getAlquilerCollection().remove(alquiler);
                socIdOld = em.merge(socIdOld);
            }
            if (socIdNew != null && !socIdNew.equals(socIdOld)) {
                socIdNew.getAlquilerCollection().add(alquiler);
                socIdNew = em.merge(socIdNew);
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
                Integer id = alquiler.getAlqId();
                if (findAlquiler(id) == null) {
                    throw new NonexistentEntityException("The alquiler with id " + id + " no longer exists.");
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
            Alquiler alquiler;
            try {
                alquiler = em.getReference(Alquiler.class, id);
                alquiler.getAlqId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The alquiler with id " + id + " no longer exists.", enfe);
            }
            Pelicula pelId = alquiler.getPelId();
            if (pelId != null) {
                pelId.getAlquilerCollection().remove(alquiler);
                pelId = em.merge(pelId);
            }
            Socio socId = alquiler.getSocId();
            if (socId != null) {
                socId.getAlquilerCollection().remove(alquiler);
                socId = em.merge(socId);
            }
            em.remove(alquiler);
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

    public List<Alquiler> findAlquilerEntities() {
        return findAlquilerEntities(true, -1, -1);
    }

    public List<Alquiler> findAlquilerEntities(int maxResults, int firstResult) {
        return findAlquilerEntities(false, maxResults, firstResult);
    }

    private List<Alquiler> findAlquilerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Alquiler.class));
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

    public Alquiler findAlquiler(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Alquiler.class, id);
        } finally {
            em.close();
        }
    }

    public int getAlquilerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Alquiler> rt = cq.from(Alquiler.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
