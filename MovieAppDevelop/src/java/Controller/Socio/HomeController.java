
package Controller.Socio;

import Model.Conectar;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
public class HomeController 
{
    private JdbcTemplate jdbcTemplate;
    
    public HomeController()
    {
        Conectar con=new Conectar();
        this.jdbcTemplate=new JdbcTemplate(con.conectar());
    }
    
    @RequestMapping("socio/home.htm")
    public ModelAndView home()
    {
        ModelAndView mav=new ModelAndView();
        String sql="select * from socio";
        List datos=this.jdbcTemplate.queryForList(sql);
        System.out.println(datos.get(1));
       
        mav.addObject("datos",datos);
        mav.setViewName("Socio/home");
        return mav;
    }
    /*
    @RequestMapping(value = "/movimientoru/teoria.htm", method = RequestMethod.GET)
    public String teoriaMRU(Model m) {
        return "/movimientoru/teoriamru";
    }*/
}
