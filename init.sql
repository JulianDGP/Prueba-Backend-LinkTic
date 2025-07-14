CREATE SCHEMA sales;
SET search_path = sales, public;

-- 1. Productos
CREATE TABLE sales.products (
  id              SERIAL PRIMARY KEY,
  name            TEXT    NOT NULL,
  price           NUMERIC(18,2) NOT NULL CHECK (price >= 0),
  description     TEXT,
  created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- 2. Inventario
CREATE TABLE sales.inventory (
  product_id      INTEGER PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
  quantity        INTEGER NOT NULL CHECK (quantity >= 0),
  updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- 3. Historial de compras
CREATE TABLE sales.purchases (
  id                  SERIAL PRIMARY KEY,
  product_id          INTEGER NOT NULL REFERENCES products(id),
  quantity            INTEGER NOT NULL CHECK (quantity > 0),
  price_unit_snapshot NUMERIC(18,2) NOT NULL CHECK (price_unit_snapshot >= 0),
  total_amount        NUMERIC(18,2) NOT NULL,
  purchased_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- Comentarios de la tabla products
COMMENT ON TABLE products IS 'Guarda el catálogo maestro de productos.';
COMMENT ON COLUMN products.id IS 'Clave primaria secuencial que identifica de forma única cada producto.';
COMMENT ON COLUMN products.name IS 'Nombre descriptivo del producto.';
COMMENT ON COLUMN products.price IS 'Precio actual del producto; valor numérico no negativo.';
COMMENT ON COLUMN products.description IS 'Descripción detallada del producto.';
COMMENT ON COLUMN products.created_at IS 'Fecha y hora de creación del registro del producto.';
COMMENT ON COLUMN products.updated_at IS 'Fecha y hora de la última actualización del registro del producto.';

-- Comentarios de la tabla inventory
COMMENT ON TABLE inventory IS 'Mantiene el stock disponible por producto.';
COMMENT ON COLUMN inventory.product_id IS 'Referencia al identificador del producto en la tabla products.';
COMMENT ON COLUMN inventory.quantity IS 'Cantidad disponible en inventario; nunca negativa.';
COMMENT ON COLUMN inventory.updated_at IS 'Fecha y hora de la última modificación del stock.';

-- Comentarios de la tabla purchases
COMMENT ON TABLE purchases IS 'Registra cada transacción de compra realizada.';
COMMENT ON COLUMN purchases.id IS 'Clave primaria secuencial que identifica cada compra.';
COMMENT ON COLUMN purchases.product_id IS 'Referencia al identificador del producto comprado.';
COMMENT ON COLUMN purchases.quantity IS 'Número de unidades compradas; debe ser mayor que cero.';
COMMENT ON COLUMN purchases.price_unit_snapshot IS 'Precio unitario del producto al momento de la compra.';
COMMENT ON COLUMN purchases.total_amount IS 'Importe total de la compra (quantity * price_unit_snapshot).';
COMMENT ON COLUMN purchases.purchased_at IS 'Fecha y hora en que se realizó la compra.';

-- 1. Productos
INSERT INTO sales.products (name, price, description)
VALUES
  ('Camiseta init',      29.99, 'Camiseta unisex de algodón'),
  ('Auriculares',  199.50, 'Inalámbricos con cancelación de ruido'),
  ('Monitor 27"',  349.00, 'QHD, 240Hz, panel QD-OLED');

-- 2. Inventario
INSERT INTO sales.inventory (product_id, quantity)
VALUES
  (1,  100),   -- Camisetas
  (2,   50),   -- Auriculares
  (3,   20);   -- Monitores

-- 3. Compras
-- Cliente compra 2 camisetas a 29.99
INSERT INTO sales.purchases (product_id, quantity, price_unit_snapshot, total_amount)
VALUES
  (1, 2, 29.99, 2 * 29.99),
  -- Cliente compra 1 monitor
  (3, 1, 349.00, 1 * 349.00),
  -- Cliente compra 3 auriculares
  (2, 3, 199.50, 3 * 199.50);