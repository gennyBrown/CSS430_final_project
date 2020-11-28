
public class Directory {
    private static int maxChars = 30; // max characters of each file name

    // Directory entries
    private int fsize[];        // each element stores a different file size.
    private char fnames[][];    // each element stores a different file name.

    // directory constructor
    public Directory( int maxInumber ) {
        // maxInumber = max files
        fsizes = new int[maxInumber];

        // all file size initialized to 0
        for ( int i = 0; i < maxInumber; i++ ){
            fsize[i] = 0;
        }

        //declare the size of hte 2D array
        fnames = new char[maxInumber][maxChars];

        // entry(inode) 0 is "/"
        String root = "/";
        // fsize[0] is the size of "/".
        fsize[0] = root.length( );
        // fnames[0] includes "/"
        root.getChars( 0, fsizes[0], fnames[0], 0 );
    }

    public int bytes2directory( byte data[] ) {
        // assumes data[] received directory information from disk
        // initializes the Directory instance with this data[]
    }

    public byte[] directory2bytes( ) {
        // converts and return Directory information into a plain byte array
        // this byte array will be written back to disk
        // note: only meaningfull directory information should be converted
        // into bytes.
    }

    public short ialloc( String filename ) {
        // filename is the one of a file to be created.
        // allocates a new inode number for this filename
    }

    public boolean ifree( short iNumber ) {
        // deallocates this inumber (inode number)
        // the corresponding file will be deleted.
    }

    public short namei( String filename ) {
        // returns the inumber corresponding to this filename
    }
}
