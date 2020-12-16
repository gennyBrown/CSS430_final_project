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

    // marked as type int? If the system makes this call would it expect an int return? 
    boolean format( int files ){
        /*
        * Formats the disk (Disk.java’s data contents).
        * The files parameter specifies the maximum number of files to be created
        * (The number of inodes to be allocated) in your file system.
        * The return value is 0 on success, and -1 otherwise.
        * */

       superblock.format(files);
        //call deallocAllBlock(fileEntry)
       return false;
    }

    /*
     * Opens the file specified by the fileName string in the given mode
     * (where "r" = ready only, "w" = write only, "w+" = read/write, "a" = append).
     * The call allocates a new file descriptor, fd to this file.
     * The file is created if it does not exist in the mode "w", "w+" or "a".
     * SysLib.open must return a negative number as an error value if the file does not exist in the mode "r".
     * */
    synchronized FileTableEntry open( String filename, String mode ){
        //validate
        if(!(mode == "r" || mode == "w"|| mode == "w+" || mode== "a")){
            return null;
        }
        //open in read
        if(mode == "r"){
            //if already open
          if(directory.filenameInumberMap.containsKey(filename) == true){
              //check mode open in
              //if same mode
              if(filetable.getEntry(directory.namei(filename)).mode == mode){
                  // increment count
                  filetable.getEntry(directory.namei(filename)).count++;

                  //return reference to entry
                  return filetable.getEntry(directory.namei(filename));
              }
              //if different
              if(filetable.getEntry(directory.namei(filename)).mode != mode){
                  //create new entry, connect to same inode
                  FileTableEntry newEnt
                          = new FileTableEntry(filetable.getEntry(directory.namei(filename)).inode,
                          directory.namei(filename), mode);

                  //return reference to entry
                  return newEnt;
              }
          }
            //if not open
          if(directory.filenameInumberMap.containsKey(filename) == false){

              //return error
              return null;
          }

        }else if(mode == "w" || mode == "w+" || mode == "a"){

            //check mode open in
            //if same mode
            if(filetable.getEntry(directory.namei(filename)).mode == mode){
                // increment count
                filetable.getEntry(directory.namei(filename)).count++;

                //return reference to entry
                return filetable.getEntry(directory.namei(filename));
            }
            //if different
            if(filetable.getEntry(directory.namei(filename)).mode != mode){
                //create new entry, connect to same inode
                FileTableEntry newEnt
                        = new FileTableEntry(filetable.getEntry(directory.namei(filename)).inode,
                        directory.namei(filename), mode);

                //return reference to entry
                return newEnt;
            }
        }
        if ((filetable.getEntry(directory.namei(filename)).mode != mode)){
            return filetable.falloc(filename, mode);
        }
        return null;
    }

    // marked as an int? need to change type to synchronized or call sync()?
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
            if(filetable.table.contains(ftEnt) ==  true){

                //if threads are using entry
                if(ftEnt.count > 0){
                    //decrement count
                    ftEnt.count--;

                    //entry closed
                    return true;

                //if no threads are using the entry
                } else if(ftEnt.count == 0){
                    //can't decrement count
                    return false;
                }
            }
        }
        return false;
    }

    int fsize( FileTableEntry ftEnt ){
        //get length from inode
        return ftEnt.inode.fileLength;
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
        // ***check status of node***
        // unused or delete exit
        // should not be able to delete while reading
        // should not be able to read an unused node
        if (ftEnt.inode.statusFlag == 0 || ftEnt.inode.statusFlag == 4){  
            return -1;  
        }
        // iNode flags unused (= 0), used(= 1), read(= 2), write(= 3), delete(= 4)
        int retRead = 0;    // return value of bytes read
        // read starting at the position currently pointed to by the seek pointer
        int offset = ftEnt.seekPtr / Disk.blockSize;
        // block number
        int blockNum = ftEnt.inode.findTargetBlock(offset);
        // find remaining file size
        int remainBytes = Disk.blockSize - offset;
        // compare amount 'buffer.length' to the size of the (file size - offset)
        int toRead = remainBytes < buffer.length ? remainBytes : buffer.length;    // number of bytes to read from file

        // can it read while it writes? Assuming it can up to the point the request is made
        // if flag is set to read will this stop writing calls? (only multi-threading?)
        ftEnt.inode.statusFlag = 2;     // set to read
        // create block for buffer
        byte[] iBlock = new byte[Disk.blockSize];
        // blockNum will need Inode seekPtr from ftEnt. Assuming seekPtr is > 0
        // offset < ftEnt.inode.directPtrs.length ? ftEnt.inode.directPtrs[offset] : indirect pointer
        // int blockNum = ftEnt.inode.directPtrs[offset];
        //while (retRead < toRead){
            // make sure read is valid
            if (SysLib.rawread(blockNum, iBlock) == -1) {
                return -1;
            }
            // TODO: check for data in other blocks? 

            retRead = toRead;
            // increments the seek pointer by the number of bytes to have been read
            seek(ftEnt, retRead, SEEK_CUR);
        //}
        //decrement count of threads at that file table entry
        if(ftEnt.count > 0){
            ftEnt.count--;
        }
        // return flag to used
        ftEnt.inode.statusFlag = 1;
    //}
    sync();
    return retRead;
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
        if (ftEnt == null){
            return -1;
        }
        // check status flag for validity
        // iNode flags unused (= 0), used(= 1), read(= 2), write(= 3), delete(= 4)
        if (ftEnt.inode.statusFlag == 4){   // can't write to a deleted node
            return -1;
        }
        int retBytes = 0;   // return number of bytes written
        // write starting at the position currently pointed to by the seek pointer
        int offset = ftEnt.seekPtr / Disk.blockSize;
        // block number
        int blockNum = ftEnt.inode.findTargetBlock(offset);
        // find remaining file size
        int remainBytes = Disk.blockSize - offset;
        // compare amount 'buffer.length' to the size of the (file size - offset)
        int toWrite = remainBytes < buffer.length ? remainBytes : buffer.length;    // number of bytes to read from file
        // set to write
        ftEnt.inode.statusFlag = 3;     
        // create block for buffer
        byte[] iBlock = new byte[Disk.blockSize];
        // assumming writing cannot happen unless prompted by other flags

        //SysLib.rawread(blockNum, iBlock);
        SysLib.rawwrite(blockNum, iBlock);
        seek(ftEnt, toWrite, SEEK_CUR);
        if (ftEnt.count > 0){
            ftEnt.count--;
        }
        ftEnt.inode.statusFlag = 1;

        return retBytes;
    }

    private boolean dealllocAllBlocks( FileTableEntry ftEnt ){
        //deallocate all blocks associated with the given entry
        //not all blocks in table

        for(int i = 0; i < ftEnt.inode.directPtrs.length; i++){
            ftEnt.inode.directPtrs[i] = -1;
            ftEnt.inode.indirectPtr = -1;
        }

        SysLib.format(ftEnt.count);
        //call format(4)
        return true;
    }

    boolean delete( String filename ){
        //check file count
        //if not 0 return false
        //will fail if file is still open by another thread
        //blocks will need to be deallocated

        //get entry and check count
        //if more than 0, can't delete
        if(filetable.getEntry(directory.namei(filename)).count > 0){
            return false;
        } else if( filetable.getEntry(directory.namei(filename)).count == 0){
            dealllocAllBlocks(filetable.getEntry(directory.namei(filename)));   //deallocate blocks
            filetable.ffree(filetable.getEntry(directory.namei(filename)));     //free the entry
            directory.ifree(directory.namei(filename));                     //free the inumber
            return true;
        }
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
            if(ftEnt.seekPtr > ftEnt.inode.fileLength){
                ftEnt.seekPtr = ftEnt.inode.fileLength;
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
            if(ftEnt.seekPtr > ftEnt.inode.fileLength){
                ftEnt.seekPtr = ftEnt.inode.fileLength;
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
            if(ftEnt.seekPtr > ftEnt.inode.fileLength){
                ftEnt.seekPtr = ftEnt.inode.fileLength;
                return ftEnt.seekPtr;
            }
            return  ftEnt.seekPtr;
        }
        return offset;
    }
}// end class


