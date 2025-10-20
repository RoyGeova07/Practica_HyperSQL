/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 20 oct 2025
 */

--eliminar table si no existe la tabla empleados
drop table if exists empleados;

--esquema
create table empleados(

    id integer generated always as identity (start with 1, increment by 1 )primary key,
    nombre varchar(100)not null,
    puesto varchar(100),
    salario decimal(10,2)

);

--datos de prueba
insert into empleados(nombre,puesto,salario)values
('Roy Umaña','Analista de datos ',1200.00),
('Camaron Quemado','Analista de sotware ',1600.00),
('Mondongo Perez','Analista de IA ',1100.00);

--datos de prueba
insert into empleados(nombre,puesto,salario)values
('Junior Nuñez','Analista de datos ',1200.00),
('Sofia Castelar','Analista de sotware ',1600.00),
('Marcelo Garrido','Analista de IA ',1100.00);


--aqui se verifica
select*from empleados order by id;

--UPDATE PARA ACTUALIZAR LA TABLA
update empleados
set salario=2000.00  --sirve para cambiar el salario
where nombre='Roy Umaña';
select*from empleados order by id;

-------ACTUALIZAR NOMBRE DEL PUESTO ------------
update empleados
set puesto='Analista IA'
where nombre in ('Sofia Castelar','Camaron Quemado');

select*from empleados order by id;
----------------------------------------

update empleados 
set puesto='Analista de datos'
where id in(1,7);

update empleados
set puesto='Programador Senior'
where id in(4);

select*from empleados order by id;

update empleados
set puesto='Programador junior'
where id in(5);

select*from empleados order by id;



--columnas especificas 
select id as codigo,nombre,salario
from empleados
order by salario desc;

--filtrar a empleados con mayor a 1200 y que tenga como nombre 'analista' en su puesto 
select*from empleados
where salario>=1200 and puesto like 'Analista%';

--in / beetwen / is nul, este comando devuelve empleados cuyo nombre sea xactamente 'Roy Umaña' o “Camaron Quemado”.
select*from empleados
where nombre in ('Roy Umaña','Camaron Quemado');

--busquedas parciales (acentos cuenta tal cual se guardaron)
select*from empleados
where nombre like '%man%';

--limite estandar sql
/*

    1. lo que hace es elegir columnas id,nombre, salario de la tabla empleados
    2. orderna el resultado de mayor a menor salario (desc = descedente)

    3. FETCH FIRST 2 ROWS ONLY: con esto toma solo las 2 primeras filas del resultado despues de ordernar
    es decir, obtiene a los empleados con salario mas alto 


*/
select id,nombre,salario
from empleados
order by salario desc
fetch first 2 rows only;--top 2

--offset + paginacion (estandar)
/*

    1. ordena por id (ascendente por defecto )
    2. offset 2: ignora las primeras 2 filas del resultado ordenado, FETCH NEXT 2 ROWS ONLY: retorna las siguientes 2.

*/
select id,nombre,salario
from empleados
order by id
OFFSET 2 rows fetch next 2 rows only;

--mostrar el id,nombre y puesto de la tabla empledos de Roy Umaña
select id,nombre,puesto from empleados where nombre='Roy Umaña';

--ejemplo de delete 
delete from empleados
where salario<1200;

select*from empleados order by id;


--3) columnas calculadas, CASE, funciones de texto
--redondeo y formateo simple
/*
    
    este comando, selecciona nombre y salario de cada empleado, calcula un 10% extra sobre salario(salario*1.10)
    redondea ese resultado a 2 decimales con round()
    le pone el alias salario_con_bono, 

*/
select nombre,salario,round(salario*1.10,2)as salario_con_bono
from empleados;


--case
/*

    este comando lo que hace es recorrer cada fila de la tabla empleados
    si salario es mayor o igual a 1800 se crea una columna rango que define si es alto
    si es mayor o igual a 1300 el rango se denomina como medio
    si no cumple ninguna el rango se muestra como null

*/
select nombre,
       salario,
       case 
        when salario>=1800 then 'ALTO'
        when salario>=1300 then 'MEDIO'
        end as rango
from empleados;

--funciones comunes 
/*

    lo que hace esta linea de comando:
    1. selecciona la columna nombre y pone todas las letras en mayuscula

    2. selecciona nombre y con length calcula la cantidad de caracters que tiene este, creando una columna nueva en el proceso
    empeiza a contar desde el 1 y cuenta los espacios

    3. y con la funcion now() trae la fecha actual, y con as crea una columna mostrando la fecha


*/
select upper(nombre)as mayus,
       length(nombre)as largo,
       now() as fecha_hora_actual
from empleados;



--Agregaciones, GROUP BY, HAVING
--promedio y conteo por puesto
/*

    group by puesto: agrupa las filas que tienen el mismo puesto,Las filas con puesto NULL forman un grupo propio).

    count(*): cuenta todas las filas del grupo (incluye salarios nulos si los hubiera)

    AVG(salario): promedio del salario en el grupo (ignora NULL en salario)

*/
select puesto,count(*)as cantidad,AVG(salario)as promedio
from empleados
group by puesto;

-------------------------------------------------------------------------------------------------
--Relaciones: otra tabla + FOREIGN KEY + JOIN
-- Departamento y FK
/*

    Primero se crea la tabla departamentos, la borra si existe y la vuelve a crear como esta 

*/
drop table if exists departamentos;
create table departamentos(

    depto_id integer generated always as identity primary key,
    nombre  varchar(80)unique not null

);
--inserta los datos
insert into departamentos(nombre)values
('DATOS'),('SOFTWARE'),('IA');

--agrega la columna depto_id a la tabla empleados
alter table empleados add column depto_id integer;

--aqui actualiza depto_id segun el puesto (demo)
update empleados
set depto_id=(select depto_id from departamentos where nombre='DATOS')
where upper(puesto)like'%DATOS%';

update empleados
set depto_id=(select depto_id from departamentos where nombre='SOFTWARE')
where upper(puesto)like'%SOFT%';

update empleados
set depto_id=(select depto_id from departamentos where nombre='IA')
where upper(puesto)like'%IA%';


--aqui crea la llave foranea(si hay valores no validos no fallara)
--garantiza que cualquier depto_id existente apunte a departamentos.depto_id
--permite nul
alter table empleados
    add constraint fk_empleado_depto 
    foreign KEY (depto_id) references departamentos(depto_id);

--aqui hace el inner join
select e.id,e.nombre,e.puesto,e.salario,d.nombre as departamento
from empleados e
join departamentos d on d.depto_id=e.depto_id
order by e.id;

--left join(para ver empleados aunque no tenga depto)
select e.id, e.nombre, d.nombre as departamento
from empleados e
inner join departamentos d on d.depto_id=e.depto_id
order by e.id;

select id,nombre,puesto
from empleados
where depto_id is null
order by id;
