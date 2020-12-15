import java.util.*;
//import javafx.util.*;

public class Directory {
    private static int maxChars = 30; // max characters of each file name

    Pair pair = new Pair();
    HashMap<String, Short> filenameInumberMap = new HashMap<String, Short>();
    //change to set of type pair index = maxInumber-1



    // Directory entries
    private int fsize[];        // each element stores a different file size.
    private char fnames[][];    // each element stores a different file name.

    // directory constructor
    public Directory( int maxInumber ) {
        // maxInumber = max files
        fsize = new int[maxInumber];

        // all file size initialized to 0
        for ( int i = 0; i < maxInumber; i++ ){
            fsize[i] = 0;
        }

        //declare the size of the 2D array
        fnames = new char[maxInumber][maxChars];

        // entry(inode) 0 is "/"
        String root = "/";
        // fsize[0] is the size of "/".
        fsize[0] = root.length( );
        // fnames[0] includes "/"
        root.getChars( 0, fsize[0], fnames[0], 0 );
    }

    // assumes data[] received directory information from disk
    // initializes the Directory instance with this data[]
    public int bytes2directory( byte data[] ) {
        //Look at  SysLib.bytes2int(data, offset)

        //check to make sure data[] is valid
        if(data == null || data.length == 0){
            return 0;
        }
        //set the offset
        int offset = 0;

        //save the first int in the array as the max iNumber
        int maxInumber = SysLib.bytes2int(data, offset);
        //intialize the arrays
        fsize = new int[maxInumber];
        fnames = new char[maxInumber][maxChars];

        int curInumber = 0;
        //while current iNumber is less than nam iNumber
        while(curInumber < maxInumber){
            //load fsize
            fsize[curInumber] = SysLib.bytes2int(data, offset);
            //move offset by value of int
            offset += 4;

            //once fsize is captured in directory
            //capture file names in fnames array
            for(int i = 0; i < fsize.length; i++){
                fnames[curInumber][i] = (char) data[offset++];
            }
            //increment curInumber to progress through the array
                curInumber++;
        }
        return maxInumber;
    }

    public byte[] directory2bytes( ) {
        // converts and return Directory information into a plain byte array
        // this byte array will be written back to disk
        // note: only meaningful directory information should be converted
        // into bytes.

        int offset = 0;
        byte[] dirToByteArray = new byte[fsize.length + fnames.length + 1];

        for(int i = 0; i < fsize.length; i ++){
            SysLib.int2bytes(fsize[i], dirToByteArray, offset);
            offset += 4;
        }
        for(int d = 0; d < fnames.length; d++){
            for(int a = 0; a < fnames[d].length; a++){
                SysLib.int2bytes((int)fnames[d][a], dirToByteArray, offset);
                offset++;
            }
        }
        //SysLib.int2bytes(fsizes[i], fromDirectory, offset)

        return dirToByteArray;
    }

    public short ialloc( String filename ) {
        // filename is the one of a file to be created.
        // allocates a new inode number for this filename
        //0 - (superBlock.totalInodes - 1)

        //find out what the largest iNumber
        Map.Entry<String, Short> curMaxInumber = null;
        for (Map.Entry<String, Short> entry : filenameInumberMap.entrySet()){
            if (curMaxInumber == null || entry.getValue().compareTo(curMaxInumber.getValue()) > 0)
            {
                curMaxInumber = entry;
            }
        }

        //get the largest iNumber in map
        Short newInumber = curMaxInumber.getValue();
        newInumber++;                                           //increment by one
        filenameInumberMap.put(filename, newInumber);           //add to the hashmap
        return newInumber;                  //return iNumber
    }


    // deallocates this inumber (inode number)
    // the corresponding file will be deleted.
    public boolean ifree( short iNumber ) {
        if(iNumber == 0){               //check to see if in directory
            return false;
        }

        //find filename of iNumber
        for (int i = 0; i< filenameInumberMap.size(); i++) {
            if(filenameInumberMap.containsValue(iNumber) == true){
                //once found, remove from map
                filenameInumberMap.remove(filenameInumberMap.get(i));

                //set fsize[iNumber ] == 0
                fsize[iNumber] = 0;
                //do the same to fnames
                for(int j = 0; j < fnames.length; j++){
                    for(int k = 0; k < fnames[j].length; k++){
                        fnames[j][k] = 0;
                    }
                }
                return true;
            }
        }
        return false;
    }


    // returns the inumber corresponding to this filename
    public short namei( String filename ) {
        //search hashmap for file
        for(int i = 0; i < filenameInumberMap.size(); i++) {
            //if found, return iNumber paired with filename
            if(filenameInumberMap.containsKey(filename) == true){
                return filenameInumberMap.get(filename);
            }
            //if get to the end of the map and file not found
            if (i == filenameInumberMap.size() - 1){
                //return error
                return -1;
            }
        }
        return -1;
    }
}
