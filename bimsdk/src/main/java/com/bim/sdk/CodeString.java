package com.bim.sdk;

public class CodeString {

    int id;
    String name;

    public CodeString(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("CodeString( ").
                append(id).
                append(" - ").
                append(name).
                append(" )");
        return stringBuffer.toString();
    }

}
