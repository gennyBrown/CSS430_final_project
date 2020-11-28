/**
 * started after SuperBlock
 * each Inode describes 1 file
 *
 * (1) the length of the corresponding file
 * (2) the number of file (structure) table entries that point to this inode
 * (3) the flag to indicate if it is unused (= 0), used(= 1),
 * or in some other status (= 2, 3, 4, ..., i.e., what/how the file is currently being used for).
 *
 * Note that 16 inodes can be stored in one block.
 *
 *
 *
 * The system must avoid any inode inconsistency among different user threads.
 * There are two ways of maintaining the inode consistency:
 *
 * (1) Before an inode in memory is updated, check the corresponding inode on disk,
 * read it from the disk if the disk has been updated by another thread.
 * Then, write back its contents to disk immediately.
 * Note that the inode data to be written back include int length, short count, short flag, short direct[11],
 * and short indirect, thus requiring a space of 32 bytes in total.
 * For this write-back operation, you will need the toDisk method
 * that saves this inode information to the iNumber-th inode in the disk,
 * where iNumber is given as an argument.
 *
 * (2) Create a Vector<Inode> object that maintains all inode on memory, is shared among all threads, and is exclusively access by each thread.
 *
 *
 *
 *
 * */
public class Inode {
    private final static int iNodeSize = 32;                // fix to 32 bytes
    private final static int directSizePtr = 11;            // # direct pointers

    public int fileLength;                                  // file size in bytes
    public short fileTableCount;                            // # file-table entries pointing to this
    public short statusFlag;                                // 0 = unused, 1 = used, ... MORE THAN JUST 0 AND 1 (SEE ABOVE)
    public short directPtrs[] = new short[directSize];      // direct pointers
    public short indirectPtr;                               // a indirect pointer

    // a default constructor
    Inode( ) {
        length = 0;
        count = 0;
        flag = 1;
        for ( int i = 0; i < directSizePtr; i++ )
            directPtrs[i] = -1;
        indirectPtr = -1;
    }

    // retrieving inode from disk
    Inode( short iNumber ) {
        // design it by yourself.
        /**
         * You will need a constructor that retrieves an existing inode from the disk into the memory.
         * Given an inode number, termed inumber,
         * this constructor reads the corresponding disk block, locates the corresponding inode information in that block,
         * and initializes a new inode with this information.
         * */
    }

    // save to disk as the i-th inode
    int toDisk( short iNumber ) {
        // design it by yourself.
    }
}
