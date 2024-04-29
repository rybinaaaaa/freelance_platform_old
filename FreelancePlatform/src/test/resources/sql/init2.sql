CREATE TABLE if not exists users
(
    id         SERIAL PRIMARY KEY,
    rating     INTEGER,
    email      VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(255) NOT NULL CHECK (role IN ('ADMIN', 'USER', 'GUEST')),
    username   VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE if not exists resume
(
    id       SERIAL PRIMARY KEY,
    user_id  INTEGER UNIQUE,
    filename VARCHAR(255) NOT NULL,
    content  OID          NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE if not exists solution
(
    id          SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    content     OID          NOT NULL
);

CREATE TABLE if not exists task
(
    customer_id    INTEGER      NOT NULL,
    freelancer_id  INTEGER,
    id             SERIAL PRIMARY KEY,
    payment        FLOAT        NOT NULL,
    solution_id    INTEGER UNIQUE,
    assigned_date  TIMESTAMP(6),
    deadline       TIMESTAMP(6) NOT NULL,
    posted_date    TIMESTAMP(6),
    submitted_date TIMESTAMP(6),
    problem        VARCHAR(255) NOT NULL,
    status         VARCHAR(255) NOT NULL CHECK (status IN ('UNASSIGNED', 'ASSIGNED', 'SUBMITTED', 'ACCEPTED')),
    title          VARCHAR(255) NOT NULL,
    type           VARCHAR(255) NOT NULL CHECK (type IN
                                                ('TranslationAndLanguageServices', 'DataEntryAndVirtualAssistance',
                                                 'ConsultingAndBusinessServices', 'CreativeAndArtisticServices',
                                                 'GraphicDesignAndMultimedia', 'EngineeringAndArchitecture',
                                                 'WritingAndContentCreation', 'ProgrammingAndDevelopment',
                                                 'GamingAndVrArDevelopment', 'TutoringAndEducation',
                                                 'SalesAndMarketing', 'DigitalMarketing')),
    FOREIGN KEY (customer_id) REFERENCES users (id),
    FOREIGN KEY (freelancer_id) REFERENCES users (id),
    FOREIGN KEY (solution_id) REFERENCES solution (id)
);

CREATE TABLE if not exists correction
(
    id      SERIAL PRIMARY KEY,
    task_id INTEGER      NOT NULL,
    date    TIMESTAMP(6) NOT NULL,
    content VARCHAR(255) NOT NULL,
    FOREIGN KEY (task_id) REFERENCES task (id)
);

CREATE TABLE if not exists feedback
(
    from_user_id INTEGER NOT NULL,
    id           SERIAL PRIMARY KEY,
    rating       INTEGER NOT NULL,
    to_user_id   INTEGER NOT NULL,
    comment      VARCHAR(255),
    FOREIGN KEY (from_user_id) REFERENCES users (id),
    FOREIGN KEY (to_user_id) REFERENCES users (id)
);

CREATE TABLE if not exists proposal
(
    freelancer_id INTEGER NOT NULL,
    id            SERIAL PRIMARY KEY,
    task_id       INTEGER NOT NULL,
    FOREIGN KEY (freelancer_id) REFERENCES users (id),
    FOREIGN KEY (task_id) REFERENCES task (id)
);

INSERT INTO users (id, rating, email, first_name, last_name, password, role, username)
VALUES (1, 80, 'user4@example.com', 'Michael', 'Brown', 'password4', 'USER', 'michaelbrown'),
       (2, 90, 'user5@example.com', 'Emily', 'Davis', 'password5', 'USER', 'emilydavis'),
       (3, 75, 'user6@example.com', 'William', 'Martinez', 'password6', 'USER', 'williammartinez'),
       (4, 85, 'user7@example.com', 'Sophia', 'Garcia', 'password7', 'USER', 'sophiagarcia'),
       (5, 95, 'user8@example.com', 'Alexander', 'Wilson', 'password8', 'USER', 'alexanderwilson');

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
        (select id from users where email = 'user4@example.com'), 10, 'TEST')