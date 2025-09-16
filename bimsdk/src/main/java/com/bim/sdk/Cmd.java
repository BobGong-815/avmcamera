package com.bim.sdk;

public class Cmd {

    protected static int MAX_CODE = 1;
    public int no;  // 消息编号，客户端对服务端某条消息指令的回复，编号必需保持一致
    public String key;
    public String param0;
    public String param1;
    public String param2;
    public String param3;

    public Cmd(String key, String param0, String param1, String param2, String param3) {
        no = MAX_CODE++;
        this.key = key;
        this.param0 = param0;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        if (this.param0 == null) {
            this.param0 = "";
        }
        if (this.param1 == null) {
            this.param1 = "";
        }
        if (this.param2 == null) {
            this.param2 = "";
        }
        if (this.param3 == null) {
            this.param3 = "";
        }
    }

    public Cmd(int no, String key, String param0, String param1, String param2, String param3) {
        this.no = no;
        this.key = key;
        this.param0 = param0;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        if (this.param0 == null) {
            this.param0 = "";
        }
        if (this.param1 == null) {
            this.param1 = "";
        }
        if (this.param2 == null) {
            this.param2 = "";
        }
        if (this.param3 == null) {
            this.param3 = "";
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("Cmd{no " + no)
                .append(", key " + key)
                .append(", param0 " + param0)
                .append(", param1 " + param1)
                .append(", param2 " + param2)
                .append(", param3 " + param3)
                .append("}");

        return stringBuffer.toString();
    }

}
