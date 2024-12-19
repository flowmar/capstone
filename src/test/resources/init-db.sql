-- Create parts table
CREATE TABLE IF NOT EXISTS parts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    min INT NOT NULL,
    max INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    machine_id INT,
    company_name VARCHAR(255)
);

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    min INT NOT NULL,
    max INT NOT NULL
);

-- Create product_parts table for many-to-many relationship
CREATE TABLE IF NOT EXISTS product_parts (
    product_id INT,
    part_id INT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (part_id) REFERENCES parts(id),
    PRIMARY KEY (product_id, part_id)
);
