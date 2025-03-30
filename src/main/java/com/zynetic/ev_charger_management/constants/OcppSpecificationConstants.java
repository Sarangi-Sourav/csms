package com.zynetic.ev_charger_management.constants;

import org.springframework.stereotype.Component;

@Component
public final class OcppSpecificationConstants {
    private OcppSpecificationConstants() {}

    // Error Codes
    public static final String ERROR_NOT_IMPLEMENTED = "NotImplemented";
    public static final String ERROR_NOT_SUPPORTED = "NotSupported";
    public static final String ERROR_INTERNAL_ERROR = "InternalError";
    public static final String ERROR_PROTOCOL_ERROR = "ProtocolError";
    public static final String ERROR_SECURITY_ERROR = "SecurityError";
    public static final String ERROR_FORMATION_VIOLATION = "FormationViolation";
    public static final String ERROR_PROPERTY_CONSTRAINT = "PropertyConstraintViolation";
    public static final String ERROR_OCCURRENCE_CONSTRAINT = "OccurrenceConstraintViolation";
    public static final String ERROR_TYPE_CONSTRAINT = "TypeConstraintViolation";
    public static final String ERROR_GENERIC_ERROR = "GenericError";

    // Error Descriptions
    public static final String MESSAGE_NOT_IMPLEMENTED = "Requested Action is not known by receiver";
    public static final String MESSAGE_NOT_SUPPORTED = "Requested Action is recognized but not supported by the receiver";
    public static final String MESSAGE_INTERNAL_ERROR = "An internal error occurred and the receiver was not able to process the requested Action successfully";
    public static final String MESSAGE_PROTOCOL_ERROR = "Payload for Action is incomplete";
    public static final String MESSAGE_SECURITY_ERROR = "During the processing of Action a security issue occurred preventing receiver from completing the Action successfully";
    public static final String MESSAGE_FORMATION_VIOLATION = "Payload for Action is syntactically incorrect or does not conform to the PDU structure for Action";
    public static final String MESSAGE_PROPERTY_CONSTRAINT = "Payload is syntactically correct but at least one field contains an invalid value";
    public static final String MESSAGE_OCCURRENCE_CONSTRAINT = "Payload for Action is syntactically correct but at least one of the fields violates occurrence constraints";
    public static final String MESSAGE_TYPE_CONSTRAINT = "Payload for Action is syntactically correct but at least one of the fields violates data type constraints (e.g. 'somestring': 12)";
    public static final String MESSAGE_GENERIC_ERROR = "Any other error not covered by the previous ones";

}
