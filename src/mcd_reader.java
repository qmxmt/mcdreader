import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.text.TextWindow;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

/* Queen Mary MCD file reader: David Mills 2013 */

public class mcd_reader implements PlugIn {
	public void run(String arg) {
		OpenDialog od = new OpenDialog("MCD File Reader", null);
		String name = od.getFileName();
		if (name==null)
			return;
		String dir = od.getDirectory();
                MCDOpener mcd = new MCDOpener();
                int status = mcd.read(dir+name);
                StringBuilder sb = new StringBuilder();
                sb.append(" ");
                sb.append(mcd.width);
                sb.append(" ");
                sb.append(mcd.height);
                sb.append(" ");
                sb.append(mcd.zsize);
                sb.append(" ");
                sb.append(mcd.num_samples);
                sb.append(" ");
                sb.append(mcd.num_proj);
                sb.append(" ");
                sb.append(mcd.num_blocks);
                sb.append(" ");
                sb.append(mcd.num_slices);
                String data = sb.toString();
                new TextWindow("Data for " +name, "wibble", data, 450, 500);
                 
	}

}

class MCDOpener {

    /**
     * File read status: No errors.
     */
    public static final int STATUS_OK = 0;

    /**
     * File read status: Error decoding file (may be partially decoded)
     */
    public static final int STATUS_FORMAT_ERROR = 1;

    /**
     * File read status: Unable to open source.
     */
    public static final int STATUS_OPEN_ERROR = 2;

	protected BufferedInputStream in;
        protected int status;
        protected short width;            // full image width
        protected short height;           // full image height
	protected short zsize;            // Z height
	protected short num_samples;	 
	protected short num_proj;
	protected short num_blocks;
	protected short num_slices;
	protected byte[] header = new byte[512]; 
        protected int rubbish;
        protected Vector frames;
   
    
     /**
     * Reads MCD file from specified source (file or URL string)
     *
     * @param name File source string
     * @return int read status code
     */
    public int read(String name) {
        status = STATUS_OK;
        try {
            name = name.trim();
            if (name.indexOf("://") > 0) {
                URL url = new URL(name);
                in = new BufferedInputStream(url.openStream());
            } else {
                in = new BufferedInputStream(new FileInputStream(name));
            }
            status = read(in);
        } catch (IOException e) {
            status = STATUS_OPEN_ERROR;
        }

        return status;
    }
        
        
     /**
     * Reads MCD image from stream
     *
     * @param BufferedInputStream containing GIF file.
     * @return int read status code
     */
    public int read(BufferedInputStream is) {
        init();
        if (is != null) {
            in = is;
            readHeader();
            if (!err()) {
               // readContents();
               // if (frameCount < 0)
                    status = STATUS_FORMAT_ERROR;
            }
        } else {
            status = STATUS_OPEN_ERROR;
        }
        try {
            is.close();
        } catch (IOException e) {}
        return status;
    }  
        
        
        
        

    
   // StringBuilder sb = new StringBuilder();
    //sb.append(" ");
    //sb.append(width);
    //sb.append(" ");
    //sb.append(height);
    //sb.append(" ");
    //sb.append(zsize);
    //sb.append(" ");
    //sb.append(num_samples);
    //sb.append(" ");
    //sb.append(num_proj);
    //sb.append(" ");
    //sb.append(num_blocks);
    //sb.append(" ");
    //sb.append(numslices);
    //sb.append(" ");

    //String data = sb.toString();
    //new TextWindow("Data for " +name, "wibble", data, 450, 500);
    

   /**
     * Returns true if an error was encountered during reading/decoding
     */
    protected boolean err() {
        return status != STATUS_OK;
    }

    /**
     * Initializes or re-initializes reader
     */
    protected void init() {
         status = STATUS_OK;
        num_proj = 0;
        frames = new Vector();
        //gct = null;
        //lct = null;
    }
    
     /**
     * Reads a single byte from the input stream.
     */
    protected int read() {
        int curByte = 0;
        try {
            curByte = in.read();
        } catch (IOException e) {
            status = STATUS_FORMAT_ERROR;
        }
        return curByte;
    }
     
     
     /**
     * Reads next 16-bit value, LSB first
     */
    protected short readshort() {
        // read 16-bit value, LSB first
        return (short) (read() | (read() << 8));
    }	
    
	
	/**
     * Reads MCD file header information.

    short xsize,ysize,zsize,lmarg,rmarg,tmarg,bmarg,tzmarg,bzmarg,\
     num_samples,num_proj,num_blocks,num_slices,bin,gain,speed,pepper,issue,num_frames,spare_int[13];
    float scale,offset,voltage,current,thickness,pixel_size,distance,exposure,\
     mag_factor,gradient,spare_float[2];
	long posdistance,slices_per_block,z,theta;
    char time[26],duration[12],owner[21],user[5],specimen[32],scan[32],\
     comment[64],spare_char[192];

     */
    protected void readHeader() {
        short junk;
		width =  readshort();
		height = readshort();
		zsize = readshort();
		junk = readshort();	//lmarg
		junk = readshort();	//rmarg
		junk = readshort();	//tmarg
		junk = readshort();	//bmarg
		junk = readshort();	//tzmarg
		junk = readshort();	//bzmarg
		num_samples =  readshort();
		num_proj = readshort();
		num_blocks = readshort();
		num_slices = readshort();
		
    }
	
}
