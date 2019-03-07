/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sw;

import static com.mchange.v2.log.MLog.config;
import com.sun.org.apache.xerces.internal.impl.dv.xs.DecimalDV;
import datos.CrudService;
import herramientas.Hasher;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import modelo.AuthGroup;
import modelo.AuthUser;
import modelo.AuthUserGroups;
import modelo.Cliente;
import modelo.Cuenta;
import modelo.Transaccion;



/**
 *
 * @author camila
 */
@Path("cuenta")
public class CuentaRest {
    
  @POST
    @Consumes({"application/json", "application/json"})
    @Produces({"application/json", "application/json"})
    public Response crearCuenta(Cuenta cuenta){
        try {
            new CrudService().create(cuenta);
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            jsonObjectBuilder.add("estado","creado");
            jsonObjectBuilder.add("cuenta","" + cuenta);
            JsonObject jsonObject = jsonObjectBuilder.build();
            return Response.ok(jsonObject.toString()).build();
            
        } catch (Exception e) {
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            jsonObjectBuilder.add("estado","no creado");
            jsonObjectBuilder.add("error",cuenta+"");
            JsonObject jsonObject = jsonObjectBuilder.build();
            return Response.ok(jsonObject.toString()).build(); 
        }
        
    }
    
     
    @GET
    @Path("/buscarCedula")
    @Produces({"application/json", "application/json"})
    public Response buscarCedula(@QueryParam("cedula") String cedula){
        List<Cliente> cliente = new CrudService().findWithQuery("SELECT e FROM Cliente e WHERE e.cedula = '"+cedula+"'");
        return Response.ok(cliente.get(0)).build();
    }
    
    @Path("/modificarCliente")
    @POST
    @Consumes({"application/json", "application/json"})
    @Produces({"application/json", "application/json"})
    public Response modificarCliente(Cliente cliente){
        List<Cliente> est = new CrudService().findWithQuery("SELECT e FROM Cliente e WHERE e.cedula = '"+cliente.getCedula()+"'");
        
        Cliente cl = est.get(0);
        cl.setApellidos(cliente.getApellidos());
        cl.setNombres(cliente.getNombres());
        cl.setEstadoCivil(cliente.getEstadoCivil());
        cl.setCorreo(cliente.getCorreo());
        cl.setTelefono(cliente.getTelefono());
        cl.setCelular(cliente.getCelular());
        cl.setDireccion(cliente.getDireccion());
       
        
        CrudService crudService = new CrudService();
        crudService.update(cl);
        
        return Response.ok().build();
    }
    
    
    @Path("/guardarCliente")
    @POST
    @Consumes({"application/json", "application/json"})
    @Produces({"application/json", "application/json"})
    public Response guardarCliente(JsonArray jsonArray){
        Date date = new Date();
        Cliente cl = new Cliente();
       
        cl.setCedula(jsonArray.getJsonObject(0).getString("cedula"));
        cl.setApellidos(jsonArray.getJsonObject(0).getString("apellidos"));
        cl.setNombres(jsonArray.getJsonObject(0).getString("nombres"));
        cl.setEstadoCivil(jsonArray.getJsonObject(0).getString("estadoCivil"));
        cl.setCorreo(jsonArray.getJsonObject(0).getString("correo"));
        cl.setTelefono(jsonArray.getJsonObject(0).getString("telefono"));
        cl.setCelular(jsonArray.getJsonObject(0).getString("celular"));
        cl.setGenero(jsonArray.getJsonObject(0).getString("genero"));
        cl.setFechaNacimiento(date);
        cl.setDireccion(jsonArray.getJsonObject(0).getString("direccion"));
        cl.setEstado(true);
        
        Cuenta ct=new Cuenta();
        ct.setEstado(true);
        ct.setFechaApertura(date);
        ct.setNumero(cl.getCedula());
        ct.setSaldo(BigDecimal.valueOf(Double.parseDouble(jsonArray.getJsonObject(1).getString("saldo"))));
        ct.setTipoCuenta(jsonArray.getJsonObject(1).getString("tipoCuenta"));
        ct.setClienteId(cl);
        
         new CrudService().create(ct);
        
        return Response.ok(ct).build();
    }
    
    @Path("/dos")
    @POST
    @Consumes({"application/json", "application/json"})
    @Produces({"application/json", "application/json"})
    public JsonObject dos(JsonArray jsonArray){
        return jsonArray.getJsonObject(2);
    }
    
    
    
    @Path("/lista")
    @GET
    @Produces({"application/json", "application/json"})
    public List<Cliente> listalClientes() {
        List<Cliente> lista= new CrudService().findWithQuery("SELECT c FROM Cliente c WHERE c.estado=true");
        return lista;
    }
   
    @Path("/cambiarEstadoCuentas")
    @GET
    @Consumes({"application/json", "application/json"})
    @Produces({"application/json", "application/json"})
    public Response cambiarEstado(@QueryParam("cedula") String cedula, @QueryParam("estado") boolean estado) {
        CrudService est = new CrudService();
        List<Cliente> esp = new CrudService().findWithQuery("SELECT e FROM Cliente e WHERE e.cedula = '" + cedula + "'");
        List<Cuenta> esl = new CrudService().findWithQuery("SELECT e FROM Cuenta e WHERE e.cuentaId = '" + esp.get(0).getClienteId() + "'");
        
        Cuenta ct=esl.get(0);
        Cliente cl = esp.get(0);
        if (estado) {
            cl.setEstado(true);
            ct.setEstado(true);
            
//            est.update(cl);
//            est.update(ct);
             new CrudService().update(ct);
            new CrudService().update(cl);
           
            
            return Response.ok(cl).build();
        } else {
            
            cl.setEstado(false);
            ct.setEstado(false);
            
//            est.update(cl);
//            est.update(ct);
            
            new CrudService().update(ct);
            new CrudService().update(cl);
            
            
            return Response.ok(cl).build();
        }
    }
    
    @GET
    @Path("/buscarCuenta")
    @Produces({"application/json", "application/json"})
    public Response buscarCuenta(@QueryParam("numero") String numero) {
        List<Cuenta> cuenta = new CrudService().findWithQuery("SELECT e FROM Cuenta e WHERE e.numero = '" + numero + "'");
        if (!cuenta.isEmpty()) {
            return Response.ok(cuenta.get(0)).build();
        } else {
            return Response.noContent().build();
        }
    }
    
     @GET
    @Path("/buscarTransferencias")
    @Produces({"application/json", "application/json"})
    public Response buscarTransferencias(@QueryParam("cedula") String cedula){
         List<Cliente> cliente = new CrudService().findWithQuery("SELECT e FROM Cliente e WHERE cedula = '"+cedula+"'");
         List<Cuenta> cuenta = new CrudService().findWithQuery("SELECT e FROM Cuenta e WHERE cliente_id = '"+cliente.get(0).getClienteId()+"'");
         List<Transaccion> transaccion = new CrudService().findWithQuery("SELECT e.fecha, e.tipo, e.valor, e.descripcion, e.responsable  FROM Transaccion e WHERE cuenta_id = '"+cuenta.get(0).getCuentaId()+"'");
        return Response.ok(transaccion.get(0)).build();
    }
    
    @Path("/deposito")
    @POST
    @Consumes({"application/json", "application/json"})
    @Produces({"application/json", "application/json"})
    public Response deposito(JsonObject jsonArray){
        List<Cuenta> listCuenta = new CrudService().findWithQuery("SELECT c FROM Cuenta c WHERE c.numero = '"+jsonArray.getString("numero")+"'");
        if(!listCuenta.isEmpty()){
            Date date = new Date();
            Cuenta cuenta = listCuenta.get(0);
            BigDecimal valor = BigDecimal.valueOf(Double.parseDouble(jsonArray.getString("valor")));
            BigDecimal saldo = cuenta.getSaldo();
            cuenta.setSaldo(saldo.add(valor));
            new CrudService().update(cuenta);
            Transaccion transaccion = new Transaccion();
            transaccion.setFecha(date);
            transaccion.setTipo("deposito");
            transaccion.setValor(valor);
            transaccion.setDescripcion(jsonArray.getString("descripcion"));
            transaccion.setResponsable(jsonArray.getString("responsable"));
            transaccion.setCuentaId(cuenta);
            return Response.ok(new CrudService().create(transaccion)).build();
        }else {
            return Response.noContent().build();
        }
    }
    
    
    @Path("/retiro")
    @POST
    @Consumes({"application/json", "application/json"})
    @Produces({"application/json", "application/json"})
    public Response retiro(JsonObject jsonArray){
        List<Cuenta> listCuenta = new CrudService().findWithQuery("SELECT c FROM Cuenta c WHERE c.numero = '"+jsonArray.getString("numero")+"'");
        if(!listCuenta.isEmpty()){
            Date date = new Date();
            Cuenta cuenta = listCuenta.get(0);
            BigDecimal saldo = cuenta.getSaldo();
            if(saldo.intValue() > 0){
                BigDecimal valor = BigDecimal.valueOf(Double.parseDouble(jsonArray.getString("valor")));
                if(saldo.intValue() >= valor.intValue()){
                    cuenta.setSaldo(saldo.subtract(valor));
                    new CrudService().update(cuenta);
                    Transaccion transaccion = new Transaccion();
                    transaccion.setFecha(date);
                    transaccion.setTipo("retiro");
                    transaccion.setValor(valor);
                    transaccion.setDescripcion(jsonArray.getString("descripcion"));
                    transaccion.setResponsable(jsonArray.getString("responsable"));
                    transaccion.setCuentaId(cuenta);
                    return Response.ok(new CrudService().create(transaccion)).build();
                }
            }
            return Response.noContent().build();
        }else {
            return Response.noContent().build();
        }
    }
    
    @Path("/transferencia")
    @POST
    @Consumes({"application/json", "application/json"})
    @Produces({"application/json", "application/json"})
    public Response transferencia(JsonObject jsonArray) {
        List<Cuenta> listacuentaR = new CrudService().findWithQuery("SELECT e FROM Cuenta e WHERE e.numero='" + jsonArray.getString("numeroR") + "'");
        List<Cuenta> listacuentaD = new CrudService().findWithQuery("SELECT e FROM Cuenta e WHERE e.numero='" + jsonArray.getString("numeroD") + "'");
        if (!listacuentaR.isEmpty() && !listacuentaD.isEmpty()) {
            Date date = new Date();
            Cuenta cuentaR = listacuentaR.get(0);
            Cuenta cuentaD = listacuentaD.get(0);
            BigDecimal saldo = cuentaR.getSaldo();
            BigDecimal saldoD = cuentaD.getSaldo();
            if (cuentaR.getNumero() != cuentaD.getNumero()) {
                if (saldo.intValue() > 0) {
                    BigDecimal valor = BigDecimal.valueOf(Double.parseDouble(jsonArray.getString("valor")));
                    if (saldo.intValue() >= valor.intValue()) {
                        cuentaR.setSaldo(saldo.subtract(valor));
                        new CrudService().update(cuentaR);
                        cuentaD.setSaldo(saldoD.add(valor));
                        new CrudService().update(cuentaD);
                        Transaccion transaccion = new Transaccion();
                        transaccion.setFecha(date);
                        transaccion.setTipo("Transferencia");
                        transaccion.setValor(valor);
                        transaccion.setDescripcion(jsonArray.getString("descripcion"));
                        transaccion.setResponsable(jsonArray.getString("responsable"));
                        transaccion.setCuentaId(cuentaR);
                        return Response.ok(new CrudService().create(transaccion)).build();
                    }
                }
                return Response.noContent().build();
            }
            return Response.noContent().build();
        } else {
            return Response.noContent().build();
        }
    }
    
    @POST
    @Path("/buscarUsuariosData")
    @Consumes({"application/json", "application/json"})
    @Produces({"application/json", "application/json"})
    public Response buscarUsuarios(JsonObject jsonArray) {
        List<AuthUser> listaUsuario= new CrudService().findWithQuery("SELECT e FROM AuthUser e WHERE e.username='" + jsonArray.getString("nombre") + "'");
        
        //tabla auth_user conectada con auth_user_groups donde se encuentran los id que se conecta con auth_group
        if (listaUsuario.isEmpty()) {
            
            return Response.noContent().build();
        } else {
            List<AuthUserGroups> listaGrupo= new CrudService().findWithQuery("SELECT e FROM AuthUserGroups e WHERE e.userId=" + listaUsuario.get(0).getId() );
//            List<AuthGroup> listaGrupoNombre= new CrudService().findWithQuery("SELECT e FROM AuthGroup e WHERE e.id='" + listaGrupo.get(0).getId() + "'");
//            Map<String, ?> config = null;
//           
//             JsonBuilderFactory factory = Json.createBuilderFactory(config);
//            JsonObject value = (JsonObject) factory.createObjectBuilder().add("usuario", listaUsuario.get(0).getFirstName()).build();
            Hasher validar= new Hasher();
            
            boolean validarClave= validar.passwordShouldMatch(jsonArray.getString("clave"),listaUsuario.get(0).getPassword());
            
            if(validarClave){
                
                
                
                return Response.ok(listaGrupo.get(0)).build();
                
            }else{
                return Response.noContent().build();
            }
            
            
        }
    }
    
    
    
 
    
}
