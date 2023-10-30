# Sockets & servidor "switch"

Un proceso cliente se ejecuta en un nodo de la red, envía una petición de consulta hacia una base de datos que está en otro nodo de la red.
La petición de consulta no se hace directamente sobre el servidor de bases de datos, sino, que se hace sobre un proceso servidor que atiende las peticiones de los clientes de forma concurrente y funciona como un “switch” de conexiones hacia distintos servidores de bases de datos los cuales gestionan distintas bases de datos

![diagram](https://github.com/matiasgimenezdev/sockets-switch/assets/117539520/8b35a523-99ca-4735-93cc-f89ca30f94d5)

## Tecnologias utilizadas 🛠️

-   Java + Spring
-   Docker + Docker compose
-   Firebird
-   Postgres

## Instrucciones de ejecución

-   Para iniciar la aplicación:

```bash
    bash start.sh
```

-   Para detener la ejecucion:

```bash
    docker-compose down --rmi all
```

## Bases de datos

Las bases de datos ya se encuentran creadas: 'FACTURACION' en Firebird y 'PERSONAL' en Postgres

#### FACTURACION

```sql
    CREATE TABLE FACTURA (
        NUMERO INTEGER NOT NULL,
        FECHA DATE NOT NULL,
        MONTO DOUBLE PRECISION DEFAULT 0.0,
        CONSTRAINT PK_FACTURA PRIMARY KEY (NUMERO)
    );

    CREATE TABLE PRODUCTO (
        CODIGO INTEGER NOT NULL,
        DESCRIPCION VARCHAR(100) NOT NULL,
        STOCK INTEGER DEFAULT 0 NOT NULL,
        PRECIO DOUBLE PRECISION DEFAULT 0 NOT NULL,
        CONSTRAINT PK_PRODUCTO PRIMARY KEY (CODIGO)
    );

    CREATE TABLE DETALLE (
        NUMERO INTEGER NOT NULL,
        CODIGO INTEGER NOT NULL,
        CANTIDAD INTEGER DEFAULT 1 NOT NULL,
        PRECIO DOUBLE PRECISION DEFAULT 0 NOT NULL,
        SUBTOTAL DOUBLE PRECISION,
        CONSTRAINT PK_DETALLE PRIMARY KEY (NUMERO,CODIGO),
        CONSTRAINT FK_DETALLE_PRODUCTO FOREIGN KEY (CODIGO) REFERENCES PRODUCTO,
        CONSTRAINT FK_DETALLE_FACTURA FOREIGN KEY (NUMERO) REFERENCES FACTURA
    );
```

#### PERSONAL

```sql
    CREATE TABLE EMPLEADO (
        ID serial PRIMARY KEY,
        NOMBRE VARCHAR(120) NOT  NULL,
        APELLIDO VARCHAR(120) NOT  NULL,
        SALARIO NUMERIC(10, 2) DEFAULT 0
    );
```
