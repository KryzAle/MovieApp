/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.ActorDAO;
import DAO.ServicioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author KryzAle
 */
@Controller
public class ActorController {
    @Autowired
    private ActorDAO dao;
    
    
    
    @RequestMapping(value = "/actor/index.htm", method = RequestMethod.GET)
    public String calcularpg(Model m) throws ServicioException {
        m.addAttribute("lista",dao.getListActor());
        return "/actor/index";
    }
    
    

//    @RequestMapping(value = "/primerGrado/calcularpg.htm", method = RequestMethod.POST)
//    public String calcularpg(@RequestParam("txtValX") float valX,
//            @RequestParam("txtValInd") float valInd,
//            Model m) {
//        primerGrado cl=new primerGrado();
//        cl.setValInd(valInd);
//        cl.setValX(valX);
//        cl.calcularPrimerGrado();
//        cl.evaluarPrimerGrado();
//        m.addAttribute("calculoPrimerGrado", cl);
//        return "/primerGrado/viewResultadoPrimerGrado";
//    }
}
