/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package no.nav.nada;

import com.datamountaineer.streamreactor.connect.json.SimpleJsonConverter;

import com.fasterxml.jackson.databind.JsonNode;
import io.confluent.connect.jdbc.dialect.DatabaseDialect;
import io.confluent.connect.jdbc.dialect.DatabaseDialectProvider;
import io.confluent.connect.jdbc.dialect.OracleDatabaseDialect;
import io.confluent.connect.jdbc.sink.metadata.SinkRecordField;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.data.*;


import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * An extended OracleDatabaseDialect that extends {@link OracleDatabaseDialect} with support for {@link Struct} types.
 * Converts {@link Struct} fields in a {@link org.apache.kafka.connect.sink.SinkRecord} to a JSON string and writes the
 * field to Oracle as a CLOB.
 */
public class ComplexTypesOracleDatabaseDialect extends OracleDatabaseDialect {

    private static String SUB_PROTOCOL_NAME = "oraclecmplx";

    /**
     * The provider for {@link ComplexTypesOracleDatabaseDialect}.
     */
    public static class Provider extends DatabaseDialectProvider.SubprotocolBasedProvider {
        public Provider() {
            super(ComplexTypesOracleDatabaseDialect.class.getSimpleName(), SUB_PROTOCOL_NAME);
        }

        @Override
        public DatabaseDialect create(AbstractConfig config) {
            return new ComplexTypesOracleDatabaseDialect(config);
        }
    }

    public ComplexTypesOracleDatabaseDialect(AbstractConfig config) {
        super(config);
    }

    @Override
    protected String getSqlType(SinkRecordField field) {
        if (field.schemaName() != null) {
            switch (field.schemaName()) {
                case Decimal.LOGICAL_NAME:
                    return "NUMBER(*," + field.schemaParameters().get(Decimal.SCALE_FIELD) + ")";
                case Date.LOGICAL_NAME:
                    return "DATE";
                case Time.LOGICAL_NAME:
                    return "DATE";
                case Timestamp.LOGICAL_NAME:
                    return "TIMESTAMP";
                default:
                    // fall through to normal types
            }
        }
        switch (field.schemaType()) {
            case INT8:
                return "NUMBER(3,0)";
            case INT16:
                return "NUMBER(5,0)";
            case INT32:
                return "NUMBER(10,0)";
            case INT64:
                return "NUMBER(19,0)";
            case FLOAT32:
                return "BINARY_FLOAT";
            case FLOAT64:
                return "BINARY_DOUBLE";
            case BOOLEAN:
                return "NUMBER(1,0)";
            case STRING:
                return "CLOB";
            case BYTES:
                return "BLOB";
            // Added to handle complex types
            case STRUCT:
            case ARRAY:
                return "CLOB";
            default:
                return super.getSqlType(field);
        }
    }

    @Override
    protected boolean maybeBindPrimitive(
            PreparedStatement statement,
            int index,
            Schema schema,
            Object value
    ) throws SQLException {
        switch (schema.type()) {
            case INT8:
                statement.setByte(index, (Byte) value);
                break;
            case INT16:
                statement.setShort(index, (Short) value);
                break;
            case INT32:
                statement.setInt(index, (Integer) value);
                break;
            case INT64:
                statement.setLong(index, (Long) value);
                break;
            case FLOAT32:
                statement.setFloat(index, (Float) value);
                break;
            case FLOAT64:
                statement.setDouble(index, (Double) value);
                break;
            case BOOLEAN:
                statement.setBoolean(index, (Boolean) value);
                break;
            case STRING:
                statement.setString(index, (String) value);
                break;
            case BYTES:
                final byte[] bytes;
                if (value instanceof ByteBuffer) {
                    final ByteBuffer buffer = ((ByteBuffer) value).slice();
                    bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                } else {
                    bytes = (byte[]) value;
                }
                statement.setBytes(index, bytes);
                break;
            case STRUCT:
            case ARRAY:
                SimpleJsonConverter simpleJson = new SimpleJsonConverter(); // SimpleJsonConverter from datamountaineer
                JsonNode node = simpleJson.fromConnectData(schema, value);
                statement.setString(index, node.toString());
                break;

            default:
                return false;
        }
        return true;
    }

}