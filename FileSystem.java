
public class FileSystem{
    private SuperBlock superblock;
    private Directory directory;
    private FileTable filetable;

    public FileSystem( int diskBlocks ){
        // create superblock, and format disk with 64 inodes in default
        superblock = new SuperBlock( diskBlocks ) ;

        // create directory, ard register "/" in directory entry 0
        directory = new Directory(superblock.inodeBlocks);

        // table is created, and store directory in the table
        filetable = new FileTable( directory ) ;

        // directory reconstruction
        FileTableEntry dirEnt = open ("/", " r");
        int dirSize = fsize( dirEnt ) ;
        if ( dirSize > 0 ){
            byte[] dirData  = new byte[dirSize];
            read( dirEnt, dirData);
            directory.bytes2directory(dirData);
        }
        close(dirEnt);
    }

    void sync( ){

    }

    boolean format( int files ){

    }

    FileTableEntry open( String filename, String mode ){
    //hash table
        //hash filename to get info
        //filename index

    //look if file table to see if open
    //if not open\
    //if so get inumber, point to the inode
    }

    boolean close( FileTableEntry ftEnt ){
    //check set to see if open
        //check inode count

    }

    int fsize( FileTableEntry ftEnt ){

    }

    int read( FileTableEntry ftEnt, byte[] buffer ){

    }

    int write (FileTableEntry ftEnt, byte[] buffer ){
        //write to inode directpoint
        //write in order
        //22 bytes per pointer
        //if over 22 need to write to the next direct pointer
    }

    private boolean dealllocAllBlocks( FileTableEntry ftEnt ){

    }

    boolean delete( String filename ){

    }

    private final int SEEK_SET = 0;
    private final int SEEK_CUR = 1;
    private final int SEEK_END = 2;

    int seek( FileTableEntry ftEnt, int offset, int whence) {
        /*
        if whence==SEEK_SET
            move pointer offset
        if whence==SEEK_CUR
            move pointer offset
        if whence==SEEK_END
            move pointer offset or EOF
            append to EOF

            */
    }
}// end class


