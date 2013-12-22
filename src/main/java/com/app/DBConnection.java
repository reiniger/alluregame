package com.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    public static void main(String[] args) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        String url = "jdbc:postgresql://ec2-54-235-174-213.compute-1.amazonaws.com:5432/dc60kdeggqi3rm";
        
        Properties props = new Properties();
		props.setProperty("user","mutlscurujleoz");
		props.setProperty("password","3VJ9yFjOvdJRY2T3ZMdFnvBD64");
		props.setProperty("ssl","true");
		props.setProperty("sslfactory","org.postgresql.ssl.NonValidatingFactory");
		
		Statement stmt = null;

        try {
			con = DriverManager.getConnection(url, props);
            
            stmt = con.createStatement();
            
            //creating row in Players
            //stmt.executeUpdate("insert into " + "\"PLAYER\" " + "values('Benja', 1, false, '{dsa, sdb}', 1, 1)");
            
            //updating data 
            String query = "update \"PLAYER\" set \"isRiddler\" = false where \"Nickname\" = 'Tonven'";
            //String query = "update \"PLAYER\" set \"ID\" = 2 where \"Nickname\" = 'Tonven'";
            stmt.executeUpdate(query);
            
            //retrieving row's data
            /*		
            String query = "select \"Nickname\", \"isRiddler\", \"Score\" from \"PLAYER\"";
            rs = stmt.executeQuery(query);
            while(rs.next()){
            	System.out.println(rs.getString("Nickname") + " " + rs.getBoolean("isRiddler"));
            }
            */
            
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBConnection.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
                if (stmt != null) {
                	stmt.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DBConnection.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
}