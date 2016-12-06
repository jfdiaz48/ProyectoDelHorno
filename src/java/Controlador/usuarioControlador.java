/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Entidad.Empleado;
import Entidad.Tablahorno;
import Modelo.EmpleadoFacade;
import Modelo.TablahornoFacade;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author asus
 */
@Named(value = "usuarioControlador")
@SessionScoped
public class usuarioControlador implements Serializable {

    @Inject
    private EmpleadoFacade empleadoFacade;

    @Inject
    private TablahornoFacade tablaHornoFacade;

    private List<Empleado> empleados = new ArrayList<>();

    private Empleado empleado;

    private Tablahorno tablaHorno;

    private Empleado usuarioLog;

    private Empleado empleadoEdit = new Empleado();

    private int estadoEdicion;

    private int estadoRegistro;

    private int estadoRegistro2;

    public usuarioControlador() {

    }

    public void registrarEmpleado() {

        //Se crean las variables globales del método
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map params = externalContext.getRequestParameterMap();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        Empleado empleado = new Empleado();
        try {

            // Se reciben los parametros de los input del empleado a editar
            
            int idEmpleado = Integer.parseInt((String) params.get("idempleado"));
            String nombre = (String) params.get("nombre");
            String apellido = (String) params.get("apellido");
            String cargo = (String) params.get("cargo");
            String clave = (String) params.get("clave");
            String correo = (String) params.get("correo");

            //Se crea un objeto con los parametros recibidos de los input
            empleado.setIdEmpleado(idEmpleado);
            empleado.setNombre(nombre);
            empleado.setApellido(apellido);
            empleado.setCargo(cargo);
            empleado.setClave(clave);
            empleado.setCorreo(correo);

            // Se crea el empleado
            empleadoFacade.create(empleado);

            //Los siguientes "if" se crearon para el estado de las alarmas de 
            //"Registro exitoso" y "Falló el registro"
            // Y también sirve para validar que cuando se actualice a página 
            
             if (estadoRegistro == 1) {
                estadoRegistro = 0;
                estadoRegistro2 = 1;
            } else {
                estadoRegistro2 = 0;
            }
            if (estadoRegistro2 == 0) {
                estadoRegistro = 1;
                estadoRegistro2 = 1;
            }

        } catch (Exception e) {

            e.printStackTrace();
            if (estadoRegistro == 1) {
                estadoRegistro = 0;
                estadoRegistro2 = 1;
            } else {
                estadoRegistro2 = 0;
            }
            if (estadoRegistro2 == 0) {
                estadoRegistro = 2;
                estadoRegistro2 = 1;
            }

        }
    }

    public void validacionEstadoRegistro() throws IOException {

        if (estadoRegistro2 == 1) {
            estadoRegistro = 0;
        }

    }

    public void editarEmpleado() {
        
        //Se crean las variables globales del método

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map params = externalContext.getRequestParameterMap();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        Empleado empleado = new Empleado();
        try {
            // Se reciben los parametros de los input del empleado a editar
            
            String documento = (String) params.get("idempleado");
            int idEmpleado = Integer.parseInt(documento);
            String nombre = (String) params.get("nombre");
            String apellido = (String) params.get("apellido");
            String cargo = (String) params.get("cargo");
            String clave = (String) params.get("clave");
            String correo = (String) params.get("correo");
            
            //Se crea un objeto con los parametros recibidos de los input

            empleado.setIdEmpleado(idEmpleado);
            empleado.setNombre(nombre);
            empleado.setApellido(apellido);
            empleado.setCargo(cargo);
            empleado.setClave(clave);
            empleado.setCorreo(correo);

            //Los siguientes "if" se crearon para el estado de las alarmas de 
            //"Edición exitosa", "Falló la edición" y "No se ha generado ningun cambio"
            
            if (empleado.getNombre().equals(empleadoEdit.getNombre()) && empleado.getApellido().equals(empleadoEdit.getApellido()) && empleado.getCargo().equals(empleadoEdit.getCargo()) && empleado.getClave().equals(empleadoEdit.getClave()) && empleado.getCorreo().equals(empleadoEdit.getCorreo())) {
                estadoEdicion = 3;
            } else {
                empleadoEdit = empleado;
                empleadoFacade.edit(empleadoEdit);
                estadoEdicion = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            estadoEdicion = 2;
        }
    }

    public void login() throws IOException {

        //Se crean las variables globales de sesión
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map params = externalContext.getRequestParameterMap();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();

        try {

            //Variables locales
            boolean autenticacion = false;

            //se reciben los parametros de los input de la vista
            int documento = Integer.parseInt((String) params.get("documento"));
            String clave = (String) params.get("clave");

            //Se lena el ArrayList con todos los empleados que estan registrados en la base de datos
            empleados = empleadoFacade.findAll();

            // For para comparar el documento y la clave del usuario que quiere loguearse
            for (int i = 0; i < empleados.size(); i++) {
                if (empleados.get(i).getIdEmpleado() == documento && empleados.get(i).getClave().equals(clave)) {

                    // Usuario encontrado que se pone en un atributo de sesión llamado "UsuarioLog"
                    httpServletRequest.getSession().setAttribute("UsuarioLog", empleados.get(i));

                    // Se redirecciona al usuario al modulo de inicio
                    facesContext.getExternalContext().redirect("/ProyectoHorno/modUsuario/principalUsuario.xhtml");
                    autenticacion = true;

                    // Se pone al usuario logueado en una variable tipo usuario hasta que se cierre sesión
                    usuarioLog = empleados.get(i);

                }
            }

            if (autenticacion == false) {

                // Si la autenticación falló, el usuario será redireccionado de nuevo al login
                facesContext.getExternalContext().redirect("/ProyectoHorno/");

            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public void cerrarSesion() {
        
        // Variables globales del método

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();

        try {
            
            // Se invalida la sesión del usuario

            facesContext.getExternalContext().redirect("/ProyectoHorno/");
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public void validarSesion() {

        //Variables globales del método 
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();

        try {
            
            // Se valida que el atributo de sesión del empleaod que se logueo 
            
            if (httpServletRequest.getSession().getAttribute("UsuarioLog") != null) {

            } else {

                facesContext.getExternalContext().redirect("/ProyectoHorno/");

            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public List<Empleado> listaEmpleados() {
        empleados = empleadoFacade.findAll();
        return empleados;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Empleado getUsuarioLog() {
        return usuarioLog;
    }

    public void setUsuarioLog(Empleado usuarioLog) {
        this.usuarioLog = usuarioLog;
    }

    public Empleado getEmpleadoEdit() {
        if (estadoEdicion != 0) {
            estadoEdicion = 0;
        }
        return empleadoEdit;
    }

    public void setEmpleadoEdit(Empleado empleadoEdit) {
        this.empleadoEdit = empleadoEdit;
    }

    public int getEstadoEdicion() {
        return estadoEdicion;
    }

    public void setEstadoEdicion(int estadoEdicion) {
        this.estadoEdicion = estadoEdicion;
    }

    public int getEstadoRegistro() {
        return estadoRegistro;
    }

    public void setEstadoRegistro(int estadoRegistro) {
        this.estadoRegistro = estadoRegistro;
    }

    public int getEstadoRegistro2() {
        return estadoRegistro2;
    }

    public void setEstadoRegistro2(int estadoRegistro2) {
        this.estadoRegistro2 = estadoRegistro2;
    }

}
