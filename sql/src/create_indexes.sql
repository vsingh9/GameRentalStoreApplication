DROP INDEX IF EXISTS catalog_price;

CREATE INDEX catalog_price
ON Catalog
(price);
