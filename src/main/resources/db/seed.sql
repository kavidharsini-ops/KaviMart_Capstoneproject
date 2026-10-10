-- Password placeholder is replaced with a fresh BCrypt hash at seed time.
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (1, 'KaviMart Administrator', 'admin@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'ADMIN', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (2, 'Anaya Textiles', 'seller1@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'SELLER', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (3, 'Meera Accessories Studio', 'seller2@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'SELLER', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (4, 'Mira Patel', 'buyer1@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'BUYER', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (5, 'Arjun Rao', 'buyer2@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'BUYER', TRUE);
MERGE INTO products (id,seller_id,name,description,price,stock_qty,category,image_url) KEY(id) VALUES
(101,2,'Cotton Anarkali Kurti','Flared cotton kurti with a soft floral print, easy to wear every day.',899.00,20,'Apparel','https://placehold.co/900x1050/f3eddf/526b5b.png?text=Cotton+Anarkali+Kurti'),
(102,2,'Embroidered Kurti Palazzo Set','Kurti and palazzo set with fine thread embroidery on the neckline.',1499.00,12,'Apparel','https://placehold.co/900x1050/f3eddf/526b5b.png?text=Kurti+Palazzo+Set'),
(103,2,'Floral Maxi Dress','Light maxi dress with a flowy skirt, made for summer outings.',1299.00,14,'Apparel','https://placehold.co/900x1050/f3eddf/526b5b.png?text=Floral+Maxi+Dress'),
(104,2,'Chikankari Dupatta','Soft georgette dupatta with hand chikankari work.',749.00,16,'Accessories','https://placehold.co/900x1050/f3eddf/526b5b.png?text=Chikankari+Dupatta'),
(105,2,'Indigo Scarf','Lightweight cotton scarf with a naturally dyed pattern.',399.00,25,'Accessories','https://images.unsplash.com/photo-1606760227091-3dd870d97f1d?auto=format&fit=crop&w=900&q=80'),
(106,2,'Handwoven Indigo Tote','A sturdy handwoven cotton tote with a deep indigo finish, big enough for daily use.',699.00,18,'Accessories','https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=900&q=80'),
(107,2,'Cotton Co-ord Set','Matching top and pants set in breathable cotton.',1199.00,10,'Apparel','https://placehold.co/900x1050/f3eddf/526b5b.png?text=Cotton+Co-ord+Set'),
(108,3,'Oxidised Silver Jhumkas','Traditional jhumka earrings with an oxidised silver finish.',349.00,30,'Accessories','https://placehold.co/900x1050/f3eddf/526b5b.png?text=Oxidised+Jhumkas'),
(109,3,'Pearl Drop Necklace Set','Simple pearl drop necklace with matching earrings.',799.00,15,'Accessories','https://placehold.co/900x1050/f3eddf/526b5b.png?text=Pearl+Necklace+Set'),
(110,3,'Beaded Handmade Bracelet','Colourful handmade bracelet with glass beads.',249.00,40,'Accessories','https://placehold.co/900x1050/f3eddf/526b5b.png?text=Beaded+Bracelet'),
(111,3,'Fabric Scrunchie Pack','Pack of five soft scrunchies in assorted colours.',199.00,50,'Accessories','https://placehold.co/900x1050/f3eddf/526b5b.png?text=Scrunchie+Pack'),
(112,3,'Leather Clutch Wallet','Compact vegetable-tanned leather wallet, stitched by hand.',599.00,11,'Accessories','https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=900&q=80'),
(113,3,'Botanical Soap Trio','Three small-batch soaps made with plant oils and essential oils.',349.00,30,'Beauty','https://images.unsplash.com/photo-1600857544200-b2f666a9a2ec?auto=format&fit=crop&w=900&q=80'),
(114,3,'Rose Tinted Lip Balm Set','Set of three tinted lip balms with a light rose flavour.',299.00,35,'Beauty','https://placehold.co/900x1050/f3eddf/526b5b.png?text=Rose+Lip+Balm+Set'),
(115,3,'Herbal Hair Oil','Light hair oil made with amla, bhringraj and coconut oil.',399.00,22,'Beauty','https://placehold.co/900x1050/f3eddf/526b5b.png?text=Herbal+Hair+Oil');
ALTER TABLE users ALTER COLUMN id RESTART WITH 6;
ALTER TABLE products ALTER COLUMN id RESTART WITH 116;
