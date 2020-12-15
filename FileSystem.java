import java.util.*;

public class FileSystem{
    private Superblock superblock;
    private Directory directory;
    private FileTable filetable;
    Set<String> openFiles = new HashSet<String>();

    public FileSystem( int diskBlocks ){
        // create superblock, and format disk with 64 inodes in default
        superblock = new Superblock( diskBlocks ) ;

        // create directory, ard register "/" in directory entry 0
        directory = new Directory(superblock.totalInodes);

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
        //iterate through table to write all inodes to disk
            //write all inodes to the drive
        //do not delete inodes
        SysLib.sync();

    }

  /* boolean format( int files ){
        /*
        * Formats the disk (Disk.java’s data contents).
        * The files parameter specifies the maximum number of files to be created
        * (The number of inodes to be allocated) in your file system.
        * The return value is 0 on success, and -1 otherwise.
        * */

        //call deallocAllBlock(fileEntry)
     //   return false;
    //}

    FileTableEntry open( String filename, String mode ){
        //Use set to capture filenames
        //first deal with mode
        //look if set to see if open

        /*
        * Opens the file specified by the fileName string in the given mode
        * (where "r" = ready only, "w" = write only, "w+" = read/write, "a" = append).
        * The call allocates a new file descriptor, fd to this file.
        * The file is created if it does not exist in the mode "w", "w+" or "a".
        * SysLib.open must return a negative number as an error value if the file does not exist in the mode "r".
        * */




        //open in read
        if(mode == "r"){
            //if already open
          if(directory.filenameInumberMap.containsKey(filename) == true){
              //get inumber from map
              short tempInumber = directory.filenameInumberMap.get(filename);

              //create filetableentry to load found entry into
              FileTableEntry fileEnt = new FileTableEntry(null, (short)0, null);

              //find in table and check mode

              for(int i = 0; i < filetable.table.size(); i++){
                  filetable.table.get(i);
                  //if inumber is found in the table
                  if(){
                      //connect the new entry to the same inode
                  }
              }
              //FileTableEntry fileEnt = new FileTableEntry(, directory.filenameInumberMap.get(filename), mode);

          }

        }else if(mode == "w" || mode == "w+" || mode == "a"){

        }

        /*
        for(int i = 0; i < filetable.table.size(); i++){
            if(directory.filenameInumberMap ==  filename){

            }

        }
        */

        // if not open
        //if so get inumber, point to the inode

        //add to the set of open files

        //before adding the hashmap, see if already in map
        for(int i = 0; i < directory.filenameInumberMap.size(); i++){
            //if already in map, don't add again
            if(directory.filenameInumberMap.containsKey(filename) == true){

            }
            //if get to the end and still not in map, add
            if(i == directory.filenameInumberMap.size() - 1
                    && directory.filenameInumberMap.containsKey(filename) == true){

                openFiles.add(filename);
                return filetable.falloc(filename, mode);
            }


        }
        return null;
    }
//************************************ CLOSE IS NOT FINISHED ***********************
    boolean close( FileTableEntry ftEnt ){
        //removes the entry from the file descriptor table in tCB
        //decrements count in entry

        /* Closes the file corresponding to fd, commits all file transactions on this file
        Unregisters fd from the user file descriptor table of the calling thread's TCB.
        The return value is 0 in success, otherwise -1.
        * */


       //check set to see if open
        for(int i= 0; i< filetable.table.size(); i++){
            //if the entry is in the table, it's open
            if(filetable.table.indexOf(ftEnt) == i ){
                //check inode count
                if(ftEnt.count > 0){
                    //if not 0, can't close
                    return false;
                    //if == 0 delete
                } else if(ftEnt.count == 0){
                    /*
                     * need to map the ftEnt to the filename
                     * delete(filename);
                     *
                     */

                    return true;
                }

            }
        }
        return false;
    }

    int fsize( FileTableEntry ftEnt ){
        //get length from inode
        return ftEnt.inode.length;
    }

    int read( FileTableEntry ftEnt, byte[] buffer ){
        /*
        Reads up to buffer.length bytes from the file indicated by the file descriptor fd,
        starting at the position currently pointed to by the seek pointer.

        If bytes remaining between the current seek pointer
        and the end of file are less than buffer.length:

        SysLib.read reads as many bytes as possible and puts them into the beginning of buffer.

                It increments the seek pointer by the number of bytes to have been read.

                The return value is the number of bytes that have been read, or a negative value upon an error.
        */


        return 0;
    }

    int write (FileTableEntry ftEnt, byte[] buffer ){
        //write to inode directpoint
        //use syslib.write or rawwrite (research the right one)
        //write in order
        //22 bytes per pointer
        //if over 22 need to write to the next direct pointer


        /*
        * Writes the contents of buffer to the file indicated by fd
        * starting at the position indicated by the seek pointer.
        * The operation may overwrite existing data in the file
        * and/or append to the end of the file.
        * SysLib.write increments the seek pointer
        * by the number of bytes to have been written.
        * The return value is the number of bytes
        *  that have been written, or a negative value upon an error.
        * */

        return 0;
    }

    private boolean dealllocAllBlocks( FileTableEntry ftEnt ){
        //deallocate all blocks associated with the given entry
        //not all blocks in table

        SysLib.format(ftEnt.count);
        //call format(4)
        return true;
    }

    boolean delete( String filename ){
        //check file count
        //if not 0 return false
        //will fail if file is still open by another thread
        //blocks will need to be deallocated

        //get iNumber for filename
        //call dir.ifree(iNumber);
        return false;
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

        pointer is located in the file table entry, add offset to move
            */
        /**
         * Updates the seek pointer corresponding to fd as follows:
         *
         * If whence is SEEK_SET (= 0),
         *      the file's seek pointer is set to offset bytes from the beginning of the file
         *
         * If whence is SEEK_CUR (= 1),
         *      the file's seek pointer is set to its current value plus the offset.
         *      The offset can be positive or negative.
         *
         * If whence is SEEK_END (= 2),
         *      the file's seek pointer is set to the size of the file plus the offset.
         *      The offset can be positive or negative.
         *
         * If the user attempts to set the seek pointer to a negative number
         *      you must clamp it to zero and the return must be successful.
         *
         * If the user attempts to set the pointer to beyond the file size,
         *      you must set the seek pointer to the end of the file and the return must be successful.
         *
         *  The offset location of the seek pointer in the file is returned from the call to seek.
         * */

        //If whence is SEEK_SET (= 0),
        if(whence == SEEK_SET) {
            //seek pointer is set to offset
            ftEnt.seekPtr = offset;

            //if user tries to go out of bounds of the file, move seekPtr to appropiate bound
            if(ftEnt.seekPtr < 0){
                ftEnt.seekPtr = 0;
                return ftEnt.seekPtr;
            }
            if(ftEnt.seekPtr > ftEnt.inode.length){
                ftEnt.seekPtr = ftEnt.inode.length;
                return ftEnt.seekPtr;
            }
            return ftEnt.seekPtr;

        //If whence is SEEK_CUR (= 1),
        } else if(whence == SEEK_CUR) {
            //seek pointer is set to its current value plus the offset
            //The offset can be positive or negative
            ftEnt.seekPtr += offset;

            //if user tries to go out of bounds of the file, move seekPtr to appropiate bound
            if(ftEnt.seekPtr < 0){
                ftEnt.seekPtr = 0;
                return ftEnt.seekPtr;
            }
            if(ftEnt.seekPtr > ftEnt.inode.length){
                ftEnt.seekPtr = ftEnt.inode.length;
                return ftEnt.seekPtr;
            }

            return  ftEnt.seekPtr;

        //If whence is SEEK_END (= 2),
            //the file's seek pointer is set to the size of the file plus the offset.
            //The offset can be positive or negative.
        } else if(whence == SEEK_END) {
            //the file's seek pointer is set to the size of the file plus the offset.
            //The offset can be positive or negative.
            ftEnt.seekPtr += offset;

            //if user tries to go out of bounds of the file, move seekPtr to appropiate bound
            if(ftEnt.seekPtr < 0){
                ftEnt.seekPtr = 0;
                return ftEnt.seekPtr;
            }
            if(ftEnt.seekPtr > ftEnt.inode.length){
                ftEnt.seekPtr = ftEnt.inode.length;
                return ftEnt.seekPtr;
            }
            return  ftEnt.seekPtr;
        }
        return offset;
    }
}// end class


