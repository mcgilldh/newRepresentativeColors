package representativeColors;

/**
 *
 * This is a utility class for loading images and reference colors from the database.
 */
import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;

public class ImageProc {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private ResultSet colorSet = null;
    private Statement insertColorStatement;
    private String imageField;
    DebugUtil debugUtil;
    
    int count = 0;

    boolean debug;

    public ImageProc(String server, String database, String user, String password, String imageField, boolean debug, DebugUtil debugUtil) {
        this.debug = debug;
        this.debugUtil = debugUtil;
        connect(server, database, user, password);
        this.imageField = imageField;
    }

    /**
     * Connect to a database using the MySQL JDBC driver.
     * @param server
     * @param database
     * @param user
     * @param password
     */
    void connect(String server, String database, String user, String password) {
        debugUtil.debug("Connecting to the database.", debug);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectString = "jdbc:mysql://"+server+"/"+database+"?user="+user+"&password="+password;
            System.out.println("Trying to connect: " + connectString);
            connect = DriverManager.getConnection("jdbc:mysql://"+server+"/"+database+"?user="+user+"&password="+password);
            statement = connect.createStatement();
            insertColorStatement = connect.createStatement();
        }
        catch (ClassNotFoundException er ) {
            debugUtil.error("Couldn't load mysql jdbc driver. Is it installed?", debug, er);
            System.exit(1);
        }
        catch(SQLException er) {
            debugUtil.error("Could not connect to database " + database, debug, er);
            debugUtil.error(er.getMessage(), debug, er);
            System.exit(1);
        }
    }

    /**
     * Launches the query to fetch all images from the database.
     * @param table The table in which the images are stored.
     */
    public void fetchImages(String table, String startID, String endID) {
        debugUtil.debug("Fetching images.", debug);
        
        String queryString = "SELECT * FROM " + table + " WHERE Img_type_cd='FL' " +
                "AND vt_tracking BETWEEN '"+startID+"' AND '"+endID+"' ORDER BY img_id";
        System.out.println(queryString);
        
        try {
            //Where Textile_imd_id > startingImageId
            if (startID.length() == 0 || endID.length() == 0 || startID.equals(endID))
                resultSet = statement.executeQuery("SELECT * FROM " + table + " WHERE img_type_cd='FL' ORDER BY img_id");
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
     * Launches the query to fetch all images whose IDs are in a given list
     * @param table The table in which the images are stored.
     * @param idList The list of VT_TRACKING IDs whose images should be fetched.
     */
    public void fetchImageList(String table, LinkedList<String> idList) {
    	String queryString = "SELECT * FROM " + table + " WHERE img_type_cd='FL' AND ";
    	for (String id : idList) {
    		queryString += "vt_tracking='" + id + "' OR ";
    	}
    	queryString += "vt_tracking='X' ORDER BY img_id";
    	
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
                map.put(new ColorItem(colorSet.getInt("Color_detail_id"), new Color(red, green, blue)), 0);
                colorSet.next();
            }
            debugUtil.debug("Loaded " + map.keySet().size() + " colors from the database.", debug);
            return map;
        }
        catch(SQLException er) {
            debugUtil.error("Failed to load color table.", debug, er);
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
        }
        return null;
    }

    /**
     * Returns the next image from the resultSet as a BufferedImage for processing.
     * @return
     */
    public ImageItem nextImage() {
        if (resultSet != null) {
        	
            try {
                // Blob imageBlob = resultSet.getBlob(imageField);
                // ImageItem retval = new ImageItem(resultSet.getInt("Textile_img_id"),resultSet.getString("VT_tracking"),ImageIO.read(imageBlob.getBinaryStream(1, imageBlob.length())));
                // resultSet.next();
            	
            	String filename = resultSet.getString("img_path"); //.replace("VT_pix", "VT_thumb");
            	
            	System.out.println(filename);
            	BufferedImage image = ImageIO.read(new File(filename));
            	ImageItem retval = new ImageItem(resultSet.getInt("img_id"), resultSet.getString("vt_tracking"), image);
            	resultSet.next();
            	
                return retval;
            }
            catch (SQLException er) {
                debugUtil.error("Could not fetch next image from query results. Did you fetch the images first?", debug, er);
            } catch (IOException er) {
				//debugUtil.error("Unable to find image file on disk", debug, er);
				debugUtil.error(er.getMessage());
	
				try {
					resultSet.next();
					return null;
				} catch (SQLException e) {
	                debugUtil.error("Could not fetch next image from query results. Did you fetch the images first?", debug, er);	
				}
			}
            catch (Exception er) {
            	try {
					resultSet.next();
					return null;
				} catch (SQLException e) {
	                debugUtil.error("Could not fetch next image from query results. Did you fetch the images first?", debug, er);	
				}
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
                	queryString = "INSERT INTO textile_color_detail (textile_inst_id, color_detail_id) VALUES ('"+textile_inst_id+"', '"+colorItem.color_detail_id+"')\n";
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
