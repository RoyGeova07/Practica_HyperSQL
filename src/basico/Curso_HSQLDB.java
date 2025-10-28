/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package basico;

import java.sql.*;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


/**
 *
 * @author royum
 */
public class Curso_HSQLDB 
{
    
    private static final String URL="jdbc:hsqldb:file:C:/data/CursoBD/CursoBD";
    private static final String USER="SA";  
    private static final String PASS="";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        //                                                                                                  fin de linea
        try(Connection con=DriverManager.getConnection(URL, USER, PASS);Scanner lea=new Scanner(System.in).useDelimiter("\\R+"))
        {
            int opciones=0,id;
            String nombre,puesto="",like;
            double salario,NuevoSalario;
            
            //para evitar errores xd
            AsegurarEsquema(con);
            
            while(opciones!=7)
            {
                
                System.out.println("==MENU HYPERSQL NIVEL BASICO==\n");
                System.out.println("1. Listar Empleados");
                System.out.println("2. Insertar Empleados");
                System.out.println("3. Actualizar salario empleado por ID");
                System.out.println("4. Borrar empleados por ID");
                System.out.println("5. Buscar empleado por puesto (LIKE)");
                System.out.println("6. Reaplicar schema.sql (DROP/CREATE/DATOS)");
                System.out.println("7. Salir");
                System.out.println("Elige una opcion: ");
                opciones=lea.nextInt();
                lea.nextLine();//limpia del salto de linea 
                
                if(opciones==1) 
                {
                    
                    ListaEmpleados(con);
                    
                }else if(opciones==2){
                    
                    System.out.println("Nombre: ");
                    nombre=lea.nextLine().trim();
                    System.out.println("Puesto: ");
                    puesto=lea.nextLine().trim();
                    System.out.println("Salario: ");
                    salario=lea.nextDouble();
                    InsertarEmpleado(con, nombre, puesto, salario);
                    
                }else if(opciones==3){
                    
                    System.out.println("ID a actualizar: ");
                    id=lea.nextInt();
                    if(!ExisteId(con, id))
                    {
                        
                        System.out.println("El Id: "+id+" no existe bro");
                        continue;
                        
                    }
                    System.out.println("Nuevo Salario: ");
                    NuevoSalario=lea.nextDouble();
                    ActualizarSalarioPorId(con, id, NuevoSalario);
                    
                }else if(opciones==4){
                    
                    System.out.println("ID de empleado a eliminar: ");
                    id=lea.nextInt();
                    if(!ExisteId(con, id))
                    {
                        
                        System.out.println("El Id: "+id+" no existe bro");
                        continue;
                        
                    }
                    BorrarPorId(con, id);
                    
                }else if(opciones==5){
                    System.out.println("Para usar bien esta opcion -> ejemplo: Licen% o Analista%");
                    System.out.println("\nPatron Like (ej. Analista%)");
                    like=lea.nextLine().trim();
                    BuscarPorPuesto(con, like);
                    
                }else if(opciones==6){
                    System.out.println("⚠ Esto BORRARA y RESETEARA los datos de 'empleados'.");
                    System.out.println("Escribe EXACTAMENTE: RESET  (cualquier otra cosa cancela)");
                    String conf=lea.nextLine().trim();
                    
                    if(!"RESET".equalsIgnoreCase(conf))
                    {
                        
                        System.out.println("\nOPERACION CANCELADA");
                        continue;
                        
                    }
                    
                    
                    EjecutarSQLScriptDesdeElRecurso(con,"/sql/schema.sql");
                    System.out.println("ESQUEMA APLICADO!!!!");
                    
                }else if(opciones==7){
                    
                    System.out.println("\nADIOS BRO ;(");
                    break;
                    
                }else{
                    
                    System.out.println("\nNO VALIDO!!");
                    
                }
                
            }
         
                
            
        }catch(SQLException e){
            
            System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
            e.printStackTrace();
            
        }
        
    }
    
    
    
    private static void EjecutarSQLScriptDesdeElRecurso(Connection con,String RutaDeRecursos)
    {
        
        try(InputStream in=Curso_HSQLDB.class.getResourceAsStream(RutaDeRecursos))
        {
            
            if(in==null)
            {
                
                System.out.println("No se encontro "+RutaDeRecursos+" (saltando)");
                return;
                
            }
            String SQL=new String(in.readAllBytes(),StandardCharsets.UTF_8).replaceAll("(?m)^\\s*--.*$", " ");
            
            try(Statement st=con.createStatement())
            {
                
                for(String stmt:SQL.split(";"))
                {
                    
                    String q=stmt.trim();
                    if(q.isEmpty())continue;
                    boolean bandera=st.execute(q);
                    
                    if(bandera)
                    {
                        
                        try(ResultSet rs=st.getResultSet())
                        {
                            
                            while(rs.next()){}//con esto consume resultados del script
                            
                        }
                        
                    }
                    
                }
                
            }
            System.out.println("Script aplicado: "+RutaDeRecursos);
            
        }catch(Exception e){
            
            System.err.println("Error ejecutando: "+RutaDeRecursos);
            e.printStackTrace();
            
        }
        
    }
    // ----------------------OPERACIONES CRUD :O----------------------------------------------
    private static void InsertarEmpleado(Connection con,String nombre,String puesto,double salario)throws SQLException
    {
        
        String SQL="INSERT INTO empleados (nombre,puesto,salario)VALUES(?,?,?)";
        try(PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setString(1,nombre);
            ps.setString(2,puesto);
            ps.setBigDecimal(3,BigDecimal.valueOf(salario));
            System.out.println("INSERT filas: "+ps.executeUpdate());
            
        }
        
    }
    private static void ActualizarSalarioPorId(Connection con,int id,double NuevoSalario)throws SQLException
    {
        
        String SQL="UPDATE empleados SET salario=? WHERE id=?";
        try(PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setBigDecimal(1,BigDecimal.valueOf(NuevoSalario));
            ps.setInt(2,id);
            System.out.println("UPDATE filas: "+ps.executeUpdate());
            
        }
        
    }
    private static void BorrarPorId(Connection con,int id)throws SQLException
    {
        
        String SQL="DELETE FROM empleados WHERE id=?";
        try(PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setInt(1,id);
            System.out.println("DELETE filas: "+ps.executeUpdate());
            
        }
        
    }
    private static void ListaEmpleados(Connection con)throws SQLException
    {
        
        String SQL="SELECT id,nombre,puesto,salario FROM empleados ORDER BY id";
        try(PreparedStatement ps=con.prepareStatement(SQL);ResultSet rs=ps.executeQuery())
        {
            
            while(rs.next())
            {
                
                System.out.printf("%d | %s | %s | %.2f%n",rs.getInt("id"),rs.getString("nombre"),rs.getString("puesto"),rs.getBigDecimal("salario"));
                
            }
            
        }
        
    }
    private static void BuscarPorPuesto(Connection con,String likePatron)throws SQLException
    {
        
        String SQL="SELECT id,nombre,puesto,salario FROM empleados WHERE puesto LIKE ? ORDER BY id";
        try(PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setString(1,likePatron);
            try(ResultSet rs=ps.executeQuery())
            {
                
                while(rs.next())
                {
                    
                    System.out.printf("%d | %s | %s | %.2f%n",rs.getInt("id"),rs.getString("nombre"),rs.getString("puesto"),rs.getBigDecimal("salario"));
                    
                }
                
            }
            
        }
        
    }
    private static boolean ExisteId(Connection con,int id)throws SQLException
    {
        
        String SQL="SELECT 1 FROM empleados WHERE id= ?";
        try(PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setInt(1,id);
            try(ResultSet rs=ps.executeQuery())
            {
                
                return rs.next();//true si hay fila, el id existe
                
            }
            
        }
        
    }
    private static void AsegurarEsquema(Connection con)
    {
        
        try
        {
           
            if(!TablaExiste(con,"EMPLEADOS"))
            {
                
                EjecutarSQLScriptDesdeElRecurso(con,"/sql/schema.sql");  
                System.out.println("\nNo existe la tabla empleados. Aplicando schema.sql");
                
                
            }
            
        }catch(SQLException e){
            
            System.err.println("No se pudo verificar/aplicar el esquema");
            e.printStackTrace();
            
        }
        
    }
    private static boolean TablaExiste(Connection con, String tabla)throws SQLException
    {
        
        String SQL="""
                   SELECT 1 
                   FROM INFORMATION_SCHEMA.TABLES
                   WHERE TABLE_SCHEMA='PUBLIC' AND UPPER(TABLE_NAME)=?
                   """;
        try(PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setString(1,tabla.toUpperCase());
            try(ResultSet rs=ps.executeQuery())
            {
                
                return rs.next();
                
            }
            
            
        }
        
    }
    
}
