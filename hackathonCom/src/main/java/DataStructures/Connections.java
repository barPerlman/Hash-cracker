package DataStructures;

import java.util.ArrayList;
import java.util.HashMap;

public class Connections {

    //this structure holds the addresses of connections as key=ip, value= list of ports
    private HashMap<String, ArrayList<Integer>> hostsAddresses;
    public Connections(){
        this.hostsAddresses = new HashMap<String, ArrayList<Integer>>();
    }
    public HashMap<String, ArrayList<Integer>> getHostsAddresses() {
        return hostsAddresses;
    }

    public void setHostsAddresses(HashMap<String, ArrayList<Integer>> hostsAddresses) {
        this.hostsAddresses = hostsAddresses;
    }

    /**
     * this add a new system end pooint details to the DS as a connection
     * @param ip
     * @param port
     */
    public void setNewServer(String ip, int port){
        ArrayList<Integer> portList = this.hostsAddresses.get(ip);
        if(portList!=null){     //the ip address received is an end system which is already connected
            this.hostsAddresses.get(ip).add(new Integer(port));  //update port list of current connection
        }else{  //there is no end system with the ip address in the data structure
            this.hostsAddresses.put(ip,new ArrayList<Integer>(port));
        }
    }
}
