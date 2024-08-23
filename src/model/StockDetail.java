package model;

public class StockDetail {
    private String batchNo;
    private String medicineCode;
    private String expiryDate;
    private double unitPrice;
    private int quantity;
    private double price;

    public StockDetail() {  }

    public StockDetail(String batchNo, String medicineCode, String expiryDate, double unitPrice, int quantity, double price) {
        this.setBatchNo(batchNo);
        this.setMedicineCode(medicineCode);
        this.setExpiryDate(expiryDate);
        this.setUnitPrice(unitPrice);
        this.setQuantity(quantity);
        this.setPrice(price);
    }

    public String getBatchNo() {
        return batchNo;
    }
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getMedicineCode() {
        return medicineCode;
    }
    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
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
        return "StockDetail{" +
                "batchNo='" + getBatchNo() + '\'' +
                ", medicineCode='" + getMedicineCode() + '\'' +
                ", expiryDate='" + getExpiryDate() + '\'' +
                ", unitPrice=" + getUnitPrice() +
                ", quantity=" + getQuantity() +
                ", price=" + getPrice() +
                '}';
    }
}
