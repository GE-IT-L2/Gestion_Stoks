package packages;

public class Client {
    private int code;
    private String name;
    private String phone;
    private double debt;

    public Client(int code, String name, String phone, double debt) {
        this.code = code;
        this.name = name;
        this.phone = phone;
        this.debt = debt;
    }

    public int getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }

    @Override
    public String toString() {
        return "Client [code=" + code + ", name=" + name + ", phone=" + phone + ", debt=" + debt + "]";
    }
}