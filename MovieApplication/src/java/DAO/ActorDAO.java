/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Beans.Actor;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author KryzAle
 */
@Service
public class ActorDAO {
    @PersistenceContext
    private EntityManager em;
    @Transactional(rollbackFor ={ServicioException.class})
    public void create(Actor dto){
        em.persist(dto);
    }
    public List<Actor> getListActor() throws ServicioException{ 
        String sql ="Select a from Actor a";
        Query q = em.createQuery(sql);
        return q.getResultList();
      
    }
    public void update(Actor dto) throws ServicioException {
        em.merge(dto);
        
    }
    
    public void delete(Actor dto) throws ServicioException {
        em.remove(dto);
    }
    
    public Actor findActor(Actor dto) throws ServicioException {
       return em.find(Actor.class, dto);
    }
} 
