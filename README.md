# Sockets & servidor "switch"

Un proceso cliente se ejecuta en un nodo de la red, envía una petición de consulta hacia una base de datos que está en otro nodo de la red.
La petición de consulta no se hace directamente sobre el servidor de bases de datos, sino, que se hace sobre un proceso servidor que atiende las peticiones de los clientes de forma concurrente y funciona como un “switch” de conexiones hacia distintos servidores de bases de datos los cuales gestionan distintas bases de datos

![diagram](https://github.com/matiasgimenezdev/sockets-switch/assets/117539520/8b35a523-99ca-4735-93cc-f89ca30f94d5)

## Tecnologias utilizadas 🛠️

-   Java + Spring
-   Docker + Docker compose
-   Firebird
-   Postgres
