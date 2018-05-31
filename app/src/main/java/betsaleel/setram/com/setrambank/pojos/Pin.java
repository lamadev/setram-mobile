package betsaleel.setram.com.setrambank.pojos;

/**
 * Created by hornellama on 31/12/2017.
 */

public class Pin {
    private String oldPin;
    private String newPin;
    private String accountNum;

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }

    public void setOldPin(String oldPin) {
        this.oldPin = oldPin;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public String getNewPin() {
        return newPin;
    }

    public String getOldPin() {
        return oldPin;
    }


}
