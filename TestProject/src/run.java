import java.io.IOException;

public class run {

	public static void main(String[] args) {
		Booking runner = new Booking();
		
		try {
			runner.Search("2021-07-20 1400", "2021-07-23 1200", "1047", "1060", 1, 0, 0, 0, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
