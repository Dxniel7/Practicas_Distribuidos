CREATE DATABASE panaderia;
USE panaderia;

CREATE TABLE inventario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    producto VARCHAR(50) NOT NULL,
    stock INT NOT NULL
);


INSERT INTO inventario (producto, stock) VALUES ('Pan', 15);
INSERT INTO inventario (producto, stock) VALUES ('Pan de trigo', 20);
INSERT INTO inventario (producto, stock) VALUES ('Pan de centeno', 15);
INSERT INTO inventario (producto, stock) VALUES ('Pan integral', 30);
INSERT INTO inventario (producto, stock) VALUES ('Pan de avena', 10);
INSERT INTO inventario (producto, stock) VALUES ('Pan de maíz', 25);
INSERT INTO inventario (producto, stock) VALUES ('Donas', 25);
INSERT INTO inventario (producto, stock) VALUES ('Conchas', 25);

select * from inventario;
UPDATE inventario SET stock = 15;