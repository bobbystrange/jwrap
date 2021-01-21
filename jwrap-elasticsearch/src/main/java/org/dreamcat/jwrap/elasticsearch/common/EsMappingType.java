package org.dreamcat.jwrap.elasticsearch.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Create by tuke on 2021/1/21
 * <p>
 * term query searches on keyword fields are often faster than
 * term searches on numeric fields
 */
@Getter
@RequiredArgsConstructor
public enum EsMappingType {
    BINARY("binary"), // accepts a binary value as a Base64 encoded string.
    BOOLEAN("boolean"),
    // keyword
    KEYWORD("keyword"), // term query
    CONSTANT_KEYWORD("constant_keyword"), // "value": "debug"
    WILDCARD("wildcard"), // wildcard query
    // integer
    BYTE("byte"),
    SHORT("short"),
    INTEGER("integer"),
    LONG("long"),
    UNSIGNED_LONG("unsigned_long"),
    // float
    HALF_FLOAT("half_float"),
    FLOAT("float"),
    DOUBLE("double"),
    SCALED_FLOAT("scaled_float"), // "scaling_factor": 10
    // date
    DATE("date"), // "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
    DATE_NANOS("date_nanos"),
    // alias
    ALIAS("alias"), // "path": "target_field"
    // object
    FLATTENED("flattened"),
    NESTED("nested"),
    // range
    INTEGER_RANGE("integer_range"), // {"gte": 1, "lt": 0}
    FLOAT_RANGE("float_range"),
    LONG_RANGE("long_range"),
    DOUBLE_RANGE("double_range"),
    DATE_RANGE("date_range"),
    IP_RANGE("ip_range"),
    // string
    TEXT("text");

    private final String name;
}
