package manager;

import database.DatabaseConnection;
import model.Employee;
import model.Medicine;
import model.PriceDetail;
import model.UserDetail;
import model.tableModel.IncomeTM;
import util.DateTimeUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MedicineManager {

    public boolean addMedicine(Medicine medicine) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isMedicineAdded(medicine,connection) && isPriceDetailAdded(medicine,connection)){
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

    public boolean updateMedicine(Medicine medicine) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isMedicineUpdated(medicine,connection) && isPriceDetailAdded(medicine,connection)){
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

    public boolean deleteMedicine(String medCode) {
        try {
            PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("DELETE FROM Medicine WHERE medicineCode=?");
            statement.setObject(1,medCode);
            return statement.executeUpdate()>0;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isPriceDetailAdded(Medicine m, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("INSERT INTO `Price Detail` VALUES (?,?,?,?,?,?)");
            statement.setObject(1,m.getCode());
            statement.setObject(2,m.getName());
            statement.setObject(3,DateTimeUtil.currentDate(1));
            statement.setObject(4,DateTimeUtil.currentTime(1));
            statement.setObject(5,m.getUnitPrice());
            statement.setObject(6,m.getUnitSalePrice());
            return statement.executeUpdate()>0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isMedicineAdded(Medicine m, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("INSERT INTO Medicine VALUES (?,?,?,?,?,?,?)");
            statement.setObject(1,m.getCode());
            statement.setObject(2,m.getName());
            statement.setObject(3,m.getDescription());
            statement.setObject(4,m.getPreservedTime());
            statement.setObject(5,m.getQtyOnHand());
            statement.setObject(6,m.getUnitPrice());
            statement.setObject(7,m.getUnitSalePrice());
            return statement.executeUpdate()>0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isMedicineUpdated(Medicine m, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("UPDATE Medicine SET name=?, description=?, preservedTime=?, qtyOnHand=?, unitPrice=?, unitSalePrice=? WHERE medicineCode=?");
            statement.setObject(7,m.getCode());
            statement.setObject(1,m.getName());
            statement.setObject(2,m.getDescription());
            statement.setObject(3,m.getPreservedTime());
            statement.setObject(4,m.getQtyOnHand());
            statement.setObject(5,m.getUnitPrice());
            statement.setObject(6,m.getUnitSalePrice());
            return statement.executeUpdate()>0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public String getMedicineCode() {
        try {
            PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT medicineCode FROM Medicine ORDER BY medicineCode DESC LIMIT 1");
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next()){
                int index=Integer.parseInt(resultSet.getString(1).split("-")[1]);
                if(index<9){
                    return "M-0000"+ ++index;
                }else if(index<99){
                    return "M-000"+ ++index;
                }else if(index<999){
                    return "M-00"+ ++index;
                }else if(index<9999){
                    return "M-0"+ ++index;
                }else{
                    return "M-"+ ++index;
                }
            }else{
                return "M-00001";
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

    public ArrayList<Medicine> getAllMedicine() {
        try {
            PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Medicine");
            ResultSet resultSet=statement.executeQuery();
            ArrayList<Medicine> medicineList=new ArrayList<>();
            while(resultSet.next()){
                medicineList.add(new Medicine(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4),
                                resultSet.getInt(5),
                                resultSet.getDouble(6),
                                resultSet.getDouble(7)
                        )
                );
            }
            return medicineList;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getAllMedicineCodes() {
        try {
            PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT medicineCode FROM Medicine");
            ResultSet resultSet=statement.executeQuery();
            ArrayList<String> medicineList=new ArrayList<>();
            while(resultSet.next()){
                medicineList.add(resultSet.getString(1));
            }
            return medicineList;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Medicine getMedicine(String medCode) {
        try {
            PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Medicine WHERE medicineCode=?");
            statement.setObject(1,medCode);
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next()){
                return new Medicine(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getInt(5),
                        resultSet.getDouble(6),
                        resultSet.getDouble(7)
                );
            }else{
                return null;
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    public PriceDetail getPriceDetail(Medicine medicine, String date, String time) {
        try {
            PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Price Detail` ORDER BY date DESC");
            ResultSet resultSet=statement.executeQuery();
            ArrayList<PriceDetail> detailList=new ArrayList<>();
            while(resultSet.next()){
                if(resultSet.getString(1).equals(medicine.getCode()) && resultSet.getString(2).equals(medicine.getName())){
                    detailList.add(new PriceDetail(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getDouble(5),
                            resultSet.getDouble(6)
                    ));
                }
            }
            for(PriceDetail pd: detailList) {
                SimpleDateFormat sdfDate=new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdfTime=new SimpleDateFormat("hh:mm:ss a");
                Date givenDate=sdfDate.parse(date);
                Date detailDate=sdfDate.parse(pd.getDate());
                Date givenTime=sdfTime.parse(time);
                Date detailTime=sdfTime.parse(pd.getTime());
                if(givenDate.compareTo(detailDate)>0){
                    return pd;
                }else if(givenDate.compareTo(detailDate)==0){
                    if(givenTime.compareTo(detailTime)>=0){
                        return pd;
                    }
                }
                int last=detailList.size()-1;
                return detailList.get(last);
            }
        } catch (SQLException | ClassNotFoundException | ParseException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ArrayList<IncomeTM> getAllIncomes() {
        try {
            PreparedStatement statement= DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT stockDate,time,(-cost) FROM Stock UNION SELECT orderDate,time,cost FROM `Order` ORDER BY stockDate");
            ResultSet resultSet=statement.executeQuery();
            ArrayList<IncomeTM> incomeTMs=new ArrayList<>();
            while(resultSet.next()){
                incomeTMs.add(new IncomeTM(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getDouble(3)
                        )
                );
            }
            return incomeTMs;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }
}
