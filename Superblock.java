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
 * */
public class Superblock {

    public int totalBlocks;   //number of disk blocks
    public int totalInodes;   //number of inodes
    public int freeList;      //the block number of the free list's head

    //constructor
    public SuperBlock(int diskSize){

    }
}
