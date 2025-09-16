package com.bim.sdk;

import java.io.IOException;
import java.io.InputStream;

public class ProtocolParser {

    private InputStream inputStream;
    private Logger LOGGER = new Logger("BIM", "ProtocolParser");

    public ProtocolParser(InputStream in) {
        inputStream = in;
    }

    public ParseResult readParseResult() {
        ParseResult parseResult = new ParseResult();
        byte[] head = new byte[9];
//        byte[] buffer = null;
//        byte[] tmpBuffer = new byte[8*1024];
        int state = 0; // 0 find head
//        int targetLen = 0;
        int readLen = 0;
        while (true) {
            try {
                if (state == 0) {
                    int len = inputStream.read(head);
                    if (len <= 0) {
                        LOGGER.i("read stream size is 0, exit.");
                        break;
                    }
//                    LOGGER.d("head : " + Utils.printByteArray(head));
                    if (head[0] == 0x56 && head[1] == 0x56 && head[2] == 0x65 && head[3] == 0x65) {
                        parseResult.type = head[4];
//                        targetLen = Utils.byteToInt(head, 5)+1;
//                        buffer = new byte[targetLen];
                        parseResult.data = new byte[Utils.byteToInt(head, 5)+1];
                        readLen = 0;
                        state = 1;
                    }
                } else if (state == 1) {
                    int needReadLen = parseResult.data.length-readLen;
//                    if (needReadLen > tmpBuffer.length) needReadLen = tmpBuffer.length;
                    int len = inputStream.read(parseResult.data, readLen, needReadLen);
//                    LOGGER.d("data : " + Utils.printByteArray(buffer));
                    if (len <= 0) {
                        LOGGER.i("read stream size is 0, exit.");
                        break;
                    }
                    readLen += len;
                    if (readLen < parseResult.data.length) {
                        continue;
                    }
                    byte sum = 0;
                    for (byte bb : head) {
                        sum += bb;
                    }
//                    LOGGER.d("000 sum is " + sum);
                    for (int k=0; k<parseResult.data.length-1; k++) {
                        sum += parseResult.data[k];
                    }
//                    LOGGER.d("111 sum is " + sum + " buffer[buffer.length-1] is " + buffer[buffer.length-1]);
                    if (parseResult.data[parseResult.data.length-1] == sum) {
                        return parseResult;
                    } else {
                        LOGGER.i("check sum fail, exit.");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        return null;
    }

    public void close() throws IOException {
        inputStream.close();
    }

    public class ParseResult {
        byte type; // 1 string, 2 json
        byte[] data;
    }

}    