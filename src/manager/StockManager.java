package manager;

import database.DatabaseConnection;
import model.Medicine;
import model.Stock;
import model.StockDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockManager {
    public String getBatchNumber(){
        try {
            PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT batchNo FROM Stock ORDER BY batchNo DESC LIMIT 1");
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next()){
                int index=Integer.parseInt(resultSet.getString(1).split("-")[1]);
                if(index<9){
                    return "B-0000"+ ++index;
                }else if(index<99){
                    return "B-000"+ ++index;
                }else if(index<999){
                    return "B-00"+ ++index;
                }else if(index<9999){
                    return "B-0"+ ++index;
                }else{
                    return "B-"+ ++index;
                }
            }else{
                return "B-00001";
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

    public Stock getStock(String batchNo) {
        try {
            Connection connection=DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement=connection.prepareStatement("SELECT * FROM Stock WHERE batchNo=?");
            statement.setObject(1,batchNo);
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next()){
                return new Stock(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDouble(5),
                        new StockManager().getAllStockDetails(resultSet.getString(1))
                );
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ArrayList<Stock> getAllStocks(){
        try {
            Connection connection=DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement=connection.prepareStatement("SELECT * FROM Stock");
            ResultSet resultSet=statement.executeQuery();
            ArrayList<Stock> stocks=new ArrayList<>();
            while(resultSet.next()){
                stocks.add(new Stock(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4),
                                resultSet.getDouble(5),
                                new StockManager().getAllStockDetails(resultSet.getString(1))
                        )
                );
            }
            return stocks;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    public StockDetail getStockDetail(String batchNo, String medCode) {
        try {
            PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Stock Detail` WHERE batchNo=? AND medicineCode=?");
            statement.setObject(1,batchNo);
            statement.setObject(2,medCode);
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next()){
                return new StockDetail(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5),
                        resultSet.getDouble(6)
                );
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ArrayList<StockDetail> getAllStockDetails(String batchNo) {
        try {
            PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Stock Detail` WHERE batchNo=?");
            statement.setObject(1,batchNo);
            ResultSet resultSet=statement.executeQuery();
            ArrayList<StockDetail> stockDetails=new ArrayList<>();
            while(resultSet.next()){
                stockDetails.add(new StockDetail(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getDouble(4),
                                resultSet.getInt(5),
                                resultSet.getDouble(6)
                        )
                );
            }
            return stockDetails;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean placeShipment(Stock stock) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isStockAdded(stock,connection) && isStockDetailAdded(stock,connection) && isMedicineUpdated(stock,connection)){
                connection.commit();
                return true;
            }else{
                connection.rollback();
                return false;
            }
        }catch(SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }finally{
            try{
                assert connection != null;
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    private boolean isStockAdded(Stock stock, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("INSERT INTO Stock VALUES(?,?,?,?,?)");
            statement.setObject(1, stock.getBatchNo());
            statement.setObject(2, stock.getSupplierId());
            statement.setObject(3, stock.getShipmentDate());
            statement.setObject(4, stock.getTime());
            statement.setObject(5, stock.getCost());
            return statement.executeUpdate()>0;
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isStockDetailAdded(Stock stock, Connection connection) {
        try{
            ArrayList<StockDetail> detailList=stock.getDetailList();
            int affectedRows=0;
            for(StockDetail temp : detailList) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO `Stock Detail` VALUES(?,?,?,?,?,?)");
                statement.setObject(1, temp.getBatchNo());
                statement.setObject(2, temp.getMedicineCode());
                statement.setObject(3, temp.getExpiryDate());
                statement.setObject(4, temp.getUnitPrice());
                statement.setObject(5, temp.getQuantity());
                statement.setObject(6, temp.getPrice());
                if(statement.executeUpdate()>0){
                    affectedRows++;
                }else{
                    return false;
                }
            }
            return detailList.size()==affectedRows;
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isMedicineUpdated(Stock stock, Connection connection) {
        try {
            ArrayList<StockDetail> detailList=stock.getDetailList();
            int affectedRows=0;
            for(StockDetail temp : detailList) {
                PreparedStatement statement=connection.prepareStatement("UPDATE Medicine SET qtyOnHand=? WHERE medicineCode=?");
                Medicine medicine=new MedicineManager().getMedicine(temp.getMedicineCode());
                int newQuantity=temp.getQuantity()+medicine.getQtyOnHand();
                statement.setObject(2,temp.getMedicineCode());
                statement.setObject(1,newQuantity);
                if(statement.executeUpdate()>0){
                    affectedRows++;
                }else{
                    return false;
                }
            }
            return detailList.size()==affectedRows;
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isMedicineUpdated(StockDetail oldDetail, StockDetail newDetail, Connection connection) {
        try {
            if(oldDetail.getMedicineCode().equals(newDetail.getMedicineCode())){
                PreparedStatement statement=connection.prepareStatement("UPDATE Medicine SET qtyOnHand=? WHERE medicineCode=?");
                Medicine medicine=new MedicineManager().getMedicine(newDetail.getMedicineCode());
                int newQuantity=medicine.getQtyOnHand()+newDetail.getQuantity()-oldDetail.getQuantity();
                statement.setObject(2, newDetail.getMedicineCode());
                statement.setObject(1, newQuantity);
                return statement.executeUpdate() > 0;
            }else{
                PreparedStatement statement1 = connection.prepareStatement("UPDATE Medicine SET qtyOnHand=? WHERE medicineCode=?");
                Medicine medicine1 = new MedicineManager().getMedicine(oldDetail.getMedicineCode());
                int newQuantity1 = medicine1.getQtyOnHand() - oldDetail.getQuantity();
                statement1.setObject(2, oldDetail.getMedicineCode());
                statement1.setObject(1, newQuantity1);

                PreparedStatement statement2 = connection.prepareStatement("UPDATE Medicine SET qtyOnHand=? WHERE medicineCode=?");
                Medicine medicine2 = new MedicineManager().getMedicine(newDetail.getMedicineCode());
                int newQuantity2 = medicine2.getQtyOnHand() + newDetail.getQuantity();
                statement2.setObject(2, newDetail.getMedicineCode());
                statement2.setObject(1, newQuantity2);

                return (statement1.executeUpdate() > 0 && statement2.executeUpdate() > 0);
            }
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean updateStock(Stock stock) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isStockUpdated(stock,connection) && isStockDetailUpdated(stock,connection)){
                connection.commit();
                return true;
            }else{
                connection.rollback();
                return false;
            }
        }catch(SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }finally{
            try{
                assert connection != null;
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    private boolean isStockDetailUpdated(Stock stock, Connection connection) {
        try{
            ArrayList<StockDetail> detailList=stock.getDetailList();
            int affectedRows=0;
            for(StockDetail temp : detailList) {
                PreparedStatement statement=connection.prepareStatement("UPDATE `Stock Detail` SET  expiryDate=?, unitPrice=?, quantity=?, price=? WHERE batchNo=? AND medicineCode=?");
                statement.setObject(5, temp.getBatchNo());
                statement.setObject(6, temp.getMedicineCode());
                statement.setObject(1, temp.getExpiryDate());
                statement.setObject(2, temp.getUnitPrice());
                statement.setObject(3, temp.getQuantity());
                statement.setObject(4, temp.getPrice());
                if(statement.executeUpdate()>0){
                    affectedRows++;
                }else{
                    return false;
                }
            }
            return detailList.size()==affectedRows;
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isStockDetailUpdated(StockDetail oldDetail, StockDetail newDetail, Connection connection) {
        try{
            PreparedStatement statement=connection.prepareStatement("UPDATE `Stock Detail` SET  medicineCode=?, expiryDate=?, unitPrice=?, quantity=?, price=? WHERE batchNo=? AND medicineCode=?");
            statement.setObject(6, oldDetail.getBatchNo());
            statement.setObject(7, oldDetail.getMedicineCode());
            statement.setObject(1, newDetail.getMedicineCode());
            statement.setObject(2, newDetail.getExpiryDate());
            statement.setObject(3, newDetail.getUnitPrice());
            statement.setObject(4, newDetail.getQuantity());
            statement.setObject(5, newDetail.getPrice());
            return statement.executeUpdate()>0;
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isStockUpdated(StockDetail detail, Connection connection) {
        try {
            Stock stock=getStock(detail.getBatchNo());
            double cost=stock.getCost()-detail.getPrice();
            PreparedStatement statement=connection.prepareStatement("UPDATE Stock SET cost=? WHERE batchNo=?");
            statement.setObject(2, stock.getBatchNo());
            statement.setObject(1, cost);
            return statement.executeUpdate()>0;
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isStockUpdated(Stock stock, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("UPDATE Stock SET supplierId=?, stockDate=?, time=?, cost=? WHERE batchNo=?");
            statement.setObject(5, stock.getBatchNo());
            statement.setObject(1, stock.getSupplierId());
            statement.setObject(2, stock.getShipmentDate());
            statement.setObject(3, stock.getTime());
            statement.setObject(4, stock.getCost());
            return statement.executeUpdate()>0;
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean updateStockDetail(Stock stock, StockDetail oldDetail, StockDetail newDetail) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isStockUpdated(stock,connection) && isStockDetailUpdated(stock,connection) && isStockDetailUpdated(oldDetail,newDetail,connection) && isMedicineUpdated(oldDetail,newDetail,connection)){
                connection.commit();
                return true;
            }else{
                connection.rollback();
                return false;
            }
        }catch(SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }finally{
            try{
                assert connection != null;
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    public boolean deleteStock(Stock stock) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isStockDeleted(stock,connection) && isMedicineRemoved(stock,connection)){
                connection.commit();
                return true;
            }else{
                connection.rollback();
                return false;
            }
        }catch(SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }finally{
            try{
                assert connection != null;
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    private boolean isMedicineRemoved(StockDetail detail, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("UPDATE Medicine SET qtyOnHand=? WHERE medicineCode=?");
            Medicine medicine=new MedicineManager().getMedicine(detail.getMedicineCode());
            int newQuantity=medicine.getQtyOnHand()-detail.getQuantity();
            statement.setObject(2,detail.getMedicineCode());
            statement.setObject(1,newQuantity);
            return statement.executeUpdate()>0;
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isMedicineRemoved(Stock stock, Connection connection) {
        try {
            ArrayList<StockDetail> detailList=stock.getDetailList();
            int affectedRows=0;
            for(StockDetail temp : detailList) {
                PreparedStatement statement=connection.prepareStatement("UPDATE Medicine SET qtyOnHand=? WHERE medicineCode=?");
                Medicine medicine=new MedicineManager().getMedicine(temp.getMedicineCode());
                int newQuantity=medicine.getQtyOnHand()-temp.getQuantity();
                statement.setObject(2,temp.getMedicineCode());
                statement.setObject(1,newQuantity);
                if(statement.executeUpdate()>0){
                    affectedRows++;
                }else{
                    return false;
                }
            }
            return detailList.size()==affectedRows;
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isStockDeleted(Stock stock, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("DELETE FROM Stock WHERE batchNo=?");
            statement.setObject(1, stock.getBatchNo());
            return statement.executeUpdate()>0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean deleteStockDetail(StockDetail detail) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isStockUpdated(detail,connection) && isStockDetailDeleted(detail,connection) && isMedicineRemoved(detail,connection)){
                connection.commit();
                return true;
            }else{
                connection.rollback();
                return false;
            }
        }catch(SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }finally{
            try{
                assert connection != null;
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    private boolean isStockDetailDeleted(StockDetail detail, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("DELETE FROM `Stock Detail` WHERE batchNo=? AND medicineCode=?");
            statement.setObject(1, detail.getBatchNo());
            statement.setObject(2, detail.getMedicineCode());
            return statement.executeUpdate()>0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
