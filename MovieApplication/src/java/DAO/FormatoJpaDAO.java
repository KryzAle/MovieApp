/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Beans.Formato;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Beans.Pelicula;
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
public class FormatoJpaDAO implements Serializable {

    public FormatoJpaDAO(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Formato formato) throws RollbackFailureException, Exception {
        if (formato.getPeliculaCollection() == null) {
            formato.setPeliculaCollection(new ArrayList<Pelicula>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Pelicula> attachedPeliculaCollection = new ArrayList<Pelicula>();
            for (Pelicula peliculaCollectionPeliculaToAttach : formato.getPeliculaCollection()) {
                peliculaCollectionPeliculaToAttach = em.getReference(peliculaCollectionPeliculaToAttach.getClass(), peliculaCollectionPeliculaToAttach.getPelId());
                attachedPeliculaCollection.add(peliculaCollectionPeliculaToAttach);
            }
            formato.setPeliculaCollection(attachedPeliculaCollection);
            em.persist(formato);
            for (Pelicula peliculaCollectionPelicula : formato.getPeliculaCollection()) {
                Formato oldForIdOfPeliculaCollectionPelicula = peliculaCollectionPelicula.getForId();
                peliculaCollectionPelicula.setForId(formato);
                peliculaCollectionPelicula = em.merge(peliculaCollectionPelicula);
                if (oldForIdOfPeliculaCollectionPelicula != null) {
                    oldForIdOfPeliculaCollectionPelicula.getPeliculaCollection().remove(peliculaCollectionPelicula);
                    oldForIdOfPeliculaCollectionPelicula = em.merge(oldForIdOfPeliculaCollectionPelicula);
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

    public void edit(Formato formato) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Formato persistentFormato = em.find(Formato.class, formato.getForId());
            Collection<Pelicula> peliculaCollectionOld = persistentFormato.getPeliculaCollection();
            Collection<Pelicula> peliculaCollectionNew = formato.getPeliculaCollection();
            Collection<Pelicula> attachedPeliculaCollectionNew = new ArrayList<Pelicula>();
            for (Pelicula peliculaCollectionNewPeliculaToAttach : peliculaCollectionNew) {
                peliculaCollectionNewPeliculaToAttach = em.getReference(peliculaCollectionNewPeliculaToAttach.getClass(), peliculaCollectionNewPeliculaToAttach.getPelId());
                attachedPeliculaCollectionNew.add(peliculaCollectionNewPeliculaToAttach);
            }
            peliculaCollectionNew = attachedPeliculaCollectionNew;
            formato.setPeliculaCollection(peliculaCollectionNew);
            formato = em.merge(formato);
            for (Pelicula peliculaCollectionOldPelicula : peliculaCollectionOld) {
                if (!peliculaCollectionNew.contains(peliculaCollectionOldPelicula)) {
                    peliculaCollectionOldPelicula.setForId(null);
                    peliculaCollectionOldPelicula = em.merge(peliculaCollectionOldPelicula);
                }
            }
            for (Pelicula peliculaCollectionNewPelicula : peliculaCollectionNew) {
                if (!peliculaCollectionOld.contains(peliculaCollectionNewPelicula)) {
                    Formato oldForIdOfPeliculaCollectionNewPelicula = peliculaCollectionNewPelicula.getForId();
                    peliculaCollectionNewPelicula.setForId(formato);
                    peliculaCollectionNewPelicula = em.merge(peliculaCollectionNewPelicula);
                    if (oldForIdOfPeliculaCollectionNewPelicula != null && !oldForIdOfPeliculaCollectionNewPelicula.equals(formato)) {
                        oldForIdOfPeliculaCollectionNewPelicula.getPeliculaCollection().remove(peliculaCollectionNewPelicula);
                        oldForIdOfPeliculaCollectionNewPelicula = em.merge(oldForIdOfPeliculaCollectionNewPelicula);
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
                Integer id = formato.getForId();
                if (findFormato(id) == null) {
                    throw new NonexistentEntityException("The formato with id " + id + " no longer exists.");
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
            Formato formato;
            try {
                formato = em.getReference(Formato.class, id);
                formato.getForId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The formato with id " + id + " no longer exists.", enfe);
            }
            Collection<Pelicula> peliculaCollection = formato.getPeliculaCollection();
            for (Pelicula peliculaCollectionPelicula : peliculaCollection) {
                peliculaCollectionPelicula.setForId(null);
                peliculaCollectionPelicula = em.merge(peliculaCollectionPelicula);
            }
            em.remove(formato);
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

    public List<Formato> findFormatoEntities() {
        return findFormatoEntities(true, -1, -1);
    }

    public List<Formato> findFormatoEntities(int maxResults, int firstResult) {
        return findFormatoEntities(false, maxResults, firstResult);
    }

    private List<Formato> findFormatoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Formato.class));
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

    public Formato findFormato(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Formato.class, id);
        } finally {
            em.close();
        }
    }

    public int getFormatoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Formato> rt = cq.from(Formato.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
