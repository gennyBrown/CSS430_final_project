/*
 * The file system maintains the file (structure) table shared among all user threads.
 * When a user thread opens a file, it follows these steps:
 *
 * (1) The user thread allocates a new entry of the user file descriptor table in its TCB.
 * This entry number itself becomes a file descriptor number.
 * he entry maintains a reference to a file (structure) table entry.
 *
 * (2) The user thread then requests the file system to allocate a new entry of the system-maintained file (structure) table.
 * This entry includes the seek pointer of this file, a reference to the inode corresponding to the file,
 * the inode number, the count to maintain #threads sharing this file (structure) table, and the access mode.
 * The seek pointer is set to the front or the tail of this file depending on the file access mode.

 * (3) The file system locates the corresponding inode and records it in this file (structure) table entry.
 *
 * (4) The user thread registers a reference to this file (structure) table entry in its file descriptor table entry of the TCB.
 *
 *
 * Created by Genny Brown
 * 12-15-2020
 * */
import java.util.*;


public class FileTable {
    public Vector <FileTableEntry> table;         // the actual entity of this file table
    private Directory dir;        // the root directory
    public byte tempArray[] = new byte[11];


    public FileTable( Directory directory ) { // constructor
        table = new Vector( );                // instantiate a file (structure) table
        dir = directory;                      // receive a reference to the Director
    }                                           // from the file system

    // major public methods
    
    //allocates a new FileTableEntry when a new file is opened
    public synchronized FileTableEntry falloc( String filename, String mode ) {
        //assume this is the first time this file is opened
        //create new iNode
        Inode iNode = new Inode();

        // allocate a new file (structure) table entry for this file name
        // allocate/retrieve and register the corresponding inode using dir
        FileTableEntry fileEntry = new FileTableEntry(iNode, dir.ialloc(filename), mode);

        // increment this inode's count
        iNode.fileTableCount++;

        // immediately write back this inode to the disk
        for(int i = 0; i < tempArray.length; i ++){
            tempArray[i] = (byte)iNode.directPtrs[i];
        }
        SysLib.rawwrite(dir.ialloc(filename), tempArray);

        //add to table structure
        table.add(fileEntry);

        // return a reference to this file (structure) table entry
        return fileEntry;
    }

    public synchronized boolean ffree( FileTableEntry e ) {
        for(int j = 0; j < table.size(); j++){
            table.get(j);
            if(table.get(j) == e){
                // receive a file table entry reference
                // save the corresponding inode to the disk
                for(int i = 0; i < e.inode.directPtrs.length; i++){
                    tempArray[i] = (byte)e.inode.directPtrs[i];
                }

                SysLib.rawwrite(e.iNumber, tempArray);

                // free this file table entry.
                table.remove(e);
                e.count--;
                // return true if this file table entry found in my table
                return true;
            }
            if (j == table.size() - 1 && table.get(j) != e){
                return false;
            }
        }
        return true;
    }

    public synchronized boolean fempty( ) {
        return table.isEmpty( );  // return if table is empty
    }                            // should be called before starting a format


    //returns the entry from a given inumber
    public FileTableEntry getEntry(short inumber){
       for(int i = 0; i < table.size(); i++){
           if(table.get(i).iNumber == inumber){
               return table.get(i);
           }
       }
       return null;
    }
}
