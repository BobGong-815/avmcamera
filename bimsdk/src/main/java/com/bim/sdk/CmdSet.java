package com.bim.sdk;

import java.util.ArrayList;
import java.util.List;

public class CmdSet {

    public List<Cmd> result = new ArrayList<>();

    public void addCmd(Cmd cmd) {
        result.add(cmd);
    }

}
