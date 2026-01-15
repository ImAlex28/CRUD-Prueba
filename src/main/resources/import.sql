-- Clientes
INSERT INTO cliente (nombre, apellidos, dni, email, register_date) VALUES ('Juan', 'Pérez', '12345678A', 'juan@test.com','2026-01-15');
INSERT INTO cliente (nombre, apellidos, dni, email, register_date) VALUES ('María', 'García', '87654321B', 'maria@test.com','2026-01-12');
INSERT INTO cliente (nombre, apellidos, dni, email, register_date) VALUES ('Pedro', 'López', '11111111C', 'pedro@test.com','2025-12-15');

SHOW COLUMNS FROM cuenta;

-- Cuentas
INSERT INTO cuenta (id_cliente, numero_cuenta, tipo_cuenta, saldo) VALUES (1, 'ES1234567890', 'AHORRO', 1500.50);
INSERT INTO cuenta (id_cliente, numero_cuenta, tipo_cuenta, saldo) VALUES (1, 'ES0987654321', 'CORRIENTE', 250.00);
INSERT INTO cuenta (id_cliente, numero_cuenta, tipo_cuenta, saldo) VALUES (2, 'ES5555444433', 'AHORRO', 3200.00);