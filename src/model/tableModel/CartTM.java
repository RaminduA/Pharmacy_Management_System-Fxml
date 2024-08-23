package model.tableModel;

public class CartTM {
    private String medicineCode;
    private String name;
    private double unitPrice;
    private int quantity;
    private double price;

    public CartTM() { }

    public CartTM(String medicineCode, String name, double unitPrice, int quantity, double price) {
        this.setMedicineCode(medicineCode);
        this.setName(name);
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
        return "CartTM{" +
                "medicineCode='" + getMedicineCode() + '\'' +
                ", name='" + getName() + '\'' +
                ", unitPrice=" + getUnitPrice() +
                ", quantity=" + getQuantity() +
                ", price=" + getPrice() +
                '}';
    }
}
