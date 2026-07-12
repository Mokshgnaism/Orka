package com.Orka.util;

import com.Orka.entities.condition.ComparisonOperator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import org.postgresql.util.PGobject;

import java.sql.SQLException;

//import logging
public class JsonUtility {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static JsonNode getValue(JsonNode root, String jsonPath){
        if(root == null || jsonPath == null){
            return null;
        }
        if(!jsonPath.startsWith("$.")) {
            throw new IllegalArgumentException(jsonPath + " is not a valid json path(doesnt start with '.')");
        }
        String[] parts = jsonPath.split("\\.");
        JsonNode current = root;
        for(String part : parts){
            if(current == null){
                throw new IllegalArgumentException(jsonPath + " is not a valid json path(wrong fields or more depth)");
            }
            current = current.get(part);
        }
        return current;
    }

    public static JsonNode setValue(
            JsonNode original,
            String jsonPath,
            JsonNode value) {

        if (!(original instanceof ObjectNode root)) {
            throw new IllegalArgumentException(
                    "Root must be an ObjectNode");
        }

        if (!jsonPath.startsWith("$."))
            throw new IllegalArgumentException(
                    "Invalid json path");

        String[] parts =
                jsonPath.substring(2).split("\\.");

        ObjectNode current = root;

        for (int i = 0; i < parts.length - 1; i++) {

            JsonNode child = current.get(parts[i]);

            if (child == null || !child.isObject()) {

                ObjectNode newChild =
                        JsonNodeFactory.instance.objectNode();

                current.set(parts[i], newChild);

                current = newChild;

            } else {

                current = (ObjectNode) child;

            }
        }

        current.set(parts[parts.length - 1], value);

        return root;
    }


    public static boolean compare(
            JsonNode actual,
            JsonNode expected,
            ComparisonOperator operator) {

        if (actual == null)
            return false;

        switch (operator) {

            case EQUAL:
                return actual.equals(expected);

            case NOT_EQUAL:
                return !actual.equals(expected);

            case GREATER_THAN:
                return actual.asDouble() >
                        expected.asDouble();

            case GREATER_THAN_EQUAL:
                return actual.asDouble() >=
                        expected.asDouble();

            case LESS_THAN:
                return actual.asDouble() <
                        expected.asDouble();

            case LESS_THAN_EQUAL:
                return actual.asDouble() <=
                        expected.asDouble();

            case EXISTS:
                return !actual.isMissingNode();

            default:
                throw new IllegalArgumentException(
                        "Unsupported operator"
                );
        }
    }
        public static JsonNode translateFromProtobufValue(Value value) {
            try {
                String json = JsonFormat.printer().print(value);
                return OBJECT_MAPPER.readTree(json);
            } catch (Exception e) {
                throw new RuntimeException("Failed to translate protobuf Value to JsonNode", e);
            }
        }
    public static PGobject toJson(JsonNode node) throws SQLException {

        PGobject object = new PGobject();
        object.setType("jsonb");
        object.setValue(node.toString());

        return object;
    }
}
