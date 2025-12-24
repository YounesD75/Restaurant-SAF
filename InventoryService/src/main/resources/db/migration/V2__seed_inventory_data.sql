INSERT INTO ingredients (name, quantity, unit, threshold)
VALUES
  ('bun', 200, 'unit', 20),
  ('beef', 200, 'unit', 20),
  ('cheddar', 200, 'slice', 20),
  ('lettuce', 200, 'leaf', 20),
  ('tomato', 200, 'slice', 20),
  ('lemon', 200, 'unit', 20),
  ('mint', 200, 'leaf', 20),
  ('coffee-beans', 200, 'g', 20),
  ('chocolate', 200, 'g', 20),
  ('cream', 200, 'ml', 20);

INSERT INTO recipes (dish_name)
VALUES
  ('burger'),
  ('salad'),
  ('dessert'),
  ('drink'),
  ('coffee');

INSERT INTO recipe_items (recipe_id, ingredient_id, quantity_needed)
SELECT r.id, i.id, 1
FROM recipes r, ingredients i
WHERE r.dish_name = 'burger' AND i.name IN ('bun', 'beef', 'cheddar');

INSERT INTO recipe_items (recipe_id, ingredient_id, quantity_needed)
SELECT r.id, i.id, 1
FROM recipes r, ingredients i
WHERE r.dish_name = 'salad' AND i.name IN ('lettuce', 'tomato');

INSERT INTO recipe_items (recipe_id, ingredient_id, quantity_needed)
SELECT r.id, i.id, 10
FROM recipes r, ingredients i
WHERE r.dish_name = 'dessert' AND i.name IN ('chocolate', 'cream');

INSERT INTO recipe_items (recipe_id, ingredient_id, quantity_needed)
SELECT r.id, i.id, 1
FROM recipes r, ingredients i
WHERE r.dish_name = 'drink' AND i.name IN ('lemon', 'mint');

INSERT INTO recipe_items (recipe_id, ingredient_id, quantity_needed)
SELECT r.id, i.id, 10
FROM recipes r, ingredients i
WHERE r.dish_name = 'coffee' AND i.name IN ('coffee-beans');
