package com.cfl.domain;

public class ApiResponse {
    private ApiResponseHeader header;
    private Object body;

    public ApiResponse() {
        header = new ApiResponseHeader();
    }

    public ApiResponse(boolean isSuccessful, Integer resultCode, String resultMessage) {
        this();
        header.setResultCode(resultCode);
        header.setIsSuccessful(isSuccessful);
        header.setResultMessage(resultMessage);
    }

    public ApiResponse(boolean isSuccessful, int resultCode, String resultMessage, Object bodyObj) {
        this(isSuccessful, resultCode, resultMessage);
        body = bodyObj;
    }

    public ApiResponseHeader getHeader() {
        return header;
    }

    public Object getBody() {
        return body;
    }

    private class ApiResponseHeader {
        private Boolean isSuccessful;
        private Integer resultCode;
        private String resultMessage;

        public Boolean getIsSuccessful() {
            return isSuccessful;
        }

        public void setIsSuccessful(Boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
        }

        public Integer getResultCode() {
            return resultCode;
        }

        public void setResultCode(Integer resultCode) {
            this.resultCode = resultCode;
        }

        public String getResultMessage() {
            return resultMessage;
        }

        public void setResultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
        }
    }
}
