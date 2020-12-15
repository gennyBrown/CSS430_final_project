//This class creates a pair object to store the filename and iNumber mapping
public class Pair {

    private String pairFile;        //filenamne
    private Short pairInumber;      //iNumber

    public void Pair(){}

    //constructor
    public Pair(String pairFile, Short pairINumber){
     this.pairFile = pairFile;
     this.pairInumber = pairInumber;
    }

    //set both
    public void add(String pairFile, Short pairInumber){
        this.pairFile = pairFile;
        this.pairInumber = pairInumber;
    }
    //setter
    public void setFirst(String pairFile){
        this.pairFile = pairFile;
    }

    //setter
    public void setSecond(Short pairInumber){
        this.pairInumber = pairInumber;
    }

    //getter
    public String getFirst(){
        return pairFile;
    }

    //getter
    public Short getSecond(){
        return pairInumber;
    }

}
