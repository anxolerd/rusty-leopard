-- name: get-talk
SELECT id, speaker_id, name, video_url, slides_url FROM talk WHERE id = :id LIMIT 1;

-- name: get-speaker
SELECT id, first_name, last_name FROM speaker WHERE id = :id LIMIT 1;

-- name: get-speaker-talks
SELECT id, speaker_id, name, video_url, slides_url FROM talk WHERE speaker_id = :speaker_id;

-- name: get-speaker-reviews
SELECT r.id, r.talk_id, r.rating, r.comment FROM talk t JOIN review r ON r.talk_id = t.id WHERE t.speaker_id = :speaker_id;

-- name: get-talk-reviews
SELECT id, talk_id, rating, comment FROM review WHERE talk_id = :talk_id;

-- name: get-speakers
SELECT id, first_name, last_name FROM speaker;

-- name: add-speaker
INSERT INTO speaker (first_name, last_name) VALUES (:first_name, :last_name) RETURNING id;
