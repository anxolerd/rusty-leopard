-- name: get-talk
SELECT
  id,
  speaker_id,
  name,
  video_url,
  slides_url
FROM talk
WHERE id = :id
LIMIT 1;

-- name: get-speaker
SELECT
  id,
  first_name,
  middle_name,
  last_name
FROM speaker
WHERE id = :id
LIMIT 1;

-- name: get-speaker-talks
SELECT
  id,
  speaker_id,
  name,
  video_url,
  slides_url
FROM talk
WHERE speaker_id = :speaker_id;

-- name: get-speaker-reviews
SELECT
  r.id,
  r.talk_id,
  r.rating,
  r.comment
FROM talk t
JOIN review r ON r.talk_id = t.id
WHERE t.speaker_id = :speaker_id;

-- name: get-talk-reviews
SELECT
  id,
  talk_id,
  rating,
  comment
FROM review
WHERE talk_id = :talk_id;

-- name: get-speakers
SELECT
  id,
  first_name,
  middle_name,
  last_name
FROM speaker;

-- name: get-speaker-rating
SELECT
  avg(rating) AS rating
FROM review
JOIN talk ON review.talk_id = talk.id
WHERE talk.speaker_id = :speaker_id;

-- name: speaker-exists?
SELECT (EXISTS (
  SELECT 1
  FROM speaker
  WHERE first_name = :first_name
    AND last_name = :last_name
    AND coalesce(middle_name = :middle_name, TRUE)
)) AS result;


-- name: speaker-exists-by-id?
SELECT (EXISTS (
  SELECT 1
  FROM speaker
  WHERE id = :id
)) AS result;

-- name: talk-exists?
SELECT (EXISTS (
  SELECT 1
  FROM talk
  WHERE speaker_id = :speaker_id
    AND name = :name
)) AS result;

-- name: talk-exists-by-id?
SELECT (EXISTS (
  SELECT 1
  FROM talk
  WHERE id = :id
)) AS result;

-- name: add-speaker
INSERT INTO speaker (first_name, middle_name, last_name)
VALUES (:first_name, :middle_name, :last_name)
RETURNING id;

-- name: add-talk
INSERT INTO talk (speaker_id, name, video_url, slides_url)
VALUES (:speaker_id, :name, :video_url, :slides_url)
RETURNING id;

-- name: add-review
INSERT INTO review (talk_id, rating, comment)
VALUES (:talk_id, :rating, :comment)
RETURNING id;
