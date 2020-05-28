# Complex types Oracle JDBC Dialect
#### ComplexTypesOracleDatabaseDialect
Oracle JDBC dialect implementation with support for Complex types (STRUCT) in Kafka Connect.

Extends OracleDatabaseDialect with support for Struct types.
Converts Struct fields in a SinkRecord to a JSON string and writes the field to Oracle as a CLOB.


#### VarcharAndComplexTypesOracleDatabaseDialect
Extends OracleDatabaseDialect with support for Struct types (as above) and also converts String fields to a VARCHAR(4000) column in Oracle.

An `IllegalArgumentException` will be thrown if the String value is longer than 4000 bytes.



NOTE: project is WIP!
