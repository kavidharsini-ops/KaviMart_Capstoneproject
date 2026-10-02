-- Password placeholder is replaced with a fresh BCrypt hash at seed time.
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (1, 'KaviMart Administrator', 'admin@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'ADMIN', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (2, 'Anaya Textiles', 'seller1@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'SELLER', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (3, 'Rohan Home Studio', 'seller2@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'SELLER', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (4, 'Mira Patel', 'buyer1@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'BUYER', TRUE);
MERGE INTO users (id, name, email, password_hash, role, is_active) KEY(id) VALUES (5, 'Arjun Rao', 'buyer2@kavimart.com', '@@ADMIN_PASSWORD_HASH@@', 'BUYER', TRUE);
MERGE INTO products (id,seller_id,name,description,price,stock_qty,category,image_url) KEY(id) VALUES
(101,2,'Handwoven Indigo Tote','A sturdy everyday tote handwoven from natural cotton with a deep indigo finish.',34.00,18,'Accessories','https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=900&q=80'),
(102,2,'Block Print Cushion Cover','Hand-printed floral cover in soft cotton; made in small batches.',26.50,24,'Home','https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?auto=format&fit=crop&w=900&q=80'),
(103,2,'Linen Everyday Shirt','Relaxed-fit breathable linen shirt in a versatile natural tone.',58.00,12,'Apparel','https://images.unsplash.com/photo-1598033129183-c4f50c736f10?auto=format&fit=crop&w=900&q=80'),
(104,2,'Terracotta Tea Set','A handmade four-piece tea set with a warm, tactile glaze.',42.00,10,'Home','https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=80'),
(105,2,'Indigo Scarf','Lightweight cotton scarf with a naturally dyed pattern.',29.00,16,'Accessories','https://images.unsplash.com/photo-1606760227091-3dd870d97f1d?auto=format&fit=crop&w=900&q=80'),
(106,2,'Botanical Soap Trio','Three small-batch soaps made with plant oils and essential oils.',18.00,30,'Beauty','https://images.unsplash.com/photo-1600857544200-b2f666a9a2ec?auto=format&fit=crop&w=900&q=80'),
(107,3,'Oak Desk Organizer','Solid oak tray to keep daily tools close at hand.',38.00,14,'Home','https://images.unsplash.com/photo-1494438639946-1ebd1d20bf85?auto=format&fit=crop&w=900&q=80'),
(108,3,'Ceramic Pour-over Set','A small-batch stoneware dripper and matching mug.',46.00,9,'Home','https://images.unsplash.com/photo-1517701604599-bb29b565090c?auto=format&fit=crop&w=900&q=80'),
(109,3,'Minimal Leather Wallet','Compact vegetable-tanned leather wallet, stitched by hand.',52.00,11,'Accessories','https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=900&q=80'),
(110,3,'Cotton Lounge Throw','A generous soft cotton throw woven for slow evenings.',64.00,7,'Home','https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=900&q=80'),
(111,3,'Cedar Travel Candle','A clean-burning soy candle with cedar and citrus notes.',22.00,28,'Beauty','https://images.unsplash.com/photo-1603006905003-be475563bc59?auto=format&fit=crop&w=900&q=80'),
(112,3,'Canvas Utility Apron','A durable everyday apron with cross-back straps and deep pockets.',44.00,13,'Apparel','https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?auto=format&fit=crop&w=900&q=80');
ALTER TABLE users ALTER COLUMN id RESTART WITH 6;
ALTER TABLE products ALTER COLUMN id RESTART WITH 113;
