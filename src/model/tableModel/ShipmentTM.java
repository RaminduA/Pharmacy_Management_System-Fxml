package model.tableModel;

public class ShipmentTM {
    private String medicineCode;
    private String name;
    private String expiryDate;
    private double unitPrice;
    private int quantity;
    private double price;

    public ShipmentTM(){ }

    public ShipmentTM(String medicineCode, String name, String expiryDate, double unitPrice, int quantity, double price) {
        this.setMedicineCode(medicineCode);
        this.setName(name);
        this.setExpiryDate(expiryDate);
        this.setUnitPrice(unitPrice);
        this.setQuantity(quantity);
        this.setPrice(price);
    }

    public String getMedicineCode() {
        return medicineCode;
    }
    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ShipmentTM{" +
                "medicineCode='" + getMedicineCode() + '\'' +
                ", name='" + getName() + '\'' +
                ", expiryDate='" + getExpiryDate() + '\'' +
                ", unitPrice=" + getUnitPrice() +
                ", quantity=" + getQuantity() +
                ", price=" + getPrice() +
                '}';
    }
}
