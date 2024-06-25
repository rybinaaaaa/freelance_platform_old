-- INSERT INTO users (id, rating, email, first_name, last_name, password, role, username)
-- VALUES (1, 80, 'user4@example.com', 'Michael', 'Brown', 'password4', 'ADMIN', 'michaelbrown'),
--        (2, 90, 'user5@example.com', 'Emily', 'Davis', 'password5', 'USER', 'emilydavis'),
--        (3, 75, 'user6@example.com', 'William', 'Martinez', 'password6', 'USER', 'williammartinez'),
--        (4, 85, 'user7@example.com', 'Sophia', 'Garcia', 'password7', 'USER', 'sophiagarcia'),
--        (5, 95, 'user8@example.com', 'Alexander', 'Wilson', 'password8', 'USER', 'alexanderwilson');

INSERT INTO users (id, rating, email, first_name, last_name, password, role, username)
VALUES (1, 80, 'user4@example.com', 'Michael', 'Brown', '$2a$12$xXtjvTcxuXMER50T6nAbaO8/6QNk2uckE3y3D3223AoNF7sMEexp.', 'ADMIN', 'michaelbrown'),
       (2, 90, 'user5@example.com', 'Emily', 'Davis', '$2a$12$UJ9k7D36g2uLltJhwMhK4O0wGL3gzqbtsQrL7Inp0PwS1JWVErymG', 'USER', 'emilydavis'),
       (3, 75, 'user6@example.com', 'William', 'Martinez', '$2a$12$QP/o.ZCfMmfYrbEaHHyj1eiq7x2ROssxH3ldgE1hzhRkpLZA3usTa', 'USER', 'williammartinez'),
       (4, 85, 'user7@example.com', 'Sophia', 'Garcia', '$2a$12$eHRV6Cty7Pp48jf6hqMOY.89RN6YkxxNqXJattHDMgEfYeC7stGZK', 'USER', 'sophiagarcia'),
       (5, 95, 'user8@example.com', 'Alexander', 'Wilson', '$2a$12$rlyIVHeFkyVs5P.S1lopee/xnDrsG0zZSiAe9E.YlLFZ6.TlShAy2', 'USER', 'alexanderwilson');

INSERT INTO task (id, customer_id, freelancer_id, payment, deadline, problem, status, title, type)
VALUES (1, (select id from users where email = 'user5@example.com'),
        (select id from users where email = 'user4@example.com'), 1.0, now(), 'some problem', 'ASSIGNED', 'test1', 'TranslationAndLanguageServices'),
       (2, (select id from users where email = 'user7@example.com'),
        (select id from users where email = 'user4@example.com'), 1.0, now(), 'some problem', 'ASSIGNED', 'test2', 'TranslationAndLanguageServices');

INSERT INTO proposal (id, freelancer_id, task_id)
VALUES (1, (select id from users where email = 'user4@example.com'),
        (select id from task where task.customer_id = (select id from users where email = 'user5@example.com'))),
       (2, (select id from users where email = 'user4@example.com'),
        (select id from task where task.customer_id = (select id from users where email = 'user7@example.com')));

INSERT INTO feedback (id, from_user_id, to_user_id, rating, comment)
VALUES (1, (select id from users where email = 'user5@example.com'),
        (select id from users where email = 'user4@example.com'), 5, 'TEST'),
       (2, (select id from users where email = 'user7@example.com'),
        (select id from users where email = 'user4@example.com'), 10, 'TEST');

INSERT INTO solution (id, task_id, description, link)
VALUES (1, 1, 'Translation completed as requested.', 'https://example.com/translation1'),
       (2, 2, 'Translation in progress.', 'https://example.com/translation2');