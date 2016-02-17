public class VotingComponent implements ComponentBase {

	public VotingComponent() {

	}

	public KeyValueList processMsg(KeyValueList kvList) {
		if(kvList.size()>0){
			System.out.println("====================");
			System.out.println(kvList);
			System.out.println("====================");
		} else {
			System.out.println("Invalid");
		}

		return kvList;
	}
}