-- Password placeholder is replaced with a fresh BCrypt hash at seed time.
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (1, 'KaviMart Administrator', 'admin@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'ADMIN', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (2, 'Little Joy Toys', 'seller1@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'SELLER', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (3, 'Meera Jewels', 'seller2@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'SELLER', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (4, 'Mira Patel', 'buyer1@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'BUYER', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (5, 'Arjun Rao', 'buyer2@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'BUYER', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (6, 'Glow Naturals', 'seller3@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'SELLER', TRUE);
MERGE INTO products (id,seller_id,name,description,price,stock_qty,category,image_url) KEY(id) VALUES
(101,2,'Wooden Building Blocks Set','Colourful wooden blocks for kids. Helps with creativity and hand skills.',899.00,20,'Toys','https://images.pexels.com/photos/4491702/pexels-photo-4491702.jpeg?auto=compress&cs=tinysrgb&w=900'),
(102,2,'Colourful Learning Toy Set','Bright and safe toy set that makes learning fun for small children.',1299.00,15,'Toys','https://images.pexels.com/photos/18403861/pexels-photo-18403861.jpeg?auto=compress&cs=tinysrgb&w=900'),
(103,2,'Kids Fun Play Toy','Fun and colourful toy for daily playtime, made with child-safe material.',699.00,25,'Toys','https://images.pexels.com/photos/14007163/pexels-photo-14007163.jpeg?auto=compress&cs=tinysrgb&w=900'),
(104,6,'Eyeshadow Makeup Palette','Palette with soft shades for day and party looks. Easy to blend.',799.00,30,'Beauty','https://images.pexels.com/photos/11672344/pexels-photo-11672344.jpeg?auto=compress&cs=tinysrgb&w=900'),
(105,6,'Everyday Glow Makeup Kit','Simple makeup kit with the basics for a fresh everyday look.',999.00,22,'Beauty','https://images.pexels.com/photos/30408335/pexels-photo-30408335.jpeg?auto=compress&cs=tinysrgb&w=900'),
(106,6,'Pink Makeup Essentials Set','Set of popular makeup products for a soft pink glam look.',1199.00,18,'Beauty','https://images.pexels.com/photos/34567763/pexels-photo-34567763.jpeg?auto=compress&cs=tinysrgb&w=900'),
(107,3,'Gold Tone Statement Necklace','Elegant gold tone necklace that goes well with sarees and western wear.',849.00,16,'Jewellery','https://images.pexels.com/photos/5380900/pexels-photo-5380900.jpeg?auto=compress&cs=tinysrgb&w=900'),
(108,3,'Elegant Drop Earrings','Lightweight earrings with a classy finish for festivals and functions.',449.00,35,'Jewellery','https://images.pexels.com/photos/35528702/pexels-photo-35528702.jpeg?auto=compress&cs=tinysrgb&w=900'),
(109,3,'Classic Fashion Jewellery Piece','Stylish handmade jewellery piece to complete any outfit.',599.00,28,'Jewellery','https://images.pexels.com/photos/20518707/pexels-photo-20518707.jpeg?auto=compress&cs=tinysrgb&w=900');
ALTER TABLE users ALTER COLUMN id RESTART WITH 7;
ALTER TABLE products ALTER COLUMN id RESTART WITH 116;