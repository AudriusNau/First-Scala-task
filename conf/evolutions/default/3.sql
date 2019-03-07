# --- !Ups

CREATE TABLE labels (
  id BIGSERIAL PRIMARY KEY,
  label TEXT
);
insert into labels(label) values
    ('vadoveliai'),
    ('rasto darbai'),
    ('namu darbai'),
    ('visa kita');

# --- !Downs

drop table if exists "labels" CASCADE;
