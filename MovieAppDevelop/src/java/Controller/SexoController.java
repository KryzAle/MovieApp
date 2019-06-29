/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Conectar;

import Model.Sexo;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class SexoController {

    Conectar con = new Conectar();
    JdbcTemplate jdbcTemplate = new JdbcTemplate(con.conectar());
    ModelAndView mav = new ModelAndView();
    int id;

    @RequestMapping("/sexo/home.htm")
    public ModelAndView home() {
        String sql = "select * from sexo";
        List datos = this.jdbcTemplate.queryForList(sql);
        mav.addObject("datos", datos);
        mav.setViewName("Sexo/home");
        return mav;
    }

    @RequestMapping(value = "/sexo/add.htm", method = RequestMethod.GET)
    public ModelAndView add() {
        mav.addObject(new Sexo());
        mav.setViewName("Sexo/add");
        return mav;
    }

    @RequestMapping(value = "/sexo/add.htm", method = RequestMethod.POST)
    public ModelAndView add(Sexo sexo) {
        String sql = "insert into sexo(SEX_NOMBRE) values(?)";
        this.jdbcTemplate.update(sql,sexo.getNombre());
        return new ModelAndView("redirect:/Sexo/home.htm");
    }

    @RequestMapping(value = "/sexo/edit.htm", method = RequestMethod.GET)
    public ModelAndView edit(HttpServletRequest request) {
        id = Integer.parseInt(request.getParameter("id"));
        List sexo = Buscar();
        mav.addObject("sexo", sexo);
        mav.setViewName("Sexo/edit");
        return mav;
    }

    @RequestMapping(value = "/sexo/edit.htm", method = RequestMethod.POST)
    public ModelAndView edit(Sexo sexo) {
        String sql = "update sexo set SEX_NOMBRE=? WHERE SEX_ID=?";
        this.jdbcTemplate.update(sql, sexo.getNombre(), id);
        return new ModelAndView("redirect:/Sexo/Home.htm");
    }

    @RequestMapping(value = "/sexo/delete.htm", method = RequestMethod.GET)
    public ModelAndView Eliminar(HttpServletRequest request) {
        id = Integer.parseInt(request.getParameter("id"));
        List sexo = Buscar();
        mav.addObject("sexo", sexo);
        mav.setViewName("Sexo/delete");
        return mav;
    }

    @RequestMapping(value = "/sexo/eliminar.htm", method = RequestMethod.POST)
    public ModelAndView Eliminar(Sexo sexo) {
        String sql = "Delete from sexo WHERE SEX_ID=?";
        this.jdbcTemplate.update(sql, id);
        return new ModelAndView("redirect:/sexo/index.htm");
    }

    public List Buscar() {
        Sexo sexo = new Sexo();
        String sql = "select * from sexo where SEX_ID=" + id;
        List datos = this.jdbcTemplate.queryForList(sql);
        return datos;
    }
}
