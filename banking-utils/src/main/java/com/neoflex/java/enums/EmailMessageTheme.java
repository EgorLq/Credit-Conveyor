package com.neoflex.java.enums;

public enum EmailMessageTheme {
    FINISH_REGISTRATION {
        @Override
        public String toString() {
            return "finish-registration";
        }
    },
    CREATE_DOCUMENTS {
        @Override
        public String toString() {
            return "create-documents";
        }
    },
    SEND_DOCUMENTS {
        @Override
        public String toString() {
            return "send-documents";
        }
    },
    SEND_SES {
        @Override
        public String toString() {
            return "send-ses";
        }
    },
    CREDIT_ISSUED {
        @Override
        public String toString() {
            return "credit-issued";
        }
    },
    APPLICATION_DENIED {
        @Override
        public String toString() {
            return "application-denied";
        }
    }

}
