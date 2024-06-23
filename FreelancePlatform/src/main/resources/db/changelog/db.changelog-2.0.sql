SELECT setval('users_id_seq', (SELECT  MAX(id) FROM users));
SELECT setval('task_id_seq', (SELECT  MAX(id) FROM task));
SELECT setval('proposal_id_seq', (SELECT  MAX(id) FROM proposal));
SELECT setval('feedback_id_seq', (SELECT  MAX(id) FROM feedback));
SELECT setval('solution_id_seq', (SELECT  MAX(id) FROM solution));