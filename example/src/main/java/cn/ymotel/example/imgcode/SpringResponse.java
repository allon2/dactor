package cn.ymotel.example.imgcode;

import java.io.Serializable;

public class SpringResponse  implements Serializable {
    private String resultByteString;
    private String[] values;

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String getResultByteString() {
        return resultByteString;
    }

    public void setResultByteString(String resultByteString) {
        this.resultByteString = resultByteString;
    }
}
