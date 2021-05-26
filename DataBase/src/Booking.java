import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class Booking {
	JSONObject obj = JSONUtils.getJSONObjectFromFile("/timeTable.json");
	JSONArray jsonArray = obj.getJSONArray("Array");
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmm");
	
	public String NormalBooking(String UID, String Ddate, String Rdate, // Ddate�X�o�ɶ�, Rdate��{�ɶ�
			String SStation, String DStation, //S�l��, D�ׯ�
			int normalT, int concessionT, int studentT, //�@�벼, �u�ݲ�, �j�ǥͲ�
			int AorW, boolean BorS) // ���Dor�a��, �ӰȩμзǨ��[
	{
		//�B�z��V
		
		//�h�{��V(�O�h�{�N�n)
		int Direction;
		
		if (Integer.valueOf(SStation) < Integer.valueOf(DStation)) {
			Direction = 0; //�n�V
		}
		else{
			Direction = 1; //�_�V
		}
		
		//�B�z�ɶ�
		
		//�h�{
		Date Dedate  = null; //Date object
		String WoDD  = null; //week of day
		String Dtime = null; //time
		
		if(Ddate != null) {
			try {
				Dedate = sdf.parse(Ddate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			WoDD = getWeekofDay(Dedate);
			
			Dtime = Ddate.substring(11);
		}
		
		//�^�{
		Date Redate  = null; //Date object
		String WoDR  = null; //week of day
		String Rtime = null; //time
		
		if(Rdate != null) {
			try {
				Redate = sdf.parse(Rdate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			WoDR = getWeekofDay(Redate);
			
			Rtime = Rdate.substring(11);
		}
		

		//�N�ŦX�ɶ����C���s����������
		ArrayList<String> Davailable = new ArrayList<String>();
		ArrayList<String> Ravailable = new ArrayList<String>();
		
		/*    �T�{���Ǭ��G
		 * 1. �T�{��V Direction
		 * 2. �T�{�P�� DayofWeek
		 * 3. �T�{�O�_�ŦX���u(���L��F�l���P�ׯ�) Stations
		 * 4. �T�{�l���X�o�ɶ� Date
		 */
		
		for(int i = 0; i < jsonArray.length(); i++) {
			
			JSONObject train = jsonArray.getJSONObject(i);
			JSONObject timetable = train.getJSONObject("GeneralTimetable");
			
			//�h�{
			if ((timetable.getJSONObject("GeneralTrainInfo").getInt("Direction") == Direction)
				//�T�{��V
				&& (timetable.getJSONObject("ServiceDay").getInt(WoDD) != 1) 
				//�T�{�P��
				&& (trainroutehas(train, SStation, DStation)) 
				//�T�{���u
				&& (dparturetime(Dtime, SStation, timetable.getJSONArray("StopTimes")))) 
				//�T�{�X�o�ɶ�
			{
				Davailable.add(timetable.getJSONObject("GeneralTrainInfo").getString("TrainNo"));
			}
			
			//�^�{
			if ((timetable.getJSONObject("GeneralTrainInfo").getInt("Direction") != Direction)
				//�T�{��V
				&& (timetable.getJSONObject("ServiceDay").getInt(WoDR) != 1) 
				//�T�{�P��
				&& (trainroutehas(train, SStation, DStation)) 
				//�T�{���u
				&& (dparturetime(Rtime, DStation, timetable.getJSONArray("StopTimes")))) 
				//�T�{�X�o�ɶ�
			{
				Ravailable.add(timetable.getJSONObject("GeneralTrainInfo").getString("TrainNo"));
			}
		}
		
		
		
		for(int i = 0; i < jsonArray.length(); i++) {
			System.out.println(jsonArray.get(i));
		}
		
		
		if ((normalT+concessionT+studentT > 10) || ((Rdate != null)&&(normalT+concessionT+studentT > 5))) {
			return "���ѡA�]�q��w�w�L�h����(�C���̦h10�i�A�Ӧ^�����W�߭p��)";
		}
		
		if (Dedate.after(null) || Redate.after(null)) {
			return "���ѡA�]�|����w��";
		}
		
		/*�U����ӭn�Ѧҧڭ̫��B�z���
		 * �j�P�W�p�U
		 * 1. ��list�x�s�Z���s��
		 * 2. �ˬd�U�ر��� (�U�Ӳ���, AorW, �ɶ�)�A�N�ŦX���󪺯Z���s�Jlist
		 * 3. �z�Llist��X�U�Z����T
		 */
		
		/* �C�X�Ӯɬq�ŦX���󪺨���*/
		/* ���ѡA�]���Ӯɬq�����y��w���j*/
		
		return null;
	}
	
	/**
	 * @param time ��J�ɶ�
	 * @param SStation �_��
	 * @param StopTimes �ӦC��������
	 * @return �Y�ӦC���ӯ����X���ɶ� �b ��J�ɶ� �� �h�^��true �Ϥ��^��false
	 */
	
	private boolean dparturetime(String time, String SStation, JSONArray StopTimes) {
		for (int i=0 ; i < StopTimes.length(); i++) {
			if (StopTimes.getJSONObject(i).getString("StationID") == SStation){
				String DepartureTime = StopTimes.getJSONObject(i).getString("DepartureTime").replace(":", "");
				if (Integer.valueOf(DepartureTime) >= Integer.valueOf(time)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param train �ӦC����JSONobject
	 * @param SStation �l��
	 * @param DStation �ׯ�
	 * @return true �Y���u���T false �Ϥ�
	 */
	
	private boolean trainroutehas(JSONObject train, String SStation, String DStation) {
		boolean S = false;
		boolean D = false;
		
		for (int j = 0; j < train.getJSONArray("StopTimes").length(); j++) {
			String station = train.getJSONArray("StopTimes").getJSONObject(j).getString("StationID");
			if (station	== SStation) {
				S = true;
			}
			if (station	== DStation) {
				D = true;
			}
		}

		if (S && D) {
			return true;
		}
		else return false;
	}
	
	private String getWeekofDay(Date date) {
		
		String[] weekDays = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        
		return weekDays[w];
	}
}