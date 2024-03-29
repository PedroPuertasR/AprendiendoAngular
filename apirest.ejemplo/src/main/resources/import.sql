INSERT INTO REGIONES (nombre) VALUES ('África');
INSERT INTO REGIONES (nombre) VALUES ('América');
INSERT INTO REGIONES (nombre) VALUES ('Asia');
INSERT INTO REGIONES (nombre) VALUES ('Europa');
INSERT INTO REGIONES (nombre) VALUES ('Oceanía');

INSERT INTO CLIENTE (nombre, apellidos, email, create_at, region_id) VALUES ('Pedro', 'Puertas Rodríguez', 'pedropuertasr@gmail.com', '2023-05-22', 1);
INSERT INTO CLIENTE (nombre, apellidos, email, create_at, region_id) VALUES ('María', 'Gómez Pérez', 'mariagomezp@gmail.com', '2023-06-15', 2);
INSERT INTO CLIENTE (nombre, apellidos, email, create_at, region_id) VALUES ('Juan', 'López Fernández', 'juanl@correo.com', '2023-04-10', 3);
INSERT INTO CLIENTE (nombre, apellidos, email, create_at, region_id) VALUES ('Laura', 'Martínez Sánchez', 'laurams@gmail.com', '2023-07-03', 3);
INSERT INTO CLIENTE (nombre, apellidos, email, create_at, region_id) VALUES ('Carlos', 'Ramírez Vargas', 'carlosrv@correo.com', '2023-08-18', 3);
INSERT INTO CLIENTE (nombre, apellidos, email, create_at, region_id) VALUES ('Ana', 'Hernández Gutiérrez', 'anahg@gmail.com', '2023-09-25', 2);
INSERT INTO CLIENTE (nombre, apellidos, email, create_at, region_id) VALUES ('Luis', 'Torres López', 'luistorresl@gmail.com', '2023-05-12', 1);
INSERT INTO CLIENTE (nombre, apellidos, email, create_at, region_id) VALUES ('Elena', 'García Rodríguez', 'elenagr@gmail.com', '2023-10-05', 1);
INSERT INTO CLIENTE (nombre, apellidos, email, create_at, region_id) VALUES ('Sofía', 'Fernández Martínez', 'sofiafm@correo.com', '2023-07-19', 2);

INSERT INTO ROLES (nombre) VALUES ('ADMIN');
INSERT INTO ROLES (nombre) VALUES ('USER');

INSERT INTO USUARIOS (username, password) VALUES ('Pedro', '$2a$10$oUzCQEGHnAzAukTl0VL.2.FD.fcwXWVYOFu5EqlRCYdq0rfdjXoDe');
INSERT INTO USUARIOS (username, password) VALUES ('Pablo', '$2a$10$.n8VZ4YLEG2YOqjfuzlvtuRPKzuYpBpfGzCImrsTYOy22wzb.khOS');

INSERT INTO USUARIOS_ROLES (usuario_id, role_id) VALUES (1, 1);
INSERT INTO USUARIOS_ROLES (usuario_id, role_id) VALUES (2, 2);