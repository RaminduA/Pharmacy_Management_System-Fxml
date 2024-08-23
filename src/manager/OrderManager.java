package manager;

import database.DatabaseConnection;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderManager {

    public String getOrderId() {
        try {
            PreparedStatement statement= DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT orderId FROM `Order` ORDER BY orderId DESC LIMIT 1");
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next()){
                int index=Integer.parseInt(resultSet.getString(1).split("-")[1]);
                if(index<9){
                    return "O-00000"+ ++index;
                }else if(index<99){
                    return "O-0000"+ ++index;
                }else if(index<999){
                    return "O-000"+ ++index;
                }else if(index<9999){
                    return "O-00"+ ++index;
                }else if(index<99999){
                    return "O-0"+ ++index;
                }else{
                    return "O-"+ ++index;
                }
            }else{
                return "O-000001";
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

    public Order getOrder(String orderId) {
        try {
            Connection connection=DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement=connection.prepareStatement("SELECT * FROM `Order` WHERE orderId=?");
            statement.setObject(1,orderId);
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next()){
                return new Order(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDouble(5),
                        new OrderManager().getAllOrderDetails(resultSet.getString(1))
                );
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ArrayList<Order> getAllOrders(){
        try {
            Connection connection=DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement=connection.prepareStatement("SELECT * FROM `Order`");
            ResultSet resultSet=statement.executeQuery();
            ArrayList<Order> orders=new ArrayList<>();
            while(resultSet.next()){
                orders.add(new Order(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4),
                                resultSet.getDouble(5),
                                getAllOrderDetails(resultSet.getString(1))
                        )
                );
            }
            return orders;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    public OrderDetail getOrderDetail(String orderId, String medCode) {
        try {
            PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Order Detail` WHERE orderId=? AND medicineCode=?");
            statement.setObject(1,orderId);
            statement.setObject(2,medCode);
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next()){
                return new OrderDetail(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getInt(4),
                        resultSet.getDouble(5)
                );
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ArrayList<OrderDetail> getAllOrderDetails(String orderId) {
        try {
            PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Order Detail` WHERE orderId=?");
            statement.setObject(1,orderId);
            ResultSet resultSet=statement.executeQuery();
            ArrayList<OrderDetail> orderDetails=new ArrayList<>();
            while(resultSet.next()){
                orderDetails.add(new OrderDetail(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getDouble(3),
                                resultSet.getInt(4),
                                resultSet.getDouble(5)
                        )
                );
            }
            return orderDetails;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean placeOrder(Order order){
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isOrderAdded(order,connection) && isOrderDetailAdded(order,connection) && isMedicineUpdated(order,connection)){
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

    private boolean isOrderAdded(Order order, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("INSERT INTO `Order` VALUES(?,?,?,?,?)");
            statement.setObject(1, order.getOrderId());
            statement.setObject(2, order.getCustomerId());
            statement.setObject(3, order.getOrderDate());
            statement.setObject(4, order.getTime());
            statement.setObject(5, order.getCost());
            return statement.executeUpdate()>0;
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isOrderDetailAdded(Order order, Connection connection) {
        try{
            ArrayList<OrderDetail> detailList=order.getDetailList();
            int affectedRows=0;
            for(OrderDetail temp : detailList) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO `Order Detail` VALUES(?,?,?,?,?)");
                statement.setObject(1, temp.getOrderId());
                statement.setObject(2, temp.getMedicineCode());
                statement.setObject(3, temp.getUnitPrice());
                statement.setObject(4, temp.getQuantity());
                statement.setObject(5, temp.getPrice());
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

    private boolean isMedicineUpdated(Order order, Connection connection) {
        try {
            ArrayList<OrderDetail> detailList=order.getDetailList();
            int affectedRows=0;
            for(OrderDetail temp : detailList) {
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

    private boolean isMedicineUpdated(OrderDetail oldDetail, OrderDetail newDetail, Connection connection) {
        try {
            if(oldDetail.getMedicineCode().equals(newDetail.getMedicineCode())){
                PreparedStatement statement=connection.prepareStatement("UPDATE Medicine SET qtyOnHand=? WHERE medicineCode=?");
                Medicine medicine=new MedicineManager().getMedicine(newDetail.getMedicineCode());
                int newQuantity=medicine.getQtyOnHand()-newDetail.getQuantity()+oldDetail.getQuantity();
                statement.setObject(2, newDetail.getMedicineCode());
                statement.setObject(1, newQuantity);
                return statement.executeUpdate() > 0;
            }else{
                PreparedStatement statement1 = connection.prepareStatement("UPDATE Medicine SET qtyOnHand=? WHERE medicineCode=?");
                Medicine medicine1 = new MedicineManager().getMedicine(oldDetail.getMedicineCode());
                int newQuantity1 = medicine1.getQtyOnHand() + oldDetail.getQuantity();
                statement1.setObject(2, oldDetail.getMedicineCode());
                statement1.setObject(1, newQuantity1);

                PreparedStatement statement2 = connection.prepareStatement("UPDATE Medicine SET qtyOnHand=? WHERE medicineCode=?");
                Medicine medicine2 = new MedicineManager().getMedicine(newDetail.getMedicineCode());
                int newQuantity2 = medicine2.getQtyOnHand() - newDetail.getQuantity();
                statement2.setObject(2, newDetail.getMedicineCode());
                statement2.setObject(1, newQuantity2);

                return (statement1.executeUpdate() > 0 && statement2.executeUpdate() > 0);
            }
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean updateOrder(Order order) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isOrderUpdated(order,connection) && isOrderDetailUpdated(order,connection)){
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

    private boolean isOrderDetailUpdated(Order order, Connection connection) {
        try{
            ArrayList<OrderDetail> detailList=order.getDetailList();
            int affectedRows=0;
            for(OrderDetail temp : detailList) {
                PreparedStatement statement=connection.prepareStatement("UPDATE `Order Detail` SET  unitPrice=?, quantity=?, price=? WHERE orderId=? AND medicineCode=?");
                statement.setObject(4, temp.getOrderId());
                statement.setObject(5, temp.getMedicineCode());
                statement.setObject(1, temp.getUnitPrice());
                statement.setObject(2, temp.getQuantity());
                statement.setObject(3, temp.getPrice());
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

    private boolean isOrderDetailUpdated(OrderDetail oldDetail, OrderDetail newDetail, Connection connection) {
        try{
            PreparedStatement statement=connection.prepareStatement("UPDATE `Order Detail` SET  medicineCode=?, unitPrice=?, quantity=?, price=? WHERE orderId=? AND medicineCode=?");
            statement.setObject(5, oldDetail.getOrderId());
            statement.setObject(6, oldDetail.getMedicineCode());
            statement.setObject(1, newDetail.getMedicineCode());
            statement.setObject(2, newDetail.getUnitPrice());
            statement.setObject(3, newDetail.getQuantity());
            statement.setObject(4, newDetail.getPrice());
            return statement.executeUpdate()>0;
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isOrderUpdated(Order order, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("UPDATE `Order` SET customerId=?, orderDate=?, time=?, cost=? WHERE orderId=?");
            statement.setObject(5, order.getOrderId());
            statement.setObject(1, order.getCustomerId());
            statement.setObject(2, order.getOrderDate());
            statement.setObject(3, order.getTime());
            statement.setObject(4, order.getCost());
            return statement.executeUpdate()>0;
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isOrderUpdated(OrderDetail detail, Connection connection) {
        try {
            Order order=getOrder(detail.getOrderId());
            double cost=order.getCost()-detail.getPrice();
            PreparedStatement statement=connection.prepareStatement("UPDATE `Order` SET cost=? WHERE orderId=?");
            statement.setObject(2, order.getOrderId());
            statement.setObject(1, cost);
            return statement.executeUpdate()>0;
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean updateOrderDetail(Order order, OrderDetail oldDetail, OrderDetail newDetail) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isOrderUpdated(order,connection) && isOrderDetailUpdated(order,connection) && isOrderDetailUpdated(oldDetail,newDetail,connection) && isMedicineUpdated(oldDetail,newDetail,connection)){
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

    public boolean deleteOrder(Order order) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isOrderDeleted(order,connection) && isMedicineReplaced(order,connection)){
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

    private boolean isOrderDeleted(Order order, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("DELETE FROM `Order` WHERE orderId=?");
            statement.setObject(1, order.getOrderId());
            return statement.executeUpdate()>0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean isMedicineReplaced(Order order, Connection connection) {
        try {
            ArrayList<OrderDetail> detailList=order.getDetailList();
            int affectedRows=0;
            for(OrderDetail temp : detailList) {
                PreparedStatement statement=connection.prepareStatement("UPDATE Medicine SET qtyOnHand=? WHERE medicineCode=?");
                Medicine medicine=new MedicineManager().getMedicine(temp.getMedicineCode());
                int newQuantity=medicine.getQtyOnHand()+temp.getQuantity();
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

    private boolean isMedicineReplaced(OrderDetail detail, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("UPDATE Medicine SET qtyOnHand=? WHERE medicineCode=?");
            Medicine medicine=new MedicineManager().getMedicine(detail.getMedicineCode());
            int newQuantity=medicine.getQtyOnHand()+detail.getQuantity();
            statement.setObject(2,detail.getMedicineCode());
            statement.setObject(1,newQuantity);
            return statement.executeUpdate()>0;
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean deleteOrderDetail(OrderDetail detail) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isOrderUpdated(detail,connection) && isOrderDetailDeleted(detail,connection) && isMedicineReplaced(detail,connection)){
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

    private boolean isOrderDetailDeleted(OrderDetail detail, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("DELETE FROM `Order Detail` WHERE orderId=? AND medicineCode=?");
            statement.setObject(1, detail.getOrderId());
            statement.setObject(2, detail.getMedicineCode());
            return statement.executeUpdate()>0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
