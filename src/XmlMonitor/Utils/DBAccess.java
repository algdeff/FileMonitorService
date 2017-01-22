package XmlMonitor.Utils;

import XmlMonitor.Logic.db.DatabaseManager;
import XmlMonitor.Publisher.Interfaces.IListener;
import XmlMonitor.Publisher.Interfaces.IPublisherEvent;
import XmlMonitor.Publisher.Publisher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Deprecated
public final class DBAccess implements IListener {

    private static volatile DBAccess instance;
    private DatabaseManager _worker = null;

    private DBAccess() {
        _worker = new DatabaseManager();
        _worker.init();
        registerOnPublisher();
    }

    public static synchronized DBAccess getInstance() {
        if (instance == null) {
            instance = new DBAccess();
        }
        return instance;
    }

    public void registerOnPublisher() {
        Publisher.getInstance().registerNewListener(this, "db");
    }
    public String[] listenerInterests() {
        return new String[] {
                "db",
                "A",
                "444"
        };
    }
    public void listenerHandler(IPublisherEvent publisherEvent) {
        //checkListnReg();
        System.out.println("dbAcc.Event: " + publisherEvent.getName() + publisherEvent.getBody().toString());
    }

    public void checkListnReg() {
        Boolean reg = Publisher.getInstance().isRegistered(this);
        //System.out.println("dbAcc.Publisher REG new listener" + reg);
    }

    public Boolean exeQuery(String query) {
        Boolean success = false;

        try {
            _worker.getStatement().executeQuery(query);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public ResultSet readQuery(String table, String name) {
        //String ins_query = "INSERT INTO reg_users (name, email) VALUES ('kiso', current_time())";
        String sel_query = "SELECT * FROM reg_users";
        String value = "";
        ResultSet rs = null;

        try {
            //st.execute(sel_query);
            rs = _worker.getStatement().executeQuery(sel_query);
//            while (rs.next()) {
//                String userid = rs.getString("id");
//                String username = rs.getString("name");
//                String email = rs.getString("email");
//                System.out.println(userid + username + email);
//            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private RegUserData createRegUserData(ResultSet rs) throws SQLException {
        RegUserData result = new RegUserData();
        result.setRegUserData(rs.getString("name"), rs.getString("pass"), rs.getString("email"));
        return result;
    }

    public RegUserData getUserByName(String name) {
        RegUserData result = null;
        try {
            PreparedStatement ps = _worker.getPreparedStatement("SELECT * FROM reg_users WHERE name=?");
            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                result =  createRegUserData(rs);
            }
            ps.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
        } catch ( Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public Boolean updateRegUserData(RegUserData userdata) {
        Boolean success = false;
        try
        {
            PreparedStatement ps;
            if ( userdata.getId() > 0 )
            {
                ps = _worker.getPreparedStatement("UPDATE reg_users SET name=?, pass=?, email=? WHERE id=?");
                ps.setInt(4, userdata.getId());
            }
            else
            {
                ps = _worker.getPreparedStatement("INSERT INTO reg_users (name, pass, email) VALUES (?,?,?)");
            }

            ps.setString( 1, userdata.getName() );
            ps.setString( 2, userdata.getPass() );
            ps.setString( 3, userdata.getEmail() );
            ps.executeUpdate();
            ps.close();
            success = true;
        }
        catch( SQLException e )
        {
            e.printStackTrace();
        }

        return success;
    }

}
