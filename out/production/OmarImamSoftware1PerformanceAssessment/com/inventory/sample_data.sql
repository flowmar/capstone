-- Sample Parts Data
INSERT INTO parts (id, name, price, stock, min, max, type, machine_id, company_name) VALUES
(101, 'Steel Hammer', 15.99, 50, 10, 100, 'InHouse', 1001, NULL),
(102, 'Fiberglass Hammer', 22.50, 35, 5, 50, 'InHouse', 1002, NULL),
(103, 'Precision Screwdriver Set', 29.99, 40, 10, 75, 'InHouse', 1003, NULL),
(104, 'Heavy Duty Screwdriver', 18.75, 45, 15, 60, 'InHouse', 1004, NULL),
(105, 'Compact Tape Measure', 12.50, 60, 20, 100, 'InHouse', 1005, NULL),
(106, 'Professional Tape Measure', 24.99, 30, 10, 50, 'InHouse', 1006, NULL),
(107, 'Stainless Steel Nails', 9.99, 200, 50, 500, 'InHouse', 1007, NULL),
(108, 'Brass Wood Screws', 14.50, 150, 50, 300, 'InHouse', 1008, NULL),
(109, 'Cordless Power Drill', 129.99, 25, 5, 40, 'InHouse', 1009, NULL),
(110, 'Drill Bit Set', 49.99, 35, 10, 50, 'InHouse', 1010, NULL),
(111, 'Professional Paint Brushes', 34.99, 40, 15, 75, 'Outsourced', NULL, 'ArtCraft Supplies'),
(112, 'Gorilla Glue', 7.99, 100, 25, 200, 'Outsourced', NULL, 'Adhesive Masters'),
(113, 'Heavy Duty Duct Tape', 6.50, 150, 50, 300, 'Outsourced', NULL, 'Tape World');

-- Sample Products Data
INSERT INTO products (id, name, price, stock, min, max) VALUES
(201, 'DIY Home Repair Kit', 79.99, 20, 5, 35),
(202, 'Professional Contractor Toolkit', 249.99, 15, 5, 25),
(203, 'Weekend Warrior Set', 129.50, 25, 10, 40),
(204, 'Painting Essentials Bundle', 54.99, 30, 10, 50),
(205, 'Home Improvement Starter Pack', 99.99, 20, 5, 35),
(206, 'Woodworking Basics Kit', 189.99, 15, 5, 25),
(207, 'Gardening and Repair Collection', 149.50, 18, 5, 30),
(208, 'Emergency Repair Kit', 64.99, 35, 10, 50),
(209, 'Professional Craftsman Bundle', 299.99, 10, 3, 20),
(210, 'Home Maintenance Mega Set', 179.99, 15, 5, 25);

-- Product Parts Associations
INSERT INTO product_parts (product_id, part_id) VALUES
(201, 101), (201, 103), (201, 105), (201, 107),
(202, 102), (202, 104), (202, 109), (202, 110),
(203, 101), (203, 105), (203, 108), (203, 109),
(204, 111), (204, 112),
(205, 103), (205, 105), (205, 107), (205, 108),
(206, 102), (206, 104), (206, 110),
(207, 101), (207, 106), (207, 108), (207, 113),
(208, 103), (208, 107), (208, 112), (208, 113),
(209, 102), (209, 104), (209, 109), (209, 110), (209, 111),
(210, 101), (210, 105), (210, 106), (210, 107), (210, 108), (210, 113);
