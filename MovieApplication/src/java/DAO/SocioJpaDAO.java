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
import Beans.Alquiler;
import Beans.Socio;
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
public class SocioJpaDAO implements Serializable {

    public SocioJpaDAO(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Socio socio) throws RollbackFailureException, Exception {
        if (socio.getAlquilerCollection() == null) {
            socio.setAlquilerCollection(new ArrayList<Alquiler>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Alquiler> attachedAlquilerCollection = new ArrayList<Alquiler>();
            for (Alquiler alquilerCollectionAlquilerToAttach : socio.getAlquilerCollection()) {
                alquilerCollectionAlquilerToAttach = em.getReference(alquilerCollectionAlquilerToAttach.getClass(), alquilerCollectionAlquilerToAttach.getAlqId());
                attachedAlquilerCollection.add(alquilerCollectionAlquilerToAttach);
            }
            socio.setAlquilerCollection(attachedAlquilerCollection);
            em.persist(socio);
            for (Alquiler alquilerCollectionAlquiler : socio.getAlquilerCollection()) {
                Socio oldSocIdOfAlquilerCollectionAlquiler = alquilerCollectionAlquiler.getSocId();
                alquilerCollectionAlquiler.setSocId(socio);
                alquilerCollectionAlquiler = em.merge(alquilerCollectionAlquiler);
                if (oldSocIdOfAlquilerCollectionAlquiler != null) {
                    oldSocIdOfAlquilerCollectionAlquiler.getAlquilerCollection().remove(alquilerCollectionAlquiler);
                    oldSocIdOfAlquilerCollectionAlquiler = em.merge(oldSocIdOfAlquilerCollectionAlquiler);
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

    public void edit(Socio socio) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Socio persistentSocio = em.find(Socio.class, socio.getSocId());
            Collection<Alquiler> alquilerCollectionOld = persistentSocio.getAlquilerCollection();
            Collection<Alquiler> alquilerCollectionNew = socio.getAlquilerCollection();
            Collection<Alquiler> attachedAlquilerCollectionNew = new ArrayList<Alquiler>();
            for (Alquiler alquilerCollectionNewAlquilerToAttach : alquilerCollectionNew) {
                alquilerCollectionNewAlquilerToAttach = em.getReference(alquilerCollectionNewAlquilerToAttach.getClass(), alquilerCollectionNewAlquilerToAttach.getAlqId());
                attachedAlquilerCollectionNew.add(alquilerCollectionNewAlquilerToAttach);
            }
            alquilerCollectionNew = attachedAlquilerCollectionNew;
            socio.setAlquilerCollection(alquilerCollectionNew);
            socio = em.merge(socio);
            for (Alquiler alquilerCollectionOldAlquiler : alquilerCollectionOld) {
                if (!alquilerCollectionNew.contains(alquilerCollectionOldAlquiler)) {
                    alquilerCollectionOldAlquiler.setSocId(null);
                    alquilerCollectionOldAlquiler = em.merge(alquilerCollectionOldAlquiler);
                }
            }
            for (Alquiler alquilerCollectionNewAlquiler : alquilerCollectionNew) {
                if (!alquilerCollectionOld.contains(alquilerCollectionNewAlquiler)) {
                    Socio oldSocIdOfAlquilerCollectionNewAlquiler = alquilerCollectionNewAlquiler.getSocId();
                    alquilerCollectionNewAlquiler.setSocId(socio);
                    alquilerCollectionNewAlquiler = em.merge(alquilerCollectionNewAlquiler);
                    if (oldSocIdOfAlquilerCollectionNewAlquiler != null && !oldSocIdOfAlquilerCollectionNewAlquiler.equals(socio)) {
                        oldSocIdOfAlquilerCollectionNewAlquiler.getAlquilerCollection().remove(alquilerCollectionNewAlquiler);
                        oldSocIdOfAlquilerCollectionNewAlquiler = em.merge(oldSocIdOfAlquilerCollectionNewAlquiler);
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
                Integer id = socio.getSocId();
                if (findSocio(id) == null) {
                    throw new NonexistentEntityException("The socio with id " + id + " no longer exists.");
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
            Socio socio;
            try {
                socio = em.getReference(Socio.class, id);
                socio.getSocId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The socio with id " + id + " no longer exists.", enfe);
            }
            Collection<Alquiler> alquilerCollection = socio.getAlquilerCollection();
            for (Alquiler alquilerCollectionAlquiler : alquilerCollection) {
                alquilerCollectionAlquiler.setSocId(null);
                alquilerCollectionAlquiler = em.merge(alquilerCollectionAlquiler);
            }
            em.remove(socio);
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

    public List<Socio> findSocioEntities() {
        return findSocioEntities(true, -1, -1);
    }

    public List<Socio> findSocioEntities(int maxResults, int firstResult) {
        return findSocioEntities(false, maxResults, firstResult);
    }

    private List<Socio> findSocioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Socio.class));
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

    public Socio findSocio(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Socio.class, id);
        } finally {
            em.close();
        }
    }

    public int getSocioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Socio> rt = cq.from(Socio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
