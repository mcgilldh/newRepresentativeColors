package representativeColors;

/**
 *
 * This is a utility class for loading images and reference colors from the database.
 */
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.omg.CORBA.portable.InputStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import com.jcraft.jsch.*;

public class ImageProc {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private ResultSet colorSet = null;
    private Statement insertColorStatement;
    private String imageField;
    private Session session;
    DebugUtil debugUtil;
    
    final static String imgHome = "/var/www";
    
    int count = 0;

    boolean debug;
    
   

    public ImageProc(String sshHost, String sshPort, String sshUser, String sshPassword, String dbServer, String dbPort, String dbName, String dbUser, String dbPassword, String imageField, boolean debug, DebugUtil debugUtil) throws SQLException, JSchException {
        this.debug = debug;
        this.debugUtil = debugUtil;
        connect(sshHost, sshPort, sshUser, sshPassword, dbServer, dbPort, dbName, dbUser, dbPassword);
        this.imageField = imageField;
    }
    
    
    
    
    public static abstract class MyUserInfo
    implements UserInfo, UIKeyboardInteractive{
public String getPassword(){ return null; }
public boolean promptYesNo(String str){ return false; }
public String getPassphrase(){ return null; }
public boolean promptPassphrase(String message){ return false; }
public boolean promptPassword(String message){ return false; }
public void showMessage(String message){ }
public String[] promptKeyboardInteractive(String destination,
                        String name,
                        String instruction,
                        String[] prompt,
                        boolean[] echo){
return null;
}
}

    

    

    
    
    /**
     * Connect to a database using the MySQL JDBC driver.
     * @param server
     * @param database
     * @param user
     * @param password
     * @throws JSchException 
     */
    void connect(String sshHost, String sshPort, String sshUser, String sshPassword, String dbServer, String dbPort, String database, String dbUser, String dbPassword) throws SQLException, JSchException {
        debugUtil.debug("Connecting to the database.", debug);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            JSch jsch = new JSch();

            String userName = sshUser;
            String host = sshHost;
            int port = Integer.parseInt(sshPort);
            
           
            if (!sshHost.isEmpty()) {
            	session = jsch.getSession(userName, host, port);
          

            	java.util.Properties config = new java.util.Properties();
            	config.put("StrictHostKeyChecking", "no");
	            session.setConfig(config);
	            
	            session.setPassword(sshPassword);
	            
	            session.connect();
	            System.out.println("session connected.....");
	            session.setPortForwardingL(9000, sshHost, Integer.parseInt(dbPort));
	            
	            
	            connect = DriverManager.getConnection("jdbc:mysql://"+dbServer+":9000/"+database+"?user="+dbUser+"&password="+dbPassword);
	            //connect = DriverManager.getConnection("jdbc:mysql://"+server+"/"+database+"?user="+user+"&password="+password);
	            statement = connect.createStatement();
	            insertColorStatement = connect.createStatement();
            }
            else {
            	 connect = DriverManager.getConnection("jdbc:mysql://"+dbServer+":"+Integer.parseInt(dbPort)+"/"+database+"?user="+dbUser+"&password="+dbPassword);
 	            //connect = DriverManager.getConnection("jdbc:mysql://"+server+"/"+database+"?user="+user+"&password="+password);
 	            statement = connect.createStatement();
 	            insertColorStatement = connect.createStatement();
            }
            
            
            
          
        }
        catch (ClassNotFoundException er ) {
            debugUtil.error("Couldn't load mysql jdbc driver. Is it installed?", debug, er);
            this.endConnection();
        }
     
    }
    
    
    /**
     * Fetch only vt tracking ID's list
     * @param table The table in which the images are stored.
     * @param startID the first ID in the range we're interested in
     * @param endID the final ID in the range we're interested in
     */
    public void fetchImageIds(String table, String startID, String endID) {
    	 debugUtil.debug("Fetching images.", debug);         
         try {
             //Where Textile_imd_id > startingImageId
             if (startID.length() == 0 || endID.length() == 0 || startID.equals(endID)) {
                 resultSet = statement.executeQuery("SELECT vt_tracking FROM " + table + " WHERE img_type_cd='FL' ORDER BY img_id");
             }
             else
                 resultSet = statement.executeQuery("SELECT vt_tracking FROM " + table + " WHERE Img_type_cd='FL' " +
                         "AND vt_tracking BETWEEN '"+startID+"' AND '"+endID+"' ORDER BY img_id");
             resultSet.first();
         }
         catch (SQLException er ) {
             debugUtil.error("Could not issue statements to the database.", debug, er);
             debugUtil.error(er.getMessage(), debug, er);
             //System.exit(1);
         }
    }
        
        

    /**
     * Launches the query to fetch all images from the database.
     * @param table The table in which the images are stored.
     * @param startID the first ID in the range we're interested in
     * @param endID the final ID in the range we're interested in
     */
    public void fetchImages(String table, String startID, String endID) {
        debugUtil.debug("Fetching images.", debug);
        
      
        
        try {
            //Where Textile_imd_id > startingImageId
            if (startID.length() == 0 || endID.length() == 0 || startID.equals(endID)) {
                resultSet = statement.executeQuery("SELECT * FROM " + table + " WHERE img_type_cd='FL' ORDER BY img_id");
            }
            else
                resultSet = statement.executeQuery("SELECT * FROM " + table + " WHERE Img_type_cd='FL' " +
                        "AND vt_tracking BETWEEN '"+startID+"' AND '"+endID+"' ORDER BY img_id");
            resultSet.first();
        }
        catch (SQLException er ) {
            debugUtil.error("Could not issue statements to the database.", debug, er);
            debugUtil.error(er.getMessage(), debug, er);
            //System.exit(1);
        }
    }
    
    /**
     *  Checks if there is another ID to display
     */
    public boolean hasNextImageID() {
        try {
            return !resultSet.isAfterLast();
        }
        catch (SQLException er) {
            if (debug) er.printStackTrace();
        }
        return false;
    }
    
    
    
    
    /**
     * Launches the query to fetch all images whose IDs are in a given list
     * @param table The table in which the images are stored.
     * @param idList The list of VT_TRACKING IDs whose images should be fetched.
     */
    public void fetchImageList(String table, LinkedList<String> idList) {
    	String queryString = "SELECT * FROM " + table + " WHERE img_type_cd='FL' AND (";
    	for (String id : idList) {
    		queryString += "vt_tracking='" + id + "' OR ";
    	}
    	queryString += "vt_tracking='X') ORDER BY img_id";
    	
    	try {
			resultSet = statement.executeQuery(queryString);
			resultSet.first();
		} catch (SQLException er) {
			debugUtil.error("Could not find list of images in database", debug, er);
		}
    	
    	
    }
    

    public HashMap<ColorItem, Integer> loadRefColors(String table) {
        try {
            colorSet = statement.executeQuery("SELECT * FROM " + table);
            colorSet.first();

            HashMap<ColorItem, Integer> map = new HashMap<>();
            while (!colorSet.isAfterLast()) {
                int red = colorSet.getInt("RGB_R");
                int green = colorSet.getInt("RGB_G");
                int blue = colorSet.getInt("RGB_B");
                map.put(new ColorItem(colorSet.getInt("color_detail_id"), new Color(red, green, blue)), 0);
                colorSet.next();
            }
            debugUtil.debug("Loaded " + map.keySet().size() + " colors from the database.", debug);
            return map;
        }
        catch(SQLException er) {
            debugUtil.error("Failed to load color table.", debug, er);
            debugUtil.error(er.getMessage());
        }
        return null;
    }

    public boolean hasNextImage() {
        try {
            return !resultSet.isAfterLast();
        }
        catch (SQLException er) {
            if (debug) er.printStackTrace();
        }
        return false;
    }
    
    
    /**
     * Returns the next image from the resultSet as a BufferedImage for processing.
     * @return string form of the next tracking ID
     * @throws SQLException 
     */
    public String nextImageID() throws SQLException {
        if (resultSet != null) {
        	//ChannelSftp sftpChannel;
        	
        	
        		
        		 /*Channel channel = session.openChannel("sftp");
                 channel.connect();
                 sftpChannel = (ChannelSftp) channel; */
        	
	           
	                // Blob imageBlob = resultSet.getBlob(imageField);
	                // ImageItem retval = new ImageItem(resultSet.getInt("Textile_img_id"),resultSet.getString("VT_tracking"),ImageIO.read(imageBlob.getBinaryStream(1, imageBlob.length())));
	                // resultSet.next();
	            	
	            	String trackingID = resultSet.getString("vt_tracking"); //.replace("VT_pix", "VT_thumb");
	            	
	            	
	            	
	                 
	                 /*File foreignFile = new File(filename);
	                 
	                 String path = foreignFile.getPath().replace(foreignFile.getName(), "");
	                 sftpChannel.cd(path);             
	                 
	                 System.out.println(foreignFile.getName());
	                 java.io.InputStream strm = sftpChannel.get(foreignFile.getName());  
	                 
	                 try {
	     				BufferedImage im = ImageIO.read(strm);
	     				System.out.println(im.getHeight());
	     			} catch (IOException e) {
	     				// TODO Auto-generated catch block
	     				System.out.println("File not found");
	     				
	     				//e.printStackTrace();
	     				//System.exit(0);
	     			}*/
	                 

	            	/*System.out.println(filename);
	            	BufferedImage image = ImageIO.read(new File(filename));
	            	ImageItem retval = new ImageItem(resultSet.getInt("img_id"), resultSet.getString("vt_tracking"), image);*/
	            	resultSet.next();
	            	
	                return trackingID;

           
        }
        else {
            debugUtil.error("Images were not fetched from the database before attempting to fetch the next image. Please contact developer.");
        }
        return null;
    }

    /**
     * Reads a single image from a file and creates an ImageItem.
     * @return
     */
    public ImageItem readImageFromFile(String filename) {
    	
        try {
        	
            BufferedImage image = ImageIO.read(new File(filename));
            String tracking = filename;
            
            
            
            
            
            if (filename.indexOf("-FL")>0) {
            	
            	tracking = new File(filename).getName();
            	tracking = tracking.substring(0,tracking.indexOf("-FL"));
            	
            }
            System.out.println(tracking);
            int imgId = getTextileImgID(tracking);
            if (imgId>0) {
            	count++;
            	System.out.println(count);
            	return new ImageItem(imgId, tracking, image);
            }	
            else
            	return null;
        }
        catch(IOException er ) {
            debugUtil.error("Unable to read image file.", debug, er);
            return null;
        }
        
    }
    
    
    public void endConnection() {
    	session.disconnect();
    }

    /**
     * Returns the next image from the resultSet as a BufferedImage for processing.
     * @return
     * @throws SQLException 
     */
    public ImageItem nextImage(int imgWidth, int imgHeight) throws SQLException {
        if (resultSet != null) {
        	ChannelSftp sftpChannel =null;
        	String filename = resultSet.getString("img_path"); //.replace("VT_pix", "VT_thumb");
        	
        	 File foreignFile = new File(filename);
             
             String path = imgHome + foreignFile.getPath().replace(foreignFile.getName(), "");
             path = path.replace("\\", "/");

             
        	
        	try {
        		Channel channel;
        		if (session!=null) {
        			channel = session.openChannel("sftp");
        		    channel.connect();
                    sftpChannel = (ChannelSftp) channel;
        		}
        		 
              
        	
	            try {
	                // Blob imageBlob = resultSet.getBlob(imageField);
	                // ImageItem retval = new ImageItem(resultSet.getInt("Textile_img_id"),resultSet.getString("VT_tracking"),ImageIO.read(imageBlob.getBinaryStream(1, imageBlob.length())));
	                // resultSet.next();
	            	
	            	
	            	
	            	
	                 
	                
	                 if (sftpChannel!=null) {
	                	 sftpChannel.cd(path);             
	                 
	                	 System.out.println(foreignFile.getName());
	                	 java.io.InputStream strm = sftpChannel.get(foreignFile.getName());  
	                 
	                 
	                 
	                 
	               
	                
	                 
	            	
	            	
	            	
	                	 System.out.println(filename);
	                	 BufferedImage image = ImageIO.read(strm);
	                	 
	                	 if (imgHeight>0 && imgWidth>0) {
	                		int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();

	                		 image = resizeImage(image, type, imgWidth, imgHeight);
	                	 }
	                	 
	                	 ImageItem retval = new ImageItem(resultSet.getInt("img_id"), resultSet.getString("vt_tracking"), image);
	                	 resultSet.next();
	            	
	                	 sftpChannel.exit();
	                	 return retval;
	                 }
	            
	            }
	            catch (IOException er) {
					//debugUtil.error("Unable to find image file on disk", debug, er);
					debugUtil.error(er.getMessage());
					debugUtil.error("IOException - disconnecting SCP Channel");
					//this.endConnection();
					
					resultSet.next();
					sftpChannel.exit();
					return null;
				
				} catch (SftpException e1) {
					// TODO Auto-generated catch block
					debugUtil.error("Could not find: " + path + foreignFile.getName());
					debugUtil.error(e1.getMessage());
					debugUtil.error("IOException - disconnecting SCP Channel");
					//this.endConnection();
					resultSet.next();
					sftpChannel.exit();
					return null;
					//e1.printStackTrace();
				} 
	            
            
        	} catch (JSchException e1) {
        		
				debugUtil.error("SSH connection error");
				e1.printStackTrace();
			} 
        	
        	if (session==null) {
				debugUtil.error("No SSH selected. Working locally");
        		ImageItem retval = readImageFromFile(foreignFile.getPath());
        		//resultSet.next();
        		return retval;
        	}
			
        	
            
            
           
        }
        else {
            debugUtil.error("Images were not fetched from the database before attempting to fetch the next image. Please contact developer.");
        }
        return null;
    }

    public void insertColorMatch(ImageItem imageItem, ColorItem colorItem, BufferedWriter out) {
        insertColorMatch(imageItem.textile_img_id, colorItem, out);
    }
    public void insertColorMatch(int textile_img_id, ColorItem colorItem, BufferedWriter out) {
        try {
        	String queryString = "SELECT textile_inst_ID FROM img_hdr, img_detail where img_id = "+ textile_img_id + " and img_hdr.img_hdr_id = img_detail.img_hdr_id";
        	System.out.println(queryString);
            ResultSet temp = insertColorStatement.executeQuery("SELECT textile_inst_ID FROM img_hdr, img_detail where img_id = "+ textile_img_id + " and img_hdr.img_hdr_id = img_detail.img_hdr_id");
            temp.first();
            int textile_inst_id = temp.getInt("textile_inst_id");

            if (out == null) {
            	queryString = "INSERT INTO textile_color_detail (textile_inst_id, color_detail_id) VALUES ('"+textile_inst_id+"', '"+colorItem.color_detail_id+"')";
            	System.out.println(queryString);
                insertColorStatement.execute(queryString);
            }
            else {
                try {
                	queryString = "INSERT INTO textile_color_detail (textile_inst_id, color_detail_id) VALUES ('"+textile_inst_id+"', '"+colorItem.color_detail_id+"');\n";
                	System.out.println(queryString);
                    out.write(queryString);
                }
                catch (IOException er ) {
                    debugUtil.error("Could not write to SQL script file.", debug, er);
                }
            }
        }
        catch (SQLException er) {
            debugUtil.error("Failed to insert color values into Textile Color Detail table.", debug, er);
            debugUtil.error(er.getMessage());
        }
    }

    public int getTextileImgID(String vtTracking) {
        try {
        	
            ResultSet temp = statement.executeQuery("SELECT img_id FROM img_detail WHERE vt_tracking='" + vtTracking + "'");
            
            temp.first();
            return temp.getInt("img_id");
        }
        catch (SQLException er) {
            debugUtil.error("Could not locate VTTracking id: "+ vtTracking, debug, er);
        }
        return -1;
    }
    
    private static BufferedImage resizeImage(BufferedImage originalImage, int type, int imgWidth, int imgHeight){
    	BufferedImage resizedImage = new BufferedImage(imgWidth, imgHeight, type);
    	Graphics2D g = resizedImage.createGraphics();
    	g.drawImage(originalImage, 0, 0, imgWidth, imgHeight, null);
    	g.dispose();
     
    	return resizedImage;
    }
}

class ColorItem {
    Color color;
    int color_detail_id;

    public ColorItem(int color_detail_id, Color color) {
        this.color = color;
        this.color_detail_id = color_detail_id;
    }

    public String toString() {
        return "(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
    }
}
