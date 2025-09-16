package com.bim.sdk;

public interface LlmListener {

    void onLlmContent(int no, String text);
    void onLlmError(String text);

}
