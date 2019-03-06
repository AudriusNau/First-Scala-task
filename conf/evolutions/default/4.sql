# --- !Ups

CREATE TABLE relationTbl (
  noteId bigint,
  labelId bigint,
  foreign key (noteId) reference notes (id),
  foreign key (labelId) reference labels (id)
  PRIMARY KEY(noteId, labelId)
);

# --- !Downs

drop table if exists "relationTbl";
