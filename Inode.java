/**
 * started after SuperBlock
 * each Inode describes 1 file
 *
 * (1) the length of the corresponding file
 * (2) the number of file (structure) table entries that point to this inode
 * (3) the flag to indicate if it is unused (= 0), used(= 1),
 * read(= 2), write(= 3), delete(= 4), what/how the file is currently being used for).
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
 * (2) Create a Vector<Inode> object that maintains all inode on memory, is shared among all threads, 
 * and is exclusively access by each thread.
 *
 * Edits by: Jonathan Young 12-9-2020
 *
 *
 * */
public class Inode {
    private final static int iNodeSize = 32;                // fix to 32 bytes
    private final static int directSizePtr = 11;            // # direct pointers

    public int fileLength;                                  // file size in bytes
    public short fileTableCount;                            // # file-table entries pointing to this
    public short statusFlag;                                // 0 = unused, 1 = used,
    public short directPtrs[] = new short[directSizePtr];   // direct pointers
    public short indirectPtr;                               // indirect pointer, points to an index block with 256 indexes (all shorts)
    // 11 direct pointers and 1 indirect pointer. Indirect pointer points to an index block which includes 256 indexes = 11 * 512B + 256 * 512B
    // 5632B + 131,072B = 136,704B (Max size of an iNode)

    // a default constructor
    Inode( ) {
        fileLength = 0;
        fileTableCount = 0;
        statusFlag = 1;       // flag is initialized to used
        for ( int i = 0; i < directSizePtr; i++ )
            directPtrs[i] = -1;
        indirectPtr = -1;
    }

    // retrieving inode from disk
    Inode( short iNumber ) {
        /**
         * You will need a constructor that retrieves an existing inode from the disk into the memory.
         * Given an inode number, termed inumber,
         * this constructor reads the corresponding disk block, locates the corresponding inode information in that block,
         * and initializes a new inode with this information.
         * */
        int blockNum = (iNumber / 16) + 1;  // 16 iNodes stored in one block, picks the next block
        // create offset, which will read 32 bytes. Mod by 16 for each iNode in a block
        int offset = (iNumber % 16) * 32;
        byte[] iBlock = new byte[Disk.blockSize];   // new block by apple
        SysLib.rawread(blockNum, iBlock);   // read from that block
        // convert iBlock to set fileLength, fileTableCount, and statusFlag
        fileLength = SysLib.bytes2int(iBlock, offset);  // sets file length based on offset (32 bytes)
        fileTableCount = SysLib.bytes2short(iBlock, offset + 4);   // fileTable spaced by fileLength offset (4 bytes)
        statusFlag  = SysLib.bytes2short(iBlock, offset + 2);     // statusFlag spaced by fileTableCount offset (2 bytes)
        
        for ( int i = 0; i < directSizePtr; i++ ){ // loop through pointers by pointer size offset (2 bytes)
            offset += 2;
            directPtrs[i] = SysLib.bytes2short(iBlock, offset);
        }
        indirectPtr = SysLib.bytes2short(iBlock, offset + 2);   // space for indirect pointer offset (2 bytes)

    }

    // save to disk as the i-th inode
    int toDisk( short iNumber ) {
        int blockNum = (iNumber / 16) + 1;  // 16 iNodes stored in one block, picks the next block
        int offset = iNodeSize * iNumber;   // offset based off size of inode (32B) spaced by the argument iNumber     
        byte[] iBlock = new byte[Disk.blockSize];   // new block by apple
        SysLib.rawread(blockNum, iBlock);   // read from that block
        // short2byte(short to convert, byte array, offset destination)
        SysLib.short2byte(fileLength, iBlock, offset);
        SysLib.short2byte(fileTableCount, iBlock, offset + 4);  // fileTable spaced by fileLength offset (4 bytes)
        SysLib.short2byte(statusFlag, iBlock, offset + 2);  // statusFlag spaced by fileTableCount offset (2 bytes)
        // loop for each pointer by pointer size offset (2 bytes)
        for (int i = 0; i < directSizePtr; i++){
            offset += 2;
            SysLib.short2byte(directPtrs[i], iBlock, offset);
        }
        SysLib.short2byte(indirectPtr, iBlock, offset + 2); // space for indirect pointer offset (2 bytes)
        SysLib.rawwrite(blockNum, iBlock);  // write to disk
    }
}
