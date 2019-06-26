package Controller.Socio;

import Model.Conectar;
import Model.Socio;
import Model.SocioValidar;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("socio/edit.htm")
public class EditController {

    SocioValidar usuariosValidar;
    private JdbcTemplate jdbcTemplate;

    public EditController() {
        this.usuariosValidar = new SocioValidar();
        Conectar con = new Conectar();
        this.jdbcTemplate = new JdbcTemplate(con.conectar());
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView form(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        int id = Integer.parseInt(request.getParameter("id"));
        Socio datos = this.selectUsuario(id);
        mav.setViewName("Socio/edit");
        mav.addObject("usuarios", new Socio(id, datos.getNombre(), datos.getCorreo(), datos.getTelefono(), datos.getCedula(), datos.getDireccion()));
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView form(
            @ModelAttribute("usuarios") Socio u,
            BindingResult result,
            SessionStatus status,
            HttpServletRequest request
    ) {
        this.usuariosValidar.validate(u, result);
        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            int id = Integer.parseInt(request.getParameter("id"));
            Socio datos = this.selectUsuario(id);
            mav.setViewName("edit");
            mav.addObject("usuarios", new Socio(id, datos.getNombre(), datos.getCorreo(), datos.getTelefono(), datos.getCedula(), datos.getDireccion()));
            return mav;
        } else {
            int id = Integer.parseInt(request.getParameter("id"));
            this.jdbcTemplate.update(
                    "update socio "
                    + "set soc_cedula=?,"
                    + "soc_nombre=?,"
                    + "soc_direccion=?,"
                    + "soc_telefono=?,"
                    + "soc_correo=?"
                    + "where "
                    + "soc_id=? ",
                    u.getCedula(), u.getNombre(),u.getDireccion(), u.getTelefono(),u.getCorreo(), id);
            return new ModelAndView("redirect:/socio/home.htm");
        }

    }

    public Socio selectUsuario(int id) {
        final Socio user = new Socio();
        String quer = "SELECT * FROM socio WHERE soc_id='" + id + "'";
        return (Socio) jdbcTemplate.query(quer, new ResultSetExtractor<Socio>() {
            public Socio extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    user.setNombre(rs.getString("soc_nombre"));
                    user.setCorreo(rs.getString("soc_correo"));
                    user.setTelefono(rs.getString("soc_telefono"));
                    user.setCedula(rs.getString("soc_cedula"));
                    user.setDireccion(rs.getString("soc_direccion"));
                }
                return user;
            }

        }
        );
    }
}
