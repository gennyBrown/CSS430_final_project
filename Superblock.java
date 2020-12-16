/**
 * managed by the OS
 * no user thread should have access
 *
 *
 * Note: the data in the disk is in bytes, so you will need to translate to integers back and forth as needed.
 * For this purpose, SysLib provides the functions bytes2int and int2bytes.
 *
 * For example, in SuperBlock above, totalBlocks is an integer which can be obtained as:
 * totalBlocks = SysLib.bytes2int(superBlock, offset);
 *
 * here superBlock is a byte array of 512 (the maximum disk block size).
 * Similarly, int2bytes, is used to calculate offsets in bytes for Free List management.
 * 
 * Edits by: Jonathan Young 12-7-2020
 * */
public class Superblock {

    private final int defaultInodeBlocks = 64;

    public int totalBlocks;   //number of disk blocks
    public int totalInodes;   //number of inodes
    public int freeList;      //the block number of the free list's head

    //constructor
    public SuperBlock(int diskSize){
        // read the superblock from disk and store as byte array of 512(max disk block size)
        byte[] superBlock = new byte[Disk.blockSize];
        SysLib.rawread(0, superBlock);
        // convert the bytes to ints from the superblock storage
        totalBlocks = SysLib.bytes2int(superBlock, 0);  // blocks 0 holds superBlock and full block info
        totalInodes = SysLib.bytes2int(superBlock, 4);  // size of each iNode block, creates spacing of 4
        freeList = SysLib.bytes2int(superBlock, 8);     // freeList = 5 (block#0 = super, blocks#1,2,3, and 4 = inodes.)

        if (totalBlocks == diskSize && totalInodes > 0 && freeList >= 2){
            // disk contents are valid
            return;
        } else {
            // need to format disk
            totalBlocks = diskSize;
            format(defaultInodeBlocks);
            // SysLib.format(defaultInodeBlocks); ?
        }
    }

    // format(int ): clears out disk info
    // int files = number of iNodes to create
    public synchronized void format(int files){
        int totaleBlocks = 1000;
        int totalInodes = files;    // number of inodes per block (marked to match argument)
        int freeList = 2;           // TODO: this was the default value from slide
        byte[] superBlock = new byte[512];  // instantiate superblock to a 512 byte array
        // void int2bytes( int i, byte[] b, int offset )
        // converts the integer i into four bytes, and then 
        //      copied those four bytes into
        //      b[offset], b[offset+1], b[offset+2], b[offset+3]
        SysLib.int2bytes(totalBlocks, superBlock, 0);
        SysLib.int2bytes(totalInodes, superBlock, 4);
        SysLib.int2bytes(freeList, superBlock, 8);
        // block #0 as the superblock. For accessing this block, 
        //     you should call SysLib.rawread( 0, data ) where data is a 512-byte array
        // write superblock to disk
        SysLib.rawwrite(0, superBlock);
        // temp superblock type
        byte[] blockRock = new byte[512];
        // loop through from position of freeList as current index
		for (int i = freeList; i < totalBlocks; i++)
		{
            // zero all values in each disk block
			for (int j = 0; j < Disk.blockSize; j++)
			{
				blockRock[j] = 0;
			}
            SysLib.int2bytes(i, blockRock, 0);
			SysLib.rawwrite(i, blockRock);          // write to disk
		}
    }
    // TODO: check how writing to the last block handles- shouldn't be able to start writing at last block?

    // sync(): Write back totalBlocks, inodeBlocks, and freeList to disk

    // : Dequeue the top block from the free list
    public short getFreeBlock(){
        short ret = (short)freeList;
        byte[] iBlock = new byte[Disk.blockSize];
        SysLib.rawread(freeList, iBlock);
        freeList = SysLib.bytes2int(iBlock, 0);
        return ret;
    }

    // returnBlock(block?): Enqueue a given block to the end of the free list

}
