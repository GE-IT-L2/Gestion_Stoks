package packages;

public class Supplier {
    
    private int SupplierCode;
    private String SupplierName;
    private String telephone;
    private String address;
    private double debtSupplier;
    
   

   

    public Supplier(int SupplierCode,String SupplierName,String telephone,String address,double debtSupplier){
        this.SupplierCode = SupplierCode;
        this.SupplierName = SupplierName;
        this.telephone = telephone;
        this.address = address;
        this.debtSupplier = debtSupplier;

    }

    public  int getSupplierCode(){
        return SupplierCode;
    }
    public void setSupplierCode(int SupplierCode){
        this.SupplierCode = SupplierCode;
    }

    public String getSupplierName(){
        return SupplierName;
    }
    public void setSupplierName(String SupplierName){
        this.SupplierName = SupplierName;
    }

    public String getTelephone(){
        return telephone;
    }
    public void setTelephone(String telephone){
        this.telephone = telephone;
    }

    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }

    public double getDebtSupplier(){
        return debtSupplier;
    }
    public void setDebtSupplier(double debtSupplier){
        this.debtSupplier = debtSupplier;
    }
    


    public String toString() {
        return "Supplier{" +
            "code='" + SupplierCode + '\'' +
            ", name='" + SupplierName + '\'' +
            ", telephone='" + telephone + '\'' +
            ", address='" + address + '\'' +
            ", debt=" + debtSupplier +
            '}';
}
    
}
