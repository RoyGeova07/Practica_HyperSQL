/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 21 oct 2025
 */

--Limpieza, buena practica sql
DROP table if exists empleados;

--Crear Tabla
create table empleados
(

    id integer generated always as identity (start with 1, INCREMENT by 1)primary key,
    nombre varchar(30)not null,
    puesto varchar(35),
    salario decimal(10,2)

);

--Datos de prueba
insert into empleados(nombre,puesto,salario)values
('Roy Umaña','Analista de datos',39000),
('Bill Gates','CEO',400000),
('Marcelo Garrido','Junior',2300),
('Mondongo Perez','Especialista en Fronted',3400);

--verificacion inicial
select*from empleados order by id;  

insert into empleados(nombre,puesto,salario)values
('Spider de Cornella','Analista de datos',31000);

delete from empleados
where id=5;
