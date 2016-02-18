/*public class VotingComponent implements ComponentBase {

	public VotingComponent() {

	}

	public KeyValueList processMsg(KeyValueList kvList) {
		// TODO
		//int msgID = Integer.parseInt(kvList.getValue("MsgID"));
		KeyValueList kvResult = new KeyValueList();

		kvResult.addPair("MsgID", "1");
		kvResult.addPair("Description", "Authentication Result");

		kvResult.addPair("Authentication", "success");

		return kvResult;
	}
}*/

 
/*
A Simple Example--Authentication Component.
To Create a Component which works with the InterfaceServer,
the interface ComponentBase is required to be implemented.

interface ComponentBase is described in InterfaceServer.java.

*/

import java.io.*;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;

public class VotingComponent implements ComponentBase{

  // 
  Hashtable<String, AtomicInteger> tallyTable;
  ArrayList<String> numberList;

  private final int init=0;
  private final int success=1;
  private final int failure=2;


  private int state;

  public VotingComponent(){
    state=init;
  }

  /* just a trivial example */

  private void doAuthentication(String first,String last,String passwd){

    if (first.equals("xin")&&last.equals("li")&&passwd.equals("xl123"))
      state=success;
    else 
      state=failure;
  }

  /* function in interface ComponentBase */

  public KeyValueList processMsg(KeyValueList kvList){
      // ID of incoming message
    int msgID;
    try {
      msgID = Integer.parseInt(kvList.getValue("MsgID"));
    } catch (Exception e) {
      return null;
    }

    KeyValueList kvResult = new KeyValueList(); // Return this
    String passCode;

    if (msgID != 0) {
    switch(msgID) {
      case 21: // Voting Software Created
        kvResult.addPair("MsgID", "26");
        kvResult.addPair("AckMsgID", Integer.toString(msgID));
        kvResult.addPair("YesNo", "Yes");
        kvResult.addPair("Name", kvList.getValue("Name"));
        break;
      case 701: // Cast Vote
        String voterPhone = kvList.getValue("VoterPhoneNo");
        String candidate = kvList.getValue("CandidateID");
          if(numberList.contains(voterPhone) && tallyTable.containsKey(candidate)) {
            // Repeat vote - nothing happens
            kvResult.addPair("MsgID", "711");
            kvResult.addPair("Status", "2"); // Failure
          } else {
              // Initial Vote - vote entered into tally table
            numberList.add(voterPhone);
            AtomicInteger atom = tallyTable.get(candidate);
            atom.getAndIncrement(); // increase vote number by 1
            kvResult.addPair("MsgID", "711");
            kvResult.addPair("Status", "1"); // Success
          } 
        break;
      case 702: // Request Report
        passCode = kvList.getValue("Passcode");

        int max = 0;
        Set<String> keys = tallyTable.keySet();

        // First find the highest value
        for(String key : keys) {
          if(tallyTable.get(key).get() > max) max = tallyTable.get(key).get();
        }
        // String returned must be a string of N CandidateID,NumberVote separated by semicolons
        String report = "";
        boolean multipleWinners = false;
        for(String key : keys) {
          if(tallyTable.get(key).get() == max) {
            if(multipleWinners == false) {
              report = key;
              report = report + "," + tallyTable.get(key).toString();
              multipleWinners = true;
            }
            else {
              report = report + ";" + key + "," + tallyTable.get(key).toString();
            }
          }
        }



        kvResult.addPair("MsgID", "712");
        kvResult.addPair("RankedReport", report);

        break;
      case 703: // Initialize Tally Table
        passCode = kvList.getValue("Passcode");

        String candidateList = kvList.getValue("CandidateList");
        String[] eachCandidate = candidateList.split(";");
        tallyTable = new Hashtable();
        numberList = new ArrayList();

        for(int i = 0; i < eachCandidate.length; i++) tallyTable.put(eachCandidate[i], new AtomicInteger(0)); // <String, int>
        kvResult.addPair("MsgID", "26");
        kvResult.addPair("AckMsgID", Integer.toString(msgID));
        kvResult.addPair("YesNo", "Yes");
        kvResult.addPair("Name", "Tally Table");
        break;
    }
}

  return kvResult; 
  }

}