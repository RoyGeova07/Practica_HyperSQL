/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package demohsqldb;

/**
 *
 * @author royum
 */
/*

el .jar es el driver real que permite que Java se conecte con la base de datos. 
Sin el, la conexion no va a funcionar.

*/

import java.sql.*;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public class DemoHSQLDB 
{

    /**
     * @param args the command line arguments
     */
    
    //aqui se ajusta la url
    private static final String URL="jdbc:hsqldb:file:C:/data/MiBD/MiBD";
    private static final String USER="SA";
    private static final String PASS="";
    
    public static void main(String[] args) 
    {
                
       try(Connection con=DriverManager.getConnection(URL, USER, PASS))
       {
           
           //aplicar al script del proyecto (DROP/CREATE/INSERT/UPDATE/)
           EjecutarSql(con,"/demohsqldb/sql/schema.sql");
           
           //CRUD con PreparedStatement (seguro y limpio) 
           InsertarEmpleado(con,"Jerry Bengston","QA",1350.00);
           InsertarEmpleado(con,"Marcelo Garrido","Analista de IA",1100.00);
           listEmpleados(con,"==LISTA INICIAL==");
           
           ActualizarPorNombre(con,"Andres Iniesta",2100.00);
           BorrarPorNombre(con,"Camaron Quemado");
           listEmpleados(con,"===LISTA POST UPDATE,DELETE===");
           
           //JOIN empleados + departamentos 
           System.out.println("=== JOIN (LEFT) empleados + departamentos ===");
           ListConDepartamento(con);
           
           //Cierre limpio para modo file
           try(Statement st=con.createStatement()){st.execute("SHUTDOWN");}
           System.out.println("OK: BD cerrada con SHUTDOWN");
           
       }catch(SQLException e){
           
           System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
           e.printStackTrace();
           
       }
        
    }
    
    //con esta funcion ejecuto al archivo .sql q esta empaquetado con el proyecto , este. Omite lineas que empiezan con '--' y separa por ';'
    private static void EjecutarSql(Connection con,String rutaDeRecurso)
    {
        
        try(InputStream in=DemoHSQLDB.class.getResourceAsStream(rutaDeRecurso))
        {
            
            if(in==null)
            {
                
                System.out.println("No se encontro: "+rutaDeRecurso+ "(saltando)");
                return;
                
            }
            String SQL=new String(in.readAllBytes(),StandardCharsets.UTF_8).replaceAll("(?m)^\\s*--.*$", "");//quita comentarios de linea
            try(Statement st=con.createStatement())
            {
                
                for(String stmt:SQL.split(";"))
                {
                
                    String q=stmt.trim();
                    if(q.isEmpty())continue;
                    
                    boolean hasResultSet=st.execute(q);
                    if(hasResultSet)
                    {
                        
                        try(ResultSet rs=st.getResultSet())
                        {
                            
                            //no imprimimos nada; basta con consumir y cerrar
                            while(rs.next()){}
                            
                        }
                        
                    }
                    
                
                }
                
            }
            
            System.out.println("Script Aplicado "+rutaDeRecurso);
            
        }catch(Exception e){
            
            System.err.println("Error ejecutando: "+rutaDeRecurso);
            e.printStackTrace();
            
        }
        
    }
    //Los '?' son marcadores de posicion en PreparedStatement.Se reemplazan con ps.setX(indice, valor)
    //y evitan problemas de comillas e inyeccion SQL.
    private static void InsertarEmpleado(Connection con,String nombre,String puesto,double salario)throws SQLException
    {
        
        String SQL="INSERT INTO empleados (nombre, puesto, salario) VALUES (?, ?, ?)";
        try(PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setString(1,nombre);
            ps.setString(2,puesto);
            ps.setBigDecimal(3,BigDecimal.valueOf(salario));
            System.out.println("INSERT filas: "+ps.executeUpdate());
            
        }
        
    }
    private static void ActualizarPorNombre(Connection con,String nombre,double nuevoSalario)throws SQLException
    {
        
        String SQL="UPDATE empleados SET salario=? WHERE nombre=?";
        try(PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setBigDecimal(1, BigDecimal.valueOf(nuevoSalario));
            ps.setString(2,nombre);
            System.out.println("UPDATE filas: "+ps.executeUpdate());
            
        }
        
    }
    private static void BorrarPorNombre(Connection con,String nombre)throws SQLException
    {
        
        String SQL="DELETE FROM empleados WHERE nombre=?";
        try(PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setString(1,nombre);
            System.out.println("DELETE filas: "+ps.executeUpdate());
            
        }
        
    }
    private static void listEmpleados(Connection con,String titulo)throws SQLException
    {
        
        System.out.println(titulo);
        String SQL="SELECT id,nombre,puesto,salario FROM empleados ORDER by id";
        try(PreparedStatement ps=con.prepareStatement(SQL);ResultSet rs=ps.executeQuery())
        {
            
            while(rs.next())
            {
                
                System.out.printf("%d| %s | %s | %.2f%n",rs.getInt("id"),rs.getString("nombre"),rs.getString("puesto"),rs.getBigDecimal("salario"));
                
            }
            
        }
        
    }
    private static void ListConDepartamento(Connection con)throws SQLException
    {
        
        //LEFT JOIN para ver empleados aunque depto_id sea NULL
        String SQL="""
            SELECT e.id, e.nombre, e.puesto, e.salario, d.nombre AS departamento
            FROM empleados e
            LEFT JOIN departamentos d ON d.depto_id = e.depto_id
            ORDER BY e.id
        """;
        try(PreparedStatement ps=con.prepareStatement(SQL);ResultSet rs=ps.executeQuery())
        {
            
            while(rs.next())
            {
                
                System.out.printf("%d | %s | %s | %.2f | %s%n",rs.getInt("id"),rs.getString("nombre"),rs.getString("puesto"),rs.getBigDecimal("salario"),rs.getString("departamento"));
                
            }
            
        }
        
    }
    
}
