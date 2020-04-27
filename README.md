# Complex types Oracle JDBC Dialect
Oracle JDBC dialect implementation with support for Complex types (STRUCT) in Kafka Connect.

Extends OracleDatabaseDialect with support for Struct types.
Converts Struct fields in a SinkRecord to a JSON string and writes the field to Oracle as a CLOB.

NOTE: project is WIP!
