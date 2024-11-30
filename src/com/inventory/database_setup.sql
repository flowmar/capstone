CREATE TABLE IF NOT EXISTS parts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    min INT NOT NULL,
    max INT NOT NULL,
    type ENUM('InHouse', 'Outsourced') NOT NULL,
    company_name VARCHAR(255),
    machine_id INT
);

CREATE TABLE IF NOT EXISTS products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    min INT NOT NULL,
    max INT NOT NULL
);

CREATE TABLE IF NOT EXISTS product_parts (
    product_id INT,
    part_id INT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (part_id) REFERENCES parts(id),
    PRIMARY KEY (product_id, part_id)
);
