# --- !Ups

CREATE TABLE notes (
  id BIGSERIAL PRIMARY KEY,
  text TEXT,
  color TEXT,
  file TEXT
);

# --- !Downs

drop table if exists "notes";


