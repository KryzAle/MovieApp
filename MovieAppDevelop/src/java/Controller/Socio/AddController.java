package Controller.Socio;

import Model.Conectar;
import Model.Socio;
import Model.SocioValidar;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("socio/add.htm")
public class AddController {

    SocioValidar usuariosValidar;
    private JdbcTemplate jdbcTemplate;

    public AddController() {
        this.usuariosValidar = new SocioValidar();
        Conectar con = new Conectar();
        this.jdbcTemplate = new JdbcTemplate(con.conectar());
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView form() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("Socio/add");
        mav.addObject("usuarios", new Socio());
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView form(
            @ModelAttribute("usuarios") Socio u,
            BindingResult result,
            SessionStatus status
    ) {
        this.usuariosValidar.validate(u, result);
        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("Socio/add");
            mav.addObject("usuarios", new Socio());
            return mav;
        } else {
            this.jdbcTemplate.update(
                    "insert into socio (SOC_CEDULA,SOC_NOMBRE,SOC_DIRECCION,SOC_TELEFONO,SOC_CORREO) values (?,?,?,?,?)",
                    u.getCedula(),u.getNombre(),u.getDireccion(), u.getTelefono(),u.getCorreo()
            );
            return new ModelAndView("redirect:/socio/home.htm");
        }

    }
}
