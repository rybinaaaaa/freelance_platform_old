CREATE TABLE if not exists resume
(
    id       SERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    content  OID          NOT NULL
);

CREATE TABLE if not exists users
(
    id         SERIAL PRIMARY KEY,
    rating     INTEGER,
    resume_id  INTEGER UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(255) CHECK (role IN ('ADMIN', 'FREELANCER', 'CUSTOMER', 'GUEST')),
    username   VARCHAR(255) NOT NULL UNIQUE,
    FOREIGN KEY (resume_id) REFERENCES resume (id)
);

CREATE TABLE if not exists feedback
(
    id           SERIAL PRIMARY KEY,
    from_user_id INTEGER NOT NULL,
    to_user_id   INTEGER NOT NULL,
    rating       INTEGER NOT NULL,
    comment      VARCHAR(255),
    FOREIGN KEY (to_user_id) REFERENCES users (id),
    FOREIGN KEY (from_user_id) REFERENCES users (id)
);

CREATE TABLE if not exists task
(
    id            SERIAL PRIMARY KEY,
    customer_id   INTEGER      NOT NULL,
    freelancer_id INTEGER,
    payment       FLOAT        NOT NULL,
    deadline      TIMESTAMP    NOT NULL,
    problem       VARCHAR(255) NOT NULL,
    status        VARCHAR(255) NOT NULL CHECK (status IN ('ASSIGNED', 'UNASSIGNED', 'SUBMITTED', 'REVIEWED')),
    title         VARCHAR(255) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES users (id),
    FOREIGN KEY (freelancer_id) REFERENCES users (id)
);

CREATE TABLE if not exists notification
(
    id      SERIAL PRIMARY KEY,
    task_id INTEGER      NOT NULL,
    text    VARCHAR(255) NOT NULL,
    type    VARCHAR(255) NOT NULL CHECK (type IN ('TaskWasCompleted', 'TaskWasPosted')),
    FOREIGN KEY (task_id) REFERENCES task (id)
);

CREATE TABLE if not exists notification_users
(
    notification_id INTEGER NOT NULL,
    users_id        INTEGER NOT NULL,
    PRIMARY KEY (notification_id, users_id),
    FOREIGN KEY (users_id) REFERENCES users (id),
    FOREIGN KEY (notification_id) REFERENCES notification (id)
);

CREATE TABLE if not exists proposal
(
    id            SERIAL PRIMARY KEY,
    freelancer_id INTEGER NOT NULL,
    task_id       INTEGER NOT NULL,
    FOREIGN KEY (freelancer_id) REFERENCES users (id),
    FOREIGN KEY (task_id) REFERENCES task (id)
);

INSERT INTO users (id, rating, email, first_name, last_name, password, role, username, resume_id)
VALUES (1, 80, 'user4@example.com', 'Michael', 'Brown', 'password4', 'FREELANCER', 'michaelbrown', NULL),
       (2, 90, 'user5@example.com', 'Emily', 'Davis', 'password5', 'CUSTOMER', 'emilydavis', NULL),
       (3, 75, 'user6@example.com', 'William', 'Martinez', 'password6', 'FREELANCER', 'williammartinez', NULL),
       (4, 85, 'user7@example.com', 'Sophia', 'Garcia', 'password7', 'CUSTOMER', 'sophiagarcia', NULL),
       (5, 95, 'user8@example.com', 'Alexander', 'Wilson', 'password8', 'FREELANCER', 'alexanderwilson', NULL);

INSERT INTO task (id, customer_id, freelancer_id, payment, deadline, problem, status, title)
VALUES (1, (select id from users where email = 'user5@example.com'),
        (select id from users where email = 'user4@example.com'), 1.0, now(), 'some problem', 'ASSIGNED', 'test1'),
       (2, (select id from users where email = 'user7@example.com'),
        (select id from users where email = 'user4@example.com'), 1.0, now(), 'some problem', 'ASSIGNED', 'test2');

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