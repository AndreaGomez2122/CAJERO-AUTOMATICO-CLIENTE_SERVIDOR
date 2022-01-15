# CAJERO-AUTOMATICO-CLIENTE_SERVIDOR

Ejemplo de desarrollo de un cajero automático de banca, usando una base de datos NoSQL MongoDB realizando mapeo a Objetos.
El programa consta de un cliente y un servidor multihilo que será capaz de atender llamadas.
También tiene implementado un log de control de errores.




## Ejecución
Si se quiere ver la base de datos de forma gráfica se deben seguir los siguiente pasos: 

### Docker
Entrar en el directorio docker desde la terminal del propio IDE si quieres
```sh
$ cd docker
```
Y ejecutar con programa docker iniciado.
```sh
$ docker-compose up -d
```
Una vez que el programa se haya ejecutado se habrá creado un volumen con dos imágenes en docker, si todo ha ido bien también se han iniciado.
Sólo se debe introducir localhost:8081 en el navegador y tendrás una vista de la base de datos.


![diagrama](./diagrams/Diagrams.png)


## Autor

Codificado con :sparkling_heart: por [Andrea Gómez de Pablo]
