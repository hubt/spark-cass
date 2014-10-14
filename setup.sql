CREATE KEYSPACE IF NOT EXISTS testkeyspace
  WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '1'};

use testkeyspace;

CREATE TABLE IF NOT EXISTS trigram (
  first text,
  second text,
  third text,
  PRIMARY KEY (first,second,third)
);

copy trigram from 'trigrams';
