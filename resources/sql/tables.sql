DROP TABLE IF EXISTS speaker CASCADE;
CREATE TABLE speaker (
  id SERIAL PRIMARY KEY,
  first_name TEXT,
  middle_name TEXT,
  last_name TEXT
);


DROP TABLE IF EXISTS talk CASCADE;
CREATE TABLE talk (
  id SERIAL PRIMARY KEY,
  speaker_id INTEGER NOT NULL,
  name TEXT NOT NULL,
  video_url TEXT,
  slides_url TEXT
);


DROP TABLE IF EXISTS review CASCADE;
CREATE TABLE review (
  id SERIAL PRIMARY KEY,
  talk_id INTEGER NOT NULL,
  rating INTEGER NOT NULL,
  comment TEXT
);


ALTER TABLE talk ADD CONSTRAINT talk_speaker_id_fk 
  FOREIGN KEY (speaker_id) REFERENCES speaker (id);
ALTER TABLE review ADD CONSTRAINT review_talk_id_fk
  FOREIGN KEY (talk_id) REFERENCES talk (id);
