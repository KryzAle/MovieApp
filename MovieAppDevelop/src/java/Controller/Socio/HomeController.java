
package Controller.Socio;

import Model.Conectar;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController{

    Conectar con = new Conectar();
    JdbcTemplate jdbcTemplate = new JdbcTemplate(con.conectar());
    ModelAndView mav = new ModelAndView();

    @RequestMapping("/socio/home.htm")
    public ModelAndView home() {
        String sql = "select * from socio";
        List datos = this.jdbcTemplate.queryForList(sql);
        mav.addObject("datos", datos);
        mav.setViewName("Socio/home");
        return mav;
    }
    
    
}