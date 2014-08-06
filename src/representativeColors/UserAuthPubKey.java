package representativeColors;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class UserAuthPubKey {
    public static void main(String[] arg) {
        try {
            JSch jsch = new JSch();

            String user = "jonathan";
            String host = "mcgill01.virtual-textile.com";
            int port = 22;
            String privateKey = "/Users/jonathaneidelman/.ssh/id_rsa";

            jsch.addIdentity(privateKey);
            System.out.println("identity added ");

            Session session = jsch.getSession(user, host, port);
            System.out.println("session created.");

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            session.setPassword("o2boe.j3am");
            
            session.connect();
            System.out.println("session connected.....");

            session.disconnect();

           

            

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}