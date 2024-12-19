-- Create parts table
CREATE TABLE IF NOT EXISTS parts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    price REAL NOT NULL,
    stock INTEGER NOT NULL,
    min INTEGER NOT NULL,
    max INTEGER NOT NULL,
    type TEXT NOT NULL,
    machine_id INTEGER,
    company_name TEXT
);

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    price REAL NOT NULL,
    stock INTEGER NOT NULL,
    min INTEGER NOT NULL,
    max INTEGER NOT NULL
);

-- Create product_parts table for many-to-many relationship
CREATE TABLE IF NOT EXISTS product_parts (
    product_id INTEGER,
    part_id INTEGER,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (part_id) REFERENCES parts(id),
    PRIMARY KEY (product_id, part_id)
);
