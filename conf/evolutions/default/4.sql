# --- !Ups

CREATE TABLE relationTbl (
  noteId bigint,
  labelId bigint,
  FOREIGN KEY (noteId) REFERENCES notes (id),
  FOREIGN KEY (labelId) REFERENCES labels (id),
  PRIMARY KEY(noteId, labelId)
);

# --- !Downs

drop table if exists "relationTbl";
