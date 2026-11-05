
INSERT INTO users (id, created_at, updated_at, email, password, role, username)
VALUES (1, now(), now(), 't@t.t', 'test', 'USER', 'test');

INSERT INTO channel (id, name)
VALUES (1, 'test');

INSERT INTO post (id, title, content, user_id, channel_id, created_at, updated_at, comment_count, dislike_count, like_count, view_count)
VALUES (1, 'test', 'test', 1, 1, now(), now(), 0, 0, 0, 0);